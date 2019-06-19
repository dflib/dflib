package com.nhl.dflib.unit;

import com.nhl.dflib.SeriesGroupBy;

import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class SeriesGroupByAsserts {

    private SeriesGroupBy<?> groupBy;

    public SeriesGroupByAsserts(SeriesGroupBy<?> groupBy) {
        assertNotNull("SeriesGroupBy is null", groupBy);
        this.groupBy = groupBy;
    }

    public SeriesGroupByAsserts expectGroups(Object... expectedGroups) {
        assertEquals("Unexpected groups length", expectedGroups.length, groupBy.getGroups().size());

        HashSet<?> expectedSet = new HashSet<>(asList(expectedGroups));
        HashSet<?> actualSet = new HashSet<>(groupBy.getGroups());
        assertEquals("Groups are different", expectedSet, actualSet);

        return this;
    }

    public SeriesGroupByAsserts expectGroupData(Object groupKey, Object... expectedValues) {
        assertTrue("Group key is not present: " + groupKey, groupBy.hasGroup(groupKey));
        new SeriesAsserts(groupBy.getGroup(groupKey))
                .expectData(expectedValues);

        return this;
    }
}
