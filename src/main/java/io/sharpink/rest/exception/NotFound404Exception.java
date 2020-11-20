package io.sharpink.rest.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Classe représentant une erreur 404 (resource non trouvée).
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NotFound404Exception extends RuntimeException {
  private MissingEntity reason;

  public NotFound404Exception(MissingEntity reason, String message) {
    super(message);
    this.reason = reason;
  }
}
