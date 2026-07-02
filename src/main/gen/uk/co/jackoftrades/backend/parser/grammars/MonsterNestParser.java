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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/MonsterNest.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars;

    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.cave.PitProfile;
    import uk.co.jackoftrades.middle.cave.enums.PitRoomType;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.monsters.MonsterBase;
    import uk.co.jackoftrades.middle.monsters.MonsterRace;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;
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
public class MonsterNestParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		COMMENT=1, EOL=2, NAME=3, ROOM=4, ALLOC=5, OBJ_RARITY=6, COLOUR=7, MON_BASE=8, 
		FLAGS_REQ=9, FLAGS_BAN=10, INNATE_FREQ=11, SPELL_REQ=12, SPELL_BAN=13, 
		MON_BAN=14, INTEGER=15, COLOUR_CHAR=16, STRING=17, COLON=18, OR=19;
	public static final int
		RULE_name = 0, RULE_room = 1, RULE_alloc = 2, RULE_objRarity = 3, RULE_colour = 4, 
		RULE_monBase = 5, RULE_flagsReq = 6, RULE_flagsBan = 7, RULE_innateFreq = 8, 
		RULE_spellReq = 9, RULE_spellBan = 10, RULE_monBan = 11, RULE_pit = 12, 
		RULE_file = 13;
	private static String[] makeRuleNames() {
		return new String[] {
			"name", "room", "alloc", "objRarity", "colour", "monBase", "flagsReq", 
			"flagsBan", "innateFreq", "spellReq", "spellBan", "monBan", "pit", "file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, "'name:'", "'room:'", "'alloc:'", "'obj-rarity:'", 
			"'color:'", "'mon-base:'", "'flags-req:'", "'flags-ban:'", "'innate-freq:'", 
			"'spell-req:'", "'spell-ban:'", "'mon-ban:'", null, null, null, "':'", 
			"'|'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "COMMENT", "EOL", "NAME", "ROOM", "ALLOC", "OBJ_RARITY", "COLOUR", 
			"MON_BASE", "FLAGS_REQ", "FLAGS_BAN", "INNATE_FREQ", "SPELL_REQ", "SPELL_BAN", 
			"MON_BAN", "INTEGER", "COLOUR_CHAR", "STRING", "COLON", "OR"
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
	public String getGrammarFileName() { return "MonsterNest.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public MonsterNestParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public Token STRING;
		public TerminalNode NAME() { return getToken(MonsterNestParser.NAME, 0); }
		public TerminalNode STRING() { return getToken(MonsterNestParser.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterNestVisitor ) return ((MonsterNestVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(28);
			match(NAME);
			setState(29);
			((NameContext)_localctx).STRING = match(STRING);
			 ((NameContext)_localctx).nameStr =  ((NameContext)_localctx).STRING.getText(); 
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
	public static class RoomContext extends ParserRuleContext {
		public PitRoomType roomTypeEnum;
		public Token INTEGER;
		public TerminalNode ROOM() { return getToken(MonsterNestParser.ROOM, 0); }
		public TerminalNode INTEGER() { return getToken(MonsterNestParser.INTEGER, 0); }
		public RoomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_room; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).enterRoom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).exitRoom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterNestVisitor ) return ((MonsterNestVisitor<? extends T>)visitor).visitRoom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RoomContext room() throws RecognitionException {
		RoomContext _localctx = new RoomContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_room);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(32);
			match(ROOM);
			setState(33);
			((RoomContext)_localctx).INTEGER = match(INTEGER);

			                switch (((RoomContext)_localctx).INTEGER.getText()) {
			                    case "1":
			                        ((RoomContext)_localctx).roomTypeEnum =  PitRoomType.PIT_TYPE_PIT;
			                        break;

			                    case "2":
			                        ((RoomContext)_localctx).roomTypeEnum =  PitRoomType.PIT_TYPE_NEST;
			                        break;

			                    case "3":
			                        ((RoomContext)_localctx).roomTypeEnum =  PitRoomType.PIT_TYPE_OTHER;
			                        break;

			                    default:
			                        ((RoomContext)_localctx).roomTypeEnum =  PitRoomType.PIT_TYPE_NONE;
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
	public static class AllocContext extends ParserRuleContext {
		public int rarityInt;
		public int levelInt;
		public Token rar;
		public Token lev;
		public TerminalNode ALLOC() { return getToken(MonsterNestParser.ALLOC, 0); }
		public TerminalNode COLON() { return getToken(MonsterNestParser.COLON, 0); }
		public List<TerminalNode> INTEGER() { return getTokens(MonsterNestParser.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(MonsterNestParser.INTEGER, i);
		}
		public AllocContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alloc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).enterAlloc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).exitAlloc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterNestVisitor ) return ((MonsterNestVisitor<? extends T>)visitor).visitAlloc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AllocContext alloc() throws RecognitionException {
		AllocContext _localctx = new AllocContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_alloc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(36);
			match(ALLOC);
			setState(37);
			((AllocContext)_localctx).rar = match(INTEGER);
			setState(38);
			match(COLON);
			setState(39);
			((AllocContext)_localctx).lev = match(INTEGER);

			                ((AllocContext)_localctx).rarityInt =  Integer.parseInt(((AllocContext)_localctx).rar.getText());
			                ((AllocContext)_localctx).levelInt =  Integer.parseInt(((AllocContext)_localctx).lev.getText());
			            
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
	public static class ObjRarityContext extends ParserRuleContext {
		public int rarityInt;
		public Token INTEGER;
		public TerminalNode OBJ_RARITY() { return getToken(MonsterNestParser.OBJ_RARITY, 0); }
		public TerminalNode INTEGER() { return getToken(MonsterNestParser.INTEGER, 0); }
		public ObjRarityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objRarity; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).enterObjRarity(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).exitObjRarity(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterNestVisitor ) return ((MonsterNestVisitor<? extends T>)visitor).visitObjRarity(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObjRarityContext objRarity() throws RecognitionException {
		ObjRarityContext _localctx = new ObjRarityContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_objRarity);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(42);
			match(OBJ_RARITY);
			setState(43);
			((ObjRarityContext)_localctx).INTEGER = match(INTEGER);
			 ((ObjRarityContext)_localctx).rarityInt =  Integer.parseInt(((ObjRarityContext)_localctx).INTEGER.getText()); 
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
		public ColourType colourType;
		public Token cc;
		public TerminalNode COLOUR() { return getToken(MonsterNestParser.COLOUR, 0); }
		public TerminalNode COLOUR_CHAR() { return getToken(MonsterNestParser.COLOUR_CHAR, 0); }
		public ColourContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_colour; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).enterColour(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).exitColour(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterNestVisitor ) return ((MonsterNestVisitor<? extends T>)visitor).visitColour(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ColourContext colour() throws RecognitionException {
		ColourContext _localctx = new ColourContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_colour);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			match(COLOUR);
			setState(47);
			((ColourContext)_localctx).cc = match(COLOUR_CHAR);
			 ((ColourContext)_localctx).colourType =  ColourType.findColourType(((ColourContext)_localctx).cc.getText().charAt(0)); 
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
	public static class MonBaseContext extends ParserRuleContext {
		public MonsterBase base;
		public Token STRING;
		public TerminalNode MON_BASE() { return getToken(MonsterNestParser.MON_BASE, 0); }
		public TerminalNode STRING() { return getToken(MonsterNestParser.STRING, 0); }
		public MonBaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_monBase; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).enterMonBase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).exitMonBase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterNestVisitor ) return ((MonsterNestVisitor<? extends T>)visitor).visitMonBase(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MonBaseContext monBase() throws RecognitionException {
		MonBaseContext _localctx = new MonBaseContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_monBase);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(50);
			match(MON_BASE);
			setState(51);
			((MonBaseContext)_localctx).STRING = match(STRING);

			                String raw = ((MonBaseContext)_localctx).STRING.getText();
			                ((MonBaseContext)_localctx).base =  GameConstants.getMonsterBase(raw);
			            
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
	public static class FlagsReqContext extends ParserRuleContext {
		public Flag<MonsterRaceFlag> flags;
		public Token f1;
		public Token f2;
		public TerminalNode FLAGS_REQ() { return getToken(MonsterNestParser.FLAGS_REQ, 0); }
		public List<TerminalNode> STRING() { return getTokens(MonsterNestParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(MonsterNestParser.STRING, i);
		}
		public List<TerminalNode> OR() { return getTokens(MonsterNestParser.OR); }
		public TerminalNode OR(int i) {
			return getToken(MonsterNestParser.OR, i);
		}
		public FlagsReqContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flagsReq; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).enterFlagsReq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).exitFlagsReq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterNestVisitor ) return ((MonsterNestVisitor<? extends T>)visitor).visitFlagsReq(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsReqContext flagsReq() throws RecognitionException {
		FlagsReqContext _localctx = new FlagsReqContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_flagsReq);

		            ((FlagsReqContext)_localctx).flags =  new Flag<>(MonsterRaceFlag.class);
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(54);
			match(FLAGS_REQ);
			setState(55);
			((FlagsReqContext)_localctx).f1 = match(STRING);

			                String raw1 = "RF_" + ((FlagsReqContext)_localctx).f1.getText().trim().toUpperCase();
			                _localctx.flags.on(MonsterRaceFlag.valueOf(raw1));
			            
			setState(62);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OR) {
				{
				{
				setState(57);
				match(OR);
				setState(58);
				((FlagsReqContext)_localctx).f2 = match(STRING);

				                String raw2 = "RF_" + ((FlagsReqContext)_localctx).f2.getText().trim().toUpperCase();
				                _localctx.flags.on(MonsterRaceFlag.valueOf(raw2));
				            
				}
				}
				setState(64);
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
	public static class FlagsBanContext extends ParserRuleContext {
		public Flag<MonsterRaceFlag> flags;
		public Token f1;
		public Token f2;
		public TerminalNode FLAGS_BAN() { return getToken(MonsterNestParser.FLAGS_BAN, 0); }
		public List<TerminalNode> STRING() { return getTokens(MonsterNestParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(MonsterNestParser.STRING, i);
		}
		public List<TerminalNode> OR() { return getTokens(MonsterNestParser.OR); }
		public TerminalNode OR(int i) {
			return getToken(MonsterNestParser.OR, i);
		}
		public FlagsBanContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flagsBan; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).enterFlagsBan(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).exitFlagsBan(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterNestVisitor ) return ((MonsterNestVisitor<? extends T>)visitor).visitFlagsBan(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsBanContext flagsBan() throws RecognitionException {
		FlagsBanContext _localctx = new FlagsBanContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_flagsBan);

		            ((FlagsBanContext)_localctx).flags =  new Flag<>(MonsterRaceFlag.class);
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(65);
			match(FLAGS_BAN);
			setState(66);
			((FlagsBanContext)_localctx).f1 = match(STRING);

			                String raw1 = "RF_" + ((FlagsBanContext)_localctx).f1.getText().trim().toUpperCase();
			                _localctx.flags.on(MonsterRaceFlag.valueOf(raw1));
			            
			setState(73);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OR) {
				{
				{
				setState(68);
				match(OR);
				setState(69);
				((FlagsBanContext)_localctx).f2 = match(STRING);

				                String raw2 = "RF_" + ((FlagsBanContext)_localctx).f2.getText().trim().toUpperCase();
				                _localctx.flags.on(MonsterRaceFlag.valueOf(raw2));
				            
				}
				}
				setState(75);
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
		public TerminalNode INNATE_FREQ() { return getToken(MonsterNestParser.INNATE_FREQ, 0); }
		public TerminalNode INTEGER() { return getToken(MonsterNestParser.INTEGER, 0); }
		public InnateFreqContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_innateFreq; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).enterInnateFreq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).exitInnateFreq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterNestVisitor ) return ((MonsterNestVisitor<? extends T>)visitor).visitInnateFreq(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InnateFreqContext innateFreq() throws RecognitionException {
		InnateFreqContext _localctx = new InnateFreqContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_innateFreq);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(76);
			match(INNATE_FREQ);
			setState(77);
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
	public static class SpellReqContext extends ParserRuleContext {
		public Flag<MonsterSpell> spells;
		public Token s1;
		public Token s2;
		public TerminalNode SPELL_REQ() { return getToken(MonsterNestParser.SPELL_REQ, 0); }
		public List<TerminalNode> STRING() { return getTokens(MonsterNestParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(MonsterNestParser.STRING, i);
		}
		public List<TerminalNode> OR() { return getTokens(MonsterNestParser.OR); }
		public TerminalNode OR(int i) {
			return getToken(MonsterNestParser.OR, i);
		}
		public SpellReqContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_spellReq; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).enterSpellReq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).exitSpellReq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterNestVisitor ) return ((MonsterNestVisitor<? extends T>)visitor).visitSpellReq(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SpellReqContext spellReq() throws RecognitionException {
		SpellReqContext _localctx = new SpellReqContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_spellReq);

		            ((SpellReqContext)_localctx).spells =  new Flag<>(MonsterSpell.class);
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
			match(SPELL_REQ);
			setState(81);
			((SpellReqContext)_localctx).s1 = match(STRING);

			                String raw1 = "RSF_" + ((SpellReqContext)_localctx).s1.getText().trim().toUpperCase();
			                _localctx.spells.on(MonsterSpell.valueOf(raw1));
			            
			setState(88);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OR) {
				{
				{
				setState(83);
				match(OR);
				setState(84);
				((SpellReqContext)_localctx).s2 = match(STRING);

				                String raw2 = "RSF_" + ((SpellReqContext)_localctx).s2.getText().trim().toUpperCase();
				                _localctx.spells.on(MonsterSpell.valueOf(raw2));
				            
				}
				}
				setState(90);
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
	public static class SpellBanContext extends ParserRuleContext {
		public Flag<MonsterSpell> spells;
		public Token s1;
		public Token s2;
		public TerminalNode SPELL_BAN() { return getToken(MonsterNestParser.SPELL_BAN, 0); }
		public List<TerminalNode> STRING() { return getTokens(MonsterNestParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(MonsterNestParser.STRING, i);
		}
		public List<TerminalNode> OR() { return getTokens(MonsterNestParser.OR); }
		public TerminalNode OR(int i) {
			return getToken(MonsterNestParser.OR, i);
		}
		public SpellBanContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_spellBan; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).enterSpellBan(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).exitSpellBan(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterNestVisitor ) return ((MonsterNestVisitor<? extends T>)visitor).visitSpellBan(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SpellBanContext spellBan() throws RecognitionException {
		SpellBanContext _localctx = new SpellBanContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_spellBan);

		            ((SpellBanContext)_localctx).spells =  new Flag<>(MonsterSpell.class);
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(91);
			match(SPELL_BAN);
			setState(92);
			((SpellBanContext)_localctx).s1 = match(STRING);

			                String raw1 = "RSF_" + ((SpellBanContext)_localctx).s1.getText().trim().toUpperCase();
			                _localctx.spells.on(MonsterSpell.valueOf(raw1));
			            
			setState(99);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OR) {
				{
				{
				setState(94);
				match(OR);
				setState(95);
				((SpellBanContext)_localctx).s2 = match(STRING);

				                String raw2 = "RSF_" + ((SpellBanContext)_localctx).s2.getText().trim().toUpperCase();
				                _localctx.spells.on(MonsterSpell.valueOf(raw2));
				            
				}
				}
				setState(101);
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
	public static class MonBanContext extends ParserRuleContext {
		public MonsterRace race;
		public Token STRING;
		public TerminalNode MON_BAN() { return getToken(MonsterNestParser.MON_BAN, 0); }
		public TerminalNode STRING() { return getToken(MonsterNestParser.STRING, 0); }
		public MonBanContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_monBan; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).enterMonBan(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).exitMonBan(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterNestVisitor ) return ((MonsterNestVisitor<? extends T>)visitor).visitMonBan(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MonBanContext monBan() throws RecognitionException {
		MonBanContext _localctx = new MonBanContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_monBan);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(102);
			match(MON_BAN);
			setState(103);
			((MonBanContext)_localctx).STRING = match(STRING);
			 ((MonBanContext)_localctx).race =  GameConstants.lookupMonsterRace(((MonBanContext)_localctx).STRING.getText()); 
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
	public static class PitContext extends ParserRuleContext {
		public PitProfile profile;
		public NameContext name;
		public RoomContext room;
		public AllocContext alloc;
		public ObjRarityContext objRarity;
		public ColourContext colour;
		public MonBaseContext monBase;
		public FlagsReqContext flagsReq;
		public FlagsBanContext flagsBan;
		public InnateFreqContext innateFreq;
		public SpellReqContext spellReq;
		public SpellBanContext spellBan;
		public MonBanContext monBan;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public List<RoomContext> room() {
			return getRuleContexts(RoomContext.class);
		}
		public RoomContext room(int i) {
			return getRuleContext(RoomContext.class,i);
		}
		public List<AllocContext> alloc() {
			return getRuleContexts(AllocContext.class);
		}
		public AllocContext alloc(int i) {
			return getRuleContext(AllocContext.class,i);
		}
		public List<ObjRarityContext> objRarity() {
			return getRuleContexts(ObjRarityContext.class);
		}
		public ObjRarityContext objRarity(int i) {
			return getRuleContext(ObjRarityContext.class,i);
		}
		public List<ColourContext> colour() {
			return getRuleContexts(ColourContext.class);
		}
		public ColourContext colour(int i) {
			return getRuleContext(ColourContext.class,i);
		}
		public List<MonBaseContext> monBase() {
			return getRuleContexts(MonBaseContext.class);
		}
		public MonBaseContext monBase(int i) {
			return getRuleContext(MonBaseContext.class,i);
		}
		public List<FlagsReqContext> flagsReq() {
			return getRuleContexts(FlagsReqContext.class);
		}
		public FlagsReqContext flagsReq(int i) {
			return getRuleContext(FlagsReqContext.class,i);
		}
		public List<FlagsBanContext> flagsBan() {
			return getRuleContexts(FlagsBanContext.class);
		}
		public FlagsBanContext flagsBan(int i) {
			return getRuleContext(FlagsBanContext.class,i);
		}
		public List<InnateFreqContext> innateFreq() {
			return getRuleContexts(InnateFreqContext.class);
		}
		public InnateFreqContext innateFreq(int i) {
			return getRuleContext(InnateFreqContext.class,i);
		}
		public List<SpellReqContext> spellReq() {
			return getRuleContexts(SpellReqContext.class);
		}
		public SpellReqContext spellReq(int i) {
			return getRuleContext(SpellReqContext.class,i);
		}
		public List<SpellBanContext> spellBan() {
			return getRuleContexts(SpellBanContext.class);
		}
		public SpellBanContext spellBan(int i) {
			return getRuleContext(SpellBanContext.class,i);
		}
		public List<MonBanContext> monBan() {
			return getRuleContexts(MonBanContext.class);
		}
		public MonBanContext monBan(int i) {
			return getRuleContext(MonBanContext.class,i);
		}
		public PitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).enterPit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).exitPit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterNestVisitor ) return ((MonsterNestVisitor<? extends T>)visitor).visitPit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PitContext pit() throws RecognitionException {
		PitContext _localctx = new PitContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_pit);

		            String nameInit = "";
		            PitRoomType roomTypeInit = PitRoomType.PIT_TYPE_NONE;
		            int aveInit = 0;
		            int rarityInit = 0;
		            int objectRarityInit = 0;
		            Flag<MonsterRaceFlag> flagsInit = new Flag<>(MonsterRaceFlag.class);
		            Flag<MonsterRaceFlag> forbiddenFlagsInit = new Flag<>(MonsterRaceFlag.class);
		            int freqInnateInit = 0;
		            Flag<MonsterSpell> spellFlags = new Flag<>(MonsterSpell.class);
		            Flag<MonsterSpell> forbiddenSpellFlags = new Flag<>(MonsterSpell.class);
		            List<MonsterBase> basesInit = new ArrayList<>();
		            List<ColourType> coloursInit = new ArrayList<>();
		            List<MonsterRace> forbiddenMonstersInit = new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(106);
			((PitContext)_localctx).name = name();
			 nameInit = ((PitContext)_localctx).name.nameStr; 
			setState(141); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(141);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case ROOM:
					{
					setState(108);
					((PitContext)_localctx).room = room();
					 roomTypeInit = ((PitContext)_localctx).room.roomTypeEnum; 
					}
					break;
				case ALLOC:
					{
					setState(111);
					((PitContext)_localctx).alloc = alloc();

					                aveInit = ((PitContext)_localctx).alloc.levelInt;
					                rarityInit = ((PitContext)_localctx).alloc.rarityInt;
					            
					}
					break;
				case OBJ_RARITY:
					{
					setState(114);
					((PitContext)_localctx).objRarity = objRarity();
					  objectRarityInit = ((PitContext)_localctx).objRarity.rarityInt; 
					}
					break;
				case COLOUR:
					{
					setState(117);
					((PitContext)_localctx).colour = colour();
					 coloursInit.add(((PitContext)_localctx).colour.colourType); 
					}
					break;
				case MON_BASE:
					{
					setState(120);
					((PitContext)_localctx).monBase = monBase();
					 basesInit.add(((PitContext)_localctx).monBase.base); 
					}
					break;
				case FLAGS_REQ:
					{
					setState(123);
					((PitContext)_localctx).flagsReq = flagsReq();
					 flagsInit.union(((PitContext)_localctx).flagsReq.flags); 
					}
					break;
				case FLAGS_BAN:
					{
					setState(126);
					((PitContext)_localctx).flagsBan = flagsBan();
					 forbiddenFlagsInit.union(((PitContext)_localctx).flagsBan.flags); 
					}
					break;
				case INNATE_FREQ:
					{
					setState(129);
					((PitContext)_localctx).innateFreq = innateFreq();
					 freqInnateInit = ((PitContext)_localctx).innateFreq.innateFreqInt; 
					}
					break;
				case SPELL_REQ:
					{
					setState(132);
					((PitContext)_localctx).spellReq = spellReq();
					 spellFlags.union(((PitContext)_localctx).spellReq.spells); 
					}
					break;
				case SPELL_BAN:
					{
					setState(135);
					((PitContext)_localctx).spellBan = spellBan();
					 forbiddenSpellFlags.union(((PitContext)_localctx).spellBan.spells); 
					}
					break;
				case MON_BAN:
					{
					setState(138);
					((PitContext)_localctx).monBan = monBan();
					 forbiddenMonstersInit.add(((PitContext)_localctx).monBan.race); 
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(143); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 32752L) != 0) );
			}
			_ctx.stop = _input.LT(-1);

			            ((PitContext)_localctx).profile =  new PitProfile(nameInit, roomTypeInit, aveInit, rarityInit, objectRarityInit,
			                                      flagsInit, forbiddenFlagsInit, freqInnateInit, spellFlags,
			                                      forbiddenSpellFlags, basesInit, coloursInit, forbiddenMonstersInit);
			        
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
		public List<PitProfile> pits;
		public PitContext pit;
		public TerminalNode EOF() { return getToken(MonsterNestParser.EOF, 0); }
		public List<PitContext> pit() {
			return getRuleContexts(PitContext.class);
		}
		public PitContext pit(int i) {
			return getRuleContext(PitContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MonsterNestListener ) ((MonsterNestListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MonsterNestVisitor ) return ((MonsterNestVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_file);

		            ((FileContext)_localctx).pits =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(148); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(145);
				((FileContext)_localctx).pit = pit();

				                _localctx.pits.add(((FileContext)_localctx).pit.profile);
				            
				}
				}
				setState(150); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(152);
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
		"\u0004\u0001\u0013\u009b\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0005\u0006=\b"+
		"\u0006\n\u0006\f\u0006@\t\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0005\u0007H\b\u0007\n\u0007\f\u0007K\t"+
		"\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t"+
		"\u0001\t\u0001\t\u0005\tW\b\t\n\t\f\tZ\t\t\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0005\nb\b\n\n\n\f\ne\t\n\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0004\f\u008e\b\f\u000b\f\f\f\u008f\u0001\r\u0001\r\u0001\r"+
		"\u0004\r\u0095\b\r\u000b\r\f\r\u0096\u0001\r\u0001\r\u0001\r\u0000\u0000"+
		"\u000e\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018"+
		"\u001a\u0000\u0000\u009c\u0000\u001c\u0001\u0000\u0000\u0000\u0002 \u0001"+
		"\u0000\u0000\u0000\u0004$\u0001\u0000\u0000\u0000\u0006*\u0001\u0000\u0000"+
		"\u0000\b.\u0001\u0000\u0000\u0000\n2\u0001\u0000\u0000\u0000\f6\u0001"+
		"\u0000\u0000\u0000\u000eA\u0001\u0000\u0000\u0000\u0010L\u0001\u0000\u0000"+
		"\u0000\u0012P\u0001\u0000\u0000\u0000\u0014[\u0001\u0000\u0000\u0000\u0016"+
		"f\u0001\u0000\u0000\u0000\u0018j\u0001\u0000\u0000\u0000\u001a\u0094\u0001"+
		"\u0000\u0000\u0000\u001c\u001d\u0005\u0003\u0000\u0000\u001d\u001e\u0005"+
		"\u0011\u0000\u0000\u001e\u001f\u0006\u0000\uffff\uffff\u0000\u001f\u0001"+
		"\u0001\u0000\u0000\u0000 !\u0005\u0004\u0000\u0000!\"\u0005\u000f\u0000"+
		"\u0000\"#\u0006\u0001\uffff\uffff\u0000#\u0003\u0001\u0000\u0000\u0000"+
		"$%\u0005\u0005\u0000\u0000%&\u0005\u000f\u0000\u0000&\'\u0005\u0012\u0000"+
		"\u0000\'(\u0005\u000f\u0000\u0000()\u0006\u0002\uffff\uffff\u0000)\u0005"+
		"\u0001\u0000\u0000\u0000*+\u0005\u0006\u0000\u0000+,\u0005\u000f\u0000"+
		"\u0000,-\u0006\u0003\uffff\uffff\u0000-\u0007\u0001\u0000\u0000\u0000"+
		"./\u0005\u0007\u0000\u0000/0\u0005\u0010\u0000\u000001\u0006\u0004\uffff"+
		"\uffff\u00001\t\u0001\u0000\u0000\u000023\u0005\b\u0000\u000034\u0005"+
		"\u0011\u0000\u000045\u0006\u0005\uffff\uffff\u00005\u000b\u0001\u0000"+
		"\u0000\u000067\u0005\t\u0000\u000078\u0005\u0011\u0000\u00008>\u0006\u0006"+
		"\uffff\uffff\u00009:\u0005\u0013\u0000\u0000:;\u0005\u0011\u0000\u0000"+
		";=\u0006\u0006\uffff\uffff\u0000<9\u0001\u0000\u0000\u0000=@\u0001\u0000"+
		"\u0000\u0000><\u0001\u0000\u0000\u0000>?\u0001\u0000\u0000\u0000?\r\u0001"+
		"\u0000\u0000\u0000@>\u0001\u0000\u0000\u0000AB\u0005\n\u0000\u0000BC\u0005"+
		"\u0011\u0000\u0000CI\u0006\u0007\uffff\uffff\u0000DE\u0005\u0013\u0000"+
		"\u0000EF\u0005\u0011\u0000\u0000FH\u0006\u0007\uffff\uffff\u0000GD\u0001"+
		"\u0000\u0000\u0000HK\u0001\u0000\u0000\u0000IG\u0001\u0000\u0000\u0000"+
		"IJ\u0001\u0000\u0000\u0000J\u000f\u0001\u0000\u0000\u0000KI\u0001\u0000"+
		"\u0000\u0000LM\u0005\u000b\u0000\u0000MN\u0005\u000f\u0000\u0000NO\u0006"+
		"\b\uffff\uffff\u0000O\u0011\u0001\u0000\u0000\u0000PQ\u0005\f\u0000\u0000"+
		"QR\u0005\u0011\u0000\u0000RX\u0006\t\uffff\uffff\u0000ST\u0005\u0013\u0000"+
		"\u0000TU\u0005\u0011\u0000\u0000UW\u0006\t\uffff\uffff\u0000VS\u0001\u0000"+
		"\u0000\u0000WZ\u0001\u0000\u0000\u0000XV\u0001\u0000\u0000\u0000XY\u0001"+
		"\u0000\u0000\u0000Y\u0013\u0001\u0000\u0000\u0000ZX\u0001\u0000\u0000"+
		"\u0000[\\\u0005\r\u0000\u0000\\]\u0005\u0011\u0000\u0000]c\u0006\n\uffff"+
		"\uffff\u0000^_\u0005\u0013\u0000\u0000_`\u0005\u0011\u0000\u0000`b\u0006"+
		"\n\uffff\uffff\u0000a^\u0001\u0000\u0000\u0000be\u0001\u0000\u0000\u0000"+
		"ca\u0001\u0000\u0000\u0000cd\u0001\u0000\u0000\u0000d\u0015\u0001\u0000"+
		"\u0000\u0000ec\u0001\u0000\u0000\u0000fg\u0005\u000e\u0000\u0000gh\u0005"+
		"\u0011\u0000\u0000hi\u0006\u000b\uffff\uffff\u0000i\u0017\u0001\u0000"+
		"\u0000\u0000jk\u0003\u0000\u0000\u0000k\u008d\u0006\f\uffff\uffff\u0000"+
		"lm\u0003\u0002\u0001\u0000mn\u0006\f\uffff\uffff\u0000n\u008e\u0001\u0000"+
		"\u0000\u0000op\u0003\u0004\u0002\u0000pq\u0006\f\uffff\uffff\u0000q\u008e"+
		"\u0001\u0000\u0000\u0000rs\u0003\u0006\u0003\u0000st\u0006\f\uffff\uffff"+
		"\u0000t\u008e\u0001\u0000\u0000\u0000uv\u0003\b\u0004\u0000vw\u0006\f"+
		"\uffff\uffff\u0000w\u008e\u0001\u0000\u0000\u0000xy\u0003\n\u0005\u0000"+
		"yz\u0006\f\uffff\uffff\u0000z\u008e\u0001\u0000\u0000\u0000{|\u0003\f"+
		"\u0006\u0000|}\u0006\f\uffff\uffff\u0000}\u008e\u0001\u0000\u0000\u0000"+
		"~\u007f\u0003\u000e\u0007\u0000\u007f\u0080\u0006\f\uffff\uffff\u0000"+
		"\u0080\u008e\u0001\u0000\u0000\u0000\u0081\u0082\u0003\u0010\b\u0000\u0082"+
		"\u0083\u0006\f\uffff\uffff\u0000\u0083\u008e\u0001\u0000\u0000\u0000\u0084"+
		"\u0085\u0003\u0012\t\u0000\u0085\u0086\u0006\f\uffff\uffff\u0000\u0086"+
		"\u008e\u0001\u0000\u0000\u0000\u0087\u0088\u0003\u0014\n\u0000\u0088\u0089"+
		"\u0006\f\uffff\uffff\u0000\u0089\u008e\u0001\u0000\u0000\u0000\u008a\u008b"+
		"\u0003\u0016\u000b\u0000\u008b\u008c\u0006\f\uffff\uffff\u0000\u008c\u008e"+
		"\u0001\u0000\u0000\u0000\u008dl\u0001\u0000\u0000\u0000\u008do\u0001\u0000"+
		"\u0000\u0000\u008dr\u0001\u0000\u0000\u0000\u008du\u0001\u0000\u0000\u0000"+
		"\u008dx\u0001\u0000\u0000\u0000\u008d{\u0001\u0000\u0000\u0000\u008d~"+
		"\u0001\u0000\u0000\u0000\u008d\u0081\u0001\u0000\u0000\u0000\u008d\u0084"+
		"\u0001\u0000\u0000\u0000\u008d\u0087\u0001\u0000\u0000\u0000\u008d\u008a"+
		"\u0001\u0000\u0000\u0000\u008e\u008f\u0001\u0000\u0000\u0000\u008f\u008d"+
		"\u0001\u0000\u0000\u0000\u008f\u0090\u0001\u0000\u0000\u0000\u0090\u0019"+
		"\u0001\u0000\u0000\u0000\u0091\u0092\u0003\u0018\f\u0000\u0092\u0093\u0006"+
		"\r\uffff\uffff\u0000\u0093\u0095\u0001\u0000\u0000\u0000\u0094\u0091\u0001"+
		"\u0000\u0000\u0000\u0095\u0096\u0001\u0000\u0000\u0000\u0096\u0094\u0001"+
		"\u0000\u0000\u0000\u0096\u0097\u0001\u0000\u0000\u0000\u0097\u0098\u0001"+
		"\u0000\u0000\u0000\u0098\u0099\u0005\u0000\u0000\u0001\u0099\u001b\u0001"+
		"\u0000\u0000\u0000\u0007>IXc\u008d\u008f\u0096";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}