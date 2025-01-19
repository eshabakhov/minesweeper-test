package test.minesweeper.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FieldOpenException extends IllegalArgumentException {
    public FieldOpenException(String message) {
        super(message);
    }
}
