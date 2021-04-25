package com.nhl.dflib.benchmark.memory;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.benchmark.ValueMaker;
import com.nhl.dflib.benchmark.memory.benchmark.MemoryTest;

import java.util.BitSet;

public class ColumnarDataFrameMemory extends MemoryTest {

    private static final int ROWS = 1_000_000;

    public static void main(String[] args) {

        int cells = ROWS * 2;

        ColumnarDataFrameMemory test = new ColumnarDataFrameMemory();
        test.run("null", test::nullCells, cells);
        test.run("int (object)", test::intCells, cells);
        test.run("int (primitive)", test::primitiveIntCells, cells);
        test.run("double (object)", test::doubleCells, cells);
        test.run("double (primitive)", test::primitiveDoubleCells, cells);
        test.run("long (object)", test::longCells, cells);
        test.run("long (primitive)", test::primitiveLongCells, cells);
        test.run("boolean (object)", test::boolCells, cells);
        test.run("boolean (primitive)", test::primitiveBoolCells, cells);
        test.run("string (repeating)", test::repeatingStringCells, cells);
        test.run("string (random)", test::randStringCells, cells);
        test.run("bitSet", test::bitSetCells, cells);
        test.run("enum", test::enumCells, cells);
    }

    public DataFrame nullCells() {
        return DataFrame.newFrame("c0", "c1").columns(
                ValueMaker.nullSeq().series(ROWS),
                ValueMaker.nullSeq().series(ROWS)).materialize();
    }

    public DataFrame doubleCells() {
        return DataFrame.newFrame("c0", "c1").columns(
                ValueMaker.doubleSeq().series(ROWS),
                ValueMaker.doubleSeq().series(ROWS)).materialize();
    }

    public DataFrame primitiveDoubleCells() {
        return DataFrame.newFrame("c0", "c1").columns(
                ValueMaker.doubleSeq().doubleSeries(ROWS),
                ValueMaker.doubleSeq().doubleSeries(ROWS)).materialize();
    }

    public DataFrame intCells() {
        return DataFrame.newFrame("c0", "c1").columns(
                ValueMaker.intSeq().series(ROWS),
                ValueMaker.intSeq().series(ROWS)).materialize();
    }

    public DataFrame primitiveIntCells() {
        return DataFrame.newFrame("c0", "c1").columns(
                ValueMaker.intSeq().intSeries(ROWS),
                ValueMaker.intSeq().intSeries(ROWS)).materialize();
    }

    public DataFrame longCells() {
        return DataFrame.newFrame("c0", "c1").columns(
                ValueMaker.longSeq().series(ROWS),
                ValueMaker.longSeq().series(ROWS)).materialize();
    }

    public DataFrame primitiveLongCells() {
        return DataFrame.newFrame("c0", "c1").columns(
                ValueMaker.longSeq().longSeries(ROWS),
                ValueMaker.longSeq().longSeries(ROWS)).materialize();
    }

    public DataFrame boolCells() {
        return DataFrame.newFrame("c0", "c1").columns(
                ValueMaker.booleanSeq().series(ROWS),
                ValueMaker.booleanSeq().series(ROWS)).materialize();
    }

    public DataFrame primitiveBoolCells() {
        return DataFrame.newFrame("c0", "c1").columns(
                ValueMaker.booleanSeq().booleanSeries(ROWS),
                ValueMaker.booleanSeq().booleanSeries(ROWS)).materialize();
    }

    public DataFrame repeatingStringCells() {
        return DataFrame.newFrame("c0", "c1").columns(
                ValueMaker.constStringSeq("abc").series(ROWS),
                ValueMaker.constStringSeq("xyz").series(ROWS)).materialize();
    }

    public DataFrame randStringCells() {
        return DataFrame.newFrame("c0", "c1").columns(
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

        return DataFrame.newFrame("c0", "c1").columns(
                bitsMaker.series(ROWS),
                bitsMaker.series(ROWS)).materialize();
    }

    public DataFrame enumCells() {
        return DataFrame.newFrame("c0", "c1").columns(
                ValueMaker.enumSeq(X.class).series(ROWS),
                ValueMaker.enumSeq(X.class).series(ROWS)).materialize();
    }

    enum X {
        a, b, c, d, e, f, g
    }
}
