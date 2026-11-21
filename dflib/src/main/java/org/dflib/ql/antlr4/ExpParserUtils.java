package org.dflib.ql.antlr4;

import org.antlr.v4.runtime.Token;
import org.dflib.Condition;
import org.dflib.DateExp;
import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.Series;
import org.dflib.StrExp;
import org.dflib.TimeExp;
import org.dflib.exp.ScalarExp;
import org.dflib.exp.str.StrScalarExp;
import org.dflib.ql.QLContext;
import org.dflib.ql.QLFunctions;
import org.dflib.ql.QLParserException;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Actions of the QL grammar that build expressions. The grammar is untyped, so the operators check the types of
 * the parsed operands here and report mismatches as positioned {@link QLParserException}s.
 */
class ExpParserUtils {

    // Types supporting all the comparison operators. Temporal ones also take ISO-8601 string literals as operands.

    private static final RelOpSupport<NumExp<?>> NUM = new RelOpSupport<>(NumExp.class, Number.class, "numeric", null,
            NumExp::gt, NumExp::ge, NumExp::lt, NumExp::le, NumExp::between, NumExp::notBetween);

    private static final RelOpSupport<TimeExp> TIME = new RelOpSupport<>(TimeExp.class, LocalTime.class, "time", LocalTime::parse,
            TimeExp::gt, TimeExp::ge, TimeExp::lt, TimeExp::le, TimeExp::between, TimeExp::notBetween);

    private static final RelOpSupport<DateExp> DATE = new RelOpSupport<>(DateExp.class, LocalDate.class, "date", LocalDate::parse,
            DateExp::gt, DateExp::ge, DateExp::lt, DateExp::le, DateExp::between, DateExp::notBetween);

    private static final RelOpSupport<DateTimeExp> DATE_TIME = new RelOpSupport<>(DateTimeExp.class, LocalDateTime.class, "datetime", LocalDateTime::parse,
            DateTimeExp::gt, DateTimeExp::ge, DateTimeExp::lt, DateTimeExp::le, DateTimeExp::between, DateTimeExp::notBetween);

    private static final RelOpSupport<OffsetDateTimeExp> OFFSET_DATE_TIME = new RelOpSupport<>(OffsetDateTimeExp.class, OffsetDateTime.class, "offset datetime", OffsetDateTime::parse,
            OffsetDateTimeExp::gt, OffsetDateTimeExp::ge, OffsetDateTimeExp::lt, OffsetDateTimeExp::le, OffsetDateTimeExp::between, OffsetDateTimeExp::notBetween);

    // Scalars

    static Exp<?> val(Object value) {
        return switch (value) {
            case Integer i -> Exp.$intVal(i);
            case Long l -> Exp.$longVal(l);
            case Float f -> Exp.$floatVal(f);
            case Double d -> Exp.$doubleVal(d);
            case BigInteger bi -> Exp.$bigintVal(bi);
            case BigDecimal bd -> Exp.$decimalVal(bd);
            case Boolean b -> Exp.$boolVal(b);
            case String s -> Exp.$strVal(s);
            case LocalTime t -> Exp.$timeVal(t);
            case LocalDate d -> Exp.$dateVal(d);
            case LocalDateTime dt -> Exp.$dateTimeVal(dt);
            case OffsetDateTime odt -> Exp.$offsetDateTimeVal(odt);
            case null, default -> Exp.$val(value);
        };
    }

    // Columns

    /**
     * Builds a column reference of the type named by the keyword token.
     */
    static Exp<?> col(Token type, Object columnId) {

        if (!(columnId instanceof Integer || columnId instanceof String)) {
            throw new QLParserException("Column '" + type.getText() + "(..)' at " + position(type)
                    + " expects an integer index or a string name, got: "
                    + (columnId != null ? columnId + " (" + columnId.getClass().getSimpleName() + ")" : "null"));
        }

        return switch (type.getType()) {
            case ExpParser.INT -> col(columnId, Exp::$int, Exp::$int);
            case ExpParser.LONG -> col(columnId, Exp::$long, Exp::$long);
            case ExpParser.BIGINT -> col(columnId, Exp::$bigint, Exp::$bigint);
            case ExpParser.FLOAT -> col(columnId, Exp::$float, Exp::$float);
            case ExpParser.DOUBLE -> col(columnId, Exp::$double, Exp::$double);
            case ExpParser.DECIMAL -> col(columnId, Exp::$decimal, Exp::$decimal);
            case ExpParser.BOOL -> col(columnId, Exp::$bool, Exp::$bool);
            case ExpParser.STR -> col(columnId, Exp::$str, Exp::$str);
            case ExpParser.DATE -> col(columnId, Exp::$date, Exp::$date);
            case ExpParser.TIME -> col(columnId, Exp::$time, Exp::$time);
            case ExpParser.DATETIME -> col(columnId, Exp::$dateTime, Exp::$dateTime);
            case ExpParser.OFFSET_DATETIME -> col(columnId, Exp::$offsetDateTime, Exp::$offsetDateTime);
            case ExpParser.COL -> col(columnId);
            default -> throw new IllegalStateException("Not a column type: " + type.getText());
        };
    }

    /**
     * Builds an untyped column reference. The grammar passes a column name here, or an id already checked by
     * {@link #col(Token, Object)}.
     */
    static Exp<?> col(Object columnId) {
        return col(columnId, Exp::$col, Exp::$col);
    }

    private static <T> T col(Object columnId, Function<Integer, T> byIndex, Function<String, T> byName) {
        return switch (columnId) {
            case Integer i -> byIndex.apply(i);
            case String s -> byName.apply(s);
            case null, default -> throw new IllegalStateException("Column id checked by the caller: " + columnId);
        };
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static Exp<?> array(Exp exp, String type) {
        Object[] array;
        try {
            array = (Object[]) Array.newInstance(Class.forName(type), 0);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return exp.array(array);
    }

    // Functions

    /**
     * Resolves a function call against the registry and builds its expression. Resolution and argument errors, and
     * anything thrown by the function body, are reported as a {@link QLParserException} positioned at the call.
     */
    static Exp<?> fn(QLContext context, Token name, List<Exp<?>> args) {
        return call(name, () -> context.functions().call(name.getText(), args));
    }

    /**
     * Builds a call of a function passed as a {@code ?f} parameter, with the same error reporting as
     * {@link #fn(QLContext, Token, List)}. The parameter source has already checked that the value is a function
     * object.
     */
    static Exp<?> udf(Token placeholder, Object function, List<Exp<?>> args) {
        return call(placeholder, () -> QLFunctions.callUdf(function, args));
    }

    private static Exp<?> call(Token name, Supplier<Exp<?>> call) {
        try {
            return call.get();
        } catch (QLParserException e) {
            // a nested call already reported its own position
            throw e;
        } catch (RuntimeException e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            throw new QLParserException("Function '" + name.getText() + "' at " + position(name) + ": " + message, e);
        }
    }

    // Operators. Each one checks the operands against the type of expression it applies to.

    /**
     * Negates a numeric expression. A negated literal is folded into a literal, so that it is still a constant, e.g.
     * for a constant function argument written as {@code - 1}.
     */
    static NumExp<?> negate(Exp<?> a, Token op) {
        NumExp<?> num = NUM.operand(a, op);
        return num instanceof ScalarExp<?> scalar && scalar.reduce((Series<?>) null) instanceof Number n
                ? (NumExp<?>) val(negate(n))
                : num.negate();
    }

    private static Number negate(Number n) {
        return switch (n) {
            // "-Integer.MIN_VALUE" and "-Long.MIN_VALUE" do not fit their types, so widen those
            case Integer i -> i != Integer.MIN_VALUE ? (Number) (-i) : (Number) (-(long) i);
            case Long l -> l != Long.MIN_VALUE ? (Number) (-l) : (Number) BigInteger.valueOf(l).negate();
            case Float f -> -f;
            case Double d -> -d;
            case BigInteger bi -> bi.negate();
            case BigDecimal bd -> bd.negate();
            // a numeric scalar built by "val(..)" is always one of the above
            default -> throw new IllegalStateException("Unexpected numeric literal: " + n);
        };
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static NumExp<?> arithmetic(Exp<?> a, Token op, Exp<?> b) {
        NumExp na = NUM.operand(a, op);
        NumExp nb = NUM.operand(b, op);
        return switch (op.getType()) {
            case ExpParser.ADD -> na.add(nb);
            case ExpParser.SUB -> na.sub(nb);
            case ExpParser.MUL -> na.mul(nb);
            case ExpParser.DIV -> na.div(nb);
            case ExpParser.MOD -> na.mod(nb);
            default -> throw unknownOperator(op);
        };
    }

    static Condition not(Exp<?> a, Token op) {
        return Exp.not(boolOperand(a, op));
    }

    static Condition logical(Exp<?> a, Token op, Exp<?> b) {
        Condition ca = boolOperand(a, op);
        Condition cb = boolOperand(b, op);
        return switch (op.getType()) {
            case ExpParser.AND -> Exp.and(ca, cb);
            case ExpParser.OR -> Exp.or(ca, cb);
            default -> throw unknownOperator(op);
        };
    }

    /**
     * Builds a comparison, dispatching on the type of the left-hand side: numeric and temporal expressions support all
     * the operators, everything else only equality. A temporal expression can be compared with an ISO-8601 string
     * literal, and an untyped expression with anything.
     */
    static Condition rel(Exp<?> a, Token op, Exp<?> b) {
        RelOpSupport<?> relOpSupport = relByType(a);
        if (relOpSupport != null) {
            return relOpSupport.rel(a, op, b);
        }

        return switch (a) {
            case StrExp s -> eqOnly(s, op, b, x -> strOperand(x, op));
            case Condition cond -> eqOnly(cond, op, b, x -> boolOperand(x, op));
            case null, default -> eqOnly(a, op, b, x -> x);
        };
    }

    /**
     * Builds a range check for a numeric or temporal expression. ISO-8601 string literals used as the bounds of a
     * temporal expression are converted to temporal scalars by the operand check.
     */
    static Condition between(Exp<?> a, Token op, Exp<?> b, Exp<?> c, boolean negate) {
        RelOpSupport<?> relOpSupport = relByType(a);
        if (relOpSupport == null) {
            throw unsupportedOperator(op, a);
        }

        return relOpSupport.between(a, op, b, c, negate);
    }

    /**
     * Builds an "in" condition, checking the list elements against the type of the left-hand side. Temporal
     * expressions take values of their type or ISO-8601 strings, an untyped expression takes anything, a boolean
     * expression is not supported.
     */
    static Condition in(Exp<?> a, Token op, Object[] values, boolean negate) {
        RelOpSupport<?> relOpSupport = relByType(a);
        Object[] converted = relOpSupport != null ? relOpSupport.elements(values, op, a) : switch (a) {
            case StrExp ignored -> strElements(values, op, a);
            case Condition ignored -> throw unsupportedOperator(op, a);
            case null, default -> values;
        };

        return negate ? a.notIn(converted) : a.in(converted);
    }

    private static RelOpSupport<?> relByType(Exp<?> exp) {
        return switch (exp) {
            case NumExp<?> ignored -> NUM;
            case TimeExp ignored -> TIME;
            case DateExp ignored -> DATE;
            case DateTimeExp ignored -> DATE_TIME;
            case OffsetDateTimeExp ignored -> OFFSET_DATE_TIME;
            case null, default -> null;
        };
    }

    // the operator is checked before the operand, so that "'a' > 1" is reported as an unsupported operator
    private static Condition eqOnly(Exp<?> a, Token op, Exp<?> b, Function<Exp<?>, Exp<?>> operand) {
        return switch (op.getType()) {
            case ExpParser.EQ -> a.eq(operand.apply(b));
            case ExpParser.NE -> a.ne(operand.apply(b));
            default -> throw unsupportedOperator(op, a);
        };
    }

    private static Object[] strElements(Object[] values, Token op, Exp<?> a) {
        for (Object v : values) {
            if (!(v instanceof String)) {
                throw elementMismatch(op, a, v);
            }
        }
        return values;
    }

    private static String temporalStr(Exp<?> exp) {
        return exp instanceof StrScalarExp scalar ? scalar.reduce((Series<?>) null) : null;
    }

    private static StrExp strOperand(Exp<?> exp, Token op) {
        if (exp instanceof StrExp s) {
            return s;
        }
        throw operandMismatch(op, exp, "string");
    }

    private static Condition boolOperand(Exp<?> exp, Token op) {
        if (exp instanceof Condition c) {
            return c;
        }
        throw operandMismatch(op, exp, "boolean");
    }

    // Errors

    private static QLParserException operandMismatch(Token op, Exp<?> exp, String required) {
        return new QLParserException("Operator '" + op.getText() + "' at " + position(op) + " expects a " + required
                + " operand, got a " + expTypeLabel(exp) + " one: " + (exp != null ? exp.toQL() : "null"));
    }

    private static QLParserException elementMismatch(Token op, Exp<?> exp, Object element) {
        String label = expTypeLabel(exp);
        RelOpSupport<?> relOpSupport = relByType(exp);
        String expected = relOpSupport != null && relOpSupport.parse() != null
                ? label + " values or ISO-8601 strings"
                : label + " values";

        return new QLParserException("Operator '" + op.getText() + "' at " + position(op) + " expects a list of "
                + expected + " for a " + label + " expression, got: " + element);
    }

    private static QLParserException unsupportedOperator(Token op, Exp<?> exp) {
        return new QLParserException("Operator '" + op.getText() + "' at " + position(op) + " is not supported for a "
                + expTypeLabel(exp) + " expression: " + (exp != null ? exp.toQL() : "null"));
    }

    // the grammar only passes the tokens the operator rules match, so this is a programming error, not a user one
    private static IllegalStateException unknownOperator(Token op) {
        return new IllegalStateException("Unknown operator: " + op.getText());
    }

    private static String position(Token token) {
        return token.getLine() + ":" + token.getCharPositionInLine();
    }

    private static String expTypeLabel(Exp<?> exp) {
        RelOpSupport<?> relOpSupport = relByType(exp);
        if (relOpSupport != null) {
            return relOpSupport.label();
        }

        return switch (exp) {
            case StrExp ignored -> "string";
            case Condition ignored -> "boolean";
            case null -> "null";
            default -> "generic";
        };
    }

    @FunctionalInterface
    private interface Range<E> {
        Condition apply(E exp, E from, E to);
    }

    /**
     * Operations of an expression type supporting all the comparison operators. "valueType" is the Java type of the
     * values the expression evaluates to, "parse" converts an ISO-8601 string to such a value for the temporal types.
     */
    private record RelOpSupport<E extends Exp<?>>(
            Class<?> type,
            Class<?> valueType,
            String label,
            Function<String, ?> parse,
            BiFunction<E, E, Condition> gt,
            BiFunction<E, E, Condition> ge,
            BiFunction<E, E, Condition> lt,
            BiFunction<E, E, Condition> le,
            Range<E> between,
            Range<E> notBetween) {

        @SuppressWarnings("unchecked")
        E operand(Exp<?> exp, Token op) {
            if (type.isInstance(exp)) {
                return (E) exp;
            }

            String str = parse != null ? temporalStr(exp) : null;
            if (str != null) {
                return (E) val(parse.apply(str));
            }

            throw operandMismatch(op, exp, label);
        }

        Condition rel(Exp<?> a, Token op, Exp<?> b) {
            E ea = operand(a, op);
            E eb = operand(b, op);
            return switch (op.getType()) {
                case ExpParser.GT -> gt.apply(ea, eb);
                case ExpParser.GE -> ge.apply(ea, eb);
                case ExpParser.LT -> lt.apply(ea, eb);
                case ExpParser.LE -> le.apply(ea, eb);
                case ExpParser.EQ -> ea.eq(eb);
                case ExpParser.NE -> ea.ne(eb);
                default -> throw unknownOperator(op);
            };
        }

        Condition between(Exp<?> a, Token op, Exp<?> b, Exp<?> c, boolean negate) {
            return (negate ? notBetween : between).apply(operand(a, op), operand(b, op), operand(c, op));
        }

        /**
         * Checks the elements of an "in" list: a value of the expression type is taken as is, a string is parsed
         * when the type supports ISO-8601 literals, anything else is an error.
         */
        Object[] elements(Object[] values, Token op, Exp<?> a) {
            Object[] converted = new Object[values.length];
            for (int i = 0; i < values.length; i++) {
                Object v = values[i];
                if (valueType.isInstance(v)) {
                    converted[i] = v;
                } else if (parse != null && v instanceof String s) {
                    converted[i] = parse.apply(s);
                } else {
                    throw elementMismatch(op, a, v);
                }
            }
            return converted;
        }
    }
}
