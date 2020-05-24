package com.nhl.dflib.junit5;

import com.nhl.dflib.SeriesGroupBy;

import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @since 0.8
 */
public class SeriesGroupByAsserts {

    private SeriesGroupBy<?> groupBy;

    public SeriesGroupByAsserts(SeriesGroupBy<?> groupBy) {
        assertNotNull(groupBy, "SeriesGroupBy is null");
        this.groupBy = groupBy;
    }

    public SeriesGroupByAsserts expectGroups(Object... expectedGroups) {
        assertEquals(expectedGroups.length, groupBy.getGroups().size(), "Unexpected groups length");

        HashSet<?> expectedSet = new HashSet<>(asList(expectedGroups));
        HashSet<?> actualSet = new HashSet<>(groupBy.getGroups());
        assertEquals(expectedSet, actualSet, "Groups are different");

        return this;
    }

    public SeriesGroupByAsserts expectGroupData(Object groupKey, Object... expectedValues) {
        assertTrue(groupBy.hasGroup(groupKey), "Group key is not present: " + groupKey);
        new SeriesAsserts(groupBy.getGroup(groupKey)).expectData(expectedValues);
        return this;
    }
}
