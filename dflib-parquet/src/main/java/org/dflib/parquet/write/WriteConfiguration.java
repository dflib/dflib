package org.dflib.parquet.write;

import org.dflib.parquet.TimeUnit;

public class WriteConfiguration {

    private final TimeUnit timeUnit;
    private final DecimalConfig decimalConfig;

    public WriteConfiguration(TimeUnit timeUnit, DecimalConfig decimalConfig) {
        this.timeUnit = timeUnit;
        this.decimalConfig = decimalConfig;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public DecimalConfig getDecimalConfig() {
        return decimalConfig;
    }
}
