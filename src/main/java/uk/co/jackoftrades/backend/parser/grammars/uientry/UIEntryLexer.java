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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/UIEntryLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.uientry;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class UIEntryLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, NAME = 2, PARAMETER = 3, RENDERER = 4, COMBINE = 5, PRIORITY = 6,
            CATEGORY = 7, FLAGS = 8, DESC = 9, LABEL = 10, LABEL5 = 11, LABEL2 = 12, TEMPLATE = 13,
            LTHAN = 14, GTHAN = 15, INTEGER = 16, LCASE = 17, PARAMETER_VALUE = 18, COMMENT = 19,
            EOL = 20, FLAG = 21, FLAG_OR = 22, FLAG_EOL = 23, STRING = 24;
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
                "RECORD_COUNT", "NAME", "PARAMETER", "RENDERER", "COMBINE", "PRIORITY",
                "CATEGORY", "FLAGS", "DESC", "LABEL", "LABEL5", "LABEL2", "TEMPLATE",
                "LTHAN", "GTHAN", "INTEGER", "LCASE", "PARAMETER_VALUE", "COMMENT", "EOL",
                "FLAG_BODY", "FLAG", "FLAG_OR", "FLAG_EOL", "STRING"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'name:'", "'parameter:'", "'renderer:'", "'combine:'",
                "'priority:'", "'category:'", "'flags:'", "'desc:'", "'label:'", "'label5:'",
                "'label2:'", "'template:'", "'<'", "'>'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "NAME", "PARAMETER", "RENDERER", "COMBINE", "PRIORITY",
                "CATEGORY", "FLAGS", "DESC", "LABEL", "LABEL5", "LABEL2", "TEMPLATE",
                "LTHAN", "GTHAN", "INTEGER", "LCASE", "PARAMETER_VALUE", "COMMENT", "EOL",
                "FLAG", "FLAG_OR", "FLAG_EOL", "STRING"
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


    public UIEntryLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "UIEntryLexer.g4";
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
            "\u0004\u0000\u0018\u0115\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff" +
                    "\uffff\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007" +
                    "\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007" +
                    "\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b" +
                    "\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007" +
                    "\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f\u0002" +
                    "\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012\u0002" +
                    "\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015\u0002" +
                    "\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b" +
                    "\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b" +
                    "\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001" +
                    "\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001" +
                    "\r\u0001\u000e\u0001\u000e\u0001\u000f\u0003\u000f\u00c5\b\u000f\u0001" +
                    "\u000f\u0004\u000f\u00c8\b\u000f\u000b\u000f\f\u000f\u00c9\u0001\u0010" +
                    "\u0004\u0010\u00cd\b\u0010\u000b\u0010\f\u0010\u00ce\u0001\u0011\u0004" +
                    "\u0011\u00d2\b\u0011\u000b\u0011\f\u0011\u00d3\u0001\u0012\u0001\u0012" +
                    "\u0005\u0012\u00d8\b\u0012\n\u0012\f\u0012\u00db\t\u0012\u0001\u0012\u0001" +
                    "\u0012\u0001\u0012\u0001\u0012\u0001\u0013\u0005\u0013\u00e2\b\u0013\n" +
                    "\u0013\f\u0013\u00e5\t\u0013\u0001\u0013\u0003\u0013\u00e8\b\u0013\u0001" +
                    "\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0005" +
                    "\u0014\u00f0\b\u0014\n\u0014\f\u0014\u00f3\t\u0014\u0001\u0015\u0001\u0015" +
                    "\u0001\u0016\u0003\u0016\u00f8\b\u0016\u0001\u0016\u0001\u0016\u0003\u0016" +
                    "\u00fc\b\u0016\u0001\u0017\u0005\u0017\u00ff\b\u0017\n\u0017\f\u0017\u0102" +
                    "\t\u0017\u0001\u0017\u0003\u0017\u0105\b\u0017\u0001\u0017\u0004\u0017" +
                    "\u0108\b\u0017\u000b\u0017\f\u0017\u0109\u0001\u0017\u0001\u0017\u0001" +
                    "\u0017\u0001\u0018\u0004\u0018\u0110\b\u0018\u000b\u0018\f\u0018\u0111" +
                    "\u0001\u0018\u0001\u0018\u0000\u0000\u0019\u0003\u0001\u0005\u0002\u0007" +
                    "\u0003\t\u0004\u000b\u0005\r\u0006\u000f\u0007\u0011\b\u0013\t\u0015\n" +
                    "\u0017\u000b\u0019\f\u001b\r\u001d\u000e\u001f\u000f!\u0010#\u0011%\u0012" +
                    "\'\u0013)\u0014+\u0000-\u0015/\u00161\u00173\u0018\u0003\u0000\u0001\u0002" +
                    "\u0004\u0003\u000009__az\u0001\u0000\n\n\u0003\u000009AZ__\u0002\u0000" +
                    "\n\n\r\r\u011f\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000" +
                    "\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000" +
                    "\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000" +
                    "\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000" +
                    "\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000" +
                    "\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000" +
                    "\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000\u0000" +
                    "\u0000\u001f\u0001\u0000\u0000\u0000\u0000!\u0001\u0000\u0000\u0000\u0000" +
                    "#\u0001\u0000\u0000\u0000\u0000%\u0001\u0000\u0000\u0000\u0000\'\u0001" +
                    "\u0000\u0000\u0000\u0000)\u0001\u0000\u0000\u0000\u0001-\u0001\u0000\u0000" +
                    "\u0000\u0001/\u0001\u0000\u0000\u0000\u00011\u0001\u0000\u0000\u0000\u0002" +
                    "3\u0001\u0000\u0000\u0000\u00035\u0001\u0000\u0000\u0000\u0005C\u0001" +
                    "\u0000\u0000\u0000\u0007I\u0001\u0000\u0000\u0000\tV\u0001\u0000\u0000" +
                    "\u0000\u000bb\u0001\u0000\u0000\u0000\rm\u0001\u0000\u0000\u0000\u000f" +
                    "y\u0001\u0000\u0000\u0000\u0011\u0085\u0001\u0000\u0000\u0000\u0013\u008e" +
                    "\u0001\u0000\u0000\u0000\u0015\u0096\u0001\u0000\u0000\u0000\u0017\u009f" +
                    "\u0001\u0000\u0000\u0000\u0019\u00a9\u0001\u0000\u0000\u0000\u001b\u00b3" +
                    "\u0001\u0000\u0000\u0000\u001d\u00bf\u0001\u0000\u0000\u0000\u001f\u00c1" +
                    "\u0001\u0000\u0000\u0000!\u00c4\u0001\u0000\u0000\u0000#\u00cc\u0001\u0000" +
                    "\u0000\u0000%\u00d1\u0001\u0000\u0000\u0000\'\u00d5\u0001\u0000\u0000" +
                    "\u0000)\u00e3\u0001\u0000\u0000\u0000+\u00ed\u0001\u0000\u0000\u0000-" +
                    "\u00f4\u0001\u0000\u0000\u0000/\u00f7\u0001\u0000\u0000\u00001\u0100\u0001" +
                    "\u0000\u0000\u00003\u010f\u0001\u0000\u0000\u000056\u0005r\u0000\u0000" +
                    "67\u0005e\u0000\u000078\u0005c\u0000\u000089\u0005o\u0000\u00009:\u0005" +
                    "r\u0000\u0000:;\u0005d\u0000\u0000;<\u0005-\u0000\u0000<=\u0005c\u0000" +
                    "\u0000=>\u0005o\u0000\u0000>?\u0005u\u0000\u0000?@\u0005n\u0000\u0000" +
                    "@A\u0005t\u0000\u0000AB\u0005:\u0000\u0000B\u0004\u0001\u0000\u0000\u0000" +
                    "CD\u0005n\u0000\u0000DE\u0005a\u0000\u0000EF\u0005m\u0000\u0000FG\u0005" +
                    "e\u0000\u0000GH\u0005:\u0000\u0000H\u0006\u0001\u0000\u0000\u0000IJ\u0005" +
                    "p\u0000\u0000JK\u0005a\u0000\u0000KL\u0005r\u0000\u0000LM\u0005a\u0000" +
                    "\u0000MN\u0005m\u0000\u0000NO\u0005e\u0000\u0000OP\u0005t\u0000\u0000" +
                    "PQ\u0005e\u0000\u0000QR\u0005r\u0000\u0000RS\u0005:\u0000\u0000ST\u0001" +
                    "\u0000\u0000\u0000TU\u0006\u0002\u0000\u0000U\b\u0001\u0000\u0000\u0000" +
                    "VW\u0005r\u0000\u0000WX\u0005e\u0000\u0000XY\u0005n\u0000\u0000YZ\u0005" +
                    "d\u0000\u0000Z[\u0005e\u0000\u0000[\\\u0005r\u0000\u0000\\]\u0005e\u0000" +
                    "\u0000]^\u0005r\u0000\u0000^_\u0005:\u0000\u0000_`\u0001\u0000\u0000\u0000" +
                    "`a\u0006\u0003\u0000\u0000a\n\u0001\u0000\u0000\u0000bc\u0005c\u0000\u0000" +
                    "cd\u0005o\u0000\u0000de\u0005m\u0000\u0000ef\u0005b\u0000\u0000fg\u0005" +
                    "i\u0000\u0000gh\u0005n\u0000\u0000hi\u0005e\u0000\u0000ij\u0005:\u0000" +
                    "\u0000jk\u0001\u0000\u0000\u0000kl\u0006\u0004\u0001\u0000l\f\u0001\u0000" +
                    "\u0000\u0000mn\u0005p\u0000\u0000no\u0005r\u0000\u0000op\u0005i\u0000" +
                    "\u0000pq\u0005o\u0000\u0000qr\u0005r\u0000\u0000rs\u0005i\u0000\u0000" +
                    "st\u0005t\u0000\u0000tu\u0005y\u0000\u0000uv\u0005:\u0000\u0000vw\u0001" +
                    "\u0000\u0000\u0000wx\u0006\u0005\u0000\u0000x\u000e\u0001\u0000\u0000" +
                    "\u0000yz\u0005c\u0000\u0000z{\u0005a\u0000\u0000{|\u0005t\u0000\u0000" +
                    "|}\u0005e\u0000\u0000}~\u0005g\u0000\u0000~\u007f\u0005o\u0000\u0000\u007f" +
                    "\u0080\u0005r\u0000\u0000\u0080\u0081\u0005y\u0000\u0000\u0081\u0082\u0005" +
                    ":\u0000\u0000\u0082\u0083\u0001\u0000\u0000\u0000\u0083\u0084\u0006\u0006" +
                    "\u0000\u0000\u0084\u0010\u0001\u0000\u0000\u0000\u0085\u0086\u0005f\u0000" +
                    "\u0000\u0086\u0087\u0005l\u0000\u0000\u0087\u0088\u0005a\u0000\u0000\u0088" +
                    "\u0089\u0005g\u0000\u0000\u0089\u008a\u0005s\u0000\u0000\u008a\u008b\u0005" +
                    ":\u0000\u0000\u008b\u008c\u0001\u0000\u0000\u0000\u008c\u008d\u0006\u0007" +
                    "\u0001\u0000\u008d\u0012\u0001\u0000\u0000\u0000\u008e\u008f\u0005d\u0000" +
                    "\u0000\u008f\u0090\u0005e\u0000\u0000\u0090\u0091\u0005s\u0000\u0000\u0091" +
                    "\u0092\u0005c\u0000\u0000\u0092\u0093\u0005:\u0000\u0000\u0093\u0094\u0001" +
                    "\u0000\u0000\u0000\u0094\u0095\u0006\b\u0000\u0000\u0095\u0014\u0001\u0000" +
                    "\u0000\u0000\u0096\u0097\u0005l\u0000\u0000\u0097\u0098\u0005a\u0000\u0000" +
                    "\u0098\u0099\u0005b\u0000\u0000\u0099\u009a\u0005e\u0000\u0000\u009a\u009b" +
                    "\u0005l\u0000\u0000\u009b\u009c\u0005:\u0000\u0000\u009c\u009d\u0001\u0000" +
                    "\u0000\u0000\u009d\u009e\u0006\t\u0000\u0000\u009e\u0016\u0001\u0000\u0000" +
                    "\u0000\u009f\u00a0\u0005l\u0000\u0000\u00a0\u00a1\u0005a\u0000\u0000\u00a1" +
                    "\u00a2\u0005b\u0000\u0000\u00a2\u00a3\u0005e\u0000\u0000\u00a3\u00a4\u0005" +
                    "l\u0000\u0000\u00a4\u00a5\u00055\u0000\u0000\u00a5\u00a6\u0005:\u0000" +
                    "\u0000\u00a6\u00a7\u0001\u0000\u0000\u0000\u00a7\u00a8\u0006\n\u0000\u0000" +
                    "\u00a8\u0018\u0001\u0000\u0000\u0000\u00a9\u00aa\u0005l\u0000\u0000\u00aa" +
                    "\u00ab\u0005a\u0000\u0000\u00ab\u00ac\u0005b\u0000\u0000\u00ac\u00ad\u0005" +
                    "e\u0000\u0000\u00ad\u00ae\u0005l\u0000\u0000\u00ae\u00af\u00052\u0000" +
                    "\u0000\u00af\u00b0\u0005:\u0000\u0000\u00b0\u00b1\u0001\u0000\u0000\u0000" +
                    "\u00b1\u00b2\u0006\u000b\u0000\u0000\u00b2\u001a\u0001\u0000\u0000\u0000" +
                    "\u00b3\u00b4\u0005t\u0000\u0000\u00b4\u00b5\u0005e\u0000\u0000\u00b5\u00b6" +
                    "\u0005m\u0000\u0000\u00b6\u00b7\u0005p\u0000\u0000\u00b7\u00b8\u0005l" +
                    "\u0000\u0000\u00b8\u00b9\u0005a\u0000\u0000\u00b9\u00ba\u0005t\u0000\u0000" +
                    "\u00ba\u00bb\u0005e\u0000\u0000\u00bb\u00bc\u0005:\u0000\u0000\u00bc\u00bd" +
                    "\u0001\u0000\u0000\u0000\u00bd\u00be\u0006\f\u0000\u0000\u00be\u001c\u0001" +
                    "\u0000\u0000\u0000\u00bf\u00c0\u0005<\u0000\u0000\u00c0\u001e\u0001\u0000" +
                    "\u0000\u0000\u00c1\u00c2\u0005>\u0000\u0000\u00c2 \u0001\u0000\u0000\u0000" +
                    "\u00c3\u00c5\u0005-\u0000\u0000\u00c4\u00c3\u0001\u0000\u0000\u0000\u00c4" +
                    "\u00c5\u0001\u0000\u0000\u0000\u00c5\u00c7\u0001\u0000\u0000\u0000\u00c6" +
                    "\u00c8\u000209\u0000\u00c7\u00c6\u0001\u0000\u0000\u0000\u00c8\u00c9\u0001" +
                    "\u0000\u0000\u0000\u00c9\u00c7\u0001\u0000\u0000\u0000\u00c9\u00ca\u0001" +
                    "\u0000\u0000\u0000\u00ca\"\u0001\u0000\u0000\u0000\u00cb\u00cd\u0007\u0000" +
                    "\u0000\u0000\u00cc\u00cb\u0001\u0000\u0000\u0000\u00cd\u00ce\u0001\u0000" +
                    "\u0000\u0000\u00ce\u00cc\u0001\u0000\u0000\u0000\u00ce\u00cf\u0001\u0000" +
                    "\u0000\u0000\u00cf$\u0001\u0000\u0000\u0000\u00d0\u00d2\u0002AZ\u0000" +
                    "\u00d1\u00d0\u0001\u0000\u0000\u0000\u00d2\u00d3\u0001\u0000\u0000\u0000" +
                    "\u00d3\u00d1\u0001\u0000\u0000\u0000\u00d3\u00d4\u0001\u0000\u0000\u0000" +
                    "\u00d4&\u0001\u0000\u0000\u0000\u00d5\u00d9\u0005#\u0000\u0000\u00d6\u00d8" +
                    "\b\u0001\u0000\u0000\u00d7\u00d6\u0001\u0000\u0000\u0000\u00d8\u00db\u0001" +
                    "\u0000\u0000\u0000\u00d9\u00d7\u0001\u0000\u0000\u0000\u00d9\u00da\u0001" +
                    "\u0000\u0000\u0000\u00da\u00dc\u0001\u0000\u0000\u0000\u00db\u00d9\u0001" +
                    "\u0000\u0000\u0000\u00dc\u00dd\u0005\n\u0000\u0000\u00dd\u00de\u0001\u0000" +
                    "\u0000\u0000\u00de\u00df\u0006\u0012\u0002\u0000\u00df(\u0001\u0000\u0000" +
                    "\u0000\u00e0\u00e2\u0005 \u0000\u0000\u00e1\u00e0\u0001\u0000\u0000\u0000" +
                    "\u00e2\u00e5\u0001\u0000\u0000\u0000\u00e3\u00e1\u0001\u0000\u0000\u0000" +
                    "\u00e3\u00e4\u0001\u0000\u0000\u0000\u00e4\u00e7\u0001\u0000\u0000\u0000" +
                    "\u00e5\u00e3\u0001\u0000\u0000\u0000\u00e6\u00e8\u0005\r\u0000\u0000\u00e7" +
                    "\u00e6\u0001\u0000\u0000\u0000\u00e7\u00e8\u0001\u0000\u0000\u0000\u00e8" +
                    "\u00e9\u0001\u0000\u0000\u0000\u00e9\u00ea\u0005\n\u0000\u0000\u00ea\u00eb" +
                    "\u0001\u0000\u0000\u0000\u00eb\u00ec\u0006\u0013\u0002\u0000\u00ec*\u0001" +
                    "\u0000\u0000\u0000\u00ed\u00f1\u0002AZ\u0000\u00ee\u00f0\u0007\u0002\u0000" +
                    "\u0000\u00ef\u00ee\u0001\u0000\u0000\u0000\u00f0\u00f3\u0001\u0000\u0000" +
                    "\u0000\u00f1\u00ef\u0001\u0000\u0000\u0000\u00f1\u00f2\u0001\u0000\u0000" +
                    "\u0000\u00f2,\u0001\u0000\u0000\u0000\u00f3\u00f1\u0001\u0000\u0000\u0000" +
                    "\u00f4\u00f5\u0003+\u0014\u0000\u00f5.\u0001\u0000\u0000\u0000\u00f6\u00f8" +
                    "\u0005 \u0000\u0000\u00f7\u00f6\u0001\u0000\u0000\u0000\u00f7\u00f8\u0001" +
                    "\u0000\u0000\u0000\u00f8\u00f9\u0001\u0000\u0000\u0000\u00f9\u00fb\u0005" +
                    "|\u0000\u0000\u00fa\u00fc\u0005 \u0000\u0000\u00fb\u00fa\u0001\u0000\u0000" +
                    "\u0000\u00fb\u00fc\u0001\u0000\u0000\u0000\u00fc0\u0001\u0000\u0000\u0000" +
                    "\u00fd\u00ff\u0005 \u0000\u0000\u00fe\u00fd\u0001\u0000\u0000\u0000\u00ff" +
                    "\u0102\u0001\u0000\u0000\u0000\u0100\u00fe\u0001\u0000\u0000\u0000\u0100" +
                    "\u0101\u0001\u0000\u0000\u0000\u0101\u0104\u0001\u0000\u0000\u0000\u0102" +
                    "\u0100\u0001\u0000\u0000\u0000\u0103\u0105\u0005\r\u0000\u0000\u0104\u0103" +
                    "\u0001\u0000\u0000\u0000\u0104\u0105\u0001\u0000\u0000\u0000\u0105\u0107" +
                    "\u0001\u0000\u0000\u0000\u0106\u0108\u0005\n\u0000\u0000\u0107\u0106\u0001" +
                    "\u0000\u0000\u0000\u0108\u0109\u0001\u0000\u0000\u0000\u0109\u0107\u0001" +
                    "\u0000\u0000\u0000\u0109\u010a\u0001\u0000\u0000\u0000\u010a\u010b\u0001" +
                    "\u0000\u0000\u0000\u010b\u010c\u0006\u0017\u0002\u0000\u010c\u010d\u0006" +
                    "\u0017\u0003\u0000\u010d2\u0001\u0000\u0000\u0000\u010e\u0110\b\u0003" +
                    "\u0000\u0000\u010f\u010e\u0001\u0000\u0000\u0000\u0110\u0111\u0001\u0000" +
                    "\u0000\u0000\u0111\u010f\u0001\u0000\u0000\u0000\u0111\u0112\u0001\u0000" +
                    "\u0000\u0000\u0112\u0113\u0001\u0000\u0000\u0000\u0113\u0114\u0006\u0018" +
                    "\u0003\u0000\u01144\u0001\u0000\u0000\u0000\u0011\u0000\u0001\u0002\u00c4" +
                    "\u00c9\u00ce\u00d3\u00d9\u00e3\u00e7\u00f1\u00f7\u00fb\u0100\u0104\u0109" +
                    "\u0111\u0004\u0005\u0002\u0000\u0005\u0001\u0000\u0006\u0000\u0000\u0004" +
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