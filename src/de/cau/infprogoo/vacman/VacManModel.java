package de.cau.infprogoo.vacman;

import acm.util.RandomGenerator;

// TODO GAME OVER, Level/Level, Tunnel

class VacManModel {

	static final byte ROWS = 14;
	static final byte COLUMNS = 28;

	private VacManView view;

	private Vac vacMan = new Vac();
	private Virus[] virus = { new RandomVirus(), new FollowVirus() };
	private Fields[][] map = new Fields[ROWS][COLUMNS];

	private boolean paused = false;
	private byte dotCounter;
	private int score = 0;

	VacManModel(VacManView view) {
		this.view = view;
		initMap();
		reset();
		paused = false;
	}

	// reset game state
	void reset() {
		paused = true;

		vacMan = new Vac();
		virus[0] = new RandomVirus();
		virus[1] = new FollowVirus();
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

	Virus[] getVirus() {
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
			dotCounter--;

			if (dotCounter == 0) {
				System.out.println("YOU WIN!");
				win();
			}

			if (map[y][x].VALUE == Fields.BONUS.VALUE) {
				for (Virus vir : virus) {
					vir.frighten();
				}
			}

			map[y][x] = Fields.EMPTY;
		}
		// update vacman, checkHit
		vacMan.update(this);
		// update virus
		for (Virus vir : virus) {
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
		Fields w = Fields.WALL;
		Fields e = Fields.EMPTY;
		map = new Fields[][] {
				{ e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e },
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
		Fields w = Fields.WALL;
		Fields e = Fields.EMPTY;
		map = new Fields[][] {
				{ e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e },
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
	private byte x;
	private byte y;

	public Entity(int xStart, int yStart) {
		XSTART = (byte) xStart;
		YSTART = (byte) yStart;
		x = XSTART;
		y = YSTART;
	}

	Direction getDir() {
		return dir;
	}

	void setDir(Direction dir) {
		this.dir = dir;
	}

	byte getX() {
		return x;
	}

	byte getY() {
		return y;
	}

	void update(VacManModel model) {
		Fields[][] map = model.getMap();

		if (map[y + dir.Y][x + dir.X].VALUE >= Fields.EMPTY.VALUE) {
			x += dir.X;
			y += dir.Y;
		}

		model.getVacMan().checkHit(model);
	}

	void update() {
		x += dir.X;
		y += dir.Y;
	}
}

/**
 * Model for Vac-Man.
 */
class Vac extends Entity {

	private static final byte XSTART = 13;
	private static final byte YSTART = 12;

	private Direction nextDir = Direction.DOWN;
	private boolean moving = false;
	private byte lives = 3;
	private byte vulnerabilityCounter = 0;

	public Vac() {
		super(XSTART, YSTART);
	}

	byte getLives() {
		return lives;
	}

	boolean isMoving() {
		return moving;
	}

	void setNextDir(Direction dir) {
		nextDir = dir;
	}

	void setLives(byte lives) {
		this.lives = lives;
	}

	void update(VacManModel model) {
		Fields[][] map = model.getMap();
		byte y = getY();
		byte x = getX();

		if (map[y + nextDir.Y][x + nextDir.X].VALUE >= Fields.EMPTY.VALUE) {
			setDir(nextDir);
		}

		if (map[y + getDir().Y][x + getDir().X].VALUE >= Fields.EMPTY.VALUE) {
			moving = true;
		} else {
			moving = false;
		}

		if (vulnerabilityCounter > 0) {
			vulnerabilityCounter--;
		}

		super.update(model);
	}

	boolean checkHit(VacManModel model) {
		// Iterates through virus
		for (Virus virus : model.getVirus()) {
			// If Hit
			if (getX() == virus.getX() && getY() == virus.getY()) {
				// Losing life or eating ghost
				if (vulnerabilityCounter == 0 && !virus.isFrightened()) {
					if (--lives == 0) {
						model.gameOver();
					} else {
						System.out.println("HIT");
						System.out.println("LIVES:" + lives );
						model.reset();
					}
					vulnerabilityCounter = 3;
					return true;
				} else if (virus.isFrightened()) {
					// TODO eat ghost
				}
			}
		}
		return false;
	}
}

/**
 * Direct superclass for individual virus.
 */
class Virus extends Entity {

	private static final byte YSTART = 6;
	/** Is out of the starting box? */
	private boolean isOut = false;
	private int frightCounter = 0;

	public Virus(int xStart) {
		super(xStart, YSTART);
	}

	/**
	 * Starts frightened behavior and counter.
	 */
	void frighten() {
		if (frightCounter == 0) {
			setDir(getDir().getOpposite());
		}
		frightCounter = 30;
	}

	boolean isFrightened() {
		return frightCounter > 0;
	}

	/**
	 * Updates the direction and coordinates for lowest distance to goal
	 * coordinates.
	 * 
	 * @param model
	 * @param xGoal
	 * @param yGoal
	 */
	void update(VacManModel model, int xGoal, int yGoal) {
		Fields[][] map = model.getMap();
		byte x = getX();
		byte y = getY();
		Direction dir = getDir();

		if (isOut) {
			// Random behavior if frightened
			if (frightCounter > 0) {
				RandomGenerator rgen = new RandomGenerator();
				xGoal = rgen.nextInt(VacManModel.COLUMNS);
				yGoal = rgen.nextInt(VacManModel.ROWS);
				frightCounter--;
			}

			double distance = 1000;

			// Finds the next cell which isnt in the opposite direction and is closest to
			// the goal cell
			for (Direction newDir : Direction.getArray()) {
				if (newDir != dir.getOpposite() && map[y + newDir.Y][x + newDir.X].VALUE >= Fields.EMPTY.VALUE) {
					int yDiff = Math.abs(y + newDir.Y - yGoal);
					int xDiff = Math.abs(x + newDir.X - xGoal);
					double newDistance = Math.sqrt(yDiff * yDiff + xDiff * xDiff);
					if (newDistance < distance) {
						setDir(newDir);
						distance = newDistance;
					}
				}
			}

			super.update(model);
		} else {
			// If still in starting room, moves up.
			setDir(Direction.UP);
			if (map[y][x] == Fields.GATE) {
				isOut = true;
			}
			super.update();
		}
	}
}

/**
 * Model for virus with random behavior.
 */
class RandomVirus extends Virus {

	private static final byte XSTART = 13;

	public RandomVirus() {
		super(XSTART);
	}

	/**
	 * Updates with random goal cell.
	 */
	void update(VacManModel model) {
		RandomGenerator rgen = new RandomGenerator();
		update(model, rgen.nextInt(VacManModel.COLUMNS), rgen.nextInt(VacManModel.ROWS));
	}

}

/**
 * Model for virus which follows the player directly.
 */
class FollowVirus extends Virus {
	private static final byte XSTART = 14;

	public FollowVirus() {
		super(XSTART);
	}

	/**
	 * Sets players coordinates as goal cell.
	 */
	void update(VacManModel model) {
		update(model, model.getVacMan().getX(), model.getVacMan().getY());
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
	 * @return an array with all Directions
	 */
	static Direction[] getArray() {
		Direction[] array = { UP, DOWN, RIGHT, LEFT };
		return array;
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