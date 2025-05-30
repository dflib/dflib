package org.dflib.exp.parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.dflib.Exp;
import org.dflib.exp.parser.antlr4.ExpExtractor;
import org.dflib.exp.parser.antlr4.ExpLexer;
import org.dflib.exp.parser.antlr4.ExpStrictLexer;
import org.dflib.exp.parser.antlr4.LexerCancellationException;

import java.time.format.DateTimeParseException;

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
        ExpLexer lexer = new ExpStrictLexer(CharStreams.fromString(expStr));
        org.dflib.exp.parser.antlr4.ExpParser parser = new org.dflib.exp.parser.antlr4.ExpParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners(); // remove logging to std.err
        parser.addErrorListener(new ErrorListener());
        ExpExtractor extractor = new ExpExtractor();

        try {
            org.dflib.exp.parser.antlr4.ExpParser.RootContext context = parser.root();
            return extractor.visit(context);
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
