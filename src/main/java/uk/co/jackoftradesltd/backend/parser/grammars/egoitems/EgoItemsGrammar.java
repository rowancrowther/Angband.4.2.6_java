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

// Generated from EgoItemsGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.egoitems;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.backend.parser.curse.FullCurseDataParseRecord;
import uk.co.jackoftradesltd.backend.parser.egoitem.EgoItemParseRecord;
import uk.co.jackoftradesltd.backend.parser.egoitem.EgoItemParseRecord.ItemRef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class EgoItemsGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, INFO=2, ALLOC=3, COMBAT=4, MIN_COMBAT=5, TYPE=6, ITEM=7, FLAGS=8, 
		FLAGS_OFF=9, VALUES=10, MIN_VALUES=11, ACT=12, TIME=13, BRAND=14, SLAY=15, 
		CURSE=16, DESC=17, INTEGER=18, COLON=19, COMMENT=20, INDENT=21, EOL=22, 
		SIMPLE_DICE_STRING=23, COMPLEX_DICE_STRING=24, CURSE_NAME=25, CURSE_COLON=26, 
		CURSE_EOL=27, COMBAT_DICE=28, COMBAT_COLON=29, COMBAT_EOL=30, ALLOC_UINT=31, 
		ALLOC_COLON=32, ALLOC_TO=33, ALLOC_EOL=34, ALLOC_INT=35, VALUE_FLAG=36, 
		VALUE_LBRACKET=37, VALUE_DICE=38, VALUE_INTEGER=39, VALUE_RBRACKET=40, 
		VALUE_OR=41, VALUE_EOL=42, FLAG=43, FLAG_OR=44, FLAG_EOL=45, STRING=46, 
		FREE_TEXT_EOL=47, ITEM_STRING=48, ITEM_COLON=49, ITEM_EOL=50;
	public static final int
		RULE_name = 0, RULE_info = 1, RULE_alloc = 2, RULE_combat = 3, RULE_minCombat = 4, 
		RULE_type = 5, RULE_item = 6, RULE_flags = 7, RULE_flagsOff = 8, RULE_values = 9, 
		RULE_minValues = 10, RULE_act = 11, RULE_time = 12, RULE_brand = 13, RULE_slay = 14, 
		RULE_curse = 15, RULE_desc = 16, RULE_egoItem = 17, RULE_file = 18;
	private static String[] makeRuleNames() {
		return new String[] {
			"name", "info", "alloc", "combat", "minCombat", "type", "item", "flags", 
			"flagsOff", "values", "minValues", "act", "time", "brand", "slay", "curse", 
			"desc", "egoItem", "file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'info:'", "'alloc:'", "'combat:'", "'min-combat:'", 
			"'type:'", "'item:'", "'flags:'", "'flags-off:'", "'values:'", "'min-values:'", 
			"'act:'", "'time:'", "'brand:'", "'slay:'", "'curse:'", "'desc:'", null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, "'['", null, null, "']'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "INFO", "ALLOC", "COMBAT", "MIN_COMBAT", "TYPE", "ITEM", 
			"FLAGS", "FLAGS_OFF", "VALUES", "MIN_VALUES", "ACT", "TIME", "BRAND", 
			"SLAY", "CURSE", "DESC", "INTEGER", "COLON", "COMMENT", "INDENT", "EOL", 
			"SIMPLE_DICE_STRING", "COMPLEX_DICE_STRING", "CURSE_NAME", "CURSE_COLON", 
			"CURSE_EOL", "COMBAT_DICE", "COMBAT_COLON", "COMBAT_EOL", "ALLOC_UINT", 
			"ALLOC_COLON", "ALLOC_TO", "ALLOC_EOL", "ALLOC_INT", "VALUE_FLAG", "VALUE_LBRACKET", 
			"VALUE_DICE", "VALUE_INTEGER", "VALUE_RBRACKET", "VALUE_OR", "VALUE_EOL", 
			"FLAG", "FLAG_OR", "FLAG_EOL", "STRING", "FREE_TEXT_EOL", "ITEM_STRING", 
			"ITEM_COLON", "ITEM_EOL"
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
	public String getGrammarFileName() { return "EgoItemsGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public EgoItemsGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public int lineNo;
		public Token STRING;
		public TerminalNode NAME() { return getToken(EgoItemsGrammar.NAME, 0); }
		public TerminalNode STRING() { return getToken(EgoItemsGrammar.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EgoItemsGrammarVisitor ) return ((EgoItemsGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(38);
			match(NAME);
			setState(39);
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
	public static class InfoContext extends ParserRuleContext {
		public String cost;
		public String rating;
		public Token c;
		public Token r;
		public TerminalNode INFO() { return getToken(EgoItemsGrammar.INFO, 0); }
		public TerminalNode COLON() { return getToken(EgoItemsGrammar.COLON, 0); }
		public List<TerminalNode> INTEGER() { return getTokens(EgoItemsGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(EgoItemsGrammar.INTEGER, i);
		}
		public InfoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_info; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).enterInfo(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).exitInfo(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EgoItemsGrammarVisitor ) return ((EgoItemsGrammarVisitor<? extends T>)visitor).visitInfo(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InfoContext info() throws RecognitionException {
		InfoContext _localctx = new InfoContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_info);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(42);
			match(INFO);
			setState(43);
			((InfoContext)_localctx).c = match(INTEGER);
			setState(44);
			match(COLON);
			setState(45);
			((InfoContext)_localctx).r = match(INTEGER);

			                ((InfoContext)_localctx).cost =  ((InfoContext)_localctx).c.getText();
			                ((InfoContext)_localctx).rating =  ((InfoContext)_localctx).r.getText();
			            
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
	public static class AllocContext extends ParserRuleContext {
		public String common;
		public String lower;
		public String upper;
		public Token c;
		public Token l;
		public Token u;
		public TerminalNode ALLOC() { return getToken(EgoItemsGrammar.ALLOC, 0); }
		public TerminalNode ALLOC_COLON() { return getToken(EgoItemsGrammar.ALLOC_COLON, 0); }
		public TerminalNode ALLOC_TO() { return getToken(EgoItemsGrammar.ALLOC_TO, 0); }
		public TerminalNode ALLOC_INT() { return getToken(EgoItemsGrammar.ALLOC_INT, 0); }
		public List<TerminalNode> ALLOC_UINT() { return getTokens(EgoItemsGrammar.ALLOC_UINT); }
		public TerminalNode ALLOC_UINT(int i) {
			return getToken(EgoItemsGrammar.ALLOC_UINT, i);
		}
		public AllocContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alloc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).enterAlloc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).exitAlloc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EgoItemsGrammarVisitor ) return ((EgoItemsGrammarVisitor<? extends T>)visitor).visitAlloc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AllocContext alloc() throws RecognitionException {
		AllocContext _localctx = new AllocContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_alloc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			match(ALLOC);
			setState(49);
			((AllocContext)_localctx).c = match(ALLOC_INT);
			setState(50);
			match(ALLOC_COLON);
			setState(51);
			((AllocContext)_localctx).l = match(ALLOC_UINT);
			setState(52);
			match(ALLOC_TO);
			setState(53);
			((AllocContext)_localctx).u = match(ALLOC_UINT);

			                ((AllocContext)_localctx).common =  ((AllocContext)_localctx).c.getText();
			                ((AllocContext)_localctx).lower =  ((AllocContext)_localctx).l.getText();
			                ((AllocContext)_localctx).upper =  ((AllocContext)_localctx).u.getText();
			            
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
		public TerminalNode COMBAT() { return getToken(EgoItemsGrammar.COMBAT, 0); }
		public List<TerminalNode> COMBAT_COLON() { return getTokens(EgoItemsGrammar.COMBAT_COLON); }
		public TerminalNode COMBAT_COLON(int i) {
			return getToken(EgoItemsGrammar.COMBAT_COLON, i);
		}
		public List<TerminalNode> COMBAT_DICE() { return getTokens(EgoItemsGrammar.COMBAT_DICE); }
		public TerminalNode COMBAT_DICE(int i) {
			return getToken(EgoItemsGrammar.COMBAT_DICE, i);
		}
		public CombatContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_combat; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).enterCombat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).exitCombat(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EgoItemsGrammarVisitor ) return ((EgoItemsGrammarVisitor<? extends T>)visitor).visitCombat(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CombatContext combat() throws RecognitionException {
		CombatContext _localctx = new CombatContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_combat);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			match(COMBAT);
			setState(57);
			((CombatContext)_localctx).h = match(COMBAT_DICE);
			setState(58);
			match(COMBAT_COLON);
			setState(59);
			((CombatContext)_localctx).d = match(COMBAT_DICE);
			setState(60);
			match(COMBAT_COLON);
			setState(61);
			((CombatContext)_localctx).a = match(COMBAT_DICE);

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
	public static class MinCombatContext extends ParserRuleContext {
		public String minToh;
		public String minTod;
		public String minToa;
		public Token h;
		public Token d;
		public Token a;
		public TerminalNode MIN_COMBAT() { return getToken(EgoItemsGrammar.MIN_COMBAT, 0); }
		public List<TerminalNode> COLON() { return getTokens(EgoItemsGrammar.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(EgoItemsGrammar.COLON, i);
		}
		public List<TerminalNode> INTEGER() { return getTokens(EgoItemsGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(EgoItemsGrammar.INTEGER, i);
		}
		public MinCombatContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_minCombat; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).enterMinCombat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).exitMinCombat(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EgoItemsGrammarVisitor ) return ((EgoItemsGrammarVisitor<? extends T>)visitor).visitMinCombat(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MinCombatContext minCombat() throws RecognitionException {
		MinCombatContext _localctx = new MinCombatContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_minCombat);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64);
			match(MIN_COMBAT);
			setState(65);
			((MinCombatContext)_localctx).h = match(INTEGER);
			setState(66);
			match(COLON);
			setState(67);
			((MinCombatContext)_localctx).d = match(INTEGER);
			setState(68);
			match(COLON);
			setState(69);
			((MinCombatContext)_localctx).a = match(INTEGER);

			                ((MinCombatContext)_localctx).minToh =  ((MinCombatContext)_localctx).h.getText();
			                ((MinCombatContext)_localctx).minTod =  ((MinCombatContext)_localctx).d.getText();
			                ((MinCombatContext)_localctx).minToa =  ((MinCombatContext)_localctx).a.getText();
			            
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
		public String tVal;
		public Token STRING;
		public TerminalNode TYPE() { return getToken(EgoItemsGrammar.TYPE, 0); }
		public TerminalNode STRING() { return getToken(EgoItemsGrammar.STRING, 0); }
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).exitType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EgoItemsGrammarVisitor ) return ((EgoItemsGrammarVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			match(TYPE);
			setState(73);
			((TypeContext)_localctx).STRING = match(STRING);
			 ((TypeContext)_localctx).tVal =  ((TypeContext)_localctx).STRING.getText(); 
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
	public static class ItemContext extends ParserRuleContext {
		public String tVal;
		public String sVal;
		public Token t;
		public Token s;
		public TerminalNode ITEM() { return getToken(EgoItemsGrammar.ITEM, 0); }
		public TerminalNode ITEM_COLON() { return getToken(EgoItemsGrammar.ITEM_COLON, 0); }
		public List<TerminalNode> ITEM_STRING() { return getTokens(EgoItemsGrammar.ITEM_STRING); }
		public TerminalNode ITEM_STRING(int i) {
			return getToken(EgoItemsGrammar.ITEM_STRING, i);
		}
		public ItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_item; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).enterItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).exitItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EgoItemsGrammarVisitor ) return ((EgoItemsGrammarVisitor<? extends T>)visitor).visitItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ItemContext item() throws RecognitionException {
		ItemContext _localctx = new ItemContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_item);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(76);
			match(ITEM);
			setState(77);
			((ItemContext)_localctx).t = match(ITEM_STRING);
			setState(78);
			match(ITEM_COLON);
			setState(79);
			((ItemContext)_localctx).s = match(ITEM_STRING);

			                ((ItemContext)_localctx).tVal =  ((ItemContext)_localctx).t.getText();
			                ((ItemContext)_localctx).sVal =  ((ItemContext)_localctx).s.getText();
			            
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
		public TerminalNode FLAGS() { return getToken(EgoItemsGrammar.FLAGS, 0); }
		public List<TerminalNode> FLAG_OR() { return getTokens(EgoItemsGrammar.FLAG_OR); }
		public TerminalNode FLAG_OR(int i) {
			return getToken(EgoItemsGrammar.FLAG_OR, i);
		}
		public List<TerminalNode> FLAG() { return getTokens(EgoItemsGrammar.FLAG); }
		public TerminalNode FLAG(int i) {
			return getToken(EgoItemsGrammar.FLAG, i);
		}
		public FlagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flags; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).enterFlags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).exitFlags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EgoItemsGrammarVisitor ) return ((EgoItemsGrammarVisitor<? extends T>)visitor).visitFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsContext flags() throws RecognitionException {
		FlagsContext _localctx = new FlagsContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_flags);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			 ((FlagsContext)_localctx).flagList =  new ArrayList<>(); 
			setState(83);
			match(FLAGS);
			setState(91);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(85); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(84);
						match(FLAG_OR);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(87); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				{
				setState(89);
				((FlagsContext)_localctx).f1 = match(FLAG);

				               _localctx.flagList.add(((FlagsContext)_localctx).f1.getText().trim());
				            
				}
				break;
			}
			setState(100);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FLAG_OR) {
				{
				{
				setState(93);
				match(FLAG_OR);
				setState(96);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FLAG) {
					{
					setState(94);
					((FlagsContext)_localctx).f2 = match(FLAG);

					               _localctx.flagList.add(((FlagsContext)_localctx).f2.getText().trim());
					            
					}
				}

				}
				}
				setState(102);
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
	public static class FlagsOffContext extends ParserRuleContext {
		public List<String> flagList;
		public Token f1;
		public Token f2;
		public TerminalNode FLAGS_OFF() { return getToken(EgoItemsGrammar.FLAGS_OFF, 0); }
		public List<TerminalNode> FLAG_OR() { return getTokens(EgoItemsGrammar.FLAG_OR); }
		public TerminalNode FLAG_OR(int i) {
			return getToken(EgoItemsGrammar.FLAG_OR, i);
		}
		public List<TerminalNode> FLAG() { return getTokens(EgoItemsGrammar.FLAG); }
		public TerminalNode FLAG(int i) {
			return getToken(EgoItemsGrammar.FLAG, i);
		}
		public FlagsOffContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flagsOff; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).enterFlagsOff(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).exitFlagsOff(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EgoItemsGrammarVisitor ) return ((EgoItemsGrammarVisitor<? extends T>)visitor).visitFlagsOff(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsOffContext flagsOff() throws RecognitionException {
		FlagsOffContext _localctx = new FlagsOffContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_flagsOff);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			 ((FlagsOffContext)_localctx).flagList =  new ArrayList<>(); 
			setState(104);
			match(FLAGS_OFF);
			setState(112);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(106); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(105);
						match(FLAG_OR);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(108); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				{
				setState(110);
				((FlagsOffContext)_localctx).f1 = match(FLAG);

				               _localctx.flagList.add(((FlagsOffContext)_localctx).f1.getText().trim());
				            
				}
				break;
			}
			setState(121);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FLAG_OR) {
				{
				{
				setState(114);
				match(FLAG_OR);
				setState(117);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FLAG) {
					{
					setState(115);
					((FlagsOffContext)_localctx).f2 = match(FLAG);

					               _localctx.flagList.add(((FlagsOffContext)_localctx).f2.getText().trim());
					            
					}
				}

				}
				}
				setState(123);
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
		public Token l1;
		public Token v1;
		public Token l2;
		public Token v2;
		public TerminalNode VALUES() { return getToken(EgoItemsGrammar.VALUES, 0); }
		public List<TerminalNode> VALUE_FLAG() { return getTokens(EgoItemsGrammar.VALUE_FLAG); }
		public TerminalNode VALUE_FLAG(int i) {
			return getToken(EgoItemsGrammar.VALUE_FLAG, i);
		}
		public List<TerminalNode> VALUE_DICE() { return getTokens(EgoItemsGrammar.VALUE_DICE); }
		public TerminalNode VALUE_DICE(int i) {
			return getToken(EgoItemsGrammar.VALUE_DICE, i);
		}
		public List<TerminalNode> VALUE_INTEGER() { return getTokens(EgoItemsGrammar.VALUE_INTEGER); }
		public TerminalNode VALUE_INTEGER(int i) {
			return getToken(EgoItemsGrammar.VALUE_INTEGER, i);
		}
		public List<TerminalNode> VALUE_OR() { return getTokens(EgoItemsGrammar.VALUE_OR); }
		public TerminalNode VALUE_OR(int i) {
			return getToken(EgoItemsGrammar.VALUE_OR, i);
		}
		public ValuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_values; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).enterValues(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).exitValues(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EgoItemsGrammarVisitor ) return ((EgoItemsGrammarVisitor<? extends T>)visitor).visitValues(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValuesContext values() throws RecognitionException {
		ValuesContext _localctx = new ValuesContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_values);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			 ((ValuesContext)_localctx).valueMap =  new HashMap<>(); 
			setState(125);
			match(VALUES);
			setState(134);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(127); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(126);
						match(VALUE_OR);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(129); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				{
				setState(131);
				((ValuesContext)_localctx).l1 = match(VALUE_FLAG);
				setState(132);
				((ValuesContext)_localctx).v1 = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==VALUE_DICE || _la==VALUE_INTEGER) ) {
					((ValuesContext)_localctx).v1 = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}

				                _localctx.valueMap.put(((ValuesContext)_localctx).l1.getText(), ((ValuesContext)_localctx).v1.getText());
				            
				}
				break;
			}
			setState(148);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VALUE_OR) {
				{
				{
				setState(137); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(136);
						match(VALUE_OR);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(139); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(144);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VALUE_FLAG) {
					{
					setState(141);
					((ValuesContext)_localctx).l2 = match(VALUE_FLAG);
					setState(142);
					((ValuesContext)_localctx).v2 = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==VALUE_DICE || _la==VALUE_INTEGER) ) {
						((ValuesContext)_localctx).v2 = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}

					                _localctx.valueMap.put(((ValuesContext)_localctx).l2.getText(), ((ValuesContext)_localctx).v2.getText());
					            
					}
				}

				}
				}
				setState(150);
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
	public static class MinValuesContext extends ParserRuleContext {
		public Map<String, String> valueMap;
		public Token l1;
		public Token v1;
		public Token l2;
		public Token v2;
		public TerminalNode MIN_VALUES() { return getToken(EgoItemsGrammar.MIN_VALUES, 0); }
		public List<TerminalNode> VALUE_FLAG() { return getTokens(EgoItemsGrammar.VALUE_FLAG); }
		public TerminalNode VALUE_FLAG(int i) {
			return getToken(EgoItemsGrammar.VALUE_FLAG, i);
		}
		public List<TerminalNode> VALUE_DICE() { return getTokens(EgoItemsGrammar.VALUE_DICE); }
		public TerminalNode VALUE_DICE(int i) {
			return getToken(EgoItemsGrammar.VALUE_DICE, i);
		}
		public List<TerminalNode> VALUE_INTEGER() { return getTokens(EgoItemsGrammar.VALUE_INTEGER); }
		public TerminalNode VALUE_INTEGER(int i) {
			return getToken(EgoItemsGrammar.VALUE_INTEGER, i);
		}
		public List<TerminalNode> VALUE_OR() { return getTokens(EgoItemsGrammar.VALUE_OR); }
		public TerminalNode VALUE_OR(int i) {
			return getToken(EgoItemsGrammar.VALUE_OR, i);
		}
		public MinValuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_minValues; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).enterMinValues(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).exitMinValues(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EgoItemsGrammarVisitor ) return ((EgoItemsGrammarVisitor<? extends T>)visitor).visitMinValues(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MinValuesContext minValues() throws RecognitionException {
		MinValuesContext _localctx = new MinValuesContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_minValues);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			 ((MinValuesContext)_localctx).valueMap =  new HashMap<>(); 
			setState(152);
			match(MIN_VALUES);
			setState(161);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				setState(154); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(153);
						match(VALUE_OR);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(156); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				{
				setState(158);
				((MinValuesContext)_localctx).l1 = match(VALUE_FLAG);
				setState(159);
				((MinValuesContext)_localctx).v1 = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==VALUE_DICE || _la==VALUE_INTEGER) ) {
					((MinValuesContext)_localctx).v1 = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}

				                _localctx.valueMap.put(((MinValuesContext)_localctx).l1.getText(), ((MinValuesContext)_localctx).v1.getText());
				            
				}
				break;
			}
			setState(175);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VALUE_OR) {
				{
				{
				setState(164); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(163);
						match(VALUE_OR);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(166); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(171);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==VALUE_FLAG) {
					{
					setState(168);
					((MinValuesContext)_localctx).l2 = match(VALUE_FLAG);
					setState(169);
					((MinValuesContext)_localctx).v2 = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==VALUE_DICE || _la==VALUE_INTEGER) ) {
						((MinValuesContext)_localctx).v2 = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}

					                _localctx.valueMap.put(((MinValuesContext)_localctx).l2.getText(), ((MinValuesContext)_localctx).v2.getText());
					            
					}
				}

				}
				}
				setState(177);
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
	public static class ActContext extends ParserRuleContext {
		public String activation;
		public Token STRING;
		public TerminalNode ACT() { return getToken(EgoItemsGrammar.ACT, 0); }
		public TerminalNode STRING() { return getToken(EgoItemsGrammar.STRING, 0); }
		public ActContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_act; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).enterAct(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).exitAct(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EgoItemsGrammarVisitor ) return ((EgoItemsGrammarVisitor<? extends T>)visitor).visitAct(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActContext act() throws RecognitionException {
		ActContext _localctx = new ActContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_act);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(178);
			match(ACT);
			setState(179);
			((ActContext)_localctx).STRING = match(STRING);
			 ((ActContext)_localctx).activation =  ((ActContext)_localctx).STRING.getText(); 
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
		public Token t;
		public TerminalNode TIME() { return getToken(EgoItemsGrammar.TIME, 0); }
		public TerminalNode COMBAT_DICE() { return getToken(EgoItemsGrammar.COMBAT_DICE, 0); }
		public TimeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_time; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).enterTime(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).exitTime(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EgoItemsGrammarVisitor ) return ((EgoItemsGrammarVisitor<? extends T>)visitor).visitTime(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TimeContext time() throws RecognitionException {
		TimeContext _localctx = new TimeContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_time);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(182);
			match(TIME);
			setState(183);
			((TimeContext)_localctx).t = match(COMBAT_DICE);
			 ((TimeContext)_localctx).timeStr =  ((TimeContext)_localctx).t.getText(); 
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
		public String brandStr;
		public Token STRING;
		public TerminalNode BRAND() { return getToken(EgoItemsGrammar.BRAND, 0); }
		public TerminalNode STRING() { return getToken(EgoItemsGrammar.STRING, 0); }
		public BrandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_brand; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).enterBrand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).exitBrand(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EgoItemsGrammarVisitor ) return ((EgoItemsGrammarVisitor<? extends T>)visitor).visitBrand(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BrandContext brand() throws RecognitionException {
		BrandContext _localctx = new BrandContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_brand);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(186);
			match(BRAND);
			setState(187);
			((BrandContext)_localctx).STRING = match(STRING);
			 ((BrandContext)_localctx).brandStr =  ((BrandContext)_localctx).STRING.getText(); 
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
		public String slayStr;
		public Token STRING;
		public TerminalNode SLAY() { return getToken(EgoItemsGrammar.SLAY, 0); }
		public TerminalNode STRING() { return getToken(EgoItemsGrammar.STRING, 0); }
		public SlayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_slay; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).enterSlay(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).exitSlay(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EgoItemsGrammarVisitor ) return ((EgoItemsGrammarVisitor<? extends T>)visitor).visitSlay(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SlayContext slay() throws RecognitionException {
		SlayContext _localctx = new SlayContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_slay);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(190);
			match(SLAY);
			setState(191);
			((SlayContext)_localctx).STRING = match(STRING);
			 ((SlayContext)_localctx).slayStr =  ((SlayContext)_localctx).STRING.getText(); 
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
	public static class CurseContext extends ParserRuleContext {
		public FullCurseDataParseRecord curseData;
		public Token n;
		public Token p;
		public TerminalNode CURSE() { return getToken(EgoItemsGrammar.CURSE, 0); }
		public TerminalNode CURSE_COLON() { return getToken(EgoItemsGrammar.CURSE_COLON, 0); }
		public List<TerminalNode> CURSE_NAME() { return getTokens(EgoItemsGrammar.CURSE_NAME); }
		public TerminalNode CURSE_NAME(int i) {
			return getToken(EgoItemsGrammar.CURSE_NAME, i);
		}
		public CurseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_curse; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).enterCurse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).exitCurse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EgoItemsGrammarVisitor ) return ((EgoItemsGrammarVisitor<? extends T>)visitor).visitCurse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CurseContext curse() throws RecognitionException {
		CurseContext _localctx = new CurseContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_curse);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(194);
			match(CURSE);
			setState(195);
			((CurseContext)_localctx).n = match(CURSE_NAME);
			setState(196);
			match(CURSE_COLON);
			setState(197);
			((CurseContext)_localctx).p = match(CURSE_NAME);

			                String curseName = ((CurseContext)_localctx).n.getText();
			                String cursePower = ((CurseContext)_localctx).p.getText();
			                ((CurseContext)_localctx).curseData =  new FullCurseDataParseRecord(curseName, cursePower, "0");
			            
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
		public TerminalNode DESC() { return getToken(EgoItemsGrammar.DESC, 0); }
		public TerminalNode STRING() { return getToken(EgoItemsGrammar.STRING, 0); }
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_desc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EgoItemsGrammarVisitor ) return ((EgoItemsGrammarVisitor<? extends T>)visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_desc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(200);
			match(DESC);
			setState(201);
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
	public static class EgoItemContext extends ParserRuleContext {
		public EgoItemParseRecord record;
		public NameContext name;
		public InfoContext info;
		public AllocContext alloc;
		public CombatContext combat;
		public MinCombatContext minCombat;
		public TypeContext type;
		public ItemContext item;
		public FlagsContext flags;
		public FlagsOffContext flagsOff;
		public ValuesContext values;
		public MinValuesContext minValues;
		public ActContext act;
		public TimeContext time;
		public BrandContext brand;
		public SlayContext slay;
		public CurseContext curse;
		public DescContext desc;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public List<InfoContext> info() {
			return getRuleContexts(InfoContext.class);
		}
		public InfoContext info(int i) {
			return getRuleContext(InfoContext.class,i);
		}
		public List<AllocContext> alloc() {
			return getRuleContexts(AllocContext.class);
		}
		public AllocContext alloc(int i) {
			return getRuleContext(AllocContext.class,i);
		}
		public List<CombatContext> combat() {
			return getRuleContexts(CombatContext.class);
		}
		public CombatContext combat(int i) {
			return getRuleContext(CombatContext.class,i);
		}
		public List<MinCombatContext> minCombat() {
			return getRuleContexts(MinCombatContext.class);
		}
		public MinCombatContext minCombat(int i) {
			return getRuleContext(MinCombatContext.class,i);
		}
		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}
		public TypeContext type(int i) {
			return getRuleContext(TypeContext.class,i);
		}
		public List<ItemContext> item() {
			return getRuleContexts(ItemContext.class);
		}
		public ItemContext item(int i) {
			return getRuleContext(ItemContext.class,i);
		}
		public List<FlagsContext> flags() {
			return getRuleContexts(FlagsContext.class);
		}
		public FlagsContext flags(int i) {
			return getRuleContext(FlagsContext.class,i);
		}
		public List<FlagsOffContext> flagsOff() {
			return getRuleContexts(FlagsOffContext.class);
		}
		public FlagsOffContext flagsOff(int i) {
			return getRuleContext(FlagsOffContext.class,i);
		}
		public List<ValuesContext> values() {
			return getRuleContexts(ValuesContext.class);
		}
		public ValuesContext values(int i) {
			return getRuleContext(ValuesContext.class,i);
		}
		public List<MinValuesContext> minValues() {
			return getRuleContexts(MinValuesContext.class);
		}
		public MinValuesContext minValues(int i) {
			return getRuleContext(MinValuesContext.class,i);
		}
		public List<ActContext> act() {
			return getRuleContexts(ActContext.class);
		}
		public ActContext act(int i) {
			return getRuleContext(ActContext.class,i);
		}
		public List<TimeContext> time() {
			return getRuleContexts(TimeContext.class);
		}
		public TimeContext time(int i) {
			return getRuleContext(TimeContext.class,i);
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
		public List<CurseContext> curse() {
			return getRuleContexts(CurseContext.class);
		}
		public CurseContext curse(int i) {
			return getRuleContext(CurseContext.class,i);
		}
		public List<DescContext> desc() {
			return getRuleContexts(DescContext.class);
		}
		public DescContext desc(int i) {
			return getRuleContext(DescContext.class,i);
		}
		public EgoItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_egoItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).enterEgoItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).exitEgoItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EgoItemsGrammarVisitor ) return ((EgoItemsGrammarVisitor<? extends T>)visitor).visitEgoItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EgoItemContext egoItem() throws RecognitionException {
		EgoItemContext _localctx = new EgoItemContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_egoItem);

		            String nameInit = "";
		            String costInit = "";
		            String ratingInit = "";
		            String commonInit = "";
		            String lowerInit = "";
		            String upperInit = "";
		            String tohInit = "";
		            String todInit = "";
		            String toaInit = "";
		            String minTohInit = "";
		            String minTodInit = "";
		            String minToaInit = "";
		            List<EgoItemParseRecord.ItemRef> itemRefsInit = new ArrayList<>();
		            List<String> flagsInit = new ArrayList<>();
		            List<String> flagsOffInit = new ArrayList<>();
		            Map<String, String> valuesInit = new HashMap<>();
		            Map<String, String> minValuesInit = new HashMap<>();
		            String actInit = "";
		            String timeoutInit = "";
		            List<String> brandsInit = new ArrayList<>();
		            List<String> slaysInit = new ArrayList<>();
		            List<FullCurseDataParseRecord> cursesInit = new ArrayList<>();
		            String descInit = "";
		            StringBuilder sb = new StringBuilder();
		            int line = 0;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(204);
			((EgoItemContext)_localctx).name = name();
			 line = ((EgoItemContext)_localctx).name.lineNo;
			                   nameInit = ((EgoItemContext)_localctx).name.nameStr; 
			setState(256);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 262140L) != 0)) {
				{
				setState(254);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case INFO:
					{
					setState(206);
					((EgoItemContext)_localctx).info = info();
					 costInit = ((EgoItemContext)_localctx).info.cost;
					                   ratingInit = ((EgoItemContext)_localctx).info.rating; 
					}
					break;
				case ALLOC:
					{
					setState(209);
					((EgoItemContext)_localctx).alloc = alloc();
					 commonInit = ((EgoItemContext)_localctx).alloc.common;
					                    lowerInit = ((EgoItemContext)_localctx).alloc.lower;
					                    upperInit = ((EgoItemContext)_localctx).alloc.upper; 
					}
					break;
				case COMBAT:
					{
					setState(212);
					((EgoItemContext)_localctx).combat = combat();
					 tohInit = ((EgoItemContext)_localctx).combat.toh;
					                     todInit = ((EgoItemContext)_localctx).combat.tod;
					                     toaInit = ((EgoItemContext)_localctx).combat.toa; 
					}
					break;
				case MIN_COMBAT:
					{
					setState(215);
					((EgoItemContext)_localctx).minCombat = minCombat();
					 minTohInit = ((EgoItemContext)_localctx).minCombat.minToh;
					                        minTodInit = ((EgoItemContext)_localctx).minCombat.minTod;
					                        minToaInit = ((EgoItemContext)_localctx).minCombat.minToa; 
					}
					break;
				case TYPE:
					{
					setState(218);
					((EgoItemContext)_localctx).type = type();
					 itemRefsInit.addFirst(new ItemRef(((EgoItemContext)_localctx).type.tVal, "")); 
					}
					break;
				case ITEM:
					{
					setState(221);
					((EgoItemContext)_localctx).item = item();
					 itemRefsInit.addFirst(new ItemRef(((EgoItemContext)_localctx).item.tVal, ((EgoItemContext)_localctx).item.sVal)); 
					}
					break;
				case FLAGS:
					{
					setState(224);
					((EgoItemContext)_localctx).flags = flags();
					 flagsInit.addAll(((EgoItemContext)_localctx).flags.flagList); 
					}
					break;
				case FLAGS_OFF:
					{
					setState(227);
					((EgoItemContext)_localctx).flagsOff = flagsOff();
					 flagsOffInit.addAll(((EgoItemContext)_localctx).flagsOff.flagList); 
					}
					break;
				case VALUES:
					{
					setState(230);
					((EgoItemContext)_localctx).values = values();
					 valuesInit.putAll(((EgoItemContext)_localctx).values.valueMap); 
					}
					break;
				case MIN_VALUES:
					{
					setState(233);
					((EgoItemContext)_localctx).minValues = minValues();
					 minValuesInit.putAll(((EgoItemContext)_localctx).minValues.valueMap); 
					}
					break;
				case ACT:
					{
					setState(236);
					((EgoItemContext)_localctx).act = act();
					 actInit = ((EgoItemContext)_localctx).act.activation; 
					}
					break;
				case TIME:
					{
					setState(239);
					((EgoItemContext)_localctx).time = time();
					 timeoutInit = ((EgoItemContext)_localctx).time.timeStr; 
					}
					break;
				case BRAND:
					{
					setState(242);
					((EgoItemContext)_localctx).brand = brand();
					 brandsInit.add(((EgoItemContext)_localctx).brand.brandStr); 
					}
					break;
				case SLAY:
					{
					setState(245);
					((EgoItemContext)_localctx).slay = slay();
					 slaysInit.add(((EgoItemContext)_localctx).slay.slayStr); 
					}
					break;
				case CURSE:
					{
					setState(248);
					((EgoItemContext)_localctx).curse = curse();
					 cursesInit.add(((EgoItemContext)_localctx).curse.curseData); 
					}
					break;
				case DESC:
					{
					setState(251);
					((EgoItemContext)_localctx).desc = desc();
					 sb.append(((EgoItemContext)_localctx).desc.descStr); 
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(258);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			_ctx.stop = _input.LT(-1);

			            descInit = sb.toString();
			            ((EgoItemContext)_localctx).record =  new EgoItemParseRecord(nameInit,
			            costInit, ratingInit, commonInit, lowerInit, upperInit,
			            tohInit, todInit, toaInit, minTohInit, minTodInit, minToaInit,
			            itemRefsInit, flagsInit, flagsOffInit,
			            valuesInit, minValuesInit, actInit, timeoutInit,
			            brandsInit, slaysInit, cursesInit, descInit, line);
			        
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
		public List<EgoItemParseRecord> items;
		public EgoItemContext egoItem;
		public TerminalNode EOF() { return getToken(EgoItemsGrammar.EOF, 0); }
		public List<EgoItemContext> egoItem() {
			return getRuleContexts(EgoItemContext.class);
		}
		public EgoItemContext egoItem(int i) {
			return getRuleContext(EgoItemContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EgoItemsGrammarListener ) ((EgoItemsGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EgoItemsGrammarVisitor ) return ((EgoItemsGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_file);

		            ((FileContext)_localctx).items =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(264);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NAME) {
				{
				{
				setState(259);
				((FileContext)_localctx).egoItem = egoItem();
				 _localctx.items.add(((FileContext)_localctx).egoItem.record); 
				}
				}
				setState(266);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(267);
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
		"\u0004\u00012\u010e\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0004\u0007"+
		"V\b\u0007\u000b\u0007\f\u0007W\u0001\u0007\u0001\u0007\u0003\u0007\\\b"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007a\b\u0007\u0005"+
		"\u0007c\b\u0007\n\u0007\f\u0007f\t\u0007\u0001\b\u0001\b\u0001\b\u0004"+
		"\bk\b\b\u000b\b\f\bl\u0001\b\u0001\b\u0003\bq\b\b\u0001\b\u0001\b\u0001"+
		"\b\u0003\bv\b\b\u0005\bx\b\b\n\b\f\b{\t\b\u0001\t\u0001\t\u0001\t\u0004"+
		"\t\u0080\b\t\u000b\t\f\t\u0081\u0001\t\u0001\t\u0001\t\u0003\t\u0087\b"+
		"\t\u0001\t\u0004\t\u008a\b\t\u000b\t\f\t\u008b\u0001\t\u0001\t\u0001\t"+
		"\u0003\t\u0091\b\t\u0005\t\u0093\b\t\n\t\f\t\u0096\t\t\u0001\n\u0001\n"+
		"\u0001\n\u0004\n\u009b\b\n\u000b\n\f\n\u009c\u0001\n\u0001\n\u0001\n\u0003"+
		"\n\u00a2\b\n\u0001\n\u0004\n\u00a5\b\n\u000b\n\f\n\u00a6\u0001\n\u0001"+
		"\n\u0001\n\u0003\n\u00ac\b\n\u0005\n\u00ae\b\n\n\n\f\n\u00b1\t\n\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0005\u0011\u00ff\b\u0011\n\u0011\f\u0011\u0102\t\u0011\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0005\u0012\u0107\b\u0012\n\u0012\f\u0012"+
		"\u010a\t\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0000\u0000\u0013\u0000"+
		"\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c"+
		"\u001e \"$\u0000\u0001\u0001\u0000&\'\u0121\u0000&\u0001\u0000\u0000\u0000"+
		"\u0002*\u0001\u0000\u0000\u0000\u00040\u0001\u0000\u0000\u0000\u00068"+
		"\u0001\u0000\u0000\u0000\b@\u0001\u0000\u0000\u0000\nH\u0001\u0000\u0000"+
		"\u0000\fL\u0001\u0000\u0000\u0000\u000eR\u0001\u0000\u0000\u0000\u0010"+
		"g\u0001\u0000\u0000\u0000\u0012|\u0001\u0000\u0000\u0000\u0014\u0097\u0001"+
		"\u0000\u0000\u0000\u0016\u00b2\u0001\u0000\u0000\u0000\u0018\u00b6\u0001"+
		"\u0000\u0000\u0000\u001a\u00ba\u0001\u0000\u0000\u0000\u001c\u00be\u0001"+
		"\u0000\u0000\u0000\u001e\u00c2\u0001\u0000\u0000\u0000 \u00c8\u0001\u0000"+
		"\u0000\u0000\"\u00cc\u0001\u0000\u0000\u0000$\u0108\u0001\u0000\u0000"+
		"\u0000&\'\u0005\u0001\u0000\u0000\'(\u0005.\u0000\u0000()\u0006\u0000"+
		"\uffff\uffff\u0000)\u0001\u0001\u0000\u0000\u0000*+\u0005\u0002\u0000"+
		"\u0000+,\u0005\u0012\u0000\u0000,-\u0005\u0013\u0000\u0000-.\u0005\u0012"+
		"\u0000\u0000./\u0006\u0001\uffff\uffff\u0000/\u0003\u0001\u0000\u0000"+
		"\u000001\u0005\u0003\u0000\u000012\u0005#\u0000\u000023\u0005 \u0000\u0000"+
		"34\u0005\u001f\u0000\u000045\u0005!\u0000\u000056\u0005\u001f\u0000\u0000"+
		"67\u0006\u0002\uffff\uffff\u00007\u0005\u0001\u0000\u0000\u000089\u0005"+
		"\u0004\u0000\u00009:\u0005\u001c\u0000\u0000:;\u0005\u001d\u0000\u0000"+
		";<\u0005\u001c\u0000\u0000<=\u0005\u001d\u0000\u0000=>\u0005\u001c\u0000"+
		"\u0000>?\u0006\u0003\uffff\uffff\u0000?\u0007\u0001\u0000\u0000\u0000"+
		"@A\u0005\u0005\u0000\u0000AB\u0005\u0012\u0000\u0000BC\u0005\u0013\u0000"+
		"\u0000CD\u0005\u0012\u0000\u0000DE\u0005\u0013\u0000\u0000EF\u0005\u0012"+
		"\u0000\u0000FG\u0006\u0004\uffff\uffff\u0000G\t\u0001\u0000\u0000\u0000"+
		"HI\u0005\u0006\u0000\u0000IJ\u0005.\u0000\u0000JK\u0006\u0005\uffff\uffff"+
		"\u0000K\u000b\u0001\u0000\u0000\u0000LM\u0005\u0007\u0000\u0000MN\u0005"+
		"0\u0000\u0000NO\u00051\u0000\u0000OP\u00050\u0000\u0000PQ\u0006\u0006"+
		"\uffff\uffff\u0000Q\r\u0001\u0000\u0000\u0000RS\u0006\u0007\uffff\uffff"+
		"\u0000S[\u0005\b\u0000\u0000TV\u0005,\u0000\u0000UT\u0001\u0000\u0000"+
		"\u0000VW\u0001\u0000\u0000\u0000WU\u0001\u0000\u0000\u0000WX\u0001\u0000"+
		"\u0000\u0000X\\\u0001\u0000\u0000\u0000YZ\u0005+\u0000\u0000Z\\\u0006"+
		"\u0007\uffff\uffff\u0000[U\u0001\u0000\u0000\u0000[Y\u0001\u0000\u0000"+
		"\u0000[\\\u0001\u0000\u0000\u0000\\d\u0001\u0000\u0000\u0000]`\u0005,"+
		"\u0000\u0000^_\u0005+\u0000\u0000_a\u0006\u0007\uffff\uffff\u0000`^\u0001"+
		"\u0000\u0000\u0000`a\u0001\u0000\u0000\u0000ac\u0001\u0000\u0000\u0000"+
		"b]\u0001\u0000\u0000\u0000cf\u0001\u0000\u0000\u0000db\u0001\u0000\u0000"+
		"\u0000de\u0001\u0000\u0000\u0000e\u000f\u0001\u0000\u0000\u0000fd\u0001"+
		"\u0000\u0000\u0000gh\u0006\b\uffff\uffff\u0000hp\u0005\t\u0000\u0000i"+
		"k\u0005,\u0000\u0000ji\u0001\u0000\u0000\u0000kl\u0001\u0000\u0000\u0000"+
		"lj\u0001\u0000\u0000\u0000lm\u0001\u0000\u0000\u0000mq\u0001\u0000\u0000"+
		"\u0000no\u0005+\u0000\u0000oq\u0006\b\uffff\uffff\u0000pj\u0001\u0000"+
		"\u0000\u0000pn\u0001\u0000\u0000\u0000pq\u0001\u0000\u0000\u0000qy\u0001"+
		"\u0000\u0000\u0000ru\u0005,\u0000\u0000st\u0005+\u0000\u0000tv\u0006\b"+
		"\uffff\uffff\u0000us\u0001\u0000\u0000\u0000uv\u0001\u0000\u0000\u0000"+
		"vx\u0001\u0000\u0000\u0000wr\u0001\u0000\u0000\u0000x{\u0001\u0000\u0000"+
		"\u0000yw\u0001\u0000\u0000\u0000yz\u0001\u0000\u0000\u0000z\u0011\u0001"+
		"\u0000\u0000\u0000{y\u0001\u0000\u0000\u0000|}\u0006\t\uffff\uffff\u0000"+
		"}\u0086\u0005\n\u0000\u0000~\u0080\u0005)\u0000\u0000\u007f~\u0001\u0000"+
		"\u0000\u0000\u0080\u0081\u0001\u0000\u0000\u0000\u0081\u007f\u0001\u0000"+
		"\u0000\u0000\u0081\u0082\u0001\u0000\u0000\u0000\u0082\u0087\u0001\u0000"+
		"\u0000\u0000\u0083\u0084\u0005$\u0000\u0000\u0084\u0085\u0007\u0000\u0000"+
		"\u0000\u0085\u0087\u0006\t\uffff\uffff\u0000\u0086\u007f\u0001\u0000\u0000"+
		"\u0000\u0086\u0083\u0001\u0000\u0000\u0000\u0086\u0087\u0001\u0000\u0000"+
		"\u0000\u0087\u0094\u0001\u0000\u0000\u0000\u0088\u008a\u0005)\u0000\u0000"+
		"\u0089\u0088\u0001\u0000\u0000\u0000\u008a\u008b\u0001\u0000\u0000\u0000"+
		"\u008b\u0089\u0001\u0000\u0000\u0000\u008b\u008c\u0001\u0000\u0000\u0000"+
		"\u008c\u0090\u0001\u0000\u0000\u0000\u008d\u008e\u0005$\u0000\u0000\u008e"+
		"\u008f\u0007\u0000\u0000\u0000\u008f\u0091\u0006\t\uffff\uffff\u0000\u0090"+
		"\u008d\u0001\u0000\u0000\u0000\u0090\u0091\u0001\u0000\u0000\u0000\u0091"+
		"\u0093\u0001\u0000\u0000\u0000\u0092\u0089\u0001\u0000\u0000\u0000\u0093"+
		"\u0096\u0001\u0000\u0000\u0000\u0094\u0092\u0001\u0000\u0000\u0000\u0094"+
		"\u0095\u0001\u0000\u0000\u0000\u0095\u0013\u0001\u0000\u0000\u0000\u0096"+
		"\u0094\u0001\u0000\u0000\u0000\u0097\u0098\u0006\n\uffff\uffff\u0000\u0098"+
		"\u00a1\u0005\u000b\u0000\u0000\u0099\u009b\u0005)\u0000\u0000\u009a\u0099"+
		"\u0001\u0000\u0000\u0000\u009b\u009c\u0001\u0000\u0000\u0000\u009c\u009a"+
		"\u0001\u0000\u0000\u0000\u009c\u009d\u0001\u0000\u0000\u0000\u009d\u00a2"+
		"\u0001\u0000\u0000\u0000\u009e\u009f\u0005$\u0000\u0000\u009f\u00a0\u0007"+
		"\u0000\u0000\u0000\u00a0\u00a2\u0006\n\uffff\uffff\u0000\u00a1\u009a\u0001"+
		"\u0000\u0000\u0000\u00a1\u009e\u0001\u0000\u0000\u0000\u00a1\u00a2\u0001"+
		"\u0000\u0000\u0000\u00a2\u00af\u0001\u0000\u0000\u0000\u00a3\u00a5\u0005"+
		")\u0000\u0000\u00a4\u00a3\u0001\u0000\u0000\u0000\u00a5\u00a6\u0001\u0000"+
		"\u0000\u0000\u00a6\u00a4\u0001\u0000\u0000\u0000\u00a6\u00a7\u0001\u0000"+
		"\u0000\u0000\u00a7\u00ab\u0001\u0000\u0000\u0000\u00a8\u00a9\u0005$\u0000"+
		"\u0000\u00a9\u00aa\u0007\u0000\u0000\u0000\u00aa\u00ac\u0006\n\uffff\uffff"+
		"\u0000\u00ab\u00a8\u0001\u0000\u0000\u0000\u00ab\u00ac\u0001\u0000\u0000"+
		"\u0000\u00ac\u00ae\u0001\u0000\u0000\u0000\u00ad\u00a4\u0001\u0000\u0000"+
		"\u0000\u00ae\u00b1\u0001\u0000\u0000\u0000\u00af\u00ad\u0001\u0000\u0000"+
		"\u0000\u00af\u00b0\u0001\u0000\u0000\u0000\u00b0\u0015\u0001\u0000\u0000"+
		"\u0000\u00b1\u00af\u0001\u0000\u0000\u0000\u00b2\u00b3\u0005\f\u0000\u0000"+
		"\u00b3\u00b4\u0005.\u0000\u0000\u00b4\u00b5\u0006\u000b\uffff\uffff\u0000"+
		"\u00b5\u0017\u0001\u0000\u0000\u0000\u00b6\u00b7\u0005\r\u0000\u0000\u00b7"+
		"\u00b8\u0005\u001c\u0000\u0000\u00b8\u00b9\u0006\f\uffff\uffff\u0000\u00b9"+
		"\u0019\u0001\u0000\u0000\u0000\u00ba\u00bb\u0005\u000e\u0000\u0000\u00bb"+
		"\u00bc\u0005.\u0000\u0000\u00bc\u00bd\u0006\r\uffff\uffff\u0000\u00bd"+
		"\u001b\u0001\u0000\u0000\u0000\u00be\u00bf\u0005\u000f\u0000\u0000\u00bf"+
		"\u00c0\u0005.\u0000\u0000\u00c0\u00c1\u0006\u000e\uffff\uffff\u0000\u00c1"+
		"\u001d\u0001\u0000\u0000\u0000\u00c2\u00c3\u0005\u0010\u0000\u0000\u00c3"+
		"\u00c4\u0005\u0019\u0000\u0000\u00c4\u00c5\u0005\u001a\u0000\u0000\u00c5"+
		"\u00c6\u0005\u0019\u0000\u0000\u00c6\u00c7\u0006\u000f\uffff\uffff\u0000"+
		"\u00c7\u001f\u0001\u0000\u0000\u0000\u00c8\u00c9\u0005\u0011\u0000\u0000"+
		"\u00c9\u00ca\u0005.\u0000\u0000\u00ca\u00cb\u0006\u0010\uffff\uffff\u0000"+
		"\u00cb!\u0001\u0000\u0000\u0000\u00cc\u00cd\u0003\u0000\u0000\u0000\u00cd"+
		"\u0100\u0006\u0011\uffff\uffff\u0000\u00ce\u00cf\u0003\u0002\u0001\u0000"+
		"\u00cf\u00d0\u0006\u0011\uffff\uffff\u0000\u00d0\u00ff\u0001\u0000\u0000"+
		"\u0000\u00d1\u00d2\u0003\u0004\u0002\u0000\u00d2\u00d3\u0006\u0011\uffff"+
		"\uffff\u0000\u00d3\u00ff\u0001\u0000\u0000\u0000\u00d4\u00d5\u0003\u0006"+
		"\u0003\u0000\u00d5\u00d6\u0006\u0011\uffff\uffff\u0000\u00d6\u00ff\u0001"+
		"\u0000\u0000\u0000\u00d7\u00d8\u0003\b\u0004\u0000\u00d8\u00d9\u0006\u0011"+
		"\uffff\uffff\u0000\u00d9\u00ff\u0001\u0000\u0000\u0000\u00da\u00db\u0003"+
		"\n\u0005\u0000\u00db\u00dc\u0006\u0011\uffff\uffff\u0000\u00dc\u00ff\u0001"+
		"\u0000\u0000\u0000\u00dd\u00de\u0003\f\u0006\u0000\u00de\u00df\u0006\u0011"+
		"\uffff\uffff\u0000\u00df\u00ff\u0001\u0000\u0000\u0000\u00e0\u00e1\u0003"+
		"\u000e\u0007\u0000\u00e1\u00e2\u0006\u0011\uffff\uffff\u0000\u00e2\u00ff"+
		"\u0001\u0000\u0000\u0000\u00e3\u00e4\u0003\u0010\b\u0000\u00e4\u00e5\u0006"+
		"\u0011\uffff\uffff\u0000\u00e5\u00ff\u0001\u0000\u0000\u0000\u00e6\u00e7"+
		"\u0003\u0012\t\u0000\u00e7\u00e8\u0006\u0011\uffff\uffff\u0000\u00e8\u00ff"+
		"\u0001\u0000\u0000\u0000\u00e9\u00ea\u0003\u0014\n\u0000\u00ea\u00eb\u0006"+
		"\u0011\uffff\uffff\u0000\u00eb\u00ff\u0001\u0000\u0000\u0000\u00ec\u00ed"+
		"\u0003\u0016\u000b\u0000\u00ed\u00ee\u0006\u0011\uffff\uffff\u0000\u00ee"+
		"\u00ff\u0001\u0000\u0000\u0000\u00ef\u00f0\u0003\u0018\f\u0000\u00f0\u00f1"+
		"\u0006\u0011\uffff\uffff\u0000\u00f1\u00ff\u0001\u0000\u0000\u0000\u00f2"+
		"\u00f3\u0003\u001a\r\u0000\u00f3\u00f4\u0006\u0011\uffff\uffff\u0000\u00f4"+
		"\u00ff\u0001\u0000\u0000\u0000\u00f5\u00f6\u0003\u001c\u000e\u0000\u00f6"+
		"\u00f7\u0006\u0011\uffff\uffff\u0000\u00f7\u00ff\u0001\u0000\u0000\u0000"+
		"\u00f8\u00f9\u0003\u001e\u000f\u0000\u00f9\u00fa\u0006\u0011\uffff\uffff"+
		"\u0000\u00fa\u00ff\u0001\u0000\u0000\u0000\u00fb\u00fc\u0003 \u0010\u0000"+
		"\u00fc\u00fd\u0006\u0011\uffff\uffff\u0000\u00fd\u00ff\u0001\u0000\u0000"+
		"\u0000\u00fe\u00ce\u0001\u0000\u0000\u0000\u00fe\u00d1\u0001\u0000\u0000"+
		"\u0000\u00fe\u00d4\u0001\u0000\u0000\u0000\u00fe\u00d7\u0001\u0000\u0000"+
		"\u0000\u00fe\u00da\u0001\u0000\u0000\u0000\u00fe\u00dd\u0001\u0000\u0000"+
		"\u0000\u00fe\u00e0\u0001\u0000\u0000\u0000\u00fe\u00e3\u0001\u0000\u0000"+
		"\u0000\u00fe\u00e6\u0001\u0000\u0000\u0000\u00fe\u00e9\u0001\u0000\u0000"+
		"\u0000\u00fe\u00ec\u0001\u0000\u0000\u0000\u00fe\u00ef\u0001\u0000\u0000"+
		"\u0000\u00fe\u00f2\u0001\u0000\u0000\u0000\u00fe\u00f5\u0001\u0000\u0000"+
		"\u0000\u00fe\u00f8\u0001\u0000\u0000\u0000\u00fe\u00fb\u0001\u0000\u0000"+
		"\u0000\u00ff\u0102\u0001\u0000\u0000\u0000\u0100\u00fe\u0001\u0000\u0000"+
		"\u0000\u0100\u0101\u0001\u0000\u0000\u0000\u0101#\u0001\u0000\u0000\u0000"+
		"\u0102\u0100\u0001\u0000\u0000\u0000\u0103\u0104\u0003\"\u0011\u0000\u0104"+
		"\u0105\u0006\u0012\uffff\uffff\u0000\u0105\u0107\u0001\u0000\u0000\u0000"+
		"\u0106\u0103\u0001\u0000\u0000\u0000\u0107\u010a\u0001\u0000\u0000\u0000"+
		"\u0108\u0106\u0001\u0000\u0000\u0000\u0108\u0109\u0001\u0000\u0000\u0000"+
		"\u0109\u010b\u0001\u0000\u0000\u0000\u010a\u0108\u0001\u0000\u0000\u0000"+
		"\u010b\u010c\u0005\u0000\u0000\u0001\u010c%\u0001\u0000\u0000\u0000\u0015"+
		"W[`dlpuy\u0081\u0086\u008b\u0090\u0094\u009c\u00a1\u00a6\u00ab\u00af\u00fe"+
		"\u0100\u0108";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}