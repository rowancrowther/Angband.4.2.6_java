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

// Generated from /home/rowan/IdeaProjects/Angband.4.2.6_java/src/main/java/uk/co/jackoftradesltd/backend/parser/grammars/ProjectionGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.projection;

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
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ProjectionGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		CODE=1, NAME=2, TYPE=3, DESC=4, PLAYER_DESC=5, BLIND_DESC=6, LASH_DESC=7, 
		NUMERATOR=8, DENOMINATOR=9, DIVISOR=10, DAMAGE_CAP=11, MSGT=12, OBVIOUS=13, 
		WAKE=14, COLOUR=15, INTEGER=16, COMMENT=17, EOL=18, SIMPLE_DICE_STRING=19, 
		COMPLEX_DICE_STRING=20, FLAG=21, STRING=22, SIMPLE_DICE=23;
	public static final int
		RULE_code = 0, RULE_name = 1, RULE_type = 2, RULE_desc = 3, RULE_playerDesc = 4, 
		RULE_blindDesc = 5, RULE_lashDesc = 6, RULE_numerator = 7, RULE_denominator = 8, 
		RULE_divisor = 9, RULE_damageCap = 10, RULE_msgt = 11, RULE_obvious = 12, 
		RULE_wake = 13, RULE_colour = 14, RULE_projection = 15, RULE_file = 16;
	private static String[] makeRuleNames() {
		return new String[] {
			"code", "name", "type", "desc", "playerDesc", "blindDesc", "lashDesc", 
			"numerator", "denominator", "divisor", "damageCap", "msgt", "obvious", 
			"wake", "colour", "projection", "file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'code:'", "'name:'", "'type:'", "'desc:'", "'player-desc:'", "'blind-desc:'", 
			"'lash-desc:'", "'numerator:'", "'denominator:'", "'divisor:'", "'damage-cap:'", 
			"'msgt:'", "'obvious:'", "'wake:'", "'color:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "CODE", "NAME", "TYPE", "DESC", "PLAYER_DESC", "BLIND_DESC", "LASH_DESC", 
			"NUMERATOR", "DENOMINATOR", "DIVISOR", "DAMAGE_CAP", "MSGT", "OBVIOUS", 
			"WAKE", "COLOUR", "INTEGER", "COMMENT", "EOL", "SIMPLE_DICE_STRING", 
			"COMPLEX_DICE_STRING", "FLAG", "STRING", "SIMPLE_DICE"
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
	public String getGrammarFileName() { return "ProjectionGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ProjectionGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CodeContext extends ParserRuleContext {
		public String projectionCode;
		public Token FLAG;
		public TerminalNode CODE() { return getToken(ProjectionGrammar.CODE, 0); }
		public TerminalNode FLAG() { return getToken(ProjectionGrammar.FLAG, 0); }
		public CodeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_code; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).enterCode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).exitCode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ProjectionGrammarVisitor ) return ((ProjectionGrammarVisitor<? extends T>)visitor).visitCode(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CodeContext code() throws RecognitionException {
		CodeContext _localctx = new CodeContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_code);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(34);
			match(CODE);
			setState(35);
			((CodeContext)_localctx).FLAG = match(FLAG);

			            ((CodeContext)_localctx).projectionCode =  ((CodeContext)_localctx).FLAG.getText();
			        
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
		public String projectionName;
		public Token STRING;
		public TerminalNode NAME() { return getToken(ProjectionGrammar.NAME, 0); }
		public TerminalNode STRING() { return getToken(ProjectionGrammar.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ProjectionGrammarVisitor ) return ((ProjectionGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(38);
			match(NAME);
			setState(39);
			((NameContext)_localctx).STRING = match(STRING);

			            ((NameContext)_localctx).projectionName =  ((NameContext)_localctx).STRING.getText();
			        
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
		public String projectionType;
		public Token STRING;
		public TerminalNode TYPE() { return getToken(ProjectionGrammar.TYPE, 0); }
		public TerminalNode STRING() { return getToken(ProjectionGrammar.STRING, 0); }
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).exitType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ProjectionGrammarVisitor ) return ((ProjectionGrammarVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(42);
			match(TYPE);
			setState(43);
			((TypeContext)_localctx).STRING = match(STRING);

			            ((TypeContext)_localctx).projectionType =  ((TypeContext)_localctx).STRING.getText();
			        
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
		public String projectionDesc;
		public Token STRING;
		public TerminalNode DESC() { return getToken(ProjectionGrammar.DESC, 0); }
		public TerminalNode STRING() { return getToken(ProjectionGrammar.STRING, 0); }
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_desc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ProjectionGrammarVisitor ) return ((ProjectionGrammarVisitor<? extends T>)visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_desc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			match(DESC);
			setState(47);
			((DescContext)_localctx).STRING = match(STRING);

			            ((DescContext)_localctx).projectionDesc =  ((DescContext)_localctx).STRING.getText();
			        
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
	public static class PlayerDescContext extends ParserRuleContext {
		public String projectionPlayerDesc;
		public Token STRING;
		public TerminalNode PLAYER_DESC() { return getToken(ProjectionGrammar.PLAYER_DESC, 0); }
		public TerminalNode STRING() { return getToken(ProjectionGrammar.STRING, 0); }
		public PlayerDescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_playerDesc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).enterPlayerDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).exitPlayerDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ProjectionGrammarVisitor ) return ((ProjectionGrammarVisitor<? extends T>)visitor).visitPlayerDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PlayerDescContext playerDesc() throws RecognitionException {
		PlayerDescContext _localctx = new PlayerDescContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_playerDesc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(50);
			match(PLAYER_DESC);
			setState(51);
			((PlayerDescContext)_localctx).STRING = match(STRING);

			            ((PlayerDescContext)_localctx).projectionPlayerDesc =  ((PlayerDescContext)_localctx).STRING.getText();
			        
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
	public static class BlindDescContext extends ParserRuleContext {
		public String projectionBlindDesc;
		public Token STRING;
		public TerminalNode BLIND_DESC() { return getToken(ProjectionGrammar.BLIND_DESC, 0); }
		public TerminalNode STRING() { return getToken(ProjectionGrammar.STRING, 0); }
		public BlindDescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_blindDesc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).enterBlindDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).exitBlindDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ProjectionGrammarVisitor ) return ((ProjectionGrammarVisitor<? extends T>)visitor).visitBlindDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlindDescContext blindDesc() throws RecognitionException {
		BlindDescContext _localctx = new BlindDescContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_blindDesc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(54);
			match(BLIND_DESC);
			setState(55);
			((BlindDescContext)_localctx).STRING = match(STRING);

			            ((BlindDescContext)_localctx).projectionBlindDesc =  ((BlindDescContext)_localctx).STRING.getText();
			        
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
	public static class LashDescContext extends ParserRuleContext {
		public String projectionLashDesc;
		public Token STRING;
		public TerminalNode LASH_DESC() { return getToken(ProjectionGrammar.LASH_DESC, 0); }
		public TerminalNode STRING() { return getToken(ProjectionGrammar.STRING, 0); }
		public LashDescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lashDesc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).enterLashDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).exitLashDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ProjectionGrammarVisitor ) return ((ProjectionGrammarVisitor<? extends T>)visitor).visitLashDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LashDescContext lashDesc() throws RecognitionException {
		LashDescContext _localctx = new LashDescContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_lashDesc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58);
			match(LASH_DESC);
			setState(59);
			((LashDescContext)_localctx).STRING = match(STRING);

			            ((LashDescContext)_localctx).projectionLashDesc =  ((LashDescContext)_localctx).STRING.getText();
			        
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
	public static class NumeratorContext extends ParserRuleContext {
		public String projectionNumerator;
		public Token INTEGER;
		public TerminalNode NUMERATOR() { return getToken(ProjectionGrammar.NUMERATOR, 0); }
		public TerminalNode INTEGER() { return getToken(ProjectionGrammar.INTEGER, 0); }
		public NumeratorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numerator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).enterNumerator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).exitNumerator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ProjectionGrammarVisitor ) return ((ProjectionGrammarVisitor<? extends T>)visitor).visitNumerator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumeratorContext numerator() throws RecognitionException {
		NumeratorContext _localctx = new NumeratorContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_numerator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(62);
			match(NUMERATOR);
			setState(63);
			((NumeratorContext)_localctx).INTEGER = match(INTEGER);

			            ((NumeratorContext)_localctx).projectionNumerator =  ((NumeratorContext)_localctx).INTEGER.getText();
			        
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
	public static class DenominatorContext extends ParserRuleContext {
		public String projectionDenominator;
		public Token SIMPLE_DICE;
		public TerminalNode DENOMINATOR() { return getToken(ProjectionGrammar.DENOMINATOR, 0); }
		public TerminalNode SIMPLE_DICE() { return getToken(ProjectionGrammar.SIMPLE_DICE, 0); }
		public DenominatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_denominator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).enterDenominator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).exitDenominator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ProjectionGrammarVisitor ) return ((ProjectionGrammarVisitor<? extends T>)visitor).visitDenominator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DenominatorContext denominator() throws RecognitionException {
		DenominatorContext _localctx = new DenominatorContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_denominator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(66);
			match(DENOMINATOR);
			setState(67);
			((DenominatorContext)_localctx).SIMPLE_DICE = match(SIMPLE_DICE);

			            ((DenominatorContext)_localctx).projectionDenominator =  ((DenominatorContext)_localctx).SIMPLE_DICE.getText();
			        
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
	public static class DivisorContext extends ParserRuleContext {
		public String projectionDivisor;
		public Token INTEGER;
		public TerminalNode DIVISOR() { return getToken(ProjectionGrammar.DIVISOR, 0); }
		public TerminalNode INTEGER() { return getToken(ProjectionGrammar.INTEGER, 0); }
		public DivisorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_divisor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).enterDivisor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).exitDivisor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ProjectionGrammarVisitor ) return ((ProjectionGrammarVisitor<? extends T>)visitor).visitDivisor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DivisorContext divisor() throws RecognitionException {
		DivisorContext _localctx = new DivisorContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_divisor);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70);
			match(DIVISOR);
			setState(71);
			((DivisorContext)_localctx).INTEGER = match(INTEGER);

			            ((DivisorContext)_localctx).projectionDivisor =  ((DivisorContext)_localctx).INTEGER.getText();
			        
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
	public static class DamageCapContext extends ParserRuleContext {
		public String projectionDamageCap;
		public Token INTEGER;
		public TerminalNode DAMAGE_CAP() { return getToken(ProjectionGrammar.DAMAGE_CAP, 0); }
		public TerminalNode INTEGER() { return getToken(ProjectionGrammar.INTEGER, 0); }
		public DamageCapContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_damageCap; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).enterDamageCap(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).exitDamageCap(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ProjectionGrammarVisitor ) return ((ProjectionGrammarVisitor<? extends T>)visitor).visitDamageCap(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DamageCapContext damageCap() throws RecognitionException {
		DamageCapContext _localctx = new DamageCapContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_damageCap);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			match(DAMAGE_CAP);
			setState(75);
			((DamageCapContext)_localctx).INTEGER = match(INTEGER);

			            ((DamageCapContext)_localctx).projectionDamageCap =  ((DamageCapContext)_localctx).INTEGER.getText();
			        
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
		public String projectionMsgt;
		public Token FLAG;
		public TerminalNode MSGT() { return getToken(ProjectionGrammar.MSGT, 0); }
		public TerminalNode FLAG() { return getToken(ProjectionGrammar.FLAG, 0); }
		public MsgtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_msgt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).enterMsgt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).exitMsgt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ProjectionGrammarVisitor ) return ((ProjectionGrammarVisitor<? extends T>)visitor).visitMsgt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MsgtContext msgt() throws RecognitionException {
		MsgtContext _localctx = new MsgtContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_msgt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(78);
			match(MSGT);
			setState(79);
			((MsgtContext)_localctx).FLAG = match(FLAG);

			            ((MsgtContext)_localctx).projectionMsgt =  ((MsgtContext)_localctx).FLAG.getText();
			        
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
	public static class ObviousContext extends ParserRuleContext {
		public String projectionIsObvious;
		public Token INTEGER;
		public TerminalNode OBVIOUS() { return getToken(ProjectionGrammar.OBVIOUS, 0); }
		public TerminalNode INTEGER() { return getToken(ProjectionGrammar.INTEGER, 0); }
		public ObviousContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_obvious; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).enterObvious(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).exitObvious(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ProjectionGrammarVisitor ) return ((ProjectionGrammarVisitor<? extends T>)visitor).visitObvious(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObviousContext obvious() throws RecognitionException {
		ObviousContext _localctx = new ObviousContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_obvious);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			match(OBVIOUS);
			setState(83);
			((ObviousContext)_localctx).INTEGER = match(INTEGER);

			            ((ObviousContext)_localctx).projectionIsObvious =  ((ObviousContext)_localctx).INTEGER.getText();
			        
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
	public static class WakeContext extends ParserRuleContext {
		public String projectionWillWake;
		public Token INTEGER;
		public TerminalNode WAKE() { return getToken(ProjectionGrammar.WAKE, 0); }
		public TerminalNode INTEGER() { return getToken(ProjectionGrammar.INTEGER, 0); }
		public WakeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_wake; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).enterWake(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).exitWake(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ProjectionGrammarVisitor ) return ((ProjectionGrammarVisitor<? extends T>)visitor).visitWake(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WakeContext wake() throws RecognitionException {
		WakeContext _localctx = new WakeContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_wake);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(86);
			match(WAKE);
			setState(87);
			((WakeContext)_localctx).INTEGER = match(INTEGER);

			            ((WakeContext)_localctx).projectionWillWake =  ((WakeContext)_localctx).INTEGER.getText();
			        
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
	public static class ColourContext extends ParserRuleContext {
		public String projectionColour;
		public Token STRING;
		public TerminalNode COLOUR() { return getToken(ProjectionGrammar.COLOUR, 0); }
		public TerminalNode STRING() { return getToken(ProjectionGrammar.STRING, 0); }
		public ColourContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_colour; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).enterColour(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).exitColour(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ProjectionGrammarVisitor ) return ((ProjectionGrammarVisitor<? extends T>)visitor).visitColour(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ColourContext colour() throws RecognitionException {
		ColourContext _localctx = new ColourContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_colour);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(90);
			match(COLOUR);
			setState(91);
			((ColourContext)_localctx).STRING = match(STRING);

			            ((ColourContext)_localctx).projectionColour =  ((ColourContext)_localctx).STRING.getText();
			        
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
	public static class ProjectionContext extends ParserRuleContext {
		public List<String> proj;
		public int lineNo;
		public CodeContext code;
		public NameContext name;
		public TypeContext type;
		public DescContext desc;
		public PlayerDescContext playerDesc;
		public BlindDescContext blindDesc;
		public LashDescContext lashDesc;
		public NumeratorContext numerator;
		public DenominatorContext denominator;
		public DivisorContext divisor;
		public DamageCapContext damageCap;
		public MsgtContext msgt;
		public ObviousContext obvious;
		public WakeContext wake;
		public ColourContext colour;
		public CodeContext code() {
			return getRuleContext(CodeContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public DescContext desc() {
			return getRuleContext(DescContext.class,0);
		}
		public BlindDescContext blindDesc() {
			return getRuleContext(BlindDescContext.class,0);
		}
		public ObviousContext obvious() {
			return getRuleContext(ObviousContext.class,0);
		}
		public ColourContext colour() {
			return getRuleContext(ColourContext.class,0);
		}
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public PlayerDescContext playerDesc() {
			return getRuleContext(PlayerDescContext.class,0);
		}
		public LashDescContext lashDesc() {
			return getRuleContext(LashDescContext.class,0);
		}
		public NumeratorContext numerator() {
			return getRuleContext(NumeratorContext.class,0);
		}
		public DenominatorContext denominator() {
			return getRuleContext(DenominatorContext.class,0);
		}
		public DivisorContext divisor() {
			return getRuleContext(DivisorContext.class,0);
		}
		public DamageCapContext damageCap() {
			return getRuleContext(DamageCapContext.class,0);
		}
		public MsgtContext msgt() {
			return getRuleContext(MsgtContext.class,0);
		}
		public WakeContext wake() {
			return getRuleContext(WakeContext.class,0);
		}
		public ProjectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_projection; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).enterProjection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).exitProjection(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ProjectionGrammarVisitor ) return ((ProjectionGrammarVisitor<? extends T>)visitor).visitProjection(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProjectionContext projection() throws RecognitionException {
		ProjectionContext _localctx = new ProjectionContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_projection);

		            String[] projectionStrings = new String[15];
		            Arrays.fill(projectionStrings, null);
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(94);
			((ProjectionContext)_localctx).code = code();
			 projectionStrings[0] = ((ProjectionContext)_localctx).code.projectionCode;
			                   ((ProjectionContext)_localctx).lineNo =  _localctx.start.getLine(); 
			setState(99);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NAME) {
				{
				setState(96);
				((ProjectionContext)_localctx).name = name();
				 projectionStrings[1] = ((ProjectionContext)_localctx).name.projectionName; 
				}
			}

			setState(101);
			((ProjectionContext)_localctx).type = type();
			 projectionStrings[2] = ((ProjectionContext)_localctx).type.projectionType; 
			setState(103);
			((ProjectionContext)_localctx).desc = desc();
			 projectionStrings[3] = ((ProjectionContext)_localctx).desc.projectionDesc; 
			setState(108);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PLAYER_DESC) {
				{
				setState(105);
				((ProjectionContext)_localctx).playerDesc = playerDesc();
				 projectionStrings[4] = ((ProjectionContext)_localctx).playerDesc.projectionPlayerDesc; 
				}
			}

			setState(110);
			((ProjectionContext)_localctx).blindDesc = blindDesc();
			 projectionStrings[5] = ((ProjectionContext)_localctx).blindDesc.projectionBlindDesc; 
			setState(115);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LASH_DESC) {
				{
				setState(112);
				((ProjectionContext)_localctx).lashDesc = lashDesc();
				 projectionStrings[6] = ((ProjectionContext)_localctx).lashDesc.projectionLashDesc; 
				}
			}

			setState(120);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NUMERATOR) {
				{
				setState(117);
				((ProjectionContext)_localctx).numerator = numerator();
				 projectionStrings[7] = ((ProjectionContext)_localctx).numerator.projectionNumerator; 
				}
			}

			setState(125);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DENOMINATOR) {
				{
				setState(122);
				((ProjectionContext)_localctx).denominator = denominator();
				 projectionStrings[8] = ((ProjectionContext)_localctx).denominator.projectionDenominator; 
				}
			}

			setState(130);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DIVISOR) {
				{
				setState(127);
				((ProjectionContext)_localctx).divisor = divisor();
				 projectionStrings[9] = ((ProjectionContext)_localctx).divisor.projectionDivisor; 
				}
			}

			setState(135);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DAMAGE_CAP) {
				{
				setState(132);
				((ProjectionContext)_localctx).damageCap = damageCap();
				 projectionStrings[10] = ((ProjectionContext)_localctx).damageCap.projectionDamageCap; 
				}
			}

			setState(140);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==MSGT) {
				{
				setState(137);
				((ProjectionContext)_localctx).msgt = msgt();
				 projectionStrings[11] = ((ProjectionContext)_localctx).msgt.projectionMsgt; 
				}
			}

			setState(142);
			((ProjectionContext)_localctx).obvious = obvious();
			 projectionStrings[12] = ((ProjectionContext)_localctx).obvious.projectionIsObvious; 
			setState(147);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WAKE) {
				{
				setState(144);
				((ProjectionContext)_localctx).wake = wake();
				 projectionStrings[13] = ((ProjectionContext)_localctx).wake.projectionWillWake; 
				}
			}

			setState(149);
			((ProjectionContext)_localctx).colour = colour();
			 projectionStrings[14] = ((ProjectionContext)_localctx).colour.projectionColour; 
			}
			_ctx.stop = _input.LT(-1);

			            ((ProjectionContext)_localctx).proj =  new ArrayList<>(Arrays.asList(projectionStrings));
			            _localctx.proj.add(String.valueOf(_localctx.lineNo));
			        
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
		public List<List<String>> projections;
		public ProjectionContext projection;
		public TerminalNode EOF() { return getToken(ProjectionGrammar.EOF, 0); }
		public List<ProjectionContext> projection() {
			return getRuleContexts(ProjectionContext.class);
		}
		public ProjectionContext projection(int i) {
			return getRuleContext(ProjectionContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ProjectionGrammarListener ) ((ProjectionGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ProjectionGrammarVisitor ) return ((ProjectionGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_file);

		            ((FileContext)_localctx).projections =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(155); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(152);
				((FileContext)_localctx).projection = projection();

				                _localctx.projections.add(((FileContext)_localctx).projection.proj);
				            
				}
				}
				setState(157); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==CODE );
			setState(159);
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
		"\u0004\u0001\u0017\u00a2\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b"+
		"\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0003\u000fd\b\u000f\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000fm\b\u000f"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f"+
		"t\b\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000fy\b\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0003\u000f~\b\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0003\u000f\u0083\b\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0003\u000f\u0088\b\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0003"+
		"\u000f\u008d\b\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0003\u000f\u0094\b\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0004\u0010\u009c\b\u0010\u000b\u0010\f"+
		"\u0010\u009d\u0001\u0010\u0001\u0010\u0001\u0010\u0000\u0000\u0011\u0000"+
		"\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c"+
		"\u001e \u0000\u0000\u009a\u0000\"\u0001\u0000\u0000\u0000\u0002&\u0001"+
		"\u0000\u0000\u0000\u0004*\u0001\u0000\u0000\u0000\u0006.\u0001\u0000\u0000"+
		"\u0000\b2\u0001\u0000\u0000\u0000\n6\u0001\u0000\u0000\u0000\f:\u0001"+
		"\u0000\u0000\u0000\u000e>\u0001\u0000\u0000\u0000\u0010B\u0001\u0000\u0000"+
		"\u0000\u0012F\u0001\u0000\u0000\u0000\u0014J\u0001\u0000\u0000\u0000\u0016"+
		"N\u0001\u0000\u0000\u0000\u0018R\u0001\u0000\u0000\u0000\u001aV\u0001"+
		"\u0000\u0000\u0000\u001cZ\u0001\u0000\u0000\u0000\u001e^\u0001\u0000\u0000"+
		"\u0000 \u009b\u0001\u0000\u0000\u0000\"#\u0005\u0001\u0000\u0000#$\u0005"+
		"\u0015\u0000\u0000$%\u0006\u0000\uffff\uffff\u0000%\u0001\u0001\u0000"+
		"\u0000\u0000&\'\u0005\u0002\u0000\u0000\'(\u0005\u0016\u0000\u0000()\u0006"+
		"\u0001\uffff\uffff\u0000)\u0003\u0001\u0000\u0000\u0000*+\u0005\u0003"+
		"\u0000\u0000+,\u0005\u0016\u0000\u0000,-\u0006\u0002\uffff\uffff\u0000"+
		"-\u0005\u0001\u0000\u0000\u0000./\u0005\u0004\u0000\u0000/0\u0005\u0016"+
		"\u0000\u000001\u0006\u0003\uffff\uffff\u00001\u0007\u0001\u0000\u0000"+
		"\u000023\u0005\u0005\u0000\u000034\u0005\u0016\u0000\u000045\u0006\u0004"+
		"\uffff\uffff\u00005\t\u0001\u0000\u0000\u000067\u0005\u0006\u0000\u0000"+
		"78\u0005\u0016\u0000\u000089\u0006\u0005\uffff\uffff\u00009\u000b\u0001"+
		"\u0000\u0000\u0000:;\u0005\u0007\u0000\u0000;<\u0005\u0016\u0000\u0000"+
		"<=\u0006\u0006\uffff\uffff\u0000=\r\u0001\u0000\u0000\u0000>?\u0005\b"+
		"\u0000\u0000?@\u0005\u0010\u0000\u0000@A\u0006\u0007\uffff\uffff\u0000"+
		"A\u000f\u0001\u0000\u0000\u0000BC\u0005\t\u0000\u0000CD\u0005\u0017\u0000"+
		"\u0000DE\u0006\b\uffff\uffff\u0000E\u0011\u0001\u0000\u0000\u0000FG\u0005"+
		"\n\u0000\u0000GH\u0005\u0010\u0000\u0000HI\u0006\t\uffff\uffff\u0000I"+
		"\u0013\u0001\u0000\u0000\u0000JK\u0005\u000b\u0000\u0000KL\u0005\u0010"+
		"\u0000\u0000LM\u0006\n\uffff\uffff\u0000M\u0015\u0001\u0000\u0000\u0000"+
		"NO\u0005\f\u0000\u0000OP\u0005\u0015\u0000\u0000PQ\u0006\u000b\uffff\uffff"+
		"\u0000Q\u0017\u0001\u0000\u0000\u0000RS\u0005\r\u0000\u0000ST\u0005\u0010"+
		"\u0000\u0000TU\u0006\f\uffff\uffff\u0000U\u0019\u0001\u0000\u0000\u0000"+
		"VW\u0005\u000e\u0000\u0000WX\u0005\u0010\u0000\u0000XY\u0006\r\uffff\uffff"+
		"\u0000Y\u001b\u0001\u0000\u0000\u0000Z[\u0005\u000f\u0000\u0000[\\\u0005"+
		"\u0016\u0000\u0000\\]\u0006\u000e\uffff\uffff\u0000]\u001d\u0001\u0000"+
		"\u0000\u0000^_\u0003\u0000\u0000\u0000_c\u0006\u000f\uffff\uffff\u0000"+
		"`a\u0003\u0002\u0001\u0000ab\u0006\u000f\uffff\uffff\u0000bd\u0001\u0000"+
		"\u0000\u0000c`\u0001\u0000\u0000\u0000cd\u0001\u0000\u0000\u0000de\u0001"+
		"\u0000\u0000\u0000ef\u0003\u0004\u0002\u0000fg\u0006\u000f\uffff\uffff"+
		"\u0000gh\u0003\u0006\u0003\u0000hl\u0006\u000f\uffff\uffff\u0000ij\u0003"+
		"\b\u0004\u0000jk\u0006\u000f\uffff\uffff\u0000km\u0001\u0000\u0000\u0000"+
		"li\u0001\u0000\u0000\u0000lm\u0001\u0000\u0000\u0000mn\u0001\u0000\u0000"+
		"\u0000no\u0003\n\u0005\u0000os\u0006\u000f\uffff\uffff\u0000pq\u0003\f"+
		"\u0006\u0000qr\u0006\u000f\uffff\uffff\u0000rt\u0001\u0000\u0000\u0000"+
		"sp\u0001\u0000\u0000\u0000st\u0001\u0000\u0000\u0000tx\u0001\u0000\u0000"+
		"\u0000uv\u0003\u000e\u0007\u0000vw\u0006\u000f\uffff\uffff\u0000wy\u0001"+
		"\u0000\u0000\u0000xu\u0001\u0000\u0000\u0000xy\u0001\u0000\u0000\u0000"+
		"y}\u0001\u0000\u0000\u0000z{\u0003\u0010\b\u0000{|\u0006\u000f\uffff\uffff"+
		"\u0000|~\u0001\u0000\u0000\u0000}z\u0001\u0000\u0000\u0000}~\u0001\u0000"+
		"\u0000\u0000~\u0082\u0001\u0000\u0000\u0000\u007f\u0080\u0003\u0012\t"+
		"\u0000\u0080\u0081\u0006\u000f\uffff\uffff\u0000\u0081\u0083\u0001\u0000"+
		"\u0000\u0000\u0082\u007f\u0001\u0000\u0000\u0000\u0082\u0083\u0001\u0000"+
		"\u0000\u0000\u0083\u0087\u0001\u0000\u0000\u0000\u0084\u0085\u0003\u0014"+
		"\n\u0000\u0085\u0086\u0006\u000f\uffff\uffff\u0000\u0086\u0088\u0001\u0000"+
		"\u0000\u0000\u0087\u0084\u0001\u0000\u0000\u0000\u0087\u0088\u0001\u0000"+
		"\u0000\u0000\u0088\u008c\u0001\u0000\u0000\u0000\u0089\u008a\u0003\u0016"+
		"\u000b\u0000\u008a\u008b\u0006\u000f\uffff\uffff\u0000\u008b\u008d\u0001"+
		"\u0000\u0000\u0000\u008c\u0089\u0001\u0000\u0000\u0000\u008c\u008d\u0001"+
		"\u0000\u0000\u0000\u008d\u008e\u0001\u0000\u0000\u0000\u008e\u008f\u0003"+
		"\u0018\f\u0000\u008f\u0093\u0006\u000f\uffff\uffff\u0000\u0090\u0091\u0003"+
		"\u001a\r\u0000\u0091\u0092\u0006\u000f\uffff\uffff\u0000\u0092\u0094\u0001"+
		"\u0000\u0000\u0000\u0093\u0090\u0001\u0000\u0000\u0000\u0093\u0094\u0001"+
		"\u0000\u0000\u0000\u0094\u0095\u0001\u0000\u0000\u0000\u0095\u0096\u0003"+
		"\u001c\u000e\u0000\u0096\u0097\u0006\u000f\uffff\uffff\u0000\u0097\u001f"+
		"\u0001\u0000\u0000\u0000\u0098\u0099\u0003\u001e\u000f\u0000\u0099\u009a"+
		"\u0006\u0010\uffff\uffff\u0000\u009a\u009c\u0001\u0000\u0000\u0000\u009b"+
		"\u0098\u0001\u0000\u0000\u0000\u009c\u009d\u0001\u0000\u0000\u0000\u009d"+
		"\u009b\u0001\u0000\u0000\u0000\u009d\u009e\u0001\u0000\u0000\u0000\u009e"+
		"\u009f\u0001\u0000\u0000\u0000\u009f\u00a0\u0005\u0000\u0000\u0001\u00a0"+
		"!\u0001\u0000\u0000\u0000\nclsx}\u0082\u0087\u008c\u0093\u009d";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}