package de.cau.infprogoo.vacman;

import java.awt.Color;

import acm.graphics.GCompound;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.graphics.GLabel;
import acm.graphics.GImage;
import acm.util.JTFTools;

// TODO animations

interface VMView {
	void draw();
	void drawFields(Fields[][] map);
	void update(double ms);
}

class VacManView extends GCompound implements VMView {
	
	VacManModel model;

	static final int FIELD_OFFSET = 50;
	static final int FIELD_SIZE = 30;

	/** Map of fields. */
	private GCompound[][] fields = new GCompound[VacManModel.ROWS][VacManModel.COLUMNS];
	/** View of Vac-Man. */
	private VacBody vacMan = new VacBody();
	/** Array of Ghost bodies. 0 -> RandomVirus */
	private VirusBody[] virus = { new VirusBody("bluevirus.png"), new VirusBody("redvirus.png"),
			new VirusBody("pinkvirus.png") };

	private GLabel scoreDisplay;
	private LifeDisplay lifeDisplay = new LifeDisplay();

	VacManView(VacManModel model) {
		this.model = model;
		scoreDisplay = new GLabel("SCORE: 0");
		scoreDisplay.setColor(Color.WHITE);
		scoreDisplay.setFont("Default-20");
		draw();
	}

	void reset() {
		lifeDisplay = new LifeDisplay();
	}

	/**
	 * Draws the view.
	 * 
	 * @param model
	 */
	public void draw() {

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

	public void drawFields(Fields[][] map) {

		removeAll();

		for (byte x = 0; x < VacManModel.COLUMNS; x++) {
			for (byte y = 0; y < VacManModel.ROWS; y++) {
				GCompound field = null;
				switch (map[y][x]) {
				case WALL:
					field = new Wall(map, x, y);
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

	public void update(double ms) {
		scoreDisplay.setLabel("SCORE: " + model.getScore());
		lifeDisplay.update(model);

		Virus[] virus = model.getVirus();
		for (int i = 0; i < virus.length; i++) {
			this.virus[i].update(virus[i]);
		}

		animate(ms);

		byte x = model.getVacMan().getX();
		byte y = model.getVacMan().getY();
		if (fields[y][x] != null && model.getMap()[y][x].VALUE >= Fields.EMPTY.VALUE) {
			remove(fields[y][x]);
			fields[y][x] = null;
		}
	}

	private void animate(double ms) {
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

	private final GImage skin;
	private final GImage fright = new GImage("frightvirus.png");
	private final GImage eaten = new GImage("eatenvirus.png");

	private boolean isFrightened = false;
	private boolean isEaten = false;

	VirusBody(String skinName) {
		skin = new GImage(skinName);
		skin.scale(1.5);
		eaten.scale(1.5);
		fright.scale(1.5);
		add(skin);
	}

	void update(Virus virus) {
		if (isFrightened != virus.isFrightened() || isEaten != virus.isEaten()) {
			removeAll();
			if (isFrightened && virus.isEaten()) {
				add(eaten);
				isFrightened = false;
				isEaten = true;
			} else if (!isFrightened && virus.isFrightened()) {
				add(fright);
				isFrightened = true;
			} else {
				add(skin);
				isEaten = false;
				isFrightened = false;
			}
		}
	}
}

class Wall extends GCompound {
	Wall(Fields[][] map, byte x, byte y) {
		String image = "wall";
		for (Direction dir : Direction.getArray()) {
			if (dir.arrayCheck(y, x) && map[y + dir.Y][x + dir.X] == Fields.WALL) {
				image += "1";
			} else {
				image += "0";
			}
		}
		try {
			add(new GImage(image + ".png"));
		} finally {}
	}
}

class Dot extends GCompound {
	Dot() {
		GImage image = new GImage("coin.png");
		add(image, 7, 7);
	}
}

class Bonus extends GCompound {
	Bonus() {
		GImage image = new GImage("bonus.png");
		image.scale(1.5);
		add(image, 5, 0);
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
	private VacBody[] lives = { new VacBody(), new VacBody(), new VacBody() };
	private byte lifeCount = 3;

	LifeDisplay() {
		for (byte i = 0; i < lives.length; i++) {
			add(lives[i], i * VacManView.FIELD_SIZE, 0);
		}
	}

	void update(VacManModel model) {
		if (model.getVacMan().getLives() != lifeCount && lifeCount > 0) {
			remove(lives[--lifeCount]);
		}
	}
}