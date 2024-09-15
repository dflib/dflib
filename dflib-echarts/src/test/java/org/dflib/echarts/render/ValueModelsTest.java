package org.dflib.echarts.render;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValueModelsTest {

    @Test
    void spliterator() {
        ValueModels<String> vms = ValueModels.of(List.of("a", "b", "c"));
        assertEquals(List.of("a", "b", "c"),
                StreamSupport.stream(vms.spliterator(), false).map(ValueModel::getValue).collect(Collectors.toList()));
        assertEquals(List.of(false, false, true),
                StreamSupport.stream(vms.spliterator(), false).map(ValueModel::isLast).collect(Collectors.toList()));
    }

    @Test
    void size() {
        ValueModels<String> vms = ValueModels.of(List.of("a", "b"));
        assertEquals(2, vms.size());
    }

    @Test
    void getValue() {
        ValueModels<String> vms = ValueModels.of(List.of("a", "b"));
        assertEquals("a", vms.getValue(0));
        assertEquals("b", vms.getValue(1));
    }
}
