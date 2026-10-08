package org.dflib.ql;

import org.dflib.Exp;
import org.dflib.Udf0;
import org.dflib.Udf1;
import org.dflib.Udf2;
import org.dflib.Udf3;
import org.dflib.UdfN;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Builds function descriptors by reflecting on {@code Udf} objects and {@link QLFunction} classes.
 */
class QLFunctionReflection {

    private QLFunctionReflection() {
    }

    static QLFunctionDescriptor udf0(String name, Udf0<?> function) {
        return reflect(name,
                callMethod(function), false,
                exps -> function.call());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static QLFunctionDescriptor udf1(String name, Udf1<?, ?> function) {
        return reflect(name,
                callMethod(function, Exp.class), false,
                exps -> function.call((Exp) exps.getFirst()));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static QLFunctionDescriptor udf2(String name, Udf2<?, ?, ?> function) {
        return reflect(name,
                callMethod(function, Exp.class, Exp.class), false,
                exps -> function.call((Exp) exps.getFirst(), (Exp) exps.get(1)));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static QLFunctionDescriptor udf3(String name, Udf3<?, ?, ?, ?> function) {
        return reflect(name,
                callMethod(function, Exp.class, Exp.class, Exp.class), false,
                exps -> function.call((Exp) exps.getFirst(), (Exp) exps.get(1), (Exp) exps.get(2)));
    }

    static QLFunctionDescriptor udfN(String name, UdfN<?> function) {
        return reflect(name,
                callMethod(function, Exp[].class), true,
                exps -> function.call(exps.toArray(new Exp[0])));
    }

    /**
     * Describes a Udf by the generic parameter types of its {@code call} method. A lambda has none (its parameters
     * are erased to a plain {@code Exp}), so a lambda registers as accepting arguments of any type.
     */
    private static QLFunctionDescriptor reflect(
            String name,
            Method method,
            boolean varArgs,
            Function<List<Exp<?>>, Exp<?>> producer) {

        List<QLFunctionArg> args = new ArrayList<>();
        if (!varArgs) {
            for (Type p : method.getGenericParameterTypes()) {
                args.add(expArg(p));
            }
        }

        return new QLFunctionDescriptor(name, args, varArgs, producer);
    }

    /**
     * Returns the public {@code call} methods declared in a class or inherited from its superclasses.
     */
    private static List<Method> callMethods(Class<?> type) {
        List<Method> methods = new ArrayList<>();
        Set<List<Class<?>>> seen = new HashSet<>();

        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            for (Method m : c.getDeclaredMethods()) {
                if ("call".equals(m.getName())
                        && Modifier.isPublic(m.getModifiers())
                        && !m.isBridge()
                        && !m.isSynthetic()
                        && seen.add(List.of(m.getParameterTypes()))) {

                    if (c != type) {
                        checkNoClassTypeVariables(type, m);
                    }

                    methods.add(m);
                }
            }
        }
        return methods;
    }

    private static void checkNoClassTypeVariables(Class<?> type, Method method) {

        List<Type> types = new ArrayList<>(Arrays.asList(method.getGenericParameterTypes()));
        types.add(method.getGenericReturnType());

        for (Type t : types) {
            TypeVariable<?> tv = findClassTypeVariable(t);
            if (tv != null) {
                throw new IllegalArgumentException("Function class " + type.getName()
                        + " inherits a 'call' method from a generic superclass, and its type variable '" + tv
                        + "' can not be resolved. Declare 'call' in " + type.getSimpleName()
                        + " with concrete types: " + method);
            }
        }
    }

    private static TypeVariable<?> findClassTypeVariable(Type type) {
        return switch (type) {
            case TypeVariable<?> tv -> tv.getGenericDeclaration() instanceof Class ? tv : null;
            case ParameterizedType pt -> Arrays.stream(pt.getActualTypeArguments())
                    .map(QLFunctionReflection::findClassTypeVariable)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
            case GenericArrayType at -> findClassTypeVariable(at.getGenericComponentType());
            case WildcardType wt -> Stream.concat(Arrays.stream(wt.getUpperBounds()), Arrays.stream(wt.getLowerBounds()))
                    .map(QLFunctionReflection::findClassTypeVariable)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
            default -> null;
        };
    }

    private static Method callMethod(Object function, Class<?>... parameterTypes) {
        Class<?> type = function.getClass();

        for (Method m : callMethods(type)) {
            if (Arrays.equals(m.getParameterTypes(), parameterTypes)) {
                return m;
            }
        }

        throw new IllegalStateException("No 'call' method with parameters " + Arrays.toString(parameterTypes)
                + " in " + type.getName());
    }

    /**
     * Orders overloads deterministically, as {@link Class#getDeclaredMethods()} order is unspecified and
     * registration order is the resolver's tie-break.
     */
    private static final Comparator<QLFunctionDescriptor> OVERLOAD_ORDER = Comparator
            .<QLFunctionDescriptor>comparingInt(d -> d.args().size())
            .thenComparing(QLFunctionReflection::argTypeOrdinals, Arrays::compare)
            .thenComparing(QLFunctionReflection::argConstancy, Arrays::compare)
            .thenComparing(QLFunctionReflection::argNumTypes, Arrays::compare)
            .thenComparing(QLFunctionDescriptor::varArgs);

    static List<QLFunctionDescriptor> qlFunction(String name, QLFunction function) {

        Class<?> type = function.getClass();

        if (function instanceof Udf0 || function instanceof Udf1 || function instanceof Udf2
                || function instanceof Udf3 || function instanceof UdfN) {
            throw new IllegalArgumentException("A QLFunction must not also implement Udf0..UdfN, as their default"
                    + " methods are public 'call' methods and would register as bogus signatures: " + type.getName());
        }

        if (!Modifier.isPublic(type.getModifiers())) {
            throw new IllegalArgumentException("A QLFunction class must be public to be invoked reflectively: "
                    + type.getName());
        }

        List<QLFunctionDescriptor> descriptors = new ArrayList<>();
        for (Method m : callMethods(type)) {
            if (!Modifier.isPublic(m.getDeclaringClass().getModifiers())) {
                throw new IllegalArgumentException("A QLFunction 'call' method must be declared in a public class"
                        + " to be invoked reflectively: " + m);
            }
            descriptors.add(reflectCall(name, function, m));
        }

        if (descriptors.isEmpty()) {
            throw new IllegalArgumentException(
                    "A QLFunction must declare at least one public 'call' method: " + type.getName());
        }

        descriptors.sort(OVERLOAD_ORDER);
        return descriptors;
    }

    private static QLFunctionDescriptor reflectCall(String name, QLFunction function, Method method) {

        if (!Exp.class.isAssignableFrom(method.getReturnType())) {
            throw new IllegalArgumentException("A QLFunction 'call' method must return an Exp, got "
                    + method.getReturnType().getName() + ": " + method);
        }

        Parameter[] parameters = method.getParameters();
        boolean varArgs = method.isVarArgs();
        int declared = varArgs ? parameters.length - 1 : parameters.length;

        List<QLFunctionArg> args = new ArrayList<>(declared);
        for (int i = 0; i < declared; i++) {
            args.add(callArg(method, parameters[i]));
        }

        if (varArgs) {
            // the resolver does not check the types of the vararg tail, so only an untyped "Exp<?>..." is allowed
            if (parameters[parameters.length - 1].getType().getComponentType() != Exp.class) {
                throw new IllegalArgumentException(
                        "A vararg parameter of a QLFunction must be an Exp<?> array: " + method);
            }
        }

        return new QLFunctionDescriptor(name, args, varArgs, new CallProducer(name, function, method));
    }

    private static QLFunctionArg callArg(Method method, Parameter parameter) {

        Class<?> raw = parameter.getType();

        if (Exp.class.isAssignableFrom(raw)) {
            return expArg(parameter.getParameterizedType());
        }

        if (!CallProducer.CONSTANT_TYPES.contains(raw)) {
            throw new IllegalArgumentException("A non-Exp parameter of a QLFunction is an implicit constant"
                    + " argument, and must be declared as one of " + CallProducer.CONSTANT_TYPES.stream()
                    .map(Class::getSimpleName).distinct().toList() + ", got " + raw.getName() + ": " + method);
        }

        return new QLFunctionArg(TypeClassifier.classify(raw), true);
    }

    private static QLFunctionArg expArg(Type type) {
        return new QLFunctionArg(TypeClassifier.classify(type), false, TypeClassifier.numericValueType(type));
    }

    private static int[] argTypeOrdinals(QLFunctionDescriptor d) {
        return d.args().stream().mapToInt(a -> a.type().ordinal()).toArray();
    }

    private static String[] argNumTypes(QLFunctionDescriptor d) {
        return d.args().stream().map(a -> a.numType() != null ? a.numType().getName() : "").toArray(String[]::new);
    }

    private static int[] argConstancy(QLFunctionDescriptor d) {
        return d.args().stream().mapToInt(a -> a.constant() ? 1 : 0).toArray();
    }
}
