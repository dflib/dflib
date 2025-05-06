package org.dflib.exp.parser.antlr4;

import org.dflib.Exp;

public class ExpExtractor extends ExpBaseVisitor<Exp<?>> {

    @Override
    public Exp<?> visitRoot(ExpParser.RootContext ctx) {
        return ctx.exp;
    }
}
