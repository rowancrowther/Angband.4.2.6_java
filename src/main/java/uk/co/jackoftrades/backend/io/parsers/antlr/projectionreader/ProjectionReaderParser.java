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
 *  Java code copyright (c) 2026 Rowan Crowther, Jack of Trades Ltd.
 */

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/background/io/parsers/antlr/grammars/ProjectionReader.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.projectionreader;

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
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
import uk.co.jackoftrades.middle.combat.enums.ProjectionType;
import uk.co.jackoftrades.middle.enums.MessageEnum;
import uk.co.jackoftrades.middle.game.Projection;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ProjectionReaderParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            T__0 = 1, T__1 = 2, T__2 = 3, T__3 = 4, T__4 = 5, COMMENT = 6, EOL = 7, CODE = 8, NAME = 9,
            TYPE = 10, DESC = 11, PLAYER_DESC = 12, BLIND_DESC = 13, LASH_DESC = 14, NUMERATOR = 15,
            DENOMINATOR = 16, DIVISOR = 17, DAMAGE_CAP = 18, MSGT = 19, OBVIOUS = 20, WAKE = 21,
            COLOUR = 22, UCASE = 23, LWORDS = 24, LCASE = 25, TCASE = 26, NUMBER = 27;
    public static final int
            RULE_code = 0, RULE_name = 1, RULE_type = 2, RULE_desc = 3, RULE_playerDesc = 4,
            RULE_blindDesc = 5, RULE_lashDesc = 6, RULE_numerator = 7, RULE_dice = 8,
            RULE_denominator = 9, RULE_divisor = 10, RULE_damageCap = 11, RULE_msgt = 12,
            RULE_obvious = 13, RULE_wake = 14, RULE_colour = 15, RULE_projection = 16,
            RULE_file = 17;

    private static String[] makeRuleNames() {
        return new String[]{
                "code", "name", "type", "desc", "playerDesc", "blindDesc", "lashDesc",
                "numerator", "dice", "denominator", "divisor", "damageCap", "msgt", "obvious",
                "wake", "colour", "projection", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'element'", "'environs'", "'monster'", "'+'", "'d'", null, null,
                "'code:'", "'name:'", "'type:'", "'desc:'", "'player-desc:'", "'blind-desc:'",
                "'lash-desc:'", "'numerator:'", "'denominator:'", "'divisor:'", "'damage-cap:'",
                "'msgt:'", "'obvious:'", "'wake:'", "'color:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, null, null, null, null, null, "COMMENT", "EOL", "CODE", "NAME",
                "TYPE", "DESC", "PLAYER_DESC", "BLIND_DESC", "LASH_DESC", "NUMERATOR",
                "DENOMINATOR", "DIVISOR", "DAMAGE_CAP", "MSGT", "OBVIOUS", "WAKE", "COLOUR",
                "UCASE", "LWORDS", "LCASE", "TCASE", "NUMBER"
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
        return "ProjectionReader.g4";
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

    public ProjectionReaderParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class CodeContext extends ParserRuleContext {
        public ProjectionEnum projectionCode;
        public Token UCASE;

        public TerminalNode CODE() {
            return getToken(ProjectionReaderParser.CODE, 0);
        }

        public TerminalNode UCASE() {
            return getToken(ProjectionReaderParser.UCASE, 0);
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
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).enterCode(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).exitCode(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ProjectionReaderVisitor)
                return ((ProjectionReaderVisitor<? extends T>) visitor).visitCode(this);
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
                ((CodeContext) _localctx).UCASE = match(UCASE);

                String projectionEnumString = "PROJ_" + ((CodeContext) _localctx).UCASE.getText();
                ((CodeContext) _localctx).projectionCode = ProjectionEnum.valueOf(projectionEnumString);

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
        public String projectionName;
        public Token LWORDS;

        public TerminalNode NAME() {
            return getToken(ProjectionReaderParser.NAME, 0);
        }

        public TerminalNode LWORDS() {
            return getToken(ProjectionReaderParser.LWORDS, 0);
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
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ProjectionReaderVisitor)
                return ((ProjectionReaderVisitor<? extends T>) visitor).visitName(this);
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
                ((NameContext) _localctx).LWORDS = match(LWORDS);

                ((NameContext) _localctx).projectionName = ((NameContext) _localctx).LWORDS.getText();

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
        public ProjectionType projectionType;
        public Token res;

        public TerminalNode TYPE() {
            return getToken(ProjectionReaderParser.TYPE, 0);
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
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).enterType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).exitType(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ProjectionReaderVisitor)
                return ((ProjectionReaderVisitor<? extends T>) visitor).visitType(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TypeContext type() throws RecognitionException {
        TypeContext _localctx = new TypeContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_type);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(44);
                match(TYPE);
                setState(45);
                ((TypeContext) _localctx).res = _input.LT(1);
                _la = _input.LA(1);
                if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & 14L) != 0))) {
                    ((TypeContext) _localctx).res = (Token) _errHandler.recoverInline(this);
                } else {
                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
                    _errHandler.reportMatch(this);
                    consume();
                }

                String projectionTypeString = "PT_" + ((TypeContext) _localctx).res.getText().toUpperCase();
                ((TypeContext) _localctx).projectionType = ProjectionType.valueOf(projectionTypeString);

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
        public Token LWORDS;

        public TerminalNode DESC() {
            return getToken(ProjectionReaderParser.DESC, 0);
        }

        public TerminalNode LWORDS() {
            return getToken(ProjectionReaderParser.LWORDS, 0);
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
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).enterDesc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).exitDesc(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ProjectionReaderVisitor)
                return ((ProjectionReaderVisitor<? extends T>) visitor).visitDesc(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DescContext desc() throws RecognitionException {
        DescContext _localctx = new DescContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_desc);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(48);
                match(DESC);
                setState(49);
                ((DescContext) _localctx).LWORDS = match(LWORDS);

                ((DescContext) _localctx).description = ((DescContext) _localctx).LWORDS.getText();

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
    public static class PlayerDescContext extends ParserRuleContext {
        public String playerDescription;
        public Token LWORDS;

        public TerminalNode PLAYER_DESC() {
            return getToken(ProjectionReaderParser.PLAYER_DESC, 0);
        }

        public TerminalNode LWORDS() {
            return getToken(ProjectionReaderParser.LWORDS, 0);
        }

        public PlayerDescContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_playerDesc;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener)
                ((ProjectionReaderListener) listener).enterPlayerDesc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener)
                ((ProjectionReaderListener) listener).exitPlayerDesc(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ProjectionReaderVisitor)
                return ((ProjectionReaderVisitor<? extends T>) visitor).visitPlayerDesc(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PlayerDescContext playerDesc() throws RecognitionException {
        PlayerDescContext _localctx = new PlayerDescContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_playerDesc);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(52);
                match(PLAYER_DESC);
                setState(53);
                ((PlayerDescContext) _localctx).LWORDS = match(LWORDS);

                ((PlayerDescContext) _localctx).playerDescription = ((PlayerDescContext) _localctx).LWORDS.getText();

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
    public static class BlindDescContext extends ParserRuleContext {
        public String blindDescription;
        public Token LWORDS;

        public TerminalNode BLIND_DESC() {
            return getToken(ProjectionReaderParser.BLIND_DESC, 0);
        }

        public TerminalNode LWORDS() {
            return getToken(ProjectionReaderParser.LWORDS, 0);
        }

        public BlindDescContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_blindDesc;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener)
                ((ProjectionReaderListener) listener).enterBlindDesc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).exitBlindDesc(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ProjectionReaderVisitor)
                return ((ProjectionReaderVisitor<? extends T>) visitor).visitBlindDesc(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BlindDescContext blindDesc() throws RecognitionException {
        BlindDescContext _localctx = new BlindDescContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_blindDesc);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(56);
                match(BLIND_DESC);
                setState(57);
                ((BlindDescContext) _localctx).LWORDS = match(LWORDS);

                ((BlindDescContext) _localctx).blindDescription = ((BlindDescContext) _localctx).LWORDS.getText();

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
    public static class LashDescContext extends ParserRuleContext {
        public String lashDescription;
        public Token LWORDS;

        public TerminalNode LASH_DESC() {
            return getToken(ProjectionReaderParser.LASH_DESC, 0);
        }

        public TerminalNode LWORDS() {
            return getToken(ProjectionReaderParser.LWORDS, 0);
        }

        public LashDescContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_lashDesc;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).enterLashDesc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).exitLashDesc(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ProjectionReaderVisitor)
                return ((ProjectionReaderVisitor<? extends T>) visitor).visitLashDesc(this);
            else return visitor.visitChildren(this);
        }
    }

    public final LashDescContext lashDesc() throws RecognitionException {
        LashDescContext _localctx = new LashDescContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_lashDesc);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(60);
                match(LASH_DESC);
                setState(61);
                ((LashDescContext) _localctx).LWORDS = match(LWORDS);

                ((LashDescContext) _localctx).lashDescription = ((LashDescContext) _localctx).LWORDS.getText();

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
    public static class NumeratorContext extends ParserRuleContext {
        public int num;
        public Token NUMBER;

        public TerminalNode NUMERATOR() {
            return getToken(ProjectionReaderParser.NUMERATOR, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(ProjectionReaderParser.NUMBER, 0);
        }

        public NumeratorContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_numerator;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener)
                ((ProjectionReaderListener) listener).enterNumerator(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).exitNumerator(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ProjectionReaderVisitor)
                return ((ProjectionReaderVisitor<? extends T>) visitor).visitNumerator(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NumeratorContext numerator() throws RecognitionException {
        NumeratorContext _localctx = new NumeratorContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_numerator);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(64);
                match(NUMERATOR);
                setState(65);
                ((NumeratorContext) _localctx).NUMBER = match(NUMBER);

                ((NumeratorContext) _localctx).num = Integer.parseInt(((NumeratorContext) _localctx).NUMBER.getText());

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
        public Random die;
        public Token base;
        public Token diceType;
        public Token sides;

        public List<TerminalNode> NUMBER() {
            return getTokens(ProjectionReaderParser.NUMBER);
        }

        public TerminalNode NUMBER(int i) {
            return getToken(ProjectionReaderParser.NUMBER, i);
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
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).enterDice(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).exitDice(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ProjectionReaderVisitor)
                return ((ProjectionReaderVisitor<? extends T>) visitor).visitDice(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DiceContext dice() throws RecognitionException {
        DiceContext _localctx = new DiceContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_dice);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(68);
                ((DiceContext) _localctx).base = match(NUMBER);
                setState(69);
                match(T__3);
                setState(70);
                ((DiceContext) _localctx).diceType = match(NUMBER);
                setState(71);
                match(T__4);
                setState(72);
                ((DiceContext) _localctx).sides = match(NUMBER);

                ((DiceContext) _localctx).die = new Random(
                        Integer.parseInt(((DiceContext) _localctx).base.getText()), 1,
                        Integer.parseInt(((DiceContext) _localctx).diceType.getText()),
                        Integer.parseInt(((DiceContext) _localctx).sides.getText()), false);

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
    public static class DenominatorContext extends ParserRuleContext {
        public int den;
        public Random denDice;
        public boolean isDice;
        public Token NUMBER;
        public DiceContext dice;

        public TerminalNode DENOMINATOR() {
            return getToken(ProjectionReaderParser.DENOMINATOR, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(ProjectionReaderParser.NUMBER, 0);
        }

        public DiceContext dice() {
            return getRuleContext(DiceContext.class, 0);
        }

        public DenominatorContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_denominator;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener)
                ((ProjectionReaderListener) listener).enterDenominator(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener)
                ((ProjectionReaderListener) listener).exitDenominator(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ProjectionReaderVisitor)
                return ((ProjectionReaderVisitor<? extends T>) visitor).visitDenominator(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DenominatorContext denominator() throws RecognitionException {
        DenominatorContext _localctx = new DenominatorContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_denominator);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(75);
                match(DENOMINATOR);
                setState(78);
                _errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(_input, 0, _ctx)) {
                    case 1: {
                        setState(76);
                        ((DenominatorContext) _localctx).NUMBER = match(NUMBER);
                    }
                    break;
                    case 2: {
                        setState(77);
                        ((DenominatorContext) _localctx).dice = dice();
                    }
                    break;
                }

                if (((DenominatorContext) _localctx).NUMBER == null) {
                    ((DenominatorContext) _localctx).denDice = ((DenominatorContext) _localctx).dice.die;
                    ((DenominatorContext) _localctx).isDice = true;
                    ((DenominatorContext) _localctx).den = -1;
                } else {
                    ((DenominatorContext) _localctx).den = Integer.parseInt(((DenominatorContext) _localctx).NUMBER.getText());
                    ((DenominatorContext) _localctx).isDice = false;
                    ((DenominatorContext) _localctx).denDice = null;
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
    public static class DivisorContext extends ParserRuleContext {
        public int div;
        public Token NUMBER;

        public TerminalNode DIVISOR() {
            return getToken(ProjectionReaderParser.DIVISOR, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(ProjectionReaderParser.NUMBER, 0);
        }

        public DivisorContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_divisor;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).enterDivisor(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).exitDivisor(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ProjectionReaderVisitor)
                return ((ProjectionReaderVisitor<? extends T>) visitor).visitDivisor(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DivisorContext divisor() throws RecognitionException {
        DivisorContext _localctx = new DivisorContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_divisor);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(82);
                match(DIVISOR);
                setState(83);
                ((DivisorContext) _localctx).NUMBER = match(NUMBER);

                ((DivisorContext) _localctx).div = Integer.parseInt(((DivisorContext) _localctx).NUMBER.getText());

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
    public static class DamageCapContext extends ParserRuleContext {
        public int damCap;
        public Token NUMBER;

        public TerminalNode DAMAGE_CAP() {
            return getToken(ProjectionReaderParser.DAMAGE_CAP, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(ProjectionReaderParser.NUMBER, 0);
        }

        public DamageCapContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_damageCap;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener)
                ((ProjectionReaderListener) listener).enterDamageCap(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).exitDamageCap(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ProjectionReaderVisitor)
                return ((ProjectionReaderVisitor<? extends T>) visitor).visitDamageCap(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DamageCapContext damageCap() throws RecognitionException {
        DamageCapContext _localctx = new DamageCapContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_damageCap);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(86);
                match(DAMAGE_CAP);
                setState(87);
                ((DamageCapContext) _localctx).NUMBER = match(NUMBER);

                ((DamageCapContext) _localctx).damCap = Integer.parseInt(((DamageCapContext) _localctx).NUMBER.getText());

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
    public static class MsgtContext extends ParserRuleContext {
        public MessageEnum sound;
        public Token UCASE;

        public TerminalNode MSGT() {
            return getToken(ProjectionReaderParser.MSGT, 0);
        }

        public TerminalNode UCASE() {
            return getToken(ProjectionReaderParser.UCASE, 0);
        }

        public MsgtContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_msgt;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).enterMsgt(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).exitMsgt(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ProjectionReaderVisitor)
                return ((ProjectionReaderVisitor<? extends T>) visitor).visitMsgt(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MsgtContext msgt() throws RecognitionException {
        MsgtContext _localctx = new MsgtContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_msgt);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(90);
                match(MSGT);
                setState(91);
                ((MsgtContext) _localctx).UCASE = match(UCASE);

                String enumTag = "MSG_" + ((MsgtContext) _localctx).UCASE.getText();
                ((MsgtContext) _localctx).sound = MessageEnum.valueOf(enumTag);

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
    public static class ObviousContext extends ParserRuleContext {
        public boolean isObvious;
        public Token NUMBER;

        public TerminalNode OBVIOUS() {
            return getToken(ProjectionReaderParser.OBVIOUS, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(ProjectionReaderParser.NUMBER, 0);
        }

        public ObviousContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_obvious;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).enterObvious(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).exitObvious(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ProjectionReaderVisitor)
                return ((ProjectionReaderVisitor<? extends T>) visitor).visitObvious(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ObviousContext obvious() throws RecognitionException {
        ObviousContext _localctx = new ObviousContext(_ctx, getState());
        enterRule(_localctx, 26, RULE_obvious);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(94);
                match(OBVIOUS);
                setState(95);
                ((ObviousContext) _localctx).NUMBER = match(NUMBER);

                int number = Integer.parseInt(((ObviousContext) _localctx).NUMBER.getText());
                ((ObviousContext) _localctx).isObvious = (number == 1);

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
    public static class WakeContext extends ParserRuleContext {
        public boolean willWake;
        public Token NUMBER;

        public TerminalNode WAKE() {
            return getToken(ProjectionReaderParser.WAKE, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(ProjectionReaderParser.NUMBER, 0);
        }

        public WakeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_wake;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).enterWake(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).exitWake(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ProjectionReaderVisitor)
                return ((ProjectionReaderVisitor<? extends T>) visitor).visitWake(this);
            else return visitor.visitChildren(this);
        }
    }

    public final WakeContext wake() throws RecognitionException {
        WakeContext _localctx = new WakeContext(_ctx, getState());
        enterRule(_localctx, 28, RULE_wake);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(98);
                match(WAKE);
                setState(99);
                ((WakeContext) _localctx).NUMBER = match(NUMBER);

                int number = Integer.parseInt(((WakeContext) _localctx).NUMBER.getText());
                ((WakeContext) _localctx).willWake = (number == 1);

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
    public static class ColourContext extends ParserRuleContext {
        public ColourType col;
        public Token tc;
        public Token uc;

        public TerminalNode COLOUR() {
            return getToken(ProjectionReaderParser.COLOUR, 0);
        }

        public TerminalNode TCASE() {
            return getToken(ProjectionReaderParser.TCASE, 0);
        }

        public TerminalNode UCASE() {
            return getToken(ProjectionReaderParser.UCASE, 0);
        }

        public ColourContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_colour;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).enterColour(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).exitColour(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ProjectionReaderVisitor)
                return ((ProjectionReaderVisitor<? extends T>) visitor).visitColour(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ColourContext colour() throws RecognitionException {
        ColourContext _localctx = new ColourContext(_ctx, getState());
        enterRule(_localctx, 30, RULE_colour);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(102);
                match(COLOUR);
                setState(105);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case TCASE: {
                        setState(103);
                        ((ColourContext) _localctx).tc = match(TCASE);
                    }
                    break;
                    case UCASE: {
                        setState(104);
                        ((ColourContext) _localctx).uc = match(UCASE);
                    }
                    break;
                    default:
                        throw new NoViableAltException(this);
                }

                String colourName;

                if (((ColourContext) _localctx).tc == null) {
                    colourName = ((ColourContext) _localctx).uc.getText().toLowerCase();
                } else {
                    colourName = ((ColourContext) _localctx).tc.getText().toLowerCase();
                }

                ((ColourContext) _localctx).col = ColourType.findColourType(colourName);

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
    public static class ProjectionContext extends ParserRuleContext {
        public Projection proj;
        public CodeContext code;
        public NameContext name;
        public TypeContext type;
        public DescContext desc;
        public PlayerDescContext playerDesc;
        public BlindDescContext blindDesc;
        public LashDescContext lashDesc;
        public NumeratorContext numerator;
        public DenominatorContext denominator;
        public DivisorContext divisor;
        public DamageCapContext damageCap;
        public MsgtContext msgt;
        public ObviousContext obvious;
        public WakeContext wake;
        public ColourContext colour;

        public CodeContext code() {
            return getRuleContext(CodeContext.class, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public DescContext desc() {
            return getRuleContext(DescContext.class, 0);
        }

        public BlindDescContext blindDesc() {
            return getRuleContext(BlindDescContext.class, 0);
        }

        public ObviousContext obvious() {
            return getRuleContext(ObviousContext.class, 0);
        }

        public ColourContext colour() {
            return getRuleContext(ColourContext.class, 0);
        }

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public PlayerDescContext playerDesc() {
            return getRuleContext(PlayerDescContext.class, 0);
        }

        public LashDescContext lashDesc() {
            return getRuleContext(LashDescContext.class, 0);
        }

        public NumeratorContext numerator() {
            return getRuleContext(NumeratorContext.class, 0);
        }

        public DenominatorContext denominator() {
            return getRuleContext(DenominatorContext.class, 0);
        }

        public DivisorContext divisor() {
            return getRuleContext(DivisorContext.class, 0);
        }

        public DamageCapContext damageCap() {
            return getRuleContext(DamageCapContext.class, 0);
        }

        public MsgtContext msgt() {
            return getRuleContext(MsgtContext.class, 0);
        }

        public WakeContext wake() {
            return getRuleContext(WakeContext.class, 0);
        }

        public ProjectionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_projection;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener)
                ((ProjectionReaderListener) listener).enterProjection(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener)
                ((ProjectionReaderListener) listener).exitProjection(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ProjectionReaderVisitor)
                return ((ProjectionReaderVisitor<? extends T>) visitor).visitProjection(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ProjectionContext projection() throws RecognitionException {
        ProjectionContext _localctx = new ProjectionContext(_ctx, getState());
        enterRule(_localctx, 32, RULE_projection);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(109);
                ((ProjectionContext) _localctx).code = code();
                setState(111);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == NAME) {
                    {
                        setState(110);
                        ((ProjectionContext) _localctx).name = name();
                    }
                }

                setState(113);
                ((ProjectionContext) _localctx).type = type();
                setState(114);
                ((ProjectionContext) _localctx).desc = desc();
                setState(116);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == PLAYER_DESC) {
                    {
                        setState(115);
                        ((ProjectionContext) _localctx).playerDesc = playerDesc();
                    }
                }

                setState(118);
                ((ProjectionContext) _localctx).blindDesc = blindDesc();
                setState(120);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == LASH_DESC) {
                    {
                        setState(119);
                        ((ProjectionContext) _localctx).lashDesc = lashDesc();
                    }
                }

                setState(123);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == NUMERATOR) {
                    {
                        setState(122);
                        ((ProjectionContext) _localctx).numerator = numerator();
                    }
                }

                setState(126);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == DENOMINATOR) {
                    {
                        setState(125);
                        ((ProjectionContext) _localctx).denominator = denominator();
                    }
                }

                setState(129);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == DIVISOR) {
                    {
                        setState(128);
                        ((ProjectionContext) _localctx).divisor = divisor();
                    }
                }

                setState(132);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == DAMAGE_CAP) {
                    {
                        setState(131);
                        ((ProjectionContext) _localctx).damageCap = damageCap();
                    }
                }

                setState(135);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == MSGT) {
                    {
                        setState(134);
                        ((ProjectionContext) _localctx).msgt = msgt();
                    }
                }

                setState(137);
                ((ProjectionContext) _localctx).obvious = obvious();
                setState(139);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == WAKE) {
                    {
                        setState(138);
                        ((ProjectionContext) _localctx).wake = wake();
                    }
                }

                setState(141);
                ((ProjectionContext) _localctx).colour = colour();

                ProjectionEnum codeInit = ((ProjectionContext) _localctx).code.projectionCode;

                String nameInit;
                if (((ProjectionContext) _localctx).name == null)
                    nameInit = "";
                else
                    nameInit = ((ProjectionContext) _localctx).name.projectionName;

                ProjectionType typeInit = ((ProjectionContext) _localctx).type.projectionType;

                String descInit = ((ProjectionContext) _localctx).desc.description;

                String pDesc = (((ProjectionContext) _localctx).playerDesc == null) ? "" : ((ProjectionContext) _localctx).playerDesc.playerDescription;

                String bDesc = ((ProjectionContext) _localctx).blindDesc.blindDescription;

                String lDesc = (((ProjectionContext) _localctx).lashDesc == null) ? "" : ((ProjectionContext) _localctx).lashDesc.lashDescription;

                int num = (((ProjectionContext) _localctx).numerator == null) ? -1 : ((ProjectionContext) _localctx).numerator.num;

                int den = (((ProjectionContext) _localctx).denominator == null) ? -1 : ((ProjectionContext) _localctx).denominator.den;
                Random dice = (((ProjectionContext) _localctx).denominator == null) ? null : ((ProjectionContext) _localctx).denominator.denDice;
                boolean isRandom = (((ProjectionContext) _localctx).denominator == null) ? false : ((ProjectionContext) _localctx).denominator.isDice;

                int div = (((ProjectionContext) _localctx).divisor == null) ? -1 : ((ProjectionContext) _localctx).divisor.div;

                int dam = (((ProjectionContext) _localctx).damageCap == null) ? -1 : ((ProjectionContext) _localctx).damageCap.damCap;

                MessageEnum msg = (((ProjectionContext) _localctx).msgt == null) ? null : ((ProjectionContext) _localctx).msgt.sound;

                boolean obv = ((ProjectionContext) _localctx).obvious.isObvious;

                boolean wak = (((ProjectionContext) _localctx).wake == null) ? false : ((ProjectionContext) _localctx).wake.willWake;

                ColourType col = ((ProjectionContext) _localctx).colour.col;

                if (isRandom)
                    ((ProjectionContext) _localctx).proj = new Projection(codeInit, nameInit, typeInit, descInit, pDesc, bDesc, lDesc, num, dice, div, dam, msg, obv, wak, col);
                else
                    ((ProjectionContext) _localctx).proj = new Projection(codeInit, nameInit, typeInit, descInit, pDesc, bDesc, lDesc, num, den, div, dam, msg, obv, wak, col);

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
    public static class FileContext extends ParserRuleContext {
        public ArrayList<Projection> projections;
        public ProjectionContext projection;

        public TerminalNode EOF() {
            return getToken(ProjectionReaderParser.EOF, 0);
        }

        public List<ProjectionContext> projection() {
            return getRuleContexts(ProjectionContext.class);
        }

        public ProjectionContext projection(int i) {
            return getRuleContext(ProjectionContext.class, i);
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
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ProjectionReaderListener) ((ProjectionReaderListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ProjectionReaderVisitor)
                return ((ProjectionReaderVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 34, RULE_file);

        ((FileContext) _localctx).projections = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(147);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(144);
                            ((FileContext) _localctx).projection = projection();

                            _localctx.projections.add(((FileContext) _localctx).projection.proj);

                        }
                    }
                    setState(149);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == CODE);
                setState(151);
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
            "\u0004\u0001\u001b\u009a\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001" +
                    "\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004" +
                    "\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007" +
                    "\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b" +
                    "\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007" +
                    "\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t" +
                    "\u0001\t\u0001\t\u0003\tO\b\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n" +
                    "\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001" +
                    "\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e" +
                    "\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f" +
                    "j\b\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0003\u0010" +
                    "p\b\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010u\b\u0010\u0001" +
                    "\u0010\u0001\u0010\u0003\u0010y\b\u0010\u0001\u0010\u0003\u0010|\b\u0010" +
                    "\u0001\u0010\u0003\u0010\u007f\b\u0010\u0001\u0010\u0003\u0010\u0082\b" +
                    "\u0010\u0001\u0010\u0003\u0010\u0085\b\u0010\u0001\u0010\u0003\u0010\u0088" +
                    "\b\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u008c\b\u0010\u0001\u0010" +
                    "\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0004\u0011" +
                    "\u0094\b\u0011\u000b\u0011\f\u0011\u0095\u0001\u0011\u0001\u0011\u0001" +
                    "\u0011\u0000\u0000\u0012\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012" +
                    "\u0014\u0016\u0018\u001a\u001c\u001e \"\u0000\u0001\u0001\u0000\u0001" +
                    "\u0003\u0093\u0000$\u0001\u0000\u0000\u0000\u0002(\u0001\u0000\u0000\u0000" +
                    "\u0004,\u0001\u0000\u0000\u0000\u00060\u0001\u0000\u0000\u0000\b4\u0001" +
                    "\u0000\u0000\u0000\n8\u0001\u0000\u0000\u0000\f<\u0001\u0000\u0000\u0000" +
                    "\u000e@\u0001\u0000\u0000\u0000\u0010D\u0001\u0000\u0000\u0000\u0012K" +
                    "\u0001\u0000\u0000\u0000\u0014R\u0001\u0000\u0000\u0000\u0016V\u0001\u0000" +
                    "\u0000\u0000\u0018Z\u0001\u0000\u0000\u0000\u001a^\u0001\u0000\u0000\u0000" +
                    "\u001cb\u0001\u0000\u0000\u0000\u001ef\u0001\u0000\u0000\u0000 m\u0001" +
                    "\u0000\u0000\u0000\"\u0093\u0001\u0000\u0000\u0000$%\u0005\b\u0000\u0000" +
                    "%&\u0005\u0017\u0000\u0000&\'\u0006\u0000\uffff\uffff\u0000\'\u0001\u0001" +
                    "\u0000\u0000\u0000()\u0005\t\u0000\u0000)*\u0005\u0018\u0000\u0000*+\u0006" +
                    "\u0001\uffff\uffff\u0000+\u0003\u0001\u0000\u0000\u0000,-\u0005\n\u0000" +
                    "\u0000-.\u0007\u0000\u0000\u0000./\u0006\u0002\uffff\uffff\u0000/\u0005" +
                    "\u0001\u0000\u0000\u000001\u0005\u000b\u0000\u000012\u0005\u0018\u0000" +
                    "\u000023\u0006\u0003\uffff\uffff\u00003\u0007\u0001\u0000\u0000\u0000" +
                    "45\u0005\f\u0000\u000056\u0005\u0018\u0000\u000067\u0006\u0004\uffff\uffff" +
                    "\u00007\t\u0001\u0000\u0000\u000089\u0005\r\u0000\u00009:\u0005\u0018" +
                    "\u0000\u0000:;\u0006\u0005\uffff\uffff\u0000;\u000b\u0001\u0000\u0000" +
                    "\u0000<=\u0005\u000e\u0000\u0000=>\u0005\u0018\u0000\u0000>?\u0006\u0006" +
                    "\uffff\uffff\u0000?\r\u0001\u0000\u0000\u0000@A\u0005\u000f\u0000\u0000" +
                    "AB\u0005\u001b\u0000\u0000BC\u0006\u0007\uffff\uffff\u0000C\u000f\u0001" +
                    "\u0000\u0000\u0000DE\u0005\u001b\u0000\u0000EF\u0005\u0004\u0000\u0000" +
                    "FG\u0005\u001b\u0000\u0000GH\u0005\u0005\u0000\u0000HI\u0005\u001b\u0000" +
                    "\u0000IJ\u0006\b\uffff\uffff\u0000J\u0011\u0001\u0000\u0000\u0000KN\u0005" +
                    "\u0010\u0000\u0000LO\u0005\u001b\u0000\u0000MO\u0003\u0010\b\u0000NL\u0001" +
                    "\u0000\u0000\u0000NM\u0001\u0000\u0000\u0000OP\u0001\u0000\u0000\u0000" +
                    "PQ\u0006\t\uffff\uffff\u0000Q\u0013\u0001\u0000\u0000\u0000RS\u0005\u0011" +
                    "\u0000\u0000ST\u0005\u001b\u0000\u0000TU\u0006\n\uffff\uffff\u0000U\u0015" +
                    "\u0001\u0000\u0000\u0000VW\u0005\u0012\u0000\u0000WX\u0005\u001b\u0000" +
                    "\u0000XY\u0006\u000b\uffff\uffff\u0000Y\u0017\u0001\u0000\u0000\u0000" +
                    "Z[\u0005\u0013\u0000\u0000[\\\u0005\u0017\u0000\u0000\\]\u0006\f\uffff" +
                    "\uffff\u0000]\u0019\u0001\u0000\u0000\u0000^_\u0005\u0014\u0000\u0000" +
                    "_`\u0005\u001b\u0000\u0000`a\u0006\r\uffff\uffff\u0000a\u001b\u0001\u0000" +
                    "\u0000\u0000bc\u0005\u0015\u0000\u0000cd\u0005\u001b\u0000\u0000de\u0006" +
                    "\u000e\uffff\uffff\u0000e\u001d\u0001\u0000\u0000\u0000fi\u0005\u0016" +
                    "\u0000\u0000gj\u0005\u001a\u0000\u0000hj\u0005\u0017\u0000\u0000ig\u0001" +
                    "\u0000\u0000\u0000ih\u0001\u0000\u0000\u0000jk\u0001\u0000\u0000\u0000" +
                    "kl\u0006\u000f\uffff\uffff\u0000l\u001f\u0001\u0000\u0000\u0000mo\u0003" +
                    "\u0000\u0000\u0000np\u0003\u0002\u0001\u0000on\u0001\u0000\u0000\u0000" +
                    "op\u0001\u0000\u0000\u0000pq\u0001\u0000\u0000\u0000qr\u0003\u0004\u0002" +
                    "\u0000rt\u0003\u0006\u0003\u0000su\u0003\b\u0004\u0000ts\u0001\u0000\u0000" +
                    "\u0000tu\u0001\u0000\u0000\u0000uv\u0001\u0000\u0000\u0000vx\u0003\n\u0005" +
                    "\u0000wy\u0003\f\u0006\u0000xw\u0001\u0000\u0000\u0000xy\u0001\u0000\u0000" +
                    "\u0000y{\u0001\u0000\u0000\u0000z|\u0003\u000e\u0007\u0000{z\u0001\u0000" +
                    "\u0000\u0000{|\u0001\u0000\u0000\u0000|~\u0001\u0000\u0000\u0000}\u007f" +
                    "\u0003\u0012\t\u0000~}\u0001\u0000\u0000\u0000~\u007f\u0001\u0000\u0000" +
                    "\u0000\u007f\u0081\u0001\u0000\u0000\u0000\u0080\u0082\u0003\u0014\n\u0000" +
                    "\u0081\u0080\u0001\u0000\u0000\u0000\u0081\u0082\u0001\u0000\u0000\u0000" +
                    "\u0082\u0084\u0001\u0000\u0000\u0000\u0083\u0085\u0003\u0016\u000b\u0000" +
                    "\u0084\u0083\u0001\u0000\u0000\u0000\u0084\u0085\u0001\u0000\u0000\u0000" +
                    "\u0085\u0087\u0001\u0000\u0000\u0000\u0086\u0088\u0003\u0018\f\u0000\u0087" +
                    "\u0086\u0001\u0000\u0000\u0000\u0087\u0088\u0001\u0000\u0000\u0000\u0088" +
                    "\u0089\u0001\u0000\u0000\u0000\u0089\u008b\u0003\u001a\r\u0000\u008a\u008c" +
                    "\u0003\u001c\u000e\u0000\u008b\u008a\u0001\u0000\u0000\u0000\u008b\u008c" +
                    "\u0001\u0000\u0000\u0000\u008c\u008d\u0001\u0000\u0000\u0000\u008d\u008e" +
                    "\u0003\u001e\u000f\u0000\u008e\u008f\u0006\u0010\uffff\uffff\u0000\u008f" +
                    "!\u0001\u0000\u0000\u0000\u0090\u0091\u0003 \u0010\u0000\u0091\u0092\u0006" +
                    "\u0011\uffff\uffff\u0000\u0092\u0094\u0001\u0000\u0000\u0000\u0093\u0090" +
                    "\u0001\u0000\u0000\u0000\u0094\u0095\u0001\u0000\u0000\u0000\u0095\u0093" +
                    "\u0001\u0000\u0000\u0000\u0095\u0096\u0001\u0000\u0000\u0000\u0096\u0097" +
                    "\u0001\u0000\u0000\u0000\u0097\u0098\u0005\u0000\u0000\u0001\u0098#\u0001" +
                    "\u0000\u0000\u0000\fNiotx{~\u0081\u0084\u0087\u008b\u0095";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}