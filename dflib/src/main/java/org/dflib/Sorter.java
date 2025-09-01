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
     * @param ql     a QL String to parse representing a sort expression
     * @param params an optional list of parameters to substitute into the parsed sorter
     * @since 2.0.0
     */
    static Sorter parseSorter(String ql, Object... params) {
        return QLParserInvoker.parse(ql, ExpParser::sorterRoot, params).sorter;
    }

    /**
     * Parses the provided sort expression String into an array of sorters.
     *
     * @param ql     a QL String to parse representing one or more comma-separated sort expressions
     * @param params an optional list of parameters to substitute into the parsed sorters
     * @since 2.0.0
     */
    static Sorter[] parseSorters(String ql, Object... params) {
        return QLParserInvoker.parse(ql, ExpParser::sorterArray, params).sorters;
    }

    IntComparator eval(DataFrame df);

    IntComparator eval(Series<?> s);
}
