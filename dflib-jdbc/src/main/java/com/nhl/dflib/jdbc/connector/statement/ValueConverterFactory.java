package com.nhl.dflib.jdbc.connector.statement;

import java.util.Map;

/**
 * @since 0.6
 */
public class ValueConverterFactory {

    private ValueConverter defaultConverter;
    private Map<Integer, ValueConverter> converters;

    public ValueConverterFactory(ValueConverter defaultConverter, Map<Integer, ValueConverter> converters) {
        this.defaultConverter = defaultConverter;
        this.converters = converters;
    }

    /**
     * @since 0.6
     */
    public ValueConverter findConverter(int jdbcType) {
        return converters.getOrDefault(jdbcType, defaultConverter);
    }
}
