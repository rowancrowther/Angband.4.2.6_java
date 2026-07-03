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

// Generated from src/main/java/uk/co/jackoftrades/backend/parser/grammars/UIEntryBaseGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.uientrybase;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.parser.uientrybase.UIEntryBaseParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class UIEntryBaseGrammar extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, NAME = 2, RENDERER = 3, COMBINE = 4, CATEGORY = 5, FLAGS = 6, DESC = 7,
            INTEGER = 8, LCASE = 9, COMMENT = 10, EOL = 11, FLAG = 12, OR = 13, FLAG_EOL = 14, STRING = 15;
    public static final int
            RULE_recordCount = 0, RULE_name = 1, RULE_renderer = 2, RULE_combine = 3,
            RULE_category = 4, RULE_flags = 5, RULE_desc = 6, RULE_entryBase = 7,
            RULE_file = 8;

    private static String[] makeRuleNames() {
        return new String[]{
                "recordCount", "name", "renderer", "combine", "category", "flags", "desc",
                "entryBase", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'name:'", "'renderer:'", "'combine:'", "'category:'",
                "'flags:'", "'desc:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "NAME", "RENDERER", "COMBINE", "CATEGORY", "FLAGS",
                "DESC", "INTEGER", "LCASE", "COMMENT", "EOL", "FLAG", "OR", "FLAG_EOL",
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
        return "UIEntryBaseGrammar.g4";
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

    public UIEntryBaseGrammar(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RecordCountContext extends ParserRuleContext {
        public String count;
        public Token INTEGER;

        public TerminalNode RECORD_COUNT() {
            return getToken(UIEntryBaseGrammar.RECORD_COUNT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(UIEntryBaseGrammar.INTEGER, 0);
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
            if (listener instanceof UIEntryBaseGrammarListener)
                ((UIEntryBaseGrammarListener) listener).enterRecordCount(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseGrammarListener)
                ((UIEntryBaseGrammarListener) listener).exitRecordCount(this);
        }
    }

    public final RecordCountContext recordCount() throws RecognitionException {
        RecordCountContext _localctx = new RecordCountContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_recordCount);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(18);
                match(RECORD_COUNT);
                setState(19);
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
    public static class NameContext extends ParserRuleContext {
        public String nameStr;
        public Token LCASE;

        public TerminalNode NAME() {
            return getToken(UIEntryBaseGrammar.NAME, 0);
        }

        public TerminalNode LCASE() {
            return getToken(UIEntryBaseGrammar.LCASE, 0);
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
            if (listener instanceof UIEntryBaseGrammarListener) ((UIEntryBaseGrammarListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseGrammarListener) ((UIEntryBaseGrammarListener) listener).exitName(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(22);
                match(NAME);
                setState(23);
                ((NameContext) _localctx).LCASE = match(LCASE);
                ((NameContext) _localctx).nameStr = ((NameContext) _localctx).LCASE.getText();
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
    public static class RendererContext extends ParserRuleContext {
        public String entryRenderer;
        public Token LCASE;

        public TerminalNode RENDERER() {
            return getToken(UIEntryBaseGrammar.RENDERER, 0);
        }

        public TerminalNode LCASE() {
            return getToken(UIEntryBaseGrammar.LCASE, 0);
        }

        public RendererContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_renderer;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseGrammarListener)
                ((UIEntryBaseGrammarListener) listener).enterRenderer(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseGrammarListener)
                ((UIEntryBaseGrammarListener) listener).exitRenderer(this);
        }
    }

    public final RendererContext renderer() throws RecognitionException {
        RendererContext _localctx = new RendererContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_renderer);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(26);
                match(RENDERER);
                setState(27);
                ((RendererContext) _localctx).LCASE = match(LCASE);

                ((RendererContext) _localctx).entryRenderer = ((RendererContext) _localctx).LCASE.getText();

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
    public static class CombineContext extends ParserRuleContext {
        public String combinerEnum;
        public Token FLAG;

        public TerminalNode COMBINE() {
            return getToken(UIEntryBaseGrammar.COMBINE, 0);
        }

        public TerminalNode FLAG() {
            return getToken(UIEntryBaseGrammar.FLAG, 0);
        }

        public CombineContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_combine;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseGrammarListener)
                ((UIEntryBaseGrammarListener) listener).enterCombine(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseGrammarListener)
                ((UIEntryBaseGrammarListener) listener).exitCombine(this);
        }
    }

    public final CombineContext combine() throws RecognitionException {
        CombineContext _localctx = new CombineContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_combine);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(30);
                match(COMBINE);
                setState(31);
                ((CombineContext) _localctx).FLAG = match(FLAG);

                ((CombineContext) _localctx).combinerEnum = ((CombineContext) _localctx).FLAG.getText();

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
    public static class CategoryContext extends ParserRuleContext {
        public List<String> categoryStr;
        public Token STRING;

        public List<TerminalNode> CATEGORY() {
            return getTokens(UIEntryBaseGrammar.CATEGORY);
        }

        public TerminalNode CATEGORY(int i) {
            return getToken(UIEntryBaseGrammar.CATEGORY, i);
        }

        public List<TerminalNode> STRING() {
            return getTokens(UIEntryBaseGrammar.STRING);
        }

        public TerminalNode STRING(int i) {
            return getToken(UIEntryBaseGrammar.STRING, i);
        }

        public CategoryContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_category;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseGrammarListener)
                ((UIEntryBaseGrammarListener) listener).enterCategory(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseGrammarListener)
                ((UIEntryBaseGrammarListener) listener).exitCategory(this);
        }
    }

    public final CategoryContext category() throws RecognitionException {
        CategoryContext _localctx = new CategoryContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_category);

        ((CategoryContext) _localctx).categoryStr = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(37);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(34);
                            match(CATEGORY);
                            setState(35);
                            ((CategoryContext) _localctx).STRING = match(STRING);
                            _localctx.categoryStr.add(((CategoryContext) _localctx).STRING.getText());
                        }
                    }
                    setState(39);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == CATEGORY);
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
        public String flagsStr;
        public Token FLAG;

        public TerminalNode FLAGS() {
            return getToken(UIEntryBaseGrammar.FLAGS, 0);
        }

        public TerminalNode FLAG() {
            return getToken(UIEntryBaseGrammar.FLAG, 0);
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
            if (listener instanceof UIEntryBaseGrammarListener)
                ((UIEntryBaseGrammarListener) listener).enterFlags(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseGrammarListener) ((UIEntryBaseGrammarListener) listener).exitFlags(this);
        }
    }

    public final FlagsContext flags() throws RecognitionException {
        FlagsContext _localctx = new FlagsContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_flags);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(41);
                match(FLAGS);
                setState(42);
                ((FlagsContext) _localctx).FLAG = match(FLAG);

                ((FlagsContext) _localctx).flagsStr = ((FlagsContext) _localctx).FLAG.getText();

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

        public List<TerminalNode> DESC() {
            return getTokens(UIEntryBaseGrammar.DESC);
        }

        public TerminalNode DESC(int i) {
            return getToken(UIEntryBaseGrammar.DESC, i);
        }

        public List<TerminalNode> STRING() {
            return getTokens(UIEntryBaseGrammar.STRING);
        }

        public TerminalNode STRING(int i) {
            return getToken(UIEntryBaseGrammar.STRING, i);
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
            if (listener instanceof UIEntryBaseGrammarListener) ((UIEntryBaseGrammarListener) listener).enterDesc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseGrammarListener) ((UIEntryBaseGrammarListener) listener).exitDesc(this);
        }
    }

    public final DescContext desc() throws RecognitionException {
        DescContext _localctx = new DescContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_desc);
        StringBuilder sb = new StringBuilder();
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(48);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(45);
                            match(DESC);
                            setState(46);
                            ((DescContext) _localctx).STRING = match(STRING);
                            sb.append(((DescContext) _localctx).STRING.getText());
                        }
                    }
                    setState(50);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == DESC);
            }
            _ctx.stop = _input.LT(-1);
            ((DescContext) _localctx).descStr = sb.toString();
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
    public static class EntryBaseContext extends ParserRuleContext {
        public UIEntryBaseParseRecord base;
        public NameContext name;
        public RendererContext renderer;
        public CombineContext combine;
        public CategoryContext category;
        public FlagsContext flags;
        public DescContext desc;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public RendererContext renderer() {
            return getRuleContext(RendererContext.class, 0);
        }

        public CombineContext combine() {
            return getRuleContext(CombineContext.class, 0);
        }

        public CategoryContext category() {
            return getRuleContext(CategoryContext.class, 0);
        }

        public FlagsContext flags() {
            return getRuleContext(FlagsContext.class, 0);
        }

        public DescContext desc() {
            return getRuleContext(DescContext.class, 0);
        }

        public EntryBaseContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_entryBase;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseGrammarListener)
                ((UIEntryBaseGrammarListener) listener).enterEntryBase(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseGrammarListener)
                ((UIEntryBaseGrammarListener) listener).exitEntryBase(this);
        }
    }

    public final EntryBaseContext entryBase() throws RecognitionException {
        EntryBaseContext _localctx = new EntryBaseContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_entryBase);

        String nameInit = "";
        String rendererInit = "";
        String combinerInit = "";
        List<String> categoryInit = new ArrayList<>();
        String flagsInit = "";
        String descInit = "";
        int lineNumber = 0;

        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(52);
                ((EntryBaseContext) _localctx).name = name();

                nameInit = ((EntryBaseContext) _localctx).name.nameStr;
                lineNumber = _localctx.start.getLine();

                setState(54);
                ((EntryBaseContext) _localctx).renderer = renderer();

                rendererInit = ((EntryBaseContext) _localctx).renderer.entryRenderer;

                setState(56);
                ((EntryBaseContext) _localctx).combine = combine();

                combinerInit = ((EntryBaseContext) _localctx).combine.combinerEnum;

                setState(58);
                ((EntryBaseContext) _localctx).category = category();

                categoryInit = ((EntryBaseContext) _localctx).category.categoryStr;

                setState(60);
                ((EntryBaseContext) _localctx).flags = flags();

                flagsInit = ((EntryBaseContext) _localctx).flags.flagsStr;

                setState(62);
                ((EntryBaseContext) _localctx).desc = desc();

                descInit = ((EntryBaseContext) _localctx).desc.descStr;

            }
            _ctx.stop = _input.LT(-1);

            ((EntryBaseContext) _localctx).base = new UIEntryBaseParseRecord(
                    nameInit, rendererInit,
                    combinerInit, flagsInit,
                    descInit, categoryInit,
                    lineNumber);

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
        public List<UIEntryBaseParseRecord> uiEntryBases;
        public String declaredCount;
        public RecordCountContext recordCount;
        public EntryBaseContext entryBase;

        public RecordCountContext recordCount() {
            return getRuleContext(RecordCountContext.class, 0);
        }

        public TerminalNode EOF() {
            return getToken(UIEntryBaseGrammar.EOF, 0);
        }

        public List<EntryBaseContext> entryBase() {
            return getRuleContexts(EntryBaseContext.class);
        }

        public EntryBaseContext entryBase(int i) {
            return getRuleContext(EntryBaseContext.class, i);
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
            if (listener instanceof UIEntryBaseGrammarListener) ((UIEntryBaseGrammarListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseGrammarListener) ((UIEntryBaseGrammarListener) listener).exitFile(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_file);

        ((FileContext) _localctx).uiEntryBases = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(65);
                ((FileContext) _localctx).recordCount = recordCount();
                ((FileContext) _localctx).declaredCount = ((FileContext) _localctx).recordCount.count;
                setState(70);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(67);
                            ((FileContext) _localctx).entryBase = entryBase();

                            _localctx.uiEntryBases.add(((FileContext) _localctx).entryBase.base);

                        }
                    }
                    setState(72);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(74);
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
            "\u0004\u0001\u000fM\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002" +
                    "\b\u0007\b\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0004\u0004&\b\u0004\u000b\u0004\f\u0004\'\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0004\u00061\b\u0006\u000b\u0006\f\u00062\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0004\bG\b\b\u000b\b\f\bH\u0001\b\u0001\b\u0001" +
                    "\b\u0000\u0000\t\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0000\u0000" +
                    "F\u0000\u0012\u0001\u0000\u0000\u0000\u0002\u0016\u0001\u0000\u0000\u0000" +
                    "\u0004\u001a\u0001\u0000\u0000\u0000\u0006\u001e\u0001\u0000\u0000\u0000" +
                    "\b%\u0001\u0000\u0000\u0000\n)\u0001\u0000\u0000\u0000\f0\u0001\u0000" +
                    "\u0000\u0000\u000e4\u0001\u0000\u0000\u0000\u0010A\u0001\u0000\u0000\u0000" +
                    "\u0012\u0013\u0005\u0001\u0000\u0000\u0013\u0014\u0005\b\u0000\u0000\u0014" +
                    "\u0015\u0006\u0000\uffff\uffff\u0000\u0015\u0001\u0001\u0000\u0000\u0000" +
                    "\u0016\u0017\u0005\u0002\u0000\u0000\u0017\u0018\u0005\t\u0000\u0000\u0018" +
                    "\u0019\u0006\u0001\uffff\uffff\u0000\u0019\u0003\u0001\u0000\u0000\u0000" +
                    "\u001a\u001b\u0005\u0003\u0000\u0000\u001b\u001c\u0005\t\u0000\u0000\u001c" +
                    "\u001d\u0006\u0002\uffff\uffff\u0000\u001d\u0005\u0001\u0000\u0000\u0000" +
                    "\u001e\u001f\u0005\u0004\u0000\u0000\u001f \u0005\f\u0000\u0000 !\u0006" +
                    "\u0003\uffff\uffff\u0000!\u0007\u0001\u0000\u0000\u0000\"#\u0005\u0005" +
                    "\u0000\u0000#$\u0005\u000f\u0000\u0000$&\u0006\u0004\uffff\uffff\u0000" +
                    "%\"\u0001\u0000\u0000\u0000&\'\u0001\u0000\u0000\u0000\'%\u0001\u0000" +
                    "\u0000\u0000\'(\u0001\u0000\u0000\u0000(\t\u0001\u0000\u0000\u0000)*\u0005" +
                    "\u0006\u0000\u0000*+\u0005\f\u0000\u0000+,\u0006\u0005\uffff\uffff\u0000" +
                    ",\u000b\u0001\u0000\u0000\u0000-.\u0005\u0007\u0000\u0000./\u0005\u000f" +
                    "\u0000\u0000/1\u0006\u0006\uffff\uffff\u00000-\u0001\u0000\u0000\u0000" +
                    "12\u0001\u0000\u0000\u000020\u0001\u0000\u0000\u000023\u0001\u0000\u0000" +
                    "\u00003\r\u0001\u0000\u0000\u000045\u0003\u0002\u0001\u000056\u0006\u0007" +
                    "\uffff\uffff\u000067\u0003\u0004\u0002\u000078\u0006\u0007\uffff\uffff" +
                    "\u000089\u0003\u0006\u0003\u00009:\u0006\u0007\uffff\uffff\u0000:;\u0003" +
                    "\b\u0004\u0000;<\u0006\u0007\uffff\uffff\u0000<=\u0003\n\u0005\u0000=" +
                    ">\u0006\u0007\uffff\uffff\u0000>?\u0003\f\u0006\u0000?@\u0006\u0007\uffff" +
                    "\uffff\u0000@\u000f\u0001\u0000\u0000\u0000AB\u0003\u0000\u0000\u0000" +
                    "BF\u0006\b\uffff\uffff\u0000CD\u0003\u000e\u0007\u0000DE\u0006\b\uffff" +
                    "\uffff\u0000EG\u0001\u0000\u0000\u0000FC\u0001\u0000\u0000\u0000GH\u0001" +
                    "\u0000\u0000\u0000HF\u0001\u0000\u0000\u0000HI\u0001\u0000\u0000\u0000" +
                    "IJ\u0001\u0000\u0000\u0000JK\u0005\u0000\u0000\u0001K\u0011\u0001\u0000" +
                    "\u0000\u0000\u0003\'2H";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}