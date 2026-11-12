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

// Generated from EffectBlock.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.imports.effectblock;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class EffectBlock extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		EFFECT=1, EFFECT_MESSAGE=2, DICE=3, TIME=4, EFFECT_YX=5, EXPR=6, COLON=7, 
		EFFECT_INTEGER=8, SIMPLE_DICE_STRING=9, COMPLEX_DICE_STRING=10, COMMENT=11, 
		INDENT=12, EOL=13, YX_INTEGER=14, YX_COLON=15, YX_EOL=16, TYPE=17, TYPE_MODE_COLON=18, 
		TYPE_EOL=19, SUBTYPE=20, SUBTYPETYPE_MODE_COLON=21, SUBTYPE_EOL=22, R_P_EFFECT_INTEGER=23, 
		R_P_COLON=24, R_P_EOL=25, FREE_TEXT=26, DICE_SIMPLE_VALUE=27, DICE_COMPLEX_VALUE=28, 
		EXPR_CHAR=29, EXPR_COLON=30, EXPR_UCASE=31, EXPR_OP=32, EXPR_EOL=33;
	public static final int
		RULE_effect = 0, RULE_effectYX = 1, RULE_dice = 2, RULE_expr = 3, RULE_effectMsg = 4, 
		RULE_time = 5, RULE_strayEffectLine = 6, RULE_effectBlock = 7;
	private static String[] makeRuleNames() {
		return new String[] {
			"effect", "effectYX", "dice", "expr", "effectMsg", "time", "strayEffectLine", 
			"effectBlock"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'effect:'", "'effect-msg:'", "'dice:'", "'time:'", "'effect-yx:'", 
			"'expr:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "EFFECT", "EFFECT_MESSAGE", "DICE", "TIME", "EFFECT_YX", "EXPR", 
			"COLON", "EFFECT_INTEGER", "SIMPLE_DICE_STRING", "COMPLEX_DICE_STRING", 
			"COMMENT", "INDENT", "EOL", "YX_INTEGER", "YX_COLON", "YX_EOL", "TYPE", 
			"TYPE_MODE_COLON", "TYPE_EOL", "SUBTYPE", "SUBTYPETYPE_MODE_COLON", "SUBTYPE_EOL", 
			"R_P_EFFECT_INTEGER", "R_P_COLON", "R_P_EOL", "FREE_TEXT", "DICE_SIMPLE_VALUE", 
			"DICE_COMPLEX_VALUE", "EXPR_CHAR", "EXPR_COLON", "EXPR_UCASE", "EXPR_OP", 
			"EXPR_EOL"
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
	public String getGrammarFileName() { return "EffectBlock.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public EffectBlock(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class EffectContext extends ParserRuleContext {
		public String type;
		public String wrapper;
		public String radius;
		public String other;
		public Token t;
		public Token st;
		public Token rad;
		public Token oth;
		public TerminalNode EFFECT() { return getToken(EffectBlock.EFFECT, 0); }
		public TerminalNode TYPE() { return getToken(EffectBlock.TYPE, 0); }
		public TerminalNode SUBTYPE() { return getToken(EffectBlock.SUBTYPE, 0); }
		public List<TerminalNode> R_P_EFFECT_INTEGER() { return getTokens(EffectBlock.R_P_EFFECT_INTEGER); }
		public TerminalNode R_P_EFFECT_INTEGER(int i) {
			return getToken(EffectBlock.R_P_EFFECT_INTEGER, i);
		}
		public EffectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effect; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EffectBlockListener ) ((EffectBlockListener)listener).enterEffect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EffectBlockListener ) ((EffectBlockListener)listener).exitEffect(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EffectBlockVisitor ) return ((EffectBlockVisitor<? extends T>)visitor).visitEffect(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectContext effect() throws RecognitionException {
		EffectContext _localctx = new EffectContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_effect);

		            ((EffectContext)_localctx).wrapper =  "";
		            ((EffectContext)_localctx).radius =  "";
		            ((EffectContext)_localctx).other =  "";
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(16);
			match(EFFECT);
			setState(17);
			((EffectContext)_localctx).t = match(TYPE);

			                ((EffectContext)_localctx).type =  ((EffectContext)_localctx).t.getText();
			            
			setState(29);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SUBTYPE) {
				{
				setState(19);
				((EffectContext)_localctx).st = match(SUBTYPE);

				                ((EffectContext)_localctx).wrapper =  ((EffectContext)_localctx).st.getText();
				            
				setState(27);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==R_P_EFFECT_INTEGER) {
					{
					setState(21);
					((EffectContext)_localctx).rad = match(R_P_EFFECT_INTEGER);

					                ((EffectContext)_localctx).radius =  ((EffectContext)_localctx).rad.getText();
					            
					setState(25);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==R_P_EFFECT_INTEGER) {
						{
						setState(23);
						((EffectContext)_localctx).oth = match(R_P_EFFECT_INTEGER);

						                ((EffectContext)_localctx).other =  ((EffectContext)_localctx).oth.getText();
						            
						}
					}

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
	public static class EffectYXContext extends ParserRuleContext {
		public String y;
		public String x;
		public Token yVal;
		public Token xVal;
		public TerminalNode EFFECT_YX() { return getToken(EffectBlock.EFFECT_YX, 0); }
		public List<TerminalNode> YX_INTEGER() { return getTokens(EffectBlock.YX_INTEGER); }
		public TerminalNode YX_INTEGER(int i) {
			return getToken(EffectBlock.YX_INTEGER, i);
		}
		public EffectYXContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effectYX; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EffectBlockListener ) ((EffectBlockListener)listener).enterEffectYX(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EffectBlockListener ) ((EffectBlockListener)listener).exitEffectYX(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EffectBlockVisitor ) return ((EffectBlockVisitor<? extends T>)visitor).visitEffectYX(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectYXContext effectYX() throws RecognitionException {
		EffectYXContext _localctx = new EffectYXContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_effectYX);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(31);
			match(EFFECT_YX);
			setState(32);
			((EffectYXContext)_localctx).yVal = match(YX_INTEGER);
			setState(33);
			((EffectYXContext)_localctx).xVal = match(YX_INTEGER);

			                ((EffectYXContext)_localctx).y =  ((EffectYXContext)_localctx).yVal.getText();
			                ((EffectYXContext)_localctx).x =  ((EffectYXContext)_localctx).xVal.getText();
			            
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
	public static class DiceContext extends ParserRuleContext {
		public String diceString;
		public String exprChar;
		public String baseName;
		public String operation;
		public Token val;
		public ExprContext expr;
		public TerminalNode DICE() { return getToken(EffectBlock.DICE, 0); }
		public TerminalNode DICE_SIMPLE_VALUE() { return getToken(EffectBlock.DICE_SIMPLE_VALUE, 0); }
		public TerminalNode DICE_COMPLEX_VALUE() { return getToken(EffectBlock.DICE_COMPLEX_VALUE, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public DiceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dice; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EffectBlockListener ) ((EffectBlockListener)listener).enterDice(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EffectBlockListener ) ((EffectBlockListener)listener).exitDice(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EffectBlockVisitor ) return ((EffectBlockVisitor<? extends T>)visitor).visitDice(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DiceContext dice() throws RecognitionException {
		DiceContext _localctx = new DiceContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_dice);

		            String charHolder = "";
		            String baseHolder = "";
		            String operHolder = "";
		            ((DiceContext)_localctx).diceString =  "";
		            ((DiceContext)_localctx).exprChar =  "";
		            ((DiceContext)_localctx).baseName =  "";
		            ((DiceContext)_localctx).operation =  "";
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(36);
			match(DICE);
			setState(49);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DICE_COMPLEX_VALUE:
				{
				{
				setState(37);
				((DiceContext)_localctx).val = match(DICE_COMPLEX_VALUE);

				                ((DiceContext)_localctx).diceString =  ((DiceContext)_localctx).val.getText();
				            
				setState(44);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==EXPR) {
					{
					{
					setState(39);
					((DiceContext)_localctx).expr = expr();

					                if (charHolder.isEmpty()) {
					                    charHolder = ((DiceContext)_localctx).expr.exprChar;
					                    baseHolder = ((DiceContext)_localctx).expr.baseName;
					                    operHolder = ((DiceContext)_localctx).expr.operation;
					                } else {
					                    charHolder = charHolder + "^" + ((DiceContext)_localctx).expr.exprChar;
					                    baseHolder = baseHolder + "^" + ((DiceContext)_localctx).expr.baseName;
					                    operHolder = operHolder + "^" + ((DiceContext)_localctx).expr.operation;
					                }
					            
					}
					}
					setState(46);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				}
				break;
			case DICE_SIMPLE_VALUE:
				{
				setState(47);
				((DiceContext)_localctx).val = match(DICE_SIMPLE_VALUE);

				                ((DiceContext)_localctx).diceString =  ((DiceContext)_localctx).val.getText();
				            
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
			_ctx.stop = _input.LT(-1);

			            ((DiceContext)_localctx).exprChar =  charHolder;
			            ((DiceContext)_localctx).baseName =  baseHolder;
			            ((DiceContext)_localctx).operation =  operHolder;
			        
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
	public static class ExprContext extends ParserRuleContext {
		public String exprChar;
		public String baseName;
		public String operation;
		public Token ch;
		public Token base;
		public Token op;
		public TerminalNode EXPR() { return getToken(EffectBlock.EXPR, 0); }
		public List<TerminalNode> EXPR_COLON() { return getTokens(EffectBlock.EXPR_COLON); }
		public TerminalNode EXPR_COLON(int i) {
			return getToken(EffectBlock.EXPR_COLON, i);
		}
		public TerminalNode EXPR_CHAR() { return getToken(EffectBlock.EXPR_CHAR, 0); }
		public TerminalNode EXPR_UCASE() { return getToken(EffectBlock.EXPR_UCASE, 0); }
		public TerminalNode EXPR_OP() { return getToken(EffectBlock.EXPR_OP, 0); }
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EffectBlockListener ) ((EffectBlockListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EffectBlockListener ) ((EffectBlockListener)listener).exitExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EffectBlockVisitor ) return ((EffectBlockVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(51);
			match(EXPR);
			setState(52);
			((ExprContext)_localctx).ch = match(EXPR_CHAR);
			setState(53);
			match(EXPR_COLON);
			setState(54);
			((ExprContext)_localctx).base = match(EXPR_UCASE);
			setState(55);
			match(EXPR_COLON);
			setState(56);
			((ExprContext)_localctx).op = match(EXPR_OP);

			                ((ExprContext)_localctx).exprChar =  ((ExprContext)_localctx).ch.getText();
			                ((ExprContext)_localctx).baseName =  ((ExprContext)_localctx).base.getText();
			                ((ExprContext)_localctx).operation =  ((ExprContext)_localctx).op.getText();
			            
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
	public static class EffectMsgContext extends ParserRuleContext {
		public String message;
		public Token FREE_TEXT;
		public TerminalNode EFFECT_MESSAGE() { return getToken(EffectBlock.EFFECT_MESSAGE, 0); }
		public TerminalNode FREE_TEXT() { return getToken(EffectBlock.FREE_TEXT, 0); }
		public EffectMsgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effectMsg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EffectBlockListener ) ((EffectBlockListener)listener).enterEffectMsg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EffectBlockListener ) ((EffectBlockListener)listener).exitEffectMsg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EffectBlockVisitor ) return ((EffectBlockVisitor<? extends T>)visitor).visitEffectMsg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectMsgContext effectMsg() throws RecognitionException {
		EffectMsgContext _localctx = new EffectMsgContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_effectMsg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(59);
			match(EFFECT_MESSAGE);
			setState(60);
			((EffectMsgContext)_localctx).FREE_TEXT = match(FREE_TEXT);
			 ((EffectMsgContext)_localctx).message =  ((EffectMsgContext)_localctx).FREE_TEXT.getText(); 
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
	public static class TimeContext extends ParserRuleContext {
		public String timeStr;
		public Token DICE_SIMPLE_VALUE;
		public TerminalNode TIME() { return getToken(EffectBlock.TIME, 0); }
		public TerminalNode DICE_SIMPLE_VALUE() { return getToken(EffectBlock.DICE_SIMPLE_VALUE, 0); }
		public TimeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_time; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EffectBlockListener ) ((EffectBlockListener)listener).enterTime(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EffectBlockListener ) ((EffectBlockListener)listener).exitTime(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EffectBlockVisitor ) return ((EffectBlockVisitor<? extends T>)visitor).visitTime(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TimeContext time() throws RecognitionException {
		TimeContext _localctx = new TimeContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_time);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			match(TIME);
			setState(64);
			((TimeContext)_localctx).DICE_SIMPLE_VALUE = match(DICE_SIMPLE_VALUE);
			 ((TimeContext)_localctx).timeStr =  ((TimeContext)_localctx).DICE_SIMPLE_VALUE.getText(); 
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
	public static class StrayEffectLineContext extends ParserRuleContext {
		public EffectYXContext effectYX() {
			return getRuleContext(EffectYXContext.class,0);
		}
		public DiceContext dice() {
			return getRuleContext(DiceContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public StrayEffectLineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_strayEffectLine; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EffectBlockListener ) ((EffectBlockListener)listener).enterStrayEffectLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EffectBlockListener ) ((EffectBlockListener)listener).exitStrayEffectLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EffectBlockVisitor ) return ((EffectBlockVisitor<? extends T>)visitor).visitStrayEffectLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StrayEffectLineContext strayEffectLine() throws RecognitionException {
		StrayEffectLineContext _localctx = new StrayEffectLineContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_strayEffectLine);
		try {
			setState(70);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EFFECT_YX:
				enterOuterAlt(_localctx, 1);
				{
				setState(67);
				effectYX();
				}
				break;
			case DICE:
				enterOuterAlt(_localctx, 2);
				{
				setState(68);
				dice();
				}
				break;
			case EXPR:
				enterOuterAlt(_localctx, 3);
				{
				setState(69);
				expr();
				}
				break;
			default:
				throw new NoViableAltException(this);
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
	public static class EffectBlockContext extends ParserRuleContext {
		public String typeInit;
		public String subtypeWrapperInit;
		public String radius;
		public String other;
		public String diceString;
		public String yVal;
		public String xVal;
		public String expressionChars;
		public String expressionBase;
		public String expressionOperation;
		public int lineNo;
		public EffectContext effect;
		public EffectYXContext effectYX;
		public DiceContext dice;
		public EffectContext effect() {
			return getRuleContext(EffectContext.class,0);
		}
		public DiceContext dice() {
			return getRuleContext(DiceContext.class,0);
		}
		public EffectYXContext effectYX() {
			return getRuleContext(EffectYXContext.class,0);
		}
		public EffectBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effectBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EffectBlockListener ) ((EffectBlockListener)listener).enterEffectBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EffectBlockListener ) ((EffectBlockListener)listener).exitEffectBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EffectBlockVisitor ) return ((EffectBlockVisitor<? extends T>)visitor).visitEffectBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectBlockContext effectBlock() throws RecognitionException {
		EffectBlockContext _localctx = new EffectBlockContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_effectBlock);

		            String expressionString = "";
		            String baseString = "";
		            String opString = "";
		            ((EffectBlockContext)_localctx).diceString =  "";
		            ((EffectBlockContext)_localctx).yVal =  "";
		            ((EffectBlockContext)_localctx).xVal =  "";
		        
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			((EffectBlockContext)_localctx).effect = effect();

			                ((EffectBlockContext)_localctx).lineNo =  _localctx.start.getLine();
			                ((EffectBlockContext)_localctx).typeInit =  ((EffectBlockContext)_localctx).effect.type;
			                ((EffectBlockContext)_localctx).subtypeWrapperInit =  ((EffectBlockContext)_localctx).effect.wrapper;
			                ((EffectBlockContext)_localctx).radius =  ((EffectBlockContext)_localctx).effect.radius;
			                ((EffectBlockContext)_localctx).other =  ((EffectBlockContext)_localctx).effect.other;
			            
			setState(90);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				{
				{
				setState(74);
				((EffectBlockContext)_localctx).effectYX = effectYX();

				                ((EffectBlockContext)_localctx).yVal =  ((EffectBlockContext)_localctx).effectYX.y;
				                ((EffectBlockContext)_localctx).xVal =  ((EffectBlockContext)_localctx).effectYX.x;
				            
				setState(76);
				((EffectBlockContext)_localctx).dice = dice();

				                ((EffectBlockContext)_localctx).diceString =  ((EffectBlockContext)_localctx).dice.diceString;
				                expressionString = ((EffectBlockContext)_localctx).dice.exprChar;
				                baseString = ((EffectBlockContext)_localctx).dice.baseName;
				                opString = ((EffectBlockContext)_localctx).dice.operation;
				            
				}
				}
				break;
			case 2:
				{
				{
				setState(79);
				((EffectBlockContext)_localctx).dice = dice();

				                ((EffectBlockContext)_localctx).diceString =  ((EffectBlockContext)_localctx).dice.diceString;
				                expressionString = ((EffectBlockContext)_localctx).dice.exprChar;
				                baseString = ((EffectBlockContext)_localctx).dice.baseName;
				                opString = ((EffectBlockContext)_localctx).dice.operation;
				            
				setState(81);
				((EffectBlockContext)_localctx).effectYX = effectYX();

				                ((EffectBlockContext)_localctx).yVal =  ((EffectBlockContext)_localctx).effectYX.y;
				                ((EffectBlockContext)_localctx).xVal =  ((EffectBlockContext)_localctx).effectYX.x;
				            
				}
				}
				break;
			case 3:
				{
				setState(84);
				((EffectBlockContext)_localctx).dice = dice();

				                ((EffectBlockContext)_localctx).diceString =  ((EffectBlockContext)_localctx).dice.diceString;
				                expressionString = ((EffectBlockContext)_localctx).dice.exprChar;
				                baseString = ((EffectBlockContext)_localctx).dice.baseName;
				                opString = ((EffectBlockContext)_localctx).dice.operation;
				            
				}
				break;
			case 4:
				{
				setState(87);
				((EffectBlockContext)_localctx).effectYX = effectYX();

				                ((EffectBlockContext)_localctx).yVal =  ((EffectBlockContext)_localctx).effectYX.y;
				                ((EffectBlockContext)_localctx).xVal =  ((EffectBlockContext)_localctx).effectYX.x;
				            
				}
				break;
			}
			}
			_ctx.stop = _input.LT(-1);

			            ((EffectBlockContext)_localctx).expressionChars =  expressionString;
			            ((EffectBlockContext)_localctx).expressionBase =  baseString;
			            ((EffectBlockContext)_localctx).expressionOperation =  opString;
			        
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
		"\u0004\u0001!]\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002"+
		"\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005"+
		"\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0003\u0000\u001a\b\u0000\u0003\u0000\u001c\b"+
		"\u0000\u0003\u0000\u001e\b\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0005\u0002+\b\u0002\n\u0002\f\u0002.\t\u0002\u0001"+
		"\u0002\u0001\u0002\u0003\u00022\b\u0002\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006G\b"+
		"\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0003\u0007[\b\u0007\u0001\u0007\u0000\u0000\b\u0000\u0002\u0004"+
		"\u0006\b\n\f\u000e\u0000\u0000_\u0000\u0010\u0001\u0000\u0000\u0000\u0002"+
		"\u001f\u0001\u0000\u0000\u0000\u0004$\u0001\u0000\u0000\u0000\u00063\u0001"+
		"\u0000\u0000\u0000\b;\u0001\u0000\u0000\u0000\n?\u0001\u0000\u0000\u0000"+
		"\fF\u0001\u0000\u0000\u0000\u000eH\u0001\u0000\u0000\u0000\u0010\u0011"+
		"\u0005\u0001\u0000\u0000\u0011\u0012\u0005\u0011\u0000\u0000\u0012\u001d"+
		"\u0006\u0000\uffff\uffff\u0000\u0013\u0014\u0005\u0014\u0000\u0000\u0014"+
		"\u001b\u0006\u0000\uffff\uffff\u0000\u0015\u0016\u0005\u0017\u0000\u0000"+
		"\u0016\u0019\u0006\u0000\uffff\uffff\u0000\u0017\u0018\u0005\u0017\u0000"+
		"\u0000\u0018\u001a\u0006\u0000\uffff\uffff\u0000\u0019\u0017\u0001\u0000"+
		"\u0000\u0000\u0019\u001a\u0001\u0000\u0000\u0000\u001a\u001c\u0001\u0000"+
		"\u0000\u0000\u001b\u0015\u0001\u0000\u0000\u0000\u001b\u001c\u0001\u0000"+
		"\u0000\u0000\u001c\u001e\u0001\u0000\u0000\u0000\u001d\u0013\u0001\u0000"+
		"\u0000\u0000\u001d\u001e\u0001\u0000\u0000\u0000\u001e\u0001\u0001\u0000"+
		"\u0000\u0000\u001f \u0005\u0005\u0000\u0000 !\u0005\u000e\u0000\u0000"+
		"!\"\u0005\u000e\u0000\u0000\"#\u0006\u0001\uffff\uffff\u0000#\u0003\u0001"+
		"\u0000\u0000\u0000$1\u0005\u0003\u0000\u0000%&\u0005\u001c\u0000\u0000"+
		"&,\u0006\u0002\uffff\uffff\u0000\'(\u0003\u0006\u0003\u0000()\u0006\u0002"+
		"\uffff\uffff\u0000)+\u0001\u0000\u0000\u0000*\'\u0001\u0000\u0000\u0000"+
		"+.\u0001\u0000\u0000\u0000,*\u0001\u0000\u0000\u0000,-\u0001\u0000\u0000"+
		"\u0000-2\u0001\u0000\u0000\u0000.,\u0001\u0000\u0000\u0000/0\u0005\u001b"+
		"\u0000\u000002\u0006\u0002\uffff\uffff\u00001%\u0001\u0000\u0000\u0000"+
		"1/\u0001\u0000\u0000\u00002\u0005\u0001\u0000\u0000\u000034\u0005\u0006"+
		"\u0000\u000045\u0005\u001d\u0000\u000056\u0005\u001e\u0000\u000067\u0005"+
		"\u001f\u0000\u000078\u0005\u001e\u0000\u000089\u0005 \u0000\u00009:\u0006"+
		"\u0003\uffff\uffff\u0000:\u0007\u0001\u0000\u0000\u0000;<\u0005\u0002"+
		"\u0000\u0000<=\u0005\u001a\u0000\u0000=>\u0006\u0004\uffff\uffff\u0000"+
		">\t\u0001\u0000\u0000\u0000?@\u0005\u0004\u0000\u0000@A\u0005\u001b\u0000"+
		"\u0000AB\u0006\u0005\uffff\uffff\u0000B\u000b\u0001\u0000\u0000\u0000"+
		"CG\u0003\u0002\u0001\u0000DG\u0003\u0004\u0002\u0000EG\u0003\u0006\u0003"+
		"\u0000FC\u0001\u0000\u0000\u0000FD\u0001\u0000\u0000\u0000FE\u0001\u0000"+
		"\u0000\u0000G\r\u0001\u0000\u0000\u0000HI\u0003\u0000\u0000\u0000IZ\u0006"+
		"\u0007\uffff\uffff\u0000JK\u0003\u0002\u0001\u0000KL\u0006\u0007\uffff"+
		"\uffff\u0000LM\u0003\u0004\u0002\u0000MN\u0006\u0007\uffff\uffff\u0000"+
		"N[\u0001\u0000\u0000\u0000OP\u0003\u0004\u0002\u0000PQ\u0006\u0007\uffff"+
		"\uffff\u0000QR\u0003\u0002\u0001\u0000RS\u0006\u0007\uffff\uffff\u0000"+
		"S[\u0001\u0000\u0000\u0000TU\u0003\u0004\u0002\u0000UV\u0006\u0007\uffff"+
		"\uffff\u0000V[\u0001\u0000\u0000\u0000WX\u0003\u0002\u0001\u0000XY\u0006"+
		"\u0007\uffff\uffff\u0000Y[\u0001\u0000\u0000\u0000ZJ\u0001\u0000\u0000"+
		"\u0000ZO\u0001\u0000\u0000\u0000ZT\u0001\u0000\u0000\u0000ZW\u0001\u0000"+
		"\u0000\u0000Z[\u0001\u0000\u0000\u0000[\u000f\u0001\u0000\u0000\u0000"+
		"\u0007\u0019\u001b\u001d,1FZ";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}