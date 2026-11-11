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

// Generated from ChestTrapGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.chesttrap;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.backend.parser.chesttrap.ChestTrapParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.EffectParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ChestTrapGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, CODE=2, LEVEL=3, DESTROY=4, MAGIC=5, MSG=6, MSG_DEATH=7, COMMENT=8, 
		INDENT=9, EOL=10, EFFECT=11, EFFECT_MESSAGE=12, DICE=13, TIME=14, EFFECT_YX=15, 
		EXPR=16, COLON=17, UCASE=18, INTEGER=19, SIMPLE_DICE_STRING=20, COMPLEX_DICE_STRING=21, 
		STRING=22, ROL_EOL=23, FREE_TEXT=24, DICE_SIMPLE_VALUE=25, DICE_COMPLEX_VALUE=26, 
		EXPR_CHAR=27, EXPR_COLON=28, EXPR_UCASE=29, EXPR_OP=30, EXPR_EOL=31;
	public static final int
		RULE_name = 0, RULE_code = 1, RULE_level = 2, RULE_destroy = 3, RULE_magic = 4, 
		RULE_msg = 5, RULE_msgDeath = 6, RULE_chestTrap = 7, RULE_file = 8, RULE_effect = 9, 
		RULE_effectYX = 10, RULE_dice = 11, RULE_expr = 12, RULE_effectMsg = 13, 
		RULE_time = 14, RULE_strayEffectLine = 15, RULE_effectBlock = 16;
	private static String[] makeRuleNames() {
		return new String[] {
			"name", "code", "level", "destroy", "magic", "msg", "msgDeath", "chestTrap", 
			"file", "effect", "effectYX", "dice", "expr", "effectMsg", "time", "strayEffectLine", 
			"effectBlock"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'code:'", "'level:'", "'destroy:'", "'magic:'", "'msg:'", 
			"'msg-death:'", null, null, null, "'effect:'", "'effect-msg:'", "'dice:'", 
			"'time:'", "'effect-yx:'", "'expr:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "CODE", "LEVEL", "DESTROY", "MAGIC", "MSG", "MSG_DEATH", 
			"COMMENT", "INDENT", "EOL", "EFFECT", "EFFECT_MESSAGE", "DICE", "TIME", 
			"EFFECT_YX", "EXPR", "COLON", "UCASE", "INTEGER", "SIMPLE_DICE_STRING", 
			"COMPLEX_DICE_STRING", "STRING", "ROL_EOL", "FREE_TEXT", "DICE_SIMPLE_VALUE", 
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
	public String getGrammarFileName() { return "ChestTrapGrammar.g4"; }

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
	         
	public ChestTrapGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public int line;
		public Token n;
		public TerminalNode NAME() { return getToken(ChestTrapGrammar.NAME, 0); }
		public TerminalNode STRING() { return getToken(ChestTrapGrammar.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChestTrapGrammarVisitor ) return ((ChestTrapGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(34);
			match(NAME);
			setState(35);
			((NameContext)_localctx).n = match(STRING);
			 
			            ((NameContext)_localctx).nameStr =  ((NameContext)_localctx).n.getText();
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
	public static class CodeContext extends ParserRuleContext {
		public String codeStr;
		public Token c;
		public TerminalNode CODE() { return getToken(ChestTrapGrammar.CODE, 0); }
		public TerminalNode STRING() { return getToken(ChestTrapGrammar.STRING, 0); }
		public CodeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_code; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).enterCode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).exitCode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChestTrapGrammarVisitor ) return ((ChestTrapGrammarVisitor<? extends T>)visitor).visitCode(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CodeContext code() throws RecognitionException {
		CodeContext _localctx = new CodeContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_code);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(38);
			match(CODE);
			setState(39);
			((CodeContext)_localctx).c = match(STRING);
			 ((CodeContext)_localctx).codeStr =  ((CodeContext)_localctx).c.getText(); 
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
		public Token l;
		public TerminalNode LEVEL() { return getToken(ChestTrapGrammar.LEVEL, 0); }
		public TerminalNode INTEGER() { return getToken(ChestTrapGrammar.INTEGER, 0); }
		public LevelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_level; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).enterLevel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).exitLevel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChestTrapGrammarVisitor ) return ((ChestTrapGrammarVisitor<? extends T>)visitor).visitLevel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LevelContext level() throws RecognitionException {
		LevelContext _localctx = new LevelContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_level);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(42);
			match(LEVEL);
			setState(43);
			((LevelContext)_localctx).l = match(INTEGER);
			 ((LevelContext)_localctx).levelStr =  ((LevelContext)_localctx).l.getText(); 
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
	public static class DestroyContext extends ParserRuleContext {
		public String destroyStr;
		public Token d;
		public TerminalNode DESTROY() { return getToken(ChestTrapGrammar.DESTROY, 0); }
		public TerminalNode INTEGER() { return getToken(ChestTrapGrammar.INTEGER, 0); }
		public DestroyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_destroy; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).enterDestroy(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).exitDestroy(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChestTrapGrammarVisitor ) return ((ChestTrapGrammarVisitor<? extends T>)visitor).visitDestroy(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DestroyContext destroy() throws RecognitionException {
		DestroyContext _localctx = new DestroyContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_destroy);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			match(DESTROY);
			setState(47);
			((DestroyContext)_localctx).d = match(INTEGER);
			 ((DestroyContext)_localctx).destroyStr =  ((DestroyContext)_localctx).d.getText(); 
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
	public static class MagicContext extends ParserRuleContext {
		public String magicStr;
		public Token m;
		public TerminalNode MAGIC() { return getToken(ChestTrapGrammar.MAGIC, 0); }
		public TerminalNode INTEGER() { return getToken(ChestTrapGrammar.INTEGER, 0); }
		public MagicContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_magic; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).enterMagic(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).exitMagic(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChestTrapGrammarVisitor ) return ((ChestTrapGrammarVisitor<? extends T>)visitor).visitMagic(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MagicContext magic() throws RecognitionException {
		MagicContext _localctx = new MagicContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_magic);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(50);
			match(MAGIC);
			setState(51);
			((MagicContext)_localctx).m = match(INTEGER);
			 ((MagicContext)_localctx).magicStr =  ((MagicContext)_localctx).m.getText(); 
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
		public Token m;
		public TerminalNode MSG() { return getToken(ChestTrapGrammar.MSG, 0); }
		public TerminalNode STRING() { return getToken(ChestTrapGrammar.STRING, 0); }
		public MsgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_msg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).enterMsg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).exitMsg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChestTrapGrammarVisitor ) return ((ChestTrapGrammarVisitor<? extends T>)visitor).visitMsg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MsgContext msg() throws RecognitionException {
		MsgContext _localctx = new MsgContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_msg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(54);
			match(MSG);
			setState(55);
			((MsgContext)_localctx).m = match(STRING);
			 ((MsgContext)_localctx).msgStr =  ((MsgContext)_localctx).m.getText(); 
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
	public static class MsgDeathContext extends ParserRuleContext {
		public String msgDeathStr;
		public Token m;
		public TerminalNode MSG_DEATH() { return getToken(ChestTrapGrammar.MSG_DEATH, 0); }
		public TerminalNode STRING() { return getToken(ChestTrapGrammar.STRING, 0); }
		public MsgDeathContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_msgDeath; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).enterMsgDeath(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).exitMsgDeath(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChestTrapGrammarVisitor ) return ((ChestTrapGrammarVisitor<? extends T>)visitor).visitMsgDeath(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MsgDeathContext msgDeath() throws RecognitionException {
		MsgDeathContext _localctx = new MsgDeathContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_msgDeath);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58);
			match(MSG_DEATH);
			setState(59);
			((MsgDeathContext)_localctx).m = match(STRING);
			 ((MsgDeathContext)_localctx).msgDeathStr =  ((MsgDeathContext)_localctx).m.getText(); 
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
	public static class ChestTrapContext extends ParserRuleContext {
		public ChestTrapParseRecord trap;
		public NameContext name;
		public CodeContext code;
		public LevelContext level;
		public EffectBlockContext effectBlock;
		public EffectYXContext effectYX;
		public DiceContext dice;
		public ExprContext expr;
		public DestroyContext destroy;
		public MagicContext magic;
		public MsgContext msg;
		public MsgDeathContext msgDeath;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public CodeContext code() {
			return getRuleContext(CodeContext.class,0);
		}
		public List<LevelContext> level() {
			return getRuleContexts(LevelContext.class);
		}
		public LevelContext level(int i) {
			return getRuleContext(LevelContext.class,i);
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
		public List<DestroyContext> destroy() {
			return getRuleContexts(DestroyContext.class);
		}
		public DestroyContext destroy(int i) {
			return getRuleContext(DestroyContext.class,i);
		}
		public List<MagicContext> magic() {
			return getRuleContexts(MagicContext.class);
		}
		public MagicContext magic(int i) {
			return getRuleContext(MagicContext.class,i);
		}
		public List<MsgContext> msg() {
			return getRuleContexts(MsgContext.class);
		}
		public MsgContext msg(int i) {
			return getRuleContext(MsgContext.class,i);
		}
		public List<MsgDeathContext> msgDeath() {
			return getRuleContexts(MsgDeathContext.class);
		}
		public MsgDeathContext msgDeath(int i) {
			return getRuleContext(MsgDeathContext.class,i);
		}
		public ChestTrapContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_chestTrap; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).enterChestTrap(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).exitChestTrap(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChestTrapGrammarVisitor ) return ((ChestTrapGrammarVisitor<? extends T>)visitor).visitChestTrap(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ChestTrapContext chestTrap() throws RecognitionException {
		ChestTrapContext _localctx = new ChestTrapContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_chestTrap);

		            String nameInit = "";
		            String codeInit = "";
		            String levelInit = "";
		            List<EffectParseRecord> effectInit = new ArrayList<>();
		            String destroyInit = "";
		            String magicInit = "";
		            String msgInit = null;
		            String msgDeathInit = null;
		            int lineInit = 0;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(62);
			((ChestTrapContext)_localctx).name = name();
			 nameInit = ((ChestTrapContext)_localctx).name.nameStr; 
			                   lineInit = (((ChestTrapContext)_localctx).name!=null?(((ChestTrapContext)_localctx).name.start):null).getLine(); 
			setState(64);
			((ChestTrapContext)_localctx).code = code();
			 codeInit = ((ChestTrapContext)_localctx).code.codeStr; 
			setState(95);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 108792L) != 0)) {
				{
				setState(93);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case LEVEL:
					{
					setState(66);
					((ChestTrapContext)_localctx).level = level();
					 levelInit = ((ChestTrapContext)_localctx).level.levelStr; 
					}
					break;
				case EFFECT:
					{
					setState(69);
					((ChestTrapContext)_localctx).effectBlock = effectBlock();
					 effectInit.add(new EffectParseRecord(((ChestTrapContext)_localctx).effectBlock.typeInit,
					                ((ChestTrapContext)_localctx).effectBlock.subtypeWrapperInit, ((ChestTrapContext)_localctx).effectBlock.radius, ((ChestTrapContext)_localctx).effectBlock.other,
					                ((ChestTrapContext)_localctx).effectBlock.diceString, ((ChestTrapContext)_localctx).effectBlock.yVal, ((ChestTrapContext)_localctx).effectBlock.xVal,
					                ((ChestTrapContext)_localctx).effectBlock.expressionChars, ((ChestTrapContext)_localctx).effectBlock.expressionBase,
					                ((ChestTrapContext)_localctx).effectBlock.expressionOperation,
					                ((ChestTrapContext)_localctx).effectBlock.effectMessage, (((ChestTrapContext)_localctx).effectBlock!=null?(((ChestTrapContext)_localctx).effectBlock.start):null).getLine())); 
					}
					break;
				case EFFECT_YX:
					{
					setState(72);
					((ChestTrapContext)_localctx).effectYX = effectYX();

					                if (!effectInit.isEmpty()) {
					                    EffectParseRecord last = effectInit.getLast();
					                    EffectParseRecord updated = last.amendYX(((ChestTrapContext)_localctx).effectYX.y, ((ChestTrapContext)_localctx).effectYX.x);
					                    replaceLast(effectInit, updated);
					                }
					            
					}
					break;
				case DICE:
					{
					setState(75);
					((ChestTrapContext)_localctx).dice = dice();

					                if (!effectInit.isEmpty()) {
					                    EffectParseRecord last = effectInit.getLast();
					                    String diceStr = ((ChestTrapContext)_localctx).dice.diceString;
					                    EffectParseRecord updated = last.amendExpression(diceStr,
					                                                                     ((ChestTrapContext)_localctx).dice.exprChar,
					                                                                     ((ChestTrapContext)_localctx).dice.baseName,
					                                                                     ((ChestTrapContext)_localctx).dice.operation);
					                    replaceLast(effectInit, updated);
					                }
					            
					}
					break;
				case EXPR:
					{
					setState(78);
					((ChestTrapContext)_localctx).expr = expr();

					                if (!effectInit.isEmpty() && !effectInit.getLast().diceString().isEmpty()) {
					                    EffectParseRecord last = effectInit.getLast();
					                    String lastDiceStr  = last.diceString();
					                    String lastExprChar = last.expressionChars().isEmpty() 
					                                        ? ((ChestTrapContext)_localctx).expr.exprChar
					                                        : last.expressionChars() + "^" + ((ChestTrapContext)_localctx).expr.exprChar;
					                    String lastExprBase = last.expressionBases().isEmpty() 
					                                        ? ((ChestTrapContext)_localctx).expr.baseName
					                                        : last.expressionBases() + "^" + ((ChestTrapContext)_localctx).expr.baseName;
					                    String lastExprOp   = last.expressionOperations().isEmpty() 
					                                        ? ((ChestTrapContext)_localctx).expr.operation
					                                        : last.expressionOperations() + "^" + ((ChestTrapContext)_localctx).expr.operation;
					                    EffectParseRecord updated = last.amendExpression(lastDiceStr,
					                                                lastExprChar,
					                                                lastExprBase,
					                                                lastExprOp);
					                    replaceLast(effectInit, updated);
					                }
					            
					}
					break;
				case DESTROY:
					{
					setState(81);
					((ChestTrapContext)_localctx).destroy = destroy();
					 destroyInit = ((ChestTrapContext)_localctx).destroy.destroyStr; 
					}
					break;
				case MAGIC:
					{
					setState(84);
					((ChestTrapContext)_localctx).magic = magic();
					 magicInit = ((ChestTrapContext)_localctx).magic.magicStr; 
					}
					break;
				case MSG:
					{
					setState(87);
					((ChestTrapContext)_localctx).msg = msg();
					 msgInit = ((ChestTrapContext)_localctx).msg.msgStr; 
					}
					break;
				case MSG_DEATH:
					{
					setState(90);
					((ChestTrapContext)_localctx).msgDeath = msgDeath();
					 msgDeathInit = ((ChestTrapContext)_localctx).msgDeath.msgDeathStr; 
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(97);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			_ctx.stop = _input.LT(-1);

			            ((ChestTrapContext)_localctx).trap =  new ChestTrapParseRecord(nameInit, codeInit, levelInit,
			                effectInit, destroyInit, magicInit, msgInit, msgDeathInit,
			                lineInit);
			        
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
		public List<ChestTrapParseRecord> chestTraps;
		public ChestTrapContext chestTrap;
		public TerminalNode EOF() { return getToken(ChestTrapGrammar.EOF, 0); }
		public List<ChestTrapContext> chestTrap() {
			return getRuleContexts(ChestTrapContext.class);
		}
		public ChestTrapContext chestTrap(int i) {
			return getRuleContext(ChestTrapContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChestTrapGrammarVisitor ) return ((ChestTrapGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_file);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			 ((FileContext)_localctx).chestTraps =  new ArrayList<>(); 
			setState(102); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(99);
				((FileContext)_localctx).chestTrap = chestTrap();
				 _localctx.chestTraps.add(((FileContext)_localctx).chestTrap.trap); 
				}
				}
				setState(104); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(106);
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
		public TerminalNode EFFECT() { return getToken(ChestTrapGrammar.EFFECT, 0); }
		public List<TerminalNode> UCASE() { return getTokens(ChestTrapGrammar.UCASE); }
		public TerminalNode UCASE(int i) {
			return getToken(ChestTrapGrammar.UCASE, i);
		}
		public List<TerminalNode> COLON() { return getTokens(ChestTrapGrammar.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(ChestTrapGrammar.COLON, i);
		}
		public List<TerminalNode> INTEGER() { return getTokens(ChestTrapGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(ChestTrapGrammar.INTEGER, i);
		}
		public EffectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effect; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).enterEffect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).exitEffect(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChestTrapGrammarVisitor ) return ((ChestTrapGrammarVisitor<? extends T>)visitor).visitEffect(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectContext effect() throws RecognitionException {
		EffectContext _localctx = new EffectContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_effect);

		            ((EffectContext)_localctx).wrapper =  "";
		            ((EffectContext)_localctx).radius =  "";
		            ((EffectContext)_localctx).other =  "";
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(108);
			match(EFFECT);
			setState(109);
			((EffectContext)_localctx).t = match(UCASE);

			                ((EffectContext)_localctx).type =  ((EffectContext)_localctx).t.getText();
			            
			setState(124);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(111);
				match(COLON);
				setState(112);
				((EffectContext)_localctx).st = match(UCASE);

				                ((EffectContext)_localctx).wrapper =  ((EffectContext)_localctx).st.getText();
				            
				setState(122);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COLON) {
					{
					setState(114);
					match(COLON);
					setState(115);
					((EffectContext)_localctx).rad = match(INTEGER);

					                ((EffectContext)_localctx).radius =  ((EffectContext)_localctx).rad.getText();
					            
					setState(120);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==COLON) {
						{
						setState(117);
						match(COLON);
						setState(118);
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
		public TerminalNode EFFECT_YX() { return getToken(ChestTrapGrammar.EFFECT_YX, 0); }
		public TerminalNode COLON() { return getToken(ChestTrapGrammar.COLON, 0); }
		public List<TerminalNode> INTEGER() { return getTokens(ChestTrapGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(ChestTrapGrammar.INTEGER, i);
		}
		public EffectYXContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effectYX; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).enterEffectYX(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).exitEffectYX(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChestTrapGrammarVisitor ) return ((ChestTrapGrammarVisitor<? extends T>)visitor).visitEffectYX(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectYXContext effectYX() throws RecognitionException {
		EffectYXContext _localctx = new EffectYXContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_effectYX);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(126);
			match(EFFECT_YX);
			setState(127);
			((EffectYXContext)_localctx).yVal = match(INTEGER);
			setState(128);
			match(COLON);
			setState(129);
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
		public TerminalNode DICE() { return getToken(ChestTrapGrammar.DICE, 0); }
		public TerminalNode DICE_SIMPLE_VALUE() { return getToken(ChestTrapGrammar.DICE_SIMPLE_VALUE, 0); }
		public TerminalNode DICE_COMPLEX_VALUE() { return getToken(ChestTrapGrammar.DICE_COMPLEX_VALUE, 0); }
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
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).enterDice(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).exitDice(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChestTrapGrammarVisitor ) return ((ChestTrapGrammarVisitor<? extends T>)visitor).visitDice(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DiceContext dice() throws RecognitionException {
		DiceContext _localctx = new DiceContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_dice);

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
			setState(132);
			match(DICE);
			setState(145);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DICE_COMPLEX_VALUE:
				{
				{
				setState(133);
				((DiceContext)_localctx).val = match(DICE_COMPLEX_VALUE);

				                ((DiceContext)_localctx).diceString =  ((DiceContext)_localctx).val.getText();
				            
				setState(140);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(135);
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
					setState(142);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
				}
				}
				}
				break;
			case DICE_SIMPLE_VALUE:
				{
				setState(143);
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
		public TerminalNode EXPR() { return getToken(ChestTrapGrammar.EXPR, 0); }
		public List<TerminalNode> EXPR_COLON() { return getTokens(ChestTrapGrammar.EXPR_COLON); }
		public TerminalNode EXPR_COLON(int i) {
			return getToken(ChestTrapGrammar.EXPR_COLON, i);
		}
		public TerminalNode EXPR_CHAR() { return getToken(ChestTrapGrammar.EXPR_CHAR, 0); }
		public TerminalNode EXPR_UCASE() { return getToken(ChestTrapGrammar.EXPR_UCASE, 0); }
		public TerminalNode EXPR_OP() { return getToken(ChestTrapGrammar.EXPR_OP, 0); }
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).exitExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChestTrapGrammarVisitor ) return ((ChestTrapGrammarVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(147);
			match(EXPR);
			setState(148);
			((ExprContext)_localctx).ch = match(EXPR_CHAR);
			setState(149);
			match(EXPR_COLON);
			setState(150);
			((ExprContext)_localctx).base = match(EXPR_UCASE);
			setState(151);
			match(EXPR_COLON);
			setState(152);
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
		public TerminalNode EFFECT_MESSAGE() { return getToken(ChestTrapGrammar.EFFECT_MESSAGE, 0); }
		public TerminalNode FREE_TEXT() { return getToken(ChestTrapGrammar.FREE_TEXT, 0); }
		public EffectMsgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effectMsg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).enterEffectMsg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).exitEffectMsg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChestTrapGrammarVisitor ) return ((ChestTrapGrammarVisitor<? extends T>)visitor).visitEffectMsg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectMsgContext effectMsg() throws RecognitionException {
		EffectMsgContext _localctx = new EffectMsgContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_effectMsg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(155);
			match(EFFECT_MESSAGE);
			setState(156);
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
		public TerminalNode TIME() { return getToken(ChestTrapGrammar.TIME, 0); }
		public TerminalNode DICE_SIMPLE_VALUE() { return getToken(ChestTrapGrammar.DICE_SIMPLE_VALUE, 0); }
		public TimeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_time; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).enterTime(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).exitTime(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChestTrapGrammarVisitor ) return ((ChestTrapGrammarVisitor<? extends T>)visitor).visitTime(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TimeContext time() throws RecognitionException {
		TimeContext _localctx = new TimeContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_time);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(159);
			match(TIME);
			setState(160);
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
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).enterStrayEffectLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).exitStrayEffectLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChestTrapGrammarVisitor ) return ((ChestTrapGrammarVisitor<? extends T>)visitor).visitStrayEffectLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StrayEffectLineContext strayEffectLine() throws RecognitionException {
		StrayEffectLineContext _localctx = new StrayEffectLineContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_strayEffectLine);
		try {
			setState(166);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EFFECT_YX:
				enterOuterAlt(_localctx, 1);
				{
				setState(163);
				effectYX();
				}
				break;
			case DICE:
				enterOuterAlt(_localctx, 2);
				{
				setState(164);
				dice();
				}
				break;
			case EXPR:
				enterOuterAlt(_localctx, 3);
				{
				setState(165);
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
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).enterEffectBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ChestTrapGrammarListener ) ((ChestTrapGrammarListener)listener).exitEffectBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ChestTrapGrammarVisitor ) return ((ChestTrapGrammarVisitor<? extends T>)visitor).visitEffectBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectBlockContext effectBlock() throws RecognitionException {
		EffectBlockContext _localctx = new EffectBlockContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_effectBlock);

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
			setState(168);
			((EffectBlockContext)_localctx).effect = effect();

			                ((EffectBlockContext)_localctx).lineNo =  _localctx.start.getLine();
			                ((EffectBlockContext)_localctx).typeInit =  ((EffectBlockContext)_localctx).effect.type;
			                ((EffectBlockContext)_localctx).subtypeWrapperInit =  ((EffectBlockContext)_localctx).effect.wrapper;
			                ((EffectBlockContext)_localctx).radius =  ((EffectBlockContext)_localctx).effect.radius;
			                ((EffectBlockContext)_localctx).other =  ((EffectBlockContext)_localctx).effect.other;
			            
			setState(186);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				{
				setState(170);
				((EffectBlockContext)_localctx).effectYX = effectYX();

				                ((EffectBlockContext)_localctx).yVal =  ((EffectBlockContext)_localctx).effectYX.y;
				                ((EffectBlockContext)_localctx).xVal =  ((EffectBlockContext)_localctx).effectYX.x;
				            
				setState(172);
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
				setState(175);
				((EffectBlockContext)_localctx).dice = dice();

				                ((EffectBlockContext)_localctx).diceString =  ((EffectBlockContext)_localctx).dice.diceString;
				                expressionString = ((EffectBlockContext)_localctx).dice.exprChar;
				                baseString = ((EffectBlockContext)_localctx).dice.baseName;
				                opString = ((EffectBlockContext)_localctx).dice.operation;
				            
				setState(177);
				((EffectBlockContext)_localctx).effectYX = effectYX();

				                ((EffectBlockContext)_localctx).yVal =  ((EffectBlockContext)_localctx).effectYX.y;
				                ((EffectBlockContext)_localctx).xVal =  ((EffectBlockContext)_localctx).effectYX.x;
				            
				}
				}
				break;
			case 3:
				{
				setState(180);
				((EffectBlockContext)_localctx).dice = dice();

				                ((EffectBlockContext)_localctx).diceString =  ((EffectBlockContext)_localctx).dice.diceString;
				                expressionString = ((EffectBlockContext)_localctx).dice.exprChar;
				                baseString = ((EffectBlockContext)_localctx).dice.baseName;
				                opString = ((EffectBlockContext)_localctx).dice.operation;
				            
				}
				break;
			case 4:
				{
				setState(183);
				((EffectBlockContext)_localctx).effectYX = effectYX();

				                ((EffectBlockContext)_localctx).yVal =  ((EffectBlockContext)_localctx).effectYX.y;
				                ((EffectBlockContext)_localctx).xVal =  ((EffectBlockContext)_localctx).effectYX.x;
				            
				}
				break;
			}
			setState(191);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EFFECT_MESSAGE) {
				{
				setState(188);
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
		"\u0004\u0001\u001f\u00c2\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0005\u0007^\b\u0007\n\u0007\f\u0007a\t\u0007\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0004\bg\b\b\u000b\b\f\bh\u0001\b\u0001\b\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0003\ty\b\t\u0003\t{\b\t\u0003\t}\b\t\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0005\u000b\u008b\b\u000b\n\u000b"+
		"\f\u000b\u008e\t\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u0092\b\u000b"+
		"\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u00a7\b\u000f\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010"+
		"\u00bb\b\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u00c0\b"+
		"\u0010\u0001\u0010\u0000\u0000\u0011\u0000\u0002\u0004\u0006\b\n\f\u000e"+
		"\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \u0000\u0000\u00c6\u0000"+
		"\"\u0001\u0000\u0000\u0000\u0002&\u0001\u0000\u0000\u0000\u0004*\u0001"+
		"\u0000\u0000\u0000\u0006.\u0001\u0000\u0000\u0000\b2\u0001\u0000\u0000"+
		"\u0000\n6\u0001\u0000\u0000\u0000\f:\u0001\u0000\u0000\u0000\u000e>\u0001"+
		"\u0000\u0000\u0000\u0010b\u0001\u0000\u0000\u0000\u0012l\u0001\u0000\u0000"+
		"\u0000\u0014~\u0001\u0000\u0000\u0000\u0016\u0084\u0001\u0000\u0000\u0000"+
		"\u0018\u0093\u0001\u0000\u0000\u0000\u001a\u009b\u0001\u0000\u0000\u0000"+
		"\u001c\u009f\u0001\u0000\u0000\u0000\u001e\u00a6\u0001\u0000\u0000\u0000"+
		" \u00a8\u0001\u0000\u0000\u0000\"#\u0005\u0001\u0000\u0000#$\u0005\u0016"+
		"\u0000\u0000$%\u0006\u0000\uffff\uffff\u0000%\u0001\u0001\u0000\u0000"+
		"\u0000&\'\u0005\u0002\u0000\u0000\'(\u0005\u0016\u0000\u0000()\u0006\u0001"+
		"\uffff\uffff\u0000)\u0003\u0001\u0000\u0000\u0000*+\u0005\u0003\u0000"+
		"\u0000+,\u0005\u0013\u0000\u0000,-\u0006\u0002\uffff\uffff\u0000-\u0005"+
		"\u0001\u0000\u0000\u0000./\u0005\u0004\u0000\u0000/0\u0005\u0013\u0000"+
		"\u000001\u0006\u0003\uffff\uffff\u00001\u0007\u0001\u0000\u0000\u0000"+
		"23\u0005\u0005\u0000\u000034\u0005\u0013\u0000\u000045\u0006\u0004\uffff"+
		"\uffff\u00005\t\u0001\u0000\u0000\u000067\u0005\u0006\u0000\u000078\u0005"+
		"\u0016\u0000\u000089\u0006\u0005\uffff\uffff\u00009\u000b\u0001\u0000"+
		"\u0000\u0000:;\u0005\u0007\u0000\u0000;<\u0005\u0016\u0000\u0000<=\u0006"+
		"\u0006\uffff\uffff\u0000=\r\u0001\u0000\u0000\u0000>?\u0003\u0000\u0000"+
		"\u0000?@\u0006\u0007\uffff\uffff\u0000@A\u0003\u0002\u0001\u0000A_\u0006"+
		"\u0007\uffff\uffff\u0000BC\u0003\u0004\u0002\u0000CD\u0006\u0007\uffff"+
		"\uffff\u0000D^\u0001\u0000\u0000\u0000EF\u0003 \u0010\u0000FG\u0006\u0007"+
		"\uffff\uffff\u0000G^\u0001\u0000\u0000\u0000HI\u0003\u0014\n\u0000IJ\u0006"+
		"\u0007\uffff\uffff\u0000J^\u0001\u0000\u0000\u0000KL\u0003\u0016\u000b"+
		"\u0000LM\u0006\u0007\uffff\uffff\u0000M^\u0001\u0000\u0000\u0000NO\u0003"+
		"\u0018\f\u0000OP\u0006\u0007\uffff\uffff\u0000P^\u0001\u0000\u0000\u0000"+
		"QR\u0003\u0006\u0003\u0000RS\u0006\u0007\uffff\uffff\u0000S^\u0001\u0000"+
		"\u0000\u0000TU\u0003\b\u0004\u0000UV\u0006\u0007\uffff\uffff\u0000V^\u0001"+
		"\u0000\u0000\u0000WX\u0003\n\u0005\u0000XY\u0006\u0007\uffff\uffff\u0000"+
		"Y^\u0001\u0000\u0000\u0000Z[\u0003\f\u0006\u0000[\\\u0006\u0007\uffff"+
		"\uffff\u0000\\^\u0001\u0000\u0000\u0000]B\u0001\u0000\u0000\u0000]E\u0001"+
		"\u0000\u0000\u0000]H\u0001\u0000\u0000\u0000]K\u0001\u0000\u0000\u0000"+
		"]N\u0001\u0000\u0000\u0000]Q\u0001\u0000\u0000\u0000]T\u0001\u0000\u0000"+
		"\u0000]W\u0001\u0000\u0000\u0000]Z\u0001\u0000\u0000\u0000^a\u0001\u0000"+
		"\u0000\u0000_]\u0001\u0000\u0000\u0000_`\u0001\u0000\u0000\u0000`\u000f"+
		"\u0001\u0000\u0000\u0000a_\u0001\u0000\u0000\u0000bf\u0006\b\uffff\uffff"+
		"\u0000cd\u0003\u000e\u0007\u0000de\u0006\b\uffff\uffff\u0000eg\u0001\u0000"+
		"\u0000\u0000fc\u0001\u0000\u0000\u0000gh\u0001\u0000\u0000\u0000hf\u0001"+
		"\u0000\u0000\u0000hi\u0001\u0000\u0000\u0000ij\u0001\u0000\u0000\u0000"+
		"jk\u0005\u0000\u0000\u0001k\u0011\u0001\u0000\u0000\u0000lm\u0005\u000b"+
		"\u0000\u0000mn\u0005\u0012\u0000\u0000n|\u0006\t\uffff\uffff\u0000op\u0005"+
		"\u0011\u0000\u0000pq\u0005\u0012\u0000\u0000qz\u0006\t\uffff\uffff\u0000"+
		"rs\u0005\u0011\u0000\u0000st\u0005\u0013\u0000\u0000tx\u0006\t\uffff\uffff"+
		"\u0000uv\u0005\u0011\u0000\u0000vw\u0005\u0013\u0000\u0000wy\u0006\t\uffff"+
		"\uffff\u0000xu\u0001\u0000\u0000\u0000xy\u0001\u0000\u0000\u0000y{\u0001"+
		"\u0000\u0000\u0000zr\u0001\u0000\u0000\u0000z{\u0001\u0000\u0000\u0000"+
		"{}\u0001\u0000\u0000\u0000|o\u0001\u0000\u0000\u0000|}\u0001\u0000\u0000"+
		"\u0000}\u0013\u0001\u0000\u0000\u0000~\u007f\u0005\u000f\u0000\u0000\u007f"+
		"\u0080\u0005\u0013\u0000\u0000\u0080\u0081\u0005\u0011\u0000\u0000\u0081"+
		"\u0082\u0005\u0013\u0000\u0000\u0082\u0083\u0006\n\uffff\uffff\u0000\u0083"+
		"\u0015\u0001\u0000\u0000\u0000\u0084\u0091\u0005\r\u0000\u0000\u0085\u0086"+
		"\u0005\u001a\u0000\u0000\u0086\u008c\u0006\u000b\uffff\uffff\u0000\u0087"+
		"\u0088\u0003\u0018\f\u0000\u0088\u0089\u0006\u000b\uffff\uffff\u0000\u0089"+
		"\u008b\u0001\u0000\u0000\u0000\u008a\u0087\u0001\u0000\u0000\u0000\u008b"+
		"\u008e\u0001\u0000\u0000\u0000\u008c\u008a\u0001\u0000\u0000\u0000\u008c"+
		"\u008d\u0001\u0000\u0000\u0000\u008d\u0092\u0001\u0000\u0000\u0000\u008e"+
		"\u008c\u0001\u0000\u0000\u0000\u008f\u0090\u0005\u0019\u0000\u0000\u0090"+
		"\u0092\u0006\u000b\uffff\uffff\u0000\u0091\u0085\u0001\u0000\u0000\u0000"+
		"\u0091\u008f\u0001\u0000\u0000\u0000\u0092\u0017\u0001\u0000\u0000\u0000"+
		"\u0093\u0094\u0005\u0010\u0000\u0000\u0094\u0095\u0005\u001b\u0000\u0000"+
		"\u0095\u0096\u0005\u001c\u0000\u0000\u0096\u0097\u0005\u001d\u0000\u0000"+
		"\u0097\u0098\u0005\u001c\u0000\u0000\u0098\u0099\u0005\u001e\u0000\u0000"+
		"\u0099\u009a\u0006\f\uffff\uffff\u0000\u009a\u0019\u0001\u0000\u0000\u0000"+
		"\u009b\u009c\u0005\f\u0000\u0000\u009c\u009d\u0005\u0018\u0000\u0000\u009d"+
		"\u009e\u0006\r\uffff\uffff\u0000\u009e\u001b\u0001\u0000\u0000\u0000\u009f"+
		"\u00a0\u0005\u000e\u0000\u0000\u00a0\u00a1\u0005\u0019\u0000\u0000\u00a1"+
		"\u00a2\u0006\u000e\uffff\uffff\u0000\u00a2\u001d\u0001\u0000\u0000\u0000"+
		"\u00a3\u00a7\u0003\u0014\n\u0000\u00a4\u00a7\u0003\u0016\u000b\u0000\u00a5"+
		"\u00a7\u0003\u0018\f\u0000\u00a6\u00a3\u0001\u0000\u0000\u0000\u00a6\u00a4"+
		"\u0001\u0000\u0000\u0000\u00a6\u00a5\u0001\u0000\u0000\u0000\u00a7\u001f"+
		"\u0001\u0000\u0000\u0000\u00a8\u00a9\u0003\u0012\t\u0000\u00a9\u00ba\u0006"+
		"\u0010\uffff\uffff\u0000\u00aa\u00ab\u0003\u0014\n\u0000\u00ab\u00ac\u0006"+
		"\u0010\uffff\uffff\u0000\u00ac\u00ad\u0003\u0016\u000b\u0000\u00ad\u00ae"+
		"\u0006\u0010\uffff\uffff\u0000\u00ae\u00bb\u0001\u0000\u0000\u0000\u00af"+
		"\u00b0\u0003\u0016\u000b\u0000\u00b0\u00b1\u0006\u0010\uffff\uffff\u0000"+
		"\u00b1\u00b2\u0003\u0014\n\u0000\u00b2\u00b3\u0006\u0010\uffff\uffff\u0000"+
		"\u00b3\u00bb\u0001\u0000\u0000\u0000\u00b4\u00b5\u0003\u0016\u000b\u0000"+
		"\u00b5\u00b6\u0006\u0010\uffff\uffff\u0000\u00b6\u00bb\u0001\u0000\u0000"+
		"\u0000\u00b7\u00b8\u0003\u0014\n\u0000\u00b8\u00b9\u0006\u0010\uffff\uffff"+
		"\u0000\u00b9\u00bb\u0001\u0000\u0000\u0000\u00ba\u00aa\u0001\u0000\u0000"+
		"\u0000\u00ba\u00af\u0001\u0000\u0000\u0000\u00ba\u00b4\u0001\u0000\u0000"+
		"\u0000\u00ba\u00b7\u0001\u0000\u0000\u0000\u00ba\u00bb\u0001\u0000\u0000"+
		"\u0000\u00bb\u00bf\u0001\u0000\u0000\u0000\u00bc\u00bd\u0003\u001a\r\u0000"+
		"\u00bd\u00be\u0006\u0010\uffff\uffff\u0000\u00be\u00c0\u0001\u0000\u0000"+
		"\u0000\u00bf\u00bc\u0001\u0000\u0000\u0000\u00bf\u00c0\u0001\u0000\u0000"+
		"\u0000\u00c0!\u0001\u0000\u0000\u0000\u000b]_hxz|\u008c\u0091\u00a6\u00ba"+
		"\u00bf";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}