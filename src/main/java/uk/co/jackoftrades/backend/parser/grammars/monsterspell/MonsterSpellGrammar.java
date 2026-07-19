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

// Generated from MonsterSpellGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.monsterspell;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.parser.grammars.EffectParseRecord;
import uk.co.jackoftrades.backend.parser.monsterspell.MonsterSpellParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class MonsterSpellGrammar extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, NAME = 2, MSGT = 3, HIT = 4, POWER_CUTOFF = 5, LORE = 6, LORE_COLOUR_BASE = 7,
            LORE_COLOUR_RESIST = 8, LORE_COLOUR_IMMUNE = 9, MESSAGE_SAVE = 10, MESSAGE_VIS = 11,
            MESSAGE_INVIS = 12, MESSAGE_MISS = 13, COMMENT = 14, EOL = 15, EFFECT = 16, EFFECT_MESSAGE = 17,
            DICE = 18, TIME = 19, EFFECT_YX = 20, EXPR = 21, COLON = 22, UCASE = 23, INTEGER = 24,
            SIMPLE_DICE_STRING = 25, COMPLEX_DICE_STRING = 26, STRING = 27, FREE_TEXT_EOL = 28,
            FREE_TEXT = 29, DICE_SIMPLE_VALUE = 30, DICE_COMPLEX_VALUE = 31, EXPR_CHAR = 32,
            EXPR_COLON = 33, EXPR_UCASE = 34, EXPR_OP = 35, EXPR_EOL = 36;
    public static final int
            RULE_recordCount = 0, RULE_name = 1, RULE_msgt = 2, RULE_hit = 3, RULE_powerCutoff = 4,
            RULE_lore = 5, RULE_loreColourBase = 6, RULE_loreColourResist = 7, RULE_loreColourImmune = 8,
            RULE_messageSave = 9, RULE_messageVis = 10, RULE_messageInvis = 11, RULE_messageMiss = 12,
            RULE_powerCutoffBlock = 13, RULE_monsterSpell = 14, RULE_file = 15, RULE_effect = 16,
            RULE_time = 17, RULE_effectYX = 18, RULE_dice = 19, RULE_expr = 20, RULE_effectMsg = 21,
            RULE_effectBlock = 22;

    private static String[] makeRuleNames() {
        return new String[]{
                "recordCount", "name", "msgt", "hit", "powerCutoff", "lore", "loreColourBase",
                "loreColourResist", "loreColourImmune", "messageSave", "messageVis",
                "messageInvis", "messageMiss", "powerCutoffBlock", "monsterSpell", "file",
                "effect", "time", "effectYX", "dice", "expr", "effectMsg", "effectBlock"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'name:'", "'msgt:'", "'hit:'", "'power-cutoff:'",
                "'lore:'", "'lore-color-base:'", "'lore-color-resist:'", "'lore-color-immune:'",
                "'message-save:'", "'message-vis:'", "'message-invis:'", "'message-miss:'",
                null, null, "'effect:'", "'effect-msg:'", "'dice:'", "'time:'", "'effect-yx:'",
                "'expr:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "NAME", "MSGT", "HIT", "POWER_CUTOFF", "LORE",
                "LORE_COLOUR_BASE", "LORE_COLOUR_RESIST", "LORE_COLOUR_IMMUNE", "MESSAGE_SAVE",
                "MESSAGE_VIS", "MESSAGE_INVIS", "MESSAGE_MISS", "COMMENT", "EOL", "EFFECT",
                "EFFECT_MESSAGE", "DICE", "TIME", "EFFECT_YX", "EXPR", "COLON", "UCASE",
                "INTEGER", "SIMPLE_DICE_STRING", "COMPLEX_DICE_STRING", "STRING", "FREE_TEXT_EOL",
                "FREE_TEXT", "DICE_SIMPLE_VALUE", "DICE_COMPLEX_VALUE", "EXPR_CHAR",
                "EXPR_COLON", "EXPR_UCASE", "EXPR_OP", "EXPR_EOL"
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
        return "MonsterSpellGrammar.g4";
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

    public MonsterSpellGrammar(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RecordCountContext extends ParserRuleContext {
        public String count;
        public Token INTEGER;

        public TerminalNode RECORD_COUNT() {
            return getToken(MonsterSpellGrammar.RECORD_COUNT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(MonsterSpellGrammar.INTEGER, 0);
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
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).enterRecordCount(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).exitRecordCount(this);
        }
    }

    public final RecordCountContext recordCount() throws RecognitionException {
        RecordCountContext _localctx = new RecordCountContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_recordCount);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(46);
                match(RECORD_COUNT);
                setState(47);
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
        public int line;
        public Token STRING;

        public TerminalNode NAME() {
            return getToken(MonsterSpellGrammar.NAME, 0);
        }

        public TerminalNode STRING() {
            return getToken(MonsterSpellGrammar.STRING, 0);
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
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).exitName(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(50);
                match(NAME);
                setState(51);
                ((NameContext) _localctx).STRING = match(STRING);
                ((NameContext) _localctx).nameStr = ((NameContext) _localctx).STRING.getText();
                ((NameContext) _localctx).line = _localctx.start.getLine();
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
    public static class MsgtContext extends ParserRuleContext {
        public String msgTStr;
        public Token STRING;

        public TerminalNode MSGT() {
            return getToken(MonsterSpellGrammar.MSGT, 0);
        }

        public TerminalNode STRING() {
            return getToken(MonsterSpellGrammar.STRING, 0);
        }

        public MsgtContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_msgt;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).enterMsgt(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).exitMsgt(this);
        }
    }

    public final MsgtContext msgt() throws RecognitionException {
        MsgtContext _localctx = new MsgtContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_msgt);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(54);
                match(MSGT);
                setState(55);
                ((MsgtContext) _localctx).STRING = match(STRING);
                ((MsgtContext) _localctx).msgTStr = ((MsgtContext) _localctx).STRING.getText();
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
    public static class HitContext extends ParserRuleContext {
        public String hitVal;
        public Token INTEGER;

        public TerminalNode HIT() {
            return getToken(MonsterSpellGrammar.HIT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(MonsterSpellGrammar.INTEGER, 0);
        }

        public HitContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_hit;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).enterHit(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener) ((MonsterSpellGrammarListener) listener).exitHit(this);
        }
    }

    public final HitContext hit() throws RecognitionException {
        HitContext _localctx = new HitContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_hit);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(58);
                match(HIT);
                setState(59);
                ((HitContext) _localctx).INTEGER = match(INTEGER);
                ((HitContext) _localctx).hitVal = ((HitContext) _localctx).INTEGER.getText();
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
    public static class PowerCutoffContext extends ParserRuleContext {
        public String cutoffVal;
        public Token INTEGER;

        public TerminalNode POWER_CUTOFF() {
            return getToken(MonsterSpellGrammar.POWER_CUTOFF, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(MonsterSpellGrammar.INTEGER, 0);
        }

        public PowerCutoffContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_powerCutoff;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).enterPowerCutoff(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).exitPowerCutoff(this);
        }
    }

    public final PowerCutoffContext powerCutoff() throws RecognitionException {
        PowerCutoffContext _localctx = new PowerCutoffContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_powerCutoff);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(62);
                match(POWER_CUTOFF);
                setState(63);
                ((PowerCutoffContext) _localctx).INTEGER = match(INTEGER);
                ((PowerCutoffContext) _localctx).cutoffVal = ((PowerCutoffContext) _localctx).INTEGER.getText();
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
    public static class LoreContext extends ParserRuleContext {
        public String loreStr;
        public Token STRING;

        public TerminalNode LORE() {
            return getToken(MonsterSpellGrammar.LORE, 0);
        }

        public TerminalNode STRING() {
            return getToken(MonsterSpellGrammar.STRING, 0);
        }

        public LoreContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_lore;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).enterLore(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).exitLore(this);
        }
    }

    public final LoreContext lore() throws RecognitionException {
        LoreContext _localctx = new LoreContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_lore);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(66);
                match(LORE);
                setState(67);
                ((LoreContext) _localctx).STRING = match(STRING);
                ((LoreContext) _localctx).loreStr = ((LoreContext) _localctx).STRING.getText();
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
    public static class LoreColourBaseContext extends ParserRuleContext {
        public String colourBase;
        public Token STRING;

        public TerminalNode LORE_COLOUR_BASE() {
            return getToken(MonsterSpellGrammar.LORE_COLOUR_BASE, 0);
        }

        public TerminalNode STRING() {
            return getToken(MonsterSpellGrammar.STRING, 0);
        }

        public LoreColourBaseContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_loreColourBase;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).enterLoreColourBase(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).exitLoreColourBase(this);
        }
    }

    public final LoreColourBaseContext loreColourBase() throws RecognitionException {
        LoreColourBaseContext _localctx = new LoreColourBaseContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_loreColourBase);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(70);
                match(LORE_COLOUR_BASE);
                setState(71);
                ((LoreColourBaseContext) _localctx).STRING = match(STRING);
                ((LoreColourBaseContext) _localctx).colourBase = ((LoreColourBaseContext) _localctx).STRING.getText();
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
    public static class LoreColourResistContext extends ParserRuleContext {
        public String colourResist;
        public Token STRING;

        public TerminalNode LORE_COLOUR_RESIST() {
            return getToken(MonsterSpellGrammar.LORE_COLOUR_RESIST, 0);
        }

        public TerminalNode STRING() {
            return getToken(MonsterSpellGrammar.STRING, 0);
        }

        public LoreColourResistContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_loreColourResist;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).enterLoreColourResist(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).exitLoreColourResist(this);
        }
    }

    public final LoreColourResistContext loreColourResist() throws RecognitionException {
        LoreColourResistContext _localctx = new LoreColourResistContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_loreColourResist);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(74);
                match(LORE_COLOUR_RESIST);
                setState(75);
                ((LoreColourResistContext) _localctx).STRING = match(STRING);
                ((LoreColourResistContext) _localctx).colourResist = ((LoreColourResistContext) _localctx).STRING.getText();
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
    public static class LoreColourImmuneContext extends ParserRuleContext {
        public String colourImmune;
        public Token STRING;

        public TerminalNode LORE_COLOUR_IMMUNE() {
            return getToken(MonsterSpellGrammar.LORE_COLOUR_IMMUNE, 0);
        }

        public TerminalNode STRING() {
            return getToken(MonsterSpellGrammar.STRING, 0);
        }

        public LoreColourImmuneContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_loreColourImmune;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).enterLoreColourImmune(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).exitLoreColourImmune(this);
        }
    }

    public final LoreColourImmuneContext loreColourImmune() throws RecognitionException {
        LoreColourImmuneContext _localctx = new LoreColourImmuneContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_loreColourImmune);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(78);
                match(LORE_COLOUR_IMMUNE);
                setState(79);
                ((LoreColourImmuneContext) _localctx).STRING = match(STRING);
                ((LoreColourImmuneContext) _localctx).colourImmune = ((LoreColourImmuneContext) _localctx).STRING.getText();
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
    public static class MessageSaveContext extends ParserRuleContext {
        public String saveMsg;
        public Token STRING;

        public TerminalNode MESSAGE_SAVE() {
            return getToken(MonsterSpellGrammar.MESSAGE_SAVE, 0);
        }

        public TerminalNode STRING() {
            return getToken(MonsterSpellGrammar.STRING, 0);
        }

        public MessageSaveContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_messageSave;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).enterMessageSave(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).exitMessageSave(this);
        }
    }

    public final MessageSaveContext messageSave() throws RecognitionException {
        MessageSaveContext _localctx = new MessageSaveContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_messageSave);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(82);
                match(MESSAGE_SAVE);
                setState(83);
                ((MessageSaveContext) _localctx).STRING = match(STRING);
                ((MessageSaveContext) _localctx).saveMsg = ((MessageSaveContext) _localctx).STRING.getText();
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
    public static class MessageVisContext extends ParserRuleContext {
        public String visMsg;
        public Token STRING;

        public TerminalNode MESSAGE_VIS() {
            return getToken(MonsterSpellGrammar.MESSAGE_VIS, 0);
        }

        public TerminalNode STRING() {
            return getToken(MonsterSpellGrammar.STRING, 0);
        }

        public MessageVisContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_messageVis;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).enterMessageVis(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).exitMessageVis(this);
        }
    }

    public final MessageVisContext messageVis() throws RecognitionException {
        MessageVisContext _localctx = new MessageVisContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_messageVis);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(86);
                match(MESSAGE_VIS);
                setState(87);
                ((MessageVisContext) _localctx).STRING = match(STRING);
                ((MessageVisContext) _localctx).visMsg = ((MessageVisContext) _localctx).STRING.getText();
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
    public static class MessageInvisContext extends ParserRuleContext {
        public String invisMsg;
        public Token STRING;

        public TerminalNode MESSAGE_INVIS() {
            return getToken(MonsterSpellGrammar.MESSAGE_INVIS, 0);
        }

        public TerminalNode STRING() {
            return getToken(MonsterSpellGrammar.STRING, 0);
        }

        public MessageInvisContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_messageInvis;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).enterMessageInvis(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).exitMessageInvis(this);
        }
    }

    public final MessageInvisContext messageInvis() throws RecognitionException {
        MessageInvisContext _localctx = new MessageInvisContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_messageInvis);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(90);
                match(MESSAGE_INVIS);
                setState(91);
                ((MessageInvisContext) _localctx).STRING = match(STRING);
                ((MessageInvisContext) _localctx).invisMsg = ((MessageInvisContext) _localctx).STRING.getText();
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
    public static class MessageMissContext extends ParserRuleContext {
        public String missMsg;
        public Token STRING;

        public TerminalNode MESSAGE_MISS() {
            return getToken(MonsterSpellGrammar.MESSAGE_MISS, 0);
        }

        public TerminalNode STRING() {
            return getToken(MonsterSpellGrammar.STRING, 0);
        }

        public MessageMissContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_messageMiss;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).enterMessageMiss(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).exitMessageMiss(this);
        }
    }

    public final MessageMissContext messageMiss() throws RecognitionException {
        MessageMissContext _localctx = new MessageMissContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_messageMiss);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(94);
                match(MESSAGE_MISS);
                setState(95);
                ((MessageMissContext) _localctx).STRING = match(STRING);
                ((MessageMissContext) _localctx).missMsg = ((MessageMissContext) _localctx).STRING.getText();
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
    public static class PowerCutoffBlockContext extends ParserRuleContext {
        public String powerCutOffVal;
        public String loreStr;
        public String loreColourBaseStr;
        public String loreColourResistStr;
        public String loreColourImmuneStr;
        public String messageVisStr;
        public String messageInvisStr;
        public String messageMissStr;
        public String messageSaveStr;
        public Integer line;
        public PowerCutoffContext powerCutoff;
        public LoreContext lore;
        public LoreColourBaseContext loreColourBase;
        public LoreColourResistContext loreColourResist;
        public LoreColourImmuneContext loreColourImmune;
        public MessageVisContext messageVis;
        public MessageInvisContext messageInvis;
        public MessageMissContext messageMiss;
        public MessageSaveContext messageSave;

        public PowerCutoffContext powerCutoff() {
            return getRuleContext(PowerCutoffContext.class, 0);
        }

        public List<LoreContext> lore() {
            return getRuleContexts(LoreContext.class);
        }

        public LoreContext lore(int i) {
            return getRuleContext(LoreContext.class, i);
        }

        public List<LoreColourBaseContext> loreColourBase() {
            return getRuleContexts(LoreColourBaseContext.class);
        }

        public LoreColourBaseContext loreColourBase(int i) {
            return getRuleContext(LoreColourBaseContext.class, i);
        }

        public List<LoreColourResistContext> loreColourResist() {
            return getRuleContexts(LoreColourResistContext.class);
        }

        public LoreColourResistContext loreColourResist(int i) {
            return getRuleContext(LoreColourResistContext.class, i);
        }

        public List<LoreColourImmuneContext> loreColourImmune() {
            return getRuleContexts(LoreColourImmuneContext.class);
        }

        public LoreColourImmuneContext loreColourImmune(int i) {
            return getRuleContext(LoreColourImmuneContext.class, i);
        }

        public List<MessageVisContext> messageVis() {
            return getRuleContexts(MessageVisContext.class);
        }

        public MessageVisContext messageVis(int i) {
            return getRuleContext(MessageVisContext.class, i);
        }

        public List<MessageInvisContext> messageInvis() {
            return getRuleContexts(MessageInvisContext.class);
        }

        public MessageInvisContext messageInvis(int i) {
            return getRuleContext(MessageInvisContext.class, i);
        }

        public List<MessageMissContext> messageMiss() {
            return getRuleContexts(MessageMissContext.class);
        }

        public MessageMissContext messageMiss(int i) {
            return getRuleContext(MessageMissContext.class, i);
        }

        public List<MessageSaveContext> messageSave() {
            return getRuleContexts(MessageSaveContext.class);
        }

        public MessageSaveContext messageSave(int i) {
            return getRuleContext(MessageSaveContext.class, i);
        }

        public PowerCutoffBlockContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_powerCutoffBlock;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).enterPowerCutoffBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).exitPowerCutoffBlock(this);
        }
    }

    public final PowerCutoffBlockContext powerCutoffBlock() throws RecognitionException {
        PowerCutoffBlockContext _localctx = new PowerCutoffBlockContext(_ctx, getState());
        enterRule(_localctx, 26, RULE_powerCutoffBlock);
        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                ((PowerCutoffBlockContext) _localctx).line = _localctx.start.getLine();
                ((PowerCutoffBlockContext) _localctx).loreStr = "";
                ((PowerCutoffBlockContext) _localctx).messageVisStr = "";
                ((PowerCutoffBlockContext) _localctx).messageInvisStr = "";
                ((PowerCutoffBlockContext) _localctx).messageMissStr = "";
                ((PowerCutoffBlockContext) _localctx).messageSaveStr = "";
                ((PowerCutoffBlockContext) _localctx).loreColourBaseStr = "";
                ((PowerCutoffBlockContext) _localctx).loreColourResistStr = "";
                ((PowerCutoffBlockContext) _localctx).loreColourImmuneStr = "";
                setState(102);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == POWER_CUTOFF) {
                    {
                        setState(99);
                        ((PowerCutoffBlockContext) _localctx).powerCutoff = powerCutoff();
                        ((PowerCutoffBlockContext) _localctx).powerCutOffVal = ((PowerCutoffBlockContext) _localctx).powerCutoff.cutoffVal;
                    }
                }

                setState(128);
                _errHandler.sync(this);
                _alt = 1;
                do {
                    switch (_alt) {
                        case 1: {
                            setState(128);
                            _errHandler.sync(this);
                            switch (_input.LA(1)) {
                                case LORE: {
                                    setState(104);
                                    ((PowerCutoffBlockContext) _localctx).lore = lore();
                                    ((PowerCutoffBlockContext) _localctx).loreStr = _localctx.loreStr + ((PowerCutoffBlockContext) _localctx).lore.loreStr;
                                }
                                break;
                                case LORE_COLOUR_BASE: {
                                    setState(107);
                                    ((PowerCutoffBlockContext) _localctx).loreColourBase = loreColourBase();
                                    ((PowerCutoffBlockContext) _localctx).loreColourBaseStr = ((PowerCutoffBlockContext) _localctx).loreColourBase.colourBase;
                                }
                                break;
                                case LORE_COLOUR_RESIST: {
                                    setState(110);
                                    ((PowerCutoffBlockContext) _localctx).loreColourResist = loreColourResist();
                                    ((PowerCutoffBlockContext) _localctx).loreColourResistStr = ((PowerCutoffBlockContext) _localctx).loreColourResist.colourResist;
                                }
                                break;
                                case LORE_COLOUR_IMMUNE: {
                                    setState(113);
                                    ((PowerCutoffBlockContext) _localctx).loreColourImmune = loreColourImmune();
                                    ((PowerCutoffBlockContext) _localctx).loreColourImmuneStr = ((PowerCutoffBlockContext) _localctx).loreColourImmune.colourImmune;
                                }
                                break;
                                case MESSAGE_VIS: {
                                    setState(116);
                                    ((PowerCutoffBlockContext) _localctx).messageVis = messageVis();
                                    ((PowerCutoffBlockContext) _localctx).messageVisStr = _localctx.messageVisStr + ((PowerCutoffBlockContext) _localctx).messageVis.visMsg;
                                }
                                break;
                                case MESSAGE_INVIS: {
                                    setState(119);
                                    ((PowerCutoffBlockContext) _localctx).messageInvis = messageInvis();
                                    ((PowerCutoffBlockContext) _localctx).messageInvisStr = _localctx.messageInvisStr + ((PowerCutoffBlockContext) _localctx).messageInvis.invisMsg;
                                }
                                break;
                                case MESSAGE_MISS: {
                                    setState(122);
                                    ((PowerCutoffBlockContext) _localctx).messageMiss = messageMiss();
                                    ((PowerCutoffBlockContext) _localctx).messageMissStr = _localctx.messageMissStr + ((PowerCutoffBlockContext) _localctx).messageMiss.missMsg;
                                }
                                break;
                                case MESSAGE_SAVE: {
                                    setState(125);
                                    ((PowerCutoffBlockContext) _localctx).messageSave = messageSave();
                                    ((PowerCutoffBlockContext) _localctx).messageSaveStr = _localctx.messageSaveStr + ((PowerCutoffBlockContext) _localctx).messageSave.saveMsg;
                                }
                                break;
                                default:
                                    throw new NoViableAltException(this);
                            }
                        }
                        break;
                        default:
                            throw new NoViableAltException(this);
                    }
                    setState(130);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 2, _ctx);
                } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
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
    public static class MonsterSpellContext extends ParserRuleContext {
        public MonsterSpellParseRecord spell;
        public NameContext name;
        public MsgtContext msgt;
        public HitContext hit;
        public EffectBlockContext effectBlock;
        public PowerCutoffBlockContext powerCutoffBlock;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public List<MsgtContext> msgt() {
            return getRuleContexts(MsgtContext.class);
        }

        public MsgtContext msgt(int i) {
            return getRuleContext(MsgtContext.class, i);
        }

        public List<HitContext> hit() {
            return getRuleContexts(HitContext.class);
        }

        public HitContext hit(int i) {
            return getRuleContext(HitContext.class, i);
        }

        public List<EffectBlockContext> effectBlock() {
            return getRuleContexts(EffectBlockContext.class);
        }

        public EffectBlockContext effectBlock(int i) {
            return getRuleContext(EffectBlockContext.class, i);
        }

        public List<PowerCutoffBlockContext> powerCutoffBlock() {
            return getRuleContexts(PowerCutoffBlockContext.class);
        }

        public PowerCutoffBlockContext powerCutoffBlock(int i) {
            return getRuleContext(PowerCutoffBlockContext.class, i);
        }

        public MonsterSpellContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_monsterSpell;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).enterMonsterSpell(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).exitMonsterSpell(this);
        }
    }

    public final MonsterSpellContext monsterSpell() throws RecognitionException {
        MonsterSpellContext _localctx = new MonsterSpellContext(_ctx, getState());
        enterRule(_localctx, 28, RULE_monsterSpell);

        String nameStrInit = "";
        String msgtStrInit = "";
        String hitInit = "";
        int lineInit = 0;
        List<EffectParseRecord> effects = new ArrayList<>();
        List<MonsterSpellParseRecord.MonsterSpellLevelParseRecord> levels = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(132);
                ((MonsterSpellContext) _localctx).name = name();
                nameStrInit = ((MonsterSpellContext) _localctx).name.nameStr;
                lineInit = ((MonsterSpellContext) _localctx).name.line;
                setState(146);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        setState(146);
                        _errHandler.sync(this);
                        switch (_input.LA(1)) {
                            case MSGT: {
                                setState(134);
                                ((MonsterSpellContext) _localctx).msgt = msgt();
                                msgtStrInit = ((MonsterSpellContext) _localctx).msgt.msgTStr;
                            }
                            break;
                            case HIT: {
                                setState(137);
                                ((MonsterSpellContext) _localctx).hit = hit();
                                hitInit = ((MonsterSpellContext) _localctx).hit.hitVal;
                            }
                            break;
                            case EFFECT: {
                                setState(140);
                                ((MonsterSpellContext) _localctx).effectBlock = effectBlock();
                                effects.add(new EffectParseRecord(((MonsterSpellContext) _localctx).effectBlock.typeInit,
                                        ((MonsterSpellContext) _localctx).effectBlock.subtypeWrapperInit, ((MonsterSpellContext) _localctx).effectBlock.radius, ((MonsterSpellContext) _localctx).effectBlock.other,
                                        ((MonsterSpellContext) _localctx).effectBlock.diceString, ((MonsterSpellContext) _localctx).effectBlock.yVal, ((MonsterSpellContext) _localctx).effectBlock.xVal,
                                        ((MonsterSpellContext) _localctx).effectBlock.expressionChars, ((MonsterSpellContext) _localctx).effectBlock.expressionBase,
                                        ((MonsterSpellContext) _localctx).effectBlock.expressionOperation, ((MonsterSpellContext) _localctx).effectBlock.timeDiceString,
                                        ((MonsterSpellContext) _localctx).effectBlock.effectMessage, (((MonsterSpellContext) _localctx).effectBlock != null ? (((MonsterSpellContext) _localctx).effectBlock.start) : null).getLine()));
                            }
                            break;
                            case POWER_CUTOFF:
                            case LORE:
                            case LORE_COLOUR_BASE:
                            case LORE_COLOUR_RESIST:
                            case LORE_COLOUR_IMMUNE:
                            case MESSAGE_SAVE:
                            case MESSAGE_VIS:
                            case MESSAGE_INVIS:
                            case MESSAGE_MISS: {
                                setState(143);
                                ((MonsterSpellContext) _localctx).powerCutoffBlock = powerCutoffBlock();
                                levels.add(new MonsterSpellParseRecord.MonsterSpellLevelParseRecord(
                                        ((MonsterSpellContext) _localctx).powerCutoffBlock.powerCutOffVal, ((MonsterSpellContext) _localctx).powerCutoffBlock.loreStr,
                                        ((MonsterSpellContext) _localctx).powerCutoffBlock.loreColourBaseStr, ((MonsterSpellContext) _localctx).powerCutoffBlock.loreColourResistStr,
                                        ((MonsterSpellContext) _localctx).powerCutoffBlock.loreColourImmuneStr, ((MonsterSpellContext) _localctx).powerCutoffBlock.messageVisStr,
                                        ((MonsterSpellContext) _localctx).powerCutoffBlock.messageInvisStr, ((MonsterSpellContext) _localctx).powerCutoffBlock.messageMissStr,
                                        ((MonsterSpellContext) _localctx).powerCutoffBlock.messageSaveStr, ((MonsterSpellContext) _localctx).powerCutoffBlock.line));
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                    }
                    setState(148);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 81912L) != 0));
            }
            _ctx.stop = _input.LT(-1);

            ((MonsterSpellContext) _localctx).spell = new MonsterSpellParseRecord(nameStrInit, msgtStrInit, hitInit,
                    effects, levels, lineInit);

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
        public List<MonsterSpellParseRecord> monsterSpells;
        public RecordCountContext recordCount;
        public MonsterSpellContext monsterSpell;

        public RecordCountContext recordCount() {
            return getRuleContext(RecordCountContext.class, 0);
        }

        public TerminalNode EOF() {
            return getToken(MonsterSpellGrammar.EOF, 0);
        }

        public List<MonsterSpellContext> monsterSpell() {
            return getRuleContexts(MonsterSpellContext.class);
        }

        public MonsterSpellContext monsterSpell(int i) {
            return getRuleContext(MonsterSpellContext.class, i);
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
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).exitFile(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 30, RULE_file);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(150);
                ((FileContext) _localctx).recordCount = recordCount();
                ((FileContext) _localctx).declaredRecordCount = ((FileContext) _localctx).recordCount.count;
                ((FileContext) _localctx).monsterSpells = new ArrayList<>();
                setState(155);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(152);
                            ((FileContext) _localctx).monsterSpell = monsterSpell();
                            _localctx.monsterSpells.add(((FileContext) _localctx).monsterSpell.spell);
                        }
                    }
                    setState(157);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(159);
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
            return getToken(MonsterSpellGrammar.EFFECT, 0);
        }

        public List<TerminalNode> UCASE() {
            return getTokens(MonsterSpellGrammar.UCASE);
        }

        public TerminalNode UCASE(int i) {
            return getToken(MonsterSpellGrammar.UCASE, i);
        }

        public List<TerminalNode> COLON() {
            return getTokens(MonsterSpellGrammar.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(MonsterSpellGrammar.COLON, i);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(MonsterSpellGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(MonsterSpellGrammar.INTEGER, i);
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
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).enterEffect(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).exitEffect(this);
        }
    }

    public final EffectContext effect() throws RecognitionException {
        EffectContext _localctx = new EffectContext(_ctx, getState());
        enterRule(_localctx, 32, RULE_effect);

        ((EffectContext) _localctx).wrapper = "";
        ((EffectContext) _localctx).radius = "";
        ((EffectContext) _localctx).other = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(161);
                match(EFFECT);
                setState(162);
                ((EffectContext) _localctx).t = match(UCASE);

                ((EffectContext) _localctx).type = ((EffectContext) _localctx).t.getText();

                setState(177);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COLON) {
                    {
                        setState(164);
                        match(COLON);
                        setState(165);
                        ((EffectContext) _localctx).st = match(UCASE);

                        ((EffectContext) _localctx).wrapper = ((EffectContext) _localctx).st.getText().toUpperCase();

                        setState(175);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == COLON) {
                            {
                                setState(167);
                                match(COLON);
                                setState(168);
                                ((EffectContext) _localctx).rad = match(INTEGER);

                                ((EffectContext) _localctx).radius = ((EffectContext) _localctx).rad.getText();

                                setState(173);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                                if (_la == COLON) {
                                    {
                                        setState(170);
                                        match(COLON);
                                        setState(171);
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
            return getToken(MonsterSpellGrammar.TIME, 0);
        }

        public TerminalNode DICE_SIMPLE_VALUE() {
            return getToken(MonsterSpellGrammar.DICE_SIMPLE_VALUE, 0);
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
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).enterTime(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).exitTime(this);
        }
    }

    public final TimeContext time() throws RecognitionException {
        TimeContext _localctx = new TimeContext(_ctx, getState());
        enterRule(_localctx, 34, RULE_time);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(179);
                match(TIME);
                setState(180);
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
            return getToken(MonsterSpellGrammar.EFFECT_YX, 0);
        }

        public TerminalNode COLON() {
            return getToken(MonsterSpellGrammar.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(MonsterSpellGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(MonsterSpellGrammar.INTEGER, i);
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
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).enterEffectYX(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).exitEffectYX(this);
        }
    }

    public final EffectYXContext effectYX() throws RecognitionException {
        EffectYXContext _localctx = new EffectYXContext(_ctx, getState());
        enterRule(_localctx, 36, RULE_effectYX);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(183);
                match(EFFECT_YX);
                setState(184);
                ((EffectYXContext) _localctx).yVal = match(INTEGER);
                setState(185);
                match(COLON);
                setState(186);
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
            return getToken(MonsterSpellGrammar.DICE, 0);
        }

        public TerminalNode DICE_SIMPLE_VALUE() {
            return getToken(MonsterSpellGrammar.DICE_SIMPLE_VALUE, 0);
        }

        public TerminalNode DICE_COMPLEX_VALUE() {
            return getToken(MonsterSpellGrammar.DICE_COMPLEX_VALUE, 0);
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
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).enterDice(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).exitDice(this);
        }
    }

    public final DiceContext dice() throws RecognitionException {
        DiceContext _localctx = new DiceContext(_ctx, getState());
        enterRule(_localctx, 38, RULE_dice);

        String charHolder = "";
        String baseHolder = "";
        String operHolder = "";
        ((DiceContext) _localctx).diceString = "";
        ((DiceContext) _localctx).exprChar = "";
        ((DiceContext) _localctx).baseName = "";
        ((DiceContext) _localctx).operation = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(189);
                match(DICE);
                setState(201);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case DICE_COMPLEX_VALUE: {
                        {
                            setState(190);
                            ((DiceContext) _localctx).val = match(DICE_COMPLEX_VALUE);

                            ((DiceContext) _localctx).diceString = ((DiceContext) _localctx).val.getText();

                            setState(195);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            do {
                                {
                                    {
                                        setState(192);
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
                                setState(197);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                            } while (_la == EXPR);
                        }
                    }
                    break;
                    case DICE_SIMPLE_VALUE: {
                        setState(199);
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
            return getToken(MonsterSpellGrammar.EXPR, 0);
        }

        public List<TerminalNode> EXPR_COLON() {
            return getTokens(MonsterSpellGrammar.EXPR_COLON);
        }

        public TerminalNode EXPR_COLON(int i) {
            return getToken(MonsterSpellGrammar.EXPR_COLON, i);
        }

        public TerminalNode EXPR_CHAR() {
            return getToken(MonsterSpellGrammar.EXPR_CHAR, 0);
        }

        public TerminalNode EXPR_UCASE() {
            return getToken(MonsterSpellGrammar.EXPR_UCASE, 0);
        }

        public TerminalNode EXPR_OP() {
            return getToken(MonsterSpellGrammar.EXPR_OP, 0);
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
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).enterExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).exitExpr(this);
        }
    }

    public final ExprContext expr() throws RecognitionException {
        ExprContext _localctx = new ExprContext(_ctx, getState());
        enterRule(_localctx, 40, RULE_expr);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(203);
                match(EXPR);
                setState(204);
                ((ExprContext) _localctx).ch = match(EXPR_CHAR);
                setState(205);
                match(EXPR_COLON);
                setState(206);
                ((ExprContext) _localctx).base = match(EXPR_UCASE);
                setState(207);
                match(EXPR_COLON);
                setState(208);
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
            return getToken(MonsterSpellGrammar.EFFECT_MESSAGE, 0);
        }

        public TerminalNode FREE_TEXT() {
            return getToken(MonsterSpellGrammar.FREE_TEXT, 0);
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
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).enterEffectMsg(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).exitEffectMsg(this);
        }
    }

    public final EffectMsgContext effectMsg() throws RecognitionException {
        EffectMsgContext _localctx = new EffectMsgContext(_ctx, getState());
        enterRule(_localctx, 42, RULE_effectMsg);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(211);
                match(EFFECT_MESSAGE);
                setState(212);
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
        public int lineNo;
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
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).enterEffectBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterSpellGrammarListener)
                ((MonsterSpellGrammarListener) listener).exitEffectBlock(this);
        }
    }

    public final EffectBlockContext effectBlock() throws RecognitionException {
        EffectBlockContext _localctx = new EffectBlockContext(_ctx, getState());
        enterRule(_localctx, 44, RULE_effectBlock);

        String expressionString = "";
        String baseString = "";
        String opString = "";
        ((EffectBlockContext) _localctx).diceString = "";
        ((EffectBlockContext) _localctx).timeDiceString = "";
        ((EffectBlockContext) _localctx).yVal = "";
        ((EffectBlockContext) _localctx).xVal = "";
        ((EffectBlockContext) _localctx).effectMessage = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(215);
                ((EffectBlockContext) _localctx).effect = effect();

                ((EffectBlockContext) _localctx).lineNo = _localctx.start.getLine();
                ((EffectBlockContext) _localctx).typeInit = ((EffectBlockContext) _localctx).effect.type;
                ((EffectBlockContext) _localctx).subtypeWrapperInit = ((EffectBlockContext) _localctx).effect.wrapper;
                ((EffectBlockContext) _localctx).radius = ((EffectBlockContext) _localctx).effect.radius;
                ((EffectBlockContext) _localctx).other = ((EffectBlockContext) _localctx).effect.other;

                setState(225);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case EFFECT_YX: {
                        {
                            setState(217);
                            ((EffectBlockContext) _localctx).effectYX = effectYX();

                            ((EffectBlockContext) _localctx).yVal = ((EffectBlockContext) _localctx).effectYX.y;
                            ((EffectBlockContext) _localctx).xVal = ((EffectBlockContext) _localctx).effectYX.x;

                        }
                    }
                    break;
                    case EOF:
                    case NAME:
                    case MSGT:
                    case HIT:
                    case POWER_CUTOFF:
                    case LORE:
                    case LORE_COLOUR_BASE:
                    case LORE_COLOUR_RESIST:
                    case LORE_COLOUR_IMMUNE:
                    case MESSAGE_SAVE:
                    case MESSAGE_VIS:
                    case MESSAGE_INVIS:
                    case MESSAGE_MISS:
                    case EFFECT:
                    case EFFECT_MESSAGE:
                    case DICE:
                    case TIME: {
                        {
                            setState(223);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            if (_la == DICE) {
                                {
                                    setState(220);
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
                setState(230);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == TIME) {
                    {
                        setState(227);
                        ((EffectBlockContext) _localctx).time = time();

                        ((EffectBlockContext) _localctx).timeDiceString = ((EffectBlockContext) _localctx).time.timeStr;

                    }
                }

                setState(235);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == EFFECT_MESSAGE) {
                    {
                        setState(232);
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
            "\u0004\u0001$\u00ee\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002" +
                    "\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002" +
                    "\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f" +
                    "\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012" +
                    "\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015" +
                    "\u0002\u0016\u0007\u0016\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001" +
                    "\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0003\rg\b\r\u0001\r\u0001" +
                    "\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001" +
                    "\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001" +
                    "\r\u0001\r\u0001\r\u0001\r\u0001\r\u0004\r\u0081\b\r\u000b\r\f\r\u0082" +
                    "\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e" +
                    "\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e" +
                    "\u0001\u000e\u0001\u000e\u0004\u000e\u0093\b\u000e\u000b\u000e\f\u000e" +
                    "\u0094\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0004" +
                    "\u000f\u009c\b\u000f\u000b\u000f\f\u000f\u009d\u0001\u000f\u0001\u000f" +
                    "\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010" +
                    "\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010" +
                    "\u0003\u0010\u00ae\b\u0010\u0003\u0010\u00b0\b\u0010\u0003\u0010\u00b2" +
                    "\b\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001" +
                    "\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0013\u0001" +
                    "\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0004\u0013\u00c4" +
                    "\b\u0013\u000b\u0013\f\u0013\u00c5\u0001\u0013\u0001\u0013\u0003\u0013" +
                    "\u00ca\b\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014" +
                    "\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015" +
                    "\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016" +
                    "\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u00e0\b\u0016\u0003\u0016" +
                    "\u00e2\b\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u00e7\b" +
                    "\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u00ec\b\u0016\u0001" +
                    "\u0016\u0000\u0000\u0017\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012" +
                    "\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,\u0000\u0000\u00ed\u0000." +
                    "\u0001\u0000\u0000\u0000\u00022\u0001\u0000\u0000\u0000\u00046\u0001\u0000" +
                    "\u0000\u0000\u0006:\u0001\u0000\u0000\u0000\b>\u0001\u0000\u0000\u0000" +
                    "\nB\u0001\u0000\u0000\u0000\fF\u0001\u0000\u0000\u0000\u000eJ\u0001\u0000" +
                    "\u0000\u0000\u0010N\u0001\u0000\u0000\u0000\u0012R\u0001\u0000\u0000\u0000" +
                    "\u0014V\u0001\u0000\u0000\u0000\u0016Z\u0001\u0000\u0000\u0000\u0018^" +
                    "\u0001\u0000\u0000\u0000\u001ab\u0001\u0000\u0000\u0000\u001c\u0084\u0001" +
                    "\u0000\u0000\u0000\u001e\u0096\u0001\u0000\u0000\u0000 \u00a1\u0001\u0000" +
                    "\u0000\u0000\"\u00b3\u0001\u0000\u0000\u0000$\u00b7\u0001\u0000\u0000" +
                    "\u0000&\u00bd\u0001\u0000\u0000\u0000(\u00cb\u0001\u0000\u0000\u0000*" +
                    "\u00d3\u0001\u0000\u0000\u0000,\u00d7\u0001\u0000\u0000\u0000./\u0005" +
                    "\u0001\u0000\u0000/0\u0005\u0018\u0000\u000001\u0006\u0000\uffff\uffff" +
                    "\u00001\u0001\u0001\u0000\u0000\u000023\u0005\u0002\u0000\u000034\u0005" +
                    "\u001b\u0000\u000045\u0006\u0001\uffff\uffff\u00005\u0003\u0001\u0000" +
                    "\u0000\u000067\u0005\u0003\u0000\u000078\u0005\u001b\u0000\u000089\u0006" +
                    "\u0002\uffff\uffff\u00009\u0005\u0001\u0000\u0000\u0000:;\u0005\u0004" +
                    "\u0000\u0000;<\u0005\u0018\u0000\u0000<=\u0006\u0003\uffff\uffff\u0000" +
                    "=\u0007\u0001\u0000\u0000\u0000>?\u0005\u0005\u0000\u0000?@\u0005\u0018" +
                    "\u0000\u0000@A\u0006\u0004\uffff\uffff\u0000A\t\u0001\u0000\u0000\u0000" +
                    "BC\u0005\u0006\u0000\u0000CD\u0005\u001b\u0000\u0000DE\u0006\u0005\uffff" +
                    "\uffff\u0000E\u000b\u0001\u0000\u0000\u0000FG\u0005\u0007\u0000\u0000" +
                    "GH\u0005\u001b\u0000\u0000HI\u0006\u0006\uffff\uffff\u0000I\r\u0001\u0000" +
                    "\u0000\u0000JK\u0005\b\u0000\u0000KL\u0005\u001b\u0000\u0000LM\u0006\u0007" +
                    "\uffff\uffff\u0000M\u000f\u0001\u0000\u0000\u0000NO\u0005\t\u0000\u0000" +
                    "OP\u0005\u001b\u0000\u0000PQ\u0006\b\uffff\uffff\u0000Q\u0011\u0001\u0000" +
                    "\u0000\u0000RS\u0005\n\u0000\u0000ST\u0005\u001b\u0000\u0000TU\u0006\t" +
                    "\uffff\uffff\u0000U\u0013\u0001\u0000\u0000\u0000VW\u0005\u000b\u0000" +
                    "\u0000WX\u0005\u001b\u0000\u0000XY\u0006\n\uffff\uffff\u0000Y\u0015\u0001" +
                    "\u0000\u0000\u0000Z[\u0005\f\u0000\u0000[\\\u0005\u001b\u0000\u0000\\" +
                    "]\u0006\u000b\uffff\uffff\u0000]\u0017\u0001\u0000\u0000\u0000^_\u0005" +
                    "\r\u0000\u0000_`\u0005\u001b\u0000\u0000`a\u0006\f\uffff\uffff\u0000a" +
                    "\u0019\u0001\u0000\u0000\u0000bf\u0006\r\uffff\uffff\u0000cd\u0003\b\u0004" +
                    "\u0000de\u0006\r\uffff\uffff\u0000eg\u0001\u0000\u0000\u0000fc\u0001\u0000" +
                    "\u0000\u0000fg\u0001\u0000\u0000\u0000g\u0080\u0001\u0000\u0000\u0000" +
                    "hi\u0003\n\u0005\u0000ij\u0006\r\uffff\uffff\u0000j\u0081\u0001\u0000" +
                    "\u0000\u0000kl\u0003\f\u0006\u0000lm\u0006\r\uffff\uffff\u0000m\u0081" +
                    "\u0001\u0000\u0000\u0000no\u0003\u000e\u0007\u0000op\u0006\r\uffff\uffff" +
                    "\u0000p\u0081\u0001\u0000\u0000\u0000qr\u0003\u0010\b\u0000rs\u0006\r" +
                    "\uffff\uffff\u0000s\u0081\u0001\u0000\u0000\u0000tu\u0003\u0014\n\u0000" +
                    "uv\u0006\r\uffff\uffff\u0000v\u0081\u0001\u0000\u0000\u0000wx\u0003\u0016" +
                    "\u000b\u0000xy\u0006\r\uffff\uffff\u0000y\u0081\u0001\u0000\u0000\u0000" +
                    "z{\u0003\u0018\f\u0000{|\u0006\r\uffff\uffff\u0000|\u0081\u0001\u0000" +
                    "\u0000\u0000}~\u0003\u0012\t\u0000~\u007f\u0006\r\uffff\uffff\u0000\u007f" +
                    "\u0081\u0001\u0000\u0000\u0000\u0080h\u0001\u0000\u0000\u0000\u0080k\u0001" +
                    "\u0000\u0000\u0000\u0080n\u0001\u0000\u0000\u0000\u0080q\u0001\u0000\u0000" +
                    "\u0000\u0080t\u0001\u0000\u0000\u0000\u0080w\u0001\u0000\u0000\u0000\u0080" +
                    "z\u0001\u0000\u0000\u0000\u0080}\u0001\u0000\u0000\u0000\u0081\u0082\u0001" +
                    "\u0000\u0000\u0000\u0082\u0080\u0001\u0000\u0000\u0000\u0082\u0083\u0001" +
                    "\u0000\u0000\u0000\u0083\u001b\u0001\u0000\u0000\u0000\u0084\u0085\u0003" +
                    "\u0002\u0001\u0000\u0085\u0092\u0006\u000e\uffff\uffff\u0000\u0086\u0087" +
                    "\u0003\u0004\u0002\u0000\u0087\u0088\u0006\u000e\uffff\uffff\u0000\u0088" +
                    "\u0093\u0001\u0000\u0000\u0000\u0089\u008a\u0003\u0006\u0003\u0000\u008a" +
                    "\u008b\u0006\u000e\uffff\uffff\u0000\u008b\u0093\u0001\u0000\u0000\u0000" +
                    "\u008c\u008d\u0003,\u0016\u0000\u008d\u008e\u0006\u000e\uffff\uffff\u0000" +
                    "\u008e\u0093\u0001\u0000\u0000\u0000\u008f\u0090\u0003\u001a\r\u0000\u0090" +
                    "\u0091\u0006\u000e\uffff\uffff\u0000\u0091\u0093\u0001\u0000\u0000\u0000" +
                    "\u0092\u0086\u0001\u0000\u0000\u0000\u0092\u0089\u0001\u0000\u0000\u0000" +
                    "\u0092\u008c\u0001\u0000\u0000\u0000\u0092\u008f\u0001\u0000\u0000\u0000" +
                    "\u0093\u0094\u0001\u0000\u0000\u0000\u0094\u0092\u0001\u0000\u0000\u0000" +
                    "\u0094\u0095\u0001\u0000\u0000\u0000\u0095\u001d\u0001\u0000\u0000\u0000" +
                    "\u0096\u0097\u0003\u0000\u0000\u0000\u0097\u009b\u0006\u000f\uffff\uffff" +
                    "\u0000\u0098\u0099\u0003\u001c\u000e\u0000\u0099\u009a\u0006\u000f\uffff" +
                    "\uffff\u0000\u009a\u009c\u0001\u0000\u0000\u0000\u009b\u0098\u0001\u0000" +
                    "\u0000\u0000\u009c\u009d\u0001\u0000\u0000\u0000\u009d\u009b\u0001\u0000" +
                    "\u0000\u0000\u009d\u009e\u0001\u0000\u0000\u0000\u009e\u009f\u0001\u0000" +
                    "\u0000\u0000\u009f\u00a0\u0005\u0000\u0000\u0001\u00a0\u001f\u0001\u0000" +
                    "\u0000\u0000\u00a1\u00a2\u0005\u0010\u0000\u0000\u00a2\u00a3\u0005\u0017" +
                    "\u0000\u0000\u00a3\u00b1\u0006\u0010\uffff\uffff\u0000\u00a4\u00a5\u0005" +
                    "\u0016\u0000\u0000\u00a5\u00a6\u0005\u0017\u0000\u0000\u00a6\u00af\u0006" +
                    "\u0010\uffff\uffff\u0000\u00a7\u00a8\u0005\u0016\u0000\u0000\u00a8\u00a9" +
                    "\u0005\u0018\u0000\u0000\u00a9\u00ad\u0006\u0010\uffff\uffff\u0000\u00aa" +
                    "\u00ab\u0005\u0016\u0000\u0000\u00ab\u00ac\u0005\u0018\u0000\u0000\u00ac" +
                    "\u00ae\u0006\u0010\uffff\uffff\u0000\u00ad\u00aa\u0001\u0000\u0000\u0000" +
                    "\u00ad\u00ae\u0001\u0000\u0000\u0000\u00ae\u00b0\u0001\u0000\u0000\u0000" +
                    "\u00af\u00a7\u0001\u0000\u0000\u0000\u00af\u00b0\u0001\u0000\u0000\u0000" +
                    "\u00b0\u00b2\u0001\u0000\u0000\u0000\u00b1\u00a4\u0001\u0000\u0000\u0000" +
                    "\u00b1\u00b2\u0001\u0000\u0000\u0000\u00b2!\u0001\u0000\u0000\u0000\u00b3" +
                    "\u00b4\u0005\u0013\u0000\u0000\u00b4\u00b5\u0005\u001e\u0000\u0000\u00b5" +
                    "\u00b6\u0006\u0011\uffff\uffff\u0000\u00b6#\u0001\u0000\u0000\u0000\u00b7" +
                    "\u00b8\u0005\u0014\u0000\u0000\u00b8\u00b9\u0005\u0018\u0000\u0000\u00b9" +
                    "\u00ba\u0005\u0016\u0000\u0000\u00ba\u00bb\u0005\u0018\u0000\u0000\u00bb" +
                    "\u00bc\u0006\u0012\uffff\uffff\u0000\u00bc%\u0001\u0000\u0000\u0000\u00bd" +
                    "\u00c9\u0005\u0012\u0000\u0000\u00be\u00bf\u0005\u001f\u0000\u0000\u00bf" +
                    "\u00c3\u0006\u0013\uffff\uffff\u0000\u00c0\u00c1\u0003(\u0014\u0000\u00c1" +
                    "\u00c2\u0006\u0013\uffff\uffff\u0000\u00c2\u00c4\u0001\u0000\u0000\u0000" +
                    "\u00c3\u00c0\u0001\u0000\u0000\u0000\u00c4\u00c5\u0001\u0000\u0000\u0000" +
                    "\u00c5\u00c3\u0001\u0000\u0000\u0000\u00c5\u00c6\u0001\u0000\u0000\u0000" +
                    "\u00c6\u00ca\u0001\u0000\u0000\u0000\u00c7\u00c8\u0005\u001e\u0000\u0000" +
                    "\u00c8\u00ca\u0006\u0013\uffff\uffff\u0000\u00c9\u00be\u0001\u0000\u0000" +
                    "\u0000\u00c9\u00c7\u0001\u0000\u0000\u0000\u00ca\'\u0001\u0000\u0000\u0000" +
                    "\u00cb\u00cc\u0005\u0015\u0000\u0000\u00cc\u00cd\u0005 \u0000\u0000\u00cd" +
                    "\u00ce\u0005!\u0000\u0000\u00ce\u00cf\u0005\"\u0000\u0000\u00cf\u00d0" +
                    "\u0005!\u0000\u0000\u00d0\u00d1\u0005#\u0000\u0000\u00d1\u00d2\u0006\u0014" +
                    "\uffff\uffff\u0000\u00d2)\u0001\u0000\u0000\u0000\u00d3\u00d4\u0005\u0011" +
                    "\u0000\u0000\u00d4\u00d5\u0005\u001d\u0000\u0000\u00d5\u00d6\u0006\u0015" +
                    "\uffff\uffff\u0000\u00d6+\u0001\u0000\u0000\u0000\u00d7\u00d8\u0003 \u0010" +
                    "\u0000\u00d8\u00e1\u0006\u0016\uffff\uffff\u0000\u00d9\u00da\u0003$\u0012" +
                    "\u0000\u00da\u00db\u0006\u0016\uffff\uffff\u0000\u00db\u00e2\u0001\u0000" +
                    "\u0000\u0000\u00dc\u00dd\u0003&\u0013\u0000\u00dd\u00de\u0006\u0016\uffff" +
                    "\uffff\u0000\u00de\u00e0\u0001\u0000\u0000\u0000\u00df\u00dc\u0001\u0000" +
                    "\u0000\u0000\u00df\u00e0\u0001\u0000\u0000\u0000\u00e0\u00e2\u0001\u0000" +
                    "\u0000\u0000\u00e1\u00d9\u0001\u0000\u0000\u0000\u00e1\u00df\u0001\u0000" +
                    "\u0000\u0000\u00e2\u00e6\u0001\u0000\u0000\u0000\u00e3\u00e4\u0003\"\u0011" +
                    "\u0000\u00e4\u00e5\u0006\u0016\uffff\uffff\u0000\u00e5\u00e7\u0001\u0000" +
                    "\u0000\u0000\u00e6\u00e3\u0001\u0000\u0000\u0000\u00e6\u00e7\u0001\u0000" +
                    "\u0000\u0000\u00e7\u00eb\u0001\u0000\u0000\u0000\u00e8\u00e9\u0003*\u0015" +
                    "\u0000\u00e9\u00ea\u0006\u0016\uffff\uffff\u0000\u00ea\u00ec\u0001\u0000" +
                    "\u0000\u0000\u00eb\u00e8\u0001\u0000\u0000\u0000\u00eb\u00ec\u0001\u0000" +
                    "\u0000\u0000\u00ec-\u0001\u0000\u0000\u0000\u000ff\u0080\u0082\u0092\u0094" +
                    "\u009d\u00ad\u00af\u00b1\u00c5\u00c9\u00df\u00e1\u00e6\u00eb";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}