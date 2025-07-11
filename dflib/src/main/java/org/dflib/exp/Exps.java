package org.dflib.exp;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Index;

public class Exps {

    public static Index index(Exp<?>... exps) {
        return Index.ofDeduplicated(labels(exps));
    }

    public static Index index(DataFrame df, Exp<?>... exps) {
        return Index.ofDeduplicated(labels(df, exps));
    }

    public static String[] labels(Exp<?>... exps) {

        int w = exps.length;
        switch (w) {
            case 0:
                return new String[0];
            case 1:
                return new String[]{exps[0].getColumnName()};
            default:
                String[] labels = new String[w];
                for (int i = 0; i < w; i++) {
                    labels[i] = exps[i].getColumnName();
                }

                return labels;
        }
    }

    public static String[] labels(DataFrame df, Exp<?>... exps) {

        int w = exps.length;
        switch (w) {
            case 0:
                return new String[0];
            case 1:
                return new String[]{exps[0].getColumnName(df)};
            default:
                String[] labels = new String[w];
                for (int i = 0; i < w; i++) {
                    labels[i] = exps[i].getColumnName(df);
                }

                return labels;
        }
    }

}
