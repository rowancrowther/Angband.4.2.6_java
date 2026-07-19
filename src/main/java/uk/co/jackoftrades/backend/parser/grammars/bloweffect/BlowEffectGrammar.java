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

// Generated from BlowEffectGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.bloweffect;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.parser.bloweffect.BlowEffectParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class BlowEffectGrammar extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, NAME = 2, POWER = 3, EVAL = 4, DESC = 5, LORE_COLOUR_BASE = 6, LORE_COLOUR_RESIST = 7,
            LORE_COLOUR_IMMUNE = 8, EFFECT_TYPE = 9, RESIST = 10, LASH = 11, INTEGER = 12, COMMENT = 13,
            EOL = 14, STRING = 15, EOL_POP = 16;
    public static final int
            RULE_recordCount = 0, RULE_name = 1, RULE_power = 2, RULE_eval = 3, RULE_desc = 4,
            RULE_loreColourBase = 5, RULE_loreColourResist = 6, RULE_loreColourImmune = 7,
            RULE_effectType = 8, RULE_resist = 9, RULE_lashType = 10, RULE_blowEffect = 11,
            RULE_file = 12;

    private static String[] makeRuleNames() {
        return new String[]{
                "recordCount", "name", "power", "eval", "desc", "loreColourBase", "loreColourResist",
                "loreColourImmune", "effectType", "resist", "lashType", "blowEffect",
                "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'name:'", "'power:'", "'eval:'", "'desc:'",
                "'lore-color-base:'", "'lore-color-resist:'", "'lore-color-immune:'",
                "'effect-type:'", "'resist:'", "'lash-type:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "NAME", "POWER", "EVAL", "DESC", "LORE_COLOUR_BASE",
                "LORE_COLOUR_RESIST", "LORE_COLOUR_IMMUNE", "EFFECT_TYPE", "RESIST",
                "LASH", "INTEGER", "COMMENT", "EOL", "STRING", "EOL_POP"
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
        return "BlowEffectGrammar.g4";
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

    public BlowEffectGrammar(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RecordCountContext extends ParserRuleContext {
        public String count;
        public Token INTEGER;

        public TerminalNode RECORD_COUNT() {
            return getToken(BlowEffectGrammar.RECORD_COUNT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(BlowEffectGrammar.INTEGER, 0);
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
            if (listener instanceof BlowEffectGrammarListener)
                ((BlowEffectGrammarListener) listener).enterRecordCount(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BlowEffectGrammarListener)
                ((BlowEffectGrammarListener) listener).exitRecordCount(this);
        }
    }

    public final RecordCountContext recordCount() throws RecognitionException {
        RecordCountContext _localctx = new RecordCountContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_recordCount);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(26);
                match(RECORD_COUNT);
                setState(27);
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
        public Token STRING;

        public TerminalNode NAME() {
            return getToken(BlowEffectGrammar.NAME, 0);
        }

        public TerminalNode STRING() {
            return getToken(BlowEffectGrammar.STRING, 0);
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
            if (listener instanceof BlowEffectGrammarListener) ((BlowEffectGrammarListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BlowEffectGrammarListener) ((BlowEffectGrammarListener) listener).exitName(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(30);
                match(NAME);
                setState(31);
                ((NameContext) _localctx).STRING = match(STRING);
                ((NameContext) _localctx).line = _localctx.start.getLine();
                ((NameContext) _localctx).nameStr = ((NameContext) _localctx).STRING.getText();
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
        public String powerVal;
        public Token INTEGER;

        public TerminalNode POWER() {
            return getToken(BlowEffectGrammar.POWER, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(BlowEffectGrammar.INTEGER, 0);
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
            if (listener instanceof BlowEffectGrammarListener) ((BlowEffectGrammarListener) listener).enterPower(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BlowEffectGrammarListener) ((BlowEffectGrammarListener) listener).exitPower(this);
        }
    }

    public final PowerContext power() throws RecognitionException {
        PowerContext _localctx = new PowerContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_power);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(34);
                match(POWER);
                setState(35);
                ((PowerContext) _localctx).INTEGER = match(INTEGER);
                ((PowerContext) _localctx).powerVal = ((PowerContext) _localctx).INTEGER.getText();
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
    public static class EvalContext extends ParserRuleContext {
        public String evalVal;
        public Token INTEGER;

        public TerminalNode EVAL() {
            return getToken(BlowEffectGrammar.EVAL, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(BlowEffectGrammar.INTEGER, 0);
        }

        public EvalContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_eval;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BlowEffectGrammarListener) ((BlowEffectGrammarListener) listener).enterEval(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BlowEffectGrammarListener) ((BlowEffectGrammarListener) listener).exitEval(this);
        }
    }

    public final EvalContext eval() throws RecognitionException {
        EvalContext _localctx = new EvalContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_eval);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(38);
                match(EVAL);
                setState(39);
                ((EvalContext) _localctx).INTEGER = match(INTEGER);
                ((EvalContext) _localctx).evalVal = ((EvalContext) _localctx).INTEGER.getText();
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

        public TerminalNode DESC() {
            return getToken(BlowEffectGrammar.DESC, 0);
        }

        public TerminalNode STRING() {
            return getToken(BlowEffectGrammar.STRING, 0);
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
            if (listener instanceof BlowEffectGrammarListener) ((BlowEffectGrammarListener) listener).enterDesc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BlowEffectGrammarListener) ((BlowEffectGrammarListener) listener).exitDesc(this);
        }
    }

    public final DescContext desc() throws RecognitionException {
        DescContext _localctx = new DescContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_desc);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(42);
                match(DESC);
                setState(43);
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
    public static class LoreColourBaseContext extends ParserRuleContext {
        public String colourStr;
        public Token STRING;

        public TerminalNode LORE_COLOUR_BASE() {
            return getToken(BlowEffectGrammar.LORE_COLOUR_BASE, 0);
        }

        public TerminalNode STRING() {
            return getToken(BlowEffectGrammar.STRING, 0);
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
            if (listener instanceof BlowEffectGrammarListener)
                ((BlowEffectGrammarListener) listener).enterLoreColourBase(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BlowEffectGrammarListener)
                ((BlowEffectGrammarListener) listener).exitLoreColourBase(this);
        }
    }

    public final LoreColourBaseContext loreColourBase() throws RecognitionException {
        LoreColourBaseContext _localctx = new LoreColourBaseContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_loreColourBase);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(46);
                match(LORE_COLOUR_BASE);
                setState(47);
                ((LoreColourBaseContext) _localctx).STRING = match(STRING);
                ((LoreColourBaseContext) _localctx).colourStr = ((LoreColourBaseContext) _localctx).STRING.getText();
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
        public String colourStr;
        public Token STRING;

        public TerminalNode LORE_COLOUR_RESIST() {
            return getToken(BlowEffectGrammar.LORE_COLOUR_RESIST, 0);
        }

        public TerminalNode STRING() {
            return getToken(BlowEffectGrammar.STRING, 0);
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
            if (listener instanceof BlowEffectGrammarListener)
                ((BlowEffectGrammarListener) listener).enterLoreColourResist(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BlowEffectGrammarListener)
                ((BlowEffectGrammarListener) listener).exitLoreColourResist(this);
        }
    }

    public final LoreColourResistContext loreColourResist() throws RecognitionException {
        LoreColourResistContext _localctx = new LoreColourResistContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_loreColourResist);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(50);
                match(LORE_COLOUR_RESIST);
                setState(51);
                ((LoreColourResistContext) _localctx).STRING = match(STRING);
                ((LoreColourResistContext) _localctx).colourStr = ((LoreColourResistContext) _localctx).STRING.getText();
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
        public String colourStr;
        public Token STRING;

        public TerminalNode LORE_COLOUR_IMMUNE() {
            return getToken(BlowEffectGrammar.LORE_COLOUR_IMMUNE, 0);
        }

        public TerminalNode STRING() {
            return getToken(BlowEffectGrammar.STRING, 0);
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
            if (listener instanceof BlowEffectGrammarListener)
                ((BlowEffectGrammarListener) listener).enterLoreColourImmune(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BlowEffectGrammarListener)
                ((BlowEffectGrammarListener) listener).exitLoreColourImmune(this);
        }
    }

    public final LoreColourImmuneContext loreColourImmune() throws RecognitionException {
        LoreColourImmuneContext _localctx = new LoreColourImmuneContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_loreColourImmune);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(54);
                match(LORE_COLOUR_IMMUNE);
                setState(55);
                ((LoreColourImmuneContext) _localctx).STRING = match(STRING);
                ((LoreColourImmuneContext) _localctx).colourStr = ((LoreColourImmuneContext) _localctx).STRING.getText();
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
    public static class EffectTypeContext extends ParserRuleContext {
        public String type;
        public Token STRING;

        public TerminalNode EFFECT_TYPE() {
            return getToken(BlowEffectGrammar.EFFECT_TYPE, 0);
        }

        public TerminalNode STRING() {
            return getToken(BlowEffectGrammar.STRING, 0);
        }

        public EffectTypeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_effectType;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BlowEffectGrammarListener)
                ((BlowEffectGrammarListener) listener).enterEffectType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BlowEffectGrammarListener)
                ((BlowEffectGrammarListener) listener).exitEffectType(this);
        }
    }

    public final EffectTypeContext effectType() throws RecognitionException {
        EffectTypeContext _localctx = new EffectTypeContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_effectType);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(58);
                match(EFFECT_TYPE);
                setState(59);
                ((EffectTypeContext) _localctx).STRING = match(STRING);
                ((EffectTypeContext) _localctx).type = ((EffectTypeContext) _localctx).STRING.getText();
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
        public String resistStr;
        public Token STRING;

        public TerminalNode RESIST() {
            return getToken(BlowEffectGrammar.RESIST, 0);
        }

        public TerminalNode STRING() {
            return getToken(BlowEffectGrammar.STRING, 0);
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
            if (listener instanceof BlowEffectGrammarListener) ((BlowEffectGrammarListener) listener).enterResist(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BlowEffectGrammarListener) ((BlowEffectGrammarListener) listener).exitResist(this);
        }
    }

    public final ResistContext resist() throws RecognitionException {
        ResistContext _localctx = new ResistContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_resist);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(62);
                match(RESIST);
                setState(63);
                ((ResistContext) _localctx).STRING = match(STRING);
                ((ResistContext) _localctx).resistStr = ((ResistContext) _localctx).STRING.getText();
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
    public static class LashTypeContext extends ParserRuleContext {
        public String lashStr;
        public Token STRING;

        public TerminalNode LASH() {
            return getToken(BlowEffectGrammar.LASH, 0);
        }

        public TerminalNode STRING() {
            return getToken(BlowEffectGrammar.STRING, 0);
        }

        public LashTypeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_lashType;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BlowEffectGrammarListener)
                ((BlowEffectGrammarListener) listener).enterLashType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BlowEffectGrammarListener)
                ((BlowEffectGrammarListener) listener).exitLashType(this);
        }
    }

    public final LashTypeContext lashType() throws RecognitionException {
        LashTypeContext _localctx = new LashTypeContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_lashType);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(66);
                match(LASH);
                setState(67);
                ((LashTypeContext) _localctx).STRING = match(STRING);
                ((LashTypeContext) _localctx).lashStr = ((LashTypeContext) _localctx).STRING.getText();
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
    public static class BlowEffectContext extends ParserRuleContext {
        public BlowEffectParseRecord effect;
        public NameContext name;
        public PowerContext power;
        public EvalContext eval;
        public DescContext desc;
        public LoreColourBaseContext loreColourBase;
        public LoreColourResistContext loreColourResist;
        public LoreColourImmuneContext loreColourImmune;
        public EffectTypeContext effectType;
        public ResistContext resist;
        public LashTypeContext lashType;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public List<PowerContext> power() {
            return getRuleContexts(PowerContext.class);
        }

        public PowerContext power(int i) {
            return getRuleContext(PowerContext.class, i);
        }

        public List<EvalContext> eval() {
            return getRuleContexts(EvalContext.class);
        }

        public EvalContext eval(int i) {
            return getRuleContext(EvalContext.class, i);
        }

        public List<DescContext> desc() {
            return getRuleContexts(DescContext.class);
        }

        public DescContext desc(int i) {
            return getRuleContext(DescContext.class, i);
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

        public List<EffectTypeContext> effectType() {
            return getRuleContexts(EffectTypeContext.class);
        }

        public EffectTypeContext effectType(int i) {
            return getRuleContext(EffectTypeContext.class, i);
        }

        public List<ResistContext> resist() {
            return getRuleContexts(ResistContext.class);
        }

        public ResistContext resist(int i) {
            return getRuleContext(ResistContext.class, i);
        }

        public List<LashTypeContext> lashType() {
            return getRuleContexts(LashTypeContext.class);
        }

        public LashTypeContext lashType(int i) {
            return getRuleContext(LashTypeContext.class, i);
        }

        public BlowEffectContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_blowEffect;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BlowEffectGrammarListener)
                ((BlowEffectGrammarListener) listener).enterBlowEffect(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BlowEffectGrammarListener)
                ((BlowEffectGrammarListener) listener).exitBlowEffect(this);
        }
    }

    public final BlowEffectContext blowEffect() throws RecognitionException {
        BlowEffectContext _localctx = new BlowEffectContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_blowEffect);

        String nameInit = "";
        String powerInit = "";
        String evalInit = "";
        String descInit = "";
        String loreColourBaseInit = "";
        String loreColourResistInit = "";
        String loreColourImmuneInit = "";
        String effectTypeInit = "";
        String resistInit = "";
        String lashTypeInit = "";
        int line = 0;

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(70);
                ((BlowEffectContext) _localctx).name = name();
                nameInit = ((BlowEffectContext) _localctx).name.nameStr;
                line = ((BlowEffectContext) _localctx).name.line;
                setState(99);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        setState(99);
                        _errHandler.sync(this);
                        switch (_input.LA(1)) {
                            case POWER: {
                                setState(72);
                                ((BlowEffectContext) _localctx).power = power();
                                powerInit = ((BlowEffectContext) _localctx).power.powerVal;
                            }
                            break;
                            case EVAL: {
                                setState(75);
                                ((BlowEffectContext) _localctx).eval = eval();
                                evalInit = ((BlowEffectContext) _localctx).eval.evalVal;
                            }
                            break;
                            case DESC: {
                                setState(78);
                                ((BlowEffectContext) _localctx).desc = desc();
                                descInit = ((BlowEffectContext) _localctx).desc.descStr;
                            }
                            break;
                            case LORE_COLOUR_BASE: {
                                setState(81);
                                ((BlowEffectContext) _localctx).loreColourBase = loreColourBase();
                                loreColourBaseInit = ((BlowEffectContext) _localctx).loreColourBase.colourStr;
                            }
                            break;
                            case LORE_COLOUR_RESIST: {
                                setState(84);
                                ((BlowEffectContext) _localctx).loreColourResist = loreColourResist();
                                loreColourResistInit = ((BlowEffectContext) _localctx).loreColourResist.colourStr;
                            }
                            break;
                            case LORE_COLOUR_IMMUNE: {
                                setState(87);
                                ((BlowEffectContext) _localctx).loreColourImmune = loreColourImmune();
                                loreColourImmuneInit = ((BlowEffectContext) _localctx).loreColourImmune.colourStr;
                            }
                            break;
                            case EFFECT_TYPE: {
                                setState(90);
                                ((BlowEffectContext) _localctx).effectType = effectType();
                                effectTypeInit = ((BlowEffectContext) _localctx).effectType.type;
                            }
                            break;
                            case RESIST: {
                                setState(93);
                                ((BlowEffectContext) _localctx).resist = resist();
                                resistInit = ((BlowEffectContext) _localctx).resist.resistStr;
                            }
                            break;
                            case LASH: {
                                setState(96);
                                ((BlowEffectContext) _localctx).lashType = lashType();
                                lashTypeInit = ((BlowEffectContext) _localctx).lashType.lashStr;
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                    }
                    setState(101);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 4088L) != 0));
            }
            _ctx.stop = _input.LT(-1);

            ((BlowEffectContext) _localctx).effect = new BlowEffectParseRecord(nameInit, powerInit,
                    evalInit, descInit, loreColourBaseInit,
                    loreColourResistInit, loreColourImmuneInit,
                    effectTypeInit, resistInit, lashTypeInit, line);

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
        public List<BlowEffectParseRecord> blowEffects;
        public RecordCountContext recordCount;
        public BlowEffectContext blowEffect;

        public RecordCountContext recordCount() {
            return getRuleContext(RecordCountContext.class, 0);
        }

        public TerminalNode EOF() {
            return getToken(BlowEffectGrammar.EOF, 0);
        }

        public List<BlowEffectContext> blowEffect() {
            return getRuleContexts(BlowEffectContext.class);
        }

        public BlowEffectContext blowEffect(int i) {
            return getRuleContext(BlowEffectContext.class, i);
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
            if (listener instanceof BlowEffectGrammarListener) ((BlowEffectGrammarListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BlowEffectGrammarListener) ((BlowEffectGrammarListener) listener).exitFile(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_file);

        ((FileContext) _localctx).blowEffects = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(103);
                ((FileContext) _localctx).recordCount = recordCount();
                ((FileContext) _localctx).declaredRecordCount = ((FileContext) _localctx).recordCount.count;
                setState(108);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(105);
                            ((FileContext) _localctx).blowEffect = blowEffect();
                            _localctx.blowEffects.add(((FileContext) _localctx).blowEffect.effect);
                        }
                    }
                    setState(110);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(112);
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
            "\u0004\u0001\u0010s\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002" +
                    "\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002" +
                    "\f\u0007\f\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b" +
                    "\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0004\u000bd\b\u000b" +
                    "\u000b\u000b\f\u000be\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0004\f" +
                    "m\b\f\u000b\f\f\fn\u0001\f\u0001\f\u0001\f\u0000\u0000\r\u0000\u0002\u0004" +
                    "\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u0000\u0000o\u0000\u001a" +
                    "\u0001\u0000\u0000\u0000\u0002\u001e\u0001\u0000\u0000\u0000\u0004\"\u0001" +
                    "\u0000\u0000\u0000\u0006&\u0001\u0000\u0000\u0000\b*\u0001\u0000\u0000" +
                    "\u0000\n.\u0001\u0000\u0000\u0000\f2\u0001\u0000\u0000\u0000\u000e6\u0001" +
                    "\u0000\u0000\u0000\u0010:\u0001\u0000\u0000\u0000\u0012>\u0001\u0000\u0000" +
                    "\u0000\u0014B\u0001\u0000\u0000\u0000\u0016F\u0001\u0000\u0000\u0000\u0018" +
                    "g\u0001\u0000\u0000\u0000\u001a\u001b\u0005\u0001\u0000\u0000\u001b\u001c" +
                    "\u0005\f\u0000\u0000\u001c\u001d\u0006\u0000\uffff\uffff\u0000\u001d\u0001" +
                    "\u0001\u0000\u0000\u0000\u001e\u001f\u0005\u0002\u0000\u0000\u001f \u0005" +
                    "\u000f\u0000\u0000 !\u0006\u0001\uffff\uffff\u0000!\u0003\u0001\u0000" +
                    "\u0000\u0000\"#\u0005\u0003\u0000\u0000#$\u0005\f\u0000\u0000$%\u0006" +
                    "\u0002\uffff\uffff\u0000%\u0005\u0001\u0000\u0000\u0000&\'\u0005\u0004" +
                    "\u0000\u0000\'(\u0005\f\u0000\u0000()\u0006\u0003\uffff\uffff\u0000)\u0007" +
                    "\u0001\u0000\u0000\u0000*+\u0005\u0005\u0000\u0000+,\u0005\u000f\u0000" +
                    "\u0000,-\u0006\u0004\uffff\uffff\u0000-\t\u0001\u0000\u0000\u0000./\u0005" +
                    "\u0006\u0000\u0000/0\u0005\u000f\u0000\u000001\u0006\u0005\uffff\uffff" +
                    "\u00001\u000b\u0001\u0000\u0000\u000023\u0005\u0007\u0000\u000034\u0005" +
                    "\u000f\u0000\u000045\u0006\u0006\uffff\uffff\u00005\r\u0001\u0000\u0000" +
                    "\u000067\u0005\b\u0000\u000078\u0005\u000f\u0000\u000089\u0006\u0007\uffff" +
                    "\uffff\u00009\u000f\u0001\u0000\u0000\u0000:;\u0005\t\u0000\u0000;<\u0005" +
                    "\u000f\u0000\u0000<=\u0006\b\uffff\uffff\u0000=\u0011\u0001\u0000\u0000" +
                    "\u0000>?\u0005\n\u0000\u0000?@\u0005\u000f\u0000\u0000@A\u0006\t\uffff" +
                    "\uffff\u0000A\u0013\u0001\u0000\u0000\u0000BC\u0005\u000b\u0000\u0000" +
                    "CD\u0005\u000f\u0000\u0000DE\u0006\n\uffff\uffff\u0000E\u0015\u0001\u0000" +
                    "\u0000\u0000FG\u0003\u0002\u0001\u0000Gc\u0006\u000b\uffff\uffff\u0000" +
                    "HI\u0003\u0004\u0002\u0000IJ\u0006\u000b\uffff\uffff\u0000Jd\u0001\u0000" +
                    "\u0000\u0000KL\u0003\u0006\u0003\u0000LM\u0006\u000b\uffff\uffff\u0000" +
                    "Md\u0001\u0000\u0000\u0000NO\u0003\b\u0004\u0000OP\u0006\u000b\uffff\uffff" +
                    "\u0000Pd\u0001\u0000\u0000\u0000QR\u0003\n\u0005\u0000RS\u0006\u000b\uffff" +
                    "\uffff\u0000Sd\u0001\u0000\u0000\u0000TU\u0003\f\u0006\u0000UV\u0006\u000b" +
                    "\uffff\uffff\u0000Vd\u0001\u0000\u0000\u0000WX\u0003\u000e\u0007\u0000" +
                    "XY\u0006\u000b\uffff\uffff\u0000Yd\u0001\u0000\u0000\u0000Z[\u0003\u0010" +
                    "\b\u0000[\\\u0006\u000b\uffff\uffff\u0000\\d\u0001\u0000\u0000\u0000]" +
                    "^\u0003\u0012\t\u0000^_\u0006\u000b\uffff\uffff\u0000_d\u0001\u0000\u0000" +
                    "\u0000`a\u0003\u0014\n\u0000ab\u0006\u000b\uffff\uffff\u0000bd\u0001\u0000" +
                    "\u0000\u0000cH\u0001\u0000\u0000\u0000cK\u0001\u0000\u0000\u0000cN\u0001" +
                    "\u0000\u0000\u0000cQ\u0001\u0000\u0000\u0000cT\u0001\u0000\u0000\u0000" +
                    "cW\u0001\u0000\u0000\u0000cZ\u0001\u0000\u0000\u0000c]\u0001\u0000\u0000" +
                    "\u0000c`\u0001\u0000\u0000\u0000de\u0001\u0000\u0000\u0000ec\u0001\u0000" +
                    "\u0000\u0000ef\u0001\u0000\u0000\u0000f\u0017\u0001\u0000\u0000\u0000" +
                    "gh\u0003\u0000\u0000\u0000hl\u0006\f\uffff\uffff\u0000ij\u0003\u0016\u000b" +
                    "\u0000jk\u0006\f\uffff\uffff\u0000km\u0001\u0000\u0000\u0000li\u0001\u0000" +
                    "\u0000\u0000mn\u0001\u0000\u0000\u0000nl\u0001\u0000\u0000\u0000no\u0001" +
                    "\u0000\u0000\u0000op\u0001\u0000\u0000\u0000pq\u0005\u0000\u0000\u0001" +
                    "q\u0019\u0001\u0000\u0000\u0000\u0003cen";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}