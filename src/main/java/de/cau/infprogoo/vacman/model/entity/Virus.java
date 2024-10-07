package de.cau.infprogoo.vacman.model.entity;

import acm.util.RandomGenerator;
import de.cau.infprogoo.vacman.model.Direction;
import de.cau.infprogoo.vacman.model.Field;
import de.cau.infprogoo.vacman.model.VacMap;
import de.cau.infprogoo.vacman.model.VacManModel;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Direct superclass for individual virus.
 */
public abstract class Virus extends Entity {

    private static final Logger logger = LogManager.getLogger(Virus.class);

    private static boolean frighten = false;
    private static byte frightTime = 26;
    private static byte frightCounter = 0;
    private static byte killStreak = 0;

    private static final byte[] phaseTimes = {14, 36, 14, 36, 10, 36, 10, -1};
    private static byte phase = 0;
    private static byte phaseCounter = phaseTimes[phase];

    public static void updateAll(Virus[] viruses) {

        if (frighten) {
            frightenAll(viruses);
        } else if (frightCounter > 0) {
            if (--frightCounter == 0) {
                killStreak = 0;
                for (Virus vir : viruses) {
                    vir.isFrightened = false;
                }
            }
        } else if (phaseCounter == 0) {
            changePhase(viruses);
        } else if (phaseCounter > 0) {
            // Only counts if frightCounter == 0
            phaseCounter--;
        }

        for (Virus vir : viruses) {
            if (!vir.isFrightened || frightCounter % 2 == 0) {
                vir.update();
            }
            if (!vir.isEaten) {
                vir.model.getVacMan().checkHit(vir);
            }
        }
    }

    public static void frightenAll(Virus[] viruses) {
        frighten = false;
        killStreak = 0;
        frightCounter = (byte) (frightTime + frightCounter % 2);
        for (Virus virus : viruses) {
            if (!virus.isEaten) {
                if (!virus.isFrightened) {
                    virus.setDir(virus.getDir().getOpposite());
                }
                virus.isFrightened = true;
            }
        }
    }

    static void changePhase(Virus[] viruses) {
        for (Virus virus : viruses) {
            if (!virus.isEaten && !virus.isFrightened) {
                virus.setDir(virus.getDir().getOpposite());
            }
        }
        phaseCounter = phaseTimes[++phase];
        logger.debug("Phase: {}", phase);
    }

    public static void resetAll(Virus[] virus) {
        killStreak = 0;
        frightCounter = 0;
        phase = 0;
        phaseCounter = phaseTimes[phase];

        for (Virus vir : virus) {
            vir.reset();
        }
    }

    /**
     * Frightens in next update.
     */
    public static void frighten() {
        frighten = true;
    }

    public static byte getFrightCounter() {
        return frightCounter;
    }

    public static void decreaseFrightTime() {
        if (frightTime > 0) {
            frightTime -= 2;
        }
    }

    public static void resetFrightTime() {
        frightTime = 26;
    }

    /**
     * Home corner to retreat/scatter.
     */
    final byte xHome;
    final byte yHome;


    /**
     * Start offset to get out.
     */
    private final int outTime;
    /**
     * Is out of the starting box? (Is > 0)
     */
    private int outCounter;
    private boolean isEaten;
    private boolean isFrightened;

    Virus(VacManModel model, int outTime, int xHome, int yHome) {
        super(model, model.getMap().getVirusStartX(), model.getMap().virusYStart);
        this.xHome = (byte) xHome;
        this.yHome = (byte) yHome;
        this.outTime = outTime;
        reset();
    }

    void eat() {
        killStreak += 1;
        model.addScore((int) Math.pow(2, killStreak) * 100);

        isEaten = true;
    }

    public boolean isFrightened() {
        return isFrightened;
    }

    public boolean isEaten() {
        return isEaten;
    }

    void reset() {
        isEaten = false;
        isFrightened = false;
        outCounter = 2 + outTime;
        super.reset(model.getMap().getVirusStartX(), model.getMap().virusYStart);
    }

    /**
     * Updates the direction and field for lowest distance to the goal field.
     */
    void update(int xGoal, int yGoal) {
        VacMap map = model.getMap();
        byte x = getX();
        byte y = getY();

        // If in start room
        if (outCounter == 0 && map.get((byte) (x + Direction.UP.x), (byte) (y + Direction.UP.y)) == Field.GATE) {
            update(Direction.UP);
            return;
        } else if (outCounter > 0) {
            outCounter--;
        }

        // Random behavior if frightened
        if (isEaten) {
            isFrightened = false;
            if (map.get(x, y) == Field.GATE || map.get((byte) (x + Direction.DOWN.x), (byte) (y + Direction.DOWN.y)) == Field.GATE) {
                if (map.get(x, y) == Field.GATE) {
                    isEaten = false;
                    outCounter = 3;
                }
                update(Direction.DOWN);
                return;
            }
            xGoal = model.getMap().getVirusStartX();
            yGoal = model.getMap().virusYBack;
        } else if (isFrightened) {
            RandomGenerator rgen = new RandomGenerator();
            xGoal = rgen.nextInt(map.columns);
            yGoal = rgen.nextInt(map.rows);
        } else if (phase % 2 == 0) { // SCATTER
            xGoal = xHome;
            yGoal = yHome;
        }

        this.setNextDir(xGoal, yGoal);

        super.update();
    }

    private void setNextDir(int xGoal, int yGoal) {
        VacMap map = model.getMap();
        byte x = getX();
        byte y = getY();

        double distance = Integer.MAX_VALUE;
        Direction dir = getDir();

        // Finds the next field (not in the opposite direction) closest to
        // the goal field
        for (Direction newDir : Direction.getArray()) {
            if (newDir != dir.getOpposite()
                    && map.get(newDir.nextX(map, x), newDir.nextY(map, y)).value >= Field.EMPTY.value) {
                int yDiff = newDir.nextY(map, y) - yGoal;
                int xDiff = newDir.nextX(map, x) - xGoal;
                double newDistance = Math.sqrt(yDiff * yDiff + xDiff * xDiff);
                if (newDistance < distance) {
                    setDir(newDir);
                    distance = newDistance;
                }
            }
        }
    }
}
