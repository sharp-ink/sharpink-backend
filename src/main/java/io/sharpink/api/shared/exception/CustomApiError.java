package io.sharpink.api.shared.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/*
 * Represents an error sent back to frontend. Contains an error code and eventually a more verbose explanation.
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
