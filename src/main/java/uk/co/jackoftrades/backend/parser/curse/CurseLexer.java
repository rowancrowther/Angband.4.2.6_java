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
 *    Java code copyright (c) Rowan Crowther 2026
 */

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Curse.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.curse;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class CurseLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            T__0 = 1, T__1 = 2, COMMENT = 3, EOL = 4, NAME = 5, TYPE = 6, WEIGHT = 7, COMBAT = 8,
            EFFECT = 9, DICE = 10, EXPR = 11, TIME = 12, FLAGS = 13, VALUES = 14, MSG = 15, DESC = 16,
            CONFLICT = 17, CONFLICT_FLAGS = 18, STRING = 19, COLON = 20, LBRACKET = 21, RBRACKET = 22;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "T__0", "T__1", "COMMENT", "EOL", "NAME", "TYPE", "WEIGHT", "COMBAT",
                "EFFECT", "DICE", "EXPR", "TIME", "FLAGS", "VALUES", "MSG", "DESC", "CONFLICT",
                "CONFLICT_FLAGS", "STRING", "COLON", "LBRACKET", "RBRACKET"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "' | '", "'|'", null, null, "'name:'", "'type:'", "'weight:'",
                "'combat:'", "'effect:'", "'dice:'", "'expr:'", "'time:'", "'flags:'",
                "'values:'", "'msg:'", "'desc:'", "'conflict:'", "'conflict-flags:'",
                null, "':'", "'['", "']'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, null, null, "COMMENT", "EOL", "NAME", "TYPE", "WEIGHT", "COMBAT",
                "EFFECT", "DICE", "EXPR", "TIME", "FLAGS", "VALUES", "MSG", "DESC", "CONFLICT",
                "CONFLICT_FLAGS", "STRING", "COLON", "LBRACKET", "RBRACKET"
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


    public CurseLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "Curse.g4";
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
            "\u0004\u0000\u0016\u00c3\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002" +
                    "\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002" +
                    "\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002" +
                    "\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002" +
                    "\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e" +
                    "\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011" +
                    "\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014" +
                    "\u0002\u0015\u0007\u0015\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0005\u00026\b\u0002" +
                    "\n\u0002\f\u00029\t\u0002\u0001\u0002\u0004\u0002<\b\u0002\u000b\u0002" +
                    "\f\u0002=\u0001\u0002\u0001\u0002\u0001\u0003\u0005\u0003C\b\u0003\n\u0003" +
                    "\f\u0003F\t\u0003\u0001\u0003\u0003\u0003I\b\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001" +
                    "\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001" +
                    "\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001" +
                    "\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001" +
                    "\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001" +
                    "\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001" +
                    "\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001" +
                    "\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001" +
                    "\u0011\u0001\u0012\u0004\u0012\u00ba\b\u0012\u000b\u0012\f\u0012\u00bb" +
                    "\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015" +
                    "\u0000\u0000\u0016\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005" +
                    "\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0019" +
                    "\r\u001b\u000e\u001d\u000f\u001f\u0010!\u0011#\u0012%\u0013\'\u0014)\u0015" +
                    "+\u0016\u0001\u0000\u0002\u0001\u0000\n\n\u0007\u0000 !$$+.09AZ__az\u00c7" +
                    "\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000" +
                    "\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000" +
                    "\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000" +
                    "\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011" +
                    "\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015" +
                    "\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019" +
                    "\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d" +
                    "\u0001\u0000\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0000!\u0001" +
                    "\u0000\u0000\u0000\u0000#\u0001\u0000\u0000\u0000\u0000%\u0001\u0000\u0000" +
                    "\u0000\u0000\'\u0001\u0000\u0000\u0000\u0000)\u0001\u0000\u0000\u0000" +
                    "\u0000+\u0001\u0000\u0000\u0000\u0001-\u0001\u0000\u0000\u0000\u00031" +
                    "\u0001\u0000\u0000\u0000\u00053\u0001\u0000\u0000\u0000\u0007D\u0001\u0000" +
                    "\u0000\u0000\tN\u0001\u0000\u0000\u0000\u000bT\u0001\u0000\u0000\u0000" +
                    "\rZ\u0001\u0000\u0000\u0000\u000fb\u0001\u0000\u0000\u0000\u0011j\u0001" +
                    "\u0000\u0000\u0000\u0013r\u0001\u0000\u0000\u0000\u0015x\u0001\u0000\u0000" +
                    "\u0000\u0017~\u0001\u0000\u0000\u0000\u0019\u0084\u0001\u0000\u0000\u0000" +
                    "\u001b\u008b\u0001\u0000\u0000\u0000\u001d\u0093\u0001\u0000\u0000\u0000" +
                    "\u001f\u0098\u0001\u0000\u0000\u0000!\u009e\u0001\u0000\u0000\u0000#\u00a8" +
                    "\u0001\u0000\u0000\u0000%\u00b9\u0001\u0000\u0000\u0000\'\u00bd\u0001" +
                    "\u0000\u0000\u0000)\u00bf\u0001\u0000\u0000\u0000+\u00c1\u0001\u0000\u0000" +
                    "\u0000-.\u0005 \u0000\u0000./\u0005|\u0000\u0000/0\u0005 \u0000\u0000" +
                    "0\u0002\u0001\u0000\u0000\u000012\u0005|\u0000\u00002\u0004\u0001\u0000" +
                    "\u0000\u000037\u0005#\u0000\u000046\b\u0000\u0000\u000054\u0001\u0000" +
                    "\u0000\u000069\u0001\u0000\u0000\u000075\u0001\u0000\u0000\u000078\u0001" +
                    "\u0000\u0000\u00008;\u0001\u0000\u0000\u000097\u0001\u0000\u0000\u0000" +
                    ":<\u0005\n\u0000\u0000;:\u0001\u0000\u0000\u0000<=\u0001\u0000\u0000\u0000" +
                    "=;\u0001\u0000\u0000\u0000=>\u0001\u0000\u0000\u0000>?\u0001\u0000\u0000" +
                    "\u0000?@\u0006\u0002\u0000\u0000@\u0006\u0001\u0000\u0000\u0000AC\u0005" +
                    " \u0000\u0000BA\u0001\u0000\u0000\u0000CF\u0001\u0000\u0000\u0000DB\u0001" +
                    "\u0000\u0000\u0000DE\u0001\u0000\u0000\u0000EH\u0001\u0000\u0000\u0000" +
                    "FD\u0001\u0000\u0000\u0000GI\u0005\r\u0000\u0000HG\u0001\u0000\u0000\u0000" +
                    "HI\u0001\u0000\u0000\u0000IJ\u0001\u0000\u0000\u0000JK\u0005\n\u0000\u0000" +
                    "KL\u0001\u0000\u0000\u0000LM\u0006\u0003\u0000\u0000M\b\u0001\u0000\u0000" +
                    "\u0000NO\u0005n\u0000\u0000OP\u0005a\u0000\u0000PQ\u0005m\u0000\u0000" +
                    "QR\u0005e\u0000\u0000RS\u0005:\u0000\u0000S\n\u0001\u0000\u0000\u0000" +
                    "TU\u0005t\u0000\u0000UV\u0005y\u0000\u0000VW\u0005p\u0000\u0000WX\u0005" +
                    "e\u0000\u0000XY\u0005:\u0000\u0000Y\f\u0001\u0000\u0000\u0000Z[\u0005" +
                    "w\u0000\u0000[\\\u0005e\u0000\u0000\\]\u0005i\u0000\u0000]^\u0005g\u0000" +
                    "\u0000^_\u0005h\u0000\u0000_`\u0005t\u0000\u0000`a\u0005:\u0000\u0000" +
                    "a\u000e\u0001\u0000\u0000\u0000bc\u0005c\u0000\u0000cd\u0005o\u0000\u0000" +
                    "de\u0005m\u0000\u0000ef\u0005b\u0000\u0000fg\u0005a\u0000\u0000gh\u0005" +
                    "t\u0000\u0000hi\u0005:\u0000\u0000i\u0010\u0001\u0000\u0000\u0000jk\u0005" +
                    "e\u0000\u0000kl\u0005f\u0000\u0000lm\u0005f\u0000\u0000mn\u0005e\u0000" +
                    "\u0000no\u0005c\u0000\u0000op\u0005t\u0000\u0000pq\u0005:\u0000\u0000" +
                    "q\u0012\u0001\u0000\u0000\u0000rs\u0005d\u0000\u0000st\u0005i\u0000\u0000" +
                    "tu\u0005c\u0000\u0000uv\u0005e\u0000\u0000vw\u0005:\u0000\u0000w\u0014" +
                    "\u0001\u0000\u0000\u0000xy\u0005e\u0000\u0000yz\u0005x\u0000\u0000z{\u0005" +
                    "p\u0000\u0000{|\u0005r\u0000\u0000|}\u0005:\u0000\u0000}\u0016\u0001\u0000" +
                    "\u0000\u0000~\u007f\u0005t\u0000\u0000\u007f\u0080\u0005i\u0000\u0000" +
                    "\u0080\u0081\u0005m\u0000\u0000\u0081\u0082\u0005e\u0000\u0000\u0082\u0083" +
                    "\u0005:\u0000\u0000\u0083\u0018\u0001\u0000\u0000\u0000\u0084\u0085\u0005" +
                    "f\u0000\u0000\u0085\u0086\u0005l\u0000\u0000\u0086\u0087\u0005a\u0000" +
                    "\u0000\u0087\u0088\u0005g\u0000\u0000\u0088\u0089\u0005s\u0000\u0000\u0089" +
                    "\u008a\u0005:\u0000\u0000\u008a\u001a\u0001\u0000\u0000\u0000\u008b\u008c" +
                    "\u0005v\u0000\u0000\u008c\u008d\u0005a\u0000\u0000\u008d\u008e\u0005l" +
                    "\u0000\u0000\u008e\u008f\u0005u\u0000\u0000\u008f\u0090\u0005e\u0000\u0000" +
                    "\u0090\u0091\u0005s\u0000\u0000\u0091\u0092\u0005:\u0000\u0000\u0092\u001c" +
                    "\u0001\u0000\u0000\u0000\u0093\u0094\u0005m\u0000\u0000\u0094\u0095\u0005" +
                    "s\u0000\u0000\u0095\u0096\u0005g\u0000\u0000\u0096\u0097\u0005:\u0000" +
                    "\u0000\u0097\u001e\u0001\u0000\u0000\u0000\u0098\u0099\u0005d\u0000\u0000" +
                    "\u0099\u009a\u0005e\u0000\u0000\u009a\u009b\u0005s\u0000\u0000\u009b\u009c" +
                    "\u0005c\u0000\u0000\u009c\u009d\u0005:\u0000\u0000\u009d \u0001\u0000" +
                    "\u0000\u0000\u009e\u009f\u0005c\u0000\u0000\u009f\u00a0\u0005o\u0000\u0000" +
                    "\u00a0\u00a1\u0005n\u0000\u0000\u00a1\u00a2\u0005f\u0000\u0000\u00a2\u00a3" +
                    "\u0005l\u0000\u0000\u00a3\u00a4\u0005i\u0000\u0000\u00a4\u00a5\u0005c" +
                    "\u0000\u0000\u00a5\u00a6\u0005t\u0000\u0000\u00a6\u00a7\u0005:\u0000\u0000" +
                    "\u00a7\"\u0001\u0000\u0000\u0000\u00a8\u00a9\u0005c\u0000\u0000\u00a9" +
                    "\u00aa\u0005o\u0000\u0000\u00aa\u00ab\u0005n\u0000\u0000\u00ab\u00ac\u0005" +
                    "f\u0000\u0000\u00ac\u00ad\u0005l\u0000\u0000\u00ad\u00ae\u0005i\u0000" +
                    "\u0000\u00ae\u00af\u0005c\u0000\u0000\u00af\u00b0\u0005t\u0000\u0000\u00b0" +
                    "\u00b1\u0005-\u0000\u0000\u00b1\u00b2\u0005f\u0000\u0000\u00b2\u00b3\u0005" +
                    "l\u0000\u0000\u00b3\u00b4\u0005a\u0000\u0000\u00b4\u00b5\u0005g\u0000" +
                    "\u0000\u00b5\u00b6\u0005s\u0000\u0000\u00b6\u00b7\u0005:\u0000\u0000\u00b7" +
                    "$\u0001\u0000\u0000\u0000\u00b8\u00ba\u0007\u0001\u0000\u0000\u00b9\u00b8" +
                    "\u0001\u0000\u0000\u0000\u00ba\u00bb\u0001\u0000\u0000\u0000\u00bb\u00b9" +
                    "\u0001\u0000\u0000\u0000\u00bb\u00bc\u0001\u0000\u0000\u0000\u00bc&\u0001" +
                    "\u0000\u0000\u0000\u00bd\u00be\u0005:\u0000\u0000\u00be(\u0001\u0000\u0000" +
                    "\u0000\u00bf\u00c0\u0005[\u0000\u0000\u00c0*\u0001\u0000\u0000\u0000\u00c1" +
                    "\u00c2\u0005]\u0000\u0000\u00c2,\u0001\u0000\u0000\u0000\u0006\u00007" +
                    "=DH\u00bb\u0001\u0006\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}