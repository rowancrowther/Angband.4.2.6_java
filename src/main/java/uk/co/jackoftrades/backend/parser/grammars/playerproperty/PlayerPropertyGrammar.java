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
// Generated from PlayerPropertyGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.playerproperty;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.parser.playerproperty.PlayerPropertyParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class PlayerPropertyGrammar extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, TYPE = 2, CODE = 3, BINDUI = 4, NAME = 5, DESC = 6, VALUE = 7, INTEGER = 8,
            TYPE_VALUE = 9, LCASE = 10, TAG = 11, COLON = 12, COMMENT = 13, EOL = 14, STRING = 15,
            FLAG = 16;
    public static final int
            RULE_recordCount = 0, RULE_type = 1, RULE_code = 2, RULE_binduiEntry = 3,
            RULE_bindUI = 4, RULE_name = 5, RULE_desc = 6, RULE_value = 7, RULE_property = 8,
            RULE_file = 9;

    private static String[] makeRuleNames() {
        return new String[]{
                "recordCount", "type", "code", "binduiEntry", "bindUI", "name", "desc",
                "value", "property", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'type:'", "'code:'", "'bindui:'", "'name:'",
                "'desc:'", "'value:'", null, null, null, null, "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "TYPE", "CODE", "BINDUI", "NAME", "DESC", "VALUE",
                "INTEGER", "TYPE_VALUE", "LCASE", "TAG", "COLON", "COMMENT", "EOL", "STRING",
                "FLAG"
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
        return "PlayerPropertyGrammar.g4";
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

    public PlayerPropertyGrammar(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RecordCountContext extends ParserRuleContext {
        public String count;
        public Token INTEGER;

        public TerminalNode RECORD_COUNT() {
            return getToken(PlayerPropertyGrammar.RECORD_COUNT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerPropertyGrammar.INTEGER, 0);
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
            if (listener instanceof PlayerPropertyGrammarListener)
                ((PlayerPropertyGrammarListener) listener).enterRecordCount(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyGrammarListener)
                ((PlayerPropertyGrammarListener) listener).exitRecordCount(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyGrammarVisitor)
                return ((PlayerPropertyGrammarVisitor<? extends T>) visitor).visitRecordCount(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RecordCountContext recordCount() throws RecognitionException {
        RecordCountContext _localctx = new RecordCountContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_recordCount);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(20);
                match(RECORD_COUNT);
                setState(21);
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
    public static class TypeContext extends ParserRuleContext {
        public String propertyType;
        public int lineNo;
        public Token TYPE_VALUE;

        public TerminalNode TYPE() {
            return getToken(PlayerPropertyGrammar.TYPE, 0);
        }

        public TerminalNode TYPE_VALUE() {
            return getToken(PlayerPropertyGrammar.TYPE_VALUE, 0);
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
            if (listener instanceof PlayerPropertyGrammarListener)
                ((PlayerPropertyGrammarListener) listener).enterType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyGrammarListener)
                ((PlayerPropertyGrammarListener) listener).exitType(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyGrammarVisitor)
                return ((PlayerPropertyGrammarVisitor<? extends T>) visitor).visitType(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TypeContext type() throws RecognitionException {
        TypeContext _localctx = new TypeContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_type);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(24);
                match(TYPE);
                setState(25);
                ((TypeContext) _localctx).TYPE_VALUE = match(TYPE_VALUE);

                ((TypeContext) _localctx).propertyType = ((TypeContext) _localctx).TYPE_VALUE.getText();
                ((TypeContext) _localctx).lineNo = _localctx.start.getLine();

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
        public String codeName;
        public Token FLAG;

        public TerminalNode CODE() {
            return getToken(PlayerPropertyGrammar.CODE, 0);
        }

        public TerminalNode FLAG() {
            return getToken(PlayerPropertyGrammar.FLAG, 0);
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
            if (listener instanceof PlayerPropertyGrammarListener)
                ((PlayerPropertyGrammarListener) listener).enterCode(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyGrammarListener)
                ((PlayerPropertyGrammarListener) listener).exitCode(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyGrammarVisitor)
                return ((PlayerPropertyGrammarVisitor<? extends T>) visitor).visitCode(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CodeContext code() throws RecognitionException {
        CodeContext _localctx = new CodeContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_code);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(28);
                match(CODE);
                setState(29);
                ((CodeContext) _localctx).FLAG = match(FLAG);
                ((CodeContext) _localctx).codeName = ((CodeContext) _localctx).FLAG.getText();
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
    public static class BinduiEntryContext extends ParserRuleContext {
        public List<String> entry;
        public Token LCASE;
        public Token TAG;
        public Token aux;
        public Token val;

        public TerminalNode BINDUI() {
            return getToken(PlayerPropertyGrammar.BINDUI, 0);
        }

        public List<TerminalNode> LCASE() {
            return getTokens(PlayerPropertyGrammar.LCASE);
        }

        public TerminalNode LCASE(int i) {
            return getToken(PlayerPropertyGrammar.LCASE, i);
        }

        public List<TerminalNode> COLON() {
            return getTokens(PlayerPropertyGrammar.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(PlayerPropertyGrammar.COLON, i);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerPropertyGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerPropertyGrammar.INTEGER, i);
        }

        public TerminalNode TAG() {
            return getToken(PlayerPropertyGrammar.TAG, 0);
        }

        public BinduiEntryContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_binduiEntry;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyGrammarListener)
                ((PlayerPropertyGrammarListener) listener).enterBinduiEntry(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyGrammarListener)
                ((PlayerPropertyGrammarListener) listener).exitBinduiEntry(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyGrammarVisitor)
                return ((PlayerPropertyGrammarVisitor<? extends T>) visitor).visitBinduiEntry(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BinduiEntryContext binduiEntry() throws RecognitionException {
        BinduiEntryContext _localctx = new BinduiEntryContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_binduiEntry);

        String tagText = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(32);
                match(BINDUI);
                setState(33);
                ((BinduiEntryContext) _localctx).LCASE = match(LCASE);
                setState(36);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == TAG) {
                    {
                        setState(34);
                        ((BinduiEntryContext) _localctx).TAG = match(TAG);
                        tagText = ((BinduiEntryContext) _localctx).TAG.getText();
                    }
                }

                setState(38);
                match(COLON);
                setState(39);
                ((BinduiEntryContext) _localctx).aux = match(INTEGER);
                setState(40);
                match(COLON);
                setState(41);
                ((BinduiEntryContext) _localctx).val = _input.LT(1);
                _la = _input.LA(1);
                if (!(_la == INTEGER || _la == LCASE)) {
                    ((BinduiEntryContext) _localctx).val = (Token) _errHandler.recoverInline(this);
                } else {
                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
                    _errHandler.reportMatch(this);
                    consume();
                }

                ((BinduiEntryContext) _localctx).entry = List.of(((BinduiEntryContext) _localctx).LCASE.getText(), tagText, ((BinduiEntryContext) _localctx).aux.getText(),
                        ((BinduiEntryContext) _localctx).val.getText());

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
        public List<List<String>> bindUIs;
        public BinduiEntryContext binduiEntry;

        public List<BinduiEntryContext> binduiEntry() {
            return getRuleContexts(BinduiEntryContext.class);
        }

        public BinduiEntryContext binduiEntry(int i) {
            return getRuleContext(BinduiEntryContext.class, i);
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
            if (listener instanceof PlayerPropertyGrammarListener)
                ((PlayerPropertyGrammarListener) listener).enterBindUI(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyGrammarListener)
                ((PlayerPropertyGrammarListener) listener).exitBindUI(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyGrammarVisitor)
                return ((PlayerPropertyGrammarVisitor<? extends T>) visitor).visitBindUI(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BindUIContext bindUI() throws RecognitionException {
        BindUIContext _localctx = new BindUIContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_bindUI);

        ((BindUIContext) _localctx).bindUIs = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(49);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == BINDUI) {
                    {
                        {
                            setState(44);
                            ((BindUIContext) _localctx).binduiEntry = binduiEntry();
                            _localctx.bindUIs.add(((BindUIContext) _localctx).binduiEntry.entry);
                        }
                    }
                    setState(51);
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
    public static class NameContext extends ParserRuleContext {
        public String nameStr;
        public Token STRING;

        public TerminalNode NAME() {
            return getToken(PlayerPropertyGrammar.NAME, 0);
        }

        public TerminalNode STRING() {
            return getToken(PlayerPropertyGrammar.STRING, 0);
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
            if (listener instanceof PlayerPropertyGrammarListener)
                ((PlayerPropertyGrammarListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyGrammarListener)
                ((PlayerPropertyGrammarListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyGrammarVisitor)
                return ((PlayerPropertyGrammarVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(52);
                match(NAME);
                setState(53);
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
    public static class DescContext extends ParserRuleContext {
        public String descStr;
        public Token STRING;

        public List<TerminalNode> DESC() {
            return getTokens(PlayerPropertyGrammar.DESC);
        }

        public TerminalNode DESC(int i) {
            return getToken(PlayerPropertyGrammar.DESC, i);
        }

        public List<TerminalNode> STRING() {
            return getTokens(PlayerPropertyGrammar.STRING);
        }

        public TerminalNode STRING(int i) {
            return getToken(PlayerPropertyGrammar.STRING, i);
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
            if (listener instanceof PlayerPropertyGrammarListener)
                ((PlayerPropertyGrammarListener) listener).enterDesc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyGrammarListener)
                ((PlayerPropertyGrammarListener) listener).exitDesc(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyGrammarVisitor)
                return ((PlayerPropertyGrammarVisitor<? extends T>) visitor).visitDesc(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DescContext desc() throws RecognitionException {
        DescContext _localctx = new DescContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_desc);

        StringBuilder sb = new StringBuilder();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(59);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(56);
                            match(DESC);
                            setState(57);
                            ((DescContext) _localctx).STRING = match(STRING);
                            sb.append(((DescContext) _localctx).STRING.getText());
                        }
                    }
                    setState(61);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == DESC);
            }
            _ctx.stop = _input.LT(-1);

            ((DescContext) _localctx).descStr = sb.toString();

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
        public String valueInt;
        public Token INTEGER;

        public TerminalNode VALUE() {
            return getToken(PlayerPropertyGrammar.VALUE, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerPropertyGrammar.INTEGER, 0);
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
            if (listener instanceof PlayerPropertyGrammarListener)
                ((PlayerPropertyGrammarListener) listener).enterValue(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyGrammarListener)
                ((PlayerPropertyGrammarListener) listener).exitValue(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyGrammarVisitor)
                return ((PlayerPropertyGrammarVisitor<? extends T>) visitor).visitValue(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ValueContext value() throws RecognitionException {
        ValueContext _localctx = new ValueContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_value);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(63);
                match(VALUE);
                setState(64);
                ((ValueContext) _localctx).INTEGER = match(INTEGER);

                ((ValueContext) _localctx).valueInt = ((ValueContext) _localctx).INTEGER.getText();

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
        public PlayerPropertyParseRecord playerProp;
        public TypeContext type;
        public CodeContext code;
        public BindUIContext bindUI;
        public NameContext name;
        public DescContext desc;
        public ValueContext value;

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public BindUIContext bindUI() {
            return getRuleContext(BindUIContext.class, 0);
        }

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public CodeContext code() {
            return getRuleContext(CodeContext.class, 0);
        }

        public DescContext desc() {
            return getRuleContext(DescContext.class, 0);
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
            if (listener instanceof PlayerPropertyGrammarListener)
                ((PlayerPropertyGrammarListener) listener).enterProperty(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyGrammarListener)
                ((PlayerPropertyGrammarListener) listener).exitProperty(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyGrammarVisitor)
                return ((PlayerPropertyGrammarVisitor<? extends T>) visitor).visitProperty(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PropertyContext property() throws RecognitionException {
        PropertyContext _localctx = new PropertyContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_property);

        String typeInit = "";
        String codeInit = "";
        List<List<String>> bindUIInit = new ArrayList<>();
        String nameInit = "";
        String descInit = "";
        String valueInit = "";
        int line = 0;

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(67);
                ((PropertyContext) _localctx).type = type();

                line = ((PropertyContext) _localctx).type.lineNo;
                typeInit = ((PropertyContext) _localctx).type.propertyType;

                setState(72);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == CODE) {
                    {
                        setState(69);
                        ((PropertyContext) _localctx).code = code();

                        codeInit = ((PropertyContext) _localctx).code.codeName;

                    }
                }

                setState(74);
                ((PropertyContext) _localctx).bindUI = bindUI();

                bindUIInit = ((PropertyContext) _localctx).bindUI.bindUIs;

                setState(76);
                ((PropertyContext) _localctx).name = name();
                nameInit = ((PropertyContext) _localctx).name.nameStr;
                setState(81);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == DESC) {
                    {
                        setState(78);
                        ((PropertyContext) _localctx).desc = desc();
                        descInit = ((PropertyContext) _localctx).desc.descStr;
                    }
                }

                setState(86);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == VALUE) {
                    {
                        setState(83);
                        ((PropertyContext) _localctx).value = value();
                        valueInit = ((PropertyContext) _localctx).value.valueInt;
                    }
                }

            }
            _ctx.stop = _input.LT(-1);

            ((PropertyContext) _localctx).playerProp = new PlayerPropertyParseRecord(typeInit, codeInit,
                    bindUIInit, nameInit, descInit, valueInit, line);

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
        public List<PlayerPropertyParseRecord> properties;
        public String declaredRecords;
        public RecordCountContext recordCount;
        public PropertyContext property;

        public RecordCountContext recordCount() {
            return getRuleContext(RecordCountContext.class, 0);
        }

        public TerminalNode EOF() {
            return getToken(PlayerPropertyGrammar.EOF, 0);
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
            if (listener instanceof PlayerPropertyGrammarListener)
                ((PlayerPropertyGrammarListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyGrammarListener)
                ((PlayerPropertyGrammarListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyGrammarVisitor)
                return ((PlayerPropertyGrammarVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_file);

        ((FileContext) _localctx).properties = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(88);
                ((FileContext) _localctx).recordCount = recordCount();
                ((FileContext) _localctx).declaredRecords = ((FileContext) _localctx).recordCount.count;
                setState(93);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(90);
                            ((FileContext) _localctx).property = property();

                            _localctx.properties.add(((FileContext) _localctx).property.playerProp);

                        }
                    }
                    setState(95);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == TYPE);
                setState(97);
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
            "\u0004\u0001\u0010d\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002" +
                    "\b\u0007\b\u0002\t\u0007\t\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0003\u0003%\b\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0005\u0004" +
                    "0\b\u0004\n\u0004\f\u00043\t\u0004\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0004\u0006<\b\u0006" +
                    "\u000b\u0006\f\u0006=\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0003\bI\b\b\u0001\b\u0001\b" +
                    "\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0003\bR\b\b\u0001\b\u0001\b" +
                    "\u0001\b\u0003\bW\b\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0004\t" +
                    "^\b\t\u000b\t\f\t_\u0001\t\u0001\t\u0001\t\u0000\u0000\n\u0000\u0002\u0004" +
                    "\u0006\b\n\f\u000e\u0010\u0012\u0000\u0001\u0002\u0000\b\b\n\n`\u0000" +
                    "\u0014\u0001\u0000\u0000\u0000\u0002\u0018\u0001\u0000\u0000\u0000\u0004" +
                    "\u001c\u0001\u0000\u0000\u0000\u0006 \u0001\u0000\u0000\u0000\b1\u0001" +
                    "\u0000\u0000\u0000\n4\u0001\u0000\u0000\u0000\f;\u0001\u0000\u0000\u0000" +
                    "\u000e?\u0001\u0000\u0000\u0000\u0010C\u0001\u0000\u0000\u0000\u0012X" +
                    "\u0001\u0000\u0000\u0000\u0014\u0015\u0005\u0001\u0000\u0000\u0015\u0016" +
                    "\u0005\b\u0000\u0000\u0016\u0017\u0006\u0000\uffff\uffff\u0000\u0017\u0001" +
                    "\u0001\u0000\u0000\u0000\u0018\u0019\u0005\u0002\u0000\u0000\u0019\u001a" +
                    "\u0005\t\u0000\u0000\u001a\u001b\u0006\u0001\uffff\uffff\u0000\u001b\u0003" +
                    "\u0001\u0000\u0000\u0000\u001c\u001d\u0005\u0003\u0000\u0000\u001d\u001e" +
                    "\u0005\u0010\u0000\u0000\u001e\u001f\u0006\u0002\uffff\uffff\u0000\u001f" +
                    "\u0005\u0001\u0000\u0000\u0000 !\u0005\u0004\u0000\u0000!$\u0005\n\u0000" +
                    "\u0000\"#\u0005\u000b\u0000\u0000#%\u0006\u0003\uffff\uffff\u0000$\"\u0001" +
                    "\u0000\u0000\u0000$%\u0001\u0000\u0000\u0000%&\u0001\u0000\u0000\u0000" +
                    "&\'\u0005\f\u0000\u0000\'(\u0005\b\u0000\u0000()\u0005\f\u0000\u0000)" +
                    "*\u0007\u0000\u0000\u0000*+\u0006\u0003\uffff\uffff\u0000+\u0007\u0001" +
                    "\u0000\u0000\u0000,-\u0003\u0006\u0003\u0000-.\u0006\u0004\uffff\uffff" +
                    "\u0000.0\u0001\u0000\u0000\u0000/,\u0001\u0000\u0000\u000003\u0001\u0000" +
                    "\u0000\u00001/\u0001\u0000\u0000\u000012\u0001\u0000\u0000\u00002\t\u0001" +
                    "\u0000\u0000\u000031\u0001\u0000\u0000\u000045\u0005\u0005\u0000\u0000" +
                    "56\u0005\u000f\u0000\u000067\u0006\u0005\uffff\uffff\u00007\u000b\u0001" +
                    "\u0000\u0000\u000089\u0005\u0006\u0000\u00009:\u0005\u000f\u0000\u0000" +
                    ":<\u0006\u0006\uffff\uffff\u0000;8\u0001\u0000\u0000\u0000<=\u0001\u0000" +
                    "\u0000\u0000=;\u0001\u0000\u0000\u0000=>\u0001\u0000\u0000\u0000>\r\u0001" +
                    "\u0000\u0000\u0000?@\u0005\u0007\u0000\u0000@A\u0005\b\u0000\u0000AB\u0006" +
                    "\u0007\uffff\uffff\u0000B\u000f\u0001\u0000\u0000\u0000CD\u0003\u0002" +
                    "\u0001\u0000DH\u0006\b\uffff\uffff\u0000EF\u0003\u0004\u0002\u0000FG\u0006" +
                    "\b\uffff\uffff\u0000GI\u0001\u0000\u0000\u0000HE\u0001\u0000\u0000\u0000" +
                    "HI\u0001\u0000\u0000\u0000IJ\u0001\u0000\u0000\u0000JK\u0003\b\u0004\u0000" +
                    "KL\u0006\b\uffff\uffff\u0000LM\u0003\n\u0005\u0000MQ\u0006\b\uffff\uffff" +
                    "\u0000NO\u0003\f\u0006\u0000OP\u0006\b\uffff\uffff\u0000PR\u0001\u0000" +
                    "\u0000\u0000QN\u0001\u0000\u0000\u0000QR\u0001\u0000\u0000\u0000RV\u0001" +
                    "\u0000\u0000\u0000ST\u0003\u000e\u0007\u0000TU\u0006\b\uffff\uffff\u0000" +
                    "UW\u0001\u0000\u0000\u0000VS\u0001\u0000\u0000\u0000VW\u0001\u0000\u0000" +
                    "\u0000W\u0011\u0001\u0000\u0000\u0000XY\u0003\u0000\u0000\u0000Y]\u0006" +
                    "\t\uffff\uffff\u0000Z[\u0003\u0010\b\u0000[\\\u0006\t\uffff\uffff\u0000" +
                    "\\^\u0001\u0000\u0000\u0000]Z\u0001\u0000\u0000\u0000^_\u0001\u0000\u0000" +
                    "\u0000_]\u0001\u0000\u0000\u0000_`\u0001\u0000\u0000\u0000`a\u0001\u0000" +
                    "\u0000\u0000ab\u0005\u0000\u0000\u0001b\u0013\u0001\u0000\u0000\u0000" +
                    "\u0007$1=HQV_";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}