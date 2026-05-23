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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/World.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.world;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class WorldLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, LEVEL = 3, COLON = 4, NUMBER = 5, NAME = 6;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "COMMENT", "EOL", "LEVEL", "COLON", "NUMBER", "NAME"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'level:'", "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "LEVEL", "COLON", "NUMBER", "NAME"
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


    public record ParsedWorld(int level, String levelName, String levelUp, String levelDown) {
    }


    public WorldLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "World.g4";
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
            "\u0004\u0000\u00065\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001" +
                    "\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004" +
                    "\u0007\u0004\u0002\u0005\u0007\u0005\u0001\u0000\u0001\u0000\u0005\u0000" +
                    "\u0010\b\u0000\n\u0000\f\u0000\u0013\t\u0000\u0001\u0000\u0004\u0000\u0016" +
                    "\b\u0000\u000b\u0000\f\u0000\u0017\u0001\u0000\u0001\u0000\u0001\u0001" +
                    "\u0003\u0001\u001d\b\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0004\u0004\u0004-\b\u0004" +
                    "\u000b\u0004\f\u0004.\u0001\u0005\u0004\u00052\b\u0005\u000b\u0005\f\u0005" +
                    "3\u0000\u0000\u0006\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t" +
                    "\u0005\u000b\u0006\u0001\u0000\u0002\u0001\u0000\n\n\u0004\u0000  09A" +
                    "Zaz9\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000" +
                    "\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000" +
                    "\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000" +
                    "\u0001\r\u0001\u0000\u0000\u0000\u0003\u001c\u0001\u0000\u0000\u0000\u0005" +
                    "\"\u0001\u0000\u0000\u0000\u0007)\u0001\u0000\u0000\u0000\t,\u0001\u0000" +
                    "\u0000\u0000\u000b1\u0001\u0000\u0000\u0000\r\u0011\u0005#\u0000\u0000" +
                    "\u000e\u0010\b\u0000\u0000\u0000\u000f\u000e\u0001\u0000\u0000\u0000\u0010" +
                    "\u0013\u0001\u0000\u0000\u0000\u0011\u000f\u0001\u0000\u0000\u0000\u0011" +
                    "\u0012\u0001\u0000\u0000\u0000\u0012\u0015\u0001\u0000\u0000\u0000\u0013" +
                    "\u0011\u0001\u0000\u0000\u0000\u0014\u0016\u0005\n\u0000\u0000\u0015\u0014" +
                    "\u0001\u0000\u0000\u0000\u0016\u0017\u0001\u0000\u0000\u0000\u0017\u0015" +
                    "\u0001\u0000\u0000\u0000\u0017\u0018\u0001\u0000\u0000\u0000\u0018\u0019" +
                    "\u0001\u0000\u0000\u0000\u0019\u001a\u0006\u0000\u0000\u0000\u001a\u0002" +
                    "\u0001\u0000\u0000\u0000\u001b\u001d\u0005\r\u0000\u0000\u001c\u001b\u0001" +
                    "\u0000\u0000\u0000\u001c\u001d\u0001\u0000\u0000\u0000\u001d\u001e\u0001" +
                    "\u0000\u0000\u0000\u001e\u001f\u0005\n\u0000\u0000\u001f \u0001\u0000" +
                    "\u0000\u0000 !\u0006\u0001\u0000\u0000!\u0004\u0001\u0000\u0000\u0000" +
                    "\"#\u0005l\u0000\u0000#$\u0005e\u0000\u0000$%\u0005v\u0000\u0000%&\u0005" +
                    "e\u0000\u0000&\'\u0005l\u0000\u0000\'(\u0005:\u0000\u0000(\u0006\u0001" +
                    "\u0000\u0000\u0000)*\u0005:\u0000\u0000*\b\u0001\u0000\u0000\u0000+-\u0002" +
                    "09\u0000,+\u0001\u0000\u0000\u0000-.\u0001\u0000\u0000\u0000.,\u0001\u0000" +
                    "\u0000\u0000./\u0001\u0000\u0000\u0000/\n\u0001\u0000\u0000\u000002\u0007" +
                    "\u0001\u0000\u000010\u0001\u0000\u0000\u000023\u0001\u0000\u0000\u0000" +
                    "31\u0001\u0000\u0000\u000034\u0001\u0000\u0000\u00004\f\u0001\u0000\u0000" +
                    "\u0000\u0006\u0000\u0011\u0017\u001c.3\u0001\u0006\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}