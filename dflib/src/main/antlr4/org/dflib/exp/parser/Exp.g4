grammar Exp;

// root node of the expression
expression : (exp | cond) EOF;

// TODO: should we split grammar rules by arity like this?
//       Or is it better to split by the type, like numExp, strExp, etc.?
exp
    : exp0
    | exp1
    | exp2
    | exp3
    ;

cond
    : NOT cond
    | cond (OR cond)+
    | cond (AND cond)+
    | '(' cond ')'
    | bool
    | comparison
    | boolType // TODO: or any column with dynamic type check at runtime
               //       (and failure if it's not boolean or explicit conversion)?
    ;

comparison : exp comparisonOp exp;

exp0
    : count
    | rowNum
    | reference
    ;

exp1
    :
    (
      intExp1
    | dateTimeExp1
    | strExp1
    | aggExp1
    )
    '('
    arg
    ')'
    ;

exp2
    : ops
    | fn2
//    | comparison
    ;

exp3
    : if
    ;

if : IF '(' cond ',' arg ',' arg ')' ;

ops: reference infixOp arg; // TODO: is it expression or a reference only?
                            //       expression needs lookahead or some other form of a recursion mitigation logic

fn2
    : (
        ifNull
    )
    '(' arg ',' arg ')'
    ;

arg
    : reference
    | exp
    ;

reference
    : column
    | scalar
    ;

column
    : positionalColumn
    | typedColumn
    | ID; // TODO: in JavaCC this clashes with function names
          //       also probably needs some escape syntax for random symbols in column names

positionalColumn : COL '(' INTEGER ')' ;

typedColumn
    : strType
    | intType
    | boolType
    // TODO: other casts
    ;

strType : STR '(' (INTEGER | ID) ')' ;

intType : INT '(' (INTEGER | ID) ')' ;

boolType : BOOL '(' (INTEGER | ID) ')' ;

scalar
    : numeric
    | string
    | datetime
    | bool
    | null
    ;

intExp1
    : abs
    ;

dateTimeExp1
    : date
    | time
    // TODO: many more time components functions
    ;

strExp1
    : trim
    ;
    // TODO: by the current logic `substr` function is considered exp1, but here it would be exp2 with a scalar arg

aggExp1
    : sum
    | avg
    | median
    | last
    | first
    ;

infixOp
    : math
    | ifNull
    ;

comparisonOp
    : eq
    | le
    | ge
    | lt
    | gt
    ;

// TODO: bit math,
math
    : add
    | sub
    | mul
    | div
    ;

// simple nodes (ops and functions)
rowNum: ROW_NUM;
count: COUNT;
sum : SUM;
avg : AVG;
median : MEDIAN;
ifNull : IF_NULL;
first: FIRST;
last: LAST;
abs: ABS;
date: DATE;
time: TIME;
trim: TRIM;
eq: EQ;
le: LE;
ge: GE;
lt: LT;
gt: GT;
add: ADD;
sub: SUB;
mul: MUL;
div: DIV;


// TODO: this all is just placeholders for the prototyping
numeric: INTEGER | FLOAT; // TODO: do we need LONG, SHORT, BYTE, DOUBLE, BigDecimal
                          //       and all java formats for the numericals ??
string: '"' '"';          // TODO: do we need escaped and non-escaped string literals, multiline, etc.
datetime: 'xxx';          // TODO: ISO date, time and datetime? periods, durations?
bool: TRUE | FALSE ;
null: NULL;

// data types
INTEGER: [0-9]+ ; // TODO: placeholder
FLOAT: INTEGER ;  // TODO: placeholder
NULL: 'null' ;
TRUE: 'true' ;
FALSE: 'false' ;

// IF ELSE
IF: 'if';
ELSE: 'else';

// ops
NOT: 'not' ;
EQ: '==' ;
LE: '<=' ;
GE: '>=' ;
LT: '<' ;
GT: '>' ;

ADD: '+' ;
SUB: '-' ;
MUL: '*' ;
DIV: '/' ;

AND: 'and' ;
OR: 'or' ;

// column operators
COL: '$col' ;
STR : '$str' ;
INT : '$int' ;
BOOL: '$bool' ;

// functions
SUBSTR: 'substr' ;
TRIM: 'trim' ;
DATE: 'date' ;
TIME: 'time' ;
ABS: 'abs' ;
ROW_NUM: 'rowNum' ;
IF_NULL: 'ifNull' ;

// aggregates
COUNT: 'count' ;
SUM: 'sum' ;
AVG: 'avg' ;
MEDIAN: 'median' ;
FIRST: 'first' ;
LAST: 'last' ;

// column id
ID : [a-zA-Z] [a-zA-Z0-9]* ;

WS: [ \t\r\n]+ -> skip ;
