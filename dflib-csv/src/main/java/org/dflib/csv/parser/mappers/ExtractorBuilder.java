package org.dflib.csv.parser.mappers;

import org.dflib.Extractor;
import org.dflib.ValueMapper;
import org.dflib.csv.parser.CsvSchema;
import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.format.CsvColumnMapping;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Trim;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static org.dflib.Extractor.*;
import static org.dflib.csv.parser.mappers.ValueMappers.*;

/**
 * Internal API. Part of the {@link org.dflib.csv.CsvLoader} API
 *
 * @since 2.0.0
 */
public class ExtractorBuilder {

    private ExtractorBuilder() {
    }

    @SuppressWarnings("unchecked")
    public static Extractor<DataSlice[], ?>[] buildExtractors(CsvFormat format,
                                                              List<CsvColumnMapping> columns,
                                                              CsvSchema schema) {
        int[] csvPositions = schema.getCsvPositions();
        Extractor<DataSlice[], ?>[] extractors = new Extractor[csvPositions.length];
        CharBufferProvider bufferProvider = CharBufferProvider.singleton();
        for (int i = 0; i < csvPositions.length; i++) {
            int csvPos = csvPositions[i];
            extractors[i] = buildColumnExtractor(format, columns.get(csvPos), bufferProvider);
        }
        return extractors;
    }

    private static Extractor<DataSlice[], ?> buildColumnExtractor(CsvFormat format,
                                                                  CsvColumnMapping columnFormat,
                                                                  CharBufferProvider bufferProvider) {
        Function<DataSlice[], DataSlice> sliceMapper;
        if (columnFormat.skip()) {
            // there are two options for skipped columns:
            // 1. present in the file, but it is skipped by the user request
            // 2. absent in the file, but the user wants it in the result
            sliceMapper = slices -> NullableSliceMapper.NULL;
        } else {
            sliceMapper = buildSliceMapper(columnFormat);
        }

        Extractor<DataSlice[], ?> extractor = switch (columnFormat.type()) {
            case BOOLEAN -> $bool(forBool(sliceMapper, columnFormat));
            case INTEGER -> $int(forInt(sliceMapper, columnFormat));
            case LONG -> $long(forLong(sliceMapper, columnFormat));
            case FLOAT -> $float(forFloat(sliceMapper, columnFormat));
            case DOUBLE -> $double(forDouble(sliceMapper, columnFormat));
            case BIG_INTEGER -> $col(forObject(sliceMapper, BigIntegerParser::parse, columnFormat));
            case BIG_DECIMAL -> $col(forObject(sliceMapper, BigDecimalParser::parse, columnFormat));
            case OTHER ->
                    $col(forObject(sliceMapper, mapperFunction(format, columnFormat, bufferProvider), columnFormat));
            default ->
                    $col(forObject(sliceMapper, unescapeFunction(format, columnFormat, bufferProvider), columnFormat));
        };
        if (columnFormat.compact()) {
            return extractor.compact();
        }
        return extractor;
    }

    private static Function<DataSlice[], DataSlice> buildSliceMapper(CsvColumnMapping columnFormat) {
        int idx = columnFormat.index();
        Function<DataSlice[], DataSlice> sliceMapper = ss -> ss[idx];
        Trim trim = columnFormat.format().trim();
        if (trim != Trim.NONE) {
            sliceMapper = new TrimmingSliceMapper(sliceMapper, trim);
        }
        if (columnFormat.nullable()) {
            return new NullableSliceMapper(sliceMapper, columnFormat.format().nullString());
        }
        return sliceMapper;
    }

    private static Function<DataSlice, String> unescapeFunction(CsvFormat format,
                                                                CsvColumnMapping columnFormat,
                                                                CharBufferProvider bufferProvider) {
        return QuoteProcessor.forFormat(format, columnFormat.format().quote(), bufferProvider);
    }

    private static Function<DataSlice, Object> mapperFunction(CsvFormat format,
                                                              CsvColumnMapping columnFormat,
                                                              CharBufferProvider bufferProvider) {
        Function<DataSlice, String> escape = unescapeFunction(format, columnFormat, bufferProvider);
        ValueMapper<String, ?> mapper = Objects.requireNonNull(columnFormat.mapper(),
                () -> "No mapper is set for the column " + columnFormat.name() + "(" + columnFormat.index() + ")");
        return slice -> mapper.map(escape.apply(slice));
    }

}
