grammar ExpClean;

exp
    :
    (
      numExp
    | strExp
    | boolExp
    | temporalExp
    | aggregates
    | if
    | ifNull
    ) EOF
    ;

numExp
    : numScalar
    | numType
    | numExp math numExp
    | numFn
    | numAgg
    | '(' numExp ')'
    ;

numFn
    : count
    | rowNum
    | abs '(' arg ')'
    ;

numAgg
    : sum
    | avg
    | median
    ;

strExp
    : strScalar
    | strType
    | strFn
    | strAgg
    ;

strFn
    : trim
    ;

strAgg
    :
    ;

temporalExp
    : temporalType
    | temporalFn
    ;

boolExp
    : NOT boolExp
    | boolExp (OR boolExp)+
    | boolExp (AND boolExp)+
    | '(' boolExp ')'
    | boolScalar
    | comparison
    | boolType // TODO: or any column with dynamic type check at runtime
               //       (and failure if it's not boolean or explicit conversion)?
    ;

// special functions
if : IF '(' boolExp ',' arg ',' arg ')' ;
ifNull : IF_NULL '(' arg ',' arg ')' ;
aggregates
    : last
    | first
    ;

comparison
    : numExp comparisonOp numExp
    | strExp comparisonOp strExp
    ;

arg
    : column
    | scalar
    | exp
    ;

column
    : typedColumn
    | id
    ;

typedColumn
    : strType
    | numType
    | boolType
    // TODO: other casts
    ;

strType : STR '(' columnId ')' ;
boolType : BOOL '('columnId ')' ;
numType
    // TODO: more numeric types
    : intType
    ;
intType : INT '(' columnId ')' ;

temporalType
    : dateType
    | timeType
    | dateTimeType
    ;

dateType : DATE '(' columnId ')' ;
timeType : TIME '(' columnId ')' ;
dateTimeType : DATETIME '(' columnId ')' ;

temporalFn
    :
    ;

id
    : COL '(' columnId ')'
    | ID // TODO: in JavaCC this clashes with function names
    ;

columnId : INTEGER | ID | strScalar;

scalar
    : numScalar
    | strScalar
    | datetimeScalar
    | boolScalar
    | null
    ;

aggExp
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
first: FIRST;
last: LAST;
abs: ABS;
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

// literals, TODO: just placeholders for now
numScalar: INTEGER | FLOAT; // TODO: we need all java formats for the numericals
strScalar: '"' '"';          // TODO: we need escaped and non-escaped string literals
datetimeScalar: 'xxx';          // TODO: ISO date, time and datetime, periods and durations are up to discussion
boolScalar: TRUE | FALSE ;
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
COL: 'col' ;
STR : 'str' ;
INT : 'int' ;
BOOL: 'bool' ;

// functions
SUBSTR: 'substr' ;
TRIM: 'trim' ;
DATE: 'date' ;
TIME: 'time' ;
DATETIME: 'datetime' ;
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