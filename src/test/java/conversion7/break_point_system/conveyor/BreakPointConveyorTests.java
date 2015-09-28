package conversion7.break_point_system.conveyor;

import conversion7.break_point_system.Utils;
import org.slf4j.Logger;
import org.testng.annotations.Test;

public class BreakPointConveyorTests {

    private static final Logger LOG = Utils.getLoggerForClass();

    /** Should print dots .... and finish line with [.] */
    @Test
    public void test() {
        for (BreakKey breakKey : BreakKey.values()) {
            App.BREAK_POINTS.lock(breakKey);
        }

        Thread dots = new Thread(() -> {
            while (true) {
                App.BREAK_POINTS.waitUnlocked(BreakKey.DOT);
                System.out.print(".");
                if (!App.BREAK_POINTS.get(BreakKey.LEFT_PART).isLocked()) {
                    App.BREAK_POINTS.lock(BreakKey.LEFT_PART);
                    App.BREAK_POINTS.unlock(BreakKey.RIGHT_PART);
                    App.BREAK_POINTS.waitLocked(BreakKey.RIGHT_PART);
                }
                Utils.sleepThread(500);
            }
        });

        Thread stages = new Thread(() -> {
            int stage = 0;
            while (true) {
                if (stage == 0) {
                    App.BREAK_POINTS.waitUnlocked(BreakKey.LEFT_PART);
                    App.BREAK_POINTS.lock(BreakKey.DOT);
                    System.out.print("[");
                    stage = 1;
                    App.BREAK_POINTS.unlock(BreakKey.DOT);
                } else {
                    App.BREAK_POINTS.waitUnlocked(BreakKey.RIGHT_PART);
                    System.out.print("]");
                    System.out.print("\n");
                    stage = 0;
                    App.BREAK_POINTS.lock(BreakKey.RIGHT_PART);
                    App.BREAK_POINTS.unlock(BreakKey.MANAGER);
                }
                Utils.sleepThread(500);
            }
        });

        Thread manager = new Thread(() -> {
            int stage = 0;
            while (true) {
                App.BREAK_POINTS.waitUnlocked(BreakKey.MANAGER);

                stage++;
                if (stage == 10) {
                    App.BREAK_POINTS.lock(BreakKey.MANAGER);
                    App.BREAK_POINTS.unlock(BreakKey.LEFT_PART);
                    stage = 0;
                }

                Utils.sleepThread(Utils.RANDOM.nextInt(200) + 100);
            }
        });

        dots.start();
        stages.start();
        manager.start();

        for (int i = 0; i < 3; i++) {
            System.out.print((3 - i) + "   ");
        }
        System.out.println();

        App.BREAK_POINTS.unlock(BreakKey.DOT);
        App.BREAK_POINTS.unlock(BreakKey.MANAGER);

        while (true) {
            Utils.sleepThread(Long.MAX_VALUE);
        }
    }

}
