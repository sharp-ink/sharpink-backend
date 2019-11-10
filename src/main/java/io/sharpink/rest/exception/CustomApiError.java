package io.sharpink.rest.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

/*
 * Représente une erreur renvoyée au frontend
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CustomApiError {
  @NonNull
  private String code;
  private String message; // optionnel
}
