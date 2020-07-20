package io.sharpink.mapper.story;

import java.util.ArrayList;
import java.util.List;

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

	// **************************************************************
	// Chapter --> ChapterDto
	// **************************************************************

	/**
	 * Crée un {@code ChapterDto} à partir d'un {@code Chapter}.
	 *
	 * @param source : une entité {@code Chapter}.
	 * @return le bean {@code ChapterDto} correspondant.
	 */
	public ChapterResponse mapDto(Chapter source) {

		ChapterResponse target = new ChapterResponse();

		target.setId(source.getId());
		target.setPosition(source.getPosition());
		target.setTitle(source.getTitle());
		target.setContent(source.getContent());

		return target;

	}

	/**
	 * Crée une {@List<ChapterDto>} à partir d'une {@code List<Chapter>}.
	 *
	 * @param source : une {@code List<Chapter>}.
	 * @return la {@List<ChapterDto>} correspondante.
	 */
	public List<ChapterResponse> mapDtos(List<Chapter> source) {

		List<ChapterResponse> target = new ArrayList<>();

		for (Chapter chapter : source) {
			target.add(mapDto(chapter));
		}

		return target;

	}

	// **************************************************************
	// ChapterDto --> Chapter
	// **************************************************************

	/**
	 * Crée un {@code Chapter} à partir d'un {@code ChapterDto}.
	 *
	 * @param source : un DTO {@code ChapterDto}.
	 * @return l'entité {@code Chapter} correspondante.
	 */
	public Chapter map(ChapterResponse source) {

		Chapter target = new Chapter();

		target.setId(source.getId());
		target.setPosition(source.getPosition());
		target.setTitle(source.getTitle());
		target.setContent(source.getContent());

		return target;

	}

	/**
	 * Crée une {@List<Chapter>} à partir d'une {@code List<ChapterDto>}.
	 *
	 * @param source : une {@code List<ChapterDto>}.
	 * @return la {@List<Chapter>} correspondante.
	 */
	public List<Chapter> map(List<ChapterResponse> source) {

		List<Chapter> target = new ArrayList<>();

		for (ChapterResponse chapterResponse : source) {
			target.add(map(chapterResponse));
		}

		return target;

	}

}
