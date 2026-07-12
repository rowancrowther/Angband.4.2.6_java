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

// Generated from BodyGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.body;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.parser.body.BodyParseRecord;
import uk.co.jackoftrades.backend.parser.body.BodyParseRecord.BodySlotRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class BodyGrammar extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, BODY = 2, SLOT = 3, INTEGER = 4, SLOT_NAME = 5, COLON = 6, COMMENT = 7,
            EOL = 8, STRING = 9, FREE_TEXT_EOL = 10;
    public static final int
            RULE_recordCount = 0, RULE_body = 1, RULE_slot = 2, RULE_bodyType = 3,
            RULE_file = 4;

    private static String[] makeRuleNames() {
        return new String[]{
                "recordCount", "body", "slot", "bodyType", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'body:'", "'slot:'", null, null, "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "BODY", "SLOT", "INTEGER", "SLOT_NAME", "COLON",
                "COMMENT", "EOL", "STRING", "FREE_TEXT_EOL"
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
        return "BodyGrammar.g4";
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

    public BodyGrammar(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RecordCountContext extends ParserRuleContext {
        public String count;
        public Token INTEGER;

        public TerminalNode RECORD_COUNT() {
            return getToken(BodyGrammar.RECORD_COUNT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(BodyGrammar.INTEGER, 0);
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
            if (listener instanceof BodyGrammarListener) ((BodyGrammarListener) listener).enterRecordCount(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BodyGrammarListener) ((BodyGrammarListener) listener).exitRecordCount(this);
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
    public static class BodyContext extends ParserRuleContext {
        public String bodyName;
        public int line;
        public Token STRING;

        public TerminalNode BODY() {
            return getToken(BodyGrammar.BODY, 0);
        }

        public TerminalNode STRING() {
            return getToken(BodyGrammar.STRING, 0);
        }

        public BodyContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_body;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BodyGrammarListener) ((BodyGrammarListener) listener).enterBody(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BodyGrammarListener) ((BodyGrammarListener) listener).exitBody(this);
        }
    }

    public final BodyContext body() throws RecognitionException {
        BodyContext _localctx = new BodyContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_body);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(14);
                match(BODY);
                setState(15);
                ((BodyContext) _localctx).STRING = match(STRING);
                ((BodyContext) _localctx).bodyName = ((BodyContext) _localctx).STRING.getText();
                ((BodyContext) _localctx).line = _localctx.start.getLine();
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
    public static class SlotContext extends ParserRuleContext {
        public String slotType;
        public String slotName;
        public Token t;
        public Token n;

        public TerminalNode SLOT() {
            return getToken(BodyGrammar.SLOT, 0);
        }

        public TerminalNode COLON() {
            return getToken(BodyGrammar.COLON, 0);
        }

        public TerminalNode SLOT_NAME() {
            return getToken(BodyGrammar.SLOT_NAME, 0);
        }

        public TerminalNode STRING() {
            return getToken(BodyGrammar.STRING, 0);
        }

        public SlotContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_slot;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BodyGrammarListener) ((BodyGrammarListener) listener).enterSlot(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BodyGrammarListener) ((BodyGrammarListener) listener).exitSlot(this);
        }
    }

    public final SlotContext slot() throws RecognitionException {
        SlotContext _localctx = new SlotContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_slot);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(18);
                match(SLOT);
                setState(19);
                ((SlotContext) _localctx).t = match(SLOT_NAME);
                setState(20);
                match(COLON);
                setState(21);
                ((SlotContext) _localctx).n = match(STRING);

                ((SlotContext) _localctx).slotType = ((SlotContext) _localctx).t.getText();
                ((SlotContext) _localctx).slotName = ((SlotContext) _localctx).n.getText();

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
    public static class BodyTypeContext extends ParserRuleContext {
        public BodyParseRecord record;
        public BodyContext body;
        public SlotContext slot;

        public BodyContext body() {
            return getRuleContext(BodyContext.class, 0);
        }

        public List<SlotContext> slot() {
            return getRuleContexts(SlotContext.class);
        }

        public SlotContext slot(int i) {
            return getRuleContext(SlotContext.class, i);
        }

        public BodyTypeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_bodyType;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BodyGrammarListener) ((BodyGrammarListener) listener).enterBodyType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BodyGrammarListener) ((BodyGrammarListener) listener).exitBodyType(this);
        }
    }

    public final BodyTypeContext bodyType() throws RecognitionException {
        BodyTypeContext _localctx = new BodyTypeContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_bodyType);

        String bodyNameInit = "";
        int line = 0;
        List<BodySlotRecord> slots = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(24);
                ((BodyTypeContext) _localctx).body = body();

                bodyNameInit = ((BodyTypeContext) _localctx).body.bodyName;
                line = ((BodyTypeContext) _localctx).body.line;

                setState(29);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(26);
                            ((BodyTypeContext) _localctx).slot = slot();

                            String slotType = ((BodyTypeContext) _localctx).slot.slotType;
                            String slotName = ((BodyTypeContext) _localctx).slot.slotName;
                            slots.add(new BodySlotRecord(slotType, slotName));

                        }
                    }
                    setState(31);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == SLOT);
            }
            _ctx.stop = _input.LT(-1);

            ((BodyTypeContext) _localctx).record = new BodyParseRecord(bodyNameInit, slots, line);

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
        public List<BodyParseRecord> bodies;
        public RecordCountContext recordCount;
        public BodyTypeContext bodyType;

        public RecordCountContext recordCount() {
            return getRuleContext(RecordCountContext.class, 0);
        }

        public TerminalNode EOF() {
            return getToken(BodyGrammar.EOF, 0);
        }

        public List<BodyTypeContext> bodyType() {
            return getRuleContexts(BodyTypeContext.class);
        }

        public BodyTypeContext bodyType(int i) {
            return getRuleContext(BodyTypeContext.class, i);
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
            if (listener instanceof BodyGrammarListener) ((BodyGrammarListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BodyGrammarListener) ((BodyGrammarListener) listener).exitFile(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_file);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(33);
                ((FileContext) _localctx).recordCount = recordCount();
                ((FileContext) _localctx).declaredRecordCount = ((FileContext) _localctx).recordCount.count;
                ((FileContext) _localctx).bodies = new ArrayList<>();
                setState(38);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(35);
                            ((FileContext) _localctx).bodyType = bodyType();
                            _localctx.bodies.add(((FileContext) _localctx).bodyType.record);
                        }
                    }
                    setState(40);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == BODY);
                setState(42);
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
            "\u0004\u0001\n-\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0004\u0003\u001e\b\u0003\u000b\u0003\f\u0003\u001f\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0004\u0004\'\b\u0004" +
                    "\u000b\u0004\f\u0004(\u0001\u0004\u0001\u0004\u0001\u0004\u0000\u0000" +
                    "\u0005\u0000\u0002\u0004\u0006\b\u0000\u0000)\u0000\n\u0001\u0000\u0000" +
                    "\u0000\u0002\u000e\u0001\u0000\u0000\u0000\u0004\u0012\u0001\u0000\u0000" +
                    "\u0000\u0006\u0018\u0001\u0000\u0000\u0000\b!\u0001\u0000\u0000\u0000" +
                    "\n\u000b\u0005\u0001\u0000\u0000\u000b\f\u0005\u0004\u0000\u0000\f\r\u0006" +
                    "\u0000\uffff\uffff\u0000\r\u0001\u0001\u0000\u0000\u0000\u000e\u000f\u0005" +
                    "\u0002\u0000\u0000\u000f\u0010\u0005\t\u0000\u0000\u0010\u0011\u0006\u0001" +
                    "\uffff\uffff\u0000\u0011\u0003\u0001\u0000\u0000\u0000\u0012\u0013\u0005" +
                    "\u0003\u0000\u0000\u0013\u0014\u0005\u0005\u0000\u0000\u0014\u0015\u0005" +
                    "\u0006\u0000\u0000\u0015\u0016\u0005\t\u0000\u0000\u0016\u0017\u0006\u0002" +
                    "\uffff\uffff\u0000\u0017\u0005\u0001\u0000\u0000\u0000\u0018\u0019\u0003" +
                    "\u0002\u0001\u0000\u0019\u001d\u0006\u0003\uffff\uffff\u0000\u001a\u001b" +
                    "\u0003\u0004\u0002\u0000\u001b\u001c\u0006\u0003\uffff\uffff\u0000\u001c" +
                    "\u001e\u0001\u0000\u0000\u0000\u001d\u001a\u0001\u0000\u0000\u0000\u001e" +
                    "\u001f\u0001\u0000\u0000\u0000\u001f\u001d\u0001\u0000\u0000\u0000\u001f" +
                    " \u0001\u0000\u0000\u0000 \u0007\u0001\u0000\u0000\u0000!\"\u0003\u0000" +
                    "\u0000\u0000\"&\u0006\u0004\uffff\uffff\u0000#$\u0003\u0006\u0003\u0000" +
                    "$%\u0006\u0004\uffff\uffff\u0000%\'\u0001\u0000\u0000\u0000&#\u0001\u0000" +
                    "\u0000\u0000\'(\u0001\u0000\u0000\u0000(&\u0001\u0000\u0000\u0000()\u0001" +
                    "\u0000\u0000\u0000)*\u0001\u0000\u0000\u0000*+\u0005\u0000\u0000\u0001" +
                    "+\t\u0001\u0000\u0000\u0000\u0002\u001f(";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}