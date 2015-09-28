package conversion7.break_point_system;

public class BreakPointException extends RuntimeException {

    public BreakPointException(String message) {
        super(message);
    }

    public BreakPointException(String message, Throwable cause) {
        super(message, cause);
    }

    public BreakPointException(Throwable cause) {
        super(cause);
    }

}
