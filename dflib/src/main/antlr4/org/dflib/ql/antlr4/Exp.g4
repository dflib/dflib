grammar Exp;

@header {
import java.util.stream.Collectors;

import org.dflib.*;

import static org.dflib.ql.antlr4.ExpParserUtils.*;
import static org.dflib.ql.antlr4.Literals.*;
}

@members {
// global state of the parser
PositionalParamSource paramSource;
Environment env;

public void setParameters(Object... params) {
    this.paramSource = new PositionalParamSource(params);
}

public void setEnv(Environment env) {
    this.env = env;
}
}

/// **Parser rules**

/**
 * The root rule of the grammar.
 */
expRoot returns [Exp<?> exp]
    : expSingle EOF { $exp = $expSingle.exp; }
    ;

expSingle returns [Exp<?> exp] locals [String alias]
    : expression
    (
       AS identifier { $alias = $identifier.id; }
    )?
    { $exp = $alias == null ? $expression.exp : $expression.exp.as($alias); }
    ;

expArray returns [Exp[] expressions]
    : args += expSingle (',' args += expSingle )* EOF
    { $expressions = $args.stream().map(e -> e.exp).toArray(Exp[]::new); }
    ;

/**
 * The root rule for the sorting spec
 */
sorterRoot returns [Sorter sorter]
    : sorterSingle EOF { $sorter = $sorterSingle.sorter; }
    ;

sorterSingle returns [Sorter sorter] locals [boolean desc]
    : expression (
        : ASC
        | DESC { $desc = true; }
    )? { $sorter = $desc ? $expression.exp.desc() : $expression.exp.asc(); }
    ;

sorterArray returns [Sorter[] sorters]
    : args += sorterSingle (',' args += sorterSingle)* EOF
    { $sorters = $args.stream().map(s -> s.sorter).toArray(Sorter[]::new); }
    ;

/**
 * An expression of any type: a literal, a column reference, a function call, or a combination of those with operators.
 * Operator precedence, from the loosest to the tightest binding: OR, AND, NOT, equality, ordering comparisons
 * (including BETWEEN and IN), additive, multiplicative, unary minus.
 */
expression returns [Exp<?> exp]
    : orExp { $exp = $orExp.exp; }
    ;

orExp returns [Exp<?> exp]
    : a=andExp { $exp = $a.exp; } (op=OR b=andExp { $exp = logical($exp, $op, $b.exp); })*
    ;

andExp returns [Exp<?> exp]
    : a=notExp { $exp = $a.exp; } (op=AND b=notExp { $exp = logical($exp, $op, $b.exp); })*
    ;

notExp returns [Exp<?> exp]
    : op=NOT notExp { $exp = not($notExp.exp, $op); }
    | eqExp { $exp = $eqExp.exp; }
    ;

/**
 * An equality comparison of two expressions. Equality is defined for expressions of any type, and binds looser than
 * the other comparisons, as in Java: `true = 5 < 4` compares `true` with the result of `5 < 4`.
 */
eqExp returns [Exp<?> exp]
    : a=cmpExp { $exp = $a.exp; } (op=(EQ | NE) b=cmpExp { $exp = rel($exp, $op, $b.exp); })*
    ;

/**
 * An ordering comparison of two expressions, or a range / list membership check. These are defined for numeric and
 * temporal expressions only, with an ISO-8601 string literal accepted as a temporal operand. A list membership check
 * is also defined for strings and untyped expressions. Unlike equality, these do not chain: `1 < 2 < 3` is a syntax
 * error.
 */
cmpExp returns [Exp<?> exp] locals [boolean negate]
    : a=addExp { $exp = $a.exp; }
    (
        : op=(GT | GE | LT | LE) b=addExp { $exp = rel($exp, $op, $b.exp); }
        | { $negate = false; } (NOT { $negate = true; })? op=BETWEEN b=addExp AND c=addExp {
            $exp = between($exp, $op, $b.exp, $c.exp, $negate);
        }
        | { $negate = false; } (NOT { $negate = true; })? op=IN l=anyScalarList {
            $exp = in($exp, $op, $l.value, $negate);
        }
    )?
    ;

addExp returns [Exp<?> exp]
    : a=mulExp { $exp = $a.exp; } (op=(ADD | SUB) b=mulExp { $exp = arithmetic($exp, $op, $b.exp); })*
    ;

mulExp returns [Exp<?> exp]
    : a=unaryExp { $exp = $a.exp; } (op=(MUL | DIV | MOD) b=unaryExp { $exp = arithmetic($exp, $op, $b.exp); })*
    ;

unaryExp returns [Exp<?> exp]
    : op=SUB unaryExp { $exp = negate($unaryExp.exp, $op); }
    | primary { $exp = $primary.exp; }
    ;

primary returns [Exp<?> exp]
    : '(' expression ')' { $exp = $expression.exp; }
    | fnCall { $exp = $fnCall.exp; }
    | column { $exp = $column.exp; }
    | array { $exp = $array.exp; }
    | scalar { $exp = val($scalar.value); }
    | NULL { $exp = val(null); }
    ;

/**
 * A call of a function from the QL function registry, resolved by name and argument types.
 */
fnCall returns [Exp<?> exp]
    : IDENTIFIER '(' (args+=expression (',' args+=expression)*)? ')' {
        $exp = fn(env, $IDENTIFIER, $args.stream().map(ctx -> ctx.exp).collect(Collectors.toList()));
    }
    ;

/// **Scalar expressions**

/**
 * A literal or a bound parameter.
 */
scalar returns [Object value]
    : boolScalar { $value = $boolScalar.value; }
    | numScalar { $value = $numScalar.value; }
    | strScalar { $value = $strScalar.value; }
    | PARAMETER { $value = paramSource.next($PARAMETER); }
    ;

/**
 * List of a comma-sperated scalars
 */
anyScalarList returns [Object[] value]
    : '(' values+=scalar (',' values+=scalar)* ')' { $value = $values.stream().map(a -> a.value).toArray(); }
    | PARAMETER { $value = paramSource.nextArray($PARAMETER); }
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
    : FLOAT_LITERAL { $value = parseFloatingPointValue($text); }
    ;

/**
 * A string literal.
 */
strScalar returns [String value]
    : STRING_LITERAL { $value = unescapeString($text.substring(1, $text.length() - 1)); }
    ;

/// **Column expressions**

/**
 * An expression referencing a column. The keyword specifies the type of the column values: `bool`, `int`, `long`,
 * `bigint`, `float`, `double`, `decimal`, `str`, `date`, `time`, `dateTime`, `offsetDateTime`, or `col` for a column
 * of an unspecified type. A bare identifier also references a column of an unspecified type.
 *
 * Parameters:
 *  - The identifier of the column (integer index or string name).
 */
column returns [Exp<?> exp]
    : type=(BOOL | INT | LONG | BIGINT | FLOAT | DOUBLE | DECIMAL | STR | DATE | TIME | DATETIME | OFFSET_DATETIME | COL)
        '(' columnId ')' { $exp = col($type, $columnId.id); }
    | identifier { $exp = col($identifier.id); }
    ;

/**
 * A column identifier, which can be an integer representing the column index or a string representing the column name.
 */
columnId returns [Object id]
    : integerScalar { $id = $integerScalar.value; }
    | identifier { $id = $identifier.id; }
    | PARAMETER { $id = paramSource.next($PARAMETER); }
    ;

/**
 * An identifier, which is a sequence of letters and digits starting with a letter.
 */
//@ doc:inline
identifier returns [String id]
    : IDENTIFIER { $id = $text; }
    | QUOTED_IDENTIFIER { $id = unescapeIdentifier($text.substring(1, $text.length() - 1)); }
    | keywordAsIdentifier { $id = $keywordAsIdentifier.id; }
    ;

/// **Aggregate expressions**

/**
 * Creates an aggregating expression whose "reduce" operation returns an array containing all Series values.
 * Array component type should be provided as a fully quolified class name (e.g. 'java.lang.String').
 */
array returns [Exp<?> exp]
    : ARRAY '(' e=expression ',' t=strScalar ')' { $exp = ExpParserUtils.array($e.exp, $t.value); }
    ;

/**
 * Rule that lets the keywords of the grammar be used where an identifier is expected, e.g. as a column name.
 */
//@ doc:inline
keywordAsIdentifier returns [String id]
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
    | DATE
    | TIME
    | DATETIME
    | OFFSET_DATETIME
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

// *Functions*

//@ doc:inline
DATE: 'date';

//@ doc:inline
TIME: 'time';

//@ doc:inline
DATETIME: 'dateTime';

//@ doc:inline
OFFSET_DATETIME: 'offsetDateTime';

// *Aggregates*

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

//@ doc:inline
PARAMETER: '?';

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
