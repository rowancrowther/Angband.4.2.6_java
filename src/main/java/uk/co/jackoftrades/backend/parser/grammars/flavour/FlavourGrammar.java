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

// Generated from FlavourGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.flavour;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.parser.flavour.FlavourKindParseRecord;
import uk.co.jackoftrades.backend.parser.flavour.FlavourParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class FlavourGrammar extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, KIND = 2, FIXED = 3, FLAVOUR = 4, INTEGER = 5, COMMENT = 6, EOL = 7,
            STRING = 8, COLON = 9, DELIM_EOL = 10;
    public static final int
            RULE_recordCount = 0, RULE_kind = 1, RULE_fixed = 2, RULE_flavour = 3,
            RULE_fixedOrFlavourBlock = 4, RULE_section = 5, RULE_file = 6;

    private static String[] makeRuleNames() {
        return new String[]{
                "recordCount", "kind", "fixed", "flavour", "fixedOrFlavourBlock", "section",
                "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'kind:'", "'fixed:'", "'flavor:'", null, null,
                null, null, "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "KIND", "FIXED", "FLAVOUR", "INTEGER", "COMMENT",
                "EOL", "STRING", "COLON", "DELIM_EOL"
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
        return "FlavourGrammar.g4";
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

    public FlavourGrammar(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RecordCountContext extends ParserRuleContext {
        public String count;
        public Token INTEGER;

        public TerminalNode RECORD_COUNT() {
            return getToken(FlavourGrammar.RECORD_COUNT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(FlavourGrammar.INTEGER, 0);
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
            if (listener instanceof FlavourGrammarListener) ((FlavourGrammarListener) listener).enterRecordCount(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof FlavourGrammarListener) ((FlavourGrammarListener) listener).exitRecordCount(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof FlavourGrammarVisitor)
                return ((FlavourGrammarVisitor<? extends T>) visitor).visitRecordCount(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RecordCountContext recordCount() throws RecognitionException {
        RecordCountContext _localctx = new RecordCountContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_recordCount);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(14);
                match(RECORD_COUNT);
                setState(15);
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
    public static class KindContext extends ParserRuleContext {
        public String tVal;
        public String glyph;
        public Token t;
        public Token g;

        public TerminalNode KIND() {
            return getToken(FlavourGrammar.KIND, 0);
        }

        public TerminalNode COLON() {
            return getToken(FlavourGrammar.COLON, 0);
        }

        public List<TerminalNode> STRING() {
            return getTokens(FlavourGrammar.STRING);
        }

        public TerminalNode STRING(int i) {
            return getToken(FlavourGrammar.STRING, i);
        }

        public KindContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_kind;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof FlavourGrammarListener) ((FlavourGrammarListener) listener).enterKind(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof FlavourGrammarListener) ((FlavourGrammarListener) listener).exitKind(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof FlavourGrammarVisitor)
                return ((FlavourGrammarVisitor<? extends T>) visitor).visitKind(this);
            else return visitor.visitChildren(this);
        }
    }

    public final KindContext kind() throws RecognitionException {
        KindContext _localctx = new KindContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_kind);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(18);
                match(KIND);
                setState(19);
                ((KindContext) _localctx).t = match(STRING);
                setState(20);
                match(COLON);
                setState(21);
                ((KindContext) _localctx).g = match(STRING);
                ((KindContext) _localctx).tVal = ((KindContext) _localctx).t.getText();
                ((KindContext) _localctx).glyph = ((KindContext) _localctx).g.getText();
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
    public static class FixedContext extends ParserRuleContext {
        public String index;
        public String sval;
        public String colour;
        public String text;
        public Token i;
        public Token s;
        public Token c;
        public Token t;

        public TerminalNode FIXED() {
            return getToken(FlavourGrammar.FIXED, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(FlavourGrammar.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(FlavourGrammar.COLON, i);
        }

        public List<TerminalNode> STRING() {
            return getTokens(FlavourGrammar.STRING);
        }

        public TerminalNode STRING(int i) {
            return getToken(FlavourGrammar.STRING, i);
        }

        public FixedContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_fixed;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof FlavourGrammarListener) ((FlavourGrammarListener) listener).enterFixed(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof FlavourGrammarListener) ((FlavourGrammarListener) listener).exitFixed(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof FlavourGrammarVisitor)
                return ((FlavourGrammarVisitor<? extends T>) visitor).visitFixed(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FixedContext fixed() throws RecognitionException {
        FixedContext _localctx = new FixedContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_fixed);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(24);
                match(FIXED);
                setState(25);
                ((FixedContext) _localctx).i = match(STRING);
                setState(26);
                match(COLON);
                setState(27);
                ((FixedContext) _localctx).s = match(STRING);
                setState(28);
                match(COLON);
                setState(29);
                ((FixedContext) _localctx).c = match(STRING);
                setState(30);
                match(COLON);
                setState(31);
                ((FixedContext) _localctx).t = match(STRING);
                ((FixedContext) _localctx).index = ((FixedContext) _localctx).i.getText();
                ((FixedContext) _localctx).sval = ((FixedContext) _localctx).s.getText();
                ((FixedContext) _localctx).colour = ((FixedContext) _localctx).c.getText();
                ((FixedContext) _localctx).text = ((FixedContext) _localctx).t.getText();
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
    public static class FlavourContext extends ParserRuleContext {
        public String index;
        public String colour;
        public String text;
        public Token i;
        public Token c;
        public Token t;

        public TerminalNode FLAVOUR() {
            return getToken(FlavourGrammar.FLAVOUR, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(FlavourGrammar.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(FlavourGrammar.COLON, i);
        }

        public List<TerminalNode> STRING() {
            return getTokens(FlavourGrammar.STRING);
        }

        public TerminalNode STRING(int i) {
            return getToken(FlavourGrammar.STRING, i);
        }

        public FlavourContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_flavour;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof FlavourGrammarListener) ((FlavourGrammarListener) listener).enterFlavour(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof FlavourGrammarListener) ((FlavourGrammarListener) listener).exitFlavour(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof FlavourGrammarVisitor)
                return ((FlavourGrammarVisitor<? extends T>) visitor).visitFlavour(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FlavourContext flavour() throws RecognitionException {
        FlavourContext _localctx = new FlavourContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_flavour);
        ((FlavourContext) _localctx).text = null;
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(34);
                match(FLAVOUR);
                setState(35);
                ((FlavourContext) _localctx).i = match(STRING);
                setState(36);
                match(COLON);
                setState(37);
                ((FlavourContext) _localctx).c = match(STRING);
                ((FlavourContext) _localctx).index = ((FlavourContext) _localctx).i.getText();
                ((FlavourContext) _localctx).colour = ((FlavourContext) _localctx).c.getText();
                setState(42);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COLON) {
                    {
                        setState(39);
                        match(COLON);
                        setState(40);
                        ((FlavourContext) _localctx).t = match(STRING);
                        ((FlavourContext) _localctx).text = ((FlavourContext) _localctx).t.getText();
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
    public static class FixedOrFlavourBlockContext extends ParserRuleContext {
        public List<FlavourParseRecord> flavours;
        public FixedContext fixed;
        public FlavourContext flavour;

        public List<FixedContext> fixed() {
            return getRuleContexts(FixedContext.class);
        }

        public FixedContext fixed(int i) {
            return getRuleContext(FixedContext.class, i);
        }

        public List<FlavourContext> flavour() {
            return getRuleContexts(FlavourContext.class);
        }

        public FlavourContext flavour(int i) {
            return getRuleContext(FlavourContext.class, i);
        }

        public FixedOrFlavourBlockContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_fixedOrFlavourBlock;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof FlavourGrammarListener)
                ((FlavourGrammarListener) listener).enterFixedOrFlavourBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof FlavourGrammarListener)
                ((FlavourGrammarListener) listener).exitFixedOrFlavourBlock(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof FlavourGrammarVisitor)
                return ((FlavourGrammarVisitor<? extends T>) visitor).visitFixedOrFlavourBlock(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FixedOrFlavourBlockContext fixedOrFlavourBlock() throws RecognitionException {
        FixedOrFlavourBlockContext _localctx = new FixedOrFlavourBlockContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_fixedOrFlavourBlock);
        ((FixedOrFlavourBlockContext) _localctx).flavours = new ArrayList<>();
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(50);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        setState(50);
                        _errHandler.sync(this);
                        switch (_input.LA(1)) {
                            case FIXED: {
                                setState(44);
                                ((FixedOrFlavourBlockContext) _localctx).fixed = fixed();
                                _localctx.flavours.add(new FlavourParseRecord(((FixedOrFlavourBlockContext) _localctx).fixed.index,
                                        ((FixedOrFlavourBlockContext) _localctx).fixed.sval, ((FixedOrFlavourBlockContext) _localctx).fixed.colour, ((FixedOrFlavourBlockContext) _localctx).fixed.text,
                                        (((FixedOrFlavourBlockContext) _localctx).fixed != null ? (((FixedOrFlavourBlockContext) _localctx).fixed.start) : null).getLine()));
                            }
                            break;
                            case FLAVOUR: {
                                setState(47);
                                ((FixedOrFlavourBlockContext) _localctx).flavour = flavour();
                                _localctx.flavours.add(new FlavourParseRecord(((FixedOrFlavourBlockContext) _localctx).flavour.index,
                                        null, ((FixedOrFlavourBlockContext) _localctx).flavour.colour, ((FixedOrFlavourBlockContext) _localctx).flavour.text,
                                        (((FixedOrFlavourBlockContext) _localctx).flavour != null ? (((FixedOrFlavourBlockContext) _localctx).flavour.start) : null).getLine()));
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                    }
                    setState(52);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == FIXED || _la == FLAVOUR);
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
        public FlavourKindParseRecord kinds;
        public KindContext kind;
        public FixedOrFlavourBlockContext fixedOrFlavourBlock;

        public KindContext kind() {
            return getRuleContext(KindContext.class, 0);
        }

        public FixedOrFlavourBlockContext fixedOrFlavourBlock() {
            return getRuleContext(FixedOrFlavourBlockContext.class, 0);
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
            if (listener instanceof FlavourGrammarListener) ((FlavourGrammarListener) listener).enterSection(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof FlavourGrammarListener) ((FlavourGrammarListener) listener).exitSection(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof FlavourGrammarVisitor)
                return ((FlavourGrammarVisitor<? extends T>) visitor).visitSection(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SectionContext section() throws RecognitionException {
        SectionContext _localctx = new SectionContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_section);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(54);
                ((SectionContext) _localctx).kind = kind();
                setState(55);
                ((SectionContext) _localctx).fixedOrFlavourBlock = fixedOrFlavourBlock();

                ((SectionContext) _localctx).kinds = new FlavourKindParseRecord(((SectionContext) _localctx).kind.tVal, ((SectionContext) _localctx).kind.glyph,
                        ((SectionContext) _localctx).fixedOrFlavourBlock.flavours, (((SectionContext) _localctx).kind != null ? (((SectionContext) _localctx).kind.start) : null).getLine());

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
        public String declaredRecordCount;
        public List<FlavourKindParseRecord> sections;
        public RecordCountContext recordCount;
        public SectionContext section;

        public RecordCountContext recordCount() {
            return getRuleContext(RecordCountContext.class, 0);
        }

        public TerminalNode EOF() {
            return getToken(FlavourGrammar.EOF, 0);
        }

        public List<SectionContext> section() {
            return getRuleContexts(SectionContext.class);
        }

        public SectionContext section(int i) {
            return getRuleContext(SectionContext.class, i);
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
            if (listener instanceof FlavourGrammarListener) ((FlavourGrammarListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof FlavourGrammarListener) ((FlavourGrammarListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof FlavourGrammarVisitor)
                return ((FlavourGrammarVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_file);
        ((FileContext) _localctx).sections = new ArrayList<>();
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(58);
                ((FileContext) _localctx).recordCount = recordCount();
                ((FileContext) _localctx).declaredRecordCount = ((FileContext) _localctx).recordCount.count;
                setState(63);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(60);
                            ((FileContext) _localctx).section = section();
                            _localctx.sections.add(((FileContext) _localctx).section.kinds);
                        }
                    }
                    setState(65);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == KIND);
                setState(67);
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
            "\u0004\u0001\nF\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0003\u0003+\b\u0003\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0004\u00043\b\u0004\u000b" +
                    "\u0004\f\u00044\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0004\u0006@\b" +
                    "\u0006\u000b\u0006\f\u0006A\u0001\u0006\u0001\u0006\u0001\u0006\u0000" +
                    "\u0000\u0007\u0000\u0002\u0004\u0006\b\n\f\u0000\u0000B\u0000\u000e\u0001" +
                    "\u0000\u0000\u0000\u0002\u0012\u0001\u0000\u0000\u0000\u0004\u0018\u0001" +
                    "\u0000\u0000\u0000\u0006\"\u0001\u0000\u0000\u0000\b2\u0001\u0000\u0000" +
                    "\u0000\n6\u0001\u0000\u0000\u0000\f:\u0001\u0000\u0000\u0000\u000e\u000f" +
                    "\u0005\u0001\u0000\u0000\u000f\u0010\u0005\u0005\u0000\u0000\u0010\u0011" +
                    "\u0006\u0000\uffff\uffff\u0000\u0011\u0001\u0001\u0000\u0000\u0000\u0012" +
                    "\u0013\u0005\u0002\u0000\u0000\u0013\u0014\u0005\b\u0000\u0000\u0014\u0015" +
                    "\u0005\t\u0000\u0000\u0015\u0016\u0005\b\u0000\u0000\u0016\u0017\u0006" +
                    "\u0001\uffff\uffff\u0000\u0017\u0003\u0001\u0000\u0000\u0000\u0018\u0019" +
                    "\u0005\u0003\u0000\u0000\u0019\u001a\u0005\b\u0000\u0000\u001a\u001b\u0005" +
                    "\t\u0000\u0000\u001b\u001c\u0005\b\u0000\u0000\u001c\u001d\u0005\t\u0000" +
                    "\u0000\u001d\u001e\u0005\b\u0000\u0000\u001e\u001f\u0005\t\u0000\u0000" +
                    "\u001f \u0005\b\u0000\u0000 !\u0006\u0002\uffff\uffff\u0000!\u0005\u0001" +
                    "\u0000\u0000\u0000\"#\u0005\u0004\u0000\u0000#$\u0005\b\u0000\u0000$%" +
                    "\u0005\t\u0000\u0000%&\u0005\b\u0000\u0000&*\u0006\u0003\uffff\uffff\u0000" +
                    "\'(\u0005\t\u0000\u0000()\u0005\b\u0000\u0000)+\u0006\u0003\uffff\uffff" +
                    "\u0000*\'\u0001\u0000\u0000\u0000*+\u0001\u0000\u0000\u0000+\u0007\u0001" +
                    "\u0000\u0000\u0000,-\u0003\u0004\u0002\u0000-.\u0006\u0004\uffff\uffff" +
                    "\u0000.3\u0001\u0000\u0000\u0000/0\u0003\u0006\u0003\u000001\u0006\u0004" +
                    "\uffff\uffff\u000013\u0001\u0000\u0000\u00002,\u0001\u0000\u0000\u0000" +
                    "2/\u0001\u0000\u0000\u000034\u0001\u0000\u0000\u000042\u0001\u0000\u0000" +
                    "\u000045\u0001\u0000\u0000\u00005\t\u0001\u0000\u0000\u000067\u0003\u0002" +
                    "\u0001\u000078\u0003\b\u0004\u000089\u0006\u0005\uffff\uffff\u00009\u000b" +
                    "\u0001\u0000\u0000\u0000:;\u0003\u0000\u0000\u0000;?\u0006\u0006\uffff" +
                    "\uffff\u0000<=\u0003\n\u0005\u0000=>\u0006\u0006\uffff\uffff\u0000>@\u0001" +
                    "\u0000\u0000\u0000?<\u0001\u0000\u0000\u0000@A\u0001\u0000\u0000\u0000" +
                    "A?\u0001\u0000\u0000\u0000AB\u0001\u0000\u0000\u0000BC\u0001\u0000\u0000" +
                    "\u0000CD\u0005\u0000\u0000\u0001D\r\u0001\u0000\u0000\u0000\u0004*24A";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}