package io.sharpink.persistence.dao;

import java.util.Optional;

import io.sharpink.persistence.entity.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends CrudRepository<User, Long> {

	/**
	 * Cherche le User ayant les identifiants login + password passés en
	 * paramètre. <br/>
	 * Le login peut être soit le pseudo soit l'email du membre.
	 *
	 * @param login
	 * @param password
	 * @return
	 */
	@Query(
		"FROM User m " +
		"WHERE (m.nickname = :login OR m.email = :login) " +
		"AND m.password = :password")
	Optional<User> findByCredentials(String login, String password);

}
