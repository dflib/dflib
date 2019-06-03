package com.nhl.dflib;

import com.nhl.dflib.row.RowProxy;

/**
 * @since 0.6
 */
public interface RowToBooleanValueMapper {

    boolean map(RowProxy row);
}
