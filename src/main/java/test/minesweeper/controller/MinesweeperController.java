package test.minesweeper.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import test.minesweeper.dto.GameClient;
import test.minesweeper.dto.GameTurnRequest;
import test.minesweeper.dto.GameNewRequest;
import test.minesweeper.service.MinesweeperService;

@RestController
@RequestMapping(value = "/v1")
@AllArgsConstructor
public class MinesweeperController {
    private MinesweeperService minesweeperService;

    @PostMapping(value = "/new")
    public GameClient createNewGame(@RequestBody GameNewRequest newGameRequest) {
        return minesweeperService.createNewGame(newGameRequest);
    }

    @PostMapping(value = "/turn")
    public GameClient turn(@RequestBody GameTurnRequest gameTurnRequest) {
        return minesweeperService.turnGame(gameTurnRequest);
    }
}
