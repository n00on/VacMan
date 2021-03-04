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
		VacManModel model = new VacManModel();
		VacManController controller = new VacManController(model);

		setSize((int) model.getView().getWidth() + 75, (int) model.getView().getHeight() + 150);
		add(model.getView());
		addKeyListeners(controller);
		model.getLighthouseView().connect();

		// Game Loop
		while (true) {
			if (model.isPaused()) {
				JTFTools.pause(MS_PER_UPDATE);
			} else {
				double startTime = System.nanoTime() / 1e6;
				model.update();
				model.getLighthouseView().update(model,startTime + MS_PER_UPDATE - System.nanoTime() / 1e6);
				model.getView().update(model, startTime + MS_PER_UPDATE - System.nanoTime() / 1e6);
			}
		}
	}
}
