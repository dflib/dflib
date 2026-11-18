package org.dflib.excel;

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

    Extractor<RowData, ?> extractor(int srcPos) {
        Extractor<RowData, ?> e = Extractor.$col(r -> value(r, srcPos));
        return compact ? e.compact() : e;
    }

    static Object value(RowData row, int pos) {
        return row != null ? row.value(pos) : null;
    }
}
