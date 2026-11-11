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

// Generated from MonsterSpellGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.monsterspell;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.backend.parser.grammars.EffectParseRecord;
import uk.co.jackoftradesltd.backend.parser.monsterspell.MonsterSpellParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class MonsterSpellGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, MSGT=2, HIT=3, POWER_CUTOFF=4, LORE=5, LORE_COLOUR_BASE=6, LORE_COLOUR_RESIST=7, 
		LORE_COLOUR_IMMUNE=8, MESSAGE_SAVE=9, MESSAGE_VIS=10, MESSAGE_INVIS=11, 
		MESSAGE_MISS=12, COMMENT=13, INDENT=14, EOL=15, EFFECT=16, EFFECT_MESSAGE=17, 
		DICE=18, TIME=19, EFFECT_YX=20, EXPR=21, COLON=22, UCASE=23, INTEGER=24, 
		SIMPLE_DICE_STRING=25, COMPLEX_DICE_STRING=26, STRING=27, FREE_TEXT_EOL=28, 
		FREE_TEXT=29, DICE_SIMPLE_VALUE=30, DICE_COMPLEX_VALUE=31, EXPR_CHAR=32, 
		EXPR_COLON=33, EXPR_UCASE=34, EXPR_OP=35, EXPR_EOL=36;
	public static final int
		RULE_name = 0, RULE_msgt = 1, RULE_hit = 2, RULE_powerCutoff = 3, RULE_lore = 4, 
		RULE_loreColourBase = 5, RULE_loreColourResist = 6, RULE_loreColourImmune = 7, 
		RULE_messageSave = 8, RULE_messageVis = 9, RULE_messageInvis = 10, RULE_messageMiss = 11, 
		RULE_levelDirectives = 12, RULE_powerCutoffBlock = 13, RULE_monsterSpell = 14, 
		RULE_file = 15, RULE_effect = 16, RULE_effectYX = 17, RULE_dice = 18, 
		RULE_expr = 19, RULE_effectMsg = 20, RULE_time = 21, RULE_strayEffectLine = 22, 
		RULE_effectBlock = 23;
	private static String[] makeRuleNames() {
		return new String[] {
			"name", "msgt", "hit", "powerCutoff", "lore", "loreColourBase", "loreColourResist", 
			"loreColourImmune", "messageSave", "messageVis", "messageInvis", "messageMiss", 
			"levelDirectives", "powerCutoffBlock", "monsterSpell", "file", "effect", 
			"effectYX", "dice", "expr", "effectMsg", "time", "strayEffectLine", "effectBlock"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'msgt:'", "'hit:'", "'power-cutoff:'", "'lore:'", "'lore-color-base:'", 
			"'lore-color-resist:'", "'lore-color-immune:'", "'message-save:'", "'message-vis:'", 
			"'message-invis:'", "'message-miss:'", null, null, null, "'effect:'", 
			"'effect-msg:'", "'dice:'", "'time:'", "'effect-yx:'", "'expr:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "MSGT", "HIT", "POWER_CUTOFF", "LORE", "LORE_COLOUR_BASE", 
			"LORE_COLOUR_RESIST", "LORE_COLOUR_IMMUNE", "MESSAGE_SAVE", "MESSAGE_VIS", 
			"MESSAGE_INVIS", "MESSAGE_MISS", "COMMENT", "INDENT", "EOL", "EFFECT", 
			"EFFECT_MESSAGE", "DICE", "TIME", "EFFECT_YX", "EXPR", "COLON", "UCASE", 
			"INTEGER", "SIMPLE_DICE_STRING", "COMPLEX_DICE_STRING", "STRING", "FREE_TEXT_EOL", 
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
	public String getGrammarFileName() { return "MonsterSpellGrammar.g4"; }

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
	         
	public MonsterSpellGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public int line;
		public Token STRING;
		public TerminalNode NAME() { return getToken(MonsterSpellGrammar.NAME, 0); }
		public TerminalNode STRING() { return getToken(MonsterSpellGrammar.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterSpellGrammarVisitor ) return ((MonsterSpellGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			match(NAME);
			setState(49);
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
	public static class MsgtContext extends ParserRuleContext {
		public String msgTStr;
		public Token STRING;
		public TerminalNode MSGT() { return getToken(MonsterSpellGrammar.MSGT, 0); }
		public TerminalNode STRING() { return getToken(MonsterSpellGrammar.STRING, 0); }
		public MsgtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_msgt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).enterMsgt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).exitMsgt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterSpellGrammarVisitor ) return ((MonsterSpellGrammarVisitor<? extends T>)visitor).visitMsgt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MsgtContext msgt() throws RecognitionException {
		MsgtContext _localctx = new MsgtContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_msgt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			match(MSGT);
			setState(53);
			((MsgtContext)_localctx).STRING = match(STRING);
			 ((MsgtContext)_localctx).msgTStr =  ((MsgtContext)_localctx).STRING.getText(); 
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
	public static class HitContext extends ParserRuleContext {
		public String hitVal;
		public Token INTEGER;
		public TerminalNode HIT() { return getToken(MonsterSpellGrammar.HIT, 0); }
		public TerminalNode INTEGER() { return getToken(MonsterSpellGrammar.INTEGER, 0); }
		public HitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).enterHit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).exitHit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterSpellGrammarVisitor ) return ((MonsterSpellGrammarVisitor<? extends T>)visitor).visitHit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HitContext hit() throws RecognitionException {
		HitContext _localctx = new HitContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_hit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			match(HIT);
			setState(57);
			((HitContext)_localctx).INTEGER = match(INTEGER);
			 ((HitContext)_localctx).hitVal =  ((HitContext)_localctx).INTEGER.getText(); 
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
	public static class PowerCutoffContext extends ParserRuleContext {
		public String cutoffVal;
		public Token INTEGER;
		public TerminalNode POWER_CUTOFF() { return getToken(MonsterSpellGrammar.POWER_CUTOFF, 0); }
		public TerminalNode INTEGER() { return getToken(MonsterSpellGrammar.INTEGER, 0); }
		public PowerCutoffContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_powerCutoff; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).enterPowerCutoff(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).exitPowerCutoff(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterSpellGrammarVisitor ) return ((MonsterSpellGrammarVisitor<? extends T>)visitor).visitPowerCutoff(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PowerCutoffContext powerCutoff() throws RecognitionException {
		PowerCutoffContext _localctx = new PowerCutoffContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_powerCutoff);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(60);
			match(POWER_CUTOFF);
			setState(61);
			((PowerCutoffContext)_localctx).INTEGER = match(INTEGER);
			 ((PowerCutoffContext)_localctx).cutoffVal =  ((PowerCutoffContext)_localctx).INTEGER.getText(); 
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
	public static class LoreContext extends ParserRuleContext {
		public String loreStr;
		public Token STRING;
		public TerminalNode LORE() { return getToken(MonsterSpellGrammar.LORE, 0); }
		public TerminalNode STRING() { return getToken(MonsterSpellGrammar.STRING, 0); }
		public LoreContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lore; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).enterLore(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).exitLore(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterSpellGrammarVisitor ) return ((MonsterSpellGrammarVisitor<? extends T>)visitor).visitLore(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LoreContext lore() throws RecognitionException {
		LoreContext _localctx = new LoreContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_lore);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64);
			match(LORE);
			setState(65);
			((LoreContext)_localctx).STRING = match(STRING);
			 ((LoreContext)_localctx).loreStr =  ((LoreContext)_localctx).STRING.getText(); 
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
		public String colourBase;
		public Token STRING;
		public TerminalNode LORE_COLOUR_BASE() { return getToken(MonsterSpellGrammar.LORE_COLOUR_BASE, 0); }
		public TerminalNode STRING() { return getToken(MonsterSpellGrammar.STRING, 0); }
		public LoreColourBaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_loreColourBase; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).enterLoreColourBase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).exitLoreColourBase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterSpellGrammarVisitor ) return ((MonsterSpellGrammarVisitor<? extends T>)visitor).visitLoreColourBase(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LoreColourBaseContext loreColourBase() throws RecognitionException {
		LoreColourBaseContext _localctx = new LoreColourBaseContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_loreColourBase);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(68);
			match(LORE_COLOUR_BASE);
			setState(69);
			((LoreColourBaseContext)_localctx).STRING = match(STRING);
			 ((LoreColourBaseContext)_localctx).colourBase =  ((LoreColourBaseContext)_localctx).STRING.getText(); 
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
		public String colourResist;
		public Token STRING;
		public TerminalNode LORE_COLOUR_RESIST() { return getToken(MonsterSpellGrammar.LORE_COLOUR_RESIST, 0); }
		public TerminalNode STRING() { return getToken(MonsterSpellGrammar.STRING, 0); }
		public LoreColourResistContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_loreColourResist; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).enterLoreColourResist(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).exitLoreColourResist(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterSpellGrammarVisitor ) return ((MonsterSpellGrammarVisitor<? extends T>)visitor).visitLoreColourResist(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LoreColourResistContext loreColourResist() throws RecognitionException {
		LoreColourResistContext _localctx = new LoreColourResistContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_loreColourResist);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			match(LORE_COLOUR_RESIST);
			setState(73);
			((LoreColourResistContext)_localctx).STRING = match(STRING);
			 ((LoreColourResistContext)_localctx).colourResist =  ((LoreColourResistContext)_localctx).STRING.getText(); 
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
		public String colourImmune;
		public Token STRING;
		public TerminalNode LORE_COLOUR_IMMUNE() { return getToken(MonsterSpellGrammar.LORE_COLOUR_IMMUNE, 0); }
		public TerminalNode STRING() { return getToken(MonsterSpellGrammar.STRING, 0); }
		public LoreColourImmuneContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_loreColourImmune; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).enterLoreColourImmune(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).exitLoreColourImmune(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterSpellGrammarVisitor ) return ((MonsterSpellGrammarVisitor<? extends T>)visitor).visitLoreColourImmune(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LoreColourImmuneContext loreColourImmune() throws RecognitionException {
		LoreColourImmuneContext _localctx = new LoreColourImmuneContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_loreColourImmune);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(76);
			match(LORE_COLOUR_IMMUNE);
			setState(77);
			((LoreColourImmuneContext)_localctx).STRING = match(STRING);
			 ((LoreColourImmuneContext)_localctx).colourImmune =  ((LoreColourImmuneContext)_localctx).STRING.getText(); 
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
	public static class MessageSaveContext extends ParserRuleContext {
		public String saveMsg;
		public Token STRING;
		public TerminalNode MESSAGE_SAVE() { return getToken(MonsterSpellGrammar.MESSAGE_SAVE, 0); }
		public TerminalNode STRING() { return getToken(MonsterSpellGrammar.STRING, 0); }
		public MessageSaveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messageSave; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).enterMessageSave(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).exitMessageSave(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterSpellGrammarVisitor ) return ((MonsterSpellGrammarVisitor<? extends T>)visitor).visitMessageSave(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MessageSaveContext messageSave() throws RecognitionException {
		MessageSaveContext _localctx = new MessageSaveContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_messageSave);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
			match(MESSAGE_SAVE);
			setState(81);
			((MessageSaveContext)_localctx).STRING = match(STRING);
			 ((MessageSaveContext)_localctx).saveMsg =  ((MessageSaveContext)_localctx).STRING.getText(); 
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
	public static class MessageVisContext extends ParserRuleContext {
		public String visMsg;
		public Token STRING;
		public TerminalNode MESSAGE_VIS() { return getToken(MonsterSpellGrammar.MESSAGE_VIS, 0); }
		public TerminalNode STRING() { return getToken(MonsterSpellGrammar.STRING, 0); }
		public MessageVisContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messageVis; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).enterMessageVis(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).exitMessageVis(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterSpellGrammarVisitor ) return ((MonsterSpellGrammarVisitor<? extends T>)visitor).visitMessageVis(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MessageVisContext messageVis() throws RecognitionException {
		MessageVisContext _localctx = new MessageVisContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_messageVis);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(84);
			match(MESSAGE_VIS);
			setState(85);
			((MessageVisContext)_localctx).STRING = match(STRING);
			 ((MessageVisContext)_localctx).visMsg =  ((MessageVisContext)_localctx).STRING.getText(); 
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
	public static class MessageInvisContext extends ParserRuleContext {
		public String invisMsg;
		public Token STRING;
		public TerminalNode MESSAGE_INVIS() { return getToken(MonsterSpellGrammar.MESSAGE_INVIS, 0); }
		public TerminalNode STRING() { return getToken(MonsterSpellGrammar.STRING, 0); }
		public MessageInvisContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messageInvis; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).enterMessageInvis(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).exitMessageInvis(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterSpellGrammarVisitor ) return ((MonsterSpellGrammarVisitor<? extends T>)visitor).visitMessageInvis(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MessageInvisContext messageInvis() throws RecognitionException {
		MessageInvisContext _localctx = new MessageInvisContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_messageInvis);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(88);
			match(MESSAGE_INVIS);
			setState(89);
			((MessageInvisContext)_localctx).STRING = match(STRING);
			 ((MessageInvisContext)_localctx).invisMsg =  ((MessageInvisContext)_localctx).STRING.getText(); 
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
	public static class MessageMissContext extends ParserRuleContext {
		public String missMsg;
		public Token STRING;
		public TerminalNode MESSAGE_MISS() { return getToken(MonsterSpellGrammar.MESSAGE_MISS, 0); }
		public TerminalNode STRING() { return getToken(MonsterSpellGrammar.STRING, 0); }
		public MessageMissContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messageMiss; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).enterMessageMiss(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).exitMessageMiss(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterSpellGrammarVisitor ) return ((MonsterSpellGrammarVisitor<? extends T>)visitor).visitMessageMiss(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MessageMissContext messageMiss() throws RecognitionException {
		MessageMissContext _localctx = new MessageMissContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_messageMiss);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(92);
			match(MESSAGE_MISS);
			setState(93);
			((MessageMissContext)_localctx).STRING = match(STRING);
			 ((MessageMissContext)_localctx).missMsg =  ((MessageMissContext)_localctx).STRING.getText(); 
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
	public static class LevelDirectivesContext extends ParserRuleContext {
		public String loreStr;
		public String loreColourBaseStr;
		public String loreColourResistStr;
		public String loreColourImmuneStr;
		public String messageVisStr;
		public String messageInvisStr;
		public String messageMissStr;
		public String messageSaveStr;
		public Integer line;
		public LoreContext lore;
		public LoreColourBaseContext loreColourBase;
		public LoreColourResistContext loreColourResist;
		public LoreColourImmuneContext loreColourImmune;
		public MessageVisContext messageVis;
		public MessageInvisContext messageInvis;
		public MessageMissContext messageMiss;
		public MessageSaveContext messageSave;
		public List<LoreContext> lore() {
			return getRuleContexts(LoreContext.class);
		}
		public LoreContext lore(int i) {
			return getRuleContext(LoreContext.class,i);
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
		public List<MessageVisContext> messageVis() {
			return getRuleContexts(MessageVisContext.class);
		}
		public MessageVisContext messageVis(int i) {
			return getRuleContext(MessageVisContext.class,i);
		}
		public List<MessageInvisContext> messageInvis() {
			return getRuleContexts(MessageInvisContext.class);
		}
		public MessageInvisContext messageInvis(int i) {
			return getRuleContext(MessageInvisContext.class,i);
		}
		public List<MessageMissContext> messageMiss() {
			return getRuleContexts(MessageMissContext.class);
		}
		public MessageMissContext messageMiss(int i) {
			return getRuleContext(MessageMissContext.class,i);
		}
		public List<MessageSaveContext> messageSave() {
			return getRuleContexts(MessageSaveContext.class);
		}
		public MessageSaveContext messageSave(int i) {
			return getRuleContext(MessageSaveContext.class,i);
		}
		public LevelDirectivesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_levelDirectives; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).enterLevelDirectives(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).exitLevelDirectives(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterSpellGrammarVisitor ) return ((MonsterSpellGrammarVisitor<? extends T>)visitor).visitLevelDirectives(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LevelDirectivesContext levelDirectives() throws RecognitionException {
		LevelDirectivesContext _localctx = new LevelDirectivesContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_levelDirectives);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			     ((LevelDirectivesContext)_localctx).line =  _localctx.start.getLine();
			                  ((LevelDirectivesContext)_localctx).loreStr =  "";
			                  ((LevelDirectivesContext)_localctx).loreColourBaseStr =  null;
			                  ((LevelDirectivesContext)_localctx).loreColourResistStr =  null;
			                  ((LevelDirectivesContext)_localctx).loreColourImmuneStr =  null;
			                  ((LevelDirectivesContext)_localctx).messageVisStr =  "";
			                  ((LevelDirectivesContext)_localctx).messageInvisStr =  "";
			                  ((LevelDirectivesContext)_localctx).messageMissStr =  "";
			                  ((LevelDirectivesContext)_localctx).messageSaveStr =  ""; 
			setState(121); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					setState(121);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case LORE:
						{
						setState(97);
						((LevelDirectivesContext)_localctx).lore = lore();
						 ((LevelDirectivesContext)_localctx).loreStr =  _localctx.loreStr + ((LevelDirectivesContext)_localctx).lore.loreStr; 
						}
						break;
					case LORE_COLOUR_BASE:
						{
						setState(100);
						((LevelDirectivesContext)_localctx).loreColourBase = loreColourBase();
						 ((LevelDirectivesContext)_localctx).loreColourBaseStr =  ((LevelDirectivesContext)_localctx).loreColourBase.colourBase; 
						}
						break;
					case LORE_COLOUR_RESIST:
						{
						setState(103);
						((LevelDirectivesContext)_localctx).loreColourResist = loreColourResist();
						 ((LevelDirectivesContext)_localctx).loreColourResistStr =  ((LevelDirectivesContext)_localctx).loreColourResist.colourResist; 
						}
						break;
					case LORE_COLOUR_IMMUNE:
						{
						setState(106);
						((LevelDirectivesContext)_localctx).loreColourImmune = loreColourImmune();
						 ((LevelDirectivesContext)_localctx).loreColourImmuneStr =   ((LevelDirectivesContext)_localctx).loreColourImmune.colourImmune; 
						}
						break;
					case MESSAGE_VIS:
						{
						setState(109);
						((LevelDirectivesContext)_localctx).messageVis = messageVis();
						 ((LevelDirectivesContext)_localctx).messageVisStr =  _localctx.messageVisStr + ((LevelDirectivesContext)_localctx).messageVis.visMsg; 
						}
						break;
					case MESSAGE_INVIS:
						{
						setState(112);
						((LevelDirectivesContext)_localctx).messageInvis = messageInvis();
						 ((LevelDirectivesContext)_localctx).messageInvisStr =  _localctx.messageInvisStr + ((LevelDirectivesContext)_localctx).messageInvis.invisMsg; 
						}
						break;
					case MESSAGE_MISS:
						{
						setState(115);
						((LevelDirectivesContext)_localctx).messageMiss = messageMiss();
						 ((LevelDirectivesContext)_localctx).messageMissStr =  _localctx.messageMissStr + ((LevelDirectivesContext)_localctx).messageMiss.missMsg; 
						}
						break;
					case MESSAGE_SAVE:
						{
						setState(118);
						((LevelDirectivesContext)_localctx).messageSave = messageSave();
						 ((LevelDirectivesContext)_localctx).messageSaveStr =  _localctx.messageSaveStr + ((LevelDirectivesContext)_localctx).messageSave.saveMsg; 
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(123); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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
	public static class PowerCutoffBlockContext extends ParserRuleContext {
		public String powerCutOffVal;
		public Integer line;
		public String loreStr;
		public String loreColourBaseStr;
		public String loreColourResistStr;
		public String loreColourImmuneStr;
		public String messageVisStr;
		public String messageInvisStr;
		public String messageMissStr;
		public String messageSaveStr;
		public PowerCutoffContext powerCutoff;
		public LevelDirectivesContext levelDirectives;
		public PowerCutoffContext powerCutoff() {
			return getRuleContext(PowerCutoffContext.class,0);
		}
		public LevelDirectivesContext levelDirectives() {
			return getRuleContext(LevelDirectivesContext.class,0);
		}
		public PowerCutoffBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_powerCutoffBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).enterPowerCutoffBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).exitPowerCutoffBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterSpellGrammarVisitor ) return ((MonsterSpellGrammarVisitor<? extends T>)visitor).visitPowerCutoffBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PowerCutoffBlockContext powerCutoffBlock() throws RecognitionException {
		PowerCutoffBlockContext _localctx = new PowerCutoffBlockContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_powerCutoffBlock);
		try {
			enterOuterAlt(_localctx, 1);
			{
			 ((PowerCutoffBlockContext)_localctx).loreStr =  "";
			                  ((PowerCutoffBlockContext)_localctx).loreColourBaseStr =  null;
			                  ((PowerCutoffBlockContext)_localctx).loreColourResistStr =  null;
			                  ((PowerCutoffBlockContext)_localctx).loreColourImmuneStr =  null;
			                  ((PowerCutoffBlockContext)_localctx).messageVisStr =  "";
			                  ((PowerCutoffBlockContext)_localctx).messageInvisStr =  "";
			                  ((PowerCutoffBlockContext)_localctx).messageMissStr =  "";
			                  ((PowerCutoffBlockContext)_localctx).messageSaveStr =  ""; 
			setState(126);
			((PowerCutoffBlockContext)_localctx).powerCutoff = powerCutoff();
			 ((PowerCutoffBlockContext)_localctx).powerCutOffVal =  ((PowerCutoffBlockContext)_localctx).powerCutoff.cutoffVal;
			                            ((PowerCutoffBlockContext)_localctx).line =  (((PowerCutoffBlockContext)_localctx).powerCutoff!=null?(((PowerCutoffBlockContext)_localctx).powerCutoff.start):null).getLine(); 
			setState(131);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				setState(128);
				((PowerCutoffBlockContext)_localctx).levelDirectives = levelDirectives();
				 ((PowerCutoffBlockContext)_localctx).loreStr =  ((PowerCutoffBlockContext)_localctx).levelDirectives.loreStr;
				                                    ((PowerCutoffBlockContext)_localctx).loreColourBaseStr =  ((PowerCutoffBlockContext)_localctx).levelDirectives.loreColourBaseStr;
				                                    ((PowerCutoffBlockContext)_localctx).loreColourResistStr =  ((PowerCutoffBlockContext)_localctx).levelDirectives.loreColourResistStr;
				                                    ((PowerCutoffBlockContext)_localctx).loreColourImmuneStr =  ((PowerCutoffBlockContext)_localctx).levelDirectives.loreColourImmuneStr;
				                                    ((PowerCutoffBlockContext)_localctx).messageVisStr =  ((PowerCutoffBlockContext)_localctx).levelDirectives.messageVisStr;
				                                    ((PowerCutoffBlockContext)_localctx).messageInvisStr =  ((PowerCutoffBlockContext)_localctx).levelDirectives.messageInvisStr;
				                                    ((PowerCutoffBlockContext)_localctx).messageMissStr =  ((PowerCutoffBlockContext)_localctx).levelDirectives.messageMissStr;
				                                    ((PowerCutoffBlockContext)_localctx).messageSaveStr =  ((PowerCutoffBlockContext)_localctx).levelDirectives.messageSaveStr; 
				}
				break;
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
	public static class MonsterSpellContext extends ParserRuleContext {
		public MonsterSpellParseRecord spell;
		public NameContext name;
		public MsgtContext msgt;
		public HitContext hit;
		public EffectBlockContext effectBlock;
		public EffectYXContext effectYX;
		public DiceContext dice;
		public ExprContext expr;
		public PowerCutoffBlockContext powerCutoffBlock;
		public LevelDirectivesContext levelDirectives;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public List<MsgtContext> msgt() {
			return getRuleContexts(MsgtContext.class);
		}
		public MsgtContext msgt(int i) {
			return getRuleContext(MsgtContext.class,i);
		}
		public List<HitContext> hit() {
			return getRuleContexts(HitContext.class);
		}
		public HitContext hit(int i) {
			return getRuleContext(HitContext.class,i);
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
		public List<PowerCutoffBlockContext> powerCutoffBlock() {
			return getRuleContexts(PowerCutoffBlockContext.class);
		}
		public PowerCutoffBlockContext powerCutoffBlock(int i) {
			return getRuleContext(PowerCutoffBlockContext.class,i);
		}
		public List<LevelDirectivesContext> levelDirectives() {
			return getRuleContexts(LevelDirectivesContext.class);
		}
		public LevelDirectivesContext levelDirectives(int i) {
			return getRuleContext(LevelDirectivesContext.class,i);
		}
		public MonsterSpellContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_monsterSpell; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).enterMonsterSpell(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).exitMonsterSpell(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterSpellGrammarVisitor ) return ((MonsterSpellGrammarVisitor<? extends T>)visitor).visitMonsterSpell(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MonsterSpellContext monsterSpell() throws RecognitionException {
		MonsterSpellContext _localctx = new MonsterSpellContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_monsterSpell);

		          String nameStrInit = "";
		          String msgtStrInit = "";
		          String hitInit = "";
		          int lineInit = 0;
		          List<EffectParseRecord> effects = new ArrayList<>();
		          List<MonsterSpellParseRecord.MonsterSpellLevelParseRecord> levels = new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(133);
			((MonsterSpellContext)_localctx).name = name();
			 nameStrInit = ((MonsterSpellContext)_localctx).name.nameStr; lineInit = ((MonsterSpellContext)_localctx).name.line; 
			setState(159); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(159);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case MSGT:
					{
					setState(135);
					((MonsterSpellContext)_localctx).msgt = msgt();
					 msgtStrInit = ((MonsterSpellContext)_localctx).msgt.msgTStr; 
					}
					break;
				case HIT:
					{
					setState(138);
					((MonsterSpellContext)_localctx).hit = hit();
					 hitInit = ((MonsterSpellContext)_localctx).hit.hitVal; 
					}
					break;
				case EFFECT:
					{
					setState(141);
					((MonsterSpellContext)_localctx).effectBlock = effectBlock();
					 effects.add(new EffectParseRecord(((MonsterSpellContext)_localctx).effectBlock.typeInit,
					                  ((MonsterSpellContext)_localctx).effectBlock.subtypeWrapperInit, ((MonsterSpellContext)_localctx).effectBlock.radius, ((MonsterSpellContext)_localctx).effectBlock.other,
					                  ((MonsterSpellContext)_localctx).effectBlock.diceString, ((MonsterSpellContext)_localctx).effectBlock.yVal, ((MonsterSpellContext)_localctx).effectBlock.xVal,
					                  ((MonsterSpellContext)_localctx).effectBlock.expressionChars, ((MonsterSpellContext)_localctx).effectBlock.expressionBase,
					                  ((MonsterSpellContext)_localctx).effectBlock.expressionOperation, ((MonsterSpellContext)_localctx).effectBlock.effectMessage,
					                  (((MonsterSpellContext)_localctx).effectBlock!=null?(((MonsterSpellContext)_localctx).effectBlock.start):null).getLine())); 
					}
					break;
				case EFFECT_YX:
					{
					setState(144);
					((MonsterSpellContext)_localctx).effectYX = effectYX();

					                if (!effects.isEmpty()) {
					                    EffectParseRecord last = effects.getLast();
					                    EffectParseRecord updated = last.amendYX(((MonsterSpellContext)_localctx).effectYX.y, ((MonsterSpellContext)_localctx).effectYX.x);
					                    replaceLast(effects, updated);
					                }
					            
					}
					break;
				case DICE:
					{
					setState(147);
					((MonsterSpellContext)_localctx).dice = dice();

					                if (!effects.isEmpty()) {
					                    EffectParseRecord last = effects.getLast();
					                    String diceStr = ((MonsterSpellContext)_localctx).dice.diceString;
					                    EffectParseRecord updated = last.amendExpression(diceStr,
					                                                                     ((MonsterSpellContext)_localctx).dice.exprChar,
					                                                                     ((MonsterSpellContext)_localctx).dice.baseName,
					                                                                     ((MonsterSpellContext)_localctx).dice.operation);
					                    replaceLast(effects, updated);
					                }
					            
					}
					break;
				case EXPR:
					{
					setState(150);
					((MonsterSpellContext)_localctx).expr = expr();

					                if (!effects.isEmpty() && !effects.getLast().diceString().isEmpty()) {
					                    EffectParseRecord last = effects.getLast();
					                    String lastDiceStr  = last.diceString();
					                    String lastExprChar = last.expressionChars().isEmpty() 
					                                        ? ((MonsterSpellContext)_localctx).expr.exprChar
					                                        : last.expressionChars() + "^" + ((MonsterSpellContext)_localctx).expr.exprChar;
					                    String lastExprBase = last.expressionBases().isEmpty() 
					                                        ? ((MonsterSpellContext)_localctx).expr.baseName
					                                        : last.expressionBases() + "^" + ((MonsterSpellContext)_localctx).expr.baseName;
					                    String lastExprOp   = last.expressionOperations().isEmpty() 
					                                        ? ((MonsterSpellContext)_localctx).expr.operation
					                                        : last.expressionOperations() + "^" + ((MonsterSpellContext)_localctx).expr.operation;
					                    EffectParseRecord updated = last.amendExpression(lastDiceStr,
					                                                lastExprChar,
					                                                lastExprBase,
					                                                lastExprOp);
					                    replaceLast(effects, updated);
					                }
					            
					}
					break;
				case POWER_CUTOFF:
					{
					setState(153);
					((MonsterSpellContext)_localctx).powerCutoffBlock = powerCutoffBlock();
					 levels.add(new MonsterSpellParseRecord.MonsterSpellLevelParseRecord(
					                  ((MonsterSpellContext)_localctx).powerCutoffBlock.powerCutOffVal, ((MonsterSpellContext)_localctx).powerCutoffBlock.loreStr,
					                  ((MonsterSpellContext)_localctx).powerCutoffBlock.loreColourBaseStr, ((MonsterSpellContext)_localctx).powerCutoffBlock.loreColourResistStr,
					                  ((MonsterSpellContext)_localctx).powerCutoffBlock.loreColourImmuneStr, ((MonsterSpellContext)_localctx).powerCutoffBlock.messageVisStr,
					                  ((MonsterSpellContext)_localctx).powerCutoffBlock.messageInvisStr, ((MonsterSpellContext)_localctx).powerCutoffBlock.messageMissStr,
					                  ((MonsterSpellContext)_localctx).powerCutoffBlock.messageSaveStr, ((MonsterSpellContext)_localctx).powerCutoffBlock.line)); 
					}
					break;
				case LORE:
				case LORE_COLOUR_BASE:
				case LORE_COLOUR_RESIST:
				case LORE_COLOUR_IMMUNE:
				case MESSAGE_SAVE:
				case MESSAGE_VIS:
				case MESSAGE_INVIS:
				case MESSAGE_MISS:
					{
					setState(156);
					((MonsterSpellContext)_localctx).levelDirectives = levelDirectives();
					 levels.add(new MonsterSpellParseRecord.MonsterSpellLevelParseRecord(
					                  null, ((MonsterSpellContext)_localctx).levelDirectives.loreStr,
					                  ((MonsterSpellContext)_localctx).levelDirectives.loreColourBaseStr, ((MonsterSpellContext)_localctx).levelDirectives.loreColourResistStr,
					                  ((MonsterSpellContext)_localctx).levelDirectives.loreColourImmuneStr, ((MonsterSpellContext)_localctx).levelDirectives.messageVisStr,
					                  ((MonsterSpellContext)_localctx).levelDirectives.messageInvisStr, ((MonsterSpellContext)_localctx).levelDirectives.messageMissStr,
					                  ((MonsterSpellContext)_localctx).levelDirectives.messageSaveStr, ((MonsterSpellContext)_localctx).levelDirectives.line)); 
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(161); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 3481596L) != 0) );
			}
			_ctx.stop = _input.LT(-1);

			          ((MonsterSpellContext)_localctx).spell =  new MonsterSpellParseRecord(nameStrInit, msgtStrInit, hitInit,                                                                   
			                  effects, levels, lineInit);
			        
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
		public List<MonsterSpellParseRecord> monsterSpells;
		public MonsterSpellContext monsterSpell;
		public TerminalNode EOF() { return getToken(MonsterSpellGrammar.EOF, 0); }
		public List<MonsterSpellContext> monsterSpell() {
			return getRuleContexts(MonsterSpellContext.class);
		}
		public MonsterSpellContext monsterSpell(int i) {
			return getRuleContext(MonsterSpellContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterSpellGrammarVisitor ) return ((MonsterSpellGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_file);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			 ((FileContext)_localctx).monsterSpells =  new ArrayList<>(); 
			setState(167); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(164);
				((FileContext)_localctx).monsterSpell = monsterSpell();
				 _localctx.monsterSpells.add(((FileContext)_localctx).monsterSpell.spell); 
				}
				}
				setState(169); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(171);
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
		public TerminalNode EFFECT() { return getToken(MonsterSpellGrammar.EFFECT, 0); }
		public List<TerminalNode> UCASE() { return getTokens(MonsterSpellGrammar.UCASE); }
		public TerminalNode UCASE(int i) {
			return getToken(MonsterSpellGrammar.UCASE, i);
		}
		public List<TerminalNode> COLON() { return getTokens(MonsterSpellGrammar.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(MonsterSpellGrammar.COLON, i);
		}
		public List<TerminalNode> INTEGER() { return getTokens(MonsterSpellGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(MonsterSpellGrammar.INTEGER, i);
		}
		public EffectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effect; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).enterEffect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).exitEffect(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterSpellGrammarVisitor ) return ((MonsterSpellGrammarVisitor<? extends T>)visitor).visitEffect(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectContext effect() throws RecognitionException {
		EffectContext _localctx = new EffectContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_effect);

		            ((EffectContext)_localctx).wrapper =  "";
		            ((EffectContext)_localctx).radius =  "";
		            ((EffectContext)_localctx).other =  "";
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(173);
			match(EFFECT);
			setState(174);
			((EffectContext)_localctx).t = match(UCASE);

			                ((EffectContext)_localctx).type =  ((EffectContext)_localctx).t.getText();
			            
			setState(189);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(176);
				match(COLON);
				setState(177);
				((EffectContext)_localctx).st = match(UCASE);

				                ((EffectContext)_localctx).wrapper =  ((EffectContext)_localctx).st.getText();
				            
				setState(187);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COLON) {
					{
					setState(179);
					match(COLON);
					setState(180);
					((EffectContext)_localctx).rad = match(INTEGER);

					                ((EffectContext)_localctx).radius =  ((EffectContext)_localctx).rad.getText();
					            
					setState(185);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==COLON) {
						{
						setState(182);
						match(COLON);
						setState(183);
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
		public TerminalNode EFFECT_YX() { return getToken(MonsterSpellGrammar.EFFECT_YX, 0); }
		public TerminalNode COLON() { return getToken(MonsterSpellGrammar.COLON, 0); }
		public List<TerminalNode> INTEGER() { return getTokens(MonsterSpellGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(MonsterSpellGrammar.INTEGER, i);
		}
		public EffectYXContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effectYX; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).enterEffectYX(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).exitEffectYX(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterSpellGrammarVisitor ) return ((MonsterSpellGrammarVisitor<? extends T>)visitor).visitEffectYX(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectYXContext effectYX() throws RecognitionException {
		EffectYXContext _localctx = new EffectYXContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_effectYX);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(191);
			match(EFFECT_YX);
			setState(192);
			((EffectYXContext)_localctx).yVal = match(INTEGER);
			setState(193);
			match(COLON);
			setState(194);
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
		public TerminalNode DICE() { return getToken(MonsterSpellGrammar.DICE, 0); }
		public TerminalNode DICE_SIMPLE_VALUE() { return getToken(MonsterSpellGrammar.DICE_SIMPLE_VALUE, 0); }
		public TerminalNode DICE_COMPLEX_VALUE() { return getToken(MonsterSpellGrammar.DICE_COMPLEX_VALUE, 0); }
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
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).enterDice(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).exitDice(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterSpellGrammarVisitor ) return ((MonsterSpellGrammarVisitor<? extends T>)visitor).visitDice(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DiceContext dice() throws RecognitionException {
		DiceContext _localctx = new DiceContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_dice);

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
			setState(197);
			match(DICE);
			setState(210);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DICE_COMPLEX_VALUE:
				{
				{
				setState(198);
				((DiceContext)_localctx).val = match(DICE_COMPLEX_VALUE);

				                ((DiceContext)_localctx).diceString =  ((DiceContext)_localctx).val.getText();
				            
				setState(205);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(200);
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
					setState(207);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
				}
				}
				}
				break;
			case DICE_SIMPLE_VALUE:
				{
				setState(208);
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
		public TerminalNode EXPR() { return getToken(MonsterSpellGrammar.EXPR, 0); }
		public List<TerminalNode> EXPR_COLON() { return getTokens(MonsterSpellGrammar.EXPR_COLON); }
		public TerminalNode EXPR_COLON(int i) {
			return getToken(MonsterSpellGrammar.EXPR_COLON, i);
		}
		public TerminalNode EXPR_CHAR() { return getToken(MonsterSpellGrammar.EXPR_CHAR, 0); }
		public TerminalNode EXPR_UCASE() { return getToken(MonsterSpellGrammar.EXPR_UCASE, 0); }
		public TerminalNode EXPR_OP() { return getToken(MonsterSpellGrammar.EXPR_OP, 0); }
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).exitExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterSpellGrammarVisitor ) return ((MonsterSpellGrammarVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(212);
			match(EXPR);
			setState(213);
			((ExprContext)_localctx).ch = match(EXPR_CHAR);
			setState(214);
			match(EXPR_COLON);
			setState(215);
			((ExprContext)_localctx).base = match(EXPR_UCASE);
			setState(216);
			match(EXPR_COLON);
			setState(217);
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
		public TerminalNode EFFECT_MESSAGE() { return getToken(MonsterSpellGrammar.EFFECT_MESSAGE, 0); }
		public TerminalNode FREE_TEXT() { return getToken(MonsterSpellGrammar.FREE_TEXT, 0); }
		public EffectMsgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effectMsg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).enterEffectMsg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).exitEffectMsg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterSpellGrammarVisitor ) return ((MonsterSpellGrammarVisitor<? extends T>)visitor).visitEffectMsg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectMsgContext effectMsg() throws RecognitionException {
		EffectMsgContext _localctx = new EffectMsgContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_effectMsg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(220);
			match(EFFECT_MESSAGE);
			setState(221);
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
		public TerminalNode TIME() { return getToken(MonsterSpellGrammar.TIME, 0); }
		public TerminalNode DICE_SIMPLE_VALUE() { return getToken(MonsterSpellGrammar.DICE_SIMPLE_VALUE, 0); }
		public TimeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_time; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).enterTime(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).exitTime(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterSpellGrammarVisitor ) return ((MonsterSpellGrammarVisitor<? extends T>)visitor).visitTime(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TimeContext time() throws RecognitionException {
		TimeContext _localctx = new TimeContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_time);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(224);
			match(TIME);
			setState(225);
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
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).enterStrayEffectLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).exitStrayEffectLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterSpellGrammarVisitor ) return ((MonsterSpellGrammarVisitor<? extends T>)visitor).visitStrayEffectLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StrayEffectLineContext strayEffectLine() throws RecognitionException {
		StrayEffectLineContext _localctx = new StrayEffectLineContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_strayEffectLine);
		try {
			setState(231);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EFFECT_YX:
				enterOuterAlt(_localctx, 1);
				{
				setState(228);
				effectYX();
				}
				break;
			case DICE:
				enterOuterAlt(_localctx, 2);
				{
				setState(229);
				dice();
				}
				break;
			case EXPR:
				enterOuterAlt(_localctx, 3);
				{
				setState(230);
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
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).enterEffectBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterSpellGrammarListener ) ((MonsterSpellGrammarListener)listener).exitEffectBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterSpellGrammarVisitor ) return ((MonsterSpellGrammarVisitor<? extends T>)visitor).visitEffectBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectBlockContext effectBlock() throws RecognitionException {
		EffectBlockContext _localctx = new EffectBlockContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_effectBlock);

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
			setState(233);
			((EffectBlockContext)_localctx).effect = effect();

			                ((EffectBlockContext)_localctx).lineNo =  _localctx.start.getLine();
			                ((EffectBlockContext)_localctx).typeInit =  ((EffectBlockContext)_localctx).effect.type;
			                ((EffectBlockContext)_localctx).subtypeWrapperInit =  ((EffectBlockContext)_localctx).effect.wrapper;
			                ((EffectBlockContext)_localctx).radius =  ((EffectBlockContext)_localctx).effect.radius;
			                ((EffectBlockContext)_localctx).other =  ((EffectBlockContext)_localctx).effect.other;
			            
			setState(251);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				{
				{
				setState(235);
				((EffectBlockContext)_localctx).effectYX = effectYX();

				                ((EffectBlockContext)_localctx).yVal =  ((EffectBlockContext)_localctx).effectYX.y;
				                ((EffectBlockContext)_localctx).xVal =  ((EffectBlockContext)_localctx).effectYX.x;
				            
				setState(237);
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
				setState(240);
				((EffectBlockContext)_localctx).dice = dice();

				                ((EffectBlockContext)_localctx).diceString =  ((EffectBlockContext)_localctx).dice.diceString;
				                expressionString = ((EffectBlockContext)_localctx).dice.exprChar;
				                baseString = ((EffectBlockContext)_localctx).dice.baseName;
				                opString = ((EffectBlockContext)_localctx).dice.operation;
				            
				setState(242);
				((EffectBlockContext)_localctx).effectYX = effectYX();

				                ((EffectBlockContext)_localctx).yVal =  ((EffectBlockContext)_localctx).effectYX.y;
				                ((EffectBlockContext)_localctx).xVal =  ((EffectBlockContext)_localctx).effectYX.x;
				            
				}
				}
				break;
			case 3:
				{
				setState(245);
				((EffectBlockContext)_localctx).dice = dice();

				                ((EffectBlockContext)_localctx).diceString =  ((EffectBlockContext)_localctx).dice.diceString;
				                expressionString = ((EffectBlockContext)_localctx).dice.exprChar;
				                baseString = ((EffectBlockContext)_localctx).dice.baseName;
				                opString = ((EffectBlockContext)_localctx).dice.operation;
				            
				}
				break;
			case 4:
				{
				setState(248);
				((EffectBlockContext)_localctx).effectYX = effectYX();

				                ((EffectBlockContext)_localctx).yVal =  ((EffectBlockContext)_localctx).effectYX.y;
				                ((EffectBlockContext)_localctx).xVal =  ((EffectBlockContext)_localctx).effectYX.x;
				            
				}
				break;
			}
			setState(256);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EFFECT_MESSAGE) {
				{
				setState(253);
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
		"\u0004\u0001$\u0103\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0004\fz\b"+
		"\f\u000b\f\f\f{\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003"+
		"\r\u0084\b\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0004\u000e\u00a0\b\u000e\u000b\u000e"+
		"\f\u000e\u00a1\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0004\u000f"+
		"\u00a8\b\u000f\u000b\u000f\f\u000f\u00a9\u0001\u000f\u0001\u000f\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003"+
		"\u0010\u00ba\b\u0010\u0003\u0010\u00bc\b\u0010\u0003\u0010\u00be\b\u0010"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0005\u0012\u00cc\b\u0012\n\u0012\f\u0012\u00cf\t\u0012\u0001\u0012\u0001"+
		"\u0012\u0003\u0012\u00d3\b\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001"+
		"\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u00e8\b\u0016\u0001"+
		"\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0003"+
		"\u0017\u00fc\b\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0003\u0017\u0101"+
		"\b\u0017\u0001\u0017\u0000\u0000\u0018\u0000\u0002\u0004\u0006\b\n\f\u000e"+
		"\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.\u0000\u0000"+
		"\u0108\u00000\u0001\u0000\u0000\u0000\u00024\u0001\u0000\u0000\u0000\u0004"+
		"8\u0001\u0000\u0000\u0000\u0006<\u0001\u0000\u0000\u0000\b@\u0001\u0000"+
		"\u0000\u0000\nD\u0001\u0000\u0000\u0000\fH\u0001\u0000\u0000\u0000\u000e"+
		"L\u0001\u0000\u0000\u0000\u0010P\u0001\u0000\u0000\u0000\u0012T\u0001"+
		"\u0000\u0000\u0000\u0014X\u0001\u0000\u0000\u0000\u0016\\\u0001\u0000"+
		"\u0000\u0000\u0018`\u0001\u0000\u0000\u0000\u001a}\u0001\u0000\u0000\u0000"+
		"\u001c\u0085\u0001\u0000\u0000\u0000\u001e\u00a3\u0001\u0000\u0000\u0000"+
		" \u00ad\u0001\u0000\u0000\u0000\"\u00bf\u0001\u0000\u0000\u0000$\u00c5"+
		"\u0001\u0000\u0000\u0000&\u00d4\u0001\u0000\u0000\u0000(\u00dc\u0001\u0000"+
		"\u0000\u0000*\u00e0\u0001\u0000\u0000\u0000,\u00e7\u0001\u0000\u0000\u0000"+
		".\u00e9\u0001\u0000\u0000\u000001\u0005\u0001\u0000\u000012\u0005\u001b"+
		"\u0000\u000023\u0006\u0000\uffff\uffff\u00003\u0001\u0001\u0000\u0000"+
		"\u000045\u0005\u0002\u0000\u000056\u0005\u001b\u0000\u000067\u0006\u0001"+
		"\uffff\uffff\u00007\u0003\u0001\u0000\u0000\u000089\u0005\u0003\u0000"+
		"\u00009:\u0005\u0018\u0000\u0000:;\u0006\u0002\uffff\uffff\u0000;\u0005"+
		"\u0001\u0000\u0000\u0000<=\u0005\u0004\u0000\u0000=>\u0005\u0018\u0000"+
		"\u0000>?\u0006\u0003\uffff\uffff\u0000?\u0007\u0001\u0000\u0000\u0000"+
		"@A\u0005\u0005\u0000\u0000AB\u0005\u001b\u0000\u0000BC\u0006\u0004\uffff"+
		"\uffff\u0000C\t\u0001\u0000\u0000\u0000DE\u0005\u0006\u0000\u0000EF\u0005"+
		"\u001b\u0000\u0000FG\u0006\u0005\uffff\uffff\u0000G\u000b\u0001\u0000"+
		"\u0000\u0000HI\u0005\u0007\u0000\u0000IJ\u0005\u001b\u0000\u0000JK\u0006"+
		"\u0006\uffff\uffff\u0000K\r\u0001\u0000\u0000\u0000LM\u0005\b\u0000\u0000"+
		"MN\u0005\u001b\u0000\u0000NO\u0006\u0007\uffff\uffff\u0000O\u000f\u0001"+
		"\u0000\u0000\u0000PQ\u0005\t\u0000\u0000QR\u0005\u001b\u0000\u0000RS\u0006"+
		"\b\uffff\uffff\u0000S\u0011\u0001\u0000\u0000\u0000TU\u0005\n\u0000\u0000"+
		"UV\u0005\u001b\u0000\u0000VW\u0006\t\uffff\uffff\u0000W\u0013\u0001\u0000"+
		"\u0000\u0000XY\u0005\u000b\u0000\u0000YZ\u0005\u001b\u0000\u0000Z[\u0006"+
		"\n\uffff\uffff\u0000[\u0015\u0001\u0000\u0000\u0000\\]\u0005\f\u0000\u0000"+
		"]^\u0005\u001b\u0000\u0000^_\u0006\u000b\uffff\uffff\u0000_\u0017\u0001"+
		"\u0000\u0000\u0000`y\u0006\f\uffff\uffff\u0000ab\u0003\b\u0004\u0000b"+
		"c\u0006\f\uffff\uffff\u0000cz\u0001\u0000\u0000\u0000de\u0003\n\u0005"+
		"\u0000ef\u0006\f\uffff\uffff\u0000fz\u0001\u0000\u0000\u0000gh\u0003\f"+
		"\u0006\u0000hi\u0006\f\uffff\uffff\u0000iz\u0001\u0000\u0000\u0000jk\u0003"+
		"\u000e\u0007\u0000kl\u0006\f\uffff\uffff\u0000lz\u0001\u0000\u0000\u0000"+
		"mn\u0003\u0012\t\u0000no\u0006\f\uffff\uffff\u0000oz\u0001\u0000\u0000"+
		"\u0000pq\u0003\u0014\n\u0000qr\u0006\f\uffff\uffff\u0000rz\u0001\u0000"+
		"\u0000\u0000st\u0003\u0016\u000b\u0000tu\u0006\f\uffff\uffff\u0000uz\u0001"+
		"\u0000\u0000\u0000vw\u0003\u0010\b\u0000wx\u0006\f\uffff\uffff\u0000x"+
		"z\u0001\u0000\u0000\u0000ya\u0001\u0000\u0000\u0000yd\u0001\u0000\u0000"+
		"\u0000yg\u0001\u0000\u0000\u0000yj\u0001\u0000\u0000\u0000ym\u0001\u0000"+
		"\u0000\u0000yp\u0001\u0000\u0000\u0000ys\u0001\u0000\u0000\u0000yv\u0001"+
		"\u0000\u0000\u0000z{\u0001\u0000\u0000\u0000{y\u0001\u0000\u0000\u0000"+
		"{|\u0001\u0000\u0000\u0000|\u0019\u0001\u0000\u0000\u0000}~\u0006\r\uffff"+
		"\uffff\u0000~\u007f\u0003\u0006\u0003\u0000\u007f\u0083\u0006\r\uffff"+
		"\uffff\u0000\u0080\u0081\u0003\u0018\f\u0000\u0081\u0082\u0006\r\uffff"+
		"\uffff\u0000\u0082\u0084\u0001\u0000\u0000\u0000\u0083\u0080\u0001\u0000"+
		"\u0000\u0000\u0083\u0084\u0001\u0000\u0000\u0000\u0084\u001b\u0001\u0000"+
		"\u0000\u0000\u0085\u0086\u0003\u0000\u0000\u0000\u0086\u009f\u0006\u000e"+
		"\uffff\uffff\u0000\u0087\u0088\u0003\u0002\u0001\u0000\u0088\u0089\u0006"+
		"\u000e\uffff\uffff\u0000\u0089\u00a0\u0001\u0000\u0000\u0000\u008a\u008b"+
		"\u0003\u0004\u0002\u0000\u008b\u008c\u0006\u000e\uffff\uffff\u0000\u008c"+
		"\u00a0\u0001\u0000\u0000\u0000\u008d\u008e\u0003.\u0017\u0000\u008e\u008f"+
		"\u0006\u000e\uffff\uffff\u0000\u008f\u00a0\u0001\u0000\u0000\u0000\u0090"+
		"\u0091\u0003\"\u0011\u0000\u0091\u0092\u0006\u000e\uffff\uffff\u0000\u0092"+
		"\u00a0\u0001\u0000\u0000\u0000\u0093\u0094\u0003$\u0012\u0000\u0094\u0095"+
		"\u0006\u000e\uffff\uffff\u0000\u0095\u00a0\u0001\u0000\u0000\u0000\u0096"+
		"\u0097\u0003&\u0013\u0000\u0097\u0098\u0006\u000e\uffff\uffff\u0000\u0098"+
		"\u00a0\u0001\u0000\u0000\u0000\u0099\u009a\u0003\u001a\r\u0000\u009a\u009b"+
		"\u0006\u000e\uffff\uffff\u0000\u009b\u00a0\u0001\u0000\u0000\u0000\u009c"+
		"\u009d\u0003\u0018\f\u0000\u009d\u009e\u0006\u000e\uffff\uffff\u0000\u009e"+
		"\u00a0\u0001\u0000\u0000\u0000\u009f\u0087\u0001\u0000\u0000\u0000\u009f"+
		"\u008a\u0001\u0000\u0000\u0000\u009f\u008d\u0001\u0000\u0000\u0000\u009f"+
		"\u0090\u0001\u0000\u0000\u0000\u009f\u0093\u0001\u0000\u0000\u0000\u009f"+
		"\u0096\u0001\u0000\u0000\u0000\u009f\u0099\u0001\u0000\u0000\u0000\u009f"+
		"\u009c\u0001\u0000\u0000\u0000\u00a0\u00a1\u0001\u0000\u0000\u0000\u00a1"+
		"\u009f\u0001\u0000\u0000\u0000\u00a1\u00a2\u0001\u0000\u0000\u0000\u00a2"+
		"\u001d\u0001\u0000\u0000\u0000\u00a3\u00a7\u0006\u000f\uffff\uffff\u0000"+
		"\u00a4\u00a5\u0003\u001c\u000e\u0000\u00a5\u00a6\u0006\u000f\uffff\uffff"+
		"\u0000\u00a6\u00a8\u0001\u0000\u0000\u0000\u00a7\u00a4\u0001\u0000\u0000"+
		"\u0000\u00a8\u00a9\u0001\u0000\u0000\u0000\u00a9\u00a7\u0001\u0000\u0000"+
		"\u0000\u00a9\u00aa\u0001\u0000\u0000\u0000\u00aa\u00ab\u0001\u0000\u0000"+
		"\u0000\u00ab\u00ac\u0005\u0000\u0000\u0001\u00ac\u001f\u0001\u0000\u0000"+
		"\u0000\u00ad\u00ae\u0005\u0010\u0000\u0000\u00ae\u00af\u0005\u0017\u0000"+
		"\u0000\u00af\u00bd\u0006\u0010\uffff\uffff\u0000\u00b0\u00b1\u0005\u0016"+
		"\u0000\u0000\u00b1\u00b2\u0005\u0017\u0000\u0000\u00b2\u00bb\u0006\u0010"+
		"\uffff\uffff\u0000\u00b3\u00b4\u0005\u0016\u0000\u0000\u00b4\u00b5\u0005"+
		"\u0018\u0000\u0000\u00b5\u00b9\u0006\u0010\uffff\uffff\u0000\u00b6\u00b7"+
		"\u0005\u0016\u0000\u0000\u00b7\u00b8\u0005\u0018\u0000\u0000\u00b8\u00ba"+
		"\u0006\u0010\uffff\uffff\u0000\u00b9\u00b6\u0001\u0000\u0000\u0000\u00b9"+
		"\u00ba\u0001\u0000\u0000\u0000\u00ba\u00bc\u0001\u0000\u0000\u0000\u00bb"+
		"\u00b3\u0001\u0000\u0000\u0000\u00bb\u00bc\u0001\u0000\u0000\u0000\u00bc"+
		"\u00be\u0001\u0000\u0000\u0000\u00bd\u00b0\u0001\u0000\u0000\u0000\u00bd"+
		"\u00be\u0001\u0000\u0000\u0000\u00be!\u0001\u0000\u0000\u0000\u00bf\u00c0"+
		"\u0005\u0014\u0000\u0000\u00c0\u00c1\u0005\u0018\u0000\u0000\u00c1\u00c2"+
		"\u0005\u0016\u0000\u0000\u00c2\u00c3\u0005\u0018\u0000\u0000\u00c3\u00c4"+
		"\u0006\u0011\uffff\uffff\u0000\u00c4#\u0001\u0000\u0000\u0000\u00c5\u00d2"+
		"\u0005\u0012\u0000\u0000\u00c6\u00c7\u0005\u001f\u0000\u0000\u00c7\u00cd"+
		"\u0006\u0012\uffff\uffff\u0000\u00c8\u00c9\u0003&\u0013\u0000\u00c9\u00ca"+
		"\u0006\u0012\uffff\uffff\u0000\u00ca\u00cc\u0001\u0000\u0000\u0000\u00cb"+
		"\u00c8\u0001\u0000\u0000\u0000\u00cc\u00cf\u0001\u0000\u0000\u0000\u00cd"+
		"\u00cb\u0001\u0000\u0000\u0000\u00cd\u00ce\u0001\u0000\u0000\u0000\u00ce"+
		"\u00d3\u0001\u0000\u0000\u0000\u00cf\u00cd\u0001\u0000\u0000\u0000\u00d0"+
		"\u00d1\u0005\u001e\u0000\u0000\u00d1\u00d3\u0006\u0012\uffff\uffff\u0000"+
		"\u00d2\u00c6\u0001\u0000\u0000\u0000\u00d2\u00d0\u0001\u0000\u0000\u0000"+
		"\u00d3%\u0001\u0000\u0000\u0000\u00d4\u00d5\u0005\u0015\u0000\u0000\u00d5"+
		"\u00d6\u0005 \u0000\u0000\u00d6\u00d7\u0005!\u0000\u0000\u00d7\u00d8\u0005"+
		"\"\u0000\u0000\u00d8\u00d9\u0005!\u0000\u0000\u00d9\u00da\u0005#\u0000"+
		"\u0000\u00da\u00db\u0006\u0013\uffff\uffff\u0000\u00db\'\u0001\u0000\u0000"+
		"\u0000\u00dc\u00dd\u0005\u0011\u0000\u0000\u00dd\u00de\u0005\u001d\u0000"+
		"\u0000\u00de\u00df\u0006\u0014\uffff\uffff\u0000\u00df)\u0001\u0000\u0000"+
		"\u0000\u00e0\u00e1\u0005\u0013\u0000\u0000\u00e1\u00e2\u0005\u001e\u0000"+
		"\u0000\u00e2\u00e3\u0006\u0015\uffff\uffff\u0000\u00e3+\u0001\u0000\u0000"+
		"\u0000\u00e4\u00e8\u0003\"\u0011\u0000\u00e5\u00e8\u0003$\u0012\u0000"+
		"\u00e6\u00e8\u0003&\u0013\u0000\u00e7\u00e4\u0001\u0000\u0000\u0000\u00e7"+
		"\u00e5\u0001\u0000\u0000\u0000\u00e7\u00e6\u0001\u0000\u0000\u0000\u00e8"+
		"-\u0001\u0000\u0000\u0000\u00e9\u00ea\u0003 \u0010\u0000\u00ea\u00fb\u0006"+
		"\u0017\uffff\uffff\u0000\u00eb\u00ec\u0003\"\u0011\u0000\u00ec\u00ed\u0006"+
		"\u0017\uffff\uffff\u0000\u00ed\u00ee\u0003$\u0012\u0000\u00ee\u00ef\u0006"+
		"\u0017\uffff\uffff\u0000\u00ef\u00fc\u0001\u0000\u0000\u0000\u00f0\u00f1"+
		"\u0003$\u0012\u0000\u00f1\u00f2\u0006\u0017\uffff\uffff\u0000\u00f2\u00f3"+
		"\u0003\"\u0011\u0000\u00f3\u00f4\u0006\u0017\uffff\uffff\u0000\u00f4\u00fc"+
		"\u0001\u0000\u0000\u0000\u00f5\u00f6\u0003$\u0012\u0000\u00f6\u00f7\u0006"+
		"\u0017\uffff\uffff\u0000\u00f7\u00fc\u0001\u0000\u0000\u0000\u00f8\u00f9"+
		"\u0003\"\u0011\u0000\u00f9\u00fa\u0006\u0017\uffff\uffff\u0000\u00fa\u00fc"+
		"\u0001\u0000\u0000\u0000\u00fb\u00eb\u0001\u0000\u0000\u0000\u00fb\u00f0"+
		"\u0001\u0000\u0000\u0000\u00fb\u00f5\u0001\u0000\u0000\u0000\u00fb\u00f8"+
		"\u0001\u0000\u0000\u0000\u00fb\u00fc\u0001\u0000\u0000\u0000\u00fc\u0100"+
		"\u0001\u0000\u0000\u0000\u00fd\u00fe\u0003(\u0014\u0000\u00fe\u00ff\u0006"+
		"\u0017\uffff\uffff\u0000\u00ff\u0101\u0001\u0000\u0000\u0000\u0100\u00fd"+
		"\u0001\u0000\u0000\u0000\u0100\u0101\u0001\u0000\u0000\u0000\u0101/\u0001"+
		"\u0000\u0000\u0000\u000ey{\u0083\u009f\u00a1\u00a9\u00b9\u00bb\u00bd\u00cd"+
		"\u00d2\u00e7\u00fb\u0100";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}