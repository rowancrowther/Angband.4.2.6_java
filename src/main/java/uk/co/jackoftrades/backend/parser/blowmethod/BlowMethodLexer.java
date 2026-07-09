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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/BlowMethod.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.blowmethod;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class BlowMethodLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, NAME = 3, CUT = 4, STUN = 5, MISS = 6, PHYS = 7, MSG = 8, ACT = 9,
            DESC = 10, BOOLEAN = 11, UCASE = 12, LCASE = 13;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "COMMENT", "EOL", "NAME", "CUT", "STUN", "MISS", "PHYS", "MSG", "ACT",
                "DESC", "BOOLEAN", "UCASE", "LCASE"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'name:'", "'cut:'", "'stun:'", "'miss:'", "'phys:'",
                "'msg:'", "'act:'", "'desc:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "NAME", "CUT", "STUN", "MISS", "PHYS", "MSG",
                "ACT", "DESC", "BOOLEAN", "UCASE", "LCASE"
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


    public BlowMethodLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "BlowMethod.g4";
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
            "\u0004\u0000\ro\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001" +
                    "\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004" +
                    "\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007" +
                    "\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b" +
                    "\u0007\u000b\u0002\f\u0007\f\u0001\u0000\u0001\u0000\u0005\u0000\u001e" +
                    "\b\u0000\n\u0000\f\u0000!\t\u0000\u0001\u0000\u0004\u0000$\b\u0000\u000b" +
                    "\u0000\f\u0000%\u0001\u0000\u0001\u0000\u0001\u0001\u0005\u0001+\b\u0001" +
                    "\n\u0001\f\u0001.\t\u0001\u0001\u0001\u0003\u00011\b\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\u000b\u0004\u000bg\b" +
                    "\u000b\u000b\u000b\f\u000bh\u0001\f\u0004\fl\b\f\u000b\f\f\fm\u0000\u0000" +
                    "\r\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006" +
                    "\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u0001\u0000" +
                    "\u0003\u0001\u0000\n\n\u0002\u0000AZ__\u0006\u0000 !\'\'??AZa{}}t\u0000" +
                    "\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000" +
                    "\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000" +
                    "\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r" +
                    "\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011" +
                    "\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015" +
                    "\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019" +
                    "\u0001\u0000\u0000\u0000\u0001\u001b\u0001\u0000\u0000\u0000\u0003,\u0001" +
                    "\u0000\u0000\u0000\u00056\u0001\u0000\u0000\u0000\u0007<\u0001\u0000\u0000" +
                    "\u0000\tA\u0001\u0000\u0000\u0000\u000bG\u0001\u0000\u0000\u0000\rM\u0001" +
                    "\u0000\u0000\u0000\u000fS\u0001\u0000\u0000\u0000\u0011X\u0001\u0000\u0000" +
                    "\u0000\u0013]\u0001\u0000\u0000\u0000\u0015c\u0001\u0000\u0000\u0000\u0017" +
                    "f\u0001\u0000\u0000\u0000\u0019k\u0001\u0000\u0000\u0000\u001b\u001f\u0005" +
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
                    "\u00005\u0004\u0001\u0000\u0000\u000067\u0005n\u0000\u000078\u0005a\u0000" +
                    "\u000089\u0005m\u0000\u00009:\u0005e\u0000\u0000:;\u0005:\u0000\u0000" +
                    ";\u0006\u0001\u0000\u0000\u0000<=\u0005c\u0000\u0000=>\u0005u\u0000\u0000" +
                    ">?\u0005t\u0000\u0000?@\u0005:\u0000\u0000@\b\u0001\u0000\u0000\u0000" +
                    "AB\u0005s\u0000\u0000BC\u0005t\u0000\u0000CD\u0005u\u0000\u0000DE\u0005" +
                    "n\u0000\u0000EF\u0005:\u0000\u0000F\n\u0001\u0000\u0000\u0000GH\u0005" +
                    "m\u0000\u0000HI\u0005i\u0000\u0000IJ\u0005s\u0000\u0000JK\u0005s\u0000" +
                    "\u0000KL\u0005:\u0000\u0000L\f\u0001\u0000\u0000\u0000MN\u0005p\u0000" +
                    "\u0000NO\u0005h\u0000\u0000OP\u0005y\u0000\u0000PQ\u0005s\u0000\u0000" +
                    "QR\u0005:\u0000\u0000R\u000e\u0001\u0000\u0000\u0000ST\u0005m\u0000\u0000" +
                    "TU\u0005s\u0000\u0000UV\u0005g\u0000\u0000VW\u0005:\u0000\u0000W\u0010" +
                    "\u0001\u0000\u0000\u0000XY\u0005a\u0000\u0000YZ\u0005c\u0000\u0000Z[\u0005" +
                    "t\u0000\u0000[\\\u0005:\u0000\u0000\\\u0012\u0001\u0000\u0000\u0000]^" +
                    "\u0005d\u0000\u0000^_\u0005e\u0000\u0000_`\u0005s\u0000\u0000`a\u0005" +
                    "c\u0000\u0000ab\u0005:\u0000\u0000b\u0014\u0001\u0000\u0000\u0000cd\u0002" +
                    "01\u0000d\u0016\u0001\u0000\u0000\u0000eg\u0007\u0001\u0000\u0000fe\u0001" +
                    "\u0000\u0000\u0000gh\u0001\u0000\u0000\u0000hf\u0001\u0000\u0000\u0000" +
                    "hi\u0001\u0000\u0000\u0000i\u0018\u0001\u0000\u0000\u0000jl\u0007\u0002" +
                    "\u0000\u0000kj\u0001\u0000\u0000\u0000lm\u0001\u0000\u0000\u0000mk\u0001" +
                    "\u0000\u0000\u0000mn\u0001\u0000\u0000\u0000n\u001a\u0001\u0000\u0000" +
                    "\u0000\u0007\u0000\u001f%,0hm\u0001\u0006\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}