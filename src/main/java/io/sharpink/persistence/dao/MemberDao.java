package io.sharpink.persistence.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.sharpink.persistence.entity.member.Member;

@Repository
public interface MemberDao extends CrudRepository<Member, Long> {

	/**
	 * Cherche le Member ayant les identifiants login + password passés en
	 * paramètre. <br/>
	 * Le login peut être soit le pseudo soit l'email du membre.
	 * 
	 * @param login
	 * @param password
	 * @return
	 */
	@Query(
		"FROM Member m " +
		"WHERE (m.nickname = :login OR m.email = :login) " + 
		"AND m.password = :password")
	Optional<Member> findByCredentials(String login, String password);
		
}
