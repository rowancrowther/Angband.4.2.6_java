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

// Generated from QuestGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.quest;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.backend.parser.quest.QuestParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class QuestGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, LEVEL=2, RACE=3, NUMBER=4, INTEGER=5, COMMENT=6, INDENT=7, EOL=8, 
		STRING=9;
	public static final int
		RULE_name = 0, RULE_level = 1, RULE_race = 2, RULE_number = 3, RULE_quest = 4, 
		RULE_file = 5;
	private static String[] makeRuleNames() {
		return new String[] {
			"name", "level", "race", "number", "quest", "file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'level:'", "'race:'", "'number:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "LEVEL", "RACE", "NUMBER", "INTEGER", "COMMENT", "INDENT", 
			"EOL", "STRING"
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
	public String getGrammarFileName() { return "QuestGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public QuestGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public int line;
		public Token STRING;
		public TerminalNode NAME() { return getToken(QuestGrammar.NAME, 0); }
		public TerminalNode STRING() { return getToken(QuestGrammar.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuestGrammarListener ) ((QuestGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuestGrammarListener ) ((QuestGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuestGrammarVisitor ) return ((QuestGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(12);
			match(NAME);
			setState(13);
			((NameContext)_localctx).STRING = match(STRING);
			 ((NameContext)_localctx).nameStr =  ((NameContext)_localctx).STRING.getText();
			                          ((NameContext)_localctx).line =  _localctx.start.getLine(); 
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
	public static class LevelContext extends ParserRuleContext {
		public String levelStr;
		public Token INTEGER;
		public TerminalNode LEVEL() { return getToken(QuestGrammar.LEVEL, 0); }
		public TerminalNode INTEGER() { return getToken(QuestGrammar.INTEGER, 0); }
		public LevelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_level; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuestGrammarListener ) ((QuestGrammarListener)listener).enterLevel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuestGrammarListener ) ((QuestGrammarListener)listener).exitLevel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuestGrammarVisitor ) return ((QuestGrammarVisitor<? extends T>)visitor).visitLevel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LevelContext level() throws RecognitionException {
		LevelContext _localctx = new LevelContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_level);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(16);
			match(LEVEL);
			setState(17);
			((LevelContext)_localctx).INTEGER = match(INTEGER);
			 ((LevelContext)_localctx).levelStr =  ((LevelContext)_localctx).INTEGER.getText(); 
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
	public static class RaceContext extends ParserRuleContext {
		public String monRace;
		public Token STRING;
		public TerminalNode RACE() { return getToken(QuestGrammar.RACE, 0); }
		public TerminalNode STRING() { return getToken(QuestGrammar.STRING, 0); }
		public RaceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_race; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuestGrammarListener ) ((QuestGrammarListener)listener).enterRace(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuestGrammarListener ) ((QuestGrammarListener)listener).exitRace(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuestGrammarVisitor ) return ((QuestGrammarVisitor<? extends T>)visitor).visitRace(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RaceContext race() throws RecognitionException {
		RaceContext _localctx = new RaceContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_race);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(20);
			match(RACE);
			setState(21);
			((RaceContext)_localctx).STRING = match(STRING);
			 ((RaceContext)_localctx).monRace =  ((RaceContext)_localctx).STRING.getText(); 
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
	public static class NumberContext extends ParserRuleContext {
		public String num;
		public Token INTEGER;
		public TerminalNode NUMBER() { return getToken(QuestGrammar.NUMBER, 0); }
		public TerminalNode INTEGER() { return getToken(QuestGrammar.INTEGER, 0); }
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuestGrammarListener ) ((QuestGrammarListener)listener).enterNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuestGrammarListener ) ((QuestGrammarListener)listener).exitNumber(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuestGrammarVisitor ) return ((QuestGrammarVisitor<? extends T>)visitor).visitNumber(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_number);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(24);
			match(NUMBER);
			setState(25);
			((NumberContext)_localctx).INTEGER = match(INTEGER);
			 ((NumberContext)_localctx).num =  ((NumberContext)_localctx).INTEGER.getText(); 
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
	public static class QuestContext extends ParserRuleContext {
		public QuestParseRecord q;
		public NameContext name;
		public LevelContext level;
		public RaceContext race;
		public NumberContext number;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public LevelContext level() {
			return getRuleContext(LevelContext.class,0);
		}
		public RaceContext race() {
			return getRuleContext(RaceContext.class,0);
		}
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public QuestContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_quest; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuestGrammarListener ) ((QuestGrammarListener)listener).enterQuest(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuestGrammarListener ) ((QuestGrammarListener)listener).exitQuest(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuestGrammarVisitor ) return ((QuestGrammarVisitor<? extends T>)visitor).visitQuest(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QuestContext quest() throws RecognitionException {
		QuestContext _localctx = new QuestContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_quest);

		            String nameInit = "";
		            String levelInit = "";
		            String raceInit = "";
		            String numberInit = "";
		            int line = 0;
		        
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(28);
			((QuestContext)_localctx).name = name();
			 line = ((QuestContext)_localctx).name.line; nameInit = ((QuestContext)_localctx).name.nameStr;
			setState(30);
			((QuestContext)_localctx).level = level();
			 levelInit = ((QuestContext)_localctx).level.levelStr; 
			setState(32);
			((QuestContext)_localctx).race = race();
			 raceInit = ((QuestContext)_localctx).race.monRace; 
			setState(34);
			((QuestContext)_localctx).number = number();
			 numberInit = ((QuestContext)_localctx).number.num; 
			}
			_ctx.stop = _input.LT(-1);

			            ((QuestContext)_localctx).q =  new QuestParseRecord(nameInit, levelInit,
			                    raceInit, numberInit, line);
			        
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
		public List<QuestParseRecord> quests;
		public QuestContext quest;
		public TerminalNode EOF() { return getToken(QuestGrammar.EOF, 0); }
		public List<QuestContext> quest() {
			return getRuleContexts(QuestContext.class);
		}
		public QuestContext quest(int i) {
			return getRuleContext(QuestContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QuestGrammarListener ) ((QuestGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QuestGrammarListener ) ((QuestGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QuestGrammarVisitor ) return ((QuestGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_file);

		            ((FileContext)_localctx).quests =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(40); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(37);
				((FileContext)_localctx).quest = quest();
				 _localctx.quests.add(((FileContext)_localctx).quest.q); 
				}
				}
				setState(42); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(44);
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
		"\u0004\u0001\t/\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0004"+
		"\u0005)\b\u0005\u000b\u0005\f\u0005*\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0000\u0000\u0006\u0000\u0002\u0004\u0006\b\n\u0000\u0000)\u0000\f\u0001"+
		"\u0000\u0000\u0000\u0002\u0010\u0001\u0000\u0000\u0000\u0004\u0014\u0001"+
		"\u0000\u0000\u0000\u0006\u0018\u0001\u0000\u0000\u0000\b\u001c\u0001\u0000"+
		"\u0000\u0000\n(\u0001\u0000\u0000\u0000\f\r\u0005\u0001\u0000\u0000\r"+
		"\u000e\u0005\t\u0000\u0000\u000e\u000f\u0006\u0000\uffff\uffff\u0000\u000f"+
		"\u0001\u0001\u0000\u0000\u0000\u0010\u0011\u0005\u0002\u0000\u0000\u0011"+
		"\u0012\u0005\u0005\u0000\u0000\u0012\u0013\u0006\u0001\uffff\uffff\u0000"+
		"\u0013\u0003\u0001\u0000\u0000\u0000\u0014\u0015\u0005\u0003\u0000\u0000"+
		"\u0015\u0016\u0005\t\u0000\u0000\u0016\u0017\u0006\u0002\uffff\uffff\u0000"+
		"\u0017\u0005\u0001\u0000\u0000\u0000\u0018\u0019\u0005\u0004\u0000\u0000"+
		"\u0019\u001a\u0005\u0005\u0000\u0000\u001a\u001b\u0006\u0003\uffff\uffff"+
		"\u0000\u001b\u0007\u0001\u0000\u0000\u0000\u001c\u001d\u0003\u0000\u0000"+
		"\u0000\u001d\u001e\u0006\u0004\uffff\uffff\u0000\u001e\u001f\u0003\u0002"+
		"\u0001\u0000\u001f \u0006\u0004\uffff\uffff\u0000 !\u0003\u0004\u0002"+
		"\u0000!\"\u0006\u0004\uffff\uffff\u0000\"#\u0003\u0006\u0003\u0000#$\u0006"+
		"\u0004\uffff\uffff\u0000$\t\u0001\u0000\u0000\u0000%&\u0003\b\u0004\u0000"+
		"&\'\u0006\u0005\uffff\uffff\u0000\')\u0001\u0000\u0000\u0000(%\u0001\u0000"+
		"\u0000\u0000)*\u0001\u0000\u0000\u0000*(\u0001\u0000\u0000\u0000*+\u0001"+
		"\u0000\u0000\u0000+,\u0001\u0000\u0000\u0000,-\u0005\u0000\u0000\u0001"+
		"-\u000b\u0001\u0000\u0000\u0000\u0001*";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}