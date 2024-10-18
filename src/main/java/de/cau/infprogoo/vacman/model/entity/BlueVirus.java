package de.cau.infprogoo.vacman.model.entity;

import de.cau.infprogoo.vacman.model.VacManModel;

/**
 * Inky
 */
public class BlueVirus extends Virus {

    public BlueVirus(VacManModel model) {
        super(model, 17, model.getMap().columns, model.getMap().rows);
    }

    /**
     * Tries to ambush with FollowVirus.
     */
    @Override
    void update() {
        Virus blinky = model.getViruses()[0];
        Vac vacMan = model.getVacMan();
        int xDiff = vacMan.getX() + vacMan.getDir().x * 2 - blinky.getX();
        int yDiff = vacMan.getY() + vacMan.getDir().y * 2 - blinky.getY();
        update(blinky.getX() + 2 * xDiff, blinky.getY() + 2 * yDiff);
    }
}
