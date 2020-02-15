package io.sharpink.rest.endpoint;

import java.util.List;
import java.util.Optional;

import io.sharpink.rest.dto.member.MemberDetailsDto;
import io.sharpink.rest.dto.member.profile.MemberProfileDto;
import io.sharpink.rest.dto.story.StoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.sharpink.rest.dto.member.MemberDto;
import io.sharpink.rest.exception.NotFound404Exception;
import io.sharpink.service.MemberService;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/members")
public class MemberController {

	private MemberService memberService;

	@Autowired
	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}

	@GetMapping("")
	public List<MemberDto> getMembers() {
		return memberService.getAllMembers();
	}

	@GetMapping("/{id}")
	public MemberDto getMember(@PathVariable Long id) {
		Optional<MemberDto> optionalMemberDto = memberService.getMember(id);
		if (optionalMemberDto.isPresent()) {
			return optionalMemberDto.get();
		} else {
			throw new NotFound404Exception();
		}
	}

	@GetMapping("/{id}/stories")
  public List<StoryDto> getStories(@PathVariable Long id) {
	  return memberService.getStories(id);
  }

	@PutMapping("/{id}/profile")
  public MemberDto updateMemberProfile(@PathVariable Long id, @RequestBody MemberProfileDto memberProfileDto) {
	  return memberService.updateMemberProfile(id, memberProfileDto);
  }
}
