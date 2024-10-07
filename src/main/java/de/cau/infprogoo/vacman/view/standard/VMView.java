package de.cau.infprogoo.vacman.view.standard;

import de.cau.infprogoo.vacman.model.Map;

public interface VMView {
    void draw();

    void drawEntities();

    void drawFields(Map map);

    void update(double ms);

    void reset();
}
