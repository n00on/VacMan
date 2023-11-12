package de.cau.infprogoo.vacman;

import acm.util.RandomGenerator;

/**
 * Abstract superclass for viruses and vac man.
 */
abstract class Entity {

	// Start values
	static final Direction DIR_START = Direction.STILL;

	final VacManModel model;
	// Instance vars
	private Direction dir;
	private byte x;
	private byte y;

	private boolean tunneling = false;

	Entity(VacManModel model, int xStart, int yStart) {
		this.model = model;
		reset(xStart, yStart);
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

	boolean isTunneling() {
		return tunneling;
	}

	/**
	 * Updates considering the goal field.
	 */
	void update() {
		Map map = model.getMap();

		if (!dir.mapCheck(map, y, x)) {
			tunneling = true;
		} else {
			tunneling = false;
		}
		if (map.get(dir.nextX(map, x), dir.nextY(map, y)).value >= Field.EMPTY.value) {
			x = dir.nextX(map, x);
			y = dir.nextY(map, y);
		} else {
			dir = Direction.STILL;
		}
	}

	void update(Direction dir) {
		this.dir = dir;
		x = dir.nextX(model.getMap(), x);
		y = dir.nextY(model.getMap(), y);
	}

	void reset(int xStart, int yStart) {
		x = (byte) xStart;
		y = (byte) yStart;
		dir = DIR_START;
	}
}

/**
 * Model for Vac-Man.
 */
class Vac extends Entity {

	private Direction nextDir = DIR_START;
	private byte lives = 3;

	public Vac(VacManModel model) {
		super(model, model.getMap().vacXStart, model.getMap().vacYStart);
	}

	byte getLives() {
		return lives;
	}

	void setNextDir(Direction dir) {
		nextDir = dir;
	}

	void reset() {
		nextDir = DIR_START;
		super.reset(model.getMap().vacXStart, model.getMap().vacYStart);
	}

	@Override
	void update() {

		Map map = model.getMap();
		if (map.get(nextDir.nextX(map, getX()), nextDir.nextY(map, getY())).value >= Field.EMPTY.value) {
			setDir(nextDir);
		}

		super.update();
	}

	/** Checks if vac and a virus collided. */
	void checkHit(Virus virus) {
		// If Hit
		if (getX() == virus.getX() && getY() == virus.getY() || //
				getX() + getDir().getOpposite().x == virus.getX() && getY() + getDir().getOpposite().y == virus.getY()
						&& virus.getX() + virus.getDir().getOpposite().x == getX()
						&& virus.getY() + virus.getDir().getOpposite().y == getY()) {
			// losing a life
			if (!virus.isFrightened()) {
				if (--lives == 0) {
					model.gameOver();
				} else {
					model.resetPositions();
				}
				// or eating the ghost
			} else {
				virus.eat();
			}
		}
	}
}

/**
 * Direct superclass for individual virus.
 */
abstract class Virus extends Entity {

	private static boolean frighten = false;
	private static byte frightTime = 26;
	private static byte frightCounter = 0;
	private static byte killStreak = 0;

	private static byte[] phaseTimes = { 14, 36, 14, 36, 10, 36, 10, -1 };
	private static byte phase = 0;
	private static byte phaseCounter = phaseTimes[phase];

	static void updateAll(Virus[] virus) {

		if (frighten) {
			frighten = false;
			killStreak = 0;
			frightCounter = (byte) (frightTime + frightCounter % 2);
			for (Virus vir : virus) {
				if (!vir.isEaten) {
					if (!vir.isFrightened) {
						vir.setDir(vir.getDir().getOpposite());
					}
					vir.isFrightened = true;
				}
			}
		} else if (frightCounter > 0) {
			if (--frightCounter == 0) {
				killStreak = 0;
				for (Virus vir : virus) {
					vir.isFrightened = false;
				}
			}
		} else if (phaseCounter == 0) {
			changePhase(virus);
		} else if (phaseCounter > 0) {
			// Only counts if frightCounter == 0
			phaseCounter--;
		}

		for (Virus vir : virus) {
			if (!vir.isFrightened || frightCounter % 2 == 0) {
				vir.update();
			}
			if (!vir.isEaten) {
				vir.model.getVacMan().checkHit(vir);
			}
		}
	}

	static void changePhase(Virus[] virus) {
		for (Virus vir : virus) {
			if (!vir.isEaten && !vir.isFrightened) {
				vir.setDir(vir.getDir().getOpposite());
			}
		}
		phaseCounter = phaseTimes[++phase];
		System.out.println(phase);
	}

	static void resetAll(Virus[] virus) {
		killStreak = 0;
		frightCounter = 0;
		phase = 0;
		phaseCounter = phaseTimes[phase];

		for (Virus vir : virus) {
			vir.reset();
		}
	}

	/** Frightens in next update. */
	static void frighten() {
		frighten = true;
	}

	static byte getFrightCounter() {
		return frightCounter;
	}

	static void decreaseFrightTime() {
		if (frightTime > 0) {
			frightTime -= 2;
		}
	}

	static void resetFrightTime() {
		frightTime = 26;
	}

	/** Home corner to retreat/scatter. */
	final byte xHome;
	final byte yHome;


	/** Start offset to get out. */
	private final int outTime;
	/** Is out of the starting box? (Is > 0) */
	private int outCounter;
	private boolean isEaten;
	private boolean isFrightened;

	Virus(VacManModel model, int outTime, int xHome, int yHome) {
		super(model, model.getMap().getVirusStartX(), model.getMap().virusYStart);
		this.xHome = (byte) xHome;
		this.yHome = (byte) yHome;
		this.outTime = outTime;
		reset();
	}

	void eat() {
		killStreak += 1;
		model.addScore((int) Math.pow(2, killStreak) * 100);

		isEaten = true;
	}

	boolean isFrightened() {
		return isFrightened;
	}

	boolean isEaten() {
		return isEaten;
	}

	void reset() {
		isEaten = false;
		isFrightened = false;
		outCounter = 2 + outTime;
		super.reset(model.getMap().getVirusStartX(), model.getMap().virusYStart);
	}

	/**
	 * Updates the direction and field for lowest distance to the goal field.
	 * 
	 * @param xGoal
	 * @param yGoal
	 */
	void update(int xGoal, int yGoal) {
		Map map = model.getMap();
		byte x = getX();
		byte y = getY();

		// If in start room
		if (outCounter == 0 && map.get((byte) (x + Direction.UP.x), (byte) (y + Direction.UP.y)) == Field.GATE) {
			update(Direction.UP);
			return;
		} else if (outCounter > 0) {
			outCounter--;
		}

		// Random behavior if frightened
		if (isEaten) {
			isFrightened = false;
			if (map.get(x, y) == Field.GATE || map.get((byte) (x + Direction.DOWN.x), (byte) (y + Direction.DOWN.y)) == Field.GATE) {
				if (map.get(x, y) == Field.GATE) {
					isEaten = false;
					outCounter = 3;
				}
				update(Direction.DOWN);
				return;
			}
			xGoal = model.getMap().getVirusStartX();
			yGoal = model.getMap().virusYBack;
		} else if (isFrightened) {
			RandomGenerator rgen = new RandomGenerator();
			xGoal = rgen.nextInt(map.columns);
			yGoal = rgen.nextInt(map.rows);
		} else if (phase % 2 == 0) { // SCATTER
			xGoal = xHome;
			yGoal = yHome;
		}

		this.setNextDir(xGoal, yGoal);

		super.update();
	}

	private void setNextDir(int xGoal, int yGoal) {
		Map map = model.getMap();
		byte x = getX();
		byte y = getY();

		double distance = Integer.MAX_VALUE;
		Direction dir = getDir();

		// Finds the next field (not in the opposite direction) closest to
		// the goal field
		for (Direction newDir : Direction.getArray()) {
			if (newDir != dir.getOpposite()
					&& map.get(newDir.nextX(map, x), newDir.nextY(map, y)).value >= Field.EMPTY.value) {
				int yDiff = newDir.nextY(map, y) - yGoal;
				int xDiff = newDir.nextX(map, x) - xGoal;
				double newDistance = Math.sqrt(yDiff * yDiff + xDiff * xDiff);
				if (newDistance < distance) {
					setDir(newDir);
					distance = newDistance;
				}
			}
		}
	}
}

/**
 * Model for virus which follows the player directly.
 */
class FollowVirus extends Virus {

	public FollowVirus(VacManModel model) {
		super(model, 7, model.getMap().columns, 0);
	}

	/**
	 * Sets players coordinates as goal cell.
	 */
	@Override
	void update() {
		update(model.getVacMan().getX(), model.getVacMan().getY());
	}
}

/**
 * Model for virus which follows the player directly.
 */
class PredictVirus extends Virus {

	/** (last) Direction of Vac. */
	private Direction vacDir = DIR_START;

	public PredictVirus(VacManModel model) {
		super(model, 2, 0, 0);
	}

	/**
	 * Sets players coordinates as goal cell.
	 */
	@Override
	void update() {
		Vac vacMan = model.getVacMan();
		if (vacMan.getDir() != Direction.STILL) {
			vacDir = vacMan.getDir();
		}
		update(vacMan.getX() + vacDir.x * 4, vacMan.getY() + vacDir.y * 4);
	}

	@Override
	void reset() {
		vacDir = Direction.STILL;
		super.reset();
	}
}

class BlueVirus extends Virus {

	public BlueVirus(VacManModel model) {
		super(model, 17, model.getMap().columns, model.getMap().rows);
	}

	/**
	 * Tries to ambush with FollowVirus.
	 */
	@Override
	void update() {
		Virus blinky = model.getVirus()[0];
		Vac vacMan = model.getVacMan();
		int xDiff = vacMan.getX() + vacMan.getDir().x * 2 - blinky.getX();
		int yDiff = vacMan.getY() + vacMan.getDir().y * 2 - blinky.getY();
		update(blinky.getX() + 2 * xDiff, blinky.getY() + 2 * yDiff);
	}
}

/**
 * Model for virus with random behavior.
 */
class DistanceVirus extends Virus {

	DistanceVirus(VacManModel model) {
		super(model, 11, 0, model.getMap().rows);
	}

	/**
	 * Chases after Vac and retreating when too close.
	 */
	@Override
	void update() {
		Vac vacMan = model.getVacMan();

		int xDiff = getX() - vacMan.getX();
		int yDiff = getY() - vacMan.getY();
		if (Math.sqrt(xDiff * xDiff + yDiff * yDiff) > 8) {
			update(vacMan.getX(), vacMan.getY());
		} else {
			update(xHome, yHome);
		}

	}

}