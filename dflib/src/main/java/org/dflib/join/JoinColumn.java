package org.dflib.join;

import java.util.Set;
import java.util.function.Predicate;

class JoinColumn {

    final int pos;
    final String name;
    final String prefixedName;
    private final String resultName;

    JoinColumn(int pos, String name, String prefixedName, String resultName) {
        this.pos = pos;
        this.name = name;
        this.prefixedName = prefixedName;
        this.resultName = resultName;
    }

    JoinColumn nameResult(String resultName) {
        return new JoinColumn(this.pos, this.name, this.prefixedName, resultName);
    }

    JoinColumn unalias() {
        return new JoinColumn(this.pos, this.name, this.name, null);
    }

    String indexName() {
        return resultName != null
                ? resultName
                : (prefixedName != null ? prefixedName : name);
    }

    boolean test(Predicate<String> predicate) {
        return predicate.test(name) || (prefixedName != null && predicate.test(prefixedName));
    }

    boolean testIn(Set<String> names) {
        return names.contains(name) || (prefixedName != null && names.contains(prefixedName));
    }

    @Override
    public String toString() {
        return "[" + pos + "," + indexName() + "]";
    }
}
