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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/ArtifactGrammar.g4 by ANTLR 4.13.2

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ArtifactGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		RECORD_COUNT=1, NAME=2, BASE_OBJECT=3, LEVEL=4, WEIGHT=5, COST=6, ALLOC=7, 
		ATTACK=8, ARMOUR=9, FLAGS=10, ACT=11, TIME=12, MSG=13, VALUES=14, BRAND=15, 
		SLAY=16, CURSE=17, DESC=18, ARTIFACT_COLON=19, ARTIFACT_INTEGER=20, GRAPHICS=21, 
		COLOUR_ONLY=22, GLYPH_ONLY=23, COMMENT=24, EOL=25, SIMPLE_DICE_STRING=26, 
		COMPLEX_DICE_STRING=27, ALLOC_INT=28, ALLOC_COLON=29, ALLOC_TO=30, ALLOC_EOL=31, 
		STRING=32, ARTIFACT_FREE_TEXT_EOL=33, DELIM_INTEGER=34, STRING_BETWEEN_COLONS=35, 
		DELIM_COLON=36, END_SKIP=37, FLAG=38, FLAG_OR=39, FLAG_EOL=40, VALUES_STRING=41, 
		VALUES_LBRACKET=42, VALUES_RBRACKET=43, VALUES_INTEGER=44, VALUES_OR=45, 
		VALUES_EOL=46, COLOUR_VALUES=47, GLYPH_VALUES=48, GLYPH_COLON_SWITCH=49, 
		GLYPH_EOL=50;
	public static final int
		RULE_recordCount = 0, RULE_name = 1, RULE_baseObject = 2, RULE_graphics = 3, 
		RULE_level = 4, RULE_weight = 5, RULE_cost = 6, RULE_alloc = 7, RULE_attack = 8, 
		RULE_armour = 9, RULE_flags = 10, RULE_act = 11, RULE_time = 12, RULE_msg = 13, 
		RULE_values = 14, RULE_brand = 15, RULE_slay = 16, RULE_curse = 17, RULE_desc = 18, 
		RULE_artifact = 19, RULE_file = 20;
	private static String[] makeRuleNames() {
		return new String[] {
			"recordCount", "name", "baseObject", "graphics", "level", "weight", "cost", 
			"alloc", "attack", "armour", "flags", "act", "time", "msg", "values", 
			"brand", "slay", "curse", "desc", "artifact", "file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'record-count:'", "'name:'", "'base-object:'", "'level:'", "'weight:'", 
			"'cost:'", "'alloc:'", "'attack:'", "'armor:'", null, "'act:'", "'time:'", 
			"'msg:'", "'values:'", "'brand:'", "'slay:'", "'curse:'", "'desc:'", 
			null, null, "'graphics:'", "'color:'", "'glyph:'", null, null, null, 
			null, null, null, "' to '", null, null, null, null, null, null, null, 
			null, null, null, null, "'['", "']'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "RECORD_COUNT", "NAME", "BASE_OBJECT", "LEVEL", "WEIGHT", "COST", 
			"ALLOC", "ATTACK", "ARMOUR", "FLAGS", "ACT", "TIME", "MSG", "VALUES", 
			"BRAND", "SLAY", "CURSE", "DESC", "ARTIFACT_COLON", "ARTIFACT_INTEGER", 
			"GRAPHICS", "COLOUR_ONLY", "GLYPH_ONLY", "COMMENT", "EOL", "SIMPLE_DICE_STRING", 
			"COMPLEX_DICE_STRING", "ALLOC_INT", "ALLOC_COLON", "ALLOC_TO", "ALLOC_EOL", 
			"STRING", "ARTIFACT_FREE_TEXT_EOL", "DELIM_INTEGER", "STRING_BETWEEN_COLONS", 
			"DELIM_COLON", "END_SKIP", "FLAG", "FLAG_OR", "FLAG_EOL", "VALUES_STRING", 
			"VALUES_LBRACKET", "VALUES_RBRACKET", "VALUES_INTEGER", "VALUES_OR", 
			"VALUES_EOL", "COLOUR_VALUES", "GLYPH_VALUES", "GLYPH_COLON_SWITCH", 
			"GLYPH_EOL"
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
	public String getGrammarFileName() { return "ArtifactGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ArtifactGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RecordCountContext extends ParserRuleContext {
		public String count;
		public Token c;
		public TerminalNode RECORD_COUNT() { return getToken(ArtifactGrammar.RECORD_COUNT, 0); }
		public TerminalNode ARTIFACT_INTEGER() { return getToken(ArtifactGrammar.ARTIFACT_INTEGER, 0); }
		public RecordCountContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_recordCount; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).enterRecordCount(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).exitRecordCount(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactGrammarVisitor ) return ((ArtifactGrammarVisitor<? extends T>)visitor).visitRecordCount(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RecordCountContext recordCount() throws RecognitionException {
		RecordCountContext _localctx = new RecordCountContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_recordCount);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(42);
			match(RECORD_COUNT);
			setState(43);
			((RecordCountContext)_localctx).c = match(ARTIFACT_INTEGER);
			 ((RecordCountContext)_localctx).count =  ((RecordCountContext)_localctx).c.getText(); 
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
		public String nameStr;
		public int line;
		public Token STRING;
		public TerminalNode NAME() { return getToken(ArtifactGrammar.NAME, 0); }
		public TerminalNode STRING() { return getToken(ArtifactGrammar.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactGrammarVisitor ) return ((ArtifactGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			match(NAME);
			setState(47);
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
	public static class BaseObjectContext extends ParserRuleContext {
		public String tVal;
		public String sVal;
		public Token t;
		public Token s;
		public TerminalNode BASE_OBJECT() { return getToken(ArtifactGrammar.BASE_OBJECT, 0); }
		public TerminalNode DELIM_COLON() { return getToken(ArtifactGrammar.DELIM_COLON, 0); }
		public List<TerminalNode> STRING_BETWEEN_COLONS() { return getTokens(ArtifactGrammar.STRING_BETWEEN_COLONS); }
		public TerminalNode STRING_BETWEEN_COLONS(int i) {
			return getToken(ArtifactGrammar.STRING_BETWEEN_COLONS, i);
		}
		public BaseObjectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_baseObject; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).enterBaseObject(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).exitBaseObject(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactGrammarVisitor ) return ((ArtifactGrammarVisitor<? extends T>)visitor).visitBaseObject(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BaseObjectContext baseObject() throws RecognitionException {
		BaseObjectContext _localctx = new BaseObjectContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_baseObject);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(50);
			match(BASE_OBJECT);
			setState(51);
			((BaseObjectContext)_localctx).t = match(STRING_BETWEEN_COLONS);
			setState(52);
			match(DELIM_COLON);
			setState(53);
			((BaseObjectContext)_localctx).s = match(STRING_BETWEEN_COLONS);
			 ((BaseObjectContext)_localctx).tVal =  ((BaseObjectContext)_localctx).t.getText(); ((BaseObjectContext)_localctx).sVal =  ((BaseObjectContext)_localctx).s.getText(); 
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
	public static class GraphicsContext extends ParserRuleContext {
		public String glyph;
		public String colour;
		public Token g;
		public Token c;
		public TerminalNode GRAPHICS() { return getToken(ArtifactGrammar.GRAPHICS, 0); }
		public TerminalNode GLYPH_COLON_SWITCH() { return getToken(ArtifactGrammar.GLYPH_COLON_SWITCH, 0); }
		public TerminalNode GLYPH_VALUES() { return getToken(ArtifactGrammar.GLYPH_VALUES, 0); }
		public TerminalNode COLOUR_VALUES() { return getToken(ArtifactGrammar.COLOUR_VALUES, 0); }
		public GraphicsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_graphics; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).enterGraphics(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).exitGraphics(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactGrammarVisitor ) return ((ArtifactGrammarVisitor<? extends T>)visitor).visitGraphics(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GraphicsContext graphics() throws RecognitionException {
		GraphicsContext _localctx = new GraphicsContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_graphics);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			match(GRAPHICS);
			setState(57);
			((GraphicsContext)_localctx).g = match(GLYPH_VALUES);
			setState(58);
			match(GLYPH_COLON_SWITCH);
			setState(59);
			((GraphicsContext)_localctx).c = match(COLOUR_VALUES);
			 ((GraphicsContext)_localctx).glyph =  ((GraphicsContext)_localctx).g.getText(); ((GraphicsContext)_localctx).colour =  ((GraphicsContext)_localctx).c.getText(); 
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
		public String levelVal;
		public Token l;
		public TerminalNode LEVEL() { return getToken(ArtifactGrammar.LEVEL, 0); }
		public TerminalNode ARTIFACT_INTEGER() { return getToken(ArtifactGrammar.ARTIFACT_INTEGER, 0); }
		public LevelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_level; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).enterLevel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).exitLevel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactGrammarVisitor ) return ((ArtifactGrammarVisitor<? extends T>)visitor).visitLevel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LevelContext level() throws RecognitionException {
		LevelContext _localctx = new LevelContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_level);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(62);
			match(LEVEL);
			setState(63);
			((LevelContext)_localctx).l = match(ARTIFACT_INTEGER);
			 ((LevelContext)_localctx).levelVal =  ((LevelContext)_localctx).l.getText(); 
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
		public String weightVal;
		public Token w;
		public TerminalNode WEIGHT() { return getToken(ArtifactGrammar.WEIGHT, 0); }
		public TerminalNode ARTIFACT_INTEGER() { return getToken(ArtifactGrammar.ARTIFACT_INTEGER, 0); }
		public WeightContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_weight; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).enterWeight(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).exitWeight(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactGrammarVisitor ) return ((ArtifactGrammarVisitor<? extends T>)visitor).visitWeight(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WeightContext weight() throws RecognitionException {
		WeightContext _localctx = new WeightContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_weight);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(66);
			match(WEIGHT);
			setState(67);
			((WeightContext)_localctx).w = match(ARTIFACT_INTEGER);
			((WeightContext)_localctx).weightVal =  ((WeightContext)_localctx).w.getText(); 
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
	public static class CostContext extends ParserRuleContext {
		public String costVal;
		public Token c;
		public TerminalNode COST() { return getToken(ArtifactGrammar.COST, 0); }
		public TerminalNode ARTIFACT_INTEGER() { return getToken(ArtifactGrammar.ARTIFACT_INTEGER, 0); }
		public CostContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cost; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).enterCost(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).exitCost(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactGrammarVisitor ) return ((ArtifactGrammarVisitor<? extends T>)visitor).visitCost(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CostContext cost() throws RecognitionException {
		CostContext _localctx = new CostContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_cost);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70);
			match(COST);
			setState(71);
			((CostContext)_localctx).c = match(ARTIFACT_INTEGER);
			((CostContext)_localctx).costVal =  ((CostContext)_localctx).c.getText(); 
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
		public String commonness;
		public String min;
		public String max;
		public Token c;
		public Token mn;
		public Token mx;
		public TerminalNode ALLOC() { return getToken(ArtifactGrammar.ALLOC, 0); }
		public TerminalNode ALLOC_COLON() { return getToken(ArtifactGrammar.ALLOC_COLON, 0); }
		public TerminalNode ALLOC_TO() { return getToken(ArtifactGrammar.ALLOC_TO, 0); }
		public List<TerminalNode> ALLOC_INT() { return getTokens(ArtifactGrammar.ALLOC_INT); }
		public TerminalNode ALLOC_INT(int i) {
			return getToken(ArtifactGrammar.ALLOC_INT, i);
		}
		public AllocContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alloc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).enterAlloc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).exitAlloc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactGrammarVisitor ) return ((ArtifactGrammarVisitor<? extends T>)visitor).visitAlloc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AllocContext alloc() throws RecognitionException {
		AllocContext _localctx = new AllocContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_alloc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			match(ALLOC);
			setState(75);
			((AllocContext)_localctx).c = match(ALLOC_INT);
			setState(76);
			match(ALLOC_COLON);
			setState(77);
			((AllocContext)_localctx).mn = match(ALLOC_INT);
			setState(78);
			match(ALLOC_TO);
			setState(79);
			((AllocContext)_localctx).mx = match(ALLOC_INT);
			 ((AllocContext)_localctx).commonness =  ((AllocContext)_localctx).c.getText(); ((AllocContext)_localctx).min =  ((AllocContext)_localctx).mn.getText(); ((AllocContext)_localctx).max =  ((AllocContext)_localctx).mx.getText(); 
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
	public static class AttackContext extends ParserRuleContext {
		public String attackBase;
		public String toh;
		public String tod;
		public Token b;
		public Token h;
		public Token d;
		public TerminalNode ATTACK() { return getToken(ArtifactGrammar.ATTACK, 0); }
		public List<TerminalNode> ARTIFACT_COLON() { return getTokens(ArtifactGrammar.ARTIFACT_COLON); }
		public TerminalNode ARTIFACT_COLON(int i) {
			return getToken(ArtifactGrammar.ARTIFACT_COLON, i);
		}
		public List<TerminalNode> ARTIFACT_INTEGER() { return getTokens(ArtifactGrammar.ARTIFACT_INTEGER); }
		public TerminalNode ARTIFACT_INTEGER(int i) {
			return getToken(ArtifactGrammar.ARTIFACT_INTEGER, i);
		}
		public TerminalNode SIMPLE_DICE_STRING() { return getToken(ArtifactGrammar.SIMPLE_DICE_STRING, 0); }
		public AttackContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attack; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).enterAttack(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).exitAttack(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactGrammarVisitor ) return ((ArtifactGrammarVisitor<? extends T>)visitor).visitAttack(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttackContext attack() throws RecognitionException {
		AttackContext _localctx = new AttackContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_attack);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			match(ATTACK);
			setState(83);
			((AttackContext)_localctx).b = _input.LT(1);
			_la = _input.LA(1);
			if ( !(_la==ARTIFACT_INTEGER || _la==SIMPLE_DICE_STRING) ) {
				((AttackContext)_localctx).b = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(84);
			match(ARTIFACT_COLON);
			setState(85);
			((AttackContext)_localctx).h = match(ARTIFACT_INTEGER);
			setState(86);
			match(ARTIFACT_COLON);
			setState(87);
			((AttackContext)_localctx).d = match(ARTIFACT_INTEGER);
			 ((AttackContext)_localctx).attackBase =  ((AttackContext)_localctx).b.getText();
			              ((AttackContext)_localctx).toh =  ((AttackContext)_localctx).h.getText();
			              ((AttackContext)_localctx).tod =  ((AttackContext)_localctx).d.getText(); 
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
	public static class ArmourContext extends ParserRuleContext {
		public String baseAC;
		public String toa;
		public Token b;
		public Token a;
		public TerminalNode ARMOUR() { return getToken(ArtifactGrammar.ARMOUR, 0); }
		public TerminalNode ARTIFACT_COLON() { return getToken(ArtifactGrammar.ARTIFACT_COLON, 0); }
		public List<TerminalNode> ARTIFACT_INTEGER() { return getTokens(ArtifactGrammar.ARTIFACT_INTEGER); }
		public TerminalNode ARTIFACT_INTEGER(int i) {
			return getToken(ArtifactGrammar.ARTIFACT_INTEGER, i);
		}
		public ArmourContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_armour; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).enterArmour(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).exitArmour(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactGrammarVisitor ) return ((ArtifactGrammarVisitor<? extends T>)visitor).visitArmour(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArmourContext armour() throws RecognitionException {
		ArmourContext _localctx = new ArmourContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_armour);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(90);
			match(ARMOUR);
			setState(91);
			((ArmourContext)_localctx).b = match(ARTIFACT_INTEGER);
			setState(92);
			match(ARTIFACT_COLON);
			setState(93);
			((ArmourContext)_localctx).a = match(ARTIFACT_INTEGER);
			 ((ArmourContext)_localctx).baseAC =  ((ArmourContext)_localctx).b.getText();
			              ((ArmourContext)_localctx).toa =  ((ArmourContext)_localctx).a.getText(); 
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
		public TerminalNode FLAGS() { return getToken(ArtifactGrammar.FLAGS, 0); }
		public List<TerminalNode> FLAG() { return getTokens(ArtifactGrammar.FLAG); }
		public TerminalNode FLAG(int i) {
			return getToken(ArtifactGrammar.FLAG, i);
		}
		public List<TerminalNode> FLAG_OR() { return getTokens(ArtifactGrammar.FLAG_OR); }
		public TerminalNode FLAG_OR(int i) {
			return getToken(ArtifactGrammar.FLAG_OR, i);
		}
		public FlagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flags; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).enterFlags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).exitFlags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactGrammarVisitor ) return ((ArtifactGrammarVisitor<? extends T>)visitor).visitFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsContext flags() throws RecognitionException {
		FlagsContext _localctx = new FlagsContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_flags);

		            flagList = new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(96);
			match(FLAGS);
			setState(97);
			((FlagsContext)_localctx).f1 = match(FLAG);

			                _localctx.flagList.add(((FlagsContext)_localctx).f1.getText());
			            
			setState(104);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FLAG_OR) {
				{
				{
				setState(99);
				match(FLAG_OR);
				setState(100);
				((FlagsContext)_localctx).f2 = match(FLAG);

				                _localctx.flagList.add(((FlagsContext)_localctx).f2.getText());
				            
				}
				}
				setState(106);
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
		public Token a;
		public TerminalNode ACT() { return getToken(ArtifactGrammar.ACT, 0); }
		public TerminalNode STRING() { return getToken(ArtifactGrammar.STRING, 0); }
		public ActContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_act; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).enterAct(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).exitAct(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactGrammarVisitor ) return ((ArtifactGrammarVisitor<? extends T>)visitor).visitAct(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActContext act() throws RecognitionException {
		ActContext _localctx = new ActContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_act);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(107);
			match(ACT);
			setState(108);
			((ActContext)_localctx).a = match(STRING);
			 ((ActContext)_localctx).activation =  ((ActContext)_localctx).a.getText(); 
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
		public TerminalNode TIME() { return getToken(ArtifactGrammar.TIME, 0); }
		public TerminalNode SIMPLE_DICE_STRING() { return getToken(ArtifactGrammar.SIMPLE_DICE_STRING, 0); }
		public TerminalNode ARTIFACT_INTEGER() { return getToken(ArtifactGrammar.ARTIFACT_INTEGER, 0); }
		public TimeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_time; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).enterTime(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).exitTime(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactGrammarVisitor ) return ((ArtifactGrammarVisitor<? extends T>)visitor).visitTime(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TimeContext time() throws RecognitionException {
		TimeContext _localctx = new TimeContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_time);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(111);
			match(TIME);
			setState(112);
			((TimeContext)_localctx).t = _input.LT(1);
			_la = _input.LA(1);
			if ( !(_la==ARTIFACT_INTEGER || _la==SIMPLE_DICE_STRING) ) {
				((TimeContext)_localctx).t = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
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
	public static class MsgContext extends ParserRuleContext {
		public String msgLine;
		public Token m;
		public TerminalNode MSG() { return getToken(ArtifactGrammar.MSG, 0); }
		public TerminalNode STRING() { return getToken(ArtifactGrammar.STRING, 0); }
		public MsgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_msg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).enterMsg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).exitMsg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactGrammarVisitor ) return ((ArtifactGrammarVisitor<? extends T>)visitor).visitMsg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MsgContext msg() throws RecognitionException {
		MsgContext _localctx = new MsgContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_msg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(115);
			match(MSG);
			setState(116);
			((MsgContext)_localctx).m = match(STRING);
			 ((MsgContext)_localctx).msgLine =  ((MsgContext)_localctx).m.getText(); 
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
		public Token k1;
		public Token v1;
		public Token k2;
		public Token v2;
		public TerminalNode VALUES() { return getToken(ArtifactGrammar.VALUES, 0); }
		public List<TerminalNode> VALUES_LBRACKET() { return getTokens(ArtifactGrammar.VALUES_LBRACKET); }
		public TerminalNode VALUES_LBRACKET(int i) {
			return getToken(ArtifactGrammar.VALUES_LBRACKET, i);
		}
		public List<TerminalNode> VALUES_RBRACKET() { return getTokens(ArtifactGrammar.VALUES_RBRACKET); }
		public TerminalNode VALUES_RBRACKET(int i) {
			return getToken(ArtifactGrammar.VALUES_RBRACKET, i);
		}
		public List<TerminalNode> VALUES_STRING() { return getTokens(ArtifactGrammar.VALUES_STRING); }
		public TerminalNode VALUES_STRING(int i) {
			return getToken(ArtifactGrammar.VALUES_STRING, i);
		}
		public List<TerminalNode> VALUES_INTEGER() { return getTokens(ArtifactGrammar.VALUES_INTEGER); }
		public TerminalNode VALUES_INTEGER(int i) {
			return getToken(ArtifactGrammar.VALUES_INTEGER, i);
		}
		public List<TerminalNode> VALUES_OR() { return getTokens(ArtifactGrammar.VALUES_OR); }
		public TerminalNode VALUES_OR(int i) {
			return getToken(ArtifactGrammar.VALUES_OR, i);
		}
		public ValuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_values; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).enterValues(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).exitValues(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactGrammarVisitor ) return ((ArtifactGrammarVisitor<? extends T>)visitor).visitValues(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValuesContext values() throws RecognitionException {
		ValuesContext _localctx = new ValuesContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_values);

		            ((ValuesContext)_localctx).valueMap =  new HashMap<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(119);
			match(VALUES);
			setState(120);
			((ValuesContext)_localctx).k1 = match(VALUES_STRING);
			setState(121);
			match(VALUES_LBRACKET);
			setState(122);
			((ValuesContext)_localctx).v1 = match(VALUES_INTEGER);
			setState(123);
			match(VALUES_RBRACKET);

			                _localctx.valueMap.put(((ValuesContext)_localctx).k1.getText(), ((ValuesContext)_localctx).v1.getText());
			            
			setState(133);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VALUES_OR) {
				{
				{
				setState(125);
				match(VALUES_OR);
				setState(126);
				((ValuesContext)_localctx).k2 = match(VALUES_STRING);
				setState(127);
				match(VALUES_LBRACKET);
				setState(128);
				((ValuesContext)_localctx).v2 = match(VALUES_INTEGER);
				setState(129);
				match(VALUES_RBRACKET);

				                _localctx.valueMap.put(((ValuesContext)_localctx).k2.getText(), ((ValuesContext)_localctx).v2.getText());
				            
				}
				}
				setState(135);
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
	public static class BrandContext extends ParserRuleContext {
		public String brandLine;
		public Token STRING;
		public TerminalNode BRAND() { return getToken(ArtifactGrammar.BRAND, 0); }
		public TerminalNode STRING() { return getToken(ArtifactGrammar.STRING, 0); }
		public BrandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_brand; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).enterBrand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).exitBrand(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactGrammarVisitor ) return ((ArtifactGrammarVisitor<? extends T>)visitor).visitBrand(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BrandContext brand() throws RecognitionException {
		BrandContext _localctx = new BrandContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_brand);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(136);
			match(BRAND);
			setState(137);
			((BrandContext)_localctx).STRING = match(STRING);
			 ((BrandContext)_localctx).brandLine =  ((BrandContext)_localctx).STRING.getText(); 
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
		public String slayLine;
		public Token STRING;
		public TerminalNode SLAY() { return getToken(ArtifactGrammar.SLAY, 0); }
		public TerminalNode STRING() { return getToken(ArtifactGrammar.STRING, 0); }
		public SlayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_slay; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).enterSlay(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).exitSlay(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactGrammarVisitor ) return ((ArtifactGrammarVisitor<? extends T>)visitor).visitSlay(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SlayContext slay() throws RecognitionException {
		SlayContext _localctx = new SlayContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_slay);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(140);
			match(SLAY);
			setState(141);
			((SlayContext)_localctx).STRING = match(STRING);
			 ((SlayContext)_localctx).slayLine =  ((SlayContext)_localctx).STRING.getText(); 
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
		public Map<String, String> curseMap;
		public Token k;
		public Token v;
		public TerminalNode CURSE() { return getToken(ArtifactGrammar.CURSE, 0); }
		public TerminalNode DELIM_COLON() { return getToken(ArtifactGrammar.DELIM_COLON, 0); }
		public List<TerminalNode> STRING_BETWEEN_COLONS() { return getTokens(ArtifactGrammar.STRING_BETWEEN_COLONS); }
		public TerminalNode STRING_BETWEEN_COLONS(int i) {
			return getToken(ArtifactGrammar.STRING_BETWEEN_COLONS, i);
		}
		public CurseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_curse; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).enterCurse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).exitCurse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactGrammarVisitor ) return ((ArtifactGrammarVisitor<? extends T>)visitor).visitCurse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CurseContext curse() throws RecognitionException {
		CurseContext _localctx = new CurseContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_curse);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(144);
			match(CURSE);
			setState(145);
			((CurseContext)_localctx).k = match(STRING_BETWEEN_COLONS);
			setState(146);
			match(DELIM_COLON);
			setState(147);
			((CurseContext)_localctx).v = match(STRING_BETWEEN_COLONS);

			            _localctx.curseMap.put(((CurseContext)_localctx).k.getText(), ((CurseContext)_localctx).v.getText()); 
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
		public String descLine;
		public Token d;
		public TerminalNode DESC() { return getToken(ArtifactGrammar.DESC, 0); }
		public TerminalNode STRING() { return getToken(ArtifactGrammar.STRING, 0); }
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_desc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactGrammarVisitor ) return ((ArtifactGrammarVisitor<? extends T>)visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_desc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(150);
			match(DESC);
			setState(151);
			((DescContext)_localctx).d = match(STRING);
			 ((DescContext)_localctx).descLine =  ((DescContext)_localctx).d.getText(); 
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
	public static class ArtifactContext extends ParserRuleContext {
		public ArtifactParseRecord artifactRecord;
		public NameContext name;
		public BaseObjectContext baseObject;
		public GraphicsContext graphics;
		public LevelContext level;
		public WeightContext weight;
		public CostContext cost;
		public AllocContext alloc;
		public AttackContext attack;
		public ArmourContext armour;
		public FlagsContext flags;
		public ActContext act;
		public TimeContext time;
		public MsgContext msg;
		public ValuesContext values;
		public BrandContext brand;
		public SlayContext slay;
		public CurseContext curse;
		public DescContext desc;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public List<BaseObjectContext> baseObject() {
			return getRuleContexts(BaseObjectContext.class);
		}
		public BaseObjectContext baseObject(int i) {
			return getRuleContext(BaseObjectContext.class,i);
		}
		public List<GraphicsContext> graphics() {
			return getRuleContexts(GraphicsContext.class);
		}
		public GraphicsContext graphics(int i) {
			return getRuleContext(GraphicsContext.class,i);
		}
		public List<LevelContext> level() {
			return getRuleContexts(LevelContext.class);
		}
		public LevelContext level(int i) {
			return getRuleContext(LevelContext.class,i);
		}
		public List<WeightContext> weight() {
			return getRuleContexts(WeightContext.class);
		}
		public WeightContext weight(int i) {
			return getRuleContext(WeightContext.class,i);
		}
		public List<CostContext> cost() {
			return getRuleContexts(CostContext.class);
		}
		public CostContext cost(int i) {
			return getRuleContext(CostContext.class,i);
		}
		public List<AllocContext> alloc() {
			return getRuleContexts(AllocContext.class);
		}
		public AllocContext alloc(int i) {
			return getRuleContext(AllocContext.class,i);
		}
		public List<AttackContext> attack() {
			return getRuleContexts(AttackContext.class);
		}
		public AttackContext attack(int i) {
			return getRuleContext(AttackContext.class,i);
		}
		public List<ArmourContext> armour() {
			return getRuleContexts(ArmourContext.class);
		}
		public ArmourContext armour(int i) {
			return getRuleContext(ArmourContext.class,i);
		}
		public List<FlagsContext> flags() {
			return getRuleContexts(FlagsContext.class);
		}
		public FlagsContext flags(int i) {
			return getRuleContext(FlagsContext.class,i);
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
		public List<MsgContext> msg() {
			return getRuleContexts(MsgContext.class);
		}
		public MsgContext msg(int i) {
			return getRuleContext(MsgContext.class,i);
		}
		public List<ValuesContext> values() {
			return getRuleContexts(ValuesContext.class);
		}
		public ValuesContext values(int i) {
			return getRuleContext(ValuesContext.class,i);
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
		public ArtifactContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_artifact; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).enterArtifact(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).exitArtifact(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactGrammarVisitor ) return ((ArtifactGrammarVisitor<? extends T>)visitor).visitArtifact(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArtifactContext artifact() throws RecognitionException {
		ArtifactContext _localctx = new ArtifactContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_artifact);

		            String nameInit = "";
		            String tValueInit = "";
		            String sValueInit = "";
		            String glyphInit = "";
		            String colourInit = "";
		            String levelInit = "";
		            String weightInit = "";
		            String costInit = "";
		            String commonnessInit = "";
		            String minInit = "";
		            String maxInit = "";
		            String baseDamageInit = "";
		            String tohInit = "";
		            String todInit = "";
		            String baseACInit = "";
		            String toaInit = "";
		            List<String> flagListInit = new ArrayList<>();
		            String activationInit = "";
		            String timeInit = "";
		            String msgInit = "";
		            Map<String, String> valuesMapInit = new HashMap<>();
		            List<String> brandInit = new ArrayList<>();
		            List<String> slayInit = new ArrayList<>();
		            Map<String, String> curseInit = new HashMap<>();
		            String descInit = "";
		            int line = 0;
		            StringBuilder msgSB = new StringBuilder();
		            StringBuilder descSB = new StringBuilder();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(154);
			((ArtifactContext)_localctx).name = name();
			 line = ((ArtifactContext)_localctx).name.line; nameInit = ((ArtifactContext)_localctx).name.nameStr; 
			setState(207); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(207);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case BASE_OBJECT:
					{
					setState(156);
					((ArtifactContext)_localctx).baseObject = baseObject();
					 tValueInit = ((ArtifactContext)_localctx).baseObject.tVal; sValueInit = ((ArtifactContext)_localctx).baseObject.sVal; 
					}
					break;
				case GRAPHICS:
					{
					setState(159);
					((ArtifactContext)_localctx).graphics = graphics();
					 glyphInit = ((ArtifactContext)_localctx).graphics.glyph; colourInit = ((ArtifactContext)_localctx).graphics.colour; 
					}
					break;
				case LEVEL:
					{
					setState(162);
					((ArtifactContext)_localctx).level = level();
					 levelInit = ((ArtifactContext)_localctx).level.levelVal; 
					}
					break;
				case WEIGHT:
					{
					setState(165);
					((ArtifactContext)_localctx).weight = weight();
					 weightInit = ((ArtifactContext)_localctx).weight.weightVal; 
					}
					break;
				case COST:
					{
					setState(168);
					((ArtifactContext)_localctx).cost = cost();
					 costInit = ((ArtifactContext)_localctx).cost.costVal; 
					}
					break;
				case ALLOC:
					{
					setState(171);
					((ArtifactContext)_localctx).alloc = alloc();
					 commonnessInit = ((ArtifactContext)_localctx).alloc.commonness;
					                    minInit = ((ArtifactContext)_localctx).alloc.min;
					                    maxInit = ((ArtifactContext)_localctx).alloc.max; 
					}
					break;
				case ATTACK:
					{
					setState(174);
					((ArtifactContext)_localctx).attack = attack();
					 baseDamageInit = ((ArtifactContext)_localctx).attack.attackBase;
					                     tohInit = ((ArtifactContext)_localctx).attack.toh;
					                     todInit = ((ArtifactContext)_localctx).attack.tod; 
					}
					break;
				case ARMOUR:
					{
					setState(177);
					((ArtifactContext)_localctx).armour = armour();
					 baseACInit = ((ArtifactContext)_localctx).armour.baseAC;
					                     toaInit = ((ArtifactContext)_localctx).armour.toa; 
					}
					break;
				case FLAGS:
					{
					setState(180);
					((ArtifactContext)_localctx).flags = flags();
					 flagsInit.addAll(((ArtifactContext)_localctx).flags.flagList); 
					}
					break;
				case ACT:
					{
					setState(183);
					((ArtifactContext)_localctx).act = act();
					 activationInit = ((ArtifactContext)_localctx).act.activation; 
					}
					break;
				case TIME:
					{
					setState(186);
					((ArtifactContext)_localctx).time = time();
					 timeInit = ((ArtifactContext)_localctx).time.timeStr; 
					}
					break;
				case MSG:
					{
					setState(189);
					((ArtifactContext)_localctx).msg = msg();
					 msgSB.append(((ArtifactContext)_localctx).msg.msgLine); 
					}
					break;
				case VALUES:
					{
					setState(192);
					((ArtifactContext)_localctx).values = values();
					 valuesMapInit.putAll(((ArtifactContext)_localctx).values.valueMap); 
					}
					break;
				case BRAND:
					{
					setState(195);
					((ArtifactContext)_localctx).brand = brand();
					 brandInit.add(((ArtifactContext)_localctx).brand.brandLine); 
					}
					break;
				case SLAY:
					{
					setState(198);
					((ArtifactContext)_localctx).slay = slay();
					 slayInit.add(((ArtifactContext)_localctx).slay.slayLine); 
					}
					break;
				case CURSE:
					{
					setState(201);
					((ArtifactContext)_localctx).curse = curse();
					 curseInit.putAll(((ArtifactContext)_localctx).curse.curseMap); 
					}
					break;
				case DESC:
					{
					setState(204);
					((ArtifactContext)_localctx).desc = desc();
					 descSB.append(((ArtifactContext)_localctx).desc.descLine); 
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(209); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 2621432L) != 0) );
			}
			_ctx.stop = _input.LT(-1);

			            msgInit = msgSB.toString();
			            descInit = descSB.toString();
			            ((ArtifactContext)_localctx).artifactRecord =  new ArtifactParseRecord(nameInit, tValueInit, sValueInit,
			                glyphInit, colourInit, levelInit, weightInit, costInit, commonnessInit,
			                minInit, maxInit, baseDamageInit, tohInit, todInit, baseACInit, toaInit,
			                flagListInit, activationInit, timeInit, msgInit, valuesMapInit,
			                brandInit, slayInit, curseInit, descInit, line);
			        
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
		public List<ArtifactParseRecord> records;
		public String declaredRecordCount;
		public RecordCountContext recordCount;
		public ArtifactContext artifact;
		public RecordCountContext recordCount() {
			return getRuleContext(RecordCountContext.class,0);
		}
		public ArtifactContext artifact() {
			return getRuleContext(ArtifactContext.class,0);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactGrammarListener ) ((ArtifactGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactGrammarVisitor ) return ((ArtifactGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_file);

		            ((FileContext)_localctx).records =  new ArrayList<>();
		        
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(211);
			((FileContext)_localctx).recordCount = recordCount();
			 ((FileContext)_localctx).declaredRecordCount =  ((FileContext)_localctx).recordCount.count; 
			{
			setState(213);
			((FileContext)_localctx).artifact = artifact();
			 _localctx.records.add(((FileContext)_localctx).artifact.artifactRecord); 
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

	public static final String _serializedATN =
		"\u0004\u00012\u00d9\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0005\ng\b\n\n\n\f\nj\t\n\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0005\u000e\u0084\b\u000e\n\u000e"+
		"\f\u000e\u0087\t\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0004\u0013\u00d0\b\u0013\u000b\u0013\f\u0013\u00d1\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0000"+
		"\u0000\u0015\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016"+
		"\u0018\u001a\u001c\u001e \"$&(\u0000\u0001\u0002\u0000\u0014\u0014\u001a"+
		"\u001a\u00d6\u0000*\u0001\u0000\u0000\u0000\u0002.\u0001\u0000\u0000\u0000"+
		"\u00042\u0001\u0000\u0000\u0000\u00068\u0001\u0000\u0000\u0000\b>\u0001"+
		"\u0000\u0000\u0000\nB\u0001\u0000\u0000\u0000\fF\u0001\u0000\u0000\u0000"+
		"\u000eJ\u0001\u0000\u0000\u0000\u0010R\u0001\u0000\u0000\u0000\u0012Z"+
		"\u0001\u0000\u0000\u0000\u0014`\u0001\u0000\u0000\u0000\u0016k\u0001\u0000"+
		"\u0000\u0000\u0018o\u0001\u0000\u0000\u0000\u001as\u0001\u0000\u0000\u0000"+
		"\u001cw\u0001\u0000\u0000\u0000\u001e\u0088\u0001\u0000\u0000\u0000 \u008c"+
		"\u0001\u0000\u0000\u0000\"\u0090\u0001\u0000\u0000\u0000$\u0096\u0001"+
		"\u0000\u0000\u0000&\u009a\u0001\u0000\u0000\u0000(\u00d3\u0001\u0000\u0000"+
		"\u0000*+\u0005\u0001\u0000\u0000+,\u0005\u0014\u0000\u0000,-\u0006\u0000"+
		"\uffff\uffff\u0000-\u0001\u0001\u0000\u0000\u0000./\u0005\u0002\u0000"+
		"\u0000/0\u0005 \u0000\u000001\u0006\u0001\uffff\uffff\u00001\u0003\u0001"+
		"\u0000\u0000\u000023\u0005\u0003\u0000\u000034\u0005#\u0000\u000045\u0005"+
		"$\u0000\u000056\u0005#\u0000\u000067\u0006\u0002\uffff\uffff\u00007\u0005"+
		"\u0001\u0000\u0000\u000089\u0005\u0015\u0000\u00009:\u00050\u0000\u0000"+
		":;\u00051\u0000\u0000;<\u0005/\u0000\u0000<=\u0006\u0003\uffff\uffff\u0000"+
		"=\u0007\u0001\u0000\u0000\u0000>?\u0005\u0004\u0000\u0000?@\u0005\u0014"+
		"\u0000\u0000@A\u0006\u0004\uffff\uffff\u0000A\t\u0001\u0000\u0000\u0000"+
		"BC\u0005\u0005\u0000\u0000CD\u0005\u0014\u0000\u0000DE\u0006\u0005\uffff"+
		"\uffff\u0000E\u000b\u0001\u0000\u0000\u0000FG\u0005\u0006\u0000\u0000"+
		"GH\u0005\u0014\u0000\u0000HI\u0006\u0006\uffff\uffff\u0000I\r\u0001\u0000"+
		"\u0000\u0000JK\u0005\u0007\u0000\u0000KL\u0005\u001c\u0000\u0000LM\u0005"+
		"\u001d\u0000\u0000MN\u0005\u001c\u0000\u0000NO\u0005\u001e\u0000\u0000"+
		"OP\u0005\u001c\u0000\u0000PQ\u0006\u0007\uffff\uffff\u0000Q\u000f\u0001"+
		"\u0000\u0000\u0000RS\u0005\b\u0000\u0000ST\u0007\u0000\u0000\u0000TU\u0005"+
		"\u0013\u0000\u0000UV\u0005\u0014\u0000\u0000VW\u0005\u0013\u0000\u0000"+
		"WX\u0005\u0014\u0000\u0000XY\u0006\b\uffff\uffff\u0000Y\u0011\u0001\u0000"+
		"\u0000\u0000Z[\u0005\t\u0000\u0000[\\\u0005\u0014\u0000\u0000\\]\u0005"+
		"\u0013\u0000\u0000]^\u0005\u0014\u0000\u0000^_\u0006\t\uffff\uffff\u0000"+
		"_\u0013\u0001\u0000\u0000\u0000`a\u0005\n\u0000\u0000ab\u0005&\u0000\u0000"+
		"bh\u0006\n\uffff\uffff\u0000cd\u0005\'\u0000\u0000de\u0005&\u0000\u0000"+
		"eg\u0006\n\uffff\uffff\u0000fc\u0001\u0000\u0000\u0000gj\u0001\u0000\u0000"+
		"\u0000hf\u0001\u0000\u0000\u0000hi\u0001\u0000\u0000\u0000i\u0015\u0001"+
		"\u0000\u0000\u0000jh\u0001\u0000\u0000\u0000kl\u0005\u000b\u0000\u0000"+
		"lm\u0005 \u0000\u0000mn\u0006\u000b\uffff\uffff\u0000n\u0017\u0001\u0000"+
		"\u0000\u0000op\u0005\f\u0000\u0000pq\u0007\u0000\u0000\u0000qr\u0006\f"+
		"\uffff\uffff\u0000r\u0019\u0001\u0000\u0000\u0000st\u0005\r\u0000\u0000"+
		"tu\u0005 \u0000\u0000uv\u0006\r\uffff\uffff\u0000v\u001b\u0001\u0000\u0000"+
		"\u0000wx\u0005\u000e\u0000\u0000xy\u0005)\u0000\u0000yz\u0005*\u0000\u0000"+
		"z{\u0005,\u0000\u0000{|\u0005+\u0000\u0000|\u0085\u0006\u000e\uffff\uffff"+
		"\u0000}~\u0005-\u0000\u0000~\u007f\u0005)\u0000\u0000\u007f\u0080\u0005"+
		"*\u0000\u0000\u0080\u0081\u0005,\u0000\u0000\u0081\u0082\u0005+\u0000"+
		"\u0000\u0082\u0084\u0006\u000e\uffff\uffff\u0000\u0083}\u0001\u0000\u0000"+
		"\u0000\u0084\u0087\u0001\u0000\u0000\u0000\u0085\u0083\u0001\u0000\u0000"+
		"\u0000\u0085\u0086\u0001\u0000\u0000\u0000\u0086\u001d\u0001\u0000\u0000"+
		"\u0000\u0087\u0085\u0001\u0000\u0000\u0000\u0088\u0089\u0005\u000f\u0000"+
		"\u0000\u0089\u008a\u0005 \u0000\u0000\u008a\u008b\u0006\u000f\uffff\uffff"+
		"\u0000\u008b\u001f\u0001\u0000\u0000\u0000\u008c\u008d\u0005\u0010\u0000"+
		"\u0000\u008d\u008e\u0005 \u0000\u0000\u008e\u008f\u0006\u0010\uffff\uffff"+
		"\u0000\u008f!\u0001\u0000\u0000\u0000\u0090\u0091\u0005\u0011\u0000\u0000"+
		"\u0091\u0092\u0005#\u0000\u0000\u0092\u0093\u0005$\u0000\u0000\u0093\u0094"+
		"\u0005#\u0000\u0000\u0094\u0095\u0006\u0011\uffff\uffff\u0000\u0095#\u0001"+
		"\u0000\u0000\u0000\u0096\u0097\u0005\u0012\u0000\u0000\u0097\u0098\u0005"+
		" \u0000\u0000\u0098\u0099\u0006\u0012\uffff\uffff\u0000\u0099%\u0001\u0000"+
		"\u0000\u0000\u009a\u009b\u0003\u0002\u0001\u0000\u009b\u00cf\u0006\u0013"+
		"\uffff\uffff\u0000\u009c\u009d\u0003\u0004\u0002\u0000\u009d\u009e\u0006"+
		"\u0013\uffff\uffff\u0000\u009e\u00d0\u0001\u0000\u0000\u0000\u009f\u00a0"+
		"\u0003\u0006\u0003\u0000\u00a0\u00a1\u0006\u0013\uffff\uffff\u0000\u00a1"+
		"\u00d0\u0001\u0000\u0000\u0000\u00a2\u00a3\u0003\b\u0004\u0000\u00a3\u00a4"+
		"\u0006\u0013\uffff\uffff\u0000\u00a4\u00d0\u0001\u0000\u0000\u0000\u00a5"+
		"\u00a6\u0003\n\u0005\u0000\u00a6\u00a7\u0006\u0013\uffff\uffff\u0000\u00a7"+
		"\u00d0\u0001\u0000\u0000\u0000\u00a8\u00a9\u0003\f\u0006\u0000\u00a9\u00aa"+
		"\u0006\u0013\uffff\uffff\u0000\u00aa\u00d0\u0001\u0000\u0000\u0000\u00ab"+
		"\u00ac\u0003\u000e\u0007\u0000\u00ac\u00ad\u0006\u0013\uffff\uffff\u0000"+
		"\u00ad\u00d0\u0001\u0000\u0000\u0000\u00ae\u00af\u0003\u0010\b\u0000\u00af"+
		"\u00b0\u0006\u0013\uffff\uffff\u0000\u00b0\u00d0\u0001\u0000\u0000\u0000"+
		"\u00b1\u00b2\u0003\u0012\t\u0000\u00b2\u00b3\u0006\u0013\uffff\uffff\u0000"+
		"\u00b3\u00d0\u0001\u0000\u0000\u0000\u00b4\u00b5\u0003\u0014\n\u0000\u00b5"+
		"\u00b6\u0006\u0013\uffff\uffff\u0000\u00b6\u00d0\u0001\u0000\u0000\u0000"+
		"\u00b7\u00b8\u0003\u0016\u000b\u0000\u00b8\u00b9\u0006\u0013\uffff\uffff"+
		"\u0000\u00b9\u00d0\u0001\u0000\u0000\u0000\u00ba\u00bb\u0003\u0018\f\u0000"+
		"\u00bb\u00bc\u0006\u0013\uffff\uffff\u0000\u00bc\u00d0\u0001\u0000\u0000"+
		"\u0000\u00bd\u00be\u0003\u001a\r\u0000\u00be\u00bf\u0006\u0013\uffff\uffff"+
		"\u0000\u00bf\u00d0\u0001\u0000\u0000\u0000\u00c0\u00c1\u0003\u001c\u000e"+
		"\u0000\u00c1\u00c2\u0006\u0013\uffff\uffff\u0000\u00c2\u00d0\u0001\u0000"+
		"\u0000\u0000\u00c3\u00c4\u0003\u001e\u000f\u0000\u00c4\u00c5\u0006\u0013"+
		"\uffff\uffff\u0000\u00c5\u00d0\u0001\u0000\u0000\u0000\u00c6\u00c7\u0003"+
		" \u0010\u0000\u00c7\u00c8\u0006\u0013\uffff\uffff\u0000\u00c8\u00d0\u0001"+
		"\u0000\u0000\u0000\u00c9\u00ca\u0003\"\u0011\u0000\u00ca\u00cb\u0006\u0013"+
		"\uffff\uffff\u0000\u00cb\u00d0\u0001\u0000\u0000\u0000\u00cc\u00cd\u0003"+
		"$\u0012\u0000\u00cd\u00ce\u0006\u0013\uffff\uffff\u0000\u00ce\u00d0\u0001"+
		"\u0000\u0000\u0000\u00cf\u009c\u0001\u0000\u0000\u0000\u00cf\u009f\u0001"+
		"\u0000\u0000\u0000\u00cf\u00a2\u0001\u0000\u0000\u0000\u00cf\u00a5\u0001"+
		"\u0000\u0000\u0000\u00cf\u00a8\u0001\u0000\u0000\u0000\u00cf\u00ab\u0001"+
		"\u0000\u0000\u0000\u00cf\u00ae\u0001\u0000\u0000\u0000\u00cf\u00b1\u0001"+
		"\u0000\u0000\u0000\u00cf\u00b4\u0001\u0000\u0000\u0000\u00cf\u00b7\u0001"+
		"\u0000\u0000\u0000\u00cf\u00ba\u0001\u0000\u0000\u0000\u00cf\u00bd\u0001"+
		"\u0000\u0000\u0000\u00cf\u00c0\u0001\u0000\u0000\u0000\u00cf\u00c3\u0001"+
		"\u0000\u0000\u0000\u00cf\u00c6\u0001\u0000\u0000\u0000\u00cf\u00c9\u0001"+
		"\u0000\u0000\u0000\u00cf\u00cc\u0001\u0000\u0000\u0000\u00d0\u00d1\u0001"+
		"\u0000\u0000\u0000\u00d1\u00cf\u0001\u0000\u0000\u0000\u00d1\u00d2\u0001"+
		"\u0000\u0000\u0000\u00d2\'\u0001\u0000\u0000\u0000\u00d3\u00d4\u0003\u0000"+
		"\u0000\u0000\u00d4\u00d5\u0006\u0014\uffff\uffff\u0000\u00d5\u00d6\u0003"+
		"&\u0013\u0000\u00d6\u00d7\u0006\u0014\uffff\uffff\u0000\u00d7)\u0001\u0000"+
		"\u0000\u0000\u0004h\u0085\u00cf\u00d1";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}