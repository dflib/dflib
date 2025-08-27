package org.dflib;

import org.dflib.ql.QLParserInvoker;
import org.dflib.ql.antlr4.ExpParser;
import org.dflib.sort.IntComparator;

/**
 * An expression derivative for sorting of Series, DataFrames, etc.
 */
public interface Sorter {

    /**
     * Parses the provided sort expression String into a sorter.
     *
     * @since 2.0.0
     */
    static Sorter parseSorter(String sorterExp, Object... params) {
        return QLParserInvoker.parse(sorterExp, ExpParser::sorterRoot, params).sorter;
    }

    /**
     * Parses the provided sort expression String into an array of sorters.
     *
     * @since 2.0.0
     */
    static Sorter[] parseSorterArray(String sorterArrayExp, Object... params) {
        return QLParserInvoker.parse(sorterArrayExp, ExpParser::sorterArray, params).sorters;
    }

    IntComparator eval(DataFrame df);

    IntComparator eval(Series<?> s);
}
