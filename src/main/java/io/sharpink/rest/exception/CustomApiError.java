package io.sharpink.rest.exception;

import lombok.*;

/*
 * Représente une erreur renvoyée au frontend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomApiError {
  private String code;
  private String message;

  public CustomApiError(String code) {
    this.code = code;
  }
}
