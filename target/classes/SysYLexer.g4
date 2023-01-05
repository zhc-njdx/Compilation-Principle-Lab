lexer grammar SysYLexer ;

CONST : 'const' ;
INT : 'int' ;
VOID : 'void' ;
IF : 'if' ;
ELSE : 'else' ;
WHILE : 'while' ;
BREAK : 'break' ;
CONTINUE : 'continue' ;
RETURN : 'return' ;
PLUS : '+' ;
MINUS : '-' ;
MUL : '*' ;
DIV : '/' ;
MOD : '%' ;
ASSIGN : '=' ;
EQ : '==' ;
NEQ : '!=' ;
LT : '<' ;
GT : '>' ;
LE : '<=' ;
GE : '>=' ;
NOT : '!' ;
AND : '&&' ;
OR : '||' ;
L_PAREN : '(' ;
R_PAREN : ')' ;
L_BRACE : '{' ;
R_BRACE : '}' ;
L_BRACKT : '[' ;
R_BRACKT : ']' ;
COMMA : ',' ;
SEMICOLON : ';' ;

INTEGR_CONST : (HEX_DIGIT) | (OCT_DIGIT) | (DEC_DIGIT) ;
IDENT : ( '_' | LETTER ) ( '_' | LETTER | DIGIT )* ;

WS : [ \t\r\n]+ -> skip ;
LINE_COMMENT : '//' .*? '\n' -> skip ;
MULTILINE_COMMENT : '/*' .*? '*/' -> skip ;

fragment DEC_DIGIT : '0' | ( ([1-9]) (DIGIT*) );
fragment OCT_DIGIT : ('0') ([0-7]+) ;
fragment HEX_DIGIT : ('0x' | '0X') ([0-9a-fA-F]+) ;

fragment LETTER : [a-zA-Z] ;
fragment DIGIT : [0-9] ;