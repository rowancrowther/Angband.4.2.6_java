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
// Generated from HintLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.hint;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class HintLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, HINT = 2, INTEGER = 3, COMMENT = 4, EOL = 5, STRING = 6;
    public static final int
            STRING_MODE = 1;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE", "STRING_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "RECORD_COUNT", "HINT", "INTEGER", "COMMENT", "EOL", "STRING"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'H:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "HINT", "INTEGER", "COMMENT", "EOL", "STRING"
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


    public HintLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "HintLexer.g4";
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
            "\u0004\u0000\u0006H\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000\u0007" +
                    "\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007" +
                    "\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0002\u0003\u0002#\b\u0002\u0001\u0002\u0004\u0002&\b\u0002\u000b\u0002" +
                    "\f\u0002\'\u0001\u0003\u0001\u0003\u0005\u0003,\b\u0003\n\u0003\f\u0003" +
                    "/\t\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004" +
                    "\u0005\u00046\b\u0004\n\u0004\f\u00049\t\u0004\u0001\u0004\u0003\u0004" +
                    "<\b\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005" +
                    "\u0004\u0005C\b\u0005\u000b\u0005\f\u0005D\u0001\u0005\u0001\u0005\u0000" +
                    "\u0000\u0006\u0002\u0001\u0004\u0002\u0006\u0003\b\u0004\n\u0005\f\u0006" +
                    "\u0002\u0000\u0001\u0002\u0001\u0000\n\n\u0002\u0000\n\n\r\rL\u0000\u0002" +
                    "\u0001\u0000\u0000\u0000\u0000\u0004\u0001\u0000\u0000\u0000\u0000\u0006" +
                    "\u0001\u0000\u0000\u0000\u0000\b\u0001\u0000\u0000\u0000\u0000\n\u0001" +
                    "\u0000\u0000\u0000\u0001\f\u0001\u0000\u0000\u0000\u0002\u000e\u0001\u0000" +
                    "\u0000\u0000\u0004\u001c\u0001\u0000\u0000\u0000\u0006\"\u0001\u0000\u0000" +
                    "\u0000\b)\u0001\u0000\u0000\u0000\n7\u0001\u0000\u0000\u0000\fB\u0001" +
                    "\u0000\u0000\u0000\u000e\u000f\u0005r\u0000\u0000\u000f\u0010\u0005e\u0000" +
                    "\u0000\u0010\u0011\u0005c\u0000\u0000\u0011\u0012\u0005o\u0000\u0000\u0012" +
                    "\u0013\u0005r\u0000\u0000\u0013\u0014\u0005d\u0000\u0000\u0014\u0015\u0005" +
                    "-\u0000\u0000\u0015\u0016\u0005c\u0000\u0000\u0016\u0017\u0005o\u0000" +
                    "\u0000\u0017\u0018\u0005u\u0000\u0000\u0018\u0019\u0005n\u0000\u0000\u0019" +
                    "\u001a\u0005t\u0000\u0000\u001a\u001b\u0005:\u0000\u0000\u001b\u0003\u0001" +
                    "\u0000\u0000\u0000\u001c\u001d\u0005H\u0000\u0000\u001d\u001e\u0005:\u0000" +
                    "\u0000\u001e\u001f\u0001\u0000\u0000\u0000\u001f \u0006\u0001\u0000\u0000" +
                    " \u0005\u0001\u0000\u0000\u0000!#\u0005-\u0000\u0000\"!\u0001\u0000\u0000" +
                    "\u0000\"#\u0001\u0000\u0000\u0000#%\u0001\u0000\u0000\u0000$&\u000209" +
                    "\u0000%$\u0001\u0000\u0000\u0000&\'\u0001\u0000\u0000\u0000\'%\u0001\u0000" +
                    "\u0000\u0000\'(\u0001\u0000\u0000\u0000(\u0007\u0001\u0000\u0000\u0000" +
                    ")-\u0005#\u0000\u0000*,\b\u0000\u0000\u0000+*\u0001\u0000\u0000\u0000" +
                    ",/\u0001\u0000\u0000\u0000-+\u0001\u0000\u0000\u0000-.\u0001\u0000\u0000" +
                    "\u0000.0\u0001\u0000\u0000\u0000/-\u0001\u0000\u0000\u000001\u0005\n\u0000" +
                    "\u000012\u0001\u0000\u0000\u000023\u0006\u0003\u0001\u00003\t\u0001\u0000" +
                    "\u0000\u000046\u0005 \u0000\u000054\u0001\u0000\u0000\u000069\u0001\u0000" +
                    "\u0000\u000075\u0001\u0000\u0000\u000078\u0001\u0000\u0000\u00008;\u0001" +
                    "\u0000\u0000\u000097\u0001\u0000\u0000\u0000:<\u0005\r\u0000\u0000;:\u0001" +
                    "\u0000\u0000\u0000;<\u0001\u0000\u0000\u0000<=\u0001\u0000\u0000\u0000" +
                    "=>\u0005\n\u0000\u0000>?\u0001\u0000\u0000\u0000?@\u0006\u0004\u0001\u0000" +
                    "@\u000b\u0001\u0000\u0000\u0000AC\b\u0001\u0000\u0000BA\u0001\u0000\u0000" +
                    "\u0000CD\u0001\u0000\u0000\u0000DB\u0001\u0000\u0000\u0000DE\u0001\u0000" +
                    "\u0000\u0000EF\u0001\u0000\u0000\u0000FG\u0006\u0005\u0002\u0000G\r\u0001" +
                    "\u0000\u0000\u0000\b\u0000\u0001\"\'-7;D\u0003\u0005\u0001\u0000\u0006" +
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