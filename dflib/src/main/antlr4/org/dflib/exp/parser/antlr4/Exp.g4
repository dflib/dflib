grammar Exp;

@header {
import java.time.temporal.*;
import java.util.function.*;

import org.dflib.*;
import org.dflib.exp.*;
import org.dflib.exp.bool.*;
import org.dflib.exp.num.*;
import org.dflib.exp.str.*;
import org.dflib.exp.datetime.*;
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

private static Number floatingPointScalar(String token) {
    return token.toLowerCase().endsWith("f")
            ? Float.parseFloat(token)
            : Double.parseDouble(token);
}
}

/// **Parser rules**
///
/// This section defines the parser rules (grammar entry points) used to parse the
/// input expressions into Abstract Syntax Tree (AST) elements. These rules
/// represent high-level language constructs such as numeric, string, conditional,
/// temporal, and aggregate expressions.

/**
 * This is the grammar root.
 *
 * The root rule serves as the main entry point for processing expressions.
 * The input expression must conform to the grammar structure and ends (EOF).
 */
root returns [Exp<?> exp]
    : expression EOF { $exp = $expression.exp; }
    ;

/**
 * This rule parses any valid expression.
 *
 * It enables a wide array of subtypes including null, nested expressions, aggregation,
 * boolean, numeric, string, temporal, conditional, and other supported constructs.
 */
expression returns [Exp<?> exp]
    : NULL { $exp = Exp.\$val(null); }
    | '(' expression ')' { $exp = $expression.exp; }
    | agg { $exp = $agg.exp; }
    | boolExp { $exp = $boolExp.exp; }
    | numExp { $exp = $numExp.exp; }
    | strExp { $exp = $strExp.exp; }
    | temporalExp { $exp = $temporalExp.exp; }
    | ifExp { $exp = $ifExp.exp; }
    | ifNull { $exp = $ifNull.exp; }
    | split { $exp = $split.exp; }
    ;

/// **Numeric expressions**
///
/// This section defines constructs used for numeric expressions, which
/// include scalars, columns, functions, and arithmetic operations.

/**
 * Parses numeric expressions, enabling combinations of calculations,
 * nested sub-expressions, or references to columns and scalar values.
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
///
/// This section defines boolean operators, enabling logical combinations,
/// scalar values, conditions, and relations.

/**
 * Parses boolean expressions that may reference scalar values,
 * columns, logical combinations (AND, OR), and conditions.
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
///
/// This section defines string operators, scalar values, column references,
/// and applicable functions.

/**
 * Parses string expressions, allowing concatenations,
 * scalar assignments, column references, and function invocations.
 */
strExp returns [StrExp exp]
    : '(' strExp ')' { $exp = $strExp.exp; }
    | strScalar { $exp = (StrScalarExp) Exp.\$val($strScalar.value); }
    | strColumn { $exp = $strColumn.exp; }
    | strFn { $exp = $strFn.exp; }
    ;

/// **Temporal expressions**
///
/// This section defines temporal-oriented operations (time, date, datetime),
/// as well as their scalar and column components.

/**
 * Parses general temporal expressions.
 * Temporal types include time, date, or date-time values.
 */
temporalExp returns [Exp<? extends Temporal> exp]
    : '(' temporalExp ')' { $exp = $temporalExp.exp; }
    | timeExp { $exp = $timeExp.exp; }
    | dateExp { $exp = $dateExp.exp; }
    | dateTimeExp { $exp = $dateTimeExp.exp; }
    ;

/**
 * Constructs expressions specific to time-based functions and columns.
 */
timeExp returns [TimeExp exp]
    : timeColumn { $exp = $timeColumn.exp; }
    | timeFn { $exp = $timeFn.exp; }
    ;

/**
 * Constructs expressions specific to date-based manipulations.
 */
dateExp returns [DateExp exp]
    : dateColumn { $exp = $dateColumn.exp; }
    | dateFn { $exp = $dateFn.exp; }
    ;

/**
 * Supports expressions using date-time for advanced operations.
 */
dateTimeExp returns [DateTimeExp exp]
    : dateTimeColumn { $exp = $dateTimeColumn.exp; }
    | dateTimeFn { $exp = $dateTimeFn.exp; }
    ;

/// **Scalar expressions**
///
/// Defines scalar values such as numbers, booleans, and strings that
/// are single literals or constants.

/**
 * Represents numeric scalar values (e.g., integers, floating points).
 */
numScalar returns [Number value]
    : longScalar { $value = $longScalar.value; }
    | integerScalar { $value = $integerScalar.value; }
    | floatingPointScalar { $value = $floatingPointScalar.value; }
    ;

/**
 * Represents individual long scalar values.
 */
longScalar returns [Long value]
    : LONG_LITERAL { $value = Long.parseLong($text); }
    ;

/**
 * Represents individual integer scalar values.
 * These are numerical constants that can be directly used in expressions.
 */
integerScalar returns [Integer value]
    : INTEGER_LITERAL { $value = Integer.parseInt($text); }
    ;

/**
 * Represents floating-point scalar values.
 * Includes both single-precision (float) and double-precision (double) values.
 */
floatingPointScalar returns [Number value]
    : FLOATING_POINT_LITERAL { $value = floatingPointScalar($text); }
    ;

/**
 * Represents boolean scalar values. Can be either `true` or `false`.
 */
boolScalar returns [Boolean value]
    : TRUE { $value = true; }
    | FALSE { $value = false; }
    ;

/**
 * Represents string scalar values, such as words or longer text.
 * Can be enclosed in quotes as single characters or string literals.
 */
strScalar returns [String value]
    : CHARACTER_LITERAL { $value = $text.substring(1, $text.length() - 1); }
    | STRING_LITERAL { $value = $text.substring(1, $text.length() - 1); }
    ;

/// **Column expressions**
///
/// This section handles expressions that operate on columns, allowing
/// users to reference and manipulate numeric, boolean, string, date,
/// time, or datetime data at the column level.

/**
 * Represents numeric column expressions, allowing references to columns
 * containing integer, long, float, double, or decimal data.
 * Each column type maps to a corresponding subtype of `NumExp<?>`.
 */
numColumn returns [NumExp<?> exp]
    : intColumn { $exp = $intColumn.exp; }
    | longColumn { $exp = $longColumn.exp; }
    | floatColumn { $exp = $floatColumn.exp; }
    | doubleColumn { $exp = $doubleColumn.exp; }
    | decimalColumn { $exp = $decimalColumn.exp; }
    ;

/**
 * Represents references to integer columns by their ID.
 * An integer column is defined by its relationship to the schema.
 */
intColumn returns [IntColumn exp]
    : INT '(' columnId ')' { $exp = (IntColumn) col($columnId.id, Exp::\$int, Exp::\$int); }
    ;

/**
 * Represents references to long columns by their ID.
 */
longColumn returns [LongColumn exp]
    : LONG '(' columnId ')' { $exp = (LongColumn) col($columnId.id, Exp::\$long, Exp::\$long); }
    ;

/**
 * Represents references to float columns by their ID.
 */
floatColumn returns [FloatColumn exp]
    : FLOAT '(' columnId ')' { $exp = (FloatColumn) col($columnId.id, Exp::\$float, Exp::\$float); }
    ;

/**
 * Represents references to double columns by their ID.
 */
doubleColumn returns [DoubleColumn exp]
    : DOUBLE '(' columnId ')' { $exp = (DoubleColumn) col($columnId.id, Exp::\$double, Exp::\$double); }
    ;

/**
 * Represents references to decimal columns by their ID.
 */
decimalColumn returns [DecimalColumn exp]
    : DECIMAL '(' columnId ')' { $exp = (DecimalColumn) col($columnId.id, Exp::\$decimal, Exp::\$decimal); }
    ;

/**
 * Represents references to boolean columns by their ID.
 */
boolColumn returns [BoolColumn exp]
    : BOOL '(' columnId ')' { $exp = (BoolColumn) col($columnId.id, Exp::\$bool, Exp::\$bool); }
    ;

/**
 * Represents references to string columns by their ID.
 */
strColumn returns [StrColumn exp]
    : STR '(' columnId ')' { $exp = (StrColumn) col($columnId.id, Exp::\$str, Exp::\$str); }
    ;

/**
 * Represents references to date columns by their ID.
 */
dateColumn returns [DateColumn exp]
    : DATE '(' columnId ')' { $exp = (DateColumn) col($columnId.id, Exp::\$date, Exp::\$date); }
    ;

/**
 * Represents references to time columns by their ID.
 */
timeColumn returns [TimeColumn exp]
    : TIME '(' columnId ')' { $exp = (TimeColumn) col($columnId.id, Exp::\$time, Exp::\$time); }
    ;

/**
 * Represents references to datetime columns by their ID.
 */
dateTimeColumn returns [DateTimeColumn exp]
    : DATETIME '(' columnId ')' { $exp = (DateTimeColumn) col($columnId.id, Exp::\$dateTime, Exp::\$dateTime); }
    ;

/**
 * Represents a column ID, which can be an integer, string, or identifier.
 * Column IDs help locate specific data in a table schema.
 */
columnId returns [Object id]
    : integerScalar { $id = $integerScalar.value; }
    | strScalar { $id = $strScalar.value; }
    | identifier { $id = $identifier.id; }
    ;

/**
 * Represents identifiers, which are user-defined names for columns, functions, or variables.
 */
identifier returns [String id]
    : IDENTIFIER { $id = $text; }
    ;

/// **Relational expressions**
///
/// Defines rules for relational expressions and operations.
///
/// These include comparisons (e.g., greater than, equal to),
/// as well as more complex relationships such as "between" conditions.

/**
 * Represents relational expression rules, covering numeric, string, time,
 * date, and datetime comparisons.
 */
relation returns [Condition exp]
    : numRelation { $exp = $numRelation.exp; }
    | strRelation { $exp = $strRelation.exp; }
    | timeRelation { $exp = $timeRelation.exp; }
    | dateRelation { $exp = $dateRelation.exp; }
    | dateTimeRelation { $exp = $dateTimeRelation.exp; }
    ;

/**
 * Handles numeric comparisons, such as equality, inequality, or range checks.
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
 * Represents relationships between string expressions, such as equality and inequality.
 */
strRelation returns [Condition exp]
    : a=strExp (
        : EQ b=strExp { $exp = $a.exp.eq($b.exp); }
        | NE b=strExp { $exp = $a.exp.ne($b.exp); }
    )
    ;

/**
 * Handles time-based comparisons, e.g., checking if one time is less than another.
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
 * Defines relational comparisons for date-based expressions, such as range checks.
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
 * Parses comparisons for datetime expressions, including equality and range checks.
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
///
/// Defines built-in functions supported by the grammar.
///
/// Functions include numeric utilities (`ABS`, `ROUND`), temporal utilities
/// (`PLUS_DAYS`, `YEAR`), string utilities (`TRIM`, `SUBSTR`), and more.

/**
 * Represents numerical functions, such as `ABS` or `ROUND`.
 * These functions are applied directly to numeric expressions.
 * They also include capabilities to retrieve row numbers or length
 * of a string and to extract components from temporal expressions.
 */
numFn returns [NumExp<?> exp]
    : COUNT ('()' | '(' b=boolExp ')') { $exp = $ctx.b != null ? Exp.count($b.exp) : Exp.count(); }
    | ROW_NUM '()' { $exp = Exp.rowNum(); }
    | ABS '(' numExp ')' { $exp = $numExp.exp.abs(); }
    | ROUND '(' numExp ')' { $exp = $numExp.exp.round(); }
    | LEN '(' strExp ')' { $exp = $strExp.exp.mapVal(String::length).castAsInt(); }
    | timeFieldFn { $exp = $timeFieldFn.exp; }
    | dateFieldFn { $exp = $dateFieldFn.exp; }
    | dateTimeFieldFn { $exp = $dateTimeFieldFn.exp; }
    ;

/**
 * Extracts unit-specific fields from time-based expressions, such as hour, minute, second, or millisecond.
 * These functions allow breaking down time expressions into granular parts for calculations or comparisons.
 */
timeFieldFn returns [NumExp<Integer> exp]
    : HOUR '(' timeExp ')' { $exp = $timeExp.exp.hour(); }
    | MINUTE '(' timeExp ')' { $exp = $timeExp.exp.minute(); }
    | SECOND '(' timeExp ')' { $exp = $timeExp.exp.second(); }
    | MILLISECOND '(' timeExp ')' { $exp = $timeExp.exp.millisecond(); }
    ;

/**
 * Extracts unit-specific fields from date expressions, such as year, month, or day.
 * These functions enable fine-grained access to the components of a date for analysis or formatting.
 */
dateFieldFn returns [NumExp<Integer> exp]
    : YEAR '(' dateExp ')' { $exp = $dateExp.exp.year(); }
    | MONTH '(' dateExp ')' { $exp = $dateExp.exp.month(); }
    | DAY '(' dateExp ')' { $exp = $dateExp.exp.day(); }
    ;

/**
 * Extracts unit-specific fields from datetime expressions,
 * such as year, month, day, hour, minute, second, or millisecond.
 * Useful for date-time manipulation where both date and time parts are considered.
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
 * Represents string-based functions that return boolean results.
 * Example includes matching one string to a specific pattern.
 */
boolFn returns [Condition exp]
    : MATCHES '(' strExp ',' strScalar ')' { $exp = $strExp.exp.matches($strScalar.value); }
    ;

/**
 * Represents time manipulation functions.
 * These include addition of units such as hours, minutes, seconds,
 * milliseconds, or nanoseconds to base time expressions.
 */
timeFn returns [TimeExp exp]
    : PLUS_HOURS '(' a=timeExp ',' b=integerScalar ')' { $exp = $a.exp.plusHours($b.value); }
    | PLUS_MINUTES '(' a=timeExp ',' b=integerScalar ')' { $exp = $a.exp.plusMinutes($b.value); }
    | PLUS_SECONDS '(' a=timeExp ',' b=integerScalar ')' { $exp = $a.exp.plusSeconds($b.value); }
    | PLUS_MILLISECONDS '(' a=timeExp ',' b=integerScalar ')' { $exp = $a.exp.plusMilliseconds($b.value); }
    | PLUS_NANOS '(' a=timeExp ',' b=integerScalar ')' { $exp = $a.exp.plusNanos($b.value); }
    ;

/**
 * Represents date manipulation functions.
 * These cover the addition of various units such as years, months, weeks, or days to date expressions.
 */
dateFn returns [DateExp exp]
    : PLUS_YEARS '(' a=dateExp ',' b=integerScalar ')' { $exp = $a.exp.plusYears($b.value); }
    | PLUS_MONTHS '(' a=dateExp ',' b=integerScalar ')' { $exp = $a.exp.plusMonths($b.value); }
    | PLUS_WEEKS '(' a=dateExp ',' b=integerScalar ')' { $exp = $a.exp.plusWeeks($b.value); }
    | PLUS_DAYS '(' a=dateExp ',' b=integerScalar ')' { $exp = $a.exp.plusDays($b.value); }
    ;

/**
 * Represents datetime manipulation functions.
 * These allow adding units such as years, months, days, hours, minutes, seconds,
 * milliseconds, or nanoseconds to datetime expressions.
 */
dateTimeFn returns [DateTimeExp exp]
    : PLUS_YEARS '(' a=dateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusYears($b.value); }
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
 * Represents string manipulation functions.
 * These include trimming or extracting substrings from string expressions.
 */
strFn returns [StrExp exp]
    : trimFn { $exp = $trimFn.exp; }
    | substrFn { $exp = $substrFn.exp; }
    ;

/**
 * Trims leading and trailing whitespace from a string expression.
 * Useful for cleaning input data or formatting strings.
 */
trimFn returns [StrExp exp]
    : TRIM '(' strExp ')' { $exp = $strExp.exp.trim(); }
    ;

/**
 * Extracts a substring from the given string expression.
 * The substring starts from a specified index and has a specified length.
 */
substrFn returns [StrExp exp]
    : SUBSTR '(' s=strExp ',' a=integerScalar ',' b=integerScalar ')' { $exp = $s.exp.substr($a.value, $b.value); }
    ;

/// **Special expressions**
///
/// This section defines special expressions which include conditional
/// constructs like `if`-statements, null-checking (`ifNull`), and
/// string splitting operations for granular control of expressions.

/**
 * Parses conditional expressions where an `if` logic is used.
 * Allows branches depending on a boolean condition.
 */
ifExp returns [Exp<?> exp]
    : IF '(' a=boolExp ',' b=expression ',' c=expression ')' { $exp = Exp.ifExp($a.exp, $b.exp, (Exp) $c.exp); }
    ;

/**
 * Parses null-checking expressions where a default value is
 * assigned if the primary expression evaluates to null.
 */
ifNull returns [Exp<?> exp]
    : IF_NULL '(' a=expression ',' b=expression ')' { $exp = Exp.ifNull($a.exp, (Exp) $b.exp); }
    ;

/**
 * Parses splitting of string expressions based on a delimiter.
 * An optional limit can be specified to control the split operation.
 */
split returns [Exp<String[]> exp]
    : SPLIT '(' a=strExp ',' b=strScalar (',' c=integerScalar)? ')' {
        $exp = $ctx.c != null ? $a.exp.split($b.value, $c.value) : $a.exp.split($b.value);
        }
    ;

/// **Aggregate expressions**
///
/// This section defines aggregation expressions including positional aggregates
/// (`first`, `last`), numeric aggregates (`sum`, `avg`, `median`), and other
/// domain-specific aggregators for numeric, string, and temporal data types.

/**
 * Parses aggregate expressions which allow combining or summarizing
 * sets of values into a single scalar output.
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
 * Parses positional aggregate expressions such as `FIRST` and `LAST`,
 * which process elements based on position in the dataset.
 */
positionalAgg returns [Exp<?> exp]
    : FIRST '(' e=expression (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $e.exp.first($b.exp) : $e.exp.first(); }
    | LAST '(' e=expression ')' { $exp = $e.exp.last(); } // TODO: bool condition
    ;

/**
 * Parses numeric aggregate expressions including `SUM`, `MIN`, `MAX`,
 * and more advanced operations like quantiles and cumulative sums.
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
 * Parses aggregation of time expressions, enabling operations like `MIN`,
 * `MAX`, `AVG` (average), and advanced aggregation such as quantiles.
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
 * Parses aggregation of date expressions for operations similar to
 * time-based aggregations, but applied to dates such as `MIN`, `MAX`.
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
 * Handles aggregation of datetime elements, allowing operations like
 * `AVG`, `MEDIAN`, and `QUANTILE` over datetime values in datasets.
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
 * Processes aggregations over string expressions, providing support
 * for finding lexicographical `MIN` or `MAX` values.
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

// *Functions*

//@ doc:inline
IF: 'if';

//@ doc:inline
IF_NULL: 'ifNull';

//@ doc:inline
SPLIT: 'split';

//@ doc:inline
SUBSTR: 'substr';

//@ doc:inline
TRIM: 'trim';

//@ doc:inline
LEN: 'len';

//@ doc:inline
MATCHES: 'matches';

//@ doc:inline
DATE: 'date';

//@ doc:inline
TIME: 'time';

//@ doc:inline
DATETIME: 'datetime';

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
          DECIMAL_LITERAL [lL]
        | HEX_LITERAL [lL]
        | OCTAL_LITERAL [lL]
        | BINARY_LITERAL [lL]
    )
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

CHARACTER_LITERAL: '\'' (~['\\\n\r] | ESCAPE | UNICODE_ESCAPE) '\'';

STRING_LITERAL: '"' (~["\\\n\r] | ESCAPE | UNICODE_ESCAPE)* '"';

//@ doc:inline
IDENTIFIER: LETTER PART_LETTER*;

fragment DECIMAL_LITERAL: [0-9] ([0-9_]* [0-9])?;

fragment HEX_LITERAL: '0' [xX] HEX_DIGITS;

fragment OCTAL_LITERAL: '0' [0-7] ([0-7_]* [0-7])?;

fragment BINARY_LITERAL: '0' [bB] [01] ([01_]* [01])?;

fragment DECIMAL_FLOATING_POINT_LITERAL
    : DECIMAL_LITERAL '.' DECIMAL_LITERAL? DECIMAL_EXPONENT? [fFdD]?
    | '.' DECIMAL_LITERAL DECIMAL_EXPONENT? [fFdD]?
    | DECIMAL_LITERAL DECIMAL_EXPONENT [fFdD]?
    | DECIMAL_LITERAL DECIMAL_EXPONENT?;

fragment DECIMAL_EXPONENT: [eE] [+-]? DECIMAL_LITERAL;

fragment HEXADECIMAL_FLOATING_POINT_LITERAL
    : HEX_LITERAL '.'? HEXADECIMAL_EXPONENT [fFdD]?
    | '0' [xX] HEX_DIGITS? '.' HEX_DIGITS HEXADECIMAL_EXPONENT [fFdD]?;

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
