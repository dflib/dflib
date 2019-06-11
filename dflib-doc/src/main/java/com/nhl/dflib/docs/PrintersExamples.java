package com.nhl.dflib.docs;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Printers;
import com.nhl.dflib.print.Printer;
import com.nhl.dflib.print.TabularPrinter;

import java.util.stream.IntStream;

public class PrintersExamples {

    public static void main(String[] args) {
        printDataFrame_Tabular_Custom();
    }

    private static void printDataFrame_Tabular() {

// tag::printDataFrame_Tabular[]
        DataFrame df = DataFrame
                .newFrame("col1", "col2", "col3")
                .foldIntStreamByColumn(IntStream.range(0, 10000));

        String table = Printers.tabular.toString(df);
        System.out.println(table);
// end::printDataFrame_Tabular[]
    }

    private static void printDataFrame_Tabular_Custom() {
        DataFrame df = DataFrame
                .newFrame("col1", "col2", "col3")
                .foldIntStreamByColumn(IntStream.range(0, 10000));

// tag::printDataFrame_Tabular_Custom[]
        Printer printer = new TabularPrinter(3, 3); // <1>
        String table = printer.toString(df);
        System.out.println(table);
// end::printDataFrame_Tabular_Custom[]
    }
}
