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

// Generated from ActivationsLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.activations;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ActivationsLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, NAME = 2, AIM = 3, LEVEL = 4, POWER = 5, MSG = 6, DESC = 7, EFFECT = 8,
            EFFECT_MESSAGE = 9, DICE = 10, TIME = 11, EFFECT_YX = 12, EXPR = 13, COLON = 14, UCASE = 15,
            INTEGER = 16, SIMPLE_DICE_STRING = 17, COMPLEX_DICE_STRING = 18, COMMENT = 19,
            EOL = 20, NAME_STRING = 21, DESC_STRING = 22, FREE_TEXT = 23, DICE_SIMPLE_VALUE = 24,
            DICE_COMPLEX_VALUE = 25, EXPR_CHAR = 26, EXPR_COLON = 27, EXPR_UCASE = 28, EXPR_OP = 29,
            EXPR_EOL = 30;
    public static final int
            NAME_MODE = 1, DESC_MODE = 2, FREE_TEXT_MODE = 3, DICE_STRING_MODE = 4, EXPR_MODE = 5;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE", "NAME_MODE", "DESC_MODE", "FREE_TEXT_MODE", "DICE_STRING_MODE",
            "EXPR_MODE"
    };

    public static final String _serializedATN =
            "\u0004\u0000\u001e\u0192\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff" +
                    "\uffff\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000" +
                    "\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003" +
                    "\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006" +
                    "\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002" +
                    "\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002" +
                    "\u000e\u0007\u000e\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002" +
                    "\u0011\u0007\u0011\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002" +
                    "\u0014\u0007\u0014\u0002\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002" +
                    "\u0017\u0007\u0017\u0002\u0018\u0007\u0018\u0002\u0019\u0007\u0019\u0002" +
                    "\u001a\u0007\u001a\u0002\u001b\u0007\u001b\u0002\u001c\u0007\u001c\u0002" +
                    "\u001d\u0007\u001d\u0002\u001e\u0007\u001e\u0002\u001f\u0007\u001f\u0002" +
                    " \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002#\u0007#\u0002$\u0007$\u0002" +
                    "%\u0007%\u0002&\u0007&\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001" +
                    "\f\u0001\r\u0001\r\u0001\u000e\u0004\u000e\u00c9\b\u000e\u000b\u000e\f" +
                    "\u000e\u00ca\u0001\u000f\u0003\u000f\u00ce\b\u000f\u0001\u000f\u0004\u000f" +
                    "\u00d1\b\u000f\u000b\u000f\f\u000f\u00d2\u0001\u0010\u0001\u0010\u0001" +
                    "\u0011\u0001\u0011\u0001\u0012\u0004\u0012\u00da\b\u0012\u000b\u0012\f" +
                    "\u0012\u00db\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014" +
                    "\u0001\u0015\u0001\u0015\u0003\u0015\u00e5\b\u0015\u0001\u0016\u0003\u0016" +
                    "\u00e8\b\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u00ed\b" +
                    "\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0003" +
                    "\u0016\u00f4\b\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u00f9" +
                    "\b\u0016\u0001\u0016\u0003\u0016\u00fc\b\u0016\u0001\u0016\u0003\u0016" +
                    "\u00ff\b\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016" +
                    "\u0003\u0016\u0106\b\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016" +
                    "\u0003\u0016\u010c\b\u0016\u0001\u0016\u0003\u0016\u010f\b\u0016\u0001" +
                    "\u0017\u0003\u0017\u0112\b\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0003" +
                    "\u0017\u0117\b\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001" +
                    "\u0017\u0003\u0017\u011e\b\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0003" +
                    "\u0017\u0123\b\u0017\u0001\u0017\u0003\u0017\u0126\b\u0017\u0001\u0017" +
                    "\u0003\u0017\u0129\b\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017" +
                    "\u0001\u0017\u0003\u0017\u0130\b\u0017\u0001\u0017\u0003\u0017\u0133\b" +
                    "\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0003\u0017\u0139" +
                    "\b\u0017\u0001\u0017\u0003\u0017\u013c\b\u0017\u0001\u0018\u0001\u0018" +
                    "\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0005\u001a\u0144\b\u001a" +
                    "\n\u001a\f\u001a\u0147\t\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001" +
                    "\u001a\u0001\u001b\u0005\u001b\u014e\b\u001b\n\u001b\f\u001b\u0151\t\u001b" +
                    "\u0001\u001b\u0003\u001b\u0154\b\u001b\u0001\u001b\u0001\u001b\u0001\u001b" +
                    "\u0001\u001b\u0001\u001c\u0004\u001c\u015b\b\u001c\u000b\u001c\f\u001c" +
                    "\u015c\u0001\u001c\u0001\u001c\u0001\u001d\u0004\u001d\u0162\b\u001d\u000b" +
                    "\u001d\f\u001d\u0163\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001" +
                    "\u001f\u0004\u001f\u016b\b\u001f\u000b\u001f\f\u001f\u016c\u0001\u001f" +
                    "\u0001\u001f\u0001 \u0001 \u0001 \u0001 \u0001!\u0001!\u0001!\u0001!\u0001" +
                    "\"\u0001\"\u0001#\u0001#\u0001$\u0004$\u017e\b$\u000b$\f$\u017f\u0001" +
                    "%\u0001%\u0004%\u0184\b%\u000b%\f%\u0185\u0001&\u0005&\u0189\b&\n&\f&" +
                    "\u018c\t&\u0001&\u0001&\u0001&\u0001&\u0001&\u0000\u0000\'\u0006\u0001" +
                    "\b\u0002\n\u0003\f\u0004\u000e\u0005\u0010\u0006\u0012\u0007\u0014\b\u0016" +
                    "\t\u0018\n\u001a\u000b\u001c\f\u001e\r \u000e\"\u000f$\u0010&\u0000(\u0000" +
                    "*\u0000,\u0000.\u00000\u00002\u00004\u00006\u00118\u0012:\u0013<\u0014" +
                    ">\u0015@\u0000B\u0016D\u0017F\u0018H\u0019J\u001aL\u001bN\u001cP\u001d" +
                    "R\u001e\u0006\u0000\u0001\u0002\u0003\u0004\u0005\u0007\u0004\u0000--" +
                    "AZ__az\u0002\u0000MMmm\u0001\u0000\n\n\u0003\u000009AZ__\u0002\u0000\n" +
                    "\n\r\r\u0002\u0000AZ__\u0003\u0000*+--//\u01a8\u0000\u0006\u0001\u0000" +
                    "\u0000\u0000\u0000\b\u0001\u0000\u0000\u0000\u0000\n\u0001\u0000\u0000" +
                    "\u0000\u0000\f\u0001\u0000\u0000\u0000\u0000\u000e\u0001\u0000\u0000\u0000" +
                    "\u0000\u0010\u0001\u0000\u0000\u0000\u0000\u0012\u0001\u0000\u0000\u0000" +
                    "\u0000\u0014\u0001\u0000\u0000\u0000\u0000\u0016\u0001\u0000\u0000\u0000" +
                    "\u0000\u0018\u0001\u0000\u0000\u0000\u0000\u001a\u0001\u0000\u0000\u0000" +
                    "\u0000\u001c\u0001\u0000\u0000\u0000\u0000\u001e\u0001\u0000\u0000\u0000" +
                    "\u0000 \u0001\u0000\u0000\u0000\u0000\"\u0001\u0000\u0000\u0000\u0000" +
                    "$\u0001\u0000\u0000\u0000\u00006\u0001\u0000\u0000\u0000\u00008\u0001" +
                    "\u0000\u0000\u0000\u0000:\u0001\u0000\u0000\u0000\u0000<\u0001\u0000\u0000" +
                    "\u0000\u0001>\u0001\u0000\u0000\u0000\u0002B\u0001\u0000\u0000\u0000\u0003" +
                    "D\u0001\u0000\u0000\u0000\u0004F\u0001\u0000\u0000\u0000\u0004H\u0001" +
                    "\u0000\u0000\u0000\u0005J\u0001\u0000\u0000\u0000\u0005L\u0001\u0000\u0000" +
                    "\u0000\u0005N\u0001\u0000\u0000\u0000\u0005P\u0001\u0000\u0000\u0000\u0005" +
                    "R\u0001\u0000\u0000\u0000\u0006T\u0001\u0000\u0000\u0000\bb\u0001\u0000" +
                    "\u0000\u0000\nj\u0001\u0000\u0000\u0000\fo\u0001\u0000\u0000\u0000\u000e" +
                    "v\u0001\u0000\u0000\u0000\u0010}\u0001\u0000\u0000\u0000\u0012\u0084\u0001" +
                    "\u0000\u0000\u0000\u0014\u008c\u0001\u0000\u0000\u0000\u0016\u0094\u0001" +
                    "\u0000\u0000\u0000\u0018\u00a2\u0001\u0000\u0000\u0000\u001a\u00aa\u0001" +
                    "\u0000\u0000\u0000\u001c\u00b2\u0001\u0000\u0000\u0000\u001e\u00bd\u0001" +
                    "\u0000\u0000\u0000 \u00c5\u0001\u0000\u0000\u0000\"\u00c8\u0001\u0000" +
                    "\u0000\u0000$\u00cd\u0001\u0000\u0000\u0000&\u00d4\u0001\u0000\u0000\u0000" +
                    "(\u00d6\u0001\u0000\u0000\u0000*\u00d9\u0001\u0000\u0000\u0000,\u00dd" +
                    "\u0001\u0000\u0000\u0000.\u00e0\u0001\u0000\u0000\u00000\u00e4\u0001\u0000" +
                    "\u0000\u00002\u010e\u0001\u0000\u0000\u00004\u013b\u0001\u0000\u0000\u0000" +
                    "6\u013d\u0001\u0000\u0000\u00008\u013f\u0001\u0000\u0000\u0000:\u0141" +
                    "\u0001\u0000\u0000\u0000<\u014f\u0001\u0000\u0000\u0000>\u015a\u0001\u0000" +
                    "\u0000\u0000@\u0161\u0001\u0000\u0000\u0000B\u0165\u0001\u0000\u0000\u0000" +
                    "D\u016a\u0001\u0000\u0000\u0000F\u0170\u0001\u0000\u0000\u0000H\u0174" +
                    "\u0001\u0000\u0000\u0000J\u0178\u0001\u0000\u0000\u0000L\u017a\u0001\u0000" +
                    "\u0000\u0000N\u017d\u0001\u0000\u0000\u0000P\u0181\u0001\u0000\u0000\u0000" +
                    "R\u018a\u0001\u0000\u0000\u0000TU\u0005r\u0000\u0000UV\u0005e\u0000\u0000" +
                    "VW\u0005c\u0000\u0000WX\u0005o\u0000\u0000XY\u0005r\u0000\u0000YZ\u0005" +
                    "d\u0000\u0000Z[\u0005-\u0000\u0000[\\\u0005c\u0000\u0000\\]\u0005o\u0000" +
                    "\u0000]^\u0005u\u0000\u0000^_\u0005n\u0000\u0000_`\u0005t\u0000\u0000" +
                    "`a\u0005:\u0000\u0000a\u0007\u0001\u0000\u0000\u0000bc\u0005n\u0000\u0000" +
                    "cd\u0005a\u0000\u0000de\u0005m\u0000\u0000ef\u0005e\u0000\u0000fg\u0005" +
                    ":\u0000\u0000gh\u0001\u0000\u0000\u0000hi\u0006\u0001\u0000\u0000i\t\u0001" +
                    "\u0000\u0000\u0000jk\u0005a\u0000\u0000kl\u0005i\u0000\u0000lm\u0005m" +
                    "\u0000\u0000mn\u0005:\u0000\u0000n\u000b\u0001\u0000\u0000\u0000op\u0005" +
                    "l\u0000\u0000pq\u0005e\u0000\u0000qr\u0005v\u0000\u0000rs\u0005e\u0000" +
                    "\u0000st\u0005l\u0000\u0000tu\u0005:\u0000\u0000u\r\u0001\u0000\u0000" +
                    "\u0000vw\u0005p\u0000\u0000wx\u0005o\u0000\u0000xy\u0005w\u0000\u0000" +
                    "yz\u0005e\u0000\u0000z{\u0005r\u0000\u0000{|\u0005:\u0000\u0000|\u000f" +
                    "\u0001\u0000\u0000\u0000}~\u0005m\u0000\u0000~\u007f\u0005s\u0000\u0000" +
                    "\u007f\u0080\u0005g\u0000\u0000\u0080\u0081\u0005:\u0000\u0000\u0081\u0082" +
                    "\u0001\u0000\u0000\u0000\u0082\u0083\u0006\u0005\u0001\u0000\u0083\u0011" +
                    "\u0001\u0000\u0000\u0000\u0084\u0085\u0005d\u0000\u0000\u0085\u0086\u0005" +
                    "e\u0000\u0000\u0086\u0087\u0005s\u0000\u0000\u0087\u0088\u0005c\u0000" +
                    "\u0000\u0088\u0089\u0005:\u0000\u0000\u0089\u008a\u0001\u0000\u0000\u0000" +
                    "\u008a\u008b\u0006\u0006\u0001\u0000\u008b\u0013\u0001\u0000\u0000\u0000" +
                    "\u008c\u008d\u0005e\u0000\u0000\u008d\u008e\u0005f\u0000\u0000\u008e\u008f" +
                    "\u0005f\u0000\u0000\u008f\u0090\u0005e\u0000\u0000\u0090\u0091\u0005c" +
                    "\u0000\u0000\u0091\u0092\u0005t\u0000\u0000\u0092\u0093\u0005:\u0000\u0000" +
                    "\u0093\u0015\u0001\u0000\u0000\u0000\u0094\u0095\u0005e\u0000\u0000\u0095" +
                    "\u0096\u0005f\u0000\u0000\u0096\u0097\u0005f\u0000\u0000\u0097\u0098\u0005" +
                    "e\u0000\u0000\u0098\u0099\u0005c\u0000\u0000\u0099\u009a\u0005t\u0000" +
                    "\u0000\u009a\u009b\u0005-\u0000\u0000\u009b\u009c\u0005m\u0000\u0000\u009c" +
                    "\u009d\u0005s\u0000\u0000\u009d\u009e\u0005g\u0000\u0000\u009e\u009f\u0005" +
                    ":\u0000\u0000\u009f\u00a0\u0001\u0000\u0000\u0000\u00a0\u00a1\u0006\b" +
                    "\u0002\u0000\u00a1\u0017\u0001\u0000\u0000\u0000\u00a2\u00a3\u0005d\u0000" +
                    "\u0000\u00a3\u00a4\u0005i\u0000\u0000\u00a4\u00a5\u0005c\u0000\u0000\u00a5" +
                    "\u00a6\u0005e\u0000\u0000\u00a6\u00a7\u0005:\u0000\u0000\u00a7\u00a8\u0001" +
                    "\u0000\u0000\u0000\u00a8\u00a9\u0006\t\u0003\u0000\u00a9\u0019\u0001\u0000" +
                    "\u0000\u0000\u00aa\u00ab\u0005t\u0000\u0000\u00ab\u00ac\u0005i\u0000\u0000" +
                    "\u00ac\u00ad\u0005m\u0000\u0000\u00ad\u00ae\u0005e\u0000\u0000\u00ae\u00af" +
                    "\u0005:\u0000\u0000\u00af\u00b0\u0001\u0000\u0000\u0000\u00b0\u00b1\u0006" +
                    "\n\u0003\u0000\u00b1\u001b\u0001\u0000\u0000\u0000\u00b2\u00b3\u0005e" +
                    "\u0000\u0000\u00b3\u00b4\u0005f\u0000\u0000\u00b4\u00b5\u0005f\u0000\u0000" +
                    "\u00b5\u00b6\u0005e\u0000\u0000\u00b6\u00b7\u0005c\u0000\u0000\u00b7\u00b8" +
                    "\u0005t\u0000\u0000\u00b8\u00b9\u0005-\u0000\u0000\u00b9\u00ba\u0005y" +
                    "\u0000\u0000\u00ba\u00bb\u0005x\u0000\u0000\u00bb\u00bc\u0005:\u0000\u0000" +
                    "\u00bc\u001d\u0001\u0000\u0000\u0000\u00bd\u00be\u0005e\u0000\u0000\u00be" +
                    "\u00bf\u0005x\u0000\u0000\u00bf\u00c0\u0005p\u0000\u0000\u00c0\u00c1\u0005" +
                    "r\u0000\u0000\u00c1\u00c2\u0005:\u0000\u0000\u00c2\u00c3\u0001\u0000\u0000" +
                    "\u0000\u00c3\u00c4\u0006\f\u0004\u0000\u00c4\u001f\u0001\u0000\u0000\u0000" +
                    "\u00c5\u00c6\u0005:\u0000\u0000\u00c6!\u0001\u0000\u0000\u0000\u00c7\u00c9" +
                    "\u0007\u0000\u0000\u0000\u00c8\u00c7\u0001\u0000\u0000\u0000\u00c9\u00ca" +
                    "\u0001\u0000\u0000\u0000\u00ca\u00c8\u0001\u0000\u0000\u0000\u00ca\u00cb" +
                    "\u0001\u0000\u0000\u0000\u00cb#\u0001\u0000\u0000\u0000\u00cc\u00ce\u0005" +
                    "-\u0000\u0000\u00cd\u00cc\u0001\u0000\u0000\u0000\u00cd\u00ce\u0001\u0000" +
                    "\u0000\u0000\u00ce\u00d0\u0001\u0000\u0000\u0000\u00cf\u00d1\u000209\u0000" +
                    "\u00d0\u00cf\u0001\u0000\u0000\u0000\u00d1\u00d2\u0001\u0000\u0000\u0000" +
                    "\u00d2\u00d0\u0001\u0000\u0000\u0000\u00d2\u00d3\u0001\u0000\u0000\u0000" +
                    "\u00d3%\u0001\u0000\u0000\u0000\u00d4\u00d5\u0005d\u0000\u0000\u00d5\'" +
                    "\u0001\u0000\u0000\u0000\u00d6\u00d7\u0007\u0001\u0000\u0000\u00d7)\u0001" +
                    "\u0000\u0000\u0000\u00d8\u00da\u000209\u0000\u00d9\u00d8\u0001\u0000\u0000" +
                    "\u0000\u00da\u00db\u0001\u0000\u0000\u0000\u00db\u00d9\u0001\u0000\u0000" +
                    "\u0000\u00db\u00dc\u0001\u0000\u0000\u0000\u00dc+\u0001\u0000\u0000\u0000" +
                    "\u00dd\u00de\u0005$\u0000\u0000\u00de\u00df\u0002AZ\u0000\u00df-\u0001" +
                    "\u0000\u0000\u0000\u00e0\u00e1\u0003*\u0012\u0000\u00e1/\u0001\u0000\u0000" +
                    "\u0000\u00e2\u00e5\u0003*\u0012\u0000\u00e3\u00e5\u0003,\u0013\u0000\u00e4" +
                    "\u00e2\u0001\u0000\u0000\u0000\u00e4\u00e3\u0001\u0000\u0000\u0000\u00e5" +
                    "1\u0001\u0000\u0000\u0000\u00e6\u00e8\u0005-\u0000\u0000\u00e7\u00e6\u0001" +
                    "\u0000\u0000\u0000\u00e7\u00e8\u0001\u0000\u0000\u0000\u00e8\u00e9\u0001" +
                    "\u0000\u0000\u0000\u00e9\u00ea\u00030\u0015\u0000\u00ea\u00f8\u0005+\u0000" +
                    "\u0000\u00eb\u00ed\u00030\u0015\u0000\u00ec\u00eb\u0001\u0000\u0000\u0000" +
                    "\u00ec\u00ed\u0001\u0000\u0000\u0000\u00ed\u00ee\u0001\u0000\u0000\u0000" +
                    "\u00ee\u00ef\u0003&\u0010\u0000\u00ef\u00f3\u00030\u0015\u0000\u00f0\u00f1" +
                    "\u0003(\u0011\u0000\u00f1\u00f2\u00030\u0015\u0000\u00f2\u00f4\u0001\u0000" +
                    "\u0000\u0000\u00f3\u00f0\u0001\u0000\u0000\u0000\u00f3\u00f4\u0001\u0000" +
                    "\u0000\u0000\u00f4\u00f9\u0001\u0000\u0000\u0000\u00f5\u00f6\u0003(\u0011" +
                    "\u0000\u00f6\u00f7\u00030\u0015\u0000\u00f7\u00f9\u0001\u0000\u0000\u0000" +
                    "\u00f8\u00ec\u0001\u0000\u0000\u0000\u00f8\u00f5\u0001\u0000\u0000\u0000" +
                    "\u00f9\u010f\u0001\u0000\u0000\u0000\u00fa\u00fc\u0005-\u0000\u0000\u00fb" +
                    "\u00fa\u0001\u0000\u0000\u0000\u00fb\u00fc\u0001\u0000\u0000\u0000\u00fc" +
                    "\u00fe\u0001\u0000\u0000\u0000\u00fd\u00ff\u00030\u0015\u0000\u00fe\u00fd" +
                    "\u0001\u0000\u0000\u0000\u00fe\u00ff\u0001\u0000\u0000\u0000\u00ff\u0100" +
                    "\u0001\u0000\u0000\u0000\u0100\u0101\u0003&\u0010\u0000\u0101\u0105\u0003" +
                    "0\u0015\u0000\u0102\u0103\u0003(\u0011\u0000\u0103\u0104\u00030\u0015" +
                    "\u0000\u0104\u0106\u0001\u0000\u0000\u0000\u0105\u0102\u0001\u0000\u0000" +
                    "\u0000\u0105\u0106\u0001\u0000\u0000\u0000\u0106\u010f\u0001\u0000\u0000" +
                    "\u0000\u0107\u0108\u0003(\u0011\u0000\u0108\u0109\u00030\u0015\u0000\u0109" +
                    "\u010f\u0001\u0000\u0000\u0000\u010a\u010c\u0005-\u0000\u0000\u010b\u010a" +
                    "\u0001\u0000\u0000\u0000\u010b\u010c\u0001\u0000\u0000\u0000\u010c\u010d" +
                    "\u0001\u0000\u0000\u0000\u010d\u010f\u00030\u0015\u0000\u010e\u00e7\u0001" +
                    "\u0000\u0000\u0000\u010e\u00fb\u0001\u0000\u0000\u0000\u010e\u0107\u0001" +
                    "\u0000\u0000\u0000\u010e\u010b\u0001\u0000\u0000\u0000\u010f3\u0001\u0000" +
                    "\u0000\u0000\u0110\u0112\u0005-\u0000\u0000\u0111\u0110\u0001\u0000\u0000" +
                    "\u0000\u0111\u0112\u0001\u0000\u0000\u0000\u0112\u0113\u0001\u0000\u0000" +
                    "\u0000\u0113\u0114\u0003.\u0014\u0000\u0114\u0122\u0005+\u0000\u0000\u0115" +
                    "\u0117\u0003.\u0014\u0000\u0116\u0115\u0001\u0000\u0000\u0000\u0116\u0117" +
                    "\u0001\u0000\u0000\u0000\u0117\u0118\u0001\u0000\u0000\u0000\u0118\u0119" +
                    "\u0003&\u0010\u0000\u0119\u011d\u0003.\u0014\u0000\u011a\u011b\u0003(" +
                    "\u0011\u0000\u011b\u011c\u0003.\u0014\u0000\u011c\u011e\u0001\u0000\u0000" +
                    "\u0000\u011d\u011a\u0001\u0000\u0000\u0000\u011d\u011e\u0001\u0000\u0000" +
                    "\u0000\u011e\u0123\u0001\u0000\u0000\u0000\u011f\u0120\u0003(\u0011\u0000" +
                    "\u0120\u0121\u0003.\u0014\u0000\u0121\u0123\u0001\u0000\u0000\u0000\u0122" +
                    "\u0116\u0001\u0000\u0000\u0000\u0122\u011f\u0001\u0000\u0000\u0000\u0123" +
                    "\u013c\u0001\u0000\u0000\u0000\u0124\u0126\u0005-\u0000\u0000\u0125\u0124" +
                    "\u0001\u0000\u0000\u0000\u0125\u0126\u0001\u0000\u0000\u0000\u0126\u0128" +
                    "\u0001\u0000\u0000\u0000\u0127\u0129\u0003.\u0014\u0000\u0128\u0127\u0001" +
                    "\u0000\u0000\u0000\u0128\u0129\u0001\u0000\u0000\u0000\u0129\u012a\u0001" +
                    "\u0000\u0000\u0000\u012a\u012b\u0003&\u0010\u0000\u012b\u012f\u0003.\u0014" +
                    "\u0000\u012c\u012d\u0003(\u0011\u0000\u012d\u012e\u0003.\u0014\u0000\u012e" +
                    "\u0130\u0001\u0000\u0000\u0000\u012f\u012c\u0001\u0000\u0000\u0000\u012f" +
                    "\u0130\u0001\u0000\u0000\u0000\u0130\u013c\u0001\u0000\u0000\u0000\u0131" +
                    "\u0133\u0005-\u0000\u0000\u0132\u0131\u0001\u0000\u0000\u0000\u0132\u0133" +
                    "\u0001\u0000\u0000\u0000\u0133\u0134\u0001\u0000\u0000\u0000\u0134\u0135" +
                    "\u0003(\u0011\u0000\u0135\u0136\u0003.\u0014\u0000\u0136\u013c\u0001\u0000" +
                    "\u0000\u0000\u0137\u0139\u0005-\u0000\u0000\u0138\u0137\u0001\u0000\u0000" +
                    "\u0000\u0138\u0139\u0001\u0000\u0000\u0000\u0139\u013a\u0001\u0000\u0000" +
                    "\u0000\u013a\u013c\u0003.\u0014\u0000\u013b\u0111\u0001\u0000\u0000\u0000" +
                    "\u013b\u0125\u0001\u0000\u0000\u0000\u013b\u0132\u0001\u0000\u0000\u0000" +
                    "\u013b\u0138\u0001\u0000\u0000\u0000\u013c5\u0001\u0000\u0000\u0000\u013d" +
                    "\u013e\u00034\u0017\u0000\u013e7\u0001\u0000\u0000\u0000\u013f\u0140\u0003" +
                    "2\u0016\u0000\u01409\u0001\u0000\u0000\u0000\u0141\u0145\u0005#\u0000" +
                    "\u0000\u0142\u0144\b\u0002\u0000\u0000\u0143\u0142\u0001\u0000\u0000\u0000" +
                    "\u0144\u0147\u0001\u0000\u0000\u0000\u0145\u0143\u0001\u0000\u0000\u0000" +
                    "\u0145\u0146\u0001\u0000\u0000\u0000\u0146\u0148\u0001\u0000\u0000\u0000" +
                    "\u0147\u0145\u0001\u0000\u0000\u0000\u0148\u0149\u0005\n\u0000\u0000\u0149" +
                    "\u014a\u0001\u0000\u0000\u0000\u014a\u014b\u0006\u001a\u0005\u0000\u014b" +
                    ";\u0001\u0000\u0000\u0000\u014c\u014e\u0005 \u0000\u0000\u014d\u014c\u0001" +
                    "\u0000\u0000\u0000\u014e\u0151\u0001\u0000\u0000\u0000\u014f\u014d\u0001" +
                    "\u0000\u0000\u0000\u014f\u0150\u0001\u0000\u0000\u0000\u0150\u0153\u0001" +
                    "\u0000\u0000\u0000\u0151\u014f\u0001\u0000\u0000\u0000\u0152\u0154\u0005" +
                    "\r\u0000\u0000\u0153\u0152\u0001\u0000\u0000\u0000\u0153\u0154\u0001\u0000" +
                    "\u0000\u0000\u0154\u0155\u0001\u0000\u0000\u0000\u0155\u0156\u0005\n\u0000" +
                    "\u0000\u0156\u0157\u0001\u0000\u0000\u0000\u0157\u0158\u0006\u001b\u0005" +
                    "\u0000\u0158=\u0001\u0000\u0000\u0000\u0159\u015b\u0007\u0003\u0000\u0000" +
                    "\u015a\u0159\u0001\u0000\u0000\u0000\u015b\u015c\u0001\u0000\u0000\u0000" +
                    "\u015c\u015a\u0001\u0000\u0000\u0000\u015c\u015d\u0001\u0000\u0000\u0000" +
                    "\u015d\u015e\u0001\u0000\u0000\u0000\u015e\u015f\u0006\u001c\u0006\u0000" +
                    "\u015f?\u0001\u0000\u0000\u0000\u0160\u0162\b\u0004\u0000\u0000\u0161" +
                    "\u0160\u0001\u0000\u0000\u0000\u0162\u0163\u0001\u0000\u0000\u0000\u0163" +
                    "\u0161\u0001\u0000\u0000\u0000\u0163\u0164\u0001\u0000\u0000\u0000\u0164" +
                    "A\u0001\u0000\u0000\u0000\u0165\u0166\u0003@\u001d\u0000\u0166\u0167\u0001" +
                    "\u0000\u0000\u0000\u0167\u0168\u0006\u001e\u0006\u0000\u0168C\u0001\u0000" +
                    "\u0000\u0000\u0169\u016b\b\u0004\u0000\u0000\u016a\u0169\u0001\u0000\u0000" +
                    "\u0000\u016b\u016c\u0001\u0000\u0000\u0000\u016c\u016a\u0001\u0000\u0000" +
                    "\u0000\u016c\u016d\u0001\u0000\u0000\u0000\u016d\u016e\u0001\u0000\u0000" +
                    "\u0000\u016e\u016f\u0006\u001f\u0006\u0000\u016fE\u0001\u0000\u0000\u0000" +
                    "\u0170\u0171\u00034\u0017\u0000\u0171\u0172\u0001\u0000\u0000\u0000\u0172" +
                    "\u0173\u0006 \u0006\u0000\u0173G\u0001\u0000\u0000\u0000\u0174\u0175\u0003" +
                    "2\u0016\u0000\u0175\u0176\u0001\u0000\u0000\u0000\u0176\u0177\u0006!\u0006" +
                    "\u0000\u0177I\u0001\u0000\u0000\u0000\u0178\u0179\u0002AZ\u0000\u0179" +
                    "K\u0001\u0000\u0000\u0000\u017a\u017b\u0005:\u0000\u0000\u017bM\u0001" +
                    "\u0000\u0000\u0000\u017c\u017e\u0007\u0005\u0000\u0000\u017d\u017c\u0001" +
                    "\u0000\u0000\u0000\u017e\u017f\u0001\u0000\u0000\u0000\u017f\u017d\u0001" +
                    "\u0000\u0000\u0000\u017f\u0180\u0001\u0000\u0000\u0000\u0180O\u0001\u0000" +
                    "\u0000\u0000\u0181\u0183\u0007\u0006\u0000\u0000\u0182\u0184\b\u0004\u0000" +
                    "\u0000\u0183\u0182\u0001\u0000\u0000\u0000\u0184\u0185\u0001\u0000\u0000" +
                    "\u0000\u0185\u0183\u0001\u0000\u0000\u0000\u0185\u0186\u0001\u0000\u0000" +
                    "\u0000\u0186Q\u0001\u0000\u0000\u0000\u0187\u0189\u0005\r\u0000\u0000" +
                    "\u0188\u0187\u0001\u0000\u0000\u0000\u0189\u018c\u0001\u0000\u0000\u0000" +
                    "\u018a\u0188\u0001\u0000\u0000\u0000\u018a\u018b\u0001\u0000\u0000\u0000" +
                    "\u018b\u018d\u0001\u0000\u0000\u0000\u018c\u018a\u0001\u0000\u0000\u0000" +
                    "\u018d\u018e\u0005\n\u0000\u0000\u018e\u018f\u0001\u0000\u0000\u0000\u018f" +
                    "\u0190\u0006&\u0006\u0000\u0190\u0191\u0006&\u0005\u0000\u0191S\u0001" +
                    "\u0000\u0000\u0000\'\u0000\u0001\u0002\u0003\u0004\u0005\u00ca\u00cd\u00d2" +
                    "\u00db\u00e4\u00e7\u00ec\u00f3\u00f8\u00fb\u00fe\u0105\u010b\u010e\u0111" +
                    "\u0116\u011d\u0122\u0125\u0128\u012f\u0132\u0138\u013b\u0145\u014f\u0153" +
                    "\u015c\u0163\u016c\u017f\u0185\u018a\u0007\u0005\u0001\u0000\u0005\u0002" +
                    "\u0000\u0005\u0003\u0000\u0005\u0004\u0000\u0005\u0005\u0000\u0006\u0000" +
                    "\u0000\u0004\u0000\u0000";

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'name:'", "'aim:'", "'level:'", "'power:'",
                "'msg:'", "'desc:'", "'effect:'", "'effect-msg:'", "'dice:'", "'time:'",
                "'effect-yx:'", "'expr:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "NAME", "AIM", "LEVEL", "POWER", "MSG", "DESC",
                "EFFECT", "EFFECT_MESSAGE", "DICE", "TIME", "EFFECT_YX", "EXPR", "COLON",
                "UCASE", "INTEGER", "SIMPLE_DICE_STRING", "COMPLEX_DICE_STRING", "COMMENT",
                "EOL", "NAME_STRING", "DESC_STRING", "FREE_TEXT", "DICE_SIMPLE_VALUE",
                "DICE_COMPLEX_VALUE", "EXPR_CHAR", "EXPR_COLON", "EXPR_UCASE", "EXPR_OP",
                "EXPR_EOL"
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


    public ActivationsLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "ActivationsLexer.g4";
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

    private static String[] makeRuleNames() {
        return new String[]{
                "RECORD_COUNT", "NAME", "AIM", "LEVEL", "POWER", "MSG", "DESC", "EFFECT",
                "EFFECT_MESSAGE", "DICE", "TIME", "EFFECT_YX", "EXPR", "COLON", "UCASE",
                "INTEGER", "DICE_D", "DICE_M", "DICE_INTEGER", "DICE_DOLLAR_LETTER",
                "DICE_SIMPLE_NUMBER", "DICE_ANY_NUMBER", "COMPLEX_DICE_STRING_BODY",
                "SIMPLE_DICE_STRING_BODY", "SIMPLE_DICE_STRING", "COMPLEX_DICE_STRING",
                "COMMENT", "EOL", "NAME_STRING", "ACTIVATION_FREE_STRING_FRAGMENT", "DESC_STRING",
                "FREE_TEXT", "DICE_SIMPLE_VALUE", "DICE_COMPLEX_VALUE", "EXPR_CHAR",
                "EXPR_COLON", "EXPR_UCASE", "EXPR_OP", "EXPR_EOL"
        };
    }
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}