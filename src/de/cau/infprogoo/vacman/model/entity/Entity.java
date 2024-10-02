package de.cau.infprogoo.vacman.model.entity;

import de.cau.infprogoo.vacman.model.Direction;
import de.cau.infprogoo.vacman.model.Field;
import de.cau.infprogoo.vacman.model.Map;
import de.cau.infprogoo.vacman.model.VacManModel;

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

	public byte getX() {
		return x;
	}

	public byte getY() {
		return y;
	}

	public Direction getDir() {
		return dir;
	}

	void setDir(Direction dir) {
		this.dir = dir;
	}

	public boolean isTunneling() {
		return tunneling;
	}

	/**
	 * Updates considering the goal field.
	 */
	void update() {
		Map map = model.getMap();

        tunneling = !dir.mapCheck(map, y, x);
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

