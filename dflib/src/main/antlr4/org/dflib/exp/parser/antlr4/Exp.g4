grammar Exp;

@header {
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.temporal.Temporal;

import org.dflib.*;

import static org.dflib.exp.parser.antlr4.ExpParserUtils.*;
}

/// **Parser rules**

/**
 * The root rule of the grammar.
 */
root returns [Exp<?> exp]
    : expression EOF { $exp = $expression.exp; }
    ;

/**
 * An expression, which can be of various types including null, aggregate, boolean, numeric, string, temporal, or special functions.
 * An expression represents a single value or a combination of values, operators, and functions.
 */
expression returns [Exp<?> exp]
    : boolExp { $exp = $boolExp.exp; }
    | numExp { $exp = $numExp.exp; }
    | strExp { $exp = $strExp.exp; }
    | temporalExp { $exp = $temporalExp.exp; }
    | genericExp { $exp = $genericExp.exp; }
    | aggregateFn { $exp = $aggregateFn.exp; }
    | specialFn { $exp = $specialFn.exp; }
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
    | a=numExp (
        : MUL b=numExp { $exp = $a.exp.mul($b.exp); }
        | DIV b=numExp { $exp = $a.exp.div($b.exp); }
        | MOD b=numExp { $exp = $a.exp.mod($b.exp); }
        | ADD b=numExp { $exp = $a.exp.add($b.exp); }
        | SUB b=numExp { $exp = $a.exp.sub($b.exp); }
    )
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
    | a=boolExp (
        : AND b=boolExp { $exp = Exp.and($a.exp, $b.exp); }
        | OR b=boolExp { $exp = Exp.or($a.exp, $b.exp); }
    )
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
 * An integer scalar value of any size.
 */
integerScalar returns [Number value]
    : INTEGER_LITERAL { $value = parseIntegerValue($text); }
    ;

/**
 * A floating-point scalar value of any scale and precision.
 */
floatingPointScalar returns [Number value]
    : FLOATING_POINT_LITERAL { $value = parseFloatingPointValue($text); }
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
    : SINGLE_QUOTE_STRING_LITERAL { $value = unescapeString($text.substring(1, $text.length() - 1)); }
    | DOUBLE_QUOTE_STRING_LITERAL { $value = unescapeString($text.substring(1, $text.length() - 1)); }
    ;


/// **Column expressions**

/**
 * An expression referencing a numeric column.  Supports various numeric types.
 */
numColumn returns [NumExp<?> exp]
    : intColumn { $exp = $intColumn.exp; }
    | longColumn { $exp = $longColumn.exp; }
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
    | strScalar { $id = $strScalar.value; }
    | identifier { $id = $identifier.id; }
    ;

/**
 * An identifier, which is a sequence of letters and digits starting with a letter.
 */
//@ doc:inline
identifier returns [String id]
    : IDENTIFIER { $id = $text; }
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
 * String relational expressions. Compares two string expressions using either
 * equality (=) or inequality (!=).
 *
 * Parameters:
 *  - The left-hand side string expression.
 *  - The right-hand side string expression.
 */
strRelation returns [Condition exp]
    : a=strExp (
        : EQ b=strExp { $exp = $a.exp.eq($b.exp); }
        | NE b=strExp { $exp = $a.exp.ne($b.exp); }
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
timeRelation returns [Condition exp]
    : a=timeExp (
        : GT (b=timeExp { $exp = $a.exp.gt($b.exp); } | s=timeStrScalar { $exp = $a.exp.gt($s.value); } )
        | GE (b=timeExp { $exp = $a.exp.ge($b.exp); } | s=timeStrScalar { $exp = $a.exp.ge($s.value); } )
        | LT (b=timeExp { $exp = $a.exp.lt($b.exp); } | s=timeStrScalar { $exp = $a.exp.lt($s.value); } )
        | LE (b=timeExp { $exp = $a.exp.le($b.exp); } | s=timeStrScalar { $exp = $a.exp.le($s.value); } )
        | EQ (b=timeExp { $exp = $a.exp.eq($b.exp); } | s=timeStrScalar { $exp = $a.exp.eq($s.value); } )
        | NE (b=timeExp { $exp = $a.exp.ne($b.exp); } | s=timeStrScalar { $exp = $a.exp.ne($s.value); } )
        | BETWEEN (
            b=timeExp AND c=timeExp { $exp = $a.exp.between($b.exp, $c.exp); }
            | s1=timeStrScalar AND s2=timeStrScalar { $exp = $a.exp.between($s1.value, $s2.value); }
        )
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
dateRelation returns [Condition exp]
    : a=dateExp (
        : GT (b=dateExp { $exp = $a.exp.gt($b.exp); } | s=dateStrScalar { $exp = $a.exp.gt($s.value); } )
        | GE (b=dateExp { $exp = $a.exp.ge($b.exp); } | s=dateStrScalar { $exp = $a.exp.ge($s.value); } )
        | LT (b=dateExp { $exp = $a.exp.lt($b.exp); } | s=dateStrScalar { $exp = $a.exp.lt($s.value); } )
        | LE (b=dateExp { $exp = $a.exp.le($b.exp); } | s=dateStrScalar { $exp = $a.exp.le($s.value); } )
        | EQ (b=dateExp { $exp = $a.exp.eq($b.exp); } | s=dateStrScalar { $exp = $a.exp.eq($s.value); } )
        | NE (b=dateExp { $exp = $a.exp.ne($b.exp); } | s=dateStrScalar { $exp = $a.exp.ne($s.value); } )
        | BETWEEN (
            b=dateExp AND c=dateExp { $exp = $a.exp.between($b.exp, $c.exp); }
            | s1=dateStrScalar AND s2=dateStrScalar { $exp = $a.exp.between($s1.value, $s2.value); }
        )
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
dateTimeRelation returns [Condition exp]
    : a=dateTimeExp (
        : GT (b=dateTimeExp { $exp = $a.exp.gt($b.exp); } | s=dateTimeStrScalar { $exp = $a.exp.gt($s.value); } )
        | GE (b=dateTimeExp { $exp = $a.exp.ge($b.exp); } | s=dateTimeStrScalar { $exp = $a.exp.ge($s.value); } )
        | LT (b=dateTimeExp { $exp = $a.exp.lt($b.exp); } | s=dateTimeStrScalar { $exp = $a.exp.lt($s.value); } )
        | LE (b=dateTimeExp { $exp = $a.exp.le($b.exp); } | s=dateTimeStrScalar { $exp = $a.exp.le($s.value); } )
        | EQ (b=dateTimeExp { $exp = $a.exp.eq($b.exp); } | s=dateTimeStrScalar { $exp = $a.exp.eq($s.value); } )
        | NE (b=dateTimeExp { $exp = $a.exp.ne($b.exp); } | s=dateTimeStrScalar { $exp = $a.exp.ne($s.value); } )
        | BETWEEN (
            b=dateTimeExp AND c=dateTimeExp { $exp = $a.exp.between($b.exp, $c.exp); }
            | s1=dateTimeStrScalar AND s2=dateTimeStrScalar { $exp = $a.exp.between($s1.value, $s2.value); }
        )
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
offsetDateTimeRelation returns [Condition exp]
    : a=offsetDateTimeExp (
        : GT (b=offsetDateTimeExp { $exp = $a.exp.gt($b.exp); } | s=offsetDateTimeStrScalar { $exp = $a.exp.gt($s.value); } )
        | GE (b=offsetDateTimeExp { $exp = $a.exp.ge($b.exp); } | s=offsetDateTimeStrScalar { $exp = $a.exp.ge($s.value); } )
        | LT (b=offsetDateTimeExp { $exp = $a.exp.lt($b.exp); } | s=offsetDateTimeStrScalar { $exp = $a.exp.lt($s.value); } )
        | LE (b=offsetDateTimeExp { $exp = $a.exp.le($b.exp); } | s=offsetDateTimeStrScalar { $exp = $a.exp.le($s.value); } )
        | EQ (b=offsetDateTimeExp { $exp = $a.exp.eq($b.exp); } | s=offsetDateTimeStrScalar { $exp = $a.exp.eq($s.value); } )
        | NE (b=offsetDateTimeExp { $exp = $a.exp.ne($b.exp); } | s=offsetDateTimeStrScalar { $exp = $a.exp.ne($s.value); } )
        | BETWEEN (
            b=offsetDateTimeExp AND c=offsetDateTimeExp { $exp = $a.exp.between($b.exp, $c.exp); }
            | s1=offsetDateTimeStrScalar AND s2=offsetDateTimeStrScalar { $exp = $a.exp.between($s1.value, $s2.value); }
        )
    )
    ;

/**
 * A generic relational expression. Compares two expressions.
 *
 * Parameters:
 *  - The left-hand expression.
 *  - The right-hand expression.
 */
genericRelation returns [Condition exp]
    : a=genericExp (
        : EQ b=expression { $exp = $a.exp.eq($b.exp); }
        | NE b=expression { $exp = $a.exp.ne($b.exp); }
    )
    ;

/// **Functions**

/**
 * Numeric functions, including casting, counting, row number, absolute value, rounding, and field functions.
 * These functions operate on or produce numeric values.
 */
numFn returns [NumExp<?> exp]
    : castAsInt { $exp = $castAsInt.exp; }
    | castAsLong { $exp = $castAsLong.exp; }
    | castAsFloat { $exp = $castAsFloat.exp; }
    | castAsDouble { $exp = $castAsDouble.exp; }
    | castAsDecimal { $exp = $castAsDecimal.exp; }
    // TODO: check out COUNT and ROW_NUM functions
    | COUNT ('(' b=boolExp? ')') { $exp = $ctx.b != null ? Exp.count($b.exp) : Exp.count(); }
    | ROW_NUM ('(' ')') { $exp = Exp.rowNum(); }
    | ABS '(' numExp ')' { $exp = $numExp.exp.abs(); }
    | ROUND '(' numExp ')' { $exp = $numExp.exp.round(); }
    | timeFieldFn { $exp = $timeFieldFn.exp; }
    | dateFieldFn { $exp = $dateFieldFn.exp; }
    | dateTimeFieldFn { $exp = $dateTimeFieldFn.exp; }
    | offsetDateTimeFieldFn { $exp = $offsetDateTimeFieldFn.exp; }
    ;

/**
 * Time field functions.
 *
 * Supports extracting fields like hour, minute, second and millisecond from Time expressions.
 *
 * Parameters:
 *  - The TimeExp from which to extract the field.
 */
timeFieldFn returns [NumExp<Integer> exp]
    : HOUR '(' timeExp ')' { $exp = $timeExp.exp.hour(); }
    | MINUTE '(' timeExp ')' { $exp = $timeExp.exp.minute(); }
    | SECOND '(' timeExp ')' { $exp = $timeExp.exp.second(); }
    | MILLISECOND '(' timeExp ')' { $exp = $timeExp.exp.millisecond(); }
    ;

/**
 * Date field functions.
 *
 * Supports extracting fields like year, month, and day from Date expressions.
 *
 * Parameters:
 *  - The DateExp from which to extract the field.
 */
dateFieldFn returns [NumExp<Integer> exp]
    : YEAR '(' dateExp ')' { $exp = $dateExp.exp.year(); }
    | MONTH '(' dateExp ')' { $exp = $dateExp.exp.month(); }
    | DAY '(' dateExp ')' { $exp = $dateExp.exp.day(); }
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
 * OffsetDateTime field functions.
 *
 * Supports extracting fields like year, month, day, hour, minute, second,
 * and millisecond from OffsetDateTime expressions.
 *
 * Parameters:
 *  - The OffsetDateTimeExp from which to extract the field.
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
 * Boolean functions, which can include casting, matches, starts with, ends with, and contains.
 * These functions produce boolean results.
 *
 * Parameters:
 *  - The input expression(s) for the boolean function. The types and number of parameters depend on the specific function.
 *    For example, `matches` takes a string expression and a string literal, while `castAsBool` takes a single expression of any type.
 */
boolFn returns [Condition exp]
    : castAsBool { $exp = $castAsBool.exp; }
    | MATCHES '(' strExp ',' strScalar ')' { $exp = $strExp.exp.matches($strScalar.value); }
    | STARTS_WITH '(' strExp ',' strScalar ')' { $exp = $strExp.exp.startsWith($strScalar.value); }
    | ENDS_WITH '(' strExp ',' strScalar ')' { $exp = $strExp.exp.endsWith($strScalar.value); }
    | CONTAINS '(' strExp ',' strScalar ')' { $exp = $strExp.exp.contains($strScalar.value); }
    ;


/**
 * Time functions, including casting and arithmetic operations. These
 * functions operate on time values.
 *
 * Parameters:
 *  - The base TimeExp.
 *  - An integer representing the value to add (e.g., hours, minutes).
 */
timeFn returns [TimeExp exp]
    : castAsTime { $exp = $castAsTime.exp; }
    | PLUS_HOURS '(' a=timeExp ',' b=integerScalar ')' { $exp = $a.exp.plusHours($b.value.intValue()); }
    | PLUS_MINUTES '(' a=timeExp ',' b=integerScalar ')' { $exp = $a.exp.plusMinutes($b.value.intValue()); }
    | PLUS_SECONDS '(' a=timeExp ',' b=integerScalar ')' { $exp = $a.exp.plusSeconds($b.value.intValue()); }
    | PLUS_MILLISECONDS '(' a=timeExp ',' b=integerScalar ')' { $exp = $a.exp.plusMilliseconds($b.value.intValue()); }
    | PLUS_NANOS '(' a=timeExp ',' b=integerScalar ')' { $exp = $a.exp.plusNanos($b.value.intValue()); }
    ;

/**
 * Date functions, enabling casting and arithmetic operations on date values.
 *
 * Parameters:
 *  - The base DateExp.
 *  - An integer representing the value to add (e.g., years, months).
 */
dateFn returns [DateExp exp]
    : castAsDate { $exp = $castAsDate.exp; }
    | PLUS_YEARS '(' a=dateExp ',' b=integerScalar ')' { $exp = $a.exp.plusYears($b.value.intValue()); }
    | PLUS_MONTHS '(' a=dateExp ',' b=integerScalar ')' { $exp = $a.exp.plusMonths($b.value.intValue()); }
    | PLUS_WEEKS '(' a=dateExp ',' b=integerScalar ')' { $exp = $a.exp.plusWeeks($b.value.intValue()); }
    | PLUS_DAYS '(' a=dateExp ',' b=integerScalar ')' { $exp = $a.exp.plusDays($b.value.intValue()); }
    ;

/**
 * Datetime functions, including casting and arithmetic operations for datetime values.
 *
 * Parameters:
 *  - The base DateTimeExp.
 *  - An integer representing the value to add (e.g., years, hours).
 */
dateTimeFn returns [DateTimeExp exp]
    : castAsDateTime { $exp = $castAsDateTime.exp; }
    | PLUS_YEARS '(' a=dateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusYears($b.value.intValue()); }
    | PLUS_MONTHS '(' a=dateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusMonths($b.value.intValue()); }
    | PLUS_WEEKS '(' a=dateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusWeeks($b.value.intValue()); }
    | PLUS_DAYS '(' a=dateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusDays($b.value.intValue()); }
    | PLUS_HOURS '(' a=dateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusHours($b.value.intValue()); }
    | PLUS_MINUTES '(' a=dateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusMinutes($b.value.intValue()); }
    | PLUS_SECONDS '(' a=dateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusSeconds($b.value.intValue()); }
    | PLUS_MILLISECONDS '(' a=dateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusMilliseconds($b.value.intValue()); }
    | PLUS_NANOS '(' a=dateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusNanos($b.value.intValue()); }
    ;

/**
 * Datetime functions with an offset from UTC/Greenwich.  Supports casting and
 * arithmetic operations on timezone-aware datetime values.
 *
 * Parameters:
 *  - The base OffsetDateTimeExp.
 *  - An integer representing the value to add (e.g., years, hours).
 */
offsetDateTimeFn returns [OffsetDateTimeExp exp]
    : castAsOffsetDateTime { $exp = $castAsOffsetDateTime.exp; }
    | PLUS_YEARS '(' a=offsetDateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusYears($b.value.intValue()); }
    | PLUS_MONTHS '(' a=offsetDateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusMonths($b.value.intValue()); }
    | PLUS_WEEKS '(' a=offsetDateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusWeeks($b.value.intValue()); }
    | PLUS_DAYS '(' a=offsetDateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusDays($b.value.intValue()); }
    | PLUS_HOURS '(' a=offsetDateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusHours($b.value.intValue()); }
    | PLUS_MINUTES '(' a=offsetDateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusMinutes($b.value.intValue()); }
    | PLUS_SECONDS '(' a=offsetDateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusSeconds($b.value.intValue()); }
    | PLUS_MILLISECONDS '(' a=offsetDateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusMilliseconds($b.value.intValue()); }
    | PLUS_NANOS '(' a=offsetDateTimeExp ',' b=integerScalar ')' { $exp = $a.exp.plusNanos($b.value.intValue()); }
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
    | CONCAT ('(' (args+=strExp (',' args+=strExp)*)? ')') {
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
 * Special functions that provide control flow, data manipulation, or other specialized operations.
 */
specialFn returns [Exp<?> exp]
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
    | specialFn { $exp = $specialFn.exp; }
    // TODO: temporal exp here, or a special case in the shift rule
    ;

/// **Aggregate expressions**

/**
 * Aggregate expressions, such as MIN, MAX, SUM, AVG, etc.
 * Aggregates perform calculations across multiple rows of data.
 */
aggregateFn returns [Exp<?> exp]
    : positionalAgg { $exp = $positionalAgg.exp; }
    | numAgg { $exp = $numAgg.exp; }
    | timeAgg { $exp = $timeAgg.exp; }
    | dateAgg { $exp = $dateAgg.exp; }
    | dateTimeAgg { $exp = $dateTimeAgg.exp; }
    | strAgg { $exp = $strAgg.exp; }
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
 * Numeric aggregate expressions, calculating aggregates like MIN, MAX, SUM, AVG, MEDIAN, and QUANTILE.
 *
 * Parameters:
 *  - The numeric column expression to aggregate.
 *  - A boolean expression to filter the rows involved in the aggregation (optional).
 *  - The quantile value to compute (between 0 and 1, for QUANTILE).
 */
numAgg returns [NumExp<?> exp]
    : MIN '(' c=numExp (',' b=boolExp)? ')' { $exp = $c.exp.min($ctx.b != null ? $b.exp : null); }
    | MAX '(' c=numExp (',' b=boolExp)? ')' { $exp = $c.exp.max($ctx.b != null ? $b.exp : null); }
    | SUM '(' c=numExp (',' b=boolExp)? ')' { $exp = $c.exp.sum($ctx.b != null ? $b.exp : null); }
    | CUMSUM '(' c=numExp ')' { $exp = $c.exp.cumSum(); } // Cumulative Sum, no filter currently supported
    | AVG '(' c=numExp (',' b=boolExp)? ')' { $exp = $c.exp.avg($ctx.b != null ? $b.exp : null); }
    | MEDIAN '(' c=numExp (',' b=boolExp)? ')' { $exp = $c.exp.median($ctx.b != null ? $b.exp : null); }
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
timeAgg returns [TimeExp exp]
    : MIN '(' c=timeExp (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.min($b.exp) : $c.exp.min(); }
    | MAX '(' c=timeExp (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.max($b.exp) : $c.exp.max(); }
    | AVG '(' c=timeExp (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.avg($b.exp) : $c.exp.avg(); }
    | MEDIAN '(' c=timeExp (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.median($b.exp) : $c.exp.median(); }
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
dateAgg returns [DateExp exp]
    : MIN '(' c=dateExp (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.min($b.exp) : $c.exp.min(); }
    | MAX '(' c=dateExp (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.max($b.exp) : $c.exp.max(); }
    | AVG '(' c=dateExp (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.avg($b.exp) : $c.exp.avg(); }
    | MEDIAN '(' c=dateExp (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.median($b.exp) : $c.exp.median(); }
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
dateTimeAgg returns [DateTimeExp exp]
    : MIN '(' c=dateTimeExp (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.min($b.exp) : $c.exp.min(); }
    | MAX '(' c=dateTimeExp (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.max($b.exp) : $c.exp.max(); }
    | AVG '(' c=dateTimeExp (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.avg($b.exp) : $c.exp.avg(); }
    | MEDIAN '(' c=dateTimeExp (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $c.exp.median($b.exp) : $c.exp.median(); }
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
IDENTIFIER: IDENTIFIER_START IDENTIFIER_PART*;

fragment DECIMAL_LITERAL: '0' | [1-9] ([0-9_]* [0-9])?;

fragment DECIMAL_LITERAL_LEADING_ZEROS: [0-9] ([0-9_]* [0-9])?;

fragment HEX_LITERAL: '0' [xX] HEX_DIGITS;

fragment OCTAL_LITERAL: '0' [0-7] ([0-7_]* [0-7])?;

fragment BINARY_LITERAL: '0' [bB] [01] ([01_]* [01])?;

fragment DECIMAL_FLOATING_POINT_LITERAL
    : DECIMAL_LITERAL_LEADING_ZEROS DECIMAL_EXPONENT?
    | DECIMAL_LITERAL_LEADING_ZEROS? '.' DECIMAL_LITERAL_LEADING_ZEROS DECIMAL_EXPONENT?
    ;

fragment DECIMAL_EXPONENT: [eE] [+-]? DECIMAL_LITERAL;

fragment HEXADECIMAL_FLOATING_POINT_LITERAL
    : HEX_LITERAL '.'? HEXADECIMAL_EXPONENT
    | '0' [xX] HEX_DIGITS? '.' HEX_DIGITS HEXADECIMAL_EXPONENT
    ;

fragment HEXADECIMAL_EXPONENT: [pP] [+-]? DECIMAL_LITERAL;

fragment HEX_DIGITS: [0-9a-fA-F] ([0-9a-fA-F_]* [0-9a-fA-F])?;

//@ doc:inline
fragment UNICODE_ESCAPE: '\\u' [0-9a-fA-F] [0-9a-fA-F] [0-9a-fA-F] [0-9a-fA-F];

//@ doc:inline
fragment IDENTIFIER_START: [$A-Z_a-z\u0080-\uFFFF];

//@ doc:inline
fragment IDENTIFIER_PART: [$A-Z_a-z0-9\u0080-\uFFFF;?!#|`[\]{}@^\\];

/**
 * Skipped symbols: Whitespace and tabs.
 */
WS: [ \t\r\n]+ -> skip;
