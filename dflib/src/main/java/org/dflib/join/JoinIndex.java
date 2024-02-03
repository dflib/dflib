package org.dflib.join;

import org.dflib.Index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

class JoinIndex {

    static JoinIndex of(String leftAlias, String rightAlias, Index leftIndex, Index rightIndex, String indicatorColumn) {
        int baseLen = leftIndex.size() + rightIndex.size();
        int len = indicatorColumn != null ? baseLen + 1 : baseLen;

        String[] lLabels = leftIndex.getLabels();
        String[] rLabels = rightIndex.getLabels();

        String lp = leftAlias != null ? leftAlias + "." : null;
        String rp = rightAlias != null ? rightAlias + "." : null;

        Set<String> uniqueNames = new HashSet<>();
        UnaryOperator<String> deduplicator = s -> {
            while (!uniqueNames.add(s)) {
                s = s + "_";
            }

            return s;
        };

        JoinColumn[] columns = new JoinColumn[len];
        int i = 0;

        for (String label : lLabels) {
            String name = deduplicator.apply(label);
            String aliasedName = lp != null ? deduplicator.apply(lp + label) : null;
            columns[i] = new JoinColumn(i, name, aliasedName, null);
            i++;
        }

        for (String label : rLabels) {
            String name = deduplicator.apply(label);
            String aliasedName = rp != null ? deduplicator.apply(rp + label) : null;
            columns[i] = new JoinColumn(i, name, aliasedName, null);
            i++;
        }

        if (indicatorColumn != null) {
            String name = deduplicator.apply(indicatorColumn);
            columns[i] = new JoinColumn(i, name, null, null);
        }

        return new JoinIndex(columns);
    }

    private final JoinColumn[] columns;
    private volatile Map<String, JoinColumn> columnsByAllPossibleAliases;

    private JoinIndex(JoinColumn[] columns) {
        this.columns = columns;
    }

    public int size() {
        return columns.length;
    }

    public Index getIndex() {
        int len = columns.length;
        String[] labels = new String[len];

        for (int i = 0; i < len; i++) {
            labels[i] = columns[i].indexName();
        }

        return Index.of(labels);
    }

    public int[] getPositions() {
        int len = columns.length;
        int[] pos = new int[len];
        for (int i = 0; i < len; i++) {
            pos[i] = columns[i].pos;
        }
        return pos;
    }

    public JoinIndex select(int... positions) {
        int len = positions.length;
        JoinColumn[] subset = new JoinColumn[len];

        for (int i = 0; i < len; i++) {
            subset[i] = columns[positions[i]];
        }

        return new JoinIndex(subset);
    }

    public JoinIndex select(String... labels) {

        int len = labels.length;
        JoinColumn[] subset = new JoinColumn[len];

        for (int i = 0; i < len; i++) {
            subset[i] = resolveColumn(labels[i]).nameResult(labels[i]);
        }

        return new JoinIndex(subset);
    }

    public JoinIndex select(Predicate<String> predicate) {

        List<JoinColumn> included = new ArrayList<>(columns.length);
        for (JoinColumn column : columns) {
            if (column.test(predicate)) {
                included.add(column);
            }
        }

        return new JoinIndex(included.toArray(new JoinColumn[0]));
    }

    public JoinIndex selectExcept(Predicate<String> predicate) {

        // due to aliases "selectExcept(c)" is not the same as "select(!c)", so need to process it explicitly

        List<JoinColumn> included = new ArrayList<>(columns.length);
        for (JoinColumn column : columns) {
            if (!column.test(predicate)) {
                included.add(column);
            }
        }

        return new JoinIndex(included.toArray(new JoinColumn[0]));
    }

    public JoinIndex selectExcept(int... positions) {
        int exceptLen = positions.length;
        if (exceptLen == 0) {
            return this;
        }

        Set<Integer> excludes = new HashSet<>((int) Math.ceil(exceptLen / 0.75));
        for (int e : positions) {
            excludes.add(e);
        }

        List<JoinColumn> included = new ArrayList<>(columns.length - excludes.size());
        int len = columns.length;
        for (int i = 0; i < len; i++) {
            if (!excludes.contains(i)) {
                included.add(columns[i]);
            }
        }

        return new JoinIndex(included.toArray(new JoinColumn[0]));
    }

    public JoinIndex selectExcept(String... labels) {
        if (labels.length == 0) {
            return this;
        }

        Set<String> excludes = Set.of(labels);

        List<JoinColumn> included = new ArrayList<>(columns.length - excludes.size());
        for (JoinColumn c : columns) {
            if (!c.testIn(excludes)) {
                included.add(c);
            }
        }

        return new JoinIndex(included.toArray(new JoinColumn[0]));
    }

    public JoinIndex selectAllAliases() {
        List<JoinColumn> expanded = new ArrayList<>(columns.length * 2);
        for (JoinColumn c : columns) {
            expanded.add(c);
            if (c.prefixedName != null) {
                // add an extra column whose name is the same as the aliased name of the original column
                expanded.add(c.unalias());
            }
        }

        return new JoinIndex(expanded.toArray(new JoinColumn[0]));
    }

    private JoinColumn resolveColumn(String label) {
        if (columnsByAllPossibleAliases == null) {
            Map<String, JoinColumn> map = new HashMap<>();

            for (JoinColumn c : columns) {
                map.put(c.name, c);

                if (c.prefixedName != null) {
                    map.put(c.prefixedName, c);
                }
            }

            this.columnsByAllPossibleAliases = map;
        }

        JoinColumn c = columnsByAllPossibleAliases.get(label);
        if (c == null) {
            throw new IllegalArgumentException("Unrecognized join column name: " + label);
        }

        return c;
    }
}
