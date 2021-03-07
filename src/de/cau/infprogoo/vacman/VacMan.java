package de.cau.infprogoo.vacman;

import java.awt.Color;

import acm.program.GraphicsProgram;
import acm.util.JTFTools;

public class VacMan extends GraphicsProgram {

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
//		model.getLighthouseView().connect();
//		model.pause();

		// Game Loop
		while (true) {
			if (model.isPaused()) {
				JTFTools.pause(model.getMsperUpdate());
			} else {
				double startTime = System.nanoTime() / 1e6;
				model.update();
//				model.getLighthouseView().update(model);
				model.getView().update(model, startTime + model.getMsperUpdate() - System.nanoTime() / 1e6);
			}
		}
	}
}
