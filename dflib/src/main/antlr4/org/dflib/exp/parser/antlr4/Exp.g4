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

root returns [Exp<?> exp]
    : expression EOF { $exp = $expression.exp; }
    ;

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

/// *Numeric expressions*

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

/// *Boolean expressions*

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

/// *String expressions*

strExp returns [StrExp exp]
    : '(' strExp ')' { $exp = $strExp.exp; }
    | strScalar { $exp = (StrScalarExp) Exp.\$val($strScalar.value); }
    | strColumn { $exp = $strColumn.exp; }
    | strFn { $exp = $strFn.exp; }
    ;

/// *Temporal expressions*

temporalExp returns [Exp<? extends Temporal> exp]
    : '(' temporalExp ')' { $exp = $temporalExp.exp; }
    | timeExp { $exp = $timeExp.exp; }
    | dateExp { $exp = $dateExp.exp; }
    | dateTimeExp { $exp = $dateTimeExp.exp; }
    ;

timeExp returns [TimeExp exp]
    : timeColumn { $exp = $timeColumn.exp; }
    | timeFn { $exp = $timeFn.exp; }
    ;

dateExp returns [DateExp exp]
    : dateColumn { $exp = $dateColumn.exp; }
    | dateFn { $exp = $dateFn.exp; }
    ;

dateTimeExp returns [DateTimeExp exp]
    : dateTimeColumn { $exp = $dateTimeColumn.exp; }
    | dateTimeFn { $exp = $dateTimeFn.exp; }
    ;

/// *Scalar expressions*

numScalar returns [Number value]
    : longScalar { $value = $longScalar.value; }
    | integerScalar { $value = $integerScalar.value; }
    | floatingPointScalar { $value = $floatingPointScalar.value; }
    ;

longScalar returns [Long value]
    : LONG_LITERAL { $value = Long.parseLong($text); }
    ;

integerScalar returns [Integer value]
    : INTEGER_LITERAL { $value = Integer.parseInt($text); }
    ;

floatingPointScalar returns [Number value]
    : FLOATING_POINT_LITERAL { $value = floatingPointScalar($text); }
    ;

boolScalar returns [Boolean value]
    : TRUE { $value = true; }
    | FALSE { $value = false; }
    ;

strScalar returns [String value]
    : CHARACTER_LITERAL { $value = $text.substring(1, $text.length() - 1); }
    | STRING_LITERAL { $value = $text.substring(1, $text.length() - 1); }
    ;

/// *Column expressions*

numColumn returns [NumExp<?> exp]
    : intColumn { $exp = $intColumn.exp; }
    | longColumn { $exp = $longColumn.exp; }
    | floatColumn { $exp = $floatColumn.exp; }
    | doubleColumn { $exp = $doubleColumn.exp; }
    | decimalColumn { $exp = $decimalColumn.exp; }
    ;

intColumn returns [IntColumn exp]
    : INT '(' columnId ')' { $exp = (IntColumn) col($columnId.id, Exp::\$int, Exp::\$int); }
    ;

longColumn returns [LongColumn exp]
    : LONG '(' columnId ')' { $exp = (LongColumn) col($columnId.id, Exp::\$long, Exp::\$long); }
    ;

floatColumn returns [FloatColumn exp]
    : FLOAT '(' columnId ')' { $exp = (FloatColumn) col($columnId.id, Exp::\$float, Exp::\$float); }
    ;

doubleColumn returns [DoubleColumn exp]
    : DOUBLE '(' columnId ')' { $exp = (DoubleColumn) col($columnId.id, Exp::\$double, Exp::\$double); }
    ;

decimalColumn returns [DecimalColumn exp]
    : DECIMAL '(' columnId ')' { $exp = (DecimalColumn) col($columnId.id, Exp::\$decimal, Exp::\$decimal); }
    ;

boolColumn returns [BoolColumn exp]
    : BOOL '(' columnId ')' { $exp = (BoolColumn) col($columnId.id, Exp::\$bool, Exp::\$bool); }
    ;

strColumn returns [StrColumn exp]:
    STR '(' columnId ')' { $exp = (StrColumn) col($columnId.id, Exp::\$str, Exp::\$str); }
    ;

dateColumn returns [DateColumn exp]
    : DATE '(' columnId ')' { $exp = (DateColumn) col($columnId.id, Exp::\$date, Exp::\$date); }
    ;

timeColumn returns [TimeColumn exp]
    : TIME '(' columnId ')' { $exp = (TimeColumn) col($columnId.id, Exp::\$time, Exp::\$time); }
    ;

dateTimeColumn returns [DateTimeColumn exp]
    : DATETIME '(' columnId ')' { $exp = (DateTimeColumn) col($columnId.id, Exp::\$dateTime, Exp::\$dateTime); }
    ;

columnId returns [Object id]
    : integerScalar { $id = $integerScalar.value; }
    | strScalar { $id = $strScalar.value; }
    | identifier { $id = $identifier.id; }
    ;

identifier returns [String id]
    : IDENTIFIER { $id = $text; }
    ;

/// *Relational expresions*

relation returns [Condition exp]
    : numRelation { $exp = $numRelation.exp; }
    | strRelation { $exp = $strRelation.exp; }
    | timeRelation { $exp = $timeRelation.exp; }
    | dateRelation { $exp = $dateRelation.exp; }
    | dateTimeRelation { $exp = $dateTimeRelation.exp; }
    ;

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

strRelation returns [Condition exp]
    : a=strExp (
        : EQ b=strExp { $exp = $a.exp.eq($b.exp); }
        | NE b=strExp { $exp = $a.exp.ne($b.exp); }
    )
    ;

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

/// *Functions*

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

timeFieldFn returns [NumExp<Integer> exp]
    : HOUR '(' timeExp ')' { $exp = $timeExp.exp.hour(); }
    | MINUTE '(' timeExp ')' { $exp = $timeExp.exp.minute(); }
    | SECOND '(' timeExp ')' { $exp = $timeExp.exp.second(); }
    | MILLISECOND '(' timeExp ')' { $exp = $timeExp.exp.millisecond(); }
    ;

dateFieldFn returns [NumExp<Integer> exp]
    : YEAR '(' dateExp ')' { $exp = $dateExp.exp.year(); }
    | MONTH '(' dateExp ')' { $exp = $dateExp.exp.month(); }
    | DAY '(' dateExp ')' { $exp = $dateExp.exp.day(); }
    ;

dateTimeFieldFn returns [NumExp<Integer> exp]
    : YEAR '(' dateTimeExp ')' { $exp = $dateTimeExp.exp.year(); }
    | MONTH '(' dateTimeExp ')' { $exp = $dateTimeExp.exp.month(); }
    | DAY '(' dateTimeExp ')' { $exp = $dateTimeExp.exp.day(); }
    | HOUR '(' dateTimeExp ')' { $exp = $dateTimeExp.exp.hour(); }
    | MINUTE '(' dateTimeExp ')' { $exp = $dateTimeExp.exp.minute(); }
    | SECOND '(' dateTimeExp ')' { $exp = $dateTimeExp.exp.second(); }
    | MILLISECOND '(' dateTimeExp ')' { $exp = $dateTimeExp.exp.millisecond(); }
    ;

boolFn returns [Condition exp]
    : MATCHES '(' strExp ',' strScalar ')' { $exp = $strExp.exp.matches($strScalar.value); }
    ;

timeFn returns [TimeExp exp]
    : PLUS_HOURS '(' a=timeExp ',' b=integerScalar ')' { $exp = $a.exp.plusHours($b.value); }
    | PLUS_MINUTES '(' a=timeExp ',' b=integerScalar ')' { $exp = $a.exp.plusMinutes($b.value); }
    | PLUS_SECONDS '(' a=timeExp ',' b=integerScalar ')' { $exp = $a.exp.plusSeconds($b.value); }
    | PLUS_MILLISECONDS '(' a=timeExp ',' b=integerScalar ')' { $exp = $a.exp.plusMilliseconds($b.value); }
    | PLUS_NANOS '(' a=timeExp ',' b=integerScalar ')' { $exp = $a.exp.plusNanos($b.value); }
    ;

dateFn returns [DateExp exp]
    : PLUS_YEARS '(' a=dateExp ',' b=integerScalar ')' { $exp = $a.exp.plusYears($b.value); }
    | PLUS_MONTHS '(' a=dateExp ',' b=integerScalar ')' { $exp = $a.exp.plusMonths($b.value); }
    | PLUS_WEEKS '(' a=dateExp ',' b=integerScalar ')' { $exp = $a.exp.plusWeeks($b.value); }
    | PLUS_DAYS '(' a=dateExp ',' b=integerScalar ')' { $exp = $a.exp.plusDays($b.value); }
    ;

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

strFn returns [StrExp exp]
    : TRIM '(' strExp ')' { $exp = $strExp.exp.trim(); }
    | SUBSTR '(' s=strExp ',' a=integerScalar ',' b=integerScalar ')' { $exp = $s.exp.substr($a.value, $b.value); }
    ;


/// *Special functions*

ifExp returns [Exp<?> exp]
    : IF '(' a=boolExp ',' b=expression ',' c=expression ')' { $exp = Exp.ifExp($a.exp, $b.exp, (Exp) $c.exp); }
    ;

ifNull returns [Exp<?> exp]
    : IF_NULL '(' a=expression ',' b=expression ')' { $exp = Exp.ifNull($a.exp, (Exp) $b.exp); }
    ;

split returns [Exp<String[]> exp]
    : SPLIT '(' a=strExp ',' b=strScalar (',' c=integerScalar)? ')' {
        $exp = $ctx.c != null ? $a.exp.split($b.value, $c.value) : $a.exp.split($b.value);
        }
    ;

/// *Aggregates*

agg returns [Exp<?> exp]
    : positionalAgg { $exp = $positionalAgg.exp; }
    | numAgg { $exp = $numAgg.exp; }
    | timeAgg { $exp = $timeAgg.exp; }
    | dateAgg { $exp = $dateAgg.exp; }
    | dateTimeAgg { $exp = $dateTimeAgg.exp; }
    | strAgg { $exp = $strAgg.exp; }
    ;

positionalAgg returns [Exp<?> exp]
    : FIRST '(' e=expression (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $e.exp.first($b.exp) : $e.exp.first(); }
    | LAST '(' e=expression ')' { $exp = $e.exp.last(); } // TODO: bool condition
    ;

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
