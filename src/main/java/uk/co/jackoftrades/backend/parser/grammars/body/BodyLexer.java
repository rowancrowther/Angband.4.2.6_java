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

// Generated from BodyLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.body;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class BodyLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, BODY = 2, SLOT = 3, INTEGER = 4, SLOT_NAME = 5, COLON = 6, COMMENT = 7,
            EOL = 8, STRING = 9, FREE_TEXT_EOL = 10;
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
                "RECORD_COUNT", "BODY", "SLOT", "INTEGER", "SLOT_NAME", "COLON", "COMMENT",
                "EOL", "STRING", "FREE_TEXT_EOL"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'body:'", "'slot:'", null, null, "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "BODY", "SLOT", "INTEGER", "SLOT_NAME", "COLON",
                "COMMENT", "EOL", "STRING", "FREE_TEXT_EOL"
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


    public BodyLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "BodyLexer.g4";
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
            "\u0004\u0000\nk\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000\u0007" +
                    "\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007" +
                    "\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007" +
                    "\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0003\u00034\b\u0003" +
                    "\u0001\u0003\u0004\u00037\b\u0003\u000b\u0003\f\u00038\u0001\u0004\u0004" +
                    "\u0004<\b\u0004\u000b\u0004\f\u0004=\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0006\u0001\u0006\u0005\u0006F\b\u0006\n\u0006\f\u0006" +
                    "I\t\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007" +
                    "\u0005\u0007P\b\u0007\n\u0007\f\u0007S\t\u0007\u0001\u0007\u0003\u0007" +
                    "V\b\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0004" +
                    "\b]\b\b\u000b\b\f\b^\u0001\t\u0005\tb\b\t\n\t\f\te\t\t\u0001\t\u0001\t" +
                    "\u0001\t\u0001\t\u0001\t\u0000\u0000\n\u0002\u0001\u0004\u0002\u0006\u0003" +
                    "\b\u0004\n\u0005\f\u0006\u000e\u0007\u0010\b\u0012\t\u0014\n\u0002\u0000" +
                    "\u0001\u0003\u0002\u0000AZ__\u0001\u0000\n\n\u0002\u0000\n\n\r\rq\u0000" +
                    "\u0002\u0001\u0000\u0000\u0000\u0000\u0004\u0001\u0000\u0000\u0000\u0000" +
                    "\u0006\u0001\u0000\u0000\u0000\u0000\b\u0001\u0000\u0000\u0000\u0000\n" +
                    "\u0001\u0000\u0000\u0000\u0000\f\u0001\u0000\u0000\u0000\u0000\u000e\u0001" +
                    "\u0000\u0000\u0000\u0000\u0010\u0001\u0000\u0000\u0000\u0001\u0012\u0001" +
                    "\u0000\u0000\u0000\u0001\u0014\u0001\u0000\u0000\u0000\u0002\u0016\u0001" +
                    "\u0000\u0000\u0000\u0004$\u0001\u0000\u0000\u0000\u0006,\u0001\u0000\u0000" +
                    "\u0000\b3\u0001\u0000\u0000\u0000\n;\u0001\u0000\u0000\u0000\f?\u0001" +
                    "\u0000\u0000\u0000\u000eC\u0001\u0000\u0000\u0000\u0010Q\u0001\u0000\u0000" +
                    "\u0000\u0012\\\u0001\u0000\u0000\u0000\u0014c\u0001\u0000\u0000\u0000" +
                    "\u0016\u0017\u0005r\u0000\u0000\u0017\u0018\u0005e\u0000\u0000\u0018\u0019" +
                    "\u0005c\u0000\u0000\u0019\u001a\u0005o\u0000\u0000\u001a\u001b\u0005r" +
                    "\u0000\u0000\u001b\u001c\u0005d\u0000\u0000\u001c\u001d\u0005-\u0000\u0000" +
                    "\u001d\u001e\u0005c\u0000\u0000\u001e\u001f\u0005o\u0000\u0000\u001f " +
                    "\u0005u\u0000\u0000 !\u0005n\u0000\u0000!\"\u0005t\u0000\u0000\"#\u0005" +
                    ":\u0000\u0000#\u0003\u0001\u0000\u0000\u0000$%\u0005b\u0000\u0000%&\u0005" +
                    "o\u0000\u0000&\'\u0005d\u0000\u0000\'(\u0005y\u0000\u0000()\u0005:\u0000" +
                    "\u0000)*\u0001\u0000\u0000\u0000*+\u0006\u0001\u0000\u0000+\u0005\u0001" +
                    "\u0000\u0000\u0000,-\u0005s\u0000\u0000-.\u0005l\u0000\u0000./\u0005o" +
                    "\u0000\u0000/0\u0005t\u0000\u000001\u0005:\u0000\u00001\u0007\u0001\u0000" +
                    "\u0000\u000024\u0005-\u0000\u000032\u0001\u0000\u0000\u000034\u0001\u0000" +
                    "\u0000\u000046\u0001\u0000\u0000\u000057\u000209\u000065\u0001\u0000\u0000" +
                    "\u000078\u0001\u0000\u0000\u000086\u0001\u0000\u0000\u000089\u0001\u0000" +
                    "\u0000\u00009\t\u0001\u0000\u0000\u0000:<\u0007\u0000\u0000\u0000;:\u0001" +
                    "\u0000\u0000\u0000<=\u0001\u0000\u0000\u0000=;\u0001\u0000\u0000\u0000" +
                    "=>\u0001\u0000\u0000\u0000>\u000b\u0001\u0000\u0000\u0000?@\u0005:\u0000" +
                    "\u0000@A\u0001\u0000\u0000\u0000AB\u0006\u0005\u0000\u0000B\r\u0001\u0000" +
                    "\u0000\u0000CG\u0005#\u0000\u0000DF\b\u0001\u0000\u0000ED\u0001\u0000" +
                    "\u0000\u0000FI\u0001\u0000\u0000\u0000GE\u0001\u0000\u0000\u0000GH\u0001" +
                    "\u0000\u0000\u0000HJ\u0001\u0000\u0000\u0000IG\u0001\u0000\u0000\u0000" +
                    "JK\u0005\n\u0000\u0000KL\u0001\u0000\u0000\u0000LM\u0006\u0006\u0001\u0000" +
                    "M\u000f\u0001\u0000\u0000\u0000NP\u0005 \u0000\u0000ON\u0001\u0000\u0000" +
                    "\u0000PS\u0001\u0000\u0000\u0000QO\u0001\u0000\u0000\u0000QR\u0001\u0000" +
                    "\u0000\u0000RU\u0001\u0000\u0000\u0000SQ\u0001\u0000\u0000\u0000TV\u0005" +
                    "\r\u0000\u0000UT\u0001\u0000\u0000\u0000UV\u0001\u0000\u0000\u0000VW\u0001" +
                    "\u0000\u0000\u0000WX\u0005\n\u0000\u0000XY\u0001\u0000\u0000\u0000YZ\u0006" +
                    "\u0007\u0001\u0000Z\u0011\u0001\u0000\u0000\u0000[]\b\u0002\u0000\u0000" +
                    "\\[\u0001\u0000\u0000\u0000]^\u0001\u0000\u0000\u0000^\\\u0001\u0000\u0000" +
                    "\u0000^_\u0001\u0000\u0000\u0000_\u0013\u0001\u0000\u0000\u0000`b\u0005" +
                    "\r\u0000\u0000a`\u0001\u0000\u0000\u0000be\u0001\u0000\u0000\u0000ca\u0001" +
                    "\u0000\u0000\u0000cd\u0001\u0000\u0000\u0000df\u0001\u0000\u0000\u0000" +
                    "ec\u0001\u0000\u0000\u0000fg\u0005\n\u0000\u0000gh\u0001\u0000\u0000\u0000" +
                    "hi\u0006\t\u0001\u0000ij\u0006\t\u0002\u0000j\u0015\u0001\u0000\u0000" +
                    "\u0000\n\u0000\u000138=GQU^c\u0003\u0005\u0001\u0000\u0006\u0000\u0000" +
                    "\u0004\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}