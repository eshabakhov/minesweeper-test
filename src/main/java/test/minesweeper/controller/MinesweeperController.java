package test.minesweeper.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import test.minesweeper.dto.ErrorResponse;
import test.minesweeper.dto.GameInfoResponse;
import test.minesweeper.dto.GameTurnRequest;
import test.minesweeper.dto.GameNewRequest;
import test.minesweeper.service.MinesweeperService;

@RestController
@RequestMapping(value = "/v1")
@AllArgsConstructor
public class MinesweeperController {
    private final MinesweeperService minesweeperService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Ошибка запроса или некорректное действие", content =
                    { @Content(mediaType = "application/json", schema =
                      @Schema(implementation = ErrorResponse.class)) }) })
    @PostMapping(value = "/new")
    public GameInfoResponse createNewGame(@RequestBody @Valid GameNewRequest newGameRequest) {
        return minesweeperService.createNewGame(newGameRequest);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Ошибка запроса или некорректное действие", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ErrorResponse.class)) }) })
    @PostMapping(value = "/turn")
    public GameInfoResponse turn(@RequestBody @Valid GameTurnRequest gameTurnRequest) {
        return minesweeperService.turnGame(gameTurnRequest);
    }
}
