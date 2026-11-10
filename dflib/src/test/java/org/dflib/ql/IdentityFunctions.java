package org.dflib.ql;

import static org.dflib.ql.DescriptorBuilder.descriptor;

/**
 * Test stand-ins for the built-in functions whose return type is the type of their receiver.
 */
class IdentityFunctions {

    private IdentityFunctions() {
    }

    /**
     * Registers a function returning its first argument unchanged, with one overload per receiver type plus an
     * untyped one.
     */
    static QLFunctions.Builder identity(QLFunctions.Builder builder, String name, QLFunctionArg... trailingArgs) {

        for (TypeClassifier t : TypeClassifier.values()) {
            builder.function(overload(name, t, trailingArgs));
        }

        return builder;
    }

    private static QLFunctionDescriptor overload(String name, TypeClassifier receiver, QLFunctionArg... trailingArgs) {

        DescriptorBuilder b = descriptor(name).arg(receiver);
        for (QLFunctionArg a : trailingArgs) {
            b.arg(a);
        }
        return b.as(args -> args.get(0));
    }
}
