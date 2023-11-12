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
	private VirusBody[] virus = {
            new VirusBody("redvirus"), new VirusBody("pinkvirus"),
			new VirusBody("bluevirus"), new VirusBody("orangevirus")};

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
	 */
	public void draw() {

		drawFields(model.getMap());
		add(lifeDisplay, FIELD_OFFSET / 2, 10);
		drawEntities();

	}

	public void drawFields(Map map) {

		removeAll();

		fields = new GCompound[model.getMap().rows][model.getMap().columns];

		for (byte x = 0; x < map.columns; x++) {
			for (byte y = 0; y < map.rows; y++) {
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
		Virus[] modelVirus = model.getVirus();
		for (int i = 0; i < modelVirus.length; i++) {
			int x = FIELD_OFFSET / 2 + FIELD_SIZE * modelVirus[i].getX();
			int y = FIELD_OFFSET + FIELD_SIZE * modelVirus[i].getY();
			add(this.virus[i], x, y);
		}

		Vac vMan = model.getVacMan();
		add(vacMan, FIELD_OFFSET / 2 + FIELD_SIZE * vMan.getX(), FIELD_OFFSET + FIELD_SIZE * vMan.getY());
	}

	public void update(double ms) {
		scoreDisplay.setLabel("L" + model.getLevel() + " SCORE: " + model.getScore());
		lifeDisplay.update(model);

		Virus[] modelVirus = model.getVirus();
		for (int i = 0; i < modelVirus.length; i++) {
			this.virus[i].update(modelVirus[i]);
		}

		Vac vMan = model.getVacMan();
		vacMan.update(vMan);

		animate(ms);

		if (vMan.isTunneling()) {
			add(vacMan, FIELD_OFFSET / 2 + FIELD_SIZE * vMan.getX(), FIELD_OFFSET + FIELD_SIZE * vMan.getY());
		}

		for (int i = 0; i < modelVirus.length; i++) {
			if (modelVirus[i].isTunneling()) {
				add(this.virus[i], FIELD_OFFSET / 2 + FIELD_SIZE * modelVirus[i].getX(), FIELD_OFFSET + FIELD_SIZE * modelVirus[i].getY());
			}
		}

		byte x = model.getVacMan().getX();
		byte y = model.getVacMan().getY();
		if (fields[y][x] != null && model.getMap().get(x, y).value >= Field.EMPTY.value) {
			remove(fields[y][x]);
			fields[y][x] = null;
		}
	}

	private void animate(double ms) {
		double msPerFrame = ms / FIELD_SIZE;

		Vac vMan = model.getVacMan();
		Virus[] modelVirus = model.getVirus();

		for (int i = 0; i < FIELD_SIZE; i++) {
			vacMan.move(vMan.getDir().x, vMan.getDir().y);
			for (byte j = 0; j < modelVirus.length; j++) {
				if (!modelVirus[j].isFrightened() || i % 2 == 0) {
					this.virus[j].move(modelVirus[j].getDir().x, modelVirus[j].getDir().y);
				}
			}
			JTFTools.pause(msPerFrame);
		}
	}

}

class VacBody extends GCompound {

	private boolean animation = false;

	VacBody() {
		add(new VacImage("vacman/vacman"));
	}

	void update(Vac vacMan) {
		removeAll();
		String skin = "vacman/vacman";
		if (animation && vacMan.getDir() != Direction.STILL) {
			skin += vacMan.getDir();
		}
		add(new VacImage(skin + ""));
		animation = !animation;
	}
}

class VirusBody extends GCompound {

	private final GImage skin;
	private final GImage fright = new VacImage("frightvirus");
	private final GImage eaten = new VacImage("eatenvirus");

	private boolean isFrightened = false;
	private boolean isEaten = false;

	VirusBody(String texture) {
		skin = new VacImage(texture);
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
		StringBuilder image = new StringBuilder("wall/wall");
		for (Direction dir : Direction.getArray()) {
			if (dir.mapCheck(map, y, x) && map.get((byte) (x + dir.x), (byte) (y + dir.y)) == Field.WALL) {
				image.append("1");
			} else {
				image.append("0");
			}
		}
		try {
			add(new VacImage(image.toString()));
		} catch (ErrorException e) {
			System.err.println(e.getMessage());
		}
	}
}

class Dot extends GCompound {
	Dot() {
		GImage image = new VacImage("coin");
		add(image, 7, 7);
	}
}

class Bonus extends GCompound {
	Bonus() {
		GImage image = new VacImage("bonus");
		image.scale(1.5);
		add(image, 5, 0);
	}
}

class Gate extends GCompound {
	Gate() {
		add(new VacImage("gate"));
	}
}

class LifeDisplay extends GCompound {
	private static final String IMAGE_PATH = "vacman/vacmanRight";
	private final GImage[] lives = { new VacImage(IMAGE_PATH), new VacImage(IMAGE_PATH), new VacImage(IMAGE_PATH) };
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

class VacImage extends GImage {

	static final String ASSET_PATH = "src/assets/";

	VacImage(String path) {
		super(ASSET_PATH + path + ".png");
	}
}