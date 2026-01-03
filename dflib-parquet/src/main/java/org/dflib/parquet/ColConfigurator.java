package org.dflib.parquet;

import org.apache.parquet.schema.Type;
import org.dflib.Index;
import org.dflib.parquet.read.converter.StoringConverter;

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

    // TODO: introduce compact ValueAccums ("compact" is the only flag influenced by ColConfigurator...
    //  The rest is purely based on Parquet schema)
    StoringConverter converter(Type colSchema) {
        return StoringConverter.of(colSchema, true);
    }
}
