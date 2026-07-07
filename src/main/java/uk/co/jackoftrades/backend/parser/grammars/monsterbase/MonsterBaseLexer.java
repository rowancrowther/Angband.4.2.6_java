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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/MonsterBaseLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.monsterbase;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class MonsterBaseLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, NAME = 2, GLYPH = 3, PAIN = 4, FLAGS = 5, DESC = 6, INTEGER = 7, COMMENT = 8,
            EOL = 9, SINGLE_CHAR = 10, STRING = 11, FLAG = 12, FLAG_OR = 13, FLAG_EOL = 14;
    public static final int
            GLYPH_MODE = 1, FREE_TEXT = 2, FLAG_MODE = 3;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE", "GLYPH_MODE", "FREE_TEXT", "FLAG_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "RECORD_COUNT", "NAME", "GLYPH", "PAIN", "FLAGS", "DESC", "INTEGER",
                "COMMENT", "EOL", "FLAG_BODY", "SINGLE_CHAR", "STRING", "FLAG", "FLAG_OR",
                "FLAG_EOL"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'name:'", "'glyph:'", "'pain:'", "'flags:'",
                "'desc:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "NAME", "GLYPH", "PAIN", "FLAGS", "DESC", "INTEGER",
                "COMMENT", "EOL", "SINGLE_CHAR", "STRING", "FLAG", "FLAG_OR", "FLAG_EOL"
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


    public MonsterBaseLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "MonsterBaseLexer.g4";
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
            "\u0004\u0000\u000e\u009a\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff" +
                    "\uffff\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001" +
                    "\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004" +
                    "\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007" +
                    "\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b" +
                    "\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0003" +
                    "\u0006Z\b\u0006\u0001\u0006\u0004\u0006]\b\u0006\u000b\u0006\f\u0006^" +
                    "\u0001\u0007\u0001\u0007\u0005\u0007c\b\u0007\n\u0007\f\u0007f\t\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0005\bm\b\b" +
                    "\n\b\f\bp\t\b\u0001\b\u0003\bs\b\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\t\u0001\t\u0005\t{\b\t\n\t\f\t~\t\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\u000b\u0004\u000b\u0085\b\u000b\u000b\u000b\f\u000b\u0086\u0001\u000b" +
                    "\u0001\u000b\u0001\f\u0001\f\u0001\r\u0003\r\u008e\b\r\u0001\r\u0001\r" +
                    "\u0003\r\u0092\b\r\u0001\u000e\u0004\u000e\u0095\b\u000e\u000b\u000e\f" +
                    "\u000e\u0096\u0001\u000e\u0001\u000e\u0000\u0000\u000f\u0004\u0001\u0006" +
                    "\u0002\b\u0003\n\u0004\f\u0005\u000e\u0006\u0010\u0007\u0012\b\u0014\t" +
                    "\u0016\u0000\u0018\n\u001a\u000b\u001c\f\u001e\r \u000e\u0004\u0000\u0001" +
                    "\u0002\u0003\u0003\u0001\u0000\n\n\u0003\u000009AZ__\u0002\u0000\n\n\r" +
                    "\r\u009f\u0000\u0004\u0001\u0000\u0000\u0000\u0000\u0006\u0001\u0000\u0000" +
                    "\u0000\u0000\b\u0001\u0000\u0000\u0000\u0000\n\u0001\u0000\u0000\u0000" +
                    "\u0000\f\u0001\u0000\u0000\u0000\u0000\u000e\u0001\u0000\u0000\u0000\u0000" +
                    "\u0010\u0001\u0000\u0000\u0000\u0000\u0012\u0001\u0000\u0000\u0000\u0000" +
                    "\u0014\u0001\u0000\u0000\u0000\u0001\u0018\u0001\u0000\u0000\u0000\u0002" +
                    "\u001a\u0001\u0000\u0000\u0000\u0003\u001c\u0001\u0000\u0000\u0000\u0003" +
                    "\u001e\u0001\u0000\u0000\u0000\u0003 \u0001\u0000\u0000\u0000\u0004\"" +
                    "\u0001\u0000\u0000\u0000\u00060\u0001\u0000\u0000\u0000\b8\u0001\u0000" +
                    "\u0000\u0000\nA\u0001\u0000\u0000\u0000\fG\u0001\u0000\u0000\u0000\u000e" +
                    "P\u0001\u0000\u0000\u0000\u0010Y\u0001\u0000\u0000\u0000\u0012`\u0001" +
                    "\u0000\u0000\u0000\u0014n\u0001\u0000\u0000\u0000\u0016x\u0001\u0000\u0000" +
                    "\u0000\u0018\u007f\u0001\u0000\u0000\u0000\u001a\u0084\u0001\u0000\u0000" +
                    "\u0000\u001c\u008a\u0001\u0000\u0000\u0000\u001e\u008d\u0001\u0000\u0000" +
                    "\u0000 \u0094\u0001\u0000\u0000\u0000\"#\u0005r\u0000\u0000#$\u0005e\u0000" +
                    "\u0000$%\u0005c\u0000\u0000%&\u0005o\u0000\u0000&\'\u0005r\u0000\u0000" +
                    "\'(\u0005d\u0000\u0000()\u0005-\u0000\u0000)*\u0005c\u0000\u0000*+\u0005" +
                    "o\u0000\u0000+,\u0005u\u0000\u0000,-\u0005n\u0000\u0000-.\u0005t\u0000" +
                    "\u0000./\u0005:\u0000\u0000/\u0005\u0001\u0000\u0000\u000001\u0005n\u0000" +
                    "\u000012\u0005a\u0000\u000023\u0005m\u0000\u000034\u0005e\u0000\u0000" +
                    "45\u0005:\u0000\u000056\u0001\u0000\u0000\u000067\u0006\u0001\u0000\u0000" +
                    "7\u0007\u0001\u0000\u0000\u000089\u0005g\u0000\u00009:\u0005l\u0000\u0000" +
                    ":;\u0005y\u0000\u0000;<\u0005p\u0000\u0000<=\u0005h\u0000\u0000=>\u0005" +
                    ":\u0000\u0000>?\u0001\u0000\u0000\u0000?@\u0006\u0002\u0001\u0000@\t\u0001" +
                    "\u0000\u0000\u0000AB\u0005p\u0000\u0000BC\u0005a\u0000\u0000CD\u0005i" +
                    "\u0000\u0000DE\u0005n\u0000\u0000EF\u0005:\u0000\u0000F\u000b\u0001\u0000" +
                    "\u0000\u0000GH\u0005f\u0000\u0000HI\u0005l\u0000\u0000IJ\u0005a\u0000" +
                    "\u0000JK\u0005g\u0000\u0000KL\u0005s\u0000\u0000LM\u0005:\u0000\u0000" +
                    "MN\u0001\u0000\u0000\u0000NO\u0006\u0004\u0002\u0000O\r\u0001\u0000\u0000" +
                    "\u0000PQ\u0005d\u0000\u0000QR\u0005e\u0000\u0000RS\u0005s\u0000\u0000" +
                    "ST\u0005c\u0000\u0000TU\u0005:\u0000\u0000UV\u0001\u0000\u0000\u0000V" +
                    "W\u0006\u0005\u0000\u0000W\u000f\u0001\u0000\u0000\u0000XZ\u0005-\u0000" +
                    "\u0000YX\u0001\u0000\u0000\u0000YZ\u0001\u0000\u0000\u0000Z\\\u0001\u0000" +
                    "\u0000\u0000[]\u000209\u0000\\[\u0001\u0000\u0000\u0000]^\u0001\u0000" +
                    "\u0000\u0000^\\\u0001\u0000\u0000\u0000^_\u0001\u0000\u0000\u0000_\u0011" +
                    "\u0001\u0000\u0000\u0000`d\u0005#\u0000\u0000ac\b\u0000\u0000\u0000ba" +
                    "\u0001\u0000\u0000\u0000cf\u0001\u0000\u0000\u0000db\u0001\u0000\u0000" +
                    "\u0000de\u0001\u0000\u0000\u0000eg\u0001\u0000\u0000\u0000fd\u0001\u0000" +
                    "\u0000\u0000gh\u0005\n\u0000\u0000hi\u0001\u0000\u0000\u0000ij\u0006\u0007" +
                    "\u0003\u0000j\u0013\u0001\u0000\u0000\u0000km\u0005 \u0000\u0000lk\u0001" +
                    "\u0000\u0000\u0000mp\u0001\u0000\u0000\u0000nl\u0001\u0000\u0000\u0000" +
                    "no\u0001\u0000\u0000\u0000or\u0001\u0000\u0000\u0000pn\u0001\u0000\u0000" +
                    "\u0000qs\u0005\r\u0000\u0000rq\u0001\u0000\u0000\u0000rs\u0001\u0000\u0000" +
                    "\u0000st\u0001\u0000\u0000\u0000tu\u0005\n\u0000\u0000uv\u0001\u0000\u0000" +
                    "\u0000vw\u0006\b\u0003\u0000w\u0015\u0001\u0000\u0000\u0000x|\u0002AZ" +
                    "\u0000y{\u0007\u0001\u0000\u0000zy\u0001\u0000\u0000\u0000{~\u0001\u0000" +
                    "\u0000\u0000|z\u0001\u0000\u0000\u0000|}\u0001\u0000\u0000\u0000}\u0017" +
                    "\u0001\u0000\u0000\u0000~|\u0001\u0000\u0000\u0000\u007f\u0080\b\u0002" +
                    "\u0000\u0000\u0080\u0081\u0001\u0000\u0000\u0000\u0081\u0082\u0006\n\u0004" +
                    "\u0000\u0082\u0019\u0001\u0000\u0000\u0000\u0083\u0085\b\u0002\u0000\u0000" +
                    "\u0084\u0083\u0001\u0000\u0000\u0000\u0085\u0086\u0001\u0000\u0000\u0000" +
                    "\u0086\u0084\u0001\u0000\u0000\u0000\u0086\u0087\u0001\u0000\u0000\u0000" +
                    "\u0087\u0088\u0001\u0000\u0000\u0000\u0088\u0089\u0006\u000b\u0004\u0000" +
                    "\u0089\u001b\u0001\u0000\u0000\u0000\u008a\u008b\u0003\u0016\t\u0000\u008b" +
                    "\u001d\u0001\u0000\u0000\u0000\u008c\u008e\u0005 \u0000\u0000\u008d\u008c" +
                    "\u0001\u0000\u0000\u0000\u008d\u008e\u0001\u0000\u0000\u0000\u008e\u008f" +
                    "\u0001\u0000\u0000\u0000\u008f\u0091\u0005|\u0000\u0000\u0090\u0092\u0005" +
                    " \u0000\u0000\u0091\u0090\u0001\u0000\u0000\u0000\u0091\u0092\u0001\u0000" +
                    "\u0000\u0000\u0092\u001f\u0001\u0000\u0000\u0000\u0093\u0095\u0007\u0002" +
                    "\u0000\u0000\u0094\u0093\u0001\u0000\u0000\u0000\u0095\u0096\u0001\u0000" +
                    "\u0000\u0000\u0096\u0094\u0001\u0000\u0000\u0000\u0096\u0097\u0001\u0000" +
                    "\u0000\u0000\u0097\u0098\u0001\u0000\u0000\u0000\u0098\u0099\u0006\u000e" +
                    "\u0004\u0000\u0099!\u0001\u0000\u0000\u0000\u000e\u0000\u0001\u0002\u0003" +
                    "Y^dnr|\u0086\u008d\u0091\u0096\u0005\u0005\u0002\u0000\u0005\u0001\u0000" +
                    "\u0005\u0003\u0000\u0006\u0000\u0000\u0004\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}