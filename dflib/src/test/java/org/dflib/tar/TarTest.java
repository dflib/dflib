package org.dflib.tar;

import org.dflib.tar.format.TarEntry;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TarTest {

    @Test
    void notHidden() {
        Predicate<TarEntry> filter = Tar.notHiddenFilter();
        assertTrue(filter.test(createTarEntry("")));
        assertTrue(filter.test(createTarEntry("a")));
        assertTrue(filter.test(createTarEntry("a/")));
        assertTrue(filter.test(createTarEntry("a/b/c")));

        assertFalse(filter.test(createTarEntry(".")));
        assertFalse(filter.test(createTarEntry(".a")));
        assertFalse(filter.test(createTarEntry(".a/")));
        assertFalse(filter.test(createTarEntry(".b/a/")));
        assertFalse(filter.test(createTarEntry(".b/a/c")));
        assertFalse(filter.test(createTarEntry("b/.a")));
        assertFalse(filter.test(createTarEntry("b/.a/")));
    }

    private TarEntry createTarEntry(String name) {
        TarEntry mock = mock(TarEntry.class);
        when(mock.getName()).thenReturn(name);
        return mock;
    }
}