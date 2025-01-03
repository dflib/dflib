package org.dflib.exp.parser.antlr4;

import java.util.concurrent.CancellationException;

public class LexerCancellationException extends CancellationException {

    public LexerCancellationException() {
    }

    public LexerCancellationException(String message) {
        super(message);
    }

    public LexerCancellationException(Throwable cause) {
        initCause(cause);
    }

    public LexerCancellationException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
