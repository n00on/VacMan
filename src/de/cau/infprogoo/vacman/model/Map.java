package de.cau.infprogoo.vacman.model;

import acm.util.RandomGenerator;
import de.cau.infprogoo.vacman.model.entity.Virus;

public class Map {

    public final int rows;
    public final int columns;

    private final VacManModel model;
    private final Field[][] mapFields;

    public final byte vacXStart;
    public final byte vacYStart;
    private final byte[] virusXStart;
    public final byte virusYStart;
    public final byte virusYBack;

    private int dotCounter = 0;

    int getDotCounter() {
        return dotCounter;
    }

    public Field get(byte x, byte y) {
        return mapFields[(y + rows) % rows][(x + columns) % columns];
    }

    public byte getVirusStartX() {
        return virusXStart[RandomGenerator.getInstance().nextInt(virusXStart.length)];
    }

    Map(VacManModel model, Field[][] mapFields, int vacXStart, int vacYStart, byte[] virusXStart, int virusYStart, int virusYBack) {
        this.model = model;
        this.mapFields = mapFields;
        this.vacXStart = (byte) vacXStart;
        this.vacYStart = (byte) vacYStart;
        this.virusXStart = virusXStart;
        this.virusYStart = (byte) virusYStart;
        this.virusYBack = (byte) virusYBack;

        rows = mapFields.length;
        columns = mapFields[0].length;

        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                if (mapFields[y][x].value >= Field.DOT.value) {
                    dotCounter++;
                }
            }
        }
    }

    void update(byte x, byte y) {

        if (mapFields[y][x].value >= Field.DOT.value) {

            dotCounter--;

            model.addScore(mapFields[y][x].value);

            if (mapFields[y][x] == Field.BONUS) {
                Virus.frighten();
            }

            mapFields[y][x] = Field.EMPTY;
        }
    }

    private static final Field w = Field.WALL;
    private static final Field d = Field.DOT;
    private static final Field g = Field.GATE;
    private static final Field b = Field.BONUS;
    private static final Field e = Field.EMPTY;

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
