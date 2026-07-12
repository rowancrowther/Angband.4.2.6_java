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

// Generated from HistoryLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.history;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class HistoryLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, CHART = 2, PHRASE = 3, INTEGER = 4, COLON = 5, COMMENT = 6, EOL = 7,
            STRING = 8, STRING_EOL = 9;
    public static final int
            FREE_TEXT_MODE = 1;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE", "FREE_TEXT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "RECORD_COUNT", "CHART", "PHRASE", "INTEGER", "COLON", "COMMENT", "EOL",
                "STRING", "STRING_EOL"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'chart:'", "'phrase:'", null, "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "CHART", "PHRASE", "INTEGER", "COLON", "COMMENT",
                "EOL", "STRING", "STRING_EOL"
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


    public HistoryLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "HistoryLexer.g4";
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
            "\u0004\u0000\te\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000\u0007" +
                    "\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007" +
                    "\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007" +
                    "\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003" +
                    "\u0003\u00035\b\u0003\u0001\u0003\u0004\u00038\b\u0003\u000b\u0003\f\u0003" +
                    "9\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0005\u0005@\b\u0005" +
                    "\n\u0005\f\u0005C\t\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0006\u0005\u0006J\b\u0006\n\u0006\f\u0006M\t\u0006\u0001\u0006" +
                    "\u0003\u0006P\b\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0007\u0004\u0007W\b\u0007\u000b\u0007\f\u0007X\u0001\b\u0005\b" +
                    "\\\b\b\n\b\f\b_\t\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0000\u0000" +
                    "\t\u0002\u0001\u0004\u0002\u0006\u0003\b\u0004\n\u0005\f\u0006\u000e\u0007" +
                    "\u0010\b\u0012\t\u0002\u0000\u0001\u0002\u0001\u0000\n\n\u0002\u0000\n" +
                    "\n\r\rj\u0000\u0002\u0001\u0000\u0000\u0000\u0000\u0004\u0001\u0000\u0000" +
                    "\u0000\u0000\u0006\u0001\u0000\u0000\u0000\u0000\b\u0001\u0000\u0000\u0000" +
                    "\u0000\n\u0001\u0000\u0000\u0000\u0000\f\u0001\u0000\u0000\u0000\u0000" +
                    "\u000e\u0001\u0000\u0000\u0000\u0001\u0010\u0001\u0000\u0000\u0000\u0001" +
                    "\u0012\u0001\u0000\u0000\u0000\u0002\u0014\u0001\u0000\u0000\u0000\u0004" +
                    "\"\u0001\u0000\u0000\u0000\u0006)\u0001\u0000\u0000\u0000\b4\u0001\u0000" +
                    "\u0000\u0000\n;\u0001\u0000\u0000\u0000\f=\u0001\u0000\u0000\u0000\u000e" +
                    "K\u0001\u0000\u0000\u0000\u0010V\u0001\u0000\u0000\u0000\u0012]\u0001" +
                    "\u0000\u0000\u0000\u0014\u0015\u0005r\u0000\u0000\u0015\u0016\u0005e\u0000" +
                    "\u0000\u0016\u0017\u0005c\u0000\u0000\u0017\u0018\u0005o\u0000\u0000\u0018" +
                    "\u0019\u0005r\u0000\u0000\u0019\u001a\u0005d\u0000\u0000\u001a\u001b\u0005" +
                    "-\u0000\u0000\u001b\u001c\u0005c\u0000\u0000\u001c\u001d\u0005o\u0000" +
                    "\u0000\u001d\u001e\u0005u\u0000\u0000\u001e\u001f\u0005n\u0000\u0000\u001f" +
                    " \u0005t\u0000\u0000 !\u0005:\u0000\u0000!\u0003\u0001\u0000\u0000\u0000" +
                    "\"#\u0005c\u0000\u0000#$\u0005h\u0000\u0000$%\u0005a\u0000\u0000%&\u0005" +
                    "r\u0000\u0000&\'\u0005t\u0000\u0000\'(\u0005:\u0000\u0000(\u0005\u0001" +
                    "\u0000\u0000\u0000)*\u0005p\u0000\u0000*+\u0005h\u0000\u0000+,\u0005r" +
                    "\u0000\u0000,-\u0005a\u0000\u0000-.\u0005s\u0000\u0000./\u0005e\u0000" +
                    "\u0000/0\u0005:\u0000\u000001\u0001\u0000\u0000\u000012\u0006\u0002\u0000" +
                    "\u00002\u0007\u0001\u0000\u0000\u000035\u0005-\u0000\u000043\u0001\u0000" +
                    "\u0000\u000045\u0001\u0000\u0000\u000057\u0001\u0000\u0000\u000068\u0002" +
                    "09\u000076\u0001\u0000\u0000\u000089\u0001\u0000\u0000\u000097\u0001\u0000" +
                    "\u0000\u00009:\u0001\u0000\u0000\u0000:\t\u0001\u0000\u0000\u0000;<\u0005" +
                    ":\u0000\u0000<\u000b\u0001\u0000\u0000\u0000=A\u0005#\u0000\u0000>@\b" +
                    "\u0000\u0000\u0000?>\u0001\u0000\u0000\u0000@C\u0001\u0000\u0000\u0000" +
                    "A?\u0001\u0000\u0000\u0000AB\u0001\u0000\u0000\u0000BD\u0001\u0000\u0000" +
                    "\u0000CA\u0001\u0000\u0000\u0000DE\u0005\n\u0000\u0000EF\u0001\u0000\u0000" +
                    "\u0000FG\u0006\u0005\u0001\u0000G\r\u0001\u0000\u0000\u0000HJ\u0005 \u0000" +
                    "\u0000IH\u0001\u0000\u0000\u0000JM\u0001\u0000\u0000\u0000KI\u0001\u0000" +
                    "\u0000\u0000KL\u0001\u0000\u0000\u0000LO\u0001\u0000\u0000\u0000MK\u0001" +
                    "\u0000\u0000\u0000NP\u0005\r\u0000\u0000ON\u0001\u0000\u0000\u0000OP\u0001" +
                    "\u0000\u0000\u0000PQ\u0001\u0000\u0000\u0000QR\u0005\n\u0000\u0000RS\u0001" +
                    "\u0000\u0000\u0000ST\u0006\u0006\u0001\u0000T\u000f\u0001\u0000\u0000" +
                    "\u0000UW\b\u0001\u0000\u0000VU\u0001\u0000\u0000\u0000WX\u0001\u0000\u0000" +
                    "\u0000XV\u0001\u0000\u0000\u0000XY\u0001\u0000\u0000\u0000Y\u0011\u0001" +
                    "\u0000\u0000\u0000Z\\\u0005\r\u0000\u0000[Z\u0001\u0000\u0000\u0000\\" +
                    "_\u0001\u0000\u0000\u0000][\u0001\u0000\u0000\u0000]^\u0001\u0000\u0000" +
                    "\u0000^`\u0001\u0000\u0000\u0000_]\u0001\u0000\u0000\u0000`a\u0005\n\u0000" +
                    "\u0000ab\u0001\u0000\u0000\u0000bc\u0006\b\u0002\u0000cd\u0006\b\u0001" +
                    "\u0000d\u0013\u0001\u0000\u0000\u0000\t\u0000\u000149AKOX]\u0003\u0005" +
                    "\u0001\u0000\u0006\u0000\u0000\u0004\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}