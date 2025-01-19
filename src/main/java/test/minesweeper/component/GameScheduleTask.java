package test.minesweeper.component;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GameScheduleTask {
    private final GameComponent gameComponent;

    public GameScheduleTask(GameComponent gameComponent) {
        this.gameComponent = gameComponent;
    }

    @Scheduled(fixedRate = 1000)
    public void deleteGame() {
        gameComponent.deleteGame();
    }
}
