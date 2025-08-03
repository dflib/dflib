/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.dflib.tar.format;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.GZIPInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class TarInputStreamTest {

    private void getNextEntryUntilIOException(TarInputStream in) {
        assertThrows(IOException.class, () -> {
            while (in.getNextEntry() != null) {
            }
        });
    }

    private TarInputStream getTestStream(String name) {
        return new TarInputStream(TarInputStreamTest.class.getResourceAsStream(name));
    }

    @Test
    void testCompress197() throws IOException {
        try (TarInputStream tar = getTestStream("COMPRESS-197.tar")) {
            TarEntry entry = tar.getNextEntry();
            assertNotNull(entry);
            while (entry != null) {
                assertTrue(entry.isTypeFlagUstar());
                entry = tar.getNextEntry();
            }
        }
    }

    @Test
    public void compress197ForEach() throws IOException {
        try (TarInputStream tar = getTestStream("COMPRESS-197.tar")) {
            while (tar.getNextEntry() != null) {
            }
        }
    }

    private void testCompress666(int factor, boolean bufferInputStream, String localPath) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        try {
            List<Future<?>> tasks = IntStream.range(0, 200).mapToObj(index -> executorService.submit(() -> {
                TarEntry tarEntry = null;
                try (InputStream in = getClass().getResourceAsStream(localPath)) {
                    try (TarInputStream tarIn = new TarInputStream(
                            bufferInputStream ? new BufferedInputStream(new GZIPInputStream(in)) : new GZIPInputStream(in),
                            TarConstants.DEFAULT_RCDSIZE * factor,
                            TarConstants.DEFAULT_RCDSIZE)) {

                        while ((tarEntry = tarIn.getNextEntry()) != null) {
                            // read to the end
                        }
                    }
                } catch (IOException e) {
                    fail(Objects.toString(tarEntry), e);
                }
            })).collect(Collectors.toList());

            for (Future<?> future : tasks) {
                future.get();
            }
        } finally {
            executorService.shutdownNow();
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 4, 8, 16, 20, 32, 64, 128})
    public void compress666Buffered(int factor) throws ExecutionException, InterruptedException {
        testCompress666(factor, true, "COMPRESS-666.tar.gz");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 4, 8, 16, 20, 32, 64, 128})
    public void compress666Unbuffered(int factor) throws ExecutionException, InterruptedException {
        testCompress666(factor, false, "COMPRESS-666.tar.gz");
    }

    @Test
    public void multiByteReadConsistentlyReturnsMinusOneAtEof() throws Exception {
        final byte[] buf = new byte[2];
        try (TarInputStream in = getTestStream("bla.tar")) {
            assertNotNull(in.getNextEntry());

            while (in.getNextEntry() != null) {
                // read to the end
            }

            assertEquals(-1, in.read(buf));
            assertEquals(-1, in.read(buf));
        }
    }

    @Test
    public void parseTarTruncatedInContent() throws IOException {
        try (TarInputStream in = getTestStream("COMPRESS-544_truncated_in_content.tar")) {
            getNextEntryUntilIOException(in);
        }
    }

    @Test
    public void parseTarTruncatedInPadding() throws IOException {
        try (TarInputStream in = getTestStream("COMPRESS-544_truncated_in_padding.tar")) {
            getNextEntryUntilIOException(in);
        }
    }


    @Test
    public void parseTarWithSpecialPaxHeaders() throws IOException {
        try (TarInputStream in = getTestStream("COMPRESS-530.tar")) {
            assertThrows(IOException.class, in::getNextEntry);
        }
    }

    @Test
    public void rejectsArchivesWithNegativeSizes() throws Exception {
        try (TarInputStream in = getTestStream("COMPRESS-569.tar")) {
            getNextEntryUntilIOException(in);
        }
    }

    @Test
    public void shouldConsumeArchiveCompletely() throws Exception {
        try (InputStream in = TarInputStreamTest.class.getResourceAsStream("archive_with_trailer.tar")) {
            try (TarInputStream tarIn = new TarInputStream(in)) {
                while (tarIn.getNextEntry() != null) {
                    // just consume the archive
                }
                byte[] expected = {'H', 'e', 'l', 'l', 'o', ',', ' ', 'w', 'o', 'r', 'l', 'd', '!', '\n'};
                byte[] actual = new byte[expected.length];
                in.read(actual);
                assertArrayEquals(expected, actual, () -> Arrays.toString(actual));
            }
        }
    }

    @Test
    public void shouldReadGNULongNameEntryWithWrongName() throws Exception {
        try (TarInputStream is = getTestStream("COMPRESS-324.tar")) {
            TarEntry entry = is.getNextEntry();
            assertEquals(
                    "1234567890123456789012345678901234567890123456789012345678901234567890"
                            + "1234567890123456789012345678901234567890123456789012345678901234567890"
                            + "1234567890123456789012345678901234567890123456789012345678901234567890" + "1234567890123456789012345678901234567890.txt",
                    entry.getName());
        }
    }

    @Test
    public void shouldThrowAnExceptionOnTruncatedEntries() throws Exception {
        try (TarInputStream in = getTestStream("COMPRESS-279.tar")) {
            assertThrows(IOException.class, () -> {
                while (in.getNextEntry() != null) {
                    // read to the end
                }
            });
        }
    }

    @Test
    public void shouldThrowAnExceptionOnTruncatedStream() throws Exception {
        try (TarInputStream in = getTestStream("COMPRESS-279.tar")) {
            assertThrows(IOException.class, () -> {
                while (in.getNextEntry() != null) {
                    // read to the end
                }
            });
        }
    }

    @Test
    public void singleByteReadConsistentlyReturnsMinusOneAtEof() throws Exception {
        try (TarInputStream in = getTestStream("bla.tar")) {
            assertNotNull(in.getNextEntry());
            while (in.getNextEntry() != null) {
                // read to the end
            }
            assertEquals(-1, in.read());
            assertEquals(-1, in.read());
        }
    }

    @Test
    public void skipsDevNumbersWhenEntryIsNoDevice() throws Exception {
        try (TarInputStream is = getTestStream("COMPRESS-417.tar")) {

            TarEntry e = is.getNextEntry();

            assertEquals("test1.xml", e.getName());
            assertEquals(TarConstants.LF_NORMAL, e.getLinkFlag());
            assertEquals("test2.xml", is.getNextEntry().getName());
            assertEquals(TarConstants.LF_NORMAL, e.getLinkFlag());
            assertNull(is.getNextEntry());
        }
    }

    @Test
    public void survivesBlankLinesInPaxHeader() throws Exception {
        try (TarInputStream is = getTestStream("COMPRESS-355.tar")) {
            final TarEntry entry = is.getNextEntry();
            assertEquals("package/package.json", entry.getName());
            assertEquals(TarConstants.LF_NORMAL, entry.getLinkFlag());
            assertNull(is.getNextEntry());
        }
    }

    @Test
    public void survivesPaxHeaderWithNameEndingInSlash() throws Exception {
        try (TarInputStream is = getTestStream("COMPRESS-356.tar")) {
            TarEntry entry = is.getNextEntry();
            assertEquals("package/package.json", entry.getName());
            assertEquals(TarConstants.LF_NORMAL, entry.getLinkFlag());
            assertNull(is.getNextEntry());
        }
    }

    @Test
    public void throwException() throws IOException {
        try (TarInputStream in = getTestStream("COMPRESS-553-fail.tar")) {
            getNextEntryUntilIOException(in);
        }
    }

    @Test
    public void throwExceptionWithNullEntry() throws IOException {
        try (TarInputStream in = getTestStream("COMPRESS-554-fail.tar")) {
            getNextEntryUntilIOException(in);
        }
    }
}
