package org.dflib.excel;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * A SAX handler of an XLSX sheet XML that only extracts the cached cell values, ignoring formulas and everything else.
 * The values are resolved the same way as POI XSSFCell does.
 */
class XlsxSheetHandler extends DefaultHandler {

    private static final int MAX_COLUMNS = SpreadsheetVersion.EXCEL2007.getMaxColumns();

    private final XlsxWorkbookData workbook;
    private final int rowOffset;
    private final int rowLimit;

    private final SheetRange.Builder rangeBuilder;
    private final List<XlsxSheetData.XlsxRowData> rows;
    private boolean rowsOrdered;

    // [min, max, style] triplets (0-based, inclusive) of the "col" elements
    private final List<int[]> colDefinitions;
    private int[] colStyles;

    private int firstRowNum;
    private int maxRowNum;

    private boolean inCols;
    private boolean inSheetData;
    private boolean inRow;
    private boolean inCell;
    private boolean inInlineString;
    private boolean inPhoneticRun;

    // current row state
    private int rowNum;
    private int rowStyle;
    private int rowFirstCol;
    private int rowLastCol;
    private int valuesCount;
    private int[] valueCols;
    private Object[] values;

    // current cell state
    private int col;
    private String cellType;
    private String cellStyle;
    private boolean hasFormula;
    private String value;
    private boolean hasInlineString;
    private final StringBuilder inlineString;

    private final StringBuilder text;
    private boolean collectingText;

    /**
     * @param rowOffset the number of rows to skip, counting from the first non-phantom row
     * @param rowLimit  the max number of rows to retain after the offset, or a negative number for no limit
     */
    XlsxSheetHandler(XlsxWorkbookData workbook, int rowOffset, int rowLimit) {
        this.workbook = workbook;
        this.rowOffset = Math.max(rowOffset, 0);
        this.rowLimit = rowLimit;

        this.rangeBuilder = new SheetRange.Builder();
        this.rows = new ArrayList<>();
        this.rowsOrdered = true;
        this.colDefinitions = new ArrayList<>();

        this.firstRowNum = -1;
        this.maxRowNum = -1;

        this.valueCols = new int[16];
        this.values = new Object[16];
        this.inlineString = new StringBuilder();
        this.text = new StringBuilder();
    }

    XlsxSheetData sheetData() {

        XlsxSheetData.XlsxRowData[] rowsArray = rows.toArray(new XlsxSheetData.XlsxRowData[0]);

        // OOXML requires the rows to be stored in order, tolerating files that don't
        if (!rowsOrdered) {
            Arrays.sort(rowsArray, Comparator.comparingInt(r -> r.rowNum));
        }

        return new XlsxSheetData(rangeBuilder.build(), rowsArray);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {

        if (inCell) {
            switch (localName) {
                case "f" -> {
                    // like XSSFCell, not treating data table formulas as formulas
                    hasFormula = !"dataTable".equals(attributes.getValue("t"));
                }
                case "v" -> startText();
                case "is" -> {
                    inInlineString = true;
                    hasInlineString = true;
                }
                case "rPh" -> inPhoneticRun = true;
                case "t" -> {
                    if (inInlineString && !inPhoneticRun) {
                        startText();
                    }
                }
            }
        } else if (inSheetData) {
            switch (localName) {
                case "row" -> startRow(attributes);
                case "c" -> {
                    if (inRow) {
                        startCell(attributes);
                    }
                }
            }
        } else {
            switch (localName) {
                case "sheetData" -> {
                    inSheetData = true;
                    colStyles = colStyles();
                }
                case "cols" -> inCols = true;
                case "col" -> {
                    if (inCols) {
                        addColDefinition(attributes);
                    }
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {

        if (inCell) {
            switch (localName) {
                case "c" -> endCell();
                case "v" -> value = endText();
                case "t" -> {
                    if (collectingText) {
                        inlineString.append(endText());
                    }
                }
                case "rPh" -> inPhoneticRun = false;
                case "is" -> inInlineString = false;
            }
        } else if (inSheetData) {
            switch (localName) {
                case "row" -> {
                    if (inRow) {
                        endRow();
                    }
                }
                case "sheetData" -> inSheetData = false;
            }
        } else if ("cols".equals(localName)) {
            inCols = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (collectingText) {
            text.append(ch, start, length);
        }
    }

    private void startText() {
        collectingText = true;
        text.setLength(0);
    }

    private String endText() {
        collectingText = false;
        return text.toString();
    }

    private void addColDefinition(Attributes attributes) {
        int min = parseInt(attributes.getValue("min"), 0) - 1;
        int max = parseInt(attributes.getValue("max"), 0) - 1;

        // "style" defaults to 0 per OOXML schema
        int style = parseInt(attributes.getValue("style"), 0);

        if (min >= 0 && max >= min && min < MAX_COLUMNS) {
            colDefinitions.add(new int[]{min, Math.min(max, MAX_COLUMNS - 1), style});
        }
    }

    // resolves default column styles the same way POI ColumnHelper does - the first column definition (sorted by
    // min/max) that covers a given column wins
    private int[] colStyles() {

        if (colDefinitions.isEmpty()) {
            return null;
        }

        colDefinitions.sort(Comparator.<int[]>comparingInt(d -> d[0]).thenComparingInt(d -> d[1]));

        int maxCol = 0;
        for (int[] d : colDefinitions) {
            maxCol = Math.max(maxCol, d[1]);
        }

        int[] styles = new int[maxCol + 1];
        Arrays.fill(styles, -1);

        // iterate backwards, so that the earlier definitions override the later ones
        for (int i = colDefinitions.size() - 1; i >= 0; i--) {
            int[] d = colDefinitions.get(i);
            Arrays.fill(styles, d[0], d[1] + 1, d[2]);
        }

        return styles;
    }

    private void startRow(Attributes attributes) {
        inRow = true;

        // like XSSFRow, assuming no gaps if the row number is missing
        rowNum = parseInt(attributes.getValue("r"), maxRowNum + 2) - 1;
        rowStyle = parseInt(attributes.getValue("s"), -1);
        rowFirstCol = -1;
        rowLastCol = -1;
        valuesCount = 0;
    }

    private void endRow() {
        inRow = false;

        maxRowNum = Math.max(maxRowNum, rowNum);
        rangeBuilder.addRow(rowNum, rowFirstCol, rowLastCol);

        // a row with physical cells
        if (rowFirstCol >= 0 && firstRowNum < 0) {
            firstRowNum = rowNum;
        }

        // only storing rows with non-null values. Missing rows are treated as all-null rows by the loader
        if (valuesCount > 0) {

            int firstValueCol = Integer.MAX_VALUE;
            int lastValueCol = Integer.MIN_VALUE;
            for (int i = 0; i < valuesCount; i++) {
                firstValueCol = Math.min(firstValueCol, valueCols[i]);
                lastValueCol = Math.max(lastValueCol, valueCols[i]);
            }

            Object[] rowValues = new Object[lastValueCol - firstValueCol + 1];
            for (int i = 0; i < valuesCount; i++) {
                rowValues[valueCols[i] - firstValueCol] = values[i];
            }

            Arrays.fill(values, 0, valuesCount, null);

            if (!rows.isEmpty() && rows.get(rows.size() - 1).rowNum >= rowNum) {
                rowsOrdered = false;
            }

            rows.add(new XlsxSheetData.XlsxRowData(rowNum, firstValueCol, rowValues));
        }
    }

    private void startCell(Attributes attributes) {
        inCell = true;

        // like XSSFCell, placing the cell after the last cell in the row if the reference is missing
        int refCol = parseColumn(attributes.getValue("r"));
        col = refCol >= 0 ? refCol : Math.max(rowLastCol, 0);

        cellType = attributes.getValue("t");
        cellStyle = attributes.getValue("s");
        hasFormula = false;
        value = null;
        hasInlineString = false;
        inInlineString = false;
        inPhoneticRun = false;
        inlineString.setLength(0);
    }

    private void endCell() {
        inCell = false;
        collectingText = false;

        // all physical cells, including blanks, contribute to the sheet range
        rowFirstCol = rowFirstCol < 0 ? col : Math.min(rowFirstCol, col);
        rowLastCol = Math.max(rowLastCol, col + 1);

        if (isRetained()) {
            Object v = cellValue();
            if (v != null) {
                addValue(col, v);
            }
        }
    }

    // checks whether the current row will be loaded into a DataFrame. Must be called when the row is known to have
    // at least one physical cell
    private boolean isRetained() {

        long first = firstRowNum >= 0 ? firstRowNum : rowNum;
        long start = first + rowOffset;

        return rowNum >= start && (rowLimit < 0 || rowNum < start + rowLimit);
    }

    private void addValue(int col, Object value) {

        if (valuesCount == values.length) {
            int capacity = valuesCount * 2;
            values = Arrays.copyOf(values, capacity);
            valueCols = Arrays.copyOf(valueCols, capacity);
        }

        valueCols[valuesCount] = col;
        values[valuesCount] = value;
        valuesCount++;
    }

    // the logic mirrors PoiSheetData.PoiRowData.value(Cell) as applied to XSSFCell
    private Object cellValue() {

        if (cellType == null) {
            return numericValue();
        }

        return switch (cellType) {
            case "s" -> workbook.sharedString(value);
            case "str" -> value != null ? XSSFRichTextString.utfDecode(value) : "";
            case "inlineStr" -> inlineStringValue();
            case "b" -> "1".equals(value);
            case "e" -> null;

            // ISO 8601 date. POI XSSFCell doesn't support this type at all, so returning the String as is
            case "d" -> value;
            default -> numericValue();
        };
    }

    private String inlineStringValue() {

        // like XSSFCell, preferring the "is" element over the "v" element
        if (hasInlineString) {
            return XSSFRichTextString.utfDecode(inlineString.toString());
        }

        return value != null ? XSSFRichTextString.utfDecode(value) : "";
    }

    private Object numericValue() {

        // a blank cell. A formula cell without a cached value is treated as zero by POI though
        if (value == null && !hasFormula) {
            return null;
        }

        double d = value == null || value.isEmpty() ? 0. : Double.parseDouble(value.trim());

        if (DateUtil.isValidExcelDate(d) && isDateStyled()) {
            return DateUtil.getLocalDateTime(d, workbook.date1904());
        }

        return d;
    }

    // resolves the cell style the same way as XSSFCell.getCellStyle() does: explicit cell style, then column default
    // style, then row style, then the default style
    private boolean isDateStyled() {

        int numStyles = workbook.numCellStyles();
        if (numStyles == 0) {
            return false;
        }

        int style = parseInt(cellStyle, -1);
        if (style >= 0 && style < numStyles) {
            return workbook.isDateStyle(style);
        }

        if (colStyles != null && col < colStyles.length) {
            int colStyle = colStyles[col];
            if (colStyle >= 0 && colStyle < numStyles) {
                return workbook.isDateStyle(colStyle);
            }
        }

        if (rowStyle >= 0 && rowStyle < numStyles) {
            return workbook.isDateStyle(rowStyle);
        }

        return workbook.isDateStyle(0);
    }

    private static int parseInt(String string, int defaultValue) {
        if (string == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // parses a 0-based column position out of a cell reference like "AB12". Returns -1 if there's no column part
    private static int parseColumn(String ref) {

        if (ref == null) {
            return -1;
        }

        int col = 0;
        int len = ref.length();

        for (int i = 0; i < len; i++) {
            char c = ref.charAt(i);

            if (c >= 'A' && c <= 'Z') {
                col = col * 26 + (c - 'A' + 1);
            } else if (c >= 'a' && c <= 'z') {
                col = col * 26 + (c - 'a' + 1);
            } else if (c != '$') {
                break;
            }
        }

        return col - 1;
    }
}
