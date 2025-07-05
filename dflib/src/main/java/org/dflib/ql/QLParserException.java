package org.dflib.ql;

/**
 * @since 2.0.0
 */
public class QLParserException extends RuntimeException {

    public QLParserException() {
    }

    public QLParserException(String message) {
        super(message);
    }

    public QLParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public QLParserException(Throwable cause) {
        super(cause);
    }
}
