package io.sharpink.api.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Represents a 401 error (not authenticated OR wrong authentication).
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class Unauthorized401Exception extends RuntimeException {

}
