package test.minesweeper.enums;

import lombok.Getter;

@Getter
public enum FieldEnum {
    EMPTY(" "),
    ZERO("0"),
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    M("M"),
    X("X");

    private final String value;

    FieldEnum() {
        this.value = null;
    }

    FieldEnum(String value) {
        this.value = value;
    }

}
