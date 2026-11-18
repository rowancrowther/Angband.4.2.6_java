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

// Generated from BlowEffect2Grammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.bloweffect2;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.backend.parser.bloweffect.BlowEffect2ParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.Directive;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class BlowEffect2Grammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, POWER=2, EVAL=3, DESC=4, LORE_COLOUR_BASE=5, LORE_COLOUR_RESIST=6, 
		LORE_COLOUR_IMMUNE=7, EFFECT_TYPE=8, RESIST=9, LASH=10, INTEGER=11, COMMENT=12, 
		INDENT=13, EOL=14, STRING=15, EOL_POP=16;
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
			null, "'name:'", "'power:'", "'eval:'", "'desc:'", "'lore-color-base:'", 
			"'lore-color-resist:'", "'lore-color-immune:'", "'effect-type:'", "'resist:'", 
			"'lash-type:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "POWER", "EVAL", "DESC", "LORE_COLOUR_BASE", "LORE_COLOUR_RESIST", 
			"LORE_COLOUR_IMMUNE", "EFFECT_TYPE", "RESIST", "LASH", "INTEGER", "COMMENT", 
			"INDENT", "EOL", "STRING", "EOL_POP"
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
	public String getGrammarFileName() { return "BlowEffect2Grammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public BlowEffect2Grammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public Directive nameDirective;
		public Token STRING;
		public TerminalNode NAME() { return getToken(BlowEffect2Grammar.NAME, 0); }
		public TerminalNode STRING() { return getToken(BlowEffect2Grammar.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffect2GrammarListener ) ((BlowEffect2GrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffect2GrammarListener ) ((BlowEffect2GrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowEffect2GrammarVisitor ) return ((BlowEffect2GrammarVisitor<? extends T>)visitor).visitName(this);
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
			((NameContext)_localctx).STRING = match(STRING);
			 
			                String name = ((NameContext)_localctx).STRING.getText();
			                List<String> values = new ArrayList<>();
			                values.add(name);
			                ((NameContext)_localctx).nameDirective =  new Directive("name", values, _localctx.start.getLine()); 
			            
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
		public Directive powerDirective;
		public Token INTEGER;
		public TerminalNode POWER() { return getToken(BlowEffect2Grammar.POWER, 0); }
		public TerminalNode INTEGER() { return getToken(BlowEffect2Grammar.INTEGER, 0); }
		public PowerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_power; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffect2GrammarListener ) ((BlowEffect2GrammarListener)listener).enterPower(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffect2GrammarListener ) ((BlowEffect2GrammarListener)listener).exitPower(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowEffect2GrammarVisitor ) return ((BlowEffect2GrammarVisitor<? extends T>)visitor).visitPower(this);
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
			 
			                String powerVal = ((PowerContext)_localctx).INTEGER.getText();
			                List<String> values = new ArrayList<>();
			                values.add(powerVal);
			                ((PowerContext)_localctx).powerDirective =  new Directive("power", values, _localctx.start.getLine()); 
			            
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
		public Directive evalDirective;
		public Token INTEGER;
		public TerminalNode EVAL() { return getToken(BlowEffect2Grammar.EVAL, 0); }
		public TerminalNode INTEGER() { return getToken(BlowEffect2Grammar.INTEGER, 0); }
		public EvalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eval; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffect2GrammarListener ) ((BlowEffect2GrammarListener)listener).enterEval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffect2GrammarListener ) ((BlowEffect2GrammarListener)listener).exitEval(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowEffect2GrammarVisitor ) return ((BlowEffect2GrammarVisitor<? extends T>)visitor).visitEval(this);
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

			                String evalVal = ((EvalContext)_localctx).INTEGER.getText();
			                List<String> values = new ArrayList<>();
			                values.add(evalVal);
			                ((EvalContext)_localctx).evalDirective =  new Directive("eval", values, _localctx.start.getLine());
			            
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
		public Directive descDirective;
		public Token STRING;
		public TerminalNode DESC() { return getToken(BlowEffect2Grammar.DESC, 0); }
		public TerminalNode STRING() { return getToken(BlowEffect2Grammar.STRING, 0); }
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_desc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffect2GrammarListener ) ((BlowEffect2GrammarListener)listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffect2GrammarListener ) ((BlowEffect2GrammarListener)listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowEffect2GrammarVisitor ) return ((BlowEffect2GrammarVisitor<? extends T>)visitor).visitDesc(this);
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
			((DescContext)_localctx).STRING = match(STRING);

			                String descStr = ((DescContext)_localctx).STRING.getText();
			                List<String> values = new ArrayList<>();
			                values.add(descStr);
			                ((DescContext)_localctx).descDirective =  new Directive("desc", values, _localctx.start.getLine());
			            
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
		public Directive baseDirective;
		public Token STRING;
		public TerminalNode LORE_COLOUR_BASE() { return getToken(BlowEffect2Grammar.LORE_COLOUR_BASE, 0); }
		public TerminalNode STRING() { return getToken(BlowEffect2Grammar.STRING, 0); }
		public LoreColourBaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_loreColourBase; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffect2GrammarListener ) ((BlowEffect2GrammarListener)listener).enterLoreColourBase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffect2GrammarListener ) ((BlowEffect2GrammarListener)listener).exitLoreColourBase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowEffect2GrammarVisitor ) return ((BlowEffect2GrammarVisitor<? extends T>)visitor).visitLoreColourBase(this);
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
			((LoreColourBaseContext)_localctx).STRING = match(STRING);
			 
			                String colourStr = ((LoreColourBaseContext)_localctx).STRING.getText();
			                List<String> values = new ArrayList<>();
			                values.add(colourStr);
			                ((LoreColourBaseContext)_localctx).baseDirective =  new Directive("base-colour", values, _localctx.start.getLine());
			            
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
		public Directive resistColourDirective;
		public Token STRING;
		public TerminalNode LORE_COLOUR_RESIST() { return getToken(BlowEffect2Grammar.LORE_COLOUR_RESIST, 0); }
		public TerminalNode STRING() { return getToken(BlowEffect2Grammar.STRING, 0); }
		public LoreColourResistContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_loreColourResist; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffect2GrammarListener ) ((BlowEffect2GrammarListener)listener).enterLoreColourResist(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffect2GrammarListener ) ((BlowEffect2GrammarListener)listener).exitLoreColourResist(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowEffect2GrammarVisitor ) return ((BlowEffect2GrammarVisitor<? extends T>)visitor).visitLoreColourResist(this);
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
			((LoreColourResistContext)_localctx).STRING = match(STRING);
			 
			                String colourStr = ((LoreColourResistContext)_localctx).STRING.getText();
			                List<String> values = new ArrayList<>();
			                values.add(colourStr);
			                ((LoreColourResistContext)_localctx).resistColourDirective =  new Directive("resist-colour", values, _localctx.start.getLine());
			            
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
		public Directive immuneDirective;
		public Token STRING;
		public TerminalNode LORE_COLOUR_IMMUNE() { return getToken(BlowEffect2Grammar.LORE_COLOUR_IMMUNE, 0); }
		public TerminalNode STRING() { return getToken(BlowEffect2Grammar.STRING, 0); }
		public LoreColourImmuneContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_loreColourImmune; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffect2GrammarListener ) ((BlowEffect2GrammarListener)listener).enterLoreColourImmune(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffect2GrammarListener ) ((BlowEffect2GrammarListener)listener).exitLoreColourImmune(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowEffect2GrammarVisitor ) return ((BlowEffect2GrammarVisitor<? extends T>)visitor).visitLoreColourImmune(this);
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
			((LoreColourImmuneContext)_localctx).STRING = match(STRING);

			                String colourStr = ((LoreColourImmuneContext)_localctx).STRING.getText();
			                List<String> values = new ArrayList<>();
			                values.add(colourStr);
			                ((LoreColourImmuneContext)_localctx).immuneDirective =  new Directive("immune-colour", values, _localctx.start.getLine());
			            
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
		public Directive effectDirective;
		public Token STRING;
		public TerminalNode EFFECT_TYPE() { return getToken(BlowEffect2Grammar.EFFECT_TYPE, 0); }
		public TerminalNode STRING() { return getToken(BlowEffect2Grammar.STRING, 0); }
		public EffectTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effectType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffect2GrammarListener ) ((BlowEffect2GrammarListener)listener).enterEffectType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffect2GrammarListener ) ((BlowEffect2GrammarListener)listener).exitEffectType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowEffect2GrammarVisitor ) return ((BlowEffect2GrammarVisitor<? extends T>)visitor).visitEffectType(this);
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
			((EffectTypeContext)_localctx).STRING = match(STRING);

			                String type = ((EffectTypeContext)_localctx).STRING.getText();
			                List<String> values = new ArrayList<>();
			                values.add(type);
			                ((EffectTypeContext)_localctx).effectDirective =  new Directive("effect", values, _localctx.start.getLine());
			            
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
		public Directive resistDirective;
		public Token STRING;
		public TerminalNode RESIST() { return getToken(BlowEffect2Grammar.RESIST, 0); }
		public TerminalNode STRING() { return getToken(BlowEffect2Grammar.STRING, 0); }
		public ResistContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_resist; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffect2GrammarListener ) ((BlowEffect2GrammarListener)listener).enterResist(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffect2GrammarListener ) ((BlowEffect2GrammarListener)listener).exitResist(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowEffect2GrammarVisitor ) return ((BlowEffect2GrammarVisitor<? extends T>)visitor).visitResist(this);
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
			((ResistContext)_localctx).STRING = match(STRING);

			                String resistStr = ((ResistContext)_localctx).STRING.getText();
			                List<String> values = new ArrayList<>();
			                values.add(resistStr);
			                ((ResistContext)_localctx).resistDirective =  new Directive("resist", values, _localctx.start.getLine());
			            
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
		public Directive lashDirective;
		public Token STRING;
		public TerminalNode LASH() { return getToken(BlowEffect2Grammar.LASH, 0); }
		public TerminalNode STRING() { return getToken(BlowEffect2Grammar.STRING, 0); }
		public LashTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lashType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffect2GrammarListener ) ((BlowEffect2GrammarListener)listener).enterLashType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffect2GrammarListener ) ((BlowEffect2GrammarListener)listener).exitLashType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowEffect2GrammarVisitor ) return ((BlowEffect2GrammarVisitor<? extends T>)visitor).visitLashType(this);
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
			match(LASH);
			setState(61);
			((LashTypeContext)_localctx).STRING = match(STRING);

			                String lashStr = ((LashTypeContext)_localctx).STRING.getText();
			                List<String> values = new ArrayList<>();
			                values.add(lashStr);
			                ((LashTypeContext)_localctx).lashDirective =  new Directive("lash", values, _localctx.start.getLine());
			            
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
		public BlowEffect2ParseRecord effect;
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
		public List<PowerContext> power() {
			return getRuleContexts(PowerContext.class);
		}
		public PowerContext power(int i) {
			return getRuleContext(PowerContext.class,i);
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
			if ( listener instanceof BlowEffect2GrammarListener ) ((BlowEffect2GrammarListener)listener).enterBlowEffect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffect2GrammarListener ) ((BlowEffect2GrammarListener)listener).exitBlowEffect(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowEffect2GrammarVisitor ) return ((BlowEffect2GrammarVisitor<? extends T>)visitor).visitBlowEffect(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlowEffectContext blowEffect() throws RecognitionException {
		BlowEffectContext _localctx = new BlowEffectContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_blowEffect);

		            List<Directive> directives = new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64);
			((BlowEffectContext)_localctx).name = name();
			 directives.add(((BlowEffectContext)_localctx).name.nameDirective); 
			setState(95);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 2044L) != 0)) {
				{
				setState(93);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case POWER:
					{
					setState(66);
					((BlowEffectContext)_localctx).power = power();
					 directives.add(((BlowEffectContext)_localctx).power.powerDirective); 
					}
					break;
				case EVAL:
					{
					setState(69);
					((BlowEffectContext)_localctx).eval = eval();
					 directives.add(((BlowEffectContext)_localctx).eval.evalDirective); 
					}
					break;
				case DESC:
					{
					setState(72);
					((BlowEffectContext)_localctx).desc = desc();
					 directives.add(((BlowEffectContext)_localctx).desc.descDirective); 
					}
					break;
				case LORE_COLOUR_BASE:
					{
					setState(75);
					((BlowEffectContext)_localctx).loreColourBase = loreColourBase();
					 directives.add(((BlowEffectContext)_localctx).loreColourBase.baseDirective); 
					}
					break;
				case LORE_COLOUR_RESIST:
					{
					setState(78);
					((BlowEffectContext)_localctx).loreColourResist = loreColourResist();
					 directives.add(((BlowEffectContext)_localctx).loreColourResist.resistColourDirective); 
					}
					break;
				case LORE_COLOUR_IMMUNE:
					{
					setState(81);
					((BlowEffectContext)_localctx).loreColourImmune = loreColourImmune();
					 directives.add(((BlowEffectContext)_localctx).loreColourImmune.immuneDirective); 
					}
					break;
				case EFFECT_TYPE:
					{
					setState(84);
					((BlowEffectContext)_localctx).effectType = effectType();
					 directives.add(((BlowEffectContext)_localctx).effectType.effectDirective); 
					}
					break;
				case RESIST:
					{
					setState(87);
					((BlowEffectContext)_localctx).resist = resist();
					 directives.add(((BlowEffectContext)_localctx).resist.resistDirective); 
					}
					break;
				case LASH:
					{
					setState(90);
					((BlowEffectContext)_localctx).lashType = lashType();
					 directives.add(((BlowEffectContext)_localctx).lashType.lashDirective); 
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

			            ((BlowEffectContext)_localctx).effect =  new BlowEffect2ParseRecord(directives);
			        
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
		public List<BlowEffect2ParseRecord> blowEffects;
		public BlowEffectContext blowEffect;
		public TerminalNode EOF() { return getToken(BlowEffect2Grammar.EOF, 0); }
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
			if ( listener instanceof BlowEffect2GrammarListener ) ((BlowEffect2GrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BlowEffect2GrammarListener ) ((BlowEffect2GrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BlowEffect2GrammarVisitor ) return ((BlowEffect2GrammarVisitor<? extends T>)visitor).visitFile(this);
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
			setState(103);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NAME) {
				{
				{
				setState(98);
				((FileContext)_localctx).blowEffect = blowEffect();
				 _localctx.blowEffects.add(((FileContext)_localctx).blowEffect.effect); 
				}
				}
				setState(105);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
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

	public static final String _serializedATN =
		"\u0004\u0001\u0010m\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
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
		"\n\u0001\n\u0001\n\u0001\n\u0005\n^\b\n\n\n\f\na\t\n\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0005\u000bf\b\u000b\n\u000b\f\u000bi\t\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0000\u0000\f\u0000\u0002\u0004\u0006\b"+
		"\n\f\u000e\u0010\u0012\u0014\u0016\u0000\u0000j\u0000\u0018\u0001\u0000"+
		"\u0000\u0000\u0002\u001c\u0001\u0000\u0000\u0000\u0004 \u0001\u0000\u0000"+
		"\u0000\u0006$\u0001\u0000\u0000\u0000\b(\u0001\u0000\u0000\u0000\n,\u0001"+
		"\u0000\u0000\u0000\f0\u0001\u0000\u0000\u0000\u000e4\u0001\u0000\u0000"+
		"\u0000\u00108\u0001\u0000\u0000\u0000\u0012<\u0001\u0000\u0000\u0000\u0014"+
		"@\u0001\u0000\u0000\u0000\u0016g\u0001\u0000\u0000\u0000\u0018\u0019\u0005"+
		"\u0001\u0000\u0000\u0019\u001a\u0005\u000f\u0000\u0000\u001a\u001b\u0006"+
		"\u0000\uffff\uffff\u0000\u001b\u0001\u0001\u0000\u0000\u0000\u001c\u001d"+
		"\u0005\u0002\u0000\u0000\u001d\u001e\u0005\u000b\u0000\u0000\u001e\u001f"+
		"\u0006\u0001\uffff\uffff\u0000\u001f\u0003\u0001\u0000\u0000\u0000 !\u0005"+
		"\u0003\u0000\u0000!\"\u0005\u000b\u0000\u0000\"#\u0006\u0002\uffff\uffff"+
		"\u0000#\u0005\u0001\u0000\u0000\u0000$%\u0005\u0004\u0000\u0000%&\u0005"+
		"\u000f\u0000\u0000&\'\u0006\u0003\uffff\uffff\u0000\'\u0007\u0001\u0000"+
		"\u0000\u0000()\u0005\u0005\u0000\u0000)*\u0005\u000f\u0000\u0000*+\u0006"+
		"\u0004\uffff\uffff\u0000+\t\u0001\u0000\u0000\u0000,-\u0005\u0006\u0000"+
		"\u0000-.\u0005\u000f\u0000\u0000./\u0006\u0005\uffff\uffff\u0000/\u000b"+
		"\u0001\u0000\u0000\u000001\u0005\u0007\u0000\u000012\u0005\u000f\u0000"+
		"\u000023\u0006\u0006\uffff\uffff\u00003\r\u0001\u0000\u0000\u000045\u0005"+
		"\b\u0000\u000056\u0005\u000f\u0000\u000067\u0006\u0007\uffff\uffff\u0000"+
		"7\u000f\u0001\u0000\u0000\u000089\u0005\t\u0000\u00009:\u0005\u000f\u0000"+
		"\u0000:;\u0006\b\uffff\uffff\u0000;\u0011\u0001\u0000\u0000\u0000<=\u0005"+
		"\n\u0000\u0000=>\u0005\u000f\u0000\u0000>?\u0006\t\uffff\uffff\u0000?"+
		"\u0013\u0001\u0000\u0000\u0000@A\u0003\u0000\u0000\u0000A_\u0006\n\uffff"+
		"\uffff\u0000BC\u0003\u0002\u0001\u0000CD\u0006\n\uffff\uffff\u0000D^\u0001"+
		"\u0000\u0000\u0000EF\u0003\u0004\u0002\u0000FG\u0006\n\uffff\uffff\u0000"+
		"G^\u0001\u0000\u0000\u0000HI\u0003\u0006\u0003\u0000IJ\u0006\n\uffff\uffff"+
		"\u0000J^\u0001\u0000\u0000\u0000KL\u0003\b\u0004\u0000LM\u0006\n\uffff"+
		"\uffff\u0000M^\u0001\u0000\u0000\u0000NO\u0003\n\u0005\u0000OP\u0006\n"+
		"\uffff\uffff\u0000P^\u0001\u0000\u0000\u0000QR\u0003\f\u0006\u0000RS\u0006"+
		"\n\uffff\uffff\u0000S^\u0001\u0000\u0000\u0000TU\u0003\u000e\u0007\u0000"+
		"UV\u0006\n\uffff\uffff\u0000V^\u0001\u0000\u0000\u0000WX\u0003\u0010\b"+
		"\u0000XY\u0006\n\uffff\uffff\u0000Y^\u0001\u0000\u0000\u0000Z[\u0003\u0012"+
		"\t\u0000[\\\u0006\n\uffff\uffff\u0000\\^\u0001\u0000\u0000\u0000]B\u0001"+
		"\u0000\u0000\u0000]E\u0001\u0000\u0000\u0000]H\u0001\u0000\u0000\u0000"+
		"]K\u0001\u0000\u0000\u0000]N\u0001\u0000\u0000\u0000]Q\u0001\u0000\u0000"+
		"\u0000]T\u0001\u0000\u0000\u0000]W\u0001\u0000\u0000\u0000]Z\u0001\u0000"+
		"\u0000\u0000^a\u0001\u0000\u0000\u0000_]\u0001\u0000\u0000\u0000_`\u0001"+
		"\u0000\u0000\u0000`\u0015\u0001\u0000\u0000\u0000a_\u0001\u0000\u0000"+
		"\u0000bc\u0003\u0014\n\u0000cd\u0006\u000b\uffff\uffff\u0000df\u0001\u0000"+
		"\u0000\u0000eb\u0001\u0000\u0000\u0000fi\u0001\u0000\u0000\u0000ge\u0001"+
		"\u0000\u0000\u0000gh\u0001\u0000\u0000\u0000hj\u0001\u0000\u0000\u0000"+
		"ig\u0001\u0000\u0000\u0000jk\u0005\u0000\u0000\u0001k\u0017\u0001\u0000"+
		"\u0000\u0000\u0003]_g";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}