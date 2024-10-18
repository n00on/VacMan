package de.cau.infprogoo.vacman.view.standard;

import acm.graphics.GCompound;
import acm.graphics.GImage;
import de.cau.infprogoo.vacman.model.entity.Virus;

class VirusBody extends GCompound {

    static final String IMAGE_GROUP = "virus/";

    private final GImage skin;
    private final GImage fright = new VacImage("frightvirus", IMAGE_GROUP);
    private final GImage eaten = new VacImage("eatenvirus", IMAGE_GROUP);

    private boolean isFrightened = false;
    private boolean isEaten = false;

    VirusBody(String texture) {
        skin = new VacImage(texture, IMAGE_GROUP);
        skin.scale(1.5);
        eaten.scale(1.5);
        fright.scale(1.5);
        add(skin);
    }

    void update(Virus virus) {
        if (isFrightened != virus.isFrightened() || isEaten != virus.isEaten()) {
            removeAll();
            if (virus.isEaten()) {
                add(eaten);
                isFrightened = false;
                isEaten = true;
            } else if (virus.isFrightened()) {
                add(fright);
                isFrightened = true;
                isEaten = false;
            } else {
                add(skin);
                isEaten = false;
                isFrightened = false;
            }
        } else if (virus.isFrightened() && Virus.getFrightCounter() < 7 && Virus.getFrightCounter() % 2 == 1) {
            remove(fright);
            add(skin);
            isFrightened = false;
        }
    }
}
