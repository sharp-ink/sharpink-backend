package io.sharpink.mapper.story;

import java.util.ArrayList;
import java.util.List;

import io.sharpink.rest.dto.request.story.ChapterRequest;
import org.springframework.stereotype.Component;

import io.sharpink.persistence.entity.story.Chapter;
import io.sharpink.rest.dto.response.story.ChapterResponse;

/**
 * Classe permettant de transformer :
 * <ul>
 * <li>des {@code Chapter} en {@code ChapterDto}</li>
 * <li>des {@code ChapterDto} en {@code Chapter}</li>
 * </ul>
 *
 * @author scaunois
 *
 */
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
		List<ChapterResponse> target = new ArrayList<>();

		for (Chapter chapter : source) {
			target.add(toChapterResponse(chapter));
		}

		return target;
	}

	public Chapter toChapter(ChapterRequest source) {
		Chapter target = new Chapter();

		target.setTitle(source.getTitle());
		target.setContent(source.getContent());

		return target;
	}

	public List<Chapter> toChapterList(List<ChapterRequest> source) {
		List<Chapter> target = new ArrayList<>();

		for (ChapterRequest chapterRequest : source) {
			target.add(toChapter(chapterRequest));
		}

		return target;
	}

}
