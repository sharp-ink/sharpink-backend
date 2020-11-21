package io.sharpink.mapper.story;

import io.sharpink.persistence.entity.story.ChaptersLoadingStrategy;
import io.sharpink.persistence.entity.story.Story;
import io.sharpink.persistence.entity.story.StoryStatus;
import io.sharpink.persistence.entity.story.StoryType;
import io.sharpink.rest.dto.request.story.StoryRequest;
import io.sharpink.rest.dto.response.story.StoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// @formatter:off
@Component
public class StoryMapper {

  private final ChapterMapper chapterMapper;

  @Autowired
  public StoryMapper(ChapterMapper chapterMapper) {
    this.chapterMapper = chapterMapper;
  }

  public StoryResponse toStoryResponse(Story source, ChaptersLoadingStrategy chaptersLoadingStrategy) {
    StoryResponse target = StoryResponse.builder()
      .id(source.getId())
      .title(source.getTitle())
      .type(source.getType().value())
      .status(source.getStatus().value())
      .summary(source.getSummary())
      .thumbnail(source.getThumbnail())
      .published(source.isPublished())
      .chaptersNumber(source.getChaptersNumber())
      .originalStory(source.isOriginalStory())
      .authorId(source.getAuthor().getId())
      .creationDate(source.getCreationDate())
      .lastModificationDate(source.getLastModificationDate())
      .finalReleaseDate(source.getFinalReleaseDate())
      .threadId(source.getThread() != null ? source.getThread().getId() : null)
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
    return Story.builder()
      .title(source.getTitle())
      .type(source.getType() != null ? StoryType.valueOf(source.getType()) : StoryType.UNDETERMINED)
      .originalStory(source.isOriginalStory())
      .status(source.getStatus() != null ? StoryStatus.valueOf(source.getStatus()) : null)
      .summary(source.getSummary())
      .published(source.isPublished())
      .build();
	}
}
