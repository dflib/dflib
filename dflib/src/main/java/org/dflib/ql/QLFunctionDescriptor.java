package org.dflib.ql;

import org.dflib.Exp;
import org.dflib.Udf0;
import org.dflib.Udf1;
import org.dflib.Udf2;
import org.dflib.Udf3;
import org.dflib.UdfN;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A description of one overload of a QL function: its name, argument types and expression factory.
 */
record QLFunctionDescriptor(String name,
                            List<TypeClassifier> args,
                            boolean varArgs,
                            Function<List<Exp<?>>, Exp<?>> expProducer) {

    static QLFunctionDescriptor of(String name, Udf0<?> function) {
        return new QLFunctionDescriptor(name, List.of(), false, exps -> function.call());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static QLFunctionDescriptor of(String name, Udf1<?, ?> function) {
        return new QLFunctionDescriptor(name, args(function, 1), false,
                exps -> function.call((Exp) exps.get(0)));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static QLFunctionDescriptor of(String name, Udf2<?, ?, ?> function) {
        return new QLFunctionDescriptor(name, args(function, 2), false,
                exps -> function.call((Exp) exps.get(0), (Exp) exps.get(1)));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static QLFunctionDescriptor of(String name, Udf3<?, ?, ?, ?> function) {
        return new QLFunctionDescriptor(name, args(function, 3), false,
                exps -> function.call((Exp) exps.get(0), (Exp) exps.get(1), (Exp) exps.get(2)));
    }

    static QLFunctionDescriptor of(String name, UdfN<?> function) {
        return new QLFunctionDescriptor(name, List.of(), true, exps -> function.call(exps.toArray(new Exp[0])));
    }

    static QLFunctionDescriptor of(String name, Object function) {
        return switch (function) {
            case Udf0<?> f -> of(name, f);
            case Udf1<?, ?> f -> of(name, f);
            case Udf2<?, ?, ?> f -> of(name, f);
            case Udf3<?, ?, ?, ?> f -> of(name, f);
            case UdfN<?> f -> of(name, f);
            case null, default -> throw new IllegalArgumentException(
                    "Not a function: expected a Udf0..UdfN object, got: "
                            + (function != null ? function.getClass().getName() : "null"));
        };
    }

    /**
     * Reads the argument types off the {@code Exp<A>} parameters of the Udf "call" method. A lambda has them erased
     * to a plain {@code Exp}, so it takes arguments of any type.
     */
    private static List<TypeClassifier> args(Object function, int arity) {
        Class<?>[] erased = new Class<?>[arity];
        Arrays.fill(erased, Exp.class);

        Type[] params;
        try {
            params = function.getClass().getMethod("call", erased).getGenericParameterTypes();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }

        return Arrays.stream(params)
                .map(p -> p instanceof ParameterizedType pt && pt.getActualTypeArguments()[0] instanceof Class<?> c
                        ? TypeClassifier.classify(c)
                        : TypeClassifier.OBJECT)
                .toList();
    }

    /**
     * Renders the shape of the overload the way the error messages show it, e.g. {@code substr(OBJECT, NUMERIC)}
     * or {@code concat(...)}.
     */
    String shape() {
        return shape(name, args, varArgs);
    }

    static String shape(String name, List<TypeClassifier> args, boolean varArgs) {
        return name
                + "("
                + (varArgs ? "..." : args.stream().map(TypeClassifier::name).collect(Collectors.joining(", ")))
                + ")";
    }
}
