package de.cau.infprogoo.vacman.view.standard;

import acm.graphics.GCompound;
import acm.graphics.GImage;

class Bonus extends GCompound {
    Bonus() {
        GImage image = new VacImage("bonus");
        image.scale(1.5);
        add(image, 5, 0);
    }
}
