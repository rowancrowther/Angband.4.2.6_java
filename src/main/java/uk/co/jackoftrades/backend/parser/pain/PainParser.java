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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Pain.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.pain;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.middle.monsters.MonsterPain;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class PainParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, TYPE = 3, MESSAGE = 4, NUMBER = 5, TEXT = 6;
    public static final int
            RULE_type = 0, RULE_message = 1, RULE_painEntry = 2, RULE_file = 3;

    private static String[] makeRuleNames() {
        return new String[]{
                "type", "message", "painEntry", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'type:'", "'message:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "TYPE", "MESSAGE", "NUMBER", "TEXT"
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
        return "Pain.g4";
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

    public PainParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class TypeContext extends ParserRuleContext {
        public int typeNum;
        public Token NUMBER;

        public TerminalNode TYPE() {
            return getToken(PainParser.TYPE, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(PainParser.NUMBER, 0);
        }

        public TypeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_type;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PainListener) ((PainListener) listener).enterType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PainListener) ((PainListener) listener).exitType(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PainVisitor) return ((PainVisitor<? extends T>) visitor).visitType(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TypeContext type() throws RecognitionException {
        TypeContext _localctx = new TypeContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_type);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(8);
                match(TYPE);
                setState(9);
                ((TypeContext) _localctx).NUMBER = match(NUMBER);

                ((TypeContext) _localctx).typeNum = Integer.parseInt(((TypeContext) _localctx).NUMBER.getText());

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
    public static class MessageContext extends ParserRuleContext {
        public String msgStr;
        public Token TEXT;

        public TerminalNode MESSAGE() {
            return getToken(PainParser.MESSAGE, 0);
        }

        public TerminalNode TEXT() {
            return getToken(PainParser.TEXT, 0);
        }

        public MessageContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_message;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PainListener) ((PainListener) listener).enterMessage(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PainListener) ((PainListener) listener).exitMessage(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PainVisitor) return ((PainVisitor<? extends T>) visitor).visitMessage(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MessageContext message() throws RecognitionException {
        MessageContext _localctx = new MessageContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_message);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(12);
                match(MESSAGE);
                setState(13);
                ((MessageContext) _localctx).TEXT = match(TEXT);

                ((MessageContext) _localctx).msgStr = ((MessageContext) _localctx).TEXT.getText();

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
    public static class PainEntryContext extends ParserRuleContext {
        public MonsterPain monPain;
        public TypeContext type;
        public MessageContext msg;

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public List<MessageContext> message() {
            return getRuleContexts(MessageContext.class);
        }

        public MessageContext message(int i) {
            return getRuleContext(MessageContext.class, i);
        }

        public PainEntryContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_painEntry;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PainListener) ((PainListener) listener).enterPainEntry(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PainListener) ((PainListener) listener).exitPainEntry(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PainVisitor) return ((PainVisitor<? extends T>) visitor).visitPainEntry(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PainEntryContext painEntry() throws RecognitionException {
        PainEntryContext _localctx = new PainEntryContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_painEntry);

        int monTypeNum = 0;
        List<String> messageList = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(16);
                ((PainEntryContext) _localctx).type = type();
                monTypeNum = ((PainEntryContext) _localctx).type.typeNum;
                setState(21);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(18);
                            ((PainEntryContext) _localctx).msg = message();
                            messageList.add(((PainEntryContext) _localctx).msg.msgStr);
                        }
                    }
                    setState(23);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == MESSAGE);
            }
            _ctx.stop = _input.LT(-1);

            ((PainEntryContext) _localctx).monPain = new MonsterPain(monTypeNum, messageList);

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
        public List<MonsterPain> monsterPain;
        public PainEntryContext painEntry;

        public TerminalNode EOF() {
            return getToken(PainParser.EOF, 0);
        }

        public List<PainEntryContext> painEntry() {
            return getRuleContexts(PainEntryContext.class);
        }

        public PainEntryContext painEntry(int i) {
            return getRuleContext(PainEntryContext.class, i);
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
            if (listener instanceof PainListener) ((PainListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PainListener) ((PainListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PainVisitor) return ((PainVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_file);

        ((FileContext) _localctx).monsterPain = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(28);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(25);
                            ((FileContext) _localctx).painEntry = painEntry();

                            _localctx.monsterPain.add(((FileContext) _localctx).painEntry.monPain);

                        }
                    }
                    setState(30);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == TYPE);
                setState(32);
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
            "\u0004\u0001\u0006#\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0004\u0002\u0016" +
                    "\b\u0002\u000b\u0002\f\u0002\u0017\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0004\u0003\u001d\b\u0003\u000b\u0003\f\u0003\u001e\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0000\u0000\u0004\u0000\u0002\u0004\u0006\u0000\u0000" +
                    " \u0000\b\u0001\u0000\u0000\u0000\u0002\f\u0001\u0000\u0000\u0000\u0004" +
                    "\u0010\u0001\u0000\u0000\u0000\u0006\u001c\u0001\u0000\u0000\u0000\b\t" +
                    "\u0005\u0003\u0000\u0000\t\n\u0005\u0005\u0000\u0000\n\u000b\u0006\u0000" +
                    "\uffff\uffff\u0000\u000b\u0001\u0001\u0000\u0000\u0000\f\r\u0005\u0004" +
                    "\u0000\u0000\r\u000e\u0005\u0006\u0000\u0000\u000e\u000f\u0006\u0001\uffff" +
                    "\uffff\u0000\u000f\u0003\u0001\u0000\u0000\u0000\u0010\u0011\u0003\u0000" +
                    "\u0000\u0000\u0011\u0015\u0006\u0002\uffff\uffff\u0000\u0012\u0013\u0003" +
                    "\u0002\u0001\u0000\u0013\u0014\u0006\u0002\uffff\uffff\u0000\u0014\u0016" +
                    "\u0001\u0000\u0000\u0000\u0015\u0012\u0001\u0000\u0000\u0000\u0016\u0017" +
                    "\u0001\u0000\u0000\u0000\u0017\u0015\u0001\u0000\u0000\u0000\u0017\u0018" +
                    "\u0001\u0000\u0000\u0000\u0018\u0005\u0001\u0000\u0000\u0000\u0019\u001a" +
                    "\u0003\u0004\u0002\u0000\u001a\u001b\u0006\u0003\uffff\uffff\u0000\u001b" +
                    "\u001d\u0001\u0000\u0000\u0000\u001c\u0019\u0001\u0000\u0000\u0000\u001d" +
                    "\u001e\u0001\u0000\u0000\u0000\u001e\u001c\u0001\u0000\u0000\u0000\u001e" +
                    "\u001f\u0001\u0000\u0000\u0000\u001f \u0001\u0000\u0000\u0000 !\u0005" +
                    "\u0000\u0000\u0001!\u0007\u0001\u0000\u0000\u0000\u0002\u0017\u001e";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}