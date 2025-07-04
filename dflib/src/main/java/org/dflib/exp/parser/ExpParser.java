package org.dflib.exp.parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.dflib.Exp;
import org.dflib.Sorter;
import org.dflib.exp.parser.antlr4.ExpLexer;
import org.dflib.exp.parser.antlr4.ExpStrictLexer;
import org.dflib.exp.parser.antlr4.LexerCancellationException;

import java.time.format.DateTimeParseException;
import java.util.function.Function;

/**
 * @since 2.0.0
 */
public class ExpParser {

    /**
     * Parses a string into an {@link Exp} object.
     *
     * @param expStr the string representation of the expression to parse.
     * @return an {@link Exp} object representing the parsed expression.
     * @throws ExpParserException if the input string contains errors.
     */
    public static Exp<?> parse(String expStr) {
        return internalParse(expStr, parser -> parser.root().exp);
    }

    /**
     * Parses a string into an {@link Sorter} object.
     *
     * @param sorterStr the string representation of the sorting spec to parse.
     * @return an {@link Sorter} object representing the parsed sorter.
     * @throws ExpParserException if the input string contains errors.
     */
    public static Sorter parseSorter(String sorterStr) {
        return internalParse(sorterStr, parser -> parser.sorterRoot().sorter);
    }

    private static <T> T internalParse(String expStr, Function<org.dflib.exp.parser.antlr4.ExpParser, T> parserFunction) {
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

    private ExpParser() {
    }

    static class ErrorListener extends BaseErrorListener {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                int line, int charPositionInLine, String msg, RecognitionException e) {
            String message = "line " + line + ":" + charPositionInLine + " " + msg;
            throw new ExpParserException(message, e);
        }
    }
}
