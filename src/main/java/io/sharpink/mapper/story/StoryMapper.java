package io.sharpink.mapper.story;

import java.util.ArrayList;
import java.util.List;

import io.scaunois.common.util.date.DateUtil;
import io.sharpink.persistence.entity.story.*;
import io.sharpink.rest.dto.request.story.StoryRequest;
import io.sharpink.rest.dto.response.story.StoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.sharpink.mapper.member.MemberMapper;
import io.sharpink.persistence.dao.MemberDao;

/**
 * Classe permettant de transformer :
 * <ul>
 * <li>des {@code Story} en {@code StoryDto}</li>
 * <li>des {@code StoryDto} en {@code Story}</li>
 * </ul>
 *
 * @author scaunois
 *
 */
// @formatter:off
@Component
public class StoryMapper {

	private final MemberMapper memberMapper;
	private final ChapterMapper chapterMapper;
	private final MemberDao memberDao;

	@Autowired
  public StoryMapper(MemberMapper memberMapper, ChapterMapper chapterMapper, MemberDao memberDao) {
    this.memberMapper = memberMapper;
    this.chapterMapper = chapterMapper;
    this.memberDao = memberDao;
  }

  public StoryResponse toStoryResponse(Story source, ChaptersLoadingStrategy chaptersLoadingStrategy) {
		StoryResponse target = StoryResponse.builder()
			.id(source.getId())
			.title(source.getTitle())
			.type(source.getType().getValue())
			.summary(source.getSummary())
			.status(source.getStatus().getValue())
			.published(source.isPublished())
			.chaptersNumber(source.getChaptersNumber())
			.originalStory(source.isOriginalStory())
			.authorId(source.getAuthor().getId())
			.author(memberMapper.map(source.getAuthor(), StoriesLoadingStrategy.DISABLED)) // TODO : should we keep that ?
			.creationDate(DateUtil.toLocalDateTime(source.getCreationDate()))
			.lastModificationDate(DateUtil.toLocalDateTime(source.getLastModificationDate()))
			.finalReleaseDate(DateUtil.toLocalDateTime(source.getFinalReleaseDate()))
			.build();

		if (chaptersLoadingStrategy == ChaptersLoadingStrategy.ENABLED) {
			target.setChapters(chapterMapper.mapDtos(source.getChapters()));
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
			.type(source.getType() != null ? StoryType.valueOf(source.getType()) : StoryType.UNKNOWN)
			.originalStory(source.isOriginalStory())
			.status(source.getStatus() != null ? StoryStatus.valueOf(source.getStatus()) : null)
			.summary(source.getSummary())
			.published(source.isPublished())
			.chaptersNumber(source.getChaptersNumber() != null ? source.getChaptersNumber() : 0)
      .build();

		target.setAuthor(memberDao.findById(source.getAuthorId()).get());

		return target;
	}
}
