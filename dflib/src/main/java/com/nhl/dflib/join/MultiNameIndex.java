package com.nhl.dflib.join;

import com.nhl.dflib.Index;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A special Index that recognizes multiple name aliases for a given column. Useful during join operations to resolve
 * aliased and unaliased columns from the joined DataFrames.
 */
class MultiNameIndex extends Index {

    // key - unique alias, value - "normal" name
    private final Map<String, String> labelAliases;

    public MultiNameIndex(Map<String, String> labelAliases, String... labels) {
        super(labels);
        this.labelAliases = Objects.requireNonNull(labelAliases);
    }

    @Override
    protected Map<String, Integer> computeLabelPositions() {
        Map<String, Integer> positions = super.computeLabelPositions();

        if (labelAliases.isEmpty()) {
            return positions;
        }

        Map<String, Integer> positionsWithAliases = new HashMap<>(positions);
        for (Map.Entry<String, String> e : labelAliases.entrySet()) {
            Integer pos = positions.get(e.getValue());
            if (pos != null && !positions.containsKey(e.getKey())) {
                positionsWithAliases.put(e.getKey(), pos);
            }
        }

        return positionsWithAliases;
    }
}
