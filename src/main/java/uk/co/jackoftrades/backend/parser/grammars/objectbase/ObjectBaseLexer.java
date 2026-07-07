/*
 * Copyright (c) 1987-2022 Angband contributors.
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of either:
 *
 * a) the GNU General Public License as published by the Free Software
 *    Foundation, version 2, or
 *
 * b) the Angband licence:
 *    This software may be copied and distributed for educational, research,
 *    and not for profit purposes provided that this copyright and statement
 *    are included in all such copies.  Other copyrights may also apply.
 *
 *    Java code and ANTLR4 grammars copyright (c) Rowan Crowther 2026
 */

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/ObjectBaseLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.objectbase;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ObjectBaseLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, DEFAULT_MAX_STACK = 2, DEFAULT_BREAK_CHANCE = 3, NAME = 4, GRAPHICS = 5,
            BREAK = 6, MAX_STACK = 7, FLAGS = 8, INTEGER = 9, COMMENT = 10, EOL = 11, T_VAL = 12,
            T_VAL_COLON = 13, NAME_EOL = 14, STRING = 15, STRING_EOL = 16, FLAG = 17, FLAG_OR = 18,
            FLAG_EOL = 19;
    public static final int
            NAME_TVAL = 1, FREE_TEXT_MODE = 2, FLAG_MODE = 3;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE", "NAME_TVAL", "FREE_TEXT_MODE", "FLAG_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "RECORD_COUNT", "DEFAULT_MAX_STACK", "DEFAULT_BREAK_CHANCE", "NAME",
                "GRAPHICS", "BREAK", "MAX_STACK", "FLAGS", "INTEGER", "COMMENT", "EOL",
                "FLAG_BODY", "T_VAL", "T_VAL_COLON", "NAME_EOL", "STRING", "STRING_EOL",
                "FLAG", "FLAG_OR", "FLAG_EOL"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'default:max-stack:'", "'default:break-chance:'",
                "'name:'", "'graphics:'", "'break:'", "'max-stack:'", "'flags:'", null,
                null, null, null, "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "DEFAULT_MAX_STACK", "DEFAULT_BREAK_CHANCE", "NAME",
                "GRAPHICS", "BREAK", "MAX_STACK", "FLAGS", "INTEGER", "COMMENT", "EOL",
                "T_VAL", "T_VAL_COLON", "NAME_EOL", "STRING", "STRING_EOL", "FLAG", "FLAG_OR",
                "FLAG_EOL"
        };
    }

    private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
    public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

    /**
     * @deprecated Use {@link #VOCABULARY} instead.
     */
    @Deprecated
    public static final String[] tokenNames;

    static {
        tokenNames = new String[_SYMBOLIC_NAMES.length];
        for (int i = 0; i < tokenNames.length; i++) {
            tokenNames[i] = VOCABULARY.getLiteralName(i);
            if (tokenNames[i] == null) {
                tokenNames[i] = VOCABULARY.getSymbolicName(i);
            }

            if (tokenNames[i] == null) {
                tokenNames[i] = "<INVALID>";
            }
        }
    }

    @Override
    @Deprecated
    public String[] getTokenNames() {
        return tokenNames;
    }

    @Override

    public Vocabulary getVocabulary() {
        return VOCABULARY;
    }


    public ObjectBaseLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "ObjectBaseLexer.g4";
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public String getSerializedATN() {
        return _serializedATN;
    }

    @Override
    public String[] getChannelNames() {
        return channelNames;
    }

    @Override
    public String[] getModeNames() {
        return modeNames;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public static final String _serializedATN =
            "\u0004\u0000\u0013\u00e6\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff" +
                    "\uffff\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001" +
                    "\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004" +
                    "\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007" +
                    "\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b" +
                    "\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007" +
                    "\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007" +
                    "\u0012\u0002\u0013\u0007\u0013\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\b\u0003\b\u0094\b\b\u0001\b\u0004" +
                    "\b\u0097\b\b\u000b\b\f\b\u0098\u0001\t\u0001\t\u0005\t\u009d\b\t\n\t\f" +
                    "\t\u00a0\t\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0005\n\u00a7\b\n" +
                    "\n\n\f\n\u00aa\t\n\u0001\n\u0003\n\u00ad\b\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\u000b\u0001\u000b\u0005\u000b\u00b5\b\u000b\n\u000b\f\u000b\u00b8" +
                    "\t\u000b\u0001\f\u0004\f\u00bb\b\f\u000b\f\f\f\u00bc\u0001\r\u0001\r\u0001" +
                    "\r\u0001\r\u0001\u000e\u0003\u000e\u00c4\b\u000e\u0001\u000e\u0001\u000e" +
                    "\u0001\u000e\u0001\u000e\u0001\u000f\u0004\u000f\u00cb\b\u000f\u000b\u000f" +
                    "\f\u000f\u00cc\u0001\u0010\u0003\u0010\u00d0\b\u0010\u0001\u0010\u0001" +
                    "\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001" +
                    "\u0012\u0003\u0012\u00da\b\u0012\u0001\u0012\u0001\u0012\u0003\u0012\u00de" +
                    "\b\u0012\u0001\u0013\u0003\u0013\u00e1\b\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0000\u0000\u0014\u0004\u0001\u0006\u0002\b\u0003" +
                    "\n\u0004\f\u0005\u000e\u0006\u0010\u0007\u0012\b\u0014\t\u0016\n\u0018" +
                    "\u000b\u001a\u0000\u001c\f\u001e\r \u000e\"\u000f$\u0010&\u0011(\u0012" +
                    "*\u0013\u0004\u0000\u0001\u0002\u0003\u0004\u0001\u0000\n\n\u0003\u0000" +
                    "09AZ__\u0003\u0000\n\n\r\r::\u0002\u0000\n\n\r\r\u00ee\u0000\u0004\u0001" +
                    "\u0000\u0000\u0000\u0000\u0006\u0001\u0000\u0000\u0000\u0000\b\u0001\u0000" +
                    "\u0000\u0000\u0000\n\u0001\u0000\u0000\u0000\u0000\f\u0001\u0000\u0000" +
                    "\u0000\u0000\u000e\u0001\u0000\u0000\u0000\u0000\u0010\u0001\u0000\u0000" +
                    "\u0000\u0000\u0012\u0001\u0000\u0000\u0000\u0000\u0014\u0001\u0000\u0000" +
                    "\u0000\u0000\u0016\u0001\u0000\u0000\u0000\u0000\u0018\u0001\u0000\u0000" +
                    "\u0000\u0001\u001c\u0001\u0000\u0000\u0000\u0001\u001e\u0001\u0000\u0000" +
                    "\u0000\u0001 \u0001\u0000\u0000\u0000\u0002\"\u0001\u0000\u0000\u0000" +
                    "\u0002$\u0001\u0000\u0000\u0000\u0003&\u0001\u0000\u0000\u0000\u0003(" +
                    "\u0001\u0000\u0000\u0000\u0003*\u0001\u0000\u0000\u0000\u0004,\u0001\u0000" +
                    "\u0000\u0000\u0006:\u0001\u0000\u0000\u0000\bM\u0001\u0000\u0000\u0000" +
                    "\nc\u0001\u0000\u0000\u0000\fk\u0001\u0000\u0000\u0000\u000ew\u0001\u0000" +
                    "\u0000\u0000\u0010~\u0001\u0000\u0000\u0000\u0012\u0089\u0001\u0000\u0000" +
                    "\u0000\u0014\u0093\u0001\u0000\u0000\u0000\u0016\u009a\u0001\u0000\u0000" +
                    "\u0000\u0018\u00a8\u0001\u0000\u0000\u0000\u001a\u00b2\u0001\u0000\u0000" +
                    "\u0000\u001c\u00ba\u0001\u0000\u0000\u0000\u001e\u00be\u0001\u0000\u0000" +
                    "\u0000 \u00c3\u0001\u0000\u0000\u0000\"\u00ca\u0001\u0000\u0000\u0000" +
                    "$\u00cf\u0001\u0000\u0000\u0000&\u00d6\u0001\u0000\u0000\u0000(\u00d9" +
                    "\u0001\u0000\u0000\u0000*\u00e0\u0001\u0000\u0000\u0000,-\u0005r\u0000" +
                    "\u0000-.\u0005e\u0000\u0000./\u0005c\u0000\u0000/0\u0005o\u0000\u0000" +
                    "01\u0005r\u0000\u000012\u0005d\u0000\u000023\u0005-\u0000\u000034\u0005" +
                    "c\u0000\u000045\u0005o\u0000\u000056\u0005u\u0000\u000067\u0005n\u0000" +
                    "\u000078\u0005t\u0000\u000089\u0005:\u0000\u00009\u0005\u0001\u0000\u0000" +
                    "\u0000:;\u0005d\u0000\u0000;<\u0005e\u0000\u0000<=\u0005f\u0000\u0000" +
                    "=>\u0005a\u0000\u0000>?\u0005u\u0000\u0000?@\u0005l\u0000\u0000@A\u0005" +
                    "t\u0000\u0000AB\u0005:\u0000\u0000BC\u0005m\u0000\u0000CD\u0005a\u0000" +
                    "\u0000DE\u0005x\u0000\u0000EF\u0005-\u0000\u0000FG\u0005s\u0000\u0000" +
                    "GH\u0005t\u0000\u0000HI\u0005a\u0000\u0000IJ\u0005c\u0000\u0000JK\u0005" +
                    "k\u0000\u0000KL\u0005:\u0000\u0000L\u0007\u0001\u0000\u0000\u0000MN\u0005" +
                    "d\u0000\u0000NO\u0005e\u0000\u0000OP\u0005f\u0000\u0000PQ\u0005a\u0000" +
                    "\u0000QR\u0005u\u0000\u0000RS\u0005l\u0000\u0000ST\u0005t\u0000\u0000" +
                    "TU\u0005:\u0000\u0000UV\u0005b\u0000\u0000VW\u0005r\u0000\u0000WX\u0005" +
                    "e\u0000\u0000XY\u0005a\u0000\u0000YZ\u0005k\u0000\u0000Z[\u0005-\u0000" +
                    "\u0000[\\\u0005c\u0000\u0000\\]\u0005h\u0000\u0000]^\u0005a\u0000\u0000" +
                    "^_\u0005n\u0000\u0000_`\u0005c\u0000\u0000`a\u0005e\u0000\u0000ab\u0005" +
                    ":\u0000\u0000b\t\u0001\u0000\u0000\u0000cd\u0005n\u0000\u0000de\u0005" +
                    "a\u0000\u0000ef\u0005m\u0000\u0000fg\u0005e\u0000\u0000gh\u0005:\u0000" +
                    "\u0000hi\u0001\u0000\u0000\u0000ij\u0006\u0003\u0000\u0000j\u000b\u0001" +
                    "\u0000\u0000\u0000kl\u0005g\u0000\u0000lm\u0005r\u0000\u0000mn\u0005a" +
                    "\u0000\u0000no\u0005p\u0000\u0000op\u0005h\u0000\u0000pq\u0005i\u0000" +
                    "\u0000qr\u0005c\u0000\u0000rs\u0005s\u0000\u0000st\u0005:\u0000\u0000" +
                    "tu\u0001\u0000\u0000\u0000uv\u0006\u0004\u0001\u0000v\r\u0001\u0000\u0000" +
                    "\u0000wx\u0005b\u0000\u0000xy\u0005r\u0000\u0000yz\u0005e\u0000\u0000" +
                    "z{\u0005a\u0000\u0000{|\u0005k\u0000\u0000|}\u0005:\u0000\u0000}\u000f" +
                    "\u0001\u0000\u0000\u0000~\u007f\u0005m\u0000\u0000\u007f\u0080\u0005a" +
                    "\u0000\u0000\u0080\u0081\u0005x\u0000\u0000\u0081\u0082\u0005-\u0000\u0000" +
                    "\u0082\u0083\u0005s\u0000\u0000\u0083\u0084\u0005t\u0000\u0000\u0084\u0085" +
                    "\u0005a\u0000\u0000\u0085\u0086\u0005c\u0000\u0000\u0086\u0087\u0005k" +
                    "\u0000\u0000\u0087\u0088\u0005:\u0000\u0000\u0088\u0011\u0001\u0000\u0000" +
                    "\u0000\u0089\u008a\u0005f\u0000\u0000\u008a\u008b\u0005l\u0000\u0000\u008b" +
                    "\u008c\u0005a\u0000\u0000\u008c\u008d\u0005g\u0000\u0000\u008d\u008e\u0005" +
                    "s\u0000\u0000\u008e\u008f\u0005:\u0000\u0000\u008f\u0090\u0001\u0000\u0000" +
                    "\u0000\u0090\u0091\u0006\u0007\u0002\u0000\u0091\u0013\u0001\u0000\u0000" +
                    "\u0000\u0092\u0094\u0005-\u0000\u0000\u0093\u0092\u0001\u0000\u0000\u0000" +
                    "\u0093\u0094\u0001\u0000\u0000\u0000\u0094\u0096\u0001\u0000\u0000\u0000" +
                    "\u0095\u0097\u000209\u0000\u0096\u0095\u0001\u0000\u0000\u0000\u0097\u0098" +
                    "\u0001\u0000\u0000\u0000\u0098\u0096\u0001\u0000\u0000\u0000\u0098\u0099" +
                    "\u0001\u0000\u0000\u0000\u0099\u0015\u0001\u0000\u0000\u0000\u009a\u009e" +
                    "\u0005#\u0000\u0000\u009b\u009d\b\u0000\u0000\u0000\u009c\u009b\u0001" +
                    "\u0000\u0000\u0000\u009d\u00a0\u0001\u0000\u0000\u0000\u009e\u009c\u0001" +
                    "\u0000\u0000\u0000\u009e\u009f\u0001\u0000\u0000\u0000\u009f\u00a1\u0001" +
                    "\u0000\u0000\u0000\u00a0\u009e\u0001\u0000\u0000\u0000\u00a1\u00a2\u0005" +
                    "\n\u0000\u0000\u00a2\u00a3\u0001\u0000\u0000\u0000\u00a3\u00a4\u0006\t" +
                    "\u0003\u0000\u00a4\u0017\u0001\u0000\u0000\u0000\u00a5\u00a7\u0005 \u0000" +
                    "\u0000\u00a6\u00a5\u0001\u0000\u0000\u0000\u00a7\u00aa\u0001\u0000\u0000" +
                    "\u0000\u00a8\u00a6\u0001\u0000\u0000\u0000\u00a8\u00a9\u0001\u0000\u0000" +
                    "\u0000\u00a9\u00ac\u0001\u0000\u0000\u0000\u00aa\u00a8\u0001\u0000\u0000" +
                    "\u0000\u00ab\u00ad\u0005\r\u0000\u0000\u00ac\u00ab\u0001\u0000\u0000\u0000" +
                    "\u00ac\u00ad\u0001\u0000\u0000\u0000\u00ad\u00ae\u0001\u0000\u0000\u0000" +
                    "\u00ae\u00af\u0005\n\u0000\u0000\u00af\u00b0\u0001\u0000\u0000\u0000\u00b0" +
                    "\u00b1\u0006\n\u0003\u0000\u00b1\u0019\u0001\u0000\u0000\u0000\u00b2\u00b6" +
                    "\u0002AZ\u0000\u00b3\u00b5\u0007\u0001\u0000\u0000\u00b4\u00b3\u0001\u0000" +
                    "\u0000\u0000\u00b5\u00b8\u0001\u0000\u0000\u0000\u00b6\u00b4\u0001\u0000" +
                    "\u0000\u0000\u00b6\u00b7\u0001\u0000\u0000\u0000\u00b7\u001b\u0001\u0000" +
                    "\u0000\u0000\u00b8\u00b6\u0001\u0000\u0000\u0000\u00b9\u00bb\b\u0002\u0000" +
                    "\u0000\u00ba\u00b9\u0001\u0000\u0000\u0000\u00bb\u00bc\u0001\u0000\u0000" +
                    "\u0000\u00bc\u00ba\u0001\u0000\u0000\u0000\u00bc\u00bd\u0001\u0000\u0000" +
                    "\u0000\u00bd\u001d\u0001\u0000\u0000\u0000\u00be\u00bf\u0005:\u0000\u0000" +
                    "\u00bf\u00c0\u0001\u0000\u0000\u0000\u00c0\u00c1\u0006\r\u0001\u0000\u00c1" +
                    "\u001f\u0001\u0000\u0000\u0000\u00c2\u00c4\u0005\r\u0000\u0000\u00c3\u00c2" +
                    "\u0001\u0000\u0000\u0000\u00c3\u00c4\u0001\u0000\u0000\u0000\u00c4\u00c5" +
                    "\u0001\u0000\u0000\u0000\u00c5\u00c6\u0005\n\u0000\u0000\u00c6\u00c7\u0001" +
                    "\u0000\u0000\u0000\u00c7\u00c8\u0006\u000e\u0004\u0000\u00c8!\u0001\u0000" +
                    "\u0000\u0000\u00c9\u00cb\b\u0003\u0000\u0000\u00ca\u00c9\u0001\u0000\u0000" +
                    "\u0000\u00cb\u00cc\u0001\u0000\u0000\u0000\u00cc\u00ca\u0001\u0000\u0000" +
                    "\u0000\u00cc\u00cd\u0001\u0000\u0000\u0000\u00cd#\u0001\u0000\u0000\u0000" +
                    "\u00ce\u00d0\u0005\r\u0000\u0000\u00cf\u00ce\u0001\u0000\u0000\u0000\u00cf" +
                    "\u00d0\u0001\u0000\u0000\u0000\u00d0\u00d1\u0001\u0000\u0000\u0000\u00d1" +
                    "\u00d2\u0005\n\u0000\u0000\u00d2\u00d3\u0001\u0000\u0000\u0000\u00d3\u00d4" +
                    "\u0006\u0010\u0003\u0000\u00d4\u00d5\u0006\u0010\u0004\u0000\u00d5%\u0001" +
                    "\u0000\u0000\u0000\u00d6\u00d7\u0003\u001a\u000b\u0000\u00d7\'\u0001\u0000" +
                    "\u0000\u0000\u00d8\u00da\u0005 \u0000\u0000\u00d9\u00d8\u0001\u0000\u0000" +
                    "\u0000\u00d9\u00da\u0001\u0000\u0000\u0000\u00da\u00db\u0001\u0000\u0000" +
                    "\u0000\u00db\u00dd\u0005|\u0000\u0000\u00dc\u00de\u0005 \u0000\u0000\u00dd" +
                    "\u00dc\u0001\u0000\u0000\u0000\u00dd\u00de\u0001\u0000\u0000\u0000\u00de" +
                    ")\u0001\u0000\u0000\u0000\u00df\u00e1\u0005\r\u0000\u0000\u00e0\u00df" +
                    "\u0001\u0000\u0000\u0000\u00e0\u00e1\u0001\u0000\u0000\u0000\u00e1\u00e2" +
                    "\u0001\u0000\u0000\u0000\u00e2\u00e3\u0005\n\u0000\u0000\u00e3\u00e4\u0001" +
                    "\u0000\u0000\u0000\u00e4\u00e5\u0006\u0013\u0005\u0000\u00e5+\u0001\u0000" +
                    "\u0000\u0000\u0011\u0000\u0001\u0002\u0003\u0093\u0098\u009e\u00a8\u00ac" +
                    "\u00b6\u00bc\u00c3\u00cc\u00cf\u00d9\u00dd\u00e0\u0006\u0002\u0001\u0000" +
                    "\u0002\u0002\u0000\u0005\u0003\u0000\u0006\u0000\u0000\u0002\u0000\u0000" +
                    "\u0004\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}