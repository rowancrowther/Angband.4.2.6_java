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
// Generated from ChestTrapGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.chesttrap;

import uk.co.jackoftrades.backend.parser.chesttrap.ChestTrapParseRecord;
import uk.co.jackoftrades.backend.parser.grammars.EffectParseRecord;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ChestTrapGrammar extends Parser {
    public static final int
            RECORD_COUNT = 1, NAME = 2, CODE = 3, LEVEL = 4, DESTROY = 5, MAGIC = 6, MSG = 7, MSG_DEATH = 8,
            COMMENT = 9, EOL = 10, EFFECT = 11, EFFECT_MESSAGE = 12, DICE = 13, TIME = 14, EFFECT_YX = 15,
            EXPR = 16, COLON = 17, UCASE = 18, INTEGER = 19, SIMPLE_DICE_STRING = 20, COMPLEX_DICE_STRING = 21,
            STRING = 22, ROL_EOL = 23, FREE_TEXT = 24, DICE_SIMPLE_VALUE = 25, DICE_COMPLEX_VALUE = 26,
            EXPR_CHAR = 27, EXPR_COLON = 28, EXPR_UCASE = 29, EXPR_OP = 30, EXPR_EOL = 31;
    public static final int
            RULE_recordCount = 0, RULE_name = 1, RULE_code = 2, RULE_level = 3, RULE_destroy = 4,
            RULE_magic = 5, RULE_msg = 6, RULE_msgDeath = 7, RULE_chestTrap = 8, RULE_file = 9,
            RULE_effect = 10, RULE_time = 11, RULE_effectYX = 12, RULE_dice = 13,
            RULE_expr = 14, RULE_effectMsg = 15, RULE_effectBlock = 16;
    public static final String[] ruleNames = makeRuleNames();
    /**
     * @deprecated Use {@link #VOCABULARY} instead.
     */
    @Deprecated
    public static final String[] tokenNames;
    public static final String _serializedATN =
            "\u0004\u0001\u001f\u00b5\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001" +
                    "\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004" +
                    "\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007" +
                    "\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b" +
                    "\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007" +
                    "\u000f\u0002\u0010\u0007\u0010\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b" +
                    "\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0005\bY\b\b\n\b\f\b\\\t\b\u0001\t\u0001\t\u0001\t" +
                    "\u0001\t\u0001\t\u0004\tc\b\t\u000b\t\f\td\u0001\t\u0001\t\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0003\nu\b\n\u0003\nw\b\n\u0003\ny\b\n\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001" +
                    "\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0004\r\u008b\b\r\u000b" +
                    "\r\f\r\u008c\u0001\r\u0001\r\u0003\r\u0091\b\r\u0001\u000e\u0001\u000e" +
                    "\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e" +
                    "\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010" +
                    "\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010" +
                    "\u0003\u0010\u00a7\b\u0010\u0003\u0010\u00a9\b\u0010\u0001\u0010\u0001" +
                    "\u0010\u0001\u0010\u0003\u0010\u00ae\b\u0010\u0001\u0010\u0001\u0010\u0001" +
                    "\u0010\u0003\u0010\u00b3\b\u0010\u0001\u0010\u0000\u0000\u0011\u0000\u0002" +
                    "\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e" +
                    " \u0000\u0000\u00b3\u0000\"\u0001\u0000\u0000\u0000\u0002&\u0001\u0000" +
                    "\u0000\u0000\u0004*\u0001\u0000\u0000\u0000\u0006.\u0001\u0000\u0000\u0000" +
                    "\b2\u0001\u0000\u0000\u0000\n6\u0001\u0000\u0000\u0000\f:\u0001\u0000" +
                    "\u0000\u0000\u000e>\u0001\u0000\u0000\u0000\u0010B\u0001\u0000\u0000\u0000" +
                    "\u0012]\u0001\u0000\u0000\u0000\u0014h\u0001\u0000\u0000\u0000\u0016z" +
                    "\u0001\u0000\u0000\u0000\u0018~\u0001\u0000\u0000\u0000\u001a\u0084\u0001" +
                    "\u0000\u0000\u0000\u001c\u0092\u0001\u0000\u0000\u0000\u001e\u009a\u0001" +
                    "\u0000\u0000\u0000 \u009e\u0001\u0000\u0000\u0000\"#\u0005\u0001\u0000" +
                    "\u0000#$\u0005\u0013\u0000\u0000$%\u0006\u0000\uffff\uffff\u0000%\u0001" +
                    "\u0001\u0000\u0000\u0000&\'\u0005\u0002\u0000\u0000\'(\u0005\u0016\u0000" +
                    "\u0000()\u0006\u0001\uffff\uffff\u0000)\u0003\u0001\u0000\u0000\u0000" +
                    "*+\u0005\u0003\u0000\u0000+,\u0005\u0016\u0000\u0000,-\u0006\u0002\uffff" +
                    "\uffff\u0000-\u0005\u0001\u0000\u0000\u0000./\u0005\u0004\u0000\u0000" +
                    "/0\u0005\u0013\u0000\u000001\u0006\u0003\uffff\uffff\u00001\u0007\u0001" +
                    "\u0000\u0000\u000023\u0005\u0005\u0000\u000034\u0005\u0013\u0000\u0000" +
                    "45\u0006\u0004\uffff\uffff\u00005\t\u0001\u0000\u0000\u000067\u0005\u0006" +
                    "\u0000\u000078\u0005\u0013\u0000\u000089\u0006\u0005\uffff\uffff\u0000" +
                    "9\u000b\u0001\u0000\u0000\u0000:;\u0005\u0007\u0000\u0000;<\u0005\u0016" +
                    "\u0000\u0000<=\u0006\u0006\uffff\uffff\u0000=\r\u0001\u0000\u0000\u0000" +
                    ">?\u0005\b\u0000\u0000?@\u0005\u0016\u0000\u0000@A\u0006\u0007\uffff\uffff" +
                    "\u0000A\u000f\u0001\u0000\u0000\u0000BC\u0003\u0002\u0001\u0000CD\u0006" +
                    "\b\uffff\uffff\u0000DE\u0003\u0004\u0002\u0000EZ\u0006\b\uffff\uffff\u0000" +
                    "FG\u0003\u0006\u0003\u0000GH\u0006\b\uffff\uffff\u0000HY\u0001\u0000\u0000" +
                    "\u0000IJ\u0003 \u0010\u0000JK\u0006\b\uffff\uffff\u0000KY\u0001\u0000" +
                    "\u0000\u0000LM\u0003\b\u0004\u0000MN\u0006\b\uffff\uffff\u0000NY\u0001" +
                    "\u0000\u0000\u0000OP\u0003\n\u0005\u0000PQ\u0006\b\uffff\uffff\u0000Q" +
                    "Y\u0001\u0000\u0000\u0000RS\u0003\f\u0006\u0000ST\u0006\b\uffff\uffff" +
                    "\u0000TY\u0001\u0000\u0000\u0000UV\u0003\u000e\u0007\u0000VW\u0006\b\uffff" +
                    "\uffff\u0000WY\u0001\u0000\u0000\u0000XF\u0001\u0000\u0000\u0000XI\u0001" +
                    "\u0000\u0000\u0000XL\u0001\u0000\u0000\u0000XO\u0001\u0000\u0000\u0000" +
                    "XR\u0001\u0000\u0000\u0000XU\u0001\u0000\u0000\u0000Y\\\u0001\u0000\u0000" +
                    "\u0000ZX\u0001\u0000\u0000\u0000Z[\u0001\u0000\u0000\u0000[\u0011\u0001" +
                    "\u0000\u0000\u0000\\Z\u0001\u0000\u0000\u0000]^\u0003\u0000\u0000\u0000" +
                    "^b\u0006\t\uffff\uffff\u0000_`\u0003\u0010\b\u0000`a\u0006\t\uffff\uffff" +
                    "\u0000ac\u0001\u0000\u0000\u0000b_\u0001\u0000\u0000\u0000cd\u0001\u0000" +
                    "\u0000\u0000db\u0001\u0000\u0000\u0000de\u0001\u0000\u0000\u0000ef\u0001" +
                    "\u0000\u0000\u0000fg\u0005\u0000\u0000\u0001g\u0013\u0001\u0000\u0000" +
                    "\u0000hi\u0005\u000b\u0000\u0000ij\u0005\u0012\u0000\u0000jx\u0006\n\uffff" +
                    "\uffff\u0000kl\u0005\u0011\u0000\u0000lm\u0005\u0012\u0000\u0000mv\u0006" +
                    "\n\uffff\uffff\u0000no\u0005\u0011\u0000\u0000op\u0005\u0013\u0000\u0000" +
                    "pt\u0006\n\uffff\uffff\u0000qr\u0005\u0011\u0000\u0000rs\u0005\u0013\u0000" +
                    "\u0000su\u0006\n\uffff\uffff\u0000tq\u0001\u0000\u0000\u0000tu\u0001\u0000" +
                    "\u0000\u0000uw\u0001\u0000\u0000\u0000vn\u0001\u0000\u0000\u0000vw\u0001" +
                    "\u0000\u0000\u0000wy\u0001\u0000\u0000\u0000xk\u0001\u0000\u0000\u0000" +
                    "xy\u0001\u0000\u0000\u0000y\u0015\u0001\u0000\u0000\u0000z{\u0005\u000e" +
                    "\u0000\u0000{|\u0005\u0019\u0000\u0000|}\u0006\u000b\uffff\uffff\u0000" +
                    "}\u0017\u0001\u0000\u0000\u0000~\u007f\u0005\u000f\u0000\u0000\u007f\u0080" +
                    "\u0005\u0013\u0000\u0000\u0080\u0081\u0005\u0011\u0000\u0000\u0081\u0082" +
                    "\u0005\u0013\u0000\u0000\u0082\u0083\u0006\f\uffff\uffff\u0000\u0083\u0019" +
                    "\u0001\u0000\u0000\u0000\u0084\u0090\u0005\r\u0000\u0000\u0085\u0086\u0005" +
                    "\u001a\u0000\u0000\u0086\u008a\u0006\r\uffff\uffff\u0000\u0087\u0088\u0003" +
                    "\u001c\u000e\u0000\u0088\u0089\u0006\r\uffff\uffff\u0000\u0089\u008b\u0001" +
                    "\u0000\u0000\u0000\u008a\u0087\u0001\u0000\u0000\u0000\u008b\u008c\u0001" +
                    "\u0000\u0000\u0000\u008c\u008a\u0001\u0000\u0000\u0000\u008c\u008d\u0001" +
                    "\u0000\u0000\u0000\u008d\u0091\u0001\u0000\u0000\u0000\u008e\u008f\u0005" +
                    "\u0019\u0000\u0000\u008f\u0091\u0006\r\uffff\uffff\u0000\u0090\u0085\u0001" +
                    "\u0000\u0000\u0000\u0090\u008e\u0001\u0000\u0000\u0000\u0091\u001b\u0001" +
                    "\u0000\u0000\u0000\u0092\u0093\u0005\u0010\u0000\u0000\u0093\u0094\u0005" +
                    "\u001b\u0000\u0000\u0094\u0095\u0005\u001c\u0000\u0000\u0095\u0096\u0005" +
                    "\u001d\u0000\u0000\u0096\u0097\u0005\u001c\u0000\u0000\u0097\u0098\u0005" +
                    "\u001e\u0000\u0000\u0098\u0099\u0006\u000e\uffff\uffff\u0000\u0099\u001d" +
                    "\u0001\u0000\u0000\u0000\u009a\u009b\u0005\f\u0000\u0000\u009b\u009c\u0005" +
                    "\u0018\u0000\u0000\u009c\u009d\u0006\u000f\uffff\uffff\u0000\u009d\u001f" +
                    "\u0001\u0000\u0000\u0000\u009e\u009f\u0003\u0014\n\u0000\u009f\u00a8\u0006" +
                    "\u0010\uffff\uffff\u0000\u00a0\u00a1\u0003\u0018\f\u0000\u00a1\u00a2\u0006" +
                    "\u0010\uffff\uffff\u0000\u00a2\u00a9\u0001\u0000\u0000\u0000\u00a3\u00a4" +
                    "\u0003\u001a\r\u0000\u00a4\u00a5\u0006\u0010\uffff\uffff\u0000\u00a5\u00a7" +
                    "\u0001\u0000\u0000\u0000\u00a6\u00a3\u0001\u0000\u0000\u0000\u00a6\u00a7" +
                    "\u0001\u0000\u0000\u0000\u00a7\u00a9\u0001\u0000\u0000\u0000\u00a8\u00a0" +
                    "\u0001\u0000\u0000\u0000\u00a8\u00a6\u0001\u0000\u0000\u0000\u00a9\u00ad" +
                    "\u0001\u0000\u0000\u0000\u00aa\u00ab\u0003\u0016\u000b\u0000\u00ab\u00ac" +
                    "\u0006\u0010\uffff\uffff\u0000\u00ac\u00ae\u0001\u0000\u0000\u0000\u00ad" +
                    "\u00aa\u0001\u0000\u0000\u0000\u00ad\u00ae\u0001\u0000\u0000\u0000\u00ae" +
                    "\u00b2\u0001\u0000\u0000\u0000\u00af\u00b0\u0003\u001e\u000f\u0000\u00b0" +
                    "\u00b1\u0006\u0010\uffff\uffff\u0000\u00b1\u00b3\u0001\u0000\u0000\u0000" +
                    "\u00b2\u00af\u0001\u0000\u0000\u0000\u00b2\u00b3\u0001\u0000\u0000\u0000" +
                    "\u00b3!\u0001\u0000\u0000\u0000\fXZdtvx\u008c\u0090\u00a6\u00a8\u00ad" +
                    "\u00b2";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());
    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    private static final String[] _LITERAL_NAMES = makeLiteralNames();
    private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
    public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

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

    public ChestTrapGrammar(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    private static String[] makeRuleNames() {
        return new String[]{
                "recordCount", "name", "code", "level", "destroy", "magic", "msg", "msgDeath",
                "chestTrap", "file", "effect", "time", "effectYX", "dice", "expr", "effectMsg",
                "effectBlock"
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
        return "ChestTrapGrammar.g4";
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
    public ATN getATN() {
        return _ATN;
    }

    public final RecordCountContext recordCount() throws RecognitionException {
        RecordCountContext _localctx = new RecordCountContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_recordCount);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(34);
                match(RECORD_COUNT);
                setState(35);
                ((RecordCountContext) _localctx).c = match(INTEGER);
                ((RecordCountContext) _localctx).count = ((RecordCountContext) _localctx).c.getText();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(38);
                match(NAME);
                setState(39);
                ((NameContext) _localctx).n = match(STRING);

                ((NameContext) _localctx).nameStr = ((NameContext) _localctx).n.getText();
                ((NameContext) _localctx).line = _localctx.start.getLine();

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final CodeContext code() throws RecognitionException {
        CodeContext _localctx = new CodeContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_code);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(42);
                match(CODE);
                setState(43);
                ((CodeContext) _localctx).c = match(STRING);
                ((CodeContext) _localctx).codeStr = ((CodeContext) _localctx).c.getText();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final LevelContext level() throws RecognitionException {
        LevelContext _localctx = new LevelContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_level);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(46);
                match(LEVEL);
                setState(47);
                ((LevelContext) _localctx).l = match(INTEGER);
                ((LevelContext) _localctx).levelStr = ((LevelContext) _localctx).l.getText();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final DestroyContext destroy() throws RecognitionException {
        DestroyContext _localctx = new DestroyContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_destroy);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(50);
                match(DESTROY);
                setState(51);
                ((DestroyContext) _localctx).d = match(INTEGER);
                ((DestroyContext) _localctx).destroyStr = ((DestroyContext) _localctx).d.getText();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final MagicContext magic() throws RecognitionException {
        MagicContext _localctx = new MagicContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_magic);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(54);
                match(MAGIC);
                setState(55);
                ((MagicContext) _localctx).m = match(INTEGER);
                ((MagicContext) _localctx).magicStr = ((MagicContext) _localctx).m.getText();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final MsgContext msg() throws RecognitionException {
        MsgContext _localctx = new MsgContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_msg);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(58);
                match(MSG);
                setState(59);
                ((MsgContext) _localctx).m = match(STRING);
                ((MsgContext) _localctx).msgStr = ((MsgContext) _localctx).m.getText();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final MsgDeathContext msgDeath() throws RecognitionException {
        MsgDeathContext _localctx = new MsgDeathContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_msgDeath);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(62);
                match(MSG_DEATH);
                setState(63);
                ((MsgDeathContext) _localctx).m = match(STRING);
                ((MsgDeathContext) _localctx).msgDeathStr = ((MsgDeathContext) _localctx).m.getText();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final ChestTrapContext chestTrap() throws RecognitionException {
        ChestTrapContext _localctx = new ChestTrapContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_chestTrap);

        String nameInit = "";
        String codeInit = "";
        String levelInit = "";
        List<EffectParseRecord> effectInit = new ArrayList<>();
        String destroyInit = "";
        String magicInit = "";
        String msgInit = "";
        String msgDeathInit = "";
        int lineInit = 0;

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(66);
                ((ChestTrapContext) _localctx).name = name();
                nameInit = ((ChestTrapContext) _localctx).name.nameStr;
                lineInit = (((ChestTrapContext) _localctx).name != null ? (((ChestTrapContext) _localctx).name.start) : null).getLine();
                setState(68);
                ((ChestTrapContext) _localctx).code = code();
                codeInit = ((ChestTrapContext) _localctx).code.codeStr;
                setState(90);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 2544L) != 0)) {
                    {
                        setState(88);
                        _errHandler.sync(this);
                        switch (_input.LA(1)) {
                            case LEVEL: {
                                setState(70);
                                ((ChestTrapContext) _localctx).level = level();
                                levelInit = ((ChestTrapContext) _localctx).level.levelStr;
                            }
                            break;
                            case EFFECT: {
                                setState(73);
                                ((ChestTrapContext) _localctx).effectBlock = effectBlock();
                                effectInit.add(new EffectParseRecord(((ChestTrapContext) _localctx).effectBlock.typeInit,
                                        ((ChestTrapContext) _localctx).effectBlock.subtypeWrapperInit, ((ChestTrapContext) _localctx).effectBlock.radius, ((ChestTrapContext) _localctx).effectBlock.other,
                                        ((ChestTrapContext) _localctx).effectBlock.diceString, ((ChestTrapContext) _localctx).effectBlock.yVal, ((ChestTrapContext) _localctx).effectBlock.xVal,
                                        ((ChestTrapContext) _localctx).effectBlock.expressionChars, ((ChestTrapContext) _localctx).effectBlock.expressionBase,
                                        ((ChestTrapContext) _localctx).effectBlock.expressionOperation, ((ChestTrapContext) _localctx).effectBlock.timeDiceString,
                                        ((ChestTrapContext) _localctx).effectBlock.effectMessage, (((ChestTrapContext) _localctx).effectBlock != null ? (((ChestTrapContext) _localctx).effectBlock.start) : null).getLine()));
                            }
                            break;
                            case DESTROY: {
                                setState(76);
                                ((ChestTrapContext) _localctx).destroy = destroy();
                                destroyInit = ((ChestTrapContext) _localctx).destroy.destroyStr;
                            }
                            break;
                            case MAGIC: {
                                setState(79);
                                ((ChestTrapContext) _localctx).magic = magic();
                                magicInit = ((ChestTrapContext) _localctx).magic.magicStr;
                            }
                            break;
                            case MSG: {
                                setState(82);
                                ((ChestTrapContext) _localctx).msg = msg();
                                msgInit = ((ChestTrapContext) _localctx).msg.msgStr;
                            }
                            break;
                            case MSG_DEATH: {
                                setState(85);
                                ((ChestTrapContext) _localctx).msgDeath = msgDeath();
                                msgDeathInit = ((ChestTrapContext) _localctx).msgDeath.msgDeathStr;
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                    }
                    setState(92);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
            }
            _ctx.stop = _input.LT(-1);

            ((ChestTrapContext) _localctx).trap = new ChestTrapParseRecord(nameInit, codeInit, levelInit,
                    effectInit, destroyInit, magicInit, msgInit, msgDeathInit,
                    lineInit);

        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_file);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(93);
                ((FileContext) _localctx).recordCount = recordCount();
                ((FileContext) _localctx).declaredRecordCount = ((FileContext) _localctx).recordCount.count;
                ((FileContext) _localctx).chestTraps = new ArrayList<>();
                setState(98);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(95);
                            ((FileContext) _localctx).chestTrap = chestTrap();
                            _localctx.chestTraps.add(((FileContext) _localctx).chestTrap.trap);
                        }
                    }
                    setState(100);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(102);
                match(EOF);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final EffectContext effect() throws RecognitionException {
        EffectContext _localctx = new EffectContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_effect);

        ((EffectContext) _localctx).wrapper = "";
        ((EffectContext) _localctx).radius = "";
        ((EffectContext) _localctx).other = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(104);
                match(EFFECT);
                setState(105);
                ((EffectContext) _localctx).t = match(UCASE);

                ((EffectContext) _localctx).type = ((EffectContext) _localctx).t.getText();

                setState(120);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COLON) {
                    {
                        setState(107);
                        match(COLON);
                        setState(108);
                        ((EffectContext) _localctx).st = match(UCASE);

                        ((EffectContext) _localctx).wrapper = ((EffectContext) _localctx).st.getText().toUpperCase();

                        setState(118);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == COLON) {
                            {
                                setState(110);
                                match(COLON);
                                setState(111);
                                ((EffectContext) _localctx).rad = match(INTEGER);

                                ((EffectContext) _localctx).radius = ((EffectContext) _localctx).rad.getText();

                                setState(116);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                                if (_la == COLON) {
                                    {
                                        setState(113);
                                        match(COLON);
                                        setState(114);
                                        ((EffectContext) _localctx).oth = match(INTEGER);

                                        ((EffectContext) _localctx).other = ((EffectContext) _localctx).oth.getText();

                                    }
                                }

                            }
                        }

                    }
                }

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final TimeContext time() throws RecognitionException {
        TimeContext _localctx = new TimeContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_time);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(122);
                match(TIME);
                setState(123);
                ((TimeContext) _localctx).DICE_SIMPLE_VALUE = match(DICE_SIMPLE_VALUE);

                ((TimeContext) _localctx).timeStr = ((TimeContext) _localctx).DICE_SIMPLE_VALUE.getText();

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final EffectYXContext effectYX() throws RecognitionException {
        EffectYXContext _localctx = new EffectYXContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_effectYX);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(126);
                match(EFFECT_YX);
                setState(127);
                ((EffectYXContext) _localctx).yVal = match(INTEGER);
                setState(128);
                match(COLON);
                setState(129);
                ((EffectYXContext) _localctx).xVal = match(INTEGER);

                ((EffectYXContext) _localctx).y = ((EffectYXContext) _localctx).yVal.getText();
                ((EffectYXContext) _localctx).x = ((EffectYXContext) _localctx).xVal.getText();

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final DiceContext dice() throws RecognitionException {
        DiceContext _localctx = new DiceContext(_ctx, getState());
        enterRule(_localctx, 26, RULE_dice);

        String charHolder = "";
        String baseHolder = "";
        String operHolder = "";
        ((DiceContext) _localctx).diceString = "";
        ((DiceContext) _localctx).exprChar = "";
        ((DiceContext) _localctx).baseName = "";
        ((DiceContext) _localctx).operation = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(132);
                match(DICE);
                setState(144);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case DICE_COMPLEX_VALUE: {
                        {
                            setState(133);
                            ((DiceContext) _localctx).val = match(DICE_COMPLEX_VALUE);

                            ((DiceContext) _localctx).diceString = ((DiceContext) _localctx).val.getText();

                            setState(138);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            do {
                                {
                                    {
                                        setState(135);
                                        ((DiceContext) _localctx).expr = expr();

                                        if (charHolder.isEmpty()) {
                                            charHolder = ((DiceContext) _localctx).expr.exprChar;
                                            baseHolder = ((DiceContext) _localctx).expr.baseName;
                                            operHolder = ((DiceContext) _localctx).expr.operation;
                                        } else {
                                            charHolder = charHolder + "^" + ((DiceContext) _localctx).expr.exprChar;
                                            baseHolder = baseHolder + "^" + ((DiceContext) _localctx).expr.baseName;
                                            operHolder = operHolder + "^" + ((DiceContext) _localctx).expr.operation;
                                        }

                                    }
                                }
                                setState(140);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                            } while (_la == EXPR);
                        }
                    }
                    break;
                    case DICE_SIMPLE_VALUE: {
                        setState(142);
                        ((DiceContext) _localctx).val = match(DICE_SIMPLE_VALUE);

                        ((DiceContext) _localctx).diceString = ((DiceContext) _localctx).val.getText();

                    }
                    break;
                    default:
                        throw new NoViableAltException(this);
                }
            }
            _ctx.stop = _input.LT(-1);

            ((DiceContext) _localctx).exprChar = charHolder;
            ((DiceContext) _localctx).baseName = baseHolder;
            ((DiceContext) _localctx).operation = operHolder;

        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final ExprContext expr() throws RecognitionException {
        ExprContext _localctx = new ExprContext(_ctx, getState());
        enterRule(_localctx, 28, RULE_expr);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(146);
                match(EXPR);
                setState(147);
                ((ExprContext) _localctx).ch = match(EXPR_CHAR);
                setState(148);
                match(EXPR_COLON);
                setState(149);
                ((ExprContext) _localctx).base = match(EXPR_UCASE);
                setState(150);
                match(EXPR_COLON);
                setState(151);
                ((ExprContext) _localctx).op = match(EXPR_OP);

                ((ExprContext) _localctx).exprChar = ((ExprContext) _localctx).ch.getText();
                ((ExprContext) _localctx).baseName = ((ExprContext) _localctx).base.getText();
                ((ExprContext) _localctx).operation = ((ExprContext) _localctx).op.getText();

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final EffectMsgContext effectMsg() throws RecognitionException {
        EffectMsgContext _localctx = new EffectMsgContext(_ctx, getState());
        enterRule(_localctx, 30, RULE_effectMsg);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(154);
                match(EFFECT_MESSAGE);
                setState(155);
                ((EffectMsgContext) _localctx).FREE_TEXT = match(FREE_TEXT);
                ((EffectMsgContext) _localctx).message = ((EffectMsgContext) _localctx).FREE_TEXT.getText();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final EffectBlockContext effectBlock() throws RecognitionException {
        EffectBlockContext _localctx = new EffectBlockContext(_ctx, getState());
        enterRule(_localctx, 32, RULE_effectBlock);

        String expressionString = "";
        String baseString = "";
        String opString = "";
        ((EffectBlockContext) _localctx).diceString = "";
        ((EffectBlockContext) _localctx).timeDiceString = "";
        ((EffectBlockContext) _localctx).yVal = "";
        ((EffectBlockContext) _localctx).xVal = "";
        ((EffectBlockContext) _localctx).effectMessage = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(158);
                ((EffectBlockContext) _localctx).effect = effect();

                ((EffectBlockContext) _localctx).lineNo = _localctx.start.getLine();
                ((EffectBlockContext) _localctx).typeInit = ((EffectBlockContext) _localctx).effect.type;
                ((EffectBlockContext) _localctx).subtypeWrapperInit = ((EffectBlockContext) _localctx).effect.wrapper;
                ((EffectBlockContext) _localctx).radius = ((EffectBlockContext) _localctx).effect.radius;
                ((EffectBlockContext) _localctx).other = ((EffectBlockContext) _localctx).effect.other;

                setState(168);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case EFFECT_YX: {
                        {
                            setState(160);
                            ((EffectBlockContext) _localctx).effectYX = effectYX();

                            ((EffectBlockContext) _localctx).yVal = ((EffectBlockContext) _localctx).effectYX.y;
                            ((EffectBlockContext) _localctx).xVal = ((EffectBlockContext) _localctx).effectYX.x;

                        }
                    }
                    break;
                    case EOF:
                    case NAME:
                    case LEVEL:
                    case DESTROY:
                    case MAGIC:
                    case MSG:
                    case MSG_DEATH:
                    case EFFECT:
                    case EFFECT_MESSAGE:
                    case DICE:
                    case TIME: {
                        {
                            setState(166);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            if (_la == DICE) {
                                {
                                    setState(163);
                                    ((EffectBlockContext) _localctx).dice = dice();

                                    ((EffectBlockContext) _localctx).diceString = ((EffectBlockContext) _localctx).dice.diceString;
                                    expressionString = ((EffectBlockContext) _localctx).dice.exprChar;
                                    baseString = ((EffectBlockContext) _localctx).dice.baseName;
                                    opString = ((EffectBlockContext) _localctx).dice.operation;

                                }
                            }

                        }
                    }
                    break;
                    default:
                        throw new NoViableAltException(this);
                }
                setState(173);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == TIME) {
                    {
                        setState(170);
                        ((EffectBlockContext) _localctx).time = time();

                        ((EffectBlockContext) _localctx).timeDiceString = ((EffectBlockContext) _localctx).time.timeStr;

                    }
                }

                setState(178);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == EFFECT_MESSAGE) {
                    {
                        setState(175);
                        ((EffectBlockContext) _localctx).effectMsg = effectMsg();
                        ((EffectBlockContext) _localctx).effectMessage = ((EffectBlockContext) _localctx).effectMsg.message;
                    }
                }

            }
            _ctx.stop = _input.LT(-1);

            ((EffectBlockContext) _localctx).expressionChars = expressionString;
            ((EffectBlockContext) _localctx).expressionBase = baseString;
            ((EffectBlockContext) _localctx).expressionOperation = opString;

        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RecordCountContext extends ParserRuleContext {
        public String count;
        public Token c;

        public RecordCountContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode RECORD_COUNT() {
            return getToken(ChestTrapGrammar.RECORD_COUNT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(ChestTrapGrammar.INTEGER, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_recordCount;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener)
                ((ChestTrapGrammarListener) listener).enterRecordCount(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener)
                ((ChestTrapGrammarListener) listener).exitRecordCount(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ChestTrapGrammarVisitor)
                return ((ChestTrapGrammarVisitor<? extends T>) visitor).visitRecordCount(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class NameContext extends ParserRuleContext {
        public String nameStr;
        public int line;
        public Token n;

        public NameContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode NAME() {
            return getToken(ChestTrapGrammar.NAME, 0);
        }

        public TerminalNode STRING() {
            return getToken(ChestTrapGrammar.STRING, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_name;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ChestTrapGrammarVisitor)
                return ((ChestTrapGrammarVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class CodeContext extends ParserRuleContext {
        public String codeStr;
        public Token c;

        public CodeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode CODE() {
            return getToken(ChestTrapGrammar.CODE, 0);
        }

        public TerminalNode STRING() {
            return getToken(ChestTrapGrammar.STRING, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_code;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).enterCode(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).exitCode(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ChestTrapGrammarVisitor)
                return ((ChestTrapGrammarVisitor<? extends T>) visitor).visitCode(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class LevelContext extends ParserRuleContext {
        public String levelStr;
        public Token l;

        public LevelContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode LEVEL() {
            return getToken(ChestTrapGrammar.LEVEL, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(ChestTrapGrammar.INTEGER, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_level;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).enterLevel(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).exitLevel(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ChestTrapGrammarVisitor)
                return ((ChestTrapGrammarVisitor<? extends T>) visitor).visitLevel(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class DestroyContext extends ParserRuleContext {
        public String destroyStr;
        public Token d;

        public DestroyContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode DESTROY() {
            return getToken(ChestTrapGrammar.DESTROY, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(ChestTrapGrammar.INTEGER, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_destroy;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).enterDestroy(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).exitDestroy(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ChestTrapGrammarVisitor)
                return ((ChestTrapGrammarVisitor<? extends T>) visitor).visitDestroy(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class MagicContext extends ParserRuleContext {
        public String magicStr;
        public Token m;

        public MagicContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode MAGIC() {
            return getToken(ChestTrapGrammar.MAGIC, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(ChestTrapGrammar.INTEGER, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_magic;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).enterMagic(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).exitMagic(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ChestTrapGrammarVisitor)
                return ((ChestTrapGrammarVisitor<? extends T>) visitor).visitMagic(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class MsgContext extends ParserRuleContext {
        public String msgStr;
        public Token m;

        public MsgContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode MSG() {
            return getToken(ChestTrapGrammar.MSG, 0);
        }

        public TerminalNode STRING() {
            return getToken(ChestTrapGrammar.STRING, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_msg;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).enterMsg(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).exitMsg(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ChestTrapGrammarVisitor)
                return ((ChestTrapGrammarVisitor<? extends T>) visitor).visitMsg(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class MsgDeathContext extends ParserRuleContext {
        public String msgDeathStr;
        public Token m;

        public MsgDeathContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode MSG_DEATH() {
            return getToken(ChestTrapGrammar.MSG_DEATH, 0);
        }

        public TerminalNode STRING() {
            return getToken(ChestTrapGrammar.STRING, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_msgDeath;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).enterMsgDeath(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).exitMsgDeath(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ChestTrapGrammarVisitor)
                return ((ChestTrapGrammarVisitor<? extends T>) visitor).visitMsgDeath(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ChestTrapContext extends ParserRuleContext {
        public ChestTrapParseRecord trap;
        public NameContext name;
        public CodeContext code;
        public LevelContext level;
        public EffectBlockContext effectBlock;
        public DestroyContext destroy;
        public MagicContext magic;
        public MsgContext msg;
        public MsgDeathContext msgDeath;

        public ChestTrapContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public CodeContext code() {
            return getRuleContext(CodeContext.class, 0);
        }

        public List<LevelContext> level() {
            return getRuleContexts(LevelContext.class);
        }

        public LevelContext level(int i) {
            return getRuleContext(LevelContext.class, i);
        }

        public List<EffectBlockContext> effectBlock() {
            return getRuleContexts(EffectBlockContext.class);
        }

        public EffectBlockContext effectBlock(int i) {
            return getRuleContext(EffectBlockContext.class, i);
        }

        public List<DestroyContext> destroy() {
            return getRuleContexts(DestroyContext.class);
        }

        public DestroyContext destroy(int i) {
            return getRuleContext(DestroyContext.class, i);
        }

        public List<MagicContext> magic() {
            return getRuleContexts(MagicContext.class);
        }

        public MagicContext magic(int i) {
            return getRuleContext(MagicContext.class, i);
        }

        public List<MsgContext> msg() {
            return getRuleContexts(MsgContext.class);
        }

        public MsgContext msg(int i) {
            return getRuleContext(MsgContext.class, i);
        }

        public List<MsgDeathContext> msgDeath() {
            return getRuleContexts(MsgDeathContext.class);
        }

        public MsgDeathContext msgDeath(int i) {
            return getRuleContext(MsgDeathContext.class, i);
        }

        @Override
        public int getRuleIndex() {
            return RULE_chestTrap;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener)
                ((ChestTrapGrammarListener) listener).enterChestTrap(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).exitChestTrap(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ChestTrapGrammarVisitor)
                return ((ChestTrapGrammarVisitor<? extends T>) visitor).visitChestTrap(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class FileContext extends ParserRuleContext {
        public String declaredRecordCount;
        public List<ChestTrapParseRecord> chestTraps;
        public RecordCountContext recordCount;
        public ChestTrapContext chestTrap;

        public FileContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public RecordCountContext recordCount() {
            return getRuleContext(RecordCountContext.class, 0);
        }

        public TerminalNode EOF() {
            return getToken(ChestTrapGrammar.EOF, 0);
        }

        public List<ChestTrapContext> chestTrap() {
            return getRuleContexts(ChestTrapContext.class);
        }

        public ChestTrapContext chestTrap(int i) {
            return getRuleContext(ChestTrapContext.class, i);
        }

        @Override
        public int getRuleIndex() {
            return RULE_file;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ChestTrapGrammarVisitor)
                return ((ChestTrapGrammarVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class EffectContext extends ParserRuleContext {
        public String type;
        public String wrapper;
        public String radius;
        public String other;
        public Token t;
        public Token st;
        public Token rad;
        public Token oth;

        public EffectContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode EFFECT() {
            return getToken(ChestTrapGrammar.EFFECT, 0);
        }

        public List<TerminalNode> UCASE() {
            return getTokens(ChestTrapGrammar.UCASE);
        }

        public TerminalNode UCASE(int i) {
            return getToken(ChestTrapGrammar.UCASE, i);
        }

        public List<TerminalNode> COLON() {
            return getTokens(ChestTrapGrammar.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(ChestTrapGrammar.COLON, i);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(ChestTrapGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(ChestTrapGrammar.INTEGER, i);
        }

        @Override
        public int getRuleIndex() {
            return RULE_effect;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).enterEffect(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).exitEffect(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ChestTrapGrammarVisitor)
                return ((ChestTrapGrammarVisitor<? extends T>) visitor).visitEffect(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class TimeContext extends ParserRuleContext {
        public String timeStr;
        public Token DICE_SIMPLE_VALUE;

        public TimeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode TIME() {
            return getToken(ChestTrapGrammar.TIME, 0);
        }

        public TerminalNode DICE_SIMPLE_VALUE() {
            return getToken(ChestTrapGrammar.DICE_SIMPLE_VALUE, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_time;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).enterTime(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).exitTime(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ChestTrapGrammarVisitor)
                return ((ChestTrapGrammarVisitor<? extends T>) visitor).visitTime(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class EffectYXContext extends ParserRuleContext {
        public String y;
        public String x;
        public Token yVal;
        public Token xVal;

        public EffectYXContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode EFFECT_YX() {
            return getToken(ChestTrapGrammar.EFFECT_YX, 0);
        }

        public TerminalNode COLON() {
            return getToken(ChestTrapGrammar.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(ChestTrapGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(ChestTrapGrammar.INTEGER, i);
        }

        @Override
        public int getRuleIndex() {
            return RULE_effectYX;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).enterEffectYX(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).exitEffectYX(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ChestTrapGrammarVisitor)
                return ((ChestTrapGrammarVisitor<? extends T>) visitor).visitEffectYX(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class DiceContext extends ParserRuleContext {
        public String diceString;
        public String exprChar;
        public String baseName;
        public String operation;
        public Token val;
        public ExprContext expr;

        public DiceContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode DICE() {
            return getToken(ChestTrapGrammar.DICE, 0);
        }

        public TerminalNode DICE_SIMPLE_VALUE() {
            return getToken(ChestTrapGrammar.DICE_SIMPLE_VALUE, 0);
        }

        public TerminalNode DICE_COMPLEX_VALUE() {
            return getToken(ChestTrapGrammar.DICE_COMPLEX_VALUE, 0);
        }

        public List<ExprContext> expr() {
            return getRuleContexts(ExprContext.class);
        }

        public ExprContext expr(int i) {
            return getRuleContext(ExprContext.class, i);
        }

        @Override
        public int getRuleIndex() {
            return RULE_dice;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).enterDice(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).exitDice(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ChestTrapGrammarVisitor)
                return ((ChestTrapGrammarVisitor<? extends T>) visitor).visitDice(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ExprContext extends ParserRuleContext {
        public String exprChar;
        public String baseName;
        public String operation;
        public Token ch;
        public Token base;
        public Token op;

        public ExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode EXPR() {
            return getToken(ChestTrapGrammar.EXPR, 0);
        }

        public List<TerminalNode> EXPR_COLON() {
            return getTokens(ChestTrapGrammar.EXPR_COLON);
        }

        public TerminalNode EXPR_COLON(int i) {
            return getToken(ChestTrapGrammar.EXPR_COLON, i);
        }

        public TerminalNode EXPR_CHAR() {
            return getToken(ChestTrapGrammar.EXPR_CHAR, 0);
        }

        public TerminalNode EXPR_UCASE() {
            return getToken(ChestTrapGrammar.EXPR_UCASE, 0);
        }

        public TerminalNode EXPR_OP() {
            return getToken(ChestTrapGrammar.EXPR_OP, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_expr;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).enterExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).exitExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ChestTrapGrammarVisitor)
                return ((ChestTrapGrammarVisitor<? extends T>) visitor).visitExpr(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class EffectMsgContext extends ParserRuleContext {
        public String message;
        public Token FREE_TEXT;

        public EffectMsgContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode EFFECT_MESSAGE() {
            return getToken(ChestTrapGrammar.EFFECT_MESSAGE, 0);
        }

        public TerminalNode FREE_TEXT() {
            return getToken(ChestTrapGrammar.FREE_TEXT, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_effectMsg;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener)
                ((ChestTrapGrammarListener) listener).enterEffectMsg(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener) ((ChestTrapGrammarListener) listener).exitEffectMsg(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ChestTrapGrammarVisitor)
                return ((ChestTrapGrammarVisitor<? extends T>) visitor).visitEffectMsg(this);
            else return visitor.visitChildren(this);
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static class EffectBlockContext extends ParserRuleContext {
        public String typeInit;
        public String subtypeWrapperInit;
        public String radius;
        public String other;
        public String diceString;
        public String yVal;
        public String xVal;
        public String expressionChars;
        public String expressionBase;
        public String expressionOperation;
        public String timeDiceString;
        public String effectMessage;
        public int lineNo;
        public EffectContext effect;
        public EffectYXContext effectYX;
        public DiceContext dice;
        public TimeContext time;
        public EffectMsgContext effectMsg;

        public EffectBlockContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public EffectContext effect() {
            return getRuleContext(EffectContext.class, 0);
        }

        public TimeContext time() {
            return getRuleContext(TimeContext.class, 0);
        }

        public EffectMsgContext effectMsg() {
            return getRuleContext(EffectMsgContext.class, 0);
        }

        public EffectYXContext effectYX() {
            return getRuleContext(EffectYXContext.class, 0);
        }

        public DiceContext dice() {
            return getRuleContext(DiceContext.class, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_effectBlock;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener)
                ((ChestTrapGrammarListener) listener).enterEffectBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ChestTrapGrammarListener)
                ((ChestTrapGrammarListener) listener).exitEffectBlock(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ChestTrapGrammarVisitor)
                return ((ChestTrapGrammarVisitor<? extends T>) visitor).visitEffectBlock(this);
            else return visitor.visitChildren(this);
        }
    }
}