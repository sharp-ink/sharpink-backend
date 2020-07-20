package io.sharpink.rest.controller;

import java.util.List;
import java.util.Optional;

import io.sharpink.rest.dto.request.member.MemberPatchRequest;
import io.sharpink.rest.dto.response.member.MemberResponse;
import io.sharpink.rest.dto.response.story.StoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.sharpink.rest.exception.NotFound404Exception;
import io.sharpink.service.MemberService;

@RestController
@RequestMapping("/members")
public class MemberController {

	private MemberService memberService;

	@Autowired
	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}

	@GetMapping("")
	public List<MemberResponse> getMembers() {
		return memberService.getAllMembers();
	}

	@GetMapping("/{id}")
	public MemberResponse getMember(@PathVariable Long id) {
		Optional<MemberResponse> optionalMemberResponse = memberService.getMember(id);
		if (optionalMemberResponse.isPresent()) {
			return optionalMemberResponse.get();
		} else {
			throw new NotFound404Exception();
		}
	}

	@GetMapping("/{id}/stories")
  public List<StoryResponse> getStories(@PathVariable Long id) {
	  return memberService.getStories(id);
  }

	@PutMapping("/{id}/profile")
  public MemberResponse updateMemberProfile(@PathVariable Long id, @RequestBody MemberPatchRequest memberPatchRequest) {
	  return memberService.updateMemberProfile(id, memberPatchRequest);
  }
}
