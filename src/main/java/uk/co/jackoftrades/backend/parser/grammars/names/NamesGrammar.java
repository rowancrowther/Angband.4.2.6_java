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
// Generated from NamesGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.names;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.parser.names.NamesParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class NamesGrammar extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, SECTION = 2, WORD = 3, INTEGER = 4, COMMENT = 5, EOL = 6, STRING = 7;
    public static final int
            RULE_recordCount = 0, RULE_section = 1, RULE_word = 2, RULE_record = 3,
            RULE_file = 4;

    private static String[] makeRuleNames() {
        return new String[]{
                "recordCount", "section", "word", "record", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'section:'", "'word:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "SECTION", "WORD", "INTEGER", "COMMENT", "EOL",
                "STRING"
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
        return "NamesGrammar.g4";
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

    public NamesGrammar(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RecordCountContext extends ParserRuleContext {
        public String count;
        public Token INTEGER;

        public TerminalNode RECORD_COUNT() {
            return getToken(NamesGrammar.RECORD_COUNT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(NamesGrammar.INTEGER, 0);
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
            if (listener instanceof NamesGrammarListener) ((NamesGrammarListener) listener).enterRecordCount(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NamesGrammarListener) ((NamesGrammarListener) listener).exitRecordCount(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NamesGrammarVisitor)
                return ((NamesGrammarVisitor<? extends T>) visitor).visitRecordCount(this);
            else return visitor.visitChildren(this);
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
    public static class SectionContext extends ParserRuleContext {
        public String sectionNum;
        public Token INTEGER;

        public TerminalNode SECTION() {
            return getToken(NamesGrammar.SECTION, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(NamesGrammar.INTEGER, 0);
        }

        public SectionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_section;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NamesGrammarListener) ((NamesGrammarListener) listener).enterSection(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NamesGrammarListener) ((NamesGrammarListener) listener).exitSection(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NamesGrammarVisitor)
                return ((NamesGrammarVisitor<? extends T>) visitor).visitSection(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SectionContext section() throws RecognitionException {
        SectionContext _localctx = new SectionContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_section);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(14);
                match(SECTION);
                setState(15);
                ((SectionContext) _localctx).INTEGER = match(INTEGER);
                ((SectionContext) _localctx).sectionNum = ((SectionContext) _localctx).INTEGER.getText();
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
    public static class WordContext extends ParserRuleContext {
        public String wordVal;
        public Token STRING;

        public TerminalNode WORD() {
            return getToken(NamesGrammar.WORD, 0);
        }

        public TerminalNode STRING() {
            return getToken(NamesGrammar.STRING, 0);
        }

        public WordContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_word;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof NamesGrammarListener) ((NamesGrammarListener) listener).enterWord(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NamesGrammarListener) ((NamesGrammarListener) listener).exitWord(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NamesGrammarVisitor)
                return ((NamesGrammarVisitor<? extends T>) visitor).visitWord(this);
            else return visitor.visitChildren(this);
        }
    }

    public final WordContext word() throws RecognitionException {
        WordContext _localctx = new WordContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_word);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(18);
                match(WORD);
                setState(19);
                ((WordContext) _localctx).STRING = match(STRING);
                ((WordContext) _localctx).wordVal = ((WordContext) _localctx).STRING.getText();
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
        public NamesParseRecord rec;
        public SectionContext section;
        public WordContext word;

        public SectionContext section() {
            return getRuleContext(SectionContext.class, 0);
        }

        public List<WordContext> word() {
            return getRuleContexts(WordContext.class);
        }

        public WordContext word(int i) {
            return getRuleContext(WordContext.class, i);
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
            if (listener instanceof NamesGrammarListener) ((NamesGrammarListener) listener).enterRecord(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NamesGrammarListener) ((NamesGrammarListener) listener).exitRecord(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NamesGrammarVisitor)
                return ((NamesGrammarVisitor<? extends T>) visitor).visitRecord(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RecordContext record() throws RecognitionException {
        RecordContext _localctx = new RecordContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_record);
        List<String> wordList = new ArrayList<>();
        String sec = "";
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(22);
                ((RecordContext) _localctx).section = section();
                sec = ((RecordContext) _localctx).section.sectionNum;
                setState(27);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(24);
                            ((RecordContext) _localctx).word = word();
                            wordList.add(((RecordContext) _localctx).word.wordVal);
                        }
                    }
                    setState(29);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == WORD);
            }
            _ctx.stop = _input.LT(-1);

            ((RecordContext) _localctx).rec = new NamesParseRecord(sec, wordList);

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
        public String declaredRecordCount;
        public List<NamesParseRecord> records;
        public RecordCountContext recordCount;
        public RecordContext record;

        public RecordCountContext recordCount() {
            return getRuleContext(RecordCountContext.class, 0);
        }

        public TerminalNode EOF() {
            return getToken(NamesGrammar.EOF, 0);
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
            if (listener instanceof NamesGrammarListener) ((NamesGrammarListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof NamesGrammarListener) ((NamesGrammarListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof NamesGrammarVisitor)
                return ((NamesGrammarVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_file);
        ((FileContext) _localctx).records = new ArrayList<>();
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(31);
                ((FileContext) _localctx).recordCount = recordCount();
                ((FileContext) _localctx).declaredRecordCount = ((FileContext) _localctx).recordCount.count;
                setState(36);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(33);
                            ((FileContext) _localctx).record = record();
                            _localctx.records.add(((FileContext) _localctx).record.rec);
                        }
                    }
                    setState(38);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == SECTION);
                setState(40);
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
            "\u0004\u0001\u0007+\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0004\u0003\u001c" +
                    "\b\u0003\u000b\u0003\f\u0003\u001d\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0004\u0004%\b\u0004\u000b\u0004\f\u0004&\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0000\u0000\u0005\u0000\u0002\u0004\u0006" +
                    "\b\u0000\u0000\'\u0000\n\u0001\u0000\u0000\u0000\u0002\u000e\u0001\u0000" +
                    "\u0000\u0000\u0004\u0012\u0001\u0000\u0000\u0000\u0006\u0016\u0001\u0000" +
                    "\u0000\u0000\b\u001f\u0001\u0000\u0000\u0000\n\u000b\u0005\u0001\u0000" +
                    "\u0000\u000b\f\u0005\u0004\u0000\u0000\f\r\u0006\u0000\uffff\uffff\u0000" +
                    "\r\u0001\u0001\u0000\u0000\u0000\u000e\u000f\u0005\u0002\u0000\u0000\u000f" +
                    "\u0010\u0005\u0004\u0000\u0000\u0010\u0011\u0006\u0001\uffff\uffff\u0000" +
                    "\u0011\u0003\u0001\u0000\u0000\u0000\u0012\u0013\u0005\u0003\u0000\u0000" +
                    "\u0013\u0014\u0005\u0007\u0000\u0000\u0014\u0015\u0006\u0002\uffff\uffff" +
                    "\u0000\u0015\u0005\u0001\u0000\u0000\u0000\u0016\u0017\u0003\u0002\u0001" +
                    "\u0000\u0017\u001b\u0006\u0003\uffff\uffff\u0000\u0018\u0019\u0003\u0004" +
                    "\u0002\u0000\u0019\u001a\u0006\u0003\uffff\uffff\u0000\u001a\u001c\u0001" +
                    "\u0000\u0000\u0000\u001b\u0018\u0001\u0000\u0000\u0000\u001c\u001d\u0001" +
                    "\u0000\u0000\u0000\u001d\u001b\u0001\u0000\u0000\u0000\u001d\u001e\u0001" +
                    "\u0000\u0000\u0000\u001e\u0007\u0001\u0000\u0000\u0000\u001f \u0003\u0000" +
                    "\u0000\u0000 $\u0006\u0004\uffff\uffff\u0000!\"\u0003\u0006\u0003\u0000" +
                    "\"#\u0006\u0004\uffff\uffff\u0000#%\u0001\u0000\u0000\u0000$!\u0001\u0000" +
                    "\u0000\u0000%&\u0001\u0000\u0000\u0000&$\u0001\u0000\u0000\u0000&\'\u0001" +
                    "\u0000\u0000\u0000\'(\u0001\u0000\u0000\u0000()\u0005\u0000\u0000\u0001" +
                    ")\t\u0001\u0000\u0000\u0000\u0002\u001d&";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}