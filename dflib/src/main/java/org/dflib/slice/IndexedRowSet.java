package org.dflib.slice;

import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.IntSeries;
import org.dflib.RowColumnSet;
import org.dflib.RowMapper;
import org.dflib.Series;
import org.dflib.Sorter;
import org.dflib.row.ColumnsRowProxy;
import org.dflib.row.MultiArrayRowBuilder;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * A {@link org.dflib.RowSet} based on {@link IntSeries} row selection index.
 *
 * @since 1.0.0-M19
 */
public class IndexedRowSet extends BaseRowSet {

    private final IntSeries intIndex;

    public IndexedRowSet(DataFrame source, Series<?>[] sourceColumns, IntSeries intIndex) {
        super(source, sourceColumns);
        this.intIndex = intIndex;
    }

    @Override
    public RowColumnSet cols() {
        return new DefaultRowColumnSet(source, this, df -> df.cols(), this::merger);
    }

    @Override
    public RowColumnSet cols(String... columns) {
        return new DefaultRowColumnSet(source, this, df -> df.cols(columns), this::merger);
    }

    @Override
    public RowColumnSet cols(Index columnsIndex) {
        return new DefaultRowColumnSet(source, this, df -> df.cols(columnsIndex), this::merger);
    }

    @Override
    public RowColumnSet cols(int... columns) {
        return new DefaultRowColumnSet(source, this, df -> df.cols(columns), this::merger);
    }

    @Override
    public RowColumnSet cols(Predicate<String> condition) {
        return new DefaultRowColumnSet(source, this, df -> df.cols(condition), this::merger);
    }

    @Override
    public RowColumnSet colsExcept(String... columns) {
        return new DefaultRowColumnSet(source, this, df -> df.colsExcept(columns), this::merger);
    }

    @Override
    public RowColumnSet colsExcept(int... columns) {
        return new DefaultRowColumnSet(source, this, df -> df.colsExcept(columns), this::merger);
    }

    @Override
    public DataFrame drop() {

        // build an inverted Boolean condition

        int srcLen = source.height();
        boolean[] condition = new boolean[srcLen];
        Arrays.fill(condition, true);

        int iiLen = intIndex.size();
        for (int i = 0; i < iiLen; i++) {
            condition[intIndex.getInt(i)] = false;
        }

        return new ConditionalRowSet(source, sourceColumns, Series.ofBool(condition)).select();
    }

    @Override
    public DataFrame sort(Sorter... sorters) {
        if (intIndex.size() < 2) {
            return source;
        }

        return super.sort(sorters);
    }

    @Override
    protected int size() {
        return intIndex.size();
    }

    @Override
    protected void doSelectByRow(RowMapper mapper, ColumnsRowProxy from, MultiArrayRowBuilder to) {
        int ih = intIndex.size();

        // Replace a subset with the mapper-produced values.
        // To ensure mapper correctness (including correctness of its possible side effects), preserve row
        // selection order and even duplicates when invoking the "mapper"

        for (int i = 0; i < ih; i++) {
            from.next(intIndex.getInt(i));
            to.next(i);
            mapper.map(from, to);
        }
    }

    @Override
    protected <T> Series<T> doSelect(Series<T> sourceColumn) {
        return sourceColumn.select(intIndex);
    }

    @Override
    protected RowSetMapper mapper() {
        return RowSetMapper.of(intIndex);
    }

    @Override
    protected RowSetMerger merger() {
        return RowSetMerger.of(source.height(), intIndex);
    }
}
