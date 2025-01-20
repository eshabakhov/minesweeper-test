package test.minesweeper.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class GameNotFoundException extends IllegalStateException {
    public GameNotFoundException(String gameTurnRequestGameId) {
        super(String.format("игра с идентификатором %s не была создана или устарела (неактуальна)", gameTurnRequestGameId));
    }
}
