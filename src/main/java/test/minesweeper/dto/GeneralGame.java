package test.minesweeper.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GeneralGame {
    private GameInfo gameInfo;
    private GameInfoResponse gameInfoResponse;
    private LocalDateTime localDateTime;

    public GeneralGame (GameInfo gameInfo, GameInfoResponse gameInfoResponse) {
        this.gameInfo = gameInfo;
        this.gameInfoResponse = gameInfoResponse;
        this.localDateTime = LocalDateTime.now();
    }
}
