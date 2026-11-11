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

// Generated from UIEntryBaseGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.frontend.ui.entrybase.antlr4.uientrybase;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.frontend.ui.entrybase.assembler.UIEntryBaseParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class UIEntryBaseGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, RENDERER=2, COMBINE=3, CATEGORY=4, FLAGS=5, DESC=6, INTEGER=7, 
		LCASE=8, COMMENT=9, INDENT=10, EOL=11, FLAG=12, OR=13, FLAG_EOL=14, STRING=15;
	public static final int
		RULE_name = 0, RULE_renderer = 1, RULE_combine = 2, RULE_category = 3, 
		RULE_flags = 4, RULE_desc = 5, RULE_entryBase = 6, RULE_file = 7;
	private static String[] makeRuleNames() {
		return new String[] {
			"name", "renderer", "combine", "category", "flags", "desc", "entryBase", 
			"file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'renderer:'", "'combine:'", "'category:'", "'flags:'", 
			"'desc:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "RENDERER", "COMBINE", "CATEGORY", "FLAGS", "DESC", "INTEGER", 
			"LCASE", "COMMENT", "INDENT", "EOL", "FLAG", "OR", "FLAG_EOL", "STRING"
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
	public String getGrammarFileName() { return "UIEntryBaseGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public UIEntryBaseGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public Token LCASE;
		public TerminalNode NAME() { return getToken(UIEntryBaseGrammar.NAME, 0); }
		public TerminalNode LCASE() { return getToken(UIEntryBaseGrammar.LCASE, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryBaseGrammarListener ) ((UIEntryBaseGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryBaseGrammarListener ) ((UIEntryBaseGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryBaseGrammarVisitor ) return ((UIEntryBaseGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(16);
			match(NAME);
			setState(17);
			((NameContext)_localctx).LCASE = match(LCASE);
			 ((NameContext)_localctx).nameStr =  ((NameContext)_localctx).LCASE.getText(); 
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
	public static class RendererContext extends ParserRuleContext {
		public String entryRenderer;
		public Token LCASE;
		public TerminalNode RENDERER() { return getToken(UIEntryBaseGrammar.RENDERER, 0); }
		public TerminalNode LCASE() { return getToken(UIEntryBaseGrammar.LCASE, 0); }
		public RendererContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_renderer; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryBaseGrammarListener ) ((UIEntryBaseGrammarListener)listener).enterRenderer(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryBaseGrammarListener ) ((UIEntryBaseGrammarListener)listener).exitRenderer(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryBaseGrammarVisitor ) return ((UIEntryBaseGrammarVisitor<? extends T>)visitor).visitRenderer(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RendererContext renderer() throws RecognitionException {
		RendererContext _localctx = new RendererContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_renderer);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(20);
			match(RENDERER);
			setState(21);
			((RendererContext)_localctx).LCASE = match(LCASE);

			                ((RendererContext)_localctx).entryRenderer =  ((RendererContext)_localctx).LCASE.getText();
			            
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
	public static class CombineContext extends ParserRuleContext {
		public String combinerEnum;
		public Token FLAG;
		public TerminalNode COMBINE() { return getToken(UIEntryBaseGrammar.COMBINE, 0); }
		public TerminalNode FLAG() { return getToken(UIEntryBaseGrammar.FLAG, 0); }
		public CombineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_combine; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryBaseGrammarListener ) ((UIEntryBaseGrammarListener)listener).enterCombine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryBaseGrammarListener ) ((UIEntryBaseGrammarListener)listener).exitCombine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryBaseGrammarVisitor ) return ((UIEntryBaseGrammarVisitor<? extends T>)visitor).visitCombine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CombineContext combine() throws RecognitionException {
		CombineContext _localctx = new CombineContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_combine);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(24);
			match(COMBINE);
			setState(25);
			((CombineContext)_localctx).FLAG = match(FLAG);

			                ((CombineContext)_localctx).combinerEnum =  ((CombineContext)_localctx).FLAG.getText();
			            
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
	public static class CategoryContext extends ParserRuleContext {
		public List<String> categoryStr;
		public Token STRING;
		public List<TerminalNode> CATEGORY() { return getTokens(UIEntryBaseGrammar.CATEGORY); }
		public TerminalNode CATEGORY(int i) {
			return getToken(UIEntryBaseGrammar.CATEGORY, i);
		}
		public List<TerminalNode> STRING() { return getTokens(UIEntryBaseGrammar.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(UIEntryBaseGrammar.STRING, i);
		}
		public CategoryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_category; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryBaseGrammarListener ) ((UIEntryBaseGrammarListener)listener).enterCategory(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryBaseGrammarListener ) ((UIEntryBaseGrammarListener)listener).exitCategory(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryBaseGrammarVisitor ) return ((UIEntryBaseGrammarVisitor<? extends T>)visitor).visitCategory(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CategoryContext category() throws RecognitionException {
		CategoryContext _localctx = new CategoryContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_category);

		            ((CategoryContext)_localctx).categoryStr =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(31); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(28);
				match(CATEGORY);
				setState(29);
				((CategoryContext)_localctx).STRING = match(STRING);
				 _localctx.categoryStr.add(((CategoryContext)_localctx).STRING.getText()); 
				}
				}
				setState(33); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==CATEGORY );
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
	public static class FlagsContext extends ParserRuleContext {
		public String flagsStr;
		public Token FLAG;
		public TerminalNode FLAGS() { return getToken(UIEntryBaseGrammar.FLAGS, 0); }
		public TerminalNode FLAG() { return getToken(UIEntryBaseGrammar.FLAG, 0); }
		public FlagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flags; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryBaseGrammarListener ) ((UIEntryBaseGrammarListener)listener).enterFlags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryBaseGrammarListener ) ((UIEntryBaseGrammarListener)listener).exitFlags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryBaseGrammarVisitor ) return ((UIEntryBaseGrammarVisitor<? extends T>)visitor).visitFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsContext flags() throws RecognitionException {
		FlagsContext _localctx = new FlagsContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_flags);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(35);
			match(FLAGS);
			setState(36);
			((FlagsContext)_localctx).FLAG = match(FLAG);

			                ((FlagsContext)_localctx).flagsStr =  ((FlagsContext)_localctx).FLAG.getText();
			            
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
	public static class DescContext extends ParserRuleContext {
		public String descStr;
		public Token STRING;
		public List<TerminalNode> DESC() { return getTokens(UIEntryBaseGrammar.DESC); }
		public TerminalNode DESC(int i) {
			return getToken(UIEntryBaseGrammar.DESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(UIEntryBaseGrammar.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(UIEntryBaseGrammar.STRING, i);
		}
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_desc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryBaseGrammarListener ) ((UIEntryBaseGrammarListener)listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryBaseGrammarListener ) ((UIEntryBaseGrammarListener)listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryBaseGrammarVisitor ) return ((UIEntryBaseGrammarVisitor<? extends T>)visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_desc);
		 StringBuilder sb = new StringBuilder(); 
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(42); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(39);
				match(DESC);
				setState(40);
				((DescContext)_localctx).STRING = match(STRING);
				 sb.append(((DescContext)_localctx).STRING.getText()); 
				}
				}
				setState(44); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==DESC );
			}
			_ctx.stop = _input.LT(-1);
			 ((DescContext)_localctx).descStr =  sb.toString(); 
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
	public static class EntryBaseContext extends ParserRuleContext {
		public UIEntryBaseParseRecord base;
		public NameContext name;
		public RendererContext renderer;
		public CombineContext combine;
		public CategoryContext category;
		public FlagsContext flags;
		public DescContext desc;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public RendererContext renderer() {
			return getRuleContext(RendererContext.class,0);
		}
		public CombineContext combine() {
			return getRuleContext(CombineContext.class,0);
		}
		public CategoryContext category() {
			return getRuleContext(CategoryContext.class,0);
		}
		public FlagsContext flags() {
			return getRuleContext(FlagsContext.class,0);
		}
		public DescContext desc() {
			return getRuleContext(DescContext.class,0);
		}
		public EntryBaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_entryBase; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryBaseGrammarListener ) ((UIEntryBaseGrammarListener)listener).enterEntryBase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryBaseGrammarListener ) ((UIEntryBaseGrammarListener)listener).exitEntryBase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryBaseGrammarVisitor ) return ((UIEntryBaseGrammarVisitor<? extends T>)visitor).visitEntryBase(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EntryBaseContext entryBase() throws RecognitionException {
		EntryBaseContext _localctx = new EntryBaseContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_entryBase);

		            String nameInit = "";
		            String rendererInit = "";
		            String combinerInit = "";
		            List<String> categoryInit = new ArrayList<>();
		            String flagsInit = "";
		            String descInit = "";
		            int lineNumber = 0;
		        
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			((EntryBaseContext)_localctx).name = name();

			                nameInit = ((EntryBaseContext)_localctx).name.nameStr;
			                lineNumber = _localctx.start.getLine();
			            
			setState(48);
			((EntryBaseContext)_localctx).renderer = renderer();

			                rendererInit = ((EntryBaseContext)_localctx).renderer.entryRenderer;
			            
			setState(50);
			((EntryBaseContext)_localctx).combine = combine();

			                combinerInit = ((EntryBaseContext)_localctx).combine.combinerEnum;
			            
			setState(52);
			((EntryBaseContext)_localctx).category = category();

			                categoryInit = ((EntryBaseContext)_localctx).category.categoryStr;
			            
			setState(54);
			((EntryBaseContext)_localctx).flags = flags();

			                flagsInit = ((EntryBaseContext)_localctx).flags.flagsStr;
			            
			setState(56);
			((EntryBaseContext)_localctx).desc = desc();

			                descInit = ((EntryBaseContext)_localctx).desc.descStr;
			            
			}
			_ctx.stop = _input.LT(-1);

			            ((EntryBaseContext)_localctx).base =  new UIEntryBaseParseRecord(
			                        nameInit, rendererInit,
			                        combinerInit, flagsInit,
			                        descInit, categoryInit,
			                        lineNumber);
			        
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
		public List<UIEntryBaseParseRecord> uiEntryBases;
		public EntryBaseContext entryBase;
		public TerminalNode EOF() { return getToken(UIEntryBaseGrammar.EOF, 0); }
		public List<EntryBaseContext> entryBase() {
			return getRuleContexts(EntryBaseContext.class);
		}
		public EntryBaseContext entryBase(int i) {
			return getRuleContext(EntryBaseContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryBaseGrammarListener ) ((UIEntryBaseGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UIEntryBaseGrammarListener ) ((UIEntryBaseGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UIEntryBaseGrammarVisitor ) return ((UIEntryBaseGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_file);

		            ((FileContext)_localctx).uiEntryBases =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(62); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(59);
				((FileContext)_localctx).entryBase = entryBase();

				            _localctx.uiEntryBases.add(((FileContext)_localctx).entryBase.base);
				        
				}
				}
				setState(64); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(66);
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
		"\u0004\u0001\u000fE\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0004\u0003 \b\u0003\u000b\u0003\f\u0003"+
		"!\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0004\u0005+\b\u0005\u000b\u0005\f\u0005,\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0004\u0007?\b\u0007\u000b\u0007\f\u0007"+
		"@\u0001\u0007\u0001\u0007\u0001\u0007\u0000\u0000\b\u0000\u0002\u0004"+
		"\u0006\b\n\f\u000e\u0000\u0000?\u0000\u0010\u0001\u0000\u0000\u0000\u0002"+
		"\u0014\u0001\u0000\u0000\u0000\u0004\u0018\u0001\u0000\u0000\u0000\u0006"+
		"\u001f\u0001\u0000\u0000\u0000\b#\u0001\u0000\u0000\u0000\n*\u0001\u0000"+
		"\u0000\u0000\f.\u0001\u0000\u0000\u0000\u000e>\u0001\u0000\u0000\u0000"+
		"\u0010\u0011\u0005\u0001\u0000\u0000\u0011\u0012\u0005\b\u0000\u0000\u0012"+
		"\u0013\u0006\u0000\uffff\uffff\u0000\u0013\u0001\u0001\u0000\u0000\u0000"+
		"\u0014\u0015\u0005\u0002\u0000\u0000\u0015\u0016\u0005\b\u0000\u0000\u0016"+
		"\u0017\u0006\u0001\uffff\uffff\u0000\u0017\u0003\u0001\u0000\u0000\u0000"+
		"\u0018\u0019\u0005\u0003\u0000\u0000\u0019\u001a\u0005\f\u0000\u0000\u001a"+
		"\u001b\u0006\u0002\uffff\uffff\u0000\u001b\u0005\u0001\u0000\u0000\u0000"+
		"\u001c\u001d\u0005\u0004\u0000\u0000\u001d\u001e\u0005\u000f\u0000\u0000"+
		"\u001e \u0006\u0003\uffff\uffff\u0000\u001f\u001c\u0001\u0000\u0000\u0000"+
		" !\u0001\u0000\u0000\u0000!\u001f\u0001\u0000\u0000\u0000!\"\u0001\u0000"+
		"\u0000\u0000\"\u0007\u0001\u0000\u0000\u0000#$\u0005\u0005\u0000\u0000"+
		"$%\u0005\f\u0000\u0000%&\u0006\u0004\uffff\uffff\u0000&\t\u0001\u0000"+
		"\u0000\u0000\'(\u0005\u0006\u0000\u0000()\u0005\u000f\u0000\u0000)+\u0006"+
		"\u0005\uffff\uffff\u0000*\'\u0001\u0000\u0000\u0000+,\u0001\u0000\u0000"+
		"\u0000,*\u0001\u0000\u0000\u0000,-\u0001\u0000\u0000\u0000-\u000b\u0001"+
		"\u0000\u0000\u0000./\u0003\u0000\u0000\u0000/0\u0006\u0006\uffff\uffff"+
		"\u000001\u0003\u0002\u0001\u000012\u0006\u0006\uffff\uffff\u000023\u0003"+
		"\u0004\u0002\u000034\u0006\u0006\uffff\uffff\u000045\u0003\u0006\u0003"+
		"\u000056\u0006\u0006\uffff\uffff\u000067\u0003\b\u0004\u000078\u0006\u0006"+
		"\uffff\uffff\u000089\u0003\n\u0005\u00009:\u0006\u0006\uffff\uffff\u0000"+
		":\r\u0001\u0000\u0000\u0000;<\u0003\f\u0006\u0000<=\u0006\u0007\uffff"+
		"\uffff\u0000=?\u0001\u0000\u0000\u0000>;\u0001\u0000\u0000\u0000?@\u0001"+
		"\u0000\u0000\u0000@>\u0001\u0000\u0000\u0000@A\u0001\u0000\u0000\u0000"+
		"AB\u0001\u0000\u0000\u0000BC\u0005\u0000\u0000\u0001C\u000f\u0001\u0000"+
		"\u0000\u0000\u0003!,@";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}