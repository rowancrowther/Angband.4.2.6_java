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
// Generated from CurseGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.curse;

import uk.co.jackoftrades.backend.parser.curse.CurseParseRecord;
import uk.co.jackoftrades.backend.parser.grammars.EffectParseRecord;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class CurseGrammar extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, NAME = 2, TYPE = 3, WEIGHT = 4, COMBAT = 5, FLAGS = 6, VALUES = 7,
            CURSE_MSG = 8, DESC = 9, CONFLICT = 10, CONFLICT_FLAGS = 11, COMMENT = 12, EOL = 13,
            EFFECT = 14, EFFECT_MESSAGE = 15, DICE = 16, TIME = 17, EFFECT_YX = 18, EXPR = 19,
            COLON = 20, UCASE = 21, INTEGER = 22, SIMPLE_DICE_STRING = 23, COMPLEX_DICE_STRING = 24,
            LBRACKET = 25, RBRACKET = 26, VALUE_FLAG = 27, VALUE_OR = 28, VALUE_EOL = 29, VALUE_INT = 30,
            FLAG = 31, FLAG_OR = 32, FLAG_EOL = 33, FREE_TEXT = 34, DICE_SIMPLE_VALUE = 35,
            DICE_COMPLEX_VALUE = 36, EXPR_CHAR = 37, EXPR_COLON = 38, EXPR_UCASE = 39, EXPR_OP = 40,
            EXPR_EOL = 41;
    public static final int
            RULE_recordCount = 0, RULE_name = 1, RULE_curseType = 2, RULE_weight = 3,
            RULE_combat = 4, RULE_flags = 5, RULE_values = 6, RULE_msg = 7, RULE_desc = 8,
            RULE_conflict = 9, RULE_conflictFlags = 10, RULE_curseRecord = 11, RULE_file = 12,
            RULE_effect = 13, RULE_time = 14, RULE_effectYX = 15, RULE_dice = 16,
            RULE_expr = 17, RULE_effectMsg = 18, RULE_effectBlock = 19;

    private static String[] makeRuleNames() {
        return new String[]{
                "recordCount", "name", "curseType", "weight", "combat", "flags", "values",
                "msg", "desc", "conflict", "conflictFlags", "curseRecord", "file", "effect",
                "time", "effectYX", "dice", "expr", "effectMsg", "effectBlock"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'name:'", "'type:'", "'weight:'", "'combat:'",
                "'flags:'", "'values:'", "'msg:'", "'desc:'", "'conflict:'", "'conflict-flags:'",
                null, null, "'effect:'", "'effect-msg:'", "'dice:'", "'time:'", "'effect-yx:'",
                "'expr:'", null, null, null, null, null, "'['", "']'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "NAME", "TYPE", "WEIGHT", "COMBAT", "FLAGS", "VALUES",
                "CURSE_MSG", "DESC", "CONFLICT", "CONFLICT_FLAGS", "COMMENT", "EOL",
                "EFFECT", "EFFECT_MESSAGE", "DICE", "TIME", "EFFECT_YX", "EXPR", "COLON",
                "UCASE", "INTEGER", "SIMPLE_DICE_STRING", "COMPLEX_DICE_STRING", "LBRACKET",
                "RBRACKET", "VALUE_FLAG", "VALUE_OR", "VALUE_EOL", "VALUE_INT", "FLAG",
                "FLAG_OR", "FLAG_EOL", "FREE_TEXT", "DICE_SIMPLE_VALUE", "DICE_COMPLEX_VALUE",
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
        return "CurseGrammar.g4";
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

    public CurseGrammar(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RecordCountContext extends ParserRuleContext {
        public String count;
        public Token INTEGER;

        public TerminalNode RECORD_COUNT() {
            return getToken(CurseGrammar.RECORD_COUNT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(CurseGrammar.INTEGER, 0);
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
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).enterRecordCount(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).exitRecordCount(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CurseGrammarVisitor)
                return ((CurseGrammarVisitor<? extends T>) visitor).visitRecordCount(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RecordCountContext recordCount() throws RecognitionException {
        RecordCountContext _localctx = new RecordCountContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_recordCount);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(40);
                match(RECORD_COUNT);
                setState(41);
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
        public Token FREE_TEXT;

        public TerminalNode NAME() {
            return getToken(CurseGrammar.NAME, 0);
        }

        public TerminalNode FREE_TEXT() {
            return getToken(CurseGrammar.FREE_TEXT, 0);
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
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CurseGrammarVisitor)
                return ((CurseGrammarVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(44);
                match(NAME);
                setState(45);
                ((NameContext) _localctx).FREE_TEXT = match(FREE_TEXT);
                ((NameContext) _localctx).nameStr = ((NameContext) _localctx).FREE_TEXT.getText();
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
    public static class CurseTypeContext extends ParserRuleContext {
        public String typeStr;
        public Token FREE_TEXT;

        public TerminalNode TYPE() {
            return getToken(CurseGrammar.TYPE, 0);
        }

        public TerminalNode FREE_TEXT() {
            return getToken(CurseGrammar.FREE_TEXT, 0);
        }

        public CurseTypeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_curseType;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).enterCurseType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).exitCurseType(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CurseGrammarVisitor)
                return ((CurseGrammarVisitor<? extends T>) visitor).visitCurseType(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CurseTypeContext curseType() throws RecognitionException {
        CurseTypeContext _localctx = new CurseTypeContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_curseType);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(48);
                match(TYPE);
                setState(49);
                ((CurseTypeContext) _localctx).FREE_TEXT = match(FREE_TEXT);
                ((CurseTypeContext) _localctx).typeStr = ((CurseTypeContext) _localctx).FREE_TEXT.getText();
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
        public String weightAdj;
        public Token INTEGER;

        public TerminalNode WEIGHT() {
            return getToken(CurseGrammar.WEIGHT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(CurseGrammar.INTEGER, 0);
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
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).enterWeight(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).exitWeight(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CurseGrammarVisitor)
                return ((CurseGrammarVisitor<? extends T>) visitor).visitWeight(this);
            else return visitor.visitChildren(this);
        }
    }

    public final WeightContext weight() throws RecognitionException {
        WeightContext _localctx = new WeightContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_weight);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(52);
                match(WEIGHT);
                setState(53);
                ((WeightContext) _localctx).INTEGER = match(INTEGER);
                ((WeightContext) _localctx).weightAdj = ((WeightContext) _localctx).INTEGER.getText();
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
        public String toa;
        public String tod;
        public Token h;
        public Token d;
        public Token a;

        public TerminalNode COMBAT() {
            return getToken(CurseGrammar.COMBAT, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(CurseGrammar.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(CurseGrammar.COLON, i);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(CurseGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(CurseGrammar.INTEGER, i);
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
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).enterCombat(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).exitCombat(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CurseGrammarVisitor)
                return ((CurseGrammarVisitor<? extends T>) visitor).visitCombat(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CombatContext combat() throws RecognitionException {
        CombatContext _localctx = new CombatContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_combat);
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
    public static class FlagsContext extends ParserRuleContext {
        public List<String> flagList;
        public Token f1;
        public Token f2;

        public TerminalNode FLAGS() {
            return getToken(CurseGrammar.FLAGS, 0);
        }

        public List<TerminalNode> FLAG() {
            return getTokens(CurseGrammar.FLAG);
        }

        public TerminalNode FLAG(int i) {
            return getToken(CurseGrammar.FLAG, i);
        }

        public List<TerminalNode> FLAG_OR() {
            return getTokens(CurseGrammar.FLAG_OR);
        }

        public TerminalNode FLAG_OR(int i) {
            return getToken(CurseGrammar.FLAG_OR, i);
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
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).enterFlags(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).exitFlags(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CurseGrammarVisitor)
                return ((CurseGrammarVisitor<? extends T>) visitor).visitFlags(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FlagsContext flags() throws RecognitionException {
        FlagsContext _localctx = new FlagsContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_flags);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                ((FlagsContext) _localctx).flagList = new ArrayList<>();
                setState(65);
                match(FLAGS);
                setState(66);
                ((FlagsContext) _localctx).f1 = match(FLAG);
                _localctx.flagList.add(((FlagsContext) _localctx).f1.getText());
                setState(73);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == FLAG_OR) {
                    {
                        {
                            setState(68);
                            match(FLAG_OR);
                            setState(69);
                            ((FlagsContext) _localctx).f2 = match(FLAG);
                            _localctx.flagList.add(((FlagsContext) _localctx).f2.getText());
                        }
                    }
                    setState(75);
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
        public Token vf1;
        public Token vi1;
        public Token vf2;
        public Token vi2;

        public TerminalNode VALUES() {
            return getToken(CurseGrammar.VALUES, 0);
        }

        public List<TerminalNode> LBRACKET() {
            return getTokens(CurseGrammar.LBRACKET);
        }

        public TerminalNode LBRACKET(int i) {
            return getToken(CurseGrammar.LBRACKET, i);
        }

        public List<TerminalNode> RBRACKET() {
            return getTokens(CurseGrammar.RBRACKET);
        }

        public TerminalNode RBRACKET(int i) {
            return getToken(CurseGrammar.RBRACKET, i);
        }

        public List<TerminalNode> VALUE_FLAG() {
            return getTokens(CurseGrammar.VALUE_FLAG);
        }

        public TerminalNode VALUE_FLAG(int i) {
            return getToken(CurseGrammar.VALUE_FLAG, i);
        }

        public List<TerminalNode> VALUE_INT() {
            return getTokens(CurseGrammar.VALUE_INT);
        }

        public TerminalNode VALUE_INT(int i) {
            return getToken(CurseGrammar.VALUE_INT, i);
        }

        public List<TerminalNode> VALUE_OR() {
            return getTokens(CurseGrammar.VALUE_OR);
        }

        public TerminalNode VALUE_OR(int i) {
            return getToken(CurseGrammar.VALUE_OR, i);
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
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).enterValues(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).exitValues(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CurseGrammarVisitor)
                return ((CurseGrammarVisitor<? extends T>) visitor).visitValues(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ValuesContext values() throws RecognitionException {
        ValuesContext _localctx = new ValuesContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_values);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                ((ValuesContext) _localctx).valueMap = new HashMap<>();
                setState(77);
                match(VALUES);
                setState(78);
                ((ValuesContext) _localctx).vf1 = match(VALUE_FLAG);
                setState(79);
                match(LBRACKET);
                setState(80);
                ((ValuesContext) _localctx).vi1 = match(VALUE_INT);
                setState(81);
                match(RBRACKET);

                _localctx.valueMap.put(((ValuesContext) _localctx).vf1.getText(), ((ValuesContext) _localctx).vi1.getText());

                setState(91);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == VALUE_OR) {
                    {
                        {
                            setState(83);
                            match(VALUE_OR);
                            setState(84);
                            ((ValuesContext) _localctx).vf2 = match(VALUE_FLAG);
                            setState(85);
                            match(LBRACKET);
                            setState(86);
                            ((ValuesContext) _localctx).vi2 = match(VALUE_INT);
                            setState(87);
                            match(RBRACKET);

                            _localctx.valueMap.put(((ValuesContext) _localctx).vf2.getText(), ((ValuesContext) _localctx).vi2.getText());

                        }
                    }
                    setState(93);
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
    public static class MsgContext extends ParserRuleContext {
        public String msgStr;
        public Token FREE_TEXT;

        public TerminalNode CURSE_MSG() {
            return getToken(CurseGrammar.CURSE_MSG, 0);
        }

        public TerminalNode FREE_TEXT() {
            return getToken(CurseGrammar.FREE_TEXT, 0);
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
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).enterMsg(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).exitMsg(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CurseGrammarVisitor)
                return ((CurseGrammarVisitor<? extends T>) visitor).visitMsg(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MsgContext msg() throws RecognitionException {
        MsgContext _localctx = new MsgContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_msg);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(94);
                match(CURSE_MSG);
                setState(95);
                ((MsgContext) _localctx).FREE_TEXT = match(FREE_TEXT);
                ((MsgContext) _localctx).msgStr = ((MsgContext) _localctx).FREE_TEXT.getText();
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
        public String description;
        public Token FREE_TEXT;

        public TerminalNode DESC() {
            return getToken(CurseGrammar.DESC, 0);
        }

        public TerminalNode FREE_TEXT() {
            return getToken(CurseGrammar.FREE_TEXT, 0);
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
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).enterDesc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).exitDesc(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CurseGrammarVisitor)
                return ((CurseGrammarVisitor<? extends T>) visitor).visitDesc(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DescContext desc() throws RecognitionException {
        DescContext _localctx = new DescContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_desc);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(98);
                match(DESC);
                setState(99);
                ((DescContext) _localctx).FREE_TEXT = match(FREE_TEXT);
                ((DescContext) _localctx).description = ((DescContext) _localctx).FREE_TEXT.getText();
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
    public static class ConflictContext extends ParserRuleContext {
        public String conflictingCurses;
        public Token FREE_TEXT;

        public TerminalNode CONFLICT() {
            return getToken(CurseGrammar.CONFLICT, 0);
        }

        public TerminalNode FREE_TEXT() {
            return getToken(CurseGrammar.FREE_TEXT, 0);
        }

        public ConflictContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_conflict;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).enterConflict(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).exitConflict(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CurseGrammarVisitor)
                return ((CurseGrammarVisitor<? extends T>) visitor).visitConflict(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ConflictContext conflict() throws RecognitionException {
        ConflictContext _localctx = new ConflictContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_conflict);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(102);
                match(CONFLICT);
                setState(103);
                ((ConflictContext) _localctx).FREE_TEXT = match(FREE_TEXT);
                ((ConflictContext) _localctx).conflictingCurses = ((ConflictContext) _localctx).FREE_TEXT.getText();
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
    public static class ConflictFlagsContext extends ParserRuleContext {
        public List<String> cFlags;
        public Token f1;
        public Token f2;

        public TerminalNode CONFLICT_FLAGS() {
            return getToken(CurseGrammar.CONFLICT_FLAGS, 0);
        }

        public List<TerminalNode> FLAG() {
            return getTokens(CurseGrammar.FLAG);
        }

        public TerminalNode FLAG(int i) {
            return getToken(CurseGrammar.FLAG, i);
        }

        public List<TerminalNode> FLAG_OR() {
            return getTokens(CurseGrammar.FLAG_OR);
        }

        public TerminalNode FLAG_OR(int i) {
            return getToken(CurseGrammar.FLAG_OR, i);
        }

        public ConflictFlagsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_conflictFlags;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).enterConflictFlags(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).exitConflictFlags(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CurseGrammarVisitor)
                return ((CurseGrammarVisitor<? extends T>) visitor).visitConflictFlags(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ConflictFlagsContext conflictFlags() throws RecognitionException {
        ConflictFlagsContext _localctx = new ConflictFlagsContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_conflictFlags);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                ((ConflictFlagsContext) _localctx).cFlags = new ArrayList<>();
                setState(107);
                match(CONFLICT_FLAGS);
                setState(108);
                ((ConflictFlagsContext) _localctx).f1 = match(FLAG);
                _localctx.cFlags.add(((ConflictFlagsContext) _localctx).f1.getText());
                setState(115);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == FLAG_OR) {
                    {
                        {
                            setState(110);
                            match(FLAG_OR);
                            setState(111);
                            ((ConflictFlagsContext) _localctx).f2 = match(FLAG);
                            _localctx.cFlags.add(((ConflictFlagsContext) _localctx).f2.getText());
                        }
                    }
                    setState(117);
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
    public static class CurseRecordContext extends ParserRuleContext {
        public CurseParseRecord record;
        public NameContext name;
        public CurseTypeContext curseType;
        public WeightContext weight;
        public CombatContext combat;
        public EffectBlockContext effectBlock;
        public FlagsContext flags;
        public ValuesContext values;
        public MsgContext msg;
        public DescContext desc;
        public ConflictContext conflict;
        public ConflictFlagsContext conflictFlags;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public List<CurseTypeContext> curseType() {
            return getRuleContexts(CurseTypeContext.class);
        }

        public CurseTypeContext curseType(int i) {
            return getRuleContext(CurseTypeContext.class, i);
        }

        public List<WeightContext> weight() {
            return getRuleContexts(WeightContext.class);
        }

        public WeightContext weight(int i) {
            return getRuleContext(WeightContext.class, i);
        }

        public List<CombatContext> combat() {
            return getRuleContexts(CombatContext.class);
        }

        public CombatContext combat(int i) {
            return getRuleContext(CombatContext.class, i);
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

        public List<MsgContext> msg() {
            return getRuleContexts(MsgContext.class);
        }

        public MsgContext msg(int i) {
            return getRuleContext(MsgContext.class, i);
        }

        public List<DescContext> desc() {
            return getRuleContexts(DescContext.class);
        }

        public DescContext desc(int i) {
            return getRuleContext(DescContext.class, i);
        }

        public List<ConflictContext> conflict() {
            return getRuleContexts(ConflictContext.class);
        }

        public ConflictContext conflict(int i) {
            return getRuleContext(ConflictContext.class, i);
        }

        public List<ConflictFlagsContext> conflictFlags() {
            return getRuleContexts(ConflictFlagsContext.class);
        }

        public ConflictFlagsContext conflictFlags(int i) {
            return getRuleContext(ConflictFlagsContext.class, i);
        }

        public CurseRecordContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_curseRecord;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).enterCurseRecord(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).exitCurseRecord(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CurseGrammarVisitor)
                return ((CurseGrammarVisitor<? extends T>) visitor).visitCurseRecord(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CurseRecordContext curseRecord() throws RecognitionException {
        CurseRecordContext _localctx = new CurseRecordContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_curseRecord);

        int blockLine = 0;
        String nameInit = "";
        String tohInit = "";
        String todInit = "";
        String toaInit = "";
        String msgInit = "";
        List<String> curseTypeInit = new ArrayList<>();
        List<EffectParseRecord> effects = new ArrayList<>();
        List<String> flagsListInit = new ArrayList<>();
        Map<String, String> valuesInit = new HashMap<>();
        List<String> descInit = new ArrayList<>();
        List<String> conflictInit = new ArrayList<>();
        List<String> cFlagsInit = new ArrayList<>();
        String weightAdjustmentInit = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(118);
                ((CurseRecordContext) _localctx).name = name();
                nameInit = ((CurseRecordContext) _localctx).name.nameStr;
                blockLine = ((CurseRecordContext) _localctx).name.line;
                setState(150);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        setState(150);
                        _errHandler.sync(this);
                        switch (_input.LA(1)) {
                            case TYPE: {
                                setState(120);
                                ((CurseRecordContext) _localctx).curseType = curseType();
                                curseTypeInit.add(((CurseRecordContext) _localctx).curseType.typeStr);
                            }
                            break;
                            case WEIGHT: {
                                setState(123);
                                ((CurseRecordContext) _localctx).weight = weight();
                                weightAdjustmentInit = ((CurseRecordContext) _localctx).weight.weightAdj;
                            }
                            break;
                            case COMBAT: {
                                setState(126);
                                ((CurseRecordContext) _localctx).combat = combat();
                                tohInit = ((CurseRecordContext) _localctx).combat.toh;
                                todInit = ((CurseRecordContext) _localctx).combat.tod;
                                toaInit = ((CurseRecordContext) _localctx).combat.toa;
                            }
                            break;
                            case EFFECT: {
                                setState(129);
                                ((CurseRecordContext) _localctx).effectBlock = effectBlock();
                                effects.add(new EffectParseRecord(((CurseRecordContext) _localctx).effectBlock.typeInit,
                                        ((CurseRecordContext) _localctx).effectBlock.subtypeWrapperInit, ((CurseRecordContext) _localctx).effectBlock.radius, ((CurseRecordContext) _localctx).effectBlock.other,
                                        ((CurseRecordContext) _localctx).effectBlock.diceString, ((CurseRecordContext) _localctx).effectBlock.yVal, ((CurseRecordContext) _localctx).effectBlock.xVal,
                                        ((CurseRecordContext) _localctx).effectBlock.expressionChars, ((CurseRecordContext) _localctx).effectBlock.expressionBase,
                                        ((CurseRecordContext) _localctx).effectBlock.expressionOperation, ((CurseRecordContext) _localctx).effectBlock.timeDiceString,
                                        ((CurseRecordContext) _localctx).effectBlock.effectMessage, (((CurseRecordContext) _localctx).effectBlock != null ? (((CurseRecordContext) _localctx).effectBlock.start) : null).getLine()));
                            }
                            break;
                            case FLAGS: {
                                setState(132);
                                ((CurseRecordContext) _localctx).flags = flags();
                                flagsListInit.addAll(((CurseRecordContext) _localctx).flags.flagList);
                            }
                            break;
                            case VALUES: {
                                setState(135);
                                ((CurseRecordContext) _localctx).values = values();
                                valuesInit.putAll(((CurseRecordContext) _localctx).values.valueMap);
                            }
                            break;
                            case CURSE_MSG: {
                                setState(138);
                                ((CurseRecordContext) _localctx).msg = msg();
                                msgInit = ((CurseRecordContext) _localctx).msg.msgStr;
                            }
                            break;
                            case DESC: {
                                setState(141);
                                ((CurseRecordContext) _localctx).desc = desc();
                                descInit.add(((CurseRecordContext) _localctx).desc.description);
                            }
                            break;
                            case CONFLICT: {
                                setState(144);
                                ((CurseRecordContext) _localctx).conflict = conflict();
                                conflictInit.add(((CurseRecordContext) _localctx).conflict.conflictingCurses);
                            }
                            break;
                            case CONFLICT_FLAGS: {
                                setState(147);
                                ((CurseRecordContext) _localctx).conflictFlags = conflictFlags();
                                cFlagsInit.addAll(((CurseRecordContext) _localctx).conflictFlags.cFlags);
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                    }
                    setState(152);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 20472L) != 0));
            }
            _ctx.stop = _input.LT(-1);

            ((CurseRecordContext) _localctx).record = new CurseParseRecord(nameInit, curseTypeInit, weightAdjustmentInit,
                    tohInit, todInit, toaInit, effects, flagsListInit, valuesInit,
                    msgInit, descInit, conflictInit, cFlagsInit, blockLine);

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
        public List<CurseParseRecord> records;
        public String declaredCount;
        public RecordCountContext recordCount;
        public CurseRecordContext curseRecord;

        public RecordCountContext recordCount() {
            return getRuleContext(RecordCountContext.class, 0);
        }

        public TerminalNode EOF() {
            return getToken(CurseGrammar.EOF, 0);
        }

        public List<CurseRecordContext> curseRecord() {
            return getRuleContexts(CurseRecordContext.class);
        }

        public CurseRecordContext curseRecord(int i) {
            return getRuleContext(CurseRecordContext.class, i);
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
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CurseGrammarVisitor)
                return ((CurseGrammarVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_file);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(154);
                ((FileContext) _localctx).recordCount = recordCount();
                ((FileContext) _localctx).declaredCount = ((FileContext) _localctx).recordCount.count;
                ((FileContext) _localctx).records = new ArrayList<>();
                setState(159);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(156);
                            ((FileContext) _localctx).curseRecord = curseRecord();
                            _localctx.records.add(((FileContext) _localctx).curseRecord.record);
                        }
                    }
                    setState(161);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(163);
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
            return getToken(CurseGrammar.EFFECT, 0);
        }

        public List<TerminalNode> UCASE() {
            return getTokens(CurseGrammar.UCASE);
        }

        public TerminalNode UCASE(int i) {
            return getToken(CurseGrammar.UCASE, i);
        }

        public List<TerminalNode> COLON() {
            return getTokens(CurseGrammar.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(CurseGrammar.COLON, i);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(CurseGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(CurseGrammar.INTEGER, i);
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
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).enterEffect(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).exitEffect(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CurseGrammarVisitor)
                return ((CurseGrammarVisitor<? extends T>) visitor).visitEffect(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectContext effect() throws RecognitionException {
        EffectContext _localctx = new EffectContext(_ctx, getState());
        enterRule(_localctx, 26, RULE_effect);

        ((EffectContext) _localctx).wrapper = "";
        ((EffectContext) _localctx).radius = "";
        ((EffectContext) _localctx).other = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(165);
                match(EFFECT);
                setState(166);
                ((EffectContext) _localctx).t = match(UCASE);

                ((EffectContext) _localctx).type = ((EffectContext) _localctx).t.getText();

                setState(181);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COLON) {
                    {
                        setState(168);
                        match(COLON);
                        setState(169);
                        ((EffectContext) _localctx).st = match(UCASE);

                        ((EffectContext) _localctx).wrapper = ((EffectContext) _localctx).st.getText().toUpperCase();

                        setState(179);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == COLON) {
                            {
                                setState(171);
                                match(COLON);
                                setState(172);
                                ((EffectContext) _localctx).rad = match(INTEGER);

                                ((EffectContext) _localctx).radius = ((EffectContext) _localctx).rad.getText();

                                setState(177);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                                if (_la == COLON) {
                                    {
                                        setState(174);
                                        match(COLON);
                                        setState(175);
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
            return getToken(CurseGrammar.TIME, 0);
        }

        public TerminalNode DICE_SIMPLE_VALUE() {
            return getToken(CurseGrammar.DICE_SIMPLE_VALUE, 0);
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
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).enterTime(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).exitTime(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CurseGrammarVisitor)
                return ((CurseGrammarVisitor<? extends T>) visitor).visitTime(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TimeContext time() throws RecognitionException {
        TimeContext _localctx = new TimeContext(_ctx, getState());
        enterRule(_localctx, 28, RULE_time);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(183);
                match(TIME);
                setState(184);
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
            return getToken(CurseGrammar.EFFECT_YX, 0);
        }

        public TerminalNode COLON() {
            return getToken(CurseGrammar.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(CurseGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(CurseGrammar.INTEGER, i);
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
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).enterEffectYX(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).exitEffectYX(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CurseGrammarVisitor)
                return ((CurseGrammarVisitor<? extends T>) visitor).visitEffectYX(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectYXContext effectYX() throws RecognitionException {
        EffectYXContext _localctx = new EffectYXContext(_ctx, getState());
        enterRule(_localctx, 30, RULE_effectYX);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(187);
                match(EFFECT_YX);
                setState(188);
                ((EffectYXContext) _localctx).yVal = match(INTEGER);
                setState(189);
                match(COLON);
                setState(190);
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
            return getToken(CurseGrammar.DICE, 0);
        }

        public TerminalNode DICE_SIMPLE_VALUE() {
            return getToken(CurseGrammar.DICE_SIMPLE_VALUE, 0);
        }

        public TerminalNode DICE_COMPLEX_VALUE() {
            return getToken(CurseGrammar.DICE_COMPLEX_VALUE, 0);
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
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).enterDice(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).exitDice(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CurseGrammarVisitor)
                return ((CurseGrammarVisitor<? extends T>) visitor).visitDice(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DiceContext dice() throws RecognitionException {
        DiceContext _localctx = new DiceContext(_ctx, getState());
        enterRule(_localctx, 32, RULE_dice);

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
                setState(193);
                match(DICE);
                setState(205);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case DICE_COMPLEX_VALUE: {
                        {
                            setState(194);
                            ((DiceContext) _localctx).val = match(DICE_COMPLEX_VALUE);

                            ((DiceContext) _localctx).diceString = ((DiceContext) _localctx).val.getText();

                            setState(199);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            do {
                                {
                                    {
                                        setState(196);
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
                                setState(201);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                            } while (_la == EXPR);
                        }
                    }
                    break;
                    case DICE_SIMPLE_VALUE: {
                        setState(203);
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
            return getToken(CurseGrammar.EXPR, 0);
        }

        public List<TerminalNode> EXPR_COLON() {
            return getTokens(CurseGrammar.EXPR_COLON);
        }

        public TerminalNode EXPR_COLON(int i) {
            return getToken(CurseGrammar.EXPR_COLON, i);
        }

        public TerminalNode EXPR_CHAR() {
            return getToken(CurseGrammar.EXPR_CHAR, 0);
        }

        public TerminalNode EXPR_UCASE() {
            return getToken(CurseGrammar.EXPR_UCASE, 0);
        }

        public TerminalNode EXPR_OP() {
            return getToken(CurseGrammar.EXPR_OP, 0);
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
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).enterExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).exitExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CurseGrammarVisitor)
                return ((CurseGrammarVisitor<? extends T>) visitor).visitExpr(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExprContext expr() throws RecognitionException {
        ExprContext _localctx = new ExprContext(_ctx, getState());
        enterRule(_localctx, 34, RULE_expr);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(207);
                match(EXPR);
                setState(208);
                ((ExprContext) _localctx).ch = match(EXPR_CHAR);
                setState(209);
                match(EXPR_COLON);
                setState(210);
                ((ExprContext) _localctx).base = match(EXPR_UCASE);
                setState(211);
                match(EXPR_COLON);
                setState(212);
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
            return getToken(CurseGrammar.EFFECT_MESSAGE, 0);
        }

        public TerminalNode FREE_TEXT() {
            return getToken(CurseGrammar.FREE_TEXT, 0);
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
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).enterEffectMsg(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).exitEffectMsg(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CurseGrammarVisitor)
                return ((CurseGrammarVisitor<? extends T>) visitor).visitEffectMsg(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectMsgContext effectMsg() throws RecognitionException {
        EffectMsgContext _localctx = new EffectMsgContext(_ctx, getState());
        enterRule(_localctx, 36, RULE_effectMsg);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(215);
                match(EFFECT_MESSAGE);
                setState(216);
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
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).enterEffectBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CurseGrammarListener) ((CurseGrammarListener) listener).exitEffectBlock(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CurseGrammarVisitor)
                return ((CurseGrammarVisitor<? extends T>) visitor).visitEffectBlock(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectBlockContext effectBlock() throws RecognitionException {
        EffectBlockContext _localctx = new EffectBlockContext(_ctx, getState());
        enterRule(_localctx, 38, RULE_effectBlock);

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
                setState(219);
                ((EffectBlockContext) _localctx).effect = effect();

                ((EffectBlockContext) _localctx).lineNo = _localctx.start.getLine();
                ((EffectBlockContext) _localctx).typeInit = ((EffectBlockContext) _localctx).effect.type;
                ((EffectBlockContext) _localctx).subtypeWrapperInit = ((EffectBlockContext) _localctx).effect.wrapper;
                ((EffectBlockContext) _localctx).radius = ((EffectBlockContext) _localctx).effect.radius;
                ((EffectBlockContext) _localctx).other = ((EffectBlockContext) _localctx).effect.other;

                setState(229);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case EFFECT_YX: {
                        {
                            setState(221);
                            ((EffectBlockContext) _localctx).effectYX = effectYX();

                            ((EffectBlockContext) _localctx).yVal = ((EffectBlockContext) _localctx).effectYX.y;
                            ((EffectBlockContext) _localctx).xVal = ((EffectBlockContext) _localctx).effectYX.x;

                        }
                    }
                    break;
                    case EOF:
                    case NAME:
                    case TYPE:
                    case WEIGHT:
                    case COMBAT:
                    case FLAGS:
                    case VALUES:
                    case CURSE_MSG:
                    case DESC:
                    case CONFLICT:
                    case CONFLICT_FLAGS:
                    case EFFECT:
                    case EFFECT_MESSAGE:
                    case DICE:
                    case TIME: {
                        {
                            setState(227);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            if (_la == DICE) {
                                {
                                    setState(224);
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
                setState(234);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == TIME) {
                    {
                        setState(231);
                        ((EffectBlockContext) _localctx).time = time();

                        ((EffectBlockContext) _localctx).timeDiceString = ((EffectBlockContext) _localctx).time.timeStr;

                    }
                }

                setState(239);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == EFFECT_MESSAGE) {
                    {
                        setState(236);
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
            "\u0004\u0001)\u00f2\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002" +
                    "\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002" +
                    "\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f" +
                    "\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012" +
                    "\u0002\u0013\u0007\u0013\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0005\u0005H\b\u0005\n\u0005\f\u0005" +
                    "K\t\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0005\u0006Z\b\u0006\n\u0006\f\u0006]\t\u0006" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0005\nr\b\n\n\n\f\nu\t\n\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0004\u000b\u0097\b\u000b\u000b\u000b\f\u000b\u0098\u0001\f\u0001" +
                    "\f\u0001\f\u0001\f\u0001\f\u0004\f\u00a0\b\f\u000b\f\f\f\u00a1\u0001\f" +
                    "\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001" +
                    "\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003\r\u00b2\b\r\u0003\r\u00b4\b\r" +
                    "\u0003\r\u00b6\b\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001" +
                    "\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001" +
                    "\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0004" +
                    "\u0010\u00c8\b\u0010\u000b\u0010\f\u0010\u00c9\u0001\u0010\u0001\u0010" +
                    "\u0003\u0010\u00ce\b\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011" +
                    "\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012" +
                    "\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0003\u0013\u00e4\b\u0013" +
                    "\u0003\u0013\u00e6\b\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0003\u0013" +
                    "\u00eb\b\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0003\u0013\u00f0\b" +
                    "\u0013\u0001\u0013\u0000\u0000\u0014\u0000\u0002\u0004\u0006\b\n\f\u000e" +
                    "\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&\u0000\u0000\u00f4" +
                    "\u0000(\u0001\u0000\u0000\u0000\u0002,\u0001\u0000\u0000\u0000\u00040" +
                    "\u0001\u0000\u0000\u0000\u00064\u0001\u0000\u0000\u0000\b8\u0001\u0000" +
                    "\u0000\u0000\n@\u0001\u0000\u0000\u0000\fL\u0001\u0000\u0000\u0000\u000e" +
                    "^\u0001\u0000\u0000\u0000\u0010b\u0001\u0000\u0000\u0000\u0012f\u0001" +
                    "\u0000\u0000\u0000\u0014j\u0001\u0000\u0000\u0000\u0016v\u0001\u0000\u0000" +
                    "\u0000\u0018\u009a\u0001\u0000\u0000\u0000\u001a\u00a5\u0001\u0000\u0000" +
                    "\u0000\u001c\u00b7\u0001\u0000\u0000\u0000\u001e\u00bb\u0001\u0000\u0000" +
                    "\u0000 \u00c1\u0001\u0000\u0000\u0000\"\u00cf\u0001\u0000\u0000\u0000" +
                    "$\u00d7\u0001\u0000\u0000\u0000&\u00db\u0001\u0000\u0000\u0000()\u0005" +
                    "\u0001\u0000\u0000)*\u0005\u0016\u0000\u0000*+\u0006\u0000\uffff\uffff" +
                    "\u0000+\u0001\u0001\u0000\u0000\u0000,-\u0005\u0002\u0000\u0000-.\u0005" +
                    "\"\u0000\u0000./\u0006\u0001\uffff\uffff\u0000/\u0003\u0001\u0000\u0000" +
                    "\u000001\u0005\u0003\u0000\u000012\u0005\"\u0000\u000023\u0006\u0002\uffff" +
                    "\uffff\u00003\u0005\u0001\u0000\u0000\u000045\u0005\u0004\u0000\u0000" +
                    "56\u0005\u0016\u0000\u000067\u0006\u0003\uffff\uffff\u00007\u0007\u0001" +
                    "\u0000\u0000\u000089\u0005\u0005\u0000\u00009:\u0005\u0016\u0000\u0000" +
                    ":;\u0005\u0014\u0000\u0000;<\u0005\u0016\u0000\u0000<=\u0005\u0014\u0000" +
                    "\u0000=>\u0005\u0016\u0000\u0000>?\u0006\u0004\uffff\uffff\u0000?\t\u0001" +
                    "\u0000\u0000\u0000@A\u0006\u0005\uffff\uffff\u0000AB\u0005\u0006\u0000" +
                    "\u0000BC\u0005\u001f\u0000\u0000CI\u0006\u0005\uffff\uffff\u0000DE\u0005" +
                    " \u0000\u0000EF\u0005\u001f\u0000\u0000FH\u0006\u0005\uffff\uffff\u0000" +
                    "GD\u0001\u0000\u0000\u0000HK\u0001\u0000\u0000\u0000IG\u0001\u0000\u0000" +
                    "\u0000IJ\u0001\u0000\u0000\u0000J\u000b\u0001\u0000\u0000\u0000KI\u0001" +
                    "\u0000\u0000\u0000LM\u0006\u0006\uffff\uffff\u0000MN\u0005\u0007\u0000" +
                    "\u0000NO\u0005\u001b\u0000\u0000OP\u0005\u0019\u0000\u0000PQ\u0005\u001e" +
                    "\u0000\u0000QR\u0005\u001a\u0000\u0000R[\u0006\u0006\uffff\uffff\u0000" +
                    "ST\u0005\u001c\u0000\u0000TU\u0005\u001b\u0000\u0000UV\u0005\u0019\u0000" +
                    "\u0000VW\u0005\u001e\u0000\u0000WX\u0005\u001a\u0000\u0000XZ\u0006\u0006" +
                    "\uffff\uffff\u0000YS\u0001\u0000\u0000\u0000Z]\u0001\u0000\u0000\u0000" +
                    "[Y\u0001\u0000\u0000\u0000[\\\u0001\u0000\u0000\u0000\\\r\u0001\u0000" +
                    "\u0000\u0000][\u0001\u0000\u0000\u0000^_\u0005\b\u0000\u0000_`\u0005\"" +
                    "\u0000\u0000`a\u0006\u0007\uffff\uffff\u0000a\u000f\u0001\u0000\u0000" +
                    "\u0000bc\u0005\t\u0000\u0000cd\u0005\"\u0000\u0000de\u0006\b\uffff\uffff" +
                    "\u0000e\u0011\u0001\u0000\u0000\u0000fg\u0005\n\u0000\u0000gh\u0005\"" +
                    "\u0000\u0000hi\u0006\t\uffff\uffff\u0000i\u0013\u0001\u0000\u0000\u0000" +
                    "jk\u0006\n\uffff\uffff\u0000kl\u0005\u000b\u0000\u0000lm\u0005\u001f\u0000" +
                    "\u0000ms\u0006\n\uffff\uffff\u0000no\u0005 \u0000\u0000op\u0005\u001f" +
                    "\u0000\u0000pr\u0006\n\uffff\uffff\u0000qn\u0001\u0000\u0000\u0000ru\u0001" +
                    "\u0000\u0000\u0000sq\u0001\u0000\u0000\u0000st\u0001\u0000\u0000\u0000" +
                    "t\u0015\u0001\u0000\u0000\u0000us\u0001\u0000\u0000\u0000vw\u0003\u0002" +
                    "\u0001\u0000w\u0096\u0006\u000b\uffff\uffff\u0000xy\u0003\u0004\u0002" +
                    "\u0000yz\u0006\u000b\uffff\uffff\u0000z\u0097\u0001\u0000\u0000\u0000" +
                    "{|\u0003\u0006\u0003\u0000|}\u0006\u000b\uffff\uffff\u0000}\u0097\u0001" +
                    "\u0000\u0000\u0000~\u007f\u0003\b\u0004\u0000\u007f\u0080\u0006\u000b" +
                    "\uffff\uffff\u0000\u0080\u0097\u0001\u0000\u0000\u0000\u0081\u0082\u0003" +
                    "&\u0013\u0000\u0082\u0083\u0006\u000b\uffff\uffff\u0000\u0083\u0097\u0001" +
                    "\u0000\u0000\u0000\u0084\u0085\u0003\n\u0005\u0000\u0085\u0086\u0006\u000b" +
                    "\uffff\uffff\u0000\u0086\u0097\u0001\u0000\u0000\u0000\u0087\u0088\u0003" +
                    "\f\u0006\u0000\u0088\u0089\u0006\u000b\uffff\uffff\u0000\u0089\u0097\u0001" +
                    "\u0000\u0000\u0000\u008a\u008b\u0003\u000e\u0007\u0000\u008b\u008c\u0006" +
                    "\u000b\uffff\uffff\u0000\u008c\u0097\u0001\u0000\u0000\u0000\u008d\u008e" +
                    "\u0003\u0010\b\u0000\u008e\u008f\u0006\u000b\uffff\uffff\u0000\u008f\u0097" +
                    "\u0001\u0000\u0000\u0000\u0090\u0091\u0003\u0012\t\u0000\u0091\u0092\u0006" +
                    "\u000b\uffff\uffff\u0000\u0092\u0097\u0001\u0000\u0000\u0000\u0093\u0094" +
                    "\u0003\u0014\n\u0000\u0094\u0095\u0006\u000b\uffff\uffff\u0000\u0095\u0097" +
                    "\u0001\u0000\u0000\u0000\u0096x\u0001\u0000\u0000\u0000\u0096{\u0001\u0000" +
                    "\u0000\u0000\u0096~\u0001\u0000\u0000\u0000\u0096\u0081\u0001\u0000\u0000" +
                    "\u0000\u0096\u0084\u0001\u0000\u0000\u0000\u0096\u0087\u0001\u0000\u0000" +
                    "\u0000\u0096\u008a\u0001\u0000\u0000\u0000\u0096\u008d\u0001\u0000\u0000" +
                    "\u0000\u0096\u0090\u0001\u0000\u0000\u0000\u0096\u0093\u0001\u0000\u0000" +
                    "\u0000\u0097\u0098\u0001\u0000\u0000\u0000\u0098\u0096\u0001\u0000\u0000" +
                    "\u0000\u0098\u0099\u0001\u0000\u0000\u0000\u0099\u0017\u0001\u0000\u0000" +
                    "\u0000\u009a\u009b\u0003\u0000\u0000\u0000\u009b\u009f\u0006\f\uffff\uffff" +
                    "\u0000\u009c\u009d\u0003\u0016\u000b\u0000\u009d\u009e\u0006\f\uffff\uffff" +
                    "\u0000\u009e\u00a0\u0001\u0000\u0000\u0000\u009f\u009c\u0001\u0000\u0000" +
                    "\u0000\u00a0\u00a1\u0001\u0000\u0000\u0000\u00a1\u009f\u0001\u0000\u0000" +
                    "\u0000\u00a1\u00a2\u0001\u0000\u0000\u0000\u00a2\u00a3\u0001\u0000\u0000" +
                    "\u0000\u00a3\u00a4\u0005\u0000\u0000\u0001\u00a4\u0019\u0001\u0000\u0000" +
                    "\u0000\u00a5\u00a6\u0005\u000e\u0000\u0000\u00a6\u00a7\u0005\u0015\u0000" +
                    "\u0000\u00a7\u00b5\u0006\r\uffff\uffff\u0000\u00a8\u00a9\u0005\u0014\u0000" +
                    "\u0000\u00a9\u00aa\u0005\u0015\u0000\u0000\u00aa\u00b3\u0006\r\uffff\uffff" +
                    "\u0000\u00ab\u00ac\u0005\u0014\u0000\u0000\u00ac\u00ad\u0005\u0016\u0000" +
                    "\u0000\u00ad\u00b1\u0006\r\uffff\uffff\u0000\u00ae\u00af\u0005\u0014\u0000" +
                    "\u0000\u00af\u00b0\u0005\u0016\u0000\u0000\u00b0\u00b2\u0006\r\uffff\uffff" +
                    "\u0000\u00b1\u00ae\u0001\u0000\u0000\u0000\u00b1\u00b2\u0001\u0000\u0000" +
                    "\u0000\u00b2\u00b4\u0001\u0000\u0000\u0000\u00b3\u00ab\u0001\u0000\u0000" +
                    "\u0000\u00b3\u00b4\u0001\u0000\u0000\u0000\u00b4\u00b6\u0001\u0000\u0000" +
                    "\u0000\u00b5\u00a8\u0001\u0000\u0000\u0000\u00b5\u00b6\u0001\u0000\u0000" +
                    "\u0000\u00b6\u001b\u0001\u0000\u0000\u0000\u00b7\u00b8\u0005\u0011\u0000" +
                    "\u0000\u00b8\u00b9\u0005#\u0000\u0000\u00b9\u00ba\u0006\u000e\uffff\uffff" +
                    "\u0000\u00ba\u001d\u0001\u0000\u0000\u0000\u00bb\u00bc\u0005\u0012\u0000" +
                    "\u0000\u00bc\u00bd\u0005\u0016\u0000\u0000\u00bd\u00be\u0005\u0014\u0000" +
                    "\u0000\u00be\u00bf\u0005\u0016\u0000\u0000\u00bf\u00c0\u0006\u000f\uffff" +
                    "\uffff\u0000\u00c0\u001f\u0001\u0000\u0000\u0000\u00c1\u00cd\u0005\u0010" +
                    "\u0000\u0000\u00c2\u00c3\u0005$\u0000\u0000\u00c3\u00c7\u0006\u0010\uffff" +
                    "\uffff\u0000\u00c4\u00c5\u0003\"\u0011\u0000\u00c5\u00c6\u0006\u0010\uffff" +
                    "\uffff\u0000\u00c6\u00c8\u0001\u0000\u0000\u0000\u00c7\u00c4\u0001\u0000" +
                    "\u0000\u0000\u00c8\u00c9\u0001\u0000\u0000\u0000\u00c9\u00c7\u0001\u0000" +
                    "\u0000\u0000\u00c9\u00ca\u0001\u0000\u0000\u0000\u00ca\u00ce\u0001\u0000" +
                    "\u0000\u0000\u00cb\u00cc\u0005#\u0000\u0000\u00cc\u00ce\u0006\u0010\uffff" +
                    "\uffff\u0000\u00cd\u00c2\u0001\u0000\u0000\u0000\u00cd\u00cb\u0001\u0000" +
                    "\u0000\u0000\u00ce!\u0001\u0000\u0000\u0000\u00cf\u00d0\u0005\u0013\u0000" +
                    "\u0000\u00d0\u00d1\u0005%\u0000\u0000\u00d1\u00d2\u0005&\u0000\u0000\u00d2" +
                    "\u00d3\u0005\'\u0000\u0000\u00d3\u00d4\u0005&\u0000\u0000\u00d4\u00d5" +
                    "\u0005(\u0000\u0000\u00d5\u00d6\u0006\u0011\uffff\uffff\u0000\u00d6#\u0001" +
                    "\u0000\u0000\u0000\u00d7\u00d8\u0005\u000f\u0000\u0000\u00d8\u00d9\u0005" +
                    "\"\u0000\u0000\u00d9\u00da\u0006\u0012\uffff\uffff\u0000\u00da%\u0001" +
                    "\u0000\u0000\u0000\u00db\u00dc\u0003\u001a\r\u0000\u00dc\u00e5\u0006\u0013" +
                    "\uffff\uffff\u0000\u00dd\u00de\u0003\u001e\u000f\u0000\u00de\u00df\u0006" +
                    "\u0013\uffff\uffff\u0000\u00df\u00e6\u0001\u0000\u0000\u0000\u00e0\u00e1" +
                    "\u0003 \u0010\u0000\u00e1\u00e2\u0006\u0013\uffff\uffff\u0000\u00e2\u00e4" +
                    "\u0001\u0000\u0000\u0000\u00e3\u00e0\u0001\u0000\u0000\u0000\u00e3\u00e4" +
                    "\u0001\u0000\u0000\u0000\u00e4\u00e6\u0001\u0000\u0000\u0000\u00e5\u00dd" +
                    "\u0001\u0000\u0000\u0000\u00e5\u00e3\u0001\u0000\u0000\u0000\u00e6\u00ea" +
                    "\u0001\u0000\u0000\u0000\u00e7\u00e8\u0003\u001c\u000e\u0000\u00e8\u00e9" +
                    "\u0006\u0013\uffff\uffff\u0000\u00e9\u00eb\u0001\u0000\u0000\u0000\u00ea" +
                    "\u00e7\u0001\u0000\u0000\u0000\u00ea\u00eb\u0001\u0000\u0000\u0000\u00eb" +
                    "\u00ef\u0001\u0000\u0000\u0000\u00ec\u00ed\u0003$\u0012\u0000\u00ed\u00ee" +
                    "\u0006\u0013\uffff\uffff\u0000\u00ee\u00f0\u0001\u0000\u0000\u0000\u00ef" +
                    "\u00ec\u0001\u0000\u0000\u0000\u00ef\u00f0\u0001\u0000\u0000\u0000\u00f0" +
                    "\'\u0001\u0000\u0000\u0000\u000fI[s\u0096\u0098\u00a1\u00b1\u00b3\u00b5" +
                    "\u00c9\u00cd\u00e3\u00e5\u00ea\u00ef";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}