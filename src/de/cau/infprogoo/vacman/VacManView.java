package de.cau.infprogoo.vacman;

import java.awt.Color;

import acm.graphics.GCompound;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.graphics.GLabel;
import acm.util.JTFTools;

// TODO animations

class VacManView extends GCompound {

	static final int FIELD_OFFSET = 50;
	static final int FIELD_SIZE = 30;

	/** Map of fields. */
	private GCompound[][] fields = new GCompound[VacManModel.ROWS][VacManModel.COLUMNS];
	/** View of Vac-Man. */
	private VacBody vacMan = new VacBody();
	/** Array of Ghost bodies. 0 -> RandomVirus */
	private VirusBody[] virus = { new VirusBody(Color.GREEN), new VirusBody(Color.ORANGE) };

	private GLabel scoreDisplay;

	/**
	 * Draws the view.
	 * 
	 * @param model
	 */
	void draw(VacManModel model) {
		drawFields(model.getMap());
		Vac vMan = model.getVacMan();
		add(vacMan, FIELD_OFFSET / 2 + FIELD_SIZE * vMan.getX(), FIELD_OFFSET + FIELD_SIZE * vMan.getY());

		Entity[] virus = model.getVirus();
		for (int i = 0; i < virus.length; i++) {
			add(this.virus[i], FIELD_OFFSET / 2 + FIELD_SIZE * virus[i].getX(),
					FIELD_OFFSET + FIELD_SIZE * virus[i].getY());
		}

		scoreDisplay = new GLabel("SCORE \n" + model.getScore());
		scoreDisplay.setColor(Color.WHITE);
		scoreDisplay.setFont("Default-20");
		add(scoreDisplay, this.getWidth() - 3 * FIELD_SIZE, 30);
	}

	void drawFields(Fields[][] map) {
		removeAll();
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
	}

	void update(VacManModel model, double ms) {
		scoreDisplay.setLabel("SCORE \n" + model.getScore());

		Virus[] virus = model.getVirus();
		for (int i = 0; i < virus.length; i++) {
			if (this.virus[i].isFrightened() != virus[i].isFrightened()) {
				this.virus[i].switchFright();
			}
		}

		animate(model, ms);

		byte x = model.getVacMan().getX();
		byte y = model.getVacMan().getY();
		if (fields[y][x] != null) {
			remove(fields[y][x]);
			fields[y][x] = null;
		}
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
				this.virus[j].move(step * virus[j].getDir().X, step * virus[j].getDir().Y);
			}
			rest -= step;
			JTFTools.pause(msPerFrame);
		}
//		System.out.println(rest);
		if (vMan.isMoving())
			vacMan.move(rest * vMan.getDir().X, rest * vMan.getDir().Y);
	}

}

class Wall extends GCompound {
	Wall() {
		GRect rect = new GRect(VacManView.FIELD_SIZE, VacManView.FIELD_SIZE);
		rect.setFilled(true);
		rect.setColor(Color.BLUE);
		add(rect);
	}
}

class Dot extends GCompound {
	Dot() {
		int fieldSize = VacManView.FIELD_SIZE;
		GOval dot = new GOval(fieldSize / 5 * 2, fieldSize / 5 * 2, fieldSize / 5, fieldSize / 5);
		dot.setFilled(true);
		dot.setColor(Color.WHITE);
		add(dot);
	}
}

class Bonus extends GCompound {
	Bonus() {
		int fieldSize = VacManView.FIELD_SIZE;
		GOval dot = new GOval(fieldSize / 3, fieldSize / 3, fieldSize / 3, fieldSize / 3);
		dot.setFilled(true);
		dot.setColor(Color.yellow);
		add(dot);
	}
}

class Gate extends GCompound {
	Gate() {
		int fieldSize = VacManView.FIELD_SIZE;
		GRect rect = new GRect(fieldSize, fieldSize / 6);
		rect.setFilled(true);
		rect.setColor(Color.gray);
		add(rect);
	}
}

class VacBody extends GCompound {
	VacBody() {
		GOval head = new GOval(VacManView.FIELD_SIZE, VacManView.FIELD_SIZE);
		head.setFilled(true);
		head.setColor(Color.YELLOW);
		add(head);
	}
}

// TODO Virus view while vulnerable
class VirusBody extends GCompound {

	private static final Color frightColor = Color.CYAN;

	private final Color normalColor;
	private GOval head = new GOval(VacManView.FIELD_SIZE, VacManView.FIELD_SIZE);
	private boolean frightened = false;

	VirusBody(Color color) {
		normalColor = color;
		head.setFilled(true);
		head.setColor(normalColor);
		add(head);
	}

	void switchFright() {
		if (!frightened) {
			head.setColor(frightColor);
		} else {
			head.setColor(normalColor);
		}
		frightened = !frightened;
	}

	boolean isFrightened() {
		return frightened;
	}
}