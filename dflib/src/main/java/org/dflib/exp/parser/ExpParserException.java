package org.dflib.exp.parser;

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
