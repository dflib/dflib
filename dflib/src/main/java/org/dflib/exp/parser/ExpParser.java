package org.dflib.exp.parser;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.dflib.Exp;
import org.dflib.exp.parser.antlr4.ExpExtractor;
import org.dflib.exp.parser.antlr4.ExpLexer;
import org.dflib.exp.parser.antlr4.ExpStrictLexer;
import org.dflib.exp.parser.antlr4.LexerCancellationException;

import java.time.format.DateTimeParseException;

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
        parser.setErrorHandler(new BailErrorStrategy());
        ExpExtractor extractor = new ExpExtractor();

        try {
            org.dflib.exp.parser.antlr4.ExpParser.RootContext context = parser.root();
            return extractor.visit(context);
        } catch (ParseCancellationException | LexerCancellationException | DateTimeParseException e) {
            throw new ExpParserException("Error parsing exception: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ExpParserException("Unexpected exception during parsing: " + e.getMessage(), e);
        }
    }

    private ExpParser() {
    }
}
