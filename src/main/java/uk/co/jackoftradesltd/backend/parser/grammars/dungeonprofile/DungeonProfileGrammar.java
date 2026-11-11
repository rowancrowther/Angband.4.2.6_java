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

// Generated from DungeonProfileGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.dungeonprofile;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.backend.parser.dungeonprofile.DungeonProfileParseRecord;
import uk.co.jackoftradesltd.backend.parser.dungeonprofile.DungeonProfileParseRecord.Params;
import uk.co.jackoftradesltd.backend.parser.dungeonprofile.DungeonProfileParseRecord.Room;
import uk.co.jackoftradesltd.backend.parser.dungeonprofile.DungeonProfileParseRecord.Streamer;
import uk.co.jackoftradesltd.backend.parser.dungeonprofile.DungeonProfileParseRecord.Tunnel;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class DungeonProfileGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, PARAMS=2, TUNNEL=3, STREAMER=4, ROOM=5, MIN_LEVEL=6, ALLOC=7, 
		INTEGER=8, COLON=9, COMMENT=10, INDENT=11, EOL=12, TEXT_BETWEEN_COLON=13, 
		DELIMITER=14, STRING=15, END_OF_MODE=16;
	public static final int
		RULE_name = 0, RULE_params = 1, RULE_tunnel = 2, RULE_streamer = 3, RULE_alloc = 4, 
		RULE_minLevel = 5, RULE_room = 6, RULE_profile = 7, RULE_file = 8;
	private static String[] makeRuleNames() {
		return new String[] {
			"name", "params", "tunnel", "streamer", "alloc", "minLevel", "room", 
			"profile", "file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'params:'", "'tunnel:'", "'streamer:'", "'room:'", 
			"'min-level:'", "'alloc:'", null, "':'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "PARAMS", "TUNNEL", "STREAMER", "ROOM", "MIN_LEVEL", "ALLOC", 
			"INTEGER", "COLON", "COMMENT", "INDENT", "EOL", "TEXT_BETWEEN_COLON", 
			"DELIMITER", "STRING", "END_OF_MODE"
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
	public String getGrammarFileName() { return "DungeonProfileGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public DungeonProfileGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String profileName;
		public int lineNo;
		public Token STRING;
		public TerminalNode NAME() { return getToken(DungeonProfileGrammar.NAME, 0); }
		public TerminalNode STRING() { return getToken(DungeonProfileGrammar.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DungeonProfileGrammarListener ) ((DungeonProfileGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DungeonProfileGrammarListener ) ((DungeonProfileGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DungeonProfileGrammarVisitor ) return ((DungeonProfileGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(18);
			match(NAME);
			setState(19);
			((NameContext)_localctx).STRING = match(STRING);
			 ((NameContext)_localctx).profileName =  ((NameContext)_localctx).STRING.getText(); ((NameContext)_localctx).lineNo =  _localctx.start.getLine(); 
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
	public static class ParamsContext extends ParserRuleContext {
		public Params paramsRecord;
		public Token b;
		public Token ro;
		public Token u;
		public Token ra;
		public TerminalNode PARAMS() { return getToken(DungeonProfileGrammar.PARAMS, 0); }
		public List<TerminalNode> COLON() { return getTokens(DungeonProfileGrammar.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(DungeonProfileGrammar.COLON, i);
		}
		public List<TerminalNode> INTEGER() { return getTokens(DungeonProfileGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(DungeonProfileGrammar.INTEGER, i);
		}
		public ParamsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_params; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DungeonProfileGrammarListener ) ((DungeonProfileGrammarListener)listener).enterParams(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DungeonProfileGrammarListener ) ((DungeonProfileGrammarListener)listener).exitParams(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DungeonProfileGrammarVisitor ) return ((DungeonProfileGrammarVisitor<? extends T>)visitor).visitParams(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParamsContext params() throws RecognitionException {
		ParamsContext _localctx = new ParamsContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_params);

		            String blockSize;
		            String rooms;
		            String unusual;
		            String rarity;
		        
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(22);
			match(PARAMS);
			setState(23);
			((ParamsContext)_localctx).b = match(INTEGER);
			setState(24);
			match(COLON);
			setState(25);
			((ParamsContext)_localctx).ro = match(INTEGER);
			setState(26);
			match(COLON);
			setState(27);
			((ParamsContext)_localctx).u = match(INTEGER);
			setState(28);
			match(COLON);
			setState(29);
			((ParamsContext)_localctx).ra = match(INTEGER);

			                blockSize = ((ParamsContext)_localctx).b.getText();
			                rooms = ((ParamsContext)_localctx).ro.getText();
			                unusual = ((ParamsContext)_localctx).u.getText();
			                rarity = ((ParamsContext)_localctx).ra.getText();
			            
			}
			_ctx.stop = _input.LT(-1);

			            ((ParamsContext)_localctx).paramsRecord =  new Params(blockSize, rooms, unusual, rarity);
			        
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
	public static class TunnelContext extends ParserRuleContext {
		public Tunnel tunnelRecord;
		public Token r;
		public Token ch;
		public Token co;
		public Token e;
		public Token j;
		public TerminalNode TUNNEL() { return getToken(DungeonProfileGrammar.TUNNEL, 0); }
		public List<TerminalNode> COLON() { return getTokens(DungeonProfileGrammar.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(DungeonProfileGrammar.COLON, i);
		}
		public List<TerminalNode> INTEGER() { return getTokens(DungeonProfileGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(DungeonProfileGrammar.INTEGER, i);
		}
		public TunnelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tunnel; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DungeonProfileGrammarListener ) ((DungeonProfileGrammarListener)listener).enterTunnel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DungeonProfileGrammarListener ) ((DungeonProfileGrammarListener)listener).exitTunnel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DungeonProfileGrammarVisitor ) return ((DungeonProfileGrammarVisitor<? extends T>)visitor).visitTunnel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TunnelContext tunnel() throws RecognitionException {
		TunnelContext _localctx = new TunnelContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_tunnel);

		            String random;
		            String change;
		            String conclude;
		            String entranceDoor;
		            String junction;
		            int lineNo;
		        
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(32);
			match(TUNNEL);
			setState(33);
			((TunnelContext)_localctx).r = match(INTEGER);
			setState(34);
			match(COLON);
			setState(35);
			((TunnelContext)_localctx).ch = match(INTEGER);
			setState(36);
			match(COLON);
			setState(37);
			((TunnelContext)_localctx).co = match(INTEGER);
			setState(38);
			match(COLON);
			setState(39);
			((TunnelContext)_localctx).e = match(INTEGER);
			setState(40);
			match(COLON);
			setState(41);
			((TunnelContext)_localctx).j = match(INTEGER);

			                random = ((TunnelContext)_localctx).r.getText();
			                change = ((TunnelContext)_localctx).ch.getText();
			                conclude = ((TunnelContext)_localctx).co.getText();
			                entranceDoor = ((TunnelContext)_localctx).e.getText();
			                junction = ((TunnelContext)_localctx).j.getText();
			                lineNo = _localctx.start.getLine();
			            
			}
			_ctx.stop = _input.LT(-1);

			            ((TunnelContext)_localctx).tunnelRecord =  new Tunnel(random, change, conclude, entranceDoor, junction, lineNo);
			        
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
	public static class StreamerContext extends ParserRuleContext {
		public Streamer streamerRecord;
		public Token d;
		public Token r;
		public Token m;
		public Token mc;
		public Token q;
		public Token qc;
		public TerminalNode STREAMER() { return getToken(DungeonProfileGrammar.STREAMER, 0); }
		public List<TerminalNode> COLON() { return getTokens(DungeonProfileGrammar.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(DungeonProfileGrammar.COLON, i);
		}
		public List<TerminalNode> INTEGER() { return getTokens(DungeonProfileGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(DungeonProfileGrammar.INTEGER, i);
		}
		public StreamerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_streamer; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DungeonProfileGrammarListener ) ((DungeonProfileGrammarListener)listener).enterStreamer(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DungeonProfileGrammarListener ) ((DungeonProfileGrammarListener)listener).exitStreamer(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DungeonProfileGrammarVisitor ) return ((DungeonProfileGrammarVisitor<? extends T>)visitor).visitStreamer(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StreamerContext streamer() throws RecognitionException {
		StreamerContext _localctx = new StreamerContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_streamer);

		            String density;
		            String range;
		            String magma;
		            String magmaChance;
		            String quartz;
		            String quartzChance;
		            int lineNo;
		        
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			match(STREAMER);
			setState(45);
			((StreamerContext)_localctx).d = match(INTEGER);
			setState(46);
			match(COLON);
			setState(47);
			((StreamerContext)_localctx).r = match(INTEGER);
			setState(48);
			match(COLON);
			setState(49);
			((StreamerContext)_localctx).m = match(INTEGER);
			setState(50);
			match(COLON);
			setState(51);
			((StreamerContext)_localctx).mc = match(INTEGER);
			setState(52);
			match(COLON);
			setState(53);
			((StreamerContext)_localctx).q = match(INTEGER);
			setState(54);
			match(COLON);
			setState(55);
			((StreamerContext)_localctx).qc = match(INTEGER);

			                density = ((StreamerContext)_localctx).d.getText();
			                range = ((StreamerContext)_localctx).r.getText();
			                magma = ((StreamerContext)_localctx).m.getText();
			                magmaChance = ((StreamerContext)_localctx).mc.getText();
			                quartz = ((StreamerContext)_localctx).q.getText();
			                quartzChance = ((StreamerContext)_localctx).qc.getText();
			                lineNo = _localctx.start.getLine();
			            
			}
			_ctx.stop = _input.LT(-1);

			            ((StreamerContext)_localctx).streamerRecord =  new Streamer(density, range, magma, magmaChance, quartz, quartzChance, lineNo);
			        
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
		public String allocChance;
		public Token INTEGER;
		public TerminalNode ALLOC() { return getToken(DungeonProfileGrammar.ALLOC, 0); }
		public TerminalNode INTEGER() { return getToken(DungeonProfileGrammar.INTEGER, 0); }
		public AllocContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alloc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DungeonProfileGrammarListener ) ((DungeonProfileGrammarListener)listener).enterAlloc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DungeonProfileGrammarListener ) ((DungeonProfileGrammarListener)listener).exitAlloc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DungeonProfileGrammarVisitor ) return ((DungeonProfileGrammarVisitor<? extends T>)visitor).visitAlloc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AllocContext alloc() throws RecognitionException {
		AllocContext _localctx = new AllocContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_alloc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58);
			match(ALLOC);
			setState(59);
			((AllocContext)_localctx).INTEGER = match(INTEGER);
			 ((AllocContext)_localctx).allocChance =  ((AllocContext)_localctx).INTEGER.getText(); 
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
	public static class MinLevelContext extends ParserRuleContext {
		public String minLevelValue;
		public Token INTEGER;
		public TerminalNode MIN_LEVEL() { return getToken(DungeonProfileGrammar.MIN_LEVEL, 0); }
		public TerminalNode INTEGER() { return getToken(DungeonProfileGrammar.INTEGER, 0); }
		public MinLevelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_minLevel; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DungeonProfileGrammarListener ) ((DungeonProfileGrammarListener)listener).enterMinLevel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DungeonProfileGrammarListener ) ((DungeonProfileGrammarListener)listener).exitMinLevel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DungeonProfileGrammarVisitor ) return ((DungeonProfileGrammarVisitor<? extends T>)visitor).visitMinLevel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MinLevelContext minLevel() throws RecognitionException {
		MinLevelContext _localctx = new MinLevelContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_minLevel);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(62);
			match(MIN_LEVEL);
			setState(63);
			((MinLevelContext)_localctx).INTEGER = match(INTEGER);
			 ((MinLevelContext)_localctx).minLevelValue =  ((MinLevelContext)_localctx).INTEGER.getText(); 
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
		public Room roomRecord;
		public Token rn;
		public Token rt;
		public Token h;
		public Token w;
		public Token l;
		public Token p;
		public Token rr;
		public Token ct;
		public TerminalNode ROOM() { return getToken(DungeonProfileGrammar.ROOM, 0); }
		public List<TerminalNode> COLON() { return getTokens(DungeonProfileGrammar.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(DungeonProfileGrammar.COLON, i);
		}
		public TerminalNode TEXT_BETWEEN_COLON() { return getToken(DungeonProfileGrammar.TEXT_BETWEEN_COLON, 0); }
		public List<TerminalNode> INTEGER() { return getTokens(DungeonProfileGrammar.INTEGER); }
		public TerminalNode INTEGER(int i) {
			return getToken(DungeonProfileGrammar.INTEGER, i);
		}
		public RoomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_room; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DungeonProfileGrammarListener ) ((DungeonProfileGrammarListener)listener).enterRoom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DungeonProfileGrammarListener ) ((DungeonProfileGrammarListener)listener).exitRoom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DungeonProfileGrammarVisitor ) return ((DungeonProfileGrammarVisitor<? extends T>)visitor).visitRoom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RoomContext room() throws RecognitionException {
		RoomContext _localctx = new RoomContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_room);

		            String roomName;
		            String rating;
		            String height;
		            String width;
		            String level;
		            String pit;
		            String rarity;
		            String cutoff;
		            int lineNo;
		        
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(66);
			match(ROOM);
			setState(67);
			((RoomContext)_localctx).rn = match(TEXT_BETWEEN_COLON);
			setState(68);
			((RoomContext)_localctx).rt = match(INTEGER);
			setState(69);
			match(COLON);
			setState(70);
			((RoomContext)_localctx).h = match(INTEGER);
			setState(71);
			match(COLON);
			setState(72);
			((RoomContext)_localctx).w = match(INTEGER);
			setState(73);
			match(COLON);
			setState(74);
			((RoomContext)_localctx).l = match(INTEGER);
			setState(75);
			match(COLON);
			setState(76);
			((RoomContext)_localctx).p = match(INTEGER);
			setState(77);
			match(COLON);
			setState(78);
			((RoomContext)_localctx).rr = match(INTEGER);
			setState(79);
			match(COLON);
			setState(80);
			((RoomContext)_localctx).ct = match(INTEGER);

			                roomName = ((RoomContext)_localctx).rn.getText();
			                rating = ((RoomContext)_localctx).rt.getText();
			                height = ((RoomContext)_localctx).h.getText();
			                width = ((RoomContext)_localctx).w.getText();
			                level = ((RoomContext)_localctx).l.getText();
			                pit = ((RoomContext)_localctx).p.getText();
			                rarity = ((RoomContext)_localctx).rr.getText();
			                cutoff = ((RoomContext)_localctx).ct.getText();
			                lineNo = _localctx.start.getLine();
			            
			}
			_ctx.stop = _input.LT(-1);

			            ((RoomContext)_localctx).roomRecord =  new Room(roomName, rating, height, width, level, pit, rarity, cutoff, lineNo);
			        
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
	public static class ProfileContext extends ParserRuleContext {
		public DungeonProfileParseRecord record;
		public NameContext name;
		public ParamsContext params;
		public TunnelContext tunnel;
		public StreamerContext streamer;
		public AllocContext alloc;
		public MinLevelContext minLevel;
		public RoomContext room;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public List<ParamsContext> params() {
			return getRuleContexts(ParamsContext.class);
		}
		public ParamsContext params(int i) {
			return getRuleContext(ParamsContext.class,i);
		}
		public List<TunnelContext> tunnel() {
			return getRuleContexts(TunnelContext.class);
		}
		public TunnelContext tunnel(int i) {
			return getRuleContext(TunnelContext.class,i);
		}
		public List<StreamerContext> streamer() {
			return getRuleContexts(StreamerContext.class);
		}
		public StreamerContext streamer(int i) {
			return getRuleContext(StreamerContext.class,i);
		}
		public List<AllocContext> alloc() {
			return getRuleContexts(AllocContext.class);
		}
		public AllocContext alloc(int i) {
			return getRuleContext(AllocContext.class,i);
		}
		public List<MinLevelContext> minLevel() {
			return getRuleContexts(MinLevelContext.class);
		}
		public MinLevelContext minLevel(int i) {
			return getRuleContext(MinLevelContext.class,i);
		}
		public List<RoomContext> room() {
			return getRuleContexts(RoomContext.class);
		}
		public RoomContext room(int i) {
			return getRuleContext(RoomContext.class,i);
		}
		public ProfileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_profile; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DungeonProfileGrammarListener ) ((DungeonProfileGrammarListener)listener).enterProfile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DungeonProfileGrammarListener ) ((DungeonProfileGrammarListener)listener).exitProfile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DungeonProfileGrammarVisitor ) return ((DungeonProfileGrammarVisitor<? extends T>)visitor).visitProfile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProfileContext profile() throws RecognitionException {
		ProfileContext _localctx = new ProfileContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_profile);

		            String profileName = "";
		            Params profileParams = null;
		            Tunnel profileTunnel = null;
		            Streamer profileStreamer = null;
		            String allocNum = null;
		            String minLevelNum = null;
		            List<Room> rooms = new ArrayList<>();
		            int lineNo = 0;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(83);
			((ProfileContext)_localctx).name = name();
			 profileName = ((ProfileContext)_localctx).name.profileName;
			                   lineNo = _localctx.start.getLine(); 
			setState(103); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(103);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case PARAMS:
					{
					setState(85);
					((ProfileContext)_localctx).params = params();
					 profileParams = ((ProfileContext)_localctx).params.paramsRecord; 
					}
					break;
				case TUNNEL:
					{
					setState(88);
					((ProfileContext)_localctx).tunnel = tunnel();
					 profileTunnel = ((ProfileContext)_localctx).tunnel.tunnelRecord; 
					}
					break;
				case STREAMER:
					{
					setState(91);
					((ProfileContext)_localctx).streamer = streamer();
					 profileStreamer = ((ProfileContext)_localctx).streamer.streamerRecord;
					}
					break;
				case ALLOC:
					{
					setState(94);
					((ProfileContext)_localctx).alloc = alloc();
					 allocNum = ((ProfileContext)_localctx).alloc.allocChance; 
					}
					break;
				case MIN_LEVEL:
					{
					setState(97);
					((ProfileContext)_localctx).minLevel = minLevel();
					 minLevelNum = ((ProfileContext)_localctx).minLevel.minLevelValue; 
					}
					break;
				case ROOM:
					{
					setState(100);
					((ProfileContext)_localctx).room = room();
					 rooms.add(((ProfileContext)_localctx).room.roomRecord); 
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(105); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 252L) != 0) );
			}
			_ctx.stop = _input.LT(-1);
			 ((ProfileContext)_localctx).record =  new DungeonProfileParseRecord(profileName,
			            profileParams, profileTunnel, profileStreamer, allocNum,
			            minLevelNum, rooms, lineNo); 
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
		public List<DungeonProfileParseRecord> profiles;
		public ProfileContext profile;
		public TerminalNode EOF() { return getToken(DungeonProfileGrammar.EOF, 0); }
		public List<ProfileContext> profile() {
			return getRuleContexts(ProfileContext.class);
		}
		public ProfileContext profile(int i) {
			return getRuleContext(ProfileContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DungeonProfileGrammarListener ) ((DungeonProfileGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DungeonProfileGrammarListener ) ((DungeonProfileGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DungeonProfileGrammarVisitor ) return ((DungeonProfileGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_file);

		            ((FileContext)_localctx).profiles =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(107);
				((FileContext)_localctx).profile = profile();
				 _localctx.profiles.add(((FileContext)_localctx).profile.record); 
				}
				}
				setState(112); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(114);
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
		"\u0004\u0001\u0010u\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0004\u0007h\b\u0007\u000b\u0007\f\u0007i\u0001"+
		"\b\u0001\b\u0001\b\u0004\bo\b\b\u000b\b\f\bp\u0001\b\u0001\b\u0001\b\u0000"+
		"\u0000\t\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0000\u0000r\u0000"+
		"\u0012\u0001\u0000\u0000\u0000\u0002\u0016\u0001\u0000\u0000\u0000\u0004"+
		" \u0001\u0000\u0000\u0000\u0006,\u0001\u0000\u0000\u0000\b:\u0001\u0000"+
		"\u0000\u0000\n>\u0001\u0000\u0000\u0000\fB\u0001\u0000\u0000\u0000\u000e"+
		"S\u0001\u0000\u0000\u0000\u0010n\u0001\u0000\u0000\u0000\u0012\u0013\u0005"+
		"\u0001\u0000\u0000\u0013\u0014\u0005\u000f\u0000\u0000\u0014\u0015\u0006"+
		"\u0000\uffff\uffff\u0000\u0015\u0001\u0001\u0000\u0000\u0000\u0016\u0017"+
		"\u0005\u0002\u0000\u0000\u0017\u0018\u0005\b\u0000\u0000\u0018\u0019\u0005"+
		"\t\u0000\u0000\u0019\u001a\u0005\b\u0000\u0000\u001a\u001b\u0005\t\u0000"+
		"\u0000\u001b\u001c\u0005\b\u0000\u0000\u001c\u001d\u0005\t\u0000\u0000"+
		"\u001d\u001e\u0005\b\u0000\u0000\u001e\u001f\u0006\u0001\uffff\uffff\u0000"+
		"\u001f\u0003\u0001\u0000\u0000\u0000 !\u0005\u0003\u0000\u0000!\"\u0005"+
		"\b\u0000\u0000\"#\u0005\t\u0000\u0000#$\u0005\b\u0000\u0000$%\u0005\t"+
		"\u0000\u0000%&\u0005\b\u0000\u0000&\'\u0005\t\u0000\u0000\'(\u0005\b\u0000"+
		"\u0000()\u0005\t\u0000\u0000)*\u0005\b\u0000\u0000*+\u0006\u0002\uffff"+
		"\uffff\u0000+\u0005\u0001\u0000\u0000\u0000,-\u0005\u0004\u0000\u0000"+
		"-.\u0005\b\u0000\u0000./\u0005\t\u0000\u0000/0\u0005\b\u0000\u000001\u0005"+
		"\t\u0000\u000012\u0005\b\u0000\u000023\u0005\t\u0000\u000034\u0005\b\u0000"+
		"\u000045\u0005\t\u0000\u000056\u0005\b\u0000\u000067\u0005\t\u0000\u0000"+
		"78\u0005\b\u0000\u000089\u0006\u0003\uffff\uffff\u00009\u0007\u0001\u0000"+
		"\u0000\u0000:;\u0005\u0007\u0000\u0000;<\u0005\b\u0000\u0000<=\u0006\u0004"+
		"\uffff\uffff\u0000=\t\u0001\u0000\u0000\u0000>?\u0005\u0006\u0000\u0000"+
		"?@\u0005\b\u0000\u0000@A\u0006\u0005\uffff\uffff\u0000A\u000b\u0001\u0000"+
		"\u0000\u0000BC\u0005\u0005\u0000\u0000CD\u0005\r\u0000\u0000DE\u0005\b"+
		"\u0000\u0000EF\u0005\t\u0000\u0000FG\u0005\b\u0000\u0000GH\u0005\t\u0000"+
		"\u0000HI\u0005\b\u0000\u0000IJ\u0005\t\u0000\u0000JK\u0005\b\u0000\u0000"+
		"KL\u0005\t\u0000\u0000LM\u0005\b\u0000\u0000MN\u0005\t\u0000\u0000NO\u0005"+
		"\b\u0000\u0000OP\u0005\t\u0000\u0000PQ\u0005\b\u0000\u0000QR\u0006\u0006"+
		"\uffff\uffff\u0000R\r\u0001\u0000\u0000\u0000ST\u0003\u0000\u0000\u0000"+
		"Tg\u0006\u0007\uffff\uffff\u0000UV\u0003\u0002\u0001\u0000VW\u0006\u0007"+
		"\uffff\uffff\u0000Wh\u0001\u0000\u0000\u0000XY\u0003\u0004\u0002\u0000"+
		"YZ\u0006\u0007\uffff\uffff\u0000Zh\u0001\u0000\u0000\u0000[\\\u0003\u0006"+
		"\u0003\u0000\\]\u0006\u0007\uffff\uffff\u0000]h\u0001\u0000\u0000\u0000"+
		"^_\u0003\b\u0004\u0000_`\u0006\u0007\uffff\uffff\u0000`h\u0001\u0000\u0000"+
		"\u0000ab\u0003\n\u0005\u0000bc\u0006\u0007\uffff\uffff\u0000ch\u0001\u0000"+
		"\u0000\u0000de\u0003\f\u0006\u0000ef\u0006\u0007\uffff\uffff\u0000fh\u0001"+
		"\u0000\u0000\u0000gU\u0001\u0000\u0000\u0000gX\u0001\u0000\u0000\u0000"+
		"g[\u0001\u0000\u0000\u0000g^\u0001\u0000\u0000\u0000ga\u0001\u0000\u0000"+
		"\u0000gd\u0001\u0000\u0000\u0000hi\u0001\u0000\u0000\u0000ig\u0001\u0000"+
		"\u0000\u0000ij\u0001\u0000\u0000\u0000j\u000f\u0001\u0000\u0000\u0000"+
		"kl\u0003\u000e\u0007\u0000lm\u0006\b\uffff\uffff\u0000mo\u0001\u0000\u0000"+
		"\u0000nk\u0001\u0000\u0000\u0000op\u0001\u0000\u0000\u0000pn\u0001\u0000"+
		"\u0000\u0000pq\u0001\u0000\u0000\u0000qr\u0001\u0000\u0000\u0000rs\u0005"+
		"\u0000\u0000\u0001s\u0011\u0001\u0000\u0000\u0000\u0003gip";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}