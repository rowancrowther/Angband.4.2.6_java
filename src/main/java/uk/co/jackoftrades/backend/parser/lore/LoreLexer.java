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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/LoreLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.lore;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class LoreLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            EOL = 1, NAME = 2, COUNTS = 3, BLOW = 4, FLAGS = 5, SPELLS = 6, BASE = 7, DROP = 8, DROP_BASE = 9,
            FRIENDS = 10, FRIENDS_BASE = 11, MIMIC = 12, INTEGER = 13, COLON = 14, STRING = 15,
            TVAL = 16, FRIENDS_NAME = 17, FRIENDS_BASE_NAME = 18, DICE_STRING = 19, MONSTER_NAME = 20,
            FLAG = 21, OR = 22, FLAG_EOL = 23, SPELL = 24, SPELL_EOL = 25, SPELL_OR = 26, BLOW_MODE_VALUES = 27,
            BLOW_EOL = 28;
    public static final int
            MONSTER_NAME_MODE = 1, FLAG_MODE = 2, SPELL_MODE = 3, BLOW_MODE = 4;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE", "MONSTER_NAME_MODE", "FLAG_MODE", "SPELL_MODE", "BLOW_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "EOL", "NAME", "COUNTS", "BLOW", "FLAGS", "SPELLS", "BASE", "DROP", "DROP_BASE",
                "FRIENDS", "FRIENDS_BASE", "MIMIC", "INTEGER_FRAGMENT", "INTEGER", "COLON",
                "STRING", "TVAL", "FRIENDS_NAME", "FRIENDS_BASE_NAME", "DICE_STRING",
                "MONSTER_NAME", "FLAG", "OR", "FLAG_EOL", "SPELL", "SPELL_EOL", "SPELL_OR",
                "BLOW_MODE_VALUES", "UCASE_FLAG", "DICE_STRING_FRAGMENT", "PLUS_FRAGMENT",
                "MINUS_FRAGMENT", "D_FRAGMENT", "M_FRAGMENT", "BLOW_EOL"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, "'name:'", "'counts:'", "'blow:'", "'flags:'", "'spells:'",
                "'base:'", "'drop:'", "'drop-base:'", "'friends:'", "'friends-base:'",
                "'mimic:'", null, "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "EOL", "NAME", "COUNTS", "BLOW", "FLAGS", "SPELLS", "BASE", "DROP",
                "DROP_BASE", "FRIENDS", "FRIENDS_BASE", "MIMIC", "INTEGER", "COLON",
                "STRING", "TVAL", "FRIENDS_NAME", "FRIENDS_BASE_NAME", "DICE_STRING",
                "MONSTER_NAME", "FLAG", "OR", "FLAG_EOL", "SPELL", "SPELL_EOL", "SPELL_OR",
                "BLOW_MODE_VALUES", "BLOW_EOL"
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


    public LoreLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "LoreLexer.g4";
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
            "\u0004\u0000\u001c\u013f\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff" +
                    "\uffff\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002" +
                    "\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002" +
                    "\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002" +
                    "\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002" +
                    "\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e" +
                    "\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011" +
                    "\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014" +
                    "\u0002\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017" +
                    "\u0002\u0018\u0007\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a" +
                    "\u0002\u001b\u0007\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d" +
                    "\u0002\u001e\u0007\u001e\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!" +
                    "\u0007!\u0002\"\u0007\"\u0001\u0000\u0005\u0000M\b\u0000\n\u0000\f\u0000" +
                    "P\t\u0000\u0001\u0000\u0003\u0000S\b\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b" +
                    "\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\f\u0004\f\u00ba\b\f\u000b\f\f\f\u00bb" +
                    "\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000f\u0004\u000f\u00c3" +
                    "\b\u000f\u000b\u000f\f\u000f\u00c4\u0001\u0010\u0004\u0010\u00c8\b\u0010" +
                    "\u000b\u0010\f\u0010\u00c9\u0001\u0011\u0004\u0011\u00cd\b\u0011\u000b" +
                    "\u0011\f\u0011\u00ce\u0001\u0012\u0001\u0012\u0004\u0012\u00d3\b\u0012" +
                    "\u000b\u0012\f\u0012\u00d4\u0001\u0012\u0001\u0012\u0004\u0012\u00d9\b" +
                    "\u0012\u000b\u0012\f\u0012\u00da\u0005\u0012\u00dd\b\u0012\n\u0012\f\u0012" +
                    "\u00e0\t\u0012\u0001\u0013\u0003\u0013\u00e3\b\u0013\u0001\u0013\u0001" +
                    "\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001" +
                    "\u0013\u0001\u0014\u0004\u0014\u00ee\b\u0014\u000b\u0014\f\u0014\u00ef" +
                    "\u0001\u0014\u0001\u0014\u0001\u0015\u0004\u0015\u00f5\b\u0015\u000b\u0015" +
                    "\f\u0015\u00f6\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0017" +
                    "\u0003\u0017\u00fe\b\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017" +
                    "\u0001\u0017\u0001\u0018\u0004\u0018\u0106\b\u0018\u000b\u0018\f\u0018" +
                    "\u0107\u0001\u0019\u0003\u0019\u010b\b\u0019\u0001\u0019\u0001\u0019\u0001" +
                    "\u0019\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0001" +
                    "\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001" +
                    "\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001c\u0004" +
                    "\u001c\u0121\b\u001c\u000b\u001c\f\u001c\u0122\u0001\u001d\u0003\u001d" +
                    "\u0126\b\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d" +
                    "\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001e\u0001\u001e\u0001\u001f" +
                    "\u0001\u001f\u0001 \u0001 \u0001!\u0001!\u0001\"\u0003\"\u0139\b\"\u0001" +
                    "\"\u0001\"\u0001\"\u0001\"\u0001\"\u0000\u0000#\u0005\u0001\u0007\u0002" +
                    "\t\u0003\u000b\u0004\r\u0005\u000f\u0006\u0011\u0007\u0013\b\u0015\t\u0017" +
                    "\n\u0019\u000b\u001b\f\u001d\u0000\u001f\r!\u000e#\u000f%\u0010\'\u0011" +
                    ")\u0012+\u0013-\u0014/\u00151\u00163\u00175\u00187\u00199\u001a;\u001b" +
                    "=\u0000?\u0000A\u0000C\u0000E\u0000G\u0000I\u001c\u0005\u0000\u0001\u0002" +
                    "\u0003\u0004\b\t\u0000  &\'**--<<>>AZaz~~\u0002\u0000  az\u000f\u0000" +
                    "  \'\',-09<<>>AZaz\u00e1\u00e1\u00e9\u00e9\u00eb\u00eb\u00ee\u00ee\u00f4" +
                    "\u00f4\u00f6\u00f6\u00fa\u00fb\u0002\u0000MMaz\u0001\u0000az\u0002\u0000" +
                    "\n\n\r\r\u0003\u000009AZ__\u0002\u0000AZ__\u0145\u0000\u0005\u0001\u0000" +
                    "\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000" +
                    "\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000" +
                    "\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000" +
                    "\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000" +
                    "\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000" +
                    "\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000" +
                    "\u0000!\u0001\u0000\u0000\u0000\u0000#\u0001\u0000\u0000\u0000\u0000%" +
                    "\u0001\u0000\u0000\u0000\u0000\'\u0001\u0000\u0000\u0000\u0000)\u0001" +
                    "\u0000\u0000\u0000\u0000+\u0001\u0000\u0000\u0000\u0001-\u0001\u0000\u0000" +
                    "\u0000\u0002/\u0001\u0000\u0000\u0000\u00021\u0001\u0000\u0000\u0000\u0002" +
                    "3\u0001\u0000\u0000\u0000\u00035\u0001\u0000\u0000\u0000\u00037\u0001" +
                    "\u0000\u0000\u0000\u00039\u0001\u0000\u0000\u0000\u0004;\u0001\u0000\u0000" +
                    "\u0000\u0004I\u0001\u0000\u0000\u0000\u0005N\u0001\u0000\u0000\u0000\u0007" +
                    "X\u0001\u0000\u0000\u0000\t`\u0001\u0000\u0000\u0000\u000bh\u0001\u0000" +
                    "\u0000\u0000\rp\u0001\u0000\u0000\u0000\u000fy\u0001\u0000\u0000\u0000" +
                    "\u0011\u0083\u0001\u0000\u0000\u0000\u0013\u0089\u0001\u0000\u0000\u0000" +
                    "\u0015\u008f\u0001\u0000\u0000\u0000\u0017\u009a\u0001\u0000\u0000\u0000" +
                    "\u0019\u00a3\u0001\u0000\u0000\u0000\u001b\u00b1\u0001\u0000\u0000\u0000" +
                    "\u001d\u00b9\u0001\u0000\u0000\u0000\u001f\u00bd\u0001\u0000\u0000\u0000" +
                    "!\u00bf\u0001\u0000\u0000\u0000#\u00c2\u0001\u0000\u0000\u0000%\u00c7" +
                    "\u0001\u0000\u0000\u0000\'\u00cc\u0001\u0000\u0000\u0000)\u00d0\u0001" +
                    "\u0000\u0000\u0000+\u00e2\u0001\u0000\u0000\u0000-\u00ed\u0001\u0000\u0000" +
                    "\u0000/\u00f4\u0001\u0000\u0000\u00001\u00f8\u0001\u0000\u0000\u00003" +
                    "\u00fd\u0001\u0000\u0000\u00005\u0105\u0001\u0000\u0000\u00007\u010a\u0001" +
                    "\u0000\u0000\u00009\u0111\u0001\u0000\u0000\u0000;\u0115\u0001\u0000\u0000" +
                    "\u0000=\u0120\u0001\u0000\u0000\u0000?\u0125\u0001\u0000\u0000\u0000A" +
                    "\u012f\u0001\u0000\u0000\u0000C\u0131\u0001\u0000\u0000\u0000E\u0133\u0001" +
                    "\u0000\u0000\u0000G\u0135\u0001\u0000\u0000\u0000I\u0138\u0001\u0000\u0000" +
                    "\u0000KM\u0005 \u0000\u0000LK\u0001\u0000\u0000\u0000MP\u0001\u0000\u0000" +
                    "\u0000NL\u0001\u0000\u0000\u0000NO\u0001\u0000\u0000\u0000OR\u0001\u0000" +
                    "\u0000\u0000PN\u0001\u0000\u0000\u0000QS\u0005\r\u0000\u0000RQ\u0001\u0000" +
                    "\u0000\u0000RS\u0001\u0000\u0000\u0000ST\u0001\u0000\u0000\u0000TU\u0005" +
                    "\n\u0000\u0000UV\u0001\u0000\u0000\u0000VW\u0006\u0000\u0000\u0000W\u0006" +
                    "\u0001\u0000\u0000\u0000XY\u0005n\u0000\u0000YZ\u0005a\u0000\u0000Z[\u0005" +
                    "m\u0000\u0000[\\\u0005e\u0000\u0000\\]\u0005:\u0000\u0000]^\u0001\u0000" +
                    "\u0000\u0000^_\u0006\u0001\u0001\u0000_\b\u0001\u0000\u0000\u0000`a\u0005" +
                    "c\u0000\u0000ab\u0005o\u0000\u0000bc\u0005u\u0000\u0000cd\u0005n\u0000" +
                    "\u0000de\u0005t\u0000\u0000ef\u0005s\u0000\u0000fg\u0005:\u0000\u0000" +
                    "g\n\u0001\u0000\u0000\u0000hi\u0005b\u0000\u0000ij\u0005l\u0000\u0000" +
                    "jk\u0005o\u0000\u0000kl\u0005w\u0000\u0000lm\u0005:\u0000\u0000mn\u0001" +
                    "\u0000\u0000\u0000no\u0006\u0003\u0002\u0000o\f\u0001\u0000\u0000\u0000" +
                    "pq\u0005f\u0000\u0000qr\u0005l\u0000\u0000rs\u0005a\u0000\u0000st\u0005" +
                    "g\u0000\u0000tu\u0005s\u0000\u0000uv\u0005:\u0000\u0000vw\u0001\u0000" +
                    "\u0000\u0000wx\u0006\u0004\u0003\u0000x\u000e\u0001\u0000\u0000\u0000" +
                    "yz\u0005s\u0000\u0000z{\u0005p\u0000\u0000{|\u0005e\u0000\u0000|}\u0005" +
                    "l\u0000\u0000}~\u0005l\u0000\u0000~\u007f\u0005s\u0000\u0000\u007f\u0080" +
                    "\u0005:\u0000\u0000\u0080\u0081\u0001\u0000\u0000\u0000\u0081\u0082\u0006" +
                    "\u0005\u0004\u0000\u0082\u0010\u0001\u0000\u0000\u0000\u0083\u0084\u0005" +
                    "b\u0000\u0000\u0084\u0085\u0005a\u0000\u0000\u0085\u0086\u0005s\u0000" +
                    "\u0000\u0086\u0087\u0005e\u0000\u0000\u0087\u0088\u0005:\u0000\u0000\u0088" +
                    "\u0012\u0001\u0000\u0000\u0000\u0089\u008a\u0005d\u0000\u0000\u008a\u008b" +
                    "\u0005r\u0000\u0000\u008b\u008c\u0005o\u0000\u0000\u008c\u008d\u0005p" +
                    "\u0000\u0000\u008d\u008e\u0005:\u0000\u0000\u008e\u0014\u0001\u0000\u0000" +
                    "\u0000\u008f\u0090\u0005d\u0000\u0000\u0090\u0091\u0005r\u0000\u0000\u0091" +
                    "\u0092\u0005o\u0000\u0000\u0092\u0093\u0005p\u0000\u0000\u0093\u0094\u0005" +
                    "-\u0000\u0000\u0094\u0095\u0005b\u0000\u0000\u0095\u0096\u0005a\u0000" +
                    "\u0000\u0096\u0097\u0005s\u0000\u0000\u0097\u0098\u0005e\u0000\u0000\u0098" +
                    "\u0099\u0005:\u0000\u0000\u0099\u0016\u0001\u0000\u0000\u0000\u009a\u009b" +
                    "\u0005f\u0000\u0000\u009b\u009c\u0005r\u0000\u0000\u009c\u009d\u0005i" +
                    "\u0000\u0000\u009d\u009e\u0005e\u0000\u0000\u009e\u009f\u0005n\u0000\u0000" +
                    "\u009f\u00a0\u0005d\u0000\u0000\u00a0\u00a1\u0005s\u0000\u0000\u00a1\u00a2" +
                    "\u0005:\u0000\u0000\u00a2\u0018\u0001\u0000\u0000\u0000\u00a3\u00a4\u0005" +
                    "f\u0000\u0000\u00a4\u00a5\u0005r\u0000\u0000\u00a5\u00a6\u0005i\u0000" +
                    "\u0000\u00a6\u00a7\u0005e\u0000\u0000\u00a7\u00a8\u0005n\u0000\u0000\u00a8" +
                    "\u00a9\u0005d\u0000\u0000\u00a9\u00aa\u0005s\u0000\u0000\u00aa\u00ab\u0005" +
                    "-\u0000\u0000\u00ab\u00ac\u0005b\u0000\u0000\u00ac\u00ad\u0005a\u0000" +
                    "\u0000\u00ad\u00ae\u0005s\u0000\u0000\u00ae\u00af\u0005e\u0000\u0000\u00af" +
                    "\u00b0\u0005:\u0000\u0000\u00b0\u001a\u0001\u0000\u0000\u0000\u00b1\u00b2" +
                    "\u0005m\u0000\u0000\u00b2\u00b3\u0005i\u0000\u0000\u00b3\u00b4\u0005m" +
                    "\u0000\u0000\u00b4\u00b5\u0005i\u0000\u0000\u00b5\u00b6\u0005c\u0000\u0000" +
                    "\u00b6\u00b7\u0005:\u0000\u0000\u00b7\u001c\u0001\u0000\u0000\u0000\u00b8" +
                    "\u00ba\u000209\u0000\u00b9\u00b8\u0001\u0000\u0000\u0000\u00ba\u00bb\u0001" +
                    "\u0000\u0000\u0000\u00bb\u00b9\u0001\u0000\u0000\u0000\u00bb\u00bc\u0001" +
                    "\u0000\u0000\u0000\u00bc\u001e\u0001\u0000\u0000\u0000\u00bd\u00be\u0003" +
                    "\u001d\f\u0000\u00be \u0001\u0000\u0000\u0000\u00bf\u00c0\u0005:\u0000" +
                    "\u0000\u00c0\"\u0001\u0000\u0000\u0000\u00c1\u00c3\u0007\u0000\u0000\u0000" +
                    "\u00c2\u00c1\u0001\u0000\u0000\u0000\u00c3\u00c4\u0001\u0000\u0000\u0000" +
                    "\u00c4\u00c2\u0001\u0000\u0000\u0000\u00c4\u00c5\u0001\u0000\u0000\u0000" +
                    "\u00c5$\u0001\u0000\u0000\u0000\u00c6\u00c8\u0007\u0001\u0000\u0000\u00c7" +
                    "\u00c6\u0001\u0000\u0000\u0000\u00c8\u00c9\u0001\u0000\u0000\u0000\u00c9" +
                    "\u00c7\u0001\u0000\u0000\u0000\u00c9\u00ca\u0001\u0000\u0000\u0000\u00ca" +
                    "&\u0001\u0000\u0000\u0000\u00cb\u00cd\u0007\u0002\u0000\u0000\u00cc\u00cb" +
                    "\u0001\u0000\u0000\u0000\u00cd\u00ce\u0001\u0000\u0000\u0000\u00ce\u00cc" +
                    "\u0001\u0000\u0000\u0000\u00ce\u00cf\u0001\u0000\u0000\u0000\u00cf(\u0001" +
                    "\u0000\u0000\u0000\u00d0\u00d2\u0007\u0003\u0000\u0000\u00d1\u00d3\u0002" +
                    "az\u0000\u00d2\u00d1\u0001\u0000\u0000\u0000\u00d3\u00d4\u0001\u0000\u0000" +
                    "\u0000\u00d4\u00d2\u0001\u0000\u0000\u0000\u00d4\u00d5\u0001\u0000\u0000" +
                    "\u0000\u00d5\u00de\u0001\u0000\u0000\u0000\u00d6\u00d8\u0005 \u0000\u0000" +
                    "\u00d7\u00d9\u0007\u0004\u0000\u0000\u00d8\u00d7\u0001\u0000\u0000\u0000" +
                    "\u00d9\u00da\u0001\u0000\u0000\u0000\u00da\u00d8\u0001\u0000\u0000\u0000" +
                    "\u00da\u00db\u0001\u0000\u0000\u0000\u00db\u00dd\u0001\u0000\u0000\u0000" +
                    "\u00dc\u00d6\u0001\u0000\u0000\u0000\u00dd\u00e0\u0001\u0000\u0000\u0000" +
                    "\u00de\u00dc\u0001\u0000\u0000\u0000\u00de\u00df\u0001\u0000\u0000\u0000" +
                    "\u00df*\u0001\u0000\u0000\u0000\u00e0\u00de\u0001\u0000\u0000\u0000\u00e1" +
                    "\u00e3\u0005-\u0000\u0000\u00e2\u00e1\u0001\u0000\u0000\u0000\u00e2\u00e3" +
                    "\u0001\u0000\u0000\u0000\u00e3\u00e4\u0001\u0000\u0000\u0000\u00e4\u00e5" +
                    "\u0003\u001f\r\u0000\u00e5\u00e6\u0005+\u0000\u0000\u00e6\u00e7\u0003" +
                    "\u001f\r\u0000\u00e7\u00e8\u0005d\u0000\u0000\u00e8\u00e9\u0003\u001f" +
                    "\r\u0000\u00e9\u00ea\u0005M\u0000\u0000\u00ea\u00eb\u0003\u001f\r\u0000" +
                    "\u00eb,\u0001\u0000\u0000\u0000\u00ec\u00ee\b\u0005\u0000\u0000\u00ed" +
                    "\u00ec\u0001\u0000\u0000\u0000\u00ee\u00ef\u0001\u0000\u0000\u0000\u00ef" +
                    "\u00ed\u0001\u0000\u0000\u0000\u00ef\u00f0\u0001\u0000\u0000\u0000\u00f0" +
                    "\u00f1\u0001\u0000\u0000\u0000\u00f1\u00f2\u0006\u0014\u0005\u0000\u00f2" +
                    ".\u0001\u0000\u0000\u0000\u00f3\u00f5\u0007\u0006\u0000\u0000\u00f4\u00f3" +
                    "\u0001\u0000\u0000\u0000\u00f5\u00f6\u0001\u0000\u0000\u0000\u00f6\u00f4" +
                    "\u0001\u0000\u0000\u0000\u00f6\u00f7\u0001\u0000\u0000\u0000\u00f70\u0001" +
                    "\u0000\u0000\u0000\u00f8\u00f9\u0005 \u0000\u0000\u00f9\u00fa\u0005|\u0000" +
                    "\u0000\u00fa\u00fb\u0005 \u0000\u0000\u00fb2\u0001\u0000\u0000\u0000\u00fc" +
                    "\u00fe\u0005\r\u0000\u0000\u00fd\u00fc\u0001\u0000\u0000\u0000\u00fd\u00fe" +
                    "\u0001\u0000\u0000\u0000\u00fe\u00ff\u0001\u0000\u0000\u0000\u00ff\u0100" +
                    "\u0005\n\u0000\u0000\u0100\u0101\u0001\u0000\u0000\u0000\u0101\u0102\u0006" +
                    "\u0017\u0005\u0000\u0102\u0103\u0006\u0017\u0000\u0000\u01034\u0001\u0000" +
                    "\u0000\u0000\u0104\u0106\u0007\u0006\u0000\u0000\u0105\u0104\u0001\u0000" +
                    "\u0000\u0000\u0106\u0107\u0001\u0000\u0000\u0000\u0107\u0105\u0001\u0000" +
                    "\u0000\u0000\u0107\u0108\u0001\u0000\u0000\u0000\u01086\u0001\u0000\u0000" +
                    "\u0000\u0109\u010b\u0005\r\u0000\u0000\u010a\u0109\u0001\u0000\u0000\u0000" +
                    "\u010a\u010b\u0001\u0000\u0000\u0000\u010b\u010c\u0001\u0000\u0000\u0000" +
                    "\u010c\u010d\u0005\n\u0000\u0000\u010d\u010e\u0001\u0000\u0000\u0000\u010e" +
                    "\u010f\u0006\u0019\u0005\u0000\u010f\u0110\u0006\u0019\u0000\u0000\u0110" +
                    "8\u0001\u0000\u0000\u0000\u0111\u0112\u0005 \u0000\u0000\u0112\u0113\u0005" +
                    "|\u0000\u0000\u0113\u0114\u0005 \u0000\u0000\u0114:\u0001\u0000\u0000" +
                    "\u0000\u0115\u0116\u0003=\u001c\u0000\u0116\u0117\u0003!\u000e\u0000\u0117" +
                    "\u0118\u0003=\u001c\u0000\u0118\u0119\u0003!\u000e\u0000\u0119\u011a\u0003" +
                    "?\u001d\u0000\u011a\u011b\u0003!\u000e\u0000\u011b\u011c\u0003\u001d\f" +
                    "\u0000\u011c\u011d\u0003!\u000e\u0000\u011d\u011e\u0003\u001d\f\u0000" +
                    "\u011e<\u0001\u0000\u0000\u0000\u011f\u0121\u0007\u0007\u0000\u0000\u0120" +
                    "\u011f\u0001\u0000\u0000\u0000\u0121\u0122\u0001\u0000\u0000\u0000\u0122" +
                    "\u0120\u0001\u0000\u0000\u0000\u0122\u0123\u0001\u0000\u0000\u0000\u0123" +
                    ">\u0001\u0000\u0000\u0000\u0124\u0126\u0003C\u001f\u0000\u0125\u0124\u0001" +
                    "\u0000\u0000\u0000\u0125\u0126\u0001\u0000\u0000\u0000\u0126\u0127\u0001" +
                    "\u0000\u0000\u0000\u0127\u0128\u0003\u001d\f\u0000\u0128\u0129\u0003A" +
                    "\u001e\u0000\u0129\u012a\u0003\u001d\f\u0000\u012a\u012b\u0003E \u0000" +
                    "\u012b\u012c\u0003\u001d\f\u0000\u012c\u012d\u0003G!\u0000\u012d\u012e" +
                    "\u0003\u001d\f\u0000\u012e@\u0001\u0000\u0000\u0000\u012f\u0130\u0005" +
                    "+\u0000\u0000\u0130B\u0001\u0000\u0000\u0000\u0131\u0132\u0005-\u0000" +
                    "\u0000\u0132D\u0001\u0000\u0000\u0000\u0133\u0134\u0005d\u0000\u0000\u0134" +
                    "F\u0001\u0000\u0000\u0000\u0135\u0136\u0005M\u0000\u0000\u0136H\u0001" +
                    "\u0000\u0000\u0000\u0137\u0139\u0005\r\u0000\u0000\u0138\u0137\u0001\u0000" +
                    "\u0000\u0000\u0138\u0139\u0001\u0000\u0000\u0000\u0139\u013a\u0001\u0000" +
                    "\u0000\u0000\u013a\u013b\u0005\n\u0000\u0000\u013b\u013c\u0001\u0000\u0000" +
                    "\u0000\u013c\u013d\u0006\"\u0005\u0000\u013d\u013e\u0006\"\u0000\u0000" +
                    "\u013eJ\u0001\u0000\u0000\u0000\u0017\u0000\u0001\u0002\u0003\u0004NR" +
                    "\u00bb\u00c4\u00c9\u00ce\u00d4\u00da\u00de\u00e2\u00ef\u00f6\u00fd\u0107" +
                    "\u010a\u0122\u0125\u0138\u0006\u0006\u0000\u0000\u0005\u0001\u0000\u0005" +
                    "\u0004\u0000\u0005\u0002\u0000\u0005\u0003\u0000\u0004\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}