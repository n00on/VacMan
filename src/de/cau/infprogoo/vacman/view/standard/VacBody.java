package de.cau.infprogoo.vacman.view.standard;

import acm.graphics.GCompound;
import de.cau.infprogoo.vacman.model.Direction;
import de.cau.infprogoo.vacman.model.entity.Vac;

class VacBody extends GCompound {

    private boolean animation = false;

    VacBody() {
        add(new VacImage("vacman/vacman"));
    }

    void update(Vac vacMan) {
        removeAll();
        String skin = "vacman/vacman";
        if (animation && vacMan.getDir() != Direction.STILL) {
            skin += vacMan.getDir();
        }
        add(new VacImage(skin));
        animation = !animation;
    }
}
