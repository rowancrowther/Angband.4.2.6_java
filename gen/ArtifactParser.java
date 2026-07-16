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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/Artifact.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.Activation;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
    import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
    import uk.co.jackoftrades.middle.objects.enums.TValue;
    import uk.co.jackoftrades.middle.objects.Artifact;
    import uk.co.jackoftrades.middle.objects.Brand;
    import uk.co.jackoftrades.middle.objects.Curse;
    import uk.co.jackoftrades.middle.objects.CurseData;
    import uk.co.jackoftrades.middle.objects.ObjectKind;
    import uk.co.jackoftrades.middle.objects.Slay;

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
public class ArtifactParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		COMMENT=1, EOL=2, NAME=3, BASE_OBJECT=4, GRAPHICS=5, LEVEL=6, WEIGHT=7, 
		COST=8, ALLOC=9, ATTACK=10, ARMOUR=11, FLAGS=12, ACT=13, TIME=14, MSG=15, 
		VALUES=16, BRAND=17, SLAY=18, CURSE=19, DESC=20, RANGE_TO=21, DICE_STRING=22, 
		MCASE=23, INTEGER=24, COLON=25, LBRACKET=26, RBRACKET=27, OR=28, PLUS=29, 
		MINUS=30;
	public static final int
		RULE_name = 0, RULE_baseObject = 1, RULE_graphics = 2, RULE_level = 3, 
		RULE_weight = 4, RULE_cost = 5, RULE_alloc = 6, RULE_attack = 7, RULE_armour = 8, 
		RULE_flags = 9, RULE_values = 10, RULE_act = 11, RULE_time = 12, RULE_desc = 13, 
		RULE_brand = 14, RULE_slay = 15, RULE_curse = 16, RULE_msg = 17, RULE_artifact = 18, 
		RULE_file = 19;
	private static String[] makeRuleNames() {
		return new String[] {
			"name", "baseObject", "graphics", "level", "weight", "cost", "alloc", 
			"attack", "armour", "flags", "values", "act", "time", "desc", "brand", 
			"slay", "curse", "msg", "artifact", "file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, "'level:'", "'weight:'", "'cost:'", 
			"'alloc:'", "'attack:'", "'armor:'", "'flags:'", null, "'time:'", null, 
			"'values:'", null, null, "'curse:'", null, "' to '", null, null, null, 
			"':'", "'['", null, "'|'", "'+'", "'-'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "COMMENT", "EOL", "NAME", "BASE_OBJECT", "GRAPHICS", "LEVEL", "WEIGHT", 
			"COST", "ALLOC", "ATTACK", "ARMOUR", "FLAGS", "ACT", "TIME", "MSG", "VALUES", 
			"BRAND", "SLAY", "CURSE", "DESC", "RANGE_TO", "DICE_STRING", "MCASE", 
			"INTEGER", "COLON", "LBRACKET", "RBRACKET", "OR", "PLUS", "MINUS"
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
	public String getGrammarFileName() { return "Artifact.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ArtifactParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public Token NAME;
		public TerminalNode NAME() { return getToken(ArtifactParser.NAME, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactVisitor ) return ((ArtifactVisitor<? extends T>)visitor).visitName(this);
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
			((NameContext)_localctx).NAME = match(NAME);

			            String raw = ((NameContext)_localctx).NAME.getText();
			            ((NameContext)_localctx).nameStr =  raw.substring(5);
			        
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
		public TValue tVal;
		public String sVal;
		public Token BASE_OBJECT;
		public Token MCASE;
		public TerminalNode BASE_OBJECT() { return getToken(ArtifactParser.BASE_OBJECT, 0); }
		public TerminalNode COLON() { return getToken(ArtifactParser.COLON, 0); }
		public TerminalNode MCASE() { return getToken(ArtifactParser.MCASE, 0); }
		public BaseObjectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_baseObject; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).enterBaseObject(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).exitBaseObject(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactVisitor ) return ((ArtifactVisitor<? extends T>)visitor).visitBaseObject(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BaseObjectContext baseObject() throws RecognitionException {
		BaseObjectContext _localctx = new BaseObjectContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_baseObject);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(43);
			((BaseObjectContext)_localctx).BASE_OBJECT = match(BASE_OBJECT);
			setState(44);
			match(COLON);
			setState(45);
			((BaseObjectContext)_localctx).MCASE = match(MCASE);

			                String raw = ((BaseObjectContext)_localctx).BASE_OBJECT.getText().substring(12);
			                raw = raw.replace(' ', '_').replace("armour", "armor").toUpperCase();
			                ((BaseObjectContext)_localctx).sVal =  ((BaseObjectContext)_localctx).MCASE.getText();
			                ((BaseObjectContext)_localctx).tVal =  TValue.valueOf("TV_" + raw);
			            
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
		public AngbandDisplayCharacter adc;
		public Token GRAPHICS;
		public TerminalNode GRAPHICS() { return getToken(ArtifactParser.GRAPHICS, 0); }
		public GraphicsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_graphics; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).enterGraphics(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).exitGraphics(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactVisitor ) return ((ArtifactVisitor<? extends T>)visitor).visitGraphics(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GraphicsContext graphics() throws RecognitionException {
		GraphicsContext _localctx = new GraphicsContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_graphics);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			((GraphicsContext)_localctx).GRAPHICS = match(GRAPHICS);

			                String raw = ((GraphicsContext)_localctx).GRAPHICS.getText();
			                ((GraphicsContext)_localctx).adc =  new AngbandDisplayCharacter(raw.charAt(9), raw.charAt(11));
			            
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
		public int lev;
		public Token INTEGER;
		public TerminalNode LEVEL() { return getToken(ArtifactParser.LEVEL, 0); }
		public TerminalNode INTEGER() { return getToken(ArtifactParser.INTEGER, 0); }
		public LevelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_level; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).enterLevel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).exitLevel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactVisitor ) return ((ArtifactVisitor<? extends T>)visitor).visitLevel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LevelContext level() throws RecognitionException {
		LevelContext _localctx = new LevelContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_level);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(51);
			match(LEVEL);
			setState(52);
			((LevelContext)_localctx).INTEGER = match(INTEGER);
			 ((LevelContext)_localctx).lev =  Integer.parseInt(((LevelContext)_localctx).INTEGER.getText()); 
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
		public int wgt;
		public Token INTEGER;
		public TerminalNode WEIGHT() { return getToken(ArtifactParser.WEIGHT, 0); }
		public TerminalNode INTEGER() { return getToken(ArtifactParser.INTEGER, 0); }
		public WeightContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_weight; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).enterWeight(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).exitWeight(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactVisitor ) return ((ArtifactVisitor<? extends T>)visitor).visitWeight(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WeightContext weight() throws RecognitionException {
		WeightContext _localctx = new WeightContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_weight);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(55);
			match(WEIGHT);
			setState(56);
			((WeightContext)_localctx).INTEGER = match(INTEGER);
			 ((WeightContext)_localctx).wgt =  Integer.parseInt(((WeightContext)_localctx).INTEGER.getText()); 
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
		public int cst;
		public Token INTEGER;
		public TerminalNode COST() { return getToken(ArtifactParser.COST, 0); }
		public TerminalNode INTEGER() { return getToken(ArtifactParser.INTEGER, 0); }
		public CostContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cost; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).enterCost(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).exitCost(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactVisitor ) return ((ArtifactVisitor<? extends T>)visitor).visitCost(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CostContext cost() throws RecognitionException {
		CostContext _localctx = new CostContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_cost);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(59);
			match(COST);
			setState(60);
			((CostContext)_localctx).INTEGER = match(INTEGER);
			 ((CostContext)_localctx).cst =  Integer.parseInt(((CostContext)_localctx).INTEGER.getText()); 
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
		public int prob;
		public int minRange;
		public int maxRange;
		public Token p;
		public Token min;
		public Token max;
		public TerminalNode ALLOC() { return getToken(ArtifactParser.ALLOC, 0); }
		public TerminalNode COLON() { return getToken(ArtifactParser.COLON, 0); }
		public TerminalNode RANGE_TO() { return getToken(ArtifactParser.RANGE_TO, 0); }
		public List<TerminalNode> INTEGER() { return getTokens(ArtifactParser.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(ArtifactParser.INTEGER, i);
		}
		public AllocContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alloc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).enterAlloc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).exitAlloc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactVisitor ) return ((ArtifactVisitor<? extends T>)visitor).visitAlloc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AllocContext alloc() throws RecognitionException {
		AllocContext _localctx = new AllocContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_alloc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			match(ALLOC);
			setState(64);
			((AllocContext)_localctx).p = match(INTEGER);
			setState(65);
			match(COLON);
			setState(66);
			((AllocContext)_localctx).min = match(INTEGER);
			setState(67);
			match(RANGE_TO);
			setState(68);
			((AllocContext)_localctx).max = match(INTEGER);

			                ((AllocContext)_localctx).prob =  Integer.parseInt(((AllocContext)_localctx).p.getText());
			                ((AllocContext)_localctx).minRange =  Integer.parseInt(((AllocContext)_localctx).min.getText());
			                ((AllocContext)_localctx).maxRange =  Integer.parseInt(((AllocContext)_localctx).max.getText());
			            
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
		public String damage;
		public int toH;
		public int toD;
		public Token DICE_STRING;
		public Token hit;
		public Token dam;
		public TerminalNode ATTACK() { return getToken(ArtifactParser.ATTACK, 0); }
		public TerminalNode DICE_STRING() { return getToken(ArtifactParser.DICE_STRING, 0); }
		public List<TerminalNode> COLON() { return getTokens(ArtifactParser.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(ArtifactParser.COLON, i);
		}
		public List<TerminalNode> INTEGER() { return getTokens(ArtifactParser.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(ArtifactParser.INTEGER, i);
		}
		public AttackContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attack; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).enterAttack(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).exitAttack(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactVisitor ) return ((ArtifactVisitor<? extends T>)visitor).visitAttack(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttackContext attack() throws RecognitionException {
		AttackContext _localctx = new AttackContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_attack);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(71);
			match(ATTACK);
			setState(72);
			((AttackContext)_localctx).DICE_STRING = match(DICE_STRING);
			setState(73);
			match(COLON);
			setState(74);
			((AttackContext)_localctx).hit = match(INTEGER);
			setState(75);
			match(COLON);
			setState(76);
			((AttackContext)_localctx).dam = match(INTEGER);

			                ((AttackContext)_localctx).damage =  ((AttackContext)_localctx).DICE_STRING.getText();
			                ((AttackContext)_localctx).toH =  Integer.parseInt(((AttackContext)_localctx).hit.getText());
			                ((AttackContext)_localctx).toD =  Integer.parseInt(((AttackContext)_localctx).dam.getText());
			            
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
		public int baseAC;
		public int toA;
		public Token base;
		public Token plus;
		public TerminalNode ARMOUR() { return getToken(ArtifactParser.ARMOUR, 0); }
		public TerminalNode COLON() { return getToken(ArtifactParser.COLON, 0); }
		public List<TerminalNode> INTEGER() { return getTokens(ArtifactParser.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(ArtifactParser.INTEGER, i);
		}
		public ArmourContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_armour; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).enterArmour(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).exitArmour(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactVisitor ) return ((ArtifactVisitor<? extends T>)visitor).visitArmour(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArmourContext armour() throws RecognitionException {
		ArmourContext _localctx = new ArmourContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_armour);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(79);
			match(ARMOUR);
			setState(80);
			((ArmourContext)_localctx).base = match(INTEGER);
			setState(81);
			match(COLON);
			setState(82);
			((ArmourContext)_localctx).plus = match(INTEGER);

			                ((ArmourContext)_localctx).baseAC =  Integer.parseInt(((ArmourContext)_localctx).base.getText());
			                ((ArmourContext)_localctx).toA =  Integer.parseInt(((ArmourContext)_localctx).plus.getText());
			            
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
		public Flag<ObjectFlag> flag;
		public Token f1;
		public Token f2;
		public TerminalNode FLAGS() { return getToken(ArtifactParser.FLAGS, 0); }
		public List<TerminalNode> MCASE() { return getTokens(ArtifactParser.MCASE); }
		public TerminalNode MCASE(int i) {
			return getToken(ArtifactParser.MCASE, i);
		}
		public List<TerminalNode> OR() { return getTokens(ArtifactParser.OR); }
		public TerminalNode OR(int i) {
			return getToken(ArtifactParser.OR, i);
		}
		public FlagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flags; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).enterFlags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).exitFlags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactVisitor ) return ((ArtifactVisitor<? extends T>)visitor).visitFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsContext flags() throws RecognitionException {
		FlagsContext _localctx = new FlagsContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_flags);

		            ((FlagsContext)_localctx).flag =  new Flag<>(ObjectFlag.class);
		        
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(85);
			match(FLAGS);
			setState(86);
			((FlagsContext)_localctx).f1 = match(MCASE);

			                String flag1 = "OF_" + ((FlagsContext)_localctx).f1.getText().trim();
			                _localctx.flag.on(ObjectFlag.valueOf(flag1));
			            
			setState(93);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(88);
					match(OR);
					setState(89);
					((FlagsContext)_localctx).f2 = match(MCASE);

					                String flag2 = "OF_" + ((FlagsContext)_localctx).f2.getText().trim();
					                _localctx.flag.on(ObjectFlag.valueOf(flag2));
					            
					}
					} 
				}
				setState(95);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(97);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OR) {
				{
				setState(96);
				match(OR);
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
	public static class ValuesContext extends ParserRuleContext {
		public Map <ObjectModifier, Integer> skills;
		public Token ps1;
		public Token v1;
		public Token ps2;
		public Token v2;
		public TerminalNode VALUES() { return getToken(ArtifactParser.VALUES, 0); }
		public List<TerminalNode> LBRACKET() { return getTokens(ArtifactParser.LBRACKET); }
		public TerminalNode LBRACKET(int i) {
			return getToken(ArtifactParser.LBRACKET, i);
		}
		public List<TerminalNode> RBRACKET() { return getTokens(ArtifactParser.RBRACKET); }
		public TerminalNode RBRACKET(int i) {
			return getToken(ArtifactParser.RBRACKET, i);
		}
		public List<TerminalNode> MCASE() { return getTokens(ArtifactParser.MCASE); }
		public TerminalNode MCASE(int i) {
			return getToken(ArtifactParser.MCASE, i);
		}
		public List<TerminalNode> INTEGER() { return getTokens(ArtifactParser.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(ArtifactParser.INTEGER, i);
		}
		public List<TerminalNode> OR() { return getTokens(ArtifactParser.OR); }
		public TerminalNode OR(int i) {
			return getToken(ArtifactParser.OR, i);
		}
		public ValuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_values; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).enterValues(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).exitValues(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactVisitor ) return ((ArtifactVisitor<? extends T>)visitor).visitValues(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValuesContext values() throws RecognitionException {
		ValuesContext _localctx = new ValuesContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_values);

		            ((ValuesContext)_localctx).skills =  new HashMap<>();
		        
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			match(VALUES);
			setState(100);
			((ValuesContext)_localctx).ps1 = match(MCASE);
			setState(101);
			match(LBRACKET);
			setState(102);
			((ValuesContext)_localctx).v1 = match(INTEGER);
			setState(103);
			match(RBRACKET);

			                String raw1 = ((ValuesContext)_localctx).ps1.getText().trim();
			                int val1 = Integer.parseInt(((ValuesContext)_localctx).v1.getText());
			                _localctx.skills.put(ObjectModifier.valueOf("OM_" + raw1), val1);
			            
			setState(113);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(105);
					match(OR);
					setState(106);
					((ValuesContext)_localctx).ps2 = match(MCASE);
					setState(107);
					match(LBRACKET);
					setState(108);
					((ValuesContext)_localctx).v2 = match(INTEGER);
					setState(109);
					match(RBRACKET);

					                String raw2 = ((ValuesContext)_localctx).ps2.getText().trim();
					                int val2 = Integer.parseInt(((ValuesContext)_localctx).v2.getText());
					                _localctx.skills.put(ObjectModifier.valueOf("OM_" + raw2), val2);
					            
					}
					} 
				}
				setState(115);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			}
			setState(117);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OR) {
				{
				setState(116);
				match(OR);
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
	public static class ActContext extends ParserRuleContext {
		public Activation activation;
		public Token ACT;
		public TerminalNode ACT() { return getToken(ArtifactParser.ACT, 0); }
		public ActContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_act; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).enterAct(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).exitAct(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactVisitor ) return ((ArtifactVisitor<? extends T>)visitor).visitAct(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActContext act() throws RecognitionException {
		ActContext _localctx = new ActContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_act);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(119);
			((ActContext)_localctx).ACT = match(ACT);

			                String name = ((ActContext)_localctx).ACT.getText().substring(4);
			                ((ActContext)_localctx).activation =  GameConstants.lookupActivation(name);
			            
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
		public String dice;
		public Token DICE_STRING;
		public Token INTEGER;
		public TerminalNode TIME() { return getToken(ArtifactParser.TIME, 0); }
		public TerminalNode DICE_STRING() { return getToken(ArtifactParser.DICE_STRING, 0); }
		public TerminalNode INTEGER() { return getToken(ArtifactParser.INTEGER, 0); }
		public TimeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_time; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).enterTime(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).exitTime(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactVisitor ) return ((ArtifactVisitor<? extends T>)visitor).visitTime(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TimeContext time() throws RecognitionException {
		TimeContext _localctx = new TimeContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_time);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(122);
			match(TIME);
			setState(127);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DICE_STRING:
				{
				{
				setState(123);
				((TimeContext)_localctx).DICE_STRING = match(DICE_STRING);

				                ((TimeContext)_localctx).dice =  ((TimeContext)_localctx).DICE_STRING.getText();
				            
				}
				}
				break;
			case INTEGER:
				{
				{
				setState(125);
				((TimeContext)_localctx).INTEGER = match(INTEGER);

				                ((TimeContext)_localctx).dice =  ((TimeContext)_localctx).INTEGER.getText();
				            
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
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
	public static class DescContext extends ParserRuleContext {
		public String description;
		public Token DESC;
		public TerminalNode DESC() { return getToken(ArtifactParser.DESC, 0); }
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_desc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactVisitor ) return ((ArtifactVisitor<? extends T>)visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_desc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(129);
			((DescContext)_localctx).DESC = match(DESC);
			 ((DescContext)_localctx).description =  ((DescContext)_localctx).DESC.getText().substring(5); 
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
		public Brand b;
		public Token BRAND;
		public TerminalNode BRAND() { return getToken(ArtifactParser.BRAND, 0); }
		public BrandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_brand; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).enterBrand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).exitBrand(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactVisitor ) return ((ArtifactVisitor<? extends T>)visitor).visitBrand(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BrandContext brand() throws RecognitionException {
		BrandContext _localctx = new BrandContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_brand);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
			((BrandContext)_localctx).BRAND = match(BRAND);

			                String raw = ((BrandContext)_localctx).BRAND.getText();
			                ((BrandContext)_localctx).b =  GameConstants.lookupBrandCode(raw.substring(6));
			            
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
		public Slay s;
		public Token SLAY;
		public TerminalNode SLAY() { return getToken(ArtifactParser.SLAY, 0); }
		public SlayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_slay; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).enterSlay(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).exitSlay(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactVisitor ) return ((ArtifactVisitor<? extends T>)visitor).visitSlay(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SlayContext slay() throws RecognitionException {
		SlayContext _localctx = new SlayContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_slay);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(135);
			((SlayContext)_localctx).SLAY = match(SLAY);

			                String raw = ((SlayContext)_localctx).SLAY.getText();
			                ((SlayContext)_localctx).s =  GameConstants.lookupSlay(raw.substring(5));
			            
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
		public Map<Curse, CurseData> curses;
		public Token MCASE;
		public Token INTEGER;
		public TerminalNode CURSE() { return getToken(ArtifactParser.CURSE, 0); }
		public TerminalNode MCASE() { return getToken(ArtifactParser.MCASE, 0); }
		public TerminalNode COLON() { return getToken(ArtifactParser.COLON, 0); }
		public TerminalNode INTEGER() { return getToken(ArtifactParser.INTEGER, 0); }
		public CurseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_curse; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).enterCurse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).exitCurse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactVisitor ) return ((ArtifactVisitor<? extends T>)visitor).visitCurse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CurseContext curse() throws RecognitionException {
		CurseContext _localctx = new CurseContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_curse);

		            ((CurseContext)_localctx).curses =  new HashMap<>();
		        
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(138);
			match(CURSE);
			setState(139);
			((CurseContext)_localctx).MCASE = match(MCASE);
			setState(140);
			match(COLON);
			setState(141);
			((CurseContext)_localctx).INTEGER = match(INTEGER);

			                String raw = ((CurseContext)_localctx).MCASE.getText();
			                int val = Integer.parseInt(((CurseContext)_localctx).INTEGER.getText());
			                Curse c = GameConstants.lookupCurse(raw);
			                CurseData cd = new CurseData(val, -1);
			                _localctx.curses.put(c, cd);
			            
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
		public Token MSG;
		public TerminalNode MSG() { return getToken(ArtifactParser.MSG, 0); }
		public MsgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_msg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).enterMsg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).exitMsg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactVisitor ) return ((ArtifactVisitor<? extends T>)visitor).visitMsg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MsgContext msg() throws RecognitionException {
		MsgContext _localctx = new MsgContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_msg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(144);
			((MsgContext)_localctx).MSG = match(MSG);
			 ((MsgContext)_localctx).message =  ((MsgContext)_localctx).MSG.getText().substring(4); 
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
		public Artifact art;
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
		public BrandContext brand;
		public CurseContext curse;
		public SlayContext slay;
		public ValuesContext values;
		public ActContext act;
		public TimeContext time;
		public MsgContext msg;
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
		public List<BrandContext> brand() {
			return getRuleContexts(BrandContext.class);
		}
		public BrandContext brand(int i) {
			return getRuleContext(BrandContext.class,i);
		}
		public List<CurseContext> curse() {
			return getRuleContexts(CurseContext.class);
		}
		public CurseContext curse(int i) {
			return getRuleContext(CurseContext.class,i);
		}
		public List<SlayContext> slay() {
			return getRuleContexts(SlayContext.class);
		}
		public SlayContext slay(int i) {
			return getRuleContext(SlayContext.class,i);
		}
		public List<ValuesContext> values() {
			return getRuleContexts(ValuesContext.class);
		}
		public ValuesContext values(int i) {
			return getRuleContext(ValuesContext.class,i);
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
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).enterArtifact(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).exitArtifact(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactVisitor ) return ((ArtifactVisitor<? extends T>)visitor).visitArtifact(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArtifactContext artifact() throws RecognitionException {
		ArtifactContext _localctx = new ArtifactContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_artifact);

		            String nameInit = "";
		            TValue tValInit = TValue.TV_NONE;
		            String sValInit = "";
		            AngbandDisplayCharacter adcInit = null;
		            int levelInit = 0;
		            int weightInit = 0;
		            int costInit = 0;
		            int probInit = 0;
		            int allocMin = 0;
		            int allocMax = 0;
		            String damageInit = "";
		            int toHInit = 0;
		            int toDInit = 0;
		            int baseACInit = 0;
		            int toAInit = 0;
		            Flag<ObjectFlag> flagsInit = new Flag<>(ObjectFlag.class);
		            Map<ObjectModifier, Integer> modifierInit = new HashMap<>();
		            Activation actInit = null;
		            String timeInit = "";
		            String descInit = "";
		            List<Brand> brandInit = new ArrayList<>();
		            List<Slay> slayInit = new ArrayList<>();
		            List<Map<Curse, CurseData>> curseInit = new ArrayList<>();
		            String messageInit = "";

		            ObjectKind kind = null;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(147);
			((ArtifactContext)_localctx).name = name();

			                nameInit = ((ArtifactContext)_localctx).name.nameStr;
			            
			setState(197); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(197);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case BASE_OBJECT:
					{
					setState(149);
					((ArtifactContext)_localctx).baseObject = baseObject();

					                tValInit = ((ArtifactContext)_localctx).baseObject.tVal;
					                sValInit = ((ArtifactContext)_localctx).baseObject.sVal;
					            
					}
					break;
				case GRAPHICS:
					{
					setState(152);
					((ArtifactContext)_localctx).graphics = graphics();

					                adcInit = ((ArtifactContext)_localctx).graphics.adc;
					            
					}
					break;
				case LEVEL:
					{
					setState(155);
					((ArtifactContext)_localctx).level = level();

					                levelInit = ((ArtifactContext)_localctx).level.lev;
					            
					}
					break;
				case WEIGHT:
					{
					setState(158);
					((ArtifactContext)_localctx).weight = weight();

					                weightInit =((ArtifactContext)_localctx).weight.wgt;
					            
					}
					break;
				case COST:
					{
					setState(161);
					((ArtifactContext)_localctx).cost = cost();

					                costInit = ((ArtifactContext)_localctx).cost.cst;
					            
					}
					break;
				case ALLOC:
					{
					setState(164);
					((ArtifactContext)_localctx).alloc = alloc();

					                probInit = ((ArtifactContext)_localctx).alloc.prob;
					                allocMin = ((ArtifactContext)_localctx).alloc.minRange;
					                allocMax = ((ArtifactContext)_localctx).alloc.maxRange;
					            
					}
					break;
				case ATTACK:
					{
					setState(167);
					((ArtifactContext)_localctx).attack = attack();

					                damageInit = ((ArtifactContext)_localctx).attack.damage;
					                toHInit = ((ArtifactContext)_localctx).attack.toH;
					                toDInit = ((ArtifactContext)_localctx).attack.toD;
					            
					}
					break;
				case ARMOUR:
					{
					setState(170);
					((ArtifactContext)_localctx).armour = armour();

					                baseACInit = ((ArtifactContext)_localctx).armour.baseAC;
					                toAInit = ((ArtifactContext)_localctx).armour.toA;
					            
					}
					break;
				case FLAGS:
					{
					setState(173);
					((ArtifactContext)_localctx).flags = flags();

					                flagsInit.union(((ArtifactContext)_localctx).flags.flag);
					            
					}
					break;
				case BRAND:
					{
					setState(176);
					((ArtifactContext)_localctx).brand = brand();

					                brandInit.add(((ArtifactContext)_localctx).brand.b);
					            
					}
					break;
				case CURSE:
					{
					setState(179);
					((ArtifactContext)_localctx).curse = curse();

					                curseInit.add(((ArtifactContext)_localctx).curse.curses);
					            
					}
					break;
				case SLAY:
					{
					setState(182);
					((ArtifactContext)_localctx).slay = slay();

					                slayInit.add(((ArtifactContext)_localctx).slay.s);
					            
					}
					break;
				case VALUES:
					{
					setState(185);
					((ArtifactContext)_localctx).values = values();

					                for (ObjectModifier mod : ((ArtifactContext)_localctx).values.skills.keySet())
					                    modifierInit.put(mod, ((ArtifactContext)_localctx).values.skills.get(mod));
					            
					}
					break;
				case ACT:
					{
					setState(188);
					((ArtifactContext)_localctx).act = act();

					                actInit = ((ArtifactContext)_localctx).act.activation;
					            
					}
					break;
				case TIME:
					{
					setState(191);
					((ArtifactContext)_localctx).time = time();

					                timeInit = ((ArtifactContext)_localctx).time.dice;
					            
					}
					break;
				case MSG:
					{
					setState(194);
					((ArtifactContext)_localctx).msg = msg();

					                messageInit = messageInit + ((ArtifactContext)_localctx).msg.message;
					            
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(199); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 1048560L) != 0) );
			setState(204); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(201);
				((ArtifactContext)_localctx).desc = desc();

				                descInit = descInit + ((ArtifactContext)_localctx).desc.description;
				            
				}
				}
				setState(206); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==DESC );
			}
			_ctx.stop = _input.LT(-1);

			            kind = GameConstants.lookupObjectKind(tValInit, nameInit);

			            if (kind == null) {
			                kind = new ObjectKind(adcInit, costInit, levelInit, allocMin, allocMax,
			                                      nameInit, tValInit, nameInit, null, true);
			            }

			            ((ArtifactContext)_localctx).art =  new Artifact(nameInit, descInit, 0, tValInit, sValInit, toHInit,
			                                toDInit, toAInit, baseACInit, damageInit,
			                                weightInit, costInit, flagsInit, modifierInit,
			                                brandInit, slayInit, curseInit, levelInit,
			                                probInit, allocMin, allocMax, actInit,
			                                messageInit, timeInit);
			        
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
		public List<Artifact> artifacts;
		public ArtifactContext artifact;
		public TerminalNode EOF() { return getToken(ArtifactParser.EOF, 0); }
		public List<ArtifactContext> artifact() {
			return getRuleContexts(ArtifactContext.class);
		}
		public ArtifactContext artifact(int i) {
			return getRuleContext(ArtifactContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ArtifactListener ) ((ArtifactListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ArtifactVisitor ) return ((ArtifactVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_file);

		            ((FileContext)_localctx).artifacts =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(211); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(208);
				((FileContext)_localctx).artifact = artifact();

				                _localctx.artifacts.add(((FileContext)_localctx).artifact.art);
				            
				}
				}
				setState(213); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(215);
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
		"\u0004\u0001\u001e\u00da\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007"+
		"\u0012\u0002\u0013\u0007\u0013\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0005\t\\\b\t\n\t\f\t_\t\t\u0001\t\u0003\tb\b\t\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0005\np\b\n\n\n\f\ns\t\n\u0001\n\u0003\nv\b\n\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f"+
		"\u0003\f\u0080\b\f\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001"+
		"\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0004\u0012\u00c6\b\u0012\u000b\u0012\f"+
		"\u0012\u00c7\u0001\u0012\u0001\u0012\u0001\u0012\u0004\u0012\u00cd\b\u0012"+
		"\u000b\u0012\f\u0012\u00ce\u0001\u0013\u0001\u0013\u0001\u0013\u0004\u0013"+
		"\u00d4\b\u0013\u000b\u0013\f\u0013\u00d5\u0001\u0013\u0001\u0013\u0001"+
		"\u0013\u0000\u0000\u0014\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012"+
		"\u0014\u0016\u0018\u001a\u001c\u001e \"$&\u0000\u0000\u00dc\u0000(\u0001"+
		"\u0000\u0000\u0000\u0002+\u0001\u0000\u0000\u0000\u00040\u0001\u0000\u0000"+
		"\u0000\u00063\u0001\u0000\u0000\u0000\b7\u0001\u0000\u0000\u0000\n;\u0001"+
		"\u0000\u0000\u0000\f?\u0001\u0000\u0000\u0000\u000eG\u0001\u0000\u0000"+
		"\u0000\u0010O\u0001\u0000\u0000\u0000\u0012U\u0001\u0000\u0000\u0000\u0014"+
		"c\u0001\u0000\u0000\u0000\u0016w\u0001\u0000\u0000\u0000\u0018z\u0001"+
		"\u0000\u0000\u0000\u001a\u0081\u0001\u0000\u0000\u0000\u001c\u0084\u0001"+
		"\u0000\u0000\u0000\u001e\u0087\u0001\u0000\u0000\u0000 \u008a\u0001\u0000"+
		"\u0000\u0000\"\u0090\u0001\u0000\u0000\u0000$\u0093\u0001\u0000\u0000"+
		"\u0000&\u00d3\u0001\u0000\u0000\u0000()\u0005\u0003\u0000\u0000)*\u0006"+
		"\u0000\uffff\uffff\u0000*\u0001\u0001\u0000\u0000\u0000+,\u0005\u0004"+
		"\u0000\u0000,-\u0005\u0019\u0000\u0000-.\u0005\u0017\u0000\u0000./\u0006"+
		"\u0001\uffff\uffff\u0000/\u0003\u0001\u0000\u0000\u000001\u0005\u0005"+
		"\u0000\u000012\u0006\u0002\uffff\uffff\u00002\u0005\u0001\u0000\u0000"+
		"\u000034\u0005\u0006\u0000\u000045\u0005\u0018\u0000\u000056\u0006\u0003"+
		"\uffff\uffff\u00006\u0007\u0001\u0000\u0000\u000078\u0005\u0007\u0000"+
		"\u000089\u0005\u0018\u0000\u00009:\u0006\u0004\uffff\uffff\u0000:\t\u0001"+
		"\u0000\u0000\u0000;<\u0005\b\u0000\u0000<=\u0005\u0018\u0000\u0000=>\u0006"+
		"\u0005\uffff\uffff\u0000>\u000b\u0001\u0000\u0000\u0000?@\u0005\t\u0000"+
		"\u0000@A\u0005\u0018\u0000\u0000AB\u0005\u0019\u0000\u0000BC\u0005\u0018"+
		"\u0000\u0000CD\u0005\u0015\u0000\u0000DE\u0005\u0018\u0000\u0000EF\u0006"+
		"\u0006\uffff\uffff\u0000F\r\u0001\u0000\u0000\u0000GH\u0005\n\u0000\u0000"+
		"HI\u0005\u0016\u0000\u0000IJ\u0005\u0019\u0000\u0000JK\u0005\u0018\u0000"+
		"\u0000KL\u0005\u0019\u0000\u0000LM\u0005\u0018\u0000\u0000MN\u0006\u0007"+
		"\uffff\uffff\u0000N\u000f\u0001\u0000\u0000\u0000OP\u0005\u000b\u0000"+
		"\u0000PQ\u0005\u0018\u0000\u0000QR\u0005\u0019\u0000\u0000RS\u0005\u0018"+
		"\u0000\u0000ST\u0006\b\uffff\uffff\u0000T\u0011\u0001\u0000\u0000\u0000"+
		"UV\u0005\f\u0000\u0000VW\u0005\u0017\u0000\u0000W]\u0006\t\uffff\uffff"+
		"\u0000XY\u0005\u001c\u0000\u0000YZ\u0005\u0017\u0000\u0000Z\\\u0006\t"+
		"\uffff\uffff\u0000[X\u0001\u0000\u0000\u0000\\_\u0001\u0000\u0000\u0000"+
		"][\u0001\u0000\u0000\u0000]^\u0001\u0000\u0000\u0000^a\u0001\u0000\u0000"+
		"\u0000_]\u0001\u0000\u0000\u0000`b\u0005\u001c\u0000\u0000a`\u0001\u0000"+
		"\u0000\u0000ab\u0001\u0000\u0000\u0000b\u0013\u0001\u0000\u0000\u0000"+
		"cd\u0005\u0010\u0000\u0000de\u0005\u0017\u0000\u0000ef\u0005\u001a\u0000"+
		"\u0000fg\u0005\u0018\u0000\u0000gh\u0005\u001b\u0000\u0000hq\u0006\n\uffff"+
		"\uffff\u0000ij\u0005\u001c\u0000\u0000jk\u0005\u0017\u0000\u0000kl\u0005"+
		"\u001a\u0000\u0000lm\u0005\u0018\u0000\u0000mn\u0005\u001b\u0000\u0000"+
		"np\u0006\n\uffff\uffff\u0000oi\u0001\u0000\u0000\u0000ps\u0001\u0000\u0000"+
		"\u0000qo\u0001\u0000\u0000\u0000qr\u0001\u0000\u0000\u0000ru\u0001\u0000"+
		"\u0000\u0000sq\u0001\u0000\u0000\u0000tv\u0005\u001c\u0000\u0000ut\u0001"+
		"\u0000\u0000\u0000uv\u0001\u0000\u0000\u0000v\u0015\u0001\u0000\u0000"+
		"\u0000wx\u0005\r\u0000\u0000xy\u0006\u000b\uffff\uffff\u0000y\u0017\u0001"+
		"\u0000\u0000\u0000z\u007f\u0005\u000e\u0000\u0000{|\u0005\u0016\u0000"+
		"\u0000|\u0080\u0006\f\uffff\uffff\u0000}~\u0005\u0018\u0000\u0000~\u0080"+
		"\u0006\f\uffff\uffff\u0000\u007f{\u0001\u0000\u0000\u0000\u007f}\u0001"+
		"\u0000\u0000\u0000\u0080\u0019\u0001\u0000\u0000\u0000\u0081\u0082\u0005"+
		"\u0014\u0000\u0000\u0082\u0083\u0006\r\uffff\uffff\u0000\u0083\u001b\u0001"+
		"\u0000\u0000\u0000\u0084\u0085\u0005\u0011\u0000\u0000\u0085\u0086\u0006"+
		"\u000e\uffff\uffff\u0000\u0086\u001d\u0001\u0000\u0000\u0000\u0087\u0088"+
		"\u0005\u0012\u0000\u0000\u0088\u0089\u0006\u000f\uffff\uffff\u0000\u0089"+
		"\u001f\u0001\u0000\u0000\u0000\u008a\u008b\u0005\u0013\u0000\u0000\u008b"+
		"\u008c\u0005\u0017\u0000\u0000\u008c\u008d\u0005\u0019\u0000\u0000\u008d"+
		"\u008e\u0005\u0018\u0000\u0000\u008e\u008f\u0006\u0010\uffff\uffff\u0000"+
		"\u008f!\u0001\u0000\u0000\u0000\u0090\u0091\u0005\u000f\u0000\u0000\u0091"+
		"\u0092\u0006\u0011\uffff\uffff\u0000\u0092#\u0001\u0000\u0000\u0000\u0093"+
		"\u0094\u0003\u0000\u0000\u0000\u0094\u00c5\u0006\u0012\uffff\uffff\u0000"+
		"\u0095\u0096\u0003\u0002\u0001\u0000\u0096\u0097\u0006\u0012\uffff\uffff"+
		"\u0000\u0097\u00c6\u0001\u0000\u0000\u0000\u0098\u0099\u0003\u0004\u0002"+
		"\u0000\u0099\u009a\u0006\u0012\uffff\uffff\u0000\u009a\u00c6\u0001\u0000"+
		"\u0000\u0000\u009b\u009c\u0003\u0006\u0003\u0000\u009c\u009d\u0006\u0012"+
		"\uffff\uffff\u0000\u009d\u00c6\u0001\u0000\u0000\u0000\u009e\u009f\u0003"+
		"\b\u0004\u0000\u009f\u00a0\u0006\u0012\uffff\uffff\u0000\u00a0\u00c6\u0001"+
		"\u0000\u0000\u0000\u00a1\u00a2\u0003\n\u0005\u0000\u00a2\u00a3\u0006\u0012"+
		"\uffff\uffff\u0000\u00a3\u00c6\u0001\u0000\u0000\u0000\u00a4\u00a5\u0003"+
		"\f\u0006\u0000\u00a5\u00a6\u0006\u0012\uffff\uffff\u0000\u00a6\u00c6\u0001"+
		"\u0000\u0000\u0000\u00a7\u00a8\u0003\u000e\u0007\u0000\u00a8\u00a9\u0006"+
		"\u0012\uffff\uffff\u0000\u00a9\u00c6\u0001\u0000\u0000\u0000\u00aa\u00ab"+
		"\u0003\u0010\b\u0000\u00ab\u00ac\u0006\u0012\uffff\uffff\u0000\u00ac\u00c6"+
		"\u0001\u0000\u0000\u0000\u00ad\u00ae\u0003\u0012\t\u0000\u00ae\u00af\u0006"+
		"\u0012\uffff\uffff\u0000\u00af\u00c6\u0001\u0000\u0000\u0000\u00b0\u00b1"+
		"\u0003\u001c\u000e\u0000\u00b1\u00b2\u0006\u0012\uffff\uffff\u0000\u00b2"+
		"\u00c6\u0001\u0000\u0000\u0000\u00b3\u00b4\u0003 \u0010\u0000\u00b4\u00b5"+
		"\u0006\u0012\uffff\uffff\u0000\u00b5\u00c6\u0001\u0000\u0000\u0000\u00b6"+
		"\u00b7\u0003\u001e\u000f\u0000\u00b7\u00b8\u0006\u0012\uffff\uffff\u0000"+
		"\u00b8\u00c6\u0001\u0000\u0000\u0000\u00b9\u00ba\u0003\u0014\n\u0000\u00ba"+
		"\u00bb\u0006\u0012\uffff\uffff\u0000\u00bb\u00c6\u0001\u0000\u0000\u0000"+
		"\u00bc\u00bd\u0003\u0016\u000b\u0000\u00bd\u00be\u0006\u0012\uffff\uffff"+
		"\u0000\u00be\u00c6\u0001\u0000\u0000\u0000\u00bf\u00c0\u0003\u0018\f\u0000"+
		"\u00c0\u00c1\u0006\u0012\uffff\uffff\u0000\u00c1\u00c6\u0001\u0000\u0000"+
		"\u0000\u00c2\u00c3\u0003\"\u0011\u0000\u00c3\u00c4\u0006\u0012\uffff\uffff"+
		"\u0000\u00c4\u00c6\u0001\u0000\u0000\u0000\u00c5\u0095\u0001\u0000\u0000"+
		"\u0000\u00c5\u0098\u0001\u0000\u0000\u0000\u00c5\u009b\u0001\u0000\u0000"+
		"\u0000\u00c5\u009e\u0001\u0000\u0000\u0000\u00c5\u00a1\u0001\u0000\u0000"+
		"\u0000\u00c5\u00a4\u0001\u0000\u0000\u0000\u00c5\u00a7\u0001\u0000\u0000"+
		"\u0000\u00c5\u00aa\u0001\u0000\u0000\u0000\u00c5\u00ad\u0001\u0000\u0000"+
		"\u0000\u00c5\u00b0\u0001\u0000\u0000\u0000\u00c5\u00b3\u0001\u0000\u0000"+
		"\u0000\u00c5\u00b6\u0001\u0000\u0000\u0000\u00c5\u00b9\u0001\u0000\u0000"+
		"\u0000\u00c5\u00bc\u0001\u0000\u0000\u0000\u00c5\u00bf\u0001\u0000\u0000"+
		"\u0000\u00c5\u00c2\u0001\u0000\u0000\u0000\u00c6\u00c7\u0001\u0000\u0000"+
		"\u0000\u00c7\u00c5\u0001\u0000\u0000\u0000\u00c7\u00c8\u0001\u0000\u0000"+
		"\u0000\u00c8\u00cc\u0001\u0000\u0000\u0000\u00c9\u00ca\u0003\u001a\r\u0000"+
		"\u00ca\u00cb\u0006\u0012\uffff\uffff\u0000\u00cb\u00cd\u0001\u0000\u0000"+
		"\u0000\u00cc\u00c9\u0001\u0000\u0000\u0000\u00cd\u00ce\u0001\u0000\u0000"+
		"\u0000\u00ce\u00cc\u0001\u0000\u0000\u0000\u00ce\u00cf\u0001\u0000\u0000"+
		"\u0000\u00cf%\u0001\u0000\u0000\u0000\u00d0\u00d1\u0003$\u0012\u0000\u00d1"+
		"\u00d2\u0006\u0013\uffff\uffff\u0000\u00d2\u00d4\u0001\u0000\u0000\u0000"+
		"\u00d3\u00d0\u0001\u0000\u0000\u0000\u00d4\u00d5\u0001\u0000\u0000\u0000"+
		"\u00d5\u00d3\u0001\u0000\u0000\u0000\u00d5\u00d6\u0001\u0000\u0000\u0000"+
		"\u00d6\u00d7\u0001\u0000\u0000\u0000\u00d7\u00d8\u0005\u0000\u0000\u0001"+
		"\u00d8\'\u0001\u0000\u0000\u0000\t]aqu\u007f\u00c5\u00c7\u00ce\u00d5";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}