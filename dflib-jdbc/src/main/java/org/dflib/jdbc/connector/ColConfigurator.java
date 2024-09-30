package org.dflib.jdbc.connector;

import org.dflib.Extractor;
import org.dflib.Index;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

class ColConfigurator {

    int srcColPos;
    String srcColName;
    boolean compact;

    private ColConfigurator() {
        srcColPos = -1;
    }

    static ColConfigurator objectCol(int pos, boolean compact) {
        ColConfigurator config = new ColConfigurator();
        config.srcColPos = pos;
        config.compact = compact;
        return config;
    }

    static ColConfigurator objectCol(String name, boolean compact) {
        ColConfigurator config = new ColConfigurator();
        config.srcColName = name;
        config.compact = compact;
        return config;
    }

    int srcPos(Index header) {
        return srcColPos >= 0 ? srcColPos : header.position(srcColName);
    }

    Extractor<ResultSet, ?> extractor(int srcPos, JdbcConnector connector, ResultSetMetaData schema) throws SQLException {
        Extractor<ResultSet, ?> e = sparseExtractor(srcPos, connector, schema);
        return compact ? e.compact() : e;
    }

    private static Extractor<ResultSet, ?> sparseExtractor(int pos, JdbcConnector connector, ResultSetMetaData schema) throws SQLException {
        int jdbcPos = pos + 1;
        return connector.createExtractor(
                jdbcPos,
                schema.getColumnType(jdbcPos),
                schema.isNullable(jdbcPos) == ResultSetMetaData.columnNoNulls);
    }
}
