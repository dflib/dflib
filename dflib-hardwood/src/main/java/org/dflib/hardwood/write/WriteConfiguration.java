package org.dflib.hardwood.write;

import org.dflib.hardwood.TimeUnit;

/**
 * @since 2.0.0
 */
public record WriteConfiguration(TimeUnit timeUnit, DecimalConfig decimalConfig) {
}
