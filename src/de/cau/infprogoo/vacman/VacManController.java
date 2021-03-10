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
		switch (keyCode) {
		case KeyEvent.VK_SPACE:
			model.pause();
			break;
		case KeyEvent.VK_W:
		case KeyEvent.VK_UP:
			model.getVacMan().setNextDir(Direction.UP);
			break;
		case KeyEvent.VK_A:
		case KeyEvent.VK_LEFT:
			model.getVacMan().setNextDir(Direction.LEFT);
			break;
		case KeyEvent.VK_S:
		case KeyEvent.VK_DOWN:
			model.getVacMan().setNextDir(Direction.DOWN);
			break;
		case KeyEvent.VK_D:
		case KeyEvent.VK_RIGHT:
			model.getVacMan().setNextDir(Direction.RIGHT);
			break;
		// reset game
		case KeyEvent.VK_R:
			model.resetGame();
			break;
		// close lighthouseView
//		case KeyEvent.VK_X:
//			model.getLighthouseView().close();
//			break;
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
