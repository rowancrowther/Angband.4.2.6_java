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
            D = 1, M = 2, PLUS = 3, MINUS = 4, NUMBER = 5;
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
                null, "'d'", "'M'", "'+'", "'-'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "D", "M", "PLUS", "MINUS", "NUMBER"
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

        public TerminalNode MINUS() {
            return getToken(RandomParser.MINUS, 0);
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

        int baseInit = 0;
        int diceInit = 0;
        int sidesInit = 1;
        int mBonusInit = 0;

        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(82);
                _errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(_input, 0, _ctx)) {
                    case 1: {
                        setState(2);
                        match(MINUS);
                        setState(3);
                        ((DiceContext) _localctx).base = match(NUMBER);
                        setState(4);
                        match(PLUS);
                        setState(5);
                        ((DiceContext) _localctx).diceNum = match(NUMBER);
                        setState(6);
                        match(D);
                        setState(7);
                        ((DiceContext) _localctx).sides = match(NUMBER);
                        setState(8);
                        match(M);
                        setState(9);
                        ((DiceContext) _localctx).m_bonus = match(NUMBER);

                        baseInit = -1 * Integer.parseInt(((DiceContext) _localctx).base.getText());
                        diceInit = Integer.parseInt(((DiceContext) _localctx).diceNum.getText());
                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());
                        mBonusInit = Integer.parseInt(((DiceContext) _localctx).m_bonus.getText());

                    }
                    break;
                    case 2: {
                        setState(11);
                        ((DiceContext) _localctx).base = match(NUMBER);
                        setState(12);
                        match(PLUS);
                        setState(13);
                        ((DiceContext) _localctx).diceNum = match(NUMBER);
                        setState(14);
                        match(D);
                        setState(15);
                        ((DiceContext) _localctx).sides = match(NUMBER);
                        setState(16);
                        match(M);
                        setState(17);
                        ((DiceContext) _localctx).m_bonus = match(NUMBER);

                        baseInit = Integer.parseInt(((DiceContext) _localctx).base.getText());
                        diceInit = Integer.parseInt(((DiceContext) _localctx).diceNum.getText());
                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());
                        mBonusInit = Integer.parseInt(((DiceContext) _localctx).m_bonus.getText());

                    }
                    break;
                    case 3: {
                        setState(19);
                        match(MINUS);
                        setState(20);
                        ((DiceContext) _localctx).base = match(NUMBER);
                        setState(21);
                        match(PLUS);
                        setState(22);
                        match(D);
                        setState(23);
                        ((DiceContext) _localctx).sides = match(NUMBER);
                        setState(24);
                        match(M);
                        setState(25);
                        ((DiceContext) _localctx).m_bonus = match(NUMBER);

                        baseInit = -1 * Integer.parseInt(((DiceContext) _localctx).base.getText());
                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());
                        mBonusInit = Integer.parseInt(((DiceContext) _localctx).m_bonus.getText());

                    }
                    break;
                    case 4: {
                        setState(27);
                        ((DiceContext) _localctx).base = match(NUMBER);
                        setState(28);
                        match(PLUS);
                        setState(29);
                        match(D);
                        setState(30);
                        ((DiceContext) _localctx).sides = match(NUMBER);
                        setState(31);
                        match(M);
                        setState(32);
                        ((DiceContext) _localctx).m_bonus = match(NUMBER);

                        baseInit = Integer.parseInt(((DiceContext) _localctx).base.getText());
                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());
                        mBonusInit = Integer.parseInt(((DiceContext) _localctx).m_bonus.getText());

                    }
                    break;
                    case 5: {
                        setState(34);
                        ((DiceContext) _localctx).diceNum = match(NUMBER);
                        setState(35);
                        match(D);
                        setState(36);
                        ((DiceContext) _localctx).sides = match(NUMBER);
                        setState(37);
                        match(M);
                        setState(38);
                        ((DiceContext) _localctx).m_bonus = match(NUMBER);

                        diceInit = Integer.parseInt(((DiceContext) _localctx).diceNum.getText());
                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());
                        mBonusInit = Integer.parseInt(((DiceContext) _localctx).m_bonus.getText());

                    }
                    break;
                    case 6: {
                        setState(40);
                        match(D);
                        setState(41);
                        ((DiceContext) _localctx).sides = match(NUMBER);
                        setState(42);
                        match(M);
                        setState(43);
                        ((DiceContext) _localctx).m_bonus = match(NUMBER);

                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());
                        mBonusInit = Integer.parseInt(((DiceContext) _localctx).m_bonus.getText());

                    }
                    break;
                    case 7: {
                        setState(45);
                        ((DiceContext) _localctx).base = match(NUMBER);
                        setState(46);
                        match(M);
                        setState(47);
                        ((DiceContext) _localctx).m_bonus = match(NUMBER);

                        baseInit = Integer.parseInt(((DiceContext) _localctx).base.getText());
                        mBonusInit = Integer.parseInt(((DiceContext) _localctx).m_bonus.getText());

                    }
                    break;
                    case 8: {
                        setState(49);
                        match(MINUS);
                        setState(50);
                        ((DiceContext) _localctx).base = match(NUMBER);
                        setState(51);
                        match(PLUS);
                        setState(52);
                        ((DiceContext) _localctx).diceNum = match(NUMBER);
                        setState(53);
                        match(D);
                        setState(54);
                        ((DiceContext) _localctx).sides = match(NUMBER);

                        baseInit = -1 * Integer.parseInt(((DiceContext) _localctx).base.getText());
                        diceInit = Integer.parseInt(((DiceContext) _localctx).diceNum.getText());
                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());

                    }
                    break;
                    case 9: {
                        setState(56);
                        ((DiceContext) _localctx).base = match(NUMBER);
                        setState(57);
                        match(PLUS);
                        setState(58);
                        ((DiceContext) _localctx).diceNum = match(NUMBER);
                        setState(59);
                        match(D);
                        setState(60);
                        ((DiceContext) _localctx).sides = match(NUMBER);

                        baseInit = Integer.parseInt(((DiceContext) _localctx).base.getText());
                        diceInit = Integer.parseInt(((DiceContext) _localctx).diceNum.getText());
                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());

                    }
                    break;
                    case 10: {
                        setState(62);
                        match(MINUS);
                        setState(63);
                        ((DiceContext) _localctx).base = match(NUMBER);
                        setState(64);
                        match(PLUS);
                        setState(65);
                        match(D);
                        setState(66);
                        ((DiceContext) _localctx).sides = match(NUMBER);

                        baseInit = -1 * Integer.parseInt(((DiceContext) _localctx).base.getText());
                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());

                    }
                    break;
                    case 11: {
                        setState(68);
                        ((DiceContext) _localctx).base = match(NUMBER);
                        setState(69);
                        match(PLUS);
                        setState(70);
                        match(D);
                        setState(71);
                        ((DiceContext) _localctx).sides = match(NUMBER);

                        baseInit = Integer.parseInt(((DiceContext) _localctx).base.getText());
                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());

                    }
                    break;
                    case 12: {
                        setState(73);
                        ((DiceContext) _localctx).diceNum = match(NUMBER);
                        setState(74);
                        match(D);
                        setState(75);
                        ((DiceContext) _localctx).sides = match(NUMBER);

                        diceInit = Integer.parseInt(((DiceContext) _localctx).diceNum.getText());
                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());

                    }
                    break;
                    case 13: {
                        setState(77);
                        match(D);
                        setState(78);
                        ((DiceContext) _localctx).sides = match(NUMBER);

                        sidesInit = Integer.parseInt(((DiceContext) _localctx).sides.getText());

                    }
                    break;
                    case 14: {
                        setState(80);
                        ((DiceContext) _localctx).base = match(NUMBER);

                        baseInit = Integer.parseInt(((DiceContext) _localctx).base.getText());

                    }
                    break;
                }
                setState(84);
                match(EOF);
            }
            _ctx.stop = _input.LT(-1);

            if (sidesInit < 1) sidesInit = 1;

            ((DiceContext) _localctx).random = new Random(baseInit, mBonusInit, diceInit, sidesInit, false);

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
            "\u0004\u0001\u0005W\u0002\u0000\u0007\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0003" +
                    "\u0000S\b\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0000\u0000\u0001" +
                    "\u0000\u0000\u0000b\u0000R\u0001\u0000\u0000\u0000\u0002\u0003\u0005\u0004" +
                    "\u0000\u0000\u0003\u0004\u0005\u0005\u0000\u0000\u0004\u0005\u0005\u0003" +
                    "\u0000\u0000\u0005\u0006\u0005\u0005\u0000\u0000\u0006\u0007\u0005\u0001" +
                    "\u0000\u0000\u0007\b\u0005\u0005\u0000\u0000\b\t\u0005\u0002\u0000\u0000" +
                    "\t\n\u0005\u0005\u0000\u0000\nS\u0006\u0000\uffff\uffff\u0000\u000b\f" +
                    "\u0005\u0005\u0000\u0000\f\r\u0005\u0003\u0000\u0000\r\u000e\u0005\u0005" +
                    "\u0000\u0000\u000e\u000f\u0005\u0001\u0000\u0000\u000f\u0010\u0005\u0005" +
                    "\u0000\u0000\u0010\u0011\u0005\u0002\u0000\u0000\u0011\u0012\u0005\u0005" +
                    "\u0000\u0000\u0012S\u0006\u0000\uffff\uffff\u0000\u0013\u0014\u0005\u0004" +
                    "\u0000\u0000\u0014\u0015\u0005\u0005\u0000\u0000\u0015\u0016\u0005\u0003" +
                    "\u0000\u0000\u0016\u0017\u0005\u0001\u0000\u0000\u0017\u0018\u0005\u0005" +
                    "\u0000\u0000\u0018\u0019\u0005\u0002\u0000\u0000\u0019\u001a\u0005\u0005" +
                    "\u0000\u0000\u001aS\u0006\u0000\uffff\uffff\u0000\u001b\u001c\u0005\u0005" +
                    "\u0000\u0000\u001c\u001d\u0005\u0003\u0000\u0000\u001d\u001e\u0005\u0001" +
                    "\u0000\u0000\u001e\u001f\u0005\u0005\u0000\u0000\u001f \u0005\u0002\u0000" +
                    "\u0000 !\u0005\u0005\u0000\u0000!S\u0006\u0000\uffff\uffff\u0000\"#\u0005" +
                    "\u0005\u0000\u0000#$\u0005\u0001\u0000\u0000$%\u0005\u0005\u0000\u0000" +
                    "%&\u0005\u0002\u0000\u0000&\'\u0005\u0005\u0000\u0000\'S\u0006\u0000\uffff" +
                    "\uffff\u0000()\u0005\u0001\u0000\u0000)*\u0005\u0005\u0000\u0000*+\u0005" +
                    "\u0002\u0000\u0000+,\u0005\u0005\u0000\u0000,S\u0006\u0000\uffff\uffff" +
                    "\u0000-.\u0005\u0005\u0000\u0000./\u0005\u0002\u0000\u0000/0\u0005\u0005" +
                    "\u0000\u00000S\u0006\u0000\uffff\uffff\u000012\u0005\u0004\u0000\u0000" +
                    "23\u0005\u0005\u0000\u000034\u0005\u0003\u0000\u000045\u0005\u0005\u0000" +
                    "\u000056\u0005\u0001\u0000\u000067\u0005\u0005\u0000\u00007S\u0006\u0000" +
                    "\uffff\uffff\u000089\u0005\u0005\u0000\u00009:\u0005\u0003\u0000\u0000" +
                    ":;\u0005\u0005\u0000\u0000;<\u0005\u0001\u0000\u0000<=\u0005\u0005\u0000" +
                    "\u0000=S\u0006\u0000\uffff\uffff\u0000>?\u0005\u0004\u0000\u0000?@\u0005" +
                    "\u0005\u0000\u0000@A\u0005\u0003\u0000\u0000AB\u0005\u0001\u0000\u0000" +
                    "BC\u0005\u0005\u0000\u0000CS\u0006\u0000\uffff\uffff\u0000DE\u0005\u0005" +
                    "\u0000\u0000EF\u0005\u0003\u0000\u0000FG\u0005\u0001\u0000\u0000GH\u0005" +
                    "\u0005\u0000\u0000HS\u0006\u0000\uffff\uffff\u0000IJ\u0005\u0005\u0000" +
                    "\u0000JK\u0005\u0001\u0000\u0000KL\u0005\u0005\u0000\u0000LS\u0006\u0000" +
                    "\uffff\uffff\u0000MN\u0005\u0001\u0000\u0000NO\u0005\u0005\u0000\u0000" +
                    "OS\u0006\u0000\uffff\uffff\u0000PQ\u0005\u0005\u0000\u0000QS\u0006\u0000" +
                    "\uffff\uffff\u0000R\u0002\u0001\u0000\u0000\u0000R\u000b\u0001\u0000\u0000" +
                    "\u0000R\u0013\u0001\u0000\u0000\u0000R\u001b\u0001\u0000\u0000\u0000R" +
                    "\"\u0001\u0000\u0000\u0000R(\u0001\u0000\u0000\u0000R-\u0001\u0000\u0000" +
                    "\u0000R1\u0001\u0000\u0000\u0000R8\u0001\u0000\u0000\u0000R>\u0001\u0000" +
                    "\u0000\u0000RD\u0001\u0000\u0000\u0000RI\u0001\u0000\u0000\u0000RM\u0001" +
                    "\u0000\u0000\u0000RP\u0001\u0000\u0000\u0000ST\u0001\u0000\u0000\u0000" +
                    "TU\u0005\u0000\u0000\u0001U\u0001\u0001\u0000\u0000\u0000\u0001R";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}