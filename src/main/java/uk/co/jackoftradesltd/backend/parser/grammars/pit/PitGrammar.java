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

// Generated from PitGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.pit;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.backend.parser.pit.PitParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class PitGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, ROOM=2, ALLOC=3, OBJ_RARITY=4, COLOUR=5, MON_BASE=6, FLAGS_REQ=7, 
		FLAGS_BAN=8, INNATE_FREQ=9, SPELL_REQ=10, SPELL_BAN=11, UINT=12, MON_BAN=13, 
		COMMENT=14, INDENT=15, EOL=16, PIT_INTEGER=17, INNATE_FREQ_EOL=18, FLAG=19, 
		FLAG_OR=20, FLAG_EOL=21, STRING=22, REST_OF_LINE_EOL=23, ALLOC_COLON=24, 
		ALLOC_UINT=25, ALLOC_EOL=26, OBJ_RARITY_UINT=27, OBJ_RARITY_EOL=28, COLOUR_LINE=29, 
		COLOUR_EOL=30;
	public static final int
		RULE_name = 0, RULE_room = 1, RULE_alloc = 2, RULE_objRarity = 3, RULE_colour = 4, 
		RULE_monBase = 5, RULE_flagsReq = 6, RULE_flagsBan = 7, RULE_innateFreq = 8, 
		RULE_spellReq = 9, RULE_spellBan = 10, RULE_monBan = 11, RULE_pitRecord = 12, 
		RULE_file = 13;
	private static String[] makeRuleNames() {
		return new String[] {
			"name", "room", "alloc", "objRarity", "colour", "monBase", "flagsReq", 
			"flagsBan", "innateFreq", "spellReq", "spellBan", "monBan", "pitRecord", 
			"file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'room:'", "'alloc:'", "'obj-rarity:'", "'color:'", 
			"'mon-base:'", "'flags-req:'", "'flags-ban:'", "'innate-freq:'", "'spell-req:'", 
			"'spell-ban:'", null, "'mon-ban:'", null, null, null, null, null, null, 
			null, null, null, null, "':'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "ROOM", "ALLOC", "OBJ_RARITY", "COLOUR", "MON_BASE", "FLAGS_REQ", 
			"FLAGS_BAN", "INNATE_FREQ", "SPELL_REQ", "SPELL_BAN", "UINT", "MON_BAN", 
			"COMMENT", "INDENT", "EOL", "PIT_INTEGER", "INNATE_FREQ_EOL", "FLAG", 
			"FLAG_OR", "FLAG_EOL", "STRING", "REST_OF_LINE_EOL", "ALLOC_COLON", "ALLOC_UINT", 
			"ALLOC_EOL", "OBJ_RARITY_UINT", "OBJ_RARITY_EOL", "COLOUR_LINE", "COLOUR_EOL"
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
	public String getGrammarFileName() { return "PitGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public PitGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public int line;
		public Token STRING;
		public TerminalNode NAME() { return getToken(PitGrammar.NAME, 0); }
		public TerminalNode STRING() { return getToken(PitGrammar.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PitGrammarVisitor ) return ((PitGrammarVisitor<? extends T>)visitor).visitName(this);
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
	public static class RoomContext extends ParserRuleContext {
		public String roomType;
		public Token UINT;
		public TerminalNode ROOM() { return getToken(PitGrammar.ROOM, 0); }
		public TerminalNode UINT() { return getToken(PitGrammar.UINT, 0); }
		public RoomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_room; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).enterRoom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).exitRoom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PitGrammarVisitor ) return ((PitGrammarVisitor<? extends T>)visitor).visitRoom(this);
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
			((RoomContext)_localctx).UINT = match(UINT);
			 ((RoomContext)_localctx).roomType =  ((RoomContext)_localctx).UINT.getText(); 
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
		public String rarity;
		public String averageLevel;
		public Token r;
		public Token l;
		public TerminalNode ALLOC() { return getToken(PitGrammar.ALLOC, 0); }
		public TerminalNode ALLOC_COLON() { return getToken(PitGrammar.ALLOC_COLON, 0); }
		public List<TerminalNode> ALLOC_UINT() { return getTokens(PitGrammar.ALLOC_UINT); }
		public TerminalNode ALLOC_UINT(int i) {
			return getToken(PitGrammar.ALLOC_UINT, i);
		}
		public AllocContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alloc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).enterAlloc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).exitAlloc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PitGrammarVisitor ) return ((PitGrammarVisitor<? extends T>)visitor).visitAlloc(this);
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
			((AllocContext)_localctx).r = match(ALLOC_UINT);
			setState(38);
			match(ALLOC_COLON);
			setState(39);
			((AllocContext)_localctx).l = match(ALLOC_UINT);
			 ((AllocContext)_localctx).rarity =  ((AllocContext)_localctx).r.getText();
			              ((AllocContext)_localctx).averageLevel =  ((AllocContext)_localctx).l.getText(); 
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
		public String objectRarity;
		public Token OBJ_RARITY_UINT;
		public TerminalNode OBJ_RARITY() { return getToken(PitGrammar.OBJ_RARITY, 0); }
		public TerminalNode OBJ_RARITY_UINT() { return getToken(PitGrammar.OBJ_RARITY_UINT, 0); }
		public ObjRarityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_objRarity; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).enterObjRarity(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).exitObjRarity(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PitGrammarVisitor ) return ((PitGrammarVisitor<? extends T>)visitor).visitObjRarity(this);
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
			((ObjRarityContext)_localctx).OBJ_RARITY_UINT = match(OBJ_RARITY_UINT);
			 ((ObjRarityContext)_localctx).objectRarity =  ((ObjRarityContext)_localctx).OBJ_RARITY_UINT.getText(); 
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
		public String colourChar;
		public Token COLOUR_LINE;
		public TerminalNode COLOUR() { return getToken(PitGrammar.COLOUR, 0); }
		public TerminalNode COLOUR_LINE() { return getToken(PitGrammar.COLOUR_LINE, 0); }
		public ColourContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_colour; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).enterColour(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).exitColour(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PitGrammarVisitor ) return ((PitGrammarVisitor<? extends T>)visitor).visitColour(this);
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
			((ColourContext)_localctx).COLOUR_LINE = match(COLOUR_LINE);
			 ((ColourContext)_localctx).colourChar =  ((ColourContext)_localctx).COLOUR_LINE.getText(); 
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
		public String monsterBase;
		public Token STRING;
		public TerminalNode MON_BASE() { return getToken(PitGrammar.MON_BASE, 0); }
		public TerminalNode STRING() { return getToken(PitGrammar.STRING, 0); }
		public MonBaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_monBase; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).enterMonBase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).exitMonBase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PitGrammarVisitor ) return ((PitGrammarVisitor<? extends T>)visitor).visitMonBase(this);
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
			 ((MonBaseContext)_localctx).monsterBase =  ((MonBaseContext)_localctx).STRING.getText(); 
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
		public List<String> flags;
		public Token f1;
		public Token f2;
		public TerminalNode FLAGS_REQ() { return getToken(PitGrammar.FLAGS_REQ, 0); }
		public List<TerminalNode> FLAG_OR() { return getTokens(PitGrammar.FLAG_OR); }
		public TerminalNode FLAG_OR(int i) {
			return getToken(PitGrammar.FLAG_OR, i);
		}
		public List<TerminalNode> FLAG() { return getTokens(PitGrammar.FLAG); }
		public TerminalNode FLAG(int i) {
			return getToken(PitGrammar.FLAG, i);
		}
		public FlagsReqContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flagsReq; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).enterFlagsReq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).exitFlagsReq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PitGrammarVisitor ) return ((PitGrammarVisitor<? extends T>)visitor).visitFlagsReq(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsReqContext flagsReq() throws RecognitionException {
		FlagsReqContext _localctx = new FlagsReqContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_flagsReq);

		            ((FlagsReqContext)_localctx).flags =  new ArrayList<>();
		        
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(54);
			match(FLAGS_REQ);
			setState(62);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(56); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(55);
						match(FLAG_OR);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(58); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				{
				setState(60);
				((FlagsReqContext)_localctx).f1 = match(FLAG);
				 _localctx.flags.add(((FlagsReqContext)_localctx).f1.getText()); 
				}
				break;
			}
			setState(71);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FLAG_OR) {
				{
				{
				setState(64);
				match(FLAG_OR);
				setState(67);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FLAG) {
					{
					setState(65);
					((FlagsReqContext)_localctx).f2 = match(FLAG);
					 _localctx.flags.add(((FlagsReqContext)_localctx).f2.getText()); 
					}
				}

				}
				}
				setState(73);
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
		public List<String> flags;
		public Token f1;
		public Token f2;
		public TerminalNode FLAGS_BAN() { return getToken(PitGrammar.FLAGS_BAN, 0); }
		public List<TerminalNode> FLAG_OR() { return getTokens(PitGrammar.FLAG_OR); }
		public TerminalNode FLAG_OR(int i) {
			return getToken(PitGrammar.FLAG_OR, i);
		}
		public List<TerminalNode> FLAG() { return getTokens(PitGrammar.FLAG); }
		public TerminalNode FLAG(int i) {
			return getToken(PitGrammar.FLAG, i);
		}
		public FlagsBanContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flagsBan; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).enterFlagsBan(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).exitFlagsBan(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PitGrammarVisitor ) return ((PitGrammarVisitor<? extends T>)visitor).visitFlagsBan(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsBanContext flagsBan() throws RecognitionException {
		FlagsBanContext _localctx = new FlagsBanContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_flagsBan);

		            ((FlagsBanContext)_localctx).flags =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			match(FLAGS_BAN);
			setState(78);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				setState(75);
				match(FLAG_OR);
				}
				break;
			case 2:
				{
				setState(76);
				((FlagsBanContext)_localctx).f1 = match(FLAG);
				 _localctx.flags.add(((FlagsBanContext)_localctx).f1.getText()); 
				}
				break;
			}
			setState(87);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FLAG_OR) {
				{
				{
				setState(80);
				match(FLAG_OR);
				setState(83);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FLAG) {
					{
					setState(81);
					((FlagsBanContext)_localctx).f2 = match(FLAG);
					 _localctx.flags.add(((FlagsBanContext)_localctx).f2.getText()); 
					}
				}

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
	public static class InnateFreqContext extends ParserRuleContext {
		public String innFreq;
		public Token f;
		public TerminalNode INNATE_FREQ() { return getToken(PitGrammar.INNATE_FREQ, 0); }
		public TerminalNode PIT_INTEGER() { return getToken(PitGrammar.PIT_INTEGER, 0); }
		public InnateFreqContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_innateFreq; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).enterInnateFreq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).exitInnateFreq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PitGrammarVisitor ) return ((PitGrammarVisitor<? extends T>)visitor).visitInnateFreq(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InnateFreqContext innateFreq() throws RecognitionException {
		InnateFreqContext _localctx = new InnateFreqContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_innateFreq);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(90);
			match(INNATE_FREQ);
			setState(91);
			((InnateFreqContext)_localctx).f = match(PIT_INTEGER);
			 ((InnateFreqContext)_localctx).innFreq =  ((InnateFreqContext)_localctx).f.getText(); 
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
		public List<String> spells;
		public Token s1;
		public Token s2;
		public TerminalNode SPELL_REQ() { return getToken(PitGrammar.SPELL_REQ, 0); }
		public List<TerminalNode> FLAG_OR() { return getTokens(PitGrammar.FLAG_OR); }
		public TerminalNode FLAG_OR(int i) {
			return getToken(PitGrammar.FLAG_OR, i);
		}
		public List<TerminalNode> FLAG() { return getTokens(PitGrammar.FLAG); }
		public TerminalNode FLAG(int i) {
			return getToken(PitGrammar.FLAG, i);
		}
		public SpellReqContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_spellReq; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).enterSpellReq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).exitSpellReq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PitGrammarVisitor ) return ((PitGrammarVisitor<? extends T>)visitor).visitSpellReq(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SpellReqContext spellReq() throws RecognitionException {
		SpellReqContext _localctx = new SpellReqContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_spellReq);

		            ((SpellReqContext)_localctx).spells =  new ArrayList<>();
		        
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(94);
			match(SPELL_REQ);
			setState(102);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				setState(96); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(95);
						match(FLAG_OR);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(98); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				{
				setState(100);
				((SpellReqContext)_localctx).s1 = match(FLAG);
				 _localctx.spells.add(((SpellReqContext)_localctx).s1.getText()); 
				}
				break;
			}
			setState(111);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FLAG_OR) {
				{
				{
				setState(104);
				match(FLAG_OR);
				setState(107);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==FLAG) {
					{
					setState(105);
					((SpellReqContext)_localctx).s2 = match(FLAG);
					 _localctx.spells.add(((SpellReqContext)_localctx).s2.getText()); 
					}
				}

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
	public static class SpellBanContext extends ParserRuleContext {
		public List<String> spells;
		public Token s1;
		public Token s2;
		public TerminalNode SPELL_BAN() { return getToken(PitGrammar.SPELL_BAN, 0); }
		public List<TerminalNode> FLAG_OR() { return getTokens(PitGrammar.FLAG_OR); }
		public TerminalNode FLAG_OR(int i) {
			return getToken(PitGrammar.FLAG_OR, i);
		}
		public List<TerminalNode> FLAG() { return getTokens(PitGrammar.FLAG); }
		public TerminalNode FLAG(int i) {
			return getToken(PitGrammar.FLAG, i);
		}
		public SpellBanContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_spellBan; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).enterSpellBan(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).exitSpellBan(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PitGrammarVisitor ) return ((PitGrammarVisitor<? extends T>)visitor).visitSpellBan(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SpellBanContext spellBan() throws RecognitionException {
		SpellBanContext _localctx = new SpellBanContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_spellBan);

		            ((SpellBanContext)_localctx).spells =  new ArrayList<>();
		        
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(114);
			match(SPELL_BAN);
			setState(122);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
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
					_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				{
				setState(120);
				((SpellBanContext)_localctx).s1 = match(FLAG);
				 _localctx.spells.add(((SpellBanContext)_localctx).s1.getText()); 
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
					((SpellBanContext)_localctx).s2 = match(FLAG);
					 _localctx.spells.add(((SpellBanContext)_localctx).s2.getText()); 
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
	public static class MonBanContext extends ParserRuleContext {
		public String monName;
		public Token STRING;
		public TerminalNode MON_BAN() { return getToken(PitGrammar.MON_BAN, 0); }
		public TerminalNode STRING() { return getToken(PitGrammar.STRING, 0); }
		public MonBanContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_monBan; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).enterMonBan(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).exitMonBan(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PitGrammarVisitor ) return ((PitGrammarVisitor<? extends T>)visitor).visitMonBan(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MonBanContext monBan() throws RecognitionException {
		MonBanContext _localctx = new MonBanContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_monBan);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(134);
			match(MON_BAN);
			setState(135);
			((MonBanContext)_localctx).STRING = match(STRING);
			 ((MonBanContext)_localctx).monName =  ((MonBanContext)_localctx).STRING.getText(); 
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
	public static class PitRecordContext extends ParserRuleContext {
		public PitParseRecord pit;
		public NameContext name;
		public RoomContext room;
		public AllocContext alloc;
		public ObjRarityContext objRarity;
		public ColourContext colour;
		public MonBaseContext monBase;
		public MonBanContext monBan;
		public FlagsReqContext flagsReq;
		public FlagsBanContext flagsBan;
		public InnateFreqContext innateFreq;
		public SpellReqContext spellReq;
		public SpellBanContext spellBan;
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
		public List<MonBanContext> monBan() {
			return getRuleContexts(MonBanContext.class);
		}
		public MonBanContext monBan(int i) {
			return getRuleContext(MonBanContext.class,i);
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
		public PitRecordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pitRecord; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).enterPitRecord(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).exitPitRecord(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PitGrammarVisitor ) return ((PitGrammarVisitor<? extends T>)visitor).visitPitRecord(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PitRecordContext pitRecord() throws RecognitionException {
		PitRecordContext _localctx = new PitRecordContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_pitRecord);

		            String nameInit = "";
		            String pitRoomTypeInit = "";
		            String rarityInit = "";
		            String depthInit = "";
		            String objRarityInit = "";
		            List<String> flags = new ArrayList<>();
		            List<String> bannedFlags = new ArrayList<>();
		            String innateFreqInit = "";
		            List<String> spells = new ArrayList<>();
		            List<String> bannedSpells = new ArrayList<>();
		            List<String> colours = new ArrayList<>();
		            List<String> monsterBases = new ArrayList<>();
		            List<String> bannedMonsterBases = new ArrayList<>();
		            int line = 0;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(138);
			((PitRecordContext)_localctx).name = name();
			 nameInit = ((PitRecordContext)_localctx).name.nameStr; line = ((PitRecordContext)_localctx).name.line; 
			setState(175);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 12284L) != 0)) {
				{
				setState(173);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case ROOM:
					{
					setState(140);
					((PitRecordContext)_localctx).room = room();
					 pitRoomTypeInit = ((PitRecordContext)_localctx).room.roomType; 
					}
					break;
				case ALLOC:
					{
					setState(143);
					((PitRecordContext)_localctx).alloc = alloc();
					 rarityInit = ((PitRecordContext)_localctx).alloc.rarity;
					                    depthInit = ((PitRecordContext)_localctx).alloc.averageLevel; 
					}
					break;
				case OBJ_RARITY:
					{
					setState(146);
					((PitRecordContext)_localctx).objRarity = objRarity();
					 objRarityInit = ((PitRecordContext)_localctx).objRarity.objectRarity; 
					}
					break;
				case COLOUR:
					{
					setState(149);
					((PitRecordContext)_localctx).colour = colour();
					 colours.add(((PitRecordContext)_localctx).colour.colourChar); 
					}
					break;
				case MON_BASE:
					{
					setState(152);
					((PitRecordContext)_localctx).monBase = monBase();
					 monsterBases.add(((PitRecordContext)_localctx).monBase.monsterBase); 
					}
					break;
				case MON_BAN:
					{
					setState(155);
					((PitRecordContext)_localctx).monBan = monBan();
					 bannedMonsterBases.add(((PitRecordContext)_localctx).monBan.monName); 
					}
					break;
				case FLAGS_REQ:
					{
					setState(158);
					((PitRecordContext)_localctx).flagsReq = flagsReq();
					 flags.addAll(((PitRecordContext)_localctx).flagsReq.flags); 
					}
					break;
				case FLAGS_BAN:
					{
					setState(161);
					((PitRecordContext)_localctx).flagsBan = flagsBan();
					 bannedFlags.addAll(((PitRecordContext)_localctx).flagsBan.flags); 
					}
					break;
				case INNATE_FREQ:
					{
					setState(164);
					((PitRecordContext)_localctx).innateFreq = innateFreq();
					 innateFreqInit = ((PitRecordContext)_localctx).innateFreq.innFreq; 
					}
					break;
				case SPELL_REQ:
					{
					setState(167);
					((PitRecordContext)_localctx).spellReq = spellReq();
					 spells.addAll(((PitRecordContext)_localctx).spellReq.spells); 
					}
					break;
				case SPELL_BAN:
					{
					setState(170);
					((PitRecordContext)_localctx).spellBan = spellBan();
					 bannedSpells.addAll(((PitRecordContext)_localctx).spellBan.spells); 
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(177);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			_ctx.stop = _input.LT(-1);

			            ((PitRecordContext)_localctx).pit =  new PitParseRecord(nameInit, pitRoomTypeInit,
			            rarityInit, depthInit, objRarityInit, flags,
			            bannedFlags, innateFreqInit, spells, bannedSpells,
			            colours, monsterBases, bannedMonsterBases, line);
			        
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
		public List<PitParseRecord> pits;
		public PitRecordContext pitRecord;
		public TerminalNode EOF() { return getToken(PitGrammar.EOF, 0); }
		public List<PitRecordContext> pitRecord() {
			return getRuleContexts(PitRecordContext.class);
		}
		public PitRecordContext pitRecord(int i) {
			return getRuleContext(PitRecordContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PitGrammarListener ) ((PitGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PitGrammarVisitor ) return ((PitGrammarVisitor<? extends T>)visitor).visitFile(this);
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
			setState(183);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NAME) {
				{
				{
				setState(178);
				((FileContext)_localctx).pitRecord = pitRecord();
				 _localctx.pits.add(((FileContext)_localctx).pitRecord.pit); 
				}
				}
				setState(185);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(186);
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
		"\u0004\u0001\u001e\u00bd\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001"+
		"\u0006\u0004\u00069\b\u0006\u000b\u0006\f\u0006:\u0001\u0006\u0001\u0006"+
		"\u0003\u0006?\b\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006"+
		"D\b\u0006\u0005\u0006F\b\u0006\n\u0006\f\u0006I\t\u0006\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0003\u0007O\b\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0003\u0007T\b\u0007\u0005\u0007V\b\u0007\n\u0007\f"+
		"\u0007Y\t\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0004"+
		"\ta\b\t\u000b\t\f\tb\u0001\t\u0001\t\u0003\tg\b\t\u0001\t\u0001\t\u0001"+
		"\t\u0003\tl\b\t\u0005\tn\b\t\n\t\f\tq\t\t\u0001\n\u0001\n\u0004\nu\b\n"+
		"\u000b\n\f\nv\u0001\n\u0001\n\u0003\n{\b\n\u0001\n\u0001\n\u0001\n\u0003"+
		"\n\u0080\b\n\u0005\n\u0082\b\n\n\n\f\n\u0085\t\n\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0005\f\u00ae\b\f\n\f\f\f\u00b1\t\f\u0001\r\u0001\r"+
		"\u0001\r\u0005\r\u00b6\b\r\n\r\f\r\u00b9\t\r\u0001\r\u0001\r\u0001\r\u0000"+
		"\u0000\u000e\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016"+
		"\u0018\u001a\u0000\u0000\u00cd\u0000\u001c\u0001\u0000\u0000\u0000\u0002"+
		" \u0001\u0000\u0000\u0000\u0004$\u0001\u0000\u0000\u0000\u0006*\u0001"+
		"\u0000\u0000\u0000\b.\u0001\u0000\u0000\u0000\n2\u0001\u0000\u0000\u0000"+
		"\f6\u0001\u0000\u0000\u0000\u000eJ\u0001\u0000\u0000\u0000\u0010Z\u0001"+
		"\u0000\u0000\u0000\u0012^\u0001\u0000\u0000\u0000\u0014r\u0001\u0000\u0000"+
		"\u0000\u0016\u0086\u0001\u0000\u0000\u0000\u0018\u008a\u0001\u0000\u0000"+
		"\u0000\u001a\u00b7\u0001\u0000\u0000\u0000\u001c\u001d\u0005\u0001\u0000"+
		"\u0000\u001d\u001e\u0005\u0016\u0000\u0000\u001e\u001f\u0006\u0000\uffff"+
		"\uffff\u0000\u001f\u0001\u0001\u0000\u0000\u0000 !\u0005\u0002\u0000\u0000"+
		"!\"\u0005\f\u0000\u0000\"#\u0006\u0001\uffff\uffff\u0000#\u0003\u0001"+
		"\u0000\u0000\u0000$%\u0005\u0003\u0000\u0000%&\u0005\u0019\u0000\u0000"+
		"&\'\u0005\u0018\u0000\u0000\'(\u0005\u0019\u0000\u0000()\u0006\u0002\uffff"+
		"\uffff\u0000)\u0005\u0001\u0000\u0000\u0000*+\u0005\u0004\u0000\u0000"+
		"+,\u0005\u001b\u0000\u0000,-\u0006\u0003\uffff\uffff\u0000-\u0007\u0001"+
		"\u0000\u0000\u0000./\u0005\u0005\u0000\u0000/0\u0005\u001d\u0000\u0000"+
		"01\u0006\u0004\uffff\uffff\u00001\t\u0001\u0000\u0000\u000023\u0005\u0006"+
		"\u0000\u000034\u0005\u0016\u0000\u000045\u0006\u0005\uffff\uffff\u0000"+
		"5\u000b\u0001\u0000\u0000\u00006>\u0005\u0007\u0000\u000079\u0005\u0014"+
		"\u0000\u000087\u0001\u0000\u0000\u00009:\u0001\u0000\u0000\u0000:8\u0001"+
		"\u0000\u0000\u0000:;\u0001\u0000\u0000\u0000;?\u0001\u0000\u0000\u0000"+
		"<=\u0005\u0013\u0000\u0000=?\u0006\u0006\uffff\uffff\u0000>8\u0001\u0000"+
		"\u0000\u0000><\u0001\u0000\u0000\u0000>?\u0001\u0000\u0000\u0000?G\u0001"+
		"\u0000\u0000\u0000@C\u0005\u0014\u0000\u0000AB\u0005\u0013\u0000\u0000"+
		"BD\u0006\u0006\uffff\uffff\u0000CA\u0001\u0000\u0000\u0000CD\u0001\u0000"+
		"\u0000\u0000DF\u0001\u0000\u0000\u0000E@\u0001\u0000\u0000\u0000FI\u0001"+
		"\u0000\u0000\u0000GE\u0001\u0000\u0000\u0000GH\u0001\u0000\u0000\u0000"+
		"H\r\u0001\u0000\u0000\u0000IG\u0001\u0000\u0000\u0000JN\u0005\b\u0000"+
		"\u0000KO\u0005\u0014\u0000\u0000LM\u0005\u0013\u0000\u0000MO\u0006\u0007"+
		"\uffff\uffff\u0000NK\u0001\u0000\u0000\u0000NL\u0001\u0000\u0000\u0000"+
		"NO\u0001\u0000\u0000\u0000OW\u0001\u0000\u0000\u0000PS\u0005\u0014\u0000"+
		"\u0000QR\u0005\u0013\u0000\u0000RT\u0006\u0007\uffff\uffff\u0000SQ\u0001"+
		"\u0000\u0000\u0000ST\u0001\u0000\u0000\u0000TV\u0001\u0000\u0000\u0000"+
		"UP\u0001\u0000\u0000\u0000VY\u0001\u0000\u0000\u0000WU\u0001\u0000\u0000"+
		"\u0000WX\u0001\u0000\u0000\u0000X\u000f\u0001\u0000\u0000\u0000YW\u0001"+
		"\u0000\u0000\u0000Z[\u0005\t\u0000\u0000[\\\u0005\u0011\u0000\u0000\\"+
		"]\u0006\b\uffff\uffff\u0000]\u0011\u0001\u0000\u0000\u0000^f\u0005\n\u0000"+
		"\u0000_a\u0005\u0014\u0000\u0000`_\u0001\u0000\u0000\u0000ab\u0001\u0000"+
		"\u0000\u0000b`\u0001\u0000\u0000\u0000bc\u0001\u0000\u0000\u0000cg\u0001"+
		"\u0000\u0000\u0000de\u0005\u0013\u0000\u0000eg\u0006\t\uffff\uffff\u0000"+
		"f`\u0001\u0000\u0000\u0000fd\u0001\u0000\u0000\u0000fg\u0001\u0000\u0000"+
		"\u0000go\u0001\u0000\u0000\u0000hk\u0005\u0014\u0000\u0000ij\u0005\u0013"+
		"\u0000\u0000jl\u0006\t\uffff\uffff\u0000ki\u0001\u0000\u0000\u0000kl\u0001"+
		"\u0000\u0000\u0000ln\u0001\u0000\u0000\u0000mh\u0001\u0000\u0000\u0000"+
		"nq\u0001\u0000\u0000\u0000om\u0001\u0000\u0000\u0000op\u0001\u0000\u0000"+
		"\u0000p\u0013\u0001\u0000\u0000\u0000qo\u0001\u0000\u0000\u0000rz\u0005"+
		"\u000b\u0000\u0000su\u0005\u0014\u0000\u0000ts\u0001\u0000\u0000\u0000"+
		"uv\u0001\u0000\u0000\u0000vt\u0001\u0000\u0000\u0000vw\u0001\u0000\u0000"+
		"\u0000w{\u0001\u0000\u0000\u0000xy\u0005\u0013\u0000\u0000y{\u0006\n\uffff"+
		"\uffff\u0000zt\u0001\u0000\u0000\u0000zx\u0001\u0000\u0000\u0000z{\u0001"+
		"\u0000\u0000\u0000{\u0083\u0001\u0000\u0000\u0000|\u007f\u0005\u0014\u0000"+
		"\u0000}~\u0005\u0013\u0000\u0000~\u0080\u0006\n\uffff\uffff\u0000\u007f"+
		"}\u0001\u0000\u0000\u0000\u007f\u0080\u0001\u0000\u0000\u0000\u0080\u0082"+
		"\u0001\u0000\u0000\u0000\u0081|\u0001\u0000\u0000\u0000\u0082\u0085\u0001"+
		"\u0000\u0000\u0000\u0083\u0081\u0001\u0000\u0000\u0000\u0083\u0084\u0001"+
		"\u0000\u0000\u0000\u0084\u0015\u0001\u0000\u0000\u0000\u0085\u0083\u0001"+
		"\u0000\u0000\u0000\u0086\u0087\u0005\r\u0000\u0000\u0087\u0088\u0005\u0016"+
		"\u0000\u0000\u0088\u0089\u0006\u000b\uffff\uffff\u0000\u0089\u0017\u0001"+
		"\u0000\u0000\u0000\u008a\u008b\u0003\u0000\u0000\u0000\u008b\u00af\u0006"+
		"\f\uffff\uffff\u0000\u008c\u008d\u0003\u0002\u0001\u0000\u008d\u008e\u0006"+
		"\f\uffff\uffff\u0000\u008e\u00ae\u0001\u0000\u0000\u0000\u008f\u0090\u0003"+
		"\u0004\u0002\u0000\u0090\u0091\u0006\f\uffff\uffff\u0000\u0091\u00ae\u0001"+
		"\u0000\u0000\u0000\u0092\u0093\u0003\u0006\u0003\u0000\u0093\u0094\u0006"+
		"\f\uffff\uffff\u0000\u0094\u00ae\u0001\u0000\u0000\u0000\u0095\u0096\u0003"+
		"\b\u0004\u0000\u0096\u0097\u0006\f\uffff\uffff\u0000\u0097\u00ae\u0001"+
		"\u0000\u0000\u0000\u0098\u0099\u0003\n\u0005\u0000\u0099\u009a\u0006\f"+
		"\uffff\uffff\u0000\u009a\u00ae\u0001\u0000\u0000\u0000\u009b\u009c\u0003"+
		"\u0016\u000b\u0000\u009c\u009d\u0006\f\uffff\uffff\u0000\u009d\u00ae\u0001"+
		"\u0000\u0000\u0000\u009e\u009f\u0003\f\u0006\u0000\u009f\u00a0\u0006\f"+
		"\uffff\uffff\u0000\u00a0\u00ae\u0001\u0000\u0000\u0000\u00a1\u00a2\u0003"+
		"\u000e\u0007\u0000\u00a2\u00a3\u0006\f\uffff\uffff\u0000\u00a3\u00ae\u0001"+
		"\u0000\u0000\u0000\u00a4\u00a5\u0003\u0010\b\u0000\u00a5\u00a6\u0006\f"+
		"\uffff\uffff\u0000\u00a6\u00ae\u0001\u0000\u0000\u0000\u00a7\u00a8\u0003"+
		"\u0012\t\u0000\u00a8\u00a9\u0006\f\uffff\uffff\u0000\u00a9\u00ae\u0001"+
		"\u0000\u0000\u0000\u00aa\u00ab\u0003\u0014\n\u0000\u00ab\u00ac\u0006\f"+
		"\uffff\uffff\u0000\u00ac\u00ae\u0001\u0000\u0000\u0000\u00ad\u008c\u0001"+
		"\u0000\u0000\u0000\u00ad\u008f\u0001\u0000\u0000\u0000\u00ad\u0092\u0001"+
		"\u0000\u0000\u0000\u00ad\u0095\u0001\u0000\u0000\u0000\u00ad\u0098\u0001"+
		"\u0000\u0000\u0000\u00ad\u009b\u0001\u0000\u0000\u0000\u00ad\u009e\u0001"+
		"\u0000\u0000\u0000\u00ad\u00a1\u0001\u0000\u0000\u0000\u00ad\u00a4\u0001"+
		"\u0000\u0000\u0000\u00ad\u00a7\u0001\u0000\u0000\u0000\u00ad\u00aa\u0001"+
		"\u0000\u0000\u0000\u00ae\u00b1\u0001\u0000\u0000\u0000\u00af\u00ad\u0001"+
		"\u0000\u0000\u0000\u00af\u00b0\u0001\u0000\u0000\u0000\u00b0\u0019\u0001"+
		"\u0000\u0000\u0000\u00b1\u00af\u0001\u0000\u0000\u0000\u00b2\u00b3\u0003"+
		"\u0018\f\u0000\u00b3\u00b4\u0006\r\uffff\uffff\u0000\u00b4\u00b6\u0001"+
		"\u0000\u0000\u0000\u00b5\u00b2\u0001\u0000\u0000\u0000\u00b6\u00b9\u0001"+
		"\u0000\u0000\u0000\u00b7\u00b5\u0001\u0000\u0000\u0000\u00b7\u00b8\u0001"+
		"\u0000\u0000\u0000\u00b8\u00ba\u0001\u0000\u0000\u0000\u00b9\u00b7\u0001"+
		"\u0000\u0000\u0000\u00ba\u00bb\u0005\u0000\u0000\u0001\u00bb\u001b\u0001"+
		"\u0000\u0000\u0000\u0012:>CGNSWbfkovz\u007f\u0083\u00ad\u00af\u00b7";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}