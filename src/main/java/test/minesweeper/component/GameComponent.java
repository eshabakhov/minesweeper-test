package test.minesweeper.component;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import test.minesweeper.dto.GeneralGame;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
public class GameComponent {

    private ConcurrentHashMap<String, GeneralGame> gameHashMap;

    private static final int MINUTES_GAME_TO_DELETE = 2;

    public GameComponent() {
        this.gameHashMap = new ConcurrentHashMap<>();
    }

    public void addGame(GeneralGame generalGame) {
        gameHashMap.put(generalGame.getGameInfo().getGame_id(), generalGame);
    }

    public GeneralGame getGame(String gameId) {
        return gameHashMap.get(gameId);
    }

    public void deleteGame() {
        gameHashMap.values().removeIf(value ->
                ChronoUnit.MINUTES.between(value.getLocalDateTime(), LocalDateTime.now()) >= MINUTES_GAME_TO_DELETE);
    }
}
