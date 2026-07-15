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

// Generated from PlayerClassGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.playerclass;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.parser.grammars.EffectParseRecord;
import uk.co.jackoftrades.backend.parser.playerclass.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class PlayerClassGrammar extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, NAME = 2, STATS = 3, SKILL_DISARM_PHYS = 4, SKILL_DISARM_MAGIC = 5,
            SKILL_DEVICE = 6, SKILL_SAVE = 7, SKILL_STEALTH = 8, SKILL_SEARCH = 9, SKILL_MELEE = 10,
            SKILL_SHOOT = 11, SKILL_THROW = 12, SKILL_DIG = 13, HITDIE = 14, EXP = 15, MAX_ATTACKS = 16,
            MIN_WEIGHT = 17, STRENGTH = 18, TITLE = 19, EQUIP = 20, OBJ_FLAGS = 21, PLAYER_FLAGS = 22,
            MAGIC = 23, BOOK = 24, BOOK_GRAPHICS = 25, BOOK_PROPERTIES = 26, SPELL = 27, DESC = 28,
            COLON = 29, INTEGER = 30, COMMENT = 31, EOL = 32, EFFECT = 33, EFFECT_MESSAGE = 34,
            DICE = 35, TIME = 36, EFFECT_YX = 37, EXPR = 38, UCASE = 39, SIMPLE_DICE_STRING = 40,
            COMPLEX_DICE_STRING = 41, GRAPHICS = 42, COLOUR_ONLY = 43, GLYPH_ONLY = 44, PROP_INT = 45,
            PROP_COLON = 46, PROP_TO = 47, PROP_EOL = 48, DELIM_INTEGER = 49, STRING_BETWEEN_COLONS = 50,
            DELIM_COLON = 51, END_SKIP = 52, FLAG = 53, FLAG_OR = 54, FLAG_EOL = 55, STRING = 56,
            FREE_TEXT_EOL = 57, FREE_TEXT = 58, DICE_SIMPLE_VALUE = 59, DICE_COMPLEX_VALUE = 60,
            EXPR_CHAR = 61, EXPR_COLON = 62, EXPR_UCASE = 63, EXPR_OP = 64, EXPR_EOL = 65, COLOUR_VALUES = 66,
            GLYPH_VALUES = 67, GLYPH_COLON_SWITCH = 68, GLYPH_EOL = 69;
    public static final int
            RULE_recordCount = 0, RULE_name = 1, RULE_stats = 2, RULE_skillDisarmPhys = 3,
            RULE_skillDisarmMagic = 4, RULE_skillDevice = 5, RULE_skillSave = 6, RULE_skillStealth = 7,
            RULE_skillSearch = 8, RULE_skillMelee = 9, RULE_skillShoot = 10, RULE_skillThrow = 11,
            RULE_skillDig = 12, RULE_hitdie = 13, RULE_maxAttacks = 14, RULE_minWeight = 15,
            RULE_strengthMultiplier = 16, RULE_title = 17, RULE_equip = 18, RULE_objFlag = 19,
            RULE_playerFlags = 20, RULE_exp = 21, RULE_magic = 22, RULE_magicBlock = 23,
            RULE_book = 24, RULE_bookGraphics = 25, RULE_bookProperties = 26, RULE_bookBlock = 27,
            RULE_spell = 28, RULE_desc = 29, RULE_spellBlock = 30, RULE_playerClass = 31,
            RULE_file = 32, RULE_effect = 33, RULE_time = 34, RULE_effectYX = 35,
            RULE_dice = 36, RULE_expr = 37, RULE_effectMsg = 38, RULE_effectBlock = 39;

    private static String[] makeRuleNames() {
        return new String[]{
                "recordCount", "name", "stats", "skillDisarmPhys", "skillDisarmMagic",
                "skillDevice", "skillSave", "skillStealth", "skillSearch", "skillMelee",
                "skillShoot", "skillThrow", "skillDig", "hitdie", "maxAttacks", "minWeight",
                "strengthMultiplier", "title", "equip", "objFlag", "playerFlags", "exp",
                "magic", "magicBlock", "book", "bookGraphics", "bookProperties", "bookBlock",
                "spell", "desc", "spellBlock", "playerClass", "file", "effect", "time",
                "effectYX", "dice", "expr", "effectMsg", "effectBlock"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'name:'", "'stats:'", "'skill-disarm-phys:'",
                "'skill-disarm-magic:'", "'skill-device:'", "'skill-save:'", "'skill-stealth:'",
                "'skill-search:'", "'skill-melee:'", "'skill-shoot:'", "'skill-throw:'",
                "'skill-dig:'", "'hitdie:'", "'exp:'", "'max-attacks:'", "'min-weight:'",
                "'strength-multiplier:'", "'title:'", "'equip:'", "'obj-flags:'", "'player-flags:'",
                "'magic:'", "'book:'", "'book-graphics:'", "'book-properties:'", "'spell:'",
                "'desc:'", null, null, null, null, "'effect:'", "'effect-msg:'", "'dice:'",
                "'time:'", "'effect-yx:'", "'expr:'", null, null, null, "'graphics:'",
                "'color:'", "'glyph:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "NAME", "STATS", "SKILL_DISARM_PHYS", "SKILL_DISARM_MAGIC",
                "SKILL_DEVICE", "SKILL_SAVE", "SKILL_STEALTH", "SKILL_SEARCH", "SKILL_MELEE",
                "SKILL_SHOOT", "SKILL_THROW", "SKILL_DIG", "HITDIE", "EXP", "MAX_ATTACKS",
                "MIN_WEIGHT", "STRENGTH", "TITLE", "EQUIP", "OBJ_FLAGS", "PLAYER_FLAGS",
                "MAGIC", "BOOK", "BOOK_GRAPHICS", "BOOK_PROPERTIES", "SPELL", "DESC",
                "COLON", "INTEGER", "COMMENT", "EOL", "EFFECT", "EFFECT_MESSAGE", "DICE",
                "TIME", "EFFECT_YX", "EXPR", "UCASE", "SIMPLE_DICE_STRING", "COMPLEX_DICE_STRING",
                "GRAPHICS", "COLOUR_ONLY", "GLYPH_ONLY", "PROP_INT", "PROP_COLON", "PROP_TO",
                "PROP_EOL", "DELIM_INTEGER", "STRING_BETWEEN_COLONS", "DELIM_COLON",
                "END_SKIP", "FLAG", "FLAG_OR", "FLAG_EOL", "STRING", "FREE_TEXT_EOL",
                "FREE_TEXT", "DICE_SIMPLE_VALUE", "DICE_COMPLEX_VALUE", "EXPR_CHAR",
                "EXPR_COLON", "EXPR_UCASE", "EXPR_OP", "EXPR_EOL", "COLOUR_VALUES", "GLYPH_VALUES",
                "GLYPH_COLON_SWITCH", "GLYPH_EOL"
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
        return "PlayerClassGrammar.g4";
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

    public PlayerClassGrammar(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RecordCountContext extends ParserRuleContext {
        public String count;
        public Token INTEGER;

        public TerminalNode RECORD_COUNT() {
            return getToken(PlayerClassGrammar.RECORD_COUNT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerClassGrammar.INTEGER, 0);
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
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterRecordCount(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitRecordCount(this);
        }
    }

    public final RecordCountContext recordCount() throws RecognitionException {
        RecordCountContext _localctx = new RecordCountContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_recordCount);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(80);
                match(RECORD_COUNT);
                setState(81);
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
        public int lineNo;
        public Token STRING;

        public TerminalNode NAME() {
            return getToken(PlayerClassGrammar.NAME, 0);
        }

        public TerminalNode STRING() {
            return getToken(PlayerClassGrammar.STRING, 0);
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
            if (listener instanceof PlayerClassGrammarListener) ((PlayerClassGrammarListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener) ((PlayerClassGrammarListener) listener).exitName(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(84);
                match(NAME);
                setState(85);
                ((NameContext) _localctx).STRING = match(STRING);
                ((NameContext) _localctx).nameStr = ((NameContext) _localctx).STRING.getText();
                ((NameContext) _localctx).lineNo = _localctx.start.getLine();
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
    public static class StatsContext extends ParserRuleContext {
        public String strs;
        public String ints;
        public String wiss;
        public String dexs;
        public String cons;
        public Token s;
        public Token i;
        public Token w;
        public Token d;
        public Token c;

        public TerminalNode STATS() {
            return getToken(PlayerClassGrammar.STATS, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(PlayerClassGrammar.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(PlayerClassGrammar.COLON, i);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassGrammar.INTEGER, i);
        }

        public StatsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_stats;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterStats(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener) ((PlayerClassGrammarListener) listener).exitStats(this);
        }
    }

    public final StatsContext stats() throws RecognitionException {
        StatsContext _localctx = new StatsContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_stats);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(88);
                match(STATS);
                setState(89);
                ((StatsContext) _localctx).s = match(INTEGER);
                setState(90);
                match(COLON);
                setState(91);
                ((StatsContext) _localctx).i = match(INTEGER);
                setState(92);
                match(COLON);
                setState(93);
                ((StatsContext) _localctx).w = match(INTEGER);
                setState(94);
                match(COLON);
                setState(95);
                ((StatsContext) _localctx).d = match(INTEGER);
                setState(96);
                match(COLON);
                setState(97);
                ((StatsContext) _localctx).c = match(INTEGER);

                ((StatsContext) _localctx).strs = ((StatsContext) _localctx).s.getText();
                ((StatsContext) _localctx).ints = ((StatsContext) _localctx).i.getText();
                ((StatsContext) _localctx).wiss = ((StatsContext) _localctx).w.getText();
                ((StatsContext) _localctx).dexs = ((StatsContext) _localctx).d.getText();
                ((StatsContext) _localctx).cons = ((StatsContext) _localctx).c.getText();

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
    public static class SkillDisarmPhysContext extends ParserRuleContext {
        public String skillVal;
        public String skillInc;
        public Token v;
        public Token i;

        public TerminalNode SKILL_DISARM_PHYS() {
            return getToken(PlayerClassGrammar.SKILL_DISARM_PHYS, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerClassGrammar.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassGrammar.INTEGER, i);
        }

        public SkillDisarmPhysContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillDisarmPhys;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterSkillDisarmPhys(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitSkillDisarmPhys(this);
        }
    }

    public final SkillDisarmPhysContext skillDisarmPhys() throws RecognitionException {
        SkillDisarmPhysContext _localctx = new SkillDisarmPhysContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_skillDisarmPhys);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(100);
                match(SKILL_DISARM_PHYS);
                setState(101);
                ((SkillDisarmPhysContext) _localctx).v = match(INTEGER);
                setState(102);
                match(COLON);
                setState(103);
                ((SkillDisarmPhysContext) _localctx).i = match(INTEGER);

                ((SkillDisarmPhysContext) _localctx).skillVal = ((SkillDisarmPhysContext) _localctx).v.getText();
                ((SkillDisarmPhysContext) _localctx).skillInc = ((SkillDisarmPhysContext) _localctx).i.getText();

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
    public static class SkillDisarmMagicContext extends ParserRuleContext {
        public String skillVal;
        public String skillInc;
        public Token v;
        public Token i;

        public TerminalNode SKILL_DISARM_MAGIC() {
            return getToken(PlayerClassGrammar.SKILL_DISARM_MAGIC, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerClassGrammar.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassGrammar.INTEGER, i);
        }

        public SkillDisarmMagicContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillDisarmMagic;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterSkillDisarmMagic(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitSkillDisarmMagic(this);
        }
    }

    public final SkillDisarmMagicContext skillDisarmMagic() throws RecognitionException {
        SkillDisarmMagicContext _localctx = new SkillDisarmMagicContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_skillDisarmMagic);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(106);
                match(SKILL_DISARM_MAGIC);
                setState(107);
                ((SkillDisarmMagicContext) _localctx).v = match(INTEGER);
                setState(108);
                match(COLON);
                setState(109);
                ((SkillDisarmMagicContext) _localctx).i = match(INTEGER);

                ((SkillDisarmMagicContext) _localctx).skillVal = ((SkillDisarmMagicContext) _localctx).v.getText();
                ((SkillDisarmMagicContext) _localctx).skillInc = ((SkillDisarmMagicContext) _localctx).i.getText();

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
    public static class SkillDeviceContext extends ParserRuleContext {
        public String skillVal;
        public String skillInc;
        public Token v;
        public Token i;

        public TerminalNode SKILL_DEVICE() {
            return getToken(PlayerClassGrammar.SKILL_DEVICE, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerClassGrammar.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassGrammar.INTEGER, i);
        }

        public SkillDeviceContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillDevice;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterSkillDevice(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitSkillDevice(this);
        }
    }

    public final SkillDeviceContext skillDevice() throws RecognitionException {
        SkillDeviceContext _localctx = new SkillDeviceContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_skillDevice);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(112);
                match(SKILL_DEVICE);
                setState(113);
                ((SkillDeviceContext) _localctx).v = match(INTEGER);
                setState(114);
                match(COLON);
                setState(115);
                ((SkillDeviceContext) _localctx).i = match(INTEGER);

                ((SkillDeviceContext) _localctx).skillVal = ((SkillDeviceContext) _localctx).v.getText();
                ((SkillDeviceContext) _localctx).skillInc = ((SkillDeviceContext) _localctx).i.getText();

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
    public static class SkillSaveContext extends ParserRuleContext {
        public String skillVal;
        public String skillInc;
        public Token v;
        public Token i;

        public TerminalNode SKILL_SAVE() {
            return getToken(PlayerClassGrammar.SKILL_SAVE, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerClassGrammar.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassGrammar.INTEGER, i);
        }

        public SkillSaveContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillSave;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterSkillSave(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitSkillSave(this);
        }
    }

    public final SkillSaveContext skillSave() throws RecognitionException {
        SkillSaveContext _localctx = new SkillSaveContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_skillSave);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(118);
                match(SKILL_SAVE);
                setState(119);
                ((SkillSaveContext) _localctx).v = match(INTEGER);
                setState(120);
                match(COLON);
                setState(121);
                ((SkillSaveContext) _localctx).i = match(INTEGER);

                ((SkillSaveContext) _localctx).skillVal = ((SkillSaveContext) _localctx).v.getText();
                ((SkillSaveContext) _localctx).skillInc = ((SkillSaveContext) _localctx).i.getText();

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
    public static class SkillStealthContext extends ParserRuleContext {
        public String skillVal;
        public String skillInc;
        public Token v;
        public Token i;

        public TerminalNode SKILL_STEALTH() {
            return getToken(PlayerClassGrammar.SKILL_STEALTH, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerClassGrammar.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassGrammar.INTEGER, i);
        }

        public SkillStealthContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillStealth;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterSkillStealth(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitSkillStealth(this);
        }
    }

    public final SkillStealthContext skillStealth() throws RecognitionException {
        SkillStealthContext _localctx = new SkillStealthContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_skillStealth);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(124);
                match(SKILL_STEALTH);
                setState(125);
                ((SkillStealthContext) _localctx).v = match(INTEGER);
                setState(126);
                match(COLON);
                setState(127);
                ((SkillStealthContext) _localctx).i = match(INTEGER);

                ((SkillStealthContext) _localctx).skillVal = ((SkillStealthContext) _localctx).v.getText();
                ((SkillStealthContext) _localctx).skillInc = ((SkillStealthContext) _localctx).i.getText();

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
    public static class SkillSearchContext extends ParserRuleContext {
        public String skillVal;
        public String skillInc;
        public Token v;
        public Token i;

        public TerminalNode SKILL_SEARCH() {
            return getToken(PlayerClassGrammar.SKILL_SEARCH, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerClassGrammar.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassGrammar.INTEGER, i);
        }

        public SkillSearchContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillSearch;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterSkillSearch(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitSkillSearch(this);
        }
    }

    public final SkillSearchContext skillSearch() throws RecognitionException {
        SkillSearchContext _localctx = new SkillSearchContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_skillSearch);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(130);
                match(SKILL_SEARCH);
                setState(131);
                ((SkillSearchContext) _localctx).v = match(INTEGER);
                setState(132);
                match(COLON);
                setState(133);
                ((SkillSearchContext) _localctx).i = match(INTEGER);

                ((SkillSearchContext) _localctx).skillVal = ((SkillSearchContext) _localctx).v.getText();
                ((SkillSearchContext) _localctx).skillInc = ((SkillSearchContext) _localctx).i.getText();

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
    public static class SkillMeleeContext extends ParserRuleContext {
        public String skillVal;
        public String skillInc;
        public Token v;
        public Token i;

        public TerminalNode SKILL_MELEE() {
            return getToken(PlayerClassGrammar.SKILL_MELEE, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerClassGrammar.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassGrammar.INTEGER, i);
        }

        public SkillMeleeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillMelee;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterSkillMelee(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitSkillMelee(this);
        }
    }

    public final SkillMeleeContext skillMelee() throws RecognitionException {
        SkillMeleeContext _localctx = new SkillMeleeContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_skillMelee);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(136);
                match(SKILL_MELEE);
                setState(137);
                ((SkillMeleeContext) _localctx).v = match(INTEGER);
                setState(138);
                match(COLON);
                setState(139);
                ((SkillMeleeContext) _localctx).i = match(INTEGER);

                ((SkillMeleeContext) _localctx).skillVal = ((SkillMeleeContext) _localctx).v.getText();
                ((SkillMeleeContext) _localctx).skillInc = ((SkillMeleeContext) _localctx).i.getText();

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
    public static class SkillShootContext extends ParserRuleContext {
        public String skillVal;
        public String skillInc;
        public Token v;
        public Token i;

        public TerminalNode SKILL_SHOOT() {
            return getToken(PlayerClassGrammar.SKILL_SHOOT, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerClassGrammar.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassGrammar.INTEGER, i);
        }

        public SkillShootContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillShoot;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterSkillShoot(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitSkillShoot(this);
        }
    }

    public final SkillShootContext skillShoot() throws RecognitionException {
        SkillShootContext _localctx = new SkillShootContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_skillShoot);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(142);
                match(SKILL_SHOOT);
                setState(143);
                ((SkillShootContext) _localctx).v = match(INTEGER);
                setState(144);
                match(COLON);
                setState(145);
                ((SkillShootContext) _localctx).i = match(INTEGER);

                ((SkillShootContext) _localctx).skillVal = ((SkillShootContext) _localctx).v.getText();
                ((SkillShootContext) _localctx).skillInc = ((SkillShootContext) _localctx).i.getText();

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
    public static class SkillThrowContext extends ParserRuleContext {
        public String skillVal;
        public String skillInc;
        public Token v;
        public Token i;

        public TerminalNode SKILL_THROW() {
            return getToken(PlayerClassGrammar.SKILL_THROW, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerClassGrammar.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassGrammar.INTEGER, i);
        }

        public SkillThrowContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillThrow;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterSkillThrow(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitSkillThrow(this);
        }
    }

    public final SkillThrowContext skillThrow() throws RecognitionException {
        SkillThrowContext _localctx = new SkillThrowContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_skillThrow);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(148);
                match(SKILL_THROW);
                setState(149);
                ((SkillThrowContext) _localctx).v = match(INTEGER);
                setState(150);
                match(COLON);
                setState(151);
                ((SkillThrowContext) _localctx).i = match(INTEGER);

                ((SkillThrowContext) _localctx).skillVal = ((SkillThrowContext) _localctx).v.getText();
                ((SkillThrowContext) _localctx).skillInc = ((SkillThrowContext) _localctx).i.getText();

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
    public static class SkillDigContext extends ParserRuleContext {
        public String skillVal;
        public String skillInc;
        public Token v;
        public Token i;

        public TerminalNode SKILL_DIG() {
            return getToken(PlayerClassGrammar.SKILL_DIG, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerClassGrammar.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassGrammar.INTEGER, i);
        }

        public SkillDigContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillDig;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterSkillDig(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitSkillDig(this);
        }
    }

    public final SkillDigContext skillDig() throws RecognitionException {
        SkillDigContext _localctx = new SkillDigContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_skillDig);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(154);
                match(SKILL_DIG);
                setState(155);
                ((SkillDigContext) _localctx).v = match(INTEGER);
                setState(156);
                match(COLON);
                setState(157);
                ((SkillDigContext) _localctx).i = match(INTEGER);

                ((SkillDigContext) _localctx).skillVal = ((SkillDigContext) _localctx).v.getText();
                ((SkillDigContext) _localctx).skillInc = ((SkillDigContext) _localctx).i.getText();

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
    public static class HitdieContext extends ParserRuleContext {
        public String increment;
        public Token INTEGER;

        public TerminalNode HITDIE() {
            return getToken(PlayerClassGrammar.HITDIE, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerClassGrammar.INTEGER, 0);
        }

        public HitdieContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_hitdie;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterHitdie(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitHitdie(this);
        }
    }

    public final HitdieContext hitdie() throws RecognitionException {
        HitdieContext _localctx = new HitdieContext(_ctx, getState());
        enterRule(_localctx, 26, RULE_hitdie);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(160);
                match(HITDIE);
                setState(161);
                ((HitdieContext) _localctx).INTEGER = match(INTEGER);
                ((HitdieContext) _localctx).increment = ((HitdieContext) _localctx).INTEGER.getText();
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
    public static class MaxAttacksContext extends ParserRuleContext {
        public String maxAtt;
        public Token INTEGER;

        public TerminalNode MAX_ATTACKS() {
            return getToken(PlayerClassGrammar.MAX_ATTACKS, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerClassGrammar.INTEGER, 0);
        }

        public MaxAttacksContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_maxAttacks;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterMaxAttacks(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitMaxAttacks(this);
        }
    }

    public final MaxAttacksContext maxAttacks() throws RecognitionException {
        MaxAttacksContext _localctx = new MaxAttacksContext(_ctx, getState());
        enterRule(_localctx, 28, RULE_maxAttacks);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(164);
                match(MAX_ATTACKS);
                setState(165);
                ((MaxAttacksContext) _localctx).INTEGER = match(INTEGER);
                ((MaxAttacksContext) _localctx).maxAtt = ((MaxAttacksContext) _localctx).INTEGER.getText();
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
    public static class MinWeightContext extends ParserRuleContext {
        public String minWgt;
        public Token INTEGER;

        public TerminalNode MIN_WEIGHT() {
            return getToken(PlayerClassGrammar.MIN_WEIGHT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerClassGrammar.INTEGER, 0);
        }

        public MinWeightContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_minWeight;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterMinWeight(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitMinWeight(this);
        }
    }

    public final MinWeightContext minWeight() throws RecognitionException {
        MinWeightContext _localctx = new MinWeightContext(_ctx, getState());
        enterRule(_localctx, 30, RULE_minWeight);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(168);
                match(MIN_WEIGHT);
                setState(169);
                ((MinWeightContext) _localctx).INTEGER = match(INTEGER);
                ((MinWeightContext) _localctx).minWgt = ((MinWeightContext) _localctx).INTEGER.getText();
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
    public static class StrengthMultiplierContext extends ParserRuleContext {
        public String strMult;
        public Token INTEGER;

        public TerminalNode STRENGTH() {
            return getToken(PlayerClassGrammar.STRENGTH, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerClassGrammar.INTEGER, 0);
        }

        public StrengthMultiplierContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_strengthMultiplier;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterStrengthMultiplier(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitStrengthMultiplier(this);
        }
    }

    public final StrengthMultiplierContext strengthMultiplier() throws RecognitionException {
        StrengthMultiplierContext _localctx = new StrengthMultiplierContext(_ctx, getState());
        enterRule(_localctx, 32, RULE_strengthMultiplier);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(172);
                match(STRENGTH);
                setState(173);
                ((StrengthMultiplierContext) _localctx).INTEGER = match(INTEGER);
                ((StrengthMultiplierContext) _localctx).strMult = ((StrengthMultiplierContext) _localctx).INTEGER.getText();
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
    public static class TitleContext extends ParserRuleContext {
        public String titleStr;
        public Token STRING;

        public TerminalNode TITLE() {
            return getToken(PlayerClassGrammar.TITLE, 0);
        }

        public TerminalNode STRING() {
            return getToken(PlayerClassGrammar.STRING, 0);
        }

        public TitleContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_title;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterTitle(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener) ((PlayerClassGrammarListener) listener).exitTitle(this);
        }
    }

    public final TitleContext title() throws RecognitionException {
        TitleContext _localctx = new TitleContext(_ctx, getState());
        enterRule(_localctx, 34, RULE_title);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(176);
                match(TITLE);
                setState(177);
                ((TitleContext) _localctx).STRING = match(STRING);
                ((TitleContext) _localctx).titleStr = ((TitleContext) _localctx).STRING.getText();
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
    public static class EquipContext extends ParserRuleContext {
        public String tValue;
        public String sValue;
        public String min;
        public String max;
        public String eopts;
        public int line;
        public Token t;
        public Token s;
        public Token mn;
        public Token mx;
        public Token e;

        public TerminalNode EQUIP() {
            return getToken(PlayerClassGrammar.EQUIP, 0);
        }

        public List<TerminalNode> DELIM_COLON() {
            return getTokens(PlayerClassGrammar.DELIM_COLON);
        }

        public TerminalNode DELIM_COLON(int i) {
            return getToken(PlayerClassGrammar.DELIM_COLON, i);
        }

        public List<TerminalNode> STRING_BETWEEN_COLONS() {
            return getTokens(PlayerClassGrammar.STRING_BETWEEN_COLONS);
        }

        public TerminalNode STRING_BETWEEN_COLONS(int i) {
            return getToken(PlayerClassGrammar.STRING_BETWEEN_COLONS, i);
        }

        public List<TerminalNode> DELIM_INTEGER() {
            return getTokens(PlayerClassGrammar.DELIM_INTEGER);
        }

        public TerminalNode DELIM_INTEGER(int i) {
            return getToken(PlayerClassGrammar.DELIM_INTEGER, i);
        }

        public EquipContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_equip;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterEquip(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener) ((PlayerClassGrammarListener) listener).exitEquip(this);
        }
    }

    public final EquipContext equip() throws RecognitionException {
        EquipContext _localctx = new EquipContext(_ctx, getState());
        enterRule(_localctx, 36, RULE_equip);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(180);
                match(EQUIP);
                setState(181);
                ((EquipContext) _localctx).t = match(STRING_BETWEEN_COLONS);
                setState(182);
                match(DELIM_COLON);
                setState(183);
                ((EquipContext) _localctx).s = match(STRING_BETWEEN_COLONS);
                setState(184);
                match(DELIM_COLON);
                setState(185);
                ((EquipContext) _localctx).mn = match(DELIM_INTEGER);
                setState(186);
                match(DELIM_COLON);
                setState(187);
                ((EquipContext) _localctx).mx = match(DELIM_INTEGER);
                setState(188);
                match(DELIM_COLON);
                setState(189);
                ((EquipContext) _localctx).e = match(STRING_BETWEEN_COLONS);

                ((EquipContext) _localctx).tValue = ((EquipContext) _localctx).t.getText();
                ((EquipContext) _localctx).sValue = ((EquipContext) _localctx).s.getText();
                ((EquipContext) _localctx).min = ((EquipContext) _localctx).mn.getText();
                ((EquipContext) _localctx).max = ((EquipContext) _localctx).mx.getText();
                ((EquipContext) _localctx).eopts = ((EquipContext) _localctx).e.getText();
                ((EquipContext) _localctx).line = _localctx.start.getLine();

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
    public static class ObjFlagContext extends ParserRuleContext {
        public List<String> oFlags;
        public Token f1;
        public Token f2;

        public TerminalNode OBJ_FLAGS() {
            return getToken(PlayerClassGrammar.OBJ_FLAGS, 0);
        }

        public List<TerminalNode> FLAG() {
            return getTokens(PlayerClassGrammar.FLAG);
        }

        public TerminalNode FLAG(int i) {
            return getToken(PlayerClassGrammar.FLAG, i);
        }

        public List<TerminalNode> FLAG_OR() {
            return getTokens(PlayerClassGrammar.FLAG_OR);
        }

        public TerminalNode FLAG_OR(int i) {
            return getToken(PlayerClassGrammar.FLAG_OR, i);
        }

        public ObjFlagContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_objFlag;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterObjFlag(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitObjFlag(this);
        }
    }

    public final ObjFlagContext objFlag() throws RecognitionException {
        ObjFlagContext _localctx = new ObjFlagContext(_ctx, getState());
        enterRule(_localctx, 38, RULE_objFlag);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(192);
                match(OBJ_FLAGS);
                setState(193);
                ((ObjFlagContext) _localctx).f1 = match(FLAG);

                ((ObjFlagContext) _localctx).oFlags = new ArrayList<>();
                _localctx.oFlags.add(((ObjFlagContext) _localctx).f1.getText());

                setState(200);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == FLAG_OR) {
                    {
                        {
                            setState(195);
                            match(FLAG_OR);
                            setState(196);
                            ((ObjFlagContext) _localctx).f2 = match(FLAG);

                            _localctx.oFlags.add(((ObjFlagContext) _localctx).f2.getText());

                        }
                    }
                    setState(202);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
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
    public static class PlayerFlagsContext extends ParserRuleContext {
        public List<String> pFlags;
        public Token f1;
        public Token f2;

        public TerminalNode PLAYER_FLAGS() {
            return getToken(PlayerClassGrammar.PLAYER_FLAGS, 0);
        }

        public List<TerminalNode> FLAG() {
            return getTokens(PlayerClassGrammar.FLAG);
        }

        public TerminalNode FLAG(int i) {
            return getToken(PlayerClassGrammar.FLAG, i);
        }

        public List<TerminalNode> FLAG_OR() {
            return getTokens(PlayerClassGrammar.FLAG_OR);
        }

        public TerminalNode FLAG_OR(int i) {
            return getToken(PlayerClassGrammar.FLAG_OR, i);
        }

        public PlayerFlagsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_playerFlags;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterPlayerFlags(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitPlayerFlags(this);
        }
    }

    public final PlayerFlagsContext playerFlags() throws RecognitionException {
        PlayerFlagsContext _localctx = new PlayerFlagsContext(_ctx, getState());
        enterRule(_localctx, 40, RULE_playerFlags);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(203);
                match(PLAYER_FLAGS);
                setState(204);
                ((PlayerFlagsContext) _localctx).f1 = match(FLAG);

                ((PlayerFlagsContext) _localctx).pFlags = new ArrayList<>();
                _localctx.pFlags.add(((PlayerFlagsContext) _localctx).f1.getText());

                setState(211);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == FLAG_OR) {
                    {
                        {
                            setState(206);
                            match(FLAG_OR);
                            setState(207);
                            ((PlayerFlagsContext) _localctx).f2 = match(FLAG);

                            _localctx.pFlags.add(((PlayerFlagsContext) _localctx).f2.getText());

                        }
                    }
                    setState(213);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
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
    public static class ExpContext extends ParserRuleContext {
        public String expStr;
        public Token INTEGER;

        public TerminalNode EXP() {
            return getToken(PlayerClassGrammar.EXP, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerClassGrammar.INTEGER, 0);
        }

        public ExpContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_exp;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener) ((PlayerClassGrammarListener) listener).enterExp(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener) ((PlayerClassGrammarListener) listener).exitExp(this);
        }
    }

    public final ExpContext exp() throws RecognitionException {
        ExpContext _localctx = new ExpContext(_ctx, getState());
        enterRule(_localctx, 42, RULE_exp);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(214);
                match(EXP);
                setState(215);
                ((ExpContext) _localctx).INTEGER = match(INTEGER);
                ((ExpContext) _localctx).expStr = ((ExpContext) _localctx).INTEGER.getText();
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
    public static class MagicContext extends ParserRuleContext {
        public String firstSpell;
        public String spellWeight;
        public String noOfBooks;
        public int line;
        public Token f;
        public Token s;
        public Token n;

        public TerminalNode MAGIC() {
            return getToken(PlayerClassGrammar.MAGIC, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(PlayerClassGrammar.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(PlayerClassGrammar.COLON, i);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassGrammar.INTEGER, i);
        }

        public MagicContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_magic;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterMagic(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener) ((PlayerClassGrammarListener) listener).exitMagic(this);
        }
    }

    public final MagicContext magic() throws RecognitionException {
        MagicContext _localctx = new MagicContext(_ctx, getState());
        enterRule(_localctx, 44, RULE_magic);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(218);
                match(MAGIC);
                setState(219);
                ((MagicContext) _localctx).f = match(INTEGER);
                setState(220);
                match(COLON);
                setState(221);
                ((MagicContext) _localctx).s = match(INTEGER);
                setState(222);
                match(COLON);
                setState(223);
                ((MagicContext) _localctx).n = match(INTEGER);

                ((MagicContext) _localctx).firstSpell = ((MagicContext) _localctx).f.getText();
                ((MagicContext) _localctx).spellWeight = ((MagicContext) _localctx).s.getText();
                ((MagicContext) _localctx).noOfBooks = ((MagicContext) _localctx).n.getText();
                ((MagicContext) _localctx).line = _localctx.start.getLine();

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
    public static class MagicBlockContext extends ParserRuleContext {
        public List<ClassEquipParseRecord> equipInit;
        public String firstSpell;
        public String spellWeight;
        public String noOfBooks;
        public int line;
        public List<ClassSpellBookParseRecord> spellBooks;
        public MagicContext magic;
        public BookBlockContext bookBlock;

        public MagicContext magic() {
            return getRuleContext(MagicContext.class, 0);
        }

        public List<BookBlockContext> bookBlock() {
            return getRuleContexts(BookBlockContext.class);
        }

        public BookBlockContext bookBlock(int i) {
            return getRuleContext(BookBlockContext.class, i);
        }

        public MagicBlockContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public MagicBlockContext(ParserRuleContext parent, int invokingState, List<ClassEquipParseRecord> equipInit) {
            super(parent, invokingState);
            this.equipInit = equipInit;
        }

        @Override
        public int getRuleIndex() {
            return RULE_magicBlock;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterMagicBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitMagicBlock(this);
        }
    }

    public final MagicBlockContext magicBlock(List<ClassEquipParseRecord> equipInit) throws RecognitionException {
        MagicBlockContext _localctx = new MagicBlockContext(_ctx, getState(), equipInit);
        enterRule(_localctx, 46, RULE_magicBlock);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(226);
                ((MagicBlockContext) _localctx).magic = magic();
                ((MagicBlockContext) _localctx).firstSpell = ((MagicBlockContext) _localctx).magic.firstSpell;
                ((MagicBlockContext) _localctx).spellWeight = ((MagicBlockContext) _localctx).magic.spellWeight;
                ((MagicBlockContext) _localctx).noOfBooks = ((MagicBlockContext) _localctx).magic.noOfBooks;
                ((MagicBlockContext) _localctx).line = ((MagicBlockContext) _localctx).magic.line;
                ((MagicBlockContext) _localctx).spellBooks = new ArrayList<>();
                setState(231);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(228);
                            ((MagicBlockContext) _localctx).bookBlock = bookBlock(equipInit);
                            _localctx.spellBooks.add(new ClassSpellBookParseRecord(
                                    ((MagicBlockContext) _localctx).bookBlock.oBase, ((MagicBlockContext) _localctx).bookBlock.locFrom, ((MagicBlockContext) _localctx).bookBlock.nameStr,
                                    ((MagicBlockContext) _localctx).bookBlock.noOfSpells, ((MagicBlockContext) _localctx).bookBlock.realm, ((MagicBlockContext) _localctx).bookBlock.glyph,
                                    ((MagicBlockContext) _localctx).bookBlock.colour, ((MagicBlockContext) _localctx).bookBlock.cost, ((MagicBlockContext) _localctx).bookBlock.commonness,
                                    ((MagicBlockContext) _localctx).bookBlock.min, ((MagicBlockContext) _localctx).bookBlock.max, ((MagicBlockContext) _localctx).bookBlock.spells,
                                    ((MagicBlockContext) _localctx).bookBlock.line));
                        }
                    }
                    setState(233);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == BOOK);
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
    public static class BookContext extends ParserRuleContext {
        public String oBase;
        public String location;
        public String bookName;
        public String noOfSpells;
        public String realm;
        public int line;
        public Token t;
        public Token l;
        public Token n;
        public Token s;
        public Token r;

        public TerminalNode BOOK() {
            return getToken(PlayerClassGrammar.BOOK, 0);
        }

        public List<TerminalNode> DELIM_COLON() {
            return getTokens(PlayerClassGrammar.DELIM_COLON);
        }

        public TerminalNode DELIM_COLON(int i) {
            return getToken(PlayerClassGrammar.DELIM_COLON, i);
        }

        public List<TerminalNode> STRING_BETWEEN_COLONS() {
            return getTokens(PlayerClassGrammar.STRING_BETWEEN_COLONS);
        }

        public TerminalNode STRING_BETWEEN_COLONS(int i) {
            return getToken(PlayerClassGrammar.STRING_BETWEEN_COLONS, i);
        }

        public TerminalNode DELIM_INTEGER() {
            return getToken(PlayerClassGrammar.DELIM_INTEGER, 0);
        }

        public BookContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_book;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener) ((PlayerClassGrammarListener) listener).enterBook(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener) ((PlayerClassGrammarListener) listener).exitBook(this);
        }
    }

    public final BookContext book() throws RecognitionException {
        BookContext _localctx = new BookContext(_ctx, getState());
        enterRule(_localctx, 48, RULE_book);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(235);
                match(BOOK);
                setState(236);
                ((BookContext) _localctx).t = match(STRING_BETWEEN_COLONS);
                setState(237);
                match(DELIM_COLON);
                setState(238);
                ((BookContext) _localctx).l = match(STRING_BETWEEN_COLONS);
                setState(239);
                match(DELIM_COLON);
                setState(240);
                ((BookContext) _localctx).n = match(STRING_BETWEEN_COLONS);
                setState(241);
                match(DELIM_COLON);
                setState(242);
                ((BookContext) _localctx).s = match(DELIM_INTEGER);
                setState(243);
                match(DELIM_COLON);
                setState(244);
                ((BookContext) _localctx).r = match(STRING_BETWEEN_COLONS);

                ((BookContext) _localctx).oBase = ((BookContext) _localctx).t.getText();
                ((BookContext) _localctx).location = ((BookContext) _localctx).l.getText();
                ((BookContext) _localctx).bookName = ((BookContext) _localctx).n.getText();
                ((BookContext) _localctx).noOfSpells = ((BookContext) _localctx).s.getText();
                ((BookContext) _localctx).realm = ((BookContext) _localctx).r.getText();
                ((BookContext) _localctx).line = _localctx.start.getLine();

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
    public static class BookGraphicsContext extends ParserRuleContext {
        public String glyph;
        public String colour;
        public Token g;
        public Token c;

        public TerminalNode BOOK_GRAPHICS() {
            return getToken(PlayerClassGrammar.BOOK_GRAPHICS, 0);
        }

        public TerminalNode GLYPH_COLON_SWITCH() {
            return getToken(PlayerClassGrammar.GLYPH_COLON_SWITCH, 0);
        }

        public TerminalNode GLYPH_VALUES() {
            return getToken(PlayerClassGrammar.GLYPH_VALUES, 0);
        }

        public TerminalNode COLOUR_VALUES() {
            return getToken(PlayerClassGrammar.COLOUR_VALUES, 0);
        }

        public BookGraphicsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_bookGraphics;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterBookGraphics(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitBookGraphics(this);
        }
    }

    public final BookGraphicsContext bookGraphics() throws RecognitionException {
        BookGraphicsContext _localctx = new BookGraphicsContext(_ctx, getState());
        enterRule(_localctx, 50, RULE_bookGraphics);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(247);
                match(BOOK_GRAPHICS);
                setState(248);
                ((BookGraphicsContext) _localctx).g = match(GLYPH_VALUES);
                setState(249);
                match(GLYPH_COLON_SWITCH);
                setState(250);
                ((BookGraphicsContext) _localctx).c = match(COLOUR_VALUES);

                ((BookGraphicsContext) _localctx).glyph = ((BookGraphicsContext) _localctx).g.getText();
                ((BookGraphicsContext) _localctx).colour = ((BookGraphicsContext) _localctx).c.getText();

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
    public static class BookPropertiesContext extends ParserRuleContext {
        public String cost;
        public String commonness;
        public String min;
        public String max;
        public Token c;
        public Token comm;
        public Token mn;
        public Token mx;

        public TerminalNode BOOK_PROPERTIES() {
            return getToken(PlayerClassGrammar.BOOK_PROPERTIES, 0);
        }

        public List<TerminalNode> PROP_COLON() {
            return getTokens(PlayerClassGrammar.PROP_COLON);
        }

        public TerminalNode PROP_COLON(int i) {
            return getToken(PlayerClassGrammar.PROP_COLON, i);
        }

        public TerminalNode PROP_TO() {
            return getToken(PlayerClassGrammar.PROP_TO, 0);
        }

        public List<TerminalNode> PROP_INT() {
            return getTokens(PlayerClassGrammar.PROP_INT);
        }

        public TerminalNode PROP_INT(int i) {
            return getToken(PlayerClassGrammar.PROP_INT, i);
        }

        public BookPropertiesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_bookProperties;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterBookProperties(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitBookProperties(this);
        }
    }

    public final BookPropertiesContext bookProperties() throws RecognitionException {
        BookPropertiesContext _localctx = new BookPropertiesContext(_ctx, getState());
        enterRule(_localctx, 52, RULE_bookProperties);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(253);
                match(BOOK_PROPERTIES);
                setState(254);
                ((BookPropertiesContext) _localctx).c = match(PROP_INT);
                setState(255);
                match(PROP_COLON);
                setState(256);
                ((BookPropertiesContext) _localctx).comm = match(PROP_INT);
                setState(257);
                match(PROP_COLON);
                setState(258);
                ((BookPropertiesContext) _localctx).mn = match(PROP_INT);
                setState(259);
                match(PROP_TO);
                setState(260);
                ((BookPropertiesContext) _localctx).mx = match(PROP_INT);

                ((BookPropertiesContext) _localctx).cost = ((BookPropertiesContext) _localctx).c.getText();
                ((BookPropertiesContext) _localctx).commonness = ((BookPropertiesContext) _localctx).comm.getText();
                ((BookPropertiesContext) _localctx).min = ((BookPropertiesContext) _localctx).mn.getText();
                ((BookPropertiesContext) _localctx).max = ((BookPropertiesContext) _localctx).mx.getText();

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
    public static class BookBlockContext extends ParserRuleContext {
        public List<ClassEquipParseRecord> equipInit;
        public String oBase;
        public String locFrom;
        public String nameStr;
        public String noOfSpells;
        public String realm;
        public String glyph;
        public String colour;
        public String cost;
        public String commonness;
        public String min;
        public String max;
        public List<ClassSpellParseRecord> spells;
        public int line;
        public BookContext book;
        public BookGraphicsContext bookGraphics;
        public BookPropertiesContext bookProperties;
        public EquipContext equip;
        public SpellBlockContext spellBlock;

        public BookContext book() {
            return getRuleContext(BookContext.class, 0);
        }

        public BookGraphicsContext bookGraphics() {
            return getRuleContext(BookGraphicsContext.class, 0);
        }

        public BookPropertiesContext bookProperties() {
            return getRuleContext(BookPropertiesContext.class, 0);
        }

        public EquipContext equip() {
            return getRuleContext(EquipContext.class, 0);
        }

        public List<SpellBlockContext> spellBlock() {
            return getRuleContexts(SpellBlockContext.class);
        }

        public SpellBlockContext spellBlock(int i) {
            return getRuleContext(SpellBlockContext.class, i);
        }

        public BookBlockContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public BookBlockContext(ParserRuleContext parent, int invokingState, List<ClassEquipParseRecord> equipInit) {
            super(parent, invokingState);
            this.equipInit = equipInit;
        }

        @Override
        public int getRuleIndex() {
            return RULE_bookBlock;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterBookBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitBookBlock(this);
        }
    }

    public final BookBlockContext bookBlock(List<ClassEquipParseRecord> equipInit) throws RecognitionException {
        BookBlockContext _localctx = new BookBlockContext(_ctx, getState(), equipInit);
        enterRule(_localctx, 54, RULE_bookBlock);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(263);
                ((BookBlockContext) _localctx).book = book();
                ((BookBlockContext) _localctx).glyph = "";
                ((BookBlockContext) _localctx).colour = "";
                ((BookBlockContext) _localctx).cost = "";
                ((BookBlockContext) _localctx).commonness = "";
                ((BookBlockContext) _localctx).min = "";
                ((BookBlockContext) _localctx).max = "";
                ((BookBlockContext) _localctx).oBase = ((BookBlockContext) _localctx).book.oBase;
                ((BookBlockContext) _localctx).locFrom = ((BookBlockContext) _localctx).book.location;
                ((BookBlockContext) _localctx).nameStr = ((BookBlockContext) _localctx).book.bookName;
                ((BookBlockContext) _localctx).noOfSpells = ((BookBlockContext) _localctx).book.noOfSpells;
                ((BookBlockContext) _localctx).realm = ((BookBlockContext) _localctx).book.realm;
                ((BookBlockContext) _localctx).line = ((BookBlockContext) _localctx).book.line;
                ((BookBlockContext) _localctx).spells = new ArrayList<>();
                setState(268);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == BOOK_GRAPHICS) {
                    {
                        setState(265);
                        ((BookBlockContext) _localctx).bookGraphics = bookGraphics();
                        ((BookBlockContext) _localctx).glyph = ((BookBlockContext) _localctx).bookGraphics.glyph;
                        ((BookBlockContext) _localctx).colour = ((BookBlockContext) _localctx).bookGraphics.colour;
                    }
                }

                setState(273);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == BOOK_PROPERTIES) {
                    {
                        setState(270);
                        ((BookBlockContext) _localctx).bookProperties = bookProperties();
                        ((BookBlockContext) _localctx).cost = ((BookBlockContext) _localctx).bookProperties.cost;
                        ((BookBlockContext) _localctx).commonness = ((BookBlockContext) _localctx).bookProperties.commonness;
                        ((BookBlockContext) _localctx).min = ((BookBlockContext) _localctx).bookProperties.min;
                        ((BookBlockContext) _localctx).max = ((BookBlockContext) _localctx).bookProperties.max;
                    }
                }

                setState(278);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == EQUIP) {
                    {
                        setState(275);
                        ((BookBlockContext) _localctx).equip = equip();
                        equipInit.add(new ClassEquipParseRecord(((BookBlockContext) _localctx).equip.tValue,
                                ((BookBlockContext) _localctx).equip.sValue, ((BookBlockContext) _localctx).equip.min, ((BookBlockContext) _localctx).equip.max,
                                ((BookBlockContext) _localctx).equip.eopts, ((BookBlockContext) _localctx).equip.line));
                    }
                }

                setState(283);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(280);
                            ((BookBlockContext) _localctx).spellBlock = spellBlock();
                            _localctx.spells.add(new ClassSpellParseRecord(((BookBlockContext) _localctx).spellBlock.spellName,
                                    ((BookBlockContext) _localctx).spellBlock.level, ((BookBlockContext) _localctx).spellBlock.mana, ((BookBlockContext) _localctx).spellBlock.fail, ((BookBlockContext) _localctx).spellBlock.spellDesc,
                                    ((BookBlockContext) _localctx).spellBlock.effects, ((BookBlockContext) _localctx).spellBlock.exper, ((BookBlockContext) _localctx).spellBlock.line));
                        }
                    }
                    setState(285);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == SPELL);
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
    public static class SpellContext extends ParserRuleContext {
        public String spellname;
        public String level;
        public String mana;
        public String fail;
        public String exper;
        public int line;
        public Token s;
        public Token l;
        public Token m;
        public Token f;
        public Token e;

        public TerminalNode SPELL() {
            return getToken(PlayerClassGrammar.SPELL, 0);
        }

        public List<TerminalNode> DELIM_COLON() {
            return getTokens(PlayerClassGrammar.DELIM_COLON);
        }

        public TerminalNode DELIM_COLON(int i) {
            return getToken(PlayerClassGrammar.DELIM_COLON, i);
        }

        public TerminalNode STRING_BETWEEN_COLONS() {
            return getToken(PlayerClassGrammar.STRING_BETWEEN_COLONS, 0);
        }

        public List<TerminalNode> DELIM_INTEGER() {
            return getTokens(PlayerClassGrammar.DELIM_INTEGER);
        }

        public TerminalNode DELIM_INTEGER(int i) {
            return getToken(PlayerClassGrammar.DELIM_INTEGER, i);
        }

        public SpellContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_spell;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterSpell(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener) ((PlayerClassGrammarListener) listener).exitSpell(this);
        }
    }

    public final SpellContext spell() throws RecognitionException {
        SpellContext _localctx = new SpellContext(_ctx, getState());
        enterRule(_localctx, 56, RULE_spell);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(287);
                match(SPELL);
                setState(288);
                ((SpellContext) _localctx).s = match(STRING_BETWEEN_COLONS);
                setState(289);
                match(DELIM_COLON);
                setState(290);
                ((SpellContext) _localctx).l = match(DELIM_INTEGER);
                setState(291);
                match(DELIM_COLON);
                setState(292);
                ((SpellContext) _localctx).m = match(DELIM_INTEGER);
                setState(293);
                match(DELIM_COLON);
                setState(294);
                ((SpellContext) _localctx).f = match(DELIM_INTEGER);
                setState(295);
                match(DELIM_COLON);
                setState(296);
                ((SpellContext) _localctx).e = match(DELIM_INTEGER);

                ((SpellContext) _localctx).spellname = ((SpellContext) _localctx).s.getText();
                ((SpellContext) _localctx).level = ((SpellContext) _localctx).l.getText();
                ((SpellContext) _localctx).mana = ((SpellContext) _localctx).m.getText();
                ((SpellContext) _localctx).fail = ((SpellContext) _localctx).f.getText();
                ((SpellContext) _localctx).exper = ((SpellContext) _localctx).e.getText();
                ((SpellContext) _localctx).line = _localctx.start.getLine();

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
        public String description;
        public Token STRING;

        public TerminalNode DESC() {
            return getToken(PlayerClassGrammar.DESC, 0);
        }

        public TerminalNode STRING() {
            return getToken(PlayerClassGrammar.STRING, 0);
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
            if (listener instanceof PlayerClassGrammarListener) ((PlayerClassGrammarListener) listener).enterDesc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener) ((PlayerClassGrammarListener) listener).exitDesc(this);
        }
    }

    public final DescContext desc() throws RecognitionException {
        DescContext _localctx = new DescContext(_ctx, getState());
        enterRule(_localctx, 58, RULE_desc);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(299);
                match(DESC);
                setState(300);
                ((DescContext) _localctx).STRING = match(STRING);
                ((DescContext) _localctx).description = ((DescContext) _localctx).STRING.getText();
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
    public static class SpellBlockContext extends ParserRuleContext {
        public String spellName;
        public String level;
        public String mana;
        public String fail;
        public String spellDesc;
        public String exper;
        public int line;
        public List<EffectParseRecord> effects;
        public SpellContext spell;
        public EffectBlockContext effectBlock;
        public DescContext desc;

        public SpellContext spell() {
            return getRuleContext(SpellContext.class, 0);
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

        public SpellBlockContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_spellBlock;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterSpellBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitSpellBlock(this);
        }
    }

    public final SpellBlockContext spellBlock() throws RecognitionException {
        SpellBlockContext _localctx = new SpellBlockContext(_ctx, getState());
        enterRule(_localctx, 60, RULE_spellBlock);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(303);
                ((SpellBlockContext) _localctx).spell = spell();
                ((SpellBlockContext) _localctx).spellDesc = "";
                ((SpellBlockContext) _localctx).effects = new ArrayList<>();
                ((SpellBlockContext) _localctx).spellName = ((SpellBlockContext) _localctx).spell.spellname;
                ((SpellBlockContext) _localctx).level = ((SpellBlockContext) _localctx).spell.level;
                ((SpellBlockContext) _localctx).mana = ((SpellBlockContext) _localctx).spell.mana;
                ((SpellBlockContext) _localctx).fail = ((SpellBlockContext) _localctx).spell.fail;
                ((SpellBlockContext) _localctx).exper = ((SpellBlockContext) _localctx).spell.exper;
                ((SpellBlockContext) _localctx).line = ((SpellBlockContext) _localctx).spell.line;
                setState(313);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == DESC || _la == EFFECT) {
                    {
                        setState(311);
                        _errHandler.sync(this);
                        switch (_input.LA(1)) {
                            case EFFECT: {
                                {
                                    setState(305);
                                    ((SpellBlockContext) _localctx).effectBlock = effectBlock();
                                    _localctx.effects.add(new EffectParseRecord(((SpellBlockContext) _localctx).effectBlock.typeInit,
                                            ((SpellBlockContext) _localctx).effectBlock.subtypeWrapperInit, ((SpellBlockContext) _localctx).effectBlock.radius, ((SpellBlockContext) _localctx).effectBlock.other,
                                            ((SpellBlockContext) _localctx).effectBlock.diceString, ((SpellBlockContext) _localctx).effectBlock.yVal, ((SpellBlockContext) _localctx).effectBlock.xVal,
                                            ((SpellBlockContext) _localctx).effectBlock.expressionChars, ((SpellBlockContext) _localctx).effectBlock.expressionBase,
                                            ((SpellBlockContext) _localctx).effectBlock.expressionOperation, ((SpellBlockContext) _localctx).effectBlock.timeDiceString,
                                            ((SpellBlockContext) _localctx).effectBlock.effectMessage, (((SpellBlockContext) _localctx).effectBlock != null ? (((SpellBlockContext) _localctx).effectBlock.start) : null).getLine()));
                                }
                            }
                            break;
                            case DESC: {
                                {
                                    setState(308);
                                    ((SpellBlockContext) _localctx).desc = desc();
                                    ((SpellBlockContext) _localctx).spellDesc = _localctx.spellDesc + ((SpellBlockContext) _localctx).desc.description;
                                }
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                    }
                    setState(315);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
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
    public static class PlayerClassContext extends ParserRuleContext {
        public PlayerClassParseRecord pClass;
        public NameContext name;
        public StatsContext stats;
        public SkillDisarmPhysContext skillDisarmPhys;
        public SkillDisarmMagicContext skillDisarmMagic;
        public SkillSaveContext skillSave;
        public SkillDeviceContext skillDevice;
        public SkillStealthContext skillStealth;
        public SkillSearchContext skillSearch;
        public SkillMeleeContext skillMelee;
        public SkillShootContext skillShoot;
        public SkillThrowContext skillThrow;
        public SkillDigContext skillDig;
        public HitdieContext hitdie;
        public MaxAttacksContext maxAttacks;
        public MinWeightContext minWeight;
        public StrengthMultiplierContext strengthMultiplier;
        public ExpContext exp;
        public TitleContext title;
        public EquipContext equip;
        public ObjFlagContext objFlag;
        public PlayerFlagsContext playerFlags;
        public MagicBlockContext magicBlock;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public List<StatsContext> stats() {
            return getRuleContexts(StatsContext.class);
        }

        public StatsContext stats(int i) {
            return getRuleContext(StatsContext.class, i);
        }

        public List<SkillDisarmPhysContext> skillDisarmPhys() {
            return getRuleContexts(SkillDisarmPhysContext.class);
        }

        public SkillDisarmPhysContext skillDisarmPhys(int i) {
            return getRuleContext(SkillDisarmPhysContext.class, i);
        }

        public List<SkillDisarmMagicContext> skillDisarmMagic() {
            return getRuleContexts(SkillDisarmMagicContext.class);
        }

        public SkillDisarmMagicContext skillDisarmMagic(int i) {
            return getRuleContext(SkillDisarmMagicContext.class, i);
        }

        public List<SkillSaveContext> skillSave() {
            return getRuleContexts(SkillSaveContext.class);
        }

        public SkillSaveContext skillSave(int i) {
            return getRuleContext(SkillSaveContext.class, i);
        }

        public List<SkillDeviceContext> skillDevice() {
            return getRuleContexts(SkillDeviceContext.class);
        }

        public SkillDeviceContext skillDevice(int i) {
            return getRuleContext(SkillDeviceContext.class, i);
        }

        public List<SkillStealthContext> skillStealth() {
            return getRuleContexts(SkillStealthContext.class);
        }

        public SkillStealthContext skillStealth(int i) {
            return getRuleContext(SkillStealthContext.class, i);
        }

        public List<SkillSearchContext> skillSearch() {
            return getRuleContexts(SkillSearchContext.class);
        }

        public SkillSearchContext skillSearch(int i) {
            return getRuleContext(SkillSearchContext.class, i);
        }

        public List<SkillMeleeContext> skillMelee() {
            return getRuleContexts(SkillMeleeContext.class);
        }

        public SkillMeleeContext skillMelee(int i) {
            return getRuleContext(SkillMeleeContext.class, i);
        }

        public List<SkillShootContext> skillShoot() {
            return getRuleContexts(SkillShootContext.class);
        }

        public SkillShootContext skillShoot(int i) {
            return getRuleContext(SkillShootContext.class, i);
        }

        public List<SkillThrowContext> skillThrow() {
            return getRuleContexts(SkillThrowContext.class);
        }

        public SkillThrowContext skillThrow(int i) {
            return getRuleContext(SkillThrowContext.class, i);
        }

        public List<SkillDigContext> skillDig() {
            return getRuleContexts(SkillDigContext.class);
        }

        public SkillDigContext skillDig(int i) {
            return getRuleContext(SkillDigContext.class, i);
        }

        public List<HitdieContext> hitdie() {
            return getRuleContexts(HitdieContext.class);
        }

        public HitdieContext hitdie(int i) {
            return getRuleContext(HitdieContext.class, i);
        }

        public List<MaxAttacksContext> maxAttacks() {
            return getRuleContexts(MaxAttacksContext.class);
        }

        public MaxAttacksContext maxAttacks(int i) {
            return getRuleContext(MaxAttacksContext.class, i);
        }

        public List<MinWeightContext> minWeight() {
            return getRuleContexts(MinWeightContext.class);
        }

        public MinWeightContext minWeight(int i) {
            return getRuleContext(MinWeightContext.class, i);
        }

        public List<StrengthMultiplierContext> strengthMultiplier() {
            return getRuleContexts(StrengthMultiplierContext.class);
        }

        public StrengthMultiplierContext strengthMultiplier(int i) {
            return getRuleContext(StrengthMultiplierContext.class, i);
        }

        public List<ExpContext> exp() {
            return getRuleContexts(ExpContext.class);
        }

        public ExpContext exp(int i) {
            return getRuleContext(ExpContext.class, i);
        }

        public List<TitleContext> title() {
            return getRuleContexts(TitleContext.class);
        }

        public TitleContext title(int i) {
            return getRuleContext(TitleContext.class, i);
        }

        public List<EquipContext> equip() {
            return getRuleContexts(EquipContext.class);
        }

        public EquipContext equip(int i) {
            return getRuleContext(EquipContext.class, i);
        }

        public List<ObjFlagContext> objFlag() {
            return getRuleContexts(ObjFlagContext.class);
        }

        public ObjFlagContext objFlag(int i) {
            return getRuleContext(ObjFlagContext.class, i);
        }

        public List<PlayerFlagsContext> playerFlags() {
            return getRuleContexts(PlayerFlagsContext.class);
        }

        public PlayerFlagsContext playerFlags(int i) {
            return getRuleContext(PlayerFlagsContext.class, i);
        }

        public List<MagicBlockContext> magicBlock() {
            return getRuleContexts(MagicBlockContext.class);
        }

        public MagicBlockContext magicBlock(int i) {
            return getRuleContext(MagicBlockContext.class, i);
        }

        public PlayerClassContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_playerClass;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterPlayerClass(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitPlayerClass(this);
        }
    }

    public final PlayerClassContext playerClass() throws RecognitionException {
        PlayerClassContext _localctx = new PlayerClassContext(_ctx, getState());
        enterRule(_localctx, 62, RULE_playerClass);

        int line;
        String nameInit = "";
        Map<String, String> statsInit = new HashMap<>();
        Map<String, String> classSkills = new HashMap<>();
        Map<String, String> extraSkills = new HashMap<>();
        String hitdieInit = "";
        String expInit = "";
        String maxAttInit = "";
        String minWghtInit = "";
        String strMultInit = "";
        List<String> titlesInit = new ArrayList<>();
        List<ClassEquipParseRecord> equipInit = new ArrayList<>();
        List<String> oFlagInit = new ArrayList<>();
        List<String> pFlagInit = new ArrayList<>();
        ClassMagicParseRecord magicInit = null;

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(316);
                ((PlayerClassContext) _localctx).name = name();
                line = ((PlayerClassContext) _localctx).name.lineNo;
                nameInit = ((PlayerClassContext) _localctx).name.nameStr;
                setState(381);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        setState(381);
                        _errHandler.sync(this);
                        switch (_input.LA(1)) {
                            case STATS: {
                                setState(318);
                                ((PlayerClassContext) _localctx).stats = stats();

                                statsInit.put("STAT_STR", ((PlayerClassContext) _localctx).stats.strs);
                                statsInit.put("STAT_INT", ((PlayerClassContext) _localctx).stats.ints);
                                statsInit.put("STAT_WIS", ((PlayerClassContext) _localctx).stats.wiss);
                                statsInit.put("STAT_DEX", ((PlayerClassContext) _localctx).stats.dexs);
                                statsInit.put("STAT_CON", ((PlayerClassContext) _localctx).stats.cons);

                            }
                            break;
                            case SKILL_DISARM_PHYS: {
                                setState(321);
                                ((PlayerClassContext) _localctx).skillDisarmPhys = skillDisarmPhys();
                                classSkills.put("SKILL_DISARM_PHYS", ((PlayerClassContext) _localctx).skillDisarmPhys.skillVal);
                                extraSkills.put("SKILL_DISARM_PHYS", ((PlayerClassContext) _localctx).skillDisarmPhys.skillInc);
                            }
                            break;
                            case SKILL_DISARM_MAGIC: {
                                setState(324);
                                ((PlayerClassContext) _localctx).skillDisarmMagic = skillDisarmMagic();
                                classSkills.put("SKILL_DISARM_MAGIC", ((PlayerClassContext) _localctx).skillDisarmMagic.skillVal);
                                extraSkills.put("SKILL_DISARM_MAGIC", ((PlayerClassContext) _localctx).skillDisarmMagic.skillInc);
                            }
                            break;
                            case SKILL_SAVE: {
                                setState(327);
                                ((PlayerClassContext) _localctx).skillSave = skillSave();
                                classSkills.put("SKILL_SAVE", ((PlayerClassContext) _localctx).skillSave.skillVal);
                                extraSkills.put("SKILL_SAVE", ((PlayerClassContext) _localctx).skillSave.skillInc);
                            }
                            break;
                            case SKILL_DEVICE: {
                                setState(330);
                                ((PlayerClassContext) _localctx).skillDevice = skillDevice();
                                classSkills.put("SKILL_DEVICE", ((PlayerClassContext) _localctx).skillDevice.skillVal);
                                extraSkills.put("SKILL_DEVICE", ((PlayerClassContext) _localctx).skillDevice.skillInc);
                            }
                            break;
                            case SKILL_STEALTH: {
                                setState(333);
                                ((PlayerClassContext) _localctx).skillStealth = skillStealth();
                                classSkills.put("SKILL_STEALTH", ((PlayerClassContext) _localctx).skillStealth.skillVal);
                                extraSkills.put("SKILL_STEALTH", ((PlayerClassContext) _localctx).skillStealth.skillInc);
                            }
                            break;
                            case SKILL_SEARCH: {
                                setState(336);
                                ((PlayerClassContext) _localctx).skillSearch = skillSearch();
                                classSkills.put("SKILL_SEARCH", ((PlayerClassContext) _localctx).skillSearch.skillVal);
                                extraSkills.put("SKILL_SEARCH", ((PlayerClassContext) _localctx).skillSearch.skillInc);
                            }
                            break;
                            case SKILL_MELEE: {
                                setState(339);
                                ((PlayerClassContext) _localctx).skillMelee = skillMelee();
                                classSkills.put("SKILL_TO_HIT_MELEE", ((PlayerClassContext) _localctx).skillMelee.skillVal);
                                extraSkills.put("SKILL_TO_HIT_MELEE", ((PlayerClassContext) _localctx).skillMelee.skillInc);
                            }
                            break;
                            case SKILL_SHOOT: {
                                setState(342);
                                ((PlayerClassContext) _localctx).skillShoot = skillShoot();
                                classSkills.put("SKILL_TO_HIT_BOW", ((PlayerClassContext) _localctx).skillShoot.skillVal);
                                extraSkills.put("SKILL_TO_HIT_BOW", ((PlayerClassContext) _localctx).skillShoot.skillInc);
                            }
                            break;
                            case SKILL_THROW: {
                                setState(345);
                                ((PlayerClassContext) _localctx).skillThrow = skillThrow();
                                classSkills.put("SKILL_TO_HIT_THROW", ((PlayerClassContext) _localctx).skillThrow.skillVal);
                                extraSkills.put("SKILL_TO_HIT_THROW", ((PlayerClassContext) _localctx).skillThrow.skillInc);
                            }
                            break;
                            case SKILL_DIG: {
                                setState(348);
                                ((PlayerClassContext) _localctx).skillDig = skillDig();
                                classSkills.put("SKILL_DIGGING", ((PlayerClassContext) _localctx).skillDig.skillVal);
                                extraSkills.put("SKILL_DIGGING", ((PlayerClassContext) _localctx).skillDig.skillInc);
                            }
                            break;
                            case HITDIE: {
                                setState(351);
                                ((PlayerClassContext) _localctx).hitdie = hitdie();
                                hitdieInit = ((PlayerClassContext) _localctx).hitdie.increment;
                            }
                            break;
                            case MAX_ATTACKS: {
                                setState(354);
                                ((PlayerClassContext) _localctx).maxAttacks = maxAttacks();
                                maxAttInit = ((PlayerClassContext) _localctx).maxAttacks.maxAtt;
                            }
                            break;
                            case MIN_WEIGHT: {
                                setState(357);
                                ((PlayerClassContext) _localctx).minWeight = minWeight();
                                minWghtInit = ((PlayerClassContext) _localctx).minWeight.minWgt;
                            }
                            break;
                            case STRENGTH: {
                                setState(360);
                                ((PlayerClassContext) _localctx).strengthMultiplier = strengthMultiplier();
                                strMultInit = ((PlayerClassContext) _localctx).strengthMultiplier.strMult;
                            }
                            break;
                            case EXP: {
                                setState(363);
                                ((PlayerClassContext) _localctx).exp = exp();
                                expInit = ((PlayerClassContext) _localctx).exp.expStr;
                            }
                            break;
                            case TITLE: {
                                setState(366);
                                ((PlayerClassContext) _localctx).title = title();
                                titlesInit.add(((PlayerClassContext) _localctx).title.titleStr);
                            }
                            break;
                            case EQUIP: {
                                setState(369);
                                ((PlayerClassContext) _localctx).equip = equip();
                                equipInit.add(new ClassEquipParseRecord(((PlayerClassContext) _localctx).equip.tValue,
                                        ((PlayerClassContext) _localctx).equip.sValue, ((PlayerClassContext) _localctx).equip.min, ((PlayerClassContext) _localctx).equip.max,
                                        ((PlayerClassContext) _localctx).equip.eopts, ((PlayerClassContext) _localctx).equip.line));
                            }
                            break;
                            case OBJ_FLAGS: {
                                setState(372);
                                ((PlayerClassContext) _localctx).objFlag = objFlag();
                                oFlagInit.addAll(((PlayerClassContext) _localctx).objFlag.oFlags);
                            }
                            break;
                            case PLAYER_FLAGS: {
                                setState(375);
                                ((PlayerClassContext) _localctx).playerFlags = playerFlags();
                                pFlagInit.addAll(((PlayerClassContext) _localctx).playerFlags.pFlags);
                            }
                            break;
                            case MAGIC: {
                                {
                                    setState(378);
                                    ((PlayerClassContext) _localctx).magicBlock = magicBlock(equipInit);
                                    magicInit = new ClassMagicParseRecord(((PlayerClassContext) _localctx).magicBlock.firstSpell,
                                            ((PlayerClassContext) _localctx).magicBlock.spellWeight, ((PlayerClassContext) _localctx).magicBlock.noOfBooks,
                                            ((PlayerClassContext) _localctx).magicBlock.spellBooks, ((PlayerClassContext) _localctx).magicBlock.line);
                                }
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                    }
                    setState(383);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 16777208L) != 0));
            }
            _ctx.stop = _input.LT(-1);

            ((PlayerClassContext) _localctx).pClass = new PlayerClassParseRecord(nameInit, statsInit, classSkills,
                    extraSkills, hitdieInit, expInit, maxAttInit, minWghtInit,
                    strMultInit, equipInit, oFlagInit, pFlagInit, titlesInit,
                    magicInit, line);

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
        public List<PlayerClassParseRecord> playerClasses;
        public RecordCountContext recordCount;
        public PlayerClassContext playerClass;

        public RecordCountContext recordCount() {
            return getRuleContext(RecordCountContext.class, 0);
        }

        public TerminalNode EOF() {
            return getToken(PlayerClassGrammar.EOF, 0);
        }

        public List<PlayerClassContext> playerClass() {
            return getRuleContexts(PlayerClassContext.class);
        }

        public PlayerClassContext playerClass(int i) {
            return getRuleContext(PlayerClassContext.class, i);
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
            if (listener instanceof PlayerClassGrammarListener) ((PlayerClassGrammarListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener) ((PlayerClassGrammarListener) listener).exitFile(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 64, RULE_file);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(385);
                ((FileContext) _localctx).recordCount = recordCount();
                ((FileContext) _localctx).playerClasses = new ArrayList<>();
                ((FileContext) _localctx).declaredRecordCount = ((FileContext) _localctx).recordCount.count;
                setState(390);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(387);
                            ((FileContext) _localctx).playerClass = playerClass();
                            _localctx.playerClasses.add(((FileContext) _localctx).playerClass.pClass);
                        }
                    }
                    setState(392);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(394);
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
            return getToken(PlayerClassGrammar.EFFECT, 0);
        }

        public List<TerminalNode> UCASE() {
            return getTokens(PlayerClassGrammar.UCASE);
        }

        public TerminalNode UCASE(int i) {
            return getToken(PlayerClassGrammar.UCASE, i);
        }

        public List<TerminalNode> COLON() {
            return getTokens(PlayerClassGrammar.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(PlayerClassGrammar.COLON, i);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassGrammar.INTEGER, i);
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
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterEffect(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitEffect(this);
        }
    }

    public final EffectContext effect() throws RecognitionException {
        EffectContext _localctx = new EffectContext(_ctx, getState());
        enterRule(_localctx, 66, RULE_effect);

        ((EffectContext) _localctx).wrapper = "";
        ((EffectContext) _localctx).radius = "";
        ((EffectContext) _localctx).other = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(396);
                match(EFFECT);
                setState(397);
                ((EffectContext) _localctx).t = match(UCASE);

                ((EffectContext) _localctx).type = ((EffectContext) _localctx).t.getText();

                setState(412);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COLON) {
                    {
                        setState(399);
                        match(COLON);
                        setState(400);
                        ((EffectContext) _localctx).st = match(UCASE);

                        ((EffectContext) _localctx).wrapper = ((EffectContext) _localctx).st.getText().toUpperCase();

                        setState(410);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == COLON) {
                            {
                                setState(402);
                                match(COLON);
                                setState(403);
                                ((EffectContext) _localctx).rad = match(INTEGER);

                                ((EffectContext) _localctx).radius = ((EffectContext) _localctx).rad.getText();

                                setState(408);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                                if (_la == COLON) {
                                    {
                                        setState(405);
                                        match(COLON);
                                        setState(406);
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
            return getToken(PlayerClassGrammar.TIME, 0);
        }

        public TerminalNode DICE_SIMPLE_VALUE() {
            return getToken(PlayerClassGrammar.DICE_SIMPLE_VALUE, 0);
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
            if (listener instanceof PlayerClassGrammarListener) ((PlayerClassGrammarListener) listener).enterTime(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener) ((PlayerClassGrammarListener) listener).exitTime(this);
        }
    }

    public final TimeContext time() throws RecognitionException {
        TimeContext _localctx = new TimeContext(_ctx, getState());
        enterRule(_localctx, 68, RULE_time);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(414);
                match(TIME);
                setState(415);
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
            return getToken(PlayerClassGrammar.EFFECT_YX, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerClassGrammar.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassGrammar.INTEGER, i);
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
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterEffectYX(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitEffectYX(this);
        }
    }

    public final EffectYXContext effectYX() throws RecognitionException {
        EffectYXContext _localctx = new EffectYXContext(_ctx, getState());
        enterRule(_localctx, 70, RULE_effectYX);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(418);
                match(EFFECT_YX);
                setState(419);
                ((EffectYXContext) _localctx).yVal = match(INTEGER);
                setState(420);
                match(COLON);
                setState(421);
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
            return getToken(PlayerClassGrammar.DICE, 0);
        }

        public TerminalNode DICE_SIMPLE_VALUE() {
            return getToken(PlayerClassGrammar.DICE_SIMPLE_VALUE, 0);
        }

        public TerminalNode DICE_COMPLEX_VALUE() {
            return getToken(PlayerClassGrammar.DICE_COMPLEX_VALUE, 0);
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
            if (listener instanceof PlayerClassGrammarListener) ((PlayerClassGrammarListener) listener).enterDice(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener) ((PlayerClassGrammarListener) listener).exitDice(this);
        }
    }

    public final DiceContext dice() throws RecognitionException {
        DiceContext _localctx = new DiceContext(_ctx, getState());
        enterRule(_localctx, 72, RULE_dice);

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
                setState(424);
                match(DICE);
                setState(436);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case DICE_COMPLEX_VALUE: {
                        {
                            setState(425);
                            ((DiceContext) _localctx).val = match(DICE_COMPLEX_VALUE);

                            ((DiceContext) _localctx).diceString = ((DiceContext) _localctx).val.getText();

                            setState(430);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            do {
                                {
                                    {
                                        setState(427);
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
                                setState(432);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                            } while (_la == EXPR);
                        }
                    }
                    break;
                    case DICE_SIMPLE_VALUE: {
                        setState(434);
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
            return getToken(PlayerClassGrammar.EXPR, 0);
        }

        public List<TerminalNode> EXPR_COLON() {
            return getTokens(PlayerClassGrammar.EXPR_COLON);
        }

        public TerminalNode EXPR_COLON(int i) {
            return getToken(PlayerClassGrammar.EXPR_COLON, i);
        }

        public TerminalNode EXPR_CHAR() {
            return getToken(PlayerClassGrammar.EXPR_CHAR, 0);
        }

        public TerminalNode EXPR_UCASE() {
            return getToken(PlayerClassGrammar.EXPR_UCASE, 0);
        }

        public TerminalNode EXPR_OP() {
            return getToken(PlayerClassGrammar.EXPR_OP, 0);
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
            if (listener instanceof PlayerClassGrammarListener) ((PlayerClassGrammarListener) listener).enterExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener) ((PlayerClassGrammarListener) listener).exitExpr(this);
        }
    }

    public final ExprContext expr() throws RecognitionException {
        ExprContext _localctx = new ExprContext(_ctx, getState());
        enterRule(_localctx, 74, RULE_expr);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(438);
                match(EXPR);
                setState(439);
                ((ExprContext) _localctx).ch = match(EXPR_CHAR);
                setState(440);
                match(EXPR_COLON);
                setState(441);
                ((ExprContext) _localctx).base = match(EXPR_UCASE);
                setState(442);
                match(EXPR_COLON);
                setState(443);
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
            return getToken(PlayerClassGrammar.EFFECT_MESSAGE, 0);
        }

        public TerminalNode FREE_TEXT() {
            return getToken(PlayerClassGrammar.FREE_TEXT, 0);
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
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterEffectMsg(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitEffectMsg(this);
        }
    }

    public final EffectMsgContext effectMsg() throws RecognitionException {
        EffectMsgContext _localctx = new EffectMsgContext(_ctx, getState());
        enterRule(_localctx, 76, RULE_effectMsg);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(446);
                match(EFFECT_MESSAGE);
                setState(447);
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
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).enterEffectBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassGrammarListener)
                ((PlayerClassGrammarListener) listener).exitEffectBlock(this);
        }
    }

    public final EffectBlockContext effectBlock() throws RecognitionException {
        EffectBlockContext _localctx = new EffectBlockContext(_ctx, getState());
        enterRule(_localctx, 78, RULE_effectBlock);

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
                setState(450);
                ((EffectBlockContext) _localctx).effect = effect();

                ((EffectBlockContext) _localctx).lineNo = _localctx.start.getLine();
                ((EffectBlockContext) _localctx).typeInit = ((EffectBlockContext) _localctx).effect.type;
                ((EffectBlockContext) _localctx).subtypeWrapperInit = ((EffectBlockContext) _localctx).effect.wrapper;
                ((EffectBlockContext) _localctx).radius = ((EffectBlockContext) _localctx).effect.radius;
                ((EffectBlockContext) _localctx).other = ((EffectBlockContext) _localctx).effect.other;

                setState(460);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case EFFECT_YX: {
                        {
                            setState(452);
                            ((EffectBlockContext) _localctx).effectYX = effectYX();

                            ((EffectBlockContext) _localctx).yVal = ((EffectBlockContext) _localctx).effectYX.y;
                            ((EffectBlockContext) _localctx).xVal = ((EffectBlockContext) _localctx).effectYX.x;

                        }
                    }
                    break;
                    case EOF:
                    case NAME:
                    case STATS:
                    case SKILL_DISARM_PHYS:
                    case SKILL_DISARM_MAGIC:
                    case SKILL_DEVICE:
                    case SKILL_SAVE:
                    case SKILL_STEALTH:
                    case SKILL_SEARCH:
                    case SKILL_MELEE:
                    case SKILL_SHOOT:
                    case SKILL_THROW:
                    case SKILL_DIG:
                    case HITDIE:
                    case EXP:
                    case MAX_ATTACKS:
                    case MIN_WEIGHT:
                    case STRENGTH:
                    case TITLE:
                    case EQUIP:
                    case OBJ_FLAGS:
                    case PLAYER_FLAGS:
                    case MAGIC:
                    case BOOK:
                    case SPELL:
                    case DESC:
                    case EFFECT:
                    case EFFECT_MESSAGE:
                    case DICE:
                    case TIME: {
                        {
                            setState(458);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            if (_la == DICE) {
                                {
                                    setState(455);
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
                setState(465);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == TIME) {
                    {
                        setState(462);
                        ((EffectBlockContext) _localctx).time = time();

                        ((EffectBlockContext) _localctx).timeDiceString = ((EffectBlockContext) _localctx).time.timeStr;

                    }
                }

                setState(470);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == EFFECT_MESSAGE) {
                    {
                        setState(467);
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
            "\u0004\u0001E\u01d9\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002" +
                    "\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002" +
                    "\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f" +
                    "\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012" +
                    "\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015" +
                    "\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018" +
                    "\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b" +
                    "\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e" +
                    "\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002" +
                    "#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007\'\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001" +
                    "\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e" +
                    "\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010" +
                    "\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011" +
                    "\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012" +
                    "\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0005\u0013\u00c7\b\u0013\n\u0013\f\u0013\u00ca\t\u0013\u0001\u0014\u0001" +
                    "\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0005\u0014\u00d2" +
                    "\b\u0014\n\u0014\f\u0014\u00d5\t\u0014\u0001\u0015\u0001\u0015\u0001\u0015" +
                    "\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016" +
                    "\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0017" +
                    "\u0001\u0017\u0001\u0017\u0004\u0017\u00e8\b\u0017\u000b\u0017\f\u0017" +
                    "\u00e9\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001" +
                    "\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001" +
                    "\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001" +
                    "\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001" +
                    "\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001b\u0001" +
                    "\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0003\u001b\u010d\b\u001b\u0001" +
                    "\u001b\u0001\u001b\u0001\u001b\u0003\u001b\u0112\b\u001b\u0001\u001b\u0001" +
                    "\u001b\u0001\u001b\u0003\u001b\u0117\b\u001b\u0001\u001b\u0001\u001b\u0001" +
                    "\u001b\u0004\u001b\u011c\b\u001b\u000b\u001b\f\u001b\u011d\u0001\u001c" +
                    "\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c" +
                    "\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001d" +
                    "\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001e\u0001\u001e\u0001\u001e" +
                    "\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0005\u001e" +
                    "\u0138\b\u001e\n\u001e\f\u001e\u013b\t\u001e\u0001\u001f\u0001\u001f\u0001" +
                    "\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001" +
                    "\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001" +
                    "\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001" +
                    "\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001" +
                    "\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001" +
                    "\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001" +
                    "\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001" +
                    "\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001" +
                    "\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001" +
                    "\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001" +
                    "\u001f\u0001\u001f\u0001\u001f\u0004\u001f\u017e\b\u001f\u000b\u001f\f" +
                    "\u001f\u017f\u0001 \u0001 \u0001 \u0001 \u0001 \u0004 \u0187\b \u000b" +
                    " \f \u0188\u0001 \u0001 \u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001" +
                    "!\u0001!\u0001!\u0001!\u0001!\u0001!\u0003!\u0199\b!\u0003!\u019b\b!\u0003" +
                    "!\u019d\b!\u0001\"\u0001\"\u0001\"\u0001\"\u0001#\u0001#\u0001#\u0001" +
                    "#\u0001#\u0001#\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0004$\u01af" +
                    "\b$\u000b$\f$\u01b0\u0001$\u0001$\u0003$\u01b5\b$\u0001%\u0001%\u0001" +
                    "%\u0001%\u0001%\u0001%\u0001%\u0001%\u0001&\u0001&\u0001&\u0001&\u0001" +
                    "\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'\u0003\'\u01cb" +
                    "\b\'\u0003\'\u01cd\b\'\u0001\'\u0001\'\u0001\'\u0003\'\u01d2\b\'\u0001" +
                    "\'\u0001\'\u0001\'\u0003\'\u01d7\b\'\u0001\'\u0000\u0000(\u0000\u0002" +
                    "\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e" +
                    " \"$&(*,.02468:<>@BDFHJLN\u0000\u0000\u01d8\u0000P\u0001\u0000\u0000\u0000" +
                    "\u0002T\u0001\u0000\u0000\u0000\u0004X\u0001\u0000\u0000\u0000\u0006d" +
                    "\u0001\u0000\u0000\u0000\bj\u0001\u0000\u0000\u0000\np\u0001\u0000\u0000" +
                    "\u0000\fv\u0001\u0000\u0000\u0000\u000e|\u0001\u0000\u0000\u0000\u0010" +
                    "\u0082\u0001\u0000\u0000\u0000\u0012\u0088\u0001\u0000\u0000\u0000\u0014" +
                    "\u008e\u0001\u0000\u0000\u0000\u0016\u0094\u0001\u0000\u0000\u0000\u0018" +
                    "\u009a\u0001\u0000\u0000\u0000\u001a\u00a0\u0001\u0000\u0000\u0000\u001c" +
                    "\u00a4\u0001\u0000\u0000\u0000\u001e\u00a8\u0001\u0000\u0000\u0000 \u00ac" +
                    "\u0001\u0000\u0000\u0000\"\u00b0\u0001\u0000\u0000\u0000$\u00b4\u0001" +
                    "\u0000\u0000\u0000&\u00c0\u0001\u0000\u0000\u0000(\u00cb\u0001\u0000\u0000" +
                    "\u0000*\u00d6\u0001\u0000\u0000\u0000,\u00da\u0001\u0000\u0000\u0000." +
                    "\u00e2\u0001\u0000\u0000\u00000\u00eb\u0001\u0000\u0000\u00002\u00f7\u0001" +
                    "\u0000\u0000\u00004\u00fd\u0001\u0000\u0000\u00006\u0107\u0001\u0000\u0000" +
                    "\u00008\u011f\u0001\u0000\u0000\u0000:\u012b\u0001\u0000\u0000\u0000<" +
                    "\u012f\u0001\u0000\u0000\u0000>\u013c\u0001\u0000\u0000\u0000@\u0181\u0001" +
                    "\u0000\u0000\u0000B\u018c\u0001\u0000\u0000\u0000D\u019e\u0001\u0000\u0000" +
                    "\u0000F\u01a2\u0001\u0000\u0000\u0000H\u01a8\u0001\u0000\u0000\u0000J" +
                    "\u01b6\u0001\u0000\u0000\u0000L\u01be\u0001\u0000\u0000\u0000N\u01c2\u0001" +
                    "\u0000\u0000\u0000PQ\u0005\u0001\u0000\u0000QR\u0005\u001e\u0000\u0000" +
                    "RS\u0006\u0000\uffff\uffff\u0000S\u0001\u0001\u0000\u0000\u0000TU\u0005" +
                    "\u0002\u0000\u0000UV\u00058\u0000\u0000VW\u0006\u0001\uffff\uffff\u0000" +
                    "W\u0003\u0001\u0000\u0000\u0000XY\u0005\u0003\u0000\u0000YZ\u0005\u001e" +
                    "\u0000\u0000Z[\u0005\u001d\u0000\u0000[\\\u0005\u001e\u0000\u0000\\]\u0005" +
                    "\u001d\u0000\u0000]^\u0005\u001e\u0000\u0000^_\u0005\u001d\u0000\u0000" +
                    "_`\u0005\u001e\u0000\u0000`a\u0005\u001d\u0000\u0000ab\u0005\u001e\u0000" +
                    "\u0000bc\u0006\u0002\uffff\uffff\u0000c\u0005\u0001\u0000\u0000\u0000" +
                    "de\u0005\u0004\u0000\u0000ef\u0005\u001e\u0000\u0000fg\u0005\u001d\u0000" +
                    "\u0000gh\u0005\u001e\u0000\u0000hi\u0006\u0003\uffff\uffff\u0000i\u0007" +
                    "\u0001\u0000\u0000\u0000jk\u0005\u0005\u0000\u0000kl\u0005\u001e\u0000" +
                    "\u0000lm\u0005\u001d\u0000\u0000mn\u0005\u001e\u0000\u0000no\u0006\u0004" +
                    "\uffff\uffff\u0000o\t\u0001\u0000\u0000\u0000pq\u0005\u0006\u0000\u0000" +
                    "qr\u0005\u001e\u0000\u0000rs\u0005\u001d\u0000\u0000st\u0005\u001e\u0000" +
                    "\u0000tu\u0006\u0005\uffff\uffff\u0000u\u000b\u0001\u0000\u0000\u0000" +
                    "vw\u0005\u0007\u0000\u0000wx\u0005\u001e\u0000\u0000xy\u0005\u001d\u0000" +
                    "\u0000yz\u0005\u001e\u0000\u0000z{\u0006\u0006\uffff\uffff\u0000{\r\u0001" +
                    "\u0000\u0000\u0000|}\u0005\b\u0000\u0000}~\u0005\u001e\u0000\u0000~\u007f" +
                    "\u0005\u001d\u0000\u0000\u007f\u0080\u0005\u001e\u0000\u0000\u0080\u0081" +
                    "\u0006\u0007\uffff\uffff\u0000\u0081\u000f\u0001\u0000\u0000\u0000\u0082" +
                    "\u0083\u0005\t\u0000\u0000\u0083\u0084\u0005\u001e\u0000\u0000\u0084\u0085" +
                    "\u0005\u001d\u0000\u0000\u0085\u0086\u0005\u001e\u0000\u0000\u0086\u0087" +
                    "\u0006\b\uffff\uffff\u0000\u0087\u0011\u0001\u0000\u0000\u0000\u0088\u0089" +
                    "\u0005\n\u0000\u0000\u0089\u008a\u0005\u001e\u0000\u0000\u008a\u008b\u0005" +
                    "\u001d\u0000\u0000\u008b\u008c\u0005\u001e\u0000\u0000\u008c\u008d\u0006" +
                    "\t\uffff\uffff\u0000\u008d\u0013\u0001\u0000\u0000\u0000\u008e\u008f\u0005" +
                    "\u000b\u0000\u0000\u008f\u0090\u0005\u001e\u0000\u0000\u0090\u0091\u0005" +
                    "\u001d\u0000\u0000\u0091\u0092\u0005\u001e\u0000\u0000\u0092\u0093\u0006" +
                    "\n\uffff\uffff\u0000\u0093\u0015\u0001\u0000\u0000\u0000\u0094\u0095\u0005" +
                    "\f\u0000\u0000\u0095\u0096\u0005\u001e\u0000\u0000\u0096\u0097\u0005\u001d" +
                    "\u0000\u0000\u0097\u0098\u0005\u001e\u0000\u0000\u0098\u0099\u0006\u000b" +
                    "\uffff\uffff\u0000\u0099\u0017\u0001\u0000\u0000\u0000\u009a\u009b\u0005" +
                    "\r\u0000\u0000\u009b\u009c\u0005\u001e\u0000\u0000\u009c\u009d\u0005\u001d" +
                    "\u0000\u0000\u009d\u009e\u0005\u001e\u0000\u0000\u009e\u009f\u0006\f\uffff" +
                    "\uffff\u0000\u009f\u0019\u0001\u0000\u0000\u0000\u00a0\u00a1\u0005\u000e" +
                    "\u0000\u0000\u00a1\u00a2\u0005\u001e\u0000\u0000\u00a2\u00a3\u0006\r\uffff" +
                    "\uffff\u0000\u00a3\u001b\u0001\u0000\u0000\u0000\u00a4\u00a5\u0005\u0010" +
                    "\u0000\u0000\u00a5\u00a6\u0005\u001e\u0000\u0000\u00a6\u00a7\u0006\u000e" +
                    "\uffff\uffff\u0000\u00a7\u001d\u0001\u0000\u0000\u0000\u00a8\u00a9\u0005" +
                    "\u0011\u0000\u0000\u00a9\u00aa\u0005\u001e\u0000\u0000\u00aa\u00ab\u0006" +
                    "\u000f\uffff\uffff\u0000\u00ab\u001f\u0001\u0000\u0000\u0000\u00ac\u00ad" +
                    "\u0005\u0012\u0000\u0000\u00ad\u00ae\u0005\u001e\u0000\u0000\u00ae\u00af" +
                    "\u0006\u0010\uffff\uffff\u0000\u00af!\u0001\u0000\u0000\u0000\u00b0\u00b1" +
                    "\u0005\u0013\u0000\u0000\u00b1\u00b2\u00058\u0000\u0000\u00b2\u00b3\u0006" +
                    "\u0011\uffff\uffff\u0000\u00b3#\u0001\u0000\u0000\u0000\u00b4\u00b5\u0005" +
                    "\u0014\u0000\u0000\u00b5\u00b6\u00052\u0000\u0000\u00b6\u00b7\u00053\u0000" +
                    "\u0000\u00b7\u00b8\u00052\u0000\u0000\u00b8\u00b9\u00053\u0000\u0000\u00b9" +
                    "\u00ba\u00051\u0000\u0000\u00ba\u00bb\u00053\u0000\u0000\u00bb\u00bc\u0005" +
                    "1\u0000\u0000\u00bc\u00bd\u00053\u0000\u0000\u00bd\u00be\u00052\u0000" +
                    "\u0000\u00be\u00bf\u0006\u0012\uffff\uffff\u0000\u00bf%\u0001\u0000\u0000" +
                    "\u0000\u00c0\u00c1\u0005\u0015\u0000\u0000\u00c1\u00c2\u00055\u0000\u0000" +
                    "\u00c2\u00c8\u0006\u0013\uffff\uffff\u0000\u00c3\u00c4\u00056\u0000\u0000" +
                    "\u00c4\u00c5\u00055\u0000\u0000\u00c5\u00c7\u0006\u0013\uffff\uffff\u0000" +
                    "\u00c6\u00c3\u0001\u0000\u0000\u0000\u00c7\u00ca\u0001\u0000\u0000\u0000" +
                    "\u00c8\u00c6\u0001\u0000\u0000\u0000\u00c8\u00c9\u0001\u0000\u0000\u0000" +
                    "\u00c9\'\u0001\u0000\u0000\u0000\u00ca\u00c8\u0001\u0000\u0000\u0000\u00cb" +
                    "\u00cc\u0005\u0016\u0000\u0000\u00cc\u00cd\u00055\u0000\u0000\u00cd\u00d3" +
                    "\u0006\u0014\uffff\uffff\u0000\u00ce\u00cf\u00056\u0000\u0000\u00cf\u00d0" +
                    "\u00055\u0000\u0000\u00d0\u00d2\u0006\u0014\uffff\uffff\u0000\u00d1\u00ce" +
                    "\u0001\u0000\u0000\u0000\u00d2\u00d5\u0001\u0000\u0000\u0000\u00d3\u00d1" +
                    "\u0001\u0000\u0000\u0000\u00d3\u00d4\u0001\u0000\u0000\u0000\u00d4)\u0001" +
                    "\u0000\u0000\u0000\u00d5\u00d3\u0001\u0000\u0000\u0000\u00d6\u00d7\u0005" +
                    "\u000f\u0000\u0000\u00d7\u00d8\u0005\u001e\u0000\u0000\u00d8\u00d9\u0006" +
                    "\u0015\uffff\uffff\u0000\u00d9+\u0001\u0000\u0000\u0000\u00da\u00db\u0005" +
                    "\u0017\u0000\u0000\u00db\u00dc\u0005\u001e\u0000\u0000\u00dc\u00dd\u0005" +
                    "\u001d\u0000\u0000\u00dd\u00de\u0005\u001e\u0000\u0000\u00de\u00df\u0005" +
                    "\u001d\u0000\u0000\u00df\u00e0\u0005\u001e\u0000\u0000\u00e0\u00e1\u0006" +
                    "\u0016\uffff\uffff\u0000\u00e1-\u0001\u0000\u0000\u0000\u00e2\u00e3\u0003" +
                    ",\u0016\u0000\u00e3\u00e7\u0006\u0017\uffff\uffff\u0000\u00e4\u00e5\u0003" +
                    "6\u001b\u0000\u00e5\u00e6\u0006\u0017\uffff\uffff\u0000\u00e6\u00e8\u0001" +
                    "\u0000\u0000\u0000\u00e7\u00e4\u0001\u0000\u0000\u0000\u00e8\u00e9\u0001" +
                    "\u0000\u0000\u0000\u00e9\u00e7\u0001\u0000\u0000\u0000\u00e9\u00ea\u0001" +
                    "\u0000\u0000\u0000\u00ea/\u0001\u0000\u0000\u0000\u00eb\u00ec\u0005\u0018" +
                    "\u0000\u0000\u00ec\u00ed\u00052\u0000\u0000\u00ed\u00ee\u00053\u0000\u0000" +
                    "\u00ee\u00ef\u00052\u0000\u0000\u00ef\u00f0\u00053\u0000\u0000\u00f0\u00f1" +
                    "\u00052\u0000\u0000\u00f1\u00f2\u00053\u0000\u0000\u00f2\u00f3\u00051" +
                    "\u0000\u0000\u00f3\u00f4\u00053\u0000\u0000\u00f4\u00f5\u00052\u0000\u0000" +
                    "\u00f5\u00f6\u0006\u0018\uffff\uffff\u0000\u00f61\u0001\u0000\u0000\u0000" +
                    "\u00f7\u00f8\u0005\u0019\u0000\u0000\u00f8\u00f9\u0005C\u0000\u0000\u00f9" +
                    "\u00fa\u0005D\u0000\u0000\u00fa\u00fb\u0005B\u0000\u0000\u00fb\u00fc\u0006" +
                    "\u0019\uffff\uffff\u0000\u00fc3\u0001\u0000\u0000\u0000\u00fd\u00fe\u0005" +
                    "\u001a\u0000\u0000\u00fe\u00ff\u0005-\u0000\u0000\u00ff\u0100\u0005.\u0000" +
                    "\u0000\u0100\u0101\u0005-\u0000\u0000\u0101\u0102\u0005.\u0000\u0000\u0102" +
                    "\u0103\u0005-\u0000\u0000\u0103\u0104\u0005/\u0000\u0000\u0104\u0105\u0005" +
                    "-\u0000\u0000\u0105\u0106\u0006\u001a\uffff\uffff\u0000\u01065\u0001\u0000" +
                    "\u0000\u0000\u0107\u0108\u00030\u0018\u0000\u0108\u010c\u0006\u001b\uffff" +
                    "\uffff\u0000\u0109\u010a\u00032\u0019\u0000\u010a\u010b\u0006\u001b\uffff" +
                    "\uffff\u0000\u010b\u010d\u0001\u0000\u0000\u0000\u010c\u0109\u0001\u0000" +
                    "\u0000\u0000\u010c\u010d\u0001\u0000\u0000\u0000\u010d\u0111\u0001\u0000" +
                    "\u0000\u0000\u010e\u010f\u00034\u001a\u0000\u010f\u0110\u0006\u001b\uffff" +
                    "\uffff\u0000\u0110\u0112\u0001\u0000\u0000\u0000\u0111\u010e\u0001\u0000" +
                    "\u0000\u0000\u0111\u0112\u0001\u0000\u0000\u0000\u0112\u0116\u0001\u0000" +
                    "\u0000\u0000\u0113\u0114\u0003$\u0012\u0000\u0114\u0115\u0006\u001b\uffff" +
                    "\uffff\u0000\u0115\u0117\u0001\u0000\u0000\u0000\u0116\u0113\u0001\u0000" +
                    "\u0000\u0000\u0116\u0117\u0001\u0000\u0000\u0000\u0117\u011b\u0001\u0000" +
                    "\u0000\u0000\u0118\u0119\u0003<\u001e\u0000\u0119\u011a\u0006\u001b\uffff" +
                    "\uffff\u0000\u011a\u011c\u0001\u0000\u0000\u0000\u011b\u0118\u0001\u0000" +
                    "\u0000\u0000\u011c\u011d\u0001\u0000\u0000\u0000\u011d\u011b\u0001\u0000" +
                    "\u0000\u0000\u011d\u011e\u0001\u0000\u0000\u0000\u011e7\u0001\u0000\u0000" +
                    "\u0000\u011f\u0120\u0005\u001b\u0000\u0000\u0120\u0121\u00052\u0000\u0000" +
                    "\u0121\u0122\u00053\u0000\u0000\u0122\u0123\u00051\u0000\u0000\u0123\u0124" +
                    "\u00053\u0000\u0000\u0124\u0125\u00051\u0000\u0000\u0125\u0126\u00053" +
                    "\u0000\u0000\u0126\u0127\u00051\u0000\u0000\u0127\u0128\u00053\u0000\u0000" +
                    "\u0128\u0129\u00051\u0000\u0000\u0129\u012a\u0006\u001c\uffff\uffff\u0000" +
                    "\u012a9\u0001\u0000\u0000\u0000\u012b\u012c\u0005\u001c\u0000\u0000\u012c" +
                    "\u012d\u00058\u0000\u0000\u012d\u012e\u0006\u001d\uffff\uffff\u0000\u012e" +
                    ";\u0001\u0000\u0000\u0000\u012f\u0130\u00038\u001c\u0000\u0130\u0139\u0006" +
                    "\u001e\uffff\uffff\u0000\u0131\u0132\u0003N\'\u0000\u0132\u0133\u0006" +
                    "\u001e\uffff\uffff\u0000\u0133\u0138\u0001\u0000\u0000\u0000\u0134\u0135" +
                    "\u0003:\u001d\u0000\u0135\u0136\u0006\u001e\uffff\uffff\u0000\u0136\u0138" +
                    "\u0001\u0000\u0000\u0000\u0137\u0131\u0001\u0000\u0000\u0000\u0137\u0134" +
                    "\u0001\u0000\u0000\u0000\u0138\u013b\u0001\u0000\u0000\u0000\u0139\u0137" +
                    "\u0001\u0000\u0000\u0000\u0139\u013a\u0001\u0000\u0000\u0000\u013a=\u0001" +
                    "\u0000\u0000\u0000\u013b\u0139\u0001\u0000\u0000\u0000\u013c\u013d\u0003" +
                    "\u0002\u0001\u0000\u013d\u017d\u0006\u001f\uffff\uffff\u0000\u013e\u013f" +
                    "\u0003\u0004\u0002\u0000\u013f\u0140\u0006\u001f\uffff\uffff\u0000\u0140" +
                    "\u017e\u0001\u0000\u0000\u0000\u0141\u0142\u0003\u0006\u0003\u0000\u0142" +
                    "\u0143\u0006\u001f\uffff\uffff\u0000\u0143\u017e\u0001\u0000\u0000\u0000" +
                    "\u0144\u0145\u0003\b\u0004\u0000\u0145\u0146\u0006\u001f\uffff\uffff\u0000" +
                    "\u0146\u017e\u0001\u0000\u0000\u0000\u0147\u0148\u0003\f\u0006\u0000\u0148" +
                    "\u0149\u0006\u001f\uffff\uffff\u0000\u0149\u017e\u0001\u0000\u0000\u0000" +
                    "\u014a\u014b\u0003\n\u0005\u0000\u014b\u014c\u0006\u001f\uffff\uffff\u0000" +
                    "\u014c\u017e\u0001\u0000\u0000\u0000\u014d\u014e\u0003\u000e\u0007\u0000" +
                    "\u014e\u014f\u0006\u001f\uffff\uffff\u0000\u014f\u017e\u0001\u0000\u0000" +
                    "\u0000\u0150\u0151\u0003\u0010\b\u0000\u0151\u0152\u0006\u001f\uffff\uffff" +
                    "\u0000\u0152\u017e\u0001\u0000\u0000\u0000\u0153\u0154\u0003\u0012\t\u0000" +
                    "\u0154\u0155\u0006\u001f\uffff\uffff\u0000\u0155\u017e\u0001\u0000\u0000" +
                    "\u0000\u0156\u0157\u0003\u0014\n\u0000\u0157\u0158\u0006\u001f\uffff\uffff" +
                    "\u0000\u0158\u017e\u0001\u0000\u0000\u0000\u0159\u015a\u0003\u0016\u000b" +
                    "\u0000\u015a\u015b\u0006\u001f\uffff\uffff\u0000\u015b\u017e\u0001\u0000" +
                    "\u0000\u0000\u015c\u015d\u0003\u0018\f\u0000\u015d\u015e\u0006\u001f\uffff" +
                    "\uffff\u0000\u015e\u017e\u0001\u0000\u0000\u0000\u015f\u0160\u0003\u001a" +
                    "\r\u0000\u0160\u0161\u0006\u001f\uffff\uffff\u0000\u0161\u017e\u0001\u0000" +
                    "\u0000\u0000\u0162\u0163\u0003\u001c\u000e\u0000\u0163\u0164\u0006\u001f" +
                    "\uffff\uffff\u0000\u0164\u017e\u0001\u0000\u0000\u0000\u0165\u0166\u0003" +
                    "\u001e\u000f\u0000\u0166\u0167\u0006\u001f\uffff\uffff\u0000\u0167\u017e" +
                    "\u0001\u0000\u0000\u0000\u0168\u0169\u0003 \u0010\u0000\u0169\u016a\u0006" +
                    "\u001f\uffff\uffff\u0000\u016a\u017e\u0001\u0000\u0000\u0000\u016b\u016c" +
                    "\u0003*\u0015\u0000\u016c\u016d\u0006\u001f\uffff\uffff\u0000\u016d\u017e" +
                    "\u0001\u0000\u0000\u0000\u016e\u016f\u0003\"\u0011\u0000\u016f\u0170\u0006" +
                    "\u001f\uffff\uffff\u0000\u0170\u017e\u0001\u0000\u0000\u0000\u0171\u0172" +
                    "\u0003$\u0012\u0000\u0172\u0173\u0006\u001f\uffff\uffff\u0000\u0173\u017e" +
                    "\u0001\u0000\u0000\u0000\u0174\u0175\u0003&\u0013\u0000\u0175\u0176\u0006" +
                    "\u001f\uffff\uffff\u0000\u0176\u017e\u0001\u0000\u0000\u0000\u0177\u0178" +
                    "\u0003(\u0014\u0000\u0178\u0179\u0006\u001f\uffff\uffff\u0000\u0179\u017e" +
                    "\u0001\u0000\u0000\u0000\u017a\u017b\u0003.\u0017\u0000\u017b\u017c\u0006" +
                    "\u001f\uffff\uffff\u0000\u017c\u017e\u0001\u0000\u0000\u0000\u017d\u013e" +
                    "\u0001\u0000\u0000\u0000\u017d\u0141\u0001\u0000\u0000\u0000\u017d\u0144" +
                    "\u0001\u0000\u0000\u0000\u017d\u0147\u0001\u0000\u0000\u0000\u017d\u014a" +
                    "\u0001\u0000\u0000\u0000\u017d\u014d\u0001\u0000\u0000\u0000\u017d\u0150" +
                    "\u0001\u0000\u0000\u0000\u017d\u0153\u0001\u0000\u0000\u0000\u017d\u0156" +
                    "\u0001\u0000\u0000\u0000\u017d\u0159\u0001\u0000\u0000\u0000\u017d\u015c" +
                    "\u0001\u0000\u0000\u0000\u017d\u015f\u0001\u0000\u0000\u0000\u017d\u0162" +
                    "\u0001\u0000\u0000\u0000\u017d\u0165\u0001\u0000\u0000\u0000\u017d\u0168" +
                    "\u0001\u0000\u0000\u0000\u017d\u016b\u0001\u0000\u0000\u0000\u017d\u016e" +
                    "\u0001\u0000\u0000\u0000\u017d\u0171\u0001\u0000\u0000\u0000\u017d\u0174" +
                    "\u0001\u0000\u0000\u0000\u017d\u0177\u0001\u0000\u0000\u0000\u017d\u017a" +
                    "\u0001\u0000\u0000\u0000\u017e\u017f\u0001\u0000\u0000\u0000\u017f\u017d" +
                    "\u0001\u0000\u0000\u0000\u017f\u0180\u0001\u0000\u0000\u0000\u0180?\u0001" +
                    "\u0000\u0000\u0000\u0181\u0182\u0003\u0000\u0000\u0000\u0182\u0186\u0006" +
                    " \uffff\uffff\u0000\u0183\u0184\u0003>\u001f\u0000\u0184\u0185\u0006 " +
                    "\uffff\uffff\u0000\u0185\u0187\u0001\u0000\u0000\u0000\u0186\u0183\u0001" +
                    "\u0000\u0000\u0000\u0187\u0188\u0001\u0000\u0000\u0000\u0188\u0186\u0001" +
                    "\u0000\u0000\u0000\u0188\u0189\u0001\u0000\u0000\u0000\u0189\u018a\u0001" +
                    "\u0000\u0000\u0000\u018a\u018b\u0005\u0000\u0000\u0001\u018bA\u0001\u0000" +
                    "\u0000\u0000\u018c\u018d\u0005!\u0000\u0000\u018d\u018e\u0005\'\u0000" +
                    "\u0000\u018e\u019c\u0006!\uffff\uffff\u0000\u018f\u0190\u0005\u001d\u0000" +
                    "\u0000\u0190\u0191\u0005\'\u0000\u0000\u0191\u019a\u0006!\uffff\uffff" +
                    "\u0000\u0192\u0193\u0005\u001d\u0000\u0000\u0193\u0194\u0005\u001e\u0000" +
                    "\u0000\u0194\u0198\u0006!\uffff\uffff\u0000\u0195\u0196\u0005\u001d\u0000" +
                    "\u0000\u0196\u0197\u0005\u001e\u0000\u0000\u0197\u0199\u0006!\uffff\uffff" +
                    "\u0000\u0198\u0195\u0001\u0000\u0000\u0000\u0198\u0199\u0001\u0000\u0000" +
                    "\u0000\u0199\u019b\u0001\u0000\u0000\u0000\u019a\u0192\u0001\u0000\u0000" +
                    "\u0000\u019a\u019b\u0001\u0000\u0000\u0000\u019b\u019d\u0001\u0000\u0000" +
                    "\u0000\u019c\u018f\u0001\u0000\u0000\u0000\u019c\u019d\u0001\u0000\u0000" +
                    "\u0000\u019dC\u0001\u0000\u0000\u0000\u019e\u019f\u0005$\u0000\u0000\u019f" +
                    "\u01a0\u0005;\u0000\u0000\u01a0\u01a1\u0006\"\uffff\uffff\u0000\u01a1" +
                    "E\u0001\u0000\u0000\u0000\u01a2\u01a3\u0005%\u0000\u0000\u01a3\u01a4\u0005" +
                    "\u001e\u0000\u0000\u01a4\u01a5\u0005\u001d\u0000\u0000\u01a5\u01a6\u0005" +
                    "\u001e\u0000\u0000\u01a6\u01a7\u0006#\uffff\uffff\u0000\u01a7G\u0001\u0000" +
                    "\u0000\u0000\u01a8\u01b4\u0005#\u0000\u0000\u01a9\u01aa\u0005<\u0000\u0000" +
                    "\u01aa\u01ae\u0006$\uffff\uffff\u0000\u01ab\u01ac\u0003J%\u0000\u01ac" +
                    "\u01ad\u0006$\uffff\uffff\u0000\u01ad\u01af\u0001\u0000\u0000\u0000\u01ae" +
                    "\u01ab\u0001\u0000\u0000\u0000\u01af\u01b0\u0001\u0000\u0000\u0000\u01b0" +
                    "\u01ae\u0001\u0000\u0000\u0000\u01b0\u01b1\u0001\u0000\u0000\u0000\u01b1" +
                    "\u01b5\u0001\u0000\u0000\u0000\u01b2\u01b3\u0005;\u0000\u0000\u01b3\u01b5" +
                    "\u0006$\uffff\uffff\u0000\u01b4\u01a9\u0001\u0000\u0000\u0000\u01b4\u01b2" +
                    "\u0001\u0000\u0000\u0000\u01b5I\u0001\u0000\u0000\u0000\u01b6\u01b7\u0005" +
                    "&\u0000\u0000\u01b7\u01b8\u0005=\u0000\u0000\u01b8\u01b9\u0005>\u0000" +
                    "\u0000\u01b9\u01ba\u0005?\u0000\u0000\u01ba\u01bb\u0005>\u0000\u0000\u01bb" +
                    "\u01bc\u0005@\u0000\u0000\u01bc\u01bd\u0006%\uffff\uffff\u0000\u01bdK" +
                    "\u0001\u0000\u0000\u0000\u01be\u01bf\u0005\"\u0000\u0000\u01bf\u01c0\u0005" +
                    ":\u0000\u0000\u01c0\u01c1\u0006&\uffff\uffff\u0000\u01c1M\u0001\u0000" +
                    "\u0000\u0000\u01c2\u01c3\u0003B!\u0000\u01c3\u01cc\u0006\'\uffff\uffff" +
                    "\u0000\u01c4\u01c5\u0003F#\u0000\u01c5\u01c6\u0006\'\uffff\uffff\u0000" +
                    "\u01c6\u01cd\u0001\u0000\u0000\u0000\u01c7\u01c8\u0003H$\u0000\u01c8\u01c9" +
                    "\u0006\'\uffff\uffff\u0000\u01c9\u01cb\u0001\u0000\u0000\u0000\u01ca\u01c7" +
                    "\u0001\u0000\u0000\u0000\u01ca\u01cb\u0001\u0000\u0000\u0000\u01cb\u01cd" +
                    "\u0001\u0000\u0000\u0000\u01cc\u01c4\u0001\u0000\u0000\u0000\u01cc\u01ca" +
                    "\u0001\u0000\u0000\u0000\u01cd\u01d1\u0001\u0000\u0000\u0000\u01ce\u01cf" +
                    "\u0003D\"\u0000\u01cf\u01d0\u0006\'\uffff\uffff\u0000\u01d0\u01d2\u0001" +
                    "\u0000\u0000\u0000\u01d1\u01ce\u0001\u0000\u0000\u0000\u01d1\u01d2\u0001" +
                    "\u0000\u0000\u0000\u01d2\u01d6\u0001\u0000\u0000\u0000\u01d3\u01d4\u0003" +
                    "L&\u0000\u01d4\u01d5\u0006\'\uffff\uffff\u0000\u01d5\u01d7\u0001\u0000" +
                    "\u0000\u0000\u01d6\u01d3\u0001\u0000\u0000\u0000\u01d6\u01d7\u0001\u0000" +
                    "\u0000\u0000\u01d7O\u0001\u0000\u0000\u0000\u0015\u00c8\u00d3\u00e9\u010c" +
                    "\u0111\u0116\u011d\u0137\u0139\u017d\u017f\u0188\u0198\u019a\u019c\u01b0" +
                    "\u01b4\u01ca\u01cc\u01d1\u01d6";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}