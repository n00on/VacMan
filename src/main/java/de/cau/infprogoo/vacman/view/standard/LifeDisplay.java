package de.cau.infprogoo.vacman.view.standard;

import acm.graphics.GCompound;
import acm.graphics.GImage;
import de.cau.infprogoo.vacman.model.VacManModel;

class LifeDisplay extends GCompound {
    private static final String IMAGE_PATH = "vacman/vacmanRight";
    private final GImage[] lives = {new VacImage(IMAGE_PATH), new VacImage(IMAGE_PATH), new VacImage(IMAGE_PATH)};
    private byte lifeCount = 3;

    LifeDisplay() {
        for (byte i = 0; i < lives.length; i++) {
            add(lives[i], i * VacManView.FIELD_SIZE, 0);
        }
    }

    void update(VacManModel model) {
        if (model.getVacMan().getLives() != lifeCount && lifeCount > 0) {
            remove(lives[--lifeCount]);
        }
    }
}
