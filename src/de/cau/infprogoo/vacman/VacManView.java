package de.cau.infprogoo.vacman;

import java.awt.Color;

import acm.graphics.GCompound;
import acm.graphics.GLabel;
import acm.graphics.GImage;
import acm.util.ErrorException;
import acm.util.JTFTools;

// TODO animations

interface VMView {
	void draw();

	void drawEntities();

	void drawFields(Map map);

	void update(double ms);

	void reset();
}

class VacManView extends GCompound implements VMView {

	VacManModel model;

	static final int FIELD_OFFSET = 50;
	static final int FIELD_SIZE = 30;

	/** Map of Field. */
	private GCompound[][] fields;
	/** View of Vac-Man. */
	private VacBody vacMan = new VacBody();
	/** Array of Ghost bodies. */
	private VirusBody[] virus = { new VirusBody("redvirus.png"), new VirusBody("pinkvirus.png"),
			new VirusBody("bluevirus.png"), new VirusBody("orangevirus.png") };

	private GLabel scoreDisplay;
	private LifeDisplay lifeDisplay = new LifeDisplay();

	VacManView(VacManModel model) {
		this.model = model;
		scoreDisplay = new GLabel("L1 SCORE: 0");
		scoreDisplay.setColor(Color.WHITE);
		scoreDisplay.setFont("Default-20");
		draw();
	}

	public void reset() {
		remove(lifeDisplay);
		lifeDisplay = new LifeDisplay();
		draw();
	}

	/**
	 * Draws the view.
	 * 
	 * @param model
	 */
	public void draw() {

		drawFields(model.getMap());
		add(lifeDisplay, FIELD_OFFSET / 2, 10);
		drawEntities();

	}

	public void drawFields(Map map) {

		removeAll();
		
		fields = new GCompound[model.getMap().ROWS][model.getMap().COLUMNS];

		for (byte x = 0; x < map.COLUMNS; x++) {
			for (byte y = 0; y < map.ROWS; y++) {
				GCompound field = null;
				switch (map.get(x, y)) {
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
		add(scoreDisplay, this.getWidth() - 5 * FIELD_SIZE, 30);
	}

	public void drawEntities() {
		Virus[] virus = model.getVirus();
		for (int i = 0; i < virus.length; i++) {
			int x = FIELD_OFFSET / 2 + FIELD_SIZE * virus[i].getX();
			int y = FIELD_OFFSET + FIELD_SIZE * virus[i].getY();
			add(this.virus[i], x, y);
		}

		Vac vMan = model.getVacMan();
		add(vacMan, FIELD_OFFSET / 2 + FIELD_SIZE * vMan.getX(), FIELD_OFFSET + FIELD_SIZE * vMan.getY());
	}

	public void update(double ms) {
		scoreDisplay.setLabel("L" + model.getLevel() + " SCORE: " + model.getScore());
		lifeDisplay.update(model);

		Virus[] virus = model.getVirus();
		for (int i = 0; i < virus.length; i++) {
			this.virus[i].update(virus[i]);
		}
		
		Vac vMan = model.getVacMan(); 
		vacMan.update(vMan);

		animate(ms);
		
		if (vMan.isTunneling()) {
			add(vacMan, FIELD_OFFSET / 2 + FIELD_SIZE * vMan.getX(), FIELD_OFFSET + FIELD_SIZE * vMan.getY());
		}
		
		for (int i = 0; i < virus.length; i++) {
			if (virus[i].isTunneling()) {
				add(this.virus[i], FIELD_OFFSET / 2 + FIELD_SIZE * virus[i].getX(), FIELD_OFFSET + FIELD_SIZE * virus[i].getY());
			}
		}

		byte x = model.getVacMan().getX();
		byte y = model.getVacMan().getY();
		if (fields[y][x] != null && model.getMap().get(x, y).VALUE >= Field.EMPTY.VALUE) {
			remove(fields[y][x]);
			fields[y][x] = null;
		}
	}

	private void animate(double ms) {
		double msPerFrame = ms / FIELD_SIZE;

		Vac vMan = model.getVacMan();
		Virus[] virus = model.getVirus();

		for (int i = 0; i < FIELD_SIZE; i++) {
			vacMan.move(vMan.getDir().X, vMan.getDir().Y);
			for (byte j = 0; j < virus.length; j++) {
				if (!virus[j].isFrightened() || i % 2 == 0) {
					this.virus[j].move(virus[j].getDir().X, virus[j].getDir().Y);
				}
			}
			JTFTools.pause(msPerFrame);
		}
	}

}

class VacBody extends GCompound {

	private boolean animation = false;

	VacBody() {
		add(new GImage("vacman\\vacman.png"));
	}

	void update(Vac vacMan) {
		removeAll();
		String skin = "vacman\\vacman";
		if (animation && vacMan.getDir() != Direction.STILL) {
			skin += vacMan.getDir();
		}
		add(new GImage(skin + ".png"));
		animation = !animation;
	}
}

class VirusBody extends GCompound {

	private final GImage skin;
	private final GImage fright = new GImage("frightvirus.png");
	private final GImage eaten = new GImage("eatenvirus.png");

	private boolean isFrightened = false;
	private boolean isEaten = false;

	VirusBody(String texture) {
		skin = new GImage(texture);
		skin.scale(1.5);
		eaten.scale(1.5);
		fright.scale(1.5);
		add(skin);
	}

	void update(Virus virus) {
		if (isFrightened != virus.isFrightened() || isEaten != virus.isEaten()) {
			removeAll();
			if (virus.isEaten()) {
				add(eaten);
				isFrightened = false;
				isEaten = true;
			} else if (virus.isFrightened()) {
				add(fright);
				isFrightened = true;
				isEaten = false;
			} else {
				add(skin);
				isEaten = false;
				isFrightened = false;
			}
		}
		if (virus.isFrightened() && Virus.getFrightCounter() < 7 && Virus.getFrightCounter() % 2 == 1) {
			remove(fright);
			add(skin);
			isFrightened = false;
		}
	}
}

class Wall extends GCompound {
	Wall(Map map, byte x, byte y) {
		String image = "wall\\wall";
		for (Direction dir : Direction.getArray()) {
			if (dir.mapCheck(map, y, x) && map.get((byte) (x + dir.X), (byte) (y + dir.Y)) == Field.WALL) {
				image += "1";
			} else {
				image += "0";
			}
		}
		try {
			add(new GImage(image + ".png"));
		} catch (ErrorException e) {
		}
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
		add(new GImage("gate.png"));
	}
}

class LifeDisplay extends GCompound {
	private GImage[] lives = { new GImage("vacman\\vacmanRight.png"), new GImage("vacman\\vacmanRight.png"), new GImage("vacman\\vacmanRight.png") };
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