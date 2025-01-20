package test.minesweeper.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MinesCountException extends IllegalArgumentException {
    public MinesCountException(int height, int width) {
        super(String.format("количество мин должно быть не менее %d и не более %d", 1, width * height - 1));
    }
}
