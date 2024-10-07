package de.cau.infprogoo.vacman.model;

/**
 * Enumeration of field types.
 */
public enum Field {
    WALL(-2), GATE(-1), EMPTY(0), DOT(10), BONUS(50);

    /**
     * Field value for comparing and point score value.
     */
    public final byte value;

    Field(int value) {
        this.value = (byte) value;
    }
}
