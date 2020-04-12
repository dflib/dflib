package com.nhl.dflib.test.junit5;

import com.nhl.dflib.Index;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @since 0.8
 */
public class IndexAsserts {

    public static void expect(Index index, Index expectedIndex) {
        expect(index, expectedIndex.getLabels());
    }

    public static void expect(Index index, List<String> expectedLabels) {
        expect(index, expectedLabels.toArray(new String[expectedLabels.size()]));
    }

    public static void expect(Index index, String... expectedLabels) {
        assertNotNull(index, "Index is null");
        assertArrayEquals(expectedLabels, index.getLabels(), "Index differs from expected");
    }
}
