package de.cau.infprogoo.vacman.model.entity;

import de.cau.infprogoo.vacman.model.VacManModel;

/**
 * Model for virus with random behavior.
 */
public class DistanceVirus extends Virus {

    public DistanceVirus(VacManModel model) {
        super(model, 11, 0, model.getMap().rows);
    }

    /**
     * Chases after Vac and retreating when too close.
     */
    @Override
    void update() {
        Vac vacMan = model.getVacMan();

        int xDiff = getX() - vacMan.getX();
        int yDiff = getY() - vacMan.getY();
        if (Math.sqrt(xDiff * xDiff + yDiff * yDiff) > 8) {
            update(vacMan.getX(), vacMan.getY());
        } else {
            update(xHome, yHome);
        }

    }

}
