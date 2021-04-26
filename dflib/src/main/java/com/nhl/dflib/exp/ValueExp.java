package com.nhl.dflib.exp;

import com.nhl.dflib.Condition;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.condition.BinaryCondition;
import com.nhl.dflib.exp.condition.UnaryCondition;

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

    default Condition eq(Object value) {
        return value != null
                ? new BinaryCondition<>("eq", this, Exp.$val(value), Series::eq)
                : isNull();
    }

    default Condition ne(Object value) {
        return value != null
                ? new BinaryCondition<>("ne", this, Exp.$val(value), Series::ne)
                : isNotNull();
    }

    default Condition isNull() {
        return new UnaryCondition<>("isNull", this, Series::isNull);
    }

    default Condition isNotNull() {
        return new UnaryCondition<>("isNotNull", this, Series::isNotNull);
    }
}
