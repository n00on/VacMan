package de.cau.infprogoo.vacman;

import acm.util.RandomGenerator;

// TODO GAME OVER, Level/Level, Tunnel

class VacManModel {

	static final byte ROWS = 14;
	static final byte COLUMNS = 28;
	static final byte VIRUSES = 1; // number of Viruses

	private VacManView view;

	private Vac vacMan = new Vac();
	private Entity[] virus = new Entity[VIRUSES];
	private Fields[][] map = new Fields[ROWS][COLUMNS];

	private boolean paused = false;
	private byte dotCounter;
	private int score = 0;

	VacManModel(VacManView view) {
		this.view = view;
		reset();
	}

	// reset game state
	void reset() {
		paused = true;
		dotCounter = 0;
		score = 0;
		vacMan = new Vac();
		for (int i = 0; i < VIRUSES; i++) {
			virus[i] = new RandomVirus();
		}
		initMap();
		view.draw(this);
		paused = false;
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
				{ w, d, d, d, d, w, d, d, w, d, w, e, w, e, e, w, e, w, d, w, d, d, w, d, d, d, d, w },
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

	Vac getVacMan() {
		return vacMan;
	}

	Entity[] getVirus() {
		return virus;
	}

	int getScore() {
		return score;
	}

	boolean isPaused() {
		return paused;
	}

	void pause() {
		paused = !paused;
	}

	// updates the entire game
	void update() {
		byte x = vacMan.getX();
		byte y = vacMan.getY();

		if (map[y][x].VALUE >= Fields.DOT.VALUE) {
			score += map[y][x].VALUE;
			map[y][x] = Fields.EMPTY;
			dotCounter--;

			if (dotCounter == 0) {
				System.out.println("YOU WIN!");
				win();
			}
		}
		// update vacman, checkHit
		vacMan.update(this);
		if (getVacMan().checkHit(this)) {
			pause();
			System.out.println("HIT");
			byte lives = vacMan.getLives();
			--lives;
			if (lives > 0) {
				reset();
				vacMan.setLives(lives);
				System.out.println(lives);
			} else {
				System.out.println(lives);
				gameOver();
			}
		}
		// update virus
		for (Entity vir : virus) {
			vir.update(this);
		}
	}

	Fields[][] getMap() {
		return map;
	}

	// game over
	void gameOver() {
		paused = true;
		System.out.println("GAME OVER");
		view.removeAll();
		Fields w = Fields.WALL;
		Fields e = Fields.EMPTY;
		map = new Fields[][] { { e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e },
				{ e, e, e, e, w, w, w, e, e, e, w, w, w, e, e, w, e, e, e, w, e, w, w, w, w, e, e, e },
				{ e, e, e, w, e, e, e, e, e, w, e, e, e, w, e, w, w, e, w, w, e, w, e, e, e, e, e, e },
				{ e, e, e, w, e, e, w, w, e, w, w, w, w, w, e, w, e, w, e, w, e, w, w, w, w, e, e, e },
				{ e, e, e, w, e, e, e, w, e, w, e, e, e, w, e, w, e, e, e, w, e, w, e, e, e, e, e, e },
				{ e, e, e, e, w, w, w, e, e, w, e, e, e, w, e, w, e, e, e, w, e, w, w, w, w, e, e, e },
				{ e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e },
				{ e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e },
				{ e, e, e, e, w, w, w, e, e, w, e, e, e, w, e, w, w, w, w, e, w, w, w, w, e, e, e, e },
				{ e, e, e, w, e, e, e, w, e, w, e, e, e, w, e, w, e, e, e, e, w, e, e, e, w, e, e, e },
				{ e, e, e, w, e, e, e, w, e, e, w, e, w, e, e, w, w, w, w, e, w, w, w, w, e, e, e, e },
				{ e, e, e, w, e, e, e, w, e, e, w, e, w, e, e, w, e, e, e, e, w, e, e, w, e, e, e, e },
				{ e, e, e, e, w, w, w, e, e, e, e, w, e, e, e, w, w, w, w, e, w, e, e, e, w, e, e, e },
				{ e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e } };
		view.drawFields(map);
	}

	// game win
	void win() {
		paused = true;
		System.out.println("YOU WIN!");
		view.removeAll();
		Fields w = Fields.WALL;
		Fields e = Fields.EMPTY;
		map = new Fields[][] { { e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e },
				{ e, e, w, w, e, e, e, e, e, e, w, w, e, e, w, w, w, w, e, e, w, w, e, e, w, w, e, e },
				{ e, e, e, w, w, w, e, e, w, w, w, e, e, w, w, e, e, w, w, e, w, w, e, e, w, w, e, e },
				{ e, e, e, e, e, w, w, w, w, e, e, e, e, w, w, e, e, w, w, e, w, w, e, e, w, w, e, e },
				{ e, e, e, e, e, e, w, w, e, e, e, e, e, w, w, e, e, w, w, e, w, w, e, e, w, w, e, e },
				{ e, e, e, e, e, e, w, w, e, e, e, e, e, e, w, w, w, w, e, e, e, w, w, w, w, e, e, e },
				{ e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e },
				{ e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e },
				{ e, e, e, w, w, e, e, e, e, w, w, e, w, w, e, w, w, w, e, e, e, w, w, e, w, w, e, e },
				{ e, e, e, w, w, e, e, e, e, w, w, e, w, w, e, w, w, w, w, e, e, w, w, e, w, w, e, e },
				{ e, e, e, w, w, e, w, w, e, w, w, e, w, w, e, w, w, e, w, w, e, w, w, e, w, w, e, e },
				{ e, e, e, w, w, w, w, w, w, w, w, e, w, w, e, w, w, e, e, w, w, w, w, e, e, e, e, e },
				{ e, e, e, w, w, e, e, e, e, w, w, e, w, w, e, w, w, e, e, e, w, w, w, e, w, w, e, e },
				{ e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e } };
		view.drawFields(map);
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

	void update(VacManModel model) {
		Fields[][] map = model.getMap();
		if (nextDir.arrayCheck(y, x) && map[y + nextDir.Y][x + nextDir.X].VALUE >= Fields.EMPTY.VALUE) {
			dir = nextDir;
		}
		if (dir.arrayCheck(y, x) && map[y + dir.Y][x + dir.X].VALUE >= Fields.EMPTY.VALUE) {
			x += dir.X;
			y += dir.Y;
			moving = true;
		} else {
			moving = false;
		}

		model.getVacMan().checkHit(model);
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

	void setLives(byte lives) {
		this.lives = lives;
	}

	void update(VacManModel model) {
		super.update(model);
	}

	boolean checkHit(VacManModel model) {
		for (Entity virus : model.getVirus()) {
			if (getX() == virus.getX() && getY() == virus.getY()) {
				return true;
			}
		}
		return false;
	}
}

// TODO implement virus algorithms

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

	void update(VacManModel model) {
		Fields[][] map = model.getMap();

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
				if (newDir != getDir().getOpposite() && newDir.arrayCheck(y, x)
						&& map[y + newDir.Y][x + newDir.X].VALUE > Fields.GATE.VALUE) {
					setNextDir(newDir);
					break;
				}
			}
			super.update(model);
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