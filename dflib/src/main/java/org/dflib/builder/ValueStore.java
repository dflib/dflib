package org.dflib.builder;

/**
 * An abstraction of a mutable primitives-aware store of values.
 */
public interface ValueStore<T> {

    default void push(T v) {
        throw new UnsupportedOperationException("No support pushing values");
    }

    default void replace(int pos, T v) {
        throw new UnsupportedOperationException("No support for setting positional values");
    }

    default void pushBool(boolean v) {
        Object vo = v;
        push((T) vo);
    }

    default void replaceBool(int pos, boolean v) {
        Object vo = v;
        replace(pos, (T) vo);
    }

    default void pushInt(int v) {
        Object vo = v;
        push((T) vo);
    }

    default void replaceInt(int pos, int v) {
        Object vo = v;
        replace(pos, (T) vo);
    }

    default void pushLong(long v) {
        Object vo = v;
        push((T) vo);
    }

    default void replaceLong(int pos, long v) {
        Object vo = v;
        replace(pos, (T) vo);
    }

    /**
     * @since 1.1.0
     */
    default void pushFloat(float v) {
        Object vo = v;
        push((T) vo);
    }

    /**
     * @since 1.1.0
     */
    default void replaceFloat(int pos, float v) {
        Object vo = v;
        replace(pos, (T) vo);
    }

    default void pushDouble(double v) {
        Object vo = v;
        push((T) vo);
    }

    default void replaceDouble(int pos, double v) {
        Object vo = v;
        replace(pos, (T) vo);
    }
}
