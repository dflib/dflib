package org.dflib.exp.parser;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$long;
import static org.dflib.Exp.$str;

public class ParserTest {

    @Disabled
    @Test
    void parse() {
        /*

        1. Should we have a separate API or something in the Expression parser to differentiate usages?

            Usages as I see right now:
             - A condition as a special case of the exp with a pretty wide representation in the API
               (and it still could be used to just eval bool as a data projection)

               Example:
                 Condition condition = Exp.cond("($int(0) > 10 or $int(a) <= 20) and $bool(c)");

             - A projection of the data
                - aggregates (and other reducers)
                - column casts and selects
                - regular functions
                - alias operator (`as`) - should we even deal with it in the grammar?
                - if/else expressions (or is it a part of conditions?)

              Examples:
                Exp<?> sum = Exp.exp("sum($long(a)) + 1");
                Exp<?> concat = Exp.exp("vconcat($str(1), ';')");

        2. Should we deal with the group by at the expression level now? no -> Full SQL
        3. Should we have parameterized expressions like in Cayenne/Agrest? yes, parameters -> :param, column
        4. Should we support user defined functions in any context, is it useful at the parser level? udf (see Udf2 interface)

        */

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x"
        );

        DataFrame df = df1.group("a").cols(1, 0).agg(
                $long("a").sum(),
                $str(1).vConcat(";"));


        Exp<?> sum = Exp.exp("sum(long(a))");
        Exp<?> concat = Exp.exp("vconcat(str(1), ';')");
        Condition condition = Exp.exp("long(a) > 10").castAsBool();

        DataFrame df2 = df1.rows(condition)
                .select()
                .group("a")
                .cols(1, 0)
                .agg(sum, concat);



    }

}
