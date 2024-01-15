package org.dflib;

import org.dflib.row.RowProxy;

/**
 * @since 0.6
 * @deprecated unused anywhere except in deprecated APIs. A complete analog of {@link RowPredicate}
 */
@Deprecated(since = "1.0.0-M19", forRemoval = true)
public interface RowToBooleanValueMapper {

    boolean map(RowProxy row);
}
