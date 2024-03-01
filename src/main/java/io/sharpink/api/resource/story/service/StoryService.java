package io.sharpink.api.resource.story.service;

import io.sharpink.api.resource.story.dto.*;
import io.sharpink.api.resource.story.dto.search.StorySearch;
import io.sharpink.api.resource.story.enums.AuthorLoadingStrategy;
import io.sharpink.api.resource.story.enums.ChaptersLoadingStrategy;
import io.sharpink.api.resource.story.persistence.Chapter;
import io.sharpink.api.resource.story.persistence.ChapterDao;
import io.sharpink.api.resource.story.persistence.Story;
import io.sharpink.api.resource.story.persistence.StoryDao;
import io.sharpink.api.shared.enums.SortType;
import io.sharpink.config.SharpinkConfiguration;
import io.sharpink.api.resource.user.service.UserMapper;
import io.sharpink.api.resource.user.persistence.UserDao;
import io.sharpink.api.shared.enums.StoriesLoadingStrategy;
import io.sharpink.api.resource.user.persistence.user.User;
import io.sharpink.api.shared.exception.InternalError500Exception;
import io.sharpink.api.shared.exception.NotFound404Exception;
import io.sharpink.api.shared.exception.UnprocessableEntity422Exception;
import io.sharpink.api.shared.service.picture.PictureManagementService;
import io.sharpink.util.ComparatorUtil;
import io.sharpink.util.PictureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static io.sharpink.api.resource.story.enums.AuthorLoadingStrategy.ENABLED;
import static io.sharpink.api.resource.story.persistence.StoryDao.*;
import static io.sharpink.api.shared.enums.SortType.isDefined;
import static io.sharpink.api.shared.exception.MissingEntity.CHAPTER;
import static io.sharpink.api.shared.exception.UnprocessableEntity422ReasonEnum.TITLE_ALREADY_USED;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Service
public class StoryService {

    private final UserDao userDao;
    private final StoryDao storyDao;
    private final ChapterDao chapterDao;
    private final StoryMapper storyMapper;
    private final UserMapper userMapper;
    private final ChapterMapper chapterMapper;
    private final PictureManagementService pictureManagementService;
    private final SharpinkConfiguration sharpinkConfiguration;

    @Autowired
    public StoryService(UserDao userDao, StoryDao storyDao, ChapterDao chapterDao, StoryMapper storyMapper, UserMapper userMapper, ChapterMapper chapterMapper,
                        PictureManagementService pictureManagementService, SharpinkConfiguration sharpinkConfiguration) {
        this.userDao = userDao;
        this.storyDao = storyDao;
        this.chapterDao = chapterDao;
        this.storyMapper = storyMapper;
        this.userMapper = userMapper;
        this.chapterMapper = chapterMapper;
        this.pictureManagementService = pictureManagementService;
        this.sharpinkConfiguration = sharpinkConfiguration;
    }

    /**
     * Retrieve all public stories, having at least one chapter.
     *
     * @return a {@code List<StoryResponse>}, empty if there is no public story with at least one chapter.
     */
    public List<StoryResponse> getAllPublicStories(AuthorLoadingStrategy authorLoadingStrategy) {

        List<Story> stories = storyDao.findAll(isPublic()).stream()
            .filter(Story::hasChapters)
            .collect(toList());

        List<StoryResponse> storyResponses = storyMapper.toStoryResponseList(stories, ChaptersLoadingStrategy.ONLY_FIRST);

        if (authorLoadingStrategy == ENABLED) {
            setAuthorInStoryResponses(storyResponses, stories);
        }

        return storyResponses;
    }

    /**
     * Get stories for a specific {@code User}
     *
     * @return A {@code List<StoryDto>} containing all stories of the given {@code User}, empty list if this user has no stories.
     */
    public List<StoryResponse> getStories(Long userId) {
        List<Story> stories = storyDao.findByAuthorId(userId);

        return storyMapper.toStoryResponseList(stories, ChaptersLoadingStrategy.NONE); // chapters are not necessary
    }

    /**
     * Récupère une histoire via son id.
     *
     * @return La {@code Story} correspondant à l'id passé en paramètre si elle
     * existe, null sinon.
     */
    public Optional<StoryResponse> getStory(Long id) {

        Optional<Story> storyOptional = storyDao.findById(id);

        // all chapters are requested
        return storyOptional.map(story -> storyMapper.toStoryResponse(story, ChaptersLoadingStrategy.ALL));

    }

    /**
     * Crée et sauvegarde une histoire en base.
     *
     * @param storyRequest Un objet contenant les informations de l'histoire à créer et
     *                     sauvegarder.
     * @return L'id de l'entité persistée, qui servira à identifier l'histoire de
     * manière unique.
     */
    public Long createStory(StoryRequest storyRequest) {
        if (storyWithSameTitleAlreadyExists(storyRequest.getTitle())) {
            throw new UnprocessableEntity422Exception(TITLE_ALREADY_USED);
        } else {
            Story story = storyMapper.toStory(storyRequest);

            story.setAuthor(userDao.findById(storyRequest.getAuthorId()).orElseThrow());
            story.setChaptersNumber(0);

            LocalDateTime now = LocalDateTime.now();
            story.setCreationDate(now);
            story.setLastModificationDate(now);

            story = storyDao.save(story);
            return story.getId(); // returns id of newly created entity
        }
    }

    /**
     * Search stories by criteria, eventually applying filters and sorting.
     *
     * @param storySearch The criteria stories should match, including filters and sorting
     * @return a list of stories matching the given criteria, with appropriate filters / sorting
     */
    public List<StoryResponse> searchStories(StorySearch storySearch, AuthorLoadingStrategy authorLoadingStrategy) {
        String title = storySearch.getCriteria().getTitle();
        String authorName = storySearch.getCriteria().getAuthorName();

        List<Story> stories = storyDao.findAll(hasTitleLike(title)
            .and(hasAuthorLike(authorName))
            .and(isPublic()));

        if (storySearch.getSort() != null) {
            applySorting(stories, storySearch.getSort());
        }

        List<StoryResponse> storyResponses = storyMapper.toStoryResponseList(stories, ChaptersLoadingStrategy.ONLY_FIRST);

        if (authorLoadingStrategy == ENABLED) {
            setAuthorInStoryResponses(storyResponses, stories);
        }

        return storyResponses;
    }

    /**
     * Update a story
     *
     * @param id The id of the story to be updated
     * @param storyPatchRequest New information (partial) to add to the story
     */
    public StoryResponse updateStory(Long id, StoryPatchRequest storyPatchRequest) {
        Optional<Story> storyOptional = storyDao.findById(id);
        if (storyOptional.isPresent()) {
            Story story = storyOptional.get();

            if (isNotEmpty(storyPatchRequest.getTitle())) {
                story.setTitle(storyPatchRequest.getTitle());
            }

            story.setOriginalStory(storyPatchRequest.isOriginalStory()); // will not very likely change but it is allowed

            if (storyPatchRequest.getStatus() != null) {
                story.setStatus(storyPatchRequest.getStatus());
            }

            if (storyPatchRequest.getType() != null) {
                story.setType(storyPatchRequest.getType());
            }

            if (storyPatchRequest.getSummary() != null) {
                story.setSummary(storyPatchRequest.getSummary());
            }

            if (isNotEmpty(storyPatchRequest.getThumbnail())) {
                String formImageData = storyPatchRequest.getThumbnail();
                String imageBase64Content = PictureUtil.extractBase64Content(formImageData);
                String extension = PictureUtil.extractExtension(formImageData);
                String storyThumbnailWebUrl = sharpinkConfiguration.getUsersProfilePictureWebUrl() + '/' + story.getAuthor()
                    .getNickname() + "/stories/" + story.getId() + "/thumbnail." + extension;
                story.setThumbnail(storyThumbnailWebUrl);
                try {
                    String storyThumbnailFSPath = sharpinkConfiguration.getUsersProfilePictureFileSystemPath() + '/' + story.getAuthor()
                        .getNickname() + "/stories/" + story.getId() + "/thumbnail." + extension;
                    pictureManagementService.storePictureOnFileSystem(imageBase64Content, storyThumbnailFSPath);
                } catch (IOException e) {
                    e.printStackTrace(); // TODO: use a logger instead
                    throw new InternalError500Exception(e);
                }
            }

            if (storyPatchRequest.getPublished() != null) {
                story.setPublished(storyPatchRequest.getPublished());
            }

            story.setLastModificationDate(LocalDateTime.now());

            Story updatedStory = storyDao.save(story);
            return storyMapper.toStoryResponse(updatedStory, ChaptersLoadingStrategy.NONE);
        } else {
            throw new NotFound404Exception();
        }
    }

    public void removeStory(Long id) {
        storyDao.deleteById(id);
    }

    /**
     * Creates and persists a new chapter for a given story
     *
     * @param chapterRequest an object with the informations of the chapter to be created
     * @return the id of the newly created chapter (unique)
     */
    public Long addChapter(Long storyId, ChapterRequest chapterRequest) {
        Optional<Story> storyOptional = storyDao.findById(storyId);
        if (storyOptional.isPresent()) {
            Story story = storyOptional.get();

            Chapter newChapter = chapterMapper.toChapter(chapterRequest);
            newChapter.setPosition(story.getChaptersNumber() + 1);
            newChapter.setStory(story);
            chapterDao.save(newChapter);

            story.getChapters().add(newChapter);
            story.setChaptersNumber(story.getChaptersNumber() + 1);
            storyDao.save(story);

            return newChapter.getId();
        } else {
            throw new NotFound404Exception();
        }
    }

    /**
     * Updates an existing chapter
     */
    public ChapterResponse updateChapter(Long storyId, int chapterPosition, ChapterRequest chapterRequest) {
        Optional<Story> storyOptional = storyDao.findById(storyId);
        if (storyOptional.isPresent()) {
            Story story = storyOptional.get();

            try {
                Chapter chapter = story.getChapters().get(chapterPosition - 1);
                chapter.setTitle(chapterRequest.getTitle());
                chapter.setContent(chapterRequest.getContent());

                Chapter updatedChapter = chapterDao.save(chapter);
                return chapterMapper.toChapterResponse(updatedChapter);
            } catch (IndexOutOfBoundsException e) {
                throw new NotFound404Exception();
            }
        } else {
            throw new NotFound404Exception();
        }
    }

    /**
     * Removes a chapter from a given story
     */
    public void removeChapter(Long storyId, Long chapterPosition) {
        Story story = storyDao.findById(storyId)
            .orElseThrow(() -> new NotFound404Exception(CHAPTER));

        List<Chapter> chapters = story.getChapters();
        if (chapters.size() >= chapterPosition) {
            chapters.remove(chapterPosition.intValue() - 1);
            story.setChaptersNumber(story.getChapters().size());
            shiftPositionsDown(chapters, chapterPosition);
            storyDao.save(story);
        } else {
            throw new NotFound404Exception(CHAPTER);
        }
    }

    /**
     * Put story's author in the {@code StoryResponse}s (by default only author's id is filled)
     */
    private void setAuthorInStoryResponses(List<StoryResponse> storyResponses, List<Story> stories) {
        storyResponses.forEach(storyResponse -> {
            User author = stories.stream()
                .filter(story -> story.getId().equals(storyResponse.getId()))
                .findFirst().orElseThrow()
                .getAuthor();
            storyResponse.setAuthor(userMapper.toUserResponse(author, StoriesLoadingStrategy.DISABLED));
        });
    }

    private boolean storyWithSameTitleAlreadyExists(String title) {
        Optional<Story> storyOptional = storyDao.findOne(hasTitle(title));
        return storyOptional.isPresent();
    }

    protected void applySorting(List<Story> stories, StorySearch.Sort sort) {
        Comparator<Story> comparator = ComparatorUtil.noOrder();

        if (isDefined(sort.getAuthorName())) {
            comparator = comparator.thenComparing(s -> s.getAuthor().getNickname());
            if (sort.getAuthorName().equals(SortType.DESC)) {
                comparator = comparator.reversed();
            }
        }

        if (isDefined(sort.getTitle())) {
            comparator = comparator.thenComparing(Story::getTitle);
            if (sort.getTitle().equals(SortType.DESC)) {
                comparator = comparator.reversed();
            }
        }

        stories.sort(comparator);
    }

    private void shiftPositionsDown(List<Chapter> chapters, Long chapterPosition) {
        chapters.stream()
            .filter(c -> c.getPosition() >= chapterPosition)
            .forEach(c -> c.setPosition(c.getPosition() - 1));
    }
}
