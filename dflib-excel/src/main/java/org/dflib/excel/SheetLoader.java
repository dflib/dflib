package org.dflib.excel;

import org.apache.poi.ss.usermodel.Sheet;
import org.dflib.DataFrame;
import org.dflib.Extractor;
import org.dflib.Index;
import org.dflib.builder.DataFrameAppender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides loading options for a single sheet in the context of {@link ExcelLoader}.
 */
public class SheetLoader {

    private Boolean firstRowAsHeader;
    private Integer offset;
    private Integer limit;
    private final List<ColConfigurator> colConfigurators;

    public static SheetLoader of() {
        return new SheetLoader();
    }

    protected SheetLoader() {
        this.colConfigurators = new ArrayList<>();
    }

    SheetLoader mergeWith(SheetLoader another) {

        SheetLoader sl = new SheetLoader();
        sl.firstRowAsHeader = this.firstRowAsHeader != null ? this.firstRowAsHeader : another.firstRowAsHeader;
        sl.offset = this.offset != null ? this.offset : another.offset;
        sl.limit = this.limit != null ? this.limit : another.limit;

        // TODO: not merging colConfigurators just yet... We don't need to do it when merging defaults from ExcelLoader,
        //  as the Workbook-level SheetLoader has no colConfigurators, but eventually we may need to figure it out.
        sl.colConfigurators.addAll(this.colConfigurators);

        return sl;
    }

    public SheetLoader firstRowAsHeader() {
        this.firstRowAsHeader = true;
        return this;
    }

    /**
     * Skips the specified number of rows. This counter only applies to non-phantom rows. I.e., those rows that have
     * non-empty cells. Phantom rows are skipped automatically.
     */
    public SheetLoader offset(int len) {
        this.offset = len;
        return this;
    }

    /**
     * Limits the max number of rows to the provided value. This counter only applies to non-phantom rows. I.e., those
     * rows that have non-empty cells. Phantom rows are skipped automatically.
     */
    public SheetLoader limit(int len) {
        this.limit = len;
        return this;
    }


    /**
     * Configures an Excel column to be loaded with value compaction. Should be used to save memory for low-cardinality
     * columns. Note that Excel already compacts String columns without the need to call this method.
     *
     * @param column 0-based column position in the source Excel sheet.
     */
    public SheetLoader compactCol(int column) {
        colConfigurators.add(ColConfigurator.objectCol(column, true));
        return this;
    }

    /**
     * Configures a CSV column to be loaded with value compaction. Should be used to save memory for low-cardinality
     * columns. Note that Excel already compacts String columns without the need to call this method.
     */
    public SheetLoader compactCol(String column) {
        colConfigurators.add(ColConfigurator.objectCol(column, true));
        return this;
    }

    public DataFrame load(Sheet sheet) {
        return load(new PoiSheetData(sheet));
    }

    DataFrame load(SheetData sheet) {

        boolean firstRowAsHeader = isFirstRowAsHeader();

        // Don't skip empty rows or columns in the middle of a range, but truncate leading empty rows and columns
        SheetRange range = sheet.valuesRange().offset(rowOffset()).limit(rowLimit());
        if (range.width == 0) {
            return DataFrame.empty();
        }

        if (range.height == 0) {
            return DataFrame.empty(range.header());
        }

        RowData row0 = sheet.row(range.startRow);
        Index index = firstRowAsHeader && row0 != null ? range.header(row0) : range.header();
        Extractor<RowData, ?>[] extractors = extractors(range, index);

        DataFrameAppender<RowData> builder = DataFrame.byRow(extractors).columnIndex(index).appender();

        if (!firstRowAsHeader) {
            builder.append(row0);
        }

        for (int r = 1; r < range.height; r++) {
            builder.append(sheet.row(range.startRow + r));
        }

        return builder.toDataFrame();
    }

    private boolean isFirstRowAsHeader() {
        return firstRowAsHeader != null ? firstRowAsHeader : false;
    }

    /**
     * Returns the number of rows to skip, counting from the first non-phantom row of the sheet.
     */
    int rowOffset() {
        return offset != null ? offset : 0;
    }

    /**
     * Returns the max number of rows to read after the offset (including the header row), or -1 for no limit.
     */
    int rowLimit() {
        return limit != null && limit >= 0 ? (isFirstRowAsHeader() ? limit + 1 : limit) : -1;
    }

    private Extractor<RowData, ?>[] extractors(SheetRange range, Index dfHeader) {

        Map<Integer, ColConfigurator> configurators = new HashMap<>();
        for (ColConfigurator c : colConfigurators) {

            int sheetPos = c.srcPos(range, dfHeader);

            // ignore out of range columns
            if (sheetPos >= 0) {
                // later configs override earlier configs at the same position
                configurators.put(sheetPos, c);
            }
        }

        Extractor<RowData, ?>[] extractors = new Extractor[range.width];
        for (int i = 0; i < range.width; i++) {
            int sheetPos = range.startCol + i;
            ColConfigurator cc = configurators.computeIfAbsent(sheetPos, ii -> ColConfigurator.objectCol(ii, false));
            extractors[i] = cc.extractor(sheetPos);
        }

        return extractors;
    }
}
