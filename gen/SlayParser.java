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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/Slay.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
    import uk.co.jackoftrades.middle.monsters.MonsterBase;
    import uk.co.jackoftrades.middle.objects.Slay;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;

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
public class SlayParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		COMMENT=1, EOL=2, CODE=3, NAME=4, RACE_FLAG=5, BASE=6, MULTIPLIER=7, O_MULTIPLIER=8, 
		POWER=9, MELEE_VERB=10, RANGE_VERB=11, NUMBER=12, FLAG=13, TEXT=14, BASE_FLAG=15;
	public static final int
		RULE_code = 0, RULE_name = 1, RULE_race_flag = 2, RULE_base = 3, RULE_multiplier = 4, 
		RULE_o_multiplier = 5, RULE_power = 6, RULE_melee_verb = 7, RULE_ranged_verb = 8, 
		RULE_slay = 9, RULE_file = 10;
	private static String[] makeRuleNames() {
		return new String[] {
			"code", "name", "race_flag", "base", "multiplier", "o_multiplier", "power", 
			"melee_verb", "ranged_verb", "slay", "file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, "'code:'", "'name:'", "'race-flag:'", "'base:'", "'multiplier:'", 
			"'o-multiplier:'", "'power:'", "'melee-verb:'", "'range-verb:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "COMMENT", "EOL", "CODE", "NAME", "RACE_FLAG", "BASE", "MULTIPLIER", 
			"O_MULTIPLIER", "POWER", "MELEE_VERB", "RANGE_VERB", "NUMBER", "FLAG", 
			"TEXT", "BASE_FLAG"
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
	public String getGrammarFileName() { return "Slay.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SlayParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CodeContext extends ParserRuleContext {
		public String codeStr;
		public Token FLAG;
		public TerminalNode CODE() { return getToken(SlayParser.CODE, 0); }
		public TerminalNode FLAG() { return getToken(SlayParser.FLAG, 0); }
		public CodeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_code; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SlayListener ) ((SlayListener)listener).enterCode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SlayListener ) ((SlayListener)listener).exitCode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SlayVisitor ) return ((SlayVisitor<? extends T>)visitor).visitCode(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CodeContext code() throws RecognitionException {
		CodeContext _localctx = new CodeContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_code);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(22);
			match(CODE);
			setState(23);
			((CodeContext)_localctx).FLAG = match(FLAG);
			 ((CodeContext)_localctx).codeStr =  ((CodeContext)_localctx).FLAG.getText(); 
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
		public Token TEXT;
		public TerminalNode NAME() { return getToken(SlayParser.NAME, 0); }
		public TerminalNode TEXT() { return getToken(SlayParser.TEXT, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SlayListener ) ((SlayListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SlayListener ) ((SlayListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SlayVisitor ) return ((SlayVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(26);
			match(NAME);
			setState(27);
			((NameContext)_localctx).TEXT = match(TEXT);
			 ((NameContext)_localctx).nameStr =  ((NameContext)_localctx).TEXT.getText(); 
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
	public static class Race_flagContext extends ParserRuleContext {
		public MonsterRaceFlag monRaceFlag;
		public Token FLAG;
		public TerminalNode RACE_FLAG() { return getToken(SlayParser.RACE_FLAG, 0); }
		public TerminalNode FLAG() { return getToken(SlayParser.FLAG, 0); }
		public Race_flagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_race_flag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SlayListener ) ((SlayListener)listener).enterRace_flag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SlayListener ) ((SlayListener)listener).exitRace_flag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SlayVisitor ) return ((SlayVisitor<? extends T>)visitor).visitRace_flag(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Race_flagContext race_flag() throws RecognitionException {
		Race_flagContext _localctx = new Race_flagContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_race_flag);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(30);
			match(RACE_FLAG);
			setState(31);
			((Race_flagContext)_localctx).FLAG = match(FLAG);
			 ((Race_flagContext)_localctx).monRaceFlag =  MonsterRaceFlag.valueOf("RF_" + ((Race_flagContext)_localctx).FLAG.getText().trim()); 
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
	public static class BaseContext extends ParserRuleContext {
		public MonsterBase monBase;
		public Token BASE_FLAG;
		public TerminalNode BASE() { return getToken(SlayParser.BASE, 0); }
		public TerminalNode BASE_FLAG() { return getToken(SlayParser.BASE_FLAG, 0); }
		public BaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_base; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SlayListener ) ((SlayListener)listener).enterBase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SlayListener ) ((SlayListener)listener).exitBase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SlayVisitor ) return ((SlayVisitor<? extends T>)visitor).visitBase(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BaseContext base() throws RecognitionException {
		BaseContext _localctx = new BaseContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_base);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(34);
			match(BASE);
			setState(35);
			((BaseContext)_localctx).BASE_FLAG = match(BASE_FLAG);
			 ((BaseContext)_localctx).monBase =  GameConstants.getMonsterBase(((BaseContext)_localctx).BASE_FLAG.getText()); 
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
	public static class MultiplierContext extends ParserRuleContext {
		public int multNum;
		public Token NUMBER;
		public TerminalNode MULTIPLIER() { return getToken(SlayParser.MULTIPLIER, 0); }
		public TerminalNode NUMBER() { return getToken(SlayParser.NUMBER, 0); }
		public MultiplierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiplier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SlayListener ) ((SlayListener)listener).enterMultiplier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SlayListener ) ((SlayListener)listener).exitMultiplier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SlayVisitor ) return ((SlayVisitor<? extends T>)visitor).visitMultiplier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MultiplierContext multiplier() throws RecognitionException {
		MultiplierContext _localctx = new MultiplierContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_multiplier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(38);
			match(MULTIPLIER);
			setState(39);
			((MultiplierContext)_localctx).NUMBER = match(NUMBER);
			 ((MultiplierContext)_localctx).multNum =  Integer.parseInt(((MultiplierContext)_localctx).NUMBER.getText()); 
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
	public static class O_multiplierContext extends ParserRuleContext {
		public int oMultNum;
		public Token NUMBER;
		public TerminalNode O_MULTIPLIER() { return getToken(SlayParser.O_MULTIPLIER, 0); }
		public TerminalNode NUMBER() { return getToken(SlayParser.NUMBER, 0); }
		public O_multiplierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_o_multiplier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SlayListener ) ((SlayListener)listener).enterO_multiplier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SlayListener ) ((SlayListener)listener).exitO_multiplier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SlayVisitor ) return ((SlayVisitor<? extends T>)visitor).visitO_multiplier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final O_multiplierContext o_multiplier() throws RecognitionException {
		O_multiplierContext _localctx = new O_multiplierContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_o_multiplier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(42);
			match(O_MULTIPLIER);
			setState(43);
			((O_multiplierContext)_localctx).NUMBER = match(NUMBER);
			 ((O_multiplierContext)_localctx).oMultNum =  Integer.parseInt(((O_multiplierContext)_localctx).NUMBER.getText()); 
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
		public int powerNum;
		public Token NUMBER;
		public TerminalNode POWER() { return getToken(SlayParser.POWER, 0); }
		public TerminalNode NUMBER() { return getToken(SlayParser.NUMBER, 0); }
		public PowerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_power; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SlayListener ) ((SlayListener)listener).enterPower(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SlayListener ) ((SlayListener)listener).exitPower(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SlayVisitor ) return ((SlayVisitor<? extends T>)visitor).visitPower(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PowerContext power() throws RecognitionException {
		PowerContext _localctx = new PowerContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_power);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			match(POWER);
			setState(47);
			((PowerContext)_localctx).NUMBER = match(NUMBER);
			 ((PowerContext)_localctx).powerNum =  Integer.parseInt(((PowerContext)_localctx).NUMBER.getText()); 
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
	public static class Melee_verbContext extends ParserRuleContext {
		public String meleeVerbStr;
		public Token TEXT;
		public TerminalNode MELEE_VERB() { return getToken(SlayParser.MELEE_VERB, 0); }
		public TerminalNode TEXT() { return getToken(SlayParser.TEXT, 0); }
		public Melee_verbContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_melee_verb; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SlayListener ) ((SlayListener)listener).enterMelee_verb(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SlayListener ) ((SlayListener)listener).exitMelee_verb(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SlayVisitor ) return ((SlayVisitor<? extends T>)visitor).visitMelee_verb(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Melee_verbContext melee_verb() throws RecognitionException {
		Melee_verbContext _localctx = new Melee_verbContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_melee_verb);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(50);
			match(MELEE_VERB);
			setState(51);
			((Melee_verbContext)_localctx).TEXT = match(TEXT);
			 ((Melee_verbContext)_localctx).meleeVerbStr =  ((Melee_verbContext)_localctx).TEXT.getText(); 
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
	public static class Ranged_verbContext extends ParserRuleContext {
		public String rangedVerbStr;
		public Token TEXT;
		public TerminalNode RANGE_VERB() { return getToken(SlayParser.RANGE_VERB, 0); }
		public TerminalNode TEXT() { return getToken(SlayParser.TEXT, 0); }
		public Ranged_verbContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ranged_verb; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SlayListener ) ((SlayListener)listener).enterRanged_verb(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SlayListener ) ((SlayListener)listener).exitRanged_verb(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SlayVisitor ) return ((SlayVisitor<? extends T>)visitor).visitRanged_verb(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Ranged_verbContext ranged_verb() throws RecognitionException {
		Ranged_verbContext _localctx = new Ranged_verbContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_ranged_verb);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(54);
			match(RANGE_VERB);
			setState(55);
			((Ranged_verbContext)_localctx).TEXT = match(TEXT);
			 ((Ranged_verbContext)_localctx).rangedVerbStr =  ((Ranged_verbContext)_localctx).TEXT.getText(); 
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
		public Slay slayObj;
		public CodeContext code;
		public NameContext name;
		public Race_flagContext race_flag;
		public BaseContext base;
		public MultiplierContext multiplier;
		public O_multiplierContext o_multiplier;
		public PowerContext power;
		public Melee_verbContext melee_verb;
		public Ranged_verbContext ranged_verb;
		public CodeContext code() {
			return getRuleContext(CodeContext.class,0);
		}
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public MultiplierContext multiplier() {
			return getRuleContext(MultiplierContext.class,0);
		}
		public O_multiplierContext o_multiplier() {
			return getRuleContext(O_multiplierContext.class,0);
		}
		public PowerContext power() {
			return getRuleContext(PowerContext.class,0);
		}
		public Melee_verbContext melee_verb() {
			return getRuleContext(Melee_verbContext.class,0);
		}
		public Ranged_verbContext ranged_verb() {
			return getRuleContext(Ranged_verbContext.class,0);
		}
		public Race_flagContext race_flag() {
			return getRuleContext(Race_flagContext.class,0);
		}
		public BaseContext base() {
			return getRuleContext(BaseContext.class,0);
		}
		public SlayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_slay; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SlayListener ) ((SlayListener)listener).enterSlay(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SlayListener ) ((SlayListener)listener).exitSlay(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SlayVisitor ) return ((SlayVisitor<? extends T>)visitor).visitSlay(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SlayContext slay() throws RecognitionException {
		SlayContext _localctx = new SlayContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_slay);

		            String codeInit = "";
		            String nameInit = "";
		            MonsterRaceFlag raceInit = MonsterRaceFlag.RF_NONE;
		            MonsterBase baseInit = null;
		            int multInit = 0;
		            int oMultInit = 0;
		            int powerInit = 0;
		            String meleeInit = "";
		            String rangedInit = "";
		        
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58);
			((SlayContext)_localctx).code = code();
			 codeInit = ((SlayContext)_localctx).code.codeStr; 
			setState(60);
			((SlayContext)_localctx).name = name();
			 nameInit = ((SlayContext)_localctx).name.nameStr; 
			setState(68);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case RACE_FLAG:
				{
				{
				setState(62);
				((SlayContext)_localctx).race_flag = race_flag();
				 raceInit = ((SlayContext)_localctx).race_flag.monRaceFlag; 
				}
				}
				break;
			case BASE:
				{
				{
				setState(65);
				((SlayContext)_localctx).base = base();
				 baseInit = ((SlayContext)_localctx).base.monBase; 
				}
				}
				break;
			case MULTIPLIER:
				break;
			default:
				break;
			}
			setState(70);
			((SlayContext)_localctx).multiplier = multiplier();
			 multInit = ((SlayContext)_localctx).multiplier.multNum; 
			setState(72);
			((SlayContext)_localctx).o_multiplier = o_multiplier();
			 oMultInit = ((SlayContext)_localctx).o_multiplier.oMultNum; 
			setState(74);
			((SlayContext)_localctx).power = power();
			 powerInit = ((SlayContext)_localctx).power.powerNum; 
			setState(76);
			((SlayContext)_localctx).melee_verb = melee_verb();
			 meleeInit = ((SlayContext)_localctx).melee_verb.meleeVerbStr; 
			setState(78);
			((SlayContext)_localctx).ranged_verb = ranged_verb();
			 rangedInit = ((SlayContext)_localctx).ranged_verb.rangedVerbStr; 
			}
			_ctx.stop = _input.LT(-1);

			            ((SlayContext)_localctx).slayObj =  new Slay(codeInit, nameInit, baseInit, meleeInit,
			                                rangedInit, raceInit, multInit, oMultInit,
			                                powerInit);
			        
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
		public List<Slay> slays;
		public SlayContext slay;
		public TerminalNode EOF() { return getToken(SlayParser.EOF, 0); }
		public List<SlayContext> slay() {
			return getRuleContexts(SlayContext.class);
		}
		public SlayContext slay(int i) {
			return getRuleContext(SlayContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SlayListener ) ((SlayListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SlayListener ) ((SlayListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SlayVisitor ) return ((SlayVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_file);

		            ((FileContext)_localctx).slays =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(84); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(81);
				((FileContext)_localctx).slay = slay();

				            _localctx.slays.add(((FileContext)_localctx).slay.slayObj);
				        
				}
				}
				setState(86); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==CODE );
			setState(88);
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
		"\u0004\u0001\u000f[\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0003\tE\b\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\n\u0001\n\u0001\n\u0004\nU\b\n\u000b\n\f\nV\u0001\n\u0001\n\u0001\n\u0000"+
		"\u0000\u000b\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0000"+
		"\u0000R\u0000\u0016\u0001\u0000\u0000\u0000\u0002\u001a\u0001\u0000\u0000"+
		"\u0000\u0004\u001e\u0001\u0000\u0000\u0000\u0006\"\u0001\u0000\u0000\u0000"+
		"\b&\u0001\u0000\u0000\u0000\n*\u0001\u0000\u0000\u0000\f.\u0001\u0000"+
		"\u0000\u0000\u000e2\u0001\u0000\u0000\u0000\u00106\u0001\u0000\u0000\u0000"+
		"\u0012:\u0001\u0000\u0000\u0000\u0014T\u0001\u0000\u0000\u0000\u0016\u0017"+
		"\u0005\u0003\u0000\u0000\u0017\u0018\u0005\r\u0000\u0000\u0018\u0019\u0006"+
		"\u0000\uffff\uffff\u0000\u0019\u0001\u0001\u0000\u0000\u0000\u001a\u001b"+
		"\u0005\u0004\u0000\u0000\u001b\u001c\u0005\u000e\u0000\u0000\u001c\u001d"+
		"\u0006\u0001\uffff\uffff\u0000\u001d\u0003\u0001\u0000\u0000\u0000\u001e"+
		"\u001f\u0005\u0005\u0000\u0000\u001f \u0005\r\u0000\u0000 !\u0006\u0002"+
		"\uffff\uffff\u0000!\u0005\u0001\u0000\u0000\u0000\"#\u0005\u0006\u0000"+
		"\u0000#$\u0005\u000f\u0000\u0000$%\u0006\u0003\uffff\uffff\u0000%\u0007"+
		"\u0001\u0000\u0000\u0000&\'\u0005\u0007\u0000\u0000\'(\u0005\f\u0000\u0000"+
		"()\u0006\u0004\uffff\uffff\u0000)\t\u0001\u0000\u0000\u0000*+\u0005\b"+
		"\u0000\u0000+,\u0005\f\u0000\u0000,-\u0006\u0005\uffff\uffff\u0000-\u000b"+
		"\u0001\u0000\u0000\u0000./\u0005\t\u0000\u0000/0\u0005\f\u0000\u00000"+
		"1\u0006\u0006\uffff\uffff\u00001\r\u0001\u0000\u0000\u000023\u0005\n\u0000"+
		"\u000034\u0005\u000e\u0000\u000045\u0006\u0007\uffff\uffff\u00005\u000f"+
		"\u0001\u0000\u0000\u000067\u0005\u000b\u0000\u000078\u0005\u000e\u0000"+
		"\u000089\u0006\b\uffff\uffff\u00009\u0011\u0001\u0000\u0000\u0000:;\u0003"+
		"\u0000\u0000\u0000;<\u0006\t\uffff\uffff\u0000<=\u0003\u0002\u0001\u0000"+
		"=D\u0006\t\uffff\uffff\u0000>?\u0003\u0004\u0002\u0000?@\u0006\t\uffff"+
		"\uffff\u0000@E\u0001\u0000\u0000\u0000AB\u0003\u0006\u0003\u0000BC\u0006"+
		"\t\uffff\uffff\u0000CE\u0001\u0000\u0000\u0000D>\u0001\u0000\u0000\u0000"+
		"DA\u0001\u0000\u0000\u0000DE\u0001\u0000\u0000\u0000EF\u0001\u0000\u0000"+
		"\u0000FG\u0003\b\u0004\u0000GH\u0006\t\uffff\uffff\u0000HI\u0003\n\u0005"+
		"\u0000IJ\u0006\t\uffff\uffff\u0000JK\u0003\f\u0006\u0000KL\u0006\t\uffff"+
		"\uffff\u0000LM\u0003\u000e\u0007\u0000MN\u0006\t\uffff\uffff\u0000NO\u0003"+
		"\u0010\b\u0000OP\u0006\t\uffff\uffff\u0000P\u0013\u0001\u0000\u0000\u0000"+
		"QR\u0003\u0012\t\u0000RS\u0006\n\uffff\uffff\u0000SU\u0001\u0000\u0000"+
		"\u0000TQ\u0001\u0000\u0000\u0000UV\u0001\u0000\u0000\u0000VT\u0001\u0000"+
		"\u0000\u0000VW\u0001\u0000\u0000\u0000WX\u0001\u0000\u0000\u0000XY\u0005"+
		"\u0000\u0000\u0001Y\u0015\u0001\u0000\u0000\u0000\u0002DV";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}