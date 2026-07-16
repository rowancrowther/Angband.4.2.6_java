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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/BlowEffect.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.frontend.colour.enums.ColourType;
    import uk.co.jackoftrades.middle.game.Projection;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.monsters.BlowEffect;
    import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;

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
public class BlowEffectParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		COMMENT=1, EOL=2, NAME=3, POWER=4, EVAL=5, DESC=6, LORE_COLOUR_BASE=7, 
		LORE_COLOUR_RESIST=8, LORE_COLOUR_IMMUNE=9, EFFECT_TYPE=10, RESIST=11, 
		LASH_TYPE=12, INTEGER=13, UCASE=14, LCASE=15;
	public static final int
		RULE_name = 0, RULE_power = 1, RULE_eval = 2, RULE_desc = 3, RULE_loreColourBase = 4, 
		RULE_loreColourResist = 5, RULE_loreColourImmune = 6, RULE_effectType = 7, 
		RULE_resist = 8, RULE_lashType = 9, RULE_blowEffect = 10, RULE_file = 11;
	private static String[] makeRuleNames() {
		return new String[] {
			"name", "power", "eval", "desc", "loreColourBase", "loreColourResist", 
			"loreColourImmune", "effectType", "resist", "lashType", "blowEffect", 
			"file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, "'name:'", "'power:'", "'eval:'", "'desc:'", "'lore-color-base:'", 
			"'lore-color-resist:'", "'lore-color-immune:'", "'effect-type:'", "'resist:'", 
			"'lash-type:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "COMMENT", "EOL", "NAME", "POWER", "EVAL", "DESC", "LORE_COLOUR_BASE", 
			"LORE_COLOUR_RESIST", "LORE_COLOUR_IMMUNE", "EFFECT_TYPE", "RESIST", 
			"LASH_TYPE", "INTEGER", "UCASE", "LCASE"
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
	public String getGrammarFileName() { return "BlowEffect.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public BlowEffectParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public Token UCASE;
		public TerminalNode NAME() { return getToken(BlowEffectParser.NAME, 0); }
		public TerminalNode UCASE() { return getToken(BlowEffectParser.UCASE, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffectListener ) ((BlowEffectListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffectListener ) ((BlowEffectListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowEffectVisitor ) return ((BlowEffectVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(24);
			match(NAME);
			setState(25);
			((NameContext)_localctx).UCASE = match(UCASE);
			 ((NameContext)_localctx).nameStr =  ((NameContext)_localctx).UCASE.getText(); 
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
		public int powerInt;
		public Token INTEGER;
		public TerminalNode POWER() { return getToken(BlowEffectParser.POWER, 0); }
		public TerminalNode INTEGER() { return getToken(BlowEffectParser.INTEGER, 0); }
		public PowerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_power; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffectListener ) ((BlowEffectListener)listener).enterPower(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffectListener ) ((BlowEffectListener)listener).exitPower(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowEffectVisitor ) return ((BlowEffectVisitor<? extends T>)visitor).visitPower(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PowerContext power() throws RecognitionException {
		PowerContext _localctx = new PowerContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_power);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(28);
			match(POWER);
			setState(29);
			((PowerContext)_localctx).INTEGER = match(INTEGER);
			 ((PowerContext)_localctx).powerInt =  Integer.parseInt(((PowerContext)_localctx).INTEGER.getText()); 
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
	public static class EvalContext extends ParserRuleContext {
		public int evalInt;
		public Token INTEGER;
		public TerminalNode EVAL() { return getToken(BlowEffectParser.EVAL, 0); }
		public TerminalNode INTEGER() { return getToken(BlowEffectParser.INTEGER, 0); }
		public EvalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eval; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffectListener ) ((BlowEffectListener)listener).enterEval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffectListener ) ((BlowEffectListener)listener).exitEval(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowEffectVisitor ) return ((BlowEffectVisitor<? extends T>)visitor).visitEval(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EvalContext eval() throws RecognitionException {
		EvalContext _localctx = new EvalContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_eval);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(32);
			match(EVAL);
			setState(33);
			((EvalContext)_localctx).INTEGER = match(INTEGER);
			 ((EvalContext)_localctx).evalInt =  Integer.parseInt(((EvalContext)_localctx).INTEGER.getText()); 
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
		public Token LCASE;
		public TerminalNode DESC() { return getToken(BlowEffectParser.DESC, 0); }
		public TerminalNode LCASE() { return getToken(BlowEffectParser.LCASE, 0); }
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_desc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffectListener ) ((BlowEffectListener)listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffectListener ) ((BlowEffectListener)listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowEffectVisitor ) return ((BlowEffectVisitor<? extends T>)visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_desc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(36);
			match(DESC);
			setState(37);
			((DescContext)_localctx).LCASE = match(LCASE);
			 ((DescContext)_localctx).descStr =  ((DescContext)_localctx).LCASE.getText(); 
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
	public static class LoreColourBaseContext extends ParserRuleContext {
		public ColourType colType;
		public Token LCASE;
		public TerminalNode LORE_COLOUR_BASE() { return getToken(BlowEffectParser.LORE_COLOUR_BASE, 0); }
		public TerminalNode LCASE() { return getToken(BlowEffectParser.LCASE, 0); }
		public LoreColourBaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_loreColourBase; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffectListener ) ((BlowEffectListener)listener).enterLoreColourBase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffectListener ) ((BlowEffectListener)listener).exitLoreColourBase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowEffectVisitor ) return ((BlowEffectVisitor<? extends T>)visitor).visitLoreColourBase(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LoreColourBaseContext loreColourBase() throws RecognitionException {
		LoreColourBaseContext _localctx = new LoreColourBaseContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_loreColourBase);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(40);
			match(LORE_COLOUR_BASE);
			setState(41);
			((LoreColourBaseContext)_localctx).LCASE = match(LCASE);

			                String raw = ((LoreColourBaseContext)_localctx).LCASE.getText();
			                ((LoreColourBaseContext)_localctx).colType =  ColourType.getColourType(raw);
			            
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
	public static class LoreColourResistContext extends ParserRuleContext {
		public ColourType colType;
		public Token LCASE;
		public TerminalNode LORE_COLOUR_RESIST() { return getToken(BlowEffectParser.LORE_COLOUR_RESIST, 0); }
		public TerminalNode LCASE() { return getToken(BlowEffectParser.LCASE, 0); }
		public LoreColourResistContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_loreColourResist; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffectListener ) ((BlowEffectListener)listener).enterLoreColourResist(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffectListener ) ((BlowEffectListener)listener).exitLoreColourResist(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowEffectVisitor ) return ((BlowEffectVisitor<? extends T>)visitor).visitLoreColourResist(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LoreColourResistContext loreColourResist() throws RecognitionException {
		LoreColourResistContext _localctx = new LoreColourResistContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_loreColourResist);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			match(LORE_COLOUR_RESIST);
			setState(45);
			((LoreColourResistContext)_localctx).LCASE = match(LCASE);

			                String raw = ((LoreColourResistContext)_localctx).LCASE.getText();
			                ((LoreColourResistContext)_localctx).colType =  ColourType.getColourType(raw);
			            
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
	public static class LoreColourImmuneContext extends ParserRuleContext {
		public ColourType colType;
		public Token LCASE;
		public TerminalNode LORE_COLOUR_IMMUNE() { return getToken(BlowEffectParser.LORE_COLOUR_IMMUNE, 0); }
		public TerminalNode LCASE() { return getToken(BlowEffectParser.LCASE, 0); }
		public LoreColourImmuneContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_loreColourImmune; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffectListener ) ((BlowEffectListener)listener).enterLoreColourImmune(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffectListener ) ((BlowEffectListener)listener).exitLoreColourImmune(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowEffectVisitor ) return ((BlowEffectVisitor<? extends T>)visitor).visitLoreColourImmune(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LoreColourImmuneContext loreColourImmune() throws RecognitionException {
		LoreColourImmuneContext _localctx = new LoreColourImmuneContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_loreColourImmune);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			match(LORE_COLOUR_IMMUNE);
			setState(49);
			((LoreColourImmuneContext)_localctx).LCASE = match(LCASE);

			                String raw = ((LoreColourImmuneContext)_localctx).LCASE.getText();
			                ((LoreColourImmuneContext)_localctx).colType =  ColourType.getColourType(raw);
			            
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
	public static class EffectTypeContext extends ParserRuleContext {
		public String type;
		public Token LCASE;
		public TerminalNode EFFECT_TYPE() { return getToken(BlowEffectParser.EFFECT_TYPE, 0); }
		public TerminalNode LCASE() { return getToken(BlowEffectParser.LCASE, 0); }
		public EffectTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effectType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffectListener ) ((BlowEffectListener)listener).enterEffectType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffectListener ) ((BlowEffectListener)listener).exitEffectType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowEffectVisitor ) return ((BlowEffectVisitor<? extends T>)visitor).visitEffectType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectTypeContext effectType() throws RecognitionException {
		EffectTypeContext _localctx = new EffectTypeContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_effectType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			match(EFFECT_TYPE);
			setState(53);
			((EffectTypeContext)_localctx).LCASE = match(LCASE);
			 ((EffectTypeContext)_localctx).type =  ((EffectTypeContext)_localctx).LCASE.getText(); 
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
		public Token UCASE;
		public TerminalNode RESIST() { return getToken(BlowEffectParser.RESIST, 0); }
		public TerminalNode UCASE() { return getToken(BlowEffectParser.UCASE, 0); }
		public ResistContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_resist; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffectListener ) ((BlowEffectListener)listener).enterResist(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffectListener ) ((BlowEffectListener)listener).exitResist(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowEffectVisitor ) return ((BlowEffectVisitor<? extends T>)visitor).visitResist(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ResistContext resist() throws RecognitionException {
		ResistContext _localctx = new ResistContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_resist);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			match(RESIST);
			setState(57);
			((ResistContext)_localctx).UCASE = match(UCASE);
			 ((ResistContext)_localctx).res =  ((ResistContext)_localctx).UCASE.getText(); 
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
	public static class LashTypeContext extends ParserRuleContext {
		public Projection projObj;
		public Token UCASE;
		public TerminalNode LASH_TYPE() { return getToken(BlowEffectParser.LASH_TYPE, 0); }
		public TerminalNode UCASE() { return getToken(BlowEffectParser.UCASE, 0); }
		public LashTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lashType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffectListener ) ((BlowEffectListener)listener).enterLashType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffectListener ) ((BlowEffectListener)listener).exitLashType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowEffectVisitor ) return ((BlowEffectVisitor<? extends T>)visitor).visitLashType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LashTypeContext lashType() throws RecognitionException {
		LashTypeContext _localctx = new LashTypeContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_lashType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(60);
			match(LASH_TYPE);
			setState(61);
			((LashTypeContext)_localctx).UCASE = match(UCASE);

			                String raw = ((LashTypeContext)_localctx).UCASE.getText().toLowerCase();
			                ((LashTypeContext)_localctx).projObj =  GameConstants.lookupProjectionByLash(raw);
			            
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
	public static class BlowEffectContext extends ParserRuleContext {
		public BlowEffect effect;
		public NameContext name;
		public PowerContext power;
		public EvalContext eval;
		public DescContext desc;
		public LoreColourBaseContext loreColourBase;
		public LoreColourResistContext loreColourResist;
		public LoreColourImmuneContext loreColourImmune;
		public EffectTypeContext effectType;
		public ResistContext resist;
		public LashTypeContext lashType;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public PowerContext power() {
			return getRuleContext(PowerContext.class,0);
		}
		public List<EvalContext> eval() {
			return getRuleContexts(EvalContext.class);
		}
		public EvalContext eval(int i) {
			return getRuleContext(EvalContext.class,i);
		}
		public List<DescContext> desc() {
			return getRuleContexts(DescContext.class);
		}
		public DescContext desc(int i) {
			return getRuleContext(DescContext.class,i);
		}
		public List<LoreColourBaseContext> loreColourBase() {
			return getRuleContexts(LoreColourBaseContext.class);
		}
		public LoreColourBaseContext loreColourBase(int i) {
			return getRuleContext(LoreColourBaseContext.class,i);
		}
		public List<LoreColourResistContext> loreColourResist() {
			return getRuleContexts(LoreColourResistContext.class);
		}
		public LoreColourResistContext loreColourResist(int i) {
			return getRuleContext(LoreColourResistContext.class,i);
		}
		public List<LoreColourImmuneContext> loreColourImmune() {
			return getRuleContexts(LoreColourImmuneContext.class);
		}
		public LoreColourImmuneContext loreColourImmune(int i) {
			return getRuleContext(LoreColourImmuneContext.class,i);
		}
		public List<EffectTypeContext> effectType() {
			return getRuleContexts(EffectTypeContext.class);
		}
		public EffectTypeContext effectType(int i) {
			return getRuleContext(EffectTypeContext.class,i);
		}
		public List<ResistContext> resist() {
			return getRuleContexts(ResistContext.class);
		}
		public ResistContext resist(int i) {
			return getRuleContext(ResistContext.class,i);
		}
		public List<LashTypeContext> lashType() {
			return getRuleContexts(LashTypeContext.class);
		}
		public LashTypeContext lashType(int i) {
			return getRuleContext(LashTypeContext.class,i);
		}
		public BlowEffectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_blowEffect; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffectListener ) ((BlowEffectListener)listener).enterBlowEffect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffectListener ) ((BlowEffectListener)listener).exitBlowEffect(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowEffectVisitor ) return ((BlowEffectVisitor<? extends T>)visitor).visitBlowEffect(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlowEffectContext blowEffect() throws RecognitionException {
		BlowEffectContext _localctx = new BlowEffectContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_blowEffect);

		            String nameInit = "";
		            int powerInit = 0;
		            int evalInit = 0;
		            String descInit = "";
		            ColourType baseInit = ColourType.COLOUR_TYPE_DARK;
		            ColourType resistColInit = ColourType.COLOUR_TYPE_DARK;
		            ColourType immuneInit = ColourType.COLOUR_TYPE_DARK;
		            String effectInit = "";
		            String resistInit = "";
		            Projection lashTypeInit = null;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64);
			((BlowEffectContext)_localctx).name = name();
			 nameInit = ((BlowEffectContext)_localctx).name.nameStr; 
			setState(66);
			((BlowEffectContext)_localctx).power = power();
			 powerInit = ((BlowEffectContext)_localctx).power.powerInt; 
			setState(92); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(92);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case EVAL:
					{
					setState(68);
					((BlowEffectContext)_localctx).eval = eval();
					 evalInit = ((BlowEffectContext)_localctx).eval.evalInt; 
					}
					break;
				case DESC:
					{
					setState(71);
					((BlowEffectContext)_localctx).desc = desc();
					 descInit = ((BlowEffectContext)_localctx).desc.descStr; 
					}
					break;
				case LORE_COLOUR_BASE:
					{
					setState(74);
					((BlowEffectContext)_localctx).loreColourBase = loreColourBase();
					 baseInit = ((BlowEffectContext)_localctx).loreColourBase.colType; 
					}
					break;
				case LORE_COLOUR_RESIST:
					{
					setState(77);
					((BlowEffectContext)_localctx).loreColourResist = loreColourResist();
					 resistColInit = ((BlowEffectContext)_localctx).loreColourResist.colType; 
					}
					break;
				case LORE_COLOUR_IMMUNE:
					{
					setState(80);
					((BlowEffectContext)_localctx).loreColourImmune = loreColourImmune();
					 immuneInit = ((BlowEffectContext)_localctx).loreColourImmune.colType; 
					}
					break;
				case EFFECT_TYPE:
					{
					setState(83);
					((BlowEffectContext)_localctx).effectType = effectType();
					 effectInit = ((BlowEffectContext)_localctx).effectType.type; 
					}
					break;
				case RESIST:
					{
					setState(86);
					((BlowEffectContext)_localctx).resist = resist();
					 resistInit = ((BlowEffectContext)_localctx).resist.res; 
					}
					break;
				case LASH_TYPE:
					{
					setState(89);
					((BlowEffectContext)_localctx).lashType = lashType();
					 lashTypeInit = ((BlowEffectContext)_localctx).lashType.projObj; 
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(94); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 8160L) != 0) );
			}
			_ctx.stop = _input.LT(-1);

			            ((BlowEffectContext)_localctx).effect =  new BlowEffect(nameInit, powerInit, evalInit, descInit, baseInit, resistColInit, immuneInit,
			                                     effectInit, resistInit, lashTypeInit);
			        
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
		public List<BlowEffect> blowEffects;
		public BlowEffectContext blowEffect;
		public TerminalNode EOF() { return getToken(BlowEffectParser.EOF, 0); }
		public List<BlowEffectContext> blowEffect() {
			return getRuleContexts(BlowEffectContext.class);
		}
		public BlowEffectContext blowEffect(int i) {
			return getRuleContext(BlowEffectContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffectListener ) ((BlowEffectListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffectListener ) ((BlowEffectListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowEffectVisitor ) return ((BlowEffectVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_file);

		            ((FileContext)_localctx).blowEffects =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(96);
				((FileContext)_localctx).blowEffect = blowEffect();
				 _localctx.blowEffects.add(((FileContext)_localctx).blowEffect.effect); 
				}
				}
				setState(101); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(103);
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
		"\u0004\u0001\u000fj\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0004\n]\b\n\u000b\n\f\n^\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0004\u000bd\b\u000b\u000b\u000b\f\u000be\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0000\u0000\f\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012"+
		"\u0014\u0016\u0000\u0000f\u0000\u0018\u0001\u0000\u0000\u0000\u0002\u001c"+
		"\u0001\u0000\u0000\u0000\u0004 \u0001\u0000\u0000\u0000\u0006$\u0001\u0000"+
		"\u0000\u0000\b(\u0001\u0000\u0000\u0000\n,\u0001\u0000\u0000\u0000\f0"+
		"\u0001\u0000\u0000\u0000\u000e4\u0001\u0000\u0000\u0000\u00108\u0001\u0000"+
		"\u0000\u0000\u0012<\u0001\u0000\u0000\u0000\u0014@\u0001\u0000\u0000\u0000"+
		"\u0016c\u0001\u0000\u0000\u0000\u0018\u0019\u0005\u0003\u0000\u0000\u0019"+
		"\u001a\u0005\u000e\u0000\u0000\u001a\u001b\u0006\u0000\uffff\uffff\u0000"+
		"\u001b\u0001\u0001\u0000\u0000\u0000\u001c\u001d\u0005\u0004\u0000\u0000"+
		"\u001d\u001e\u0005\r\u0000\u0000\u001e\u001f\u0006\u0001\uffff\uffff\u0000"+
		"\u001f\u0003\u0001\u0000\u0000\u0000 !\u0005\u0005\u0000\u0000!\"\u0005"+
		"\r\u0000\u0000\"#\u0006\u0002\uffff\uffff\u0000#\u0005\u0001\u0000\u0000"+
		"\u0000$%\u0005\u0006\u0000\u0000%&\u0005\u000f\u0000\u0000&\'\u0006\u0003"+
		"\uffff\uffff\u0000\'\u0007\u0001\u0000\u0000\u0000()\u0005\u0007\u0000"+
		"\u0000)*\u0005\u000f\u0000\u0000*+\u0006\u0004\uffff\uffff\u0000+\t\u0001"+
		"\u0000\u0000\u0000,-\u0005\b\u0000\u0000-.\u0005\u000f\u0000\u0000./\u0006"+
		"\u0005\uffff\uffff\u0000/\u000b\u0001\u0000\u0000\u000001\u0005\t\u0000"+
		"\u000012\u0005\u000f\u0000\u000023\u0006\u0006\uffff\uffff\u00003\r\u0001"+
		"\u0000\u0000\u000045\u0005\n\u0000\u000056\u0005\u000f\u0000\u000067\u0006"+
		"\u0007\uffff\uffff\u00007\u000f\u0001\u0000\u0000\u000089\u0005\u000b"+
		"\u0000\u00009:\u0005\u000e\u0000\u0000:;\u0006\b\uffff\uffff\u0000;\u0011"+
		"\u0001\u0000\u0000\u0000<=\u0005\f\u0000\u0000=>\u0005\u000e\u0000\u0000"+
		">?\u0006\t\uffff\uffff\u0000?\u0013\u0001\u0000\u0000\u0000@A\u0003\u0000"+
		"\u0000\u0000AB\u0006\n\uffff\uffff\u0000BC\u0003\u0002\u0001\u0000C\\"+
		"\u0006\n\uffff\uffff\u0000DE\u0003\u0004\u0002\u0000EF\u0006\n\uffff\uffff"+
		"\u0000F]\u0001\u0000\u0000\u0000GH\u0003\u0006\u0003\u0000HI\u0006\n\uffff"+
		"\uffff\u0000I]\u0001\u0000\u0000\u0000JK\u0003\b\u0004\u0000KL\u0006\n"+
		"\uffff\uffff\u0000L]\u0001\u0000\u0000\u0000MN\u0003\n\u0005\u0000NO\u0006"+
		"\n\uffff\uffff\u0000O]\u0001\u0000\u0000\u0000PQ\u0003\f\u0006\u0000Q"+
		"R\u0006\n\uffff\uffff\u0000R]\u0001\u0000\u0000\u0000ST\u0003\u000e\u0007"+
		"\u0000TU\u0006\n\uffff\uffff\u0000U]\u0001\u0000\u0000\u0000VW\u0003\u0010"+
		"\b\u0000WX\u0006\n\uffff\uffff\u0000X]\u0001\u0000\u0000\u0000YZ\u0003"+
		"\u0012\t\u0000Z[\u0006\n\uffff\uffff\u0000[]\u0001\u0000\u0000\u0000\\"+
		"D\u0001\u0000\u0000\u0000\\G\u0001\u0000\u0000\u0000\\J\u0001\u0000\u0000"+
		"\u0000\\M\u0001\u0000\u0000\u0000\\P\u0001\u0000\u0000\u0000\\S\u0001"+
		"\u0000\u0000\u0000\\V\u0001\u0000\u0000\u0000\\Y\u0001\u0000\u0000\u0000"+
		"]^\u0001\u0000\u0000\u0000^\\\u0001\u0000\u0000\u0000^_\u0001\u0000\u0000"+
		"\u0000_\u0015\u0001\u0000\u0000\u0000`a\u0003\u0014\n\u0000ab\u0006\u000b"+
		"\uffff\uffff\u0000bd\u0001\u0000\u0000\u0000c`\u0001\u0000\u0000\u0000"+
		"de\u0001\u0000\u0000\u0000ec\u0001\u0000\u0000\u0000ef\u0001\u0000\u0000"+
		"\u0000fg\u0001\u0000\u0000\u0000gh\u0005\u0000\u0000\u0001h\u0017\u0001"+
		"\u0000\u0000\u0000\u0003\\^e";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}