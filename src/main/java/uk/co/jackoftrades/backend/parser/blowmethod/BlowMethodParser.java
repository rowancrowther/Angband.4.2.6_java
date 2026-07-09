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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/BlowMethod.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.blowmethod;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.middle.combat.BlowMethod;
import uk.co.jackoftrades.middle.enums.MessageType;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class BlowMethodParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, NAME = 3, CUT = 4, STUN = 5, MISS = 6, PHYS = 7, MSG = 8, ACT = 9,
            DESC = 10, BOOLEAN = 11, UCASE = 12, LCASE = 13;
    public static final int
            RULE_name = 0, RULE_cut = 1, RULE_stun = 2, RULE_miss = 3, RULE_phys = 4,
            RULE_msg = 5, RULE_act = 6, RULE_desc = 7, RULE_blowMethod = 8, RULE_file = 9;

    private static String[] makeRuleNames() {
        return new String[]{
                "name", "cut", "stun", "miss", "phys", "msg", "act", "desc", "blowMethod",
                "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'name:'", "'cut:'", "'stun:'", "'miss:'", "'phys:'",
                "'msg:'", "'act:'", "'desc:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "NAME", "CUT", "STUN", "MISS", "PHYS", "MSG",
                "ACT", "DESC", "BOOLEAN", "UCASE", "LCASE"
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
        return "BlowMethod.g4";
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

    public BlowMethodParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class NameContext extends ParserRuleContext {
        public String nameStr;
        public Token UCASE;

        public TerminalNode NAME() {
            return getToken(BlowMethodParser.NAME, 0);
        }

        public TerminalNode UCASE() {
            return getToken(BlowMethodParser.UCASE, 0);
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
            if (listener instanceof BlowMethodListener) ((BlowMethodListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BlowMethodListener) ((BlowMethodListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BlowMethodVisitor) return ((BlowMethodVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(20);
                match(NAME);
                setState(21);
                ((NameContext) _localctx).UCASE = match(UCASE);
                ((NameContext) _localctx).nameStr = ((NameContext) _localctx).UCASE.getText();
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
    public static class CutContext extends ParserRuleContext {
        public boolean cutBool;
        public Token BOOLEAN;

        public TerminalNode CUT() {
            return getToken(BlowMethodParser.CUT, 0);
        }

        public TerminalNode BOOLEAN() {
            return getToken(BlowMethodParser.BOOLEAN, 0);
        }

        public CutContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_cut;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BlowMethodListener) ((BlowMethodListener) listener).enterCut(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BlowMethodListener) ((BlowMethodListener) listener).exitCut(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BlowMethodVisitor) return ((BlowMethodVisitor<? extends T>) visitor).visitCut(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CutContext cut() throws RecognitionException {
        CutContext _localctx = new CutContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_cut);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(24);
                match(CUT);
                setState(25);
                ((CutContext) _localctx).BOOLEAN = match(BOOLEAN);
                ((CutContext) _localctx).cutBool = ((CutContext) _localctx).BOOLEAN.getText().equals("1");
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
    public static class StunContext extends ParserRuleContext {
        public boolean stunBool;
        public Token BOOLEAN;

        public TerminalNode STUN() {
            return getToken(BlowMethodParser.STUN, 0);
        }

        public TerminalNode BOOLEAN() {
            return getToken(BlowMethodParser.BOOLEAN, 0);
        }

        public StunContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_stun;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BlowMethodListener) ((BlowMethodListener) listener).enterStun(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BlowMethodListener) ((BlowMethodListener) listener).exitStun(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BlowMethodVisitor) return ((BlowMethodVisitor<? extends T>) visitor).visitStun(this);
            else return visitor.visitChildren(this);
        }
    }

    public final StunContext stun() throws RecognitionException {
        StunContext _localctx = new StunContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_stun);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(28);
                match(STUN);
                setState(29);
                ((StunContext) _localctx).BOOLEAN = match(BOOLEAN);
                ((StunContext) _localctx).stunBool = ((StunContext) _localctx).BOOLEAN.getText().equals("1");
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
    public static class MissContext extends ParserRuleContext {
        public boolean missBool;
        public Token BOOLEAN;

        public TerminalNode MISS() {
            return getToken(BlowMethodParser.MISS, 0);
        }

        public TerminalNode BOOLEAN() {
            return getToken(BlowMethodParser.BOOLEAN, 0);
        }

        public MissContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_miss;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BlowMethodListener) ((BlowMethodListener) listener).enterMiss(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BlowMethodListener) ((BlowMethodListener) listener).exitMiss(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BlowMethodVisitor) return ((BlowMethodVisitor<? extends T>) visitor).visitMiss(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MissContext miss() throws RecognitionException {
        MissContext _localctx = new MissContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_miss);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(32);
                match(MISS);
                setState(33);
                ((MissContext) _localctx).BOOLEAN = match(BOOLEAN);
                ((MissContext) _localctx).missBool = ((MissContext) _localctx).BOOLEAN.getText().equals("1");
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
    public static class PhysContext extends ParserRuleContext {
        public boolean physBool;
        public Token BOOLEAN;

        public TerminalNode PHYS() {
            return getToken(BlowMethodParser.PHYS, 0);
        }

        public TerminalNode BOOLEAN() {
            return getToken(BlowMethodParser.BOOLEAN, 0);
        }

        public PhysContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_phys;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BlowMethodListener) ((BlowMethodListener) listener).enterPhys(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BlowMethodListener) ((BlowMethodListener) listener).exitPhys(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BlowMethodVisitor) return ((BlowMethodVisitor<? extends T>) visitor).visitPhys(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PhysContext phys() throws RecognitionException {
        PhysContext _localctx = new PhysContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_phys);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(36);
                match(PHYS);
                setState(37);
                ((PhysContext) _localctx).BOOLEAN = match(BOOLEAN);
                ((PhysContext) _localctx).physBool = ((PhysContext) _localctx).BOOLEAN.getText().equals("1");
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
    public static class MsgContext extends ParserRuleContext {
        public MessageType message;
        public Token UCASE;

        public TerminalNode MSG() {
            return getToken(BlowMethodParser.MSG, 0);
        }

        public TerminalNode UCASE() {
            return getToken(BlowMethodParser.UCASE, 0);
        }

        public MsgContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_msg;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BlowMethodListener) ((BlowMethodListener) listener).enterMsg(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BlowMethodListener) ((BlowMethodListener) listener).exitMsg(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BlowMethodVisitor) return ((BlowMethodVisitor<? extends T>) visitor).visitMsg(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MsgContext msg() throws RecognitionException {
        MsgContext _localctx = new MsgContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_msg);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(40);
                match(MSG);
                setState(41);
                ((MsgContext) _localctx).UCASE = match(UCASE);

                String raw = ((MsgContext) _localctx).UCASE.getText();
                ((MsgContext) _localctx).message = MessageType.valueOf("MSG_" + raw);

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
    public static class ActContext extends ParserRuleContext {
        public String actStr;
        public Token LCASE;

        public TerminalNode ACT() {
            return getToken(BlowMethodParser.ACT, 0);
        }

        public TerminalNode LCASE() {
            return getToken(BlowMethodParser.LCASE, 0);
        }

        public ActContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_act;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BlowMethodListener) ((BlowMethodListener) listener).enterAct(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BlowMethodListener) ((BlowMethodListener) listener).exitAct(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BlowMethodVisitor) return ((BlowMethodVisitor<? extends T>) visitor).visitAct(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ActContext act() throws RecognitionException {
        ActContext _localctx = new ActContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_act);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(44);
                match(ACT);
                setState(45);
                ((ActContext) _localctx).LCASE = match(LCASE);
                ((ActContext) _localctx).actStr = ((ActContext) _localctx).LCASE.getText();
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
        public Token LCASE;

        public TerminalNode DESC() {
            return getToken(BlowMethodParser.DESC, 0);
        }

        public TerminalNode LCASE() {
            return getToken(BlowMethodParser.LCASE, 0);
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
            if (listener instanceof BlowMethodListener) ((BlowMethodListener) listener).enterDesc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BlowMethodListener) ((BlowMethodListener) listener).exitDesc(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BlowMethodVisitor) return ((BlowMethodVisitor<? extends T>) visitor).visitDesc(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DescContext desc() throws RecognitionException {
        DescContext _localctx = new DescContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_desc);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(48);
                match(DESC);
                setState(49);
                ((DescContext) _localctx).LCASE = match(LCASE);
                ((DescContext) _localctx).descStr = ((DescContext) _localctx).LCASE.getText();
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
    public static class BlowMethodContext extends ParserRuleContext {
        public BlowMethod method;
        public NameContext name;
        public CutContext cut;
        public StunContext stun;
        public MissContext miss;
        public PhysContext phys;
        public MsgContext msg;
        public ActContext act;
        public DescContext desc;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public CutContext cut() {
            return getRuleContext(CutContext.class, 0);
        }

        public StunContext stun() {
            return getRuleContext(StunContext.class, 0);
        }

        public MissContext miss() {
            return getRuleContext(MissContext.class, 0);
        }

        public PhysContext phys() {
            return getRuleContext(PhysContext.class, 0);
        }

        public MsgContext msg() {
            return getRuleContext(MsgContext.class, 0);
        }

        public DescContext desc() {
            return getRuleContext(DescContext.class, 0);
        }

        public List<ActContext> act() {
            return getRuleContexts(ActContext.class);
        }

        public ActContext act(int i) {
            return getRuleContext(ActContext.class, i);
        }

        public BlowMethodContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_blowMethod;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BlowMethodListener) ((BlowMethodListener) listener).enterBlowMethod(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BlowMethodListener) ((BlowMethodListener) listener).exitBlowMethod(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BlowMethodVisitor)
                return ((BlowMethodVisitor<? extends T>) visitor).visitBlowMethod(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BlowMethodContext blowMethod() throws RecognitionException {
        BlowMethodContext _localctx = new BlowMethodContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_blowMethod);

        String nameInit = "";
        boolean cutInit = false;
        boolean stunInit = false;
        boolean missInit = false;
        boolean physInit = false;
        MessageType msgInit = MessageType.MSG_NONE;
        List<String> actInit = new ArrayList<>();
        String descInit = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(52);
                ((BlowMethodContext) _localctx).name = name();
                nameInit = ((BlowMethodContext) _localctx).name.nameStr;
                setState(54);
                ((BlowMethodContext) _localctx).cut = cut();
                cutInit = ((BlowMethodContext) _localctx).cut.cutBool;
                setState(56);
                ((BlowMethodContext) _localctx).stun = stun();
                stunInit = ((BlowMethodContext) _localctx).stun.stunBool;
                setState(58);
                ((BlowMethodContext) _localctx).miss = miss();
                missInit = ((BlowMethodContext) _localctx).miss.missBool;
                setState(60);
                ((BlowMethodContext) _localctx).phys = phys();
                physInit = ((BlowMethodContext) _localctx).phys.physBool;
                setState(62);
                ((BlowMethodContext) _localctx).msg = msg();
                msgInit = ((BlowMethodContext) _localctx).msg.message;
                setState(67);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(64);
                            ((BlowMethodContext) _localctx).act = act();
                            actInit.add(((BlowMethodContext) _localctx).act.actStr);
                        }
                    }
                    setState(69);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == ACT);
                setState(71);
                ((BlowMethodContext) _localctx).desc = desc();
                descInit = ((BlowMethodContext) _localctx).desc.descStr;
            }
            _ctx.stop = _input.LT(-1);

            ((BlowMethodContext) _localctx).method = new BlowMethod(nameInit, cutInit, stunInit, missInit, physInit,
                    msgInit, actInit, descInit);

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
        public List<BlowMethod> methods;
        public BlowMethodContext blowMethod;

        public TerminalNode EOF() {
            return getToken(BlowMethodParser.EOF, 0);
        }

        public List<BlowMethodContext> blowMethod() {
            return getRuleContexts(BlowMethodContext.class);
        }

        public BlowMethodContext blowMethod(int i) {
            return getRuleContext(BlowMethodContext.class, i);
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
            if (listener instanceof BlowMethodListener) ((BlowMethodListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BlowMethodListener) ((BlowMethodListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BlowMethodVisitor) return ((BlowMethodVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_file);

        ((FileContext) _localctx).methods = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(77);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(74);
                            ((FileContext) _localctx).blowMethod = blowMethod();
                            _localctx.methods.add(((FileContext) _localctx).blowMethod.method);
                        }
                    }
                    setState(79);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(81);
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
            "\u0004\u0001\rT\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002" +
                    "\b\u0007\b\u0002\t\u0007\t\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0004\bD\b\b\u000b\b\f\bE\u0001\b\u0001\b\u0001" +
                    "\b\u0001\t\u0001\t\u0001\t\u0004\tN\b\t\u000b\t\f\tO\u0001\t\u0001\t\u0001" +
                    "\t\u0000\u0000\n\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0000" +
                    "\u0000K\u0000\u0014\u0001\u0000\u0000\u0000\u0002\u0018\u0001\u0000\u0000" +
                    "\u0000\u0004\u001c\u0001\u0000\u0000\u0000\u0006 \u0001\u0000\u0000\u0000" +
                    "\b$\u0001\u0000\u0000\u0000\n(\u0001\u0000\u0000\u0000\f,\u0001\u0000" +
                    "\u0000\u0000\u000e0\u0001\u0000\u0000\u0000\u00104\u0001\u0000\u0000\u0000" +
                    "\u0012M\u0001\u0000\u0000\u0000\u0014\u0015\u0005\u0003\u0000\u0000\u0015" +
                    "\u0016\u0005\f\u0000\u0000\u0016\u0017\u0006\u0000\uffff\uffff\u0000\u0017" +
                    "\u0001\u0001\u0000\u0000\u0000\u0018\u0019\u0005\u0004\u0000\u0000\u0019" +
                    "\u001a\u0005\u000b\u0000\u0000\u001a\u001b\u0006\u0001\uffff\uffff\u0000" +
                    "\u001b\u0003\u0001\u0000\u0000\u0000\u001c\u001d\u0005\u0005\u0000\u0000" +
                    "\u001d\u001e\u0005\u000b\u0000\u0000\u001e\u001f\u0006\u0002\uffff\uffff" +
                    "\u0000\u001f\u0005\u0001\u0000\u0000\u0000 !\u0005\u0006\u0000\u0000!" +
                    "\"\u0005\u000b\u0000\u0000\"#\u0006\u0003\uffff\uffff\u0000#\u0007\u0001" +
                    "\u0000\u0000\u0000$%\u0005\u0007\u0000\u0000%&\u0005\u000b\u0000\u0000" +
                    "&\'\u0006\u0004\uffff\uffff\u0000\'\t\u0001\u0000\u0000\u0000()\u0005" +
                    "\b\u0000\u0000)*\u0005\f\u0000\u0000*+\u0006\u0005\uffff\uffff\u0000+" +
                    "\u000b\u0001\u0000\u0000\u0000,-\u0005\t\u0000\u0000-.\u0005\r\u0000\u0000" +
                    "./\u0006\u0006\uffff\uffff\u0000/\r\u0001\u0000\u0000\u000001\u0005\n" +
                    "\u0000\u000012\u0005\r\u0000\u000023\u0006\u0007\uffff\uffff\u00003\u000f" +
                    "\u0001\u0000\u0000\u000045\u0003\u0000\u0000\u000056\u0006\b\uffff\uffff" +
                    "\u000067\u0003\u0002\u0001\u000078\u0006\b\uffff\uffff\u000089\u0003\u0004" +
                    "\u0002\u00009:\u0006\b\uffff\uffff\u0000:;\u0003\u0006\u0003\u0000;<\u0006" +
                    "\b\uffff\uffff\u0000<=\u0003\b\u0004\u0000=>\u0006\b\uffff\uffff\u0000" +
                    ">?\u0003\n\u0005\u0000?C\u0006\b\uffff\uffff\u0000@A\u0003\f\u0006\u0000" +
                    "AB\u0006\b\uffff\uffff\u0000BD\u0001\u0000\u0000\u0000C@\u0001\u0000\u0000" +
                    "\u0000DE\u0001\u0000\u0000\u0000EC\u0001\u0000\u0000\u0000EF\u0001\u0000" +
                    "\u0000\u0000FG\u0001\u0000\u0000\u0000GH\u0003\u000e\u0007\u0000HI\u0006" +
                    "\b\uffff\uffff\u0000I\u0011\u0001\u0000\u0000\u0000JK\u0003\u0010\b\u0000" +
                    "KL\u0006\t\uffff\uffff\u0000LN\u0001\u0000\u0000\u0000MJ\u0001\u0000\u0000" +
                    "\u0000NO\u0001\u0000\u0000\u0000OM\u0001\u0000\u0000\u0000OP\u0001\u0000" +
                    "\u0000\u0000PQ\u0001\u0000\u0000\u0000QR\u0005\u0000\u0000\u0001R\u0013" +
                    "\u0001\u0000\u0000\u0000\u0002EO";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}