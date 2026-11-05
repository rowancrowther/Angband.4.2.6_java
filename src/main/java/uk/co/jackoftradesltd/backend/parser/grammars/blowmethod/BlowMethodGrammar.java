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
// Generated from /home/rowan/IdeaProjects/Angband.4.2.6_java/src/main/java/uk/co/jackoftradesltd/backend/parser/grammars/BlowMethodGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.blowmethod;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.backend.parser.blowmethod.BlowMethodParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class BlowMethodGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, CUT=2, STUN=3, MISS=4, PHYS=5, MSG=6, ACT=7, DESC=8, INTEGER=9, 
		COMMENT=10, EOL=11, STRING=12, FREE_TEXT_EOL=13;
	public static final int
		RULE_name = 0, RULE_cut = 1, RULE_stun = 2, RULE_miss = 3, RULE_phys = 4, 
		RULE_msg = 5, RULE_act = 6, RULE_desc = 7, RULE_blow = 8, RULE_file = 9;
	private static String[] makeRuleNames() {
		return new String[] {
			"name", "cut", "stun", "miss", "phys", "msg", "act", "desc", "blow", 
			"file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'cut:'", "'stun:'", "'miss:'", "'phys:'", "'msg:'", 
			"'act:'", "'desc:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "CUT", "STUN", "MISS", "PHYS", "MSG", "ACT", "DESC", "INTEGER", 
			"COMMENT", "EOL", "STRING", "FREE_TEXT_EOL"
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
	public String getGrammarFileName() { return "BlowMethodGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public BlowMethodGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public int line;
		public Token STRING;
		public TerminalNode NAME() { return getToken(BlowMethodGrammar.NAME, 0); }
		public TerminalNode STRING() { return getToken(BlowMethodGrammar.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowMethodGrammarListener ) ((BlowMethodGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowMethodGrammarListener ) ((BlowMethodGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowMethodGrammarVisitor ) return ((BlowMethodGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_name);
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
	public static class CutContext extends ParserRuleContext {
		public String cutVal;
		public Token INTEGER;
		public TerminalNode CUT() { return getToken(BlowMethodGrammar.CUT, 0); }
		public TerminalNode INTEGER() { return getToken(BlowMethodGrammar.INTEGER, 0); }
		public CutContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cut; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowMethodGrammarListener ) ((BlowMethodGrammarListener)listener).enterCut(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowMethodGrammarListener ) ((BlowMethodGrammarListener)listener).exitCut(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowMethodGrammarVisitor ) return ((BlowMethodGrammarVisitor<? extends T>)visitor).visitCut(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CutContext cut() throws RecognitionException {
		CutContext _localctx = new CutContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_cut);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(24);
			match(CUT);
			setState(25);
			((CutContext)_localctx).INTEGER = match(INTEGER);
			 ((CutContext)_localctx).cutVal =  ((CutContext)_localctx).INTEGER.getText(); 
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
	public static class StunContext extends ParserRuleContext {
		public String stunVal;
		public Token INTEGER;
		public TerminalNode STUN() { return getToken(BlowMethodGrammar.STUN, 0); }
		public TerminalNode INTEGER() { return getToken(BlowMethodGrammar.INTEGER, 0); }
		public StunContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stun; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowMethodGrammarListener ) ((BlowMethodGrammarListener)listener).enterStun(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowMethodGrammarListener ) ((BlowMethodGrammarListener)listener).exitStun(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowMethodGrammarVisitor ) return ((BlowMethodGrammarVisitor<? extends T>)visitor).visitStun(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StunContext stun() throws RecognitionException {
		StunContext _localctx = new StunContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_stun);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(28);
			match(STUN);
			setState(29);
			((StunContext)_localctx).INTEGER = match(INTEGER);
			 ((StunContext)_localctx).stunVal =  ((StunContext)_localctx).INTEGER.getText(); 
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
	public static class MissContext extends ParserRuleContext {
		public String missVal;
		public Token INTEGER;
		public TerminalNode MISS() { return getToken(BlowMethodGrammar.MISS, 0); }
		public TerminalNode INTEGER() { return getToken(BlowMethodGrammar.INTEGER, 0); }
		public MissContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_miss; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowMethodGrammarListener ) ((BlowMethodGrammarListener)listener).enterMiss(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowMethodGrammarListener ) ((BlowMethodGrammarListener)listener).exitMiss(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowMethodGrammarVisitor ) return ((BlowMethodGrammarVisitor<? extends T>)visitor).visitMiss(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MissContext miss() throws RecognitionException {
		MissContext _localctx = new MissContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_miss);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(32);
			match(MISS);
			setState(33);
			((MissContext)_localctx).INTEGER = match(INTEGER);
			 ((MissContext)_localctx).missVal =  ((MissContext)_localctx).INTEGER.getText(); 
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
	public static class PhysContext extends ParserRuleContext {
		public String physVal;
		public Token INTEGER;
		public TerminalNode PHYS() { return getToken(BlowMethodGrammar.PHYS, 0); }
		public TerminalNode INTEGER() { return getToken(BlowMethodGrammar.INTEGER, 0); }
		public PhysContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_phys; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowMethodGrammarListener ) ((BlowMethodGrammarListener)listener).enterPhys(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowMethodGrammarListener ) ((BlowMethodGrammarListener)listener).exitPhys(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowMethodGrammarVisitor ) return ((BlowMethodGrammarVisitor<? extends T>)visitor).visitPhys(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PhysContext phys() throws RecognitionException {
		PhysContext _localctx = new PhysContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_phys);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(36);
			match(PHYS);
			setState(37);
			((PhysContext)_localctx).INTEGER = match(INTEGER);
			 ((PhysContext)_localctx).physVal =  ((PhysContext)_localctx).INTEGER.getText(); 
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
	public static class MsgContext extends ParserRuleContext {
		public String msgStr;
		public Token STRING;
		public TerminalNode MSG() { return getToken(BlowMethodGrammar.MSG, 0); }
		public TerminalNode STRING() { return getToken(BlowMethodGrammar.STRING, 0); }
		public MsgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_msg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowMethodGrammarListener ) ((BlowMethodGrammarListener)listener).enterMsg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowMethodGrammarListener ) ((BlowMethodGrammarListener)listener).exitMsg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowMethodGrammarVisitor ) return ((BlowMethodGrammarVisitor<? extends T>)visitor).visitMsg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MsgContext msg() throws RecognitionException {
		MsgContext _localctx = new MsgContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_msg);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(40);
			match(MSG);
			setState(43);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STRING) {
				{
				setState(41);
				((MsgContext)_localctx).STRING = match(STRING);
				 ((MsgContext)_localctx).msgStr =  ((MsgContext)_localctx).STRING.getText(); 
				}
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
	public static class ActContext extends ParserRuleContext {
		public String actStr;
		public Token STRING;
		public TerminalNode ACT() { return getToken(BlowMethodGrammar.ACT, 0); }
		public TerminalNode STRING() { return getToken(BlowMethodGrammar.STRING, 0); }
		public ActContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_act; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowMethodGrammarListener ) ((BlowMethodGrammarListener)listener).enterAct(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowMethodGrammarListener ) ((BlowMethodGrammarListener)listener).exitAct(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowMethodGrammarVisitor ) return ((BlowMethodGrammarVisitor<? extends T>)visitor).visitAct(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActContext act() throws RecognitionException {
		ActContext _localctx = new ActContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_act);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(45);
			match(ACT);
			setState(46);
			((ActContext)_localctx).STRING = match(STRING);
			 ((ActContext)_localctx).actStr =  ((ActContext)_localctx).STRING.getText(); 
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
		public TerminalNode DESC() { return getToken(BlowMethodGrammar.DESC, 0); }
		public TerminalNode STRING() { return getToken(BlowMethodGrammar.STRING, 0); }
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_desc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowMethodGrammarListener ) ((BlowMethodGrammarListener)listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowMethodGrammarListener ) ((BlowMethodGrammarListener)listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowMethodGrammarVisitor ) return ((BlowMethodGrammarVisitor<? extends T>)visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_desc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(49);
			match(DESC);
			setState(50);
			((DescContext)_localctx).STRING = match(STRING);
			 ((DescContext)_localctx).descStr =  ((DescContext)_localctx).STRING.getText(); 
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
	public static class BlowContext extends ParserRuleContext {
		public BlowMethodParseRecord record;
		public NameContext name;
		public CutContext cut;
		public StunContext stun;
		public MissContext miss;
		public PhysContext phys;
		public MsgContext msg;
		public ActContext act;
		public DescContext desc;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public CutContext cut() {
			return getRuleContext(CutContext.class,0);
		}
		public StunContext stun() {
			return getRuleContext(StunContext.class,0);
		}
		public MissContext miss() {
			return getRuleContext(MissContext.class,0);
		}
		public PhysContext phys() {
			return getRuleContext(PhysContext.class,0);
		}
		public DescContext desc() {
			return getRuleContext(DescContext.class,0);
		}
		public MsgContext msg() {
			return getRuleContext(MsgContext.class,0);
		}
		public List<ActContext> act() {
			return getRuleContexts(ActContext.class);
		}
		public ActContext act(int i) {
			return getRuleContext(ActContext.class,i);
		}
		public BlowContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_blow; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowMethodGrammarListener ) ((BlowMethodGrammarListener)listener).enterBlow(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowMethodGrammarListener ) ((BlowMethodGrammarListener)listener).exitBlow(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowMethodGrammarVisitor ) return ((BlowMethodGrammarVisitor<? extends T>)visitor).visitBlow(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlowContext blow() throws RecognitionException {
		BlowContext _localctx = new BlowContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_blow);

		            String nameInit = "";
		            String cutInit = "";
		            String stunInit = "";
		            String missInit = "";
		            String physInit = "";
		            String msgInit = "";
		            List<String> actInit = new ArrayList<>();
		            String descInit = "";
		            int line = 0;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(53);
			((BlowContext)_localctx).name = name();
			 line     = ((BlowContext)_localctx).name.line; nameInit = ((BlowContext)_localctx).name.nameStr.trim(); 
			setState(55);
			((BlowContext)_localctx).cut = cut();
			 cutInit  = ((BlowContext)_localctx).cut.cutVal; 
			setState(57);
			((BlowContext)_localctx).stun = stun();
			 stunInit = ((BlowContext)_localctx).stun.stunVal; 
			setState(59);
			((BlowContext)_localctx).miss = miss();
			 missInit = ((BlowContext)_localctx).miss.missVal; 
			setState(61);
			((BlowContext)_localctx).phys = phys();
			 physInit = ((BlowContext)_localctx).phys.physVal; 
			setState(66);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==MSG) {
				{
				setState(63);
				((BlowContext)_localctx).msg = msg();
				 msgInit  = ((BlowContext)_localctx).msg.msgStr; 
				}
			}

			setState(73);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ACT) {
				{
				{
				setState(68);
				((BlowContext)_localctx).act = act();
				 actInit.add(((BlowContext)_localctx).act.actStr); 
				}
				}
				setState(75);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(76);
			((BlowContext)_localctx).desc = desc();
			 descInit = ((BlowContext)_localctx).desc.descStr; 
			}
			_ctx.stop = _input.LT(-1);

			            ((BlowContext)_localctx).record =  new BlowMethodParseRecord(nameInit,
			                cutInit, stunInit, missInit, physInit,
			                msgInit, actInit, descInit, line);
			        
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
		public List<BlowMethodParseRecord> records;
		public BlowContext blow;
		public TerminalNode EOF() { return getToken(BlowMethodGrammar.EOF, 0); }
		public List<BlowContext> blow() {
			return getRuleContexts(BlowContext.class);
		}
		public BlowContext blow(int i) {
			return getRuleContext(BlowContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowMethodGrammarListener ) ((BlowMethodGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowMethodGrammarListener ) ((BlowMethodGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowMethodGrammarVisitor ) return ((BlowMethodGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_file);

		            ((FileContext)_localctx).records =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(79);
				((FileContext)_localctx).blow = blow();
				 _localctx.records.add(((FileContext)_localctx).blow.record); 
				}
				}
				setState(84); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(86);
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
		"\u0004\u0001\rY\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0003\u0005,\b\u0005\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0003\bC\b\b\u0001\b\u0001\b\u0001\b\u0005\bH\b\b\n"+
		"\b\f\bK\t\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0004\tS\b"+
		"\t\u000b\t\f\tT\u0001\t\u0001\t\u0001\t\u0000\u0000\n\u0000\u0002\u0004"+
		"\u0006\b\n\f\u000e\u0010\u0012\u0000\u0000R\u0000\u0014\u0001\u0000\u0000"+
		"\u0000\u0002\u0018\u0001\u0000\u0000\u0000\u0004\u001c\u0001\u0000\u0000"+
		"\u0000\u0006 \u0001\u0000\u0000\u0000\b$\u0001\u0000\u0000\u0000\n(\u0001"+
		"\u0000\u0000\u0000\f-\u0001\u0000\u0000\u0000\u000e1\u0001\u0000\u0000"+
		"\u0000\u00105\u0001\u0000\u0000\u0000\u0012R\u0001\u0000\u0000\u0000\u0014"+
		"\u0015\u0005\u0001\u0000\u0000\u0015\u0016\u0005\f\u0000\u0000\u0016\u0017"+
		"\u0006\u0000\uffff\uffff\u0000\u0017\u0001\u0001\u0000\u0000\u0000\u0018"+
		"\u0019\u0005\u0002\u0000\u0000\u0019\u001a\u0005\t\u0000\u0000\u001a\u001b"+
		"\u0006\u0001\uffff\uffff\u0000\u001b\u0003\u0001\u0000\u0000\u0000\u001c"+
		"\u001d\u0005\u0003\u0000\u0000\u001d\u001e\u0005\t\u0000\u0000\u001e\u001f"+
		"\u0006\u0002\uffff\uffff\u0000\u001f\u0005\u0001\u0000\u0000\u0000 !\u0005"+
		"\u0004\u0000\u0000!\"\u0005\t\u0000\u0000\"#\u0006\u0003\uffff\uffff\u0000"+
		"#\u0007\u0001\u0000\u0000\u0000$%\u0005\u0005\u0000\u0000%&\u0005\t\u0000"+
		"\u0000&\'\u0006\u0004\uffff\uffff\u0000\'\t\u0001\u0000\u0000\u0000(+"+
		"\u0005\u0006\u0000\u0000)*\u0005\f\u0000\u0000*,\u0006\u0005\uffff\uffff"+
		"\u0000+)\u0001\u0000\u0000\u0000+,\u0001\u0000\u0000\u0000,\u000b\u0001"+
		"\u0000\u0000\u0000-.\u0005\u0007\u0000\u0000./\u0005\f\u0000\u0000/0\u0006"+
		"\u0006\uffff\uffff\u00000\r\u0001\u0000\u0000\u000012\u0005\b\u0000\u0000"+
		"23\u0005\f\u0000\u000034\u0006\u0007\uffff\uffff\u00004\u000f\u0001\u0000"+
		"\u0000\u000056\u0003\u0000\u0000\u000067\u0006\b\uffff\uffff\u000078\u0003"+
		"\u0002\u0001\u000089\u0006\b\uffff\uffff\u00009:\u0003\u0004\u0002\u0000"+
		":;\u0006\b\uffff\uffff\u0000;<\u0003\u0006\u0003\u0000<=\u0006\b\uffff"+
		"\uffff\u0000=>\u0003\b\u0004\u0000>B\u0006\b\uffff\uffff\u0000?@\u0003"+
		"\n\u0005\u0000@A\u0006\b\uffff\uffff\u0000AC\u0001\u0000\u0000\u0000B"+
		"?\u0001\u0000\u0000\u0000BC\u0001\u0000\u0000\u0000CI\u0001\u0000\u0000"+
		"\u0000DE\u0003\f\u0006\u0000EF\u0006\b\uffff\uffff\u0000FH\u0001\u0000"+
		"\u0000\u0000GD\u0001\u0000\u0000\u0000HK\u0001\u0000\u0000\u0000IG\u0001"+
		"\u0000\u0000\u0000IJ\u0001\u0000\u0000\u0000JL\u0001\u0000\u0000\u0000"+
		"KI\u0001\u0000\u0000\u0000LM\u0003\u000e\u0007\u0000MN\u0006\b\uffff\uffff"+
		"\u0000N\u0011\u0001\u0000\u0000\u0000OP\u0003\u0010\b\u0000PQ\u0006\t"+
		"\uffff\uffff\u0000QS\u0001\u0000\u0000\u0000RO\u0001\u0000\u0000\u0000"+
		"ST\u0001\u0000\u0000\u0000TR\u0001\u0000\u0000\u0000TU\u0001\u0000\u0000"+
		"\u0000UV\u0001\u0000\u0000\u0000VW\u0005\u0000\u0000\u0001W\u0013\u0001"+
		"\u0000\u0000\u0000\u0004+BIT";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}