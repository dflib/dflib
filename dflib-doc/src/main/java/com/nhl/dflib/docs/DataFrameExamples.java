package com.nhl.dflib.docs;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Printers;
import com.nhl.dflib.Series;

import java.util.stream.IntStream;

public class DataFrameExamples {

    public static void main(String[] args) {
        createFromIntStream();
    }

    private static void createFoldByRow() {
// tag::createFoldByRow[]
        DataFrame df = DataFrame
                .newFrame("name", "age") // <1>
                .foldByRow("Joe", 18, "Andrus", 45, "Joan", 32); // <2>
// end::createFoldByRow[]

        System.out.println(Printers.tabular.toString(df));
    }

    private static void createFoldByColumn() {
// tag::createFoldByColumn[]
        DataFrame df = DataFrame
                .newFrame("name", "age")
                .foldByColumn("Joe", "Andrus", "Joan", 18, 45, 32);
// end::createFoldByColumn[]

        System.out.println(Printers.tabular.toString(df));
    }

    private static void createFromIntStream() {
// tag::createFromIntStream[]
        DataFrame df = DataFrame
                .newFrame("col1", "col2")
                .foldIntStreamByColumn(IntStream.range(0, 10000));
// end::createFromIntStream[]

        System.out.println(Printers.tabular.toString(df));
    }

    private static void createFromSeries() {
// tag::createFromSeries[]
        DataFrame df = DataFrame
                .newFrame("name", "age")
                .columns(
                        Series.forData("Joe", "Andrus", "Joan"),
                        IntSeries.forInts(18, 45, 32)
                );
// end::createFromSeries[]

        System.out.println(Printers.tabular.toString(df));
    }
}
