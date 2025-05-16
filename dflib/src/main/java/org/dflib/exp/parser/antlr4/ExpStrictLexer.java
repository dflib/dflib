package org.dflib.exp.parser.antlr4;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.RecognitionException;

/**
 * @since 2.0.0
 */
public class ExpStrictLexer extends ExpLexer {

    public ExpStrictLexer(CharStream input) {
        super(input);
    }

    @Override
    public void recover(LexerNoViableAltException e) {
        throw new LexerCancellationException(e);
    }

    @Override
    public void recover(RecognitionException re) {
        throw new LexerCancellationException(re);
    }
}
