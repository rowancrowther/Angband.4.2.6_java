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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Brand.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.brand;

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
import uk.co.jackoftrades.middle.objects.Brand;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class BrandParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, CODE = 3, NAME = 4, MULTIPLIER = 5, O_MULTIPLIER = 6, POWER = 7,
            VERB = 8, RESIST_FLAG = 9, VULN_FLAG = 10, NUMBER = 11, FLAG = 12, TEXT = 13;
    public static final int
            RULE_code = 0, RULE_name = 1, RULE_multiplier = 2, RULE_o_multiplier = 3,
            RULE_power = 4, RULE_verb = 5, RULE_resist_flag = 6, RULE_vuln_flag = 7,
            RULE_brand = 8, RULE_file = 9;

    private static String[] makeRuleNames() {
        return new String[]{
                "code", "name", "multiplier", "o_multiplier", "power", "verb", "resist_flag",
                "vuln_flag", "brand", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'code:'", "'name:'", "'multiplier:'", "'o-multiplier:'",
                "'power:'", "'verb:'", "'resist-flag:'", "'vuln-flag:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "CODE", "NAME", "MULTIPLIER", "O_MULTIPLIER",
                "POWER", "VERB", "RESIST_FLAG", "VULN_FLAG", "NUMBER", "FLAG", "TEXT"
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
        return "Brand.g4";
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

    public BrandParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class CodeContext extends ParserRuleContext {
        public String codeStr;
        public Token FLAG;

        public TerminalNode CODE() {
            return getToken(BrandParser.CODE, 0);
        }

        public TerminalNode FLAG() {
            return getToken(BrandParser.FLAG, 0);
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
            if (listener instanceof BrandListener) ((BrandListener) listener).enterCode(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandListener) ((BrandListener) listener).exitCode(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandVisitor) return ((BrandVisitor<? extends T>) visitor).visitCode(this);
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
                ((CodeContext) _localctx).FLAG = match(FLAG);
                ((CodeContext) _localctx).codeStr = ((CodeContext) _localctx).FLAG.getText();
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
            return getToken(BrandParser.NAME, 0);
        }

        public TerminalNode TEXT() {
            return getToken(BrandParser.TEXT, 0);
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
            if (listener instanceof BrandListener) ((BrandListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandListener) ((BrandListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandVisitor) return ((BrandVisitor<? extends T>) visitor).visitName(this);
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
    public static class MultiplierContext extends ParserRuleContext {
        public int multNum;
        public Token NUMBER;

        public TerminalNode MULTIPLIER() {
            return getToken(BrandParser.MULTIPLIER, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(BrandParser.NUMBER, 0);
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
            if (listener instanceof BrandListener) ((BrandListener) listener).enterMultiplier(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandListener) ((BrandListener) listener).exitMultiplier(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandVisitor) return ((BrandVisitor<? extends T>) visitor).visitMultiplier(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MultiplierContext multiplier() throws RecognitionException {
        MultiplierContext _localctx = new MultiplierContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_multiplier);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(28);
                match(MULTIPLIER);
                setState(29);
                ((MultiplierContext) _localctx).NUMBER = match(NUMBER);
                ((MultiplierContext) _localctx).multNum = Integer.parseInt(((MultiplierContext) _localctx).NUMBER.getText());
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
    public static class O_multiplierContext extends ParserRuleContext {
        public int oMultNum;
        public Token NUMBER;

        public TerminalNode O_MULTIPLIER() {
            return getToken(BrandParser.O_MULTIPLIER, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(BrandParser.NUMBER, 0);
        }

        public O_multiplierContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_o_multiplier;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BrandListener) ((BrandListener) listener).enterO_multiplier(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandListener) ((BrandListener) listener).exitO_multiplier(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandVisitor) return ((BrandVisitor<? extends T>) visitor).visitO_multiplier(this);
            else return visitor.visitChildren(this);
        }
    }

    public final O_multiplierContext o_multiplier() throws RecognitionException {
        O_multiplierContext _localctx = new O_multiplierContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_o_multiplier);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(32);
                match(O_MULTIPLIER);
                setState(33);
                ((O_multiplierContext) _localctx).NUMBER = match(NUMBER);
                ((O_multiplierContext) _localctx).oMultNum = Integer.parseInt(((O_multiplierContext) _localctx).NUMBER.getText());
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
            return getToken(BrandParser.POWER, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(BrandParser.NUMBER, 0);
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
            if (listener instanceof BrandListener) ((BrandListener) listener).enterPower(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandListener) ((BrandListener) listener).exitPower(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandVisitor) return ((BrandVisitor<? extends T>) visitor).visitPower(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PowerContext power() throws RecognitionException {
        PowerContext _localctx = new PowerContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_power);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(36);
                match(POWER);
                setState(37);
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
    public static class VerbContext extends ParserRuleContext {
        public String verbStr;
        public Token TEXT;

        public TerminalNode VERB() {
            return getToken(BrandParser.VERB, 0);
        }

        public TerminalNode TEXT() {
            return getToken(BrandParser.TEXT, 0);
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
            if (listener instanceof BrandListener) ((BrandListener) listener).enterVerb(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandListener) ((BrandListener) listener).exitVerb(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandVisitor) return ((BrandVisitor<? extends T>) visitor).visitVerb(this);
            else return visitor.visitChildren(this);
        }
    }

    public final VerbContext verb() throws RecognitionException {
        VerbContext _localctx = new VerbContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_verb);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(40);
                match(VERB);
                setState(41);
                ((VerbContext) _localctx).TEXT = match(TEXT);
                ((VerbContext) _localctx).verbStr = ((VerbContext) _localctx).TEXT.getText();
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
    public static class Resist_flagContext extends ParserRuleContext {
        public MonsterRaceFlag resFlag;
        public Token FLAG;

        public TerminalNode RESIST_FLAG() {
            return getToken(BrandParser.RESIST_FLAG, 0);
        }

        public TerminalNode FLAG() {
            return getToken(BrandParser.FLAG, 0);
        }

        public Resist_flagContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_resist_flag;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BrandListener) ((BrandListener) listener).enterResist_flag(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandListener) ((BrandListener) listener).exitResist_flag(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandVisitor) return ((BrandVisitor<? extends T>) visitor).visitResist_flag(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Resist_flagContext resist_flag() throws RecognitionException {
        Resist_flagContext _localctx = new Resist_flagContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_resist_flag);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(44);
                match(RESIST_FLAG);
                setState(45);
                ((Resist_flagContext) _localctx).FLAG = match(FLAG);
                ((Resist_flagContext) _localctx).resFlag = MonsterRaceFlag.valueOf("RF_" + ((Resist_flagContext) _localctx).FLAG.getText().trim());
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
    public static class Vuln_flagContext extends ParserRuleContext {
        public MonsterRaceFlag vulnFlag;
        public Token FLAG;

        public TerminalNode VULN_FLAG() {
            return getToken(BrandParser.VULN_FLAG, 0);
        }

        public TerminalNode FLAG() {
            return getToken(BrandParser.FLAG, 0);
        }

        public Vuln_flagContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_vuln_flag;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BrandListener) ((BrandListener) listener).enterVuln_flag(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandListener) ((BrandListener) listener).exitVuln_flag(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandVisitor) return ((BrandVisitor<? extends T>) visitor).visitVuln_flag(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Vuln_flagContext vuln_flag() throws RecognitionException {
        Vuln_flagContext _localctx = new Vuln_flagContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_vuln_flag);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(48);
                match(VULN_FLAG);
                setState(49);
                ((Vuln_flagContext) _localctx).FLAG = match(FLAG);
                ((Vuln_flagContext) _localctx).vulnFlag = MonsterRaceFlag.valueOf("RF_" + ((Vuln_flagContext) _localctx).FLAG.getText().trim());
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
        public CodeContext code;
        public NameContext name;
        public MultiplierContext multiplier;
        public O_multiplierContext o_multiplier;
        public PowerContext power;
        public VerbContext verb;
        public Resist_flagContext resist_flag;
        public Vuln_flagContext vuln_flag;

        public CodeContext code() {
            return getRuleContext(CodeContext.class, 0);
        }

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public List<MultiplierContext> multiplier() {
            return getRuleContexts(MultiplierContext.class);
        }

        public MultiplierContext multiplier(int i) {
            return getRuleContext(MultiplierContext.class, i);
        }

        public List<O_multiplierContext> o_multiplier() {
            return getRuleContexts(O_multiplierContext.class);
        }

        public O_multiplierContext o_multiplier(int i) {
            return getRuleContext(O_multiplierContext.class, i);
        }

        public List<PowerContext> power() {
            return getRuleContexts(PowerContext.class);
        }

        public PowerContext power(int i) {
            return getRuleContext(PowerContext.class, i);
        }

        public List<VerbContext> verb() {
            return getRuleContexts(VerbContext.class);
        }

        public VerbContext verb(int i) {
            return getRuleContext(VerbContext.class, i);
        }

        public List<Resist_flagContext> resist_flag() {
            return getRuleContexts(Resist_flagContext.class);
        }

        public Resist_flagContext resist_flag(int i) {
            return getRuleContext(Resist_flagContext.class, i);
        }

        public List<Vuln_flagContext> vuln_flag() {
            return getRuleContexts(Vuln_flagContext.class);
        }

        public Vuln_flagContext vuln_flag(int i) {
            return getRuleContext(Vuln_flagContext.class, i);
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
            if (listener instanceof BrandListener) ((BrandListener) listener).enterBrand(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandListener) ((BrandListener) listener).exitBrand(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandVisitor) return ((BrandVisitor<? extends T>) visitor).visitBrand(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BrandContext brand() throws RecognitionException {
        BrandContext _localctx = new BrandContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_brand);

        String codeInit = "";
        String nameInit = "";
        String verbInit = "";
        MonsterRaceFlag resFlagInit = MonsterRaceFlag.RF_NONE;
        MonsterRaceFlag vulnFlagInit = MonsterRaceFlag.RF_NONE;
        int multInit = 0;
        int oMultInit = 0;
        int powerInit = 0;

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(52);
                ((BrandContext) _localctx).code = code();
                codeInit = ((BrandContext) _localctx).code.codeStr;
                setState(54);
                ((BrandContext) _localctx).name = name();
                nameInit = ((BrandContext) _localctx).name.nameStr;
                setState(76);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 2016L) != 0)) {
                    {
                        setState(74);
                        _errHandler.sync(this);
                        switch (_input.LA(1)) {
                            case MULTIPLIER: {
                                setState(56);
                                ((BrandContext) _localctx).multiplier = multiplier();
                                multInit = ((BrandContext) _localctx).multiplier.multNum;
                            }
                            break;
                            case O_MULTIPLIER: {
                                setState(59);
                                ((BrandContext) _localctx).o_multiplier = o_multiplier();
                                oMultInit = ((BrandContext) _localctx).o_multiplier.oMultNum;
                            }
                            break;
                            case POWER: {
                                setState(62);
                                ((BrandContext) _localctx).power = power();
                                powerInit = ((BrandContext) _localctx).power.powerNum;
                            }
                            break;
                            case VERB: {
                                setState(65);
                                ((BrandContext) _localctx).verb = verb();
                                verbInit = ((BrandContext) _localctx).verb.verbStr;
                            }
                            break;
                            case RESIST_FLAG: {
                                setState(68);
                                ((BrandContext) _localctx).resist_flag = resist_flag();
                                resFlagInit = ((BrandContext) _localctx).resist_flag.resFlag;
                            }
                            break;
                            case VULN_FLAG: {
                                setState(71);
                                ((BrandContext) _localctx).vuln_flag = vuln_flag();
                                vulnFlagInit = ((BrandContext) _localctx).vuln_flag.vulnFlag;
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                    }
                    setState(78);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
            }
            _ctx.stop = _input.LT(-1);

            ((BrandContext) _localctx).brandObj = new Brand(codeInit, nameInit, verbInit, resFlagInit,
                    vulnFlagInit, multInit, oMultInit, powerInit);

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
        public List<Brand> brands;
        public BrandContext brand;

        public TerminalNode EOF() {
            return getToken(BrandParser.EOF, 0);
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
            if (listener instanceof BrandListener) ((BrandListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandListener) ((BrandListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandVisitor) return ((BrandVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_file);

        ((FileContext) _localctx).brands = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(82);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(79);
                            ((FileContext) _localctx).brand = brand();

                            _localctx.brands.add(((FileContext) _localctx).brand.brandObj);

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
            "\u0004\u0001\rY\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002" +
                    "\b\u0007\b\u0002\t\u0007\t\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0005\bK\b\b\n\b\f\bN\t\b\u0001\t\u0001\t\u0001\t\u0004\tS" +
                    "\b\t\u000b\t\f\tT\u0001\t\u0001\t\u0001\t\u0000\u0000\n\u0000\u0002\u0004" +
                    "\u0006\b\n\f\u000e\u0010\u0012\u0000\u0000U\u0000\u0014\u0001\u0000\u0000" +
                    "\u0000\u0002\u0018\u0001\u0000\u0000\u0000\u0004\u001c\u0001\u0000\u0000" +
                    "\u0000\u0006 \u0001\u0000\u0000\u0000\b$\u0001\u0000\u0000\u0000\n(\u0001" +
                    "\u0000\u0000\u0000\f,\u0001\u0000\u0000\u0000\u000e0\u0001\u0000\u0000" +
                    "\u0000\u00104\u0001\u0000\u0000\u0000\u0012R\u0001\u0000\u0000\u0000\u0014" +
                    "\u0015\u0005\u0003\u0000\u0000\u0015\u0016\u0005\f\u0000\u0000\u0016\u0017" +
                    "\u0006\u0000\uffff\uffff\u0000\u0017\u0001\u0001\u0000\u0000\u0000\u0018" +
                    "\u0019\u0005\u0004\u0000\u0000\u0019\u001a\u0005\r\u0000\u0000\u001a\u001b" +
                    "\u0006\u0001\uffff\uffff\u0000\u001b\u0003\u0001\u0000\u0000\u0000\u001c" +
                    "\u001d\u0005\u0005\u0000\u0000\u001d\u001e\u0005\u000b\u0000\u0000\u001e" +
                    "\u001f\u0006\u0002\uffff\uffff\u0000\u001f\u0005\u0001\u0000\u0000\u0000" +
                    " !\u0005\u0006\u0000\u0000!\"\u0005\u000b\u0000\u0000\"#\u0006\u0003\uffff" +
                    "\uffff\u0000#\u0007\u0001\u0000\u0000\u0000$%\u0005\u0007\u0000\u0000" +
                    "%&\u0005\u000b\u0000\u0000&\'\u0006\u0004\uffff\uffff\u0000\'\t\u0001" +
                    "\u0000\u0000\u0000()\u0005\b\u0000\u0000)*\u0005\r\u0000\u0000*+\u0006" +
                    "\u0005\uffff\uffff\u0000+\u000b\u0001\u0000\u0000\u0000,-\u0005\t\u0000" +
                    "\u0000-.\u0005\f\u0000\u0000./\u0006\u0006\uffff\uffff\u0000/\r\u0001" +
                    "\u0000\u0000\u000001\u0005\n\u0000\u000012\u0005\f\u0000\u000023\u0006" +
                    "\u0007\uffff\uffff\u00003\u000f\u0001\u0000\u0000\u000045\u0003\u0000" +
                    "\u0000\u000056\u0006\b\uffff\uffff\u000067\u0003\u0002\u0001\u00007L\u0006" +
                    "\b\uffff\uffff\u000089\u0003\u0004\u0002\u00009:\u0006\b\uffff\uffff\u0000" +
                    ":K\u0001\u0000\u0000\u0000;<\u0003\u0006\u0003\u0000<=\u0006\b\uffff\uffff" +
                    "\u0000=K\u0001\u0000\u0000\u0000>?\u0003\b\u0004\u0000?@\u0006\b\uffff" +
                    "\uffff\u0000@K\u0001\u0000\u0000\u0000AB\u0003\n\u0005\u0000BC\u0006\b" +
                    "\uffff\uffff\u0000CK\u0001\u0000\u0000\u0000DE\u0003\f\u0006\u0000EF\u0006" +
                    "\b\uffff\uffff\u0000FK\u0001\u0000\u0000\u0000GH\u0003\u000e\u0007\u0000" +
                    "HI\u0006\b\uffff\uffff\u0000IK\u0001\u0000\u0000\u0000J8\u0001\u0000\u0000" +
                    "\u0000J;\u0001\u0000\u0000\u0000J>\u0001\u0000\u0000\u0000JA\u0001\u0000" +
                    "\u0000\u0000JD\u0001\u0000\u0000\u0000JG\u0001\u0000\u0000\u0000KN\u0001" +
                    "\u0000\u0000\u0000LJ\u0001\u0000\u0000\u0000LM\u0001\u0000\u0000\u0000" +
                    "M\u0011\u0001\u0000\u0000\u0000NL\u0001\u0000\u0000\u0000OP\u0003\u0010" +
                    "\b\u0000PQ\u0006\t\uffff\uffff\u0000QS\u0001\u0000\u0000\u0000RO\u0001" +
                    "\u0000\u0000\u0000ST\u0001\u0000\u0000\u0000TR\u0001\u0000\u0000\u0000" +
                    "TU\u0001\u0000\u0000\u0000UV\u0001\u0000\u0000\u0000VW\u0005\u0000\u0000" +
                    "\u0001W\u0013\u0001\u0000\u0000\u0000\u0003JLT";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}