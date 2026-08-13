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
// Generated from ChestTrapLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.chesttrap;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ChestTrapLexer extends Lexer {
    public static final int
            RECORD_COUNT = 1, NAME = 2, CODE = 3, LEVEL = 4, DESTROY = 5, MAGIC = 6, MSG = 7, MSG_DEATH = 8,
            COMMENT = 9, EOL = 10, EFFECT = 11, EFFECT_MESSAGE = 12, DICE = 13, TIME = 14, EFFECT_YX = 15,
            EXPR = 16, COLON = 17, UCASE = 18, INTEGER = 19, SIMPLE_DICE_STRING = 20, COMPLEX_DICE_STRING = 21,
            STRING = 22, ROL_EOL = 23, FREE_TEXT = 24, DICE_SIMPLE_VALUE = 25, DICE_COMPLEX_VALUE = 26,
            EXPR_CHAR = 27, EXPR_COLON = 28, EXPR_UCASE = 29, EXPR_OP = 30, EXPR_EOL = 31;
    public static final int
            REST_OF_LINE = 1, FREE_TEXT_MODE = 2, DICE_STRING_MODE = 3, EXPR_MODE = 4;
    public static final String[] ruleNames = makeRuleNames();
    /**
     * @deprecated Use {@link #VOCABULARY} instead.
     */
    @Deprecated
    public static final String[] tokenNames;
    public static final String _serializedATN =
            "\u0004\u0000\u001f\u01a2\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff" +
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
                    "\u0007!\u0002\"\u0007\"\u0002#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002" +
                    "&\u0007&\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\b\u0001\b\u0005\b\u009f\b\b\n\b\f\b\u00a2\t\b" +
                    "\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0005\t\u00a9\b\t\n\t\f\t\u00ac" +
                    "\t\t\u0001\t\u0003\t\u00af\b\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n" +
                    "\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f" +
                    "\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001" +
                    "\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001" +
                    "\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001" +
                    "\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001" +
                    "\u000f\u0001\u0010\u0001\u0010\u0001\u0011\u0004\u0011\u00f1\b\u0011\u000b" +
                    "\u0011\f\u0011\u00f2\u0001\u0012\u0003\u0012\u00f6\b\u0012\u0001\u0012" +
                    "\u0004\u0012\u00f9\b\u0012\u000b\u0012\f\u0012\u00fa\u0001\u0013\u0001" +
                    "\u0013\u0001\u0014\u0001\u0014\u0001\u0015\u0004\u0015\u0102\b\u0015\u000b" +
                    "\u0015\f\u0015\u0103\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0017\u0001" +
                    "\u0017\u0001\u0018\u0001\u0018\u0003\u0018\u010d\b\u0018\u0001\u0019\u0003" +
                    "\u0019\u0110\b\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0003\u0019\u0115" +
                    "\b\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0003" +
                    "\u0019\u011c\b\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0003\u0019\u0121" +
                    "\b\u0019\u0001\u0019\u0003\u0019\u0124\b\u0019\u0001\u0019\u0003\u0019" +
                    "\u0127\b\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019" +
                    "\u0003\u0019\u012e\b\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019" +
                    "\u0003\u0019\u0134\b\u0019\u0001\u0019\u0003\u0019\u0137\b\u0019\u0001" +
                    "\u001a\u0003\u001a\u013a\b\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0003" +
                    "\u001a\u013f\b\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001" +
                    "\u001a\u0003\u001a\u0146\b\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0003" +
                    "\u001a\u014b\b\u001a\u0001\u001a\u0003\u001a\u014e\b\u001a\u0001\u001a" +
                    "\u0003\u001a\u0151\b\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a" +
                    "\u0001\u001a\u0003\u001a\u0158\b\u001a\u0001\u001a\u0003\u001a\u015b\b" +
                    "\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0003\u001a\u0161" +
                    "\b\u001a\u0001\u001a\u0003\u001a\u0164\b\u001a\u0001\u001b\u0001\u001b" +
                    "\u0001\u001c\u0001\u001c\u0001\u001d\u0004\u001d\u016b\b\u001d\u000b\u001d" +
                    "\f\u001d\u016c\u0001\u001e\u0005\u001e\u0170\b\u001e\n\u001e\f\u001e\u0173" +
                    "\t\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001" +
                    "\u001f\u0004\u001f\u017b\b\u001f\u000b\u001f\f\u001f\u017c\u0001\u001f" +
                    "\u0001\u001f\u0001 \u0001 \u0001 \u0001 \u0001!\u0001!\u0001!\u0001!\u0001" +
                    "\"\u0001\"\u0001#\u0001#\u0001$\u0004$\u018e\b$\u000b$\f$\u018f\u0001" +
                    "%\u0001%\u0004%\u0194\b%\u000b%\f%\u0195\u0001&\u0005&\u0199\b&\n&\f&" +
                    "\u019c\t&\u0001&\u0001&\u0001&\u0001&\u0001&\u0000\u0000\'\u0005\u0001" +
                    "\u0007\u0002\t\u0003\u000b\u0004\r\u0005\u000f\u0006\u0011\u0007\u0013" +
                    "\b\u0015\t\u0017\n\u0019\u000b\u001b\f\u001d\r\u001f\u000e!\u000f#\u0010" +
                    "%\u0011\'\u0012)\u0013+\u0000-\u0000/\u00001\u00003\u00005\u00007\u0000" +
                    "9\u0000;\u0014=\u0015?\u0016A\u0017C\u0018E\u0019G\u001aI\u001bK\u001c" +
                    "M\u001dO\u001eQ\u001f\u0005\u0000\u0001\u0002\u0003\u0004\u0006\u0001" +
                    "\u0000\n\n\u0004\u0000--AZ__az\u0002\u0000MMmm\u0002\u0000\n\n\r\r\u0002" +
                    "\u0000AZ__\u0003\u0000*+--//\u01ba\u0000\u0005\u0001\u0000\u0000\u0000" +
                    "\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000" +
                    "\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f" +
                    "\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013" +
                    "\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017" +
                    "\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000\u0000\u001b" +
                    "\u0001\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000\u0000\u0000\u001f" +
                    "\u0001\u0000\u0000\u0000\u0000!\u0001\u0000\u0000\u0000\u0000#\u0001\u0000" +
                    "\u0000\u0000\u0000%\u0001\u0000\u0000\u0000\u0000\'\u0001\u0000\u0000" +
                    "\u0000\u0000)\u0001\u0000\u0000\u0000\u0000;\u0001\u0000\u0000\u0000\u0000" +
                    "=\u0001\u0000\u0000\u0000\u0001?\u0001\u0000\u0000\u0000\u0001A\u0001" +
                    "\u0000\u0000\u0000\u0002C\u0001\u0000\u0000\u0000\u0003E\u0001\u0000\u0000" +
                    "\u0000\u0003G\u0001\u0000\u0000\u0000\u0004I\u0001\u0000\u0000\u0000\u0004" +
                    "K\u0001\u0000\u0000\u0000\u0004M\u0001\u0000\u0000\u0000\u0004O\u0001" +
                    "\u0000\u0000\u0000\u0004Q\u0001\u0000\u0000\u0000\u0005S\u0001\u0000\u0000" +
                    "\u0000\u0007a\u0001\u0000\u0000\u0000\ti\u0001\u0000\u0000\u0000\u000b" +
                    "q\u0001\u0000\u0000\u0000\rx\u0001\u0000\u0000\u0000\u000f\u0081\u0001" +
                    "\u0000\u0000\u0000\u0011\u0088\u0001\u0000\u0000\u0000\u0013\u008f\u0001" +
                    "\u0000\u0000\u0000\u0015\u009c\u0001\u0000\u0000\u0000\u0017\u00aa\u0001" +
                    "\u0000\u0000\u0000\u0019\u00b4\u0001\u0000\u0000\u0000\u001b\u00bc\u0001" +
                    "\u0000\u0000\u0000\u001d\u00ca\u0001\u0000\u0000\u0000\u001f\u00d2\u0001" +
                    "\u0000\u0000\u0000!\u00da\u0001\u0000\u0000\u0000#\u00e5\u0001\u0000\u0000" +
                    "\u0000%\u00ed\u0001\u0000\u0000\u0000\'\u00f0\u0001\u0000\u0000\u0000" +
                    ")\u00f5\u0001\u0000\u0000\u0000+\u00fc\u0001\u0000\u0000\u0000-\u00fe" +
                    "\u0001\u0000\u0000\u0000/\u0101\u0001\u0000\u0000\u00001\u0105\u0001\u0000" +
                    "\u0000\u00003\u0108\u0001\u0000\u0000\u00005\u010c\u0001\u0000\u0000\u0000" +
                    "7\u0136\u0001\u0000\u0000\u00009\u0163\u0001\u0000\u0000\u0000;\u0165" +
                    "\u0001\u0000\u0000\u0000=\u0167\u0001\u0000\u0000\u0000?\u016a\u0001\u0000" +
                    "\u0000\u0000A\u0171\u0001\u0000\u0000\u0000C\u017a\u0001\u0000\u0000\u0000" +
                    "E\u0180\u0001\u0000\u0000\u0000G\u0184\u0001\u0000\u0000\u0000I\u0188" +
                    "\u0001\u0000\u0000\u0000K\u018a\u0001\u0000\u0000\u0000M\u018d\u0001\u0000" +
                    "\u0000\u0000O\u0191\u0001\u0000\u0000\u0000Q\u019a\u0001\u0000\u0000\u0000" +
                    "ST\u0005r\u0000\u0000TU\u0005e\u0000\u0000UV\u0005c\u0000\u0000VW\u0005" +
                    "o\u0000\u0000WX\u0005r\u0000\u0000XY\u0005d\u0000\u0000YZ\u0005-\u0000" +
                    "\u0000Z[\u0005c\u0000\u0000[\\\u0005o\u0000\u0000\\]\u0005u\u0000\u0000" +
                    "]^\u0005n\u0000\u0000^_\u0005t\u0000\u0000_`\u0005:\u0000\u0000`\u0006" +
                    "\u0001\u0000\u0000\u0000ab\u0005n\u0000\u0000bc\u0005a\u0000\u0000cd\u0005" +
                    "m\u0000\u0000de\u0005e\u0000\u0000ef\u0005:\u0000\u0000fg\u0001\u0000" +
                    "\u0000\u0000gh\u0006\u0001\u0000\u0000h\b\u0001\u0000\u0000\u0000ij\u0005" +
                    "c\u0000\u0000jk\u0005o\u0000\u0000kl\u0005d\u0000\u0000lm\u0005e\u0000" +
                    "\u0000mn\u0005:\u0000\u0000no\u0001\u0000\u0000\u0000op\u0006\u0002\u0000" +
                    "\u0000p\n\u0001\u0000\u0000\u0000qr\u0005l\u0000\u0000rs\u0005e\u0000" +
                    "\u0000st\u0005v\u0000\u0000tu\u0005e\u0000\u0000uv\u0005l\u0000\u0000" +
                    "vw\u0005:\u0000\u0000w\f\u0001\u0000\u0000\u0000xy\u0005d\u0000\u0000" +
                    "yz\u0005e\u0000\u0000z{\u0005s\u0000\u0000{|\u0005t\u0000\u0000|}\u0005" +
                    "r\u0000\u0000}~\u0005o\u0000\u0000~\u007f\u0005y\u0000\u0000\u007f\u0080" +
                    "\u0005:\u0000\u0000\u0080\u000e\u0001\u0000\u0000\u0000\u0081\u0082\u0005" +
                    "m\u0000\u0000\u0082\u0083\u0005a\u0000\u0000\u0083\u0084\u0005g\u0000" +
                    "\u0000\u0084\u0085\u0005i\u0000\u0000\u0085\u0086\u0005c\u0000\u0000\u0086" +
                    "\u0087\u0005:\u0000\u0000\u0087\u0010\u0001\u0000\u0000\u0000\u0088\u0089" +
                    "\u0005m\u0000\u0000\u0089\u008a\u0005s\u0000\u0000\u008a\u008b\u0005g" +
                    "\u0000\u0000\u008b\u008c\u0005:\u0000\u0000\u008c\u008d\u0001\u0000\u0000" +
                    "\u0000\u008d\u008e\u0006\u0006\u0000\u0000\u008e\u0012\u0001\u0000\u0000" +
                    "\u0000\u008f\u0090\u0005m\u0000\u0000\u0090\u0091\u0005s\u0000\u0000\u0091" +
                    "\u0092\u0005g\u0000\u0000\u0092\u0093\u0005-\u0000\u0000\u0093\u0094\u0005" +
                    "d\u0000\u0000\u0094\u0095\u0005e\u0000\u0000\u0095\u0096\u0005a\u0000" +
                    "\u0000\u0096\u0097\u0005t\u0000\u0000\u0097\u0098\u0005h\u0000\u0000\u0098" +
                    "\u0099\u0005:\u0000\u0000\u0099\u009a\u0001\u0000\u0000\u0000\u009a\u009b" +
                    "\u0006\u0007\u0000\u0000\u009b\u0014\u0001\u0000\u0000\u0000\u009c\u00a0" +
                    "\u0005#\u0000\u0000\u009d\u009f\b\u0000\u0000\u0000\u009e\u009d\u0001" +
                    "\u0000\u0000\u0000\u009f\u00a2\u0001\u0000\u0000\u0000\u00a0\u009e\u0001" +
                    "\u0000\u0000\u0000\u00a0\u00a1\u0001\u0000\u0000\u0000\u00a1\u00a3\u0001" +
                    "\u0000\u0000\u0000\u00a2\u00a0\u0001\u0000\u0000\u0000\u00a3\u00a4\u0005" +
                    "\n\u0000\u0000\u00a4\u00a5\u0001\u0000\u0000\u0000\u00a5\u00a6\u0006\b" +
                    "\u0001\u0000\u00a6\u0016\u0001\u0000\u0000\u0000\u00a7\u00a9\u0005 \u0000" +
                    "\u0000\u00a8\u00a7\u0001\u0000\u0000\u0000\u00a9\u00ac\u0001\u0000\u0000" +
                    "\u0000\u00aa\u00a8\u0001\u0000\u0000\u0000\u00aa\u00ab\u0001\u0000\u0000" +
                    "\u0000\u00ab\u00ae\u0001\u0000\u0000\u0000\u00ac\u00aa\u0001\u0000\u0000" +
                    "\u0000\u00ad\u00af\u0005\r\u0000\u0000\u00ae\u00ad\u0001\u0000\u0000\u0000" +
                    "\u00ae\u00af\u0001\u0000\u0000\u0000\u00af\u00b0\u0001\u0000\u0000\u0000" +
                    "\u00b0\u00b1\u0005\n\u0000\u0000\u00b1\u00b2\u0001\u0000\u0000\u0000\u00b2" +
                    "\u00b3\u0006\t\u0001\u0000\u00b3\u0018\u0001\u0000\u0000\u0000\u00b4\u00b5" +
                    "\u0005e\u0000\u0000\u00b5\u00b6\u0005f\u0000\u0000\u00b6\u00b7\u0005f" +
                    "\u0000\u0000\u00b7\u00b8\u0005e\u0000\u0000\u00b8\u00b9\u0005c\u0000\u0000" +
                    "\u00b9\u00ba\u0005t\u0000\u0000\u00ba\u00bb\u0005:\u0000\u0000\u00bb\u001a" +
                    "\u0001\u0000\u0000\u0000\u00bc\u00bd\u0005e\u0000\u0000\u00bd\u00be\u0005" +
                    "f\u0000\u0000\u00be\u00bf\u0005f\u0000\u0000\u00bf\u00c0\u0005e\u0000" +
                    "\u0000\u00c0\u00c1\u0005c\u0000\u0000\u00c1\u00c2\u0005t\u0000\u0000\u00c2" +
                    "\u00c3\u0005-\u0000\u0000\u00c3\u00c4\u0005m\u0000\u0000\u00c4\u00c5\u0005" +
                    "s\u0000\u0000\u00c5\u00c6\u0005g\u0000\u0000\u00c6\u00c7\u0005:\u0000" +
                    "\u0000\u00c7\u00c8\u0001\u0000\u0000\u0000\u00c8\u00c9\u0006\u000b\u0002" +
                    "\u0000\u00c9\u001c\u0001\u0000\u0000\u0000\u00ca\u00cb\u0005d\u0000\u0000" +
                    "\u00cb\u00cc\u0005i\u0000\u0000\u00cc\u00cd\u0005c\u0000\u0000\u00cd\u00ce" +
                    "\u0005e\u0000\u0000\u00ce\u00cf\u0005:\u0000\u0000\u00cf\u00d0\u0001\u0000" +
                    "\u0000\u0000\u00d0\u00d1\u0006\f\u0003\u0000\u00d1\u001e\u0001\u0000\u0000" +
                    "\u0000\u00d2\u00d3\u0005t\u0000\u0000\u00d3\u00d4\u0005i\u0000\u0000\u00d4" +
                    "\u00d5\u0005m\u0000\u0000\u00d5\u00d6\u0005e\u0000\u0000\u00d6\u00d7\u0005" +
                    ":\u0000\u0000\u00d7\u00d8\u0001\u0000\u0000\u0000\u00d8\u00d9\u0006\r" +
                    "\u0003\u0000\u00d9 \u0001\u0000\u0000\u0000\u00da\u00db\u0005e\u0000\u0000" +
                    "\u00db\u00dc\u0005f\u0000\u0000\u00dc\u00dd\u0005f\u0000\u0000\u00dd\u00de" +
                    "\u0005e\u0000\u0000\u00de\u00df\u0005c\u0000\u0000\u00df\u00e0\u0005t" +
                    "\u0000\u0000\u00e0\u00e1\u0005-\u0000\u0000\u00e1\u00e2\u0005y\u0000\u0000" +
                    "\u00e2\u00e3\u0005x\u0000\u0000\u00e3\u00e4\u0005:\u0000\u0000\u00e4\"" +
                    "\u0001\u0000\u0000\u0000\u00e5\u00e6\u0005e\u0000\u0000\u00e6\u00e7\u0005" +
                    "x\u0000\u0000\u00e7\u00e8\u0005p\u0000\u0000\u00e8\u00e9\u0005r\u0000" +
                    "\u0000\u00e9\u00ea\u0005:\u0000\u0000\u00ea\u00eb\u0001\u0000\u0000\u0000" +
                    "\u00eb\u00ec\u0006\u000f\u0004\u0000\u00ec$\u0001\u0000\u0000\u0000\u00ed" +
                    "\u00ee\u0005:\u0000\u0000\u00ee&\u0001\u0000\u0000\u0000\u00ef\u00f1\u0007" +
                    "\u0001\u0000\u0000\u00f0\u00ef\u0001\u0000\u0000\u0000\u00f1\u00f2\u0001" +
                    "\u0000\u0000\u0000\u00f2\u00f0\u0001\u0000\u0000\u0000\u00f2\u00f3\u0001" +
                    "\u0000\u0000\u0000\u00f3(\u0001\u0000\u0000\u0000\u00f4\u00f6\u0005-\u0000" +
                    "\u0000\u00f5\u00f4\u0001\u0000\u0000\u0000\u00f5\u00f6\u0001\u0000\u0000" +
                    "\u0000\u00f6\u00f8\u0001\u0000\u0000\u0000\u00f7\u00f9\u000209\u0000\u00f8" +
                    "\u00f7\u0001\u0000\u0000\u0000\u00f9\u00fa\u0001\u0000\u0000\u0000\u00fa" +
                    "\u00f8\u0001\u0000\u0000\u0000\u00fa\u00fb\u0001\u0000\u0000\u0000\u00fb" +
                    "*\u0001\u0000\u0000\u0000\u00fc\u00fd\u0005d\u0000\u0000\u00fd,\u0001" +
                    "\u0000\u0000\u0000\u00fe\u00ff\u0007\u0002\u0000\u0000\u00ff.\u0001\u0000" +
                    "\u0000\u0000\u0100\u0102\u000209\u0000\u0101\u0100\u0001\u0000\u0000\u0000" +
                    "\u0102\u0103\u0001\u0000\u0000\u0000\u0103\u0101\u0001\u0000\u0000\u0000" +
                    "\u0103\u0104\u0001\u0000\u0000\u0000\u01040\u0001\u0000\u0000\u0000\u0105" +
                    "\u0106\u0005$\u0000\u0000\u0106\u0107\u0002AZ\u0000\u01072\u0001\u0000" +
                    "\u0000\u0000\u0108\u0109\u0003/\u0015\u0000\u01094\u0001\u0000\u0000\u0000" +
                    "\u010a\u010d\u0003/\u0015\u0000\u010b\u010d\u00031\u0016\u0000\u010c\u010a" +
                    "\u0001\u0000\u0000\u0000\u010c\u010b\u0001\u0000\u0000\u0000\u010d6\u0001" +
                    "\u0000\u0000\u0000\u010e\u0110\u0005-\u0000\u0000\u010f\u010e\u0001\u0000" +
                    "\u0000\u0000\u010f\u0110\u0001\u0000\u0000\u0000\u0110\u0111\u0001\u0000" +
                    "\u0000\u0000\u0111\u0112\u00035\u0018\u0000\u0112\u0120\u0005+\u0000\u0000" +
                    "\u0113\u0115\u00035\u0018\u0000\u0114\u0113\u0001\u0000\u0000\u0000\u0114" +
                    "\u0115\u0001\u0000\u0000\u0000\u0115\u0116\u0001\u0000\u0000\u0000\u0116" +
                    "\u0117\u0003+\u0013\u0000\u0117\u011b\u00035\u0018\u0000\u0118\u0119\u0003" +
                    "-\u0014\u0000\u0119\u011a\u00035\u0018\u0000\u011a\u011c\u0001\u0000\u0000" +
                    "\u0000\u011b\u0118\u0001\u0000\u0000\u0000\u011b\u011c\u0001\u0000\u0000" +
                    "\u0000\u011c\u0121\u0001\u0000\u0000\u0000\u011d\u011e\u0003-\u0014\u0000" +
                    "\u011e\u011f\u00035\u0018\u0000\u011f\u0121\u0001\u0000\u0000\u0000\u0120" +
                    "\u0114\u0001\u0000\u0000\u0000\u0120\u011d\u0001\u0000\u0000\u0000\u0121" +
                    "\u0137\u0001\u0000\u0000\u0000\u0122\u0124\u0005-\u0000\u0000\u0123\u0122" +
                    "\u0001\u0000\u0000\u0000\u0123\u0124\u0001\u0000\u0000\u0000\u0124\u0126" +
                    "\u0001\u0000\u0000\u0000\u0125\u0127\u00035\u0018\u0000\u0126\u0125\u0001" +
                    "\u0000\u0000\u0000\u0126\u0127\u0001\u0000\u0000\u0000\u0127\u0128\u0001" +
                    "\u0000\u0000\u0000\u0128\u0129\u0003+\u0013\u0000\u0129\u012d\u00035\u0018" +
                    "\u0000\u012a\u012b\u0003-\u0014\u0000\u012b\u012c\u00035\u0018\u0000\u012c" +
                    "\u012e\u0001\u0000\u0000\u0000\u012d\u012a\u0001\u0000\u0000\u0000\u012d" +
                    "\u012e\u0001\u0000\u0000\u0000\u012e\u0137\u0001\u0000\u0000\u0000\u012f" +
                    "\u0130\u0003-\u0014\u0000\u0130\u0131\u00035\u0018\u0000\u0131\u0137\u0001" +
                    "\u0000\u0000\u0000\u0132\u0134\u0005-\u0000\u0000\u0133\u0132\u0001\u0000" +
                    "\u0000\u0000\u0133\u0134\u0001\u0000\u0000\u0000\u0134\u0135\u0001\u0000" +
                    "\u0000\u0000\u0135\u0137\u00035\u0018\u0000\u0136\u010f\u0001\u0000\u0000" +
                    "\u0000\u0136\u0123\u0001\u0000\u0000\u0000\u0136\u012f\u0001\u0000\u0000" +
                    "\u0000\u0136\u0133\u0001\u0000\u0000\u0000\u01378\u0001\u0000\u0000\u0000" +
                    "\u0138\u013a\u0005-\u0000\u0000\u0139\u0138\u0001\u0000\u0000\u0000\u0139" +
                    "\u013a\u0001\u0000\u0000\u0000\u013a\u013b\u0001\u0000\u0000\u0000\u013b" +
                    "\u013c\u00033\u0017\u0000\u013c\u014a\u0005+\u0000\u0000\u013d\u013f\u0003" +
                    "3\u0017\u0000\u013e\u013d\u0001\u0000\u0000\u0000\u013e\u013f\u0001\u0000" +
                    "\u0000\u0000\u013f\u0140\u0001\u0000\u0000\u0000\u0140\u0141\u0003+\u0013" +
                    "\u0000\u0141\u0145\u00033\u0017\u0000\u0142\u0143\u0003-\u0014\u0000\u0143" +
                    "\u0144\u00033\u0017\u0000\u0144\u0146\u0001\u0000\u0000\u0000\u0145\u0142" +
                    "\u0001\u0000\u0000\u0000\u0145\u0146\u0001\u0000\u0000\u0000\u0146\u014b" +
                    "\u0001\u0000\u0000\u0000\u0147\u0148\u0003-\u0014\u0000\u0148\u0149\u0003" +
                    "3\u0017\u0000\u0149\u014b\u0001\u0000\u0000\u0000\u014a\u013e\u0001\u0000" +
                    "\u0000\u0000\u014a\u0147\u0001\u0000\u0000\u0000\u014b\u0164\u0001\u0000" +
                    "\u0000\u0000\u014c\u014e\u0005-\u0000\u0000\u014d\u014c\u0001\u0000\u0000" +
                    "\u0000\u014d\u014e\u0001\u0000\u0000\u0000\u014e\u0150\u0001\u0000\u0000" +
                    "\u0000\u014f\u0151\u00033\u0017\u0000\u0150\u014f\u0001\u0000\u0000\u0000" +
                    "\u0150\u0151\u0001\u0000\u0000\u0000\u0151\u0152\u0001\u0000\u0000\u0000" +
                    "\u0152\u0153\u0003+\u0013\u0000\u0153\u0157\u00033\u0017\u0000\u0154\u0155" +
                    "\u0003-\u0014\u0000\u0155\u0156\u00033\u0017\u0000\u0156\u0158\u0001\u0000" +
                    "\u0000\u0000\u0157\u0154\u0001\u0000\u0000\u0000\u0157\u0158\u0001\u0000" +
                    "\u0000\u0000\u0158\u0164\u0001\u0000\u0000\u0000\u0159\u015b\u0005-\u0000" +
                    "\u0000\u015a\u0159\u0001\u0000\u0000\u0000\u015a\u015b\u0001\u0000\u0000" +
                    "\u0000\u015b\u015c\u0001\u0000\u0000\u0000\u015c\u015d\u0003-\u0014\u0000" +
                    "\u015d\u015e\u00033\u0017\u0000\u015e\u0164\u0001\u0000\u0000\u0000\u015f" +
                    "\u0161\u0005-\u0000\u0000\u0160\u015f\u0001\u0000\u0000\u0000\u0160\u0161" +
                    "\u0001\u0000\u0000\u0000\u0161\u0162\u0001\u0000\u0000\u0000\u0162\u0164" +
                    "\u00033\u0017\u0000\u0163\u0139\u0001\u0000\u0000\u0000\u0163\u014d\u0001" +
                    "\u0000\u0000\u0000\u0163\u015a\u0001\u0000\u0000\u0000\u0163\u0160\u0001" +
                    "\u0000\u0000\u0000\u0164:\u0001\u0000\u0000\u0000\u0165\u0166\u00039\u001a" +
                    "\u0000\u0166<\u0001\u0000\u0000\u0000\u0167\u0168\u00037\u0019\u0000\u0168" +
                    ">\u0001\u0000\u0000\u0000\u0169\u016b\b\u0003\u0000\u0000\u016a\u0169" +
                    "\u0001\u0000\u0000\u0000\u016b\u016c\u0001\u0000\u0000\u0000\u016c\u016a" +
                    "\u0001\u0000\u0000\u0000\u016c\u016d\u0001\u0000\u0000\u0000\u016d@\u0001" +
                    "\u0000\u0000\u0000\u016e\u0170\u0005\r\u0000\u0000\u016f\u016e\u0001\u0000" +
                    "\u0000\u0000\u0170\u0173\u0001\u0000\u0000\u0000\u0171\u016f\u0001\u0000" +
                    "\u0000\u0000\u0171\u0172\u0001\u0000\u0000\u0000\u0172\u0174\u0001\u0000" +
                    "\u0000\u0000\u0173\u0171\u0001\u0000\u0000\u0000\u0174\u0175\u0005\n\u0000" +
                    "\u0000\u0175\u0176\u0001\u0000\u0000\u0000\u0176\u0177\u0006\u001e\u0001" +
                    "\u0000\u0177\u0178\u0006\u001e\u0005\u0000\u0178B\u0001\u0000\u0000\u0000" +
                    "\u0179\u017b\b\u0003\u0000\u0000\u017a\u0179\u0001\u0000\u0000\u0000\u017b" +
                    "\u017c\u0001\u0000\u0000\u0000\u017c\u017a\u0001\u0000\u0000\u0000\u017c" +
                    "\u017d\u0001\u0000\u0000\u0000\u017d\u017e\u0001\u0000\u0000\u0000\u017e" +
                    "\u017f\u0006\u001f\u0005\u0000\u017fD\u0001\u0000\u0000\u0000\u0180\u0181" +
                    "\u00039\u001a\u0000\u0181\u0182\u0001\u0000\u0000\u0000\u0182\u0183\u0006" +
                    " \u0005\u0000\u0183F\u0001\u0000\u0000\u0000\u0184\u0185\u00037\u0019" +
                    "\u0000\u0185\u0186\u0001\u0000\u0000\u0000\u0186\u0187\u0006!\u0005\u0000" +
                    "\u0187H\u0001\u0000\u0000\u0000\u0188\u0189\u0002AZ\u0000\u0189J\u0001" +
                    "\u0000\u0000\u0000\u018a\u018b\u0005:\u0000\u0000\u018bL\u0001\u0000\u0000" +
                    "\u0000\u018c\u018e\u0007\u0004\u0000\u0000\u018d\u018c\u0001\u0000\u0000" +
                    "\u0000\u018e\u018f\u0001\u0000\u0000\u0000\u018f\u018d\u0001\u0000\u0000" +
                    "\u0000\u018f\u0190\u0001\u0000\u0000\u0000\u0190N\u0001\u0000\u0000\u0000" +
                    "\u0191\u0193\u0007\u0005\u0000\u0000\u0192\u0194\b\u0003\u0000\u0000\u0193" +
                    "\u0192\u0001\u0000\u0000\u0000\u0194\u0195\u0001\u0000\u0000\u0000\u0195" +
                    "\u0193\u0001\u0000\u0000\u0000\u0195\u0196\u0001\u0000\u0000\u0000\u0196" +
                    "P\u0001\u0000\u0000\u0000\u0197\u0199\u0005\r\u0000\u0000\u0198\u0197" +
                    "\u0001\u0000\u0000\u0000\u0199\u019c\u0001\u0000\u0000\u0000\u019a\u0198" +
                    "\u0001\u0000\u0000\u0000\u019a\u019b\u0001\u0000\u0000\u0000\u019b\u019d" +
                    "\u0001\u0000\u0000\u0000\u019c\u019a\u0001\u0000\u0000\u0000\u019d\u019e" +
                    "\u0005\n\u0000\u0000\u019e\u019f\u0001\u0000\u0000\u0000\u019f\u01a0\u0006" +
                    "&\u0005\u0000\u01a0\u01a1\u0006&\u0001\u0000\u01a1R\u0001\u0000\u0000" +
                    "\u0000&\u0000\u0001\u0002\u0003\u0004\u00a0\u00aa\u00ae\u00f2\u00f5\u00fa" +
                    "\u0103\u010c\u010f\u0114\u011b\u0120\u0123\u0126\u012d\u0133\u0136\u0139" +
                    "\u013e\u0145\u014a\u014d\u0150\u0157\u015a\u0160\u0163\u016c\u0171\u017c" +
                    "\u018f\u0195\u019a\u0006\u0005\u0001\u0000\u0006\u0000\u0000\u0005\u0002" +
                    "\u0000\u0005\u0003\u0000\u0005\u0004\u0000\u0004\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());
    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    private static final String[] _LITERAL_NAMES = makeLiteralNames();
    private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
    public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };
    public static String[] modeNames = {
            "DEFAULT_MODE", "REST_OF_LINE", "FREE_TEXT_MODE", "DICE_STRING_MODE",
            "EXPR_MODE"
    };

    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

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

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }

    public ChestTrapLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    private static String[] makeRuleNames() {
        return new String[]{
                "RECORD_COUNT", "NAME", "CODE", "LEVEL", "DESTROY", "MAGIC", "MSG", "MSG_DEATH",
                "COMMENT", "EOL", "EFFECT", "EFFECT_MESSAGE", "DICE", "TIME", "EFFECT_YX",
                "EXPR", "COLON", "UCASE", "INTEGER", "DICE_D", "DICE_M", "DICE_INTEGER",
                "DICE_DOLLAR_LETTER", "DICE_SIMPLE_NUMBER", "DICE_ANY_NUMBER", "COMPLEX_DICE_STRING_BODY",
                "SIMPLE_DICE_STRING_BODY", "SIMPLE_DICE_STRING", "COMPLEX_DICE_STRING",
                "STRING", "ROL_EOL", "FREE_TEXT", "DICE_SIMPLE_VALUE", "DICE_COMPLEX_VALUE",
                "EXPR_CHAR", "EXPR_COLON", "EXPR_UCASE", "EXPR_OP", "EXPR_EOL"
        };
    }

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'name:'", "'code:'", "'level:'", "'destroy:'",
                "'magic:'", "'msg:'", "'msg-death:'", null, null, "'effect:'", "'effect-msg:'",
                "'dice:'", "'time:'", "'effect-yx:'", "'expr:'"
        };
    }

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "NAME", "CODE", "LEVEL", "DESTROY", "MAGIC", "MSG",
                "MSG_DEATH", "COMMENT", "EOL", "EFFECT", "EFFECT_MESSAGE", "DICE", "TIME",
                "EFFECT_YX", "EXPR", "COLON", "UCASE", "INTEGER", "SIMPLE_DICE_STRING",
                "COMPLEX_DICE_STRING", "STRING", "ROL_EOL", "FREE_TEXT", "DICE_SIMPLE_VALUE",
                "DICE_COMPLEX_VALUE", "EXPR_CHAR", "EXPR_COLON", "EXPR_UCASE", "EXPR_OP",
                "EXPR_EOL"
        };
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

    @Override
    public String getGrammarFileName() {
        return "ChestTrapLexer.g4";
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
}