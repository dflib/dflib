package org.dflib.ql;

import org.dflib.Exp;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * A description of one overload of a QL function: its name, argument shape and expression factory.
 */
record QLFunctionDescriptor(String name,
                            List<QLFunctionArg> args,
                            boolean varArgs,
                            Function<List<Exp<?>>, Exp<?>> expProducer) {

    QLFunctionDescriptor(String name, List<QLFunctionArg> args, boolean varArgs, Function<List<Exp<?>>, Exp<?>> expProducer) {
        this.name = Objects.requireNonNull(name);
        this.args = List.copyOf(args);
        this.varArgs = varArgs;
        this.expProducer = Objects.requireNonNull(expProducer);
    }

    /**
     * Renders the shape of the overload the way the error messages show it, e.g. {@code substr(OBJECT, const NUMERIC)}
     * or {@code concat(OBJECT...)}.
     */
    String shape() {
        return shape(name, args, varArgs);
    }

    static String shape(String name, List<?> args, boolean varArgs) {
        StringBuilder out = new StringBuilder(name).append('(');
        for (int i = 0; i < args.size(); i++) {
            if (i > 0) {
                out.append(", ");
            }
            out.append(args.get(i));
        }

        if (varArgs) {
            out.append(args.isEmpty() ? "..." : ", ...");
        }

        return out.append(')').toString();
    }

    /**
     * Descriptors with the same name and argument shape are equal, as the parser can not tell them apart.
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof QLFunctionDescriptor that
                && name.equals(that.name)
                && varArgs == that.varArgs
                && args.equals(that.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, args, varArgs);
    }
}
