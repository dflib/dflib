package org.dflib.echarts.render.util;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A generator of HTML container IDs.
 *
 * @since 2.0.0
 */
@FunctionalInterface
public interface ElementIdGenerator {

    String nextId();

    static ElementIdGenerator random() {
        Random rnd = new SecureRandom();
        return () -> "dfl_ech_" + Math.abs(rnd.nextInt(10_000));
    }

    static ElementIdGenerator sequential() {
        AtomicInteger ids = new AtomicInteger(0);
        return () -> "dfl_ech_" + ids.incrementAndGet();
    }
}
