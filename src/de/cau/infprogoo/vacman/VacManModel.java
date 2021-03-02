package de.cau.infprogoo.vacman;

import acm.util.RandomGenerator;

class VacManModel {

	static final byte ROWS = 14;
	static final byte COLUMNS = 28;

	private VacManView view;

	private Vac vacMan = new Vac();
	private Entity[] virus = { new RandomVirus() };
	private Fields[][] map = new Fields[ROWS][COLUMNS];

	private boolean paused = false;
	private byte dotCounter;
	private int score = 0;

	VacManModel(VacManView view) {
		this.view = view;
		initMap();
		view.draw(this);
	}

	private void initMap() {

		Fields w = Fields.WALL;
		Fields d = Fields.DOT;
		Fields g = Fields.GATE;
		Fields b = Fields.BONUS;
		Fields e = Fields.EMPTY;

		Fields[][] map = { { w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w },
				{ w, b, d, d, w, d, d, d, d, w, d, d, d, w, w, d, d, d, w, d, d, d, d, w, d, d, b, w },
				{ w, d, w, d, d, d, w, w, d, d, d, w, d, d, d, d, w, d, d, d, w, w, d, d, d, w, d, w },
				{ w, d, w, w, w, d, d, w, w, d, w, w, e, w, w, e, w, w, d, w, w, d, d, w, w, w, d, w },
				{ w, d, d, d, w, w, d, d, d, d, e, e, e, e, e, e, e, e, d, d, d, d, w, w, d, d, d, w },
				{ w, w, w, d, d, d, d, w, w, d, w, e, w, g, g, w, e, w, d, w, w, d, d, d, d, w, w, w },
				{ w, e, e, d, d, w, d, d, w, d, w, e, w, e, e, w, e, w, d, w, d, d, w, d, d, e, e, w },
				{ w, w, w, w, d, w, w, d, d, d, w, e, w, w, w, w, e, w, d, d, d, w, w, d, w, w, w, w },
				{ w, d, d, d, d, d, w, d, w, d, e, e, e, e, e, e, e, e, d, w, d, w, d, d, d, d, d, w },
				{ w, d, w, w, w, d, d, d, w, d, w, e, w, w, w, w, e, w, d, w, d, d, d, w, w, w, d, w },
				{ w, d, d, d, d, d, w, d, w, d, w, d, d, d, d, d, d, w, d, w, d, w, d, d, d, d, d, w },
				{ w, d, w, w, w, w, w, d, d, d, w, w, w, e, e, w, w, w, d, d, d, w, w, w, w, w, d, w },
				{ w, b, d, d, d, d, d, d, w, d, d, d, d, e, e, d, d, d, d, w, d, d, d, d, d, d, b, w },
				{ w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w } };

		for (int x = 0; x < VacManModel.COLUMNS; x++) {
			for (int y = 0; y < VacManModel.ROWS; y++) {
				if (map[y][x].VALUE >= d.VALUE) {
					dotCounter++;
				}
			}
		}

		this.map = map;

	}

	void setDirection(Direction dir) {
		vacMan.setNextDir(dir);
	}

	Vac getVacMan() {
		return vacMan;
	}

	Entity[] getVirus() {
		return virus;
	}

	boolean isPaused() {
		return paused;
	}

	void pause() {
		paused = !paused;
	}

	void update() {
		vacMan.update(map);
		virus[0].update(map, vacMan.getX(), vacMan.getY());
		byte x = vacMan.getX();
		byte y = vacMan.getY();
		if (map[y][x].VALUE >= Fields.DOT.VALUE) {
			score += map[y][x].VALUE;
			map[y][x] = Fields.EMPTY;
			view.updateMap(y, x);
			dotCounter--;

			System.out.println(score);
			if (dotCounter == 0) {
				System.out.println("WON!");
			}
		}
	}

	Fields[][] getMap() {
		return map;
	}

}

/**
 * Abstract superclass for viruses and vac man.
 */
abstract class Entity {
	// Start values
	private final byte XSTART;
	private final byte YSTART;
	private static final Direction DIRSTART = Direction.DOWN;

	// Instance vars
	private Direction dir = DIRSTART;
	private Direction nextDir = DIRSTART;
	private byte x;
	private byte y;
	private boolean moving = false;

	public Entity(int xStart, int yStart) {
		XSTART = (byte) xStart;
		YSTART = (byte) yStart;
		x = XSTART;
		y = YSTART;
	}

	Direction getDir() {
		return dir;
	}

	void setNextDir(Direction nextDir) {
		this.nextDir = nextDir;
	}

	boolean isMoving() {
		return moving;
	}

	byte getX() {
		return x;
	}

	byte getY() {
		return y;
	}

	void update(Fields[][] map, byte x, byte y) {
		update(map);
	}

	void update(Fields[][] map) {
		if (nextDir.arrayCheck(y, x) && map[y + nextDir.Y][x + nextDir.X].VALUE > Fields.GATE.VALUE) {
			dir = nextDir;
		}
		if (dir.arrayCheck(y, x) && map[y + dir.Y][x + dir.X].VALUE > Fields.GATE.VALUE) {
			x += dir.X;
			y += dir.Y;
			moving = true;
		} else {
			moving = false;
		}
	}

	void update() {
		dir = nextDir;
		x += dir.X;
		y += dir.Y;
		moving = true;
	}
}

/**
 * Model for Vac-Man.
 */
class Vac extends Entity {

	private static final byte XSTART = 13;
	private static final byte YSTART = 12;

	private byte lives = 3;

	public Vac() {
		super(XSTART, YSTART);
	}

	byte getLives() {
		return lives;
	}

	void update(Fields[][] map) {
		super.update(map);

	}
}

//class Virus extends Entity {
//
//	private static final byte XSTART = 6;
//	private static final byte YSTART = 13;
//
//	public Virus() {
//		super(XSTART, YSTART);
//	}
//
//	void update(Fields[][] map, byte vacX, byte vacY) {
//		byte x = getX();
//		byte y = getY();
//		int xDiff = x - vacX;
//		int yDiff = y - vacY;
//		Direction goalDir;
//
//		if (Math.abs(xDiff) > Math.abs(yDiff)) {
//
//		} else {
//
//		}
//
//		super.update(map);
//	}
//}

/**
 * Model for virus with random behavior.
 */
class RandomVirus extends Entity {

	private static final byte XSTART = 13;
	private static final byte YSTART = 6;
	/** Is out of the starting box? */
	private boolean isOut = false;

	public RandomVirus() {
		super(XSTART, YSTART);
	}

	void update(Fields[][] map, byte vacX, byte vacY) {

		if (isOut) {
			RandomGenerator rgen = new RandomGenerator();
			byte x = getX();
			byte y = getY();
			while (true) {
				Direction newDir = Direction.DOWN;
				switch (rgen.nextInt(4)) {
				case 0:
					newDir = Direction.DOWN;
					break;
				case 1:
					newDir = Direction.LEFT;
					break;
				case 2:
					newDir = Direction.UP;
					break;
				case 3:
					newDir = Direction.RIGHT;
					break;
				}
				
				// Hallöle
				// TODO fix game crash when 
				if (newDir != getDir().getOpposite() && newDir.arrayCheck(y, x)
						&& map[y + newDir.Y][x + newDir.X].VALUE > Fields.GATE.VALUE) {
					setNextDir(newDir);
					break;
				}
			}
			super.update(map);
		} else {
			// If still in starting room, moves up.
			setNextDir(Direction.UP);
			super.update();
			if (map[getY()][getX()] == Fields.GATE) {
				isOut = true;
			}
		}
	}
}

/**
 * Enumeration of field types.
 */
enum Fields {
	WALL(-2), GATE(-1), EMPTY(0), DOT(10), BONUS(50);

	/** Field value for comparing reasons and point score value. */
	final byte VALUE;

	private Fields(int value) {
		VALUE = (byte) value;
	}
}

/**
 * Enumeration of directions.
 */
enum Direction {
	UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0);

	/** x direction. */
	final byte X;
	/** y direction. */
	final byte Y;
//Kommentar
	private Direction(int x, int y) {
		X = (byte) x;
		Y = (byte) y;
	}

	/**
	 * Checks if next step in this direction is in the map array.
	 * 
	 * @param y
	 * @param x
	 * @return
	 */
	boolean arrayCheck(byte y, byte x) {
		return X + x >= 0 && X + x < VacManModel.COLUMNS && Y + y >= 0 && Y + y < VacManModel.ROWS;
	}

	/**
	 * @return opposite direction
	 */
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
		default:
			return null;
		}
	}
}