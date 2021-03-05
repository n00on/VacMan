package de.cau.infprogoo.vacman;

import java.io.IOException;
import de.cau.infprogoo.lighthouse.LighthouseDisplay;

// TODO In Bearbeitung LightHouse

class LighthouseView {

	private LighthouseDisplay display = null;
	private VacManModel model;

	void close() {
		display.close();
	}

	void connect() {
		// Try connecting to the display
		try {
			display = LighthouseDisplay.getDisplay();
			display.setUsername("daniel");////////////////////////////////////////////////////////
			display.setToken("API-TOK_ZrbM-9vUU-VC9K-lyJC-aP/S");////////////////////////////////
		} catch (Exception e) {
			System.out.println("Connection failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	void update(VacManModel model) {
		// Send data to the display
		try {
			this.model = model;
			display.sendImage(toBytes(toPixels(model.getMap())));
		} catch (IOException e) {
			System.out.println("Connection failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	Colour[][] toPixels(Fields[][] map) {
		Colour[][] pixels = new Colour[14][28];
		// convert map to pixels
		for (int i = 0; i < 14; i++) {
			for (int j = 0; j < 28; j++) {
				switch (map[i][j]) {
				case WALL:
					pixels[i][j] = new Colour(0, 0, -1);// blue
					break;
				case DOT:
					pixels[i][j] = new Colour(0, -1, 0); // green
					break;
				case BONUS:
					pixels[i][j] = new Colour(-1, -1, -1); // white
					break;
				case GATE:
					pixels[i][j] = new Colour(127, 127, 127); // grey
					break;
				case EMPTY:
					pixels[i][j] = new Colour(0, 0, 0); // black
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
		for (Virus vir : model.getVirus()) {
			if (vir.isFrightened()) {
				pixels[vir.getY()][vir.getX()] = new Colour(0, -1, -1); // cyan
			} else {
				pixels[vir.getY()][vir.getX()] = new Colour(-1, 0, 0); // red
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

}
