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

// Generated from BlowEffectLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.bloweffect;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class BlowEffectLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, NAME = 2, POWER = 3, EVAL = 4, DESC = 5, LORE_COLOUR_BASE = 6, LORE_COLOUR_RESIST = 7,
            LORE_COLOUR_IMMUNE = 8, EFFECT_TYPE = 9, RESIST = 10, LASH = 11, INTEGER = 12, COMMENT = 13,
            EOL = 14, STRING = 15, EOL_POP = 16;
    public static final int
            FREE_TEXT_MODE = 1;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE", "FREE_TEXT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "RECORD_COUNT", "NAME", "POWER", "EVAL", "DESC", "LORE_COLOUR_BASE",
                "LORE_COLOUR_RESIST", "LORE_COLOUR_IMMUNE", "EFFECT_TYPE", "RESIST",
                "LASH", "INTEGER", "COMMENT", "EOL", "STRING", "EOL_POP"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'name:'", "'power:'", "'eval:'", "'desc:'",
                "'lore-color-base:'", "'lore-color-resist:'", "'lore-color-immune:'",
                "'effect-type:'", "'resist:'", "'lash-type:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "NAME", "POWER", "EVAL", "DESC", "LORE_COLOUR_BASE",
                "LORE_COLOUR_RESIST", "LORE_COLOUR_IMMUNE", "EFFECT_TYPE", "RESIST",
                "LASH", "INTEGER", "COMMENT", "EOL", "STRING", "EOL_POP"
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


    public BlowEffectLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "BlowEffectLexer.g4";
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
            "\u0004\u0000\u0010\u00e0\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000" +
                    "\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003" +
                    "\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006" +
                    "\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002" +
                    "\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002" +
                    "\u000e\u0007\u000e\u0002\u000f\u0007\u000f\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\u000b\u0003\u000b\u00b2\b\u000b\u0001\u000b\u0004\u000b" +
                    "\u00b5\b\u000b\u000b\u000b\f\u000b\u00b6\u0001\f\u0001\f\u0005\f\u00bb" +
                    "\b\f\n\f\f\f\u00be\t\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0005\r" +
                    "\u00c5\b\r\n\r\f\r\u00c8\t\r\u0001\r\u0003\r\u00cb\b\r\u0001\r\u0001\r" +
                    "\u0001\r\u0001\r\u0001\u000e\u0004\u000e\u00d2\b\u000e\u000b\u000e\f\u000e" +
                    "\u00d3\u0001\u000f\u0005\u000f\u00d7\b\u000f\n\u000f\f\u000f\u00da\t\u000f" +
                    "\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0000\u0000" +
                    "\u0010\u0002\u0001\u0004\u0002\u0006\u0003\b\u0004\n\u0005\f\u0006\u000e" +
                    "\u0007\u0010\b\u0012\t\u0014\n\u0016\u000b\u0018\f\u001a\r\u001c\u000e" +
                    "\u001e\u000f \u0010\u0002\u0000\u0001\u0002\u0001\u0000\n\n\u0002\u0000" +
                    "\n\n\r\r\u00e5\u0000\u0002\u0001\u0000\u0000\u0000\u0000\u0004\u0001\u0000" +
                    "\u0000\u0000\u0000\u0006\u0001\u0000\u0000\u0000\u0000\b\u0001\u0000\u0000" +
                    "\u0000\u0000\n\u0001\u0000\u0000\u0000\u0000\f\u0001\u0000\u0000\u0000" +
                    "\u0000\u000e\u0001\u0000\u0000\u0000\u0000\u0010\u0001\u0000\u0000\u0000" +
                    "\u0000\u0012\u0001\u0000\u0000\u0000\u0000\u0014\u0001\u0000\u0000\u0000" +
                    "\u0000\u0016\u0001\u0000\u0000\u0000\u0000\u0018\u0001\u0000\u0000\u0000" +
                    "\u0000\u001a\u0001\u0000\u0000\u0000\u0000\u001c\u0001\u0000\u0000\u0000" +
                    "\u0001\u001e\u0001\u0000\u0000\u0000\u0001 \u0001\u0000\u0000\u0000\u0002" +
                    "\"\u0001\u0000\u0000\u0000\u00040\u0001\u0000\u0000\u0000\u00068\u0001" +
                    "\u0000\u0000\u0000\b?\u0001\u0000\u0000\u0000\nE\u0001\u0000\u0000\u0000" +
                    "\fM\u0001\u0000\u0000\u0000\u000e`\u0001\u0000\u0000\u0000\u0010u\u0001" +
                    "\u0000\u0000\u0000\u0012\u008a\u0001\u0000\u0000\u0000\u0014\u0099\u0001" +
                    "\u0000\u0000\u0000\u0016\u00a3\u0001\u0000\u0000\u0000\u0018\u00b1\u0001" +
                    "\u0000\u0000\u0000\u001a\u00b8\u0001\u0000\u0000\u0000\u001c\u00c6\u0001" +
                    "\u0000\u0000\u0000\u001e\u00d1\u0001\u0000\u0000\u0000 \u00d8\u0001\u0000" +
                    "\u0000\u0000\"#\u0005r\u0000\u0000#$\u0005e\u0000\u0000$%\u0005c\u0000" +
                    "\u0000%&\u0005o\u0000\u0000&\'\u0005r\u0000\u0000\'(\u0005d\u0000\u0000" +
                    "()\u0005-\u0000\u0000)*\u0005c\u0000\u0000*+\u0005o\u0000\u0000+,\u0005" +
                    "u\u0000\u0000,-\u0005n\u0000\u0000-.\u0005t\u0000\u0000./\u0005:\u0000" +
                    "\u0000/\u0003\u0001\u0000\u0000\u000001\u0005n\u0000\u000012\u0005a\u0000" +
                    "\u000023\u0005m\u0000\u000034\u0005e\u0000\u000045\u0005:\u0000\u0000" +
                    "56\u0001\u0000\u0000\u000067\u0006\u0001\u0000\u00007\u0005\u0001\u0000" +
                    "\u0000\u000089\u0005p\u0000\u00009:\u0005o\u0000\u0000:;\u0005w\u0000" +
                    "\u0000;<\u0005e\u0000\u0000<=\u0005r\u0000\u0000=>\u0005:\u0000\u0000" +
                    ">\u0007\u0001\u0000\u0000\u0000?@\u0005e\u0000\u0000@A\u0005v\u0000\u0000" +
                    "AB\u0005a\u0000\u0000BC\u0005l\u0000\u0000CD\u0005:\u0000\u0000D\t\u0001" +
                    "\u0000\u0000\u0000EF\u0005d\u0000\u0000FG\u0005e\u0000\u0000GH\u0005s" +
                    "\u0000\u0000HI\u0005c\u0000\u0000IJ\u0005:\u0000\u0000JK\u0001\u0000\u0000" +
                    "\u0000KL\u0006\u0004\u0000\u0000L\u000b\u0001\u0000\u0000\u0000MN\u0005" +
                    "l\u0000\u0000NO\u0005o\u0000\u0000OP\u0005r\u0000\u0000PQ\u0005e\u0000" +
                    "\u0000QR\u0005-\u0000\u0000RS\u0005c\u0000\u0000ST\u0005o\u0000\u0000" +
                    "TU\u0005l\u0000\u0000UV\u0005o\u0000\u0000VW\u0005r\u0000\u0000WX\u0005" +
                    "-\u0000\u0000XY\u0005b\u0000\u0000YZ\u0005a\u0000\u0000Z[\u0005s\u0000" +
                    "\u0000[\\\u0005e\u0000\u0000\\]\u0005:\u0000\u0000]^\u0001\u0000\u0000" +
                    "\u0000^_\u0006\u0005\u0000\u0000_\r\u0001\u0000\u0000\u0000`a\u0005l\u0000" +
                    "\u0000ab\u0005o\u0000\u0000bc\u0005r\u0000\u0000cd\u0005e\u0000\u0000" +
                    "de\u0005-\u0000\u0000ef\u0005c\u0000\u0000fg\u0005o\u0000\u0000gh\u0005" +
                    "l\u0000\u0000hi\u0005o\u0000\u0000ij\u0005r\u0000\u0000jk\u0005-\u0000" +
                    "\u0000kl\u0005r\u0000\u0000lm\u0005e\u0000\u0000mn\u0005s\u0000\u0000" +
                    "no\u0005i\u0000\u0000op\u0005s\u0000\u0000pq\u0005t\u0000\u0000qr\u0005" +
                    ":\u0000\u0000rs\u0001\u0000\u0000\u0000st\u0006\u0006\u0000\u0000t\u000f" +
                    "\u0001\u0000\u0000\u0000uv\u0005l\u0000\u0000vw\u0005o\u0000\u0000wx\u0005" +
                    "r\u0000\u0000xy\u0005e\u0000\u0000yz\u0005-\u0000\u0000z{\u0005c\u0000" +
                    "\u0000{|\u0005o\u0000\u0000|}\u0005l\u0000\u0000}~\u0005o\u0000\u0000" +
                    "~\u007f\u0005r\u0000\u0000\u007f\u0080\u0005-\u0000\u0000\u0080\u0081" +
                    "\u0005i\u0000\u0000\u0081\u0082\u0005m\u0000\u0000\u0082\u0083\u0005m" +
                    "\u0000\u0000\u0083\u0084\u0005u\u0000\u0000\u0084\u0085\u0005n\u0000\u0000" +
                    "\u0085\u0086\u0005e\u0000\u0000\u0086\u0087\u0005:\u0000\u0000\u0087\u0088" +
                    "\u0001\u0000\u0000\u0000\u0088\u0089\u0006\u0007\u0000\u0000\u0089\u0011" +
                    "\u0001\u0000\u0000\u0000\u008a\u008b\u0005e\u0000\u0000\u008b\u008c\u0005" +
                    "f\u0000\u0000\u008c\u008d\u0005f\u0000\u0000\u008d\u008e\u0005e\u0000" +
                    "\u0000\u008e\u008f\u0005c\u0000\u0000\u008f\u0090\u0005t\u0000\u0000\u0090" +
                    "\u0091\u0005-\u0000\u0000\u0091\u0092\u0005t\u0000\u0000\u0092\u0093\u0005" +
                    "y\u0000\u0000\u0093\u0094\u0005p\u0000\u0000\u0094\u0095\u0005e\u0000" +
                    "\u0000\u0095\u0096\u0005:\u0000\u0000\u0096\u0097\u0001\u0000\u0000\u0000" +
                    "\u0097\u0098\u0006\b\u0000\u0000\u0098\u0013\u0001\u0000\u0000\u0000\u0099" +
                    "\u009a\u0005r\u0000\u0000\u009a\u009b\u0005e\u0000\u0000\u009b\u009c\u0005" +
                    "s\u0000\u0000\u009c\u009d\u0005i\u0000\u0000\u009d\u009e\u0005s\u0000" +
                    "\u0000\u009e\u009f\u0005t\u0000\u0000\u009f\u00a0\u0005:\u0000\u0000\u00a0" +
                    "\u00a1\u0001\u0000\u0000\u0000\u00a1\u00a2\u0006\t\u0000\u0000\u00a2\u0015" +
                    "\u0001\u0000\u0000\u0000\u00a3\u00a4\u0005l\u0000\u0000\u00a4\u00a5\u0005" +
                    "a\u0000\u0000\u00a5\u00a6\u0005s\u0000\u0000\u00a6\u00a7\u0005h\u0000" +
                    "\u0000\u00a7\u00a8\u0005-\u0000\u0000\u00a8\u00a9\u0005t\u0000\u0000\u00a9" +
                    "\u00aa\u0005y\u0000\u0000\u00aa\u00ab\u0005p\u0000\u0000\u00ab\u00ac\u0005" +
                    "e\u0000\u0000\u00ac\u00ad\u0005:\u0000\u0000\u00ad\u00ae\u0001\u0000\u0000" +
                    "\u0000\u00ae\u00af\u0006\n\u0000\u0000\u00af\u0017\u0001\u0000\u0000\u0000" +
                    "\u00b0\u00b2\u0005-\u0000\u0000\u00b1\u00b0\u0001\u0000\u0000\u0000\u00b1" +
                    "\u00b2\u0001\u0000\u0000\u0000\u00b2\u00b4\u0001\u0000\u0000\u0000\u00b3" +
                    "\u00b5\u000209\u0000\u00b4\u00b3\u0001\u0000\u0000\u0000\u00b5\u00b6\u0001" +
                    "\u0000\u0000\u0000\u00b6\u00b4\u0001\u0000\u0000\u0000\u00b6\u00b7\u0001" +
                    "\u0000\u0000\u0000\u00b7\u0019\u0001\u0000\u0000\u0000\u00b8\u00bc\u0005" +
                    "#\u0000\u0000\u00b9\u00bb\b\u0000\u0000\u0000\u00ba\u00b9\u0001\u0000" +
                    "\u0000\u0000\u00bb\u00be\u0001\u0000\u0000\u0000\u00bc\u00ba\u0001\u0000" +
                    "\u0000\u0000\u00bc\u00bd\u0001\u0000\u0000\u0000\u00bd\u00bf\u0001\u0000" +
                    "\u0000\u0000\u00be\u00bc\u0001\u0000\u0000\u0000\u00bf\u00c0\u0005\n\u0000" +
                    "\u0000\u00c0\u00c1\u0001\u0000\u0000\u0000\u00c1\u00c2\u0006\f\u0001\u0000" +
                    "\u00c2\u001b\u0001\u0000\u0000\u0000\u00c3\u00c5\u0005 \u0000\u0000\u00c4" +
                    "\u00c3\u0001\u0000\u0000\u0000\u00c5\u00c8\u0001\u0000\u0000\u0000\u00c6" +
                    "\u00c4\u0001\u0000\u0000\u0000\u00c6\u00c7\u0001\u0000\u0000\u0000\u00c7" +
                    "\u00ca\u0001\u0000\u0000\u0000\u00c8\u00c6\u0001\u0000\u0000\u0000\u00c9" +
                    "\u00cb\u0005\r\u0000\u0000\u00ca\u00c9\u0001\u0000\u0000\u0000\u00ca\u00cb" +
                    "\u0001\u0000\u0000\u0000\u00cb\u00cc\u0001\u0000\u0000\u0000\u00cc\u00cd" +
                    "\u0005\n\u0000\u0000\u00cd\u00ce\u0001\u0000\u0000\u0000\u00ce\u00cf\u0006" +
                    "\r\u0001\u0000\u00cf\u001d\u0001\u0000\u0000\u0000\u00d0\u00d2\b\u0001" +
                    "\u0000\u0000\u00d1\u00d0\u0001\u0000\u0000\u0000\u00d2\u00d3\u0001\u0000" +
                    "\u0000\u0000\u00d3\u00d1\u0001\u0000\u0000\u0000\u00d3\u00d4\u0001\u0000" +
                    "\u0000\u0000\u00d4\u001f\u0001\u0000\u0000\u0000\u00d5\u00d7\u0005\r\u0000" +
                    "\u0000\u00d6\u00d5\u0001\u0000\u0000\u0000\u00d7\u00da\u0001\u0000\u0000" +
                    "\u0000\u00d8\u00d6\u0001\u0000\u0000\u0000\u00d8\u00d9\u0001\u0000\u0000" +
                    "\u0000\u00d9\u00db\u0001\u0000\u0000\u0000\u00da\u00d8\u0001\u0000\u0000" +
                    "\u0000\u00db\u00dc\u0005\n\u0000\u0000\u00dc\u00dd\u0001\u0000\u0000\u0000" +
                    "\u00dd\u00de\u0006\u000f\u0001\u0000\u00de\u00df\u0006\u000f\u0002\u0000" +
                    "\u00df!\u0001\u0000\u0000\u0000\t\u0000\u0001\u00b1\u00b6\u00bc\u00c6" +
                    "\u00ca\u00d3\u00d8\u0003\u0005\u0001\u0000\u0006\u0000\u0000\u0004\u0000" +
                    "\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}