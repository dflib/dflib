package org.dflib;

import org.dflib.row.RowProxy;

// TODO: deprecate? It is the same as an Exp that references multiple columns, but creates its own evaluation path.
//  Research how to make multi-column expressions easier to write (similar to "mapVal(Exp<> other, BiFunction<> op)"
//  but with an arbitrary number of arguments.
@FunctionalInterface
public interface RowToValueMapper<V> {

    V map(RowProxy row);
}
