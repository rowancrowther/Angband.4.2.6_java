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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Curse.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.curse;

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
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class CurseParser extends Parser {
	static {
		RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
	}

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
			new PredictionContextCache();
	public static final int
			T__0 = 1, T__1 = 2, COMMENT = 3, EOL = 4, NAME = 5, TYPE = 6, WEIGHT = 7, COMBAT = 8,
			EFFECT = 9, DICE = 10, EXPR = 11, TIME = 12, FLAGS = 13, VALUES = 14, MSG = 15, DESC = 16,
			CONFLICT = 17, CONFLICT_FLAGS = 18, STRING = 19, COLON = 20, LBRACKET = 21, RBRACKET = 22;
	public static final int
			RULE_name = 0, RULE_type = 1, RULE_weight = 2, RULE_combat = 3, RULE_effect = 4,
			RULE_dice = 5, RULE_expr = 6, RULE_time = 7, RULE_flags = 8, RULE_values = 9,
			RULE_msg = 10, RULE_desc = 11, RULE_conflict = 12, RULE_conflict_flags = 13,
			RULE_curse = 14, RULE_file = 15;

	private static String[] makeRuleNames() {
		return new String[]{
				"name", "type", "weight", "combat", "effect", "dice", "expr", "time",
				"flags", "values", "msg", "desc", "conflict", "conflict_flags", "curse",
				"file"
		};
	}

	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[]{
				null, "' | '", "'|'", null, null, "'name:'", "'type:'", "'weight:'",
				"'combat:'", "'effect:'", "'dice:'", "'expr:'", "'time:'", "'flags:'",
				"'values:'", "'msg:'", "'desc:'", "'conflict:'", "'conflict-flags:'",
				null, "':'", "'['", "']'"
		};
	}

	private static final String[] _LITERAL_NAMES = makeLiteralNames();

	private static String[] makeSymbolicNames() {
		return new String[]{
				null, null, null, "COMMENT", "EOL", "NAME", "TYPE", "WEIGHT", "COMBAT",
				"EFFECT", "DICE", "EXPR", "TIME", "FLAGS", "VALUES", "MSG", "DESC", "CONFLICT",
				"CONFLICT_FLAGS", "STRING", "COLON", "LBRACKET", "RBRACKET"
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
		return "Curse.g4";
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

	public CurseParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public Token STRING;

		public TerminalNode NAME() {
			return getToken(CurseParser.NAME, 0);
		}

		public TerminalNode STRING() {
			return getToken(CurseParser.STRING, 0);
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
			if (listener instanceof CurseListener) ((CurseListener) listener).enterName(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseListener) ((CurseListener) listener).exitName(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseVisitor) return ((CurseVisitor<? extends T>) visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(32);
				match(NAME);
				setState(33);
				((NameContext) _localctx).STRING = match(STRING);
				((NameContext) _localctx).nameStr = ((NameContext) _localctx).STRING.getText();
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
		public ObjectBase baseObj;
		public Token STRING;

		public TerminalNode TYPE() {
			return getToken(CurseParser.TYPE, 0);
		}

		public TerminalNode STRING() {
			return getToken(CurseParser.STRING, 0);
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
			if (listener instanceof CurseListener) ((CurseListener) listener).enterType(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseListener) ((CurseListener) listener).exitType(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseVisitor) return ((CurseVisitor<? extends T>) visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(36);
				match(TYPE);
				setState(37);
				((TypeContext) _localctx).STRING = match(STRING);
				((TypeContext) _localctx).baseObj = GameConstants.lookupObjectBase(((TypeContext) _localctx).STRING.getText());
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
	public static class WeightContext extends ParserRuleContext {
		public int weightAdj;
		public Token STRING;

		public TerminalNode WEIGHT() {
			return getToken(CurseParser.WEIGHT, 0);
		}

		public TerminalNode STRING() {
			return getToken(CurseParser.STRING, 0);
		}

		public WeightContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}

		@Override
		public int getRuleIndex() {
			return RULE_weight;
		}

		@Override
		public void enterRule(ParseTreeListener listener) {
			if (listener instanceof CurseListener) ((CurseListener) listener).enterWeight(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseListener) ((CurseListener) listener).exitWeight(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseVisitor) return ((CurseVisitor<? extends T>) visitor).visitWeight(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WeightContext weight() throws RecognitionException {
		WeightContext _localctx = new WeightContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_weight);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(40);
				match(WEIGHT);
				setState(41);
				((WeightContext) _localctx).STRING = match(STRING);
				((WeightContext) _localctx).weightAdj = Integer.parseInt(((WeightContext) _localctx).STRING.getText());
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
		public int toHit;
		public int toDam;
		public int toAC;
		public Token toh;
		public Token tod;
		public Token toa;

		public TerminalNode COMBAT() {
			return getToken(CurseParser.COMBAT, 0);
		}

		public List<TerminalNode> COLON() {
			return getTokens(CurseParser.COLON);
		}

		public TerminalNode COLON(int i) {
			return getToken(CurseParser.COLON, i);
		}

		public List<TerminalNode> STRING() {
			return getTokens(CurseParser.STRING);
		}

		public TerminalNode STRING(int i) {
			return getToken(CurseParser.STRING, i);
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
			if (listener instanceof CurseListener) ((CurseListener) listener).enterCombat(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseListener) ((CurseListener) listener).exitCombat(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseVisitor) return ((CurseVisitor<? extends T>) visitor).visitCombat(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CombatContext combat() throws RecognitionException {
		CombatContext _localctx = new CombatContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_combat);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(44);
				match(COMBAT);
				setState(45);
				((CombatContext) _localctx).toh = match(STRING);
				setState(46);
				match(COLON);
				setState(47);
				((CombatContext) _localctx).tod = match(STRING);
				setState(48);
				match(COLON);
				setState(49);
				((CombatContext) _localctx).toa = match(STRING);

				((CombatContext) _localctx).toHit = Integer.parseInt(((CombatContext) _localctx).toh.getText());
				((CombatContext) _localctx).toDam = Integer.parseInt(((CombatContext) _localctx).tod.getText());
				((CombatContext) _localctx).toAC = Integer.parseInt(((CombatContext) _localctx).toa.getText());

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
		public MonsterRaceFlag summonMon;
		public TimedEffect timedEffect;
		public Token eff;
		public Token opt;

		public TerminalNode EFFECT() {
			return getToken(CurseParser.EFFECT, 0);
		}

		public List<TerminalNode> STRING() {
			return getTokens(CurseParser.STRING);
		}

		public TerminalNode STRING(int i) {
			return getToken(CurseParser.STRING, i);
		}

		public TerminalNode COLON() {
			return getToken(CurseParser.COLON, 0);
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
			if (listener instanceof CurseListener) ((CurseListener) listener).enterEffect(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseListener) ((CurseListener) listener).exitEffect(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseVisitor) return ((CurseVisitor<? extends T>) visitor).visitEffect(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectContext effect() throws RecognitionException {
		EffectContext _localctx = new EffectContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_effect);

		((EffectContext) _localctx).summonMon = MonsterRaceFlag.RF_NONE;
		((EffectContext) _localctx).timedEffect = TimedEffect.TMD_NONE;

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(52);
				match(EFFECT);
				setState(53);
				((EffectContext) _localctx).eff = match(STRING);

				((EffectContext) _localctx).effectEnum = EffectEnum.valueOf("EF_" + ((EffectContext) _localctx).eff.getText());

				setState(58);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la == COLON) {
					{
						setState(55);
						match(COLON);
						setState(56);
						((EffectContext) _localctx).opt = match(STRING);

						((EffectContext) _localctx).summonMon = MonsterRaceFlag.RF_NONE;
						((EffectContext) _localctx).timedEffect = TimedEffect.TMD_NONE;

						switch (((EffectContext) _localctx).eff.getText()) {
							case "SUMMON":
								((EffectContext) _localctx).summonMon = MonsterRaceFlag.valueOf("RF_" + ((EffectContext) _localctx).opt.getText());
								break;

							case "TIMED_INC":
								((EffectContext) _localctx).timedEffect = TimedEffect.valueOf("TMD_" + ((EffectContext) _localctx).opt.getText());
								break;
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
		public String diceStr;
		public Token STRING;

		public TerminalNode DICE() {
			return getToken(CurseParser.DICE, 0);
		}

		public TerminalNode STRING() {
			return getToken(CurseParser.STRING, 0);
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
			if (listener instanceof CurseListener) ((CurseListener) listener).enterDice(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseListener) ((CurseListener) listener).exitDice(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseVisitor) return ((CurseVisitor<? extends T>) visitor).visitDice(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DiceContext dice() throws RecognitionException {
		DiceContext _localctx = new DiceContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_dice);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(60);
				match(DICE);
				setState(61);
				((DiceContext) _localctx).STRING = match(STRING);
				((DiceContext) _localctx).diceStr = ((DiceContext) _localctx).STRING.getText();
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
		public char exprChar;
		public EffectBaseType evBase;
		public String effectString;
		public Token ch;
		public Token base;
		public Token eff;

		public TerminalNode EXPR() {
			return getToken(CurseParser.EXPR, 0);
		}

		public List<TerminalNode> COLON() {
			return getTokens(CurseParser.COLON);
		}

		public TerminalNode COLON(int i) {
			return getToken(CurseParser.COLON, i);
		}

		public List<TerminalNode> STRING() {
			return getTokens(CurseParser.STRING);
		}

		public TerminalNode STRING(int i) {
			return getToken(CurseParser.STRING, i);
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
			if (listener instanceof CurseListener) ((CurseListener) listener).enterExpr(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseListener) ((CurseListener) listener).exitExpr(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseVisitor) return ((CurseVisitor<? extends T>) visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(64);
				match(EXPR);
				setState(65);
				((ExprContext) _localctx).ch = match(STRING);
				setState(66);
				match(COLON);
				setState(67);
				((ExprContext) _localctx).base = match(STRING);
				setState(68);
				match(COLON);
				setState(69);
				((ExprContext) _localctx).eff = match(STRING);

				String raw = ((ExprContext) _localctx).ch.getText();
				if (raw.length() != 1)
					throw new IllegalArgumentException("Expression character code may only have one character.");

				((ExprContext) _localctx).exprChar = ((ExprContext) _localctx).ch.getText().charAt(0);
				((ExprContext) _localctx).evBase = EffectBaseType.valueOf("EFB_" + ((ExprContext) _localctx).base.getText());
				((ExprContext) _localctx).effectString = ((ExprContext) _localctx).eff.getText();

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
		public String timeStr;
		public Token STRING;

		public TerminalNode TIME() {
			return getToken(CurseParser.TIME, 0);
		}

		public TerminalNode STRING() {
			return getToken(CurseParser.STRING, 0);
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
			if (listener instanceof CurseListener) ((CurseListener) listener).enterTime(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseListener) ((CurseListener) listener).exitTime(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseVisitor) return ((CurseVisitor<? extends T>) visitor).visitTime(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TimeContext time() throws RecognitionException {
		TimeContext _localctx = new TimeContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_time);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(72);
				match(TIME);
				setState(73);
				((TimeContext) _localctx).STRING = match(STRING);
				((TimeContext) _localctx).timeStr = ((TimeContext) _localctx).STRING.getText();
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
        public List<ObjectFlag> flagList;
		public Token flg1;
		public Token flg2;

		public TerminalNode FLAGS() {
			return getToken(CurseParser.FLAGS, 0);
		}

		public List<TerminalNode> STRING() {
			return getTokens(CurseParser.STRING);
		}

		public TerminalNode STRING(int i) {
			return getToken(CurseParser.STRING, i);
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
			if (listener instanceof CurseListener) ((CurseListener) listener).enterFlags(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseListener) ((CurseListener) listener).exitFlags(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseVisitor) return ((CurseVisitor<? extends T>) visitor).visitFlags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagsContext flags() throws RecognitionException {
		FlagsContext _localctx = new FlagsContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_flags);
		((FlagsContext) _localctx).flagList = new ArrayList<>();
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(76);
				match(FLAGS);
				setState(77);
				((FlagsContext) _localctx).flg1 = match(STRING);

                _localctx.flagList.add(ObjectFlag.valueOf("OF_" + ((FlagsContext) _localctx).flg1.getText().trim()));

				setState(84);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la == T__0) {
					{
						{
							setState(79);
							match(T__0);
							setState(80);
							((FlagsContext) _localctx).flg2 = match(STRING);

                            _localctx.flagList.add(ObjectFlag.valueOf("OF_" + ((FlagsContext) _localctx).flg2.getText().trim()));

						}
					}
					setState(86);
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
		public Map<ValueEnum, Integer> vals;
		public Token cde1;
		public Token val1;
		public Token cde2;
		public Token val2;

		public TerminalNode VALUES() {
			return getToken(CurseParser.VALUES, 0);
		}

		public List<TerminalNode> LBRACKET() {
			return getTokens(CurseParser.LBRACKET);
		}

		public TerminalNode LBRACKET(int i) {
			return getToken(CurseParser.LBRACKET, i);
		}

		public List<TerminalNode> RBRACKET() {
			return getTokens(CurseParser.RBRACKET);
		}

		public TerminalNode RBRACKET(int i) {
			return getToken(CurseParser.RBRACKET, i);
		}

		public List<TerminalNode> STRING() {
			return getTokens(CurseParser.STRING);
		}

		public TerminalNode STRING(int i) {
			return getToken(CurseParser.STRING, i);
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
			if (listener instanceof CurseListener) ((CurseListener) listener).enterValues(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseListener) ((CurseListener) listener).exitValues(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseVisitor) return ((CurseVisitor<? extends T>) visitor).visitValues(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValuesContext values() throws RecognitionException {
		ValuesContext _localctx = new ValuesContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_values);
		((ValuesContext) _localctx).vals = new HashMap<>();
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(87);
				match(VALUES);
				setState(88);
				((ValuesContext) _localctx).cde1 = match(STRING);
				setState(89);
				match(LBRACKET);
				setState(90);
				((ValuesContext) _localctx).val1 = match(STRING);
				setState(91);
				match(RBRACKET);

				ValueEnum c1 = ValueEnum.valueOf("CV_" + ((ValuesContext) _localctx).cde1.getText().trim());
				int v1 = Integer.parseInt(((ValuesContext) _localctx).val1.getText());
				_localctx.vals.put(c1, v1);

				setState(101);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la == T__0) {
					{
						{
							setState(93);
							match(T__0);
							setState(94);
							((ValuesContext) _localctx).cde2 = match(STRING);
							setState(95);
							match(LBRACKET);
							setState(96);
							((ValuesContext) _localctx).val2 = match(STRING);
							setState(97);
							match(RBRACKET);

							ValueEnum c2 = ValueEnum.valueOf("CV_" + ((ValuesContext) _localctx).cde2.getText().trim());
							int v2 = Integer.parseInt(((ValuesContext) _localctx).val2.getText());
							_localctx.vals.put(c2, v2);

						}
					}
					setState(103);
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
		public String msgStr;
		public Token STRING;

		public TerminalNode MSG() {
			return getToken(CurseParser.MSG, 0);
		}

		public TerminalNode STRING() {
			return getToken(CurseParser.STRING, 0);
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
			if (listener instanceof CurseListener) ((CurseListener) listener).enterMsg(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseListener) ((CurseListener) listener).exitMsg(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseVisitor) return ((CurseVisitor<? extends T>) visitor).visitMsg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MsgContext msg() throws RecognitionException {
		MsgContext _localctx = new MsgContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_msg);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(104);
				match(MSG);
				setState(105);
				((MsgContext) _localctx).STRING = match(STRING);
				((MsgContext) _localctx).msgStr = ((MsgContext) _localctx).STRING.getText();
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
		public Token STRING;

		public TerminalNode DESC() {
			return getToken(CurseParser.DESC, 0);
		}

		public TerminalNode STRING() {
			return getToken(CurseParser.STRING, 0);
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
			if (listener instanceof CurseListener) ((CurseListener) listener).enterDesc(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseListener) ((CurseListener) listener).exitDesc(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseVisitor) return ((CurseVisitor<? extends T>) visitor).visitDesc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescContext desc() throws RecognitionException {
		DescContext _localctx = new DescContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_desc);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(108);
				match(DESC);
				setState(109);
				((DescContext) _localctx).STRING = match(STRING);
				((DescContext) _localctx).descStr = ((DescContext) _localctx).STRING.getText();
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
		public String confStr;
		public Token STRING;

		public TerminalNode CONFLICT() {
			return getToken(CurseParser.CONFLICT, 0);
		}

		public TerminalNode STRING() {
			return getToken(CurseParser.STRING, 0);
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
			if (listener instanceof CurseListener) ((CurseListener) listener).enterConflict(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseListener) ((CurseListener) listener).exitConflict(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseVisitor) return ((CurseVisitor<? extends T>) visitor).visitConflict(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConflictContext conflict() throws RecognitionException {
		ConflictContext _localctx = new ConflictContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_conflict);
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(112);
				match(CONFLICT);
				setState(113);
				((ConflictContext) _localctx).STRING = match(STRING);
				((ConflictContext) _localctx).confStr = ((ConflictContext) _localctx).STRING.getText();
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
        public List<ObjectFlag> cFlags;
		public Token flg1;
		public Token flg2;

		public TerminalNode CONFLICT_FLAGS() {
			return getToken(CurseParser.CONFLICT_FLAGS, 0);
		}

		public List<TerminalNode> STRING() {
			return getTokens(CurseParser.STRING);
		}

		public TerminalNode STRING(int i) {
			return getToken(CurseParser.STRING, i);
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
			if (listener instanceof CurseListener) ((CurseListener) listener).enterConflict_flags(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseListener) ((CurseListener) listener).exitConflict_flags(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseVisitor) return ((CurseVisitor<? extends T>) visitor).visitConflict_flags(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Conflict_flagsContext conflict_flags() throws RecognitionException {
		Conflict_flagsContext _localctx = new Conflict_flagsContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_conflict_flags);
		((Conflict_flagsContext) _localctx).cFlags = new ArrayList<>();
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(116);
				match(CONFLICT_FLAGS);
				setState(117);
				((Conflict_flagsContext) _localctx).flg1 = match(STRING);

                _localctx.cFlags.add(ObjectFlag.valueOf("OF_" + ((Conflict_flagsContext) _localctx).flg1.getText().trim()));

				setState(124);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la == T__1) {
					{
						{
							setState(119);
							match(T__1);
							setState(120);
							((Conflict_flagsContext) _localctx).flg2 = match(STRING);

                            _localctx.cFlags.add(ObjectFlag.valueOf("OF_" + ((Conflict_flagsContext) _localctx).flg2.getText().trim()));

						}
					}
					setState(126);
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
		public Curse cur;
		public NameContext name;
		public TypeContext type;
		public WeightContext weight;
		public CombatContext combat;
		public EffectContext effect;
		public DiceContext dice;
		public ExprContext expr;
		public TimeContext time;
		public FlagsContext flags;
		public ValuesContext values;
		public MsgContext msg;
		public DescContext desc;
		public ConflictContext conflict;
		public Conflict_flagsContext conflict_flags;

		public NameContext name() {
			return getRuleContext(NameContext.class, 0);
		}

		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}

		public TypeContext type(int i) {
			return getRuleContext(TypeContext.class, i);
		}

		public List<WeightContext> weight() {
			return getRuleContexts(WeightContext.class);
		}

		public WeightContext weight(int i) {
			return getRuleContext(WeightContext.class, i);
		}

		public List<CombatContext> combat() {
			return getRuleContexts(CombatContext.class);
		}

		public CombatContext combat(int i) {
			return getRuleContext(CombatContext.class, i);
		}

		public List<EffectContext> effect() {
			return getRuleContexts(EffectContext.class);
		}

		public EffectContext effect(int i) {
			return getRuleContext(EffectContext.class, i);
		}

		public List<DiceContext> dice() {
			return getRuleContexts(DiceContext.class);
		}

		public DiceContext dice(int i) {
			return getRuleContext(DiceContext.class, i);
		}

		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}

		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class, i);
		}

		public List<TimeContext> time() {
			return getRuleContexts(TimeContext.class);
		}

		public TimeContext time(int i) {
			return getRuleContext(TimeContext.class, i);
		}

		public List<FlagsContext> flags() {
			return getRuleContexts(FlagsContext.class);
		}

		public FlagsContext flags(int i) {
			return getRuleContext(FlagsContext.class, i);
		}

		public List<ValuesContext> values() {
			return getRuleContexts(ValuesContext.class);
		}

		public ValuesContext values(int i) {
			return getRuleContext(ValuesContext.class, i);
		}

		public List<MsgContext> msg() {
			return getRuleContexts(MsgContext.class);
		}

		public MsgContext msg(int i) {
			return getRuleContext(MsgContext.class, i);
		}

		public List<DescContext> desc() {
			return getRuleContexts(DescContext.class);
		}

		public DescContext desc(int i) {
			return getRuleContext(DescContext.class, i);
		}

		public List<ConflictContext> conflict() {
			return getRuleContexts(ConflictContext.class);
		}

		public ConflictContext conflict(int i) {
			return getRuleContext(ConflictContext.class, i);
		}

		public List<Conflict_flagsContext> conflict_flags() {
			return getRuleContexts(Conflict_flagsContext.class);
		}

		public Conflict_flagsContext conflict_flags(int i) {
			return getRuleContext(Conflict_flagsContext.class, i);
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
			if (listener instanceof CurseListener) ((CurseListener) listener).enterCurse(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseListener) ((CurseListener) listener).exitCurse(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseVisitor) return ((CurseVisitor<? extends T>) visitor).visitCurse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CurseContext curse() throws RecognitionException {
		CurseContext _localctx = new CurseContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_curse);

		String nameInit = "";
		boolean possInit = false;
		List<ObjectBase> basesInit = new ArrayList<>();
		int weightInit = 0;
		int tohInit = 0;
		int todInit = 0;
		int toaInit = 0;
		EffectEnum effectEnumInit = EffectEnum.EF_NONE;
		MonsterRaceFlag summonMonInit = MonsterRaceFlag.RF_NONE;
		TimedEffect timedEffectInit = TimedEffect.TMD_NONE;
		String diceInit = "";
		char exprCharInit = '\0';
		EffectBaseType evBaseInit = EffectBaseType.EFB_NONE;
		String effectStringInit = "";
		String timeInit = "";
        List<ObjectFlag> flagsInit = new ArrayList<>();
		Map<ValueEnum, Integer> valsInit = new HashMap<>();
		String msgInit = "";
		String descInit = "";
		String conflictInit = "";
        List<ObjectFlag> confFlagsInit = new ArrayList<>();

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(127);
				((CurseContext) _localctx).name = name();
				nameInit = ((CurseContext) _localctx).name.nameStr;
				setState(168);
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
						setState(168);
						_errHandler.sync(this);
						switch (_input.LA(1)) {
							case TYPE: {
								setState(129);
								((CurseContext) _localctx).type = type();
								basesInit.add(((CurseContext) _localctx).type.baseObj);
							}
							break;
							case WEIGHT: {
								setState(132);
								((CurseContext) _localctx).weight = weight();
								weightInit = ((CurseContext) _localctx).weight.weightAdj;
							}
							break;
							case COMBAT: {
								setState(135);
								((CurseContext) _localctx).combat = combat();

								tohInit = ((CurseContext) _localctx).combat.toHit;
								todInit = ((CurseContext) _localctx).combat.toDam;
								toaInit = ((CurseContext) _localctx).combat.toAC;

							}
							break;
							case EFFECT: {
								setState(138);
								((CurseContext) _localctx).effect = effect();

								effectEnumInit = ((CurseContext) _localctx).effect.effectEnum;
								summonMonInit = ((CurseContext) _localctx).effect.summonMon;
								timedEffectInit = ((CurseContext) _localctx).effect.timedEffect;

							}
							break;
							case DICE: {
								setState(141);
								((CurseContext) _localctx).dice = dice();
								diceInit = ((CurseContext) _localctx).dice.diceStr;
							}
							break;
							case EXPR: {
								setState(144);
								((CurseContext) _localctx).expr = expr();

								exprCharInit = ((CurseContext) _localctx).expr.exprChar;
								evBaseInit = ((CurseContext) _localctx).expr.evBase;
								effectStringInit = ((CurseContext) _localctx).expr.effectString;

							}
							break;
							case TIME: {
								setState(147);
								((CurseContext) _localctx).time = time();
								timeInit = ((CurseContext) _localctx).time.timeStr;
							}
							break;
							case FLAGS: {
								setState(150);
								((CurseContext) _localctx).flags = flags();

								flagsInit.addAll(((CurseContext) _localctx).flags.flagList);

							}
							break;
							case VALUES: {
								setState(153);
								((CurseContext) _localctx).values = values();
								valsInit.putAll(((CurseContext) _localctx).values.vals);
							}
							break;
							case MSG: {
								setState(156);
								((CurseContext) _localctx).msg = msg();
								msgInit = ((CurseContext) _localctx).msg.msgStr;
							}
							break;
							case DESC: {
								setState(159);
								((CurseContext) _localctx).desc = desc();
								descInit = ((CurseContext) _localctx).desc.descStr;
							}
							break;
							case CONFLICT: {
								setState(162);
								((CurseContext) _localctx).conflict = conflict();
								conflictInit = ((CurseContext) _localctx).conflict.confStr;
							}
							break;
							case CONFLICT_FLAGS: {
								setState(165);
								((CurseContext) _localctx).conflict_flags = conflict_flags();

								confFlagsInit.addAll(((CurseContext) _localctx).conflict_flags.cFlags);

							}
							break;
							default:
								throw new NoViableAltException(this);
						}
					}
					setState(170);
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 524224L) != 0));
			}
			_ctx.stop = _input.LT(-1);

			((CurseContext) _localctx).cur = new Curse(nameInit, possInit, basesInit, weightInit, flagsInit,
					conflictInit, confFlagsInit, diceInit, timeInit,
					descInit, effectEnumInit, summonMonInit,
					timedEffectInit, tohInit, todInit, toaInit,
					exprCharInit, evBaseInit, effectStringInit, valsInit,
					msgInit);

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
		public List<Curse> curses;
		public CurseContext curse;

		public TerminalNode EOF() {
			return getToken(CurseParser.EOF, 0);
		}

		public List<CurseContext> curse() {
			return getRuleContexts(CurseContext.class);
		}

		public CurseContext curse(int i) {
			return getRuleContext(CurseContext.class, i);
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
			if (listener instanceof CurseListener) ((CurseListener) listener).enterFile(this);
		}

		@Override
		public void exitRule(ParseTreeListener listener) {
			if (listener instanceof CurseListener) ((CurseListener) listener).exitFile(this);
		}

		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if (visitor instanceof CurseVisitor) return ((CurseVisitor<? extends T>) visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_file);

		((FileContext) _localctx).curses = new ArrayList<>();

		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
				setState(175);
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
						{
							setState(172);
							((FileContext) _localctx).curse = curse();
							_localctx.curses.add(((FileContext) _localctx).curse.cur);
						}
					}
					setState(177);
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while (_la == NAME);
				setState(179);
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
			"\u0004\u0001\u0016\u00b6\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001" +
					"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004" +
					"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007" +
					"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b" +
					"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007" +
					"\u000f\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001" +
					"\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
					"\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
					"\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
					"\u0004\u0001\u0004\u0001\u0004\u0003\u0004;\b\u0004\u0001\u0005\u0001" +
					"\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
					"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001" +
					"\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b" +
					"\u0001\b\u0005\bS\b\b\n\b\f\bV\t\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001" +
					"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0005\td\b" +
					"\t\n\t\f\tg\t\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b" +
					"\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001" +
					"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0005\r{\b\r\n\r\f\r~\t\r\u0001\u000e" +
					"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e" +
					"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e" +
					"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e" +
					"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e" +
					"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e" +
					"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e" +
					"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0004\u000e\u00a9\b\u000e" +
					"\u000b\u000e\f\u000e\u00aa\u0001\u000f\u0001\u000f\u0001\u000f\u0004\u000f" +
					"\u00b0\b\u000f\u000b\u000f\f\u000f\u00b1\u0001\u000f\u0001\u000f\u0001" +
					"\u000f\u0000\u0000\u0010\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012" +
					"\u0014\u0016\u0018\u001a\u001c\u001e\u0000\u0000\u00b7\u0000 \u0001\u0000" +
					"\u0000\u0000\u0002$\u0001\u0000\u0000\u0000\u0004(\u0001\u0000\u0000\u0000" +
					"\u0006,\u0001\u0000\u0000\u0000\b4\u0001\u0000\u0000\u0000\n<\u0001\u0000" +
					"\u0000\u0000\f@\u0001\u0000\u0000\u0000\u000eH\u0001\u0000\u0000\u0000" +
					"\u0010L\u0001\u0000\u0000\u0000\u0012W\u0001\u0000\u0000\u0000\u0014h" +
					"\u0001\u0000\u0000\u0000\u0016l\u0001\u0000\u0000\u0000\u0018p\u0001\u0000" +
					"\u0000\u0000\u001at\u0001\u0000\u0000\u0000\u001c\u007f\u0001\u0000\u0000" +
					"\u0000\u001e\u00af\u0001\u0000\u0000\u0000 !\u0005\u0005\u0000\u0000!" +
					"\"\u0005\u0013\u0000\u0000\"#\u0006\u0000\uffff\uffff\u0000#\u0001\u0001" +
					"\u0000\u0000\u0000$%\u0005\u0006\u0000\u0000%&\u0005\u0013\u0000\u0000" +
					"&\'\u0006\u0001\uffff\uffff\u0000\'\u0003\u0001\u0000\u0000\u0000()\u0005" +
					"\u0007\u0000\u0000)*\u0005\u0013\u0000\u0000*+\u0006\u0002\uffff\uffff" +
					"\u0000+\u0005\u0001\u0000\u0000\u0000,-\u0005\b\u0000\u0000-.\u0005\u0013" +
					"\u0000\u0000./\u0005\u0014\u0000\u0000/0\u0005\u0013\u0000\u000001\u0005" +
					"\u0014\u0000\u000012\u0005\u0013\u0000\u000023\u0006\u0003\uffff\uffff" +
					"\u00003\u0007\u0001\u0000\u0000\u000045\u0005\t\u0000\u000056\u0005\u0013" +
					"\u0000\u00006:\u0006\u0004\uffff\uffff\u000078\u0005\u0014\u0000\u0000" +
					"89\u0005\u0013\u0000\u00009;\u0006\u0004\uffff\uffff\u0000:7\u0001\u0000" +
					"\u0000\u0000:;\u0001\u0000\u0000\u0000;\t\u0001\u0000\u0000\u0000<=\u0005" +
					"\n\u0000\u0000=>\u0005\u0013\u0000\u0000>?\u0006\u0005\uffff\uffff\u0000" +
					"?\u000b\u0001\u0000\u0000\u0000@A\u0005\u000b\u0000\u0000AB\u0005\u0013" +
					"\u0000\u0000BC\u0005\u0014\u0000\u0000CD\u0005\u0013\u0000\u0000DE\u0005" +
					"\u0014\u0000\u0000EF\u0005\u0013\u0000\u0000FG\u0006\u0006\uffff\uffff" +
					"\u0000G\r\u0001\u0000\u0000\u0000HI\u0005\f\u0000\u0000IJ\u0005\u0013" +
					"\u0000\u0000JK\u0006\u0007\uffff\uffff\u0000K\u000f\u0001\u0000\u0000" +
					"\u0000LM\u0005\r\u0000\u0000MN\u0005\u0013\u0000\u0000NT\u0006\b\uffff" +
					"\uffff\u0000OP\u0005\u0001\u0000\u0000PQ\u0005\u0013\u0000\u0000QS\u0006" +
					"\b\uffff\uffff\u0000RO\u0001\u0000\u0000\u0000SV\u0001\u0000\u0000\u0000" +
					"TR\u0001\u0000\u0000\u0000TU\u0001\u0000\u0000\u0000U\u0011\u0001\u0000" +
					"\u0000\u0000VT\u0001\u0000\u0000\u0000WX\u0005\u000e\u0000\u0000XY\u0005" +
					"\u0013\u0000\u0000YZ\u0005\u0015\u0000\u0000Z[\u0005\u0013\u0000\u0000" +
					"[\\\u0005\u0016\u0000\u0000\\e\u0006\t\uffff\uffff\u0000]^\u0005\u0001" +
					"\u0000\u0000^_\u0005\u0013\u0000\u0000_`\u0005\u0015\u0000\u0000`a\u0005" +
					"\u0013\u0000\u0000ab\u0005\u0016\u0000\u0000bd\u0006\t\uffff\uffff\u0000" +
					"c]\u0001\u0000\u0000\u0000dg\u0001\u0000\u0000\u0000ec\u0001\u0000\u0000" +
					"\u0000ef\u0001\u0000\u0000\u0000f\u0013\u0001\u0000\u0000\u0000ge\u0001" +
					"\u0000\u0000\u0000hi\u0005\u000f\u0000\u0000ij\u0005\u0013\u0000\u0000" +
					"jk\u0006\n\uffff\uffff\u0000k\u0015\u0001\u0000\u0000\u0000lm\u0005\u0010" +
					"\u0000\u0000mn\u0005\u0013\u0000\u0000no\u0006\u000b\uffff\uffff\u0000" +
					"o\u0017\u0001\u0000\u0000\u0000pq\u0005\u0011\u0000\u0000qr\u0005\u0013" +
					"\u0000\u0000rs\u0006\f\uffff\uffff\u0000s\u0019\u0001\u0000\u0000\u0000" +
					"tu\u0005\u0012\u0000\u0000uv\u0005\u0013\u0000\u0000v|\u0006\r\uffff\uffff" +
					"\u0000wx\u0005\u0002\u0000\u0000xy\u0005\u0013\u0000\u0000y{\u0006\r\uffff" +
					"\uffff\u0000zw\u0001\u0000\u0000\u0000{~\u0001\u0000\u0000\u0000|z\u0001" +
					"\u0000\u0000\u0000|}\u0001\u0000\u0000\u0000}\u001b\u0001\u0000\u0000" +
					"\u0000~|\u0001\u0000\u0000\u0000\u007f\u0080\u0003\u0000\u0000\u0000\u0080" +
					"\u00a8\u0006\u000e\uffff\uffff\u0000\u0081\u0082\u0003\u0002\u0001\u0000" +
					"\u0082\u0083\u0006\u000e\uffff\uffff\u0000\u0083\u00a9\u0001\u0000\u0000" +
					"\u0000\u0084\u0085\u0003\u0004\u0002\u0000\u0085\u0086\u0006\u000e\uffff" +
					"\uffff\u0000\u0086\u00a9\u0001\u0000\u0000\u0000\u0087\u0088\u0003\u0006" +
					"\u0003\u0000\u0088\u0089\u0006\u000e\uffff\uffff\u0000\u0089\u00a9\u0001" +
					"\u0000\u0000\u0000\u008a\u008b\u0003\b\u0004\u0000\u008b\u008c\u0006\u000e" +
					"\uffff\uffff\u0000\u008c\u00a9\u0001\u0000\u0000\u0000\u008d\u008e\u0003" +
					"\n\u0005\u0000\u008e\u008f\u0006\u000e\uffff\uffff\u0000\u008f\u00a9\u0001" +
					"\u0000\u0000\u0000\u0090\u0091\u0003\f\u0006\u0000\u0091\u0092\u0006\u000e" +
					"\uffff\uffff\u0000\u0092\u00a9\u0001\u0000\u0000\u0000\u0093\u0094\u0003" +
					"\u000e\u0007\u0000\u0094\u0095\u0006\u000e\uffff\uffff\u0000\u0095\u00a9" +
					"\u0001\u0000\u0000\u0000\u0096\u0097\u0003\u0010\b\u0000\u0097\u0098\u0006" +
					"\u000e\uffff\uffff\u0000\u0098\u00a9\u0001\u0000\u0000\u0000\u0099\u009a" +
					"\u0003\u0012\t\u0000\u009a\u009b\u0006\u000e\uffff\uffff\u0000\u009b\u00a9" +
					"\u0001\u0000\u0000\u0000\u009c\u009d\u0003\u0014\n\u0000\u009d\u009e\u0006" +
					"\u000e\uffff\uffff\u0000\u009e\u00a9\u0001\u0000\u0000\u0000\u009f\u00a0" +
					"\u0003\u0016\u000b\u0000\u00a0\u00a1\u0006\u000e\uffff\uffff\u0000\u00a1" +
					"\u00a9\u0001\u0000\u0000\u0000\u00a2\u00a3\u0003\u0018\f\u0000\u00a3\u00a4" +
					"\u0006\u000e\uffff\uffff\u0000\u00a4\u00a9\u0001\u0000\u0000\u0000\u00a5" +
					"\u00a6\u0003\u001a\r\u0000\u00a6\u00a7\u0006\u000e\uffff\uffff\u0000\u00a7" +
					"\u00a9\u0001\u0000\u0000\u0000\u00a8\u0081\u0001\u0000\u0000\u0000\u00a8" +
					"\u0084\u0001\u0000\u0000\u0000\u00a8\u0087\u0001\u0000\u0000\u0000\u00a8" +
					"\u008a\u0001\u0000\u0000\u0000\u00a8\u008d\u0001\u0000\u0000\u0000\u00a8" +
					"\u0090\u0001\u0000\u0000\u0000\u00a8\u0093\u0001\u0000\u0000\u0000\u00a8" +
					"\u0096\u0001\u0000\u0000\u0000\u00a8\u0099\u0001\u0000\u0000\u0000\u00a8" +
					"\u009c\u0001\u0000\u0000\u0000\u00a8\u009f\u0001\u0000\u0000\u0000\u00a8" +
					"\u00a2\u0001\u0000\u0000\u0000\u00a8\u00a5\u0001\u0000\u0000\u0000\u00a9" +
					"\u00aa\u0001\u0000\u0000\u0000\u00aa\u00a8\u0001\u0000\u0000\u0000\u00aa" +
					"\u00ab\u0001\u0000\u0000\u0000\u00ab\u001d\u0001\u0000\u0000\u0000\u00ac" +
					"\u00ad\u0003\u001c\u000e\u0000\u00ad\u00ae\u0006\u000f\uffff\uffff\u0000" +
					"\u00ae\u00b0\u0001\u0000\u0000\u0000\u00af\u00ac\u0001\u0000\u0000\u0000" +
					"\u00b0\u00b1\u0001\u0000\u0000\u0000\u00b1\u00af\u0001\u0000\u0000\u0000" +
					"\u00b1\u00b2\u0001\u0000\u0000\u0000\u00b2\u00b3\u0001\u0000\u0000\u0000" +
					"\u00b3\u00b4\u0005\u0000\u0000\u0001\u00b4\u001f\u0001\u0000\u0000\u0000" +
					"\u0007:Te|\u00a8\u00aa\u00b1";
	public static final ATN _ATN =
			new ATNDeserializer().deserialize(_serializedATN.toCharArray());

	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}