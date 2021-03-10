package de.cau.infprogoo.vacman;

import java.util.ArrayList;

import acm.util.JTFTools;

// TODO Level/Level, Tunnel

class VacManModel {

	static final byte ROWS = 14;
	static final byte COLUMNS = 28;

	private Vac vacMan = new Vac();
	private Virus[] virus = { new RandomVirus(), new FollowVirus(), new PredictVirus() };
	private Fields[][] map = new Fields[ROWS][COLUMNS];
	
	private int msPerUpdate = 250;
	private boolean newLevel = false;
	private boolean resetPositions = false;
	private boolean resetGame = false;
	private boolean paused = false;
	private byte dotCounter;
	private int score = 0;

	private ArrayList<VMView> views = new ArrayList<>();
	
	public VacManModel() {
		initMap();
	}
	
	// Game Loop
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

	void pause() {
		paused = !paused;
	}

	Fields[][] getMap() {
		return map;
	}
	
	/**
	 * Reset vac man and viruses in next update
	 */
	void resetPositions() {
		resetPositions = true;
	}
	
	void resetGame() {
		resetGame = true;
		newLevel = true;
		resetPositions = true;
	}
	
	void scoreEaten() {
		score += 200;
	}

	// updates the entire game state
	void update() {
		
		if (resetPositions || newLevel) {
			if (newLevel) {
				if (resetGame) {
					msPerUpdate = 250;
					score = 0;
					vacMan = new Vac();
					resetGame = false;
				}
				initMap();
				newLevel = false;
			}
			vacMan.reset();
			for (Virus vir : virus) {
				vir.reset();
			}
			for (VMView view : views) {
				view.draw();
			}
			resetPositions = false;
			return;
		}
		
		
		
		vacMan.update(this);
		
		for (Virus vir : virus) {
			vir.update(this);
		}
		
		byte x = vacMan.getX();
		byte y = vacMan.getY();
		if (map[y][x].VALUE >= Fields.DOT.VALUE) {
			
			score += map[y][x].VALUE;
	
			if (--dotCounter == 0) {
				msPerUpdate -= msPerUpdate / 8;
				newLevel = true;
				return;
			} else {
	
				if (map[y][x] == Fields.BONUS) {
					for (Virus vir : virus) {
						vir.frighten();
					}
				}
		
				map[y][x] = Fields.EMPTY;
			}
		}
		
		vacMan.checkHit(this);
	}

	private void initMap() {
	
		Fields w = Fields.WALL;
		Fields d = Fields.DOT;
		Fields g = Fields.GATE;
		Fields b = Fields.BONUS;
		Fields e = Fields.EMPTY;
	
		Fields[][] map = { 
				{ w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w },
				{ w, b, d, d, w, d, d, d, d, w, d, d, d, w, w, d, d, d, w, d, d, d, d, w, d, d, b, w },
				{ w, d, w, d, d, d, w, w, d, d, d, w, d, d, d, d, w, d, d, d, w, w, d, d, d, w, d, w },
				{ w, d, w, w, w, d, d, w, w, d, w, w, e, w, w, e, w, w, d, w, w, d, d, w, w, w, d, w },
				{ w, d, d, d, w, w, d, d, d, d, e, e, e, e, e, e, e, e, d, d, d, d, w, w, d, d, d, w },
				{ w, w, w, d, d, d, d, w, w, d, w, e, w, g, g, w, e, w, d, w, w, d, d, d, d, w, w, w },
				{ w, e, w, d, d, w, d, d, w, d, w, e, w, e, e, w, e, w, d, w, d, d, w, d, d, w, e, w },
				{ w, w, w, w, d, w, w, d, d, d, w, e, w, w, w, w, e, w, d, d, d, w, w, d, w, w, w, w },
				{ w, d, d, d, d, d, w, d, w, d, e, e, e, e, e, e, e, e, d, w, d, w, d, d, d, d, d, w },
				{ w, d, w, w, w, d, d, d, w, d, w, e, w, w, w, w, e, w, d, w, d, d, d, w, w, w, d, w },
				{ w, d, d, d, d, d, w, d, w, d, w, d, d, d, d, d, d, w, d, w, d, w, d, d, d, d, d, w },
				{ w, d, w, w, w, w, w, d, d, d, w, w, w, e, e, w, w, w, d, d, d, w, w, w, w, w, d, w },
				{ w, b, d, d, d, d, d, d, w, d, d, d, d, e, e, d, d, d, d, w, d, d, d, d, d, d, b, w },
				{ w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w, w } };
	
		dotCounter = 0;
		for (int x = 0; x < VacManModel.COLUMNS; x++) {
			for (int y = 0; y < VacManModel.ROWS; y++) {
				if (map[y][x].VALUE >= d.VALUE) {
					dotCounter++;
				}
			}
		}
		this.map = map;
	}

	// game over
	void gameOver() {
		Fields w = Fields.WALL;
		Fields e = Fields.EMPTY;
		map = new Fields[][] { { e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e },
				{ e, e, e, e, w, w, w, e, e, e, w, w, w, e, e, w, e, e, e, w, e, w, w, w, w, e, e, e },
				{ e, e, e, w, w, e, e, e, e, w, w, e, w, w, e, w, w, w, w, w, e, w, e, e, e, e, e, e },
				{ e, e, e, w, e, e, w, w, e, w, e, e, e, w, e, w, e, w, e, w, e, w, w, w, w, e, e, e },
				{ e, e, e, w, w, e, e, w, e, w, w, w, w, w, e, w, e, e, e, w, e, w, e, e, e, e, e, e },
				{ e, e, e, e, w, w, w, w, e, w, e, e, e, w, e, w, e, e, e, w, e, w, w, w, w, e, e, e },
				{ e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e },
				{ e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e },
				{ e, e, e, e, w, w, w, e, e, w, e, e, e, w, e, w, w, w, w, e, w, w, w, e, e, e, e, e },
				{ e, e, e, w, w, e, w, w, e, w, w, e, w, w, e, w, e, e, e, e, w, e, w, e, e, e, e, e },
				{ e, e, e, w, e, e, e, w, e, e, w, e, w, e, e, w, w, w, w, e, w, w, w, w, e, e, e, e },
				{ e, e, e, w, w, e, w, w, e, e, w, w, w, e, e, w, e, e, e, e, w, e, e, w, e, e, e, e },
				{ e, e, e, e, w, w, w, e, e, e, e, w, e, e, e, w, w, w, w, e, w, e, e, w, e, e, e, e },
				{ e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e, e } };
		for (VMView view : views) {
			view.drawFields(map);
		}
//		lighthouseView.close();
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
			return this;
		}
	}
}