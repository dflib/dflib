package org.dflib.parquet.write;

/**
 * @since 1.0.0-M23
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

    public Class<?> getInferredType() {
        return inferredType;
    }

    public String getInferredTypeName() {
        return inferredType.isArray() ? inferredType.getComponentType().getName() + "[]" : inferredType.getName();
    }

    public boolean isEnum() {
        return inferredType.isEnum();
    }

    public int getIndex() {
        return index;
    }

}
