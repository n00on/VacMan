package de.cau.infprogoo.vacman;

import java.awt.Color;

import acm.program.GraphicsProgram;
import acm.util.JTFTools;

public class VacMan extends GraphicsProgram {

	/** The frequency of game updates. */
	static final int MS_PER_UPDATE = 333;

	public static void main(String[] args) {
		new VacMan().start();
	}

	public void run() {
		// Initializes game
		setBackground(Color.BLACK);
		VacManView view = new VacManView();
		VacManModel model = new VacManModel(view);
		VacManController controller = new VacManController(model);

		setSize((int) view.getWidth() + 75, (int) view.getHeight() + 150);

		add(view);
		addKeyListeners(controller);

		// Game Loop
		while (true) {
			if (model.isPaused()) {
				JTFTools.pause(MS_PER_UPDATE);
			} else {
				double startTime = System.nanoTime() / 1e6;
				model.update();
				view.update(model, startTime + MS_PER_UPDATE - System.nanoTime() / 1e6);
			}
		}
	}
}
