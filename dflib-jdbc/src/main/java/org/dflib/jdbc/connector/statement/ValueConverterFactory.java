package org.dflib.jdbc.connector.statement;

import java.util.Map;

public class ValueConverterFactory {

    private final ValueConverter defaultConverter;
    private final Map<Integer, ValueConverter> converters;

    public ValueConverterFactory(ValueConverter defaultConverter, Map<Integer, ValueConverter> converters) {
        this.defaultConverter = defaultConverter;
        this.converters = converters;
    }

    public ValueConverter findConverter(int jdbcType) {
        return converters.getOrDefault(jdbcType, defaultConverter);
    }
}
