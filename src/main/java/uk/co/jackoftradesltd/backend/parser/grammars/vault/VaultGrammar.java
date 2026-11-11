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
		public String nameStr;
		public int line;
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
			 ((NameContext)_localctx).nameStr =  ((NameContext)_localctx).n.getText(); ((NameContext)_localctx).line =  _localctx.start.getLine(); 
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
		public String typeStr;
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
			 ((TypeContext)_localctx).typeStr =  ((TypeContext)_localctx).t.getText(); 
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
		public String ratingStr;
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
			 ((RatingContext)_localctx).ratingStr =  ((RatingContext)_localctx).r.getText(); 
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
		public String rowsStr;
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
			 ((RowsContext)_localctx).rowsStr =  ((RowsContext)_localctx).r.getText(); 
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
		public String columnsStr;
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
			 ((ColumnsContext)_localctx).columnsStr =  ((ColumnsContext)_localctx).c.getText(); 
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
		public String minDepthStr;
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
			 ((MinDepthContext)_localctx).minDepthStr =  ((MinDepthContext)_localctx).m.getText(); 
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
		public String maxDepthStr;
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
			 ((MaxDepthContext)_localctx).maxDepthStr =  ((MaxDepthContext)_localctx).m.getText(); 
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
		public List<String> flags;
		public Token f1;
		public Token f2;
		public TerminalNode FLAGS() { return getToken(VaultGrammar.FLAGS, 0); }
		public List<TerminalNode> FLAG() { return getTokens(VaultGrammar.FLAG); }
		public TerminalNode FLAG(int i) {
			return getToken(VaultGrammar.FLAG, i);
		}
		public List<TerminalNode> OR() { return getTokens(VaultGrammar.OR); }
		public TerminalNode OR(int i) {
			return getToken(VaultGrammar.OR, i);
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
		 ((FlagContext)_localctx).flags =  new ArrayList<>(); 
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(50);
			match(FLAGS);
			setState(51);
			((FlagContext)_localctx).f1 = match(FLAG);
			 _localctx.flags.add(((FlagContext)_localctx).f1.getText()); 
			setState(58);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OR) {
				{
				{
				setState(53);
				match(OR);
				setState(54);
				((FlagContext)_localctx).f2 = match(FLAG);
				 _localctx.flags.add(((FlagContext)_localctx).f2.getText()); 
				}
				}
				setState(60);
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
	public static class DContext extends ParserRuleContext {
		public List<String> map;
		public Token m;
		public List<TerminalNode> D() { return getTokens(VaultGrammar.D); }
		public TerminalNode D(int i) {
			return getToken(VaultGrammar.D, i);
		}
		public List<TerminalNode> STRING() { return getTokens(VaultGrammar.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(VaultGrammar.STRING, i);
		}
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
		 ((DContext)_localctx).map =  new ArrayList<>(); 
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(61);
				match(D);
				setState(62);
				((DContext)_localctx).m = match(STRING);
				 _localctx.map.add(((DContext)_localctx).m.getText()); 
				}
				}
				setState(66); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==D );
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
		public VaultParseRecord record;
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
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public RatingContext rating() {
			return getRuleContext(RatingContext.class,0);
		}
		public RowsContext rows() {
			return getRuleContext(RowsContext.class,0);
		}
		public ColumnsContext columns() {
			return getRuleContext(ColumnsContext.class,0);
		}
		public MinDepthContext minDepth() {
			return getRuleContext(MinDepthContext.class,0);
		}
		public MaxDepthContext maxDepth() {
			return getRuleContext(MaxDepthContext.class,0);
		}
		public DContext d() {
			return getRuleContext(DContext.class,0);
		}
		public List<FlagContext> flag() {
			return getRuleContexts(FlagContext.class);
		}
		public FlagContext flag(int i) {
			return getRuleContext(FlagContext.class,i);
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

		            String nameInit = "";
		            String typeInit = "";
		            String ratingInit = "";
		            String rowsInit = "";
		            String columnsInit = "";
		            String minDepthInit = "";
		            String maxDepthInit = "";
		            List<String> flags = new ArrayList<>();
		            String mapText = "";
		            List<String> map = new ArrayList<>();
		            int line = 0;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(68);
			((ProfileRecordContext)_localctx).name = name();

			                line = ((ProfileRecordContext)_localctx).name.line;
			                nameInit = ((ProfileRecordContext)_localctx).name.nameStr;
			            
			setState(70);
			((ProfileRecordContext)_localctx).type = type();
			 typeInit = ((ProfileRecordContext)_localctx).type.typeStr; 
			setState(72);
			((ProfileRecordContext)_localctx).rating = rating();
			 ratingInit = ((ProfileRecordContext)_localctx).rating.ratingStr; 
			setState(74);
			((ProfileRecordContext)_localctx).rows = rows();
			 rowsInit = ((ProfileRecordContext)_localctx).rows.rowsStr; 
			setState(76);
			((ProfileRecordContext)_localctx).columns = columns();
			 columnsInit = ((ProfileRecordContext)_localctx).columns.columnsStr; 
			setState(78);
			((ProfileRecordContext)_localctx).minDepth = minDepth();
			 minDepthInit = ((ProfileRecordContext)_localctx).minDepth.minDepthStr; 
			setState(80);
			((ProfileRecordContext)_localctx).maxDepth = maxDepth();
			 maxDepthInit = ((ProfileRecordContext)_localctx).maxDepth.maxDepthStr; 
			setState(87);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FLAGS) {
				{
				{
				setState(82);
				((ProfileRecordContext)_localctx).flag = flag();
				 flags.addAll(((ProfileRecordContext)_localctx).flag.flags); 
				}
				}
				setState(89);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(90);
			((ProfileRecordContext)_localctx).d = d();

			                map.addAll(((ProfileRecordContext)_localctx).d.map);
			                StringBuilder sb = new StringBuilder();
			                for (String mapLine : map) {
			                    sb.append(mapLine);
			                }
			                mapText = sb.toString();
			            
			}
			_ctx.stop = _input.LT(-1);

			            ((ProfileRecordContext)_localctx).record =  new VaultParseRecord(nameInit, typeInit, mapText, map, ratingInit,
			                rowsInit, columnsInit, minDepthInit, maxDepthInit, flags, line);
			        
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
			setState(96); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(93);
				((FileContext)_localctx).profileRecord = profileRecord();
				 _localctx.profiles.add(((FileContext)_localctx).profileRecord.record); 
				}
				}
				setState(98); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(100);
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
		"\u0004\u0001\u0012g\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0005\u00079\b\u0007\n\u0007\f\u0007<\t\u0007\u0001"+
		"\b\u0001\b\u0001\b\u0004\bA\b\b\u000b\b\f\bB\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0005\tV\b\t\n\t\f\tY\t\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\n\u0001\n\u0001\n\u0004\na\b\n\u000b\n\f\nb\u0001\n\u0001"+
		"\n\u0001\n\u0000\u0000\u000b\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010"+
		"\u0012\u0014\u0000\u0000_\u0000\u0016\u0001\u0000\u0000\u0000\u0002\u001a"+
		"\u0001\u0000\u0000\u0000\u0004\u001e\u0001\u0000\u0000\u0000\u0006\"\u0001"+
		"\u0000\u0000\u0000\b&\u0001\u0000\u0000\u0000\n*\u0001\u0000\u0000\u0000"+
		"\f.\u0001\u0000\u0000\u0000\u000e2\u0001\u0000\u0000\u0000\u0010@\u0001"+
		"\u0000\u0000\u0000\u0012D\u0001\u0000\u0000\u0000\u0014`\u0001\u0000\u0000"+
		"\u0000\u0016\u0017\u0005\u0001\u0000\u0000\u0017\u0018\u0005\u0011\u0000"+
		"\u0000\u0018\u0019\u0006\u0000\uffff\uffff\u0000\u0019\u0001\u0001\u0000"+
		"\u0000\u0000\u001a\u001b\u0005\u0002\u0000\u0000\u001b\u001c\u0005\u0011"+
		"\u0000\u0000\u001c\u001d\u0006\u0001\uffff\uffff\u0000\u001d\u0003\u0001"+
		"\u0000\u0000\u0000\u001e\u001f\u0005\u0003\u0000\u0000\u001f \u0005\n"+
		"\u0000\u0000 !\u0006\u0002\uffff\uffff\u0000!\u0005\u0001\u0000\u0000"+
		"\u0000\"#\u0005\u0004\u0000\u0000#$\u0005\n\u0000\u0000$%\u0006\u0003"+
		"\uffff\uffff\u0000%\u0007\u0001\u0000\u0000\u0000&\'\u0005\u0005\u0000"+
		"\u0000\'(\u0005\n\u0000\u0000()\u0006\u0004\uffff\uffff\u0000)\t\u0001"+
		"\u0000\u0000\u0000*+\u0005\u0006\u0000\u0000+,\u0005\n\u0000\u0000,-\u0006"+
		"\u0005\uffff\uffff\u0000-\u000b\u0001\u0000\u0000\u0000./\u0005\u0007"+
		"\u0000\u0000/0\u0005\n\u0000\u000001\u0006\u0006\uffff\uffff\u00001\r"+
		"\u0001\u0000\u0000\u000023\u0005\b\u0000\u000034\u0005\u000e\u0000\u0000"+
		"4:\u0006\u0007\uffff\uffff\u000056\u0005\u000f\u0000\u000067\u0005\u000e"+
		"\u0000\u000079\u0006\u0007\uffff\uffff\u000085\u0001\u0000\u0000\u0000"+
		"9<\u0001\u0000\u0000\u0000:8\u0001\u0000\u0000\u0000:;\u0001\u0000\u0000"+
		"\u0000;\u000f\u0001\u0000\u0000\u0000<:\u0001\u0000\u0000\u0000=>\u0005"+
		"\t\u0000\u0000>?\u0005\u0011\u0000\u0000?A\u0006\b\uffff\uffff\u0000@"+
		"=\u0001\u0000\u0000\u0000AB\u0001\u0000\u0000\u0000B@\u0001\u0000\u0000"+
		"\u0000BC\u0001\u0000\u0000\u0000C\u0011\u0001\u0000\u0000\u0000DE\u0003"+
		"\u0000\u0000\u0000EF\u0006\t\uffff\uffff\u0000FG\u0003\u0002\u0001\u0000"+
		"GH\u0006\t\uffff\uffff\u0000HI\u0003\u0004\u0002\u0000IJ\u0006\t\uffff"+
		"\uffff\u0000JK\u0003\u0006\u0003\u0000KL\u0006\t\uffff\uffff\u0000LM\u0003"+
		"\b\u0004\u0000MN\u0006\t\uffff\uffff\u0000NO\u0003\n\u0005\u0000OP\u0006"+
		"\t\uffff\uffff\u0000PQ\u0003\f\u0006\u0000QW\u0006\t\uffff\uffff\u0000"+
		"RS\u0003\u000e\u0007\u0000ST\u0006\t\uffff\uffff\u0000TV\u0001\u0000\u0000"+
		"\u0000UR\u0001\u0000\u0000\u0000VY\u0001\u0000\u0000\u0000WU\u0001\u0000"+
		"\u0000\u0000WX\u0001\u0000\u0000\u0000XZ\u0001\u0000\u0000\u0000YW\u0001"+
		"\u0000\u0000\u0000Z[\u0003\u0010\b\u0000[\\\u0006\t\uffff\uffff\u0000"+
		"\\\u0013\u0001\u0000\u0000\u0000]^\u0003\u0012\t\u0000^_\u0006\n\uffff"+
		"\uffff\u0000_a\u0001\u0000\u0000\u0000`]\u0001\u0000\u0000\u0000ab\u0001"+
		"\u0000\u0000\u0000b`\u0001\u0000\u0000\u0000bc\u0001\u0000\u0000\u0000"+
		"cd\u0001\u0000\u0000\u0000de\u0005\u0000\u0000\u0001e\u0015\u0001\u0000"+
		"\u0000\u0000\u0004:BWb";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}