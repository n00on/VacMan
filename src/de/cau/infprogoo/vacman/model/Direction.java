package de.cau.infprogoo.vacman.model;

/**
 * Enumeration of directions.
 */
public enum Direction {
    UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0), STILL(0, 0);

    /**
     * x direction.
     */
    public final byte x;
    /**
     * y direction.
     */
    public final byte y;

    Direction(int x, int y) {
        this.x = (byte) x;
        this.y = (byte) y;
    }

    /**
     * @return an array with all Directions
     */
    public static Direction[] getArray() {
        return new Direction[]{UP, RIGHT, DOWN, LEFT};
    }

    /**
     * Checks if next step in this direction is over the edge.
     */
    public boolean mapCheck(Map map, byte y, byte x) {
        return this.x + x >= 0 && this.x + x < map.columns && this.y + y >= 0 && this.y + y < map.rows;
    }

    public byte nextX(Map map, byte x) {
        return (byte) ((x + this.x + map.columns) % map.columns);
    }

    public byte nextY(Map map, byte y) {
        return (byte) ((y + this.y + map.rows) % map.rows);
    }

    public Direction getOpposite() {
        switch (this) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case RIGHT:
                return LEFT;
            case LEFT:
                return RIGHT;
            default: // returns STILL for STILL
                return this;
        }
    }
}
