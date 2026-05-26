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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/CurseReader.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.curse;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.middle.enums.EffectBaseType;
import uk.co.jackoftrades.middle.enums.EffectEnum;
import uk.co.jackoftrades.middle.enums.ValueEnum;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.objects.Curse;
import uk.co.jackoftrades.middle.objects.ObjectBase;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.TValue;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class CurseReaderParser extends Parser {
	static {
		RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
	}

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
			new PredictionContextCache();
	public static final int
			COMMENT = 1, EOL = 2, NAME = 3, TYPE = 4, WEIGHT = 5, COMBAT = 6, EFFECT = 7, DICE = 8,
			EXPR = 9, TIME = 10, FLAGS = 11, VALUES = 12, MSG = 13, DESC = 14, CONFLICT = 15, CONFLICT_FLAGS = 16,
			VALUE = 17, TEXT = 18, COLON = 19, OR = 20;
	public static final int
			RULE_name = 0, RULE_type = 1, RULE_combat = 2, RULE_effect = 3, RULE_dice = 4,
			RULE_expr = 5, RULE_time = 6, RULE_flags = 7, RULE_values = 8, RULE_msg = 9,
			RULE_desc = 10, RULE_conflict = 11, RULE_conflict_flags = 12, RULE_curse = 13,
			RULE_curses = 14;

	private static String[] makeRuleNames() {
		return new String[]{
				"name", "type", "combat", "effect", "dice", "expr", "time", "flags",
				"values", "msg", "desc", "conflict", "conflict_flags", "curse", "curses"
		};
	}

	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[]{
				null, null, null, "'name:'", "'type:'", "'weight'", "'combat:'", "'effect:'",
				"'dice:'", "'expr:'", "'time:'", "'flags:'", "'values:'", "'msg:'", "'desc:'",
				"'conflict:'", "'conflict-flags:'", null, null, "':'"
		};
	}

	private static final String[] _LITERAL_NAMES = makeLiteralNames();

	private static String[] makeSymbolicNames() {
		return new String[]{
				null, "COMMENT", "EOL", "NAME", "TYPE", "WEIGHT", "COMBAT", "EFFECT",
				"DICE", "EXPR", "TIME", "FLAGS", "VALUES", "MSG", "DESC", "CONFLICT",
				"CONFLICT_FLAGS", "VALUE", "TEXT", "COLON", "OR"
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
		return "CurseReader.g4";
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

	public CurseReaderParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public Token TEXT;

		public TerminalNode NAME() {
			return getToken(CurseReaderParser.NAME, 0);
		}

		public TerminalNode TEXT() {
			return getToken(CurseReaderParser.TEXT, 0);
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
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).enterName(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).exitName(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseReaderVisitor)
				return ((CurseReaderVisitor<? extends T>) visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(30);
				match(NAME);
				setState(31);
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
	public static class TypeContext extends ParserRuleContext {
		public ObjectBase typeBase;
		public Token TEXT;

		public TerminalNode TYPE() {
			return getToken(CurseReaderParser.TYPE, 0);
		}

		public TerminalNode TEXT() {
			return getToken(CurseReaderParser.TEXT, 0);
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
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).enterType(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).exitType(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseReaderVisitor)
				return ((CurseReaderVisitor<? extends T>) visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(34);
				match(TYPE);
				setState(35);
				((TypeContext) _localctx).TEXT = match(TEXT);

				String tValName = ((TypeContext) _localctx).TEXT.getText();
				TValue tVal = TValue.fromName(tValName);
				((TypeContext) _localctx).typeBase = GameConstants.getBaseFromTVal(tVal);

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
		public int toHitVal;
		public int toDamVal;
		public int acVal;
		public Token toHit;
		public Token toDam;
		public Token ac;

		public TerminalNode COMBAT() {
			return getToken(CurseReaderParser.COMBAT, 0);
		}

		public List<TerminalNode> COLON() {
			return getTokens(CurseReaderParser.COLON);
		}

		public TerminalNode COLON(int i) {
			return getToken(CurseReaderParser.COLON, i);
		}

		public List<TerminalNode> TEXT() {
			return getTokens(CurseReaderParser.TEXT);
		}

		public TerminalNode TEXT(int i) {
			return getToken(CurseReaderParser.TEXT, i);
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
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).enterCombat(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).exitCombat(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseReaderVisitor)
				return ((CurseReaderVisitor<? extends T>) visitor).visitCombat(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CombatContext combat() throws RecognitionException {
		CombatContext _localctx = new CombatContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_combat);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(38);
				match(COMBAT);
				setState(39);
				((CombatContext) _localctx).toHit = match(TEXT);
				setState(40);
				match(COLON);
				setState(41);
				((CombatContext) _localctx).toDam = match(TEXT);
				setState(42);
				match(COLON);
				setState(43);
				((CombatContext) _localctx).ac = match(TEXT);

				((CombatContext) _localctx).toHitVal = Integer.parseInt(((CombatContext) _localctx).toHit.getText());
				((CombatContext) _localctx).toDamVal = Integer.parseInt(((CombatContext) _localctx).toDam.getText());
				((CombatContext) _localctx).acVal = Integer.parseInt(((CombatContext) _localctx).ac.getText());

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
	public static class EffectContext extends ParserRuleContext {
		public EffectEnum effectEnum;
		public TimedEffect timedEnum;
		public MonsterRaceFlag summon;
		public Token effEnum;
		public Token tmdEnum;

		public TerminalNode EFFECT() {
			return getToken(CurseReaderParser.EFFECT, 0);
		}

		public List<TerminalNode> TEXT() {
			return getTokens(CurseReaderParser.TEXT);
		}

		public TerminalNode TEXT(int i) {
			return getToken(CurseReaderParser.TEXT, i);
		}

		public TerminalNode COLON() {
			return getToken(CurseReaderParser.COLON, 0);
		}

		public EffectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_effect;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).enterEffect(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).exitEffect(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseReaderVisitor)
				return ((CurseReaderVisitor<? extends T>) visitor).visitEffect(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectContext effect() throws RecognitionException {
		EffectContext _localctx = new EffectContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_effect);

		((EffectContext) _localctx).timedEnum = TimedEffect.TMD_NULL;
		((EffectContext) _localctx).effectEnum = EffectEnum.EF_NONE;
		((EffectContext) _localctx).summon = MonsterRaceFlag.RF_NONE;
		String effectType = "Timed";

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(46);
				match(EFFECT);
				setState(47);
				((EffectContext) _localctx).effEnum = match(TEXT);

				String effectEnumStr = "EF_" + ((EffectContext) _localctx).effEnum.getText().trim();
				((EffectContext) _localctx).effectEnum = EffectEnum.valueOf(effectEnumStr);
				if (effectEnumStr.equals("EF_SUMMON"))
					effectType = "Summon";

				setState(52);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la == COLON) {
					{
						setState(49);
						match(COLON);
						setState(50);
						((EffectContext) _localctx).tmdEnum = match(TEXT);

						if (((EffectContext) _localctx).tmdEnum != null) {
							if (effectType.equals("Timed"))
								((EffectContext) _localctx).timedEnum = TimedEffect.valueOf("TMD_" + ((EffectContext) _localctx).tmdEnum.getText().trim());
							else
								((EffectContext) _localctx).summon = MonsterRaceFlag.valueOf("RF_" + ((EffectContext) _localctx).tmdEnum.getText().trim());
						}

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
	public static class DiceContext extends ParserRuleContext {
		public String diceString;
		public Token TEXT;

		public TerminalNode DICE() {
			return getToken(CurseReaderParser.DICE, 0);
		}

		public TerminalNode TEXT() {
			return getToken(CurseReaderParser.TEXT, 0);
		}

		public DiceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_dice;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).enterDice(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).exitDice(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseReaderVisitor)
				return ((CurseReaderVisitor<? extends T>) visitor).visitDice(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DiceContext dice() throws RecognitionException {
		DiceContext _localctx = new DiceContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_dice);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(54);
				match(DICE);
				setState(55);
				((DiceContext) _localctx).TEXT = match(TEXT);

				((DiceContext) _localctx).diceString = ((DiceContext) _localctx).TEXT.getText();

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
	public static class ExprContext extends ParserRuleContext {
		public char letter;
		public EffectBaseType effectBaseType;
		public String operation;
		public Token codeLetter;
		public Token efbType;
		public Token operations;

		public TerminalNode EXPR() {
			return getToken(CurseReaderParser.EXPR, 0);
		}

		public List<TerminalNode> COLON() {
			return getTokens(CurseReaderParser.COLON);
		}

		public TerminalNode COLON(int i) {
			return getToken(CurseReaderParser.COLON, i);
		}

		public List<TerminalNode> TEXT() {
			return getTokens(CurseReaderParser.TEXT);
		}

		public TerminalNode TEXT(int i) {
			return getToken(CurseReaderParser.TEXT, i);
		}

		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_expr;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).enterExpr(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).exitExpr(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseReaderVisitor)
				return ((CurseReaderVisitor<? extends T>) visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(58);
				match(EXPR);
				setState(59);
				((ExprContext) _localctx).codeLetter = match(TEXT);
				setState(60);
				match(COLON);
				setState(61);
				((ExprContext) _localctx).efbType = match(TEXT);
				setState(62);
				match(COLON);
				setState(63);
				((ExprContext) _localctx).operations = match(TEXT);

				((ExprContext) _localctx).letter = ((ExprContext) _localctx).codeLetter.getText().charAt(0);

				String efbString = "EFB_" + ((ExprContext) _localctx).efbType.getText().trim();
				((ExprContext) _localctx).effectBaseType = EffectBaseType.valueOf(efbString);

				((ExprContext) _localctx).operation = ((ExprContext) _localctx).operations.getText();

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
		public String timeString;
		public Token TEXT;

		public TerminalNode TIME() {
			return getToken(CurseReaderParser.TIME, 0);
		}

		public TerminalNode TEXT() {
			return getToken(CurseReaderParser.TEXT, 0);
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
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).enterTime(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).exitTime(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseReaderVisitor)
				return ((CurseReaderVisitor<? extends T>) visitor).visitTime(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TimeContext time() throws RecognitionException {
		TimeContext _localctx = new TimeContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_time);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(66);
				match(TIME);
				setState(67);
				((TimeContext) _localctx).TEXT = match(TEXT);

				((TimeContext) _localctx).timeString = ((TimeContext) _localctx).TEXT.getText();

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
		public ArrayList<ObjectFlag> flagArray;
		public Token flag1;
		public Token flag2;

		public TerminalNode FLAGS() {
			return getToken(CurseReaderParser.FLAGS, 0);
		}

		public List<TerminalNode> TEXT() {
			return getTokens(CurseReaderParser.TEXT);
		}

		public TerminalNode TEXT(int i) {
			return getToken(CurseReaderParser.TEXT, i);
		}

		public List<TerminalNode> OR() {
			return getTokens(CurseReaderParser.OR);
		}

		public TerminalNode OR(int i) {
			return getToken(CurseReaderParser.OR, i);
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
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).enterFlags(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).exitFlags(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseReaderVisitor)
				return ((CurseReaderVisitor<? extends T>) visitor).visitFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsContext flags() throws RecognitionException {
		FlagsContext _localctx = new FlagsContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_flags);

		((FlagsContext) _localctx).flagArray = new ArrayList<>();

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(70);
				match(FLAGS);
				setState(71);
				((FlagsContext) _localctx).flag1 = match(TEXT);

				String flag1String = "OF_" + ((FlagsContext) _localctx).flag1.getText().trim();
				ObjectFlag objectFlag1 = ObjectFlag.valueOf(flag1String);
				_localctx.flagArray.add(objectFlag1);

				setState(78);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la == OR) {
					{
						{
							setState(73);
							match(OR);
							setState(74);
							((FlagsContext) _localctx).flag2 = match(TEXT);

							String flag2String = "OF_" + ((FlagsContext) _localctx).flag2.getText().trim();
							ObjectFlag objectFlag2 = ObjectFlag.valueOf(flag2String);
							_localctx.flagArray.add(objectFlag2);

						}
					}
					setState(80);
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
		public HashMap<ValueEnum, Integer> valueCollection;
		public Token tag1;
		public Token val1;
		public Token tag2;
		public Token val2;

		public TerminalNode VALUES() {
			return getToken(CurseReaderParser.VALUES, 0);
		}

		public List<TerminalNode> TEXT() {
			return getTokens(CurseReaderParser.TEXT);
		}

		public TerminalNode TEXT(int i) {
			return getToken(CurseReaderParser.TEXT, i);
		}

		public List<TerminalNode> VALUE() {
			return getTokens(CurseReaderParser.VALUE);
		}

		public TerminalNode VALUE(int i) {
			return getToken(CurseReaderParser.VALUE, i);
		}

		public List<TerminalNode> OR() {
			return getTokens(CurseReaderParser.OR);
		}

		public TerminalNode OR(int i) {
			return getToken(CurseReaderParser.OR, i);
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
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).enterValues(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).exitValues(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseReaderVisitor)
				return ((CurseReaderVisitor<? extends T>) visitor).visitValues(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValuesContext values() throws RecognitionException {
		ValuesContext _localctx = new ValuesContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_values);

		((ValuesContext) _localctx).valueCollection = new HashMap<>();

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(81);
				match(VALUES);
				setState(82);
				((ValuesContext) _localctx).tag1 = match(TEXT);
				setState(83);
				((ValuesContext) _localctx).val1 = match(VALUE);

				String cv1String = ((ValuesContext) _localctx).tag1.getText();
				String val1String = ((ValuesContext) _localctx).val1.getText();
				ValueEnum cv1 = ValueEnum.valueOf("CV_" + cv1String.trim());
				int int1 = Integer.parseInt(val1String.substring(1, val1String.length() - 1));
				_localctx.valueCollection.put(cv1, int1);

				setState(91);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la == OR) {
					{
						{
							setState(85);
							match(OR);
							setState(86);
							((ValuesContext) _localctx).tag2 = match(TEXT);
							setState(87);
							((ValuesContext) _localctx).val2 = match(VALUE);

							String cv2String = ((ValuesContext) _localctx).tag2.getText();
							String val2String = ((ValuesContext) _localctx).val2.getText();
							ValueEnum cv2 = ValueEnum.valueOf("CV_" + cv2String.trim());
							int int2 = Integer.parseInt(val2String.substring(1, val2String.length() - 1));
							_localctx.valueCollection.put(cv2, int2);

						}
					}
					setState(93);
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
	public static class MsgContext extends ParserRuleContext {
		public String message;
		public Token TEXT;

		public TerminalNode MSG() {
			return getToken(CurseReaderParser.MSG, 0);
		}

		public TerminalNode TEXT() {
			return getToken(CurseReaderParser.TEXT, 0);
		}

		public MsgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_msg;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).enterMsg(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).exitMsg(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseReaderVisitor)
				return ((CurseReaderVisitor<? extends T>) visitor).visitMsg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MsgContext msg() throws RecognitionException {
		MsgContext _localctx = new MsgContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_msg);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(94);
				match(MSG);
				setState(95);
				((MsgContext) _localctx).TEXT = match(TEXT);

				((MsgContext) _localctx).message = ((MsgContext) _localctx).TEXT.getText();

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
		public String description;
		public Token TEXT;

		public TerminalNode DESC() {
			return getToken(CurseReaderParser.DESC, 0);
		}

		public TerminalNode TEXT() {
			return getToken(CurseReaderParser.TEXT, 0);
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
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).enterDesc(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).exitDesc(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseReaderVisitor)
				return ((CurseReaderVisitor<? extends T>) visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_desc);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(98);
				match(DESC);
				setState(99);
				((DescContext) _localctx).TEXT = match(TEXT);

				((DescContext) _localctx).description = ((DescContext) _localctx).TEXT.getText();

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
	public static class ConflictContext extends ParserRuleContext {
		public String conflictString;
		public Token TEXT;

		public TerminalNode CONFLICT() {
			return getToken(CurseReaderParser.CONFLICT, 0);
		}

		public TerminalNode TEXT() {
			return getToken(CurseReaderParser.TEXT, 0);
		}

		public ConflictContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_conflict;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).enterConflict(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).exitConflict(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseReaderVisitor)
				return ((CurseReaderVisitor<? extends T>) visitor).visitConflict(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConflictContext conflict() throws RecognitionException {
		ConflictContext _localctx = new ConflictContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_conflict);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(102);
				match(CONFLICT);
				setState(103);
				((ConflictContext) _localctx).TEXT = match(TEXT);

				((ConflictContext) _localctx).conflictString = ((ConflictContext) _localctx).TEXT.getText();

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
	public static class Conflict_flagsContext extends ParserRuleContext {
		public ArrayList<ObjectFlag> conflictFlags;
		public Token flag1;
		public Token flag2;

		public TerminalNode CONFLICT_FLAGS() {
			return getToken(CurseReaderParser.CONFLICT_FLAGS, 0);
		}

		public List<TerminalNode> TEXT() {
			return getTokens(CurseReaderParser.TEXT);
		}

		public TerminalNode TEXT(int i) {
			return getToken(CurseReaderParser.TEXT, i);
		}

		public List<TerminalNode> OR() {
			return getTokens(CurseReaderParser.OR);
		}

		public TerminalNode OR(int i) {
			return getToken(CurseReaderParser.OR, i);
		}

		public Conflict_flagsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_conflict_flags;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).enterConflict_flags(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).exitConflict_flags(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseReaderVisitor)
				return ((CurseReaderVisitor<? extends T>) visitor).visitConflict_flags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Conflict_flagsContext conflict_flags() throws RecognitionException {
		Conflict_flagsContext _localctx = new Conflict_flagsContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_conflict_flags);

		((Conflict_flagsContext) _localctx).conflictFlags = new ArrayList<>();

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(106);
				match(CONFLICT_FLAGS);
				setState(107);
				((Conflict_flagsContext) _localctx).flag1 = match(TEXT);

				String flag1Name = "OF_" + ((Conflict_flagsContext) _localctx).flag1.getText().trim();
				ObjectFlag oFlag1 = ObjectFlag.valueOf(flag1Name);
				_localctx.conflictFlags.add(oFlag1);

				setState(114);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la == OR) {
					{
						{
							setState(109);
							match(OR);
							setState(110);
							((Conflict_flagsContext) _localctx).flag2 = match(TEXT);

							String flag2Name = "OF_" + ((Conflict_flagsContext) _localctx).flag2.getText().trim();
							ObjectFlag oFlag2 = ObjectFlag.valueOf(flag2Name);
							_localctx.conflictFlags.add(oFlag2);

						}
					}
					setState(116);
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
	public static class CurseContext extends ParserRuleContext {
		public Curse crs;
		public NameContext name;
		public CombatContext combat;
		public TypeContext type;
		public EffectContext effect;
		public DiceContext dice;
		public ExprContext expr;
		public TimeContext time;
		public FlagsContext flags;
		public ValuesContext values;
		public DescContext desc;
		public ConflictContext conflict;
		public Conflict_flagsContext conflict_flags;

		public NameContext name() {
			return getRuleContext(NameContext.class, 0);
		}

		public EffectContext effect() {
			return getRuleContext(EffectContext.class, 0);
		}

		public DiceContext dice() {
			return getRuleContext(DiceContext.class, 0);
		}

		public ExprContext expr() {
			return getRuleContext(ExprContext.class, 0);
		}

		public TimeContext time() {
			return getRuleContext(TimeContext.class, 0);
		}

		public MsgContext msg() {
			return getRuleContext(MsgContext.class, 0);
		}

		public DescContext desc() {
			return getRuleContext(DescContext.class, 0);
		}

		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}

		public TypeContext type(int i) {
			return getRuleContext(TypeContext.class, i);
		}

		public CombatContext combat() {
			return getRuleContext(CombatContext.class, 0);
		}

		public FlagsContext flags() {
			return getRuleContext(FlagsContext.class, 0);
		}

		public ValuesContext values() {
			return getRuleContext(ValuesContext.class, 0);
		}

		public ConflictContext conflict() {
			return getRuleContext(ConflictContext.class, 0);
		}

		public Conflict_flagsContext conflict_flags() {
			return getRuleContext(Conflict_flagsContext.class, 0);
		}

		public CurseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_curse;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).enterCurse(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).exitCurse(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseReaderVisitor)
				return ((CurseReaderVisitor<? extends T>) visitor).visitCurse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CurseContext curse() throws RecognitionException {
		CurseContext _localctx = new CurseContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_curse);

		String curseName = "";
		boolean cursePoss = false;
		ArrayList<ObjectBase> curseBases = new ArrayList<>();
		ArrayList<ObjectFlag> curseFlags = new ArrayList<>();
		String curseConflict = "";
		ArrayList<ObjectFlag> curseConflictFlags = new ArrayList<>();
		String curseDice = "";
		String curseTime = "";
		String curseDesc = "";
		EffectEnum curseEffect = EffectEnum.EF_NONE;
		TimedEffect curseTimedEffect = TimedEffect.TMD_NULL;
		int curseComToHit = 0;
		int curseComToDam = 0;
		int curseComAC = 0;
		char curseExpressionChar = '\0';
		EffectBaseType curseEFB = EffectBaseType.EFB_NULL;
		String expressionOperation = "";
		HashMap<ValueEnum, Integer> curseValueCollection = new HashMap<>();
		String curseMessage = "";

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(117);
				((CurseContext) _localctx).name = name();

				curseName = ((CurseContext) _localctx).name.nameStr;

				setState(143);
				_errHandler.sync(this);
				switch (getInterpreter().adaptivePredict(_input, 8, _ctx)) {
					case 1: {
						{
							setState(122);
							_errHandler.sync(this);
							_la = _input.LA(1);
							if (_la == COMBAT) {
								{
									setState(119);
									((CurseContext) _localctx).combat = combat();

									curseComToHit = ((CurseContext) _localctx).combat.toHitVal;
									curseComToDam = ((CurseContext) _localctx).combat.toDamVal;
									curseComAC = ((CurseContext) _localctx).combat.acVal;

								}
							}

							setState(127);
							_errHandler.sync(this);
							_la = _input.LA(1);
							do {
								{
									{
										setState(124);
										((CurseContext) _localctx).type = type();

										curseBases.add(((CurseContext) _localctx).type.typeBase);

									}
								}
								setState(129);
								_errHandler.sync(this);
								_la = _input.LA(1);
							} while (_la == TYPE);
						}
					}
					break;
					case 2: {
						setState(134);
						_errHandler.sync(this);
						_la = _input.LA(1);
						do {
							{
								{
									setState(131);
									((CurseContext) _localctx).type = type();

									curseBases.add(((CurseContext) _localctx).type.typeBase);
								}
							}
							setState(136);
							_errHandler.sync(this);
							_la = _input.LA(1);
						} while (_la == TYPE);
						setState(141);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la == COMBAT) {
							{
								setState(138);
								((CurseContext) _localctx).combat = combat();

								curseComToHit = ((CurseContext) _localctx).combat.toHitVal;
								curseComToDam = ((CurseContext) _localctx).combat.toDamVal;
								curseComAC = ((CurseContext) _localctx).combat.acVal;

							}
						}

					}
					break;
				}
				setState(148);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la == EFFECT) {
					{
						setState(145);
						((CurseContext) _localctx).effect = effect();

						curseEffect = ((CurseContext) _localctx).effect.effectEnum;
						curseTimedEffect = ((CurseContext) _localctx).effect.timedEnum;

					}
				}

				setState(153);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la == DICE) {
					{
						setState(150);
						((CurseContext) _localctx).dice = dice();

						curseDice = ((CurseContext) _localctx).dice.diceString;

					}
				}

				setState(158);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la == EXPR) {
					{
						setState(155);
						((CurseContext) _localctx).expr = expr();

						curseExpressionChar = ((CurseContext) _localctx).expr.letter;
						curseEFB = ((CurseContext) _localctx).expr.effectBaseType;
						expressionOperation = ((CurseContext) _localctx).expr.operation;

					}
				}

				setState(163);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la == TIME) {
					{
						setState(160);
						((CurseContext) _localctx).time = time();

						curseTime = ((CurseContext) _localctx).time.timeString;

					}
				}

				setState(183);
				_errHandler.sync(this);
				switch (getInterpreter().adaptivePredict(_input, 16, _ctx)) {
					case 1: {
						{
							setState(168);
							_errHandler.sync(this);
							_la = _input.LA(1);
							if (_la == FLAGS) {
								{
									setState(165);
									((CurseContext) _localctx).flags = flags();

									for (ObjectFlag flag : ((CurseContext) _localctx).flags.flagArray)
										curseFlags.add(flag);

								}
							}

							setState(173);
							_errHandler.sync(this);
							_la = _input.LA(1);
							if (_la == VALUES) {
								{
									setState(170);
									((CurseContext) _localctx).values = values();

									for (ValueEnum value : ((CurseContext) _localctx).values.valueCollection.keySet()) {
										curseValueCollection.put(value, ((CurseContext) _localctx).values.valueCollection.get(value));
									}

								}
							}

						}
					}
					break;
					case 2: {
						{
							setState(178);
							_errHandler.sync(this);
							_la = _input.LA(1);
							if (_la == VALUES) {
								{
									setState(175);
									((CurseContext) _localctx).values = values();

									for (ValueEnum value : ((CurseContext) _localctx).values.valueCollection.keySet()) {
										curseValueCollection.put(value, ((CurseContext) _localctx).values.valueCollection.get(value));
									}
								}
							}

							{
								setState(180);
								((CurseContext) _localctx).flags = flags();

								for (ObjectFlag flag : ((CurseContext) _localctx).flags.flagArray)
									curseFlags.add(flag);

							}
						}
					}
					break;
				}
				setState(186);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la == MSG) {
					{
						setState(185);
						msg();
					}
				}

				setState(213);
				_errHandler.sync(this);
				switch (getInterpreter().adaptivePredict(_input, 22, _ctx)) {
					case 1: {
						{
							setState(188);
							((CurseContext) _localctx).desc = desc();

							curseDesc = ((CurseContext) _localctx).desc.description;

							setState(193);
							_errHandler.sync(this);
							_la = _input.LA(1);
							if (_la == CONFLICT) {
								{
									setState(190);
									((CurseContext) _localctx).conflict = conflict();

									curseConflict = ((CurseContext) _localctx).conflict.conflictString;

								}
							}

							setState(198);
							_errHandler.sync(this);
							_la = _input.LA(1);
							if (_la == CONFLICT_FLAGS) {
								{
									setState(195);
									((CurseContext) _localctx).conflict_flags = conflict_flags();

									for (ObjectFlag flag : ((CurseContext) _localctx).conflict_flags.conflictFlags)
										curseConflictFlags.add(flag);

								}
							}

						}
					}
					break;
					case 2: {
						{
							setState(203);
							_errHandler.sync(this);
							_la = _input.LA(1);
							if (_la == CONFLICT) {
								{
									setState(200);
									((CurseContext) _localctx).conflict = conflict();

									curseConflict = ((CurseContext) _localctx).conflict.conflictString;

								}
							}

							setState(208);
							_errHandler.sync(this);
							_la = _input.LA(1);
							if (_la == CONFLICT_FLAGS) {
								{
									setState(205);
									((CurseContext) _localctx).conflict_flags = conflict_flags();

									for (ObjectFlag flag : ((CurseContext) _localctx).conflict_flags.conflictFlags)
										curseConflictFlags.add(flag);

								}
							}

							setState(210);
							((CurseContext) _localctx).desc = desc();

							curseDesc = ((CurseContext) _localctx).desc.description;

						}
					}
					break;
				}
			}
			_ctx.stop = _input.LT(-1);

			((CurseContext) _localctx).crs = new Curse(curseName, cursePoss, curseBases, curseFlags,
					curseConflict, curseConflictFlags, curseDice, curseTime,
					curseDesc, curseEffect, curseTimedEffect,
					curseComToHit, curseComToDam, curseComAC,
					curseExpressionChar, curseEFB, expressionOperation,
					curseValueCollection, curseMessage);

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
	public static class CursesContext extends ParserRuleContext {
		public ArrayList<Curse> curseList;
		public CurseContext curse;

		public TerminalNode EOF() {
			return getToken(CurseReaderParser.EOF, 0);
		}

		public List<CurseContext> curse() {
			return getRuleContexts(CurseContext.class);
		}

		public CurseContext curse(int i) {
			return getRuleContext(CurseContext.class, i);
		}

		public CursesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_curses;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).enterCurses(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseReaderListener) ((CurseReaderListener) listener).exitCurses(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseReaderVisitor)
				return ((CurseReaderVisitor<? extends T>) visitor).visitCurses(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CursesContext curses() throws RecognitionException {
		CursesContext _localctx = new CursesContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_curses);

		((CursesContext) _localctx).curseList = new ArrayList<>();

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(218);
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
						{
							setState(215);
							((CursesContext) _localctx).curse = curse();

							_localctx.curseList.add(((CursesContext) _localctx).curse.crs);

						}
					}
					setState(220);
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while (_la == NAME);
				setState(222);
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
			"\u0004\u0001\u0014\u00e1\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001" +
					"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004" +
					"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007" +
					"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b" +
					"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0001\u0000\u0001" +
					"\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
					"\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
					"\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
					"\u0003\u0001\u0003\u0001\u0003\u0003\u00035\b\u0003\u0001\u0004\u0001" +
					"\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
					"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001" +
					"\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
					"\u0007\u0001\u0007\u0001\u0007\u0005\u0007M\b\u0007\n\u0007\f\u0007P\t" +
					"\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b" +
					"\u0005\bZ\b\b\n\b\f\b]\t\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001" +
					"\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
					"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0005\fq\b\f\n\f\f\ft\t\f\u0001" +
					"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003\r{\b\r\u0001\r\u0001\r\u0001" +
					"\r\u0004\r\u0080\b\r\u000b\r\f\r\u0081\u0001\r\u0001\r\u0001\r\u0004\r" +
					"\u0087\b\r\u000b\r\f\r\u0088\u0001\r\u0001\r\u0001\r\u0003\r\u008e\b\r" +
					"\u0003\r\u0090\b\r\u0001\r\u0001\r\u0001\r\u0003\r\u0095\b\r\u0001\r\u0001" +
					"\r\u0001\r\u0003\r\u009a\b\r\u0001\r\u0001\r\u0001\r\u0003\r\u009f\b\r" +
					"\u0001\r\u0001\r\u0001\r\u0003\r\u00a4\b\r\u0001\r\u0001\r\u0001\r\u0003" +
					"\r\u00a9\b\r\u0001\r\u0001\r\u0001\r\u0003\r\u00ae\b\r\u0001\r\u0001\r" +
					"\u0001\r\u0003\r\u00b3\b\r\u0001\r\u0001\r\u0001\r\u0003\r\u00b8\b\r\u0001" +
					"\r\u0003\r\u00bb\b\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003\r\u00c2" +
					"\b\r\u0001\r\u0001\r\u0001\r\u0003\r\u00c7\b\r\u0001\r\u0001\r\u0001\r" +
					"\u0003\r\u00cc\b\r\u0001\r\u0001\r\u0001\r\u0003\r\u00d1\b\r\u0001\r\u0001" +
					"\r\u0001\r\u0003\r\u00d6\b\r\u0001\u000e\u0001\u000e\u0001\u000e\u0004" +
					"\u000e\u00db\b\u000e\u000b\u000e\f\u000e\u00dc\u0001\u000e\u0001\u000e" +
					"\u0001\u000e\u0000\u0000\u000f\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010" +
					"\u0012\u0014\u0016\u0018\u001a\u001c\u0000\u0000\u00e9\u0000\u001e\u0001" +
					"\u0000\u0000\u0000\u0002\"\u0001\u0000\u0000\u0000\u0004&\u0001\u0000" +
					"\u0000\u0000\u0006.\u0001\u0000\u0000\u0000\b6\u0001\u0000\u0000\u0000" +
					"\n:\u0001\u0000\u0000\u0000\fB\u0001\u0000\u0000\u0000\u000eF\u0001\u0000" +
					"\u0000\u0000\u0010Q\u0001\u0000\u0000\u0000\u0012^\u0001\u0000\u0000\u0000" +
					"\u0014b\u0001\u0000\u0000\u0000\u0016f\u0001\u0000\u0000\u0000\u0018j" +
					"\u0001\u0000\u0000\u0000\u001au\u0001\u0000\u0000\u0000\u001c\u00da\u0001" +
					"\u0000\u0000\u0000\u001e\u001f\u0005\u0003\u0000\u0000\u001f \u0005\u0012" +
					"\u0000\u0000 !\u0006\u0000\uffff\uffff\u0000!\u0001\u0001\u0000\u0000" +
					"\u0000\"#\u0005\u0004\u0000\u0000#$\u0005\u0012\u0000\u0000$%\u0006\u0001" +
					"\uffff\uffff\u0000%\u0003\u0001\u0000\u0000\u0000&\'\u0005\u0006\u0000" +
					"\u0000\'(\u0005\u0012\u0000\u0000()\u0005\u0013\u0000\u0000)*\u0005\u0012" +
					"\u0000\u0000*+\u0005\u0013\u0000\u0000+,\u0005\u0012\u0000\u0000,-\u0006" +
					"\u0002\uffff\uffff\u0000-\u0005\u0001\u0000\u0000\u0000./\u0005\u0007" +
					"\u0000\u0000/0\u0005\u0012\u0000\u000004\u0006\u0003\uffff\uffff\u0000" +
					"12\u0005\u0013\u0000\u000023\u0005\u0012\u0000\u000035\u0006\u0003\uffff" +
					"\uffff\u000041\u0001\u0000\u0000\u000045\u0001\u0000\u0000\u00005\u0007" +
					"\u0001\u0000\u0000\u000067\u0005\b\u0000\u000078\u0005\u0012\u0000\u0000" +
					"89\u0006\u0004\uffff\uffff\u00009\t\u0001\u0000\u0000\u0000:;\u0005\t" +
					"\u0000\u0000;<\u0005\u0012\u0000\u0000<=\u0005\u0013\u0000\u0000=>\u0005" +
					"\u0012\u0000\u0000>?\u0005\u0013\u0000\u0000?@\u0005\u0012\u0000\u0000" +
					"@A\u0006\u0005\uffff\uffff\u0000A\u000b\u0001\u0000\u0000\u0000BC\u0005" +
					"\n\u0000\u0000CD\u0005\u0012\u0000\u0000DE\u0006\u0006\uffff\uffff\u0000" +
					"E\r\u0001\u0000\u0000\u0000FG\u0005\u000b\u0000\u0000GH\u0005\u0012\u0000" +
					"\u0000HN\u0006\u0007\uffff\uffff\u0000IJ\u0005\u0014\u0000\u0000JK\u0005" +
					"\u0012\u0000\u0000KM\u0006\u0007\uffff\uffff\u0000LI\u0001\u0000\u0000" +
					"\u0000MP\u0001\u0000\u0000\u0000NL\u0001\u0000\u0000\u0000NO\u0001\u0000" +
					"\u0000\u0000O\u000f\u0001\u0000\u0000\u0000PN\u0001\u0000\u0000\u0000" +
					"QR\u0005\f\u0000\u0000RS\u0005\u0012\u0000\u0000ST\u0005\u0011\u0000\u0000" +
					"T[\u0006\b\uffff\uffff\u0000UV\u0005\u0014\u0000\u0000VW\u0005\u0012\u0000" +
					"\u0000WX\u0005\u0011\u0000\u0000XZ\u0006\b\uffff\uffff\u0000YU\u0001\u0000" +
					"\u0000\u0000Z]\u0001\u0000\u0000\u0000[Y\u0001\u0000\u0000\u0000[\\\u0001" +
					"\u0000\u0000\u0000\\\u0011\u0001\u0000\u0000\u0000][\u0001\u0000\u0000" +
					"\u0000^_\u0005\r\u0000\u0000_`\u0005\u0012\u0000\u0000`a\u0006\t\uffff" +
					"\uffff\u0000a\u0013\u0001\u0000\u0000\u0000bc\u0005\u000e\u0000\u0000" +
					"cd\u0005\u0012\u0000\u0000de\u0006\n\uffff\uffff\u0000e\u0015\u0001\u0000" +
					"\u0000\u0000fg\u0005\u000f\u0000\u0000gh\u0005\u0012\u0000\u0000hi\u0006" +
					"\u000b\uffff\uffff\u0000i\u0017\u0001\u0000\u0000\u0000jk\u0005\u0010" +
					"\u0000\u0000kl\u0005\u0012\u0000\u0000lr\u0006\f\uffff\uffff\u0000mn\u0005" +
					"\u0014\u0000\u0000no\u0005\u0012\u0000\u0000oq\u0006\f\uffff\uffff\u0000" +
					"pm\u0001\u0000\u0000\u0000qt\u0001\u0000\u0000\u0000rp\u0001\u0000\u0000" +
					"\u0000rs\u0001\u0000\u0000\u0000s\u0019\u0001\u0000\u0000\u0000tr\u0001" +
					"\u0000\u0000\u0000uv\u0003\u0000\u0000\u0000v\u008f\u0006\r\uffff\uffff" +
					"\u0000wx\u0003\u0004\u0002\u0000xy\u0006\r\uffff\uffff\u0000y{\u0001\u0000" +
					"\u0000\u0000zw\u0001\u0000\u0000\u0000z{\u0001\u0000\u0000\u0000{\u007f" +
					"\u0001\u0000\u0000\u0000|}\u0003\u0002\u0001\u0000}~\u0006\r\uffff\uffff" +
					"\u0000~\u0080\u0001\u0000\u0000\u0000\u007f|\u0001\u0000\u0000\u0000\u0080" +
					"\u0081\u0001\u0000\u0000\u0000\u0081\u007f\u0001\u0000\u0000\u0000\u0081" +
					"\u0082\u0001\u0000\u0000\u0000\u0082\u0090\u0001\u0000\u0000\u0000\u0083" +
					"\u0084\u0003\u0002\u0001\u0000\u0084\u0085\u0006\r\uffff\uffff\u0000\u0085" +
					"\u0087\u0001\u0000\u0000\u0000\u0086\u0083\u0001\u0000\u0000\u0000\u0087" +
					"\u0088\u0001\u0000\u0000\u0000\u0088\u0086\u0001\u0000\u0000\u0000\u0088" +
					"\u0089\u0001\u0000\u0000\u0000\u0089\u008d\u0001\u0000\u0000\u0000\u008a" +
					"\u008b\u0003\u0004\u0002\u0000\u008b\u008c\u0006\r\uffff\uffff\u0000\u008c" +
					"\u008e\u0001\u0000\u0000\u0000\u008d\u008a\u0001\u0000\u0000\u0000\u008d" +
					"\u008e\u0001\u0000\u0000\u0000\u008e\u0090\u0001\u0000\u0000\u0000\u008f" +
					"z\u0001\u0000\u0000\u0000\u008f\u0086\u0001\u0000\u0000\u0000\u0090\u0094" +
					"\u0001\u0000\u0000\u0000\u0091\u0092\u0003\u0006\u0003\u0000\u0092\u0093" +
					"\u0006\r\uffff\uffff\u0000\u0093\u0095\u0001\u0000\u0000\u0000\u0094\u0091" +
					"\u0001\u0000\u0000\u0000\u0094\u0095\u0001\u0000\u0000\u0000\u0095\u0099" +
					"\u0001\u0000\u0000\u0000\u0096\u0097\u0003\b\u0004\u0000\u0097\u0098\u0006" +
					"\r\uffff\uffff\u0000\u0098\u009a\u0001\u0000\u0000\u0000\u0099\u0096\u0001" +
					"\u0000\u0000\u0000\u0099\u009a\u0001\u0000\u0000\u0000\u009a\u009e\u0001" +
					"\u0000\u0000\u0000\u009b\u009c\u0003\n\u0005\u0000\u009c\u009d\u0006\r" +
					"\uffff\uffff\u0000\u009d\u009f\u0001\u0000\u0000\u0000\u009e\u009b\u0001" +
					"\u0000\u0000\u0000\u009e\u009f\u0001\u0000\u0000\u0000\u009f\u00a3\u0001" +
					"\u0000\u0000\u0000\u00a0\u00a1\u0003\f\u0006\u0000\u00a1\u00a2\u0006\r" +
					"\uffff\uffff\u0000\u00a2\u00a4\u0001\u0000\u0000\u0000\u00a3\u00a0\u0001" +
					"\u0000\u0000\u0000\u00a3\u00a4\u0001\u0000\u0000\u0000\u00a4\u00b7\u0001" +
					"\u0000\u0000\u0000\u00a5\u00a6\u0003\u000e\u0007\u0000\u00a6\u00a7\u0006" +
					"\r\uffff\uffff\u0000\u00a7\u00a9\u0001\u0000\u0000\u0000\u00a8\u00a5\u0001" +
					"\u0000\u0000\u0000\u00a8\u00a9\u0001\u0000\u0000\u0000\u00a9\u00ad\u0001" +
					"\u0000\u0000\u0000\u00aa\u00ab\u0003\u0010\b\u0000\u00ab\u00ac\u0006\r" +
					"\uffff\uffff\u0000\u00ac\u00ae\u0001\u0000\u0000\u0000\u00ad\u00aa\u0001" +
					"\u0000\u0000\u0000\u00ad\u00ae\u0001\u0000\u0000\u0000\u00ae\u00b8\u0001" +
					"\u0000\u0000\u0000\u00af\u00b0\u0003\u0010\b\u0000\u00b0\u00b1\u0006\r" +
					"\uffff\uffff\u0000\u00b1\u00b3\u0001\u0000\u0000\u0000\u00b2\u00af\u0001" +
					"\u0000\u0000\u0000\u00b2\u00b3\u0001\u0000\u0000\u0000\u00b3\u00b4\u0001" +
					"\u0000\u0000\u0000\u00b4\u00b5\u0003\u000e\u0007\u0000\u00b5\u00b6\u0006" +
					"\r\uffff\uffff\u0000\u00b6\u00b8\u0001\u0000\u0000\u0000\u00b7\u00a8\u0001" +
					"\u0000\u0000\u0000\u00b7\u00b2\u0001\u0000\u0000\u0000\u00b8\u00ba\u0001" +
					"\u0000\u0000\u0000\u00b9\u00bb\u0003\u0012\t\u0000\u00ba\u00b9\u0001\u0000" +
					"\u0000\u0000\u00ba\u00bb\u0001\u0000\u0000\u0000\u00bb\u00d5\u0001\u0000" +
					"\u0000\u0000\u00bc\u00bd\u0003\u0014\n\u0000\u00bd\u00c1\u0006\r\uffff" +
					"\uffff\u0000\u00be\u00bf\u0003\u0016\u000b\u0000\u00bf\u00c0\u0006\r\uffff" +
					"\uffff\u0000\u00c0\u00c2\u0001\u0000\u0000\u0000\u00c1\u00be\u0001\u0000" +
					"\u0000\u0000\u00c1\u00c2\u0001\u0000\u0000\u0000\u00c2\u00c6\u0001\u0000" +
					"\u0000\u0000\u00c3\u00c4\u0003\u0018\f\u0000\u00c4\u00c5\u0006\r\uffff" +
					"\uffff\u0000\u00c5\u00c7\u0001\u0000\u0000\u0000\u00c6\u00c3\u0001\u0000" +
					"\u0000\u0000\u00c6\u00c7\u0001\u0000\u0000\u0000\u00c7\u00d6\u0001\u0000" +
					"\u0000\u0000\u00c8\u00c9\u0003\u0016\u000b\u0000\u00c9\u00ca\u0006\r\uffff" +
					"\uffff\u0000\u00ca\u00cc\u0001\u0000\u0000\u0000\u00cb\u00c8\u0001\u0000" +
					"\u0000\u0000\u00cb\u00cc\u0001\u0000\u0000\u0000\u00cc\u00d0\u0001\u0000" +
					"\u0000\u0000\u00cd\u00ce\u0003\u0018\f\u0000\u00ce\u00cf\u0006\r\uffff" +
					"\uffff\u0000\u00cf\u00d1\u0001\u0000\u0000\u0000\u00d0\u00cd\u0001\u0000" +
					"\u0000\u0000\u00d0\u00d1\u0001\u0000\u0000\u0000\u00d1\u00d2\u0001\u0000" +
					"\u0000\u0000\u00d2\u00d3\u0003\u0014\n\u0000\u00d3\u00d4\u0006\r\uffff" +
					"\uffff\u0000\u00d4\u00d6\u0001\u0000\u0000\u0000\u00d5\u00bc\u0001\u0000" +
					"\u0000\u0000\u00d5\u00cb\u0001\u0000\u0000\u0000\u00d6\u001b\u0001\u0000" +
					"\u0000\u0000\u00d7\u00d8\u0003\u001a\r\u0000\u00d8\u00d9\u0006\u000e\uffff" +
					"\uffff\u0000\u00d9\u00db\u0001\u0000\u0000\u0000\u00da\u00d7\u0001\u0000" +
					"\u0000\u0000\u00db\u00dc\u0001\u0000\u0000\u0000\u00dc\u00da\u0001\u0000" +
					"\u0000\u0000\u00dc\u00dd\u0001\u0000\u0000\u0000\u00dd\u00de\u0001\u0000" +
					"\u0000\u0000\u00de\u00df\u0005\u0000\u0000\u0001\u00df\u001d\u0001\u0000" +
					"\u0000\u0000\u00184N[rz\u0081\u0088\u008d\u008f\u0094\u0099\u009e\u00a3" +
					"\u00a8\u00ad\u00b2\u00b7\u00ba\u00c1\u00c6\u00cb\u00d0\u00d5\u00dc";
	public static final ATN _ATN =
			new ATNDeserializer().deserialize(_serializedATN.toCharArray());

	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}