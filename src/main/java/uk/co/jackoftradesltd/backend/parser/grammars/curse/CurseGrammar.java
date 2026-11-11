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

// Generated from CurseGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.curse;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.backend.parser.curse.CurseParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.EffectParseRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class CurseGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, TYPE=2, WEIGHT=3, COMBAT=4, FLAGS=5, VALUES=6, CURSE_MSG=7, DESC=8, 
		CONFLICT=9, CONFLICT_FLAGS=10, COMMENT=11, INDENT=12, EOL=13, EFFECT=14, 
		EFFECT_MESSAGE=15, DICE=16, TIME=17, EFFECT_YX=18, EXPR=19, COLON=20, 
		UCASE=21, INTEGER=22, SIMPLE_DICE_STRING=23, COMPLEX_DICE_STRING=24, LBRACKET=25, 
		RBRACKET=26, VALUE_FLAG=27, VALUE_OR=28, VALUE_EOL=29, VALUE_INT=30, FLAG=31, 
		FLAG_OR=32, FLAG_EOL=33, FREE_TEXT=34, DICE_SIMPLE_VALUE=35, DICE_COMPLEX_VALUE=36, 
		EXPR_CHAR=37, EXPR_COLON=38, EXPR_UCASE=39, EXPR_OP=40, EXPR_EOL=41;
	public static final int
		RULE_name = 0, RULE_curseType = 1, RULE_weight = 2, RULE_combat = 3, RULE_flags = 4, 
		RULE_values = 5, RULE_msg = 6, RULE_desc = 7, RULE_conflict = 8, RULE_conflictFlags = 9, 
		RULE_time = 10, RULE_curseRecord = 11, RULE_file = 12, RULE_effect = 13, 
		RULE_effectYX = 14, RULE_dice = 15, RULE_expr = 16, RULE_effectMsg = 17, 
		RULE_strayEffectLine = 18, RULE_effectBlock = 19;
	private static String[] makeRuleNames() {
		return new String[] {
			"name", "curseType", "weight", "combat", "flags", "values", "msg", "desc", 
			"conflict", "conflictFlags", "time", "curseRecord", "file", "effect", 
			"effectYX", "dice", "expr", "effectMsg", "strayEffectLine", "effectBlock"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'type:'", "'weight:'", "'combat:'", "'flags:'", "'values:'", 
			"'msg:'", "'desc:'", "'conflict:'", "'conflict-flags:'", null, null, 
			null, "'effect:'", "'effect-msg:'", "'dice:'", "'time:'", "'effect-yx:'", 
			"'expr:'", null, null, null, null, null, "'['", "']'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "TYPE", "WEIGHT", "COMBAT", "FLAGS", "VALUES", "CURSE_MSG", 
			"DESC", "CONFLICT", "CONFLICT_FLAGS", "COMMENT", "INDENT", "EOL", "EFFECT", 
			"EFFECT_MESSAGE", "DICE", "TIME", "EFFECT_YX", "EXPR", "COLON", "UCASE", 
			"INTEGER", "SIMPLE_DICE_STRING", "COMPLEX_DICE_STRING", "LBRACKET", "RBRACKET", 
			"VALUE_FLAG", "VALUE_OR", "VALUE_EOL", "VALUE_INT", "FLAG", "FLAG_OR", 
			"FLAG_EOL", "FREE_TEXT", "DICE_SIMPLE_VALUE", "DICE_COMPLEX_VALUE", "EXPR_CHAR", 
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
	public String getGrammarFileName() { return "CurseGrammar.g4"; }

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

	public CurseGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public int line;
		public Token FREE_TEXT;
		public TerminalNode NAME() { return getToken(CurseGrammar.NAME, 0); }
		public TerminalNode FREE_TEXT() { return getToken(CurseGrammar.FREE_TEXT, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CurseGrammarVisitor ) return ((CurseGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(40);
			match(NAME);
			setState(41);
			((NameContext)_localctx).FREE_TEXT = match(FREE_TEXT);
			 ((NameContext)_localctx).nameStr =  ((NameContext)_localctx).FREE_TEXT.getText();
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
	public static class CurseTypeContext extends ParserRuleContext {
		public String typeStr;
		public Token FREE_TEXT;
		public TerminalNode TYPE() { return getToken(CurseGrammar.TYPE, 0); }
		public TerminalNode FREE_TEXT() { return getToken(CurseGrammar.FREE_TEXT, 0); }
		public CurseTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_curseType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).enterCurseType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).exitCurseType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CurseGrammarVisitor ) return ((CurseGrammarVisitor<? extends T>)visitor).visitCurseType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CurseTypeContext curseType() throws RecognitionException {
		CurseTypeContext _localctx = new CurseTypeContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_curseType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			match(TYPE);
			setState(45);
			((CurseTypeContext)_localctx).FREE_TEXT = match(FREE_TEXT);
			 ((CurseTypeContext)_localctx).typeStr =  ((CurseTypeContext)_localctx).FREE_TEXT.getText(); 
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
	public static class WeightContext extends ParserRuleContext {
		public String weightAdj;
		public Token INTEGER;
		public TerminalNode WEIGHT() { return getToken(CurseGrammar.WEIGHT, 0); }
		public TerminalNode INTEGER() { return getToken(CurseGrammar.INTEGER, 0); }
		public WeightContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_weight; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).enterWeight(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).exitWeight(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CurseGrammarVisitor ) return ((CurseGrammarVisitor<? extends T>)visitor).visitWeight(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WeightContext weight() throws RecognitionException {
		WeightContext _localctx = new WeightContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_weight);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			match(WEIGHT);
			setState(49);
			((WeightContext)_localctx).INTEGER = match(INTEGER);
			 ((WeightContext)_localctx).weightAdj =  ((WeightContext)_localctx).INTEGER.getText(); 
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
		public String toa;
		public String tod;
		public Token h;
		public Token d;
		public Token a;
		public TerminalNode COMBAT() { return getToken(CurseGrammar.COMBAT, 0); }
		public List<TerminalNode> COLON() { return getTokens(CurseGrammar.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(CurseGrammar.COLON, i);
		}
		public List<TerminalNode> INTEGER() { return getTokens(CurseGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(CurseGrammar.INTEGER, i);
		}
		public CombatContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_combat; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).enterCombat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).exitCombat(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CurseGrammarVisitor ) return ((CurseGrammarVisitor<? extends T>)visitor).visitCombat(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CombatContext combat() throws RecognitionException {
		CombatContext _localctx = new CombatContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_combat);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			match(COMBAT);
			setState(53);
			((CombatContext)_localctx).h = match(INTEGER);
			setState(54);
			match(COLON);
			setState(55);
			((CombatContext)_localctx).d = match(INTEGER);
			setState(56);
			match(COLON);
			setState(57);
			((CombatContext)_localctx).a = match(INTEGER);

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
	public static class FlagsContext extends ParserRuleContext {
		public List<String> flagList;
		public Token f1;
		public Token f2;
		public TerminalNode FLAGS() { return getToken(CurseGrammar.FLAGS, 0); }
		public List<TerminalNode> FLAG() { return getTokens(CurseGrammar.FLAG); }
		public TerminalNode FLAG(int i) {
			return getToken(CurseGrammar.FLAG, i);
		}
		public List<TerminalNode> FLAG_OR() { return getTokens(CurseGrammar.FLAG_OR); }
		public TerminalNode FLAG_OR(int i) {
			return getToken(CurseGrammar.FLAG_OR, i);
		}
		public FlagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flags; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).enterFlags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).exitFlags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CurseGrammarVisitor ) return ((CurseGrammarVisitor<? extends T>)visitor).visitFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsContext flags() throws RecognitionException {
		FlagsContext _localctx = new FlagsContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_flags);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			 ((FlagsContext)_localctx).flagList =  new ArrayList<>(); 
			setState(61);
			match(FLAGS);
			setState(62);
			((FlagsContext)_localctx).f1 = match(FLAG);
			 _localctx.flagList.add(((FlagsContext)_localctx).f1.getText()); 
			setState(69);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FLAG_OR) {
				{
				{
				setState(64);
				match(FLAG_OR);
				setState(65);
				((FlagsContext)_localctx).f2 = match(FLAG);
				 _localctx.flagList.add(((FlagsContext)_localctx).f2.getText()); 
				}
				}
				setState(71);
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
		public Token vf1;
		public Token vi1;
		public Token vf2;
		public Token vi2;
		public TerminalNode VALUES() { return getToken(CurseGrammar.VALUES, 0); }
		public List<TerminalNode> LBRACKET() { return getTokens(CurseGrammar.LBRACKET); }
		public TerminalNode LBRACKET(int i) {
			return getToken(CurseGrammar.LBRACKET, i);
		}
		public List<TerminalNode> RBRACKET() { return getTokens(CurseGrammar.RBRACKET); }
		public TerminalNode RBRACKET(int i) {
			return getToken(CurseGrammar.RBRACKET, i);
		}
		public List<TerminalNode> VALUE_FLAG() { return getTokens(CurseGrammar.VALUE_FLAG); }
		public TerminalNode VALUE_FLAG(int i) {
			return getToken(CurseGrammar.VALUE_FLAG, i);
		}
		public List<TerminalNode> VALUE_INT() { return getTokens(CurseGrammar.VALUE_INT); }
		public TerminalNode VALUE_INT(int i) {
			return getToken(CurseGrammar.VALUE_INT, i);
		}
		public List<TerminalNode> VALUE_OR() { return getTokens(CurseGrammar.VALUE_OR); }
		public TerminalNode VALUE_OR(int i) {
			return getToken(CurseGrammar.VALUE_OR, i);
		}
		public ValuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_values; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).enterValues(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).exitValues(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CurseGrammarVisitor ) return ((CurseGrammarVisitor<? extends T>)visitor).visitValues(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValuesContext values() throws RecognitionException {
		ValuesContext _localctx = new ValuesContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_values);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			 ((ValuesContext)_localctx).valueMap =  new HashMap<>(); 
			setState(73);
			match(VALUES);
			setState(74);
			((ValuesContext)_localctx).vf1 = match(VALUE_FLAG);
			setState(75);
			match(LBRACKET);
			setState(76);
			((ValuesContext)_localctx).vi1 = match(VALUE_INT);
			setState(77);
			match(RBRACKET);

			                _localctx.valueMap.put(((ValuesContext)_localctx).vf1.getText(), ((ValuesContext)_localctx).vi1.getText());
			            
			setState(87);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VALUE_OR) {
				{
				{
				setState(79);
				match(VALUE_OR);
				setState(80);
				((ValuesContext)_localctx).vf2 = match(VALUE_FLAG);
				setState(81);
				match(LBRACKET);
				setState(82);
				((ValuesContext)_localctx).vi2 = match(VALUE_INT);
				setState(83);
				match(RBRACKET);

				                _localctx.valueMap.put(((ValuesContext)_localctx).vf2.getText(), ((ValuesContext)_localctx).vi2.getText());
				            
				}
				}
				setState(89);
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
	public static class MsgContext extends ParserRuleContext {
		public String msgStr;
		public Token FREE_TEXT;
		public TerminalNode CURSE_MSG() { return getToken(CurseGrammar.CURSE_MSG, 0); }
		public TerminalNode FREE_TEXT() { return getToken(CurseGrammar.FREE_TEXT, 0); }
		public MsgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_msg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).enterMsg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).exitMsg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CurseGrammarVisitor ) return ((CurseGrammarVisitor<? extends T>)visitor).visitMsg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MsgContext msg() throws RecognitionException {
		MsgContext _localctx = new MsgContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_msg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(90);
			match(CURSE_MSG);
			setState(91);
			((MsgContext)_localctx).FREE_TEXT = match(FREE_TEXT);
			 ((MsgContext)_localctx).msgStr =  ((MsgContext)_localctx).FREE_TEXT.getText(); 
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
		public String description;
		public Token FREE_TEXT;
		public TerminalNode DESC() { return getToken(CurseGrammar.DESC, 0); }
		public TerminalNode FREE_TEXT() { return getToken(CurseGrammar.FREE_TEXT, 0); }
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_desc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CurseGrammarVisitor ) return ((CurseGrammarVisitor<? extends T>)visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_desc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(94);
			match(DESC);
			setState(95);
			((DescContext)_localctx).FREE_TEXT = match(FREE_TEXT);
			 ((DescContext)_localctx).description =  ((DescContext)_localctx).FREE_TEXT.getText(); 
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
	public static class ConflictContext extends ParserRuleContext {
		public String conflictingCurses;
		public Token FREE_TEXT;
		public TerminalNode CONFLICT() { return getToken(CurseGrammar.CONFLICT, 0); }
		public TerminalNode FREE_TEXT() { return getToken(CurseGrammar.FREE_TEXT, 0); }
		public ConflictContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conflict; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).enterConflict(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).exitConflict(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CurseGrammarVisitor ) return ((CurseGrammarVisitor<? extends T>)visitor).visitConflict(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConflictContext conflict() throws RecognitionException {
		ConflictContext _localctx = new ConflictContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_conflict);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(98);
			match(CONFLICT);
			setState(99);
			((ConflictContext)_localctx).FREE_TEXT = match(FREE_TEXT);
			 ((ConflictContext)_localctx).conflictingCurses =  ((ConflictContext)_localctx).FREE_TEXT.getText(); 
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
	public static class ConflictFlagsContext extends ParserRuleContext {
		public List<String> cFlags;
		public Token f1;
		public Token f2;
		public TerminalNode CONFLICT_FLAGS() { return getToken(CurseGrammar.CONFLICT_FLAGS, 0); }
		public List<TerminalNode> FLAG() { return getTokens(CurseGrammar.FLAG); }
		public TerminalNode FLAG(int i) {
			return getToken(CurseGrammar.FLAG, i);
		}
		public List<TerminalNode> FLAG_OR() { return getTokens(CurseGrammar.FLAG_OR); }
		public TerminalNode FLAG_OR(int i) {
			return getToken(CurseGrammar.FLAG_OR, i);
		}
		public ConflictFlagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conflictFlags; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).enterConflictFlags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).exitConflictFlags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CurseGrammarVisitor ) return ((CurseGrammarVisitor<? extends T>)visitor).visitConflictFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConflictFlagsContext conflictFlags() throws RecognitionException {
		ConflictFlagsContext _localctx = new ConflictFlagsContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_conflictFlags);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			 ((ConflictFlagsContext)_localctx).cFlags =  new ArrayList<>(); 
			setState(103);
			match(CONFLICT_FLAGS);
			setState(104);
			((ConflictFlagsContext)_localctx).f1 = match(FLAG);
			 _localctx.cFlags.add(((ConflictFlagsContext)_localctx).f1.getText()); 
			setState(111);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FLAG_OR) {
				{
				{
				setState(106);
				match(FLAG_OR);
				setState(107);
				((ConflictFlagsContext)_localctx).f2 = match(FLAG);
				 _localctx.cFlags.add(((ConflictFlagsContext)_localctx).f2.getText()); 
				}
				}
				setState(113);
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
	public static class TimeContext extends ParserRuleContext {
		public String timeStr;
		public Token DICE_SIMPLE_VALUE;
		public TerminalNode TIME() { return getToken(CurseGrammar.TIME, 0); }
		public TerminalNode DICE_SIMPLE_VALUE() { return getToken(CurseGrammar.DICE_SIMPLE_VALUE, 0); }
		public TimeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_time; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).enterTime(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).exitTime(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CurseGrammarVisitor ) return ((CurseGrammarVisitor<? extends T>)visitor).visitTime(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TimeContext time() throws RecognitionException {
		TimeContext _localctx = new TimeContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_time);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(114);
			match(TIME);
			setState(115);
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
	public static class CurseRecordContext extends ParserRuleContext {
		public CurseParseRecord record;
		public NameContext name;
		public CurseTypeContext curseType;
		public WeightContext weight;
		public CombatContext combat;
		public EffectBlockContext effectBlock;
		public EffectYXContext effectYX;
		public DiceContext dice;
		public ExprContext expr;
		public FlagsContext flags;
		public ValuesContext values;
		public MsgContext msg;
		public DescContext desc;
		public ConflictContext conflict;
		public ConflictFlagsContext conflictFlags;
		public TimeContext time;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public List<CurseTypeContext> curseType() {
			return getRuleContexts(CurseTypeContext.class);
		}
		public CurseTypeContext curseType(int i) {
			return getRuleContext(CurseTypeContext.class,i);
		}
		public List<WeightContext> weight() {
			return getRuleContexts(WeightContext.class);
		}
		public WeightContext weight(int i) {
			return getRuleContext(WeightContext.class,i);
		}
		public List<CombatContext> combat() {
			return getRuleContexts(CombatContext.class);
		}
		public CombatContext combat(int i) {
			return getRuleContext(CombatContext.class,i);
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
		public List<FlagsContext> flags() {
			return getRuleContexts(FlagsContext.class);
		}
		public FlagsContext flags(int i) {
			return getRuleContext(FlagsContext.class,i);
		}
		public List<ValuesContext> values() {
			return getRuleContexts(ValuesContext.class);
		}
		public ValuesContext values(int i) {
			return getRuleContext(ValuesContext.class,i);
		}
		public List<MsgContext> msg() {
			return getRuleContexts(MsgContext.class);
		}
		public MsgContext msg(int i) {
			return getRuleContext(MsgContext.class,i);
		}
		public List<DescContext> desc() {
			return getRuleContexts(DescContext.class);
		}
		public DescContext desc(int i) {
			return getRuleContext(DescContext.class,i);
		}
		public List<ConflictContext> conflict() {
			return getRuleContexts(ConflictContext.class);
		}
		public ConflictContext conflict(int i) {
			return getRuleContext(ConflictContext.class,i);
		}
		public List<ConflictFlagsContext> conflictFlags() {
			return getRuleContexts(ConflictFlagsContext.class);
		}
		public ConflictFlagsContext conflictFlags(int i) {
			return getRuleContext(ConflictFlagsContext.class,i);
		}
		public List<TimeContext> time() {
			return getRuleContexts(TimeContext.class);
		}
		public TimeContext time(int i) {
			return getRuleContext(TimeContext.class,i);
		}
		public CurseRecordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_curseRecord; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).enterCurseRecord(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).exitCurseRecord(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CurseGrammarVisitor ) return ((CurseGrammarVisitor<? extends T>)visitor).visitCurseRecord(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CurseRecordContext curseRecord() throws RecognitionException {
		CurseRecordContext _localctx = new CurseRecordContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_curseRecord);

		            int blockLine = 0;
		            String nameInit = "";
		            String tohInit = "";
		            String todInit = "";
		            String toaInit = "";
		            String msgInit = null;
		            List<String> curseTypeInit = new ArrayList<>();
		            List<EffectParseRecord> effects = new ArrayList<>();
		            List<String> flagsListInit = new ArrayList<>();
		            Map<String, String> valuesInit = new HashMap<>();
		            List<String> descInit = new ArrayList<>();
		            List<String> conflictInit = new ArrayList<>();
		            List<String> cFlagsInit = new ArrayList<>();
		            String weightAdjustmentInit = "";
		            String timeInit = "";
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(118);
			((CurseRecordContext)_localctx).name = name();
			 nameInit = ((CurseRecordContext)_localctx).name.nameStr;
			                   blockLine = ((CurseRecordContext)_localctx).name.line; 
			setState(162); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(162);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case TYPE:
					{
					setState(120);
					((CurseRecordContext)_localctx).curseType = curseType();
					 curseTypeInit.add(((CurseRecordContext)_localctx).curseType.typeStr); 
					}
					break;
				case WEIGHT:
					{
					setState(123);
					((CurseRecordContext)_localctx).weight = weight();
					 weightAdjustmentInit = ((CurseRecordContext)_localctx).weight.weightAdj; 
					}
					break;
				case COMBAT:
					{
					setState(126);
					((CurseRecordContext)_localctx).combat = combat();
					 tohInit = ((CurseRecordContext)_localctx).combat.toh;
					                     todInit = ((CurseRecordContext)_localctx).combat.tod;
					                     toaInit = ((CurseRecordContext)_localctx).combat.toa; 
					}
					break;
				case EFFECT:
					{
					setState(129);
					((CurseRecordContext)_localctx).effectBlock = effectBlock();
					 effects.add(new EffectParseRecord(((CurseRecordContext)_localctx).effectBlock.typeInit,
					                ((CurseRecordContext)_localctx).effectBlock.subtypeWrapperInit, ((CurseRecordContext)_localctx).effectBlock.radius, ((CurseRecordContext)_localctx).effectBlock.other,
					                ((CurseRecordContext)_localctx).effectBlock.diceString, ((CurseRecordContext)_localctx).effectBlock.yVal, ((CurseRecordContext)_localctx).effectBlock.xVal,
					                ((CurseRecordContext)_localctx).effectBlock.expressionChars, ((CurseRecordContext)_localctx).effectBlock.expressionBase,
					                ((CurseRecordContext)_localctx).effectBlock.expressionOperation,
					                ((CurseRecordContext)_localctx).effectBlock.effectMessage, (((CurseRecordContext)_localctx).effectBlock!=null?(((CurseRecordContext)_localctx).effectBlock.start):null).getLine())); 
					}
					break;
				case EFFECT_YX:
					{
					setState(132);
					((CurseRecordContext)_localctx).effectYX = effectYX();

					                if (!effects.isEmpty()) {
					                    EffectParseRecord last = effects.getLast();
					                    EffectParseRecord updated = last.amendYX(((CurseRecordContext)_localctx).effectYX.y, ((CurseRecordContext)_localctx).effectYX.x);
					                    replaceLast(effects, updated);
					                }
					            
					}
					break;
				case DICE:
					{
					setState(135);
					((CurseRecordContext)_localctx).dice = dice();

					                if (!effects.isEmpty()) {
					                    EffectParseRecord last = effects.getLast();
					                    String diceStr = ((CurseRecordContext)_localctx).dice.diceString;
					                    EffectParseRecord updated = last.amendExpression(diceStr,
					                                                                     ((CurseRecordContext)_localctx).dice.exprChar,
					                                                                     ((CurseRecordContext)_localctx).dice.baseName,
					                                                                     ((CurseRecordContext)_localctx).dice.operation);
					                    replaceLast(effects, updated);
					                }
					            
					}
					break;
				case EXPR:
					{
					setState(138);
					((CurseRecordContext)_localctx).expr = expr();

					                if (!effects.isEmpty() && !effects.getLast().diceString().isEmpty()) {
					                    EffectParseRecord last = effects.getLast();
					                    String lastDiceStr  = last.diceString();
					                    String lastExprChar = last.expressionChars().isEmpty() 
					                                        ? ((CurseRecordContext)_localctx).expr.exprChar
					                                        : last.expressionChars() + "^" + ((CurseRecordContext)_localctx).expr.exprChar;
					                    String lastExprBase = last.expressionBases().isEmpty() 
					                                        ? ((CurseRecordContext)_localctx).expr.baseName
					                                        : last.expressionBases() + "^" + ((CurseRecordContext)_localctx).expr.baseName;
					                    String lastExprOp   = last.expressionOperations().isEmpty() 
					                                        ? ((CurseRecordContext)_localctx).expr.operation
					                                        : last.expressionOperations() + "^" + ((CurseRecordContext)_localctx).expr.operation;
					                    EffectParseRecord updated = last.amendExpression(lastDiceStr,
					                                                lastExprChar,
					                                                lastExprBase,
					                                                lastExprOp);
					                    replaceLast(effects, updated);
					                }
					            
					}
					break;
				case FLAGS:
					{
					setState(141);
					((CurseRecordContext)_localctx).flags = flags();
					 flagsListInit.addAll(((CurseRecordContext)_localctx).flags.flagList); 
					}
					break;
				case VALUES:
					{
					setState(144);
					((CurseRecordContext)_localctx).values = values();
					 valuesInit.putAll(((CurseRecordContext)_localctx).values.valueMap); 
					}
					break;
				case CURSE_MSG:
					{
					setState(147);
					((CurseRecordContext)_localctx).msg = msg();
					 
					                if (msgInit == null) 
					                    msgInit = ((CurseRecordContext)_localctx).msg.msgStr;
					                else
					                    msgInit = msgInit + ((CurseRecordContext)_localctx).msg.msgStr; 
					}
					break;
				case DESC:
					{
					setState(150);
					((CurseRecordContext)_localctx).desc = desc();
					 descInit.add(((CurseRecordContext)_localctx).desc.description); 
					}
					break;
				case CONFLICT:
					{
					setState(153);
					((CurseRecordContext)_localctx).conflict = conflict();
					 conflictInit.add(((CurseRecordContext)_localctx).conflict.conflictingCurses); 
					}
					break;
				case CONFLICT_FLAGS:
					{
					setState(156);
					((CurseRecordContext)_localctx).conflictFlags = conflictFlags();
					 cFlagsInit.addAll(((CurseRecordContext)_localctx).conflictFlags.cFlags); 
					}
					break;
				case TIME:
					{
					setState(159);
					((CurseRecordContext)_localctx).time = time();
					 timeInit = ((CurseRecordContext)_localctx).time.timeStr; 
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(164); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 1001468L) != 0) );
			}
			_ctx.stop = _input.LT(-1);

			            ((CurseRecordContext)_localctx).record =  new CurseParseRecord(nameInit, curseTypeInit, weightAdjustmentInit,
			                        tohInit, todInit, toaInit, effects, flagsListInit, valuesInit,
			                        msgInit, descInit, conflictInit, cFlagsInit, timeInit, blockLine);
			        
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
		public List<CurseParseRecord> records;
		public CurseRecordContext curseRecord;
		public TerminalNode EOF() { return getToken(CurseGrammar.EOF, 0); }
		public List<CurseRecordContext> curseRecord() {
			return getRuleContexts(CurseRecordContext.class);
		}
		public CurseRecordContext curseRecord(int i) {
			return getRuleContext(CurseRecordContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CurseGrammarVisitor ) return ((CurseGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_file);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			 ((FileContext)_localctx).records =  new ArrayList<>(); 
			setState(170); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(167);
				((FileContext)_localctx).curseRecord = curseRecord();
				 _localctx.records.add(((FileContext)_localctx).curseRecord.record); 
				}
				}
				setState(172); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(174);
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
		public TerminalNode EFFECT() { return getToken(CurseGrammar.EFFECT, 0); }
		public List<TerminalNode> UCASE() { return getTokens(CurseGrammar.UCASE); }
		public TerminalNode UCASE(int i) {
			return getToken(CurseGrammar.UCASE, i);
		}
		public List<TerminalNode> COLON() { return getTokens(CurseGrammar.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(CurseGrammar.COLON, i);
		}
		public List<TerminalNode> INTEGER() { return getTokens(CurseGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(CurseGrammar.INTEGER, i);
		}
		public EffectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effect; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).enterEffect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).exitEffect(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CurseGrammarVisitor ) return ((CurseGrammarVisitor<? extends T>)visitor).visitEffect(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectContext effect() throws RecognitionException {
		EffectContext _localctx = new EffectContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_effect);

		            ((EffectContext)_localctx).wrapper =  "";
		            ((EffectContext)_localctx).radius =  "";
		            ((EffectContext)_localctx).other =  "";
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(176);
			match(EFFECT);
			setState(177);
			((EffectContext)_localctx).t = match(UCASE);

			                ((EffectContext)_localctx).type =  ((EffectContext)_localctx).t.getText();
			            
			setState(192);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(179);
				match(COLON);
				setState(180);
				((EffectContext)_localctx).st = match(UCASE);

				                ((EffectContext)_localctx).wrapper =  ((EffectContext)_localctx).st.getText();
				            
				setState(190);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COLON) {
					{
					setState(182);
					match(COLON);
					setState(183);
					((EffectContext)_localctx).rad = match(INTEGER);

					                ((EffectContext)_localctx).radius =  ((EffectContext)_localctx).rad.getText();
					            
					setState(188);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==COLON) {
						{
						setState(185);
						match(COLON);
						setState(186);
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
		public TerminalNode EFFECT_YX() { return getToken(CurseGrammar.EFFECT_YX, 0); }
		public TerminalNode COLON() { return getToken(CurseGrammar.COLON, 0); }
		public List<TerminalNode> INTEGER() { return getTokens(CurseGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(CurseGrammar.INTEGER, i);
		}
		public EffectYXContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effectYX; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).enterEffectYX(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).exitEffectYX(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CurseGrammarVisitor ) return ((CurseGrammarVisitor<? extends T>)visitor).visitEffectYX(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectYXContext effectYX() throws RecognitionException {
		EffectYXContext _localctx = new EffectYXContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_effectYX);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(194);
			match(EFFECT_YX);
			setState(195);
			((EffectYXContext)_localctx).yVal = match(INTEGER);
			setState(196);
			match(COLON);
			setState(197);
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
		public TerminalNode DICE() { return getToken(CurseGrammar.DICE, 0); }
		public TerminalNode DICE_SIMPLE_VALUE() { return getToken(CurseGrammar.DICE_SIMPLE_VALUE, 0); }
		public TerminalNode DICE_COMPLEX_VALUE() { return getToken(CurseGrammar.DICE_COMPLEX_VALUE, 0); }
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
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).enterDice(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).exitDice(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CurseGrammarVisitor ) return ((CurseGrammarVisitor<? extends T>)visitor).visitDice(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DiceContext dice() throws RecognitionException {
		DiceContext _localctx = new DiceContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_dice);

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
			setState(200);
			match(DICE);
			setState(213);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DICE_COMPLEX_VALUE:
				{
				{
				setState(201);
				((DiceContext)_localctx).val = match(DICE_COMPLEX_VALUE);

				                ((DiceContext)_localctx).diceString =  ((DiceContext)_localctx).val.getText();
				            
				setState(208);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(203);
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
					setState(210);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
				}
				}
				}
				break;
			case DICE_SIMPLE_VALUE:
				{
				setState(211);
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
		public TerminalNode EXPR() { return getToken(CurseGrammar.EXPR, 0); }
		public List<TerminalNode> EXPR_COLON() { return getTokens(CurseGrammar.EXPR_COLON); }
		public TerminalNode EXPR_COLON(int i) {
			return getToken(CurseGrammar.EXPR_COLON, i);
		}
		public TerminalNode EXPR_CHAR() { return getToken(CurseGrammar.EXPR_CHAR, 0); }
		public TerminalNode EXPR_UCASE() { return getToken(CurseGrammar.EXPR_UCASE, 0); }
		public TerminalNode EXPR_OP() { return getToken(CurseGrammar.EXPR_OP, 0); }
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).exitExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CurseGrammarVisitor ) return ((CurseGrammarVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(215);
			match(EXPR);
			setState(216);
			((ExprContext)_localctx).ch = match(EXPR_CHAR);
			setState(217);
			match(EXPR_COLON);
			setState(218);
			((ExprContext)_localctx).base = match(EXPR_UCASE);
			setState(219);
			match(EXPR_COLON);
			setState(220);
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
		public TerminalNode EFFECT_MESSAGE() { return getToken(CurseGrammar.EFFECT_MESSAGE, 0); }
		public TerminalNode FREE_TEXT() { return getToken(CurseGrammar.FREE_TEXT, 0); }
		public EffectMsgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effectMsg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).enterEffectMsg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).exitEffectMsg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CurseGrammarVisitor ) return ((CurseGrammarVisitor<? extends T>)visitor).visitEffectMsg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectMsgContext effectMsg() throws RecognitionException {
		EffectMsgContext _localctx = new EffectMsgContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_effectMsg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(223);
			match(EFFECT_MESSAGE);
			setState(224);
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
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).enterStrayEffectLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).exitStrayEffectLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CurseGrammarVisitor ) return ((CurseGrammarVisitor<? extends T>)visitor).visitStrayEffectLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StrayEffectLineContext strayEffectLine() throws RecognitionException {
		StrayEffectLineContext _localctx = new StrayEffectLineContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_strayEffectLine);
		try {
			setState(230);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EFFECT_YX:
				enterOuterAlt(_localctx, 1);
				{
				setState(227);
				effectYX();
				}
				break;
			case DICE:
				enterOuterAlt(_localctx, 2);
				{
				setState(228);
				dice();
				}
				break;
			case EXPR:
				enterOuterAlt(_localctx, 3);
				{
				setState(229);
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
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).enterEffectBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CurseGrammarListener ) ((CurseGrammarListener)listener).exitEffectBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CurseGrammarVisitor ) return ((CurseGrammarVisitor<? extends T>)visitor).visitEffectBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectBlockContext effectBlock() throws RecognitionException {
		EffectBlockContext _localctx = new EffectBlockContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_effectBlock);

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
			setState(232);
			((EffectBlockContext)_localctx).effect = effect();

			                ((EffectBlockContext)_localctx).lineNo =  _localctx.start.getLine();
			                ((EffectBlockContext)_localctx).typeInit =  ((EffectBlockContext)_localctx).effect.type;
			                ((EffectBlockContext)_localctx).subtypeWrapperInit =  ((EffectBlockContext)_localctx).effect.wrapper;
			                ((EffectBlockContext)_localctx).radius =  ((EffectBlockContext)_localctx).effect.radius;
			                ((EffectBlockContext)_localctx).other =  ((EffectBlockContext)_localctx).effect.other;
			            
			setState(250);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				{
				{
				setState(234);
				((EffectBlockContext)_localctx).effectYX = effectYX();

				                ((EffectBlockContext)_localctx).yVal =  ((EffectBlockContext)_localctx).effectYX.y;
				                ((EffectBlockContext)_localctx).xVal =  ((EffectBlockContext)_localctx).effectYX.x;
				            
				setState(236);
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
				setState(239);
				((EffectBlockContext)_localctx).dice = dice();

				                ((EffectBlockContext)_localctx).diceString =  ((EffectBlockContext)_localctx).dice.diceString;
				                expressionString = ((EffectBlockContext)_localctx).dice.exprChar;
				                baseString = ((EffectBlockContext)_localctx).dice.baseName;
				                opString = ((EffectBlockContext)_localctx).dice.operation;
				            
				setState(241);
				((EffectBlockContext)_localctx).effectYX = effectYX();

				                ((EffectBlockContext)_localctx).yVal =  ((EffectBlockContext)_localctx).effectYX.y;
				                ((EffectBlockContext)_localctx).xVal =  ((EffectBlockContext)_localctx).effectYX.x;
				            
				}
				}
				break;
			case 3:
				{
				setState(244);
				((EffectBlockContext)_localctx).dice = dice();

				                ((EffectBlockContext)_localctx).diceString =  ((EffectBlockContext)_localctx).dice.diceString;
				                expressionString = ((EffectBlockContext)_localctx).dice.exprChar;
				                baseString = ((EffectBlockContext)_localctx).dice.baseName;
				                opString = ((EffectBlockContext)_localctx).dice.operation;
				            
				}
				break;
			case 4:
				{
				setState(247);
				((EffectBlockContext)_localctx).effectYX = effectYX();

				                ((EffectBlockContext)_localctx).yVal =  ((EffectBlockContext)_localctx).effectYX.y;
				                ((EffectBlockContext)_localctx).xVal =  ((EffectBlockContext)_localctx).effectYX.x;
				            
				}
				break;
			}
			setState(255);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EFFECT_MESSAGE) {
				{
				setState(252);
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
		"\u0004\u0001)\u0102\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0005\u0004"+
		"D\b\u0004\n\u0004\f\u0004G\t\u0004\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0005\u0005V\b\u0005"+
		"\n\u0005\f\u0005Y\t\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0005"+
		"\tn\b\t\n\t\f\tq\t\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0004\u000b\u00a3\b\u000b\u000b\u000b\f\u000b\u00a4\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0004\f\u00ab\b\f\u000b\f\f\f\u00ac\u0001\f\u0001\f"+
		"\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0003\r\u00bd\b\r\u0003\r\u00bf\b\r\u0003\r"+
		"\u00c1\b\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0005\u000f\u00cf\b\u000f\n\u000f\f\u000f\u00d2\t\u000f\u0001"+
		"\u000f\u0001\u000f\u0003\u000f\u00d6\b\u000f\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0003\u0012\u00e7\b\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0003\u0013\u00fb\b\u0013\u0001\u0013\u0001"+
		"\u0013\u0001\u0013\u0003\u0013\u0100\b\u0013\u0001\u0013\u0000\u0000\u0014"+
		"\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a"+
		"\u001c\u001e \"$&\u0000\u0000\u010b\u0000(\u0001\u0000\u0000\u0000\u0002"+
		",\u0001\u0000\u0000\u0000\u00040\u0001\u0000\u0000\u0000\u00064\u0001"+
		"\u0000\u0000\u0000\b<\u0001\u0000\u0000\u0000\nH\u0001\u0000\u0000\u0000"+
		"\fZ\u0001\u0000\u0000\u0000\u000e^\u0001\u0000\u0000\u0000\u0010b\u0001"+
		"\u0000\u0000\u0000\u0012f\u0001\u0000\u0000\u0000\u0014r\u0001\u0000\u0000"+
		"\u0000\u0016v\u0001\u0000\u0000\u0000\u0018\u00a6\u0001\u0000\u0000\u0000"+
		"\u001a\u00b0\u0001\u0000\u0000\u0000\u001c\u00c2\u0001\u0000\u0000\u0000"+
		"\u001e\u00c8\u0001\u0000\u0000\u0000 \u00d7\u0001\u0000\u0000\u0000\""+
		"\u00df\u0001\u0000\u0000\u0000$\u00e6\u0001\u0000\u0000\u0000&\u00e8\u0001"+
		"\u0000\u0000\u0000()\u0005\u0001\u0000\u0000)*\u0005\"\u0000\u0000*+\u0006"+
		"\u0000\uffff\uffff\u0000+\u0001\u0001\u0000\u0000\u0000,-\u0005\u0002"+
		"\u0000\u0000-.\u0005\"\u0000\u0000./\u0006\u0001\uffff\uffff\u0000/\u0003"+
		"\u0001\u0000\u0000\u000001\u0005\u0003\u0000\u000012\u0005\u0016\u0000"+
		"\u000023\u0006\u0002\uffff\uffff\u00003\u0005\u0001\u0000\u0000\u0000"+
		"45\u0005\u0004\u0000\u000056\u0005\u0016\u0000\u000067\u0005\u0014\u0000"+
		"\u000078\u0005\u0016\u0000\u000089\u0005\u0014\u0000\u00009:\u0005\u0016"+
		"\u0000\u0000:;\u0006\u0003\uffff\uffff\u0000;\u0007\u0001\u0000\u0000"+
		"\u0000<=\u0006\u0004\uffff\uffff\u0000=>\u0005\u0005\u0000\u0000>?\u0005"+
		"\u001f\u0000\u0000?E\u0006\u0004\uffff\uffff\u0000@A\u0005 \u0000\u0000"+
		"AB\u0005\u001f\u0000\u0000BD\u0006\u0004\uffff\uffff\u0000C@\u0001\u0000"+
		"\u0000\u0000DG\u0001\u0000\u0000\u0000EC\u0001\u0000\u0000\u0000EF\u0001"+
		"\u0000\u0000\u0000F\t\u0001\u0000\u0000\u0000GE\u0001\u0000\u0000\u0000"+
		"HI\u0006\u0005\uffff\uffff\u0000IJ\u0005\u0006\u0000\u0000JK\u0005\u001b"+
		"\u0000\u0000KL\u0005\u0019\u0000\u0000LM\u0005\u001e\u0000\u0000MN\u0005"+
		"\u001a\u0000\u0000NW\u0006\u0005\uffff\uffff\u0000OP\u0005\u001c\u0000"+
		"\u0000PQ\u0005\u001b\u0000\u0000QR\u0005\u0019\u0000\u0000RS\u0005\u001e"+
		"\u0000\u0000ST\u0005\u001a\u0000\u0000TV\u0006\u0005\uffff\uffff\u0000"+
		"UO\u0001\u0000\u0000\u0000VY\u0001\u0000\u0000\u0000WU\u0001\u0000\u0000"+
		"\u0000WX\u0001\u0000\u0000\u0000X\u000b\u0001\u0000\u0000\u0000YW\u0001"+
		"\u0000\u0000\u0000Z[\u0005\u0007\u0000\u0000[\\\u0005\"\u0000\u0000\\"+
		"]\u0006\u0006\uffff\uffff\u0000]\r\u0001\u0000\u0000\u0000^_\u0005\b\u0000"+
		"\u0000_`\u0005\"\u0000\u0000`a\u0006\u0007\uffff\uffff\u0000a\u000f\u0001"+
		"\u0000\u0000\u0000bc\u0005\t\u0000\u0000cd\u0005\"\u0000\u0000de\u0006"+
		"\b\uffff\uffff\u0000e\u0011\u0001\u0000\u0000\u0000fg\u0006\t\uffff\uffff"+
		"\u0000gh\u0005\n\u0000\u0000hi\u0005\u001f\u0000\u0000io\u0006\t\uffff"+
		"\uffff\u0000jk\u0005 \u0000\u0000kl\u0005\u001f\u0000\u0000ln\u0006\t"+
		"\uffff\uffff\u0000mj\u0001\u0000\u0000\u0000nq\u0001\u0000\u0000\u0000"+
		"om\u0001\u0000\u0000\u0000op\u0001\u0000\u0000\u0000p\u0013\u0001\u0000"+
		"\u0000\u0000qo\u0001\u0000\u0000\u0000rs\u0005\u0011\u0000\u0000st\u0005"+
		"#\u0000\u0000tu\u0006\n\uffff\uffff\u0000u\u0015\u0001\u0000\u0000\u0000"+
		"vw\u0003\u0000\u0000\u0000w\u00a2\u0006\u000b\uffff\uffff\u0000xy\u0003"+
		"\u0002\u0001\u0000yz\u0006\u000b\uffff\uffff\u0000z\u00a3\u0001\u0000"+
		"\u0000\u0000{|\u0003\u0004\u0002\u0000|}\u0006\u000b\uffff\uffff\u0000"+
		"}\u00a3\u0001\u0000\u0000\u0000~\u007f\u0003\u0006\u0003\u0000\u007f\u0080"+
		"\u0006\u000b\uffff\uffff\u0000\u0080\u00a3\u0001\u0000\u0000\u0000\u0081"+
		"\u0082\u0003&\u0013\u0000\u0082\u0083\u0006\u000b\uffff\uffff\u0000\u0083"+
		"\u00a3\u0001\u0000\u0000\u0000\u0084\u0085\u0003\u001c\u000e\u0000\u0085"+
		"\u0086\u0006\u000b\uffff\uffff\u0000\u0086\u00a3\u0001\u0000\u0000\u0000"+
		"\u0087\u0088\u0003\u001e\u000f\u0000\u0088\u0089\u0006\u000b\uffff\uffff"+
		"\u0000\u0089\u00a3\u0001\u0000\u0000\u0000\u008a\u008b\u0003 \u0010\u0000"+
		"\u008b\u008c\u0006\u000b\uffff\uffff\u0000\u008c\u00a3\u0001\u0000\u0000"+
		"\u0000\u008d\u008e\u0003\b\u0004\u0000\u008e\u008f\u0006\u000b\uffff\uffff"+
		"\u0000\u008f\u00a3\u0001\u0000\u0000\u0000\u0090\u0091\u0003\n\u0005\u0000"+
		"\u0091\u0092\u0006\u000b\uffff\uffff\u0000\u0092\u00a3\u0001\u0000\u0000"+
		"\u0000\u0093\u0094\u0003\f\u0006\u0000\u0094\u0095\u0006\u000b\uffff\uffff"+
		"\u0000\u0095\u00a3\u0001\u0000\u0000\u0000\u0096\u0097\u0003\u000e\u0007"+
		"\u0000\u0097\u0098\u0006\u000b\uffff\uffff\u0000\u0098\u00a3\u0001\u0000"+
		"\u0000\u0000\u0099\u009a\u0003\u0010\b\u0000\u009a\u009b\u0006\u000b\uffff"+
		"\uffff\u0000\u009b\u00a3\u0001\u0000\u0000\u0000\u009c\u009d\u0003\u0012"+
		"\t\u0000\u009d\u009e\u0006\u000b\uffff\uffff\u0000\u009e\u00a3\u0001\u0000"+
		"\u0000\u0000\u009f\u00a0\u0003\u0014\n\u0000\u00a0\u00a1\u0006\u000b\uffff"+
		"\uffff\u0000\u00a1\u00a3\u0001\u0000\u0000\u0000\u00a2x\u0001\u0000\u0000"+
		"\u0000\u00a2{\u0001\u0000\u0000\u0000\u00a2~\u0001\u0000\u0000\u0000\u00a2"+
		"\u0081\u0001\u0000\u0000\u0000\u00a2\u0084\u0001\u0000\u0000\u0000\u00a2"+
		"\u0087\u0001\u0000\u0000\u0000\u00a2\u008a\u0001\u0000\u0000\u0000\u00a2"+
		"\u008d\u0001\u0000\u0000\u0000\u00a2\u0090\u0001\u0000\u0000\u0000\u00a2"+
		"\u0093\u0001\u0000\u0000\u0000\u00a2\u0096\u0001\u0000\u0000\u0000\u00a2"+
		"\u0099\u0001\u0000\u0000\u0000\u00a2\u009c\u0001\u0000\u0000\u0000\u00a2"+
		"\u009f\u0001\u0000\u0000\u0000\u00a3\u00a4\u0001\u0000\u0000\u0000\u00a4"+
		"\u00a2\u0001\u0000\u0000\u0000\u00a4\u00a5\u0001\u0000\u0000\u0000\u00a5"+
		"\u0017\u0001\u0000\u0000\u0000\u00a6\u00aa\u0006\f\uffff\uffff\u0000\u00a7"+
		"\u00a8\u0003\u0016\u000b\u0000\u00a8\u00a9\u0006\f\uffff\uffff\u0000\u00a9"+
		"\u00ab\u0001\u0000\u0000\u0000\u00aa\u00a7\u0001\u0000\u0000\u0000\u00ab"+
		"\u00ac\u0001\u0000\u0000\u0000\u00ac\u00aa\u0001\u0000\u0000\u0000\u00ac"+
		"\u00ad\u0001\u0000\u0000\u0000\u00ad\u00ae\u0001\u0000\u0000\u0000\u00ae"+
		"\u00af\u0005\u0000\u0000\u0001\u00af\u0019\u0001\u0000\u0000\u0000\u00b0"+
		"\u00b1\u0005\u000e\u0000\u0000\u00b1\u00b2\u0005\u0015\u0000\u0000\u00b2"+
		"\u00c0\u0006\r\uffff\uffff\u0000\u00b3\u00b4\u0005\u0014\u0000\u0000\u00b4"+
		"\u00b5\u0005\u0015\u0000\u0000\u00b5\u00be\u0006\r\uffff\uffff\u0000\u00b6"+
		"\u00b7\u0005\u0014\u0000\u0000\u00b7\u00b8\u0005\u0016\u0000\u0000\u00b8"+
		"\u00bc\u0006\r\uffff\uffff\u0000\u00b9\u00ba\u0005\u0014\u0000\u0000\u00ba"+
		"\u00bb\u0005\u0016\u0000\u0000\u00bb\u00bd\u0006\r\uffff\uffff\u0000\u00bc"+
		"\u00b9\u0001\u0000\u0000\u0000\u00bc\u00bd\u0001\u0000\u0000\u0000\u00bd"+
		"\u00bf\u0001\u0000\u0000\u0000\u00be\u00b6\u0001\u0000\u0000\u0000\u00be"+
		"\u00bf\u0001\u0000\u0000\u0000\u00bf\u00c1\u0001\u0000\u0000\u0000\u00c0"+
		"\u00b3\u0001\u0000\u0000\u0000\u00c0\u00c1\u0001\u0000\u0000\u0000\u00c1"+
		"\u001b\u0001\u0000\u0000\u0000\u00c2\u00c3\u0005\u0012\u0000\u0000\u00c3"+
		"\u00c4\u0005\u0016\u0000\u0000\u00c4\u00c5\u0005\u0014\u0000\u0000\u00c5"+
		"\u00c6\u0005\u0016\u0000\u0000\u00c6\u00c7\u0006\u000e\uffff\uffff\u0000"+
		"\u00c7\u001d\u0001\u0000\u0000\u0000\u00c8\u00d5\u0005\u0010\u0000\u0000"+
		"\u00c9\u00ca\u0005$\u0000\u0000\u00ca\u00d0\u0006\u000f\uffff\uffff\u0000"+
		"\u00cb\u00cc\u0003 \u0010\u0000\u00cc\u00cd\u0006\u000f\uffff\uffff\u0000"+
		"\u00cd\u00cf\u0001\u0000\u0000\u0000\u00ce\u00cb\u0001\u0000\u0000\u0000"+
		"\u00cf\u00d2\u0001\u0000\u0000\u0000\u00d0\u00ce\u0001\u0000\u0000\u0000"+
		"\u00d0\u00d1\u0001\u0000\u0000\u0000\u00d1\u00d6\u0001\u0000\u0000\u0000"+
		"\u00d2\u00d0\u0001\u0000\u0000\u0000\u00d3\u00d4\u0005#\u0000\u0000\u00d4"+
		"\u00d6\u0006\u000f\uffff\uffff\u0000\u00d5\u00c9\u0001\u0000\u0000\u0000"+
		"\u00d5\u00d3\u0001\u0000\u0000\u0000\u00d6\u001f\u0001\u0000\u0000\u0000"+
		"\u00d7\u00d8\u0005\u0013\u0000\u0000\u00d8\u00d9\u0005%\u0000\u0000\u00d9"+
		"\u00da\u0005&\u0000\u0000\u00da\u00db\u0005\'\u0000\u0000\u00db\u00dc"+
		"\u0005&\u0000\u0000\u00dc\u00dd\u0005(\u0000\u0000\u00dd\u00de\u0006\u0010"+
		"\uffff\uffff\u0000\u00de!\u0001\u0000\u0000\u0000\u00df\u00e0\u0005\u000f"+
		"\u0000\u0000\u00e0\u00e1\u0005\"\u0000\u0000\u00e1\u00e2\u0006\u0011\uffff"+
		"\uffff\u0000\u00e2#\u0001\u0000\u0000\u0000\u00e3\u00e7\u0003\u001c\u000e"+
		"\u0000\u00e4\u00e7\u0003\u001e\u000f\u0000\u00e5\u00e7\u0003 \u0010\u0000"+
		"\u00e6\u00e3\u0001\u0000\u0000\u0000\u00e6\u00e4\u0001\u0000\u0000\u0000"+
		"\u00e6\u00e5\u0001\u0000\u0000\u0000\u00e7%\u0001\u0000\u0000\u0000\u00e8"+
		"\u00e9\u0003\u001a\r\u0000\u00e9\u00fa\u0006\u0013\uffff\uffff\u0000\u00ea"+
		"\u00eb\u0003\u001c\u000e\u0000\u00eb\u00ec\u0006\u0013\uffff\uffff\u0000"+
		"\u00ec\u00ed\u0003\u001e\u000f\u0000\u00ed\u00ee\u0006\u0013\uffff\uffff"+
		"\u0000\u00ee\u00fb\u0001\u0000\u0000\u0000\u00ef\u00f0\u0003\u001e\u000f"+
		"\u0000\u00f0\u00f1\u0006\u0013\uffff\uffff\u0000\u00f1\u00f2\u0003\u001c"+
		"\u000e\u0000\u00f2\u00f3\u0006\u0013\uffff\uffff\u0000\u00f3\u00fb\u0001"+
		"\u0000\u0000\u0000\u00f4\u00f5\u0003\u001e\u000f\u0000\u00f5\u00f6\u0006"+
		"\u0013\uffff\uffff\u0000\u00f6\u00fb\u0001\u0000\u0000\u0000\u00f7\u00f8"+
		"\u0003\u001c\u000e\u0000\u00f8\u00f9\u0006\u0013\uffff\uffff\u0000\u00f9"+
		"\u00fb\u0001\u0000\u0000\u0000\u00fa\u00ea\u0001\u0000\u0000\u0000\u00fa"+
		"\u00ef\u0001\u0000\u0000\u0000\u00fa\u00f4\u0001\u0000\u0000\u0000\u00fa"+
		"\u00f7\u0001\u0000\u0000\u0000\u00fa\u00fb\u0001\u0000\u0000\u0000\u00fb"+
		"\u00ff\u0001\u0000\u0000\u0000\u00fc\u00fd\u0003\"\u0011\u0000\u00fd\u00fe"+
		"\u0006\u0013\uffff\uffff\u0000\u00fe\u0100\u0001\u0000\u0000\u0000\u00ff"+
		"\u00fc\u0001\u0000\u0000\u0000\u00ff\u0100\u0001\u0000\u0000\u0000\u0100"+
		"\'\u0001\u0000\u0000\u0000\u000eEWo\u00a2\u00a4\u00ac\u00bc\u00be\u00c0"+
		"\u00d0\u00d5\u00e6\u00fa\u00ff";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}