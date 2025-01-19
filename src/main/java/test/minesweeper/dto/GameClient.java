package test.minesweeper.dto;

import lombok.Data;
import test.minesweeper.enums.FieldEnum;

@Data
public class GameClient {
    private final String game_id;
    private final Integer width;
    private final Integer height;
    private final Integer mines_count;
    private String[][] field;
    private Boolean completed;

    public GameClient(GameServer gameServer) {
        game_id = gameServer.getGame_id();
        width = gameServer.getWidth();
        height = gameServer.getHeight();
        mines_count = gameServer.getMinesCount();
        field = initFields();
        completed = false;
    }

    private String[][] initFields() {
        String[][] fieldEnums = new String[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                fieldEnums[i][j] = FieldEnum.EMPTY.getValue();
            }
        }
        return fieldEnums;
    }

    public boolean checkOpenField(int row, int col) {
        return !FieldEnum.EMPTY.getValue().equals(field[row][col]);
    }
}
