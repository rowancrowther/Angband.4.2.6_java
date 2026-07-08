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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/BrandGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.brand;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.parser.brand.BrandParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class BrandGrammar extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, CODE = 2, NAME = 3, MULTIPLIER = 4, O_MULTIPLIER = 5, POWER = 6,
            VERB = 7, RESIST_FLAG = 8, VULN_FLAG = 9, INTEGER = 10, COMMENT = 11, EOL = 12, FLAG = 13,
            STRING = 14;
    public static final int
            RULE_recordCount = 0, RULE_code = 1, RULE_name = 2, RULE_multiplier = 3,
            RULE_oMultiplier = 4, RULE_power = 5, RULE_verb = 6, RULE_resistFlag = 7,
            RULE_vulnFlag = 8, RULE_brand = 9, RULE_file = 10;

    private static String[] makeRuleNames() {
        return new String[]{
                "recordCount", "code", "name", "multiplier", "oMultiplier", "power",
                "verb", "resistFlag", "vulnFlag", "brand", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'code:'", "'name:'", "'multiplier:'", "'o-multiplier:'",
                "'power:'", "'verb:'", "'resist-flag:'", "'vuln-flag:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "CODE", "NAME", "MULTIPLIER", "O_MULTIPLIER", "POWER",
                "VERB", "RESIST_FLAG", "VULN_FLAG", "INTEGER", "COMMENT", "EOL", "FLAG",
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
        return "BrandGrammar.g4";
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

    public BrandGrammar(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RecordCountContext extends ParserRuleContext {
        public String count;
        public Token INTEGER;

        public TerminalNode RECORD_COUNT() {
            return getToken(BrandGrammar.RECORD_COUNT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(BrandGrammar.INTEGER, 0);
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
            if (listener instanceof BrandGrammarListener) ((BrandGrammarListener) listener).enterRecordCount(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandGrammarListener) ((BrandGrammarListener) listener).exitRecordCount(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandGrammarVisitor)
                return ((BrandGrammarVisitor<? extends T>) visitor).visitRecordCount(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RecordCountContext recordCount() throws RecognitionException {
        RecordCountContext _localctx = new RecordCountContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_recordCount);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(22);
                match(RECORD_COUNT);
                setState(23);
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
            return getToken(BrandGrammar.CODE, 0);
        }

        public TerminalNode STRING() {
            return getToken(BrandGrammar.STRING, 0);
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
            if (listener instanceof BrandGrammarListener) ((BrandGrammarListener) listener).enterCode(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandGrammarListener) ((BrandGrammarListener) listener).exitCode(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandGrammarVisitor)
                return ((BrandGrammarVisitor<? extends T>) visitor).visitCode(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CodeContext code() throws RecognitionException {
        CodeContext _localctx = new CodeContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_code);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(26);
                match(CODE);
                setState(27);
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
            return getToken(BrandGrammar.NAME, 0);
        }

        public TerminalNode STRING() {
            return getToken(BrandGrammar.STRING, 0);
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
            if (listener instanceof BrandGrammarListener) ((BrandGrammarListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandGrammarListener) ((BrandGrammarListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandGrammarVisitor)
                return ((BrandGrammarVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(30);
                match(NAME);
                setState(31);
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
    public static class MultiplierContext extends ParserRuleContext {
        public String mult;
        public Token INTEGER;

        public TerminalNode MULTIPLIER() {
            return getToken(BrandGrammar.MULTIPLIER, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(BrandGrammar.INTEGER, 0);
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
            if (listener instanceof BrandGrammarListener) ((BrandGrammarListener) listener).enterMultiplier(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandGrammarListener) ((BrandGrammarListener) listener).exitMultiplier(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandGrammarVisitor)
                return ((BrandGrammarVisitor<? extends T>) visitor).visitMultiplier(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MultiplierContext multiplier() throws RecognitionException {
        MultiplierContext _localctx = new MultiplierContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_multiplier);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(34);
                match(MULTIPLIER);
                setState(35);
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
            return getToken(BrandGrammar.O_MULTIPLIER, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(BrandGrammar.INTEGER, 0);
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
            if (listener instanceof BrandGrammarListener) ((BrandGrammarListener) listener).enterOMultiplier(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandGrammarListener) ((BrandGrammarListener) listener).exitOMultiplier(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandGrammarVisitor)
                return ((BrandGrammarVisitor<? extends T>) visitor).visitOMultiplier(this);
            else return visitor.visitChildren(this);
        }
    }

    public final OMultiplierContext oMultiplier() throws RecognitionException {
        OMultiplierContext _localctx = new OMultiplierContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_oMultiplier);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(38);
                match(O_MULTIPLIER);
                setState(39);
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
        public String powerStr;
        public Token INTEGER;

        public TerminalNode POWER() {
            return getToken(BrandGrammar.POWER, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(BrandGrammar.INTEGER, 0);
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
            if (listener instanceof BrandGrammarListener) ((BrandGrammarListener) listener).enterPower(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandGrammarListener) ((BrandGrammarListener) listener).exitPower(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandGrammarVisitor)
                return ((BrandGrammarVisitor<? extends T>) visitor).visitPower(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PowerContext power() throws RecognitionException {
        PowerContext _localctx = new PowerContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_power);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(42);
                match(POWER);
                setState(43);
                ((PowerContext) _localctx).INTEGER = match(INTEGER);
                ((PowerContext) _localctx).powerStr = ((PowerContext) _localctx).INTEGER.getText();
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
    public static class VerbContext extends ParserRuleContext {
        public String verbStr;
        public Token STRING;

        public TerminalNode VERB() {
            return getToken(BrandGrammar.VERB, 0);
        }

        public TerminalNode STRING() {
            return getToken(BrandGrammar.STRING, 0);
        }

        public VerbContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_verb;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BrandGrammarListener) ((BrandGrammarListener) listener).enterVerb(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandGrammarListener) ((BrandGrammarListener) listener).exitVerb(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandGrammarVisitor)
                return ((BrandGrammarVisitor<? extends T>) visitor).visitVerb(this);
            else return visitor.visitChildren(this);
        }
    }

    public final VerbContext verb() throws RecognitionException {
        VerbContext _localctx = new VerbContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_verb);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(46);
                match(VERB);
                setState(47);
                ((VerbContext) _localctx).STRING = match(STRING);
                ((VerbContext) _localctx).verbStr = ((VerbContext) _localctx).STRING.getText();
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
    public static class ResistFlagContext extends ParserRuleContext {
        public String rFlag;
        public Token FLAG;

        public TerminalNode RESIST_FLAG() {
            return getToken(BrandGrammar.RESIST_FLAG, 0);
        }

        public TerminalNode FLAG() {
            return getToken(BrandGrammar.FLAG, 0);
        }

        public ResistFlagContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_resistFlag;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BrandGrammarListener) ((BrandGrammarListener) listener).enterResistFlag(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandGrammarListener) ((BrandGrammarListener) listener).exitResistFlag(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandGrammarVisitor)
                return ((BrandGrammarVisitor<? extends T>) visitor).visitResistFlag(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ResistFlagContext resistFlag() throws RecognitionException {
        ResistFlagContext _localctx = new ResistFlagContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_resistFlag);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(50);
                match(RESIST_FLAG);
                setState(51);
                ((ResistFlagContext) _localctx).FLAG = match(FLAG);
                ((ResistFlagContext) _localctx).rFlag = ((ResistFlagContext) _localctx).FLAG.getText();
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
    public static class VulnFlagContext extends ParserRuleContext {
        public String vFlag;
        public Token FLAG;

        public TerminalNode VULN_FLAG() {
            return getToken(BrandGrammar.VULN_FLAG, 0);
        }

        public TerminalNode FLAG() {
            return getToken(BrandGrammar.FLAG, 0);
        }

        public VulnFlagContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_vulnFlag;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BrandGrammarListener) ((BrandGrammarListener) listener).enterVulnFlag(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandGrammarListener) ((BrandGrammarListener) listener).exitVulnFlag(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandGrammarVisitor)
                return ((BrandGrammarVisitor<? extends T>) visitor).visitVulnFlag(this);
            else return visitor.visitChildren(this);
        }
    }

    public final VulnFlagContext vulnFlag() throws RecognitionException {
        VulnFlagContext _localctx = new VulnFlagContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_vulnFlag);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(54);
                match(VULN_FLAG);
                setState(55);
                ((VulnFlagContext) _localctx).FLAG = match(FLAG);
                ((VulnFlagContext) _localctx).vFlag = ((VulnFlagContext) _localctx).FLAG.getText();
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
        public BrandParseRecord record;
        public CodeContext code;
        public NameContext name;
        public VerbContext verb;
        public MultiplierContext multiplier;
        public OMultiplierContext oMultiplier;
        public PowerContext power;
        public ResistFlagContext resistFlag;
        public VulnFlagContext vulnFlag;

        public CodeContext code() {
            return getRuleContext(CodeContext.class, 0);
        }

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public VerbContext verb() {
            return getRuleContext(VerbContext.class, 0);
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

        public ResistFlagContext resistFlag() {
            return getRuleContext(ResistFlagContext.class, 0);
        }

        public VulnFlagContext vulnFlag() {
            return getRuleContext(VulnFlagContext.class, 0);
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
            if (listener instanceof BrandGrammarListener) ((BrandGrammarListener) listener).enterBrand(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandGrammarListener) ((BrandGrammarListener) listener).exitBrand(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandGrammarVisitor)
                return ((BrandGrammarVisitor<? extends T>) visitor).visitBrand(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BrandContext brand() throws RecognitionException {
        BrandContext _localctx = new BrandContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_brand);

        int lineInit = 0;
        String codeInit = "";
        String nameInit = "";
        String multiplierInit = "";
        String oMultiplierInit = "";
        String powerInit = "";
        String verbInit = "";
        String resistFlagInit = "";
        String vulnFlagInit = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(58);
                ((BrandContext) _localctx).code = code();
                lineInit = ((BrandContext) _localctx).code.lineNo;
                codeInit = ((BrandContext) _localctx).code.codeStr;
                setState(60);
                ((BrandContext) _localctx).name = name();
                nameInit = ((BrandContext) _localctx).name.nameStr;
                setState(62);
                ((BrandContext) _localctx).verb = verb();
                verbInit = ((BrandContext) _localctx).verb.verbStr;
                setState(64);
                ((BrandContext) _localctx).multiplier = multiplier();
                multiplierInit = ((BrandContext) _localctx).multiplier.mult;
                setState(66);
                ((BrandContext) _localctx).oMultiplier = oMultiplier();
                oMultiplierInit = ((BrandContext) _localctx).oMultiplier.oMult;
                setState(68);
                ((BrandContext) _localctx).power = power();
                powerInit = ((BrandContext) _localctx).power.powerStr;
                setState(70);
                ((BrandContext) _localctx).resistFlag = resistFlag();
                resistFlagInit = ((BrandContext) _localctx).resistFlag.rFlag;
                setState(75);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == VULN_FLAG) {
                    {
                        setState(72);
                        ((BrandContext) _localctx).vulnFlag = vulnFlag();
                        vulnFlagInit = ((BrandContext) _localctx).vulnFlag.vFlag;
                    }
                }

            }
            _ctx.stop = _input.LT(-1);

            ((BrandContext) _localctx).record = new BrandParseRecord(codeInit, nameInit, multiplierInit,
                    oMultiplierInit, powerInit, verbInit, resistFlagInit,
                    vulnFlagInit, lineInit);

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
        public List<BrandParseRecord> records;
        public String declaredRecordCount;
        public RecordCountContext recordCount;
        public BrandContext brand;

        public RecordCountContext recordCount() {
            return getRuleContext(RecordCountContext.class, 0);
        }

        public TerminalNode EOF() {
            return getToken(BrandGrammar.EOF, 0);
        }

        public List<BrandContext> brand() {
            return getRuleContexts(BrandContext.class);
        }

        public BrandContext brand(int i) {
            return getRuleContext(BrandContext.class, i);
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
            if (listener instanceof BrandGrammarListener) ((BrandGrammarListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandGrammarListener) ((BrandGrammarListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandGrammarVisitor)
                return ((BrandGrammarVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_file);

        ((FileContext) _localctx).records = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(77);
                ((FileContext) _localctx).recordCount = recordCount();
                ((FileContext) _localctx).declaredRecordCount = ((FileContext) _localctx).recordCount.count;
                setState(82);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(79);
                            ((FileContext) _localctx).brand = brand();
                            _localctx.records.add(((FileContext) _localctx).brand.record);
                        }
                    }
                    setState(84);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == CODE);
                setState(86);
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
            "\u0004\u0001\u000eY\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002" +
                    "\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001" +
                    "\t\u0001\t\u0001\t\u0003\tL\b\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0004\nS\b\n\u000b\n\f\nT\u0001\n\u0001\n\u0001\n\u0000\u0000\u000b" +
                    "\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0000\u0000O\u0000" +
                    "\u0016\u0001\u0000\u0000\u0000\u0002\u001a\u0001\u0000\u0000\u0000\u0004" +
                    "\u001e\u0001\u0000\u0000\u0000\u0006\"\u0001\u0000\u0000\u0000\b&\u0001" +
                    "\u0000\u0000\u0000\n*\u0001\u0000\u0000\u0000\f.\u0001\u0000\u0000\u0000" +
                    "\u000e2\u0001\u0000\u0000\u0000\u00106\u0001\u0000\u0000\u0000\u0012:" +
                    "\u0001\u0000\u0000\u0000\u0014M\u0001\u0000\u0000\u0000\u0016\u0017\u0005" +
                    "\u0001\u0000\u0000\u0017\u0018\u0005\n\u0000\u0000\u0018\u0019\u0006\u0000" +
                    "\uffff\uffff\u0000\u0019\u0001\u0001\u0000\u0000\u0000\u001a\u001b\u0005" +
                    "\u0002\u0000\u0000\u001b\u001c\u0005\u000e\u0000\u0000\u001c\u001d\u0006" +
                    "\u0001\uffff\uffff\u0000\u001d\u0003\u0001\u0000\u0000\u0000\u001e\u001f" +
                    "\u0005\u0003\u0000\u0000\u001f \u0005\u000e\u0000\u0000 !\u0006\u0002" +
                    "\uffff\uffff\u0000!\u0005\u0001\u0000\u0000\u0000\"#\u0005\u0004\u0000" +
                    "\u0000#$\u0005\n\u0000\u0000$%\u0006\u0003\uffff\uffff\u0000%\u0007\u0001" +
                    "\u0000\u0000\u0000&\'\u0005\u0005\u0000\u0000\'(\u0005\n\u0000\u0000(" +
                    ")\u0006\u0004\uffff\uffff\u0000)\t\u0001\u0000\u0000\u0000*+\u0005\u0006" +
                    "\u0000\u0000+,\u0005\n\u0000\u0000,-\u0006\u0005\uffff\uffff\u0000-\u000b" +
                    "\u0001\u0000\u0000\u0000./\u0005\u0007\u0000\u0000/0\u0005\u000e\u0000" +
                    "\u000001\u0006\u0006\uffff\uffff\u00001\r\u0001\u0000\u0000\u000023\u0005" +
                    "\b\u0000\u000034\u0005\r\u0000\u000045\u0006\u0007\uffff\uffff\u00005" +
                    "\u000f\u0001\u0000\u0000\u000067\u0005\t\u0000\u000078\u0005\r\u0000\u0000" +
                    "89\u0006\b\uffff\uffff\u00009\u0011\u0001\u0000\u0000\u0000:;\u0003\u0002" +
                    "\u0001\u0000;<\u0006\t\uffff\uffff\u0000<=\u0003\u0004\u0002\u0000=>\u0006" +
                    "\t\uffff\uffff\u0000>?\u0003\f\u0006\u0000?@\u0006\t\uffff\uffff\u0000" +
                    "@A\u0003\u0006\u0003\u0000AB\u0006\t\uffff\uffff\u0000BC\u0003\b\u0004" +
                    "\u0000CD\u0006\t\uffff\uffff\u0000DE\u0003\n\u0005\u0000EF\u0006\t\uffff" +
                    "\uffff\u0000FG\u0003\u000e\u0007\u0000GK\u0006\t\uffff\uffff\u0000HI\u0003" +
                    "\u0010\b\u0000IJ\u0006\t\uffff\uffff\u0000JL\u0001\u0000\u0000\u0000K" +
                    "H\u0001\u0000\u0000\u0000KL\u0001\u0000\u0000\u0000L\u0013\u0001\u0000" +
                    "\u0000\u0000MN\u0003\u0000\u0000\u0000NR\u0006\n\uffff\uffff\u0000OP\u0003" +
                    "\u0012\t\u0000PQ\u0006\n\uffff\uffff\u0000QS\u0001\u0000\u0000\u0000R" +
                    "O\u0001\u0000\u0000\u0000ST\u0001\u0000\u0000\u0000TR\u0001\u0000\u0000" +
                    "\u0000TU\u0001\u0000\u0000\u0000UV\u0001\u0000\u0000\u0000VW\u0005\u0000" +
                    "\u0000\u0001W\u0015\u0001\u0000\u0000\u0000\u0002KT";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}