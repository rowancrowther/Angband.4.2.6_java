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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/SummonLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.summon;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class SummonLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, NAME = 2, MSGT = 3, UNIQUES = 4, BASE = 5, RACE_FLAG = 6, FALLBACK = 7,
            DESC = 8, INTEGER = 9, COMMENT = 10, EOL = 11, FLAG = 12, STRING = 13;
    public static final int
            FLAG_MODE = 1, FREE_TEXT_MODE = 2;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE", "FLAG_MODE", "FREE_TEXT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "RECORD_COUNT", "NAME", "MSGT", "UNIQUES", "BASE", "RACE_FLAG", "FALLBACK",
                "DESC", "INTEGER", "COMMENT", "EOL", "FLAG_BODY", "FLAG", "STRING"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'name:'", "'msgt:'", "'uniques:'", "'base:'",
                "'race-flag:'", "'fallback:'", "'desc:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "NAME", "MSGT", "UNIQUES", "BASE", "RACE_FLAG",
                "FALLBACK", "DESC", "INTEGER", "COMMENT", "EOL", "FLAG", "STRING"
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


    public SummonLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "SummonLexer.g4";
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
            "\u0004\u0000\r\u00a1\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff\uffff" +
                    "\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002" +
                    "\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005" +
                    "\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002" +
                    "\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007\f\u0002" +
                    "\r\u0007\r\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0003\bq\b\b\u0001\b\u0004" +
                    "\bt\b\b\u000b\b\f\bu\u0001\t\u0001\t\u0005\tz\b\t\n\t\f\t}\t\t\u0001\t" +
                    "\u0001\t\u0001\t\u0001\t\u0001\n\u0005\n\u0084\b\n\n\n\f\n\u0087\t\n\u0001" +
                    "\n\u0003\n\u008a\b\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001" +
                    "\u000b\u0005\u000b\u0092\b\u000b\n\u000b\f\u000b\u0095\t\u000b\u0001\f" +
                    "\u0001\f\u0001\f\u0001\f\u0001\r\u0004\r\u009c\b\r\u000b\r\f\r\u009d\u0001" +
                    "\r\u0001\r\u0000\u0000\u000e\u0003\u0001\u0005\u0002\u0007\u0003\t\u0004" +
                    "\u000b\u0005\r\u0006\u000f\u0007\u0011\b\u0013\t\u0015\n\u0017\u000b\u0019" +
                    "\u0000\u001b\f\u001d\r\u0003\u0000\u0001\u0002\u0003\u0001\u0000\n\n\u0003" +
                    "\u000009AZ__\u0002\u0000\n\n\r\r\u00a4\u0000\u0003\u0001\u0000\u0000\u0000" +
                    "\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000" +
                    "\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000" +
                    "\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011" +
                    "\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015" +
                    "\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0001\u001b" +
                    "\u0001\u0000\u0000\u0000\u0002\u001d\u0001\u0000\u0000\u0000\u0003\u001f" +
                    "\u0001\u0000\u0000\u0000\u0005-\u0001\u0000\u0000\u0000\u00075\u0001\u0000" +
                    "\u0000\u0000\t=\u0001\u0000\u0000\u0000\u000bF\u0001\u0000\u0000\u0000" +
                    "\rN\u0001\u0000\u0000\u0000\u000f[\u0001\u0000\u0000\u0000\u0011g\u0001" +
                    "\u0000\u0000\u0000\u0013p\u0001\u0000\u0000\u0000\u0015w\u0001\u0000\u0000" +
                    "\u0000\u0017\u0085\u0001\u0000\u0000\u0000\u0019\u008f\u0001\u0000\u0000" +
                    "\u0000\u001b\u0096\u0001\u0000\u0000\u0000\u001d\u009b\u0001\u0000\u0000" +
                    "\u0000\u001f \u0005r\u0000\u0000 !\u0005e\u0000\u0000!\"\u0005c\u0000" +
                    "\u0000\"#\u0005o\u0000\u0000#$\u0005r\u0000\u0000$%\u0005d\u0000\u0000" +
                    "%&\u0005-\u0000\u0000&\'\u0005c\u0000\u0000\'(\u0005o\u0000\u0000()\u0005" +
                    "u\u0000\u0000)*\u0005n\u0000\u0000*+\u0005t\u0000\u0000+,\u0005:\u0000" +
                    "\u0000,\u0004\u0001\u0000\u0000\u0000-.\u0005n\u0000\u0000./\u0005a\u0000" +
                    "\u0000/0\u0005m\u0000\u000001\u0005e\u0000\u000012\u0005:\u0000\u0000" +
                    "23\u0001\u0000\u0000\u000034\u0006\u0001\u0000\u00004\u0006\u0001\u0000" +
                    "\u0000\u000056\u0005m\u0000\u000067\u0005s\u0000\u000078\u0005g\u0000" +
                    "\u000089\u0005t\u0000\u00009:\u0005:\u0000\u0000:;\u0001\u0000\u0000\u0000" +
                    ";<\u0006\u0002\u0001\u0000<\b\u0001\u0000\u0000\u0000=>\u0005u\u0000\u0000" +
                    ">?\u0005n\u0000\u0000?@\u0005i\u0000\u0000@A\u0005q\u0000\u0000AB\u0005" +
                    "u\u0000\u0000BC\u0005e\u0000\u0000CD\u0005s\u0000\u0000DE\u0005:\u0000" +
                    "\u0000E\n\u0001\u0000\u0000\u0000FG\u0005b\u0000\u0000GH\u0005a\u0000" +
                    "\u0000HI\u0005s\u0000\u0000IJ\u0005e\u0000\u0000JK\u0005:\u0000\u0000" +
                    "KL\u0001\u0000\u0000\u0000LM\u0006\u0004\u0000\u0000M\f\u0001\u0000\u0000" +
                    "\u0000NO\u0005r\u0000\u0000OP\u0005a\u0000\u0000PQ\u0005c\u0000\u0000" +
                    "QR\u0005e\u0000\u0000RS\u0005-\u0000\u0000ST\u0005f\u0000\u0000TU\u0005" +
                    "l\u0000\u0000UV\u0005a\u0000\u0000VW\u0005g\u0000\u0000WX\u0005:\u0000" +
                    "\u0000XY\u0001\u0000\u0000\u0000YZ\u0006\u0005\u0001\u0000Z\u000e\u0001" +
                    "\u0000\u0000\u0000[\\\u0005f\u0000\u0000\\]\u0005a\u0000\u0000]^\u0005" +
                    "l\u0000\u0000^_\u0005l\u0000\u0000_`\u0005b\u0000\u0000`a\u0005a\u0000" +
                    "\u0000ab\u0005c\u0000\u0000bc\u0005k\u0000\u0000cd\u0005:\u0000\u0000" +
                    "de\u0001\u0000\u0000\u0000ef\u0006\u0006\u0000\u0000f\u0010\u0001\u0000" +
                    "\u0000\u0000gh\u0005d\u0000\u0000hi\u0005e\u0000\u0000ij\u0005s\u0000" +
                    "\u0000jk\u0005c\u0000\u0000kl\u0005:\u0000\u0000lm\u0001\u0000\u0000\u0000" +
                    "mn\u0006\u0007\u0000\u0000n\u0012\u0001\u0000\u0000\u0000oq\u0005-\u0000" +
                    "\u0000po\u0001\u0000\u0000\u0000pq\u0001\u0000\u0000\u0000qs\u0001\u0000" +
                    "\u0000\u0000rt\u000209\u0000sr\u0001\u0000\u0000\u0000tu\u0001\u0000\u0000" +
                    "\u0000us\u0001\u0000\u0000\u0000uv\u0001\u0000\u0000\u0000v\u0014\u0001" +
                    "\u0000\u0000\u0000w{\u0005#\u0000\u0000xz\b\u0000\u0000\u0000yx\u0001" +
                    "\u0000\u0000\u0000z}\u0001\u0000\u0000\u0000{y\u0001\u0000\u0000\u0000" +
                    "{|\u0001\u0000\u0000\u0000|~\u0001\u0000\u0000\u0000}{\u0001\u0000\u0000" +
                    "\u0000~\u007f\u0005\n\u0000\u0000\u007f\u0080\u0001\u0000\u0000\u0000" +
                    "\u0080\u0081\u0006\t\u0002\u0000\u0081\u0016\u0001\u0000\u0000\u0000\u0082" +
                    "\u0084\u0005 \u0000\u0000\u0083\u0082\u0001\u0000\u0000\u0000\u0084\u0087" +
                    "\u0001\u0000\u0000\u0000\u0085\u0083\u0001\u0000\u0000\u0000\u0085\u0086" +
                    "\u0001\u0000\u0000\u0000\u0086\u0089\u0001\u0000\u0000\u0000\u0087\u0085" +
                    "\u0001\u0000\u0000\u0000\u0088\u008a\u0005\r\u0000\u0000\u0089\u0088\u0001" +
                    "\u0000\u0000\u0000\u0089\u008a\u0001\u0000\u0000\u0000\u008a\u008b\u0001" +
                    "\u0000\u0000\u0000\u008b\u008c\u0005\n\u0000\u0000\u008c\u008d\u0001\u0000" +
                    "\u0000\u0000\u008d\u008e\u0006\n\u0002\u0000\u008e\u0018\u0001\u0000\u0000" +
                    "\u0000\u008f\u0093\u0002AZ\u0000\u0090\u0092\u0007\u0001\u0000\u0000\u0091" +
                    "\u0090\u0001\u0000\u0000\u0000\u0092\u0095\u0001\u0000\u0000\u0000\u0093" +
                    "\u0091\u0001\u0000\u0000\u0000\u0093\u0094\u0001\u0000\u0000\u0000\u0094" +
                    "\u001a\u0001\u0000\u0000\u0000\u0095\u0093\u0001\u0000\u0000\u0000\u0096" +
                    "\u0097\u0003\u0019\u000b\u0000\u0097\u0098\u0001\u0000\u0000\u0000\u0098" +
                    "\u0099\u0006\f\u0003\u0000\u0099\u001c\u0001\u0000\u0000\u0000\u009a\u009c" +
                    "\b\u0002\u0000\u0000\u009b\u009a\u0001\u0000\u0000\u0000\u009c\u009d\u0001" +
                    "\u0000\u0000\u0000\u009d\u009b\u0001\u0000\u0000\u0000\u009d\u009e\u0001" +
                    "\u0000\u0000\u0000\u009e\u009f\u0001\u0000\u0000\u0000\u009f\u00a0\u0006" +
                    "\r\u0003\u0000\u00a0\u001e\u0001\u0000\u0000\u0000\n\u0000\u0001\u0002" +
                    "pu{\u0085\u0089\u0093\u009d\u0004\u0005\u0002\u0000\u0005\u0001\u0000" +
                    "\u0006\u0000\u0000\u0004\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}