package org.dflib.ql;

import org.dflib.Exp;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A fluent test helper assembling a {@link QLFunctionDescriptor} of an arbitrary shape.
 */
class DescriptorBuilder {

    private final String name;
    private final List<QLFunctionArg> args = new ArrayList<>();
    private boolean varArgs;

    private DescriptorBuilder(String name) {
        this.name = name;
    }

    static DescriptorBuilder descriptor(String name) {
        return new DescriptorBuilder(name);
    }

    DescriptorBuilder arg(TypeClassifier type) {
        return arg(new QLFunctionArg(type, false));
    }

    DescriptorBuilder constArg(TypeClassifier type) {
        return arg(new QLFunctionArg(type, true));
    }

    DescriptorBuilder arg(QLFunctionArg arg) {
        this.args.add(arg);
        return this;
    }

    DescriptorBuilder varArgs() {
        this.varArgs = true;
        return this;
    }

    QLFunctionDescriptor as(Function<List<Exp<?>>, Exp<?>> producer) {
        return new QLFunctionDescriptor(name, args, varArgs, producer);
    }
}
