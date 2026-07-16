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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/PlayerRaceGrammar.g4 by ANTLR 4.13.2

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;

    import uk.co.jackoftrades.backend.parser.playerrace.PlayerRaceParseRecord;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class PlayerRaceGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		RECORD_COUNT=1, NAME=2, STATS=3, SKILL_DISARM_PHYS=4, SKILL_DISARM_MAGIC=5, 
		SKILL_DEVICE=6, SKILL_SAVE=7, SKILL_STEALTH=8, SKILL_SEARCH=9, SKILL_MELEE=10, 
		SKILL_SHOOT=11, SKILL_THROW=12, SKILL_DIG=13, HITDIE=14, EXP=15, INFRAVISION=16, 
		HISTORY=17, AGE=18, HEIGHT=19, WEIGHT=20, OBJ_FLAGS=21, PLAYER_FLAGS=22, 
		VALUES=23, INTEGER=24, COLON=25, COMMENT=26, EOL=27, STRING=28, FREE_TEXT_EOL=29, 
		FLAG=30, FLAG_OR=31, FLAG_EOL=32, VALUE_NAME=33, VALUE_LBRACKET=34, VALUE_INT=35, 
		VALUE_RBRACKET=36, VALUE_OR=37, VALUE_EOL=38;
	public static final int
		RULE_recordCount = 0, RULE_name = 1, RULE_stats = 2, RULE_skillDisarmPhys = 3, 
		RULE_skillDisarmMagic = 4, RULE_skillDevice = 5, RULE_skillSave = 6, RULE_skillStealth = 7, 
		RULE_skillSearch = 8, RULE_skillMelee = 9, RULE_skillShoot = 10, RULE_skillThrow = 11, 
		RULE_skillDig = 12, RULE_hitdie = 13, RULE_exp = 14, RULE_infravision = 15, 
		RULE_history = 16, RULE_age = 17, RULE_height = 18, RULE_weight = 19, 
		RULE_objFlags = 20, RULE_playerFlags = 21, RULE_values = 22, RULE_playerRace = 23, 
		RULE_file = 24;
	private static String[] makeRuleNames() {
		return new String[] {
			"recordCount", "name", "stats", "skillDisarmPhys", "skillDisarmMagic", 
			"skillDevice", "skillSave", "skillStealth", "skillSearch", "skillMelee", 
			"skillShoot", "skillThrow", "skillDig", "hitdie", "exp", "infravision", 
			"history", "age", "height", "weight", "objFlags", "playerFlags", "values", 
			"playerRace", "file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'record-count:'", "'name:'", "'stats:'", "'skill-disarm-phys:'", 
			"'skill-disarm-magic:'", "'skill-device:'", "'skill-save:'", "'skill-stealth:'", 
			"'skill-search:'", "'skill-melee:'", "'skill-shoot:'", "'skill-throw:'", 
			"'skill-dig:'", "'hitdie:'", "'exp:'", "'infravision:'", "'history:'", 
			"'age:'", "'height:'", "'weight:'", "'obj-flags:'", "'player-flags:'", 
			"'values:'", null, "':'", null, null, null, null, null, null, null, null, 
			"'['", null, "']'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "RECORD_COUNT", "NAME", "STATS", "SKILL_DISARM_PHYS", "SKILL_DISARM_MAGIC", 
			"SKILL_DEVICE", "SKILL_SAVE", "SKILL_STEALTH", "SKILL_SEARCH", "SKILL_MELEE", 
			"SKILL_SHOOT", "SKILL_THROW", "SKILL_DIG", "HITDIE", "EXP", "INFRAVISION", 
			"HISTORY", "AGE", "HEIGHT", "WEIGHT", "OBJ_FLAGS", "PLAYER_FLAGS", "VALUES", 
			"INTEGER", "COLON", "COMMENT", "EOL", "STRING", "FREE_TEXT_EOL", "FLAG", 
			"FLAG_OR", "FLAG_EOL", "VALUE_NAME", "VALUE_LBRACKET", "VALUE_INT", "VALUE_RBRACKET", 
			"VALUE_OR", "VALUE_EOL"
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
	public String getGrammarFileName() { return "PlayerRaceGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public PlayerRaceGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RecordCountContext extends ParserRuleContext {
		public String count;
		public Token INTEGER;
		public TerminalNode RECORD_COUNT() { return getToken(PlayerRaceGrammar.RECORD_COUNT, 0); }
		public TerminalNode INTEGER() { return getToken(PlayerRaceGrammar.INTEGER, 0); }
		public RecordCountContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_recordCount; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterRecordCount(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitRecordCount(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitRecordCount(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RecordCountContext recordCount() throws RecognitionException {
		RecordCountContext _localctx = new RecordCountContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_recordCount);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(50);
			match(RECORD_COUNT);
			setState(51);
			((RecordCountContext)_localctx).INTEGER = match(INTEGER);
			 ((RecordCountContext)_localctx).count =  ((RecordCountContext)_localctx).INTEGER.getText(); 
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
		public TerminalNode NAME() { return getToken(PlayerRaceGrammar.NAME, 0); }
		public TerminalNode STRING() { return getToken(PlayerRaceGrammar.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(54);
			match(NAME);
			setState(55);
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
	public static class StatsContext extends ParserRuleContext {
		public Map<String, String> statsMap;
		public Token str;
		public Token intVal;
		public Token wis;
		public Token dex;
		public Token con;
		public TerminalNode STATS() { return getToken(PlayerRaceGrammar.STATS, 0); }
		public List<TerminalNode> COLON() { return getTokens(PlayerRaceGrammar.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(PlayerRaceGrammar.COLON, i);
		}
		public List<TerminalNode> INTEGER() { return getTokens(PlayerRaceGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(PlayerRaceGrammar.INTEGER, i);
		}
		public StatsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stats; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterStats(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitStats(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitStats(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatsContext stats() throws RecognitionException {
		StatsContext _localctx = new StatsContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_stats);

		            ((StatsContext)_localctx).statsMap =  new HashMap<>();
		        
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58);
			match(STATS);
			setState(59);
			((StatsContext)_localctx).str = match(INTEGER);
			setState(60);
			match(COLON);
			setState(61);
			((StatsContext)_localctx).intVal = match(INTEGER);
			setState(62);
			match(COLON);
			setState(63);
			((StatsContext)_localctx).wis = match(INTEGER);
			setState(64);
			match(COLON);
			setState(65);
			((StatsContext)_localctx).dex = match(INTEGER);
			setState(66);
			match(COLON);
			setState(67);
			((StatsContext)_localctx).con = match(INTEGER);

			                _localctx.statsMap.put("STAT_STR", ((StatsContext)_localctx).str.getText());
			                _localctx.statsMap.put("STAT_INT", ((StatsContext)_localctx).intVal.getText());
			                _localctx.statsMap.put("STAT_WIS", ((StatsContext)_localctx).wis.getText());
			                _localctx.statsMap.put("STAT_DEX", ((StatsContext)_localctx).dex.getText());
			                _localctx.statsMap.put("STAT_CON", ((StatsContext)_localctx).con.getText());
			            
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
	public static class SkillDisarmPhysContext extends ParserRuleContext {
		public String skillDisarmPhysValue;
		public Token INTEGER;
		public TerminalNode SKILL_DISARM_PHYS() { return getToken(PlayerRaceGrammar.SKILL_DISARM_PHYS, 0); }
		public TerminalNode INTEGER() { return getToken(PlayerRaceGrammar.INTEGER, 0); }
		public SkillDisarmPhysContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_skillDisarmPhys; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterSkillDisarmPhys(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitSkillDisarmPhys(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitSkillDisarmPhys(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SkillDisarmPhysContext skillDisarmPhys() throws RecognitionException {
		SkillDisarmPhysContext _localctx = new SkillDisarmPhysContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_skillDisarmPhys);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70);
			match(SKILL_DISARM_PHYS);
			setState(71);
			((SkillDisarmPhysContext)_localctx).INTEGER = match(INTEGER);
			 ((SkillDisarmPhysContext)_localctx).skillDisarmPhysValue =  ((SkillDisarmPhysContext)_localctx).INTEGER.getText(); 
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
	public static class SkillDisarmMagicContext extends ParserRuleContext {
		public String skillDisarmMagicValue;
		public Token INTEGER;
		public TerminalNode SKILL_DISARM_MAGIC() { return getToken(PlayerRaceGrammar.SKILL_DISARM_MAGIC, 0); }
		public TerminalNode INTEGER() { return getToken(PlayerRaceGrammar.INTEGER, 0); }
		public SkillDisarmMagicContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_skillDisarmMagic; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterSkillDisarmMagic(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitSkillDisarmMagic(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitSkillDisarmMagic(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SkillDisarmMagicContext skillDisarmMagic() throws RecognitionException {
		SkillDisarmMagicContext _localctx = new SkillDisarmMagicContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_skillDisarmMagic);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			match(SKILL_DISARM_MAGIC);
			setState(75);
			((SkillDisarmMagicContext)_localctx).INTEGER = match(INTEGER);
			 ((SkillDisarmMagicContext)_localctx).skillDisarmMagicValue =  ((SkillDisarmMagicContext)_localctx).INTEGER.getText(); 
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
	public static class SkillDeviceContext extends ParserRuleContext {
		public String skillDeviceValue;
		public Token INTEGER;
		public TerminalNode SKILL_DEVICE() { return getToken(PlayerRaceGrammar.SKILL_DEVICE, 0); }
		public TerminalNode INTEGER() { return getToken(PlayerRaceGrammar.INTEGER, 0); }
		public SkillDeviceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_skillDevice; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterSkillDevice(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitSkillDevice(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitSkillDevice(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SkillDeviceContext skillDevice() throws RecognitionException {
		SkillDeviceContext _localctx = new SkillDeviceContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_skillDevice);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(78);
			match(SKILL_DEVICE);
			setState(79);
			((SkillDeviceContext)_localctx).INTEGER = match(INTEGER);
			 ((SkillDeviceContext)_localctx).skillDeviceValue =  ((SkillDeviceContext)_localctx).INTEGER.getText(); 
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
		public String skillSaveValue;
		public Token INTEGER;
		public TerminalNode SKILL_SAVE() { return getToken(PlayerRaceGrammar.SKILL_SAVE, 0); }
		public TerminalNode INTEGER() { return getToken(PlayerRaceGrammar.INTEGER, 0); }
		public SkillSaveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_skillSave; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterSkillSave(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitSkillSave(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitSkillSave(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SkillSaveContext skillSave() throws RecognitionException {
		SkillSaveContext _localctx = new SkillSaveContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_skillSave);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			match(SKILL_SAVE);
			setState(83);
			((SkillSaveContext)_localctx).INTEGER = match(INTEGER);
			 ((SkillSaveContext)_localctx).skillSaveValue =  ((SkillSaveContext)_localctx).INTEGER.getText(); 
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
		public String skillStealthValue;
		public Token INTEGER;
		public TerminalNode SKILL_STEALTH() { return getToken(PlayerRaceGrammar.SKILL_STEALTH, 0); }
		public TerminalNode INTEGER() { return getToken(PlayerRaceGrammar.INTEGER, 0); }
		public SkillStealthContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_skillStealth; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterSkillStealth(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitSkillStealth(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitSkillStealth(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SkillStealthContext skillStealth() throws RecognitionException {
		SkillStealthContext _localctx = new SkillStealthContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_skillStealth);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(86);
			match(SKILL_STEALTH);
			setState(87);
			((SkillStealthContext)_localctx).INTEGER = match(INTEGER);
			 ((SkillStealthContext)_localctx).skillStealthValue =  ((SkillStealthContext)_localctx).INTEGER.getText(); 
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
		public String skillSearchValue;
		public Token INTEGER;
		public TerminalNode SKILL_SEARCH() { return getToken(PlayerRaceGrammar.SKILL_SEARCH, 0); }
		public TerminalNode INTEGER() { return getToken(PlayerRaceGrammar.INTEGER, 0); }
		public SkillSearchContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_skillSearch; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterSkillSearch(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitSkillSearch(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitSkillSearch(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SkillSearchContext skillSearch() throws RecognitionException {
		SkillSearchContext _localctx = new SkillSearchContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_skillSearch);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(90);
			match(SKILL_SEARCH);
			setState(91);
			((SkillSearchContext)_localctx).INTEGER = match(INTEGER);
			 ((SkillSearchContext)_localctx).skillSearchValue =  ((SkillSearchContext)_localctx).INTEGER.getText(); 
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
		public String skillMeleeValue;
		public Token INTEGER;
		public TerminalNode SKILL_MELEE() { return getToken(PlayerRaceGrammar.SKILL_MELEE, 0); }
		public TerminalNode INTEGER() { return getToken(PlayerRaceGrammar.INTEGER, 0); }
		public SkillMeleeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_skillMelee; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterSkillMelee(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitSkillMelee(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitSkillMelee(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SkillMeleeContext skillMelee() throws RecognitionException {
		SkillMeleeContext _localctx = new SkillMeleeContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_skillMelee);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(94);
			match(SKILL_MELEE);
			setState(95);
			((SkillMeleeContext)_localctx).INTEGER = match(INTEGER);
			 ((SkillMeleeContext)_localctx).skillMeleeValue =  ((SkillMeleeContext)_localctx).INTEGER.getText(); 
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
	public static class SkillShootContext extends ParserRuleContext {
		public String skillShootValue;
		public Token INTEGER;
		public TerminalNode SKILL_SHOOT() { return getToken(PlayerRaceGrammar.SKILL_SHOOT, 0); }
		public TerminalNode INTEGER() { return getToken(PlayerRaceGrammar.INTEGER, 0); }
		public SkillShootContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_skillShoot; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterSkillShoot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitSkillShoot(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitSkillShoot(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SkillShootContext skillShoot() throws RecognitionException {
		SkillShootContext _localctx = new SkillShootContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_skillShoot);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(98);
			match(SKILL_SHOOT);
			setState(99);
			((SkillShootContext)_localctx).INTEGER = match(INTEGER);
			 ((SkillShootContext)_localctx).skillShootValue =  ((SkillShootContext)_localctx).INTEGER.getText(); 
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
		public String skillThrowValue;
		public Token INTEGER;
		public TerminalNode SKILL_THROW() { return getToken(PlayerRaceGrammar.SKILL_THROW, 0); }
		public TerminalNode INTEGER() { return getToken(PlayerRaceGrammar.INTEGER, 0); }
		public SkillThrowContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_skillThrow; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterSkillThrow(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitSkillThrow(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitSkillThrow(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SkillThrowContext skillThrow() throws RecognitionException {
		SkillThrowContext _localctx = new SkillThrowContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_skillThrow);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(102);
			match(SKILL_THROW);
			setState(103);
			((SkillThrowContext)_localctx).INTEGER = match(INTEGER);
			 ((SkillThrowContext)_localctx).skillThrowValue =  ((SkillThrowContext)_localctx).INTEGER.getText(); 
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
		public String skillDigValue;
		public Token INTEGER;
		public TerminalNode SKILL_DIG() { return getToken(PlayerRaceGrammar.SKILL_DIG, 0); }
		public TerminalNode INTEGER() { return getToken(PlayerRaceGrammar.INTEGER, 0); }
		public SkillDigContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_skillDig; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterSkillDig(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitSkillDig(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitSkillDig(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SkillDigContext skillDig() throws RecognitionException {
		SkillDigContext _localctx = new SkillDigContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_skillDig);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(106);
			match(SKILL_DIG);
			setState(107);
			((SkillDigContext)_localctx).INTEGER = match(INTEGER);
			 ((SkillDigContext)_localctx).skillDigValue =  ((SkillDigContext)_localctx).INTEGER.getText(); 
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
	public static class HitdieContext extends ParserRuleContext {
		public String hitDieStr;
		public Token INTEGER;
		public TerminalNode HITDIE() { return getToken(PlayerRaceGrammar.HITDIE, 0); }
		public TerminalNode INTEGER() { return getToken(PlayerRaceGrammar.INTEGER, 0); }
		public HitdieContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hitdie; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterHitdie(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitHitdie(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitHitdie(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HitdieContext hitdie() throws RecognitionException {
		HitdieContext _localctx = new HitdieContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_hitdie);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110);
			match(HITDIE);
			setState(111);
			((HitdieContext)_localctx).INTEGER = match(INTEGER);
			 ((HitdieContext)_localctx).hitDieStr =  ((HitdieContext)_localctx).INTEGER.getText(); 
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
	public static class ExpContext extends ParserRuleContext {
		public String expStr;
		public Token INTEGER;
		public TerminalNode EXP() { return getToken(PlayerRaceGrammar.EXP, 0); }
		public TerminalNode INTEGER() { return getToken(PlayerRaceGrammar.INTEGER, 0); }
		public ExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterExp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitExp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpContext exp() throws RecognitionException {
		ExpContext _localctx = new ExpContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_exp);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(114);
			match(EXP);
			setState(115);
			((ExpContext)_localctx).INTEGER = match(INTEGER);
			 ((ExpContext)_localctx).expStr =  ((ExpContext)_localctx).INTEGER.getText(); 
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
	public static class InfravisionContext extends ParserRuleContext {
		public String infra;
		public Token INTEGER;
		public TerminalNode INFRAVISION() { return getToken(PlayerRaceGrammar.INFRAVISION, 0); }
		public TerminalNode INTEGER() { return getToken(PlayerRaceGrammar.INTEGER, 0); }
		public InfravisionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_infravision; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterInfravision(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitInfravision(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitInfravision(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InfravisionContext infravision() throws RecognitionException {
		InfravisionContext _localctx = new InfravisionContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_infravision);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(118);
			match(INFRAVISION);
			setState(119);
			((InfravisionContext)_localctx).INTEGER = match(INTEGER);
			 ((InfravisionContext)_localctx).infra =  ((InfravisionContext)_localctx).INTEGER.getText(); 
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
	public static class HistoryContext extends ParserRuleContext {
		public String startingHistory;
		public Token INTEGER;
		public TerminalNode HISTORY() { return getToken(PlayerRaceGrammar.HISTORY, 0); }
		public TerminalNode INTEGER() { return getToken(PlayerRaceGrammar.INTEGER, 0); }
		public HistoryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_history; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterHistory(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitHistory(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitHistory(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HistoryContext history() throws RecognitionException {
		HistoryContext _localctx = new HistoryContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_history);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(122);
			match(HISTORY);
			setState(123);
			((HistoryContext)_localctx).INTEGER = match(INTEGER);
			 ((HistoryContext)_localctx).startingHistory =  ((HistoryContext)_localctx).INTEGER.getText(); 
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
	public static class AgeContext extends ParserRuleContext {
		public String ageBase;
		public String ageMod;
		public Token b;
		public Token m;
		public TerminalNode AGE() { return getToken(PlayerRaceGrammar.AGE, 0); }
		public TerminalNode COLON() { return getToken(PlayerRaceGrammar.COLON, 0); }
		public List<TerminalNode> INTEGER() { return getTokens(PlayerRaceGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(PlayerRaceGrammar.INTEGER, i);
		}
		public AgeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_age; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterAge(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitAge(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitAge(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AgeContext age() throws RecognitionException {
		AgeContext _localctx = new AgeContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_age);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(126);
			match(AGE);
			setState(127);
			((AgeContext)_localctx).b = match(INTEGER);
			setState(128);
			match(COLON);
			setState(129);
			((AgeContext)_localctx).m = match(INTEGER);

			                ((AgeContext)_localctx).ageBase =  ((AgeContext)_localctx).b.getText();
			                ((AgeContext)_localctx).ageMod =  ((AgeContext)_localctx).m.getText();
			            
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
	public static class HeightContext extends ParserRuleContext {
		public String heightBase;
		public String heightMod;
		public Token b;
		public Token m;
		public TerminalNode HEIGHT() { return getToken(PlayerRaceGrammar.HEIGHT, 0); }
		public TerminalNode COLON() { return getToken(PlayerRaceGrammar.COLON, 0); }
		public List<TerminalNode> INTEGER() { return getTokens(PlayerRaceGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(PlayerRaceGrammar.INTEGER, i);
		}
		public HeightContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_height; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterHeight(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitHeight(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitHeight(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HeightContext height() throws RecognitionException {
		HeightContext _localctx = new HeightContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_height);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
			match(HEIGHT);
			setState(133);
			((HeightContext)_localctx).b = match(INTEGER);
			setState(134);
			match(COLON);
			setState(135);
			((HeightContext)_localctx).m = match(INTEGER);

			                ((HeightContext)_localctx).heightBase =  ((HeightContext)_localctx).b.getText();
			                ((HeightContext)_localctx).heightMod =  ((HeightContext)_localctx).m.getText();
			            
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
		public String weightBase;
		public String weightMod;
		public Token b;
		public Token m;
		public TerminalNode WEIGHT() { return getToken(PlayerRaceGrammar.WEIGHT, 0); }
		public TerminalNode COLON() { return getToken(PlayerRaceGrammar.COLON, 0); }
		public List<TerminalNode> INTEGER() { return getTokens(PlayerRaceGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(PlayerRaceGrammar.INTEGER, i);
		}
		public WeightContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_weight; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterWeight(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitWeight(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitWeight(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WeightContext weight() throws RecognitionException {
		WeightContext _localctx = new WeightContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_weight);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(138);
			match(WEIGHT);
			setState(139);
			((WeightContext)_localctx).b = match(INTEGER);
			setState(140);
			match(COLON);
			setState(141);
			((WeightContext)_localctx).m = match(INTEGER);

			                ((WeightContext)_localctx).weightBase =  ((WeightContext)_localctx).b.getText();
			                ((WeightContext)_localctx).weightMod =  ((WeightContext)_localctx).m.getText();
			            
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
		public List<String> oFlags;
		public Token f1;
		public Token f2;
		public TerminalNode OBJ_FLAGS() { return getToken(PlayerRaceGrammar.OBJ_FLAGS, 0); }
		public List<TerminalNode> FLAG() { return getTokens(PlayerRaceGrammar.FLAG); }
		public TerminalNode FLAG(int i) {
			return getToken(PlayerRaceGrammar.FLAG, i);
		}
		public List<TerminalNode> FLAG_OR() { return getTokens(PlayerRaceGrammar.FLAG_OR); }
		public TerminalNode FLAG_OR(int i) {
			return getToken(PlayerRaceGrammar.FLAG_OR, i);
		}
		public ObjFlagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objFlags; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterObjFlags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitObjFlags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitObjFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjFlagsContext objFlags() throws RecognitionException {
		ObjFlagsContext _localctx = new ObjFlagsContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_objFlags);

		            ((ObjFlagsContext)_localctx).oFlags =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(144);
			match(OBJ_FLAGS);
			setState(155);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FLAG) {
				{
				setState(145);
				((ObjFlagsContext)_localctx).f1 = match(FLAG);

				                _localctx.oFlags.add(((ObjFlagsContext)_localctx).f1.getText());
				            
				setState(152);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==FLAG_OR) {
					{
					{
					setState(147);
					match(FLAG_OR);
					setState(148);
					((ObjFlagsContext)_localctx).f2 = match(FLAG);

					                _localctx.oFlags.add(((ObjFlagsContext)_localctx).f2.getText());
					            
					}
					}
					setState(154);
					_errHandler.sync(this);
					_la = _input.LA(1);
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
	public static class PlayerFlagsContext extends ParserRuleContext {
		public List<String> pFlags;
		public Token f1;
		public Token f2;
		public TerminalNode PLAYER_FLAGS() { return getToken(PlayerRaceGrammar.PLAYER_FLAGS, 0); }
		public List<TerminalNode> FLAG() { return getTokens(PlayerRaceGrammar.FLAG); }
		public TerminalNode FLAG(int i) {
			return getToken(PlayerRaceGrammar.FLAG, i);
		}
		public List<TerminalNode> FLAG_OR() { return getTokens(PlayerRaceGrammar.FLAG_OR); }
		public TerminalNode FLAG_OR(int i) {
			return getToken(PlayerRaceGrammar.FLAG_OR, i);
		}
		public PlayerFlagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_playerFlags; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterPlayerFlags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitPlayerFlags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitPlayerFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PlayerFlagsContext playerFlags() throws RecognitionException {
		PlayerFlagsContext _localctx = new PlayerFlagsContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_playerFlags);

		                ((PlayerFlagsContext)_localctx).pFlags =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(157);
			match(PLAYER_FLAGS);
			setState(168);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FLAG) {
				{
				setState(158);
				((PlayerFlagsContext)_localctx).f1 = match(FLAG);

				                _localctx.pFlags.add(((PlayerFlagsContext)_localctx).f1.getText());
				            
				setState(165);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==FLAG_OR) {
					{
					{
					setState(160);
					match(FLAG_OR);
					setState(161);
					((PlayerFlagsContext)_localctx).f2 = match(FLAG);

					                _localctx.pFlags.add(((PlayerFlagsContext)_localctx).f2.getText());
					            
					}
					}
					setState(167);
					_errHandler.sync(this);
					_la = _input.LA(1);
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
	public static class ValuesContext extends ParserRuleContext {
		public Map<String, String> valueMap;
		public Token n1;
		public Token v1;
		public Token n2;
		public Token v2;
		public TerminalNode VALUES() { return getToken(PlayerRaceGrammar.VALUES, 0); }
		public List<TerminalNode> VALUE_LBRACKET() { return getTokens(PlayerRaceGrammar.VALUE_LBRACKET); }
		public TerminalNode VALUE_LBRACKET(int i) {
			return getToken(PlayerRaceGrammar.VALUE_LBRACKET, i);
		}
		public List<TerminalNode> VALUE_RBRACKET() { return getTokens(PlayerRaceGrammar.VALUE_RBRACKET); }
		public TerminalNode VALUE_RBRACKET(int i) {
			return getToken(PlayerRaceGrammar.VALUE_RBRACKET, i);
		}
		public List<TerminalNode> VALUE_NAME() { return getTokens(PlayerRaceGrammar.VALUE_NAME); }
		public TerminalNode VALUE_NAME(int i) {
			return getToken(PlayerRaceGrammar.VALUE_NAME, i);
		}
		public List<TerminalNode> VALUE_INT() { return getTokens(PlayerRaceGrammar.VALUE_INT); }
		public TerminalNode VALUE_INT(int i) {
			return getToken(PlayerRaceGrammar.VALUE_INT, i);
		}
		public List<TerminalNode> VALUE_OR() { return getTokens(PlayerRaceGrammar.VALUE_OR); }
		public TerminalNode VALUE_OR(int i) {
			return getToken(PlayerRaceGrammar.VALUE_OR, i);
		}
		public ValuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_values; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterValues(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitValues(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitValues(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValuesContext values() throws RecognitionException {
		ValuesContext _localctx = new ValuesContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_values);

		            ((ValuesContext)_localctx).valueMap =  new HashMap<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(170);
			match(VALUES);
			setState(171);
			((ValuesContext)_localctx).n1 = match(VALUE_NAME);
			setState(172);
			match(VALUE_LBRACKET);
			setState(173);
			((ValuesContext)_localctx).v1 = match(VALUE_INT);
			setState(174);
			match(VALUE_RBRACKET);

			                _localctx.valueMap.put(((ValuesContext)_localctx).n1.getText(), ((ValuesContext)_localctx).v1.getText());
			            
			setState(184);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==VALUE_OR) {
				{
				{
				setState(176);
				match(VALUE_OR);
				setState(177);
				((ValuesContext)_localctx).n2 = match(VALUE_NAME);
				setState(178);
				match(VALUE_LBRACKET);
				setState(179);
				((ValuesContext)_localctx).v2 = match(VALUE_INT);
				setState(180);
				match(VALUE_RBRACKET);

				                _localctx.valueMap.put(((ValuesContext)_localctx).n2.getText(), ((ValuesContext)_localctx).v2.getText());
				            
				}
				}
				setState(186);
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
	public static class PlayerRaceContext extends ParserRuleContext {
		public PlayerRaceParseRecord race;
		public NameContext name;
		public StatsContext stats;
		public SkillDisarmPhysContext skillDisarmPhys;
		public SkillDisarmMagicContext skillDisarmMagic;
		public SkillDeviceContext skillDevice;
		public SkillSaveContext skillSave;
		public SkillStealthContext skillStealth;
		public SkillSearchContext skillSearch;
		public SkillMeleeContext skillMelee;
		public SkillShootContext skillShoot;
		public SkillThrowContext skillThrow;
		public SkillDigContext skillDig;
		public HitdieContext hitdie;
		public ExpContext exp;
		public InfravisionContext infravision;
		public HistoryContext history;
		public AgeContext age;
		public HeightContext height;
		public WeightContext weight;
		public ObjFlagsContext objFlags;
		public PlayerFlagsContext playerFlags;
		public ValuesContext values;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public List<StatsContext> stats() {
			return getRuleContexts(StatsContext.class);
		}
		public StatsContext stats(int i) {
			return getRuleContext(StatsContext.class,i);
		}
		public List<SkillDisarmPhysContext> skillDisarmPhys() {
			return getRuleContexts(SkillDisarmPhysContext.class);
		}
		public SkillDisarmPhysContext skillDisarmPhys(int i) {
			return getRuleContext(SkillDisarmPhysContext.class,i);
		}
		public List<SkillDisarmMagicContext> skillDisarmMagic() {
			return getRuleContexts(SkillDisarmMagicContext.class);
		}
		public SkillDisarmMagicContext skillDisarmMagic(int i) {
			return getRuleContext(SkillDisarmMagicContext.class,i);
		}
		public List<SkillDeviceContext> skillDevice() {
			return getRuleContexts(SkillDeviceContext.class);
		}
		public SkillDeviceContext skillDevice(int i) {
			return getRuleContext(SkillDeviceContext.class,i);
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
		public List<SkillShootContext> skillShoot() {
			return getRuleContexts(SkillShootContext.class);
		}
		public SkillShootContext skillShoot(int i) {
			return getRuleContext(SkillShootContext.class,i);
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
		public List<HitdieContext> hitdie() {
			return getRuleContexts(HitdieContext.class);
		}
		public HitdieContext hitdie(int i) {
			return getRuleContext(HitdieContext.class,i);
		}
		public List<ExpContext> exp() {
			return getRuleContexts(ExpContext.class);
		}
		public ExpContext exp(int i) {
			return getRuleContext(ExpContext.class,i);
		}
		public List<InfravisionContext> infravision() {
			return getRuleContexts(InfravisionContext.class);
		}
		public InfravisionContext infravision(int i) {
			return getRuleContext(InfravisionContext.class,i);
		}
		public List<HistoryContext> history() {
			return getRuleContexts(HistoryContext.class);
		}
		public HistoryContext history(int i) {
			return getRuleContext(HistoryContext.class,i);
		}
		public List<AgeContext> age() {
			return getRuleContexts(AgeContext.class);
		}
		public AgeContext age(int i) {
			return getRuleContext(AgeContext.class,i);
		}
		public List<HeightContext> height() {
			return getRuleContexts(HeightContext.class);
		}
		public HeightContext height(int i) {
			return getRuleContext(HeightContext.class,i);
		}
		public List<WeightContext> weight() {
			return getRuleContexts(WeightContext.class);
		}
		public WeightContext weight(int i) {
			return getRuleContext(WeightContext.class,i);
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
		public PlayerRaceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_playerRace; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterPlayerRace(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitPlayerRace(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitPlayerRace(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PlayerRaceContext playerRace() throws RecognitionException {
		PlayerRaceContext _localctx = new PlayerRaceContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_playerRace);

		            String nameInit = "";
		            Map<String, String> statsInit = new HashMap<>();
		            Map<String, String> skillsInit = new HashMap<>();
		            String hitdieInit = "";
		            String expInit = "";
		            String infravisionInit = "";
		            String historyInit = "";
		            String ageBaseInit = "";
		            String ageModifierInit = "";
		            String heightBaseInit = "";
		            String heightModifierInit = "";
		            String weightBaseInit = "";
		            String weightModifierInit = "";
		            List<String> oFlagsInit = new ArrayList<>();
		            List<String> pFlagsInit = new ArrayList<>();
		            Map<String, String> valuesInit = new HashMap<>();
		            int lineInit = 0;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(187);
			((PlayerRaceContext)_localctx).name = name();

			                nameInit = ((PlayerRaceContext)_localctx).name.nameStr;
			                lineInit = ((PlayerRaceContext)_localctx).name.line;
			            
			setState(252); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(252);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case STATS:
					{
					setState(189);
					((PlayerRaceContext)_localctx).stats = stats();
					 statsInit.putAll(((PlayerRaceContext)_localctx).stats.statsMap); 
					}
					break;
				case SKILL_DISARM_PHYS:
					{
					setState(192);
					((PlayerRaceContext)_localctx).skillDisarmPhys = skillDisarmPhys();
					 skillsInit.put("SKILL_DISARM_PHYS", ((PlayerRaceContext)_localctx).skillDisarmPhys.skillDisarmPhysValue); 
					}
					break;
				case SKILL_DISARM_MAGIC:
					{
					setState(195);
					((PlayerRaceContext)_localctx).skillDisarmMagic = skillDisarmMagic();
					 skillsInit.put("SKILL_DISARM_MAGIC", ((PlayerRaceContext)_localctx).skillDisarmMagic.skillDisarmMagicValue); 
					}
					break;
				case SKILL_DEVICE:
					{
					setState(198);
					((PlayerRaceContext)_localctx).skillDevice = skillDevice();
					 skillsInit.put("SKILL_DEVICE", ((PlayerRaceContext)_localctx).skillDevice.skillDeviceValue); 
					}
					break;
				case SKILL_SAVE:
					{
					setState(201);
					((PlayerRaceContext)_localctx).skillSave = skillSave();
					 skillsInit.put("SKILL_SAVE", ((PlayerRaceContext)_localctx).skillSave.skillSaveValue); 
					}
					break;
				case SKILL_STEALTH:
					{
					setState(204);
					((PlayerRaceContext)_localctx).skillStealth = skillStealth();
					 skillsInit.put("SKILL_STEALTH", ((PlayerRaceContext)_localctx).skillStealth.skillStealthValue); 
					}
					break;
				case SKILL_SEARCH:
					{
					setState(207);
					((PlayerRaceContext)_localctx).skillSearch = skillSearch();
					 skillsInit.put("SKILL_SEARCH", ((PlayerRaceContext)_localctx).skillSearch.skillSearchValue); 
					}
					break;
				case SKILL_MELEE:
					{
					setState(210);
					((PlayerRaceContext)_localctx).skillMelee = skillMelee();
					 skillsInit.put("SKILL_TO_HIT_MELEE", ((PlayerRaceContext)_localctx).skillMelee.skillMeleeValue); 
					}
					break;
				case SKILL_SHOOT:
					{
					setState(213);
					((PlayerRaceContext)_localctx).skillShoot = skillShoot();
					 skillsInit.put("SKILL_TO_HIT_BOW", ((PlayerRaceContext)_localctx).skillShoot.skillShootValue); 
					}
					break;
				case SKILL_THROW:
					{
					setState(216);
					((PlayerRaceContext)_localctx).skillThrow = skillThrow();
					 skillsInit.put("SKILL_TO_HIT_THROW", ((PlayerRaceContext)_localctx).skillThrow.skillThrowValue); 
					}
					break;
				case SKILL_DIG:
					{
					setState(219);
					((PlayerRaceContext)_localctx).skillDig = skillDig();
					 skillsInit.put("SKILL_DIGGING", ((PlayerRaceContext)_localctx).skillDig.skillDigValue); 
					}
					break;
				case HITDIE:
					{
					setState(222);
					((PlayerRaceContext)_localctx).hitdie = hitdie();
					 hitdieInit = ((PlayerRaceContext)_localctx).hitdie.hitDieStr; 
					}
					break;
				case EXP:
					{
					setState(225);
					((PlayerRaceContext)_localctx).exp = exp();
					 expInit = ((PlayerRaceContext)_localctx).exp.expStr; 
					}
					break;
				case INFRAVISION:
					{
					setState(228);
					((PlayerRaceContext)_localctx).infravision = infravision();
					 infravisionInit = ((PlayerRaceContext)_localctx).infravision.infra; 
					}
					break;
				case HISTORY:
					{
					setState(231);
					((PlayerRaceContext)_localctx).history = history();
					 historyInit = ((PlayerRaceContext)_localctx).history.startingHistory; 
					}
					break;
				case AGE:
					{
					setState(234);
					((PlayerRaceContext)_localctx).age = age();
					 ageBaseInit = ((PlayerRaceContext)_localctx).age.ageBase; ageModifierInit = ((PlayerRaceContext)_localctx).age.ageMod; 
					}
					break;
				case HEIGHT:
					{
					setState(237);
					((PlayerRaceContext)_localctx).height = height();
					 heightBaseInit = ((PlayerRaceContext)_localctx).height.heightBase; heightModifierInit = ((PlayerRaceContext)_localctx).height.heightMod; 
					}
					break;
				case WEIGHT:
					{
					setState(240);
					((PlayerRaceContext)_localctx).weight = weight();
					 weightBaseInit = ((PlayerRaceContext)_localctx).weight.weightBase; weightModifierInit = ((PlayerRaceContext)_localctx).weight.weightMod; 
					}
					break;
				case OBJ_FLAGS:
					{
					setState(243);
					((PlayerRaceContext)_localctx).objFlags = objFlags();
					 oFlagsInit.addAll(((PlayerRaceContext)_localctx).objFlags.oFlags); 
					}
					break;
				case PLAYER_FLAGS:
					{
					setState(246);
					((PlayerRaceContext)_localctx).playerFlags = playerFlags();
					 pFlagsInit.addAll(((PlayerRaceContext)_localctx).playerFlags.pFlags); 
					}
					break;
				case VALUES:
					{
					setState(249);
					((PlayerRaceContext)_localctx).values = values();
					 valuesInit.putAll(((PlayerRaceContext)_localctx).values.valueMap); 
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(254); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 16777208L) != 0) );
			}
			_ctx.stop = _input.LT(-1);

			            ((PlayerRaceContext)_localctx).race =  new PlayerRaceParseRecord(nameInit, statsInit, skillsInit,
			                    hitdieInit, expInit, infravisionInit, historyInit,
			                    ageBaseInit, ageModifierInit, heightBaseInit, heightModifierInit,
			                    weightBaseInit, weightModifierInit, oFlagsInit, pFlagsInit,
			                    valuesInit, lineInit);
			        
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
		public List<PlayerRaceParseRecord> races;
		public String declaredRecordCount;
		public RecordCountContext recordCount;
		public PlayerRaceContext playerRace;
		public RecordCountContext recordCount() {
			return getRuleContext(RecordCountContext.class,0);
		}
		public TerminalNode EOF() { return getToken(PlayerRaceGrammar.EOF, 0); }
		public List<PlayerRaceContext> playerRace() {
			return getRuleContexts(PlayerRaceContext.class);
		}
		public PlayerRaceContext playerRace(int i) {
			return getRuleContext(PlayerRaceContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlayerRaceGrammarListener ) ((PlayerRaceGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PlayerRaceGrammarVisitor ) return ((PlayerRaceGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_file);

		            ((FileContext)_localctx).races =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(256);
			((FileContext)_localctx).recordCount = recordCount();
			 ((FileContext)_localctx).declaredRecordCount =  ((FileContext)_localctx).recordCount.count; 
			setState(261); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(258);
				((FileContext)_localctx).playerRace = playerRace();
				 _localctx.races.add(((FileContext)_localctx).playerRace.race); 
				}
				}
				setState(263); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(265);
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
		"\u0004\u0001&\u010c\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0005\u0014\u0097\b\u0014\n"+
		"\u0014\f\u0014\u009a\t\u0014\u0003\u0014\u009c\b\u0014\u0001\u0015\u0001"+
		"\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0005\u0015\u00a4"+
		"\b\u0015\n\u0015\f\u0015\u00a7\t\u0015\u0003\u0015\u00a9\b\u0015\u0001"+
		"\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001"+
		"\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0005"+
		"\u0016\u00b7\b\u0016\n\u0016\f\u0016\u00ba\t\u0016\u0001\u0017\u0001\u0017"+
		"\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017"+
		"\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017"+
		"\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017"+
		"\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017"+
		"\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017"+
		"\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017"+
		"\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017"+
		"\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017"+
		"\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017"+
		"\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017"+
		"\u0001\u0017\u0001\u0017\u0001\u0017\u0004\u0017\u00fd\b\u0017\u000b\u0017"+
		"\f\u0017\u00fe\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018"+
		"\u0004\u0018\u0106\b\u0018\u000b\u0018\f\u0018\u0107\u0001\u0018\u0001"+
		"\u0018\u0001\u0018\u0000\u0000\u0019\u0000\u0002\u0004\u0006\b\n\f\u000e"+
		"\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.0\u0000\u0000"+
		"\u010d\u00002\u0001\u0000\u0000\u0000\u00026\u0001\u0000\u0000\u0000\u0004"+
		":\u0001\u0000\u0000\u0000\u0006F\u0001\u0000\u0000\u0000\bJ\u0001\u0000"+
		"\u0000\u0000\nN\u0001\u0000\u0000\u0000\fR\u0001\u0000\u0000\u0000\u000e"+
		"V\u0001\u0000\u0000\u0000\u0010Z\u0001\u0000\u0000\u0000\u0012^\u0001"+
		"\u0000\u0000\u0000\u0014b\u0001\u0000\u0000\u0000\u0016f\u0001\u0000\u0000"+
		"\u0000\u0018j\u0001\u0000\u0000\u0000\u001an\u0001\u0000\u0000\u0000\u001c"+
		"r\u0001\u0000\u0000\u0000\u001ev\u0001\u0000\u0000\u0000 z\u0001\u0000"+
		"\u0000\u0000\"~\u0001\u0000\u0000\u0000$\u0084\u0001\u0000\u0000\u0000"+
		"&\u008a\u0001\u0000\u0000\u0000(\u0090\u0001\u0000\u0000\u0000*\u009d"+
		"\u0001\u0000\u0000\u0000,\u00aa\u0001\u0000\u0000\u0000.\u00bb\u0001\u0000"+
		"\u0000\u00000\u0100\u0001\u0000\u0000\u000023\u0005\u0001\u0000\u0000"+
		"34\u0005\u0018\u0000\u000045\u0006\u0000\uffff\uffff\u00005\u0001\u0001"+
		"\u0000\u0000\u000067\u0005\u0002\u0000\u000078\u0005\u001c\u0000\u0000"+
		"89\u0006\u0001\uffff\uffff\u00009\u0003\u0001\u0000\u0000\u0000:;\u0005"+
		"\u0003\u0000\u0000;<\u0005\u0018\u0000\u0000<=\u0005\u0019\u0000\u0000"+
		"=>\u0005\u0018\u0000\u0000>?\u0005\u0019\u0000\u0000?@\u0005\u0018\u0000"+
		"\u0000@A\u0005\u0019\u0000\u0000AB\u0005\u0018\u0000\u0000BC\u0005\u0019"+
		"\u0000\u0000CD\u0005\u0018\u0000\u0000DE\u0006\u0002\uffff\uffff\u0000"+
		"E\u0005\u0001\u0000\u0000\u0000FG\u0005\u0004\u0000\u0000GH\u0005\u0018"+
		"\u0000\u0000HI\u0006\u0003\uffff\uffff\u0000I\u0007\u0001\u0000\u0000"+
		"\u0000JK\u0005\u0005\u0000\u0000KL\u0005\u0018\u0000\u0000LM\u0006\u0004"+
		"\uffff\uffff\u0000M\t\u0001\u0000\u0000\u0000NO\u0005\u0006\u0000\u0000"+
		"OP\u0005\u0018\u0000\u0000PQ\u0006\u0005\uffff\uffff\u0000Q\u000b\u0001"+
		"\u0000\u0000\u0000RS\u0005\u0007\u0000\u0000ST\u0005\u0018\u0000\u0000"+
		"TU\u0006\u0006\uffff\uffff\u0000U\r\u0001\u0000\u0000\u0000VW\u0005\b"+
		"\u0000\u0000WX\u0005\u0018\u0000\u0000XY\u0006\u0007\uffff\uffff\u0000"+
		"Y\u000f\u0001\u0000\u0000\u0000Z[\u0005\t\u0000\u0000[\\\u0005\u0018\u0000"+
		"\u0000\\]\u0006\b\uffff\uffff\u0000]\u0011\u0001\u0000\u0000\u0000^_\u0005"+
		"\n\u0000\u0000_`\u0005\u0018\u0000\u0000`a\u0006\t\uffff\uffff\u0000a"+
		"\u0013\u0001\u0000\u0000\u0000bc\u0005\u000b\u0000\u0000cd\u0005\u0018"+
		"\u0000\u0000de\u0006\n\uffff\uffff\u0000e\u0015\u0001\u0000\u0000\u0000"+
		"fg\u0005\f\u0000\u0000gh\u0005\u0018\u0000\u0000hi\u0006\u000b\uffff\uffff"+
		"\u0000i\u0017\u0001\u0000\u0000\u0000jk\u0005\r\u0000\u0000kl\u0005\u0018"+
		"\u0000\u0000lm\u0006\f\uffff\uffff\u0000m\u0019\u0001\u0000\u0000\u0000"+
		"no\u0005\u000e\u0000\u0000op\u0005\u0018\u0000\u0000pq\u0006\r\uffff\uffff"+
		"\u0000q\u001b\u0001\u0000\u0000\u0000rs\u0005\u000f\u0000\u0000st\u0005"+
		"\u0018\u0000\u0000tu\u0006\u000e\uffff\uffff\u0000u\u001d\u0001\u0000"+
		"\u0000\u0000vw\u0005\u0010\u0000\u0000wx\u0005\u0018\u0000\u0000xy\u0006"+
		"\u000f\uffff\uffff\u0000y\u001f\u0001\u0000\u0000\u0000z{\u0005\u0011"+
		"\u0000\u0000{|\u0005\u0018\u0000\u0000|}\u0006\u0010\uffff\uffff\u0000"+
		"}!\u0001\u0000\u0000\u0000~\u007f\u0005\u0012\u0000\u0000\u007f\u0080"+
		"\u0005\u0018\u0000\u0000\u0080\u0081\u0005\u0019\u0000\u0000\u0081\u0082"+
		"\u0005\u0018\u0000\u0000\u0082\u0083\u0006\u0011\uffff\uffff\u0000\u0083"+
		"#\u0001\u0000\u0000\u0000\u0084\u0085\u0005\u0013\u0000\u0000\u0085\u0086"+
		"\u0005\u0018\u0000\u0000\u0086\u0087\u0005\u0019\u0000\u0000\u0087\u0088"+
		"\u0005\u0018\u0000\u0000\u0088\u0089\u0006\u0012\uffff\uffff\u0000\u0089"+
		"%\u0001\u0000\u0000\u0000\u008a\u008b\u0005\u0014\u0000\u0000\u008b\u008c"+
		"\u0005\u0018\u0000\u0000\u008c\u008d\u0005\u0019\u0000\u0000\u008d\u008e"+
		"\u0005\u0018\u0000\u0000\u008e\u008f\u0006\u0013\uffff\uffff\u0000\u008f"+
		"\'\u0001\u0000\u0000\u0000\u0090\u009b\u0005\u0015\u0000\u0000\u0091\u0092"+
		"\u0005\u001e\u0000\u0000\u0092\u0098\u0006\u0014\uffff\uffff\u0000\u0093"+
		"\u0094\u0005\u001f\u0000\u0000\u0094\u0095\u0005\u001e\u0000\u0000\u0095"+
		"\u0097\u0006\u0014\uffff\uffff\u0000\u0096\u0093\u0001\u0000\u0000\u0000"+
		"\u0097\u009a\u0001\u0000\u0000\u0000\u0098\u0096\u0001\u0000\u0000\u0000"+
		"\u0098\u0099\u0001\u0000\u0000\u0000\u0099\u009c\u0001\u0000\u0000\u0000"+
		"\u009a\u0098\u0001\u0000\u0000\u0000\u009b\u0091\u0001\u0000\u0000\u0000"+
		"\u009b\u009c\u0001\u0000\u0000\u0000\u009c)\u0001\u0000\u0000\u0000\u009d"+
		"\u00a8\u0005\u0016\u0000\u0000\u009e\u009f\u0005\u001e\u0000\u0000\u009f"+
		"\u00a5\u0006\u0015\uffff\uffff\u0000\u00a0\u00a1\u0005\u001f\u0000\u0000"+
		"\u00a1\u00a2\u0005\u001e\u0000\u0000\u00a2\u00a4\u0006\u0015\uffff\uffff"+
		"\u0000\u00a3\u00a0\u0001\u0000\u0000\u0000\u00a4\u00a7\u0001\u0000\u0000"+
		"\u0000\u00a5\u00a3\u0001\u0000\u0000\u0000\u00a5\u00a6\u0001\u0000\u0000"+
		"\u0000\u00a6\u00a9\u0001\u0000\u0000\u0000\u00a7\u00a5\u0001\u0000\u0000"+
		"\u0000\u00a8\u009e\u0001\u0000\u0000\u0000\u00a8\u00a9\u0001\u0000\u0000"+
		"\u0000\u00a9+\u0001\u0000\u0000\u0000\u00aa\u00ab\u0005\u0017\u0000\u0000"+
		"\u00ab\u00ac\u0005!\u0000\u0000\u00ac\u00ad\u0005\"\u0000\u0000\u00ad"+
		"\u00ae\u0005#\u0000\u0000\u00ae\u00af\u0005$\u0000\u0000\u00af\u00b8\u0006"+
		"\u0016\uffff\uffff\u0000\u00b0\u00b1\u0005%\u0000\u0000\u00b1\u00b2\u0005"+
		"!\u0000\u0000\u00b2\u00b3\u0005\"\u0000\u0000\u00b3\u00b4\u0005#\u0000"+
		"\u0000\u00b4\u00b5\u0005$\u0000\u0000\u00b5\u00b7\u0006\u0016\uffff\uffff"+
		"\u0000\u00b6\u00b0\u0001\u0000\u0000\u0000\u00b7\u00ba\u0001\u0000\u0000"+
		"\u0000\u00b8\u00b6\u0001\u0000\u0000\u0000\u00b8\u00b9\u0001\u0000\u0000"+
		"\u0000\u00b9-\u0001\u0000\u0000\u0000\u00ba\u00b8\u0001\u0000\u0000\u0000"+
		"\u00bb\u00bc\u0003\u0002\u0001\u0000\u00bc\u00fc\u0006\u0017\uffff\uffff"+
		"\u0000\u00bd\u00be\u0003\u0004\u0002\u0000\u00be\u00bf\u0006\u0017\uffff"+
		"\uffff\u0000\u00bf\u00fd\u0001\u0000\u0000\u0000\u00c0\u00c1\u0003\u0006"+
		"\u0003\u0000\u00c1\u00c2\u0006\u0017\uffff\uffff\u0000\u00c2\u00fd\u0001"+
		"\u0000\u0000\u0000\u00c3\u00c4\u0003\b\u0004\u0000\u00c4\u00c5\u0006\u0017"+
		"\uffff\uffff\u0000\u00c5\u00fd\u0001\u0000\u0000\u0000\u00c6\u00c7\u0003"+
		"\n\u0005\u0000\u00c7\u00c8\u0006\u0017\uffff\uffff\u0000\u00c8\u00fd\u0001"+
		"\u0000\u0000\u0000\u00c9\u00ca\u0003\f\u0006\u0000\u00ca\u00cb\u0006\u0017"+
		"\uffff\uffff\u0000\u00cb\u00fd\u0001\u0000\u0000\u0000\u00cc\u00cd\u0003"+
		"\u000e\u0007\u0000\u00cd\u00ce\u0006\u0017\uffff\uffff\u0000\u00ce\u00fd"+
		"\u0001\u0000\u0000\u0000\u00cf\u00d0\u0003\u0010\b\u0000\u00d0\u00d1\u0006"+
		"\u0017\uffff\uffff\u0000\u00d1\u00fd\u0001\u0000\u0000\u0000\u00d2\u00d3"+
		"\u0003\u0012\t\u0000\u00d3\u00d4\u0006\u0017\uffff\uffff\u0000\u00d4\u00fd"+
		"\u0001\u0000\u0000\u0000\u00d5\u00d6\u0003\u0014\n\u0000\u00d6\u00d7\u0006"+
		"\u0017\uffff\uffff\u0000\u00d7\u00fd\u0001\u0000\u0000\u0000\u00d8\u00d9"+
		"\u0003\u0016\u000b\u0000\u00d9\u00da\u0006\u0017\uffff\uffff\u0000\u00da"+
		"\u00fd\u0001\u0000\u0000\u0000\u00db\u00dc\u0003\u0018\f\u0000\u00dc\u00dd"+
		"\u0006\u0017\uffff\uffff\u0000\u00dd\u00fd\u0001\u0000\u0000\u0000\u00de"+
		"\u00df\u0003\u001a\r\u0000\u00df\u00e0\u0006\u0017\uffff\uffff\u0000\u00e0"+
		"\u00fd\u0001\u0000\u0000\u0000\u00e1\u00e2\u0003\u001c\u000e\u0000\u00e2"+
		"\u00e3\u0006\u0017\uffff\uffff\u0000\u00e3\u00fd\u0001\u0000\u0000\u0000"+
		"\u00e4\u00e5\u0003\u001e\u000f\u0000\u00e5\u00e6\u0006\u0017\uffff\uffff"+
		"\u0000\u00e6\u00fd\u0001\u0000\u0000\u0000\u00e7\u00e8\u0003 \u0010\u0000"+
		"\u00e8\u00e9\u0006\u0017\uffff\uffff\u0000\u00e9\u00fd\u0001\u0000\u0000"+
		"\u0000\u00ea\u00eb\u0003\"\u0011\u0000\u00eb\u00ec\u0006\u0017\uffff\uffff"+
		"\u0000\u00ec\u00fd\u0001\u0000\u0000\u0000\u00ed\u00ee\u0003$\u0012\u0000"+
		"\u00ee\u00ef\u0006\u0017\uffff\uffff\u0000\u00ef\u00fd\u0001\u0000\u0000"+
		"\u0000\u00f0\u00f1\u0003&\u0013\u0000\u00f1\u00f2\u0006\u0017\uffff\uffff"+
		"\u0000\u00f2\u00fd\u0001\u0000\u0000\u0000\u00f3\u00f4\u0003(\u0014\u0000"+
		"\u00f4\u00f5\u0006\u0017\uffff\uffff\u0000\u00f5\u00fd\u0001\u0000\u0000"+
		"\u0000\u00f6\u00f7\u0003*\u0015\u0000\u00f7\u00f8\u0006\u0017\uffff\uffff"+
		"\u0000\u00f8\u00fd\u0001\u0000\u0000\u0000\u00f9\u00fa\u0003,\u0016\u0000"+
		"\u00fa\u00fb\u0006\u0017\uffff\uffff\u0000\u00fb\u00fd\u0001\u0000\u0000"+
		"\u0000\u00fc\u00bd\u0001\u0000\u0000\u0000\u00fc\u00c0\u0001\u0000\u0000"+
		"\u0000\u00fc\u00c3\u0001\u0000\u0000\u0000\u00fc\u00c6\u0001\u0000\u0000"+
		"\u0000\u00fc\u00c9\u0001\u0000\u0000\u0000\u00fc\u00cc\u0001\u0000\u0000"+
		"\u0000\u00fc\u00cf\u0001\u0000\u0000\u0000\u00fc\u00d2\u0001\u0000\u0000"+
		"\u0000\u00fc\u00d5\u0001\u0000\u0000\u0000\u00fc\u00d8\u0001\u0000\u0000"+
		"\u0000\u00fc\u00db\u0001\u0000\u0000\u0000\u00fc\u00de\u0001\u0000\u0000"+
		"\u0000\u00fc\u00e1\u0001\u0000\u0000\u0000\u00fc\u00e4\u0001\u0000\u0000"+
		"\u0000\u00fc\u00e7\u0001\u0000\u0000\u0000\u00fc\u00ea\u0001\u0000\u0000"+
		"\u0000\u00fc\u00ed\u0001\u0000\u0000\u0000\u00fc\u00f0\u0001\u0000\u0000"+
		"\u0000\u00fc\u00f3\u0001\u0000\u0000\u0000\u00fc\u00f6\u0001\u0000\u0000"+
		"\u0000\u00fc\u00f9\u0001\u0000\u0000\u0000\u00fd\u00fe\u0001\u0000\u0000"+
		"\u0000\u00fe\u00fc\u0001\u0000\u0000\u0000\u00fe\u00ff\u0001\u0000\u0000"+
		"\u0000\u00ff/\u0001\u0000\u0000\u0000\u0100\u0101\u0003\u0000\u0000\u0000"+
		"\u0101\u0105\u0006\u0018\uffff\uffff\u0000\u0102\u0103\u0003.\u0017\u0000"+
		"\u0103\u0104\u0006\u0018\uffff\uffff\u0000\u0104\u0106\u0001\u0000\u0000"+
		"\u0000\u0105\u0102\u0001\u0000\u0000\u0000\u0106\u0107\u0001\u0000\u0000"+
		"\u0000\u0107\u0105\u0001\u0000\u0000\u0000\u0107\u0108\u0001\u0000\u0000"+
		"\u0000\u0108\u0109\u0001\u0000\u0000\u0000\u0109\u010a\u0005\u0000\u0000"+
		"\u0001\u010a1\u0001\u0000\u0000\u0000\b\u0098\u009b\u00a5\u00a8\u00b8"+
		"\u00fc\u00fe\u0107";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}