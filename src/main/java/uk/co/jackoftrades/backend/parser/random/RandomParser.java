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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Random.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.random;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.numerics.Random;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class RandomParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
	public static final int
            D = 1, M = 2, PLUS = 3, MINUS = 4, DOLLAR_LETTER = 5, NUMBER = 6;
	public static final int
            RULE_dice = 0;
	private static String[] makeRuleNames() {
        return new String[]{
                "dice"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
        return new String[]{
                null, "'d'", null, "'+'", "'-'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
        return new String[]{
                null, "D", "M", "PLUS", "MINUS", "DOLLAR_LETTER", "NUMBER"
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
        return "Random.g4";
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

	public RandomParser(TokenStream input) {
		super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DiceContext extends ParserRuleContext {
		public Random random;
		public Token base;
		public Token diceNum;
		public Token sides;
		public Token m_bonus;

        public TerminalNode EOF() {
            return getToken(RandomParser.EOF, 0);
        }

        public TerminalNode PLUS() {
            return getToken(RandomParser.PLUS, 0);
        }

        public TerminalNode D() {
            return getToken(RandomParser.D, 0);
        }

        public TerminalNode M() {
            return getToken(RandomParser.M, 0);
        }

        public TerminalNode MINUS() {
            return getToken(RandomParser.MINUS, 0);
        }

        public TerminalNode DOLLAR_LETTER() {
            return getToken(RandomParser.DOLLAR_LETTER, 0);
        }

        public List<TerminalNode> NUMBER() {
            return getTokens(RandomParser.NUMBER);
        }
		public TerminalNode NUMBER(int i) {
			return getToken(RandomParser.NUMBER, i);
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
            if (listener instanceof RandomListener) ((RandomListener) listener).enterDice(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
            if (listener instanceof RandomListener) ((RandomListener) listener).exitDice(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RandomVisitor) return ((RandomVisitor<? extends T>) visitor).visitDice(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DiceContext dice() throws RecognitionException {
		DiceContext _localctx = new DiceContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_dice);

        String baseStr = "";
        int baseInit = 0;
        int diceInit = 0;
        String sidesStr = "";
        int sidesInit = 1;
        int mBonusInit = 0;
		        
		try {
			enterOuterAlt(_localctx, 1);
			{
                setState(134);
                _errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(_input, 0, _ctx)) {
                    case 1: {
                        setState(2);
                        ((DiceContext) _localctx).base = match(DOLLAR_LETTER);
                        setState(3);
                        match(PLUS);
                        setState(4);
                        ((DiceContext) _localctx).diceNum = match(NUMBER);
                        setState(5);
                        match(D);
                        setState(6);
                        ((DiceContext) _localctx).sides = match(NUMBER);
                        setState(7);
                        match(M);
                        setState(8);
                        ((DiceContext) _localctx).m_bonus = match(NUMBER);

                        baseStr = ((DiceContext) _localctx).base.getText();
                        diceInit = Integer.parseInt(((DiceContext) _localctx).diceNum.getText());
                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());
                        mBonusInit = Integer.parseInt(((DiceContext) _localctx).m_bonus.getText());

                    }
                    break;
                    case 2: {
                        setState(10);
                        ((DiceContext) _localctx).base = match(DOLLAR_LETTER);
                        setState(11);
                        match(PLUS);
                        setState(12);
                        ((DiceContext) _localctx).diceNum = match(NUMBER);
                        setState(13);
                        match(D);
                        setState(14);
                        ((DiceContext) _localctx).sides = match(NUMBER);

                        baseStr = ((DiceContext) _localctx).base.getText();
                        diceInit = Integer.parseInt(((DiceContext) _localctx).diceNum.getText());
                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());

                    }
                    break;
                    case 3: {
                        setState(16);
                        match(MINUS);
                        setState(17);
                        ((DiceContext) _localctx).base = match(NUMBER);
                        setState(18);
                        match(PLUS);
                        setState(19);
                        ((DiceContext) _localctx).diceNum = match(NUMBER);
                        setState(20);
                        match(D);
                        setState(21);
                        ((DiceContext) _localctx).sides = match(NUMBER);
                        setState(22);
                        match(M);
                        setState(23);
                        ((DiceContext) _localctx).m_bonus = match(NUMBER);

                        baseInit = -1 * Integer.parseInt(((DiceContext) _localctx).base.getText());
                        diceInit = Integer.parseInt(((DiceContext) _localctx).diceNum.getText());
                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());
                        mBonusInit = Integer.parseInt(((DiceContext) _localctx).m_bonus.getText());

                    }
                    break;
                    case 4: {
                        setState(25);
                        ((DiceContext) _localctx).base = match(NUMBER);
                        setState(26);
                        match(PLUS);
                        setState(27);
                        ((DiceContext) _localctx).diceNum = match(NUMBER);
                        setState(28);
                        match(D);
                        setState(29);
                        ((DiceContext) _localctx).sides = match(NUMBER);
                        setState(30);
                        match(M);
                        setState(31);
                        ((DiceContext) _localctx).m_bonus = match(NUMBER);

                        baseInit = Integer.parseInt(((DiceContext) _localctx).base.getText());
                        diceInit = Integer.parseInt(((DiceContext) _localctx).diceNum.getText());
                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());
                        mBonusInit = Integer.parseInt(((DiceContext) _localctx).m_bonus.getText());

                    }
                    break;
                    case 5: {
                        setState(33);
                        match(MINUS);
                        setState(34);
                        ((DiceContext) _localctx).base = match(NUMBER);
                        setState(35);
                        match(PLUS);
                        setState(36);
                        match(D);
                        setState(37);
                        ((DiceContext) _localctx).sides = match(NUMBER);
                        setState(38);
                        match(M);
                        setState(39);
                        ((DiceContext) _localctx).m_bonus = match(NUMBER);

                        baseInit = -1 * Integer.parseInt(((DiceContext) _localctx).base.getText());
                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());
                        mBonusInit = Integer.parseInt(((DiceContext) _localctx).m_bonus.getText());

                    }
                    break;
                    case 6: {
                        setState(41);
                        ((DiceContext) _localctx).base = match(DOLLAR_LETTER);
                        setState(42);
                        match(PLUS);
                        setState(43);
                        match(D);
                        setState(44);
                        ((DiceContext) _localctx).sides = match(NUMBER);
                        setState(45);
                        match(M);
                        setState(46);
                        ((DiceContext) _localctx).m_bonus = match(NUMBER);

                        baseStr = ((DiceContext) _localctx).base.getText();
                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());
                        mBonusInit = Integer.parseInt(((DiceContext) _localctx).m_bonus.getText());

                    }
                    break;
                    case 7: {
                        setState(48);
                        ((DiceContext) _localctx).base = match(NUMBER);
                        setState(49);
                        match(PLUS);
                        setState(50);
                        match(D);
                        setState(51);
                        ((DiceContext) _localctx).sides = match(NUMBER);
                        setState(52);
                        match(M);
                        setState(53);
                        ((DiceContext) _localctx).m_bonus = match(NUMBER);

                        baseInit = Integer.parseInt(((DiceContext) _localctx).base.getText());
                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());
                        mBonusInit = Integer.parseInt(((DiceContext) _localctx).m_bonus.getText());

                    }
                    break;
                    case 8: {
                        setState(55);
                        ((DiceContext) _localctx).diceNum = match(NUMBER);
                        setState(56);
                        match(D);
                        setState(57);
                        ((DiceContext) _localctx).sides = match(NUMBER);
                        setState(58);
                        match(M);
                        setState(59);
                        ((DiceContext) _localctx).m_bonus = match(NUMBER);

                        diceInit = Integer.parseInt(((DiceContext) _localctx).diceNum.getText());
                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());
                        mBonusInit = Integer.parseInt(((DiceContext) _localctx).m_bonus.getText());

                    }
                    break;
                    case 9: {
                        setState(61);
                        match(D);
                        setState(62);
                        ((DiceContext) _localctx).sides = match(NUMBER);
                        setState(63);
                        match(M);
                        setState(64);
                        ((DiceContext) _localctx).m_bonus = match(NUMBER);

                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());
                        mBonusInit = Integer.parseInt(((DiceContext) _localctx).m_bonus.getText());

                    }
                    break;
                    case 10: {
                        setState(66);
                        ((DiceContext) _localctx).base = match(NUMBER);
                        setState(67);
                        match(M);
                        setState(68);
                        ((DiceContext) _localctx).m_bonus = match(NUMBER);

                        baseInit = Integer.parseInt(((DiceContext) _localctx).base.getText());
                        mBonusInit = Integer.parseInt(((DiceContext) _localctx).m_bonus.getText());

                    }
                    break;
                    case 11: {
                        setState(70);
                        ((DiceContext) _localctx).base = match(NUMBER);
                        setState(71);
                        match(PLUS);
                        setState(72);
                        match(M);
                        setState(73);
                        ((DiceContext) _localctx).m_bonus = match(NUMBER);

                        baseInit = Integer.parseInt(((DiceContext) _localctx).base.getText());
                        mBonusInit = Integer.parseInt(((DiceContext) _localctx).m_bonus.getText());

                    }
                    break;
                    case 12: {
                        setState(75);
                        ((DiceContext) _localctx).base = match(DOLLAR_LETTER);
                        setState(76);
                        match(M);
                        setState(77);
                        ((DiceContext) _localctx).m_bonus = match(NUMBER);

                        baseStr = ((DiceContext) _localctx).base.getText();
                        mBonusInit = Integer.parseInt(((DiceContext) _localctx).m_bonus.getText());

                    }
                    break;
                    case 13: {
                        setState(79);
                        match(MINUS);
                        setState(80);
                        ((DiceContext) _localctx).base = match(NUMBER);
                        setState(81);
                        match(PLUS);
                        setState(82);
                        ((DiceContext) _localctx).diceNum = match(NUMBER);
                        setState(83);
                        match(D);
                        setState(84);
                        ((DiceContext) _localctx).sides = match(NUMBER);

                        baseInit = -1 * Integer.parseInt(((DiceContext) _localctx).base.getText());
                        diceInit = Integer.parseInt(((DiceContext) _localctx).diceNum.getText());
                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());

                    }
                    break;
                    case 14: {
                        setState(86);
                        ((DiceContext) _localctx).base = match(NUMBER);
                        setState(87);
                        match(PLUS);
                        setState(88);
                        ((DiceContext) _localctx).diceNum = match(NUMBER);
                        setState(89);
                        match(D);
                        setState(90);
                        ((DiceContext) _localctx).sides = match(NUMBER);

                        baseInit = Integer.parseInt(((DiceContext) _localctx).base.getText());
                        diceInit = Integer.parseInt(((DiceContext) _localctx).diceNum.getText());
                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());

                    }
                    break;
                    case 15: {
                        setState(92);
                        match(MINUS);
                        setState(93);
                        ((DiceContext) _localctx).base = match(NUMBER);
                        setState(94);
                        match(PLUS);
                        setState(95);
                        match(D);
                        setState(96);
                        ((DiceContext) _localctx).sides = match(NUMBER);

                        baseInit = -1 * Integer.parseInt(((DiceContext) _localctx).base.getText());
                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());

                    }
                    break;
                    case 16: {
                        setState(98);
                        ((DiceContext) _localctx).base = match(NUMBER);
                        setState(99);
                        match(PLUS);
                        setState(100);
                        match(D);
                        setState(101);
                        ((DiceContext) _localctx).sides = match(NUMBER);

                        baseInit = Integer.parseInt(((DiceContext) _localctx).base.getText());
                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());

                    }
                    break;
                    case 17: {
                        setState(103);
                        ((DiceContext) _localctx).diceNum = match(NUMBER);
                        setState(104);
                        match(D);
                        setState(105);
                        ((DiceContext) _localctx).sides = match(NUMBER);

                        diceInit = Integer.parseInt(((DiceContext) _localctx).diceNum.getText());
                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());

                    }
                    break;
                    case 18: {
                        setState(107);
                        match(D);
                        setState(108);
                        ((DiceContext) _localctx).sides = match(NUMBER);

                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());

                    }
                    break;
                    case 19: {
                        setState(110);
                        ((DiceContext) _localctx).base = match(DOLLAR_LETTER);

                        baseStr = ((DiceContext) _localctx).base.getText();

                    }
                    break;
                    case 20: {
                        setState(112);
                        ((DiceContext) _localctx).base = match(NUMBER);

                        baseInit = Integer.parseInt(((DiceContext) _localctx).base.getText());

                    }
                    break;
                    case 21: {
                        setState(114);
                        match(MINUS);
                        setState(115);
                        ((DiceContext) _localctx).base = match(NUMBER);

                        baseInit = -1 * Integer.parseInt(((DiceContext) _localctx).base.getText());

                    }
                    break;
                    case 22: {
                        setState(117);
                        ((DiceContext) _localctx).diceNum = match(NUMBER);
                        setState(118);
                        match(D);
                        setState(119);
                        ((DiceContext) _localctx).sides = match(DOLLAR_LETTER);

                        diceInit = Integer.parseInt(((DiceContext) _localctx).diceNum.getText());
                        sidesStr = ((DiceContext) _localctx).sides.getText();

                    }
                    break;
                    case 23: {
                        setState(121);
                        ((DiceContext) _localctx).base = match(NUMBER);
                        setState(122);
                        match(PLUS);
                        setState(123);
                        match(M);
                        setState(124);
                        ((DiceContext) _localctx).m_bonus = match(NUMBER);

                        baseInit = Integer.parseInt(((DiceContext) _localctx).base.getText());
                        mBonusInit = Integer.parseInt(((DiceContext) _localctx).m_bonus.getText());

                    }
                    break;
                    case 24: {
                        setState(126);
                        match(MINUS);
                        setState(127);
                        ((DiceContext) _localctx).diceNum = match(NUMBER);
                        setState(128);
                        match(D);
                        setState(129);
                        ((DiceContext) _localctx).sides = match(NUMBER);

                        diceInit = -1 * Integer.parseInt(((DiceContext) _localctx).diceNum.getText());
                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());

                    }
                    break;
                    case 25: {
                        setState(131);
                        match(M);
                        setState(132);
                        ((DiceContext) _localctx).m_bonus = match(NUMBER);

                        mBonusInit = Integer.parseInt(((DiceContext) _localctx).m_bonus.getText());

                    }
                    break;
                }
                setState(136);
                match(EOF);
			}
			_ctx.stop = _input.LT(-1);

            if (sidesInit < 1) sidesInit = 1;

            if (baseStr.isEmpty() && sidesStr.isEmpty())
                ((DiceContext) _localctx).random = new Random(baseInit, mBonusInit, diceInit, sidesInit, false);
            else if (baseStr.isEmpty())
                ((DiceContext) _localctx).random = new Random(baseInit, mBonusInit, diceInit, sidesStr);
            else
                ((DiceContext) _localctx).random = new Random(baseStr, mBonusInit, diceInit, sidesInit);

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
            "\u0004\u0001\u0006\u008b\u0002\u0000\u0007\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0003\u0000\u0087\b\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0000\u0000\u0001\u0000\u0000\u0000" +
                    "\u00a1\u0000\u0086\u0001\u0000\u0000\u0000\u0002\u0003\u0005\u0005\u0000" +
                    "\u0000\u0003\u0004\u0005\u0003\u0000\u0000\u0004\u0005\u0005\u0006\u0000" +
                    "\u0000\u0005\u0006\u0005\u0001\u0000\u0000\u0006\u0007\u0005\u0006\u0000" +
                    "\u0000\u0007\b\u0005\u0002\u0000\u0000\b\t\u0005\u0006\u0000\u0000\t\u0087" +
                    "\u0006\u0000\uffff\uffff\u0000\n\u000b\u0005\u0005\u0000\u0000\u000b\f" +
                    "\u0005\u0003\u0000\u0000\f\r\u0005\u0006\u0000\u0000\r\u000e\u0005\u0001" +
                    "\u0000\u0000\u000e\u000f\u0005\u0006\u0000\u0000\u000f\u0087\u0006\u0000" +
                    "\uffff\uffff\u0000\u0010\u0011\u0005\u0004\u0000\u0000\u0011\u0012\u0005" +
                    "\u0006\u0000\u0000\u0012\u0013\u0005\u0003\u0000\u0000\u0013\u0014\u0005" +
                    "\u0006\u0000\u0000\u0014\u0015\u0005\u0001\u0000\u0000\u0015\u0016\u0005" +
                    "\u0006\u0000\u0000\u0016\u0017\u0005\u0002\u0000\u0000\u0017\u0018\u0005" +
                    "\u0006\u0000\u0000\u0018\u0087\u0006\u0000\uffff\uffff\u0000\u0019\u001a" +
                    "\u0005\u0006\u0000\u0000\u001a\u001b\u0005\u0003\u0000\u0000\u001b\u001c" +
                    "\u0005\u0006\u0000\u0000\u001c\u001d\u0005\u0001\u0000\u0000\u001d\u001e" +
                    "\u0005\u0006\u0000\u0000\u001e\u001f\u0005\u0002\u0000\u0000\u001f \u0005" +
                    "\u0006\u0000\u0000 \u0087\u0006\u0000\uffff\uffff\u0000!\"\u0005\u0004" +
                    "\u0000\u0000\"#\u0005\u0006\u0000\u0000#$\u0005\u0003\u0000\u0000$%\u0005" +
                    "\u0001\u0000\u0000%&\u0005\u0006\u0000\u0000&\'\u0005\u0002\u0000\u0000" +
                    "\'(\u0005\u0006\u0000\u0000(\u0087\u0006\u0000\uffff\uffff\u0000)*\u0005" +
                    "\u0005\u0000\u0000*+\u0005\u0003\u0000\u0000+,\u0005\u0001\u0000\u0000" +
                    ",-\u0005\u0006\u0000\u0000-.\u0005\u0002\u0000\u0000./\u0005\u0006\u0000" +
                    "\u0000/\u0087\u0006\u0000\uffff\uffff\u000001\u0005\u0006\u0000\u0000" +
                    "12\u0005\u0003\u0000\u000023\u0005\u0001\u0000\u000034\u0005\u0006\u0000" +
                    "\u000045\u0005\u0002\u0000\u000056\u0005\u0006\u0000\u00006\u0087\u0006" +
                    "\u0000\uffff\uffff\u000078\u0005\u0006\u0000\u000089\u0005\u0001\u0000" +
                    "\u00009:\u0005\u0006\u0000\u0000:;\u0005\u0002\u0000\u0000;<\u0005\u0006" +
                    "\u0000\u0000<\u0087\u0006\u0000\uffff\uffff\u0000=>\u0005\u0001\u0000" +
                    "\u0000>?\u0005\u0006\u0000\u0000?@\u0005\u0002\u0000\u0000@A\u0005\u0006" +
                    "\u0000\u0000A\u0087\u0006\u0000\uffff\uffff\u0000BC\u0005\u0006\u0000" +
                    "\u0000CD\u0005\u0002\u0000\u0000DE\u0005\u0006\u0000\u0000E\u0087\u0006" +
                    "\u0000\uffff\uffff\u0000FG\u0005\u0006\u0000\u0000GH\u0005\u0003\u0000" +
                    "\u0000HI\u0005\u0002\u0000\u0000IJ\u0005\u0006\u0000\u0000J\u0087\u0006" +
                    "\u0000\uffff\uffff\u0000KL\u0005\u0005\u0000\u0000LM\u0005\u0002\u0000" +
                    "\u0000MN\u0005\u0006\u0000\u0000N\u0087\u0006\u0000\uffff\uffff\u0000" +
                    "OP\u0005\u0004\u0000\u0000PQ\u0005\u0006\u0000\u0000QR\u0005\u0003\u0000" +
                    "\u0000RS\u0005\u0006\u0000\u0000ST\u0005\u0001\u0000\u0000TU\u0005\u0006" +
                    "\u0000\u0000U\u0087\u0006\u0000\uffff\uffff\u0000VW\u0005\u0006\u0000" +
                    "\u0000WX\u0005\u0003\u0000\u0000XY\u0005\u0006\u0000\u0000YZ\u0005\u0001" +
                    "\u0000\u0000Z[\u0005\u0006\u0000\u0000[\u0087\u0006\u0000\uffff\uffff" +
                    "\u0000\\]\u0005\u0004\u0000\u0000]^\u0005\u0006\u0000\u0000^_\u0005\u0003" +
                    "\u0000\u0000_`\u0005\u0001\u0000\u0000`a\u0005\u0006\u0000\u0000a\u0087" +
                    "\u0006\u0000\uffff\uffff\u0000bc\u0005\u0006\u0000\u0000cd\u0005\u0003" +
                    "\u0000\u0000de\u0005\u0001\u0000\u0000ef\u0005\u0006\u0000\u0000f\u0087" +
                    "\u0006\u0000\uffff\uffff\u0000gh\u0005\u0006\u0000\u0000hi\u0005\u0001" +
                    "\u0000\u0000ij\u0005\u0006\u0000\u0000j\u0087\u0006\u0000\uffff\uffff" +
                    "\u0000kl\u0005\u0001\u0000\u0000lm\u0005\u0006\u0000\u0000m\u0087\u0006" +
                    "\u0000\uffff\uffff\u0000no\u0005\u0005\u0000\u0000o\u0087\u0006\u0000" +
                    "\uffff\uffff\u0000pq\u0005\u0006\u0000\u0000q\u0087\u0006\u0000\uffff" +
                    "\uffff\u0000rs\u0005\u0004\u0000\u0000st\u0005\u0006\u0000\u0000t\u0087" +
                    "\u0006\u0000\uffff\uffff\u0000uv\u0005\u0006\u0000\u0000vw\u0005\u0001" +
                    "\u0000\u0000wx\u0005\u0005\u0000\u0000x\u0087\u0006\u0000\uffff\uffff" +
                    "\u0000yz\u0005\u0006\u0000\u0000z{\u0005\u0003\u0000\u0000{|\u0005\u0002" +
                    "\u0000\u0000|}\u0005\u0006\u0000\u0000}\u0087\u0006\u0000\uffff\uffff" +
                    "\u0000~\u007f\u0005\u0004\u0000\u0000\u007f\u0080\u0005\u0006\u0000\u0000" +
                    "\u0080\u0081\u0005\u0001\u0000\u0000\u0081\u0082\u0005\u0006\u0000\u0000" +
                    "\u0082\u0087\u0006\u0000\uffff\uffff\u0000\u0083\u0084\u0005\u0002\u0000" +
                    "\u0000\u0084\u0085\u0005\u0006\u0000\u0000\u0085\u0087\u0006\u0000\uffff" +
                    "\uffff\u0000\u0086\u0002\u0001\u0000\u0000\u0000\u0086\n\u0001\u0000\u0000" +
                    "\u0000\u0086\u0010\u0001\u0000\u0000\u0000\u0086\u0019\u0001\u0000\u0000" +
                    "\u0000\u0086!\u0001\u0000\u0000\u0000\u0086)\u0001\u0000\u0000\u0000\u0086" +
                    "0\u0001\u0000\u0000\u0000\u00867\u0001\u0000\u0000\u0000\u0086=\u0001" +
                    "\u0000\u0000\u0000\u0086B\u0001\u0000\u0000\u0000\u0086F\u0001\u0000\u0000" +
                    "\u0000\u0086K\u0001\u0000\u0000\u0000\u0086O\u0001\u0000\u0000\u0000\u0086" +
                    "V\u0001\u0000\u0000\u0000\u0086\\\u0001\u0000\u0000\u0000\u0086b\u0001" +
                    "\u0000\u0000\u0000\u0086g\u0001\u0000\u0000\u0000\u0086k\u0001\u0000\u0000" +
                    "\u0000\u0086n\u0001\u0000\u0000\u0000\u0086p\u0001\u0000\u0000\u0000\u0086" +
                    "r\u0001\u0000\u0000\u0000\u0086u\u0001\u0000\u0000\u0000\u0086y\u0001" +
                    "\u0000\u0000\u0000\u0086~\u0001\u0000\u0000\u0000\u0086\u0083\u0001\u0000" +
                    "\u0000\u0000\u0087\u0088\u0001\u0000\u0000\u0000\u0088\u0089\u0005\u0000" +
                    "\u0000\u0001\u0089\u0001\u0001\u0000\u0000\u0000\u0001\u0086";
	public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}