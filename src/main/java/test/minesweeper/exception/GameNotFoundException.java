package test.minesweeper.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class GameNotFoundException extends IllegalStateException {
    public GameNotFoundException(String message) {
        super(message);
    }
}
