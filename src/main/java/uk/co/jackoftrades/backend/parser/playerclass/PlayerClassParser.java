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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/PlayerClass.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.playerclass;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
import uk.co.jackoftrades.middle.effect.*;
import uk.co.jackoftrades.middle.enums.*;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.magic.ClassMagic;
import uk.co.jackoftrades.middle.magic.MagicBook;
import uk.co.jackoftrades.middle.magic.MagicRealm;
import uk.co.jackoftrades.middle.magic.MagicSpell;
import uk.co.jackoftrades.middle.objects.ObjectKind;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.TValue;
import uk.co.jackoftrades.middle.player.PlayerClass;
import uk.co.jackoftrades.middle.player.StartItem;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class PlayerClassParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, NAME = 3, STATS = 4, SKILL_DISARM_PHYS = 5, SKILL_DISARM_MAGIC = 6,
            SKILL_DEVICE = 7, SKILL_SAVE = 8, SKILL_STEALTH = 9, SKILL_SEARCH = 10, SKILL_MELEE = 11,
            SKILL_SHOOT = 12, SKILL_THROW = 13, SKILL_DIG = 14, HITDIE = 15, EXP = 16, MAX_ATTACKS = 17,
            MIN_WEIGHT = 18, STRENGTH_MULTIPLIER = 19, TITLE = 20, EQUIP = 21, OBJ_FLAGS = 22,
            PLAYER_FLAGS = 23, MAGIC = 24, BOOK = 25, BOOK_GRAPHICS = 26, BOOK_PROPERTIES = 27,
            SPELL = 28, EFFECT = 29, EFFECT_YX = 30, EXPR = 31, EFFECT_MSG = 32, DESC = 33, COLON = 34,
            MINUS = 35, SPACE = 36, OR = 37, COLOUR_CHAR = 38, EXPR_LETTER = 39, INTEGER = 40,
            UPPER_WORD = 41, LOWER_WORD = 42, UPPER_LOWER_STRING = 43, DESC_STRING = 44, BRACKETED_STRING = 45,
            GRAPHICS_CHAR = 46, RANGE = 47, DOLLAR = 48, PLUS = 49, DICE = 50, EXPR_OPERATORS = 51,
            SPELL_STRING = 52;
    public static final int
            RULE_name = 0, RULE_stats = 1, RULE_skillDisarmPhys = 2, RULE_skillDisarmMagic = 3,
            RULE_skillDevice = 4, RULE_skillSave = 5, RULE_skillStealth = 6, RULE_skillSearch = 7,
            RULE_skillMelee = 8, RULE_skillShoot = 9, RULE_skillThrow = 10, RULE_skillDig = 11,
            RULE_hitdie = 12, RULE_maxAttacks = 13, RULE_minWeight = 14, RULE_strengthMultiplier = 15,
            RULE_equip = 16, RULE_equipBlock = 17, RULE_objectFlags = 18, RULE_playerFlags = 19,
            RULE_title = 20, RULE_titleBlock = 21, RULE_magic = 22, RULE_book = 23,
            RULE_bookGraphics = 24, RULE_bookProperties = 25, RULE_spell = 26, RULE_effect = 27,
            RULE_effectMsg = 28, RULE_effectYX = 29, RULE_dice = 30, RULE_effectBlock = 31,
            RULE_expr = 32, RULE_desc = 33, RULE_spellBlock = 34, RULE_bookBlock = 35,
            RULE_playerClass = 36, RULE_file = 37;

    private static String[] makeRuleNames() {
        return new String[]{
                "name", "stats", "skillDisarmPhys", "skillDisarmMagic", "skillDevice",
                "skillSave", "skillStealth", "skillSearch", "skillMelee", "skillShoot",
                "skillThrow", "skillDig", "hitdie", "maxAttacks", "minWeight", "strengthMultiplier",
                "equip", "equipBlock", "objectFlags", "playerFlags", "title", "titleBlock",
                "magic", "book", "bookGraphics", "bookProperties", "spell", "effect",
                "effectMsg", "effectYX", "dice", "effectBlock", "expr", "desc", "spellBlock",
                "bookBlock", "playerClass", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'name:'", "'stats:'", "'skill-disarm-phys:'", "'skill-disarm-magic:'",
                "'skill-device:'", "'skill-save:'", "'skill-stealth:'", "'skill-search:'",
                "'skill-melee:'", "'skill-shoot:'", "'skill-throw:'", "'skill-dig:'",
                "'hitdie:'", "'exp:'", "'max-attacks:'", "'min-weight:'", "'strength-multiplier:'",
                "'title:'", "'equip:'", "'obj-flags:'", "'player-flags:'", "'magic:'",
                "'book:'", "'book-graphics:'", "'book-properties:'", "'spell:'", "'effect:'",
                "'effect-yx:'", "'expr:'", "'effect-msg:'", "'desc:'", "':'", "'-'",
                "' '", "' | '", null, null, null, null, null, null, null, null, null,
                null, "'$'", "'+'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "NAME", "STATS", "SKILL_DISARM_PHYS", "SKILL_DISARM_MAGIC",
                "SKILL_DEVICE", "SKILL_SAVE", "SKILL_STEALTH", "SKILL_SEARCH", "SKILL_MELEE",
                "SKILL_SHOOT", "SKILL_THROW", "SKILL_DIG", "HITDIE", "EXP", "MAX_ATTACKS",
                "MIN_WEIGHT", "STRENGTH_MULTIPLIER", "TITLE", "EQUIP", "OBJ_FLAGS", "PLAYER_FLAGS",
                "MAGIC", "BOOK", "BOOK_GRAPHICS", "BOOK_PROPERTIES", "SPELL", "EFFECT",
                "EFFECT_YX", "EXPR", "EFFECT_MSG", "DESC", "COLON", "MINUS", "SPACE",
                "OR", "COLOUR_CHAR", "EXPR_LETTER", "INTEGER", "UPPER_WORD", "LOWER_WORD",
                "UPPER_LOWER_STRING", "DESC_STRING", "BRACKETED_STRING", "GRAPHICS_CHAR",
                "RANGE", "DOLLAR", "PLUS", "DICE", "EXPR_OPERATORS", "SPELL_STRING"
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
        return "PlayerClass.g4";
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

    public PlayerClassParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class NameContext extends ParserRuleContext {
        public String nameStr;
        public Token string;

        public TerminalNode NAME() {
            return getToken(PlayerClassParser.NAME, 0);
        }

        public TerminalNode UPPER_LOWER_STRING() {
            return getToken(PlayerClassParser.UPPER_LOWER_STRING, 0);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(76);
                match(NAME);
                setState(77);
                ((NameContext) _localctx).string = match(UPPER_LOWER_STRING);
                ((NameContext) _localctx).nameStr = ((NameContext) _localctx).string.getText();
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
        public Map<Stats, Integer> stat;
        public Token s;
        public Token i;
        public Token w;
        public Token d;
        public Token c;

        public TerminalNode STATS() {
            return getToken(PlayerClassParser.STATS, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(PlayerClassParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(PlayerClassParser.COLON, i);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassParser.INTEGER, i);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterStats(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitStats(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitStats(this);
            else return visitor.visitChildren(this);
        }
    }

    public final StatsContext stats() throws RecognitionException {
        StatsContext _localctx = new StatsContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_stats);

        ((StatsContext) _localctx).stat = new HashMap<>();

        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(80);
                match(STATS);
                setState(81);
                ((StatsContext) _localctx).s = match(INTEGER);
                setState(82);
                match(COLON);
                setState(83);
                ((StatsContext) _localctx).i = match(INTEGER);
                setState(84);
                match(COLON);
                setState(85);
                ((StatsContext) _localctx).w = match(INTEGER);
                setState(86);
                match(COLON);
                setState(87);
                ((StatsContext) _localctx).d = match(INTEGER);
                setState(88);
                match(COLON);
                setState(89);
                ((StatsContext) _localctx).c = match(INTEGER);

                _localctx.stat.put(Stats.STAT_STR, Integer.parseInt(((StatsContext) _localctx).s.getText()));
                _localctx.stat.put(Stats.STAT_INT, Integer.parseInt(((StatsContext) _localctx).i.getText()));
                _localctx.stat.put(Stats.STAT_WIS, Integer.parseInt(((StatsContext) _localctx).w.getText()));
                _localctx.stat.put(Stats.STAT_DEX, Integer.parseInt(((StatsContext) _localctx).d.getText()));
                _localctx.stat.put(Stats.STAT_CON, Integer.parseInt(((StatsContext) _localctx).c.getText()));

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
        public int base;
        public int increment;
        public Token b;
        public Token inc;

        public TerminalNode SKILL_DISARM_PHYS() {
            return getToken(PlayerClassParser.SKILL_DISARM_PHYS, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerClassParser.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassParser.INTEGER, i);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterSkillDisarmPhys(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitSkillDisarmPhys(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitSkillDisarmPhys(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillDisarmPhysContext skillDisarmPhys() throws RecognitionException {
        SkillDisarmPhysContext _localctx = new SkillDisarmPhysContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_skillDisarmPhys);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(92);
                match(SKILL_DISARM_PHYS);
                setState(93);
                ((SkillDisarmPhysContext) _localctx).b = match(INTEGER);
                setState(94);
                match(COLON);
                setState(95);
                ((SkillDisarmPhysContext) _localctx).inc = match(INTEGER);

                ((SkillDisarmPhysContext) _localctx).base = Integer.parseInt(((SkillDisarmPhysContext) _localctx).b.getText());
                ((SkillDisarmPhysContext) _localctx).increment = Integer.parseInt(((SkillDisarmPhysContext) _localctx).inc.getText());

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
        public int base;
        public int increment;
        public Token b;
        public Token inc;

        public TerminalNode SKILL_DISARM_MAGIC() {
            return getToken(PlayerClassParser.SKILL_DISARM_MAGIC, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerClassParser.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassParser.INTEGER, i);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterSkillDisarmMagic(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitSkillDisarmMagic(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitSkillDisarmMagic(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillDisarmMagicContext skillDisarmMagic() throws RecognitionException {
        SkillDisarmMagicContext _localctx = new SkillDisarmMagicContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_skillDisarmMagic);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(98);
                match(SKILL_DISARM_MAGIC);
                setState(99);
                ((SkillDisarmMagicContext) _localctx).b = match(INTEGER);
                setState(100);
                match(COLON);
                setState(101);
                ((SkillDisarmMagicContext) _localctx).inc = match(INTEGER);

                ((SkillDisarmMagicContext) _localctx).base = Integer.parseInt(((SkillDisarmMagicContext) _localctx).b.getText());
                ((SkillDisarmMagicContext) _localctx).increment = Integer.parseInt(((SkillDisarmMagicContext) _localctx).inc.getText());

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
        public int base;
        public int increment;
        public Token b;
        public Token inc;

        public TerminalNode SKILL_DEVICE() {
            return getToken(PlayerClassParser.SKILL_DEVICE, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerClassParser.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassParser.INTEGER, i);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterSkillDevice(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitSkillDevice(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitSkillDevice(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillDeviceContext skillDevice() throws RecognitionException {
        SkillDeviceContext _localctx = new SkillDeviceContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_skillDevice);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(104);
                match(SKILL_DEVICE);
                setState(105);
                ((SkillDeviceContext) _localctx).b = match(INTEGER);
                setState(106);
                match(COLON);
                setState(107);
                ((SkillDeviceContext) _localctx).inc = match(INTEGER);

                ((SkillDeviceContext) _localctx).base = Integer.parseInt(((SkillDeviceContext) _localctx).b.getText());
                ((SkillDeviceContext) _localctx).increment = Integer.parseInt(((SkillDeviceContext) _localctx).inc.getText());

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
        public int base;
        public int increment;
        public Token b;
        public Token inc;

        public TerminalNode SKILL_SAVE() {
            return getToken(PlayerClassParser.SKILL_SAVE, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerClassParser.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassParser.INTEGER, i);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterSkillSave(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitSkillSave(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitSkillSave(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillSaveContext skillSave() throws RecognitionException {
        SkillSaveContext _localctx = new SkillSaveContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_skillSave);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(110);
                match(SKILL_SAVE);
                setState(111);
                ((SkillSaveContext) _localctx).b = match(INTEGER);
                setState(112);
                match(COLON);
                setState(113);
                ((SkillSaveContext) _localctx).inc = match(INTEGER);

                ((SkillSaveContext) _localctx).base = Integer.parseInt(((SkillSaveContext) _localctx).b.getText());
                ((SkillSaveContext) _localctx).increment = Integer.parseInt(((SkillSaveContext) _localctx).inc.getText());

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
        public int base;
        public int increment;
        public Token b;
        public Token inc;

        public TerminalNode SKILL_STEALTH() {
            return getToken(PlayerClassParser.SKILL_STEALTH, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerClassParser.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassParser.INTEGER, i);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterSkillStealth(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitSkillStealth(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitSkillStealth(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillStealthContext skillStealth() throws RecognitionException {
        SkillStealthContext _localctx = new SkillStealthContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_skillStealth);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(116);
                match(SKILL_STEALTH);
                setState(117);
                ((SkillStealthContext) _localctx).b = match(INTEGER);
                setState(118);
                match(COLON);
                setState(119);
                ((SkillStealthContext) _localctx).inc = match(INTEGER);

                ((SkillStealthContext) _localctx).base = Integer.parseInt(((SkillStealthContext) _localctx).b.getText());
                ((SkillStealthContext) _localctx).increment = Integer.parseInt(((SkillStealthContext) _localctx).inc.getText());

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
        public int base;
        public int increment;
        public Token b;
        public Token inc;

        public TerminalNode SKILL_SEARCH() {
            return getToken(PlayerClassParser.SKILL_SEARCH, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerClassParser.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassParser.INTEGER, i);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterSkillSearch(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitSkillSearch(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitSkillSearch(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillSearchContext skillSearch() throws RecognitionException {
        SkillSearchContext _localctx = new SkillSearchContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_skillSearch);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(122);
                match(SKILL_SEARCH);
                setState(123);
                ((SkillSearchContext) _localctx).b = match(INTEGER);
                setState(124);
                match(COLON);
                setState(125);
                ((SkillSearchContext) _localctx).inc = match(INTEGER);

                ((SkillSearchContext) _localctx).base = Integer.parseInt(((SkillSearchContext) _localctx).b.getText());
                ((SkillSearchContext) _localctx).increment = Integer.parseInt(((SkillSearchContext) _localctx).inc.getText());

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
        public int base;
        public int increment;
        public Token b;
        public Token inc;

        public TerminalNode SKILL_MELEE() {
            return getToken(PlayerClassParser.SKILL_MELEE, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerClassParser.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassParser.INTEGER, i);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterSkillMelee(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitSkillMelee(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitSkillMelee(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillMeleeContext skillMelee() throws RecognitionException {
        SkillMeleeContext _localctx = new SkillMeleeContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_skillMelee);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(128);
                match(SKILL_MELEE);
                setState(129);
                ((SkillMeleeContext) _localctx).b = match(INTEGER);
                setState(130);
                match(COLON);
                setState(131);
                ((SkillMeleeContext) _localctx).inc = match(INTEGER);

                ((SkillMeleeContext) _localctx).base = Integer.parseInt(((SkillMeleeContext) _localctx).b.getText());
                ((SkillMeleeContext) _localctx).increment = Integer.parseInt(((SkillMeleeContext) _localctx).inc.getText());

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
        public int base;
        public int increment;
        public Token b;
        public Token inc;

        public TerminalNode SKILL_SHOOT() {
            return getToken(PlayerClassParser.SKILL_SHOOT, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerClassParser.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassParser.INTEGER, i);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterSkillShoot(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitSkillShoot(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitSkillShoot(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillShootContext skillShoot() throws RecognitionException {
        SkillShootContext _localctx = new SkillShootContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_skillShoot);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(134);
                match(SKILL_SHOOT);
                setState(135);
                ((SkillShootContext) _localctx).b = match(INTEGER);
                setState(136);
                match(COLON);
                setState(137);
                ((SkillShootContext) _localctx).inc = match(INTEGER);

                ((SkillShootContext) _localctx).base = Integer.parseInt(((SkillShootContext) _localctx).b.getText());
                ((SkillShootContext) _localctx).increment = Integer.parseInt(((SkillShootContext) _localctx).inc.getText());

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
        public int base;
        public int increment;
        public Token b;
        public Token inc;

        public TerminalNode SKILL_THROW() {
            return getToken(PlayerClassParser.SKILL_THROW, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerClassParser.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassParser.INTEGER, i);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterSkillThrow(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitSkillThrow(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitSkillThrow(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillThrowContext skillThrow() throws RecognitionException {
        SkillThrowContext _localctx = new SkillThrowContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_skillThrow);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(140);
                match(SKILL_THROW);
                setState(141);
                ((SkillThrowContext) _localctx).b = match(INTEGER);
                setState(142);
                match(COLON);
                setState(143);
                ((SkillThrowContext) _localctx).inc = match(INTEGER);

                ((SkillThrowContext) _localctx).base = Integer.parseInt(((SkillThrowContext) _localctx).b.getText());
                ((SkillThrowContext) _localctx).increment = Integer.parseInt(((SkillThrowContext) _localctx).inc.getText());

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
        public int base;
        public int increment;
        public Token b;
        public Token inc;

        public TerminalNode SKILL_DIG() {
            return getToken(PlayerClassParser.SKILL_DIG, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerClassParser.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassParser.INTEGER, i);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterSkillDig(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitSkillDig(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitSkillDig(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillDigContext skillDig() throws RecognitionException {
        SkillDigContext _localctx = new SkillDigContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_skillDig);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(146);
                match(SKILL_DIG);
                setState(147);
                ((SkillDigContext) _localctx).b = match(INTEGER);
                setState(148);
                match(COLON);
                setState(149);
                ((SkillDigContext) _localctx).inc = match(INTEGER);

                ((SkillDigContext) _localctx).base = Integer.parseInt(((SkillDigContext) _localctx).b.getText());
                ((SkillDigContext) _localctx).increment = Integer.parseInt(((SkillDigContext) _localctx).inc.getText());

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
        public int increment;
        public Token INTEGER;

        public TerminalNode HITDIE() {
            return getToken(PlayerClassParser.HITDIE, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerClassParser.INTEGER, 0);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterHitdie(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitHitdie(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitHitdie(this);
            else return visitor.visitChildren(this);
        }
    }

    public final HitdieContext hitdie() throws RecognitionException {
        HitdieContext _localctx = new HitdieContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_hitdie);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(152);
                match(HITDIE);
                setState(153);
                ((HitdieContext) _localctx).INTEGER = match(INTEGER);
                ((HitdieContext) _localctx).increment = Integer.parseInt(((HitdieContext) _localctx).INTEGER.getText());
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
        public int count;
        public Token INTEGER;

        public TerminalNode MAX_ATTACKS() {
            return getToken(PlayerClassParser.MAX_ATTACKS, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerClassParser.INTEGER, 0);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterMaxAttacks(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitMaxAttacks(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitMaxAttacks(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MaxAttacksContext maxAttacks() throws RecognitionException {
        MaxAttacksContext _localctx = new MaxAttacksContext(_ctx, getState());
        enterRule(_localctx, 26, RULE_maxAttacks);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(156);
                match(MAX_ATTACKS);
                setState(157);
                ((MaxAttacksContext) _localctx).INTEGER = match(INTEGER);
                ((MaxAttacksContext) _localctx).count = Integer.parseInt(((MaxAttacksContext) _localctx).INTEGER.getText());
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
        public int weight;
        public Token INTEGER;

        public TerminalNode MIN_WEIGHT() {
            return getToken(PlayerClassParser.MIN_WEIGHT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerClassParser.INTEGER, 0);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterMinWeight(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitMinWeight(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitMinWeight(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MinWeightContext minWeight() throws RecognitionException {
        MinWeightContext _localctx = new MinWeightContext(_ctx, getState());
        enterRule(_localctx, 28, RULE_minWeight);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(160);
                match(MIN_WEIGHT);
                setState(161);
                ((MinWeightContext) _localctx).INTEGER = match(INTEGER);
                ((MinWeightContext) _localctx).weight = Integer.parseInt(((MinWeightContext) _localctx).INTEGER.getText());
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
        public int factor;
        public Token INTEGER;

        public TerminalNode STRENGTH_MULTIPLIER() {
            return getToken(PlayerClassParser.STRENGTH_MULTIPLIER, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerClassParser.INTEGER, 0);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterStrengthMultiplier(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitStrengthMultiplier(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitStrengthMultiplier(this);
            else return visitor.visitChildren(this);
        }
    }

    public final StrengthMultiplierContext strengthMultiplier() throws RecognitionException {
        StrengthMultiplierContext _localctx = new StrengthMultiplierContext(_ctx, getState());
        enterRule(_localctx, 30, RULE_strengthMultiplier);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(164);
                match(STRENGTH_MULTIPLIER);
                setState(165);
                ((StrengthMultiplierContext) _localctx).INTEGER = match(INTEGER);
                ((StrengthMultiplierContext) _localctx).factor = Integer.parseInt(((StrengthMultiplierContext) _localctx).INTEGER.getText());
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
        public StartItem startItem;
        public Token tv;
        public Token svs;
        public Token bs;
        public Token mn;
        public Token mx;
        public Token eopts;

        public TerminalNode EQUIP() {
            return getToken(PlayerClassParser.EQUIP, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(PlayerClassParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(PlayerClassParser.COLON, i);
        }

        public List<TerminalNode> LOWER_WORD() {
            return getTokens(PlayerClassParser.LOWER_WORD);
        }

        public TerminalNode LOWER_WORD(int i) {
            return getToken(PlayerClassParser.LOWER_WORD, i);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassParser.INTEGER, i);
        }

        public TerminalNode UPPER_LOWER_STRING() {
            return getToken(PlayerClassParser.UPPER_LOWER_STRING, 0);
        }

        public TerminalNode BRACKETED_STRING() {
            return getToken(PlayerClassParser.BRACKETED_STRING, 0);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterEquip(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitEquip(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitEquip(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EquipContext equip() throws RecognitionException {
        EquipContext _localctx = new EquipContext(_ctx, getState());
        enterRule(_localctx, 32, RULE_equip);

        TValue tval = TValue.TV_NONE;
        String sVal = "";
        int min = 0;
        int max = 0;
        String eopt = "";

        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(168);
                match(EQUIP);
                setState(169);
                ((EquipContext) _localctx).tv = match(LOWER_WORD);
                setState(170);
                match(COLON);
                setState(173);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case UPPER_LOWER_STRING: {
                        setState(171);
                        ((EquipContext) _localctx).svs = match(UPPER_LOWER_STRING);
                    }
                    break;
                    case BRACKETED_STRING: {
                        setState(172);
                        ((EquipContext) _localctx).bs = match(BRACKETED_STRING);
                    }
                    break;
                    default:
                        throw new NoViableAltException(this);
                }
                setState(175);
                match(COLON);
                setState(176);
                ((EquipContext) _localctx).mn = match(INTEGER);
                setState(177);
                match(COLON);
                setState(178);
                ((EquipContext) _localctx).mx = match(INTEGER);
                setState(179);
                match(COLON);
                setState(180);
                ((EquipContext) _localctx).eopts = match(LOWER_WORD);

                String raw = "TV_" + ((EquipContext) _localctx).tv.getText().toUpperCase().replace(' ', '_');
                tval = TValue.fromName(raw);
                if (((EquipContext) _localctx).bs == null)
                    sVal = ((EquipContext) _localctx).svs.getText();
                else
                    sVal = ((EquipContext) _localctx).bs.getText();
                min = Integer.parseInt(((EquipContext) _localctx).mn.getText());
                max = Integer.parseInt(((EquipContext) _localctx).mx.getText());
                eopt = ((EquipContext) _localctx).eopts.getText();

            }
            _ctx.stop = _input.LT(-1);

            ((EquipContext) _localctx).startItem = new StartItem(tval, sVal, min, max, eopt);

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
    public static class EquipBlockContext extends ParserRuleContext {
        public List<StartItem> startItems;
        public EquipContext equip;

        public List<EquipContext> equip() {
            return getRuleContexts(EquipContext.class);
        }

        public EquipContext equip(int i) {
            return getRuleContext(EquipContext.class, i);
        }

        public EquipBlockContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_equipBlock;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterEquipBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitEquipBlock(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitEquipBlock(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EquipBlockContext equipBlock() throws RecognitionException {
        EquipBlockContext _localctx = new EquipBlockContext(_ctx, getState());
        enterRule(_localctx, 34, RULE_equipBlock);

        ((EquipBlockContext) _localctx).startItems = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(186);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(183);
                            ((EquipBlockContext) _localctx).equip = equip();
                            _localctx.startItems.add(((EquipBlockContext) _localctx).equip.startItem);
                        }
                    }
                    setState(188);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == EQUIP);
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
    public static class ObjectFlagsContext extends ParserRuleContext {
        public Flag<ObjectFlag> oFlags;
        public Token flag1;
        public Token flag2;

        public TerminalNode OBJ_FLAGS() {
            return getToken(PlayerClassParser.OBJ_FLAGS, 0);
        }

        public List<TerminalNode> UPPER_WORD() {
            return getTokens(PlayerClassParser.UPPER_WORD);
        }

        public TerminalNode UPPER_WORD(int i) {
            return getToken(PlayerClassParser.UPPER_WORD, i);
        }

        public List<TerminalNode> OR() {
            return getTokens(PlayerClassParser.OR);
        }

        public TerminalNode OR(int i) {
            return getToken(PlayerClassParser.OR, i);
        }

        public ObjectFlagsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_objectFlags;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterObjectFlags(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitObjectFlags(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitObjectFlags(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ObjectFlagsContext objectFlags() throws RecognitionException {
        ObjectFlagsContext _localctx = new ObjectFlagsContext(_ctx, getState());
        enterRule(_localctx, 36, RULE_objectFlags);

        ((ObjectFlagsContext) _localctx).oFlags = new Flag<>(ObjectFlag.class);

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(190);
                match(OBJ_FLAGS);
                setState(191);
                ((ObjectFlagsContext) _localctx).flag1 = match(UPPER_WORD);

                String raw1 = "OF_" + ((ObjectFlagsContext) _localctx).flag1.getText().trim();
                _localctx.oFlags.on(ObjectFlag.valueOf(raw1));

                setState(198);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == OR) {
                    {
                        {
                            setState(193);
                            match(OR);
                            setState(194);
                            ((ObjectFlagsContext) _localctx).flag2 = match(UPPER_WORD);

                            String raw2 = "OF_" + ((ObjectFlagsContext) _localctx).flag2.getText().trim();
                            _localctx.oFlags.on(ObjectFlag.valueOf(raw2));

                        }
                    }
                    setState(200);
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
        public Flag<PlayerFlag> pFlags;
        public Token flag1;
        public Token flag2;

        public TerminalNode PLAYER_FLAGS() {
            return getToken(PlayerClassParser.PLAYER_FLAGS, 0);
        }

        public List<TerminalNode> UPPER_WORD() {
            return getTokens(PlayerClassParser.UPPER_WORD);
        }

        public TerminalNode UPPER_WORD(int i) {
            return getToken(PlayerClassParser.UPPER_WORD, i);
        }

        public List<TerminalNode> OR() {
            return getTokens(PlayerClassParser.OR);
        }

        public TerminalNode OR(int i) {
            return getToken(PlayerClassParser.OR, i);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterPlayerFlags(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitPlayerFlags(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitPlayerFlags(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PlayerFlagsContext playerFlags() throws RecognitionException {
        PlayerFlagsContext _localctx = new PlayerFlagsContext(_ctx, getState());
        enterRule(_localctx, 38, RULE_playerFlags);

        ((PlayerFlagsContext) _localctx).pFlags = new Flag<>(PlayerFlag.class);

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(201);
                match(PLAYER_FLAGS);
                setState(202);
                ((PlayerFlagsContext) _localctx).flag1 = match(UPPER_WORD);

                String raw1 = "PF_" + ((PlayerFlagsContext) _localctx).flag1.getText().trim();
                _localctx.pFlags.on(PlayerFlag.valueOf(raw1));

                setState(209);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == OR) {
                    {
                        {
                            setState(204);
                            match(OR);
                            setState(205);
                            ((PlayerFlagsContext) _localctx).flag2 = match(UPPER_WORD);

                            String raw2 = "PF_" + ((PlayerFlagsContext) _localctx).flag2.getText().trim();
                            _localctx.pFlags.on(PlayerFlag.valueOf(raw2));


                        }
                    }
                    setState(211);
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
    public static class TitleContext extends ParserRuleContext {
        public String titleStr;
        public Token UPPER_LOWER_STRING;

        public TerminalNode TITLE() {
            return getToken(PlayerClassParser.TITLE, 0);
        }

        public TerminalNode UPPER_LOWER_STRING() {
            return getToken(PlayerClassParser.UPPER_LOWER_STRING, 0);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterTitle(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitTitle(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitTitle(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TitleContext title() throws RecognitionException {
        TitleContext _localctx = new TitleContext(_ctx, getState());
        enterRule(_localctx, 40, RULE_title);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(212);
                match(TITLE);
                setState(213);
                ((TitleContext) _localctx).UPPER_LOWER_STRING = match(UPPER_LOWER_STRING);
                ((TitleContext) _localctx).titleStr = ((TitleContext) _localctx).UPPER_LOWER_STRING.getText();
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
    public static class TitleBlockContext extends ParserRuleContext {
        public List<String> titles;
        public TitleContext t1;
        public TitleContext t2;
        public TitleContext t3;
        public TitleContext t4;
        public TitleContext t5;
        public TitleContext t6;
        public TitleContext t7;
        public TitleContext t8;
        public TitleContext t9;
        public TitleContext t10;

        public List<TitleContext> title() {
            return getRuleContexts(TitleContext.class);
        }

        public TitleContext title(int i) {
            return getRuleContext(TitleContext.class, i);
        }

        public TitleBlockContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_titleBlock;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterTitleBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitTitleBlock(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitTitleBlock(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TitleBlockContext titleBlock() throws RecognitionException {
        TitleBlockContext _localctx = new TitleBlockContext(_ctx, getState());
        enterRule(_localctx, 42, RULE_titleBlock);

        ((TitleBlockContext) _localctx).titles = new ArrayList<>();

        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(216);
                ((TitleBlockContext) _localctx).t1 = title();
                setState(217);
                ((TitleBlockContext) _localctx).t2 = title();
                setState(218);
                ((TitleBlockContext) _localctx).t3 = title();
                setState(219);
                ((TitleBlockContext) _localctx).t4 = title();
                setState(220);
                ((TitleBlockContext) _localctx).t5 = title();
                setState(221);
                ((TitleBlockContext) _localctx).t6 = title();
                setState(222);
                ((TitleBlockContext) _localctx).t7 = title();
                setState(223);
                ((TitleBlockContext) _localctx).t8 = title();
                setState(224);
                ((TitleBlockContext) _localctx).t9 = title();
                setState(225);
                ((TitleBlockContext) _localctx).t10 = title();

                _localctx.titles.add(((TitleBlockContext) _localctx).t1.titleStr);
                _localctx.titles.add(((TitleBlockContext) _localctx).t2.titleStr);
                _localctx.titles.add(((TitleBlockContext) _localctx).t3.titleStr);
                _localctx.titles.add(((TitleBlockContext) _localctx).t4.titleStr);
                _localctx.titles.add(((TitleBlockContext) _localctx).t5.titleStr);
                _localctx.titles.add(((TitleBlockContext) _localctx).t6.titleStr);
                _localctx.titles.add(((TitleBlockContext) _localctx).t7.titleStr);
                _localctx.titles.add(((TitleBlockContext) _localctx).t8.titleStr);
                _localctx.titles.add(((TitleBlockContext) _localctx).t9.titleStr);
                _localctx.titles.add(((TitleBlockContext) _localctx).t10.titleStr);

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
        public ClassMagic classMagic;
        public Token first;
        public Token hWeight;
        public Token books;

        public TerminalNode MAGIC() {
            return getToken(PlayerClassParser.MAGIC, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(PlayerClassParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(PlayerClassParser.COLON, i);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassParser.INTEGER, i);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterMagic(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitMagic(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitMagic(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MagicContext magic() throws RecognitionException {
        MagicContext _localctx = new MagicContext(_ctx, getState());
        enterRule(_localctx, 44, RULE_magic);

        int firstSpellLevel = 0;
        int weight = 0;
        int numOfBooks = 0;

        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(228);
                match(MAGIC);
                setState(229);
                ((MagicContext) _localctx).first = match(INTEGER);
                setState(230);
                match(COLON);
                setState(231);
                ((MagicContext) _localctx).hWeight = match(INTEGER);
                setState(232);
                match(COLON);
                setState(233);
                ((MagicContext) _localctx).books = match(INTEGER);

                firstSpellLevel = Integer.parseInt(((MagicContext) _localctx).first.getText());
                weight = Integer.parseInt(((MagicContext) _localctx).hWeight.getText());
                numOfBooks = Integer.parseInt(((MagicContext) _localctx).books.getText());

            }
            _ctx.stop = _input.LT(-1);

            ((MagicContext) _localctx).classMagic = new ClassMagic(firstSpellLevel, weight, numOfBooks);

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
        public MagicBook magicBook;
        public Token objectBase;
        public Token dung;
        public Token bookName;
        public Token numOfSpells;
        public Token realm;

        public TerminalNode BOOK() {
            return getToken(PlayerClassParser.BOOK, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(PlayerClassParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(PlayerClassParser.COLON, i);
        }

        public List<TerminalNode> LOWER_WORD() {
            return getTokens(PlayerClassParser.LOWER_WORD);
        }

        public TerminalNode LOWER_WORD(int i) {
            return getToken(PlayerClassParser.LOWER_WORD, i);
        }

        public TerminalNode BRACKETED_STRING() {
            return getToken(PlayerClassParser.BRACKETED_STRING, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerClassParser.INTEGER, 0);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterBook(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitBook(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitBook(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BookContext book() throws RecognitionException {
        BookContext _localctx = new BookContext(_ctx, getState());
        enterRule(_localctx, 46, RULE_book);

        ObjectKind kind = null;
        boolean dungeon = false;
        String name = "";
        int numSpells = 0;
        MagicRealm magicRealm = null;

        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(236);
                match(BOOK);
                setState(237);
                ((BookContext) _localctx).objectBase = match(LOWER_WORD);
                setState(238);
                match(COLON);
                setState(239);
                ((BookContext) _localctx).dung = match(LOWER_WORD);
                setState(240);
                match(COLON);
                setState(241);
                ((BookContext) _localctx).bookName = match(BRACKETED_STRING);
                setState(242);
                match(COLON);
                setState(243);
                ((BookContext) _localctx).numOfSpells = match(INTEGER);
                setState(244);
                match(COLON);
                setState(245);
                ((BookContext) _localctx).realm = match(LOWER_WORD);

                kind = GameConstants.lookupObjectKind(((BookContext) _localctx).objectBase.getText());
                dungeon = ((BookContext) _localctx).dung.getText().equals("dungeon");
                name = ((BookContext) _localctx).bookName.getText();
                numSpells = Integer.parseInt(((BookContext) _localctx).numOfSpells.getText());
                magicRealm = GameConstants.lookupRealm(((BookContext) _localctx).realm.getText());

            }
            _ctx.stop = _input.LT(-1);

            ((BookContext) _localctx).magicBook = new MagicBook(kind, dungeon, name, numSpells, magicRealm);

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
        public AngbandDisplayCharacter adc;
        public Token charGrap;
        public Token charCol;

        public TerminalNode BOOK_GRAPHICS() {
            return getToken(PlayerClassParser.BOOK_GRAPHICS, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerClassParser.COLON, 0);
        }

        public TerminalNode GRAPHICS_CHAR() {
            return getToken(PlayerClassParser.GRAPHICS_CHAR, 0);
        }

        public TerminalNode COLOUR_CHAR() {
            return getToken(PlayerClassParser.COLOUR_CHAR, 0);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterBookGraphics(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitBookGraphics(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitBookGraphics(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BookGraphicsContext bookGraphics() throws RecognitionException {
        BookGraphicsContext _localctx = new BookGraphicsContext(_ctx, getState());
        enterRule(_localctx, 48, RULE_bookGraphics);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(248);
                match(BOOK_GRAPHICS);
                setState(249);
                ((BookGraphicsContext) _localctx).charGrap = match(GRAPHICS_CHAR);
                setState(250);
                match(COLON);
                setState(251);
                ((BookGraphicsContext) _localctx).charCol = match(COLOUR_CHAR);

                String rawChar = ((BookGraphicsContext) _localctx).charGrap.getText();
                String rawCol = ((BookGraphicsContext) _localctx).charCol.getText();

                if (rawCol.length() != 1 || rawChar.length() != 1) {
                    String message = "Invalid book graphics line, character and/or colour are not 1 character long.\n"
                            + "Line is: book-graphics:" + rawChar + ":" + rawCol;
                    throw new InvalidTokenFoundDuringParse(message);
                }

                ((BookGraphicsContext) _localctx).adc = new AngbandDisplayCharacter(rawChar.charAt(0), rawCol.charAt(0));

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
        public int cost;
        public int commonness;
        public int min;
        public int max;
        public Token c;
        public Token common;
        public Token range;

        public TerminalNode BOOK_PROPERTIES() {
            return getToken(PlayerClassParser.BOOK_PROPERTIES, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(PlayerClassParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(PlayerClassParser.COLON, i);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassParser.INTEGER, i);
        }

        public TerminalNode RANGE() {
            return getToken(PlayerClassParser.RANGE, 0);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterBookProperties(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitBookProperties(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitBookProperties(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BookPropertiesContext bookProperties() throws RecognitionException {
        BookPropertiesContext _localctx = new BookPropertiesContext(_ctx, getState());
        enterRule(_localctx, 50, RULE_bookProperties);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(254);
                match(BOOK_PROPERTIES);
                setState(255);
                ((BookPropertiesContext) _localctx).c = match(INTEGER);
                setState(256);
                match(COLON);
                setState(257);
                ((BookPropertiesContext) _localctx).common = match(INTEGER);
                setState(258);
                match(COLON);
                setState(259);
                ((BookPropertiesContext) _localctx).range = match(RANGE);

                ((BookPropertiesContext) _localctx).cost = Integer.parseInt(((BookPropertiesContext) _localctx).c.getText());
                ((BookPropertiesContext) _localctx).commonness = Integer.parseInt(((BookPropertiesContext) _localctx).common.getText());
                String rawRange = ((BookPropertiesContext) _localctx).range.getText();
                String[] ranges = rawRange.split(" ");
                ((BookPropertiesContext) _localctx).min = Integer.parseInt(ranges[0]);
                ((BookPropertiesContext) _localctx).max = Integer.parseInt(ranges[2]);

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
        public MagicSpell magicSpell;
        public Token sName;
        public Token level;
        public Token mana;
        public Token fail;
        public Token exp;

        public List<TerminalNode> COLON() {
            return getTokens(PlayerClassParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(PlayerClassParser.COLON, i);
        }

        public TerminalNode SPELL_STRING() {
            return getToken(PlayerClassParser.SPELL_STRING, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassParser.INTEGER, i);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterSpell(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitSpell(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitSpell(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SpellContext spell() throws RecognitionException {
        SpellContext _localctx = new SpellContext(_ctx, getState());
        enterRule(_localctx, 52, RULE_spell);

        String name = "";
        int lev = 0;
        int man = 0;
        int fai = 0;
        int ex = 0;

        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(262);
                ((SpellContext) _localctx).sName = match(SPELL_STRING);
                setState(263);
                match(COLON);
                setState(264);
                ((SpellContext) _localctx).level = match(INTEGER);
                setState(265);
                match(COLON);
                setState(266);
                ((SpellContext) _localctx).mana = match(INTEGER);
                setState(267);
                match(COLON);
                setState(268);
                ((SpellContext) _localctx).fail = match(INTEGER);
                setState(269);
                match(COLON);
                setState(270);
                ((SpellContext) _localctx).exp = match(INTEGER);

                name = ((SpellContext) _localctx).sName.getText().substring(6);
                lev = Integer.parseInt(((SpellContext) _localctx).level.getText());
                man = Integer.parseInt(((SpellContext) _localctx).mana.getText());
                fai = Integer.parseInt(((SpellContext) _localctx).fail.getText());
                ex = Integer.parseInt(((SpellContext) _localctx).exp.getText());

            }
            _ctx.stop = _input.LT(-1);

            ((SpellContext) _localctx).magicSpell = new MagicSpell(name, lev, man, fai, ex);

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
        public Effect effectObj;
        public Token flagA1;
        public Token flagA2;
        public Token flagAa2;
        public Token flagAb2;
        public Token flagAc2;
        public Token flagA3;
        public Token flagB3;
        public Token rad3;
        public Token flagA4;
        public Token flagB4;
        public Token rad4;
        public Token parm4;

        public TerminalNode EFFECT() {
            return getToken(PlayerClassParser.EFFECT, 0);
        }

        public List<TerminalNode> UPPER_WORD() {
            return getTokens(PlayerClassParser.UPPER_WORD);
        }

        public TerminalNode UPPER_WORD(int i) {
            return getToken(PlayerClassParser.UPPER_WORD, i);
        }

        public List<TerminalNode> COLON() {
            return getTokens(PlayerClassParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(PlayerClassParser.COLON, i);
        }

        public TerminalNode UPPER_LOWER_STRING() {
            return getToken(PlayerClassParser.UPPER_LOWER_STRING, 0);
        }

        public TerminalNode LOWER_WORD() {
            return getToken(PlayerClassParser.LOWER_WORD, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassParser.INTEGER, i);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterEffect(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitEffect(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitEffect(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectContext effect() throws RecognitionException {
        EffectContext _localctx = new EffectContext(_ctx, getState());
        enterRule(_localctx, 54, RULE_effect);

        EffectEnum effectType = EffectEnum.EF_NONE;
        String radius = "0";
        String extraParm = "0";
        EffectSubTypeWrapper wrappedValue = null;
        EffectSubTypeEnum subType = EffectSubTypeEnum.EST_NONE;

        try {
            setState(301);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 5, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(273);
                    match(EFFECT);
                    setState(274);
                    ((EffectContext) _localctx).flagA1 = match(UPPER_WORD);

                    String rawA1 = "EF_" + ((EffectContext) _localctx).flagA1.getText();
                    effectType = EffectEnum.valueOf(rawA1);
                    subType = effectType.getSubType();

                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(276);
                    match(EFFECT);
                    setState(277);
                    ((EffectContext) _localctx).flagA2 = match(UPPER_WORD);
                    setState(278);
                    match(COLON);
                    setState(282);
                    _errHandler.sync(this);
                    switch (_input.LA(1)) {
                        case UPPER_LOWER_STRING: {
                            setState(279);
                            ((EffectContext) _localctx).flagAa2 = match(UPPER_LOWER_STRING);
                        }
                        break;
                        case LOWER_WORD: {
                            setState(280);
                            ((EffectContext) _localctx).flagAb2 = match(LOWER_WORD);
                        }
                        break;
                        case UPPER_WORD: {
                            setState(281);
                            ((EffectContext) _localctx).flagAc2 = match(UPPER_WORD);
                        }
                        break;
                        default:
                            throw new NoViableAltException(this);
                    }

                    String rawA2 = "EF_" + ((EffectContext) _localctx).flagA2.getText();
                    wrappedValue = null;
                    effectType = EffectEnum.valueOf(rawA2);
                    subType = effectType.getSubType();
                    String rawFlag2;
                    if (((EffectContext) _localctx).flagAa2 == null && ((EffectContext) _localctx).flagAb2 == null)
                        rawFlag2 = ((EffectContext) _localctx).flagAc2.getText();
                    else if (((EffectContext) _localctx).flagAa2 == null && ((EffectContext) _localctx).flagAc2 == null)
                        rawFlag2 = ((EffectContext) _localctx).flagAb2.getText();
                    else
                        rawFlag2 = ((EffectContext) _localctx).flagAa2.getText();

                    switch (subType) {
                        case EST_TMD:
                            wrappedValue = new EffectSubTypeWrapper(TimedEffect.valueOf("TMD_" + rawFlag2));
                            break;

                        case EST_PROJ:
                            wrappedValue = new EffectSubTypeWrapper(ProjectionEnum.valueOf("PROJ_" + rawFlag2));
                            break;

                        case EST_NOURISH:
                            wrappedValue = new EffectSubTypeWrapper(EffectNourish.valueOf("EN_" + rawFlag2));
                            break;

                        case EST_SUMMON:
                            wrappedValue = new EffectSubTypeWrapper(GameConstants.lookupSummon(rawFlag2));
                            break;

                        case EST_STAT:
                            wrappedValue = new EffectSubTypeWrapper(Stats.valueOf("STAT_" + rawFlag2));
                            break;

                        case EST_ENCHANT:
                            wrappedValue = new EffectSubTypeWrapper(EffectEnchant.valueOf("EE_" + rawFlag2));
                            break;

                        case EST_SHAPECHANGE:
                            wrappedValue = new EffectSubTypeWrapper(GameConstants.lookupPlayerShape(rawFlag2));
                            break;

                        case EST_EARTHQUAKE:
                            wrappedValue = new EffectSubTypeWrapper(Earthquake.valueOf("QUAKE_" + rawFlag2));
                            break;

                        case EST_GLYPH:
                            wrappedValue = new EffectSubTypeWrapper(GlyphType.valueOf("GLYPH_" + rawFlag2));
                            break;

                        default:
                            String message = "Invalid effect line, illegal or unexpected sub type found.\n"
                                    + "Line is: effect:" + ((EffectContext) _localctx).flagA2.getText() + ":" + rawFlag2;
                            throw new InvalidTokenFoundDuringParse(message);
                    }

                }
                break;
                case 3:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(285);
                    match(EFFECT);
                    setState(286);
                    ((EffectContext) _localctx).flagA3 = match(UPPER_WORD);
                    setState(287);
                    match(COLON);
                    setState(288);
                    ((EffectContext) _localctx).flagB3 = match(UPPER_WORD);
                    setState(289);
                    match(COLON);
                    setState(290);
                    ((EffectContext) _localctx).rad3 = match(INTEGER);

                    String rawA3 = "EF_" + ((EffectContext) _localctx).flagA3.getText();
                    effectType = EffectEnum.valueOf(rawA3);
                    String rawFlag3 = ((EffectContext) _localctx).flagB3.getText();

                    switch (effectType.getSubType()) {
                        case EST_TMD:
                            wrappedValue = new EffectSubTypeWrapper(TimedEffect.valueOf("TMD_" + rawFlag3));
                            break;

                        case EST_PROJ:
                            wrappedValue = new EffectSubTypeWrapper(ProjectionEnum.valueOf("PROJ_" + rawFlag3));
                            break;

                        case EST_NOURISH:
                            wrappedValue = new EffectSubTypeWrapper(EffectNourish.valueOf("EN_" + rawFlag3));
                            break;

                        case EST_SUMMON:
                            wrappedValue = new EffectSubTypeWrapper(GameConstants.lookupSummon(rawFlag3));
                            break;

                        case EST_STAT:
                            wrappedValue = new EffectSubTypeWrapper(Stats.valueOf("STAT_" + rawFlag3));
                            break;

                        case EST_ENCHANT:
                            wrappedValue = new EffectSubTypeWrapper(EffectEnchant.valueOf("EE_" + rawFlag3));
                            break;

                        case EST_EARTHQUAKE:
                            wrappedValue = new EffectSubTypeWrapper(Earthquake.valueOf("QUAKE_" + rawFlag3));
                            break;

                        default:
                            String message = "Invalid effect line, illegal or unexpected sub type found.\n"
                                    + "Line is: effect:" + ((EffectContext) _localctx).flagA3.getText() + ":" + rawFlag3 + ":" + ((EffectContext) _localctx).rad3.getText();
                            throw new InvalidTokenFoundDuringParse(message);
                    }
                    radius = ((EffectContext) _localctx).rad3.getText();

                }
                break;
                case 4:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(292);
                    match(EFFECT);
                    setState(293);
                    ((EffectContext) _localctx).flagA4 = match(UPPER_WORD);
                    setState(294);
                    match(COLON);
                    setState(295);
                    ((EffectContext) _localctx).flagB4 = match(UPPER_WORD);
                    setState(296);
                    match(COLON);
                    setState(297);
                    ((EffectContext) _localctx).rad4 = match(INTEGER);
                    setState(298);
                    match(COLON);
                    setState(299);
                    ((EffectContext) _localctx).parm4 = match(INTEGER);

                    String rawA4 = "EF_" + ((EffectContext) _localctx).flagA4.getText();
                    effectType = EffectEnum.valueOf(rawA4);
                    String rawFlag4 = ((EffectContext) _localctx).flagB4.getText();

                    switch (effectType.getSubType()) {
                        case EST_TMD:
                            wrappedValue = new EffectSubTypeWrapper(TimedEffect.valueOf("TMD_" + rawFlag4));
                            break;

                        case EST_PROJ:
                            wrappedValue = new EffectSubTypeWrapper(ProjectionEnum.valueOf("PROJ_" + rawFlag4));
                            break;

                        case EST_NOURISH:
                            wrappedValue = new EffectSubTypeWrapper(EffectNourish.valueOf("EN_" + rawFlag4));
                            break;

                        case EST_SUMMON:
                            wrappedValue = new EffectSubTypeWrapper(GameConstants.lookupSummon(rawFlag4));
                            break;

                        case EST_STAT:
                            wrappedValue = new EffectSubTypeWrapper(Stats.valueOf("STAT_" + rawFlag4));
                            break;

                        case EST_ENCHANT:
                            wrappedValue = new EffectSubTypeWrapper(EffectEnchant.valueOf("EE_" + rawFlag4));
                            break;

                        default:
                            String message = "Invalid effect line, illegal or unexpected sub type found.\n"
                                    + "Line is: effect:" + ((EffectContext) _localctx).flagA4.getText() + ":" + rawFlag4 + ":"
                                    + ((EffectContext) _localctx).rad4.getText() + ":" + ((EffectContext) _localctx).parm4.getText();
                            throw new InvalidTokenFoundDuringParse(message);
                    }
                    radius = ((EffectContext) _localctx).rad4.getText();
                    extraParm = ((EffectContext) _localctx).parm4.getText();

                }
                break;
            }
            _ctx.stop = _input.LT(-1);

            // TODO(ClaudeCode): EffectBlock not yet re-plumbed to the current Effect constructor API;
            // this Effect(...) overload no longer exists. Commented out to keep the build green.
            /*((EffectContext)_localctx).effectObj =  new Effect(effectType, subType, wrappedValue, radius, extraParm);*/
            ((EffectContext) _localctx).effectObj = null;

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
        public String msg;
        public Token LOWER_WORD;

        public TerminalNode EFFECT_MSG() {
            return getToken(PlayerClassParser.EFFECT_MSG, 0);
        }

        public TerminalNode LOWER_WORD() {
            return getToken(PlayerClassParser.LOWER_WORD, 0);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterEffectMsg(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitEffectMsg(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitEffectMsg(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectMsgContext effectMsg() throws RecognitionException {
        EffectMsgContext _localctx = new EffectMsgContext(_ctx, getState());
        enterRule(_localctx, 56, RULE_effectMsg);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(303);
                match(EFFECT_MSG);
                setState(304);
                ((EffectMsgContext) _localctx).LOWER_WORD = match(LOWER_WORD);
                ((EffectMsgContext) _localctx).msg = ((EffectMsgContext) _localctx).LOWER_WORD.getText();
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
        public int y;
        public int x;
        public Token yInt;
        public Token xInt;

        public TerminalNode EFFECT_YX() {
            return getToken(PlayerClassParser.EFFECT_YX, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerClassParser.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassParser.INTEGER, i);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterEffectYX(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitEffectYX(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitEffectYX(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectYXContext effectYX() throws RecognitionException {
        EffectYXContext _localctx = new EffectYXContext(_ctx, getState());
        enterRule(_localctx, 58, RULE_effectYX);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(307);
                match(EFFECT_YX);
                setState(308);
                ((EffectYXContext) _localctx).yInt = match(INTEGER);
                setState(309);
                match(COLON);
                setState(310);
                ((EffectYXContext) _localctx).xInt = match(INTEGER);

                ((EffectYXContext) _localctx).y = Integer.parseInt(((EffectYXContext) _localctx).yInt.getText());
                ((EffectYXContext) _localctx).x = Integer.parseInt(((EffectYXContext) _localctx).xInt.getText());

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
        public Token DICE;

        public TerminalNode DICE() {
            return getToken(PlayerClassParser.DICE, 0);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterDice(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitDice(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitDice(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DiceContext dice() throws RecognitionException {
        DiceContext _localctx = new DiceContext(_ctx, getState());
        enterRule(_localctx, 60, RULE_dice);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(313);
                ((DiceContext) _localctx).DICE = match(DICE);

                ((DiceContext) _localctx).diceString = ((DiceContext) _localctx).DICE.getText().substring(5);

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
        public Effect eff;
        public EffectContext effect;
        public DiceContext dice;
        public EffectYXContext effectYX;
        public EffectMsgContext effectMsg;
        public ExprContext expr;

        public EffectContext effect() {
            return getRuleContext(EffectContext.class, 0);
        }

        public DiceContext dice() {
            return getRuleContext(DiceContext.class, 0);
        }

        public EffectYXContext effectYX() {
            return getRuleContext(EffectYXContext.class, 0);
        }

        public EffectMsgContext effectMsg() {
            return getRuleContext(EffectMsgContext.class, 0);
        }

        public List<ExprContext> expr() {
            return getRuleContexts(ExprContext.class);
        }

        public ExprContext expr(int i) {
            return getRuleContext(ExprContext.class, i);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterEffectBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitEffectBlock(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitEffectBlock(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectBlockContext effectBlock() throws RecognitionException {
        EffectBlockContext _localctx = new EffectBlockContext(_ctx, getState());
        enterRule(_localctx, 62, RULE_effectBlock);

        ((EffectBlockContext) _localctx).eff = null;

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(316);
                ((EffectBlockContext) _localctx).effect = effect();
                ((EffectBlockContext) _localctx).eff = ((EffectBlockContext) _localctx).effect.effectObj;
                setState(321);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == DICE) {
                    {
                        setState(318);
                        ((EffectBlockContext) _localctx).dice = dice();

                        String diceString = ((EffectBlockContext) _localctx).dice.diceString;
                        // TODO(Rowan): Effect.setDice causes a NPE - comment out to allow running _localctx.eff.setDice(diceString);

                    }
                }

                setState(326);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == EFFECT_YX) {
                    {
                        setState(323);
                        ((EffectBlockContext) _localctx).effectYX = effectYX();

                        // TODO(Rowan): Effect.setYX causes a NPE - comment out to allow running   _localctx.eff.setYX(((EffectBlockContext)_localctx).effectYX.y, ((EffectBlockContext)_localctx).effectYX.x);

                    }
                }

                setState(331);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == EFFECT_MSG) {
                    {
                        setState(328);
                        ((EffectBlockContext) _localctx).effectMsg = effectMsg();

                        // TODO(ClaudeCode): Effect.setMsg(...) no longer exists. Commented out to compile.
                        //_localctx.eff.setMsg(((EffectBlockContext)_localctx).effectMsg.msg);

                    }
                }

                setState(338);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == EXPR) {
                    {
                        {
                            setState(333);
                            ((EffectBlockContext) _localctx).expr = expr();

                            // TODO(ClaudeCode): Effect.setExpression(...) no longer exists. Commented out to compile.
                            //_localctx.eff.setExpression(((EffectBlockContext)_localctx).expr.expression);

                        }
                    }
                    setState(340);
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
    public static class ExprContext extends ParserRuleContext {
        public Expression expression;
        public Token letter;
        public Token flag;
        public Token expOp1;
        public Token m1;
        public Token p1;
        public Token int1;
        public Token expOp2;
        public Token m2;
        public Token p2;
        public Token int2;

        public TerminalNode EXPR() {
            return getToken(PlayerClassParser.EXPR, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(PlayerClassParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(PlayerClassParser.COLON, i);
        }

        public List<TerminalNode> SPACE() {
            return getTokens(PlayerClassParser.SPACE);
        }

        public TerminalNode SPACE(int i) {
            return getToken(PlayerClassParser.SPACE, i);
        }

        public TerminalNode EXPR_LETTER() {
            return getToken(PlayerClassParser.EXPR_LETTER, 0);
        }

        public TerminalNode UPPER_WORD() {
            return getToken(PlayerClassParser.UPPER_WORD, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerClassParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerClassParser.INTEGER, i);
        }

        public List<TerminalNode> EXPR_OPERATORS() {
            return getTokens(PlayerClassParser.EXPR_OPERATORS);
        }

        public TerminalNode EXPR_OPERATORS(int i) {
            return getToken(PlayerClassParser.EXPR_OPERATORS, i);
        }

        public List<TerminalNode> MINUS() {
            return getTokens(PlayerClassParser.MINUS);
        }

        public TerminalNode MINUS(int i) {
            return getToken(PlayerClassParser.MINUS, i);
        }

        public List<TerminalNode> PLUS() {
            return getTokens(PlayerClassParser.PLUS);
        }

        public TerminalNode PLUS(int i) {
            return getToken(PlayerClassParser.PLUS, i);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitExpr(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExprContext expr() throws RecognitionException {
        ExprContext _localctx = new ExprContext(_ctx, getState());
        enterRule(_localctx, 64, RULE_expr);

        char codeLetter = '\0';
        EffectBaseType type = EffectBaseType.EFB_NONE;
        String operations = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(341);
                match(EXPR);
                setState(342);
                ((ExprContext) _localctx).letter = match(EXPR_LETTER);
                setState(343);
                match(COLON);
                setState(344);
                ((ExprContext) _localctx).flag = match(UPPER_WORD);
                setState(345);
                match(COLON);

                String rawLetter = ((ExprContext) _localctx).letter.getText();
                if (rawLetter.length() != 1) {
                    String message = "Invalid expr line.\n";
                    throw new InvalidTokenFoundDuringParse(message);
                }

                codeLetter = rawLetter.charAt(0);
                String rawFlag = ((ExprContext) _localctx).flag.getText();
                type = EffectBaseType.valueOf("EFB_" + rawFlag);

                setState(350);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case EXPR_OPERATORS: {
                        setState(347);
                        ((ExprContext) _localctx).expOp1 = match(EXPR_OPERATORS);
                    }
                    break;
                    case MINUS: {
                        setState(348);
                        ((ExprContext) _localctx).m1 = match(MINUS);
                    }
                    break;
                    case PLUS: {
                        setState(349);
                        ((ExprContext) _localctx).p1 = match(PLUS);
                    }
                    break;
                    default:
                        throw new NoViableAltException(this);
                }
                setState(352);
                match(SPACE);
                setState(353);
                ((ExprContext) _localctx).int1 = match(INTEGER);

                if (((ExprContext) _localctx).expOp1 == null && ((ExprContext) _localctx).m1 == null)
                    operations = operations + ((ExprContext) _localctx).p1.getText();
                else if (((ExprContext) _localctx).expOp1 == null && ((ExprContext) _localctx).p1 == null)
                    operations = operations + ((ExprContext) _localctx).m1.getText();
                else
                    operations = operations + ((ExprContext) _localctx).expOp1.getText();

                operations = operations + " " + ((ExprContext) _localctx).int1.getText();

                setState(366);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == SPACE) {
                    {
                        {
                            setState(355);
                            match(SPACE);
                            setState(359);
                            _errHandler.sync(this);
                            switch (_input.LA(1)) {
                                case EXPR_OPERATORS: {
                                    setState(356);
                                    ((ExprContext) _localctx).expOp2 = match(EXPR_OPERATORS);
                                }
                                break;
                                case MINUS: {
                                    setState(357);
                                    ((ExprContext) _localctx).m2 = match(MINUS);
                                }
                                break;
                                case PLUS: {
                                    setState(358);
                                    ((ExprContext) _localctx).p2 = match(PLUS);
                                }
                                break;
                                default:
                                    throw new NoViableAltException(this);
                            }
                            setState(361);
                            match(SPACE);
                            setState(362);
                            ((ExprContext) _localctx).int2 = match(INTEGER);

                            if (((ExprContext) _localctx).expOp2 == null && ((ExprContext) _localctx).m2 == null)
                                operations = operations + " " + ((ExprContext) _localctx).p2.getText();
                            else if (((ExprContext) _localctx).expOp2 == null && ((ExprContext) _localctx).p2 == null)
                                operations = operations + " " + ((ExprContext) _localctx).m2.getText();
                            else
                                operations = operations + " " + ((ExprContext) _localctx).expOp2.getText();

                            operations = operations + " " + ((ExprContext) _localctx).int2.getText();

                        }
                    }
                    setState(368);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
            }
            _ctx.stop = _input.LT(-1);

            ((ExprContext) _localctx).expression = new Expression(codeLetter, type, operations);

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
        public String descString;
        public Token DESC_STRING;

        public TerminalNode DESC_STRING() {
            return getToken(PlayerClassParser.DESC_STRING, 0);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterDesc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitDesc(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitDesc(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DescContext desc() throws RecognitionException {
        DescContext _localctx = new DescContext(_ctx, getState());
        enterRule(_localctx, 66, RULE_desc);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(369);
                ((DescContext) _localctx).DESC_STRING = match(DESC_STRING);
                ((DescContext) _localctx).descString = ((DescContext) _localctx).DESC_STRING.getText().substring(5);
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
        public MagicSpell magicSpell;
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterSpellBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitSpellBlock(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitSpellBlock(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SpellBlockContext spellBlock() throws RecognitionException {
        SpellBlockContext _localctx = new SpellBlockContext(_ctx, getState());
        enterRule(_localctx, 68, RULE_spellBlock);

        String descStr = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(372);
                ((SpellBlockContext) _localctx).spell = spell();
                ((SpellBlockContext) _localctx).magicSpell = ((SpellBlockContext) _localctx).spell.magicSpell;
                setState(377);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(374);
                            ((SpellBlockContext) _localctx).effectBlock = effectBlock();
                            _localctx.magicSpell.addEffect(((SpellBlockContext) _localctx).effectBlock.eff);
                        }
                    }
                    setState(379);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == EFFECT);
                setState(386);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == DESC_STRING) {
                    {
                        {
                            setState(381);
                            ((SpellBlockContext) _localctx).desc = desc();
                            descStr = descStr + ((SpellBlockContext) _localctx).desc.descString;
                        }
                    }
                    setState(388);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
            }
            _ctx.stop = _input.LT(-1);

            _localctx.magicSpell.setSpellDescription(descStr);

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
        public MagicBook magicBook;
        public StartItem startItem;
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

        @Override
        public int getRuleIndex() {
            return RULE_bookBlock;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterBookBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitBookBlock(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitBookBlock(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BookBlockContext bookBlock() throws RecognitionException {
        BookBlockContext _localctx = new BookBlockContext(_ctx, getState());
        enterRule(_localctx, 70, RULE_bookBlock);

        boolean kindFound = false;
        ObjectKind bookKind = new ObjectKind();
        ((BookBlockContext) _localctx).startItem = null;

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(389);
                ((BookBlockContext) _localctx).book = book();

                ((BookBlockContext) _localctx).magicBook = ((BookBlockContext) _localctx).book.magicBook;

                setState(394);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == BOOK_GRAPHICS) {
                    {
                        setState(391);
                        ((BookBlockContext) _localctx).bookGraphics = bookGraphics();

                        bookKind.setCharacter(((BookBlockContext) _localctx).bookGraphics.adc);
                        kindFound = true;

                    }
                }

                setState(399);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == BOOK_PROPERTIES) {
                    {
                        setState(396);
                        ((BookBlockContext) _localctx).bookProperties = bookProperties();

                        bookKind.setAlloc_prob(((BookBlockContext) _localctx).bookProperties.commonness);
                        bookKind.setCost(((BookBlockContext) _localctx).bookProperties.cost);
                        bookKind.setAlloc_min(((BookBlockContext) _localctx).bookProperties.min);
                        bookKind.setAlloc_max(((BookBlockContext) _localctx).bookProperties.max);
                        kindFound = true;

                    }
                }

                setState(404);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == EQUIP) {
                    {
                        setState(401);
                        ((BookBlockContext) _localctx).equip = equip();

                        ((BookBlockContext) _localctx).startItem = ((BookBlockContext) _localctx).equip.startItem;

                    }
                }

                setState(409);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(406);
                            ((BookBlockContext) _localctx).spellBlock = spellBlock();

                            _localctx.magicBook.addSpell(((BookBlockContext) _localctx).spellBlock.magicSpell);

                        }
                    }
                    setState(411);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == SPELL_STRING);
            }
            _ctx.stop = _input.LT(-1);

            if (kindFound)
                _localctx.magicBook.setBookKind(bookKind);

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
        public PlayerClass player;
        public NameContext name;
        public StatsContext stats;
        public SkillDisarmPhysContext skillDisarmPhys;
        public SkillDisarmMagicContext skillDisarmMagic;
        public SkillDeviceContext skillDevice;
        public SkillSaveContext skillSave;
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
        public EquipBlockContext equipBlock;
        public ObjectFlagsContext objectFlags;
        public PlayerFlagsContext playerFlags;
        public TitleBlockContext titleBlock;
        public MagicContext magic;
        public BookBlockContext bookBlock;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public StatsContext stats() {
            return getRuleContext(StatsContext.class, 0);
        }

        public SkillDisarmPhysContext skillDisarmPhys() {
            return getRuleContext(SkillDisarmPhysContext.class, 0);
        }

        public SkillDisarmMagicContext skillDisarmMagic() {
            return getRuleContext(SkillDisarmMagicContext.class, 0);
        }

        public SkillDeviceContext skillDevice() {
            return getRuleContext(SkillDeviceContext.class, 0);
        }

        public SkillSaveContext skillSave() {
            return getRuleContext(SkillSaveContext.class, 0);
        }

        public SkillStealthContext skillStealth() {
            return getRuleContext(SkillStealthContext.class, 0);
        }

        public SkillSearchContext skillSearch() {
            return getRuleContext(SkillSearchContext.class, 0);
        }

        public SkillMeleeContext skillMelee() {
            return getRuleContext(SkillMeleeContext.class, 0);
        }

        public SkillShootContext skillShoot() {
            return getRuleContext(SkillShootContext.class, 0);
        }

        public SkillThrowContext skillThrow() {
            return getRuleContext(SkillThrowContext.class, 0);
        }

        public SkillDigContext skillDig() {
            return getRuleContext(SkillDigContext.class, 0);
        }

        public HitdieContext hitdie() {
            return getRuleContext(HitdieContext.class, 0);
        }

        public MaxAttacksContext maxAttacks() {
            return getRuleContext(MaxAttacksContext.class, 0);
        }

        public MinWeightContext minWeight() {
            return getRuleContext(MinWeightContext.class, 0);
        }

        public StrengthMultiplierContext strengthMultiplier() {
            return getRuleContext(StrengthMultiplierContext.class, 0);
        }

        public EquipBlockContext equipBlock() {
            return getRuleContext(EquipBlockContext.class, 0);
        }

        public PlayerFlagsContext playerFlags() {
            return getRuleContext(PlayerFlagsContext.class, 0);
        }

        public TitleBlockContext titleBlock() {
            return getRuleContext(TitleBlockContext.class, 0);
        }

        public ObjectFlagsContext objectFlags() {
            return getRuleContext(ObjectFlagsContext.class, 0);
        }

        public MagicContext magic() {
            return getRuleContext(MagicContext.class, 0);
        }

        public List<BookBlockContext> bookBlock() {
            return getRuleContexts(BookBlockContext.class);
        }

        public BookBlockContext bookBlock(int i) {
            return getRuleContext(BookBlockContext.class, i);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterPlayerClass(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitPlayerClass(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitPlayerClass(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PlayerClassContext playerClass() throws RecognitionException {
        PlayerClassContext _localctx = new PlayerClassContext(_ctx, getState());
        enterRule(_localctx, 72, RULE_playerClass);

        String classStrInit = "";
        List<String> titlesInit = new ArrayList<>();
        Map<Stats, Integer> statsInit = new HashMap<>();
        Map<PlayerSkill, Integer> classSkillsInit = new HashMap<>();
        Map<PlayerSkill, Integer> extraSkillsInit = new HashMap<>();
        int hpAdjInit = 0;
        int attackCount = 0;
        int weight = 0;
        int strMult = 0;
        List<StartItem> startItems = new ArrayList<>();
        Flag<ObjectFlag> oFlags = new Flag<>(ObjectFlag.class);
        Flag<PlayerFlag> pFlags = new Flag<>(PlayerFlag.class);
        ClassMagic classMagic = null;

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(413);
                ((PlayerClassContext) _localctx).name = name();
                classStrInit = ((PlayerClassContext) _localctx).name.nameStr;
                setState(415);
                ((PlayerClassContext) _localctx).stats = stats();
                statsInit = ((PlayerClassContext) _localctx).stats.stat;
                setState(417);
                ((PlayerClassContext) _localctx).skillDisarmPhys = skillDisarmPhys();

                classSkillsInit.put(PlayerSkill.SKILL_DISARM_PHYS, ((PlayerClassContext) _localctx).skillDisarmPhys.base);
                extraSkillsInit.put(PlayerSkill.SKILL_DISARM_PHYS, ((PlayerClassContext) _localctx).skillDisarmPhys.increment);

                setState(419);
                ((PlayerClassContext) _localctx).skillDisarmMagic = skillDisarmMagic();

                classSkillsInit.put(PlayerSkill.SKILL_DISARM_MAGIC, ((PlayerClassContext) _localctx).skillDisarmMagic.base);
                extraSkillsInit.put(PlayerSkill.SKILL_DISARM_MAGIC, ((PlayerClassContext) _localctx).skillDisarmMagic.increment);

                setState(421);
                ((PlayerClassContext) _localctx).skillDevice = skillDevice();

                classSkillsInit.put(PlayerSkill.SKILL_DEVICE, ((PlayerClassContext) _localctx).skillDevice.base);
                extraSkillsInit.put(PlayerSkill.SKILL_DEVICE, ((PlayerClassContext) _localctx).skillDevice.increment);

                setState(423);
                ((PlayerClassContext) _localctx).skillSave = skillSave();

                classSkillsInit.put(PlayerSkill.SKILL_SAVE, ((PlayerClassContext) _localctx).skillSave.base);
                extraSkillsInit.put(PlayerSkill.SKILL_SAVE, ((PlayerClassContext) _localctx).skillSave.increment);

                setState(425);
                ((PlayerClassContext) _localctx).skillStealth = skillStealth();

                classSkillsInit.put(PlayerSkill.SKILL_STEALTH, ((PlayerClassContext) _localctx).skillStealth.base);
                extraSkillsInit.put(PlayerSkill.SKILL_STEALTH, ((PlayerClassContext) _localctx).skillStealth.increment);

                setState(427);
                ((PlayerClassContext) _localctx).skillSearch = skillSearch();

                classSkillsInit.put(PlayerSkill.SKILL_SEARCH, ((PlayerClassContext) _localctx).skillSearch.base);
                extraSkillsInit.put(PlayerSkill.SKILL_SEARCH, ((PlayerClassContext) _localctx).skillSearch.increment);

                setState(429);
                ((PlayerClassContext) _localctx).skillMelee = skillMelee();

                classSkillsInit.put(PlayerSkill.SKILL_TO_HIT_MELEE, ((PlayerClassContext) _localctx).skillMelee.base);
                extraSkillsInit.put(PlayerSkill.SKILL_TO_HIT_MELEE, ((PlayerClassContext) _localctx).skillMelee.increment);

                setState(431);
                ((PlayerClassContext) _localctx).skillShoot = skillShoot();

                classSkillsInit.put(PlayerSkill.SKILL_TO_HIT_BOW, ((PlayerClassContext) _localctx).skillShoot.base);
                extraSkillsInit.put(PlayerSkill.SKILL_TO_HIT_BOW, ((PlayerClassContext) _localctx).skillShoot.increment);

                setState(433);
                ((PlayerClassContext) _localctx).skillThrow = skillThrow();

                classSkillsInit.put(PlayerSkill.SKILL_TO_HIT_THROW, ((PlayerClassContext) _localctx).skillThrow.base);
                extraSkillsInit.put(PlayerSkill.SKILL_TO_HIT_THROW, ((PlayerClassContext) _localctx).skillThrow.increment);

                setState(435);
                ((PlayerClassContext) _localctx).skillDig = skillDig();

                classSkillsInit.put(PlayerSkill.SKILL_DIGGING, ((PlayerClassContext) _localctx).skillDig.base);
                extraSkillsInit.put(PlayerSkill.SKILL_DIGGING, ((PlayerClassContext) _localctx).skillDig.increment);

                setState(437);
                ((PlayerClassContext) _localctx).hitdie = hitdie();

                hpAdjInit = ((PlayerClassContext) _localctx).hitdie.increment;

                setState(439);
                ((PlayerClassContext) _localctx).maxAttacks = maxAttacks();

                attackCount = ((PlayerClassContext) _localctx).maxAttacks.count;

                setState(441);
                ((PlayerClassContext) _localctx).minWeight = minWeight();

                weight = ((PlayerClassContext) _localctx).minWeight.weight;

                setState(443);
                ((PlayerClassContext) _localctx).strengthMultiplier = strengthMultiplier();

                strMult = ((PlayerClassContext) _localctx).strengthMultiplier.factor;

                setState(445);
                ((PlayerClassContext) _localctx).equipBlock = equipBlock();

                startItems = ((PlayerClassContext) _localctx).equipBlock.startItems;

                setState(450);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == OBJ_FLAGS) {
                    {
                        setState(447);
                        ((PlayerClassContext) _localctx).objectFlags = objectFlags();

                        oFlags = ((PlayerClassContext) _localctx).objectFlags.oFlags;

                    }
                }

                setState(452);
                ((PlayerClassContext) _localctx).playerFlags = playerFlags();

                pFlags = ((PlayerClassContext) _localctx).playerFlags.pFlags;

                setState(454);
                ((PlayerClassContext) _localctx).titleBlock = titleBlock();
                titlesInit = ((PlayerClassContext) _localctx).titleBlock.titles;
                setState(465);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == MAGIC) {
                    {
                        setState(456);
                        ((PlayerClassContext) _localctx).magic = magic();

                        classMagic = ((PlayerClassContext) _localctx).magic.classMagic;

                        setState(461);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        do {
                            {
                                {
                                    setState(458);
                                    ((PlayerClassContext) _localctx).bookBlock = bookBlock();

                                    classMagic.addMagicBook(((PlayerClassContext) _localctx).bookBlock.magicBook);
                                    StartItem si = ((PlayerClassContext) _localctx).bookBlock.startItem;
                                    if (si != null)
                                        startItems.add(si);

                                }
                            }
                            setState(463);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        } while (_la == BOOK);
                    }
                }

            }
            _ctx.stop = _input.LT(-1);

            ((PlayerClassContext) _localctx).player = new PlayerClass(classStrInit, titlesInit, statsInit,
                    classSkillsInit, extraSkillsInit, hpAdjInit, 100, oFlags,
                    pFlags, attackCount, weight, strMult, startItems, classMagic);

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
        public List<PlayerClass> playerClasses;
        public PlayerClassContext playerClass;

        public TerminalNode EOF() {
            return getToken(PlayerClassParser.EOF, 0);
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
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerClassListener) ((PlayerClassListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerClassVisitor)
                return ((PlayerClassVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 74, RULE_file);

        ((FileContext) _localctx).playerClasses = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(470);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(467);
                            ((FileContext) _localctx).playerClass = playerClass();

                            _localctx.playerClasses.add(((FileContext) _localctx).playerClass.player);

                        }
                    }
                    setState(472);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(474);
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
            "\u0004\u00014\u01dd\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
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
                    "#\u0007#\u0002$\u0007$\u0002%\u0007%\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001" +
                    "\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f" +
                    "\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010" +
                    "\u0001\u0010\u0003\u0010\u00ae\b\u0010\u0001\u0010\u0001\u0010\u0001\u0010" +
                    "\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011" +
                    "\u0001\u0011\u0001\u0011\u0004\u0011\u00bb\b\u0011\u000b\u0011\f\u0011" +
                    "\u00bc\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001" +
                    "\u0012\u0005\u0012\u00c5\b\u0012\n\u0012\f\u0012\u00c8\t\u0012\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0005\u0013" +
                    "\u00d0\b\u0013\n\u0013\f\u0013\u00d3\t\u0013\u0001\u0014\u0001\u0014\u0001" +
                    "\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001" +
                    "\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001" +
                    "\u0015\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001" +
                    "\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001" +
                    "\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001" +
                    "\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001" +
                    "\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0001" +
                    "\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001" +
                    "\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001" +
                    "\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001b\u0001" +
                    "\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001" +
                    "\u001b\u0001\u001b\u0003\u001b\u011b\b\u001b\u0001\u001b\u0001\u001b\u0001" +
                    "\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001" +
                    "\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001" +
                    "\u001b\u0001\u001b\u0001\u001b\u0003\u001b\u012e\b\u001b\u0001\u001c\u0001" +
                    "\u001c\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001d\u0001" +
                    "\u001d\u0001\u001d\u0001\u001d\u0001\u001e\u0001\u001e\u0001\u001e\u0001" +
                    "\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0003\u001f\u0142" +
                    "\b\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0003\u001f\u0147\b\u001f" +
                    "\u0001\u001f\u0001\u001f\u0001\u001f\u0003\u001f\u014c\b\u001f\u0001\u001f" +
                    "\u0001\u001f\u0001\u001f\u0005\u001f\u0151\b\u001f\n\u001f\f\u001f\u0154" +
                    "\t\u001f\u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0001" +
                    " \u0003 \u015f\b \u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0003" +
                    " \u0168\b \u0001 \u0001 \u0001 \u0005 \u016d\b \n \f \u0170\t \u0001!" +
                    "\u0001!\u0001!\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0004\"\u017a\b" +
                    "\"\u000b\"\f\"\u017b\u0001\"\u0001\"\u0001\"\u0005\"\u0181\b\"\n\"\f\"" +
                    "\u0184\t\"\u0001#\u0001#\u0001#\u0001#\u0001#\u0003#\u018b\b#\u0001#\u0001" +
                    "#\u0001#\u0003#\u0190\b#\u0001#\u0001#\u0001#\u0003#\u0195\b#\u0001#\u0001" +
                    "#\u0001#\u0004#\u019a\b#\u000b#\f#\u019b\u0001$\u0001$\u0001$\u0001$\u0001" +
                    "$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001" +
                    "$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001" +
                    "$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001" +
                    "$\u0001$\u0001$\u0003$\u01c3\b$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001" +
                    "$\u0001$\u0001$\u0001$\u0004$\u01ce\b$\u000b$\f$\u01cf\u0003$\u01d2\b" +
                    "$\u0001%\u0001%\u0001%\u0004%\u01d7\b%\u000b%\f%\u01d8\u0001%\u0001%\u0001" +
                    "%\u0000\u0000&\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016" +
                    "\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJ\u0000\u0000\u01d2\u0000" +
                    "L\u0001\u0000\u0000\u0000\u0002P\u0001\u0000\u0000\u0000\u0004\\\u0001" +
                    "\u0000\u0000\u0000\u0006b\u0001\u0000\u0000\u0000\bh\u0001\u0000\u0000" +
                    "\u0000\nn\u0001\u0000\u0000\u0000\ft\u0001\u0000\u0000\u0000\u000ez\u0001" +
                    "\u0000\u0000\u0000\u0010\u0080\u0001\u0000\u0000\u0000\u0012\u0086\u0001" +
                    "\u0000\u0000\u0000\u0014\u008c\u0001\u0000\u0000\u0000\u0016\u0092\u0001" +
                    "\u0000\u0000\u0000\u0018\u0098\u0001\u0000\u0000\u0000\u001a\u009c\u0001" +
                    "\u0000\u0000\u0000\u001c\u00a0\u0001\u0000\u0000\u0000\u001e\u00a4\u0001" +
                    "\u0000\u0000\u0000 \u00a8\u0001\u0000\u0000\u0000\"\u00ba\u0001\u0000" +
                    "\u0000\u0000$\u00be\u0001\u0000\u0000\u0000&\u00c9\u0001\u0000\u0000\u0000" +
                    "(\u00d4\u0001\u0000\u0000\u0000*\u00d8\u0001\u0000\u0000\u0000,\u00e4" +
                    "\u0001\u0000\u0000\u0000.\u00ec\u0001\u0000\u0000\u00000\u00f8\u0001\u0000" +
                    "\u0000\u00002\u00fe\u0001\u0000\u0000\u00004\u0106\u0001\u0000\u0000\u0000" +
                    "6\u012d\u0001\u0000\u0000\u00008\u012f\u0001\u0000\u0000\u0000:\u0133" +
                    "\u0001\u0000\u0000\u0000<\u0139\u0001\u0000\u0000\u0000>\u013c\u0001\u0000" +
                    "\u0000\u0000@\u0155\u0001\u0000\u0000\u0000B\u0171\u0001\u0000\u0000\u0000" +
                    "D\u0174\u0001\u0000\u0000\u0000F\u0185\u0001\u0000\u0000\u0000H\u019d" +
                    "\u0001\u0000\u0000\u0000J\u01d6\u0001\u0000\u0000\u0000LM\u0005\u0003" +
                    "\u0000\u0000MN\u0005+\u0000\u0000NO\u0006\u0000\uffff\uffff\u0000O\u0001" +
                    "\u0001\u0000\u0000\u0000PQ\u0005\u0004\u0000\u0000QR\u0005(\u0000\u0000" +
                    "RS\u0005\"\u0000\u0000ST\u0005(\u0000\u0000TU\u0005\"\u0000\u0000UV\u0005" +
                    "(\u0000\u0000VW\u0005\"\u0000\u0000WX\u0005(\u0000\u0000XY\u0005\"\u0000" +
                    "\u0000YZ\u0005(\u0000\u0000Z[\u0006\u0001\uffff\uffff\u0000[\u0003\u0001" +
                    "\u0000\u0000\u0000\\]\u0005\u0005\u0000\u0000]^\u0005(\u0000\u0000^_\u0005" +
                    "\"\u0000\u0000_`\u0005(\u0000\u0000`a\u0006\u0002\uffff\uffff\u0000a\u0005" +
                    "\u0001\u0000\u0000\u0000bc\u0005\u0006\u0000\u0000cd\u0005(\u0000\u0000" +
                    "de\u0005\"\u0000\u0000ef\u0005(\u0000\u0000fg\u0006\u0003\uffff\uffff" +
                    "\u0000g\u0007\u0001\u0000\u0000\u0000hi\u0005\u0007\u0000\u0000ij\u0005" +
                    "(\u0000\u0000jk\u0005\"\u0000\u0000kl\u0005(\u0000\u0000lm\u0006\u0004" +
                    "\uffff\uffff\u0000m\t\u0001\u0000\u0000\u0000no\u0005\b\u0000\u0000op" +
                    "\u0005(\u0000\u0000pq\u0005\"\u0000\u0000qr\u0005(\u0000\u0000rs\u0006" +
                    "\u0005\uffff\uffff\u0000s\u000b\u0001\u0000\u0000\u0000tu\u0005\t\u0000" +
                    "\u0000uv\u0005(\u0000\u0000vw\u0005\"\u0000\u0000wx\u0005(\u0000\u0000" +
                    "xy\u0006\u0006\uffff\uffff\u0000y\r\u0001\u0000\u0000\u0000z{\u0005\n" +
                    "\u0000\u0000{|\u0005(\u0000\u0000|}\u0005\"\u0000\u0000}~\u0005(\u0000" +
                    "\u0000~\u007f\u0006\u0007\uffff\uffff\u0000\u007f\u000f\u0001\u0000\u0000" +
                    "\u0000\u0080\u0081\u0005\u000b\u0000\u0000\u0081\u0082\u0005(\u0000\u0000" +
                    "\u0082\u0083\u0005\"\u0000\u0000\u0083\u0084\u0005(\u0000\u0000\u0084" +
                    "\u0085\u0006\b\uffff\uffff\u0000\u0085\u0011\u0001\u0000\u0000\u0000\u0086" +
                    "\u0087\u0005\f\u0000\u0000\u0087\u0088\u0005(\u0000\u0000\u0088\u0089" +
                    "\u0005\"\u0000\u0000\u0089\u008a\u0005(\u0000\u0000\u008a\u008b\u0006" +
                    "\t\uffff\uffff\u0000\u008b\u0013\u0001\u0000\u0000\u0000\u008c\u008d\u0005" +
                    "\r\u0000\u0000\u008d\u008e\u0005(\u0000\u0000\u008e\u008f\u0005\"\u0000" +
                    "\u0000\u008f\u0090\u0005(\u0000\u0000\u0090\u0091\u0006\n\uffff\uffff" +
                    "\u0000\u0091\u0015\u0001\u0000\u0000\u0000\u0092\u0093\u0005\u000e\u0000" +
                    "\u0000\u0093\u0094\u0005(\u0000\u0000\u0094\u0095\u0005\"\u0000\u0000" +
                    "\u0095\u0096\u0005(\u0000\u0000\u0096\u0097\u0006\u000b\uffff\uffff\u0000" +
                    "\u0097\u0017\u0001\u0000\u0000\u0000\u0098\u0099\u0005\u000f\u0000\u0000" +
                    "\u0099\u009a\u0005(\u0000\u0000\u009a\u009b\u0006\f\uffff\uffff\u0000" +
                    "\u009b\u0019\u0001\u0000\u0000\u0000\u009c\u009d\u0005\u0011\u0000\u0000" +
                    "\u009d\u009e\u0005(\u0000\u0000\u009e\u009f\u0006\r\uffff\uffff\u0000" +
                    "\u009f\u001b\u0001\u0000\u0000\u0000\u00a0\u00a1\u0005\u0012\u0000\u0000" +
                    "\u00a1\u00a2\u0005(\u0000\u0000\u00a2\u00a3\u0006\u000e\uffff\uffff\u0000" +
                    "\u00a3\u001d\u0001\u0000\u0000\u0000\u00a4\u00a5\u0005\u0013\u0000\u0000" +
                    "\u00a5\u00a6\u0005(\u0000\u0000\u00a6\u00a7\u0006\u000f\uffff\uffff\u0000" +
                    "\u00a7\u001f\u0001\u0000\u0000\u0000\u00a8\u00a9\u0005\u0015\u0000\u0000" +
                    "\u00a9\u00aa\u0005*\u0000\u0000\u00aa\u00ad\u0005\"\u0000\u0000\u00ab" +
                    "\u00ae\u0005+\u0000\u0000\u00ac\u00ae\u0005-\u0000\u0000\u00ad\u00ab\u0001" +
                    "\u0000\u0000\u0000\u00ad\u00ac\u0001\u0000\u0000\u0000\u00ae\u00af\u0001" +
                    "\u0000\u0000\u0000\u00af\u00b0\u0005\"\u0000\u0000\u00b0\u00b1\u0005(" +
                    "\u0000\u0000\u00b1\u00b2\u0005\"\u0000\u0000\u00b2\u00b3\u0005(\u0000" +
                    "\u0000\u00b3\u00b4\u0005\"\u0000\u0000\u00b4\u00b5\u0005*\u0000\u0000" +
                    "\u00b5\u00b6\u0006\u0010\uffff\uffff\u0000\u00b6!\u0001\u0000\u0000\u0000" +
                    "\u00b7\u00b8\u0003 \u0010\u0000\u00b8\u00b9\u0006\u0011\uffff\uffff\u0000" +
                    "\u00b9\u00bb\u0001\u0000\u0000\u0000\u00ba\u00b7\u0001\u0000\u0000\u0000" +
                    "\u00bb\u00bc\u0001\u0000\u0000\u0000\u00bc\u00ba\u0001\u0000\u0000\u0000" +
                    "\u00bc\u00bd\u0001\u0000\u0000\u0000\u00bd#\u0001\u0000\u0000\u0000\u00be" +
                    "\u00bf\u0005\u0016\u0000\u0000\u00bf\u00c0\u0005)\u0000\u0000\u00c0\u00c6" +
                    "\u0006\u0012\uffff\uffff\u0000\u00c1\u00c2\u0005%\u0000\u0000\u00c2\u00c3" +
                    "\u0005)\u0000\u0000\u00c3\u00c5\u0006\u0012\uffff\uffff\u0000\u00c4\u00c1" +
                    "\u0001\u0000\u0000\u0000\u00c5\u00c8\u0001\u0000\u0000\u0000\u00c6\u00c4" +
                    "\u0001\u0000\u0000\u0000\u00c6\u00c7\u0001\u0000\u0000\u0000\u00c7%\u0001" +
                    "\u0000\u0000\u0000\u00c8\u00c6\u0001\u0000\u0000\u0000\u00c9\u00ca\u0005" +
                    "\u0017\u0000\u0000\u00ca\u00cb\u0005)\u0000\u0000\u00cb\u00d1\u0006\u0013" +
                    "\uffff\uffff\u0000\u00cc\u00cd\u0005%\u0000\u0000\u00cd\u00ce\u0005)\u0000" +
                    "\u0000\u00ce\u00d0\u0006\u0013\uffff\uffff\u0000\u00cf\u00cc\u0001\u0000" +
                    "\u0000\u0000\u00d0\u00d3\u0001\u0000\u0000\u0000\u00d1\u00cf\u0001\u0000" +
                    "\u0000\u0000\u00d1\u00d2\u0001\u0000\u0000\u0000\u00d2\'\u0001\u0000\u0000" +
                    "\u0000\u00d3\u00d1\u0001\u0000\u0000\u0000\u00d4\u00d5\u0005\u0014\u0000" +
                    "\u0000\u00d5\u00d6\u0005+\u0000\u0000\u00d6\u00d7\u0006\u0014\uffff\uffff" +
                    "\u0000\u00d7)\u0001\u0000\u0000\u0000\u00d8\u00d9\u0003(\u0014\u0000\u00d9" +
                    "\u00da\u0003(\u0014\u0000\u00da\u00db\u0003(\u0014\u0000\u00db\u00dc\u0003" +
                    "(\u0014\u0000\u00dc\u00dd\u0003(\u0014\u0000\u00dd\u00de\u0003(\u0014" +
                    "\u0000\u00de\u00df\u0003(\u0014\u0000\u00df\u00e0\u0003(\u0014\u0000\u00e0" +
                    "\u00e1\u0003(\u0014\u0000\u00e1\u00e2\u0003(\u0014\u0000\u00e2\u00e3\u0006" +
                    "\u0015\uffff\uffff\u0000\u00e3+\u0001\u0000\u0000\u0000\u00e4\u00e5\u0005" +
                    "\u0018\u0000\u0000\u00e5\u00e6\u0005(\u0000\u0000\u00e6\u00e7\u0005\"" +
                    "\u0000\u0000\u00e7\u00e8\u0005(\u0000\u0000\u00e8\u00e9\u0005\"\u0000" +
                    "\u0000\u00e9\u00ea\u0005(\u0000\u0000\u00ea\u00eb\u0006\u0016\uffff\uffff" +
                    "\u0000\u00eb-\u0001\u0000\u0000\u0000\u00ec\u00ed\u0005\u0019\u0000\u0000" +
                    "\u00ed\u00ee\u0005*\u0000\u0000\u00ee\u00ef\u0005\"\u0000\u0000\u00ef" +
                    "\u00f0\u0005*\u0000\u0000\u00f0\u00f1\u0005\"\u0000\u0000\u00f1\u00f2" +
                    "\u0005-\u0000\u0000\u00f2\u00f3\u0005\"\u0000\u0000\u00f3\u00f4\u0005" +
                    "(\u0000\u0000\u00f4\u00f5\u0005\"\u0000\u0000\u00f5\u00f6\u0005*\u0000" +
                    "\u0000\u00f6\u00f7\u0006\u0017\uffff\uffff\u0000\u00f7/\u0001\u0000\u0000" +
                    "\u0000\u00f8\u00f9\u0005\u001a\u0000\u0000\u00f9\u00fa\u0005.\u0000\u0000" +
                    "\u00fa\u00fb\u0005\"\u0000\u0000\u00fb\u00fc\u0005&\u0000\u0000\u00fc" +
                    "\u00fd\u0006\u0018\uffff\uffff\u0000\u00fd1\u0001\u0000\u0000\u0000\u00fe" +
                    "\u00ff\u0005\u001b\u0000\u0000\u00ff\u0100\u0005(\u0000\u0000\u0100\u0101" +
                    "\u0005\"\u0000\u0000\u0101\u0102\u0005(\u0000\u0000\u0102\u0103\u0005" +
                    "\"\u0000\u0000\u0103\u0104\u0005/\u0000\u0000\u0104\u0105\u0006\u0019" +
                    "\uffff\uffff\u0000\u01053\u0001\u0000\u0000\u0000\u0106\u0107\u00054\u0000" +
                    "\u0000\u0107\u0108\u0005\"\u0000\u0000\u0108\u0109\u0005(\u0000\u0000" +
                    "\u0109\u010a\u0005\"\u0000\u0000\u010a\u010b\u0005(\u0000\u0000\u010b" +
                    "\u010c\u0005\"\u0000\u0000\u010c\u010d\u0005(\u0000\u0000\u010d\u010e" +
                    "\u0005\"\u0000\u0000\u010e\u010f\u0005(\u0000\u0000\u010f\u0110\u0006" +
                    "\u001a\uffff\uffff\u0000\u01105\u0001\u0000\u0000\u0000\u0111\u0112\u0005" +
                    "\u001d\u0000\u0000\u0112\u0113\u0005)\u0000\u0000\u0113\u012e\u0006\u001b" +
                    "\uffff\uffff\u0000\u0114\u0115\u0005\u001d\u0000\u0000\u0115\u0116\u0005" +
                    ")\u0000\u0000\u0116\u011a\u0005\"\u0000\u0000\u0117\u011b\u0005+\u0000" +
                    "\u0000\u0118\u011b\u0005*\u0000\u0000\u0119\u011b\u0005)\u0000\u0000\u011a" +
                    "\u0117\u0001\u0000\u0000\u0000\u011a\u0118\u0001\u0000\u0000\u0000\u011a" +
                    "\u0119\u0001\u0000\u0000\u0000\u011b\u011c\u0001\u0000\u0000\u0000\u011c" +
                    "\u012e\u0006\u001b\uffff\uffff\u0000\u011d\u011e\u0005\u001d\u0000\u0000" +
                    "\u011e\u011f\u0005)\u0000\u0000\u011f\u0120\u0005\"\u0000\u0000\u0120" +
                    "\u0121\u0005)\u0000\u0000\u0121\u0122\u0005\"\u0000\u0000\u0122\u0123" +
                    "\u0005(\u0000\u0000\u0123\u012e\u0006\u001b\uffff\uffff\u0000\u0124\u0125" +
                    "\u0005\u001d\u0000\u0000\u0125\u0126\u0005)\u0000\u0000\u0126\u0127\u0005" +
                    "\"\u0000\u0000\u0127\u0128\u0005)\u0000\u0000\u0128\u0129\u0005\"\u0000" +
                    "\u0000\u0129\u012a\u0005(\u0000\u0000\u012a\u012b\u0005\"\u0000\u0000" +
                    "\u012b\u012c\u0005(\u0000\u0000\u012c\u012e\u0006\u001b\uffff\uffff\u0000" +
                    "\u012d\u0111\u0001\u0000\u0000\u0000\u012d\u0114\u0001\u0000\u0000\u0000" +
                    "\u012d\u011d\u0001\u0000\u0000\u0000\u012d\u0124\u0001\u0000\u0000\u0000" +
                    "\u012e7\u0001\u0000\u0000\u0000\u012f\u0130\u0005 \u0000\u0000\u0130\u0131" +
                    "\u0005*\u0000\u0000\u0131\u0132\u0006\u001c\uffff\uffff\u0000\u01329\u0001" +
                    "\u0000\u0000\u0000\u0133\u0134\u0005\u001e\u0000\u0000\u0134\u0135\u0005" +
                    "(\u0000\u0000\u0135\u0136\u0005\"\u0000\u0000\u0136\u0137\u0005(\u0000" +
                    "\u0000\u0137\u0138\u0006\u001d\uffff\uffff\u0000\u0138;\u0001\u0000\u0000" +
                    "\u0000\u0139\u013a\u00052\u0000\u0000\u013a\u013b\u0006\u001e\uffff\uffff" +
                    "\u0000\u013b=\u0001\u0000\u0000\u0000\u013c\u013d\u00036\u001b\u0000\u013d" +
                    "\u0141\u0006\u001f\uffff\uffff\u0000\u013e\u013f\u0003<\u001e\u0000\u013f" +
                    "\u0140\u0006\u001f\uffff\uffff\u0000\u0140\u0142\u0001\u0000\u0000\u0000" +
                    "\u0141\u013e\u0001\u0000\u0000\u0000\u0141\u0142\u0001\u0000\u0000\u0000" +
                    "\u0142\u0146\u0001\u0000\u0000\u0000\u0143\u0144\u0003:\u001d\u0000\u0144" +
                    "\u0145\u0006\u001f\uffff\uffff\u0000\u0145\u0147\u0001\u0000\u0000\u0000" +
                    "\u0146\u0143\u0001\u0000\u0000\u0000\u0146\u0147\u0001\u0000\u0000\u0000" +
                    "\u0147\u014b\u0001\u0000\u0000\u0000\u0148\u0149\u00038\u001c\u0000\u0149" +
                    "\u014a\u0006\u001f\uffff\uffff\u0000\u014a\u014c\u0001\u0000\u0000\u0000" +
                    "\u014b\u0148\u0001\u0000\u0000\u0000\u014b\u014c\u0001\u0000\u0000\u0000" +
                    "\u014c\u0152\u0001\u0000\u0000\u0000\u014d\u014e\u0003@ \u0000\u014e\u014f" +
                    "\u0006\u001f\uffff\uffff\u0000\u014f\u0151\u0001\u0000\u0000\u0000\u0150" +
                    "\u014d\u0001\u0000\u0000\u0000\u0151\u0154\u0001\u0000\u0000\u0000\u0152" +
                    "\u0150\u0001\u0000\u0000\u0000\u0152\u0153\u0001\u0000\u0000\u0000\u0153" +
                    "?\u0001\u0000\u0000\u0000\u0154\u0152\u0001\u0000\u0000\u0000\u0155\u0156" +
                    "\u0005\u001f\u0000\u0000\u0156\u0157\u0005\'\u0000\u0000\u0157\u0158\u0005" +
                    "\"\u0000\u0000\u0158\u0159\u0005)\u0000\u0000\u0159\u015a\u0005\"\u0000" +
                    "\u0000\u015a\u015e\u0006 \uffff\uffff\u0000\u015b\u015f\u00053\u0000\u0000" +
                    "\u015c\u015f\u0005#\u0000\u0000\u015d\u015f\u00051\u0000\u0000\u015e\u015b" +
                    "\u0001\u0000\u0000\u0000\u015e\u015c\u0001\u0000\u0000\u0000\u015e\u015d" +
                    "\u0001\u0000\u0000\u0000\u015f\u0160\u0001\u0000\u0000\u0000\u0160\u0161" +
                    "\u0005$\u0000\u0000\u0161\u0162\u0005(\u0000\u0000\u0162\u016e\u0006 " +
                    "\uffff\uffff\u0000\u0163\u0167\u0005$\u0000\u0000\u0164\u0168\u00053\u0000" +
                    "\u0000\u0165\u0168\u0005#\u0000\u0000\u0166\u0168\u00051\u0000\u0000\u0167" +
                    "\u0164\u0001\u0000\u0000\u0000\u0167\u0165\u0001\u0000\u0000\u0000\u0167" +
                    "\u0166\u0001\u0000\u0000\u0000\u0168\u0169\u0001\u0000\u0000\u0000\u0169" +
                    "\u016a\u0005$\u0000\u0000\u016a\u016b\u0005(\u0000\u0000\u016b\u016d\u0006" +
                    " \uffff\uffff\u0000\u016c\u0163\u0001\u0000\u0000\u0000\u016d\u0170\u0001" +
                    "\u0000\u0000\u0000\u016e\u016c\u0001\u0000\u0000\u0000\u016e\u016f\u0001" +
                    "\u0000\u0000\u0000\u016fA\u0001\u0000\u0000\u0000\u0170\u016e\u0001\u0000" +
                    "\u0000\u0000\u0171\u0172\u0005,\u0000\u0000\u0172\u0173\u0006!\uffff\uffff" +
                    "\u0000\u0173C\u0001\u0000\u0000\u0000\u0174\u0175\u00034\u001a\u0000\u0175" +
                    "\u0179\u0006\"\uffff\uffff\u0000\u0176\u0177\u0003>\u001f\u0000\u0177" +
                    "\u0178\u0006\"\uffff\uffff\u0000\u0178\u017a\u0001\u0000\u0000\u0000\u0179" +
                    "\u0176\u0001\u0000\u0000\u0000\u017a\u017b\u0001\u0000\u0000\u0000\u017b" +
                    "\u0179\u0001\u0000\u0000\u0000\u017b\u017c\u0001\u0000\u0000\u0000\u017c" +
                    "\u0182\u0001\u0000\u0000\u0000\u017d\u017e\u0003B!\u0000\u017e\u017f\u0006" +
                    "\"\uffff\uffff\u0000\u017f\u0181\u0001\u0000\u0000\u0000\u0180\u017d\u0001" +
                    "\u0000\u0000\u0000\u0181\u0184\u0001\u0000\u0000\u0000\u0182\u0180\u0001" +
                    "\u0000\u0000\u0000\u0182\u0183\u0001\u0000\u0000\u0000\u0183E\u0001\u0000" +
                    "\u0000\u0000\u0184\u0182\u0001\u0000\u0000\u0000\u0185\u0186\u0003.\u0017" +
                    "\u0000\u0186\u018a\u0006#\uffff\uffff\u0000\u0187\u0188\u00030\u0018\u0000" +
                    "\u0188\u0189\u0006#\uffff\uffff\u0000\u0189\u018b\u0001\u0000\u0000\u0000" +
                    "\u018a\u0187\u0001\u0000\u0000\u0000\u018a\u018b\u0001\u0000\u0000\u0000" +
                    "\u018b\u018f\u0001\u0000\u0000\u0000\u018c\u018d\u00032\u0019\u0000\u018d" +
                    "\u018e\u0006#\uffff\uffff\u0000\u018e\u0190\u0001\u0000\u0000\u0000\u018f" +
                    "\u018c\u0001\u0000\u0000\u0000\u018f\u0190\u0001\u0000\u0000\u0000\u0190" +
                    "\u0194\u0001\u0000\u0000\u0000\u0191\u0192\u0003 \u0010\u0000\u0192\u0193" +
                    "\u0006#\uffff\uffff\u0000\u0193\u0195\u0001\u0000\u0000\u0000\u0194\u0191" +
                    "\u0001\u0000\u0000\u0000\u0194\u0195\u0001\u0000\u0000\u0000\u0195\u0199" +
                    "\u0001\u0000\u0000\u0000\u0196\u0197\u0003D\"\u0000\u0197\u0198\u0006" +
                    "#\uffff\uffff\u0000\u0198\u019a\u0001\u0000\u0000\u0000\u0199\u0196\u0001" +
                    "\u0000\u0000\u0000\u019a\u019b\u0001\u0000\u0000\u0000\u019b\u0199\u0001" +
                    "\u0000\u0000\u0000\u019b\u019c\u0001\u0000\u0000\u0000\u019cG\u0001\u0000" +
                    "\u0000\u0000\u019d\u019e\u0003\u0000\u0000\u0000\u019e\u019f\u0006$\uffff" +
                    "\uffff\u0000\u019f\u01a0\u0003\u0002\u0001\u0000\u01a0\u01a1\u0006$\uffff" +
                    "\uffff\u0000\u01a1\u01a2\u0003\u0004\u0002\u0000\u01a2\u01a3\u0006$\uffff" +
                    "\uffff\u0000\u01a3\u01a4\u0003\u0006\u0003\u0000\u01a4\u01a5\u0006$\uffff" +
                    "\uffff\u0000\u01a5\u01a6\u0003\b\u0004\u0000\u01a6\u01a7\u0006$\uffff" +
                    "\uffff\u0000\u01a7\u01a8\u0003\n\u0005\u0000\u01a8\u01a9\u0006$\uffff" +
                    "\uffff\u0000\u01a9\u01aa\u0003\f\u0006\u0000\u01aa\u01ab\u0006$\uffff" +
                    "\uffff\u0000\u01ab\u01ac\u0003\u000e\u0007\u0000\u01ac\u01ad\u0006$\uffff" +
                    "\uffff\u0000\u01ad\u01ae\u0003\u0010\b\u0000\u01ae\u01af\u0006$\uffff" +
                    "\uffff\u0000\u01af\u01b0\u0003\u0012\t\u0000\u01b0\u01b1\u0006$\uffff" +
                    "\uffff\u0000\u01b1\u01b2\u0003\u0014\n\u0000\u01b2\u01b3\u0006$\uffff" +
                    "\uffff\u0000\u01b3\u01b4\u0003\u0016\u000b\u0000\u01b4\u01b5\u0006$\uffff" +
                    "\uffff\u0000\u01b5\u01b6\u0003\u0018\f\u0000\u01b6\u01b7\u0006$\uffff" +
                    "\uffff\u0000\u01b7\u01b8\u0003\u001a\r\u0000\u01b8\u01b9\u0006$\uffff" +
                    "\uffff\u0000\u01b9\u01ba\u0003\u001c\u000e\u0000\u01ba\u01bb\u0006$\uffff" +
                    "\uffff\u0000\u01bb\u01bc\u0003\u001e\u000f\u0000\u01bc\u01bd\u0006$\uffff" +
                    "\uffff\u0000\u01bd\u01be\u0003\"\u0011\u0000\u01be\u01c2\u0006$\uffff" +
                    "\uffff\u0000\u01bf\u01c0\u0003$\u0012\u0000\u01c0\u01c1\u0006$\uffff\uffff" +
                    "\u0000\u01c1\u01c3\u0001\u0000\u0000\u0000\u01c2\u01bf\u0001\u0000\u0000" +
                    "\u0000\u01c2\u01c3\u0001\u0000\u0000\u0000\u01c3\u01c4\u0001\u0000\u0000" +
                    "\u0000\u01c4\u01c5\u0003&\u0013\u0000\u01c5\u01c6\u0006$\uffff\uffff\u0000" +
                    "\u01c6\u01c7\u0003*\u0015\u0000\u01c7\u01d1\u0006$\uffff\uffff\u0000\u01c8" +
                    "\u01c9\u0003,\u0016\u0000\u01c9\u01cd\u0006$\uffff\uffff\u0000\u01ca\u01cb" +
                    "\u0003F#\u0000\u01cb\u01cc\u0006$\uffff\uffff\u0000\u01cc\u01ce\u0001" +
                    "\u0000\u0000\u0000\u01cd\u01ca\u0001\u0000\u0000\u0000\u01ce\u01cf\u0001" +
                    "\u0000\u0000\u0000\u01cf\u01cd\u0001\u0000\u0000\u0000\u01cf\u01d0\u0001" +
                    "\u0000\u0000\u0000\u01d0\u01d2\u0001\u0000\u0000\u0000\u01d1\u01c8\u0001" +
                    "\u0000\u0000\u0000\u01d1\u01d2\u0001\u0000\u0000\u0000\u01d2I\u0001\u0000" +
                    "\u0000\u0000\u01d3\u01d4\u0003H$\u0000\u01d4\u01d5\u0006%\uffff\uffff" +
                    "\u0000\u01d5\u01d7\u0001\u0000\u0000\u0000\u01d6\u01d3\u0001\u0000\u0000" +
                    "\u0000\u01d7\u01d8\u0001\u0000\u0000\u0000\u01d8\u01d6\u0001\u0000\u0000" +
                    "\u0000\u01d8\u01d9\u0001\u0000\u0000\u0000\u01d9\u01da\u0001\u0000\u0000" +
                    "\u0000\u01da\u01db\u0005\u0000\u0000\u0001\u01dbK\u0001\u0000\u0000\u0000" +
                    "\u0017\u00ad\u00bc\u00c6\u00d1\u011a\u012d\u0141\u0146\u014b\u0152\u015e" +
                    "\u0167\u016e\u017b\u0182\u018a\u018f\u0194\u019b\u01c2\u01cf\u01d1\u01d8";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
	}
}