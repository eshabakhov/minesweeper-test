package test.minesweeper.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import test.minesweeper.component.GameComponent;
import test.minesweeper.dto.*;
import test.minesweeper.enums.FieldEnum;
import test.minesweeper.exception.FieldCoordinatesException;
import test.minesweeper.exception.FieldOpenException;
import test.minesweeper.exception.GameNotFoundException;
import test.minesweeper.exception.MinesCountException;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class MinesweeperService {
    private final GameComponent gameComponent;

    public GameInfoResponse createNewGame(GameNewRequest newGameRequest) {
        int width = newGameRequest.getWidth();
        int height = newGameRequest.getHeight();
        int minesCount = newGameRequest.getMines_count();
        if (width < 1 || height < 1 || minesCount < 0 || minesCount > width * height - 1) {
            throw new MinesCountException(height, width);
        }
        GameInfo gameInfo = new GameInfo(newGameRequest);
        GameInfoResponse gameInfoResponse = new GameInfoResponse(gameInfo);
        gameComponent.addGame(new GeneralGame(gameInfo, gameInfoResponse));

        String message = String.format("Создана игра с id %s", gameInfo.getGame_id());
        log.info(message);

        return gameInfoResponse;
    }

    public GameInfoResponse turnGame(GameTurnRequest gameTurnRequest) {
        String gameTurnRequestGameId = gameTurnRequest.getGame_id();
        Integer gameTurnRequestRow = gameTurnRequest.getRow();
        Integer gameTurnRequestCol = gameTurnRequest.getCol();

        GeneralGame generalGame = gameComponent.getGame(gameTurnRequestGameId);

        // проверка, что игра существует
        if (Objects.isNull(generalGame)) {
            throw new GameNotFoundException(gameTurnRequestGameId);
        }
        GameInfo gameInfo = generalGame.getGameInfo();
        GameInfoResponse gameInfoResponse = generalGame.getGameInfoResponse();

        // проверка, что указанные координаты корректные
        if (!checkValidFieldCoordinates(gameInfo,
                gameTurnRequestRow,
                gameTurnRequestCol)) {
            throw new FieldCoordinatesException(gameInfo.getHeight(), gameInfo.getWidth());
        }
        // проверка на первый ход
        if (gameInfo.isZeroFields()) {
            gameInfo.initFirstTurn(gameTurnRequestRow, gameTurnRequestCol);
        }
        // проверка на повторный вызов ячейки
        if (gameInfoResponse.checkOpenField(gameTurnRequestRow, gameTurnRequestCol)){
            throw new FieldOpenException();
        }
        // проверка, что игра програна
        if (checkLostGame(gameInfo, gameTurnRequest.getRow(), gameTurnRequest.getCol())) {
            log.info(String.format("Игра с id %s: завершена проигрышем", gameTurnRequestGameId));
            lostGame(generalGame);
        } else {
            repaintCells(generalGame, gameTurnRequest.getRow(), gameTurnRequest.getCol());
            // проверка, что игра выиграна
            if (checkWinGame(generalGame)) {
                log.info(String.format("Игра с id %s: завершена выигрышем", gameTurnRequestGameId));
                winGame(generalGame);
            }
        }

        generalGame.setLocalDateTime(LocalDateTime.now());
        return gameInfoResponse;
    }

    private boolean checkValidFieldCoordinates(GameInfo gameServer, Integer row, Integer col) {
        return row >= 0 && col >= 0 && row < gameServer.getHeight() && col < gameServer.getWidth();
    }

    private boolean checkLostGame(GameInfo gameServer, Integer row, Integer col) {
        return FieldEnum.X.equals(gameServer.getField()[row][col]);
    }

    private void lostGame(GeneralGame generalGame) {
        GameInfo gameInfo = generalGame.getGameInfo();
        GameInfoResponse gameInfoResponse = generalGame.getGameInfoResponse();
        for (int i = 0; i < gameInfo.getHeight(); i++) {
            for (int j = 0; j < gameInfoResponse.getWidth(); j++) {
                gameInfoResponse.getField()[i][j] = gameInfo.getField()[i][j].getValue();
            }
        }
        gameInfoResponse.setCompleted(true);
    }

    private void repaintCells(GeneralGame generalGame, Integer row, Integer col) {
        GameInfo gameInfo = generalGame.getGameInfo();
        GameInfoResponse gameInfoResponse = generalGame.getGameInfoResponse();
        FieldEnum[][] serverFields = gameInfo.getField();
        String[][] clientFields = gameInfoResponse.getField();

        if (!FieldEnum.ZERO.equals(serverFields[row][col])) {
            clientFields[row][col] = serverFields[row][col].getValue();
        } else {
            paintZeroFields(serverFields, clientFields, gameInfo.getHeight(), gameInfo.getWidth(), row, col);
        }
    }

    private void paintZeroFields(FieldEnum[][] serverFields, String[][] clientFields, int height, int width, int row, int col) {
        if (row < 0 || col < 0 || row >= height || col >= width) {
            return;
        }
        if (FieldEnum.ZERO.equals(serverFields[row][col]) &&
                !Objects.equals(clientFields[row][col], serverFields[row][col].getValue())) {

            clientFields[row][col] = serverFields[row][col].getValue();

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    paintZeroFields(serverFields, clientFields, height, width, row + dx, col + dy);
                }
            }

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx != 0 || dy != 0) {
                        int nx = row + dx, ny = col + dy;
                        if (nx >= 0 && ny >= 0 && nx < height && ny < width) {
                            if (!FieldEnum.EMPTY.equals(serverFields[nx][ny]) &&
                                    !FieldEnum.X.equals(serverFields[nx][ny]) &&
                                    !FieldEnum.M.equals(serverFields[nx][ny]) &&
                                    !FieldEnum.ZERO.equals(serverFields[nx][ny]))
                                clientFields[nx][ny] = serverFields[nx][ny].getValue();
                        }
                    }
                }
            }
        }
    }

    private boolean checkWinGame(GeneralGame generalGame) {
        GameInfo gameInfo = generalGame.getGameInfo();
        GameInfoResponse gameInfoResponse = generalGame.getGameInfoResponse();
        FieldEnum[][] serverFields = gameInfo.getField();
        String[][] clientFields = gameInfoResponse.getField();
        boolean t = true;

        // можно перейти на счетчики, чтобы не итерироваться по массиву
        for (int i = 0; i < gameInfo.getHeight() && t; i++) {
            for (int j = 0; j < gameInfo.getWidth(); j++) {
                if (FieldEnum.EMPTY.getValue().equals(clientFields[i][j]) && !FieldEnum.X.equals(serverFields[i][j])) {
                    t = false;
                    break;
                }
            }
        }

        return t;
    }

    private void winGame(GeneralGame generalGame) {
        GameInfo gameInfo = generalGame.getGameInfo();
        GameInfoResponse gameInfoResponse = generalGame.getGameInfoResponse();
        FieldEnum[][] serverFields = gameInfo.getField();
        String[][] clientFields = gameInfoResponse.getField();

        for (int i = 0; i < gameInfo.getHeight(); i++) {
            for (int j = 0; j < gameInfo.getWidth(); j++) {
                clientFields[i][j] = serverFields[i][j].getValue();
                if (FieldEnum.X.getValue().equals(clientFields[i][j])) {
                    clientFields[i][j] = FieldEnum.M.getValue();
                }
            }
        }

        gameInfoResponse.setCompleted(true);
    }
}
