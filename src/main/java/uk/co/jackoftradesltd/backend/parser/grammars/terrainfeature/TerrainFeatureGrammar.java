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

// Generated from TerrainFeatureGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.terrainfeature;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftradesltd.backend.parser.terrainfeature.TerrainFeatureParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class TerrainFeatureGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		CODE=1, NAME=2, MIMIC=3, PRIORITY=4, FLAGS=5, DIGGING=6, WALK_MESSAGE=7, 
		RUN_MESSAGE=8, HURT_MESSAGE=9, DIE_MESSAGE=10, CONFUSED_MESSAGE=11, LOOK_PREFIX=12, 
		LOOK_IN_PREPOSITION=13, RESIST_FLAG=14, DESC=15, INTEGER=16, COMMENT=17, 
		INDENT=18, EOL=19, GRAPHICS=20, COLOUR_ONLY=21, GLYPH_ONLY=22, STRING=23, 
		FLAG=24, FLAG_OR=25, FLAG_EOL=26, COLOUR_VALUES=27, GLYPH_VALUES=28, GLYPH_COLON_SWITCH=29, 
		GLYPH_EOL=30;
	public static final int
		RULE_code = 0, RULE_name = 1, RULE_graphics = 2, RULE_mimic = 3, RULE_priority = 4, 
		RULE_flags = 5, RULE_digging = 6, RULE_walk_message = 7, RULE_run_message = 8, 
		RULE_hurt_message = 9, RULE_die_message = 10, RULE_confused_message = 11, 
		RULE_look_prefix = 12, RULE_look_in_preposition = 13, RULE_resist_flag = 14, 
		RULE_desc = 15, RULE_terrain = 16, RULE_file = 17;
	private static String[] makeRuleNames() {
		return new String[] {
			"code", "name", "graphics", "mimic", "priority", "flags", "digging", 
			"walk_message", "run_message", "hurt_message", "die_message", "confused_message", 
			"look_prefix", "look_in_preposition", "resist_flag", "desc", "terrain", 
			"file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'code:'", "'name:'", "'mimic:'", "'priority:'", "'flags:'", "'digging:'", 
			"'walk-msg:'", "'run-msg:'", "'hurt-msg:'", "'die-msg:'", "'confused-msg:'", 
			"'look-prefix:'", "'look-in-preposition:'", "'resist-flag:'", "'desc:'", 
			null, null, null, null, "'graphics:'", "'color:'", "'glyph:'", null, 
			null, null, null, null, null, "':'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "CODE", "NAME", "MIMIC", "PRIORITY", "FLAGS", "DIGGING", "WALK_MESSAGE", 
			"RUN_MESSAGE", "HURT_MESSAGE", "DIE_MESSAGE", "CONFUSED_MESSAGE", "LOOK_PREFIX", 
			"LOOK_IN_PREPOSITION", "RESIST_FLAG", "DESC", "INTEGER", "COMMENT", "INDENT", 
			"EOL", "GRAPHICS", "COLOUR_ONLY", "GLYPH_ONLY", "STRING", "FLAG", "FLAG_OR", 
			"FLAG_EOL", "COLOUR_VALUES", "GLYPH_VALUES", "GLYPH_COLON_SWITCH", "GLYPH_EOL"
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
	public String getGrammarFileName() { return "TerrainFeatureGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public TerrainFeatureGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CodeContext extends ParserRuleContext {
		public String codeFlag;
		public int lineNo;
		public Token FLAG;
		public TerminalNode CODE() { return getToken(TerrainFeatureGrammar.CODE, 0); }
		public TerminalNode FLAG() { return getToken(TerrainFeatureGrammar.FLAG, 0); }
		public TerminalNode FLAG_EOL() { return getToken(TerrainFeatureGrammar.FLAG_EOL, 0); }
		public CodeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_code; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).enterCode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).exitCode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureGrammarVisitor ) return ((TerrainFeatureGrammarVisitor<? extends T>)visitor).visitCode(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CodeContext code() throws RecognitionException {
		CodeContext _localctx = new CodeContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_code);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(36);
			match(CODE);
			setState(37);
			((CodeContext)_localctx).FLAG = match(FLAG);
			setState(38);
			match(FLAG_EOL);

			                ((CodeContext)_localctx).codeFlag =  ((CodeContext)_localctx).FLAG.getText();
			                ((CodeContext)_localctx).lineNo =  _localctx.start.getLine();
			            
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
		public Token STRING;
		public TerminalNode NAME() { return getToken(TerrainFeatureGrammar.NAME, 0); }
		public TerminalNode STRING() { return getToken(TerrainFeatureGrammar.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureGrammarVisitor ) return ((TerrainFeatureGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(41);
			match(NAME);
			setState(42);
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
	public static class GraphicsContext extends ParserRuleContext {
		public String glyph;
		public String colour;
		public Token gly;
		public Token col;
		public TerminalNode GRAPHICS() { return getToken(TerrainFeatureGrammar.GRAPHICS, 0); }
		public TerminalNode GLYPH_COLON_SWITCH() { return getToken(TerrainFeatureGrammar.GLYPH_COLON_SWITCH, 0); }
		public TerminalNode GLYPH_VALUES() { return getToken(TerrainFeatureGrammar.GLYPH_VALUES, 0); }
		public TerminalNode COLOUR_VALUES() { return getToken(TerrainFeatureGrammar.COLOUR_VALUES, 0); }
		public GraphicsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_graphics; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).enterGraphics(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).exitGraphics(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureGrammarVisitor ) return ((TerrainFeatureGrammarVisitor<? extends T>)visitor).visitGraphics(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GraphicsContext graphics() throws RecognitionException {
		GraphicsContext _localctx = new GraphicsContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_graphics);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(45);
			match(GRAPHICS);
			setState(46);
			((GraphicsContext)_localctx).gly = match(GLYPH_VALUES);
			setState(47);
			match(GLYPH_COLON_SWITCH);
			setState(48);
			((GraphicsContext)_localctx).col = match(COLOUR_VALUES);

			                ((GraphicsContext)_localctx).glyph =  ((GraphicsContext)_localctx).gly.getText();
			                ((GraphicsContext)_localctx).colour =  ((GraphicsContext)_localctx).col.getText();
			            
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
		public String mimicFlag;
		public Token FLAG;
		public TerminalNode MIMIC() { return getToken(TerrainFeatureGrammar.MIMIC, 0); }
		public TerminalNode FLAG() { return getToken(TerrainFeatureGrammar.FLAG, 0); }
		public TerminalNode FLAG_EOL() { return getToken(TerrainFeatureGrammar.FLAG_EOL, 0); }
		public MimicContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mimic; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).enterMimic(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).exitMimic(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureGrammarVisitor ) return ((TerrainFeatureGrammarVisitor<? extends T>)visitor).visitMimic(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MimicContext mimic() throws RecognitionException {
		MimicContext _localctx = new MimicContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_mimic);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(51);
			match(MIMIC);
			setState(52);
			((MimicContext)_localctx).FLAG = match(FLAG);
			setState(53);
			match(FLAG_EOL);

			                ((MimicContext)_localctx).mimicFlag =  ((MimicContext)_localctx).FLAG.getText();
			            
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
	public static class PriorityContext extends ParserRuleContext {
		public String priorityNum;
		public Token INTEGER;
		public TerminalNode PRIORITY() { return getToken(TerrainFeatureGrammar.PRIORITY, 0); }
		public TerminalNode INTEGER() { return getToken(TerrainFeatureGrammar.INTEGER, 0); }
		public PriorityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_priority; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).enterPriority(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).exitPriority(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureGrammarVisitor ) return ((TerrainFeatureGrammarVisitor<? extends T>)visitor).visitPriority(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PriorityContext priority() throws RecognitionException {
		PriorityContext _localctx = new PriorityContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_priority);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			match(PRIORITY);
			setState(57);
			((PriorityContext)_localctx).INTEGER = match(INTEGER);

			                ((PriorityContext)_localctx).priorityNum =  ((PriorityContext)_localctx).INTEGER.getText();
			            
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
		public List<String> flagsList;
		public Token f1;
		public Token f2;
		public TerminalNode FLAGS() { return getToken(TerrainFeatureGrammar.FLAGS, 0); }
		public TerminalNode FLAG_EOL() { return getToken(TerrainFeatureGrammar.FLAG_EOL, 0); }
		public List<TerminalNode> FLAG() { return getTokens(TerrainFeatureGrammar.FLAG); }
		public TerminalNode FLAG(int i) {
			return getToken(TerrainFeatureGrammar.FLAG, i);
		}
		public List<TerminalNode> FLAG_OR() { return getTokens(TerrainFeatureGrammar.FLAG_OR); }
		public TerminalNode FLAG_OR(int i) {
			return getToken(TerrainFeatureGrammar.FLAG_OR, i);
		}
		public FlagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flags; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).enterFlags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).exitFlags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureGrammarVisitor ) return ((TerrainFeatureGrammarVisitor<? extends T>)visitor).visitFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsContext flags() throws RecognitionException {
		FlagsContext _localctx = new FlagsContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_flags);

		            ((FlagsContext)_localctx).flagsList =  new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(60);
			match(FLAGS);
			setState(61);
			((FlagsContext)_localctx).f1 = match(FLAG);

			                _localctx.flagsList.add(((FlagsContext)_localctx).f1.getText());
			            
			setState(68);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FLAG_OR) {
				{
				{
				setState(63);
				match(FLAG_OR);
				setState(64);
				((FlagsContext)_localctx).f2 = match(FLAG);

				                _localctx.flagsList.add(((FlagsContext)_localctx).f2.getText());
				            
				}
				}
				setState(70);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(71);
			match(FLAG_EOL);
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
	public static class DiggingContext extends ParserRuleContext {
		public String diggingNum;
		public Token INTEGER;
		public TerminalNode DIGGING() { return getToken(TerrainFeatureGrammar.DIGGING, 0); }
		public TerminalNode INTEGER() { return getToken(TerrainFeatureGrammar.INTEGER, 0); }
		public DiggingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_digging; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).enterDigging(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).exitDigging(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureGrammarVisitor ) return ((TerrainFeatureGrammarVisitor<? extends T>)visitor).visitDigging(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DiggingContext digging() throws RecognitionException {
		DiggingContext _localctx = new DiggingContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_digging);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(73);
			match(DIGGING);
			setState(74);
			((DiggingContext)_localctx).INTEGER = match(INTEGER);

			                ((DiggingContext)_localctx).diggingNum =  ((DiggingContext)_localctx).INTEGER.getText();
			            
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
	public static class Walk_messageContext extends ParserRuleContext {
		public String walkMsgStr;
		public Token STRING;
		public TerminalNode WALK_MESSAGE() { return getToken(TerrainFeatureGrammar.WALK_MESSAGE, 0); }
		public TerminalNode STRING() { return getToken(TerrainFeatureGrammar.STRING, 0); }
		public Walk_messageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_walk_message; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).enterWalk_message(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).exitWalk_message(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureGrammarVisitor ) return ((TerrainFeatureGrammarVisitor<? extends T>)visitor).visitWalk_message(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Walk_messageContext walk_message() throws RecognitionException {
		Walk_messageContext _localctx = new Walk_messageContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_walk_message);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(77);
			match(WALK_MESSAGE);
			setState(78);
			((Walk_messageContext)_localctx).STRING = match(STRING);

			                ((Walk_messageContext)_localctx).walkMsgStr =  ((Walk_messageContext)_localctx).STRING.getText();
			            
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
	public static class Run_messageContext extends ParserRuleContext {
		public String runMsgStr;
		public Token STRING;
		public TerminalNode RUN_MESSAGE() { return getToken(TerrainFeatureGrammar.RUN_MESSAGE, 0); }
		public TerminalNode STRING() { return getToken(TerrainFeatureGrammar.STRING, 0); }
		public Run_messageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_run_message; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).enterRun_message(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).exitRun_message(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureGrammarVisitor ) return ((TerrainFeatureGrammarVisitor<? extends T>)visitor).visitRun_message(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Run_messageContext run_message() throws RecognitionException {
		Run_messageContext _localctx = new Run_messageContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_run_message);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(81);
			match(RUN_MESSAGE);
			setState(82);
			((Run_messageContext)_localctx).STRING = match(STRING);

			                ((Run_messageContext)_localctx).runMsgStr =  ((Run_messageContext)_localctx).STRING.getText();
			            
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
	public static class Hurt_messageContext extends ParserRuleContext {
		public String hurtMsgStr;
		public Token STRING;
		public TerminalNode HURT_MESSAGE() { return getToken(TerrainFeatureGrammar.HURT_MESSAGE, 0); }
		public TerminalNode STRING() { return getToken(TerrainFeatureGrammar.STRING, 0); }
		public Hurt_messageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hurt_message; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).enterHurt_message(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).exitHurt_message(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureGrammarVisitor ) return ((TerrainFeatureGrammarVisitor<? extends T>)visitor).visitHurt_message(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Hurt_messageContext hurt_message() throws RecognitionException {
		Hurt_messageContext _localctx = new Hurt_messageContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_hurt_message);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(85);
			match(HURT_MESSAGE);
			setState(86);
			((Hurt_messageContext)_localctx).STRING = match(STRING);

			                ((Hurt_messageContext)_localctx).hurtMsgStr =  ((Hurt_messageContext)_localctx).STRING.getText();
			            
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
	public static class Die_messageContext extends ParserRuleContext {
		public String dieMsgStr;
		public Token STRING;
		public TerminalNode DIE_MESSAGE() { return getToken(TerrainFeatureGrammar.DIE_MESSAGE, 0); }
		public TerminalNode STRING() { return getToken(TerrainFeatureGrammar.STRING, 0); }
		public Die_messageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_die_message; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).enterDie_message(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).exitDie_message(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureGrammarVisitor ) return ((TerrainFeatureGrammarVisitor<? extends T>)visitor).visitDie_message(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Die_messageContext die_message() throws RecognitionException {
		Die_messageContext _localctx = new Die_messageContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_die_message);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(89);
			match(DIE_MESSAGE);
			setState(90);
			((Die_messageContext)_localctx).STRING = match(STRING);

			                ((Die_messageContext)_localctx).dieMsgStr =  ((Die_messageContext)_localctx).STRING.getText();
			            
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
	public static class Confused_messageContext extends ParserRuleContext {
		public String confMsgStr;
		public Token STRING;
		public TerminalNode CONFUSED_MESSAGE() { return getToken(TerrainFeatureGrammar.CONFUSED_MESSAGE, 0); }
		public TerminalNode STRING() { return getToken(TerrainFeatureGrammar.STRING, 0); }
		public Confused_messageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_confused_message; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).enterConfused_message(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).exitConfused_message(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureGrammarVisitor ) return ((TerrainFeatureGrammarVisitor<? extends T>)visitor).visitConfused_message(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Confused_messageContext confused_message() throws RecognitionException {
		Confused_messageContext _localctx = new Confused_messageContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_confused_message);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(93);
			match(CONFUSED_MESSAGE);
			setState(94);
			((Confused_messageContext)_localctx).STRING = match(STRING);

			                ((Confused_messageContext)_localctx).confMsgStr =  ((Confused_messageContext)_localctx).STRING.getText();
			            
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
	public static class Look_prefixContext extends ParserRuleContext {
		public String prefixStr;
		public Token STRING;
		public TerminalNode LOOK_PREFIX() { return getToken(TerrainFeatureGrammar.LOOK_PREFIX, 0); }
		public TerminalNode STRING() { return getToken(TerrainFeatureGrammar.STRING, 0); }
		public Look_prefixContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_look_prefix; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).enterLook_prefix(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).exitLook_prefix(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureGrammarVisitor ) return ((TerrainFeatureGrammarVisitor<? extends T>)visitor).visitLook_prefix(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Look_prefixContext look_prefix() throws RecognitionException {
		Look_prefixContext _localctx = new Look_prefixContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_look_prefix);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(97);
			match(LOOK_PREFIX);
			setState(98);
			((Look_prefixContext)_localctx).STRING = match(STRING);

			                ((Look_prefixContext)_localctx).prefixStr =  ((Look_prefixContext)_localctx).STRING.getText();
			            
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
	public static class Look_in_prepositionContext extends ParserRuleContext {
		public String prepositionStr;
		public Token STRING;
		public TerminalNode LOOK_IN_PREPOSITION() { return getToken(TerrainFeatureGrammar.LOOK_IN_PREPOSITION, 0); }
		public TerminalNode STRING() { return getToken(TerrainFeatureGrammar.STRING, 0); }
		public Look_in_prepositionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_look_in_preposition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).enterLook_in_preposition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).exitLook_in_preposition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureGrammarVisitor ) return ((TerrainFeatureGrammarVisitor<? extends T>)visitor).visitLook_in_preposition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Look_in_prepositionContext look_in_preposition() throws RecognitionException {
		Look_in_prepositionContext _localctx = new Look_in_prepositionContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_look_in_preposition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(101);
			match(LOOK_IN_PREPOSITION);
			setState(102);
			((Look_in_prepositionContext)_localctx).STRING = match(STRING);

			                ((Look_in_prepositionContext)_localctx).prepositionStr =  ((Look_in_prepositionContext)_localctx).STRING.getText();
			            
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
	public static class Resist_flagContext extends ParserRuleContext {
		public String resistFlag;
		public Token f1;
		public TerminalNode RESIST_FLAG() { return getToken(TerrainFeatureGrammar.RESIST_FLAG, 0); }
		public TerminalNode FLAG_EOL() { return getToken(TerrainFeatureGrammar.FLAG_EOL, 0); }
		public TerminalNode FLAG() { return getToken(TerrainFeatureGrammar.FLAG, 0); }
		public Resist_flagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_resist_flag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).enterResist_flag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).exitResist_flag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureGrammarVisitor ) return ((TerrainFeatureGrammarVisitor<? extends T>)visitor).visitResist_flag(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Resist_flagContext resist_flag() throws RecognitionException {
		Resist_flagContext _localctx = new Resist_flagContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_resist_flag);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(105);
			match(RESIST_FLAG);
			setState(106);
			((Resist_flagContext)_localctx).f1 = match(FLAG);

			                ((Resist_flagContext)_localctx).resistFlag =  ((Resist_flagContext)_localctx).f1.getText(); 
			setState(108);
			match(FLAG_EOL);
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
		public List<TerminalNode> DESC() { return getTokens(TerrainFeatureGrammar.DESC); }
		public TerminalNode DESC(int i) {
			return getToken(TerrainFeatureGrammar.DESC, i);
		}
		public List<TerminalNode> STRING() { return getTokens(TerrainFeatureGrammar.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(TerrainFeatureGrammar.STRING, i);
		}
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_desc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureGrammarVisitor ) return ((TerrainFeatureGrammarVisitor<? extends T>)visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_desc);

		            StringBuilder sb = new StringBuilder();
		        
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(113); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(110);
					match(DESC);
					setState(111);
					((DescContext)_localctx).STRING = match(STRING);

					                sb.append(((DescContext)_localctx).STRING.getText());
					            
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(115); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			}
			_ctx.stop = _input.LT(-1);

			            ((DescContext)_localctx).descStr =  sb.toString();
			        
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
	public static class TerrainContext extends ParserRuleContext {
		public TerrainFeatureParseRecord feature;
		public CodeContext code;
		public NameContext name;
		public GraphicsContext graphics;
		public PriorityContext priority;
		public MimicContext mimic;
		public FlagsContext flags;
		public DiggingContext digging;
		public Walk_messageContext walk_message;
		public Run_messageContext run_message;
		public Hurt_messageContext hurt_message;
		public Die_messageContext die_message;
		public Confused_messageContext confused_message;
		public Look_prefixContext look_prefix;
		public Look_in_prepositionContext look_in_preposition;
		public Resist_flagContext resist_flag;
		public DescContext desc;
		public CodeContext code() {
			return getRuleContext(CodeContext.class,0);
		}
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public GraphicsContext graphics() {
			return getRuleContext(GraphicsContext.class,0);
		}
		public List<PriorityContext> priority() {
			return getRuleContexts(PriorityContext.class);
		}
		public PriorityContext priority(int i) {
			return getRuleContext(PriorityContext.class,i);
		}
		public List<MimicContext> mimic() {
			return getRuleContexts(MimicContext.class);
		}
		public MimicContext mimic(int i) {
			return getRuleContext(MimicContext.class,i);
		}
		public List<DiggingContext> digging() {
			return getRuleContexts(DiggingContext.class);
		}
		public DiggingContext digging(int i) {
			return getRuleContext(DiggingContext.class,i);
		}
		public List<Walk_messageContext> walk_message() {
			return getRuleContexts(Walk_messageContext.class);
		}
		public Walk_messageContext walk_message(int i) {
			return getRuleContext(Walk_messageContext.class,i);
		}
		public List<Run_messageContext> run_message() {
			return getRuleContexts(Run_messageContext.class);
		}
		public Run_messageContext run_message(int i) {
			return getRuleContext(Run_messageContext.class,i);
		}
		public List<Hurt_messageContext> hurt_message() {
			return getRuleContexts(Hurt_messageContext.class);
		}
		public Hurt_messageContext hurt_message(int i) {
			return getRuleContext(Hurt_messageContext.class,i);
		}
		public List<Die_messageContext> die_message() {
			return getRuleContexts(Die_messageContext.class);
		}
		public Die_messageContext die_message(int i) {
			return getRuleContext(Die_messageContext.class,i);
		}
		public List<Confused_messageContext> confused_message() {
			return getRuleContexts(Confused_messageContext.class);
		}
		public Confused_messageContext confused_message(int i) {
			return getRuleContext(Confused_messageContext.class,i);
		}
		public List<Look_prefixContext> look_prefix() {
			return getRuleContexts(Look_prefixContext.class);
		}
		public Look_prefixContext look_prefix(int i) {
			return getRuleContext(Look_prefixContext.class,i);
		}
		public List<Look_in_prepositionContext> look_in_preposition() {
			return getRuleContexts(Look_in_prepositionContext.class);
		}
		public Look_in_prepositionContext look_in_preposition(int i) {
			return getRuleContext(Look_in_prepositionContext.class,i);
		}
		public List<Resist_flagContext> resist_flag() {
			return getRuleContexts(Resist_flagContext.class);
		}
		public Resist_flagContext resist_flag(int i) {
			return getRuleContext(Resist_flagContext.class,i);
		}
		public List<DescContext> desc() {
			return getRuleContexts(DescContext.class);
		}
		public DescContext desc(int i) {
			return getRuleContext(DescContext.class,i);
		}
		public List<FlagsContext> flags() {
			return getRuleContexts(FlagsContext.class);
		}
		public FlagsContext flags(int i) {
			return getRuleContext(FlagsContext.class,i);
		}
		public TerrainContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_terrain; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).enterTerrain(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).exitTerrain(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureGrammarVisitor ) return ((TerrainFeatureGrammarVisitor<? extends T>)visitor).visitTerrain(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TerrainContext terrain() throws RecognitionException {
		TerrainContext _localctx = new TerrainContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_terrain);

		            String codeInit = "";
		            String nameInit = "";
		            String glyph = "";
		            String colour = "";
		            String priorityInit = "";
		            String mimicInit = "";
		            List<String> tfFlags = new ArrayList<>();
		            String diggingInit = "";
		            String hurtInit = "";
		            String walkInit = "";
		            String runInit = "";
		            String dieInit = "";
		            String confInit = "";
		            String prefixInit = "";
		            String prepositionInit = "";
		            String resistInit = "";
		            String descInit = "";
		            int lineNo = 0;
		        
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(117);
			((TerrainContext)_localctx).code = code();

			                codeInit = ((TerrainContext)_localctx).code.codeFlag;
			                lineNo = ((TerrainContext)_localctx).code.lineNo;
			            
			setState(119);
			((TerrainContext)_localctx).name = name();

			                nameInit = ((TerrainContext)_localctx).name.nameStr;
			            
			setState(121);
			((TerrainContext)_localctx).graphics = graphics();

			                glyph = ((TerrainContext)_localctx).graphics.glyph;
			                colour = ((TerrainContext)_localctx).graphics.colour;
			            
			setState(168);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 65528L) != 0)) {
				{
				setState(166);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case PRIORITY:
					{
					setState(123);
					((TerrainContext)_localctx).priority = priority();

					                    priorityInit = ((TerrainContext)_localctx).priority.priorityNum;
					                
					}
					break;
				case MIMIC:
					{
					setState(126);
					((TerrainContext)_localctx).mimic = mimic();

					                    mimicInit = ((TerrainContext)_localctx).mimic.mimicFlag;
					                
					}
					break;
				case FLAGS:
					{
					setState(132); 
					_errHandler.sync(this);
					_alt = 1;
					do {
						switch (_alt) {
						case 1:
							{
							{
							setState(129);
							((TerrainContext)_localctx).flags = flags();

							                    tfFlags.addAll(((TerrainContext)_localctx).flags.flagsList);
							                
							}
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						setState(134); 
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
					} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
					}
					break;
				case DIGGING:
					{
					setState(136);
					((TerrainContext)_localctx).digging = digging();

					                    diggingInit = ((TerrainContext)_localctx).digging.diggingNum;
					                
					}
					break;
				case WALK_MESSAGE:
					{
					setState(139);
					((TerrainContext)_localctx).walk_message = walk_message();

					                    walkInit = ((TerrainContext)_localctx).walk_message.walkMsgStr;
					                
					}
					break;
				case RUN_MESSAGE:
					{
					setState(142);
					((TerrainContext)_localctx).run_message = run_message();

					                    runInit = ((TerrainContext)_localctx).run_message.runMsgStr;
					                
					}
					break;
				case HURT_MESSAGE:
					{
					setState(145);
					((TerrainContext)_localctx).hurt_message = hurt_message();

					                    hurtInit = ((TerrainContext)_localctx).hurt_message.hurtMsgStr;
					                
					}
					break;
				case DIE_MESSAGE:
					{
					setState(148);
					((TerrainContext)_localctx).die_message = die_message();

					                    dieInit = ((TerrainContext)_localctx).die_message.dieMsgStr;
					                
					}
					break;
				case CONFUSED_MESSAGE:
					{
					setState(151);
					((TerrainContext)_localctx).confused_message = confused_message();

					                    confInit = ((TerrainContext)_localctx).confused_message.confMsgStr;
					                
					}
					break;
				case LOOK_PREFIX:
					{
					setState(154);
					((TerrainContext)_localctx).look_prefix = look_prefix();

					                    prefixInit = ((TerrainContext)_localctx).look_prefix.prefixStr;
					                
					}
					break;
				case LOOK_IN_PREPOSITION:
					{
					setState(157);
					((TerrainContext)_localctx).look_in_preposition = look_in_preposition();

					                    prepositionInit = ((TerrainContext)_localctx).look_in_preposition.prepositionStr;
					                
					}
					break;
				case RESIST_FLAG:
					{
					setState(160);
					((TerrainContext)_localctx).resist_flag = resist_flag();

					                    resistInit = ((TerrainContext)_localctx).resist_flag.resistFlag;
					                
					}
					break;
				case DESC:
					{
					setState(163);
					((TerrainContext)_localctx).desc = desc();

					                    descInit = descInit + ((TerrainContext)_localctx).desc.descStr;
					                
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(170);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			_ctx.stop = _input.LT(-1);

			            ((TerrainContext)_localctx).feature =  new TerrainFeatureParseRecord(
			                                   codeInit, nameInit, descInit, mimicInit,
			                                   priorityInit, diggingInit, tfFlags,
			                                   glyph, colour, walkInit, runInit, hurtInit, dieInit,
			                                   confInit, prefixInit, prepositionInit,
			                                   resistInit, lineNo);
			        
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
		public List<TerrainFeatureParseRecord> features;
		public TerrainContext terrain;
		public TerminalNode EOF() { return getToken(TerrainFeatureGrammar.EOF, 0); }
		public List<TerrainContext> terrain() {
			return getRuleContexts(TerrainContext.class);
		}
		public TerrainContext terrain(int i) {
			return getRuleContext(TerrainContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureGrammarListener ) ((TerrainFeatureGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureGrammarVisitor ) return ((TerrainFeatureGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_file);
		 ((FileContext)_localctx).features =  new ArrayList<>(); 
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(174); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(171);
				((FileContext)_localctx).terrain = terrain();

				                _localctx.features.add(((FileContext)_localctx).terrain.feature);
				            
				}
				}
				setState(176); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==CODE );
			setState(178);
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
		"\u0004\u0001\u001e\u00b5\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0005\u0005C\b"+
		"\u0005\n\u0005\f\u0005F\t\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t"+
		"\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0004\u000fr\b\u000f\u000b\u000f\f\u000fs\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0004\u0010\u0085\b\u0010\u000b\u0010\f"+
		"\u0010\u0086\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0005\u0010\u00a7\b\u0010\n\u0010\f\u0010\u00aa\t\u0010\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0004\u0011\u00af\b\u0011\u000b\u0011\f"+
		"\u0011\u00b0\u0001\u0011\u0001\u0011\u0001\u0011\u0000\u0000\u0012\u0000"+
		"\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c"+
		"\u001e \"\u0000\u0000\u00b3\u0000$\u0001\u0000\u0000\u0000\u0002)\u0001"+
		"\u0000\u0000\u0000\u0004-\u0001\u0000\u0000\u0000\u00063\u0001\u0000\u0000"+
		"\u0000\b8\u0001\u0000\u0000\u0000\n<\u0001\u0000\u0000\u0000\fI\u0001"+
		"\u0000\u0000\u0000\u000eM\u0001\u0000\u0000\u0000\u0010Q\u0001\u0000\u0000"+
		"\u0000\u0012U\u0001\u0000\u0000\u0000\u0014Y\u0001\u0000\u0000\u0000\u0016"+
		"]\u0001\u0000\u0000\u0000\u0018a\u0001\u0000\u0000\u0000\u001ae\u0001"+
		"\u0000\u0000\u0000\u001ci\u0001\u0000\u0000\u0000\u001eq\u0001\u0000\u0000"+
		"\u0000 u\u0001\u0000\u0000\u0000\"\u00ae\u0001\u0000\u0000\u0000$%\u0005"+
		"\u0001\u0000\u0000%&\u0005\u0018\u0000\u0000&\'\u0005\u001a\u0000\u0000"+
		"\'(\u0006\u0000\uffff\uffff\u0000(\u0001\u0001\u0000\u0000\u0000)*\u0005"+
		"\u0002\u0000\u0000*+\u0005\u0017\u0000\u0000+,\u0006\u0001\uffff\uffff"+
		"\u0000,\u0003\u0001\u0000\u0000\u0000-.\u0005\u0014\u0000\u0000./\u0005"+
		"\u001c\u0000\u0000/0\u0005\u001d\u0000\u000001\u0005\u001b\u0000\u0000"+
		"12\u0006\u0002\uffff\uffff\u00002\u0005\u0001\u0000\u0000\u000034\u0005"+
		"\u0003\u0000\u000045\u0005\u0018\u0000\u000056\u0005\u001a\u0000\u0000"+
		"67\u0006\u0003\uffff\uffff\u00007\u0007\u0001\u0000\u0000\u000089\u0005"+
		"\u0004\u0000\u00009:\u0005\u0010\u0000\u0000:;\u0006\u0004\uffff\uffff"+
		"\u0000;\t\u0001\u0000\u0000\u0000<=\u0005\u0005\u0000\u0000=>\u0005\u0018"+
		"\u0000\u0000>D\u0006\u0005\uffff\uffff\u0000?@\u0005\u0019\u0000\u0000"+
		"@A\u0005\u0018\u0000\u0000AC\u0006\u0005\uffff\uffff\u0000B?\u0001\u0000"+
		"\u0000\u0000CF\u0001\u0000\u0000\u0000DB\u0001\u0000\u0000\u0000DE\u0001"+
		"\u0000\u0000\u0000EG\u0001\u0000\u0000\u0000FD\u0001\u0000\u0000\u0000"+
		"GH\u0005\u001a\u0000\u0000H\u000b\u0001\u0000\u0000\u0000IJ\u0005\u0006"+
		"\u0000\u0000JK\u0005\u0010\u0000\u0000KL\u0006\u0006\uffff\uffff\u0000"+
		"L\r\u0001\u0000\u0000\u0000MN\u0005\u0007\u0000\u0000NO\u0005\u0017\u0000"+
		"\u0000OP\u0006\u0007\uffff\uffff\u0000P\u000f\u0001\u0000\u0000\u0000"+
		"QR\u0005\b\u0000\u0000RS\u0005\u0017\u0000\u0000ST\u0006\b\uffff\uffff"+
		"\u0000T\u0011\u0001\u0000\u0000\u0000UV\u0005\t\u0000\u0000VW\u0005\u0017"+
		"\u0000\u0000WX\u0006\t\uffff\uffff\u0000X\u0013\u0001\u0000\u0000\u0000"+
		"YZ\u0005\n\u0000\u0000Z[\u0005\u0017\u0000\u0000[\\\u0006\n\uffff\uffff"+
		"\u0000\\\u0015\u0001\u0000\u0000\u0000]^\u0005\u000b\u0000\u0000^_\u0005"+
		"\u0017\u0000\u0000_`\u0006\u000b\uffff\uffff\u0000`\u0017\u0001\u0000"+
		"\u0000\u0000ab\u0005\f\u0000\u0000bc\u0005\u0017\u0000\u0000cd\u0006\f"+
		"\uffff\uffff\u0000d\u0019\u0001\u0000\u0000\u0000ef\u0005\r\u0000\u0000"+
		"fg\u0005\u0017\u0000\u0000gh\u0006\r\uffff\uffff\u0000h\u001b\u0001\u0000"+
		"\u0000\u0000ij\u0005\u000e\u0000\u0000jk\u0005\u0018\u0000\u0000kl\u0006"+
		"\u000e\uffff\uffff\u0000lm\u0005\u001a\u0000\u0000m\u001d\u0001\u0000"+
		"\u0000\u0000no\u0005\u000f\u0000\u0000op\u0005\u0017\u0000\u0000pr\u0006"+
		"\u000f\uffff\uffff\u0000qn\u0001\u0000\u0000\u0000rs\u0001\u0000\u0000"+
		"\u0000sq\u0001\u0000\u0000\u0000st\u0001\u0000\u0000\u0000t\u001f\u0001"+
		"\u0000\u0000\u0000uv\u0003\u0000\u0000\u0000vw\u0006\u0010\uffff\uffff"+
		"\u0000wx\u0003\u0002\u0001\u0000xy\u0006\u0010\uffff\uffff\u0000yz\u0003"+
		"\u0004\u0002\u0000z\u00a8\u0006\u0010\uffff\uffff\u0000{|\u0003\b\u0004"+
		"\u0000|}\u0006\u0010\uffff\uffff\u0000}\u00a7\u0001\u0000\u0000\u0000"+
		"~\u007f\u0003\u0006\u0003\u0000\u007f\u0080\u0006\u0010\uffff\uffff\u0000"+
		"\u0080\u00a7\u0001\u0000\u0000\u0000\u0081\u0082\u0003\n\u0005\u0000\u0082"+
		"\u0083\u0006\u0010\uffff\uffff\u0000\u0083\u0085\u0001\u0000\u0000\u0000"+
		"\u0084\u0081\u0001\u0000\u0000\u0000\u0085\u0086\u0001\u0000\u0000\u0000"+
		"\u0086\u0084\u0001\u0000\u0000\u0000\u0086\u0087\u0001\u0000\u0000\u0000"+
		"\u0087\u00a7\u0001\u0000\u0000\u0000\u0088\u0089\u0003\f\u0006\u0000\u0089"+
		"\u008a\u0006\u0010\uffff\uffff\u0000\u008a\u00a7\u0001\u0000\u0000\u0000"+
		"\u008b\u008c\u0003\u000e\u0007\u0000\u008c\u008d\u0006\u0010\uffff\uffff"+
		"\u0000\u008d\u00a7\u0001\u0000\u0000\u0000\u008e\u008f\u0003\u0010\b\u0000"+
		"\u008f\u0090\u0006\u0010\uffff\uffff\u0000\u0090\u00a7\u0001\u0000\u0000"+
		"\u0000\u0091\u0092\u0003\u0012\t\u0000\u0092\u0093\u0006\u0010\uffff\uffff"+
		"\u0000\u0093\u00a7\u0001\u0000\u0000\u0000\u0094\u0095\u0003\u0014\n\u0000"+
		"\u0095\u0096\u0006\u0010\uffff\uffff\u0000\u0096\u00a7\u0001\u0000\u0000"+
		"\u0000\u0097\u0098\u0003\u0016\u000b\u0000\u0098\u0099\u0006\u0010\uffff"+
		"\uffff\u0000\u0099\u00a7\u0001\u0000\u0000\u0000\u009a\u009b\u0003\u0018"+
		"\f\u0000\u009b\u009c\u0006\u0010\uffff\uffff\u0000\u009c\u00a7\u0001\u0000"+
		"\u0000\u0000\u009d\u009e\u0003\u001a\r\u0000\u009e\u009f\u0006\u0010\uffff"+
		"\uffff\u0000\u009f\u00a7\u0001\u0000\u0000\u0000\u00a0\u00a1\u0003\u001c"+
		"\u000e\u0000\u00a1\u00a2\u0006\u0010\uffff\uffff\u0000\u00a2\u00a7\u0001"+
		"\u0000\u0000\u0000\u00a3\u00a4\u0003\u001e\u000f\u0000\u00a4\u00a5\u0006"+
		"\u0010\uffff\uffff\u0000\u00a5\u00a7\u0001\u0000\u0000\u0000\u00a6{\u0001"+
		"\u0000\u0000\u0000\u00a6~\u0001\u0000\u0000\u0000\u00a6\u0084\u0001\u0000"+
		"\u0000\u0000\u00a6\u0088\u0001\u0000\u0000\u0000\u00a6\u008b\u0001\u0000"+
		"\u0000\u0000\u00a6\u008e\u0001\u0000\u0000\u0000\u00a6\u0091\u0001\u0000"+
		"\u0000\u0000\u00a6\u0094\u0001\u0000\u0000\u0000\u00a6\u0097\u0001\u0000"+
		"\u0000\u0000\u00a6\u009a\u0001\u0000\u0000\u0000\u00a6\u009d\u0001\u0000"+
		"\u0000\u0000\u00a6\u00a0\u0001\u0000\u0000\u0000\u00a6\u00a3\u0001\u0000"+
		"\u0000\u0000\u00a7\u00aa\u0001\u0000\u0000\u0000\u00a8\u00a6\u0001\u0000"+
		"\u0000\u0000\u00a8\u00a9\u0001\u0000\u0000\u0000\u00a9!\u0001\u0000\u0000"+
		"\u0000\u00aa\u00a8\u0001\u0000\u0000\u0000\u00ab\u00ac\u0003 \u0010\u0000"+
		"\u00ac\u00ad\u0006\u0011\uffff\uffff\u0000\u00ad\u00af\u0001\u0000\u0000"+
		"\u0000\u00ae\u00ab\u0001\u0000\u0000\u0000\u00af\u00b0\u0001\u0000\u0000"+
		"\u0000\u00b0\u00ae\u0001\u0000\u0000\u0000\u00b0\u00b1\u0001\u0000\u0000"+
		"\u0000\u00b1\u00b2\u0001\u0000\u0000\u0000\u00b2\u00b3\u0005\u0000\u0000"+
		"\u0001\u00b3#\u0001\u0000\u0000\u0000\u0006Ds\u0086\u00a6\u00a8\u00b0";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}