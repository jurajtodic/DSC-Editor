
grammar DSC;

/*
 * Parser Rules
 */

dsc:	type EOF;

type: WORD '{' typename '}' ;

typename: WORD '{' pair+ '}' ;

pair
	: object
	| array
	| property
	;

property
    : WORD '=' value+
    | WORD '=' '"' value+ '"'
    ;

object: WORD '{' pair+ '}' ;

array
    :  WORD '{' value (',' value)* '}'
    |  WORD '{' value+ '}'
    ;

value
    :	'"' WORD '"'
    |   '"' NUMBER '"'
    |   '"' 'true' '"'
    |   '"' 'false' '"'
    |   '"' 'null' '"'
    |   WORD
    |   NUMBER
    |   'true'
    |   'false'
    |   'null'
    ;

/*
 * Lexer Rules
 */

// STRING :  '"' (ESC | ~["\\])* '"' ;
WORD
    : (UPPERCASE | LOWERCASE | DIGIT | ESC)+
    | '"' (UPPERCASE | LOWERCASE | DIGIT | ESC)+ '"'
    ;

fragment LOWERCASE  : [a-z] ;
fragment UPPERCASE  : [A-Z] ;
fragment DIGIT : [0-9] ;

fragment ESC :   '\\' (["\\/bfnrt] | UNICODE) ;
fragment UNICODE : 'u' HEX HEX HEX HEX ;
fragment HEX : [0-9a-fA-F] ;


NUMBER
    :   '-'? INT '.' [0-9]+ EXP? // 1.35, 1.35E-9, 0.3, -4.5
    |   '-'? INT EXP             // 1e10 -3e4
    |   '-'? INT                 // -3, 45
    ;
fragment INT :   '0' | [1-9] [0-9]* ; // no leading zeros
fragment EXP :   [Ee] [+\-]? INT ; // \- since - means "range" inside [...]


COMMENT	: '#' ~[\r\n\f]* -> skip ;
WHITESPACE : [ \t\r\n] -> skip;

// mode MODIFIER_MODE;

//   MODIFIER_MODE_SPACES : [ \t] -> skip;
//   MODIFIER_MODE_NL     : [\r\n]+ -> skip, mode(DEFAULT_MODE);



