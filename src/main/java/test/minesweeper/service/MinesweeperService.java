package test.minesweeper.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import test.minesweeper.component.GameComponent;
import test.minesweeper.dto.*;
import test.minesweeper.enums.FieldEnum;
import test.minesweeper.exception.FieldCoordinatesException;
import test.minesweeper.exception.FieldOpenException;
import test.minesweeper.exception.GameNotFoundException;
import test.minesweeper.exception.MinesCountException;

import java.util.Objects;

@Service
@AllArgsConstructor
public class MinesweeperService {
    private static final Logger logger = LoggerFactory.getLogger(MinesweeperService.class);

    private GameComponent gameComponent;

    public GameClient createNewGame(GameNewRequest newGameRequest) {
        int width = newGameRequest.getWidth();
        int height = newGameRequest.getHeight();
        int minesCount = newGameRequest.getMines_count();
        if (minesCount < 0 || minesCount > width * height - 1) {
            String message = String.format("количество мин должно быть не менее %d и не более %d", 1, width * height - 1);
            logger.warn(message);
            throw new MinesCountException(message);
        }
        GameServer gameServer = new GameServer(newGameRequest);
        GameClient gameClient = new GameClient(gameServer);
        gameComponent.addGame(new GeneralGame(gameServer, gameClient));

        String message = String.format("Создана игра с id %s", gameServer.getGame_id());
        logger.info(message);

        return gameClient;
    }

    public GameClient turnGame(GameTurnRequest gameTurnRequest) {
        String gameTurnRequestGameId = gameTurnRequest.getGame_id();
        Integer gameTurnRequestRow = gameTurnRequest.getRow();
        Integer gameTurnRequestCol = gameTurnRequest.getCol();

        GeneralGame generalGame = gameComponent.getGame(gameTurnRequestGameId);

        // проверка, что игра существует
        if (Objects.isNull(generalGame)) {
            String message = String.format("игра с идентификатором %s не была создана или устарела (неактуальна)", gameTurnRequestGameId);
            logger.warn(message);
            throw new GameNotFoundException(message);
        }
        GameServer gameServer = generalGame.getGameServer();
        GameClient gameClient = generalGame.getGameClient();

        // проверка, что указанные координаты корректные
        if (!checkValidFieldCoordinates(gameServer,
                gameTurnRequestRow,
                gameTurnRequestCol)) {
            String message = String.format("Игра с id %s: выбранная клетка должна быть с координатамине " +
                            "не менее %d и не более %d по строкам и не менее %d и не более %d по столбцам",
                    gameTurnRequestGameId,
                    0, gameServer.getHeight() - 1,
                    0, gameServer.getWidth() - 1);
            logger.warn(message);
            throw new FieldCoordinatesException(message);
        }
        // проверка на первый ход
        if (gameServer.isZeroFields()) {
            gameServer.initFirstTurn(gameTurnRequestRow, gameTurnRequestCol);
        }
        // проверка на повторный вызов ячейки
        if (gameClient.checkOpenField(gameTurnRequestRow, gameTurnRequestCol)){
            String message = String.format("Игра с id %s: уже открытая ячейка", gameTurnRequestGameId);
            logger.warn(message);
            throw new FieldOpenException(message);
        }
        // проверка, что игра програна
        if (checkLostGame(gameServer, gameTurnRequest.getRow(), gameTurnRequest.getCol())) {
            String message = String.format("Игра с id %s: завершена проигрышем", gameTurnRequestGameId);
            logger.info(message);
            lostGame(generalGame);
        } else {
            repaintCells(generalGame, gameTurnRequest.getRow(), gameTurnRequest.getCol());
            // проверка, что игра выиграна
            if (checkWinGame(generalGame)) {
                String message = String.format("Игра с id %s: завершена выигрышем", gameTurnRequestGameId);
                logger.info(message);
                winGame(generalGame);
            }
        }
        return generalGame.getGameClient();
    }

    private boolean checkValidFieldCoordinates(GameServer gameServer, Integer row, Integer col) {
        return row >= 0 && col >= 0 && row < gameServer.getHeight() && col < gameServer.getWidth();
    }

    private boolean checkLostGame(GameServer gameServer, Integer row, Integer col) {
        return FieldEnum.X.getValue().equals(gameServer.getField()[row][col]);
    }

    private void lostGame(GeneralGame generalGame) {
        GameServer gameServer = generalGame.getGameServer();
        GameClient gameClient = generalGame.getGameClient();
        for (int i = 0; i < gameServer.getHeight(); i++) {
            if (gameServer.getWidth() >= 0)
                System.arraycopy(gameServer.getField()[i], 0, gameClient.getField()[i], 0, gameServer.getWidth());
        }
        gameClient.setCompleted(true);
    }

    private void repaintCells(GeneralGame generalGame, Integer row, Integer col) {
        GameServer gameServer = generalGame.getGameServer();
        GameClient gameClient = generalGame.getGameClient();
        String[][] serverFields = gameServer.getField();
        String[][] clientFields = gameClient.getField();

        if (!FieldEnum.ZERO.getValue().equals(serverFields[row][col])) {
            clientFields[row][col] = serverFields[row][col];
        } else {
            paintZeroFields(serverFields, clientFields, gameServer.getHeight(), gameServer.getWidth(), row, col);
        }
    }

    private void paintZeroFields(String[][] serverFields, String[][] clientFields, int height, int width, int row, int col) {
        if (row >= 0 && col >= 0 && row < height && col < width) {
            if (FieldEnum.ZERO.getValue().equals(serverFields[row][col]) &&
                    !Objects.equals(clientFields[row][col], serverFields[row][col])) {

                clientFields[row][col] = serverFields[row][col];

                paintZeroFields(serverFields, clientFields, height, width,row - 1, col - 1);
                paintZeroFields(serverFields, clientFields, height, width,row - 1, col);
                paintZeroFields(serverFields, clientFields, height, width,row - 1, col + 1);

                paintZeroFields(serverFields, clientFields, height, width, row, col - 1);
                paintZeroFields(serverFields, clientFields, height, width, row, col);
                paintZeroFields(serverFields, clientFields, height, width, row, col + 1);

                paintZeroFields(serverFields, clientFields, height, width,row + 1, col - 1);
                paintZeroFields(serverFields, clientFields, height, width,row + 1, col);
                paintZeroFields(serverFields, clientFields, height, width,row + 1, col + 1);
            }  else {
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx != 0 || dy != 0) {
                            int nx = row + dx, ny = col + dy;
                            if (nx >= 0 && ny >= 0 && nx < height && ny < width) {
                                switch (serverFields[nx][ny]) {
                                    case "1":
                                    case "2":
                                    case "3":
                                    case "4":
                                    case "5":
                                    case "6":
                                    case "7":
                                    case "8":
                                        clientFields[nx][ny] = serverFields[nx][ny];
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean checkWinGame(GeneralGame generalGame) {
        GameServer gameServer = generalGame.getGameServer();
        GameClient gameClient = generalGame.getGameClient();
        String[][] serverFields = gameServer.getField();
        String[][] clientFields = gameClient.getField();
        boolean t = true;

        for (int i = 0; i < gameServer.getHeight() && t; i++) {
            for (int j = 0; j < gameServer.getWidth(); j++) {
                if (FieldEnum.EMPTY.getValue().equals(clientFields[i][j]) && !FieldEnum.X.getValue().equals(serverFields[i][j])) {
                    t = false;
                    break;
                }
            }
        }

        return t;
    }

    private void winGame(GeneralGame generalGame) {
        GameServer gameServer = generalGame.getGameServer();
        GameClient gameClient = generalGame.getGameClient();
        String[][] serverFields = gameServer.getField();
        String[][] clientFields = gameClient.getField();

        for (int i = 0; i < gameServer.getHeight(); i++) {
            for (int j = 0; j < gameServer.getWidth(); j++) {
                clientFields[i][j] = serverFields[i][j];
                if (FieldEnum.X.getValue().equals(clientFields[i][j])) {
                    clientFields[i][j] = FieldEnum.M.getValue();
                }
            }
        }

        gameClient.setCompleted(true);
    }
}
