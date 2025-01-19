package test.minesweeper;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import test.minesweeper.controller.MinesweeperController;
import test.minesweeper.dto.GameClient;
import test.minesweeper.dto.GameTurnRequest;
import test.minesweeper.dto.GameNewRequest;
import test.minesweeper.enums.FieldEnum;
import test.minesweeper.exception.FieldCoordinatesException;
import test.minesweeper.exception.FieldOpenException;
import test.minesweeper.exception.GameNotFoundException;
import test.minesweeper.exception.MinesCountException;
import test.minesweeper.service.MinesweeperService;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MinesweeperApplicationTests {

    @Autowired
    private MinesweeperController minesweeperController;

    @Autowired
    private MinesweeperService minesweeperService;

    @Test
    @Order(1)
    public void testCreateGame() {
        GameNewRequest gameRequest = new GameNewRequest();
        gameRequest.setHeight(10);
        gameRequest.setWidth(10);
        gameRequest.setMines_count(10);
        GameClient gameClient = minesweeperController.createNewGame(gameRequest);
        Assertions.assertNotNull(gameClient.getGame_id());
    }

    @Test
    @Order(2)
    public void testNegativeMinesCount() {
        final int height = 10;
        final int width = 10;
        final int minesCount = -10;
        GameNewRequest gameRequest = new GameNewRequest();
        gameRequest.setHeight(height);
        gameRequest.setWidth(width);
        gameRequest.setMines_count(minesCount);
        MinesCountException thrown = Assertions.assertThrows(
                MinesCountException.class,
                () -> minesweeperController.createNewGame(gameRequest), "MinesCountException error was expected"
        );

        Assertions.assertEquals(String.format("количество мин должно быть не менее %d и не более %d", 1, height * width - 1),
                thrown.getMessage());
    }

    @Test
    @Order(3)
    public void testOverflowMinesCount() {
        final int height = 10;
        final int width = 10;
        final int minesCount = 100;
        GameNewRequest gameRequest = new GameNewRequest();
        gameRequest.setHeight(height);
        gameRequest.setWidth(width);
        gameRequest.setMines_count(minesCount);
        MinesCountException thrown = Assertions.assertThrows(
                MinesCountException.class, () ->
                        minesweeperController.createNewGame(gameRequest), "MinesCountException error was expected"
        );

        Assertions.assertEquals(String.format("количество мин должно быть не менее %d и не более %d", 1, height * width - 1),
                thrown.getMessage());
    }

    @Test
    @Order(4)
    public void testTurn() {
        final int height = 10;
        final int width = 10;
        final int minesCount = 10;
        final int col = 4;
        final int row = 4;

        GameNewRequest gameRequest = new GameNewRequest();
        gameRequest.setHeight(height);
        gameRequest.setWidth(width);
        gameRequest.setMines_count(minesCount);
        GameClient gameClient = minesweeperController.createNewGame(gameRequest);

        GameTurnRequest gameTurnRequest = new GameTurnRequest();
        gameTurnRequest.setGame_id(gameClient.getGame_id());
        gameTurnRequest.setCol(col);
        gameTurnRequest.setRow(row);
        gameClient = minesweeperController.turn(gameTurnRequest);

        Assertions.assertNotEquals(FieldEnum.EMPTY.getValue(), gameClient.getField()[row][col]);
    }

    @Test
    @Order(5)
    public void testRecallTurn() {
        final int height = 10;
        final int width = 10;
        final int minesCount = 10;
        final int col = 4;
        final int row = 4;

        GameNewRequest gameRequest = new GameNewRequest();
        gameRequest.setHeight(height);
        gameRequest.setWidth(width);
        gameRequest.setMines_count(minesCount);
        GameClient gameClient = minesweeperController.createNewGame(gameRequest);

        GameTurnRequest gameTurnRequest = new GameTurnRequest();
        gameTurnRequest.setGame_id(gameClient.getGame_id());
        gameTurnRequest.setCol(col);
        gameTurnRequest.setRow(row);
        minesweeperController.turn(gameTurnRequest);

        FieldOpenException thrown = Assertions.assertThrows(
                FieldOpenException.class, () ->
                        minesweeperController.turn(gameTurnRequest), "FieldOpenException error was expected"
        );

        Assertions.assertEquals("уже открытая ячейка", thrown.getMessage());
    }

    @Test
    @Order(6)
    public void testWinGame() {
        final int height = 10;
        final int width = 10;
        final int minesCount = 99;
        final int col = 4;
        final int row = 4;

        GameNewRequest gameRequest = new GameNewRequest();
        gameRequest.setHeight(height);
        gameRequest.setWidth(width);
        gameRequest.setMines_count(minesCount);
        GameClient gameClient = minesweeperController.createNewGame(gameRequest);

        GameTurnRequest gameTurnRequest = new GameTurnRequest();
        gameTurnRequest.setGame_id(gameClient.getGame_id());
        gameTurnRequest.setCol(col);
        gameTurnRequest.setRow(row);
        gameClient = minesweeperController.turn(gameTurnRequest);

        Assertions.assertEquals(true, gameClient.getCompleted());
    }

    @Test
    @Order(7)
    public void testLostGame() {
        final int height = 10;
        final int width = 10;
        final int minesCount = 98;
        final int col = 4;
        final int row = 4;

        GameNewRequest gameRequest = new GameNewRequest();
        gameRequest.setHeight(height);
        gameRequest.setWidth(width);
        gameRequest.setMines_count(minesCount);
        GameClient gameClient = minesweeperController.createNewGame(gameRequest);

        GameTurnRequest gameTurnRequest = new GameTurnRequest();
        gameTurnRequest.setGame_id(gameClient.getGame_id());
        gameTurnRequest.setCol(col);
        gameTurnRequest.setRow(row);
        gameClient = minesweeperController.turn(gameTurnRequest);

        if (FieldEnum.EIGHT.getValue().equals(gameClient.getField()[col][row])) {
            gameTurnRequest.setCol(col - 1);
            gameClient = minesweeperController.turn(gameTurnRequest);
            Assertions.assertEquals(FieldEnum.X.getValue(), gameClient.getField()[row][col -1]);
        } else {
            gameTurnRequest.setCol(0);
            gameTurnRequest.setRow(0);
            gameClient = minesweeperController.turn(gameTurnRequest);
            Assertions.assertEquals(FieldEnum.X.getValue(), gameClient.getField()[0][0]);
        }
    }

    @Test
    @Order(8)
    public void testGameTimeExpired() throws InterruptedException {
        final int height = 10;
        final int width = 10;
        final int minesCount = 10;
        final int col = 4;
        final int row = 4;
        final int timeSleepMilliseconds = 1000 * 60 * 2 + 1000;

        GameNewRequest gameRequest = new GameNewRequest();
        gameRequest.setHeight(height);
        gameRequest.setWidth(width);
        gameRequest.setMines_count(minesCount);
        GameClient gameClient = minesweeperController.createNewGame(gameRequest);

        Thread.sleep(timeSleepMilliseconds);

        GameTurnRequest gameTurnRequest = new GameTurnRequest();
        gameTurnRequest.setGame_id(gameClient.getGame_id());
        gameTurnRequest.setCol(col);
        gameTurnRequest.setRow(row);

        GameNotFoundException thrown = Assertions.assertThrows(
                GameNotFoundException.class, () ->
                        minesweeperController.turn(gameTurnRequest), "GameNotFoundException error was expected"
        );

        Assertions.assertEquals(String.format("игра с идентификатором %s не была создана или устарела (неактуальна)", gameClient.getGame_id())
                , thrown.getMessage());
    }

    @Test
    @Order(9)
    public void testInvalidTurn() {
        final int height = 10;
        final int width = 10;
        final int minesCount = 10;
        final int col = -1;
        final int row = -1;

        GameNewRequest gameRequest = new GameNewRequest();
        gameRequest.setHeight(height);
        gameRequest.setWidth(width);
        gameRequest.setMines_count(minesCount);
        GameClient gameClient = minesweeperController.createNewGame(gameRequest);

        GameTurnRequest gameTurnRequest = new GameTurnRequest();
        gameTurnRequest.setGame_id(gameClient.getGame_id());
        gameTurnRequest.setCol(col);
        gameTurnRequest.setRow(row);

        FieldCoordinatesException thrown = Assertions.assertThrows(
                FieldCoordinatesException.class, () ->
                        minesweeperController.turn(gameTurnRequest), "FieldCoordinatesException error was expected"
        );

        Assertions.assertEquals(String.format("выбранная клетка должна быть с координатамине " +
                        "не менее %d и не более %d по строкам и не менее %d и не более %d по столбцам",
                0, height - 1,
                0, width - 1), thrown.getMessage());
    }
}