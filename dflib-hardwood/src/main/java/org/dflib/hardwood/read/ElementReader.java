package org.dflib.hardwood.read;

import dev.hardwood.metadata.LogicalType;
import dev.hardwood.metadata.PhysicalType;
import dev.hardwood.row.PqInterval;
import dev.hardwood.row.PqList;
import dev.hardwood.row.PqMap;
import dev.hardwood.schema.SchemaNode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Materializes the contents of a Parquet LIST or MAP column into plain Java collections.
 *
 * <p>Hardwood exposes nested values as flyweights over the current read batch, which are only valid until the reader
 * advances to the next row, so their contents have to be copied out eagerly. Element values also need the same
 * DFLib-specific adjustments as top-level columns (widening unsigned ints, turning intervals into int arrays).
 *
 * @since 2.0.0
 */
@FunctionalInterface
public interface ElementReader {

    /**
     * Converts a single non-null element, as decoded by Hardwood, to its DFLib representation.
     */
    Object convert(Object value);

    default List<Object> toList(PqList list) {
        int len = list.size();
        List<Object> out = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            out.add(list.isNull(i) ? null : convert(list.get(i)));
        }
        return out;
    }

    static Map<Object, Object> toMap(PqMap map, ElementReader keyReader, ElementReader valueReader) {
        List<PqMap.Entry> entries = map.getEntries();
        Map<Object, Object> out = new LinkedHashMap<>((int) Math.ceil(entries.size() / 0.75) + 1);
        for (PqMap.Entry e : entries) {
            out.put(keyReader.convert(e.getKey()), e.isValueNull() ? null : valueReader.convert(e.getValue()));
        }
        return out;
    }

    static ElementReader of(SchemaNode element) {
        return switch (element) {
            case SchemaNode.PrimitiveNode p -> primitive(p);
            case SchemaNode.GroupNode g -> group(g);
        };
    }

    private static ElementReader primitive(SchemaNode.PrimitiveNode element) {

        if (element.type() == PhysicalType.INT96) {
            throw new IllegalArgumentException(
                    "INT96 deserialization is deprecated and is not supported: " + element.name());
        }

        LogicalType lt = element.logicalType();

        if (lt instanceof LogicalType.IntType it && !it.isSigned()) {
            return switch (it.bitWidth()) {
                case 8 -> v -> (short) (((Integer) v) & 0xFF);
                case 16 -> v -> ((Integer) v) & 0xFFFF;
                case 32 -> v -> Integer.toUnsignedLong((Integer) v);
                case 64 -> v -> LogicalTypes.toUnsignedBigInteger((Long) v);
                default -> throw new IllegalArgumentException(
                        "Invalid bit width for an int type: " + element.name() + ": " + it.bitWidth());
            };
        }

        if (lt instanceof LogicalType.IntervalType) {
            return v -> {
                PqInterval i = (PqInterval) v;
                return new int[]{(int) i.months(), (int) i.days(), (int) i.milliseconds()};
            };
        }

        // everything else already arrives in the type DFLib uses
        return v -> v;
    }

    private static ElementReader group(SchemaNode.GroupNode element) {

        if (element.isList()) {
            SchemaNode inner = element.getListElement();
            if (inner == null) {
                throw new IllegalArgumentException("Unrecognized LIST layout: " + element.name());
            }

            ElementReader innerReader = of(inner);
            return v -> innerReader.toList((PqList) v);
        }

        if (element.isMap()) {
            SchemaNode key = element.getMapKey();
            SchemaNode value = element.getMapValue();
            if (key == null || value == null) {
                throw new IllegalArgumentException("Unrecognized MAP layout: " + element.name());
            }

            ElementReader keyReader = of(key);
            ElementReader valueReader = of(value);
            return v -> toMap((PqMap) v, keyReader, valueReader);
        }

        throw new IllegalArgumentException(
                "Deserialization of this group type is not supported: " + element.name());
    }
}
