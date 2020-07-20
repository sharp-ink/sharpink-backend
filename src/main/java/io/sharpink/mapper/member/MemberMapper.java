package io.sharpink.mapper.member;

import java.util.ArrayList;
import java.util.List;

import io.sharpink.persistence.entity.story.ChaptersLoadingStrategy;
import io.sharpink.persistence.entity.story.StoriesLoadingStrategy;
import io.sharpink.rest.dto.request.member.MemberPatchRequest;
import io.sharpink.rest.dto.response.member.MemberResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.sharpink.mapper.story.StoryMapper;
import io.sharpink.persistence.entity.member.Member;
import io.sharpink.persistence.entity.member.MemberDetails;
import io.sharpink.rest.dto.response.member.MemberDetailsResponse;

@Component
public class MemberMapper {

  private StoryMapper storyMapper;

  // use setter injection here because of circular dependency MemberMapper <-> StoryMapper
  @Autowired
  public void setStoryMapper(StoryMapper storyMapper) {
    this.storyMapper = storyMapper;
  }

  /**
   * Construit un objet {@code MemberResponse} à partir d'un objet {@code Member}.
   *
   * @param source                 L'entité {@code Member} à partir de laquelle on
   *                               construit notre objet.
   * @param storiesLoadingStrategy Indique s'il faut ou non charger la liste des
   *                               histoires, qui est pour rappel chargé par Hibernate
   *                               avec
   *                               l'annotation @LazyCollection(LazyCollectionOption.TRUE),
   *                               donc seulement quand on y accède réellement. <br/>
   *                               true : charge les histoires, false : ne charge pas
   *                               les histoires (on se contente du nombre total
   *                               d'histoires qui, lui, est toujours chargé)
   * @return Un {@code Member}
   */
  public MemberResponse map(Member source, StoriesLoadingStrategy storiesLoadingStrategy) {
    MemberResponse target = MemberResponse.builder()
      .id(source.getId())
      .nickname(source.getNickname())
      .email(source.getEmail())
      .storiesCount(source.getStoriesCount())
      .memberDetails(source.getMemberDetails().isPresent() ? map(source.getMemberDetails().get()) : null)
      .build();

    // on ne charge les histoires que si demandé
    if (storiesLoadingStrategy == StoriesLoadingStrategy.ENABLED) {
      // meme si on veut les histoires, on ne veut pas charger les chapitres
      target.setStories(storyMapper.toStoryResponseList(source.getStories(), ChaptersLoadingStrategy.DISABLED));
    }

    return target;
  }

  /**
   * Construit une {@code List<MemberResponse>} à partir d'une {@code List<Member>}.
   *
   * @param source                 La liste des {@code Member} à partir duquel on
   *                               construit notre liste de {@code MemberResponse}.
   * @param storiesLoadingStrategy Indique s'il faut ou non charger la liste des
   *                               histoires pour chaque membre, qui sont pour rappel
   *                               chargées par Hibernate avec
   *                               l'annotation @LazyCollection(LazyCollectionOption.TRUE),
   *                               donc seulement quand on y accède réellement. <br/>
   *                               true : charge les histoires, false : ne charge pas
   *                               les histoires (on se contente du nombre total
   *                               d'histoires qui, lui, est toujours chargé)
   * @return Une {@code List<MemberResponse>}
   */
  public List<MemberResponse> map(List<Member> source, StoriesLoadingStrategy storiesLoadingStrategy) {
    List<MemberResponse> target = new ArrayList<>();
    for (Member member : source) {
      target.add(map(member, storiesLoadingStrategy));
    }
    return target;
  }

  private MemberDetailsResponse map(MemberDetails source) {
    return MemberDetailsResponse.builder()
      .firstName(source.getFirstName())
      .lastName(source.getLastName())
      .profilePicture(source.getProfilePicture())
      // TODO : finir de mapper les champs de MemberDetails
      .build();
  }

  public MemberDetails map(MemberPatchRequest memberPatchRequest) {
    return MemberDetails.builder()
      .firstName(memberPatchRequest.getFirstName())
      .lastName(memberPatchRequest.getLastName())
      // TODO mapper les autres champs
      .build();
  }
}
