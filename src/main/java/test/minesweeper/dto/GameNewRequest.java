package test.minesweeper.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GameNewRequest {
    @NotNull
    private Integer width;
    @NotNull
    private Integer height;
    @NotNull
    private Integer mines_count;
}
