grammar ExpClean;

root
    : exp EOF
    ;

exp
    : boolExp
    | numExp
    | strExp
    | temporalExp
    | aggregates
    | if
    | ifNull
    | null
    ;

numExp
    : numScalar
    | numType
    | numExp mathOp numExp
    | numFn
    | numAgg
    | '(' numExp ')'
    ;

/**
 * Functions returning numeric type
 */
numFn
    : count
    | rowNum
    | abs '(' numExp ')'
    | len '(' strExp ')'
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
//    | strAgg
    ;

/**
 * Functions returning string type
 */
strFn
    : trim '(' strExp ')'
    ;

//strAgg
//    :
//    ;

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
if : IF '(' boolExp ',' exp ',' exp ')' ;
ifNull : IF_NULL '(' exp ',' exp ')' ;

aggregates
    : last
    | first
    ;

comparison
    : numExp comparisonOp numExp
    | strExp comparisonOp strExp
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

columnId : INTEGER | ID | strScalar;

comparisonOp
    : eq
    | le
    | ge
    | lt
    | gt
    ;

// TODO: bit math,
mathOp
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
len: LEN;
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
LEN: 'len' ;
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