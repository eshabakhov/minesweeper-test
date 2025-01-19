package test.minesweeper.dto;

import lombok.Data;

@Data
public class GameTurnRequest {
    private String game_id;
    private Integer col;
    private Integer row;
}
