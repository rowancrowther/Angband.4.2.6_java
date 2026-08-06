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

// Generated from DungeonProfileLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.dungeonprofile;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class DungeonProfileLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, NAME = 2, PARAMS = 3, TUNNEL = 4, STREAMER = 5, ROOM = 6, MIN_LEVEL = 7,
            ALLOC = 8, INTEGER = 9, COLON = 10, COMMENT = 11, EOL = 12, TEXT_BETWEEN_COLON = 13,
            DELIMITER = 14, STRING = 15, END_OF_MODE = 16;
    public static final int
            DELIMITED_TEXT = 1, REST_OF_LINE = 2;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE", "DELIMITED_TEXT", "REST_OF_LINE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "RECORD_COUNT", "NAME", "PARAMS", "TUNNEL", "STREAMER", "ROOM", "MIN_LEVEL",
                "ALLOC", "INTEGER", "COLON", "COMMENT", "EOL", "TEXT_BETWEEN_COLON",
                "DELIMITER", "STRING", "END_OF_MODE"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'name:'", "'params:'", "'tunnel:'", "'streamer:'",
                "'room:'", "'min-level:'", "'alloc:'", null, "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "NAME", "PARAMS", "TUNNEL", "STREAMER", "ROOM",
                "MIN_LEVEL", "ALLOC", "INTEGER", "COLON", "COMMENT", "EOL", "TEXT_BETWEEN_COLON",
                "DELIMITER", "STRING", "END_OF_MODE"
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


    public DungeonProfileLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "DungeonProfileLexer.g4";
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
            "\u0004\u0000\u0010\u00a9\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff" +
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
                    "\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\b\u0003\bo\b\b\u0001\b\u0004\br\b\b\u000b\b\f" +
                    "\bs\u0001\t\u0001\t\u0001\n\u0001\n\u0005\nz\b\n\n\n\f\n}\t\n\u0001\n" +
                    "\u0001\n\u0001\n\u0001\n\u0001\u000b\u0005\u000b\u0084\b\u000b\n\u000b" +
                    "\f\u000b\u0087\t\u000b\u0001\u000b\u0003\u000b\u008a\b\u000b\u0001\u000b" +
                    "\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0004\f\u0091\b\f\u000b\f" +
                    "\f\f\u0092\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0004\u000e" +
                    "\u009b\b\u000e\u000b\u000e\f\u000e\u009c\u0001\u000f\u0005\u000f\u00a0" +
                    "\b\u000f\n\u000f\f\u000f\u00a3\t\u000f\u0001\u000f\u0001\u000f\u0001\u000f" +
                    "\u0001\u000f\u0001\u000f\u0000\u0000\u0010\u0003\u0001\u0005\u0002\u0007" +
                    "\u0003\t\u0004\u000b\u0005\r\u0006\u000f\u0007\u0011\b\u0013\t\u0015\n" +
                    "\u0017\u000b\u0019\f\u001b\r\u001d\u000e\u001f\u000f!\u0010\u0003\u0000" +
                    "\u0001\u0002\u0003\u0001\u0000\n\n\u0003\u0000\n\n\r\r::\u0002\u0000\n" +
                    "\n\r\r\u00ae\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000" +
                    "\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000" +
                    "\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000" +
                    "\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000" +
                    "\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000" +
                    "\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000" +
                    "\u0001\u001b\u0001\u0000\u0000\u0000\u0001\u001d\u0001\u0000\u0000\u0000" +
                    "\u0002\u001f\u0001\u0000\u0000\u0000\u0002!\u0001\u0000\u0000\u0000\u0003" +
                    "#\u0001\u0000\u0000\u0000\u00051\u0001\u0000\u0000\u0000\u00079\u0001" +
                    "\u0000\u0000\u0000\tA\u0001\u0000\u0000\u0000\u000bI\u0001\u0000\u0000" +
                    "\u0000\rS\u0001\u0000\u0000\u0000\u000f[\u0001\u0000\u0000\u0000\u0011" +
                    "f\u0001\u0000\u0000\u0000\u0013n\u0001\u0000\u0000\u0000\u0015u\u0001" +
                    "\u0000\u0000\u0000\u0017w\u0001\u0000\u0000\u0000\u0019\u0085\u0001\u0000" +
                    "\u0000\u0000\u001b\u0090\u0001\u0000\u0000\u0000\u001d\u0094\u0001\u0000" +
                    "\u0000\u0000\u001f\u009a\u0001\u0000\u0000\u0000!\u00a1\u0001\u0000\u0000" +
                    "\u0000#$\u0005r\u0000\u0000$%\u0005e\u0000\u0000%&\u0005c\u0000\u0000" +
                    "&\'\u0005o\u0000\u0000\'(\u0005r\u0000\u0000()\u0005d\u0000\u0000)*\u0005" +
                    "-\u0000\u0000*+\u0005c\u0000\u0000+,\u0005o\u0000\u0000,-\u0005u\u0000" +
                    "\u0000-.\u0005n\u0000\u0000./\u0005t\u0000\u0000/0\u0005:\u0000\u0000" +
                    "0\u0004\u0001\u0000\u0000\u000012\u0005n\u0000\u000023\u0005a\u0000\u0000" +
                    "34\u0005m\u0000\u000045\u0005e\u0000\u000056\u0005:\u0000\u000067\u0001" +
                    "\u0000\u0000\u000078\u0006\u0001\u0000\u00008\u0006\u0001\u0000\u0000" +
                    "\u00009:\u0005p\u0000\u0000:;\u0005a\u0000\u0000;<\u0005r\u0000\u0000" +
                    "<=\u0005a\u0000\u0000=>\u0005m\u0000\u0000>?\u0005s\u0000\u0000?@\u0005" +
                    ":\u0000\u0000@\b\u0001\u0000\u0000\u0000AB\u0005t\u0000\u0000BC\u0005" +
                    "u\u0000\u0000CD\u0005n\u0000\u0000DE\u0005n\u0000\u0000EF\u0005e\u0000" +
                    "\u0000FG\u0005l\u0000\u0000GH\u0005:\u0000\u0000H\n\u0001\u0000\u0000" +
                    "\u0000IJ\u0005s\u0000\u0000JK\u0005t\u0000\u0000KL\u0005r\u0000\u0000" +
                    "LM\u0005e\u0000\u0000MN\u0005a\u0000\u0000NO\u0005m\u0000\u0000OP\u0005" +
                    "e\u0000\u0000PQ\u0005r\u0000\u0000QR\u0005:\u0000\u0000R\f\u0001\u0000" +
                    "\u0000\u0000ST\u0005r\u0000\u0000TU\u0005o\u0000\u0000UV\u0005o\u0000" +
                    "\u0000VW\u0005m\u0000\u0000WX\u0005:\u0000\u0000XY\u0001\u0000\u0000\u0000" +
                    "YZ\u0006\u0005\u0001\u0000Z\u000e\u0001\u0000\u0000\u0000[\\\u0005m\u0000" +
                    "\u0000\\]\u0005i\u0000\u0000]^\u0005n\u0000\u0000^_\u0005-\u0000\u0000" +
                    "_`\u0005l\u0000\u0000`a\u0005e\u0000\u0000ab\u0005v\u0000\u0000bc\u0005" +
                    "e\u0000\u0000cd\u0005l\u0000\u0000de\u0005:\u0000\u0000e\u0010\u0001\u0000" +
                    "\u0000\u0000fg\u0005a\u0000\u0000gh\u0005l\u0000\u0000hi\u0005l\u0000" +
                    "\u0000ij\u0005o\u0000\u0000jk\u0005c\u0000\u0000kl\u0005:\u0000\u0000" +
                    "l\u0012\u0001\u0000\u0000\u0000mo\u0005-\u0000\u0000nm\u0001\u0000\u0000" +
                    "\u0000no\u0001\u0000\u0000\u0000oq\u0001\u0000\u0000\u0000pr\u000209\u0000" +
                    "qp\u0001\u0000\u0000\u0000rs\u0001\u0000\u0000\u0000sq\u0001\u0000\u0000" +
                    "\u0000st\u0001\u0000\u0000\u0000t\u0014\u0001\u0000\u0000\u0000uv\u0005" +
                    ":\u0000\u0000v\u0016\u0001\u0000\u0000\u0000w{\u0005#\u0000\u0000xz\b" +
                    "\u0000\u0000\u0000yx\u0001\u0000\u0000\u0000z}\u0001\u0000\u0000\u0000" +
                    "{y\u0001\u0000\u0000\u0000{|\u0001\u0000\u0000\u0000|~\u0001\u0000\u0000" +
                    "\u0000}{\u0001\u0000\u0000\u0000~\u007f\u0005\n\u0000\u0000\u007f\u0080" +
                    "\u0001\u0000\u0000\u0000\u0080\u0081\u0006\n\u0002\u0000\u0081\u0018\u0001" +
                    "\u0000\u0000\u0000\u0082\u0084\u0005 \u0000\u0000\u0083\u0082\u0001\u0000" +
                    "\u0000\u0000\u0084\u0087\u0001\u0000\u0000\u0000\u0085\u0083\u0001\u0000" +
                    "\u0000\u0000\u0085\u0086\u0001\u0000\u0000\u0000\u0086\u0089\u0001\u0000" +
                    "\u0000\u0000\u0087\u0085\u0001\u0000\u0000\u0000\u0088\u008a\u0005\r\u0000" +
                    "\u0000\u0089\u0088\u0001\u0000\u0000\u0000\u0089\u008a\u0001\u0000\u0000" +
                    "\u0000\u008a\u008b\u0001\u0000\u0000\u0000\u008b\u008c\u0005\n\u0000\u0000" +
                    "\u008c\u008d\u0001\u0000\u0000\u0000\u008d\u008e\u0006\u000b\u0002\u0000" +
                    "\u008e\u001a\u0001\u0000\u0000\u0000\u008f\u0091\b\u0001\u0000\u0000\u0090" +
                    "\u008f\u0001\u0000\u0000\u0000\u0091\u0092\u0001\u0000\u0000\u0000\u0092" +
                    "\u0090\u0001\u0000\u0000\u0000\u0092\u0093\u0001\u0000\u0000\u0000\u0093" +
                    "\u001c\u0001\u0000\u0000\u0000\u0094\u0095\u0007\u0001\u0000\u0000\u0095" +
                    "\u0096\u0001\u0000\u0000\u0000\u0096\u0097\u0006\r\u0002\u0000\u0097\u0098" +
                    "\u0006\r\u0003\u0000\u0098\u001e\u0001\u0000\u0000\u0000\u0099\u009b\b" +
                    "\u0002\u0000\u0000\u009a\u0099\u0001\u0000\u0000\u0000\u009b\u009c\u0001" +
                    "\u0000\u0000\u0000\u009c\u009a\u0001\u0000\u0000\u0000\u009c\u009d\u0001" +
                    "\u0000\u0000\u0000\u009d \u0001\u0000\u0000\u0000\u009e\u00a0\u0005\r" +
                    "\u0000\u0000\u009f\u009e\u0001\u0000\u0000\u0000\u00a0\u00a3\u0001\u0000" +
                    "\u0000\u0000\u00a1\u009f\u0001\u0000\u0000\u0000\u00a1\u00a2\u0001\u0000" +
                    "\u0000\u0000\u00a2\u00a4\u0001\u0000\u0000\u0000\u00a3\u00a1\u0001\u0000" +
                    "\u0000\u0000\u00a4\u00a5\u0005\n\u0000\u0000\u00a5\u00a6\u0001\u0000\u0000" +
                    "\u0000\u00a6\u00a7\u0006\u000f\u0002\u0000\u00a7\u00a8\u0006\u000f\u0003" +
                    "\u0000\u00a8\"\u0001\u0000\u0000\u0000\u000b\u0000\u0001\u0002ns{\u0085" +
                    "\u0089\u0092\u009c\u00a1\u0004\u0005\u0002\u0000\u0005\u0001\u0000\u0006" +
                    "\u0000\u0000\u0004\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}