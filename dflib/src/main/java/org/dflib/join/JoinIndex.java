package org.dflib.join;

import org.dflib.Index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

class JoinIndex {

    static JoinIndex of(String leftAlias, String rightAlias, Index leftIndex, Index rightIndex, String indicatorColumn) {
        int baseLen = leftIndex.size() + rightIndex.size();
        int len = indicatorColumn != null ? baseLen + 1 : baseLen;

        String[] lLabels = leftIndex.getLabels();
        String[] rLabels = rightIndex.getLabels();

        String lp = leftAlias != null ? leftAlias + "." : null;
        String rp = rightAlias != null ? rightAlias + "." : null;

        Set<String> uniqueNames = new HashSet<>();
        JoinColumn[] columns = new JoinColumn[len];
        int i = 0;

        for (String label : lLabels) {
            String name = uniqueName(uniqueNames, label);
            String aliasedName = lp != null ? uniqueName(uniqueNames, lp + label) : null;
            columns[i] = new JoinColumn(i, name, aliasedName, null);
            i++;
        }

        for (String label : rLabels) {
            String name = uniqueName(uniqueNames, label);
            String aliasedName = rp != null ? uniqueName(uniqueNames, rp + label) : null;
            columns[i] = new JoinColumn(i, name, aliasedName, null);
            i++;
        }

        if (indicatorColumn != null) {
            String name = uniqueName(uniqueNames, indicatorColumn);
            columns[i] = new JoinColumn(i, name, null, null);
        }

        return new JoinIndex(columns, uniqueNames);
    }

    private static String uniqueName(Set<String> uniqueNames, String name) {
        while (!uniqueNames.add(name)) {
            name = name + "_";
        }

        return name;
    }

    private final Set<String> uniqueNames;
    private final JoinColumn[] columns;
    private Map<String, JoinColumn> columnsByAllPossibleAliases;

    private JoinIndex(JoinColumn[] columns, Set<String> uniqueNames) {
        this.columns = columns;
        this.uniqueNames = uniqueNames;
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

    public JoinIndex cols(int... csIndex) {

        int sLen = columns.length;
        int csLen = csIndex.length;

        JoinColumn[] cols = new JoinColumn[csLen];

        for (int i = 0; i < csLen; i++) {
            cols[i] = csIndex[i] < sLen
                    ? columns[csIndex[i]]
                    : createColumn(String.valueOf(csIndex[i]), csIndex[i]);
        }

        return new JoinIndex(cols, uniqueNames);
    }

    public JoinIndex cols(String... labels) {

        int len = labels.length;
        JoinColumn[] cols = new JoinColumn[len];

        for (int i = 0, extras = columns.length; i < len; i++) {
            String name = labels[i];
            JoinColumn c = resolveColumn(name);
            cols[i] = c != null ? c.nameResult(name) : createColumn(name, extras++);
        }

        return new JoinIndex(cols, uniqueNames);
    }

    public JoinIndex cols(Predicate<String> predicate) {

        List<JoinColumn> cols = new ArrayList<>(columns.length);
        for (JoinColumn column : columns) {
            if (column.test(predicate)) {
                cols.add(column);
            }
        }

        return new JoinIndex(cols.toArray(new JoinColumn[0]), uniqueNames);
    }

    public JoinIndex colsExcept(Predicate<String> predicate) {

        // due to aliases "selectExcept(c)" is not the same as "select(!c)", so need to process it explicitly

        List<JoinColumn> included = new ArrayList<>(columns.length);
        for (JoinColumn column : columns) {
            if (!column.test(predicate)) {
                included.add(column);
            }
        }

        return new JoinIndex(included.toArray(new JoinColumn[0]), uniqueNames);
    }

    public JoinIndex colsExcept(int... positions) {
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

        return new JoinIndex(included.toArray(new JoinColumn[0]), uniqueNames);
    }

    public JoinIndex colsExcept(String... labels) {
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

        return new JoinIndex(included.toArray(new JoinColumn[0]), uniqueNames);
    }

    public JoinIndex colsExpandAliases() {
        List<JoinColumn> expanded = new ArrayList<>(columns.length * 2);
        for (JoinColumn c : columns) {
            expanded.add(c);

            // conditionally add an extra column with name matching the prefixed name of the original column
            if (c.prefixedName != null) {
                expanded.add(c.unalias());
            }
        }

        return new JoinIndex(expanded.toArray(new JoinColumn[0]), uniqueNames);
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

        return columnsByAllPossibleAliases.get(label);
    }

    private JoinColumn createColumn(String label, int pos) {
        String name = uniqueName(label);
        return new JoinColumn(pos, name, null, name);
    }

    private String uniqueName(String name) {
        return uniqueName(uniqueNames, name);
    }
}
