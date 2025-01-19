package test.minesweeper.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MinesCountException extends IllegalArgumentException {
    public MinesCountException(String message) {
        super(message);
    }
}
