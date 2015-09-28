package conversion7.break_point_system;

import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.Duration;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BreakPointStorage<E extends BaseBreakKey> {

    private static final Logger LOG = Utils.getLoggerForClass();
    private final Map<E, BreakPoint> breakPointsStorage = new HashMap<>();
    private Duration waitCreatedTimeout = Duration.FOREVER;
    private Duration waitPointLockedTimeout = Duration.FOREVER;
    private int pollingTimeoutMillis = 5;

    public Duration getWaitCreatedTimeout() {
        return waitCreatedTimeout;
    }

    public void setWaitCreatedTimeout(Duration waitCreatedTimeout) {
        this.waitCreatedTimeout = waitCreatedTimeout;
    }

    public int getPollingTimeoutMillis() {
        return pollingTimeoutMillis;
    }

    public void setPollingTimeoutMillis(int pollingTimeoutMillis) {
        this.pollingTimeoutMillis = pollingTimeoutMillis;
    }

    public Duration getWaitPointLockedTimeout() {
        return waitPointLockedTimeout;
    }

    public void setWaitPointLockedTimeout(Duration waitPointLockedTimeout) {
        this.waitPointLockedTimeout = waitPointLockedTimeout;
    }

    public BreakPoint lock(final E key) {
        return get(key).lock();
    }

    public synchronized BreakPoint get(final E key) {
        BreakPoint breakPoint = breakPointsStorage.get(key);
        if (breakPoint == null) {
            breakPoint = new BreakPoint(key.toString(), this);
            breakPointsStorage.put(key, breakPoint);
            LOG.debug("Created {}", key);
        }
        return breakPoint;
    }

    public BreakPoint unlock(final E key) {
        return get(key).unlock();
    }

    public void waitUnlocked(E key) {
        waitCreated(key);
        get(key).waitLocked(false);
    }

    private void waitCreated(E key) {
        LOG.debug("waitCreated {}", key);
        try {
            Awaitility.waitAtMost(waitCreatedTimeout)
                    .pollInterval(pollingTimeoutMillis, TimeUnit.MILLISECONDS)
                    .until(() -> {
                        return breakPointsStorage.containsKey(key);
                    });
        } catch (Exception e) {
            throw new BreakPointException("Breakpoint [" + key + "] was not created: "
                    + e.getMessage(), e);
        }
    }

    public void waitLocked(E key) {
        waitCreated(key);
        get(key).waitLocked(true);
    }

    public void printLocks() {
        StringBuilder stringBuilder = new StringBuilder("\n\n").append(getClass().getSimpleName()).append("\n");
        for (Map.Entry<E, BreakPoint> breakPointEntry : breakPointsStorage.entrySet()) {
            BreakPoint breakPoint = breakPointEntry.getValue();
            stringBuilder.append(" ===============================================================================\n")
                    .append(breakPoint.getName()).append("\n")
                    .append("Locked: ").append(breakPoint.isLocked()).append("\n")
                    .append("Last lock: ").append(breakPoint.getStackTraceLockedOn()).append("\n\n");
        }

        stringBuilder.append(" ===============================================================================");
        LOG.info(stringBuilder.toString());
    }
}
