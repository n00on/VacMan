package de.cau.infprogoo.vacman;

import java.awt.Color;

import acm.program.GraphicsProgram;
import de.cau.infprogoo.vacman.model.VacManModel;
import de.cau.infprogoo.vacman.view.LighthouseView;
import de.cau.infprogoo.vacman.view.standard.VacManView;

public class VacMan extends GraphicsProgram {

	public static void main(String[] args) {
		new VacMan().start();
	}

	@Override
	public void run() {
		// Initializes game
		setBackground(Color.BLACK);
		
		VacManModel model = new VacManModel();
		
		VacManView view = new VacManView(model);
		model.addView(view);

		tryAddLightHouseView(model);
		
		VacManController controller = new VacManController(model);

		add(view);
		addKeyListeners(controller);
		setSize((int) view.getWidth() + 75, (int) view.getHeight() + 150);

		model.run();
	}

	void tryAddLightHouseView(VacManModel model) {
		String username = System.getenv("username");
		String token = System.getenv("token");
		try {
			LighthouseView lighthouse = new LighthouseView(model, username, token);
			model.addView(lighthouse);
		} catch (Exception e) {
			System.out.println("Connection failed: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
