package io.sharpink.service;

import io.scaunois.common.util.date.DateUtil;
import io.sharpink.mapper.story.StoryMapper;
import io.sharpink.persistence.dao.StoryDao;
import io.sharpink.persistence.entity.story.Story;
import io.sharpink.rest.exception.InternalError500Exception;
import io.sharpink.util.picture.PictureUtil;
import io.sharpink.rest.dto.story.StoryDto;
import io.sharpink.rest.dto.story.StoryPatchDto;
import io.sharpink.rest.exception.NotFound404Exception;
import io.sharpink.rest.exception.UnprocessableEntity422Exception;
import io.sharpink.service.picture.PictureManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static io.sharpink.constant.Constants.USERS_PROFILE_PICTURES_PATH;
import static io.sharpink.constant.Constants.USERS_PROFILE_PICTURES_WEB_URL;
import static io.sharpink.rest.exception.UnprocessableEntity422ReasonEnum.TITLE_ALREADY_USED;
import static java.util.stream.Collectors.toList;

@Service
public class StoryService {

  private StoryDao storyDao;

  private StoryMapper storyMapper;

  private PictureManagementService pictureManagementService;

  @Autowired
  public StoryService(StoryDao storyDao, StoryMapper storyMapper, PictureManagementService pictureManagementService) {
    this.storyDao = storyDao;
    this.storyMapper = storyMapper;
    this.pictureManagementService = pictureManagementService;
  }

  /**
   * Récupère toutes les histoires avec au moins un chapitre présentes en base.
   *
   * @return Une {@code List<StoryDto>} représentant la liste des histoires, vide
   * s'il n'y aucune histoire.
   */
  public List<StoryDto> getAllStories(Boolean published) {

    List<Story> stories = ((List<Story>) storyDao.findAll())
      .stream()
      .filter(story -> story.getChaptersNumber() != 0) // keep stories having at least 1 chapter
      .filter(story -> story.isPublished() == published.booleanValue() || published == null) // keep only if given published status (or no published status specified)
      .collect(toList());

    // on n'a pas besoin de charger les chapitres
    boolean shouldLoadChapters = false;

    return storyMapper.mapDtos(stories, shouldLoadChapters);

  }

  /**
   * Récupère une histoire via son id.
   *
   * @param id L'id de l'histoire à récupérer.
   * @return La {@code Story} correspondant à l'id passé en paramètre si elle
   * existe, null sinon.
   */
  public Optional<StoryDto> getStory(Long id) {

    Optional<Story> storyOptional = storyDao.findById(id);

    if (storyOptional.isPresent()) {
      // on souhaite charger les chapitres
      boolean shouldLoadChapters = true;
      return Optional.of(storyMapper.mapDto(storyOptional.get(), shouldLoadChapters));
    } else {
      return Optional.empty();
    }

  }

  /**
   * Crée et sauvegarde une histoire en base.
   *
   * @param storyDto Un objet contenant les informations de l'histoire à créer et
   *                 sauvegarder.
   * @return L'id de l'entité persistée, qui servira à identifier l'histoire de
   * manière unique.
   */
  public Long createStory(StoryDto storyDto) {
    if (storyWithSameTitleAlreadyExists(storyDto.getTitle())) {
      throw new UnprocessableEntity422Exception(TITLE_ALREADY_USED);
    } else {
      Story story = storyMapper.map(storyDto);
      story.setCreationDate(DateUtil.toDate(LocalDateTime.now()));
      story.setLastModificationDate(story.getCreationDate());
      story = storyDao.save(story);
      // renvoie l'id généré lors de la persistance de l'entité
      return story.getId();
    }
  }

  /**
   * Met à jour une histoire.
   *
   * @param id L'id de l'histoire à mettre à jour.
   * @param storyPatchDto Les nouvelles informations (partielles) à ajouter à l'histoire.
   */
  public StoryDto updateStory(Long id, StoryPatchDto storyPatchDto) {
    Optional<Story> storyOptional = storyDao.findById(id);
    if (storyOptional.isPresent()) {
      Story story = storyOptional.get();

      if (storyPatchDto.getType() != null) {
        story.setType(storyPatchDto.getType());
      }

      if (storyPatchDto.getSummary() != null) {
        story.setSummary(storyPatchDto.getSummary());
      }

      if (storyPatchDto.getThumbnail() != null) {
        String formImageData = storyPatchDto.getThumbnail();
        String extension = PictureUtil.extractExtension(formImageData);
        String storyThumbnailWebUrl = USERS_PROFILE_PICTURES_WEB_URL + '/' + story.getAuthor().getNickname() + "/stories/" + story.getId() + "/thumbnail." + extension;
        story.setThumbnail(storyThumbnailWebUrl);
        try {
          String storyThumbnailFSPath = USERS_PROFILE_PICTURES_PATH + '/' + story.getAuthor().getNickname() + "/stories/" + story.getId() + "/thumbnail." + extension;
          pictureManagementService.storePictureOnFileSystem(formImageData, storyThumbnailFSPath);
        } catch (IOException e) {
          e.printStackTrace(); // TODO: use a logger instead
          throw new InternalError500Exception(e);
        }
      }

      Story updatedStory = storyDao.save(story);
      return storyMapper.mapDto(updatedStory, false);
    } else {
      throw new NotFound404Exception();
    }
  }

  private boolean storyWithSameTitleAlreadyExists(String title) {
    Optional<Story> storyOptional = storyDao.findByTitle(title);
    return storyOptional.isPresent();
  }
}
