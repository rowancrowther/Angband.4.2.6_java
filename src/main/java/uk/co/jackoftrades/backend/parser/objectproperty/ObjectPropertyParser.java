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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/ObjectProperty.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.objectproperty;

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
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
import uk.co.jackoftrades.frontend.entries.UIEntry;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.ObjectProperty;
import uk.co.jackoftrades.middle.objects.ObjectPropertyTypeWrapper;
import uk.co.jackoftrades.middle.objects.enums.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ObjectPropertyParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, NAME = 3, TYPE = 4, SUBTYPE = 5, ID_TYPE = 6, CODE = 7, POWER = 8,
            MULT = 9, TYPE_MULT = 10, ADJECTIVE = 11, NEG_ADJECTIVE = 12, MSG = 13, BINDUI = 14,
            DESC = 15, INTEGER = 16, LCASE = 17, UCASE = 18, COLON = 19, LESSTHAN = 20, GREATERTHAN = 21;
    public static final int
            RULE_name = 0, RULE_type = 1, RULE_subType = 2, RULE_idType = 3, RULE_code = 4,
            RULE_power = 5, RULE_mult = 6, RULE_typeMult = 7, RULE_adjective = 8,
            RULE_negAdjective = 9, RULE_msg = 10, RULE_bindUI = 11, RULE_desc = 12,
            RULE_objProperty = 13, RULE_file = 14;

    private static String[] makeRuleNames() {
        return new String[]{
                "name", "type", "subType", "idType", "code", "power", "mult", "typeMult",
                "adjective", "negAdjective", "msg", "bindUI", "desc", "objProperty",
                "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'name:'", "'type:'", "'subtype:'", "'id-type:'", "'code:'",
                "'power:'", "'mult:'", "'type-mult:'", "'adjective:'", "'neg-adjective:'",
                null, "'bindui:'", null, null, null, null, "':'", "'<'", "'>'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "NAME", "TYPE", "SUBTYPE", "ID_TYPE", "CODE",
                "POWER", "MULT", "TYPE_MULT", "ADJECTIVE", "NEG_ADJECTIVE", "MSG", "BINDUI",
                "DESC", "INTEGER", "LCASE", "UCASE", "COLON", "LESSTHAN", "GREATERTHAN"
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
        return "ObjectProperty.g4";
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

    public ObjectPropertyParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class NameContext extends ParserRuleContext {
        public String nameStr;
        public Token LCASE;

        public TerminalNode NAME() {
            return getToken(ObjectPropertyParser.NAME, 0);
        }

        public TerminalNode LCASE() {
            return getToken(ObjectPropertyParser.LCASE, 0);
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
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectPropertyVisitor)
                return ((ObjectPropertyVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(30);
                match(NAME);
                setState(31);
                ((NameContext) _localctx).LCASE = match(LCASE);

                ((NameContext) _localctx).nameStr = ((NameContext) _localctx).LCASE.getText();
                logger.info("Name: " + _localctx.nameStr);

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
    public static class TypeContext extends ParserRuleContext {
        public ObjPropertyType typeObj;
        public Token LCASE;

        public TerminalNode TYPE() {
            return getToken(ObjectPropertyParser.TYPE, 0);
        }

        public TerminalNode LCASE() {
            return getToken(ObjectPropertyParser.LCASE, 0);
        }

        public TypeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_type;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).enterType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).exitType(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectPropertyVisitor)
                return ((ObjectPropertyVisitor<? extends T>) visitor).visitType(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TypeContext type() throws RecognitionException {
        TypeContext _localctx = new TypeContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_type);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(34);
                match(TYPE);
                setState(35);
                ((TypeContext) _localctx).LCASE = match(LCASE);

                String raw = ((TypeContext) _localctx).LCASE.getText();
                switch (raw) {
                    case "resistance":
                        raw = "resist";
                        break;

                    case "vulnerability":
                        raw = "vuln";
                        break;

                    case "immunity":
                        raw = "imm";
                        break;
                }

                ((TypeContext) _localctx).typeObj = ObjPropertyType.valueOf("OBJ_PROPERTY_" + raw.toUpperCase());

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
    public static class SubTypeContext extends ParserRuleContext {
        public String subTypeStr;
        public Token LCASE;

        public TerminalNode SUBTYPE() {
            return getToken(ObjectPropertyParser.SUBTYPE, 0);
        }

        public TerminalNode LCASE() {
            return getToken(ObjectPropertyParser.LCASE, 0);
        }

        public SubTypeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_subType;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).enterSubType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).exitSubType(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectPropertyVisitor)
                return ((ObjectPropertyVisitor<? extends T>) visitor).visitSubType(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SubTypeContext subType() throws RecognitionException {
        SubTypeContext _localctx = new SubTypeContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_subType);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(38);
                match(SUBTYPE);
                setState(39);
                ((SubTypeContext) _localctx).LCASE = match(LCASE);
                ((SubTypeContext) _localctx).subTypeStr = ((SubTypeContext) _localctx).LCASE.getText();
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
    public static class IdTypeContext extends ParserRuleContext {
        public String idTypeStr;
        public Token LCASE;

        public TerminalNode ID_TYPE() {
            return getToken(ObjectPropertyParser.ID_TYPE, 0);
        }

        public TerminalNode LCASE() {
            return getToken(ObjectPropertyParser.LCASE, 0);
        }

        public IdTypeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_idType;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).enterIdType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).exitIdType(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectPropertyVisitor)
                return ((ObjectPropertyVisitor<? extends T>) visitor).visitIdType(this);
            else return visitor.visitChildren(this);
        }
    }

    public final IdTypeContext idType() throws RecognitionException {
        IdTypeContext _localctx = new IdTypeContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_idType);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(42);
                match(ID_TYPE);
                setState(43);
                ((IdTypeContext) _localctx).LCASE = match(LCASE);
                ((IdTypeContext) _localctx).idTypeStr = ((IdTypeContext) _localctx).LCASE.getText();
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
        public Token UCASE;

        public TerminalNode CODE() {
            return getToken(ObjectPropertyParser.CODE, 0);
        }

        public TerminalNode UCASE() {
            return getToken(ObjectPropertyParser.UCASE, 0);
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
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).enterCode(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).exitCode(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectPropertyVisitor)
                return ((ObjectPropertyVisitor<? extends T>) visitor).visitCode(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CodeContext code() throws RecognitionException {
        CodeContext _localctx = new CodeContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_code);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(46);
                match(CODE);
                setState(47);
                ((CodeContext) _localctx).UCASE = match(UCASE);
                ((CodeContext) _localctx).codeStr = ((CodeContext) _localctx).UCASE.getText();
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
        public Token INTEGER;

        public TerminalNode POWER() {
            return getToken(ObjectPropertyParser.POWER, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(ObjectPropertyParser.INTEGER, 0);
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
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).enterPower(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).exitPower(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectPropertyVisitor)
                return ((ObjectPropertyVisitor<? extends T>) visitor).visitPower(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PowerContext power() throws RecognitionException {
        PowerContext _localctx = new PowerContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_power);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(50);
                match(POWER);
                setState(51);
                ((PowerContext) _localctx).INTEGER = match(INTEGER);
                ((PowerContext) _localctx).powerInt = Integer.parseInt(((PowerContext) _localctx).INTEGER.getText());
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
    public static class MultContext extends ParserRuleContext {
        public int multInt;
        public Token INTEGER;

        public TerminalNode MULT() {
            return getToken(ObjectPropertyParser.MULT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(ObjectPropertyParser.INTEGER, 0);
        }

        public MultContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_mult;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).enterMult(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).exitMult(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectPropertyVisitor)
                return ((ObjectPropertyVisitor<? extends T>) visitor).visitMult(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MultContext mult() throws RecognitionException {
        MultContext _localctx = new MultContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_mult);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(54);
                match(MULT);
                setState(55);
                ((MultContext) _localctx).INTEGER = match(INTEGER);
                ((MultContext) _localctx).multInt = Integer.parseInt(((MultContext) _localctx).INTEGER.getText());
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
    public static class TypeMultContext extends ParserRuleContext {
        public TValue tVal;
        public int value;
        public Token ty;
        public Token val;

        public TerminalNode TYPE_MULT() {
            return getToken(ObjectPropertyParser.TYPE_MULT, 0);
        }

        public TerminalNode COLON() {
            return getToken(ObjectPropertyParser.COLON, 0);
        }

        public TerminalNode LCASE() {
            return getToken(ObjectPropertyParser.LCASE, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(ObjectPropertyParser.INTEGER, 0);
        }

        public TypeMultContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_typeMult;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).enterTypeMult(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).exitTypeMult(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectPropertyVisitor)
                return ((ObjectPropertyVisitor<? extends T>) visitor).visitTypeMult(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TypeMultContext typeMult() throws RecognitionException {
        TypeMultContext _localctx = new TypeMultContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_typeMult);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(58);
                match(TYPE_MULT);
                setState(59);
                ((TypeMultContext) _localctx).ty = match(LCASE);
                setState(60);
                match(COLON);
                setState(61);
                ((TypeMultContext) _localctx).val = match(INTEGER);

                String rawType = "TV_" + ((TypeMultContext) _localctx).ty.getText().toUpperCase().replace(' ', '_').replace("ARMOUR", "ARMOR");
                ((TypeMultContext) _localctx).tVal = TValue.valueOf(rawType);
                ((TypeMultContext) _localctx).value = Integer.parseInt(((TypeMultContext) _localctx).val.getText());

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
    public static class AdjectiveContext extends ParserRuleContext {
        public String adjStr;
        public Token LCASE;

        public TerminalNode ADJECTIVE() {
            return getToken(ObjectPropertyParser.ADJECTIVE, 0);
        }

        public TerminalNode LCASE() {
            return getToken(ObjectPropertyParser.LCASE, 0);
        }

        public AdjectiveContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_adjective;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).enterAdjective(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).exitAdjective(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectPropertyVisitor)
                return ((ObjectPropertyVisitor<? extends T>) visitor).visitAdjective(this);
            else return visitor.visitChildren(this);
        }
    }

    public final AdjectiveContext adjective() throws RecognitionException {
        AdjectiveContext _localctx = new AdjectiveContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_adjective);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(64);
                match(ADJECTIVE);
                setState(65);
                ((AdjectiveContext) _localctx).LCASE = match(LCASE);
                ((AdjectiveContext) _localctx).adjStr = ((AdjectiveContext) _localctx).LCASE.getText();
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
    public static class NegAdjectiveContext extends ParserRuleContext {
        public String negAdjStr;
        public Token LCASE;

        public TerminalNode NEG_ADJECTIVE() {
            return getToken(ObjectPropertyParser.NEG_ADJECTIVE, 0);
        }

        public TerminalNode LCASE() {
            return getToken(ObjectPropertyParser.LCASE, 0);
        }

        public NegAdjectiveContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_negAdjective;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).enterNegAdjective(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).exitNegAdjective(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectPropertyVisitor)
                return ((ObjectPropertyVisitor<? extends T>) visitor).visitNegAdjective(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NegAdjectiveContext negAdjective() throws RecognitionException {
        NegAdjectiveContext _localctx = new NegAdjectiveContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_negAdjective);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(68);
                match(NEG_ADJECTIVE);
                setState(69);
                ((NegAdjectiveContext) _localctx).LCASE = match(LCASE);
                ((NegAdjectiveContext) _localctx).negAdjStr = ((NegAdjectiveContext) _localctx).LCASE.getText();
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
        public Token MSG;

        public TerminalNode MSG() {
            return getToken(ObjectPropertyParser.MSG, 0);
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
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).enterMsg(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).exitMsg(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectPropertyVisitor)
                return ((ObjectPropertyVisitor<? extends T>) visitor).visitMsg(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MsgContext msg() throws RecognitionException {
        MsgContext _localctx = new MsgContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_msg);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(72);
                ((MsgContext) _localctx).MSG = match(MSG);

                String raw = ((MsgContext) _localctx).MSG.getText();
                ((MsgContext) _localctx).msgStr = raw.substring(4);

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
    public static class BindUIContext extends ParserRuleContext {
        public UIEntry entryObj;
        public ObjPropertyType propType;
        public int parm1;
        public int parm2;
        public Token entry;
        public Token flg;
        public Token value;
        public Token index;

        public TerminalNode BINDUI() {
            return getToken(ObjectPropertyParser.BINDUI, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(ObjectPropertyParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(ObjectPropertyParser.COLON, i);
        }

        public TerminalNode LCASE() {
            return getToken(ObjectPropertyParser.LCASE, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(ObjectPropertyParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(ObjectPropertyParser.INTEGER, i);
        }

        public TerminalNode LESSTHAN() {
            return getToken(ObjectPropertyParser.LESSTHAN, 0);
        }

        public TerminalNode GREATERTHAN() {
            return getToken(ObjectPropertyParser.GREATERTHAN, 0);
        }

        public TerminalNode UCASE() {
            return getToken(ObjectPropertyParser.UCASE, 0);
        }

        public BindUIContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_bindUI;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).enterBindUI(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).exitBindUI(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectPropertyVisitor)
                return ((ObjectPropertyVisitor<? extends T>) visitor).visitBindUI(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BindUIContext bindUI() throws RecognitionException {
        BindUIContext _localctx = new BindUIContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_bindUI);

        ((BindUIContext) _localctx).parm2 = 0;
        ((BindUIContext) _localctx).propType = ObjPropertyType.OBJ_PROPERTY_NONE;

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(75);
                match(BINDUI);
                setState(76);
                ((BindUIContext) _localctx).entry = match(LCASE);

                ((BindUIContext) _localctx).entryObj = GameConstants.getUIEntry(((BindUIContext) _localctx).entry.getText());

                setState(82);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == LESSTHAN) {
                    {
                        setState(78);
                        match(LESSTHAN);
                        setState(79);
                        ((BindUIContext) _localctx).flg = match(UCASE);
                        setState(80);
                        match(GREATERTHAN);

                        String typeStr = ((BindUIContext) _localctx).flg.getText();
                        String testFlag = "STAT_" + typeStr.toUpperCase();
                        boolean isStat = false;
                        try {
                            Stats st = Stats.valueOf(testFlag);
                            isStat = true;
                        } catch (Exception e) {
                            // Do nothing
                        }
                        if (isStat) {
                            ((BindUIContext) _localctx).propType = ObjPropertyType.OBJ_PROPERTY_STAT;
                        } else {
                            ((BindUIContext) _localctx).propType = ObjPropertyType.OBJ_PROPERTY_RESIST;
                        }


                    }
                }

                setState(84);
                match(COLON);
                setState(85);
                ((BindUIContext) _localctx).value = match(INTEGER);

                ((BindUIContext) _localctx).parm1 = Integer.parseInt(((BindUIContext) _localctx).value.getText());

                setState(90);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COLON) {
                    {
                        setState(87);
                        match(COLON);
                        setState(88);
                        ((BindUIContext) _localctx).index = match(INTEGER);

                        ((BindUIContext) _localctx).parm2 = Integer.parseInt(((BindUIContext) _localctx).index.getText());

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
    public static class DescContext extends ParserRuleContext {
        public String descStr;
        public Token DESC;

        public TerminalNode DESC() {
            return getToken(ObjectPropertyParser.DESC, 0);
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
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).enterDesc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).exitDesc(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectPropertyVisitor)
                return ((ObjectPropertyVisitor<? extends T>) visitor).visitDesc(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DescContext desc() throws RecognitionException {
        DescContext _localctx = new DescContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_desc);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(92);
                ((DescContext) _localctx).DESC = match(DESC);
                ((DescContext) _localctx).descStr = ((DescContext) _localctx).DESC.getText().substring(5);
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
    public static class ObjPropertyContext extends ParserRuleContext {
        public ObjectProperty property;
        public NameContext name;
        public TypeContext type;
        public CodeContext code;
        public SubTypeContext subType;
        public IdTypeContext idType;
        public PowerContext power;
        public MultContext mult;
        public TypeMultContext typeMult;
        public AdjectiveContext adjective;
        public NegAdjectiveContext negAdjective;
        public MsgContext msg;
        public BindUIContext bindUI;
        public DescContext desc;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public List<TypeContext> type() {
            return getRuleContexts(TypeContext.class);
        }

        public TypeContext type(int i) {
            return getRuleContext(TypeContext.class, i);
        }

        public List<CodeContext> code() {
            return getRuleContexts(CodeContext.class);
        }

        public CodeContext code(int i) {
            return getRuleContext(CodeContext.class, i);
        }

        public List<SubTypeContext> subType() {
            return getRuleContexts(SubTypeContext.class);
        }

        public SubTypeContext subType(int i) {
            return getRuleContext(SubTypeContext.class, i);
        }

        public List<IdTypeContext> idType() {
            return getRuleContexts(IdTypeContext.class);
        }

        public IdTypeContext idType(int i) {
            return getRuleContext(IdTypeContext.class, i);
        }

        public List<PowerContext> power() {
            return getRuleContexts(PowerContext.class);
        }

        public PowerContext power(int i) {
            return getRuleContext(PowerContext.class, i);
        }

        public List<MultContext> mult() {
            return getRuleContexts(MultContext.class);
        }

        public MultContext mult(int i) {
            return getRuleContext(MultContext.class, i);
        }

        public List<TypeMultContext> typeMult() {
            return getRuleContexts(TypeMultContext.class);
        }

        public TypeMultContext typeMult(int i) {
            return getRuleContext(TypeMultContext.class, i);
        }

        public List<AdjectiveContext> adjective() {
            return getRuleContexts(AdjectiveContext.class);
        }

        public AdjectiveContext adjective(int i) {
            return getRuleContext(AdjectiveContext.class, i);
        }

        public List<NegAdjectiveContext> negAdjective() {
            return getRuleContexts(NegAdjectiveContext.class);
        }

        public NegAdjectiveContext negAdjective(int i) {
            return getRuleContext(NegAdjectiveContext.class, i);
        }

        public List<MsgContext> msg() {
            return getRuleContexts(MsgContext.class);
        }

        public MsgContext msg(int i) {
            return getRuleContext(MsgContext.class, i);
        }

        public BindUIContext bindUI() {
            return getRuleContext(BindUIContext.class, 0);
        }

        public DescContext desc() {
            return getRuleContext(DescContext.class, 0);
        }

        public ObjPropertyContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_objProperty;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).enterObjProperty(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).exitObjProperty(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectPropertyVisitor)
                return ((ObjectPropertyVisitor<? extends T>) visitor).visitObjProperty(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ObjPropertyContext objProperty() throws RecognitionException {
        ObjPropertyContext _localctx = new ObjPropertyContext(_ctx, getState());
        enterRule(_localctx, 26, RULE_objProperty);

        ObjPropertyType typeInit = ObjPropertyType.OBJ_PROPERTY_NONE;
        String subTypeInit = "";
        String idTypeInit = "";
        String codeInit = "";
        ObjectPropertyTypeWrapper wrapperObject = null;
        int powerInit = 0;
        int multInit = 0;
        Map<TValue, Integer> typeMultInit = new HashMap<>();
        String nameInit = "";
        String adjInit = "";
        String negAdjInit = "";
        String message = "";
        String description = "";
        Map<UIEntry, ObjectPropertyTypeWrapper> uiEntriesMap = new HashMap<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(95);
                ((ObjPropertyContext) _localctx).name = name();
                nameInit = ((ObjPropertyContext) _localctx).name.nameStr;
                setState(127);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        setState(127);
                        _errHandler.sync(this);
                        switch (_input.LA(1)) {
                            case TYPE: {
                                setState(97);
                                ((ObjPropertyContext) _localctx).type = type();
                                typeInit = ((ObjPropertyContext) _localctx).type.typeObj;
                            }
                            break;
                            case CODE: {
                                setState(100);
                                ((ObjPropertyContext) _localctx).code = code();
                                codeInit = ((ObjPropertyContext) _localctx).code.codeStr;
                            }
                            break;
                            case SUBTYPE: {
                                setState(103);
                                ((ObjPropertyContext) _localctx).subType = subType();
                                subTypeInit = ((ObjPropertyContext) _localctx).subType.subTypeStr;
                            }
                            break;
                            case ID_TYPE: {
                                setState(106);
                                ((ObjPropertyContext) _localctx).idType = idType();
                                idTypeInit = ((ObjPropertyContext) _localctx).idType.idTypeStr;
                            }
                            break;
                            case POWER: {
                                setState(109);
                                ((ObjPropertyContext) _localctx).power = power();
                                powerInit = ((ObjPropertyContext) _localctx).power.powerInt;
                            }
                            break;
                            case MULT: {
                                setState(112);
                                ((ObjPropertyContext) _localctx).mult = mult();
                                multInit = ((ObjPropertyContext) _localctx).mult.multInt;
                            }
                            break;
                            case TYPE_MULT: {
                                setState(115);
                                ((ObjPropertyContext) _localctx).typeMult = typeMult();

                                typeMultInit.put(((ObjPropertyContext) _localctx).typeMult.tVal, ((ObjPropertyContext) _localctx).typeMult.value);

                            }
                            break;
                            case ADJECTIVE: {
                                setState(118);
                                ((ObjPropertyContext) _localctx).adjective = adjective();
                                adjInit = ((ObjPropertyContext) _localctx).adjective.adjStr;
                            }
                            break;
                            case NEG_ADJECTIVE: {
                                setState(121);
                                ((ObjPropertyContext) _localctx).negAdjective = negAdjective();
                                negAdjInit = ((ObjPropertyContext) _localctx).negAdjective.negAdjStr;
                            }
                            break;
                            case MSG: {
                                setState(124);
                                ((ObjPropertyContext) _localctx).msg = msg();
                                message = message + ((ObjPropertyContext) _localctx).msg.msgStr;
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                    }
                    setState(129);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 16368L) != 0));
                setState(134);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == BINDUI) {
                    {
                        setState(131);
                        ((ObjPropertyContext) _localctx).bindUI = bindUI();

                        UIEntry entry = ((ObjPropertyContext) _localctx).bindUI.entryObj;

                        int p1 = ((ObjPropertyContext) _localctx).bindUI.parm1;
                        int p2 = ((ObjPropertyContext) _localctx).bindUI.parm2;

                        switch (typeInit) {
                            case OBJ_PROPERTY_STAT:
                            case OBJ_PROPERTY_MOD:
                                ObjectModifier modifier = ObjectModifier.valueOf("OM_" + codeInit);
                                wrapperObject = new ObjectPropertyTypeWrapper(typeInit, modifier);
                                break;

                            case OBJ_PROPERTY_FLAG:
                                ObjectFlag flag = ObjectFlag.valueOf("OF_" + codeInit);
                                wrapperObject = new ObjectPropertyTypeWrapper(typeInit, flag);
                                break;

                            case OBJ_PROPERTY_IGNORE:
                            case OBJ_PROPERTY_RESIST:
                            case OBJ_PROPERTY_VULN:
                            case OBJ_PROPERTY_IMM:
                                ElementEnum element = ElementEnum.valueOf("ELEM_" + codeInit);
                                wrapperObject = new ObjectPropertyTypeWrapper(typeInit, element);
                                break;

                            default:
                                String msg = "Invalid object property type found while parsing bindui.\n"
                                        + "Type: " + typeInit;
                                throw new InvalidTokenFoundDuringParse(msg);
                        }

                        wrapperObject.setValues(p1, p2);
                        uiEntriesMap.put(entry, wrapperObject);

                    }
                }

                setState(139);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == DESC) {
                    {
                        setState(136);
                        ((ObjPropertyContext) _localctx).desc = desc();
                        description = description + ((ObjPropertyContext) _localctx).desc.descStr;
                    }
                }

            }
            _ctx.stop = _input.LT(-1);

            ((ObjPropertyContext) _localctx).property = new ObjectProperty(typeInit, subTypeInit, idTypeInit, wrapperObject, powerInit,
                    multInit, typeMultInit, nameInit, adjInit, negAdjInit,
                    message, description, uiEntriesMap);

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
        public List<ObjectProperty> properties;
        public ObjPropertyContext objProperty;

        public TerminalNode EOF() {
            return getToken(ObjectPropertyParser.EOF, 0);
        }

        public List<ObjPropertyContext> objProperty() {
            return getRuleContexts(ObjPropertyContext.class);
        }

        public ObjPropertyContext objProperty(int i) {
            return getRuleContext(ObjPropertyContext.class, i);
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
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectPropertyListener) ((ObjectPropertyListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectPropertyVisitor)
                return ((ObjectPropertyVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 28, RULE_file);

        ((FileContext) _localctx).properties = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(144);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(141);
                            ((FileContext) _localctx).objProperty = objProperty();

                            _localctx.properties.add(((FileContext) _localctx).objProperty.property);

                        }
                    }
                    setState(146);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(148);
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
            "\u0004\u0001\u0015\u0097\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001" +
                    "\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004" +
                    "\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007" +
                    "\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b" +
                    "\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t" +
                    "\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b" +
                    "S\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0003\u000b[\b\u000b\u0001\f\u0001\f\u0001\f\u0001\r\u0001" +
                    "\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001" +
                    "\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001" +
                    "\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001" +
                    "\r\u0001\r\u0001\r\u0001\r\u0004\r\u0080\b\r\u000b\r\f\r\u0081\u0001\r" +
                    "\u0001\r\u0001\r\u0003\r\u0087\b\r\u0001\r\u0001\r\u0001\r\u0003\r\u008c" +
                    "\b\r\u0001\u000e\u0001\u000e\u0001\u000e\u0004\u000e\u0091\b\u000e\u000b" +
                    "\u000e\f\u000e\u0092\u0001\u000e\u0001\u000e\u0001\u000e\u0000\u0000\u000f" +
                    "\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a" +
                    "\u001c\u0000\u0000\u0096\u0000\u001e\u0001\u0000\u0000\u0000\u0002\"\u0001" +
                    "\u0000\u0000\u0000\u0004&\u0001\u0000\u0000\u0000\u0006*\u0001\u0000\u0000" +
                    "\u0000\b.\u0001\u0000\u0000\u0000\n2\u0001\u0000\u0000\u0000\f6\u0001" +
                    "\u0000\u0000\u0000\u000e:\u0001\u0000\u0000\u0000\u0010@\u0001\u0000\u0000" +
                    "\u0000\u0012D\u0001\u0000\u0000\u0000\u0014H\u0001\u0000\u0000\u0000\u0016" +
                    "K\u0001\u0000\u0000\u0000\u0018\\\u0001\u0000\u0000\u0000\u001a_\u0001" +
                    "\u0000\u0000\u0000\u001c\u0090\u0001\u0000\u0000\u0000\u001e\u001f\u0005" +
                    "\u0003\u0000\u0000\u001f \u0005\u0011\u0000\u0000 !\u0006\u0000\uffff" +
                    "\uffff\u0000!\u0001\u0001\u0000\u0000\u0000\"#\u0005\u0004\u0000\u0000" +
                    "#$\u0005\u0011\u0000\u0000$%\u0006\u0001\uffff\uffff\u0000%\u0003\u0001" +
                    "\u0000\u0000\u0000&\'\u0005\u0005\u0000\u0000\'(\u0005\u0011\u0000\u0000" +
                    "()\u0006\u0002\uffff\uffff\u0000)\u0005\u0001\u0000\u0000\u0000*+\u0005" +
                    "\u0006\u0000\u0000+,\u0005\u0011\u0000\u0000,-\u0006\u0003\uffff\uffff" +
                    "\u0000-\u0007\u0001\u0000\u0000\u0000./\u0005\u0007\u0000\u0000/0\u0005" +
                    "\u0012\u0000\u000001\u0006\u0004\uffff\uffff\u00001\t\u0001\u0000\u0000" +
                    "\u000023\u0005\b\u0000\u000034\u0005\u0010\u0000\u000045\u0006\u0005\uffff" +
                    "\uffff\u00005\u000b\u0001\u0000\u0000\u000067\u0005\t\u0000\u000078\u0005" +
                    "\u0010\u0000\u000089\u0006\u0006\uffff\uffff\u00009\r\u0001\u0000\u0000" +
                    "\u0000:;\u0005\n\u0000\u0000;<\u0005\u0011\u0000\u0000<=\u0005\u0013\u0000" +
                    "\u0000=>\u0005\u0010\u0000\u0000>?\u0006\u0007\uffff\uffff\u0000?\u000f" +
                    "\u0001\u0000\u0000\u0000@A\u0005\u000b\u0000\u0000AB\u0005\u0011\u0000" +
                    "\u0000BC\u0006\b\uffff\uffff\u0000C\u0011\u0001\u0000\u0000\u0000DE\u0005" +
                    "\f\u0000\u0000EF\u0005\u0011\u0000\u0000FG\u0006\t\uffff\uffff\u0000G" +
                    "\u0013\u0001\u0000\u0000\u0000HI\u0005\r\u0000\u0000IJ\u0006\n\uffff\uffff" +
                    "\u0000J\u0015\u0001\u0000\u0000\u0000KL\u0005\u000e\u0000\u0000LM\u0005" +
                    "\u0011\u0000\u0000MR\u0006\u000b\uffff\uffff\u0000NO\u0005\u0014\u0000" +
                    "\u0000OP\u0005\u0012\u0000\u0000PQ\u0005\u0015\u0000\u0000QS\u0006\u000b" +
                    "\uffff\uffff\u0000RN\u0001\u0000\u0000\u0000RS\u0001\u0000\u0000\u0000" +
                    "ST\u0001\u0000\u0000\u0000TU\u0005\u0013\u0000\u0000UV\u0005\u0010\u0000" +
                    "\u0000VZ\u0006\u000b\uffff\uffff\u0000WX\u0005\u0013\u0000\u0000XY\u0005" +
                    "\u0010\u0000\u0000Y[\u0006\u000b\uffff\uffff\u0000ZW\u0001\u0000\u0000" +
                    "\u0000Z[\u0001\u0000\u0000\u0000[\u0017\u0001\u0000\u0000\u0000\\]\u0005" +
                    "\u000f\u0000\u0000]^\u0006\f\uffff\uffff\u0000^\u0019\u0001\u0000\u0000" +
                    "\u0000_`\u0003\u0000\u0000\u0000`\u007f\u0006\r\uffff\uffff\u0000ab\u0003" +
                    "\u0002\u0001\u0000bc\u0006\r\uffff\uffff\u0000c\u0080\u0001\u0000\u0000" +
                    "\u0000de\u0003\b\u0004\u0000ef\u0006\r\uffff\uffff\u0000f\u0080\u0001" +
                    "\u0000\u0000\u0000gh\u0003\u0004\u0002\u0000hi\u0006\r\uffff\uffff\u0000" +
                    "i\u0080\u0001\u0000\u0000\u0000jk\u0003\u0006\u0003\u0000kl\u0006\r\uffff" +
                    "\uffff\u0000l\u0080\u0001\u0000\u0000\u0000mn\u0003\n\u0005\u0000no\u0006" +
                    "\r\uffff\uffff\u0000o\u0080\u0001\u0000\u0000\u0000pq\u0003\f\u0006\u0000" +
                    "qr\u0006\r\uffff\uffff\u0000r\u0080\u0001\u0000\u0000\u0000st\u0003\u000e" +
                    "\u0007\u0000tu\u0006\r\uffff\uffff\u0000u\u0080\u0001\u0000\u0000\u0000" +
                    "vw\u0003\u0010\b\u0000wx\u0006\r\uffff\uffff\u0000x\u0080\u0001\u0000" +
                    "\u0000\u0000yz\u0003\u0012\t\u0000z{\u0006\r\uffff\uffff\u0000{\u0080" +
                    "\u0001\u0000\u0000\u0000|}\u0003\u0014\n\u0000}~\u0006\r\uffff\uffff\u0000" +
                    "~\u0080\u0001\u0000\u0000\u0000\u007fa\u0001\u0000\u0000\u0000\u007fd" +
                    "\u0001\u0000\u0000\u0000\u007fg\u0001\u0000\u0000\u0000\u007fj\u0001\u0000" +
                    "\u0000\u0000\u007fm\u0001\u0000\u0000\u0000\u007fp\u0001\u0000\u0000\u0000" +
                    "\u007fs\u0001\u0000\u0000\u0000\u007fv\u0001\u0000\u0000\u0000\u007fy" +
                    "\u0001\u0000\u0000\u0000\u007f|\u0001\u0000\u0000\u0000\u0080\u0081\u0001" +
                    "\u0000\u0000\u0000\u0081\u007f\u0001\u0000\u0000\u0000\u0081\u0082\u0001" +
                    "\u0000\u0000\u0000\u0082\u0086\u0001\u0000\u0000\u0000\u0083\u0084\u0003" +
                    "\u0016\u000b\u0000\u0084\u0085\u0006\r\uffff\uffff\u0000\u0085\u0087\u0001" +
                    "\u0000\u0000\u0000\u0086\u0083\u0001\u0000\u0000\u0000\u0086\u0087\u0001" +
                    "\u0000\u0000\u0000\u0087\u008b\u0001\u0000\u0000\u0000\u0088\u0089\u0003" +
                    "\u0018\f\u0000\u0089\u008a\u0006\r\uffff\uffff\u0000\u008a\u008c\u0001" +
                    "\u0000\u0000\u0000\u008b\u0088\u0001\u0000\u0000\u0000\u008b\u008c\u0001" +
                    "\u0000\u0000\u0000\u008c\u001b\u0001\u0000\u0000\u0000\u008d\u008e\u0003" +
                    "\u001a\r\u0000\u008e\u008f\u0006\u000e\uffff\uffff\u0000\u008f\u0091\u0001" +
                    "\u0000\u0000\u0000\u0090\u008d\u0001\u0000\u0000\u0000\u0091\u0092\u0001" +
                    "\u0000\u0000\u0000\u0092\u0090\u0001\u0000\u0000\u0000\u0092\u0093\u0001" +
                    "\u0000\u0000\u0000\u0093\u0094\u0001\u0000\u0000\u0000\u0094\u0095\u0005" +
                    "\u0000\u0000\u0001\u0095\u001d\u0001\u0000\u0000\u0000\u0007RZ\u007f\u0081" +
                    "\u0086\u008b\u0092";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}