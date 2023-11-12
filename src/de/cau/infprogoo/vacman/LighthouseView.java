package de.cau.infprogoo.vacman;

import java.io.IOException;
import de.cau.infprogoo.lighthouse.LighthouseDisplay;

class LighthouseView implements VMView {
	
	static final byte ROWS = 14;
	static final byte COLUMNS = 28;

	private LighthouseDisplay display;
	private VacManModel model;
	
	private Colour[] virus = { new Colour(-1, 0, 0), new Colour(-1, 127, -1), new Colour(0, 0, -1), new Colour(-1, -60, 0)};
	
	public LighthouseView(VacManModel model) {
		this.model = model;
		connect();
	}

	void close() {
		display.close();
	}

	void connect() {
		// Try connecting to the display
		try {
			display = LighthouseDisplay.getDisplay();
			display.setUsername("josntue");////////////////////////////////////////////////////////
			display.setToken("API-TOK_O6SE-s3DE-JzEv-6Log-OIQF");////////////////////////////////
		} catch (Exception e) {
			System.out.println("Connection failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	void update() {
		// Send data to the display
		try {
			display.sendImage(toBytes(toPixels(model.getMap())));
		} catch (IOException e) {
			System.out.println("Connection failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	Colour[][] toPixels(Map map) {
		Colour[][] pixels = new Colour[14][28];
		// convert map to pixels
		for (byte i = 0; i < 14; i++) {
			for (byte j = 0; j < 28; j++) {
				switch (map.get(i, j)) {
				case WALL:
					pixels[i][j] = new Colour(30, 136, -27);// blue
					break;
				case DOT:
					pixels[i][j] = new Colour(127, 127, 0); // yellow
					break;
				case BONUS:
					pixels[i][j] = new Colour(-1, -1, -1); // white
					break;
				case GATE:
					pixels[i][j] = new Colour(70, 70, 70); // grey
					break;
				default:
					pixels[i][j] = new Colour(0, 0, 0); // black
					break;
				}
			}
		}
		// add Vac
		pixels[model.getVacMan().getY()][model.getVacMan().getX()] = new Colour(-1, -1, 0); // yellow
		// add Virus
		Virus[] virus = model.getVirus();
		for (int i = 0; i < virus.length; i++) {
			if (virus[i].isFrightened() && !(Virus.getFrightCounter() < 7 && Virus.getFrightCounter() % 2 == 0)) {
				pixels[virus[i].getY()][virus[i].getX()] = new Colour(0, -1, -1); // cyan
			} else if (!virus[i].isEaten()){
				pixels[virus[i].getY()][virus[i].getX()] = this.virus[i];
			}
		}
		return pixels;
	}

	byte[] toBytes(Colour[][] pixels) {
		// This array contains for every window (14 rows, 28 columns) three
		// bytes that define the red, green, and blue component of the color
		// to be shown in that window. See documentation of LighthouseDisplay's
		// send(...) method.
		byte[] bytes = new byte[14 * 28 * 3];
		int k = 0;
		for (int i = 0; i < 14; i++) {
			for (int j = 0; j < 28; j++) {
				bytes[k] = pixels[i][j].getRed();
				k++;
				bytes[k] = pixels[i][j].getGreen();
				k++;
				bytes[k] = pixels[i][j].getBlue();
				k++;
			}
		}
		return bytes;
	}

	class Colour {
		private final byte red;
		private final byte green;
		private final byte blue;

		/**
		 * 
		 * @param red   should be between -128 and 127
		 * @param green should be between -128 and 127
		 * @param blue  should be between -128 and 127
		 */
		Colour(int red, int green, int blue) {
			this.red = (byte) red;
			this.green = (byte) green;
			this.blue = (byte) blue;
		}

		public byte getBlue() {
			return blue;
		}

		public byte getGreen() {
			return green;
		}

		public byte getRed() {
			return red;
		}

	}

	@Override
	public void draw() {
		update();
	}

	@Override
	public void drawFields(Map map) {
		update();
	}

	@Override
	public void update(double ms) {
		update();
	}

	@Override
	public void drawEntities() {
	}

	@Override
	public void reset() {
	}

}
