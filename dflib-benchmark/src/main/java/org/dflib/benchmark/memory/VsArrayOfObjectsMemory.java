package org.dflib.benchmark.memory;

import org.dflib.DataFrame;
import org.dflib.benchmark.ValueMaker;
import org.dflib.benchmark.memory.benchmark.MemoryTest;

import java.util.ArrayList;
import java.util.List;

public class VsArrayOfObjectsMemory extends MemoryTest {

    private static final int ROWS = 1_000_000;

    static class TestIntRow {
        final Integer i1;
        final Integer i2;

        TestIntRow(Integer i1, Integer i2) {
            this.i1 = i1;
            this.i2 = i2;
        }
    }

    static class TestIntPrimitiveRow {
        final int i1;
        final int i2;

        TestIntPrimitiveRow(int i1, int i2) {
            this.i1 = i1;
            this.i2 = i2;
        }
    }

    public static void main(String[] args) {

        int cells = ROWS * 2;

        VsArrayOfObjectsMemory test = new VsArrayOfObjectsMemory();
        test.run("Integer DataFrame", test::intCells, cells);
        test.run("Integer List<..>", test::intCellsAsObjectList, cells);
        test.run("Integer Object[]", test::intCellsAsObjectArray, cells);

        test.run("int DataFrame", test::intPrimitiveCells, cells);
        test.run("int List<..>", test::intPrimitiveCellsAsObjectList, cells);
        test.run("int Object[]", test::intPrimitiveCellsAsObjectArray, cells);
    }

    public DataFrame intCells() {
        return DataFrame.byColumn("c0", "c1").of(
                ValueMaker.intSeq().series(ROWS),
                ValueMaker.intSeq().series(ROWS)).materialize();
    }

    public DataFrame intPrimitiveCells() {
        return DataFrame.byColumn("c0", "c1").of(
                ValueMaker.intSeq().intSeries(ROWS),
                ValueMaker.intSeq().intSeries(ROWS)).materialize();
    }

    public List<TestIntRow> intCellsAsObjectList() {

        ValueMaker<Integer> i1s = ValueMaker.intSeq();
        ValueMaker<Integer> i2s = ValueMaker.intSeq();
        List<TestIntRow> rows = new ArrayList<>(ROWS);
        for (int i = 0; i < ROWS; i++) {
            rows.add(new TestIntRow(i1s.get(), i2s.get()));
        }

        return rows;
    }

    public TestIntRow[] intCellsAsObjectArray() {

        ValueMaker<Integer> i1s = ValueMaker.intSeq();
        ValueMaker<Integer> i2s = ValueMaker.intSeq();
        TestIntRow[] rows = new TestIntRow[ROWS];
        for (int i = 0; i < ROWS; i++) {
            rows[i] = new TestIntRow(i1s.get(), i2s.get());
        }

        return rows;
    }

    public List<TestIntPrimitiveRow> intPrimitiveCellsAsObjectList() {

        ValueMaker<Integer> i1s = ValueMaker.intSeq();
        ValueMaker<Integer> i2s = ValueMaker.intSeq();
        List<TestIntPrimitiveRow> rows = new ArrayList<>(ROWS);
        for (int i = 0; i < ROWS; i++) {
            rows.add(new TestIntPrimitiveRow(i1s.get(), i2s.get()));
        }

        return rows;
    }

    public TestIntPrimitiveRow[] intPrimitiveCellsAsObjectArray() {

        ValueMaker<Integer> i1s = ValueMaker.intSeq();
        ValueMaker<Integer> i2s = ValueMaker.intSeq();
        TestIntPrimitiveRow[] rows = new TestIntPrimitiveRow[ROWS];
        for (int i = 0; i < ROWS; i++) {
            rows[i] = new TestIntPrimitiveRow(i1s.get(), i2s.get());
        }

        return rows;
    }
}
