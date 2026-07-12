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

// Generated from HistoryGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.history;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.parser.history.HistoryParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class HistoryGrammar extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, CHART = 2, PHRASE = 3, INTEGER = 4, COLON = 5, COMMENT = 6, EOL = 7,
            STRING = 8, STRING_EOL = 9;
    public static final int
            RULE_recordCount = 0, RULE_chart = 1, RULE_phrase = 2, RULE_record = 3,
            RULE_file = 4;

    private static String[] makeRuleNames() {
        return new String[]{
                "recordCount", "chart", "phrase", "record", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'chart:'", "'phrase:'", null, "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "CHART", "PHRASE", "INTEGER", "COLON", "COMMENT",
                "EOL", "STRING", "STRING_EOL"
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
        return "HistoryGrammar.g4";
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

    public HistoryGrammar(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RecordCountContext extends ParserRuleContext {
        public String count;
        public Token INTEGER;

        public TerminalNode RECORD_COUNT() {
            return getToken(HistoryGrammar.RECORD_COUNT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(HistoryGrammar.INTEGER, 0);
        }

        public RecordCountContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_recordCount;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof HistoryGrammarListener) ((HistoryGrammarListener) listener).enterRecordCount(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof HistoryGrammarListener) ((HistoryGrammarListener) listener).exitRecordCount(this);
        }
    }

    public final RecordCountContext recordCount() throws RecognitionException {
        RecordCountContext _localctx = new RecordCountContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_recordCount);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(10);
                match(RECORD_COUNT);
                setState(11);
                ((RecordCountContext) _localctx).INTEGER = match(INTEGER);
                ((RecordCountContext) _localctx).count = ((RecordCountContext) _localctx).INTEGER.getText();
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
    public static class ChartContext extends ParserRuleContext {
        public String currentChart;
        public String nextChart;
        public String percent;
        public int line;
        public Token cc;
        public Token nc;
        public Token p;

        public TerminalNode CHART() {
            return getToken(HistoryGrammar.CHART, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(HistoryGrammar.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(HistoryGrammar.COLON, i);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(HistoryGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(HistoryGrammar.INTEGER, i);
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
            if (listener instanceof HistoryGrammarListener) ((HistoryGrammarListener) listener).enterChart(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof HistoryGrammarListener) ((HistoryGrammarListener) listener).exitChart(this);
        }
    }

    public final ChartContext chart() throws RecognitionException {
        ChartContext _localctx = new ChartContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_chart);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(14);
                match(CHART);
                setState(15);
                ((ChartContext) _localctx).cc = match(INTEGER);
                setState(16);
                match(COLON);
                setState(17);
                ((ChartContext) _localctx).nc = match(INTEGER);
                setState(18);
                match(COLON);
                setState(19);
                ((ChartContext) _localctx).p = match(INTEGER);

                ((ChartContext) _localctx).currentChart = ((ChartContext) _localctx).cc.getText();
                ((ChartContext) _localctx).nextChart = ((ChartContext) _localctx).nc.getText();
                ((ChartContext) _localctx).percent = ((ChartContext) _localctx).p.getText();
                ((ChartContext) _localctx).line = _localctx.start.getLine();

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
            return getToken(HistoryGrammar.PHRASE, 0);
        }

        public TerminalNode STRING() {
            return getToken(HistoryGrammar.STRING, 0);
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
            if (listener instanceof HistoryGrammarListener) ((HistoryGrammarListener) listener).enterPhrase(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof HistoryGrammarListener) ((HistoryGrammarListener) listener).exitPhrase(this);
        }
    }

    public final PhraseContext phrase() throws RecognitionException {
        PhraseContext _localctx = new PhraseContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_phrase);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(22);
                match(PHRASE);
                setState(23);
                ((PhraseContext) _localctx).STRING = match(STRING);
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
    public static class RecordContext extends ParserRuleContext {
        public HistoryParseRecord hist;
        public ChartContext chart;
        public PhraseContext phrase;

        public ChartContext chart() {
            return getRuleContext(ChartContext.class, 0);
        }

        public PhraseContext phrase() {
            return getRuleContext(PhraseContext.class, 0);
        }

        public RecordContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_record;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof HistoryGrammarListener) ((HistoryGrammarListener) listener).enterRecord(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof HistoryGrammarListener) ((HistoryGrammarListener) listener).exitRecord(this);
        }
    }

    public final RecordContext record() throws RecognitionException {
        RecordContext _localctx = new RecordContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_record);

        String currentInit = "";
        String nextInit = "";
        String percentInit = "";
        String phraseInit = "";
        int lineInit = 0;

        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(26);
                ((RecordContext) _localctx).chart = chart();

                currentInit = ((RecordContext) _localctx).chart.currentChart;
                nextInit = ((RecordContext) _localctx).chart.nextChart;
                percentInit = ((RecordContext) _localctx).chart.percent;
                lineInit = ((RecordContext) _localctx).chart.line;

                setState(28);
                ((RecordContext) _localctx).phrase = phrase();
                phraseInit = ((RecordContext) _localctx).phrase.phraseStr;
            }
            _ctx.stop = _input.LT(-1);

            ((RecordContext) _localctx).hist = new HistoryParseRecord(currentInit,
                    nextInit, percentInit, phraseInit,
                    lineInit);

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
        public List<HistoryParseRecord> records;
        public String declaredRecordCount;
        public RecordCountContext recordCount;
        public RecordContext record;

        public RecordCountContext recordCount() {
            return getRuleContext(RecordCountContext.class, 0);
        }

        public TerminalNode EOF() {
            return getToken(HistoryGrammar.EOF, 0);
        }

        public List<RecordContext> record() {
            return getRuleContexts(RecordContext.class);
        }

        public RecordContext record(int i) {
            return getRuleContext(RecordContext.class, i);
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
            if (listener instanceof HistoryGrammarListener) ((HistoryGrammarListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof HistoryGrammarListener) ((HistoryGrammarListener) listener).exitFile(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_file);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {

                {
                    ((FileContext) _localctx).records = new ArrayList<>();
                }

                setState(32);
                ((FileContext) _localctx).recordCount = recordCount();
                ((FileContext) _localctx).declaredRecordCount = ((FileContext) _localctx).recordCount.count;
                setState(37);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(34);
                            ((FileContext) _localctx).record = record();
                            _localctx.records.add(((FileContext) _localctx).record.hist);
                        }
                    }
                    setState(39);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == CHART);
                setState(41);
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
            "\u0004\u0001\t,\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0004\u0004&\b\u0004\u000b\u0004\f\u0004" +
                    "\'\u0001\u0004\u0001\u0004\u0001\u0004\u0000\u0000\u0005\u0000\u0002\u0004" +
                    "\u0006\b\u0000\u0000\'\u0000\n\u0001\u0000\u0000\u0000\u0002\u000e\u0001" +
                    "\u0000\u0000\u0000\u0004\u0016\u0001\u0000\u0000\u0000\u0006\u001a\u0001" +
                    "\u0000\u0000\u0000\b\u001f\u0001\u0000\u0000\u0000\n\u000b\u0005\u0001" +
                    "\u0000\u0000\u000b\f\u0005\u0004\u0000\u0000\f\r\u0006\u0000\uffff\uffff" +
                    "\u0000\r\u0001\u0001\u0000\u0000\u0000\u000e\u000f\u0005\u0002\u0000\u0000" +
                    "\u000f\u0010\u0005\u0004\u0000\u0000\u0010\u0011\u0005\u0005\u0000\u0000" +
                    "\u0011\u0012\u0005\u0004\u0000\u0000\u0012\u0013\u0005\u0005\u0000\u0000" +
                    "\u0013\u0014\u0005\u0004\u0000\u0000\u0014\u0015\u0006\u0001\uffff\uffff" +
                    "\u0000\u0015\u0003\u0001\u0000\u0000\u0000\u0016\u0017\u0005\u0003\u0000" +
                    "\u0000\u0017\u0018\u0005\b\u0000\u0000\u0018\u0019\u0006\u0002\uffff\uffff" +
                    "\u0000\u0019\u0005\u0001\u0000\u0000\u0000\u001a\u001b\u0003\u0002\u0001" +
                    "\u0000\u001b\u001c\u0006\u0003\uffff\uffff\u0000\u001c\u001d\u0003\u0004" +
                    "\u0002\u0000\u001d\u001e\u0006\u0003\uffff\uffff\u0000\u001e\u0007\u0001" +
                    "\u0000\u0000\u0000\u001f \u0006\u0004\uffff\uffff\u0000 !\u0003\u0000" +
                    "\u0000\u0000!%\u0006\u0004\uffff\uffff\u0000\"#\u0003\u0006\u0003\u0000" +
                    "#$\u0006\u0004\uffff\uffff\u0000$&\u0001\u0000\u0000\u0000%\"\u0001\u0000" +
                    "\u0000\u0000&\'\u0001\u0000\u0000\u0000\'%\u0001\u0000\u0000\u0000\'(" +
                    "\u0001\u0000\u0000\u0000()\u0001\u0000\u0000\u0000)*\u0005\u0000\u0000" +
                    "\u0001*\t\u0001\u0000\u0000\u0000\u0001\'";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}