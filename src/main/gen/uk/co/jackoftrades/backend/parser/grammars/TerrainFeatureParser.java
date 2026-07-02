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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/TerrainFeature.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars;

            import java.util.List;
            import java.util.ArrayList;

            import uk.co.jackoftrades.backend.utils.Flag;
            import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
            import uk.co.jackoftrades.middle.cave.enums.TerrainFlags;
            import uk.co.jackoftrades.middle.cave.enums.TerrainFeatureFlags;
            import uk.co.jackoftrades.middle.cave.Feature;
            import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
            import uk.co.jackoftrades.frontend.colour.enums.AttributeColour;
            import uk.co.jackoftrades.frontend.colour.enums.ColourType;
            import uk.co.jackoftrades.frontend.colour.enums.ColourTranslation;
        
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class TerrainFeatureParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, COMMENT=2, EOL=3, CODE=4, NAME=5, GRAPHICS=6, MIMIC=7, PRIORITY=8, 
		FLAGS=9, DIGGING=10, WALK_MESSAGE=11, RUN_MESSAGE=12, HURT_MESSAGE=13, 
		DIE_MESSAGE=14, CONFUSED_MESSAGE=15, LOOK_PREFIX=16, LOOK_IN_PREPOSITION=17, 
		RESIST_FLAG=18, DESC=19, NUMBER=20, GRAPHICS_COLOUR=21, GRAPHICS_SYMBOL=22, 
		GRAPHICS_CHARACTER=23, TEXT=24;
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
			null, "'|'", null, null, "'code:'", "'name:'", "'graphics:'", "'mimic:'", 
			"'priority:'", "'flags:'", "'digging:'", "'walk-msg:'", "'run-msg:'", 
			"'hurt-msg:'", "'die-msg:'", "'confused-msg:'", "'look-prefix:'", "'look-in-preposition:'", 
			"'resist-flag:'", "'desc:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, "COMMENT", "EOL", "CODE", "NAME", "GRAPHICS", "MIMIC", "PRIORITY", 
			"FLAGS", "DIGGING", "WALK_MESSAGE", "RUN_MESSAGE", "HURT_MESSAGE", "DIE_MESSAGE", 
			"CONFUSED_MESSAGE", "LOOK_PREFIX", "LOOK_IN_PREPOSITION", "RESIST_FLAG", 
			"DESC", "NUMBER", "GRAPHICS_COLOUR", "GRAPHICS_SYMBOL", "GRAPHICS_CHARACTER", 
			"TEXT"
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
	public String getGrammarFileName() { return "TerrainFeature.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public TerrainFeatureParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CodeContext extends ParserRuleContext {
		public TerrainFlags codeFlag;
		public Token TEXT;
		public TerminalNode CODE() { return getToken(TerrainFeatureParser.CODE, 0); }
		public TerminalNode TEXT() { return getToken(TerrainFeatureParser.TEXT, 0); }
		public CodeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_code; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).enterCode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).exitCode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureVisitor ) return ((TerrainFeatureVisitor<? extends T>)visitor).visitCode(this);
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
			((CodeContext)_localctx).TEXT = match(TEXT);

			                ((CodeContext)_localctx).codeFlag =  TerrainFlags.valueOf("FEAT_" + ((CodeContext)_localctx).TEXT.getText());
			            
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
		public TerminalNode NAME() { return getToken(TerrainFeatureParser.NAME, 0); }
		public TerminalNode TEXT() { return getToken(TerrainFeatureParser.TEXT, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureVisitor ) return ((TerrainFeatureVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(40);
			match(NAME);
			setState(41);
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
	public static class GraphicsContext extends ParserRuleContext {
		public String graphicsStr;
		public Token GRAPHICS_CHARACTER;
		public TerminalNode GRAPHICS() { return getToken(TerrainFeatureParser.GRAPHICS, 0); }
		public TerminalNode GRAPHICS_CHARACTER() { return getToken(TerrainFeatureParser.GRAPHICS_CHARACTER, 0); }
		public GraphicsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_graphics; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).enterGraphics(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).exitGraphics(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureVisitor ) return ((TerrainFeatureVisitor<? extends T>)visitor).visitGraphics(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GraphicsContext graphics() throws RecognitionException {
		GraphicsContext _localctx = new GraphicsContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_graphics);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			match(GRAPHICS);
			setState(45);
			((GraphicsContext)_localctx).GRAPHICS_CHARACTER = match(GRAPHICS_CHARACTER);

			                ((GraphicsContext)_localctx).graphicsStr =  ((GraphicsContext)_localctx).GRAPHICS_CHARACTER.getText();
			            
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
		public TerrainFlags mimicFlag;
		public Token TEXT;
		public TerminalNode MIMIC() { return getToken(TerrainFeatureParser.MIMIC, 0); }
		public TerminalNode TEXT() { return getToken(TerrainFeatureParser.TEXT, 0); }
		public MimicContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mimic; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).enterMimic(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).exitMimic(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureVisitor ) return ((TerrainFeatureVisitor<? extends T>)visitor).visitMimic(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MimicContext mimic() throws RecognitionException {
		MimicContext _localctx = new MimicContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_mimic);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			match(MIMIC);
			setState(49);
			((MimicContext)_localctx).TEXT = match(TEXT);

			                ((MimicContext)_localctx).mimicFlag =  TerrainFlags.valueOf("FEAT_" + ((MimicContext)_localctx).TEXT.getText());
			            
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
		public int priorityNum;
		public Token NUMBER;
		public TerminalNode PRIORITY() { return getToken(TerrainFeatureParser.PRIORITY, 0); }
		public TerminalNode NUMBER() { return getToken(TerrainFeatureParser.NUMBER, 0); }
		public PriorityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_priority; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).enterPriority(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).exitPriority(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureVisitor ) return ((TerrainFeatureVisitor<? extends T>)visitor).visitPriority(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PriorityContext priority() throws RecognitionException {
		PriorityContext _localctx = new PriorityContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_priority);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			match(PRIORITY);
			setState(53);
			((PriorityContext)_localctx).NUMBER = match(NUMBER);

			                ((PriorityContext)_localctx).priorityNum =  Integer.parseInt(((PriorityContext)_localctx).NUMBER.getText());
			            
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
		public Flag<TerrainFeatureFlags> flagsList;
		public Token flag1;
		public Token flag2;
		public TerminalNode FLAGS() { return getToken(TerrainFeatureParser.FLAGS, 0); }
		public List<TerminalNode> TEXT() { return getTokens(TerrainFeatureParser.TEXT); }
		public TerminalNode TEXT(int i) {
			return getToken(TerrainFeatureParser.TEXT, i);
		}
		public FlagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flags; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).enterFlags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).exitFlags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureVisitor ) return ((TerrainFeatureVisitor<? extends T>)visitor).visitFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsContext flags() throws RecognitionException {
		FlagsContext _localctx = new FlagsContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_flags);

		            ((FlagsContext)_localctx).flagsList =  new Flag<TerrainFeatureFlags>(TerrainFeatureFlags.class);
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			match(FLAGS);
			setState(57);
			((FlagsContext)_localctx).flag1 = match(TEXT);

			                String raw1 = ((FlagsContext)_localctx).flag1.getText().trim();
			                TerrainFeatureFlags flagEnum1 = TerrainFeatureFlags.valueOf("TF_" + raw1);
			                _localctx.flagsList.on(flagEnum1);
			            
			setState(64);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(59);
				match(T__0);
				setState(60);
				((FlagsContext)_localctx).flag2 = match(TEXT);

				                String raw2 = ((FlagsContext)_localctx).flag2.getText().trim();
				                TerrainFeatureFlags flagEnum2 = TerrainFeatureFlags.valueOf("TF_" + raw2);
				                _localctx.flagsList.on(flagEnum2);
				            
				}
				}
				setState(66);
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
	public static class DiggingContext extends ParserRuleContext {
		public int diggingNum;
		public Token NUMBER;
		public TerminalNode DIGGING() { return getToken(TerrainFeatureParser.DIGGING, 0); }
		public TerminalNode NUMBER() { return getToken(TerrainFeatureParser.NUMBER, 0); }
		public DiggingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_digging; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).enterDigging(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).exitDigging(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureVisitor ) return ((TerrainFeatureVisitor<? extends T>)visitor).visitDigging(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DiggingContext digging() throws RecognitionException {
		DiggingContext _localctx = new DiggingContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_digging);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(67);
			match(DIGGING);
			setState(68);
			((DiggingContext)_localctx).NUMBER = match(NUMBER);

			                ((DiggingContext)_localctx).diggingNum =  Integer.parseInt(((DiggingContext)_localctx).NUMBER.getText());
			            
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
		public Token TEXT;
		public TerminalNode WALK_MESSAGE() { return getToken(TerrainFeatureParser.WALK_MESSAGE, 0); }
		public TerminalNode TEXT() { return getToken(TerrainFeatureParser.TEXT, 0); }
		public Walk_messageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_walk_message; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).enterWalk_message(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).exitWalk_message(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureVisitor ) return ((TerrainFeatureVisitor<? extends T>)visitor).visitWalk_message(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Walk_messageContext walk_message() throws RecognitionException {
		Walk_messageContext _localctx = new Walk_messageContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_walk_message);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(71);
			match(WALK_MESSAGE);
			setState(72);
			((Walk_messageContext)_localctx).TEXT = match(TEXT);

			                ((Walk_messageContext)_localctx).walkMsgStr =  ((Walk_messageContext)_localctx).TEXT.getText();
			            
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
		public Token TEXT;
		public TerminalNode RUN_MESSAGE() { return getToken(TerrainFeatureParser.RUN_MESSAGE, 0); }
		public TerminalNode TEXT() { return getToken(TerrainFeatureParser.TEXT, 0); }
		public Run_messageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_run_message; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).enterRun_message(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).exitRun_message(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureVisitor ) return ((TerrainFeatureVisitor<? extends T>)visitor).visitRun_message(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Run_messageContext run_message() throws RecognitionException {
		Run_messageContext _localctx = new Run_messageContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_run_message);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(75);
			match(RUN_MESSAGE);
			setState(76);
			((Run_messageContext)_localctx).TEXT = match(TEXT);

			                ((Run_messageContext)_localctx).runMsgStr =  ((Run_messageContext)_localctx).TEXT.getText();
			            
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
		public Token TEXT;
		public TerminalNode HURT_MESSAGE() { return getToken(TerrainFeatureParser.HURT_MESSAGE, 0); }
		public TerminalNode TEXT() { return getToken(TerrainFeatureParser.TEXT, 0); }
		public Hurt_messageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hurt_message; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).enterHurt_message(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).exitHurt_message(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureVisitor ) return ((TerrainFeatureVisitor<? extends T>)visitor).visitHurt_message(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Hurt_messageContext hurt_message() throws RecognitionException {
		Hurt_messageContext _localctx = new Hurt_messageContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_hurt_message);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(79);
			match(HURT_MESSAGE);
			setState(80);
			((Hurt_messageContext)_localctx).TEXT = match(TEXT);

			                ((Hurt_messageContext)_localctx).hurtMsgStr =  ((Hurt_messageContext)_localctx).TEXT.getText();
			            
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
		public Token TEXT;
		public TerminalNode DIE_MESSAGE() { return getToken(TerrainFeatureParser.DIE_MESSAGE, 0); }
		public TerminalNode TEXT() { return getToken(TerrainFeatureParser.TEXT, 0); }
		public Die_messageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_die_message; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).enterDie_message(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).exitDie_message(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureVisitor ) return ((TerrainFeatureVisitor<? extends T>)visitor).visitDie_message(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Die_messageContext die_message() throws RecognitionException {
		Die_messageContext _localctx = new Die_messageContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_die_message);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(83);
			match(DIE_MESSAGE);
			setState(84);
			((Die_messageContext)_localctx).TEXT = match(TEXT);

			                ((Die_messageContext)_localctx).dieMsgStr =  ((Die_messageContext)_localctx).TEXT.getText();
			            
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
		public Token TEXT;
		public TerminalNode CONFUSED_MESSAGE() { return getToken(TerrainFeatureParser.CONFUSED_MESSAGE, 0); }
		public TerminalNode TEXT() { return getToken(TerrainFeatureParser.TEXT, 0); }
		public Confused_messageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_confused_message; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).enterConfused_message(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).exitConfused_message(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureVisitor ) return ((TerrainFeatureVisitor<? extends T>)visitor).visitConfused_message(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Confused_messageContext confused_message() throws RecognitionException {
		Confused_messageContext _localctx = new Confused_messageContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_confused_message);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(87);
			match(CONFUSED_MESSAGE);
			setState(88);
			((Confused_messageContext)_localctx).TEXT = match(TEXT);

			                ((Confused_messageContext)_localctx).confMsgStr =  ((Confused_messageContext)_localctx).TEXT.getText();
			            
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
		public Token TEXT;
		public TerminalNode LOOK_PREFIX() { return getToken(TerrainFeatureParser.LOOK_PREFIX, 0); }
		public TerminalNode TEXT() { return getToken(TerrainFeatureParser.TEXT, 0); }
		public Look_prefixContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_look_prefix; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).enterLook_prefix(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).exitLook_prefix(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureVisitor ) return ((TerrainFeatureVisitor<? extends T>)visitor).visitLook_prefix(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Look_prefixContext look_prefix() throws RecognitionException {
		Look_prefixContext _localctx = new Look_prefixContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_look_prefix);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(91);
			match(LOOK_PREFIX);
			setState(92);
			((Look_prefixContext)_localctx).TEXT = match(TEXT);

			                ((Look_prefixContext)_localctx).prefixStr =  ((Look_prefixContext)_localctx).TEXT.getText();
			            
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
		public Token TEXT;
		public TerminalNode LOOK_IN_PREPOSITION() { return getToken(TerrainFeatureParser.LOOK_IN_PREPOSITION, 0); }
		public TerminalNode TEXT() { return getToken(TerrainFeatureParser.TEXT, 0); }
		public Look_in_prepositionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_look_in_preposition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).enterLook_in_preposition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).exitLook_in_preposition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureVisitor ) return ((TerrainFeatureVisitor<? extends T>)visitor).visitLook_in_preposition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Look_in_prepositionContext look_in_preposition() throws RecognitionException {
		Look_in_prepositionContext _localctx = new Look_in_prepositionContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_look_in_preposition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			match(LOOK_IN_PREPOSITION);
			setState(96);
			((Look_in_prepositionContext)_localctx).TEXT = match(TEXT);

			                ((Look_in_prepositionContext)_localctx).prepositionStr =  ((Look_in_prepositionContext)_localctx).TEXT.getText();
			            
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
		public MonsterRaceFlag resistFlag;
		public Token TEXT;
		public TerminalNode RESIST_FLAG() { return getToken(TerrainFeatureParser.RESIST_FLAG, 0); }
		public TerminalNode TEXT() { return getToken(TerrainFeatureParser.TEXT, 0); }
		public Resist_flagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_resist_flag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).enterResist_flag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).exitResist_flag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureVisitor ) return ((TerrainFeatureVisitor<? extends T>)visitor).visitResist_flag(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Resist_flagContext resist_flag() throws RecognitionException {
		Resist_flagContext _localctx = new Resist_flagContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_resist_flag);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			match(RESIST_FLAG);
			setState(100);
			((Resist_flagContext)_localctx).TEXT = match(TEXT);

			                ((Resist_flagContext)_localctx).resistFlag =  MonsterRaceFlag.valueOf("RF_" + ((Resist_flagContext)_localctx).TEXT.getText().trim());
			            
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
		public Token TEXT;
		public TerminalNode DESC() { return getToken(TerrainFeatureParser.DESC, 0); }
		public TerminalNode TEXT() { return getToken(TerrainFeatureParser.TEXT, 0); }
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_desc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureVisitor ) return ((TerrainFeatureVisitor<? extends T>)visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_desc);

		            ((DescContext)_localctx).descStr =  "";
		        
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(103);
			match(DESC);
			setState(104);
			((DescContext)_localctx).TEXT = match(TEXT);

			                ((DescContext)_localctx).descStr =  _localctx.descStr + ((DescContext)_localctx).TEXT.getText();
			            
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
	public static class TerrainContext extends ParserRuleContext {
		public uk.co.jackoftrades.middle.cave.Feature feature;
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
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).enterTerrain(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).exitTerrain(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureVisitor ) return ((TerrainFeatureVisitor<? extends T>)visitor).visitTerrain(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TerrainContext terrain() throws RecognitionException {
		TerrainContext _localctx = new TerrainContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_terrain);

		            TerrainFlags codeInit = TerrainFlags.FEAT_NONE;
		            String nameInit = "";
		            String graphicsStringInit = "";
		            int priorityInit = 0;
		            TerrainFlags mimicInit = TerrainFlags.FEAT_NONE;
		            Flag<TerrainFeatureFlags> tfFlags = new Flag<>(TerrainFeatureFlags.class);
		            int diggingInit = 0;
		            String hurtInit = "";
		            String walkInit = "";
		            String runInit = "";
		            String dieInit = "";
		            String confInit = "";
		            String prefixInit = "";
		            String prepositionInit = "";
		            MonsterRaceFlag resistInit = MonsterRaceFlag.RF_NONE;
		            String descInit = "";
		        
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(107);
			((TerrainContext)_localctx).code = code();

			                codeInit = ((TerrainContext)_localctx).code.codeFlag;
			            
			setState(109);
			((TerrainContext)_localctx).name = name();

			                nameInit = ((TerrainContext)_localctx).name.nameStr;
			            
			setState(111);
			((TerrainContext)_localctx).graphics = graphics();

			                graphicsStringInit = ((TerrainContext)_localctx).graphics.graphicsStr;
			            
			setState(158);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1048448L) != 0)) {
				{
				setState(156);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case PRIORITY:
					{
					setState(113);
					((TerrainContext)_localctx).priority = priority();

					                    priorityInit = ((TerrainContext)_localctx).priority.priorityNum;
					                
					}
					break;
				case MIMIC:
					{
					setState(116);
					((TerrainContext)_localctx).mimic = mimic();

					                    mimicInit = ((TerrainContext)_localctx).mimic.mimicFlag;
					                
					}
					break;
				case FLAGS:
					{
					setState(122); 
					_errHandler.sync(this);
					_alt = 1;
					do {
						switch (_alt) {
						case 1:
							{
							{
							setState(119);
							((TerrainContext)_localctx).flags = flags();

							                    tfFlags.union(((TerrainContext)_localctx).flags.flagsList);
							                
							}
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						setState(124); 
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
					} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
					}
					break;
				case DIGGING:
					{
					setState(126);
					((TerrainContext)_localctx).digging = digging();

					                    diggingInit = ((TerrainContext)_localctx).digging.diggingNum;
					                
					}
					break;
				case WALK_MESSAGE:
					{
					setState(129);
					((TerrainContext)_localctx).walk_message = walk_message();

					                    walkInit = ((TerrainContext)_localctx).walk_message.walkMsgStr;
					                
					}
					break;
				case RUN_MESSAGE:
					{
					setState(132);
					((TerrainContext)_localctx).run_message = run_message();

					                    runInit = ((TerrainContext)_localctx).run_message.runMsgStr;
					                
					}
					break;
				case HURT_MESSAGE:
					{
					setState(135);
					((TerrainContext)_localctx).hurt_message = hurt_message();

					                    hurtInit = ((TerrainContext)_localctx).hurt_message.hurtMsgStr;
					                
					}
					break;
				case DIE_MESSAGE:
					{
					setState(138);
					((TerrainContext)_localctx).die_message = die_message();

					                    dieInit = ((TerrainContext)_localctx).die_message.dieMsgStr;
					                
					}
					break;
				case CONFUSED_MESSAGE:
					{
					setState(141);
					((TerrainContext)_localctx).confused_message = confused_message();

					                    confInit = ((TerrainContext)_localctx).confused_message.confMsgStr;
					                
					}
					break;
				case LOOK_PREFIX:
					{
					setState(144);
					((TerrainContext)_localctx).look_prefix = look_prefix();

					                    prefixInit = ((TerrainContext)_localctx).look_prefix.prefixStr;
					                
					}
					break;
				case LOOK_IN_PREPOSITION:
					{
					setState(147);
					((TerrainContext)_localctx).look_in_preposition = look_in_preposition();

					                    prepositionInit = ((TerrainContext)_localctx).look_in_preposition.prepositionStr;
					                
					}
					break;
				case RESIST_FLAG:
					{
					setState(150);
					((TerrainContext)_localctx).resist_flag = resist_flag();

					                    resistInit = ((TerrainContext)_localctx).resist_flag.resistFlag;
					                
					}
					break;
				case DESC:
					{
					setState(153);
					((TerrainContext)_localctx).desc = desc();

					                    descInit = descInit + ((TerrainContext)_localctx).desc.descStr;
					                
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(160);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			_ctx.stop = _input.LT(-1);

			            char graphicsChar = graphicsStringInit.charAt(0);
			            char graphicsColour = graphicsStringInit.charAt(2);
			            AttributeColour colour = ColourType.getAttributeColour(graphicsColour, ColourTranslation.ATTR_FULL);

			            int shopNum = -1;

			            if (tfFlags.has(TerrainFeatureFlags.TF_SHOP)) {
			                shopNum = Character.getNumericValue(graphicsChar);
			            }

			            AngbandDisplayCharacter dc = new AngbandDisplayCharacter(graphicsChar, colour);

			            ((TerrainContext)_localctx).feature =  new Feature(codeInit, nameInit, descInit, mimicInit,
			                                   priorityInit, shopNum, diggingInit, tfFlags,
			                                   dc, walkInit, runInit, hurtInit, dieInit,
			                                   confInit, prefixInit, prepositionInit,
			                                   resistInit);
			        
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
		public List<uk.co.jackoftrades.middle.cave.Feature> features;
		public TerrainContext terrain;
		public TerminalNode EOF() { return getToken(TerrainFeatureParser.EOF, 0); }
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
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TerrainFeatureListener ) ((TerrainFeatureListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TerrainFeatureVisitor ) return ((TerrainFeatureVisitor<? extends T>)visitor).visitFile(this);
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
			setState(164); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(161);
				((FileContext)_localctx).terrain = terrain();

				                _localctx.features.add(((FileContext)_localctx).terrain.feature);
				            
				}
				}
				setState(166); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==CODE );
			setState(168);
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
		"\u0004\u0001\u0018\u00ab\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0005\u0005?\b\u0005\n\u0005\f\u0005B\t\u0005\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t"+
		"\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0004\u0010"+
		"{\b\u0010\u000b\u0010\f\u0010|\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0005\u0010\u009d\b\u0010\n\u0010\f\u0010"+
		"\u00a0\t\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0004\u0011\u00a5\b"+
		"\u0011\u000b\u0011\f\u0011\u00a6\u0001\u0011\u0001\u0011\u0001\u0011\u0000"+
		"\u0000\u0012\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016"+
		"\u0018\u001a\u001c\u001e \"\u0000\u0000\u00a8\u0000$\u0001\u0000\u0000"+
		"\u0000\u0002(\u0001\u0000\u0000\u0000\u0004,\u0001\u0000\u0000\u0000\u0006"+
		"0\u0001\u0000\u0000\u0000\b4\u0001\u0000\u0000\u0000\n8\u0001\u0000\u0000"+
		"\u0000\fC\u0001\u0000\u0000\u0000\u000eG\u0001\u0000\u0000\u0000\u0010"+
		"K\u0001\u0000\u0000\u0000\u0012O\u0001\u0000\u0000\u0000\u0014S\u0001"+
		"\u0000\u0000\u0000\u0016W\u0001\u0000\u0000\u0000\u0018[\u0001\u0000\u0000"+
		"\u0000\u001a_\u0001\u0000\u0000\u0000\u001cc\u0001\u0000\u0000\u0000\u001e"+
		"g\u0001\u0000\u0000\u0000 k\u0001\u0000\u0000\u0000\"\u00a4\u0001\u0000"+
		"\u0000\u0000$%\u0005\u0004\u0000\u0000%&\u0005\u0018\u0000\u0000&\'\u0006"+
		"\u0000\uffff\uffff\u0000\'\u0001\u0001\u0000\u0000\u0000()\u0005\u0005"+
		"\u0000\u0000)*\u0005\u0018\u0000\u0000*+\u0006\u0001\uffff\uffff\u0000"+
		"+\u0003\u0001\u0000\u0000\u0000,-\u0005\u0006\u0000\u0000-.\u0005\u0017"+
		"\u0000\u0000./\u0006\u0002\uffff\uffff\u0000/\u0005\u0001\u0000\u0000"+
		"\u000001\u0005\u0007\u0000\u000012\u0005\u0018\u0000\u000023\u0006\u0003"+
		"\uffff\uffff\u00003\u0007\u0001\u0000\u0000\u000045\u0005\b\u0000\u0000"+
		"56\u0005\u0014\u0000\u000067\u0006\u0004\uffff\uffff\u00007\t\u0001\u0000"+
		"\u0000\u000089\u0005\t\u0000\u00009:\u0005\u0018\u0000\u0000:@\u0006\u0005"+
		"\uffff\uffff\u0000;<\u0005\u0001\u0000\u0000<=\u0005\u0018\u0000\u0000"+
		"=?\u0006\u0005\uffff\uffff\u0000>;\u0001\u0000\u0000\u0000?B\u0001\u0000"+
		"\u0000\u0000@>\u0001\u0000\u0000\u0000@A\u0001\u0000\u0000\u0000A\u000b"+
		"\u0001\u0000\u0000\u0000B@\u0001\u0000\u0000\u0000CD\u0005\n\u0000\u0000"+
		"DE\u0005\u0014\u0000\u0000EF\u0006\u0006\uffff\uffff\u0000F\r\u0001\u0000"+
		"\u0000\u0000GH\u0005\u000b\u0000\u0000HI\u0005\u0018\u0000\u0000IJ\u0006"+
		"\u0007\uffff\uffff\u0000J\u000f\u0001\u0000\u0000\u0000KL\u0005\f\u0000"+
		"\u0000LM\u0005\u0018\u0000\u0000MN\u0006\b\uffff\uffff\u0000N\u0011\u0001"+
		"\u0000\u0000\u0000OP\u0005\r\u0000\u0000PQ\u0005\u0018\u0000\u0000QR\u0006"+
		"\t\uffff\uffff\u0000R\u0013\u0001\u0000\u0000\u0000ST\u0005\u000e\u0000"+
		"\u0000TU\u0005\u0018\u0000\u0000UV\u0006\n\uffff\uffff\u0000V\u0015\u0001"+
		"\u0000\u0000\u0000WX\u0005\u000f\u0000\u0000XY\u0005\u0018\u0000\u0000"+
		"YZ\u0006\u000b\uffff\uffff\u0000Z\u0017\u0001\u0000\u0000\u0000[\\\u0005"+
		"\u0010\u0000\u0000\\]\u0005\u0018\u0000\u0000]^\u0006\f\uffff\uffff\u0000"+
		"^\u0019\u0001\u0000\u0000\u0000_`\u0005\u0011\u0000\u0000`a\u0005\u0018"+
		"\u0000\u0000ab\u0006\r\uffff\uffff\u0000b\u001b\u0001\u0000\u0000\u0000"+
		"cd\u0005\u0012\u0000\u0000de\u0005\u0018\u0000\u0000ef\u0006\u000e\uffff"+
		"\uffff\u0000f\u001d\u0001\u0000\u0000\u0000gh\u0005\u0013\u0000\u0000"+
		"hi\u0005\u0018\u0000\u0000ij\u0006\u000f\uffff\uffff\u0000j\u001f\u0001"+
		"\u0000\u0000\u0000kl\u0003\u0000\u0000\u0000lm\u0006\u0010\uffff\uffff"+
		"\u0000mn\u0003\u0002\u0001\u0000no\u0006\u0010\uffff\uffff\u0000op\u0003"+
		"\u0004\u0002\u0000p\u009e\u0006\u0010\uffff\uffff\u0000qr\u0003\b\u0004"+
		"\u0000rs\u0006\u0010\uffff\uffff\u0000s\u009d\u0001\u0000\u0000\u0000"+
		"tu\u0003\u0006\u0003\u0000uv\u0006\u0010\uffff\uffff\u0000v\u009d\u0001"+
		"\u0000\u0000\u0000wx\u0003\n\u0005\u0000xy\u0006\u0010\uffff\uffff\u0000"+
		"y{\u0001\u0000\u0000\u0000zw\u0001\u0000\u0000\u0000{|\u0001\u0000\u0000"+
		"\u0000|z\u0001\u0000\u0000\u0000|}\u0001\u0000\u0000\u0000}\u009d\u0001"+
		"\u0000\u0000\u0000~\u007f\u0003\f\u0006\u0000\u007f\u0080\u0006\u0010"+
		"\uffff\uffff\u0000\u0080\u009d\u0001\u0000\u0000\u0000\u0081\u0082\u0003"+
		"\u000e\u0007\u0000\u0082\u0083\u0006\u0010\uffff\uffff\u0000\u0083\u009d"+
		"\u0001\u0000\u0000\u0000\u0084\u0085\u0003\u0010\b\u0000\u0085\u0086\u0006"+
		"\u0010\uffff\uffff\u0000\u0086\u009d\u0001\u0000\u0000\u0000\u0087\u0088"+
		"\u0003\u0012\t\u0000\u0088\u0089\u0006\u0010\uffff\uffff\u0000\u0089\u009d"+
		"\u0001\u0000\u0000\u0000\u008a\u008b\u0003\u0014\n\u0000\u008b\u008c\u0006"+
		"\u0010\uffff\uffff\u0000\u008c\u009d\u0001\u0000\u0000\u0000\u008d\u008e"+
		"\u0003\u0016\u000b\u0000\u008e\u008f\u0006\u0010\uffff\uffff\u0000\u008f"+
		"\u009d\u0001\u0000\u0000\u0000\u0090\u0091\u0003\u0018\f\u0000\u0091\u0092"+
		"\u0006\u0010\uffff\uffff\u0000\u0092\u009d\u0001\u0000\u0000\u0000\u0093"+
		"\u0094\u0003\u001a\r\u0000\u0094\u0095\u0006\u0010\uffff\uffff\u0000\u0095"+
		"\u009d\u0001\u0000\u0000\u0000\u0096\u0097\u0003\u001c\u000e\u0000\u0097"+
		"\u0098\u0006\u0010\uffff\uffff\u0000\u0098\u009d\u0001\u0000\u0000\u0000"+
		"\u0099\u009a\u0003\u001e\u000f\u0000\u009a\u009b\u0006\u0010\uffff\uffff"+
		"\u0000\u009b\u009d\u0001\u0000\u0000\u0000\u009cq\u0001\u0000\u0000\u0000"+
		"\u009ct\u0001\u0000\u0000\u0000\u009cz\u0001\u0000\u0000\u0000\u009c~"+
		"\u0001\u0000\u0000\u0000\u009c\u0081\u0001\u0000\u0000\u0000\u009c\u0084"+
		"\u0001\u0000\u0000\u0000\u009c\u0087\u0001\u0000\u0000\u0000\u009c\u008a"+
		"\u0001\u0000\u0000\u0000\u009c\u008d\u0001\u0000\u0000\u0000\u009c\u0090"+
		"\u0001\u0000\u0000\u0000\u009c\u0093\u0001\u0000\u0000\u0000\u009c\u0096"+
		"\u0001\u0000\u0000\u0000\u009c\u0099\u0001\u0000\u0000\u0000\u009d\u00a0"+
		"\u0001\u0000\u0000\u0000\u009e\u009c\u0001\u0000\u0000\u0000\u009e\u009f"+
		"\u0001\u0000\u0000\u0000\u009f!\u0001\u0000\u0000\u0000\u00a0\u009e\u0001"+
		"\u0000\u0000\u0000\u00a1\u00a2\u0003 \u0010\u0000\u00a2\u00a3\u0006\u0011"+
		"\uffff\uffff\u0000\u00a3\u00a5\u0001\u0000\u0000\u0000\u00a4\u00a1\u0001"+
		"\u0000\u0000\u0000\u00a5\u00a6\u0001\u0000\u0000\u0000\u00a6\u00a4\u0001"+
		"\u0000\u0000\u0000\u00a6\u00a7\u0001\u0000\u0000\u0000\u00a7\u00a8\u0001"+
		"\u0000\u0000\u0000\u00a8\u00a9\u0005\u0000\u0000\u0001\u00a9#\u0001\u0000"+
		"\u0000\u0000\u0005@|\u009c\u009e\u00a6";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}