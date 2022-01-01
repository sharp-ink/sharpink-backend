package io.sharpink.api.shared.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Represents a 422 error (unprocessable entity).
 *
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
@Getter
@Setter
@AllArgsConstructor
public class UnprocessableEntity422Exception extends RuntimeException {
    private UnprocessableEntity422ReasonEnum reason;
}
