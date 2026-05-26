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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/MonsterBaseReader.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.monsterbasereader;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class MonsterBaseReaderLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, NAME = 3, GLYPH = 4, PAIN = 5, FLAGS = 6, DESC = 7, OR = 8, NUMBER = 9,
            SINGLE_CHAR = 10, TEXT = 11;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "COMMENT", "EOL", "NAME", "GLYPH", "PAIN", "FLAGS", "DESC", "OR", "NUMBER",
                "SINGLE_CHAR", "TEXT"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'name:'", "'glyph:'", "'pain:'", "'flags:'", "'desc:'",
                "'| '"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "NAME", "GLYPH", "PAIN", "FLAGS", "DESC", "OR",
                "NUMBER", "SINGLE_CHAR", "TEXT"
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


    public MonsterBaseReaderLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "MonsterBaseReader.g4";
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
            "\u0004\u0000\u000bo\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001" +
                    "\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004" +
                    "\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007" +
                    "\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0001\u0000" +
                    "\u0001\u0000\u0005\u0000\u001a\b\u0000\n\u0000\f\u0000\u001d\t\u0000\u0001" +
                    "\u0000\u0004\u0000 \b\u0000\u000b\u0000\f\u0000!\u0001\u0000\u0001\u0000" +
                    "\u0001\u0001\u0005\u0001\'\b\u0001\n\u0001\f\u0001*\t\u0001\u0001\u0001" +
                    "\u0003\u0001-\b\u0001\u0001\u0001\u0001\u0001\u0005\u00011\b\u0001\n\u0001" +
                    "\f\u00014\t\u0001\u0001\u0001\u0003\u00017\b\u0001\u0001\u0001\u0005\u0001" +
                    ":\b\u0001\n\u0001\f\u0001=\t\u0001\u0001\u0001\u0001\u0001\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0004\be\b\b" +
                    "\u000b\b\f\bf\u0001\t\u0001\t\u0001\n\u0004\nl\b\n\u000b\n\f\nm\u0000" +
                    "\u0000\u000b\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b" +
                    "\u0006\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b\u0001\u0000\u0004\u0001" +
                    "\u0000\n\n\u0001\u000009\u0006\u0000$$,,.9?Z\\\\az\u0006\u0000 !\'),9" +
                    "AZ__azw\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000" +
                    "\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000" +
                    "\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000" +
                    "\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000" +
                    "\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000" +
                    "\u0015\u0001\u0000\u0000\u0000\u0001\u0017\u0001\u0000\u0000\u0000\u0003" +
                    "(\u0001\u0000\u0000\u0000\u0005@\u0001\u0000\u0000\u0000\u0007F\u0001" +
                    "\u0000\u0000\u0000\tM\u0001\u0000\u0000\u0000\u000bS\u0001\u0000\u0000" +
                    "\u0000\rZ\u0001\u0000\u0000\u0000\u000f`\u0001\u0000\u0000\u0000\u0011" +
                    "d\u0001\u0000\u0000\u0000\u0013h\u0001\u0000\u0000\u0000\u0015k\u0001" +
                    "\u0000\u0000\u0000\u0017\u001b\u0005#\u0000\u0000\u0018\u001a\b\u0000" +
                    "\u0000\u0000\u0019\u0018\u0001\u0000\u0000\u0000\u001a\u001d\u0001\u0000" +
                    "\u0000\u0000\u001b\u0019\u0001\u0000\u0000\u0000\u001b\u001c\u0001\u0000" +
                    "\u0000\u0000\u001c\u001f\u0001\u0000\u0000\u0000\u001d\u001b\u0001\u0000" +
                    "\u0000\u0000\u001e \u0005\n\u0000\u0000\u001f\u001e\u0001\u0000\u0000" +
                    "\u0000 !\u0001\u0000\u0000\u0000!\u001f\u0001\u0000\u0000\u0000!\"\u0001" +
                    "\u0000\u0000\u0000\"#\u0001\u0000\u0000\u0000#$\u0006\u0000\u0000\u0000" +
                    "$\u0002\u0001\u0000\u0000\u0000%\'\u0005 \u0000\u0000&%\u0001\u0000\u0000" +
                    "\u0000\'*\u0001\u0000\u0000\u0000(&\u0001\u0000\u0000\u0000()\u0001\u0000" +
                    "\u0000\u0000),\u0001\u0000\u0000\u0000*(\u0001\u0000\u0000\u0000+-\u0005" +
                    "\r\u0000\u0000,+\u0001\u0000\u0000\u0000,-\u0001\u0000\u0000\u0000-.\u0001" +
                    "\u0000\u0000\u0000.;\u0005\n\u0000\u0000/1\u0005 \u0000\u00000/\u0001" +
                    "\u0000\u0000\u000014\u0001\u0000\u0000\u000020\u0001\u0000\u0000\u0000" +
                    "23\u0001\u0000\u0000\u000036\u0001\u0000\u0000\u000042\u0001\u0000\u0000" +
                    "\u000057\u0005\r\u0000\u000065\u0001\u0000\u0000\u000067\u0001\u0000\u0000" +
                    "\u000078\u0001\u0000\u0000\u00008:\u0005\n\u0000\u000092\u0001\u0000\u0000" +
                    "\u0000:=\u0001\u0000\u0000\u0000;9\u0001\u0000\u0000\u0000;<\u0001\u0000" +
                    "\u0000\u0000<>\u0001\u0000\u0000\u0000=;\u0001\u0000\u0000\u0000>?\u0006" +
                    "\u0001\u0000\u0000?\u0004\u0001\u0000\u0000\u0000@A\u0005n\u0000\u0000" +
                    "AB\u0005a\u0000\u0000BC\u0005m\u0000\u0000CD\u0005e\u0000\u0000DE\u0005" +
                    ":\u0000\u0000E\u0006\u0001\u0000\u0000\u0000FG\u0005g\u0000\u0000GH\u0005" +
                    "l\u0000\u0000HI\u0005y\u0000\u0000IJ\u0005p\u0000\u0000JK\u0005h\u0000" +
                    "\u0000KL\u0005:\u0000\u0000L\b\u0001\u0000\u0000\u0000MN\u0005p\u0000" +
                    "\u0000NO\u0005a\u0000\u0000OP\u0005i\u0000\u0000PQ\u0005n\u0000\u0000" +
                    "QR\u0005:\u0000\u0000R\n\u0001\u0000\u0000\u0000ST\u0005f\u0000\u0000" +
                    "TU\u0005l\u0000\u0000UV\u0005a\u0000\u0000VW\u0005g\u0000\u0000WX\u0005" +
                    "s\u0000\u0000XY\u0005:\u0000\u0000Y\f\u0001\u0000\u0000\u0000Z[\u0005" +
                    "d\u0000\u0000[\\\u0005e\u0000\u0000\\]\u0005s\u0000\u0000]^\u0005c\u0000" +
                    "\u0000^_\u0005:\u0000\u0000_\u000e\u0001\u0000\u0000\u0000`a\u0005|\u0000" +
                    "\u0000ab\u0005 \u0000\u0000b\u0010\u0001\u0000\u0000\u0000ce\u0007\u0001" +
                    "\u0000\u0000dc\u0001\u0000\u0000\u0000ef\u0001\u0000\u0000\u0000fd\u0001" +
                    "\u0000\u0000\u0000fg\u0001\u0000\u0000\u0000g\u0012\u0001\u0000\u0000" +
                    "\u0000hi\u0007\u0002\u0000\u0000i\u0014\u0001\u0000\u0000\u0000jl\u0007" +
                    "\u0003\u0000\u0000kj\u0001\u0000\u0000\u0000lm\u0001\u0000\u0000\u0000" +
                    "mk\u0001\u0000\u0000\u0000mn\u0001\u0000\u0000\u0000n\u0016\u0001\u0000" +
                    "\u0000\u0000\n\u0000\u001b!(,26;fm\u0001\u0006\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}