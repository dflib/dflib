package org.dflib.exp.parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.dflib.exp.parser.antlr4.ExpLexer;
import org.dflib.exp.parser.antlr4.ExpStrictLexer;
import org.dflib.exp.parser.antlr4.LexerCancellationException;

import java.time.format.DateTimeParseException;
import java.util.function.Function;

/**
 * A helper to call the expression parser.
 *
 * @since 2.0.0
 */
public class ExpParserInvoker {

    /**
     * Invokes the expression parser for a given String input and a function that selects a proper parser root.
     * Implements common approach to lexer creation and error handling.
     */
    public static <T> T parse(String expStr, Function<org.dflib.exp.parser.antlr4.ExpParser, T> parserFunction) {
        ExpLexer lexer = new ExpStrictLexer(CharStreams.fromString(expStr));
        org.dflib.exp.parser.antlr4.ExpParser parser = new org.dflib.exp.parser.antlr4.ExpParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners(); // remove logging to std.err
        parser.addErrorListener(new ErrorListener());

        try {
            return parserFunction.apply(parser);
        } catch (ExpParserException e) {
            throw e;
        } catch (ParseCancellationException | LexerCancellationException | DateTimeParseException e) {
            throw new ExpParserException(e.getMessage(), e);
        } catch (Exception e) {
            throw new ExpParserException("Unexpected exception during parsing: " + e.getMessage(), e);
        }
    }

    private ExpParserInvoker() {
    }

    static class ErrorListener extends BaseErrorListener {
        @Override
        public void syntaxError(
                Recognizer<?, ?> recognizer,
                Object offendingSymbol,
                int line,
                int charPositionInLine,
                String msg,
                RecognitionException e) {
            String message = "line " + line + ":" + charPositionInLine + " " + msg;
            throw new ExpParserException(message, e);
        }
    }
}
