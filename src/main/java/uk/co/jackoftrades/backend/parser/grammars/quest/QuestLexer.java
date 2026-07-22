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
// Generated from QuestLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.quest;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class QuestLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, NAME = 2, LEVEL = 3, RACE = 4, NUMBER = 5, INTEGER = 6, COMMENT = 7,
            EOL = 8, STRING = 9;
    public static final int
            REST_OF_LINE = 1;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE", "REST_OF_LINE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "RECORD_COUNT", "NAME", "LEVEL", "RACE", "NUMBER", "INTEGER", "COMMENT",
                "EOL", "STRING"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'name:'", "'level:'", "'race:'", "'number:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "NAME", "LEVEL", "RACE", "NUMBER", "INTEGER", "COMMENT",
                "EOL", "STRING"
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


    public QuestLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "QuestLexer.g4";
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
            "\u0004\u0000\th\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000\u0007" +
                    "\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007" +
                    "\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007" +
                    "\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0005\u0003\u0005C\b\u0005\u0001\u0005\u0004\u0005" +
                    "F\b\u0005\u000b\u0005\f\u0005G\u0001\u0006\u0001\u0006\u0005\u0006L\b" +
                    "\u0006\n\u0006\f\u0006O\t\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0007\u0005\u0007V\b\u0007\n\u0007\f\u0007Y\t\u0007\u0001" +
                    "\u0007\u0003\u0007\\\b\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\b\u0004\bc\b\b\u000b\b\f\bd\u0001\b\u0001\b\u0000\u0000\t" +
                    "\u0002\u0001\u0004\u0002\u0006\u0003\b\u0004\n\u0005\f\u0006\u000e\u0007" +
                    "\u0010\b\u0012\t\u0002\u0000\u0001\u0002\u0001\u0000\n\n\u0002\u0000\n" +
                    "\n\r\rl\u0000\u0002\u0001\u0000\u0000\u0000\u0000\u0004\u0001\u0000\u0000" +
                    "\u0000\u0000\u0006\u0001\u0000\u0000\u0000\u0000\b\u0001\u0000\u0000\u0000" +
                    "\u0000\n\u0001\u0000\u0000\u0000\u0000\f\u0001\u0000\u0000\u0000\u0000" +
                    "\u000e\u0001\u0000\u0000\u0000\u0000\u0010\u0001\u0000\u0000\u0000\u0001" +
                    "\u0012\u0001\u0000\u0000\u0000\u0002\u0014\u0001\u0000\u0000\u0000\u0004" +
                    "\"\u0001\u0000\u0000\u0000\u0006*\u0001\u0000\u0000\u0000\b1\u0001\u0000" +
                    "\u0000\u0000\n9\u0001\u0000\u0000\u0000\fB\u0001\u0000\u0000\u0000\u000e" +
                    "I\u0001\u0000\u0000\u0000\u0010W\u0001\u0000\u0000\u0000\u0012b\u0001" +
                    "\u0000\u0000\u0000\u0014\u0015\u0005r\u0000\u0000\u0015\u0016\u0005e\u0000" +
                    "\u0000\u0016\u0017\u0005c\u0000\u0000\u0017\u0018\u0005o\u0000\u0000\u0018" +
                    "\u0019\u0005r\u0000\u0000\u0019\u001a\u0005d\u0000\u0000\u001a\u001b\u0005" +
                    "-\u0000\u0000\u001b\u001c\u0005c\u0000\u0000\u001c\u001d\u0005o\u0000" +
                    "\u0000\u001d\u001e\u0005u\u0000\u0000\u001e\u001f\u0005n\u0000\u0000\u001f" +
                    " \u0005t\u0000\u0000 !\u0005:\u0000\u0000!\u0003\u0001\u0000\u0000\u0000" +
                    "\"#\u0005n\u0000\u0000#$\u0005a\u0000\u0000$%\u0005m\u0000\u0000%&\u0005" +
                    "e\u0000\u0000&\'\u0005:\u0000\u0000\'(\u0001\u0000\u0000\u0000()\u0006" +
                    "\u0001\u0000\u0000)\u0005\u0001\u0000\u0000\u0000*+\u0005l\u0000\u0000" +
                    "+,\u0005e\u0000\u0000,-\u0005v\u0000\u0000-.\u0005e\u0000\u0000./\u0005" +
                    "l\u0000\u0000/0\u0005:\u0000\u00000\u0007\u0001\u0000\u0000\u000012\u0005" +
                    "r\u0000\u000023\u0005a\u0000\u000034\u0005c\u0000\u000045\u0005e\u0000" +
                    "\u000056\u0005:\u0000\u000067\u0001\u0000\u0000\u000078\u0006\u0003\u0000" +
                    "\u00008\t\u0001\u0000\u0000\u00009:\u0005n\u0000\u0000:;\u0005u\u0000" +
                    "\u0000;<\u0005m\u0000\u0000<=\u0005b\u0000\u0000=>\u0005e\u0000\u0000" +
                    ">?\u0005r\u0000\u0000?@\u0005:\u0000\u0000@\u000b\u0001\u0000\u0000\u0000" +
                    "AC\u0005-\u0000\u0000BA\u0001\u0000\u0000\u0000BC\u0001\u0000\u0000\u0000" +
                    "CE\u0001\u0000\u0000\u0000DF\u000209\u0000ED\u0001\u0000\u0000\u0000F" +
                    "G\u0001\u0000\u0000\u0000GE\u0001\u0000\u0000\u0000GH\u0001\u0000\u0000" +
                    "\u0000H\r\u0001\u0000\u0000\u0000IM\u0005#\u0000\u0000JL\b\u0000\u0000" +
                    "\u0000KJ\u0001\u0000\u0000\u0000LO\u0001\u0000\u0000\u0000MK\u0001\u0000" +
                    "\u0000\u0000MN\u0001\u0000\u0000\u0000NP\u0001\u0000\u0000\u0000OM\u0001" +
                    "\u0000\u0000\u0000PQ\u0005\n\u0000\u0000QR\u0001\u0000\u0000\u0000RS\u0006" +
                    "\u0006\u0001\u0000S\u000f\u0001\u0000\u0000\u0000TV\u0005 \u0000\u0000" +
                    "UT\u0001\u0000\u0000\u0000VY\u0001\u0000\u0000\u0000WU\u0001\u0000\u0000" +
                    "\u0000WX\u0001\u0000\u0000\u0000X[\u0001\u0000\u0000\u0000YW\u0001\u0000" +
                    "\u0000\u0000Z\\\u0005\r\u0000\u0000[Z\u0001\u0000\u0000\u0000[\\\u0001" +
                    "\u0000\u0000\u0000\\]\u0001\u0000\u0000\u0000]^\u0005\n\u0000\u0000^_" +
                    "\u0001\u0000\u0000\u0000_`\u0006\u0007\u0001\u0000`\u0011\u0001\u0000" +
                    "\u0000\u0000ac\b\u0001\u0000\u0000ba\u0001\u0000\u0000\u0000cd\u0001\u0000" +
                    "\u0000\u0000db\u0001\u0000\u0000\u0000de\u0001\u0000\u0000\u0000ef\u0001" +
                    "\u0000\u0000\u0000fg\u0006\b\u0002\u0000g\u0013\u0001\u0000\u0000\u0000" +
                    "\b\u0000\u0001BGMW[d\u0003\u0005\u0001\u0000\u0006\u0000\u0000\u0004\u0000" +
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