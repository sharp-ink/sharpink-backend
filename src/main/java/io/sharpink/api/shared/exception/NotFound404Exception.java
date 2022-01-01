package io.sharpink.api.shared.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Represents a 404 error (resource not found).
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotFound404Exception extends RuntimeException {
    private MissingEntity reason;

    public NotFound404Exception(MissingEntity reason, String message) {
        super(message);
        this.reason = reason;
    }
}
