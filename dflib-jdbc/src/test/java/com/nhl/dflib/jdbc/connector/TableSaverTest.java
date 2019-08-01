package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.jdbc.connector.metadata.TableFQName;
import com.nhl.dflib.jdbc.connector.saver.SaveViaDeleteThenInsert;
import com.nhl.dflib.jdbc.connector.saver.SaveViaInsert;
import com.nhl.dflib.jdbc.connector.saver.SaveViaUpsert;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class TableSaverTest {

    @Test
    public void testCreateSaveStrategy_Default() {
        TableSaver saver = new TableSaver(mock(DefaultJdbcConnector.class), TableFQName.forName("xt"));

        assertEquals(SaveViaInsert.class, saver.createSaveStrategy().getClass());
    }

    @Test
    public void testCreateSaveStrategy_DeleteInsert() {
        TableSaver saver = new TableSaver(mock(DefaultJdbcConnector.class), TableFQName.forName("xt"))
                .deleteTableData();

        assertEquals(SaveViaDeleteThenInsert.class, saver.createSaveStrategy().getClass());
    }

    @Test
    public void testCreateSaveStrategy_DeleteUpsert() {
        TableSaver saver = new TableSaver(mock(DefaultJdbcConnector.class), TableFQName.forName("xt"))
                .deleteTableData()
                .mergeByPk();

        assertEquals("If DELETE is in effect, UPSERT should be replaced by INSERT",
                SaveViaDeleteThenInsert.class,
                saver.createSaveStrategy().getClass());
    }

    @Test
    public void testCreateSaveStrategy_Upsert_PK() {
        TableSaver saver = new TableSaver(mock(DefaultJdbcConnector.class), TableFQName.forName("xt")) {
            @Override
            protected String[] getPkColumns() {
                return new String[]{"X", "Y"};
            }
        };

        saver.mergeByPk();

        assertEquals(SaveViaUpsert.class, saver.createSaveStrategy().getClass());
    }

    @Test
    public void testCreateSaveStrategy_Upsert_Columns() {
        TableSaver saver = new TableSaver(mock(DefaultJdbcConnector.class), TableFQName.forName("xt"))
                .mergeByColumns("X", "Y");

        assertEquals(SaveViaUpsert.class, saver.createSaveStrategy().getClass());
    }
}
