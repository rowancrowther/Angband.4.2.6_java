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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Activations.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.activations;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.parser.activation.ActivationParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class Activations extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, NAME = 2, AIM = 3, LEVEL = 4, POWER = 5, MSG = 6, DESC = 7, EFFECT = 8,
            EFFECT_MESSAGE = 9, DICE = 10, TIME = 11, EFFECT_YX = 12, EXPR = 13, COLON = 14, UCASE = 15,
            INTEGER = 16, SIMPLE_DICE_STRING = 17, COMPLEX_DICE_STRING = 18, COMMENT = 19,
            EOL = 20, NAME_STRING = 21, DESC_STRING = 22, FREE_TEXT = 23, DICE_SIMPLE_VALUE = 24,
            DICE_COMPLEX_VALUE = 25, EXPR_CHAR = 26, EXPR_COLON = 27, EXPR_UCASE = 28, EXPR_OP = 29,
            EXPR_EOL = 30;
    public static final int
            RULE_recordCount = 0, RULE_name = 1, RULE_aim = 2, RULE_level = 3, RULE_power = 4,
            RULE_desc = 5, RULE_msg = 6, RULE_activation = 7, RULE_file = 8, RULE_effect = 9,
            RULE_time = 10, RULE_effectYX = 11, RULE_dice = 12, RULE_expr = 13, RULE_effectMsg = 14,
            RULE_effectBlock = 15;

    private static String[] makeRuleNames() {
        return new String[]{
                "recordCount", "name", "aim", "level", "power", "desc", "msg", "activation",
                "file", "effect", "time", "effectYX", "dice", "expr", "effectMsg", "effectBlock"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'name:'", "'aim:'", "'level:'", "'power:'",
                "'msg:'", "'desc:'", "'effect:'", "'effect-msg:'", "'dice:'", "'time:'",
                "'effect-yx:'", "'expr:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "NAME", "AIM", "LEVEL", "POWER", "MSG", "DESC",
                "EFFECT", "EFFECT_MESSAGE", "DICE", "TIME", "EFFECT_YX", "EXPR", "COLON",
                "UCASE", "INTEGER", "SIMPLE_DICE_STRING", "COMPLEX_DICE_STRING", "COMMENT",
                "EOL", "NAME_STRING", "DESC_STRING", "FREE_TEXT", "DICE_SIMPLE_VALUE",
                "DICE_COMPLEX_VALUE", "EXPR_CHAR", "EXPR_COLON", "EXPR_UCASE", "EXPR_OP",
                "EXPR_EOL"
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
        return "Activations.g4";
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

    public Activations(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RecordCountContext extends ParserRuleContext {
        public int count;
        public Token INTEGER;

        public TerminalNode RECORD_COUNT() {
            return getToken(Activations.RECORD_COUNT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(Activations.INTEGER, 0);
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
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterRecordCount(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitRecordCount(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitRecordCount(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RecordCountContext recordCount() throws RecognitionException {
        RecordCountContext _localctx = new RecordCountContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_recordCount);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(32);
                match(RECORD_COUNT);
                setState(33);
                ((RecordCountContext) _localctx).INTEGER = match(INTEGER);
                ((RecordCountContext) _localctx).count = Integer.parseInt(((RecordCountContext) _localctx).INTEGER.getText());
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
        public Token NAME_STRING;

        public TerminalNode NAME() {
            return getToken(Activations.NAME, 0);
        }

        public TerminalNode NAME_STRING() {
            return getToken(Activations.NAME_STRING, 0);
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
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(36);
                match(NAME);
                setState(37);
                ((NameContext) _localctx).NAME_STRING = match(NAME_STRING);
                ((NameContext) _localctx).nameStr = ((NameContext) _localctx).NAME_STRING.getText();
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
    public static class AimContext extends ParserRuleContext {
        public boolean aimBool;
        public Token INTEGER;

        public TerminalNode AIM() {
            return getToken(Activations.AIM, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(Activations.INTEGER, 0);
        }

        public AimContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_aim;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterAim(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitAim(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitAim(this);
            else return visitor.visitChildren(this);
        }
    }

    public final AimContext aim() throws RecognitionException {
        AimContext _localctx = new AimContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_aim);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(40);
                match(AIM);
                setState(41);
                ((AimContext) _localctx).INTEGER = match(INTEGER);
                ((AimContext) _localctx).aimBool = ((AimContext) _localctx).INTEGER.getText().equals("1");
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
    public static class LevelContext extends ParserRuleContext {
        public int levelInt;
        public Token INTEGER;

        public TerminalNode LEVEL() {
            return getToken(Activations.LEVEL, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(Activations.INTEGER, 0);
        }

        public LevelContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_level;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterLevel(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitLevel(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitLevel(this);
            else return visitor.visitChildren(this);
        }
    }

    public final LevelContext level() throws RecognitionException {
        LevelContext _localctx = new LevelContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_level);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(44);
                match(LEVEL);
                setState(45);
                ((LevelContext) _localctx).INTEGER = match(INTEGER);
                ((LevelContext) _localctx).levelInt = Integer.parseInt(((LevelContext) _localctx).INTEGER.getText());
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
    public static class PowerContext extends ParserRuleContext {
        public int powerInt;
        public Token INTEGER;

        public TerminalNode POWER() {
            return getToken(Activations.POWER, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(Activations.INTEGER, 0);
        }

        public PowerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_power;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterPower(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitPower(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitPower(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PowerContext power() throws RecognitionException {
        PowerContext _localctx = new PowerContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_power);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(48);
                match(POWER);
                setState(49);
                ((PowerContext) _localctx).INTEGER = match(INTEGER);
                ((PowerContext) _localctx).powerInt = Integer.parseInt(((PowerContext) _localctx).INTEGER.getText());
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
        public Token DESC_STRING;

        public TerminalNode DESC() {
            return getToken(Activations.DESC, 0);
        }

        public TerminalNode DESC_STRING() {
            return getToken(Activations.DESC_STRING, 0);
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
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterDesc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitDesc(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitDesc(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DescContext desc() throws RecognitionException {
        DescContext _localctx = new DescContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_desc);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(52);
                match(DESC);
                setState(53);
                ((DescContext) _localctx).DESC_STRING = match(DESC_STRING);
                ((DescContext) _localctx).descStr = ((DescContext) _localctx).DESC_STRING.getText();
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
        public String message;
        public Token DESC_STRING;

        public TerminalNode MSG() {
            return getToken(Activations.MSG, 0);
        }

        public TerminalNode DESC_STRING() {
            return getToken(Activations.DESC_STRING, 0);
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
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterMsg(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitMsg(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitMsg(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MsgContext msg() throws RecognitionException {
        MsgContext _localctx = new MsgContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_msg);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(56);
                match(MSG);
                setState(57);
                ((MsgContext) _localctx).DESC_STRING = match(DESC_STRING);
                ((MsgContext) _localctx).message = ((MsgContext) _localctx).DESC_STRING.getText();
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
    public static class ActivationContext extends ParserRuleContext {
        public ActivationParseRecord activationRecord;
        public NameContext name;
        public AimContext aim;
        public LevelContext level;
        public PowerContext power;
        public MsgContext msg;
        public EffectBlockContext effectBlock;
        public DescContext desc;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public List<AimContext> aim() {
            return getRuleContexts(AimContext.class);
        }

        public AimContext aim(int i) {
            return getRuleContext(AimContext.class, i);
        }

        public List<LevelContext> level() {
            return getRuleContexts(LevelContext.class);
        }

        public LevelContext level(int i) {
            return getRuleContext(LevelContext.class, i);
        }

        public List<PowerContext> power() {
            return getRuleContexts(PowerContext.class);
        }

        public PowerContext power(int i) {
            return getRuleContext(PowerContext.class, i);
        }

        public List<MsgContext> msg() {
            return getRuleContexts(MsgContext.class);
        }

        public MsgContext msg(int i) {
            return getRuleContext(MsgContext.class, i);
        }

        public List<EffectBlockContext> effectBlock() {
            return getRuleContexts(EffectBlockContext.class);
        }

        public EffectBlockContext effectBlock(int i) {
            return getRuleContext(EffectBlockContext.class, i);
        }

        public List<DescContext> desc() {
            return getRuleContexts(DescContext.class);
        }

        public DescContext desc(int i) {
            return getRuleContext(DescContext.class, i);
        }

        public ActivationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_activation;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterActivation(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitActivation(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitActivation(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ActivationContext activation() throws RecognitionException {
        ActivationContext _localctx = new ActivationContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_activation);

        String nameInit = "";
        boolean aimInit = false;
        int levelInit = 0;
        int powerInit = 0;
        String messageInit = "";
        String descInit = "";
        List<List<String>> effectsInit = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(60);
                ((ActivationContext) _localctx).name = name();

                nameInit = ((ActivationContext) _localctx).name.nameStr;

                setState(80);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        setState(80);
                        _errHandler.sync(this);
                        switch (_input.LA(1)) {
                            case AIM: {
                                setState(62);
                                ((ActivationContext) _localctx).aim = aim();
                                aimInit = ((ActivationContext) _localctx).aim.aimBool;
                            }
                            break;
                            case LEVEL: {
                                setState(65);
                                ((ActivationContext) _localctx).level = level();
                                levelInit = ((ActivationContext) _localctx).level.levelInt;
                            }
                            break;
                            case POWER: {
                                setState(68);
                                ((ActivationContext) _localctx).power = power();
                                powerInit = ((ActivationContext) _localctx).power.powerInt;
                            }
                            break;
                            case MSG: {
                                setState(71);
                                ((ActivationContext) _localctx).msg = msg();
                                messageInit = ((ActivationContext) _localctx).msg.message;
                            }
                            break;
                            case EFFECT: {
                                setState(74);
                                ((ActivationContext) _localctx).effectBlock = effectBlock();

                                List<String> effectValues = new ArrayList<>();
                                effectValues.add(((ActivationContext) _localctx).effectBlock.typeInit);
                                effectValues.add(((ActivationContext) _localctx).effectBlock.subtypeWrapperInit);
                                effectValues.add(((ActivationContext) _localctx).effectBlock.radius);
                                effectValues.add(((ActivationContext) _localctx).effectBlock.other);
                                effectValues.add(((ActivationContext) _localctx).effectBlock.diceString);
                                effectValues.add(((ActivationContext) _localctx).effectBlock.yVal);
                                effectValues.add(((ActivationContext) _localctx).effectBlock.xVal);
                                effectValues.add(((ActivationContext) _localctx).effectBlock.expressionChars);
                                effectValues.add(((ActivationContext) _localctx).effectBlock.expressionBase);
                                effectValues.add(((ActivationContext) _localctx).effectBlock.expressionOperation);
                                effectValues.add(((ActivationContext) _localctx).effectBlock.timeDiceString);
                                effectValues.add(((ActivationContext) _localctx).effectBlock.effectMessage);

                                effectsInit.add(effectValues);

                            }
                            break;
                            case DESC: {
                                setState(77);
                                ((ActivationContext) _localctx).desc = desc();
                                descInit = ((ActivationContext) _localctx).desc.descStr;
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                    }
                    setState(82);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 504L) != 0));
            }
            _ctx.stop = _input.LT(-1);

            ((ActivationContext) _localctx).activationRecord =
                    new ActivationParseRecord(nameInit,
                            aimInit, levelInit, powerInit,
                            messageInit, descInit, effectsInit,
                            _localctx.start.getLine());

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
        public List<ActivationParseRecord> records;
        public int declaredCount;
        public RecordCountContext recordCount;
        public ActivationContext activation;

        public RecordCountContext recordCount() {
            return getRuleContext(RecordCountContext.class, 0);
        }

        public TerminalNode EOF() {
            return getToken(Activations.EOF, 0);
        }

        public List<ActivationContext> activation() {
            return getRuleContexts(ActivationContext.class);
        }

        public ActivationContext activation(int i) {
            return getRuleContext(ActivationContext.class, i);
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
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_file);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {

                ((FileContext) _localctx).records = new ArrayList<>();

                setState(85);
                ((FileContext) _localctx).recordCount = recordCount();
                ((FileContext) _localctx).declaredCount = ((FileContext) _localctx).recordCount.count;
                setState(90);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(87);
                            ((FileContext) _localctx).activation = activation();

                            _localctx.records.add(((FileContext) _localctx).activation.activationRecord);

                        }
                    }
                    setState(92);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(94);
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

    @SuppressWarnings("CheckReturnValue")
    public static class EffectContext extends ParserRuleContext {
        public String type;
        public String wrapper;
        public String radius;
        public String other;
        public Token t;
        public Token st;
        public Token rad;
        public Token oth;

        public TerminalNode EFFECT() {
            return getToken(Activations.EFFECT, 0);
        }

        public List<TerminalNode> UCASE() {
            return getTokens(Activations.UCASE);
        }

        public TerminalNode UCASE(int i) {
            return getToken(Activations.UCASE, i);
        }

        public List<TerminalNode> COLON() {
            return getTokens(Activations.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(Activations.COLON, i);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(Activations.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(Activations.INTEGER, i);
        }

        public EffectContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_effect;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterEffect(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitEffect(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitEffect(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectContext effect() throws RecognitionException {
        EffectContext _localctx = new EffectContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_effect);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(96);
                match(EFFECT);
                setState(97);
                ((EffectContext) _localctx).t = match(UCASE);

                ((EffectContext) _localctx).type = ((EffectContext) _localctx).t.getText();

                setState(112);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COLON) {
                    {
                        setState(99);
                        match(COLON);
                        setState(100);
                        ((EffectContext) _localctx).st = match(UCASE);

                        ((EffectContext) _localctx).wrapper = ((EffectContext) _localctx).st.getText().toUpperCase();

                        setState(110);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == COLON) {
                            {
                                setState(102);
                                match(COLON);
                                setState(103);
                                ((EffectContext) _localctx).rad = match(INTEGER);

                                ((EffectContext) _localctx).radius = ((EffectContext) _localctx).rad.getText();

                                setState(108);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                                if (_la == COLON) {
                                    {
                                        setState(105);
                                        match(COLON);
                                        setState(106);
                                        ((EffectContext) _localctx).oth = match(INTEGER);

                                        ((EffectContext) _localctx).other = ((EffectContext) _localctx).oth.getText();

                                    }
                                }

                            }
                        }

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
    public static class TimeContext extends ParserRuleContext {
        public String timeStr;
        public Token DICE_SIMPLE_VALUE;

        public TerminalNode TIME() {
            return getToken(Activations.TIME, 0);
        }

        public TerminalNode DICE_SIMPLE_VALUE() {
            return getToken(Activations.DICE_SIMPLE_VALUE, 0);
        }

        public TimeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_time;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterTime(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitTime(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitTime(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TimeContext time() throws RecognitionException {
        TimeContext _localctx = new TimeContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_time);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(114);
                match(TIME);
                setState(115);
                ((TimeContext) _localctx).DICE_SIMPLE_VALUE = match(DICE_SIMPLE_VALUE);

                ((TimeContext) _localctx).timeStr = ((TimeContext) _localctx).DICE_SIMPLE_VALUE.getText();

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
    public static class EffectYXContext extends ParserRuleContext {
        public String y;
        public String x;
        public Token yVal;
        public Token xVal;

        public TerminalNode EFFECT_YX() {
            return getToken(Activations.EFFECT_YX, 0);
        }

        public TerminalNode COLON() {
            return getToken(Activations.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(Activations.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(Activations.INTEGER, i);
        }

        public EffectYXContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_effectYX;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterEffectYX(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitEffectYX(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitEffectYX(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectYXContext effectYX() throws RecognitionException {
        EffectYXContext _localctx = new EffectYXContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_effectYX);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(118);
                match(EFFECT_YX);
                setState(119);
                ((EffectYXContext) _localctx).yVal = match(INTEGER);
                setState(120);
                match(COLON);
                setState(121);
                ((EffectYXContext) _localctx).xVal = match(INTEGER);

                ((EffectYXContext) _localctx).y = ((EffectYXContext) _localctx).yVal.getText();
                ((EffectYXContext) _localctx).x = ((EffectYXContext) _localctx).xVal.getText();

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
    public static class DiceContext extends ParserRuleContext {
        public String diceString;
        public String exprChar;
        public String baseName;
        public String operation;
        public Token val;
        public ExprContext expr;

        public TerminalNode DICE() {
            return getToken(Activations.DICE, 0);
        }

        public TerminalNode DICE_SIMPLE_VALUE() {
            return getToken(Activations.DICE_SIMPLE_VALUE, 0);
        }

        public TerminalNode DICE_COMPLEX_VALUE() {
            return getToken(Activations.DICE_COMPLEX_VALUE, 0);
        }

        public List<ExprContext> expr() {
            return getRuleContexts(ExprContext.class);
        }

        public ExprContext expr(int i) {
            return getRuleContext(ExprContext.class, i);
        }

        public DiceContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_dice;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterDice(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitDice(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitDice(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DiceContext dice() throws RecognitionException {
        DiceContext _localctx = new DiceContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_dice);

        String charHolder = "";
        String baseHolder = "";
        String operHolder = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(124);
                match(DICE);
                setState(136);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case DICE_COMPLEX_VALUE: {
                        {
                            setState(125);
                            ((DiceContext) _localctx).val = match(DICE_COMPLEX_VALUE);

                            ((DiceContext) _localctx).diceString = ((DiceContext) _localctx).val.getText();

                            setState(130);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            do {
                                {
                                    {
                                        setState(127);
                                        ((DiceContext) _localctx).expr = expr();

                                        if (charHolder.isEmpty()) {
                                            charHolder = ((DiceContext) _localctx).expr.exprChar;
                                            baseHolder = ((DiceContext) _localctx).expr.baseName;
                                            operHolder = ((DiceContext) _localctx).expr.operation;
                                        } else {
                                            charHolder = charHolder + "^" + ((DiceContext) _localctx).expr.exprChar;
                                            baseHolder = baseHolder + "^" + ((DiceContext) _localctx).expr.baseName;
                                            operHolder = operHolder + "^" + ((DiceContext) _localctx).expr.operation;
                                        }

                                    }
                                }
                                setState(132);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                            } while (_la == EXPR);
                        }
                    }
                    break;
                    case DICE_SIMPLE_VALUE: {
                        setState(134);
                        ((DiceContext) _localctx).val = match(DICE_SIMPLE_VALUE);

                        ((DiceContext) _localctx).diceString = ((DiceContext) _localctx).val.getText();

                    }
                    break;
                    default:
                        throw new NoViableAltException(this);
                }
            }
            _ctx.stop = _input.LT(-1);

            ((DiceContext) _localctx).exprChar = charHolder;
            ((DiceContext) _localctx).baseName = baseHolder;
            ((DiceContext) _localctx).operation = operHolder;

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
    public static class ExprContext extends ParserRuleContext {
        public String exprChar;
        public String baseName;
        public String operation;
        public Token ch;
        public Token base;
        public Token op;

        public TerminalNode EXPR() {
            return getToken(Activations.EXPR, 0);
        }

        public List<TerminalNode> EXPR_COLON() {
            return getTokens(Activations.EXPR_COLON);
        }

        public TerminalNode EXPR_COLON(int i) {
            return getToken(Activations.EXPR_COLON, i);
        }

        public TerminalNode EXPR_CHAR() {
            return getToken(Activations.EXPR_CHAR, 0);
        }

        public TerminalNode EXPR_UCASE() {
            return getToken(Activations.EXPR_UCASE, 0);
        }

        public TerminalNode EXPR_OP() {
            return getToken(Activations.EXPR_OP, 0);
        }

        public ExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_expr;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitExpr(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExprContext expr() throws RecognitionException {
        ExprContext _localctx = new ExprContext(_ctx, getState());
        enterRule(_localctx, 26, RULE_expr);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(138);
                match(EXPR);
                setState(139);
                ((ExprContext) _localctx).ch = match(EXPR_CHAR);
                setState(140);
                match(EXPR_COLON);
                setState(141);
                ((ExprContext) _localctx).base = match(EXPR_UCASE);
                setState(142);
                match(EXPR_COLON);
                setState(143);
                ((ExprContext) _localctx).op = match(EXPR_OP);

                ((ExprContext) _localctx).exprChar = ((ExprContext) _localctx).ch.getText();
                ((ExprContext) _localctx).baseName = ((ExprContext) _localctx).base.getText();
                ((ExprContext) _localctx).operation = ((ExprContext) _localctx).op.getText();

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
    public static class EffectMsgContext extends ParserRuleContext {
        public String message;
        public Token FREE_TEXT;

        public TerminalNode EFFECT_MESSAGE() {
            return getToken(Activations.EFFECT_MESSAGE, 0);
        }

        public TerminalNode FREE_TEXT() {
            return getToken(Activations.FREE_TEXT, 0);
        }

        public EffectMsgContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_effectMsg;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterEffectMsg(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitEffectMsg(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitEffectMsg(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectMsgContext effectMsg() throws RecognitionException {
        EffectMsgContext _localctx = new EffectMsgContext(_ctx, getState());
        enterRule(_localctx, 28, RULE_effectMsg);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(146);
                match(EFFECT_MESSAGE);
                setState(147);
                ((EffectMsgContext) _localctx).FREE_TEXT = match(FREE_TEXT);
                ((EffectMsgContext) _localctx).message = ((EffectMsgContext) _localctx).FREE_TEXT.getText();
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
    public static class EffectBlockContext extends ParserRuleContext {
        public String typeInit;
        public String subtypeWrapperInit;
        public String radius;
        public String other;
        public String diceString;
        public String yVal;
        public String xVal;
        public String expressionChars;
        public String expressionBase;
        public String expressionOperation;
        public String timeDiceString;
        public String effectMessage;
        public EffectContext effect;
        public EffectYXContext effectYX;
        public DiceContext dice;
        public TimeContext time;
        public EffectMsgContext effectMsg;

        public EffectContext effect() {
            return getRuleContext(EffectContext.class, 0);
        }

        public TimeContext time() {
            return getRuleContext(TimeContext.class, 0);
        }

        public EffectMsgContext effectMsg() {
            return getRuleContext(EffectMsgContext.class, 0);
        }

        public EffectYXContext effectYX() {
            return getRuleContext(EffectYXContext.class, 0);
        }

        public DiceContext dice() {
            return getRuleContext(DiceContext.class, 0);
        }

        public EffectBlockContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_effectBlock;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).enterEffectBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ActivationsListener) ((ActivationsListener) listener).exitEffectBlock(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ActivationsVisitor)
                return ((ActivationsVisitor<? extends T>) visitor).visitEffectBlock(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectBlockContext effectBlock() throws RecognitionException {
        EffectBlockContext _localctx = new EffectBlockContext(_ctx, getState());
        enterRule(_localctx, 30, RULE_effectBlock);

        String expressionString = "";
        String baseString = "";
        String opString = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(150);
                ((EffectBlockContext) _localctx).effect = effect();

                ((EffectBlockContext) _localctx).typeInit = ((EffectBlockContext) _localctx).effect.type;
                ((EffectBlockContext) _localctx).subtypeWrapperInit = ((EffectBlockContext) _localctx).effect.wrapper;
                ((EffectBlockContext) _localctx).radius = ((EffectBlockContext) _localctx).effect.radius;
                ((EffectBlockContext) _localctx).other = ((EffectBlockContext) _localctx).effect.other;

                setState(160);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case EFFECT_YX: {
                        {
                            setState(152);
                            ((EffectBlockContext) _localctx).effectYX = effectYX();

                            ((EffectBlockContext) _localctx).yVal = ((EffectBlockContext) _localctx).effectYX.y;
                            ((EffectBlockContext) _localctx).xVal = ((EffectBlockContext) _localctx).effectYX.x;

                        }
                    }
                    break;
                    case EOF:
                    case NAME:
                    case AIM:
                    case LEVEL:
                    case POWER:
                    case MSG:
                    case DESC:
                    case EFFECT:
                    case EFFECT_MESSAGE:
                    case DICE:
                    case TIME: {
                        {
                            setState(158);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            if (_la == DICE) {
                                {
                                    setState(155);
                                    ((EffectBlockContext) _localctx).dice = dice();

                                    ((EffectBlockContext) _localctx).diceString = ((EffectBlockContext) _localctx).dice.diceString;
                                    expressionString = ((EffectBlockContext) _localctx).dice.exprChar;
                                    baseString = ((EffectBlockContext) _localctx).dice.baseName;
                                    opString = ((EffectBlockContext) _localctx).dice.operation;

                                }
                            }

                        }
                    }
                    break;
                    default:
                        throw new NoViableAltException(this);
                }
                setState(165);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == TIME) {
                    {
                        setState(162);
                        ((EffectBlockContext) _localctx).time = time();

                        ((EffectBlockContext) _localctx).timeDiceString = ((EffectBlockContext) _localctx).time.timeStr;

                    }
                }

                setState(170);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == EFFECT_MESSAGE) {
                    {
                        setState(167);
                        ((EffectBlockContext) _localctx).effectMsg = effectMsg();
                        ((EffectBlockContext) _localctx).effectMessage = ((EffectBlockContext) _localctx).effectMsg.message;
                    }
                }

            }
            _ctx.stop = _input.LT(-1);

            ((EffectBlockContext) _localctx).expressionChars = expressionString;
            ((EffectBlockContext) _localctx).expressionBase = baseString;
            ((EffectBlockContext) _localctx).expressionOperation = opString;

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
            "\u0004\u0001\u001e\u00ad\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001" +
                    "\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004" +
                    "\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007" +
                    "\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b" +
                    "\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007" +
                    "\u000f\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0004\u0007Q\b\u0007\u000b\u0007\f\u0007R\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0004\b[\b\b\u000b\b\f\b\\\u0001\b\u0001\b" +
                    "\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0003\tm\b\t\u0003\to\b\t\u0003\tq\b\t\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001" +
                    "\f\u0004\f\u0083\b\f\u000b\f\f\f\u0084\u0001\f\u0001\f\u0003\f\u0089\b" +
                    "\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001" +
                    "\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001" +
                    "\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0003" +
                    "\u000f\u009f\b\u000f\u0003\u000f\u00a1\b\u000f\u0001\u000f\u0001\u000f" +
                    "\u0001\u000f\u0003\u000f\u00a6\b\u000f\u0001\u000f\u0001\u000f\u0001\u000f" +
                    "\u0003\u000f\u00ab\b\u000f\u0001\u000f\u0000\u0000\u0010\u0000\u0002\u0004" +
                    "\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e\u0000" +
                    "\u0000\u00ac\u0000 \u0001\u0000\u0000\u0000\u0002$\u0001\u0000\u0000\u0000" +
                    "\u0004(\u0001\u0000\u0000\u0000\u0006,\u0001\u0000\u0000\u0000\b0\u0001" +
                    "\u0000\u0000\u0000\n4\u0001\u0000\u0000\u0000\f8\u0001\u0000\u0000\u0000" +
                    "\u000e<\u0001\u0000\u0000\u0000\u0010T\u0001\u0000\u0000\u0000\u0012`" +
                    "\u0001\u0000\u0000\u0000\u0014r\u0001\u0000\u0000\u0000\u0016v\u0001\u0000" +
                    "\u0000\u0000\u0018|\u0001\u0000\u0000\u0000\u001a\u008a\u0001\u0000\u0000" +
                    "\u0000\u001c\u0092\u0001\u0000\u0000\u0000\u001e\u0096\u0001\u0000\u0000" +
                    "\u0000 !\u0005\u0001\u0000\u0000!\"\u0005\u0010\u0000\u0000\"#\u0006\u0000" +
                    "\uffff\uffff\u0000#\u0001\u0001\u0000\u0000\u0000$%\u0005\u0002\u0000" +
                    "\u0000%&\u0005\u0015\u0000\u0000&\'\u0006\u0001\uffff\uffff\u0000\'\u0003" +
                    "\u0001\u0000\u0000\u0000()\u0005\u0003\u0000\u0000)*\u0005\u0010\u0000" +
                    "\u0000*+\u0006\u0002\uffff\uffff\u0000+\u0005\u0001\u0000\u0000\u0000" +
                    ",-\u0005\u0004\u0000\u0000-.\u0005\u0010\u0000\u0000./\u0006\u0003\uffff" +
                    "\uffff\u0000/\u0007\u0001\u0000\u0000\u000001\u0005\u0005\u0000\u0000" +
                    "12\u0005\u0010\u0000\u000023\u0006\u0004\uffff\uffff\u00003\t\u0001\u0000" +
                    "\u0000\u000045\u0005\u0007\u0000\u000056\u0005\u0016\u0000\u000067\u0006" +
                    "\u0005\uffff\uffff\u00007\u000b\u0001\u0000\u0000\u000089\u0005\u0006" +
                    "\u0000\u00009:\u0005\u0016\u0000\u0000:;\u0006\u0006\uffff\uffff\u0000" +
                    ";\r\u0001\u0000\u0000\u0000<=\u0003\u0002\u0001\u0000=P\u0006\u0007\uffff" +
                    "\uffff\u0000>?\u0003\u0004\u0002\u0000?@\u0006\u0007\uffff\uffff\u0000" +
                    "@Q\u0001\u0000\u0000\u0000AB\u0003\u0006\u0003\u0000BC\u0006\u0007\uffff" +
                    "\uffff\u0000CQ\u0001\u0000\u0000\u0000DE\u0003\b\u0004\u0000EF\u0006\u0007" +
                    "\uffff\uffff\u0000FQ\u0001\u0000\u0000\u0000GH\u0003\f\u0006\u0000HI\u0006" +
                    "\u0007\uffff\uffff\u0000IQ\u0001\u0000\u0000\u0000JK\u0003\u001e\u000f" +
                    "\u0000KL\u0006\u0007\uffff\uffff\u0000LQ\u0001\u0000\u0000\u0000MN\u0003" +
                    "\n\u0005\u0000NO\u0006\u0007\uffff\uffff\u0000OQ\u0001\u0000\u0000\u0000" +
                    "P>\u0001\u0000\u0000\u0000PA\u0001\u0000\u0000\u0000PD\u0001\u0000\u0000" +
                    "\u0000PG\u0001\u0000\u0000\u0000PJ\u0001\u0000\u0000\u0000PM\u0001\u0000" +
                    "\u0000\u0000QR\u0001\u0000\u0000\u0000RP\u0001\u0000\u0000\u0000RS\u0001" +
                    "\u0000\u0000\u0000S\u000f\u0001\u0000\u0000\u0000TU\u0006\b\uffff\uffff" +
                    "\u0000UV\u0003\u0000\u0000\u0000VZ\u0006\b\uffff\uffff\u0000WX\u0003\u000e" +
                    "\u0007\u0000XY\u0006\b\uffff\uffff\u0000Y[\u0001\u0000\u0000\u0000ZW\u0001" +
                    "\u0000\u0000\u0000[\\\u0001\u0000\u0000\u0000\\Z\u0001\u0000\u0000\u0000" +
                    "\\]\u0001\u0000\u0000\u0000]^\u0001\u0000\u0000\u0000^_\u0005\u0000\u0000" +
                    "\u0001_\u0011\u0001\u0000\u0000\u0000`a\u0005\b\u0000\u0000ab\u0005\u000f" +
                    "\u0000\u0000bp\u0006\t\uffff\uffff\u0000cd\u0005\u000e\u0000\u0000de\u0005" +
                    "\u000f\u0000\u0000en\u0006\t\uffff\uffff\u0000fg\u0005\u000e\u0000\u0000" +
                    "gh\u0005\u0010\u0000\u0000hl\u0006\t\uffff\uffff\u0000ij\u0005\u000e\u0000" +
                    "\u0000jk\u0005\u0010\u0000\u0000km\u0006\t\uffff\uffff\u0000li\u0001\u0000" +
                    "\u0000\u0000lm\u0001\u0000\u0000\u0000mo\u0001\u0000\u0000\u0000nf\u0001" +
                    "\u0000\u0000\u0000no\u0001\u0000\u0000\u0000oq\u0001\u0000\u0000\u0000" +
                    "pc\u0001\u0000\u0000\u0000pq\u0001\u0000\u0000\u0000q\u0013\u0001\u0000" +
                    "\u0000\u0000rs\u0005\u000b\u0000\u0000st\u0005\u0018\u0000\u0000tu\u0006" +
                    "\n\uffff\uffff\u0000u\u0015\u0001\u0000\u0000\u0000vw\u0005\f\u0000\u0000" +
                    "wx\u0005\u0010\u0000\u0000xy\u0005\u000e\u0000\u0000yz\u0005\u0010\u0000" +
                    "\u0000z{\u0006\u000b\uffff\uffff\u0000{\u0017\u0001\u0000\u0000\u0000" +
                    "|\u0088\u0005\n\u0000\u0000}~\u0005\u0019\u0000\u0000~\u0082\u0006\f\uffff" +
                    "\uffff\u0000\u007f\u0080\u0003\u001a\r\u0000\u0080\u0081\u0006\f\uffff" +
                    "\uffff\u0000\u0081\u0083\u0001\u0000\u0000\u0000\u0082\u007f\u0001\u0000" +
                    "\u0000\u0000\u0083\u0084\u0001\u0000\u0000\u0000\u0084\u0082\u0001\u0000" +
                    "\u0000\u0000\u0084\u0085\u0001\u0000\u0000\u0000\u0085\u0089\u0001\u0000" +
                    "\u0000\u0000\u0086\u0087\u0005\u0018\u0000\u0000\u0087\u0089\u0006\f\uffff" +
                    "\uffff\u0000\u0088}\u0001\u0000\u0000\u0000\u0088\u0086\u0001\u0000\u0000" +
                    "\u0000\u0089\u0019\u0001\u0000\u0000\u0000\u008a\u008b\u0005\r\u0000\u0000" +
                    "\u008b\u008c\u0005\u001a\u0000\u0000\u008c\u008d\u0005\u001b\u0000\u0000" +
                    "\u008d\u008e\u0005\u001c\u0000\u0000\u008e\u008f\u0005\u001b\u0000\u0000" +
                    "\u008f\u0090\u0005\u001d\u0000\u0000\u0090\u0091\u0006\r\uffff\uffff\u0000" +
                    "\u0091\u001b\u0001\u0000\u0000\u0000\u0092\u0093\u0005\t\u0000\u0000\u0093" +
                    "\u0094\u0005\u0017\u0000\u0000\u0094\u0095\u0006\u000e\uffff\uffff\u0000" +
                    "\u0095\u001d\u0001\u0000\u0000\u0000\u0096\u0097\u0003\u0012\t\u0000\u0097" +
                    "\u00a0\u0006\u000f\uffff\uffff\u0000\u0098\u0099\u0003\u0016\u000b\u0000" +
                    "\u0099\u009a\u0006\u000f\uffff\uffff\u0000\u009a\u00a1\u0001\u0000\u0000" +
                    "\u0000\u009b\u009c\u0003\u0018\f\u0000\u009c\u009d\u0006\u000f\uffff\uffff" +
                    "\u0000\u009d\u009f\u0001\u0000\u0000\u0000\u009e\u009b\u0001\u0000\u0000" +
                    "\u0000\u009e\u009f\u0001\u0000\u0000\u0000\u009f\u00a1\u0001\u0000\u0000" +
                    "\u0000\u00a0\u0098\u0001\u0000\u0000\u0000\u00a0\u009e\u0001\u0000\u0000" +
                    "\u0000\u00a1\u00a5\u0001\u0000\u0000\u0000\u00a2\u00a3\u0003\u0014\n\u0000" +
                    "\u00a3\u00a4\u0006\u000f\uffff\uffff\u0000\u00a4\u00a6\u0001\u0000\u0000" +
                    "\u0000\u00a5\u00a2\u0001\u0000\u0000\u0000\u00a5\u00a6\u0001\u0000\u0000" +
                    "\u0000\u00a6\u00aa\u0001\u0000\u0000\u0000\u00a7\u00a8\u0003\u001c\u000e" +
                    "\u0000\u00a8\u00a9\u0006\u000f\uffff\uffff\u0000\u00a9\u00ab\u0001\u0000" +
                    "\u0000\u0000\u00aa\u00a7\u0001\u0000\u0000\u0000\u00aa\u00ab\u0001\u0000" +
                    "\u0000\u0000\u00ab\u001f\u0001\u0000\u0000\u0000\fPR\\lnp\u0084\u0088" +
                    "\u009e\u00a0\u00a5\u00aa";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
	}
}