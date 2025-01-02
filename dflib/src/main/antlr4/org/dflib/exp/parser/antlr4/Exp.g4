grammar Exp;

@header {
import java.math.*;
import java.time.temporal.*;
import java.util.function.*;

import org.dflib.*;
import org.dflib.exp.*;
import org.dflib.exp.bool.*;
import org.dflib.exp.num.*;
import org.dflib.exp.str.*;
import org.dflib.exp.datetime.*;

import org.apache.commons.text.*;
}

@members {
private static Exp<?> col(Object columnId, Function<Integer, Exp<?>> byIndex, Function<String, Exp<?>> byName) {
    if (columnId instanceof Integer) {
        return byIndex.apply((Integer) columnId);
    } else if (columnId instanceof String) {
        return byName.apply((String) columnId);
    } else {
        throw new IllegalArgumentException("An integer or a string expected");
    }
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
    if (token.toLowerCase().endsWith("f")) {
        return Float.valueOf(token);
    } else {
        return Double.valueOf(token);
    }
}
}

/// **Parser rules**

/**
 * The root rule of the grammar. It parses an expression and expects the end of the file.
 *
 * Returns: *Exp<?>* - The parsed expression.
 */
root returns [Exp<?> exp]
    : expression EOF { $exp = $expression.exp; }
    ;

/**
 * Parses an expression which can be of various types including null, aggregate, boolean, numeric, string, temporal, or special functions.
 * An expression can be a single value or a combination of values and operators.
 * 
 * Returns: **Exp<?>** - The parsed expression.
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
 * Parses numeric expressions which can include scalar values, columns, functions, aggregates, and arithmetic operations.
 * Numeric expressions can be combined using arithmetic operators like +, -, \*, /, and %.
 * 
 * Returns: *NumExp<?>* - The parsed numeric expression.
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
 * Parses boolean expressions which can include scalar values, columns, functions, relations, and logical operations.
 * Boolean expressions can be combined using logical operators like and, or, and not.
 * 
 * Returns: *Condition* - The parsed boolean expression.
 */
boolExp returns [Condition exp]
    : '(' boolExp ')' { $exp = $boolExp.exp; }
    | boolScalar { $exp = (BoolScalarExp) Exp.\$val($boolScalar.value); }
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
 * Parses string expressions which can include scalar values, columns, and functions.
 * String expressions can be combined using various string functions.
 * 
 * Returns: *StrExp* - The parsed string expression.
 */
strExp returns [StrExp exp]
    : '(' strExp ')' { $exp = $strExp.exp; }
    | strScalar { $exp = (StrScalarExp) Exp.\$val($strScalar.value); }
    | strColumn { $exp = $strColumn.exp; }
    | strFn { $exp = $strFn.exp; }
    ;

/// **Temporal expressions**

/**
 * Parses temporal expressions which can include time, date, and datetime expressions.
 * Temporal expressions can be combined using various temporal functions.
 * 
 * Returns: *Exp<? extends Temporal>* - The parsed temporal expression.
 */
temporalExp returns [Exp<? extends Temporal> exp]
    : '(' temporalExp ')' { $exp = $temporalExp.exp; }
    | timeExp { $exp = $timeExp.exp; }
    | dateExp { $exp = $dateExp.exp; }
    | dateTimeExp { $exp = $dateTimeExp.exp; }
    ;

/**
 * Parses time expressions which can include columns and functions.
 * 
 * Returns: *TimeExp* - The parsed time expression.
 */
timeExp returns [TimeExp exp]
    : timeColumn { $exp = $timeColumn.exp; }
    | timeFn { $exp = $timeFn.exp; }
    ;

/**
 * Parses date expressions which can include columns and functions.
 * 
 * Returns: *DateExp* - The parsed date expression.
 */
dateExp returns [DateExp exp]
    : dateColumn { $exp = $dateColumn.exp; }
    | dateFn { $exp = $dateFn.exp; }
    ;

/**
 * Parses datetime expressions which can include columns and functions.
 * 
 * Returns: *DateTimeExp* - The parsed datetime expression.
 */
dateTimeExp returns [DateTimeExp exp]
    : dateTimeColumn { $exp = $dateTimeColumn.exp; }
    | dateTimeFn { $exp = $dateTimeFn.exp; }
    ;

/// **Scalar expressions**

/**
 * Parses scalar values which can be numeric, boolean, or string.
 * Scalar values are the basic building blocks of expressions.
 * 
 * Returns: *Object* - The parsed scalar value.
 */
scalar returns [Object value]
    : numScalar { $value = $numScalar.value; }
    | boolScalar { $value = $boolScalar.value; }
    | strScalar { $value = $strScalar.value; }
    ;

/**
 * Parses numeric scalar values which can be integers, longs, or floating-point numbers.
 * 
 * Returns: *Number* - The parsed numeric scalar value.
 */
numScalar returns [Number value]
    : integerScalar { $value = $integerScalar.value; }
    | longScalar { $value = $longScalar.value; }
    | floatingPointScalar { $value = $floatingPointScalar.value; }
    ;

/**
 * Parses long scalar values.
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
 * Parses integer scalar values.
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
 * Parses floating-point scalar values.
 * 
 * Returns: *Number* - The parsed floating-point scalar value.
 */
floatingPointScalar returns [Number value]
    : FLOATING_POINT_LITERAL { $value = floatingPointScalar($text); }
    ;

/**
 * Parses boolean scalar values.
 * 
 * Returns: *Boolean* - The parsed boolean scalar value.
 */
boolScalar returns [Boolean value]
    : TRUE { $value = true; }
    | FALSE { $value = false; }
    ;

/**
 * Parses string scalar values.
 * 
 * Returns: *String* - The parsed string scalar value.
 */
strScalar returns [String value]
    : SINGLE_QUOTE_STRING_LITERAL { $value = StringEscapeUtils.unescapeJava($text.substring(1, $text.length() - 1)); }
    | DOUBLE_QUOTE_STRING_LITERAL { $value = StringEscapeUtils.unescapeJava($text.substring(1, $text.length() - 1)); }
    ;

/// **Column expressions**

/**
 * Parses numeric column expressions which can include various numeric types.
 * Column expressions refer to columns in a dataset.
 * 
 * Returns: *NumExp<?>* - The parsed numeric column expression.
 */
numColumn returns [NumExp<?> exp]
    : intColumn { $exp = $intColumn.exp; }
    | longColumn { $exp = $longColumn.exp; }
    | floatColumn { $exp = $floatColumn.exp; }
    | doubleColumn { $exp = $doubleColumn.exp; }
    | decimalColumn { $exp = $decimalColumn.exp; }
    ;

/**
 * Parses integer column expressions.
 * 
 * Returns: *IntColumn* - The parsed integer column expression.
 */
intColumn returns [IntColumn exp]
    : INT '(' columnId ')' { $exp = (IntColumn) col($columnId.id, Exp::\$int, Exp::\$int); }
    ;

/**
 * Parses long column expressions.
 * 
 * Returns: *LongColumn* - The parsed long column expression.
 */
longColumn returns [LongColumn exp]
    : LONG '(' columnId ')' { $exp = (LongColumn) col($columnId.id, Exp::\$long, Exp::\$long); }
    ;

/**
 * Parses float column expressions.
 * 
 * Returns: *FloatColumn* - The parsed float column expression.
 */
floatColumn returns [FloatColumn exp]
    : FLOAT '(' columnId ')' { $exp = (FloatColumn) col($columnId.id, Exp::\$float, Exp::\$float); }
    ;

/**
 * Parses double column expressions.
 * 
 * Returns: *DoubleColumn* - The parsed double column expression.
 */
doubleColumn returns [DoubleColumn exp]
    : DOUBLE '(' columnId ')' { $exp = (DoubleColumn) col($columnId.id, Exp::\$double, Exp::\$double); }
    ;

/**
 * Parses decimal column expressions.
 * 
 * Returns: *DecimalColumn* - The parsed decimal column expression.
 */
decimalColumn returns [DecimalColumn exp]
    : DECIMAL '(' columnId ')' { $exp = (DecimalColumn) col($columnId.id, Exp::\$decimal, Exp::\$decimal); }
    ;

/**
 * Parses boolean column expressions.
 * 
 * Returns: *BoolColumn* - The parsed boolean column expression.
 */
boolColumn returns [BoolColumn exp]
    : BOOL '(' columnId ')' { $exp = (BoolColumn) col($columnId.id, Exp::\$bool, Exp::\$bool); }
    ;

/**
 * Parses string column expressions.
 * 
 * Returns: *StrColumn* - The parsed string column expression.
 */
strColumn returns [StrColumn exp]
    : STR '(' columnId ')' { $exp = (StrColumn) col($columnId.id, Exp::\$str, Exp::\$str); }
    ;

/**
 * Parses date column expressions.
 * 
 * Returns: *DateColumn* - The parsed date column expression.
 */
dateColumn returns [DateColumn exp]
    : DATE '(' columnId ')' { $exp = (DateColumn) col($columnId.id, Exp::\$date, Exp::\$date); }
    ;

/**
 * Parses time column expressions.
 * 
 * Returns: *TimeColumn* - The parsed time column expression.
 */
timeColumn returns [TimeColumn exp]
    : TIME '(' columnId ')' { $exp = (TimeColumn) col($columnId.id, Exp::\$time, Exp::\$time); }
    ;

/**
 * Parses datetime column expressions.
 * 
 * Returns: *DateTimeColumn* - The parsed datetime column expression.
 */
dateTimeColumn returns [DateTimeColumn exp]
    : DATETIME '(' columnId ')' { $exp = (DateTimeColumn) col($columnId.id, Exp::\$dateTime, Exp::\$dateTime); }
    ;

/**
 * Parses column identifiers which can be integers, strings, or identifiers.
 * 
 * Returns: *Object* - The parsed column identifier.
 */
columnId returns [Object id]
    : integerScalar { $id = $integerScalar.value; }
    | strScalar { $id = $strScalar.value; }
    | identifier { $id = $identifier.id; }
    ;

/**
 * Parses identifiers.
 * 
 * Returns: *String* - The parsed identifier.
 */
identifier returns [String id]
    : IDENTIFIER { $id = $text; }
    ;

/// **Relational expressions**

/**
 * Parses relational expressions which can include numeric, string, time, date, and datetime relations.
 * Relational expressions compare two values using relational operators like >, <, =, etc.
 * 
 * Returns: *Condition* - The parsed relational expression.
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
 * Parses numeric relational expressions.
 * 
 * Returns: *Condition* - The parsed numeric relational expression.
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
 * Parses string relational expressions.
 * 
 * Returns: *Condition* - The parsed string relational expression.
 */
strRelation returns [Condition exp]
    : a=strExp (
        : EQ b=strExp { $exp = $a.exp.eq($b.exp); }
        | NE b=strExp { $exp = $a.exp.ne($b.exp); }
    )
    ;

/**
 * Parses time relational expressions.
 * 
 * Returns: *Condition* - The parsed time relational expression.
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
 * Parses date relational expressions.
 * 
 * Returns: *Condition* - The parsed date relational expression.
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
 * Parses datetime relational expressions.
 * 
 * Returns: *Condition* - The parsed datetime relational expression.
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

/// **Functions**

/**
 * Parses numeric functions which can include casting, counting, row number, absolute value, rounding, length, and field functions.
 * 
 * Returns: *NumExp<?>* - The parsed numeric function.
 */
numFn returns [NumExp<?> exp]
    : castAsInt { $exp = $castAsInt.exp; }
    | castAsLong { $exp = $castAsLong.exp; }
    | castAsFloat { $exp = $castAsFloat.exp; }
    | castAsDouble { $exp = $castAsDouble.exp; }
    | castAsDecimal { $exp = $castAsDecimal.exp; }
    | COUNT ('()' | '(' b=boolExp ')') { $exp = $ctx.b != null ? Exp.count($b.exp) : Exp.count(); }
    | ROW_NUM '()' { $exp = Exp.rowNum(); }
    | ABS '(' numExp ')' { $exp = $numExp.exp.abs(); }
    | ROUND '(' numExp ')' { $exp = $numExp.exp.round(); }
    | LEN '(' strExp ')' { $exp = $strExp.exp.mapVal(String::length).castAsInt(); }
    | timeFieldFn { $exp = $timeFieldFn.exp; }
    | dateFieldFn { $exp = $dateFieldFn.exp; }
    | dateTimeFieldFn { $exp = $dateTimeFieldFn.exp; }
    ;

/**
 * Parses time field functions which can include hour, minute, second, and millisecond.
 * 
 * Returns: *NumExp<Integer>* - The parsed time field function.
 */
timeFieldFn returns [NumExp<Integer> exp]
    : HOUR '(' timeExp ')' { $exp = $timeExp.exp.hour(); }
    | MINUTE '(' timeExp ')' { $exp = $timeExp.exp.minute(); }
    | SECOND '(' timeExp ')' { $exp = $timeExp.exp.second(); }
    | MILLISECOND '(' timeExp ')' { $exp = $timeExp.exp.millisecond(); }
    ;

/**
 * Parses date field functions which can include year, month, and day.
 * 
 * Returns: *NumExp<Integer>* - The parsed date field function.
 */
dateFieldFn returns [NumExp<Integer> exp]
    : YEAR '(' dateExp ')' { $exp = $dateExp.exp.year(); }
    | MONTH '(' dateExp ')' { $exp = $dateExp.exp.month(); }
    | DAY '(' dateExp ')' { $exp = $dateExp.exp.day(); }
    ;

/**
 * Parses datetime field functions which can include year, month, day, hour, minute, second, and millisecond.
 * 
 * Returns: *NumExp<Integer>* - The parsed datetime field function.
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
 * Parses boolean functions which can include casting, matches, starts with, ends with, and contains.
 * 
 * Returns: *Condition* - The parsed boolean function.
 */
boolFn returns [Condition exp]
    : castAsBool { $exp = $castAsBool.exp; }
    | MATCHES '(' strExp ',' strScalar ')' { $exp = $strExp.exp.matches($strScalar.value); }
    | STARTS_WITH '(' strExp ',' strScalar ')' { $exp = $strExp.exp.startsWith($strScalar.value); }
    | ENDS_WITH '(' strExp ',' strScalar ')' { $exp = $strExp.exp.endsWith($strScalar.value); }
    | CONTAINS '(' strExp ',' strScalar ')' { $exp = $strExp.exp.contains($strScalar.value); }
    ;

/**
 * Parses time functions which can include casting and various time arithmetic operations.
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
 * Parses date functions which can include casting and various date arithmetic operations.
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
 * Parses datetime functions which can include casting and various datetime arithmetic operations.
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
 * Parses string functions which can include casting, trimming, and substring operations.
 * 
 * Returns: *StrExp* - The parsed string function.
 */
strFn returns [StrExp exp]
    : castAsStr { $exp = $castAsStr.exp; }
    | TRIM '(' strExp ')' { $exp = $strExp.exp.trim(); }
    | SUBSTR '(' s=strExp ',' a=integerScalar (',' b=integerScalar)? ')' {
        $exp = $ctx.b != null ? $s.exp.substr($a.value, $b.value) : $s.exp.substr($a.value);
        }
    ;

/// **Cast functions**

/**
 * Parses cast to boolean function.
 * 
 * Returns: *Condition* - The parsed cast to boolean function.
 */
castAsBool returns [Condition exp]
    : CAST_AS_BOOL '(' expression ')' { $exp = $expression.exp.castAsBool(); }
    ;

/**
 * Parses cast to integer function.
 * 
 * Returns: *NumExp<Integer>* - The parsed cast to integer function.
 */
castAsInt returns [NumExp<Integer> exp]
    : CAST_AS_INT '(' expression ')' { $exp = $expression.exp.castAsInt(); }
    ;

/**
 * Parses cast to long function.
 * 
 * Returns: *NumExp<Long>* - The parsed cast to long function.
 */
castAsLong returns [NumExp<Long> exp]
    : CAST_AS_LONG '(' expression ')' { $exp = $expression.exp.castAsLong(); }
    ;

/**
 * Parses cast to float function.
 * 
 * Returns: *NumExp<Float>* - The parsed cast to float function.
 */
castAsFloat returns [NumExp<Float> exp]
    : CAST_AS_FLOAT '(' expression ')' { $exp = $expression.exp.castAsFloat(); }
    ;

/**
 * Parses cast to double function.
 * 
 * Returns: *NumExp<Double>* - The parsed cast to double function.
 */
castAsDouble returns [NumExp<Double> exp]
    : CAST_AS_DOUBLE '(' expression ')' { $exp = $expression.exp.castAsDouble(); }
    ;

/**
 * Parses cast to decimal function.
 * 
 * Returns: *NumExp<BigDecimal>* - The parsed cast to decimal function.
 */
castAsDecimal returns [NumExp<BigDecimal> exp]
    : CAST_AS_DECIMAL '(' expression ')' { $exp = $expression.exp.castAsDecimal(); }
    ;

/**
 * Parses cast to string function.
 * 
 * Returns: *StrExp* - The parsed cast to string function.
 */
castAsStr returns [StrExp exp]
    : CAST_AS_STR '(' expression ')' { $exp = $expression.exp.castAsStr(); }
    ;

/**
 * Parses cast to time function.
 * 
 * Returns: *TimeExp* - The parsed cast to time function.
 */
castAsTime returns [TimeExp exp]
    : CAST_AS_TIME '(' e=expression (',' f=strScalar )? ')' {
        $exp = $ctx.f != null ? $e.exp.castAsTime($f.value) : $e.exp.castAsTime();
        }
    ;

/**
 * Parses cast to date function.
 * 
 * Returns: *DateExp* - The parsed cast to date function.
 */
castAsDate returns [DateExp exp]
    : CAST_AS_DATE '(' e=expression (',' f=strScalar )? ')' {
        $exp = $ctx.f != null ? $e.exp.castAsDate($f.value) : $e.exp.castAsDate();
        }
    ;

/**
 * Parses cast to datetime function.
 * 
 * Returns: *DateTimeExp* - The parsed cast to datetime function.
 */
castAsDateTime returns [DateTimeExp exp]
    : CAST_AS_DATETIME '(' e=expression (',' f=strScalar )? ')' {
        $exp = $ctx.f != null ? $e.exp.castAsDateTime($f.value) : $e.exp.castAsDateTime();
        }
    ;

/// **Special functions**

/**
 * Parses special functions which can include if expressions, if null expressions, split, and shift.
 * 
 * Returns: *Exp<?>* - The parsed special function.
 */
specialFn returns [Exp<?> exp]
    : ifExp { $exp = $ifExp.exp; }
    | ifNull { $exp = $ifNull.exp; }
    | split { $exp = $split.exp; }
    | shift { $exp = $shift.exp; }
    ;

/**
 * Parses if expressions.
 * 
 * Returns: *Exp<?>* - The parsed if expression.
 */
ifExp returns [Exp<?> exp]
    : IF '(' (
          a=boolExp ',' b1=boolExp ',' b2=boolExp { $exp = Exp.ifExp($a.exp, $b1.exp, $b2.exp); }
        | a=boolExp ',' s1=strExp ',' s2=strExp { $exp = Exp.ifExp($a.exp, $s1.exp, $s2.exp); }
        | a=boolExp ',' t1=timeExp ',' t2=timeExp { $exp = Exp.ifExp($a.exp, $t1.exp, $t2.exp); }
        | a=boolExp ',' d1=dateExp ',' d2=dateExp { $exp = Exp.ifExp($a.exp, $d1.exp, $d2.exp); }
        | a=boolExp ',' dt1=dateTimeExp ',' dt2=dateTimeExp { $exp = Exp.ifExp($a.exp, $dt1.exp, $dt2.exp); }
        | a=boolExp ',' n1=numExp ',' n2=numExp {
            $exp = Exp.ifExp($a.exp, (NumExp<Number>) $n1.exp, (NumExp<Number>) $n2.exp);
            }
    ) ')'
    ;

/**
 * Parses if null expressions.
 * 
 * Returns: *Exp<?>* - The parsed if null expression.
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
 * Parses split expressions.
 * 
 * Returns: *Exp<String[]>* - The parsed split expression.
 */
split returns [Exp<String[]> exp]
    : SPLIT '(' a=strExp ',' b=strScalar (',' c=integerScalar)? ')' {
        $exp = $ctx.c != null ? $a.exp.split($b.value, $c.value) : $a.exp.split($b.value);
        }
    ;

/**
 * Parses shift expressions.
 * 
 * Returns: *Exp<?>* - The parsed shift expression.
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
 * Parses aggregate expressions which can include positional, numeric, time, date, datetime, and string aggregates.
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
 * Parses positional aggregate expressions.
 * 
 * Returns: *Exp<?>* - The parsed positional aggregate expression.
 */
positionalAgg returns [Exp<?> exp]
    : FIRST '(' e=expression (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $e.exp.first($b.exp) : $e.exp.first(); }
    | LAST '(' e=expression ')' { $exp = $e.exp.last(); } // TODO: bool condition
    ;

/**
 * Parses numeric aggregate expressions.
 * 
 * Returns: *NumExp<?>* - The parsed numeric aggregate expression.
 */
numAgg returns [NumExp<?> exp]
    : MIN '(' c=numColumn (',' b=boolExp)? ')' { $exp = $c.exp.min($ctx.b != null ? $b.exp : null); }
    | MAX '(' c=numColumn (',' b=boolExp)? ')' { $exp = $c.exp.max($ctx.b != null ? $b.exp : null); }
    | SUM '(' c=numColumn (',' b=boolExp)? ')' { $exp = $c.exp.sum($ctx.b != null ? $b.exp : null); }
    | CUMSUM '(' c=numColumn ')' { $exp = $c.exp.cumSum(); }
    | AVG '(' c=numColumn (',' b=boolExp)? ')' { $exp = $c.exp.avg($ctx.b != null ? $b.exp : null); }
    | MEDIAN '(' c=numColumn (',' b=boolExp)? ')' { $exp = $c.exp.median($ctx.b != null ? $b.exp : null); }
    | QUANTILE '(' c=numColumn ',' q=numScalar (',' b=boolExp)? ')' {
            $exp = $ctx.b != null
                ? $c.exp.quantile($q.value.doubleValue(), $b.exp)
                : $c.exp.quantile($q.value.doubleValue());
            }
    ;

/**
 * Parses time aggregate expressions.
 * 
 * Returns: *TimeExp* - The parsed time aggregate expression.
 */
timeAgg returns [TimeExp exp]
    : MIN '(' c=timeColumn (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.min($b.exp) : $c.exp.min(); }
    | MAX '(' c=timeColumn (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.max($b.exp) : $c.exp.max(); }
    | AVG '(' c=timeColumn (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.avg($b.exp) : $c.exp.avg(); }
    | MEDIAN '(' c=timeColumn (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.median($b.exp) : $c.exp.median(); }
    | QUANTILE '(' c=timeColumn ',' q=numScalar (',' b=boolExp)? ')' {
        $exp = $ctx.b != null
            ? $c.exp.quantile($q.value.doubleValue(), $b.exp)
            : $c.exp.quantile($q.value.doubleValue());
        }
    ;

/**
 * Parses date aggregate expressions.
 * 
 * Returns: *DateExp* - The parsed date aggregate expression.
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
 * Parses datetime aggregate expressions.
 * 
 * Returns: *DateTimeExp* - The parsed datetime aggregate expression.
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
 * Parses string aggregate expressions.
 * 
 * Returns: *StrExp* - The parsed string aggregate expression.
 */
strAgg returns [StrExp exp]
    : MIN '(' e=strExp (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $e.exp.min($b.exp) : $e.exp.min(); }
    | MAX '(' e=strExp (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $e.exp.max($b.exp) : $e.exp.max(); }
    ;

/// **Lexer rules**

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
CUMSUM: 'cumsum';

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

//@ doc:inline
LONG_LITERAL
    : [+-]? (
          DECIMAL_LITERAL
        | HEX_LITERAL
        | OCTAL_LITERAL
        | BINARY_LITERAL
    ) [lL]
    ;

//@ doc:inline
INTEGER_LITERAL
    : [+-]? (
          DECIMAL_LITERAL
        | HEX_LITERAL
        | OCTAL_LITERAL
        | BINARY_LITERAL
    )
    ;

//@ doc:inline
FLOATING_POINT_LITERAL
    : [+-]? (
          DECIMAL_FLOATING_POINT_LITERAL
        | HEXADECIMAL_FLOATING_POINT_LITERAL
    )
    ;

SINGLE_QUOTE_STRING_LITERAL: '\'' (~['\\\n\r] | ESCAPE | UNICODE_ESCAPE)* '\'';

DOUBLE_QUOTE_STRING_LITERAL: '"' (~["\\\n\r] | ESCAPE | UNICODE_ESCAPE)* '"';

//@ doc:inline
IDENTIFIER: LETTER PART_LETTER*;

fragment DECIMAL_LITERAL: '0' | [1-9] ([0-9_]* [0-9])?;

fragment DECIMAL_LITERAL_LEADING_ZEROS: [0-9] ([0-9_]* [0-9])?;

fragment HEX_LITERAL: '0' [xX] HEX_DIGITS;

fragment OCTAL_LITERAL: '0' [0-7] ([0-7_]* [0-7])?;

fragment BINARY_LITERAL: '0' [bB] [01] ([01_]* [01])?;

fragment DECIMAL_FLOATING_POINT_LITERAL
    : DECIMAL_LITERAL_LEADING_ZEROS '.' DECIMAL_LITERAL_LEADING_ZEROS? DECIMAL_EXPONENT? [fFdD]?
    | '.' DECIMAL_LITERAL_LEADING_ZEROS DECIMAL_EXPONENT? [fFdD]?
    | DECIMAL_LITERAL_LEADING_ZEROS DECIMAL_EXPONENT [fFdD]?
    ;

fragment DECIMAL_EXPONENT: [eE] [+-]? DECIMAL_LITERAL;

fragment HEXADECIMAL_FLOATING_POINT_LITERAL
    : HEX_LITERAL '.'? HEXADECIMAL_EXPONENT [fFdD]?
    | '0' [xX] HEX_DIGITS? '.' HEX_DIGITS HEXADECIMAL_EXPONENT [fFdD]?
    ;

fragment HEXADECIMAL_EXPONENT: [pP] [+-]? DECIMAL_LITERAL;

fragment HEX_DIGITS: [0-9a-fA-F] ([0-9a-fA-F_]* [0-9a-fA-F])?;

//@ doc:inline
fragment ESCAPE: '\\' ([sntrbf\\"'] | [0-7] [0-7]? | [0-3] [0-7] [0-7]);

//@ doc:inline
fragment UNICODE_ESCAPE: '\\u' HEX_DIGITS;

//@ doc:inline
fragment LETTER: [$A-Z_a-z];

//@ doc:inline
fragment PART_LETTER: [$0-9A-Z_a-z];

/**
 * Skipped symbols.
 */
WS: [ \t\r\n]+ -> skip;
