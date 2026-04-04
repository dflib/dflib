package org.dflib.jdbc.connector.metadata.flavors;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * @deprecated in favor of {@link DbFlavor#of(DatabaseMetaData)}
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public class DbFlavorFactory {

    /**
     * @deprecated in favor of {@link DbFlavor#of(DatabaseMetaData)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static DbFlavor create(DataSource dataSource) {

        try (Connection c = dataSource.getConnection()) {
            return DbFlavor.of(c.getMetaData());
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to DB or retrieving DB metadata");
        }
    }
}
