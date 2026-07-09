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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/EgoItem.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.egoitem;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.Activation;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.*;
import uk.co.jackoftrades.middle.objects.enums.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class EgoItemParser extends Parser {
	static {
		RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
	}

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
			new PredictionContextCache();
	public static final int
			COMMENT = 1, EOL = 2, NAME = 3, INFO = 4, ALLOC = 5, COMBAT = 6, MIN_COMBAT = 7, TYPE = 8,
			ITEM = 9, FLAGS = 10, FLAG_OFF = 11, VALUES = 12, MIN_VALUES = 13, ACT = 14, TIME = 15,
			BRAND = 16, SLAY = 17, CURSE = 18, DESC = 19, COLON = 20, DICE_STRING = 21, TEXT = 22,
			OR = 23;
	public static final int
			RULE_name = 0, RULE_info = 1, RULE_alloc = 2, RULE_diceString = 3, RULE_combat = 4,
			RULE_minCombat = 5, RULE_type = 6, RULE_item = 7, RULE_flags = 8, RULE_flags_off = 9,
			RULE_values = 10, RULE_minValues = 11, RULE_act = 12, RULE_time = 13,
			RULE_brand = 14, RULE_slay = 15, RULE_desc = 16, RULE_egoItem = 17, RULE_file = 18;

	private static String[] makeRuleNames() {
		return new String[]{
				"name", "info", "alloc", "diceString", "combat", "minCombat", "type",
				"item", "flags", "flags_off", "values", "minValues", "act", "time", "brand",
				"slay", "desc", "egoItem", "file"
		};
	}

	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[]{
				null, null, null, "'name:'", "'info:'", "'alloc:'", "'combat:'", "'min-combat:'",
				"'type:'", "'item:'", "'flags:'", "'flags-off:'", "'values:'", "'min-values:'",
				"'act:'", "'time:'", "'brand:'", "'slay:'", "'curse:'", "'desc:'", "':'"
		};
	}

	private static final String[] _LITERAL_NAMES = makeLiteralNames();

	private static String[] makeSymbolicNames() {
		return new String[]{
				null, "COMMENT", "EOL", "NAME", "INFO", "ALLOC", "COMBAT", "MIN_COMBAT",
				"TYPE", "ITEM", "FLAGS", "FLAG_OFF", "VALUES", "MIN_VALUES", "ACT", "TIME",
				"BRAND", "SLAY", "CURSE", "DESC", "COLON", "DICE_STRING", "TEXT", "OR"
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
		return "EgoItem.g4";
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


	public record CurseEntry(Curse curse, CurseData curseData) {
	}

	private static final Logger logger = LogManager.getLogger();

	public EgoItemParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public Token TEXT;

		public TerminalNode NAME() {
			return getToken(EgoItemParser.NAME, 0);
		}

		public TerminalNode TEXT() {
			return getToken(EgoItemParser.TEXT, 0);
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
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).enterName(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).exitName(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof EgoItemVisitor) return ((EgoItemVisitor<? extends T>) visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(38);
				match(NAME);
				setState(39);
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
	public static class InfoContext extends ParserRuleContext {
		public Random costDS;
		public Random ratingDS;
		public DiceStringContext cost;
		public DiceStringContext rating;

		public TerminalNode INFO() {
			return getToken(EgoItemParser.INFO, 0);
		}

		public TerminalNode COLON() {
			return getToken(EgoItemParser.COLON, 0);
		}

		public List<DiceStringContext> diceString() {
			return getRuleContexts(DiceStringContext.class);
		}

		public DiceStringContext diceString(int i) {
			return getRuleContext(DiceStringContext.class, i);
		}

		public InfoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_info;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).enterInfo(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).exitInfo(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof EgoItemVisitor) return ((EgoItemVisitor<? extends T>) visitor).visitInfo(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InfoContext info() throws RecognitionException {
		InfoContext _localctx = new InfoContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_info);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(42);
				match(INFO);
				setState(43);
				((InfoContext) _localctx).cost = diceString();
				setState(44);
				match(COLON);
				setState(45);
				((InfoContext) _localctx).rating = diceString();

				((InfoContext) _localctx).costDS = ((InfoContext) _localctx).cost.diceStr;
				((InfoContext) _localctx).ratingDS = ((InfoContext) _localctx).rating.diceStr;

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
	public static class AllocContext extends ParserRuleContext {
		public Random commonInt;
		public int minLev;
		public int maxLev;
		public DiceStringContext diceString;
		public Token level;

		public TerminalNode ALLOC() {
			return getToken(EgoItemParser.ALLOC, 0);
		}

		public DiceStringContext diceString() {
			return getRuleContext(DiceStringContext.class, 0);
		}

		public TerminalNode COLON() {
			return getToken(EgoItemParser.COLON, 0);
		}

		public TerminalNode TEXT() {
			return getToken(EgoItemParser.TEXT, 0);
		}

		public AllocContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_alloc;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).enterAlloc(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).exitAlloc(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof EgoItemVisitor) return ((EgoItemVisitor<? extends T>) visitor).visitAlloc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AllocContext alloc() throws RecognitionException {
		AllocContext _localctx = new AllocContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_alloc);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(48);
				match(ALLOC);
				setState(49);
				((AllocContext) _localctx).diceString = diceString();
				setState(50);
				match(COLON);
				setState(51);
				((AllocContext) _localctx).level = match(TEXT);

				((AllocContext) _localctx).commonInt = ((AllocContext) _localctx).diceString.diceStr;

				String[] levels = ((AllocContext) _localctx).level.getText().split(" to ");
				if (levels.length != 2)
					throw new InvalidTokenFoundDuringParse("Invalid alloc line. alloc:" + ((AllocContext) _localctx).diceString.diceStr
							+ ":" + ((AllocContext) _localctx).level.getText());

				((AllocContext) _localctx).minLev = Integer.parseInt(levels[0]);
				((AllocContext) _localctx).maxLev = Integer.parseInt(levels[1]);

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
	public static class DiceStringContext extends ParserRuleContext {
		public Random diceStr;
		public Token ds;

		public TerminalNode DICE_STRING() {
			return getToken(EgoItemParser.DICE_STRING, 0);
		}

		public DiceStringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_diceString;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).enterDiceString(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).exitDiceString(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof EgoItemVisitor) return ((EgoItemVisitor<? extends T>) visitor).visitDiceString(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DiceStringContext diceString() throws RecognitionException {
		DiceStringContext _localctx = new DiceStringContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_diceString);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(54);
				((DiceStringContext) _localctx).ds = match(DICE_STRING);
				((DiceStringContext) _localctx).diceStr = Random.parseStr(((DiceStringContext) _localctx).ds.getText());
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
	public static class CombatContext extends ParserRuleContext {
		public Random tohStr;
		public Random todStr;
		public Random toaStr;
		public DiceStringContext toh;
		public DiceStringContext tod;
		public DiceStringContext toa;

		public TerminalNode COMBAT() {
			return getToken(EgoItemParser.COMBAT, 0);
		}

		public List<TerminalNode> COLON() {
			return getTokens(EgoItemParser.COLON);
		}

		public TerminalNode COLON(int i) {
			return getToken(EgoItemParser.COLON, i);
		}

		public List<DiceStringContext> diceString() {
			return getRuleContexts(DiceStringContext.class);
		}

		public DiceStringContext diceString(int i) {
			return getRuleContext(DiceStringContext.class, i);
		}

		public CombatContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_combat;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).enterCombat(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).exitCombat(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof EgoItemVisitor) return ((EgoItemVisitor<? extends T>) visitor).visitCombat(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CombatContext combat() throws RecognitionException {
		CombatContext _localctx = new CombatContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_combat);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(57);
				match(COMBAT);
				setState(58);
				((CombatContext) _localctx).toh = diceString();
				setState(59);
				match(COLON);
				setState(60);
				((CombatContext) _localctx).tod = diceString();
				setState(61);
				match(COLON);
				setState(62);
				((CombatContext) _localctx).toa = diceString();

				((CombatContext) _localctx).tohStr = ((CombatContext) _localctx).toh.diceStr;
				((CombatContext) _localctx).todStr = ((CombatContext) _localctx).tod.diceStr;
				((CombatContext) _localctx).toaStr = ((CombatContext) _localctx).toa.diceStr;

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
	public static class MinCombatContext extends ParserRuleContext {
		public Random tohStr;
		public Random todStr;
		public Random toaStr;
		public DiceStringContext toh;
		public DiceStringContext tod;
		public DiceStringContext toa;

		public TerminalNode MIN_COMBAT() {
			return getToken(EgoItemParser.MIN_COMBAT, 0);
		}

		public List<TerminalNode> COLON() {
			return getTokens(EgoItemParser.COLON);
		}

		public TerminalNode COLON(int i) {
			return getToken(EgoItemParser.COLON, i);
		}

		public List<DiceStringContext> diceString() {
			return getRuleContexts(DiceStringContext.class);
		}

		public DiceStringContext diceString(int i) {
			return getRuleContext(DiceStringContext.class, i);
		}

		public MinCombatContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_minCombat;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).enterMinCombat(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).exitMinCombat(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof EgoItemVisitor) return ((EgoItemVisitor<? extends T>) visitor).visitMinCombat(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MinCombatContext minCombat() throws RecognitionException {
		MinCombatContext _localctx = new MinCombatContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_minCombat);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(65);
				match(MIN_COMBAT);
				setState(66);
				((MinCombatContext) _localctx).toh = diceString();
				setState(67);
				match(COLON);
				setState(68);
				((MinCombatContext) _localctx).tod = diceString();
				setState(69);
				match(COLON);
				setState(70);
				((MinCombatContext) _localctx).toa = diceString();

				((MinCombatContext) _localctx).tohStr = ((MinCombatContext) _localctx).toh.diceStr;
				((MinCombatContext) _localctx).todStr = ((MinCombatContext) _localctx).tod.diceStr;
				((MinCombatContext) _localctx).toaStr = ((MinCombatContext) _localctx).toa.diceStr;

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
	public static class TypeContext extends ParserRuleContext {
		public TValue typeTVal;
		public Token TEXT;

		public TerminalNode TYPE() {
			return getToken(EgoItemParser.TYPE, 0);
		}

		public TerminalNode TEXT() {
			return getToken(EgoItemParser.TEXT, 0);
		}

		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_type;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).enterType(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).exitType(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof EgoItemVisitor) return ((EgoItemVisitor<? extends T>) visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(73);
				match(TYPE);
				setState(74);
				((TypeContext) _localctx).TEXT = match(TEXT);

				String raw = "TV_" + ((TypeContext) _localctx).TEXT.getText().toUpperCase().replace(' ', '_');
				((TypeContext) _localctx).typeTVal = TValue.valueOf(raw);

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
	public static class ItemContext extends ParserRuleContext {
		public TValue itemTVal;
		public ObjectKind oKindObj;
		public Token tval;
		public Token sval;

		public TerminalNode ITEM() {
			return getToken(EgoItemParser.ITEM, 0);
		}

		public TerminalNode COLON() {
			return getToken(EgoItemParser.COLON, 0);
		}

		public List<TerminalNode> TEXT() {
			return getTokens(EgoItemParser.TEXT);
		}

		public TerminalNode TEXT(int i) {
			return getToken(EgoItemParser.TEXT, i);
		}

		public ItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_item;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).enterItem(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).exitItem(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof EgoItemVisitor) return ((EgoItemVisitor<? extends T>) visitor).visitItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ItemContext item() throws RecognitionException {
		ItemContext _localctx = new ItemContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_item);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(77);
				match(ITEM);
				setState(78);
				((ItemContext) _localctx).tval = match(TEXT);
				setState(79);
				match(COLON);
				setState(80);
				((ItemContext) _localctx).sval = match(TEXT);

				String raw = "TV_" + ((ItemContext) _localctx).tval.getText().toUpperCase().replace(' ', '_');
				((ItemContext) _localctx).itemTVal = TValue.valueOf(raw);

				((ItemContext) _localctx).oKindObj = GameConstants.lookupObjectKind(((ItemContext) _localctx).sval.getText());

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
		public List<ObjectFlag> oFlagList;
		public List<ObjectKindFlag> okFlagList;
		public Token flag1;
		public Token flag2;

		public TerminalNode FLAGS() {
			return getToken(EgoItemParser.FLAGS, 0);
		}

		public List<TerminalNode> TEXT() {
			return getTokens(EgoItemParser.TEXT);
		}

		public TerminalNode TEXT(int i) {
			return getToken(EgoItemParser.TEXT, i);
		}

		public List<TerminalNode> OR() {
			return getTokens(EgoItemParser.OR);
		}

		public TerminalNode OR(int i) {
			return getToken(EgoItemParser.OR, i);
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
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).enterFlags(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).exitFlags(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof EgoItemVisitor) return ((EgoItemVisitor<? extends T>) visitor).visitFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsContext flags() throws RecognitionException {
		FlagsContext _localctx = new FlagsContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_flags);

		((FlagsContext) _localctx).oFlagList = new ArrayList<>();
		((FlagsContext) _localctx).okFlagList = new ArrayList<>();

		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
				setState(83);
				match(FLAGS);
				setState(84);
				((FlagsContext) _localctx).flag1 = match(TEXT);

				String raw1 = ((FlagsContext) _localctx).flag1.getText().toUpperCase().trim();
				String oFlagName1 = "OF_" + raw1;
				String kFlagName1 = "KF_" + raw1;
				try {
					ObjectFlag oFlag1 = ObjectFlag.valueOf(oFlagName1);
					_localctx.oFlagList.add(oFlag1);
				} catch (Exception e) {
					ObjectKindFlag kFlag1 = ObjectKindFlag.valueOf(kFlagName1);
					_localctx.okFlagList.add(kFlag1);
				}

				setState(91);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input, 0, _ctx);
				while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
					if (_alt == 1) {
						{
							{
								setState(86);
								match(OR);
								setState(87);
								((FlagsContext) _localctx).flag2 = match(TEXT);

								String raw2 = ((FlagsContext) _localctx).flag2.getText().toUpperCase().trim();
								String oFlagName2 = "OF_" + raw2;
								String kFlagName2 = "KF_" + raw2;
								try {
									ObjectFlag oFlag2 = ObjectFlag.valueOf(oFlagName2);
									_localctx.oFlagList.add(oFlag2);
								} catch (Exception e) {
									ObjectKindFlag kFlag2 = ObjectKindFlag.valueOf(kFlagName2);
									_localctx.okFlagList.add(kFlag2);
								}

							}
						}
					}
					setState(93);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input, 0, _ctx);
				}
				setState(95);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la == OR) {
					{
						setState(94);
						match(OR);
					}
				}

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
	public static class Flags_offContext extends ParserRuleContext {
		public List<ObjectFlag> oFlagList;
		public List<ObjectKindFlag> okFlagList;
		public Token flag1;
		public Token flag2;

		public TerminalNode FLAG_OFF() {
			return getToken(EgoItemParser.FLAG_OFF, 0);
		}

		public List<TerminalNode> TEXT() {
			return getTokens(EgoItemParser.TEXT);
		}

		public TerminalNode TEXT(int i) {
			return getToken(EgoItemParser.TEXT, i);
		}

		public List<TerminalNode> OR() {
			return getTokens(EgoItemParser.OR);
		}

		public TerminalNode OR(int i) {
			return getToken(EgoItemParser.OR, i);
		}

		public Flags_offContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_flags_off;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).enterFlags_off(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).exitFlags_off(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof EgoItemVisitor) return ((EgoItemVisitor<? extends T>) visitor).visitFlags_off(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Flags_offContext flags_off() throws RecognitionException {
		Flags_offContext _localctx = new Flags_offContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_flags_off);

		((Flags_offContext) _localctx).oFlagList = new ArrayList<>();
		((Flags_offContext) _localctx).okFlagList = new ArrayList<>();

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(97);
				match(FLAG_OFF);
				setState(98);
				((Flags_offContext) _localctx).flag1 = match(TEXT);

				String raw1 = ((Flags_offContext) _localctx).flag1.getText().toUpperCase().trim();
				String oFlagName1 = "OF_" + raw1;
				String kFlagName1 = "KF_" + raw1;
				try {
					ObjectFlag oFlag1 = ObjectFlag.valueOf(oFlagName1);
					_localctx.oFlagList.add(oFlag1);
				} catch (Exception e) {
					ObjectKindFlag kFlag1 = ObjectKindFlag.valueOf(kFlagName1);
					_localctx.okFlagList.add(kFlag1);
				}

				setState(105);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la == OR) {
					{
						{
							setState(100);
							match(OR);
							setState(101);
							((Flags_offContext) _localctx).flag2 = match(TEXT);

							String raw2 = ((Flags_offContext) _localctx).flag2.getText().toUpperCase().trim();
							String oFlagName2 = "OF_" + raw2;
							String kFlagName2 = "KF_" + raw2;
							try {
								ObjectFlag oFlag2 = ObjectFlag.valueOf(oFlagName2);
								_localctx.oFlagList.add(oFlag2);
							} catch (Exception e) {
								ObjectKindFlag kFlag2 = ObjectKindFlag.valueOf(kFlagName2);
								_localctx.okFlagList.add(kFlag2);
							}

						}
					}
					setState(107);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
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
	public static class ValuesContext extends ParserRuleContext {
		public Map<ObjectModifier, Random> valueMap;
		public Token val1;
		public Token val2;

		public TerminalNode VALUES() {
			return getToken(EgoItemParser.VALUES, 0);
		}

		public List<TerminalNode> TEXT() {
			return getTokens(EgoItemParser.TEXT);
		}

		public TerminalNode TEXT(int i) {
			return getToken(EgoItemParser.TEXT, i);
		}

		public List<TerminalNode> OR() {
			return getTokens(EgoItemParser.OR);
		}

		public TerminalNode OR(int i) {
			return getToken(EgoItemParser.OR, i);
		}

		public ValuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_values;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).enterValues(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).exitValues(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof EgoItemVisitor) return ((EgoItemVisitor<? extends T>) visitor).visitValues(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValuesContext values() throws RecognitionException {
		ValuesContext _localctx = new ValuesContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_values);

		((ValuesContext) _localctx).valueMap = new HashMap<>();

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(108);
				match(VALUES);
				setState(109);
				((ValuesContext) _localctx).val1 = match(TEXT);

				String raw1 = ((ValuesContext) _localctx).val1.getText();
				String[] parts1 = raw1.split("\\[");
				if (parts1.length != 2)
					throw new InvalidTokenFoundDuringParse("Invalid values tokens. value:" + ((ValuesContext) _localctx).val1.getText());

				String secondRaw1 = parts1[1].replace(']', ' ').trim();
				Random valStr1 = Random.parseStr(secondRaw1);
				ObjectModifier key1 = ObjectModifier.valueOf("OM_" + parts1[0]);
				_localctx.valueMap.put(key1, valStr1);

				setState(116);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la == OR) {
					{
						{
							setState(111);
							match(OR);
							setState(112);
							((ValuesContext) _localctx).val2 = match(TEXT);

							String raw2 = ((ValuesContext) _localctx).val2.getText();
							String[] parts2 = raw2.split("\\[");
							if (parts2.length != 2)
								throw new InvalidTokenFoundDuringParse("Invalid values tokens. value:" + ((ValuesContext) _localctx).val2.getText());

							String secondRaw2 = parts2[1].replace(']', ' ').trim();
							Random valStr2 = Random.parseStr(secondRaw2);

							ObjectModifier key2 = ObjectModifier.valueOf("OM_" + parts2[0]);
							_localctx.valueMap.put(key2, valStr2);

						}
					}
					setState(118);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
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
	public static class MinValuesContext extends ParserRuleContext {
		public Map<ObjectModifier, Random> valueMap;
		public Token val1;
		public Token val2;

		public TerminalNode MIN_VALUES() {
			return getToken(EgoItemParser.MIN_VALUES, 0);
		}

		public List<TerminalNode> TEXT() {
			return getTokens(EgoItemParser.TEXT);
		}

		public TerminalNode TEXT(int i) {
			return getToken(EgoItemParser.TEXT, i);
		}

		public List<TerminalNode> OR() {
			return getTokens(EgoItemParser.OR);
		}

		public TerminalNode OR(int i) {
			return getToken(EgoItemParser.OR, i);
		}

		public MinValuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_minValues;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).enterMinValues(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).exitMinValues(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof EgoItemVisitor) return ((EgoItemVisitor<? extends T>) visitor).visitMinValues(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MinValuesContext minValues() throws RecognitionException {
		MinValuesContext _localctx = new MinValuesContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_minValues);

		((MinValuesContext) _localctx).valueMap = new HashMap<>();

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(119);
				match(MIN_VALUES);
				setState(120);
				((MinValuesContext) _localctx).val1 = match(TEXT);

				String raw1 = ((MinValuesContext) _localctx).val1.getText();
				String[] parts1 = raw1.split("\\[");
				if (parts1.length != 2)
					throw new InvalidTokenFoundDuringParse("Invalid values tokens. value:" + ((MinValuesContext) _localctx).val1.getText());

				ObjectModifier key1 = ObjectModifier.valueOf("OM_" + parts1[0]);
				Random valStr1 = Random.parseStr(parts1[1].replace(']', ' ').trim());
				_localctx.valueMap.put(key1, valStr1);

				setState(127);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la == OR) {
					{
						{
							setState(122);
							match(OR);
							setState(123);
							((MinValuesContext) _localctx).val2 = match(TEXT);

							String raw2 = ((MinValuesContext) _localctx).val2.getText();
							String[] parts2 = raw2.split("\\[");
							if (parts2.length != 2)
								throw new InvalidTokenFoundDuringParse("Invalid values tokens. value:" + ((MinValuesContext) _localctx).val2.getText());

							ObjectModifier key2 = ObjectModifier.valueOf("OM_" + parts2[0]);
							Random valStr2 = Random.parseStr(parts2[1].replace(']', ' ').trim());
							_localctx.valueMap.put(key2, valStr2);

						}
					}
					setState(129);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
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
	public static class ActContext extends ParserRuleContext {
		public Activation activation;
		public Token actName;

		public TerminalNode ACT() {
			return getToken(EgoItemParser.ACT, 0);
		}

		public TerminalNode TEXT() {
			return getToken(EgoItemParser.TEXT, 0);
		}

		public ActContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_act;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).enterAct(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).exitAct(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof EgoItemVisitor) return ((EgoItemVisitor<? extends T>) visitor).visitAct(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActContext act() throws RecognitionException {
		ActContext _localctx = new ActContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_act);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(130);
				match(ACT);
				setState(131);
				((ActContext) _localctx).actName = match(TEXT);

				((ActContext) _localctx).activation = GameConstants.lookupActivation(((ActContext) _localctx).actName.getText());

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
	public static class TimeContext extends ParserRuleContext {
		public Random timeDS;
		public DiceStringContext diceString;

		public TerminalNode TIME() {
			return getToken(EgoItemParser.TIME, 0);
		}

		public DiceStringContext diceString() {
			return getRuleContext(DiceStringContext.class, 0);
		}

		public TimeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_time;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).enterTime(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).exitTime(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof EgoItemVisitor) return ((EgoItemVisitor<? extends T>) visitor).visitTime(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TimeContext time() throws RecognitionException {
		TimeContext _localctx = new TimeContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_time);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(134);
				match(TIME);
				setState(135);
				((TimeContext) _localctx).diceString = diceString();
				((TimeContext) _localctx).timeDS = ((TimeContext) _localctx).diceString.diceStr;
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
	public static class BrandContext extends ParserRuleContext {
		public Brand brandObj;
		public Token TEXT;

		public TerminalNode BRAND() {
			return getToken(EgoItemParser.BRAND, 0);
		}

		public TerminalNode TEXT() {
			return getToken(EgoItemParser.TEXT, 0);
		}

		public BrandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_brand;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).enterBrand(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).exitBrand(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof EgoItemVisitor) return ((EgoItemVisitor<? extends T>) visitor).visitBrand(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BrandContext brand() throws RecognitionException {
		BrandContext _localctx = new BrandContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_brand);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(138);
				match(BRAND);
				setState(139);
				((BrandContext) _localctx).TEXT = match(TEXT);
				((BrandContext) _localctx).brandObj = GameConstants.lookupBrandCode(((BrandContext) _localctx).TEXT.getText());
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
	public static class SlayContext extends ParserRuleContext {
		public Slay slayObj;
		public Token TEXT;

		public TerminalNode SLAY() {
			return getToken(EgoItemParser.SLAY, 0);
		}

		public TerminalNode TEXT() {
			return getToken(EgoItemParser.TEXT, 0);
		}

		public SlayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_slay;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).enterSlay(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).exitSlay(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof EgoItemVisitor) return ((EgoItemVisitor<? extends T>) visitor).visitSlay(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SlayContext slay() throws RecognitionException {
		SlayContext _localctx = new SlayContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_slay);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(142);
				match(SLAY);
				setState(143);
				((SlayContext) _localctx).TEXT = match(TEXT);
				((SlayContext) _localctx).slayObj = GameConstants.lookupSlay(((SlayContext) _localctx).TEXT.getText());
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

		public TerminalNode DESC() {
			return getToken(EgoItemParser.DESC, 0);
		}

		public TerminalNode TEXT() {
			return getToken(EgoItemParser.TEXT, 0);
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
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).enterDesc(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).exitDesc(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof EgoItemVisitor) return ((EgoItemVisitor<? extends T>) visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_desc);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(146);
				match(DESC);
				setState(147);
				((DescContext) _localctx).TEXT = match(TEXT);
				((DescContext) _localctx).descStr = ((DescContext) _localctx).TEXT.getText();
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
	public static class EgoItemContext extends ParserRuleContext {
		public EgoItem egoItemObj;
		public NameContext name;
		public InfoContext info;
		public AllocContext alloc;
		public CombatContext combat;
		public MinCombatContext minCombat;
		public TypeContext type;
		public ItemContext item;
		public FlagsContext flags;
		public Flags_offContext flags_off;
		public ValuesContext values;
		public MinValuesContext minValues;
		public ActContext act;
		public TimeContext time;
		public BrandContext brand;
		public SlayContext slay;
		public DescContext desc;

		public NameContext name() {
			return getRuleContext(NameContext.class, 0);
		}

		public List<InfoContext> info() {
			return getRuleContexts(InfoContext.class);
		}

		public InfoContext info(int i) {
			return getRuleContext(InfoContext.class, i);
		}

		public List<AllocContext> alloc() {
			return getRuleContexts(AllocContext.class);
		}

		public AllocContext alloc(int i) {
			return getRuleContext(AllocContext.class, i);
		}

		public List<CombatContext> combat() {
			return getRuleContexts(CombatContext.class);
		}

		public CombatContext combat(int i) {
			return getRuleContext(CombatContext.class, i);
		}

		public List<MinCombatContext> minCombat() {
			return getRuleContexts(MinCombatContext.class);
		}

		public MinCombatContext minCombat(int i) {
			return getRuleContext(MinCombatContext.class, i);
		}

		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}

		public TypeContext type(int i) {
			return getRuleContext(TypeContext.class, i);
		}

		public List<ItemContext> item() {
			return getRuleContexts(ItemContext.class);
		}

		public ItemContext item(int i) {
			return getRuleContext(ItemContext.class, i);
		}

		public List<FlagsContext> flags() {
			return getRuleContexts(FlagsContext.class);
		}

		public FlagsContext flags(int i) {
			return getRuleContext(FlagsContext.class, i);
		}

		public List<Flags_offContext> flags_off() {
			return getRuleContexts(Flags_offContext.class);
		}

		public Flags_offContext flags_off(int i) {
			return getRuleContext(Flags_offContext.class, i);
		}

		public List<ValuesContext> values() {
			return getRuleContexts(ValuesContext.class);
		}

		public ValuesContext values(int i) {
			return getRuleContext(ValuesContext.class, i);
		}

		public List<MinValuesContext> minValues() {
			return getRuleContexts(MinValuesContext.class);
		}

		public MinValuesContext minValues(int i) {
			return getRuleContext(MinValuesContext.class, i);
		}

		public List<ActContext> act() {
			return getRuleContexts(ActContext.class);
		}

		public ActContext act(int i) {
			return getRuleContext(ActContext.class, i);
		}

		public List<TimeContext> time() {
			return getRuleContexts(TimeContext.class);
		}

		public TimeContext time(int i) {
			return getRuleContext(TimeContext.class, i);
		}

		public List<BrandContext> brand() {
			return getRuleContexts(BrandContext.class);
		}

		public BrandContext brand(int i) {
			return getRuleContext(BrandContext.class, i);
		}

		public List<SlayContext> slay() {
			return getRuleContexts(SlayContext.class);
		}

		public SlayContext slay(int i) {
			return getRuleContext(SlayContext.class, i);
		}

		public List<DescContext> desc() {
			return getRuleContexts(DescContext.class);
		}

		public DescContext desc(int i) {
			return getRuleContext(DescContext.class, i);
		}

		public EgoItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_egoItem;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).enterEgoItem(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).exitEgoItem(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof EgoItemVisitor) return ((EgoItemVisitor<? extends T>) visitor).visitEgoItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EgoItemContext egoItem() throws RecognitionException {
		EgoItemContext _localctx = new EgoItemContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_egoItem);

		String nameInit = "";
		String descInit = "";
		TValue typeInit = TValue.TV_NONE;
		int egoInit = 0;
		Random costInit = null;
		Flag<ObjectFlag> flagsInit = new Flag<>(ObjectFlag.class);
		Flag<ObjectFlag> flagsOffInit = new Flag<>(ObjectFlag.class);
		Flag<ObjectKindFlag> kindFlagsInit = new Flag<>(ObjectKindFlag.class);
		Flag<ObjectKindFlag> kindFlagsOffInit = new Flag<>(ObjectKindFlag.class);
		Map<ObjectModifier, Random> modifiersInit = new HashMap<>();
		Map<ObjectModifier, Random> minModifiersInit = new HashMap<>();
		Map<Brand, Boolean> brandsInit = new HashMap<>();
		Map<Slay, Boolean> slaysInit = new HashMap<>();
		Map<CurseData, Integer> curseInit = new HashMap<>();
		Random ratingInit = null;
		Random allocProbInit = null;
		int minLevelInit = 0;
		int maxLevelInit = 0;
		Map<TValue, ObjectKind> possItemInit = new HashMap<>();
		Random toHInit = null;
		Random toDInit = null;
		Random toAInit = null;
		Random minToHInit = null;
		Random minToDInit = null;
		Random minToAInit = null;
		Activation actInit = null;
		Random timeInit = null;
		boolean everSeenInit = false;

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(150);
				((EgoItemContext) _localctx).name = name();
				nameInit = ((EgoItemContext) _localctx).name.nameStr;
				setState(197);
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
						setState(197);
						_errHandler.sync(this);
						switch (_input.LA(1)) {
							case INFO: {
								setState(152);
								((EgoItemContext) _localctx).info = info();

								costInit = ((EgoItemContext) _localctx).info.costDS;
								ratingInit = ((EgoItemContext) _localctx).info.ratingDS;

							}
							break;
							case ALLOC: {
								setState(155);
								((EgoItemContext) _localctx).alloc = alloc();

								allocProbInit = ((EgoItemContext) _localctx).alloc.commonInt;
								minLevelInit = ((EgoItemContext) _localctx).alloc.minLev;
								maxLevelInit = ((EgoItemContext) _localctx).alloc.maxLev;

							}
							break;
							case COMBAT: {
								setState(158);
								((EgoItemContext) _localctx).combat = combat();

								toHInit = ((EgoItemContext) _localctx).combat.tohStr;
								toDInit = ((EgoItemContext) _localctx).combat.todStr;
								toAInit = ((EgoItemContext) _localctx).combat.toaStr;

							}
							break;
							case MIN_COMBAT: {
								setState(161);
								((EgoItemContext) _localctx).minCombat = minCombat();

								minToHInit = ((EgoItemContext) _localctx).minCombat.tohStr;
								minToDInit = ((EgoItemContext) _localctx).minCombat.todStr;
								minToAInit = ((EgoItemContext) _localctx).minCombat.toaStr;

							}
							break;
							case TYPE: {
								setState(164);
								((EgoItemContext) _localctx).type = type();
								typeInit = ((EgoItemContext) _localctx).type.typeTVal;
							}
							break;
							case ITEM: {
								setState(167);
								((EgoItemContext) _localctx).item = item();
								possItemInit.put(((EgoItemContext) _localctx).item.itemTVal, ((EgoItemContext) _localctx).item.oKindObj);
							}
							break;
							case FLAGS: {
								setState(170);
								((EgoItemContext) _localctx).flags = flags();

								for (ObjectFlag o : ((EgoItemContext) _localctx).flags.oFlagList)
									flagsInit.on(o);

								for (ObjectKindFlag k : ((EgoItemContext) _localctx).flags.okFlagList)
									kindFlagsInit.on(k);

							}
							break;
							case FLAG_OFF: {
								setState(173);
								((EgoItemContext) _localctx).flags_off = flags_off();

								for (ObjectFlag o : ((EgoItemContext) _localctx).flags_off.oFlagList)
									flagsOffInit.on(o);

								for (ObjectKindFlag k : ((EgoItemContext) _localctx).flags_off.okFlagList)
									kindFlagsOffInit.on(k);

							}
							break;
							case VALUES: {
								setState(176);
								((EgoItemContext) _localctx).values = values();
								modifiersInit.putAll(((EgoItemContext) _localctx).values.valueMap);
							}
							break;
							case MIN_VALUES: {
								setState(179);
								((EgoItemContext) _localctx).minValues = minValues();
								minModifiersInit.putAll(((EgoItemContext) _localctx).minValues.valueMap);
							}
							break;
							case ACT: {
								setState(182);
								((EgoItemContext) _localctx).act = act();
								actInit = ((EgoItemContext) _localctx).act.activation;
							}
							break;
							case TIME: {
								setState(185);
								((EgoItemContext) _localctx).time = time();
								timeInit = ((EgoItemContext) _localctx).time.timeDS;
							}
							break;
							case BRAND: {
								setState(188);
								((EgoItemContext) _localctx).brand = brand();
								brandsInit.put(((EgoItemContext) _localctx).brand.brandObj, true);
							}
							break;
							case SLAY: {
								setState(191);
								((EgoItemContext) _localctx).slay = slay();
								slaysInit.put(((EgoItemContext) _localctx).slay.slayObj, true);
							}
							break;
							case DESC: {
								setState(194);
								((EgoItemContext) _localctx).desc = desc();
								descInit = descInit + ((EgoItemContext) _localctx).desc.descStr;
							}
							break;
							default:
								throw new NoViableAltException(this);
						}
					}
					setState(199);
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 786416L) != 0));
			}
			_ctx.stop = _input.LT(-1);

			((EgoItemContext) _localctx).egoItemObj = new EgoItem(nameInit, typeInit, egoInit,
					costInit, flagsInit, flagsOffInit, kindFlagsInit,
					kindFlagsOffInit, modifiersInit, minModifiersInit,
					new HashMap<ElementEnum, ElementInfo>(), brandsInit,
					slaysInit, curseInit, ratingInit, allocProbInit,
					minLevelInit, maxLevelInit, possItemInit, toHInit,
					toDInit, toAInit, minToHInit, minToDInit, minToAInit,
					actInit, timeInit, everSeenInit);

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
	public static class FileContext extends ParserRuleContext {
		public List<EgoItem> egoItems;
		public EgoItemContext egoItem;

		public TerminalNode EOF() {
			return getToken(EgoItemParser.EOF, 0);
		}

		public List<EgoItemContext> egoItem() {
			return getRuleContexts(EgoItemContext.class);
		}

		public EgoItemContext egoItem(int i) {
			return getRuleContext(EgoItemContext.class, i);
		}

		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_file;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).enterFile(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof EgoItemListener) ((EgoItemListener) listener).exitFile(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof EgoItemVisitor) return ((EgoItemVisitor<? extends T>) visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_file);

		((FileContext) _localctx).egoItems = new ArrayList<>();

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(204);
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
						{
							setState(201);
							((FileContext) _localctx).egoItem = egoItem();
							_localctx.egoItems.add(((FileContext) _localctx).egoItem.egoItemObj);
						}
					}
					setState(206);
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while (_la == NAME);
				setState(208);
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
			"\u0004\u0001\u0017\u00d3\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001" +
					"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004" +
					"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007" +
					"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b" +
					"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007" +
					"\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007" +
					"\u0012\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001" +
					"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001" +
					"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001" +
					"\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
					"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001" +
					"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
					"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001" +
					"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001" +
					"\b\u0001\b\u0001\b\u0005\bZ\b\b\n\b\f\b]\t\b\u0001\b\u0003\b`\b\b\u0001" +
					"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0005\th\b\t\n\t\f\tk\t\t\u0001" +
					"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0005\ns\b\n\n\n\f\nv\t\n\u0001" +
					"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0005" +
					"\u000b~\b\u000b\n\u000b\f\u000b\u0081\t\u000b\u0001\f\u0001\f\u0001\f" +
					"\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001" +
					"\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001" +
					"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001" +
					"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001" +
					"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001" +
					"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001" +
					"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001" +
					"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001" +
					"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001" +
					"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001" +
					"\u0011\u0001\u0011\u0001\u0011\u0004\u0011\u00c6\b\u0011\u000b\u0011\f" +
					"\u0011\u00c7\u0001\u0012\u0001\u0012\u0001\u0012\u0004\u0012\u00cd\b\u0012" +
					"\u000b\u0012\f\u0012\u00ce\u0001\u0012\u0001\u0012\u0001\u0012\u0000\u0000" +
					"\u0013\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018" +
					"\u001a\u001c\u001e \"$\u0000\u0000\u00d4\u0000&\u0001\u0000\u0000\u0000" +
					"\u0002*\u0001\u0000\u0000\u0000\u00040\u0001\u0000\u0000\u0000\u00066" +
					"\u0001\u0000\u0000\u0000\b9\u0001\u0000\u0000\u0000\nA\u0001\u0000\u0000" +
					"\u0000\fI\u0001\u0000\u0000\u0000\u000eM\u0001\u0000\u0000\u0000\u0010" +
					"S\u0001\u0000\u0000\u0000\u0012a\u0001\u0000\u0000\u0000\u0014l\u0001" +
					"\u0000\u0000\u0000\u0016w\u0001\u0000\u0000\u0000\u0018\u0082\u0001\u0000" +
					"\u0000\u0000\u001a\u0086\u0001\u0000\u0000\u0000\u001c\u008a\u0001\u0000" +
					"\u0000\u0000\u001e\u008e\u0001\u0000\u0000\u0000 \u0092\u0001\u0000\u0000" +
					"\u0000\"\u0096\u0001\u0000\u0000\u0000$\u00cc\u0001\u0000\u0000\u0000" +
					"&\'\u0005\u0003\u0000\u0000\'(\u0005\u0016\u0000\u0000()\u0006\u0000\uffff" +
					"\uffff\u0000)\u0001\u0001\u0000\u0000\u0000*+\u0005\u0004\u0000\u0000" +
					"+,\u0003\u0006\u0003\u0000,-\u0005\u0014\u0000\u0000-.\u0003\u0006\u0003" +
					"\u0000./\u0006\u0001\uffff\uffff\u0000/\u0003\u0001\u0000\u0000\u0000" +
					"01\u0005\u0005\u0000\u000012\u0003\u0006\u0003\u000023\u0005\u0014\u0000" +
					"\u000034\u0005\u0016\u0000\u000045\u0006\u0002\uffff\uffff\u00005\u0005" +
					"\u0001\u0000\u0000\u000067\u0005\u0015\u0000\u000078\u0006\u0003\uffff" +
					"\uffff\u00008\u0007\u0001\u0000\u0000\u00009:\u0005\u0006\u0000\u0000" +
					":;\u0003\u0006\u0003\u0000;<\u0005\u0014\u0000\u0000<=\u0003\u0006\u0003" +
					"\u0000=>\u0005\u0014\u0000\u0000>?\u0003\u0006\u0003\u0000?@\u0006\u0004" +
					"\uffff\uffff\u0000@\t\u0001\u0000\u0000\u0000AB\u0005\u0007\u0000\u0000" +
					"BC\u0003\u0006\u0003\u0000CD\u0005\u0014\u0000\u0000DE\u0003\u0006\u0003" +
					"\u0000EF\u0005\u0014\u0000\u0000FG\u0003\u0006\u0003\u0000GH\u0006\u0005" +
					"\uffff\uffff\u0000H\u000b\u0001\u0000\u0000\u0000IJ\u0005\b\u0000\u0000" +
					"JK\u0005\u0016\u0000\u0000KL\u0006\u0006\uffff\uffff\u0000L\r\u0001\u0000" +
					"\u0000\u0000MN\u0005\t\u0000\u0000NO\u0005\u0016\u0000\u0000OP\u0005\u0014" +
					"\u0000\u0000PQ\u0005\u0016\u0000\u0000QR\u0006\u0007\uffff\uffff\u0000" +
					"R\u000f\u0001\u0000\u0000\u0000ST\u0005\n\u0000\u0000TU\u0005\u0016\u0000" +
					"\u0000U[\u0006\b\uffff\uffff\u0000VW\u0005\u0017\u0000\u0000WX\u0005\u0016" +
					"\u0000\u0000XZ\u0006\b\uffff\uffff\u0000YV\u0001\u0000\u0000\u0000Z]\u0001" +
					"\u0000\u0000\u0000[Y\u0001\u0000\u0000\u0000[\\\u0001\u0000\u0000\u0000" +
					"\\_\u0001\u0000\u0000\u0000][\u0001\u0000\u0000\u0000^`\u0005\u0017\u0000" +
					"\u0000_^\u0001\u0000\u0000\u0000_`\u0001\u0000\u0000\u0000`\u0011\u0001" +
					"\u0000\u0000\u0000ab\u0005\u000b\u0000\u0000bc\u0005\u0016\u0000\u0000" +
					"ci\u0006\t\uffff\uffff\u0000de\u0005\u0017\u0000\u0000ef\u0005\u0016\u0000" +
					"\u0000fh\u0006\t\uffff\uffff\u0000gd\u0001\u0000\u0000\u0000hk\u0001\u0000" +
					"\u0000\u0000ig\u0001\u0000\u0000\u0000ij\u0001\u0000\u0000\u0000j\u0013" +
					"\u0001\u0000\u0000\u0000ki\u0001\u0000\u0000\u0000lm\u0005\f\u0000\u0000" +
					"mn\u0005\u0016\u0000\u0000nt\u0006\n\uffff\uffff\u0000op\u0005\u0017\u0000" +
					"\u0000pq\u0005\u0016\u0000\u0000qs\u0006\n\uffff\uffff\u0000ro\u0001\u0000" +
					"\u0000\u0000sv\u0001\u0000\u0000\u0000tr\u0001\u0000\u0000\u0000tu\u0001" +
					"\u0000\u0000\u0000u\u0015\u0001\u0000\u0000\u0000vt\u0001\u0000\u0000" +
					"\u0000wx\u0005\r\u0000\u0000xy\u0005\u0016\u0000\u0000y\u007f\u0006\u000b" +
					"\uffff\uffff\u0000z{\u0005\u0017\u0000\u0000{|\u0005\u0016\u0000\u0000" +
					"|~\u0006\u000b\uffff\uffff\u0000}z\u0001\u0000\u0000\u0000~\u0081\u0001" +
					"\u0000\u0000\u0000\u007f}\u0001\u0000\u0000\u0000\u007f\u0080\u0001\u0000" +
					"\u0000\u0000\u0080\u0017\u0001\u0000\u0000\u0000\u0081\u007f\u0001\u0000" +
					"\u0000\u0000\u0082\u0083\u0005\u000e\u0000\u0000\u0083\u0084\u0005\u0016" +
					"\u0000\u0000\u0084\u0085\u0006\f\uffff\uffff\u0000\u0085\u0019\u0001\u0000" +
					"\u0000\u0000\u0086\u0087\u0005\u000f\u0000\u0000\u0087\u0088\u0003\u0006" +
					"\u0003\u0000\u0088\u0089\u0006\r\uffff\uffff\u0000\u0089\u001b\u0001\u0000" +
					"\u0000\u0000\u008a\u008b\u0005\u0010\u0000\u0000\u008b\u008c\u0005\u0016" +
					"\u0000\u0000\u008c\u008d\u0006\u000e\uffff\uffff\u0000\u008d\u001d\u0001" +
					"\u0000\u0000\u0000\u008e\u008f\u0005\u0011\u0000\u0000\u008f\u0090\u0005" +
					"\u0016\u0000\u0000\u0090\u0091\u0006\u000f\uffff\uffff\u0000\u0091\u001f" +
					"\u0001\u0000\u0000\u0000\u0092\u0093\u0005\u0013\u0000\u0000\u0093\u0094" +
					"\u0005\u0016\u0000\u0000\u0094\u0095\u0006\u0010\uffff\uffff\u0000\u0095" +
					"!\u0001\u0000\u0000\u0000\u0096\u0097\u0003\u0000\u0000\u0000\u0097\u00c5" +
					"\u0006\u0011\uffff\uffff\u0000\u0098\u0099\u0003\u0002\u0001\u0000\u0099" +
					"\u009a\u0006\u0011\uffff\uffff\u0000\u009a\u00c6\u0001\u0000\u0000\u0000" +
					"\u009b\u009c\u0003\u0004\u0002\u0000\u009c\u009d\u0006\u0011\uffff\uffff" +
					"\u0000\u009d\u00c6\u0001\u0000\u0000\u0000\u009e\u009f\u0003\b\u0004\u0000" +
					"\u009f\u00a0\u0006\u0011\uffff\uffff\u0000\u00a0\u00c6\u0001\u0000\u0000" +
					"\u0000\u00a1\u00a2\u0003\n\u0005\u0000\u00a2\u00a3\u0006\u0011\uffff\uffff" +
					"\u0000\u00a3\u00c6\u0001\u0000\u0000\u0000\u00a4\u00a5\u0003\f\u0006\u0000" +
					"\u00a5\u00a6\u0006\u0011\uffff\uffff\u0000\u00a6\u00c6\u0001\u0000\u0000" +
					"\u0000\u00a7\u00a8\u0003\u000e\u0007\u0000\u00a8\u00a9\u0006\u0011\uffff" +
					"\uffff\u0000\u00a9\u00c6\u0001\u0000\u0000\u0000\u00aa\u00ab\u0003\u0010" +
					"\b\u0000\u00ab\u00ac\u0006\u0011\uffff\uffff\u0000\u00ac\u00c6\u0001\u0000" +
					"\u0000\u0000\u00ad\u00ae\u0003\u0012\t\u0000\u00ae\u00af\u0006\u0011\uffff" +
					"\uffff\u0000\u00af\u00c6\u0001\u0000\u0000\u0000\u00b0\u00b1\u0003\u0014" +
					"\n\u0000\u00b1\u00b2\u0006\u0011\uffff\uffff\u0000\u00b2\u00c6\u0001\u0000" +
					"\u0000\u0000\u00b3\u00b4\u0003\u0016\u000b\u0000\u00b4\u00b5\u0006\u0011" +
					"\uffff\uffff\u0000\u00b5\u00c6\u0001\u0000\u0000\u0000\u00b6\u00b7\u0003" +
					"\u0018\f\u0000\u00b7\u00b8\u0006\u0011\uffff\uffff\u0000\u00b8\u00c6\u0001" +
					"\u0000\u0000\u0000\u00b9\u00ba\u0003\u001a\r\u0000\u00ba\u00bb\u0006\u0011" +
					"\uffff\uffff\u0000\u00bb\u00c6\u0001\u0000\u0000\u0000\u00bc\u00bd\u0003" +
					"\u001c\u000e\u0000\u00bd\u00be\u0006\u0011\uffff\uffff\u0000\u00be\u00c6" +
					"\u0001\u0000\u0000\u0000\u00bf\u00c0\u0003\u001e\u000f\u0000\u00c0\u00c1" +
					"\u0006\u0011\uffff\uffff\u0000\u00c1\u00c6\u0001\u0000\u0000\u0000\u00c2" +
					"\u00c3\u0003 \u0010\u0000\u00c3\u00c4\u0006\u0011\uffff\uffff\u0000\u00c4" +
					"\u00c6\u0001\u0000\u0000\u0000\u00c5\u0098\u0001\u0000\u0000\u0000\u00c5" +
					"\u009b\u0001\u0000\u0000\u0000\u00c5\u009e\u0001\u0000\u0000\u0000\u00c5" +
					"\u00a1\u0001\u0000\u0000\u0000\u00c5\u00a4\u0001\u0000\u0000\u0000\u00c5" +
					"\u00a7\u0001\u0000\u0000\u0000\u00c5\u00aa\u0001\u0000\u0000\u0000\u00c5" +
					"\u00ad\u0001\u0000\u0000\u0000\u00c5\u00b0\u0001\u0000\u0000\u0000\u00c5" +
					"\u00b3\u0001\u0000\u0000\u0000\u00c5\u00b6\u0001\u0000\u0000\u0000\u00c5" +
					"\u00b9\u0001\u0000\u0000\u0000\u00c5\u00bc\u0001\u0000\u0000\u0000\u00c5" +
					"\u00bf\u0001\u0000\u0000\u0000\u00c5\u00c2\u0001\u0000\u0000\u0000\u00c6" +
					"\u00c7\u0001\u0000\u0000\u0000\u00c7\u00c5\u0001\u0000\u0000\u0000\u00c7" +
					"\u00c8\u0001\u0000\u0000\u0000\u00c8#\u0001\u0000\u0000\u0000\u00c9\u00ca" +
					"\u0003\"\u0011\u0000\u00ca\u00cb\u0006\u0012\uffff\uffff\u0000\u00cb\u00cd" +
					"\u0001\u0000\u0000\u0000\u00cc\u00c9\u0001\u0000\u0000\u0000\u00cd\u00ce" +
					"\u0001\u0000\u0000\u0000\u00ce\u00cc\u0001\u0000\u0000\u0000\u00ce\u00cf" +
					"\u0001\u0000\u0000\u0000\u00cf\u00d0\u0001\u0000\u0000\u0000\u00d0\u00d1" +
					"\u0005\u0000\u0000\u0001\u00d1%\u0001\u0000\u0000\u0000\b[_it\u007f\u00c5" +
					"\u00c7\u00ce";
	public static final ATN _ATN =
			new ATNDeserializer().deserialize(_serializedATN.toCharArray());

	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}