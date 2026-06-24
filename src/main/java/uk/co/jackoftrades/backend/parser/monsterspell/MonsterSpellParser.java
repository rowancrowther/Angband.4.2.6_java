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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/MonsterSpell.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.monsterspell;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
import uk.co.jackoftrades.middle.effect.*;
import uk.co.jackoftrades.middle.enums.*;
import uk.co.jackoftrades.middle.monsters.MonsterSpellLevel;
import uk.co.jackoftrades.middle.monsters.MonsterSpellType;
import uk.co.jackoftrades.middle.monsters.enums.MonTimed;
import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class MonsterSpellParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, NAME = 3, MSGT = 4, HIT = 5, EFFECT = 6, EFFECT_YX = 7, DICE = 8,
            EXPR = 9, POWER_CUTOFF = 10, LORE = 11, LORE_COLOUR_BASE = 12, LORE_COLOUR_RESIST = 13,
            LORE_COLOUR_IMMUNE = 14, MESSAGE_SAVE = 15, MESSAGE_VIS = 16, MESSAGE_INVIS = 17,
            MESSAGE_MISS = 18, INTEGER = 19, UCASE = 20, LCASE = 21, COLON = 22;
    public static final int
            RULE_name = 0, RULE_msgt = 1, RULE_hit = 2, RULE_effect = 3, RULE_effectYX = 4,
            RULE_dice = 5, RULE_expr = 6, RULE_effectBlock = 7, RULE_powerCutoff = 8,
            RULE_lore = 9, RULE_loreColourBase = 10, RULE_loreColourResist = 11, RULE_loreColourImmune = 12,
            RULE_messageSave = 13, RULE_messageVis = 14, RULE_messageInvis = 15, RULE_messageMiss = 16,
            RULE_monsterSpellLevel = 17, RULE_monsterSpellType = 18, RULE_file = 19;

    private static String[] makeRuleNames() {
        return new String[]{
                "name", "msgt", "hit", "effect", "effectYX", "dice", "expr", "effectBlock",
                "powerCutoff", "lore", "loreColourBase", "loreColourResist", "loreColourImmune",
                "messageSave", "messageVis", "messageInvis", "messageMiss", "monsterSpellLevel",
                "monsterSpellType", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'name:'", "'msgt:'", "'hit:'", "'effect:'", "'effect-yx:'",
                null, null, "'power-cutoff:'", "'lore:'", "'lore-color-base:'", "'lore-color-resist:'",
                "'lore-color-immune:'", "'message-save:'", "'message-vis:'", "'message-invis:'",
                "'message-miss:'", null, null, null, "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "NAME", "MSGT", "HIT", "EFFECT", "EFFECT_YX",
                "DICE", "EXPR", "POWER_CUTOFF", "LORE", "LORE_COLOUR_BASE", "LORE_COLOUR_RESIST",
                "LORE_COLOUR_IMMUNE", "MESSAGE_SAVE", "MESSAGE_VIS", "MESSAGE_INVIS",
                "MESSAGE_MISS", "INTEGER", "UCASE", "LCASE", "COLON"
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
        return "MonsterSpell.g4";
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


    private static final Logger logger = LogManager.getLogger();

    public MonsterSpellParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class NameContext extends ParserRuleContext {
        public String nameStr;
        public Token UCASE;

        public TerminalNode NAME() {
            return getToken(MonsterSpellParser.NAME, 0);
        }

        public TerminalNode UCASE() {
            return getToken(MonsterSpellParser.UCASE, 0);
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
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterSpellVisitor)
                return ((MonsterSpellVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(40);
                match(NAME);
                setState(41);
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
    public static class MsgtContext extends ParserRuleContext {
        public MessageType msgtEnum;
        public Token UCASE;

        public TerminalNode MSGT() {
            return getToken(MonsterSpellParser.MSGT, 0);
        }

        public TerminalNode UCASE() {
            return getToken(MonsterSpellParser.UCASE, 0);
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
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).enterMsgt(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).exitMsgt(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterSpellVisitor)
                return ((MonsterSpellVisitor<? extends T>) visitor).visitMsgt(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MsgtContext msgt() throws RecognitionException {
        MsgtContext _localctx = new MsgtContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_msgt);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(44);
                match(MSGT);
                setState(45);
                ((MsgtContext) _localctx).UCASE = match(UCASE);

                String raw = ((MsgtContext) _localctx).UCASE.getText();
                ((MsgtContext) _localctx).msgtEnum = MessageType.valueOf("MSG_" + raw);

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
    public static class HitContext extends ParserRuleContext {
        public int hitInt;
        public Token INTEGER;

        public TerminalNode HIT() {
            return getToken(MonsterSpellParser.HIT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(MonsterSpellParser.INTEGER, 0);
        }

        public HitContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_hit;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).enterHit(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).exitHit(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterSpellVisitor)
                return ((MonsterSpellVisitor<? extends T>) visitor).visitHit(this);
            else return visitor.visitChildren(this);
        }
    }

    public final HitContext hit() throws RecognitionException {
        HitContext _localctx = new HitContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_hit);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(48);
                match(HIT);
                setState(49);
                ((HitContext) _localctx).INTEGER = match(INTEGER);
                ((HitContext) _localctx).hitInt = Integer.parseInt(((HitContext) _localctx).INTEGER.getText());
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
        public EffectEnum parm1;
        public String parm2;
        public String parm3;
        public String parm4;
        public Token pa1;
        public Token pa2;
        public Token pa3;
        public Token pa4;
        public Token pb1;
        public Token pb2;
        public Token pb3;
        public Token pc1;
        public Token pc2;
        public Token pd1;

        public TerminalNode EFFECT() {
            return getToken(MonsterSpellParser.EFFECT, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(MonsterSpellParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(MonsterSpellParser.COLON, i);
        }

        public List<TerminalNode> UCASE() {
            return getTokens(MonsterSpellParser.UCASE);
        }

        public TerminalNode UCASE(int i) {
            return getToken(MonsterSpellParser.UCASE, i);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(MonsterSpellParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(MonsterSpellParser.INTEGER, i);
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
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).enterEffect(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).exitEffect(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterSpellVisitor)
                return ((MonsterSpellVisitor<? extends T>) visitor).visitEffect(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectContext effect() throws RecognitionException {
        EffectContext _localctx = new EffectContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_effect);

        String eeRaw = "";
        ((EffectContext) _localctx).parm2 = "";
        ((EffectContext) _localctx).parm3 = "";
        ((EffectContext) _localctx).parm4 = "";

        try {
            setState(76);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 0, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(52);
                    match(EFFECT);
                    setState(53);
                    ((EffectContext) _localctx).pa1 = match(UCASE);
                    setState(54);
                    match(COLON);
                    setState(55);
                    ((EffectContext) _localctx).pa2 = match(UCASE);
                    setState(56);
                    match(COLON);
                    setState(57);
                    ((EffectContext) _localctx).pa3 = match(INTEGER);
                    setState(58);
                    match(COLON);
                    setState(59);
                    ((EffectContext) _localctx).pa4 = match(INTEGER);

                    eeRaw = ((EffectContext) _localctx).pa1.getText();
                    ((EffectContext) _localctx).parm2 = ((EffectContext) _localctx).pa2.getText();
                    ((EffectContext) _localctx).parm3 = ((EffectContext) _localctx).pa3.getText();
                    ((EffectContext) _localctx).parm4 = ((EffectContext) _localctx).pa4.getText();

                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(61);
                    match(EFFECT);
                    setState(62);
                    ((EffectContext) _localctx).pb1 = match(UCASE);
                    setState(63);
                    match(COLON);
                    setState(64);
                    ((EffectContext) _localctx).pb2 = match(UCASE);
                    setState(65);
                    match(COLON);
                    setState(66);
                    ((EffectContext) _localctx).pb3 = match(INTEGER);

                    eeRaw = ((EffectContext) _localctx).pb1.getText();
                    ((EffectContext) _localctx).parm2 = ((EffectContext) _localctx).pb2.getText();
                    ((EffectContext) _localctx).parm3 = ((EffectContext) _localctx).pb3.getText();

                }
                break;
                case 3:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(68);
                    match(EFFECT);
                    setState(69);
                    ((EffectContext) _localctx).pc1 = match(UCASE);
                    setState(70);
                    match(COLON);
                    setState(71);
                    ((EffectContext) _localctx).pc2 = match(UCASE);

                    eeRaw = ((EffectContext) _localctx).pc1.getText();
                    ((EffectContext) _localctx).parm2 = ((EffectContext) _localctx).pc2.getText();

                }
                break;
                case 4:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(73);
                    match(EFFECT);
                    setState(74);
                    ((EffectContext) _localctx).pd1 = match(UCASE);
                    eeRaw = ((EffectContext) _localctx).pd1.getText();
                }
                break;
            }
            _ctx.stop = _input.LT(-1);

            if (!eeRaw.isEmpty())
                ((EffectContext) _localctx).parm1 = EffectEnum.valueOf("EF_" + eeRaw);

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
            return getToken(MonsterSpellParser.EFFECT_YX, 0);
        }

        public TerminalNode COLON() {
            return getToken(MonsterSpellParser.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(MonsterSpellParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(MonsterSpellParser.INTEGER, i);
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
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).enterEffectYX(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).exitEffectYX(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterSpellVisitor)
                return ((MonsterSpellVisitor<? extends T>) visitor).visitEffectYX(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectYXContext effectYX() throws RecognitionException {
        EffectYXContext _localctx = new EffectYXContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_effectYX);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(78);
                match(EFFECT_YX);
                setState(79);
                ((EffectYXContext) _localctx).yInt = match(INTEGER);
                setState(80);
                match(COLON);
                setState(81);
                ((EffectYXContext) _localctx).xInt = match(INTEGER);

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
    public static class DiceContext extends ParserRuleContext {
        public String diceString;
        public Token DICE;

        public TerminalNode DICE() {
            return getToken(MonsterSpellParser.DICE, 0);
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
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).enterDice(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).exitDice(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterSpellVisitor)
                return ((MonsterSpellVisitor<? extends T>) visitor).visitDice(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DiceContext dice() throws RecognitionException {
        DiceContext _localctx = new DiceContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_dice);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(84);
                ((DiceContext) _localctx).DICE = match(DICE);

                ((DiceContext) _localctx).diceString = ((DiceContext) _localctx).DICE.getText().substring(5);

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
        public Expression e;
        public Token EXPR;

        public TerminalNode EXPR() {
            return getToken(MonsterSpellParser.EXPR, 0);
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
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).enterExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).exitExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterSpellVisitor)
                return ((MonsterSpellVisitor<? extends T>) visitor).visitExpr(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExprContext expr() throws RecognitionException {
        ExprContext _localctx = new ExprContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_expr);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(87);
                ((ExprContext) _localctx).EXPR = match(EXPR);

                // Set the defaults
                char defChar = 'D';
                EffectBaseType defType = EffectBaseType.EFB_NONE;
                String defOperation = "";

                String[] exprStrings = ((ExprContext) _localctx).EXPR.getText().split(":");
                // Ignore the first string
                if (exprStrings.length > 1)
                    defChar = exprStrings[1].charAt(0);
                if (exprStrings.length > 2)
                    defType = EffectBaseType.valueOf("EFB_" + exprStrings[2]);
                if (exprStrings.length > 3)
                    defOperation = exprStrings[3];

                ((ExprContext) _localctx).e = new Expression(defChar, defType, defOperation);

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
        public Effect eff;
        public EffectContext effect;
        public EffectYXContext effectYX;
        public DiceContext dice;
        public ExprContext expr;

        public EffectContext effect() {
            return getRuleContext(EffectContext.class, 0);
        }

        public EffectYXContext effectYX() {
            return getRuleContext(EffectYXContext.class, 0);
        }

        public DiceContext dice() {
            return getRuleContext(DiceContext.class, 0);
        }

        public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
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
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).enterEffectBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).exitEffectBlock(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterSpellVisitor)
                return ((MonsterSpellVisitor<? extends T>) visitor).visitEffectBlock(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectBlockContext effectBlock() throws RecognitionException {
        EffectBlockContext _localctx = new EffectBlockContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_effectBlock);

        int yInit = 0;
        int xInit = 0;
        String diceStringInit = "";
        Expression expInit = null;
        EffectEnum parm1Init = EffectEnum.EF_NONE;
        String parm2Init = "";
        EffectSubTypeWrapper wrapperInit = null;
        String radiusInit = "";
        String parm4Init = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(90);
                ((EffectBlockContext) _localctx).effect = effect();

                parm1Init = ((EffectBlockContext) _localctx).effect.parm1;
                parm2Init = ((EffectBlockContext) _localctx).effect.parm2;
                radiusInit = ((EffectBlockContext) _localctx).effect.parm3;
                parm4Init = ((EffectBlockContext) _localctx).effect.parm4;

                setState(95);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == EFFECT_YX) {
                    {
                        setState(92);
                        ((EffectBlockContext) _localctx).effectYX = effectYX();

                        yInit = ((EffectBlockContext) _localctx).effectYX.y;
                        xInit = ((EffectBlockContext) _localctx).effectYX.x;

                    }
                }

                setState(100);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == DICE) {
                    {
                        setState(97);
                        ((EffectBlockContext) _localctx).dice = dice();

                        diceStringInit = ((EffectBlockContext) _localctx).dice.diceString;

                    }
                }

                setState(105);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == EXPR) {
                    {
                        setState(102);
                        ((EffectBlockContext) _localctx).expr = expr();

                        expInit = ((EffectBlockContext) _localctx).expr.e;

                    }
                }

            }
            _ctx.stop = _input.LT(-1);

            if (!parm2Init.isEmpty()) {
                // Create the wrapper
                switch (parm1Init.getSubType()) {
                    case EST_PROJ:
                        wrapperInit = new EffectSubTypeWrapper(ProjectionEnum.valueOf("PROJ_" + parm2Init));
                        break;

                    case EST_TMD:
                        wrapperInit = new EffectSubTypeWrapper(TimedEffect.valueOf("TMD_" + parm2Init));
                        break;

                    case EST_NOURISH:
                        wrapperInit = new EffectSubTypeWrapper(EffectNourish.valueOf("EN_" + parm2Init));
                        break;

                    case EST_MON_TMD:
                        wrapperInit = new EffectSubTypeWrapper(MonTimed.valueOf("MON_TMD_" + parm2Init));
                        break;

                    case EST_SUMMON:
                        wrapperInit = new EffectSubTypeWrapper(SummonType.valueOf("SUM_" + parm2Init));
                        break;

                    case EST_STAT:
                        wrapperInit = new EffectSubTypeWrapper(Stats.valueOf("STAT_" + parm2Init));
                        break;

                    case EST_ENCHANT:
                        wrapperInit = new EffectSubTypeWrapper(EffectEnchant.valueOf("EE_" + parm2Init));
                        break;

                    case EST_EARTHQUAKE:
                        wrapperInit = new EffectSubTypeWrapper(Earthquake.valueOf("QUAKE_" + parm2Init));
                        break;

                    case EST_GLYPH:
                        wrapperInit = new EffectSubTypeWrapper(GlyphType.valueOf("GLYPH_" + parm2Init));
                        break;

                    case EST_TELEPORT:
                        wrapperInit = new EffectSubTypeWrapper(TeleportEnum.valueOf("TELE_TELEPORT_" + parm2Init), false);
                        break;

                    case EST_TELEPORT_TO:
                        wrapperInit = new EffectSubTypeWrapper(TeleportEnum.valueOf("TELE_TELEPORT_" + parm2Init), true);
                        break;

                    default:
                        logger.error("Invalid subtype found in Effect Block.");
                }
            }

            Random radiusRnd = Random.parseStr(radiusInit);

            ((EffectBlockContext) _localctx).eff = new Effect(parm1Init, yInit, xInit, diceStringInit,
                    parm1Init.getSubType(), wrapperInit,
                    radiusRnd, parm4Init, expInit);

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
    public static class PowerCutoffContext extends ParserRuleContext {
        public int powerInt;
        public Token INTEGER;

        public TerminalNode POWER_CUTOFF() {
            return getToken(MonsterSpellParser.POWER_CUTOFF, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(MonsterSpellParser.INTEGER, 0);
        }

        public PowerCutoffContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_powerCutoff;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).enterPowerCutoff(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).exitPowerCutoff(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterSpellVisitor)
                return ((MonsterSpellVisitor<? extends T>) visitor).visitPowerCutoff(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PowerCutoffContext powerCutoff() throws RecognitionException {
        PowerCutoffContext _localctx = new PowerCutoffContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_powerCutoff);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(107);
                match(POWER_CUTOFF);
                setState(108);
                ((PowerCutoffContext) _localctx).INTEGER = match(INTEGER);

                ((PowerCutoffContext) _localctx).powerInt = Integer.parseInt(((PowerCutoffContext) _localctx).INTEGER.getText());

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
    public static class LoreContext extends ParserRuleContext {
        public String loreStr;
        public Token LCASE;

        public TerminalNode LORE() {
            return getToken(MonsterSpellParser.LORE, 0);
        }

        public TerminalNode LCASE() {
            return getToken(MonsterSpellParser.LCASE, 0);
        }

        public LoreContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_lore;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).enterLore(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).exitLore(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterSpellVisitor)
                return ((MonsterSpellVisitor<? extends T>) visitor).visitLore(this);
            else return visitor.visitChildren(this);
        }
    }

    public final LoreContext lore() throws RecognitionException {
        LoreContext _localctx = new LoreContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_lore);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(111);
                match(LORE);
                setState(112);
                ((LoreContext) _localctx).LCASE = match(LCASE);
                ((LoreContext) _localctx).loreStr = ((LoreContext) _localctx).LCASE.getText();
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
    public static class LoreColourBaseContext extends ParserRuleContext {
        public ColourType baseCol;
        public Token LCASE;

        public TerminalNode LORE_COLOUR_BASE() {
            return getToken(MonsterSpellParser.LORE_COLOUR_BASE, 0);
        }

        public TerminalNode LCASE() {
            return getToken(MonsterSpellParser.LCASE, 0);
        }

        public LoreColourBaseContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_loreColourBase;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).enterLoreColourBase(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).exitLoreColourBase(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterSpellVisitor)
                return ((MonsterSpellVisitor<? extends T>) visitor).visitLoreColourBase(this);
            else return visitor.visitChildren(this);
        }
    }

    public final LoreColourBaseContext loreColourBase() throws RecognitionException {
        LoreColourBaseContext _localctx = new LoreColourBaseContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_loreColourBase);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(115);
                match(LORE_COLOUR_BASE);
                setState(116);
                ((LoreColourBaseContext) _localctx).LCASE = match(LCASE);

                ((LoreColourBaseContext) _localctx).baseCol = ColourType.findColourType(((LoreColourBaseContext) _localctx).LCASE.getText());

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
    public static class LoreColourResistContext extends ParserRuleContext {
        public ColourType resCol;
        public Token LCASE;

        public TerminalNode LORE_COLOUR_RESIST() {
            return getToken(MonsterSpellParser.LORE_COLOUR_RESIST, 0);
        }

        public TerminalNode LCASE() {
            return getToken(MonsterSpellParser.LCASE, 0);
        }

        public LoreColourResistContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_loreColourResist;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).enterLoreColourResist(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).exitLoreColourResist(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterSpellVisitor)
                return ((MonsterSpellVisitor<? extends T>) visitor).visitLoreColourResist(this);
            else return visitor.visitChildren(this);
        }
    }

    public final LoreColourResistContext loreColourResist() throws RecognitionException {
        LoreColourResistContext _localctx = new LoreColourResistContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_loreColourResist);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(119);
                match(LORE_COLOUR_RESIST);
                setState(120);
                ((LoreColourResistContext) _localctx).LCASE = match(LCASE);

                ((LoreColourResistContext) _localctx).resCol = ColourType.findColourType(((LoreColourResistContext) _localctx).LCASE.getText());

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
    public static class LoreColourImmuneContext extends ParserRuleContext {
        public ColourType immCol;
        public Token LCASE;

        public TerminalNode LORE_COLOUR_IMMUNE() {
            return getToken(MonsterSpellParser.LORE_COLOUR_IMMUNE, 0);
        }

        public TerminalNode LCASE() {
            return getToken(MonsterSpellParser.LCASE, 0);
        }

        public LoreColourImmuneContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_loreColourImmune;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).enterLoreColourImmune(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).exitLoreColourImmune(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterSpellVisitor)
                return ((MonsterSpellVisitor<? extends T>) visitor).visitLoreColourImmune(this);
            else return visitor.visitChildren(this);
        }
    }

    public final LoreColourImmuneContext loreColourImmune() throws RecognitionException {
        LoreColourImmuneContext _localctx = new LoreColourImmuneContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_loreColourImmune);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(123);
                match(LORE_COLOUR_IMMUNE);
                setState(124);
                ((LoreColourImmuneContext) _localctx).LCASE = match(LCASE);

                ((LoreColourImmuneContext) _localctx).immCol = ColourType.findColourType(((LoreColourImmuneContext) _localctx).LCASE.getText());

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
    public static class MessageSaveContext extends ParserRuleContext {
        public String saveMsgStr;
        public Token LCASE;

        public TerminalNode MESSAGE_SAVE() {
            return getToken(MonsterSpellParser.MESSAGE_SAVE, 0);
        }

        public TerminalNode LCASE() {
            return getToken(MonsterSpellParser.LCASE, 0);
        }

        public MessageSaveContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_messageSave;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).enterMessageSave(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).exitMessageSave(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterSpellVisitor)
                return ((MonsterSpellVisitor<? extends T>) visitor).visitMessageSave(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MessageSaveContext messageSave() throws RecognitionException {
        MessageSaveContext _localctx = new MessageSaveContext(_ctx, getState());
        enterRule(_localctx, 26, RULE_messageSave);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(127);
                match(MESSAGE_SAVE);
                setState(128);
                ((MessageSaveContext) _localctx).LCASE = match(LCASE);

                ((MessageSaveContext) _localctx).saveMsgStr = ((MessageSaveContext) _localctx).LCASE.getText();

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
    public static class MessageVisContext extends ParserRuleContext {
        public String visMsgStr;
        public Token LCASE;

        public TerminalNode MESSAGE_VIS() {
            return getToken(MonsterSpellParser.MESSAGE_VIS, 0);
        }

        public TerminalNode LCASE() {
            return getToken(MonsterSpellParser.LCASE, 0);
        }

        public MessageVisContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_messageVis;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).enterMessageVis(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).exitMessageVis(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterSpellVisitor)
                return ((MonsterSpellVisitor<? extends T>) visitor).visitMessageVis(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MessageVisContext messageVis() throws RecognitionException {
        MessageVisContext _localctx = new MessageVisContext(_ctx, getState());
        enterRule(_localctx, 28, RULE_messageVis);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(131);
                match(MESSAGE_VIS);
                setState(132);
                ((MessageVisContext) _localctx).LCASE = match(LCASE);

                ((MessageVisContext) _localctx).visMsgStr = ((MessageVisContext) _localctx).LCASE.getText();

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
    public static class MessageInvisContext extends ParserRuleContext {
        public String invisMsgStr;
        public Token LCASE;

        public TerminalNode MESSAGE_INVIS() {
            return getToken(MonsterSpellParser.MESSAGE_INVIS, 0);
        }

        public TerminalNode LCASE() {
            return getToken(MonsterSpellParser.LCASE, 0);
        }

        public MessageInvisContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_messageInvis;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).enterMessageInvis(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).exitMessageInvis(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterSpellVisitor)
                return ((MonsterSpellVisitor<? extends T>) visitor).visitMessageInvis(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MessageInvisContext messageInvis() throws RecognitionException {
        MessageInvisContext _localctx = new MessageInvisContext(_ctx, getState());
        enterRule(_localctx, 30, RULE_messageInvis);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(135);
                match(MESSAGE_INVIS);
                setState(136);
                ((MessageInvisContext) _localctx).LCASE = match(LCASE);

                ((MessageInvisContext) _localctx).invisMsgStr = ((MessageInvisContext) _localctx).LCASE.getText();

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
    public static class MessageMissContext extends ParserRuleContext {
        public String missMsgStr;
        public Token LCASE;

        public TerminalNode MESSAGE_MISS() {
            return getToken(MonsterSpellParser.MESSAGE_MISS, 0);
        }

        public TerminalNode LCASE() {
            return getToken(MonsterSpellParser.LCASE, 0);
        }

        public MessageMissContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_messageMiss;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).enterMessageMiss(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).exitMessageMiss(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterSpellVisitor)
                return ((MonsterSpellVisitor<? extends T>) visitor).visitMessageMiss(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MessageMissContext messageMiss() throws RecognitionException {
        MessageMissContext _localctx = new MessageMissContext(_ctx, getState());
        enterRule(_localctx, 32, RULE_messageMiss);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(139);
                match(MESSAGE_MISS);
                setState(140);
                ((MessageMissContext) _localctx).LCASE = match(LCASE);

                ((MessageMissContext) _localctx).missMsgStr = ((MessageMissContext) _localctx).LCASE.getText();

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
    public static class MonsterSpellLevelContext extends ParserRuleContext {
        public MonsterSpellLevel spellLevel;
        public PowerCutoffContext powerCutoff;
        public LoreContext lore;
        public LoreColourBaseContext loreColourBase;
        public LoreColourResistContext loreColourResist;
        public LoreColourImmuneContext loreColourImmune;
        public MessageVisContext messageVis;
        public MessageInvisContext messageInvis;
        public MessageMissContext messageMiss;
        public MessageSaveContext messageSave;

        public PowerCutoffContext powerCutoff() {
            return getRuleContext(PowerCutoffContext.class, 0);
        }

        public List<LoreContext> lore() {
            return getRuleContexts(LoreContext.class);
        }

        public LoreContext lore(int i) {
            return getRuleContext(LoreContext.class, i);
        }

        public List<LoreColourBaseContext> loreColourBase() {
            return getRuleContexts(LoreColourBaseContext.class);
        }

        public LoreColourBaseContext loreColourBase(int i) {
            return getRuleContext(LoreColourBaseContext.class, i);
        }

        public List<LoreColourResistContext> loreColourResist() {
            return getRuleContexts(LoreColourResistContext.class);
        }

        public LoreColourResistContext loreColourResist(int i) {
            return getRuleContext(LoreColourResistContext.class, i);
        }

        public List<LoreColourImmuneContext> loreColourImmune() {
            return getRuleContexts(LoreColourImmuneContext.class);
        }

        public LoreColourImmuneContext loreColourImmune(int i) {
            return getRuleContext(LoreColourImmuneContext.class, i);
        }

        public List<MessageVisContext> messageVis() {
            return getRuleContexts(MessageVisContext.class);
        }

        public MessageVisContext messageVis(int i) {
            return getRuleContext(MessageVisContext.class, i);
        }

        public List<MessageInvisContext> messageInvis() {
            return getRuleContexts(MessageInvisContext.class);
        }

        public MessageInvisContext messageInvis(int i) {
            return getRuleContext(MessageInvisContext.class, i);
        }

        public List<MessageMissContext> messageMiss() {
            return getRuleContexts(MessageMissContext.class);
        }

        public MessageMissContext messageMiss(int i) {
            return getRuleContext(MessageMissContext.class, i);
        }

        public List<MessageSaveContext> messageSave() {
            return getRuleContexts(MessageSaveContext.class);
        }

        public MessageSaveContext messageSave(int i) {
            return getRuleContext(MessageSaveContext.class, i);
        }

        public MonsterSpellLevelContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_monsterSpellLevel;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener)
                ((MonsterSpellListener) listener).enterMonsterSpellLevel(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).exitMonsterSpellLevel(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterSpellVisitor)
                return ((MonsterSpellVisitor<? extends T>) visitor).visitMonsterSpellLevel(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MonsterSpellLevelContext monsterSpellLevel() throws RecognitionException {
        MonsterSpellLevelContext _localctx = new MonsterSpellLevelContext(_ctx, getState());
        enterRule(_localctx, 34, RULE_monsterSpellLevel);

        int powerCutOffInit = 0;
        String loreDescInit = "";
        ColourType loreColTypeInit = ColourType.COLOUR_TYPE_DARK;
        ColourType loreResistColTypeInit = ColourType.COLOUR_TYPE_DARK;
        ColourType loreImmuneColTypeInit = ColourType.COLOUR_TYPE_DARK;
        String messageInit = "";
        String blindMessageInit = "";
        String missMessageInit = "";
        String saveMessageInit = "";

        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(146);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == POWER_CUTOFF) {
                    {
                        setState(143);
                        ((MonsterSpellLevelContext) _localctx).powerCutoff = powerCutoff();

                        powerCutOffInit = ((MonsterSpellLevelContext) _localctx).powerCutoff.powerInt;

                    }
                }

                setState(172);
                _errHandler.sync(this);
                _alt = 1;
                do {
                    switch (_alt) {
                        case 1: {
                            setState(172);
                            _errHandler.sync(this);
                            switch (_input.LA(1)) {
                                case LORE: {
                                    setState(148);
                                    ((MonsterSpellLevelContext) _localctx).lore = lore();

                                    loreDescInit = loreDescInit + ((MonsterSpellLevelContext) _localctx).lore.loreStr;

                                }
                                break;
                                case LORE_COLOUR_BASE: {
                                    setState(151);
                                    ((MonsterSpellLevelContext) _localctx).loreColourBase = loreColourBase();

                                    loreColTypeInit = ((MonsterSpellLevelContext) _localctx).loreColourBase.baseCol;

                                }
                                break;
                                case LORE_COLOUR_RESIST: {
                                    setState(154);
                                    ((MonsterSpellLevelContext) _localctx).loreColourResist = loreColourResist();

                                    loreResistColTypeInit = ((MonsterSpellLevelContext) _localctx).loreColourResist.resCol;

                                }
                                break;
                                case LORE_COLOUR_IMMUNE: {
                                    setState(157);
                                    ((MonsterSpellLevelContext) _localctx).loreColourImmune = loreColourImmune();

                                    loreImmuneColTypeInit = ((MonsterSpellLevelContext) _localctx).loreColourImmune.immCol;

                                }
                                break;
                                case MESSAGE_VIS: {
                                    setState(160);
                                    ((MonsterSpellLevelContext) _localctx).messageVis = messageVis();

                                    messageInit = messageInit + ((MonsterSpellLevelContext) _localctx).messageVis.visMsgStr;

                                }
                                break;
                                case MESSAGE_INVIS: {
                                    setState(163);
                                    ((MonsterSpellLevelContext) _localctx).messageInvis = messageInvis();

                                    blindMessageInit = blindMessageInit + ((MonsterSpellLevelContext) _localctx).messageInvis.invisMsgStr;

                                }
                                break;
                                case MESSAGE_MISS: {
                                    setState(166);
                                    ((MonsterSpellLevelContext) _localctx).messageMiss = messageMiss();

                                    missMessageInit = missMessageInit + ((MonsterSpellLevelContext) _localctx).messageMiss.missMsgStr;

                                }
                                break;
                                case MESSAGE_SAVE: {
                                    setState(169);
                                    ((MonsterSpellLevelContext) _localctx).messageSave = messageSave();

                                    saveMessageInit = saveMessageInit + ((MonsterSpellLevelContext) _localctx).messageSave.saveMsgStr;

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
                    setState(174);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 6, _ctx);
                } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
            }
            _ctx.stop = _input.LT(-1);

            ((MonsterSpellLevelContext) _localctx).spellLevel = new MonsterSpellLevel(powerCutOffInit, loreDescInit,
                    loreColTypeInit, loreResistColTypeInit,
                    loreImmuneColTypeInit, messageInit,
                    blindMessageInit, missMessageInit,
                    saveMessageInit);

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
    public static class MonsterSpellTypeContext extends ParserRuleContext {
        public MonsterSpellType spellType;
        public NameContext name;
        public MsgtContext msgt;
        public HitContext hit;
        public EffectBlockContext effectBlock;
        public MonsterSpellLevelContext monsterSpellLevel;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public List<MsgtContext> msgt() {
            return getRuleContexts(MsgtContext.class);
        }

        public MsgtContext msgt(int i) {
            return getRuleContext(MsgtContext.class, i);
        }

        public List<HitContext> hit() {
            return getRuleContexts(HitContext.class);
        }

        public HitContext hit(int i) {
            return getRuleContext(HitContext.class, i);
        }

        public List<EffectBlockContext> effectBlock() {
            return getRuleContexts(EffectBlockContext.class);
        }

        public EffectBlockContext effectBlock(int i) {
            return getRuleContext(EffectBlockContext.class, i);
        }

        public List<MonsterSpellLevelContext> monsterSpellLevel() {
            return getRuleContexts(MonsterSpellLevelContext.class);
        }

        public MonsterSpellLevelContext monsterSpellLevel(int i) {
            return getRuleContext(MonsterSpellLevelContext.class, i);
        }

        public MonsterSpellTypeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_monsterSpellType;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).enterMonsterSpellType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).exitMonsterSpellType(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterSpellVisitor)
                return ((MonsterSpellVisitor<? extends T>) visitor).visitMonsterSpellType(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MonsterSpellTypeContext monsterSpellType() throws RecognitionException {
        MonsterSpellTypeContext _localctx = new MonsterSpellTypeContext(_ctx, getState());
        enterRule(_localctx, 36, RULE_monsterSpellType);

        MonsterSpell indexInit = MonsterSpell.RSF_NONE;
        MessageType msgInit = MessageType.MSG_NONE;
        int hitInit = 0;
        List<Effect> effectsInit = new ArrayList<>();
        List<MonsterSpellLevel> levels = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(176);
                ((MonsterSpellTypeContext) _localctx).name = name();

                String raw = ((MonsterSpellTypeContext) _localctx).name.nameStr;
                indexInit = MonsterSpell.valueOf("RSF_" + raw);

                setState(184);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        setState(184);
                        _errHandler.sync(this);
                        switch (_input.LA(1)) {
                            case MSGT: {
                                setState(178);
                                ((MonsterSpellTypeContext) _localctx).msgt = msgt();

                                msgInit = ((MonsterSpellTypeContext) _localctx).msgt.msgtEnum;

                            }
                            break;
                            case HIT: {
                                setState(181);
                                ((MonsterSpellTypeContext) _localctx).hit = hit();

                                hitInit = ((MonsterSpellTypeContext) _localctx).hit.hitInt;

                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                    }
                    setState(186);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == MSGT || _la == HIT);
                setState(193);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == EFFECT) {
                    {
                        {
                            setState(188);
                            ((MonsterSpellTypeContext) _localctx).effectBlock = effectBlock();

                            effectsInit.add(((MonsterSpellTypeContext) _localctx).effectBlock.eff);

                        }
                    }
                    setState(195);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(201);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 523264L) != 0)) {
                    {
                        {
                            setState(196);
                            ((MonsterSpellTypeContext) _localctx).monsterSpellLevel = monsterSpellLevel();

                            levels.add(((MonsterSpellTypeContext) _localctx).monsterSpellLevel.spellLevel);

                        }
                    }
                    setState(203);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
            }
            _ctx.stop = _input.LT(-1);

            ((MonsterSpellTypeContext) _localctx).spellType = new MonsterSpellType(indexInit, msgInit,
                    hitInit, effectsInit, levels);

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
        public List<MonsterSpellType> spellTypes;
        public MonsterSpellTypeContext monsterSpellType;

        public TerminalNode EOF() {
            return getToken(MonsterSpellParser.EOF, 0);
        }

        public List<MonsterSpellTypeContext> monsterSpellType() {
            return getRuleContexts(MonsterSpellTypeContext.class);
        }

        public MonsterSpellTypeContext monsterSpellType(int i) {
            return getRuleContext(MonsterSpellTypeContext.class, i);
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
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellListener) ((MonsterSpellListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterSpellVisitor)
                return ((MonsterSpellVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 38, RULE_file);

        ((FileContext) _localctx).spellTypes = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(207);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(204);
                            ((FileContext) _localctx).monsterSpellType = monsterSpellType();

                            _localctx.spellTypes.add(((FileContext) _localctx).monsterSpellType.spellType);

                        }
                    }
                    setState(209);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(211);
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
            "\u0004\u0001\u0016\u00d6\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001" +
                    "\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004" +
                    "\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007" +
                    "\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b" +
                    "\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007" +
                    "\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007" +
                    "\u0012\u0002\u0013\u0007\u0013\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0003\u0003M\b\u0003\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007`\b\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0003\u0007e\b\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0003\u0007j\b\u0007\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001" +
                    "\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e" +
                    "\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010" +
                    "\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011" +
                    "\u0003\u0011\u0093\b\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011" +
                    "\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011" +
                    "\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011" +
                    "\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011" +
                    "\u0001\u0011\u0001\u0011\u0004\u0011\u00ad\b\u0011\u000b\u0011\f\u0011" +
                    "\u00ae\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001" +
                    "\u0012\u0001\u0012\u0001\u0012\u0004\u0012\u00b9\b\u0012\u000b\u0012\f" +
                    "\u0012\u00ba\u0001\u0012\u0001\u0012\u0001\u0012\u0005\u0012\u00c0\b\u0012" +
                    "\n\u0012\f\u0012\u00c3\t\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0005" +
                    "\u0012\u00c8\b\u0012\n\u0012\f\u0012\u00cb\t\u0012\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0004\u0013\u00d0\b\u0013\u000b\u0013\f\u0013\u00d1\u0001" +
                    "\u0013\u0001\u0013\u0001\u0013\u0000\u0000\u0014\u0000\u0002\u0004\u0006" +
                    "\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&\u0000" +
                    "\u0000\u00d5\u0000(\u0001\u0000\u0000\u0000\u0002,\u0001\u0000\u0000\u0000" +
                    "\u00040\u0001\u0000\u0000\u0000\u0006L\u0001\u0000\u0000\u0000\bN\u0001" +
                    "\u0000\u0000\u0000\nT\u0001\u0000\u0000\u0000\fW\u0001\u0000\u0000\u0000" +
                    "\u000eZ\u0001\u0000\u0000\u0000\u0010k\u0001\u0000\u0000\u0000\u0012o" +
                    "\u0001\u0000\u0000\u0000\u0014s\u0001\u0000\u0000\u0000\u0016w\u0001\u0000" +
                    "\u0000\u0000\u0018{\u0001\u0000\u0000\u0000\u001a\u007f\u0001\u0000\u0000" +
                    "\u0000\u001c\u0083\u0001\u0000\u0000\u0000\u001e\u0087\u0001\u0000\u0000" +
                    "\u0000 \u008b\u0001\u0000\u0000\u0000\"\u0092\u0001\u0000\u0000\u0000" +
                    "$\u00b0\u0001\u0000\u0000\u0000&\u00cf\u0001\u0000\u0000\u0000()\u0005" +
                    "\u0003\u0000\u0000)*\u0005\u0014\u0000\u0000*+\u0006\u0000\uffff\uffff" +
                    "\u0000+\u0001\u0001\u0000\u0000\u0000,-\u0005\u0004\u0000\u0000-.\u0005" +
                    "\u0014\u0000\u0000./\u0006\u0001\uffff\uffff\u0000/\u0003\u0001\u0000" +
                    "\u0000\u000001\u0005\u0005\u0000\u000012\u0005\u0013\u0000\u000023\u0006" +
                    "\u0002\uffff\uffff\u00003\u0005\u0001\u0000\u0000\u000045\u0005\u0006" +
                    "\u0000\u000056\u0005\u0014\u0000\u000067\u0005\u0016\u0000\u000078\u0005" +
                    "\u0014\u0000\u000089\u0005\u0016\u0000\u00009:\u0005\u0013\u0000\u0000" +
                    ":;\u0005\u0016\u0000\u0000;<\u0005\u0013\u0000\u0000<M\u0006\u0003\uffff" +
                    "\uffff\u0000=>\u0005\u0006\u0000\u0000>?\u0005\u0014\u0000\u0000?@\u0005" +
                    "\u0016\u0000\u0000@A\u0005\u0014\u0000\u0000AB\u0005\u0016\u0000\u0000" +
                    "BC\u0005\u0013\u0000\u0000CM\u0006\u0003\uffff\uffff\u0000DE\u0005\u0006" +
                    "\u0000\u0000EF\u0005\u0014\u0000\u0000FG\u0005\u0016\u0000\u0000GH\u0005" +
                    "\u0014\u0000\u0000HM\u0006\u0003\uffff\uffff\u0000IJ\u0005\u0006\u0000" +
                    "\u0000JK\u0005\u0014\u0000\u0000KM\u0006\u0003\uffff\uffff\u0000L4\u0001" +
                    "\u0000\u0000\u0000L=\u0001\u0000\u0000\u0000LD\u0001\u0000\u0000\u0000" +
                    "LI\u0001\u0000\u0000\u0000M\u0007\u0001\u0000\u0000\u0000NO\u0005\u0007" +
                    "\u0000\u0000OP\u0005\u0013\u0000\u0000PQ\u0005\u0016\u0000\u0000QR\u0005" +
                    "\u0013\u0000\u0000RS\u0006\u0004\uffff\uffff\u0000S\t\u0001\u0000\u0000" +
                    "\u0000TU\u0005\b\u0000\u0000UV\u0006\u0005\uffff\uffff\u0000V\u000b\u0001" +
                    "\u0000\u0000\u0000WX\u0005\t\u0000\u0000XY\u0006\u0006\uffff\uffff\u0000" +
                    "Y\r\u0001\u0000\u0000\u0000Z[\u0003\u0006\u0003\u0000[_\u0006\u0007\uffff" +
                    "\uffff\u0000\\]\u0003\b\u0004\u0000]^\u0006\u0007\uffff\uffff\u0000^`" +
                    "\u0001\u0000\u0000\u0000_\\\u0001\u0000\u0000\u0000_`\u0001\u0000\u0000" +
                    "\u0000`d\u0001\u0000\u0000\u0000ab\u0003\n\u0005\u0000bc\u0006\u0007\uffff" +
                    "\uffff\u0000ce\u0001\u0000\u0000\u0000da\u0001\u0000\u0000\u0000de\u0001" +
                    "\u0000\u0000\u0000ei\u0001\u0000\u0000\u0000fg\u0003\f\u0006\u0000gh\u0006" +
                    "\u0007\uffff\uffff\u0000hj\u0001\u0000\u0000\u0000if\u0001\u0000\u0000" +
                    "\u0000ij\u0001\u0000\u0000\u0000j\u000f\u0001\u0000\u0000\u0000kl\u0005" +
                    "\n\u0000\u0000lm\u0005\u0013\u0000\u0000mn\u0006\b\uffff\uffff\u0000n" +
                    "\u0011\u0001\u0000\u0000\u0000op\u0005\u000b\u0000\u0000pq\u0005\u0015" +
                    "\u0000\u0000qr\u0006\t\uffff\uffff\u0000r\u0013\u0001\u0000\u0000\u0000" +
                    "st\u0005\f\u0000\u0000tu\u0005\u0015\u0000\u0000uv\u0006\n\uffff\uffff" +
                    "\u0000v\u0015\u0001\u0000\u0000\u0000wx\u0005\r\u0000\u0000xy\u0005\u0015" +
                    "\u0000\u0000yz\u0006\u000b\uffff\uffff\u0000z\u0017\u0001\u0000\u0000" +
                    "\u0000{|\u0005\u000e\u0000\u0000|}\u0005\u0015\u0000\u0000}~\u0006\f\uffff" +
                    "\uffff\u0000~\u0019\u0001\u0000\u0000\u0000\u007f\u0080\u0005\u000f\u0000" +
                    "\u0000\u0080\u0081\u0005\u0015\u0000\u0000\u0081\u0082\u0006\r\uffff\uffff" +
                    "\u0000\u0082\u001b\u0001\u0000\u0000\u0000\u0083\u0084\u0005\u0010\u0000" +
                    "\u0000\u0084\u0085\u0005\u0015\u0000\u0000\u0085\u0086\u0006\u000e\uffff" +
                    "\uffff\u0000\u0086\u001d\u0001\u0000\u0000\u0000\u0087\u0088\u0005\u0011" +
                    "\u0000\u0000\u0088\u0089\u0005\u0015\u0000\u0000\u0089\u008a\u0006\u000f" +
                    "\uffff\uffff\u0000\u008a\u001f\u0001\u0000\u0000\u0000\u008b\u008c\u0005" +
                    "\u0012\u0000\u0000\u008c\u008d\u0005\u0015\u0000\u0000\u008d\u008e\u0006" +
                    "\u0010\uffff\uffff\u0000\u008e!\u0001\u0000\u0000\u0000\u008f\u0090\u0003" +
                    "\u0010\b\u0000\u0090\u0091\u0006\u0011\uffff\uffff\u0000\u0091\u0093\u0001" +
                    "\u0000\u0000\u0000\u0092\u008f\u0001\u0000\u0000\u0000\u0092\u0093\u0001" +
                    "\u0000\u0000\u0000\u0093\u00ac\u0001\u0000\u0000\u0000\u0094\u0095\u0003" +
                    "\u0012\t\u0000\u0095\u0096\u0006\u0011\uffff\uffff\u0000\u0096\u00ad\u0001" +
                    "\u0000\u0000\u0000\u0097\u0098\u0003\u0014\n\u0000\u0098\u0099\u0006\u0011" +
                    "\uffff\uffff\u0000\u0099\u00ad\u0001\u0000\u0000\u0000\u009a\u009b\u0003" +
                    "\u0016\u000b\u0000\u009b\u009c\u0006\u0011\uffff\uffff\u0000\u009c\u00ad" +
                    "\u0001\u0000\u0000\u0000\u009d\u009e\u0003\u0018\f\u0000\u009e\u009f\u0006" +
                    "\u0011\uffff\uffff\u0000\u009f\u00ad\u0001\u0000\u0000\u0000\u00a0\u00a1" +
                    "\u0003\u001c\u000e\u0000\u00a1\u00a2\u0006\u0011\uffff\uffff\u0000\u00a2" +
                    "\u00ad\u0001\u0000\u0000\u0000\u00a3\u00a4\u0003\u001e\u000f\u0000\u00a4" +
                    "\u00a5\u0006\u0011\uffff\uffff\u0000\u00a5\u00ad\u0001\u0000\u0000\u0000" +
                    "\u00a6\u00a7\u0003 \u0010\u0000\u00a7\u00a8\u0006\u0011\uffff\uffff\u0000" +
                    "\u00a8\u00ad\u0001\u0000\u0000\u0000\u00a9\u00aa\u0003\u001a\r\u0000\u00aa" +
                    "\u00ab\u0006\u0011\uffff\uffff\u0000\u00ab\u00ad\u0001\u0000\u0000\u0000" +
                    "\u00ac\u0094\u0001\u0000\u0000\u0000\u00ac\u0097\u0001\u0000\u0000\u0000" +
                    "\u00ac\u009a\u0001\u0000\u0000\u0000\u00ac\u009d\u0001\u0000\u0000\u0000" +
                    "\u00ac\u00a0\u0001\u0000\u0000\u0000\u00ac\u00a3\u0001\u0000\u0000\u0000" +
                    "\u00ac\u00a6\u0001\u0000\u0000\u0000\u00ac\u00a9\u0001\u0000\u0000\u0000" +
                    "\u00ad\u00ae\u0001\u0000\u0000\u0000\u00ae\u00ac\u0001\u0000\u0000\u0000" +
                    "\u00ae\u00af\u0001\u0000\u0000\u0000\u00af#\u0001\u0000\u0000\u0000\u00b0" +
                    "\u00b1\u0003\u0000\u0000\u0000\u00b1\u00b8\u0006\u0012\uffff\uffff\u0000" +
                    "\u00b2\u00b3\u0003\u0002\u0001\u0000\u00b3\u00b4\u0006\u0012\uffff\uffff" +
                    "\u0000\u00b4\u00b9\u0001\u0000\u0000\u0000\u00b5\u00b6\u0003\u0004\u0002" +
                    "\u0000\u00b6\u00b7\u0006\u0012\uffff\uffff\u0000\u00b7\u00b9\u0001\u0000" +
                    "\u0000\u0000\u00b8\u00b2\u0001\u0000\u0000\u0000\u00b8\u00b5\u0001\u0000" +
                    "\u0000\u0000\u00b9\u00ba\u0001\u0000\u0000\u0000\u00ba\u00b8\u0001\u0000" +
                    "\u0000\u0000\u00ba\u00bb\u0001\u0000\u0000\u0000\u00bb\u00c1\u0001\u0000" +
                    "\u0000\u0000\u00bc\u00bd\u0003\u000e\u0007\u0000\u00bd\u00be\u0006\u0012" +
                    "\uffff\uffff\u0000\u00be\u00c0\u0001\u0000\u0000\u0000\u00bf\u00bc\u0001" +
                    "\u0000\u0000\u0000\u00c0\u00c3\u0001\u0000\u0000\u0000\u00c1\u00bf\u0001" +
                    "\u0000\u0000\u0000\u00c1\u00c2\u0001\u0000\u0000\u0000\u00c2\u00c9\u0001" +
                    "\u0000\u0000\u0000\u00c3\u00c1\u0001\u0000\u0000\u0000\u00c4\u00c5\u0003" +
                    "\"\u0011\u0000\u00c5\u00c6\u0006\u0012\uffff\uffff\u0000\u00c6\u00c8\u0001" +
                    "\u0000\u0000\u0000\u00c7\u00c4\u0001\u0000\u0000\u0000\u00c8\u00cb\u0001" +
                    "\u0000\u0000\u0000\u00c9\u00c7\u0001\u0000\u0000\u0000\u00c9\u00ca\u0001" +
                    "\u0000\u0000\u0000\u00ca%\u0001\u0000\u0000\u0000\u00cb\u00c9\u0001\u0000" +
                    "\u0000\u0000\u00cc\u00cd\u0003$\u0012\u0000\u00cd\u00ce\u0006\u0013\uffff" +
                    "\uffff\u0000\u00ce\u00d0\u0001\u0000\u0000\u0000\u00cf\u00cc\u0001\u0000" +
                    "\u0000\u0000\u00d0\u00d1\u0001\u0000\u0000\u0000\u00d1\u00cf\u0001\u0000" +
                    "\u0000\u0000\u00d1\u00d2\u0001\u0000\u0000\u0000\u00d2\u00d3\u0001\u0000" +
                    "\u0000\u0000\u00d3\u00d4\u0005\u0000\u0000\u0001\u00d4\'\u0001\u0000\u0000" +
                    "\u0000\fL_di\u0092\u00ac\u00ae\u00b8\u00ba\u00c1\u00c9\u00d1";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}