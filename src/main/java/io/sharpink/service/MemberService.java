package io.sharpink.service;

import io.sharpink.mapper.member.MemberMapper;
import io.sharpink.persistence.dao.MemberDao;
import io.sharpink.persistence.entity.member.Member;
import io.sharpink.persistence.entity.member.MemberDetails;
import io.sharpink.persistence.entity.story.StoriesLoadingStrategy;
import io.sharpink.rest.dto.response.member.MemberResponse;
import io.sharpink.rest.dto.response.story.StoryResponse;
import io.sharpink.rest.exception.InternalError500Exception;
import io.sharpink.util.picture.PictureUtil;
import io.sharpink.rest.dto.request.member.MemberPatchRequest;
import io.sharpink.rest.exception.NotFound404Exception;
import io.sharpink.service.picture.PictureManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static io.sharpink.constant.Constants.USERS_PROFILE_PICTURES_PATH;
import static io.sharpink.constant.Constants.USERS_PROFILE_PICTURES_WEB_URL;

@Service
public class MemberService {

  private MemberDao memberDao;
  private MemberMapper memberMapper;
  private StoryService storyService;
  private PictureManagementService pictureManagementService;

  @Autowired
  public MemberService(MemberDao memberDao, MemberMapper memberMapper, StoryService storyService, PictureManagementService pictureManagementService) {
    this.memberDao = memberDao;
    this.memberMapper = memberMapper;
    this.storyService = storyService;
    this.pictureManagementService = pictureManagementService;
  }

  public List<MemberResponse> getAllMembers() {
    List<Member> members = (List<Member>) memberDao.findAll();
    return memberMapper.map(members, StoriesLoadingStrategy.DISABLED);
  }

  public Optional<MemberResponse> getMember(Long id) {

    Optional<Member> memberOptional = memberDao.findById(id);

    if (memberOptional.isPresent()) {
      return Optional.of(memberMapper.map(memberOptional.get(), StoriesLoadingStrategy.DISABLED));
    } else {
      return Optional.empty();
    }

  }

  public List<StoryResponse> getStories(Long memberId) {
    return storyService.getStories(memberId);
  }

  public MemberResponse updateMemberProfile(Long id, MemberPatchRequest memberPatchRequest) {
    Optional<Member> memberOptional = memberDao.findById(id);

    Member member = memberOptional.orElseThrow(NotFound404Exception::new);

    // update profile informations
    member.setNickname(memberPatchRequest.getNickname());
    member.setEmail(memberPatchRequest.getEmail());
    MemberDetails newMemberDetails = memberMapper.map(memberPatchRequest);

    // update profile picture (if any) and store it on the file system
    if (!memberPatchRequest.getProfilePicture().isEmpty()) {
      String nickname = member.getNickname();
      String extension = PictureUtil.extractExtension(memberPatchRequest.getProfilePicture());
      String profilePictureWebUrl = USERS_PROFILE_PICTURES_WEB_URL + '/' + nickname + '/' + nickname + '.' + extension;
      newMemberDetails.setProfilePicture(profilePictureWebUrl);
      try {
        String profilePictureBase64Content = PictureUtil.extractBase64Content(memberPatchRequest.getProfilePicture());
        String profilePictureFSPath = USERS_PROFILE_PICTURES_PATH + '/' + nickname + '/' + nickname + '.' + extension;
        pictureManagementService.storePictureOnFileSystem(profilePictureBase64Content, profilePictureFSPath);
      } catch (IOException e) {
        e.printStackTrace(); // TODO: use a logger instead
        throw new InternalError500Exception(e);
      }
    } else if (member.getMemberDetails().isPresent() && member.getMemberDetails().get().getProfilePicture() != null) {
      // keep old profile picture (if there was one)
      newMemberDetails.setProfilePicture(member.getMemberDetails().get().getProfilePicture());
    }

    // update profile in DB
    member.setMemberDetails(newMemberDetails);
    newMemberDetails.setMember(member);
    memberDao.save(member);

    return memberMapper.map(member, StoriesLoadingStrategy.DISABLED);
  }
}
