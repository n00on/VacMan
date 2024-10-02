package de.cau.infprogoo.vacman.view.standard;

import acm.graphics.GCompound;
import acm.util.ErrorException;
import de.cau.infprogoo.vacman.model.Direction;
import de.cau.infprogoo.vacman.model.Field;
import de.cau.infprogoo.vacman.model.Map;

class Wall extends GCompound {
    Wall(Map map, byte x, byte y) {
        StringBuilder image = new StringBuilder("wall/wall");
        for (Direction dir : Direction.getArray()) {
            if (dir.mapCheck(map, y, x) && map.get((byte) (x + dir.x), (byte) (y + dir.y)) == Field.WALL) {
                image.append("1");
            } else {
                image.append("0");
            }
        }
        try {
            add(new VacImage(image.toString()));
        } catch (ErrorException e) {
            System.err.println(e.getMessage());
        }
    }
}
