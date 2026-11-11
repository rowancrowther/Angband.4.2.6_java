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

// Generated from ActivationsGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.activations;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.backend.parser.activation.ActivationParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.EffectParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ActivationsGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, AIM=2, LEVEL=3, POWER=4, MSG=5, DESC=6, EFFECT=7, EFFECT_MESSAGE=8, 
		DICE=9, TIME=10, EFFECT_YX=11, EXPR=12, COLON=13, UCASE=14, INTEGER=15, 
		SIMPLE_DICE_STRING=16, COMPLEX_DICE_STRING=17, COMMENT=18, INDENT=19, 
		EOL=20, NAME_STRING=21, DESC_STRING=22, FREE_TEXT=23, DICE_SIMPLE_VALUE=24, 
		DICE_COMPLEX_VALUE=25, EXPR_CHAR=26, EXPR_COLON=27, EXPR_UCASE=28, EXPR_OP=29, 
		EXPR_EOL=30;
	public static final int
		RULE_name = 0, RULE_aim = 1, RULE_level = 2, RULE_power = 3, RULE_desc = 4, 
		RULE_msg = 5, RULE_activation = 6, RULE_file = 7, RULE_effect = 8, RULE_effectYX = 9, 
		RULE_dice = 10, RULE_expr = 11, RULE_effectMsg = 12, RULE_time = 13, RULE_strayEffectLine = 14, 
		RULE_effectBlock = 15;
	private static String[] makeRuleNames() {
		return new String[] {
			"name", "aim", "level", "power", "desc", "msg", "activation", "file", 
			"effect", "effectYX", "dice", "expr", "effectMsg", "time", "strayEffectLine", 
			"effectBlock"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'aim:'", "'level:'", "'power:'", "'msg:'", "'desc:'", 
			"'effect:'", "'effect-msg:'", "'dice:'", "'time:'", "'effect-yx:'", "'expr:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "AIM", "LEVEL", "POWER", "MSG", "DESC", "EFFECT", "EFFECT_MESSAGE", 
			"DICE", "TIME", "EFFECT_YX", "EXPR", "COLON", "UCASE", "INTEGER", "SIMPLE_DICE_STRING", 
			"COMPLEX_DICE_STRING", "COMMENT", "INDENT", "EOL", "NAME_STRING", "DESC_STRING", 
			"FREE_TEXT", "DICE_SIMPLE_VALUE", "DICE_COMPLEX_VALUE", "EXPR_CHAR", 
			"EXPR_COLON", "EXPR_UCASE", "EXPR_OP", "EXPR_EOL"
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
	public String getGrammarFileName() { return "ActivationsGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }


	             // EffectParseRecord is a record, so "updating" the last effect means
	             // swapping in a copy with one or more fields changed
	             void replaceLast(List<EffectParseRecord> list, EffectParseRecord updated) {
	                 list.set(list.size() - 1, updated);
	             }
	         
	public ActivationsGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public Token NAME_STRING;
		public TerminalNode NAME() { return getToken(ActivationsGrammar.NAME, 0); }
		public TerminalNode NAME_STRING() { return getToken(ActivationsGrammar.NAME_STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ActivationsGrammarVisitor ) return ((ActivationsGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(32);
			match(NAME);
			setState(33);
			((NameContext)_localctx).NAME_STRING = match(NAME_STRING);
			 ((NameContext)_localctx).nameStr =  ((NameContext)_localctx).NAME_STRING.getText(); 
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
	public static class AimContext extends ParserRuleContext {
		public String aimBool;
		public Token INTEGER;
		public TerminalNode AIM() { return getToken(ActivationsGrammar.AIM, 0); }
		public TerminalNode INTEGER() { return getToken(ActivationsGrammar.INTEGER, 0); }
		public AimContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aim; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).enterAim(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).exitAim(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ActivationsGrammarVisitor ) return ((ActivationsGrammarVisitor<? extends T>)visitor).visitAim(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AimContext aim() throws RecognitionException {
		AimContext _localctx = new AimContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_aim);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(36);
			match(AIM);
			setState(37);
			((AimContext)_localctx).INTEGER = match(INTEGER);
			 ((AimContext)_localctx).aimBool =  ((AimContext)_localctx).INTEGER.getText(); 
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
		public String levelInt;
		public Token INTEGER;
		public TerminalNode LEVEL() { return getToken(ActivationsGrammar.LEVEL, 0); }
		public TerminalNode INTEGER() { return getToken(ActivationsGrammar.INTEGER, 0); }
		public LevelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_level; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).enterLevel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).exitLevel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ActivationsGrammarVisitor ) return ((ActivationsGrammarVisitor<? extends T>)visitor).visitLevel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LevelContext level() throws RecognitionException {
		LevelContext _localctx = new LevelContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_level);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(40);
			match(LEVEL);
			setState(41);
			((LevelContext)_localctx).INTEGER = match(INTEGER);
			 ((LevelContext)_localctx).levelInt =  ((LevelContext)_localctx).INTEGER.getText(); 
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
	public static class PowerContext extends ParserRuleContext {
		public String powerInt;
		public Token INTEGER;
		public TerminalNode POWER() { return getToken(ActivationsGrammar.POWER, 0); }
		public TerminalNode INTEGER() { return getToken(ActivationsGrammar.INTEGER, 0); }
		public PowerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_power; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).enterPower(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).exitPower(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ActivationsGrammarVisitor ) return ((ActivationsGrammarVisitor<? extends T>)visitor).visitPower(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PowerContext power() throws RecognitionException {
		PowerContext _localctx = new PowerContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_power);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			match(POWER);
			setState(45);
			((PowerContext)_localctx).INTEGER = match(INTEGER);
			 ((PowerContext)_localctx).powerInt =  ((PowerContext)_localctx).INTEGER.getText(); 
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
		public Token DESC_STRING;
		public TerminalNode DESC() { return getToken(ActivationsGrammar.DESC, 0); }
		public TerminalNode DESC_STRING() { return getToken(ActivationsGrammar.DESC_STRING, 0); }
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_desc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ActivationsGrammarVisitor ) return ((ActivationsGrammarVisitor<? extends T>)visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_desc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			match(DESC);
			setState(49);
			((DescContext)_localctx).DESC_STRING = match(DESC_STRING);
			 ((DescContext)_localctx).descStr =  ((DescContext)_localctx).DESC_STRING.getText(); 
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
		public String message;
		public Token DESC_STRING;
		public TerminalNode MSG() { return getToken(ActivationsGrammar.MSG, 0); }
		public TerminalNode DESC_STRING() { return getToken(ActivationsGrammar.DESC_STRING, 0); }
		public MsgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_msg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).enterMsg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).exitMsg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ActivationsGrammarVisitor ) return ((ActivationsGrammarVisitor<? extends T>)visitor).visitMsg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MsgContext msg() throws RecognitionException {
		MsgContext _localctx = new MsgContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_msg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			match(MSG);
			setState(53);
			((MsgContext)_localctx).DESC_STRING = match(DESC_STRING);
			 ((MsgContext)_localctx).message =  ((MsgContext)_localctx).DESC_STRING.getText(); 
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
	public static class ActivationContext extends ParserRuleContext {
		public ActivationParseRecord activationRecord;
		public NameContext name;
		public AimContext aim;
		public LevelContext level;
		public PowerContext power;
		public MsgContext msg;
		public EffectBlockContext effectBlock;
		public EffectYXContext effectYX;
		public DiceContext dice;
		public ExprContext expr;
		public DescContext desc;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public List<AimContext> aim() {
			return getRuleContexts(AimContext.class);
		}
		public AimContext aim(int i) {
			return getRuleContext(AimContext.class,i);
		}
		public List<LevelContext> level() {
			return getRuleContexts(LevelContext.class);
		}
		public LevelContext level(int i) {
			return getRuleContext(LevelContext.class,i);
		}
		public List<PowerContext> power() {
			return getRuleContexts(PowerContext.class);
		}
		public PowerContext power(int i) {
			return getRuleContext(PowerContext.class,i);
		}
		public List<MsgContext> msg() {
			return getRuleContexts(MsgContext.class);
		}
		public MsgContext msg(int i) {
			return getRuleContext(MsgContext.class,i);
		}
		public List<EffectBlockContext> effectBlock() {
			return getRuleContexts(EffectBlockContext.class);
		}
		public EffectBlockContext effectBlock(int i) {
			return getRuleContext(EffectBlockContext.class,i);
		}
		public List<EffectYXContext> effectYX() {
			return getRuleContexts(EffectYXContext.class);
		}
		public EffectYXContext effectYX(int i) {
			return getRuleContext(EffectYXContext.class,i);
		}
		public List<DiceContext> dice() {
			return getRuleContexts(DiceContext.class);
		}
		public DiceContext dice(int i) {
			return getRuleContext(DiceContext.class,i);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<DescContext> desc() {
			return getRuleContexts(DescContext.class);
		}
		public DescContext desc(int i) {
			return getRuleContext(DescContext.class,i);
		}
		public ActivationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_activation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).enterActivation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).exitActivation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ActivationsGrammarVisitor ) return ((ActivationsGrammarVisitor<? extends T>)visitor).visitActivation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActivationContext activation() throws RecognitionException {
		ActivationContext _localctx = new ActivationContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_activation);

		            String nameInit = "";
		            String aimInit = "";
		            String levelInit = "";
		            String powerInit = "";
		            String messageInit = null;
		            String descInit = null;
		            List<EffectParseRecord> effects = new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			((ActivationContext)_localctx).name = name();

			                nameInit = ((ActivationContext)_localctx).name.nameStr;
			            
			setState(85); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(85);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case AIM:
					{
					setState(58);
					((ActivationContext)_localctx).aim = aim();
					 aimInit = ((ActivationContext)_localctx).aim.aimBool; 
					}
					break;
				case LEVEL:
					{
					setState(61);
					((ActivationContext)_localctx).level = level();
					 levelInit = ((ActivationContext)_localctx).level.levelInt; 
					}
					break;
				case POWER:
					{
					setState(64);
					((ActivationContext)_localctx).power = power();
					 powerInit = ((ActivationContext)_localctx).power.powerInt; 
					}
					break;
				case MSG:
					{
					setState(67);
					((ActivationContext)_localctx).msg = msg();
					 if (messageInit == null)
					                    messageInit = ((ActivationContext)_localctx).msg.message; 
					                else
					                    messageInit = messageInit + ((ActivationContext)_localctx).msg.message; 
					}
					break;
				case EFFECT:
					{
					setState(70);
					((ActivationContext)_localctx).effectBlock = effectBlock();
					 effects.add(new EffectParseRecord(((ActivationContext)_localctx).effectBlock.typeInit,
					                ((ActivationContext)_localctx).effectBlock.subtypeWrapperInit, ((ActivationContext)_localctx).effectBlock.radius, ((ActivationContext)_localctx).effectBlock.other,
					                ((ActivationContext)_localctx).effectBlock.diceString, ((ActivationContext)_localctx).effectBlock.yVal, ((ActivationContext)_localctx).effectBlock.xVal,
					                ((ActivationContext)_localctx).effectBlock.expressionChars, ((ActivationContext)_localctx).effectBlock.expressionBase,
					                ((ActivationContext)_localctx).effectBlock.expressionOperation,
					                ((ActivationContext)_localctx).effectBlock.effectMessage, (((ActivationContext)_localctx).effectBlock!=null?(((ActivationContext)_localctx).effectBlock.start):null).getLine())); 
					}
					break;
				case EFFECT_YX:
					{
					setState(73);
					((ActivationContext)_localctx).effectYX = effectYX();

					                if (!effects.isEmpty()) {
					                    EffectParseRecord last = effects.getLast();
					                    EffectParseRecord updated = last.amendYX(((ActivationContext)_localctx).effectYX.y, ((ActivationContext)_localctx).effectYX.x);
					                    replaceLast(effects, updated);
					                }
					            
					}
					break;
				case DICE:
					{
					setState(76);
					((ActivationContext)_localctx).dice = dice();

					                if (!effects.isEmpty()) {
					                    EffectParseRecord last = effects.getLast();
					                    String diceStr = ((ActivationContext)_localctx).dice.diceString;
					                    EffectParseRecord updated = last.amendExpression(diceStr,
					                                                                     ((ActivationContext)_localctx).dice.exprChar,
					                                                                     ((ActivationContext)_localctx).dice.baseName,
					                                                                     ((ActivationContext)_localctx).dice.operation);
					                    replaceLast(effects, updated);
					                }
					            
					}
					break;
				case EXPR:
					{
					setState(79);
					((ActivationContext)_localctx).expr = expr();

					                if (!effects.isEmpty() && !effects.getLast().diceString().isEmpty()) {
					                    EffectParseRecord last = effects.getLast();
					                    String lastDiceStr  = last.diceString();
					                    String lastExprChar = last.expressionChars().isEmpty() 
					                                        ? ((ActivationContext)_localctx).expr.exprChar
					                                        : last.expressionChars() + "^" + ((ActivationContext)_localctx).expr.exprChar;
					                    String lastExprBase = last.expressionBases().isEmpty() 
					                                        ? ((ActivationContext)_localctx).expr.baseName
					                                        : last.expressionBases() + "^" + ((ActivationContext)_localctx).expr.baseName;
					                    String lastExprOp   = last.expressionOperations().isEmpty() 
					                                        ? ((ActivationContext)_localctx).expr.operation
					                                        : last.expressionOperations() + "^" + ((ActivationContext)_localctx).expr.operation;
					                    EffectParseRecord updated = last.amendExpression(lastDiceStr,
					                                                lastExprChar,
					                                                lastExprBase,
					                                                lastExprOp);
					                    replaceLast(effects, updated);
					                }
					            
					}
					break;
				case DESC:
					{
					setState(82);
					((ActivationContext)_localctx).desc = desc();
					 if (descInit == null) 
					                    descInit = ((ActivationContext)_localctx).desc.descStr;
					                else
					                    descInit = descInit + ((ActivationContext)_localctx).desc.descStr; 
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(87); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 6908L) != 0) );
			}
			_ctx.stop = _input.LT(-1);

			            ((ActivationContext)_localctx).activationRecord = 
			                new ActivationParseRecord(nameInit,
			                    aimInit, levelInit, powerInit,
			                    messageInit, descInit, effects,
			                    _localctx.start.getLine());
			        
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
		public List<ActivationParseRecord> records;
		public ActivationContext activation;
		public TerminalNode EOF() { return getToken(ActivationsGrammar.EOF, 0); }
		public List<ActivationContext> activation() {
			return getRuleContexts(ActivationContext.class);
		}
		public ActivationContext activation(int i) {
			return getRuleContext(ActivationContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ActivationsGrammarVisitor ) return ((ActivationsGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_file);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{

			                ((FileContext)_localctx).records =  new ArrayList<>();
			            
			setState(93); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(90);
				((FileContext)_localctx).activation = activation();

				                _localctx.records.add(((FileContext)_localctx).activation.activationRecord);
				            
				}
				}
				setState(95); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(97);
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
		public TerminalNode EFFECT() { return getToken(ActivationsGrammar.EFFECT, 0); }
		public List<TerminalNode> UCASE() { return getTokens(ActivationsGrammar.UCASE); }
		public TerminalNode UCASE(int i) {
			return getToken(ActivationsGrammar.UCASE, i);
		}
		public List<TerminalNode> COLON() { return getTokens(ActivationsGrammar.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(ActivationsGrammar.COLON, i);
		}
		public List<TerminalNode> INTEGER() { return getTokens(ActivationsGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(ActivationsGrammar.INTEGER, i);
		}
		public EffectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effect; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).enterEffect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).exitEffect(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ActivationsGrammarVisitor ) return ((ActivationsGrammarVisitor<? extends T>)visitor).visitEffect(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectContext effect() throws RecognitionException {
		EffectContext _localctx = new EffectContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_effect);

		            ((EffectContext)_localctx).wrapper =  "";
		            ((EffectContext)_localctx).radius =  "";
		            ((EffectContext)_localctx).other =  "";
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			match(EFFECT);
			setState(100);
			((EffectContext)_localctx).t = match(UCASE);

			                ((EffectContext)_localctx).type =  ((EffectContext)_localctx).t.getText();
			            
			setState(115);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(102);
				match(COLON);
				setState(103);
				((EffectContext)_localctx).st = match(UCASE);

				                ((EffectContext)_localctx).wrapper =  ((EffectContext)_localctx).st.getText();
				            
				setState(113);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COLON) {
					{
					setState(105);
					match(COLON);
					setState(106);
					((EffectContext)_localctx).rad = match(INTEGER);

					                ((EffectContext)_localctx).radius =  ((EffectContext)_localctx).rad.getText();
					            
					setState(111);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==COLON) {
						{
						setState(108);
						match(COLON);
						setState(109);
						((EffectContext)_localctx).oth = match(INTEGER);

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
		public TerminalNode EFFECT_YX() { return getToken(ActivationsGrammar.EFFECT_YX, 0); }
		public TerminalNode COLON() { return getToken(ActivationsGrammar.COLON, 0); }
		public List<TerminalNode> INTEGER() { return getTokens(ActivationsGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(ActivationsGrammar.INTEGER, i);
		}
		public EffectYXContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effectYX; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).enterEffectYX(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).exitEffectYX(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ActivationsGrammarVisitor ) return ((ActivationsGrammarVisitor<? extends T>)visitor).visitEffectYX(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectYXContext effectYX() throws RecognitionException {
		EffectYXContext _localctx = new EffectYXContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_effectYX);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(117);
			match(EFFECT_YX);
			setState(118);
			((EffectYXContext)_localctx).yVal = match(INTEGER);
			setState(119);
			match(COLON);
			setState(120);
			((EffectYXContext)_localctx).xVal = match(INTEGER);

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
		public TerminalNode DICE() { return getToken(ActivationsGrammar.DICE, 0); }
		public TerminalNode DICE_SIMPLE_VALUE() { return getToken(ActivationsGrammar.DICE_SIMPLE_VALUE, 0); }
		public TerminalNode DICE_COMPLEX_VALUE() { return getToken(ActivationsGrammar.DICE_COMPLEX_VALUE, 0); }
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
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).enterDice(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).exitDice(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ActivationsGrammarVisitor ) return ((ActivationsGrammarVisitor<? extends T>)visitor).visitDice(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DiceContext dice() throws RecognitionException {
		DiceContext _localctx = new DiceContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_dice);

		            String charHolder = "";
		            String baseHolder = "";
		            String operHolder = "";
		            ((DiceContext)_localctx).diceString =  "";
		            ((DiceContext)_localctx).exprChar =  "";
		            ((DiceContext)_localctx).baseName =  "";
		            ((DiceContext)_localctx).operation =  "";
		        
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(123);
			match(DICE);
			setState(136);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DICE_COMPLEX_VALUE:
				{
				{
				setState(124);
				((DiceContext)_localctx).val = match(DICE_COMPLEX_VALUE);

				                ((DiceContext)_localctx).diceString =  ((DiceContext)_localctx).val.getText();
				            
				setState(131);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(126);
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
					}
					setState(133);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
				}
				}
				}
				break;
			case DICE_SIMPLE_VALUE:
				{
				setState(134);
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
		public TerminalNode EXPR() { return getToken(ActivationsGrammar.EXPR, 0); }
		public List<TerminalNode> EXPR_COLON() { return getTokens(ActivationsGrammar.EXPR_COLON); }
		public TerminalNode EXPR_COLON(int i) {
			return getToken(ActivationsGrammar.EXPR_COLON, i);
		}
		public TerminalNode EXPR_CHAR() { return getToken(ActivationsGrammar.EXPR_CHAR, 0); }
		public TerminalNode EXPR_UCASE() { return getToken(ActivationsGrammar.EXPR_UCASE, 0); }
		public TerminalNode EXPR_OP() { return getToken(ActivationsGrammar.EXPR_OP, 0); }
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).exitExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ActivationsGrammarVisitor ) return ((ActivationsGrammarVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(138);
			match(EXPR);
			setState(139);
			((ExprContext)_localctx).ch = match(EXPR_CHAR);
			setState(140);
			match(EXPR_COLON);
			setState(141);
			((ExprContext)_localctx).base = match(EXPR_UCASE);
			setState(142);
			match(EXPR_COLON);
			setState(143);
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
		public TerminalNode EFFECT_MESSAGE() { return getToken(ActivationsGrammar.EFFECT_MESSAGE, 0); }
		public TerminalNode FREE_TEXT() { return getToken(ActivationsGrammar.FREE_TEXT, 0); }
		public EffectMsgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effectMsg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).enterEffectMsg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).exitEffectMsg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ActivationsGrammarVisitor ) return ((ActivationsGrammarVisitor<? extends T>)visitor).visitEffectMsg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectMsgContext effectMsg() throws RecognitionException {
		EffectMsgContext _localctx = new EffectMsgContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_effectMsg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(146);
			match(EFFECT_MESSAGE);
			setState(147);
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
		public TerminalNode TIME() { return getToken(ActivationsGrammar.TIME, 0); }
		public TerminalNode DICE_SIMPLE_VALUE() { return getToken(ActivationsGrammar.DICE_SIMPLE_VALUE, 0); }
		public TimeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_time; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).enterTime(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).exitTime(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ActivationsGrammarVisitor ) return ((ActivationsGrammarVisitor<? extends T>)visitor).visitTime(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TimeContext time() throws RecognitionException {
		TimeContext _localctx = new TimeContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_time);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(150);
			match(TIME);
			setState(151);
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
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).enterStrayEffectLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).exitStrayEffectLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ActivationsGrammarVisitor ) return ((ActivationsGrammarVisitor<? extends T>)visitor).visitStrayEffectLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StrayEffectLineContext strayEffectLine() throws RecognitionException {
		StrayEffectLineContext _localctx = new StrayEffectLineContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_strayEffectLine);
		try {
			setState(157);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EFFECT_YX:
				enterOuterAlt(_localctx, 1);
				{
				setState(154);
				effectYX();
				}
				break;
			case DICE:
				enterOuterAlt(_localctx, 2);
				{
				setState(155);
				dice();
				}
				break;
			case EXPR:
				enterOuterAlt(_localctx, 3);
				{
				setState(156);
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
		public String effectMessage;
		public int lineNo;
		public EffectContext effect;
		public EffectYXContext effectYX;
		public DiceContext dice;
		public EffectMsgContext effectMsg;
		public EffectContext effect() {
			return getRuleContext(EffectContext.class,0);
		}
		public DiceContext dice() {
			return getRuleContext(DiceContext.class,0);
		}
		public EffectYXContext effectYX() {
			return getRuleContext(EffectYXContext.class,0);
		}
		public EffectMsgContext effectMsg() {
			return getRuleContext(EffectMsgContext.class,0);
		}
		public EffectBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effectBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).enterEffectBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ActivationsGrammarListener ) ((ActivationsGrammarListener)listener).exitEffectBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ActivationsGrammarVisitor ) return ((ActivationsGrammarVisitor<? extends T>)visitor).visitEffectBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectBlockContext effectBlock() throws RecognitionException {
		EffectBlockContext _localctx = new EffectBlockContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_effectBlock);

		            String expressionString = "";
		            String baseString = "";
		            String opString = "";
		            ((EffectBlockContext)_localctx).diceString =  "";
		            ((EffectBlockContext)_localctx).yVal =  "";
		            ((EffectBlockContext)_localctx).xVal =  "";
		            ((EffectBlockContext)_localctx).effectMessage =  null;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(159);
			((EffectBlockContext)_localctx).effect = effect();

			                ((EffectBlockContext)_localctx).lineNo =  _localctx.start.getLine();
			                ((EffectBlockContext)_localctx).typeInit =  ((EffectBlockContext)_localctx).effect.type;
			                ((EffectBlockContext)_localctx).subtypeWrapperInit =  ((EffectBlockContext)_localctx).effect.wrapper;
			                ((EffectBlockContext)_localctx).radius =  ((EffectBlockContext)_localctx).effect.radius;
			                ((EffectBlockContext)_localctx).other =  ((EffectBlockContext)_localctx).effect.other;
			            
			setState(177);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				{
				setState(161);
				((EffectBlockContext)_localctx).effectYX = effectYX();

				                ((EffectBlockContext)_localctx).yVal =  ((EffectBlockContext)_localctx).effectYX.y;
				                ((EffectBlockContext)_localctx).xVal =  ((EffectBlockContext)_localctx).effectYX.x;
				            
				setState(163);
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
				setState(166);
				((EffectBlockContext)_localctx).dice = dice();

				                ((EffectBlockContext)_localctx).diceString =  ((EffectBlockContext)_localctx).dice.diceString;
				                expressionString = ((EffectBlockContext)_localctx).dice.exprChar;
				                baseString = ((EffectBlockContext)_localctx).dice.baseName;
				                opString = ((EffectBlockContext)_localctx).dice.operation;
				            
				setState(168);
				((EffectBlockContext)_localctx).effectYX = effectYX();

				                ((EffectBlockContext)_localctx).yVal =  ((EffectBlockContext)_localctx).effectYX.y;
				                ((EffectBlockContext)_localctx).xVal =  ((EffectBlockContext)_localctx).effectYX.x;
				            
				}
				}
				break;
			case 3:
				{
				setState(171);
				((EffectBlockContext)_localctx).dice = dice();

				                ((EffectBlockContext)_localctx).diceString =  ((EffectBlockContext)_localctx).dice.diceString;
				                expressionString = ((EffectBlockContext)_localctx).dice.exprChar;
				                baseString = ((EffectBlockContext)_localctx).dice.baseName;
				                opString = ((EffectBlockContext)_localctx).dice.operation;
				            
				}
				break;
			case 4:
				{
				setState(174);
				((EffectBlockContext)_localctx).effectYX = effectYX();

				                ((EffectBlockContext)_localctx).yVal =  ((EffectBlockContext)_localctx).effectYX.y;
				                ((EffectBlockContext)_localctx).xVal =  ((EffectBlockContext)_localctx).effectYX.x;
				            
				}
				break;
			}
			setState(182);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EFFECT_MESSAGE) {
				{
				setState(179);
				((EffectBlockContext)_localctx).effectMsg = effectMsg();
				 ((EffectBlockContext)_localctx).effectMessage =  ((EffectBlockContext)_localctx).effectMsg.message; 
				}
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
		"\u0004\u0001\u001e\u00b9\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0004"+
		"\u0006V\b\u0006\u000b\u0006\f\u0006W\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0004\u0007^\b\u0007\u000b\u0007\f\u0007_\u0001\u0007\u0001"+
		"\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b"+
		"\u0001\b\u0001\b\u0001\b\u0001\b\u0003\bp\b\b\u0003\br\b\b\u0003\bt\b"+
		"\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0005\n\u0082\b\n\n\n\f\n\u0085\t\n\u0001\n"+
		"\u0001\n\u0003\n\u0089\b\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0003\u000e\u009e\b\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u00b2\b\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0003\u000f\u00b7\b\u000f\u0001\u000f\u0000\u0000\u0010"+
		"\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a"+
		"\u001c\u001e\u0000\u0000\u00be\u0000 \u0001\u0000\u0000\u0000\u0002$\u0001"+
		"\u0000\u0000\u0000\u0004(\u0001\u0000\u0000\u0000\u0006,\u0001\u0000\u0000"+
		"\u0000\b0\u0001\u0000\u0000\u0000\n4\u0001\u0000\u0000\u0000\f8\u0001"+
		"\u0000\u0000\u0000\u000eY\u0001\u0000\u0000\u0000\u0010c\u0001\u0000\u0000"+
		"\u0000\u0012u\u0001\u0000\u0000\u0000\u0014{\u0001\u0000\u0000\u0000\u0016"+
		"\u008a\u0001\u0000\u0000\u0000\u0018\u0092\u0001\u0000\u0000\u0000\u001a"+
		"\u0096\u0001\u0000\u0000\u0000\u001c\u009d\u0001\u0000\u0000\u0000\u001e"+
		"\u009f\u0001\u0000\u0000\u0000 !\u0005\u0001\u0000\u0000!\"\u0005\u0015"+
		"\u0000\u0000\"#\u0006\u0000\uffff\uffff\u0000#\u0001\u0001\u0000\u0000"+
		"\u0000$%\u0005\u0002\u0000\u0000%&\u0005\u000f\u0000\u0000&\'\u0006\u0001"+
		"\uffff\uffff\u0000\'\u0003\u0001\u0000\u0000\u0000()\u0005\u0003\u0000"+
		"\u0000)*\u0005\u000f\u0000\u0000*+\u0006\u0002\uffff\uffff\u0000+\u0005"+
		"\u0001\u0000\u0000\u0000,-\u0005\u0004\u0000\u0000-.\u0005\u000f\u0000"+
		"\u0000./\u0006\u0003\uffff\uffff\u0000/\u0007\u0001\u0000\u0000\u0000"+
		"01\u0005\u0006\u0000\u000012\u0005\u0016\u0000\u000023\u0006\u0004\uffff"+
		"\uffff\u00003\t\u0001\u0000\u0000\u000045\u0005\u0005\u0000\u000056\u0005"+
		"\u0016\u0000\u000067\u0006\u0005\uffff\uffff\u00007\u000b\u0001\u0000"+
		"\u0000\u000089\u0003\u0000\u0000\u00009U\u0006\u0006\uffff\uffff\u0000"+
		":;\u0003\u0002\u0001\u0000;<\u0006\u0006\uffff\uffff\u0000<V\u0001\u0000"+
		"\u0000\u0000=>\u0003\u0004\u0002\u0000>?\u0006\u0006\uffff\uffff\u0000"+
		"?V\u0001\u0000\u0000\u0000@A\u0003\u0006\u0003\u0000AB\u0006\u0006\uffff"+
		"\uffff\u0000BV\u0001\u0000\u0000\u0000CD\u0003\n\u0005\u0000DE\u0006\u0006"+
		"\uffff\uffff\u0000EV\u0001\u0000\u0000\u0000FG\u0003\u001e\u000f\u0000"+
		"GH\u0006\u0006\uffff\uffff\u0000HV\u0001\u0000\u0000\u0000IJ\u0003\u0012"+
		"\t\u0000JK\u0006\u0006\uffff\uffff\u0000KV\u0001\u0000\u0000\u0000LM\u0003"+
		"\u0014\n\u0000MN\u0006\u0006\uffff\uffff\u0000NV\u0001\u0000\u0000\u0000"+
		"OP\u0003\u0016\u000b\u0000PQ\u0006\u0006\uffff\uffff\u0000QV\u0001\u0000"+
		"\u0000\u0000RS\u0003\b\u0004\u0000ST\u0006\u0006\uffff\uffff\u0000TV\u0001"+
		"\u0000\u0000\u0000U:\u0001\u0000\u0000\u0000U=\u0001\u0000\u0000\u0000"+
		"U@\u0001\u0000\u0000\u0000UC\u0001\u0000\u0000\u0000UF\u0001\u0000\u0000"+
		"\u0000UI\u0001\u0000\u0000\u0000UL\u0001\u0000\u0000\u0000UO\u0001\u0000"+
		"\u0000\u0000UR\u0001\u0000\u0000\u0000VW\u0001\u0000\u0000\u0000WU\u0001"+
		"\u0000\u0000\u0000WX\u0001\u0000\u0000\u0000X\r\u0001\u0000\u0000\u0000"+
		"Y]\u0006\u0007\uffff\uffff\u0000Z[\u0003\f\u0006\u0000[\\\u0006\u0007"+
		"\uffff\uffff\u0000\\^\u0001\u0000\u0000\u0000]Z\u0001\u0000\u0000\u0000"+
		"^_\u0001\u0000\u0000\u0000_]\u0001\u0000\u0000\u0000_`\u0001\u0000\u0000"+
		"\u0000`a\u0001\u0000\u0000\u0000ab\u0005\u0000\u0000\u0001b\u000f\u0001"+
		"\u0000\u0000\u0000cd\u0005\u0007\u0000\u0000de\u0005\u000e\u0000\u0000"+
		"es\u0006\b\uffff\uffff\u0000fg\u0005\r\u0000\u0000gh\u0005\u000e\u0000"+
		"\u0000hq\u0006\b\uffff\uffff\u0000ij\u0005\r\u0000\u0000jk\u0005\u000f"+
		"\u0000\u0000ko\u0006\b\uffff\uffff\u0000lm\u0005\r\u0000\u0000mn\u0005"+
		"\u000f\u0000\u0000np\u0006\b\uffff\uffff\u0000ol\u0001\u0000\u0000\u0000"+
		"op\u0001\u0000\u0000\u0000pr\u0001\u0000\u0000\u0000qi\u0001\u0000\u0000"+
		"\u0000qr\u0001\u0000\u0000\u0000rt\u0001\u0000\u0000\u0000sf\u0001\u0000"+
		"\u0000\u0000st\u0001\u0000\u0000\u0000t\u0011\u0001\u0000\u0000\u0000"+
		"uv\u0005\u000b\u0000\u0000vw\u0005\u000f\u0000\u0000wx\u0005\r\u0000\u0000"+
		"xy\u0005\u000f\u0000\u0000yz\u0006\t\uffff\uffff\u0000z\u0013\u0001\u0000"+
		"\u0000\u0000{\u0088\u0005\t\u0000\u0000|}\u0005\u0019\u0000\u0000}\u0083"+
		"\u0006\n\uffff\uffff\u0000~\u007f\u0003\u0016\u000b\u0000\u007f\u0080"+
		"\u0006\n\uffff\uffff\u0000\u0080\u0082\u0001\u0000\u0000\u0000\u0081~"+
		"\u0001\u0000\u0000\u0000\u0082\u0085\u0001\u0000\u0000\u0000\u0083\u0081"+
		"\u0001\u0000\u0000\u0000\u0083\u0084\u0001\u0000\u0000\u0000\u0084\u0089"+
		"\u0001\u0000\u0000\u0000\u0085\u0083\u0001\u0000\u0000\u0000\u0086\u0087"+
		"\u0005\u0018\u0000\u0000\u0087\u0089\u0006\n\uffff\uffff\u0000\u0088|"+
		"\u0001\u0000\u0000\u0000\u0088\u0086\u0001\u0000\u0000\u0000\u0089\u0015"+
		"\u0001\u0000\u0000\u0000\u008a\u008b\u0005\f\u0000\u0000\u008b\u008c\u0005"+
		"\u001a\u0000\u0000\u008c\u008d\u0005\u001b\u0000\u0000\u008d\u008e\u0005"+
		"\u001c\u0000\u0000\u008e\u008f\u0005\u001b\u0000\u0000\u008f\u0090\u0005"+
		"\u001d\u0000\u0000\u0090\u0091\u0006\u000b\uffff\uffff\u0000\u0091\u0017"+
		"\u0001\u0000\u0000\u0000\u0092\u0093\u0005\b\u0000\u0000\u0093\u0094\u0005"+
		"\u0017\u0000\u0000\u0094\u0095\u0006\f\uffff\uffff\u0000\u0095\u0019\u0001"+
		"\u0000\u0000\u0000\u0096\u0097\u0005\n\u0000\u0000\u0097\u0098\u0005\u0018"+
		"\u0000\u0000\u0098\u0099\u0006\r\uffff\uffff\u0000\u0099\u001b\u0001\u0000"+
		"\u0000\u0000\u009a\u009e\u0003\u0012\t\u0000\u009b\u009e\u0003\u0014\n"+
		"\u0000\u009c\u009e\u0003\u0016\u000b\u0000\u009d\u009a\u0001\u0000\u0000"+
		"\u0000\u009d\u009b\u0001\u0000\u0000\u0000\u009d\u009c\u0001\u0000\u0000"+
		"\u0000\u009e\u001d\u0001\u0000\u0000\u0000\u009f\u00a0\u0003\u0010\b\u0000"+
		"\u00a0\u00b1\u0006\u000f\uffff\uffff\u0000\u00a1\u00a2\u0003\u0012\t\u0000"+
		"\u00a2\u00a3\u0006\u000f\uffff\uffff\u0000\u00a3\u00a4\u0003\u0014\n\u0000"+
		"\u00a4\u00a5\u0006\u000f\uffff\uffff\u0000\u00a5\u00b2\u0001\u0000\u0000"+
		"\u0000\u00a6\u00a7\u0003\u0014\n\u0000\u00a7\u00a8\u0006\u000f\uffff\uffff"+
		"\u0000\u00a8\u00a9\u0003\u0012\t\u0000\u00a9\u00aa\u0006\u000f\uffff\uffff"+
		"\u0000\u00aa\u00b2\u0001\u0000\u0000\u0000\u00ab\u00ac\u0003\u0014\n\u0000"+
		"\u00ac\u00ad\u0006\u000f\uffff\uffff\u0000\u00ad\u00b2\u0001\u0000\u0000"+
		"\u0000\u00ae\u00af\u0003\u0012\t\u0000\u00af\u00b0\u0006\u000f\uffff\uffff"+
		"\u0000\u00b0\u00b2\u0001\u0000\u0000\u0000\u00b1\u00a1\u0001\u0000\u0000"+
		"\u0000\u00b1\u00a6\u0001\u0000\u0000\u0000\u00b1\u00ab\u0001\u0000\u0000"+
		"\u0000\u00b1\u00ae\u0001\u0000\u0000\u0000\u00b1\u00b2\u0001\u0000\u0000"+
		"\u0000\u00b2\u00b6\u0001\u0000\u0000\u0000\u00b3\u00b4\u0003\u0018\f\u0000"+
		"\u00b4\u00b5\u0006\u000f\uffff\uffff\u0000\u00b5\u00b7\u0001\u0000\u0000"+
		"\u0000\u00b6\u00b3\u0001\u0000\u0000\u0000\u00b6\u00b7\u0001\u0000\u0000"+
		"\u0000\u00b7\u001f\u0001\u0000\u0000\u0000\u000bUW_oqs\u0083\u0088\u009d"+
		"\u00b1\u00b6";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}