package org.dflib.avro;

import org.dflib.avro.schema.AvroSchemaCompiler;
import org.dflib.avro.types.AvroTypeExtensions;

import java.io.File;


public abstract class BaseSaver<SELF extends BaseSaver<SELF>> {

    static {
        AvroTypeExtensions.initIfNeeded();
    }

    private boolean createMissingDirs;
    protected final AvroSchemaCompiler schemaBuilder;

    protected BaseSaver() {
        this.schemaBuilder = new AvroSchemaCompiler();
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
     * Sets the schema namespace of the generated Avro file. Optional. The default will be "org.dflib".
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
