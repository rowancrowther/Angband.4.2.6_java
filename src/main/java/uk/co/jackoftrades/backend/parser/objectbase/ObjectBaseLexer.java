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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/ObjectBase.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.objectbase;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ObjectBaseLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            T__0 = 1, COMMENT = 2, EOL = 3, DEFAULT_MAX_STACK = 4, DEFAULT_BREAK_CHANCE = 5,
            NAME = 6, GRAPHICS = 7, BREAK = 8, MAX_STACK = 9, FLAGS = 10, NUMBER = 11, COLON = 12,
            TEXT = 13;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "T__0", "COMMENT", "EOL", "DEFAULT_MAX_STACK", "DEFAULT_BREAK_CHANCE",
                "NAME", "GRAPHICS", "BREAK", "MAX_STACK", "FLAGS", "NUMBER", "COLON",
                "TEXT"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'|'", null, null, "'default:max-stack:'", "'default:break-chance:'",
                "'name:'", "'graphics:'", "'break:'", "'max-stack:'", "'flags:'", null,
                "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, null, "COMMENT", "EOL", "DEFAULT_MAX_STACK", "DEFAULT_BREAK_CHANCE",
                "NAME", "GRAPHICS", "BREAK", "MAX_STACK", "FLAGS", "NUMBER", "COLON",
                "TEXT"
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


    public ObjectBaseLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "ObjectBase.g4";
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
            "\u0004\u0000\r\u0096\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001" +
                    "\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004" +
                    "\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007" +
                    "\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b" +
                    "\u0007\u000b\u0002\f\u0007\f\u0001\u0000\u0001\u0000\u0001\u0001\u0001" +
                    "\u0001\u0005\u0001 \b\u0001\n\u0001\f\u0001#\t\u0001\u0001\u0001\u0004" +
                    "\u0001&\b\u0001\u000b\u0001\f\u0001\'\u0001\u0001\u0001\u0001\u0001\u0002" +
                    "\u0005\u0002-\b\u0002\n\u0002\f\u00020\t\u0002\u0001\u0002\u0003\u0002" +
                    "3\b\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0004\n\u008c\b\n\u000b\n\f" +
                    "\n\u008d\u0001\u000b\u0001\u000b\u0001\f\u0004\f\u0093\b\f\u000b\f\f\f" +
                    "\u0094\u0000\u0000\r\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t" +
                    "\u0005\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017\f" +
                    "\u0019\r\u0001\u0000\u0002\u0001\u0000\n\n\u0006\u0000  --AZ__az~~\u009b" +
                    "\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000" +
                    "\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000" +
                    "\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000" +
                    "\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011" +
                    "\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015" +
                    "\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019" +
                    "\u0001\u0000\u0000\u0000\u0001\u001b\u0001\u0000\u0000\u0000\u0003\u001d" +
                    "\u0001\u0000\u0000\u0000\u0005.\u0001\u0000\u0000\u0000\u00078\u0001\u0000" +
                    "\u0000\u0000\tK\u0001\u0000\u0000\u0000\u000ba\u0001\u0000\u0000\u0000" +
                    "\rg\u0001\u0000\u0000\u0000\u000fq\u0001\u0000\u0000\u0000\u0011x\u0001" +
                    "\u0000\u0000\u0000\u0013\u0083\u0001\u0000\u0000\u0000\u0015\u008b\u0001" +
                    "\u0000\u0000\u0000\u0017\u008f\u0001\u0000\u0000\u0000\u0019\u0092\u0001" +
                    "\u0000\u0000\u0000\u001b\u001c\u0005|\u0000\u0000\u001c\u0002\u0001\u0000" +
                    "\u0000\u0000\u001d!\u0005#\u0000\u0000\u001e \b\u0000\u0000\u0000\u001f" +
                    "\u001e\u0001\u0000\u0000\u0000 #\u0001\u0000\u0000\u0000!\u001f\u0001" +
                    "\u0000\u0000\u0000!\"\u0001\u0000\u0000\u0000\"%\u0001\u0000\u0000\u0000" +
                    "#!\u0001\u0000\u0000\u0000$&\u0005\n\u0000\u0000%$\u0001\u0000\u0000\u0000" +
                    "&\'\u0001\u0000\u0000\u0000\'%\u0001\u0000\u0000\u0000\'(\u0001\u0000" +
                    "\u0000\u0000()\u0001\u0000\u0000\u0000)*\u0006\u0001\u0000\u0000*\u0004" +
                    "\u0001\u0000\u0000\u0000+-\u0005 \u0000\u0000,+\u0001\u0000\u0000\u0000" +
                    "-0\u0001\u0000\u0000\u0000.,\u0001\u0000\u0000\u0000./\u0001\u0000\u0000" +
                    "\u0000/2\u0001\u0000\u0000\u00000.\u0001\u0000\u0000\u000013\u0005\r\u0000" +
                    "\u000021\u0001\u0000\u0000\u000023\u0001\u0000\u0000\u000034\u0001\u0000" +
                    "\u0000\u000045\u0005\n\u0000\u000056\u0001\u0000\u0000\u000067\u0006\u0002" +
                    "\u0000\u00007\u0006\u0001\u0000\u0000\u000089\u0005d\u0000\u00009:\u0005" +
                    "e\u0000\u0000:;\u0005f\u0000\u0000;<\u0005a\u0000\u0000<=\u0005u\u0000" +
                    "\u0000=>\u0005l\u0000\u0000>?\u0005t\u0000\u0000?@\u0005:\u0000\u0000" +
                    "@A\u0005m\u0000\u0000AB\u0005a\u0000\u0000BC\u0005x\u0000\u0000CD\u0005" +
                    "-\u0000\u0000DE\u0005s\u0000\u0000EF\u0005t\u0000\u0000FG\u0005a\u0000" +
                    "\u0000GH\u0005c\u0000\u0000HI\u0005k\u0000\u0000IJ\u0005:\u0000\u0000" +
                    "J\b\u0001\u0000\u0000\u0000KL\u0005d\u0000\u0000LM\u0005e\u0000\u0000" +
                    "MN\u0005f\u0000\u0000NO\u0005a\u0000\u0000OP\u0005u\u0000\u0000PQ\u0005" +
                    "l\u0000\u0000QR\u0005t\u0000\u0000RS\u0005:\u0000\u0000ST\u0005b\u0000" +
                    "\u0000TU\u0005r\u0000\u0000UV\u0005e\u0000\u0000VW\u0005a\u0000\u0000" +
                    "WX\u0005k\u0000\u0000XY\u0005-\u0000\u0000YZ\u0005c\u0000\u0000Z[\u0005" +
                    "h\u0000\u0000[\\\u0005a\u0000\u0000\\]\u0005n\u0000\u0000]^\u0005c\u0000" +
                    "\u0000^_\u0005e\u0000\u0000_`\u0005:\u0000\u0000`\n\u0001\u0000\u0000" +
                    "\u0000ab\u0005n\u0000\u0000bc\u0005a\u0000\u0000cd\u0005m\u0000\u0000" +
                    "de\u0005e\u0000\u0000ef\u0005:\u0000\u0000f\f\u0001\u0000\u0000\u0000" +
                    "gh\u0005g\u0000\u0000hi\u0005r\u0000\u0000ij\u0005a\u0000\u0000jk\u0005" +
                    "p\u0000\u0000kl\u0005h\u0000\u0000lm\u0005i\u0000\u0000mn\u0005c\u0000" +
                    "\u0000no\u0005s\u0000\u0000op\u0005:\u0000\u0000p\u000e\u0001\u0000\u0000" +
                    "\u0000qr\u0005b\u0000\u0000rs\u0005r\u0000\u0000st\u0005e\u0000\u0000" +
                    "tu\u0005a\u0000\u0000uv\u0005k\u0000\u0000vw\u0005:\u0000\u0000w\u0010" +
                    "\u0001\u0000\u0000\u0000xy\u0005m\u0000\u0000yz\u0005a\u0000\u0000z{\u0005" +
                    "x\u0000\u0000{|\u0005-\u0000\u0000|}\u0005s\u0000\u0000}~\u0005t\u0000" +
                    "\u0000~\u007f\u0005a\u0000\u0000\u007f\u0080\u0005c\u0000\u0000\u0080" +
                    "\u0081\u0005k\u0000\u0000\u0081\u0082\u0005:\u0000\u0000\u0082\u0012\u0001" +
                    "\u0000\u0000\u0000\u0083\u0084\u0005f\u0000\u0000\u0084\u0085\u0005l\u0000" +
                    "\u0000\u0085\u0086\u0005a\u0000\u0000\u0086\u0087\u0005g\u0000\u0000\u0087" +
                    "\u0088\u0005s\u0000\u0000\u0088\u0089\u0005:\u0000\u0000\u0089\u0014\u0001" +
                    "\u0000\u0000\u0000\u008a\u008c\u000209\u0000\u008b\u008a\u0001\u0000\u0000" +
                    "\u0000\u008c\u008d\u0001\u0000\u0000\u0000\u008d\u008b\u0001\u0000\u0000" +
                    "\u0000\u008d\u008e\u0001\u0000\u0000\u0000\u008e\u0016\u0001\u0000\u0000" +
                    "\u0000\u008f\u0090\u0005:\u0000\u0000\u0090\u0018\u0001\u0000\u0000\u0000" +
                    "\u0091\u0093\u0007\u0001\u0000\u0000\u0092\u0091\u0001\u0000\u0000\u0000" +
                    "\u0093\u0094\u0001\u0000\u0000\u0000\u0094\u0092\u0001\u0000\u0000\u0000" +
                    "\u0094\u0095\u0001\u0000\u0000\u0000\u0095\u001a\u0001\u0000\u0000\u0000" +
                    "\u0007\u0000!\'.2\u008d\u0094\u0001\u0006\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}