parser grammar SysYParser;

options {
    tokenVocab = SysYLexer;
}

program
   : compUnit
   ;

compUnit
   : (funcDef | decl)+ EOF
   ;

// 下面是其他的语法单元定义

// Function
funcDef
    : funcType IDENT L_PAREN funcFParams? R_PAREN block
    ;

funcType
    : INT | VOID
    ;

funcFParams
    : funcFParam (COMMA funcFParam)*
    ;
// int i |
// int i [] |
// int i [][exp]
funcFParam
    : bType IDENT (L_BRACKT R_BRACKT (L_BRACKT exp R_BRACKT)* )?
    ;

block
    : L_BRACE ( blockItem )* R_BRACE
    ;

blockItem
    : decl | stmt
    ;

stmt
    : lVal ASSIGN exp SEMICOLON # ASSIGNMENT
    | (exp)? SEMICOLON  # EXP
    | block # BLOCK
    | IF L_PAREN cond R_PAREN stmt (ELSE stmt)? # IF_ELSE
    | WHILE L_PAREN cond R_PAREN stmt   # WHILE_STMT
    | BREAK SEMICOLON   # BREAK_STMT
    | CONTINUE SEMICOLON    # CONTINUE_STMT
    | RETURN (exp)? SEMICOLON   # RETURN_STMT
    ;

// decl
decl
    : constDecl | varDecl
    ;

constDecl
    : CONST bType constDef (COMMA constDef)* SEMICOLON
    ;

constDef
    : IDENT (L_BRACKT constExp R_BRACKT)* ASSIGN constInitVal
    ;

constInitVal
    : constExp
    | L_BRACE ( constInitVal ( COMMA constInitVal)* )? R_BRACE
    ;

varDecl
    : bType varDef (COMMA varDef)* SEMICOLON
    ;

varDef
    : IDENT (L_BRACKT constExp R_BRACKT)*
    | IDENT (L_BRACKT constExp R_BRACKT)* ASSIGN initVal
    ;

initVal
    : exp
    | L_BRACE (initVal (COMMA initVal)* )? R_BRACE
    ;

bType
    : INT
    ;

exp
   : L_PAREN exp R_PAREN                # EXPR
   | lVal                               # LV
   | number                             # NUM
   | IDENT L_PAREN funcRParams? R_PAREN # FUNC_CALL
   | unaryOp exp                        # UN_OP
   | exp (MUL | DIV | MOD) exp          # MDM_OP
   | exp (PLUS | MINUS) exp             # PM_OP
   ;


cond
   : exp                            # CONEXP
   | cond (LT | GT | LE | GE) cond  # LG
   | cond (EQ | NEQ) cond           # EN
   | cond AND cond                  # AND
   | cond OR cond                   # OR
   ;

lVal
   : IDENT (L_BRACKT exp R_BRACKT)* // a[?][?][?]
   ;

number
   : INTEGR_CONST
   ;

unaryOp
   : PLUS
   | MINUS
   | NOT
   ;

funcRParams
   : param (COMMA param)*
   ;

param
   : exp
   ;

constExp
   : exp
   ;