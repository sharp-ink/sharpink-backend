package io.sharpink.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Classe représentant une erreur 401 (non authentifié ou bien authentification incorrecte).
 */
@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class Unauthorized401Exception extends RuntimeException {

}
