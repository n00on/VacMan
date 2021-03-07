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
	private VirusBody[] virus = { new VirusBody(Color.GREEN), new VirusBody(Color.ORANGE), new VirusBody(Color.MAGENTA) };

	private GLabel scoreDisplay;
	private LifeDisplay lifeDisplay = new LifeDisplay();
	
	public VacManView() {
		scoreDisplay = new GLabel("SCORE: 0");
		scoreDisplay.setColor(Color.WHITE);
		scoreDisplay.setFont("Default-20");
	}

	/**
	 * Draws the view.
	 * 
	 * @param model
	 */
	void draw(VacManModel model) {
		
		drawFields(model.getMap());
		add(lifeDisplay, FIELD_OFFSET / 2, 10);

		Entity[] virus = model.getVirus();
		for (int i = 0; i < virus.length; i++) {
			add(this.virus[i], FIELD_OFFSET / 2 + FIELD_SIZE * virus[i].getX(),
					FIELD_OFFSET + FIELD_SIZE * virus[i].getY());
		}
		
		Vac vMan = model.getVacMan();
		add(vacMan, FIELD_OFFSET / 2 + FIELD_SIZE * vMan.getX(), FIELD_OFFSET + FIELD_SIZE * vMan.getY());
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
		add(scoreDisplay, this.getWidth() - 3 * FIELD_SIZE, 30);
	}

	void update(VacManModel model, double ms) {
		scoreDisplay.setLabel("SCORE: " + model.getScore());
		lifeDisplay.update(model);

		Virus[] virus = model.getVirus();
		for (int i = 0; i < virus.length; i++) {
			this.virus[i].update(virus[i]);
		}

		animate(model, ms);

		byte x = model.getVacMan().getX();
		byte y = model.getVacMan().getY();
		if (fields[y][x] != null && model.getMap()[y][x] == Fields.EMPTY) {
			remove(fields[y][x]);
			fields[y][x] = null;
		}
	}

	void animate(VacManModel model, double ms) {
		double msPerFrame = ms / FIELD_SIZE;

		Vac vMan = model.getVacMan();
		Entity[] virus = model.getVirus();

		for (int i = 0; i < FIELD_SIZE; i++) {
			vacMan.move(vMan.getDir().X, vMan.getDir().Y);
			for (byte j = 0; j < virus.length; j++) {
				this.virus[j].move(virus[j].getDir().X, virus[j].getDir().Y);
			}
			JTFTools.pause(msPerFrame);
		}
	}

}

class VacBody extends GCompound {
	VacBody() {
		GOval head = new GOval(VacManView.FIELD_SIZE / 6 * 5, VacManView.FIELD_SIZE / 6 * 5);
		head.setFilled(true);
		head.setColor(Color.YELLOW);
		add(head, VacManView.FIELD_SIZE / 12 * 1, VacManView.FIELD_SIZE / 12 * 1);
	}
}

class VirusBody extends GCompound {

	private static final Color frightColor = Color.CYAN;
	private static final Color eatenColor = Color.LIGHT_GRAY;

	private final Color normalColor;
	private GOval head = new GOval(VacManView.FIELD_SIZE, VacManView.FIELD_SIZE);
	private boolean frightened = false;
	private boolean eaten = false;

	VirusBody(Color color) {
		normalColor = color;
		head.setFilled(true);
		head.setColor(normalColor);
		add(head);
	}

	void update(Virus virus) {
		if (frightened != virus.isFrightened() || eaten != virus.isEaten()) {
			if (!eaten && virus.isEaten()) {
				head.setColor(eatenColor);
				eaten = true;
				frightened = false;
			} else if (!frightened) {
				head.setColor(frightColor);
				frightened = true;
			} else {
				head.setColor(normalColor);
				eaten = false;
				frightened = false;
			}
		}
	}

	boolean isFrightened() {
		return frightened;
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

class LifeDisplay extends GCompound {
	VacBody[] lives = { new VacBody(),  new VacBody(),  new VacBody() };
	byte lifeCount = 3;
	
	public LifeDisplay() {
		for (byte i = 0; i < lives.length; i++) {
			add(lives[i], i * VacManView.FIELD_SIZE, 0);
		}
	}
	
	void update(VacManModel model) {
		if (model.getVacMan().getLives() != lifeCount) {
			lifeCount--;
			remove(lives[lifeCount]);
		}
	}
}