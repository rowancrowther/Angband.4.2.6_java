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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/RealmGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars;

    import uk.co.jackoftrades.backend.parser.realm.RealmParseRecord;

    import java.util.List;
    import java.util.ArrayList;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class RealmGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		RECORD_COUNT=1, NAME=2, STAT=3, VERB=4, SPELL_NOUN=5, BOOK_NOUN=6, INTEGER=7, 
		COMMENT=8, EOL=9, STRING=10, FREE_TEXT_EOL=11;
	public static final int
		RULE_recordCount = 0, RULE_name = 1, RULE_stat = 2, RULE_verb = 3, RULE_spellNoun = 4, 
		RULE_bookNoun = 5, RULE_realm = 6, RULE_file = 7;
	private static String[] makeRuleNames() {
		return new String[] {
			"recordCount", "name", "stat", "verb", "spellNoun", "bookNoun", "realm", 
			"file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'record-count:'", "'name:'", "'stat:'", "'verb:'", "'spell-noun:'", 
			"'book-noun:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "RECORD_COUNT", "NAME", "STAT", "VERB", "SPELL_NOUN", "BOOK_NOUN", 
			"INTEGER", "COMMENT", "EOL", "STRING", "FREE_TEXT_EOL"
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
	public String getGrammarFileName() { return "RealmGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public RealmGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RecordCountContext extends ParserRuleContext {
		public String count;
		public Token INTEGER;
		public TerminalNode RECORD_COUNT() { return getToken(RealmGrammar.RECORD_COUNT, 0); }
		public TerminalNode INTEGER() { return getToken(RealmGrammar.INTEGER, 0); }
		public RecordCountContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_recordCount; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RealmGrammarListener ) ((RealmGrammarListener)listener).enterRecordCount(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RealmGrammarListener ) ((RealmGrammarListener)listener).exitRecordCount(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RealmGrammarVisitor ) return ((RealmGrammarVisitor<? extends T>)visitor).visitRecordCount(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RecordCountContext recordCount() throws RecognitionException {
		RecordCountContext _localctx = new RecordCountContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_recordCount);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(16);
			match(RECORD_COUNT);
			setState(17);
			((RecordCountContext)_localctx).INTEGER = match(INTEGER);
			 ((RecordCountContext)_localctx).count =  ((RecordCountContext)_localctx).INTEGER.getText(); 
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
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public int line;
		public Token STRING;
		public TerminalNode NAME() { return getToken(RealmGrammar.NAME, 0); }
		public TerminalNode STRING() { return getToken(RealmGrammar.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RealmGrammarListener ) ((RealmGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RealmGrammarListener ) ((RealmGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RealmGrammarVisitor ) return ((RealmGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(20);
			match(NAME);
			setState(21);
			((NameContext)_localctx).STRING = match(STRING);
			 ((NameContext)_localctx).nameStr =  ((NameContext)_localctx).STRING.getText(); ((NameContext)_localctx).line =  _localctx.start.getLine(); 
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
	public static class StatContext extends ParserRuleContext {
		public String statStr;
		public Token STRING;
		public TerminalNode STAT() { return getToken(RealmGrammar.STAT, 0); }
		public TerminalNode STRING() { return getToken(RealmGrammar.STRING, 0); }
		public StatContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stat; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RealmGrammarListener ) ((RealmGrammarListener)listener).enterStat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RealmGrammarListener ) ((RealmGrammarListener)listener).exitStat(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RealmGrammarVisitor ) return ((RealmGrammarVisitor<? extends T>)visitor).visitStat(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatContext stat() throws RecognitionException {
		StatContext _localctx = new StatContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_stat);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(24);
			match(STAT);
			setState(25);
			((StatContext)_localctx).STRING = match(STRING);
			 ((StatContext)_localctx).statStr =  ((StatContext)_localctx).STRING.getText(); 
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
	public static class VerbContext extends ParserRuleContext {
		public String verbStr;
		public Token STRING;
		public TerminalNode VERB() { return getToken(RealmGrammar.VERB, 0); }
		public TerminalNode STRING() { return getToken(RealmGrammar.STRING, 0); }
		public VerbContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_verb; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RealmGrammarListener ) ((RealmGrammarListener)listener).enterVerb(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RealmGrammarListener ) ((RealmGrammarListener)listener).exitVerb(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RealmGrammarVisitor ) return ((RealmGrammarVisitor<? extends T>)visitor).visitVerb(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VerbContext verb() throws RecognitionException {
		VerbContext _localctx = new VerbContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_verb);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(28);
			match(VERB);
			setState(29);
			((VerbContext)_localctx).STRING = match(STRING);
			 ((VerbContext)_localctx).verbStr =  ((VerbContext)_localctx).STRING.getText(); 
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
	public static class SpellNounContext extends ParserRuleContext {
		public String spellNounStr;
		public Token STRING;
		public TerminalNode SPELL_NOUN() { return getToken(RealmGrammar.SPELL_NOUN, 0); }
		public TerminalNode STRING() { return getToken(RealmGrammar.STRING, 0); }
		public SpellNounContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_spellNoun; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RealmGrammarListener ) ((RealmGrammarListener)listener).enterSpellNoun(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RealmGrammarListener ) ((RealmGrammarListener)listener).exitSpellNoun(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RealmGrammarVisitor ) return ((RealmGrammarVisitor<? extends T>)visitor).visitSpellNoun(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SpellNounContext spellNoun() throws RecognitionException {
		SpellNounContext _localctx = new SpellNounContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_spellNoun);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(32);
			match(SPELL_NOUN);
			setState(33);
			((SpellNounContext)_localctx).STRING = match(STRING);
			 ((SpellNounContext)_localctx).spellNounStr =  ((SpellNounContext)_localctx).STRING.getText(); 
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
	public static class BookNounContext extends ParserRuleContext {
		public String bookNounStr;
		public Token STRING;
		public TerminalNode BOOK_NOUN() { return getToken(RealmGrammar.BOOK_NOUN, 0); }
		public TerminalNode STRING() { return getToken(RealmGrammar.STRING, 0); }
		public BookNounContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bookNoun; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RealmGrammarListener ) ((RealmGrammarListener)listener).enterBookNoun(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RealmGrammarListener ) ((RealmGrammarListener)listener).exitBookNoun(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RealmGrammarVisitor ) return ((RealmGrammarVisitor<? extends T>)visitor).visitBookNoun(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BookNounContext bookNoun() throws RecognitionException {
		BookNounContext _localctx = new BookNounContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_bookNoun);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(36);
			match(BOOK_NOUN);
			setState(37);
			((BookNounContext)_localctx).STRING = match(STRING);
			 ((BookNounContext)_localctx).bookNounStr =  ((BookNounContext)_localctx).STRING.getText(); 
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
	public static class RealmContext extends ParserRuleContext {
		public RealmParseRecord record;
		public NameContext name;
		public StatContext stat;
		public VerbContext verb;
		public SpellNounContext spellNoun;
		public BookNounContext bookNoun;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public List<StatContext> stat() {
			return getRuleContexts(StatContext.class);
		}
		public StatContext stat(int i) {
			return getRuleContext(StatContext.class,i);
		}
		public List<VerbContext> verb() {
			return getRuleContexts(VerbContext.class);
		}
		public VerbContext verb(int i) {
			return getRuleContext(VerbContext.class,i);
		}
		public List<SpellNounContext> spellNoun() {
			return getRuleContexts(SpellNounContext.class);
		}
		public SpellNounContext spellNoun(int i) {
			return getRuleContext(SpellNounContext.class,i);
		}
		public List<BookNounContext> bookNoun() {
			return getRuleContexts(BookNounContext.class);
		}
		public BookNounContext bookNoun(int i) {
			return getRuleContext(BookNounContext.class,i);
		}
		public RealmContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_realm; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RealmGrammarListener ) ((RealmGrammarListener)listener).enterRealm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RealmGrammarListener ) ((RealmGrammarListener)listener).exitRealm(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RealmGrammarVisitor ) return ((RealmGrammarVisitor<? extends T>)visitor).visitRealm(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RealmContext realm() throws RecognitionException {
		RealmContext _localctx = new RealmContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_realm);

		            String nameInit = "";
		            String statInit = "";
		            String verbInit = "";
		            String spellNounInit = "";
		            String bookNounInit = "";
		            int lineInit = 0;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(40);
			((RealmContext)_localctx).name = name();
			 lineInit = ((RealmContext)_localctx).name.line;
			                   nameInit = ((RealmContext)_localctx).name.nameStr; 
			setState(54); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(54);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case STAT:
					{
					setState(42);
					((RealmContext)_localctx).stat = stat();
					 statInit = ((RealmContext)_localctx).stat.statStr; 
					}
					break;
				case VERB:
					{
					setState(45);
					((RealmContext)_localctx).verb = verb();
					 verbInit = ((RealmContext)_localctx).verb.verbStr; 
					}
					break;
				case SPELL_NOUN:
					{
					setState(48);
					((RealmContext)_localctx).spellNoun = spellNoun();
					 spellNounInit = ((RealmContext)_localctx).spellNoun.spellNounStr; 
					}
					break;
				case BOOK_NOUN:
					{
					setState(51);
					((RealmContext)_localctx).bookNoun = bookNoun();
					 bookNounInit = ((RealmContext)_localctx).bookNoun.bookNounStr; 
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(56); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 120L) != 0) );
			}
			_ctx.stop = _input.LT(-1);

			            ((RealmContext)_localctx).record =  new RealmParseRecord(nameInit, statInit, verbInit,
			                        spellNounInit, bookNounInit, lineInit);
			        
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
		public List<RealmParseRecord> realms;
		public String declaredRecordCount;
		public RecordCountContext recordCount;
		public RealmContext realm;
		public RecordCountContext recordCount() {
			return getRuleContext(RecordCountContext.class,0);
		}
		public TerminalNode EOF() { return getToken(RealmGrammar.EOF, 0); }
		public List<RealmContext> realm() {
			return getRuleContexts(RealmContext.class);
		}
		public RealmContext realm(int i) {
			return getRuleContext(RealmContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RealmGrammarListener ) ((RealmGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RealmGrammarListener ) ((RealmGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RealmGrammarVisitor ) return ((RealmGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_file);

		            ((FileContext)_localctx).realms =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58);
			((FileContext)_localctx).recordCount = recordCount();
			 ((FileContext)_localctx).declaredRecordCount =  ((FileContext)_localctx).recordCount.count; 
			setState(63); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(60);
				((FileContext)_localctx).realm = realm();
				 _localctx.realms.add(((FileContext)_localctx).realm.record); 
				}
				}
				setState(65); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(67);
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
		"\u0004\u0001\u000bF\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0004\u00067\b\u0006\u000b\u0006\f\u00068\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0004\u0007@\b\u0007"+
		"\u000b\u0007\f\u0007A\u0001\u0007\u0001\u0007\u0001\u0007\u0000\u0000"+
		"\b\u0000\u0002\u0004\u0006\b\n\f\u000e\u0000\u0000B\u0000\u0010\u0001"+
		"\u0000\u0000\u0000\u0002\u0014\u0001\u0000\u0000\u0000\u0004\u0018\u0001"+
		"\u0000\u0000\u0000\u0006\u001c\u0001\u0000\u0000\u0000\b \u0001\u0000"+
		"\u0000\u0000\n$\u0001\u0000\u0000\u0000\f(\u0001\u0000\u0000\u0000\u000e"+
		":\u0001\u0000\u0000\u0000\u0010\u0011\u0005\u0001\u0000\u0000\u0011\u0012"+
		"\u0005\u0007\u0000\u0000\u0012\u0013\u0006\u0000\uffff\uffff\u0000\u0013"+
		"\u0001\u0001\u0000\u0000\u0000\u0014\u0015\u0005\u0002\u0000\u0000\u0015"+
		"\u0016\u0005\n\u0000\u0000\u0016\u0017\u0006\u0001\uffff\uffff\u0000\u0017"+
		"\u0003\u0001\u0000\u0000\u0000\u0018\u0019\u0005\u0003\u0000\u0000\u0019"+
		"\u001a\u0005\n\u0000\u0000\u001a\u001b\u0006\u0002\uffff\uffff\u0000\u001b"+
		"\u0005\u0001\u0000\u0000\u0000\u001c\u001d\u0005\u0004\u0000\u0000\u001d"+
		"\u001e\u0005\n\u0000\u0000\u001e\u001f\u0006\u0003\uffff\uffff\u0000\u001f"+
		"\u0007\u0001\u0000\u0000\u0000 !\u0005\u0005\u0000\u0000!\"\u0005\n\u0000"+
		"\u0000\"#\u0006\u0004\uffff\uffff\u0000#\t\u0001\u0000\u0000\u0000$%\u0005"+
		"\u0006\u0000\u0000%&\u0005\n\u0000\u0000&\'\u0006\u0005\uffff\uffff\u0000"+
		"\'\u000b\u0001\u0000\u0000\u0000()\u0003\u0002\u0001\u0000)6\u0006\u0006"+
		"\uffff\uffff\u0000*+\u0003\u0004\u0002\u0000+,\u0006\u0006\uffff\uffff"+
		"\u0000,7\u0001\u0000\u0000\u0000-.\u0003\u0006\u0003\u0000./\u0006\u0006"+
		"\uffff\uffff\u0000/7\u0001\u0000\u0000\u000001\u0003\b\u0004\u000012\u0006"+
		"\u0006\uffff\uffff\u000027\u0001\u0000\u0000\u000034\u0003\n\u0005\u0000"+
		"45\u0006\u0006\uffff\uffff\u000057\u0001\u0000\u0000\u00006*\u0001\u0000"+
		"\u0000\u00006-\u0001\u0000\u0000\u000060\u0001\u0000\u0000\u000063\u0001"+
		"\u0000\u0000\u000078\u0001\u0000\u0000\u000086\u0001\u0000\u0000\u0000"+
		"89\u0001\u0000\u0000\u00009\r\u0001\u0000\u0000\u0000:;\u0003\u0000\u0000"+
		"\u0000;?\u0006\u0007\uffff\uffff\u0000<=\u0003\f\u0006\u0000=>\u0006\u0007"+
		"\uffff\uffff\u0000>@\u0001\u0000\u0000\u0000?<\u0001\u0000\u0000\u0000"+
		"@A\u0001\u0000\u0000\u0000A?\u0001\u0000\u0000\u0000AB\u0001\u0000\u0000"+
		"\u0000BC\u0001\u0000\u0000\u0000CD\u0005\u0000\u0000\u0001D\u000f\u0001"+
		"\u0000\u0000\u0000\u000368A";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}