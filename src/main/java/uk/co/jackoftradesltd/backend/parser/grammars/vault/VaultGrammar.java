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

// Generated from VaultGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.vault;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.backend.parser.grammars.Directive;
import uk.co.jackoftradesltd.backend.parser.vault.VaultParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class VaultGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, TYPE=2, RATING=3, ROWS=4, COLUMNS=5, MIN_DEPTH=6, MAX_DEPTH=7, 
		FLAGS=8, D=9, INTEGER=10, COMMENT=11, INDENT=12, EOL=13, FLAG=14, OR=15, 
		FLAG_EOL=16, STRING=17, END_OF_LINE=18;
	public static final int
		RULE_name = 0, RULE_type = 1, RULE_rating = 2, RULE_rows = 3, RULE_columns = 4, 
		RULE_minDepth = 5, RULE_maxDepth = 6, RULE_flag = 7, RULE_d = 8, RULE_profileRecord = 9, 
		RULE_file = 10;
	private static String[] makeRuleNames() {
		return new String[] {
			"name", "type", "rating", "rows", "columns", "minDepth", "maxDepth", 
			"flag", "d", "profileRecord", "file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'type:'", "'rating:'", "'rows:'", "'columns:'", "'min-depth:'", 
			"'max-depth:'", "'flags:'", "'D:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "TYPE", "RATING", "ROWS", "COLUMNS", "MIN_DEPTH", "MAX_DEPTH", 
			"FLAGS", "D", "INTEGER", "COMMENT", "INDENT", "EOL", "FLAG", "OR", "FLAG_EOL", 
			"STRING", "END_OF_LINE"
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
	public String getGrammarFileName() { return "VaultGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public VaultGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public Directive nameDirective;
		public Token n;
		public TerminalNode NAME() { return getToken(VaultGrammar.NAME, 0); }
		public TerminalNode STRING() { return getToken(VaultGrammar.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VaultGrammarVisitor ) return ((VaultGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(22);
			match(NAME);
			setState(23);
			((NameContext)_localctx).n = match(STRING);

			                String value = ((NameContext)_localctx).n.getText();
			                List<String> values = new ArrayList<>();
			                values.add(value);
			                ((NameContext)_localctx).nameDirective =  new Directive("name", values, _localctx.start.getLine());
			            
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
	public static class TypeContext extends ParserRuleContext {
		public Directive typeDirective;
		public Token t;
		public TerminalNode TYPE() { return getToken(VaultGrammar.TYPE, 0); }
		public TerminalNode STRING() { return getToken(VaultGrammar.STRING, 0); }
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).exitType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VaultGrammarVisitor ) return ((VaultGrammarVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(26);
			match(TYPE);
			setState(27);
			((TypeContext)_localctx).t = match(STRING);
			 
			                String value = ((TypeContext)_localctx).t.getText();
			                List<String> values = new ArrayList<>();
			                values.add(value);
			                ((TypeContext)_localctx).typeDirective =  new Directive("type", values, _localctx.start.getLine()); 
			            
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
	public static class RatingContext extends ParserRuleContext {
		public Directive ratingDirective;
		public Token r;
		public TerminalNode RATING() { return getToken(VaultGrammar.RATING, 0); }
		public TerminalNode INTEGER() { return getToken(VaultGrammar.INTEGER, 0); }
		public RatingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rating; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).enterRating(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).exitRating(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VaultGrammarVisitor ) return ((VaultGrammarVisitor<? extends T>)visitor).visitRating(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RatingContext rating() throws RecognitionException {
		RatingContext _localctx = new RatingContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_rating);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(30);
			match(RATING);
			setState(31);
			((RatingContext)_localctx).r = match(INTEGER);

			                String value = ((RatingContext)_localctx).r.getText();
			                List<String> values = new ArrayList<>();
			                values.add(value);
			                ((RatingContext)_localctx).ratingDirective =  new Directive("rating", values, _localctx.start.getLine());
			            
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
	public static class RowsContext extends ParserRuleContext {
		public Directive rowsDirective;
		public Token r;
		public TerminalNode ROWS() { return getToken(VaultGrammar.ROWS, 0); }
		public TerminalNode INTEGER() { return getToken(VaultGrammar.INTEGER, 0); }
		public RowsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rows; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).enterRows(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).exitRows(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VaultGrammarVisitor ) return ((VaultGrammarVisitor<? extends T>)visitor).visitRows(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RowsContext rows() throws RecognitionException {
		RowsContext _localctx = new RowsContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_rows);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(34);
			match(ROWS);
			setState(35);
			((RowsContext)_localctx).r = match(INTEGER);

			                String value = ((RowsContext)_localctx).r.getText();
			                List<String> values = new ArrayList<>();
			                values.add(value);
			                ((RowsContext)_localctx).rowsDirective =  new Directive("rows", values, _localctx.start.getLine());
			            
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
	public static class ColumnsContext extends ParserRuleContext {
		public Directive colsDirective;
		public Token c;
		public TerminalNode COLUMNS() { return getToken(VaultGrammar.COLUMNS, 0); }
		public TerminalNode INTEGER() { return getToken(VaultGrammar.INTEGER, 0); }
		public ColumnsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_columns; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).enterColumns(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).exitColumns(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VaultGrammarVisitor ) return ((VaultGrammarVisitor<? extends T>)visitor).visitColumns(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ColumnsContext columns() throws RecognitionException {
		ColumnsContext _localctx = new ColumnsContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_columns);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(38);
			match(COLUMNS);
			setState(39);
			((ColumnsContext)_localctx).c = match(INTEGER);

			                String value = ((ColumnsContext)_localctx).c.getText();
			                List<String> values = new ArrayList<>();
			                values.add(value);
			                ((ColumnsContext)_localctx).colsDirective =  new Directive("columns", values, _localctx.start.getLine());
			            
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
	public static class MinDepthContext extends ParserRuleContext {
		public Directive minDepthDirective;
		public Token m;
		public TerminalNode MIN_DEPTH() { return getToken(VaultGrammar.MIN_DEPTH, 0); }
		public TerminalNode INTEGER() { return getToken(VaultGrammar.INTEGER, 0); }
		public MinDepthContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_minDepth; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).enterMinDepth(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).exitMinDepth(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VaultGrammarVisitor ) return ((VaultGrammarVisitor<? extends T>)visitor).visitMinDepth(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MinDepthContext minDepth() throws RecognitionException {
		MinDepthContext _localctx = new MinDepthContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_minDepth);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(42);
			match(MIN_DEPTH);
			setState(43);
			((MinDepthContext)_localctx).m = match(INTEGER);

			                String value = ((MinDepthContext)_localctx).m.getText();
			                List<String> values = new ArrayList<>();
			                values.add(value);
			                ((MinDepthContext)_localctx).minDepthDirective =  new Directive("min-depth", values, _localctx.start.getLine());
			            
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
	public static class MaxDepthContext extends ParserRuleContext {
		public Directive maxDepthDirective;
		public Token m;
		public TerminalNode MAX_DEPTH() { return getToken(VaultGrammar.MAX_DEPTH, 0); }
		public TerminalNode INTEGER() { return getToken(VaultGrammar.INTEGER, 0); }
		public MaxDepthContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_maxDepth; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).enterMaxDepth(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).exitMaxDepth(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VaultGrammarVisitor ) return ((VaultGrammarVisitor<? extends T>)visitor).visitMaxDepth(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MaxDepthContext maxDepth() throws RecognitionException {
		MaxDepthContext _localctx = new MaxDepthContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_maxDepth);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			match(MAX_DEPTH);
			setState(47);
			((MaxDepthContext)_localctx).m = match(INTEGER);

			                String value = ((MaxDepthContext)_localctx).m.getText();
			                List<String> values = new ArrayList<>();
			                values.add(value);
			                ((MaxDepthContext)_localctx).maxDepthDirective =  new Directive("max-depth", values, _localctx.start.getLine());        
			            
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
	public static class FlagContext extends ParserRuleContext {
		public Directive flagDirective;
		public Token f1;
		public Token f2;
		public TerminalNode FLAGS() { return getToken(VaultGrammar.FLAGS, 0); }
		public List<TerminalNode> OR() { return getTokens(VaultGrammar.OR); }
		public TerminalNode OR(int i) {
			return getToken(VaultGrammar.OR, i);
		}
		public List<TerminalNode> FLAG() { return getTokens(VaultGrammar.FLAG); }
		public TerminalNode FLAG(int i) {
			return getToken(VaultGrammar.FLAG, i);
		}
		public FlagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).enterFlag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).exitFlag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VaultGrammarVisitor ) return ((VaultGrammarVisitor<? extends T>)visitor).visitFlag(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagContext flag() throws RecognitionException {
		FlagContext _localctx = new FlagContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_flag);
		 List<String> flags = new ArrayList<>(); 
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(50);
			match(FLAGS);
			setState(58);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(52); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(51);
						match(OR);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(54); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				{
				setState(56);
				((FlagContext)_localctx).f1 = match(FLAG);
				 flags.add(((FlagContext)_localctx).f1.getText()); 
				}
				break;
			}
			setState(67);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OR) {
				{
				{
				setState(60);
				match(OR);
				setState(63);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FLAG) {
					{
					setState(61);
					((FlagContext)_localctx).f2 = match(FLAG);
					 flags.add(((FlagContext)_localctx).f2.getText()); 
					}
				}

				}
				}
				setState(69);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			_ctx.stop = _input.LT(-1);

			                ((FlagContext)_localctx).flagDirective =  new Directive("flag", flags, _localctx.start.getLine());
			        
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
	public static class DContext extends ParserRuleContext {
		public Directive mapDirective;
		public Token m;
		public TerminalNode D() { return getToken(VaultGrammar.D, 0); }
		public TerminalNode STRING() { return getToken(VaultGrammar.STRING, 0); }
		public DContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_d; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).enterD(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).exitD(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VaultGrammarVisitor ) return ((VaultGrammarVisitor<? extends T>)visitor).visitD(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DContext d() throws RecognitionException {
		DContext _localctx = new DContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_d);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70);
			match(D);
			setState(71);
			((DContext)_localctx).m = match(STRING);

			                String value = ((DContext)_localctx).m.getText();
			                List<String> values = new ArrayList<>();
			                values.add(value);
			                ((DContext)_localctx).mapDirective =  new Directive("map", values, _localctx.start.getLine());
			            
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
	public static class ProfileRecordContext extends ParserRuleContext {
		public List<Directive> vault;
		public NameContext name;
		public TypeContext type;
		public RatingContext rating;
		public RowsContext rows;
		public ColumnsContext columns;
		public MinDepthContext minDepth;
		public MaxDepthContext maxDepth;
		public FlagContext flag;
		public DContext d;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}
		public TypeContext type(int i) {
			return getRuleContext(TypeContext.class,i);
		}
		public List<RatingContext> rating() {
			return getRuleContexts(RatingContext.class);
		}
		public RatingContext rating(int i) {
			return getRuleContext(RatingContext.class,i);
		}
		public List<RowsContext> rows() {
			return getRuleContexts(RowsContext.class);
		}
		public RowsContext rows(int i) {
			return getRuleContext(RowsContext.class,i);
		}
		public List<ColumnsContext> columns() {
			return getRuleContexts(ColumnsContext.class);
		}
		public ColumnsContext columns(int i) {
			return getRuleContext(ColumnsContext.class,i);
		}
		public List<MinDepthContext> minDepth() {
			return getRuleContexts(MinDepthContext.class);
		}
		public MinDepthContext minDepth(int i) {
			return getRuleContext(MinDepthContext.class,i);
		}
		public List<MaxDepthContext> maxDepth() {
			return getRuleContexts(MaxDepthContext.class);
		}
		public MaxDepthContext maxDepth(int i) {
			return getRuleContext(MaxDepthContext.class,i);
		}
		public List<FlagContext> flag() {
			return getRuleContexts(FlagContext.class);
		}
		public FlagContext flag(int i) {
			return getRuleContext(FlagContext.class,i);
		}
		public List<DContext> d() {
			return getRuleContexts(DContext.class);
		}
		public DContext d(int i) {
			return getRuleContext(DContext.class,i);
		}
		public ProfileRecordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_profileRecord; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).enterProfileRecord(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).exitProfileRecord(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VaultGrammarVisitor ) return ((VaultGrammarVisitor<? extends T>)visitor).visitProfileRecord(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProfileRecordContext profileRecord() throws RecognitionException {
		ProfileRecordContext _localctx = new ProfileRecordContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_profileRecord);

		            ((ProfileRecordContext)_localctx).vault =  new ArrayList<Directive>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			((ProfileRecordContext)_localctx).name = name();
			 _localctx.vault.add(((ProfileRecordContext)_localctx).name.nameDirective); 
			setState(102);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1020L) != 0)) {
				{
				setState(100);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case TYPE:
					{
					setState(76);
					((ProfileRecordContext)_localctx).type = type();
					 _localctx.vault.add(((ProfileRecordContext)_localctx).type.typeDirective); 
					}
					break;
				case RATING:
					{
					setState(79);
					((ProfileRecordContext)_localctx).rating = rating();
					 _localctx.vault.add(((ProfileRecordContext)_localctx).rating.ratingDirective); 
					}
					break;
				case ROWS:
					{
					setState(82);
					((ProfileRecordContext)_localctx).rows = rows();
					 _localctx.vault.add(((ProfileRecordContext)_localctx).rows.rowsDirective); 
					}
					break;
				case COLUMNS:
					{
					setState(85);
					((ProfileRecordContext)_localctx).columns = columns();
					 _localctx.vault.add(((ProfileRecordContext)_localctx).columns.colsDirective); 
					}
					break;
				case MIN_DEPTH:
					{
					setState(88);
					((ProfileRecordContext)_localctx).minDepth = minDepth();
					 _localctx.vault.add(((ProfileRecordContext)_localctx).minDepth.minDepthDirective); 
					}
					break;
				case MAX_DEPTH:
					{
					setState(91);
					((ProfileRecordContext)_localctx).maxDepth = maxDepth();
					 _localctx.vault.add(((ProfileRecordContext)_localctx).maxDepth.maxDepthDirective); 
					}
					break;
				case FLAGS:
					{
					setState(94);
					((ProfileRecordContext)_localctx).flag = flag();
					 _localctx.vault.add(((ProfileRecordContext)_localctx).flag.flagDirective); 
					}
					break;
				case D:
					{
					setState(97);
					((ProfileRecordContext)_localctx).d = d();
					 _localctx.vault.add(((ProfileRecordContext)_localctx).d.mapDirective); 
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(104);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
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
	public static class FileContext extends ParserRuleContext {
		public List<VaultParseRecord> profiles;
		public ProfileRecordContext profileRecord;
		public TerminalNode EOF() { return getToken(VaultGrammar.EOF, 0); }
		public List<ProfileRecordContext> profileRecord() {
			return getRuleContexts(ProfileRecordContext.class);
		}
		public ProfileRecordContext profileRecord(int i) {
			return getRuleContext(ProfileRecordContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VaultGrammarVisitor ) return ((VaultGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_file);
		 ((FileContext)_localctx).profiles =  new ArrayList<>(); 
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NAME) {
				{
				{
				setState(105);
				((FileContext)_localctx).profileRecord = profileRecord();
				 _localctx.profiles.add(new VaultParseRecord(((FileContext)_localctx).profileRecord.vault)); 
				}
				}
				setState(112);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(113);
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
		"\u0004\u0001\u0012t\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0004\u00075\b\u0007\u000b"+
		"\u0007\f\u00076\u0001\u0007\u0001\u0007\u0003\u0007;\b\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0003\u0007@\b\u0007\u0005\u0007B\b\u0007\n\u0007"+
		"\f\u0007E\t\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0005\te\b\t\n\t\f\th\t\t\u0001"+
		"\n\u0001\n\u0001\n\u0005\nm\b\n\n\n\f\np\t\n\u0001\n\u0001\n\u0001\n\u0000"+
		"\u0000\u000b\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0000"+
		"\u0000v\u0000\u0016\u0001\u0000\u0000\u0000\u0002\u001a\u0001\u0000\u0000"+
		"\u0000\u0004\u001e\u0001\u0000\u0000\u0000\u0006\"\u0001\u0000\u0000\u0000"+
		"\b&\u0001\u0000\u0000\u0000\n*\u0001\u0000\u0000\u0000\f.\u0001\u0000"+
		"\u0000\u0000\u000e2\u0001\u0000\u0000\u0000\u0010F\u0001\u0000\u0000\u0000"+
		"\u0012J\u0001\u0000\u0000\u0000\u0014n\u0001\u0000\u0000\u0000\u0016\u0017"+
		"\u0005\u0001\u0000\u0000\u0017\u0018\u0005\u0011\u0000\u0000\u0018\u0019"+
		"\u0006\u0000\uffff\uffff\u0000\u0019\u0001\u0001\u0000\u0000\u0000\u001a"+
		"\u001b\u0005\u0002\u0000\u0000\u001b\u001c\u0005\u0011\u0000\u0000\u001c"+
		"\u001d\u0006\u0001\uffff\uffff\u0000\u001d\u0003\u0001\u0000\u0000\u0000"+
		"\u001e\u001f\u0005\u0003\u0000\u0000\u001f \u0005\n\u0000\u0000 !\u0006"+
		"\u0002\uffff\uffff\u0000!\u0005\u0001\u0000\u0000\u0000\"#\u0005\u0004"+
		"\u0000\u0000#$\u0005\n\u0000\u0000$%\u0006\u0003\uffff\uffff\u0000%\u0007"+
		"\u0001\u0000\u0000\u0000&\'\u0005\u0005\u0000\u0000\'(\u0005\n\u0000\u0000"+
		"()\u0006\u0004\uffff\uffff\u0000)\t\u0001\u0000\u0000\u0000*+\u0005\u0006"+
		"\u0000\u0000+,\u0005\n\u0000\u0000,-\u0006\u0005\uffff\uffff\u0000-\u000b"+
		"\u0001\u0000\u0000\u0000./\u0005\u0007\u0000\u0000/0\u0005\n\u0000\u0000"+
		"01\u0006\u0006\uffff\uffff\u00001\r\u0001\u0000\u0000\u00002:\u0005\b"+
		"\u0000\u000035\u0005\u000f\u0000\u000043\u0001\u0000\u0000\u000056\u0001"+
		"\u0000\u0000\u000064\u0001\u0000\u0000\u000067\u0001\u0000\u0000\u0000"+
		"7;\u0001\u0000\u0000\u000089\u0005\u000e\u0000\u00009;\u0006\u0007\uffff"+
		"\uffff\u0000:4\u0001\u0000\u0000\u0000:8\u0001\u0000\u0000\u0000:;\u0001"+
		"\u0000\u0000\u0000;C\u0001\u0000\u0000\u0000<?\u0005\u000f\u0000\u0000"+
		"=>\u0005\u000e\u0000\u0000>@\u0006\u0007\uffff\uffff\u0000?=\u0001\u0000"+
		"\u0000\u0000?@\u0001\u0000\u0000\u0000@B\u0001\u0000\u0000\u0000A<\u0001"+
		"\u0000\u0000\u0000BE\u0001\u0000\u0000\u0000CA\u0001\u0000\u0000\u0000"+
		"CD\u0001\u0000\u0000\u0000D\u000f\u0001\u0000\u0000\u0000EC\u0001\u0000"+
		"\u0000\u0000FG\u0005\t\u0000\u0000GH\u0005\u0011\u0000\u0000HI\u0006\b"+
		"\uffff\uffff\u0000I\u0011\u0001\u0000\u0000\u0000JK\u0003\u0000\u0000"+
		"\u0000Kf\u0006\t\uffff\uffff\u0000LM\u0003\u0002\u0001\u0000MN\u0006\t"+
		"\uffff\uffff\u0000Ne\u0001\u0000\u0000\u0000OP\u0003\u0004\u0002\u0000"+
		"PQ\u0006\t\uffff\uffff\u0000Qe\u0001\u0000\u0000\u0000RS\u0003\u0006\u0003"+
		"\u0000ST\u0006\t\uffff\uffff\u0000Te\u0001\u0000\u0000\u0000UV\u0003\b"+
		"\u0004\u0000VW\u0006\t\uffff\uffff\u0000We\u0001\u0000\u0000\u0000XY\u0003"+
		"\n\u0005\u0000YZ\u0006\t\uffff\uffff\u0000Ze\u0001\u0000\u0000\u0000["+
		"\\\u0003\f\u0006\u0000\\]\u0006\t\uffff\uffff\u0000]e\u0001\u0000\u0000"+
		"\u0000^_\u0003\u000e\u0007\u0000_`\u0006\t\uffff\uffff\u0000`e\u0001\u0000"+
		"\u0000\u0000ab\u0003\u0010\b\u0000bc\u0006\t\uffff\uffff\u0000ce\u0001"+
		"\u0000\u0000\u0000dL\u0001\u0000\u0000\u0000dO\u0001\u0000\u0000\u0000"+
		"dR\u0001\u0000\u0000\u0000dU\u0001\u0000\u0000\u0000dX\u0001\u0000\u0000"+
		"\u0000d[\u0001\u0000\u0000\u0000d^\u0001\u0000\u0000\u0000da\u0001\u0000"+
		"\u0000\u0000eh\u0001\u0000\u0000\u0000fd\u0001\u0000\u0000\u0000fg\u0001"+
		"\u0000\u0000\u0000g\u0013\u0001\u0000\u0000\u0000hf\u0001\u0000\u0000"+
		"\u0000ij\u0003\u0012\t\u0000jk\u0006\n\uffff\uffff\u0000km\u0001\u0000"+
		"\u0000\u0000li\u0001\u0000\u0000\u0000mp\u0001\u0000\u0000\u0000nl\u0001"+
		"\u0000\u0000\u0000no\u0001\u0000\u0000\u0000oq\u0001\u0000\u0000\u0000"+
		"pn\u0001\u0000\u0000\u0000qr\u0005\u0000\u0000\u0001r\u0015\u0001\u0000"+
		"\u0000\u0000\u00076:?Cdfn";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}