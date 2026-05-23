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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/GameConstants.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.gameconstants;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class GameConstantsParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            TOKEN = 1, FURTHER = 2, COMMENT = 3, COLON = 4, VALUE = 5, EOL = 6;
    public static final int
            RULE_section = 0, RULE_furtherValue = 1, RULE_multiValue = 2, RULE_line = 3,
            RULE_file = 4;

    private static String[] makeRuleNames() {
        return new String[]{
                "section", "furtherValue", "multiValue", "line", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, null, "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "TOKEN", "FURTHER", "COMMENT", "COLON", "VALUE", "EOL"
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
        return "GameConstants.g4";
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


    public record Entry(String key, String value) {
    }

    public GameConstantsParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class SectionContext extends ParserRuleContext {
        public String sect;
        public String value;
        public Token token1;
        public Token token2;
        public Token VALUE;

        public List<TerminalNode> COLON() {
            return getTokens(GameConstantsParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(GameConstantsParser.COLON, i);
        }

        public TerminalNode VALUE() {
            return getToken(GameConstantsParser.VALUE, 0);
        }

        public List<TerminalNode> TOKEN() {
            return getTokens(GameConstantsParser.TOKEN);
        }

        public TerminalNode TOKEN(int i) {
            return getToken(GameConstantsParser.TOKEN, i);
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
            if (listener instanceof GameConstantsListener) ((GameConstantsListener) listener).enterSection(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GameConstantsListener) ((GameConstantsListener) listener).exitSection(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GameConstantsVisitor)
                return ((GameConstantsVisitor<? extends T>) visitor).visitSection(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SectionContext section() throws RecognitionException {
        SectionContext _localctx = new SectionContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_section);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(10);
                ((SectionContext) _localctx).token1 = match(TOKEN);
                ((SectionContext) _localctx).sect = ((SectionContext) _localctx).token1.getText();
                setState(12);
                match(COLON);
                setState(13);
                ((SectionContext) _localctx).token2 = match(TOKEN);
                ((SectionContext) _localctx).value = ((SectionContext) _localctx).token2.getText();
                setState(15);
                match(COLON);
                setState(16);
                ((SectionContext) _localctx).VALUE = match(VALUE);
                ((SectionContext) _localctx).value = _localctx.value + ":" + ((SectionContext) _localctx).VALUE.getText();
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
    public static class FurtherValueContext extends ParserRuleContext {
        public String sect;
        public String further;
        public Token TOKEN;
        public Token val1;
        public Token val2;
        public Token FURTHER;

        public TerminalNode TOKEN() {
            return getToken(GameConstantsParser.TOKEN, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(GameConstantsParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(GameConstantsParser.COLON, i);
        }

        public TerminalNode FURTHER() {
            return getToken(GameConstantsParser.FURTHER, 0);
        }

        public List<TerminalNode> VALUE() {
            return getTokens(GameConstantsParser.VALUE);
        }

        public TerminalNode VALUE(int i) {
            return getToken(GameConstantsParser.VALUE, i);
        }

        public FurtherValueContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_furtherValue;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GameConstantsListener) ((GameConstantsListener) listener).enterFurtherValue(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GameConstantsListener) ((GameConstantsListener) listener).exitFurtherValue(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GameConstantsVisitor)
                return ((GameConstantsVisitor<? extends T>) visitor).visitFurtherValue(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FurtherValueContext furtherValue() throws RecognitionException {
        FurtherValueContext _localctx = new FurtherValueContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_furtherValue);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(19);
                ((FurtherValueContext) _localctx).TOKEN = match(TOKEN);
                ((FurtherValueContext) _localctx).sect = ((FurtherValueContext) _localctx).TOKEN.getText();
                setState(21);
                match(COLON);
                setState(22);
                ((FurtherValueContext) _localctx).val1 = match(VALUE);
                setState(23);
                match(COLON);
                setState(24);
                ((FurtherValueContext) _localctx).val2 = match(VALUE);
                setState(25);
                match(COLON);
                setState(26);
                ((FurtherValueContext) _localctx).FURTHER = match(FURTHER);

                ((FurtherValueContext) _localctx).further = ((FurtherValueContext) _localctx).val1.getText() + ":" + ((FurtherValueContext) _localctx).val2.getText() + ":" + ((FurtherValueContext) _localctx).FURTHER.getText();

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
    public static class MultiValueContext extends ParserRuleContext {
        public String sect;
        public String multi;
        public Token TOKEN;
        public Token val1;
        public Token val2;
        public Token val3;
        public Token FURTHER;

        public TerminalNode TOKEN() {
            return getToken(GameConstantsParser.TOKEN, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(GameConstantsParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(GameConstantsParser.COLON, i);
        }

        public TerminalNode FURTHER() {
            return getToken(GameConstantsParser.FURTHER, 0);
        }

        public List<TerminalNode> VALUE() {
            return getTokens(GameConstantsParser.VALUE);
        }

        public TerminalNode VALUE(int i) {
            return getToken(GameConstantsParser.VALUE, i);
        }

        public MultiValueContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_multiValue;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GameConstantsListener) ((GameConstantsListener) listener).enterMultiValue(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GameConstantsListener) ((GameConstantsListener) listener).exitMultiValue(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GameConstantsVisitor)
                return ((GameConstantsVisitor<? extends T>) visitor).visitMultiValue(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MultiValueContext multiValue() throws RecognitionException {
        MultiValueContext _localctx = new MultiValueContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_multiValue);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(29);
                ((MultiValueContext) _localctx).TOKEN = match(TOKEN);
                ((MultiValueContext) _localctx).sect = ((MultiValueContext) _localctx).TOKEN.getText();
                setState(31);
                match(COLON);
                setState(32);
                ((MultiValueContext) _localctx).val1 = match(VALUE);
                setState(33);
                match(COLON);
                setState(34);
                ((MultiValueContext) _localctx).val2 = match(VALUE);
                setState(35);
                match(COLON);
                setState(36);
                ((MultiValueContext) _localctx).val3 = match(VALUE);
                setState(37);
                match(COLON);
                setState(38);
                ((MultiValueContext) _localctx).FURTHER = match(FURTHER);

                ((MultiValueContext) _localctx).multi = ((MultiValueContext) _localctx).val1.getText() + ":" + ((MultiValueContext) _localctx).val2.getText() + ":" + ((MultiValueContext) _localctx).val3.getText() + ":" + ((MultiValueContext) _localctx).FURTHER.getText();

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
    public static class LineContext extends ParserRuleContext {
        public String sect;
        public String val;
        public SectionContext section;
        public FurtherValueContext furtherValue;
        public MultiValueContext multiValue;

        public SectionContext section() {
            return getRuleContext(SectionContext.class, 0);
        }

        public FurtherValueContext furtherValue() {
            return getRuleContext(FurtherValueContext.class, 0);
        }

        public MultiValueContext multiValue() {
            return getRuleContext(MultiValueContext.class, 0);
        }

        public LineContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_line;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GameConstantsListener) ((GameConstantsListener) listener).enterLine(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GameConstantsListener) ((GameConstantsListener) listener).exitLine(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GameConstantsVisitor)
                return ((GameConstantsVisitor<? extends T>) visitor).visitLine(this);
            else return visitor.visitChildren(this);
        }
    }

    public final LineContext line() throws RecognitionException {
        LineContext _localctx = new LineContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_line);
        try {
            setState(50);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 0, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(41);
                    ((LineContext) _localctx).section = section();

                    ((LineContext) _localctx).sect = ((LineContext) _localctx).section.sect;
                    ((LineContext) _localctx).val = ((LineContext) _localctx).section.value;

                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(44);
                    ((LineContext) _localctx).furtherValue = furtherValue();

                    ((LineContext) _localctx).sect = ((LineContext) _localctx).furtherValue.sect;
                    ((LineContext) _localctx).val = ((LineContext) _localctx).furtherValue.further;

                }
                break;
                case 3:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(47);
                    ((LineContext) _localctx).multiValue = multiValue();

                    ((LineContext) _localctx).sect = ((LineContext) _localctx).multiValue.sect;
                    ((LineContext) _localctx).val = ((LineContext) _localctx).multiValue.multi;

                }
                break;
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
        public ArrayList<Entry> results;
        public LineContext line;

        public TerminalNode EOF() {
            return getToken(GameConstantsParser.EOF, 0);
        }

        public List<LineContext> line() {
            return getRuleContexts(LineContext.class);
        }

        public LineContext line(int i) {
            return getRuleContext(LineContext.class, i);
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
            if (listener instanceof GameConstantsListener) ((GameConstantsListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GameConstantsListener) ((GameConstantsListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GameConstantsVisitor)
                return ((GameConstantsVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_file);

        ((FileContext) _localctx).results = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(57);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == TOKEN) {
                    {
                        {
                            setState(52);
                            ((FileContext) _localctx).line = line();

                            _localctx.results.add(new Entry(((FileContext) _localctx).line.sect, ((FileContext) _localctx).line.val));

                        }
                    }
                    setState(59);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(60);
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
            "\u0004\u0001\u0006?\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u00033\b\u0003\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0005\u00048\b\u0004\n\u0004\f\u0004;\t" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0000\u0000\u0005\u0000\u0002" +
                    "\u0004\u0006\b\u0000\u0000<\u0000\n\u0001\u0000\u0000\u0000\u0002\u0013" +
                    "\u0001\u0000\u0000\u0000\u0004\u001d\u0001\u0000\u0000\u0000\u00062\u0001" +
                    "\u0000\u0000\u0000\b9\u0001\u0000\u0000\u0000\n\u000b\u0005\u0001\u0000" +
                    "\u0000\u000b\f\u0006\u0000\uffff\uffff\u0000\f\r\u0005\u0004\u0000\u0000" +
                    "\r\u000e\u0005\u0001\u0000\u0000\u000e\u000f\u0006\u0000\uffff\uffff\u0000" +
                    "\u000f\u0010\u0005\u0004\u0000\u0000\u0010\u0011\u0005\u0005\u0000\u0000" +
                    "\u0011\u0012\u0006\u0000\uffff\uffff\u0000\u0012\u0001\u0001\u0000\u0000" +
                    "\u0000\u0013\u0014\u0005\u0001\u0000\u0000\u0014\u0015\u0006\u0001\uffff" +
                    "\uffff\u0000\u0015\u0016\u0005\u0004\u0000\u0000\u0016\u0017\u0005\u0005" +
                    "\u0000\u0000\u0017\u0018\u0005\u0004\u0000\u0000\u0018\u0019\u0005\u0005" +
                    "\u0000\u0000\u0019\u001a\u0005\u0004\u0000\u0000\u001a\u001b\u0005\u0002" +
                    "\u0000\u0000\u001b\u001c\u0006\u0001\uffff\uffff\u0000\u001c\u0003\u0001" +
                    "\u0000\u0000\u0000\u001d\u001e\u0005\u0001\u0000\u0000\u001e\u001f\u0006" +
                    "\u0002\uffff\uffff\u0000\u001f \u0005\u0004\u0000\u0000 !\u0005\u0005" +
                    "\u0000\u0000!\"\u0005\u0004\u0000\u0000\"#\u0005\u0005\u0000\u0000#$\u0005" +
                    "\u0004\u0000\u0000$%\u0005\u0005\u0000\u0000%&\u0005\u0004\u0000\u0000" +
                    "&\'\u0005\u0002\u0000\u0000\'(\u0006\u0002\uffff\uffff\u0000(\u0005\u0001" +
                    "\u0000\u0000\u0000)*\u0003\u0000\u0000\u0000*+\u0006\u0003\uffff\uffff" +
                    "\u0000+3\u0001\u0000\u0000\u0000,-\u0003\u0002\u0001\u0000-.\u0006\u0003" +
                    "\uffff\uffff\u0000.3\u0001\u0000\u0000\u0000/0\u0003\u0004\u0002\u0000" +
                    "01\u0006\u0003\uffff\uffff\u000013\u0001\u0000\u0000\u00002)\u0001\u0000" +
                    "\u0000\u00002,\u0001\u0000\u0000\u00002/\u0001\u0000\u0000\u00003\u0007" +
                    "\u0001\u0000\u0000\u000045\u0003\u0006\u0003\u000056\u0006\u0004\uffff" +
                    "\uffff\u000068\u0001\u0000\u0000\u000074\u0001\u0000\u0000\u00008;\u0001" +
                    "\u0000\u0000\u000097\u0001\u0000\u0000\u00009:\u0001\u0000\u0000\u0000" +
                    ":<\u0001\u0000\u0000\u0000;9\u0001\u0000\u0000\u0000<=\u0005\u0000\u0000" +
                    "\u0001=\t\u0001\u0000\u0000\u0000\u000229";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}