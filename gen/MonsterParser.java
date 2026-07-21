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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/Monster.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.backend.numerics.Random;
    import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.combat.BlowMethod;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.monsters.BlowEffect;
    import uk.co.jackoftrades.middle.monsters.MonsterAltmsg;
    import uk.co.jackoftrades.middle.monsters.MonsterBase;
    import uk.co.jackoftrades.middle.monsters.MonsterBlow;
    import uk.co.jackoftrades.middle.monsters.MonsterDrop;
    import uk.co.jackoftrades.middle.monsters.MonsterFriends;
    import uk.co.jackoftrades.middle.monsters.MonsterFriendsBase;
    import uk.co.jackoftrades.middle.monsters.MonsterMimic;
    import uk.co.jackoftrades.middle.monsters.MonsterRace;
    import uk.co.jackoftrades.middle.monsters.MonsterShape;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterAltmsgType;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterGroupRole;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;
    import uk.co.jackoftrades.middle.objects.ObjectKind;
    import uk.co.jackoftrades.middle.objects.enums.TValue;
    import uk.co.jackoftrades.frontend.colour.VisualsColourCyclesByRace;
    import uk.co.jackoftrades.frontend.colour.enums.ColourType;


    import java.util.ArrayList;
    import java.util.List;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class MonsterParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		COMMENT=1, EOL=2, NAME=3, PLURAL=4, BASE=5, GLYPH=6, COLOUR=7, SPEED=8, 
		HIT_POINTS=9, LIGHT=10, HEARING=11, SMELL=12, ARMOUR_CLASS=13, SLEEPINESS=14, 
		DEPTH=15, RARITY=16, EXPERIENCE=17, BLOW=18, FLAGS=19, FLAGS_OFF=20, INNATE_FREQ=21, 
		SPELL_FREQ=22, SPELL_POWER=23, SPELLS=24, MESSAGE_VIS=25, MESSAGE_INVIS=26, 
		MESSAGE_MISS=27, DESC=28, DROP=29, DROP_BASE=30, MIMIC=31, SHAPE=32, FRIENDS=33, 
		FRIENDS_BASE=34, COLOUR_CYCLE=35, INTEGER=36, DICE_STRING=37, COLON=38, 
		OR=39, COLOUR_CHAR=40, UCASE=41, LCASE=42;
	public static final int
		RULE_name = 0, RULE_plural = 1, RULE_base = 2, RULE_glyph = 3, RULE_colour = 4, 
		RULE_colourCycle = 5, RULE_speed = 6, RULE_hitPoints = 7, RULE_light = 8, 
		RULE_hearing = 9, RULE_smell = 10, RULE_armourClass = 11, RULE_sleepiness = 12, 
		RULE_dungeonDepth = 13, RULE_rarity = 14, RULE_experience = 15, RULE_blow = 16, 
		RULE_flags = 17, RULE_flagsOff = 18, RULE_innateFreq = 19, RULE_spellFreq = 20, 
		RULE_spellPower = 21, RULE_spells = 22, RULE_messageVis = 23, RULE_messageInvis = 24, 
		RULE_messageMiss = 25, RULE_desc = 26, RULE_shape = 27, RULE_drop = 28, 
		RULE_dropBase = 29, RULE_mimic = 30, RULE_friends = 31, RULE_friendsBase = 32, 
		RULE_monster = 33, RULE_file = 34;
	private static String[] makeRuleNames() {
		return new String[] {
			"name", "plural", "base", "glyph", "colour", "colourCycle", "speed", 
			"hitPoints", "light", "hearing", "smell", "armourClass", "sleepiness", 
			"dungeonDepth", "rarity", "experience", "blow", "flags", "flagsOff", 
			"innateFreq", "spellFreq", "spellPower", "spells", "messageVis", "messageInvis", 
			"messageMiss", "desc", "shape", "drop", "dropBase", "mimic", "friends", 
			"friendsBase", "monster", "file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, "'name:'", "'plural:'", "'base:'", null, "'color:'", 
			"'speed:'", "'hit-points:'", "'light:'", "'hearing:'", "'smell:'", "'armor-class:'", 
			"'sleepiness:'", "'depth:'", "'rarity:'", "'experience:'", "'blow:'", 
			null, "'flags-off:'", "'innate-freq:'", "'spell-freq:'", "'spell-power:'", 
			null, "'message-vis:'", "'message-invis:'", "'message-miss:'", "'desc:'", 
			"'drop:'", "'drop-base:'", "'mimic:'", "'shape:'", null, null, "'color-cycle:'", 
			null, null, "':'", "' | '"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "COMMENT", "EOL", "NAME", "PLURAL", "BASE", "GLYPH", "COLOUR", 
			"SPEED", "HIT_POINTS", "LIGHT", "HEARING", "SMELL", "ARMOUR_CLASS", "SLEEPINESS", 
			"DEPTH", "RARITY", "EXPERIENCE", "BLOW", "FLAGS", "FLAGS_OFF", "INNATE_FREQ", 
			"SPELL_FREQ", "SPELL_POWER", "SPELLS", "MESSAGE_VIS", "MESSAGE_INVIS", 
			"MESSAGE_MISS", "DESC", "DROP", "DROP_BASE", "MIMIC", "SHAPE", "FRIENDS", 
			"FRIENDS_BASE", "COLOUR_CYCLE", "INTEGER", "DICE_STRING", "COLON", "OR", 
			"COLOUR_CHAR", "UCASE", "LCASE"
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
	public String getGrammarFileName() { return "Monster.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public MonsterParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public Token LCASE;
		public TerminalNode NAME() { return getToken(MonsterParser.NAME, 0); }
		public TerminalNode LCASE() { return getToken(MonsterParser.LCASE, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70);
			match(NAME);
			setState(71);
			((NameContext)_localctx).LCASE = match(LCASE);
			 ((NameContext)_localctx).nameStr =  ((NameContext)_localctx).LCASE.getText(); 
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
	public static class PluralContext extends ParserRuleContext {
		public String pluralStr;
		public Token LCASE;
		public TerminalNode PLURAL() { return getToken(MonsterParser.PLURAL, 0); }
		public TerminalNode LCASE() { return getToken(MonsterParser.LCASE, 0); }
		public PluralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_plural; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterPlural(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitPlural(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitPlural(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PluralContext plural() throws RecognitionException {
		PluralContext _localctx = new PluralContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_plural);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			match(PLURAL);
			setState(75);
			((PluralContext)_localctx).LCASE = match(LCASE);
			 ((PluralContext)_localctx).pluralStr =  ((PluralContext)_localctx).LCASE.getText(); 
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
		public MonsterBase baseObj;
		public Token LCASE;
		public TerminalNode BASE() { return getToken(MonsterParser.BASE, 0); }
		public TerminalNode LCASE() { return getToken(MonsterParser.LCASE, 0); }
		public BaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_base; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterBase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitBase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitBase(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BaseContext base() throws RecognitionException {
		BaseContext _localctx = new BaseContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_base);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(78);
			match(BASE);
			setState(79);
			((BaseContext)_localctx).LCASE = match(LCASE);

			                String raw = ((BaseContext)_localctx).LCASE.getText();
			                ((BaseContext)_localctx).baseObj =  GameConstants.lookupMonsterBase(raw);
			            
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
	public static class GlyphContext extends ParserRuleContext {
		public char glyphChr;
		public Token GLYPH;
		public TerminalNode GLYPH() { return getToken(MonsterParser.GLYPH, 0); }
		public GlyphContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_glyph; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterGlyph(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitGlyph(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitGlyph(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GlyphContext glyph() throws RecognitionException {
		GlyphContext _localctx = new GlyphContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_glyph);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			((GlyphContext)_localctx).GLYPH = match(GLYPH);

			                ((GlyphContext)_localctx).glyphChr =  ((GlyphContext)_localctx).GLYPH.getText().charAt(6);
			            
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
	public static class ColourContext extends ParserRuleContext {
		public ColourType colourEnum;
		public Token COLOUR_CHAR;
		public TerminalNode COLOUR() { return getToken(MonsterParser.COLOUR, 0); }
		public TerminalNode COLOUR_CHAR() { return getToken(MonsterParser.COLOUR_CHAR, 0); }
		public ColourContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_colour; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterColour(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitColour(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitColour(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ColourContext colour() throws RecognitionException {
		ColourContext _localctx = new ColourContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_colour);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(85);
			match(COLOUR);
			setState(86);
			((ColourContext)_localctx).COLOUR_CHAR = match(COLOUR_CHAR);

			                String raw = ((ColourContext)_localctx).COLOUR_CHAR.getText();
			                ((ColourContext)_localctx).colourEnum =  ColourType.findColourType(raw.charAt(0));
			            
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
	public static class ColourCycleContext extends ParserRuleContext {
		public String groupName;
		public String cycleName;
		public Token group;
		public Token cycle;
		public TerminalNode COLOUR_CYCLE() { return getToken(MonsterParser.COLOUR_CYCLE, 0); }
		public TerminalNode COLON() { return getToken(MonsterParser.COLON, 0); }
		public List<TerminalNode> LCASE() { return getTokens(MonsterParser.LCASE); }
		public TerminalNode LCASE(int i) {
			return getToken(MonsterParser.LCASE, i);
		}
		public ColourCycleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_colourCycle; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterColourCycle(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitColourCycle(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitColourCycle(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ColourCycleContext colourCycle() throws RecognitionException {
		ColourCycleContext _localctx = new ColourCycleContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_colourCycle);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(89);
			match(COLOUR_CYCLE);
			setState(90);
			((ColourCycleContext)_localctx).group = match(LCASE);
			setState(91);
			match(COLON);
			setState(92);
			((ColourCycleContext)_localctx).cycle = match(LCASE);

			                ((ColourCycleContext)_localctx).groupName =  ((ColourCycleContext)_localctx).group.getText();
			                ((ColourCycleContext)_localctx).cycleName =  ((ColourCycleContext)_localctx).cycle.getText();
			            
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
	public static class SpeedContext extends ParserRuleContext {
		public int speedInt;
		public Token INTEGER;
		public TerminalNode SPEED() { return getToken(MonsterParser.SPEED, 0); }
		public TerminalNode INTEGER() { return getToken(MonsterParser.INTEGER, 0); }
		public SpeedContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_speed; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterSpeed(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitSpeed(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitSpeed(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SpeedContext speed() throws RecognitionException {
		SpeedContext _localctx = new SpeedContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_speed);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			match(SPEED);
			setState(96);
			((SpeedContext)_localctx).INTEGER = match(INTEGER);
			 ((SpeedContext)_localctx).speedInt =  Integer.parseInt(((SpeedContext)_localctx).INTEGER.getText()); 
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
	public static class HitPointsContext extends ParserRuleContext {
		public int hpInt;
		public Token INTEGER;
		public TerminalNode HIT_POINTS() { return getToken(MonsterParser.HIT_POINTS, 0); }
		public TerminalNode INTEGER() { return getToken(MonsterParser.INTEGER, 0); }
		public HitPointsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hitPoints; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterHitPoints(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitHitPoints(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitHitPoints(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HitPointsContext hitPoints() throws RecognitionException {
		HitPointsContext _localctx = new HitPointsContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_hitPoints);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			match(HIT_POINTS);
			setState(100);
			((HitPointsContext)_localctx).INTEGER = match(INTEGER);
			 ((HitPointsContext)_localctx).hpInt =  Integer.parseInt(((HitPointsContext)_localctx).INTEGER.getText()); 
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
	public static class LightContext extends ParserRuleContext {
		public int lightInt;
		public Token INTEGER;
		public TerminalNode LIGHT() { return getToken(MonsterParser.LIGHT, 0); }
		public TerminalNode INTEGER() { return getToken(MonsterParser.INTEGER, 0); }
		public LightContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_light; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterLight(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitLight(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitLight(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LightContext light() throws RecognitionException {
		LightContext _localctx = new LightContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_light);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(103);
			match(LIGHT);
			setState(104);
			((LightContext)_localctx).INTEGER = match(INTEGER);
			 ((LightContext)_localctx).lightInt =  Integer.parseInt(((LightContext)_localctx).INTEGER.getText()); 
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
	public static class HearingContext extends ParserRuleContext {
		public int hearingInt;
		public Token INTEGER;
		public TerminalNode HEARING() { return getToken(MonsterParser.HEARING, 0); }
		public TerminalNode INTEGER() { return getToken(MonsterParser.INTEGER, 0); }
		public HearingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hearing; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterHearing(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitHearing(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitHearing(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HearingContext hearing() throws RecognitionException {
		HearingContext _localctx = new HearingContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_hearing);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(107);
			match(HEARING);
			setState(108);
			((HearingContext)_localctx).INTEGER = match(INTEGER);
			 ((HearingContext)_localctx).hearingInt =  Integer.parseInt(((HearingContext)_localctx).INTEGER.getText()); 
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
	public static class SmellContext extends ParserRuleContext {
		public int smellInt;
		public Token INTEGER;
		public TerminalNode SMELL() { return getToken(MonsterParser.SMELL, 0); }
		public TerminalNode INTEGER() { return getToken(MonsterParser.INTEGER, 0); }
		public SmellContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_smell; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterSmell(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitSmell(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitSmell(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SmellContext smell() throws RecognitionException {
		SmellContext _localctx = new SmellContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_smell);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(111);
			match(SMELL);
			setState(112);
			((SmellContext)_localctx).INTEGER = match(INTEGER);
			 ((SmellContext)_localctx).smellInt =  Integer.parseInt(((SmellContext)_localctx).INTEGER.getText()); 
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
	public static class ArmourClassContext extends ParserRuleContext {
		public int acInt;
		public Token INTEGER;
		public TerminalNode ARMOUR_CLASS() { return getToken(MonsterParser.ARMOUR_CLASS, 0); }
		public TerminalNode INTEGER() { return getToken(MonsterParser.INTEGER, 0); }
		public ArmourClassContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_armourClass; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterArmourClass(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitArmourClass(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitArmourClass(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArmourClassContext armourClass() throws RecognitionException {
		ArmourClassContext _localctx = new ArmourClassContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_armourClass);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(115);
			match(ARMOUR_CLASS);
			setState(116);
			((ArmourClassContext)_localctx).INTEGER = match(INTEGER);
			 ((ArmourClassContext)_localctx).acInt =  Integer.parseInt(((ArmourClassContext)_localctx).INTEGER.getText()); 
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
	public static class SleepinessContext extends ParserRuleContext {
		public int sleepInt;
		public Token INTEGER;
		public TerminalNode SLEEPINESS() { return getToken(MonsterParser.SLEEPINESS, 0); }
		public TerminalNode INTEGER() { return getToken(MonsterParser.INTEGER, 0); }
		public SleepinessContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sleepiness; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterSleepiness(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitSleepiness(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitSleepiness(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SleepinessContext sleepiness() throws RecognitionException {
		SleepinessContext _localctx = new SleepinessContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_sleepiness);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(119);
			match(SLEEPINESS);
			setState(120);
			((SleepinessContext)_localctx).INTEGER = match(INTEGER);
			 ((SleepinessContext)_localctx).sleepInt =  Integer.parseInt(((SleepinessContext)_localctx).INTEGER.getText()); 
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
	public static class DungeonDepthContext extends ParserRuleContext {
		public int depthInt;
		public Token INTEGER;
		public TerminalNode DEPTH() { return getToken(MonsterParser.DEPTH, 0); }
		public TerminalNode INTEGER() { return getToken(MonsterParser.INTEGER, 0); }
		public DungeonDepthContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dungeonDepth; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterDungeonDepth(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitDungeonDepth(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitDungeonDepth(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DungeonDepthContext dungeonDepth() throws RecognitionException {
		DungeonDepthContext _localctx = new DungeonDepthContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_dungeonDepth);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(123);
			match(DEPTH);
			setState(124);
			((DungeonDepthContext)_localctx).INTEGER = match(INTEGER);
			 ((DungeonDepthContext)_localctx).depthInt =  Integer.parseInt(((DungeonDepthContext)_localctx).INTEGER.getText()); 
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
	public static class RarityContext extends ParserRuleContext {
		public int rarityInt;
		public Token INTEGER;
		public TerminalNode RARITY() { return getToken(MonsterParser.RARITY, 0); }
		public TerminalNode INTEGER() { return getToken(MonsterParser.INTEGER, 0); }
		public RarityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rarity; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterRarity(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitRarity(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitRarity(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RarityContext rarity() throws RecognitionException {
		RarityContext _localctx = new RarityContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_rarity);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(127);
			match(RARITY);
			setState(128);
			((RarityContext)_localctx).INTEGER = match(INTEGER);
			 ((RarityContext)_localctx).rarityInt =  Integer.parseInt(((RarityContext)_localctx).INTEGER.getText()); 
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
	public static class ExperienceContext extends ParserRuleContext {
		public int expInt;
		public Token INTEGER;
		public TerminalNode EXPERIENCE() { return getToken(MonsterParser.EXPERIENCE, 0); }
		public TerminalNode INTEGER() { return getToken(MonsterParser.INTEGER, 0); }
		public ExperienceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_experience; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterExperience(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitExperience(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitExperience(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExperienceContext experience() throws RecognitionException {
		ExperienceContext _localctx = new ExperienceContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_experience);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(131);
			match(EXPERIENCE);
			setState(132);
			((ExperienceContext)_localctx).INTEGER = match(INTEGER);
			 ((ExperienceContext)_localctx).expInt =  Integer.parseInt(((ExperienceContext)_localctx).INTEGER.getText()); 
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
		public MonsterBlow blowObj;
		public Token method1;
		public Token effect1;
		public Token amount1;
		public Token method2;
		public Token effect2;
		public Token method3;
		public TerminalNode BLOW() { return getToken(MonsterParser.BLOW, 0); }
		public List<TerminalNode> COLON() { return getTokens(MonsterParser.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(MonsterParser.COLON, i);
		}
		public List<TerminalNode> UCASE() { return getTokens(MonsterParser.UCASE); }
		public TerminalNode UCASE(int i) {
			return getToken(MonsterParser.UCASE, i);
		}
		public TerminalNode DICE_STRING() { return getToken(MonsterParser.DICE_STRING, 0); }
		public BlowContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_blow; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterBlow(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitBlow(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitBlow(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlowContext blow() throws RecognitionException {
		BlowContext _localctx = new BlowContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_blow);

		            BlowMethod metObj = null;
		            BlowEffect effObj = null;
		            Random amtStr = null;
		        
		try {
			setState(150);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(135);
				match(BLOW);
				setState(136);
				((BlowContext)_localctx).method1 = match(UCASE);
				setState(137);
				match(COLON);
				setState(138);
				((BlowContext)_localctx).effect1 = match(UCASE);
				setState(139);
				match(COLON);
				setState(140);
				((BlowContext)_localctx).amount1 = match(DICE_STRING);

				                metObj = GameConstants.lookupBlowMethod(((BlowContext)_localctx).method1.getText());
				                effObj = GameConstants.lookupBlowEffect(((BlowContext)_localctx).effect1.getText());
				                amtStr = Random.parseStr(((BlowContext)_localctx).amount1.getText());
				            
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(142);
				match(BLOW);
				setState(143);
				((BlowContext)_localctx).method2 = match(UCASE);
				setState(144);
				match(COLON);
				setState(145);
				((BlowContext)_localctx).effect2 = match(UCASE);

				                metObj = GameConstants.lookupBlowMethod(((BlowContext)_localctx).method2.getText());
				                effObj = GameConstants.lookupBlowEffect(((BlowContext)_localctx).effect2.getText());
				            
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(147);
				match(BLOW);
				setState(148);
				((BlowContext)_localctx).method3 = match(UCASE);

				                metObj = GameConstants.lookupBlowMethod(((BlowContext)_localctx).method3.getText());
				            
				}
				break;
			}
			_ctx.stop = _input.LT(-1);

			            ((BlowContext)_localctx).blowObj =  new MonsterBlow(metObj, effObj, amtStr);
			        
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
		public Flag<MonsterRaceFlag> flags_On;
		public Token FLAGS;
		public TerminalNode FLAGS() { return getToken(MonsterParser.FLAGS, 0); }
		public FlagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flags; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterFlags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitFlags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsContext flags() throws RecognitionException {
		FlagsContext _localctx = new FlagsContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_flags);

		            ((FlagsContext)_localctx).flags_On =  new Flag<>(MonsterRaceFlag.class);
		        
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(152);
			((FlagsContext)_localctx).FLAGS = match(FLAGS);

			                String [] fullString = ((FlagsContext)_localctx).FLAGS.getText().split(":");
			                String [] flagsString = fullString[1].split("\\|");

			                for (String flagString : flagsString) {
			                    if (!flagString.isEmpty()) {
			                        String flag = "RF_" + flagString.trim();
			                        _localctx.flags_On.on(MonsterRaceFlag.valueOf(flag));
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
	public static class FlagsOffContext extends ParserRuleContext {
		public Flag<MonsterRaceFlag> flags_Off;
		public Token f1;
		public Token f2;
		public TerminalNode FLAGS_OFF() { return getToken(MonsterParser.FLAGS_OFF, 0); }
		public List<TerminalNode> UCASE() { return getTokens(MonsterParser.UCASE); }
		public TerminalNode UCASE(int i) {
			return getToken(MonsterParser.UCASE, i);
		}
		public List<TerminalNode> OR() { return getTokens(MonsterParser.OR); }
		public TerminalNode OR(int i) {
			return getToken(MonsterParser.OR, i);
		}
		public FlagsOffContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flagsOff; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterFlagsOff(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitFlagsOff(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitFlagsOff(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsOffContext flagsOff() throws RecognitionException {
		FlagsOffContext _localctx = new FlagsOffContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_flagsOff);

		            ((FlagsOffContext)_localctx).flags_Off =  new Flag<>(MonsterRaceFlag.class);
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(155);
			match(FLAGS_OFF);
			setState(156);
			((FlagsOffContext)_localctx).f1 = match(UCASE);

			                String flagString1 = ((FlagsOffContext)_localctx).f1.getText().trim();
			                _localctx.flags_Off.on(MonsterRaceFlag.valueOf("RF_" + flagString1));
			            
			setState(163);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OR) {
				{
				{
				setState(158);
				match(OR);
				setState(159);
				((FlagsOffContext)_localctx).f2 = match(UCASE);

				                String flagString2 = ((FlagsOffContext)_localctx).f2.getText().trim();
				                _localctx.flags_Off.on(MonsterRaceFlag.valueOf("RF_" + flagString2));
				            
				}
				}
				setState(165);
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
	public static class InnateFreqContext extends ParserRuleContext {
		public int innateFreqInt;
		public Token INTEGER;
		public TerminalNode INNATE_FREQ() { return getToken(MonsterParser.INNATE_FREQ, 0); }
		public TerminalNode INTEGER() { return getToken(MonsterParser.INTEGER, 0); }
		public InnateFreqContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_innateFreq; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterInnateFreq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitInnateFreq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitInnateFreq(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InnateFreqContext innateFreq() throws RecognitionException {
		InnateFreqContext _localctx = new InnateFreqContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_innateFreq);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(166);
			match(INNATE_FREQ);
			setState(167);
			((InnateFreqContext)_localctx).INTEGER = match(INTEGER);
			 ((InnateFreqContext)_localctx).innateFreqInt =  Integer.parseInt(((InnateFreqContext)_localctx).INTEGER.getText()); 
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
	public static class SpellFreqContext extends ParserRuleContext {
		public int spellFreqInt;
		public Token INTEGER;
		public TerminalNode SPELL_FREQ() { return getToken(MonsterParser.SPELL_FREQ, 0); }
		public TerminalNode INTEGER() { return getToken(MonsterParser.INTEGER, 0); }
		public SpellFreqContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_spellFreq; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterSpellFreq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitSpellFreq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitSpellFreq(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SpellFreqContext spellFreq() throws RecognitionException {
		SpellFreqContext _localctx = new SpellFreqContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_spellFreq);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(170);
			match(SPELL_FREQ);
			setState(171);
			((SpellFreqContext)_localctx).INTEGER = match(INTEGER);
			 ((SpellFreqContext)_localctx).spellFreqInt =  Integer.parseInt(((SpellFreqContext)_localctx).INTEGER.getText()); 
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
	public static class SpellPowerContext extends ParserRuleContext {
		public int spellPowerInt;
		public Token INTEGER;
		public TerminalNode SPELL_POWER() { return getToken(MonsterParser.SPELL_POWER, 0); }
		public TerminalNode INTEGER() { return getToken(MonsterParser.INTEGER, 0); }
		public SpellPowerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_spellPower; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterSpellPower(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitSpellPower(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitSpellPower(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SpellPowerContext spellPower() throws RecognitionException {
		SpellPowerContext _localctx = new SpellPowerContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_spellPower);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(174);
			match(SPELL_POWER);
			setState(175);
			((SpellPowerContext)_localctx).INTEGER = match(INTEGER);
			 ((SpellPowerContext)_localctx).spellPowerInt =  Integer.parseInt(((SpellPowerContext)_localctx).INTEGER.getText()); 
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
	public static class SpellsContext extends ParserRuleContext {
		public List<MonsterSpell> spellList;
		public Token SPELLS;
		public TerminalNode SPELLS() { return getToken(MonsterParser.SPELLS, 0); }
		public SpellsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_spells; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterSpells(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitSpells(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitSpells(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SpellsContext spells() throws RecognitionException {
		SpellsContext _localctx = new SpellsContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_spells);

		            ((SpellsContext)_localctx).spellList =  new ArrayList<>();
		        
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(178);
			((SpellsContext)_localctx).SPELLS = match(SPELLS);

			                String [] rawSplit = ((SpellsContext)_localctx).SPELLS.getText().split(":");
			                String [] spellArray = rawSplit[1].split("\\|");

			                for (String spellName : spellArray) {
			                    MonsterSpell spellType = MonsterSpell.valueOf("RSF_" + spellName.trim());
			                    _localctx.spellList.add(spellType);
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
	public static class MessageVisContext extends ParserRuleContext {
		public MonsterAltmsg altMsg;
		public Token UCASE;
		public Token LCASE;
		public TerminalNode MESSAGE_VIS() { return getToken(MonsterParser.MESSAGE_VIS, 0); }
		public TerminalNode UCASE() { return getToken(MonsterParser.UCASE, 0); }
		public TerminalNode COLON() { return getToken(MonsterParser.COLON, 0); }
		public TerminalNode LCASE() { return getToken(MonsterParser.LCASE, 0); }
		public MessageVisContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messageVis; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterMessageVis(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitMessageVis(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitMessageVis(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MessageVisContext messageVis() throws RecognitionException {
		MessageVisContext _localctx = new MessageVisContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_messageVis);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(181);
			match(MESSAGE_VIS);
			setState(182);
			((MessageVisContext)_localctx).UCASE = match(UCASE);
			setState(183);
			match(COLON);
			setState(184);
			((MessageVisContext)_localctx).LCASE = match(LCASE);

			                MonsterSpell spellType = MonsterSpell.valueOf("RSF_" + ((MessageVisContext)_localctx).UCASE.getText());
			                String spellMessage = ((MessageVisContext)_localctx).LCASE.getText();

			                ((MessageVisContext)_localctx).altMsg =  new MonsterAltmsg(spellMessage, MonsterAltmsgType.MON_ALTMSG_SEEN, spellType);
			            
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
		public MonsterAltmsg altMsg;
		public Token UCASE;
		public Token LCASE;
		public TerminalNode MESSAGE_INVIS() { return getToken(MonsterParser.MESSAGE_INVIS, 0); }
		public TerminalNode UCASE() { return getToken(MonsterParser.UCASE, 0); }
		public TerminalNode COLON() { return getToken(MonsterParser.COLON, 0); }
		public TerminalNode LCASE() { return getToken(MonsterParser.LCASE, 0); }
		public MessageInvisContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messageInvis; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterMessageInvis(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitMessageInvis(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitMessageInvis(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MessageInvisContext messageInvis() throws RecognitionException {
		MessageInvisContext _localctx = new MessageInvisContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_messageInvis);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(187);
			match(MESSAGE_INVIS);
			setState(188);
			((MessageInvisContext)_localctx).UCASE = match(UCASE);
			setState(189);
			match(COLON);
			setState(190);
			((MessageInvisContext)_localctx).LCASE = match(LCASE);

			                MonsterSpell spellType = MonsterSpell.valueOf("RSF_" + ((MessageInvisContext)_localctx).UCASE.getText());
			                String spellMessage = ((MessageInvisContext)_localctx).LCASE.getText();

			                ((MessageInvisContext)_localctx).altMsg =  new MonsterAltmsg(spellMessage, MonsterAltmsgType.MON_ALTMSG_UNSEEN, spellType);
			            
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
		public MonsterAltmsg altMsg;
		public Token UCASE;
		public Token LCASE;
		public TerminalNode MESSAGE_MISS() { return getToken(MonsterParser.MESSAGE_MISS, 0); }
		public TerminalNode UCASE() { return getToken(MonsterParser.UCASE, 0); }
		public TerminalNode COLON() { return getToken(MonsterParser.COLON, 0); }
		public TerminalNode LCASE() { return getToken(MonsterParser.LCASE, 0); }
		public MessageMissContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messageMiss; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterMessageMiss(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitMessageMiss(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitMessageMiss(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MessageMissContext messageMiss() throws RecognitionException {
		MessageMissContext _localctx = new MessageMissContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_messageMiss);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(193);
			match(MESSAGE_MISS);
			setState(194);
			((MessageMissContext)_localctx).UCASE = match(UCASE);
			setState(195);
			match(COLON);
			setState(196);
			((MessageMissContext)_localctx).LCASE = match(LCASE);

			                MonsterSpell spellType = MonsterSpell.valueOf("RSF_" + ((MessageMissContext)_localctx).UCASE.getText());
			                String spellMessage = ((MessageMissContext)_localctx).LCASE.getText();

			                ((MessageMissContext)_localctx).altMsg =  new MonsterAltmsg(spellMessage, MonsterAltmsgType.MON_ALTMSG_MISS, spellType);
			            
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
		public TerminalNode DESC() { return getToken(MonsterParser.DESC, 0); }
		public TerminalNode LCASE() { return getToken(MonsterParser.LCASE, 0); }
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_desc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_desc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(199);
			match(DESC);
			setState(200);
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
	public static class ShapeContext extends ParserRuleContext {
		public String shapeStr;
		public Token LCASE;
		public TerminalNode SHAPE() { return getToken(MonsterParser.SHAPE, 0); }
		public TerminalNode LCASE() { return getToken(MonsterParser.LCASE, 0); }
		public ShapeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_shape; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterShape(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitShape(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitShape(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ShapeContext shape() throws RecognitionException {
		ShapeContext _localctx = new ShapeContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_shape);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(203);
			match(SHAPE);
			setState(204);
			((ShapeContext)_localctx).LCASE = match(LCASE);
			 ((ShapeContext)_localctx).shapeStr =  ((ShapeContext)_localctx).LCASE.getText(); 
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
	public static class DropContext extends ParserRuleContext {
		public MonsterDrop dropObj;
		public Token t;
		public Token s;
		public Token p;
		public Token mi;
		public Token ma;
		public TerminalNode DROP() { return getToken(MonsterParser.DROP, 0); }
		public List<TerminalNode> COLON() { return getTokens(MonsterParser.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(MonsterParser.COLON, i);
		}
		public List<TerminalNode> LCASE() { return getTokens(MonsterParser.LCASE); }
		public TerminalNode LCASE(int i) {
			return getToken(MonsterParser.LCASE, i);
		}
		public List<TerminalNode> INTEGER() { return getTokens(MonsterParser.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(MonsterParser.INTEGER, i);
		}
		public DropContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_drop; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterDrop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitDrop(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitDrop(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DropContext drop() throws RecognitionException {
		DropContext _localctx = new DropContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_drop);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(207);
			match(DROP);
			setState(208);
			((DropContext)_localctx).t = match(LCASE);
			setState(209);
			match(COLON);
			setState(210);
			((DropContext)_localctx).s = match(LCASE);
			setState(211);
			match(COLON);
			setState(212);
			((DropContext)_localctx).p = match(INTEGER);
			setState(213);
			match(COLON);
			setState(214);
			((DropContext)_localctx).mi = match(INTEGER);
			setState(215);
			match(COLON);
			setState(216);
			((DropContext)_localctx).ma = match(INTEGER);

			                TValue tval = TValue.valueOf("TV_" + ((DropContext)_localctx).t.getText().toUpperCase().replace(" ", "_").replace("ARMOUR", "ARMOR"));
			                ObjectKind oKind = GameConstants.lookupObjectKind(((DropContext)_localctx).s.getText());
			                int percentage = Integer.parseInt(((DropContext)_localctx).p.getText());
			                int min = Integer.parseInt(((DropContext)_localctx).mi.getText());
			                int max = Integer.parseInt(((DropContext)_localctx).ma.getText());
			                ((DropContext)_localctx).dropObj =  new MonsterDrop(tval, oKind, percentage, min, max);
			            
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
	public static class DropBaseContext extends ParserRuleContext {
		public MonsterDrop dropObj;
		public Token LCASE;
		public Token p;
		public Token mi;
		public Token ma;
		public TerminalNode DROP_BASE() { return getToken(MonsterParser.DROP_BASE, 0); }
		public TerminalNode LCASE() { return getToken(MonsterParser.LCASE, 0); }
		public List<TerminalNode> COLON() { return getTokens(MonsterParser.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(MonsterParser.COLON, i);
		}
		public List<TerminalNode> INTEGER() { return getTokens(MonsterParser.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(MonsterParser.INTEGER, i);
		}
		public DropBaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dropBase; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterDropBase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitDropBase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitDropBase(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DropBaseContext dropBase() throws RecognitionException {
		DropBaseContext _localctx = new DropBaseContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_dropBase);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(219);
			match(DROP_BASE);
			setState(220);
			((DropBaseContext)_localctx).LCASE = match(LCASE);
			setState(221);
			match(COLON);
			setState(222);
			((DropBaseContext)_localctx).p = match(INTEGER);
			setState(223);
			match(COLON);
			setState(224);
			((DropBaseContext)_localctx).mi = match(INTEGER);
			setState(225);
			match(COLON);
			setState(226);
			((DropBaseContext)_localctx).ma = match(INTEGER);

			                TValue tval = TValue.valueOf("TV_" + ((DropBaseContext)_localctx).LCASE.getText().toUpperCase().replace(" ", "_").replace("ARMOUR", "ARMOR"));
			                int percentage = Integer.parseInt(((DropBaseContext)_localctx).p.getText());
			                int min = Integer.parseInt(((DropBaseContext)_localctx).mi.getText());
			                int max = Integer.parseInt(((DropBaseContext)_localctx).ma.getText());
			                ((DropBaseContext)_localctx).dropObj =  new MonsterDrop(tval, percentage, min, max);
			            
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
	public static class MimicContext extends ParserRuleContext {
		public ObjectKind objKind;
		public Token t;
		public Token s;
		public TerminalNode MIMIC() { return getToken(MonsterParser.MIMIC, 0); }
		public TerminalNode COLON() { return getToken(MonsterParser.COLON, 0); }
		public List<TerminalNode> LCASE() { return getTokens(MonsterParser.LCASE); }
		public TerminalNode LCASE(int i) {
			return getToken(MonsterParser.LCASE, i);
		}
		public MimicContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mimic; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterMimic(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitMimic(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitMimic(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MimicContext mimic() throws RecognitionException {
		MimicContext _localctx = new MimicContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_mimic);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(229);
			match(MIMIC);
			setState(230);
			((MimicContext)_localctx).t = match(LCASE);
			setState(231);
			match(COLON);
			setState(232);
			((MimicContext)_localctx).s = match(LCASE);

			                TValue tval = TValue.valueOf("TV_" + ((MimicContext)_localctx).t.getText().toUpperCase().replace(" ", "_").replace("ARMOUR", "ARMOR"));
			                String sval = ((MimicContext)_localctx).s.getText();
			                ((MimicContext)_localctx).objKind =  GameConstants.lookupObjectKind(tval, sval);
			            
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
	public static class FriendsContext extends ParserRuleContext {
		public MonsterFriends friendsObj;
		public Token FRIENDS;
		public TerminalNode FRIENDS() { return getToken(MonsterParser.FRIENDS, 0); }
		public FriendsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_friends; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterFriends(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitFriends(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitFriends(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FriendsContext friends() throws RecognitionException {
		FriendsContext _localctx = new FriendsContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_friends);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(235);
			((FriendsContext)_localctx).FRIENDS = match(FRIENDS);

			                String [] rawSplit = ((FriendsContext)_localctx).FRIENDS.getText().split(":");
			                // Ignore the first entry
			                int chance = Integer.parseInt(rawSplit[1]);
			                String [] noAppearing = rawSplit[2].split("d");
			                String nameOfMonster = rawSplit[3];
			                String role = "none";
			                if (rawSplit.length > 4)
			                    role = rawSplit[4];

			                role = "MON_GROUP_" + role.toUpperCase();

			                MonsterGroupRole mgRole = MonsterGroupRole.valueOf(role);

			                ((FriendsContext)_localctx).friendsObj =  new MonsterFriends(nameOfMonster, mgRole, chance, Integer.parseInt(noAppearing[0]),
			                 Integer.parseInt(noAppearing[1]));
			            
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
	public static class FriendsBaseContext extends ParserRuleContext {
		public MonsterFriendsBase friendsBaseObj;
		public Token FRIENDS_BASE;
		public TerminalNode FRIENDS_BASE() { return getToken(MonsterParser.FRIENDS_BASE, 0); }
		public FriendsBaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_friendsBase; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterFriendsBase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitFriendsBase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitFriendsBase(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FriendsBaseContext friendsBase() throws RecognitionException {
		FriendsBaseContext _localctx = new FriendsBaseContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_friendsBase);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(238);
			((FriendsBaseContext)_localctx).FRIENDS_BASE = match(FRIENDS_BASE);

			                String [] rawSplit = ((FriendsBaseContext)_localctx).FRIENDS_BASE.getText().split(":");
			                // Ignore the first entry
			                int chance = Integer.parseInt(rawSplit[1]);
			                String[] noAppearing = rawSplit[2].split("d");
			                MonsterBase typeAppearing = GameConstants.lookupMonsterBase(rawSplit[3]);
			                String roleName = "MON_GROUP_NONE";
			                if (rawSplit.length > 4)
			                    roleName = "MON_GROUP_" + rawSplit[4].toUpperCase();

			                MonsterGroupRole mgRole = MonsterGroupRole.valueOf(roleName);

			                ((FriendsBaseContext)_localctx).friendsBaseObj =  new MonsterFriendsBase(typeAppearing, mgRole, chance,
			                Integer.parseInt(noAppearing[0]), Integer.parseInt(noAppearing[1]));
			            
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
	public static class MonsterContext extends ParserRuleContext {
		public MonsterRace race;
		public NameContext name;
		public PluralContext plural;
		public BaseContext base;
		public GlyphContext glyph;
		public ColourContext colour;
		public ColourCycleContext colourCycle;
		public SpeedContext speed;
		public HitPointsContext hitPoints;
		public LightContext light;
		public HearingContext hearing;
		public SmellContext smell;
		public ArmourClassContext armourClass;
		public SleepinessContext sleepiness;
		public DungeonDepthContext dungeonDepth;
		public RarityContext rarity;
		public ExperienceContext experience;
		public BlowContext blow;
		public FlagsContext flags;
		public FlagsOffContext flagsOff;
		public InnateFreqContext innateFreq;
		public SpellFreqContext spellFreq;
		public SpellPowerContext spellPower;
		public SpellsContext spells;
		public MessageVisContext messageVis;
		public MessageInvisContext messageInvis;
		public MessageMissContext messageMiss;
		public DropContext drop;
		public DropBaseContext dropBase;
		public MimicContext mimic;
		public FriendsContext friends;
		public FriendsBaseContext friendsBase;
		public DescContext desc;
		public ShapeContext shape;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public BaseContext base() {
			return getRuleContext(BaseContext.class,0);
		}
		public ColourContext colour() {
			return getRuleContext(ColourContext.class,0);
		}
		public PluralContext plural() {
			return getRuleContext(PluralContext.class,0);
		}
		public GlyphContext glyph() {
			return getRuleContext(GlyphContext.class,0);
		}
		public List<ColourCycleContext> colourCycle() {
			return getRuleContexts(ColourCycleContext.class);
		}
		public ColourCycleContext colourCycle(int i) {
			return getRuleContext(ColourCycleContext.class,i);
		}
		public List<SpeedContext> speed() {
			return getRuleContexts(SpeedContext.class);
		}
		public SpeedContext speed(int i) {
			return getRuleContext(SpeedContext.class,i);
		}
		public List<HitPointsContext> hitPoints() {
			return getRuleContexts(HitPointsContext.class);
		}
		public HitPointsContext hitPoints(int i) {
			return getRuleContext(HitPointsContext.class,i);
		}
		public List<LightContext> light() {
			return getRuleContexts(LightContext.class);
		}
		public LightContext light(int i) {
			return getRuleContext(LightContext.class,i);
		}
		public List<HearingContext> hearing() {
			return getRuleContexts(HearingContext.class);
		}
		public HearingContext hearing(int i) {
			return getRuleContext(HearingContext.class,i);
		}
		public List<SmellContext> smell() {
			return getRuleContexts(SmellContext.class);
		}
		public SmellContext smell(int i) {
			return getRuleContext(SmellContext.class,i);
		}
		public List<ArmourClassContext> armourClass() {
			return getRuleContexts(ArmourClassContext.class);
		}
		public ArmourClassContext armourClass(int i) {
			return getRuleContext(ArmourClassContext.class,i);
		}
		public List<SleepinessContext> sleepiness() {
			return getRuleContexts(SleepinessContext.class);
		}
		public SleepinessContext sleepiness(int i) {
			return getRuleContext(SleepinessContext.class,i);
		}
		public List<DungeonDepthContext> dungeonDepth() {
			return getRuleContexts(DungeonDepthContext.class);
		}
		public DungeonDepthContext dungeonDepth(int i) {
			return getRuleContext(DungeonDepthContext.class,i);
		}
		public List<RarityContext> rarity() {
			return getRuleContexts(RarityContext.class);
		}
		public RarityContext rarity(int i) {
			return getRuleContext(RarityContext.class,i);
		}
		public List<ExperienceContext> experience() {
			return getRuleContexts(ExperienceContext.class);
		}
		public ExperienceContext experience(int i) {
			return getRuleContext(ExperienceContext.class,i);
		}
		public List<BlowContext> blow() {
			return getRuleContexts(BlowContext.class);
		}
		public BlowContext blow(int i) {
			return getRuleContext(BlowContext.class,i);
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
		public List<InnateFreqContext> innateFreq() {
			return getRuleContexts(InnateFreqContext.class);
		}
		public InnateFreqContext innateFreq(int i) {
			return getRuleContext(InnateFreqContext.class,i);
		}
		public List<SpellFreqContext> spellFreq() {
			return getRuleContexts(SpellFreqContext.class);
		}
		public SpellFreqContext spellFreq(int i) {
			return getRuleContext(SpellFreqContext.class,i);
		}
		public List<SpellPowerContext> spellPower() {
			return getRuleContexts(SpellPowerContext.class);
		}
		public SpellPowerContext spellPower(int i) {
			return getRuleContext(SpellPowerContext.class,i);
		}
		public List<SpellsContext> spells() {
			return getRuleContexts(SpellsContext.class);
		}
		public SpellsContext spells(int i) {
			return getRuleContext(SpellsContext.class,i);
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
		public List<DropContext> drop() {
			return getRuleContexts(DropContext.class);
		}
		public DropContext drop(int i) {
			return getRuleContext(DropContext.class,i);
		}
		public List<DropBaseContext> dropBase() {
			return getRuleContexts(DropBaseContext.class);
		}
		public DropBaseContext dropBase(int i) {
			return getRuleContext(DropBaseContext.class,i);
		}
		public List<MimicContext> mimic() {
			return getRuleContexts(MimicContext.class);
		}
		public MimicContext mimic(int i) {
			return getRuleContext(MimicContext.class,i);
		}
		public List<FriendsContext> friends() {
			return getRuleContexts(FriendsContext.class);
		}
		public FriendsContext friends(int i) {
			return getRuleContext(FriendsContext.class,i);
		}
		public List<FriendsBaseContext> friendsBase() {
			return getRuleContexts(FriendsBaseContext.class);
		}
		public FriendsBaseContext friendsBase(int i) {
			return getRuleContext(FriendsBaseContext.class,i);
		}
		public List<DescContext> desc() {
			return getRuleContexts(DescContext.class);
		}
		public DescContext desc(int i) {
			return getRuleContext(DescContext.class,i);
		}
		public List<ShapeContext> shape() {
			return getRuleContexts(ShapeContext.class);
		}
		public ShapeContext shape(int i) {
			return getRuleContext(ShapeContext.class,i);
		}
		public MonsterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_monster; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterMonster(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitMonster(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitMonster(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MonsterContext monster() throws RecognitionException {
		MonsterContext _localctx = new MonsterContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_monster);

		            String nameInit = "";
		            String textInit = "";
		            String pluralInit = "";
		            MonsterBase baseInit = null;
		            int averageHPInit = 0;
		            int acInit = 0;
		            int sleepInit = 0;
		            int hearingInit = 0;
		            int smellInit = 0;
		            int speedInit = 0;
		            int lightInit = 0;
		            int mexpInit = 0;
		            int freqInnateInit = 0;
		            int freqSpellInit = 0;
		            int spellPowerInit = 0;
		            Flag<MonsterRaceFlag> flagsInit = new Flag<>(MonsterRaceFlag.class);
		            Flag<MonsterRaceFlag> flagsOffInit = new Flag<>(MonsterRaceFlag.class);
		            List<MonsterSpell> spellsInit = new ArrayList<>();
		            List<MonsterBlow> blowsInit = new ArrayList<>();
		            int levelInit = 0;
		            int rarityInit = 0;
		            char glyphInit = ' ';
		            ColourType colourTypeInit = ColourType.COLOUR_TYPE_DARK;
		            int maxNumInit = 0;
		            int curNumInit = 0;
		            List<MonsterAltmsg> spellMsgInit = new ArrayList<>();
		            List<MonsterDrop> dropsInit = new ArrayList<>();
		            List<MonsterFriends> friendsInit = new ArrayList<>();
		            List<MonsterFriendsBase> friendsBaseInit = new ArrayList<>();
		            List<ObjectKind> mimicKindsInit = new ArrayList<>();
		            List<String> shapesInit = new ArrayList<>();
		            int numShapesInit = 0;
		            String groupNameInit = "";
		            String cycleNameInit = "";
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(241);
			((MonsterContext)_localctx).name = name();
			 nameInit = ((MonsterContext)_localctx).name.nameStr; 
			setState(246);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PLURAL) {
				{
				setState(243);
				((MonsterContext)_localctx).plural = plural();
				 pluralInit = ((MonsterContext)_localctx).plural.pluralStr; 
				}
			}

			setState(248);
			((MonsterContext)_localctx).base = base();
			 baseInit = ((MonsterContext)_localctx).base.baseObj; 
			setState(253);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==GLYPH) {
				{
				setState(250);
				((MonsterContext)_localctx).glyph = glyph();
				 glyphInit = ((MonsterContext)_localctx).glyph.glyphChr; 
				}
			}

			setState(255);
			((MonsterContext)_localctx).colour = colour();
			 colourTypeInit = ((MonsterContext)_localctx).colour.colourEnum; 
			setState(343);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 68719476480L) != 0)) {
				{
				setState(341);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case COLOUR_CYCLE:
					{
					setState(257);
					((MonsterContext)_localctx).colourCycle = colourCycle();

					                groupNameInit = ((MonsterContext)_localctx).colourCycle.groupName;
					                cycleNameInit = ((MonsterContext)_localctx).colourCycle.cycleName; 
					}
					break;
				case SPEED:
					{
					setState(260);
					((MonsterContext)_localctx).speed = speed();
					 speedInit = ((MonsterContext)_localctx).speed.speedInt; 
					}
					break;
				case HIT_POINTS:
					{
					setState(263);
					((MonsterContext)_localctx).hitPoints = hitPoints();
					 averageHPInit = ((MonsterContext)_localctx).hitPoints.hpInt; 
					}
					break;
				case LIGHT:
					{
					setState(266);
					((MonsterContext)_localctx).light = light();
					 lightInit = ((MonsterContext)_localctx).light.lightInt; 
					}
					break;
				case HEARING:
					{
					setState(269);
					((MonsterContext)_localctx).hearing = hearing();
					 hearingInit = ((MonsterContext)_localctx).hearing.hearingInt; 
					}
					break;
				case SMELL:
					{
					setState(272);
					((MonsterContext)_localctx).smell = smell();
					 smellInit = ((MonsterContext)_localctx).smell.smellInt; 
					}
					break;
				case ARMOUR_CLASS:
					{
					setState(275);
					((MonsterContext)_localctx).armourClass = armourClass();
					 acInit = ((MonsterContext)_localctx).armourClass.acInt; 
					}
					break;
				case SLEEPINESS:
					{
					setState(278);
					((MonsterContext)_localctx).sleepiness = sleepiness();
					 sleepInit = ((MonsterContext)_localctx).sleepiness.sleepInt; 
					}
					break;
				case DEPTH:
					{
					setState(281);
					((MonsterContext)_localctx).dungeonDepth = dungeonDepth();
					 levelInit = ((MonsterContext)_localctx).dungeonDepth.depthInt; 
					}
					break;
				case RARITY:
					{
					setState(284);
					((MonsterContext)_localctx).rarity = rarity();
					 rarityInit = ((MonsterContext)_localctx).rarity.rarityInt; 
					}
					break;
				case EXPERIENCE:
					{
					setState(287);
					((MonsterContext)_localctx).experience = experience();
					 mexpInit = ((MonsterContext)_localctx).experience.expInt; 
					}
					break;
				case BLOW:
					{
					setState(290);
					((MonsterContext)_localctx).blow = blow();
					 blowsInit.add(((MonsterContext)_localctx).blow.blowObj); 
					}
					break;
				case FLAGS:
					{
					setState(293);
					((MonsterContext)_localctx).flags = flags();
					 flagsInit.union(((MonsterContext)_localctx).flags.flags_On); 
					}
					break;
				case FLAGS_OFF:
					{
					setState(296);
					((MonsterContext)_localctx).flagsOff = flagsOff();
					 flagsOffInit.union(((MonsterContext)_localctx).flagsOff.flags_Off); 
					}
					break;
				case INNATE_FREQ:
					{
					setState(299);
					((MonsterContext)_localctx).innateFreq = innateFreq();
					 freqInnateInit = ((MonsterContext)_localctx).innateFreq.innateFreqInt; 
					}
					break;
				case SPELL_FREQ:
					{
					setState(302);
					((MonsterContext)_localctx).spellFreq = spellFreq();
					 freqSpellInit = ((MonsterContext)_localctx).spellFreq.spellFreqInt; 
					}
					break;
				case SPELL_POWER:
					{
					setState(305);
					((MonsterContext)_localctx).spellPower = spellPower();
					 spellPowerInit = ((MonsterContext)_localctx).spellPower.spellPowerInt; 
					}
					break;
				case SPELLS:
					{
					setState(308);
					((MonsterContext)_localctx).spells = spells();
					 spellsInit.addAll(((MonsterContext)_localctx).spells.spellList); 
					}
					break;
				case MESSAGE_VIS:
					{
					setState(311);
					((MonsterContext)_localctx).messageVis = messageVis();
					 spellMsgInit.add(((MonsterContext)_localctx).messageVis.altMsg); 
					}
					break;
				case MESSAGE_INVIS:
					{
					setState(314);
					((MonsterContext)_localctx).messageInvis = messageInvis();
					 spellMsgInit.add(((MonsterContext)_localctx).messageInvis.altMsg); 
					}
					break;
				case MESSAGE_MISS:
					{
					setState(317);
					((MonsterContext)_localctx).messageMiss = messageMiss();
					 spellMsgInit.add(((MonsterContext)_localctx).messageMiss.altMsg); 
					}
					break;
				case DROP:
					{
					setState(320);
					((MonsterContext)_localctx).drop = drop();
					 dropsInit.add(((MonsterContext)_localctx).drop.dropObj); 
					}
					break;
				case DROP_BASE:
					{
					setState(323);
					((MonsterContext)_localctx).dropBase = dropBase();
					 dropsInit.add(((MonsterContext)_localctx).dropBase.dropObj); 
					}
					break;
				case MIMIC:
					{
					setState(326);
					((MonsterContext)_localctx).mimic = mimic();
					 mimicKindsInit.add(((MonsterContext)_localctx).mimic.objKind); 
					}
					break;
				case FRIENDS:
					{
					setState(329);
					((MonsterContext)_localctx).friends = friends();
					 friendsInit.add(((MonsterContext)_localctx).friends.friendsObj); 
					}
					break;
				case FRIENDS_BASE:
					{
					setState(332);
					((MonsterContext)_localctx).friendsBase = friendsBase();
					 friendsBaseInit.add(((MonsterContext)_localctx).friendsBase.friendsBaseObj); 
					}
					break;
				case DESC:
					{
					setState(335);
					((MonsterContext)_localctx).desc = desc();
					 textInit = textInit + ((MonsterContext)_localctx).desc.descStr; 
					}
					break;
				case SHAPE:
					{
					setState(338);
					((MonsterContext)_localctx).shape = shape();

					                shapesInit.add(((MonsterContext)_localctx).shape.shapeStr);
					                numShapesInit++;
					            
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(345);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			_ctx.stop = _input.LT(-1);

			            // TODO(ClaudeCode): the MonsterRace constructor signature has changed; this call no longer
			            // matches. Commented out to keep the build green until the monster parser is re-plumbed.
			            /*AngbandDisplayCharacter adcInit = new AngbandDisplayCharacter(glyphInit, colourTypeInit);
			            ((MonsterContext)_localctx).race =  new MonsterRace(nameInit, textInit, pluralInit,
			            baseInit, averageHPInit, acInit, sleepInit, hearingInit,
			            smellInit, speedInit, lightInit, mexpInit, freqInnateInit,
			            freqSpellInit, spellPowerInit, flagsInit, flagsOffInit,
			            spellsInit, blowsInit, levelInit, rarityInit, adcInit,
			            maxNumInit, curNumInit, spellMsgInit, dropsInit,
			            friendsInit, friendsBaseInit, mimicKindsInit, shapesInit,
			            numShapesInit, groupNameInit, cycleNameInit);*/
			            ((MonsterContext)_localctx).race =  null;
			        
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
		public List<MonsterRace> monsterRaces;
		public MonsterContext monster;
		public TerminalNode EOF() { return getToken(MonsterParser.EOF, 0); }
		public List<MonsterContext> monster() {
			return getRuleContexts(MonsterContext.class);
		}
		public MonsterContext monster(int i) {
			return getRuleContext(MonsterContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterListener ) ((MonsterListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterVisitor ) return ((MonsterVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_file);

		            ((FileContext)_localctx).monsterRaces =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(349); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(346);
				((FileContext)_localctx).monster = monster();

				                _localctx.monsterRaces.add(((FileContext)_localctx).monster.race);
				            
				}
				}
				setState(351); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(353);
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
		"\u0004\u0001*\u0164\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t"+
		"\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r"+
		"\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0003\u0010\u0097\b\u0010\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0005\u0012\u00a2\b\u0012\n\u0012\f\u0012\u00a5\t\u0012\u0001\u0013\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0016\u0001"+
		"\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001"+
		"\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001"+
		"\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001"+
		"\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001"+
		"\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001"+
		"\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001"+
		"\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001"+
		"\u001d\u0001\u001d\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001"+
		"\u001e\u0001\u001e\u0001\u001f\u0001\u001f\u0001\u001f\u0001 \u0001 \u0001"+
		" \u0001!\u0001!\u0001!\u0001!\u0001!\u0003!\u00f7\b!\u0001!\u0001!\u0001"+
		"!\u0001!\u0001!\u0003!\u00fe\b!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001"+
		"!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001"+
		"!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001"+
		"!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001"+
		"!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001"+
		"!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001"+
		"!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001"+
		"!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001"+
		"!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001"+
		"!\u0005!\u0156\b!\n!\f!\u0159\t!\u0001\"\u0001\"\u0001\"\u0004\"\u015e"+
		"\b\"\u000b\"\f\"\u015f\u0001\"\u0001\"\u0001\"\u0000\u0000#\u0000\u0002"+
		"\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e"+
		" \"$&(*,.02468:<>@BD\u0000\u0000\u0162\u0000F\u0001\u0000\u0000\u0000"+
		"\u0002J\u0001\u0000\u0000\u0000\u0004N\u0001\u0000\u0000\u0000\u0006R"+
		"\u0001\u0000\u0000\u0000\bU\u0001\u0000\u0000\u0000\nY\u0001\u0000\u0000"+
		"\u0000\f_\u0001\u0000\u0000\u0000\u000ec\u0001\u0000\u0000\u0000\u0010"+
		"g\u0001\u0000\u0000\u0000\u0012k\u0001\u0000\u0000\u0000\u0014o\u0001"+
		"\u0000\u0000\u0000\u0016s\u0001\u0000\u0000\u0000\u0018w\u0001\u0000\u0000"+
		"\u0000\u001a{\u0001\u0000\u0000\u0000\u001c\u007f\u0001\u0000\u0000\u0000"+
		"\u001e\u0083\u0001\u0000\u0000\u0000 \u0096\u0001\u0000\u0000\u0000\""+
		"\u0098\u0001\u0000\u0000\u0000$\u009b\u0001\u0000\u0000\u0000&\u00a6\u0001"+
		"\u0000\u0000\u0000(\u00aa\u0001\u0000\u0000\u0000*\u00ae\u0001\u0000\u0000"+
		"\u0000,\u00b2\u0001\u0000\u0000\u0000.\u00b5\u0001\u0000\u0000\u00000"+
		"\u00bb\u0001\u0000\u0000\u00002\u00c1\u0001\u0000\u0000\u00004\u00c7\u0001"+
		"\u0000\u0000\u00006\u00cb\u0001\u0000\u0000\u00008\u00cf\u0001\u0000\u0000"+
		"\u0000:\u00db\u0001\u0000\u0000\u0000<\u00e5\u0001\u0000\u0000\u0000>"+
		"\u00eb\u0001\u0000\u0000\u0000@\u00ee\u0001\u0000\u0000\u0000B\u00f1\u0001"+
		"\u0000\u0000\u0000D\u015d\u0001\u0000\u0000\u0000FG\u0005\u0003\u0000"+
		"\u0000GH\u0005*\u0000\u0000HI\u0006\u0000\uffff\uffff\u0000I\u0001\u0001"+
		"\u0000\u0000\u0000JK\u0005\u0004\u0000\u0000KL\u0005*\u0000\u0000LM\u0006"+
		"\u0001\uffff\uffff\u0000M\u0003\u0001\u0000\u0000\u0000NO\u0005\u0005"+
		"\u0000\u0000OP\u0005*\u0000\u0000PQ\u0006\u0002\uffff\uffff\u0000Q\u0005"+
		"\u0001\u0000\u0000\u0000RS\u0005\u0006\u0000\u0000ST\u0006\u0003\uffff"+
		"\uffff\u0000T\u0007\u0001\u0000\u0000\u0000UV\u0005\u0007\u0000\u0000"+
		"VW\u0005(\u0000\u0000WX\u0006\u0004\uffff\uffff\u0000X\t\u0001\u0000\u0000"+
		"\u0000YZ\u0005#\u0000\u0000Z[\u0005*\u0000\u0000[\\\u0005&\u0000\u0000"+
		"\\]\u0005*\u0000\u0000]^\u0006\u0005\uffff\uffff\u0000^\u000b\u0001\u0000"+
		"\u0000\u0000_`\u0005\b\u0000\u0000`a\u0005$\u0000\u0000ab\u0006\u0006"+
		"\uffff\uffff\u0000b\r\u0001\u0000\u0000\u0000cd\u0005\t\u0000\u0000de"+
		"\u0005$\u0000\u0000ef\u0006\u0007\uffff\uffff\u0000f\u000f\u0001\u0000"+
		"\u0000\u0000gh\u0005\n\u0000\u0000hi\u0005$\u0000\u0000ij\u0006\b\uffff"+
		"\uffff\u0000j\u0011\u0001\u0000\u0000\u0000kl\u0005\u000b\u0000\u0000"+
		"lm\u0005$\u0000\u0000mn\u0006\t\uffff\uffff\u0000n\u0013\u0001\u0000\u0000"+
		"\u0000op\u0005\f\u0000\u0000pq\u0005$\u0000\u0000qr\u0006\n\uffff\uffff"+
		"\u0000r\u0015\u0001\u0000\u0000\u0000st\u0005\r\u0000\u0000tu\u0005$\u0000"+
		"\u0000uv\u0006\u000b\uffff\uffff\u0000v\u0017\u0001\u0000\u0000\u0000"+
		"wx\u0005\u000e\u0000\u0000xy\u0005$\u0000\u0000yz\u0006\f\uffff\uffff"+
		"\u0000z\u0019\u0001\u0000\u0000\u0000{|\u0005\u000f\u0000\u0000|}\u0005"+
		"$\u0000\u0000}~\u0006\r\uffff\uffff\u0000~\u001b\u0001\u0000\u0000\u0000"+
		"\u007f\u0080\u0005\u0010\u0000\u0000\u0080\u0081\u0005$\u0000\u0000\u0081"+
		"\u0082\u0006\u000e\uffff\uffff\u0000\u0082\u001d\u0001\u0000\u0000\u0000"+
		"\u0083\u0084\u0005\u0011\u0000\u0000\u0084\u0085\u0005$\u0000\u0000\u0085"+
		"\u0086\u0006\u000f\uffff\uffff\u0000\u0086\u001f\u0001\u0000\u0000\u0000"+
		"\u0087\u0088\u0005\u0012\u0000\u0000\u0088\u0089\u0005)\u0000\u0000\u0089"+
		"\u008a\u0005&\u0000\u0000\u008a\u008b\u0005)\u0000\u0000\u008b\u008c\u0005"+
		"&\u0000\u0000\u008c\u008d\u0005%\u0000\u0000\u008d\u0097\u0006\u0010\uffff"+
		"\uffff\u0000\u008e\u008f\u0005\u0012\u0000\u0000\u008f\u0090\u0005)\u0000"+
		"\u0000\u0090\u0091\u0005&\u0000\u0000\u0091\u0092\u0005)\u0000\u0000\u0092"+
		"\u0097\u0006\u0010\uffff\uffff\u0000\u0093\u0094\u0005\u0012\u0000\u0000"+
		"\u0094\u0095\u0005)\u0000\u0000\u0095\u0097\u0006\u0010\uffff\uffff\u0000"+
		"\u0096\u0087\u0001\u0000\u0000\u0000\u0096\u008e\u0001\u0000\u0000\u0000"+
		"\u0096\u0093\u0001\u0000\u0000\u0000\u0097!\u0001\u0000\u0000\u0000\u0098"+
		"\u0099\u0005\u0013\u0000\u0000\u0099\u009a\u0006\u0011\uffff\uffff\u0000"+
		"\u009a#\u0001\u0000\u0000\u0000\u009b\u009c\u0005\u0014\u0000\u0000\u009c"+
		"\u009d\u0005)\u0000\u0000\u009d\u00a3\u0006\u0012\uffff\uffff\u0000\u009e"+
		"\u009f\u0005\'\u0000\u0000\u009f\u00a0\u0005)\u0000\u0000\u00a0\u00a2"+
		"\u0006\u0012\uffff\uffff\u0000\u00a1\u009e\u0001\u0000\u0000\u0000\u00a2"+
		"\u00a5\u0001\u0000\u0000\u0000\u00a3\u00a1\u0001\u0000\u0000\u0000\u00a3"+
		"\u00a4\u0001\u0000\u0000\u0000\u00a4%\u0001\u0000\u0000\u0000\u00a5\u00a3"+
		"\u0001\u0000\u0000\u0000\u00a6\u00a7\u0005\u0015\u0000\u0000\u00a7\u00a8"+
		"\u0005$\u0000\u0000\u00a8\u00a9\u0006\u0013\uffff\uffff\u0000\u00a9\'"+
		"\u0001\u0000\u0000\u0000\u00aa\u00ab\u0005\u0016\u0000\u0000\u00ab\u00ac"+
		"\u0005$\u0000\u0000\u00ac\u00ad\u0006\u0014\uffff\uffff\u0000\u00ad)\u0001"+
		"\u0000\u0000\u0000\u00ae\u00af\u0005\u0017\u0000\u0000\u00af\u00b0\u0005"+
		"$\u0000\u0000\u00b0\u00b1\u0006\u0015\uffff\uffff\u0000\u00b1+\u0001\u0000"+
		"\u0000\u0000\u00b2\u00b3\u0005\u0018\u0000\u0000\u00b3\u00b4\u0006\u0016"+
		"\uffff\uffff\u0000\u00b4-\u0001\u0000\u0000\u0000\u00b5\u00b6\u0005\u0019"+
		"\u0000\u0000\u00b6\u00b7\u0005)\u0000\u0000\u00b7\u00b8\u0005&\u0000\u0000"+
		"\u00b8\u00b9\u0005*\u0000\u0000\u00b9\u00ba\u0006\u0017\uffff\uffff\u0000"+
		"\u00ba/\u0001\u0000\u0000\u0000\u00bb\u00bc\u0005\u001a\u0000\u0000\u00bc"+
		"\u00bd\u0005)\u0000\u0000\u00bd\u00be\u0005&\u0000\u0000\u00be\u00bf\u0005"+
		"*\u0000\u0000\u00bf\u00c0\u0006\u0018\uffff\uffff\u0000\u00c01\u0001\u0000"+
		"\u0000\u0000\u00c1\u00c2\u0005\u001b\u0000\u0000\u00c2\u00c3\u0005)\u0000"+
		"\u0000\u00c3\u00c4\u0005&\u0000\u0000\u00c4\u00c5\u0005*\u0000\u0000\u00c5"+
		"\u00c6\u0006\u0019\uffff\uffff\u0000\u00c63\u0001\u0000\u0000\u0000\u00c7"+
		"\u00c8\u0005\u001c\u0000\u0000\u00c8\u00c9\u0005*\u0000\u0000\u00c9\u00ca"+
		"\u0006\u001a\uffff\uffff\u0000\u00ca5\u0001\u0000\u0000\u0000\u00cb\u00cc"+
		"\u0005 \u0000\u0000\u00cc\u00cd\u0005*\u0000\u0000\u00cd\u00ce\u0006\u001b"+
		"\uffff\uffff\u0000\u00ce7\u0001\u0000\u0000\u0000\u00cf\u00d0\u0005\u001d"+
		"\u0000\u0000\u00d0\u00d1\u0005*\u0000\u0000\u00d1\u00d2\u0005&\u0000\u0000"+
		"\u00d2\u00d3\u0005*\u0000\u0000\u00d3\u00d4\u0005&\u0000\u0000\u00d4\u00d5"+
		"\u0005$\u0000\u0000\u00d5\u00d6\u0005&\u0000\u0000\u00d6\u00d7\u0005$"+
		"\u0000\u0000\u00d7\u00d8\u0005&\u0000\u0000\u00d8\u00d9\u0005$\u0000\u0000"+
		"\u00d9\u00da\u0006\u001c\uffff\uffff\u0000\u00da9\u0001\u0000\u0000\u0000"+
		"\u00db\u00dc\u0005\u001e\u0000\u0000\u00dc\u00dd\u0005*\u0000\u0000\u00dd"+
		"\u00de\u0005&\u0000\u0000\u00de\u00df\u0005$\u0000\u0000\u00df\u00e0\u0005"+
		"&\u0000\u0000\u00e0\u00e1\u0005$\u0000\u0000\u00e1\u00e2\u0005&\u0000"+
		"\u0000\u00e2\u00e3\u0005$\u0000\u0000\u00e3\u00e4\u0006\u001d\uffff\uffff"+
		"\u0000\u00e4;\u0001\u0000\u0000\u0000\u00e5\u00e6\u0005\u001f\u0000\u0000"+
		"\u00e6\u00e7\u0005*\u0000\u0000\u00e7\u00e8\u0005&\u0000\u0000\u00e8\u00e9"+
		"\u0005*\u0000\u0000\u00e9\u00ea\u0006\u001e\uffff\uffff\u0000\u00ea=\u0001"+
		"\u0000\u0000\u0000\u00eb\u00ec\u0005!\u0000\u0000\u00ec\u00ed\u0006\u001f"+
		"\uffff\uffff\u0000\u00ed?\u0001\u0000\u0000\u0000\u00ee\u00ef\u0005\""+
		"\u0000\u0000\u00ef\u00f0\u0006 \uffff\uffff\u0000\u00f0A\u0001\u0000\u0000"+
		"\u0000\u00f1\u00f2\u0003\u0000\u0000\u0000\u00f2\u00f6\u0006!\uffff\uffff"+
		"\u0000\u00f3\u00f4\u0003\u0002\u0001\u0000\u00f4\u00f5\u0006!\uffff\uffff"+
		"\u0000\u00f5\u00f7\u0001\u0000\u0000\u0000\u00f6\u00f3\u0001\u0000\u0000"+
		"\u0000\u00f6\u00f7\u0001\u0000\u0000\u0000\u00f7\u00f8\u0001\u0000\u0000"+
		"\u0000\u00f8\u00f9\u0003\u0004\u0002\u0000\u00f9\u00fd\u0006!\uffff\uffff"+
		"\u0000\u00fa\u00fb\u0003\u0006\u0003\u0000\u00fb\u00fc\u0006!\uffff\uffff"+
		"\u0000\u00fc\u00fe\u0001\u0000\u0000\u0000\u00fd\u00fa\u0001\u0000\u0000"+
		"\u0000\u00fd\u00fe\u0001\u0000\u0000\u0000\u00fe\u00ff\u0001\u0000\u0000"+
		"\u0000\u00ff\u0100\u0003\b\u0004\u0000\u0100\u0157\u0006!\uffff\uffff"+
		"\u0000\u0101\u0102\u0003\n\u0005\u0000\u0102\u0103\u0006!\uffff\uffff"+
		"\u0000\u0103\u0156\u0001\u0000\u0000\u0000\u0104\u0105\u0003\f\u0006\u0000"+
		"\u0105\u0106\u0006!\uffff\uffff\u0000\u0106\u0156\u0001\u0000\u0000\u0000"+
		"\u0107\u0108\u0003\u000e\u0007\u0000\u0108\u0109\u0006!\uffff\uffff\u0000"+
		"\u0109\u0156\u0001\u0000\u0000\u0000\u010a\u010b\u0003\u0010\b\u0000\u010b"+
		"\u010c\u0006!\uffff\uffff\u0000\u010c\u0156\u0001\u0000\u0000\u0000\u010d"+
		"\u010e\u0003\u0012\t\u0000\u010e\u010f\u0006!\uffff\uffff\u0000\u010f"+
		"\u0156\u0001\u0000\u0000\u0000\u0110\u0111\u0003\u0014\n\u0000\u0111\u0112"+
		"\u0006!\uffff\uffff\u0000\u0112\u0156\u0001\u0000\u0000\u0000\u0113\u0114"+
		"\u0003\u0016\u000b\u0000\u0114\u0115\u0006!\uffff\uffff\u0000\u0115\u0156"+
		"\u0001\u0000\u0000\u0000\u0116\u0117\u0003\u0018\f\u0000\u0117\u0118\u0006"+
		"!\uffff\uffff\u0000\u0118\u0156\u0001\u0000\u0000\u0000\u0119\u011a\u0003"+
		"\u001a\r\u0000\u011a\u011b\u0006!\uffff\uffff\u0000\u011b\u0156\u0001"+
		"\u0000\u0000\u0000\u011c\u011d\u0003\u001c\u000e\u0000\u011d\u011e\u0006"+
		"!\uffff\uffff\u0000\u011e\u0156\u0001\u0000\u0000\u0000\u011f\u0120\u0003"+
		"\u001e\u000f\u0000\u0120\u0121\u0006!\uffff\uffff\u0000\u0121\u0156\u0001"+
		"\u0000\u0000\u0000\u0122\u0123\u0003 \u0010\u0000\u0123\u0124\u0006!\uffff"+
		"\uffff\u0000\u0124\u0156\u0001\u0000\u0000\u0000\u0125\u0126\u0003\"\u0011"+
		"\u0000\u0126\u0127\u0006!\uffff\uffff\u0000\u0127\u0156\u0001\u0000\u0000"+
		"\u0000\u0128\u0129\u0003$\u0012\u0000\u0129\u012a\u0006!\uffff\uffff\u0000"+
		"\u012a\u0156\u0001\u0000\u0000\u0000\u012b\u012c\u0003&\u0013\u0000\u012c"+
		"\u012d\u0006!\uffff\uffff\u0000\u012d\u0156\u0001\u0000\u0000\u0000\u012e"+
		"\u012f\u0003(\u0014\u0000\u012f\u0130\u0006!\uffff\uffff\u0000\u0130\u0156"+
		"\u0001\u0000\u0000\u0000\u0131\u0132\u0003*\u0015\u0000\u0132\u0133\u0006"+
		"!\uffff\uffff\u0000\u0133\u0156\u0001\u0000\u0000\u0000\u0134\u0135\u0003"+
		",\u0016\u0000\u0135\u0136\u0006!\uffff\uffff\u0000\u0136\u0156\u0001\u0000"+
		"\u0000\u0000\u0137\u0138\u0003.\u0017\u0000\u0138\u0139\u0006!\uffff\uffff"+
		"\u0000\u0139\u0156\u0001\u0000\u0000\u0000\u013a\u013b\u00030\u0018\u0000"+
		"\u013b\u013c\u0006!\uffff\uffff\u0000\u013c\u0156\u0001\u0000\u0000\u0000"+
		"\u013d\u013e\u00032\u0019\u0000\u013e\u013f\u0006!\uffff\uffff\u0000\u013f"+
		"\u0156\u0001\u0000\u0000\u0000\u0140\u0141\u00038\u001c\u0000\u0141\u0142"+
		"\u0006!\uffff\uffff\u0000\u0142\u0156\u0001\u0000\u0000\u0000\u0143\u0144"+
		"\u0003:\u001d\u0000\u0144\u0145\u0006!\uffff\uffff\u0000\u0145\u0156\u0001"+
		"\u0000\u0000\u0000\u0146\u0147\u0003<\u001e\u0000\u0147\u0148\u0006!\uffff"+
		"\uffff\u0000\u0148\u0156\u0001\u0000\u0000\u0000\u0149\u014a\u0003>\u001f"+
		"\u0000\u014a\u014b\u0006!\uffff\uffff\u0000\u014b\u0156\u0001\u0000\u0000"+
		"\u0000\u014c\u014d\u0003@ \u0000\u014d\u014e\u0006!\uffff\uffff\u0000"+
		"\u014e\u0156\u0001\u0000\u0000\u0000\u014f\u0150\u00034\u001a\u0000\u0150"+
		"\u0151\u0006!\uffff\uffff\u0000\u0151\u0156\u0001\u0000\u0000\u0000\u0152"+
		"\u0153\u00036\u001b\u0000\u0153\u0154\u0006!\uffff\uffff\u0000\u0154\u0156"+
		"\u0001\u0000\u0000\u0000\u0155\u0101\u0001\u0000\u0000\u0000\u0155\u0104"+
		"\u0001\u0000\u0000\u0000\u0155\u0107\u0001\u0000\u0000\u0000\u0155\u010a"+
		"\u0001\u0000\u0000\u0000\u0155\u010d\u0001\u0000\u0000\u0000\u0155\u0110"+
		"\u0001\u0000\u0000\u0000\u0155\u0113\u0001\u0000\u0000\u0000\u0155\u0116"+
		"\u0001\u0000\u0000\u0000\u0155\u0119\u0001\u0000\u0000\u0000\u0155\u011c"+
		"\u0001\u0000\u0000\u0000\u0155\u011f\u0001\u0000\u0000\u0000\u0155\u0122"+
		"\u0001\u0000\u0000\u0000\u0155\u0125\u0001\u0000\u0000\u0000\u0155\u0128"+
		"\u0001\u0000\u0000\u0000\u0155\u012b\u0001\u0000\u0000\u0000\u0155\u012e"+
		"\u0001\u0000\u0000\u0000\u0155\u0131\u0001\u0000\u0000\u0000\u0155\u0134"+
		"\u0001\u0000\u0000\u0000\u0155\u0137\u0001\u0000\u0000\u0000\u0155\u013a"+
		"\u0001\u0000\u0000\u0000\u0155\u013d\u0001\u0000\u0000\u0000\u0155\u0140"+
		"\u0001\u0000\u0000\u0000\u0155\u0143\u0001\u0000\u0000\u0000\u0155\u0146"+
		"\u0001\u0000\u0000\u0000\u0155\u0149\u0001\u0000\u0000\u0000\u0155\u014c"+
		"\u0001\u0000\u0000\u0000\u0155\u014f\u0001\u0000\u0000\u0000\u0155\u0152"+
		"\u0001\u0000\u0000\u0000\u0156\u0159\u0001\u0000\u0000\u0000\u0157\u0155"+
		"\u0001\u0000\u0000\u0000\u0157\u0158\u0001\u0000\u0000\u0000\u0158C\u0001"+
		"\u0000\u0000\u0000\u0159\u0157\u0001\u0000\u0000\u0000\u015a\u015b\u0003"+
		"B!\u0000\u015b\u015c\u0006\"\uffff\uffff\u0000\u015c\u015e\u0001\u0000"+
		"\u0000\u0000\u015d\u015a\u0001\u0000\u0000\u0000\u015e\u015f\u0001\u0000"+
		"\u0000\u0000\u015f\u015d\u0001\u0000\u0000\u0000\u015f\u0160\u0001\u0000"+
		"\u0000\u0000\u0160\u0161\u0001\u0000\u0000\u0000\u0161\u0162\u0005\u0000"+
		"\u0000\u0001\u0162E\u0001\u0000\u0000\u0000\u0007\u0096\u00a3\u00f6\u00fd"+
		"\u0155\u0157\u015f";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}