package test.minesweeper.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GameTurnRequest {
    @NotNull
    private String game_id;
    @NotNull
    private Integer col;
    @NotNull
    private Integer row;
}
