package io.sharpink.service;

import io.sharpink.mapper.member.MemberMapper;
import io.sharpink.persistence.dao.MemberDao;
import io.sharpink.persistence.entity.member.Member;
import io.sharpink.persistence.entity.member.MemberDetails;
import io.sharpink.rest.dto.member.MemberDto;
import io.sharpink.rest.dto.member.profile.MemberProfileDto;
import io.sharpink.rest.exception.NotFound404Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static io.sharpink.constant.Constants.USERS_PROFILE_PICTURES_PATH;
import static io.sharpink.constant.Constants.USERS_PROFILE_PICTURES_WEB_URL;

@Service
public class MemberService {

	private MemberDao memberDao;

	private MemberMapper memberMapper;

	@Autowired
	public MemberService(MemberDao memberDao, MemberMapper memberMapper) {
		this.memberDao = memberDao;
		this.memberMapper = memberMapper;
	}

	public List<MemberDto> getAllMembers() {

		List<Member> members = (List<Member>) memberDao.findAll();
		boolean shouldLoadStories = false;
		return memberMapper.map(members, shouldLoadStories);

	}

	public Optional<MemberDto> getMember(Long id) {

		Optional<Member> memberOptional = memberDao.findById(id);

		if (memberOptional.isPresent()) {
			boolean shouldLoadStories = false;
			return Optional.of(memberMapper.map(memberOptional.get(), shouldLoadStories));
		} else {
			return Optional.empty();
		}

	}

  public MemberDto updateMemberProfile(Long id, MemberProfileDto memberProfileDto) {
	  Optional<Member> memberOptional = memberDao.findById(id);
	  if (memberOptional.isPresent()) {
	    Member member = memberOptional.get();
      // update profile informations
      member.setNickname(memberProfileDto.getNickname());
      member.setEmail(memberProfileDto.getEmail());
      MemberDetails newMemberDetails = memberMapper.map(memberProfileDto);

      // update profile picture (if any) and store it on the file system
      if (!memberProfileDto.getProfilePicture().isEmpty()) {
        String nickname = member.getNickname();
        String extension = getExtension(memberProfileDto.getProfilePicture());
        newMemberDetails.setProfilePicture(USERS_PROFILE_PICTURES_WEB_URL + '/' + nickname + '/' + nickname + '.' + extension);
        try {
          storeProfilePictureOnFileSystem(getBase64ImageContent(memberProfileDto.getProfilePicture()), nickname, extension);
        } catch (IOException e) {
          e.printStackTrace();
          // TODO rethrow e and handle it better
        }
      } else if (member.getMemberDetails().isPresent() && member.getMemberDetails().get().getProfilePicture() != null) {
        // keep old profile picture (it there was one)
        newMemberDetails.setProfilePicture(member.getMemberDetails().get().getProfilePicture());
      }

      // update profile in DB
      member.setMemberDetails(newMemberDetails);
      newMemberDetails.setMember(member);
      memberDao.save(member);

      boolean shouldLoadStories = false;
      return memberMapper.map(member, shouldLoadStories);
    } else {
	    throw new NotFound404Exception();
    }
  }

  private String getBase64ImageContent(String profilePictureBase64) {
	  return profilePictureBase64.split(",")[1];
  }

  private String getExtension(String profilePictureBase64) {
    return profilePictureBase64.split(",")[0] // get the "data:image/XXX;base64" part
      .split(";")[0] // get the "data:image/XXX" part
      .split("/")[1]; // get the "XXX" part (this is the extension!)
	}

  private void storeProfilePictureOnFileSystem(String base64Image, String nickame, String extension) throws IOException {
	  Path profilePictureFolderPath = Files.createDirectories(Paths.get(USERS_PROFILE_PICTURES_PATH + '/' + nickame)); // create the folder containing the user profile picture if it does not exist
    Files.write(
      Paths.get(profilePictureFolderPath.toString() + '/' + nickame + '.' + extension),
      javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image)
    );
	}
}
