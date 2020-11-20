package io.sharpink.rest.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/*
 * Représente une erreur renvoyée au frontend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CustomApiError {
  private String code;
  private String message;

  public CustomApiError(String code) {
    this.code = code;
  }
}
