package test.minesweeper.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FieldCoordinatesException extends IllegalArgumentException {
    public FieldCoordinatesException(int height, int width) {
        super(String.format("выбранная клетка должна быть с координатамине " +
                        "не менее %d и не более %d по строкам и не менее %d и не более %d по столбцам",
                0, height - 1,
                0, width - 1));
    }
}
