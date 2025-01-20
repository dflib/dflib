package org.dflib.exp.parser;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.dflib.Exp;
import org.dflib.exp.parser.antlr4.ExpExtractor;
import org.dflib.exp.parser.antlr4.ExpLexer;
import org.dflib.exp.parser.antlr4.ExpStrictLexer;

public class ExpParser {

    public static Exp<?> parse(String expStr) {
        ExpLexer lexer = new ExpStrictLexer(CharStreams.fromString(expStr));
        org.dflib.exp.parser.antlr4.ExpParser parser = new org.dflib.exp.parser.antlr4.ExpParser(new CommonTokenStream(lexer));
        parser.setErrorHandler(new BailErrorStrategy());
        ExpExtractor extractor = new ExpExtractor();

        org.dflib.exp.parser.antlr4.ExpParser.RootContext context = parser.root();
        return extractor.visit(context);
    }

    private ExpParser() {
    }

}
