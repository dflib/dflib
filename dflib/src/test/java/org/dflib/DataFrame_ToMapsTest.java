package org.dflib;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DataFrame_ToMapsTest {

    @Test
    public void basic() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                3, "z"
        );

        List<Map<String, Object>> maps = df.toMaps();

        assertEquals(3, maps.size());

        assertEquals(Map.of("a", 1, "b", "x"), maps.get(0));
        assertEquals(Map.of("a", 2, "b", "y"), maps.get(1));
        assertEquals(Map.of("a", 3, "b", "z"), maps.get(2));
    }

    @Test
    public void empty() {
        DataFrame df = DataFrame.empty("a", "b");

        List<Map<String, Object>> maps = df.toMaps();

        assertEquals(0, maps.size());
    }

    @Test
    public void singleRow() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", 3.14
        );

        List<Map<String, Object>> maps = df.toMaps();

        assertEquals(1, maps.size());
        assertEquals(Map.of("a", 1, "b", "x", "c", 3.14), maps.get(0));
    }

    @Test
    public void singleColumn() {
        DataFrame df = DataFrame.foldByRow("a").of(
                1,
                2,
                3
        );

        List<Map<String, Object>> maps = df.toMaps();

        assertEquals(3, maps.size());
        assertEquals(Map.of("a", 1), maps.get(0));
        assertEquals(Map.of("a", 2), maps.get(1));
        assertEquals(Map.of("a", 3), maps.get(2));
    }

    @Test
    public void withNulls() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                null, "y",
                3, null
        );

        List<Map<String, Object>> maps = df.toMaps();

        assertEquals(3, maps.size());

        Map<String, Object> map0 = maps.get(0);
        assertEquals(2, map0.size());
        assertEquals(1, map0.get("a"));
        assertEquals("x", map0.get("b"));

        Map<String, Object> map1 = maps.get(1);
        assertEquals(2, map1.size());
        assertNull(map1.get("a"));
        assertEquals("y", map1.get("b"));
        assertTrue(map1.containsKey("a"));

        Map<String, Object> map2 = maps.get(2);
        assertEquals(2, map2.size());
        assertEquals(3, map2.get("a"));
        assertNull(map2.get("b"));
        assertTrue(map2.containsKey("b"));
    }
}
