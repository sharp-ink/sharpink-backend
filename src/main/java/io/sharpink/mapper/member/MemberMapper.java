package io.sharpink.mapper.member;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.sharpink.mapper.story.StoryMapper;
import io.sharpink.persistence.entity.member.Member;
import io.sharpink.persistence.entity.member.MemberDetails;
import io.sharpink.rest.dto.member.MemberDetailsDto;
import io.sharpink.rest.dto.member.MemberDto;

@Component
public class MemberMapper {

	@Autowired
	private StoryMapper storyMapper;

	/**
	 * Construit un objet {@code MemberDto} à partir d'un objet {@code Member}.
	 * 
	 * @param source            L'entité {@code Member} à partir de laquelle on
	 *                          construit notre objet.
	 * @param shouldLoadStories Indique s'il faut ou non charger la liste des
	 *                          histoires, qui est pour rappel chargé par Hibernate
	 *                          avec
	 *                          l'annotation @LazyCollection(LazyCollectionOption.TRUE),
	 *                          donc seulement quand on y accède réellement. <br/>
	 *                          true : charge les histoires, false : ne charge pas
	 *                          les histoires (on se contente du nombre total
	 *                          d'histoires qui, lui, est toujours chargé)
	 * @return Un {@code Member}
	 */
	public MemberDto map(Member source, boolean shouldLoadStories) {

		MemberDto target = MemberDto.builder()
			.id(source.getId())
			.nickname(source.getNickname())
			.email(source.getEmail())
			.storiesCount(source.getStoriesCount())
			.memberDetails(source.getMemberDetails().isPresent() ? map(source.getMemberDetails().get()) : null)
			.build();

		// on ne charge les histoires que si demandé
		if (shouldLoadStories) {
			// meme si on veut les histoires, on ne veut pas charger les chapitres
			boolean shouldLoadChapters = false;
			target.setStories(storyMapper.mapDtos(source.getStories(), shouldLoadChapters));
		}

		return target;

	}

	/**
	 * Construit une {@code List<MemberDto>} à partir d'une {@code List<Member>}.
	 * 
	 * @param source            La liste des {@code Member} à partir duquel on
	 *                          construit notre liste de {@code MemberDto}.
	 * @param shouldLoadStories Indique s'il faut ou non charger la liste des
	 *                          histoires pour chaque membre, qui sont pour rappel
	 *                          chargées par Hibernate avec
	 *                          l'annotation @LazyCollection(LazyCollectionOption.TRUE),
	 *                          donc seulement quand on y accède réellement. <br/>
	 *                          true : charge les histoires, false : ne charge pas
	 *                          les histoires (on se contente du nombre total
	 *                          d'histoires qui, lui, est toujours chargé)
	 * @return Une {@code List<MemberDto>}
	 */
	public List<MemberDto> map(List<Member> source, boolean shouldLoadStories) {

		List<MemberDto> target = new ArrayList<>();

		for (Member member : source) {
			target.add(map(member, shouldLoadStories));
		}

		return target;

	}

	private MemberDetailsDto map(MemberDetails source) {
		
		return MemberDetailsDto.builder()
			.firstName(source.getFirstName())
			.lastName(source.getLastName()).build();
		// TODO : finir de mapper les champs de MemberDetails
		
	}

}
