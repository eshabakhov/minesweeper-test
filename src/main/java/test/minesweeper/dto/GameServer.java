package test.minesweeper.dto;

import lombok.Data;
import test.minesweeper.enums.FieldEnum;

import java.util.Random;
import java.util.UUID;

@Data
public class GameServer {
    private final String game_id;
    private final Integer width;
    private final Integer height;
    private final Integer minesCount;
    private final String[][] field;
    private Boolean completed;

    public GameServer(GameNewRequest newGameRequest) {
        game_id = UUID.randomUUID().toString();
        width = newGameRequest.getWidth();
        height = newGameRequest.getHeight();
        minesCount = newGameRequest.getMines_count();
        field = initFields(height, width);
    }

    private String[][] initFields(Integer height, Integer width) {
        String [][] fieldEnums = new String[height][width];
        initDefaultFields(fieldEnums);
        return fieldEnums;
    }

    private void initDefaultFields(String[][] fieldEnums) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                fieldEnums[i][j] = FieldEnum.ZERO.getValue();
            }
        }
    }

    public boolean isZeroFields() {
        boolean t = true;
        for (int i = 0; i < height && t; i++) {
            for (int j = 0; j < width; j++) {
                if (!FieldEnum.ZERO.getValue().equals(field[i][j])) {
                    t = false;
                    break;
                }
            }
        }
        return t;
    }

    public void initFirstTurn(int row, int col) {
        initBombs(field, minesCount, row, col);
        reInitFields(field);
    }

    private void initBombs(String[][] fieldEnums, Integer minesCount, int row, int col) {
        Random random = new Random();
        int k = (int) Math.round(Math.sqrt(height * width));
        while(minesCount > 0) {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (random.nextInt(k) == 0 && !FieldEnum.X.getValue().equals(fieldEnums[i][j]) && minesCount > 0
                    && (i != row || j != col)) {
                        fieldEnums[i][j] = FieldEnum.X.getValue();
                        minesCount--;
                    }
                }
            }
            if (minesCount > 0 && k > 0) {
                k--;
            }
        }
    }

    private void reInitFields(String[][] fieldEnums) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!FieldEnum.X.getValue().equals(fieldEnums[i][j])) {
                    reInitField(fieldEnums, i, j);
                }
            }
        }
    }

    private void reInitField(String[][] fieldEnums, int row, int col) {
        int minesCount = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx != 0 || dy != 0) {
                    int nx = row + dx, ny = col + dy;
                    if (nx >= 0 && ny >= 0 && nx < height && ny < width) {
                        if (FieldEnum.X.getValue().equals(fieldEnums[nx][ny])) {
                            minesCount++;
                        }
                    }
                }
            }
        }
        fieldEnums[row][col] = String.valueOf(minesCount);
    }
}
