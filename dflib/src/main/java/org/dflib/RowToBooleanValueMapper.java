package org.dflib;

import org.dflib.row.RowProxy;

/**
 * @since 0.6
 */
public interface RowToBooleanValueMapper {

    boolean map(RowProxy row);
}
