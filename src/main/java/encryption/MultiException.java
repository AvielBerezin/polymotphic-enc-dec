package encryption;

import java.util.LinkedList;
import java.util.List;

public class MultiException extends Exception {
    private final List<Exception> reasons;

    public MultiException() {
        super();
        reasons = new LinkedList<>();
    }

    public MultiException(String message) {
        super(message);
        reasons = new LinkedList<>();
    }

    public MultiException addReason(Exception exception) {
        reasons.add(exception);
        return this;
    }

    public List<Exception> reasons() {
        return reasons;
    }
}
