package com.nhl.dflib.builder;

import com.nhl.dflib.Index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @since 0.16
 */
public abstract class BaseDataFrameBuilder {

    protected final Index columnsIndex;

    protected BaseDataFrameBuilder(Index columnsIndex) {
        this.columnsIndex = columnsIndex;
    }

    protected <T> Collection<T> toCollection(Iterable<T> iterable) {

        if (iterable instanceof Collection) {
            return (Collection) iterable;
        }

        List<T> values = new ArrayList<>();
        iterable.forEach(values::add);
        return values;
    }
}
