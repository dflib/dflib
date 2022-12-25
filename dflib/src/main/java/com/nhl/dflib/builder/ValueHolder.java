package com.nhl.dflib.builder;

/**
 * A holder of an object or a primitive value. Used as a flyweight to provides value access API that does not require
 * boxing/unboxing of primitives.
 *
 * @since 0.8
 */
public interface ValueHolder<T> extends ValueStore<T> {

    // used in conditions evaluation... no primitive flavors
    T get();

    void pushToStore(ValueStore<T> to);

    void pushToStore(ValueStore<T> to, int pos);
}
