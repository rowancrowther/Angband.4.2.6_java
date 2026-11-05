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

// Generated from /home/rowan/IdeaProjects/Angband.4.2.6_java/src/main/java/uk/co/jackoftradesltd/backend/parser/grammars/SummonGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.summon;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.backend.parser.summon.SummonParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class SummonGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, MSGT=2, UNIQUES=3, BASE=4, RACE_FLAG=5, FALLBACK=6, DESC=7, INTEGER=8, 
		COMMENT=9, EOL=10, FLAG=11, STRING=12;
	public static final int
		RULE_name = 0, RULE_msgt = 1, RULE_uniques = 2, RULE_base = 3, RULE_raceFlag = 4, 
		RULE_fallback = 5, RULE_desc = 6, RULE_summon = 7, RULE_file = 8;
	private static String[] makeRuleNames() {
		return new String[] {
			"name", "msgt", "uniques", "base", "raceFlag", "fallback", "desc", "summon", 
			"file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'msgt:'", "'uniques:'", "'base:'", "'race-flag:'", 
			"'fallback:'", "'desc:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "MSGT", "UNIQUES", "BASE", "RACE_FLAG", "FALLBACK", "DESC", 
			"INTEGER", "COMMENT", "EOL", "FLAG", "STRING"
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
	public String getGrammarFileName() { return "SummonGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SummonGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public int lineNo;
		public Token STRING;
		public TerminalNode NAME() { return getToken(SummonGrammar.NAME, 0); }
		public TerminalNode STRING() { return getToken(SummonGrammar.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SummonGrammarListener ) ((SummonGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SummonGrammarListener ) ((SummonGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SummonGrammarVisitor ) return ((SummonGrammarVisitor<? extends T>)visitor).visitName(this);
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
			                          ((NameContext)_localctx).lineNo =  _localctx.start.getLine(); 
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
	public static class MsgtContext extends ParserRuleContext {
		public String msgtStr;
		public Token FLAG;
		public TerminalNode MSGT() { return getToken(SummonGrammar.MSGT, 0); }
		public TerminalNode FLAG() { return getToken(SummonGrammar.FLAG, 0); }
		public MsgtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_msgt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SummonGrammarListener ) ((SummonGrammarListener)listener).enterMsgt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SummonGrammarListener ) ((SummonGrammarListener)listener).exitMsgt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SummonGrammarVisitor ) return ((SummonGrammarVisitor<? extends T>)visitor).visitMsgt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MsgtContext msgt() throws RecognitionException {
		MsgtContext _localctx = new MsgtContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_msgt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(22);
			match(MSGT);
			setState(23);
			((MsgtContext)_localctx).FLAG = match(FLAG);
			 ((MsgtContext)_localctx).msgtStr =  ((MsgtContext)_localctx).FLAG.getText(); 
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
	public static class UniquesContext extends ParserRuleContext {
		public String uniquesStr;
		public Token INTEGER;
		public TerminalNode UNIQUES() { return getToken(SummonGrammar.UNIQUES, 0); }
		public TerminalNode INTEGER() { return getToken(SummonGrammar.INTEGER, 0); }
		public UniquesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_uniques; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SummonGrammarListener ) ((SummonGrammarListener)listener).enterUniques(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SummonGrammarListener ) ((SummonGrammarListener)listener).exitUniques(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SummonGrammarVisitor ) return ((SummonGrammarVisitor<? extends T>)visitor).visitUniques(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UniquesContext uniques() throws RecognitionException {
		UniquesContext _localctx = new UniquesContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_uniques);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(26);
			match(UNIQUES);
			setState(27);
			((UniquesContext)_localctx).INTEGER = match(INTEGER);
			 ((UniquesContext)_localctx).uniquesStr =  ((UniquesContext)_localctx).INTEGER.getText(); 
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
	public static class BaseContext extends ParserRuleContext {
		public List<String> baseStr;
		public Token STRING;
		public List<TerminalNode> BASE() { return getTokens(SummonGrammar.BASE); }
		public TerminalNode BASE(int i) {
			return getToken(SummonGrammar.BASE, i);
		}
		public List<TerminalNode> STRING() { return getTokens(SummonGrammar.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(SummonGrammar.STRING, i);
		}
		public BaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_base; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SummonGrammarListener ) ((SummonGrammarListener)listener).enterBase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SummonGrammarListener ) ((SummonGrammarListener)listener).exitBase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SummonGrammarVisitor ) return ((SummonGrammarVisitor<? extends T>)visitor).visitBase(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BaseContext base() throws RecognitionException {
		BaseContext _localctx = new BaseContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_base);

		            ((BaseContext)_localctx).baseStr =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(33); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(30);
				match(BASE);
				setState(31);
				((BaseContext)_localctx).STRING = match(STRING);
				 _localctx.baseStr.add(((BaseContext)_localctx).STRING.getText()); 
				}
				}
				setState(35); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==BASE );
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
	public static class RaceFlagContext extends ParserRuleContext {
		public String flagStr;
		public Token FLAG;
		public TerminalNode RACE_FLAG() { return getToken(SummonGrammar.RACE_FLAG, 0); }
		public TerminalNode FLAG() { return getToken(SummonGrammar.FLAG, 0); }
		public RaceFlagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_raceFlag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SummonGrammarListener ) ((SummonGrammarListener)listener).enterRaceFlag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SummonGrammarListener ) ((SummonGrammarListener)listener).exitRaceFlag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SummonGrammarVisitor ) return ((SummonGrammarVisitor<? extends T>)visitor).visitRaceFlag(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RaceFlagContext raceFlag() throws RecognitionException {
		RaceFlagContext _localctx = new RaceFlagContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_raceFlag);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(37);
			match(RACE_FLAG);
			setState(38);
			((RaceFlagContext)_localctx).FLAG = match(FLAG);
			 ((RaceFlagContext)_localctx).flagStr =  ((RaceFlagContext)_localctx).FLAG.getText(); 
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
	public static class FallbackContext extends ParserRuleContext {
		public String fallbackStr;
		public Token STRING;
		public TerminalNode FALLBACK() { return getToken(SummonGrammar.FALLBACK, 0); }
		public TerminalNode STRING() { return getToken(SummonGrammar.STRING, 0); }
		public FallbackContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fallback; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SummonGrammarListener ) ((SummonGrammarListener)listener).enterFallback(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SummonGrammarListener ) ((SummonGrammarListener)listener).exitFallback(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SummonGrammarVisitor ) return ((SummonGrammarVisitor<? extends T>)visitor).visitFallback(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FallbackContext fallback() throws RecognitionException {
		FallbackContext _localctx = new FallbackContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_fallback);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(41);
			match(FALLBACK);
			setState(42);
			((FallbackContext)_localctx).STRING = match(STRING);
			 ((FallbackContext)_localctx).fallbackStr =  ((FallbackContext)_localctx).STRING.getText(); 
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
		public String description;
		public Token STRING;
		public TerminalNode DESC() { return getToken(SummonGrammar.DESC, 0); }
		public TerminalNode STRING() { return getToken(SummonGrammar.STRING, 0); }
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_desc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SummonGrammarListener ) ((SummonGrammarListener)listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SummonGrammarListener ) ((SummonGrammarListener)listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SummonGrammarVisitor ) return ((SummonGrammarVisitor<? extends T>)visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_desc);

		            StringBuilder sb = new StringBuilder();
		        
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(45);
			match(DESC);
			setState(46);
			((DescContext)_localctx).STRING = match(STRING);
			 sb.append(((DescContext)_localctx).STRING.getText()); 
			}
			}
			_ctx.stop = _input.LT(-1);

			            ((DescContext)_localctx).description =  sb.toString();
			        
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
	public static class SummonContext extends ParserRuleContext {
		public SummonParseRecord record;
		public NameContext name;
		public MsgtContext msgt;
		public UniquesContext uniques;
		public BaseContext base;
		public RaceFlagContext raceFlag;
		public FallbackContext fallback;
		public DescContext desc;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public MsgtContext msgt() {
			return getRuleContext(MsgtContext.class,0);
		}
		public UniquesContext uniques() {
			return getRuleContext(UniquesContext.class,0);
		}
		public DescContext desc() {
			return getRuleContext(DescContext.class,0);
		}
		public BaseContext base() {
			return getRuleContext(BaseContext.class,0);
		}
		public RaceFlagContext raceFlag() {
			return getRuleContext(RaceFlagContext.class,0);
		}
		public FallbackContext fallback() {
			return getRuleContext(FallbackContext.class,0);
		}
		public SummonContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_summon; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SummonGrammarListener ) ((SummonGrammarListener)listener).enterSummon(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SummonGrammarListener ) ((SummonGrammarListener)listener).exitSummon(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SummonGrammarVisitor ) return ((SummonGrammarVisitor<? extends T>)visitor).visitSummon(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SummonContext summon() throws RecognitionException {
		SummonContext _localctx = new SummonContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_summon);

		            String nameInit = "";
		            String msgTypeInit = "";
		            String uniquesInit = "";
		            List<String> basesInit = new ArrayList<>();
		            String raceFlagInit = "";
		            String fallbackInit = "";
		            String descInit = "";
		            int line = 0;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(49);
			((SummonContext)_localctx).name = name();
			 line = ((SummonContext)_localctx).name.lineNo;
			                   nameInit = ((SummonContext)_localctx).name.nameStr; 
			setState(51);
			((SummonContext)_localctx).msgt = msgt();
			 msgTypeInit = ((SummonContext)_localctx).msgt.msgtStr; 
			setState(53);
			((SummonContext)_localctx).uniques = uniques();
			 uniquesInit = ((SummonContext)_localctx).uniques.uniquesStr; 
			setState(58);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BASE) {
				{
				setState(55);
				((SummonContext)_localctx).base = base();
				 basesInit = ((SummonContext)_localctx).base.baseStr; 
				}
			}

			setState(63);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==RACE_FLAG) {
				{
				setState(60);
				((SummonContext)_localctx).raceFlag = raceFlag();
				 raceFlagInit = ((SummonContext)_localctx).raceFlag.flagStr; 
				}
			}

			setState(68);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FALLBACK) {
				{
				setState(65);
				((SummonContext)_localctx).fallback = fallback();
				 fallbackInit = ((SummonContext)_localctx).fallback.fallbackStr; 
				}
			}

			setState(70);
			((SummonContext)_localctx).desc = desc();
			 descInit = ((SummonContext)_localctx).desc.description; 
			}
			_ctx.stop = _input.LT(-1);

			            ((SummonContext)_localctx).record =  new SummonParseRecord(nameInit, msgTypeInit,
			                uniquesInit, basesInit, raceFlagInit, fallbackInit,
			                descInit, line);
			        
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
		public List<SummonParseRecord> summons;
		public SummonContext summon;
		public TerminalNode EOF() { return getToken(SummonGrammar.EOF, 0); }
		public List<SummonContext> summon() {
			return getRuleContexts(SummonContext.class);
		}
		public SummonContext summon(int i) {
			return getRuleContext(SummonContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SummonGrammarListener ) ((SummonGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SummonGrammarListener ) ((SummonGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SummonGrammarVisitor ) return ((SummonGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_file);

		            ((FileContext)_localctx).summons =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(76); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(73);
				((FileContext)_localctx).summon = summon();
				 _localctx.summons.add(((FileContext)_localctx).summon.record); 
				}
				}
				setState(78); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(80);
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
		"\u0004\u0001\fS\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0004\u0003\"\b\u0003"+
		"\u000b\u0003\f\u0003#\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007"+
		";\b\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007@\b\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0003\u0007E\b\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0004\bM\b\b\u000b\b\f\bN\u0001"+
		"\b\u0001\b\u0001\b\u0000\u0000\t\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010"+
		"\u0000\u0000N\u0000\u0012\u0001\u0000\u0000\u0000\u0002\u0016\u0001\u0000"+
		"\u0000\u0000\u0004\u001a\u0001\u0000\u0000\u0000\u0006!\u0001\u0000\u0000"+
		"\u0000\b%\u0001\u0000\u0000\u0000\n)\u0001\u0000\u0000\u0000\f-\u0001"+
		"\u0000\u0000\u0000\u000e1\u0001\u0000\u0000\u0000\u0010L\u0001\u0000\u0000"+
		"\u0000\u0012\u0013\u0005\u0001\u0000\u0000\u0013\u0014\u0005\f\u0000\u0000"+
		"\u0014\u0015\u0006\u0000\uffff\uffff\u0000\u0015\u0001\u0001\u0000\u0000"+
		"\u0000\u0016\u0017\u0005\u0002\u0000\u0000\u0017\u0018\u0005\u000b\u0000"+
		"\u0000\u0018\u0019\u0006\u0001\uffff\uffff\u0000\u0019\u0003\u0001\u0000"+
		"\u0000\u0000\u001a\u001b\u0005\u0003\u0000\u0000\u001b\u001c\u0005\b\u0000"+
		"\u0000\u001c\u001d\u0006\u0002\uffff\uffff\u0000\u001d\u0005\u0001\u0000"+
		"\u0000\u0000\u001e\u001f\u0005\u0004\u0000\u0000\u001f \u0005\f\u0000"+
		"\u0000 \"\u0006\u0003\uffff\uffff\u0000!\u001e\u0001\u0000\u0000\u0000"+
		"\"#\u0001\u0000\u0000\u0000#!\u0001\u0000\u0000\u0000#$\u0001\u0000\u0000"+
		"\u0000$\u0007\u0001\u0000\u0000\u0000%&\u0005\u0005\u0000\u0000&\'\u0005"+
		"\u000b\u0000\u0000\'(\u0006\u0004\uffff\uffff\u0000(\t\u0001\u0000\u0000"+
		"\u0000)*\u0005\u0006\u0000\u0000*+\u0005\f\u0000\u0000+,\u0006\u0005\uffff"+
		"\uffff\u0000,\u000b\u0001\u0000\u0000\u0000-.\u0005\u0007\u0000\u0000"+
		"./\u0005\f\u0000\u0000/0\u0006\u0006\uffff\uffff\u00000\r\u0001\u0000"+
		"\u0000\u000012\u0003\u0000\u0000\u000023\u0006\u0007\uffff\uffff\u0000"+
		"34\u0003\u0002\u0001\u000045\u0006\u0007\uffff\uffff\u000056\u0003\u0004"+
		"\u0002\u00006:\u0006\u0007\uffff\uffff\u000078\u0003\u0006\u0003\u0000"+
		"89\u0006\u0007\uffff\uffff\u00009;\u0001\u0000\u0000\u0000:7\u0001\u0000"+
		"\u0000\u0000:;\u0001\u0000\u0000\u0000;?\u0001\u0000\u0000\u0000<=\u0003"+
		"\b\u0004\u0000=>\u0006\u0007\uffff\uffff\u0000>@\u0001\u0000\u0000\u0000"+
		"?<\u0001\u0000\u0000\u0000?@\u0001\u0000\u0000\u0000@D\u0001\u0000\u0000"+
		"\u0000AB\u0003\n\u0005\u0000BC\u0006\u0007\uffff\uffff\u0000CE\u0001\u0000"+
		"\u0000\u0000DA\u0001\u0000\u0000\u0000DE\u0001\u0000\u0000\u0000EF\u0001"+
		"\u0000\u0000\u0000FG\u0003\f\u0006\u0000GH\u0006\u0007\uffff\uffff\u0000"+
		"H\u000f\u0001\u0000\u0000\u0000IJ\u0003\u000e\u0007\u0000JK\u0006\b\uffff"+
		"\uffff\u0000KM\u0001\u0000\u0000\u0000LI\u0001\u0000\u0000\u0000MN\u0001"+
		"\u0000\u0000\u0000NL\u0001\u0000\u0000\u0000NO\u0001\u0000\u0000\u0000"+
		"OP\u0001\u0000\u0000\u0000PQ\u0005\u0000\u0000\u0001Q\u0011\u0001\u0000"+
		"\u0000\u0000\u0005#:?DN";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}