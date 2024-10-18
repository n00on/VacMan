package de.cau.infprogoo.vacman.view.standard;

import de.cau.infprogoo.vacman.model.*;

public interface VMView {
    void draw();

    void drawEntities();

    void drawFields(VacMap map);

    void update(double ms);

    void reset();

    void noUpdate();
}
