package org.dflib.ql;

import org.dflib.Exp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A registry of functions recognized by the QL parser. Functions of the same name are told apart by the number of
 * arguments, with a vararg function taking the calls that no fixed-arity one does.
 */
class QLFunctions {

    // the name a function passed as a parameter is reported under
    private static final String UDF_NAME = "?";

    private final Map<String, List<QLFunctionDescriptor>> functions;

    QLFunctions(List<QLFunctionDescriptor> descriptors) {
        this.functions = new LinkedHashMap<>();

        for (QLFunctionDescriptor d : descriptors) {
            List<QLFunctionDescriptor> overloads = functions.computeIfAbsent(d.name(), n -> new ArrayList<>());
            if (overloads.stream().anyMatch(o -> o.varArgs() == d.varArgs() && o.args().size() == d.args().size())) {
                throw new IllegalArgumentException("Function " + d.shape() + " already defined");
            }
            overloads.add(d);
        }
    }

    /**
     * Resolves a function by its name and the number of arguments, checks the argument types and builds the call
     * expression. Untyped arguments are cast to the types of the typed parameters.
     */
    Exp<?> call(String name, List<Exp<?>> args) {

        List<QLFunctionDescriptor> overloads = functions.get(name);
        if (overloads == null) {
            throw new IllegalArgumentException("Unknown function: " + name);
        }

        List<TypeClassifier> argTypes = args.stream().map(TypeClassifier::classify).toList();
        QLFunctionDescriptor descriptor = byArity(overloads, args.size());
        if (descriptor == null) {
            throw notFound(name, argTypes, overloads);
        }

        // a vararg function declares no argument types
        List<TypeClassifier> declared = descriptor.args();
        List<Exp<?>> cast = new ArrayList<>(args);
        for (int i = 0; i < declared.size(); i++) {
            TypeClassifier d = declared.get(i);
            TypeClassifier a = argTypes.get(i);

            if (a == TypeClassifier.OBJECT) {
                cast.set(i, d.cast(args.get(i)));
            } else if (d != a && d != TypeClassifier.OBJECT) {
                throw notFound(name, argTypes, overloads);
            }
        }

        return descriptor.expProducer().apply(cast);
    }

    /**
     * Builds a call of a function that is not in a registry, but is passed to the parser as a {@code ?(..)} parameter.
     */
    static Exp<?> callUdf(Object function, List<Exp<?>> args) {
        return new QLFunctions(List.of(QLFunctionDescriptor.of(UDF_NAME, function))).call(UDF_NAME, args);
    }

    private static QLFunctionDescriptor byArity(List<QLFunctionDescriptor> overloads, int arity) {
        QLFunctionDescriptor varArgs = null;
        for (QLFunctionDescriptor d : overloads) {
            if (d.varArgs()) {
                varArgs = d;
            } else if (d.args().size() == arity) {
                return d;
            }
        }
        return varArgs;
    }

    Stream<QLFunctionDescriptor> descriptors() {
        return functions.values().stream().flatMap(Collection::stream);
    }

    private static IllegalArgumentException notFound(
            String name,
            List<TypeClassifier> args,
            List<QLFunctionDescriptor> overloads) {

        return new IllegalArgumentException("No overload of " + name + " matches "
                + QLFunctionDescriptor.shape(name, args, false)
                + ". Available: "
                + overloads.stream().map(QLFunctionDescriptor::shape).collect(Collectors.joining(", ")));
    }
}
