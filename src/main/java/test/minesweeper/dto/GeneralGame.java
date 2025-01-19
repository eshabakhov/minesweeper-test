package test.minesweeper.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GeneralGame {
    private GameServer gameServer;
    private GameClient gameClient;
    private LocalDateTime localDateTime;

    public GeneralGame (GameServer gameServer, GameClient gameClient) {
        this.gameServer = gameServer;
        this.gameClient = gameClient;
        this.localDateTime = LocalDateTime.now();
    }
}
