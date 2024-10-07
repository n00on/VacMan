package de.cau.infprogoo.vacman.model.entity;

import de.cau.infprogoo.vacman.model.Direction;
import de.cau.infprogoo.vacman.model.Field;
import de.cau.infprogoo.vacman.model.VacMap;
import de.cau.infprogoo.vacman.model.VacManModel;

/**
 * Model for Vac-Man.
 */
public class Vac extends Entity {

    private Direction nextDir = DIR_START;
    private byte lives = 3;

    public Vac(VacManModel model) {
        super(model, model.getMap().vacXStart, model.getMap().vacYStart);
    }

    public byte getLives() {
        return lives;
    }

    public void setNextDir(Direction dir) {
        nextDir = dir;
    }

    public void reset() {
        nextDir = DIR_START;
        super.reset(model.getMap().vacXStart, model.getMap().vacYStart);
    }

    @Override
    public void update() {

        VacMap map = model.getMap();
        if (map.get(nextDir.nextX(map, getX()), nextDir.nextY(map, getY())).value >= Field.EMPTY.value) {
            setDir(nextDir);
        }

        super.update();
    }

    /**
     * Checks if vac and a virus collided.
     */
    void checkHit(Virus virus) {
        // If Hit
        if (getX() == virus.getX() && getY() == virus.getY() || //
                getX() + getDir().getOpposite().x == virus.getX() && getY() + getDir().getOpposite().y == virus.getY()
                        && virus.getX() + virus.getDir().getOpposite().x == getX()
                        && virus.getY() + virus.getDir().getOpposite().y == getY()) {
            // losing a life
            if (!virus.isFrightened()) {
                if (--lives == 0) {
                    model.gameOver();
                } else {
                    model.resetPositions();
                }
                // or eating the ghost
            } else {
                virus.eat();
            }
        }
    }
}
