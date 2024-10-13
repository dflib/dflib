package org.dflib.benchmark.memory;

import org.dflib.DataFrame;
import org.dflib.benchmark.ValueMaker;
import org.dflib.benchmark.memory.benchmark.MemoryTest;

import java.util.BitSet;

public class ColumnarDataFrameMemory extends MemoryTest {

    private static final int ROWS = 1_000_000;

    public static void main(String[] args) {

        int cells = ROWS * 2;

        ColumnarDataFrameMemory test = new ColumnarDataFrameMemory();
        test.run("null", test::nullCells, cells);
        test.run("int (object)", test::intCells, cells);
        test.run("int (object, repeating)", test::repeatingIntCells, cells);
        test.run("int (primitive)", test::primitiveIntCells, cells);
        test.run("float (object)", test::floatCells, cells);
        test.run("float (primitive)", test::primitiveFloatCells, cells);
        test.run("double (object)", test::doubleCells, cells);
        test.run("double (primitive)", test::primitiveDoubleCells, cells);
        test.run("long (object)", test::longCells, cells);
        test.run("long (primitive)", test::primitiveLongCells, cells);
        test.run("boolean (object)", test::boolCells, cells);
        test.run("boolean (primitive)", test::primitiveBoolCells, cells);
        test.run("string (repeating)", test::repeatingStringCells, cells);
        test.run("string (interned)", test::internedStringCells, cells);
        test.run("string (random)", test::randStringCells, cells);
        test.run("bitSet", test::bitSetCells, cells);
        test.run("enum", test::enumCells, cells);
    }

    public DataFrame nullCells() {
        return DataFrame.byColumn("c0", "c1").of(
                ValueMaker.nullSeq().series(ROWS),
                ValueMaker.nullSeq().series(ROWS)).materialize();
    }

    public DataFrame floatCells() {
        return DataFrame.byColumn("c0", "c1").of(
                ValueMaker.floatSeq().series(ROWS),
                ValueMaker.floatSeq().series(ROWS)).materialize();
    }

    public DataFrame primitiveFloatCells() {
        return DataFrame.byColumn("c0", "c1").of(
                ValueMaker.floatSeq().floatSeries(ROWS),
                ValueMaker.floatSeq().floatSeries(ROWS)).materialize();
    }

    public DataFrame doubleCells() {
        return DataFrame.byColumn("c0", "c1").of(
                ValueMaker.doubleSeq().series(ROWS),
                ValueMaker.doubleSeq().series(ROWS)).materialize();
    }

    public DataFrame primitiveDoubleCells() {
        return DataFrame.byColumn("c0", "c1").of(
                ValueMaker.doubleSeq().doubleSeries(ROWS),
                ValueMaker.doubleSeq().doubleSeries(ROWS)).materialize();
    }

    public DataFrame intCells() {
        return DataFrame.byColumn("c0", "c1").of(
                ValueMaker.intSeq().series(ROWS),
                ValueMaker.intSeq().series(ROWS)).materialize();
    }

    public DataFrame repeatingIntCells() {
        return DataFrame.byColumn("c0", "c1").of(
                ValueMaker.constSeq(999, Integer::valueOf).series(ROWS),
                ValueMaker.constSeq(13_555_101, Integer::valueOf).series(ROWS)).materialize();
    }

    public DataFrame primitiveIntCells() {
        return DataFrame.byColumn("c0", "c1").of(
                ValueMaker.intSeq().intSeries(ROWS),
                ValueMaker.intSeq().intSeries(ROWS)).materialize();
    }

    public DataFrame longCells() {
        return DataFrame.byColumn("c0", "c1").of(
                ValueMaker.longSeq().series(ROWS),
                ValueMaker.longSeq().series(ROWS)).materialize();
    }

    public DataFrame primitiveLongCells() {
        return DataFrame.byColumn("c0", "c1").of(
                ValueMaker.longSeq().longSeries(ROWS),
                ValueMaker.longSeq().longSeries(ROWS)).materialize();
    }

    public DataFrame boolCells() {
        return DataFrame.byColumn("c0", "c1").of(
                ValueMaker.booleanSeq().series(ROWS),
                ValueMaker.booleanSeq().series(ROWS)).materialize();
    }

    public DataFrame primitiveBoolCells() {
        return DataFrame.byColumn("c0", "c1").of(
                ValueMaker.booleanSeq().booleanSeries(ROWS),
                ValueMaker.booleanSeq().booleanSeries(ROWS)).materialize();
    }

    public DataFrame repeatingStringCells() {
        return DataFrame.byColumn("c0", "c1").of(
                ValueMaker.constSeq("abc", s -> new String(s.toCharArray())).series(ROWS),
                ValueMaker.constSeq("xyz", s -> new String(s.toCharArray())).series(ROWS)).materialize();
    }

    public DataFrame internedStringCells() {
        return DataFrame.byColumn("c0", "c1").of(
                ValueMaker.constSeq("abc", s -> new String(s.toCharArray()).intern()).series(ROWS),
                ValueMaker.constSeq("xyz", s -> new String(s.toCharArray()).intern()).series(ROWS)).materialize();
    }

    public DataFrame randStringCells() {
        return DataFrame.byColumn("c0", "c1").of(
                ValueMaker.semiRandomStringSeq("abc", ROWS).series(ROWS),
                ValueMaker.semiRandomStringSeq("xyz", ROWS).series(ROWS)).materialize();
    }

    public DataFrame bitSetCells() {
        ValueMaker<Integer> intMaker = ValueMaker.intSeq(0, 1024);
        ValueMaker<BitSet> bitsMaker = () -> {
            BitSet s = new BitSet();
            s.set(intMaker.get());
            return s;

        };

        return DataFrame.byColumn("c0", "c1").of(
                bitsMaker.series(ROWS),
                bitsMaker.series(ROWS)).materialize();
    }

    public DataFrame enumCells() {
        return DataFrame.byColumn("c0", "c1").of(
                ValueMaker.enumSeq(X.class).series(ROWS),
                ValueMaker.enumSeq(X.class).series(ROWS)).materialize();
    }

    enum X {
        a, b, c, d, e, f, g
    }
}
