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
 *    Java code copyright (c) Rowan Crowther 2026
 */

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/Terrain.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.terrainfeature;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.frontend.colour.enums.AttributeColour;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftrades.middle.cave.enums.TerrainFlags;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class TerrainParser extends Parser {
	static {
		RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
	}

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
			new PredictionContextCache();
	public static final int
			COMMENT = 1, EOL = 2, CODE = 3, NAME = 4, GRAPHICS = 5, MIMIC = 6, PRIORITY = 7, FLAGS = 8,
			DIGGING = 9, WALK_MSG = 10, RUN_MSG = 11, HURT_MSG = 12, DIE_MSG = 13, CONFUSED_MSG = 14,
			LOOK_PREFIX = 15, LOOK_IN_PREP = 16, RESIST_FLAG = 17, DESC = 18, COLON = 19, OR = 20,
			SINGLE_CHAR = 21, NUMBER = 22, TEXT = 23;
	public static final int
			RULE_code = 0, RULE_name = 1, RULE_graphics = 2, RULE_mimic = 3, RULE_priority = 4,
			RULE_flags = 5, RULE_walk_msg = 6, RULE_run_msg = 7, RULE_hurt_msg = 8,
			RULE_die_msg = 9, RULE_confused_msg = 10, RULE_look_prefix = 11, RULE_look_in_preposition = 12,
			RULE_resist_flag = 13, RULE_desc = 14, RULE_digging = 15, RULE_feature = 16,
			RULE_features = 17;
	private static String[] makeRuleNames() {
		return new String[]{
				"code", "name", "graphics", "mimic", "priority", "flags", "walk_msg",
				"run_msg", "hurt_msg", "die_msg", "confused_msg", "look_prefix", "look_in_preposition",
				"resist_flag", "desc", "digging", "feature", "features"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[]{
				null, null, null, "'code:'", "'name:'", "'graphics:'", "'mimic:'", "'priority:'",
				"'flags:'", "'digging:'", "'walk-msg:'", "'run-msg:'", "'hurt-msg:'",
				"'die-msg:'", "'confused-msg:'", "'look-prefix:'", "'look-in-preposition:'",
				"'resist-flag:'", "'desc:'", "':'", "'| '"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[]{
				null, "COMMENT", "EOL", "CODE", "NAME", "GRAPHICS", "MIMIC", "PRIORITY",
				"FLAGS", "DIGGING", "WALK_MSG", "RUN_MSG", "HURT_MSG", "DIE_MSG", "CONFUSED_MSG",
				"LOOK_PREFIX", "LOOK_IN_PREP", "RESIST_FLAG", "DESC", "COLON", "OR",
				"SINGLE_CHAR", "NUMBER", "TEXT"
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
	public String getGrammarFileName() {
		return "Terrain.g4";
	}

	@Override
	public String[] getRuleNames() {
		return ruleNames;
	}

	@Override
	public String getSerializedATN() {
		return _serializedATN;
	}

	@Override
	public ATN getATN() {
		return _ATN;
	}

	public TerrainParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CodeContext extends ParserRuleContext {
		public TerrainFlags codeFlag;
		public Token TEXT;

		public TerminalNode CODE() {
			return getToken(TerrainParser.CODE, 0);
		}

		public TerminalNode TEXT() {
			return getToken(TerrainParser.TEXT, 0);
		}
		public CodeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_code;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).enterCode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).exitCode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof TerrainVisitor) return ((TerrainVisitor<? extends T>) visitor).visitCode(this);
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
				((CodeContext) _localctx).TEXT = match(TEXT);

				((CodeContext) _localctx).codeFlag = TerrainFlags.valueOf("FEAT_" + ((CodeContext) _localctx).TEXT.getText());
			        
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public Token TEXT;

		public TerminalNode NAME() {
			return getToken(TerrainParser.NAME, 0);
		}

		public TerminalNode TEXT() {
			return getToken(TerrainParser.TEXT, 0);
		}
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_name;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof TerrainVisitor) return ((TerrainVisitor<? extends T>) visitor).visitName(this);
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
				((NameContext) _localctx).TEXT = match(TEXT);

				((NameContext) _localctx).nameStr = ((NameContext) _localctx).TEXT.getText();
			        
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class GraphicsContext extends ParserRuleContext {
		public AngbandDisplayCharacter displayCharacter;
		public Token attr;
		public Token colour;

		public TerminalNode GRAPHICS() {
			return getToken(TerrainParser.GRAPHICS, 0);
		}

		public TerminalNode SINGLE_CHAR() {
			return getToken(TerrainParser.SINGLE_CHAR, 0);
		}

		public TerminalNode TEXT() {
			return getToken(TerrainParser.TEXT, 0);
		}
		public GraphicsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_graphics;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).enterGraphics(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).exitGraphics(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof TerrainVisitor) return ((TerrainVisitor<? extends T>) visitor).visitGraphics(this);
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
				((GraphicsContext) _localctx).attr = match(SINGLE_CHAR);
				setState(46);
				((GraphicsContext) _localctx).colour = match(TEXT);

				char displayChar = ((GraphicsContext) _localctx).attr.getText().charAt(0);
				char colourChar = ((GraphicsContext) _localctx).colour.getText().charAt(0);

				AttributeColour attrColour = ColourType.getAttributeColour(colourChar);
				((GraphicsContext) _localctx).displayCharacter = new AngbandDisplayCharacter(displayChar, attrColour);
			        
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MimicContext extends ParserRuleContext {
		public TerrainFlags mimicFlag;
		public Token TEXT;

		public TerminalNode MIMIC() {
			return getToken(TerrainParser.MIMIC, 0);
		}

		public TerminalNode TEXT() {
			return getToken(TerrainParser.TEXT, 0);
		}
		public MimicContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_mimic;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).enterMimic(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).exitMimic(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof TerrainVisitor) return ((TerrainVisitor<? extends T>) visitor).visitMimic(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MimicContext mimic() throws RecognitionException {
		MimicContext _localctx = new MimicContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_mimic);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(49);
				match(MIMIC);
				setState(50);
				((MimicContext) _localctx).TEXT = match(TEXT);

				((MimicContext) _localctx).mimicFlag = TerrainFlags.valueOf("FEAT_" + ((MimicContext) _localctx).TEXT.getText());
			        
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PriorityContext extends ParserRuleContext {
		public int priorityNum;
		public Token NUMBER;

		public TerminalNode PRIORITY() {
			return getToken(TerrainParser.PRIORITY, 0);
		}

		public TerminalNode NUMBER() {
			return getToken(TerrainParser.NUMBER, 0);
		}
		public PriorityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_priority;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).enterPriority(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).exitPriority(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof TerrainVisitor) return ((TerrainVisitor<? extends T>) visitor).visitPriority(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PriorityContext priority() throws RecognitionException {
		PriorityContext _localctx = new PriorityContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_priority);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(53);
				match(PRIORITY);
				setState(54);
				((PriorityContext) _localctx).NUMBER = match(NUMBER);

				((PriorityContext) _localctx).priorityNum = Integer.parseInt(((PriorityContext) _localctx).NUMBER.getText());
			        
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FlagsContext extends ParserRuleContext {
		public ArrayList<TerrainFeatureFlags> flagList;
		public Token flag1;
		public Token flag2;

		public List<TerminalNode> FLAGS() {
			return getTokens(TerrainParser.FLAGS);
		}
		public TerminalNode FLAGS(int i) {
			return getToken(TerrainParser.FLAGS, i);
		}

		public List<TerminalNode> TEXT() {
			return getTokens(TerrainParser.TEXT);
		}
		public TerminalNode TEXT(int i) {
			return getToken(TerrainParser.TEXT, i);
		}

		public List<TerminalNode> OR() {
			return getTokens(TerrainParser.OR);
		}
		public TerminalNode OR(int i) {
			return getToken(TerrainParser.OR, i);
		}
		public FlagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_flags;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).enterFlags(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).exitFlags(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof TerrainVisitor) return ((TerrainVisitor<? extends T>) visitor).visitFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsContext flags() throws RecognitionException {
		FlagsContext _localctx = new FlagsContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_flags);

		((FlagsContext) _localctx).flagList = new ArrayList<>();
		        
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
				setState(68);
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
						case 1: {
							{
								setState(57);
								match(FLAGS);
								setState(58);
								((FlagsContext) _localctx).flag1 = match(TEXT);

								_localctx.flagList.add(TerrainFeatureFlags.valueOf("TF_" + ((FlagsContext) _localctx).flag1.getText().trim()));

								setState(65);
								_errHandler.sync(this);
								_la = _input.LA(1);
								while (_la == OR) {
									{
										{
											setState(60);
											match(OR);
											setState(61);
											((FlagsContext) _localctx).flag2 = match(TEXT);

											_localctx.flagList.add(TerrainFeatureFlags.valueOf("TF_" + ((FlagsContext) _localctx).flag2.getText().trim()));

										}
									}
									setState(67);
									_errHandler.sync(this);
									_la = _input.LA(1);
								}
							}
						}
						break;
						default:
							throw new NoViableAltException(this);
					}
					setState(70);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input, 1, _ctx);
				} while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Walk_msgContext extends ParserRuleContext {
		public String walkMsgStr;
		public Token TEXT;

		public TerminalNode WALK_MSG() {
			return getToken(TerrainParser.WALK_MSG, 0);
		}

		public TerminalNode TEXT() {
			return getToken(TerrainParser.TEXT, 0);
		}
		public Walk_msgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_walk_msg;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).enterWalk_msg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).exitWalk_msg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof TerrainVisitor) return ((TerrainVisitor<? extends T>) visitor).visitWalk_msg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Walk_msgContext walk_msg() throws RecognitionException {
		Walk_msgContext _localctx = new Walk_msgContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_walk_msg);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(72);
				match(WALK_MSG);
				setState(73);
				((Walk_msgContext) _localctx).TEXT = match(TEXT);

				((Walk_msgContext) _localctx).walkMsgStr = ((Walk_msgContext) _localctx).TEXT.getText();
			        
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Run_msgContext extends ParserRuleContext {
		public String runMsgStr;
		public Token TEXT;

		public TerminalNode RUN_MSG() {
			return getToken(TerrainParser.RUN_MSG, 0);
		}

		public TerminalNode TEXT() {
			return getToken(TerrainParser.TEXT, 0);
		}
		public Run_msgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_run_msg;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).enterRun_msg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).exitRun_msg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof TerrainVisitor) return ((TerrainVisitor<? extends T>) visitor).visitRun_msg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Run_msgContext run_msg() throws RecognitionException {
		Run_msgContext _localctx = new Run_msgContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_run_msg);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(76);
				match(RUN_MSG);
				setState(77);
				((Run_msgContext) _localctx).TEXT = match(TEXT);

				((Run_msgContext) _localctx).runMsgStr = ((Run_msgContext) _localctx).TEXT.getText();
			        
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Hurt_msgContext extends ParserRuleContext {
		public String hurtMsgStr;
		public Token TEXT;

		public TerminalNode HURT_MSG() {
			return getToken(TerrainParser.HURT_MSG, 0);
		}

		public TerminalNode TEXT() {
			return getToken(TerrainParser.TEXT, 0);
		}
		public Hurt_msgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_hurt_msg;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).enterHurt_msg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).exitHurt_msg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof TerrainVisitor) return ((TerrainVisitor<? extends T>) visitor).visitHurt_msg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Hurt_msgContext hurt_msg() throws RecognitionException {
		Hurt_msgContext _localctx = new Hurt_msgContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_hurt_msg);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(80);
				match(HURT_MSG);
				setState(81);
				((Hurt_msgContext) _localctx).TEXT = match(TEXT);

				((Hurt_msgContext) _localctx).hurtMsgStr = ((Hurt_msgContext) _localctx).TEXT.getText();
			        
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Die_msgContext extends ParserRuleContext {
		public String dieMsgStr;
		public Token TEXT;

		public TerminalNode DIE_MSG() {
			return getToken(TerrainParser.DIE_MSG, 0);
		}

		public TerminalNode TEXT() {
			return getToken(TerrainParser.TEXT, 0);
		}
		public Die_msgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_die_msg;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).enterDie_msg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).exitDie_msg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof TerrainVisitor) return ((TerrainVisitor<? extends T>) visitor).visitDie_msg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Die_msgContext die_msg() throws RecognitionException {
		Die_msgContext _localctx = new Die_msgContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_die_msg);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(84);
				match(DIE_MSG);
				setState(85);
				((Die_msgContext) _localctx).TEXT = match(TEXT);

				((Die_msgContext) _localctx).dieMsgStr = ((Die_msgContext) _localctx).TEXT.getText();
			        
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Confused_msgContext extends ParserRuleContext {
		public String confusedMsgStr;
		public Token TEXT;

		public TerminalNode CONFUSED_MSG() {
			return getToken(TerrainParser.CONFUSED_MSG, 0);
		}

		public TerminalNode TEXT() {
			return getToken(TerrainParser.TEXT, 0);
		}
		public Confused_msgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_confused_msg;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).enterConfused_msg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).exitConfused_msg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof TerrainVisitor)
				return ((TerrainVisitor<? extends T>) visitor).visitConfused_msg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Confused_msgContext confused_msg() throws RecognitionException {
		Confused_msgContext _localctx = new Confused_msgContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_confused_msg);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(88);
				match(CONFUSED_MSG);
				setState(89);
				((Confused_msgContext) _localctx).TEXT = match(TEXT);

				((Confused_msgContext) _localctx).confusedMsgStr = ((Confused_msgContext) _localctx).TEXT.getText();
			        
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Look_prefixContext extends ParserRuleContext {
		public String lookPrefix;
		public Token TEXT;

		public TerminalNode LOOK_PREFIX() {
			return getToken(TerrainParser.LOOK_PREFIX, 0);
		}

		public TerminalNode TEXT() {
			return getToken(TerrainParser.TEXT, 0);
		}
		public Look_prefixContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_look_prefix;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).enterLook_prefix(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).exitLook_prefix(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof TerrainVisitor)
				return ((TerrainVisitor<? extends T>) visitor).visitLook_prefix(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Look_prefixContext look_prefix() throws RecognitionException {
		Look_prefixContext _localctx = new Look_prefixContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_look_prefix);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(92);
				match(LOOK_PREFIX);
				setState(93);
				((Look_prefixContext) _localctx).TEXT = match(TEXT);

				((Look_prefixContext) _localctx).lookPrefix = ((Look_prefixContext) _localctx).TEXT.getText();
			        
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Look_in_prepositionContext extends ParserRuleContext {
		public String lookInPreposition;
		public Token TEXT;

		public TerminalNode LOOK_IN_PREP() {
			return getToken(TerrainParser.LOOK_IN_PREP, 0);
		}

		public TerminalNode TEXT() {
			return getToken(TerrainParser.TEXT, 0);
		}
		public Look_in_prepositionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_look_in_preposition;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).enterLook_in_preposition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).exitLook_in_preposition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof TerrainVisitor)
				return ((TerrainVisitor<? extends T>) visitor).visitLook_in_preposition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Look_in_prepositionContext look_in_preposition() throws RecognitionException {
		Look_in_prepositionContext _localctx = new Look_in_prepositionContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_look_in_preposition);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(96);
				match(LOOK_IN_PREP);
				setState(97);
				((Look_in_prepositionContext) _localctx).TEXT = match(TEXT);

				((Look_in_prepositionContext) _localctx).lookInPreposition = ((Look_in_prepositionContext) _localctx).TEXT.getText();
			        
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Resist_flagContext extends ParserRuleContext {
		public MonsterRaceFlag monsterRaceFlag;
		public Token TEXT;

		public TerminalNode RESIST_FLAG() {
			return getToken(TerrainParser.RESIST_FLAG, 0);
		}

		public TerminalNode TEXT() {
			return getToken(TerrainParser.TEXT, 0);
		}
		public Resist_flagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_resist_flag;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).enterResist_flag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).exitResist_flag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof TerrainVisitor)
				return ((TerrainVisitor<? extends T>) visitor).visitResist_flag(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Resist_flagContext resist_flag() throws RecognitionException {
		Resist_flagContext _localctx = new Resist_flagContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_resist_flag);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(100);
				match(RESIST_FLAG);
				setState(101);
				((Resist_flagContext) _localctx).TEXT = match(TEXT);

				((Resist_flagContext) _localctx).monsterRaceFlag = MonsterRaceFlag.valueOf("RF_" + ((Resist_flagContext) _localctx).TEXT.getText());
			        
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DescContext extends ParserRuleContext {
		public String descStr;
		public Token TEXT;

		public List<TerminalNode> DESC() {
			return getTokens(TerrainParser.DESC);
		}
		public TerminalNode DESC(int i) {
			return getToken(TerrainParser.DESC, i);
		}

		public List<TerminalNode> TEXT() {
			return getTokens(TerrainParser.TEXT);
		}
		public TerminalNode TEXT(int i) {
			return getToken(TerrainParser.TEXT, i);
		}
		public DescContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_desc;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).enterDesc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).exitDesc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof TerrainVisitor) return ((TerrainVisitor<? extends T>) visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_desc);

		((DescContext) _localctx).descStr = "";
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(107);
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
						{
							setState(104);
							match(DESC);
							setState(105);
							((DescContext) _localctx).TEXT = match(TEXT);

							((DescContext) _localctx).descStr = _localctx.descStr + ((DescContext) _localctx).TEXT.getText();

						}
					}
					setState(109);
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while (_la == DESC);
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DiggingContext extends ParserRuleContext {
		public int diggingNum;
		public Token NUMBER;

		public TerminalNode DIGGING() {
			return getToken(TerrainParser.DIGGING, 0);
		}

		public TerminalNode NUMBER() {
			return getToken(TerrainParser.NUMBER, 0);
		}
		public DiggingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_digging;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).enterDigging(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).exitDigging(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof TerrainVisitor) return ((TerrainVisitor<? extends T>) visitor).visitDigging(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DiggingContext digging() throws RecognitionException {
		DiggingContext _localctx = new DiggingContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_digging);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(111);
				match(DIGGING);
				setState(112);
				((DiggingContext) _localctx).NUMBER = match(NUMBER);

				((DiggingContext) _localctx).diggingNum = Integer.parseInt(((DiggingContext) _localctx).NUMBER.getText());
			        
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FeatureContext extends ParserRuleContext {
		public TerrainFeature terrain;
		public CodeContext code;
		public NameContext name;
		public GraphicsContext graphics;
		public PriorityContext priority;
		public MimicContext mimic;
		public FlagsContext flags;
		public DiggingContext digging;
		public Walk_msgContext walk_msg;
		public Run_msgContext run_msg;
		public Hurt_msgContext hurt_msg;
		public Die_msgContext die_msg;
		public Confused_msgContext confused_msg;
		public Look_prefixContext look_prefix;
		public Look_in_prepositionContext look_in_preposition;
		public Resist_flagContext resist_flag;
		public DescContext desc;
		public CodeContext code() {
			return getRuleContext(CodeContext.class, 0);
		}
		public NameContext name() {
			return getRuleContext(NameContext.class, 0);
		}
		public GraphicsContext graphics() {
			return getRuleContext(GraphicsContext.class, 0);
		}
		public PriorityContext priority() {
			return getRuleContext(PriorityContext.class, 0);
		}
		public MimicContext mimic() {
			return getRuleContext(MimicContext.class, 0);
		}
		public List<FlagsContext> flags() {
			return getRuleContexts(FlagsContext.class);
		}
		public FlagsContext flags(int i) {
			return getRuleContext(FlagsContext.class, i);
		}
		public DiggingContext digging() {
			return getRuleContext(DiggingContext.class, 0);
		}
		public Walk_msgContext walk_msg() {
			return getRuleContext(Walk_msgContext.class, 0);
		}
		public Run_msgContext run_msg() {
			return getRuleContext(Run_msgContext.class, 0);
		}
		public Hurt_msgContext hurt_msg() {
			return getRuleContext(Hurt_msgContext.class, 0);
		}
		public Die_msgContext die_msg() {
			return getRuleContext(Die_msgContext.class, 0);
		}
		public Confused_msgContext confused_msg() {
			return getRuleContext(Confused_msgContext.class, 0);
		}
		public Look_prefixContext look_prefix() {
			return getRuleContext(Look_prefixContext.class, 0);
		}
		public Look_in_prepositionContext look_in_preposition() {
			return getRuleContext(Look_in_prepositionContext.class, 0);
		}
		public Resist_flagContext resist_flag() {
			return getRuleContext(Resist_flagContext.class, 0);
		}
		public DescContext desc() {
			return getRuleContext(DescContext.class, 0);
		}
		public FeatureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_feature;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).enterFeature(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).exitFeature(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof TerrainVisitor) return ((TerrainVisitor<? extends T>) visitor).visitFeature(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FeatureContext feature() throws RecognitionException {
		FeatureContext _localctx = new FeatureContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_feature);

		TerrainFlags codeInit = TerrainFlags.FEAT_NONE;
		String nameInit = "";
		AngbandDisplayCharacter adcharInit = null;
		TerrainFlags mimicInit = TerrainFlags.FEAT_NONE;
		int priorityInit = 0;
		Flag<TerrainFeatureFlags> flagsInit = null;
		String walkMsgInit = "";
		String runMsgInit = "";
		String hurtMsgInit = "";
		String dieMsgInit = "";
		String confusedMsgInit = "";
		String lookPrefixInit = "";
		String lookInPreposInit = "";
		MonsterRaceFlag resistFlagInit = MonsterRaceFlag.RF_NONE;
		String descInit = "";
		int diggingInit = 0;
		int shopNum = 0;
		        
		int _la;
		try {
			setState(257);
			_errHandler.sync(this);
			switch (getInterpreter().adaptivePredict(_input, 27, _ctx)) {
				case 1:
					enterOuterAlt(_localctx, 1);
				{
					{
						setState(115);
						((FeatureContext) _localctx).code = code();

						codeInit = ((FeatureContext) _localctx).code.codeFlag;

						setState(117);
						((FeatureContext) _localctx).name = name();

						nameInit = ((FeatureContext) _localctx).name.nameStr;

						setState(119);
						((FeatureContext) _localctx).graphics = graphics();

						adcharInit = ((FeatureContext) _localctx).graphics.displayCharacter;

						setState(121);
						((FeatureContext) _localctx).priority = priority();

						priorityInit = ((FeatureContext) _localctx).priority.priorityNum;

						setState(126);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la == MIMIC) {
							{
								setState(123);
								((FeatureContext) _localctx).mimic = mimic();

								mimicInit = ((FeatureContext) _localctx).mimic.mimicFlag;

							}
						}

						setState(133);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la == FLAGS) {
							{
								{
									setState(128);
									((FeatureContext) _localctx).flags = flags();

									flagsInit = new Flag<TerrainFeatureFlags>(TerrainFeatureFlags.class);

									for (TerrainFeatureFlags flag : ((FeatureContext) _localctx).flags.flagList)
										flagsInit.on(flag);

								}
							}
							setState(135);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(139);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la == DIGGING) {
							{
								setState(136);
								((FeatureContext) _localctx).digging = digging();

								diggingInit = ((FeatureContext) _localctx).digging.diggingNum;

							}
						}

						setState(144);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la == WALK_MSG) {
							{
								setState(141);
								((FeatureContext) _localctx).walk_msg = walk_msg();

								walkMsgInit = ((FeatureContext) _localctx).walk_msg.walkMsgStr;

							}
						}

						setState(149);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la == RUN_MSG) {
							{
								setState(146);
								((FeatureContext) _localctx).run_msg = run_msg();

								runMsgInit = ((FeatureContext) _localctx).run_msg.runMsgStr;

							}
						}

						setState(154);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la == HURT_MSG) {
							{
								setState(151);
								((FeatureContext) _localctx).hurt_msg = hurt_msg();

								hurtMsgInit = ((FeatureContext) _localctx).hurt_msg.hurtMsgStr;

							}
						}

						setState(159);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la == DIE_MSG) {
							{
								setState(156);
								((FeatureContext) _localctx).die_msg = die_msg();

								dieMsgInit = ((FeatureContext) _localctx).die_msg.dieMsgStr;

							}
						}

						setState(164);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la == CONFUSED_MSG) {
							{
								setState(161);
								((FeatureContext) _localctx).confused_msg = confused_msg();

								confusedMsgInit = ((FeatureContext) _localctx).confused_msg.confusedMsgStr;

							}
						}

						setState(169);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la == LOOK_PREFIX) {
							{
								setState(166);
								((FeatureContext) _localctx).look_prefix = look_prefix();

								lookPrefixInit = ((FeatureContext) _localctx).look_prefix.lookPrefix;

							}
						}

						setState(174);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la == LOOK_IN_PREP) {
							{
								setState(171);
								((FeatureContext) _localctx).look_in_preposition = look_in_preposition();

								lookInPreposInit = ((FeatureContext) _localctx).look_in_preposition.lookInPreposition;

							}
						}

						setState(179);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la == RESIST_FLAG) {
							{
								setState(176);
								((FeatureContext) _localctx).resist_flag = resist_flag();

								resistFlagInit = ((FeatureContext) _localctx).resist_flag.monsterRaceFlag;

							}
						}

						setState(184);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la == DESC) {
							{
								setState(181);
								((FeatureContext) _localctx).desc = desc();

								descInit = ((FeatureContext) _localctx).desc.descStr;

							}
						}

					}
				}
				break;
				case 2:
					enterOuterAlt(_localctx, 2);
				{
					{
						setState(186);
						((FeatureContext) _localctx).code = code();

						codeInit = ((FeatureContext) _localctx).code.codeFlag;

						setState(188);
						((FeatureContext) _localctx).name = name();

						nameInit = ((FeatureContext) _localctx).name.nameStr;

						setState(190);
						((FeatureContext) _localctx).graphics = graphics();

						adcharInit = ((FeatureContext) _localctx).graphics.displayCharacter;

						setState(192);
						((FeatureContext) _localctx).priority = priority();

						priorityInit = ((FeatureContext) _localctx).priority.priorityNum;

						setState(197);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la == MIMIC) {
							{
								setState(194);
								((FeatureContext) _localctx).mimic = mimic();

								mimicInit = ((FeatureContext) _localctx).mimic.mimicFlag;

							}
						}

						setState(204);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la == FLAGS) {
							{
								{
									setState(199);
									((FeatureContext) _localctx).flags = flags();

									flagsInit = new Flag<TerrainFeatureFlags>(TerrainFeatureFlags.class);

									for (TerrainFeatureFlags flag : ((FeatureContext) _localctx).flags.flagList)
										flagsInit.on(flag);

								}
							}
							setState(206);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(210);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la == LOOK_PREFIX) {
							{
								setState(207);
								((FeatureContext) _localctx).look_prefix = look_prefix();

								lookPrefixInit = ((FeatureContext) _localctx).look_prefix.lookPrefix;

							}
						}

						setState(215);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la == DESC) {
							{
								setState(212);
								((FeatureContext) _localctx).desc = desc();

								descInit = ((FeatureContext) _localctx).desc.descStr;

							}
						}

						setState(220);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la == DIGGING) {
							{
								setState(217);
								((FeatureContext) _localctx).digging = digging();

								diggingInit = ((FeatureContext) _localctx).digging.diggingNum;

							}
						}

						setState(225);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la == WALK_MSG) {
							{
								setState(222);
								((FeatureContext) _localctx).walk_msg = walk_msg();

								walkMsgInit = ((FeatureContext) _localctx).walk_msg.walkMsgStr;

							}
						}

						setState(230);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la == RUN_MSG) {
							{
								setState(227);
								((FeatureContext) _localctx).run_msg = run_msg();

								runMsgInit = ((FeatureContext) _localctx).run_msg.runMsgStr;

							}
						}

						setState(235);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la == HURT_MSG) {
							{
								setState(232);
								((FeatureContext) _localctx).hurt_msg = hurt_msg();

								hurtMsgInit = ((FeatureContext) _localctx).hurt_msg.hurtMsgStr;

							}
						}

						setState(240);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la == DIE_MSG) {
							{
								setState(237);
								((FeatureContext) _localctx).die_msg = die_msg();

								dieMsgInit = ((FeatureContext) _localctx).die_msg.dieMsgStr;

							}
						}

						setState(245);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la == CONFUSED_MSG) {
							{
								setState(242);
								((FeatureContext) _localctx).confused_msg = confused_msg();

								confusedMsgInit = ((FeatureContext) _localctx).confused_msg.confusedMsgStr;

							}
						}

						setState(250);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la == LOOK_IN_PREP) {
							{
								setState(247);
								((FeatureContext) _localctx).look_in_preposition = look_in_preposition();

								lookInPreposInit = ((FeatureContext) _localctx).look_in_preposition.lookInPreposition;

							}
						}

						setState(255);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la == RESIST_FLAG) {
							{
								setState(252);
								((FeatureContext) _localctx).resist_flag = resist_flag();

								resistFlagInit = ((FeatureContext) _localctx).resist_flag.monsterRaceFlag;

							}
						}

					}
				}
				break;
			}
			_ctx.stop = _input.LT(-1);

			((FeatureContext) _localctx).terrain = new TerrainFeature(
					codeInit, nameInit, descInit, mimicInit,
					priorityInit, shopNum, diggingInit,
					flagsInit, adcharInit, walkMsgInit,
					runMsgInit, hurtMsgInit, dieMsgInit,
					confusedMsgInit, lookPrefixInit,
					lookInPreposInit,
					resistFlagInit);

		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FeaturesContext extends ParserRuleContext {
		public ArrayList<TerrainFeature> terrainFeatures;
		public FeatureContext feature;

		public TerminalNode EOF() {
			return getToken(TerrainParser.EOF, 0);
		}
		public List<FeatureContext> feature() {
			return getRuleContexts(FeatureContext.class);
		}
		public FeatureContext feature(int i) {
			return getRuleContext(FeatureContext.class, i);
		}
		public FeaturesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_features;
		}
		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).enterFeatures(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof TerrainListener) ((TerrainListener) listener).exitFeatures(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof TerrainVisitor) return ((TerrainVisitor<? extends T>) visitor).visitFeatures(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FeaturesContext features() throws RecognitionException {
		FeaturesContext _localctx = new FeaturesContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_features);

		((FeaturesContext) _localctx).terrainFeatures = new ArrayList<>();
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(262);
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
						{
							setState(259);
							((FeaturesContext) _localctx).feature = feature();

							_localctx.terrainFeatures.add(((FeaturesContext) _localctx).feature.terrain);

						}
					}
					setState(264);
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while (_la == CODE);
				setState(266);
				match(EOF);
			}
		} catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		} finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
			"\u0004\u0001\u0017\u010d\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001" +
					"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004" +
					"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007" +
					"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b" +
					"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007" +
					"\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0001\u0000\u0001" +
					"\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
					"\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
					"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001" +
					"\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
					"\u0005\u0001\u0005\u0005\u0005@\b\u0005\n\u0005\f\u0005C\t\u0005\u0004" +
					"\u0005E\b\u0005\u000b\u0005\f\u0005F\u0001\u0006\u0001\u0006\u0001\u0006" +
					"\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001" +
					"\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001" +
					"\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001" +
					"\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e" +
					"\u0001\u000e\u0004\u000el\b\u000e\u000b\u000e\f\u000em\u0001\u000f\u0001" +
					"\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001" +
					"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001" +
					"\u0010\u0001\u0010\u0003\u0010\u007f\b\u0010\u0001\u0010\u0001\u0010\u0001" +
					"\u0010\u0005\u0010\u0084\b\u0010\n\u0010\f\u0010\u0087\t\u0010\u0001\u0010" +
					"\u0001\u0010\u0001\u0010\u0003\u0010\u008c\b\u0010\u0001\u0010\u0001\u0010" +
					"\u0001\u0010\u0003\u0010\u0091\b\u0010\u0001\u0010\u0001\u0010\u0001\u0010" +
					"\u0003\u0010\u0096\b\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010" +
					"\u009b\b\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u00a0\b" +
					"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u00a5\b\u0010\u0001" +
					"\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u00aa\b\u0010\u0001\u0010\u0001" +
					"\u0010\u0001\u0010\u0003\u0010\u00af\b\u0010\u0001\u0010\u0001\u0010\u0001" +
					"\u0010\u0003\u0010\u00b4\b\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003" +
					"\u0010\u00b9\b\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001" +
					"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001" +
					"\u0010\u0003\u0010\u00c6\b\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0005" +
					"\u0010\u00cb\b\u0010\n\u0010\f\u0010\u00ce\t\u0010\u0001\u0010\u0001\u0010" +
					"\u0001\u0010\u0003\u0010\u00d3\b\u0010\u0001\u0010\u0001\u0010\u0001\u0010" +
					"\u0003\u0010\u00d8\b\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010" +
					"\u00dd\b\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u00e2\b" +
					"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u00e7\b\u0010\u0001" +
					"\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u00ec\b\u0010\u0001\u0010\u0001" +
					"\u0010\u0001\u0010\u0003\u0010\u00f1\b\u0010\u0001\u0010\u0001\u0010\u0001" +
					"\u0010\u0003\u0010\u00f6\b\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003" +
					"\u0010\u00fb\b\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u0100" +
					"\b\u0010\u0003\u0010\u0102\b\u0010\u0001\u0011\u0001\u0011\u0001\u0011" +
					"\u0004\u0011\u0107\b\u0011\u000b\u0011\f\u0011\u0108\u0001\u0011\u0001" +
					"\u0011\u0001\u0011\u0000\u0000\u0012\u0000\u0002\u0004\u0006\b\n\f\u000e" +
					"\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"\u0000\u0000\u0117" +
					"\u0000$\u0001\u0000\u0000\u0000\u0002(\u0001\u0000\u0000\u0000\u0004," +
					"\u0001\u0000\u0000\u0000\u00061\u0001\u0000\u0000\u0000\b5\u0001\u0000" +
					"\u0000\u0000\nD\u0001\u0000\u0000\u0000\fH\u0001\u0000\u0000\u0000\u000e" +
					"L\u0001\u0000\u0000\u0000\u0010P\u0001\u0000\u0000\u0000\u0012T\u0001" +
					"\u0000\u0000\u0000\u0014X\u0001\u0000\u0000\u0000\u0016\\\u0001\u0000" +
					"\u0000\u0000\u0018`\u0001\u0000\u0000\u0000\u001ad\u0001\u0000\u0000\u0000" +
					"\u001ck\u0001\u0000\u0000\u0000\u001eo\u0001\u0000\u0000\u0000 \u0101" +
					"\u0001\u0000\u0000\u0000\"\u0106\u0001\u0000\u0000\u0000$%\u0005\u0003" +
					"\u0000\u0000%&\u0005\u0017\u0000\u0000&\'\u0006\u0000\uffff\uffff\u0000" +
					"\'\u0001\u0001\u0000\u0000\u0000()\u0005\u0004\u0000\u0000)*\u0005\u0017" +
					"\u0000\u0000*+\u0006\u0001\uffff\uffff\u0000+\u0003\u0001\u0000\u0000" +
					"\u0000,-\u0005\u0005\u0000\u0000-.\u0005\u0015\u0000\u0000./\u0005\u0017" +
					"\u0000\u0000/0\u0006\u0002\uffff\uffff\u00000\u0005\u0001\u0000\u0000" +
					"\u000012\u0005\u0006\u0000\u000023\u0005\u0017\u0000\u000034\u0006\u0003" +
					"\uffff\uffff\u00004\u0007\u0001\u0000\u0000\u000056\u0005\u0007\u0000" +
					"\u000067\u0005\u0016\u0000\u000078\u0006\u0004\uffff\uffff\u00008\t\u0001" +
					"\u0000\u0000\u00009:\u0005\b\u0000\u0000:;\u0005\u0017\u0000\u0000;A\u0006" +
					"\u0005\uffff\uffff\u0000<=\u0005\u0014\u0000\u0000=>\u0005\u0017\u0000" +
					"\u0000>@\u0006\u0005\uffff\uffff\u0000?<\u0001\u0000\u0000\u0000@C\u0001" +
					"\u0000\u0000\u0000A?\u0001\u0000\u0000\u0000AB\u0001\u0000\u0000\u0000" +
					"BE\u0001\u0000\u0000\u0000CA\u0001\u0000\u0000\u0000D9\u0001\u0000\u0000" +
					"\u0000EF\u0001\u0000\u0000\u0000FD\u0001\u0000\u0000\u0000FG\u0001\u0000" +
					"\u0000\u0000G\u000b\u0001\u0000\u0000\u0000HI\u0005\n\u0000\u0000IJ\u0005" +
					"\u0017\u0000\u0000JK\u0006\u0006\uffff\uffff\u0000K\r\u0001\u0000\u0000" +
					"\u0000LM\u0005\u000b\u0000\u0000MN\u0005\u0017\u0000\u0000NO\u0006\u0007" +
					"\uffff\uffff\u0000O\u000f\u0001\u0000\u0000\u0000PQ\u0005\f\u0000\u0000" +
					"QR\u0005\u0017\u0000\u0000RS\u0006\b\uffff\uffff\u0000S\u0011\u0001\u0000" +
					"\u0000\u0000TU\u0005\r\u0000\u0000UV\u0005\u0017\u0000\u0000VW\u0006\t" +
					"\uffff\uffff\u0000W\u0013\u0001\u0000\u0000\u0000XY\u0005\u000e\u0000" +
					"\u0000YZ\u0005\u0017\u0000\u0000Z[\u0006\n\uffff\uffff\u0000[\u0015\u0001" +
					"\u0000\u0000\u0000\\]\u0005\u000f\u0000\u0000]^\u0005\u0017\u0000\u0000" +
					"^_\u0006\u000b\uffff\uffff\u0000_\u0017\u0001\u0000\u0000\u0000`a\u0005" +
					"\u0010\u0000\u0000ab\u0005\u0017\u0000\u0000bc\u0006\f\uffff\uffff\u0000" +
					"c\u0019\u0001\u0000\u0000\u0000de\u0005\u0011\u0000\u0000ef\u0005\u0017" +
					"\u0000\u0000fg\u0006\r\uffff\uffff\u0000g\u001b\u0001\u0000\u0000\u0000" +
					"hi\u0005\u0012\u0000\u0000ij\u0005\u0017\u0000\u0000jl\u0006\u000e\uffff" +
					"\uffff\u0000kh\u0001\u0000\u0000\u0000lm\u0001\u0000\u0000\u0000mk\u0001" +
					"\u0000\u0000\u0000mn\u0001\u0000\u0000\u0000n\u001d\u0001\u0000\u0000" +
					"\u0000op\u0005\t\u0000\u0000pq\u0005\u0016\u0000\u0000qr\u0006\u000f\uffff" +
					"\uffff\u0000r\u001f\u0001\u0000\u0000\u0000st\u0003\u0000\u0000\u0000" +
					"tu\u0006\u0010\uffff\uffff\u0000uv\u0003\u0002\u0001\u0000vw\u0006\u0010" +
					"\uffff\uffff\u0000wx\u0003\u0004\u0002\u0000xy\u0006\u0010\uffff\uffff" +
					"\u0000yz\u0003\b\u0004\u0000z~\u0006\u0010\uffff\uffff\u0000{|\u0003\u0006" +
					"\u0003\u0000|}\u0006\u0010\uffff\uffff\u0000}\u007f\u0001\u0000\u0000" +
					"\u0000~{\u0001\u0000\u0000\u0000~\u007f\u0001\u0000\u0000\u0000\u007f" +
					"\u0085\u0001\u0000\u0000\u0000\u0080\u0081\u0003\n\u0005\u0000\u0081\u0082" +
					"\u0006\u0010\uffff\uffff\u0000\u0082\u0084\u0001\u0000\u0000\u0000\u0083" +
					"\u0080\u0001\u0000\u0000\u0000\u0084\u0087\u0001\u0000\u0000\u0000\u0085" +
					"\u0083\u0001\u0000\u0000\u0000\u0085\u0086\u0001\u0000\u0000\u0000\u0086" +
					"\u008b\u0001\u0000\u0000\u0000\u0087\u0085\u0001\u0000\u0000\u0000\u0088" +
					"\u0089\u0003\u001e\u000f\u0000\u0089\u008a\u0006\u0010\uffff\uffff\u0000" +
					"\u008a\u008c\u0001\u0000\u0000\u0000\u008b\u0088\u0001\u0000\u0000\u0000" +
					"\u008b\u008c\u0001\u0000\u0000\u0000\u008c\u0090\u0001\u0000\u0000\u0000" +
					"\u008d\u008e\u0003\f\u0006\u0000\u008e\u008f\u0006\u0010\uffff\uffff\u0000" +
					"\u008f\u0091\u0001\u0000\u0000\u0000\u0090\u008d\u0001\u0000\u0000\u0000" +
					"\u0090\u0091\u0001\u0000\u0000\u0000\u0091\u0095\u0001\u0000\u0000\u0000" +
					"\u0092\u0093\u0003\u000e\u0007\u0000\u0093\u0094\u0006\u0010\uffff\uffff" +
					"\u0000\u0094\u0096\u0001\u0000\u0000\u0000\u0095\u0092\u0001\u0000\u0000" +
					"\u0000\u0095\u0096\u0001\u0000\u0000\u0000\u0096\u009a\u0001\u0000\u0000" +
					"\u0000\u0097\u0098\u0003\u0010\b\u0000\u0098\u0099\u0006\u0010\uffff\uffff" +
					"\u0000\u0099\u009b\u0001\u0000\u0000\u0000\u009a\u0097\u0001\u0000\u0000" +
					"\u0000\u009a\u009b\u0001\u0000\u0000\u0000\u009b\u009f\u0001\u0000\u0000" +
					"\u0000\u009c\u009d\u0003\u0012\t\u0000\u009d\u009e\u0006\u0010\uffff\uffff" +
					"\u0000\u009e\u00a0\u0001\u0000\u0000\u0000\u009f\u009c\u0001\u0000\u0000" +
					"\u0000\u009f\u00a0\u0001\u0000\u0000\u0000\u00a0\u00a4\u0001\u0000\u0000" +
					"\u0000\u00a1\u00a2\u0003\u0014\n\u0000\u00a2\u00a3\u0006\u0010\uffff\uffff" +
					"\u0000\u00a3\u00a5\u0001\u0000\u0000\u0000\u00a4\u00a1\u0001\u0000\u0000" +
					"\u0000\u00a4\u00a5\u0001\u0000\u0000\u0000\u00a5\u00a9\u0001\u0000\u0000" +
					"\u0000\u00a6\u00a7\u0003\u0016\u000b\u0000\u00a7\u00a8\u0006\u0010\uffff" +
					"\uffff\u0000\u00a8\u00aa\u0001\u0000\u0000\u0000\u00a9\u00a6\u0001\u0000" +
					"\u0000\u0000\u00a9\u00aa\u0001\u0000\u0000\u0000\u00aa\u00ae\u0001\u0000" +
					"\u0000\u0000\u00ab\u00ac\u0003\u0018\f\u0000\u00ac\u00ad\u0006\u0010\uffff" +
					"\uffff\u0000\u00ad\u00af\u0001\u0000\u0000\u0000\u00ae\u00ab\u0001\u0000" +
					"\u0000\u0000\u00ae\u00af\u0001\u0000\u0000\u0000\u00af\u00b3\u0001\u0000" +
					"\u0000\u0000\u00b0\u00b1\u0003\u001a\r\u0000\u00b1\u00b2\u0006\u0010\uffff" +
					"\uffff\u0000\u00b2\u00b4\u0001\u0000\u0000\u0000\u00b3\u00b0\u0001\u0000" +
					"\u0000\u0000\u00b3\u00b4\u0001\u0000\u0000\u0000\u00b4\u00b8\u0001\u0000" +
					"\u0000\u0000\u00b5\u00b6\u0003\u001c\u000e\u0000\u00b6\u00b7\u0006\u0010" +
					"\uffff\uffff\u0000\u00b7\u00b9\u0001\u0000\u0000\u0000\u00b8\u00b5\u0001" +
					"\u0000\u0000\u0000\u00b8\u00b9\u0001\u0000\u0000\u0000\u00b9\u0102\u0001" +
					"\u0000\u0000\u0000\u00ba\u00bb\u0003\u0000\u0000\u0000\u00bb\u00bc\u0006" +
					"\u0010\uffff\uffff\u0000\u00bc\u00bd\u0003\u0002\u0001\u0000\u00bd\u00be" +
					"\u0006\u0010\uffff\uffff\u0000\u00be\u00bf\u0003\u0004\u0002\u0000\u00bf" +
					"\u00c0\u0006\u0010\uffff\uffff\u0000\u00c0\u00c1\u0003\b\u0004\u0000\u00c1" +
					"\u00c5\u0006\u0010\uffff\uffff\u0000\u00c2\u00c3\u0003\u0006\u0003\u0000" +
					"\u00c3\u00c4\u0006\u0010\uffff\uffff\u0000\u00c4\u00c6\u0001\u0000\u0000" +
					"\u0000\u00c5\u00c2\u0001\u0000\u0000\u0000\u00c5\u00c6\u0001\u0000\u0000" +
					"\u0000\u00c6\u00cc\u0001\u0000\u0000\u0000\u00c7\u00c8\u0003\n\u0005\u0000" +
					"\u00c8\u00c9\u0006\u0010\uffff\uffff\u0000\u00c9\u00cb\u0001\u0000\u0000" +
					"\u0000\u00ca\u00c7\u0001\u0000\u0000\u0000\u00cb\u00ce\u0001\u0000\u0000" +
					"\u0000\u00cc\u00ca\u0001\u0000\u0000\u0000\u00cc\u00cd\u0001\u0000\u0000" +
					"\u0000\u00cd\u00d2\u0001\u0000\u0000\u0000\u00ce\u00cc\u0001\u0000\u0000" +
					"\u0000\u00cf\u00d0\u0003\u0016\u000b\u0000\u00d0\u00d1\u0006\u0010\uffff" +
					"\uffff\u0000\u00d1\u00d3\u0001\u0000\u0000\u0000\u00d2\u00cf\u0001\u0000" +
					"\u0000\u0000\u00d2\u00d3\u0001\u0000\u0000\u0000\u00d3\u00d7\u0001\u0000" +
					"\u0000\u0000\u00d4\u00d5\u0003\u001c\u000e\u0000\u00d5\u00d6\u0006\u0010" +
					"\uffff\uffff\u0000\u00d6\u00d8\u0001\u0000\u0000\u0000\u00d7\u00d4\u0001" +
					"\u0000\u0000\u0000\u00d7\u00d8\u0001\u0000\u0000\u0000\u00d8\u00dc\u0001" +
					"\u0000\u0000\u0000\u00d9\u00da\u0003\u001e\u000f\u0000\u00da\u00db\u0006" +
					"\u0010\uffff\uffff\u0000\u00db\u00dd\u0001\u0000\u0000\u0000\u00dc\u00d9" +
					"\u0001\u0000\u0000\u0000\u00dc\u00dd\u0001\u0000\u0000\u0000\u00dd\u00e1" +
					"\u0001\u0000\u0000\u0000\u00de\u00df\u0003\f\u0006\u0000\u00df\u00e0\u0006" +
					"\u0010\uffff\uffff\u0000\u00e0\u00e2\u0001\u0000\u0000\u0000\u00e1\u00de" +
					"\u0001\u0000\u0000\u0000\u00e1\u00e2\u0001\u0000\u0000\u0000\u00e2\u00e6" +
					"\u0001\u0000\u0000\u0000\u00e3\u00e4\u0003\u000e\u0007\u0000\u00e4\u00e5" +
					"\u0006\u0010\uffff\uffff\u0000\u00e5\u00e7\u0001\u0000\u0000\u0000\u00e6" +
					"\u00e3\u0001\u0000\u0000\u0000\u00e6\u00e7\u0001\u0000\u0000\u0000\u00e7" +
					"\u00eb\u0001\u0000\u0000\u0000\u00e8\u00e9\u0003\u0010\b\u0000\u00e9\u00ea" +
					"\u0006\u0010\uffff\uffff\u0000\u00ea\u00ec\u0001\u0000\u0000\u0000\u00eb" +
					"\u00e8\u0001\u0000\u0000\u0000\u00eb\u00ec\u0001\u0000\u0000\u0000\u00ec" +
					"\u00f0\u0001\u0000\u0000\u0000\u00ed\u00ee\u0003\u0012\t\u0000\u00ee\u00ef" +
					"\u0006\u0010\uffff\uffff\u0000\u00ef\u00f1\u0001\u0000\u0000\u0000\u00f0" +
					"\u00ed\u0001\u0000\u0000\u0000\u00f0\u00f1\u0001\u0000\u0000\u0000\u00f1" +
					"\u00f5\u0001\u0000\u0000\u0000\u00f2\u00f3\u0003\u0014\n\u0000\u00f3\u00f4" +
					"\u0006\u0010\uffff\uffff\u0000\u00f4\u00f6\u0001\u0000\u0000\u0000\u00f5" +
					"\u00f2\u0001\u0000\u0000\u0000\u00f5\u00f6\u0001\u0000\u0000\u0000\u00f6" +
					"\u00fa\u0001\u0000\u0000\u0000\u00f7\u00f8\u0003\u0018\f\u0000\u00f8\u00f9" +
					"\u0006\u0010\uffff\uffff\u0000\u00f9\u00fb\u0001\u0000\u0000\u0000\u00fa" +
					"\u00f7\u0001\u0000\u0000\u0000\u00fa\u00fb\u0001\u0000\u0000\u0000\u00fb" +
					"\u00ff\u0001\u0000\u0000\u0000\u00fc\u00fd\u0003\u001a\r\u0000\u00fd\u00fe" +
					"\u0006\u0010\uffff\uffff\u0000\u00fe\u0100\u0001\u0000\u0000\u0000\u00ff" +
					"\u00fc\u0001\u0000\u0000\u0000\u00ff\u0100\u0001\u0000\u0000\u0000\u0100" +
					"\u0102\u0001\u0000\u0000\u0000\u0101s\u0001\u0000\u0000\u0000\u0101\u00ba" +
					"\u0001\u0000\u0000\u0000\u0102!\u0001\u0000\u0000\u0000\u0103\u0104\u0003" +
					" \u0010\u0000\u0104\u0105\u0006\u0011\uffff\uffff\u0000\u0105\u0107\u0001" +
					"\u0000\u0000\u0000\u0106\u0103\u0001\u0000\u0000\u0000\u0107\u0108\u0001" +
					"\u0000\u0000\u0000\u0108\u0106\u0001\u0000\u0000\u0000\u0108\u0109\u0001" +
					"\u0000\u0000\u0000\u0109\u010a\u0001\u0000\u0000\u0000\u010a\u010b\u0005" +
					"\u0000\u0000\u0001\u010b#\u0001\u0000\u0000\u0000\u001dAFm~\u0085\u008b" +
					"\u0090\u0095\u009a\u009f\u00a4\u00a9\u00ae\u00b3\u00b8\u00c5\u00cc\u00d2" +
					"\u00d7\u00dc\u00e1\u00e6\u00eb\u00f0\u00f5\u00fa\u00ff\u0101\u0108";
	public static final ATN _ATN =
			new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}