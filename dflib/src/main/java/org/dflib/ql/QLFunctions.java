package org.dflib.ql;

import org.dflib.Exp;
import org.dflib.Udf0;
import org.dflib.Udf1;
import org.dflib.Udf2;
import org.dflib.Udf3;
import org.dflib.UdfN;
import org.dflib.ql.antlr4.ExpLexer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.SequencedSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.dflib.ql.TypeClassifier.COERCION;
import static org.dflib.ql.TypeClassifier.NO_MATCH;
import static org.dflib.ql.TypeClassifier.WILDCARD;

/**
 * A registry of functions recognized by the QL parser.
 *
 * @since 2.0.0
 */
public class QLFunctions {

    private final Map<String, SequencedSet<QLFunctionDescriptor>> functions;

    private QLFunctions(Map<String, SequencedSet<QLFunctionDescriptor>> functions) {
        this.functions = functions;
    }

    /**
     * Starts a builder of a registry that includes the built-in functions, unless
     * {@link Builder#noDefaultFunctions()} is called.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Resolves a function call by its name and argument types and builds the call expression.
     *
     * @throws IllegalArgumentException if the function is unknown, no overload matches the arguments, or the
     *                                  matching overload rejects them
     */
    public Exp<?> call(String name, List<Exp<?>> args) {

        List<QLFunctionArg> argTypes = args.stream().map(QLFunctionArg::of).toList();
        QLFunctionDescriptor descriptor = function(name, argTypes);

        return descriptor.expProducer().apply(coerce(descriptor, args, argTypes));
    }

    /**
     * Casts the untyped arguments passed to typed parameters to the parameter types.
     */
    private static List<Exp<?>> coerce(QLFunctionDescriptor descriptor, List<Exp<?>> args, List<QLFunctionArg> argTypes) {

        List<QLFunctionArg> declared = descriptor.args();
        List<Exp<?>> coerced = null;

        for (int i = 0; i < declared.size(); i++) {
            if (QLFunctionArg.isCoerced(declared.get(i), argTypes.get(i))) {
                if (coerced == null) {
                    coerced = new ArrayList<>(args);
                }
                coerced.set(i, declared.get(i).cast(args.get(i)));
            }
        }

        return coerced != null ? coerced : args;
    }

    /**
     * Resolves a function by its name and best matching argument types.
     */
    QLFunctionDescriptor function(String name, List<QLFunctionArg> args) {

        SequencedSet<QLFunctionDescriptor> descriptors = functions.get(name);
        if (descriptors == null) {
            throw new IllegalArgumentException("Unknown function: " + name);
        }

        List<QLFunctionDescriptor> best = new ArrayList<>(2);
        MatchCost bestCost = null;

        for (QLFunctionDescriptor d : descriptors) {

            MatchCost cost = MatchCost.of(d, args);
            if (cost == null) {
                continue;
            }

            int cmp = bestCost == null ? -1 : cost.compareTo(bestCost);
            if (cmp < 0) {
                best.clear();
                best.add(d);
                bestCost = cost;
            } else if (cmp == 0) {
                best.add(d);
            }
        }

        if (best.isEmpty()) {
            throw notFound(name, args, descriptors);
        }

        if (best.size() > 1) {
            checkAmbiguity(name, args, best);
        }

        return best.getFirst();
    }

    /**
     * Throws if equally good candidates would cast the same untyped argument to different types.
     */
    private static void checkAmbiguity(String name, List<QLFunctionArg> args, List<QLFunctionDescriptor> candidates) {

        for (int i = 0; i < args.size(); i++) {
            // keyed by the full cast target, as e.g. NUMERIC<Long> and NUMERIC<Double> are different casts
            Set<String> casts = null;
            for (QLFunctionDescriptor d : candidates) {
                if (i < d.args().size() && QLFunctionArg.isCoerced(d.args().get(i), args.get(i))) {
                    if(casts == null) {
                        casts = new HashSet<>();
                    }
                    casts.add(d.args().get(i).typeLabel());
                }
            }

            if (casts != null && casts.size() > 1) {
                throw new IllegalArgumentException("Ambiguous call to " + QLFunctionDescriptor.shape(name, args, false)
                        + ": argument " + (i + 1) + " is "
                        + (args.get(i).type().isTyped() ? args.get(i).typeLabel() : "untyped")
                        + ", cast it explicitly.");
            }
        }
    }

    Stream<QLFunctionDescriptor> descriptors() {
        return functions.values().stream().flatMap(Collection::stream);
    }

    /**
     * How well the arguments fit a descriptor, lower being better.
     */
    private record MatchCost(boolean varArgs, int coercions, int wildcards, int anyNumTypes)
            implements Comparable<MatchCost> {

        private static final Comparator<MatchCost> ORDER = Comparator
                .comparing(MatchCost::varArgs)
                .thenComparingInt(MatchCost::coercions)
                .thenComparingInt(MatchCost::wildcards)
                .thenComparingInt(MatchCost::anyNumTypes);

        static MatchCost of(QLFunctionDescriptor descriptor, List<QLFunctionArg> args) {

            int declared = descriptor.args().size();

            if (descriptor.varArgs() ? args.size() < declared : args.size() != declared) {
                return null;
            }

            int coercions = 0;
            int wildcards = 0;
            int anyNumTypes = 0;
            for (int i = 0; i < declared; i++) {
                QLFunctionArg declaredArg = descriptor.args().get(i);
                switch (QLFunctionArg.matchCost(declaredArg, args.get(i))) {
                    case NO_MATCH -> {
                        return null;
                    }
                    case COERCION -> coercions++;
                    case WILDCARD -> wildcards++;
                    default -> {
                        // e.g. "decimal(a)" passed as "NumExp<?>" fits worse than passed as "DecimalExp"
                        if (declaredArg.numType() == null && args.get(i).numType() != null) {
                            anyNumTypes++;
                        }
                    }
                }
            }

            return new MatchCost(descriptor.varArgs(), coercions, wildcards, anyNumTypes);
        }

        @Override
        public int compareTo(MatchCost o) {
            return ORDER.compare(this, o);
        }
    }

    /**
     * Builds an error listing the registered overloads, and for an untyped argument in a position where the
     * overloads expect a type the resolver can not cast to, the casts that would make it match.
     */
    private static IllegalArgumentException notFound(
            String name,
            List<QLFunctionArg> args,
            Collection<QLFunctionDescriptor> descriptors) {

        String message = "No overload of " + name + " matches " +
                QLFunctionDescriptor.shape(name, args, false) +
                ". Available: " +
                descriptors.stream().map(QLFunctionDescriptor::shape).collect(Collectors.joining(", "));

        return new IllegalArgumentException(message);
    }

    private static Set<String> keywords() {
        Set<String> keywords = new HashSet<>();
        for (int i = 0; i <= ExpLexer.VOCABULARY.getMaxTokenType(); i++) {
            String literal = ExpLexer.VOCABULARY.getLiteralName(i);
            if (literal != null) {
                keywords.add(literal.substring(1, literal.length() - 1));
            }
        }
        return keywords;
    }

    /**
     * A builder of a function registry. A function is registered as a {@link QLFunction} class, or as a
     * {@code Udf0..UdfN} object; a lambda needs a type to be reflected on, so it is registered via
     * {@code Udf1.of(e -> ..)}, {@code Udf2.of(..)}, etc.
     */
    public static class Builder {

        // must match the "IDENTIFIER" lexer rule
        private static final Pattern IDENTIFIER = Pattern.compile("[$A-Z_a-z\\u0080-\\uFFFF][$A-Z_a-z0-9\\u0080-\\uFFFF]*");

        private static final Set<String> KEYWORDS = keywords();

        private final Map<String, SequencedSet<QLFunctionDescriptor>> functions = new LinkedHashMap<>();

        private boolean defaultFunctions = true;

        private Builder() {
        }

        public Builder function(String name, Udf0<?> function) {
            return function(QLFunctionReflection.udf0(name, function));
        }

        public Builder function(String name, Udf1<?, ?> function) {
            return function(QLFunctionReflection.udf1(name, function));
        }

        public Builder function(String name, Udf2<?, ?, ?> function) {
            return function(QLFunctionReflection.udf2(name, function));
        }

        public Builder function(String name, Udf3<?, ?, ?, ?> function) {
            return function(QLFunctionReflection.udf3(name, function));
        }

        public Builder function(String name, UdfN<?> function) {
            return function(QLFunctionReflection.udfN(name, function));
        }

        /**
         * Registers every public {@code call} overload of a {@link QLFunction} class as an overload of the function.
         */
        public Builder function(String name, QLFunction function) {
            for (QLFunctionDescriptor d : QLFunctionReflection.qlFunction(name, function)) {
                function(d);
            }

            return this;
        }

        Builder function(QLFunctionDescriptor descriptor) {
            String name = descriptor.name();
            checkName(name);

            boolean added = functions.computeIfAbsent(name, n -> new LinkedHashSet<>()).add(descriptor);
            if (!added) {
                throw new IllegalArgumentException("Function " + descriptor.shape() + " already defined");
            }

            return this;
        }

        private static void checkName(String name) {
            if (!IDENTIFIER.matcher(name).matches()) {
                throw new IllegalArgumentException("Function name must be a QL identifier (a letter, '$' or '_'"
                        + " followed by letters, digits, '$' or '_'), got: '" + name + "'");
            }

            if (KEYWORDS.contains(name)) {
                throw new IllegalArgumentException("Function name '" + name + "' is a QL keyword.");
            }
        }

        /**
         * Excludes the built-in functions from the registry, leaving only the explicitly registered ones.
         */
        public Builder noDefaultFunctions() {
            this.defaultFunctions = false;
            return this;
        }

        public QLFunctions build() {

            // built-ins go first, so that they win ties with custom functions
            Builder builder = new Builder().noDefaultFunctions();
            if (defaultFunctions) {
                DefaultQLFunctions.register(builder);
            }

            functions.values().forEach(descriptors -> descriptors.forEach(builder::function));
            return new QLFunctions(builder.functions);
        }
    }
}
