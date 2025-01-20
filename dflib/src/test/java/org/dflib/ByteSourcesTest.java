package org.dflib;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ByteSourcesTest {

    @Test
    void process_0() {
        ByteSources sources = ByteSources.of(Map.of());

        Map<String, ByteSource> captured = sources.process((n, b) -> b);
        assertTrue(captured.isEmpty());
    }

    @Test
    void process_Nulls_0() {
        ByteSources sources = ByteSources.of(Map.of());

        Map<String, Object> captured = sources.process((n, b) -> null);
        assertTrue(captured.isEmpty());
    }

    @Test
    void process_1() {
        Map<String, ByteSource> sourcesMap = Map.of("b1", ByteSource.of("a".getBytes()));
        ByteSources sources = ByteSources.of(sourcesMap);

        Map<String, ByteSource> captured = sources.process((n, b) -> b);
        assertEquals(sourcesMap, captured);
    }

    @Test
    void process_Nulls_1() {
        Map<String, ByteSource> sourcesMap = Map.of("b1", ByteSource.of("a".getBytes()));

        ByteSources sources = ByteSources.of(sourcesMap);

        Map<String, Object> captured = sources.process((n, b) -> null);
        assertEquals(Set.of("b1"), captured.keySet());
        assertEquals(asList((Object) null), new ArrayList(captured.values()));
    }

    @Test
    void process_2() {
        ByteSource b1 = ByteSource.of("a".getBytes());
        ByteSource b2 = ByteSource.of("b".getBytes());
        Map<String, ByteSource> sourcesMap = Map.of("b1", b1, "b2", b2);

        ByteSources sources = ByteSources.of(sourcesMap);

        Map<String, ByteSource> captured = sources.process((n, b) -> b);
        assertEquals(sourcesMap, captured);
    }

    @Test
    void process_Nulls_2() {
        ByteSource b1 = ByteSource.of("a".getBytes());
        ByteSource b2 = ByteSource.of("b".getBytes());
        Map<String, ByteSource> sourcesMap = Map.of("b1", b1, "b2", b2);

        ByteSources sources = ByteSources.of(sourcesMap);

        Map<String, Object> captured = sources.process((n, b) -> null);
        assertEquals(Set.of("b1", "b2"), captured.keySet());
        assertEquals(asList(null, null), new ArrayList(captured.values()));
    }
}
