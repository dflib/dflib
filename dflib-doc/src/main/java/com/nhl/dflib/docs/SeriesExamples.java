package com.nhl.dflib.docs;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Printers;
import com.nhl.dflib.Series;

public class SeriesExamples {

    public static void main(String[] args) {
        createInt();
    }

    private static void create() {
// tag::create[]
        Series<String> s = Series.forData("a", "bcd", "ef", "g");
// end::create[]

        System.out.println(Printers.tabular.toString(s));
    }

    private static void createInt() {
// tag::createInt[]
        IntSeries is = IntSeries.forInts(0, 1, -300, Integer.MAX_VALUE);
// end::createInt[]

        System.out.println(Printers.tabular.toString(is));
    }
}
