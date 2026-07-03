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

// Generated from src/main/java/uk/co/jackoftrades/backend/parser/grammars/UIEntryBaseLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.uientrybase;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class UIEntryBaseLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, NAME = 2, RENDERER = 3, COMBINE = 4, CATEGORY = 5, FLAGS = 6, DESC = 7,
            INTEGER = 8, LCASE = 9, COMMENT = 10, EOL = 11, FLAG = 12, OR = 13, FLAG_EOL = 14, STRING = 15;
    public static final int
            FLAG_MODE = 1, FREE_TEXT_MODE = 2;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE", "FLAG_MODE", "FREE_TEXT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "RECORD_COUNT", "NAME", "RENDERER", "COMBINE", "CATEGORY", "FLAGS", "DESC",
                "INTEGER", "LCASE", "COMMENT", "EOL", "FLAG_BODY", "FLAG", "OR", "FLAG_EOL",
                "STRING"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'name:'", "'renderer:'", "'combine:'", "'category:'",
                "'flags:'", "'desc:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "NAME", "RENDERER", "COMBINE", "CATEGORY", "FLAGS",
                "DESC", "INTEGER", "LCASE", "COMMENT", "EOL", "FLAG", "OR", "FLAG_EOL",
                "STRING"
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


    public UIEntryBaseLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "UIEntryBaseLexer.g4";
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
            "\u0004\u0000\u000f\u00b3\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff" +
                    "\uffff\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007" +
                    "\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007" +
                    "\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b" +
                    "\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007" +
                    "\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0003\u0007k\b" +
                    "\u0007\u0001\u0007\u0004\u0007n\b\u0007\u000b\u0007\f\u0007o\u0001\b\u0004" +
                    "\bs\b\b\u000b\b\f\bt\u0001\t\u0001\t\u0005\ty\b\t\n\t\f\t|\t\t\u0001\t" +
                    "\u0001\t\u0001\t\u0001\t\u0001\n\u0005\n\u0083\b\n\n\n\f\n\u0086\t\n\u0001" +
                    "\n\u0003\n\u0089\b\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001" +
                    "\u000b\u0005\u000b\u0091\b\u000b\n\u000b\f\u000b\u0094\t\u000b\u0001\f" +
                    "\u0001\f\u0001\r\u0003\r\u0099\b\r\u0001\r\u0001\r\u0003\r\u009d\b\r\u0001" +
                    "\u000e\u0005\u000e\u00a0\b\u000e\n\u000e\f\u000e\u00a3\t\u000e\u0001\u000e" +
                    "\u0003\u000e\u00a6\b\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e" +
                    "\u0001\u000e\u0001\u000f\u0004\u000f\u00ae\b\u000f\u000b\u000f\f\u000f" +
                    "\u00af\u0001\u000f\u0001\u000f\u0000\u0000\u0010\u0003\u0001\u0005\u0002" +
                    "\u0007\u0003\t\u0004\u000b\u0005\r\u0006\u000f\u0007\u0011\b\u0013\t\u0015" +
                    "\n\u0017\u000b\u0019\u0000\u001b\f\u001d\r\u001f\u000e!\u000f\u0003\u0000" +
                    "\u0001\u0002\u0004\u0003\u000009__az\u0001\u0000\n\n\u0003\u000009AZ_" +
                    "_\u0002\u0000\n\n\r\r\u00bb\u0000\u0003\u0001\u0000\u0000\u0000\u0000" +
                    "\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000" +
                    "\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r" +
                    "\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011" +
                    "\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015" +
                    "\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0001\u001b" +
                    "\u0001\u0000\u0000\u0000\u0001\u001d\u0001\u0000\u0000\u0000\u0001\u001f" +
                    "\u0001\u0000\u0000\u0000\u0002!\u0001\u0000\u0000\u0000\u0003#\u0001\u0000" +
                    "\u0000\u0000\u00051\u0001\u0000\u0000\u0000\u00077\u0001\u0000\u0000\u0000" +
                    "\tA\u0001\u0000\u0000\u0000\u000bL\u0001\u0000\u0000\u0000\rX\u0001\u0000" +
                    "\u0000\u0000\u000fa\u0001\u0000\u0000\u0000\u0011j\u0001\u0000\u0000\u0000" +
                    "\u0013r\u0001\u0000\u0000\u0000\u0015v\u0001\u0000\u0000\u0000\u0017\u0084" +
                    "\u0001\u0000\u0000\u0000\u0019\u008e\u0001\u0000\u0000\u0000\u001b\u0095" +
                    "\u0001\u0000\u0000\u0000\u001d\u0098\u0001\u0000\u0000\u0000\u001f\u00a1" +
                    "\u0001\u0000\u0000\u0000!\u00ad\u0001\u0000\u0000\u0000#$\u0005r\u0000" +
                    "\u0000$%\u0005e\u0000\u0000%&\u0005c\u0000\u0000&\'\u0005o\u0000\u0000" +
                    "\'(\u0005r\u0000\u0000()\u0005d\u0000\u0000)*\u0005-\u0000\u0000*+\u0005" +
                    "c\u0000\u0000+,\u0005o\u0000\u0000,-\u0005u\u0000\u0000-.\u0005n\u0000" +
                    "\u0000./\u0005t\u0000\u0000/0\u0005:\u0000\u00000\u0004\u0001\u0000\u0000" +
                    "\u000012\u0005n\u0000\u000023\u0005a\u0000\u000034\u0005m\u0000\u0000" +
                    "45\u0005e\u0000\u000056\u0005:\u0000\u00006\u0006\u0001\u0000\u0000\u0000" +
                    "78\u0005r\u0000\u000089\u0005e\u0000\u00009:\u0005n\u0000\u0000:;\u0005" +
                    "d\u0000\u0000;<\u0005e\u0000\u0000<=\u0005r\u0000\u0000=>\u0005e\u0000" +
                    "\u0000>?\u0005r\u0000\u0000?@\u0005:\u0000\u0000@\b\u0001\u0000\u0000" +
                    "\u0000AB\u0005c\u0000\u0000BC\u0005o\u0000\u0000CD\u0005m\u0000\u0000" +
                    "DE\u0005b\u0000\u0000EF\u0005i\u0000\u0000FG\u0005n\u0000\u0000GH\u0005" +
                    "e\u0000\u0000HI\u0005:\u0000\u0000IJ\u0001\u0000\u0000\u0000JK\u0006\u0003" +
                    "\u0000\u0000K\n\u0001\u0000\u0000\u0000LM\u0005c\u0000\u0000MN\u0005a" +
                    "\u0000\u0000NO\u0005t\u0000\u0000OP\u0005e\u0000\u0000PQ\u0005g\u0000" +
                    "\u0000QR\u0005o\u0000\u0000RS\u0005r\u0000\u0000ST\u0005y\u0000\u0000" +
                    "TU\u0005:\u0000\u0000UV\u0001\u0000\u0000\u0000VW\u0006\u0004\u0001\u0000" +
                    "W\f\u0001\u0000\u0000\u0000XY\u0005f\u0000\u0000YZ\u0005l\u0000\u0000" +
                    "Z[\u0005a\u0000\u0000[\\\u0005g\u0000\u0000\\]\u0005s\u0000\u0000]^\u0005" +
                    ":\u0000\u0000^_\u0001\u0000\u0000\u0000_`\u0006\u0005\u0000\u0000`\u000e" +
                    "\u0001\u0000\u0000\u0000ab\u0005d\u0000\u0000bc\u0005e\u0000\u0000cd\u0005" +
                    "s\u0000\u0000de\u0005c\u0000\u0000ef\u0005:\u0000\u0000fg\u0001\u0000" +
                    "\u0000\u0000gh\u0006\u0006\u0001\u0000h\u0010\u0001\u0000\u0000\u0000" +
                    "ik\u0005-\u0000\u0000ji\u0001\u0000\u0000\u0000jk\u0001\u0000\u0000\u0000" +
                    "km\u0001\u0000\u0000\u0000ln\u000209\u0000ml\u0001\u0000\u0000\u0000n" +
                    "o\u0001\u0000\u0000\u0000om\u0001\u0000\u0000\u0000op\u0001\u0000\u0000" +
                    "\u0000p\u0012\u0001\u0000\u0000\u0000qs\u0007\u0000\u0000\u0000rq\u0001" +
                    "\u0000\u0000\u0000st\u0001\u0000\u0000\u0000tr\u0001\u0000\u0000\u0000" +
                    "tu\u0001\u0000\u0000\u0000u\u0014\u0001\u0000\u0000\u0000vz\u0005#\u0000" +
                    "\u0000wy\b\u0001\u0000\u0000xw\u0001\u0000\u0000\u0000y|\u0001\u0000\u0000" +
                    "\u0000zx\u0001\u0000\u0000\u0000z{\u0001\u0000\u0000\u0000{}\u0001\u0000" +
                    "\u0000\u0000|z\u0001\u0000\u0000\u0000}~\u0005\n\u0000\u0000~\u007f\u0001" +
                    "\u0000\u0000\u0000\u007f\u0080\u0006\t\u0002\u0000\u0080\u0016\u0001\u0000" +
                    "\u0000\u0000\u0081\u0083\u0005 \u0000\u0000\u0082\u0081\u0001\u0000\u0000" +
                    "\u0000\u0083\u0086\u0001\u0000\u0000\u0000\u0084\u0082\u0001\u0000\u0000" +
                    "\u0000\u0084\u0085\u0001\u0000\u0000\u0000\u0085\u0088\u0001\u0000\u0000" +
                    "\u0000\u0086\u0084\u0001\u0000\u0000\u0000\u0087\u0089\u0005\r\u0000\u0000" +
                    "\u0088\u0087\u0001\u0000\u0000\u0000\u0088\u0089\u0001\u0000\u0000\u0000" +
                    "\u0089\u008a\u0001\u0000\u0000\u0000\u008a\u008b\u0005\n\u0000\u0000\u008b" +
                    "\u008c\u0001\u0000\u0000\u0000\u008c\u008d\u0006\n\u0002\u0000\u008d\u0018" +
                    "\u0001\u0000\u0000\u0000\u008e\u0092\u0002AZ\u0000\u008f\u0091\u0007\u0002" +
                    "\u0000\u0000\u0090\u008f\u0001\u0000\u0000\u0000\u0091\u0094\u0001\u0000" +
                    "\u0000\u0000\u0092\u0090\u0001\u0000\u0000\u0000\u0092\u0093\u0001\u0000" +
                    "\u0000\u0000\u0093\u001a\u0001\u0000\u0000\u0000\u0094\u0092\u0001\u0000" +
                    "\u0000\u0000\u0095\u0096\u0003\u0019\u000b\u0000\u0096\u001c\u0001\u0000" +
                    "\u0000\u0000\u0097\u0099\u0005 \u0000\u0000\u0098\u0097\u0001\u0000\u0000" +
                    "\u0000\u0098\u0099\u0001\u0000\u0000\u0000\u0099\u009a\u0001\u0000\u0000" +
                    "\u0000\u009a\u009c\u0005|\u0000\u0000\u009b\u009d\u0005 \u0000\u0000\u009c" +
                    "\u009b\u0001\u0000\u0000\u0000\u009c\u009d\u0001\u0000\u0000\u0000\u009d" +
                    "\u001e\u0001\u0000\u0000\u0000\u009e\u00a0\u0005 \u0000\u0000\u009f\u009e" +
                    "\u0001\u0000\u0000\u0000\u00a0\u00a3\u0001\u0000\u0000\u0000\u00a1\u009f" +
                    "\u0001\u0000\u0000\u0000\u00a1\u00a2\u0001\u0000\u0000\u0000\u00a2\u00a5" +
                    "\u0001\u0000\u0000\u0000\u00a3\u00a1\u0001\u0000\u0000\u0000\u00a4\u00a6" +
                    "\u0005\r\u0000\u0000\u00a5\u00a4\u0001\u0000\u0000\u0000\u00a5\u00a6\u0001" +
                    "\u0000\u0000\u0000\u00a6\u00a7\u0001\u0000\u0000\u0000\u00a7\u00a8\u0005" +
                    "\n\u0000\u0000\u00a8\u00a9\u0001\u0000\u0000\u0000\u00a9\u00aa\u0006\u000e" +
                    "\u0002\u0000\u00aa\u00ab\u0006\u000e\u0003\u0000\u00ab \u0001\u0000\u0000" +
                    "\u0000\u00ac\u00ae\b\u0003\u0000\u0000\u00ad\u00ac\u0001\u0000\u0000\u0000" +
                    "\u00ae\u00af\u0001\u0000\u0000\u0000\u00af\u00ad\u0001\u0000\u0000\u0000" +
                    "\u00af\u00b0\u0001\u0000\u0000\u0000\u00b0\u00b1\u0001\u0000\u0000\u0000" +
                    "\u00b1\u00b2\u0006\u000f\u0003\u0000\u00b2\"\u0001\u0000\u0000\u0000\u000f" +
                    "\u0000\u0001\u0002jotz\u0084\u0088\u0092\u0098\u009c\u00a1\u00a5\u00af" +
                    "\u0004\u0005\u0001\u0000\u0005\u0002\u0000\u0006\u0000\u0000\u0004\u0000" +
                    "\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}