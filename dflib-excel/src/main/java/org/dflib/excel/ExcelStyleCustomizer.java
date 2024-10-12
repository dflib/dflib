package org.dflib.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

@FunctionalInterface
public interface ExcelStyleCustomizer {

    void customize(Workbook wb, CellStyle style);

    default CellStyle createCustomStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        customize(wb, style);
        return style;
    }
}
