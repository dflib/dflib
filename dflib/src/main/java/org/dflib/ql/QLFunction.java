package org.dflib.ql;

/**
 * A marker for a class implementing one QL function as a set of public {@code call} overloads, registered via
 * {@link QLContext.Builder#function(String, QLFunction)}.
 *
 * @since 2.0.0
 */
public interface QLFunction {
}
