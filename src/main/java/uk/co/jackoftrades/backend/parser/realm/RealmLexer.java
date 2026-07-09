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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Realm.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.realm;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class RealmLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, NAME = 3, STAT = 4, VERB = 5, SPELL_NOUN = 6, BOOK_NOUN = 7, UCASE = 8,
            LCASE = 9;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "COMMENT", "EOL", "NAME", "STAT", "VERB", "SPELL_NOUN", "BOOK_NOUN",
                "UCASE", "LCASE"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'name:'", "'stat:'", "'verb:'", "'spell-noun:'", "'book-noun:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "NAME", "STAT", "VERB", "SPELL_NOUN", "BOOK_NOUN",
                "UCASE", "LCASE"
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


    public RealmLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "Realm.g4";
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
            "\u0004\u0000\ta\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001" +
                    "\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004" +
                    "\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007" +
                    "\u0007\u0007\u0002\b\u0007\b\u0001\u0000\u0001\u0000\u0005\u0000\u0016" +
                    "\b\u0000\n\u0000\f\u0000\u0019\t\u0000\u0001\u0000\u0004\u0000\u001c\b" +
                    "\u0000\u000b\u0000\f\u0000\u001d\u0001\u0000\u0001\u0000\u0001\u0001\u0005" +
                    "\u0001#\b\u0001\n\u0001\f\u0001&\t\u0001\u0001\u0001\u0003\u0001)\b\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0004\u0007Y\b\u0007" +
                    "\u000b\u0007\f\u0007Z\u0001\b\u0004\b^\b\b\u000b\b\f\b_\u0000\u0000\t" +
                    "\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r" +
                    "\u0007\u000f\b\u0011\t\u0001\u0000\u0002\u0001\u0000\n\n\u0002\u0000 " +
                    " azf\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000" +
                    "\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000" +
                    "\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000" +
                    "\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000" +
                    "\u0011\u0001\u0000\u0000\u0000\u0001\u0013\u0001\u0000\u0000\u0000\u0003" +
                    "$\u0001\u0000\u0000\u0000\u0005.\u0001\u0000\u0000\u0000\u00074\u0001" +
                    "\u0000\u0000\u0000\t:\u0001\u0000\u0000\u0000\u000b@\u0001\u0000\u0000" +
                    "\u0000\rL\u0001\u0000\u0000\u0000\u000fX\u0001\u0000\u0000\u0000\u0011" +
                    "]\u0001\u0000\u0000\u0000\u0013\u0017\u0005#\u0000\u0000\u0014\u0016\b" +
                    "\u0000\u0000\u0000\u0015\u0014\u0001\u0000\u0000\u0000\u0016\u0019\u0001" +
                    "\u0000\u0000\u0000\u0017\u0015\u0001\u0000\u0000\u0000\u0017\u0018\u0001" +
                    "\u0000\u0000\u0000\u0018\u001b\u0001\u0000\u0000\u0000\u0019\u0017\u0001" +
                    "\u0000\u0000\u0000\u001a\u001c\u0005\n\u0000\u0000\u001b\u001a\u0001\u0000" +
                    "\u0000\u0000\u001c\u001d\u0001\u0000\u0000\u0000\u001d\u001b\u0001\u0000" +
                    "\u0000\u0000\u001d\u001e\u0001\u0000\u0000\u0000\u001e\u001f\u0001\u0000" +
                    "\u0000\u0000\u001f \u0006\u0000\u0000\u0000 \u0002\u0001\u0000\u0000\u0000" +
                    "!#\u0005 \u0000\u0000\"!\u0001\u0000\u0000\u0000#&\u0001\u0000\u0000\u0000" +
                    "$\"\u0001\u0000\u0000\u0000$%\u0001\u0000\u0000\u0000%(\u0001\u0000\u0000" +
                    "\u0000&$\u0001\u0000\u0000\u0000\')\u0005\r\u0000\u0000(\'\u0001\u0000" +
                    "\u0000\u0000()\u0001\u0000\u0000\u0000)*\u0001\u0000\u0000\u0000*+\u0005" +
                    "\n\u0000\u0000+,\u0001\u0000\u0000\u0000,-\u0006\u0001\u0000\u0000-\u0004" +
                    "\u0001\u0000\u0000\u0000./\u0005n\u0000\u0000/0\u0005a\u0000\u000001\u0005" +
                    "m\u0000\u000012\u0005e\u0000\u000023\u0005:\u0000\u00003\u0006\u0001\u0000" +
                    "\u0000\u000045\u0005s\u0000\u000056\u0005t\u0000\u000067\u0005a\u0000" +
                    "\u000078\u0005t\u0000\u000089\u0005:\u0000\u00009\b\u0001\u0000\u0000" +
                    "\u0000:;\u0005v\u0000\u0000;<\u0005e\u0000\u0000<=\u0005r\u0000\u0000" +
                    "=>\u0005b\u0000\u0000>?\u0005:\u0000\u0000?\n\u0001\u0000\u0000\u0000" +
                    "@A\u0005s\u0000\u0000AB\u0005p\u0000\u0000BC\u0005e\u0000\u0000CD\u0005" +
                    "l\u0000\u0000DE\u0005l\u0000\u0000EF\u0005-\u0000\u0000FG\u0005n\u0000" +
                    "\u0000GH\u0005o\u0000\u0000HI\u0005u\u0000\u0000IJ\u0005n\u0000\u0000" +
                    "JK\u0005:\u0000\u0000K\f\u0001\u0000\u0000\u0000LM\u0005b\u0000\u0000" +
                    "MN\u0005o\u0000\u0000NO\u0005o\u0000\u0000OP\u0005k\u0000\u0000PQ\u0005" +
                    "-\u0000\u0000QR\u0005n\u0000\u0000RS\u0005o\u0000\u0000ST\u0005u\u0000" +
                    "\u0000TU\u0005n\u0000\u0000UV\u0005:\u0000\u0000V\u000e\u0001\u0000\u0000" +
                    "\u0000WY\u0002AZ\u0000XW\u0001\u0000\u0000\u0000YZ\u0001\u0000\u0000\u0000" +
                    "ZX\u0001\u0000\u0000\u0000Z[\u0001\u0000\u0000\u0000[\u0010\u0001\u0000" +
                    "\u0000\u0000\\^\u0007\u0001\u0000\u0000]\\\u0001\u0000\u0000\u0000^_\u0001" +
                    "\u0000\u0000\u0000_]\u0001\u0000\u0000\u0000_`\u0001\u0000\u0000\u0000" +
                    "`\u0012\u0001\u0000\u0000\u0000\u0007\u0000\u0017\u001d$(Z_\u0001\u0006" +
                    "\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
	}
}