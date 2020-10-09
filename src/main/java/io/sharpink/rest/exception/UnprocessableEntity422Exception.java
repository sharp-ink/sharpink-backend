package io.sharpink.rest.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Classe représentant une erreur 422 (unprocessable entity).
 *
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
@Data
@AllArgsConstructor
public class UnprocessableEntity422Exception extends RuntimeException {
  private UnprocessableEntity422ReasonEnum reason;
}
