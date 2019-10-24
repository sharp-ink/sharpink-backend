package io.sharpink.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.sharpink.mapper.member.MemberMapper;
import io.sharpink.persistence.dao.MemberDao;
import io.sharpink.persistence.entity.member.Member;
import io.sharpink.rest.dto.member.MemberDto;

@Service
public class AccountService {

	private MemberDao memberDao;

	private MemberMapper memberMapper;

	@Autowired
	public AccountService(MemberDao memberDao, MemberMapper memberMapper) {
		this.memberDao = memberDao;
		this.memberMapper = memberMapper;
	}

	/**
	 * Renvoie le membre authentifié si les identifiants envoyés sont les bons, null
	 * sinon.
	 * 
	 * @param memberCredentials
	 * @return
	 */
	public Optional<MemberDto> logIn(String login, String password) {

		Optional<Member> optionalMember = memberDao.findByCredentials(login, password);
		if (optionalMember.isPresent()) {
			return Optional.of(memberMapper.map(optionalMember.get(), false));
		} else {
			return Optional.empty();
		}
		
	}

}
