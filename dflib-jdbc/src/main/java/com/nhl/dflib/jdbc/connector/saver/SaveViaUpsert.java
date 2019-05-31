package com.nhl.dflib.jdbc.connector.saver;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Hasher;
import com.nhl.dflib.Index;
import com.nhl.dflib.JoinType;
import com.nhl.dflib.jdbc.connector.JdbcConnector;
import com.nhl.dflib.jdbc.connector.TableLoader;

import java.sql.Connection;

/**
 * @since 0.6
 */
public class SaveViaUpsert extends SaveViaInsert {

    private String[] keyColumns;

    public SaveViaUpsert(JdbcConnector connector, String tableName, String[] keyColumns) {
        super(connector, tableName);
        this.keyColumns = keyColumns;
    }

    @Override
    protected void doSave(Connection connection, DataFrame df) {

        DataFrame keyDf = df.selectColumns(Index.forLabels(keyColumns));

        DataFrame toUpdate = new TableLoader(connector, tableName)
                // TODO: include all columns from "df" if we want to track changes before doing update
                .includeColumns(keyColumns)
                .eq(keyDf)
                .load();

        if (toUpdate.height() == 0) {
            insert(connection, df);
            return;
        }

        Hasher keyHasher = keyHasher();
        DataFrame insertAndUpdate = df.join(toUpdate, keyHasher, keyHasher, JoinType.left);

        super.doSave(connection, df);
    }

    protected void insert(Connection connection, DataFrame df) {
        super.doSave(connection, df);
    }

    protected void update(Connection connection, DataFrame df) {
        throw new UnsupportedOperationException("TODO");
    }

    protected Hasher keyHasher() {
        Hasher h = Hasher.forColumn(keyColumns[0]);
        for (int i = 1; i < keyColumns.length; i++) {
            h = h.and(keyColumns[i]);
        }

        return h;
    }
}
