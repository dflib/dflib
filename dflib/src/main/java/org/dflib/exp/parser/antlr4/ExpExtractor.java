package org.dflib.exp.parser.antlr4;

import org.dflib.Exp;

/**
 * @since 2.0.0
 */
public class ExpExtractor extends ExpBaseVisitor<Exp<?>> {

    @Override
    public Exp<?> visitRoot(ExpParser.RootContext ctx) {
        return ctx.exp;
    }
}
