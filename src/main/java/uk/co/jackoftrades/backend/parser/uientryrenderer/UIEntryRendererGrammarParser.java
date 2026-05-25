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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/UIEntryRendererGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.uientryrenderer;

import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
import uk.co.jackoftrades.frontend.entries.enums.UIEntryEnum;
import uk.co.jackoftrades.frontend.entries.enums.UIEntryRendererEnum;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class UIEntryRendererGrammarParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, NAME = 3, CODE = 4, COLOURS = 5, LABELCOLOURS = 6, SYMBOLS = 7,
            NDIGITS = 8, SIGN = 9, COLOURCHARS = 10, SYMBOLCHARS = 11, DIGIT = 12, LCASEWORD = 13,
            UCASEWORD = 14;
    public static final int
            RULE_name = 0, RULE_code = 1, RULE_colours = 2, RULE_labelcolours = 3,
            RULE_symbols = 4, RULE_ndigit = 5, RULE_sign = 6, RULE_uiEntry = 7, RULE_file = 8;

    private static String[] makeRuleNames() {
        return new String[]{
                "name", "code", "colours", "labelcolours", "symbols", "ndigit", "sign",
                "uiEntry", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'name:'", "'code:'", "'colors:'", "'labelcolors:'",
                "'symbols:'", "'ndigit:'", "'sign:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "NAME", "CODE", "COLOURS", "LABELCOLOURS", "SYMBOLS",
                "NDIGITS", "SIGN", "COLOURCHARS", "SYMBOLCHARS", "DIGIT", "LCASEWORD",
                "UCASEWORD"
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
        return "UIEntryRendererGrammar.g4";
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

    public UIEntryRendererGrammarParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class NameContext extends ParserRuleContext {
        public String nameStr;
        public Token LCASEWORD;

        public TerminalNode NAME() {
            return getToken(UIEntryRendererGrammarParser.NAME, 0);
        }

        public TerminalNode LCASEWORD() {
            return getToken(UIEntryRendererGrammarParser.LCASEWORD, 0);
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
            if (listener instanceof UIEntryRendererGrammarListener)
                ((UIEntryRendererGrammarListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererGrammarListener)
                ((UIEntryRendererGrammarListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryRendererGrammarVisitor)
                return ((UIEntryRendererGrammarVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(18);
                match(NAME);
                setState(19);
                ((NameContext) _localctx).LCASEWORD = match(LCASEWORD);

                ((NameContext) _localctx).nameStr = ((NameContext) _localctx).LCASEWORD.getText();

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
        public UIEntryRendererEnum codeEnum;
        public Token UCASEWORD;

        public TerminalNode CODE() {
            return getToken(UIEntryRendererGrammarParser.CODE, 0);
        }

        public TerminalNode UCASEWORD() {
            return getToken(UIEntryRendererGrammarParser.UCASEWORD, 0);
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
            if (listener instanceof UIEntryRendererGrammarListener)
                ((UIEntryRendererGrammarListener) listener).enterCode(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererGrammarListener)
                ((UIEntryRendererGrammarListener) listener).exitCode(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryRendererGrammarVisitor)
                return ((UIEntryRendererGrammarVisitor<? extends T>) visitor).visitCode(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CodeContext code() throws RecognitionException {
        CodeContext _localctx = new CodeContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_code);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(22);
                match(CODE);
                setState(23);
                ((CodeContext) _localctx).UCASEWORD = match(UCASEWORD);

                String flag = "UI_ENTRY_RENDERER_" + ((CodeContext) _localctx).UCASEWORD.getText();
                ((CodeContext) _localctx).codeEnum = UIEntryRendererEnum.valueOf(flag);

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
    public static class ColoursContext extends ParserRuleContext {
        public String colourCharStr;
        public Token COLOURCHARS;

        public TerminalNode COLOURS() {
            return getToken(UIEntryRendererGrammarParser.COLOURS, 0);
        }

        public TerminalNode COLOURCHARS() {
            return getToken(UIEntryRendererGrammarParser.COLOURCHARS, 0);
        }

        public ColoursContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_colours;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererGrammarListener)
                ((UIEntryRendererGrammarListener) listener).enterColours(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererGrammarListener)
                ((UIEntryRendererGrammarListener) listener).exitColours(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryRendererGrammarVisitor)
                return ((UIEntryRendererGrammarVisitor<? extends T>) visitor).visitColours(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ColoursContext colours() throws RecognitionException {
        ColoursContext _localctx = new ColoursContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_colours);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(26);
                match(COLOURS);
                setState(27);
                ((ColoursContext) _localctx).COLOURCHARS = match(COLOURCHARS);

                ((ColoursContext) _localctx).colourCharStr = ((ColoursContext) _localctx).COLOURCHARS.getText();

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
    public static class LabelcoloursContext extends ParserRuleContext {
        public String colourCharStr;
        public Token COLOURCHARS;

        public TerminalNode LABELCOLOURS() {
            return getToken(UIEntryRendererGrammarParser.LABELCOLOURS, 0);
        }

        public TerminalNode COLOURCHARS() {
            return getToken(UIEntryRendererGrammarParser.COLOURCHARS, 0);
        }

        public LabelcoloursContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_labelcolours;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererGrammarListener)
                ((UIEntryRendererGrammarListener) listener).enterLabelcolours(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererGrammarListener)
                ((UIEntryRendererGrammarListener) listener).exitLabelcolours(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryRendererGrammarVisitor)
                return ((UIEntryRendererGrammarVisitor<? extends T>) visitor).visitLabelcolours(this);
            else return visitor.visitChildren(this);
        }
    }

    public final LabelcoloursContext labelcolours() throws RecognitionException {
        LabelcoloursContext _localctx = new LabelcoloursContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_labelcolours);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(30);
                match(LABELCOLOURS);
                setState(31);
                ((LabelcoloursContext) _localctx).COLOURCHARS = match(COLOURCHARS);

                ((LabelcoloursContext) _localctx).colourCharStr = ((LabelcoloursContext) _localctx).COLOURCHARS.getText();

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
    public static class SymbolsContext extends ParserRuleContext {
        public String symbolCharsStr;
        public Token SYMBOLCHARS;

        public TerminalNode SYMBOLS() {
            return getToken(UIEntryRendererGrammarParser.SYMBOLS, 0);
        }

        public TerminalNode SYMBOLCHARS() {
            return getToken(UIEntryRendererGrammarParser.SYMBOLCHARS, 0);
        }

        public SymbolsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_symbols;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererGrammarListener)
                ((UIEntryRendererGrammarListener) listener).enterSymbols(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererGrammarListener)
                ((UIEntryRendererGrammarListener) listener).exitSymbols(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryRendererGrammarVisitor)
                return ((UIEntryRendererGrammarVisitor<? extends T>) visitor).visitSymbols(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SymbolsContext symbols() throws RecognitionException {
        SymbolsContext _localctx = new SymbolsContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_symbols);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(34);
                match(SYMBOLS);
                setState(35);
                ((SymbolsContext) _localctx).SYMBOLCHARS = match(SYMBOLCHARS);

                ((SymbolsContext) _localctx).symbolCharsStr = ((SymbolsContext) _localctx).SYMBOLCHARS.getText();

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
    public static class NdigitContext extends ParserRuleContext {
        public int numDigitsInt;
        public Token DIGIT;

        public TerminalNode NDIGITS() {
            return getToken(UIEntryRendererGrammarParser.NDIGITS, 0);
        }

        public TerminalNode DIGIT() {
            return getToken(UIEntryRendererGrammarParser.DIGIT, 0);
        }

        public NdigitContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_ndigit;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererGrammarListener)
                ((UIEntryRendererGrammarListener) listener).enterNdigit(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererGrammarListener)
                ((UIEntryRendererGrammarListener) listener).exitNdigit(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryRendererGrammarVisitor)
                return ((UIEntryRendererGrammarVisitor<? extends T>) visitor).visitNdigit(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NdigitContext ndigit() throws RecognitionException {
        NdigitContext _localctx = new NdigitContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_ndigit);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(38);
                match(NDIGITS);
                setState(39);
                ((NdigitContext) _localctx).DIGIT = match(DIGIT);

                ((NdigitContext) _localctx).numDigitsInt = Integer.parseInt(((NdigitContext) _localctx).DIGIT.getText());

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
    public static class SignContext extends ParserRuleContext {
        public UIEntryEnum signEnum;
        public Token UCASEWORD;

        public TerminalNode SIGN() {
            return getToken(UIEntryRendererGrammarParser.SIGN, 0);
        }

        public TerminalNode UCASEWORD() {
            return getToken(UIEntryRendererGrammarParser.UCASEWORD, 0);
        }

        public SignContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_sign;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererGrammarListener)
                ((UIEntryRendererGrammarListener) listener).enterSign(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererGrammarListener)
                ((UIEntryRendererGrammarListener) listener).exitSign(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryRendererGrammarVisitor)
                return ((UIEntryRendererGrammarVisitor<? extends T>) visitor).visitSign(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SignContext sign() throws RecognitionException {
        SignContext _localctx = new SignContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_sign);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(42);
                match(SIGN);
                setState(43);
                ((SignContext) _localctx).UCASEWORD = match(UCASEWORD);

                String flag = "UI_ENTRY_" + ((SignContext) _localctx).UCASEWORD.getText();
                ((SignContext) _localctx).signEnum = UIEntryEnum.valueOf(flag);

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
    public static class UiEntryContext extends ParserRuleContext {
        public UIEntryRenderer renderer;
        public NameContext name;
        public CodeContext code;
        public ColoursContext colours;
        public LabelcoloursContext labelcolours;
        public SymbolsContext symbols;
        public NdigitContext ndigit;
        public SignContext sign;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public CodeContext code() {
            return getRuleContext(CodeContext.class, 0);
        }

        public ColoursContext colours() {
            return getRuleContext(ColoursContext.class, 0);
        }

        public LabelcoloursContext labelcolours() {
            return getRuleContext(LabelcoloursContext.class, 0);
        }

        public SymbolsContext symbols() {
            return getRuleContext(SymbolsContext.class, 0);
        }

        public NdigitContext ndigit() {
            return getRuleContext(NdigitContext.class, 0);
        }

        public SignContext sign() {
            return getRuleContext(SignContext.class, 0);
        }

        public UiEntryContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_uiEntry;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererGrammarListener)
                ((UIEntryRendererGrammarListener) listener).enterUiEntry(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererGrammarListener)
                ((UIEntryRendererGrammarListener) listener).exitUiEntry(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryRendererGrammarVisitor)
                return ((UIEntryRendererGrammarVisitor<? extends T>) visitor).visitUiEntry(this);
            else return visitor.visitChildren(this);
        }
    }

    public final UiEntryContext uiEntry() throws RecognitionException {
        UiEntryContext _localctx = new UiEntryContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_uiEntry);

        String nameInit = "";
        UIEntryRendererEnum codeInit = UIEntryRendererEnum.UI_ENTRY_RENDERER_NONE;
        String colourCharsInit = "";
        String labelColourCharsInit = "";
        String symbolCharsInit = "";
        int nDigitInit = 1;
        UIEntryEnum signInit = UIEntryEnum.UI_ENTRY_NO_SIGN;

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(46);
                ((UiEntryContext) _localctx).name = name();
                nameInit = ((UiEntryContext) _localctx).name.nameStr;
                setState(48);
                ((UiEntryContext) _localctx).code = code();
                codeInit = ((UiEntryContext) _localctx).code.codeEnum;
                setState(50);
                ((UiEntryContext) _localctx).colours = colours();
                colourCharsInit = ((UiEntryContext) _localctx).colours.colourCharStr;
                setState(52);
                ((UiEntryContext) _localctx).labelcolours = labelcolours();
                labelColourCharsInit = ((UiEntryContext) _localctx).labelcolours.colourCharStr;
                setState(54);
                ((UiEntryContext) _localctx).symbols = symbols();
                symbolCharsInit = ((UiEntryContext) _localctx).symbols.symbolCharsStr;
                setState(59);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == NDIGITS) {
                    {
                        setState(56);
                        ((UiEntryContext) _localctx).ndigit = ndigit();
                        nDigitInit = ((UiEntryContext) _localctx).ndigit.numDigitsInt;
                    }
                }

                setState(64);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == SIGN) {
                    {
                        setState(61);
                        ((UiEntryContext) _localctx).sign = sign();
                        signInit = ((UiEntryContext) _localctx).sign.signEnum;
                    }
                }

            }
            _ctx.stop = _input.LT(-1);

            ((UiEntryContext) _localctx).renderer = new UIEntryRenderer(nameInit,
                    codeInit, colourCharsInit,
                    labelColourCharsInit, symbolCharsInit,
                    nDigitInit, signInit);

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
        public List<UIEntryRenderer> renderers;
        public UiEntryContext uiEntry;

        public TerminalNode EOF() {
            return getToken(UIEntryRendererGrammarParser.EOF, 0);
        }

        public List<UiEntryContext> uiEntry() {
            return getRuleContexts(UiEntryContext.class);
        }

        public UiEntryContext uiEntry(int i) {
            return getRuleContext(UiEntryContext.class, i);
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
            if (listener instanceof UIEntryRendererGrammarListener)
                ((UIEntryRendererGrammarListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererGrammarListener)
                ((UIEntryRendererGrammarListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryRendererGrammarVisitor)
                return ((UIEntryRendererGrammarVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_file);

        ((FileContext) _localctx).renderers = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(69);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(66);
                            ((FileContext) _localctx).uiEntry = uiEntry();

                            _localctx.renderers.add(((FileContext) _localctx).uiEntry.renderer);

                        }
                    }
                    setState(71);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(73);
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
            "\u0004\u0001\u000eL\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002" +
                    "\b\u0007\b\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0003\u0007<\b\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007" +
                    "A\b\u0007\u0001\b\u0001\b\u0001\b\u0004\bF\b\b\u000b\b\f\bG\u0001\b\u0001" +
                    "\b\u0001\b\u0000\u0000\t\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0000" +
                    "\u0000E\u0000\u0012\u0001\u0000\u0000\u0000\u0002\u0016\u0001\u0000\u0000" +
                    "\u0000\u0004\u001a\u0001\u0000\u0000\u0000\u0006\u001e\u0001\u0000\u0000" +
                    "\u0000\b\"\u0001\u0000\u0000\u0000\n&\u0001\u0000\u0000\u0000\f*\u0001" +
                    "\u0000\u0000\u0000\u000e.\u0001\u0000\u0000\u0000\u0010E\u0001\u0000\u0000" +
                    "\u0000\u0012\u0013\u0005\u0003\u0000\u0000\u0013\u0014\u0005\r\u0000\u0000" +
                    "\u0014\u0015\u0006\u0000\uffff\uffff\u0000\u0015\u0001\u0001\u0000\u0000" +
                    "\u0000\u0016\u0017\u0005\u0004\u0000\u0000\u0017\u0018\u0005\u000e\u0000" +
                    "\u0000\u0018\u0019\u0006\u0001\uffff\uffff\u0000\u0019\u0003\u0001\u0000" +
                    "\u0000\u0000\u001a\u001b\u0005\u0005\u0000\u0000\u001b\u001c\u0005\n\u0000" +
                    "\u0000\u001c\u001d\u0006\u0002\uffff\uffff\u0000\u001d\u0005\u0001\u0000" +
                    "\u0000\u0000\u001e\u001f\u0005\u0006\u0000\u0000\u001f \u0005\n\u0000" +
                    "\u0000 !\u0006\u0003\uffff\uffff\u0000!\u0007\u0001\u0000\u0000\u0000" +
                    "\"#\u0005\u0007\u0000\u0000#$\u0005\u000b\u0000\u0000$%\u0006\u0004\uffff" +
                    "\uffff\u0000%\t\u0001\u0000\u0000\u0000&\'\u0005\b\u0000\u0000\'(\u0005" +
                    "\f\u0000\u0000()\u0006\u0005\uffff\uffff\u0000)\u000b\u0001\u0000\u0000" +
                    "\u0000*+\u0005\t\u0000\u0000+,\u0005\u000e\u0000\u0000,-\u0006\u0006\uffff" +
                    "\uffff\u0000-\r\u0001\u0000\u0000\u0000./\u0003\u0000\u0000\u0000/0\u0006" +
                    "\u0007\uffff\uffff\u000001\u0003\u0002\u0001\u000012\u0006\u0007\uffff" +
                    "\uffff\u000023\u0003\u0004\u0002\u000034\u0006\u0007\uffff\uffff\u0000" +
                    "45\u0003\u0006\u0003\u000056\u0006\u0007\uffff\uffff\u000067\u0003\b\u0004" +
                    "\u00007;\u0006\u0007\uffff\uffff\u000089\u0003\n\u0005\u00009:\u0006\u0007" +
                    "\uffff\uffff\u0000:<\u0001\u0000\u0000\u0000;8\u0001\u0000\u0000\u0000" +
                    ";<\u0001\u0000\u0000\u0000<@\u0001\u0000\u0000\u0000=>\u0003\f\u0006\u0000" +
                    ">?\u0006\u0007\uffff\uffff\u0000?A\u0001\u0000\u0000\u0000@=\u0001\u0000" +
                    "\u0000\u0000@A\u0001\u0000\u0000\u0000A\u000f\u0001\u0000\u0000\u0000" +
                    "BC\u0003\u000e\u0007\u0000CD\u0006\b\uffff\uffff\u0000DF\u0001\u0000\u0000" +
                    "\u0000EB\u0001\u0000\u0000\u0000FG\u0001\u0000\u0000\u0000GE\u0001\u0000" +
                    "\u0000\u0000GH\u0001\u0000\u0000\u0000HI\u0001\u0000\u0000\u0000IJ\u0005" +
                    "\u0000\u0000\u0001J\u0011\u0001\u0000\u0000\u0000\u0003;@G";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}