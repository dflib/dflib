package com.nhl.dflib.docs;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Printers;
import com.nhl.dflib.Series;

public class SeriesExamples {

    public static void main(String[] args) {
        createInt();
    }

    private static void create() {
        Series<String> s = Series.forData("a", "bcd", "ef", "g");

        System.out.println(Printers.tabular.toString(s));
    }

    private static void createInt() {
        IntSeries is = IntSeries.forInts(0, 1, -300, Integer.MAX_VALUE);

        System.out.println(Printers.tabular.toString(is));
    }
}
