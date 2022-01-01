package io.sharpink.api.resource.story.service;

import io.sharpink.api.resource.story.persistence.Chapter;
import io.sharpink.api.resource.story.dto.ChapterRequest;
import io.sharpink.api.resource.story.dto.ChapterResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChapterMapper {

  public ChapterResponse toChapterResponse(Chapter source) {
    ChapterResponse target = new ChapterResponse();

    target.setId(source.getId());
    target.setPosition(source.getPosition());
    target.setTitle(source.getTitle());
		target.setContent(source.getContent());

		return target;
	}

	public List<ChapterResponse> toChapterResponseList(List<Chapter> source) {
    return source.stream().map(this::toChapterResponse).collect(Collectors.toList());
  }

	public Chapter toChapter(ChapterRequest source) {
		Chapter target = new Chapter();

		target.setTitle(source.getTitle());
		target.setContent(source.getContent());

		return target;
	}

	public List<Chapter> toChapterList(List<ChapterRequest> source) {
    return source.stream().map(this::toChapter).collect(Collectors.toList());
  }

}
