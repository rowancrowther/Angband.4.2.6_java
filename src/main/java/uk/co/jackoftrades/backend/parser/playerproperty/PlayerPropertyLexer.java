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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/PlayerProperty.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.playerproperty;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class PlayerPropertyLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, TYPE = 3, CODE = 4, BINDUI = 5, NAME = 6, DESC = 7, VALUE = 8, TYPE_OPTIONS = 9,
            INTEGER = 10, TEXT = 11, TAG = 12, BINDUIVAL = 13;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "COMMENT", "EOL", "TYPE", "CODE", "BINDUI", "NAME", "DESC", "VALUE",
                "TYPE_OPTIONS", "INTEGER", "TEXT", "TAG", "BINDUIVAL"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'type:'", "'code:'", "'bindui:'", "'name:'", "'desc:'",
                "'value:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "TYPE", "CODE", "BINDUI", "NAME", "DESC", "VALUE",
                "TYPE_OPTIONS", "INTEGER", "TEXT", "TAG", "BINDUIVAL"
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

    String typeOption;

    public PlayerPropertyLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "PlayerProperty.g4";
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
            "\u0004\u0000\r\u0097\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001" +
                    "\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004" +
                    "\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007" +
                    "\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b" +
                    "\u0007\u000b\u0002\f\u0007\f\u0001\u0000\u0001\u0000\u0005\u0000\u001e" +
                    "\b\u0000\n\u0000\f\u0000!\t\u0000\u0001\u0000\u0004\u0000$\b\u0000\u000b" +
                    "\u0000\f\u0000%\u0001\u0000\u0001\u0000\u0001\u0001\u0005\u0001+\b\u0001" +
                    "\n\u0001\f\u0001.\t\u0001\u0001\u0001\u0003\u00011\b\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0003\bq\b\b\u0001\t\u0003\tt\b\t\u0001\t\u0004\tw\b\t\u000b" +
                    "\t\f\tx\u0001\n\u0004\n|\b\n\u000b\n\f\n}\u0001\u000b\u0001\u000b\u0004" +
                    "\u000b\u0082\b\u000b\u000b\u000b\f\u000b\u0083\u0001\u000b\u0001\u000b" +
                    "\u0001\f\u0001\f\u0001\f\u0001\f\u0003\f\u008c\b\f\u0001\f\u0001\f\u0001" +
                    "\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0003\f\u0096\b\f\u0000\u0000" +
                    "\r\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006" +
                    "\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u0001\u0000" +
                    "\u0002\u0001\u0000\n\n\n\u0000  (),,..09;;A[]]__az\u00a2\u0000\u0001\u0001" +
                    "\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001" +
                    "\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000" +
                    "\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000" +
                    "\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000" +
                    "\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000" +
                    "\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000" +
                    "\u0000\u0001\u001b\u0001\u0000\u0000\u0000\u0003,\u0001\u0000\u0000\u0000" +
                    "\u00056\u0001\u0000\u0000\u0000\u0007<\u0001\u0000\u0000\u0000\tB\u0001" +
                    "\u0000\u0000\u0000\u000bJ\u0001\u0000\u0000\u0000\rP\u0001\u0000\u0000" +
                    "\u0000\u000fV\u0001\u0000\u0000\u0000\u0011p\u0001\u0000\u0000\u0000\u0013" +
                    "s\u0001\u0000\u0000\u0000\u0015{\u0001\u0000\u0000\u0000\u0017\u007f\u0001" +
                    "\u0000\u0000\u0000\u0019\u0087\u0001\u0000\u0000\u0000\u001b\u001f\u0005" +
                    "#\u0000\u0000\u001c\u001e\b\u0000\u0000\u0000\u001d\u001c\u0001\u0000" +
                    "\u0000\u0000\u001e!\u0001\u0000\u0000\u0000\u001f\u001d\u0001\u0000\u0000" +
                    "\u0000\u001f \u0001\u0000\u0000\u0000 #\u0001\u0000\u0000\u0000!\u001f" +
                    "\u0001\u0000\u0000\u0000\"$\u0005\n\u0000\u0000#\"\u0001\u0000\u0000\u0000" +
                    "$%\u0001\u0000\u0000\u0000%#\u0001\u0000\u0000\u0000%&\u0001\u0000\u0000" +
                    "\u0000&\'\u0001\u0000\u0000\u0000\'(\u0006\u0000\u0000\u0000(\u0002\u0001" +
                    "\u0000\u0000\u0000)+\u0005 \u0000\u0000*)\u0001\u0000\u0000\u0000+.\u0001" +
                    "\u0000\u0000\u0000,*\u0001\u0000\u0000\u0000,-\u0001\u0000\u0000\u0000" +
                    "-0\u0001\u0000\u0000\u0000.,\u0001\u0000\u0000\u0000/1\u0005\r\u0000\u0000" +
                    "0/\u0001\u0000\u0000\u000001\u0001\u0000\u0000\u000012\u0001\u0000\u0000" +
                    "\u000023\u0005\n\u0000\u000034\u0001\u0000\u0000\u000045\u0006\u0001\u0000" +
                    "\u00005\u0004\u0001\u0000\u0000\u000067\u0005t\u0000\u000078\u0005y\u0000" +
                    "\u000089\u0005p\u0000\u00009:\u0005e\u0000\u0000:;\u0005:\u0000\u0000" +
                    ";\u0006\u0001\u0000\u0000\u0000<=\u0005c\u0000\u0000=>\u0005o\u0000\u0000" +
                    ">?\u0005d\u0000\u0000?@\u0005e\u0000\u0000@A\u0005:\u0000\u0000A\b\u0001" +
                    "\u0000\u0000\u0000BC\u0005b\u0000\u0000CD\u0005i\u0000\u0000DE\u0005n" +
                    "\u0000\u0000EF\u0005d\u0000\u0000FG\u0005u\u0000\u0000GH\u0005i\u0000" +
                    "\u0000HI\u0005:\u0000\u0000I\n\u0001\u0000\u0000\u0000JK\u0005n\u0000" +
                    "\u0000KL\u0005a\u0000\u0000LM\u0005m\u0000\u0000MN\u0005e\u0000\u0000" +
                    "NO\u0005:\u0000\u0000O\f\u0001\u0000\u0000\u0000PQ\u0005d\u0000\u0000" +
                    "QR\u0005e\u0000\u0000RS\u0005s\u0000\u0000ST\u0005c\u0000\u0000TU\u0005" +
                    ":\u0000\u0000U\u000e\u0001\u0000\u0000\u0000VW\u0005v\u0000\u0000WX\u0005" +
                    "a\u0000\u0000XY\u0005l\u0000\u0000YZ\u0005u\u0000\u0000Z[\u0005e\u0000" +
                    "\u0000[\\\u0005:\u0000\u0000\\\u0010\u0001\u0000\u0000\u0000]^\u0005p" +
                    "\u0000\u0000^_\u0005l\u0000\u0000_`\u0005a\u0000\u0000`a\u0005y\u0000" +
                    "\u0000ab\u0005e\u0000\u0000bq\u0005r\u0000\u0000cd\u0005o\u0000\u0000" +
                    "de\u0005b\u0000\u0000ef\u0005j\u0000\u0000fg\u0005e\u0000\u0000gh\u0005" +
                    "c\u0000\u0000hq\u0005t\u0000\u0000ij\u0005e\u0000\u0000jk\u0005l\u0000" +
                    "\u0000kl\u0005e\u0000\u0000lm\u0005m\u0000\u0000mn\u0005e\u0000\u0000" +
                    "no\u0005n\u0000\u0000oq\u0005t\u0000\u0000p]\u0001\u0000\u0000\u0000p" +
                    "c\u0001\u0000\u0000\u0000pi\u0001\u0000\u0000\u0000q\u0012\u0001\u0000" +
                    "\u0000\u0000rt\u0005-\u0000\u0000sr\u0001\u0000\u0000\u0000st\u0001\u0000" +
                    "\u0000\u0000tv\u0001\u0000\u0000\u0000uw\u000209\u0000vu\u0001\u0000\u0000" +
                    "\u0000wx\u0001\u0000\u0000\u0000xv\u0001\u0000\u0000\u0000xy\u0001\u0000" +
                    "\u0000\u0000y\u0014\u0001\u0000\u0000\u0000z|\u0007\u0001\u0000\u0000" +
                    "{z\u0001\u0000\u0000\u0000|}\u0001\u0000\u0000\u0000}{\u0001\u0000\u0000" +
                    "\u0000}~\u0001\u0000\u0000\u0000~\u0016\u0001\u0000\u0000\u0000\u007f" +
                    "\u0081\u0005<\u0000\u0000\u0080\u0082\u0002AZ\u0000\u0081\u0080\u0001" +
                    "\u0000\u0000\u0000\u0082\u0083\u0001\u0000\u0000\u0000\u0083\u0081\u0001" +
                    "\u0000\u0000\u0000\u0083\u0084\u0001\u0000\u0000\u0000\u0084\u0085\u0001" +
                    "\u0000\u0000\u0000\u0085\u0086\u0005>\u0000\u0000\u0086\u0018\u0001\u0000" +
                    "\u0000\u0000\u0087\u0088\u0005:\u0000\u0000\u0088\u0089\u000201\u0000" +
                    "\u0089\u0095\u0005:\u0000\u0000\u008a\u008c\u0005-\u0000\u0000\u008b\u008a" +
                    "\u0001\u0000\u0000\u0000\u008b\u008c\u0001\u0000\u0000\u0000\u008c\u008d" +
                    "\u0001\u0000\u0000\u0000\u008d\u0096\u000209\u0000\u008e\u008f\u0005s" +
                    "\u0000\u0000\u008f\u0090\u0005p\u0000\u0000\u0090\u0091\u0005e\u0000\u0000" +
                    "\u0091\u0092\u0005c\u0000\u0000\u0092\u0093\u0005i\u0000\u0000\u0093\u0094" +
                    "\u0005a\u0000\u0000\u0094\u0096\u0005l\u0000\u0000\u0095\u008b\u0001\u0000" +
                    "\u0000\u0000\u0095\u008e\u0001\u0000\u0000\u0000\u0096\u001a\u0001\u0000" +
                    "\u0000\u0000\f\u0000\u001f%,0psx}\u0083\u008b\u0095\u0001\u0006\u0000" +
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