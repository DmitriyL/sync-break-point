package conversion7.break_point_system;

import com.jayway.awaitility.Awaitility;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

public class BreakPoint<E extends BaseBreakKey> {

    private static final Logger LOG = Utils.getLoggerForClass();

    private boolean locked;
    private String name;
    private BreakPointStorage<E> breakPoints;
    Exception stackTraceLockedOn;

    public BreakPoint(String name, BreakPointStorage<E> breakPoints) {
        this.name = name;
        this.breakPoints = breakPoints;
    }

    public boolean isLocked() {
        return locked;
    }

    public String getName() {
        return name;
    }

    public String getStackTraceLockedOn() {
        if (stackTraceLockedOn == null) {
            return "...never locked...";
        } else {
            return Utils.getStackTrace(stackTraceLockedOn);
        }
    }

    public BreakPoint lock() {
        LOG.debug("lock {}", name);
        locked = true;
        stackTraceLockedOn = new Exception();
        return this;
    }

    public BreakPoint unlock() {
        LOG.debug("unlock {}", name);
        locked = false;
        return this;
    }

    public void waitLocked(boolean b) {
        LOG.debug("waitLocked [{}] {}", b, name);
        try {
            Awaitility.waitAtMost(breakPoints.getWaitPointLockedTimeout())
                    .pollInterval(breakPoints.getPollingTimeoutMillis(), TimeUnit.MILLISECONDS)
                    .until(() -> locked == b);
        } catch (Exception e) {
            throw new BreakPointException("Breakpoint [" + name + "] waitLocked " + b + ", crashed: "
                    + e.getMessage(), e);
        }
    }
}
