package de.cau.infprogoo.vacman.view.standard;

import acm.graphics.GCompound;
import acm.graphics.GImage;

class Dot extends GCompound {
    Dot() {
        GImage image = new VacImage("coin");
        add(image, 7, 7);
    }
}
