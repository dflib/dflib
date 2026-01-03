package org.dflib.parquet.read.converter;

import org.apache.parquet.column.Dictionary;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.ObjectHolder;
import org.dflib.builder.ValueStore;

import java.time.LocalDate;

class LocalDateConverter extends StoringPrimitiveConverter<LocalDate> {

    public static LocalDateConverter of(boolean accum, int accumCapacity, boolean allowsNulls) {
        ValueStore<LocalDate> store = accum ? new ObjectAccum<>(accumCapacity) : new ObjectHolder<>();
        return new LocalDateConverter(store, allowsNulls);
    }

    private LocalDate[] dict;

    protected LocalDateConverter(ValueStore<LocalDate> store, boolean allowsNulls) {
        super(store, allowsNulls);
    }

    @Override
    public void addInt(int daysFromEpoch) {
        store.push(convert(daysFromEpoch));
    }

    @Override
    public boolean hasDictionarySupport() {
        return true;
    }

    @Override
    public void setDictionary(Dictionary dictionary) {
        int maxId = dictionary.getMaxId();
        dict = new LocalDate[maxId + 1];
        for (int i = 0; i <= maxId; i++) {
            dict[i] = convert(dictionary.decodeToInt(i));
        }
    }

    @Override
    public void addValueFromDictionary(int dictionaryId) {
        store.push(dict[dictionaryId]);
    }

    private LocalDate convert(int daysFromEpoch) {
        return LocalDate.ofEpochDay(daysFromEpoch);
    }

}
