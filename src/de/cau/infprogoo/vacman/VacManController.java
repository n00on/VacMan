package de.cau.infprogoo.vacman;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class VacManController implements KeyListener {
	
	private VacManModel model;
	
	VacManController(VacManModel model) {
		this.model = model;
	}

	/** Updates model. */
	void identifyKey(int keyCode) {
		if (keyCode == KeyEvent.VK_SPACE) {
			model.pause();
		} else if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) {
			model.setDirection(Direction.UP);
		} else if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
			model.setDirection(Direction.LEFT);
		} else if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) {
			model.setDirection(Direction.DOWN);
		} else if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
			model.setDirection(Direction.RIGHT);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		identifyKey(e.getExtendedKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// identifyKey(e.getExtendedKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent e) {
		identifyKey(e.getExtendedKeyCode());
	}
}
