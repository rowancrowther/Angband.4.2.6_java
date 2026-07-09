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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/History.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.history;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
import uk.co.jackoftrades.middle.player.PlayerHistoryChart;
import uk.co.jackoftrades.middle.player.PlayerHistoryEntry;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class HistoryParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, CHART = 3, PHRASE = 4, COLON = 5, NUMBER = 6, STRING = 7;
    public static final int
            RULE_chart = 0, RULE_phrase = 1, RULE_history = 2, RULE_file = 3;

    private static String[] makeRuleNames() {
        return new String[]{
                "chart", "phrase", "history", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'chart:'", "'phrase:'", "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "CHART", "PHRASE", "COLON", "NUMBER", "STRING"
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
        return "History.g4";
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

    public HistoryParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ChartContext extends ParserRuleContext {
        public int chartInt;
        public int nextInt;
        public int percInt;
        public Token chartNum;
        public Token nextChartNum;
        public Token perc;

        public TerminalNode CHART() {
            return getToken(HistoryParser.CHART, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(HistoryParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(HistoryParser.COLON, i);
        }

        public TerminalNode EOL() {
            return getToken(HistoryParser.EOL, 0);
        }

        public List<TerminalNode> NUMBER() {
            return getTokens(HistoryParser.NUMBER);
        }

        public TerminalNode NUMBER(int i) {
            return getToken(HistoryParser.NUMBER, i);
        }

        public ChartContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_chart;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof HistoryListener) ((HistoryListener) listener).enterChart(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof HistoryListener) ((HistoryListener) listener).exitChart(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof HistoryVisitor) return ((HistoryVisitor<? extends T>) visitor).visitChart(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ChartContext chart() throws RecognitionException {
        ChartContext _localctx = new ChartContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_chart);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(8);
                match(CHART);
                setState(9);
                ((ChartContext) _localctx).chartNum = match(NUMBER);
                setState(10);
                match(COLON);
                setState(11);
                ((ChartContext) _localctx).nextChartNum = match(NUMBER);
                setState(12);
                match(COLON);
                setState(13);
                ((ChartContext) _localctx).perc = match(NUMBER);
                setState(14);
                match(EOL);

                ((ChartContext) _localctx).chartInt = Integer.parseInt(((ChartContext) _localctx).chartNum.getText());
                ((ChartContext) _localctx).nextInt = Integer.parseInt(((ChartContext) _localctx).nextChartNum.getText());
                ((ChartContext) _localctx).percInt = Integer.parseInt(((ChartContext) _localctx).perc.getText());

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
    public static class PhraseContext extends ParserRuleContext {
        public String phraseStr;
        public Token STRING;

        public TerminalNode PHRASE() {
            return getToken(HistoryParser.PHRASE, 0);
        }

        public TerminalNode STRING() {
            return getToken(HistoryParser.STRING, 0);
        }

        public TerminalNode EOL() {
            return getToken(HistoryParser.EOL, 0);
        }

        public PhraseContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_phrase;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof HistoryListener) ((HistoryListener) listener).enterPhrase(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof HistoryListener) ((HistoryListener) listener).exitPhrase(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof HistoryVisitor) return ((HistoryVisitor<? extends T>) visitor).visitPhrase(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PhraseContext phrase() throws RecognitionException {
        PhraseContext _localctx = new PhraseContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_phrase);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(17);
                match(PHRASE);
                setState(18);
                ((PhraseContext) _localctx).STRING = match(STRING);
                setState(19);
                match(EOL);
                ((PhraseContext) _localctx).phraseStr = ((PhraseContext) _localctx).STRING.getText();
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
    public static class HistoryContext extends ParserRuleContext {
        public PlayerHistoryChart historyChart;
        public ChartContext chart;
        public PhraseContext phrase;

        public List<TerminalNode> EOL() {
            return getTokens(HistoryParser.EOL);
        }

        public TerminalNode EOL(int i) {
            return getToken(HistoryParser.EOL, i);
        }

        public List<ChartContext> chart() {
            return getRuleContexts(ChartContext.class);
        }

        public ChartContext chart(int i) {
            return getRuleContext(ChartContext.class, i);
        }

        public List<PhraseContext> phrase() {
            return getRuleContexts(PhraseContext.class);
        }

        public PhraseContext phrase(int i) {
            return getRuleContext(PhraseContext.class, i);
        }

        public HistoryContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_history;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof HistoryListener) ((HistoryListener) listener).enterHistory(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof HistoryListener) ((HistoryListener) listener).exitHistory(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof HistoryVisitor) return ((HistoryVisitor<? extends T>) visitor).visitHistory(this);
            else return visitor.visitChildren(this);
        }
    }

    public final HistoryContext history() throws RecognitionException {
        HistoryContext _localctx = new HistoryContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_history);

        int chartNumber = 0;
        List<PlayerHistoryEntry> entries = new ArrayList<>();

        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(25);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == EOL) {
                    {
                        {
                            setState(22);
                            match(EOL);
                        }
                    }
                    setState(27);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(32);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(28);
                            ((HistoryContext) _localctx).chart = chart();
                            setState(29);
                            ((HistoryContext) _localctx).phrase = phrase();

                            int chartNum = ((HistoryContext) _localctx).chart.chartInt;
                            int nextChart = ((HistoryContext) _localctx).chart.nextInt;
                            int percentage = ((HistoryContext) _localctx).chart.percInt;
                            String string = ((HistoryContext) _localctx).phrase.phraseStr;
                            PlayerHistoryEntry entry;
                            entry = new PlayerHistoryEntry(chartNum, nextChart, percentage, string);
                            entries.add(entry);
                            if (chartNumber == 0)
                                chartNumber = chartNum;
                            else if (chartNumber != chartNum)
                                throw new InvalidTokenFoundDuringParse("History entry found out of order: " + chartNum + ":"
                                        + nextChart + ":" + percentage + ":" + string);

                        }
                    }
                    setState(34);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == CHART);
                setState(37);
                _errHandler.sync(this);
                _alt = 1;
                do {
                    switch (_alt) {
                        case 1: {
                            {
                                setState(36);
                                match(EOL);
                            }
                        }
                        break;
                        default:
                            throw new NoViableAltException(this);
                    }
                    setState(39);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 2, _ctx);
                } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
            }
            _ctx.stop = _input.LT(-1);

            ((HistoryContext) _localctx).historyChart = new PlayerHistoryChart(chartNumber);
            for (PlayerHistoryEntry entry : entries)
                _localctx.historyChart.addEntry(entry);

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
        public List<PlayerHistoryChart> historyCharts;
        public HistoryContext history;

        public TerminalNode EOF() {
            return getToken(HistoryParser.EOF, 0);
        }

        public List<HistoryContext> history() {
            return getRuleContexts(HistoryContext.class);
        }

        public HistoryContext history(int i) {
            return getRuleContext(HistoryContext.class, i);
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
            if (listener instanceof HistoryListener) ((HistoryListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof HistoryListener) ((HistoryListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof HistoryVisitor) return ((HistoryVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_file);

        ((FileContext) _localctx).historyCharts = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(44);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(41);
                            ((FileContext) _localctx).history = history();

                            _localctx.historyCharts.add(((FileContext) _localctx).history.historyChart);

                        }
                    }
                    setState(46);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == EOL || _la == CHART);
                setState(48);
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
            "\u0004\u0001\u00073\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0002\u0005\u0002\u0018\b\u0002\n\u0002\f\u0002\u001b\t\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0002\u0004\u0002!\b\u0002\u000b\u0002" +
                    "\f\u0002\"\u0001\u0002\u0004\u0002&\b\u0002\u000b\u0002\f\u0002\'\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0004\u0003-\b\u0003\u000b\u0003\f\u0003" +
                    ".\u0001\u0003\u0001\u0003\u0001\u0003\u0000\u0000\u0004\u0000\u0002\u0004" +
                    "\u0006\u0000\u00002\u0000\b\u0001\u0000\u0000\u0000\u0002\u0011\u0001" +
                    "\u0000\u0000\u0000\u0004\u0019\u0001\u0000\u0000\u0000\u0006,\u0001\u0000" +
                    "\u0000\u0000\b\t\u0005\u0003\u0000\u0000\t\n\u0005\u0006\u0000\u0000\n" +
                    "\u000b\u0005\u0005\u0000\u0000\u000b\f\u0005\u0006\u0000\u0000\f\r\u0005" +
                    "\u0005\u0000\u0000\r\u000e\u0005\u0006\u0000\u0000\u000e\u000f\u0005\u0002" +
                    "\u0000\u0000\u000f\u0010\u0006\u0000\uffff\uffff\u0000\u0010\u0001\u0001" +
                    "\u0000\u0000\u0000\u0011\u0012\u0005\u0004\u0000\u0000\u0012\u0013\u0005" +
                    "\u0007\u0000\u0000\u0013\u0014\u0005\u0002\u0000\u0000\u0014\u0015\u0006" +
                    "\u0001\uffff\uffff\u0000\u0015\u0003\u0001\u0000\u0000\u0000\u0016\u0018" +
                    "\u0005\u0002\u0000\u0000\u0017\u0016\u0001\u0000\u0000\u0000\u0018\u001b" +
                    "\u0001\u0000\u0000\u0000\u0019\u0017\u0001\u0000\u0000\u0000\u0019\u001a" +
                    "\u0001\u0000\u0000\u0000\u001a \u0001\u0000\u0000\u0000\u001b\u0019\u0001" +
                    "\u0000\u0000\u0000\u001c\u001d\u0003\u0000\u0000\u0000\u001d\u001e\u0003" +
                    "\u0002\u0001\u0000\u001e\u001f\u0006\u0002\uffff\uffff\u0000\u001f!\u0001" +
                    "\u0000\u0000\u0000 \u001c\u0001\u0000\u0000\u0000!\"\u0001\u0000\u0000" +
                    "\u0000\" \u0001\u0000\u0000\u0000\"#\u0001\u0000\u0000\u0000#%\u0001\u0000" +
                    "\u0000\u0000$&\u0005\u0002\u0000\u0000%$\u0001\u0000\u0000\u0000&\'\u0001" +
                    "\u0000\u0000\u0000\'%\u0001\u0000\u0000\u0000\'(\u0001\u0000\u0000\u0000" +
                    "(\u0005\u0001\u0000\u0000\u0000)*\u0003\u0004\u0002\u0000*+\u0006\u0003" +
                    "\uffff\uffff\u0000+-\u0001\u0000\u0000\u0000,)\u0001\u0000\u0000\u0000" +
                    "-.\u0001\u0000\u0000\u0000.,\u0001\u0000\u0000\u0000./\u0001\u0000\u0000" +
                    "\u0000/0\u0001\u0000\u0000\u000001\u0005\u0000\u0000\u00011\u0007\u0001" +
                    "\u0000\u0000\u0000\u0004\u0019\"\'.";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}