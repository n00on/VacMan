package de.cau.infprogoo.vacman;

import acm.util.RandomGenerator;

/**
 * Abstract superclass for viruses and vac man.
 */
abstract class Entity {
	// Start values
	private final byte X_START;
	private final byte Y_START;
	private static final Direction DIR_START = Direction.STILL;

	// Instance vars
	private Direction dir = DIR_START;
	private byte x;
	private byte y;

	public Entity(int xStart, int yStart) {
		X_START = (byte) xStart;
		Y_START = (byte) yStart;
		x = X_START;
		y = Y_START;
	}

	byte getX() {
		return x;
	}

	byte getY() {
		return y;
	}

	Direction getDir() {
		return dir;
	}

	void setDir(Direction dir) {
		this.dir = dir;
	}

	void update(VacManModel model) {
		Fields[][] map = model.getMap();

		if (map[y + dir.Y][x + dir.X].VALUE >= Fields.EMPTY.VALUE) {
			x += dir.X;
			y += dir.Y;
		}
	}

	void update() {
		x += dir.X;
		y += dir.Y;
	}

	void reset() {
		x = X_START;
		y = Y_START;
		dir = DIR_START;
	}
}

/**
 * Model for Vac-Man.
 */
class Vac extends Entity {

	private static final byte X_START = 13;
	private static final byte Y_START = 12;

	private Direction nextDir = Direction.DOWN;
	private byte lives = 3;
	private byte vulnerabilityCounter = 0;

	public Vac() {
		super(X_START, Y_START);
	}

	byte getLives() {
		return lives;
	}

	void setNextDir(Direction dir) {
		nextDir = dir;
	}

	void update(VacManModel model) {
		Fields[][] map = model.getMap();
		byte y = getY();
		byte x = getX();

		if (map[y + nextDir.Y][x + nextDir.X].VALUE >= Fields.EMPTY.VALUE) {
			setDir(nextDir);
		}

		if (map[y + getDir().Y][x + getDir().X].VALUE < Fields.EMPTY.VALUE) {
			setDir(Direction.STILL);
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
			if (getX() == virus.getX() && getY() == virus.getY() || getX() + getDir().getOpposite().X == virus.getX()
					&& getY() + getDir().getOpposite().Y == virus.getY()
					&& virus.getX() + virus.getDir().getOpposite().X == getX()
					&& virus.getY() + virus.getDir().getOpposite().Y == getY()) {
				// Losing life or eating ghost
				if (vulnerabilityCounter == 0 && !virus.isFrightened() && !virus.isEaten()) {
					if (--lives == 0) {
						model.gameOver();
					} else {
						model.resetPositions();
					}
					vulnerabilityCounter = 3;
					return true;
				} else if (virus.isFrightened()) {
					virus.eat();
					model.scoreEaten();
				}
			}
		}
		return false;
	}

	void reset() {
		super.reset();
	}
}

/**
 * Direct superclass for individual virus.
 */
class Virus extends Entity {

	private static final byte Y_BACK = 4;

	private final byte X_START;
	private static final byte Y_START = 6;
	/** Is out of the starting box? */
	private int outCounter = 2;
	private boolean isEaten = false;
	private boolean frighten = false;
	private int frightCounter = 0;

	Virus(int xStart, int outTime) {
		super(xStart, Y_START);
		X_START = (byte) xStart;
		outCounter += outTime;
	}

	/**
	 * Starts frightened behavior and counter.
	 */
	void frighten() {
		frighten = true;
	}

	void eat() {
		isEaten = true;
		frightCounter = 0;
	}

	boolean isFrightened() {
		return frightCounter > 0;
	}

	boolean isEaten() {
		return isEaten;
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

		if (outCounter == 0) {

			if (frighten && !isEaten) {
				if (frightCounter == 0) {
					setDir(getDir().getOpposite());
				}
				frightCounter = 30;
				frighten = false;
			}

			// Random behavior if frightened
			if (frightCounter > 0) {
				RandomGenerator rgen = new RandomGenerator();
				xGoal = rgen.nextInt(VacManModel.COLUMNS);
				yGoal = rgen.nextInt(VacManModel.ROWS);
				if (--frightCounter == 0) {
					setDir(getDir().getOpposite());
				}
			} else if (isEaten) {
				xGoal = X_START;
				yGoal = Y_BACK;
			}

			if (isEaten && (x == xGoal && y == yGoal || map[y][x] == Fields.GATE)) {
				setDir(Direction.DOWN);
				if (map[y][x] == Fields.GATE) {
					isEaten = false;
					outCounter = 2;
				}
				super.update();
			} else {

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
			}
		} else {
			// If still in starting room, moves up.
			if (outCounter-- > 2) {
				setDir(Direction.STILL);
			} else {
				setDir(Direction.UP);
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

	RandomVirus() {
		super(XSTART, 2);
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
		super(XSTART, 7);
	}

	/**
	 * Sets players coordinates as goal cell.
	 */
	void update(VacManModel model) {
		update(model, model.getVacMan().getX(), model.getVacMan().getY());
	}
}

/**
 * Model for virus which follows the player directly.
 */
class PredictVirus extends Virus {
	private static final byte XSTART = 14;

	public PredictVirus() {
		super(XSTART, 12);
	}

	/**
	 * Sets players coordinates as goal cell.
	 */
	void update(VacManModel model) {
		Vac vacMan = model.getVacMan();
		update(model, vacMan.getX() + vacMan.getDir().X * 5, vacMan.getY() + vacMan.getDir().Y * 5);
	}
}