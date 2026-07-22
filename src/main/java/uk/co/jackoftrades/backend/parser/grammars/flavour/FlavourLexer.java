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

// Generated from FlavourLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.flavour;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class FlavourLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, KIND = 2, FIXED = 3, FLAVOUR = 4, INTEGER = 5, COMMENT = 6, EOL = 7,
            STRING = 8, COLON = 9, DELIM_EOL = 10;
    public static final int
            DELIM = 1;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE", "DELIM"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "RECORD_COUNT", "KIND", "FIXED", "FLAVOUR", "INTEGER", "COMMENT", "EOL",
                "STRING", "COLON", "DELIM_EOL"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'kind:'", "'fixed:'", "'flavor:'", null, null,
                null, null, "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "KIND", "FIXED", "FLAVOUR", "INTEGER", "COMMENT",
                "EOL", "STRING", "COLON", "DELIM_EOL"
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


    public FlavourLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "FlavourLexer.g4";
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
            "\u0004\u0000\nq\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000\u0007" +
                    "\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007" +
                    "\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007" +
                    "\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0003\u0004" +
                    "A\b\u0004\u0001\u0004\u0004\u0004D\b\u0004\u000b\u0004\f\u0004E\u0001" +
                    "\u0005\u0001\u0005\u0005\u0005J\b\u0005\n\u0005\f\u0005M\t\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0005\u0006T\b" +
                    "\u0006\n\u0006\f\u0006W\t\u0006\u0001\u0006\u0003\u0006Z\b\u0006\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0004\u0007a\b" +
                    "\u0007\u000b\u0007\f\u0007b\u0001\b\u0001\b\u0001\t\u0005\th\b\t\n\t\f" +
                    "\tk\t\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0000\u0000\n\u0002\u0001" +
                    "\u0004\u0002\u0006\u0003\b\u0004\n\u0005\f\u0006\u000e\u0007\u0010\b\u0012" +
                    "\t\u0014\n\u0002\u0000\u0001\u0002\u0001\u0000\n\n\u0003\u0000\n\n\r\r" +
                    "::v\u0000\u0002\u0001\u0000\u0000\u0000\u0000\u0004\u0001\u0000\u0000" +
                    "\u0000\u0000\u0006\u0001\u0000\u0000\u0000\u0000\b\u0001\u0000\u0000\u0000" +
                    "\u0000\n\u0001\u0000\u0000\u0000\u0000\f\u0001\u0000\u0000\u0000\u0000" +
                    "\u000e\u0001\u0000\u0000\u0000\u0001\u0010\u0001\u0000\u0000\u0000\u0001" +
                    "\u0012\u0001\u0000\u0000\u0000\u0001\u0014\u0001\u0000\u0000\u0000\u0002" +
                    "\u0016\u0001\u0000\u0000\u0000\u0004$\u0001\u0000\u0000\u0000\u0006,\u0001" +
                    "\u0000\u0000\u0000\b5\u0001\u0000\u0000\u0000\n@\u0001\u0000\u0000\u0000" +
                    "\fG\u0001\u0000\u0000\u0000\u000eU\u0001\u0000\u0000\u0000\u0010`\u0001" +
                    "\u0000\u0000\u0000\u0012d\u0001\u0000\u0000\u0000\u0014i\u0001\u0000\u0000" +
                    "\u0000\u0016\u0017\u0005r\u0000\u0000\u0017\u0018\u0005e\u0000\u0000\u0018" +
                    "\u0019\u0005c\u0000\u0000\u0019\u001a\u0005o\u0000\u0000\u001a\u001b\u0005" +
                    "r\u0000\u0000\u001b\u001c\u0005d\u0000\u0000\u001c\u001d\u0005-\u0000" +
                    "\u0000\u001d\u001e\u0005c\u0000\u0000\u001e\u001f\u0005o\u0000\u0000\u001f" +
                    " \u0005u\u0000\u0000 !\u0005n\u0000\u0000!\"\u0005t\u0000\u0000\"#\u0005" +
                    ":\u0000\u0000#\u0003\u0001\u0000\u0000\u0000$%\u0005k\u0000\u0000%&\u0005" +
                    "i\u0000\u0000&\'\u0005n\u0000\u0000\'(\u0005d\u0000\u0000()\u0005:\u0000" +
                    "\u0000)*\u0001\u0000\u0000\u0000*+\u0006\u0001\u0000\u0000+\u0005\u0001" +
                    "\u0000\u0000\u0000,-\u0005f\u0000\u0000-.\u0005i\u0000\u0000./\u0005x" +
                    "\u0000\u0000/0\u0005e\u0000\u000001\u0005d\u0000\u000012\u0005:\u0000" +
                    "\u000023\u0001\u0000\u0000\u000034\u0006\u0002\u0000\u00004\u0007\u0001" +
                    "\u0000\u0000\u000056\u0005f\u0000\u000067\u0005l\u0000\u000078\u0005a" +
                    "\u0000\u000089\u0005v\u0000\u00009:\u0005o\u0000\u0000:;\u0005r\u0000" +
                    "\u0000;<\u0005:\u0000\u0000<=\u0001\u0000\u0000\u0000=>\u0006\u0003\u0000" +
                    "\u0000>\t\u0001\u0000\u0000\u0000?A\u0005-\u0000\u0000@?\u0001\u0000\u0000" +
                    "\u0000@A\u0001\u0000\u0000\u0000AC\u0001\u0000\u0000\u0000BD\u000209\u0000" +
                    "CB\u0001\u0000\u0000\u0000DE\u0001\u0000\u0000\u0000EC\u0001\u0000\u0000" +
                    "\u0000EF\u0001\u0000\u0000\u0000F\u000b\u0001\u0000\u0000\u0000GK\u0005" +
                    "#\u0000\u0000HJ\b\u0000\u0000\u0000IH\u0001\u0000\u0000\u0000JM\u0001" +
                    "\u0000\u0000\u0000KI\u0001\u0000\u0000\u0000KL\u0001\u0000\u0000\u0000" +
                    "LN\u0001\u0000\u0000\u0000MK\u0001\u0000\u0000\u0000NO\u0005\n\u0000\u0000" +
                    "OP\u0001\u0000\u0000\u0000PQ\u0006\u0005\u0001\u0000Q\r\u0001\u0000\u0000" +
                    "\u0000RT\u0005 \u0000\u0000SR\u0001\u0000\u0000\u0000TW\u0001\u0000\u0000" +
                    "\u0000US\u0001\u0000\u0000\u0000UV\u0001\u0000\u0000\u0000VY\u0001\u0000" +
                    "\u0000\u0000WU\u0001\u0000\u0000\u0000XZ\u0005\r\u0000\u0000YX\u0001\u0000" +
                    "\u0000\u0000YZ\u0001\u0000\u0000\u0000Z[\u0001\u0000\u0000\u0000[\\\u0005" +
                    "\n\u0000\u0000\\]\u0001\u0000\u0000\u0000]^\u0006\u0006\u0001\u0000^\u000f" +
                    "\u0001\u0000\u0000\u0000_a\b\u0001\u0000\u0000`_\u0001\u0000\u0000\u0000" +
                    "ab\u0001\u0000\u0000\u0000b`\u0001\u0000\u0000\u0000bc\u0001\u0000\u0000" +
                    "\u0000c\u0011\u0001\u0000\u0000\u0000de\u0005:\u0000\u0000e\u0013\u0001" +
                    "\u0000\u0000\u0000fh\u0005\r\u0000\u0000gf\u0001\u0000\u0000\u0000hk\u0001" +
                    "\u0000\u0000\u0000ig\u0001\u0000\u0000\u0000ij\u0001\u0000\u0000\u0000" +
                    "jl\u0001\u0000\u0000\u0000ki\u0001\u0000\u0000\u0000lm\u0005\n\u0000\u0000" +
                    "mn\u0001\u0000\u0000\u0000no\u0006\t\u0002\u0000op\u0006\t\u0001\u0000" +
                    "p\u0015\u0001\u0000\u0000\u0000\t\u0000\u0001@EKUYbi\u0003\u0005\u0001" +
                    "\u0000\u0006\u0000\u0000\u0004\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}