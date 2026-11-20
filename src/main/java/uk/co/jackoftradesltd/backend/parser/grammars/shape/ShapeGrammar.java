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

// Generated from ShapeGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.shape;

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
import uk.co.jackoftradesltd.backend.parser.shape.ShapeParseRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ShapeGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, COMBAT=2, SKILL_D_P=3, SKILL_D_M=4, SKILL_SAVE=5, SKILL_STEALTH=6, 
		SKILL_SEARCH=7, SKILL_MELEE=8, SKILL_THROW=9, SKILL_DIG=10, OBJ_FLAGS=11, 
		PLAYER_FLAGS=12, VALUES=13, BLOW=14, EFFECT=15, EFFECT_MESSAGE=16, DICE=17, 
		TIME=18, EFFECT_YX=19, EXPR=20, COLON=21, EFFECT_INTEGER=22, SIMPLE_DICE_STRING=23, 
		COMPLEX_DICE_STRING=24, COMMENT=25, INDENT=26, EOL=27, FLAG=28, FLAG_OR=29, 
		FLAG_EOL=30, VALUE_MOD=31, VALUE_INTRO_OR=32, VALUE_INTRO_EOL=33, VALUE_L_BRACKET=34, 
		VALUE_INT=35, VALUE_R_BRACKET=36, VALUE_OR=37, VALUE_EOL=38, STRING=39, 
		YX_INTEGER=40, YX_COLON=41, YX_EOL=42, TYPE=43, TYPE_MODE_COLON=44, TYPE_EOL=45, 
		SUBTYPE=46, SUBTYPETYPE_MODE_COLON=47, SUBTYPE_EOL=48, R_P_EFFECT_INTEGER=49, 
		R_P_COLON=50, R_P_EOL=51, FREE_TEXT=52, DICE_SIMPLE_VALUE=53, DICE_COMPLEX_VALUE=54, 
		EXPR_CHAR=55, EXPR_COLON=56, EXPR_UCASE=57, EXPR_OP=58, EXPR_EOL=59;
	public static final int
		RULE_name = 0, RULE_combat = 1, RULE_skillDisarmP = 2, RULE_skillDisarmM = 3, 
		RULE_skillSave = 4, RULE_skillStealth = 5, RULE_skillSearch = 6, RULE_skillMelee = 7, 
		RULE_skillThrow = 8, RULE_skillDig = 9, RULE_objFlags = 10, RULE_playerFlags = 11, 
		RULE_values = 12, RULE_blow = 13, RULE_shape = 14, RULE_file = 15, RULE_effect = 16, 
		RULE_effectYX = 17, RULE_dice = 18, RULE_expr = 19, RULE_effectMsg = 20, 
		RULE_time = 21, RULE_strayEffectLine = 22, RULE_effectBlock = 23;
	private static String[] makeRuleNames() {
		return new String[] {
			"name", "combat", "skillDisarmP", "skillDisarmM", "skillSave", "skillStealth", 
			"skillSearch", "skillMelee", "skillThrow", "skillDig", "objFlags", "playerFlags", 
			"values", "blow", "shape", "file", "effect", "effectYX", "dice", "expr", 
			"effectMsg", "time", "strayEffectLine", "effectBlock"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'combat:'", "'skill-disarm-phys:'", "'skill-disarm-magic:'", 
			"'skill-save:'", "'skill-stealth:'", "'skill-search:'", "'skill-melee:'", 
			"'skill-throw:'", "'skill-dig:'", "'obj-flags:'", "'player-flags:'", 
			"'values:'", "'blow:'", "'effect:'", "'effect-msg:'", "'dice:'", "'time:'", 
			"'effect-yx:'", "'expr:'", null, null, null, null, null, null, null, 
			null, null, null, null, null, null, "'['", null, "']'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "COMBAT", "SKILL_D_P", "SKILL_D_M", "SKILL_SAVE", "SKILL_STEALTH", 
			"SKILL_SEARCH", "SKILL_MELEE", "SKILL_THROW", "SKILL_DIG", "OBJ_FLAGS", 
			"PLAYER_FLAGS", "VALUES", "BLOW", "EFFECT", "EFFECT_MESSAGE", "DICE", 
			"TIME", "EFFECT_YX", "EXPR", "COLON", "EFFECT_INTEGER", "SIMPLE_DICE_STRING", 
			"COMPLEX_DICE_STRING", "COMMENT", "INDENT", "EOL", "FLAG", "FLAG_OR", 
			"FLAG_EOL", "VALUE_MOD", "VALUE_INTRO_OR", "VALUE_INTRO_EOL", "VALUE_L_BRACKET", 
			"VALUE_INT", "VALUE_R_BRACKET", "VALUE_OR", "VALUE_EOL", "STRING", "YX_INTEGER", 
			"YX_COLON", "YX_EOL", "TYPE", "TYPE_MODE_COLON", "TYPE_EOL", "SUBTYPE", 
			"SUBTYPETYPE_MODE_COLON", "SUBTYPE_EOL", "R_P_EFFECT_INTEGER", "R_P_COLON", 
			"R_P_EOL", "FREE_TEXT", "DICE_SIMPLE_VALUE", "DICE_COMPLEX_VALUE", "EXPR_CHAR", 
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
	public String getGrammarFileName() { return "ShapeGrammar.g4"; }

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
	         
	public ShapeGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public int lineNo;
		public Token STRING;
		public TerminalNode NAME() { return getToken(ShapeGrammar.NAME, 0); }
		public TerminalNode STRING() { return getToken(ShapeGrammar.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapeGrammarVisitor ) return ((ShapeGrammarVisitor<? extends T>)visitor).visitName(this);
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
			 ((NameContext)_localctx).nameStr =  ((NameContext)_localctx).STRING.getText();
			                          ((NameContext)_localctx).lineNo =  _localctx.start.getLine(); 
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
	public static class CombatContext extends ParserRuleContext {
		public String toh;
		public String tod;
		public String toa;
		public Token h;
		public Token d;
		public Token a;
		public TerminalNode COMBAT() { return getToken(ShapeGrammar.COMBAT, 0); }
		public List<TerminalNode> COLON() { return getTokens(ShapeGrammar.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(ShapeGrammar.COLON, i);
		}
		public List<TerminalNode> EFFECT_INTEGER() { return getTokens(ShapeGrammar.EFFECT_INTEGER); }
		public TerminalNode EFFECT_INTEGER(int i) {
			return getToken(ShapeGrammar.EFFECT_INTEGER, i);
		}
		public CombatContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_combat; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).enterCombat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).exitCombat(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapeGrammarVisitor ) return ((ShapeGrammarVisitor<? extends T>)visitor).visitCombat(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CombatContext combat() throws RecognitionException {
		CombatContext _localctx = new CombatContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_combat);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			match(COMBAT);
			setState(53);
			((CombatContext)_localctx).h = match(EFFECT_INTEGER);
			setState(54);
			match(COLON);
			setState(55);
			((CombatContext)_localctx).d = match(EFFECT_INTEGER);
			setState(56);
			match(COLON);
			setState(57);
			((CombatContext)_localctx).a = match(EFFECT_INTEGER);

			                ((CombatContext)_localctx).toh =  ((CombatContext)_localctx).h.getText();
			                ((CombatContext)_localctx).tod =  ((CombatContext)_localctx).d.getText();
			                ((CombatContext)_localctx).toa =  ((CombatContext)_localctx).a.getText();
			            
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
	public static class SkillDisarmPContext extends ParserRuleContext {
		public String skillVal;
		public Token EFFECT_INTEGER;
		public TerminalNode SKILL_D_P() { return getToken(ShapeGrammar.SKILL_D_P, 0); }
		public TerminalNode EFFECT_INTEGER() { return getToken(ShapeGrammar.EFFECT_INTEGER, 0); }
		public SkillDisarmPContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_skillDisarmP; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).enterSkillDisarmP(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).exitSkillDisarmP(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapeGrammarVisitor ) return ((ShapeGrammarVisitor<? extends T>)visitor).visitSkillDisarmP(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SkillDisarmPContext skillDisarmP() throws RecognitionException {
		SkillDisarmPContext _localctx = new SkillDisarmPContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_skillDisarmP);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(60);
			match(SKILL_D_P);
			setState(61);
			((SkillDisarmPContext)_localctx).EFFECT_INTEGER = match(EFFECT_INTEGER);
			 ((SkillDisarmPContext)_localctx).skillVal =  ((SkillDisarmPContext)_localctx).EFFECT_INTEGER.getText(); 
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
	public static class SkillDisarmMContext extends ParserRuleContext {
		public String skillVal;
		public Token EFFECT_INTEGER;
		public TerminalNode SKILL_D_M() { return getToken(ShapeGrammar.SKILL_D_M, 0); }
		public TerminalNode EFFECT_INTEGER() { return getToken(ShapeGrammar.EFFECT_INTEGER, 0); }
		public SkillDisarmMContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_skillDisarmM; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).enterSkillDisarmM(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).exitSkillDisarmM(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapeGrammarVisitor ) return ((ShapeGrammarVisitor<? extends T>)visitor).visitSkillDisarmM(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SkillDisarmMContext skillDisarmM() throws RecognitionException {
		SkillDisarmMContext _localctx = new SkillDisarmMContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_skillDisarmM);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64);
			match(SKILL_D_M);
			setState(65);
			((SkillDisarmMContext)_localctx).EFFECT_INTEGER = match(EFFECT_INTEGER);
			 ((SkillDisarmMContext)_localctx).skillVal =  ((SkillDisarmMContext)_localctx).EFFECT_INTEGER.getText(); 
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
	public static class SkillSaveContext extends ParserRuleContext {
		public String skillVal;
		public Token EFFECT_INTEGER;
		public TerminalNode SKILL_SAVE() { return getToken(ShapeGrammar.SKILL_SAVE, 0); }
		public TerminalNode EFFECT_INTEGER() { return getToken(ShapeGrammar.EFFECT_INTEGER, 0); }
		public SkillSaveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_skillSave; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).enterSkillSave(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).exitSkillSave(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapeGrammarVisitor ) return ((ShapeGrammarVisitor<? extends T>)visitor).visitSkillSave(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SkillSaveContext skillSave() throws RecognitionException {
		SkillSaveContext _localctx = new SkillSaveContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_skillSave);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(68);
			match(SKILL_SAVE);
			setState(69);
			((SkillSaveContext)_localctx).EFFECT_INTEGER = match(EFFECT_INTEGER);
			 ((SkillSaveContext)_localctx).skillVal =  ((SkillSaveContext)_localctx).EFFECT_INTEGER.getText(); 
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
	public static class SkillStealthContext extends ParserRuleContext {
		public String skillVal;
		public Token EFFECT_INTEGER;
		public TerminalNode SKILL_STEALTH() { return getToken(ShapeGrammar.SKILL_STEALTH, 0); }
		public TerminalNode EFFECT_INTEGER() { return getToken(ShapeGrammar.EFFECT_INTEGER, 0); }
		public SkillStealthContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_skillStealth; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).enterSkillStealth(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).exitSkillStealth(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapeGrammarVisitor ) return ((ShapeGrammarVisitor<? extends T>)visitor).visitSkillStealth(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SkillStealthContext skillStealth() throws RecognitionException {
		SkillStealthContext _localctx = new SkillStealthContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_skillStealth);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			match(SKILL_STEALTH);
			setState(73);
			((SkillStealthContext)_localctx).EFFECT_INTEGER = match(EFFECT_INTEGER);
			 ((SkillStealthContext)_localctx).skillVal =  ((SkillStealthContext)_localctx).EFFECT_INTEGER.getText(); 
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
	public static class SkillSearchContext extends ParserRuleContext {
		public String skillVal;
		public Token EFFECT_INTEGER;
		public TerminalNode SKILL_SEARCH() { return getToken(ShapeGrammar.SKILL_SEARCH, 0); }
		public TerminalNode EFFECT_INTEGER() { return getToken(ShapeGrammar.EFFECT_INTEGER, 0); }
		public SkillSearchContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_skillSearch; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).enterSkillSearch(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).exitSkillSearch(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapeGrammarVisitor ) return ((ShapeGrammarVisitor<? extends T>)visitor).visitSkillSearch(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SkillSearchContext skillSearch() throws RecognitionException {
		SkillSearchContext _localctx = new SkillSearchContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_skillSearch);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(76);
			match(SKILL_SEARCH);
			setState(77);
			((SkillSearchContext)_localctx).EFFECT_INTEGER = match(EFFECT_INTEGER);
			 ((SkillSearchContext)_localctx).skillVal =  ((SkillSearchContext)_localctx).EFFECT_INTEGER.getText(); 
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
	public static class SkillMeleeContext extends ParserRuleContext {
		public String skillVal;
		public Token EFFECT_INTEGER;
		public TerminalNode SKILL_MELEE() { return getToken(ShapeGrammar.SKILL_MELEE, 0); }
		public TerminalNode EFFECT_INTEGER() { return getToken(ShapeGrammar.EFFECT_INTEGER, 0); }
		public SkillMeleeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_skillMelee; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).enterSkillMelee(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).exitSkillMelee(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapeGrammarVisitor ) return ((ShapeGrammarVisitor<? extends T>)visitor).visitSkillMelee(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SkillMeleeContext skillMelee() throws RecognitionException {
		SkillMeleeContext _localctx = new SkillMeleeContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_skillMelee);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
			match(SKILL_MELEE);
			setState(81);
			((SkillMeleeContext)_localctx).EFFECT_INTEGER = match(EFFECT_INTEGER);
			 ((SkillMeleeContext)_localctx).skillVal =  ((SkillMeleeContext)_localctx).EFFECT_INTEGER.getText(); 
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
	public static class SkillThrowContext extends ParserRuleContext {
		public String skillVal;
		public Token EFFECT_INTEGER;
		public TerminalNode SKILL_THROW() { return getToken(ShapeGrammar.SKILL_THROW, 0); }
		public TerminalNode EFFECT_INTEGER() { return getToken(ShapeGrammar.EFFECT_INTEGER, 0); }
		public SkillThrowContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_skillThrow; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).enterSkillThrow(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).exitSkillThrow(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapeGrammarVisitor ) return ((ShapeGrammarVisitor<? extends T>)visitor).visitSkillThrow(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SkillThrowContext skillThrow() throws RecognitionException {
		SkillThrowContext _localctx = new SkillThrowContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_skillThrow);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(84);
			match(SKILL_THROW);
			setState(85);
			((SkillThrowContext)_localctx).EFFECT_INTEGER = match(EFFECT_INTEGER);
			 ((SkillThrowContext)_localctx).skillVal =  ((SkillThrowContext)_localctx).EFFECT_INTEGER.getText(); 
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
	public static class SkillDigContext extends ParserRuleContext {
		public String skillVal;
		public Token EFFECT_INTEGER;
		public TerminalNode SKILL_DIG() { return getToken(ShapeGrammar.SKILL_DIG, 0); }
		public TerminalNode EFFECT_INTEGER() { return getToken(ShapeGrammar.EFFECT_INTEGER, 0); }
		public SkillDigContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_skillDig; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).enterSkillDig(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).exitSkillDig(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapeGrammarVisitor ) return ((ShapeGrammarVisitor<? extends T>)visitor).visitSkillDig(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SkillDigContext skillDig() throws RecognitionException {
		SkillDigContext _localctx = new SkillDigContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_skillDig);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(88);
			match(SKILL_DIG);
			setState(89);
			((SkillDigContext)_localctx).EFFECT_INTEGER = match(EFFECT_INTEGER);
			 ((SkillDigContext)_localctx).skillVal =  ((SkillDigContext)_localctx).EFFECT_INTEGER.getText(); 
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
	public static class ObjFlagsContext extends ParserRuleContext {
		public List<String> flags;
		public Token f1;
		public Token f2;
		public TerminalNode OBJ_FLAGS() { return getToken(ShapeGrammar.OBJ_FLAGS, 0); }
		public List<TerminalNode> FLAG_OR() { return getTokens(ShapeGrammar.FLAG_OR); }
		public TerminalNode FLAG_OR(int i) {
			return getToken(ShapeGrammar.FLAG_OR, i);
		}
		public List<TerminalNode> FLAG() { return getTokens(ShapeGrammar.FLAG); }
		public TerminalNode FLAG(int i) {
			return getToken(ShapeGrammar.FLAG, i);
		}
		public ObjFlagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objFlags; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).enterObjFlags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).exitObjFlags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapeGrammarVisitor ) return ((ShapeGrammarVisitor<? extends T>)visitor).visitObjFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjFlagsContext objFlags() throws RecognitionException {
		ObjFlagsContext _localctx = new ObjFlagsContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_objFlags);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			 ((ObjFlagsContext)_localctx).flags =  new ArrayList<>(); 
			setState(93);
			match(OBJ_FLAGS);
			setState(101);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(95); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(94);
						match(FLAG_OR);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(97); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				{
				setState(99);
				((ObjFlagsContext)_localctx).f1 = match(FLAG);

				                _localctx.flags.add(((ObjFlagsContext)_localctx).f1.getText());
				            
				}
				break;
			}
			setState(110);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FLAG_OR) {
				{
				{
				setState(103);
				match(FLAG_OR);
				setState(106);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FLAG) {
					{
					setState(104);
					((ObjFlagsContext)_localctx).f2 = match(FLAG);

					                _localctx.flags.add(((ObjFlagsContext)_localctx).f2.getText());
					            
					}
				}

				}
				}
				setState(112);
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
	public static class PlayerFlagsContext extends ParserRuleContext {
		public List<String> flags;
		public Token f1;
		public Token f2;
		public TerminalNode PLAYER_FLAGS() { return getToken(ShapeGrammar.PLAYER_FLAGS, 0); }
		public List<TerminalNode> FLAG_OR() { return getTokens(ShapeGrammar.FLAG_OR); }
		public TerminalNode FLAG_OR(int i) {
			return getToken(ShapeGrammar.FLAG_OR, i);
		}
		public List<TerminalNode> FLAG() { return getTokens(ShapeGrammar.FLAG); }
		public TerminalNode FLAG(int i) {
			return getToken(ShapeGrammar.FLAG, i);
		}
		public PlayerFlagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_playerFlags; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).enterPlayerFlags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).exitPlayerFlags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapeGrammarVisitor ) return ((ShapeGrammarVisitor<? extends T>)visitor).visitPlayerFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PlayerFlagsContext playerFlags() throws RecognitionException {
		PlayerFlagsContext _localctx = new PlayerFlagsContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_playerFlags);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			 ((PlayerFlagsContext)_localctx).flags =  new ArrayList<>(); 
			setState(114);
			match(PLAYER_FLAGS);
			setState(122);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(116); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(115);
						match(FLAG_OR);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(118); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				{
				setState(120);
				((PlayerFlagsContext)_localctx).f1 = match(FLAG);

				                _localctx.flags.add(((PlayerFlagsContext)_localctx).f1.getText());
				            
				}
				break;
			}
			setState(131);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FLAG_OR) {
				{
				{
				setState(124);
				match(FLAG_OR);
				setState(127);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FLAG) {
					{
					setState(125);
					((PlayerFlagsContext)_localctx).f2 = match(FLAG);

					                _localctx.flags.add(((PlayerFlagsContext)_localctx).f2.getText());
					            
					}
				}

				}
				}
				setState(133);
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
	public static class ValuesContext extends ParserRuleContext {
		public Map<String, String> valueMap;
		public Token vm0;
		public Token vi0;
		public Token vm1;
		public Token vi1;
		public Token vm2;
		public Token vi2;
		public TerminalNode VALUES() { return getToken(ShapeGrammar.VALUES, 0); }
		public List<TerminalNode> VALUE_L_BRACKET() { return getTokens(ShapeGrammar.VALUE_L_BRACKET); }
		public TerminalNode VALUE_L_BRACKET(int i) {
			return getToken(ShapeGrammar.VALUE_L_BRACKET, i);
		}
		public List<TerminalNode> VALUE_R_BRACKET() { return getTokens(ShapeGrammar.VALUE_R_BRACKET); }
		public TerminalNode VALUE_R_BRACKET(int i) {
			return getToken(ShapeGrammar.VALUE_R_BRACKET, i);
		}
		public List<TerminalNode> VALUE_OR() { return getTokens(ShapeGrammar.VALUE_OR); }
		public TerminalNode VALUE_OR(int i) {
			return getToken(ShapeGrammar.VALUE_OR, i);
		}
		public List<TerminalNode> VALUE_MOD() { return getTokens(ShapeGrammar.VALUE_MOD); }
		public TerminalNode VALUE_MOD(int i) {
			return getToken(ShapeGrammar.VALUE_MOD, i);
		}
		public List<TerminalNode> VALUE_INTRO_OR() { return getTokens(ShapeGrammar.VALUE_INTRO_OR); }
		public TerminalNode VALUE_INTRO_OR(int i) {
			return getToken(ShapeGrammar.VALUE_INTRO_OR, i);
		}
		public List<TerminalNode> VALUE_INT() { return getTokens(ShapeGrammar.VALUE_INT); }
		public TerminalNode VALUE_INT(int i) {
			return getToken(ShapeGrammar.VALUE_INT, i);
		}
		public ValuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_values; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).enterValues(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).exitValues(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapeGrammarVisitor ) return ((ShapeGrammarVisitor<? extends T>)visitor).visitValues(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValuesContext values() throws RecognitionException {
		ValuesContext _localctx = new ValuesContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_values);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{

			            ((ValuesContext)_localctx).valueMap =  new HashMap<>();
			            String vm = "";
			            String vi = "0";
			            
			setState(135);
			match(VALUES);
			setState(159);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VALUE_INTRO_OR:
				{
				setState(137); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(136);
					match(VALUE_INTRO_OR);
					}
					}
					setState(139); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==VALUE_INTRO_OR );
				setState(149);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VALUE_MOD) {
					{
					setState(141);
					((ValuesContext)_localctx).vm0 = match(VALUE_MOD);
					setState(142);
					match(VALUE_L_BRACKET);
					setState(145);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==VALUE_INT) {
						{
						setState(143);
						((ValuesContext)_localctx).vi0 = match(VALUE_INT);
						 vi = ((ValuesContext)_localctx).vi0.getText(); 
						}
					}

					setState(147);
					match(VALUE_R_BRACKET);

					            vm = ((ValuesContext)_localctx).vm0.getText();
					            _localctx.valueMap.put(vm, vi);
					            vi = "0";
					            
					}
				}

				}
				break;
			case VALUE_MOD:
				{
				setState(151);
				((ValuesContext)_localctx).vm1 = match(VALUE_MOD);
				setState(152);
				match(VALUE_L_BRACKET);
				setState(155);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VALUE_INT) {
					{
					setState(153);
					((ValuesContext)_localctx).vi1 = match(VALUE_INT);
					 vi = ((ValuesContext)_localctx).vi1.getText(); 
					}
				}

				setState(157);
				match(VALUE_R_BRACKET);

				            vm = ((ValuesContext)_localctx).vm1.getText();
				            _localctx.valueMap.put(vm, vi);
				            vi = "0";
				            
				}
				break;
			case EOF:
			case NAME:
			case COMBAT:
			case SKILL_D_P:
			case SKILL_D_M:
			case SKILL_SAVE:
			case SKILL_STEALTH:
			case SKILL_SEARCH:
			case SKILL_MELEE:
			case SKILL_THROW:
			case SKILL_DIG:
			case OBJ_FLAGS:
			case PLAYER_FLAGS:
			case VALUES:
			case BLOW:
			case EFFECT:
			case EFFECT_MESSAGE:
			case DICE:
			case EFFECT_YX:
			case EXPR:
			case VALUE_OR:
				break;
			default:
				break;
			}
			setState(180);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VALUE_OR) {
				{
				{
				setState(161);
				match(VALUE_OR);
				setState(165);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==VALUE_INTRO_OR) {
					{
					{
					setState(162);
					match(VALUE_INTRO_OR);
					}
					}
					setState(167);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(176);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VALUE_MOD) {
					{
					setState(168);
					((ValuesContext)_localctx).vm2 = match(VALUE_MOD);
					setState(169);
					match(VALUE_L_BRACKET);
					setState(172);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==VALUE_INT) {
						{
						setState(170);
						((ValuesContext)_localctx).vi2 = match(VALUE_INT);
						 vi = ((ValuesContext)_localctx).vi2.getText(); 
						}
					}

					setState(174);
					match(VALUE_R_BRACKET);

					            vm = ((ValuesContext)_localctx).vm2.getText();
					            _localctx.valueMap.put(vm, vi);
					            vi = "0";
					        
					}
				}

				}
				}
				setState(182);
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
	public static class BlowContext extends ParserRuleContext {
		public String blowStr;
		public Token STRING;
		public TerminalNode BLOW() { return getToken(ShapeGrammar.BLOW, 0); }
		public TerminalNode STRING() { return getToken(ShapeGrammar.STRING, 0); }
		public BlowContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_blow; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).enterBlow(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).exitBlow(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapeGrammarVisitor ) return ((ShapeGrammarVisitor<? extends T>)visitor).visitBlow(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlowContext blow() throws RecognitionException {
		BlowContext _localctx = new BlowContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_blow);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(183);
			match(BLOW);
			setState(184);
			((BlowContext)_localctx).STRING = match(STRING);
			 ((BlowContext)_localctx).blowStr =  ((BlowContext)_localctx).STRING.getText(); 
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
	public static class ShapeContext extends ParserRuleContext {
		public ShapeParseRecord shapeInit;
		public NameContext name;
		public CombatContext combat;
		public SkillDisarmPContext skillDisarmP;
		public SkillDisarmMContext skillDisarmM;
		public SkillSaveContext skillSave;
		public SkillStealthContext skillStealth;
		public SkillSearchContext skillSearch;
		public SkillMeleeContext skillMelee;
		public SkillThrowContext skillThrow;
		public SkillDigContext skillDig;
		public ObjFlagsContext objFlags;
		public PlayerFlagsContext playerFlags;
		public ValuesContext values;
		public EffectBlockContext effectBlock;
		public EffectMsgContext effectMsg;
		public EffectYXContext effectYX;
		public DiceContext dice;
		public ExprContext expr;
		public BlowContext blow;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public List<CombatContext> combat() {
			return getRuleContexts(CombatContext.class);
		}
		public CombatContext combat(int i) {
			return getRuleContext(CombatContext.class,i);
		}
		public List<SkillDisarmPContext> skillDisarmP() {
			return getRuleContexts(SkillDisarmPContext.class);
		}
		public SkillDisarmPContext skillDisarmP(int i) {
			return getRuleContext(SkillDisarmPContext.class,i);
		}
		public List<SkillDisarmMContext> skillDisarmM() {
			return getRuleContexts(SkillDisarmMContext.class);
		}
		public SkillDisarmMContext skillDisarmM(int i) {
			return getRuleContext(SkillDisarmMContext.class,i);
		}
		public List<SkillSaveContext> skillSave() {
			return getRuleContexts(SkillSaveContext.class);
		}
		public SkillSaveContext skillSave(int i) {
			return getRuleContext(SkillSaveContext.class,i);
		}
		public List<SkillStealthContext> skillStealth() {
			return getRuleContexts(SkillStealthContext.class);
		}
		public SkillStealthContext skillStealth(int i) {
			return getRuleContext(SkillStealthContext.class,i);
		}
		public List<SkillSearchContext> skillSearch() {
			return getRuleContexts(SkillSearchContext.class);
		}
		public SkillSearchContext skillSearch(int i) {
			return getRuleContext(SkillSearchContext.class,i);
		}
		public List<SkillMeleeContext> skillMelee() {
			return getRuleContexts(SkillMeleeContext.class);
		}
		public SkillMeleeContext skillMelee(int i) {
			return getRuleContext(SkillMeleeContext.class,i);
		}
		public List<SkillThrowContext> skillThrow() {
			return getRuleContexts(SkillThrowContext.class);
		}
		public SkillThrowContext skillThrow(int i) {
			return getRuleContext(SkillThrowContext.class,i);
		}
		public List<SkillDigContext> skillDig() {
			return getRuleContexts(SkillDigContext.class);
		}
		public SkillDigContext skillDig(int i) {
			return getRuleContext(SkillDigContext.class,i);
		}
		public List<ObjFlagsContext> objFlags() {
			return getRuleContexts(ObjFlagsContext.class);
		}
		public ObjFlagsContext objFlags(int i) {
			return getRuleContext(ObjFlagsContext.class,i);
		}
		public List<PlayerFlagsContext> playerFlags() {
			return getRuleContexts(PlayerFlagsContext.class);
		}
		public PlayerFlagsContext playerFlags(int i) {
			return getRuleContext(PlayerFlagsContext.class,i);
		}
		public List<ValuesContext> values() {
			return getRuleContexts(ValuesContext.class);
		}
		public ValuesContext values(int i) {
			return getRuleContext(ValuesContext.class,i);
		}
		public List<EffectBlockContext> effectBlock() {
			return getRuleContexts(EffectBlockContext.class);
		}
		public EffectBlockContext effectBlock(int i) {
			return getRuleContext(EffectBlockContext.class,i);
		}
		public List<EffectMsgContext> effectMsg() {
			return getRuleContexts(EffectMsgContext.class);
		}
		public EffectMsgContext effectMsg(int i) {
			return getRuleContext(EffectMsgContext.class,i);
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
		public List<BlowContext> blow() {
			return getRuleContexts(BlowContext.class);
		}
		public BlowContext blow(int i) {
			return getRuleContext(BlowContext.class,i);
		}
		public ShapeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_shape; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).enterShape(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).exitShape(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapeGrammarVisitor ) return ((ShapeGrammarVisitor<? extends T>)visitor).visitShape(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ShapeContext shape() throws RecognitionException {
		ShapeContext _localctx = new ShapeContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_shape);

		            String nameInit = "";
		            String combatTohInit = "";
		            String combatTodInit = "";
		            String combatToaInit = "";
		            String skillDisarmPhysInit = "";
		            String skillDisarmMagicInit = "";
		            String skillSaveInit = "";
		            String skillStealthInit = "";
		            String skillSearchInit = "";
		            String skillMeleeInit = "";
		            String skillThrowInit = "";
		            String skillDigInit = "";
		            List<String> oFlagsInit = new ArrayList<>();
		            List<String> pFlagsInit = new ArrayList<>();
		            Map<String, String> valuesInit = new HashMap<>();
		            List<EffectParseRecord> effectsInit = new ArrayList<>();
		            List<String> blowsInit = new ArrayList<>();
		            int line = 0;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(187);
			((ShapeContext)_localctx).name = name();
			 nameInit = ((ShapeContext)_localctx).name.nameStr;
			                   line = ((ShapeContext)_localctx).name.lineNo; 
			setState(245);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1835004L) != 0)) {
				{
				setState(243);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case COMBAT:
					{
					setState(189);
					((ShapeContext)_localctx).combat = combat();

					                combatTohInit = ((ShapeContext)_localctx).combat.toh;
					                combatTodInit = ((ShapeContext)_localctx).combat.tod;
					                combatToaInit = ((ShapeContext)_localctx).combat.toa;
					            
					}
					break;
				case SKILL_D_P:
					{
					setState(192);
					((ShapeContext)_localctx).skillDisarmP = skillDisarmP();
					 skillDisarmPhysInit = ((ShapeContext)_localctx).skillDisarmP.skillVal; 
					}
					break;
				case SKILL_D_M:
					{
					setState(195);
					((ShapeContext)_localctx).skillDisarmM = skillDisarmM();
					 skillDisarmMagicInit= ((ShapeContext)_localctx).skillDisarmM.skillVal; 
					}
					break;
				case SKILL_SAVE:
					{
					setState(198);
					((ShapeContext)_localctx).skillSave = skillSave();
					 skillSaveInit = ((ShapeContext)_localctx).skillSave.skillVal; 
					}
					break;
				case SKILL_STEALTH:
					{
					setState(201);
					((ShapeContext)_localctx).skillStealth = skillStealth();
					 skillStealthInit = ((ShapeContext)_localctx).skillStealth.skillVal; 
					}
					break;
				case SKILL_SEARCH:
					{
					setState(204);
					((ShapeContext)_localctx).skillSearch = skillSearch();
					 skillSearchInit = ((ShapeContext)_localctx).skillSearch.skillVal; 
					}
					break;
				case SKILL_MELEE:
					{
					setState(207);
					((ShapeContext)_localctx).skillMelee = skillMelee();
					 skillMeleeInit = ((ShapeContext)_localctx).skillMelee.skillVal; 
					}
					break;
				case SKILL_THROW:
					{
					setState(210);
					((ShapeContext)_localctx).skillThrow = skillThrow();
					 skillThrowInit = ((ShapeContext)_localctx).skillThrow.skillVal; 
					}
					break;
				case SKILL_DIG:
					{
					setState(213);
					((ShapeContext)_localctx).skillDig = skillDig();
					 skillDigInit = ((ShapeContext)_localctx).skillDig.skillVal; 
					}
					break;
				case OBJ_FLAGS:
					{
					setState(216);
					((ShapeContext)_localctx).objFlags = objFlags();
					 oFlagsInit.addAll(((ShapeContext)_localctx).objFlags.flags); 
					}
					break;
				case PLAYER_FLAGS:
					{
					setState(219);
					((ShapeContext)_localctx).playerFlags = playerFlags();
					 pFlagsInit.addAll(((ShapeContext)_localctx).playerFlags.flags); 
					}
					break;
				case VALUES:
					{
					setState(222);
					((ShapeContext)_localctx).values = values();
					 valuesInit.putAll(((ShapeContext)_localctx).values.valueMap); 
					}
					break;
				case EFFECT:
					{
					setState(225);
					((ShapeContext)_localctx).effectBlock = effectBlock();

					                effectsInit.add(new EffectParseRecord(((ShapeContext)_localctx).effectBlock.typeInit,
					                    ((ShapeContext)_localctx).effectBlock.subtypeWrapperInit, ((ShapeContext)_localctx).effectBlock.radius, ((ShapeContext)_localctx).effectBlock.other,
					                    ((ShapeContext)_localctx).effectBlock.diceString, ((ShapeContext)_localctx).effectBlock.yVal, ((ShapeContext)_localctx).effectBlock.xVal,
					                    ((ShapeContext)_localctx).effectBlock.expressionChars, ((ShapeContext)_localctx).effectBlock.expressionBase,
					                    ((ShapeContext)_localctx).effectBlock.expressionOperation,
					                    null, (((ShapeContext)_localctx).effectBlock!=null?(((ShapeContext)_localctx).effectBlock.start):null).getLine())); 
					}
					break;
				case EFFECT_MESSAGE:
					{
					setState(228);
					((ShapeContext)_localctx).effectMsg = effectMsg();

					                if (!effectsInit.isEmpty()) {
					                    EffectParseRecord last = effectsInit.getLast();
					                    replaceLast(effectsInit, last.appendMessage(((ShapeContext)_localctx).effectMsg.message));
					                }
					            
					}
					break;
				case EFFECT_YX:
					{
					setState(231);
					((ShapeContext)_localctx).effectYX = effectYX();

					                if (!effectsInit.isEmpty()) {
					                    EffectParseRecord last = effectsInit.getLast();
					                    EffectParseRecord updated = last.amendYX(((ShapeContext)_localctx).effectYX.y, ((ShapeContext)_localctx).effectYX.x);
					                    replaceLast(effectsInit, updated);
					                }
					            
					}
					break;
				case DICE:
					{
					setState(234);
					((ShapeContext)_localctx).dice = dice();

					                if (!effectsInit.isEmpty()) {
					                    EffectParseRecord last = effectsInit.getLast();
					                    String diceStr = ((ShapeContext)_localctx).dice.diceString;
					                    EffectParseRecord updated = last.amendExpression(diceStr,
					                                                                     ((ShapeContext)_localctx).dice.exprChar,
					                                                                     ((ShapeContext)_localctx).dice.baseName,
					                                                                     ((ShapeContext)_localctx).dice.operation);
					                    replaceLast(effectsInit, updated);
					                }
					            
					}
					break;
				case EXPR:
					{
					setState(237);
					((ShapeContext)_localctx).expr = expr();

					                if (!effectsInit.isEmpty() && !effectsInit.getLast().diceString().isEmpty()) {
					                    EffectParseRecord last = effectsInit.getLast();
					                    String lastDiceStr  = last.diceString();
					                    String lastExprChar = last.expressionChars().isEmpty() 
					                                        ? ((ShapeContext)_localctx).expr.exprChar
					                                        : last.expressionChars() + "^" + ((ShapeContext)_localctx).expr.exprChar;
					                    String lastExprBase = last.expressionBases().isEmpty() 
					                                        ? ((ShapeContext)_localctx).expr.baseName
					                                        : last.expressionBases() + "^" + ((ShapeContext)_localctx).expr.baseName;
					                    String lastExprOp   = last.expressionOperations().isEmpty() 
					                                        ? ((ShapeContext)_localctx).expr.operation
					                                        : last.expressionOperations() + "^" + ((ShapeContext)_localctx).expr.operation;
					                    EffectParseRecord updated = last.amendExpression(lastDiceStr,
					                                                lastExprChar,
					                                                lastExprBase,
					                                                lastExprOp);
					                    replaceLast(effectsInit, updated);
					                }
					            
					}
					break;
				case BLOW:
					{
					setState(240);
					((ShapeContext)_localctx).blow = blow();
					 blowsInit.add(((ShapeContext)_localctx).blow.blowStr); 
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(247);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			_ctx.stop = _input.LT(-1);

			            ((ShapeContext)_localctx).shapeInit =  new ShapeParseRecord(nameInit,
			                combatTohInit, combatTodInit, combatToaInit,
			                skillDisarmPhysInit, skillDisarmMagicInit,
			                skillSaveInit, skillStealthInit, skillSearchInit,
			                skillMeleeInit, skillThrowInit, skillDigInit,
			                oFlagsInit, pFlagsInit, valuesInit,
			                effectsInit, blowsInit, line);
			        
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
		public List<ShapeParseRecord> shapes;
		public ShapeContext shape;
		public TerminalNode EOF() { return getToken(ShapeGrammar.EOF, 0); }
		public List<ShapeContext> shape() {
			return getRuleContexts(ShapeContext.class);
		}
		public ShapeContext shape(int i) {
			return getRuleContext(ShapeContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapeGrammarVisitor ) return ((ShapeGrammarVisitor<? extends T>)visitor).visitFile(this);
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
			 ((FileContext)_localctx).shapes =  new ArrayList<>(); 
			setState(254);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NAME) {
				{
				{
				setState(249);
				((FileContext)_localctx).shape = shape();
				 _localctx.shapes.add(((FileContext)_localctx).shape.shapeInit); 
				}
				}
				setState(256);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(257);
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
		public TerminalNode EFFECT() { return getToken(ShapeGrammar.EFFECT, 0); }
		public TerminalNode TYPE() { return getToken(ShapeGrammar.TYPE, 0); }
		public TerminalNode SUBTYPE() { return getToken(ShapeGrammar.SUBTYPE, 0); }
		public List<TerminalNode> R_P_EFFECT_INTEGER() { return getTokens(ShapeGrammar.R_P_EFFECT_INTEGER); }
		public TerminalNode R_P_EFFECT_INTEGER(int i) {
			return getToken(ShapeGrammar.R_P_EFFECT_INTEGER, i);
		}
		public EffectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effect; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).enterEffect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).exitEffect(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapeGrammarVisitor ) return ((ShapeGrammarVisitor<? extends T>)visitor).visitEffect(this);
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
			setState(259);
			match(EFFECT);
			setState(260);
			((EffectContext)_localctx).t = match(TYPE);

			                ((EffectContext)_localctx).type =  ((EffectContext)_localctx).t.getText();
			            
			setState(272);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SUBTYPE) {
				{
				setState(262);
				((EffectContext)_localctx).st = match(SUBTYPE);

				                ((EffectContext)_localctx).wrapper =  ((EffectContext)_localctx).st.getText();
				            
				setState(270);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==R_P_EFFECT_INTEGER) {
					{
					setState(264);
					((EffectContext)_localctx).rad = match(R_P_EFFECT_INTEGER);

					                ((EffectContext)_localctx).radius =  ((EffectContext)_localctx).rad.getText();
					            
					setState(268);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==R_P_EFFECT_INTEGER) {
						{
						setState(266);
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
		public TerminalNode EFFECT_YX() { return getToken(ShapeGrammar.EFFECT_YX, 0); }
		public List<TerminalNode> YX_INTEGER() { return getTokens(ShapeGrammar.YX_INTEGER); }
		public TerminalNode YX_INTEGER(int i) {
			return getToken(ShapeGrammar.YX_INTEGER, i);
		}
		public EffectYXContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effectYX; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).enterEffectYX(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).exitEffectYX(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapeGrammarVisitor ) return ((ShapeGrammarVisitor<? extends T>)visitor).visitEffectYX(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectYXContext effectYX() throws RecognitionException {
		EffectYXContext _localctx = new EffectYXContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_effectYX);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(274);
			match(EFFECT_YX);
			setState(275);
			((EffectYXContext)_localctx).yVal = match(YX_INTEGER);
			setState(276);
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
		public TerminalNode DICE() { return getToken(ShapeGrammar.DICE, 0); }
		public TerminalNode DICE_SIMPLE_VALUE() { return getToken(ShapeGrammar.DICE_SIMPLE_VALUE, 0); }
		public TerminalNode DICE_COMPLEX_VALUE() { return getToken(ShapeGrammar.DICE_COMPLEX_VALUE, 0); }
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
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).enterDice(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).exitDice(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapeGrammarVisitor ) return ((ShapeGrammarVisitor<? extends T>)visitor).visitDice(this);
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
			setState(279);
			match(DICE);
			setState(292);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DICE_COMPLEX_VALUE:
				{
				{
				setState(280);
				((DiceContext)_localctx).val = match(DICE_COMPLEX_VALUE);

				                ((DiceContext)_localctx).diceString =  ((DiceContext)_localctx).val.getText();
				            
				setState(287);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(282);
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
					setState(289);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
				}
				}
				}
				break;
			case DICE_SIMPLE_VALUE:
				{
				setState(290);
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
		public TerminalNode EXPR() { return getToken(ShapeGrammar.EXPR, 0); }
		public List<TerminalNode> EXPR_COLON() { return getTokens(ShapeGrammar.EXPR_COLON); }
		public TerminalNode EXPR_COLON(int i) {
			return getToken(ShapeGrammar.EXPR_COLON, i);
		}
		public TerminalNode EXPR_CHAR() { return getToken(ShapeGrammar.EXPR_CHAR, 0); }
		public TerminalNode EXPR_UCASE() { return getToken(ShapeGrammar.EXPR_UCASE, 0); }
		public TerminalNode EXPR_OP() { return getToken(ShapeGrammar.EXPR_OP, 0); }
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).exitExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapeGrammarVisitor ) return ((ShapeGrammarVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(294);
			match(EXPR);
			setState(295);
			((ExprContext)_localctx).ch = match(EXPR_CHAR);
			setState(296);
			match(EXPR_COLON);
			setState(297);
			((ExprContext)_localctx).base = match(EXPR_UCASE);
			setState(298);
			match(EXPR_COLON);
			setState(299);
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
		public TerminalNode EFFECT_MESSAGE() { return getToken(ShapeGrammar.EFFECT_MESSAGE, 0); }
		public TerminalNode FREE_TEXT() { return getToken(ShapeGrammar.FREE_TEXT, 0); }
		public EffectMsgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effectMsg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).enterEffectMsg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).exitEffectMsg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapeGrammarVisitor ) return ((ShapeGrammarVisitor<? extends T>)visitor).visitEffectMsg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectMsgContext effectMsg() throws RecognitionException {
		EffectMsgContext _localctx = new EffectMsgContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_effectMsg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(302);
			match(EFFECT_MESSAGE);
			setState(303);
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
		public TerminalNode TIME() { return getToken(ShapeGrammar.TIME, 0); }
		public TerminalNode DICE_SIMPLE_VALUE() { return getToken(ShapeGrammar.DICE_SIMPLE_VALUE, 0); }
		public TimeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_time; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).enterTime(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).exitTime(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapeGrammarVisitor ) return ((ShapeGrammarVisitor<? extends T>)visitor).visitTime(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TimeContext time() throws RecognitionException {
		TimeContext _localctx = new TimeContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_time);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(306);
			match(TIME);
			setState(307);
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
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).enterStrayEffectLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).exitStrayEffectLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapeGrammarVisitor ) return ((ShapeGrammarVisitor<? extends T>)visitor).visitStrayEffectLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StrayEffectLineContext strayEffectLine() throws RecognitionException {
		StrayEffectLineContext _localctx = new StrayEffectLineContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_strayEffectLine);
		try {
			setState(313);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EFFECT_YX:
				enterOuterAlt(_localctx, 1);
				{
				setState(310);
				effectYX();
				}
				break;
			case DICE:
				enterOuterAlt(_localctx, 2);
				{
				setState(311);
				dice();
				}
				break;
			case EXPR:
				enterOuterAlt(_localctx, 3);
				{
				setState(312);
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
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).enterEffectBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ShapeGrammarListener ) ((ShapeGrammarListener)listener).exitEffectBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ShapeGrammarVisitor ) return ((ShapeGrammarVisitor<? extends T>)visitor).visitEffectBlock(this);
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
		        
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(315);
			((EffectBlockContext)_localctx).effect = effect();

			                ((EffectBlockContext)_localctx).lineNo =  _localctx.start.getLine();
			                ((EffectBlockContext)_localctx).typeInit =  ((EffectBlockContext)_localctx).effect.type;
			                ((EffectBlockContext)_localctx).subtypeWrapperInit =  ((EffectBlockContext)_localctx).effect.wrapper;
			                ((EffectBlockContext)_localctx).radius =  ((EffectBlockContext)_localctx).effect.radius;
			                ((EffectBlockContext)_localctx).other =  ((EffectBlockContext)_localctx).effect.other;
			            
			setState(333);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				{
				{
				setState(317);
				((EffectBlockContext)_localctx).effectYX = effectYX();

				                ((EffectBlockContext)_localctx).yVal =  ((EffectBlockContext)_localctx).effectYX.y;
				                ((EffectBlockContext)_localctx).xVal =  ((EffectBlockContext)_localctx).effectYX.x;
				            
				setState(319);
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
				setState(322);
				((EffectBlockContext)_localctx).dice = dice();

				                ((EffectBlockContext)_localctx).diceString =  ((EffectBlockContext)_localctx).dice.diceString;
				                expressionString = ((EffectBlockContext)_localctx).dice.exprChar;
				                baseString = ((EffectBlockContext)_localctx).dice.baseName;
				                opString = ((EffectBlockContext)_localctx).dice.operation;
				            
				setState(324);
				((EffectBlockContext)_localctx).effectYX = effectYX();

				                ((EffectBlockContext)_localctx).yVal =  ((EffectBlockContext)_localctx).effectYX.y;
				                ((EffectBlockContext)_localctx).xVal =  ((EffectBlockContext)_localctx).effectYX.x;
				            
				}
				}
				break;
			case 3:
				{
				setState(327);
				((EffectBlockContext)_localctx).dice = dice();

				                ((EffectBlockContext)_localctx).diceString =  ((EffectBlockContext)_localctx).dice.diceString;
				                expressionString = ((EffectBlockContext)_localctx).dice.exprChar;
				                baseString = ((EffectBlockContext)_localctx).dice.baseName;
				                opString = ((EffectBlockContext)_localctx).dice.operation;
				            
				}
				break;
			case 4:
				{
				setState(330);
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
		"\u0004\u0001;\u0150\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0004"+
		"\n`\b\n\u000b\n\f\na\u0001\n\u0001\n\u0003\nf\b\n\u0001\n\u0001\n\u0001"+
		"\n\u0003\nk\b\n\u0005\nm\b\n\n\n\f\np\t\n\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0004\u000bu\b\u000b\u000b\u000b\f\u000bv\u0001\u000b\u0001\u000b"+
		"\u0003\u000b{\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b"+
		"\u0080\b\u000b\u0005\u000b\u0082\b\u000b\n\u000b\f\u000b\u0085\t\u000b"+
		"\u0001\f\u0001\f\u0001\f\u0004\f\u008a\b\f\u000b\f\f\f\u008b\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0003\f\u0092\b\f\u0001\f\u0001\f\u0003\f\u0096\b\f"+
		"\u0001\f\u0001\f\u0001\f\u0001\f\u0003\f\u009c\b\f\u0001\f\u0001\f\u0003"+
		"\f\u00a0\b\f\u0001\f\u0001\f\u0005\f\u00a4\b\f\n\f\f\f\u00a7\t\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0003\f\u00ad\b\f\u0001\f\u0001\f\u0003\f\u00b1"+
		"\b\f\u0005\f\u00b3\b\f\n\f\f\f\u00b6\t\f\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0005\u000e\u00f4\b\u000e\n\u000e\f\u000e\u00f7"+
		"\t\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0005\u000f\u00fd"+
		"\b\u000f\n\u000f\f\u000f\u0100\t\u000f\u0001\u000f\u0001\u000f\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0003\u0010\u010d\b\u0010\u0003\u0010\u010f\b"+
		"\u0010\u0003\u0010\u0111\b\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001"+
		"\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0005\u0012\u011e\b\u0012\n\u0012\f\u0012\u0121\t\u0012"+
		"\u0001\u0012\u0001\u0012\u0003\u0012\u0125\b\u0012\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016"+
		"\u013a\b\u0016\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017"+
		"\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017"+
		"\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017"+
		"\u0001\u0017\u0003\u0017\u014e\b\u0017\u0001\u0017\u0000\u0000\u0018\u0000"+
		"\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c"+
		"\u001e \"$&(*,.\u0000\u0000\u0169\u00000\u0001\u0000\u0000\u0000\u0002"+
		"4\u0001\u0000\u0000\u0000\u0004<\u0001\u0000\u0000\u0000\u0006@\u0001"+
		"\u0000\u0000\u0000\bD\u0001\u0000\u0000\u0000\nH\u0001\u0000\u0000\u0000"+
		"\fL\u0001\u0000\u0000\u0000\u000eP\u0001\u0000\u0000\u0000\u0010T\u0001"+
		"\u0000\u0000\u0000\u0012X\u0001\u0000\u0000\u0000\u0014\\\u0001\u0000"+
		"\u0000\u0000\u0016q\u0001\u0000\u0000\u0000\u0018\u0086\u0001\u0000\u0000"+
		"\u0000\u001a\u00b7\u0001\u0000\u0000\u0000\u001c\u00bb\u0001\u0000\u0000"+
		"\u0000\u001e\u00f8\u0001\u0000\u0000\u0000 \u0103\u0001\u0000\u0000\u0000"+
		"\"\u0112\u0001\u0000\u0000\u0000$\u0117\u0001\u0000\u0000\u0000&\u0126"+
		"\u0001\u0000\u0000\u0000(\u012e\u0001\u0000\u0000\u0000*\u0132\u0001\u0000"+
		"\u0000\u0000,\u0139\u0001\u0000\u0000\u0000.\u013b\u0001\u0000\u0000\u0000"+
		"01\u0005\u0001\u0000\u000012\u0005\'\u0000\u000023\u0006\u0000\uffff\uffff"+
		"\u00003\u0001\u0001\u0000\u0000\u000045\u0005\u0002\u0000\u000056\u0005"+
		"\u0016\u0000\u000067\u0005\u0015\u0000\u000078\u0005\u0016\u0000\u0000"+
		"89\u0005\u0015\u0000\u00009:\u0005\u0016\u0000\u0000:;\u0006\u0001\uffff"+
		"\uffff\u0000;\u0003\u0001\u0000\u0000\u0000<=\u0005\u0003\u0000\u0000"+
		"=>\u0005\u0016\u0000\u0000>?\u0006\u0002\uffff\uffff\u0000?\u0005\u0001"+
		"\u0000\u0000\u0000@A\u0005\u0004\u0000\u0000AB\u0005\u0016\u0000\u0000"+
		"BC\u0006\u0003\uffff\uffff\u0000C\u0007\u0001\u0000\u0000\u0000DE\u0005"+
		"\u0005\u0000\u0000EF\u0005\u0016\u0000\u0000FG\u0006\u0004\uffff\uffff"+
		"\u0000G\t\u0001\u0000\u0000\u0000HI\u0005\u0006\u0000\u0000IJ\u0005\u0016"+
		"\u0000\u0000JK\u0006\u0005\uffff\uffff\u0000K\u000b\u0001\u0000\u0000"+
		"\u0000LM\u0005\u0007\u0000\u0000MN\u0005\u0016\u0000\u0000NO\u0006\u0006"+
		"\uffff\uffff\u0000O\r\u0001\u0000\u0000\u0000PQ\u0005\b\u0000\u0000QR"+
		"\u0005\u0016\u0000\u0000RS\u0006\u0007\uffff\uffff\u0000S\u000f\u0001"+
		"\u0000\u0000\u0000TU\u0005\t\u0000\u0000UV\u0005\u0016\u0000\u0000VW\u0006"+
		"\b\uffff\uffff\u0000W\u0011\u0001\u0000\u0000\u0000XY\u0005\n\u0000\u0000"+
		"YZ\u0005\u0016\u0000\u0000Z[\u0006\t\uffff\uffff\u0000[\u0013\u0001\u0000"+
		"\u0000\u0000\\]\u0006\n\uffff\uffff\u0000]e\u0005\u000b\u0000\u0000^`"+
		"\u0005\u001d\u0000\u0000_^\u0001\u0000\u0000\u0000`a\u0001\u0000\u0000"+
		"\u0000a_\u0001\u0000\u0000\u0000ab\u0001\u0000\u0000\u0000bf\u0001\u0000"+
		"\u0000\u0000cd\u0005\u001c\u0000\u0000df\u0006\n\uffff\uffff\u0000e_\u0001"+
		"\u0000\u0000\u0000ec\u0001\u0000\u0000\u0000ef\u0001\u0000\u0000\u0000"+
		"fn\u0001\u0000\u0000\u0000gj\u0005\u001d\u0000\u0000hi\u0005\u001c\u0000"+
		"\u0000ik\u0006\n\uffff\uffff\u0000jh\u0001\u0000\u0000\u0000jk\u0001\u0000"+
		"\u0000\u0000km\u0001\u0000\u0000\u0000lg\u0001\u0000\u0000\u0000mp\u0001"+
		"\u0000\u0000\u0000nl\u0001\u0000\u0000\u0000no\u0001\u0000\u0000\u0000"+
		"o\u0015\u0001\u0000\u0000\u0000pn\u0001\u0000\u0000\u0000qr\u0006\u000b"+
		"\uffff\uffff\u0000rz\u0005\f\u0000\u0000su\u0005\u001d\u0000\u0000ts\u0001"+
		"\u0000\u0000\u0000uv\u0001\u0000\u0000\u0000vt\u0001\u0000\u0000\u0000"+
		"vw\u0001\u0000\u0000\u0000w{\u0001\u0000\u0000\u0000xy\u0005\u001c\u0000"+
		"\u0000y{\u0006\u000b\uffff\uffff\u0000zt\u0001\u0000\u0000\u0000zx\u0001"+
		"\u0000\u0000\u0000z{\u0001\u0000\u0000\u0000{\u0083\u0001\u0000\u0000"+
		"\u0000|\u007f\u0005\u001d\u0000\u0000}~\u0005\u001c\u0000\u0000~\u0080"+
		"\u0006\u000b\uffff\uffff\u0000\u007f}\u0001\u0000\u0000\u0000\u007f\u0080"+
		"\u0001\u0000\u0000\u0000\u0080\u0082\u0001\u0000\u0000\u0000\u0081|\u0001"+
		"\u0000\u0000\u0000\u0082\u0085\u0001\u0000\u0000\u0000\u0083\u0081\u0001"+
		"\u0000\u0000\u0000\u0083\u0084\u0001\u0000\u0000\u0000\u0084\u0017\u0001"+
		"\u0000\u0000\u0000\u0085\u0083\u0001\u0000\u0000\u0000\u0086\u0087\u0006"+
		"\f\uffff\uffff\u0000\u0087\u009f\u0005\r\u0000\u0000\u0088\u008a\u0005"+
		" \u0000\u0000\u0089\u0088\u0001\u0000\u0000\u0000\u008a\u008b\u0001\u0000"+
		"\u0000\u0000\u008b\u0089\u0001\u0000\u0000\u0000\u008b\u008c\u0001\u0000"+
		"\u0000\u0000\u008c\u0095\u0001\u0000\u0000\u0000\u008d\u008e\u0005\u001f"+
		"\u0000\u0000\u008e\u0091\u0005\"\u0000\u0000\u008f\u0090\u0005#\u0000"+
		"\u0000\u0090\u0092\u0006\f\uffff\uffff\u0000\u0091\u008f\u0001\u0000\u0000"+
		"\u0000\u0091\u0092\u0001\u0000\u0000\u0000\u0092\u0093\u0001\u0000\u0000"+
		"\u0000\u0093\u0094\u0005$\u0000\u0000\u0094\u0096\u0006\f\uffff\uffff"+
		"\u0000\u0095\u008d\u0001\u0000\u0000\u0000\u0095\u0096\u0001\u0000\u0000"+
		"\u0000\u0096\u00a0\u0001\u0000\u0000\u0000\u0097\u0098\u0005\u001f\u0000"+
		"\u0000\u0098\u009b\u0005\"\u0000\u0000\u0099\u009a\u0005#\u0000\u0000"+
		"\u009a\u009c\u0006\f\uffff\uffff\u0000\u009b\u0099\u0001\u0000\u0000\u0000"+
		"\u009b\u009c\u0001\u0000\u0000\u0000\u009c\u009d\u0001\u0000\u0000\u0000"+
		"\u009d\u009e\u0005$\u0000\u0000\u009e\u00a0\u0006\f\uffff\uffff\u0000"+
		"\u009f\u0089\u0001\u0000\u0000\u0000\u009f\u0097\u0001\u0000\u0000\u0000"+
		"\u009f\u00a0\u0001\u0000\u0000\u0000\u00a0\u00b4\u0001\u0000\u0000\u0000"+
		"\u00a1\u00a5\u0005%\u0000\u0000\u00a2\u00a4\u0005 \u0000\u0000\u00a3\u00a2"+
		"\u0001\u0000\u0000\u0000\u00a4\u00a7\u0001\u0000\u0000\u0000\u00a5\u00a3"+
		"\u0001\u0000\u0000\u0000\u00a5\u00a6\u0001\u0000\u0000\u0000\u00a6\u00b0"+
		"\u0001\u0000\u0000\u0000\u00a7\u00a5\u0001\u0000\u0000\u0000\u00a8\u00a9"+
		"\u0005\u001f\u0000\u0000\u00a9\u00ac\u0005\"\u0000\u0000\u00aa\u00ab\u0005"+
		"#\u0000\u0000\u00ab\u00ad\u0006\f\uffff\uffff\u0000\u00ac\u00aa\u0001"+
		"\u0000\u0000\u0000\u00ac\u00ad\u0001\u0000\u0000\u0000\u00ad\u00ae\u0001"+
		"\u0000\u0000\u0000\u00ae\u00af\u0005$\u0000\u0000\u00af\u00b1\u0006\f"+
		"\uffff\uffff\u0000\u00b0\u00a8\u0001\u0000\u0000\u0000\u00b0\u00b1\u0001"+
		"\u0000\u0000\u0000\u00b1\u00b3\u0001\u0000\u0000\u0000\u00b2\u00a1\u0001"+
		"\u0000\u0000\u0000\u00b3\u00b6\u0001\u0000\u0000\u0000\u00b4\u00b2\u0001"+
		"\u0000\u0000\u0000\u00b4\u00b5\u0001\u0000\u0000\u0000\u00b5\u0019\u0001"+
		"\u0000\u0000\u0000\u00b6\u00b4\u0001\u0000\u0000\u0000\u00b7\u00b8\u0005"+
		"\u000e\u0000\u0000\u00b8\u00b9\u0005\'\u0000\u0000\u00b9\u00ba\u0006\r"+
		"\uffff\uffff\u0000\u00ba\u001b\u0001\u0000\u0000\u0000\u00bb\u00bc\u0003"+
		"\u0000\u0000\u0000\u00bc\u00f5\u0006\u000e\uffff\uffff\u0000\u00bd\u00be"+
		"\u0003\u0002\u0001\u0000\u00be\u00bf\u0006\u000e\uffff\uffff\u0000\u00bf"+
		"\u00f4\u0001\u0000\u0000\u0000\u00c0\u00c1\u0003\u0004\u0002\u0000\u00c1"+
		"\u00c2\u0006\u000e\uffff\uffff\u0000\u00c2\u00f4\u0001\u0000\u0000\u0000"+
		"\u00c3\u00c4\u0003\u0006\u0003\u0000\u00c4\u00c5\u0006\u000e\uffff\uffff"+
		"\u0000\u00c5\u00f4\u0001\u0000\u0000\u0000\u00c6\u00c7\u0003\b\u0004\u0000"+
		"\u00c7\u00c8\u0006\u000e\uffff\uffff\u0000\u00c8\u00f4\u0001\u0000\u0000"+
		"\u0000\u00c9\u00ca\u0003\n\u0005\u0000\u00ca\u00cb\u0006\u000e\uffff\uffff"+
		"\u0000\u00cb\u00f4\u0001\u0000\u0000\u0000\u00cc\u00cd\u0003\f\u0006\u0000"+
		"\u00cd\u00ce\u0006\u000e\uffff\uffff\u0000\u00ce\u00f4\u0001\u0000\u0000"+
		"\u0000\u00cf\u00d0\u0003\u000e\u0007\u0000\u00d0\u00d1\u0006\u000e\uffff"+
		"\uffff\u0000\u00d1\u00f4\u0001\u0000\u0000\u0000\u00d2\u00d3\u0003\u0010"+
		"\b\u0000\u00d3\u00d4\u0006\u000e\uffff\uffff\u0000\u00d4\u00f4\u0001\u0000"+
		"\u0000\u0000\u00d5\u00d6\u0003\u0012\t\u0000\u00d6\u00d7\u0006\u000e\uffff"+
		"\uffff\u0000\u00d7\u00f4\u0001\u0000\u0000\u0000\u00d8\u00d9\u0003\u0014"+
		"\n\u0000\u00d9\u00da\u0006\u000e\uffff\uffff\u0000\u00da\u00f4\u0001\u0000"+
		"\u0000\u0000\u00db\u00dc\u0003\u0016\u000b\u0000\u00dc\u00dd\u0006\u000e"+
		"\uffff\uffff\u0000\u00dd\u00f4\u0001\u0000\u0000\u0000\u00de\u00df\u0003"+
		"\u0018\f\u0000\u00df\u00e0\u0006\u000e\uffff\uffff\u0000\u00e0\u00f4\u0001"+
		"\u0000\u0000\u0000\u00e1\u00e2\u0003.\u0017\u0000\u00e2\u00e3\u0006\u000e"+
		"\uffff\uffff\u0000\u00e3\u00f4\u0001\u0000\u0000\u0000\u00e4\u00e5\u0003"+
		"(\u0014\u0000\u00e5\u00e6\u0006\u000e\uffff\uffff\u0000\u00e6\u00f4\u0001"+
		"\u0000\u0000\u0000\u00e7\u00e8\u0003\"\u0011\u0000\u00e8\u00e9\u0006\u000e"+
		"\uffff\uffff\u0000\u00e9\u00f4\u0001\u0000\u0000\u0000\u00ea\u00eb\u0003"+
		"$\u0012\u0000\u00eb\u00ec\u0006\u000e\uffff\uffff\u0000\u00ec\u00f4\u0001"+
		"\u0000\u0000\u0000\u00ed\u00ee\u0003&\u0013\u0000\u00ee\u00ef\u0006\u000e"+
		"\uffff\uffff\u0000\u00ef\u00f4\u0001\u0000\u0000\u0000\u00f0\u00f1\u0003"+
		"\u001a\r\u0000\u00f1\u00f2\u0006\u000e\uffff\uffff\u0000\u00f2\u00f4\u0001"+
		"\u0000\u0000\u0000\u00f3\u00bd\u0001\u0000\u0000\u0000\u00f3\u00c0\u0001"+
		"\u0000\u0000\u0000\u00f3\u00c3\u0001\u0000\u0000\u0000\u00f3\u00c6\u0001"+
		"\u0000\u0000\u0000\u00f3\u00c9\u0001\u0000\u0000\u0000\u00f3\u00cc\u0001"+
		"\u0000\u0000\u0000\u00f3\u00cf\u0001\u0000\u0000\u0000\u00f3\u00d2\u0001"+
		"\u0000\u0000\u0000\u00f3\u00d5\u0001\u0000\u0000\u0000\u00f3\u00d8\u0001"+
		"\u0000\u0000\u0000\u00f3\u00db\u0001\u0000\u0000\u0000\u00f3\u00de\u0001"+
		"\u0000\u0000\u0000\u00f3\u00e1\u0001\u0000\u0000\u0000\u00f3\u00e4\u0001"+
		"\u0000\u0000\u0000\u00f3\u00e7\u0001\u0000\u0000\u0000\u00f3\u00ea\u0001"+
		"\u0000\u0000\u0000\u00f3\u00ed\u0001\u0000\u0000\u0000\u00f3\u00f0\u0001"+
		"\u0000\u0000\u0000\u00f4\u00f7\u0001\u0000\u0000\u0000\u00f5\u00f3\u0001"+
		"\u0000\u0000\u0000\u00f5\u00f6\u0001\u0000\u0000\u0000\u00f6\u001d\u0001"+
		"\u0000\u0000\u0000\u00f7\u00f5\u0001\u0000\u0000\u0000\u00f8\u00fe\u0006"+
		"\u000f\uffff\uffff\u0000\u00f9\u00fa\u0003\u001c\u000e\u0000\u00fa\u00fb"+
		"\u0006\u000f\uffff\uffff\u0000\u00fb\u00fd\u0001\u0000\u0000\u0000\u00fc"+
		"\u00f9\u0001\u0000\u0000\u0000\u00fd\u0100\u0001\u0000\u0000\u0000\u00fe"+
		"\u00fc\u0001\u0000\u0000\u0000\u00fe\u00ff\u0001\u0000\u0000\u0000\u00ff"+
		"\u0101\u0001\u0000\u0000\u0000\u0100\u00fe\u0001\u0000\u0000\u0000\u0101"+
		"\u0102\u0005\u0000\u0000\u0001\u0102\u001f\u0001\u0000\u0000\u0000\u0103"+
		"\u0104\u0005\u000f\u0000\u0000\u0104\u0105\u0005+\u0000\u0000\u0105\u0110"+
		"\u0006\u0010\uffff\uffff\u0000\u0106\u0107\u0005.\u0000\u0000\u0107\u010e"+
		"\u0006\u0010\uffff\uffff\u0000\u0108\u0109\u00051\u0000\u0000\u0109\u010c"+
		"\u0006\u0010\uffff\uffff\u0000\u010a\u010b\u00051\u0000\u0000\u010b\u010d"+
		"\u0006\u0010\uffff\uffff\u0000\u010c\u010a\u0001\u0000\u0000\u0000\u010c"+
		"\u010d\u0001\u0000\u0000\u0000\u010d\u010f\u0001\u0000\u0000\u0000\u010e"+
		"\u0108\u0001\u0000\u0000\u0000\u010e\u010f\u0001\u0000\u0000\u0000\u010f"+
		"\u0111\u0001\u0000\u0000\u0000\u0110\u0106\u0001\u0000\u0000\u0000\u0110"+
		"\u0111\u0001\u0000\u0000\u0000\u0111!\u0001\u0000\u0000\u0000\u0112\u0113"+
		"\u0005\u0013\u0000\u0000\u0113\u0114\u0005(\u0000\u0000\u0114\u0115\u0005"+
		"(\u0000\u0000\u0115\u0116\u0006\u0011\uffff\uffff\u0000\u0116#\u0001\u0000"+
		"\u0000\u0000\u0117\u0124\u0005\u0011\u0000\u0000\u0118\u0119\u00056\u0000"+
		"\u0000\u0119\u011f\u0006\u0012\uffff\uffff\u0000\u011a\u011b\u0003&\u0013"+
		"\u0000\u011b\u011c\u0006\u0012\uffff\uffff\u0000\u011c\u011e\u0001\u0000"+
		"\u0000\u0000\u011d\u011a\u0001\u0000\u0000\u0000\u011e\u0121\u0001\u0000"+
		"\u0000\u0000\u011f\u011d\u0001\u0000\u0000\u0000\u011f\u0120\u0001\u0000"+
		"\u0000\u0000\u0120\u0125\u0001\u0000\u0000\u0000\u0121\u011f\u0001\u0000"+
		"\u0000\u0000\u0122\u0123\u00055\u0000\u0000\u0123\u0125\u0006\u0012\uffff"+
		"\uffff\u0000\u0124\u0118\u0001\u0000\u0000\u0000\u0124\u0122\u0001\u0000"+
		"\u0000\u0000\u0125%\u0001\u0000\u0000\u0000\u0126\u0127\u0005\u0014\u0000"+
		"\u0000\u0127\u0128\u00057\u0000\u0000\u0128\u0129\u00058\u0000\u0000\u0129"+
		"\u012a\u00059\u0000\u0000\u012a\u012b\u00058\u0000\u0000\u012b\u012c\u0005"+
		":\u0000\u0000\u012c\u012d\u0006\u0013\uffff\uffff\u0000\u012d\'\u0001"+
		"\u0000\u0000\u0000\u012e\u012f\u0005\u0010\u0000\u0000\u012f\u0130\u0005"+
		"4\u0000\u0000\u0130\u0131\u0006\u0014\uffff\uffff\u0000\u0131)\u0001\u0000"+
		"\u0000\u0000\u0132\u0133\u0005\u0012\u0000\u0000\u0133\u0134\u00055\u0000"+
		"\u0000\u0134\u0135\u0006\u0015\uffff\uffff\u0000\u0135+\u0001\u0000\u0000"+
		"\u0000\u0136\u013a\u0003\"\u0011\u0000\u0137\u013a\u0003$\u0012\u0000"+
		"\u0138\u013a\u0003&\u0013\u0000\u0139\u0136\u0001\u0000\u0000\u0000\u0139"+
		"\u0137\u0001\u0000\u0000\u0000\u0139\u0138\u0001\u0000\u0000\u0000\u013a"+
		"-\u0001\u0000\u0000\u0000\u013b\u013c\u0003 \u0010\u0000\u013c\u014d\u0006"+
		"\u0017\uffff\uffff\u0000\u013d\u013e\u0003\"\u0011\u0000\u013e\u013f\u0006"+
		"\u0017\uffff\uffff\u0000\u013f\u0140\u0003$\u0012\u0000\u0140\u0141\u0006"+
		"\u0017\uffff\uffff\u0000\u0141\u014e\u0001\u0000\u0000\u0000\u0142\u0143"+
		"\u0003$\u0012\u0000\u0143\u0144\u0006\u0017\uffff\uffff\u0000\u0144\u0145"+
		"\u0003\"\u0011\u0000\u0145\u0146\u0006\u0017\uffff\uffff\u0000\u0146\u014e"+
		"\u0001\u0000\u0000\u0000\u0147\u0148\u0003$\u0012\u0000\u0148\u0149\u0006"+
		"\u0017\uffff\uffff\u0000\u0149\u014e\u0001\u0000\u0000\u0000\u014a\u014b"+
		"\u0003\"\u0011\u0000\u014b\u014c\u0006\u0017\uffff\uffff\u0000\u014c\u014e"+
		"\u0001\u0000\u0000\u0000\u014d\u013d\u0001\u0000\u0000\u0000\u014d\u0142"+
		"\u0001\u0000\u0000\u0000\u014d\u0147\u0001\u0000\u0000\u0000\u014d\u014a"+
		"\u0001\u0000\u0000\u0000\u014d\u014e\u0001\u0000\u0000\u0000\u014e/\u0001"+
		"\u0000\u0000\u0000\u001baejnvz\u007f\u0083\u008b\u0091\u0095\u009b\u009f"+
		"\u00a5\u00ac\u00b0\u00b4\u00f3\u00f5\u00fe\u010c\u010e\u0110\u011f\u0124"+
		"\u0139\u014d";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}