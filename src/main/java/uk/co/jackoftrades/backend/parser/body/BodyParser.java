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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Body.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.body;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftrades.middle.player.PlayerBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class BodyParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, BODY = 3, SLOT = 4, COLON = 5, UCASE = 6, STRING = 7;
    public static final int
            RULE_body = 0, RULE_slot = 1, RULE_entry = 2, RULE_file = 3;

    private static String[] makeRuleNames() {
        return new String[]{
                "body", "slot", "entry", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'body:'", "'slot:'", "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "BODY", "SLOT", "COLON", "UCASE", "STRING"
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
        return "Body.g4";
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


    record BodySlot(EquipmentSlotsEnum slotType, String name) {
    }

    public BodyParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class BodyContext extends ParserRuleContext {
        public String bodyName;
        public Token STRING;

        public TerminalNode BODY() {
            return getToken(BodyParser.BODY, 0);
        }

        public TerminalNode STRING() {
            return getToken(BodyParser.STRING, 0);
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
            if (listener instanceof BodyListener) ((BodyListener) listener).enterBody(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BodyListener) ((BodyListener) listener).exitBody(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BodyVisitor) return ((BodyVisitor<? extends T>) visitor).visitBody(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BodyContext body() throws RecognitionException {
        BodyContext _localctx = new BodyContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_body);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(8);
                match(BODY);
                setState(9);
                ((BodyContext) _localctx).STRING = match(STRING);
                ((BodyContext) _localctx).bodyName = ((BodyContext) _localctx).STRING.getText();
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
        public BodySlot bodySlot;
        public Token UCASE;
        public Token STRING;

        public TerminalNode SLOT() {
            return getToken(BodyParser.SLOT, 0);
        }

        public TerminalNode UCASE() {
            return getToken(BodyParser.UCASE, 0);
        }

        public TerminalNode COLON() {
            return getToken(BodyParser.COLON, 0);
        }

        public TerminalNode STRING() {
            return getToken(BodyParser.STRING, 0);
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
            if (listener instanceof BodyListener) ((BodyListener) listener).enterSlot(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BodyListener) ((BodyListener) listener).exitSlot(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BodyVisitor) return ((BodyVisitor<? extends T>) visitor).visitSlot(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SlotContext slot() throws RecognitionException {
        SlotContext _localctx = new SlotContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_slot);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(12);
                match(SLOT);
                setState(13);
                ((SlotContext) _localctx).UCASE = match(UCASE);
                setState(14);
                match(COLON);
                setState(15);
                ((SlotContext) _localctx).STRING = match(STRING);

                String raw = ((SlotContext) _localctx).UCASE.getText().toUpperCase().trim();
                EquipmentSlotsEnum equipSlot = EquipmentSlotsEnum.valueOf("EQUIP_" + raw);
                String locationStr = ((SlotContext) _localctx).STRING.getText();
                ((SlotContext) _localctx).bodySlot = new BodySlot(equipSlot, locationStr);

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
    public static class EntryContext extends ParserRuleContext {
        public PlayerBody playerBody;
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

        public EntryContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_entry;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BodyListener) ((BodyListener) listener).enterEntry(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BodyListener) ((BodyListener) listener).exitEntry(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BodyVisitor) return ((BodyVisitor<? extends T>) visitor).visitEntry(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EntryContext entry() throws RecognitionException {
        EntryContext _localctx = new EntryContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_entry);

        int countInit = 0;
        ItemObject objInit = null;
        Map<BodySlot, ItemObject> slotsInit = new HashMap<>();
        String bodyNameInit = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(18);
                ((EntryContext) _localctx).body = body();
                bodyNameInit = ((EntryContext) _localctx).body.bodyName;
                setState(23);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(20);
                            ((EntryContext) _localctx).slot = slot();

                            slotsInit.put(((EntryContext) _localctx).slot.bodySlot, objInit);

                        }
                    }
                    setState(25);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == SLOT);
            }
            _ctx.stop = _input.LT(-1);

            ((EntryContext) _localctx).playerBody = new PlayerBody(bodyNameInit, countInit, slotsInit);

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
        public List<PlayerBody> bodies;
        public EntryContext entry;

        public TerminalNode EOF() {
            return getToken(BodyParser.EOF, 0);
        }

        public List<EntryContext> entry() {
            return getRuleContexts(EntryContext.class);
        }

        public EntryContext entry(int i) {
            return getRuleContext(EntryContext.class, i);
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
            if (listener instanceof BodyListener) ((BodyListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BodyListener) ((BodyListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BodyVisitor) return ((BodyVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_file);

        ((FileContext) _localctx).bodies = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(30);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(27);
                            ((FileContext) _localctx).entry = entry();

                            _localctx.bodies.add(((FileContext) _localctx).entry.playerBody);

                        }
                    }
                    setState(32);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == BODY);
                setState(34);
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
            "\u0004\u0001\u0007%\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0004\u0002\u0018\b\u0002\u000b\u0002\f\u0002\u0019\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0004\u0003\u001f\b\u0003\u000b\u0003\f\u0003" +
                    " \u0001\u0003\u0001\u0003\u0001\u0003\u0000\u0000\u0004\u0000\u0002\u0004" +
                    "\u0006\u0000\u0000\"\u0000\b\u0001\u0000\u0000\u0000\u0002\f\u0001\u0000" +
                    "\u0000\u0000\u0004\u0012\u0001\u0000\u0000\u0000\u0006\u001e\u0001\u0000" +
                    "\u0000\u0000\b\t\u0005\u0003\u0000\u0000\t\n\u0005\u0007\u0000\u0000\n" +
                    "\u000b\u0006\u0000\uffff\uffff\u0000\u000b\u0001\u0001\u0000\u0000\u0000" +
                    "\f\r\u0005\u0004\u0000\u0000\r\u000e\u0005\u0006\u0000\u0000\u000e\u000f" +
                    "\u0005\u0005\u0000\u0000\u000f\u0010\u0005\u0007\u0000\u0000\u0010\u0011" +
                    "\u0006\u0001\uffff\uffff\u0000\u0011\u0003\u0001\u0000\u0000\u0000\u0012" +
                    "\u0013\u0003\u0000\u0000\u0000\u0013\u0017\u0006\u0002\uffff\uffff\u0000" +
                    "\u0014\u0015\u0003\u0002\u0001\u0000\u0015\u0016\u0006\u0002\uffff\uffff" +
                    "\u0000\u0016\u0018\u0001\u0000\u0000\u0000\u0017\u0014\u0001\u0000\u0000" +
                    "\u0000\u0018\u0019\u0001\u0000\u0000\u0000\u0019\u0017\u0001\u0000\u0000" +
                    "\u0000\u0019\u001a\u0001\u0000\u0000\u0000\u001a\u0005\u0001\u0000\u0000" +
                    "\u0000\u001b\u001c\u0003\u0004\u0002\u0000\u001c\u001d\u0006\u0003\uffff" +
                    "\uffff\u0000\u001d\u001f\u0001\u0000\u0000\u0000\u001e\u001b\u0001\u0000" +
                    "\u0000\u0000\u001f \u0001\u0000\u0000\u0000 \u001e\u0001\u0000\u0000\u0000" +
                    " !\u0001\u0000\u0000\u0000!\"\u0001\u0000\u0000\u0000\"#\u0005\u0000\u0000" +
                    "\u0001#\u0007\u0001\u0000\u0000\u0000\u0002\u0019 ";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}