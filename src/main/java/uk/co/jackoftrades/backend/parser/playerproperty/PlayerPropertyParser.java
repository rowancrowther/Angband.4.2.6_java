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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/PlayerProperty.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.playerproperty;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.frontend.entries.UIEntry;
import uk.co.jackoftrades.middle.combat.enums.Element;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.PlayerProperty;
import uk.co.jackoftrades.middle.player.PlayerProperty.PlayerPropertyType;
import uk.co.jackoftrades.middle.player.PlayerProperty.PlayerPropertyValue;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class PlayerPropertyParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, TYPE = 3, CODE = 4, BINDUI = 5, NAME = 6, DESC = 7, VALUE = 8, TYPE_OPTIONS = 9,
            INTEGER = 10, TEXT = 11, TAG = 12, BINDUIVAL = 13;
    public static final int
            RULE_type = 0, RULE_code = 1, RULE_bindui = 2, RULE_name = 3, RULE_desc = 4,
            RULE_value = 5, RULE_property = 6, RULE_file = 7;

    private static String[] makeRuleNames() {
        return new String[]{
                "type", "code", "bindui", "name", "desc", "value", "property", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'type:'", "'code:'", "'bindui:'", "'name:'", "'desc:'",
                "'value:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "TYPE", "CODE", "BINDUI", "NAME", "DESC", "VALUE",
                "TYPE_OPTIONS", "INTEGER", "TEXT", "TAG", "BINDUIVAL"
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
        return "PlayerProperty.g4";
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

    String typeOption;

    public PlayerPropertyParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class TypeContext extends ParserRuleContext {
        public Token TYPE_OPTIONS;

        public TerminalNode TYPE() {
            return getToken(PlayerPropertyParser.TYPE, 0);
        }

        public TerminalNode TYPE_OPTIONS() {
            return getToken(PlayerPropertyParser.TYPE_OPTIONS, 0);
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
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).enterType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).exitType(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyVisitor)
                return ((PlayerPropertyVisitor<? extends T>) visitor).visitType(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TypeContext type() throws RecognitionException {
        TypeContext _localctx = new TypeContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_type);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(16);
                match(TYPE);
                setState(17);
                ((TypeContext) _localctx).TYPE_OPTIONS = match(TYPE_OPTIONS);
                typeOption = ((TypeContext) _localctx).TYPE_OPTIONS.getText();
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
        public ObjectFlag oFlag;
        public PlayerFlag pFlag;
        public Token TEXT;

        public TerminalNode CODE() {
            return getToken(PlayerPropertyParser.CODE, 0);
        }

        public TerminalNode TEXT() {
            return getToken(PlayerPropertyParser.TEXT, 0);
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
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).enterCode(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).exitCode(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyVisitor)
                return ((PlayerPropertyVisitor<? extends T>) visitor).visitCode(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CodeContext code() throws RecognitionException {
        CodeContext _localctx = new CodeContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_code);

        ((CodeContext) _localctx).oFlag = ObjectFlag.OF_NONE;
        ((CodeContext) _localctx).pFlag = PlayerFlag.PF_NONE;

        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(20);
                match(CODE);
                setState(21);
                ((CodeContext) _localctx).TEXT = match(TEXT);

                String raw = ((CodeContext) _localctx).TEXT.getText();
                if (typeOption.equals("player"))
                    ((CodeContext) _localctx).pFlag = PlayerFlag.valueOf("PF_" + raw);
                else if (typeOption.equals("object"))
                    ((CodeContext) _localctx).oFlag = ObjectFlag.valueOf("OF_" + raw);

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
    public static class BinduiContext extends ParserRuleContext {
        public UIEntry entry;
        public Element elemEnum;
        public Stats statEnum;
        public String bindUIVal;
        public Token TEXT;
        public Token TAG;
        public Token BINDUIVAL;

        public TerminalNode BINDUI() {
            return getToken(PlayerPropertyParser.BINDUI, 0);
        }

        public TerminalNode TEXT() {
            return getToken(PlayerPropertyParser.TEXT, 0);
        }

        public TerminalNode BINDUIVAL() {
            return getToken(PlayerPropertyParser.BINDUIVAL, 0);
        }

        public TerminalNode TAG() {
            return getToken(PlayerPropertyParser.TAG, 0);
        }

        public BinduiContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_bindui;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).enterBindui(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).exitBindui(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyVisitor)
                return ((PlayerPropertyVisitor<? extends T>) visitor).visitBindui(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BinduiContext bindui() throws RecognitionException {
        BinduiContext _localctx = new BinduiContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_bindui);

        ((BinduiContext) _localctx).elemEnum = Element.ELEM_NONE;
        ((BinduiContext) _localctx).statEnum = Stats.STAT_NONE;

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(24);
                match(BINDUI);
                setState(25);
                ((BinduiContext) _localctx).TEXT = match(TEXT);

                ((BinduiContext) _localctx).entry = GameConstants.getUIEntry(((BinduiContext) _localctx).TEXT.getText());

                setState(29);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == TAG) {
                    {
                        setState(27);
                        ((BinduiContext) _localctx).TAG = match(TAG);

                        String raw = ((BinduiContext) _localctx).TAG.getText();
                        String flag;
                        if (raw.length() < 3) {
                            flag = "ERROR";
                        } else {
                            flag = raw.substring(1, raw.length() - 1);
                            if (typeOption.equals("player"))
                                ((BinduiContext) _localctx).elemEnum = Element.valueOf("ELEM_" + flag);
                            else if (typeOption.equals("object"))
                                ((BinduiContext) _localctx).statEnum = Stats.valueOf("STAT_" + flag);
                        }

                    }
                }

                setState(31);
                ((BinduiContext) _localctx).BINDUIVAL = match(BINDUIVAL);

                ((BinduiContext) _localctx).bindUIVal = ((BinduiContext) _localctx).BINDUIVAL.getText();

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
            return getToken(PlayerPropertyParser.NAME, 0);
        }

        public TerminalNode TEXT() {
            return getToken(PlayerPropertyParser.TEXT, 0);
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
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyVisitor)
                return ((PlayerPropertyVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(34);
                match(NAME);
                setState(35);
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
    public static class DescContext extends ParserRuleContext {
        public String descStr;
        public Token TEXT;

        public TerminalNode DESC() {
            return getToken(PlayerPropertyParser.DESC, 0);
        }

        public TerminalNode TEXT() {
            return getToken(PlayerPropertyParser.TEXT, 0);
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
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).enterDesc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).exitDesc(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyVisitor)
                return ((PlayerPropertyVisitor<? extends T>) visitor).visitDesc(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DescContext desc() throws RecognitionException {
        DescContext _localctx = new DescContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_desc);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(38);
                match(DESC);
                setState(39);
                ((DescContext) _localctx).TEXT = match(TEXT);
                ((DescContext) _localctx).descStr = ((DescContext) _localctx).TEXT.getText();
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
    public static class ValueContext extends ParserRuleContext {
        public int valueInt;
        public Token INTEGER;

        public TerminalNode VALUE() {
            return getToken(PlayerPropertyParser.VALUE, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerPropertyParser.INTEGER, 0);
        }

        public ValueContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_value;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).enterValue(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).exitValue(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyVisitor)
                return ((PlayerPropertyVisitor<? extends T>) visitor).visitValue(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ValueContext value() throws RecognitionException {
        ValueContext _localctx = new ValueContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_value);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(42);
                match(VALUE);
                setState(43);
                ((ValueContext) _localctx).INTEGER = match(INTEGER);

                ((ValueContext) _localctx).valueInt = Integer.parseInt(((ValueContext) _localctx).INTEGER.getText());

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
    public static class PropertyContext extends ParserRuleContext {
        public PlayerProperty playerProp;
        public CodeContext code;
        public BinduiContext bindui;
        public NameContext name;
        public DescContext desc;
        public ValueContext value;

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public CodeContext code() {
            return getRuleContext(CodeContext.class, 0);
        }

        public BinduiContext bindui() {
            return getRuleContext(BinduiContext.class, 0);
        }

        public List<DescContext> desc() {
            return getRuleContexts(DescContext.class);
        }

        public DescContext desc(int i) {
            return getRuleContext(DescContext.class, i);
        }

        public ValueContext value() {
            return getRuleContext(ValueContext.class, 0);
        }

        public PropertyContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_property;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).enterProperty(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).exitProperty(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyVisitor)
                return ((PlayerPropertyVisitor<? extends T>) visitor).visitProperty(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PropertyContext property() throws RecognitionException {
        PropertyContext _localctx = new PropertyContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_property);

        PlayerProperty.PlayerPropertyType typeInit;

        ObjectFlag oFlagInit = ObjectFlag.OF_NONE;
        PlayerFlag pFlagInit = PlayerFlag.PF_NONE;
        UIEntry entryInit = null;
        Stats statInit = Stats.STAT_NONE;
        Element elemInit = Element.ELEM_NONE;
        String bindUIValInit = "";
        String nameInit = "";
        String descInit = "";
        PlayerPropertyValue valueInit = PlayerPropertyValue.NONE;

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(46);
                type();

                if (typeOption.equals("player"))
                    typeInit = PlayerPropertyType.PROP_TYPE_PLAYER;
                else if (typeOption.equals("object"))
                    typeInit = PlayerPropertyType.PROP_TYPE_OBJECT;
                else
                    typeInit = PlayerPropertyType.PROP_TYPE_ELEMENT;

                setState(51);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == CODE) {
                    {
                        setState(48);
                        ((PropertyContext) _localctx).code = code();

                        oFlagInit = ((PropertyContext) _localctx).code.oFlag;
                        pFlagInit = ((PropertyContext) _localctx).code.pFlag;

                    }
                }

                setState(56);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == BINDUI) {
                    {
                        setState(53);
                        ((PropertyContext) _localctx).bindui = bindui();

                        entryInit = ((PropertyContext) _localctx).bindui.entry;
                        elemInit = ((PropertyContext) _localctx).bindui.elemEnum;
                        statInit = ((PropertyContext) _localctx).bindui.statEnum;
                        bindUIValInit = ((PropertyContext) _localctx).bindui.bindUIVal;

                    }
                }

                setState(58);
                ((PropertyContext) _localctx).name = name();
                nameInit = ((PropertyContext) _localctx).name.nameStr;
                setState(65);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == DESC) {
                    {
                        {
                            setState(60);
                            ((PropertyContext) _localctx).desc = desc();
                            descInit = descInit + ((PropertyContext) _localctx).desc.descStr;
                        }
                    }
                    setState(67);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(71);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == VALUE) {
                    {
                        setState(68);
                        ((PropertyContext) _localctx).value = value();

                        switch (((PropertyContext) _localctx).value.valueInt) {
                            case -1:
                                valueInit = PlayerPropertyValue.VULNERABILITY;
                                break;
                            case 1:
                                valueInit = PlayerPropertyValue.RESISTANCE;
                                break;
                            case 3:
                                valueInit = PlayerPropertyValue.IMMUNITY;
                                break;
                            default:
                                valueInit = PlayerPropertyValue.NONE;
                        }

                    }
                }

            }
            _ctx.stop = _input.LT(-1);

            ((PropertyContext) _localctx).playerProp = new PlayerProperty(typeInit, pFlagInit, oFlagInit,
                    entryInit, statInit, elemInit,
                    bindUIValInit, nameInit, descInit,
                    valueInit);

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
        public List<PlayerProperty> properties;
        public PropertyContext property;

        public TerminalNode EOF() {
            return getToken(PlayerPropertyParser.EOF, 0);
        }

        public List<PropertyContext> property() {
            return getRuleContexts(PropertyContext.class);
        }

        public PropertyContext property(int i) {
            return getRuleContext(PropertyContext.class, i);
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
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyVisitor)
                return ((PlayerPropertyVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_file);

        ((FileContext) _localctx).properties = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(76);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(73);
                            ((FileContext) _localctx).property = property();

                            _localctx.properties.add(((FileContext) _localctx).property.playerProp);

                        }
                    }
                    setState(78);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == TYPE);
                setState(80);
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
            "\u0004\u0001\rS\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0003\u0002\u001e\b\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u00064\b" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u00069\b\u0006\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0005\u0006@\b" +
                    "\u0006\n\u0006\f\u0006C\t\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003" +
                    "\u0006H\b\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0004\u0007M\b\u0007" +
                    "\u000b\u0007\f\u0007N\u0001\u0007\u0001\u0007\u0001\u0007\u0000\u0000" +
                    "\b\u0000\u0002\u0004\u0006\b\n\f\u000e\u0000\u0000P\u0000\u0010\u0001" +
                    "\u0000\u0000\u0000\u0002\u0014\u0001\u0000\u0000\u0000\u0004\u0018\u0001" +
                    "\u0000\u0000\u0000\u0006\"\u0001\u0000\u0000\u0000\b&\u0001\u0000\u0000" +
                    "\u0000\n*\u0001\u0000\u0000\u0000\f.\u0001\u0000\u0000\u0000\u000eL\u0001" +
                    "\u0000\u0000\u0000\u0010\u0011\u0005\u0003\u0000\u0000\u0011\u0012\u0005" +
                    "\t\u0000\u0000\u0012\u0013\u0006\u0000\uffff\uffff\u0000\u0013\u0001\u0001" +
                    "\u0000\u0000\u0000\u0014\u0015\u0005\u0004\u0000\u0000\u0015\u0016\u0005" +
                    "\u000b\u0000\u0000\u0016\u0017\u0006\u0001\uffff\uffff\u0000\u0017\u0003" +
                    "\u0001\u0000\u0000\u0000\u0018\u0019\u0005\u0005\u0000\u0000\u0019\u001a" +
                    "\u0005\u000b\u0000\u0000\u001a\u001d\u0006\u0002\uffff\uffff\u0000\u001b" +
                    "\u001c\u0005\f\u0000\u0000\u001c\u001e\u0006\u0002\uffff\uffff\u0000\u001d" +
                    "\u001b\u0001\u0000\u0000\u0000\u001d\u001e\u0001\u0000\u0000\u0000\u001e" +
                    "\u001f\u0001\u0000\u0000\u0000\u001f \u0005\r\u0000\u0000 !\u0006\u0002" +
                    "\uffff\uffff\u0000!\u0005\u0001\u0000\u0000\u0000\"#\u0005\u0006\u0000" +
                    "\u0000#$\u0005\u000b\u0000\u0000$%\u0006\u0003\uffff\uffff\u0000%\u0007" +
                    "\u0001\u0000\u0000\u0000&\'\u0005\u0007\u0000\u0000\'(\u0005\u000b\u0000" +
                    "\u0000()\u0006\u0004\uffff\uffff\u0000)\t\u0001\u0000\u0000\u0000*+\u0005" +
                    "\b\u0000\u0000+,\u0005\n\u0000\u0000,-\u0006\u0005\uffff\uffff\u0000-" +
                    "\u000b\u0001\u0000\u0000\u0000./\u0003\u0000\u0000\u0000/3\u0006\u0006" +
                    "\uffff\uffff\u000001\u0003\u0002\u0001\u000012\u0006\u0006\uffff\uffff" +
                    "\u000024\u0001\u0000\u0000\u000030\u0001\u0000\u0000\u000034\u0001\u0000" +
                    "\u0000\u000048\u0001\u0000\u0000\u000056\u0003\u0004\u0002\u000067\u0006" +
                    "\u0006\uffff\uffff\u000079\u0001\u0000\u0000\u000085\u0001\u0000\u0000" +
                    "\u000089\u0001\u0000\u0000\u00009:\u0001\u0000\u0000\u0000:;\u0003\u0006" +
                    "\u0003\u0000;A\u0006\u0006\uffff\uffff\u0000<=\u0003\b\u0004\u0000=>\u0006" +
                    "\u0006\uffff\uffff\u0000>@\u0001\u0000\u0000\u0000?<\u0001\u0000\u0000" +
                    "\u0000@C\u0001\u0000\u0000\u0000A?\u0001\u0000\u0000\u0000AB\u0001\u0000" +
                    "\u0000\u0000BG\u0001\u0000\u0000\u0000CA\u0001\u0000\u0000\u0000DE\u0003" +
                    "\n\u0005\u0000EF\u0006\u0006\uffff\uffff\u0000FH\u0001\u0000\u0000\u0000" +
                    "GD\u0001\u0000\u0000\u0000GH\u0001\u0000\u0000\u0000H\r\u0001\u0000\u0000" +
                    "\u0000IJ\u0003\f\u0006\u0000JK\u0006\u0007\uffff\uffff\u0000KM\u0001\u0000" +
                    "\u0000\u0000LI\u0001\u0000\u0000\u0000MN\u0001\u0000\u0000\u0000NL\u0001" +
                    "\u0000\u0000\u0000NO\u0001\u0000\u0000\u0000OP\u0001\u0000\u0000\u0000" +
                    "PQ\u0005\u0000\u0000\u0001Q\u000f\u0001\u0000\u0000\u0000\u0006\u001d" +
                    "38AGN";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
	}
}