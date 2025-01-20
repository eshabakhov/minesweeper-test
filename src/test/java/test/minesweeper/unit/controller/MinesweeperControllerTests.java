package test.minesweeper.unit.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import test.minesweeper.controller.MinesweeperController;
import test.minesweeper.dto.*;
import test.minesweeper.enums.FieldEnum;
import test.minesweeper.exception.FieldCoordinatesException;
import test.minesweeper.exception.FieldOpenException;
import test.minesweeper.exception.GameNotFoundException;
import test.minesweeper.exception.MinesCountException;
import test.minesweeper.service.MinesweeperService;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(MinesweeperController.class)
public class MinesweeperControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MinesweeperService minesweeperService;

    @Test
    public void minesweeperNewShouldReturnMessageFromService() throws Exception {
        final int HEIGHT = 10;
        final int WIDTH = 10;
        final int MINES_COUNT = 10;
        final boolean COMPLETED = false;
        GameNewRequest gameRequest = new GameNewRequest();
        gameRequest.setHeight(HEIGHT);
        gameRequest.setWidth(WIDTH);
        gameRequest.setMines_count(MINES_COUNT);

        GameInfoResponse gameInfoResponse = new GameInfoResponse(new GameInfo(gameRequest));
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(gameRequest);

        when(minesweeperService.createNewGame(gameRequest)).thenReturn(gameInfoResponse);
        this.mockMvc.perform(post("/v1/new")
                .contentType("application/json")
                .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.game_id").value(gameInfoResponse.getGame_id()))
                .andExpect(jsonPath("$.width").value(MINES_COUNT))
                .andExpect(jsonPath("$.height").value(WIDTH))
                .andExpect(jsonPath("$.mines_count").value(MINES_COUNT))
                .andExpect(jsonPath("$.field").isNotEmpty())
                .andExpect(jsonPath("$.completed").value(COMPLETED));
    }

    @Test
    public void minesweeperNewShouldReturnMinesCountExceptionFromService() throws Exception {
        final int HEIGHT = 10;
        final int WIDTH = 10;
        final int MINES_COUNT = -10;
        GameNewRequest gameRequest = new GameNewRequest();
        gameRequest.setHeight(HEIGHT);
        gameRequest.setWidth(WIDTH);
        gameRequest.setMines_count(MINES_COUNT);

        MinesCountException minesCountException = new MinesCountException(HEIGHT, WIDTH);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(gameRequest);

        when(minesweeperService.createNewGame(gameRequest)).thenThrow(minesCountException);
        this.mockMvc.perform(post("/v1/new")
                        .contentType("application/json")
                        .content(jsonContent))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.error").value(
                                String.format("количество мин должно быть не менее %d и не более %d", 1, HEIGHT * WIDTH - 1)));
    }

    @Test
    public void minesweeperTurnShouldReturnMessageFromService() throws Exception {
        final int HEIGHT = 10;
        final int WIDTH = 10;
        final int MINES_COUNT = 10;
        final int COL = 4;
        final int ROW = 4;
        final boolean COMPLETED = false;

        GameNewRequest gameRequest = new GameNewRequest();
        gameRequest.setHeight(HEIGHT);
        gameRequest.setWidth(WIDTH);
        gameRequest.setMines_count(MINES_COUNT);

        GameInfoResponse gameInfoResponse = new GameInfoResponse(new GameInfo(gameRequest));
        gameInfoResponse.getField()[ROW][COL] = FieldEnum.ONE.getValue();

        GameTurnRequest gameTurnRequest = new GameTurnRequest();
        gameTurnRequest.setGame_id(UUID.randomUUID().toString());
        gameTurnRequest.setCol(COL);
        gameTurnRequest.setRow(ROW);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(gameTurnRequest);

        when(minesweeperService.turnGame(gameTurnRequest)).thenReturn(gameInfoResponse);
        this.mockMvc.perform(post("/v1/turn")
                        .contentType("application/json")
                        .content(jsonContent))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.game_id").value(gameInfoResponse.getGame_id()))
                        .andExpect(jsonPath("$.width").value(MINES_COUNT))
                        .andExpect(jsonPath("$.height").value(WIDTH))
                        .andExpect(jsonPath("$.mines_count").value(MINES_COUNT))
                        .andExpect(jsonPath(String.format("$.field[%d][%d])", ROW, COL)).value(FieldEnum.ONE.getValue()))
                        .andExpect(jsonPath("$.completed").value(COMPLETED));

    }

    @Test
    public void minesweeperTurnShouldReturnGameNotFoundExceptionFromService() throws Exception {
        final int COL = 4;
        final int ROW = 4;

        GameTurnRequest gameTurnRequest = new GameTurnRequest();
        gameTurnRequest.setGame_id(UUID.randomUUID().toString());
        gameTurnRequest.setCol(COL);
        gameTurnRequest.setRow(ROW);

        GameNotFoundException gameNotFoundException = new GameNotFoundException(gameTurnRequest.getGame_id());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(gameTurnRequest);

        when(minesweeperService.turnGame(gameTurnRequest)).thenThrow(gameNotFoundException);
        this.mockMvc.perform(post("/v1/turn")
                        .contentType("application/json")
                        .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        String.format("игра с идентификатором %s не была создана или устарела (неактуальна)", gameTurnRequest.getGame_id())));
    }

    @Test
    public void minesweeperTurnShouldReturnFieldOpenExceptionFromService() throws Exception {
        final int COL = 4;
        final int ROW = 4;

        GameTurnRequest gameTurnRequest = new GameTurnRequest();
        gameTurnRequest.setGame_id(UUID.randomUUID().toString());
        gameTurnRequest.setCol(COL);
        gameTurnRequest.setRow(ROW);

        FieldOpenException fieldOpenException = new FieldOpenException();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(gameTurnRequest);

        when(minesweeperService.turnGame(gameTurnRequest)).thenThrow(fieldOpenException);
        this.mockMvc.perform(post("/v1/turn")
                        .contentType("application/json")
                        .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("уже открытая ячейка"));
    }

    @Test
    public void minesweeperTurnShouldReturnFieldCoordinatesExceptionFromService() throws Exception {
        final int HEIGHT = 10;
        final int WIDTH = 10;
        final int COL = 4;
        final int ROW = 4;

        GameTurnRequest gameTurnRequest = new GameTurnRequest();
        gameTurnRequest.setGame_id(UUID.randomUUID().toString());
        gameTurnRequest.setCol(COL);
        gameTurnRequest.setRow(ROW);

        FieldCoordinatesException fieldCoordinatesException = new FieldCoordinatesException(HEIGHT, WIDTH);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(gameTurnRequest);

        when(minesweeperService.turnGame(gameTurnRequest)).thenThrow(fieldCoordinatesException);
        this.mockMvc.perform(post("/v1/turn")
                        .contentType("application/json")
                        .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(String.format("выбранная клетка должна быть с координатамине " +
                                "не менее %d и не более %d по строкам и не менее %d и не более %d по столбцам",
                        0, HEIGHT - 1,
                        0, WIDTH - 1)));
    }
}