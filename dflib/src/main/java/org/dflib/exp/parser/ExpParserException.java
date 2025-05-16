package org.dflib.exp.parser;

/**
 * @since 2.0.0
 */
public class ExpParserException extends RuntimeException {

    public ExpParserException() {
    }

    public ExpParserException(String message) {
        super(message);
    }

    public ExpParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpParserException(Throwable cause) {
        super(cause);
    }
}
