package io.sharpink.api.shared.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalError500Exception extends RuntimeException {
    private Exception cause;
}
