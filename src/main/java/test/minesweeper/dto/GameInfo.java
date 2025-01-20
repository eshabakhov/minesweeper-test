package test.minesweeper.dto;

import lombok.Data;
import test.minesweeper.enums.FieldEnum;

import java.util.Random;
import java.util.UUID;

@Data
public class GameInfo {
    private final String game_id;
    private final Integer width;
    private final Integer height;
    private final Integer minesCount;
    private final FieldEnum[][] field;
    private Boolean completed;

    public GameInfo(GameNewRequest newGameRequest) {
        game_id = UUID.randomUUID().toString();
        width = newGameRequest.getWidth();
        height = newGameRequest.getHeight();
        minesCount = newGameRequest.getMines_count();
        field = initFields(height, width);
    }

    private FieldEnum[][] initFields(Integer height, Integer width) {
        FieldEnum [][] fieldEnums = new FieldEnum[height][width];
        initDefaultFields(fieldEnums);
        return fieldEnums;
    }

    private void initDefaultFields(FieldEnum[][] fieldEnums) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                fieldEnums[i][j] = FieldEnum.ZERO;
            }
        }
    }

    public boolean isZeroFields() {
        boolean t = true;
        for (int i = 0; i < height && t; i++) {
            for (int j = 0; j < width; j++) {
                if (!FieldEnum.ZERO.equals(field[i][j])) {
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

    private void initBombs(FieldEnum[][] fieldEnums, Integer minesCount, int row, int col) {
        Random random = new Random();
        int k = (int) Math.round(Math.sqrt(height * width));
        while(minesCount > 0) {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (random.nextInt(k) == 0 && !FieldEnum.X.equals(fieldEnums[i][j]) && minesCount > 0
                    && (i != row || j != col)) {
                        fieldEnums[i][j] = FieldEnum.X;
                        minesCount--;
                    }
                }
            }
            if (minesCount > 0 && k > 0) {
                k--;
            }
        }
    }

    private void reInitFields(FieldEnum[][] fieldEnums) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!FieldEnum.X.equals(fieldEnums[i][j])) {
                    reInitField(fieldEnums, i, j);
                }
            }
        }
    }

    private void reInitField(FieldEnum[][] fieldEnums, int row, int col) {
        int minesCount = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx != 0 || dy != 0) {
                    int nx = row + dx, ny = col + dy;
                    if (nx >= 0 && ny >= 0 && nx < height && ny < width) {
                        if (FieldEnum.X.equals(fieldEnums[nx][ny])) {
                            minesCount++;
                        }
                    }
                }
            }
        }
        switch (minesCount) {
            case 0:
                fieldEnums[row][col] = FieldEnum.ZERO;
                break;
            case 1:
                fieldEnums[row][col] = FieldEnum.ONE;
                break;
            case 2:
                fieldEnums[row][col] = FieldEnum.TWO;
                break;
            case 3:
                fieldEnums[row][col] = FieldEnum.THREE;
                break;
            case 4:
                fieldEnums[row][col] = FieldEnum.FOUR;
                break;
            case 5:
                fieldEnums[row][col] = FieldEnum.FIVE;
                break;
            case 6:
                fieldEnums[row][col] = FieldEnum.SIX;
                break;
            case 7:
                fieldEnums[row][col] = FieldEnum.SEVEN;
                break;
            case 8:
                fieldEnums[row][col] = FieldEnum.EIGHT;
                break;
            default:
                break;
        }

    }
}
