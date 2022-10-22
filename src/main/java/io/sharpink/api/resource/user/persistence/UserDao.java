package io.sharpink.api.resource.user.persistence;

import io.sharpink.api.resource.user.persistence.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends PagingAndSortingRepository<User, Long> {

	/**
	 * Retrieve user with given login + password.<br/>
	 * Login can be either user's nickname or user's email.
	 */
	@Query("FROM User u WHERE (u.nickname = :login OR u.email = :login) AND u.password = :password")
	Optional<User> findByCredentials(String login, String password);

}
