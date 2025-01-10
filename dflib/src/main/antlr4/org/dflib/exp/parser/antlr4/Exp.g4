grammar Exp;

@header {
import java.math.*;
import java.time.temporal.*;
import java.util.function.*;

import org.dflib.*;
}

@members {
private static <T> T col(Object columnId, Function<Integer, T> byIndex, Function<String, T> byName) {
    if (columnId instanceof Integer) {
        return byIndex.apply((Integer) columnId);
    } else if (columnId instanceof String) {
        return byName.apply((String) columnId);
    } else {
        throw new IllegalArgumentException("An integer or a string expected");
    }
}

private static String unescapeString(String raw) {
    if (raw == null) {
        return null;
    }

    StringBuilder result = new StringBuilder();
    for (int i = 0; i < raw.length(); i++) {
        char currentChar = raw.charAt(i);
        if (currentChar != '\\' || i + 1 >= raw.length()) {
            result.append(currentChar);
            continue;
        }

        char nextChar = raw.charAt(i + 1);
        if (nextChar == 'u' && i + 5 < raw.length()) {
            String hex = raw.substring(i + 2, i + 6);
            try {
                int unicodeValue = Integer.parseInt(hex, 16);
                result.append((char) unicodeValue);
                i += 5;
                continue;
            } catch (NumberFormatException e) {
                result.append("\\u");
                i++;
                continue;
            }
        }

        if (nextChar == '"' || nextChar == '\'') {
            result.append(nextChar);
        } else {
            result.append(currentChar);
            result.append(nextChar);
        }
        i++;
    }
    return result.toString();
}

private static int radix(String token) {
    int offset = token.startsWith("+") || token.startsWith("-") ? 1 : 0;
    int dotPosition = token.indexOf(".");
    String lowerToken = token.toLowerCase();
    if (lowerToken.startsWith("0x", offset)) {
        return 16;
    }
    if (lowerToken.startsWith("0b", offset)) {
        return 2;
    }
    if (token.indexOf(".") == -1 && lowerToken.startsWith("0", offset) && token.length() > offset + 1) {
        return 8;
    }
    return 10;
}

private static String sanitizeNumScalar(String token, int radix) {
    return sanitizeNumScalar(token, radix, null);
}

private static String sanitizeNumScalar(String token, int radix, String postfix) {
    String scalar = token.toLowerCase();
    scalar = scalar.replaceAll("_+", "");
    scalar = postfix != null ? scalar.replaceAll(postfix.toLowerCase() + "$", "") : scalar;
    switch (radix) {
        case 2:
            return scalar.replaceFirst("0b", "");
        case 8:
            return scalar.replaceFirst("0(?=.)", "");
        case 16:
            return scalar.replaceFirst("0x", "");
        default:
            return scalar;
    }
}

private static Number floatingPointScalar(String token) {
    String scalar = token.toLowerCase();
    scalar = scalar.replaceAll("_+", "");
    if (scalar.endsWith("f")) {
        return Float.valueOf(scalar);
    } else {
        return Double.valueOf(scalar);
    }
}
}

/// **Parser rules**

/**
 * The root rule of the grammar. Parses a complete expression,
 * expecting the end of the file afterward.
 *
 * Returns: *Exp<?>* - The parsed expression representing the entire input.
 */
root returns [Exp<?> exp]
    : expression EOF { $exp = $expression.exp; }
    ;

/**
 * Parses an expression, which can be of various types including null, aggregate, boolean, numeric, string, temporal, or special functions.
 * An expression represents a single value or a combination of values, operators, and functions.
 *
 * Returns: *Exp<?>* - The parsed expression, potentially of any supported type.
 */
expression returns [Exp<?> exp]
    : NULL { $exp = Exp.\$val(null); }
    | '(' expression ')' { $exp = $expression.exp; }
    | agg { $exp = $agg.exp; }
    | boolExp { $exp = $boolExp.exp; }
    | numExp { $exp = $numExp.exp; }
    | strExp { $exp = $strExp.exp; }
    | temporalExp { $exp = $temporalExp.exp; }
    | specialFn { $exp = $specialFn.exp; }
    ;

/// **Numeric expressions**

/**
 * Parses numeric expressions, encompassing scalar values, column references, functions,
 * aggregates, and arithmetic operations.
 *
 * Returns: *NumExp<?>* - The parsed numeric expression, representing a numeric computation.
 */
numExp returns [NumExp<?> exp]
    : '(' numExp ')' { $exp = $numExp.exp; }
    | numScalar { $exp = (NumExp<?>) Exp.\$val($numScalar.value); }
    | numColumn { $exp = $numColumn.exp; }
    | numFn { $exp = $numFn.exp; }
    | numAgg { $exp = $numAgg.exp; }
    | a=numExp (
        : MUL b=numExp { $exp = $a.exp.mul($b.exp); }
        | DIV b=numExp { $exp = $a.exp.div($b.exp); }
        | MOD b=numExp { $exp = $a.exp.mod($b.exp); }
        | ADD b=numExp { $exp = $a.exp.add($b.exp); }
        | SUB b=numExp { $exp = $a.exp.sub($b.exp); }
    )
    ;

/// **Boolean expressions**

/**
 * Parses boolean expressions, which evaluate to true or false. These can include
 * boolean scalar values, column references, boolean functions, comparisons (relations),
 * and logical operations (AND, OR, NOT).
 *
 * Returns: *Condition* - The parsed boolean expression, representing a logical condition.
 */
boolExp returns [Condition exp]
    : '(' boolExp ')' { $exp = $boolExp.exp; }
    | boolScalar { $exp = Exp.\$boolVal($boolScalar.value); }
    | boolColumn { $exp = $boolColumn.exp; }
    | boolFn { $exp = $boolFn.exp; }
    | relation { $exp = $relation.exp; }
    | NOT boolExp { $exp = Exp.not($boolExp.exp); }
    | a=boolExp (
        : AND b=boolExp { $exp = Exp.and($a.exp, $b.exp); }
        | OR b=boolExp { $exp = Exp.or($a.exp, $b.exp); }
    )
    ;

/// **String expressions**

/**
 * Parses string expressions, including string literals, column references, and
 * string manipulation functions.
 *
 * Returns: *StrExp* - The parsed string expression.
 */
strExp returns [StrExp exp]
    : '(' strExp ')' { $exp = $strExp.exp; }
    | strScalar { $exp = Exp.\$strVal($strScalar.value); }
    | strColumn { $exp = $strColumn.exp; }
    | strFn { $exp = $strFn.exp; }
    ;

/// **Temporal expressions**

/**
 * Parses temporal expressions, encompassing time, date, and datetime values.
 *
 * Returns: *Exp<? extends Temporal>* - The parsed temporal expression, representing a time-related value.
 */
temporalExp returns [Exp<? extends Temporal> exp]
    : '(' temporalExp ')' { $exp = $temporalExp.exp; }
    | timeExp { $exp = $timeExp.exp; }
    | dateExp { $exp = $dateExp.exp; }
    | dateTimeExp { $exp = $dateTimeExp.exp; }
    | offsetDateTimeExp { $exp = $offsetDateTimeExp.exp; }
    ;

/**
 * Parses expressions representing a time of day. These may include column references
 * and time functions.
 *
 * Returns: *TimeExp* - The parsed time expression.
 */
timeExp returns [TimeExp exp]
    : timeColumn { $exp = $timeColumn.exp; }
    | timeFn { $exp = $timeFn.exp; }
    ;

/**
 * Parses date expressions, which can include references to date columns and date functions.
 *
 * Returns: *DateExp* - The parsed date expression.
 */
dateExp returns [DateExp exp]
    : dateColumn { $exp = $dateColumn.exp; }
    | dateFn { $exp = $dateFn.exp; }
    ;

/**
 * Parses datetime expressions, which can refer to datetime columns and utilize datetime functions.
 *
 * Returns: *DateTimeExp* - The parsed datetime expression.
 */
dateTimeExp returns [DateTimeExp exp]
    : dateTimeColumn { $exp = $dateTimeColumn.exp; }
    | dateTimeFn { $exp = $dateTimeFn.exp; }
    ;

/**
 * Parses datetime expressions with an offset from UTC/Greenwich.
 *
 * This is essential for handling timezones correctly.
 *
 * Returns: *OffsetDateTimeExp* - The parsed OffsetDateTime expression.
 */
offsetDateTimeExp returns [OffsetDateTimeExp exp]
    : offsetDateTimeColumn { $exp = $offsetDateTimeColumn.exp; }
    | offsetDateTimeFn { $exp = $offsetDateTimeFn.exp; }
    ;

/// **Scalar expressions**

/**
 * Parses numeric scalar values (literals), which can be integers, longs,
 * or floating-point numbers.
 *
 * Returns: *Number* - The parsed numeric scalar value.
 */
numScalar returns [Number value]
    : integerScalar { $value = $integerScalar.value; }
    | longScalar { $value = $longScalar.value; }
    | floatingPointScalar { $value = $floatingPointScalar.value; }
    ;

/**
 * Parses a long integer scalar value.
 *
 * Returns: *Long* - The parsed long scalar value.
 */
longScalar returns [Long value] locals [int radix]
    : LONG_LITERAL {
        $radix = radix($text);
        $value = Long.valueOf(sanitizeNumScalar($text, $radix, "l"), $radix);
        }
    ;

/**
 * Parses an integer scalar value.
 *
 * Returns: *Integer* - The parsed integer scalar value.
 */
integerScalar returns [Integer value] locals [int radix]
    : INTEGER_LITERAL {
        $radix = radix($text);
        $value = Integer.valueOf(sanitizeNumScalar($text, $radix), $radix);
        }
    ;

/**
 * Parses a floating-point scalar value (float or double).
 *
 * Returns: *Number* - The parsed floating-point scalar value.
 */
floatingPointScalar returns [Number value]
    : FLOATING_POINT_LITERAL { $value = floatingPointScalar($text); }
    ;

/**
 * Parses a boolean scalar value (true or false).
 *
 * Returns: *Boolean* - The parsed boolean scalar value.
 */
boolScalar returns [Boolean value]
    : TRUE { $value = true; }
    | FALSE { $value = false; }
    ;

/**
 * Parses a string literal, enclosed in single or double quotes.
 *
 * Returns: *String* - The parsed string literal.
 */
strScalar returns [String value]
    : SINGLE_QUOTE_STRING_LITERAL { $value = unescapeString($text.substring(1, $text.length() - 1)); }
    | DOUBLE_QUOTE_STRING_LITERAL { $value = unescapeString($text.substring(1, $text.length() - 1)); }
    ;

/// **Column expressions**

/**
 * Parses an expression referencing a numeric column.  Supports various numeric types.
 *
 * Returns: *NumExp<?>* - The parsed numeric column expression, representing the column's numeric data.
 */
numColumn returns [NumExp<?> exp]
    : intColumn { $exp = $intColumn.exp; }
    | longColumn { $exp = $longColumn.exp; }
    | floatColumn { $exp = $floatColumn.exp; }
    | doubleColumn { $exp = $doubleColumn.exp; }
    | decimalColumn { $exp = $decimalColumn.exp; }
    ;

/**
 * Parses an expression referencing a column of integer values.
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name).
 *
 * Returns: *NumExp<Integer>* - The parsed column expression, producing Integer values.
 */
intColumn returns [NumExp<Integer> exp]
    : INT '(' columnId ')' { $exp = col($columnId.id, Exp::\$int, Exp::\$int); }
    ;

/**
 * Parses an expression referencing a column containing long integer values.
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name).
 *
 * Returns: *NumExp<Long>* - The parsed column expression, producing Long values.
 */
longColumn returns [NumExp<Long> exp]
    : LONG '(' columnId ')' { $exp = col($columnId.id, Exp::\$long, Exp::\$long); }
    ;

/**
 * Parses an expression that references a column of float values.
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name).
 *
 * Returns: *NumExp<Float>* - The parsed column expression, producing Float values.
 */
floatColumn returns [NumExp<Float> exp]
    : FLOAT '(' columnId ')' { $exp = col($columnId.id, Exp::\$float, Exp::\$float); }
    ;

/**
 * Parses an expression referencing a column containing double-precision floating-point numbers.
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name).
 *
 * Returns: *NumExp<Double>* - The parsed column expression, producing Double values.
 */
doubleColumn returns [NumExp<Double> exp]
    : DOUBLE '(' columnId ')' { $exp = col($columnId.id, Exp::\$double, Exp::\$double); }
    ;

/**
 * Parses an expression referencing a column of Decimal values (for high-precision arithmetic).
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name)n.
 *
 * Returns: *DecimalExp* - The parsed column expression, producing Decimal values.
 */
decimalColumn returns [DecimalExp exp]
    : DECIMAL '(' columnId ')' { $exp = col($columnId.id, Exp::\$decimal, Exp::\$decimal); }
    ;

/**
 * Parses an expression that accesses a column of boolean values.
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name).
 *
 * Returns: *Condition* - The parsed column expression, producing Boolean values.
 */
boolColumn returns [Condition exp]
    : BOOL '(' columnId ')' { $exp = col($columnId.id, Exp::\$bool, Exp::\$bool); }
    ;

/**
 * Parses an expression referring to a column containing string values.
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name).
 *
 * Returns: *StrExp* - The parsed column expression, producing String values
 */
strColumn returns [StrExp exp]
    : STR '(' columnId ')' { $exp = col($columnId.id, Exp::\$str, Exp::\$str); }
    ;

/**
 * Parses an expression referencing a column containing Date values.
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name).
 *
 * Returns: *DateExp* - The parsed column expression, producing Date values.
 */
dateColumn returns [DateExp exp]
    : DATE '(' columnId ')' { $exp = col($columnId.id, Exp::\$date, Exp::\$date); }
    ;

/**
 * Parses an expression that refers to a column containing Time values.
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name).
 *
 * Returns: *TimeExp* - The parsed column expression, producing Time values.
 */
timeColumn returns [TimeExp exp]
    : TIME '(' columnId ')' { $exp = col($columnId.id, Exp::\$time, Exp::\$time); }
    ;

/**
 * Parses an expression referencing a column storing DateTime values.
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name).
 *
 * Returns: *DateTimeExp* - The parsed column expression, producing DateTime values.
 */
dateTimeColumn returns [DateTimeExp exp]
    : DATETIME '(' columnId ')' { $exp = col($columnId.id, Exp::\$dateTime, Exp::\$dateTime); }
    ;

/**
 * Parses an expression referencing a column containing OffsetDateTime values (datetime with timezone offset).
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name).
 *
 * Returns: *OffsetDateTimeExp* - The parsed column expression, producing OffsetDateTime values.
 */
offsetDateTimeColumn returns [OffsetDateTimeExp exp]
    : OFFSET_DATETIME '(' columnId ')' { $exp = col($columnId.id, Exp::\$offsetDateTime, Exp::\$offsetDateTime); }
    ;

/**
 * Parses a column identifier, which can be an integer representing the column index or a string representing the column name.
 *
 * Returns: *Object* - The parsed column identifier (either an Integer or a String).
 */
columnId returns [Object id]
    : integerScalar { $id = $integerScalar.value; }
    | strScalar { $id = $strScalar.value; }
    | identifier { $id = $identifier.id; }
    ;

/**
 * Parses an identifier, which is a sequence of letters and digits starting with a letter.
 *
 * Returns: *String* - The parsed identifier.
 */
identifier returns [String id]
    : IDENTIFIER { $id = $text; }
    ;

/// **Relational expressions**

/**
 * Parses relational expressions, which can include numeric, string, time, date, and datetime relations.
 * These expressions compare two values using operators like >, <, =, !=, etc.
 *
 * Returns: *Condition* - The parsed relational expression, representing a logical condition.
 */
relation returns [Condition exp]
    : '(' relation ')' { $exp = $relation.exp; }
    | numRelation { $exp = $numRelation.exp; }
    | strRelation { $exp = $strRelation.exp; }
    | timeRelation { $exp = $timeRelation.exp; }
    | dateRelation { $exp = $dateRelation.exp; }
    | dateTimeRelation { $exp = $dateTimeRelation.exp; }
    ;

/**
 * Parses a numeric relational expression. This compares two numeric expressions
 * using comparison operators (>, >=, <, <=, =, !=, BETWEEN).
 *
 * Parameters:
 *  - The left-hand side numeric expression.
 *  - The right-hand side numeric expression.
 *  - The upper bound numeric expression (for BETWEEN).
 *
 * Returns: *Condition* - The resulting boolean condition from the comparison.
 */
numRelation returns [Condition exp]
    : a=numExp (
        : GT b=numExp { $exp = $a.exp.gt($b.exp); }
        | GE b=numExp { $exp = $a.exp.ge($b.exp); }
        | LT b=numExp { $exp = $a.exp.lt($b.exp); }
        | LE b=numExp { $exp = $a.exp.le($b.exp); }
        | EQ b=numExp { $exp = $a.exp.eq($b.exp); }
        | NE b=numExp { $exp = $a.exp.ne($b.exp); }
        | BETWEEN b=numExp AND c=numExp { $exp = $a.exp.between($b.exp, $c.exp); }
    )
    ;

/**
 * Parses string relational expressions. Compares two string expressions using either
 * equality (=) or inequality (!=).
 *
 * Parameters:
 *  - The left-hand side string expression.
 *  - The right-hand side string expression.
 *
 * Returns: *Condition* - The boolean condition representing the result of the string relation.
 */
strRelation returns [Condition exp]
    : a=strExp (
        : EQ b=strExp { $exp = $a.exp.eq($b.exp); }
        | NE b=strExp { $exp = $a.exp.ne($b.exp); }
    )
    ;

/**
 * Parses time relational expressions. Compares two time expressions using comparison operators.
 *
 * Parameters:
 *  - The left-hand TimeExp.
 *  - The right-hand TimeExp.
 *  - The upper bound TimeExp (for BETWEEN).
 *
 * Returns: *Condition* - The boolean condition representing the result of the time relation.
 */
timeRelation returns [Condition exp]
    : a=timeExp (
        : GT b=timeExp { $exp = $a.exp.gt($b.exp); }
        | GE b=timeExp { $exp = $a.exp.ge($b.exp); }
        | LT b=timeExp { $exp = $a.exp.lt($b.exp); }
        | LE b=timeExp { $exp = $a.exp.le($b.exp); }
        | EQ b=timeExp { $exp = $a.exp.eq($b.exp); }
        | NE b=timeExp { $exp = $a.exp.ne($b.exp); }
        | BETWEEN b=timeExp AND c=timeExp { $exp = $a.exp.between($b.exp, $c.exp); }
    )
    ;

/**
 * Parses date relational expressions, comparing two DateExps.
 *
 * Parameters:
 *  - The left-hand DateExp.
 *  - The right-hand DateExp.
 *  - The upper bound DateExp (for BETWEEN).
 *
 * Returns: *Condition* - The boolean condition representing the result of the date relation.
 */
dateRelation returns [Condition exp]
    : a=dateExp (
        : GT b=dateExp { $exp = $a.exp.gt($b.exp); }
        | GE b=dateExp { $exp = $a.exp.ge($b.exp); }
        | LT b=dateExp { $exp = $a.exp.lt($b.exp); }
        | LE b=dateExp { $exp = $a.exp.le($b.exp); }
        | EQ b=dateExp { $exp = $a.exp.eq($b.exp); }
        | NE b=dateExp { $exp = $a.exp.ne($b.exp); }
        | BETWEEN b=dateExp AND c=dateExp { $exp = $a.exp.between($b.exp, $c.exp); }
    )
    ;

/**
 * Parses a datetime relational expression. Compares two DateTimeExp values.
 *
 * Parameters:
 *  - The left-hand DateTimeExp.
 *  - The right-hand DateTimeExp.
 *  - The upper bound DateTimeExp (for BETWEEN).
 *
 * Returns: *Condition* - The boolean condition representing the result of the datetime relation.
 */
dateTimeRelation returns [Condition exp]
    : a=dateTimeExp (
        : GT b=dateTimeExp { $exp = $a.exp.gt($b.exp); }
        | GE b=dateTimeExp { $exp = $a.exp.ge($b.exp); }
        | LT b=dateTimeExp { $exp = $a.exp.lt($b.exp); }
        | LE b=dateTimeExp { $exp = $a.exp.le($b.exp); }
        | EQ b=dateTimeExp { $exp = $a.exp.eq($b.exp); }
        | NE b=dateTimeExp { $exp = $a.exp.ne($b.exp); }
        | BETWEEN b=dateTimeExp AND c=dateTimeExp { $exp = $a.exp.between($b.exp, $c.exp); }
    )
    ;

/**
 * Parses an OffsetDateTime relational expression. Compares two OffsetDateTimeExp values.
 *
 * Parameters:
 *  - The left-hand OffsetDateTimeExp.
 *  - The right-hand OffsetDateTimeExp.
 *  - The upper bound OffsetDateTimeExp (for BETWEEN).
 *
 * Returns: *Condition* - The boolean condition representing the result of the OffsetDateTime relation.
 */
offsetDateTimeRelation returns [Condition exp]
    : a=offsetDateTimeExp (
        : GT b=offsetDateTimeExp { $exp = $a.exp.gt($b.exp); }
        | GE b=offsetDateTimeExp { $exp = $a.exp.ge($b.exp); }
        | LT b=offsetDateTimeExp { $exp = $a.exp.lt($b.exp); }
        | LE b=offsetDateTimeExp { $exp = $a.exp.le($b.exp); }
        | EQ b=offsetDateTimeExp { $exp = $a.exp.eq($b.exp); }
        | NE b=offsetDateTimeExp { $exp = $a.exp.ne($b.exp); }
        | BETWEEN b=offsetDateTimeExp AND c=offsetDateTimeExp { $exp = $a.exp.between($b.exp, $c.exp); }
    )
    ;

/// **Functions**

/**
 * Parses numeric functions, including casting, counting, row number, absolute value, rounding, length, and field functions.
 * These functions operate on or produce numeric values.
 *
 * Returns: *NumExp<?>* - The parsed numeric function expression.
 */
numFn returns [NumExp<?> exp]
    : castAsInt { $exp = $castAsInt.exp; }
    | castAsLong { $exp = $castAsLong.exp; }
    | castAsFloat { $exp = $castAsFloat.exp; }
    | castAsDouble { $exp = $castAsDouble.exp; }
    | castAsDecimal { $exp = $castAsDecimal.exp; }
    | COUNT ('()' | '(' b=boolExp? ')') { $exp = $ctx.b != null ? Exp.count($b.exp) : Exp.count(); }
    | ROW_NUM ('()' | '(' ')') { $exp = Exp.rowNum(); }
    | ABS '(' numExp ')' { $exp = $numExp.exp.abs(); }
    | ROUND '(' numExp ')' { $exp = $numExp.exp.round(); }
    | LEN '(' strExp ')' { $exp = $strExp.exp.mapVal(String::length).castAsInt(); }
    | timeFieldFn { $exp = $timeFieldFn.exp; }
    | dateFieldFn { $exp = $dateFieldFn.exp; }
    | dateTimeFieldFn { $exp = $dateTimeFieldFn.exp; }
    | offsetDateTimeFieldFn { $exp = $offsetDateTimeFieldFn.exp; }
    ;

/**
 * Parses time field functions.
 *
 * Supports extracting fields like hour, minute, second and millisecond from Time expressions.
 *
 * Parameters:
 *  - The TimeExp from which to extract the field.
 *
 * Returns: *NumExp<Integer>* - The parsed time field function, producing an Integer result.
 */
timeFieldFn returns [NumExp<Integer> exp]
    : HOUR '(' timeExp ')' { $exp = $timeExp.exp.hour(); }
    | MINUTE '(' timeExp ')' { $exp = $timeExp.exp.minute(); }
    | SECOND '(' timeExp ')' { $exp = $timeExp.exp.second(); }
    | MILLISECOND '(' timeExp ')' { $exp = $timeExp.exp.millisecond(); }
    ;

/**
 * Parses date field functions.
 *
 * Supports extracting fields like year, month, and day from Date expressions.
 *
 * Parameters:
 *  - The DateExp from which to extract the field.
 *
 * Returns: *NumExp<Integer>* - The parsed date field function, producing an Integer result.
 */
dateFieldFn returns [NumExp<Integer> exp]
    : YEAR '(' dateExp ')' { $exp = $dateExp.exp.year(); }
    | MONTH '(' dateExp ')' { $exp = $dateExp.exp.month(); }
    | DAY '(' dateExp ')' { $exp = $dateExp.exp.day(); }
    ;

/**
 * Parses datetime field functions.
 *
 * Supports extracting fields like year, month, day, hour, minute, second,
 * and millisecond from DateTime expressions.
 *
 * Parameters:
 *  - The DateTimeExp from which to extract the field.
 *
 * Returns: *NumExp<Integer>* - The parsed datetime field function, producing an Integer result.
 */
dateTimeFieldFn returns [NumExp<Integer> exp]
    : YEAR '(' dateTimeExp ')' { $exp = $dateTimeExp.exp.year(); }
    | MONTH '(' dateTimeExp ')' { $exp = $dateTimeExp.exp.month(); }
    | DAY '(' dateTimeExp ')' { $exp = $dateTimeExp.exp.day(); }
    | HOUR '(' dateTimeExp ')' { $exp = $dateTimeExp.exp.hour(); }
    | MINUTE '(' dateTimeExp ')' { $exp = $dateTimeExp.exp.minute(); }
    | SECOND '(' dateTimeExp ')' { $exp = $dateTimeExp.exp.second(); }
    | MILLISECOND '(' dateTimeExp ')' { $exp = $dateTimeExp.exp.millisecond(); }
    ;

/**
 * Parses OffsetDateTime field functions.
 *
 * Supports extracting fields like year, month, day, hour, minute, second,
 * and millisecond from OffsetDateTime expressions.
 *
 * Parameters:
 *  - The OffsetDateTimeExp from which to extract the field.
 *
 * Returns: *NumExp<Integer>* - The parsed OffsetDateTime field function, producing an Integer result.
 */
offsetDateTimeFieldFn returns [NumExp<Integer> exp]
    : YEAR '(' offsetDateTimeExp ')' { $exp = $offsetDateTimeExp.exp.year(); }
    | MONTH '(' offsetDateTimeExp ')' { $exp = $offsetDateTimeExp.exp.month(); }
    | DAY '(' offsetDateTimeExp ')' { $exp = $offsetDateTimeExp.exp.day(); }
    | HOUR '(' offsetDateTimeExp ')' { $exp = $offsetDateTimeExp.exp.hour(); }
    | MINUTE '(' offsetDateTimeExp ')' { $exp = $offsetDateTimeExp.exp.minute(); }
    | SECOND '(' offsetDateTimeExp ')' { $exp = $offsetDateTimeExp.exp.second(); }
    | MILLISECOND '(' offsetDateTimeExp ')' { $exp = $offsetDateTimeExp.exp.millisecond(); }
    ;

/**
 * Parses boolean functions, which can include casting, matches, starts with, ends with, and contains.
 * These functions produce boolean results.
 *
 * Parameters:
 *  - The input expression(s) for the boolean function. The types and number of parameters depend on the specific function.
 *    For example, `matches` takes a string expression and a string literal, while `castAsBool` takes a single expression of any type.
 *
 * Returns: *Condition* - The parsed boolean function, representing a condition that evaluates to true or false.
 */
boolFn returns [Condition exp]
    : castAsBool { $exp = $castAsBool.exp; }
    | MATCHES '(' strExp ',' strScalar ')' { $exp = $strExp.exp.matches($strScalar.value); }
    | STARTS_WITH '(' strExp ',' strScalar ')' { $exp = $strExp.exp.startsWith($strScalar.value); }
    | ENDS_WITH '(' strExp ',' strScalar ')' { $exp = $strExp.exp.endsWith($strScalar.value); }
    | CONTAINS '(' strExp ',' strScalar ')' { $exp = $strExp.exp.contains($strScalar.value); }
    ;


/**
 * Parses time functions, including casting and arithmetic operations. These
 * functions operate on time values.
 *
 * Parameters:
 *  - The base TimeExp.
 *  - An integer representing the value to add (e.g., hours, minutes).
 *
 * Returns: *TimeExp* - The parsed time function.
 */
timeFn returns [TimeExp exp]
    : castAsTime { $exp = $castAsTime.exp; }
    | PLUS_HOURS '(' a=timeExp ',' b=integerScalar ')' { $exp = $a.exp.plusHours($b.value); }
    | PLUS_MINUTES '(' a=timeExp ',' b=integerScalar ')' { $exp = $a.exp.plusMinutes($b.value); }
    | PLUS_SECONDS '(' a=timeExp ',' b=integerScalar ')' { $exp = $a.exp.plusSeconds($b.value); }
    | PLUS_MILLISECONDS '(' a=timeExp ',' b=integerScalar ')' { $exp = $a.exp.plusMilliseconds($b.value); }
    | PLUS_NANOS '(' a=timeExp ',' b=integerScalar ')' { $exp = $a.exp.plusNanos($b.value); }
    ;

/**
 * Parses date functions, enabling casting and arithmetic operations on date values.
 *
 * Parameters:
 *  - The base DateExp.
 *  - An integer representing the value to add (e.g., years, months).
 *
 * Returns: *DateExp* - The parsed date function.
 */
dateFn returns [DateExp exp]
    : castAsDate { $exp = $castAsDate.exp; }
    | PLUS_YEARS '(' a=dateExp ',' b=integerScalar ')' { $exp = $a.exp.plusYears($b.value); }
    | PLUS_MONTHS '(' a=dateExp ',' b=integerScalar ')' { $exp = $a.exp.plusMonths($b.value); }
    | PLUS_WEEKS '(' a=dateExp ',' b=integerScalar ')' { $exp = $a.exp.plusWeeks($b.value); }
    | PLUS_DAYS '(' a=dateExp ',' b=integerScalar ')' { $exp = $a.exp.plusDays($b.value); }
    ;

/**
 * Parses datetime functions, including casting and arithmetic operations for datetime values.
 *
 * Parameters:
 *  - The base DateTimeExp.
 *  - An integer representing the value to add (e.g., years, hours).
 *
 * Returns: *DateTimeExp* - The parsed datetime function.
 */
dateTimeFn returns [DateTimeExp exp]
    : castAsDateTime { $exp = $castAsDateTime.exp; }
    | PLUS_YEARS '(' a=dateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusYears($b.value); }
    | PLUS_MONTHS '(' a=dateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusMonths($b.value); }
    | PLUS_WEEKS '(' a=dateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusWeeks($b.value); }
    | PLUS_DAYS '(' a=dateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusDays($b.value); }
    | PLUS_HOURS '(' a=dateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusHours($b.value); }
    | PLUS_MINUTES '(' a=dateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusMinutes($b.value); }
    | PLUS_SECONDS '(' a=dateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusSeconds($b.value); }
    | PLUS_MILLISECONDS '(' a=dateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusMilliseconds($b.value); }
    | PLUS_NANOS '(' a=dateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusNanos($b.value); }
    ;

/**
 * Parses datetime functions with an offset from UTC/Greenwich.  Supports casting and
 * arithmetic operations on timezone-aware datetime values.
 *
 * Parameters:
 *  - The base OffsetDateTimeExp.
 *  - An integer representing the value to add (e.g., years, hours).
 *
 * Returns: *OffsetDateTimeExp* - The parsed OffsetDateTime function.
 */
offsetDateTimeFn returns [OffsetDateTimeExp exp]
    : castAsOffsetDateTime { $exp = $castAsOffsetDateTime.exp; }
    | PLUS_YEARS '(' a=offsetDateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusYears($b.value); }
    | PLUS_MONTHS '(' a=offsetDateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusMonths($b.value); }
    | PLUS_WEEKS '(' a=offsetDateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusWeeks($b.value); }
    | PLUS_DAYS '(' a=offsetDateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusDays($b.value); }
    | PLUS_HOURS '(' a=offsetDateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusHours($b.value); }
    | PLUS_MINUTES '(' a=offsetDateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusMinutes($b.value); }
    | PLUS_SECONDS '(' a=offsetDateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusSeconds($b.value); }
    | PLUS_MILLISECONDS '(' a=offsetDateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusMilliseconds($b.value); }
    | PLUS_NANOS '(' a=offsetDateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusNanos($b.value); }
    ;

/**
 * Parses string functions, covering casting, trimming, substrings, and concatenation.
 *
 * Parameters:
 *  - The input StrExp (for functions like SUBSTR, TRIM).
 *  - The starting position (an integer, for SUBSTR).
 *  - The length (an integer, optional for SUBSTR).
 *  - A variable number of StrExp arguments (for CONCAT).
 *
 * Returns: *StrExp* - The parsed string function.
 */
strFn returns [StrExp exp]
    : castAsStr { $exp = $castAsStr.exp; }
    | TRIM '(' strExp ')' { $exp = $strExp.exp.trim(); }
    | SUBSTR '(' s=strExp ',' a=integerScalar (',' b=integerScalar)? ')' {
        $exp = $ctx.b != null ? $s.exp.substr($a.value, $b.value) : $s.exp.substr($a.value);
        }
    | CONCAT ('()' | '(' (args+=strExp (',' args+=strExp)*)? ')') {
        $exp = !$args.isEmpty() ? Exp.concat($args.stream().map(a -> a.exp).toArray()) : Exp.concat();
        }
    ;

/// **Cast functions**

/**
 * Parses the cast-to-boolean function, converting an expression to a boolean value.
 *
 * Parameters:
 *  - The expression to be cast.
 *
 * Returns: *Condition* - The boolean condition resulting from the cast.
 */
castAsBool returns [Condition exp]
    : CAST_AS_BOOL '(' expression ')' { $exp = $expression.exp.castAsBool(); }
    ;

/**
 * Parses the cast-to-integer function, converting an expression to an integer.
 *
 * Parameters:
 *  - The expression to be cast.
 *
 * Returns: *NumExp<Integer>* - The integer representation of the expression.
 */
castAsInt returns [NumExp<Integer> exp]
    : CAST_AS_INT '(' expression ')' { $exp = $expression.exp.castAsInt(); }
    ;

/**
 * Casts an expression to a long integer.
 *
 * Parameters:
 *  - The expression to be cast.
 *
 * Returns:  *NumExp<Long>* -  The long integer representation of the expression.
 */
castAsLong returns [NumExp<Long> exp]
    : CAST_AS_LONG '(' expression ')' { $exp = $expression.exp.castAsLong(); }
    ;

/**
 * Casts an expression to a floating-point value (float).
 *
 * Parameters:
 *  - The expression to be cast.
 *
 * Returns: *NumExp<Float>* - The float representation of the expression.
 */
castAsFloat returns [NumExp<Float> exp]
    : CAST_AS_FLOAT '(' expression ')' { $exp = $expression.exp.castAsFloat(); }
    ;

/**
 * Casts an expression to a double-precision floating-point value.
 *
 * Parameters:
 *  - The expression to be cast.
 *
 * Returns: *NumExp<Double>* - The double-precision float representation of the expression.
 */
castAsDouble returns [NumExp<Double> exp]
    : CAST_AS_DOUBLE '(' expression ')' { $exp = $expression.exp.castAsDouble(); }
    ;

/**
 * Casts an expression to a Decimal value. Decimals are used for high-precision arithmetic.
 *
 * Parameters:
 *  - The expression to be cast.
 *
 * Returns: *DecimalExp* - The decimal representation of the expression.
 */
castAsDecimal returns [DecimalExp exp]
    : CAST_AS_DECIMAL '(' expression ')' { $exp = $expression.exp.castAsDecimal(); }
    ;

/**
 * Casts an expression to a string.
 *
 * Parameters:
 *  - The expression to be cast.
 *
 * Returns: *StrExp* - The string representation of the expression.
 */
castAsStr returns [StrExp exp]
    : CAST_AS_STR '(' expression ')' { $exp = $expression.exp.castAsStr(); }
    ;

/**
 * Casts an expression to a Time value. Supports optional formatting.
 *
 * Parameters:
 *  - The expression to be cast.
 *  - A format string specifying how to interpret the time (optional).
 *
 * Returns: *TimeExp* - The time representation of the expression.
 */
castAsTime returns [TimeExp exp]
    : CAST_AS_TIME '(' e=expression (',' f=strScalar )? ')' {
        $exp = $ctx.f != null ? $e.exp.castAsTime($f.value) : $e.exp.castAsTime();
        }
    ;

/**
 * Casts an expression to a Date value.  Handles optional date formatting.
 *
 * Parameters:
 *  - The expression to be cast.
 *  - A format string specifying how to interpret the date (optional).
 *
 * Returns: *DateExp* - The date representation of the expression.
 */
castAsDate returns [DateExp exp]
    : CAST_AS_DATE '(' e=expression (',' f=strScalar )? ')' {
        $exp = $ctx.f != null ? $e.exp.castAsDate($f.value) : $e.exp.castAsDate();
        }
    ;

/**
 * Casts an expression to a DateTime value, with optional formatting.
 *
 * Parameters:
 *  - The expression to be cast.
 *  - A format string specifying how to interpret the datetime (optional).
 *
 * Returns: *DateTimeExp* - The datetime representation of the expression.
 */
castAsDateTime returns [DateTimeExp exp]
    : CAST_AS_DATETIME '(' e=expression (',' f=strScalar )? ')' {
        $exp = $ctx.f != null ? $e.exp.castAsDateTime($f.value) : $e.exp.castAsDateTime();
        }
    ;

/**
 * Parses a cast operation to convert an expression into an OffsetDateTime.
 *
 * Parameters:
 *  - The expression to be cast.
 *  - A format string specifying how to interpret the OffsetDateTime (optional).
 *
 * Returns: *OffsetDateTimeExp* - The OffsetDateTime representation of the expression.
 */
castAsOffsetDateTime returns [OffsetDateTimeExp exp]
    : CAST_AS_OFFSET_DATETIME '(' e=expression (',' f=strScalar )? ')' {
        $exp = $ctx.f != null ? $e.exp.castAsOffsetDateTime($f.value) : $e.exp.castAsOffsetDateTime();
        }
    ;

/// **Special functions**

/**
 * Parses special functions that provide control flow, data manipulation, or other specialized operations.
 *
 * Returns: *Exp<?>* - The parsed special function.  The result type depends on the function.
 */
specialFn returns [Exp<?> exp]
    : ifExp { $exp = $ifExp.exp; }
    | ifNull { $exp = $ifNull.exp; }
    | split { $exp = $split.exp; }
    | shift { $exp = $shift.exp; }
    ;

/**
 * Parses an IF expression, a conditional expression that returns one of two values based on a boolean condition.
 *
 * Parameters:
 *  - The boolean condition.
 *  - The expression to return if the condition is true.
 *  - The expression to return if the condition is false. Must be the same type as the true branch.
 *
 * Returns: *Exp<?>* - The parsed IF expression. The result type matches the type of the branch expressions.
 */
ifExp returns [Exp<?> exp]
    : IF '(' a=boolExp ',' (
          b1=boolExp ',' b2=boolExp { $exp = Exp.ifExp($a.exp, $b1.exp, $b2.exp); }
        | s1=strExp ',' s2=strExp { $exp = Exp.ifExp($a.exp, $s1.exp, $s2.exp); }
        | t1=timeExp ',' t2=timeExp { $exp = Exp.ifExp($a.exp, $t1.exp, $t2.exp); }
        | d1=dateExp ',' d2=dateExp { $exp = Exp.ifExp($a.exp, $d1.exp, $d2.exp); }
        | dt1=dateTimeExp ',' dt2=dateTimeExp { $exp = Exp.ifExp($a.exp, $dt1.exp, $dt2.exp); }
        | n1=numExp ',' n2=numExp { $exp = Exp.ifExp($a.exp, (NumExp<Number>) $n1.exp, (NumExp<Number>) $n2.exp); }
    ) ')'
    ;

/**
 * Parses an IF_NULL expression. This function returns the first expression if it is not null;
 * otherwise, it returns the second expression.
 *
 * Parameters:
 *  - The expression to check for null.
 *  - The expression to return if the first expression is null. Must be the same type as the first expression.
 *
 * Returns:  *Exp<?>* - The parsed IF_NULL expression. The result type is the same as the input expressions.
 */
ifNull returns [Exp<?> exp]
    : IF_NULL '(' (
          b1=boolExp ',' b2=boolExp { $exp = Exp.ifNull($b1.exp, $b2.exp); }
        | s1=strExp ',' s2=strExp { $exp = Exp.ifNull($s1.exp, $s2.exp); }
        | t1=timeExp ',' t2=timeExp { $exp = Exp.ifNull($t1.exp, $t2.exp); }
        | d1=dateExp ',' d2=dateExp { $exp = Exp.ifNull($d1.exp, $d2.exp); }
        | dt1=dateTimeExp ',' dt2=dateTimeExp { $exp = Exp.ifNull($dt1.exp, $dt2.exp); }
        | n1=numExp ',' n2=numExp { $exp = Exp.ifNull((NumExp<Number>) $n1.exp, (NumExp<Number>) $n2.exp); }
    ) ')'
    ;

/**
 * Parses a SPLIT expression, which splits a string into an array of substrings based on a delimiter.
 *
 * Parameters:
 *  - The string expression to split.
 *  - The delimiter string to split the string by.
 *  - An integer that limits the maximum number of substrings (optional).
 *
 * Returns: *Exp<String[]>* - The SPLIT expression, which produces an array of strings.
 */
split returns [Exp<String[]> exp]
    : SPLIT '(' a=strExp ',' b=strScalar (',' c=integerScalar)? ')' {
        $exp = $ctx.c != null ? $a.exp.split($b.value, $c.value) : $a.exp.split($b.value);
        }
    ;

/**
 * Parses a SHIFT expression, shifting values in a sequence forward or backward.
 * Head or tail gaps produced by the shift are filled with the provided filler value or null.
 *
 * Parameters:
 *  - The expression to shift.
 *  - The integer shift amount (positive for forward, negative for backward).
 *  - The default value to use for positions that become empty after shifting (optional).
 *
 * Returns: *Exp<?>* - The parsed SHIFT expression. The result type matches the input expression type.
 */
shift returns [Exp<?> exp]
    : SHIFT '(' (
          be=boolExp ',' i=integerScalar (',' bs=boolScalar)? {
            $exp = $ctx.bs != null ? $be.exp.shift($i.value, $bs.value) : $be.exp.shift($i.value);
            }
        | ne=numExp ',' i=integerScalar (',' ns=numScalar)? {
            $exp = $ctx.ns != null ? ((NumExp<Number>) $ne.exp).shift($i.value, (Number) $ns.value) : $ne.exp.shift($i.value);
            }
        | se=strExp ',' i=integerScalar (',' ss=strScalar)? {
            $exp = $ctx.ss != null ? $se.exp.shift($i.value, $ss.value) : $se.exp.shift($i.value);
            }
    ) ')'
    ;

/// **Aggregate expressions**

/**
 * Parses aggregate expressions, such as MIN, MAX, SUM, AVG, etc.
 * Aggregates perform calculations across multiple rows of data.
 *
 * Returns: *Exp<?>* - The parsed aggregate expression.
 */
agg returns [Exp<?> exp]
    : positionalAgg { $exp = $positionalAgg.exp; }
    | numAgg { $exp = $numAgg.exp; }
    | timeAgg { $exp = $timeAgg.exp; }
    | dateAgg { $exp = $dateAgg.exp; }
    | dateTimeAgg { $exp = $dateTimeAgg.exp; }
    | strAgg { $exp = $strAgg.exp; }
    ;

/**
 * Parses positional aggregate expressions, like FIRST and LAST. These functions
 * return the first or last value encountered in a sequence.
 *
 * Parameters:
 *  - The expression from which to get the first or last value.
 *  - A boolean expression to filter the data before finding the first element (optional).
 *
 * Returns: *Exp<?>* - The parsed positional aggregate expression. The result type matches the input expression type.
 */
positionalAgg returns [Exp<?> exp]
    : FIRST '(' e=expression (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $e.exp.first($b.exp) : $e.exp.first(); }
    | LAST '(' e=expression ')' { $exp = $e.exp.last(); } // TODO: bool condition
    ;

/**
 * Parses numeric aggregate expressions, calculating aggregates like MIN, MAX, SUM, AVG, MEDIAN, and QUANTILE.
 *
 * Parameters:
 *  - The numeric column expression to aggregate.
 *  - A boolean expression to filter the rows involved in the aggregation (optional).
 *  - The quantile value to compute (between 0 and 1, for QUANTILE).
 *
 * Returns: *NumExp<?>* - The parsed numeric aggregate expression. The result type is generally numeric, though COUNT produces a Long.
 */
numAgg returns [NumExp<?> exp]
    : MIN '(' c=numColumn (',' b=boolExp)? ')' { $exp = $c.exp.min($ctx.b != null ? $b.exp : null); }
    | MAX '(' c=numColumn (',' b=boolExp)? ')' { $exp = $c.exp.max($ctx.b != null ? $b.exp : null); }
    | SUM '(' c=numColumn (',' b=boolExp)? ')' { $exp = $c.exp.sum($ctx.b != null ? $b.exp : null); }
    | CUMSUM '(' c=numColumn ')' { $exp = $c.exp.cumSum(); } // Cumulative Sum, no filter currently supported
    | AVG '(' c=numColumn (',' b=boolExp)? ')' { $exp = $c.exp.avg($ctx.b != null ? $b.exp : null); }
    | MEDIAN '(' c=numColumn (',' b=boolExp)? ')' { $exp = $c.exp.median($ctx.b != null ? $b.exp : null); }
    | QUANTILE '(' c=numColumn ',' q=numScalar (',' b=boolExp)? ')' {
        $exp = $ctx.b != null
            ? $c.exp.quantile($q.value.doubleValue(), $b.exp)
            : $c.exp.quantile($q.value.doubleValue());
        }
    ;

/**
 * Parses time aggregate expressions, performing operations like MIN, MAX, AVG, MEDIAN and QUANTILE on time values.
 *
 * Parameters:
 *  - The time column expression to aggregate.
 *  - A boolean expression to filter the rows involved in the aggregation (optional).
 *  - The quantile value to compute (between 0 and 1, for QUANTILE).
 *
 * Returns: *TimeExp* - The resulting time aggregate expression.
 */
timeAgg returns [TimeExp exp]
    : MIN '(' c=timeColumn (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.min($b.exp) : $c.exp.min(); }
    | MAX '(' c=timeColumn (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.max($b.exp) : $c.exp.max(); }
    | AVG '(' c=timeColumn (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.avg($b.exp) : $c.exp.avg(); }
    | MEDIAN '(' c=timeColumn (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.median($b.exp) : $c.exp.median(); }
    | QUANTILE '(' c=timeColumn ',' q=numScalar (',' b=boolExp)? ')' {  // Quantile of times
        $exp = $ctx.b != null ? $c.exp.quantile($q.value.doubleValue(), $b.exp) : $c.exp.quantile($q.value.doubleValue());
        }
    ;

/**
 * Parses date aggregate expressions, computing aggregates like MIN, MAX, AVG, MEDIAN, and QUANTILE over dates.
 *
 * Parameters:
 *  - The date column expression to aggregate.
 *  - A boolean expression to filter the rows involved in the aggregation (optional).
 *  - The quantile value to compute (between 0 and 1, for QUANTILE).
 *
 * Returns: *DateExp* - The resulting date aggregate expression.
 */
dateAgg returns [DateExp exp]
    : MIN '(' c=dateColumn (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.min($b.exp) : $c.exp.min(); }
    | MAX '(' c=dateColumn (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.max($b.exp) : $c.exp.max(); }
    | AVG '(' c=dateColumn (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.avg($b.exp) : $c.exp.avg(); }
    | MEDIAN '(' c=dateColumn (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.median($b.exp) : $c.exp.median(); }
    | QUANTILE '(' c=dateColumn ',' q=numScalar (',' b=boolExp)? ')' {
        $exp = $ctx.b != null
            ? $c.exp.quantile($q.value.doubleValue(), $b.exp)
            : $c.exp.quantile($q.value.doubleValue());
        }
    ;

/**
 * Parses datetime aggregate expressions, performing MIN, MAX, AVG, MEDIAN, or QUANTILE calculations over datetime values.
 *
 * Parameters:
 *  - The datetime column expression to aggregate.
 *  - A boolean expression to filter the rows involved in the aggregation (optional).
 *  - The quantile value to compute (between 0 and 1, for QUANTILE).
 *
 * Returns: *DateTimeExp* - The resulting datetime aggregate expression.
 */
dateTimeAgg returns [DateTimeExp exp]
    : MIN '(' c=dateTimeColumn (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.min($b.exp) : $c.exp.min(); }
    | MAX '(' c=dateTimeColumn (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.max($b.exp) : $c.exp.max(); }
    | AVG '(' c=dateTimeColumn (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.avg($b.exp) : $c.exp.avg(); }
    | MEDIAN '(' c=dateTimeColumn (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.median($b.exp) : $c.exp.median(); }
    | QUANTILE '(' c=dateTimeColumn ',' q=numScalar (',' b=boolExp)? ')' {
        $exp = $ctx.b != null
            ? $c.exp.quantile($q.value.doubleValue(), $b.exp)
            : $c.exp.quantile($q.value.doubleValue());
        }
    ;

/**
 * Parses string aggregate expressions, specifically MIN and MAX. These find the lexicographically minimum or maximum string value.
 *
 * Parameters:
 *  - The string expression to aggregate.
 *  - A filtering condition (optional).
 *
 * Returns: *StrExp* - The string aggregate expression, producing the MIN/MAX string value.
 */
strAgg returns [StrExp exp]
    : MIN '(' e=strExp (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $e.exp.min($b.exp) : $e.exp.min(); }
    | MAX '(' e=strExp (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $e.exp.max($b.exp) : $e.exp.max(); }
    ;

/// **Lexer rules**

// *General purpose tokens*

//@ doc:inline
LP: '(';

//@ doc:inline
RP: ')';

//@ doc:inline
PARENTHESES: '()';

//@ doc:inline
COMMA: ',';

// *General operators*

//@ doc:inline
NOT: 'not';

//@ doc:inline
EQ: '=';

//@ doc:inline
NE: '!=';

//@ doc:inline
LE: '<=';

//@ doc:inline
GE: '>=';

//@ doc:inline
LT: '<';

//@ doc:inline
GT: '>';

//@ doc:inline
BETWEEN: 'between';

//@ doc:inline
ADD: '+';

//@ doc:inline
SUB: '-';

//@ doc:inline
MUL: '*';

//@ doc:inline
DIV: '/';

//@ doc:inline
MOD: '%';

//@ doc:inline
AND: 'and';

//@ doc:inline
OR: 'or';

// *Column operators*

//@ doc:inline
BOOL: 'bool';

//@ doc:inline
INT: 'int';

//@ doc:inline
LONG: 'long';

//@ doc:inline
FLOAT: 'float';

//@ doc:inline
DOUBLE: 'double';

//@ doc:inline
DECIMAL: 'decimal';

//@ doc:inline
STR: 'str';

// *Cast functions*

//@ doc:inline
CAST_AS_BOOL: 'castAsBool';

//@ doc:inline
CAST_AS_INT: 'castAsInt';

//@ doc:inline
CAST_AS_LONG: 'castAsLong';

//@ doc:inline
CAST_AS_FLOAT: 'castAsFloat';

//@ doc:inline
CAST_AS_DOUBLE: 'castAsDouble';

//@ doc:inline
CAST_AS_DECIMAL: 'castAsDecimal';

//@ doc:inline
CAST_AS_STR: 'castAsStr';

//@ doc:inline
CAST_AS_TIME: 'castAsTime';

//@ doc:inline
CAST_AS_DATE: 'castAsDate';

//@ doc:inline
CAST_AS_DATETIME: 'castAsDateTime';

//@ doc:inline
CAST_AS_OFFSET_DATETIME: 'castAsOffsetDateTime';

// *Functions*

//@ doc:inline
IF: 'if';

//@ doc:inline
IF_NULL: 'ifNull';

//@ doc:inline
SPLIT: 'split';

//@ doc:inline
SHIFT: 'shift';

//@ doc:inline
CONCAT: 'concat';

//@ doc:inline
SUBSTR: 'substr';

//@ doc:inline
TRIM: 'trim';

//@ doc:inline
LEN: 'len';

//@ doc:inline
MATCHES: 'matches';

//@ doc:inline
STARTS_WITH: 'startsWith';

//@ doc:inline
ENDS_WITH: 'endsWith';

//@ doc:inline
CONTAINS: 'contains';

//@ doc:inline
DATE: 'date';

//@ doc:inline
TIME: 'time';

//@ doc:inline
DATETIME: 'dateTime';

//@ doc:inline
OFFSET_DATETIME: 'offsetDateTime';

//@ doc:inline
YEAR: 'year';

//@ doc:inline
MONTH: 'month';

//@ doc:inline
DAY: 'day';

//@ doc:inline
HOUR: 'hour';

//@ doc:inline
MINUTE: 'minute';

//@ doc:inline
SECOND: 'second';

//@ doc:inline
MILLISECOND: 'millisecond';

//@ doc:inline
PLUS_YEARS: 'plusYears';

//@ doc:inline
PLUS_MONTHS: 'plusMonths';

//@ doc:inline
PLUS_WEEKS: 'plusWeeks';

//@ doc:inline
PLUS_DAYS: 'plusDays';

//@ doc:inline
PLUS_HOURS: 'plusHours';

//@ doc:inline
PLUS_MINUTES: 'plusMinutes';

//@ doc:inline
PLUS_SECONDS: 'plusSeconds';

//@ doc:inline
PLUS_MILLISECONDS: 'plusMilliseconds';

//@ doc:inline
PLUS_NANOS: 'plusNanos';

//@ doc:inline
ABS: 'abs';

//@ doc:inline
ROUND: 'round';

//@ doc:inline
ROW_NUM: 'rowNum';

// *Aggregates*

//@ doc:inline
COUNT: 'count';

//@ doc:inline
SUM: 'sum';

//@ doc:inline
CUMSUM: 'cumSum';

//@ doc:inline
MIN: 'min';

//@ doc:inline
MAX: 'max';

//@ doc:inline
AVG: 'avg';

//@ doc:inline
MEDIAN: 'median';

//@ doc:inline
QUANTILE: 'quantile';

//@ doc:inline
FIRST: 'first';

//@ doc:inline
LAST: 'last';

/// *Literals*

//@ doc:inline
NULL: 'null';

//@ doc:inline
TRUE: 'true';

//@ doc:inline
FALSE: 'false';

/**
 * Matches a long integer literal, supporting decimal, hexadecimal, octal, and binary formats.
 */
LONG_LITERAL
    : [+-]? (
          DECIMAL_LITERAL
        | HEX_LITERAL
        | OCTAL_LITERAL
        | BINARY_LITERAL
    ) [lL]
    ;

/**
 * Matches an integer literal in decimal, hexadecimal, octal, or binary format.
 */
INTEGER_LITERAL
    : [+-]? (
          DECIMAL_LITERAL
        | HEX_LITERAL
        | OCTAL_LITERAL
        | BINARY_LITERAL
    )
    ;

/**
 * Matches a floating-point literal, supporting both decimal and hexadecimal representations.
 */
FLOATING_POINT_LITERAL
    : [+-]? (
          DECIMAL_FLOATING_POINT_LITERAL
        | HEXADECIMAL_FLOATING_POINT_LITERAL
    )
    ;

/**
 * Matches a string literal enclosed in single quotes. Supports standard Java escape sequences.
 */
SINGLE_QUOTE_STRING_LITERAL: '\'' ('\\\'' | ~['] | UNICODE_ESCAPE)* '\'';

/**
 * Matches a string literal enclosed in double quotes. Supports Java escape sequences.
 */
DOUBLE_QUOTE_STRING_LITERAL: '"' ('\\"' | ~["] | UNICODE_ESCAPE)* '"';

/**
 * Matches an identifier. Identifiers start with a letter and can be followed by letters or digits.
 */
IDENTIFIER: LETTER PART_LETTER*;

fragment DECIMAL_LITERAL: '0' | [1-9] ([0-9_]* [0-9])?;

fragment DECIMAL_LITERAL_LEADING_ZEROS: [0-9] ([0-9_]* [0-9])?;

fragment HEX_LITERAL: '0' [xX] HEX_DIGITS;

fragment OCTAL_LITERAL: '0' [0-7] ([0-7_]* [0-7])?;

fragment BINARY_LITERAL: '0' [bB] [01] ([01_]* [01])?;

fragment DECIMAL_FLOATING_POINT_LITERAL
    : DECIMAL_LITERAL_LEADING_ZEROS [fFdD]
    | DECIMAL_LITERAL_LEADING_ZEROS DECIMAL_EXPONENT [fFdD]?
    | DECIMAL_LITERAL_LEADING_ZEROS? '.' DECIMAL_LITERAL_LEADING_ZEROS DECIMAL_EXPONENT? [fFdD]?
    ;

fragment DECIMAL_EXPONENT: [eE] [+-]? DECIMAL_LITERAL;

fragment HEXADECIMAL_FLOATING_POINT_LITERAL
    : HEX_LITERAL '.'? HEXADECIMAL_EXPONENT [fFdD]?
    | '0' [xX] HEX_DIGITS? '.' HEX_DIGITS HEXADECIMAL_EXPONENT [fFdD]?
    ;

fragment HEXADECIMAL_EXPONENT: [pP] [+-]? DECIMAL_LITERAL;

fragment HEX_DIGITS: [0-9a-fA-F] ([0-9a-fA-F_]* [0-9a-fA-F])?;

//@ doc:inline
fragment UNICODE_ESCAPE: '\\u' [0-9a-fA-F] [0-9a-fA-F] [0-9a-fA-F] [0-9a-fA-F];

//@ doc:inline
fragment LETTER: [$A-Z_a-z];

//@ doc:inline
fragment PART_LETTER: [$0-9A-Z_a-z];

/**
 * Skipped symbols: Whitespace and tabs.
 */
WS: [ \t\r\n]+ -> skip;
