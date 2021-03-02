package de.cau.infprogoo.vacman;

import java.awt.Color;

import acm.graphics.GCompound;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.util.JTFTools;

// TODO implement LighHouse
// TODO animations

class VacManView extends GCompound {

	static final int FIELD_OFFSET = 50;
	static final int FIELD_SIZE = 30;

	/** Map of fields. */
	private GCompound[][] fields = new GCompound[VacManModel.ROWS][VacManModel.COLUMNS];
	/** View of Vac-Man. */
	private VacBody vacMan = new VacBody();
	/** Array of Ghost bodies. 0 -> RandomVirus */
	private GCompound[] virus = { new RandomVirusBody() };

	/** 
	 * Draws the map.
	 * 
	 * @param model
	 */
	void draw(VacManModel model) {
		Fields[][] map = model.getMap();
		for (int x = 0; x < VacManModel.COLUMNS; x++) {
			for (int y = 0; y < VacManModel.ROWS; y++) {
				GCompound field = null;
				switch (map[y][x]) {
				case WALL:
					field = new Wall();
					break;
				case DOT:
					field = new Dot();
					break;
				case BONUS:
					field = new Bonus();
					break;
				case GATE:
					field = new Gate();
					break;
				default:
				}
				if (field != null) {
					fields[y][x] = field;
					add(field, FIELD_OFFSET / 2 + FIELD_SIZE * x, FIELD_OFFSET + FIELD_SIZE * y);
				}
			}
		}
		
		Vac vMan = model.getVacMan();
		Entity[] virus = model.getVirus();
		add(vacMan, FIELD_OFFSET / 2 + FIELD_SIZE * vMan.getX(), FIELD_OFFSET + FIELD_SIZE * vMan.getY());
		add(this.virus[0], FIELD_OFFSET / 2 + FIELD_SIZE * virus[0].getX(), FIELD_OFFSET + FIELD_SIZE * virus[0].getY());
	}

	void animate(VacManModel model, double ms) {
		double msPerFrame = 33;

		Vac vMan = model.getVacMan();
		Entity[] virus = model.getVirus();
		double step = FIELD_SIZE * msPerFrame / ms;

		int rest = FIELD_SIZE;

		for (int i = 0; i < ms / msPerFrame - 1; i++) {
			if (vMan.isMoving()) {
				vacMan.move(step * vMan.getDir().X, step * vMan.getDir().Y);
			}
			for (byte j = 0; j < virus.length; j++) {
				if (virus[j].isMoving()) {
					this.virus[j].move(step * virus[j].getDir().X, step * virus[j].getDir().Y);
				}
			}
			rest -= step;
			JTFTools.pause(msPerFrame);
		}
//		System.out.println(rest);
		if (vMan.isMoving())
			vacMan.move(rest * vMan.getDir().X, rest * vMan.getDir().Y);
	}
	
	void updateScore() {
		// TODO und initialisieren, Lebensanzeige
	}

	/** 
	 * Removes dot/bonus
	 * 
	 * @param x
	 * @param y
	 */
	void removeDot(byte x, byte y) {
		remove(fields[x][y]);
	}

}

class Wall extends GCompound {
	public Wall() {
		GRect rect = new GRect(VacManView.FIELD_SIZE, VacManView.FIELD_SIZE);
		rect.setFilled(true);
		rect.setColor(Color.BLUE);
		add(rect);
	}
}

class Dot extends GCompound {
	public Dot() {
		int fieldSize = VacManView.FIELD_SIZE;
		GOval dot = new GOval(fieldSize / 5 * 2, fieldSize / 5 * 2, fieldSize / 5, fieldSize / 5);
		dot.setFilled(true);
		dot.setColor(Color.WHITE);
		add(dot);
	}
}

class Bonus extends GCompound {
	public Bonus() {
		int fieldSize = VacManView.FIELD_SIZE;
		GOval dot = new GOval(fieldSize / 3, fieldSize / 3, fieldSize / 3, fieldSize / 3);
		dot.setFilled(true);
		dot.setColor(Color.yellow);
		add(dot);
	}
}

class Gate extends GCompound {
	public Gate() {
		int fieldSize = VacManView.FIELD_SIZE;
		GRect rect = new GRect(fieldSize, fieldSize / 6);
		rect.setFilled(true);
		rect.setColor(Color.gray);
		add(rect);
	}
}

class VacBody extends GCompound {
	public VacBody() {
		GOval head = new GOval(VacManView.FIELD_SIZE, VacManView.FIELD_SIZE);
		head.setFilled(true);
		head.setColor(Color.YELLOW);
		add(head);
	}
}

// TODO Virus view while vulnerable
class RandomVirusBody extends GCompound {
	public RandomVirusBody() {
		GOval head = new GOval(VacManView.FIELD_SIZE, VacManView.FIELD_SIZE);
		head.setFilled(true);
		head.setColor(Color.GREEN);
		add(head);
	}
}