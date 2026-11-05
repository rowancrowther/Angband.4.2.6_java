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

// Generated from /home/rowan/IdeaProjects/Angband.4.2.6_java/src/main/java/uk/co/jackoftradesltd/backend/parser/grammars/RoomProfileGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.roomprofiler;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.backend.parser.roomprofile.RoomProfileParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class RoomProfileGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, TYPE=2, RATING=3, ROWS=4, COLUMNS=5, DOORS=6, TVAL=7, FLAGS=8, 
		DLINE=9, INTEGER=10, COMMENT=11, EOL=12, FLAG=13, FLAG_OR=14, FLAG_EOF=15, 
		STRING=16, END_OF_LINE=17;
	public static final int
		RULE_name = 0, RULE_type = 1, RULE_rating = 2, RULE_rows = 3, RULE_columns = 4, 
		RULE_doors = 5, RULE_tval = 6, RULE_flags = 7, RULE_dLine = 8, RULE_roomMap = 9, 
		RULE_roomProfile = 10, RULE_file = 11;
	private static String[] makeRuleNames() {
		return new String[] {
			"name", "type", "rating", "rows", "columns", "doors", "tval", "flags", 
			"dLine", "roomMap", "roomProfile", "file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'type:'", "'rating:'", "'rows:'", "'columns:'", "'doors:'", 
			"'tval:'", "'flags:'", "'D:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "TYPE", "RATING", "ROWS", "COLUMNS", "DOORS", "TVAL", "FLAGS", 
			"DLINE", "INTEGER", "COMMENT", "EOL", "FLAG", "FLAG_OR", "FLAG_EOF", 
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
	public String getGrammarFileName() { return "RoomProfileGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public RoomProfileGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public int line;
		public Token n;
		public TerminalNode NAME() { return getToken(RoomProfileGrammar.NAME, 0); }
		public TerminalNode STRING() { return getToken(RoomProfileGrammar.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RoomProfileGrammarListener ) ((RoomProfileGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RoomProfileGrammarListener ) ((RoomProfileGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RoomProfileGrammarVisitor ) return ((RoomProfileGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(24);
			match(NAME);
			setState(25);
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
		public String typeInt;
		public Token t;
		public TerminalNode TYPE() { return getToken(RoomProfileGrammar.TYPE, 0); }
		public TerminalNode INTEGER() { return getToken(RoomProfileGrammar.INTEGER, 0); }
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RoomProfileGrammarListener ) ((RoomProfileGrammarListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RoomProfileGrammarListener ) ((RoomProfileGrammarListener)listener).exitType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RoomProfileGrammarVisitor ) return ((RoomProfileGrammarVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(28);
			match(TYPE);
			setState(29);
			((TypeContext)_localctx).t = match(INTEGER);
			 ((TypeContext)_localctx).typeInt =  ((TypeContext)_localctx).t.getText(); 
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
		public String ratingInt;
		public Token r;
		public TerminalNode RATING() { return getToken(RoomProfileGrammar.RATING, 0); }
		public TerminalNode INTEGER() { return getToken(RoomProfileGrammar.INTEGER, 0); }
		public RatingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rating; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RoomProfileGrammarListener ) ((RoomProfileGrammarListener)listener).enterRating(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RoomProfileGrammarListener ) ((RoomProfileGrammarListener)listener).exitRating(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RoomProfileGrammarVisitor ) return ((RoomProfileGrammarVisitor<? extends T>)visitor).visitRating(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RatingContext rating() throws RecognitionException {
		RatingContext _localctx = new RatingContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_rating);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(32);
			match(RATING);
			setState(33);
			((RatingContext)_localctx).r = match(INTEGER);
			 ((RatingContext)_localctx).ratingInt =  ((RatingContext)_localctx).r.getText(); 
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
		public String rowsInt;
		public Token r;
		public TerminalNode ROWS() { return getToken(RoomProfileGrammar.ROWS, 0); }
		public TerminalNode INTEGER() { return getToken(RoomProfileGrammar.INTEGER, 0); }
		public RowsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rows; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RoomProfileGrammarListener ) ((RoomProfileGrammarListener)listener).enterRows(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RoomProfileGrammarListener ) ((RoomProfileGrammarListener)listener).exitRows(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RoomProfileGrammarVisitor ) return ((RoomProfileGrammarVisitor<? extends T>)visitor).visitRows(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RowsContext rows() throws RecognitionException {
		RowsContext _localctx = new RowsContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_rows);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(36);
			match(ROWS);
			setState(37);
			((RowsContext)_localctx).r = match(INTEGER);
			 ((RowsContext)_localctx).rowsInt =  ((RowsContext)_localctx).r.getText(); 
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
		public String columnsInt;
		public Token c;
		public TerminalNode COLUMNS() { return getToken(RoomProfileGrammar.COLUMNS, 0); }
		public TerminalNode INTEGER() { return getToken(RoomProfileGrammar.INTEGER, 0); }
		public ColumnsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_columns; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RoomProfileGrammarListener ) ((RoomProfileGrammarListener)listener).enterColumns(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RoomProfileGrammarListener ) ((RoomProfileGrammarListener)listener).exitColumns(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RoomProfileGrammarVisitor ) return ((RoomProfileGrammarVisitor<? extends T>)visitor).visitColumns(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ColumnsContext columns() throws RecognitionException {
		ColumnsContext _localctx = new ColumnsContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_columns);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(40);
			match(COLUMNS);
			setState(41);
			((ColumnsContext)_localctx).c = match(INTEGER);
			 ((ColumnsContext)_localctx).columnsInt =  ((ColumnsContext)_localctx).c.getText(); 
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
	public static class DoorsContext extends ParserRuleContext {
		public String doorsInt;
		public Token d;
		public TerminalNode DOORS() { return getToken(RoomProfileGrammar.DOORS, 0); }
		public TerminalNode INTEGER() { return getToken(RoomProfileGrammar.INTEGER, 0); }
		public DoorsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_doors; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RoomProfileGrammarListener ) ((RoomProfileGrammarListener)listener).enterDoors(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RoomProfileGrammarListener ) ((RoomProfileGrammarListener)listener).exitDoors(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RoomProfileGrammarVisitor ) return ((RoomProfileGrammarVisitor<? extends T>)visitor).visitDoors(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DoorsContext doors() throws RecognitionException {
		DoorsContext _localctx = new DoorsContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_doors);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			match(DOORS);
			setState(45);
			((DoorsContext)_localctx).d = match(INTEGER);
			 ((DoorsContext)_localctx).doorsInt =  ((DoorsContext)_localctx).d.getText(); 
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
	public static class TvalContext extends ParserRuleContext {
		public String tvalStr;
		public Token t;
		public TerminalNode TVAL() { return getToken(RoomProfileGrammar.TVAL, 0); }
		public TerminalNode STRING() { return getToken(RoomProfileGrammar.STRING, 0); }
		public TvalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tval; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RoomProfileGrammarListener ) ((RoomProfileGrammarListener)listener).enterTval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RoomProfileGrammarListener ) ((RoomProfileGrammarListener)listener).exitTval(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RoomProfileGrammarVisitor ) return ((RoomProfileGrammarVisitor<? extends T>)visitor).visitTval(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TvalContext tval() throws RecognitionException {
		TvalContext _localctx = new TvalContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_tval);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			match(TVAL);
			setState(49);
			((TvalContext)_localctx).t = match(STRING);
			 ((TvalContext)_localctx).tvalStr =  ((TvalContext)_localctx).t.getText(); 
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
		public List<String> flagList;
		public Token f1;
		public Token f2;
		public TerminalNode FLAGS() { return getToken(RoomProfileGrammar.FLAGS, 0); }
		public List<TerminalNode> FLAG() { return getTokens(RoomProfileGrammar.FLAG); }
		public TerminalNode FLAG(int i) {
			return getToken(RoomProfileGrammar.FLAG, i);
		}
		public List<TerminalNode> FLAG_OR() { return getTokens(RoomProfileGrammar.FLAG_OR); }
		public TerminalNode FLAG_OR(int i) {
			return getToken(RoomProfileGrammar.FLAG_OR, i);
		}
		public FlagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flags; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RoomProfileGrammarListener ) ((RoomProfileGrammarListener)listener).enterFlags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RoomProfileGrammarListener ) ((RoomProfileGrammarListener)listener).exitFlags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RoomProfileGrammarVisitor ) return ((RoomProfileGrammarVisitor<? extends T>)visitor).visitFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsContext flags() throws RecognitionException {
		FlagsContext _localctx = new FlagsContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_flags);

		            ((FlagsContext)_localctx).flagList =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			match(FLAGS);
			setState(53);
			((FlagsContext)_localctx).f1 = match(FLAG);

			                _localctx.flagList.add(((FlagsContext)_localctx).f1.getText());
			            
			setState(60);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FLAG_OR) {
				{
				{
				setState(55);
				match(FLAG_OR);
				setState(56);
				((FlagsContext)_localctx).f2 = match(FLAG);

				                _localctx.flagList.add(((FlagsContext)_localctx).f2.getText());
				            
				}
				}
				setState(62);
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
	public static class DLineContext extends ParserRuleContext {
		public String roomMapLine;
		public int lineNo;
		public Token l;
		public TerminalNode DLINE() { return getToken(RoomProfileGrammar.DLINE, 0); }
		public TerminalNode STRING() { return getToken(RoomProfileGrammar.STRING, 0); }
		public DLineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dLine; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RoomProfileGrammarListener ) ((RoomProfileGrammarListener)listener).enterDLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RoomProfileGrammarListener ) ((RoomProfileGrammarListener)listener).exitDLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RoomProfileGrammarVisitor ) return ((RoomProfileGrammarVisitor<? extends T>)visitor).visitDLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DLineContext dLine() throws RecognitionException {
		DLineContext _localctx = new DLineContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_dLine);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			match(DLINE);
			setState(64);
			((DLineContext)_localctx).l = match(STRING);
			 ((DLineContext)_localctx).roomMapLine =  ((DLineContext)_localctx).l.getText(); ((DLineContext)_localctx).lineNo =  _localctx.start.getLine(); 
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
	public static class RoomMapContext extends ParserRuleContext {
		public List<String> roomMapList;
		public int firstLine;
		public DLineContext dLine;
		public List<DLineContext> dLine() {
			return getRuleContexts(DLineContext.class);
		}
		public DLineContext dLine(int i) {
			return getRuleContext(DLineContext.class,i);
		}
		public RoomMapContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_roomMap; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RoomProfileGrammarListener ) ((RoomProfileGrammarListener)listener).enterRoomMap(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RoomProfileGrammarListener ) ((RoomProfileGrammarListener)listener).exitRoomMap(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RoomProfileGrammarVisitor ) return ((RoomProfileGrammarVisitor<? extends T>)visitor).visitRoomMap(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RoomMapContext roomMap() throws RecognitionException {
		RoomMapContext _localctx = new RoomMapContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_roomMap);

		            ((RoomMapContext)_localctx).roomMapList =  new ArrayList<>();
		            ((RoomMapContext)_localctx).firstLine =  -1;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(67);
				((RoomMapContext)_localctx).dLine = dLine();

				                if (_localctx.firstLine == -1) ((RoomMapContext)_localctx).firstLine =  ((RoomMapContext)_localctx).dLine.lineNo;
				                if (((RoomMapContext)_localctx).dLine.lineNo < _localctx.firstLine) ((RoomMapContext)_localctx).firstLine =  ((RoomMapContext)_localctx).dLine.lineNo;
				                _localctx.roomMapList.add(((RoomMapContext)_localctx).dLine.roomMapLine);
				            
				}
				}
				setState(72); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==DLINE );
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
	public static class RoomProfileContext extends ParserRuleContext {
		public RoomProfileParseRecord profile;
		public NameContext name;
		public TypeContext type;
		public RatingContext rating;
		public RowsContext rows;
		public ColumnsContext columns;
		public DoorsContext doors;
		public TvalContext tval;
		public FlagsContext flags;
		public RoomMapContext roomMap;
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
		public DoorsContext doors() {
			return getRuleContext(DoorsContext.class,0);
		}
		public TvalContext tval() {
			return getRuleContext(TvalContext.class,0);
		}
		public RoomMapContext roomMap() {
			return getRuleContext(RoomMapContext.class,0);
		}
		public FlagsContext flags() {
			return getRuleContext(FlagsContext.class,0);
		}
		public RoomProfileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_roomProfile; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RoomProfileGrammarListener ) ((RoomProfileGrammarListener)listener).enterRoomProfile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RoomProfileGrammarListener ) ((RoomProfileGrammarListener)listener).exitRoomProfile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RoomProfileGrammarVisitor ) return ((RoomProfileGrammarVisitor<? extends T>)visitor).visitRoomProfile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RoomProfileContext roomProfile() throws RecognitionException {
		RoomProfileContext _localctx = new RoomProfileContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_roomProfile);

		            String nameInit = "";
		            String typeInit = "";
		            String ratingInit = "";
		            String rowsInit = "";
		            String columnsInit = "";
		            String doorsInit = "";
		            String tvalInit = "";
		            List<String> flagsInit = new ArrayList<>();
		            List<String> roomInit = new ArrayList<>();
		            int profileLineNo = 0;
		            int roomMapLineNo = 0;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			((RoomProfileContext)_localctx).name = name();
			 nameInit = ((RoomProfileContext)_localctx).name.nameStr; profileLineNo = ((RoomProfileContext)_localctx).name.line; 
			setState(76);
			((RoomProfileContext)_localctx).type = type();
			 typeInit = ((RoomProfileContext)_localctx).type.typeInt; 
			setState(78);
			((RoomProfileContext)_localctx).rating = rating();
			 ratingInit = ((RoomProfileContext)_localctx).rating.ratingInt; 
			setState(80);
			((RoomProfileContext)_localctx).rows = rows();
			 rowsInit = ((RoomProfileContext)_localctx).rows.rowsInt; 
			setState(82);
			((RoomProfileContext)_localctx).columns = columns();
			 columnsInit = ((RoomProfileContext)_localctx).columns.columnsInt; 
			setState(84);
			((RoomProfileContext)_localctx).doors = doors();
			 doorsInit = ((RoomProfileContext)_localctx).doors.doorsInt; 
			setState(86);
			((RoomProfileContext)_localctx).tval = tval();
			 tvalInit = ((RoomProfileContext)_localctx).tval.tvalStr; 
			setState(91);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FLAGS) {
				{
				setState(88);
				((RoomProfileContext)_localctx).flags = flags();
				 flagsInit.addAll(((RoomProfileContext)_localctx).flags.flagList); 
				}
			}

			setState(93);
			((RoomProfileContext)_localctx).roomMap = roomMap();
			 roomInit.addAll(((RoomProfileContext)_localctx).roomMap.roomMapList); roomMapLineNo = ((RoomProfileContext)_localctx).roomMap.firstLine; 
			}
			_ctx.stop = _input.LT(-1);

			            ((RoomProfileContext)_localctx).profile =  new RoomProfileParseRecord(nameInit, typeInit, ratingInit,
			                rowsInit, columnsInit, doorsInit, tvalInit, flagsInit, roomInit,
			                profileLineNo, roomMapLineNo);
			        
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
		public List<RoomProfileParseRecord> records;
		public RoomProfileContext roomProfile;
		public TerminalNode EOF() { return getToken(RoomProfileGrammar.EOF, 0); }
		public List<RoomProfileContext> roomProfile() {
			return getRuleContexts(RoomProfileContext.class);
		}
		public RoomProfileContext roomProfile(int i) {
			return getRuleContext(RoomProfileContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RoomProfileGrammarListener ) ((RoomProfileGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RoomProfileGrammarListener ) ((RoomProfileGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RoomProfileGrammarVisitor ) return ((RoomProfileGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_file);

		            ((FileContext)_localctx).records =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(96);
				((FileContext)_localctx).roomProfile = roomProfile();
				 _localctx.records.add(((FileContext)_localctx).roomProfile.profile); 
				}
				}
				setState(101); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(103);
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
		"\u0004\u0001\u0011j\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0005\u0007;\b\u0007\n\u0007"+
		"\f\u0007>\t\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001"+
		"\t\u0004\tG\b\t\u000b\t\f\tH\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0003\n\\\b\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0004\u000bd\b\u000b\u000b\u000b\f\u000be\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0000\u0000\f\u0000\u0002\u0004\u0006\b\n\f\u000e"+
		"\u0010\u0012\u0014\u0016\u0000\u0000a\u0000\u0018\u0001\u0000\u0000\u0000"+
		"\u0002\u001c\u0001\u0000\u0000\u0000\u0004 \u0001\u0000\u0000\u0000\u0006"+
		"$\u0001\u0000\u0000\u0000\b(\u0001\u0000\u0000\u0000\n,\u0001\u0000\u0000"+
		"\u0000\f0\u0001\u0000\u0000\u0000\u000e4\u0001\u0000\u0000\u0000\u0010"+
		"?\u0001\u0000\u0000\u0000\u0012F\u0001\u0000\u0000\u0000\u0014J\u0001"+
		"\u0000\u0000\u0000\u0016c\u0001\u0000\u0000\u0000\u0018\u0019\u0005\u0001"+
		"\u0000\u0000\u0019\u001a\u0005\u0010\u0000\u0000\u001a\u001b\u0006\u0000"+
		"\uffff\uffff\u0000\u001b\u0001\u0001\u0000\u0000\u0000\u001c\u001d\u0005"+
		"\u0002\u0000\u0000\u001d\u001e\u0005\n\u0000\u0000\u001e\u001f\u0006\u0001"+
		"\uffff\uffff\u0000\u001f\u0003\u0001\u0000\u0000\u0000 !\u0005\u0003\u0000"+
		"\u0000!\"\u0005\n\u0000\u0000\"#\u0006\u0002\uffff\uffff\u0000#\u0005"+
		"\u0001\u0000\u0000\u0000$%\u0005\u0004\u0000\u0000%&\u0005\n\u0000\u0000"+
		"&\'\u0006\u0003\uffff\uffff\u0000\'\u0007\u0001\u0000\u0000\u0000()\u0005"+
		"\u0005\u0000\u0000)*\u0005\n\u0000\u0000*+\u0006\u0004\uffff\uffff\u0000"+
		"+\t\u0001\u0000\u0000\u0000,-\u0005\u0006\u0000\u0000-.\u0005\n\u0000"+
		"\u0000./\u0006\u0005\uffff\uffff\u0000/\u000b\u0001\u0000\u0000\u0000"+
		"01\u0005\u0007\u0000\u000012\u0005\u0010\u0000\u000023\u0006\u0006\uffff"+
		"\uffff\u00003\r\u0001\u0000\u0000\u000045\u0005\b\u0000\u000056\u0005"+
		"\r\u0000\u00006<\u0006\u0007\uffff\uffff\u000078\u0005\u000e\u0000\u0000"+
		"89\u0005\r\u0000\u00009;\u0006\u0007\uffff\uffff\u0000:7\u0001\u0000\u0000"+
		"\u0000;>\u0001\u0000\u0000\u0000<:\u0001\u0000\u0000\u0000<=\u0001\u0000"+
		"\u0000\u0000=\u000f\u0001\u0000\u0000\u0000><\u0001\u0000\u0000\u0000"+
		"?@\u0005\t\u0000\u0000@A\u0005\u0010\u0000\u0000AB\u0006\b\uffff\uffff"+
		"\u0000B\u0011\u0001\u0000\u0000\u0000CD\u0003\u0010\b\u0000DE\u0006\t"+
		"\uffff\uffff\u0000EG\u0001\u0000\u0000\u0000FC\u0001\u0000\u0000\u0000"+
		"GH\u0001\u0000\u0000\u0000HF\u0001\u0000\u0000\u0000HI\u0001\u0000\u0000"+
		"\u0000I\u0013\u0001\u0000\u0000\u0000JK\u0003\u0000\u0000\u0000KL\u0006"+
		"\n\uffff\uffff\u0000LM\u0003\u0002\u0001\u0000MN\u0006\n\uffff\uffff\u0000"+
		"NO\u0003\u0004\u0002\u0000OP\u0006\n\uffff\uffff\u0000PQ\u0003\u0006\u0003"+
		"\u0000QR\u0006\n\uffff\uffff\u0000RS\u0003\b\u0004\u0000ST\u0006\n\uffff"+
		"\uffff\u0000TU\u0003\n\u0005\u0000UV\u0006\n\uffff\uffff\u0000VW\u0003"+
		"\f\u0006\u0000W[\u0006\n\uffff\uffff\u0000XY\u0003\u000e\u0007\u0000Y"+
		"Z\u0006\n\uffff\uffff\u0000Z\\\u0001\u0000\u0000\u0000[X\u0001\u0000\u0000"+
		"\u0000[\\\u0001\u0000\u0000\u0000\\]\u0001\u0000\u0000\u0000]^\u0003\u0012"+
		"\t\u0000^_\u0006\n\uffff\uffff\u0000_\u0015\u0001\u0000\u0000\u0000`a"+
		"\u0003\u0014\n\u0000ab\u0006\u000b\uffff\uffff\u0000bd\u0001\u0000\u0000"+
		"\u0000c`\u0001\u0000\u0000\u0000de\u0001\u0000\u0000\u0000ec\u0001\u0000"+
		"\u0000\u0000ef\u0001\u0000\u0000\u0000fg\u0001\u0000\u0000\u0000gh\u0005"+
		"\u0000\u0000\u0001h\u0017\u0001\u0000\u0000\u0000\u0004<H[e";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}