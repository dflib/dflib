package org.dflib.echarts.dataframeset;

import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.Series;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Provides a unified column namespace for multiple DataFrames. DataFrames in the set do not have to have the same
 * height or width.
 *
 * @since 2.0.0
 */
// TODO: use it in other multi-DataFrame contexts like joins?
public class DataFrameSet {

    public static DataFrameSet of(DataFrame... dataFrames) {
        return new DataFrameSet(dataFrames);
    }

    private final DataFrame[] dataFrames;
    private volatile Map<String, int[]> nameIndex;

    private DataFrameSet(DataFrame[] dataFrames) {
        this.dataFrames = dataFrames;
    }

    /**
     * Returns a column from one of the set DataFrames that matches the name argument. Accepted names are:
     * <ul>
     *     <li>Qualified names (if any of the DataFrames have names of their own)</li>
     *     <li>Unqualified names (regardless of whether source DataFrames have names).</li>
     *     <li>Names with "_" suffixes for conflicting names. This is the approach used for name conflict resolution
     *     elsewhere in Bootique. If there are two conflicting column names between two DataFrames, the column from
     *     the first DataFrame is addressed directly, whereas "_" is appended to the second name (possibly multiple
     *     times) to produce a set-unique name. E.g. "col" and "col_"</li>
     * </ul>
     * <p>
     *
     * @throws IllegalArgumentException if the name doesn't match a know column across the DataFrames in the set
     */
    public <T> Series<T> getColumn(String name) throws IllegalArgumentException {
        int[] ref = nameIndex().get(name);
        if (ref == null) {
            throw new IllegalArgumentException("No column '" + name + "' in DataFrameSet");
        }
        return dataFrames[ref[0]].getColumn(ref[1]);
    }

    /**
     * Returns the height of the DataFrames in the set, assuming all DataFrames have the same height. Throws
     * IllegalStateException if DataFrames in the set have different heights
     */
    public int height() {
        int h = dataFrames[0].height();
        for (int i = 1; i < dataFrames.length; i++) {
            int hi = dataFrames[i].height();
            if (hi != h) {
                throw new IllegalStateException("DataFrames in the set have different heights. Expected: " + h + ", got: " + hi);
            }
        }
        return h;
    }

    private Map<String, int[]> nameIndex() {
        if (nameIndex == null) {
            Map<String, int[]> idx = new HashMap<>();
            Set<String> usedNames = new HashSet<>();
            int len = dataFrames.length;

            for (int i = 0; i < len; i++) {
                DataFrame df = dataFrames[i];
                String dfName = df.getName();
                Index colIndex = df.getColumnsIndex();

                int w = df.width();
                for (int j = 0; j < w; j++) {
                    String label = colIndex.get(j);
                    String dedupName = uniqueName(usedNames, label);
                    idx.put(dedupName, new int[]{i, j});

                    if (dfName != null) {
                        String dedupQualifiedName = uniqueName(usedNames, dfName + "." + label);
                        idx.put(dedupQualifiedName, new int[]{i, j});
                    }
                }
            }
            nameIndex = idx;
        }
        return nameIndex;
    }

    private static String uniqueName(Set<String> usedNames, String name) {
        while (!usedNames.add(name)) {
            name = name + "_";
        }
        return name;
    }
}
