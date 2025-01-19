package test.minesweeper.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FieldCoordinatesException extends IllegalArgumentException {
    public FieldCoordinatesException(String message) {
        super(message);
    }
}
