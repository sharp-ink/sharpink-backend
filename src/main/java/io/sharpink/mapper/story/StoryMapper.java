package io.sharpink.mapper.story;

import java.util.ArrayList;
import java.util.List;

import io.scaunois.common.util.date.DateUtil;
import io.sharpink.mapper.user.UserMapper;
import io.sharpink.persistence.entity.story.*;
import io.sharpink.rest.dto.request.story.StoryRequest;
import io.sharpink.rest.dto.response.story.StoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.sharpink.persistence.dao.UserDao;

// @formatter:off
@Component
public class StoryMapper {

	private final UserMapper userMapper;
	private final ChapterMapper chapterMapper;
	private final UserDao userDao;

	@Autowired
  public StoryMapper(UserMapper userMapper, ChapterMapper chapterMapper, UserDao userDao) {
    this.userMapper = userMapper;
    this.chapterMapper = chapterMapper;
    this.userDao = userDao;
  }

  public StoryResponse toStoryResponse(Story source, ChaptersLoadingStrategy chaptersLoadingStrategy) {
		StoryResponse target = StoryResponse.builder()
			.id(source.getId())
			.title(source.getTitle())
			.type(source.getType().getValue())
			.status(source.getStatus().getValue())
			.summary(source.getSummary())
      .thumbnail(source.getThumbnail())
			.published(source.isPublished())
			.chaptersNumber(source.getChaptersNumber())
			.originalStory(source.isOriginalStory())
			.authorId(source.getAuthor().getId())
			.author(userMapper.map(source.getAuthor(), StoriesLoadingStrategy.DISABLED)) // TODO : should we keep that ?
			.creationDate(DateUtil.toLocalDateTime(source.getCreationDate()))
			.lastModificationDate(DateUtil.toLocalDateTime(source.getLastModificationDate()))
			.finalReleaseDate(DateUtil.toLocalDateTime(source.getFinalReleaseDate()))
			.build();

		if (chaptersLoadingStrategy == ChaptersLoadingStrategy.ALL) {
			target.setChapters(chapterMapper.toChapterResponseList(source.getChapters()));
		} else if (chaptersLoadingStrategy == ChaptersLoadingStrategy.ONLY_FIRST) {
		  target.setChapters(chapterMapper.toChapterResponseList(source.getChapters().subList(0, 1)));
    }

		return target;
	}

	public List<StoryResponse> toStoryResponseList(List<Story> source, ChaptersLoadingStrategy chaptersLoadingStrategy) {
		List<StoryResponse> target = new ArrayList<>();

		for (Story story : source) {
			target.add(toStoryResponse(story, chaptersLoadingStrategy));
		}

		return target;
	}

	public Story toStory(StoryRequest source) {
    Story target = Story.builder()
			.title(source.getTitle())
			.type(source.getType() != null ? StoryType.valueOf(source.getType()) : StoryType.UNDETERMINED)
			.originalStory(source.isOriginalStory())
			.status(source.getStatus() != null ? StoryStatus.valueOf(source.getStatus()) : null)
			.summary(source.getSummary())
			.published(source.isPublished())
			.chaptersNumber(source.getChaptersNumber() != null ? source.getChaptersNumber() : 0)
      .build();

		target.setAuthor(userDao.findById(source.getAuthorId()).get());

		return target;
	}
}
