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

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TarEntryTest {

    @Test
    public void getOrderedSparseHeadersRejectsOverlappingStructs() {
        TarEntry te = new TarEntry("test", -1, 201, 0, -1, false, (byte) 0, "", false, false, true, List.of());
        te.setSparseHeaders(List.of(new TarStructSparse(10, 5), new TarStructSparse(12, 1)));
        assertThrows(IOException.class, () -> te.getOrderedSparseHeaders());
    }

    @Test
    public void getOrderedSparseHeadersRejectsStructsPointingBeyondOutputEntry() {
        TarEntry te = new TarEntry("test", -1, 201, 0, -1, false, (byte) 0, "", false, false, true, List.of());
        te.setSparseHeaders(List.of(new TarStructSparse(200, 2)));
        assertThrows(IOException.class, () -> te.getOrderedSparseHeaders());
    }

    @Test
    public void getOrderedSparseHeadersRejectsStructsWithReallyBigNumbers() throws Exception {
        TarEntry te = new TarEntry("test", -1, Long.MAX_VALUE, 0, -1, false, (byte) 0, "", false, false, true, List.of());
        te.setSparseHeaders(List.of(new TarStructSparse(Long.MAX_VALUE, 2)));
        assertThrows(IOException.class, () -> te.getOrderedSparseHeaders());
    }

    @Test
    public void getOrderedSparseHeadersSortsAndFiltersSparseStructs() throws Exception {
        TarEntry te = new TarEntry("test", -1, 201, 0, -1, false, (byte) 0, "", false, false, true, List.of());
        te.setSparseHeaders(List.of(
                new TarStructSparse(10, 2),
                new TarStructSparse(20, 0),
                new TarStructSparse(15, 1),
                new TarStructSparse(0, 0)));

        List<TarStructSparse> strs = te.getOrderedSparseHeaders();
        assertEquals(3, strs.size());
        assertEquals(10, strs.get(0).getOffset());
        assertEquals(15, strs.get(1).getOffset());
        assertEquals(20, strs.get(2).getOffset());
    }
}
