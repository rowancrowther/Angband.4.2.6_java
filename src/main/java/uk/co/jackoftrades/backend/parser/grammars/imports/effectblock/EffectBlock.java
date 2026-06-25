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

// Generated from EffectBlock.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.imports.effectblock;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class EffectBlock extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            EFFECT = 1, EFFECT_MESSAGE = 2, DICE = 3, TIME = 4, EFFECT_YX = 5, EXPR = 6, COLON = 7,
            UCASE = 8, INTEGER = 9, SIMPLE_DICE_STRING = 10, COMPLEX_DICE_STRING = 11, COMMENT = 12,
            EOL = 13, FREE_TEXT = 14, DICE_SIMPLE_VALUE = 15, DICE_COMPLEX_VALUE = 16, EXPR_CHAR = 17,
            EXPR_COLON = 18, EXPR_UCASE = 19, EXPR_OP = 20, EXPR_EOL = 21;
    public static final int
            RULE_effect = 0, RULE_time = 1, RULE_effectYX = 2, RULE_dice = 3, RULE_expr = 4,
            RULE_effectMsg = 5, RULE_effectBlock = 6;

    private static String[] makeRuleNames() {
        return new String[]{
                "effect", "time", "effectYX", "dice", "expr", "effectMsg", "effectBlock"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'effect:'", "'effect-msg:'", "'dice:'", "'time:'", "'effect-yx:'",
                "'expr:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "EFFECT", "EFFECT_MESSAGE", "DICE", "TIME", "EFFECT_YX", "EXPR",
                "COLON", "UCASE", "INTEGER", "SIMPLE_DICE_STRING", "COMPLEX_DICE_STRING",
                "COMMENT", "EOL", "FREE_TEXT", "DICE_SIMPLE_VALUE", "DICE_COMPLEX_VALUE",
                "EXPR_CHAR", "EXPR_COLON", "EXPR_UCASE", "EXPR_OP", "EXPR_EOL"
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

    @Override
    public String getGrammarFileName() {
        return "EffectBlock.g4";
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

    public EffectBlock(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
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

        public TerminalNode EFFECT() {
            return getToken(EffectBlock.EFFECT, 0);
        }

        public List<TerminalNode> UCASE() {
            return getTokens(EffectBlock.UCASE);
        }

        public TerminalNode UCASE(int i) {
            return getToken(EffectBlock.UCASE, i);
        }

        public List<TerminalNode> COLON() {
            return getTokens(EffectBlock.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(EffectBlock.COLON, i);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(EffectBlock.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(EffectBlock.INTEGER, i);
        }

        public EffectContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_effect;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof EffectBlockListener) ((EffectBlockListener) listener).enterEffect(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof EffectBlockListener) ((EffectBlockListener) listener).exitEffect(this);
        }
    }

    public final EffectContext effect() throws RecognitionException {
        EffectContext _localctx = new EffectContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_effect);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(14);
                match(EFFECT);
                setState(15);
                ((EffectContext) _localctx).t = match(UCASE);

                ((EffectContext) _localctx).type = ((EffectContext) _localctx).t.getText();

                setState(30);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COLON) {
                    {
                        setState(17);
                        match(COLON);
                        setState(18);
                        ((EffectContext) _localctx).st = match(UCASE);

                        ((EffectContext) _localctx).wrapper = ((EffectContext) _localctx).st.getText().toUpperCase();

                        setState(28);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == COLON) {
                            {
                                setState(20);
                                match(COLON);
                                setState(21);
                                ((EffectContext) _localctx).rad = match(INTEGER);

                                ((EffectContext) _localctx).radius = ((EffectContext) _localctx).rad.getText();

                                setState(26);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                                if (_la == COLON) {
                                    {
                                        setState(23);
                                        match(COLON);
                                        setState(24);
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

    @SuppressWarnings("CheckReturnValue")
    public static class TimeContext extends ParserRuleContext {
        public String timeStr;
        public Token DICE_SIMPLE_VALUE;

        public TerminalNode TIME() {
            return getToken(EffectBlock.TIME, 0);
        }

        public TerminalNode DICE_SIMPLE_VALUE() {
            return getToken(EffectBlock.DICE_SIMPLE_VALUE, 0);
        }

        public TimeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_time;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof EffectBlockListener) ((EffectBlockListener) listener).enterTime(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof EffectBlockListener) ((EffectBlockListener) listener).exitTime(this);
        }
    }

    public final TimeContext time() throws RecognitionException {
        TimeContext _localctx = new TimeContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_time);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(32);
                match(TIME);
                setState(33);
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

    @SuppressWarnings("CheckReturnValue")
    public static class EffectYXContext extends ParserRuleContext {
        public String y;
        public String x;
        public Token yVal;
        public Token xVal;

        public TerminalNode EFFECT_YX() {
            return getToken(EffectBlock.EFFECT_YX, 0);
        }

        public TerminalNode COLON() {
            return getToken(EffectBlock.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(EffectBlock.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(EffectBlock.INTEGER, i);
        }

        public EffectYXContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_effectYX;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof EffectBlockListener) ((EffectBlockListener) listener).enterEffectYX(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof EffectBlockListener) ((EffectBlockListener) listener).exitEffectYX(this);
        }
    }

    public final EffectYXContext effectYX() throws RecognitionException {
        EffectYXContext _localctx = new EffectYXContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_effectYX);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(36);
                match(EFFECT_YX);
                setState(37);
                ((EffectYXContext) _localctx).yVal = match(INTEGER);
                setState(38);
                match(COLON);
                setState(39);
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

    @SuppressWarnings("CheckReturnValue")
    public static class DiceContext extends ParserRuleContext {
        public String diceString;
        public String exprChar;
        public String baseName;
        public String operation;
        public Token val;
        public ExprContext expr;

        public TerminalNode DICE() {
            return getToken(EffectBlock.DICE, 0);
        }

        public TerminalNode DICE_SIMPLE_VALUE() {
            return getToken(EffectBlock.DICE_SIMPLE_VALUE, 0);
        }

        public TerminalNode DICE_COMPLEX_VALUE() {
            return getToken(EffectBlock.DICE_COMPLEX_VALUE, 0);
        }

        public List<ExprContext> expr() {
            return getRuleContexts(ExprContext.class);
        }

        public ExprContext expr(int i) {
            return getRuleContext(ExprContext.class, i);
        }

        public DiceContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_dice;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof EffectBlockListener) ((EffectBlockListener) listener).enterDice(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof EffectBlockListener) ((EffectBlockListener) listener).exitDice(this);
        }
    }

    public final DiceContext dice() throws RecognitionException {
        DiceContext _localctx = new DiceContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_dice);

        String charHolder = "";
        String baseHolder = "";
        String operHolder = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(42);
                match(DICE);
                setState(54);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case DICE_COMPLEX_VALUE: {
                        {
                            setState(43);
                            ((DiceContext) _localctx).val = match(DICE_COMPLEX_VALUE);

                            ((DiceContext) _localctx).diceString = ((DiceContext) _localctx).val.getText();

                            setState(48);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            do {
                                {
                                    {
                                        setState(45);
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
                                setState(50);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                            } while (_la == EXPR);
                        }
                    }
                    break;
                    case DICE_SIMPLE_VALUE: {
                        setState(52);
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

    @SuppressWarnings("CheckReturnValue")
    public static class ExprContext extends ParserRuleContext {
        public String exprChar;
        public String baseName;
        public String operation;
        public Token ch;
        public Token base;
        public Token op;

        public TerminalNode EXPR() {
            return getToken(EffectBlock.EXPR, 0);
        }

        public List<TerminalNode> EXPR_COLON() {
            return getTokens(EffectBlock.EXPR_COLON);
        }

        public TerminalNode EXPR_COLON(int i) {
            return getToken(EffectBlock.EXPR_COLON, i);
        }

        public TerminalNode EXPR_CHAR() {
            return getToken(EffectBlock.EXPR_CHAR, 0);
        }

        public TerminalNode EXPR_UCASE() {
            return getToken(EffectBlock.EXPR_UCASE, 0);
        }

        public TerminalNode EXPR_OP() {
            return getToken(EffectBlock.EXPR_OP, 0);
        }

        public ExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_expr;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof EffectBlockListener) ((EffectBlockListener) listener).enterExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof EffectBlockListener) ((EffectBlockListener) listener).exitExpr(this);
        }
    }

    public final ExprContext expr() throws RecognitionException {
        ExprContext _localctx = new ExprContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_expr);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(56);
                match(EXPR);
                setState(57);
                ((ExprContext) _localctx).ch = match(EXPR_CHAR);
                setState(58);
                match(EXPR_COLON);
                setState(59);
                ((ExprContext) _localctx).base = match(EXPR_UCASE);
                setState(60);
                match(EXPR_COLON);
                setState(61);
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

    @SuppressWarnings("CheckReturnValue")
    public static class EffectMsgContext extends ParserRuleContext {
        public String message;
        public Token FREE_TEXT;

        public TerminalNode EFFECT_MESSAGE() {
            return getToken(EffectBlock.EFFECT_MESSAGE, 0);
        }

        public TerminalNode FREE_TEXT() {
            return getToken(EffectBlock.FREE_TEXT, 0);
        }

        public EffectMsgContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_effectMsg;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof EffectBlockListener) ((EffectBlockListener) listener).enterEffectMsg(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof EffectBlockListener) ((EffectBlockListener) listener).exitEffectMsg(this);
        }
    }

    public final EffectMsgContext effectMsg() throws RecognitionException {
        EffectMsgContext _localctx = new EffectMsgContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_effectMsg);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(64);
                match(EFFECT_MESSAGE);
                setState(65);
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
        public EffectContext effect;
        public EffectYXContext effectYX;
        public DiceContext dice;
        public TimeContext time;
        public EffectMsgContext effectMsg;

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

        public EffectBlockContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_effectBlock;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof EffectBlockListener) ((EffectBlockListener) listener).enterEffectBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof EffectBlockListener) ((EffectBlockListener) listener).exitEffectBlock(this);
        }
    }

    public final EffectBlockContext effectBlock() throws RecognitionException {
        EffectBlockContext _localctx = new EffectBlockContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_effectBlock);

        String expressionString = "";
        String baseString = "";
        String opString = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(68);
                ((EffectBlockContext) _localctx).effect = effect();

                ((EffectBlockContext) _localctx).typeInit = ((EffectBlockContext) _localctx).effect.type;
                ((EffectBlockContext) _localctx).subtypeWrapperInit = ((EffectBlockContext) _localctx).effect.wrapper;
                ((EffectBlockContext) _localctx).radius = ((EffectBlockContext) _localctx).effect.radius;
                ((EffectBlockContext) _localctx).other = ((EffectBlockContext) _localctx).effect.other;

                setState(78);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case EFFECT_YX: {
                        {
                            setState(70);
                            ((EffectBlockContext) _localctx).effectYX = effectYX();

                            ((EffectBlockContext) _localctx).yVal = ((EffectBlockContext) _localctx).effectYX.y;
                            ((EffectBlockContext) _localctx).xVal = ((EffectBlockContext) _localctx).effectYX.x;

                        }
                    }
                    break;
                    case EOF:
                    case EFFECT_MESSAGE:
                    case DICE:
                    case TIME: {
                        {
                            setState(76);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            if (_la == DICE) {
                                {
                                    setState(73);
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
                setState(83);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == TIME) {
                    {
                        setState(80);
                        ((EffectBlockContext) _localctx).time = time();

                        ((EffectBlockContext) _localctx).timeDiceString = ((EffectBlockContext) _localctx).time.timeStr;

                    }
                }

                setState(88);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == EFFECT_MESSAGE) {
                    {
                        setState(85);
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

    public static final String _serializedATN =
            "\u0004\u0001\u0015[\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0003\u0000\u001b\b\u0000\u0003" +
                    "\u0000\u001d\b\u0000\u0003\u0000\u001f\b\u0000\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0004\u00031\b\u0003\u000b\u0003\f\u00032\u0001" +
                    "\u0003\u0001\u0003\u0003\u00037\b\u0003\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003" +
                    "\u0006M\b\u0006\u0003\u0006O\b\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0003\u0006T\b\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006" +
                    "Y\b\u0006\u0001\u0006\u0000\u0000\u0007\u0000\u0002\u0004\u0006\b\n\f" +
                    "\u0000\u0000\\\u0000\u000e\u0001\u0000\u0000\u0000\u0002 \u0001\u0000" +
                    "\u0000\u0000\u0004$\u0001\u0000\u0000\u0000\u0006*\u0001\u0000\u0000\u0000" +
                    "\b8\u0001\u0000\u0000\u0000\n@\u0001\u0000\u0000\u0000\fD\u0001\u0000" +
                    "\u0000\u0000\u000e\u000f\u0005\u0001\u0000\u0000\u000f\u0010\u0005\b\u0000" +
                    "\u0000\u0010\u001e\u0006\u0000\uffff\uffff\u0000\u0011\u0012\u0005\u0007" +
                    "\u0000\u0000\u0012\u0013\u0005\b\u0000\u0000\u0013\u001c\u0006\u0000\uffff" +
                    "\uffff\u0000\u0014\u0015\u0005\u0007\u0000\u0000\u0015\u0016\u0005\t\u0000" +
                    "\u0000\u0016\u001a\u0006\u0000\uffff\uffff\u0000\u0017\u0018\u0005\u0007" +
                    "\u0000\u0000\u0018\u0019\u0005\t\u0000\u0000\u0019\u001b\u0006\u0000\uffff" +
                    "\uffff\u0000\u001a\u0017\u0001\u0000\u0000\u0000\u001a\u001b\u0001\u0000" +
                    "\u0000\u0000\u001b\u001d\u0001\u0000\u0000\u0000\u001c\u0014\u0001\u0000" +
                    "\u0000\u0000\u001c\u001d\u0001\u0000\u0000\u0000\u001d\u001f\u0001\u0000" +
                    "\u0000\u0000\u001e\u0011\u0001\u0000\u0000\u0000\u001e\u001f\u0001\u0000" +
                    "\u0000\u0000\u001f\u0001\u0001\u0000\u0000\u0000 !\u0005\u0004\u0000\u0000" +
                    "!\"\u0005\u000f\u0000\u0000\"#\u0006\u0001\uffff\uffff\u0000#\u0003\u0001" +
                    "\u0000\u0000\u0000$%\u0005\u0005\u0000\u0000%&\u0005\t\u0000\u0000&\'" +
                    "\u0005\u0007\u0000\u0000\'(\u0005\t\u0000\u0000()\u0006\u0002\uffff\uffff" +
                    "\u0000)\u0005\u0001\u0000\u0000\u0000*6\u0005\u0003\u0000\u0000+,\u0005" +
                    "\u0010\u0000\u0000,0\u0006\u0003\uffff\uffff\u0000-.\u0003\b\u0004\u0000" +
                    "./\u0006\u0003\uffff\uffff\u0000/1\u0001\u0000\u0000\u00000-\u0001\u0000" +
                    "\u0000\u000012\u0001\u0000\u0000\u000020\u0001\u0000\u0000\u000023\u0001" +
                    "\u0000\u0000\u000037\u0001\u0000\u0000\u000045\u0005\u000f\u0000\u0000" +
                    "57\u0006\u0003\uffff\uffff\u00006+\u0001\u0000\u0000\u000064\u0001\u0000" +
                    "\u0000\u00007\u0007\u0001\u0000\u0000\u000089\u0005\u0006\u0000\u0000" +
                    "9:\u0005\u0011\u0000\u0000:;\u0005\u0012\u0000\u0000;<\u0005\u0013\u0000" +
                    "\u0000<=\u0005\u0012\u0000\u0000=>\u0005\u0014\u0000\u0000>?\u0006\u0004" +
                    "\uffff\uffff\u0000?\t\u0001\u0000\u0000\u0000@A\u0005\u0002\u0000\u0000" +
                    "AB\u0005\u000e\u0000\u0000BC\u0006\u0005\uffff\uffff\u0000C\u000b\u0001" +
                    "\u0000\u0000\u0000DE\u0003\u0000\u0000\u0000EN\u0006\u0006\uffff\uffff" +
                    "\u0000FG\u0003\u0004\u0002\u0000GH\u0006\u0006\uffff\uffff\u0000HO\u0001" +
                    "\u0000\u0000\u0000IJ\u0003\u0006\u0003\u0000JK\u0006\u0006\uffff\uffff" +
                    "\u0000KM\u0001\u0000\u0000\u0000LI\u0001\u0000\u0000\u0000LM\u0001\u0000" +
                    "\u0000\u0000MO\u0001\u0000\u0000\u0000NF\u0001\u0000\u0000\u0000NL\u0001" +
                    "\u0000\u0000\u0000OS\u0001\u0000\u0000\u0000PQ\u0003\u0002\u0001\u0000" +
                    "QR\u0006\u0006\uffff\uffff\u0000RT\u0001\u0000\u0000\u0000SP\u0001\u0000" +
                    "\u0000\u0000ST\u0001\u0000\u0000\u0000TX\u0001\u0000\u0000\u0000UV\u0003" +
                    "\n\u0005\u0000VW\u0006\u0006\uffff\uffff\u0000WY\u0001\u0000\u0000\u0000" +
                    "XU\u0001\u0000\u0000\u0000XY\u0001\u0000\u0000\u0000Y\r\u0001\u0000\u0000" +
                    "\u0000\t\u001a\u001c\u001e26LNSX";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}