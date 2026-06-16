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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/PlayerTimed.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.playertimed;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.effect.EffectSubTypeWrapper;
import uk.co.jackoftrades.middle.enums.EffectEnum;
import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.Brand;
import uk.co.jackoftrades.middle.objects.Slay;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.PlayerTimedEffect;
import uk.co.jackoftrades.middle.player.TimedFailure;
import uk.co.jackoftrades.middle.player.TimedGrade;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;
import uk.co.jackoftrades.middle.player.enums.TimedEffectReasonType;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class PlayerTimedParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, NAME = 3, DESC = 4, GRADE = 5, ON_END = 6, ON_INCREASE = 7, ON_DECREASE = 8,
            MSGT = 9, FAIL = 10, ON_BEGIN_EFFECT = 11, ON_END_EFFECT = 12, EFFECT_YX = 13, EFFECT_DICE = 14,
            EFFECT_EXPR = 15, EFFECT_MSG = 16, RESIST = 17, BRAND = 18, SLAY = 19, FLAG_SYNONYM = 20,
            LOWER_BOUND = 21, FLAGS = 22, ANY_CHAR = 23, INTEGER = 24, UCASE = 25, LCASE = 26,
            COLON = 27, STRING = 28;
    public static final int
            RULE_name = 0, RULE_desc = 1, RULE_grade = 2, RULE_onEnd = 3, RULE_onIncrease = 4,
            RULE_onDecrease = 5, RULE_msgt = 6, RULE_fail = 7, RULE_onBeginEffect = 8,
            RULE_onEndEffect = 9, RULE_effectYX = 10, RULE_effectDice = 11, RULE_effectMsg = 12,
            RULE_resist = 13, RULE_brand = 14, RULE_slay = 15, RULE_flagSynonym = 16,
            RULE_lowerBound = 17, RULE_flags = 18, RULE_playerTimed = 19, RULE_file = 20;

    private static String[] makeRuleNames() {
        return new String[]{
                "name", "desc", "grade", "onEnd", "onIncrease", "onDecrease", "msgt",
                "fail", "onBeginEffect", "onEndEffect", "effectYX", "effectDice", "effectMsg",
                "resist", "brand", "slay", "flagSynonym", "lowerBound", "flags", "playerTimed",
                "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'name:'", "'desc:'", null, "'on-end:'", "'on-increase:'",
                "'on-decrease:'", "'msgt:'", "'fail:'", "'on-begin-effect:'", "'on-end-effect:'",
                "'effect-yx:'", "'effect-dice:'", "'effect-expr:'", "'effect-msg:'",
                "'resist:'", "'brand:'", "'slay:'", "'flag-synonym:'", "'lower-bound:'",
                "'flags:'", null, null, null, null, "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "NAME", "DESC", "GRADE", "ON_END", "ON_INCREASE",
                "ON_DECREASE", "MSGT", "FAIL", "ON_BEGIN_EFFECT", "ON_END_EFFECT", "EFFECT_YX",
                "EFFECT_DICE", "EFFECT_EXPR", "EFFECT_MSG", "RESIST", "BRAND", "SLAY",
                "FLAG_SYNONYM", "LOWER_BOUND", "FLAGS", "ANY_CHAR", "INTEGER", "UCASE",
                "LCASE", "COLON", "STRING"
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
        return "PlayerTimed.g4";
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

    public PlayerTimedParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class NameContext extends ParserRuleContext {
        public TimedEffect nameStr;
        public Token UCASE;

        public TerminalNode NAME() {
            return getToken(PlayerTimedParser.NAME, 0);
        }

        public TerminalNode UCASE() {
            return getToken(PlayerTimedParser.UCASE, 0);
        }

        public TerminalNode EOL() {
            return getToken(PlayerTimedParser.EOL, 0);
        }

        public NameContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_name;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerTimedVisitor)
                return ((PlayerTimedVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(42);
                match(NAME);
                setState(43);
                ((NameContext) _localctx).UCASE = match(UCASE);
                setState(44);
                match(EOL);

                String raw = ((NameContext) _localctx).UCASE.getText();
                ((NameContext) _localctx).nameStr = TimedEffect.valueOf("TMD_" + raw);

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
    public static class DescContext extends ParserRuleContext {
        public String descStr;
        public Token STRING;
        public Token LCASE;

        public TerminalNode DESC() {
            return getToken(PlayerTimedParser.DESC, 0);
        }

        public TerminalNode EOL() {
            return getToken(PlayerTimedParser.EOL, 0);
        }

        public TerminalNode STRING() {
            return getToken(PlayerTimedParser.STRING, 0);
        }

        public TerminalNode LCASE() {
            return getToken(PlayerTimedParser.LCASE, 0);
        }

        public DescContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_desc;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).enterDesc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).exitDesc(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerTimedVisitor)
                return ((PlayerTimedVisitor<? extends T>) visitor).visitDesc(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DescContext desc() throws RecognitionException {
        DescContext _localctx = new DescContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_desc);

        ((DescContext) _localctx).descStr = "";

        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(47);
                match(DESC);
                setState(52);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case STRING: {
                        {
                            setState(48);
                            ((DescContext) _localctx).STRING = match(STRING);
                            ((DescContext) _localctx).descStr = ((DescContext) _localctx).STRING.getText();
                        }
                    }
                    break;
                    case LCASE: {
                        {
                            setState(50);
                            ((DescContext) _localctx).LCASE = match(LCASE);
                            ((DescContext) _localctx).descStr = ((DescContext) _localctx).LCASE.getText();
                        }
                    }
                    break;
                    default:
                        throw new NoViableAltException(this);
                }
                setState(54);
                match(EOL);
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
    public static class GradeContext extends ParserRuleContext {
        public TimedGrade timedGrade;
        public Token GRADE;

        public TerminalNode GRADE() {
            return getToken(PlayerTimedParser.GRADE, 0);
        }

        public TerminalNode EOL() {
            return getToken(PlayerTimedParser.EOL, 0);
        }

        public GradeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_grade;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).enterGrade(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).exitGrade(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerTimedVisitor)
                return ((PlayerTimedVisitor<? extends T>) visitor).visitGrade(this);
            else return visitor.visitChildren(this);
        }
    }

    public final GradeContext grade() throws RecognitionException {
        GradeContext _localctx = new GradeContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_grade);

        int gradeInit = 0;
        ColourType colInit = ColourType.COLOUR_TYPE_DARK;
        int maxInit = 0;
        String nameInit = "";
        String upMsgInit = "";
        String downMsgInit = "";

        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(56);
                ((GradeContext) _localctx).GRADE = match(GRADE);
                setState(57);
                match(EOL);

                String[] parts = ((GradeContext) _localctx).GRADE.getText().split(":");
                // Ignore the first part
                // Second part is a single character which relates to a colour
                colInit = ColourType.findColourType(parts[1].charAt(0));
                // Third part is the maximum turns this can apply to
                maxInit = Integer.parseInt(parts[2]);
                // Next come three strings - Name of the grade,
                // the message when the effect occurs, and the
                // message when it finishes.
                //
                // The third message is optional
                nameInit = parts[3];
                upMsgInit = parts[4];

                if (parts.length > 5)
                    downMsgInit = parts[5];

            }
            _ctx.stop = _input.LT(-1);

            ((GradeContext) _localctx).timedGrade = new TimedGrade(gradeInit, colInit, maxInit, nameInit, upMsgInit,
                    downMsgInit);

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
    public static class OnEndContext extends ParserRuleContext {
        public String onEndStr;
        public Token STRING;

        public TerminalNode ON_END() {
            return getToken(PlayerTimedParser.ON_END, 0);
        }

        public TerminalNode STRING() {
            return getToken(PlayerTimedParser.STRING, 0);
        }

        public TerminalNode EOL() {
            return getToken(PlayerTimedParser.EOL, 0);
        }

        public OnEndContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_onEnd;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).enterOnEnd(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).exitOnEnd(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerTimedVisitor)
                return ((PlayerTimedVisitor<? extends T>) visitor).visitOnEnd(this);
            else return visitor.visitChildren(this);
        }
    }

    public final OnEndContext onEnd() throws RecognitionException {
        OnEndContext _localctx = new OnEndContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_onEnd);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(60);
                match(ON_END);
                setState(61);
                ((OnEndContext) _localctx).STRING = match(STRING);
                setState(62);
                match(EOL);
                ((OnEndContext) _localctx).onEndStr = ((OnEndContext) _localctx).STRING.getText();
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
    public static class OnIncreaseContext extends ParserRuleContext {
        public String onIncStr;
        public Token STRING;

        public TerminalNode ON_INCREASE() {
            return getToken(PlayerTimedParser.ON_INCREASE, 0);
        }

        public TerminalNode STRING() {
            return getToken(PlayerTimedParser.STRING, 0);
        }

        public TerminalNode EOL() {
            return getToken(PlayerTimedParser.EOL, 0);
        }

        public OnIncreaseContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_onIncrease;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).enterOnIncrease(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).exitOnIncrease(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerTimedVisitor)
                return ((PlayerTimedVisitor<? extends T>) visitor).visitOnIncrease(this);
            else return visitor.visitChildren(this);
        }
    }

    public final OnIncreaseContext onIncrease() throws RecognitionException {
        OnIncreaseContext _localctx = new OnIncreaseContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_onIncrease);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(65);
                match(ON_INCREASE);
                setState(66);
                ((OnIncreaseContext) _localctx).STRING = match(STRING);
                setState(67);
                match(EOL);
                ((OnIncreaseContext) _localctx).onIncStr = ((OnIncreaseContext) _localctx).STRING.getText();
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
    public static class OnDecreaseContext extends ParserRuleContext {
        public String onDecStr;
        public Token STRING;

        public TerminalNode ON_DECREASE() {
            return getToken(PlayerTimedParser.ON_DECREASE, 0);
        }

        public TerminalNode STRING() {
            return getToken(PlayerTimedParser.STRING, 0);
        }

        public TerminalNode EOL() {
            return getToken(PlayerTimedParser.EOL, 0);
        }

        public OnDecreaseContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_onDecrease;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).enterOnDecrease(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).exitOnDecrease(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerTimedVisitor)
                return ((PlayerTimedVisitor<? extends T>) visitor).visitOnDecrease(this);
            else return visitor.visitChildren(this);
        }
    }

    public final OnDecreaseContext onDecrease() throws RecognitionException {
        OnDecreaseContext _localctx = new OnDecreaseContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_onDecrease);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(70);
                match(ON_DECREASE);
                setState(71);
                ((OnDecreaseContext) _localctx).STRING = match(STRING);
                setState(72);
                match(EOL);
                ((OnDecreaseContext) _localctx).onDecStr = ((OnDecreaseContext) _localctx).STRING.getText();
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
    public static class MsgtContext extends ParserRuleContext {
        public MessageType msgType;
        public Token UCASE;

        public TerminalNode MSGT() {
            return getToken(PlayerTimedParser.MSGT, 0);
        }

        public TerminalNode UCASE() {
            return getToken(PlayerTimedParser.UCASE, 0);
        }

        public TerminalNode EOL() {
            return getToken(PlayerTimedParser.EOL, 0);
        }

        public MsgtContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_msgt;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).enterMsgt(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).exitMsgt(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerTimedVisitor)
                return ((PlayerTimedVisitor<? extends T>) visitor).visitMsgt(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MsgtContext msgt() throws RecognitionException {
        MsgtContext _localctx = new MsgtContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_msgt);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(75);
                match(MSGT);
                setState(76);
                ((MsgtContext) _localctx).UCASE = match(UCASE);
                setState(77);
                match(EOL);

                String raw = "MSG_" + ((MsgtContext) _localctx).UCASE.getText();
                ((MsgtContext) _localctx).msgType = MessageType.valueOf(raw);

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
    public static class FailContext extends ParserRuleContext {
        public TimedFailure failure;
        public Token idx;
        public Token code;

        public TerminalNode FAIL() {
            return getToken(PlayerTimedParser.FAIL, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerTimedParser.COLON, 0);
        }

        public TerminalNode EOL() {
            return getToken(PlayerTimedParser.EOL, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerTimedParser.INTEGER, 0);
        }

        public TerminalNode UCASE() {
            return getToken(PlayerTimedParser.UCASE, 0);
        }

        public FailContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_fail;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).enterFail(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).exitFail(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerTimedVisitor)
                return ((PlayerTimedVisitor<? extends T>) visitor).visitFail(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FailContext fail() throws RecognitionException {
        FailContext _localctx = new FailContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_fail);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(80);
                match(FAIL);
                setState(81);
                ((FailContext) _localctx).idx = match(INTEGER);
                setState(82);
                match(COLON);
                setState(83);
                ((FailContext) _localctx).code = match(UCASE);
                setState(84);
                match(EOL);

                String raw = ((FailContext) _localctx).code.getText();
                int indexVal = Integer.parseInt(((FailContext) _localctx).idx.getText());
                TimedEffectReasonType codeRes = TimedEffectReasonType.fromValue(indexVal);

                switch (codeRes) {
                    case TYPE_TIMED_EFFECT:
                        TimedEffect tValue = TimedEffect.valueOf("TMD_" + raw);
                        ((FailContext) _localctx).failure = new TimedFailure(tValue, codeRes);
                        break;

                    case TYPE_OBJECT_FLAG:
                        ObjectFlag oValue = ObjectFlag.valueOf("OF_" + raw);
                        ((FailContext) _localctx).failure = new TimedFailure(oValue, codeRes);
                        break;

                    case TYPE_VULN:
                    case TYPE_RESIST:
                        ElementEnum eValue = ElementEnum.valueOf("ELEM_" + raw);
                        ((FailContext) _localctx).failure = new TimedFailure(eValue, codeRes);
                        break;

                    case TYPE_PLAYER_FLAG:
                        PlayerFlag pValue = PlayerFlag.valueOf("PF_" + raw);
                        ((FailContext) _localctx).failure = new TimedFailure(pValue, codeRes);
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
    public static class OnBeginEffectContext extends ParserRuleContext {
        public EffectEnum effObj;
        public Token UCASE;

        public TerminalNode ON_BEGIN_EFFECT() {
            return getToken(PlayerTimedParser.ON_BEGIN_EFFECT, 0);
        }

        public TerminalNode UCASE() {
            return getToken(PlayerTimedParser.UCASE, 0);
        }

        public TerminalNode EOL() {
            return getToken(PlayerTimedParser.EOL, 0);
        }

        public OnBeginEffectContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_onBeginEffect;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).enterOnBeginEffect(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).exitOnBeginEffect(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerTimedVisitor)
                return ((PlayerTimedVisitor<? extends T>) visitor).visitOnBeginEffect(this);
            else return visitor.visitChildren(this);
        }
    }

    public final OnBeginEffectContext onBeginEffect() throws RecognitionException {
        OnBeginEffectContext _localctx = new OnBeginEffectContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_onBeginEffect);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(87);
                match(ON_BEGIN_EFFECT);
                setState(88);
                ((OnBeginEffectContext) _localctx).UCASE = match(UCASE);
                setState(89);
                match(EOL);

                String raw = ((OnBeginEffectContext) _localctx).UCASE.getText();
                ((OnBeginEffectContext) _localctx).effObj = EffectEnum.valueOf("EF_" + raw);

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
    public static class OnEndEffectContext extends ParserRuleContext {
        public EffectEnum eEnum;
        public TimedEffect tEnum;
        public Token eff;
        public Token tef;
        public Token UCASE;

        public TerminalNode ON_END_EFFECT() {
            return getToken(PlayerTimedParser.ON_END_EFFECT, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerTimedParser.COLON, 0);
        }

        public TerminalNode EOL() {
            return getToken(PlayerTimedParser.EOL, 0);
        }

        public List<TerminalNode> UCASE() {
            return getTokens(PlayerTimedParser.UCASE);
        }

        public TerminalNode UCASE(int i) {
            return getToken(PlayerTimedParser.UCASE, i);
        }

        public OnEndEffectContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_onEndEffect;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).enterOnEndEffect(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).exitOnEndEffect(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerTimedVisitor)
                return ((PlayerTimedVisitor<? extends T>) visitor).visitOnEndEffect(this);
            else return visitor.visitChildren(this);
        }
    }

    public final OnEndEffectContext onEndEffect() throws RecognitionException {
        OnEndEffectContext _localctx = new OnEndEffectContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_onEndEffect);
        try {
            setState(102);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 1, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(92);
                    match(ON_END_EFFECT);
                    setState(93);
                    ((OnEndEffectContext) _localctx).eff = match(UCASE);
                    setState(94);
                    match(COLON);
                    setState(95);
                    ((OnEndEffectContext) _localctx).tef = match(UCASE);
                    setState(96);
                    match(EOL);

                    ((OnEndEffectContext) _localctx).eEnum = EffectEnum.valueOf("EF_" + ((OnEndEffectContext) _localctx).eff.getText());
                    ((OnEndEffectContext) _localctx).tEnum = TimedEffect.valueOf("TMD_" + ((OnEndEffectContext) _localctx).tef.getText());

                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(98);
                    match(ON_END_EFFECT);
                    setState(99);
                    ((OnEndEffectContext) _localctx).UCASE = match(UCASE);
                    setState(100);
                    match(EOL);

                    ((OnEndEffectContext) _localctx).eEnum = EffectEnum.valueOf("EF_" + ((OnEndEffectContext) _localctx).UCASE.getText());
                    ((OnEndEffectContext) _localctx).tEnum = TimedEffect.TMD_NONE;

                }
                break;
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
        public int y;
        public int x;
        public Token yInt;
        public Token xInt;

        public TerminalNode EFFECT_YX() {
            return getToken(PlayerTimedParser.EFFECT_YX, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerTimedParser.COLON, 0);
        }

        public TerminalNode EOL() {
            return getToken(PlayerTimedParser.EOL, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerTimedParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerTimedParser.INTEGER, i);
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
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).enterEffectYX(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).exitEffectYX(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerTimedVisitor)
                return ((PlayerTimedVisitor<? extends T>) visitor).visitEffectYX(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectYXContext effectYX() throws RecognitionException {
        EffectYXContext _localctx = new EffectYXContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_effectYX);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(104);
                match(EFFECT_YX);
                setState(105);
                ((EffectYXContext) _localctx).yInt = match(INTEGER);
                setState(106);
                match(COLON);
                setState(107);
                ((EffectYXContext) _localctx).xInt = match(INTEGER);
                setState(108);
                match(EOL);

                ((EffectYXContext) _localctx).y = Integer.parseInt(((EffectYXContext) _localctx).yInt.getText());
                ((EffectYXContext) _localctx).x = Integer.parseInt(((EffectYXContext) _localctx).xInt.getText());

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
    public static class EffectDiceContext extends ParserRuleContext {
        public String diceStr;
        public Token INTEGER;

        public TerminalNode EFFECT_DICE() {
            return getToken(PlayerTimedParser.EFFECT_DICE, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerTimedParser.INTEGER, 0);
        }

        public TerminalNode EOL() {
            return getToken(PlayerTimedParser.EOL, 0);
        }

        public EffectDiceContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_effectDice;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).enterEffectDice(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).exitEffectDice(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerTimedVisitor)
                return ((PlayerTimedVisitor<? extends T>) visitor).visitEffectDice(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectDiceContext effectDice() throws RecognitionException {
        EffectDiceContext _localctx = new EffectDiceContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_effectDice);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(111);
                match(EFFECT_DICE);
                setState(112);
                ((EffectDiceContext) _localctx).INTEGER = match(INTEGER);
                setState(113);
                match(EOL);
                ((EffectDiceContext) _localctx).diceStr = ((EffectDiceContext) _localctx).INTEGER.getText();
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
        public String effMsg;
        public Token STRING;

        public TerminalNode EFFECT_MSG() {
            return getToken(PlayerTimedParser.EFFECT_MSG, 0);
        }

        public TerminalNode STRING() {
            return getToken(PlayerTimedParser.STRING, 0);
        }

        public TerminalNode EOL() {
            return getToken(PlayerTimedParser.EOL, 0);
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
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).enterEffectMsg(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).exitEffectMsg(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerTimedVisitor)
                return ((PlayerTimedVisitor<? extends T>) visitor).visitEffectMsg(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectMsgContext effectMsg() throws RecognitionException {
        EffectMsgContext _localctx = new EffectMsgContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_effectMsg);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(116);
                match(EFFECT_MSG);
                setState(117);
                ((EffectMsgContext) _localctx).STRING = match(STRING);
                setState(118);
                match(EOL);

                ((EffectMsgContext) _localctx).effMsg = ((EffectMsgContext) _localctx).STRING.getText();

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
    public static class ResistContext extends ParserRuleContext {
        public ElementEnum resEnum;
        public Token UCASE;

        public TerminalNode RESIST() {
            return getToken(PlayerTimedParser.RESIST, 0);
        }

        public TerminalNode UCASE() {
            return getToken(PlayerTimedParser.UCASE, 0);
        }

        public TerminalNode EOL() {
            return getToken(PlayerTimedParser.EOL, 0);
        }

        public ResistContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_resist;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).enterResist(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).exitResist(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerTimedVisitor)
                return ((PlayerTimedVisitor<? extends T>) visitor).visitResist(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ResistContext resist() throws RecognitionException {
        ResistContext _localctx = new ResistContext(_ctx, getState());
        enterRule(_localctx, 26, RULE_resist);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(121);
                match(RESIST);
                setState(122);
                ((ResistContext) _localctx).UCASE = match(UCASE);
                setState(123);
                match(EOL);

                String raw = ((ResistContext) _localctx).UCASE.getText();
                ((ResistContext) _localctx).resEnum = ElementEnum.valueOf("ELEM_" + raw);

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
    public static class BrandContext extends ParserRuleContext {
        public Brand brandObj;
        public Token UCASE;

        public TerminalNode BRAND() {
            return getToken(PlayerTimedParser.BRAND, 0);
        }

        public TerminalNode UCASE() {
            return getToken(PlayerTimedParser.UCASE, 0);
        }

        public TerminalNode EOL() {
            return getToken(PlayerTimedParser.EOL, 0);
        }

        public BrandContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_brand;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).enterBrand(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).exitBrand(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerTimedVisitor)
                return ((PlayerTimedVisitor<? extends T>) visitor).visitBrand(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BrandContext brand() throws RecognitionException {
        BrandContext _localctx = new BrandContext(_ctx, getState());
        enterRule(_localctx, 28, RULE_brand);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(126);
                match(BRAND);
                setState(127);
                ((BrandContext) _localctx).UCASE = match(UCASE);
                setState(128);
                match(EOL);

                String raw = ((BrandContext) _localctx).UCASE.getText();
                ((BrandContext) _localctx).brandObj = GameConstants.lookupBrandCode(raw);

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
    public static class SlayContext extends ParserRuleContext {
        public Slay slayObj;
        public Token UCASE;

        public TerminalNode SLAY() {
            return getToken(PlayerTimedParser.SLAY, 0);
        }

        public TerminalNode UCASE() {
            return getToken(PlayerTimedParser.UCASE, 0);
        }

        public TerminalNode EOL() {
            return getToken(PlayerTimedParser.EOL, 0);
        }

        public SlayContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_slay;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).enterSlay(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).exitSlay(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerTimedVisitor)
                return ((PlayerTimedVisitor<? extends T>) visitor).visitSlay(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SlayContext slay() throws RecognitionException {
        SlayContext _localctx = new SlayContext(_ctx, getState());
        enterRule(_localctx, 30, RULE_slay);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(131);
                match(SLAY);
                setState(132);
                ((SlayContext) _localctx).UCASE = match(UCASE);
                setState(133);
                match(EOL);

                String raw = ((SlayContext) _localctx).UCASE.getText();
                ((SlayContext) _localctx).slayObj = GameConstants.lookupSlay(raw);

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
    public static class FlagSynonymContext extends ParserRuleContext {
        public ObjectFlag oFlag;
        public int synonymous;
        public Token UCASE;
        public Token INTEGER;

        public TerminalNode FLAG_SYNONYM() {
            return getToken(PlayerTimedParser.FLAG_SYNONYM, 0);
        }

        public TerminalNode UCASE() {
            return getToken(PlayerTimedParser.UCASE, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerTimedParser.COLON, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerTimedParser.INTEGER, 0);
        }

        public TerminalNode EOL() {
            return getToken(PlayerTimedParser.EOL, 0);
        }

        public FlagSynonymContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_flagSynonym;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).enterFlagSynonym(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).exitFlagSynonym(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerTimedVisitor)
                return ((PlayerTimedVisitor<? extends T>) visitor).visitFlagSynonym(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FlagSynonymContext flagSynonym() throws RecognitionException {
        FlagSynonymContext _localctx = new FlagSynonymContext(_ctx, getState());
        enterRule(_localctx, 32, RULE_flagSynonym);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(136);
                match(FLAG_SYNONYM);
                setState(137);
                ((FlagSynonymContext) _localctx).UCASE = match(UCASE);
                setState(138);
                match(COLON);
                setState(139);
                ((FlagSynonymContext) _localctx).INTEGER = match(INTEGER);
                setState(140);
                match(EOL);

                String raw = ((FlagSynonymContext) _localctx).UCASE.getText();
                ((FlagSynonymContext) _localctx).oFlag = ObjectFlag.valueOf("OF_" + raw);
                ((FlagSynonymContext) _localctx).synonymous = Integer.parseInt(((FlagSynonymContext) _localctx).INTEGER.getText());

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
    public static class LowerBoundContext extends ParserRuleContext {
        public int bound;
        public Token INTEGER;

        public TerminalNode LOWER_BOUND() {
            return getToken(PlayerTimedParser.LOWER_BOUND, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerTimedParser.INTEGER, 0);
        }

        public TerminalNode EOL() {
            return getToken(PlayerTimedParser.EOL, 0);
        }

        public LowerBoundContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_lowerBound;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).enterLowerBound(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).exitLowerBound(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerTimedVisitor)
                return ((PlayerTimedVisitor<? extends T>) visitor).visitLowerBound(this);
            else return visitor.visitChildren(this);
        }
    }

    public final LowerBoundContext lowerBound() throws RecognitionException {
        LowerBoundContext _localctx = new LowerBoundContext(_ctx, getState());
        enterRule(_localctx, 34, RULE_lowerBound);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(143);
                match(LOWER_BOUND);
                setState(144);
                ((LowerBoundContext) _localctx).INTEGER = match(INTEGER);
                setState(145);
                match(EOL);

                ((LowerBoundContext) _localctx).bound = Integer.parseInt(((LowerBoundContext) _localctx).INTEGER.getText());

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
    public static class FlagsContext extends ParserRuleContext {
        public String flagStr;
        public Token UCASE;

        public TerminalNode FLAGS() {
            return getToken(PlayerTimedParser.FLAGS, 0);
        }

        public TerminalNode UCASE() {
            return getToken(PlayerTimedParser.UCASE, 0);
        }

        public TerminalNode EOL() {
            return getToken(PlayerTimedParser.EOL, 0);
        }

        public FlagsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_flags;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).enterFlags(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).exitFlags(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerTimedVisitor)
                return ((PlayerTimedVisitor<? extends T>) visitor).visitFlags(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FlagsContext flags() throws RecognitionException {
        FlagsContext _localctx = new FlagsContext(_ctx, getState());
        enterRule(_localctx, 36, RULE_flags);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(148);
                match(FLAGS);
                setState(149);
                ((FlagsContext) _localctx).UCASE = match(UCASE);
                setState(150);
                match(EOL);

                ((FlagsContext) _localctx).flagStr = ((FlagsContext) _localctx).UCASE.getText();

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
    public static class PlayerTimedContext extends ParserRuleContext {
        public PlayerTimedEffect playerTimedEffect;
        public NameContext name;
        public DescContext desc;
        public GradeContext grade;
        public OnEndContext onEnd;
        public OnIncreaseContext onIncrease;
        public OnDecreaseContext onDecrease;
        public MsgtContext msgt;
        public FailContext fail;
        public ResistContext resist;
        public BrandContext brand;
        public SlayContext slay;
        public FlagSynonymContext flagSynonym;
        public FlagsContext flags;
        public LowerBoundContext lowerBound;
        public OnBeginEffectContext onBeginEffect;
        public OnEndEffectContext onEndEffect;
        public EffectDiceContext effectDice;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public List<TerminalNode> EOL() {
            return getTokens(PlayerTimedParser.EOL);
        }

        public TerminalNode EOL(int i) {
            return getToken(PlayerTimedParser.EOL, i);
        }

        public List<DescContext> desc() {
            return getRuleContexts(DescContext.class);
        }

        public DescContext desc(int i) {
            return getRuleContext(DescContext.class, i);
        }

        public List<GradeContext> grade() {
            return getRuleContexts(GradeContext.class);
        }

        public GradeContext grade(int i) {
            return getRuleContext(GradeContext.class, i);
        }

        public List<OnEndContext> onEnd() {
            return getRuleContexts(OnEndContext.class);
        }

        public OnEndContext onEnd(int i) {
            return getRuleContext(OnEndContext.class, i);
        }

        public List<OnIncreaseContext> onIncrease() {
            return getRuleContexts(OnIncreaseContext.class);
        }

        public OnIncreaseContext onIncrease(int i) {
            return getRuleContext(OnIncreaseContext.class, i);
        }

        public List<OnDecreaseContext> onDecrease() {
            return getRuleContexts(OnDecreaseContext.class);
        }

        public OnDecreaseContext onDecrease(int i) {
            return getRuleContext(OnDecreaseContext.class, i);
        }

        public List<MsgtContext> msgt() {
            return getRuleContexts(MsgtContext.class);
        }

        public MsgtContext msgt(int i) {
            return getRuleContext(MsgtContext.class, i);
        }

        public List<FailContext> fail() {
            return getRuleContexts(FailContext.class);
        }

        public FailContext fail(int i) {
            return getRuleContext(FailContext.class, i);
        }

        public List<ResistContext> resist() {
            return getRuleContexts(ResistContext.class);
        }

        public ResistContext resist(int i) {
            return getRuleContext(ResistContext.class, i);
        }

        public List<BrandContext> brand() {
            return getRuleContexts(BrandContext.class);
        }

        public BrandContext brand(int i) {
            return getRuleContext(BrandContext.class, i);
        }

        public List<SlayContext> slay() {
            return getRuleContexts(SlayContext.class);
        }

        public SlayContext slay(int i) {
            return getRuleContext(SlayContext.class, i);
        }

        public List<FlagSynonymContext> flagSynonym() {
            return getRuleContexts(FlagSynonymContext.class);
        }

        public FlagSynonymContext flagSynonym(int i) {
            return getRuleContext(FlagSynonymContext.class, i);
        }

        public List<FlagsContext> flags() {
            return getRuleContexts(FlagsContext.class);
        }

        public FlagsContext flags(int i) {
            return getRuleContext(FlagsContext.class, i);
        }

        public List<LowerBoundContext> lowerBound() {
            return getRuleContexts(LowerBoundContext.class);
        }

        public LowerBoundContext lowerBound(int i) {
            return getRuleContext(LowerBoundContext.class, i);
        }

        public List<OnBeginEffectContext> onBeginEffect() {
            return getRuleContexts(OnBeginEffectContext.class);
        }

        public OnBeginEffectContext onBeginEffect(int i) {
            return getRuleContext(OnBeginEffectContext.class, i);
        }

        public List<OnEndEffectContext> onEndEffect() {
            return getRuleContexts(OnEndEffectContext.class);
        }

        public OnEndEffectContext onEndEffect(int i) {
            return getRuleContext(OnEndEffectContext.class, i);
        }

        public List<EffectDiceContext> effectDice() {
            return getRuleContexts(EffectDiceContext.class);
        }

        public EffectDiceContext effectDice(int i) {
            return getRuleContext(EffectDiceContext.class, i);
        }

        public PlayerTimedContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_playerTimed;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).enterPlayerTimed(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).exitPlayerTimed(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerTimedVisitor)
                return ((PlayerTimedVisitor<? extends T>) visitor).visitPlayerTimed(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PlayerTimedContext playerTimed() throws RecognitionException {
        PlayerTimedContext _localctx = new PlayerTimedContext(_ctx, getState());
        enterRule(_localctx, 38, RULE_playerTimed);

        TimedEffect nameInit = TimedEffect.TMD_NONE;
        String descInit = "";
        String onEndInit = "";
        String onIncInit = "";
        String onDecInit = "";
        MessageType msgInit = MessageType.MSG_NONE;
        TimedFailure failInit = null;
        List<TimedGrade> gradeInit = new ArrayList<>();
        Effect onBegEffInit = null;
        Effect onEndEffInit = null;
        boolean nonStackInit = false;
        int lowerBoundInit = 0;
        int ofFlagDup = 0;
        ObjectFlag oFlagSynInit = ObjectFlag.OF_NONE;
        ElementEnum resistEnum = ElementEnum.ELEM_NONE;
        Brand brandInit = null;
        Slay slayInit = null;

        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(156);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == EOL) {
                    {
                        {
                            setState(153);
                            match(EOL);
                        }
                    }
                    setState(158);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(159);
                ((PlayerTimedContext) _localctx).name = name();

                nameInit = ((PlayerTimedContext) _localctx).name.nameStr;

                setState(209);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        setState(209);
                        _errHandler.sync(this);
                        switch (_input.LA(1)) {
                            case DESC: {
                                setState(161);
                                ((PlayerTimedContext) _localctx).desc = desc();

                                descInit = ((PlayerTimedContext) _localctx).desc.descStr;

                            }
                            break;
                            case GRADE: {
                                setState(164);
                                ((PlayerTimedContext) _localctx).grade = grade();

                                int count = gradeInit.size();
                                TimedGrade tmdG = ((PlayerTimedContext) _localctx).grade.timedGrade;
                                tmdG.setGrade(count);
                                gradeInit.add(tmdG);

                            }
                            break;
                            case ON_END: {
                                setState(167);
                                ((PlayerTimedContext) _localctx).onEnd = onEnd();

                                onEndInit = ((PlayerTimedContext) _localctx).onEnd.onEndStr;

                            }
                            break;
                            case ON_INCREASE: {
                                setState(170);
                                ((PlayerTimedContext) _localctx).onIncrease = onIncrease();

                                onIncInit = ((PlayerTimedContext) _localctx).onIncrease.onIncStr;

                            }
                            break;
                            case ON_DECREASE: {
                                setState(173);
                                ((PlayerTimedContext) _localctx).onDecrease = onDecrease();

                                onDecInit = ((PlayerTimedContext) _localctx).onDecrease.onDecStr;

                            }
                            break;
                            case MSGT: {
                                setState(176);
                                ((PlayerTimedContext) _localctx).msgt = msgt();

                                msgInit = ((PlayerTimedContext) _localctx).msgt.msgType;

                            }
                            break;
                            case FAIL: {
                                setState(179);
                                ((PlayerTimedContext) _localctx).fail = fail();

                                failInit = ((PlayerTimedContext) _localctx).fail.failure;

                            }
                            break;
                            case RESIST: {
                                setState(182);
                                ((PlayerTimedContext) _localctx).resist = resist();

                                resistEnum = ((PlayerTimedContext) _localctx).resist.resEnum;

                            }
                            break;
                            case BRAND: {
                                setState(185);
                                ((PlayerTimedContext) _localctx).brand = brand();

                                brandInit = ((PlayerTimedContext) _localctx).brand.brandObj;

                            }
                            break;
                            case SLAY: {
                                setState(188);
                                ((PlayerTimedContext) _localctx).slay = slay();

                                slayInit = ((PlayerTimedContext) _localctx).slay.slayObj;

                            }
                            break;
                            case FLAG_SYNONYM: {
                                setState(191);
                                ((PlayerTimedContext) _localctx).flagSynonym = flagSynonym();

                                oFlagSynInit = ((PlayerTimedContext) _localctx).flagSynonym.oFlag;
                                ofFlagDup = ((PlayerTimedContext) _localctx).flagSynonym.synonymous;

                            }
                            break;
                            case FLAGS: {
                                setState(194);
                                ((PlayerTimedContext) _localctx).flags = flags();

                                nonStackInit = (((PlayerTimedContext) _localctx).flags.flagStr.equals("NONSTACKING"));

                            }
                            break;
                            case LOWER_BOUND: {
                                setState(197);
                                ((PlayerTimedContext) _localctx).lowerBound = lowerBound();

                                lowerBoundInit = ((PlayerTimedContext) _localctx).lowerBound.bound;

                            }
                            break;
                            case ON_BEGIN_EFFECT: {
                                setState(200);
                                ((PlayerTimedContext) _localctx).onBeginEffect = onBeginEffect();

                                onBegEffInit = new Effect(((PlayerTimedContext) _localctx).onBeginEffect.effObj);

                            }
                            break;
                            case ON_END_EFFECT: {
                                setState(203);
                                ((PlayerTimedContext) _localctx).onEndEffect = onEndEffect();

                                EffectEnum e = ((PlayerTimedContext) _localctx).onEndEffect.eEnum;
                                TimedEffect t = ((PlayerTimedContext) _localctx).onEndEffect.tEnum;
                                EffectSubTypeWrapper wrapper = new EffectSubTypeWrapper(t);
                                onEndEffInit = new Effect(e, wrapper);

                            }
                            break;
                            case EFFECT_DICE: {
                                setState(206);
                                ((PlayerTimedContext) _localctx).effectDice = effectDice();

                                if (onEndEffInit != null)
                                    onEndEffInit.setDice(((PlayerTimedContext) _localctx).effectDice.diceStr);

                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                    }
                    setState(211);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8282096L) != 0));
                setState(216);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 5, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(213);
                                match(EOL);
                            }
                        }
                    }
                    setState(218);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 5, _ctx);
                }
            }
            _ctx.stop = _input.LT(-1);

            ((PlayerTimedContext) _localctx).playerTimedEffect = new PlayerTimedEffect(nameInit,
                    descInit, onEndInit, onIncInit, onDecInit, msgInit,
                    failInit, gradeInit, onBegEffInit, onEndEffInit,
                    nonStackInit, lowerBoundInit, ofFlagDup,
                    oFlagSynInit, resistEnum, brandInit, slayInit);

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
    public static class FileContext extends ParserRuleContext {
        public List<PlayerTimedEffect> effects;
        public PlayerTimedContext playerTimed;

        public TerminalNode EOF() {
            return getToken(PlayerTimedParser.EOF, 0);
        }

        public List<PlayerTimedContext> playerTimed() {
            return getRuleContexts(PlayerTimedContext.class);
        }

        public PlayerTimedContext playerTimed(int i) {
            return getRuleContext(PlayerTimedContext.class, i);
        }

        public List<TerminalNode> EOL() {
            return getTokens(PlayerTimedParser.EOL);
        }

        public TerminalNode EOL(int i) {
            return getToken(PlayerTimedParser.EOL, i);
        }

        public FileContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_file;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerTimedListener) ((PlayerTimedListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerTimedVisitor)
                return ((PlayerTimedVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 40, RULE_file);

        ((FileContext) _localctx).effects = new ArrayList<>();

        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(222);
                _errHandler.sync(this);
                _alt = 1;
                do {
                    switch (_alt) {
                        case 1: {
                            {
                                setState(219);
                                ((FileContext) _localctx).playerTimed = playerTimed();

                                _localctx.effects.add(((FileContext) _localctx).playerTimed.playerTimedEffect);

                            }
                        }
                        break;
                        default:
                            throw new NoViableAltException(this);
                    }
                    setState(224);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 6, _ctx);
                } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
                setState(229);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == EOL) {
                    {
                        {
                            setState(226);
                            match(EOL);
                        }
                    }
                    setState(231);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(232);
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

    public static final String _serializedATN =
            "\u0004\u0001\u001c\u00eb\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001" +
                    "\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004" +
                    "\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007" +
                    "\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b" +
                    "\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007" +
                    "\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007" +
                    "\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0003\u00015\b\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0003" +
                    "\tg\b\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f" +
                    "\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001" +
                    "\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001" +
                    "\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001" +
                    "\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001" +
                    "\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001" +
                    "\u0012\u0001\u0012\u0001\u0012\u0001\u0013\u0005\u0013\u009b\b\u0013\n" +
                    "\u0013\f\u0013\u009e\t\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001" +
                    "\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001" +
                    "\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001" +
                    "\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001" +
                    "\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001" +
                    "\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001" +
                    "\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001" +
                    "\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001" +
                    "\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0004\u0013\u00d2" +
                    "\b\u0013\u000b\u0013\f\u0013\u00d3\u0001\u0013\u0005\u0013\u00d7\b\u0013" +
                    "\n\u0013\f\u0013\u00da\t\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0004" +
                    "\u0014\u00df\b\u0014\u000b\u0014\f\u0014\u00e0\u0001\u0014\u0005\u0014" +
                    "\u00e4\b\u0014\n\u0014\f\u0014\u00e7\t\u0014\u0001\u0014\u0001\u0014\u0001" +
                    "\u0014\u0000\u0000\u0015\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012" +
                    "\u0014\u0016\u0018\u001a\u001c\u001e \"$&(\u0000\u0000\u00eb\u0000*\u0001" +
                    "\u0000\u0000\u0000\u0002/\u0001\u0000\u0000\u0000\u00048\u0001\u0000\u0000" +
                    "\u0000\u0006<\u0001\u0000\u0000\u0000\bA\u0001\u0000\u0000\u0000\nF\u0001" +
                    "\u0000\u0000\u0000\fK\u0001\u0000\u0000\u0000\u000eP\u0001\u0000\u0000" +
                    "\u0000\u0010W\u0001\u0000\u0000\u0000\u0012f\u0001\u0000\u0000\u0000\u0014" +
                    "h\u0001\u0000\u0000\u0000\u0016o\u0001\u0000\u0000\u0000\u0018t\u0001" +
                    "\u0000\u0000\u0000\u001ay\u0001\u0000\u0000\u0000\u001c~\u0001\u0000\u0000" +
                    "\u0000\u001e\u0083\u0001\u0000\u0000\u0000 \u0088\u0001\u0000\u0000\u0000" +
                    "\"\u008f\u0001\u0000\u0000\u0000$\u0094\u0001\u0000\u0000\u0000&\u009c" +
                    "\u0001\u0000\u0000\u0000(\u00de\u0001\u0000\u0000\u0000*+\u0005\u0003" +
                    "\u0000\u0000+,\u0005\u0019\u0000\u0000,-\u0005\u0002\u0000\u0000-.\u0006" +
                    "\u0000\uffff\uffff\u0000.\u0001\u0001\u0000\u0000\u0000/4\u0005\u0004" +
                    "\u0000\u000001\u0005\u001c\u0000\u000015\u0006\u0001\uffff\uffff\u0000" +
                    "23\u0005\u001a\u0000\u000035\u0006\u0001\uffff\uffff\u000040\u0001\u0000" +
                    "\u0000\u000042\u0001\u0000\u0000\u000056\u0001\u0000\u0000\u000067\u0005" +
                    "\u0002\u0000\u00007\u0003\u0001\u0000\u0000\u000089\u0005\u0005\u0000" +
                    "\u00009:\u0005\u0002\u0000\u0000:;\u0006\u0002\uffff\uffff\u0000;\u0005" +
                    "\u0001\u0000\u0000\u0000<=\u0005\u0006\u0000\u0000=>\u0005\u001c\u0000" +
                    "\u0000>?\u0005\u0002\u0000\u0000?@\u0006\u0003\uffff\uffff\u0000@\u0007" +
                    "\u0001\u0000\u0000\u0000AB\u0005\u0007\u0000\u0000BC\u0005\u001c\u0000" +
                    "\u0000CD\u0005\u0002\u0000\u0000DE\u0006\u0004\uffff\uffff\u0000E\t\u0001" +
                    "\u0000\u0000\u0000FG\u0005\b\u0000\u0000GH\u0005\u001c\u0000\u0000HI\u0005" +
                    "\u0002\u0000\u0000IJ\u0006\u0005\uffff\uffff\u0000J\u000b\u0001\u0000" +
                    "\u0000\u0000KL\u0005\t\u0000\u0000LM\u0005\u0019\u0000\u0000MN\u0005\u0002" +
                    "\u0000\u0000NO\u0006\u0006\uffff\uffff\u0000O\r\u0001\u0000\u0000\u0000" +
                    "PQ\u0005\n\u0000\u0000QR\u0005\u0018\u0000\u0000RS\u0005\u001b\u0000\u0000" +
                    "ST\u0005\u0019\u0000\u0000TU\u0005\u0002\u0000\u0000UV\u0006\u0007\uffff" +
                    "\uffff\u0000V\u000f\u0001\u0000\u0000\u0000WX\u0005\u000b\u0000\u0000" +
                    "XY\u0005\u0019\u0000\u0000YZ\u0005\u0002\u0000\u0000Z[\u0006\b\uffff\uffff" +
                    "\u0000[\u0011\u0001\u0000\u0000\u0000\\]\u0005\f\u0000\u0000]^\u0005\u0019" +
                    "\u0000\u0000^_\u0005\u001b\u0000\u0000_`\u0005\u0019\u0000\u0000`a\u0005" +
                    "\u0002\u0000\u0000ag\u0006\t\uffff\uffff\u0000bc\u0005\f\u0000\u0000c" +
                    "d\u0005\u0019\u0000\u0000de\u0005\u0002\u0000\u0000eg\u0006\t\uffff\uffff" +
                    "\u0000f\\\u0001\u0000\u0000\u0000fb\u0001\u0000\u0000\u0000g\u0013\u0001" +
                    "\u0000\u0000\u0000hi\u0005\r\u0000\u0000ij\u0005\u0018\u0000\u0000jk\u0005" +
                    "\u001b\u0000\u0000kl\u0005\u0018\u0000\u0000lm\u0005\u0002\u0000\u0000" +
                    "mn\u0006\n\uffff\uffff\u0000n\u0015\u0001\u0000\u0000\u0000op\u0005\u000e" +
                    "\u0000\u0000pq\u0005\u0018\u0000\u0000qr\u0005\u0002\u0000\u0000rs\u0006" +
                    "\u000b\uffff\uffff\u0000s\u0017\u0001\u0000\u0000\u0000tu\u0005\u0010" +
                    "\u0000\u0000uv\u0005\u001c\u0000\u0000vw\u0005\u0002\u0000\u0000wx\u0006" +
                    "\f\uffff\uffff\u0000x\u0019\u0001\u0000\u0000\u0000yz\u0005\u0011\u0000" +
                    "\u0000z{\u0005\u0019\u0000\u0000{|\u0005\u0002\u0000\u0000|}\u0006\r\uffff" +
                    "\uffff\u0000}\u001b\u0001\u0000\u0000\u0000~\u007f\u0005\u0012\u0000\u0000" +
                    "\u007f\u0080\u0005\u0019\u0000\u0000\u0080\u0081\u0005\u0002\u0000\u0000" +
                    "\u0081\u0082\u0006\u000e\uffff\uffff\u0000\u0082\u001d\u0001\u0000\u0000" +
                    "\u0000\u0083\u0084\u0005\u0013\u0000\u0000\u0084\u0085\u0005\u0019\u0000" +
                    "\u0000\u0085\u0086\u0005\u0002\u0000\u0000\u0086\u0087\u0006\u000f\uffff" +
                    "\uffff\u0000\u0087\u001f\u0001\u0000\u0000\u0000\u0088\u0089\u0005\u0014" +
                    "\u0000\u0000\u0089\u008a\u0005\u0019\u0000\u0000\u008a\u008b\u0005\u001b" +
                    "\u0000\u0000\u008b\u008c\u0005\u0018\u0000\u0000\u008c\u008d\u0005\u0002" +
                    "\u0000\u0000\u008d\u008e\u0006\u0010\uffff\uffff\u0000\u008e!\u0001\u0000" +
                    "\u0000\u0000\u008f\u0090\u0005\u0015\u0000\u0000\u0090\u0091\u0005\u0018" +
                    "\u0000\u0000\u0091\u0092\u0005\u0002\u0000\u0000\u0092\u0093\u0006\u0011" +
                    "\uffff\uffff\u0000\u0093#\u0001\u0000\u0000\u0000\u0094\u0095\u0005\u0016" +
                    "\u0000\u0000\u0095\u0096\u0005\u0019\u0000\u0000\u0096\u0097\u0005\u0002" +
                    "\u0000\u0000\u0097\u0098\u0006\u0012\uffff\uffff\u0000\u0098%\u0001\u0000" +
                    "\u0000\u0000\u0099\u009b\u0005\u0002\u0000\u0000\u009a\u0099\u0001\u0000" +
                    "\u0000\u0000\u009b\u009e\u0001\u0000\u0000\u0000\u009c\u009a\u0001\u0000" +
                    "\u0000\u0000\u009c\u009d\u0001\u0000\u0000\u0000\u009d\u009f\u0001\u0000" +
                    "\u0000\u0000\u009e\u009c\u0001\u0000\u0000\u0000\u009f\u00a0\u0003\u0000" +
                    "\u0000\u0000\u00a0\u00d1\u0006\u0013\uffff\uffff\u0000\u00a1\u00a2\u0003" +
                    "\u0002\u0001\u0000\u00a2\u00a3\u0006\u0013\uffff\uffff\u0000\u00a3\u00d2" +
                    "\u0001\u0000\u0000\u0000\u00a4\u00a5\u0003\u0004\u0002\u0000\u00a5\u00a6" +
                    "\u0006\u0013\uffff\uffff\u0000\u00a6\u00d2\u0001\u0000\u0000\u0000\u00a7" +
                    "\u00a8\u0003\u0006\u0003\u0000\u00a8\u00a9\u0006\u0013\uffff\uffff\u0000" +
                    "\u00a9\u00d2\u0001\u0000\u0000\u0000\u00aa\u00ab\u0003\b\u0004\u0000\u00ab" +
                    "\u00ac\u0006\u0013\uffff\uffff\u0000\u00ac\u00d2\u0001\u0000\u0000\u0000" +
                    "\u00ad\u00ae\u0003\n\u0005\u0000\u00ae\u00af\u0006\u0013\uffff\uffff\u0000" +
                    "\u00af\u00d2\u0001\u0000\u0000\u0000\u00b0\u00b1\u0003\f\u0006\u0000\u00b1" +
                    "\u00b2\u0006\u0013\uffff\uffff\u0000\u00b2\u00d2\u0001\u0000\u0000\u0000" +
                    "\u00b3\u00b4\u0003\u000e\u0007\u0000\u00b4\u00b5\u0006\u0013\uffff\uffff" +
                    "\u0000\u00b5\u00d2\u0001\u0000\u0000\u0000\u00b6\u00b7\u0003\u001a\r\u0000" +
                    "\u00b7\u00b8\u0006\u0013\uffff\uffff\u0000\u00b8\u00d2\u0001\u0000\u0000" +
                    "\u0000\u00b9\u00ba\u0003\u001c\u000e\u0000\u00ba\u00bb\u0006\u0013\uffff" +
                    "\uffff\u0000\u00bb\u00d2\u0001\u0000\u0000\u0000\u00bc\u00bd\u0003\u001e" +
                    "\u000f\u0000\u00bd\u00be\u0006\u0013\uffff\uffff\u0000\u00be\u00d2\u0001" +
                    "\u0000\u0000\u0000\u00bf\u00c0\u0003 \u0010\u0000\u00c0\u00c1\u0006\u0013" +
                    "\uffff\uffff\u0000\u00c1\u00d2\u0001\u0000\u0000\u0000\u00c2\u00c3\u0003" +
                    "$\u0012\u0000\u00c3\u00c4\u0006\u0013\uffff\uffff\u0000\u00c4\u00d2\u0001" +
                    "\u0000\u0000\u0000\u00c5\u00c6\u0003\"\u0011\u0000\u00c6\u00c7\u0006\u0013" +
                    "\uffff\uffff\u0000\u00c7\u00d2\u0001\u0000\u0000\u0000\u00c8\u00c9\u0003" +
                    "\u0010\b\u0000\u00c9\u00ca\u0006\u0013\uffff\uffff\u0000\u00ca\u00d2\u0001" +
                    "\u0000\u0000\u0000\u00cb\u00cc\u0003\u0012\t\u0000\u00cc\u00cd\u0006\u0013" +
                    "\uffff\uffff\u0000\u00cd\u00d2\u0001\u0000\u0000\u0000\u00ce\u00cf\u0003" +
                    "\u0016\u000b\u0000\u00cf\u00d0\u0006\u0013\uffff\uffff\u0000\u00d0\u00d2" +
                    "\u0001\u0000\u0000\u0000\u00d1\u00a1\u0001\u0000\u0000\u0000\u00d1\u00a4" +
                    "\u0001\u0000\u0000\u0000\u00d1\u00a7\u0001\u0000\u0000\u0000\u00d1\u00aa" +
                    "\u0001\u0000\u0000\u0000\u00d1\u00ad\u0001\u0000\u0000\u0000\u00d1\u00b0" +
                    "\u0001\u0000\u0000\u0000\u00d1\u00b3\u0001\u0000\u0000\u0000\u00d1\u00b6" +
                    "\u0001\u0000\u0000\u0000\u00d1\u00b9\u0001\u0000\u0000\u0000\u00d1\u00bc" +
                    "\u0001\u0000\u0000\u0000\u00d1\u00bf\u0001\u0000\u0000\u0000\u00d1\u00c2" +
                    "\u0001\u0000\u0000\u0000\u00d1\u00c5\u0001\u0000\u0000\u0000\u00d1\u00c8" +
                    "\u0001\u0000\u0000\u0000\u00d1\u00cb\u0001\u0000\u0000\u0000\u00d1\u00ce" +
                    "\u0001\u0000\u0000\u0000\u00d2\u00d3\u0001\u0000\u0000\u0000\u00d3\u00d1" +
                    "\u0001\u0000\u0000\u0000\u00d3\u00d4\u0001\u0000\u0000\u0000\u00d4\u00d8" +
                    "\u0001\u0000\u0000\u0000\u00d5\u00d7\u0005\u0002\u0000\u0000\u00d6\u00d5" +
                    "\u0001\u0000\u0000\u0000\u00d7\u00da\u0001\u0000\u0000\u0000\u00d8\u00d6" +
                    "\u0001\u0000\u0000\u0000\u00d8\u00d9\u0001\u0000\u0000\u0000\u00d9\'\u0001" +
                    "\u0000\u0000\u0000\u00da\u00d8\u0001\u0000\u0000\u0000\u00db\u00dc\u0003" +
                    "&\u0013\u0000\u00dc\u00dd\u0006\u0014\uffff\uffff\u0000\u00dd\u00df\u0001" +
                    "\u0000\u0000\u0000\u00de\u00db\u0001\u0000\u0000\u0000\u00df\u00e0\u0001" +
                    "\u0000\u0000\u0000\u00e0\u00de\u0001\u0000\u0000\u0000\u00e0\u00e1\u0001" +
                    "\u0000\u0000\u0000\u00e1\u00e5\u0001\u0000\u0000\u0000\u00e2\u00e4\u0005" +
                    "\u0002\u0000\u0000\u00e3\u00e2\u0001\u0000\u0000\u0000\u00e4\u00e7\u0001" +
                    "\u0000\u0000\u0000\u00e5\u00e3\u0001\u0000\u0000\u0000\u00e5\u00e6\u0001" +
                    "\u0000\u0000\u0000\u00e6\u00e8\u0001\u0000\u0000\u0000\u00e7\u00e5\u0001" +
                    "\u0000\u0000\u0000\u00e8\u00e9\u0005\u0000\u0000\u0001\u00e9)\u0001\u0000" +
                    "\u0000\u0000\b4f\u009c\u00d1\u00d3\u00d8\u00e0\u00e5";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}