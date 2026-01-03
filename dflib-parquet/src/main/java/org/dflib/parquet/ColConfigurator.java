package org.dflib.parquet;

import org.apache.parquet.schema.Type;
import org.dflib.Index;
import org.dflib.parquet.read.converter.StoringConverter;

class ColConfigurator {
    int DEFAULT_CAPACITY = 1000;

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

    StoringConverter converter(Type colSchema) {
        // TODO: can we get the file height from the Parquet file to set capacity?
        return StoringConverter.ofAccum(colSchema, DEFAULT_CAPACITY, compact);
    }
}
