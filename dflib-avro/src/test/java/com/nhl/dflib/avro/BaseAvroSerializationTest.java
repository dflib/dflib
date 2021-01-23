package com.nhl.dflib.avro;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.avro.types.AvroTypeExtensions;
import org.apache.avro.generic.GenericData;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class BaseAvroSerializationTest {

    @BeforeEach
    protected void clearTypeStatics() {

        // dirty - changing private vars via reflection, as there's no public API to do it
        try {
            Field fConversions = GenericData.class.getDeclaredField("conversions");
            fConversions.setAccessible(true);
            Map<?, ?> conversions = (Map<?, ?>) fConversions.get(GenericData.get());
            conversions.clear();

            Field fConversionsByClass = GenericData.class.getDeclaredField("conversionsByClass");
            fConversionsByClass.setAccessible(true);
            Map<?, ?> conversionsByClass = (Map<?, ?>) fConversionsByClass.get(GenericData.get());
            conversionsByClass.clear();

        } catch (Exception e) {
            throw new RuntimeException("Error resetting static state of Avro types", e);
        } finally {
            // reinit statics
            AvroTypeExtensions.init();
        }
    }

    protected byte[] save(DataFrame df) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Avro.saveData(df, out);
        byte[] bytes = out.toByteArray();
        assertTrue(bytes.length > 0, "No bytes generated");
        return bytes;
    }

    protected DataFrame load(byte[] bytes) {
        DataFrame df = Avro.load(bytes);
        assertNotNull(df);
        return df;
    }

    protected DataFrame saveAndLoad(DataFrame df) {
        return load(save(df));
    }


}
