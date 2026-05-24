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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/UIEntryBase.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.uientrybase;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class UIEntryBaseLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, NAME = 3, RENDERER = 4, COMBINE = 5, CATEGORY = 6, FLAGS = 7,
            DESC = 8, LCASEWORD = 9, UCASEWORD = 10, TEXT = 11;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "COMMENT", "EOL", "NAME", "RENDERER", "COMBINE", "CATEGORY", "FLAGS",
                "DESC", "LCASEWORD", "UCASEWORD", "TEXT"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'name:'", "'renderer:'", "'combine:'", "'category:'",
                "'flags:'", "'desc:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "NAME", "RENDERER", "COMBINE", "CATEGORY", "FLAGS",
                "DESC", "LCASEWORD", "UCASEWORD", "TEXT"
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


    public UIEntryBaseLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "UIEntryBase.g4";
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
            "\u0004\u0000\u000bq\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001" +
                    "\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004" +
                    "\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007" +
                    "\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0001\u0000" +
                    "\u0001\u0000\u0005\u0000\u001a\b\u0000\n\u0000\f\u0000\u001d\t\u0000\u0001" +
                    "\u0000\u0004\u0000 \b\u0000\u000b\u0000\f\u0000!\u0001\u0000\u0001\u0000" +
                    "\u0001\u0001\u0005\u0001\'\b\u0001\n\u0001\f\u0001*\t\u0001\u0001\u0001" +
                    "\u0003\u0001-\b\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\b\u0004\bd\b\b\u000b\b\f\be\u0001\t\u0004\ti\b\t\u000b\t\f\tj\u0001" +
                    "\n\u0004\nn\b\n\u000b\n\f\no\u0000\u0000\u000b\u0001\u0001\u0003\u0002" +
                    "\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013" +
                    "\n\u0015\u000b\u0001\u0000\u0004\u0001\u0000\n\n\u0003\u000009__az\u0003" +
                    "\u000009AZ__\u0004\u0000  ..AZazw\u0000\u0001\u0001\u0000\u0000\u0000" +
                    "\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000" +
                    "\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000" +
                    "\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f" +
                    "\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013" +
                    "\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0001\u0017" +
                    "\u0001\u0000\u0000\u0000\u0003(\u0001\u0000\u0000\u0000\u00052\u0001\u0000" +
                    "\u0000\u0000\u00078\u0001\u0000\u0000\u0000\tB\u0001\u0000\u0000\u0000" +
                    "\u000bK\u0001\u0000\u0000\u0000\rU\u0001\u0000\u0000\u0000\u000f\\\u0001" +
                    "\u0000\u0000\u0000\u0011c\u0001\u0000\u0000\u0000\u0013h\u0001\u0000\u0000" +
                    "\u0000\u0015m\u0001\u0000\u0000\u0000\u0017\u001b\u0005#\u0000\u0000\u0018" +
                    "\u001a\b\u0000\u0000\u0000\u0019\u0018\u0001\u0000\u0000\u0000\u001a\u001d" +
                    "\u0001\u0000\u0000\u0000\u001b\u0019\u0001\u0000\u0000\u0000\u001b\u001c" +
                    "\u0001\u0000\u0000\u0000\u001c\u001f\u0001\u0000\u0000\u0000\u001d\u001b" +
                    "\u0001\u0000\u0000\u0000\u001e \u0005\n\u0000\u0000\u001f\u001e\u0001" +
                    "\u0000\u0000\u0000 !\u0001\u0000\u0000\u0000!\u001f\u0001\u0000\u0000" +
                    "\u0000!\"\u0001\u0000\u0000\u0000\"#\u0001\u0000\u0000\u0000#$\u0006\u0000" +
                    "\u0000\u0000$\u0002\u0001\u0000\u0000\u0000%\'\u0005 \u0000\u0000&%\u0001" +
                    "\u0000\u0000\u0000\'*\u0001\u0000\u0000\u0000(&\u0001\u0000\u0000\u0000" +
                    "()\u0001\u0000\u0000\u0000),\u0001\u0000\u0000\u0000*(\u0001\u0000\u0000" +
                    "\u0000+-\u0005\r\u0000\u0000,+\u0001\u0000\u0000\u0000,-\u0001\u0000\u0000" +
                    "\u0000-.\u0001\u0000\u0000\u0000./\u0005\n\u0000\u0000/0\u0001\u0000\u0000" +
                    "\u000001\u0006\u0001\u0000\u00001\u0004\u0001\u0000\u0000\u000023\u0005" +
                    "n\u0000\u000034\u0005a\u0000\u000045\u0005m\u0000\u000056\u0005e\u0000" +
                    "\u000067\u0005:\u0000\u00007\u0006\u0001\u0000\u0000\u000089\u0005r\u0000" +
                    "\u00009:\u0005e\u0000\u0000:;\u0005n\u0000\u0000;<\u0005d\u0000\u0000" +
                    "<=\u0005e\u0000\u0000=>\u0005r\u0000\u0000>?\u0005e\u0000\u0000?@\u0005" +
                    "r\u0000\u0000@A\u0005:\u0000\u0000A\b\u0001\u0000\u0000\u0000BC\u0005" +
                    "c\u0000\u0000CD\u0005o\u0000\u0000DE\u0005m\u0000\u0000EF\u0005b\u0000" +
                    "\u0000FG\u0005i\u0000\u0000GH\u0005n\u0000\u0000HI\u0005e\u0000\u0000" +
                    "IJ\u0005:\u0000\u0000J\n\u0001\u0000\u0000\u0000KL\u0005c\u0000\u0000" +
                    "LM\u0005a\u0000\u0000MN\u0005t\u0000\u0000NO\u0005e\u0000\u0000OP\u0005" +
                    "g\u0000\u0000PQ\u0005o\u0000\u0000QR\u0005r\u0000\u0000RS\u0005y\u0000" +
                    "\u0000ST\u0005:\u0000\u0000T\f\u0001\u0000\u0000\u0000UV\u0005f\u0000" +
                    "\u0000VW\u0005l\u0000\u0000WX\u0005a\u0000\u0000XY\u0005g\u0000\u0000" +
                    "YZ\u0005s\u0000\u0000Z[\u0005:\u0000\u0000[\u000e\u0001\u0000\u0000\u0000" +
                    "\\]\u0005d\u0000\u0000]^\u0005e\u0000\u0000^_\u0005s\u0000\u0000_`\u0005" +
                    "c\u0000\u0000`a\u0005:\u0000\u0000a\u0010\u0001\u0000\u0000\u0000bd\u0007" +
                    "\u0001\u0000\u0000cb\u0001\u0000\u0000\u0000de\u0001\u0000\u0000\u0000" +
                    "ec\u0001\u0000\u0000\u0000ef\u0001\u0000\u0000\u0000f\u0012\u0001\u0000" +
                    "\u0000\u0000gi\u0007\u0002\u0000\u0000hg\u0001\u0000\u0000\u0000ij\u0001" +
                    "\u0000\u0000\u0000jh\u0001\u0000\u0000\u0000jk\u0001\u0000\u0000\u0000" +
                    "k\u0014\u0001\u0000\u0000\u0000ln\u0007\u0003\u0000\u0000ml\u0001\u0000" +
                    "\u0000\u0000no\u0001\u0000\u0000\u0000om\u0001\u0000\u0000\u0000op\u0001" +
                    "\u0000\u0000\u0000p\u0016\u0001\u0000\u0000\u0000\b\u0000\u001b!(,ejo" +
                    "\u0001\u0006\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}