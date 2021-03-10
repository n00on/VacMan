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
		
		VacManView view = new VacManView(model);
		model.addView(view);
//		LighthouseView lighthouse = new LighthouseView(model);
//		model.addView(lighthouse);
		
		VacManController controller = new VacManController(model);

		add(view);
		addKeyListeners(controller);
		setSize((int) view.getWidth() + 75, (int) view.getHeight() + 150);

		model.run();
	}
}
