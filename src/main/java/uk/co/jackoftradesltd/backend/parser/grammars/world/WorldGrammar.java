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

// Generated from WorldGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.world;

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
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class WorldGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		LEVEL=1, COLON=2, INTEGER=3, NAME=4, COMMENT=5, INDENT=6, EOL=7;
	public static final int
		RULE_levelNum = 0, RULE_levelName = 1, RULE_upAndDown = 2, RULE_line = 3, 
		RULE_file = 4;
	private static String[] makeRuleNames() {
		return new String[] {
			"levelNum", "levelName", "upAndDown", "line", "file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'level:'", "':'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "LEVEL", "COLON", "INTEGER", "NAME", "COMMENT", "INDENT", "EOL"
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
	public String getGrammarFileName() { return "WorldGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public WorldGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LevelNumContext extends ParserRuleContext {
		public String num;
		public Token INTEGER;
		public TerminalNode INTEGER() { return getToken(WorldGrammar.INTEGER, 0); }
		public TerminalNode COLON() { return getToken(WorldGrammar.COLON, 0); }
		public LevelNumContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_levelNum; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorldGrammarListener ) ((WorldGrammarListener)listener).enterLevelNum(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorldGrammarListener ) ((WorldGrammarListener)listener).exitLevelNum(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorldGrammarVisitor ) return ((WorldGrammarVisitor<? extends T>)visitor).visitLevelNum(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LevelNumContext levelNum() throws RecognitionException {
		LevelNumContext _localctx = new LevelNumContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_levelNum);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(10);
			((LevelNumContext)_localctx).INTEGER = match(INTEGER);
			setState(11);
			match(COLON);

			                ((LevelNumContext)_localctx).num =  ((LevelNumContext)_localctx).INTEGER.getText();
			            
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
	public static class LevelNameContext extends ParserRuleContext {
		public String name;
		public Token NAME;
		public TerminalNode NAME() { return getToken(WorldGrammar.NAME, 0); }
		public TerminalNode COLON() { return getToken(WorldGrammar.COLON, 0); }
		public LevelNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_levelName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorldGrammarListener ) ((WorldGrammarListener)listener).enterLevelName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorldGrammarListener ) ((WorldGrammarListener)listener).exitLevelName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorldGrammarVisitor ) return ((WorldGrammarVisitor<? extends T>)visitor).visitLevelName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LevelNameContext levelName() throws RecognitionException {
		LevelNameContext _localctx = new LevelNameContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_levelName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(14);
			((LevelNameContext)_localctx).NAME = match(NAME);
			setState(15);
			match(COLON);

			                ((LevelNameContext)_localctx).name =  ((LevelNameContext)_localctx).NAME.getText();
			            
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
	public static class UpAndDownContext extends ParserRuleContext {
		public String up;
		public String down;
		public Token u;
		public Token d;
		public TerminalNode COLON() { return getToken(WorldGrammar.COLON, 0); }
		public List<TerminalNode> NAME() { return getTokens(WorldGrammar.NAME); }
		public TerminalNode NAME(int i) {
			return getToken(WorldGrammar.NAME, i);
		}
		public UpAndDownContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_upAndDown; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorldGrammarListener ) ((WorldGrammarListener)listener).enterUpAndDown(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorldGrammarListener ) ((WorldGrammarListener)listener).exitUpAndDown(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorldGrammarVisitor ) return ((WorldGrammarVisitor<? extends T>)visitor).visitUpAndDown(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UpAndDownContext upAndDown() throws RecognitionException {
		UpAndDownContext _localctx = new UpAndDownContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_upAndDown);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(18);
			((UpAndDownContext)_localctx).u = match(NAME);
			}
			setState(19);
			match(COLON);
			{
			setState(20);
			((UpAndDownContext)_localctx).d = match(NAME);
			}

			                ((UpAndDownContext)_localctx).up =  ((UpAndDownContext)_localctx).u.getText();
			                ((UpAndDownContext)_localctx).down =  ((UpAndDownContext)_localctx).d.getText();
			            
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
	public static class LineContext extends ParserRuleContext {
		public List<String> world;
		public LevelNumContext levelNum;
		public LevelNameContext levelName;
		public UpAndDownContext upAndDown;
		public TerminalNode LEVEL() { return getToken(WorldGrammar.LEVEL, 0); }
		public LevelNumContext levelNum() {
			return getRuleContext(LevelNumContext.class,0);
		}
		public LevelNameContext levelName() {
			return getRuleContext(LevelNameContext.class,0);
		}
		public UpAndDownContext upAndDown() {
			return getRuleContext(UpAndDownContext.class,0);
		}
		public LineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_line; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorldGrammarListener ) ((WorldGrammarListener)listener).enterLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorldGrammarListener ) ((WorldGrammarListener)listener).exitLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorldGrammarVisitor ) return ((WorldGrammarVisitor<? extends T>)visitor).visitLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LineContext line() throws RecognitionException {
		LineContext _localctx = new LineContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_line);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(23);
			match(LEVEL);
			setState(24);
			((LineContext)_localctx).levelNum = levelNum();
			setState(25);
			((LineContext)_localctx).levelName = levelName();
			setState(26);
			((LineContext)_localctx).upAndDown = upAndDown();

			                ((LineContext)_localctx).world =  new ArrayList<>();
			                _localctx.world.add(((LineContext)_localctx).levelNum.num);
			                _localctx.world.add(((LineContext)_localctx).levelName.name);
			                _localctx.world.add(((LineContext)_localctx).upAndDown.up);
			                _localctx.world.add(((LineContext)_localctx).upAndDown.down);
			                _localctx.world.add(String.valueOf(_localctx.start.getLine()));
			            
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
		public List<List<String>> levels;
		public LineContext line;
		public TerminalNode EOF() { return getToken(WorldGrammar.EOF, 0); }
		public List<LineContext> line() {
			return getRuleContexts(LineContext.class);
		}
		public LineContext line(int i) {
			return getRuleContext(LineContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorldGrammarListener ) ((WorldGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorldGrammarListener ) ((WorldGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorldGrammarVisitor ) return ((WorldGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_file);

		            ((FileContext)_localctx).levels =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(32); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(29);
				((FileContext)_localctx).line = line();

				                _localctx.levels.add(((FileContext)_localctx).line.world);
				            
				}
				}
				setState(34); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==LEVEL );
			setState(36);
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
		"\u0004\u0001\u0007\'\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0004\u0004!\b\u0004\u000b"+
		"\u0004\f\u0004\"\u0001\u0004\u0001\u0004\u0001\u0004\u0000\u0000\u0005"+
		"\u0000\u0002\u0004\u0006\b\u0000\u0000\"\u0000\n\u0001\u0000\u0000\u0000"+
		"\u0002\u000e\u0001\u0000\u0000\u0000\u0004\u0012\u0001\u0000\u0000\u0000"+
		"\u0006\u0017\u0001\u0000\u0000\u0000\b \u0001\u0000\u0000\u0000\n\u000b"+
		"\u0005\u0003\u0000\u0000\u000b\f\u0005\u0002\u0000\u0000\f\r\u0006\u0000"+
		"\uffff\uffff\u0000\r\u0001\u0001\u0000\u0000\u0000\u000e\u000f\u0005\u0004"+
		"\u0000\u0000\u000f\u0010\u0005\u0002\u0000\u0000\u0010\u0011\u0006\u0001"+
		"\uffff\uffff\u0000\u0011\u0003\u0001\u0000\u0000\u0000\u0012\u0013\u0005"+
		"\u0004\u0000\u0000\u0013\u0014\u0005\u0002\u0000\u0000\u0014\u0015\u0005"+
		"\u0004\u0000\u0000\u0015\u0016\u0006\u0002\uffff\uffff\u0000\u0016\u0005"+
		"\u0001\u0000\u0000\u0000\u0017\u0018\u0005\u0001\u0000\u0000\u0018\u0019"+
		"\u0003\u0000\u0000\u0000\u0019\u001a\u0003\u0002\u0001\u0000\u001a\u001b"+
		"\u0003\u0004\u0002\u0000\u001b\u001c\u0006\u0003\uffff\uffff\u0000\u001c"+
		"\u0007\u0001\u0000\u0000\u0000\u001d\u001e\u0003\u0006\u0003\u0000\u001e"+
		"\u001f\u0006\u0004\uffff\uffff\u0000\u001f!\u0001\u0000\u0000\u0000 \u001d"+
		"\u0001\u0000\u0000\u0000!\"\u0001\u0000\u0000\u0000\" \u0001\u0000\u0000"+
		"\u0000\"#\u0001\u0000\u0000\u0000#$\u0001\u0000\u0000\u0000$%\u0005\u0000"+
		"\u0000\u0001%\t\u0001\u0000\u0000\u0000\u0001\"";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}