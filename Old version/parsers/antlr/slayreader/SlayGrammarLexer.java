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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/SlayGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.slayreader;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class SlayGrammarLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, CODE = 3, NAME = 4, RACE_FLAG = 5, MULTIPLIER = 6, O_MULTIPLIER = 7,
            POWER = 8, MELEE_VERB = 9, RANGE_VERB = 10, NUMBER = 11, TEXT = 12;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "COMMENT", "EOL", "CODE", "NAME", "RACE_FLAG", "MULTIPLIER", "O_MULTIPLIER",
                "POWER", "MELEE_VERB", "RANGE_VERB", "NUMBER", "TEXT"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'code:'", "'name:'", "'race-flag:'", "'multiplier:'",
                "'o-multiplier:'", "'power:'", "'melee-verb:'", "'range-verb:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "CODE", "NAME", "RACE_FLAG", "MULTIPLIER", "O_MULTIPLIER",
                "POWER", "MELEE_VERB", "RANGE_VERB", "NUMBER", "TEXT"
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


    public SlayGrammarLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "SlayGrammar.g4";
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
            "\u0004\u0000\f\u0091\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001" +
                    "\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004" +
                    "\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007" +
                    "\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b" +
                    "\u0007\u000b\u0001\u0000\u0001\u0000\u0001\u0000\u0005\u0000\u001d\b\u0000" +
                    "\n\u0000\f\u0000 \t\u0000\u0001\u0000\u0004\u0000#\b\u0000\u000b\u0000" +
                    "\f\u0000$\u0001\u0000\u0001\u0000\u0001\u0001\u0005\u0001*\b\u0001\n\u0001" +
                    "\f\u0001-\t\u0001\u0001\u0001\u0003\u00010\b\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0001\n\u0004\n\u0087\b\n\u000b\n\f\n\u0088" +
                    "\u0001\u000b\u0001\u000b\u0005\u000b\u008d\b\u000b\n\u000b\f\u000b\u0090" +
                    "\t\u000b\u0000\u0000\f\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004" +
                    "\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017" +
                    "\f\u0001\u0000\u0005\u0001\u0000::\u0001\u0000\n\n\u0001\u000009\u0002" +
                    "\u0000AZaz\u0005\u0000  09AZ__az\u0096\u0000\u0001\u0001\u0000\u0000\u0000" +
                    "\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000" +
                    "\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000" +
                    "\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f" +
                    "\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013" +
                    "\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017" +
                    "\u0001\u0000\u0000\u0000\u0001\u0019\u0001\u0000\u0000\u0000\u0003+\u0001" +
                    "\u0000\u0000\u0000\u00055\u0001\u0000\u0000\u0000\u0007;\u0001\u0000\u0000" +
                    "\u0000\tA\u0001\u0000\u0000\u0000\u000bL\u0001\u0000\u0000\u0000\rX\u0001" +
                    "\u0000\u0000\u0000\u000ff\u0001\u0000\u0000\u0000\u0011m\u0001\u0000\u0000" +
                    "\u0000\u0013y\u0001\u0000\u0000\u0000\u0015\u0086\u0001\u0000\u0000\u0000" +
                    "\u0017\u008a\u0001\u0000\u0000\u0000\u0019\u001a\u0005#\u0000\u0000\u001a" +
                    "\u001e\b\u0000\u0000\u0000\u001b\u001d\b\u0001\u0000\u0000\u001c\u001b" +
                    "\u0001\u0000\u0000\u0000\u001d \u0001\u0000\u0000\u0000\u001e\u001c\u0001" +
                    "\u0000\u0000\u0000\u001e\u001f\u0001\u0000\u0000\u0000\u001f\"\u0001\u0000" +
                    "\u0000\u0000 \u001e\u0001\u0000\u0000\u0000!#\u0005\n\u0000\u0000\"!\u0001" +
                    "\u0000\u0000\u0000#$\u0001\u0000\u0000\u0000$\"\u0001\u0000\u0000\u0000" +
                    "$%\u0001\u0000\u0000\u0000%&\u0001\u0000\u0000\u0000&\'\u0006\u0000\u0000" +
                    "\u0000\'\u0002\u0001\u0000\u0000\u0000(*\u0005 \u0000\u0000)(\u0001\u0000" +
                    "\u0000\u0000*-\u0001\u0000\u0000\u0000+)\u0001\u0000\u0000\u0000+,\u0001" +
                    "\u0000\u0000\u0000,/\u0001\u0000\u0000\u0000-+\u0001\u0000\u0000\u0000" +
                    ".0\u0005\r\u0000\u0000/.\u0001\u0000\u0000\u0000/0\u0001\u0000\u0000\u0000" +
                    "01\u0001\u0000\u0000\u000012\u0005\n\u0000\u000023\u0001\u0000\u0000\u0000" +
                    "34\u0006\u0001\u0000\u00004\u0004\u0001\u0000\u0000\u000056\u0005c\u0000" +
                    "\u000067\u0005o\u0000\u000078\u0005d\u0000\u000089\u0005e\u0000\u0000" +
                    "9:\u0005:\u0000\u0000:\u0006\u0001\u0000\u0000\u0000;<\u0005n\u0000\u0000" +
                    "<=\u0005a\u0000\u0000=>\u0005m\u0000\u0000>?\u0005e\u0000\u0000?@\u0005" +
                    ":\u0000\u0000@\b\u0001\u0000\u0000\u0000AB\u0005r\u0000\u0000BC\u0005" +
                    "a\u0000\u0000CD\u0005c\u0000\u0000DE\u0005e\u0000\u0000EF\u0005-\u0000" +
                    "\u0000FG\u0005f\u0000\u0000GH\u0005l\u0000\u0000HI\u0005a\u0000\u0000" +
                    "IJ\u0005g\u0000\u0000JK\u0005:\u0000\u0000K\n\u0001\u0000\u0000\u0000" +
                    "LM\u0005m\u0000\u0000MN\u0005u\u0000\u0000NO\u0005l\u0000\u0000OP\u0005" +
                    "t\u0000\u0000PQ\u0005i\u0000\u0000QR\u0005p\u0000\u0000RS\u0005l\u0000" +
                    "\u0000ST\u0005i\u0000\u0000TU\u0005e\u0000\u0000UV\u0005r\u0000\u0000" +
                    "VW\u0005:\u0000\u0000W\f\u0001\u0000\u0000\u0000XY\u0005o\u0000\u0000" +
                    "YZ\u0005-\u0000\u0000Z[\u0005m\u0000\u0000[\\\u0005u\u0000\u0000\\]\u0005" +
                    "l\u0000\u0000]^\u0005t\u0000\u0000^_\u0005i\u0000\u0000_`\u0005p\u0000" +
                    "\u0000`a\u0005l\u0000\u0000ab\u0005i\u0000\u0000bc\u0005e\u0000\u0000" +
                    "cd\u0005r\u0000\u0000de\u0005:\u0000\u0000e\u000e\u0001\u0000\u0000\u0000" +
                    "fg\u0005p\u0000\u0000gh\u0005o\u0000\u0000hi\u0005w\u0000\u0000ij\u0005" +
                    "e\u0000\u0000jk\u0005r\u0000\u0000kl\u0005:\u0000\u0000l\u0010\u0001\u0000" +
                    "\u0000\u0000mn\u0005m\u0000\u0000no\u0005e\u0000\u0000op\u0005l\u0000" +
                    "\u0000pq\u0005e\u0000\u0000qr\u0005e\u0000\u0000rs\u0005-\u0000\u0000" +
                    "st\u0005v\u0000\u0000tu\u0005e\u0000\u0000uv\u0005r\u0000\u0000vw\u0005" +
                    "b\u0000\u0000wx\u0005:\u0000\u0000x\u0012\u0001\u0000\u0000\u0000yz\u0005" +
                    "r\u0000\u0000z{\u0005a\u0000\u0000{|\u0005n\u0000\u0000|}\u0005g\u0000" +
                    "\u0000}~\u0005e\u0000\u0000~\u007f\u0005-\u0000\u0000\u007f\u0080\u0005" +
                    "v\u0000\u0000\u0080\u0081\u0005e\u0000\u0000\u0081\u0082\u0005r\u0000" +
                    "\u0000\u0082\u0083\u0005b\u0000\u0000\u0083\u0084\u0005:\u0000\u0000\u0084" +
                    "\u0014\u0001\u0000\u0000\u0000\u0085\u0087\u0007\u0002\u0000\u0000\u0086" +
                    "\u0085\u0001\u0000\u0000\u0000\u0087\u0088\u0001\u0000\u0000\u0000\u0088" +
                    "\u0086\u0001\u0000\u0000\u0000\u0088\u0089\u0001\u0000\u0000\u0000\u0089" +
                    "\u0016\u0001\u0000\u0000\u0000\u008a\u008e\u0007\u0003\u0000\u0000\u008b" +
                    "\u008d\u0007\u0004\u0000\u0000\u008c\u008b\u0001\u0000\u0000\u0000\u008d" +
                    "\u0090\u0001\u0000\u0000\u0000\u008e\u008c\u0001\u0000\u0000\u0000\u008e" +
                    "\u008f\u0001\u0000\u0000\u0000\u008f\u0018\u0001\u0000\u0000\u0000\u0090" +
                    "\u008e\u0001\u0000\u0000\u0000\u0007\u0000\u001e$+/\u0088\u008e\u0001" +
                    "\u0006\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}