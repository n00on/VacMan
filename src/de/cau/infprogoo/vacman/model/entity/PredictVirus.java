package de.cau.infprogoo.vacman.model.entity;

import de.cau.infprogoo.vacman.model.Direction;
import de.cau.infprogoo.vacman.model.VacManModel;

/**
 * Model for virus which follows the player directly.
 */
public class PredictVirus extends Virus {

    /**
     * (last) Direction of Vac.
     */
    private Direction vacDir = DIR_START;

    public PredictVirus(VacManModel model) {
        super(model, 2, 0, 0);
    }

    /**
     * Sets players coordinates as goal cell.
     */
    @Override
    void update() {
        Vac vacMan = model.getVacMan();
        if (vacMan.getDir() != Direction.STILL) {
            vacDir = vacMan.getDir();
        }
        update(vacMan.getX() + vacDir.x * 4, vacMan.getY() + vacDir.y * 4);
    }

    @Override
    void reset() {
        vacDir = Direction.STILL;
        super.reset();
    }
}
