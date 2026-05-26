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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/SlayGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.slayreader;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.objects.Slay;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class SlayGrammarParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, CODE = 3, NAME = 4, RACE_FLAG = 5, MULTIPLIER = 6, O_MULTIPLIER = 7,
            POWER = 8, MELEE_VERB = 9, RANGE_VERB = 10, NUMBER = 11, TEXT = 12;
    public static final int
            RULE_code = 0, RULE_name = 1, RULE_raceFlag = 2, RULE_multiplier = 3,
            RULE_oMultiplier = 4, RULE_power = 5, RULE_meleeVerb = 6, RULE_rangeVerb = 7,
            RULE_slay = 8, RULE_slays = 9;

    private static String[] makeRuleNames() {
        return new String[]{
                "code", "name", "raceFlag", "multiplier", "oMultiplier", "power", "meleeVerb",
                "rangeVerb", "slay", "slays"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'code:'", "'name:'", "'race-flag:'", "'multiplier:'",
                "'o-multiplier:'", "'power:'", "'melee-verb:'", "'range-verb:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "CODE", "NAME", "RACE_FLAG", "MULTIPLIER", "O_MULTIPLIER",
                "POWER", "MELEE_VERB", "RANGE_VERB", "NUMBER", "TEXT"
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

    public SlayGrammarParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class CodeContext extends ParserRuleContext {
        public String codeStr;
        public Token TEXT;

        public TerminalNode CODE() {
            return getToken(SlayGrammarParser.CODE, 0);
        }

        public TerminalNode TEXT() {
            return getToken(SlayGrammarParser.TEXT, 0);
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
        enterRule(_localctx, 0, RULE_code);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(20);
                match(CODE);
                setState(21);
                ((CodeContext) _localctx).TEXT = match(TEXT);

                ((CodeContext) _localctx).codeStr = ((CodeContext) _localctx).TEXT.getText();

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
        public Token TEXT;

        public TerminalNode NAME() {
            return getToken(SlayGrammarParser.NAME, 0);
        }

        public TerminalNode TEXT() {
            return getToken(SlayGrammarParser.TEXT, 0);
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
        enterRule(_localctx, 2, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(24);
                match(NAME);
                setState(25);
                ((NameContext) _localctx).TEXT = match(TEXT);

                ((NameContext) _localctx).nameStr = ((NameContext) _localctx).TEXT.getText();

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
        public MonsterRaceFlag raceFlagEnum;
        public Token TEXT;

        public TerminalNode RACE_FLAG() {
            return getToken(SlayGrammarParser.RACE_FLAG, 0);
        }

        public TerminalNode TEXT() {
            return getToken(SlayGrammarParser.TEXT, 0);
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
        enterRule(_localctx, 4, RULE_raceFlag);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(28);
                match(RACE_FLAG);
                setState(29);
                ((RaceFlagContext) _localctx).TEXT = match(TEXT);

                MonsterRaceFlag flag = MonsterRaceFlag.RF_NONE;
                String flagText = "RF_" + ((RaceFlagContext) _localctx).TEXT.getText();
                try {
                    flag = MonsterRaceFlag.valueOf(flagText);
                } catch (Exception e) {
                    // Do nothing
                }
                ((RaceFlagContext) _localctx).raceFlagEnum = flag;

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
        public int multiplierNum;
        public Token NUMBER;

        public TerminalNode MULTIPLIER() {
            return getToken(SlayGrammarParser.MULTIPLIER, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(SlayGrammarParser.NUMBER, 0);
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
        enterRule(_localctx, 6, RULE_multiplier);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(32);
                match(MULTIPLIER);
                setState(33);
                ((MultiplierContext) _localctx).NUMBER = match(NUMBER);

                ((MultiplierContext) _localctx).multiplierNum = Integer.parseInt(((MultiplierContext) _localctx).NUMBER.getText());

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
        public int oMultiplierNum;
        public Token NUMBER;

        public TerminalNode O_MULTIPLIER() {
            return getToken(SlayGrammarParser.O_MULTIPLIER, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(SlayGrammarParser.NUMBER, 0);
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
        enterRule(_localctx, 8, RULE_oMultiplier);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(36);
                match(O_MULTIPLIER);
                setState(37);
                ((OMultiplierContext) _localctx).NUMBER = match(NUMBER);

                ((OMultiplierContext) _localctx).oMultiplierNum = Integer.parseInt(((OMultiplierContext) _localctx).NUMBER.getText());

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
        public int powerNum;
        public Token NUMBER;

        public TerminalNode POWER() {
            return getToken(SlayGrammarParser.POWER, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(SlayGrammarParser.NUMBER, 0);
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
        enterRule(_localctx, 10, RULE_power);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(40);
                match(POWER);
                setState(41);
                ((PowerContext) _localctx).NUMBER = match(NUMBER);

                ((PowerContext) _localctx).powerNum = Integer.parseInt(((PowerContext) _localctx).NUMBER.getText());

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
        public String melee;
        public Token TEXT;

        public TerminalNode MELEE_VERB() {
            return getToken(SlayGrammarParser.MELEE_VERB, 0);
        }

        public TerminalNode TEXT() {
            return getToken(SlayGrammarParser.TEXT, 0);
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
        enterRule(_localctx, 12, RULE_meleeVerb);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(44);
                match(MELEE_VERB);
                setState(45);
                ((MeleeVerbContext) _localctx).TEXT = match(TEXT);

                ((MeleeVerbContext) _localctx).melee = ((MeleeVerbContext) _localctx).TEXT.getText();

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
        public String ranged;
        public Token TEXT;

        public TerminalNode RANGE_VERB() {
            return getToken(SlayGrammarParser.RANGE_VERB, 0);
        }

        public TerminalNode TEXT() {
            return getToken(SlayGrammarParser.TEXT, 0);
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
        enterRule(_localctx, 14, RULE_rangeVerb);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(48);
                match(RANGE_VERB);
                setState(49);
                ((RangeVerbContext) _localctx).TEXT = match(TEXT);

                ((RangeVerbContext) _localctx).ranged = ((RangeVerbContext) _localctx).TEXT.getText();

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
        public Slay slayObject;
        public CodeContext code;
        public NameContext name;
        public RaceFlagContext raceFlag;
        public MultiplierContext multiplier;
        public OMultiplierContext oMultiplier;
        public PowerContext power;
        public MeleeVerbContext meleeVerb;
        public RangeVerbContext rangeVerb;

        public CodeContext code() {
            return getRuleContext(CodeContext.class, 0);
        }

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public RaceFlagContext raceFlag() {
            return getRuleContext(RaceFlagContext.class, 0);
        }

        public MultiplierContext multiplier() {
            return getRuleContext(MultiplierContext.class, 0);
        }

        public OMultiplierContext oMultiplier() {
            return getRuleContext(OMultiplierContext.class, 0);
        }

        public PowerContext power() {
            return getRuleContext(PowerContext.class, 0);
        }

        public MeleeVerbContext meleeVerb() {
            return getRuleContext(MeleeVerbContext.class, 0);
        }

        public RangeVerbContext rangeVerb() {
            return getRuleContext(RangeVerbContext.class, 0);
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
        enterRule(_localctx, 16, RULE_slay);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(52);
                ((SlayContext) _localctx).code = code();
                setState(53);
                ((SlayContext) _localctx).name = name();
                setState(54);
                ((SlayContext) _localctx).raceFlag = raceFlag();
                setState(55);
                ((SlayContext) _localctx).multiplier = multiplier();
                setState(56);
                ((SlayContext) _localctx).oMultiplier = oMultiplier();
                setState(57);
                ((SlayContext) _localctx).power = power();
                setState(58);
                ((SlayContext) _localctx).meleeVerb = meleeVerb();
                setState(59);
                ((SlayContext) _localctx).rangeVerb = rangeVerb();

                ((SlayContext) _localctx).slayObject = new Slay(((SlayContext) _localctx).code.codeStr, ((SlayContext) _localctx).name.nameStr, null,
                        ((SlayContext) _localctx).meleeVerb.melee, ((SlayContext) _localctx).rangeVerb.ranged,
                        ((SlayContext) _localctx).raceFlag.raceFlagEnum, ((SlayContext) _localctx).multiplier.multiplierNum,
                        ((SlayContext) _localctx).oMultiplier.oMultiplierNum, ((SlayContext) _localctx).power.powerNum);

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
    public static class SlaysContext extends ParserRuleContext {
        public ArrayList<Slay> slayList;
        public SlayContext slay;

        public TerminalNode EOF() {
            return getToken(SlayGrammarParser.EOF, 0);
        }

        public List<SlayContext> slay() {
            return getRuleContexts(SlayContext.class);
        }

        public SlayContext slay(int i) {
            return getRuleContext(SlayContext.class, i);
        }

        public SlaysContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_slays;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).enterSlays(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SlayGrammarListener) ((SlayGrammarListener) listener).exitSlays(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SlayGrammarVisitor)
                return ((SlayGrammarVisitor<? extends T>) visitor).visitSlays(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SlaysContext slays() throws RecognitionException {
        SlaysContext _localctx = new SlaysContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_slays);

        ((SlaysContext) _localctx).slayList = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(65);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(62);
                            ((SlaysContext) _localctx).slay = slay();

                            _localctx.slayList.add(((SlaysContext) _localctx).slay.slayObject);

                        }
                    }
                    setState(67);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == CODE);
                setState(69);
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
            "\u0004\u0001\fH\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002" +
                    "\b\u0007\b\u0002\t\u0007\t\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001" +
                    "\t\u0001\t\u0004\tB\b\t\u000b\t\f\tC\u0001\t\u0001\t\u0001\t\u0000\u0000" +
                    "\n\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0000\u0000>\u0000" +
                    "\u0014\u0001\u0000\u0000\u0000\u0002\u0018\u0001\u0000\u0000\u0000\u0004" +
                    "\u001c\u0001\u0000\u0000\u0000\u0006 \u0001\u0000\u0000\u0000\b$\u0001" +
                    "\u0000\u0000\u0000\n(\u0001\u0000\u0000\u0000\f,\u0001\u0000\u0000\u0000" +
                    "\u000e0\u0001\u0000\u0000\u0000\u00104\u0001\u0000\u0000\u0000\u0012A" +
                    "\u0001\u0000\u0000\u0000\u0014\u0015\u0005\u0003\u0000\u0000\u0015\u0016" +
                    "\u0005\f\u0000\u0000\u0016\u0017\u0006\u0000\uffff\uffff\u0000\u0017\u0001" +
                    "\u0001\u0000\u0000\u0000\u0018\u0019\u0005\u0004\u0000\u0000\u0019\u001a" +
                    "\u0005\f\u0000\u0000\u001a\u001b\u0006\u0001\uffff\uffff\u0000\u001b\u0003" +
                    "\u0001\u0000\u0000\u0000\u001c\u001d\u0005\u0005\u0000\u0000\u001d\u001e" +
                    "\u0005\f\u0000\u0000\u001e\u001f\u0006\u0002\uffff\uffff\u0000\u001f\u0005" +
                    "\u0001\u0000\u0000\u0000 !\u0005\u0006\u0000\u0000!\"\u0005\u000b\u0000" +
                    "\u0000\"#\u0006\u0003\uffff\uffff\u0000#\u0007\u0001\u0000\u0000\u0000" +
                    "$%\u0005\u0007\u0000\u0000%&\u0005\u000b\u0000\u0000&\'\u0006\u0004\uffff" +
                    "\uffff\u0000\'\t\u0001\u0000\u0000\u0000()\u0005\b\u0000\u0000)*\u0005" +
                    "\u000b\u0000\u0000*+\u0006\u0005\uffff\uffff\u0000+\u000b\u0001\u0000" +
                    "\u0000\u0000,-\u0005\t\u0000\u0000-.\u0005\f\u0000\u0000./\u0006\u0006" +
                    "\uffff\uffff\u0000/\r\u0001\u0000\u0000\u000001\u0005\n\u0000\u000012" +
                    "\u0005\f\u0000\u000023\u0006\u0007\uffff\uffff\u00003\u000f\u0001\u0000" +
                    "\u0000\u000045\u0003\u0000\u0000\u000056\u0003\u0002\u0001\u000067\u0003" +
                    "\u0004\u0002\u000078\u0003\u0006\u0003\u000089\u0003\b\u0004\u00009:\u0003" +
                    "\n\u0005\u0000:;\u0003\f\u0006\u0000;<\u0003\u000e\u0007\u0000<=\u0006" +
                    "\b\uffff\uffff\u0000=\u0011\u0001\u0000\u0000\u0000>?\u0003\u0010\b\u0000" +
                    "?@\u0006\t\uffff\uffff\u0000@B\u0001\u0000\u0000\u0000A>\u0001\u0000\u0000" +
                    "\u0000BC\u0001\u0000\u0000\u0000CA\u0001\u0000\u0000\u0000CD\u0001\u0000" +
                    "\u0000\u0000DE\u0001\u0000\u0000\u0000EF\u0005\u0000\u0000\u0001F\u0013" +
                    "\u0001\u0000\u0000\u0000\u0001C";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}