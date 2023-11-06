package de.cau.infprogoo.vacman;

import java.util.ArrayList;

import acm.util.JTFTools;
import acm.util.RandomGenerator;

class VacManModel {

	/** MVC Views implementing the VMView interface. */
	private ArrayList<VMView> views = new ArrayList<>();

	/** Entities and Map. */
	private Vac vacMan;
	private Virus[] virus;
	private Map map;

	/** Booleans determining update behavior. */
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
		Virus[] virus = { new FollowVirus(this), new PredictVirus(this), new BlueVirus(this),
				new DistanceVirus(this) };
		this.virus = virus;
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

	/** Updates the entire game state. */
	void update() {

		if (resetPositions || newLevel || resetGame) {
			vacMan.reset();
			Virus.resetAll(virus);
			resetPositions = false;

			if (newLevel || resetGame) {
				level++;
				msPerUpdate -= msPerUpdate / 10;
				Virus.decreaseFrightTime();
				initMap();
				newLevel = false;

				if (resetGame) {
					level = 1;
					msPerUpdate = 200;
					Virus.resetFrightTime();
					score = 0;
					vacMan = new Vac(this);
					resetGame = false;
					for (VMView view : views) {
						view.reset();
					}
				} else {
					for (VMView view : views) {
						view.draw();
					}
				}

			} else {
				for (VMView view : views) {
					view.drawEntities();
				}
			}
			return;
		}
		
		vacMan.update();

		Virus.updateAll(virus);

		map.update(vacMan.getX(), vacMan.getY());
		
		if (map.getDotCounter() == 0) {
			newLevel = true;
			return;
		}
	}

	/** Initiates standard map. */
	private void initMap() {
		this.map = Map.getMap(this);
	}

	/** Game Over map. */
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
	
	final int ROWS;
	final int COLUMNS;
	
	private VacManModel model;
	private Field[][] map;
	
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
		return map[(y + ROWS) % ROWS][(x + COLUMNS) % COLUMNS];
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
		
		ROWS = map.length;
		COLUMNS = map[0].length;
		
		for (int x = 0; x < COLUMNS; x++) {
			for (int y = 0; y < ROWS; y++) {
				if (map[y][x].VALUE >= Field.DOT.VALUE) {
					dotCounter++;
				}
			}
		}
	}
	
	void update(byte x, byte y) {
		
		if (map[y][x].VALUE >= Field.DOT.VALUE) {
			
			dotCounter--;

			model.addScore(map[y][x].VALUE);

			if (map[y][x] == Field.BONUS) {
				Virus.frighten();
			}

			map[y][x] = Field.EMPTY;
		}
	}
	
	static private Field w = Field.WALL;
	static private Field d = Field.DOT;
	static private Field g = Field.GATE;
	static private Field b = Field.BONUS;
	static private Field e = Field.EMPTY;
	
	static Map getMap(VacManModel model) {
		Field[][] map = { { w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w },
		{ w, b, d, d, w, d, d, d, d, w, d, d, d, w, w, d, d, d, w, d, d, d, d, w, d, d, b, w },
		{ w, d, w, d, d, d, w, w, d, d, d, w, d, d, d, d, w, d, d, d, w, w, d, d, d, w, d, w },
		{ w, d, w, w, w, d, d, w, w, d, w, w, e, w, w, e, w, w, d, w, w, d, d, w, w, w, d, w },
		{ w, d, d, d, w, w, d, d, d, d, e, e, e, e, e, e, e, e, d, d, d, d, w, w, d, d, d, w },
		{ w, w, w, d, d, d, d, w, w, d, w, e, w, g, g, w, e, w, d, w, w, d, d, d, d, w, w, w },
		{ e, e, e, d, d, w, d, d, w, d, w, e, w, e, e, w, e, w, d, w, d, d, w, d, d, e, e, e },
		{ w, w, w, w, d, w, w, d, d, d, w, e, w, w, w, w, e, w, d, d, d, w, w, d, w, w, w, w },
		{ w, d, d, d, d, d, w, d, w, d, e, e, e, e, e, e, e, e, d, w, d, w, d, d, d, d, d, w },
		{ w, d, w, w, w, d, d, d, w, w, w, e, w, w, w, w, e, w, w, w, d, d, d, w, w, w, d, w },
		{ w, d, d, d, d, d, w, d, w, d, d, d, d, d, d, d, d, d, d, w, d, w, d, d, d, d, d, w },
		{ w, d, w, w, w, w, w, d, d, d, w, w, w, e, e, w, w, w, d, d, d, w, w, w, w, w, d, w },
		{ w, b, d, d, d, d, d, d, w, d, d, d, d, d, d, d, d, d, d, w, d, d, d, d, d, d, b, w },
		{ w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w } };
		
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
		Field[][] map = { { e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e },
				{ e, e, e, e, w, w, w, e, e, e, w, w, w, e, e, w, e, e, e, w, e, w, w, w, w, e, e, e },
				{ e, e, e, w, w, e, e, e, e, w, w, d, w, w, e, w, w, w, w, w, e, w, e, e, e, e, e, e },
				{ e, e, e, w, e, e, w, w, e, w, d, b, d, w, e, w, e, w, e, w, e, w, w, w, w, e, e, e },
				{ e, e, e, w, w, e, b, w, e, w, w, w, w, w, e, w, e, e, e, w, e, w, e, e, e, e, e, e },
				{ e, e, e, e, w, w, w, w, e, w, e, e, e, w, e, w, e, e, e, w, e, w, w, w, w, e, e, e },
				{ e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e },
				{ e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e },
				{ e, e, e, e, w, w, w, e, e, w, e, e, e, w, e, w, w, w, w, w, e, w, w, w, e, e, e, e },
				{ e, e, e, w, w, d, w, w, e, w, w, e, w, w, e, w, e, e, e, e, e, w, b, w, e, e, e, e },
				{ e, e, e, w, d, b, d, w, e, e, w, e, w, e, e, w, w, w, w, w, e, w, w, w, w, e, e, e },
				{ e, e, e, w, w, d, w, w, e, e, w, w, w, e, e, w, e, e, e, e, e, w, e, e, w, e, e, e },
				{ e, e, e, e, w, w, w, e, e, e, e, w, e, e, e, w, w, w, w, w, e, w, e, e, w, e, e, e },
				{ e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e } };
		return map;
	}
}

/**
 * Enumeration of field types.
 */
enum Field {
	WALL(-2), GATE(-1), EMPTY(0), DOT(10), BONUS(50);

	/** Field value for comparing and point score value. */
	final byte VALUE;

	private Field(int value) {
		VALUE = (byte) value;
	}
}

/**
 * Enumeration of directions.
 */
enum Direction {
	UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0), STILL(0, 0);

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
		Direction[] array = { UP, RIGHT, DOWN, LEFT };
		return array;
	}

	/**
	 * Checks if next step in this direction is over the edge.
	 */
	boolean mapCheck(Map map, byte y, byte x) {
		return X + x >= 0 && X + x < map.COLUMNS && Y + y >= 0 && Y + y < map.ROWS;
	}

	byte nextX(Map map, byte x) {
		return (byte) ((x + X + map.COLUMNS) % map.COLUMNS);
	}

	byte nextY(Map map, byte y) {
		return (byte) ((y + Y + map.ROWS) % map.ROWS);
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