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
// Generated from /home/rowan/IdeaProjects/Angband.4.2.6_java/src/main/java/uk/co/jackoftradesltd/frontend/ui/entryrenderer/UIEntryRendererGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.frontend.ui.entryrenderer.antlr4.uientryrenderer;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class UIEntryRendererGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, CODE=2, COLOURS=3, LABELCOLOURS=4, SYMBOLS=5, NDIGITS=6, SIGN=7, 
		COLOURCHARS=8, INTEGER=9, COMMENT=10, EOL=11, FLAG=12, STRING=13;
	public static final int
		RULE_name = 0, RULE_code = 1, RULE_colours = 2, RULE_labelcolours = 3, 
		RULE_symbols = 4, RULE_ndigit = 5, RULE_sign = 6, RULE_uiEntry = 7, RULE_file = 8;
	private static String[] makeRuleNames() {
		return new String[] {
			"name", "code", "colours", "labelcolours", "symbols", "ndigit", "sign", 
			"uiEntry", "file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'code:'", "'colors:'", "'labelcolors:'", "'symbols:'", 
			"'ndigit:'", "'sign:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "CODE", "COLOURS", "LABELCOLOURS", "SYMBOLS", "NDIGITS", 
			"SIGN", "COLOURCHARS", "INTEGER", "COMMENT", "EOL", "FLAG", "STRING"
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
	public String getGrammarFileName() { return "UIEntryRendererGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public UIEntryRendererGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public Token STRING;
		public TerminalNode NAME() { return getToken(UIEntryRendererGrammar.NAME, 0); }
		public TerminalNode STRING() { return getToken(UIEntryRendererGrammar.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryRendererGrammarListener ) ((UIEntryRendererGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryRendererGrammarListener ) ((UIEntryRendererGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryRendererGrammarVisitor ) return ((UIEntryRendererGrammarVisitor<? extends T>)visitor).visitName(this);
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
			((NameContext)_localctx).STRING = match(STRING);

			                ((NameContext)_localctx).nameStr =  ((NameContext)_localctx).STRING.getText();
			            
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CodeContext extends ParserRuleContext {
		public String codeStr;
		public Token FLAG;
		public TerminalNode CODE() { return getToken(UIEntryRendererGrammar.CODE, 0); }
		public TerminalNode FLAG() { return getToken(UIEntryRendererGrammar.FLAG, 0); }
		public CodeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_code; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryRendererGrammarListener ) ((UIEntryRendererGrammarListener)listener).enterCode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryRendererGrammarListener ) ((UIEntryRendererGrammarListener)listener).exitCode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryRendererGrammarVisitor ) return ((UIEntryRendererGrammarVisitor<? extends T>)visitor).visitCode(this);
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
			((CodeContext)_localctx).FLAG = match(FLAG);

			                ((CodeContext)_localctx).codeStr =  ((CodeContext)_localctx).FLAG.getText();
			            
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ColoursContext extends ParserRuleContext {
		public String colourCharStr;
		public Token COLOURCHARS;
		public TerminalNode COLOURS() { return getToken(UIEntryRendererGrammar.COLOURS, 0); }
		public TerminalNode COLOURCHARS() { return getToken(UIEntryRendererGrammar.COLOURCHARS, 0); }
		public ColoursContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_colours; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryRendererGrammarListener ) ((UIEntryRendererGrammarListener)listener).enterColours(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryRendererGrammarListener ) ((UIEntryRendererGrammarListener)listener).exitColours(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryRendererGrammarVisitor ) return ((UIEntryRendererGrammarVisitor<? extends T>)visitor).visitColours(this);
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
			((ColoursContext)_localctx).COLOURCHARS = match(COLOURCHARS);

			                ((ColoursContext)_localctx).colourCharStr =  ((ColoursContext)_localctx).COLOURCHARS.getText();
			            
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LabelcoloursContext extends ParserRuleContext {
		public String colourCharStr;
		public Token COLOURCHARS;
		public TerminalNode LABELCOLOURS() { return getToken(UIEntryRendererGrammar.LABELCOLOURS, 0); }
		public TerminalNode COLOURCHARS() { return getToken(UIEntryRendererGrammar.COLOURCHARS, 0); }
		public LabelcoloursContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_labelcolours; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryRendererGrammarListener ) ((UIEntryRendererGrammarListener)listener).enterLabelcolours(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryRendererGrammarListener ) ((UIEntryRendererGrammarListener)listener).exitLabelcolours(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryRendererGrammarVisitor ) return ((UIEntryRendererGrammarVisitor<? extends T>)visitor).visitLabelcolours(this);
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
			((LabelcoloursContext)_localctx).COLOURCHARS = match(COLOURCHARS);

			                ((LabelcoloursContext)_localctx).colourCharStr =  ((LabelcoloursContext)_localctx).COLOURCHARS.getText();
			            
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SymbolsContext extends ParserRuleContext {
		public String symbolCharsStr;
		public Token STRING;
		public TerminalNode SYMBOLS() { return getToken(UIEntryRendererGrammar.SYMBOLS, 0); }
		public TerminalNode STRING() { return getToken(UIEntryRendererGrammar.STRING, 0); }
		public SymbolsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_symbols; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryRendererGrammarListener ) ((UIEntryRendererGrammarListener)listener).enterSymbols(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryRendererGrammarListener ) ((UIEntryRendererGrammarListener)listener).exitSymbols(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryRendererGrammarVisitor ) return ((UIEntryRendererGrammarVisitor<? extends T>)visitor).visitSymbols(this);
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
			((SymbolsContext)_localctx).STRING = match(STRING);

			                ((SymbolsContext)_localctx).symbolCharsStr =  ((SymbolsContext)_localctx).STRING.getText();
			            
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NdigitContext extends ParserRuleContext {
		public String numDigitsStr;
		public Token INTEGER;
		public TerminalNode NDIGITS() { return getToken(UIEntryRendererGrammar.NDIGITS, 0); }
		public TerminalNode INTEGER() { return getToken(UIEntryRendererGrammar.INTEGER, 0); }
		public NdigitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ndigit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryRendererGrammarListener ) ((UIEntryRendererGrammarListener)listener).enterNdigit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryRendererGrammarListener ) ((UIEntryRendererGrammarListener)listener).exitNdigit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryRendererGrammarVisitor ) return ((UIEntryRendererGrammarVisitor<? extends T>)visitor).visitNdigit(this);
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
			((NdigitContext)_localctx).INTEGER = match(INTEGER);

			                ((NdigitContext)_localctx).numDigitsStr =  ((NdigitContext)_localctx).INTEGER.getText();
			            
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SignContext extends ParserRuleContext {
		public String signEnum;
		public Token FLAG;
		public TerminalNode SIGN() { return getToken(UIEntryRendererGrammar.SIGN, 0); }
		public TerminalNode FLAG() { return getToken(UIEntryRendererGrammar.FLAG, 0); }
		public SignContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sign; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryRendererGrammarListener ) ((UIEntryRendererGrammarListener)listener).enterSign(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryRendererGrammarListener ) ((UIEntryRendererGrammarListener)listener).exitSign(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryRendererGrammarVisitor ) return ((UIEntryRendererGrammarVisitor<? extends T>)visitor).visitSign(this);
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
			((SignContext)_localctx).FLAG = match(FLAG);

			                ((SignContext)_localctx).signEnum =  ((SignContext)_localctx).FLAG.getText();
			            
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class UiEntryContext extends ParserRuleContext {
		public List<String> renderer;
		public NameContext name;
		public CodeContext code;
		public ColoursContext colours;
		public LabelcoloursContext labelcolours;
		public SymbolsContext symbols;
		public NdigitContext ndigit;
		public SignContext sign;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public CodeContext code() {
			return getRuleContext(CodeContext.class,0);
		}
		public ColoursContext colours() {
			return getRuleContext(ColoursContext.class,0);
		}
		public LabelcoloursContext labelcolours() {
			return getRuleContext(LabelcoloursContext.class,0);
		}
		public SymbolsContext symbols() {
			return getRuleContext(SymbolsContext.class,0);
		}
		public NdigitContext ndigit() {
			return getRuleContext(NdigitContext.class,0);
		}
		public SignContext sign() {
			return getRuleContext(SignContext.class,0);
		}
		public UiEntryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_uiEntry; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryRendererGrammarListener ) ((UIEntryRendererGrammarListener)listener).enterUiEntry(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryRendererGrammarListener ) ((UIEntryRendererGrammarListener)listener).exitUiEntry(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryRendererGrammarVisitor ) return ((UIEntryRendererGrammarVisitor<? extends T>)visitor).visitUiEntry(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UiEntryContext uiEntry() throws RecognitionException {
		UiEntryContext _localctx = new UiEntryContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_uiEntry);

		            String[] uiEntryRendererStrings = new String[7];
		            Arrays.fill(uiEntryRendererStrings, "");
		            int lineNumber = -1;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			((UiEntryContext)_localctx).name = name();
			   uiEntryRendererStrings[0] = ((UiEntryContext)_localctx).name.nameStr;
			                lineNumber = _localctx.start.getLine();
			            
			setState(48);
			((UiEntryContext)_localctx).code = code();
			 uiEntryRendererStrings[1] = ((UiEntryContext)_localctx).code.codeStr; 
			setState(53);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLOURS) {
				{
				setState(50);
				((UiEntryContext)_localctx).colours = colours();
				 uiEntryRendererStrings[2] = ((UiEntryContext)_localctx).colours.colourCharStr; 
				}
			}

			setState(58);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LABELCOLOURS) {
				{
				setState(55);
				((UiEntryContext)_localctx).labelcolours = labelcolours();
				 uiEntryRendererStrings[3] = ((UiEntryContext)_localctx).labelcolours.colourCharStr; 
				}
			}

			setState(63);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SYMBOLS) {
				{
				setState(60);
				((UiEntryContext)_localctx).symbols = symbols();
				 uiEntryRendererStrings[4] = ((UiEntryContext)_localctx).symbols.symbolCharsStr; 
				}
			}

			setState(68);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NDIGITS) {
				{
				setState(65);
				((UiEntryContext)_localctx).ndigit = ndigit();
				 uiEntryRendererStrings[5] = ((UiEntryContext)_localctx).ndigit.numDigitsStr; 
				}
			}

			setState(73);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SIGN) {
				{
				setState(70);
				((UiEntryContext)_localctx).sign = sign();
				 uiEntryRendererStrings[6] = ((UiEntryContext)_localctx).sign.signEnum; 
				}
			}

			}
			_ctx.stop = _input.LT(-1);

			            ((UiEntryContext)_localctx).renderer =  new ArrayList<>(Arrays.asList(uiEntryRendererStrings));
			            _localctx.renderer.add(String.valueOf(lineNumber));
			        
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FileContext extends ParserRuleContext {
		public List<List<String>> renderers;
		public UiEntryContext uiEntry;
		public TerminalNode EOF() { return getToken(UIEntryRendererGrammar.EOF, 0); }
		public List<UiEntryContext> uiEntry() {
			return getRuleContexts(UiEntryContext.class);
		}
		public UiEntryContext uiEntry(int i) {
			return getRuleContext(UiEntryContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryRendererGrammarListener ) ((UIEntryRendererGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryRendererGrammarListener ) ((UIEntryRendererGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryRendererGrammarVisitor ) return ((UIEntryRendererGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_file);

		            ((FileContext)_localctx).renderers =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(78); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(75);
				((FileContext)_localctx).uiEntry = uiEntry();

				                _localctx.renderers.add(((FileContext)_localctx).uiEntry.renderer);
				            
				}
				}
				setState(80); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(82);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001\rU\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0003\u00076\b\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007"+
		";\b\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007@\b\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0003\u0007E\b\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0003\u0007J\b\u0007\u0001\b\u0001\b\u0001\b\u0004"+
		"\bO\b\b\u000b\b\f\bP\u0001\b\u0001\b\u0001\b\u0000\u0000\t\u0000\u0002"+
		"\u0004\u0006\b\n\f\u000e\u0010\u0000\u0000Q\u0000\u0012\u0001\u0000\u0000"+
		"\u0000\u0002\u0016\u0001\u0000\u0000\u0000\u0004\u001a\u0001\u0000\u0000"+
		"\u0000\u0006\u001e\u0001\u0000\u0000\u0000\b\"\u0001\u0000\u0000\u0000"+
		"\n&\u0001\u0000\u0000\u0000\f*\u0001\u0000\u0000\u0000\u000e.\u0001\u0000"+
		"\u0000\u0000\u0010N\u0001\u0000\u0000\u0000\u0012\u0013\u0005\u0001\u0000"+
		"\u0000\u0013\u0014\u0005\r\u0000\u0000\u0014\u0015\u0006\u0000\uffff\uffff"+
		"\u0000\u0015\u0001\u0001\u0000\u0000\u0000\u0016\u0017\u0005\u0002\u0000"+
		"\u0000\u0017\u0018\u0005\f\u0000\u0000\u0018\u0019\u0006\u0001\uffff\uffff"+
		"\u0000\u0019\u0003\u0001\u0000\u0000\u0000\u001a\u001b\u0005\u0003\u0000"+
		"\u0000\u001b\u001c\u0005\b\u0000\u0000\u001c\u001d\u0006\u0002\uffff\uffff"+
		"\u0000\u001d\u0005\u0001\u0000\u0000\u0000\u001e\u001f\u0005\u0004\u0000"+
		"\u0000\u001f \u0005\b\u0000\u0000 !\u0006\u0003\uffff\uffff\u0000!\u0007"+
		"\u0001\u0000\u0000\u0000\"#\u0005\u0005\u0000\u0000#$\u0005\r\u0000\u0000"+
		"$%\u0006\u0004\uffff\uffff\u0000%\t\u0001\u0000\u0000\u0000&\'\u0005\u0006"+
		"\u0000\u0000\'(\u0005\t\u0000\u0000()\u0006\u0005\uffff\uffff\u0000)\u000b"+
		"\u0001\u0000\u0000\u0000*+\u0005\u0007\u0000\u0000+,\u0005\f\u0000\u0000"+
		",-\u0006\u0006\uffff\uffff\u0000-\r\u0001\u0000\u0000\u0000./\u0003\u0000"+
		"\u0000\u0000/0\u0006\u0007\uffff\uffff\u000001\u0003\u0002\u0001\u0000"+
		"15\u0006\u0007\uffff\uffff\u000023\u0003\u0004\u0002\u000034\u0006\u0007"+
		"\uffff\uffff\u000046\u0001\u0000\u0000\u000052\u0001\u0000\u0000\u0000"+
		"56\u0001\u0000\u0000\u00006:\u0001\u0000\u0000\u000078\u0003\u0006\u0003"+
		"\u000089\u0006\u0007\uffff\uffff\u00009;\u0001\u0000\u0000\u0000:7\u0001"+
		"\u0000\u0000\u0000:;\u0001\u0000\u0000\u0000;?\u0001\u0000\u0000\u0000"+
		"<=\u0003\b\u0004\u0000=>\u0006\u0007\uffff\uffff\u0000>@\u0001\u0000\u0000"+
		"\u0000?<\u0001\u0000\u0000\u0000?@\u0001\u0000\u0000\u0000@D\u0001\u0000"+
		"\u0000\u0000AB\u0003\n\u0005\u0000BC\u0006\u0007\uffff\uffff\u0000CE\u0001"+
		"\u0000\u0000\u0000DA\u0001\u0000\u0000\u0000DE\u0001\u0000\u0000\u0000"+
		"EI\u0001\u0000\u0000\u0000FG\u0003\f\u0006\u0000GH\u0006\u0007\uffff\uffff"+
		"\u0000HJ\u0001\u0000\u0000\u0000IF\u0001\u0000\u0000\u0000IJ\u0001\u0000"+
		"\u0000\u0000J\u000f\u0001\u0000\u0000\u0000KL\u0003\u000e\u0007\u0000"+
		"LM\u0006\b\uffff\uffff\u0000MO\u0001\u0000\u0000\u0000NK\u0001\u0000\u0000"+
		"\u0000OP\u0001\u0000\u0000\u0000PN\u0001\u0000\u0000\u0000PQ\u0001\u0000"+
		"\u0000\u0000QR\u0001\u0000\u0000\u0000RS\u0005\u0000\u0000\u0001S\u0011"+
		"\u0001\u0000\u0000\u0000\u00065:?DIP";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}