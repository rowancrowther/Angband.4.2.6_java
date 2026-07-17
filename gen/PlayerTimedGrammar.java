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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/pt_lexcheck/PlayerTimedGrammar.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.backend.parser.playertimed.PlayerTimedParseRecord;
    import uk.co.jackoftrades.backend.parser.playertimed.PlayerTimedParseRecord.PlayerTimedGradeParseRecord;

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
public class PlayerTimedGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		RECORD_COUNT=1, NAME=2, DESC=3, GRADE=4, ON_END=5, ON_INCREASE=6, ON_DECREASE=7, 
		MSGT=8, FAIL=9, ON_BEGIN_EFFECT=10, ON_END_EFFECT=11, EFFECT_DICE=12, 
		RESIST=13, BRAND=14, SLAY=15, FLAG_SYNONYM=16, LOWER_BOUND=17, FLAGS=18, 
		INTEGER=19, COMMENT=20, EOL=21, FLAG=22, FLAG_OR=23, FLAG_EOL=24, STRING=25, 
		PT_FREE_TEXT_MODE_EOL=26, DELIM_INTEGER=27, DELIM_COLOUR_CODE=28, DELIM_BETWEEN_COLONS=29, 
		DELIM_COLON=30, DELIM_EOL=31;
	public static final int
		RULE_recordCount = 0, RULE_name = 1, RULE_desc = 2, RULE_grade = 3, RULE_onEnd = 4, 
		RULE_onIncrease = 5, RULE_onDecrease = 6, RULE_msgt = 7, RULE_fail = 8, 
		RULE_onBeginEffect = 9, RULE_onEndEffect = 10, RULE_effectDice = 11, RULE_resist = 12, 
		RULE_brand = 13, RULE_slay = 14, RULE_flagSynonym = 15, RULE_lowerBound = 16, 
		RULE_flags = 17, RULE_playerTimed = 18, RULE_file = 19;
	private static String[] makeRuleNames() {
		return new String[] {
			"recordCount", "name", "desc", "grade", "onEnd", "onIncrease", "onDecrease", 
			"msgt", "fail", "onBeginEffect", "onEndEffect", "effectDice", "resist", 
			"brand", "slay", "flagSynonym", "lowerBound", "flags", "playerTimed", 
			"file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'record-count:'", "'name:'", "'desc:'", "'grade:'", "'on-end:'", 
			"'on-increase:'", "'on-decrease:'", "'msgt:'", "'fail:'", "'on-begin-effect:'", 
			"'on-end-effect:'", "'effect-dice:'", "'resist:'", "'brand:'", "'slay:'", 
			"'flag-synonym:'", "'lower-bound:'", "'flags:'", null, null, null, null, 
			null, null, null, null, null, null, null, "':'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "RECORD_COUNT", "NAME", "DESC", "GRADE", "ON_END", "ON_INCREASE", 
			"ON_DECREASE", "MSGT", "FAIL", "ON_BEGIN_EFFECT", "ON_END_EFFECT", "EFFECT_DICE", 
			"RESIST", "BRAND", "SLAY", "FLAG_SYNONYM", "LOWER_BOUND", "FLAGS", "INTEGER", 
			"COMMENT", "EOL", "FLAG", "FLAG_OR", "FLAG_EOL", "STRING", "PT_FREE_TEXT_MODE_EOL", 
			"DELIM_INTEGER", "DELIM_COLOUR_CODE", "DELIM_BETWEEN_COLONS", "DELIM_COLON", 
			"DELIM_EOL"
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
	public String getGrammarFileName() { return "PlayerTimedGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public PlayerTimedGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RecordCountContext extends ParserRuleContext {
		public String count;
		public Token INTEGER;
		public TerminalNode RECORD_COUNT() { return getToken(PlayerTimedGrammar.RECORD_COUNT, 0); }
		public TerminalNode INTEGER() { return getToken(PlayerTimedGrammar.INTEGER, 0); }
		public RecordCountContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_recordCount; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).enterRecordCount(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).exitRecordCount(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerTimedGrammarVisitor ) return ((PlayerTimedGrammarVisitor<? extends T>)visitor).visitRecordCount(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RecordCountContext recordCount() throws RecognitionException {
		RecordCountContext _localctx = new RecordCountContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_recordCount);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(40);
			match(RECORD_COUNT);
			setState(41);
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
		public int lineNo;
		public Token FLAG;
		public TerminalNode NAME() { return getToken(PlayerTimedGrammar.NAME, 0); }
		public TerminalNode FLAG() { return getToken(PlayerTimedGrammar.FLAG, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerTimedGrammarVisitor ) return ((PlayerTimedGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			match(NAME);
			setState(45);
			((NameContext)_localctx).FLAG = match(FLAG);
			 ((NameContext)_localctx).nameStr =  ((NameContext)_localctx).FLAG.getText(); ((NameContext)_localctx).lineNo =  _localctx.start.getLine(); 
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
		public TerminalNode DESC() { return getToken(PlayerTimedGrammar.DESC, 0); }
		public TerminalNode STRING() { return getToken(PlayerTimedGrammar.STRING, 0); }
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_desc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerTimedGrammarVisitor ) return ((PlayerTimedGrammarVisitor<? extends T>)visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_desc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			match(DESC);
			setState(49);
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
	public static class GradeContext extends ParserRuleContext {
		public String colour;
		public String max;
		public String status;
		public String messageUp;
		public String messageDown;
		public Token c;
		public Token m;
		public Token s;
		public Token u;
		public Token d;
		public TerminalNode GRADE() { return getToken(PlayerTimedGrammar.GRADE, 0); }
		public List<TerminalNode> DELIM_COLON() { return getTokens(PlayerTimedGrammar.DELIM_COLON); }
		public TerminalNode DELIM_COLON(int i) {
			return getToken(PlayerTimedGrammar.DELIM_COLON, i);
		}
		public TerminalNode DELIM_COLOUR_CODE() { return getToken(PlayerTimedGrammar.DELIM_COLOUR_CODE, 0); }
		public TerminalNode DELIM_INTEGER() { return getToken(PlayerTimedGrammar.DELIM_INTEGER, 0); }
		public List<TerminalNode> DELIM_BETWEEN_COLONS() { return getTokens(PlayerTimedGrammar.DELIM_BETWEEN_COLONS); }
		public TerminalNode DELIM_BETWEEN_COLONS(int i) {
			return getToken(PlayerTimedGrammar.DELIM_BETWEEN_COLONS, i);
		}
		public GradeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_grade; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).enterGrade(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).exitGrade(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerTimedGrammarVisitor ) return ((PlayerTimedGrammarVisitor<? extends T>)visitor).visitGrade(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GradeContext grade() throws RecognitionException {
		GradeContext _localctx = new GradeContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_grade);

		            ((GradeContext)_localctx).messageDown =  "";
		            ((GradeContext)_localctx).messageUp =  "";
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			match(GRADE);
			setState(53);
			((GradeContext)_localctx).c = match(DELIM_COLOUR_CODE);
			setState(54);
			match(DELIM_COLON);
			setState(55);
			((GradeContext)_localctx).m = match(DELIM_INTEGER);
			setState(56);
			match(DELIM_COLON);
			setState(57);
			((GradeContext)_localctx).s = match(DELIM_BETWEEN_COLONS);
			setState(58);
			match(DELIM_COLON);
			setState(59);
			((GradeContext)_localctx).u = match(DELIM_BETWEEN_COLONS);

			                  ((GradeContext)_localctx).colour =  ((GradeContext)_localctx).c.getText();
			                  ((GradeContext)_localctx).max =  ((GradeContext)_localctx).m.getText();
			                  ((GradeContext)_localctx).status =  ((GradeContext)_localctx).s.getText();
			                  ((GradeContext)_localctx).messageUp =  ((GradeContext)_localctx).u.getText(); 
			setState(66);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DELIM_COLON) {
				{
				setState(61);
				match(DELIM_COLON);
				setState(64);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==DELIM_BETWEEN_COLONS) {
					{
					setState(62);
					((GradeContext)_localctx).d = match(DELIM_BETWEEN_COLONS);

					                  ((GradeContext)_localctx).messageDown =  ((GradeContext)_localctx).d.getText();
					                  
					}
				}

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
	public static class OnEndContext extends ParserRuleContext {
		public String message;
		public Token STRING;
		public TerminalNode ON_END() { return getToken(PlayerTimedGrammar.ON_END, 0); }
		public TerminalNode STRING() { return getToken(PlayerTimedGrammar.STRING, 0); }
		public OnEndContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_onEnd; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).enterOnEnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).exitOnEnd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerTimedGrammarVisitor ) return ((PlayerTimedGrammarVisitor<? extends T>)visitor).visitOnEnd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OnEndContext onEnd() throws RecognitionException {
		OnEndContext _localctx = new OnEndContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_onEnd);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(68);
			match(ON_END);
			setState(69);
			((OnEndContext)_localctx).STRING = match(STRING);
			 ((OnEndContext)_localctx).message =  ((OnEndContext)_localctx).STRING.getText(); 
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
	public static class OnIncreaseContext extends ParserRuleContext {
		public String message;
		public Token STRING;
		public TerminalNode ON_INCREASE() { return getToken(PlayerTimedGrammar.ON_INCREASE, 0); }
		public TerminalNode STRING() { return getToken(PlayerTimedGrammar.STRING, 0); }
		public OnIncreaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_onIncrease; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).enterOnIncrease(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).exitOnIncrease(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerTimedGrammarVisitor ) return ((PlayerTimedGrammarVisitor<? extends T>)visitor).visitOnIncrease(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OnIncreaseContext onIncrease() throws RecognitionException {
		OnIncreaseContext _localctx = new OnIncreaseContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_onIncrease);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			match(ON_INCREASE);
			setState(73);
			((OnIncreaseContext)_localctx).STRING = match(STRING);
			 ((OnIncreaseContext)_localctx).message =  ((OnIncreaseContext)_localctx).STRING.getText(); 
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
	public static class OnDecreaseContext extends ParserRuleContext {
		public String message;
		public Token STRING;
		public TerminalNode ON_DECREASE() { return getToken(PlayerTimedGrammar.ON_DECREASE, 0); }
		public TerminalNode STRING() { return getToken(PlayerTimedGrammar.STRING, 0); }
		public OnDecreaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_onDecrease; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).enterOnDecrease(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).exitOnDecrease(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerTimedGrammarVisitor ) return ((PlayerTimedGrammarVisitor<? extends T>)visitor).visitOnDecrease(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OnDecreaseContext onDecrease() throws RecognitionException {
		OnDecreaseContext _localctx = new OnDecreaseContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_onDecrease);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(76);
			match(ON_DECREASE);
			setState(77);
			((OnDecreaseContext)_localctx).STRING = match(STRING);
			 ((OnDecreaseContext)_localctx).message =  ((OnDecreaseContext)_localctx).STRING.getText(); 
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
		public String msgType;
		public Token FLAG;
		public TerminalNode MSGT() { return getToken(PlayerTimedGrammar.MSGT, 0); }
		public TerminalNode FLAG() { return getToken(PlayerTimedGrammar.FLAG, 0); }
		public MsgtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_msgt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).enterMsgt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).exitMsgt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerTimedGrammarVisitor ) return ((PlayerTimedGrammarVisitor<? extends T>)visitor).visitMsgt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MsgtContext msgt() throws RecognitionException {
		MsgtContext _localctx = new MsgtContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_msgt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
			match(MSGT);
			setState(81);
			((MsgtContext)_localctx).FLAG = match(FLAG);
			 ((MsgtContext)_localctx).msgType =  ((MsgtContext)_localctx).FLAG.getText(); 
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
	public static class FailContext extends ParserRuleContext {
		public String type;
		public String value;
		public Token t;
		public Token v;
		public TerminalNode FAIL() { return getToken(PlayerTimedGrammar.FAIL, 0); }
		public TerminalNode DELIM_COLON() { return getToken(PlayerTimedGrammar.DELIM_COLON, 0); }
		public TerminalNode DELIM_INTEGER() { return getToken(PlayerTimedGrammar.DELIM_INTEGER, 0); }
		public TerminalNode DELIM_BETWEEN_COLONS() { return getToken(PlayerTimedGrammar.DELIM_BETWEEN_COLONS, 0); }
		public FailContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fail; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).enterFail(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).exitFail(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerTimedGrammarVisitor ) return ((PlayerTimedGrammarVisitor<? extends T>)visitor).visitFail(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FailContext fail() throws RecognitionException {
		FailContext _localctx = new FailContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_fail);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(84);
			match(FAIL);
			setState(85);
			((FailContext)_localctx).t = match(DELIM_INTEGER);
			setState(86);
			match(DELIM_COLON);
			setState(87);
			((FailContext)_localctx).v = match(DELIM_BETWEEN_COLONS);

			                ((FailContext)_localctx).type =  ((FailContext)_localctx).t.getText();
			                ((FailContext)_localctx).value =  ((FailContext)_localctx).v.getText();
			            
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
	public static class OnBeginEffectContext extends ParserRuleContext {
		public String type;
		public String subType;
		public Token t;
		public Token s;
		public TerminalNode ON_BEGIN_EFFECT() { return getToken(PlayerTimedGrammar.ON_BEGIN_EFFECT, 0); }
		public List<TerminalNode> DELIM_BETWEEN_COLONS() { return getTokens(PlayerTimedGrammar.DELIM_BETWEEN_COLONS); }
		public TerminalNode DELIM_BETWEEN_COLONS(int i) {
			return getToken(PlayerTimedGrammar.DELIM_BETWEEN_COLONS, i);
		}
		public TerminalNode DELIM_COLON() { return getToken(PlayerTimedGrammar.DELIM_COLON, 0); }
		public OnBeginEffectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_onBeginEffect; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).enterOnBeginEffect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).exitOnBeginEffect(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerTimedGrammarVisitor ) return ((PlayerTimedGrammarVisitor<? extends T>)visitor).visitOnBeginEffect(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OnBeginEffectContext onBeginEffect() throws RecognitionException {
		OnBeginEffectContext _localctx = new OnBeginEffectContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_onBeginEffect);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(90);
			match(ON_BEGIN_EFFECT);
			setState(91);
			((OnBeginEffectContext)_localctx).t = match(DELIM_BETWEEN_COLONS);
			 ((OnBeginEffectContext)_localctx).type =  ((OnBeginEffectContext)_localctx).t.getText(); ((OnBeginEffectContext)_localctx).subType =  ""; 
			setState(96);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DELIM_COLON) {
				{
				setState(93);
				match(DELIM_COLON);
				setState(94);
				((OnBeginEffectContext)_localctx).s = match(DELIM_BETWEEN_COLONS);
				 ((OnBeginEffectContext)_localctx).subType =  ((OnBeginEffectContext)_localctx).s.getText(); 
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
	public static class OnEndEffectContext extends ParserRuleContext {
		public String type;
		public String subType;
		public Token t;
		public Token s;
		public TerminalNode ON_END_EFFECT() { return getToken(PlayerTimedGrammar.ON_END_EFFECT, 0); }
		public List<TerminalNode> DELIM_BETWEEN_COLONS() { return getTokens(PlayerTimedGrammar.DELIM_BETWEEN_COLONS); }
		public TerminalNode DELIM_BETWEEN_COLONS(int i) {
			return getToken(PlayerTimedGrammar.DELIM_BETWEEN_COLONS, i);
		}
		public TerminalNode DELIM_COLON() { return getToken(PlayerTimedGrammar.DELIM_COLON, 0); }
		public OnEndEffectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_onEndEffect; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).enterOnEndEffect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).exitOnEndEffect(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerTimedGrammarVisitor ) return ((PlayerTimedGrammarVisitor<? extends T>)visitor).visitOnEndEffect(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OnEndEffectContext onEndEffect() throws RecognitionException {
		OnEndEffectContext _localctx = new OnEndEffectContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_onEndEffect);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(98);
			match(ON_END_EFFECT);
			setState(99);
			((OnEndEffectContext)_localctx).t = match(DELIM_BETWEEN_COLONS);
			 ((OnEndEffectContext)_localctx).type =  ((OnEndEffectContext)_localctx).t.getText(); ((OnEndEffectContext)_localctx).subType =  ""; 
			setState(104);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DELIM_COLON) {
				{
				setState(101);
				match(DELIM_COLON);
				setState(102);
				((OnEndEffectContext)_localctx).s = match(DELIM_BETWEEN_COLONS);
				 ((OnEndEffectContext)_localctx).subType =  ((OnEndEffectContext)_localctx).s.getText(); 
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
	public static class EffectDiceContext extends ParserRuleContext {
		public String dice;
		public Token INTEGER;
		public TerminalNode EFFECT_DICE() { return getToken(PlayerTimedGrammar.EFFECT_DICE, 0); }
		public TerminalNode INTEGER() { return getToken(PlayerTimedGrammar.INTEGER, 0); }
		public EffectDiceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effectDice; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).enterEffectDice(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).exitEffectDice(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerTimedGrammarVisitor ) return ((PlayerTimedGrammarVisitor<? extends T>)visitor).visitEffectDice(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectDiceContext effectDice() throws RecognitionException {
		EffectDiceContext _localctx = new EffectDiceContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_effectDice);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(106);
			match(EFFECT_DICE);
			setState(107);
			((EffectDiceContext)_localctx).INTEGER = match(INTEGER);
			 ((EffectDiceContext)_localctx).dice =  ((EffectDiceContext)_localctx).INTEGER.getText(); 
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
	public static class ResistContext extends ParserRuleContext {
		public String res;
		public Token FLAG;
		public TerminalNode RESIST() { return getToken(PlayerTimedGrammar.RESIST, 0); }
		public TerminalNode FLAG() { return getToken(PlayerTimedGrammar.FLAG, 0); }
		public ResistContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_resist; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).enterResist(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).exitResist(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerTimedGrammarVisitor ) return ((PlayerTimedGrammarVisitor<? extends T>)visitor).visitResist(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ResistContext resist() throws RecognitionException {
		ResistContext _localctx = new ResistContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_resist);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110);
			match(RESIST);
			setState(111);
			((ResistContext)_localctx).FLAG = match(FLAG);
			 ((ResistContext)_localctx).res =  ((ResistContext)_localctx).FLAG.getText(); 
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
	public static class BrandContext extends ParserRuleContext {
		public String b;
		public Token FLAG;
		public TerminalNode BRAND() { return getToken(PlayerTimedGrammar.BRAND, 0); }
		public TerminalNode FLAG() { return getToken(PlayerTimedGrammar.FLAG, 0); }
		public BrandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_brand; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).enterBrand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).exitBrand(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerTimedGrammarVisitor ) return ((PlayerTimedGrammarVisitor<? extends T>)visitor).visitBrand(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BrandContext brand() throws RecognitionException {
		BrandContext _localctx = new BrandContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_brand);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(114);
			match(BRAND);
			setState(115);
			((BrandContext)_localctx).FLAG = match(FLAG);
			 ((BrandContext)_localctx).b =  ((BrandContext)_localctx).FLAG.getText(); 
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
	public static class SlayContext extends ParserRuleContext {
		public String s;
		public Token FLAG;
		public TerminalNode SLAY() { return getToken(PlayerTimedGrammar.SLAY, 0); }
		public TerminalNode FLAG() { return getToken(PlayerTimedGrammar.FLAG, 0); }
		public SlayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_slay; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).enterSlay(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).exitSlay(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerTimedGrammarVisitor ) return ((PlayerTimedGrammarVisitor<? extends T>)visitor).visitSlay(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SlayContext slay() throws RecognitionException {
		SlayContext _localctx = new SlayContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_slay);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(118);
			match(SLAY);
			setState(119);
			((SlayContext)_localctx).FLAG = match(FLAG);
			 ((SlayContext)_localctx).s =  ((SlayContext)_localctx).FLAG.getText(); 
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
	public static class FlagSynonymContext extends ParserRuleContext {
		public String objProp;
		public String val;
		public Token op;
		public Token v;
		public TerminalNode FLAG_SYNONYM() { return getToken(PlayerTimedGrammar.FLAG_SYNONYM, 0); }
		public TerminalNode DELIM_COLON() { return getToken(PlayerTimedGrammar.DELIM_COLON, 0); }
		public TerminalNode DELIM_BETWEEN_COLONS() { return getToken(PlayerTimedGrammar.DELIM_BETWEEN_COLONS, 0); }
		public TerminalNode DELIM_INTEGER() { return getToken(PlayerTimedGrammar.DELIM_INTEGER, 0); }
		public FlagSynonymContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flagSynonym; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).enterFlagSynonym(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).exitFlagSynonym(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerTimedGrammarVisitor ) return ((PlayerTimedGrammarVisitor<? extends T>)visitor).visitFlagSynonym(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagSynonymContext flagSynonym() throws RecognitionException {
		FlagSynonymContext _localctx = new FlagSynonymContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_flagSynonym);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(122);
			match(FLAG_SYNONYM);
			setState(123);
			((FlagSynonymContext)_localctx).op = match(DELIM_BETWEEN_COLONS);
			setState(124);
			match(DELIM_COLON);
			setState(125);
			((FlagSynonymContext)_localctx).v = match(DELIM_INTEGER);
			 ((FlagSynonymContext)_localctx).objProp =  ((FlagSynonymContext)_localctx).op.getText(); ((FlagSynonymContext)_localctx).val =  ((FlagSynonymContext)_localctx).v.getText(); 
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
	public static class LowerBoundContext extends ParserRuleContext {
		public String bound;
		public Token INTEGER;
		public TerminalNode LOWER_BOUND() { return getToken(PlayerTimedGrammar.LOWER_BOUND, 0); }
		public TerminalNode INTEGER() { return getToken(PlayerTimedGrammar.INTEGER, 0); }
		public LowerBoundContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lowerBound; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).enterLowerBound(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).exitLowerBound(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerTimedGrammarVisitor ) return ((PlayerTimedGrammarVisitor<? extends T>)visitor).visitLowerBound(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LowerBoundContext lowerBound() throws RecognitionException {
		LowerBoundContext _localctx = new LowerBoundContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_lowerBound);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(128);
			match(LOWER_BOUND);
			setState(129);
			((LowerBoundContext)_localctx).INTEGER = match(INTEGER);
			 ((LowerBoundContext)_localctx).bound =  ((LowerBoundContext)_localctx).INTEGER.getText(); 
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
		public TerminalNode FLAGS() { return getToken(PlayerTimedGrammar.FLAGS, 0); }
		public List<TerminalNode> FLAG() { return getTokens(PlayerTimedGrammar.FLAG); }
		public TerminalNode FLAG(int i) {
			return getToken(PlayerTimedGrammar.FLAG, i);
		}
		public List<TerminalNode> FLAG_OR() { return getTokens(PlayerTimedGrammar.FLAG_OR); }
		public TerminalNode FLAG_OR(int i) {
			return getToken(PlayerTimedGrammar.FLAG_OR, i);
		}
		public FlagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flags; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).enterFlags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).exitFlags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerTimedGrammarVisitor ) return ((PlayerTimedGrammarVisitor<? extends T>)visitor).visitFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsContext flags() throws RecognitionException {
		FlagsContext _localctx = new FlagsContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_flags);

		            ((FlagsContext)_localctx).flagList =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
			match(FLAGS);
			setState(133);
			((FlagsContext)_localctx).f1 = match(FLAG);
			 _localctx.flagList.add(((FlagsContext)_localctx).f1.getText()); 
			setState(140);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FLAG_OR) {
				{
				{
				setState(135);
				match(FLAG_OR);
				setState(136);
				((FlagsContext)_localctx).f2 = match(FLAG);
				 _localctx.flagList.add(((FlagsContext)_localctx).f2.getText()); 
				}
				}
				setState(142);
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
	public static class PlayerTimedContext extends ParserRuleContext {
		public PlayerTimedParseRecord timed;
		public NameContext name;
		public DescContext desc;
		public GradeContext grade;
		public OnEndContext onEnd;
		public OnIncreaseContext onIncrease;
		public OnDecreaseContext onDecrease;
		public MsgtContext msgt;
		public FailContext fail;
		public OnBeginEffectContext onBeginEffect;
		public OnEndEffectContext onEndEffect;
		public EffectDiceContext effectDice;
		public ResistContext resist;
		public BrandContext brand;
		public SlayContext slay;
		public FlagSynonymContext flagSynonym;
		public LowerBoundContext lowerBound;
		public FlagsContext flags;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public List<DescContext> desc() {
			return getRuleContexts(DescContext.class);
		}
		public DescContext desc(int i) {
			return getRuleContext(DescContext.class,i);
		}
		public List<GradeContext> grade() {
			return getRuleContexts(GradeContext.class);
		}
		public GradeContext grade(int i) {
			return getRuleContext(GradeContext.class,i);
		}
		public List<OnEndContext> onEnd() {
			return getRuleContexts(OnEndContext.class);
		}
		public OnEndContext onEnd(int i) {
			return getRuleContext(OnEndContext.class,i);
		}
		public List<OnIncreaseContext> onIncrease() {
			return getRuleContexts(OnIncreaseContext.class);
		}
		public OnIncreaseContext onIncrease(int i) {
			return getRuleContext(OnIncreaseContext.class,i);
		}
		public List<OnDecreaseContext> onDecrease() {
			return getRuleContexts(OnDecreaseContext.class);
		}
		public OnDecreaseContext onDecrease(int i) {
			return getRuleContext(OnDecreaseContext.class,i);
		}
		public List<MsgtContext> msgt() {
			return getRuleContexts(MsgtContext.class);
		}
		public MsgtContext msgt(int i) {
			return getRuleContext(MsgtContext.class,i);
		}
		public List<FailContext> fail() {
			return getRuleContexts(FailContext.class);
		}
		public FailContext fail(int i) {
			return getRuleContext(FailContext.class,i);
		}
		public List<OnBeginEffectContext> onBeginEffect() {
			return getRuleContexts(OnBeginEffectContext.class);
		}
		public OnBeginEffectContext onBeginEffect(int i) {
			return getRuleContext(OnBeginEffectContext.class,i);
		}
		public List<OnEndEffectContext> onEndEffect() {
			return getRuleContexts(OnEndEffectContext.class);
		}
		public OnEndEffectContext onEndEffect(int i) {
			return getRuleContext(OnEndEffectContext.class,i);
		}
		public List<EffectDiceContext> effectDice() {
			return getRuleContexts(EffectDiceContext.class);
		}
		public EffectDiceContext effectDice(int i) {
			return getRuleContext(EffectDiceContext.class,i);
		}
		public List<ResistContext> resist() {
			return getRuleContexts(ResistContext.class);
		}
		public ResistContext resist(int i) {
			return getRuleContext(ResistContext.class,i);
		}
		public List<BrandContext> brand() {
			return getRuleContexts(BrandContext.class);
		}
		public BrandContext brand(int i) {
			return getRuleContext(BrandContext.class,i);
		}
		public List<SlayContext> slay() {
			return getRuleContexts(SlayContext.class);
		}
		public SlayContext slay(int i) {
			return getRuleContext(SlayContext.class,i);
		}
		public List<FlagSynonymContext> flagSynonym() {
			return getRuleContexts(FlagSynonymContext.class);
		}
		public FlagSynonymContext flagSynonym(int i) {
			return getRuleContext(FlagSynonymContext.class,i);
		}
		public List<LowerBoundContext> lowerBound() {
			return getRuleContexts(LowerBoundContext.class);
		}
		public LowerBoundContext lowerBound(int i) {
			return getRuleContext(LowerBoundContext.class,i);
		}
		public List<FlagsContext> flags() {
			return getRuleContexts(FlagsContext.class);
		}
		public FlagsContext flags(int i) {
			return getRuleContext(FlagsContext.class,i);
		}
		public PlayerTimedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_playerTimed; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).enterPlayerTimed(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).exitPlayerTimed(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerTimedGrammarVisitor ) return ((PlayerTimedGrammarVisitor<? extends T>)visitor).visitPlayerTimed(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PlayerTimedContext playerTimed() throws RecognitionException {
		PlayerTimedContext _localctx = new PlayerTimedContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_playerTimed);

		            String nameInit = "";
		            String descInit = "";
		            List<PlayerTimedGradeParseRecord> grades = new ArrayList<>();
		            String onEndInit = "";
		            String onIncreaseInit = "";
		            String onDecreaseInit = "";
		            String msgtInit = "";
		            String failTypeInit = "";
		            String failValueInit = "";
		            String onBeginEffectInit = "";
		            String onBeginSubtypeInit = "";
		            String onEndEffectInit = "";
		            String onEndSubtypeInit = "";
		            String effectDiceInit = "";
		            String resistInit = "";
		            List<String> brandList = new ArrayList<>();
		            List<String> slayList = new ArrayList<>();
		            String flagSynonymFlagInit = "";
		            String flagSynonymValueInit = "";
		            String lowerBoundInit = "";
		            List<String> flags = new ArrayList<>();
		            int line = 0;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(143);
			((PlayerTimedContext)_localctx).name = name();
			 nameInit = ((PlayerTimedContext)_localctx).name.nameStr; line = ((PlayerTimedContext)_localctx).name.lineNo; 
			setState(193); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(193);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case DESC:
					{
					setState(145);
					((PlayerTimedContext)_localctx).desc = desc();
					 descInit = ((PlayerTimedContext)_localctx).desc.descStr; 
					}
					break;
				case GRADE:
					{
					setState(148);
					((PlayerTimedContext)_localctx).grade = grade();
					 String colour       = ((PlayerTimedContext)_localctx).grade.colour;
					                    String max          = ((PlayerTimedContext)_localctx).grade.max;
					                    String status       = ((PlayerTimedContext)_localctx).grade.status;
					                    String messageUp    = ((PlayerTimedContext)_localctx).grade.messageUp;
					                    String messageDown  = ((PlayerTimedContext)_localctx).grade.messageDown;
					                    PlayerTimedGradeParseRecord gradeRecord =
					                        new PlayerTimedGradeParseRecord(colour, max, status, messageDown, messageUp);
					                    grades.add(gradeRecord);
					                
					}
					break;
				case ON_END:
					{
					setState(151);
					((PlayerTimedContext)_localctx).onEnd = onEnd();
					 onEndInit = ((PlayerTimedContext)_localctx).onEnd.message; 
					}
					break;
				case ON_INCREASE:
					{
					setState(154);
					((PlayerTimedContext)_localctx).onIncrease = onIncrease();
					 onIncreaseInit = ((PlayerTimedContext)_localctx).onIncrease.message; 
					}
					break;
				case ON_DECREASE:
					{
					setState(157);
					((PlayerTimedContext)_localctx).onDecrease = onDecrease();
					 onDecreaseInit = ((PlayerTimedContext)_localctx).onDecrease.message; 
					}
					break;
				case MSGT:
					{
					setState(160);
					((PlayerTimedContext)_localctx).msgt = msgt();
					 msgtInit = ((PlayerTimedContext)_localctx).msgt.msgType; 
					}
					break;
				case FAIL:
					{
					setState(163);
					((PlayerTimedContext)_localctx).fail = fail();
					 failTypeInit = ((PlayerTimedContext)_localctx).fail.type;
					                   failValueInit = ((PlayerTimedContext)_localctx).fail.value; 
					}
					break;
				case ON_BEGIN_EFFECT:
					{
					setState(166);
					((PlayerTimedContext)_localctx).onBeginEffect = onBeginEffect();
					 onBeginEffectInit = ((PlayerTimedContext)_localctx).onBeginEffect.type;
					                            onBeginSubtypeInit = ((PlayerTimedContext)_localctx).onBeginEffect.subType; 
					}
					break;
				case ON_END_EFFECT:
					{
					setState(169);
					((PlayerTimedContext)_localctx).onEndEffect = onEndEffect();
					 onEndEffectInit = ((PlayerTimedContext)_localctx).onEndEffect.type;
					                          onEndSubtypeInit = ((PlayerTimedContext)_localctx).onEndEffect.subType; 
					}
					break;
				case EFFECT_DICE:
					{
					setState(172);
					((PlayerTimedContext)_localctx).effectDice = effectDice();
					 effectDiceInit = ((PlayerTimedContext)_localctx).effectDice.dice; 
					}
					break;
				case RESIST:
					{
					setState(175);
					((PlayerTimedContext)_localctx).resist = resist();
					 resistInit = ((PlayerTimedContext)_localctx).resist.res; 
					}
					break;
				case BRAND:
					{
					setState(178);
					((PlayerTimedContext)_localctx).brand = brand();
					 brandList.add(((PlayerTimedContext)_localctx).brand.b); 
					}
					break;
				case SLAY:
					{
					setState(181);
					((PlayerTimedContext)_localctx).slay = slay();
					 slayList.add(((PlayerTimedContext)_localctx).slay.s); 
					}
					break;
				case FLAG_SYNONYM:
					{
					setState(184);
					((PlayerTimedContext)_localctx).flagSynonym = flagSynonym();
					 flagSynonymFlagInit = ((PlayerTimedContext)_localctx).flagSynonym.objProp;
					                          flagSynonymValueInit = ((PlayerTimedContext)_localctx).flagSynonym.val; 
					}
					break;
				case LOWER_BOUND:
					{
					setState(187);
					((PlayerTimedContext)_localctx).lowerBound = lowerBound();
					 lowerBoundInit = ((PlayerTimedContext)_localctx).lowerBound.bound; 
					}
					break;
				case FLAGS:
					{
					setState(190);
					((PlayerTimedContext)_localctx).flags = flags();
					 flags.addAll(((PlayerTimedContext)_localctx).flags.flagList); 
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(195); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 524280L) != 0) );
			}
			_ctx.stop = _input.LT(-1);

			            ((PlayerTimedContext)_localctx).timed =  new PlayerTimedParseRecord(nameInit, descInit, grades,
			                    onEndInit, onIncreaseInit, onDecreaseInit, msgtInit, failTypeInit,
			                    failValueInit, onBeginEffectInit, onBeginSubtypeInit,
			                    onEndEffectInit, onEndSubtypeInit, effectDiceInit,
			                    resistInit, brandList, slayList, flagSynonymFlagInit,
			                    flagSynonymValueInit, lowerBoundInit, flags, line);
			        
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
		public String declaredRecordCount;
		public List<PlayerTimedParseRecord> results;
		public RecordCountContext recordCount;
		public PlayerTimedContext playerTimed;
		public RecordCountContext recordCount() {
			return getRuleContext(RecordCountContext.class,0);
		}
		public TerminalNode EOF() { return getToken(PlayerTimedGrammar.EOF, 0); }
		public List<PlayerTimedContext> playerTimed() {
			return getRuleContexts(PlayerTimedContext.class);
		}
		public PlayerTimedContext playerTimed(int i) {
			return getRuleContext(PlayerTimedContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerTimedGrammarListener ) ((PlayerTimedGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerTimedGrammarVisitor ) return ((PlayerTimedGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_file);

		            ((FileContext)_localctx).results =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(197);
			((FileContext)_localctx).recordCount = recordCount();
			 ((FileContext)_localctx).declaredRecordCount =  ((FileContext)_localctx).recordCount.count; 
			setState(202); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(199);
				((FileContext)_localctx).playerTimed = playerTimed();
				 _localctx.results.add(((FileContext)_localctx).playerTimed.timed); 
				}
				}
				setState(204); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(206);
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
		"\u0004\u0001\u001f\u00d1\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007"+
		"\u0012\u0002\u0013\u0007\u0013\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0003\u0003A\b\u0003\u0003\u0003C\b\u0003"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0003\ta\b\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0003"+
		"\ni\b\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0005\u0011\u008b\b\u0011\n\u0011\f\u0011\u008e\t\u0011\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0004\u0012\u00c2\b\u0012\u000b\u0012\f\u0012\u00c3\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0004\u0013\u00cb\b\u0013"+
		"\u000b\u0013\f\u0013\u00cc\u0001\u0013\u0001\u0013\u0001\u0013\u0000\u0000"+
		"\u0014\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018"+
		"\u001a\u001c\u001e \"$&\u0000\u0000\u00d2\u0000(\u0001\u0000\u0000\u0000"+
		"\u0002,\u0001\u0000\u0000\u0000\u00040\u0001\u0000\u0000\u0000\u00064"+
		"\u0001\u0000\u0000\u0000\bD\u0001\u0000\u0000\u0000\nH\u0001\u0000\u0000"+
		"\u0000\fL\u0001\u0000\u0000\u0000\u000eP\u0001\u0000\u0000\u0000\u0010"+
		"T\u0001\u0000\u0000\u0000\u0012Z\u0001\u0000\u0000\u0000\u0014b\u0001"+
		"\u0000\u0000\u0000\u0016j\u0001\u0000\u0000\u0000\u0018n\u0001\u0000\u0000"+
		"\u0000\u001ar\u0001\u0000\u0000\u0000\u001cv\u0001\u0000\u0000\u0000\u001e"+
		"z\u0001\u0000\u0000\u0000 \u0080\u0001\u0000\u0000\u0000\"\u0084\u0001"+
		"\u0000\u0000\u0000$\u008f\u0001\u0000\u0000\u0000&\u00c5\u0001\u0000\u0000"+
		"\u0000()\u0005\u0001\u0000\u0000)*\u0005\u0013\u0000\u0000*+\u0006\u0000"+
		"\uffff\uffff\u0000+\u0001\u0001\u0000\u0000\u0000,-\u0005\u0002\u0000"+
		"\u0000-.\u0005\u0016\u0000\u0000./\u0006\u0001\uffff\uffff\u0000/\u0003"+
		"\u0001\u0000\u0000\u000001\u0005\u0003\u0000\u000012\u0005\u0019\u0000"+
		"\u000023\u0006\u0002\uffff\uffff\u00003\u0005\u0001\u0000\u0000\u0000"+
		"45\u0005\u0004\u0000\u000056\u0005\u001c\u0000\u000067\u0005\u001e\u0000"+
		"\u000078\u0005\u001b\u0000\u000089\u0005\u001e\u0000\u00009:\u0005\u001d"+
		"\u0000\u0000:;\u0005\u001e\u0000\u0000;<\u0005\u001d\u0000\u0000<B\u0006"+
		"\u0003\uffff\uffff\u0000=@\u0005\u001e\u0000\u0000>?\u0005\u001d\u0000"+
		"\u0000?A\u0006\u0003\uffff\uffff\u0000@>\u0001\u0000\u0000\u0000@A\u0001"+
		"\u0000\u0000\u0000AC\u0001\u0000\u0000\u0000B=\u0001\u0000\u0000\u0000"+
		"BC\u0001\u0000\u0000\u0000C\u0007\u0001\u0000\u0000\u0000DE\u0005\u0005"+
		"\u0000\u0000EF\u0005\u0019\u0000\u0000FG\u0006\u0004\uffff\uffff\u0000"+
		"G\t\u0001\u0000\u0000\u0000HI\u0005\u0006\u0000\u0000IJ\u0005\u0019\u0000"+
		"\u0000JK\u0006\u0005\uffff\uffff\u0000K\u000b\u0001\u0000\u0000\u0000"+
		"LM\u0005\u0007\u0000\u0000MN\u0005\u0019\u0000\u0000NO\u0006\u0006\uffff"+
		"\uffff\u0000O\r\u0001\u0000\u0000\u0000PQ\u0005\b\u0000\u0000QR\u0005"+
		"\u0016\u0000\u0000RS\u0006\u0007\uffff\uffff\u0000S\u000f\u0001\u0000"+
		"\u0000\u0000TU\u0005\t\u0000\u0000UV\u0005\u001b\u0000\u0000VW\u0005\u001e"+
		"\u0000\u0000WX\u0005\u001d\u0000\u0000XY\u0006\b\uffff\uffff\u0000Y\u0011"+
		"\u0001\u0000\u0000\u0000Z[\u0005\n\u0000\u0000[\\\u0005\u001d\u0000\u0000"+
		"\\`\u0006\t\uffff\uffff\u0000]^\u0005\u001e\u0000\u0000^_\u0005\u001d"+
		"\u0000\u0000_a\u0006\t\uffff\uffff\u0000`]\u0001\u0000\u0000\u0000`a\u0001"+
		"\u0000\u0000\u0000a\u0013\u0001\u0000\u0000\u0000bc\u0005\u000b\u0000"+
		"\u0000cd\u0005\u001d\u0000\u0000dh\u0006\n\uffff\uffff\u0000ef\u0005\u001e"+
		"\u0000\u0000fg\u0005\u001d\u0000\u0000gi\u0006\n\uffff\uffff\u0000he\u0001"+
		"\u0000\u0000\u0000hi\u0001\u0000\u0000\u0000i\u0015\u0001\u0000\u0000"+
		"\u0000jk\u0005\f\u0000\u0000kl\u0005\u0013\u0000\u0000lm\u0006\u000b\uffff"+
		"\uffff\u0000m\u0017\u0001\u0000\u0000\u0000no\u0005\r\u0000\u0000op\u0005"+
		"\u0016\u0000\u0000pq\u0006\f\uffff\uffff\u0000q\u0019\u0001\u0000\u0000"+
		"\u0000rs\u0005\u000e\u0000\u0000st\u0005\u0016\u0000\u0000tu\u0006\r\uffff"+
		"\uffff\u0000u\u001b\u0001\u0000\u0000\u0000vw\u0005\u000f\u0000\u0000"+
		"wx\u0005\u0016\u0000\u0000xy\u0006\u000e\uffff\uffff\u0000y\u001d\u0001"+
		"\u0000\u0000\u0000z{\u0005\u0010\u0000\u0000{|\u0005\u001d\u0000\u0000"+
		"|}\u0005\u001e\u0000\u0000}~\u0005\u001b\u0000\u0000~\u007f\u0006\u000f"+
		"\uffff\uffff\u0000\u007f\u001f\u0001\u0000\u0000\u0000\u0080\u0081\u0005"+
		"\u0011\u0000\u0000\u0081\u0082\u0005\u0013\u0000\u0000\u0082\u0083\u0006"+
		"\u0010\uffff\uffff\u0000\u0083!\u0001\u0000\u0000\u0000\u0084\u0085\u0005"+
		"\u0012\u0000\u0000\u0085\u0086\u0005\u0016\u0000\u0000\u0086\u008c\u0006"+
		"\u0011\uffff\uffff\u0000\u0087\u0088\u0005\u0017\u0000\u0000\u0088\u0089"+
		"\u0005\u0016\u0000\u0000\u0089\u008b\u0006\u0011\uffff\uffff\u0000\u008a"+
		"\u0087\u0001\u0000\u0000\u0000\u008b\u008e\u0001\u0000\u0000\u0000\u008c"+
		"\u008a\u0001\u0000\u0000\u0000\u008c\u008d\u0001\u0000\u0000\u0000\u008d"+
		"#\u0001\u0000\u0000\u0000\u008e\u008c\u0001\u0000\u0000\u0000\u008f\u0090"+
		"\u0003\u0002\u0001\u0000\u0090\u00c1\u0006\u0012\uffff\uffff\u0000\u0091"+
		"\u0092\u0003\u0004\u0002\u0000\u0092\u0093\u0006\u0012\uffff\uffff\u0000"+
		"\u0093\u00c2\u0001\u0000\u0000\u0000\u0094\u0095\u0003\u0006\u0003\u0000"+
		"\u0095\u0096\u0006\u0012\uffff\uffff\u0000\u0096\u00c2\u0001\u0000\u0000"+
		"\u0000\u0097\u0098\u0003\b\u0004\u0000\u0098\u0099\u0006\u0012\uffff\uffff"+
		"\u0000\u0099\u00c2\u0001\u0000\u0000\u0000\u009a\u009b\u0003\n\u0005\u0000"+
		"\u009b\u009c\u0006\u0012\uffff\uffff\u0000\u009c\u00c2\u0001\u0000\u0000"+
		"\u0000\u009d\u009e\u0003\f\u0006\u0000\u009e\u009f\u0006\u0012\uffff\uffff"+
		"\u0000\u009f\u00c2\u0001\u0000\u0000\u0000\u00a0\u00a1\u0003\u000e\u0007"+
		"\u0000\u00a1\u00a2\u0006\u0012\uffff\uffff\u0000\u00a2\u00c2\u0001\u0000"+
		"\u0000\u0000\u00a3\u00a4\u0003\u0010\b\u0000\u00a4\u00a5\u0006\u0012\uffff"+
		"\uffff\u0000\u00a5\u00c2\u0001\u0000\u0000\u0000\u00a6\u00a7\u0003\u0012"+
		"\t\u0000\u00a7\u00a8\u0006\u0012\uffff\uffff\u0000\u00a8\u00c2\u0001\u0000"+
		"\u0000\u0000\u00a9\u00aa\u0003\u0014\n\u0000\u00aa\u00ab\u0006\u0012\uffff"+
		"\uffff\u0000\u00ab\u00c2\u0001\u0000\u0000\u0000\u00ac\u00ad\u0003\u0016"+
		"\u000b\u0000\u00ad\u00ae\u0006\u0012\uffff\uffff\u0000\u00ae\u00c2\u0001"+
		"\u0000\u0000\u0000\u00af\u00b0\u0003\u0018\f\u0000\u00b0\u00b1\u0006\u0012"+
		"\uffff\uffff\u0000\u00b1\u00c2\u0001\u0000\u0000\u0000\u00b2\u00b3\u0003"+
		"\u001a\r\u0000\u00b3\u00b4\u0006\u0012\uffff\uffff\u0000\u00b4\u00c2\u0001"+
		"\u0000\u0000\u0000\u00b5\u00b6\u0003\u001c\u000e\u0000\u00b6\u00b7\u0006"+
		"\u0012\uffff\uffff\u0000\u00b7\u00c2\u0001\u0000\u0000\u0000\u00b8\u00b9"+
		"\u0003\u001e\u000f\u0000\u00b9\u00ba\u0006\u0012\uffff\uffff\u0000\u00ba"+
		"\u00c2\u0001\u0000\u0000\u0000\u00bb\u00bc\u0003 \u0010\u0000\u00bc\u00bd"+
		"\u0006\u0012\uffff\uffff\u0000\u00bd\u00c2\u0001\u0000\u0000\u0000\u00be"+
		"\u00bf\u0003\"\u0011\u0000\u00bf\u00c0\u0006\u0012\uffff\uffff\u0000\u00c0"+
		"\u00c2\u0001\u0000\u0000\u0000\u00c1\u0091\u0001\u0000\u0000\u0000\u00c1"+
		"\u0094\u0001\u0000\u0000\u0000\u00c1\u0097\u0001\u0000\u0000\u0000\u00c1"+
		"\u009a\u0001\u0000\u0000\u0000\u00c1\u009d\u0001\u0000\u0000\u0000\u00c1"+
		"\u00a0\u0001\u0000\u0000\u0000\u00c1\u00a3\u0001\u0000\u0000\u0000\u00c1"+
		"\u00a6\u0001\u0000\u0000\u0000\u00c1\u00a9\u0001\u0000\u0000\u0000\u00c1"+
		"\u00ac\u0001\u0000\u0000\u0000\u00c1\u00af\u0001\u0000\u0000\u0000\u00c1"+
		"\u00b2\u0001\u0000\u0000\u0000\u00c1\u00b5\u0001\u0000\u0000\u0000\u00c1"+
		"\u00b8\u0001\u0000\u0000\u0000\u00c1\u00bb\u0001\u0000\u0000\u0000\u00c1"+
		"\u00be\u0001\u0000\u0000\u0000\u00c2\u00c3\u0001\u0000\u0000\u0000\u00c3"+
		"\u00c1\u0001\u0000\u0000\u0000\u00c3\u00c4\u0001\u0000\u0000\u0000\u00c4"+
		"%\u0001\u0000\u0000\u0000\u00c5\u00c6\u0003\u0000\u0000\u0000\u00c6\u00ca"+
		"\u0006\u0013\uffff\uffff\u0000\u00c7\u00c8\u0003$\u0012\u0000\u00c8\u00c9"+
		"\u0006\u0013\uffff\uffff\u0000\u00c9\u00cb\u0001\u0000\u0000\u0000\u00ca"+
		"\u00c7\u0001\u0000\u0000\u0000\u00cb\u00cc\u0001\u0000\u0000\u0000\u00cc"+
		"\u00ca\u0001\u0000\u0000\u0000\u00cc\u00cd\u0001\u0000\u0000\u0000\u00cd"+
		"\u00ce\u0001\u0000\u0000\u0000\u00ce\u00cf\u0005\u0000\u0000\u0001\u00cf"+
		"\'\u0001\u0000\u0000\u0000\b@B`h\u008c\u00c1\u00c3\u00cc";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}