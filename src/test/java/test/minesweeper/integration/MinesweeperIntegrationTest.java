package test.minesweeper.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import test.minesweeper.component.GameComponent;
import test.minesweeper.controller.MinesweeperController;
import test.minesweeper.dto.GameInfoResponse;
import test.minesweeper.dto.GameNewRequest;
import test.minesweeper.dto.GameTurnRequest;
import test.minesweeper.enums.FieldEnum;
import test.minesweeper.exception.FieldCoordinatesException;
import test.minesweeper.exception.FieldOpenException;
import test.minesweeper.exception.GameNotFoundException;
import test.minesweeper.exception.MinesCountException;
import test.minesweeper.service.MinesweeperService;

import java.time.LocalDateTime;

@Slf4j
@SpringBootTest
public class MinesweeperIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(MinesweeperIntegrationTest.class);
    @Autowired
    private MinesweeperController minesweeperController;

    @Autowired
    private MinesweeperService minesweeperService;

    @Autowired
    private GameComponent gameComponent;

    @Test
    public void testCreateGame() {
        final int HEIGHT = 10;
        final int WIDTH = 10;
        final int MINES_COUNT = 10;
        GameNewRequest gameRequest = new GameNewRequest();
        gameRequest.setHeight(HEIGHT);
        gameRequest.setWidth(WIDTH);
        gameRequest.setMines_count(MINES_COUNT);
        GameInfoResponse gameInfoResponse = minesweeperController.createNewGame(gameRequest);
        Assertions.assertNotNull(gameInfoResponse.getGame_id());
    }

    @Test
    public void testNegativeMinesCount() {
        final int HEIGHT = 10;
        final int WIDTH = 10;
        final int MINES_COUNT = -10;
        GameNewRequest gameRequest = new GameNewRequest();
        gameRequest.setHeight(HEIGHT);
        gameRequest.setWidth(WIDTH);
        gameRequest.setMines_count(MINES_COUNT);
        MinesCountException thrown = Assertions.assertThrows(
                MinesCountException.class,
                () -> minesweeperController.createNewGame(gameRequest), "MinesCountException error was expected"
        );

        Assertions.assertEquals(String.format("количество мин должно быть не менее %d и не более %d", 1, HEIGHT * WIDTH - 1),
                thrown.getMessage());
    }

    @Test
    public void testOverflowMinesCount() {
        final int HEIGHT = 10;
        final int WIDTH = 10;
        final int MINES_COUNT = 100;
        GameNewRequest gameRequest = new GameNewRequest();
        gameRequest.setHeight(HEIGHT);
        gameRequest.setWidth(WIDTH);
        gameRequest.setMines_count(MINES_COUNT);
        MinesCountException thrown = Assertions.assertThrows(
                MinesCountException.class, () ->
                        minesweeperController.createNewGame(gameRequest), "MinesCountException error was expected"
        );

        Assertions.assertEquals(String.format("количество мин должно быть не менее %d и не более %d", 1, HEIGHT * WIDTH - 1),
                thrown.getMessage());
    }

    @Test
    public void testTurn() {
        final int HEIGHT = 10;
        final int WIDTH = 10;
        final int MINES_COUNT = 10;
        final int COL = 4;
        final int ROW = 4;

        GameNewRequest gameRequest = new GameNewRequest();
        gameRequest.setHeight(HEIGHT);
        gameRequest.setWidth(WIDTH);
        gameRequest.setMines_count(MINES_COUNT);
        GameInfoResponse gameInfoResponse = minesweeperController.createNewGame(gameRequest);

        GameTurnRequest gameTurnRequest = new GameTurnRequest();
        gameTurnRequest.setGame_id(gameInfoResponse.getGame_id());
        gameTurnRequest.setCol(COL);
        gameTurnRequest.setRow(ROW);
        gameInfoResponse = minesweeperController.turn(gameTurnRequest);

        Assertions.assertNotEquals(FieldEnum.EMPTY.getValue(), gameInfoResponse.getField()[ROW][COL]);
    }

    @Test
    public void testRecallTurn() {
        final int HEIGHT = 10;
        final int WIDTH = 10;
        final int MINES_COUNT = 10;
        final int COL = 4;
        final int ROW = 4;

        GameNewRequest gameRequest = new GameNewRequest();
        gameRequest.setHeight(HEIGHT);
        gameRequest.setWidth(WIDTH);
        gameRequest.setMines_count(MINES_COUNT);
        GameInfoResponse gameInfoResponse = minesweeperController.createNewGame(gameRequest);

        GameTurnRequest gameTurnRequest = new GameTurnRequest();
        gameTurnRequest.setGame_id(gameInfoResponse.getGame_id());
        gameTurnRequest.setCol(COL);
        gameTurnRequest.setRow(ROW);
        minesweeperController.turn(gameTurnRequest);

        FieldOpenException thrown = Assertions.assertThrows(
                FieldOpenException.class, () ->
                        minesweeperController.turn(gameTurnRequest), "FieldOpenException error was expected"
        );

        Assertions.assertEquals("уже открытая ячейка", thrown.getMessage());
    }

    @Test
    public void testWinGame() {
        final int HEIGHT = 10;
        final int WIDTH = 10;
        final int MINES_COUNT = 99;
        final int COL = 4;
        final int ROW = 4;

        GameNewRequest gameRequest = new GameNewRequest();
        gameRequest.setHeight(HEIGHT);
        gameRequest.setWidth(WIDTH);
        gameRequest.setMines_count(MINES_COUNT);
        GameInfoResponse gameClient = minesweeperController.createNewGame(gameRequest);

        GameTurnRequest gameTurnRequest = new GameTurnRequest();
        gameTurnRequest.setGame_id(gameClient.getGame_id());
        gameTurnRequest.setCol(COL);
        gameTurnRequest.setRow(ROW);
        gameClient = minesweeperController.turn(gameTurnRequest);

        Assertions.assertEquals(true, gameClient.getCompleted());
    }

    @Test
    public void testLostGame() {
        final int HEIGHT = 10;
        final int WIDTH = 10;
        final int MINES_COUNT = 98;
        final int COL = 4;
        final int ROW = 4;

        GameNewRequest gameRequest = new GameNewRequest();
        gameRequest.setHeight(HEIGHT);
        gameRequest.setWidth(WIDTH);
        gameRequest.setMines_count(MINES_COUNT);
        GameInfoResponse gameInfoResponse = minesweeperController.createNewGame(gameRequest);

        GameTurnRequest gameTurnRequest = new GameTurnRequest();
        gameTurnRequest.setGame_id(gameInfoResponse.getGame_id());
        gameTurnRequest.setCol(COL);
        gameTurnRequest.setRow(ROW);
        gameInfoResponse = minesweeperController.turn(gameTurnRequest);

        if (FieldEnum.EIGHT.getValue().equals(gameInfoResponse.getField()[ROW][COL])) {
            gameTurnRequest.setCol(COL - 1);
            gameInfoResponse = minesweeperController.turn(gameTurnRequest);
            Assertions.assertEquals(FieldEnum.X.getValue(), gameInfoResponse.getField()[ROW][COL -1]);
        } else {
            gameTurnRequest.setCol(0);
            gameTurnRequest.setRow(0);
            gameInfoResponse = minesweeperController.turn(gameTurnRequest);
            Assertions.assertEquals(FieldEnum.X.getValue(), gameInfoResponse.getField()[0][0]);
        }
    }

    @Test
    public void testGameTimeExpired() throws InterruptedException {
        final int HEIGHT = 10;
        final int WIDTH = 10;
        final int MINES_COUNT = 10;
        final int COL = 4;
        final int ROW = 4;

        GameNewRequest gameRequest = new GameNewRequest();
        gameRequest.setHeight(HEIGHT);
        gameRequest.setWidth(WIDTH);
        gameRequest.setMines_count(MINES_COUNT);
        GameInfoResponse gameInfoResponse = minesweeperController.createNewGame(gameRequest);

        GameTurnRequest gameTurnRequest = new GameTurnRequest();
        gameTurnRequest.setGame_id(gameInfoResponse.getGame_id());
        gameTurnRequest.setCol(COL);
        gameTurnRequest.setRow(ROW);

        gameComponent.getGame(gameInfoResponse.getGame_id()).setLocalDateTime(LocalDateTime.MIN);
        gameComponent.deleteGame();

        GameNotFoundException thrown = Assertions.assertThrows(
                GameNotFoundException.class, () ->
                        minesweeperController.turn(gameTurnRequest), "GameNotFoundException error was expected"
        );

        Assertions.assertEquals(String.format("игра с идентификатором %s не была создана или устарела (неактуальна)", gameInfoResponse.getGame_id())
                , thrown.getMessage());
    }

    @Test
    public void testInvalidTurn() {
        final int HEIGHT = 10;
        final int WIDTH = 10;
        final int MINES_COUNT = 10;
        final int COL = -1;
        final int ROW = -1;

        GameNewRequest gameRequest = new GameNewRequest();
        gameRequest.setHeight(HEIGHT);
        gameRequest.setWidth(WIDTH);
        gameRequest.setMines_count(MINES_COUNT);
        GameInfoResponse gameInfoResponse = minesweeperController.createNewGame(gameRequest);

        GameTurnRequest gameTurnRequest = new GameTurnRequest();
        gameTurnRequest.setGame_id(gameInfoResponse.getGame_id());
        gameTurnRequest.setCol(COL);
        gameTurnRequest.setRow(ROW);

        FieldCoordinatesException thrown = Assertions.assertThrows(
                FieldCoordinatesException.class, () ->
                        minesweeperController.turn(gameTurnRequest), "FieldCoordinatesException error was expected"
        );

        Assertions.assertEquals(String.format("выбранная клетка должна быть с координатамине " +
                        "не менее %d и не более %d по строкам и не менее %d и не более %d по столбцам",
                0, HEIGHT - 1,
                0, WIDTH - 1), thrown.getMessage());
    }
}
