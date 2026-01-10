package org.dflib.avro.write;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * A GenericData that does a better job of type mapping for the Avro primitive types that are not covered by converters.
 *
 * @since 2.0.0
 */
public class GenericDataForSave extends GenericData {

    private final Map<Class<?>, String> primitiveTypeCache;

    public GenericDataForSave() {
        this.primitiveTypeCache = new IdentityHashMap<>(super.getPrimitiveTypeCache());
        this.primitiveTypeCache.put(byte[].class, Schema.Type.BYTES.getName());
    }

    @Override
    public Map<Class<?>, String> getPrimitiveTypeCache() {
        return primitiveTypeCache;
    }
}
