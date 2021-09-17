package com.nhl.dflib.excel;

import com.nhl.dflib.DataFrame;

import java.io.InputStream;
import java.util.Map;

/**
 * @since 0.13
 */
public class Excel {

    public static Map<String, DataFrame> load(InputStream in) {
        return new ExcelLoader().load(in);
    }
}
