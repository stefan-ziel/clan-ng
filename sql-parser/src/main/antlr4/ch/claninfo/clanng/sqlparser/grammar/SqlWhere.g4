grammar SqlWhere;

where: conditions;

conditions: condition (connector condition)*;

connector: BLANK+ ('OR' | 'AND') BLANK+;

condition: BLANK* COLUMN BLANK* op BLANK* literal BLANK*
	| BLANK* literal BLANK* op BLANK* COLUMN BLANK*
	| '(' conditions ')'
	| COLUMN BLANK+ 'IN' BLANK+ list;

literal: NUMBER | STRING;

op: '=' | '<>' | '<' | '<=' | '>=' | 'LIKE';

list  : '(' (STRING | NUMBER) (',' BLANK+ (STRING | NUMBER)) ')';

BLANK : [ \t\r\n]+;
COLUMN  : 'p' [_a-zA-Z0-9]+;
NUMBER: [-]? [0-9]+;
STRING: '\'' (~'\'')* '\'';