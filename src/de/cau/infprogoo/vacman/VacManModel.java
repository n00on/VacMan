package de.cau.infprogoo.vacman;

import java.util.ArrayList;

import acm.util.JTFTools;
import acm.util.RandomGenerator;

class VacManModel {

    /**
     * MVC Views implementing the VMView interface.
     */
    private ArrayList<VMView> views = new ArrayList<>();

    /**
     * Entities and Map.
     */
    private Vac vacMan;
    private Virus[] virus;
    private Map map;

    /**
     * Booleans determining update behavior.
     */
    private boolean paused = false;
    private boolean resetPositions = false;
    private boolean newLevel = false;
    private boolean resetGame = false;

    private int msPerUpdate = 220;
    private int level = 1;
    private int score = 0;

    public VacManModel() {
        initMap();
        vacMan = new Vac(this);
        this.virus = new Virus[]{new FollowVirus(this), new PredictVirus(this), new BlueVirus(this),
                new DistanceVirus(this)};
    }

    /**
     * Game Loop.
     */
    void run() {
        while (true) {
            if (paused) {
                JTFTools.pause(msPerUpdate);
            } else {
                double startTime = System.nanoTime() / 1e6;
                update();
                for (VMView view : views) {
                    view.update(startTime + msPerUpdate - System.nanoTime() / 1e6);
                }
            }
        }
    }

    ArrayList<VMView> getViews() {
        return views;
    }

    void addView(VMView view) {
        views.add(view);
        view.draw();
    }

    Vac getVacMan() {
        return vacMan;
    }

    Virus[] getVirus() {
        return virus;
    }

    int getScore() {
        return score;
    }

    int getLevel() {
        return level;
    }

    void pause() {
        paused = !paused;
    }

    Map getMap() {
        return map;
    }

    /**
     * Reset vac man and viruses in next update
     */
    void resetPositions() {
        resetPositions = true;
    }

    void resetGame() {
        paused = false;
        resetGame = true;
    }

    void addScore(int scorePlus) {
        score += scorePlus;
        if (scorePlus > 100) {
            System.out.println(scorePlus);
        }
    }

    /**
     * Updates the entire game state.
     */
    void update() {

        if (resetPositions || newLevel || resetGame) {
            reset();
            return;
        }

        vacMan.update();
        Virus.updateAll(virus);
        map.update(vacMan.getX(), vacMan.getY());

        if (map.getDotCounter() == 0) {
            newLevel = true;
        }
    }

    private void reset() {
        vacMan.reset();
        Virus.resetAll(virus);

        if (newLevel) {
            level++;
            msPerUpdate -= msPerUpdate / 10;
            Virus.decreaseFrightTime();
            initMap();
            for (VMView view : views) {
                view.draw();
            }
        } else if (resetGame) {
            level = 1;
            msPerUpdate = 200;
            Virus.resetFrightTime();
            initMap();
            score = 0;
            vacMan = new Vac(this);
            for (VMView view : views) {
                view.reset();
            }
        } else {
            for (VMView view : views) {
                view.drawEntities();
            }
        }

        resetPositions = false;
        newLevel = false;
        resetGame = false;
    }

    /**
     * Initiates standard map.
     */
    private void initMap() {
        this.map = Map.getMap(this);
    }

    /**
     * Game Over map.
     */
    void gameOver() {
        resetPositions = true;
        paused = true;

        byte[] virusXStart = {13, 14};
        this.map = new Map(this, Map.getGameOver(), 13, 11, virusXStart, 6, 4);
        for (VMView view : views) {
            view.drawFields(this.map);
        }
    }
}

class Map {

    final int rows;
    final int columns;

    private final VacManModel model;
    private final Field[][] map;

    final byte vacXStart;
    final byte vacYStart;
    private final byte[] virusXStart;
    final byte virusYStart;
    final byte virusYBack;

    private int dotCounter = 0;

    int getDotCounter() {
        return dotCounter;
    }

    Field get(byte x, byte y) {
        return map[(y + rows) % rows][(x + columns) % columns];
    }

    byte getVirusStartX() {
        return virusXStart[RandomGenerator.getInstance().nextInt(virusXStart.length)];
    }

    Map(VacManModel model, Field[][] map, int vacXStart, int vacYStart, byte[] virusXStart, int virusYStart, int virusYBack) {
        this.model = model;
        this.map = map;
        this.vacXStart = (byte) vacXStart;
        this.vacYStart = (byte) vacYStart;
        this.virusXStart = virusXStart;
        this.virusYStart = (byte) virusYStart;
        this.virusYBack = (byte) virusYBack;

        rows = map.length;
        columns = map[0].length;

        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                if (map[y][x].value >= Field.DOT.value) {
                    dotCounter++;
                }
            }
        }
    }

    void update(byte x, byte y) {

        if (map[y][x].value >= Field.DOT.value) {

            dotCounter--;

            model.addScore(map[y][x].value);

            if (map[y][x] == Field.BONUS) {
                Virus.frighten();
            }

            map[y][x] = Field.EMPTY;
        }
    }

    private static Field w = Field.WALL;
    private static Field d = Field.DOT;
    private static Field g = Field.GATE;
    private static Field b = Field.BONUS;
    private static Field e = Field.EMPTY;

    static Map getMap(VacManModel model) {
        Field[][] map = {{w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w},
                {w, b, d, d, w, d, d, d, d, w, d, d, d, w, w, d, d, d, w, d, d, d, d, w, d, d, b, w},
                {w, d, w, d, d, d, w, w, d, d, d, w, d, d, d, d, w, d, d, d, w, w, d, d, d, w, d, w},
                {w, d, w, w, w, d, d, w, w, d, w, w, e, w, w, e, w, w, d, w, w, d, d, w, w, w, d, w},
                {w, d, d, d, w, w, d, d, d, d, e, e, e, e, e, e, e, e, d, d, d, d, w, w, d, d, d, w},
                {w, w, w, d, d, d, d, w, w, d, w, e, w, g, g, w, e, w, d, w, w, d, d, d, d, w, w, w},
                {e, e, e, d, d, w, d, d, w, d, w, e, w, e, e, w, e, w, d, w, d, d, w, d, d, e, e, e},
                {w, w, w, w, d, w, w, d, d, d, w, e, w, w, w, w, e, w, d, d, d, w, w, d, w, w, w, w},
                {w, d, d, d, d, d, w, d, w, d, e, e, e, e, e, e, e, e, d, w, d, w, d, d, d, d, d, w},
                {w, d, w, w, w, d, d, d, w, w, w, e, w, w, w, w, e, w, w, w, d, d, d, w, w, w, d, w},
                {w, d, d, d, d, d, w, d, w, d, d, d, d, d, d, d, d, d, d, w, d, w, d, d, d, d, d, w},
                {w, d, w, w, w, w, w, d, d, d, w, w, w, e, e, w, w, w, d, d, d, w, w, w, w, w, d, w},
                {w, b, d, d, d, d, d, d, w, d, d, d, d, d, d, d, d, d, d, w, d, d, d, d, d, d, b, w},
                {w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w}};

        byte[] virusXStart = {13, 14};
        return new Map(model, map, 13, 11, virusXStart, 6, 4);

//		Field[][] map = { { w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w},
//				{ w, d, d, d, d, d, d, d, d, w, d, d, d, d, d, d, d, d, w},
//				{ w, b, w, w, d, w, w, w, d, w, d, w, w, w, d, w, w, b, w},
//				{ w, d, w, w, d, w, w, w, d, w, d, w, w, w, d, w, w, d, w},
//				{ w, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, w},
//				{ w, d, w, w, d, w, d, w, w, w, w, w, d, w, d, w, w, d, w},
//				{ w, d, d, d, d, w, d, d, d, w, d, d, d, w, d, d, d, d, w},
//				{ w, w, w, w, d, w, w, w, d, w, d, w, w, w, d, w, w, w, w},
//				{ w, w, w, w, d, w, e, e, e, e, e, e, e, w, d, w, w, w, w},
//				{ w, w, w, w, d, w, e, w, w, g, w, w, e, w, d, w, w, w, w},
//				{ e, e, e, e, d, e, e, w, e, e, e, w, e, e, d, e, e, e, e},
//				{ w, w, w, w, d, w, e, w, w, w, w, w, e, w, d, w, w, w, w},
//				{ w, w, w, w, d, w, e, e, e, e, e, e, e, w, d, w, w, w, w},
//				{ w, w, w, w, d, w, e, w, w, w, w, w, e, w, d, w, w, w, w},
//				{ w, d, d, d, d, d, d, d, d, w, d, d, d, d, d, d, d, d, w},
//				{ w, d, w, w, d, w, w, w, d, w, d, w, w, w, d, w, w, d, w},
//				{ w, b, d, w, d, d, d, d, d, d, d, d, d, d, d, w, d, b, w},
//				{ w, w, d, w, d, w, d, w, w, w, w, w, d, w, d, w, d, w, w},
//				{ w, d, d, d, d, w, d, d, d, w, d, d, d, w, d, d, d, d, w},
//				{ w, d, w, w, w, w, w, w, d, w, d, w, w, w, w, w, w, d, w},
//				{ w, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, d, w},
//				{ w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w} };
//		
//		byte[] virusXStart = {8, 9, 10};
//		return new Map(model, map, 9, 16, virusXStart, 10, 8);
    }

    static Field[][] getGameOver() {
        return new Field[][]
                {{e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e},
                {e, e, e, e, w, w, w, e, e, e, w, w, w, e, e, w, e, e, e, w, e, w, w, w, w, e, e, e},
                {e, e, e, w, w, e, e, e, e, w, w, d, w, w, e, w, w, w, w, w, e, w, e, e, e, e, e, e},
                {e, e, e, w, e, e, w, w, e, w, d, b, d, w, e, w, e, w, e, w, e, w, w, w, w, e, e, e},
                {e, e, e, w, w, e, b, w, e, w, w, w, w, w, e, w, e, e, e, w, e, w, e, e, e, e, e, e},
                {e, e, e, e, w, w, w, w, e, w, e, e, e, w, e, w, e, e, e, w, e, w, w, w, w, e, e, e},
                {e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e},
                {e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e},
                {e, e, e, e, w, w, w, e, e, w, e, e, e, w, e, w, w, w, w, w, e, w, w, w, e, e, e, e},
                {e, e, e, w, w, d, w, w, e, w, w, e, w, w, e, w, e, e, e, e, e, w, b, w, e, e, e, e},
                {e, e, e, w, d, b, d, w, e, e, w, e, w, e, e, w, w, w, w, w, e, w, w, w, w, e, e, e},
                {e, e, e, w, w, d, w, w, e, e, w, w, w, e, e, w, e, e, e, e, e, w, e, e, w, e, e, e},
                {e, e, e, e, w, w, w, e, e, e, e, w, e, e, e, w, w, w, w, w, e, w, e, e, w, e, e, e},
                {e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e}};
    }
}

/**
 * Enumeration of field types.
 */
enum Field {
    WALL(-2), GATE(-1), EMPTY(0), DOT(10), BONUS(50);

    /**
     * Field value for comparing and point score value.
     */
    final byte value;

    Field(int value) {
        this.value = (byte) value;
    }
}

/**
 * Enumeration of directions.
 */
enum Direction {
    UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0), STILL(0, 0);

    /**
     * x direction.
     */
    final byte x;
    /**
     * y direction.
     */
    final byte y;

    Direction(int x, int y) {
        this.x = (byte) x;
        this.y = (byte) y;
    }

    /**
     * @return an array with all Directions
     */
    static Direction[] getArray() {
        return new Direction[]{UP, RIGHT, DOWN, LEFT};
    }

    /**
     * Checks if next step in this direction is over the edge.
     */
    boolean mapCheck(Map map, byte y, byte x) {
        return this.x + x >= 0 && this.x + x < map.columns && this.y + y >= 0 && this.y + y < map.rows;
    }

    byte nextX(Map map, byte x) {
        return (byte) ((x + this.x + map.columns) % map.columns);
    }

    byte nextY(Map map, byte y) {
        return (byte) ((y + this.y + map.rows) % map.rows);
    }

    Direction getOpposite() {
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