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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/SlayGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.slay;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.parser.slay.SlayParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class SlayGrammar extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, CODE = 2, NAME = 3, RACE_FLAG = 4, BASE = 5, MULTIPLIER = 6, O_MULTIPLIER = 7,
            POWER = 8, MELEE_VERB = 9, RANGE_VERB = 10, INTEGER = 11, COMMENT = 12, EOL = 13,
            FLAG = 14, STRING = 15;
    public static final int
            RULE_recordCount = 0, RULE_code = 1, RULE_name = 2, RULE_raceFlag = 3,
            RULE_base = 4, RULE_multiplier = 5, RULE_oMultiplier = 6, RULE_power = 7,
            RULE_meleeVerb = 8, RULE_rangeVerb = 9, RULE_slay = 10, RULE_file = 11;

    private static String[] makeRuleNames() {
        return new String[]{
                "recordCount", "code", "name", "raceFlag", "base", "multiplier", "oMultiplier",
                "power", "meleeVerb", "rangeVerb", "slay", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'code:'", "'name:'", "'race-flag:'", "'base:'",
                "'multiplier:'", "'o-multiplier:'", "'power:'", "'melee-verb:'", "'range-verb:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "CODE", "NAME", "RACE_FLAG", "BASE", "MULTIPLIER",
                "O_MULTIPLIER", "POWER", "MELEE_VERB", "RANGE_VERB", "INTEGER", "COMMENT",
                "EOL", "FLAG", "STRING"
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
        return "SlayGrammar.g4";
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

    public SlayGrammar(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RecordCountContext extends ParserRuleContext {
        public String count;
        public Token INTEGER;

        public TerminalNode RECORD_COUNT() {
            return getToken(SlayGrammar.RECORD_COUNT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(SlayGrammar.INTEGER, 0);
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
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).enterRecordCount(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).exitRecordCount(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SlayGrammarVisitor)
                return ((SlayGrammarVisitor<? extends T>) visitor).visitRecordCount(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RecordCountContext recordCount() throws RecognitionException {
        RecordCountContext _localctx = new RecordCountContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_recordCount);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(24);
                match(RECORD_COUNT);
                setState(25);
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
    public static class CodeContext extends ParserRuleContext {
        public String codeStr;
        public int lineNo;
        public Token STRING;

        public TerminalNode CODE() {
            return getToken(SlayGrammar.CODE, 0);
        }

        public TerminalNode STRING() {
            return getToken(SlayGrammar.STRING, 0);
        }

        public CodeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_code;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).enterCode(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).exitCode(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SlayGrammarVisitor)
                return ((SlayGrammarVisitor<? extends T>) visitor).visitCode(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CodeContext code() throws RecognitionException {
        CodeContext _localctx = new CodeContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_code);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(28);
                match(CODE);
                setState(29);
                ((CodeContext) _localctx).STRING = match(STRING);

                ((CodeContext) _localctx).codeStr = ((CodeContext) _localctx).STRING.getText();
                ((CodeContext) _localctx).lineNo = _localctx.start.getLine();

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
        public Token STRING;

        public TerminalNode NAME() {
            return getToken(SlayGrammar.NAME, 0);
        }

        public TerminalNode STRING() {
            return getToken(SlayGrammar.STRING, 0);
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
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SlayGrammarVisitor)
                return ((SlayGrammarVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(32);
                match(NAME);
                setState(33);
                ((NameContext) _localctx).STRING = match(STRING);
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
    public static class RaceFlagContext extends ParserRuleContext {
        public String rFlag;
        public Token FLAG;

        public TerminalNode RACE_FLAG() {
            return getToken(SlayGrammar.RACE_FLAG, 0);
        }

        public TerminalNode FLAG() {
            return getToken(SlayGrammar.FLAG, 0);
        }

        public RaceFlagContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_raceFlag;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).enterRaceFlag(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).exitRaceFlag(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SlayGrammarVisitor)
                return ((SlayGrammarVisitor<? extends T>) visitor).visitRaceFlag(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RaceFlagContext raceFlag() throws RecognitionException {
        RaceFlagContext _localctx = new RaceFlagContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_raceFlag);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(36);
                match(RACE_FLAG);
                setState(37);
                ((RaceFlagContext) _localctx).FLAG = match(FLAG);
                ((RaceFlagContext) _localctx).rFlag = ((RaceFlagContext) _localctx).FLAG.getText();
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
    public static class BaseContext extends ParserRuleContext {
        public String baseStr;
        public Token STRING;

        public TerminalNode BASE() {
            return getToken(SlayGrammar.BASE, 0);
        }

        public TerminalNode STRING() {
            return getToken(SlayGrammar.STRING, 0);
        }

        public BaseContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_base;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).enterBase(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).exitBase(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SlayGrammarVisitor)
                return ((SlayGrammarVisitor<? extends T>) visitor).visitBase(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BaseContext base() throws RecognitionException {
        BaseContext _localctx = new BaseContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_base);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(40);
                match(BASE);
                setState(41);
                ((BaseContext) _localctx).STRING = match(STRING);
                ((BaseContext) _localctx).baseStr = ((BaseContext) _localctx).STRING.getText();
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
    public static class MultiplierContext extends ParserRuleContext {
        public String mult;
        public Token INTEGER;

        public TerminalNode MULTIPLIER() {
            return getToken(SlayGrammar.MULTIPLIER, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(SlayGrammar.INTEGER, 0);
        }

        public MultiplierContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_multiplier;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).enterMultiplier(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).exitMultiplier(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SlayGrammarVisitor)
                return ((SlayGrammarVisitor<? extends T>) visitor).visitMultiplier(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MultiplierContext multiplier() throws RecognitionException {
        MultiplierContext _localctx = new MultiplierContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_multiplier);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(44);
                match(MULTIPLIER);
                setState(45);
                ((MultiplierContext) _localctx).INTEGER = match(INTEGER);
                ((MultiplierContext) _localctx).mult = ((MultiplierContext) _localctx).INTEGER.getText();
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
    public static class OMultiplierContext extends ParserRuleContext {
        public String oMult;
        public Token INTEGER;

        public TerminalNode O_MULTIPLIER() {
            return getToken(SlayGrammar.O_MULTIPLIER, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(SlayGrammar.INTEGER, 0);
        }

        public OMultiplierContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_oMultiplier;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).enterOMultiplier(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).exitOMultiplier(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SlayGrammarVisitor)
                return ((SlayGrammarVisitor<? extends T>) visitor).visitOMultiplier(this);
            else return visitor.visitChildren(this);
        }
    }

    public final OMultiplierContext oMultiplier() throws RecognitionException {
        OMultiplierContext _localctx = new OMultiplierContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_oMultiplier);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(48);
                match(O_MULTIPLIER);
                setState(49);
                ((OMultiplierContext) _localctx).INTEGER = match(INTEGER);
                ((OMultiplierContext) _localctx).oMult = ((OMultiplierContext) _localctx).INTEGER.getText();
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
            return getToken(SlayGrammar.POWER, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(SlayGrammar.INTEGER, 0);
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
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).enterPower(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).exitPower(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SlayGrammarVisitor)
                return ((SlayGrammarVisitor<? extends T>) visitor).visitPower(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PowerContext power() throws RecognitionException {
        PowerContext _localctx = new PowerContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_power);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(52);
                match(POWER);
                setState(53);
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
    public static class MeleeVerbContext extends ParserRuleContext {
        public String mVerb;
        public Token STRING;

        public TerminalNode MELEE_VERB() {
            return getToken(SlayGrammar.MELEE_VERB, 0);
        }

        public TerminalNode STRING() {
            return getToken(SlayGrammar.STRING, 0);
        }

        public MeleeVerbContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_meleeVerb;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).enterMeleeVerb(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).exitMeleeVerb(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SlayGrammarVisitor)
                return ((SlayGrammarVisitor<? extends T>) visitor).visitMeleeVerb(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MeleeVerbContext meleeVerb() throws RecognitionException {
        MeleeVerbContext _localctx = new MeleeVerbContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_meleeVerb);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(56);
                match(MELEE_VERB);
                setState(57);
                ((MeleeVerbContext) _localctx).STRING = match(STRING);
                ((MeleeVerbContext) _localctx).mVerb = ((MeleeVerbContext) _localctx).STRING.getText();
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
    public static class RangeVerbContext extends ParserRuleContext {
        public String rVerb;
        public Token STRING;

        public TerminalNode RANGE_VERB() {
            return getToken(SlayGrammar.RANGE_VERB, 0);
        }

        public TerminalNode STRING() {
            return getToken(SlayGrammar.STRING, 0);
        }

        public RangeVerbContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_rangeVerb;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).enterRangeVerb(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).exitRangeVerb(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SlayGrammarVisitor)
                return ((SlayGrammarVisitor<? extends T>) visitor).visitRangeVerb(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RangeVerbContext rangeVerb() throws RecognitionException {
        RangeVerbContext _localctx = new RangeVerbContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_rangeVerb);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(60);
                match(RANGE_VERB);
                setState(61);
                ((RangeVerbContext) _localctx).STRING = match(STRING);
                ((RangeVerbContext) _localctx).rVerb = ((RangeVerbContext) _localctx).STRING.getText();
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
        public SlayParseRecord record;
        public CodeContext code;
        public NameContext name;
        public RaceFlagContext raceFlag;
        public BaseContext base;
        public MultiplierContext multiplier;
        public OMultiplierContext oMultiplier;
        public PowerContext power;
        public MeleeVerbContext meleeVerb;
        public RangeVerbContext rangeVerb;

        public CodeContext code() {
            return getRuleContext(CodeContext.class, 0);
        }

        public List<NameContext> name() {
            return getRuleContexts(NameContext.class);
        }

        public NameContext name(int i) {
            return getRuleContext(NameContext.class, i);
        }

        public List<RaceFlagContext> raceFlag() {
            return getRuleContexts(RaceFlagContext.class);
        }

        public RaceFlagContext raceFlag(int i) {
            return getRuleContext(RaceFlagContext.class, i);
        }

        public List<BaseContext> base() {
            return getRuleContexts(BaseContext.class);
        }

        public BaseContext base(int i) {
            return getRuleContext(BaseContext.class, i);
        }

        public List<MultiplierContext> multiplier() {
            return getRuleContexts(MultiplierContext.class);
        }

        public MultiplierContext multiplier(int i) {
            return getRuleContext(MultiplierContext.class, i);
        }

        public List<OMultiplierContext> oMultiplier() {
            return getRuleContexts(OMultiplierContext.class);
        }

        public OMultiplierContext oMultiplier(int i) {
            return getRuleContext(OMultiplierContext.class, i);
        }

        public List<PowerContext> power() {
            return getRuleContexts(PowerContext.class);
        }

        public PowerContext power(int i) {
            return getRuleContext(PowerContext.class, i);
        }

        public List<MeleeVerbContext> meleeVerb() {
            return getRuleContexts(MeleeVerbContext.class);
        }

        public MeleeVerbContext meleeVerb(int i) {
            return getRuleContext(MeleeVerbContext.class, i);
        }

        public List<RangeVerbContext> rangeVerb() {
            return getRuleContexts(RangeVerbContext.class);
        }

        public RangeVerbContext rangeVerb(int i) {
            return getRuleContext(RangeVerbContext.class, i);
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
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).enterSlay(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).exitSlay(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SlayGrammarVisitor)
                return ((SlayGrammarVisitor<? extends T>) visitor).visitSlay(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SlayContext slay() throws RecognitionException {
        SlayContext _localctx = new SlayContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_slay);

        String codeInit = "";
        String nameInit = "";
        String raceFlagInit = "";
        String baseInit = "";
        String multInit = "";
        String oMultInit = "";
        String powerInit = "";
        String mVerbInit = "";
        String rVerbInit = "";
        int line = 0;

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(64);
                ((SlayContext) _localctx).code = code();

                codeInit = ((SlayContext) _localctx).code.codeStr;
                line = ((SlayContext) _localctx).code.lineNo;

                setState(92);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 2040L) != 0)) {
                    {
                        setState(90);
                        _errHandler.sync(this);
                        switch (_input.LA(1)) {
                            case NAME: {
                                setState(66);
                                ((SlayContext) _localctx).name = name();
                                nameInit = ((SlayContext) _localctx).name.nameStr;
                            }
                            break;
                            case RACE_FLAG: {
                                setState(69);
                                ((SlayContext) _localctx).raceFlag = raceFlag();
                                raceFlagInit = ((SlayContext) _localctx).raceFlag.rFlag;
                            }
                            break;
                            case BASE: {
                                setState(72);
                                ((SlayContext) _localctx).base = base();
                                baseInit = ((SlayContext) _localctx).base.baseStr;
                            }
                            break;
                            case MULTIPLIER: {
                                setState(75);
                                ((SlayContext) _localctx).multiplier = multiplier();
                                multInit = ((SlayContext) _localctx).multiplier.mult;
                            }
                            break;
                            case O_MULTIPLIER: {
                                setState(78);
                                ((SlayContext) _localctx).oMultiplier = oMultiplier();
                                oMultInit = ((SlayContext) _localctx).oMultiplier.oMult;
                            }
                            break;
                            case POWER: {
                                setState(81);
                                ((SlayContext) _localctx).power = power();
                                powerInit = ((SlayContext) _localctx).power.powerInt;
                            }
                            break;
                            case MELEE_VERB: {
                                setState(84);
                                ((SlayContext) _localctx).meleeVerb = meleeVerb();
                                mVerbInit = ((SlayContext) _localctx).meleeVerb.mVerb;
                            }
                            break;
                            case RANGE_VERB: {
                                setState(87);
                                ((SlayContext) _localctx).rangeVerb = rangeVerb();
                                rVerbInit = ((SlayContext) _localctx).rangeVerb.rVerb;
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                    }
                    setState(94);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
            }
            _ctx.stop = _input.LT(-1);

            ((SlayContext) _localctx).record = new SlayParseRecord(codeInit, nameInit,
                    raceFlagInit, baseInit, multInit, oMultInit,
                    powerInit, mVerbInit, rVerbInit, line);

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
        public List<SlayParseRecord> records;
        public String declaredRecordCount;
        public RecordCountContext recordCount;
        public SlayContext slay;

        public RecordCountContext recordCount() {
            return getRuleContext(RecordCountContext.class, 0);
        }

        public TerminalNode EOF() {
            return getToken(SlayGrammar.EOF, 0);
        }

        public List<SlayContext> slay() {
            return getRuleContexts(SlayContext.class);
        }

        public SlayContext slay(int i) {
            return getRuleContext(SlayContext.class, i);
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
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SlayGrammarVisitor)
                return ((SlayGrammarVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_file);

        ((FileContext) _localctx).records = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(95);
                ((FileContext) _localctx).recordCount = recordCount();
                ((FileContext) _localctx).declaredRecordCount = ((FileContext) _localctx).recordCount.count;
                setState(100);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(97);
                            ((FileContext) _localctx).slay = slay();
                            _localctx.records.add(((FileContext) _localctx).slay.record);
                        }
                    }
                    setState(102);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == CODE);
                setState(104);
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
            "\u0004\u0001\u000fk\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002" +
                    "\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001" +
                    "\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0005\n[\b\n\n\n\f\n^\t\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0004\u000be\b\u000b\u000b\u000b\f\u000bf\u0001\u000b" +
                    "\u0001\u000b\u0001\u000b\u0000\u0000\f\u0000\u0002\u0004\u0006\b\n\f\u000e" +
                    "\u0010\u0012\u0014\u0016\u0000\u0000g\u0000\u0018\u0001\u0000\u0000\u0000" +
                    "\u0002\u001c\u0001\u0000\u0000\u0000\u0004 \u0001\u0000\u0000\u0000\u0006" +
                    "$\u0001\u0000\u0000\u0000\b(\u0001\u0000\u0000\u0000\n,\u0001\u0000\u0000" +
                    "\u0000\f0\u0001\u0000\u0000\u0000\u000e4\u0001\u0000\u0000\u0000\u0010" +
                    "8\u0001\u0000\u0000\u0000\u0012<\u0001\u0000\u0000\u0000\u0014@\u0001" +
                    "\u0000\u0000\u0000\u0016_\u0001\u0000\u0000\u0000\u0018\u0019\u0005\u0001" +
                    "\u0000\u0000\u0019\u001a\u0005\u000b\u0000\u0000\u001a\u001b\u0006\u0000" +
                    "\uffff\uffff\u0000\u001b\u0001\u0001\u0000\u0000\u0000\u001c\u001d\u0005" +
                    "\u0002\u0000\u0000\u001d\u001e\u0005\u000f\u0000\u0000\u001e\u001f\u0006" +
                    "\u0001\uffff\uffff\u0000\u001f\u0003\u0001\u0000\u0000\u0000 !\u0005\u0003" +
                    "\u0000\u0000!\"\u0005\u000f\u0000\u0000\"#\u0006\u0002\uffff\uffff\u0000" +
                    "#\u0005\u0001\u0000\u0000\u0000$%\u0005\u0004\u0000\u0000%&\u0005\u000e" +
                    "\u0000\u0000&\'\u0006\u0003\uffff\uffff\u0000\'\u0007\u0001\u0000\u0000" +
                    "\u0000()\u0005\u0005\u0000\u0000)*\u0005\u000f\u0000\u0000*+\u0006\u0004" +
                    "\uffff\uffff\u0000+\t\u0001\u0000\u0000\u0000,-\u0005\u0006\u0000\u0000" +
                    "-.\u0005\u000b\u0000\u0000./\u0006\u0005\uffff\uffff\u0000/\u000b\u0001" +
                    "\u0000\u0000\u000001\u0005\u0007\u0000\u000012\u0005\u000b\u0000\u0000" +
                    "23\u0006\u0006\uffff\uffff\u00003\r\u0001\u0000\u0000\u000045\u0005\b" +
                    "\u0000\u000056\u0005\u000b\u0000\u000067\u0006\u0007\uffff\uffff\u0000" +
                    "7\u000f\u0001\u0000\u0000\u000089\u0005\t\u0000\u00009:\u0005\u000f\u0000" +
                    "\u0000:;\u0006\b\uffff\uffff\u0000;\u0011\u0001\u0000\u0000\u0000<=\u0005" +
                    "\n\u0000\u0000=>\u0005\u000f\u0000\u0000>?\u0006\t\uffff\uffff\u0000?" +
                    "\u0013\u0001\u0000\u0000\u0000@A\u0003\u0002\u0001\u0000A\\\u0006\n\uffff" +
                    "\uffff\u0000BC\u0003\u0004\u0002\u0000CD\u0006\n\uffff\uffff\u0000D[\u0001" +
                    "\u0000\u0000\u0000EF\u0003\u0006\u0003\u0000FG\u0006\n\uffff\uffff\u0000" +
                    "G[\u0001\u0000\u0000\u0000HI\u0003\b\u0004\u0000IJ\u0006\n\uffff\uffff" +
                    "\u0000J[\u0001\u0000\u0000\u0000KL\u0003\n\u0005\u0000LM\u0006\n\uffff" +
                    "\uffff\u0000M[\u0001\u0000\u0000\u0000NO\u0003\f\u0006\u0000OP\u0006\n" +
                    "\uffff\uffff\u0000P[\u0001\u0000\u0000\u0000QR\u0003\u000e\u0007\u0000" +
                    "RS\u0006\n\uffff\uffff\u0000S[\u0001\u0000\u0000\u0000TU\u0003\u0010\b" +
                    "\u0000UV\u0006\n\uffff\uffff\u0000V[\u0001\u0000\u0000\u0000WX\u0003\u0012" +
                    "\t\u0000XY\u0006\n\uffff\uffff\u0000Y[\u0001\u0000\u0000\u0000ZB\u0001" +
                    "\u0000\u0000\u0000ZE\u0001\u0000\u0000\u0000ZH\u0001\u0000\u0000\u0000" +
                    "ZK\u0001\u0000\u0000\u0000ZN\u0001\u0000\u0000\u0000ZQ\u0001\u0000\u0000" +
                    "\u0000ZT\u0001\u0000\u0000\u0000ZW\u0001\u0000\u0000\u0000[^\u0001\u0000" +
                    "\u0000\u0000\\Z\u0001\u0000\u0000\u0000\\]\u0001\u0000\u0000\u0000]\u0015" +
                    "\u0001\u0000\u0000\u0000^\\\u0001\u0000\u0000\u0000_`\u0003\u0000\u0000" +
                    "\u0000`d\u0006\u000b\uffff\uffff\u0000ab\u0003\u0014\n\u0000bc\u0006\u000b" +
                    "\uffff\uffff\u0000ce\u0001\u0000\u0000\u0000da\u0001\u0000\u0000\u0000" +
                    "ef\u0001\u0000\u0000\u0000fd\u0001\u0000\u0000\u0000fg\u0001\u0000\u0000" +
                    "\u0000gh\u0001\u0000\u0000\u0000hi\u0005\u0000\u0000\u0001i\u0017\u0001" +
                    "\u0000\u0000\u0000\u0003Z\\f";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}