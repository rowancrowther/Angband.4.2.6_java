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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/Pain.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.pain;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class PainLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, TYPE = 3, MESSAGE = 4, TEXT = 5, NUMBER = 6;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "COMMENT", "EOL", "TYPE", "MESSAGE", "TEXT", "NUMBER"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'type:'", "'message:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "TYPE", "MESSAGE", "TEXT", "NUMBER"
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


    public PainLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "Pain.g4";
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
            "\u0004\u0000\u0006B\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001" +
                    "\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004" +
                    "\u0007\u0004\u0002\u0005\u0007\u0005\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0005\u0000\u0011\b\u0000\n\u0000\f\u0000\u0014\t\u0000\u0001\u0000\u0004" +
                    "\u0000\u0017\b\u0000\u000b\u0000\f\u0000\u0018\u0001\u0000\u0001\u0000" +
                    "\u0001\u0001\u0005\u0001\u001e\b\u0001\n\u0001\f\u0001!\t\u0001\u0001" +
                    "\u0001\u0003\u0001$\b\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0004\u0004:\b" +
                    "\u0004\u000b\u0004\f\u0004;\u0001\u0005\u0004\u0005?\b\u0005\u000b\u0005" +
                    "\f\u0005@\u0000\u0000\u0006\u0001\u0001\u0003\u0002\u0005\u0003\u0007" +
                    "\u0004\t\u0005\u000b\u0006\u0001\u0000\u0004\u0001\u0000::\u0001\u0000" +
                    "\n\n\u0006\u0000  ..A[]]az||\u0001\u000009G\u0000\u0001\u0001\u0000\u0000" +
                    "\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000" +
                    "\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000" +
                    "\u0000\u000b\u0001\u0000\u0000\u0000\u0001\r\u0001\u0000\u0000\u0000\u0003" +
                    "\u001f\u0001\u0000\u0000\u0000\u0005)\u0001\u0000\u0000\u0000\u0007/\u0001" +
                    "\u0000\u0000\u0000\t9\u0001\u0000\u0000\u0000\u000b>\u0001\u0000\u0000" +
                    "\u0000\r\u000e\u0005#\u0000\u0000\u000e\u0012\b\u0000\u0000\u0000\u000f" +
                    "\u0011\b\u0001\u0000\u0000\u0010\u000f\u0001\u0000\u0000\u0000\u0011\u0014" +
                    "\u0001\u0000\u0000\u0000\u0012\u0010\u0001\u0000\u0000\u0000\u0012\u0013" +
                    "\u0001\u0000\u0000\u0000\u0013\u0016\u0001\u0000\u0000\u0000\u0014\u0012" +
                    "\u0001\u0000\u0000\u0000\u0015\u0017\u0005\n\u0000\u0000\u0016\u0015\u0001" +
                    "\u0000\u0000\u0000\u0017\u0018\u0001\u0000\u0000\u0000\u0018\u0016\u0001" +
                    "\u0000\u0000\u0000\u0018\u0019\u0001\u0000\u0000\u0000\u0019\u001a\u0001" +
                    "\u0000\u0000\u0000\u001a\u001b\u0006\u0000\u0000\u0000\u001b\u0002\u0001" +
                    "\u0000\u0000\u0000\u001c\u001e\u0005 \u0000\u0000\u001d\u001c\u0001\u0000" +
                    "\u0000\u0000\u001e!\u0001\u0000\u0000\u0000\u001f\u001d\u0001\u0000\u0000" +
                    "\u0000\u001f \u0001\u0000\u0000\u0000 #\u0001\u0000\u0000\u0000!\u001f" +
                    "\u0001\u0000\u0000\u0000\"$\u0005\r\u0000\u0000#\"\u0001\u0000\u0000\u0000" +
                    "#$\u0001\u0000\u0000\u0000$%\u0001\u0000\u0000\u0000%&\u0005\n\u0000\u0000" +
                    "&\'\u0001\u0000\u0000\u0000\'(\u0006\u0001\u0000\u0000(\u0004\u0001\u0000" +
                    "\u0000\u0000)*\u0005t\u0000\u0000*+\u0005y\u0000\u0000+,\u0005p\u0000" +
                    "\u0000,-\u0005e\u0000\u0000-.\u0005:\u0000\u0000.\u0006\u0001\u0000\u0000" +
                    "\u0000/0\u0005m\u0000\u000001\u0005e\u0000\u000012\u0005s\u0000\u0000" +
                    "23\u0005s\u0000\u000034\u0005a\u0000\u000045\u0005g\u0000\u000056\u0005" +
                    "e\u0000\u000067\u0005:\u0000\u00007\b\u0001\u0000\u0000\u00008:\u0007" +
                    "\u0002\u0000\u000098\u0001\u0000\u0000\u0000:;\u0001\u0000\u0000\u0000" +
                    ";9\u0001\u0000\u0000\u0000;<\u0001\u0000\u0000\u0000<\n\u0001\u0000\u0000" +
                    "\u0000=?\u0007\u0003\u0000\u0000>=\u0001\u0000\u0000\u0000?@\u0001\u0000" +
                    "\u0000\u0000@>\u0001\u0000\u0000\u0000@A\u0001\u0000\u0000\u0000A\f\u0001" +
                    "\u0000\u0000\u0000\b\u0000\u0012\u0018\u001f#9;@\u0001\u0006\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}