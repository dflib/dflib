package org.dflib.parquet.write;

import org.dflib.parquet.TimeUnit;

public record WriteConfiguration(TimeUnit timeUnit, DecimalConfig decimalConfig) {
}
