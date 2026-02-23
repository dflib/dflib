package org.dflib.csv.parser.format;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfigValidationTest {

    @Test
    void autoDisabledRequiresColumns() {
        assertThrows(IllegalArgumentException.class, () -> CsvParserConfig.builder()
                .autoColumns(false)
                .build());
    }

    @Test
    void invalidNumericSettingsRejected() {
        assertThrows(IllegalArgumentException.class, () -> CsvParserConfig.builder()
                .limit(-2)
                .build());

        assertThrows(IllegalArgumentException.class, () -> CsvParserConfig.builder()
                .offset(-1)
                .build());
    }
}
