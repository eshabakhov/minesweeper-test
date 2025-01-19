package test.minesweeper.enums;

public enum FieldEnum {
    EMPTY(" "), // Это может быть для пустого значения (если нужно)
    ZERO("0"),
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    M("M"), // Символ "M"
    X("X");  // Символ "X"

    private String value;

    // Конструктор для числовых значений
    FieldEnum() {
        // Для M и X оставляем null, так как они не имеют числового значения
        this.value = null;
    }

    // Конструктор для числовых значений
    FieldEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
