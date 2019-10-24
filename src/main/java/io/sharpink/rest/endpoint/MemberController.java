package io.sharpink.rest.endpoint;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.sharpink.rest.dto.member.MemberDto;
import io.sharpink.rest.exception.ResourceNotFoundException;
import io.sharpink.service.MemberService;

@RestController
@RequestMapping("/members")
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 86400)
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
			throw new ResourceNotFoundException();
		}
	}

}
