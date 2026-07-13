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

// Generated from RealmLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.realm;

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
            RECORD_COUNT = 1, NAME = 2, STAT = 3, VERB = 4, SPELL_NOUN = 5, BOOK_NOUN = 6, INTEGER = 7,
            COMMENT = 8, EOL = 9, STRING = 10, FREE_TEXT_EOL = 11;
    public static final int
            FREE_TEXT = 1;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE", "FREE_TEXT"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "RECORD_COUNT", "NAME", "STAT", "VERB", "SPELL_NOUN", "BOOK_NOUN", "INTEGER",
                "COMMENT", "EOL", "STRING", "FREE_TEXT_EOL"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'name:'", "'stat:'", "'verb:'", "'spell-noun:'",
                "'book-noun:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "NAME", "STAT", "VERB", "SPELL_NOUN", "BOOK_NOUN",
                "INTEGER", "COMMENT", "EOL", "STRING", "FREE_TEXT_EOL"
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
        return "RealmLexer.g4";
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
            "\u0004\u0000\u000b\u0089\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000" +
                    "\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003" +
                    "\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006" +
                    "\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002" +
                    "\n\u0007\n\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0006\u0003\u0006[\b\u0006\u0001\u0006\u0004\u0006^\b\u0006\u000b" +
                    "\u0006\f\u0006_\u0001\u0007\u0001\u0007\u0005\u0007d\b\u0007\n\u0007\f" +
                    "\u0007g\t\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\b\u0005\bn\b\b\n\b\f\bq\t\b\u0001\b\u0003\bt\b\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\t\u0004\t{\b\t\u000b\t\f\t|\u0001\n\u0005\n\u0080\b\n" +
                    "\n\n\f\n\u0083\t\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0000\u0000" +
                    "\u000b\u0002\u0001\u0004\u0002\u0006\u0003\b\u0004\n\u0005\f\u0006\u000e" +
                    "\u0007\u0010\b\u0012\t\u0014\n\u0016\u000b\u0002\u0000\u0001\u0002\u0001" +
                    "\u0000\n\n\u0002\u0000\n\n\r\r\u008e\u0000\u0002\u0001\u0000\u0000\u0000" +
                    "\u0000\u0004\u0001\u0000\u0000\u0000\u0000\u0006\u0001\u0000\u0000\u0000" +
                    "\u0000\b\u0001\u0000\u0000\u0000\u0000\n\u0001\u0000\u0000\u0000\u0000" +
                    "\f\u0001\u0000\u0000\u0000\u0000\u000e\u0001\u0000\u0000\u0000\u0000\u0010" +
                    "\u0001\u0000\u0000\u0000\u0000\u0012\u0001\u0000\u0000\u0000\u0001\u0014" +
                    "\u0001\u0000\u0000\u0000\u0001\u0016\u0001\u0000\u0000\u0000\u0002\u0018" +
                    "\u0001\u0000\u0000\u0000\u0004&\u0001\u0000\u0000\u0000\u0006.\u0001\u0000" +
                    "\u0000\u0000\b6\u0001\u0000\u0000\u0000\n>\u0001\u0000\u0000\u0000\fL" +
                    "\u0001\u0000\u0000\u0000\u000eZ\u0001\u0000\u0000\u0000\u0010a\u0001\u0000" +
                    "\u0000\u0000\u0012o\u0001\u0000\u0000\u0000\u0014z\u0001\u0000\u0000\u0000" +
                    "\u0016\u0081\u0001\u0000\u0000\u0000\u0018\u0019\u0005r\u0000\u0000\u0019" +
                    "\u001a\u0005e\u0000\u0000\u001a\u001b\u0005c\u0000\u0000\u001b\u001c\u0005" +
                    "o\u0000\u0000\u001c\u001d\u0005r\u0000\u0000\u001d\u001e\u0005d\u0000" +
                    "\u0000\u001e\u001f\u0005-\u0000\u0000\u001f \u0005c\u0000\u0000 !\u0005" +
                    "o\u0000\u0000!\"\u0005u\u0000\u0000\"#\u0005n\u0000\u0000#$\u0005t\u0000" +
                    "\u0000$%\u0005:\u0000\u0000%\u0003\u0001\u0000\u0000\u0000&\'\u0005n\u0000" +
                    "\u0000\'(\u0005a\u0000\u0000()\u0005m\u0000\u0000)*\u0005e\u0000\u0000" +
                    "*+\u0005:\u0000\u0000+,\u0001\u0000\u0000\u0000,-\u0006\u0001\u0000\u0000" +
                    "-\u0005\u0001\u0000\u0000\u0000./\u0005s\u0000\u0000/0\u0005t\u0000\u0000" +
                    "01\u0005a\u0000\u000012\u0005t\u0000\u000023\u0005:\u0000\u000034\u0001" +
                    "\u0000\u0000\u000045\u0006\u0002\u0000\u00005\u0007\u0001\u0000\u0000" +
                    "\u000067\u0005v\u0000\u000078\u0005e\u0000\u000089\u0005r\u0000\u0000" +
                    "9:\u0005b\u0000\u0000:;\u0005:\u0000\u0000;<\u0001\u0000\u0000\u0000<" +
                    "=\u0006\u0003\u0000\u0000=\t\u0001\u0000\u0000\u0000>?\u0005s\u0000\u0000" +
                    "?@\u0005p\u0000\u0000@A\u0005e\u0000\u0000AB\u0005l\u0000\u0000BC\u0005" +
                    "l\u0000\u0000CD\u0005-\u0000\u0000DE\u0005n\u0000\u0000EF\u0005o\u0000" +
                    "\u0000FG\u0005u\u0000\u0000GH\u0005n\u0000\u0000HI\u0005:\u0000\u0000" +
                    "IJ\u0001\u0000\u0000\u0000JK\u0006\u0004\u0000\u0000K\u000b\u0001\u0000" +
                    "\u0000\u0000LM\u0005b\u0000\u0000MN\u0005o\u0000\u0000NO\u0005o\u0000" +
                    "\u0000OP\u0005k\u0000\u0000PQ\u0005-\u0000\u0000QR\u0005n\u0000\u0000" +
                    "RS\u0005o\u0000\u0000ST\u0005u\u0000\u0000TU\u0005n\u0000\u0000UV\u0005" +
                    ":\u0000\u0000VW\u0001\u0000\u0000\u0000WX\u0006\u0005\u0000\u0000X\r\u0001" +
                    "\u0000\u0000\u0000Y[\u0005-\u0000\u0000ZY\u0001\u0000\u0000\u0000Z[\u0001" +
                    "\u0000\u0000\u0000[]\u0001\u0000\u0000\u0000\\^\u000209\u0000]\\\u0001" +
                    "\u0000\u0000\u0000^_\u0001\u0000\u0000\u0000_]\u0001\u0000\u0000\u0000" +
                    "_`\u0001\u0000\u0000\u0000`\u000f\u0001\u0000\u0000\u0000ae\u0005#\u0000" +
                    "\u0000bd\b\u0000\u0000\u0000cb\u0001\u0000\u0000\u0000dg\u0001\u0000\u0000" +
                    "\u0000ec\u0001\u0000\u0000\u0000ef\u0001\u0000\u0000\u0000fh\u0001\u0000" +
                    "\u0000\u0000ge\u0001\u0000\u0000\u0000hi\u0005\n\u0000\u0000ij\u0001\u0000" +
                    "\u0000\u0000jk\u0006\u0007\u0001\u0000k\u0011\u0001\u0000\u0000\u0000" +
                    "ln\u0005 \u0000\u0000ml\u0001\u0000\u0000\u0000nq\u0001\u0000\u0000\u0000" +
                    "om\u0001\u0000\u0000\u0000op\u0001\u0000\u0000\u0000ps\u0001\u0000\u0000" +
                    "\u0000qo\u0001\u0000\u0000\u0000rt\u0005\r\u0000\u0000sr\u0001\u0000\u0000" +
                    "\u0000st\u0001\u0000\u0000\u0000tu\u0001\u0000\u0000\u0000uv\u0005\n\u0000" +
                    "\u0000vw\u0001\u0000\u0000\u0000wx\u0006\b\u0001\u0000x\u0013\u0001\u0000" +
                    "\u0000\u0000y{\b\u0001\u0000\u0000zy\u0001\u0000\u0000\u0000{|\u0001\u0000" +
                    "\u0000\u0000|z\u0001\u0000\u0000\u0000|}\u0001\u0000\u0000\u0000}\u0015" +
                    "\u0001\u0000\u0000\u0000~\u0080\u0005\r\u0000\u0000\u007f~\u0001\u0000" +
                    "\u0000\u0000\u0080\u0083\u0001\u0000\u0000\u0000\u0081\u007f\u0001\u0000" +
                    "\u0000\u0000\u0081\u0082\u0001\u0000\u0000\u0000\u0082\u0084\u0001\u0000" +
                    "\u0000\u0000\u0083\u0081\u0001\u0000\u0000\u0000\u0084\u0085\u0005\n\u0000" +
                    "\u0000\u0085\u0086\u0001\u0000\u0000\u0000\u0086\u0087\u0006\n\u0001\u0000" +
                    "\u0087\u0088\u0006\n\u0002\u0000\u0088\u0017\u0001\u0000\u0000\u0000\t" +
                    "\u0000\u0001Z_eos|\u0081\u0003\u0005\u0001\u0000\u0006\u0000\u0000\u0004" +
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