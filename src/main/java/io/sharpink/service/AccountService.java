package io.sharpink.service;

import java.util.Optional;
import io.sharpink.persistence.entity.story.StoriesLoadingStrategy;
import io.sharpink.rest.dto.response.member.MemberResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.sharpink.mapper.member.MemberMapper;
import io.sharpink.persistence.dao.MemberDao;
import io.sharpink.persistence.entity.member.Member;

@Service
public class AccountService {

	private MemberDao memberDao;
	private MemberMapper memberMapper;

	@Autowired
	public AccountService(MemberDao memberDao, MemberMapper memberMapper) {
		this.memberDao = memberDao;
		this.memberMapper = memberMapper;
	}

	public Optional<MemberResponse> logIn(String login, String password) {

		Optional<Member> optionalMember = memberDao.findByCredentials(login, password);
		if (optionalMember.isPresent()) {
			return Optional.of(memberMapper.map(optionalMember.get(), StoriesLoadingStrategy.DISABLED));
		} else {
			return Optional.empty();
		}

	}
}
