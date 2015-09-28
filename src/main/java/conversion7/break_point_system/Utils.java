package conversion7.break_point_system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;

public class Utils {
    public static final Random RANDOM = new Random();

    public static Logger getLoggerForClass() {
        return LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static void sleepThread(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
