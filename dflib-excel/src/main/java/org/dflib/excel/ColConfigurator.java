package org.dflib.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.dflib.Extractor;
import org.dflib.Index;

class ColConfigurator {

    int srcColPos;
    String srcColName;
    boolean compact;

    private ColConfigurator() {
        srcColPos = -1;
    }

    public static ColConfigurator objectCol(int pos, boolean compact) {
        ColConfigurator config = new ColConfigurator();
        config.srcColPos = pos;
        config.compact = compact;
        return config;
    }

    public static ColConfigurator objectCol(String name, boolean compact) {
        ColConfigurator config = new ColConfigurator();
        config.srcColName = name;
        config.compact = compact;
        return config;
    }

    int srcPos(SheetRange range, Index dfHeader) {
        return srcColPos >= 0 ? srcColPos : (dfHeader.contains(srcColName) ? range.startCol + dfHeader.position(srcColName) : -1);
    }

    Extractor<Row, ?> extractor(int srcPos) {
        Extractor<Row, ?> e = Extractor.$col(r -> value(r, srcPos));
        return compact ? e.compact() : e;
    }

    static Object value(Row row, int pos) {
        if (row == null) {
            return null;
        }

        Cell cell = row.getCell(pos, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return cell != null ? value(cell) : null;
    }

    private static Object value(Cell cell) {

        CellType type = cell.getCellType() == CellType.FORMULA
                ? cell.getCachedFormulaResultType() : cell.getCellType();

        switch (type) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                // TODO: check the actual format to return LocalDate, LocalDateTime or smth else
                return DateUtil.isCellDateFormatted(cell)
                        ? cell.getLocalDateTimeCellValue()
                        : cell.getNumericCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                throw new IllegalStateException("FORMULA is unexpected as a FORMULA cell result");
            case BLANK:
            case ERROR:
            default:
                return null;
        }
    }
}
