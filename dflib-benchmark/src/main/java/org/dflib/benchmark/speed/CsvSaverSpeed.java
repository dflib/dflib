package org.dflib.benchmark.speed;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.QuoteMode;
import org.dflib.DataFrame;
import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.benchmark.ValueMaker;
import org.dflib.csv.Csv;
import org.dflib.csv.CsvSaver;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(2)
@State(Scope.Thread)
public class CsvSaverSpeed {

    @Param("1000000")
    public int rows;

    @Param({"default", "quoteAll", "backslash", "multiCharDelimiter"})
    public String format;

    private DataFrame df;
    private CsvSaver saver;
    private File gzipFile;

    @Setup
    @SuppressWarnings("deprecation")
    public void setUp() throws IOException {

        IntSeries c1 = ValueMaker.intSeq().intSeries(rows);
        Series<String> c2 = ValueMaker.stringSeq().series(rows);

        // ~10% of values need quoting
        Series<String> c3 = ValueMaker.repeatedSeq(Function.identity(),
                "a,b", "x\"y", "l1\nl2",
                "v01", "v02", "v03", "v04", "v05", "v06", "v07", "v08", "v09", "v10",
                "v11", "v12", "v13", "v14", "v15", "v16", "v17", "v18", "v19", "v20",
                "v21", "v22", "v23", "v24", "v25", "v26", "v27").series(rows);

        df = DataFrame.byColumn("c1", "c2", "c3").of(c1, c2, c3);

        // using the commons-csv format, so that the benchmark can be compared with the commons-csv based saver
        CSVFormat csvFormat = switch (format) {
            case "default" -> CSVFormat.DEFAULT;
            case "quoteAll" -> CSVFormat.DEFAULT.builder().setQuoteMode(QuoteMode.ALL).build();
            case "backslash" -> CSVFormat.DEFAULT.builder().setEscape('\\').build();
            case "multiCharDelimiter" -> CSVFormat.DEFAULT.builder().setDelimiter("||").build();
            default -> throw new IllegalArgumentException("Unknown format: " + format);
        };

        saver = Csv.saver().format(csvFormat);
        gzipFile = Files.createTempFile("CsvSaverSpeed", ".csv.gz").toFile();
    }

    @TearDown
    public void tearDown() {
        gzipFile.delete();
    }

    @Benchmark
    public Object saveToString() {
        return saver.saveToString(df);
    }

    @Benchmark
    public void saveToOutputStream() {
        saver.save(df, OutputStream.nullOutputStream());
    }

    @Benchmark
    public void saveToGzipFile() {
        saver.save(df, gzipFile);
    }
}
