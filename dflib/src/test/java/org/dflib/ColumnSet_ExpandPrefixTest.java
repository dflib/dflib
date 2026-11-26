package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.*;

public class ColumnSet_ExpandPrefixTest {

    @ParameterizedTest
    @CsvSource({"true,true", "true,false", "false,true", "false,false"})
    public void merge_UnevenAndNullRows(boolean arrays, boolean deferred) {
        DataFrame source = DataFrame.foldByRow("a", "b").of(1, "x", 2, "y", 3, "z", 4, "w");
        Exp<String[]> split = $int("a").mapVal(i -> switch (i) {
            case 1 -> new String[]{"one"};
            case 2 -> new String[]{"one", null, "three"};
            case 3 -> new String[0];
            default -> null;
        });

        DataFrame result = expand(columns(source, deferred), split, "split_", arrays).merge();

        new DataFrameAsserts(result, "a", "b", "split_0", "split_1", "split_2")
                .expectHeight(4)
                .expectRow(0, 1, "x", "one", null, null)
                .expectRow(1, 2, "y", "one", null, "three")
                .expectRow(2, 3, "z", null, null, null)
                .expectRow(3, 4, "w", null, null, null);
        new DataFrameAsserts(source, "a", "b").expectHeight(4).expectRow(0, 1, "x");
    }

    @ParameterizedTest
    @CsvSource({"true,true", "true,false", "false,true", "false,false"})
    public void select_PreservesColumnSelection(boolean arrays, boolean deferred) {
        DataFrame source = DataFrame.foldByRow("a", "b").of(1, "x");
        ColumnSet columns = deferred ? source.cols() : source.cols("split_1", "a", "split_0");
        DataFrame result = expand(columns, $val(new String[]{"one", "two"}), "split_", arrays).select();

        if (deferred) {
            new DataFrameAsserts(result, "a", "b", "split_0", "split_1")
                    .expectHeight(1).expectRow(0, 1, "x", "one", "two");
        } else {
            new DataFrameAsserts(result, "split_1", "a", "split_0")
                    .expectHeight(1).expectRow(0, "two", 1, "one");
        }
    }

    @ParameterizedTest
    @CsvSource({"true,true", "true,false", "false,true", "false,false"})
    public void merge_ExpressionByGeneratedName(boolean arrays, boolean deferred) {
        DataFrame source = DataFrame.foldByRow("a", "b").of(1, "x");
        ColumnSet columns = deferred ? source.cols() : source.cols("col1", "col2");
        DataFrame result = expand(columns, $val(new String[]{"one", "two"}), "split_", arrays)
                .merge("split_0 as col1, split_1 as col2");

        new DataFrameAsserts(result, "a", "b", "split_0", "split_1", "col1", "col2")
                .expectHeight(1).expectRow(0, 1, "x", "one", "two", "one", "two");
    }

    @ParameterizedTest
    @CsvSource({"true,true", "true,false", "false,true", "false,false"})
    public void emptyExpansion(boolean arrays, boolean deferred) {
        DataFrame source = DataFrame.foldByRow("a").of(1, 2);
        DataFrame result = expand(columns(source, deferred), $val(new String[0]), "split_", arrays).merge();
        new DataFrameAsserts(result, "a").expectHeight(2).expectRow(0, 1).expectRow(1, 2);

        DataFrame empty = DataFrame.byColumn("a").of(Series.ofInt());
        DataFrame emptyResult = expand(columns(empty, deferred), $val(new String[]{"one"}), "split_", arrays).merge();
        new DataFrameAsserts(emptyResult, "a").expectHeight(0);
    }

    @ParameterizedTest
    @CsvSource({"true,true", "true,false", "false,true", "false,false"})
    public void prefixesAndChainedExpansions(boolean arrays, boolean deferred) {
        DataFrame source = DataFrame.foldByRow("a").of(1);
        ColumnSet first = expand(columns(source, deferred), $val(new String[]{"one"}), "", arrays);
        DataFrame result = expand(first, $val(new String[]{"two"}), "列_", arrays).merge();
        new DataFrameAsserts(result, "a", "0", "列_0").expectHeight(1).expectRow(0, 1, "one", "two");

        assertThrows(IllegalArgumentException.class,
                () -> expand(first, $val(new String[]{"three"}), "", arrays));
    }

    @ParameterizedTest
    @CsvSource({"true,true", "true,false", "false,true", "false,false"})
    public void invalidPrefix(boolean arrays, boolean deferred) {
        DataFrame source = DataFrame.foldByRow("a", "split_1").of(1, "original");
        IllegalArgumentException collision = assertThrows(IllegalArgumentException.class,
                () -> expand(columns(source, deferred), $val(new String[]{"one", "two"}), "split_", arrays));
        assertTrue(collision.getMessage().contains("split_1"));
        assertThrows(NullPointerException.class,
                () -> expand(columns(source, deferred), $val(new String[0]), null, arrays));
        new DataFrameAsserts(source, "a", "split_1").expectHeight(1).expectRow(0, 1, "original");
    }

    @ParameterizedTest
    @CsvSource({"true,true", "true,false", "false,true", "false,false"})
    public void compactionIsDeferredUntilTerminalOperation(boolean arrays, boolean deferred) {
        DataFrame source = DataFrame.byColumn("a").of(Series.of(1, 2));
        AtomicInteger calls = new AtomicInteger();
        ColumnSet compacted = (deferred ? source.cols() : source.cols("split_0"))
                .compactInt((Object value) -> {
                    calls.incrementAndGet();
                    return Integer.parseInt(value.toString());
                });
        ColumnSet expanded = expand(compacted, $val(new String[]{"10"}), "split_", arrays);
        assertEquals(0, calls.get());

        DataFrame result = expanded.select();
        assertEquals(deferred ? 4 : 2, calls.get());
        if (deferred) {
            new DataFrameAsserts(result, "a", "split_0").expectHeight(2).expectIntColumns("a", "split_0")
                    .expectRow(0, 1, 10).expectRow(1, 2, 10);
        } else {
            new DataFrameAsserts(result, "split_0").expectHeight(2).expectIntColumns("split_0")
                    .expectRow(0, 10).expectRow(1, 10);
        }
    }

    @Test
    public void predicateSelectionUsesGeneratedNames() {
        DataFrame source = DataFrame.foldByRow("a", "b").of(1, "x");
        DataFrame result = source.cols(c -> c.startsWith("split_"))
                .expandArray($val(new String[]{"one", "two"}), "split_").select();
        new DataFrameAsserts(result, "split_0", "split_1").expectHeight(1).expectRow(0, "one", "two");

        DataFrame excluded = source.colsExcept("b", "split_1")
                .expandArray($val(new String[]{"one", "two"}), "split_").select();
        new DataFrameAsserts(excluded, "a", "split_0").expectHeight(1).expectRow(0, 1, "one");
    }

    private ColumnSet columns(DataFrame source, boolean deferred) {
        return deferred ? source.cols() : source.cols("a");
    }

    private ColumnSet expand(ColumnSet columns, Exp<String[]> split, String prefix, boolean arrays) {
        return arrays ? columns.expandArray(split, prefix) : columns.expand(split.mapVal(Arrays::asList), prefix);
    }
}
