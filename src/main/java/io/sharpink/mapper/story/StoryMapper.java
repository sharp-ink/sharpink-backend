package io.sharpink.mapper.story;

import java.util.ArrayList;
import java.util.List;

import io.sharpink.persistence.entity.story.Story.StoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.scaunois.date.DateUtil;
import io.sharpink.mapper.member.MemberMapper;
import io.sharpink.persistence.dao.MemberDao;
import io.sharpink.persistence.entity.story.EnumStoryStatus;
import io.sharpink.persistence.entity.story.EnumStoryType;
import io.sharpink.persistence.entity.story.Story;
import io.sharpink.rest.dto.story.StoryDto;

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

	@Autowired
	private MemberMapper memberMapper;

	@Autowired
	private ChapterMapper chapterMapper;

	@Autowired
	private MemberDao memberDao;

	// **************************************************************
	// Story --> StoryDto
	// **************************************************************

	/**
	 * Crée un {@code StoryDto} à partir d'une {@code Story}.
	 *
	 * @param source : une entité {@code Story}.
	 * @return le bean {@code StoryDto} correspondant.
	 */
	public StoryDto mapDto(Story source, boolean shouldLoadChapters) {

		StoryDto target = StoryDto.builder()
			.id(source.getId())
			.title(source.getTitle())
			.type(source.getType().getValue())
			.summary(source.getSummary())
			.status(source.getStatus().getValue())
			.published(source.isPublished())
			.chaptersNumber(source.getChaptersNumber())
			.originalStory(source.isOriginalStory())
			.authorId(source.getAuthor().getId())
			.author(memberMapper.map(source.getAuthor(), false))
			.creationDate(DateUtil.toLocalDateTime(source.getCreationDate()))
			.lastModificationDate(DateUtil.toLocalDateTime(source.getLastModificationDate()))
			.finalReleaseDate(DateUtil.toLocalDateTime(source.getFinalReleaseDate()))
			.build();

		// on ne charge les chapitres que si demandé
		if (shouldLoadChapters) {
			target.setChapters(chapterMapper.mapDtos(source.getChapters()));
		}

		return target;

	}

	/**
	 * Crée une {@List<StoryDto>} à partir d'une {@code List<Story>}.
	 *
	 * @param source : une {@code List<Story>}.
	 * @return la {@List<StoryDto>} correspondante.
	 */
	public List<StoryDto> mapDtos(List<Story> source, boolean shouldLoadChapters) {

		List<StoryDto> target = new ArrayList<>();

		for (Story story : source) {
			target.add(mapDto(story, shouldLoadChapters));
		}

		return target;

	}

	// **************************************************************
	// StoryDto --> Story
	// **************************************************************

	/**
	 * Crée une {@code Story} à partir d'un {@code StoryDto}.
	 * @param source : un DTO {@code StoryDto}.
	 * @return l'entité {@code Story} correspondante.
	 */
	public Story map(StoryDto source) {

    Story target = Story.builder()
			.title(source.getTitle())
			.type(source.getType() != null ? EnumStoryType.valueOf(source.getType()) : EnumStoryType.UNKNOWN)
			.originalStory(source.isOriginalStory())
			.status(source.getStatus() != null ? EnumStoryStatus.valueOf(source.getStatus()) : null)
			.summary(source.getSummary())
			.published(source.isPublished())
			.chaptersNumber(source.getChaptersNumber() != null ? source.getChaptersNumber() : 0)
			.chapters(source.getChapters() == null ? null : chapterMapper.map(source.getChapters()))
      .build();

		// source.author n'est pas renseigné donc on récupère l'auteur grâce au dao, via son id
		// on n'a pas besoin d'appeler isPresent() sur l'Optional renvoyé par le dao, car l'id passé fait forcément référence à un auteur existant.
		target.setAuthor(memberDao.findById(source.getAuthorId()).get());

		return target;

	}

	/**
	 * Crée une {@List<Story>} à partir d'une {@code List<StoryDto>}.
	 *
	 * @param source : une {@code List<StoryDto>}.
	 * @return la {@List<Story>} correspondante.
	 */
	public List<Story> map(List<StoryDto> source) {

		List<Story> target = new ArrayList<>();

		for (StoryDto storyDto : source) {
			target.add(map(storyDto));
		}

		return target;

	}

}
