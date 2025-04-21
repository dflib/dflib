package org.dflib.f;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @since 2.0.0
 */
public class CachingSupplier<T> implements Supplier<T> {

    private final Supplier<T> delegate;
    private volatile T value;

    public CachingSupplier(Supplier<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T get() {
        if (value == null) {
            synchronized (this) {
                if (value == null) {
                    value = Objects.requireNonNull(delegate.get(), "Delegate returned null value");
                }
            }
        }

        return value;
    }
}
