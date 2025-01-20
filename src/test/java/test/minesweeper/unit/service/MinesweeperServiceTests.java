package test.minesweeper.unit.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import test.minesweeper.component.GameComponent;
import test.minesweeper.dto.GameInfo;
import test.minesweeper.dto.GameInfoResponse;
import test.minesweeper.dto.GameNewRequest;
import test.minesweeper.dto.GeneralGame;
import test.minesweeper.service.MinesweeperService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest(MinesweeperService.class)
public class MinesweeperServiceTests {
    @MockitoBean
    GameComponent gameComponent;
    @Test
    public void minesweeperCreateNewGameShouldReturnMessageFromService() throws Exception {
        GameComponent gameComponent = spy(GameComponent.class);
        final int HEIGHT = 10;
        final int WIDTH = 10;
        final int MINES_COUNT = 10;
        GameNewRequest gameRequest = new GameNewRequest();
        gameRequest.setHeight(HEIGHT);
        gameRequest.setWidth(WIDTH);
        gameRequest.setMines_count(MINES_COUNT);
        GameInfo gameInfo = new GameInfo(gameRequest);
        GameInfoResponse gameInfoResponse = new GameInfoResponse(gameInfo);
        GeneralGame generalGame = new GeneralGame(gameInfo, gameInfoResponse);

        doCallRealMethod().when(gameComponent).addGame(any(GeneralGame.class));
        gameComponent.addGame(generalGame);
        verify(gameComponent, times(1)).addGame(generalGame);
    }
    @Test
    public void minesweeperGetGameShouldReturnMessageFromService() throws Exception {
        GameComponent gameComponent = spy(GameComponent.class);
        final int HEIGHT = 10;
        final int WIDTH = 10;
        final int MINES_COUNT = 10;
        GameNewRequest gameRequest = new GameNewRequest();
        gameRequest.setHeight(HEIGHT);
        gameRequest.setWidth(WIDTH);
        gameRequest.setMines_count(MINES_COUNT);
        GameInfo gameInfo = new GameInfo(gameRequest);
        GameInfoResponse gameInfoResponse = new GameInfoResponse(gameInfo);
        GeneralGame generalGame = new GeneralGame(gameInfo, gameInfoResponse);
        gameComponent.addGame(generalGame);

        doCallRealMethod().when(gameComponent).getGame(any(String.class));
        gameComponent.getGame(generalGame.getGameInfo().getGame_id());
        verify(gameComponent, times(1)).getGame(generalGame.getGameInfo().getGame_id());
    }
    @Test
    public void minesweeperDeleteNewGameShouldReturnMessageFromService() throws Exception {
        GameComponent gameComponent = spy(GameComponent.class);
        final int HEIGHT = 10;
        final int WIDTH = 10;
        final int MINES_COUNT = 10;
        GameNewRequest gameRequest = new GameNewRequest();
        gameRequest.setHeight(HEIGHT);
        gameRequest.setWidth(WIDTH);
        gameRequest.setMines_count(MINES_COUNT);
        GameInfo gameInfo = new GameInfo(gameRequest);
        GameInfoResponse gameInfoResponse = new GameInfoResponse(gameInfo);
        GeneralGame generalGame = new GeneralGame(gameInfo, gameInfoResponse);
        generalGame.setLocalDateTime(LocalDateTime.MIN);
        gameComponent.addGame(generalGame);

        doCallRealMethod().when(gameComponent).deleteGame();
        gameComponent.deleteGame();
        verify(gameComponent, times(1)).deleteGame();
    }
}
