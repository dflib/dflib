grammar Exp;

@header {
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.BiFunction;

import org.dflib.*;

import static org.dflib.ql.antlr4.ExpParserUtils.*;
}

/// **Parser rules**

/**
 * The root rule of the grammar.
 */
expRoot returns [Exp<?> exp] locals [String alias]
    : expression
    (
       AS identifier { $alias = $identifier.id; }
    )?
    EOF { $exp = $alias == null ? $expression.exp : $expression.exp.as($alias); }
    ;

/**
 * The root rule for the sorting spec
 */
sorterRoot returns [Sorter sorter] locals [boolean desc]
    : expression (
        : ASC
        | DESC { $desc = true; }
    )? EOF { $sorter = $desc ? $expression.exp.desc() : $expression.exp.asc(); }
    ;

/**
 * An expression, which can be of various types including null, aggregate, boolean, numeric, string, temporal, or type-agnostic functions.
 * An expression represents a single value or a combination of values, operators, and functions.
 */
expression returns [Exp<?> exp]
    : boolExp { $exp = $boolExp.exp; }
    | numExp { $exp = $numExp.exp; }
    | strExp { $exp = $strExp.exp; }
    | temporalExp { $exp = $temporalExp.exp; }
    | genericExp { $exp = $genericExp.exp; }
    | aggregateFn { $exp = $aggregateFn.exp; }
    | genericFn { $exp = $genericFn.exp; }
    | NULL { $exp = val(null); }
    | '(' expression ')' { $exp = $expression.exp; }
    ;

/// **Numeric expressions**

/**
 * Numeric expressions, encompassing scalar values, column references, functions,
 * aggregates, and arithmetic operations.
 */
numExp returns [NumExp<?> exp]
    : numScalar { $exp = (NumExp<?>) val($numScalar.value); }
    | numColumn { $exp = $numColumn.exp; }
    | numFn { $exp = $numFn.exp; }
    | numAgg { $exp = $numAgg.exp; }
    | a=numExp op=(MUL | DIV | MOD) b=numExp { $exp = mulDivOrMod($a.exp, $b.exp, $op); }
    | a=numExp op=(ADD | SUB) b=numExp { $exp = addOrSub($a.exp, $b.exp, $op); }
    | '(' numExp ')' { $exp = $numExp.exp; }
    ;

/// **Boolean expressions**

/**
 * Boolean expressions, which evaluate to true or false. These can include
 * boolean scalar values, column references, boolean functions, comparisons (relations),
 * and logical operations (AND, OR, NOT).
 */
boolExp returns [Condition exp]
    : boolScalar { $exp = Exp.\$boolVal($boolScalar.value); }
    | boolColumn { $exp = $boolColumn.exp; }
    | boolFn { $exp = $boolFn.exp; }
    | relation { $exp = $relation.exp; }
    | NOT boolExp { $exp = Exp.not($boolExp.exp); }
    | a=boolExp AND b=boolExp { $exp = Exp.and($a.exp, $b.exp); }
    | a=boolExp OR b=boolExp { $exp = Exp.or($a.exp, $b.exp); }
    | a=boolExp EQ b=boolExp { $exp = $a.exp.eq($b.exp); }
    | a=boolExp NE b=boolExp { $exp = $a.exp.ne($b.exp); }
    | '(' boolExp ')' { $exp = $boolExp.exp; }
    ;

/// **String expressions**

/**
 * String expressions, including string literals, column references, and
 * string manipulation functions.
 */
strExp returns [StrExp exp]
    : strScalar { $exp = Exp.\$strVal($strScalar.value); }
    | strColumn { $exp = $strColumn.exp; }
    | strFn { $exp = $strFn.exp; }
    | '(' strExp ')' { $exp = $strExp.exp; }
    ;

/// **Temporal expressions**

/**
 * Temporal expressions, encompassing time, date, and datetime values.
 */
temporalExp returns [Exp<? extends Temporal> exp]
    : timeExp { $exp = $timeExp.exp; }
    | dateExp { $exp = $dateExp.exp; }
    | dateTimeExp { $exp = $dateTimeExp.exp; }
    | offsetDateTimeExp { $exp = $offsetDateTimeExp.exp; }
    | '(' temporalExp ')' { $exp = $temporalExp.exp; }
    ;

/**
 * Expressions representing a time of day. These may include column references
 * and time functions.
 */
timeExp returns [TimeExp exp]
    : timeColumn { $exp = $timeColumn.exp; }
    | timeFn { $exp = $timeFn.exp; }
    ;

/**
 * Date expressions, which can include references to date columns and date functions.
 */
dateExp returns [DateExp exp]
    : dateColumn { $exp = $dateColumn.exp; }
    | dateFn { $exp = $dateFn.exp; }
    ;

/**
 * Datetime expressions, which can refer to datetime columns and utilize datetime functions.
 */
dateTimeExp returns [DateTimeExp exp]
    : dateTimeColumn { $exp = $dateTimeColumn.exp; }
    | dateTimeFn { $exp = $dateTimeFn.exp; }
    ;

/**
 * Datetime expressions with an offset from UTC+0.
 *
 * This is essential for handling timezones correctly.
 */
offsetDateTimeExp returns [OffsetDateTimeExp exp]
    : offsetDateTimeColumn { $exp = $offsetDateTimeColumn.exp; }
    | offsetDateTimeFn { $exp = $offsetDateTimeFn.exp; }
    ;

/// **Generic expressions**

/**
 *  Expressions with no type specified.
 */
genericExp returns [Exp<?> exp]
    : genericColumn { $exp = $genericColumn.exp; }
    | '(' genericExp ')' { $exp = $genericExp.exp; }
    ;

/// **Scalar expressions**

anyScalar returns [Object value]
    : boolScalar { $value = $boolScalar.value; }
    | numScalar { $value = $numScalar.value; }
    | strScalar { $value = $strScalar.value; }
    ;

/**
 * List of a comma-sperated scalars
 */
anyScalarList returns [Object[] value]
    : '(' values+=anyScalar (',' values+=anyScalar)* ')'{ $value = $values.stream().map(a -> a.value).toArray(); }
    ;

/**
 * Boolean scalar value (true or false).
 */
boolScalar returns [Boolean value]
    : TRUE { $value = true; }
    | FALSE { $value = false; }
    ;

/**
 * Numeric scalar values (literals), which can be integer or floating-point numbers.
 */
numScalar returns [Number value]
    : integerScalar { $value = $integerScalar.value; }
    | floatingPointScalar { $value = $floatingPointScalar.value; }
    ;

/**
 * List of a comma-sperated numeric scalars
 */
numScalarList returns [Number[] value]
    : '(' values+=numScalar (',' values+=numScalar)* ')'{ $value = $values.stream().map(a -> a.value).toArray(Number[]::new); }
    ;

/**
 * An integer scalar value of any size.
 */
integerScalar returns [Number value]
    : INTEGER_LITERAL { $value = parseIntegerValue($text); }
    ;

/**
 * A floating-point scalar value of any scale and precision.
 */
floatingPointScalar returns [Number value]
    : FLOAT_LITERAL { $value = parseFloatingPointValue($text); }
    ;

/**
 * Time string in ISO-8601 compatible format (`hh:mm[:ss[.sss]]`)
 */
timeStrScalar returns [String value]
    // this is just an alias for a string, validation done in runtime
    : strScalar { $value = $strScalar.value; }
    ;

/**
 * Time string in ISO-8601 compatible format (`YYYY-MM-DD`)
 */
dateStrScalar returns [String value]
    // this is just an alias for a string, validation done in runtime
    : strScalar { $value = $strScalar.value; }
    ;

/**
 * Time string in ISO-8601 compatible format (`<date>T<time>`)
 */
dateTimeStrScalar returns [String value]
    // this is just an alias for a string, validation done in runtime
    : strScalar { $value = $strScalar.value; }
    ;

/**
 * A dateTime with offset string in ISO-8601 compatible format  (`<date>T<time>Z`)
 */
offsetDateTimeStrScalar returns [String value]
    // this is just an alias for a string, validation done in runtime
    : strScalar { $value = $strScalar.value; }
    ;

/**
 * A string literal.
 */
strScalar returns [String value]
    : STRING_LITERAL { $value = unescapeString($text.substring(1, $text.length() - 1)); }
    ;

/**
 * List of a comma-sperated string literals
 */
strScalarList returns [String[] value]
    : '(' values+=strScalar (',' values+=strScalar)* ')'{ $value = $values.stream().map(a -> a.value).toArray(String[]::new); }
    ;

/// **Column expressions**

/**
 * An expression referencing a numeric column.  Supports various numeric types.
 */
numColumn returns [NumExp<?> exp]
    : intColumn { $exp = $intColumn.exp; }
    | longColumn { $exp = $longColumn.exp; }
    | bigintColumn { $exp = $bigintColumn.exp; }
    | floatColumn { $exp = $floatColumn.exp; }
    | doubleColumn { $exp = $doubleColumn.exp; }
    | decimalColumn { $exp = $decimalColumn.exp; }
    ;

/**
 * An expression referencing a column of integer values.
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name).
 */
intColumn returns [NumExp<Integer> exp]
    : INT '(' columnId ')' { $exp = intCol($columnId.id); }
    ;

/**
 * An expression referencing a column containing long integer values.
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name).
 */
longColumn returns [NumExp<Long> exp]
    : LONG '(' columnId ')' { $exp = longCol($columnId.id); }
    ;

/**
 * An expression referencing a column containing BigInteger values.
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name).
 */
bigintColumn returns [NumExp<BigInteger> exp]
    : BIGINT '(' columnId ')' { $exp = bigintCol($columnId.id); }
    ;

/**
 * An expression that references a column of float values.
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name).
 */
floatColumn returns [NumExp<Float> exp]
    : FLOAT '(' columnId ')' { $exp = floatCol($columnId.id); }
    ;

/**
 * An expression referencing a column containing double-precision floating-point numbers.
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name).
 */
doubleColumn returns [NumExp<Double> exp]
    : DOUBLE '(' columnId ')' { $exp = doubleCol($columnId.id); }
    ;

/**
 * An expression referencing a column of Decimal values (for high-precision arithmetic).
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name)n.
 */
decimalColumn returns [DecimalExp exp]
    : DECIMAL '(' columnId ')' { $exp = decimalCol($columnId.id); }
    ;

/**
 * An expression that accesses a column of boolean values.
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name).
 */
boolColumn returns [Condition exp]
    : BOOL '(' columnId ')' { $exp = boolCol($columnId.id); }
    ;

/**
 * An expression referring to a column containing string values.
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name).
 */
strColumn returns [StrExp exp]
    : STR '(' columnId ')' { $exp = strCol($columnId.id); }
    ;

/**
 * An expression referencing a column containing Date values.
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name).
 */
dateColumn returns [DateExp exp]
    : DATE '(' columnId ')' { $exp = dateCol($columnId.id); }
    ;

/**
 * An expression that refers to a column containing Time values.
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name).
 */
timeColumn returns [TimeExp exp]
    : TIME '(' columnId ')' { $exp = timeCol($columnId.id); }
    ;

/**
 * An expression referencing a column storing DateTime values.
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name).
 */
dateTimeColumn returns [DateTimeExp exp]
    : DATETIME '(' columnId ')' { $exp = dateTimeCol($columnId.id); }
    ;

/**
 * An expression referencing a column containing OffsetDateTime values (datetime with timezone offset).
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name).
 */
offsetDateTimeColumn returns [OffsetDateTimeExp exp]
    : OFFSET_DATETIME '(' columnId ')' { $exp = offsetCol($columnId.id); }
    ;

/**
 * An expression referencing a column with a non-specified type.
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name).
 */
genericColumn returns [Exp<?> exp]
    : COL '(' columnId ')' { $exp = col($columnId.id); }
    | identifier { $exp = Exp.\$col($identifier.id); }
    ;

/**
 * A column identifier, which can be an integer representing the column index or a string representing the column name.
 */
columnId returns [Object id]
    : integerScalar { $id = $integerScalar.value; }
    | identifier { $id = $identifier.id; }
    ;

/**
 * An identifier, which is a sequence of letters and digits starting with a letter.
 */
//@ doc:inline
identifier returns [String id]
    : IDENTIFIER { $id = $text; }
    | QUOTED_IDENTIFIER { $id = unescapeIdentifier($text.substring(1, $text.length() - 1)); }
    | fnName { $id = $fnName.id; }
    ;

/// **Relational expressions**

/**
 * Relational expressions, which can include numeric, string, time, date, and datetime relations.
 * These expressions compare two values using operators like >, <, =, !=, etc.
 */
relation returns [Condition exp]
    : numRelation { $exp = $numRelation.exp; }
    | strRelation { $exp = $strRelation.exp; }
    | timeRelation { $exp = $timeRelation.exp; }
    | dateRelation { $exp = $dateRelation.exp; }
    | dateTimeRelation { $exp = $dateTimeRelation.exp; }
    | offsetDateTimeRelation { $exp = $offsetDateTimeRelation.exp; }
    | genericRelation { $exp = $genericRelation.exp; }
    | '(' relation ')' { $exp = $relation.exp; }
    ;

/**
 * A numeric relational expression. This compares two numeric expressions
 * using comparison operators (>, >=, <, <=, =, !=, BETWEEN).
 *
 * Parameters:
 *  - The left-hand side numeric expression.
 *  - The right-hand side numeric expression.
 *  - The upper bound numeric expression (for BETWEEN).
 */
numRelation returns [Condition exp] locals [BiFunction<NumExp<?>, NumExp<?>, Condition> rel]
    : a=numExp (
        : (
            : GT { $rel = (a, b) -> a.gt(b); }
            | GE { $rel = (a, b) -> a.ge(b); }
            | LT { $rel = (a, b) -> a.lt(b); }
            | LE { $rel = (a, b) -> a.le(b); }
            | EQ { $rel = (a, b) -> a.eq(b); }
            | NE { $rel = (a, b) -> a.ne(b); }
        ) b=numExp { $exp = $rel.apply($a.exp, $b.exp); }
        | BETWEEN b=numExp AND c=numExp { $exp = $a.exp.between($b.exp, $c.exp); }
        | IN l=numScalarList { $exp = $a.exp.in($l.value); }
        | NOT IN l=numScalarList { $exp = $a.exp.notIn($l.value); }
    )
    ;

/**
 * String relational expressions. Compares two string expressions using either
 * equality (=) or inequality (!=).
 *
 * Parameters:
 *  - The left-hand side string expression.
 *  - The right-hand side string expression.
 */
strRelation returns [Condition exp] locals [BiFunction<StrExp, StrExp, Condition> rel]
    : a=strExp (
        : (
            : EQ { $rel = (a, b) -> a.eq(b); }
            | NE { $rel = (a, b) -> a.ne(b); }
        ) b=strExp { $exp = $rel.apply($a.exp, $b.exp); }
        | IN l=strScalarList { $exp = $a.exp.in($l.value); }
        | NOT IN l=strScalarList { $exp = $a.exp.notIn($l.value); }
    )
    ;

/**
 * Time relational expressions. Compares two time expressions using comparison operators.
 *
 * Parameters:
 *  - The left-hand TimeExp.
 *  - The right-hand TimeExp.
 *  - The upper bound TimeExp (for BETWEEN).
 */
timeRelation returns [Condition exp] locals [BiFunction<TimeExp, TimeExp, Condition> rel]
    : a=timeExp (
        : (
            : GT { $rel = (a, b) -> a.gt(b); }
            | GE { $rel = (a, b) -> a.ge(b); }
            | LT { $rel = (a, b) -> a.lt(b); }
            | LE { $rel = (a, b) -> a.le(b); }
            | EQ { $rel = (a, b) -> a.eq(b); }
            | NE { $rel = (a, b) -> a.ne(b); }
        ) (
            : b=timeExp { $exp = $rel.apply($a.exp, $b.exp); }
            | s=timeStrScalar { $exp = $rel.apply($a.exp, Exp.\$timeVal(LocalTime.parse($s.value))); }
        )
        | BETWEEN (
            b=timeExp AND c=timeExp { $exp = $a.exp.between($b.exp, $c.exp); }
            | s1=timeStrScalar AND s2=timeStrScalar { $exp = $a.exp.between($s1.value, $s2.value); }
        )
        | IN l=strScalarList { $exp = $a.exp.in(Arrays.stream($l.value).map(LocalTime::parse).toArray(LocalTime[]::new)); }
        | NOT IN l=strScalarList { $exp = $a.exp.notIn(Arrays.stream($l.value).map(LocalTime::parse).toArray(LocalTime[]::new)); }
    )
    ;

/**
 * Date relational expressions, comparing two DateExps.
 *
 * Parameters:
 *  - The left-hand DateExp.
 *  - The right-hand DateExp.
 *  - The upper bound DateExp (for BETWEEN).
 */
dateRelation returns [Condition exp] locals [BiFunction<DateExp, DateExp, Condition> rel]
    : a=dateExp (
        : (
            : GT { $rel = (a, b) -> a.gt(b); }
            | GE { $rel = (a, b) -> a.ge(b); }
            | LT { $rel = (a, b) -> a.lt(b); }
            | LE { $rel = (a, b) -> a.le(b); }
            | EQ { $rel = (a, b) -> a.eq(b); }
            | NE { $rel = (a, b) -> a.ne(b); }
        ) (
            : b=dateExp { $exp = $rel.apply($a.exp, $b.exp); }
            | s=dateStrScalar { $exp = $rel.apply($a.exp, Exp.\$dateVal(LocalDate.parse($s.value))); }
        )
        | BETWEEN (
            b=dateExp AND c=dateExp { $exp = $a.exp.between($b.exp, $c.exp); }
            | s1=dateStrScalar AND s2=dateStrScalar { $exp = $a.exp.between($s1.value, $s2.value); }
        )
        | IN l=strScalarList { $exp = $a.exp.in(Arrays.stream($l.value).map(LocalDate::parse).toArray(LocalDate[]::new)); }
        | NOT IN l=strScalarList { $exp = $a.exp.notIn(Arrays.stream($l.value).map(LocalDate::parse).toArray(LocalDate[]::new)); }
    )
    ;

/**
 * A datetime relational expression. Compares two DateTimeExp values.
 *
 * Parameters:
 *  - The left-hand DateTimeExp.
 *  - The right-hand DateTimeExp.
 *  - The upper bound DateTimeExp (for BETWEEN).
 */
dateTimeRelation returns [Condition exp] locals [BiFunction<DateTimeExp, DateTimeExp, Condition> rel]
    : a=dateTimeExp (
        : (
            : GT { $rel = (a, b) -> a.gt(b); }
            | GE { $rel = (a, b) -> a.ge(b); }
            | LT { $rel = (a, b) -> a.lt(b); }
            | LE { $rel = (a, b) -> a.le(b); }
            | EQ { $rel = (a, b) -> a.eq(b); }
            | NE { $rel = (a, b) -> a.ne(b); }
        ) (
            : b=dateTimeExp { $exp = $rel.apply($a.exp, $b.exp); }
            | s=dateTimeStrScalar { $exp = $rel.apply($a.exp, Exp.\$dateTimeVal(LocalDateTime.parse($s.value))); }
        )
        | BETWEEN (
            b=dateTimeExp AND c=dateTimeExp { $exp = $a.exp.between($b.exp, $c.exp); }
            | s1=dateTimeStrScalar AND s2=dateTimeStrScalar { $exp = $a.exp.between($s1.value, $s2.value); }
        )
        | IN l=strScalarList { $exp = $a.exp.in(Arrays.stream($l.value).map(LocalDateTime::parse).toArray(LocalDateTime[]::new)); }
        | NOT IN l=strScalarList { $exp = $a.exp.notIn(Arrays.stream($l.value).map(LocalDateTime::parse).toArray(LocalDateTime[]::new)); }
    )
    ;

/**
 * An OffsetDateTime relational expression. Compares two OffsetDateTimeExp values.
 *
 * Parameters:
 *  - The left-hand OffsetDateTimeExp.
 *  - The right-hand OffsetDateTimeExp.
 *  - The upper bound OffsetDateTimeExp (for BETWEEN).
 */
offsetDateTimeRelation returns [Condition exp] locals [BiFunction<OffsetDateTimeExp, OffsetDateTimeExp, Condition> rel]
    : a=offsetDateTimeExp (
        : (
            : GT { $rel = (a, b) -> a.gt(b); }
            | GE { $rel = (a, b) -> a.ge(b); }
            | LT { $rel = (a, b) -> a.lt(b); }
            | LE { $rel = (a, b) -> a.le(b); }
            | EQ { $rel = (a, b) -> a.eq(b); }
            | NE { $rel = (a, b) -> a.ne(b); }
        ) (
            : b=offsetDateTimeExp { $exp = $rel.apply($a.exp, $b.exp); }
            | s=offsetDateTimeStrScalar { $exp = $rel.apply($a.exp, Exp.\$offsetDateTimeVal(OffsetDateTime.parse($s.value))); }
        )
        | BETWEEN (
            : b=offsetDateTimeExp AND c=offsetDateTimeExp { $exp = $a.exp.between($b.exp, $c.exp); }
            | s1=offsetDateTimeStrScalar AND s2=offsetDateTimeStrScalar { $exp = $a.exp.between($s1.value, $s2.value); }
        )
        | IN l=strScalarList { $exp = $a.exp.in(Arrays.stream($l.value).map(OffsetDateTime::parse).toArray(OffsetDateTime[]::new)); }
        | NOT IN l=strScalarList { $exp = $a.exp.notIn(Arrays.stream($l.value).map(OffsetDateTime::parse).toArray(OffsetDateTime[]::new)); }
    )
    ;

/**
 * A generic relational expression. Compares two expressions.
 *
 * Parameters:
 *  - The left-hand expression.
 *  - The right-hand expression.
 */
genericRelation returns [Condition exp] locals [BiFunction<Exp<?>, Exp<?>, Condition> rel]
    : a=genericExp (
        :(
            : EQ { $rel = (a, b) -> a.eq(b); }
            | NE { $rel = (a, b) -> a.ne(b); }
        ) b=expression { $exp = $rel.apply($a.exp, $b.exp); }
        | IN l=anyScalarList { $exp = $a.exp.in($l.value); }
        | NOT IN l=anyScalarList { $exp = $a.exp.notIn($l.value); }
    )
    ;

/// **Functions**

/**
 * Numeric functions, including casting, counting, row number, absolute value, rounding, and field functions.
 * These functions operate on or produce numeric values.
 */
numFn returns [NumExp<?> exp] locals [Function<NumExp<?>, NumExp<?>> fn]
    : castAsInt { $exp = $castAsInt.exp; }
    | castAsLong { $exp = $castAsLong.exp; }
    | castAsBigint { $exp = $castAsBigint.exp; }
    | castAsFloat { $exp = $castAsFloat.exp; }
    | castAsDouble { $exp = $castAsDouble.exp; }
    | castAsDecimal { $exp = $castAsDecimal.exp; }
    | timeFieldFn { $exp = $timeFieldFn.exp; }
    | dateFieldFn { $exp = $dateFieldFn.exp; }
    | dateTimeFieldFn { $exp = $dateTimeFieldFn.exp; }
    | offsetDateTimeFieldFn { $exp = $offsetDateTimeFieldFn.exp; }
    // TODO: check out COUNT and ROW_NUM functions
    | COUNT ('(' b=boolExp? ')') { $exp = $ctx.b != null ? Exp.count($b.exp) : Exp.count(); }
    | ROW_NUM ('(' ')') { $exp = Exp.rowNum(); }
    | (
        : ABS { $fn = e -> e.abs(); }
        | ROUND { $fn = e -> e.round(); }
    ) '(' e=numExp ')' { $exp = $fn.apply($e.exp); }
    | SCALE '(' e=numExp ',' s=integerScalar ')' { $exp = $e.exp.castAsDecimal().scale( $s.value.intValue() ); }
    ;

/**
 * Time field functions.
 *
 * Supports extracting fields like hour, minute, second and millisecond from Time expressions.
 *
 * Parameters:
 *  - The TimeExp from which to extract the field.
 */
timeFieldFn returns [NumExp<Integer> exp] locals [Function<TimeExp, NumExp<Integer>> fn]
     : (
         : HOUR { $fn = e -> e.hour(); }
         | MINUTE { $fn = e -> e.minute(); }
         | SECOND { $fn = e -> e.second(); }
         | MILLISECOND { $fn = e -> e.millisecond(); }
     ) '(' e=timeExp ')' { $exp = $fn.apply($e.exp); }
     ;

/**
 * Date field functions.
 *
 * Supports extracting fields like year, month, and day from Date expressions.
 *
 * Parameters:
 *  - The DateExp from which to extract the field.
 */
dateFieldFn returns [NumExp<Integer> exp] locals [Function<DateExp, NumExp<Integer>> fn]
     : (
         : YEAR { $fn = e -> e.year(); }
         | MONTH { $fn = e -> e.month(); }
         | DAY { $fn = e -> e.day(); }
     ) '(' e=dateExp ')' { $exp = $fn.apply($e.exp); }
     ;

/**
 * Datetime field functions.
 *
 * Supports extracting fields like year, month, day, hour, minute, second,
 * and millisecond from DateTime expressions.
 *
 * Parameters:
 *  - The DateTimeExp from which to extract the field.
 */
dateTimeFieldFn returns [NumExp<Integer> exp] locals [Function<DateTimeExp, NumExp<Integer>> fn]
     : (
         : YEAR { $fn = e -> e.year(); }
         | MONTH { $fn = e -> e.month(); }
         | DAY { $fn = e -> e.day(); }
         | HOUR { $fn = e -> e.hour(); }
         | MINUTE { $fn = e -> e.minute(); }
         | SECOND { $fn = e -> e.second(); }
         | MILLISECOND { $fn = e -> e.millisecond(); }
     ) '(' e=dateTimeExp ')' { $exp = $fn.apply($e.exp); }
     ;

/**
 * OffsetDateTime field functions.
 *
 * Supports extracting fields like year, month, day, hour, minute, second,
 * and millisecond from OffsetDateTime expressions.
 *
 * Parameters:
 *  - The OffsetDateTimeExp from which to extract the field.
 */
offsetDateTimeFieldFn returns [NumExp<Integer> exp] locals [Function<OffsetDateTimeExp, NumExp<Integer>> fn]
    : (
        : YEAR { $fn = e -> e.year(); }
        | MONTH { $fn = e -> e.month(); }
        | DAY { $fn = e -> e.day(); }
        | HOUR { $fn = e -> e.hour(); }
        | MINUTE { $fn = e -> e.minute(); }
        | SECOND { $fn = e -> e.second(); }
        | MILLISECOND { $fn = e -> e.millisecond(); }
    ) '(' e=offsetDateTimeExp ')' { $exp = $fn.apply($e.exp); }
    ;

/**
 * Boolean functions, which can include casting, matches, starts with, ends with, and contains.
 * These functions produce boolean results.
 *
 * Parameters:
 *  - The input expression(s) for the boolean function. The types and number of parameters depend on the specific function.
 *    For example, `matches` takes a string expression and a string literal, while `castAsBool` takes a single expression of any type.
 */
boolFn returns [Condition exp] locals [BiFunction<StrExp, String, Condition> fn]
    : castAsBool { $exp = $castAsBool.exp; }
    | (
        : MATCHES { $fn = (a, b) -> a.matches(b); }
        | STARTS_WITH { $fn = (a, b) -> a.startsWith(b); }
        | ENDS_WITH { $fn = (a, b) -> a.endsWith(b); }
        | CONTAINS { $fn = (a, b) -> a.contains(b); }
    ) '(' a=strExp ',' b=strScalar ')' { $exp = $fn.apply($a.exp, $b.value); }
    ;


/**
 * Time functions, including casting and arithmetic operations. These
 * functions operate on time values.
 *
 * Parameters:
 *  - The base TimeExp.
 *  - An integer representing the value to add (e.g., hours, minutes).
 */
timeFn returns [TimeExp exp] locals [BiFunction<TimeExp, Integer, TimeExp> fn]
    : castAsTime { $exp = $castAsTime.exp; }
    | (
        : PLUS_HOURS { $fn = (a, b) -> a.plusHours(b); }
        | PLUS_MINUTES { $fn = (a, b) -> a.plusMinutes(b); }
        | PLUS_SECONDS { $fn = (a, b) -> a.plusSeconds(b); }
        | PLUS_MILLISECONDS { $fn = (a, b) -> a.plusMilliseconds(b); }
        | PLUS_NANOS { $fn = (a, b) -> a.plusNanos(b); }
    ) '(' a=timeExp ',' b=integerScalar ')' { $exp = $fn.apply($a.exp, $b.value.intValue()); }
    ;

/**
 * Date functions, enabling casting and arithmetic operations on date values.
 *
 * Parameters:
 *  - The base DateExp.
 *  - An integer representing the value to add (e.g., years, months).
 */
dateFn returns [DateExp exp] locals [BiFunction<DateExp, Integer, DateExp> fn]
    : castAsDate { $exp = $castAsDate.exp; }
    | (
        : PLUS_YEARS { $fn = (a, b) -> a.plusYears(b); }
        | PLUS_MONTHS { $fn = (a, b) -> a.plusMonths(b); }
        | PLUS_WEEKS { $fn = (a, b) -> a.plusWeeks(b); }
        | PLUS_DAYS { $fn = (a, b) -> a.plusDays(b); }
    ) '(' a=dateExp ',' b=integerScalar ')' { $exp = $fn.apply($a.exp, $b.value.intValue()); }
    ;

/**
 * Datetime functions, including casting and arithmetic operations for datetime values.
 *
 * Parameters:
 *  - The base DateTimeExp.
 *  - An integer representing the value to add (e.g., years, hours).
 */
dateTimeFn returns [DateTimeExp exp] locals [BiFunction<DateTimeExp, Integer, DateTimeExp> fn]
    : castAsDateTime { $exp = $castAsDateTime.exp; }
    | (
        : PLUS_YEARS { $fn = (a, b) -> a.plusYears(b); }
        | PLUS_MONTHS { $fn = (a, b) -> a.plusMonths(b); }
        | PLUS_WEEKS { $fn = (a, b) -> a.plusWeeks(b); }
        | PLUS_DAYS { $fn = (a, b) -> a.plusDays(b); }
        | PLUS_HOURS { $fn = (a, b) -> a.plusHours(b); }
        | PLUS_MINUTES { $fn = (a, b) -> a.plusMinutes(b); }
        | PLUS_SECONDS { $fn = (a, b) -> a.plusSeconds(b); }
        | PLUS_MILLISECONDS { $fn = (a, b) -> a.plusMilliseconds(b); }
        | PLUS_NANOS { $fn = (a, b) -> a.plusNanos(b); }
    ) '(' a=dateTimeExp ',' b=integerScalar ')' { $exp = $fn.apply($a.exp, $b.value.intValue()); }
    ;

/**
 * Datetime functions with an offset from UTC/Greenwich.  Supports casting and
 * arithmetic operations on timezone-aware datetime values.
 *
 * Parameters:
 *  - The base OffsetDateTimeExp.
 *  - An integer representing the value to add (e.g., years, hours).
 */
offsetDateTimeFn returns [OffsetDateTimeExp exp] locals [BiFunction<OffsetDateTimeExp, Integer, OffsetDateTimeExp> fn]
    : castAsOffsetDateTime { $exp = $castAsOffsetDateTime.exp; }
    | (
        : PLUS_YEARS { $fn = (a, b) -> a.plusYears(b); }
        | PLUS_MONTHS { $fn = (a, b) -> a.plusMonths(b); }
        | PLUS_WEEKS { $fn = (a, b) -> a.plusWeeks(b); }
        | PLUS_DAYS { $fn = (a, b) -> a.plusDays(b); }
        | PLUS_HOURS { $fn = (a, b) -> a.plusHours(b); }
        | PLUS_MINUTES { $fn = (a, b) -> a.plusMinutes(b); }
        | PLUS_SECONDS { $fn = (a, b) -> a.plusSeconds(b); }
        | PLUS_MILLISECONDS { $fn = (a, b) -> a.plusMilliseconds(b); }
        | PLUS_NANOS { $fn = (a, b) -> a.plusNanos(b); }
    ) '(' a=offsetDateTimeExp ',' b=integerScalar ')' { $exp = $fn.apply($a.exp, $b.value.intValue()); }
    ;

/**
 * String functions, covering casting, trimming, substrings, and concatenation.
 *
 * Parameters:
 *  - The input StrExp (for functions like SUBSTR, TRIM).
 *  - The starting position (an integer, for SUBSTR).
 *  - The length (an integer, optional for SUBSTR).
 *  - A variable number of StrExp arguments (for CONCAT).
 */
strFn returns [StrExp exp]
    : castAsStr { $exp = $castAsStr.exp; }
    | TRIM '(' strExp ')' { $exp = $strExp.exp.trim(); }
    | SUBSTR '(' s=strExp ',' a=integerScalar (',' b=integerScalar)? ')' {
        $exp = $ctx.b != null ? $s.exp.substr($a.value.intValue(), $b.value.intValue()) : $s.exp.substr($a.value.intValue());
    }
    | CONCAT ('(' (args+=expression (',' args+=expression)*)? ')') {
        $exp = !$args.isEmpty() ? Exp.concat($args.stream().map(a -> a.exp).toArray()) : Exp.concat();
    }
    ;

/// **Cast functions**

/**
 * The cast-to-boolean function, converting an expression to a boolean value.
 *
 * Parameters:
 *  - The expression to be cast.
 */
castAsBool returns [Condition exp]
    : CAST_AS_BOOL '(' expression ')' { $exp = $expression.exp.castAsBool(); }
    ;

/**
 * The cast-to-integer function, converting an expression to an integer.
 *
 * Parameters:
 *  - The expression to be cast.
 */
castAsInt returns [NumExp<Integer> exp]
    : CAST_AS_INT '(' expression ')' { $exp = $expression.exp.castAsInt(); }
    ;

/**
 * Casts an expression to a long integer.
 *
 * Parameters:
 *  - The expression to be cast.
 */
castAsLong returns [NumExp<Long> exp]
    : CAST_AS_LONG '(' expression ')' { $exp = $expression.exp.castAsLong(); }
    ;

/**
 * Casts an expression to a BigInteger value.
 *
 * Parameters:
 *  - The expression to be cast.
 */
castAsBigint returns [NumExp<BigInteger> exp]
    : CAST_AS_BIGINT '(' expression ')' { $exp = $expression.exp.castAsBigint(); }
    ;

/**
 * Casts an expression to a floating-point value (float).
 *
 * Parameters:
 *  - The expression to be cast.
 */
castAsFloat returns [NumExp<Float> exp]
    : CAST_AS_FLOAT '(' expression ')' { $exp = $expression.exp.castAsFloat(); }
    ;

/**
 * Casts an expression to a double-precision floating-point value.
 *
 * Parameters:
 *  - The expression to be cast.
 */
castAsDouble returns [NumExp<Double> exp]
    : CAST_AS_DOUBLE '(' expression ')' { $exp = $expression.exp.castAsDouble(); }
    ;

/**
 * Casts an expression to a Decimal value. Decimals are used for high-precision arithmetic.
 *
 * Parameters:
 *  - The expression to be cast.
 */
castAsDecimal returns [DecimalExp exp]
    : CAST_AS_DECIMAL '(' expression ')' { $exp = $expression.exp.castAsDecimal(); }
    ;

/**
 * Casts an expression to a string.
 *
 * Parameters:
 *  - The expression to be cast.
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
 */
castAsDateTime returns [DateTimeExp exp]
    : CAST_AS_DATETIME '(' e=expression (',' f=strScalar )? ')' {
        $exp = $ctx.f != null ? $e.exp.castAsDateTime($f.value) : $e.exp.castAsDateTime();
    }
    ;

/**
 * A cast operation to convert an expression into an OffsetDateTime.
 *
 * Parameters:
 *  - The expression to be cast.
 *  - A format string specifying how to interpret the OffsetDateTime (optional).
 */
castAsOffsetDateTime returns [OffsetDateTimeExp exp]
    : CAST_AS_OFFSET_DATETIME '(' e=expression (',' f=strScalar )? ')' {
        $exp = $ctx.f != null ? $e.exp.castAsOffsetDateTime($f.value) : $e.exp.castAsOffsetDateTime();
    }
    ;

/// **Special functions**

/**
 * Special functions that provide control flow, data manipulation, or other type-agnostic operations.
 */
genericFn returns [Exp<?> exp]
    : ifExp { $exp = $ifExp.exp; }
    | ifNull { $exp = $ifNull.exp; }
    | split { $exp = $split.exp; }
    | shift { $exp = $shift.exp; }
    ;

/**
 * An IF expression, a conditional expression that returns one of two values based on a condition.
 *
 * Parameters:
 *  - The boolean condition.
 *  - The expression to return if the condition is true.
 *  - The expression to return if the condition is false.
 */
ifExp returns [Exp<?> exp]
    : IF '(' condition=boolExp ',' trueExp=expression ',' elseExpression=expression ')' {
        $exp = Exp.ifExp($condition.exp, (Exp)$trueExp.exp, (Exp)$elseExpression.exp);
    }
    ;

/**
 * An IF_NULL expression. This function returns the first expression if it is not null;
 * otherwise, it returns the second expression.
 *
 * Parameters:
 *  - The expression to check for null.
 *  - The expression to return if the first expression is null. Must be the same type as the first expression.
 */
ifNull returns [Exp<?> exp]
    : IF_NULL '(' nullableExp ',' expression ')' { $exp = ifNullExp($nullableExp.exp, $expression.exp); }
    ;

//@ doc:no-diagram
//@ doc:nodoc
//@ doc:name nullable expression
nullableExp returns [Exp<?> exp]
    : expression { $exp = $expression.exp; }
    ;

/**
 * A SPLIT expression, which splits a string into an array of substrings based on a delimiter.
 *
 * Parameters:
 *  - The string expression to split.
 *  - The delimiter string to split the string by.
 *  - An integer that limits the maximum number of substrings (optional).
 */
split returns [Exp<String[]> exp]
    : SPLIT '(' a=strExp ',' b=strScalar (',' c=integerScalar)? ')' {
        $exp = $ctx.c != null ? $a.exp.split($b.value, $c.value.intValue()) : $a.exp.split($b.value);
    }
    ;

/**
 * A SHIFT expression, shifting values in a sequence forward or backward.
 * Head or tail gaps produced by the shift are filled with the provided filler value or null.
 *
 * Parameters:
 *  - The expression to shift.
 *  - The integer shift amount (positive for forward, negative for backward).
 *  - The default value to use for positions that become empty after shifting (optional).
 */
shift returns [Exp<?> exp]
    : SHIFT '(' (
        be=boolExp ',' i=integerScalar (',' bs=boolScalar)? {
            $exp = $ctx.bs != null ? $be.exp.shift($i.value.intValue(), $bs.value) : $be.exp.shift($i.value.intValue());
        }
        | ne=numExp ',' i=integerScalar (',' ns=numScalar)? {
            $exp = $ctx.ns != null ? ((NumExp<Number>) $ne.exp).shift($i.value.intValue(), (Number) $ns.value) : $ne.exp.shift($i.value.intValue());
        }
        | se=strExp ',' i=integerScalar (',' ss=strScalar)? {
            $exp = $ctx.ss != null ? $se.exp.shift($i.value.intValue(), $ss.value) : $se.exp.shift($i.value.intValue());
        }
        | ge=genericShiftExp ',' i=integerScalar (',' s=anyScalar)? {
            $exp = $ctx.ss != null ? ((Exp)$ge.exp).shift($i.value.intValue(), (Object)$s.value) : $ge.exp.shift($i.value.intValue());
        }
    ) ')'
    ;

//@ doc:inline
genericShiftExp returns [Exp<?> exp]
    : genericExp { $exp = $genericExp.exp; }
    | aggregateFn { $exp = $aggregateFn.exp; }
    | genericFn { $exp = $genericFn.exp; }
    // TODO: temporal exp here, or a special case in the shift rule
    ;

/// **Aggregate expressions**

/**
 * Aggregate expressions, such as MIN, MAX, SUM, AVG, etc.
 * Aggregates perform calculations across multiple rows of data.
 */
aggregateFn returns [Exp<?> exp]
    : genericAgg { $exp = $genericAgg.exp; }
    | numAgg { $exp = $numAgg.exp; }
    | timeAgg { $exp = $timeAgg.exp; }
    | dateAgg { $exp = $dateAgg.exp; }
    | dateTimeAgg { $exp = $dateTimeAgg.exp; }
    | strAgg { $exp = $strAgg.exp; }
    ;

/**
 * Generic aggregate functions that could be used with any expression type.
 */
genericAgg returns [Exp<?> exp]
    : positionalAgg { $exp = $positionalAgg.exp; }
    | vConcat { $exp = $vConcat.exp; }
    | list { $exp = $list.exp; }
    | set { $exp = $set.exp; }
    | array { $exp = $array.exp; }
    ;

/**
 * Positional aggregate expressions, like FIRST and LAST. These functions
 * return the first or last value encountered in a sequence.
 *
 * Parameters:
 *  - The expression from which to get the first or last value.
 *  - A boolean expression to filter the data before finding the first element (optional).
 */
positionalAgg returns [Exp<?> exp]
    : FIRST '(' e=expression (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $e.exp.first($b.exp) : $e.exp.first(); }
    | LAST '(' e=expression ')' { $exp = $e.exp.last(); } // TODO: bool condition
    ;

/**
 * Creates an aggregating expression whose "reduce" operation returns a String of concatenated filtered Series values
 * separated by the delimiter preceded by the prefix and followed by the suffix.
 *
 * Parameters:
 *  - The expression to reduce
 *  - [optional] filter condition
 *  - delimiter to join values with
 *  - [optional] prefix to add
 *  - [optional] suffix to add
 */
vConcat returns [Exp<?> exp]
    : VCONCAT '(' e=expression (',' c=boolExp)? ',' d=strScalar (',' p=strScalar ',' s=strScalar)? ')' {
        $exp = $e.exp.vConcat(
            $ctx.c == null ? null : $ctx.c.exp,
            $d.value,
            $ctx.p == null ? "" : $ctx.p.value,
            $ctx.s == null ? "" : $ctx.s.value
        );
    }
    ;

/**
 * Creates an aggregating expression whose "reduce" operation returns a List containing all Series values.
 */
list returns [Exp<?> exp]
    : LIST '(' e=expression ')' { $exp = $e.exp.list(); }
    ;

/**
 * Creates an aggregating expression whose "reduce" operation returns a Set containing all Series values.
 */
set returns [Exp<?> exp]
    : SET '(' e=expression ')' { $exp = $e.exp.set(); }
    ;

/**
 * Creates an aggregating expression whose "reduce" operation returns an array containing all Series values.
 * Array component type should be provided as a fully quolified class name (e.g. 'java.lang.String').
 */
array returns [Exp<?> exp]
    : ARRAY '(' e=expression ',' t=strScalar ')' { $exp = ExpParserUtils.array($e.exp, $t.value); }
    ;

/**
 * Numeric aggregate expressions, calculating aggregates like MIN, MAX, SUM, AVG, MEDIAN, and QUANTILE.
 *
 * Parameters:
 *  - The numeric column expression to aggregate.
 *  - A boolean expression to filter the rows involved in the aggregation (optional).
 *  - The quantile value to compute (between 0 and 1, for QUANTILE).
 */
numAgg returns [NumExp<?> exp] locals [BiFunction<NumExp, Condition, NumExp> aggFn]
    : (
        : MIN { $aggFn = (c, b) -> c.min(b); }
        | MAX { $aggFn = (c, b) -> c.max(b); }
        | SUM { $aggFn = (c, b) -> c.sum(b); }
        | AVG { $aggFn = (c, b) -> c.avg(b); }
        | MEDIAN { $aggFn = (c, b) -> c.median(b); }
    ) '(' c=numExp (',' b=boolExp)? ')' { $exp = $aggFn.apply($c.exp, $ctx.b != null ? $b.exp: null); }
    | CUMSUM '(' c=numExp ')' { $exp = $c.exp.cumSum(); } // Cumulative Sum, no filter currently supported
    | QUANTILE '(' c=numExp ',' q=numScalar (',' b=boolExp)? ')' {
        $exp = $ctx.b != null
            ? $c.exp.quantile($q.value.doubleValue(), $b.exp)
            : $c.exp.quantile($q.value.doubleValue());
    }
    ;

/**
 * Time aggregate expressions, performing operations like MIN, MAX, AVG, MEDIAN and QUANTILE on time values.
 *
 * Parameters:
 *  - The time column expression to aggregate.
 *  - A boolean expression to filter the rows involved in the aggregation (optional).
 *  - The quantile value to compute (between 0 and 1, for QUANTILE).
 */
timeAgg returns [TimeExp exp] locals [BiFunction<TimeExp, Condition, TimeExp> aggFn]
    : (
        : MIN { $aggFn = (c, b) -> c.min(b); }
        | MAX { $aggFn = (c, b) -> c.max(b); }
        | AVG { $aggFn = (c, b) -> c.avg(b); }
        | MEDIAN { $aggFn = (c, b) -> c.median(b); }
    ) '(' c=timeExp (',' b=boolExp)? ')' { $exp = $aggFn.apply($c.exp, $ctx.b != null ? $b.exp: null); }
    | QUANTILE '(' c=timeExp ',' q=numScalar (',' b=boolExp)? ')' {  // Quantile of times
        $exp = $ctx.b != null ? $c.exp.quantile($q.value.doubleValue(), $b.exp) : $c.exp.quantile($q.value.doubleValue());
    }
    ;

/**
 * Date aggregate expressions, computing aggregates like MIN, MAX, AVG, MEDIAN, and QUANTILE over dates.
 *
 * Parameters:
 *  - The date column expression to aggregate.
 *  - A boolean expression to filter the rows involved in the aggregation (optional).
 *  - The quantile value to compute (between 0 and 1, for QUANTILE).
 */
dateAgg returns [DateExp exp] locals [BiFunction<DateExp, Condition, DateExp> aggFn]
    : (
        : MIN { $aggFn = (c, b) -> c.min(b); }
        | MAX { $aggFn = (c, b) -> c.max(b); }
        | AVG { $aggFn = (c, b) -> c.avg(b); }
        | MEDIAN { $aggFn = (c, b) -> c.median(b); }
    ) '(' c=dateExp (',' b=boolExp)? ')' { $exp = $aggFn.apply($c.exp, $ctx.b != null ? $b.exp: null); }
    | QUANTILE '(' c=dateExp ',' q=numScalar (',' b=boolExp)? ')' {
        $exp = $ctx.b != null
            ? $c.exp.quantile($q.value.doubleValue(), $b.exp)
            : $c.exp.quantile($q.value.doubleValue());
    }
    ;

/**
 * Datetime aggregate expressions, performing MIN, MAX, AVG, MEDIAN, or QUANTILE calculations over datetime values.
 *
 * Parameters:
 *  - The datetime column expression to aggregate.
 *  - A boolean expression to filter the rows involved in the aggregation (optional).
 *  - The quantile value to compute (between 0 and 1, for QUANTILE).
 */
dateTimeAgg returns [DateTimeExp exp] locals [BiFunction<DateTimeExp, Condition, DateTimeExp> aggFn]
    : (
        : MIN { $aggFn = (c, b) -> c.min(b); }
        | MAX { $aggFn = (c, b) -> c.max(b); }
        | AVG { $aggFn = (c, b) -> c.avg(b); }
        | MEDIAN { $aggFn = (c, b) -> c.median(b); }
    ) '(' c=dateTimeExp (',' b=boolExp)? ')' { $exp = $aggFn.apply($c.exp, $ctx.b != null ? $b.exp: null); }
    | QUANTILE '(' c=dateTimeExp ',' q=numScalar (',' b=boolExp)? ')' {
        $exp = $ctx.b != null
            ? $c.exp.quantile($q.value.doubleValue(), $b.exp)
            : $c.exp.quantile($q.value.doubleValue());
    }
    ;

/**
 * String aggregate expressions, specifically MIN and MAX. These find the lexicographically minimum or maximum string value.
 *
 * Parameters:
 *  - The string expression to aggregate.
 *  - A filtering condition (optional).
 */
strAgg returns [StrExp exp] locals [BiFunction<StrExp, Condition, StrExp> aggFn]
    : (
        : MIN { $aggFn = (c, b) -> c.min(b); }
        | MAX { $aggFn = (c, b) -> c.max(b); }
    ) '(' c=strExp (',' b=boolExp)? ')' { $exp = $aggFn.apply($c.exp, $ctx.b != null ? $b.exp: null); }
    ;

/**
 * Rule that consumes all function names in the context of an identifier.
 * Any new function name should be copied here.
 */
//@ doc:inline
fnName returns [String id]
    : (
    BOOL
    | INT
    | LONG
    | BIGINT
    | FLOAT
    | DOUBLE
    | DECIMAL
    | STR
    | COL
    | CAST_AS_BOOL
    | CAST_AS_INT
    | CAST_AS_LONG
    | CAST_AS_BIGINT
    | CAST_AS_FLOAT
    | CAST_AS_DOUBLE
    | CAST_AS_DECIMAL
    | CAST_AS_STR
    | CAST_AS_TIME
    | CAST_AS_DATE
    | CAST_AS_DATETIME
    | CAST_AS_OFFSET_DATETIME
    | IF
    | IF_NULL
    | SPLIT
    | SHIFT
    | CONCAT
    | SUBSTR
    | TRIM
    | LEN
    | MATCHES
    | STARTS_WITH
    | ENDS_WITH
    | CONTAINS
    | DATE
    | TIME
    | DATETIME
    | OFFSET_DATETIME
    | YEAR
    | MONTH
    | DAY
    | HOUR
    | MINUTE
    | SECOND
    | MILLISECOND
    | PLUS_YEARS
    | PLUS_MONTHS
    | PLUS_WEEKS
    | PLUS_DAYS
    | PLUS_HOURS
    | PLUS_MINUTES
    | PLUS_SECONDS
    | PLUS_MILLISECONDS
    | PLUS_NANOS
    | ABS
    | ROUND
    | ROW_NUM
    | SCALE
    | COUNT
    | SUM
    | CUMSUM
    | MIN
    | MAX
    | AVG
    | MEDIAN
    | QUANTILE
    | FIRST
    | LAST
    | VCONCAT
    | LIST
    | SET
    | ARRAY
    | ASC
    | DESC
    ) { $id = $text; }
    ;

/// **Lexer rules**

// *General purpose tokens*

//@ doc:inline
LP: '(';

//@ doc:inline
RP: ')';

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

IN: 'in';

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
BIGINT: 'bigint';

//@ doc:inline
FLOAT: 'float';

//@ doc:inline
DOUBLE: 'double';

//@ doc:inline
DECIMAL: 'decimal';

//@ doc:inline
STR: 'str';

//@ doc:inline
COL: 'col';

// *Cast functions*

//@ doc:inline
CAST_AS_BOOL: 'castAsBool';

//@ doc:inline
CAST_AS_INT: 'castAsInt';

//@ doc:inline
CAST_AS_LONG: 'castAsLong';

//@ doc:inline
CAST_AS_BIGINT: 'castAsBigint';

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

//@ doc:inline
SCALE: 'scale';

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

//@ doc:inline
VCONCAT: 'vConcat';

//@ doc:inline
LIST: 'list';

//@ doc:inline
SET: 'set';

//@ doc:inline
ARRAY: 'array';

/// *Literals*

//@ doc:inline
NULL: 'null';

//@ doc:inline
TRUE: 'true';

//@ doc:inline
FALSE: 'false';

//@ doc:inline
ASC: 'asc';

//@ doc:inline
DESC: 'desc';

//@ doc:inline
AS: 'as';

/**
 * Matches an integer literal in decimal, hexadecimal, octal, or binary format.
 */
//@ doc:inline
INTEGER_LITERAL
    : [+-]? ( DEC_LITERAL | HEX_LITERAL ) [iIlLhH]?
    ;

/**
 * Matches a floating-point literal, supporting both decimal and hexadecimal representations.
 */
//@ doc:inline
FLOAT_LITERAL
    : [+-]? ( DEC_FLOAT_LITERAL| HEX_FLOAT_LITERAL )
    ;

/**
 * Matches a string literal.
 */
STRING_LITERAL: '\'' ('\'\'' | ~['])* '\'';

/**
 * Matches a quoted identifier.
 */
QUOTED_IDENTIFIER: '`' ('``' | ~[`])* '`';

/**
 * Matches an identifier. Identifiers start with a letter and can be followed by letters or digits.
 */
IDENTIFIER: IDENTIFIER_START IDENTIFIER_PART*;

fragment DEC_LITERAL: [0-9] ([0-9_]* [0-9])?;

fragment HEX_LITERAL: '0' [xX] HEX_DIGITS;

fragment DEC_FLOAT_LITERAL
    : DEC_LITERAL DEC_EXPONENT? [fFdDmM]?
    | DEC_LITERAL? '.' DEC_LITERAL DEC_EXPONENT? [fFdDmM]?
    ;

fragment DEC_EXPONENT: [eE] [+-]? DEC_LITERAL;

fragment HEX_FLOAT_LITERAL
    : HEX_LITERAL '.'? HEX_EXPONENT [fFdDmM]?
    | '0' [xX] HEX_DIGITS? '.' HEX_DIGITS HEX_EXPONENT [fFdDmM]?
    ;

fragment HEX_EXPONENT: [pP] [+-]? DEC_LITERAL;

fragment HEX_DIGITS: [0-9a-fA-F] ([0-9a-fA-F_]* [0-9a-fA-F])?;

//@ doc:inline
fragment IDENTIFIER_START: [$A-Z_a-z\u0080-\uFFFF];

//@ doc:inline
fragment IDENTIFIER_PART: [$A-Z_a-z0-9\u0080-\uFFFF];

/**
 * Skipped symbols: Whitespace and tabs.
 */
WS: [ \t\r\n]+ -> skip;
