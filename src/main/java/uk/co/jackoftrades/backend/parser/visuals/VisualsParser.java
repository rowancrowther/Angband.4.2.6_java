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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Visuals.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.visuals;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.frontend.colour.VisualsColourCycle;
import uk.co.jackoftrades.frontend.colour.VisualsCycleGroup;
import uk.co.jackoftrades.frontend.colour.VisualsCycler;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class VisualsParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, FLICKER = 3, FLICKER_COLOUR = 4, CYCLE = 5, FANCY = 6, CYCLE_COLOUR = 7,
            COLON = 8, COLOUR_CHAR = 9, LCASE = 10;
    public static final int
            RULE_flicker = 0, RULE_flickerColour = 1, RULE_flickerBlock = 2, RULE_cycle = 3,
            RULE_cycleColour = 4, RULE_cycleBlock = 5, RULE_file = 6;

    private static String[] makeRuleNames() {
        return new String[]{
                "flicker", "flickerColour", "flickerBlock", "cycle", "cycleColour", "cycleBlock",
                "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'flicker:'", "'flicker-color:'", "'cycle:'", "'fancy:'",
                "'cycle-color:'", "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "FLICKER", "FLICKER_COLOUR", "CYCLE", "FANCY",
                "CYCLE_COLOUR", "COLON", "COLOUR_CHAR", "LCASE"
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
        return "Visuals.g4";
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

    public VisualsParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class FlickerContext extends ParserRuleContext {
        public TerminalNode FLICKER() {
            return getToken(VisualsParser.FLICKER, 0);
        }

        public TerminalNode COLOUR_CHAR() {
            return getToken(VisualsParser.COLOUR_CHAR, 0);
        }

        public TerminalNode COLON() {
            return getToken(VisualsParser.COLON, 0);
        }

        public TerminalNode LCASE() {
            return getToken(VisualsParser.LCASE, 0);
        }

        public FlickerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_flicker;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof VisualsListener) ((VisualsListener) listener).enterFlicker(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof VisualsListener) ((VisualsListener) listener).exitFlicker(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof VisualsVisitor) return ((VisualsVisitor<? extends T>) visitor).visitFlicker(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FlickerContext flicker() throws RecognitionException {
        FlickerContext _localctx = new FlickerContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_flicker);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(14);
                match(FLICKER);
                setState(15);
                match(COLOUR_CHAR);
                setState(16);
                match(COLON);
                setState(17);
                match(LCASE);
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
    public static class FlickerColourContext extends ParserRuleContext {
        public TerminalNode FLICKER_COLOUR() {
            return getToken(VisualsParser.FLICKER_COLOUR, 0);
        }

        public TerminalNode COLOUR_CHAR() {
            return getToken(VisualsParser.COLOUR_CHAR, 0);
        }

        public FlickerColourContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_flickerColour;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof VisualsListener) ((VisualsListener) listener).enterFlickerColour(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof VisualsListener) ((VisualsListener) listener).exitFlickerColour(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof VisualsVisitor)
                return ((VisualsVisitor<? extends T>) visitor).visitFlickerColour(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FlickerColourContext flickerColour() throws RecognitionException {
        FlickerColourContext _localctx = new FlickerColourContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_flickerColour);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(19);
                match(FLICKER_COLOUR);
                setState(20);
                match(COLOUR_CHAR);
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
    public static class FlickerBlockContext extends ParserRuleContext {
        public FlickerContext flicker() {
            return getRuleContext(FlickerContext.class, 0);
        }

        public List<FlickerColourContext> flickerColour() {
            return getRuleContexts(FlickerColourContext.class);
        }

        public FlickerColourContext flickerColour(int i) {
            return getRuleContext(FlickerColourContext.class, i);
        }

        public FlickerBlockContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_flickerBlock;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof VisualsListener) ((VisualsListener) listener).enterFlickerBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof VisualsListener) ((VisualsListener) listener).exitFlickerBlock(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof VisualsVisitor)
                return ((VisualsVisitor<? extends T>) visitor).visitFlickerBlock(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FlickerBlockContext flickerBlock() throws RecognitionException {
        FlickerBlockContext _localctx = new FlickerBlockContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_flickerBlock);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(22);
                flicker();
                setState(24);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(23);
                            flickerColour();
                        }
                    }
                    setState(26);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == FLICKER_COLOUR);
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
    public static class CycleContext extends ParserRuleContext {
        public String groupName;
        public String cycleName;
        public Token LCASE;
        public Token COLOUR_CHAR;

        public TerminalNode CYCLE() {
            return getToken(VisualsParser.CYCLE, 0);
        }

        public TerminalNode FLICKER() {
            return getToken(VisualsParser.FLICKER, 0);
        }

        public TerminalNode FANCY() {
            return getToken(VisualsParser.FANCY, 0);
        }

        public TerminalNode LCASE() {
            return getToken(VisualsParser.LCASE, 0);
        }

        public TerminalNode COLOUR_CHAR() {
            return getToken(VisualsParser.COLOUR_CHAR, 0);
        }

        public CycleContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_cycle;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof VisualsListener) ((VisualsListener) listener).enterCycle(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof VisualsListener) ((VisualsListener) listener).exitCycle(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof VisualsVisitor) return ((VisualsVisitor<? extends T>) visitor).visitCycle(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CycleContext cycle() throws RecognitionException {
        CycleContext _localctx = new CycleContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_cycle);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(28);
                match(CYCLE);
                setState(33);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case FLICKER: {
                        setState(29);
                        match(FLICKER);
                        ((CycleContext) _localctx).groupName = "flicker";
                    }
                    break;
                    case FANCY: {
                        setState(31);
                        match(FANCY);
                        ((CycleContext) _localctx).groupName = "fancy";
                    }
                    break;
                    default:
                        throw new NoViableAltException(this);
                }
                setState(39);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case LCASE: {
                        setState(35);
                        ((CycleContext) _localctx).LCASE = match(LCASE);
                        ((CycleContext) _localctx).cycleName = ((CycleContext) _localctx).LCASE.getText();
                    }
                    break;
                    case COLOUR_CHAR: {
                        setState(37);
                        ((CycleContext) _localctx).COLOUR_CHAR = match(COLOUR_CHAR);
                        ((CycleContext) _localctx).cycleName = ((CycleContext) _localctx).COLOUR_CHAR.getText();
                    }
                    break;
                    default:
                        throw new NoViableAltException(this);
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
    public static class CycleColourContext extends ParserRuleContext {
        public ColourType colourType;
        public Token COLOUR_CHAR;

        public TerminalNode CYCLE_COLOUR() {
            return getToken(VisualsParser.CYCLE_COLOUR, 0);
        }

        public TerminalNode COLOUR_CHAR() {
            return getToken(VisualsParser.COLOUR_CHAR, 0);
        }

        public CycleColourContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_cycleColour;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof VisualsListener) ((VisualsListener) listener).enterCycleColour(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof VisualsListener) ((VisualsListener) listener).exitCycleColour(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof VisualsVisitor)
                return ((VisualsVisitor<? extends T>) visitor).visitCycleColour(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CycleColourContext cycleColour() throws RecognitionException {
        CycleColourContext _localctx = new CycleColourContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_cycleColour);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(41);
                match(CYCLE_COLOUR);
                setState(42);
                ((CycleColourContext) _localctx).COLOUR_CHAR = match(COLOUR_CHAR);

                char rawChar = ((CycleColourContext) _localctx).COLOUR_CHAR.getText().charAt(0);
                ((CycleColourContext) _localctx).colourType = ColourType.findColourType(rawChar);

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
    public static class CycleBlockContext extends ParserRuleContext {
        public VisualsCycleGroup group;
        public CycleContext cycle;
        public CycleColourContext cycleColour;

        public CycleContext cycle() {
            return getRuleContext(CycleContext.class, 0);
        }

        public List<CycleColourContext> cycleColour() {
            return getRuleContexts(CycleColourContext.class);
        }

        public CycleColourContext cycleColour(int i) {
            return getRuleContext(CycleColourContext.class, i);
        }

        public CycleBlockContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_cycleBlock;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof VisualsListener) ((VisualsListener) listener).enterCycleBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof VisualsListener) ((VisualsListener) listener).exitCycleBlock(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof VisualsVisitor) return ((VisualsVisitor<? extends T>) visitor).visitCycleBlock(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CycleBlockContext cycleBlock() throws RecognitionException {
        CycleBlockContext _localctx = new CycleBlockContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_cycleBlock);

        String groupName = "";
        String cycleName = "";
        List<ColourType> colourTypes = new ArrayList<>();
        ((CycleBlockContext) _localctx).group = new VisualsCycleGroup();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(45);
                ((CycleBlockContext) _localctx).cycle = cycle();

                groupName = ((CycleBlockContext) _localctx).cycle.groupName;
                cycleName = ((CycleBlockContext) _localctx).cycle.cycleName;

                setState(50);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(47);
                            ((CycleBlockContext) _localctx).cycleColour = cycleColour();

                            ColourType colourType = ((CycleBlockContext) _localctx).cycleColour.colourType;
                            colourTypes.add(colourType);

                        }
                    }
                    setState(52);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == CYCLE_COLOUR);
            }
            _ctx.stop = _input.LT(-1);

            VisualsColourCycle colourCycle = new VisualsColourCycle(cycleName, ColourType.COLOUR_TYPE_DARK);

            for (ColourType colType : colourTypes)
                colourCycle.addStep(colType);

            _localctx.group.setGroupName(groupName);
            _localctx.group.addCycle(colourCycle);

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
        public VisualsCycler cycler;
        public CycleBlockContext cycleBlock;

        public TerminalNode EOF() {
            return getToken(VisualsParser.EOF, 0);
        }

        public List<FlickerBlockContext> flickerBlock() {
            return getRuleContexts(FlickerBlockContext.class);
        }

        public FlickerBlockContext flickerBlock(int i) {
            return getRuleContext(FlickerBlockContext.class, i);
        }

        public List<CycleBlockContext> cycleBlock() {
            return getRuleContexts(CycleBlockContext.class);
        }

        public CycleBlockContext cycleBlock(int i) {
            return getRuleContext(CycleBlockContext.class, i);
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
            if (listener instanceof VisualsListener) ((VisualsListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof VisualsListener) ((VisualsListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof VisualsVisitor) return ((VisualsVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_file);

        ((FileContext) _localctx).cycler = new VisualsCycler();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(55);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(54);
                            flickerBlock();
                        }
                    }
                    setState(57);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == FLICKER);
                setState(62);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(59);
                            ((FileContext) _localctx).cycleBlock = cycleBlock();

                            _localctx.cycler.addVisualsCycleGroup(((FileContext) _localctx).cycleBlock.group);

                        }
                    }
                    setState(64);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == CYCLE);
                setState(66);
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
            "\u0004\u0001\nE\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0002\u0001\u0002\u0004\u0002\u0019\b\u0002\u000b\u0002\f\u0002\u001a" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003" +
                    "\"\b\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003" +
                    "(\b\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0004\u00053\b\u0005" +
                    "\u000b\u0005\f\u00054\u0001\u0006\u0004\u00068\b\u0006\u000b\u0006\f\u0006" +
                    "9\u0001\u0006\u0001\u0006\u0001\u0006\u0004\u0006?\b\u0006\u000b\u0006" +
                    "\f\u0006@\u0001\u0006\u0001\u0006\u0001\u0006\u0000\u0000\u0007\u0000" +
                    "\u0002\u0004\u0006\b\n\f\u0000\u0000C\u0000\u000e\u0001\u0000\u0000\u0000" +
                    "\u0002\u0013\u0001\u0000\u0000\u0000\u0004\u0016\u0001\u0000\u0000\u0000" +
                    "\u0006\u001c\u0001\u0000\u0000\u0000\b)\u0001\u0000\u0000\u0000\n-\u0001" +
                    "\u0000\u0000\u0000\f7\u0001\u0000\u0000\u0000\u000e\u000f\u0005\u0003" +
                    "\u0000\u0000\u000f\u0010\u0005\t\u0000\u0000\u0010\u0011\u0005\b\u0000" +
                    "\u0000\u0011\u0012\u0005\n\u0000\u0000\u0012\u0001\u0001\u0000\u0000\u0000" +
                    "\u0013\u0014\u0005\u0004\u0000\u0000\u0014\u0015\u0005\t\u0000\u0000\u0015" +
                    "\u0003\u0001\u0000\u0000\u0000\u0016\u0018\u0003\u0000\u0000\u0000\u0017" +
                    "\u0019\u0003\u0002\u0001\u0000\u0018\u0017\u0001\u0000\u0000\u0000\u0019" +
                    "\u001a\u0001\u0000\u0000\u0000\u001a\u0018\u0001\u0000\u0000\u0000\u001a" +
                    "\u001b\u0001\u0000\u0000\u0000\u001b\u0005\u0001\u0000\u0000\u0000\u001c" +
                    "!\u0005\u0005\u0000\u0000\u001d\u001e\u0005\u0003\u0000\u0000\u001e\"" +
                    "\u0006\u0003\uffff\uffff\u0000\u001f \u0005\u0006\u0000\u0000 \"\u0006" +
                    "\u0003\uffff\uffff\u0000!\u001d\u0001\u0000\u0000\u0000!\u001f\u0001\u0000" +
                    "\u0000\u0000\"\'\u0001\u0000\u0000\u0000#$\u0005\n\u0000\u0000$(\u0006" +
                    "\u0003\uffff\uffff\u0000%&\u0005\t\u0000\u0000&(\u0006\u0003\uffff\uffff" +
                    "\u0000\'#\u0001\u0000\u0000\u0000\'%\u0001\u0000\u0000\u0000(\u0007\u0001" +
                    "\u0000\u0000\u0000)*\u0005\u0007\u0000\u0000*+\u0005\t\u0000\u0000+,\u0006" +
                    "\u0004\uffff\uffff\u0000,\t\u0001\u0000\u0000\u0000-.\u0003\u0006\u0003" +
                    "\u0000.2\u0006\u0005\uffff\uffff\u0000/0\u0003\b\u0004\u000001\u0006\u0005" +
                    "\uffff\uffff\u000013\u0001\u0000\u0000\u00002/\u0001\u0000\u0000\u0000" +
                    "34\u0001\u0000\u0000\u000042\u0001\u0000\u0000\u000045\u0001\u0000\u0000" +
                    "\u00005\u000b\u0001\u0000\u0000\u000068\u0003\u0004\u0002\u000076\u0001" +
                    "\u0000\u0000\u000089\u0001\u0000\u0000\u000097\u0001\u0000\u0000\u0000" +
                    "9:\u0001\u0000\u0000\u0000:>\u0001\u0000\u0000\u0000;<\u0003\n\u0005\u0000" +
                    "<=\u0006\u0006\uffff\uffff\u0000=?\u0001\u0000\u0000\u0000>;\u0001\u0000" +
                    "\u0000\u0000?@\u0001\u0000\u0000\u0000@>\u0001\u0000\u0000\u0000@A\u0001" +
                    "\u0000\u0000\u0000AB\u0001\u0000\u0000\u0000BC\u0005\u0000\u0000\u0001" +
                    "C\r\u0001\u0000\u0000\u0000\u0006\u001a!\'49@";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}