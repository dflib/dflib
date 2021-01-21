package com.nhl.dflib.avro;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.LongSeries;
import com.nhl.dflib.Series;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AvroSaverTest {

    @TempDir
    static File destination;

    static final DataFrame df = DataFrame.newFrame("int", "Integer", "long", "Long")
            .columns(
                    IntSeries.forInts(1, 2),
                    Series.forData(Integer.valueOf(11), null),
                    LongSeries.forLongs(Long.MAX_VALUE - 1L, Long.MIN_VALUE + 1L),
                    Series.forData(null, Long.valueOf(21L))
            );

    @Test
    public void testSave() {
        File out = new File(destination, "s1.avro");
        new AvroSaver().save(df, out);
        assertTrue(out.exists());
    }
}
