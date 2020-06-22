package com.nhl.dflib.jdbc.connector.saver;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.jdbc.SaveOp;
import com.nhl.dflib.jdbc.connector.JdbcConnector;
import com.nhl.dflib.jdbc.connector.TableDeleter;
import com.nhl.dflib.jdbc.connector.metadata.TableFQName;
import com.nhl.dflib.series.EmptySeries;

import java.util.function.Supplier;

/**
 * @since 0.6
 */
public class SaveViaDeleteThenInsert extends SaveViaInsert {

    public SaveViaDeleteThenInsert(JdbcConnector connector, TableFQName tableName) {
        super(connector, tableName);
    }

    @Override
    protected boolean shouldSave(DataFrame df) {
        return true;
    }

    @Override
    protected Supplier<Series<SaveOp>> doSave(JdbcConnector connector, DataFrame df) {

        new TableDeleter(connector, tableName).delete();

        if (df.height() > 0) {
            return super.doSave(connector, df);
        } else {
            log("Empty DataFrame. Skipping insert.");
            return () -> new EmptySeries<>();
        }
    }
}
