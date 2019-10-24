package io.sharpink.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Classe représentant une erreur 401 (échec d'authentification).
 * 
 * @author scaunois
 *
 */
@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class AuthenticationFailureException extends RuntimeException {

}
