package test.minesweeper.dto;

import lombok.Data;

@Data
public class GameNewRequest {
    private Integer width;
    private Integer height;
    private Integer mines_count;
}
