package io.sharpink.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.sharpink.mapper.member.MemberMapper;
import io.sharpink.persistence.dao.MemberDao;
import io.sharpink.persistence.entity.member.Member;
import io.sharpink.rest.dto.member.MemberDto;

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

		Optional<Member> optionalMember = memberDao.findById(id);

		if (optionalMember.isPresent()) {
			boolean shouldLoadStories = false;
			return Optional.of(memberMapper.map(optionalMember.get(), shouldLoadStories));
		} else {
			return Optional.empty();
		}

	}

}
