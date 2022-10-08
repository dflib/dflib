package com.nhl.dflib.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @since 0.14
 */
@FunctionalInterface
public interface ExcelStyleCustomizer {

    void customize(Workbook wb, CellStyle style);

    default CellStyle createCustomStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        customize(wb, style);
        return style;
    }
}
