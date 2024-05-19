package org.dflib.echarts;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

// a helper to resolve per-column property overrides
class PerColumnPropertyBuilder<T> {

    private final T defaultType;
    private T userDefaultType;
    private Map<String, T> perColumnTypes;

    PerColumnPropertyBuilder(T defaultType) {
        this.defaultType = Objects.requireNonNull(defaultType);
    }

    void setType(T v) {
        this.userDefaultType = v;
    }

    void setType(String name, T v) {
        if (perColumnTypes == null) {
            perColumnTypes = new HashMap<>();
        }

        perColumnTypes.put(name, v);
    }

    List<T> resolve(String... names) {
        T defaultType = this.userDefaultType != null ? this.userDefaultType : this.defaultType;
        Map<String, T> perColumnTypes = this.perColumnTypes != null ? this.perColumnTypes : Map.of();
        return Arrays.stream(names)
                .map(c -> perColumnTypes.getOrDefault(c, defaultType))
                .collect(Collectors.toList());
    }
}
