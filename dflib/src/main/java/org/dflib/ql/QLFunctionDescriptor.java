package org.dflib.ql;

import org.dflib.DateExp;
import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.StrExp;
import org.dflib.TimeExp;
import org.dflib.Udf1;
import org.dflib.Udf2;
import org.dflib.Udf3;
import org.dflib.UdfN;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class QLFunctionDescriptor {

    final String name;
    final TypeClassifier returnType;
    final TypeClassifier[] argTypes;
    final Function<List<Exp<?>>, Exp<?>> fnExpProducer;

    private QLFunctionDescriptor(String name,
                                 TypeClassifier returnType,
                                 TypeClassifier[] argTypes,
                                 Function<List<Exp<?>>, Exp<?>> fnExpProducer) {
        this.name = name;
        this.returnType = returnType;
        this.argTypes = argTypes;
        this.fnExpProducer = fnExpProducer;
    }

    public static Builder ofUdf1(Udf1<?, ?> function) {
        return new Builder().udf1(function);
    }

    public static Builder ofUdf2(Udf2<?, ?, ?> function) {
        return new Builder().udf2(function);
    }

    public static Builder ofUdf3(Udf3<?, ?, ?, ?> function) {
        return new Builder().udf3(function);
    }

    public static Builder ofUdfN(UdfN<?> function) {
        return new Builder().udfN(function);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        QLFunctionDescriptor that = (QLFunctionDescriptor) o;
        return name.equals(that.name) && Arrays.equals(argTypes, that.argTypes);
    }

    public String name() {
        return name;
    }

    public TypeClassifier returnType() {
        return returnType;
    }

    public TypeClassifier[] argTypes() {
        return argTypes;
    }

    public Function<List<Exp<?>>, Exp<?>> expProducer() {
        return fnExpProducer;
    }

    @Override
    public int hashCode() {
        return 31 * name.hashCode() + Arrays.hashCode(argTypes);
    }

    public static class Builder {

        String name;
        TypeClassifier returnType;
        TypeClassifier[] argTypes;
        Function<List<Exp<?>>, Exp<?>> fnExpProducer;

        private Builder() {
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        public Builder udf1(Udf1<?, ?> function) {
            fnExpProducer = exps -> function.call((Exp)exps.get(0));
            inferTypes(getCallMethodSafe(function, 1));
            return this;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        public Builder udf2(Udf2<?, ?, ?> function) {
            fnExpProducer = exps -> function.call((Exp)exps.get(0), (Exp)exps.get(1));
            inferTypes(getCallMethodSafe(function, 2));
            return this;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        public Builder udf3(Udf3<?, ?, ?, ?> function) {
            fnExpProducer = exps -> function.call((Exp)exps.get(0), (Exp)exps.get(1), (Exp)exps.get(2));
            inferTypes(getCallMethodSafe(function, 3));
            return this;
        }

        public Builder udfN(UdfN<?> function) {
            fnExpProducer = exps -> function.call(exps.toArray(new Exp[0]));
            inferTypes(getCallMethodSafe(function, 4));
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        QLFunctionDescriptor build() {
            return new QLFunctionDescriptor(name, returnType, argTypes, fnExpProducer);
        }

        private void inferTypes(Method method) {
            Type genericReturnType = method.getGenericReturnType();
            returnType = TypeClassifier.classify(genericReturnType);
            Type[] genericParameterTypes = method.getGenericParameterTypes();
            argTypes = new TypeClassifier[genericParameterTypes.length];
            for (int i = 0; i < genericParameterTypes.length; i++) {
                argTypes[i] = TypeClassifier.classify(genericParameterTypes[i]);
            }
        }
    }

    public enum TypeClassifier {
        NUMERIC,
        STRING,
        BOOLEAN,
        DATE,
        TIME,
        DATETIME,
        UNKNOWN;

        public static TypeClassifier classify(Type type) {
            Class<?> expressionType = unwindGeneric(type);
            if (Number.class.isAssignableFrom(expressionType)) {
                return NUMERIC;
            } else if (CharSequence.class.isAssignableFrom(expressionType)) {
                return STRING;
            } else if (expressionType.equals(Boolean.class) || expressionType.equals(boolean.class)) {
                return BOOLEAN;
            } else if (expressionType.equals(java.time.LocalDate.class)) {
                return DATE;
            } else if (expressionType.equals(java.time.LocalTime.class)) {
                return TIME;
            } else if (expressionType.equals(java.time.LocalDateTime.class)) {
                return DATETIME;
            } else {
                return UNKNOWN;
            }
        }

        public static TypeClassifier classify(Exp<?> exp) {
            if(exp instanceof NumExp) {
                return QLFunctionDescriptor.TypeClassifier.NUMERIC;
            } else if(exp instanceof StrExp) {
                return QLFunctionDescriptor.TypeClassifier.STRING;
            } else if(exp instanceof DateExp) {
                return QLFunctionDescriptor.TypeClassifier.DATE;
            } else if(exp instanceof TimeExp) {
                return QLFunctionDescriptor.TypeClassifier.TIME;
            } else if(exp instanceof DateTimeExp) {
                return QLFunctionDescriptor.TypeClassifier.DATETIME;
            } else if(exp instanceof OffsetDateTimeExp) {
                return QLFunctionDescriptor.TypeClassifier.DATETIME;
            } else {
                return QLFunctionDescriptor.TypeClassifier.UNKNOWN;
            }
        }
    }

    static private Method getCallMethodSafe(Object function, int arity) {
        Class<?> aClass = function.getClass();
        try {
            switch (arity) {
                case 1:
                    return aClass.getDeclaredMethod("call", Exp.class);
                case 2:
                    return aClass.getDeclaredMethod("call", Exp.class, Exp.class);
                case 3:
                    return aClass.getDeclaredMethod("call", Exp.class, Exp.class, Exp.class);
                default:
                    return aClass.getDeclaredMethod("call", Exp[].class);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> unwindGeneric(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            return unwindGeneric(actualTypeArguments[0]);
        } else if (type instanceof GenericArrayType) {
            Type genericComponentType = ((GenericArrayType) type).getGenericComponentType();
            return unwindGeneric(genericComponentType);
        } else if (type instanceof WildcardType) {
            WildcardType wt = (WildcardType) type;
            Type[] lowerBounds = wt.getLowerBounds();
            if (lowerBounds.length > 0) {
                return unwindGeneric(lowerBounds[0]);
            }
            Type[] upperBounds = wt.getUpperBounds();
            if (upperBounds.length > 0) {
                return unwindGeneric(upperBounds[0]);
            }
            throw new IllegalArgumentException("Wildcard type with no bounds");
        } else if (type instanceof TypeVariable) {
            throw new RuntimeException("Variable type " + type + " can't be fully resolved");
        } else {
            throw new IllegalArgumentException("Unexpected type " + type);
        }
    }
}
