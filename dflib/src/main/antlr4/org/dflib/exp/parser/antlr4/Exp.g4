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
private static Exp<?> col(Exp<?> columnId, Function<Integer, Exp<?>> byIndex, Function<String, Exp<?>> byName) {
    if (Integer.class.equals(columnId.getType())) {
        return byIndex.apply((Integer) columnId.reduce((DataFrame) null));
    } else if (String.class.equals(columnId.getType())) {
        return byName.apply((String) columnId.reduce((DataFrame) null));
    } else {
        throw new IllegalArgumentException("An integer or a string expected");
    }
}
private static Exp<?> minAgg(Exp<?> exp, Condition condition) {
    if (exp instanceof NumExp) {
        NumExp numExp = (NumExp) exp;
        return condition != null ? numExp.min(condition) : numExp.max();
    } else if (exp instanceof StrExp) {
        StrExp strExp = (StrExp) exp;
        return condition != null ? strExp.min(condition) : strExp.max();
    } else {
        throw new IllegalArgumentException("A number or a string is expected");
    }
}
private static Exp<?> maxAgg(Exp<?> exp, Condition condition) {
    if (exp instanceof NumExp) {
        NumExp numExp = (NumExp) exp;
        return condition != null ? numExp.max(condition) : numExp.max();
    } else if (exp instanceof StrExp) {
        StrExp strExp = (StrExp) exp;
        return condition != null ? strExp.max(condition) : strExp.max();
    } else {
        throw new IllegalArgumentException("A number or a string is expected");
    }
}
private static NumExp<?> floatingPointScalar(String token) {
    return token.toLowerCase().endsWith("f")
            ? (FloatScalarExp) Exp.\$val(Float.parseFloat(token))
            : (DoubleScalarExp) Exp.\$val(Double.parseDouble(token));
}
}

/// **Parser rules**

root returns [Exp<?> exp]
    : expression EOF { $exp = $expression.exp; }
    ;

expression returns [Exp<?> exp]
    : NULL { $exp = Exp.\$val(null); }
    | agg { $exp = $agg.exp; }
    | boolExp { $exp = $boolExp.exp; }
    | numExp { $exp = $numExp.exp; }
    | strExp { $exp = $strExp.exp; }
    | temporalExp { $exp = $temporalExp.exp; }
    | ifExp { $exp = $ifExp.exp; }
    | ifNull { $exp = $ifNull.exp; }
    | split { $exp = $split.exp; }
    ;

boolExp returns [Condition exp]
    : '(' boolExp ')' { $exp = $boolExp.exp; }
    | boolScalar { $exp = $boolScalar.exp; }
    | boolColumn { $exp = $boolColumn.exp; }
    | boolFn { $exp = $boolFn.exp; }
    | comparison { $exp = $comparison.exp; }
    | NOT boolExp { $exp = Exp.not($boolExp.exp); }
    | boolExp (OR boolExp)+ { $exp = Exp.or($boolExp.exp); }
    | boolExp (AND boolExp)+ { $exp = Exp.and($boolExp.exp); }
    ;

numExp returns [NumExp<?> exp]
    : '(' numExp ')' { $exp = $numExp.exp; }
    | numScalar { $exp = $numScalar.exp; }
    | numColumn { $exp = $numColumn.exp; }
    | numFn { $exp = $numFn.exp; }
    | numAgg { $exp = $numAgg.exp; }
    | a=numExp MUL b=numExp { $exp = $a.exp.mul($b.exp); }
    | a=numExp DIV b=numExp { $exp = $a.exp.div($b.exp); }
    | a=numExp MOD b=numExp { $exp = $a.exp.mod($b.exp); }
    | a=numExp ADD b=numExp { $exp = $a.exp.add($b.exp); }
    | a=numExp SUB b=numExp { $exp = $a.exp.sub($b.exp); }
    ;

numFn returns [NumExp<?> exp]
    : COUNT ('(' b=boolExp? ')') { $exp = (NumExp<?>) ($ctx.b != null ? Exp.count($b.exp) : Exp.count()); }
    | ROW_NUM '()' { $exp = Exp.rowNum(); }
    | ABS '(' numExp ')' { $exp = $numExp.exp.abs(); }
    | LEN '(' strExp ')' { $exp = $strExp.exp.mapVal(String::length).castAsInt(); }
    ;

boolFn returns [Condition exp]
    : MATCHES '(' strExp ',' strScalar ')' { $exp = $strExp.exp.matches($strScalar.exp.reduce((DataFrame) null)); }
    ;

agg returns [Exp<?> exp]
    : FIRST '(' e=expression (',' b=boolExp)? ')' { $exp = $ctx.b != null ? $e.exp.first($b.exp) : $e.exp.first(); }
    | LAST '(' e=expression ')' { $exp = $e.exp.last(); } // TODO: bool condition
    | MIN '(' e=expression (',' b=boolExp)? ')' { $exp = minAgg($e.exp, $ctx.b != null ? $b.exp : null); }
    | MAX '(' e=expression (',' b=boolExp)? ')' { $exp = maxAgg($e.exp, $ctx.b != null ? $b.exp : null); }
    ;

numAgg returns [NumExp<?> exp]
    : SUM '(' n=numColumn (',' b=boolExp)? ')' { $exp = $n.exp.sum($ctx.b != null ? $b.exp : null); }
    | CUMSUM '(' n=numColumn ')' { $exp = $n.exp.cumSum(); }
    | AVG '(' n=numColumn (',' b=boolExp)? ')' { $exp = $n.exp.avg($ctx.b != null ? $b.exp : null); }
    | MEDIAN '(' n=numColumn (',' b=boolExp)? ')' { $exp = $n.exp.median($ctx.b != null ? $b.exp : null); }
    ;

strExp returns [StrExp exp]
    : strScalar { $exp = $strScalar.exp; }
    | strColumn { $exp = $strColumn.exp; }
    ;

temporalExp returns [Exp<?> exp]
    : temporalColumn { $exp = $temporalColumn.exp; }
    | temporalFn { $exp = $temporalFn.exp; }
    ;

temporalFn returns [Exp<?> exp]
    :
    ;

ifExp returns [Exp<?> exp]
    : IF '(' a=boolExp ',' b=expression ',' c=expression ')' { $exp = Exp.ifExp($a.exp, $b.exp, (Exp) $c.exp); }
    ;

ifNull returns [Exp<?> exp]
    : IF_NULL '(' a=expression ',' b=expression ')' { $exp = Exp.ifNull($a.exp, (Exp) $b.exp); }
    ;

split returns [Exp<String[]> exp] locals [String regex, Integer limit]
    : SPLIT '(' a=strExp ',' b=strScalar (',' c=integerScalar)? ')' {
        $regex = $b.exp.reduce((DataFrame) null);
        $limit = $ctx.c != null ? $c.exp.reduce((DataFrame) null) : 0;
        $exp = $a.exp.split($regex, $limit);
        }
    ;

comparison returns [Condition exp]
    : na=numExp (
        GT nb=numExp { $exp = $ctx.numExp(0).exp.gt($ctx.numExp(1).exp); }
        | GE nb=numExp { $exp = $na.exp.ge($nb.exp); }
        | LT nb=numExp { $exp = $na.exp.lt($nb.exp); }
        | LE nb=numExp { $exp = $na.exp.le($nb.exp); }
        | EQ nb=numExp { $exp = $na.exp.eq($nb.exp); }
        | NE nb=numExp { $exp = $na.exp.ne($nb.exp); }
    )
    | sa=strExp (
        EQ sb=strExp { $exp = $sa.exp.eq($sb.exp); }
        | NE sb=strExp { $exp = $sb.exp.ne($sb.exp); }
    )
    ;

numColumn returns [NumExp<?> exp]
    : intColumn { $exp = $intColumn.exp; }
    | longColumn { $exp = $longColumn.exp; }
    | floatColumn { $exp = $floatColumn.exp; }
    | doubleColumn { $exp = $doubleColumn.exp; }
    | decimalColumn { $exp = $decimalColumn.exp; }
    ;

intColumn returns [IntColumn exp]
    : INT '(' columnId ')' { $exp = (IntColumn) col($columnId.exp, Exp::\$int, Exp::\$int); }
    ;

longColumn returns [LongColumn exp]
    : LONG '(' columnId ')' { $exp = (LongColumn) col($columnId.exp, Exp::\$long, Exp::\$long); }
    ;

floatColumn returns [FloatColumn exp]
    : FLOAT '(' columnId ')' { $exp = (FloatColumn) col($columnId.exp, Exp::\$float, Exp::\$float); }
    ;

doubleColumn returns [DoubleColumn exp]
    : DOUBLE '(' columnId ')' { $exp = (DoubleColumn) col($columnId.exp, Exp::\$double, Exp::\$double); }
    ;

decimalColumn returns [DecimalColumn exp]
    : DECIMAL '(' columnId ')' { $exp = (DecimalColumn) col($columnId.exp, Exp::\$decimal, Exp::\$decimal); }
    ;

boolColumn returns [BoolColumn exp]
    : BOOL '(' columnId ')' { $exp = (BoolColumn) col($columnId.exp, Exp::\$bool, Exp::\$bool); }
    ;

strColumn returns [StrColumn exp]:
    STR '(' columnId ')' { $exp = (StrColumn) col($columnId.exp, Exp::\$str, Exp::\$str); }
    ;

temporalColumn returns [Column<? extends Temporal> exp]
    : dateColumn { $exp = $dateColumn.exp; }
    | timeColumn { $exp = $timeColumn.exp; }
    | dateTimeColumn { $exp = $dateTimeColumn.exp; }
    ;

dateColumn returns [DateColumn exp]
    : DATE '(' columnId ')' { $exp = (DateColumn) col($columnId.exp, Exp::\$date, Exp::\$date); }
    ;

timeColumn returns [TimeColumn exp]
    : TIME '(' columnId ')' { $exp = (TimeColumn) col($columnId.exp, Exp::\$time, Exp::\$time); }
    ;

dateTimeColumn returns [DateTimeColumn exp]
    : DATETIME '(' columnId ')' { $exp = (DateTimeColumn) col($columnId.exp, Exp::\$dateTime, Exp::\$dateTime); }
    ;

columnId returns [ScalarExp<?> exp]
    : integerScalar { $exp = $integerScalar.exp; }
    | identifier { $exp = $identifier.exp; }
    | strScalar { $exp = $strScalar.exp; }
    ;

boolScalar returns [BoolScalarExp exp]
    : TRUE { $exp = (BoolScalarExp) Exp.\$val(true); }
    | FALSE { $exp = (BoolScalarExp) Exp.\$val(false); }
    ;

numScalar returns [NumExp<?> exp]
    : longScalar { $exp = $longScalar.exp; }
    | integerScalar { $exp = $integerScalar.exp; }
    | floatingPointScalar { $exp = $floatingPointScalar.exp; }
    ;

longScalar returns [LongScalarExp exp]
    : LONG_LITERAL { $exp = (LongScalarExp) Exp.\$val(Long.parseLong($text)); }
    ;

integerScalar returns [IntScalarExp exp]
    : INTEGER_LITERAL { $exp = (IntScalarExp) Exp.\$val(Integer.parseInt($text)); }
    ;

floatingPointScalar returns [NumExp<?> exp]
    : FLOATING_POINT_LITERAL { $exp = floatingPointScalar($text); }
    ;

strScalar returns [StrScalarExp exp]
    : CHARACTER_LITERAL { $exp = new StrScalarExp($text.substring(1, $text.length() - 1)); }
    | STRING_LITERAL { $exp = new StrScalarExp($text.substring(1, $text.length() - 1)); }
    ;

identifier returns [StrScalarExp exp]
    : IDENTIFIER { $exp = new StrScalarExp($text); }
    ;

/// **Lexer rules**

//@ doc:inline
NULL: 'null';

//@ doc:inline
TRUE: 'true';

//@ doc:inline
FALSE: 'false';

//@ doc:inline
IF: 'if';

//@ doc:inline
ELSE: 'else';

//@ doc:inline
SPLIT: 'split';

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
COL: 'col';

//@ doc:inline
BOOL: 'bool';

//@ doc:inline
INT : 'int';

//@ doc:inline

//@ doc:inline
LONG : 'long';

//@ doc:inline
FLOAT : 'float';

//@ doc:inline
DOUBLE : 'double';

//@ doc:inline
DECIMAL : 'decimal';

//@ doc:inline
STR : 'str';

// *Functions*

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
ABS: 'abs';

//@ doc:inline
ROW_NUM: 'rowNum';

//@ doc:inline
IF_NULL: 'ifNull';

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

//@ doc:inline
MEDIAN: 'median';

//@ doc:inline
FIRST: 'first';

//@ doc:inline
LAST: 'last';

/// *Literals*

//@ doc:inline
LONG_LITERAL: DECIMAL_LITERAL [lL] | HEX_LITERAL [lL] | OCTAL_LITERAL [lL] | BINARY_LITERAL [lL];

//@ doc:inline
INTEGER_LITERAL: DECIMAL_LITERAL | HEX_LITERAL | OCTAL_LITERAL | BINARY_LITERAL;

//@ doc:inline
FLOATING_POINT_LITERAL: DECIMAL_FLOATING_POINT_LITERAL | HEXADECIMAL_FLOATING_POINT_LITERAL;

fragment DECIMAL_LITERAL: [0-9] ([0-9_]* [0-9])?;

fragment HEX_LITERAL: '0' [xX] HEX_DIGITS;

fragment OCTAL_LITERAL: '0' [0-7] ([0-7_]* [0-7])?;

fragment BINARY_LITERAL: '0' [bB] [01] ([01_]* [01])?;

fragment DECIMAL_FLOATING_POINT_LITERAL: DECIMAL_LITERAL '.' DECIMAL_LITERAL? DECIMAL_EXPONENT? [fFdD]? | '.' DECIMAL_LITERAL DECIMAL_EXPONENT? [fFdD]? | DECIMAL_LITERAL DECIMAL_EXPONENT [fFdD]? | DECIMAL_LITERAL DECIMAL_EXPONENT?;

fragment DECIMAL_EXPONENT: [eE] [+-]? DECIMAL_LITERAL;

fragment HEXADECIMAL_FLOATING_POINT_LITERAL: HEX_LITERAL '.'? HEXADECIMAL_EXPONENT [fFdD]? | '0' [xX] HEX_DIGITS? '.' HEX_DIGITS HEXADECIMAL_EXPONENT [fFdD]?;

fragment HEXADECIMAL_EXPONENT: [pP] [+-]? DECIMAL_LITERAL;

fragment HEX_DIGITS: [0-9a-fA-F] ([0-9a-fA-F_]* [0-9a-fA-F])?;

CHARACTER_LITERAL: '\'' (~['\\\n\r] | ESCAPE | UNICODE_ESCAPE) '\'';
STRING_LITERAL: '"' (~["\\\n\r] | ESCAPE | UNICODE_ESCAPE)* '"';

//@ doc:inline
fragment ESCAPE: '\\' ([sntrbf\\"'] | [0-7] [0-7]? | [0-3] [0-7] [0-7]);

//@ doc:inline
fragment UNICODE_ESCAPE: '\\u' HEX_DIGITS;

//@ doc:inline
IDENTIFIER: LETTER PART_LETTER*;

//@ doc:inline
fragment LETTER: [$A-Z_a-z];

//@ doc:inline
fragment PART_LETTER: [$0-9A-Z_a-z];

/**
 * Skipped symbols.
 */
WS: [ \t\r\n]+ -> skip;
