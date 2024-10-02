package de.cau.infprogoo.vacman.view.standard;

import acm.graphics.GImage;

class VacImage extends GImage {

    static final String ASSET_PATH = "src/assets/";

    VacImage(String path) {
        super(ASSET_PATH + path + ".png");
    }
}
