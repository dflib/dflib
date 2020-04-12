package com.nhl.dflib.jdbc.unit.dbadapter;

class MySQLTestAdapter implements TestDbAdapter {

    private static final String QUOTE = "`";

    @Override
    public String toNativeSql(String command) {
        if (command.contains("SUBSTR")) {
            return command
                    .replaceAll("\"", QUOTE)
                    .replaceAll("SUBSTR", "SUBSTRING");
        }
        return command.replaceAll("\"", QUOTE);
    }
}
