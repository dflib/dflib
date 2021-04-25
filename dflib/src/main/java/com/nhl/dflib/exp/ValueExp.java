package com.nhl.dflib.exp;

import com.nhl.dflib.Condition;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.condition.BinaryCondition;

/**
 * @since 0.11
 */
public interface ValueExp<V> extends Exp<V> {

    default Condition eq(Exp<?> exp) {
        return new BinaryCondition<>("eq", this, exp, Series::eq);
    }

    default Condition ne(Exp<?> exp) {
        return new BinaryCondition<>("ne", this, exp, Series::ne);
    }
}
