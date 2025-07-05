package org.dflib.ql;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.dflib.ql.antlr4.ExpLexer;
import org.dflib.ql.antlr4.ExpStrictLexer;
import org.dflib.ql.antlr4.ExpParser;
import org.dflib.ql.antlr4.LexerCancellationException;

import java.time.format.DateTimeParseException;
import java.util.function.Function;

/**
 * A helper to call the expression parser.
 *
 * @since 2.0.0
 */
public class QLParserInvoker {

    /**
     * Invokes the expression parser for a given String input and a function that selects a proper parser root.
     * Implements common approach to lexer creation and error handling.
     */
    public static <T> T parse(String expStr, Function<ExpParser, T> parserFunction) {
        ExpLexer lexer = new ExpStrictLexer(CharStreams.fromString(expStr));
        ExpParser parser = new ExpParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners(); // remove logging to std.err
        parser.addErrorListener(new ErrorListener());

        try {
            return parserFunction.apply(parser);
        } catch (QLParserException e) {
            throw e;
        } catch (ParseCancellationException | LexerCancellationException | DateTimeParseException e) {
            throw new QLParserException(e.getMessage(), e);
        } catch (Exception e) {
            throw new QLParserException("Unexpected exception during parsing: " + e.getMessage(), e);
        }
    }

    private QLParserInvoker() {
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
            throw new QLParserException(message, e);
        }
    }
}
