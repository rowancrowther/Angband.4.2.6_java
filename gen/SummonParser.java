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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/Summon.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.middle.monsters.Summon;
    import uk.co.jackoftrades.middle.monsters.MonsterBase;
    import uk.co.jackoftrades.middle.enums.MessageType;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

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
public class SummonParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		COMMENT=1, EOL=2, NAME=3, MSGT=4, UNIQUES=5, BASE=6, RACE_FLAG=7, FALLBACK=8, 
		DESC=9, BOOLEAN=10, FLAG=11, TEXT=12;
	public static final int
		RULE_name = 0, RULE_msgt = 1, RULE_uniques = 2, RULE_base = 3, RULE_race_flag = 4, 
		RULE_fallback = 5, RULE_desc = 6, RULE_summon = 7, RULE_file = 8;
	private static String[] makeRuleNames() {
		return new String[] {
			"name", "msgt", "uniques", "base", "race_flag", "fallback", "desc", "summon", 
			"file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, "'name:'", "'msgt:'", "'uniques:'", "'base:'", "'race-flag:'", 
			"'fallback:'", "'desc:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "COMMENT", "EOL", "NAME", "MSGT", "UNIQUES", "BASE", "RACE_FLAG", 
			"FALLBACK", "DESC", "BOOLEAN", "FLAG", "TEXT"
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
	public String getGrammarFileName() { return "Summon.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SummonParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public Token FLAG;
		public TerminalNode NAME() { return getToken(SummonParser.NAME, 0); }
		public TerminalNode FLAG() { return getToken(SummonParser.FLAG, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SummonListener ) ((SummonListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SummonListener ) ((SummonListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SummonVisitor ) return ((SummonVisitor<? extends T>)visitor).visitName(this);
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
			((NameContext)_localctx).FLAG = match(FLAG);
			 ((NameContext)_localctx).nameStr =  ((NameContext)_localctx).FLAG.getText(); 
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
		public MessageType msgType;
		public Token FLAG;
		public TerminalNode MSGT() { return getToken(SummonParser.MSGT, 0); }
		public TerminalNode FLAG() { return getToken(SummonParser.FLAG, 0); }
		public MsgtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_msgt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SummonListener ) ((SummonListener)listener).enterMsgt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SummonListener ) ((SummonListener)listener).exitMsgt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SummonVisitor ) return ((SummonVisitor<? extends T>)visitor).visitMsgt(this);
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
			 ((MsgtContext)_localctx).msgType =  MessageType.valueOf("MSG_" + ((MsgtContext)_localctx).FLAG.getText().trim()); 
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
		public boolean uniquesBool;
		public Token BOOLEAN;
		public TerminalNode UNIQUES() { return getToken(SummonParser.UNIQUES, 0); }
		public TerminalNode BOOLEAN() { return getToken(SummonParser.BOOLEAN, 0); }
		public UniquesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_uniques; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SummonListener ) ((SummonListener)listener).enterUniques(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SummonListener ) ((SummonListener)listener).exitUniques(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SummonVisitor ) return ((SummonVisitor<? extends T>)visitor).visitUniques(this);
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
			((UniquesContext)_localctx).BOOLEAN = match(BOOLEAN);
			 ((UniquesContext)_localctx).uniquesBool =  ((UniquesContext)_localctx).BOOLEAN.getText().equals("1"); 
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
		public MonsterBase baseObj;
		public Token TEXT;
		public TerminalNode BASE() { return getToken(SummonParser.BASE, 0); }
		public TerminalNode TEXT() { return getToken(SummonParser.TEXT, 0); }
		public BaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_base; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SummonListener ) ((SummonListener)listener).enterBase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SummonListener ) ((SummonListener)listener).exitBase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SummonVisitor ) return ((SummonVisitor<? extends T>)visitor).visitBase(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BaseContext base() throws RecognitionException {
		BaseContext _localctx = new BaseContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_base);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(30);
			match(BASE);
			setState(31);
			((BaseContext)_localctx).TEXT = match(TEXT);
			 ((BaseContext)_localctx).baseObj =  GameConstants.getBaseFromName(((BaseContext)_localctx).TEXT.getText()); 
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
	public static class Race_flagContext extends ParserRuleContext {
		public MonsterRaceFlag raceFlg;
		public Token FLAG;
		public TerminalNode RACE_FLAG() { return getToken(SummonParser.RACE_FLAG, 0); }
		public TerminalNode FLAG() { return getToken(SummonParser.FLAG, 0); }
		public Race_flagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_race_flag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SummonListener ) ((SummonListener)listener).enterRace_flag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SummonListener ) ((SummonListener)listener).exitRace_flag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SummonVisitor ) return ((SummonVisitor<? extends T>)visitor).visitRace_flag(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Race_flagContext race_flag() throws RecognitionException {
		Race_flagContext _localctx = new Race_flagContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_race_flag);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(34);
			match(RACE_FLAG);
			setState(35);
			((Race_flagContext)_localctx).FLAG = match(FLAG);
			 ((Race_flagContext)_localctx).raceFlg =  MonsterRaceFlag.valueOf("RF_" + ((Race_flagContext)_localctx).FLAG.getText().trim()); 
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
		public Token FLAG;
		public TerminalNode FALLBACK() { return getToken(SummonParser.FALLBACK, 0); }
		public TerminalNode FLAG() { return getToken(SummonParser.FLAG, 0); }
		public FallbackContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fallback; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SummonListener ) ((SummonListener)listener).enterFallback(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SummonListener ) ((SummonListener)listener).exitFallback(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SummonVisitor ) return ((SummonVisitor<? extends T>)visitor).visitFallback(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FallbackContext fallback() throws RecognitionException {
		FallbackContext _localctx = new FallbackContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_fallback);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(38);
			match(FALLBACK);
			setState(39);
			((FallbackContext)_localctx).FLAG = match(FLAG);
			 ((FallbackContext)_localctx).fallbackStr =  ((FallbackContext)_localctx).FLAG.getText(); 
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
		public Token TEXT;
		public TerminalNode DESC() { return getToken(SummonParser.DESC, 0); }
		public TerminalNode TEXT() { return getToken(SummonParser.TEXT, 0); }
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_desc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SummonListener ) ((SummonListener)listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SummonListener ) ((SummonListener)listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SummonVisitor ) return ((SummonVisitor<? extends T>)visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_desc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(42);
			match(DESC);
			setState(43);
			((DescContext)_localctx).TEXT = match(TEXT);
			 ((DescContext)_localctx).descStr =  ((DescContext)_localctx).TEXT.getText(); 
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
	public static class SummonContext extends ParserRuleContext {
		public Summon summonObj;
		public NameContext name;
		public MsgtContext msgt;
		public UniquesContext uniques;
		public BaseContext base;
		public Race_flagContext race_flag;
		public FallbackContext fallback;
		public DescContext desc;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public List<MsgtContext> msgt() {
			return getRuleContexts(MsgtContext.class);
		}
		public MsgtContext msgt(int i) {
			return getRuleContext(MsgtContext.class,i);
		}
		public List<UniquesContext> uniques() {
			return getRuleContexts(UniquesContext.class);
		}
		public UniquesContext uniques(int i) {
			return getRuleContext(UniquesContext.class,i);
		}
		public List<BaseContext> base() {
			return getRuleContexts(BaseContext.class);
		}
		public BaseContext base(int i) {
			return getRuleContext(BaseContext.class,i);
		}
		public List<Race_flagContext> race_flag() {
			return getRuleContexts(Race_flagContext.class);
		}
		public Race_flagContext race_flag(int i) {
			return getRuleContext(Race_flagContext.class,i);
		}
		public List<FallbackContext> fallback() {
			return getRuleContexts(FallbackContext.class);
		}
		public FallbackContext fallback(int i) {
			return getRuleContext(FallbackContext.class,i);
		}
		public List<DescContext> desc() {
			return getRuleContexts(DescContext.class);
		}
		public DescContext desc(int i) {
			return getRuleContext(DescContext.class,i);
		}
		public SummonContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_summon; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SummonListener ) ((SummonListener)listener).enterSummon(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SummonListener ) ((SummonListener)listener).exitSummon(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SummonVisitor ) return ((SummonVisitor<? extends T>)visitor).visitSummon(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SummonContext summon() throws RecognitionException {
		SummonContext _localctx = new SummonContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_summon);

		            String nameInit = "";
		            MessageType msgTypeInit = MessageType.MSG_NONE;
		            boolean uniquesInit = false;
		            List<MonsterBase> basesInit = new ArrayList<>();
		            MonsterRaceFlag raceFlagInit = MonsterRaceFlag.RF_NONE;
		            String fallbackInit = "";
		            String descInit = "";
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			((SummonContext)_localctx).name = name();
			 nameInit = ((SummonContext)_localctx).name.nameStr; 
			setState(66); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(66);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case MSGT:
					{
					setState(48);
					((SummonContext)_localctx).msgt = msgt();
					 msgTypeInit = ((SummonContext)_localctx).msgt.msgType; 
					}
					break;
				case UNIQUES:
					{
					setState(51);
					((SummonContext)_localctx).uniques = uniques();
					 uniquesInit = ((SummonContext)_localctx).uniques.uniquesBool; 
					}
					break;
				case BASE:
					{
					setState(54);
					((SummonContext)_localctx).base = base();
					 basesInit.add(((SummonContext)_localctx).base.baseObj); 
					}
					break;
				case RACE_FLAG:
					{
					setState(57);
					((SummonContext)_localctx).race_flag = race_flag();
					 raceFlagInit = ((SummonContext)_localctx).race_flag.raceFlg; 
					}
					break;
				case FALLBACK:
					{
					setState(60);
					((SummonContext)_localctx).fallback = fallback();
					 fallbackInit = ((SummonContext)_localctx).fallback.fallbackStr; 
					}
					break;
				case DESC:
					{
					setState(63);
					((SummonContext)_localctx).desc = desc();
					 descInit = ((SummonContext)_localctx).desc.descStr; 
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(68); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 1008L) != 0) );
			}
			_ctx.stop = _input.LT(-1);

			            ((SummonContext)_localctx).summonObj =  new Summon(nameInit, msgTypeInit, uniquesInit, basesInit,
			                                    raceFlagInit, fallbackInit, descInit);
			        
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
		public List<Summon> summons;
		public SummonContext summon;
		public TerminalNode EOF() { return getToken(SummonParser.EOF, 0); }
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
			if ( listener instanceof SummonListener ) ((SummonListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SummonListener ) ((SummonListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SummonVisitor ) return ((SummonVisitor<? extends T>)visitor).visitFile(this);
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
			setState(73); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(70);
				((FileContext)_localctx).summon = summon();

				            _localctx.summons.add(((FileContext)_localctx).summon.summonObj);
				        
				}
				}
				setState(75); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(77);
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
		"\u0004\u0001\fP\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0004\u0007C\b\u0007\u000b\u0007\f\u0007D\u0001\b\u0001\b"+
		"\u0001\b\u0004\bJ\b\b\u000b\b\f\bK\u0001\b\u0001\b\u0001\b\u0000\u0000"+
		"\t\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0000\u0000M\u0000\u0012"+
		"\u0001\u0000\u0000\u0000\u0002\u0016\u0001\u0000\u0000\u0000\u0004\u001a"+
		"\u0001\u0000\u0000\u0000\u0006\u001e\u0001\u0000\u0000\u0000\b\"\u0001"+
		"\u0000\u0000\u0000\n&\u0001\u0000\u0000\u0000\f*\u0001\u0000\u0000\u0000"+
		"\u000e.\u0001\u0000\u0000\u0000\u0010I\u0001\u0000\u0000\u0000\u0012\u0013"+
		"\u0005\u0003\u0000\u0000\u0013\u0014\u0005\u000b\u0000\u0000\u0014\u0015"+
		"\u0006\u0000\uffff\uffff\u0000\u0015\u0001\u0001\u0000\u0000\u0000\u0016"+
		"\u0017\u0005\u0004\u0000\u0000\u0017\u0018\u0005\u000b\u0000\u0000\u0018"+
		"\u0019\u0006\u0001\uffff\uffff\u0000\u0019\u0003\u0001\u0000\u0000\u0000"+
		"\u001a\u001b\u0005\u0005\u0000\u0000\u001b\u001c\u0005\n\u0000\u0000\u001c"+
		"\u001d\u0006\u0002\uffff\uffff\u0000\u001d\u0005\u0001\u0000\u0000\u0000"+
		"\u001e\u001f\u0005\u0006\u0000\u0000\u001f \u0005\f\u0000\u0000 !\u0006"+
		"\u0003\uffff\uffff\u0000!\u0007\u0001\u0000\u0000\u0000\"#\u0005\u0007"+
		"\u0000\u0000#$\u0005\u000b\u0000\u0000$%\u0006\u0004\uffff\uffff\u0000"+
		"%\t\u0001\u0000\u0000\u0000&\'\u0005\b\u0000\u0000\'(\u0005\u000b\u0000"+
		"\u0000()\u0006\u0005\uffff\uffff\u0000)\u000b\u0001\u0000\u0000\u0000"+
		"*+\u0005\t\u0000\u0000+,\u0005\f\u0000\u0000,-\u0006\u0006\uffff\uffff"+
		"\u0000-\r\u0001\u0000\u0000\u0000./\u0003\u0000\u0000\u0000/B\u0006\u0007"+
		"\uffff\uffff\u000001\u0003\u0002\u0001\u000012\u0006\u0007\uffff\uffff"+
		"\u00002C\u0001\u0000\u0000\u000034\u0003\u0004\u0002\u000045\u0006\u0007"+
		"\uffff\uffff\u00005C\u0001\u0000\u0000\u000067\u0003\u0006\u0003\u0000"+
		"78\u0006\u0007\uffff\uffff\u00008C\u0001\u0000\u0000\u00009:\u0003\b\u0004"+
		"\u0000:;\u0006\u0007\uffff\uffff\u0000;C\u0001\u0000\u0000\u0000<=\u0003"+
		"\n\u0005\u0000=>\u0006\u0007\uffff\uffff\u0000>C\u0001\u0000\u0000\u0000"+
		"?@\u0003\f\u0006\u0000@A\u0006\u0007\uffff\uffff\u0000AC\u0001\u0000\u0000"+
		"\u0000B0\u0001\u0000\u0000\u0000B3\u0001\u0000\u0000\u0000B6\u0001\u0000"+
		"\u0000\u0000B9\u0001\u0000\u0000\u0000B<\u0001\u0000\u0000\u0000B?\u0001"+
		"\u0000\u0000\u0000CD\u0001\u0000\u0000\u0000DB\u0001\u0000\u0000\u0000"+
		"DE\u0001\u0000\u0000\u0000E\u000f\u0001\u0000\u0000\u0000FG\u0003\u000e"+
		"\u0007\u0000GH\u0006\b\uffff\uffff\u0000HJ\u0001\u0000\u0000\u0000IF\u0001"+
		"\u0000\u0000\u0000JK\u0001\u0000\u0000\u0000KI\u0001\u0000\u0000\u0000"+
		"KL\u0001\u0000\u0000\u0000LM\u0001\u0000\u0000\u0000MN\u0005\u0000\u0000"+
		"\u0001N\u0011\u0001\u0000\u0000\u0000\u0003BDK";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}