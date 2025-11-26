package org.dflib.ql;

import org.dflib.Udf1;
import org.dflib.Udf2;
import org.dflib.Udf3;
import org.dflib.UdfN;
import org.dflib.exp.fn.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class QLFunctions {

    private final Map<String, Set<QLFunctionDescriptor>> functions;

    private QLFunctions(Map<String, Set<QLFunctionDescriptor>> functions) {
        this.functions = functions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean strFn(String fnName) {
        return descriptorsForTypeAndName(fnName, QLFunctionDescriptor.TypeClassifier.STRING)
                .findFirst()
                .isPresent();
    }

    public boolean numFn(String fnName) {
        return descriptorsForTypeAndName(fnName, QLFunctionDescriptor.TypeClassifier.NUMERIC)
                .findFirst()
                .isPresent();
    }

    public QLFunctionDescriptor function(String name, QLFunctionDescriptor.TypeClassifier type, List<QLFunctionDescriptor.TypeClassifier> argTypes) {
        return descriptorsForTypeAndName(name, type)
                .filter(d -> d.returnType() == type)
                .filter(d -> d.argTypes().length == argTypes.size())
                .filter(d -> {
                    for (int i = 0; i < d.argTypes().length; i++) {
                        if (d.argTypes()[i] != QLFunctionDescriptor.TypeClassifier.UNKNOWN
                                && d.argTypes()[i] != argTypes.get(i)) {
                            return false;
                        }
                    }
                    return true;
                })
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Function " + type + " " + name + "(" + argTypes + ") not found"
                ));
    }

    Stream<QLFunctionDescriptor> descriptorsForTypeAndName(String name, QLFunctionDescriptor.TypeClassifier type) {
        return functions.getOrDefault(name, Collections.emptySet())
                .stream()
                .filter(d -> d.returnType() == type);
    }

    public static class Builder {

        private final Map<String, Set<QLFunctionDescriptor>> functions = new ConcurrentHashMap<>();

        private Builder() {
        }

        public Builder function(String name, Udf1<?, ?> function) {
            return defineFunction(name, QLFunctionDescriptor.ofUdf1(function));
        }

        public Builder function(String name, Udf2<?, ?, ?> function) {
            return defineFunction(name, QLFunctionDescriptor.ofUdf2(function));
        }

        public Builder function(String name, Udf3<?, ?, ?, ?> function) {
            return defineFunction(name, QLFunctionDescriptor.ofUdf3(function));
        }

        public Builder function(String name, UdfN<?> function) {
            return defineFunction(name, QLFunctionDescriptor.ofUdfN(function));
        }

        private Builder defineFunction(String name, QLFunctionDescriptor.Builder builder) {
            QLFunctionDescriptor descriptor = builder.name(name).build();
            boolean hasSameDescriptor = !functions.computeIfAbsent(name, n -> new HashSet<>()).add(descriptor);
            if(hasSameDescriptor) {
                throw new IllegalArgumentException("Function " + name + "(" + Arrays.toString(descriptor.argTypes()) + ")  already defined");
            }

            return this;
        }

        public Builder defaultFunctions() {
            return this.function("trim", new TrimFunction())
                    .function("lower", new LowerFunction())
                    .function("upper", new UpperFunction())
                    .function("substr", new Substr2Function())
                    .function("substr", new Substr3Function())
                    .function("len", new LenFunction())
                    .function("abs", new AbsFunction())
                    .function("round", new RoundFunction());
        }

        public QLFunctions build() {
            return new QLFunctions(functions);
        }
    }
}
