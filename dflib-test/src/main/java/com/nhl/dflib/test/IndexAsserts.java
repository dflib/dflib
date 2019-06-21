package com.nhl.dflib.test;

import com.nhl.dflib.Index;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @since 0.6
 */
public class IndexAsserts {

    public static void expect(Index index, Index expectedIndex) {
        expect(index, expectedIndex.getLabels());
    }

    public static void expect(Index index, List<String> expectedLabels) {
        expect(index, expectedLabels.toArray(new String[expectedLabels.size()]));
    }

    public static void expect(Index index, String... expectedLabels) {
        assertNotNull("Index is null", index);
        assertArrayEquals("Index differs from expected", expectedLabels, index.getLabels());
    }
}
