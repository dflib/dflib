package org.dflib.jdbc.connector;

import org.dflib.Extractor;
import org.dflib.jdbc.connector.metadata.DbMetadata;
import org.dflib.jdbc.connector.metadata.TableFQName;
import org.dflib.jdbc.connector.saver.SaveViaDeleteThenInsert;
import org.dflib.jdbc.connector.saver.SaveViaInsert;
import org.dflib.jdbc.connector.saver.SaveViaUpsert;
import org.dflib.jdbc.connector.statement.ValueConverterFactory;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TableSaverTest {

    @Test
    public void createSaveStrategy_Default() {
        TableSaver saver = new TableSaver(new TestJdbcConnector(), TableFQName.forName("xt"));

        assertEquals(SaveViaInsert.class, saver.createSaveStrategy().getClass());
    }

    @Test
    public void createSaveStrategy_DeleteInsert() {
        TableSaver saver = new TableSaver(new TestJdbcConnector(), TableFQName.forName("xt"))
                .deleteTableData();

        assertEquals(SaveViaDeleteThenInsert.class, saver.createSaveStrategy().getClass());
    }

    @Test
    public void createSaveStrategy_DeleteUpsert() {
        TableSaver saver = new TableSaver(new TestJdbcConnector(), TableFQName.forName("xt"))
                .deleteTableData()
                .mergeByPk();

        assertEquals(SaveViaDeleteThenInsert.class, saver.createSaveStrategy().getClass(),
                "If DELETE is in effect, UPSERT should be replaced by INSERT");
    }

    @Test
    public void createSaveStrategy_Upsert_PK() {
        TableSaver saver = new TableSaver(new TestJdbcConnector(), TableFQName.forName("xt")) {
            @Override
            protected String[] getPkColumns() {
                return new String[]{"X", "Y"};
            }
        };

        saver.mergeByPk();

        assertEquals(SaveViaUpsert.class, saver.createSaveStrategy().getClass());
    }

    @Test
    public void createSaveStrategy_Upsert_Columns() {
        TableSaver saver = new TableSaver(new TestJdbcConnector(), TableFQName.forName("xt"))
                .mergeByColumns("X", "Y");

        assertEquals(SaveViaUpsert.class, saver.createSaveStrategy().getClass());
    }

    static class TestJdbcConnector implements JdbcConnector {
        @Override
        public Extractor<ResultSet, ?> createExtractor(int resultSetPosition, int type, boolean mandatory) {
            throw new UnsupportedOperationException();
        }

        @Override
        public TableSaver tableSaver(String tableName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public TableSaver tableSaver(TableFQName tableName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public TableLoader tableLoader(String tableName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public TableLoader tableLoader(TableFQName tableName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public TableDeleter tableDeleter(String tableName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public TableDeleter tableDeleter(TableFQName tableName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SqlLoader sqlLoader(String sql) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SqlSaver sqlSaver(String sql) {
            throw new UnsupportedOperationException();
        }

        @Override
        public StatementBuilder createStatementBuilder(String sql) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DbMetadata getMetadata() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DataSource getDataSource() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Connection getConnection() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String quoteIdentifier(String bareIdentifier) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String quoteTableName(TableFQName tableName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SqlLogger getSqlLogger() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ValueConverterFactory getBindConverterFactory() {
            throw new UnsupportedOperationException();
        }
    }
}
