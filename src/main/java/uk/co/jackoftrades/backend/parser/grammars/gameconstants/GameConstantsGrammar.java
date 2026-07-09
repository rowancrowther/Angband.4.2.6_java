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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/GameConstantsGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.gameconstants;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.parser.gameconstants.GameConstantsParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class GameConstantsGrammar extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            GC_NAME = 1, GC_MSG = 2, COLON = 3, INTEGER = 4, COMMENT = 5, EOL = 6;
    public static final int
            RULE_field = 0, RULE_line = 1, RULE_file = 2;

    private static String[] makeRuleNames() {
        return new String[]{
                "field", "line", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "GC_NAME", "GC_MSG", "COLON", "INTEGER", "COMMENT", "EOL"
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
        return "GameConstantsGrammar.g4";
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

    public GameConstantsGrammar(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class FieldContext extends ParserRuleContext {
        public String fieldValue;
        public Token GC_NAME;
        public Token INTEGER;
        public Token GC_MSG;

        public TerminalNode GC_NAME() {
            return getToken(GameConstantsGrammar.GC_NAME, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(GameConstantsGrammar.INTEGER, 0);
        }

        public TerminalNode GC_MSG() {
            return getToken(GameConstantsGrammar.GC_MSG, 0);
        }

        public FieldContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_field;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof GameConstantsGrammarListener)
                ((GameConstantsGrammarListener) listener).enterField(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GameConstantsGrammarListener)
                ((GameConstantsGrammarListener) listener).exitField(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GameConstantsGrammarVisitor)
                return ((GameConstantsGrammarVisitor<? extends T>) visitor).visitField(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FieldContext field() throws RecognitionException {
        FieldContext _localctx = new FieldContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_field);
        try {
            setState(12);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case GC_NAME:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(6);
                    ((FieldContext) _localctx).GC_NAME = match(GC_NAME);
                    ((FieldContext) _localctx).fieldValue = ((FieldContext) _localctx).GC_NAME.getText();
                }
                break;
                case INTEGER:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(8);
                    ((FieldContext) _localctx).INTEGER = match(INTEGER);
                    ((FieldContext) _localctx).fieldValue = ((FieldContext) _localctx).INTEGER.getText();
                }
                break;
                case GC_MSG:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(10);
                    ((FieldContext) _localctx).GC_MSG = match(GC_MSG);
                    ((FieldContext) _localctx).fieldValue = ((FieldContext) _localctx).GC_MSG.getText();
                }
                break;
                default:
                    throw new NoViableAltException(this);
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
        public String category;
        public List<String> fields;
        public int lineNo;
        public Token GC_NAME;
        public FieldContext f;

        public TerminalNode GC_NAME() {
            return getToken(GameConstantsGrammar.GC_NAME, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(GameConstantsGrammar.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(GameConstantsGrammar.COLON, i);
        }

        public List<FieldContext> field() {
            return getRuleContexts(FieldContext.class);
        }

        public FieldContext field(int i) {
            return getRuleContext(FieldContext.class, i);
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
            if (listener instanceof GameConstantsGrammarListener)
                ((GameConstantsGrammarListener) listener).enterLine(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GameConstantsGrammarListener)
                ((GameConstantsGrammarListener) listener).exitLine(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GameConstantsGrammarVisitor)
                return ((GameConstantsGrammarVisitor<? extends T>) visitor).visitLine(this);
            else return visitor.visitChildren(this);
        }
    }

    public final LineContext line() throws RecognitionException {
        LineContext _localctx = new LineContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_line);

        ((LineContext) _localctx).fields = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                ((LineContext) _localctx).lineNo = _localctx.start.getLine();
                setState(15);
                ((LineContext) _localctx).GC_NAME = match(GC_NAME);
                ((LineContext) _localctx).category = ((LineContext) _localctx).GC_NAME.getText();
                setState(21);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(17);
                            match(COLON);
                            setState(18);
                            ((LineContext) _localctx).f = field();
                            _localctx.fields.add(((LineContext) _localctx).f.fieldValue);
                        }
                    }
                    setState(23);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == COLON);
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
        public ArrayList<GameConstantsParseRecord> results;
        public LineContext line;

        public TerminalNode EOF() {
            return getToken(GameConstantsGrammar.EOF, 0);
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
            if (listener instanceof GameConstantsGrammarListener)
                ((GameConstantsGrammarListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof GameConstantsGrammarListener)
                ((GameConstantsGrammarListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GameConstantsGrammarVisitor)
                return ((GameConstantsGrammarVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_file);

        ((FileContext) _localctx).results = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(30);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == GC_NAME) {
                    {
                        {
                            setState(25);
                            ((FileContext) _localctx).line = line();

                            _localctx.results.add(new GameConstantsParseRecord(((FileContext) _localctx).line.category,
                                    ((FileContext) _localctx).line.fields,
                                    ((FileContext) _localctx).line.lineNo));

                        }
                    }
                    setState(32);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(33);
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
            "\u0004\u0001\u0006$\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0003\u0000\r\b\u0000\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0004\u0001\u0016" +
                    "\b\u0001\u000b\u0001\f\u0001\u0017\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0005\u0002\u001d\b\u0002\n\u0002\f\u0002 \t\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0000\u0000\u0003\u0000\u0002\u0004\u0000\u0000$\u0000" +
                    "\f\u0001\u0000\u0000\u0000\u0002\u000e\u0001\u0000\u0000\u0000\u0004\u001e" +
                    "\u0001\u0000\u0000\u0000\u0006\u0007\u0005\u0001\u0000\u0000\u0007\r\u0006" +
                    "\u0000\uffff\uffff\u0000\b\t\u0005\u0004\u0000\u0000\t\r\u0006\u0000\uffff" +
                    "\uffff\u0000\n\u000b\u0005\u0002\u0000\u0000\u000b\r\u0006\u0000\uffff" +
                    "\uffff\u0000\f\u0006\u0001\u0000\u0000\u0000\f\b\u0001\u0000\u0000\u0000" +
                    "\f\n\u0001\u0000\u0000\u0000\r\u0001\u0001\u0000\u0000\u0000\u000e\u000f" +
                    "\u0006\u0001\uffff\uffff\u0000\u000f\u0010\u0005\u0001\u0000\u0000\u0010" +
                    "\u0015\u0006\u0001\uffff\uffff\u0000\u0011\u0012\u0005\u0003\u0000\u0000" +
                    "\u0012\u0013\u0003\u0000\u0000\u0000\u0013\u0014\u0006\u0001\uffff\uffff" +
                    "\u0000\u0014\u0016\u0001\u0000\u0000\u0000\u0015\u0011\u0001\u0000\u0000" +
                    "\u0000\u0016\u0017\u0001\u0000\u0000\u0000\u0017\u0015\u0001\u0000\u0000" +
                    "\u0000\u0017\u0018\u0001\u0000\u0000\u0000\u0018\u0003\u0001\u0000\u0000" +
                    "\u0000\u0019\u001a\u0003\u0002\u0001\u0000\u001a\u001b\u0006\u0002\uffff" +
                    "\uffff\u0000\u001b\u001d\u0001\u0000\u0000\u0000\u001c\u0019\u0001\u0000" +
                    "\u0000\u0000\u001d \u0001\u0000\u0000\u0000\u001e\u001c\u0001\u0000\u0000" +
                    "\u0000\u001e\u001f\u0001\u0000\u0000\u0000\u001f!\u0001\u0000\u0000\u0000" +
                    " \u001e\u0001\u0000\u0000\u0000!\"\u0005\u0000\u0000\u0001\"\u0005\u0001" +
                    "\u0000\u0000\u0000\u0003\f\u0017\u001e";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}