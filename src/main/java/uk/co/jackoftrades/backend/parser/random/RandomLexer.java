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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Random.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.random;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class RandomLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            D = 1, M = 2, PLUS = 3, MINUS = 4, NUMBER = 5;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "D", "M", "PLUS", "MINUS", "NUMBER"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'d'", "'M'", "'+'", "'-'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "D", "M", "PLUS", "MINUS", "NUMBER"
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


    public RandomLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "Random.g4";
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
            "\u0004\u0000\u0005\u0018\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002" +
                    "\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002" +
                    "\u0004\u0007\u0004\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001" +
                    "\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0004\u0004\u0004\u0015" +
                    "\b\u0004\u000b\u0004\f\u0004\u0016\u0000\u0000\u0005\u0001\u0001\u0003" +
                    "\u0002\u0005\u0003\u0007\u0004\t\u0005\u0001\u0000\u0000\u0018\u0000\u0001" +
                    "\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005" +
                    "\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001" +
                    "\u0000\u0000\u0000\u0001\u000b\u0001\u0000\u0000\u0000\u0003\r\u0001\u0000" +
                    "\u0000\u0000\u0005\u000f\u0001\u0000\u0000\u0000\u0007\u0011\u0001\u0000" +
                    "\u0000\u0000\t\u0014\u0001\u0000\u0000\u0000\u000b\f\u0005d\u0000\u0000" +
                    "\f\u0002\u0001\u0000\u0000\u0000\r\u000e\u0005M\u0000\u0000\u000e\u0004" +
                    "\u0001\u0000\u0000\u0000\u000f\u0010\u0005+\u0000\u0000\u0010\u0006\u0001" +
                    "\u0000\u0000\u0000\u0011\u0012\u0005-\u0000\u0000\u0012\b\u0001\u0000" +
                    "\u0000\u0000\u0013\u0015\u000209\u0000\u0014\u0013\u0001\u0000\u0000\u0000" +
                    "\u0015\u0016\u0001\u0000\u0000\u0000\u0016\u0014\u0001\u0000\u0000\u0000" +
                    "\u0016\u0017\u0001\u0000\u0000\u0000\u0017\n\u0001\u0000\u0000\u0000\u0002" +
                    "\u0000\u0016\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}