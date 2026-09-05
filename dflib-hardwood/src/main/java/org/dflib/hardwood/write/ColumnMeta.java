package org.dflib.hardwood.write;

/**
 * The DFLib-side description of a DataFrame column being written to Parquet.
 *
 * @since 2.0.0
 */
public class ColumnMeta {

    private final String columnName;
    private final Class<?> inferredType;
    private final int index;

    public ColumnMeta(String columnName, Class<?> inferredType, int index) {
        this.columnName = columnName;
        this.inferredType = inferredType;
        this.index = index;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getInferredTypeName() {
        return inferredType.isArray() ? inferredType.getComponentType().getName() + "[]" : inferredType.getName();
    }

    public boolean isEnum() {
        return inferredType.isEnum();
    }

    /**
     * Whether the column values are non-null primitives, and hence can be written as a "required" Parquet field.
     */
    public boolean isPrimitive() {
        return inferredType.isPrimitive();
    }

    public int getIndex() {
        return index;
    }
}
