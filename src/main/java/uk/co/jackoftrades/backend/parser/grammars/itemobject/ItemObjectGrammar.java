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
// Generated from ItemObjectGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.itemobject;

import uk.co.jackoftrades.backend.parser.grammars.EffectParseRecord;
import uk.co.jackoftrades.backend.parser.itemobject.ItemObjectParseRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ItemObjectGrammar extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, NAME = 2, TYPE = 3, LEVEL = 4, WEIGHT = 5, COST = 6, ATTACK = 7, ARMOUR = 8,
            ALLOC = 9, CHARGES = 10, PILE = 11, POWER = 12, MSG = 13, VIS_MSG = 14, FLAGS = 15,
            VALUES = 16, BRAND = 17, SLAY = 18, CURSE = 19, PVAL = 20, DESC = 21, COMMENT = 22,
            EOL = 23, EFFECT = 24, EFFECT_MESSAGE = 25, DICE = 26, TIME = 27, EFFECT_YX = 28,
            EXPR = 29, COLON = 30, UCASE = 31, INTEGER = 32, SIMPLE_DICE_STRING = 33, COMPLEX_DICE_STRING = 34,
            GRAPHICS = 35, COLOUR_ONLY = 36, GLYPH_ONLY = 37, CURSE_STRING = 38, CURSE_COLON = 39,
            CURSE_INT = 40, CURSE_EOL = 41, FLAG = 42, FLAG_OR = 43, FLAG_EOL = 44, ALLOC_INT = 45,
            ALLOC_COLON = 46, ALLOC_TO = 47, ALLOC_EOL = 48, OBJECT_STRING = 49, VALUE_TYPE = 50,
            VALUE_OR = 51, VALUE_LBRACKET = 52, VALUE_INT = 53, VALUE_RBRACKET = 54, VALUE_EOL = 55,
            RAND_VALUE = 56, RAND_COLON = 57, RAND_EOL = 58, FREE_TEXT = 59, DICE_SIMPLE_VALUE = 60,
            DICE_COMPLEX_VALUE = 61, EXPR_CHAR = 62, EXPR_COLON = 63, EXPR_UCASE = 64, EXPR_OP = 65,
            EXPR_EOL = 66, COLOUR_VALUES = 67, GLYPH_VALUES = 68, GLYPH_COLON_SWITCH = 69,
            GLYPH_EOL = 70;
    public static final int
            RULE_recordCount = 0, RULE_name = 1, RULE_tval = 2, RULE_graphics = 3,
            RULE_level = 4, RULE_weight = 5, RULE_cost = 6, RULE_attack = 7, RULE_armour = 8,
            RULE_alloc = 9, RULE_charges = 10, RULE_pile = 11, RULE_power = 12, RULE_msg = 13,
            RULE_visMsg = 14, RULE_flags = 15, RULE_values = 16, RULE_brand = 17,
            RULE_slay = 18, RULE_curse = 19, RULE_pval = 20, RULE_desc = 21, RULE_itemObject = 22,
            RULE_file = 23, RULE_effect = 24, RULE_time = 25, RULE_effectYX = 26,
            RULE_dice = 27, RULE_expr = 28, RULE_effectMsg = 29, RULE_effectBlock = 30;

    private static String[] makeRuleNames() {
        return new String[]{
                "recordCount", "name", "tval", "graphics", "level", "weight", "cost",
                "attack", "armour", "alloc", "charges", "pile", "power", "msg", "visMsg",
                "flags", "values", "brand", "slay", "curse", "pval", "desc", "itemObject",
                "file", "effect", "time", "effectYX", "dice", "expr", "effectMsg", "effectBlock"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'name:'", "'type:'", "'level:'", "'weight:'",
                "'cost:'", "'attack:'", "'armor:'", "'alloc:'", "'charges:'", "'pile:'",
                "'power:'", "'msg:'", "'vis-msg:'", "'flags:'", "'values:'", "'brand:'",
                "'slay:'", "'curse:'", "'pval:'", "'desc:'", null, null, "'effect:'",
                "'effect-msg:'", "'dice:'", "'time:'", "'effect-yx:'", "'expr:'", null,
                null, null, null, null, "'graphics:'", "'color:'", "'glyph:'", null,
                null, null, null, null, null, null, null, null, null, null, null, null,
                null, "'['", null, "']'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "NAME", "TYPE", "LEVEL", "WEIGHT", "COST", "ATTACK",
                "ARMOUR", "ALLOC", "CHARGES", "PILE", "POWER", "MSG", "VIS_MSG", "FLAGS",
                "VALUES", "BRAND", "SLAY", "CURSE", "PVAL", "DESC", "COMMENT", "EOL",
                "EFFECT", "EFFECT_MESSAGE", "DICE", "TIME", "EFFECT_YX", "EXPR", "COLON",
                "UCASE", "INTEGER", "SIMPLE_DICE_STRING", "COMPLEX_DICE_STRING", "GRAPHICS",
                "COLOUR_ONLY", "GLYPH_ONLY", "CURSE_STRING", "CURSE_COLON", "CURSE_INT",
                "CURSE_EOL", "FLAG", "FLAG_OR", "FLAG_EOL", "ALLOC_INT", "ALLOC_COLON",
                "ALLOC_TO", "ALLOC_EOL", "OBJECT_STRING", "VALUE_TYPE", "VALUE_OR", "VALUE_LBRACKET",
                "VALUE_INT", "VALUE_RBRACKET", "VALUE_EOL", "RAND_VALUE", "RAND_COLON",
                "RAND_EOL", "FREE_TEXT", "DICE_SIMPLE_VALUE", "DICE_COMPLEX_VALUE", "EXPR_CHAR",
                "EXPR_COLON", "EXPR_UCASE", "EXPR_OP", "EXPR_EOL", "COLOUR_VALUES", "GLYPH_VALUES",
                "GLYPH_COLON_SWITCH", "GLYPH_EOL"
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
        return "ItemObjectGrammar.g4";
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

    public ItemObjectGrammar(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RecordCountContext extends ParserRuleContext {
        public String count;
        public Token INTEGER;

        public TerminalNode RECORD_COUNT() {
            return getToken(ItemObjectGrammar.RECORD_COUNT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(ItemObjectGrammar.INTEGER, 0);
        }

        public RecordCountContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_recordCount;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener)
                ((ItemObjectGrammarListener) listener).enterRecordCount(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener)
                ((ItemObjectGrammarListener) listener).exitRecordCount(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitRecordCount(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RecordCountContext recordCount() throws RecognitionException {
        RecordCountContext _localctx = new RecordCountContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_recordCount);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(62);
                match(RECORD_COUNT);
                setState(63);
                ((RecordCountContext) _localctx).INTEGER = match(INTEGER);
                ((RecordCountContext) _localctx).count = ((RecordCountContext) _localctx).INTEGER.getText();
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
    public static class NameContext extends ParserRuleContext {
        public String nameStr;
        public int line;
        public Token OBJECT_STRING;

        public TerminalNode NAME() {
            return getToken(ItemObjectGrammar.NAME, 0);
        }

        public TerminalNode OBJECT_STRING() {
            return getToken(ItemObjectGrammar.OBJECT_STRING, 0);
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
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(66);
                match(NAME);
                setState(67);
                ((NameContext) _localctx).OBJECT_STRING = match(OBJECT_STRING);
                ((NameContext) _localctx).nameStr = ((NameContext) _localctx).OBJECT_STRING.getText();
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

    @SuppressWarnings("CheckReturnValue")
    public static class TvalContext extends ParserRuleContext {
        public String tvalStr;
        public Token OBJECT_STRING;

        public TerminalNode TYPE() {
            return getToken(ItemObjectGrammar.TYPE, 0);
        }

        public TerminalNode OBJECT_STRING() {
            return getToken(ItemObjectGrammar.OBJECT_STRING, 0);
        }

        public TvalContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_tval;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).enterTval(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitTval(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitTval(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TvalContext tval() throws RecognitionException {
        TvalContext _localctx = new TvalContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_tval);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(70);
                match(TYPE);
                setState(71);
                ((TvalContext) _localctx).OBJECT_STRING = match(OBJECT_STRING);
                ((TvalContext) _localctx).tvalStr = ((TvalContext) _localctx).OBJECT_STRING.getText();
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
    public static class GraphicsContext extends ParserRuleContext {
        public String glyph;
        public String colour;
        public Token g;
        public Token c;

        public TerminalNode GRAPHICS() {
            return getToken(ItemObjectGrammar.GRAPHICS, 0);
        }

        public TerminalNode GLYPH_COLON_SWITCH() {
            return getToken(ItemObjectGrammar.GLYPH_COLON_SWITCH, 0);
        }

        public TerminalNode GLYPH_VALUES() {
            return getToken(ItemObjectGrammar.GLYPH_VALUES, 0);
        }

        public TerminalNode COLOUR_VALUES() {
            return getToken(ItemObjectGrammar.COLOUR_VALUES, 0);
        }

        public GraphicsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_graphics;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener)
                ((ItemObjectGrammarListener) listener).enterGraphics(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener)
                ((ItemObjectGrammarListener) listener).exitGraphics(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitGraphics(this);
            else return visitor.visitChildren(this);
        }
    }

    public final GraphicsContext graphics() throws RecognitionException {
        GraphicsContext _localctx = new GraphicsContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_graphics);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(74);
                match(GRAPHICS);
                setState(75);
                ((GraphicsContext) _localctx).g = match(GLYPH_VALUES);
                setState(76);
                match(GLYPH_COLON_SWITCH);
                setState(77);
                ((GraphicsContext) _localctx).c = match(COLOUR_VALUES);

                ((GraphicsContext) _localctx).glyph = ((GraphicsContext) _localctx).g.getText();
                ((GraphicsContext) _localctx).colour = ((GraphicsContext) _localctx).c.getText();

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
        public String levelStr;
        public Token INTEGER;

        public TerminalNode LEVEL() {
            return getToken(ItemObjectGrammar.LEVEL, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(ItemObjectGrammar.INTEGER, 0);
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
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).enterLevel(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitLevel(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitLevel(this);
            else return visitor.visitChildren(this);
        }
    }

    public final LevelContext level() throws RecognitionException {
        LevelContext _localctx = new LevelContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_level);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(80);
                match(LEVEL);
                setState(81);
                ((LevelContext) _localctx).INTEGER = match(INTEGER);
                ((LevelContext) _localctx).levelStr = ((LevelContext) _localctx).INTEGER.getText();
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
    public static class WeightContext extends ParserRuleContext {
        public String weightStr;
        public Token INTEGER;

        public TerminalNode WEIGHT() {
            return getToken(ItemObjectGrammar.WEIGHT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(ItemObjectGrammar.INTEGER, 0);
        }

        public WeightContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_weight;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).enterWeight(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitWeight(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitWeight(this);
            else return visitor.visitChildren(this);
        }
    }

    public final WeightContext weight() throws RecognitionException {
        WeightContext _localctx = new WeightContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_weight);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(84);
                match(WEIGHT);
                setState(85);
                ((WeightContext) _localctx).INTEGER = match(INTEGER);
                ((WeightContext) _localctx).weightStr = ((WeightContext) _localctx).INTEGER.getText();
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
    public static class CostContext extends ParserRuleContext {
        public String costStr;
        public Token INTEGER;

        public TerminalNode COST() {
            return getToken(ItemObjectGrammar.COST, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(ItemObjectGrammar.INTEGER, 0);
        }

        public CostContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_cost;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).enterCost(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitCost(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitCost(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CostContext cost() throws RecognitionException {
        CostContext _localctx = new CostContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_cost);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(88);
                match(COST);
                setState(89);
                ((CostContext) _localctx).INTEGER = match(INTEGER);
                ((CostContext) _localctx).costStr = ((CostContext) _localctx).INTEGER.getText();
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
    public static class AttackContext extends ParserRuleContext {
        public String base;
        public String toh;
        public String tod;
        public Token b;
        public Token h;
        public Token d;

        public TerminalNode ATTACK() {
            return getToken(ItemObjectGrammar.ATTACK, 0);
        }

        public List<TerminalNode> RAND_COLON() {
            return getTokens(ItemObjectGrammar.RAND_COLON);
        }

        public TerminalNode RAND_COLON(int i) {
            return getToken(ItemObjectGrammar.RAND_COLON, i);
        }

        public List<TerminalNode> RAND_VALUE() {
            return getTokens(ItemObjectGrammar.RAND_VALUE);
        }

        public TerminalNode RAND_VALUE(int i) {
            return getToken(ItemObjectGrammar.RAND_VALUE, i);
        }

        public AttackContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_attack;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).enterAttack(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitAttack(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitAttack(this);
            else return visitor.visitChildren(this);
        }
    }

    public final AttackContext attack() throws RecognitionException {
        AttackContext _localctx = new AttackContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_attack);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(92);
                match(ATTACK);
                setState(93);
                ((AttackContext) _localctx).b = match(RAND_VALUE);
                setState(94);
                match(RAND_COLON);
                setState(95);
                ((AttackContext) _localctx).h = match(RAND_VALUE);
                setState(96);
                match(RAND_COLON);
                setState(97);
                ((AttackContext) _localctx).d = match(RAND_VALUE);

                ((AttackContext) _localctx).base = ((AttackContext) _localctx).b.getText();
                ((AttackContext) _localctx).toh = ((AttackContext) _localctx).h.getText();
                ((AttackContext) _localctx).tod = ((AttackContext) _localctx).d.getText();

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
    public static class ArmourContext extends ParserRuleContext {
        public String base;
        public String toa;
        public Token b;
        public Token a;

        public TerminalNode ARMOUR() {
            return getToken(ItemObjectGrammar.ARMOUR, 0);
        }

        public TerminalNode RAND_COLON() {
            return getToken(ItemObjectGrammar.RAND_COLON, 0);
        }

        public List<TerminalNode> RAND_VALUE() {
            return getTokens(ItemObjectGrammar.RAND_VALUE);
        }

        public TerminalNode RAND_VALUE(int i) {
            return getToken(ItemObjectGrammar.RAND_VALUE, i);
        }

        public ArmourContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_armour;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).enterArmour(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitArmour(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitArmour(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ArmourContext armour() throws RecognitionException {
        ArmourContext _localctx = new ArmourContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_armour);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(100);
                match(ARMOUR);
                setState(101);
                ((ArmourContext) _localctx).b = match(RAND_VALUE);
                setState(102);
                match(RAND_COLON);
                setState(103);
                ((ArmourContext) _localctx).a = match(RAND_VALUE);

                ((ArmourContext) _localctx).base = ((ArmourContext) _localctx).b.getText();
                ((ArmourContext) _localctx).toa = ((ArmourContext) _localctx).a.getText();

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
    public static class AllocContext extends ParserRuleContext {
        public String common;
        public String lower;
        public String upper;
        public Token c;
        public Token l;
        public Token u;

        public TerminalNode ALLOC() {
            return getToken(ItemObjectGrammar.ALLOC, 0);
        }

        public TerminalNode ALLOC_COLON() {
            return getToken(ItemObjectGrammar.ALLOC_COLON, 0);
        }

        public TerminalNode ALLOC_TO() {
            return getToken(ItemObjectGrammar.ALLOC_TO, 0);
        }

        public List<TerminalNode> ALLOC_INT() {
            return getTokens(ItemObjectGrammar.ALLOC_INT);
        }

        public TerminalNode ALLOC_INT(int i) {
            return getToken(ItemObjectGrammar.ALLOC_INT, i);
        }

        public AllocContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_alloc;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).enterAlloc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitAlloc(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitAlloc(this);
            else return visitor.visitChildren(this);
        }
    }

    public final AllocContext alloc() throws RecognitionException {
        AllocContext _localctx = new AllocContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_alloc);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(106);
                match(ALLOC);
                setState(107);
                ((AllocContext) _localctx).c = match(ALLOC_INT);
                setState(108);
                match(ALLOC_COLON);
                setState(109);
                ((AllocContext) _localctx).l = match(ALLOC_INT);
                setState(110);
                match(ALLOC_TO);
                setState(111);
                ((AllocContext) _localctx).u = match(ALLOC_INT);

                ((AllocContext) _localctx).common = ((AllocContext) _localctx).c.getText();
                ((AllocContext) _localctx).lower = ((AllocContext) _localctx).l.getText();
                ((AllocContext) _localctx).upper = ((AllocContext) _localctx).u.getText();

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
    public static class ChargesContext extends ParserRuleContext {
        public String chargesRnd;
        public Token c;

        public TerminalNode CHARGES() {
            return getToken(ItemObjectGrammar.CHARGES, 0);
        }

        public TerminalNode RAND_VALUE() {
            return getToken(ItemObjectGrammar.RAND_VALUE, 0);
        }

        public ChargesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_charges;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener)
                ((ItemObjectGrammarListener) listener).enterCharges(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitCharges(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitCharges(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ChargesContext charges() throws RecognitionException {
        ChargesContext _localctx = new ChargesContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_charges);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(114);
                match(CHARGES);
                setState(115);
                ((ChargesContext) _localctx).c = match(RAND_VALUE);
                ((ChargesContext) _localctx).chargesRnd = ((ChargesContext) _localctx).c.getText();
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
    public static class PileContext extends ParserRuleContext {
        public String chance;
        public String noOfItems;
        public Token c;
        public Token n;

        public TerminalNode PILE() {
            return getToken(ItemObjectGrammar.PILE, 0);
        }

        public TerminalNode RAND_COLON() {
            return getToken(ItemObjectGrammar.RAND_COLON, 0);
        }

        public List<TerminalNode> RAND_VALUE() {
            return getTokens(ItemObjectGrammar.RAND_VALUE);
        }

        public TerminalNode RAND_VALUE(int i) {
            return getToken(ItemObjectGrammar.RAND_VALUE, i);
        }

        public PileContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_pile;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).enterPile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitPile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitPile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PileContext pile() throws RecognitionException {
        PileContext _localctx = new PileContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_pile);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(118);
                match(PILE);
                setState(119);
                ((PileContext) _localctx).c = match(RAND_VALUE);
                setState(120);
                match(RAND_COLON);
                setState(121);
                ((PileContext) _localctx).n = match(RAND_VALUE);

                ((PileContext) _localctx).chance = ((PileContext) _localctx).c.getText();
                ((PileContext) _localctx).noOfItems = ((PileContext) _localctx).n.getText();

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
        public String powerInt;
        public Token INTEGER;

        public TerminalNode POWER() {
            return getToken(ItemObjectGrammar.POWER, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(ItemObjectGrammar.INTEGER, 0);
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
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).enterPower(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitPower(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitPower(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PowerContext power() throws RecognitionException {
        PowerContext _localctx = new PowerContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_power);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(124);
                match(POWER);
                setState(125);
                ((PowerContext) _localctx).INTEGER = match(INTEGER);
                ((PowerContext) _localctx).powerInt = ((PowerContext) _localctx).INTEGER.getText();
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
        public String message;
        public Token OBJECT_STRING;

        public TerminalNode MSG() {
            return getToken(ItemObjectGrammar.MSG, 0);
        }

        public TerminalNode OBJECT_STRING() {
            return getToken(ItemObjectGrammar.OBJECT_STRING, 0);
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
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).enterMsg(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitMsg(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitMsg(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MsgContext msg() throws RecognitionException {
        MsgContext _localctx = new MsgContext(_ctx, getState());
        enterRule(_localctx, 26, RULE_msg);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(128);
                match(MSG);
                setState(129);
                ((MsgContext) _localctx).OBJECT_STRING = match(OBJECT_STRING);
                ((MsgContext) _localctx).message = ((MsgContext) _localctx).OBJECT_STRING.getText();
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
    public static class VisMsgContext extends ParserRuleContext {
        public String vMessage;
        public Token OBJECT_STRING;

        public TerminalNode VIS_MSG() {
            return getToken(ItemObjectGrammar.VIS_MSG, 0);
        }

        public TerminalNode OBJECT_STRING() {
            return getToken(ItemObjectGrammar.OBJECT_STRING, 0);
        }

        public VisMsgContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_visMsg;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).enterVisMsg(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitVisMsg(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitVisMsg(this);
            else return visitor.visitChildren(this);
        }
    }

    public final VisMsgContext visMsg() throws RecognitionException {
        VisMsgContext _localctx = new VisMsgContext(_ctx, getState());
        enterRule(_localctx, 28, RULE_visMsg);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(132);
                match(VIS_MSG);
                setState(133);
                ((VisMsgContext) _localctx).OBJECT_STRING = match(OBJECT_STRING);
                ((VisMsgContext) _localctx).vMessage = ((VisMsgContext) _localctx).OBJECT_STRING.getText();
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
        public List<String> flagList;
        public Token f1;
        public Token f2;

        public TerminalNode FLAGS() {
            return getToken(ItemObjectGrammar.FLAGS, 0);
        }

        public List<TerminalNode> FLAG() {
            return getTokens(ItemObjectGrammar.FLAG);
        }

        public TerminalNode FLAG(int i) {
            return getToken(ItemObjectGrammar.FLAG, i);
        }

        public List<TerminalNode> FLAG_OR() {
            return getTokens(ItemObjectGrammar.FLAG_OR);
        }

        public TerminalNode FLAG_OR(int i) {
            return getToken(ItemObjectGrammar.FLAG_OR, i);
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
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).enterFlags(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitFlags(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitFlags(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FlagsContext flags() throws RecognitionException {
        FlagsContext _localctx = new FlagsContext(_ctx, getState());
        enterRule(_localctx, 30, RULE_flags);
        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                ((FlagsContext) _localctx).flagList = new ArrayList<>();
                setState(137);
                match(FLAGS);
                setState(138);
                ((FlagsContext) _localctx).f1 = match(FLAG);
                _localctx.flagList.add(((FlagsContext) _localctx).f1.getText());
                setState(145);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 0, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(140);
                                match(FLAG_OR);
                                setState(141);
                                ((FlagsContext) _localctx).f2 = match(FLAG);
                                _localctx.flagList.add(((FlagsContext) _localctx).f2.getText());
                            }
                        }
                    }
                    setState(147);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 0, _ctx);
                }
                setState(149);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == FLAG_OR) {
                    {
                        setState(148);
                        match(FLAG_OR);
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
    public static class ValuesContext extends ParserRuleContext {
        public Map<String, String> valueMap;
        public Token vt1;
        public Token vi1;
        public Token vt2;
        public Token vi2;

        public TerminalNode VALUES() {
            return getToken(ItemObjectGrammar.VALUES, 0);
        }

        public List<TerminalNode> VALUE_LBRACKET() {
            return getTokens(ItemObjectGrammar.VALUE_LBRACKET);
        }

        public TerminalNode VALUE_LBRACKET(int i) {
            return getToken(ItemObjectGrammar.VALUE_LBRACKET, i);
        }

        public List<TerminalNode> VALUE_RBRACKET() {
            return getTokens(ItemObjectGrammar.VALUE_RBRACKET);
        }

        public TerminalNode VALUE_RBRACKET(int i) {
            return getToken(ItemObjectGrammar.VALUE_RBRACKET, i);
        }

        public List<TerminalNode> VALUE_TYPE() {
            return getTokens(ItemObjectGrammar.VALUE_TYPE);
        }

        public TerminalNode VALUE_TYPE(int i) {
            return getToken(ItemObjectGrammar.VALUE_TYPE, i);
        }

        public List<TerminalNode> VALUE_INT() {
            return getTokens(ItemObjectGrammar.VALUE_INT);
        }

        public TerminalNode VALUE_INT(int i) {
            return getToken(ItemObjectGrammar.VALUE_INT, i);
        }

        public List<TerminalNode> VALUE_OR() {
            return getTokens(ItemObjectGrammar.VALUE_OR);
        }

        public TerminalNode VALUE_OR(int i) {
            return getToken(ItemObjectGrammar.VALUE_OR, i);
        }

        public ValuesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_values;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).enterValues(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitValues(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitValues(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ValuesContext values() throws RecognitionException {
        ValuesContext _localctx = new ValuesContext(_ctx, getState());
        enterRule(_localctx, 32, RULE_values);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                ((ValuesContext) _localctx).valueMap = new HashMap<>();
                setState(152);
                match(VALUES);
                setState(153);
                ((ValuesContext) _localctx).vt1 = match(VALUE_TYPE);
                setState(154);
                match(VALUE_LBRACKET);
                setState(155);
                ((ValuesContext) _localctx).vi1 = match(VALUE_INT);
                setState(156);
                match(VALUE_RBRACKET);

                _localctx.valueMap.put(((ValuesContext) _localctx).vt1.getText(), ((ValuesContext) _localctx).vi1.getText());

                setState(166);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == VALUE_OR) {
                    {
                        {
                            setState(158);
                            match(VALUE_OR);
                            setState(159);
                            ((ValuesContext) _localctx).vt2 = match(VALUE_TYPE);
                            setState(160);
                            match(VALUE_LBRACKET);
                            setState(161);
                            ((ValuesContext) _localctx).vi2 = match(VALUE_INT);
                            setState(162);
                            match(VALUE_RBRACKET);

                            _localctx.valueMap.put(((ValuesContext) _localctx).vt2.getText(), ((ValuesContext) _localctx).vi2.getText());

                        }
                    }
                    setState(168);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
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
    public static class BrandContext extends ParserRuleContext {
        public String brandStr;
        public Token OBJECT_STRING;

        public TerminalNode BRAND() {
            return getToken(ItemObjectGrammar.BRAND, 0);
        }

        public TerminalNode OBJECT_STRING() {
            return getToken(ItemObjectGrammar.OBJECT_STRING, 0);
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
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).enterBrand(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitBrand(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitBrand(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BrandContext brand() throws RecognitionException {
        BrandContext _localctx = new BrandContext(_ctx, getState());
        enterRule(_localctx, 34, RULE_brand);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(169);
                match(BRAND);
                setState(170);
                ((BrandContext) _localctx).OBJECT_STRING = match(OBJECT_STRING);
                ((BrandContext) _localctx).brandStr = ((BrandContext) _localctx).OBJECT_STRING.getText();
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
        public String slayStr;
        public Token OBJECT_STRING;

        public TerminalNode SLAY() {
            return getToken(ItemObjectGrammar.SLAY, 0);
        }

        public TerminalNode OBJECT_STRING() {
            return getToken(ItemObjectGrammar.OBJECT_STRING, 0);
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
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).enterSlay(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitSlay(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitSlay(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SlayContext slay() throws RecognitionException {
        SlayContext _localctx = new SlayContext(_ctx, getState());
        enterRule(_localctx, 36, RULE_slay);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(173);
                match(SLAY);
                setState(174);
                ((SlayContext) _localctx).OBJECT_STRING = match(OBJECT_STRING);
                ((SlayContext) _localctx).slayStr = ((SlayContext) _localctx).OBJECT_STRING.getText();
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
    public static class CurseContext extends ParserRuleContext {
        public String curseName;
        public String cursePower;
        public Token n;
        public Token p;

        public TerminalNode CURSE() {
            return getToken(ItemObjectGrammar.CURSE, 0);
        }

        public TerminalNode CURSE_COLON() {
            return getToken(ItemObjectGrammar.CURSE_COLON, 0);
        }

        public TerminalNode CURSE_STRING() {
            return getToken(ItemObjectGrammar.CURSE_STRING, 0);
        }

        public TerminalNode CURSE_INT() {
            return getToken(ItemObjectGrammar.CURSE_INT, 0);
        }

        public CurseContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_curse;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).enterCurse(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitCurse(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitCurse(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CurseContext curse() throws RecognitionException {
        CurseContext _localctx = new CurseContext(_ctx, getState());
        enterRule(_localctx, 38, RULE_curse);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(177);
                match(CURSE);
                setState(178);
                ((CurseContext) _localctx).n = match(CURSE_STRING);
                setState(179);
                match(CURSE_COLON);
                setState(180);
                ((CurseContext) _localctx).p = match(CURSE_INT);

                ((CurseContext) _localctx).curseName = ((CurseContext) _localctx).n.getText();
                ((CurseContext) _localctx).cursePower = ((CurseContext) _localctx).p.getText();

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
    public static class PvalContext extends ParserRuleContext {
        public String pvalInt;
        public Token RAND_VALUE;

        public TerminalNode PVAL() {
            return getToken(ItemObjectGrammar.PVAL, 0);
        }

        public TerminalNode RAND_VALUE() {
            return getToken(ItemObjectGrammar.RAND_VALUE, 0);
        }

        public PvalContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_pval;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).enterPval(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitPval(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitPval(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PvalContext pval() throws RecognitionException {
        PvalContext _localctx = new PvalContext(_ctx, getState());
        enterRule(_localctx, 40, RULE_pval);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(183);
                match(PVAL);
                setState(184);
                ((PvalContext) _localctx).RAND_VALUE = match(RAND_VALUE);
                ((PvalContext) _localctx).pvalInt = ((PvalContext) _localctx).RAND_VALUE.getText();
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
        public Token OBJECT_STRING;

        public TerminalNode DESC() {
            return getToken(ItemObjectGrammar.DESC, 0);
        }

        public TerminalNode OBJECT_STRING() {
            return getToken(ItemObjectGrammar.OBJECT_STRING, 0);
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
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).enterDesc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitDesc(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitDesc(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DescContext desc() throws RecognitionException {
        DescContext _localctx = new DescContext(_ctx, getState());
        enterRule(_localctx, 42, RULE_desc);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(187);
                match(DESC);
                setState(188);
                ((DescContext) _localctx).OBJECT_STRING = match(OBJECT_STRING);
                ((DescContext) _localctx).descStr = ((DescContext) _localctx).OBJECT_STRING.getText();
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
    public static class ItemObjectContext extends ParserRuleContext {
        public ItemObjectParseRecord object;
        public NameContext name;
        public TvalContext tval;
        public GraphicsContext graphics;
        public LevelContext level;
        public WeightContext weight;
        public CostContext cost;
        public AttackContext attack;
        public ArmourContext armour;
        public AllocContext alloc;
        public ChargesContext charges;
        public PileContext pile;
        public PowerContext power;
        public MsgContext msg;
        public VisMsgContext visMsg;
        public EffectBlockContext effectBlock;
        public FlagsContext flags;
        public ValuesContext values;
        public BrandContext brand;
        public SlayContext slay;
        public CurseContext curse;
        public PvalContext pval;
        public DescContext desc;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public List<TvalContext> tval() {
            return getRuleContexts(TvalContext.class);
        }

        public TvalContext tval(int i) {
            return getRuleContext(TvalContext.class, i);
        }

        public List<GraphicsContext> graphics() {
            return getRuleContexts(GraphicsContext.class);
        }

        public GraphicsContext graphics(int i) {
            return getRuleContext(GraphicsContext.class, i);
        }

        public List<LevelContext> level() {
            return getRuleContexts(LevelContext.class);
        }

        public LevelContext level(int i) {
            return getRuleContext(LevelContext.class, i);
        }

        public List<WeightContext> weight() {
            return getRuleContexts(WeightContext.class);
        }

        public WeightContext weight(int i) {
            return getRuleContext(WeightContext.class, i);
        }

        public List<CostContext> cost() {
            return getRuleContexts(CostContext.class);
        }

        public CostContext cost(int i) {
            return getRuleContext(CostContext.class, i);
        }

        public List<AttackContext> attack() {
            return getRuleContexts(AttackContext.class);
        }

        public AttackContext attack(int i) {
            return getRuleContext(AttackContext.class, i);
        }

        public List<ArmourContext> armour() {
            return getRuleContexts(ArmourContext.class);
        }

        public ArmourContext armour(int i) {
            return getRuleContext(ArmourContext.class, i);
        }

        public List<AllocContext> alloc() {
            return getRuleContexts(AllocContext.class);
        }

        public AllocContext alloc(int i) {
            return getRuleContext(AllocContext.class, i);
        }

        public List<ChargesContext> charges() {
            return getRuleContexts(ChargesContext.class);
        }

        public ChargesContext charges(int i) {
            return getRuleContext(ChargesContext.class, i);
        }

        public List<PileContext> pile() {
            return getRuleContexts(PileContext.class);
        }

        public PileContext pile(int i) {
            return getRuleContext(PileContext.class, i);
        }

        public List<PowerContext> power() {
            return getRuleContexts(PowerContext.class);
        }

        public PowerContext power(int i) {
            return getRuleContext(PowerContext.class, i);
        }

        public List<MsgContext> msg() {
            return getRuleContexts(MsgContext.class);
        }

        public MsgContext msg(int i) {
            return getRuleContext(MsgContext.class, i);
        }

        public List<VisMsgContext> visMsg() {
            return getRuleContexts(VisMsgContext.class);
        }

        public VisMsgContext visMsg(int i) {
            return getRuleContext(VisMsgContext.class, i);
        }

        public List<EffectBlockContext> effectBlock() {
            return getRuleContexts(EffectBlockContext.class);
        }

        public EffectBlockContext effectBlock(int i) {
            return getRuleContext(EffectBlockContext.class, i);
        }

        public List<FlagsContext> flags() {
            return getRuleContexts(FlagsContext.class);
        }

        public FlagsContext flags(int i) {
            return getRuleContext(FlagsContext.class, i);
        }

        public List<ValuesContext> values() {
            return getRuleContexts(ValuesContext.class);
        }

        public ValuesContext values(int i) {
            return getRuleContext(ValuesContext.class, i);
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

        public List<CurseContext> curse() {
            return getRuleContexts(CurseContext.class);
        }

        public CurseContext curse(int i) {
            return getRuleContext(CurseContext.class, i);
        }

        public List<PvalContext> pval() {
            return getRuleContexts(PvalContext.class);
        }

        public PvalContext pval(int i) {
            return getRuleContext(PvalContext.class, i);
        }

        public List<DescContext> desc() {
            return getRuleContexts(DescContext.class);
        }

        public DescContext desc(int i) {
            return getRuleContext(DescContext.class, i);
        }

        public ItemObjectContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_itemObject;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener)
                ((ItemObjectGrammarListener) listener).enterItemObject(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener)
                ((ItemObjectGrammarListener) listener).exitItemObject(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitItemObject(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ItemObjectContext itemObject() throws RecognitionException {
        ItemObjectContext _localctx = new ItemObjectContext(_ctx, getState());
        enterRule(_localctx, 44, RULE_itemObject);

        String nameInit = "";
        int line = 0;
        String typeInit = "";
        String glyphInit = "";
        String colourInit = "";
        String levelInit = "";
        String weightInit = "";
        String costInit = "";
        String atBaseInit = "";
        String atTohInit = "";
        String atTodInit = "";
        String arBaseInit = "";
        String arToaInit = "";
        String alCommonInit = "";
        String alLowerInit = "";
        String alUpperInit = "";
        String chargesInit = "";
        String pileChanceInit = "";
        String pileItemsInit = "";
        String powerInit = "";
        String msgInit = "";
        String visMsgInit = "";
        List<EffectParseRecord> effectsInit = new ArrayList<>();
        List<String> flagListInit = new ArrayList<>();
        Map<String, String> valuesInit = new HashMap<>();
        List<String> brandInit = new ArrayList<>();
        List<String> slayInit = new ArrayList<>();
        Map<String, String> curseInit = new HashMap<>();
        String pValInit = "";
        StringBuilder dsb = new StringBuilder();
        StringBuilder msb = new StringBuilder();
        StringBuilder vsb = new StringBuilder();
        String descInit = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(191);
                ((ItemObjectContext) _localctx).name = name();
                nameInit = ((ItemObjectContext) _localctx).name.nameStr;
                line = ((ItemObjectContext) _localctx).name.line;
                setState(256);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        setState(256);
                        _errHandler.sync(this);
                        switch (_input.LA(1)) {
                            case TYPE: {
                                setState(193);
                                ((ItemObjectContext) _localctx).tval = tval();
                                typeInit = ((ItemObjectContext) _localctx).tval.tvalStr;
                            }
                            break;
                            case GRAPHICS: {
                                setState(196);
                                ((ItemObjectContext) _localctx).graphics = graphics();
                                glyphInit = ((ItemObjectContext) _localctx).graphics.glyph;
                                colourInit = ((ItemObjectContext) _localctx).graphics.colour;
                            }
                            break;
                            case LEVEL: {
                                setState(199);
                                ((ItemObjectContext) _localctx).level = level();
                                levelInit = ((ItemObjectContext) _localctx).level.levelStr;
                            }
                            break;
                            case WEIGHT: {
                                setState(202);
                                ((ItemObjectContext) _localctx).weight = weight();
                                weightInit = ((ItemObjectContext) _localctx).weight.weightStr;
                            }
                            break;
                            case COST: {
                                setState(205);
                                ((ItemObjectContext) _localctx).cost = cost();
                                costInit = ((ItemObjectContext) _localctx).cost.costStr;
                            }
                            break;
                            case ATTACK: {
                                setState(208);
                                ((ItemObjectContext) _localctx).attack = attack();
                                atBaseInit = ((ItemObjectContext) _localctx).attack.base;
                                atTohInit = ((ItemObjectContext) _localctx).attack.toh;
                                atTodInit = ((ItemObjectContext) _localctx).attack.tod;
                            }
                            break;
                            case ARMOUR: {
                                setState(211);
                                ((ItemObjectContext) _localctx).armour = armour();
                                arBaseInit = ((ItemObjectContext) _localctx).armour.base;
                                arToaInit = ((ItemObjectContext) _localctx).armour.toa;
                            }
                            break;
                            case ALLOC: {
                                setState(214);
                                ((ItemObjectContext) _localctx).alloc = alloc();
                                alCommonInit = ((ItemObjectContext) _localctx).alloc.common;
                                alLowerInit = ((ItemObjectContext) _localctx).alloc.lower;
                                alUpperInit = ((ItemObjectContext) _localctx).alloc.upper;
                            }
                            break;
                            case CHARGES: {
                                setState(217);
                                ((ItemObjectContext) _localctx).charges = charges();
                                chargesInit = ((ItemObjectContext) _localctx).charges.chargesRnd;
                            }
                            break;
                            case PILE: {
                                setState(220);
                                ((ItemObjectContext) _localctx).pile = pile();
                                pileChanceInit = ((ItemObjectContext) _localctx).pile.chance;
                                pileItemsInit = ((ItemObjectContext) _localctx).pile.noOfItems;
                            }
                            break;
                            case POWER: {
                                setState(223);
                                ((ItemObjectContext) _localctx).power = power();
                                powerInit = ((ItemObjectContext) _localctx).power.powerInt;
                            }
                            break;
                            case MSG: {
                                setState(226);
                                ((ItemObjectContext) _localctx).msg = msg();
                                msb.append(((ItemObjectContext) _localctx).msg.message);
                            }
                            break;
                            case VIS_MSG: {
                                setState(229);
                                ((ItemObjectContext) _localctx).visMsg = visMsg();
                                vsb.append(((ItemObjectContext) _localctx).visMsg.vMessage);
                            }
                            break;
                            case EFFECT: {
                                setState(232);
                                ((ItemObjectContext) _localctx).effectBlock = effectBlock();
                                effectsInit.add(new EffectParseRecord(((ItemObjectContext) _localctx).effectBlock.typeInit,
                                        ((ItemObjectContext) _localctx).effectBlock.subtypeWrapperInit, ((ItemObjectContext) _localctx).effectBlock.radius, ((ItemObjectContext) _localctx).effectBlock.other,
                                        ((ItemObjectContext) _localctx).effectBlock.diceString, ((ItemObjectContext) _localctx).effectBlock.yVal, ((ItemObjectContext) _localctx).effectBlock.xVal,
                                        ((ItemObjectContext) _localctx).effectBlock.expressionChars, ((ItemObjectContext) _localctx).effectBlock.expressionBase,
                                        ((ItemObjectContext) _localctx).effectBlock.expressionOperation, ((ItemObjectContext) _localctx).effectBlock.timeDiceString,
                                        ((ItemObjectContext) _localctx).effectBlock.effectMessage, (((ItemObjectContext) _localctx).effectBlock != null ? (((ItemObjectContext) _localctx).effectBlock.start) : null).getLine()));
                            }
                            break;
                            case FLAGS: {
                                setState(235);
                                ((ItemObjectContext) _localctx).flags = flags();
                                flagListInit.addAll(((ItemObjectContext) _localctx).flags.flagList);
                            }
                            break;
                            case VALUES: {
                                setState(238);
                                ((ItemObjectContext) _localctx).values = values();
                                valuesInit.putAll(((ItemObjectContext) _localctx).values.valueMap);
                            }
                            break;
                            case BRAND: {
                                setState(241);
                                ((ItemObjectContext) _localctx).brand = brand();
                                brandInit.add(((ItemObjectContext) _localctx).brand.brandStr);
                            }
                            break;
                            case SLAY: {
                                setState(244);
                                ((ItemObjectContext) _localctx).slay = slay();
                                slayInit.add(((ItemObjectContext) _localctx).slay.slayStr);
                            }
                            break;
                            case CURSE: {
                                setState(247);
                                ((ItemObjectContext) _localctx).curse = curse();
                                curseInit.put(((ItemObjectContext) _localctx).curse.curseName, ((ItemObjectContext) _localctx).curse.cursePower);
                            }
                            break;
                            case PVAL: {
                                setState(250);
                                ((ItemObjectContext) _localctx).pval = pval();
                                pValInit = ((ItemObjectContext) _localctx).pval.pvalInt;
                            }
                            break;
                            case DESC: {
                                setState(253);
                                ((ItemObjectContext) _localctx).desc = desc();
                                dsb.append(((ItemObjectContext) _localctx).desc.descStr);
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                    }
                    setState(258);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 34380709880L) != 0));
            }
            _ctx.stop = _input.LT(-1);

            descInit = dsb.toString();
            msgInit = msb.toString();
            visMsgInit = vsb.toString();
            ((ItemObjectContext) _localctx).object = new ItemObjectParseRecord(nameInit, glyphInit, colourInit, typeInit,
                    levelInit, weightInit, costInit, atBaseInit, atTohInit, atTodInit,
                    arBaseInit, arToaInit, alCommonInit, alLowerInit, alUpperInit,
                    chargesInit, pileChanceInit, pileItemsInit, powerInit, msgInit,
                    visMsgInit, effectsInit, flagListInit, valuesInit, brandInit, slayInit,
                    curseInit, pValInit, descInit, line);

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
        public String declaredRecordCount;
        public List<ItemObjectParseRecord> itemObjects;
        public RecordCountContext recordCount;
        public ItemObjectContext itemObject;

        public RecordCountContext recordCount() {
            return getRuleContext(RecordCountContext.class, 0);
        }

        public TerminalNode EOF() {
            return getToken(ItemObjectGrammar.EOF, 0);
        }

        public List<ItemObjectContext> itemObject() {
            return getRuleContexts(ItemObjectContext.class);
        }

        public ItemObjectContext itemObject(int i) {
            return getRuleContext(ItemObjectContext.class, i);
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
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 46, RULE_file);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(260);
                ((FileContext) _localctx).recordCount = recordCount();
                ((FileContext) _localctx).itemObjects = new ArrayList<>();
                ((FileContext) _localctx).declaredRecordCount = ((FileContext) _localctx).recordCount.count;
                setState(265);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(262);
                            ((FileContext) _localctx).itemObject = itemObject();
                            _localctx.itemObjects.add(((FileContext) _localctx).itemObject.object);
                        }
                    }
                    setState(267);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(269);
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
            return getToken(ItemObjectGrammar.EFFECT, 0);
        }

        public List<TerminalNode> UCASE() {
            return getTokens(ItemObjectGrammar.UCASE);
        }

        public TerminalNode UCASE(int i) {
            return getToken(ItemObjectGrammar.UCASE, i);
        }

        public List<TerminalNode> COLON() {
            return getTokens(ItemObjectGrammar.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(ItemObjectGrammar.COLON, i);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(ItemObjectGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(ItemObjectGrammar.INTEGER, i);
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
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).enterEffect(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitEffect(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitEffect(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectContext effect() throws RecognitionException {
        EffectContext _localctx = new EffectContext(_ctx, getState());
        enterRule(_localctx, 48, RULE_effect);

        ((EffectContext) _localctx).wrapper = "";
        ((EffectContext) _localctx).radius = "";
        ((EffectContext) _localctx).other = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(271);
                match(EFFECT);
                setState(272);
                ((EffectContext) _localctx).t = match(UCASE);

                ((EffectContext) _localctx).type = ((EffectContext) _localctx).t.getText();

                setState(287);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COLON) {
                    {
                        setState(274);
                        match(COLON);
                        setState(275);
                        ((EffectContext) _localctx).st = match(UCASE);

                        ((EffectContext) _localctx).wrapper = ((EffectContext) _localctx).st.getText().toUpperCase();

                        setState(285);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == COLON) {
                            {
                                setState(277);
                                match(COLON);
                                setState(278);
                                ((EffectContext) _localctx).rad = match(INTEGER);

                                ((EffectContext) _localctx).radius = ((EffectContext) _localctx).rad.getText();

                                setState(283);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                                if (_la == COLON) {
                                    {
                                        setState(280);
                                        match(COLON);
                                        setState(281);
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
            return getToken(ItemObjectGrammar.TIME, 0);
        }

        public TerminalNode DICE_SIMPLE_VALUE() {
            return getToken(ItemObjectGrammar.DICE_SIMPLE_VALUE, 0);
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
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).enterTime(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitTime(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitTime(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TimeContext time() throws RecognitionException {
        TimeContext _localctx = new TimeContext(_ctx, getState());
        enterRule(_localctx, 50, RULE_time);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(289);
                match(TIME);
                setState(290);
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
            return getToken(ItemObjectGrammar.EFFECT_YX, 0);
        }

        public TerminalNode COLON() {
            return getToken(ItemObjectGrammar.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(ItemObjectGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(ItemObjectGrammar.INTEGER, i);
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
            if (listener instanceof ItemObjectGrammarListener)
                ((ItemObjectGrammarListener) listener).enterEffectYX(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener)
                ((ItemObjectGrammarListener) listener).exitEffectYX(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitEffectYX(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectYXContext effectYX() throws RecognitionException {
        EffectYXContext _localctx = new EffectYXContext(_ctx, getState());
        enterRule(_localctx, 52, RULE_effectYX);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(293);
                match(EFFECT_YX);
                setState(294);
                ((EffectYXContext) _localctx).yVal = match(INTEGER);
                setState(295);
                match(COLON);
                setState(296);
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
            return getToken(ItemObjectGrammar.DICE, 0);
        }

        public TerminalNode DICE_SIMPLE_VALUE() {
            return getToken(ItemObjectGrammar.DICE_SIMPLE_VALUE, 0);
        }

        public TerminalNode DICE_COMPLEX_VALUE() {
            return getToken(ItemObjectGrammar.DICE_COMPLEX_VALUE, 0);
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
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).enterDice(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitDice(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitDice(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DiceContext dice() throws RecognitionException {
        DiceContext _localctx = new DiceContext(_ctx, getState());
        enterRule(_localctx, 54, RULE_dice);

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
                setState(299);
                match(DICE);
                setState(311);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case DICE_COMPLEX_VALUE: {
                        {
                            setState(300);
                            ((DiceContext) _localctx).val = match(DICE_COMPLEX_VALUE);

                            ((DiceContext) _localctx).diceString = ((DiceContext) _localctx).val.getText();

                            setState(305);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            do {
                                {
                                    {
                                        setState(302);
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
                                setState(307);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                            } while (_la == EXPR);
                        }
                    }
                    break;
                    case DICE_SIMPLE_VALUE: {
                        setState(309);
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
            return getToken(ItemObjectGrammar.EXPR, 0);
        }

        public List<TerminalNode> EXPR_COLON() {
            return getTokens(ItemObjectGrammar.EXPR_COLON);
        }

        public TerminalNode EXPR_COLON(int i) {
            return getToken(ItemObjectGrammar.EXPR_COLON, i);
        }

        public TerminalNode EXPR_CHAR() {
            return getToken(ItemObjectGrammar.EXPR_CHAR, 0);
        }

        public TerminalNode EXPR_UCASE() {
            return getToken(ItemObjectGrammar.EXPR_UCASE, 0);
        }

        public TerminalNode EXPR_OP() {
            return getToken(ItemObjectGrammar.EXPR_OP, 0);
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
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).enterExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener) ((ItemObjectGrammarListener) listener).exitExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitExpr(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExprContext expr() throws RecognitionException {
        ExprContext _localctx = new ExprContext(_ctx, getState());
        enterRule(_localctx, 56, RULE_expr);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(313);
                match(EXPR);
                setState(314);
                ((ExprContext) _localctx).ch = match(EXPR_CHAR);
                setState(315);
                match(EXPR_COLON);
                setState(316);
                ((ExprContext) _localctx).base = match(EXPR_UCASE);
                setState(317);
                match(EXPR_COLON);
                setState(318);
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
            return getToken(ItemObjectGrammar.EFFECT_MESSAGE, 0);
        }

        public TerminalNode FREE_TEXT() {
            return getToken(ItemObjectGrammar.FREE_TEXT, 0);
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
            if (listener instanceof ItemObjectGrammarListener)
                ((ItemObjectGrammarListener) listener).enterEffectMsg(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener)
                ((ItemObjectGrammarListener) listener).exitEffectMsg(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitEffectMsg(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectMsgContext effectMsg() throws RecognitionException {
        EffectMsgContext _localctx = new EffectMsgContext(_ctx, getState());
        enterRule(_localctx, 58, RULE_effectMsg);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(321);
                match(EFFECT_MESSAGE);
                setState(322);
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
        public int lineNo;
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
            if (listener instanceof ItemObjectGrammarListener)
                ((ItemObjectGrammarListener) listener).enterEffectBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ItemObjectGrammarListener)
                ((ItemObjectGrammarListener) listener).exitEffectBlock(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ItemObjectGrammarVisitor)
                return ((ItemObjectGrammarVisitor<? extends T>) visitor).visitEffectBlock(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectBlockContext effectBlock() throws RecognitionException {
        EffectBlockContext _localctx = new EffectBlockContext(_ctx, getState());
        enterRule(_localctx, 60, RULE_effectBlock);

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
                setState(325);
                ((EffectBlockContext) _localctx).effect = effect();

                ((EffectBlockContext) _localctx).lineNo = _localctx.start.getLine();
                ((EffectBlockContext) _localctx).typeInit = ((EffectBlockContext) _localctx).effect.type;
                ((EffectBlockContext) _localctx).subtypeWrapperInit = ((EffectBlockContext) _localctx).effect.wrapper;
                ((EffectBlockContext) _localctx).radius = ((EffectBlockContext) _localctx).effect.radius;
                ((EffectBlockContext) _localctx).other = ((EffectBlockContext) _localctx).effect.other;

                setState(335);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case EFFECT_YX: {
                        {
                            setState(327);
                            ((EffectBlockContext) _localctx).effectYX = effectYX();

                            ((EffectBlockContext) _localctx).yVal = ((EffectBlockContext) _localctx).effectYX.y;
                            ((EffectBlockContext) _localctx).xVal = ((EffectBlockContext) _localctx).effectYX.x;

                        }
                    }
                    break;
                    case EOF:
                    case NAME:
                    case TYPE:
                    case LEVEL:
                    case WEIGHT:
                    case COST:
                    case ATTACK:
                    case ARMOUR:
                    case ALLOC:
                    case CHARGES:
                    case PILE:
                    case POWER:
                    case MSG:
                    case VIS_MSG:
                    case FLAGS:
                    case VALUES:
                    case BRAND:
                    case SLAY:
                    case CURSE:
                    case PVAL:
                    case DESC:
                    case EFFECT:
                    case EFFECT_MESSAGE:
                    case DICE:
                    case TIME:
                    case GRAPHICS: {
                        {
                            setState(333);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            if (_la == DICE) {
                                {
                                    setState(330);
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
                setState(340);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == TIME) {
                    {
                        setState(337);
                        ((EffectBlockContext) _localctx).time = time();

                        ((EffectBlockContext) _localctx).timeDiceString = ((EffectBlockContext) _localctx).time.timeStr;

                    }
                }

                setState(345);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == EFFECT_MESSAGE) {
                    {
                        setState(342);
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
            "\u0004\u0001F\u015c\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002" +
                    "\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002" +
                    "\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f" +
                    "\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012" +
                    "\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015" +
                    "\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018" +
                    "\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b" +
                    "\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001" +
                    "\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001" +
                    "\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001" +
                    "\u000f\u0005\u000f\u0090\b\u000f\n\u000f\f\u000f\u0093\t\u000f\u0001\u000f" +
                    "\u0003\u000f\u0096\b\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010" +
                    "\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010" +
                    "\u0001\u0010\u0001\u0010\u0001\u0010\u0005\u0010\u00a5\b\u0010\n\u0010" +
                    "\f\u0010\u00a8\t\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011" +
                    "\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014" +
                    "\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015" +
                    "\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016" +
                    "\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016" +
                    "\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016" +
                    "\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016" +
                    "\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016" +
                    "\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016" +
                    "\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016" +
                    "\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016" +
                    "\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016" +
                    "\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016" +
                    "\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0004\u0016" +
                    "\u0101\b\u0016\u000b\u0016\f\u0016\u0102\u0001\u0017\u0001\u0017\u0001" +
                    "\u0017\u0001\u0017\u0001\u0017\u0004\u0017\u010a\b\u0017\u000b\u0017\f" +
                    "\u0017\u010b\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0018" +
                    "\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018" +
                    "\u0001\u0018\u0001\u0018\u0001\u0018\u0003\u0018\u011c\b\u0018\u0003\u0018" +
                    "\u011e\b\u0018\u0003\u0018\u0120\b\u0018\u0001\u0019\u0001\u0019\u0001" +
                    "\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001" +
                    "\u001a\u0001\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001" +
                    "\u001b\u0001\u001b\u0004\u001b\u0132\b\u001b\u000b\u001b\f\u001b\u0133" +
                    "\u0001\u001b\u0001\u001b\u0003\u001b\u0138\b\u001b\u0001\u001c\u0001\u001c" +
                    "\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c" +
                    "\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001e\u0001\u001e" +
                    "\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e" +
                    "\u0003\u001e\u014e\b\u001e\u0003\u001e\u0150\b\u001e\u0001\u001e\u0001" +
                    "\u001e\u0001\u001e\u0003\u001e\u0155\b\u001e\u0001\u001e\u0001\u001e\u0001" +
                    "\u001e\u0003\u001e\u015a\b\u001e\u0001\u001e\u0000\u0000\u001f\u0000\u0002" +
                    "\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e" +
                    " \"$&(*,.02468:<\u0000\u0000\u015e\u0000>\u0001\u0000\u0000\u0000\u0002" +
                    "B\u0001\u0000\u0000\u0000\u0004F\u0001\u0000\u0000\u0000\u0006J\u0001" +
                    "\u0000\u0000\u0000\bP\u0001\u0000\u0000\u0000\nT\u0001\u0000\u0000\u0000" +
                    "\fX\u0001\u0000\u0000\u0000\u000e\\\u0001\u0000\u0000\u0000\u0010d\u0001" +
                    "\u0000\u0000\u0000\u0012j\u0001\u0000\u0000\u0000\u0014r\u0001\u0000\u0000" +
                    "\u0000\u0016v\u0001\u0000\u0000\u0000\u0018|\u0001\u0000\u0000\u0000\u001a" +
                    "\u0080\u0001\u0000\u0000\u0000\u001c\u0084\u0001\u0000\u0000\u0000\u001e" +
                    "\u0088\u0001\u0000\u0000\u0000 \u0097\u0001\u0000\u0000\u0000\"\u00a9" +
                    "\u0001\u0000\u0000\u0000$\u00ad\u0001\u0000\u0000\u0000&\u00b1\u0001\u0000" +
                    "\u0000\u0000(\u00b7\u0001\u0000\u0000\u0000*\u00bb\u0001\u0000\u0000\u0000" +
                    ",\u00bf\u0001\u0000\u0000\u0000.\u0104\u0001\u0000\u0000\u00000\u010f" +
                    "\u0001\u0000\u0000\u00002\u0121\u0001\u0000\u0000\u00004\u0125\u0001\u0000" +
                    "\u0000\u00006\u012b\u0001\u0000\u0000\u00008\u0139\u0001\u0000\u0000\u0000" +
                    ":\u0141\u0001\u0000\u0000\u0000<\u0145\u0001\u0000\u0000\u0000>?\u0005" +
                    "\u0001\u0000\u0000?@\u0005 \u0000\u0000@A\u0006\u0000\uffff\uffff\u0000" +
                    "A\u0001\u0001\u0000\u0000\u0000BC\u0005\u0002\u0000\u0000CD\u00051\u0000" +
                    "\u0000DE\u0006\u0001\uffff\uffff\u0000E\u0003\u0001\u0000\u0000\u0000" +
                    "FG\u0005\u0003\u0000\u0000GH\u00051\u0000\u0000HI\u0006\u0002\uffff\uffff" +
                    "\u0000I\u0005\u0001\u0000\u0000\u0000JK\u0005#\u0000\u0000KL\u0005D\u0000" +
                    "\u0000LM\u0005E\u0000\u0000MN\u0005C\u0000\u0000NO\u0006\u0003\uffff\uffff" +
                    "\u0000O\u0007\u0001\u0000\u0000\u0000PQ\u0005\u0004\u0000\u0000QR\u0005" +
                    " \u0000\u0000RS\u0006\u0004\uffff\uffff\u0000S\t\u0001\u0000\u0000\u0000" +
                    "TU\u0005\u0005\u0000\u0000UV\u0005 \u0000\u0000VW\u0006\u0005\uffff\uffff" +
                    "\u0000W\u000b\u0001\u0000\u0000\u0000XY\u0005\u0006\u0000\u0000YZ\u0005" +
                    " \u0000\u0000Z[\u0006\u0006\uffff\uffff\u0000[\r\u0001\u0000\u0000\u0000" +
                    "\\]\u0005\u0007\u0000\u0000]^\u00058\u0000\u0000^_\u00059\u0000\u0000" +
                    "_`\u00058\u0000\u0000`a\u00059\u0000\u0000ab\u00058\u0000\u0000bc\u0006" +
                    "\u0007\uffff\uffff\u0000c\u000f\u0001\u0000\u0000\u0000de\u0005\b\u0000" +
                    "\u0000ef\u00058\u0000\u0000fg\u00059\u0000\u0000gh\u00058\u0000\u0000" +
                    "hi\u0006\b\uffff\uffff\u0000i\u0011\u0001\u0000\u0000\u0000jk\u0005\t" +
                    "\u0000\u0000kl\u0005-\u0000\u0000lm\u0005.\u0000\u0000mn\u0005-\u0000" +
                    "\u0000no\u0005/\u0000\u0000op\u0005-\u0000\u0000pq\u0006\t\uffff\uffff" +
                    "\u0000q\u0013\u0001\u0000\u0000\u0000rs\u0005\n\u0000\u0000st\u00058\u0000" +
                    "\u0000tu\u0006\n\uffff\uffff\u0000u\u0015\u0001\u0000\u0000\u0000vw\u0005" +
                    "\u000b\u0000\u0000wx\u00058\u0000\u0000xy\u00059\u0000\u0000yz\u00058" +
                    "\u0000\u0000z{\u0006\u000b\uffff\uffff\u0000{\u0017\u0001\u0000\u0000" +
                    "\u0000|}\u0005\f\u0000\u0000}~\u0005 \u0000\u0000~\u007f\u0006\f\uffff" +
                    "\uffff\u0000\u007f\u0019\u0001\u0000\u0000\u0000\u0080\u0081\u0005\r\u0000" +
                    "\u0000\u0081\u0082\u00051\u0000\u0000\u0082\u0083\u0006\r\uffff\uffff" +
                    "\u0000\u0083\u001b\u0001\u0000\u0000\u0000\u0084\u0085\u0005\u000e\u0000" +
                    "\u0000\u0085\u0086\u00051\u0000\u0000\u0086\u0087\u0006\u000e\uffff\uffff" +
                    "\u0000\u0087\u001d\u0001\u0000\u0000\u0000\u0088\u0089\u0006\u000f\uffff" +
                    "\uffff\u0000\u0089\u008a\u0005\u000f\u0000\u0000\u008a\u008b\u0005*\u0000" +
                    "\u0000\u008b\u0091\u0006\u000f\uffff\uffff\u0000\u008c\u008d\u0005+\u0000" +
                    "\u0000\u008d\u008e\u0005*\u0000\u0000\u008e\u0090\u0006\u000f\uffff\uffff" +
                    "\u0000\u008f\u008c\u0001\u0000\u0000\u0000\u0090\u0093\u0001\u0000\u0000" +
                    "\u0000\u0091\u008f\u0001\u0000\u0000\u0000\u0091\u0092\u0001\u0000\u0000" +
                    "\u0000\u0092\u0095\u0001\u0000\u0000\u0000\u0093\u0091\u0001\u0000\u0000" +
                    "\u0000\u0094\u0096\u0005+\u0000\u0000\u0095\u0094\u0001\u0000\u0000\u0000" +
                    "\u0095\u0096\u0001\u0000\u0000\u0000\u0096\u001f\u0001\u0000\u0000\u0000" +
                    "\u0097\u0098\u0006\u0010\uffff\uffff\u0000\u0098\u0099\u0005\u0010\u0000" +
                    "\u0000\u0099\u009a\u00052\u0000\u0000\u009a\u009b\u00054\u0000\u0000\u009b" +
                    "\u009c\u00055\u0000\u0000\u009c\u009d\u00056\u0000\u0000\u009d\u00a6\u0006" +
                    "\u0010\uffff\uffff\u0000\u009e\u009f\u00053\u0000\u0000\u009f\u00a0\u0005" +
                    "2\u0000\u0000\u00a0\u00a1\u00054\u0000\u0000\u00a1\u00a2\u00055\u0000" +
                    "\u0000\u00a2\u00a3\u00056\u0000\u0000\u00a3\u00a5\u0006\u0010\uffff\uffff" +
                    "\u0000\u00a4\u009e\u0001\u0000\u0000\u0000\u00a5\u00a8\u0001\u0000\u0000" +
                    "\u0000\u00a6\u00a4\u0001\u0000\u0000\u0000\u00a6\u00a7\u0001\u0000\u0000" +
                    "\u0000\u00a7!\u0001\u0000\u0000\u0000\u00a8\u00a6\u0001\u0000\u0000\u0000" +
                    "\u00a9\u00aa\u0005\u0011\u0000\u0000\u00aa\u00ab\u00051\u0000\u0000\u00ab" +
                    "\u00ac\u0006\u0011\uffff\uffff\u0000\u00ac#\u0001\u0000\u0000\u0000\u00ad" +
                    "\u00ae\u0005\u0012\u0000\u0000\u00ae\u00af\u00051\u0000\u0000\u00af\u00b0" +
                    "\u0006\u0012\uffff\uffff\u0000\u00b0%\u0001\u0000\u0000\u0000\u00b1\u00b2" +
                    "\u0005\u0013\u0000\u0000\u00b2\u00b3\u0005&\u0000\u0000\u00b3\u00b4\u0005" +
                    "\'\u0000\u0000\u00b4\u00b5\u0005(\u0000\u0000\u00b5\u00b6\u0006\u0013" +
                    "\uffff\uffff\u0000\u00b6\'\u0001\u0000\u0000\u0000\u00b7\u00b8\u0005\u0014" +
                    "\u0000\u0000\u00b8\u00b9\u00058\u0000\u0000\u00b9\u00ba\u0006\u0014\uffff" +
                    "\uffff\u0000\u00ba)\u0001\u0000\u0000\u0000\u00bb\u00bc\u0005\u0015\u0000" +
                    "\u0000\u00bc\u00bd\u00051\u0000\u0000\u00bd\u00be\u0006\u0015\uffff\uffff" +
                    "\u0000\u00be+\u0001\u0000\u0000\u0000\u00bf\u00c0\u0003\u0002\u0001\u0000" +
                    "\u00c0\u0100\u0006\u0016\uffff\uffff\u0000\u00c1\u00c2\u0003\u0004\u0002" +
                    "\u0000\u00c2\u00c3\u0006\u0016\uffff\uffff\u0000\u00c3\u0101\u0001\u0000" +
                    "\u0000\u0000\u00c4\u00c5\u0003\u0006\u0003\u0000\u00c5\u00c6\u0006\u0016" +
                    "\uffff\uffff\u0000\u00c6\u0101\u0001\u0000\u0000\u0000\u00c7\u00c8\u0003" +
                    "\b\u0004\u0000\u00c8\u00c9\u0006\u0016\uffff\uffff\u0000\u00c9\u0101\u0001" +
                    "\u0000\u0000\u0000\u00ca\u00cb\u0003\n\u0005\u0000\u00cb\u00cc\u0006\u0016" +
                    "\uffff\uffff\u0000\u00cc\u0101\u0001\u0000\u0000\u0000\u00cd\u00ce\u0003" +
                    "\f\u0006\u0000\u00ce\u00cf\u0006\u0016\uffff\uffff\u0000\u00cf\u0101\u0001" +
                    "\u0000\u0000\u0000\u00d0\u00d1\u0003\u000e\u0007\u0000\u00d1\u00d2\u0006" +
                    "\u0016\uffff\uffff\u0000\u00d2\u0101\u0001\u0000\u0000\u0000\u00d3\u00d4" +
                    "\u0003\u0010\b\u0000\u00d4\u00d5\u0006\u0016\uffff\uffff\u0000\u00d5\u0101" +
                    "\u0001\u0000\u0000\u0000\u00d6\u00d7\u0003\u0012\t\u0000\u00d7\u00d8\u0006" +
                    "\u0016\uffff\uffff\u0000\u00d8\u0101\u0001\u0000\u0000\u0000\u00d9\u00da" +
                    "\u0003\u0014\n\u0000\u00da\u00db\u0006\u0016\uffff\uffff\u0000\u00db\u0101" +
                    "\u0001\u0000\u0000\u0000\u00dc\u00dd\u0003\u0016\u000b\u0000\u00dd\u00de" +
                    "\u0006\u0016\uffff\uffff\u0000\u00de\u0101\u0001\u0000\u0000\u0000\u00df" +
                    "\u00e0\u0003\u0018\f\u0000\u00e0\u00e1\u0006\u0016\uffff\uffff\u0000\u00e1" +
                    "\u0101\u0001\u0000\u0000\u0000\u00e2\u00e3\u0003\u001a\r\u0000\u00e3\u00e4" +
                    "\u0006\u0016\uffff\uffff\u0000\u00e4\u0101\u0001\u0000\u0000\u0000\u00e5" +
                    "\u00e6\u0003\u001c\u000e\u0000\u00e6\u00e7\u0006\u0016\uffff\uffff\u0000" +
                    "\u00e7\u0101\u0001\u0000\u0000\u0000\u00e8\u00e9\u0003<\u001e\u0000\u00e9" +
                    "\u00ea\u0006\u0016\uffff\uffff\u0000\u00ea\u0101\u0001\u0000\u0000\u0000" +
                    "\u00eb\u00ec\u0003\u001e\u000f\u0000\u00ec\u00ed\u0006\u0016\uffff\uffff" +
                    "\u0000\u00ed\u0101\u0001\u0000\u0000\u0000\u00ee\u00ef\u0003 \u0010\u0000" +
                    "\u00ef\u00f0\u0006\u0016\uffff\uffff\u0000\u00f0\u0101\u0001\u0000\u0000" +
                    "\u0000\u00f1\u00f2\u0003\"\u0011\u0000\u00f2\u00f3\u0006\u0016\uffff\uffff" +
                    "\u0000\u00f3\u0101\u0001\u0000\u0000\u0000\u00f4\u00f5\u0003$\u0012\u0000" +
                    "\u00f5\u00f6\u0006\u0016\uffff\uffff\u0000\u00f6\u0101\u0001\u0000\u0000" +
                    "\u0000\u00f7\u00f8\u0003&\u0013\u0000\u00f8\u00f9\u0006\u0016\uffff\uffff" +
                    "\u0000\u00f9\u0101\u0001\u0000\u0000\u0000\u00fa\u00fb\u0003(\u0014\u0000" +
                    "\u00fb\u00fc\u0006\u0016\uffff\uffff\u0000\u00fc\u0101\u0001\u0000\u0000" +
                    "\u0000\u00fd\u00fe\u0003*\u0015\u0000\u00fe\u00ff\u0006\u0016\uffff\uffff" +
                    "\u0000\u00ff\u0101\u0001\u0000\u0000\u0000\u0100\u00c1\u0001\u0000\u0000" +
                    "\u0000\u0100\u00c4\u0001\u0000\u0000\u0000\u0100\u00c7\u0001\u0000\u0000" +
                    "\u0000\u0100\u00ca\u0001\u0000\u0000\u0000\u0100\u00cd\u0001\u0000\u0000" +
                    "\u0000\u0100\u00d0\u0001\u0000\u0000\u0000\u0100\u00d3\u0001\u0000\u0000" +
                    "\u0000\u0100\u00d6\u0001\u0000\u0000\u0000\u0100\u00d9\u0001\u0000\u0000" +
                    "\u0000\u0100\u00dc\u0001\u0000\u0000\u0000\u0100\u00df\u0001\u0000\u0000" +
                    "\u0000\u0100\u00e2\u0001\u0000\u0000\u0000\u0100\u00e5\u0001\u0000\u0000" +
                    "\u0000\u0100\u00e8\u0001\u0000\u0000\u0000\u0100\u00eb\u0001\u0000\u0000" +
                    "\u0000\u0100\u00ee\u0001\u0000\u0000\u0000\u0100\u00f1\u0001\u0000\u0000" +
                    "\u0000\u0100\u00f4\u0001\u0000\u0000\u0000\u0100\u00f7\u0001\u0000\u0000" +
                    "\u0000\u0100\u00fa\u0001\u0000\u0000\u0000\u0100\u00fd\u0001\u0000\u0000" +
                    "\u0000\u0101\u0102\u0001\u0000\u0000\u0000\u0102\u0100\u0001\u0000\u0000" +
                    "\u0000\u0102\u0103\u0001\u0000\u0000\u0000\u0103-\u0001\u0000\u0000\u0000" +
                    "\u0104\u0105\u0003\u0000\u0000\u0000\u0105\u0109\u0006\u0017\uffff\uffff" +
                    "\u0000\u0106\u0107\u0003,\u0016\u0000\u0107\u0108\u0006\u0017\uffff\uffff" +
                    "\u0000\u0108\u010a\u0001\u0000\u0000\u0000\u0109\u0106\u0001\u0000\u0000" +
                    "\u0000\u010a\u010b\u0001\u0000\u0000\u0000\u010b\u0109\u0001\u0000\u0000" +
                    "\u0000\u010b\u010c\u0001\u0000\u0000\u0000\u010c\u010d\u0001\u0000\u0000" +
                    "\u0000\u010d\u010e\u0005\u0000\u0000\u0001\u010e/\u0001\u0000\u0000\u0000" +
                    "\u010f\u0110\u0005\u0018\u0000\u0000\u0110\u0111\u0005\u001f\u0000\u0000" +
                    "\u0111\u011f\u0006\u0018\uffff\uffff\u0000\u0112\u0113\u0005\u001e\u0000" +
                    "\u0000\u0113\u0114\u0005\u001f\u0000\u0000\u0114\u011d\u0006\u0018\uffff" +
                    "\uffff\u0000\u0115\u0116\u0005\u001e\u0000\u0000\u0116\u0117\u0005 \u0000" +
                    "\u0000\u0117\u011b\u0006\u0018\uffff\uffff\u0000\u0118\u0119\u0005\u001e" +
                    "\u0000\u0000\u0119\u011a\u0005 \u0000\u0000\u011a\u011c\u0006\u0018\uffff" +
                    "\uffff\u0000\u011b\u0118\u0001\u0000\u0000\u0000\u011b\u011c\u0001\u0000" +
                    "\u0000\u0000\u011c\u011e\u0001\u0000\u0000\u0000\u011d\u0115\u0001\u0000" +
                    "\u0000\u0000\u011d\u011e\u0001\u0000\u0000\u0000\u011e\u0120\u0001\u0000" +
                    "\u0000\u0000\u011f\u0112\u0001\u0000\u0000\u0000\u011f\u0120\u0001\u0000" +
                    "\u0000\u0000\u01201\u0001\u0000\u0000\u0000\u0121\u0122\u0005\u001b\u0000" +
                    "\u0000\u0122\u0123\u0005<\u0000\u0000\u0123\u0124\u0006\u0019\uffff\uffff" +
                    "\u0000\u01243\u0001\u0000\u0000\u0000\u0125\u0126\u0005\u001c\u0000\u0000" +
                    "\u0126\u0127\u0005 \u0000\u0000\u0127\u0128\u0005\u001e\u0000\u0000\u0128" +
                    "\u0129\u0005 \u0000\u0000\u0129\u012a\u0006\u001a\uffff\uffff\u0000\u012a" +
                    "5\u0001\u0000\u0000\u0000\u012b\u0137\u0005\u001a\u0000\u0000\u012c\u012d" +
                    "\u0005=\u0000\u0000\u012d\u0131\u0006\u001b\uffff\uffff\u0000\u012e\u012f" +
                    "\u00038\u001c\u0000\u012f\u0130\u0006\u001b\uffff\uffff\u0000\u0130\u0132" +
                    "\u0001\u0000\u0000\u0000\u0131\u012e\u0001\u0000\u0000\u0000\u0132\u0133" +
                    "\u0001\u0000\u0000\u0000\u0133\u0131\u0001\u0000\u0000\u0000\u0133\u0134" +
                    "\u0001\u0000\u0000\u0000\u0134\u0138\u0001\u0000\u0000\u0000\u0135\u0136" +
                    "\u0005<\u0000\u0000\u0136\u0138\u0006\u001b\uffff\uffff\u0000\u0137\u012c" +
                    "\u0001\u0000\u0000\u0000\u0137\u0135\u0001\u0000\u0000\u0000\u01387\u0001" +
                    "\u0000\u0000\u0000\u0139\u013a\u0005\u001d\u0000\u0000\u013a\u013b\u0005" +
                    ">\u0000\u0000\u013b\u013c\u0005?\u0000\u0000\u013c\u013d\u0005@\u0000" +
                    "\u0000\u013d\u013e\u0005?\u0000\u0000\u013e\u013f\u0005A\u0000\u0000\u013f" +
                    "\u0140\u0006\u001c\uffff\uffff\u0000\u01409\u0001\u0000\u0000\u0000\u0141" +
                    "\u0142\u0005\u0019\u0000\u0000\u0142\u0143\u0005;\u0000\u0000\u0143\u0144" +
                    "\u0006\u001d\uffff\uffff\u0000\u0144;\u0001\u0000\u0000\u0000\u0145\u0146" +
                    "\u00030\u0018\u0000\u0146\u014f\u0006\u001e\uffff\uffff\u0000\u0147\u0148" +
                    "\u00034\u001a\u0000\u0148\u0149\u0006\u001e\uffff\uffff\u0000\u0149\u0150" +
                    "\u0001\u0000\u0000\u0000\u014a\u014b\u00036\u001b\u0000\u014b\u014c\u0006" +
                    "\u001e\uffff\uffff\u0000\u014c\u014e\u0001\u0000\u0000\u0000\u014d\u014a" +
                    "\u0001\u0000\u0000\u0000\u014d\u014e\u0001\u0000\u0000\u0000\u014e\u0150" +
                    "\u0001\u0000\u0000\u0000\u014f\u0147\u0001\u0000\u0000\u0000\u014f\u014d" +
                    "\u0001\u0000\u0000\u0000\u0150\u0154\u0001\u0000\u0000\u0000\u0151\u0152" +
                    "\u00032\u0019\u0000\u0152\u0153\u0006\u001e\uffff\uffff\u0000\u0153\u0155" +
                    "\u0001\u0000\u0000\u0000\u0154\u0151\u0001\u0000\u0000\u0000\u0154\u0155" +
                    "\u0001\u0000\u0000\u0000\u0155\u0159\u0001\u0000\u0000\u0000\u0156\u0157" +
                    "\u0003:\u001d\u0000\u0157\u0158\u0006\u001e\uffff\uffff\u0000\u0158\u015a" +
                    "\u0001\u0000\u0000\u0000\u0159\u0156\u0001\u0000\u0000\u0000\u0159\u015a" +
                    "\u0001\u0000\u0000\u0000\u015a=\u0001\u0000\u0000\u0000\u000f\u0091\u0095" +
                    "\u00a6\u0100\u0102\u010b\u011b\u011d\u011f\u0133\u0137\u014d\u014f\u0154" +
                    "\u0159";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}