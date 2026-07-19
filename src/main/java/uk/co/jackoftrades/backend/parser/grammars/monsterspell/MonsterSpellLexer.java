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

// Generated from MonsterSpellLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.monsterspell;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class MonsterSpellLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, NAME = 2, MSGT = 3, HIT = 4, POWER_CUTOFF = 5, LORE = 6, LORE_COLOUR_BASE = 7,
            LORE_COLOUR_RESIST = 8, LORE_COLOUR_IMMUNE = 9, MESSAGE_SAVE = 10, MESSAGE_VIS = 11,
            MESSAGE_INVIS = 12, MESSAGE_MISS = 13, COMMENT = 14, EOL = 15, EFFECT = 16, EFFECT_MESSAGE = 17,
            DICE = 18, TIME = 19, EFFECT_YX = 20, EXPR = 21, COLON = 22, UCASE = 23, INTEGER = 24,
            SIMPLE_DICE_STRING = 25, COMPLEX_DICE_STRING = 26, STRING = 27, FREE_TEXT_EOL = 28,
            FREE_TEXT = 29, DICE_SIMPLE_VALUE = 30, DICE_COMPLEX_VALUE = 31, EXPR_CHAR = 32,
            EXPR_COLON = 33, EXPR_UCASE = 34, EXPR_OP = 35, EXPR_EOL = 36;
    public static final int
            MS_FREE_TEXT_MODE = 1, FREE_TEXT_MODE = 2, DICE_STRING_MODE = 3, EXPR_MODE = 4;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE", "MS_FREE_TEXT_MODE", "FREE_TEXT_MODE", "DICE_STRING_MODE",
            "EXPR_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "RECORD_COUNT", "NAME", "MSGT", "HIT", "POWER_CUTOFF", "LORE", "LORE_COLOUR_BASE",
                "LORE_COLOUR_RESIST", "LORE_COLOUR_IMMUNE", "MESSAGE_SAVE", "MESSAGE_VIS",
                "MESSAGE_INVIS", "MESSAGE_MISS", "COMMENT", "EOL", "EFFECT", "EFFECT_MESSAGE",
                "DICE", "TIME", "EFFECT_YX", "EXPR", "COLON", "UCASE", "INTEGER", "DICE_D",
                "DICE_M", "DICE_INTEGER", "DICE_DOLLAR_LETTER", "DICE_SIMPLE_NUMBER",
                "DICE_ANY_NUMBER", "COMPLEX_DICE_STRING_BODY", "SIMPLE_DICE_STRING_BODY",
                "SIMPLE_DICE_STRING", "COMPLEX_DICE_STRING", "STRING", "FREE_TEXT_EOL",
                "FREE_TEXT", "DICE_SIMPLE_VALUE", "DICE_COMPLEX_VALUE", "EXPR_CHAR",
                "EXPR_COLON", "EXPR_UCASE", "EXPR_OP", "EXPR_EOL"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'name:'", "'msgt:'", "'hit:'", "'power-cutoff:'",
                "'lore:'", "'lore-color-base:'", "'lore-color-resist:'", "'lore-color-immune:'",
                "'message-save:'", "'message-vis:'", "'message-invis:'", "'message-miss:'",
                null, null, "'effect:'", "'effect-msg:'", "'dice:'", "'time:'", "'effect-yx:'",
                "'expr:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "NAME", "MSGT", "HIT", "POWER_CUTOFF", "LORE",
                "LORE_COLOUR_BASE", "LORE_COLOUR_RESIST", "LORE_COLOUR_IMMUNE", "MESSAGE_SAVE",
                "MESSAGE_VIS", "MESSAGE_INVIS", "MESSAGE_MISS", "COMMENT", "EOL", "EFFECT",
                "EFFECT_MESSAGE", "DICE", "TIME", "EFFECT_YX", "EXPR", "COLON", "UCASE",
                "INTEGER", "SIMPLE_DICE_STRING", "COMPLEX_DICE_STRING", "STRING", "FREE_TEXT_EOL",
                "FREE_TEXT", "DICE_SIMPLE_VALUE", "DICE_COMPLEX_VALUE", "EXPR_CHAR",
                "EXPR_COLON", "EXPR_UCASE", "EXPR_OP", "EXPR_EOL"
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


    public MonsterSpellLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "MonsterSpellLexer.g4";
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
            "\u0004\u0000$\u0219\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff\uffff" +
                    "\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001" +
                    "\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004" +
                    "\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007" +
                    "\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b" +
                    "\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002" +
                    "\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002" +
                    "\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002" +
                    "\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002" +
                    "\u0018\u0007\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002" +
                    "\u001b\u0007\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002" +
                    "\u001e\u0007\u001e\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007" +
                    "!\u0002\"\u0007\"\u0002#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007" +
                    "&\u0002\'\u0007\'\u0002(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007" +
                    "+\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001" +
                    "\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f" +
                    "\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001" +
                    "\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0005\r\u0116" +
                    "\b\r\n\r\f\r\u0119\t\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0005" +
                    "\u000e\u0120\b\u000e\n\u000e\f\u000e\u0123\t\u000e\u0001\u000e\u0003\u000e" +
                    "\u0126\b\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f" +
                    "\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f" +
                    "\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010" +
                    "\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010" +
                    "\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011" +
                    "\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012" +
                    "\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012" +
                    "\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014" +
                    "\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0016\u0004\u0016" +
                    "\u0168\b\u0016\u000b\u0016\f\u0016\u0169\u0001\u0017\u0003\u0017\u016d" +
                    "\b\u0017\u0001\u0017\u0004\u0017\u0170\b\u0017\u000b\u0017\f\u0017\u0171" +
                    "\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0001\u001a\u0004\u001a" +
                    "\u0179\b\u001a\u000b\u001a\f\u001a\u017a\u0001\u001b\u0001\u001b\u0001" +
                    "\u001b\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0003\u001d\u0184" +
                    "\b\u001d\u0001\u001e\u0003\u001e\u0187\b\u001e\u0001\u001e\u0001\u001e" +
                    "\u0001\u001e\u0003\u001e\u018c\b\u001e\u0001\u001e\u0001\u001e\u0001\u001e" +
                    "\u0001\u001e\u0001\u001e\u0003\u001e\u0193\b\u001e\u0001\u001e\u0001\u001e" +
                    "\u0001\u001e\u0003\u001e\u0198\b\u001e\u0001\u001e\u0003\u001e\u019b\b" +
                    "\u001e\u0001\u001e\u0003\u001e\u019e\b\u001e\u0001\u001e\u0001\u001e\u0001" +
                    "\u001e\u0001\u001e\u0001\u001e\u0003\u001e\u01a5\b\u001e\u0001\u001e\u0001" +
                    "\u001e\u0001\u001e\u0001\u001e\u0003\u001e\u01ab\b\u001e\u0001\u001e\u0003" +
                    "\u001e\u01ae\b\u001e\u0001\u001f\u0003\u001f\u01b1\b\u001f\u0001\u001f" +
                    "\u0001\u001f\u0001\u001f\u0003\u001f\u01b6\b\u001f\u0001\u001f\u0001\u001f" +
                    "\u0001\u001f\u0001\u001f\u0001\u001f\u0003\u001f\u01bd\b\u001f\u0001\u001f" +
                    "\u0001\u001f\u0001\u001f\u0003\u001f\u01c2\b\u001f\u0001\u001f\u0003\u001f" +
                    "\u01c5\b\u001f\u0001\u001f\u0003\u001f\u01c8\b\u001f\u0001\u001f\u0001" +
                    "\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0003\u001f\u01cf\b\u001f\u0001" +
                    "\u001f\u0003\u001f\u01d2\b\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001" +
                    "\u001f\u0003\u001f\u01d8\b\u001f\u0001\u001f\u0003\u001f\u01db\b\u001f" +
                    "\u0001 \u0001 \u0001!\u0001!\u0001\"\u0004\"\u01e2\b\"\u000b\"\f\"\u01e3" +
                    "\u0001#\u0005#\u01e7\b#\n#\f#\u01ea\t#\u0001#\u0001#\u0001#\u0001#\u0001" +
                    "#\u0001$\u0004$\u01f2\b$\u000b$\f$\u01f3\u0001$\u0001$\u0001%\u0001%\u0001" +
                    "%\u0001%\u0001&\u0001&\u0001&\u0001&\u0001\'\u0001\'\u0001(\u0001(\u0001" +
                    ")\u0004)\u0205\b)\u000b)\f)\u0206\u0001*\u0001*\u0004*\u020b\b*\u000b" +
                    "*\f*\u020c\u0001+\u0005+\u0210\b+\n+\f+\u0213\t+\u0001+\u0001+\u0001+" +
                    "\u0001+\u0001+\u0000\u0000,\u0005\u0001\u0007\u0002\t\u0003\u000b\u0004" +
                    "\r\u0005\u000f\u0006\u0011\u0007\u0013\b\u0015\t\u0017\n\u0019\u000b\u001b" +
                    "\f\u001d\r\u001f\u000e!\u000f#\u0010%\u0011\'\u0012)\u0013+\u0014-\u0015" +
                    "/\u00161\u00173\u00185\u00007\u00009\u0000;\u0000=\u0000?\u0000A\u0000" +
                    "C\u0000E\u0019G\u001aI\u001bK\u001cM\u001dO\u001eQ\u001fS U!W\"Y#[$\u0005" +
                    "\u0000\u0001\u0002\u0003\u0004\u0006\u0001\u0000\n\n\u0004\u0000--AZ_" +
                    "_az\u0002\u0000MMmm\u0002\u0000\n\n\r\r\u0002\u0000AZ__\u0003\u0000*+" +
                    "--//\u0231\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000" +
                    "\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000" +
                    "\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000" +
                    "\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000" +
                    "\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000" +
                    "\u0000\u0019\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000" +
                    "\u0000\u001d\u0001\u0000\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000" +
                    "\u0000!\u0001\u0000\u0000\u0000\u0000#\u0001\u0000\u0000\u0000\u0000%" +
                    "\u0001\u0000\u0000\u0000\u0000\'\u0001\u0000\u0000\u0000\u0000)\u0001" +
                    "\u0000\u0000\u0000\u0000+\u0001\u0000\u0000\u0000\u0000-\u0001\u0000\u0000" +
                    "\u0000\u0000/\u0001\u0000\u0000\u0000\u00001\u0001\u0000\u0000\u0000\u0000" +
                    "3\u0001\u0000\u0000\u0000\u0000E\u0001\u0000\u0000\u0000\u0000G\u0001" +
                    "\u0000\u0000\u0000\u0001I\u0001\u0000\u0000\u0000\u0001K\u0001\u0000\u0000" +
                    "\u0000\u0002M\u0001\u0000\u0000\u0000\u0003O\u0001\u0000\u0000\u0000\u0003" +
                    "Q\u0001\u0000\u0000\u0000\u0004S\u0001\u0000\u0000\u0000\u0004U\u0001" +
                    "\u0000\u0000\u0000\u0004W\u0001\u0000\u0000\u0000\u0004Y\u0001\u0000\u0000" +
                    "\u0000\u0004[\u0001\u0000\u0000\u0000\u0005]\u0001\u0000\u0000\u0000\u0007" +
                    "k\u0001\u0000\u0000\u0000\ts\u0001\u0000\u0000\u0000\u000b{\u0001\u0000" +
                    "\u0000\u0000\r\u0080\u0001\u0000\u0000\u0000\u000f\u008e\u0001\u0000\u0000" +
                    "\u0000\u0011\u0096\u0001\u0000\u0000\u0000\u0013\u00a9\u0001\u0000\u0000" +
                    "\u0000\u0015\u00be\u0001\u0000\u0000\u0000\u0017\u00d3\u0001\u0000\u0000" +
                    "\u0000\u0019\u00e3\u0001\u0000\u0000\u0000\u001b\u00f2\u0001\u0000\u0000" +
                    "\u0000\u001d\u0103\u0001\u0000\u0000\u0000\u001f\u0113\u0001\u0000\u0000" +
                    "\u0000!\u0121\u0001\u0000\u0000\u0000#\u012b\u0001\u0000\u0000\u0000%" +
                    "\u0133\u0001\u0000\u0000\u0000\'\u0141\u0001\u0000\u0000\u0000)\u0149" +
                    "\u0001\u0000\u0000\u0000+\u0151\u0001\u0000\u0000\u0000-\u015c\u0001\u0000" +
                    "\u0000\u0000/\u0164\u0001\u0000\u0000\u00001\u0167\u0001\u0000\u0000\u0000" +
                    "3\u016c\u0001\u0000\u0000\u00005\u0173\u0001\u0000\u0000\u00007\u0175" +
                    "\u0001\u0000\u0000\u00009\u0178\u0001\u0000\u0000\u0000;\u017c\u0001\u0000" +
                    "\u0000\u0000=\u017f\u0001\u0000\u0000\u0000?\u0183\u0001\u0000\u0000\u0000" +
                    "A\u01ad\u0001\u0000\u0000\u0000C\u01da\u0001\u0000\u0000\u0000E\u01dc" +
                    "\u0001\u0000\u0000\u0000G\u01de\u0001\u0000\u0000\u0000I\u01e1\u0001\u0000" +
                    "\u0000\u0000K\u01e8\u0001\u0000\u0000\u0000M\u01f1\u0001\u0000\u0000\u0000" +
                    "O\u01f7\u0001\u0000\u0000\u0000Q\u01fb\u0001\u0000\u0000\u0000S\u01ff" +
                    "\u0001\u0000\u0000\u0000U\u0201\u0001\u0000\u0000\u0000W\u0204\u0001\u0000" +
                    "\u0000\u0000Y\u0208\u0001\u0000\u0000\u0000[\u0211\u0001\u0000\u0000\u0000" +
                    "]^\u0005r\u0000\u0000^_\u0005e\u0000\u0000_`\u0005c\u0000\u0000`a\u0005" +
                    "o\u0000\u0000ab\u0005r\u0000\u0000bc\u0005d\u0000\u0000cd\u0005-\u0000" +
                    "\u0000de\u0005c\u0000\u0000ef\u0005o\u0000\u0000fg\u0005u\u0000\u0000" +
                    "gh\u0005n\u0000\u0000hi\u0005t\u0000\u0000ij\u0005:\u0000\u0000j\u0006" +
                    "\u0001\u0000\u0000\u0000kl\u0005n\u0000\u0000lm\u0005a\u0000\u0000mn\u0005" +
                    "m\u0000\u0000no\u0005e\u0000\u0000op\u0005:\u0000\u0000pq\u0001\u0000" +
                    "\u0000\u0000qr\u0006\u0001\u0000\u0000r\b\u0001\u0000\u0000\u0000st\u0005" +
                    "m\u0000\u0000tu\u0005s\u0000\u0000uv\u0005g\u0000\u0000vw\u0005t\u0000" +
                    "\u0000wx\u0005:\u0000\u0000xy\u0001\u0000\u0000\u0000yz\u0006\u0002\u0000" +
                    "\u0000z\n\u0001\u0000\u0000\u0000{|\u0005h\u0000\u0000|}\u0005i\u0000" +
                    "\u0000}~\u0005t\u0000\u0000~\u007f\u0005:\u0000\u0000\u007f\f\u0001\u0000" +
                    "\u0000\u0000\u0080\u0081\u0005p\u0000\u0000\u0081\u0082\u0005o\u0000\u0000" +
                    "\u0082\u0083\u0005w\u0000\u0000\u0083\u0084\u0005e\u0000\u0000\u0084\u0085" +
                    "\u0005r\u0000\u0000\u0085\u0086\u0005-\u0000\u0000\u0086\u0087\u0005c" +
                    "\u0000\u0000\u0087\u0088\u0005u\u0000\u0000\u0088\u0089\u0005t\u0000\u0000" +
                    "\u0089\u008a\u0005o\u0000\u0000\u008a\u008b\u0005f\u0000\u0000\u008b\u008c" +
                    "\u0005f\u0000\u0000\u008c\u008d\u0005:\u0000\u0000\u008d\u000e\u0001\u0000" +
                    "\u0000\u0000\u008e\u008f\u0005l\u0000\u0000\u008f\u0090\u0005o\u0000\u0000" +
                    "\u0090\u0091\u0005r\u0000\u0000\u0091\u0092\u0005e\u0000\u0000\u0092\u0093" +
                    "\u0005:\u0000\u0000\u0093\u0094\u0001\u0000\u0000\u0000\u0094\u0095\u0006" +
                    "\u0005\u0000\u0000\u0095\u0010\u0001\u0000\u0000\u0000\u0096\u0097\u0005" +
                    "l\u0000\u0000\u0097\u0098\u0005o\u0000\u0000\u0098\u0099\u0005r\u0000" +
                    "\u0000\u0099\u009a\u0005e\u0000\u0000\u009a\u009b\u0005-\u0000\u0000\u009b" +
                    "\u009c\u0005c\u0000\u0000\u009c\u009d\u0005o\u0000\u0000\u009d\u009e\u0005" +
                    "l\u0000\u0000\u009e\u009f\u0005o\u0000\u0000\u009f\u00a0\u0005r\u0000" +
                    "\u0000\u00a0\u00a1\u0005-\u0000\u0000\u00a1\u00a2\u0005b\u0000\u0000\u00a2" +
                    "\u00a3\u0005a\u0000\u0000\u00a3\u00a4\u0005s\u0000\u0000\u00a4\u00a5\u0005" +
                    "e\u0000\u0000\u00a5\u00a6\u0005:\u0000\u0000\u00a6\u00a7\u0001\u0000\u0000" +
                    "\u0000\u00a7\u00a8\u0006\u0006\u0000\u0000\u00a8\u0012\u0001\u0000\u0000" +
                    "\u0000\u00a9\u00aa\u0005l\u0000\u0000\u00aa\u00ab\u0005o\u0000\u0000\u00ab" +
                    "\u00ac\u0005r\u0000\u0000\u00ac\u00ad\u0005e\u0000\u0000\u00ad\u00ae\u0005" +
                    "-\u0000\u0000\u00ae\u00af\u0005c\u0000\u0000\u00af\u00b0\u0005o\u0000" +
                    "\u0000\u00b0\u00b1\u0005l\u0000\u0000\u00b1\u00b2\u0005o\u0000\u0000\u00b2" +
                    "\u00b3\u0005r\u0000\u0000\u00b3\u00b4\u0005-\u0000\u0000\u00b4\u00b5\u0005" +
                    "r\u0000\u0000\u00b5\u00b6\u0005e\u0000\u0000\u00b6\u00b7\u0005s\u0000" +
                    "\u0000\u00b7\u00b8\u0005i\u0000\u0000\u00b8\u00b9\u0005s\u0000\u0000\u00b9" +
                    "\u00ba\u0005t\u0000\u0000\u00ba\u00bb\u0005:\u0000\u0000\u00bb\u00bc\u0001" +
                    "\u0000\u0000\u0000\u00bc\u00bd\u0006\u0007\u0000\u0000\u00bd\u0014\u0001" +
                    "\u0000\u0000\u0000\u00be\u00bf\u0005l\u0000\u0000\u00bf\u00c0\u0005o\u0000" +
                    "\u0000\u00c0\u00c1\u0005r\u0000\u0000\u00c1\u00c2\u0005e\u0000\u0000\u00c2" +
                    "\u00c3\u0005-\u0000\u0000\u00c3\u00c4\u0005c\u0000\u0000\u00c4\u00c5\u0005" +
                    "o\u0000\u0000\u00c5\u00c6\u0005l\u0000\u0000\u00c6\u00c7\u0005o\u0000" +
                    "\u0000\u00c7\u00c8\u0005r\u0000\u0000\u00c8\u00c9\u0005-\u0000\u0000\u00c9" +
                    "\u00ca\u0005i\u0000\u0000\u00ca\u00cb\u0005m\u0000\u0000\u00cb\u00cc\u0005" +
                    "m\u0000\u0000\u00cc\u00cd\u0005u\u0000\u0000\u00cd\u00ce\u0005n\u0000" +
                    "\u0000\u00ce\u00cf\u0005e\u0000\u0000\u00cf\u00d0\u0005:\u0000\u0000\u00d0" +
                    "\u00d1\u0001\u0000\u0000\u0000\u00d1\u00d2\u0006\b\u0000\u0000\u00d2\u0016" +
                    "\u0001\u0000\u0000\u0000\u00d3\u00d4\u0005m\u0000\u0000\u00d4\u00d5\u0005" +
                    "e\u0000\u0000\u00d5\u00d6\u0005s\u0000\u0000\u00d6\u00d7\u0005s\u0000" +
                    "\u0000\u00d7\u00d8\u0005a\u0000\u0000\u00d8\u00d9\u0005g\u0000\u0000\u00d9" +
                    "\u00da\u0005e\u0000\u0000\u00da\u00db\u0005-\u0000\u0000\u00db\u00dc\u0005" +
                    "s\u0000\u0000\u00dc\u00dd\u0005a\u0000\u0000\u00dd\u00de\u0005v\u0000" +
                    "\u0000\u00de\u00df\u0005e\u0000\u0000\u00df\u00e0\u0005:\u0000\u0000\u00e0" +
                    "\u00e1\u0001\u0000\u0000\u0000\u00e1\u00e2\u0006\t\u0000\u0000\u00e2\u0018" +
                    "\u0001\u0000\u0000\u0000\u00e3\u00e4\u0005m\u0000\u0000\u00e4\u00e5\u0005" +
                    "e\u0000\u0000\u00e5\u00e6\u0005s\u0000\u0000\u00e6\u00e7\u0005s\u0000" +
                    "\u0000\u00e7\u00e8\u0005a\u0000\u0000\u00e8\u00e9\u0005g\u0000\u0000\u00e9" +
                    "\u00ea\u0005e\u0000\u0000\u00ea\u00eb\u0005-\u0000\u0000\u00eb\u00ec\u0005" +
                    "v\u0000\u0000\u00ec\u00ed\u0005i\u0000\u0000\u00ed\u00ee\u0005s\u0000" +
                    "\u0000\u00ee\u00ef\u0005:\u0000\u0000\u00ef\u00f0\u0001\u0000\u0000\u0000" +
                    "\u00f0\u00f1\u0006\n\u0000\u0000\u00f1\u001a\u0001\u0000\u0000\u0000\u00f2" +
                    "\u00f3\u0005m\u0000\u0000\u00f3\u00f4\u0005e\u0000\u0000\u00f4\u00f5\u0005" +
                    "s\u0000\u0000\u00f5\u00f6\u0005s\u0000\u0000\u00f6\u00f7\u0005a\u0000" +
                    "\u0000\u00f7\u00f8\u0005g\u0000\u0000\u00f8\u00f9\u0005e\u0000\u0000\u00f9" +
                    "\u00fa\u0005-\u0000\u0000\u00fa\u00fb\u0005i\u0000\u0000\u00fb\u00fc\u0005" +
                    "n\u0000\u0000\u00fc\u00fd\u0005v\u0000\u0000\u00fd\u00fe\u0005i\u0000" +
                    "\u0000\u00fe\u00ff\u0005s\u0000\u0000\u00ff\u0100\u0005:\u0000\u0000\u0100" +
                    "\u0101\u0001\u0000\u0000\u0000\u0101\u0102\u0006\u000b\u0000\u0000\u0102" +
                    "\u001c\u0001\u0000\u0000\u0000\u0103\u0104\u0005m\u0000\u0000\u0104\u0105" +
                    "\u0005e\u0000\u0000\u0105\u0106\u0005s\u0000\u0000\u0106\u0107\u0005s" +
                    "\u0000\u0000\u0107\u0108\u0005a\u0000\u0000\u0108\u0109\u0005g\u0000\u0000" +
                    "\u0109\u010a\u0005e\u0000\u0000\u010a\u010b\u0005-\u0000\u0000\u010b\u010c" +
                    "\u0005m\u0000\u0000\u010c\u010d\u0005i\u0000\u0000\u010d\u010e\u0005s" +
                    "\u0000\u0000\u010e\u010f\u0005s\u0000\u0000\u010f\u0110\u0005:\u0000\u0000" +
                    "\u0110\u0111\u0001\u0000\u0000\u0000\u0111\u0112\u0006\f\u0000\u0000\u0112" +
                    "\u001e\u0001\u0000\u0000\u0000\u0113\u0117\u0005#\u0000\u0000\u0114\u0116" +
                    "\b\u0000\u0000\u0000\u0115\u0114\u0001\u0000\u0000\u0000\u0116\u0119\u0001" +
                    "\u0000\u0000\u0000\u0117\u0115\u0001\u0000\u0000\u0000\u0117\u0118\u0001" +
                    "\u0000\u0000\u0000\u0118\u011a\u0001\u0000\u0000\u0000\u0119\u0117\u0001" +
                    "\u0000\u0000\u0000\u011a\u011b\u0005\n\u0000\u0000\u011b\u011c\u0001\u0000" +
                    "\u0000\u0000\u011c\u011d\u0006\r\u0001\u0000\u011d \u0001\u0000\u0000" +
                    "\u0000\u011e\u0120\u0005 \u0000\u0000\u011f\u011e\u0001\u0000\u0000\u0000" +
                    "\u0120\u0123\u0001\u0000\u0000\u0000\u0121\u011f\u0001\u0000\u0000\u0000" +
                    "\u0121\u0122\u0001\u0000\u0000\u0000\u0122\u0125\u0001\u0000\u0000\u0000" +
                    "\u0123\u0121\u0001\u0000\u0000\u0000\u0124\u0126\u0005\r\u0000\u0000\u0125" +
                    "\u0124\u0001\u0000\u0000\u0000\u0125\u0126\u0001\u0000\u0000\u0000\u0126" +
                    "\u0127\u0001\u0000\u0000\u0000\u0127\u0128\u0005\n\u0000\u0000\u0128\u0129" +
                    "\u0001\u0000\u0000\u0000\u0129\u012a\u0006\u000e\u0001\u0000\u012a\"\u0001" +
                    "\u0000\u0000\u0000\u012b\u012c\u0005e\u0000\u0000\u012c\u012d\u0005f\u0000" +
                    "\u0000\u012d\u012e\u0005f\u0000\u0000\u012e\u012f\u0005e\u0000\u0000\u012f" +
                    "\u0130\u0005c\u0000\u0000\u0130\u0131\u0005t\u0000\u0000\u0131\u0132\u0005" +
                    ":\u0000\u0000\u0132$\u0001\u0000\u0000\u0000\u0133\u0134\u0005e\u0000" +
                    "\u0000\u0134\u0135\u0005f\u0000\u0000\u0135\u0136\u0005f\u0000\u0000\u0136" +
                    "\u0137\u0005e\u0000\u0000\u0137\u0138\u0005c\u0000\u0000\u0138\u0139\u0005" +
                    "t\u0000\u0000\u0139\u013a\u0005-\u0000\u0000\u013a\u013b\u0005m\u0000" +
                    "\u0000\u013b\u013c\u0005s\u0000\u0000\u013c\u013d\u0005g\u0000\u0000\u013d" +
                    "\u013e\u0005:\u0000\u0000\u013e\u013f\u0001\u0000\u0000\u0000\u013f\u0140" +
                    "\u0006\u0010\u0002\u0000\u0140&\u0001\u0000\u0000\u0000\u0141\u0142\u0005" +
                    "d\u0000\u0000\u0142\u0143\u0005i\u0000\u0000\u0143\u0144\u0005c\u0000" +
                    "\u0000\u0144\u0145\u0005e\u0000\u0000\u0145\u0146\u0005:\u0000\u0000\u0146" +
                    "\u0147\u0001\u0000\u0000\u0000\u0147\u0148\u0006\u0011\u0003\u0000\u0148" +
                    "(\u0001\u0000\u0000\u0000\u0149\u014a\u0005t\u0000\u0000\u014a\u014b\u0005" +
                    "i\u0000\u0000\u014b\u014c\u0005m\u0000\u0000\u014c\u014d\u0005e\u0000" +
                    "\u0000\u014d\u014e\u0005:\u0000\u0000\u014e\u014f\u0001\u0000\u0000\u0000" +
                    "\u014f\u0150\u0006\u0012\u0003\u0000\u0150*\u0001\u0000\u0000\u0000\u0151" +
                    "\u0152\u0005e\u0000\u0000\u0152\u0153\u0005f\u0000\u0000\u0153\u0154\u0005" +
                    "f\u0000\u0000\u0154\u0155\u0005e\u0000\u0000\u0155\u0156\u0005c\u0000" +
                    "\u0000\u0156\u0157\u0005t\u0000\u0000\u0157\u0158\u0005-\u0000\u0000\u0158" +
                    "\u0159\u0005y\u0000\u0000\u0159\u015a\u0005x\u0000\u0000\u015a\u015b\u0005" +
                    ":\u0000\u0000\u015b,\u0001\u0000\u0000\u0000\u015c\u015d\u0005e\u0000" +
                    "\u0000\u015d\u015e\u0005x\u0000\u0000\u015e\u015f\u0005p\u0000\u0000\u015f" +
                    "\u0160\u0005r\u0000\u0000\u0160\u0161\u0005:\u0000\u0000\u0161\u0162\u0001" +
                    "\u0000\u0000\u0000\u0162\u0163\u0006\u0014\u0004\u0000\u0163.\u0001\u0000" +
                    "\u0000\u0000\u0164\u0165\u0005:\u0000\u0000\u01650\u0001\u0000\u0000\u0000" +
                    "\u0166\u0168\u0007\u0001\u0000\u0000\u0167\u0166\u0001\u0000\u0000\u0000" +
                    "\u0168\u0169\u0001\u0000\u0000\u0000\u0169\u0167\u0001\u0000\u0000\u0000" +
                    "\u0169\u016a\u0001\u0000\u0000\u0000\u016a2\u0001\u0000\u0000\u0000\u016b" +
                    "\u016d\u0005-\u0000\u0000\u016c\u016b\u0001\u0000\u0000\u0000\u016c\u016d" +
                    "\u0001\u0000\u0000\u0000\u016d\u016f\u0001\u0000\u0000\u0000\u016e\u0170" +
                    "\u000209\u0000\u016f\u016e\u0001\u0000\u0000\u0000\u0170\u0171\u0001\u0000" +
                    "\u0000\u0000\u0171\u016f\u0001\u0000\u0000\u0000\u0171\u0172\u0001\u0000" +
                    "\u0000\u0000\u01724\u0001\u0000\u0000\u0000\u0173\u0174\u0005d\u0000\u0000" +
                    "\u01746\u0001\u0000\u0000\u0000\u0175\u0176\u0007\u0002\u0000\u0000\u0176" +
                    "8\u0001\u0000\u0000\u0000\u0177\u0179\u000209\u0000\u0178\u0177\u0001" +
                    "\u0000\u0000\u0000\u0179\u017a\u0001\u0000\u0000\u0000\u017a\u0178\u0001" +
                    "\u0000\u0000\u0000\u017a\u017b\u0001\u0000\u0000\u0000\u017b:\u0001\u0000" +
                    "\u0000\u0000\u017c\u017d\u0005$\u0000\u0000\u017d\u017e\u0002AZ\u0000" +
                    "\u017e<\u0001\u0000\u0000\u0000\u017f\u0180\u00039\u001a\u0000\u0180>" +
                    "\u0001\u0000\u0000\u0000\u0181\u0184\u00039\u001a\u0000\u0182\u0184\u0003" +
                    ";\u001b\u0000\u0183\u0181\u0001\u0000\u0000\u0000\u0183\u0182\u0001\u0000" +
                    "\u0000\u0000\u0184@\u0001\u0000\u0000\u0000\u0185\u0187\u0005-\u0000\u0000" +
                    "\u0186\u0185\u0001\u0000\u0000\u0000\u0186\u0187\u0001\u0000\u0000\u0000" +
                    "\u0187\u0188\u0001\u0000\u0000\u0000\u0188\u0189\u0003?\u001d\u0000\u0189" +
                    "\u0197\u0005+\u0000\u0000\u018a\u018c\u0003?\u001d\u0000\u018b\u018a\u0001" +
                    "\u0000\u0000\u0000\u018b\u018c\u0001\u0000\u0000\u0000\u018c\u018d\u0001" +
                    "\u0000\u0000\u0000\u018d\u018e\u00035\u0018\u0000\u018e\u0192\u0003?\u001d" +
                    "\u0000\u018f\u0190\u00037\u0019\u0000\u0190\u0191\u0003?\u001d\u0000\u0191" +
                    "\u0193\u0001\u0000\u0000\u0000\u0192\u018f\u0001\u0000\u0000\u0000\u0192" +
                    "\u0193\u0001\u0000\u0000\u0000\u0193\u0198\u0001\u0000\u0000\u0000\u0194" +
                    "\u0195\u00037\u0019\u0000\u0195\u0196\u0003?\u001d\u0000\u0196\u0198\u0001" +
                    "\u0000\u0000\u0000\u0197\u018b\u0001\u0000\u0000\u0000\u0197\u0194\u0001" +
                    "\u0000\u0000\u0000\u0198\u01ae\u0001\u0000\u0000\u0000\u0199\u019b\u0005" +
                    "-\u0000\u0000\u019a\u0199\u0001\u0000\u0000\u0000\u019a\u019b\u0001\u0000" +
                    "\u0000\u0000\u019b\u019d\u0001\u0000\u0000\u0000\u019c\u019e\u0003?\u001d" +
                    "\u0000\u019d\u019c\u0001\u0000\u0000\u0000\u019d\u019e\u0001\u0000\u0000" +
                    "\u0000\u019e\u019f\u0001\u0000\u0000\u0000\u019f\u01a0\u00035\u0018\u0000" +
                    "\u01a0\u01a4\u0003?\u001d\u0000\u01a1\u01a2\u00037\u0019\u0000\u01a2\u01a3" +
                    "\u0003?\u001d\u0000\u01a3\u01a5\u0001\u0000\u0000\u0000\u01a4\u01a1\u0001" +
                    "\u0000\u0000\u0000\u01a4\u01a5\u0001\u0000\u0000\u0000\u01a5\u01ae\u0001" +
                    "\u0000\u0000\u0000\u01a6\u01a7\u00037\u0019\u0000\u01a7\u01a8\u0003?\u001d" +
                    "\u0000\u01a8\u01ae\u0001\u0000\u0000\u0000\u01a9\u01ab\u0005-\u0000\u0000" +
                    "\u01aa\u01a9\u0001\u0000\u0000\u0000\u01aa\u01ab\u0001\u0000\u0000\u0000" +
                    "\u01ab\u01ac\u0001\u0000\u0000\u0000\u01ac\u01ae\u0003?\u001d\u0000\u01ad" +
                    "\u0186\u0001\u0000\u0000\u0000\u01ad\u019a\u0001\u0000\u0000\u0000\u01ad" +
                    "\u01a6\u0001\u0000\u0000\u0000\u01ad\u01aa\u0001\u0000\u0000\u0000\u01ae" +
                    "B\u0001\u0000\u0000\u0000\u01af\u01b1\u0005-\u0000\u0000\u01b0\u01af\u0001" +
                    "\u0000\u0000\u0000\u01b0\u01b1\u0001\u0000\u0000\u0000\u01b1\u01b2\u0001" +
                    "\u0000\u0000\u0000\u01b2\u01b3\u0003=\u001c\u0000\u01b3\u01c1\u0005+\u0000" +
                    "\u0000\u01b4\u01b6\u0003=\u001c\u0000\u01b5\u01b4\u0001\u0000\u0000\u0000" +
                    "\u01b5\u01b6\u0001\u0000\u0000\u0000\u01b6\u01b7\u0001\u0000\u0000\u0000" +
                    "\u01b7\u01b8\u00035\u0018\u0000\u01b8\u01bc\u0003=\u001c\u0000\u01b9\u01ba" +
                    "\u00037\u0019\u0000\u01ba\u01bb\u0003=\u001c\u0000\u01bb\u01bd\u0001\u0000" +
                    "\u0000\u0000\u01bc\u01b9\u0001\u0000\u0000\u0000\u01bc\u01bd\u0001\u0000" +
                    "\u0000\u0000\u01bd\u01c2\u0001\u0000\u0000\u0000\u01be\u01bf\u00037\u0019" +
                    "\u0000\u01bf\u01c0\u0003=\u001c\u0000\u01c0\u01c2\u0001\u0000\u0000\u0000" +
                    "\u01c1\u01b5\u0001\u0000\u0000\u0000\u01c1\u01be\u0001\u0000\u0000\u0000" +
                    "\u01c2\u01db\u0001\u0000\u0000\u0000\u01c3\u01c5\u0005-\u0000\u0000\u01c4" +
                    "\u01c3\u0001\u0000\u0000\u0000\u01c4\u01c5\u0001\u0000\u0000\u0000\u01c5" +
                    "\u01c7\u0001\u0000\u0000\u0000\u01c6\u01c8\u0003=\u001c\u0000\u01c7\u01c6" +
                    "\u0001\u0000\u0000\u0000\u01c7\u01c8\u0001\u0000\u0000\u0000\u01c8\u01c9" +
                    "\u0001\u0000\u0000\u0000\u01c9\u01ca\u00035\u0018\u0000\u01ca\u01ce\u0003" +
                    "=\u001c\u0000\u01cb\u01cc\u00037\u0019\u0000\u01cc\u01cd\u0003=\u001c" +
                    "\u0000\u01cd\u01cf\u0001\u0000\u0000\u0000\u01ce\u01cb\u0001\u0000\u0000" +
                    "\u0000\u01ce\u01cf\u0001\u0000\u0000\u0000\u01cf\u01db\u0001\u0000\u0000" +
                    "\u0000\u01d0\u01d2\u0005-\u0000\u0000\u01d1\u01d0\u0001\u0000\u0000\u0000" +
                    "\u01d1\u01d2\u0001\u0000\u0000\u0000\u01d2\u01d3\u0001\u0000\u0000\u0000" +
                    "\u01d3\u01d4\u00037\u0019\u0000\u01d4\u01d5\u0003=\u001c\u0000\u01d5\u01db" +
                    "\u0001\u0000\u0000\u0000\u01d6\u01d8\u0005-\u0000\u0000\u01d7\u01d6\u0001" +
                    "\u0000\u0000\u0000\u01d7\u01d8\u0001\u0000\u0000\u0000\u01d8\u01d9\u0001" +
                    "\u0000\u0000\u0000\u01d9\u01db\u0003=\u001c\u0000\u01da\u01b0\u0001\u0000" +
                    "\u0000\u0000\u01da\u01c4\u0001\u0000\u0000\u0000\u01da\u01d1\u0001\u0000" +
                    "\u0000\u0000\u01da\u01d7\u0001\u0000\u0000\u0000\u01dbD\u0001\u0000\u0000" +
                    "\u0000\u01dc\u01dd\u0003C\u001f\u0000\u01ddF\u0001\u0000\u0000\u0000\u01de" +
                    "\u01df\u0003A\u001e\u0000\u01dfH\u0001\u0000\u0000\u0000\u01e0\u01e2\b" +
                    "\u0003\u0000\u0000\u01e1\u01e0\u0001\u0000\u0000\u0000\u01e2\u01e3\u0001" +
                    "\u0000\u0000\u0000\u01e3\u01e1\u0001\u0000\u0000\u0000\u01e3\u01e4\u0001" +
                    "\u0000\u0000\u0000\u01e4J\u0001\u0000\u0000\u0000\u01e5\u01e7\u0005\r" +
                    "\u0000\u0000\u01e6\u01e5\u0001\u0000\u0000\u0000\u01e7\u01ea\u0001\u0000" +
                    "\u0000\u0000\u01e8\u01e6\u0001\u0000\u0000\u0000\u01e8\u01e9\u0001\u0000" +
                    "\u0000\u0000\u01e9\u01eb\u0001\u0000\u0000\u0000\u01ea\u01e8\u0001\u0000" +
                    "\u0000\u0000\u01eb\u01ec\u0005\n\u0000\u0000\u01ec\u01ed\u0001\u0000\u0000" +
                    "\u0000\u01ed\u01ee\u0006#\u0001\u0000\u01ee\u01ef\u0006#\u0005\u0000\u01ef" +
                    "L\u0001\u0000\u0000\u0000\u01f0\u01f2\b\u0003\u0000\u0000\u01f1\u01f0" +
                    "\u0001\u0000\u0000\u0000\u01f2\u01f3\u0001\u0000\u0000\u0000\u01f3\u01f1" +
                    "\u0001\u0000\u0000\u0000\u01f3\u01f4\u0001\u0000\u0000\u0000\u01f4\u01f5" +
                    "\u0001\u0000\u0000\u0000\u01f5\u01f6\u0006$\u0005\u0000\u01f6N\u0001\u0000" +
                    "\u0000\u0000\u01f7\u01f8\u0003C\u001f\u0000\u01f8\u01f9\u0001\u0000\u0000" +
                    "\u0000\u01f9\u01fa\u0006%\u0005\u0000\u01faP\u0001\u0000\u0000\u0000\u01fb" +
                    "\u01fc\u0003A\u001e\u0000\u01fc\u01fd\u0001\u0000\u0000\u0000\u01fd\u01fe" +
                    "\u0006&\u0005\u0000\u01feR\u0001\u0000\u0000\u0000\u01ff\u0200\u0002A" +
                    "Z\u0000\u0200T\u0001\u0000\u0000\u0000\u0201\u0202\u0005:\u0000\u0000" +
                    "\u0202V\u0001\u0000\u0000\u0000\u0203\u0205\u0007\u0004\u0000\u0000\u0204" +
                    "\u0203\u0001\u0000\u0000\u0000\u0205\u0206\u0001\u0000\u0000\u0000\u0206" +
                    "\u0204\u0001\u0000\u0000\u0000\u0206\u0207\u0001\u0000\u0000\u0000\u0207" +
                    "X\u0001\u0000\u0000\u0000\u0208\u020a\u0007\u0005\u0000\u0000\u0209\u020b" +
                    "\b\u0003\u0000\u0000\u020a\u0209\u0001\u0000\u0000\u0000\u020b\u020c\u0001" +
                    "\u0000\u0000\u0000\u020c\u020a\u0001\u0000\u0000\u0000\u020c\u020d\u0001" +
                    "\u0000\u0000\u0000\u020dZ\u0001\u0000\u0000\u0000\u020e\u0210\u0005\r" +
                    "\u0000\u0000\u020f\u020e\u0001\u0000\u0000\u0000\u0210\u0213\u0001\u0000" +
                    "\u0000\u0000\u0211\u020f\u0001\u0000\u0000\u0000\u0211\u0212\u0001\u0000" +
                    "\u0000\u0000\u0212\u0214\u0001\u0000\u0000\u0000\u0213\u0211\u0001\u0000" +
                    "\u0000\u0000\u0214\u0215\u0005\n\u0000\u0000\u0215\u0216\u0001\u0000\u0000" +
                    "\u0000\u0216\u0217\u0006+\u0005\u0000\u0217\u0218\u0006+\u0001\u0000\u0218" +
                    "\\\u0001\u0000\u0000\u0000&\u0000\u0001\u0002\u0003\u0004\u0117\u0121" +
                    "\u0125\u0169\u016c\u0171\u017a\u0183\u0186\u018b\u0192\u0197\u019a\u019d" +
                    "\u01a4\u01aa\u01ad\u01b0\u01b5\u01bc\u01c1\u01c4\u01c7\u01ce\u01d1\u01d7" +
                    "\u01da\u01e3\u01e8\u01f3\u0206\u020c\u0211\u0006\u0005\u0001\u0000\u0006" +
                    "\u0000\u0000\u0005\u0002\u0000\u0005\u0003\u0000\u0005\u0004\u0000\u0004" +
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