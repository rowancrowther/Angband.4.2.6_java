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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Activations.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.activation;

import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
import uk.co.jackoftrades.middle.enums.EffectEnum;
import uk.co.jackoftrades.middle.Effect;
import uk.co.jackoftrades.middle.enums.EffectBaseType;
import uk.co.jackoftrades.middle.Expression;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;
import uk.co.jackoftrades.middle.enums.EffectSubTypeEnum;
import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
import uk.co.jackoftrades.middle.enums.EffectNourish;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.enums.EffectEnchant;
import uk.co.jackoftrades.middle.monsters.Summon;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.Activation;

import java.util.List;
import java.util.ArrayList;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ActivationsParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, NAME = 3, AIM = 4, LEVEL = 5, POWER = 6, EFFECT = 7, EFFECT_YX = 8,
            DICE = 9, EXPR = 10, MSG = 11, DESC = 12, NUMBER = 13, UCASE = 14, COLON = 15, EXPR_OPERATORS = 16,
            STRING = 17;
    public static final int
            RULE_name = 0, RULE_aim = 1, RULE_level = 2, RULE_power = 3, RULE_effect = 4,
            RULE_effect_yx = 5, RULE_dice = 6, RULE_expr = 7, RULE_msg = 8, RULE_effect_block = 9,
            RULE_desc = 10, RULE_activation = 11, RULE_file = 12;

    private static String[] makeRuleNames() {
        return new String[]{
                "name", "aim", "level", "power", "effect", "effect_yx", "dice", "expr",
                "msg", "effect_block", "desc", "activation", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'name:'", "'aim:'", "'level:'", "'power:'", "'effect:'",
                "'effect-yx:'", "'dice:'", "'expr:'", "'msg:'", "'desc:'", null, null,
                "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "NAME", "AIM", "LEVEL", "POWER", "EFFECT", "EFFECT_YX",
                "DICE", "EXPR", "MSG", "DESC", "NUMBER", "UCASE", "COLON", "EXPR_OPERATORS",
                "STRING"
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
        return "Activations.g4";
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


    record EffectRecord(EffectEnum type, EffectSubTypeEnum subType, TimedEffect tmdEff,
                        ProjectionEnum projType, EffectNourish nourType, Stats statType,
                        EffectEnchant effEnc, Summon summType, String radiusStr,
                        String parmStr) {
    }

    public ActivationsParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class NameContext extends ParserRuleContext {
        public String nameStr;
        public Token UCASE;

        public TerminalNode NAME() {
            return getToken(ActivationsParser.NAME, 0);
        }

        public TerminalNode UCASE() {
            return getToken(ActivationsParser.UCASE, 0);
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
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(26);
                match(NAME);
                setState(27);
                ((NameContext) _localctx).UCASE = match(UCASE);
                ((NameContext) _localctx).nameStr = ((NameContext) _localctx).UCASE.getText();
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
    public static class AimContext extends ParserRuleContext {
        public boolean aimBool;
        public Token NUMBER;

        public TerminalNode AIM() {
            return getToken(ActivationsParser.AIM, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(ActivationsParser.NUMBER, 0);
        }

        public AimContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_aim;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterAim(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitAim(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitAim(this);
            else return visitor.visitChildren(this);
        }
    }

    public final AimContext aim() throws RecognitionException {
        AimContext _localctx = new AimContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_aim);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(30);
                match(AIM);
                setState(31);
                ((AimContext) _localctx).NUMBER = match(NUMBER);
                ((AimContext) _localctx).aimBool = ((AimContext) _localctx).NUMBER.getText().equals("1");
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
    public static class LevelContext extends ParserRuleContext {
        public int levelInt;
        public Token NUMBER;

        public TerminalNode LEVEL() {
            return getToken(ActivationsParser.LEVEL, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(ActivationsParser.NUMBER, 0);
        }

        public LevelContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_level;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterLevel(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitLevel(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitLevel(this);
            else return visitor.visitChildren(this);
        }
    }

    public final LevelContext level() throws RecognitionException {
        LevelContext _localctx = new LevelContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_level);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(34);
                match(LEVEL);
                setState(35);
                ((LevelContext) _localctx).NUMBER = match(NUMBER);
                ((LevelContext) _localctx).levelInt = Integer.parseInt(((LevelContext) _localctx).NUMBER.getText());
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
    public static class PowerContext extends ParserRuleContext {
        public int powerInt;
        public Token NUMBER;

        public TerminalNode POWER() {
            return getToken(ActivationsParser.POWER, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(ActivationsParser.NUMBER, 0);
        }

        public PowerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_power;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterPower(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitPower(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitPower(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PowerContext power() throws RecognitionException {
        PowerContext _localctx = new PowerContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_power);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(38);
                match(POWER);
                setState(39);
                ((PowerContext) _localctx).NUMBER = match(NUMBER);
                ((PowerContext) _localctx).powerInt = Integer.parseInt(((PowerContext) _localctx).NUMBER.getText());
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
    public static class EffectContext extends ParserRuleContext {
        public EffectRecord effectEntry;
        public Token effType1;
        public Token subType1;
        public Token effRad1;
        public Token effO1;
        public Token effType2;
        public Token subType2;
        public Token effRad2;
        public Token effO2;
        public Token effType3;
        public Token subType3;
        public Token effRad3;
        public Token effType4;
        public Token subType4;
        public Token effType5;

        public TerminalNode EFFECT() {
            return getToken(ActivationsParser.EFFECT, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(ActivationsParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(ActivationsParser.COLON, i);
        }

        public List<TerminalNode> UCASE() {
            return getTokens(ActivationsParser.UCASE);
        }

        public TerminalNode UCASE(int i) {
            return getToken(ActivationsParser.UCASE, i);
        }

        public List<TerminalNode> NUMBER() {
            return getTokens(ActivationsParser.NUMBER);
        }

        public TerminalNode NUMBER(int i) {
            return getToken(ActivationsParser.NUMBER, i);
        }

        public TerminalNode STRING() {
            return getToken(ActivationsParser.STRING, 0);
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
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterEffect(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitEffect(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitEffect(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectContext effect() throws RecognitionException {
        EffectContext _localctx = new EffectContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_effect);

        String effRadStr = "";
        String effParmStr = "";
        EffectEnum effType = EffectEnum.EF_NONE;
        EffectSubTypeEnum subTypeEnum = EffectSubTypeEnum.EST_NONE;
        TimedEffect timedInit = TimedEffect.TMD_NONE;
        ProjectionEnum projInit = ProjectionEnum.PROJ_NONE;
        EffectNourish nourishInit = EffectNourish.EN_NONE;
        Stats statInit = Stats.STAT_NONE;
        EffectEnchant effeInit = EffectEnchant.EE_NONE;
        Summon effSummon = null;

        String entry1 = "";
        String entry2 = "";
        String entry3 = "";
        String entry4 = "";

        try {
            setState(75);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 0, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(42);
                    match(EFFECT);
                    setState(43);
                    ((EffectContext) _localctx).effType1 = match(UCASE);
                    setState(44);
                    match(COLON);
                    setState(45);
                    ((EffectContext) _localctx).subType1 = match(UCASE);
                    setState(46);
                    match(COLON);
                    setState(47);
                    ((EffectContext) _localctx).effRad1 = match(NUMBER);
                    setState(48);
                    match(COLON);
                    setState(49);
                    ((EffectContext) _localctx).effO1 = match(STRING);

                    entry1 = ((EffectContext) _localctx).effType1.getText();
                    entry2 = ((EffectContext) _localctx).subType1.getText();
                    entry3 = ((EffectContext) _localctx).effRad1.getText();
                    entry4 = ((EffectContext) _localctx).effO1.getText();

                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(51);
                    match(EFFECT);
                    setState(52);
                    ((EffectContext) _localctx).effType2 = match(UCASE);
                    setState(53);
                    match(COLON);
                    setState(54);
                    ((EffectContext) _localctx).subType2 = match(UCASE);
                    setState(55);
                    match(COLON);
                    setState(56);
                    ((EffectContext) _localctx).effRad2 = match(NUMBER);
                    setState(57);
                    match(COLON);
                    setState(58);
                    ((EffectContext) _localctx).effO2 = match(NUMBER);

                    entry1 = ((EffectContext) _localctx).effType2.getText();
                    entry2 = ((EffectContext) _localctx).subType2.getText();
                    entry3 = ((EffectContext) _localctx).effRad2.getText();
                    entry4 = ((EffectContext) _localctx).effO2.getText();

                }
                break;
                case 3:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(60);
                    match(EFFECT);
                    setState(61);
                    ((EffectContext) _localctx).effType3 = match(UCASE);
                    setState(62);
                    match(COLON);
                    setState(63);
                    ((EffectContext) _localctx).subType3 = match(UCASE);
                    setState(64);
                    match(COLON);
                    setState(65);
                    ((EffectContext) _localctx).effRad3 = match(NUMBER);

                    entry1 = ((EffectContext) _localctx).effType3.getText();
                    entry2 = ((EffectContext) _localctx).subType3.getText();
                    entry3 = ((EffectContext) _localctx).effRad3.getText();

                }
                break;
                case 4:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(67);
                    match(EFFECT);
                    setState(68);
                    ((EffectContext) _localctx).effType4 = match(UCASE);
                    setState(69);
                    match(COLON);
                    setState(70);
                    ((EffectContext) _localctx).subType4 = match(UCASE);

                    entry1 = ((EffectContext) _localctx).effType4.getText();
                    entry2 = ((EffectContext) _localctx).subType4.getText();

                }
                break;
                case 5:
                    enterOuterAlt(_localctx, 5);
                {
                    setState(72);
                    match(EFFECT);
                    setState(73);
                    ((EffectContext) _localctx).effType5 = match(UCASE);

                    entry1 = ((EffectContext) _localctx).effType5.getText();

                }
                break;
            }
            _ctx.stop = _input.LT(-1);

            effType = EffectEnum.valueOf("EF_" + entry1);
            subTypeEnum = effType.getSubType();

            switch (subTypeEnum) {
                case EST_PROJ:
                    projInit = ProjectionEnum.valueOf("PROJ_" + entry2);
                    break;
                case EST_TMD:
                    timedInit = TimedEffect.valueOf("TMD_" + entry2);
                    break;
                case EST_STAT:
                    statInit = Stats.valueOf("STAT_" + entry2);
                    break;
                case EST_NOURISH:
                    nourishInit = EffectNourish.valueOf("EN_" + entry2);
                    break;
                case EST_ENCHANT:
                    effeInit = EffectEnchant.valueOf("EE_" + entry2);
                    break;
                case EST_SUMMON:
                    effSummon = GameConstants.lookupSummon(entry2);
                    break;
            }

            effRadStr = entry3;
            effParmStr = entry4;

            ((EffectContext) _localctx).effectEntry = new EffectRecord(effType, subTypeEnum, timedInit, projInit,
                    nourishInit, statInit, effeInit, effSummon,
                    effRadStr, effParmStr);

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
    public static class Effect_yxContext extends ParserRuleContext {
        public int yInt;
        public int xInt;
        public Token y;
        public Token x;

        public TerminalNode EFFECT_YX() {
            return getToken(ActivationsParser.EFFECT_YX, 0);
        }

        public TerminalNode COLON() {
            return getToken(ActivationsParser.COLON, 0);
        }

        public List<TerminalNode> NUMBER() {
            return getTokens(ActivationsParser.NUMBER);
        }

        public TerminalNode NUMBER(int i) {
            return getToken(ActivationsParser.NUMBER, i);
        }

        public Effect_yxContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_effect_yx;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterEffect_yx(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitEffect_yx(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitEffect_yx(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Effect_yxContext effect_yx() throws RecognitionException {
        Effect_yxContext _localctx = new Effect_yxContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_effect_yx);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(77);
                match(EFFECT_YX);
                setState(78);
                ((Effect_yxContext) _localctx).y = match(NUMBER);
                setState(79);
                match(COLON);
                setState(80);
                ((Effect_yxContext) _localctx).x = match(NUMBER);

                ((Effect_yxContext) _localctx).yInt = Integer.parseInt(((Effect_yxContext) _localctx).y.getText());
                ((Effect_yxContext) _localctx).xInt = Integer.parseInt(((Effect_yxContext) _localctx).x.getText());

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
        public String diceStr;
        public Token STRING;
        public Token NUMBER;
        public Token UCASE;

        public TerminalNode DICE() {
            return getToken(ActivationsParser.DICE, 0);
        }

        public TerminalNode STRING() {
            return getToken(ActivationsParser.STRING, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(ActivationsParser.NUMBER, 0);
        }

        public TerminalNode UCASE() {
            return getToken(ActivationsParser.UCASE, 0);
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
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterDice(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitDice(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitDice(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DiceContext dice() throws RecognitionException {
        DiceContext _localctx = new DiceContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_dice);
        try {
            setState(92);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 1, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(83);
                    match(DICE);
                    setState(84);
                    ((DiceContext) _localctx).STRING = match(STRING);
                    ((DiceContext) _localctx).diceStr = ((DiceContext) _localctx).STRING.getText();
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(86);
                    match(DICE);
                    setState(87);
                    ((DiceContext) _localctx).NUMBER = match(NUMBER);
                    ((DiceContext) _localctx).diceStr = ((DiceContext) _localctx).NUMBER.getText();
                }
                break;
                case 3:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(89);
                    match(DICE);
                    setState(90);
                    ((DiceContext) _localctx).UCASE = match(UCASE);
                    ((DiceContext) _localctx).diceStr = ((DiceContext) _localctx).UCASE.getText();
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
    public static class ExprContext extends ParserRuleContext {
        public Expression exprObj;
        public Token char_;
        public Token func;
        public Token oper;

        public TerminalNode EXPR() {
            return getToken(ActivationsParser.EXPR, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(ActivationsParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(ActivationsParser.COLON, i);
        }

        public List<TerminalNode> UCASE() {
            return getTokens(ActivationsParser.UCASE);
        }

        public TerminalNode UCASE(int i) {
            return getToken(ActivationsParser.UCASE, i);
        }

        public TerminalNode EXPR_OPERATORS() {
            return getToken(ActivationsParser.EXPR_OPERATORS, 0);
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
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitExpr(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExprContext expr() throws RecognitionException {
        ExprContext _localctx = new ExprContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_expr);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(94);
                match(EXPR);
                setState(95);
                ((ExprContext) _localctx).char_ = match(UCASE);
                setState(96);
                match(COLON);
                setState(97);
                ((ExprContext) _localctx).func = match(UCASE);
                setState(98);
                match(COLON);
                setState(99);
                ((ExprContext) _localctx).oper = match(EXPR_OPERATORS);

                String rawCh = ((ExprContext) _localctx).char_.getText();
                if (rawCh.length() != 1) {
                    String message = "Invalid exression string. expression:" + rawCh + ":" + ((ExprContext) _localctx).func.getText() + ":" + ((ExprContext) _localctx).oper.getText();
                    throw new InvalidTokenFoundDuringParse(message);
                }

                EffectBaseType exp = EffectBaseType.valueOf("EFB_" + ((ExprContext) _localctx).func.getText());
                String operations = ((ExprContext) _localctx).oper.getText();

                ((ExprContext) _localctx).exprObj = new Expression(rawCh.charAt(0), exp, operations);

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
    public static class MsgContext extends ParserRuleContext {
        public String msgStr;
        public Token STRING;

        public TerminalNode MSG() {
            return getToken(ActivationsParser.MSG, 0);
        }

        public TerminalNode STRING() {
            return getToken(ActivationsParser.STRING, 0);
        }

        public MsgContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_msg;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterMsg(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitMsg(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitMsg(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MsgContext msg() throws RecognitionException {
        MsgContext _localctx = new MsgContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_msg);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(102);
                match(MSG);
                setState(103);
                ((MsgContext) _localctx).STRING = match(STRING);
                ((MsgContext) _localctx).msgStr = ((MsgContext) _localctx).STRING.getText();
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
    public static class Effect_blockContext extends ParserRuleContext {
        public Effect effObj;
        public PowerContext power;
        public EffectContext effect;
        public DiceContext dice;
        public ExprContext expr;
        public Effect_yxContext effect_yx;
        public MsgContext msg;

        public PowerContext power() {
            return getRuleContext(PowerContext.class, 0);
        }

        public List<EffectContext> effect() {
            return getRuleContexts(EffectContext.class);
        }

        public EffectContext effect(int i) {
            return getRuleContext(EffectContext.class, i);
        }

        public List<DiceContext> dice() {
            return getRuleContexts(DiceContext.class);
        }

        public DiceContext dice(int i) {
            return getRuleContext(DiceContext.class, i);
        }

        public List<ExprContext> expr() {
            return getRuleContexts(ExprContext.class);
        }

        public ExprContext expr(int i) {
            return getRuleContext(ExprContext.class, i);
        }

        public List<Effect_yxContext> effect_yx() {
            return getRuleContexts(Effect_yxContext.class);
        }

        public Effect_yxContext effect_yx(int i) {
            return getRuleContext(Effect_yxContext.class, i);
        }

        public List<MsgContext> msg() {
            return getRuleContexts(MsgContext.class);
        }

        public MsgContext msg(int i) {
            return getRuleContext(MsgContext.class, i);
        }

        public Effect_blockContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_effect_block;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterEffect_block(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitEffect_block(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitEffect_block(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Effect_blockContext effect_block() throws RecognitionException {
        Effect_blockContext _localctx = new Effect_blockContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_effect_block);

        String effRadStr = "";
        String effParmStr = "";
        EffectEnum effInit = EffectEnum.EF_NONE;
        String diceInit = "";
        int yInit = 0;
        int xInit = 0;
        EffectSubTypeEnum subTypeInit = EffectSubTypeEnum.EST_NONE;
        TimedEffect timedInit = TimedEffect.TMD_NONE;
        ProjectionEnum projInit = ProjectionEnum.PROJ_NONE;
        EffectNourish effNourish = EffectNourish.EN_NONE;
        EffectEnchant effEnchant = EffectEnchant.EE_NONE;
        Summon effSummon = null;
        Stats effStat = Stats.STAT_NONE;
        int powerInit = 0;
        String msgInit = "";
        String visMsgInit = "";
        String timeInit = "";
        Expression exprObj = null;

        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(109);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == POWER) {
                    {
                        setState(106);
                        ((Effect_blockContext) _localctx).power = power();
                        powerInit = ((Effect_blockContext) _localctx).power.powerInt;
                    }
                }

                setState(126);
                _errHandler.sync(this);
                _alt = 1;
                do {
                    switch (_alt) {
                        case 1: {
                            setState(126);
                            _errHandler.sync(this);
                            switch (_input.LA(1)) {
                                case EFFECT: {
                                    setState(111);
                                    ((Effect_blockContext) _localctx).effect = effect();

                                    EffectRecord entry = ((Effect_blockContext) _localctx).effect.effectEntry;
                                    effInit = entry.type();
                                    subTypeInit = entry.subType();
                                    timedInit = entry.tmdEff();
                                    projInit = entry.projType();
                                    effNourish = entry.nourType();
                                    effStat = entry.statType();
                                    effEnchant = entry.effEnc();
                                    effSummon = entry.summType();
                                    effRadStr = entry.radiusStr();
                                    effParmStr = entry.parmStr();

                                }
                                break;
                                case DICE: {
                                    setState(114);
                                    ((Effect_blockContext) _localctx).dice = dice();
                                    diceInit = ((Effect_blockContext) _localctx).dice.diceStr;
                                }
                                break;
                                case EXPR: {
                                    setState(117);
                                    ((Effect_blockContext) _localctx).expr = expr();
                                    exprObj = ((Effect_blockContext) _localctx).expr.exprObj;
                                }
                                break;
                                case EFFECT_YX: {
                                    setState(120);
                                    ((Effect_blockContext) _localctx).effect_yx = effect_yx();
                                    yInit = ((Effect_blockContext) _localctx).effect_yx.yInt;
                                    xInit = ((Effect_blockContext) _localctx).effect_yx.xInt;
                                }
                                break;
                                case MSG: {
                                    setState(123);
                                    ((Effect_blockContext) _localctx).msg = msg();
                                    msgInit = ((Effect_blockContext) _localctx).msg.msgStr;
                                }
                                break;
                                default:
                                    throw new NoViableAltException(this);
                            }
                        }
                        break;
                        default:
                            throw new NoViableAltException(this);
                    }
                    setState(128);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 4, _ctx);
                } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
            }
            _ctx.stop = _input.LT(-1);

            ((Effect_blockContext) _localctx).effObj = new Effect(effInit, diceInit, yInit, xInit, timedInit, projInit,
                    effStat, effNourish, effEnchant, effSummon, effRadStr,
                    effParmStr, powerInit, msgInit, visMsgInit, timeInit,
                    exprObj);

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

        public TerminalNode DESC() {
            return getToken(ActivationsParser.DESC, 0);
        }

        public TerminalNode STRING() {
            return getToken(ActivationsParser.STRING, 0);
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
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterDesc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitDesc(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitDesc(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DescContext desc() throws RecognitionException {
        DescContext _localctx = new DescContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_desc);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(130);
                match(DESC);
                setState(131);
                ((DescContext) _localctx).STRING = match(STRING);
                ((DescContext) _localctx).descStr = ((DescContext) _localctx).STRING.getText();
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
    public static class ActivationContext extends ParserRuleContext {
        public Activation activationRecord;
        public NameContext name;
        public AimContext aim;
        public LevelContext level;
        public Effect_blockContext effect_block;
        public DescContext desc;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public List<AimContext> aim() {
            return getRuleContexts(AimContext.class);
        }

        public AimContext aim(int i) {
            return getRuleContext(AimContext.class, i);
        }

        public List<LevelContext> level() {
            return getRuleContexts(LevelContext.class);
        }

        public LevelContext level(int i) {
            return getRuleContext(LevelContext.class, i);
        }

        public List<Effect_blockContext> effect_block() {
            return getRuleContexts(Effect_blockContext.class);
        }

        public Effect_blockContext effect_block(int i) {
            return getRuleContext(Effect_blockContext.class, i);
        }

        public List<DescContext> desc() {
            return getRuleContexts(DescContext.class);
        }

        public DescContext desc(int i) {
            return getRuleContext(DescContext.class, i);
        }

        public ActivationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_activation;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterActivation(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitActivation(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitActivation(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ActivationContext activation() throws RecognitionException {
        ActivationContext _localctx = new ActivationContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_activation);

        String nameInit = "";
        int indexInit = 0;
        boolean aimInit = false;
        int levelInit = 0;
        int powerInit = 0;
        List<Effect> effectsInit = new ArrayList<>();
        String message = "";
        String descInit = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(134);
                ((ActivationContext) _localctx).name = name();
                nameInit = ((ActivationContext) _localctx).name.nameStr;
                setState(148);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        setState(148);
                        _errHandler.sync(this);
                        switch (_input.LA(1)) {
                            case AIM: {
                                setState(136);
                                ((ActivationContext) _localctx).aim = aim();
                                aimInit = ((ActivationContext) _localctx).aim.aimBool;
                            }
                            break;
                            case LEVEL: {
                                setState(139);
                                ((ActivationContext) _localctx).level = level();
                                levelInit = ((ActivationContext) _localctx).level.levelInt;
                            }
                            break;
                            case POWER:
                            case EFFECT:
                            case EFFECT_YX:
                            case DICE:
                            case EXPR:
                            case MSG: {
                                setState(142);
                                ((ActivationContext) _localctx).effect_block = effect_block();

                                effectsInit.add(((ActivationContext) _localctx).effect_block.effObj);
                                message = ((ActivationContext) _localctx).effect_block.effObj.getMessage();

                            }
                            break;
                            case DESC: {
                                setState(145);
                                ((ActivationContext) _localctx).desc = desc();
                                descInit = ((ActivationContext) _localctx).desc.descStr;
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                    }
                    setState(150);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8176L) != 0));
            }
            _ctx.stop = _input.LT(-1);

            ((ActivationContext) _localctx).activationRecord = new Activation(nameInit, indexInit,
                    aimInit, levelInit,
                    powerInit, effectsInit,
                    message, descInit);

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
        public List<Activation> activations;
        public ActivationContext activation;

        public TerminalNode EOF() {
            return getToken(ActivationsParser.EOF, 0);
        }

        public List<ActivationContext> activation() {
            return getRuleContexts(ActivationContext.class);
        }

        public ActivationContext activation(int i) {
            return getRuleContext(ActivationContext.class, i);
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
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_file);

        ((FileContext) _localctx).activations = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(155);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(152);
                            ((FileContext) _localctx).activation = activation();

                            _localctx.activations.add(((FileContext) _localctx).activation.activationRecord);

                        }
                    }
                    setState(157);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(159);
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
            "\u0004\u0001\u0011\u00a2\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001" +
                    "\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004" +
                    "\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007" +
                    "\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b" +
                    "\u0002\f\u0007\f\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0003\u0004L\b\u0004\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0006\u0003\u0006]\b\u0006\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0003\tn\b\t\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0004\t\u007f\b\t\u000b\t\f" +
                    "\t\u0080\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0004" +
                    "\u000b\u0095\b\u000b\u000b\u000b\f\u000b\u0096\u0001\f\u0001\f\u0001\f" +
                    "\u0004\f\u009c\b\f\u000b\f\f\f\u009d\u0001\f\u0001\f\u0001\f\u0000\u0000" +
                    "\r\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u0000" +
                    "\u0000\u00a5\u0000\u001a\u0001\u0000\u0000\u0000\u0002\u001e\u0001\u0000" +
                    "\u0000\u0000\u0004\"\u0001\u0000\u0000\u0000\u0006&\u0001\u0000\u0000" +
                    "\u0000\bK\u0001\u0000\u0000\u0000\nM\u0001\u0000\u0000\u0000\f\\\u0001" +
                    "\u0000\u0000\u0000\u000e^\u0001\u0000\u0000\u0000\u0010f\u0001\u0000\u0000" +
                    "\u0000\u0012m\u0001\u0000\u0000\u0000\u0014\u0082\u0001\u0000\u0000\u0000" +
                    "\u0016\u0086\u0001\u0000\u0000\u0000\u0018\u009b\u0001\u0000\u0000\u0000" +
                    "\u001a\u001b\u0005\u0003\u0000\u0000\u001b\u001c\u0005\u000e\u0000\u0000" +
                    "\u001c\u001d\u0006\u0000\uffff\uffff\u0000\u001d\u0001\u0001\u0000\u0000" +
                    "\u0000\u001e\u001f\u0005\u0004\u0000\u0000\u001f \u0005\r\u0000\u0000" +
                    " !\u0006\u0001\uffff\uffff\u0000!\u0003\u0001\u0000\u0000\u0000\"#\u0005" +
                    "\u0005\u0000\u0000#$\u0005\r\u0000\u0000$%\u0006\u0002\uffff\uffff\u0000" +
                    "%\u0005\u0001\u0000\u0000\u0000&\'\u0005\u0006\u0000\u0000\'(\u0005\r" +
                    "\u0000\u0000()\u0006\u0003\uffff\uffff\u0000)\u0007\u0001\u0000\u0000" +
                    "\u0000*+\u0005\u0007\u0000\u0000+,\u0005\u000e\u0000\u0000,-\u0005\u000f" +
                    "\u0000\u0000-.\u0005\u000e\u0000\u0000./\u0005\u000f\u0000\u0000/0\u0005" +
                    "\r\u0000\u000001\u0005\u000f\u0000\u000012\u0005\u0011\u0000\u00002L\u0006" +
                    "\u0004\uffff\uffff\u000034\u0005\u0007\u0000\u000045\u0005\u000e\u0000" +
                    "\u000056\u0005\u000f\u0000\u000067\u0005\u000e\u0000\u000078\u0005\u000f" +
                    "\u0000\u000089\u0005\r\u0000\u00009:\u0005\u000f\u0000\u0000:;\u0005\r" +
                    "\u0000\u0000;L\u0006\u0004\uffff\uffff\u0000<=\u0005\u0007\u0000\u0000" +
                    "=>\u0005\u000e\u0000\u0000>?\u0005\u000f\u0000\u0000?@\u0005\u000e\u0000" +
                    "\u0000@A\u0005\u000f\u0000\u0000AB\u0005\r\u0000\u0000BL\u0006\u0004\uffff" +
                    "\uffff\u0000CD\u0005\u0007\u0000\u0000DE\u0005\u000e\u0000\u0000EF\u0005" +
                    "\u000f\u0000\u0000FG\u0005\u000e\u0000\u0000GL\u0006\u0004\uffff\uffff" +
                    "\u0000HI\u0005\u0007\u0000\u0000IJ\u0005\u000e\u0000\u0000JL\u0006\u0004" +
                    "\uffff\uffff\u0000K*\u0001\u0000\u0000\u0000K3\u0001\u0000\u0000\u0000" +
                    "K<\u0001\u0000\u0000\u0000KC\u0001\u0000\u0000\u0000KH\u0001\u0000\u0000" +
                    "\u0000L\t\u0001\u0000\u0000\u0000MN\u0005\b\u0000\u0000NO\u0005\r\u0000" +
                    "\u0000OP\u0005\u000f\u0000\u0000PQ\u0005\r\u0000\u0000QR\u0006\u0005\uffff" +
                    "\uffff\u0000R\u000b\u0001\u0000\u0000\u0000ST\u0005\t\u0000\u0000TU\u0005" +
                    "\u0011\u0000\u0000U]\u0006\u0006\uffff\uffff\u0000VW\u0005\t\u0000\u0000" +
                    "WX\u0005\r\u0000\u0000X]\u0006\u0006\uffff\uffff\u0000YZ\u0005\t\u0000" +
                    "\u0000Z[\u0005\u000e\u0000\u0000[]\u0006\u0006\uffff\uffff\u0000\\S\u0001" +
                    "\u0000\u0000\u0000\\V\u0001\u0000\u0000\u0000\\Y\u0001\u0000\u0000\u0000" +
                    "]\r\u0001\u0000\u0000\u0000^_\u0005\n\u0000\u0000_`\u0005\u000e\u0000" +
                    "\u0000`a\u0005\u000f\u0000\u0000ab\u0005\u000e\u0000\u0000bc\u0005\u000f" +
                    "\u0000\u0000cd\u0005\u0010\u0000\u0000de\u0006\u0007\uffff\uffff\u0000" +
                    "e\u000f\u0001\u0000\u0000\u0000fg\u0005\u000b\u0000\u0000gh\u0005\u0011" +
                    "\u0000\u0000hi\u0006\b\uffff\uffff\u0000i\u0011\u0001\u0000\u0000\u0000" +
                    "jk\u0003\u0006\u0003\u0000kl\u0006\t\uffff\uffff\u0000ln\u0001\u0000\u0000" +
                    "\u0000mj\u0001\u0000\u0000\u0000mn\u0001\u0000\u0000\u0000n~\u0001\u0000" +
                    "\u0000\u0000op\u0003\b\u0004\u0000pq\u0006\t\uffff\uffff\u0000q\u007f" +
                    "\u0001\u0000\u0000\u0000rs\u0003\f\u0006\u0000st\u0006\t\uffff\uffff\u0000" +
                    "t\u007f\u0001\u0000\u0000\u0000uv\u0003\u000e\u0007\u0000vw\u0006\t\uffff" +
                    "\uffff\u0000w\u007f\u0001\u0000\u0000\u0000xy\u0003\n\u0005\u0000yz\u0006" +
                    "\t\uffff\uffff\u0000z\u007f\u0001\u0000\u0000\u0000{|\u0003\u0010\b\u0000" +
                    "|}\u0006\t\uffff\uffff\u0000}\u007f\u0001\u0000\u0000\u0000~o\u0001\u0000" +
                    "\u0000\u0000~r\u0001\u0000\u0000\u0000~u\u0001\u0000\u0000\u0000~x\u0001" +
                    "\u0000\u0000\u0000~{\u0001\u0000\u0000\u0000\u007f\u0080\u0001\u0000\u0000" +
                    "\u0000\u0080~\u0001\u0000\u0000\u0000\u0080\u0081\u0001\u0000\u0000\u0000" +
                    "\u0081\u0013\u0001\u0000\u0000\u0000\u0082\u0083\u0005\f\u0000\u0000\u0083" +
                    "\u0084\u0005\u0011\u0000\u0000\u0084\u0085\u0006\n\uffff\uffff\u0000\u0085" +
                    "\u0015\u0001\u0000\u0000\u0000\u0086\u0087\u0003\u0000\u0000\u0000\u0087" +
                    "\u0094\u0006\u000b\uffff\uffff\u0000\u0088\u0089\u0003\u0002\u0001\u0000" +
                    "\u0089\u008a\u0006\u000b\uffff\uffff\u0000\u008a\u0095\u0001\u0000\u0000" +
                    "\u0000\u008b\u008c\u0003\u0004\u0002\u0000\u008c\u008d\u0006\u000b\uffff" +
                    "\uffff\u0000\u008d\u0095\u0001\u0000\u0000\u0000\u008e\u008f\u0003\u0012" +
                    "\t\u0000\u008f\u0090\u0006\u000b\uffff\uffff\u0000\u0090\u0095\u0001\u0000" +
                    "\u0000\u0000\u0091\u0092\u0003\u0014\n\u0000\u0092\u0093\u0006\u000b\uffff" +
                    "\uffff\u0000\u0093\u0095\u0001\u0000\u0000\u0000\u0094\u0088\u0001\u0000" +
                    "\u0000\u0000\u0094\u008b\u0001\u0000\u0000\u0000\u0094\u008e\u0001\u0000" +
                    "\u0000\u0000\u0094\u0091\u0001\u0000\u0000\u0000\u0095\u0096\u0001\u0000" +
                    "\u0000\u0000\u0096\u0094\u0001\u0000\u0000\u0000\u0096\u0097\u0001\u0000" +
                    "\u0000\u0000\u0097\u0017\u0001\u0000\u0000\u0000\u0098\u0099\u0003\u0016" +
                    "\u000b\u0000\u0099\u009a\u0006\f\uffff\uffff\u0000\u009a\u009c\u0001\u0000" +
                    "\u0000\u0000\u009b\u0098\u0001\u0000\u0000\u0000\u009c\u009d\u0001\u0000" +
                    "\u0000\u0000\u009d\u009b\u0001\u0000\u0000\u0000\u009d\u009e\u0001\u0000" +
                    "\u0000\u0000\u009e\u009f\u0001\u0000\u0000\u0000\u009f\u00a0\u0005\u0000" +
                    "\u0000\u0001\u00a0\u0019\u0001\u0000\u0000\u0000\bK\\m~\u0080\u0094\u0096" +
                    "\u009d";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}