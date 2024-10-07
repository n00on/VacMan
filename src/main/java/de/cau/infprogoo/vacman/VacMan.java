package de.cau.infprogoo.vacman;

import java.awt.Color;

import acm.program.GraphicsProgram;
import de.cau.infprogoo.vacman.model.VacManModel;
import de.cau.infprogoo.vacman.view.LighthouseView;
import de.cau.infprogoo.vacman.view.standard.VacManView;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class VacMan extends GraphicsProgram {

	private static final Logger logger = LogManager.getLogger(VacMan.class);

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
		String username = System.getenv("LH_USERNAME");
		String token = System.getenv("LH_TOKEN");
		if (token == null || username == null) {
			logger.warn("Missing credentials for Jighthouse connection.");
			return;
		}
		try {
			LighthouseView lighthouse = new LighthouseView(model, username, token);
			model.addView(lighthouse);
		} catch (Exception e) {
			logger.error("Connection failed: ", e);
		}
	}
}
