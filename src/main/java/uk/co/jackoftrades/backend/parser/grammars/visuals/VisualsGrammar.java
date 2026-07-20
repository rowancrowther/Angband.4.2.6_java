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
// Generated from VisualsGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.visuals;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.parser.visuals.VisualsCycleParseRecord;
import uk.co.jackoftrades.backend.parser.visuals.VisualsFlickerParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class VisualsGrammar extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            FLICKER = 1, FLICKER_COLOUR = 2, CYCLE = 3, CYCLE_COLOUR = 4, COMMENT = 5, EOL = 6,
            STRING = 7, ROL_EOL = 8, COLOUR_CHAR = 9, SWAP_COLON = 10, COLOUR_EOL = 11, DELIM_COLON = 12,
            STRING_BETWEEN_COLON = 13, POP_EOL = 14;
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
                null, "'flicker:'", "'flicker-color:'", "'cycle:'", "'cycle-color:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "FLICKER", "FLICKER_COLOUR", "CYCLE", "CYCLE_COLOUR", "COMMENT",
                "EOL", "STRING", "ROL_EOL", "COLOUR_CHAR", "SWAP_COLON", "COLOUR_EOL",
                "DELIM_COLON", "STRING_BETWEEN_COLON", "POP_EOL"
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
        return "VisualsGrammar.g4";
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

    public VisualsGrammar(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class FlickerContext extends ParserRuleContext {
        public String colourChar;
        public String blockName;
        public Token COLOUR_CHAR;
        public Token STRING;

        public TerminalNode FLICKER() {
            return getToken(VisualsGrammar.FLICKER, 0);
        }

        public TerminalNode COLOUR_CHAR() {
            return getToken(VisualsGrammar.COLOUR_CHAR, 0);
        }

        public TerminalNode SWAP_COLON() {
            return getToken(VisualsGrammar.SWAP_COLON, 0);
        }

        public TerminalNode STRING() {
            return getToken(VisualsGrammar.STRING, 0);
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
            if (listener instanceof VisualsGrammarListener) ((VisualsGrammarListener) listener).enterFlicker(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof VisualsGrammarListener) ((VisualsGrammarListener) listener).exitFlicker(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof VisualsGrammarVisitor)
                return ((VisualsGrammarVisitor<? extends T>) visitor).visitFlicker(this);
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
                ((FlickerContext) _localctx).COLOUR_CHAR = match(COLOUR_CHAR);
                setState(16);
                match(SWAP_COLON);
                setState(17);
                ((FlickerContext) _localctx).STRING = match(STRING);
                ((FlickerContext) _localctx).colourChar = ((FlickerContext) _localctx).COLOUR_CHAR.getText();
                ((FlickerContext) _localctx).blockName = ((FlickerContext) _localctx).STRING.getText();
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
        public String colourChar;
        public Token COLOUR_CHAR;

        public TerminalNode FLICKER_COLOUR() {
            return getToken(VisualsGrammar.FLICKER_COLOUR, 0);
        }

        public TerminalNode COLOUR_CHAR() {
            return getToken(VisualsGrammar.COLOUR_CHAR, 0);
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
            if (listener instanceof VisualsGrammarListener)
                ((VisualsGrammarListener) listener).enterFlickerColour(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof VisualsGrammarListener) ((VisualsGrammarListener) listener).exitFlickerColour(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof VisualsGrammarVisitor)
                return ((VisualsGrammarVisitor<? extends T>) visitor).visitFlickerColour(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FlickerColourContext flickerColour() throws RecognitionException {
        FlickerColourContext _localctx = new FlickerColourContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_flickerColour);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(20);
                match(FLICKER_COLOUR);
                setState(21);
                ((FlickerColourContext) _localctx).COLOUR_CHAR = match(COLOUR_CHAR);
                ((FlickerColourContext) _localctx).colourChar = ((FlickerColourContext) _localctx).COLOUR_CHAR.getText();
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
        public VisualsFlickerParseRecord flickerRecord;
        public FlickerContext flicker;
        public FlickerColourContext flickerColour;

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
            if (listener instanceof VisualsGrammarListener) ((VisualsGrammarListener) listener).enterFlickerBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof VisualsGrammarListener) ((VisualsGrammarListener) listener).exitFlickerBlock(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof VisualsGrammarVisitor)
                return ((VisualsGrammarVisitor<? extends T>) visitor).visitFlickerBlock(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FlickerBlockContext flickerBlock() throws RecognitionException {
        FlickerBlockContext _localctx = new FlickerBlockContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_flickerBlock);

        ArrayList<String> colourChars = new ArrayList<>();
        int line = _localctx.start.getLine();
        String name = "";
        String colourChar = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(24);
                ((FlickerBlockContext) _localctx).flicker = flicker();
                name = ((FlickerBlockContext) _localctx).flicker.blockName;
                colourChar = ((FlickerBlockContext) _localctx).flicker.colourChar;
                setState(29);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(26);
                            ((FlickerBlockContext) _localctx).flickerColour = flickerColour();
                            colourChars.add(((FlickerBlockContext) _localctx).flickerColour.colourChar);
                        }
                    }
                    setState(31);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == FLICKER_COLOUR);
            }
            _ctx.stop = _input.LT(-1);

            ((FlickerBlockContext) _localctx).flickerRecord = new VisualsFlickerParseRecord(name, colourChar, colourChars, line);

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
        public String cycleGroup;
        public String cycleName;
        public Token STRING_BETWEEN_COLON;
        public Token STRING;

        public TerminalNode CYCLE() {
            return getToken(VisualsGrammar.CYCLE, 0);
        }

        public TerminalNode STRING_BETWEEN_COLON() {
            return getToken(VisualsGrammar.STRING_BETWEEN_COLON, 0);
        }

        public TerminalNode DELIM_COLON() {
            return getToken(VisualsGrammar.DELIM_COLON, 0);
        }

        public TerminalNode STRING() {
            return getToken(VisualsGrammar.STRING, 0);
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
            if (listener instanceof VisualsGrammarListener) ((VisualsGrammarListener) listener).enterCycle(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof VisualsGrammarListener) ((VisualsGrammarListener) listener).exitCycle(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof VisualsGrammarVisitor)
                return ((VisualsGrammarVisitor<? extends T>) visitor).visitCycle(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CycleContext cycle() throws RecognitionException {
        CycleContext _localctx = new CycleContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_cycle);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(33);
                match(CYCLE);
                setState(34);
                ((CycleContext) _localctx).STRING_BETWEEN_COLON = match(STRING_BETWEEN_COLON);
                setState(35);
                match(DELIM_COLON);
                setState(36);
                ((CycleContext) _localctx).STRING = match(STRING);
                ((CycleContext) _localctx).cycleGroup = ((CycleContext) _localctx).STRING_BETWEEN_COLON.getText();
                ((CycleContext) _localctx).cycleName = ((CycleContext) _localctx).STRING.getText();
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
        public String colour;
        public Token COLOUR_CHAR;

        public TerminalNode CYCLE_COLOUR() {
            return getToken(VisualsGrammar.CYCLE_COLOUR, 0);
        }

        public TerminalNode COLOUR_CHAR() {
            return getToken(VisualsGrammar.COLOUR_CHAR, 0);
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
            if (listener instanceof VisualsGrammarListener) ((VisualsGrammarListener) listener).enterCycleColour(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof VisualsGrammarListener) ((VisualsGrammarListener) listener).exitCycleColour(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof VisualsGrammarVisitor)
                return ((VisualsGrammarVisitor<? extends T>) visitor).visitCycleColour(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CycleColourContext cycleColour() throws RecognitionException {
        CycleColourContext _localctx = new CycleColourContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_cycleColour);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(39);
                match(CYCLE_COLOUR);
                setState(40);
                ((CycleColourContext) _localctx).COLOUR_CHAR = match(COLOUR_CHAR);
                ((CycleColourContext) _localctx).colour = ((CycleColourContext) _localctx).COLOUR_CHAR.getText();
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
        public VisualsCycleParseRecord cycleRecord;
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
            if (listener instanceof VisualsGrammarListener) ((VisualsGrammarListener) listener).enterCycleBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof VisualsGrammarListener) ((VisualsGrammarListener) listener).exitCycleBlock(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof VisualsGrammarVisitor)
                return ((VisualsGrammarVisitor<? extends T>) visitor).visitCycleBlock(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CycleBlockContext cycleBlock() throws RecognitionException {
        CycleBlockContext _localctx = new CycleBlockContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_cycleBlock);

        String cycleGroup;
        String cycleName;
        ArrayList<String> colours = new ArrayList<>();
        int line = _localctx.start.getLine();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(43);
                ((CycleBlockContext) _localctx).cycle = cycle();
                cycleGroup = ((CycleBlockContext) _localctx).cycle.cycleGroup;
                cycleName = ((CycleBlockContext) _localctx).cycle.cycleName;
                setState(48);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(45);
                            ((CycleBlockContext) _localctx).cycleColour = cycleColour();
                            colours.add(((CycleBlockContext) _localctx).cycleColour.colour);
                        }
                    }
                    setState(50);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == CYCLE_COLOUR);
            }
            _ctx.stop = _input.LT(-1);

            ((CycleBlockContext) _localctx).cycleRecord = new VisualsCycleParseRecord(cycleGroup, cycleName, colours, line);

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
        public List<VisualsFlickerParseRecord> flickers;
        public List<VisualsCycleParseRecord> cycles;
        public FlickerBlockContext flickerBlock;
        public CycleBlockContext cycleBlock;

        public TerminalNode EOF() {
            return getToken(VisualsGrammar.EOF, 0);
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
            if (listener instanceof VisualsGrammarListener) ((VisualsGrammarListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof VisualsGrammarListener) ((VisualsGrammarListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof VisualsGrammarVisitor)
                return ((VisualsGrammarVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_file);
        ((FileContext) _localctx).flickers = new ArrayList<>();
        ((FileContext) _localctx).cycles = new ArrayList<>();
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(58);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        setState(58);
                        _errHandler.sync(this);
                        switch (_input.LA(1)) {
                            case FLICKER: {
                                {
                                    setState(52);
                                    ((FileContext) _localctx).flickerBlock = flickerBlock();
                                    _localctx.flickers.add(((FileContext) _localctx).flickerBlock.flickerRecord);
                                }
                            }
                            break;
                            case CYCLE: {
                                {
                                    setState(55);
                                    ((FileContext) _localctx).cycleBlock = cycleBlock();
                                    _localctx.cycles.add(((FileContext) _localctx).cycleBlock.cycleRecord);
                                }
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                    }
                    setState(60);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == FLICKER || _la == CYCLE);
                setState(62);
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
            "\u0004\u0001\u000eA\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0004\u0002\u001e\b\u0002\u000b\u0002\f\u0002\u001f\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0004\u00051\b\u0005\u000b\u0005\f\u00052\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0004" +
                    "\u0006;\b\u0006\u000b\u0006\f\u0006<\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0000\u0000\u0007\u0000\u0002\u0004\u0006\b\n\f\u0000\u0000=\u0000\u000e" +
                    "\u0001\u0000\u0000\u0000\u0002\u0014\u0001\u0000\u0000\u0000\u0004\u0018" +
                    "\u0001\u0000\u0000\u0000\u0006!\u0001\u0000\u0000\u0000\b\'\u0001\u0000" +
                    "\u0000\u0000\n+\u0001\u0000\u0000\u0000\f:\u0001\u0000\u0000\u0000\u000e" +
                    "\u000f\u0005\u0001\u0000\u0000\u000f\u0010\u0005\t\u0000\u0000\u0010\u0011" +
                    "\u0005\n\u0000\u0000\u0011\u0012\u0005\u0007\u0000\u0000\u0012\u0013\u0006" +
                    "\u0000\uffff\uffff\u0000\u0013\u0001\u0001\u0000\u0000\u0000\u0014\u0015" +
                    "\u0005\u0002\u0000\u0000\u0015\u0016\u0005\t\u0000\u0000\u0016\u0017\u0006" +
                    "\u0001\uffff\uffff\u0000\u0017\u0003\u0001\u0000\u0000\u0000\u0018\u0019" +
                    "\u0003\u0000\u0000\u0000\u0019\u001d\u0006\u0002\uffff\uffff\u0000\u001a" +
                    "\u001b\u0003\u0002\u0001\u0000\u001b\u001c\u0006\u0002\uffff\uffff\u0000" +
                    "\u001c\u001e\u0001\u0000\u0000\u0000\u001d\u001a\u0001\u0000\u0000\u0000" +
                    "\u001e\u001f\u0001\u0000\u0000\u0000\u001f\u001d\u0001\u0000\u0000\u0000" +
                    "\u001f \u0001\u0000\u0000\u0000 \u0005\u0001\u0000\u0000\u0000!\"\u0005" +
                    "\u0003\u0000\u0000\"#\u0005\r\u0000\u0000#$\u0005\f\u0000\u0000$%\u0005" +
                    "\u0007\u0000\u0000%&\u0006\u0003\uffff\uffff\u0000&\u0007\u0001\u0000" +
                    "\u0000\u0000\'(\u0005\u0004\u0000\u0000()\u0005\t\u0000\u0000)*\u0006" +
                    "\u0004\uffff\uffff\u0000*\t\u0001\u0000\u0000\u0000+,\u0003\u0006\u0003" +
                    "\u0000,0\u0006\u0005\uffff\uffff\u0000-.\u0003\b\u0004\u0000./\u0006\u0005" +
                    "\uffff\uffff\u0000/1\u0001\u0000\u0000\u00000-\u0001\u0000\u0000\u0000" +
                    "12\u0001\u0000\u0000\u000020\u0001\u0000\u0000\u000023\u0001\u0000\u0000" +
                    "\u00003\u000b\u0001\u0000\u0000\u000045\u0003\u0004\u0002\u000056\u0006" +
                    "\u0006\uffff\uffff\u00006;\u0001\u0000\u0000\u000078\u0003\n\u0005\u0000" +
                    "89\u0006\u0006\uffff\uffff\u00009;\u0001\u0000\u0000\u0000:4\u0001\u0000" +
                    "\u0000\u0000:7\u0001\u0000\u0000\u0000;<\u0001\u0000\u0000\u0000<:\u0001" +
                    "\u0000\u0000\u0000<=\u0001\u0000\u0000\u0000=>\u0001\u0000\u0000\u0000" +
                    ">?\u0005\u0000\u0000\u0001?\r\u0001\u0000\u0000\u0000\u0004\u001f2:<";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}