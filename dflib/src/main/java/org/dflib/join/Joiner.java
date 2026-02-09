package org.dflib.join;

import org.dflib.DataFrame;
import org.dflib.JoinType;
import org.dflib.Series;

import java.util.Objects;

/**
 * @since 2.0.0
 */
public abstract class Joiner {

    protected final JoinType type;

    protected Joiner(JoinType type) {
        this.type = Objects.requireNonNull(type);
    }

    public abstract Series<?>[] buildColumns(
            DataFrame leftFrame,
            DataFrame rightFrame,
            String indicatorColumn,
            int[] positions);
}
