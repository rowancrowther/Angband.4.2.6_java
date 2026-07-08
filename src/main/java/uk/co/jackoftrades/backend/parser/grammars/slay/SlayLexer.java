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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/SlayLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.slay;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class SlayLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, CODE = 2, NAME = 3, RACE_FLAG = 4, BASE = 5, MULTIPLIER = 6, O_MULTIPLIER = 7,
            POWER = 8, MELEE_VERB = 9, RANGE_VERB = 10, INTEGER = 11, COMMENT = 12, EOL = 13,
            FLAG = 14, STRING = 15;
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
                "RECORD_COUNT", "CODE", "NAME", "RACE_FLAG", "BASE", "MULTIPLIER", "O_MULTIPLIER",
                "POWER", "MELEE_VERB", "RANGE_VERB", "INTEGER", "COMMENT", "EOL", "FLAG_BODY",
                "FLAG", "STRING"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'code:'", "'name:'", "'race-flag:'", "'base:'",
                "'multiplier:'", "'o-multiplier:'", "'power:'", "'melee-verb:'", "'range-verb:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "CODE", "NAME", "RACE_FLAG", "BASE", "MULTIPLIER",
                "O_MULTIPLIER", "POWER", "MELEE_VERB", "RANGE_VERB", "INTEGER", "COMMENT",
                "EOL", "FLAG", "STRING"
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


    public SlayLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "SlayLexer.g4";
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
            "\u0004\u0000\u000f\u00c5\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff" +
                    "\uffff\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007" +
                    "\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007" +
                    "\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b" +
                    "\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007" +
                    "\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001" +
                    "\t\u0001\n\u0003\n\u0095\b\n\u0001\n\u0004\n\u0098\b\n\u000b\n\f\n\u0099" +
                    "\u0001\u000b\u0001\u000b\u0005\u000b\u009e\b\u000b\n\u000b\f\u000b\u00a1" +
                    "\t\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0005" +
                    "\f\u00a8\b\f\n\f\f\f\u00ab\t\f\u0001\f\u0003\f\u00ae\b\f\u0001\f\u0001" +
                    "\f\u0001\f\u0001\f\u0001\r\u0001\r\u0005\r\u00b6\b\r\n\r\f\r\u00b9\t\r" +
                    "\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0004\u000f" +
                    "\u00c0\b\u000f\u000b\u000f\f\u000f\u00c1\u0001\u000f\u0001\u000f\u0000" +
                    "\u0000\u0010\u0003\u0001\u0005\u0002\u0007\u0003\t\u0004\u000b\u0005\r" +
                    "\u0006\u000f\u0007\u0011\b\u0013\t\u0015\n\u0017\u000b\u0019\f\u001b\r" +
                    "\u001d\u0000\u001f\u000e!\u000f\u0003\u0000\u0001\u0002\u0003\u0001\u0000" +
                    "\n\n\u0003\u000009AZ__\u0002\u0000\n\n\r\r\u00c8\u0000\u0003\u0001\u0000" +
                    "\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000" +
                    "\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000" +
                    "\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000" +
                    "\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000" +
                    "\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000" +
                    "\u0000\u0019\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000" +
                    "\u0001\u001f\u0001\u0000\u0000\u0000\u0002!\u0001\u0000\u0000\u0000\u0003" +
                    "#\u0001\u0000\u0000\u0000\u00051\u0001\u0000\u0000\u0000\u00079\u0001" +
                    "\u0000\u0000\u0000\tA\u0001\u0000\u0000\u0000\u000bN\u0001\u0000\u0000" +
                    "\u0000\rV\u0001\u0000\u0000\u0000\u000fb\u0001\u0000\u0000\u0000\u0011" +
                    "p\u0001\u0000\u0000\u0000\u0013w\u0001\u0000\u0000\u0000\u0015\u0085\u0001" +
                    "\u0000\u0000\u0000\u0017\u0094\u0001\u0000\u0000\u0000\u0019\u009b\u0001" +
                    "\u0000\u0000\u0000\u001b\u00a9\u0001\u0000\u0000\u0000\u001d\u00b3\u0001" +
                    "\u0000\u0000\u0000\u001f\u00ba\u0001\u0000\u0000\u0000!\u00bf\u0001\u0000" +
                    "\u0000\u0000#$\u0005r\u0000\u0000$%\u0005e\u0000\u0000%&\u0005c\u0000" +
                    "\u0000&\'\u0005o\u0000\u0000\'(\u0005r\u0000\u0000()\u0005d\u0000\u0000" +
                    ")*\u0005-\u0000\u0000*+\u0005c\u0000\u0000+,\u0005o\u0000\u0000,-\u0005" +
                    "u\u0000\u0000-.\u0005n\u0000\u0000./\u0005t\u0000\u0000/0\u0005:\u0000" +
                    "\u00000\u0004\u0001\u0000\u0000\u000012\u0005c\u0000\u000023\u0005o\u0000" +
                    "\u000034\u0005d\u0000\u000045\u0005e\u0000\u000056\u0005:\u0000\u0000" +
                    "67\u0001\u0000\u0000\u000078\u0006\u0001\u0000\u00008\u0006\u0001\u0000" +
                    "\u0000\u00009:\u0005n\u0000\u0000:;\u0005a\u0000\u0000;<\u0005m\u0000" +
                    "\u0000<=\u0005e\u0000\u0000=>\u0005:\u0000\u0000>?\u0001\u0000\u0000\u0000" +
                    "?@\u0006\u0002\u0000\u0000@\b\u0001\u0000\u0000\u0000AB\u0005r\u0000\u0000" +
                    "BC\u0005a\u0000\u0000CD\u0005c\u0000\u0000DE\u0005e\u0000\u0000EF\u0005" +
                    "-\u0000\u0000FG\u0005f\u0000\u0000GH\u0005l\u0000\u0000HI\u0005a\u0000" +
                    "\u0000IJ\u0005g\u0000\u0000JK\u0005:\u0000\u0000KL\u0001\u0000\u0000\u0000" +
                    "LM\u0006\u0003\u0001\u0000M\n\u0001\u0000\u0000\u0000NO\u0005b\u0000\u0000" +
                    "OP\u0005a\u0000\u0000PQ\u0005s\u0000\u0000QR\u0005e\u0000\u0000RS\u0005" +
                    ":\u0000\u0000ST\u0001\u0000\u0000\u0000TU\u0006\u0004\u0000\u0000U\f\u0001" +
                    "\u0000\u0000\u0000VW\u0005m\u0000\u0000WX\u0005u\u0000\u0000XY\u0005l" +
                    "\u0000\u0000YZ\u0005t\u0000\u0000Z[\u0005i\u0000\u0000[\\\u0005p\u0000" +
                    "\u0000\\]\u0005l\u0000\u0000]^\u0005i\u0000\u0000^_\u0005e\u0000\u0000" +
                    "_`\u0005r\u0000\u0000`a\u0005:\u0000\u0000a\u000e\u0001\u0000\u0000\u0000" +
                    "bc\u0005o\u0000\u0000cd\u0005-\u0000\u0000de\u0005m\u0000\u0000ef\u0005" +
                    "u\u0000\u0000fg\u0005l\u0000\u0000gh\u0005t\u0000\u0000hi\u0005i\u0000" +
                    "\u0000ij\u0005p\u0000\u0000jk\u0005l\u0000\u0000kl\u0005i\u0000\u0000" +
                    "lm\u0005e\u0000\u0000mn\u0005r\u0000\u0000no\u0005:\u0000\u0000o\u0010" +
                    "\u0001\u0000\u0000\u0000pq\u0005p\u0000\u0000qr\u0005o\u0000\u0000rs\u0005" +
                    "w\u0000\u0000st\u0005e\u0000\u0000tu\u0005r\u0000\u0000uv\u0005:\u0000" +
                    "\u0000v\u0012\u0001\u0000\u0000\u0000wx\u0005m\u0000\u0000xy\u0005e\u0000" +
                    "\u0000yz\u0005l\u0000\u0000z{\u0005e\u0000\u0000{|\u0005e\u0000\u0000" +
                    "|}\u0005-\u0000\u0000}~\u0005v\u0000\u0000~\u007f\u0005e\u0000\u0000\u007f" +
                    "\u0080\u0005r\u0000\u0000\u0080\u0081\u0005b\u0000\u0000\u0081\u0082\u0005" +
                    ":\u0000\u0000\u0082\u0083\u0001\u0000\u0000\u0000\u0083\u0084\u0006\b" +
                    "\u0000\u0000\u0084\u0014\u0001\u0000\u0000\u0000\u0085\u0086\u0005r\u0000" +
                    "\u0000\u0086\u0087\u0005a\u0000\u0000\u0087\u0088\u0005n\u0000\u0000\u0088" +
                    "\u0089\u0005g\u0000\u0000\u0089\u008a\u0005e\u0000\u0000\u008a\u008b\u0005" +
                    "-\u0000\u0000\u008b\u008c\u0005v\u0000\u0000\u008c\u008d\u0005e\u0000" +
                    "\u0000\u008d\u008e\u0005r\u0000\u0000\u008e\u008f\u0005b\u0000\u0000\u008f" +
                    "\u0090\u0005:\u0000\u0000\u0090\u0091\u0001\u0000\u0000\u0000\u0091\u0092" +
                    "\u0006\t\u0000\u0000\u0092\u0016\u0001\u0000\u0000\u0000\u0093\u0095\u0005" +
                    "-\u0000\u0000\u0094\u0093\u0001\u0000\u0000\u0000\u0094\u0095\u0001\u0000" +
                    "\u0000\u0000\u0095\u0097\u0001\u0000\u0000\u0000\u0096\u0098\u000209\u0000" +
                    "\u0097\u0096\u0001\u0000\u0000\u0000\u0098\u0099\u0001\u0000\u0000\u0000" +
                    "\u0099\u0097\u0001\u0000\u0000\u0000\u0099\u009a\u0001\u0000\u0000\u0000" +
                    "\u009a\u0018\u0001\u0000\u0000\u0000\u009b\u009f\u0005#\u0000\u0000\u009c" +
                    "\u009e\b\u0000\u0000\u0000\u009d\u009c\u0001\u0000\u0000\u0000\u009e\u00a1" +
                    "\u0001\u0000\u0000\u0000\u009f\u009d\u0001\u0000\u0000\u0000\u009f\u00a0" +
                    "\u0001\u0000\u0000\u0000\u00a0\u00a2\u0001\u0000\u0000\u0000\u00a1\u009f" +
                    "\u0001\u0000\u0000\u0000\u00a2\u00a3\u0005\n\u0000\u0000\u00a3\u00a4\u0001" +
                    "\u0000\u0000\u0000\u00a4\u00a5\u0006\u000b\u0002\u0000\u00a5\u001a\u0001" +
                    "\u0000\u0000\u0000\u00a6\u00a8\u0005 \u0000\u0000\u00a7\u00a6\u0001\u0000" +
                    "\u0000\u0000\u00a8\u00ab\u0001\u0000\u0000\u0000\u00a9\u00a7\u0001\u0000" +
                    "\u0000\u0000\u00a9\u00aa\u0001\u0000\u0000\u0000\u00aa\u00ad\u0001\u0000" +
                    "\u0000\u0000\u00ab\u00a9\u0001\u0000\u0000\u0000\u00ac\u00ae\u0005\r\u0000" +
                    "\u0000\u00ad\u00ac\u0001\u0000\u0000\u0000\u00ad\u00ae\u0001\u0000\u0000" +
                    "\u0000\u00ae\u00af\u0001\u0000\u0000\u0000\u00af\u00b0\u0005\n\u0000\u0000" +
                    "\u00b0\u00b1\u0001\u0000\u0000\u0000\u00b1\u00b2\u0006\f\u0002\u0000\u00b2" +
                    "\u001c\u0001\u0000\u0000\u0000\u00b3\u00b7\u0002AZ\u0000\u00b4\u00b6\u0007" +
                    "\u0001\u0000\u0000\u00b5\u00b4\u0001\u0000\u0000\u0000\u00b6\u00b9\u0001" +
                    "\u0000\u0000\u0000\u00b7\u00b5\u0001\u0000\u0000\u0000\u00b7\u00b8\u0001" +
                    "\u0000\u0000\u0000\u00b8\u001e\u0001\u0000\u0000\u0000\u00b9\u00b7\u0001" +
                    "\u0000\u0000\u0000\u00ba\u00bb\u0003\u001d\r\u0000\u00bb\u00bc\u0001\u0000" +
                    "\u0000\u0000\u00bc\u00bd\u0006\u000e\u0003\u0000\u00bd \u0001\u0000\u0000" +
                    "\u0000\u00be\u00c0\b\u0002\u0000\u0000\u00bf\u00be\u0001\u0000\u0000\u0000" +
                    "\u00c0\u00c1\u0001\u0000\u0000\u0000\u00c1\u00bf\u0001\u0000\u0000\u0000" +
                    "\u00c1\u00c2\u0001\u0000\u0000\u0000\u00c2\u00c3\u0001\u0000\u0000\u0000" +
                    "\u00c3\u00c4\u0006\u000f\u0003\u0000\u00c4\"\u0001\u0000\u0000\u0000\n" +
                    "\u0000\u0001\u0002\u0094\u0099\u009f\u00a9\u00ad\u00b7\u00c1\u0004\u0005" +
                    "\u0002\u0000\u0005\u0001\u0000\u0006\u0000\u0000\u0004\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}