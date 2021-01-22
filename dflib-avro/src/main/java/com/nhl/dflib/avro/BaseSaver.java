package com.nhl.dflib.avro;

import java.io.File;

/**
 * @since 0.11
 */
public abstract class BaseSaver<SELF extends BaseSaver<SELF>> {

    private boolean createMissingDirs;
    protected final AvroSchemaBuilder schemaBuilder;

    protected BaseSaver() {
        this.schemaBuilder = new AvroSchemaBuilder();
    }

    /**
     * Instructs the saver to create any missing directories in the file path.
     *
     * @return this saver instance
     */
    public SELF createMissingDirs() {
        this.createMissingDirs = true;
        return (SELF) this;
    }

    /**
     * Sets the schema name of the generated Avro file. Optional. The default will be "DataFrame".
     */
    public SELF name(String name) {
        this.schemaBuilder.name(name);
        return (SELF) this;
    }

    /**
     * Sets the schema namespace of the generated Avro file. Optional. The default will be "com.nhl.dflib".
     */
    public SELF namespace(String namespace) {
        this.schemaBuilder.namespace(namespace);
        return (SELF) this;
    }

    protected void createMissingDirsIfNeeded(File file) {
        if (createMissingDirs) {
            File dir = file.getParentFile();
            if (dir != null) {
                dir.mkdirs();
            }
        }
    }
}
