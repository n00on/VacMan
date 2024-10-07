package de.cau.infprogoo.vacman.view.standard;

import java.awt.Color;

import acm.graphics.GCompound;
import acm.graphics.GLabel;
import acm.util.JTFTools;
import de.cau.infprogoo.vacman.model.*;
import de.cau.infprogoo.vacman.model.entity.Vac;
import de.cau.infprogoo.vacman.model.entity.Virus;

// TODO animations

public class VacManView extends GCompound implements VMView {

    VacManModel model;

    static final int FIELD_OFFSET = 50;
    static final int FIELD_SIZE = 30;

    /**
     * Map of Field.
     */
    private GCompound[][] fields;
    /**
     * View of Vac-Man.
     */
    private final VacBody vacMan = new VacBody();
    /**
     * Array of Ghost bodies.
     */
    private final VirusBody[] virus = {
            new VirusBody("redvirus"), new VirusBody("pinkvirus"),
            new VirusBody("bluevirus"), new VirusBody("orangevirus")};

    private final GLabel scoreDisplay;
    private LifeDisplay lifeDisplay = new LifeDisplay();

    public VacManView(VacManModel model) {
        this.model = model;
        scoreDisplay = new GLabel("L1 SCORE: 0");
        scoreDisplay.setColor(Color.WHITE);
        scoreDisplay.setFont("Default-20");
        draw();
    }

    public void reset() {
        remove(lifeDisplay);
        lifeDisplay = new LifeDisplay();
        draw();
    }

    /**
     * Draws the view.
     */
    public void draw() {

        drawFields(model.getMap());
        add(lifeDisplay, FIELD_OFFSET / 2, 10);
        drawEntities();

    }

    public void drawFields(VacMap map) {

        removeAll();

        fields = new GCompound[model.getMap().rows][model.getMap().columns];

        for (byte x = 0; x < map.columns; x++) {
            for (byte y = 0; y < map.rows; y++) {
                GCompound field =
                        switch (map.get(x, y)) {
                            case WALL -> new Wall(map, x, y);
                            case DOT -> new Dot();
                            case BONUS -> new Bonus();
                            case GATE -> new Gate();
                            case EMPTY -> null;
                        };
                if (field != null) {
                    fields[y][x] = field;
                    add(field, (double) FIELD_OFFSET / 2 + FIELD_SIZE * x, FIELD_OFFSET + FIELD_SIZE * y);
                }
            }
        }
        add(scoreDisplay, this.getWidth() - 5 * FIELD_SIZE, 30);
    }

    public void drawEntities() {
        Virus[] modelVirus = model.getVirus();
        for (int i = 0; i < modelVirus.length; i++) {
            int x = FIELD_OFFSET / 2 + FIELD_SIZE * modelVirus[i].getX();
            int y = FIELD_OFFSET + FIELD_SIZE * modelVirus[i].getY();
            add(this.virus[i], x, y);
        }

        Vac vMan = model.getVacMan();
        add(vacMan, FIELD_OFFSET / 2 + FIELD_SIZE * vMan.getX(), FIELD_OFFSET + FIELD_SIZE * vMan.getY());
    }

    public void update(double ms) {
        scoreDisplay.setLabel("L" + model.getLevel() + " SCORE: " + model.getScore());
        lifeDisplay.update(model);

        Virus[] modelVirus = model.getVirus();
        for (int i = 0; i < modelVirus.length; i++) {
            this.virus[i].update(modelVirus[i]);
        }

        Vac vMan = model.getVacMan();
        vacMan.update(vMan);

        animate(ms);

        if (vMan.isTunneling()) {
            add(vacMan, FIELD_OFFSET / 2 + FIELD_SIZE * vMan.getX(), FIELD_OFFSET + FIELD_SIZE * vMan.getY());
        }

        for (int i = 0; i < modelVirus.length; i++) {
            if (modelVirus[i].isTunneling()) {
                add(this.virus[i], FIELD_OFFSET / 2 + FIELD_SIZE * modelVirus[i].getX(), FIELD_OFFSET + FIELD_SIZE * modelVirus[i].getY());
            }
        }

        byte x = model.getVacMan().getX();
        byte y = model.getVacMan().getY();
        if (fields[y][x] != null && model.getMap().get(x, y).value >= Field.EMPTY.value) {
            remove(fields[y][x]);
            fields[y][x] = null;
        }
    }

    private void animate(double ms) {
        double msPerFrame = ms / FIELD_SIZE;

        Vac vMan = model.getVacMan();
        Virus[] modelVirus = model.getVirus();

        for (int i = 0; i < FIELD_SIZE; i++) {
            vacMan.move(vMan.getDir().x, vMan.getDir().y);
            for (byte j = 0; j < modelVirus.length; j++) {
                if (!modelVirus[j].isFrightened() || i % 2 == 0) {
                    this.virus[j].move(modelVirus[j].getDir().x, modelVirus[j].getDir().y);
                }
            }
            JTFTools.pause(msPerFrame);
        }
    }

}

