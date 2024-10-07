package de.cau.infprogoo.vacman.model;

import java.util.ArrayList;

import acm.util.JTFTools;
import de.cau.infprogoo.vacman.model.entity.*;
import de.cau.infprogoo.vacman.view.standard.VMView;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class VacManModel {


    private static final Logger logger = LogManager.getLogger(VacManModel.class);

    /**
     * MVC Views implementing the VMView interface.
     */
    private final ArrayList<VMView> views = new ArrayList<>();

    /**
     * Entities and Map.
     */
    private Vac vacMan;
    private final Virus[] virus;
    private Map map;

    /**
     * Booleans determining update behavior.
     */
    private boolean paused = false;
    private boolean resetPositions = false;
    private boolean newLevel = false;
    private boolean resetGame = false;

    private int msPerUpdate = 220;
    private int level = 1;
    private int score = 0;

    public VacManModel() {
        initMap();
        vacMan = new Vac(this);
        this.virus = new Virus[]{new FollowVirus(this), new PredictVirus(this), new BlueVirus(this),
                new DistanceVirus(this)};
    }

    /**
     * Game Loop.
     */
    public void run() {
        while (true) {
            if (paused) {
                JTFTools.pause(msPerUpdate);
            } else {
                double startTime = System.nanoTime() / 1e6;
                update();
                for (VMView view : views) {
                    view.update(startTime + msPerUpdate - System.nanoTime() / 1e6);
                }
            }
        }
    }

    ArrayList<VMView> getViews() {
        return views;
    }

    public void addView(VMView view) {
        views.add(view);
        view.draw();
    }

    public Vac getVacMan() {
        return vacMan;
    }

    public Virus[] getVirus() {
        return virus;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public void pause() {
        paused = !paused;
    }

    public Map getMap() {
        return map;
    }

    /**
     * Reset vac man and viruses in next update
     */
    public void resetPositions() {
        resetPositions = true;
    }

    public void resetGame() {
        paused = false;
        resetGame = true;
    }

    public void addScore(int scorePlus) {
        score += scorePlus;
        if (scorePlus > 100) {
            logger.debug("100+ points: {}", scorePlus);
        }
    }

    /**
     * Updates the entire game state.
     */
    void update() {

        if (resetPositions || newLevel || resetGame) {
            reset();
            return;
        }

        vacMan.update();
        Virus.updateAll(virus);
        map.update(vacMan.getX(), vacMan.getY());

        if (map.getDotCounter() == 0) {
            newLevel = true;
        }
    }

    private void reset() {
        vacMan.reset();
        Virus.resetAll(virus);

        if (newLevel) {
            level++;
            msPerUpdate -= msPerUpdate / 10;
            Virus.decreaseFrightTime();
            initMap();
            for (VMView view : views) {
                view.draw();
            }
        } else if (resetGame) {
            level = 1;
            msPerUpdate = 200;
            Virus.resetFrightTime();
            initMap();
            score = 0;
            vacMan = new Vac(this);
            for (VMView view : views) {
                view.reset();
            }
        } else {
            for (VMView view : views) {
                view.drawEntities();
            }
        }

        resetPositions = false;
        newLevel = false;
        resetGame = false;
    }

    /**
     * Initiates standard map.
     */
    private void initMap() {
        this.map = Map.getMap(this);
    }

    /**
     * Game Over map.
     */
    public void gameOver() {
        resetPositions = true;
        paused = true;

        byte[] virusXStart = {13, 14};
        this.map = new Map(this, Map.getGameOver(), 13, 11, virusXStart, 6, 4);
        for (VMView view : views) {
            view.drawFields(this.map);
        }
    }
}

