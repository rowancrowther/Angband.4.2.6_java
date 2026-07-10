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
// Generated from ShapeGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.shape;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.parser.grammars.EffectParseRecord;
import uk.co.jackoftrades.backend.parser.shape.ShapeParseRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ShapeGrammar extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, NAME = 2, COMBAT = 3, SKILL_D_P = 4, SKILL_D_M = 5, SKILL_SAVE = 6,
            SKILL_STEALTH = 7, SKILL_SEARCH = 8, SKILL_MELEE = 9, SKILL_THROW = 10, SKILL_DIG = 11,
            OBJ_FLAGS = 12, PLAYER_FLAGS = 13, VALUES = 14, BLOW = 15, EFFECT = 16, EFFECT_MESSAGE = 17,
            DICE = 18, TIME = 19, EFFECT_YX = 20, EXPR = 21, COLON = 22, UCASE = 23, INTEGER = 24,
            SIMPLE_DICE_STRING = 25, COMPLEX_DICE_STRING = 26, COMMENT = 27, EOL = 28, FLAG = 29,
            FLAG_OR = 30, FLAG_EOL = 31, VALUE_MOD = 32, LBRACKET = 33, RBRACKET = 34, VALUE_INT = 35,
            VALUE_OR = 36, VALUE_EOL = 37, STRING = 38, FREE_TEXT = 39, DICE_SIMPLE_VALUE = 40,
            DICE_COMPLEX_VALUE = 41, EXPR_CHAR = 42, EXPR_COLON = 43, EXPR_UCASE = 44, EXPR_OP = 45,
            EXPR_EOL = 46;
    public static final int
            RULE_recordCount = 0, RULE_name = 1, RULE_combat = 2, RULE_skillDisarmP = 3,
            RULE_skillDisarmM = 4, RULE_skillSave = 5, RULE_skillStealth = 6, RULE_skillSearch = 7,
            RULE_skillMelee = 8, RULE_skillThrow = 9, RULE_skillDig = 10, RULE_objFlags = 11,
            RULE_playerFlags = 12, RULE_values = 13, RULE_blow = 14, RULE_shape = 15,
            RULE_file = 16, RULE_effect = 17, RULE_time = 18, RULE_effectYX = 19,
            RULE_dice = 20, RULE_expr = 21, RULE_effectMsg = 22, RULE_effectBlock = 23;

    private static String[] makeRuleNames() {
        return new String[]{
                "recordCount", "name", "combat", "skillDisarmP", "skillDisarmM", "skillSave",
                "skillStealth", "skillSearch", "skillMelee", "skillThrow", "skillDig",
                "objFlags", "playerFlags", "values", "blow", "shape", "file", "effect",
                "time", "effectYX", "dice", "expr", "effectMsg", "effectBlock"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'name:'", "'combat:'", "'skill-disarm-phys:'",
                "'skill-disarm-magic:'", "'skill-save:'", "'skill-stealth:'", "'skill-search:'",
                "'skill-melee:'", "'skill-throw:'", "'skill-dig:'", "'obj-flags:'", "'player-flags:'",
                "'values:'", "'blow:'", "'effect:'", "'effect-msg:'", "'dice:'", "'time:'",
                "'effect-yx:'", "'expr:'", null, null, null, null, null, null, null,
                null, null, null, null, "'['", "']'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "NAME", "COMBAT", "SKILL_D_P", "SKILL_D_M", "SKILL_SAVE",
                "SKILL_STEALTH", "SKILL_SEARCH", "SKILL_MELEE", "SKILL_THROW", "SKILL_DIG",
                "OBJ_FLAGS", "PLAYER_FLAGS", "VALUES", "BLOW", "EFFECT", "EFFECT_MESSAGE",
                "DICE", "TIME", "EFFECT_YX", "EXPR", "COLON", "UCASE", "INTEGER", "SIMPLE_DICE_STRING",
                "COMPLEX_DICE_STRING", "COMMENT", "EOL", "FLAG", "FLAG_OR", "FLAG_EOL",
                "VALUE_MOD", "LBRACKET", "RBRACKET", "VALUE_INT", "VALUE_OR", "VALUE_EOL",
                "STRING", "FREE_TEXT", "DICE_SIMPLE_VALUE", "DICE_COMPLEX_VALUE", "EXPR_CHAR",
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

    @Override
    public String getGrammarFileName() {
        return "ShapeGrammar.g4";
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

    public ShapeGrammar(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RecordCountContext extends ParserRuleContext {
        public String count;
        public Token INTEGER;

        public TerminalNode RECORD_COUNT() {
            return getToken(ShapeGrammar.RECORD_COUNT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(ShapeGrammar.INTEGER, 0);
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
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).enterRecordCount(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).exitRecordCount(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeGrammarVisitor)
                return ((ShapeGrammarVisitor<? extends T>) visitor).visitRecordCount(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RecordCountContext recordCount() throws RecognitionException {
        RecordCountContext _localctx = new RecordCountContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_recordCount);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(48);
                match(RECORD_COUNT);
                setState(49);
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
        public int lineNo;
        public Token STRING;

        public TerminalNode NAME() {
            return getToken(ShapeGrammar.NAME, 0);
        }

        public TerminalNode STRING() {
            return getToken(ShapeGrammar.STRING, 0);
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
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeGrammarVisitor)
                return ((ShapeGrammarVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(52);
                match(NAME);
                setState(53);
                ((NameContext) _localctx).STRING = match(STRING);
                ((NameContext) _localctx).nameStr = ((NameContext) _localctx).STRING.getText();
                ((NameContext) _localctx).lineNo = _localctx.start.getLine();
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
    public static class CombatContext extends ParserRuleContext {
        public String toh;
        public String tod;
        public String toa;
        public Token h;
        public Token d;
        public Token a;

        public TerminalNode COMBAT() {
            return getToken(ShapeGrammar.COMBAT, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(ShapeGrammar.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(ShapeGrammar.COLON, i);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(ShapeGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(ShapeGrammar.INTEGER, i);
        }

        public CombatContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_combat;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).enterCombat(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).exitCombat(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeGrammarVisitor)
                return ((ShapeGrammarVisitor<? extends T>) visitor).visitCombat(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CombatContext combat() throws RecognitionException {
        CombatContext _localctx = new CombatContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_combat);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(56);
                match(COMBAT);
                setState(57);
                ((CombatContext) _localctx).h = match(INTEGER);
                setState(58);
                match(COLON);
                setState(59);
                ((CombatContext) _localctx).d = match(INTEGER);
                setState(60);
                match(COLON);
                setState(61);
                ((CombatContext) _localctx).a = match(INTEGER);

                ((CombatContext) _localctx).toh = ((CombatContext) _localctx).h.getText();
                ((CombatContext) _localctx).tod = ((CombatContext) _localctx).d.getText();
                ((CombatContext) _localctx).toa = ((CombatContext) _localctx).a.getText();

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
    public static class SkillDisarmPContext extends ParserRuleContext {
        public String skillVal;
        public Token INTEGER;

        public TerminalNode SKILL_D_P() {
            return getToken(ShapeGrammar.SKILL_D_P, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(ShapeGrammar.INTEGER, 0);
        }

        public SkillDisarmPContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillDisarmP;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).enterSkillDisarmP(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).exitSkillDisarmP(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeGrammarVisitor)
                return ((ShapeGrammarVisitor<? extends T>) visitor).visitSkillDisarmP(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillDisarmPContext skillDisarmP() throws RecognitionException {
        SkillDisarmPContext _localctx = new SkillDisarmPContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_skillDisarmP);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(64);
                match(SKILL_D_P);
                setState(65);
                ((SkillDisarmPContext) _localctx).INTEGER = match(INTEGER);
                ((SkillDisarmPContext) _localctx).skillVal = ((SkillDisarmPContext) _localctx).INTEGER.getText();
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
    public static class SkillDisarmMContext extends ParserRuleContext {
        public String skillVal;
        public Token INTEGER;

        public TerminalNode SKILL_D_M() {
            return getToken(ShapeGrammar.SKILL_D_M, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(ShapeGrammar.INTEGER, 0);
        }

        public SkillDisarmMContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillDisarmM;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).enterSkillDisarmM(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).exitSkillDisarmM(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeGrammarVisitor)
                return ((ShapeGrammarVisitor<? extends T>) visitor).visitSkillDisarmM(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillDisarmMContext skillDisarmM() throws RecognitionException {
        SkillDisarmMContext _localctx = new SkillDisarmMContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_skillDisarmM);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(68);
                match(SKILL_D_M);
                setState(69);
                ((SkillDisarmMContext) _localctx).INTEGER = match(INTEGER);
                ((SkillDisarmMContext) _localctx).skillVal = ((SkillDisarmMContext) _localctx).INTEGER.getText();
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
    public static class SkillSaveContext extends ParserRuleContext {
        public String skillVal;
        public Token INTEGER;

        public TerminalNode SKILL_SAVE() {
            return getToken(ShapeGrammar.SKILL_SAVE, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(ShapeGrammar.INTEGER, 0);
        }

        public SkillSaveContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillSave;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).enterSkillSave(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).exitSkillSave(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeGrammarVisitor)
                return ((ShapeGrammarVisitor<? extends T>) visitor).visitSkillSave(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillSaveContext skillSave() throws RecognitionException {
        SkillSaveContext _localctx = new SkillSaveContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_skillSave);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(72);
                match(SKILL_SAVE);
                setState(73);
                ((SkillSaveContext) _localctx).INTEGER = match(INTEGER);
                ((SkillSaveContext) _localctx).skillVal = ((SkillSaveContext) _localctx).INTEGER.getText();
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
    public static class SkillStealthContext extends ParserRuleContext {
        public String skillVal;
        public Token INTEGER;

        public TerminalNode SKILL_STEALTH() {
            return getToken(ShapeGrammar.SKILL_STEALTH, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(ShapeGrammar.INTEGER, 0);
        }

        public SkillStealthContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillStealth;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).enterSkillStealth(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).exitSkillStealth(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeGrammarVisitor)
                return ((ShapeGrammarVisitor<? extends T>) visitor).visitSkillStealth(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillStealthContext skillStealth() throws RecognitionException {
        SkillStealthContext _localctx = new SkillStealthContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_skillStealth);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(76);
                match(SKILL_STEALTH);
                setState(77);
                ((SkillStealthContext) _localctx).INTEGER = match(INTEGER);
                ((SkillStealthContext) _localctx).skillVal = ((SkillStealthContext) _localctx).INTEGER.getText();
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
    public static class SkillSearchContext extends ParserRuleContext {
        public String skillVal;
        public Token INTEGER;

        public TerminalNode SKILL_SEARCH() {
            return getToken(ShapeGrammar.SKILL_SEARCH, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(ShapeGrammar.INTEGER, 0);
        }

        public SkillSearchContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillSearch;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).enterSkillSearch(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).exitSkillSearch(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeGrammarVisitor)
                return ((ShapeGrammarVisitor<? extends T>) visitor).visitSkillSearch(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillSearchContext skillSearch() throws RecognitionException {
        SkillSearchContext _localctx = new SkillSearchContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_skillSearch);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(80);
                match(SKILL_SEARCH);
                setState(81);
                ((SkillSearchContext) _localctx).INTEGER = match(INTEGER);
                ((SkillSearchContext) _localctx).skillVal = ((SkillSearchContext) _localctx).INTEGER.getText();
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
    public static class SkillMeleeContext extends ParserRuleContext {
        public String skillVal;
        public Token INTEGER;

        public TerminalNode SKILL_MELEE() {
            return getToken(ShapeGrammar.SKILL_MELEE, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(ShapeGrammar.INTEGER, 0);
        }

        public SkillMeleeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillMelee;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).enterSkillMelee(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).exitSkillMelee(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeGrammarVisitor)
                return ((ShapeGrammarVisitor<? extends T>) visitor).visitSkillMelee(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillMeleeContext skillMelee() throws RecognitionException {
        SkillMeleeContext _localctx = new SkillMeleeContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_skillMelee);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(84);
                match(SKILL_MELEE);
                setState(85);
                ((SkillMeleeContext) _localctx).INTEGER = match(INTEGER);
                ((SkillMeleeContext) _localctx).skillVal = ((SkillMeleeContext) _localctx).INTEGER.getText();
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
    public static class SkillThrowContext extends ParserRuleContext {
        public String skillVal;
        public Token INTEGER;

        public TerminalNode SKILL_THROW() {
            return getToken(ShapeGrammar.SKILL_THROW, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(ShapeGrammar.INTEGER, 0);
        }

        public SkillThrowContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillThrow;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).enterSkillThrow(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).exitSkillThrow(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeGrammarVisitor)
                return ((ShapeGrammarVisitor<? extends T>) visitor).visitSkillThrow(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillThrowContext skillThrow() throws RecognitionException {
        SkillThrowContext _localctx = new SkillThrowContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_skillThrow);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(88);
                match(SKILL_THROW);
                setState(89);
                ((SkillThrowContext) _localctx).INTEGER = match(INTEGER);
                ((SkillThrowContext) _localctx).skillVal = ((SkillThrowContext) _localctx).INTEGER.getText();
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
    public static class SkillDigContext extends ParserRuleContext {
        public String skillVal;
        public Token INTEGER;

        public TerminalNode SKILL_DIG() {
            return getToken(ShapeGrammar.SKILL_DIG, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(ShapeGrammar.INTEGER, 0);
        }

        public SkillDigContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillDig;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).enterSkillDig(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).exitSkillDig(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeGrammarVisitor)
                return ((ShapeGrammarVisitor<? extends T>) visitor).visitSkillDig(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillDigContext skillDig() throws RecognitionException {
        SkillDigContext _localctx = new SkillDigContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_skillDig);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(92);
                match(SKILL_DIG);
                setState(93);
                ((SkillDigContext) _localctx).INTEGER = match(INTEGER);
                ((SkillDigContext) _localctx).skillVal = ((SkillDigContext) _localctx).INTEGER.getText();
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
    public static class ObjFlagsContext extends ParserRuleContext {
        public List<String> flags;
        public Token f1;
        public Token f2;

        public TerminalNode OBJ_FLAGS() {
            return getToken(ShapeGrammar.OBJ_FLAGS, 0);
        }

        public List<TerminalNode> FLAG() {
            return getTokens(ShapeGrammar.FLAG);
        }

        public TerminalNode FLAG(int i) {
            return getToken(ShapeGrammar.FLAG, i);
        }

        public List<TerminalNode> FLAG_OR() {
            return getTokens(ShapeGrammar.FLAG_OR);
        }

        public TerminalNode FLAG_OR(int i) {
            return getToken(ShapeGrammar.FLAG_OR, i);
        }

        public ObjFlagsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_objFlags;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).enterObjFlags(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).exitObjFlags(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeGrammarVisitor)
                return ((ShapeGrammarVisitor<? extends T>) visitor).visitObjFlags(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ObjFlagsContext objFlags() throws RecognitionException {
        ObjFlagsContext _localctx = new ObjFlagsContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_objFlags);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                ((ObjFlagsContext) _localctx).flags = new ArrayList<>();
                setState(97);
                match(OBJ_FLAGS);
                setState(98);
                ((ObjFlagsContext) _localctx).f1 = match(FLAG);

                _localctx.flags.add(((ObjFlagsContext) _localctx).f1.getText());

                setState(105);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == FLAG_OR) {
                    {
                        {
                            setState(100);
                            match(FLAG_OR);
                            setState(101);
                            ((ObjFlagsContext) _localctx).f2 = match(FLAG);

                            _localctx.flags.add(((ObjFlagsContext) _localctx).f2.getText());

                        }
                    }
                    setState(107);
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
    public static class PlayerFlagsContext extends ParserRuleContext {
        public List<String> flags;
        public Token f1;
        public Token f2;

        public TerminalNode PLAYER_FLAGS() {
            return getToken(ShapeGrammar.PLAYER_FLAGS, 0);
        }

        public List<TerminalNode> FLAG() {
            return getTokens(ShapeGrammar.FLAG);
        }

        public TerminalNode FLAG(int i) {
            return getToken(ShapeGrammar.FLAG, i);
        }

        public List<TerminalNode> FLAG_OR() {
            return getTokens(ShapeGrammar.FLAG_OR);
        }

        public TerminalNode FLAG_OR(int i) {
            return getToken(ShapeGrammar.FLAG_OR, i);
        }

        public PlayerFlagsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_playerFlags;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).enterPlayerFlags(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).exitPlayerFlags(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeGrammarVisitor)
                return ((ShapeGrammarVisitor<? extends T>) visitor).visitPlayerFlags(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PlayerFlagsContext playerFlags() throws RecognitionException {
        PlayerFlagsContext _localctx = new PlayerFlagsContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_playerFlags);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                ((PlayerFlagsContext) _localctx).flags = new ArrayList<>();
                setState(109);
                match(PLAYER_FLAGS);
                setState(110);
                ((PlayerFlagsContext) _localctx).f1 = match(FLAG);

                _localctx.flags.add(((PlayerFlagsContext) _localctx).f1.getText());

                setState(117);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == FLAG_OR) {
                    {
                        {
                            setState(112);
                            match(FLAG_OR);
                            setState(113);
                            ((PlayerFlagsContext) _localctx).f2 = match(FLAG);

                            _localctx.flags.add(((PlayerFlagsContext) _localctx).f2.getText());

                        }
                    }
                    setState(119);
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
    public static class ValuesContext extends ParserRuleContext {
        public Map<String, String> valueMap;
        public Token vm1;
        public Token vi1;
        public Token vm2;
        public Token vi2;

        public TerminalNode VALUES() {
            return getToken(ShapeGrammar.VALUES, 0);
        }

        public List<TerminalNode> LBRACKET() {
            return getTokens(ShapeGrammar.LBRACKET);
        }

        public TerminalNode LBRACKET(int i) {
            return getToken(ShapeGrammar.LBRACKET, i);
        }

        public List<TerminalNode> RBRACKET() {
            return getTokens(ShapeGrammar.RBRACKET);
        }

        public TerminalNode RBRACKET(int i) {
            return getToken(ShapeGrammar.RBRACKET, i);
        }

        public List<TerminalNode> VALUE_MOD() {
            return getTokens(ShapeGrammar.VALUE_MOD);
        }

        public TerminalNode VALUE_MOD(int i) {
            return getToken(ShapeGrammar.VALUE_MOD, i);
        }

        public List<TerminalNode> VALUE_INT() {
            return getTokens(ShapeGrammar.VALUE_INT);
        }

        public TerminalNode VALUE_INT(int i) {
            return getToken(ShapeGrammar.VALUE_INT, i);
        }

        public List<TerminalNode> VALUE_OR() {
            return getTokens(ShapeGrammar.VALUE_OR);
        }

        public TerminalNode VALUE_OR(int i) {
            return getToken(ShapeGrammar.VALUE_OR, i);
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
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).enterValues(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).exitValues(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeGrammarVisitor)
                return ((ShapeGrammarVisitor<? extends T>) visitor).visitValues(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ValuesContext values() throws RecognitionException {
        ValuesContext _localctx = new ValuesContext(_ctx, getState());
        enterRule(_localctx, 26, RULE_values);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {

                ((ValuesContext) _localctx).valueMap = new HashMap<>();

                setState(121);
                match(VALUES);
                setState(122);
                ((ValuesContext) _localctx).vm1 = match(VALUE_MOD);
                setState(123);
                match(LBRACKET);
                setState(124);
                ((ValuesContext) _localctx).vi1 = match(VALUE_INT);
                setState(125);
                match(RBRACKET);

                _localctx.valueMap.put(((ValuesContext) _localctx).vm1.getText(), ((ValuesContext) _localctx).vi1.getText());

                setState(135);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == VALUE_OR) {
                    {
                        {
                            setState(127);
                            match(VALUE_OR);
                            setState(128);
                            ((ValuesContext) _localctx).vm2 = match(VALUE_MOD);
                            setState(129);
                            match(LBRACKET);
                            setState(130);
                            ((ValuesContext) _localctx).vi2 = match(VALUE_INT);
                            setState(131);
                            match(RBRACKET);

                            _localctx.valueMap.put(((ValuesContext) _localctx).vm2.getText(), ((ValuesContext) _localctx).vi2.getText());

                        }
                    }
                    setState(137);
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
    public static class BlowContext extends ParserRuleContext {
        public String blowStr;
        public Token STRING;

        public TerminalNode BLOW() {
            return getToken(ShapeGrammar.BLOW, 0);
        }

        public TerminalNode STRING() {
            return getToken(ShapeGrammar.STRING, 0);
        }

        public BlowContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_blow;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).enterBlow(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).exitBlow(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeGrammarVisitor)
                return ((ShapeGrammarVisitor<? extends T>) visitor).visitBlow(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BlowContext blow() throws RecognitionException {
        BlowContext _localctx = new BlowContext(_ctx, getState());
        enterRule(_localctx, 28, RULE_blow);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(138);
                match(BLOW);
                setState(139);
                ((BlowContext) _localctx).STRING = match(STRING);
                ((BlowContext) _localctx).blowStr = ((BlowContext) _localctx).STRING.getText();
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
    public static class ShapeContext extends ParserRuleContext {
        public ShapeParseRecord shapeInit;
        public NameContext name;
        public CombatContext combat;
        public SkillDisarmPContext skillDisarmP;
        public SkillDisarmMContext skillDisarmM;
        public SkillSaveContext skillSave;
        public SkillStealthContext skillStealth;
        public SkillSearchContext skillSearch;
        public SkillMeleeContext skillMelee;
        public SkillThrowContext skillThrow;
        public SkillDigContext skillDig;
        public ObjFlagsContext objFlags;
        public PlayerFlagsContext playerFlags;
        public ValuesContext values;
        public EffectBlockContext effectBlock;
        public BlowContext blow;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public List<CombatContext> combat() {
            return getRuleContexts(CombatContext.class);
        }

        public CombatContext combat(int i) {
            return getRuleContext(CombatContext.class, i);
        }

        public List<SkillDisarmPContext> skillDisarmP() {
            return getRuleContexts(SkillDisarmPContext.class);
        }

        public SkillDisarmPContext skillDisarmP(int i) {
            return getRuleContext(SkillDisarmPContext.class, i);
        }

        public List<SkillDisarmMContext> skillDisarmM() {
            return getRuleContexts(SkillDisarmMContext.class);
        }

        public SkillDisarmMContext skillDisarmM(int i) {
            return getRuleContext(SkillDisarmMContext.class, i);
        }

        public List<SkillSaveContext> skillSave() {
            return getRuleContexts(SkillSaveContext.class);
        }

        public SkillSaveContext skillSave(int i) {
            return getRuleContext(SkillSaveContext.class, i);
        }

        public List<SkillStealthContext> skillStealth() {
            return getRuleContexts(SkillStealthContext.class);
        }

        public SkillStealthContext skillStealth(int i) {
            return getRuleContext(SkillStealthContext.class, i);
        }

        public List<SkillSearchContext> skillSearch() {
            return getRuleContexts(SkillSearchContext.class);
        }

        public SkillSearchContext skillSearch(int i) {
            return getRuleContext(SkillSearchContext.class, i);
        }

        public List<SkillMeleeContext> skillMelee() {
            return getRuleContexts(SkillMeleeContext.class);
        }

        public SkillMeleeContext skillMelee(int i) {
            return getRuleContext(SkillMeleeContext.class, i);
        }

        public List<SkillThrowContext> skillThrow() {
            return getRuleContexts(SkillThrowContext.class);
        }

        public SkillThrowContext skillThrow(int i) {
            return getRuleContext(SkillThrowContext.class, i);
        }

        public List<SkillDigContext> skillDig() {
            return getRuleContexts(SkillDigContext.class);
        }

        public SkillDigContext skillDig(int i) {
            return getRuleContext(SkillDigContext.class, i);
        }

        public List<ObjFlagsContext> objFlags() {
            return getRuleContexts(ObjFlagsContext.class);
        }

        public ObjFlagsContext objFlags(int i) {
            return getRuleContext(ObjFlagsContext.class, i);
        }

        public List<PlayerFlagsContext> playerFlags() {
            return getRuleContexts(PlayerFlagsContext.class);
        }

        public PlayerFlagsContext playerFlags(int i) {
            return getRuleContext(PlayerFlagsContext.class, i);
        }

        public List<ValuesContext> values() {
            return getRuleContexts(ValuesContext.class);
        }

        public ValuesContext values(int i) {
            return getRuleContext(ValuesContext.class, i);
        }

        public List<EffectBlockContext> effectBlock() {
            return getRuleContexts(EffectBlockContext.class);
        }

        public EffectBlockContext effectBlock(int i) {
            return getRuleContext(EffectBlockContext.class, i);
        }

        public List<BlowContext> blow() {
            return getRuleContexts(BlowContext.class);
        }

        public BlowContext blow(int i) {
            return getRuleContext(BlowContext.class, i);
        }

        public ShapeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_shape;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).enterShape(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).exitShape(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeGrammarVisitor)
                return ((ShapeGrammarVisitor<? extends T>) visitor).visitShape(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ShapeContext shape() throws RecognitionException {
        ShapeContext _localctx = new ShapeContext(_ctx, getState());
        enterRule(_localctx, 30, RULE_shape);

        String nameInit = "";
        String combatTohInit = "";
        String combatTodInit = "";
        String combatToaInit = "";
        String skillDisarmPhysInit = "";
        String skillDisarmMagicInit = "";
        String skillSaveInit = "";
        String skillStealthInit = "";
        String skillSearchInit = "";
        String skillMeleeInit = "";
        String skillThrowInit = "";
        String skillDigInit = "";
        List<String> oFlagsInit = new ArrayList<>();
        List<String> pFlagsInit = new ArrayList<>();
        Map<String, String> valuesInit = new HashMap<>();
        List<EffectParseRecord> effectsInit = new ArrayList<>();
        List<String> blowsInit = new ArrayList<>();
        int line = 0;

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(142);
                ((ShapeContext) _localctx).name = name();
                nameInit = ((ShapeContext) _localctx).name.nameStr;
                line = ((ShapeContext) _localctx).name.lineNo;
                setState(188);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 131064L) != 0)) {
                    {
                        setState(186);
                        _errHandler.sync(this);
                        switch (_input.LA(1)) {
                            case COMBAT: {
                                setState(144);
                                ((ShapeContext) _localctx).combat = combat();

                                combatTohInit = ((ShapeContext) _localctx).combat.toh;
                                combatTodInit = ((ShapeContext) _localctx).combat.tod;
                                combatToaInit = ((ShapeContext) _localctx).combat.toa;

                            }
                            break;
                            case SKILL_D_P: {
                                setState(147);
                                ((ShapeContext) _localctx).skillDisarmP = skillDisarmP();
                                skillDisarmPhysInit = ((ShapeContext) _localctx).skillDisarmP.skillVal;
                            }
                            break;
                            case SKILL_D_M: {
                                setState(150);
                                ((ShapeContext) _localctx).skillDisarmM = skillDisarmM();
                                skillDisarmMagicInit = ((ShapeContext) _localctx).skillDisarmM.skillVal;
                            }
                            break;
                            case SKILL_SAVE: {
                                setState(153);
                                ((ShapeContext) _localctx).skillSave = skillSave();
                                skillSaveInit = ((ShapeContext) _localctx).skillSave.skillVal;
                            }
                            break;
                            case SKILL_STEALTH: {
                                setState(156);
                                ((ShapeContext) _localctx).skillStealth = skillStealth();
                                skillStealthInit = ((ShapeContext) _localctx).skillStealth.skillVal;
                            }
                            break;
                            case SKILL_SEARCH: {
                                setState(159);
                                ((ShapeContext) _localctx).skillSearch = skillSearch();
                                skillSearchInit = ((ShapeContext) _localctx).skillSearch.skillVal;
                            }
                            break;
                            case SKILL_MELEE: {
                                setState(162);
                                ((ShapeContext) _localctx).skillMelee = skillMelee();
                                skillMeleeInit = ((ShapeContext) _localctx).skillMelee.skillVal;
                            }
                            break;
                            case SKILL_THROW: {
                                setState(165);
                                ((ShapeContext) _localctx).skillThrow = skillThrow();
                                skillThrowInit = ((ShapeContext) _localctx).skillThrow.skillVal;
                            }
                            break;
                            case SKILL_DIG: {
                                setState(168);
                                ((ShapeContext) _localctx).skillDig = skillDig();
                                skillDigInit = ((ShapeContext) _localctx).skillDig.skillVal;
                            }
                            break;
                            case OBJ_FLAGS: {
                                setState(171);
                                ((ShapeContext) _localctx).objFlags = objFlags();
                                oFlagsInit.addAll(((ShapeContext) _localctx).objFlags.flags);
                            }
                            break;
                            case PLAYER_FLAGS: {
                                setState(174);
                                ((ShapeContext) _localctx).playerFlags = playerFlags();
                                pFlagsInit.addAll(((ShapeContext) _localctx).playerFlags.flags);
                            }
                            break;
                            case VALUES: {
                                setState(177);
                                ((ShapeContext) _localctx).values = values();
                                valuesInit.putAll(((ShapeContext) _localctx).values.valueMap);
                            }
                            break;
                            case EFFECT: {
                                setState(180);
                                ((ShapeContext) _localctx).effectBlock = effectBlock();
                                effectsInit.add(new EffectParseRecord(((ShapeContext) _localctx).effectBlock.typeInit,
                                        ((ShapeContext) _localctx).effectBlock.subtypeWrapperInit, ((ShapeContext) _localctx).effectBlock.radius, ((ShapeContext) _localctx).effectBlock.other,
                                        ((ShapeContext) _localctx).effectBlock.diceString, ((ShapeContext) _localctx).effectBlock.yVal, ((ShapeContext) _localctx).effectBlock.xVal,
                                        ((ShapeContext) _localctx).effectBlock.expressionChars, ((ShapeContext) _localctx).effectBlock.expressionBase,
                                        ((ShapeContext) _localctx).effectBlock.expressionOperation, ((ShapeContext) _localctx).effectBlock.timeDiceString,
                                        ((ShapeContext) _localctx).effectBlock.effectMessage, (((ShapeContext) _localctx).effectBlock != null ? (((ShapeContext) _localctx).effectBlock.start) : null).getLine()));
                            }
                            break;
                            case BLOW: {
                                setState(183);
                                ((ShapeContext) _localctx).blow = blow();
                                blowsInit.add(((ShapeContext) _localctx).blow.blowStr);
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                    }
                    setState(190);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
            }
            _ctx.stop = _input.LT(-1);

            ((ShapeContext) _localctx).shapeInit = new ShapeParseRecord(nameInit,
                    combatTohInit, combatTodInit, combatToaInit,
                    skillDisarmPhysInit, skillDisarmMagicInit,
                    skillSaveInit, skillStealthInit, skillSearchInit,
                    skillMeleeInit, skillThrowInit, skillDigInit,
                    oFlagsInit, pFlagsInit, valuesInit,
                    effectsInit, blowsInit, line);

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
        public List<ShapeParseRecord> shapes;
        public String declaredRecordCount;
        public RecordCountContext recordCount;
        public ShapeContext shape;

        public RecordCountContext recordCount() {
            return getRuleContext(RecordCountContext.class, 0);
        }

        public TerminalNode EOF() {
            return getToken(ShapeGrammar.EOF, 0);
        }

        public List<ShapeContext> shape() {
            return getRuleContexts(ShapeContext.class);
        }

        public ShapeContext shape(int i) {
            return getRuleContext(ShapeContext.class, i);
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
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeGrammarVisitor)
                return ((ShapeGrammarVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 32, RULE_file);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(191);
                ((FileContext) _localctx).recordCount = recordCount();
                ((FileContext) _localctx).declaredRecordCount = ((FileContext) _localctx).recordCount.count;
                ((FileContext) _localctx).shapes = new ArrayList<>();
                setState(196);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(193);
                            ((FileContext) _localctx).shape = shape();
                            _localctx.shapes.add(((FileContext) _localctx).shape.shapeInit);
                        }
                    }
                    setState(198);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(200);
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
            return getToken(ShapeGrammar.EFFECT, 0);
        }

        public List<TerminalNode> UCASE() {
            return getTokens(ShapeGrammar.UCASE);
        }

        public TerminalNode UCASE(int i) {
            return getToken(ShapeGrammar.UCASE, i);
        }

        public List<TerminalNode> COLON() {
            return getTokens(ShapeGrammar.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(ShapeGrammar.COLON, i);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(ShapeGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(ShapeGrammar.INTEGER, i);
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
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).enterEffect(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).exitEffect(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeGrammarVisitor)
                return ((ShapeGrammarVisitor<? extends T>) visitor).visitEffect(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectContext effect() throws RecognitionException {
        EffectContext _localctx = new EffectContext(_ctx, getState());
        enterRule(_localctx, 34, RULE_effect);

        ((EffectContext) _localctx).wrapper = "";
        ((EffectContext) _localctx).radius = "";
        ((EffectContext) _localctx).other = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(202);
                match(EFFECT);
                setState(203);
                ((EffectContext) _localctx).t = match(UCASE);

                ((EffectContext) _localctx).type = ((EffectContext) _localctx).t.getText();

                setState(218);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COLON) {
                    {
                        setState(205);
                        match(COLON);
                        setState(206);
                        ((EffectContext) _localctx).st = match(UCASE);

                        ((EffectContext) _localctx).wrapper = ((EffectContext) _localctx).st.getText().toUpperCase();

                        setState(216);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == COLON) {
                            {
                                setState(208);
                                match(COLON);
                                setState(209);
                                ((EffectContext) _localctx).rad = match(INTEGER);

                                ((EffectContext) _localctx).radius = ((EffectContext) _localctx).rad.getText();

                                setState(214);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                                if (_la == COLON) {
                                    {
                                        setState(211);
                                        match(COLON);
                                        setState(212);
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
            return getToken(ShapeGrammar.TIME, 0);
        }

        public TerminalNode DICE_SIMPLE_VALUE() {
            return getToken(ShapeGrammar.DICE_SIMPLE_VALUE, 0);
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
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).enterTime(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).exitTime(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeGrammarVisitor)
                return ((ShapeGrammarVisitor<? extends T>) visitor).visitTime(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TimeContext time() throws RecognitionException {
        TimeContext _localctx = new TimeContext(_ctx, getState());
        enterRule(_localctx, 36, RULE_time);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(220);
                match(TIME);
                setState(221);
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
            return getToken(ShapeGrammar.EFFECT_YX, 0);
        }

        public TerminalNode COLON() {
            return getToken(ShapeGrammar.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(ShapeGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(ShapeGrammar.INTEGER, i);
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
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).enterEffectYX(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).exitEffectYX(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeGrammarVisitor)
                return ((ShapeGrammarVisitor<? extends T>) visitor).visitEffectYX(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectYXContext effectYX() throws RecognitionException {
        EffectYXContext _localctx = new EffectYXContext(_ctx, getState());
        enterRule(_localctx, 38, RULE_effectYX);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(224);
                match(EFFECT_YX);
                setState(225);
                ((EffectYXContext) _localctx).yVal = match(INTEGER);
                setState(226);
                match(COLON);
                setState(227);
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
            return getToken(ShapeGrammar.DICE, 0);
        }

        public TerminalNode DICE_SIMPLE_VALUE() {
            return getToken(ShapeGrammar.DICE_SIMPLE_VALUE, 0);
        }

        public TerminalNode DICE_COMPLEX_VALUE() {
            return getToken(ShapeGrammar.DICE_COMPLEX_VALUE, 0);
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
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).enterDice(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).exitDice(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeGrammarVisitor)
                return ((ShapeGrammarVisitor<? extends T>) visitor).visitDice(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DiceContext dice() throws RecognitionException {
        DiceContext _localctx = new DiceContext(_ctx, getState());
        enterRule(_localctx, 40, RULE_dice);

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
                setState(230);
                match(DICE);
                setState(242);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case DICE_COMPLEX_VALUE: {
                        {
                            setState(231);
                            ((DiceContext) _localctx).val = match(DICE_COMPLEX_VALUE);

                            ((DiceContext) _localctx).diceString = ((DiceContext) _localctx).val.getText();

                            setState(236);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            do {
                                {
                                    {
                                        setState(233);
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
                                setState(238);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                            } while (_la == EXPR);
                        }
                    }
                    break;
                    case DICE_SIMPLE_VALUE: {
                        setState(240);
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
            return getToken(ShapeGrammar.EXPR, 0);
        }

        public List<TerminalNode> EXPR_COLON() {
            return getTokens(ShapeGrammar.EXPR_COLON);
        }

        public TerminalNode EXPR_COLON(int i) {
            return getToken(ShapeGrammar.EXPR_COLON, i);
        }

        public TerminalNode EXPR_CHAR() {
            return getToken(ShapeGrammar.EXPR_CHAR, 0);
        }

        public TerminalNode EXPR_UCASE() {
            return getToken(ShapeGrammar.EXPR_UCASE, 0);
        }

        public TerminalNode EXPR_OP() {
            return getToken(ShapeGrammar.EXPR_OP, 0);
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
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).enterExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).exitExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeGrammarVisitor)
                return ((ShapeGrammarVisitor<? extends T>) visitor).visitExpr(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExprContext expr() throws RecognitionException {
        ExprContext _localctx = new ExprContext(_ctx, getState());
        enterRule(_localctx, 42, RULE_expr);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(244);
                match(EXPR);
                setState(245);
                ((ExprContext) _localctx).ch = match(EXPR_CHAR);
                setState(246);
                match(EXPR_COLON);
                setState(247);
                ((ExprContext) _localctx).base = match(EXPR_UCASE);
                setState(248);
                match(EXPR_COLON);
                setState(249);
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
            return getToken(ShapeGrammar.EFFECT_MESSAGE, 0);
        }

        public TerminalNode FREE_TEXT() {
            return getToken(ShapeGrammar.FREE_TEXT, 0);
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
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).enterEffectMsg(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).exitEffectMsg(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeGrammarVisitor)
                return ((ShapeGrammarVisitor<? extends T>) visitor).visitEffectMsg(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectMsgContext effectMsg() throws RecognitionException {
        EffectMsgContext _localctx = new EffectMsgContext(_ctx, getState());
        enterRule(_localctx, 44, RULE_effectMsg);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(252);
                match(EFFECT_MESSAGE);
                setState(253);
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
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).enterEffectBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ShapeGrammarListener) ((ShapeGrammarListener) listener).exitEffectBlock(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ShapeGrammarVisitor)
                return ((ShapeGrammarVisitor<? extends T>) visitor).visitEffectBlock(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectBlockContext effectBlock() throws RecognitionException {
        EffectBlockContext _localctx = new EffectBlockContext(_ctx, getState());
        enterRule(_localctx, 46, RULE_effectBlock);

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
                setState(256);
                ((EffectBlockContext) _localctx).effect = effect();

                ((EffectBlockContext) _localctx).lineNo = _localctx.start.getLine();
                ((EffectBlockContext) _localctx).typeInit = ((EffectBlockContext) _localctx).effect.type;
                ((EffectBlockContext) _localctx).subtypeWrapperInit = ((EffectBlockContext) _localctx).effect.wrapper;
                ((EffectBlockContext) _localctx).radius = ((EffectBlockContext) _localctx).effect.radius;
                ((EffectBlockContext) _localctx).other = ((EffectBlockContext) _localctx).effect.other;

                setState(266);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case EFFECT_YX: {
                        {
                            setState(258);
                            ((EffectBlockContext) _localctx).effectYX = effectYX();

                            ((EffectBlockContext) _localctx).yVal = ((EffectBlockContext) _localctx).effectYX.y;
                            ((EffectBlockContext) _localctx).xVal = ((EffectBlockContext) _localctx).effectYX.x;

                        }
                    }
                    break;
                    case EOF:
                    case NAME:
                    case COMBAT:
                    case SKILL_D_P:
                    case SKILL_D_M:
                    case SKILL_SAVE:
                    case SKILL_STEALTH:
                    case SKILL_SEARCH:
                    case SKILL_MELEE:
                    case SKILL_THROW:
                    case SKILL_DIG:
                    case OBJ_FLAGS:
                    case PLAYER_FLAGS:
                    case VALUES:
                    case BLOW:
                    case EFFECT:
                    case EFFECT_MESSAGE:
                    case DICE:
                    case TIME: {
                        {
                            setState(264);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            if (_la == DICE) {
                                {
                                    setState(261);
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
                setState(271);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == TIME) {
                    {
                        setState(268);
                        ((EffectBlockContext) _localctx).time = time();

                        ((EffectBlockContext) _localctx).timeDiceString = ((EffectBlockContext) _localctx).time.timeStr;

                    }
                }

                setState(276);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == EFFECT_MESSAGE) {
                    {
                        setState(273);
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
            "\u0004\u0001.\u0117\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002" +
                    "\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002" +
                    "\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f" +
                    "\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012" +
                    "\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015" +
                    "\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0005\u000bh\b\u000b\n\u000b\f\u000bk\t\u000b\u0001\f\u0001" +
                    "\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0005\ft\b\f\n\f\f\fw\t\f\u0001" +
                    "\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001" +
                    "\r\u0001\r\u0001\r\u0001\r\u0005\r\u0086\b\r\n\r\f\r\u0089\t\r\u0001\u000e" +
                    "\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f" +
                    "\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f" +
                    "\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f" +
                    "\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f" +
                    "\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f" +
                    "\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f" +
                    "\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f" +
                    "\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0005\u000f" +
                    "\u00bb\b\u000f\n\u000f\f\u000f\u00be\t\u000f\u0001\u0010\u0001\u0010\u0001" +
                    "\u0010\u0001\u0010\u0001\u0010\u0004\u0010\u00c5\b\u0010\u000b\u0010\f" +
                    "\u0010\u00c6\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011" +
                    "\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011" +
                    "\u0001\u0011\u0001\u0011\u0001\u0011\u0003\u0011\u00d7\b\u0011\u0003\u0011" +
                    "\u00d9\b\u0011\u0003\u0011\u00db\b\u0011\u0001\u0012\u0001\u0012\u0001" +
                    "\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001" +
                    "\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001" +
                    "\u0014\u0001\u0014\u0004\u0014\u00ed\b\u0014\u000b\u0014\f\u0014\u00ee" +
                    "\u0001\u0014\u0001\u0014\u0003\u0014\u00f3\b\u0014\u0001\u0015\u0001\u0015" +
                    "\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015" +
                    "\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017" +
                    "\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017" +
                    "\u0003\u0017\u0109\b\u0017\u0003\u0017\u010b\b\u0017\u0001\u0017\u0001" +
                    "\u0017\u0001\u0017\u0003\u0017\u0110\b\u0017\u0001\u0017\u0001\u0017\u0001" +
                    "\u0017\u0003\u0017\u0115\b\u0017\u0001\u0017\u0000\u0000\u0018\u0000\u0002" +
                    "\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e" +
                    " \"$&(*,.\u0000\u0000\u0119\u00000\u0001\u0000\u0000\u0000\u00024\u0001" +
                    "\u0000\u0000\u0000\u00048\u0001\u0000\u0000\u0000\u0006@\u0001\u0000\u0000" +
                    "\u0000\bD\u0001\u0000\u0000\u0000\nH\u0001\u0000\u0000\u0000\fL\u0001" +
                    "\u0000\u0000\u0000\u000eP\u0001\u0000\u0000\u0000\u0010T\u0001\u0000\u0000" +
                    "\u0000\u0012X\u0001\u0000\u0000\u0000\u0014\\\u0001\u0000\u0000\u0000" +
                    "\u0016`\u0001\u0000\u0000\u0000\u0018l\u0001\u0000\u0000\u0000\u001ax" +
                    "\u0001\u0000\u0000\u0000\u001c\u008a\u0001\u0000\u0000\u0000\u001e\u008e" +
                    "\u0001\u0000\u0000\u0000 \u00bf\u0001\u0000\u0000\u0000\"\u00ca\u0001" +
                    "\u0000\u0000\u0000$\u00dc\u0001\u0000\u0000\u0000&\u00e0\u0001\u0000\u0000" +
                    "\u0000(\u00e6\u0001\u0000\u0000\u0000*\u00f4\u0001\u0000\u0000\u0000," +
                    "\u00fc\u0001\u0000\u0000\u0000.\u0100\u0001\u0000\u0000\u000001\u0005" +
                    "\u0001\u0000\u000012\u0005\u0018\u0000\u000023\u0006\u0000\uffff\uffff" +
                    "\u00003\u0001\u0001\u0000\u0000\u000045\u0005\u0002\u0000\u000056\u0005" +
                    "&\u0000\u000067\u0006\u0001\uffff\uffff\u00007\u0003\u0001\u0000\u0000" +
                    "\u000089\u0005\u0003\u0000\u00009:\u0005\u0018\u0000\u0000:;\u0005\u0016" +
                    "\u0000\u0000;<\u0005\u0018\u0000\u0000<=\u0005\u0016\u0000\u0000=>\u0005" +
                    "\u0018\u0000\u0000>?\u0006\u0002\uffff\uffff\u0000?\u0005\u0001\u0000" +
                    "\u0000\u0000@A\u0005\u0004\u0000\u0000AB\u0005\u0018\u0000\u0000BC\u0006" +
                    "\u0003\uffff\uffff\u0000C\u0007\u0001\u0000\u0000\u0000DE\u0005\u0005" +
                    "\u0000\u0000EF\u0005\u0018\u0000\u0000FG\u0006\u0004\uffff\uffff\u0000" +
                    "G\t\u0001\u0000\u0000\u0000HI\u0005\u0006\u0000\u0000IJ\u0005\u0018\u0000" +
                    "\u0000JK\u0006\u0005\uffff\uffff\u0000K\u000b\u0001\u0000\u0000\u0000" +
                    "LM\u0005\u0007\u0000\u0000MN\u0005\u0018\u0000\u0000NO\u0006\u0006\uffff" +
                    "\uffff\u0000O\r\u0001\u0000\u0000\u0000PQ\u0005\b\u0000\u0000QR\u0005" +
                    "\u0018\u0000\u0000RS\u0006\u0007\uffff\uffff\u0000S\u000f\u0001\u0000" +
                    "\u0000\u0000TU\u0005\t\u0000\u0000UV\u0005\u0018\u0000\u0000VW\u0006\b" +
                    "\uffff\uffff\u0000W\u0011\u0001\u0000\u0000\u0000XY\u0005\n\u0000\u0000" +
                    "YZ\u0005\u0018\u0000\u0000Z[\u0006\t\uffff\uffff\u0000[\u0013\u0001\u0000" +
                    "\u0000\u0000\\]\u0005\u000b\u0000\u0000]^\u0005\u0018\u0000\u0000^_\u0006" +
                    "\n\uffff\uffff\u0000_\u0015\u0001\u0000\u0000\u0000`a\u0006\u000b\uffff" +
                    "\uffff\u0000ab\u0005\f\u0000\u0000bc\u0005\u001d\u0000\u0000ci\u0006\u000b" +
                    "\uffff\uffff\u0000de\u0005\u001e\u0000\u0000ef\u0005\u001d\u0000\u0000" +
                    "fh\u0006\u000b\uffff\uffff\u0000gd\u0001\u0000\u0000\u0000hk\u0001\u0000" +
                    "\u0000\u0000ig\u0001\u0000\u0000\u0000ij\u0001\u0000\u0000\u0000j\u0017" +
                    "\u0001\u0000\u0000\u0000ki\u0001\u0000\u0000\u0000lm\u0006\f\uffff\uffff" +
                    "\u0000mn\u0005\r\u0000\u0000no\u0005\u001d\u0000\u0000ou\u0006\f\uffff" +
                    "\uffff\u0000pq\u0005\u001e\u0000\u0000qr\u0005\u001d\u0000\u0000rt\u0006" +
                    "\f\uffff\uffff\u0000sp\u0001\u0000\u0000\u0000tw\u0001\u0000\u0000\u0000" +
                    "us\u0001\u0000\u0000\u0000uv\u0001\u0000\u0000\u0000v\u0019\u0001\u0000" +
                    "\u0000\u0000wu\u0001\u0000\u0000\u0000xy\u0006\r\uffff\uffff\u0000yz\u0005" +
                    "\u000e\u0000\u0000z{\u0005 \u0000\u0000{|\u0005!\u0000\u0000|}\u0005#" +
                    "\u0000\u0000}~\u0005\"\u0000\u0000~\u0087\u0006\r\uffff\uffff\u0000\u007f" +
                    "\u0080\u0005$\u0000\u0000\u0080\u0081\u0005 \u0000\u0000\u0081\u0082\u0005" +
                    "!\u0000\u0000\u0082\u0083\u0005#\u0000\u0000\u0083\u0084\u0005\"\u0000" +
                    "\u0000\u0084\u0086\u0006\r\uffff\uffff\u0000\u0085\u007f\u0001\u0000\u0000" +
                    "\u0000\u0086\u0089\u0001\u0000\u0000\u0000\u0087\u0085\u0001\u0000\u0000" +
                    "\u0000\u0087\u0088\u0001\u0000\u0000\u0000\u0088\u001b\u0001\u0000\u0000" +
                    "\u0000\u0089\u0087\u0001\u0000\u0000\u0000\u008a\u008b\u0005\u000f\u0000" +
                    "\u0000\u008b\u008c\u0005&\u0000\u0000\u008c\u008d\u0006\u000e\uffff\uffff" +
                    "\u0000\u008d\u001d\u0001\u0000\u0000\u0000\u008e\u008f\u0003\u0002\u0001" +
                    "\u0000\u008f\u00bc\u0006\u000f\uffff\uffff\u0000\u0090\u0091\u0003\u0004" +
                    "\u0002\u0000\u0091\u0092\u0006\u000f\uffff\uffff\u0000\u0092\u00bb\u0001" +
                    "\u0000\u0000\u0000\u0093\u0094\u0003\u0006\u0003\u0000\u0094\u0095\u0006" +
                    "\u000f\uffff\uffff\u0000\u0095\u00bb\u0001\u0000\u0000\u0000\u0096\u0097" +
                    "\u0003\b\u0004\u0000\u0097\u0098\u0006\u000f\uffff\uffff\u0000\u0098\u00bb" +
                    "\u0001\u0000\u0000\u0000\u0099\u009a\u0003\n\u0005\u0000\u009a\u009b\u0006" +
                    "\u000f\uffff\uffff\u0000\u009b\u00bb\u0001\u0000\u0000\u0000\u009c\u009d" +
                    "\u0003\f\u0006\u0000\u009d\u009e\u0006\u000f\uffff\uffff\u0000\u009e\u00bb" +
                    "\u0001\u0000\u0000\u0000\u009f\u00a0\u0003\u000e\u0007\u0000\u00a0\u00a1" +
                    "\u0006\u000f\uffff\uffff\u0000\u00a1\u00bb\u0001\u0000\u0000\u0000\u00a2" +
                    "\u00a3\u0003\u0010\b\u0000\u00a3\u00a4\u0006\u000f\uffff\uffff\u0000\u00a4" +
                    "\u00bb\u0001\u0000\u0000\u0000\u00a5\u00a6\u0003\u0012\t\u0000\u00a6\u00a7" +
                    "\u0006\u000f\uffff\uffff\u0000\u00a7\u00bb\u0001\u0000\u0000\u0000\u00a8" +
                    "\u00a9\u0003\u0014\n\u0000\u00a9\u00aa\u0006\u000f\uffff\uffff\u0000\u00aa" +
                    "\u00bb\u0001\u0000\u0000\u0000\u00ab\u00ac\u0003\u0016\u000b\u0000\u00ac" +
                    "\u00ad\u0006\u000f\uffff\uffff\u0000\u00ad\u00bb\u0001\u0000\u0000\u0000" +
                    "\u00ae\u00af\u0003\u0018\f\u0000\u00af\u00b0\u0006\u000f\uffff\uffff\u0000" +
                    "\u00b0\u00bb\u0001\u0000\u0000\u0000\u00b1\u00b2\u0003\u001a\r\u0000\u00b2" +
                    "\u00b3\u0006\u000f\uffff\uffff\u0000\u00b3\u00bb\u0001\u0000\u0000\u0000" +
                    "\u00b4\u00b5\u0003.\u0017\u0000\u00b5\u00b6\u0006\u000f\uffff\uffff\u0000" +
                    "\u00b6\u00bb\u0001\u0000\u0000\u0000\u00b7\u00b8\u0003\u001c\u000e\u0000" +
                    "\u00b8\u00b9\u0006\u000f\uffff\uffff\u0000\u00b9\u00bb\u0001\u0000\u0000" +
                    "\u0000\u00ba\u0090\u0001\u0000\u0000\u0000\u00ba\u0093\u0001\u0000\u0000" +
                    "\u0000\u00ba\u0096\u0001\u0000\u0000\u0000\u00ba\u0099\u0001\u0000\u0000" +
                    "\u0000\u00ba\u009c\u0001\u0000\u0000\u0000\u00ba\u009f\u0001\u0000\u0000" +
                    "\u0000\u00ba\u00a2\u0001\u0000\u0000\u0000\u00ba\u00a5\u0001\u0000\u0000" +
                    "\u0000\u00ba\u00a8\u0001\u0000\u0000\u0000\u00ba\u00ab\u0001\u0000\u0000" +
                    "\u0000\u00ba\u00ae\u0001\u0000\u0000\u0000\u00ba\u00b1\u0001\u0000\u0000" +
                    "\u0000\u00ba\u00b4\u0001\u0000\u0000\u0000\u00ba\u00b7\u0001\u0000\u0000" +
                    "\u0000\u00bb\u00be\u0001\u0000\u0000\u0000\u00bc\u00ba\u0001\u0000\u0000" +
                    "\u0000\u00bc\u00bd\u0001\u0000\u0000\u0000\u00bd\u001f\u0001\u0000\u0000" +
                    "\u0000\u00be\u00bc\u0001\u0000\u0000\u0000\u00bf\u00c0\u0003\u0000\u0000" +
                    "\u0000\u00c0\u00c4\u0006\u0010\uffff\uffff\u0000\u00c1\u00c2\u0003\u001e" +
                    "\u000f\u0000\u00c2\u00c3\u0006\u0010\uffff\uffff\u0000\u00c3\u00c5\u0001" +
                    "\u0000\u0000\u0000\u00c4\u00c1\u0001\u0000\u0000\u0000\u00c5\u00c6\u0001" +
                    "\u0000\u0000\u0000\u00c6\u00c4\u0001\u0000\u0000\u0000\u00c6\u00c7\u0001" +
                    "\u0000\u0000\u0000\u00c7\u00c8\u0001\u0000\u0000\u0000\u00c8\u00c9\u0005" +
                    "\u0000\u0000\u0001\u00c9!\u0001\u0000\u0000\u0000\u00ca\u00cb\u0005\u0010" +
                    "\u0000\u0000\u00cb\u00cc\u0005\u0017\u0000\u0000\u00cc\u00da\u0006\u0011" +
                    "\uffff\uffff\u0000\u00cd\u00ce\u0005\u0016\u0000\u0000\u00ce\u00cf\u0005" +
                    "\u0017\u0000\u0000\u00cf\u00d8\u0006\u0011\uffff\uffff\u0000\u00d0\u00d1" +
                    "\u0005\u0016\u0000\u0000\u00d1\u00d2\u0005\u0018\u0000\u0000\u00d2\u00d6" +
                    "\u0006\u0011\uffff\uffff\u0000\u00d3\u00d4\u0005\u0016\u0000\u0000\u00d4" +
                    "\u00d5\u0005\u0018\u0000\u0000\u00d5\u00d7\u0006\u0011\uffff\uffff\u0000" +
                    "\u00d6\u00d3\u0001\u0000\u0000\u0000\u00d6\u00d7\u0001\u0000\u0000\u0000" +
                    "\u00d7\u00d9\u0001\u0000\u0000\u0000\u00d8\u00d0\u0001\u0000\u0000\u0000" +
                    "\u00d8\u00d9\u0001\u0000\u0000\u0000\u00d9\u00db\u0001\u0000\u0000\u0000" +
                    "\u00da\u00cd\u0001\u0000\u0000\u0000\u00da\u00db\u0001\u0000\u0000\u0000" +
                    "\u00db#\u0001\u0000\u0000\u0000\u00dc\u00dd\u0005\u0013\u0000\u0000\u00dd" +
                    "\u00de\u0005(\u0000\u0000\u00de\u00df\u0006\u0012\uffff\uffff\u0000\u00df" +
                    "%\u0001\u0000\u0000\u0000\u00e0\u00e1\u0005\u0014\u0000\u0000\u00e1\u00e2" +
                    "\u0005\u0018\u0000\u0000\u00e2\u00e3\u0005\u0016\u0000\u0000\u00e3\u00e4" +
                    "\u0005\u0018\u0000\u0000\u00e4\u00e5\u0006\u0013\uffff\uffff\u0000\u00e5" +
                    "\'\u0001\u0000\u0000\u0000\u00e6\u00f2\u0005\u0012\u0000\u0000\u00e7\u00e8" +
                    "\u0005)\u0000\u0000\u00e8\u00ec\u0006\u0014\uffff\uffff\u0000\u00e9\u00ea" +
                    "\u0003*\u0015\u0000\u00ea\u00eb\u0006\u0014\uffff\uffff\u0000\u00eb\u00ed" +
                    "\u0001\u0000\u0000\u0000\u00ec\u00e9\u0001\u0000\u0000\u0000\u00ed\u00ee" +
                    "\u0001\u0000\u0000\u0000\u00ee\u00ec\u0001\u0000\u0000\u0000\u00ee\u00ef" +
                    "\u0001\u0000\u0000\u0000\u00ef\u00f3\u0001\u0000\u0000\u0000\u00f0\u00f1" +
                    "\u0005(\u0000\u0000\u00f1\u00f3\u0006\u0014\uffff\uffff\u0000\u00f2\u00e7" +
                    "\u0001\u0000\u0000\u0000\u00f2\u00f0\u0001\u0000\u0000\u0000\u00f3)\u0001" +
                    "\u0000\u0000\u0000\u00f4\u00f5\u0005\u0015\u0000\u0000\u00f5\u00f6\u0005" +
                    "*\u0000\u0000\u00f6\u00f7\u0005+\u0000\u0000\u00f7\u00f8\u0005,\u0000" +
                    "\u0000\u00f8\u00f9\u0005+\u0000\u0000\u00f9\u00fa\u0005-\u0000\u0000\u00fa" +
                    "\u00fb\u0006\u0015\uffff\uffff\u0000\u00fb+\u0001\u0000\u0000\u0000\u00fc" +
                    "\u00fd\u0005\u0011\u0000\u0000\u00fd\u00fe\u0005\'\u0000\u0000\u00fe\u00ff" +
                    "\u0006\u0016\uffff\uffff\u0000\u00ff-\u0001\u0000\u0000\u0000\u0100\u0101" +
                    "\u0003\"\u0011\u0000\u0101\u010a\u0006\u0017\uffff\uffff\u0000\u0102\u0103" +
                    "\u0003&\u0013\u0000\u0103\u0104\u0006\u0017\uffff\uffff\u0000\u0104\u010b" +
                    "\u0001\u0000\u0000\u0000\u0105\u0106\u0003(\u0014\u0000\u0106\u0107\u0006" +
                    "\u0017\uffff\uffff\u0000\u0107\u0109\u0001\u0000\u0000\u0000\u0108\u0105" +
                    "\u0001\u0000\u0000\u0000\u0108\u0109\u0001\u0000\u0000\u0000\u0109\u010b" +
                    "\u0001\u0000\u0000\u0000\u010a\u0102\u0001\u0000\u0000\u0000\u010a\u0108" +
                    "\u0001\u0000\u0000\u0000\u010b\u010f\u0001\u0000\u0000\u0000\u010c\u010d" +
                    "\u0003$\u0012\u0000\u010d\u010e\u0006\u0017\uffff\uffff\u0000\u010e\u0110" +
                    "\u0001\u0000\u0000\u0000\u010f\u010c\u0001\u0000\u0000\u0000\u010f\u0110" +
                    "\u0001\u0000\u0000\u0000\u0110\u0114\u0001\u0000\u0000\u0000\u0111\u0112" +
                    "\u0003,\u0016\u0000\u0112\u0113\u0006\u0017\uffff\uffff\u0000\u0113\u0115" +
                    "\u0001\u0000\u0000\u0000\u0114\u0111\u0001\u0000\u0000\u0000\u0114\u0115" +
                    "\u0001\u0000\u0000\u0000\u0115/\u0001\u0000\u0000\u0000\u000fiu\u0087" +
                    "\u00ba\u00bc\u00c6\u00d6\u00d8\u00da\u00ee\u00f2\u0108\u010a\u010f\u0114";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}