package org.mozilla.javascript;

import com.integralblue.httpresponsecache.compat.libcore.net.http.HttpEngine;

public class Token {
    public static final int ADD = 21;
    public static final int AND = 106;
    public static final int ARRAYCOMP = 158;
    public static final int ARRAYLIT = 66;
    public static final int ARROW = 165;
    public static final int ASSIGN = 91;
    public static final int ASSIGN_ADD = 98;
    public static final int ASSIGN_BITAND = 94;
    public static final int ASSIGN_BITOR = 92;
    public static final int ASSIGN_BITXOR = 93;
    public static final int ASSIGN_DIV = 101;
    public static final int ASSIGN_LSH = 95;
    public static final int ASSIGN_MOD = 102;
    public static final int ASSIGN_MUL = 100;
    public static final int ASSIGN_RSH = 96;
    public static final int ASSIGN_SUB = 99;
    public static final int ASSIGN_URSH = 97;
    public static final int BINDNAME = 49;
    public static final int BITAND = 11;
    public static final int BITNOT = 27;
    public static final int BITOR = 9;
    public static final int BITXOR = 10;
    public static final int BLOCK = 130;
    public static final int BREAK = 121;
    public static final int CALL = 38;
    public static final int CASE = 116;
    public static final int CATCH = 125;
    public static final int CATCH_SCOPE = 57;
    public static final int COLON = 104;
    public static final int COLONCOLON = 145;
    public static final int COMMA = 90;
    public static final int COMMENT = 162;
    public static final int CONST = 155;
    public static final int CONTINUE = 122;
    public static final int DEBUGGER = 161;
    public static final int DEC = 108;
    public static final int DEFAULT = 117;
    public static final int DEFAULTNAMESPACE = 75;
    public static final int DELPROP = 31;
    public static final int DEL_REF = 70;
    public static final int DIV = 24;
    public static final int DO = 119;
    public static final int DOT = 109;
    public static final int DOTDOT = 144;
    public static final int DOTQUERY = 147;
    public static final int ELSE = 114;
    public static final int EMPTY = 129;
    public static final int ENTERWITH = 2;
    public static final int ENUM_ID = 63;
    public static final int ENUM_INIT_ARRAY = 60;
    public static final int ENUM_INIT_KEYS = 58;
    public static final int ENUM_INIT_VALUES = 59;
    public static final int ENUM_INIT_VALUES_IN_ORDER = 61;
    public static final int ENUM_NEXT = 62;
    public static final int EOF = 0;
    public static final int EOL = 1;
    public static final int EQ = 12;
    public static final int ERROR = -1;
    public static final int ESCXMLATTR = 76;
    public static final int ESCXMLTEXT = 77;
    public static final int EXPORT = 111;
    public static final int EXPR_RESULT = 135;
    public static final int EXPR_VOID = 134;
    public static final int FALSE = 44;
    public static final int FINALLY = 126;
    public static final int FIRST_ASSIGN = 91;
    public static final int FIRST_BYTECODE_TOKEN = 2;
    public static final int FOR = 120;
    public static final int FUNCTION = 110;
    public static final int GE = 17;
    public static final int GENEXPR = 163;
    public static final int GET = 152;
    public static final int GETELEM = 36;
    public static final int GETPROP = 33;
    public static final int GETPROPNOWARN = 34;
    public static final int GETVAR = 55;
    public static final int GET_REF = 68;
    public static final int GOTO = 5;
    public static final int GT = 16;
    public static final int HOOK = 103;
    public static final int IF = 113;
    public static final int IFEQ = 6;
    public static final int IFNE = 7;
    public static final int IMPORT = 112;
    public static final int IN = 52;
    public static final int INC = 107;
    public static final int INSTANCEOF = 53;
    public static final int JSR = 136;
    public static final int LABEL = 131;
    public static final int LAST_ASSIGN = 102;
    public static final int LAST_BYTECODE_TOKEN = 81;
    public static final int LAST_TOKEN = 166;
    public static final int LB = 84;
    public static final int LC = 86;
    public static final int LE = 15;
    public static final int LEAVEWITH = 3;
    public static final int LET = 154;
    public static final int LETEXPR = 159;
    public static final int LOCAL_BLOCK = 142;
    public static final int LOCAL_LOAD = 54;
    public static final int LOOP = 133;
    public static final int LP = 88;
    public static final int LSH = 18;
    public static final int LT = 14;
    public static final int METHOD = 164;
    public static final int MOD = 25;
    public static final int MUL = 23;
    public static final int NAME = 39;
    public static final int NE = 13;
    public static final int NEG = 29;
    public static final int NEW = 30;
    public static final int NOT = 26;
    public static final int NULL = 42;
    public static final int NUMBER = 40;
    public static final int OBJECTLIT = 67;
    public static final int OR = 105;
    public static final int POS = 28;
    public static final int RB = 85;
    public static final int RC = 87;
    public static final int REF_CALL = 71;
    public static final int REF_MEMBER = 78;
    public static final int REF_NAME = 80;
    public static final int REF_NS_MEMBER = 79;
    public static final int REF_NS_NAME = 81;
    public static final int REF_SPECIAL = 72;
    public static final int REGEXP = 48;
    public static final int RESERVED = 128;
    public static final int RETHROW = 51;
    public static final int RETURN = 4;
    public static final int RETURN_RESULT = 65;
    public static final int RP = 89;
    public static final int RSH = 19;
    public static final int SCRIPT = 137;
    public static final int SEMI = 83;
    public static final int SET = 153;
    public static final int SETCONST = 156;
    public static final int SETCONSTVAR = 157;
    public static final int SETELEM = 37;
    public static final int SETELEM_OP = 141;
    public static final int SETNAME = 8;
    public static final int SETPROP = 35;
    public static final int SETPROP_OP = 140;
    public static final int SETVAR = 56;
    public static final int SET_REF = 69;
    public static final int SET_REF_OP = 143;
    public static final int SHEQ = 46;
    public static final int SHNE = 47;
    public static final int STRICT_SETNAME = 74;
    public static final int STRING = 41;
    public static final int SUB = 22;
    public static final int SWITCH = 115;
    public static final int TARGET = 132;
    public static final int THIS = 43;
    public static final int THISFN = 64;
    public static final int THROW = 50;
    public static final int TO_DOUBLE = 151;
    public static final int TO_OBJECT = 150;
    public static final int TRUE = 45;
    public static final int TRY = 82;
    public static final int TYPEOF = 32;
    public static final int TYPEOFNAME = 138;
    public static final int URSH = 20;
    public static final int USE_STACK = 139;
    public static final int VAR = 123;
    public static final int VOID = 127;
    public static final int WHILE = 118;
    public static final int WITH = 124;
    public static final int WITHEXPR = 160;
    public static final int XML = 146;
    public static final int XMLATTR = 148;
    public static final int XMLEND = 149;
    public static final int YIELD = 73;
    static final boolean printICode = false;
    static final boolean printNames = false;
    public static final boolean printTrees = false;

    public enum CommentType {
        LINE,
        BLOCK_COMMENT,
        JSDOC,
        HTML
    }

    public static boolean isValidToken(int i) {
        return i >= ERROR && i <= LAST_TOKEN;
    }

    public static String keywordToName(int i) {
        switch (i) {
            case RETURN /*4*/:
                return "return";
            case NEW /*30*/:
                return "new";
            case DELPROP /*31*/:
                return "delete";
            case TYPEOF /*32*/:
                return "typeof";
            case NULL /*42*/:
                return "null";
            case THIS /*43*/:
                return "this";
            case FALSE /*44*/:
                return "false";
            case TRUE /*45*/:
                return "true";
            case THROW /*50*/:
                return "throw";
            case IN /*52*/:
                return "in";
            case INSTANCEOF /*53*/:
                return "instanceof";
            case YIELD /*73*/:
                return "yield";
            case TRY /*82*/:
                return "try";
            case FUNCTION /*110*/:
                return "function";
            case IF /*113*/:
                return "if";
            case ELSE /*114*/:
                return "else";
            case SWITCH /*115*/:
                return "switch";
            case CASE /*116*/:
                return "case";
            case DEFAULT /*117*/:
                return "default";
            case WHILE /*118*/:
                return "while";
            case DO /*119*/:
                return "do";
            case FOR /*120*/:
                return "for";
            case BREAK /*121*/:
                return "break";
            case CONTINUE /*122*/:
                return "continue";
            case VAR /*123*/:
                return "var";
            case WITH /*124*/:
                return "with";
            case CATCH /*125*/:
                return "catch";
            case FINALLY /*126*/:
                return "finally";
            case VOID /*127*/:
                return "void";
            case LET /*154*/:
                return "let";
            case CONST /*155*/:
                return "const";
            case DEBUGGER /*161*/:
                return "debugger";
            default:
                return null;
        }
    }

    public static String name(int i) {
        return String.valueOf(i);
    }

    public static String typeToName(int i) {
        switch (i) {
            case ERROR /*-1*/:
                return "ERROR";
            case EOF /*0*/:
                return "EOF";
            case EOL /*1*/:
                return "EOL";
            case FIRST_BYTECODE_TOKEN /*2*/:
                return "ENTERWITH";
            case LEAVEWITH /*3*/:
                return "LEAVEWITH";
            case RETURN /*4*/:
                return "RETURN";
            case GOTO /*5*/:
                return "GOTO";
            case IFEQ /*6*/:
                return "IFEQ";
            case IFNE /*7*/:
                return "IFNE";
            case SETNAME /*8*/:
                return "SETNAME";
            case BITOR /*9*/:
                return "BITOR";
            case BITXOR /*10*/:
                return "BITXOR";
            case BITAND /*11*/:
                return "BITAND";
            case EQ /*12*/:
                return "EQ";
            case NE /*13*/:
                return "NE";
            case LT /*14*/:
                return "LT";
            case LE /*15*/:
                return "LE";
            case GT /*16*/:
                return "GT";
            case GE /*17*/:
                return "GE";
            case LSH /*18*/:
                return "LSH";
            case RSH /*19*/:
                return "RSH";
            case URSH /*20*/:
                return "URSH";
            case ADD /*21*/:
                return "ADD";
            case SUB /*22*/:
                return "SUB";
            case MUL /*23*/:
                return "MUL";
            case DIV /*24*/:
                return "DIV";
            case MOD /*25*/:
                return "MOD";
            case NOT /*26*/:
                return "NOT";
            case BITNOT /*27*/:
                return "BITNOT";
            case POS /*28*/:
                return "POS";
            case NEG /*29*/:
                return "NEG";
            case NEW /*30*/:
                return "NEW";
            case DELPROP /*31*/:
                return "DELPROP";
            case TYPEOF /*32*/:
                return "TYPEOF";
            case GETPROP /*33*/:
                return "GETPROP";
            case GETPROPNOWARN /*34*/:
                return "GETPROPNOWARN";
            case SETPROP /*35*/:
                return "SETPROP";
            case GETELEM /*36*/:
                return "GETELEM";
            case SETELEM /*37*/:
                return "SETELEM";
            case CALL /*38*/:
                return "CALL";
            case NAME /*39*/:
                return "NAME";
            case NUMBER /*40*/:
                return "NUMBER";
            case STRING /*41*/:
                return "STRING";
            case NULL /*42*/:
                return "NULL";
            case THIS /*43*/:
                return "THIS";
            case FALSE /*44*/:
                return "FALSE";
            case TRUE /*45*/:
                return "TRUE";
            case SHEQ /*46*/:
                return "SHEQ";
            case SHNE /*47*/:
                return "SHNE";
            case REGEXP /*48*/:
                return "REGEXP";
            case BINDNAME /*49*/:
                return "BINDNAME";
            case THROW /*50*/:
                return "THROW";
            case RETHROW /*51*/:
                return "RETHROW";
            case IN /*52*/:
                return "IN";
            case INSTANCEOF /*53*/:
                return "INSTANCEOF";
            case LOCAL_LOAD /*54*/:
                return "LOCAL_LOAD";
            case GETVAR /*55*/:
                return "GETVAR";
            case SETVAR /*56*/:
                return "SETVAR";
            case CATCH_SCOPE /*57*/:
                return "CATCH_SCOPE";
            case ENUM_INIT_KEYS /*58*/:
                return "ENUM_INIT_KEYS";
            case ENUM_INIT_VALUES /*59*/:
                return "ENUM_INIT_VALUES";
            case ENUM_INIT_ARRAY /*60*/:
                return "ENUM_INIT_ARRAY";
            case ENUM_INIT_VALUES_IN_ORDER /*61*/:
                return "ENUM_INIT_VALUES_IN_ORDER";
            case ENUM_NEXT /*62*/:
                return "ENUM_NEXT";
            case ENUM_ID /*63*/:
                return "ENUM_ID";
            case THISFN /*64*/:
                return "THISFN";
            case RETURN_RESULT /*65*/:
                return "RETURN_RESULT";
            case ARRAYLIT /*66*/:
                return "ARRAYLIT";
            case OBJECTLIT /*67*/:
                return "OBJECTLIT";
            case GET_REF /*68*/:
                return "GET_REF";
            case SET_REF /*69*/:
                return "SET_REF";
            case DEL_REF /*70*/:
                return "DEL_REF";
            case REF_CALL /*71*/:
                return "REF_CALL";
            case REF_SPECIAL /*72*/:
                return "REF_SPECIAL";
            case YIELD /*73*/:
                return "YIELD";
            case DEFAULTNAMESPACE /*75*/:
                return "DEFAULTNAMESPACE";
            case ESCXMLATTR /*76*/:
                return "ESCXMLATTR";
            case ESCXMLTEXT /*77*/:
                return "ESCXMLTEXT";
            case REF_MEMBER /*78*/:
                return "REF_MEMBER";
            case REF_NS_MEMBER /*79*/:
                return "REF_NS_MEMBER";
            case REF_NAME /*80*/:
                return "REF_NAME";
            case REF_NS_NAME /*81*/:
                return "REF_NS_NAME";
            case TRY /*82*/:
                return "TRY";
            case SEMI /*83*/:
                return "SEMI";
            case LB /*84*/:
                return "LB";
            case RB /*85*/:
                return "RB";
            case LC /*86*/:
                return "LC";
            case RC /*87*/:
                return "RC";
            case LP /*88*/:
                return "LP";
            case RP /*89*/:
                return "RP";
            case COMMA /*90*/:
                return "COMMA";
            case FIRST_ASSIGN /*91*/:
                return "ASSIGN";
            case ASSIGN_BITOR /*92*/:
                return "ASSIGN_BITOR";
            case ASSIGN_BITXOR /*93*/:
                return "ASSIGN_BITXOR";
            case ASSIGN_BITAND /*94*/:
                return "ASSIGN_BITAND";
            case ASSIGN_LSH /*95*/:
                return "ASSIGN_LSH";
            case ASSIGN_RSH /*96*/:
                return "ASSIGN_RSH";
            case ASSIGN_URSH /*97*/:
                return "ASSIGN_URSH";
            case ASSIGN_ADD /*98*/:
                return "ASSIGN_ADD";
            case ASSIGN_SUB /*99*/:
                return "ASSIGN_SUB";
            case ASSIGN_MUL /*100*/:
                return "ASSIGN_MUL";
            case ASSIGN_DIV /*101*/:
                return "ASSIGN_DIV";
            case LAST_ASSIGN /*102*/:
                return "ASSIGN_MOD";
            case HOOK /*103*/:
                return "HOOK";
            case COLON /*104*/:
                return "COLON";
            case OR /*105*/:
                return "OR";
            case AND /*106*/:
                return "AND";
            case INC /*107*/:
                return "INC";
            case DEC /*108*/:
                return "DEC";
            case DOT /*109*/:
                return "DOT";
            case FUNCTION /*110*/:
                return "FUNCTION";
            case EXPORT /*111*/:
                return "EXPORT";
            case IMPORT /*112*/:
                return "IMPORT";
            case IF /*113*/:
                return "IF";
            case ELSE /*114*/:
                return "ELSE";
            case SWITCH /*115*/:
                return "SWITCH";
            case CASE /*116*/:
                return "CASE";
            case DEFAULT /*117*/:
                return "DEFAULT";
            case WHILE /*118*/:
                return "WHILE";
            case DO /*119*/:
                return "DO";
            case FOR /*120*/:
                return "FOR";
            case BREAK /*121*/:
                return "BREAK";
            case CONTINUE /*122*/:
                return "CONTINUE";
            case VAR /*123*/:
                return "VAR";
            case WITH /*124*/:
                return "WITH";
            case CATCH /*125*/:
                return "CATCH";
            case FINALLY /*126*/:
                return "FINALLY";
            case VOID /*127*/:
                return "VOID";
            case RESERVED /*128*/:
                return "RESERVED";
            case EMPTY /*129*/:
                return "EMPTY";
            case BLOCK /*130*/:
                return "BLOCK";
            case LABEL /*131*/:
                return "LABEL";
            case TARGET /*132*/:
                return "TARGET";
            case LOOP /*133*/:
                return "LOOP";
            case EXPR_VOID /*134*/:
                return "EXPR_VOID";
            case EXPR_RESULT /*135*/:
                return "EXPR_RESULT";
            case JSR /*136*/:
                return "JSR";
            case SCRIPT /*137*/:
                return "SCRIPT";
            case TYPEOFNAME /*138*/:
                return "TYPEOFNAME";
            case USE_STACK /*139*/:
                return "USE_STACK";
            case SETPROP_OP /*140*/:
                return "SETPROP_OP";
            case SETELEM_OP /*141*/:
                return "SETELEM_OP";
            case LOCAL_BLOCK /*142*/:
                return "LOCAL_BLOCK";
            case SET_REF_OP /*143*/:
                return "SET_REF_OP";
            case DOTDOT /*144*/:
                return "DOTDOT";
            case COLONCOLON /*145*/:
                return "COLONCOLON";
            case XML /*146*/:
                return "XML";
            case DOTQUERY /*147*/:
                return "DOTQUERY";
            case XMLATTR /*148*/:
                return "XMLATTR";
            case XMLEND /*149*/:
                return "XMLEND";
            case TO_OBJECT /*150*/:
                return "TO_OBJECT";
            case TO_DOUBLE /*151*/:
                return "TO_DOUBLE";
            case GET /*152*/:
                return HttpEngine.GET;
            case SET /*153*/:
                return "SET";
            case LET /*154*/:
                return "LET";
            case CONST /*155*/:
                return "CONST";
            case SETCONST /*156*/:
                return "SETCONST";
            case ARRAYCOMP /*158*/:
                return "ARRAYCOMP";
            case LETEXPR /*159*/:
                return "LETEXPR";
            case WITHEXPR /*160*/:
                return "WITHEXPR";
            case DEBUGGER /*161*/:
                return "DEBUGGER";
            case COMMENT /*162*/:
                return "COMMENT";
            case GENEXPR /*163*/:
                return "GENEXPR";
            case METHOD /*164*/:
                return "METHOD";
            case ARROW /*165*/:
                return "ARROW";
            default:
                throw new IllegalStateException(String.valueOf(i));
        }
    }
}
