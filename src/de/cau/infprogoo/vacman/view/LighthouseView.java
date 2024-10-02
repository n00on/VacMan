package de.cau.infprogoo.vacman.view;

import de.cau.infprogoo.vacman.model.Map;
import de.cau.infprogoo.vacman.model.VacManModel;
import de.cau.infprogoo.vacman.model.entity.Virus;
import de.cau.infprogoo.vacman.view.standard.VMView;
import org.jighthouse.Jighthouse;

public class LighthouseView implements VMView {

    static final byte ROWS = 14;
    static final byte COLUMNS = 28;

    private final VacManModel model;

    private final Jighthouse jighthouse;

    private final Colour[] viruses = {new Colour(-1, 0, 0), new Colour(-1, 127, -1), new Colour(0, 0, -1), new Colour(-1, -60, 0)};

    public LighthouseView(VacManModel model, String username, String token) {
        jighthouse = new Jighthouse(username, token);
        this.model = model;
        jighthouse.start();
    }

    void stop() {
        jighthouse.stop();
    }

    void update() {
        // Send data to the display
        try {
            jighthouse.sendFrame(toBytes(toPixels(model.getMap())));
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    Colour[][] toPixels(Map map) {
        Colour[][] pixels = new Colour[14][28];
        // convert map to pixels
        for (byte y = 0; y < ROWS; y++) {
            for (byte x = 0; x < COLUMNS; x++) {
                pixels[y][x] = switch (map.get(y, x)) {
                    case WALL -> new Colour(30, 136, -27);// blue
                    case DOT -> new Colour(127, 127, 0); // yellow
                    case BONUS -> new Colour(-1, -1, -1); // white
                    case GATE -> new Colour(70, 70, 70); // grey
                    default -> new Colour(0, 0, 0); // black
                };
            }
        }
        // add Vac
        pixels[model.getVacMan().getY()][model.getVacMan().getX()] = new Colour(-1, -1, 0); // yellow
        // add Virus
        Virus[] virus = model.getVirus();
        for (int i = 0; i < virus.length; i++) {
            if (virus[i].isFrightened() && !(Virus.getFrightCounter() < 7 && Virus.getFrightCounter() % 2 == 0)) {
                pixels[virus[i].getY()][virus[i].getX()] = new Colour(0, -1, -1); // cyan
            } else if (!virus[i].isEaten()) {
                pixels[virus[i].getY()][virus[i].getX()] = this.viruses[i];
            }
        }
        return pixels;
    }

    byte[] toBytes(Colour[][] pixels) {
        // This array contains for every window (14 rows, 28 columns) three
        // bytes that define the red, green, and blue component of the color
        // to be shown in that window. See documentation of LighthouseDisplay's
        // send(...) method.
        byte[] bytes = new byte[ROWS * COLUMNS * 3];
        int k = 0;
        for (int x = 0; x < COLUMNS; x++) {
            for (int y = 0; y < ROWS; y++) {
                bytes[k] = pixels[y][x].getRed();
                bytes[k + 1] = pixels[y][x].getGreen();
                bytes[k + 2] = pixels[y][x].getBlue();
                k += 3;
            }
        }
        return bytes;
    }

    static class Colour {
        private final byte red;
        private final byte green;
        private final byte blue;

        /**
         * @param red   should be between -128 and 127
         * @param green should be between -128 and 127
         * @param blue  should be between -128 and 127
         */
        Colour(int red, int green, int blue) {
            this.red = (byte) red;
            this.green = (byte) green;
            this.blue = (byte) blue;
        }

        public byte getBlue() {
            return blue;
        }

        public byte getGreen() {
            return green;
        }

        public byte getRed() {
            return red;
        }

    }

    @Override
    public void draw() {
        update();
    }

    @Override
    public void drawFields(Map map) {
        update();
    }

    @Override
    public void update(double ms) {
        update();
    }

    @Override
    public void drawEntities() {
        // Ignore for lighthouse
    }

    @Override
    public void reset() {
        // Ignore for lighthouse
    }

}
