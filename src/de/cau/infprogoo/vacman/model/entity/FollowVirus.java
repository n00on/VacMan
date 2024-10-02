package de.cau.infprogoo.vacman.model.entity;

import de.cau.infprogoo.vacman.model.VacManModel;

/**
 * Model for virus which follows the player directly.
 */
public class FollowVirus extends Virus {

    public FollowVirus(VacManModel model) {
        super(model, 7, model.getMap().columns, 0);
    }

    /**
     * Sets players coordinates as goal cell.
     */
    @Override
    void update() {
        update(model.getVacMan().getX(), model.getVacMan().getY());
    }
}
