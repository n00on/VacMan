package de.cau.infprogoo.vacman;

import acm.util.RandomGenerator;

/**
 * Abstract superclass for viruses and vac man.
 */
abstract class Entity {
	// Start values
	final byte X_START;
	final byte Y_START;
	private static final Direction DIR_START = Direction.STILL;

	// Instance vars
	private Direction dir;
	private byte x;
	private byte y;

	public Entity(int xStart, int yStart) {
		X_START = (byte) xStart;
		Y_START = (byte) yStart;
		reset();
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

	/**
	 * Updates considering the goal field.
	 * 
	 * @param model
	 */
	void update(VacManModel model) {
		Fields[][] map = model.getMap();

		if (map[y + dir.Y][x + dir.X].VALUE >= Fields.EMPTY.VALUE) {
			x += dir.X;
			y += dir.Y;
		} else {
			dir = Direction.STILL;
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

	private Direction nextDir = Direction.STILL;
	private byte lives = 3;

	public Vac() {
		super(13, 12);
	}

	byte getLives() {
		return lives;
	}

	void setNextDir(Direction dir) {
		nextDir = dir;
	}
	
	void reset() {
		nextDir = Direction.STILL;
		super.reset();
	}

	void update(VacManModel model) {

		if (model.getMap()[getY() + nextDir.Y][getX() + nextDir.X].VALUE >= Fields.EMPTY.VALUE) {
			setDir(nextDir);
		}

		super.update(model);
	}

	void checkHit(VacManModel model) {
		// Iterates through virus
		for (Virus virus : model.getVirus()) {
			// If Hit
			if (getX() == virus.getX() && getY() == virus.getY() || //
					getX() + getDir().getOpposite().X == virus.getX()
							&& getY() + getDir().getOpposite().Y == virus.getY()
							&& virus.getX() + virus.getDir().getOpposite().X == getX()
							&& virus.getY() + virus.getDir().getOpposite().Y == getY()) {
				// losing a life
				if (!virus.isFrightened() && !virus.isEaten()) {
					if (--lives == 0) {
						model.gameOver();
					} else {
						model.resetPositions();
					}
					return;
					// or eating the ghost
				} else if (virus.isFrightened()) {
					virus.eat();
					model.scoreEaten();
				}
			}
		}
	}
}

/**
 * Direct superclass for individual virus.
 */
abstract class Virus extends Entity {

	private static final byte Y_BACK = 4;

	/** Is out of the starting box? */
	private int outCounter;
	private final int outTime;
	private boolean isEaten;
	private boolean frighten;
	private int frightCounter;

	Virus(int xStart, int outTime) {
		super(xStart, 6);
		this.outTime = outTime;
		reset();
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
	
	void reset() {
		isEaten = false;
		frighten = false;
		frightCounter = 0;
		outCounter = 2 + outTime;
		super.reset();
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

		// If in start room
		if (outCounter > 0) {
			// either waits to move up
			if (outCounter-- > 2) {
				setDir(Direction.STILL);
			} else {
				// or moves out
				setDir(Direction.UP);
			}
			super.update();
			return;
		}

		// Frightens the virus
		if (frighten && !isEaten) {
			if (frightCounter == 0) {
				setDir(getDir().getOpposite());
			}
			frightCounter = 30;
			frighten = false;
		} else if (frighten) {
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
			
			if (x == xGoal && y == yGoal || map[y][x] == Fields.GATE) {
				setDir(Direction.DOWN);
				if (map[y][x] == Fields.GATE) {
					isEaten = false;
					outCounter = 2;
				}
				super.update();
				return;
			}
		}


		double distance = Integer.MAX_VALUE;
		Direction dir = getDir();

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
}

/**
 * Model for virus with random behavior.
 */
class RandomVirus extends Virus {

	RandomVirus() {
		super(13, 2);
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

	public FollowVirus() {
		super(14, 7);
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

	public PredictVirus() {
		super(13, 12);
	}

	/**
	 * Sets players coordinates as goal cell.
	 */
	void update(VacManModel model) {
		Vac vacMan = model.getVacMan();
		update(model, vacMan.getX() + vacMan.getDir().X * 5, vacMan.getY() + vacMan.getDir().Y * 5);
	}
}