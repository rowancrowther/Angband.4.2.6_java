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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/PlayerRace.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.playerrace;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.player.PlayerBody;
import uk.co.jackoftrades.middle.player.PlayerHistoryChart;
import uk.co.jackoftrades.middle.player.PlayerRace;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class PlayerRaceParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, NAME = 3, STATS = 4, SKILL_DISARM_PHYS = 5, SKILL_DISARM_MAGIC = 6,
            SKILL_DEVICE = 7, SKILL_SAVE = 8, SKILL_STEALTH = 9, SKILL_SEARCH = 10, SKILL_MELEE = 11,
            SKILL_SHOOT = 12, SKILL_THROW = 13, SKILL_DIG = 14, HITDIE = 15, EXP = 16, INFRAVISION = 17,
            HISTORY = 18, AGE = 19, HEIGHT = 20, WEIGHT = 21, OBJ_FLAGS = 22, PLAYER_FLAGS = 23,
            VALUES = 24, INTEGER = 25, STRING = 26, LBRACKET = 27, RBRACKET = 28, COLON = 29,
            OR = 30;
    public static final int
            RULE_name = 0, RULE_stats = 1, RULE_skill_disarm_phys = 2, RULE_skill_disarm_magic = 3,
            RULE_skill_device = 4, RULE_skill_save = 5, RULE_skill_stealth = 6, RULE_skill_search = 7,
            RULE_skill_melee = 8, RULE_skill_shoot = 9, RULE_skill_throw = 10, RULE_skill_dig = 11,
            RULE_hitdie = 12, RULE_exp = 13, RULE_infravision = 14, RULE_history = 15,
            RULE_age = 16, RULE_height = 17, RULE_weight = 18, RULE_obj_flags = 19,
            RULE_player_flags = 20, RULE_values = 21, RULE_race = 22, RULE_file = 23;

    private static String[] makeRuleNames() {
        return new String[]{
                "name", "stats", "skill_disarm_phys", "skill_disarm_magic", "skill_device",
                "skill_save", "skill_stealth", "skill_search", "skill_melee", "skill_shoot",
                "skill_throw", "skill_dig", "hitdie", "exp", "infravision", "history",
                "age", "height", "weight", "obj_flags", "player_flags", "values", "race",
                "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'name:'", "'stats:'", "'skill-disarm-phys:'", "'skill-disarm-magic:'",
                "'skill-device:'", "'skill-save:'", "'skill-stealth:'", "'skill-search:'",
                "'skill-melee:'", "'skill-shoot:'", "'skill-throw:'", "'skill-dig:'",
                "'hitdie:'", "'exp:'", "'infravision:'", "'history:'", "'age:'", "'height:'",
                "'weight:'", "'obj-flags:'", "'player-flags:'", "'values:'", null, null,
                "'['", "']'", "':'", "' | '"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "NAME", "STATS", "SKILL_DISARM_PHYS", "SKILL_DISARM_MAGIC",
                "SKILL_DEVICE", "SKILL_SAVE", "SKILL_STEALTH", "SKILL_SEARCH", "SKILL_MELEE",
                "SKILL_SHOOT", "SKILL_THROW", "SKILL_DIG", "HITDIE", "EXP", "INFRAVISION",
                "HISTORY", "AGE", "HEIGHT", "WEIGHT", "OBJ_FLAGS", "PLAYER_FLAGS", "VALUES",
                "INTEGER", "STRING", "LBRACKET", "RBRACKET", "COLON", "OR"
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
        return "PlayerRace.g4";
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

    public PlayerRaceParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class NameContext extends ParserRuleContext {
        public String nameStr;
        public Token STRING;

        public TerminalNode NAME() {
            return getToken(PlayerRaceParser.NAME, 0);
        }

        public TerminalNode STRING() {
            return getToken(PlayerRaceParser.STRING, 0);
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
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerRaceVisitor) return ((PlayerRaceVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(48);
                match(NAME);
                setState(49);
                ((NameContext) _localctx).STRING = match(STRING);
                ((NameContext) _localctx).nameStr = ((NameContext) _localctx).STRING.getText();
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
        public Map<Stats, Integer> statsMap;
        public Token str;
        public Token intVal;
        public Token wis;
        public Token dex;
        public Token con;

        public TerminalNode STATS() {
            return getToken(PlayerRaceParser.STATS, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(PlayerRaceParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(PlayerRaceParser.COLON, i);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerRaceParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerRaceParser.INTEGER, i);
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
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).enterStats(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).exitStats(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerRaceVisitor)
                return ((PlayerRaceVisitor<? extends T>) visitor).visitStats(this);
            else return visitor.visitChildren(this);
        }
    }

    public final StatsContext stats() throws RecognitionException {
        StatsContext _localctx = new StatsContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_stats);

        ((StatsContext) _localctx).statsMap = new HashMap<>();

        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(52);
                match(STATS);
                setState(53);
                ((StatsContext) _localctx).str = match(INTEGER);
                setState(54);
                match(COLON);
                setState(55);
                ((StatsContext) _localctx).intVal = match(INTEGER);
                setState(56);
                match(COLON);
                setState(57);
                ((StatsContext) _localctx).wis = match(INTEGER);
                setState(58);
                match(COLON);
                setState(59);
                ((StatsContext) _localctx).dex = match(INTEGER);
                setState(60);
                match(COLON);
                setState(61);
                ((StatsContext) _localctx).con = match(INTEGER);

                _localctx.statsMap.put(Stats.STAT_STR, Integer.parseInt(((StatsContext) _localctx).str.getText()));
                _localctx.statsMap.put(Stats.STAT_INT, Integer.parseInt(((StatsContext) _localctx).intVal.getText()));
                _localctx.statsMap.put(Stats.STAT_WIS, Integer.parseInt(((StatsContext) _localctx).wis.getText()));
                _localctx.statsMap.put(Stats.STAT_DEX, Integer.parseInt(((StatsContext) _localctx).dex.getText()));
                _localctx.statsMap.put(Stats.STAT_CON, Integer.parseInt(((StatsContext) _localctx).con.getText()));

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
    public static class Skill_disarm_physContext extends ParserRuleContext {
        public int value;
        public Token INTEGER;

        public TerminalNode SKILL_DISARM_PHYS() {
            return getToken(PlayerRaceParser.SKILL_DISARM_PHYS, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerRaceParser.INTEGER, 0);
        }

        public Skill_disarm_physContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skill_disarm_phys;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).enterSkill_disarm_phys(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).exitSkill_disarm_phys(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerRaceVisitor)
                return ((PlayerRaceVisitor<? extends T>) visitor).visitSkill_disarm_phys(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Skill_disarm_physContext skill_disarm_phys() throws RecognitionException {
        Skill_disarm_physContext _localctx = new Skill_disarm_physContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_skill_disarm_phys);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(64);
                match(SKILL_DISARM_PHYS);
                setState(65);
                ((Skill_disarm_physContext) _localctx).INTEGER = match(INTEGER);
                ((Skill_disarm_physContext) _localctx).value = Integer.parseInt(((Skill_disarm_physContext) _localctx).INTEGER.getText());
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
    public static class Skill_disarm_magicContext extends ParserRuleContext {
        public int value;
        public Token INTEGER;

        public TerminalNode SKILL_DISARM_MAGIC() {
            return getToken(PlayerRaceParser.SKILL_DISARM_MAGIC, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerRaceParser.INTEGER, 0);
        }

        public Skill_disarm_magicContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skill_disarm_magic;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).enterSkill_disarm_magic(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).exitSkill_disarm_magic(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerRaceVisitor)
                return ((PlayerRaceVisitor<? extends T>) visitor).visitSkill_disarm_magic(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Skill_disarm_magicContext skill_disarm_magic() throws RecognitionException {
        Skill_disarm_magicContext _localctx = new Skill_disarm_magicContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_skill_disarm_magic);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(68);
                match(SKILL_DISARM_MAGIC);
                setState(69);
                ((Skill_disarm_magicContext) _localctx).INTEGER = match(INTEGER);
                ((Skill_disarm_magicContext) _localctx).value = Integer.parseInt(((Skill_disarm_magicContext) _localctx).INTEGER.getText());
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
    public static class Skill_deviceContext extends ParserRuleContext {
        public int value;
        public Token INTEGER;

        public TerminalNode SKILL_DEVICE() {
            return getToken(PlayerRaceParser.SKILL_DEVICE, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerRaceParser.INTEGER, 0);
        }

        public Skill_deviceContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skill_device;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).enterSkill_device(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).exitSkill_device(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerRaceVisitor)
                return ((PlayerRaceVisitor<? extends T>) visitor).visitSkill_device(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Skill_deviceContext skill_device() throws RecognitionException {
        Skill_deviceContext _localctx = new Skill_deviceContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_skill_device);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(72);
                match(SKILL_DEVICE);
                setState(73);
                ((Skill_deviceContext) _localctx).INTEGER = match(INTEGER);
                ((Skill_deviceContext) _localctx).value = Integer.parseInt(((Skill_deviceContext) _localctx).INTEGER.getText());
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
    public static class Skill_saveContext extends ParserRuleContext {
        public int value;
        public Token INTEGER;

        public TerminalNode SKILL_SAVE() {
            return getToken(PlayerRaceParser.SKILL_SAVE, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerRaceParser.INTEGER, 0);
        }

        public Skill_saveContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skill_save;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).enterSkill_save(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).exitSkill_save(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerRaceVisitor)
                return ((PlayerRaceVisitor<? extends T>) visitor).visitSkill_save(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Skill_saveContext skill_save() throws RecognitionException {
        Skill_saveContext _localctx = new Skill_saveContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_skill_save);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(76);
                match(SKILL_SAVE);
                setState(77);
                ((Skill_saveContext) _localctx).INTEGER = match(INTEGER);
                ((Skill_saveContext) _localctx).value = Integer.parseInt(((Skill_saveContext) _localctx).INTEGER.getText());
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
    public static class Skill_stealthContext extends ParserRuleContext {
        public int value;
        public Token INTEGER;

        public TerminalNode SKILL_STEALTH() {
            return getToken(PlayerRaceParser.SKILL_STEALTH, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerRaceParser.INTEGER, 0);
        }

        public Skill_stealthContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skill_stealth;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).enterSkill_stealth(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).exitSkill_stealth(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerRaceVisitor)
                return ((PlayerRaceVisitor<? extends T>) visitor).visitSkill_stealth(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Skill_stealthContext skill_stealth() throws RecognitionException {
        Skill_stealthContext _localctx = new Skill_stealthContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_skill_stealth);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(80);
                match(SKILL_STEALTH);
                setState(81);
                ((Skill_stealthContext) _localctx).INTEGER = match(INTEGER);
                ((Skill_stealthContext) _localctx).value = Integer.parseInt(((Skill_stealthContext) _localctx).INTEGER.getText());
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
    public static class Skill_searchContext extends ParserRuleContext {
        public int value;
        public Token INTEGER;

        public TerminalNode SKILL_SEARCH() {
            return getToken(PlayerRaceParser.SKILL_SEARCH, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerRaceParser.INTEGER, 0);
        }

        public Skill_searchContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skill_search;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).enterSkill_search(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).exitSkill_search(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerRaceVisitor)
                return ((PlayerRaceVisitor<? extends T>) visitor).visitSkill_search(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Skill_searchContext skill_search() throws RecognitionException {
        Skill_searchContext _localctx = new Skill_searchContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_skill_search);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(84);
                match(SKILL_SEARCH);
                setState(85);
                ((Skill_searchContext) _localctx).INTEGER = match(INTEGER);
                ((Skill_searchContext) _localctx).value = Integer.parseInt(((Skill_searchContext) _localctx).INTEGER.getText());
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
    public static class Skill_meleeContext extends ParserRuleContext {
        public int value;
        public Token INTEGER;

        public TerminalNode SKILL_MELEE() {
            return getToken(PlayerRaceParser.SKILL_MELEE, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerRaceParser.INTEGER, 0);
        }

        public Skill_meleeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skill_melee;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).enterSkill_melee(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).exitSkill_melee(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerRaceVisitor)
                return ((PlayerRaceVisitor<? extends T>) visitor).visitSkill_melee(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Skill_meleeContext skill_melee() throws RecognitionException {
        Skill_meleeContext _localctx = new Skill_meleeContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_skill_melee);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(88);
                match(SKILL_MELEE);
                setState(89);
                ((Skill_meleeContext) _localctx).INTEGER = match(INTEGER);
                ((Skill_meleeContext) _localctx).value = Integer.parseInt(((Skill_meleeContext) _localctx).INTEGER.getText());
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
    public static class Skill_shootContext extends ParserRuleContext {
        public int value;
        public Token INTEGER;

        public TerminalNode SKILL_SHOOT() {
            return getToken(PlayerRaceParser.SKILL_SHOOT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerRaceParser.INTEGER, 0);
        }

        public Skill_shootContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skill_shoot;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).enterSkill_shoot(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).exitSkill_shoot(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerRaceVisitor)
                return ((PlayerRaceVisitor<? extends T>) visitor).visitSkill_shoot(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Skill_shootContext skill_shoot() throws RecognitionException {
        Skill_shootContext _localctx = new Skill_shootContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_skill_shoot);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(92);
                match(SKILL_SHOOT);
                setState(93);
                ((Skill_shootContext) _localctx).INTEGER = match(INTEGER);
                ((Skill_shootContext) _localctx).value = Integer.parseInt(((Skill_shootContext) _localctx).INTEGER.getText());
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
    public static class Skill_throwContext extends ParserRuleContext {
        public int value;
        public Token INTEGER;

        public TerminalNode SKILL_THROW() {
            return getToken(PlayerRaceParser.SKILL_THROW, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerRaceParser.INTEGER, 0);
        }

        public Skill_throwContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skill_throw;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).enterSkill_throw(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).exitSkill_throw(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerRaceVisitor)
                return ((PlayerRaceVisitor<? extends T>) visitor).visitSkill_throw(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Skill_throwContext skill_throw() throws RecognitionException {
        Skill_throwContext _localctx = new Skill_throwContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_skill_throw);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(96);
                match(SKILL_THROW);
                setState(97);
                ((Skill_throwContext) _localctx).INTEGER = match(INTEGER);
                ((Skill_throwContext) _localctx).value = Integer.parseInt(((Skill_throwContext) _localctx).INTEGER.getText());
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
    public static class Skill_digContext extends ParserRuleContext {
        public int value;
        public Token INTEGER;

        public TerminalNode SKILL_DIG() {
            return getToken(PlayerRaceParser.SKILL_DIG, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerRaceParser.INTEGER, 0);
        }

        public Skill_digContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skill_dig;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).enterSkill_dig(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).exitSkill_dig(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerRaceVisitor)
                return ((PlayerRaceVisitor<? extends T>) visitor).visitSkill_dig(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Skill_digContext skill_dig() throws RecognitionException {
        Skill_digContext _localctx = new Skill_digContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_skill_dig);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(100);
                match(SKILL_DIG);
                setState(101);
                ((Skill_digContext) _localctx).INTEGER = match(INTEGER);
                ((Skill_digContext) _localctx).value = Integer.parseInt(((Skill_digContext) _localctx).INTEGER.getText());
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
        public int die;
        public Token INTEGER;

        public TerminalNode HITDIE() {
            return getToken(PlayerRaceParser.HITDIE, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerRaceParser.INTEGER, 0);
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
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).enterHitdie(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).exitHitdie(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerRaceVisitor)
                return ((PlayerRaceVisitor<? extends T>) visitor).visitHitdie(this);
            else return visitor.visitChildren(this);
        }
    }

    public final HitdieContext hitdie() throws RecognitionException {
        HitdieContext _localctx = new HitdieContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_hitdie);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(104);
                match(HITDIE);
                setState(105);
                ((HitdieContext) _localctx).INTEGER = match(INTEGER);
                ((HitdieContext) _localctx).die = Integer.parseInt(((HitdieContext) _localctx).INTEGER.getText());
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
        public int expInt;
        public Token INTEGER;

        public TerminalNode EXP() {
            return getToken(PlayerRaceParser.EXP, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerRaceParser.INTEGER, 0);
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
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).enterExp(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).exitExp(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerRaceVisitor) return ((PlayerRaceVisitor<? extends T>) visitor).visitExp(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExpContext exp() throws RecognitionException {
        ExpContext _localctx = new ExpContext(_ctx, getState());
        enterRule(_localctx, 26, RULE_exp);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(108);
                match(EXP);
                setState(109);
                ((ExpContext) _localctx).INTEGER = match(INTEGER);
                ((ExpContext) _localctx).expInt = Integer.parseInt(((ExpContext) _localctx).INTEGER.getText());
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
    public static class InfravisionContext extends ParserRuleContext {
        public int infraInt;
        public Token INTEGER;

        public TerminalNode INFRAVISION() {
            return getToken(PlayerRaceParser.INFRAVISION, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerRaceParser.INTEGER, 0);
        }

        public InfravisionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_infravision;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).enterInfravision(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).exitInfravision(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerRaceVisitor)
                return ((PlayerRaceVisitor<? extends T>) visitor).visitInfravision(this);
            else return visitor.visitChildren(this);
        }
    }

    public final InfravisionContext infravision() throws RecognitionException {
        InfravisionContext _localctx = new InfravisionContext(_ctx, getState());
        enterRule(_localctx, 28, RULE_infravision);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(112);
                match(INFRAVISION);
                setState(113);
                ((InfravisionContext) _localctx).INTEGER = match(INTEGER);
                ((InfravisionContext) _localctx).infraInt = Integer.parseInt(((InfravisionContext) _localctx).INTEGER.getText());
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
    public static class HistoryContext extends ParserRuleContext {
        public int histInt;
        public Token INTEGER;

        public TerminalNode HISTORY() {
            return getToken(PlayerRaceParser.HISTORY, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(PlayerRaceParser.INTEGER, 0);
        }

        public HistoryContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_history;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).enterHistory(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).exitHistory(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerRaceVisitor)
                return ((PlayerRaceVisitor<? extends T>) visitor).visitHistory(this);
            else return visitor.visitChildren(this);
        }
    }

    public final HistoryContext history() throws RecognitionException {
        HistoryContext _localctx = new HistoryContext(_ctx, getState());
        enterRule(_localctx, 30, RULE_history);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(116);
                match(HISTORY);
                setState(117);
                ((HistoryContext) _localctx).INTEGER = match(INTEGER);
                ((HistoryContext) _localctx).histInt = Integer.parseInt(((HistoryContext) _localctx).INTEGER.getText());
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
    public static class AgeContext extends ParserRuleContext {
        public int ageBase;
        public int ageMod;
        public Token base;
        public Token mod;

        public TerminalNode AGE() {
            return getToken(PlayerRaceParser.AGE, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerRaceParser.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerRaceParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerRaceParser.INTEGER, i);
        }

        public AgeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_age;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).enterAge(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).exitAge(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerRaceVisitor) return ((PlayerRaceVisitor<? extends T>) visitor).visitAge(this);
            else return visitor.visitChildren(this);
        }
    }

    public final AgeContext age() throws RecognitionException {
        AgeContext _localctx = new AgeContext(_ctx, getState());
        enterRule(_localctx, 32, RULE_age);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(120);
                match(AGE);
                setState(121);
                ((AgeContext) _localctx).base = match(INTEGER);
                setState(122);
                match(COLON);
                setState(123);
                ((AgeContext) _localctx).mod = match(INTEGER);

                ((AgeContext) _localctx).ageBase = Integer.parseInt(((AgeContext) _localctx).base.getText());
                ((AgeContext) _localctx).ageMod = Integer.parseInt(((AgeContext) _localctx).mod.getText());

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
    public static class HeightContext extends ParserRuleContext {
        public int heightBase;
        public int heightMod;
        public Token base;
        public Token mod;

        public TerminalNode HEIGHT() {
            return getToken(PlayerRaceParser.HEIGHT, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerRaceParser.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerRaceParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerRaceParser.INTEGER, i);
        }

        public HeightContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_height;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).enterHeight(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).exitHeight(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerRaceVisitor)
                return ((PlayerRaceVisitor<? extends T>) visitor).visitHeight(this);
            else return visitor.visitChildren(this);
        }
    }

    public final HeightContext height() throws RecognitionException {
        HeightContext _localctx = new HeightContext(_ctx, getState());
        enterRule(_localctx, 34, RULE_height);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(126);
                match(HEIGHT);
                setState(127);
                ((HeightContext) _localctx).base = match(INTEGER);
                setState(128);
                match(COLON);
                setState(129);
                ((HeightContext) _localctx).mod = match(INTEGER);

                ((HeightContext) _localctx).heightBase = Integer.parseInt(((HeightContext) _localctx).base.getText());
                ((HeightContext) _localctx).heightMod = Integer.parseInt(((HeightContext) _localctx).mod.getText());

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
    public static class WeightContext extends ParserRuleContext {
        public int weightBase;
        public int weightMod;
        public Token base;
        public Token mod;

        public TerminalNode WEIGHT() {
            return getToken(PlayerRaceParser.WEIGHT, 0);
        }

        public TerminalNode COLON() {
            return getToken(PlayerRaceParser.COLON, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerRaceParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerRaceParser.INTEGER, i);
        }

        public WeightContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_weight;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).enterWeight(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).exitWeight(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerRaceVisitor)
                return ((PlayerRaceVisitor<? extends T>) visitor).visitWeight(this);
            else return visitor.visitChildren(this);
        }
    }

    public final WeightContext weight() throws RecognitionException {
        WeightContext _localctx = new WeightContext(_ctx, getState());
        enterRule(_localctx, 36, RULE_weight);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(132);
                match(WEIGHT);
                setState(133);
                ((WeightContext) _localctx).base = match(INTEGER);
                setState(134);
                match(COLON);
                setState(135);
                ((WeightContext) _localctx).mod = match(INTEGER);

                ((WeightContext) _localctx).weightBase = Integer.parseInt(((WeightContext) _localctx).base.getText());
                ((WeightContext) _localctx).weightMod = Integer.parseInt(((WeightContext) _localctx).mod.getText());

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
    public static class Obj_flagsContext extends ParserRuleContext {
        public Flag<ObjectFlag> flags;
        public Token flag1;
        public Token flag2;

        public TerminalNode OBJ_FLAGS() {
            return getToken(PlayerRaceParser.OBJ_FLAGS, 0);
        }

        public List<TerminalNode> STRING() {
            return getTokens(PlayerRaceParser.STRING);
        }

        public TerminalNode STRING(int i) {
            return getToken(PlayerRaceParser.STRING, i);
        }

        public List<TerminalNode> OR() {
            return getTokens(PlayerRaceParser.OR);
        }

        public TerminalNode OR(int i) {
            return getToken(PlayerRaceParser.OR, i);
        }

        public Obj_flagsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_obj_flags;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).enterObj_flags(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).exitObj_flags(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerRaceVisitor)
                return ((PlayerRaceVisitor<? extends T>) visitor).visitObj_flags(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Obj_flagsContext obj_flags() throws RecognitionException {
        Obj_flagsContext _localctx = new Obj_flagsContext(_ctx, getState());
        enterRule(_localctx, 38, RULE_obj_flags);

        ((Obj_flagsContext) _localctx).flags = new Flag<>(ObjectFlag.class);

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(138);
                match(OBJ_FLAGS);
                setState(139);
                ((Obj_flagsContext) _localctx).flag1 = match(STRING);

                String raw1 = ((Obj_flagsContext) _localctx).flag1.getText().toUpperCase().trim();
                ObjectFlag of1 = ObjectFlag.valueOf("OF_" + raw1);
                _localctx.flags.on(of1);

                setState(146);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == OR) {
                    {
                        {
                            setState(141);
                            match(OR);
                            setState(142);
                            ((Obj_flagsContext) _localctx).flag2 = match(STRING);

                            String raw2 = ((Obj_flagsContext) _localctx).flag2.getText().toUpperCase().trim();
                            ObjectFlag of2 = ObjectFlag.valueOf("OF_" + raw2);
                            _localctx.flags.on(of2);

                        }
                    }
                    setState(148);
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
    public static class Player_flagsContext extends ParserRuleContext {
        public Flag<PlayerFlag> flags;
        public Token flag1;
        public Token flag2;

        public TerminalNode PLAYER_FLAGS() {
            return getToken(PlayerRaceParser.PLAYER_FLAGS, 0);
        }

        public List<TerminalNode> STRING() {
            return getTokens(PlayerRaceParser.STRING);
        }

        public TerminalNode STRING(int i) {
            return getToken(PlayerRaceParser.STRING, i);
        }

        public List<TerminalNode> OR() {
            return getTokens(PlayerRaceParser.OR);
        }

        public TerminalNode OR(int i) {
            return getToken(PlayerRaceParser.OR, i);
        }

        public Player_flagsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_player_flags;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).enterPlayer_flags(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).exitPlayer_flags(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerRaceVisitor)
                return ((PlayerRaceVisitor<? extends T>) visitor).visitPlayer_flags(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Player_flagsContext player_flags() throws RecognitionException {
        Player_flagsContext _localctx = new Player_flagsContext(_ctx, getState());
        enterRule(_localctx, 40, RULE_player_flags);

        ((Player_flagsContext) _localctx).flags = new Flag<>(PlayerFlag.class);

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(149);
                match(PLAYER_FLAGS);
                setState(150);
                ((Player_flagsContext) _localctx).flag1 = match(STRING);

                String raw1 = ((Player_flagsContext) _localctx).flag1.getText().toUpperCase().trim();
                PlayerFlag pf1 = PlayerFlag.valueOf("PF_" + raw1);
                _localctx.flags.on(pf1);

                setState(157);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == OR) {
                    {
                        {
                            setState(152);
                            match(OR);
                            setState(153);
                            ((Player_flagsContext) _localctx).flag2 = match(STRING);

                            String raw2 = ((Player_flagsContext) _localctx).flag2.getText().toUpperCase().trim();
                            PlayerFlag pf2 = PlayerFlag.valueOf("PF_" + raw2);
                            _localctx.flags.on(pf2);

                        }
                    }
                    setState(159);
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
    public static class ValuesContext extends ParserRuleContext {
        public Map<ObjectModifier, Integer> modifiers;
        public Token valName1;
        public Token valInt1;
        public Token valName2;
        public Token valInt2;

        public TerminalNode VALUES() {
            return getToken(PlayerRaceParser.VALUES, 0);
        }

        public List<TerminalNode> LBRACKET() {
            return getTokens(PlayerRaceParser.LBRACKET);
        }

        public TerminalNode LBRACKET(int i) {
            return getToken(PlayerRaceParser.LBRACKET, i);
        }

        public List<TerminalNode> RBRACKET() {
            return getTokens(PlayerRaceParser.RBRACKET);
        }

        public TerminalNode RBRACKET(int i) {
            return getToken(PlayerRaceParser.RBRACKET, i);
        }

        public List<TerminalNode> STRING() {
            return getTokens(PlayerRaceParser.STRING);
        }

        public TerminalNode STRING(int i) {
            return getToken(PlayerRaceParser.STRING, i);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(PlayerRaceParser.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(PlayerRaceParser.INTEGER, i);
        }

        public List<TerminalNode> OR() {
            return getTokens(PlayerRaceParser.OR);
        }

        public TerminalNode OR(int i) {
            return getToken(PlayerRaceParser.OR, i);
        }

        public ValuesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_values;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).enterValues(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).exitValues(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerRaceVisitor)
                return ((PlayerRaceVisitor<? extends T>) visitor).visitValues(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ValuesContext values() throws RecognitionException {
        ValuesContext _localctx = new ValuesContext(_ctx, getState());
        enterRule(_localctx, 42, RULE_values);

        ((ValuesContext) _localctx).modifiers = new HashMap<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(160);
                match(VALUES);
                setState(161);
                ((ValuesContext) _localctx).valName1 = match(STRING);
                setState(162);
                match(LBRACKET);
                setState(163);
                ((ValuesContext) _localctx).valInt1 = match(INTEGER);
                setState(164);
                match(RBRACKET);

                String raw1 = ((ValuesContext) _localctx).valName1.getText().toUpperCase().trim();
                ObjectModifier om1 = ObjectModifier.valueOf("OM_" + raw1);
                int val1 = Integer.parseInt(((ValuesContext) _localctx).valInt1.getText());
                _localctx.modifiers.put(om1, val1);

                setState(174);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == OR) {
                    {
                        {
                            setState(166);
                            match(OR);
                            setState(167);
                            ((ValuesContext) _localctx).valName2 = match(STRING);
                            setState(168);
                            match(LBRACKET);
                            setState(169);
                            ((ValuesContext) _localctx).valInt2 = match(INTEGER);
                            setState(170);
                            match(RBRACKET);

                            String raw2 = ((ValuesContext) _localctx).valName2.getText().toUpperCase().trim();
                            ObjectModifier om2 = ObjectModifier.valueOf("OM_" + raw2);
                            int val2 = Integer.parseInt(((ValuesContext) _localctx).valInt2.getText());
                            _localctx.modifiers.put(om2, val2);

                        }
                    }
                    setState(176);
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
    public static class RaceContext extends ParserRuleContext {
        public PlayerRace playerRace;
        public NameContext name;
        public StatsContext stats;
        public Skill_disarm_physContext skill_disarm_phys;
        public Skill_disarm_magicContext skill_disarm_magic;
        public Skill_deviceContext skill_device;
        public Skill_saveContext skill_save;
        public Skill_stealthContext skill_stealth;
        public Skill_searchContext skill_search;
        public Skill_meleeContext skill_melee;
        public Skill_shootContext skill_shoot;
        public Skill_throwContext skill_throw;
        public Skill_digContext skill_dig;
        public HitdieContext hitdie;
        public ExpContext exp;
        public InfravisionContext infravision;
        public HistoryContext history;
        public AgeContext age;
        public HeightContext height;
        public WeightContext weight;
        public Obj_flagsContext obj_flags;
        public Player_flagsContext player_flags;
        public ValuesContext values;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public List<StatsContext> stats() {
            return getRuleContexts(StatsContext.class);
        }

        public StatsContext stats(int i) {
            return getRuleContext(StatsContext.class, i);
        }

        public List<Skill_disarm_physContext> skill_disarm_phys() {
            return getRuleContexts(Skill_disarm_physContext.class);
        }

        public Skill_disarm_physContext skill_disarm_phys(int i) {
            return getRuleContext(Skill_disarm_physContext.class, i);
        }

        public List<Skill_disarm_magicContext> skill_disarm_magic() {
            return getRuleContexts(Skill_disarm_magicContext.class);
        }

        public Skill_disarm_magicContext skill_disarm_magic(int i) {
            return getRuleContext(Skill_disarm_magicContext.class, i);
        }

        public List<Skill_deviceContext> skill_device() {
            return getRuleContexts(Skill_deviceContext.class);
        }

        public Skill_deviceContext skill_device(int i) {
            return getRuleContext(Skill_deviceContext.class, i);
        }

        public List<Skill_saveContext> skill_save() {
            return getRuleContexts(Skill_saveContext.class);
        }

        public Skill_saveContext skill_save(int i) {
            return getRuleContext(Skill_saveContext.class, i);
        }

        public List<Skill_stealthContext> skill_stealth() {
            return getRuleContexts(Skill_stealthContext.class);
        }

        public Skill_stealthContext skill_stealth(int i) {
            return getRuleContext(Skill_stealthContext.class, i);
        }

        public List<Skill_searchContext> skill_search() {
            return getRuleContexts(Skill_searchContext.class);
        }

        public Skill_searchContext skill_search(int i) {
            return getRuleContext(Skill_searchContext.class, i);
        }

        public List<Skill_meleeContext> skill_melee() {
            return getRuleContexts(Skill_meleeContext.class);
        }

        public Skill_meleeContext skill_melee(int i) {
            return getRuleContext(Skill_meleeContext.class, i);
        }

        public List<Skill_shootContext> skill_shoot() {
            return getRuleContexts(Skill_shootContext.class);
        }

        public Skill_shootContext skill_shoot(int i) {
            return getRuleContext(Skill_shootContext.class, i);
        }

        public List<Skill_throwContext> skill_throw() {
            return getRuleContexts(Skill_throwContext.class);
        }

        public Skill_throwContext skill_throw(int i) {
            return getRuleContext(Skill_throwContext.class, i);
        }

        public List<Skill_digContext> skill_dig() {
            return getRuleContexts(Skill_digContext.class);
        }

        public Skill_digContext skill_dig(int i) {
            return getRuleContext(Skill_digContext.class, i);
        }

        public List<HitdieContext> hitdie() {
            return getRuleContexts(HitdieContext.class);
        }

        public HitdieContext hitdie(int i) {
            return getRuleContext(HitdieContext.class, i);
        }

        public List<ExpContext> exp() {
            return getRuleContexts(ExpContext.class);
        }

        public ExpContext exp(int i) {
            return getRuleContext(ExpContext.class, i);
        }

        public List<InfravisionContext> infravision() {
            return getRuleContexts(InfravisionContext.class);
        }

        public InfravisionContext infravision(int i) {
            return getRuleContext(InfravisionContext.class, i);
        }

        public List<HistoryContext> history() {
            return getRuleContexts(HistoryContext.class);
        }

        public HistoryContext history(int i) {
            return getRuleContext(HistoryContext.class, i);
        }

        public List<AgeContext> age() {
            return getRuleContexts(AgeContext.class);
        }

        public AgeContext age(int i) {
            return getRuleContext(AgeContext.class, i);
        }

        public List<HeightContext> height() {
            return getRuleContexts(HeightContext.class);
        }

        public HeightContext height(int i) {
            return getRuleContext(HeightContext.class, i);
        }

        public List<WeightContext> weight() {
            return getRuleContexts(WeightContext.class);
        }

        public WeightContext weight(int i) {
            return getRuleContext(WeightContext.class, i);
        }

        public List<Obj_flagsContext> obj_flags() {
            return getRuleContexts(Obj_flagsContext.class);
        }

        public Obj_flagsContext obj_flags(int i) {
            return getRuleContext(Obj_flagsContext.class, i);
        }

        public List<Player_flagsContext> player_flags() {
            return getRuleContexts(Player_flagsContext.class);
        }

        public Player_flagsContext player_flags(int i) {
            return getRuleContext(Player_flagsContext.class, i);
        }

        public List<ValuesContext> values() {
            return getRuleContexts(ValuesContext.class);
        }

        public ValuesContext values(int i) {
            return getRuleContext(ValuesContext.class, i);
        }

        public RaceContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_race;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).enterRace(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).exitRace(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerRaceVisitor) return ((PlayerRaceVisitor<? extends T>) visitor).visitRace(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RaceContext race() throws RecognitionException {
        RaceContext _localctx = new RaceContext(_ctx, getState());
        enterRule(_localctx, 44, RULE_race);

        String nameInit = "";
        int rIndexInit = 0;
        Map<Stats, Integer> statsInit = new HashMap<>();
        Map<PlayerSkill, Integer> skillsInit = new HashMap<>();
        int hitDieInit = 0;
        int expInit = 0;
        int infraInit = 0;
        int bodyIntInit = 0;
        int historyInit = 0;
        int ageBaseInit = 0;
        int ageModInit = 0;
        int heightBaseInit = 0;
        int heightModInit = 0;
        int weightBaseInit = 0;
        int weightModInit = 0;
        Flag<ObjectFlag> oFlagsInit = new Flag<>(ObjectFlag.class);
        Flag<PlayerFlag> pFlagsInit = new Flag<>(PlayerFlag.class);
        Map<ObjectModifier, Integer> valuesInit = new HashMap<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(177);
                ((RaceContext) _localctx).name = name();
                nameInit = ((RaceContext) _localctx).name.nameStr;
                setState(242);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        setState(242);
                        _errHandler.sync(this);
                        switch (_input.LA(1)) {
                            case STATS: {
                                setState(179);
                                ((RaceContext) _localctx).stats = stats();
                                statsInit = ((RaceContext) _localctx).stats.statsMap;
                            }
                            break;
                            case SKILL_DISARM_PHYS: {
                                setState(182);
                                ((RaceContext) _localctx).skill_disarm_phys = skill_disarm_phys();
                                skillsInit.put(PlayerSkill.SKILL_DISARM_PHYS, ((RaceContext) _localctx).skill_disarm_phys.value);
                            }
                            break;
                            case SKILL_DISARM_MAGIC: {
                                setState(185);
                                ((RaceContext) _localctx).skill_disarm_magic = skill_disarm_magic();
                                skillsInit.put(PlayerSkill.SKILL_DISARM_MAGIC, ((RaceContext) _localctx).skill_disarm_magic.value);
                            }
                            break;
                            case SKILL_DEVICE: {
                                setState(188);
                                ((RaceContext) _localctx).skill_device = skill_device();
                                skillsInit.put(PlayerSkill.SKILL_DEVICE, ((RaceContext) _localctx).skill_device.value);
                            }
                            break;
                            case SKILL_SAVE: {
                                setState(191);
                                ((RaceContext) _localctx).skill_save = skill_save();
                                skillsInit.put(PlayerSkill.SKILL_SAVE, ((RaceContext) _localctx).skill_save.value);
                            }
                            break;
                            case SKILL_STEALTH: {
                                setState(194);
                                ((RaceContext) _localctx).skill_stealth = skill_stealth();
                                skillsInit.put(PlayerSkill.SKILL_STEALTH, ((RaceContext) _localctx).skill_stealth.value);
                            }
                            break;
                            case SKILL_SEARCH: {
                                setState(197);
                                ((RaceContext) _localctx).skill_search = skill_search();
                                skillsInit.put(PlayerSkill.SKILL_SEARCH, ((RaceContext) _localctx).skill_search.value);
                            }
                            break;
                            case SKILL_MELEE: {
                                setState(200);
                                ((RaceContext) _localctx).skill_melee = skill_melee();
                                skillsInit.put(PlayerSkill.SKILL_TO_HIT_MELEE, ((RaceContext) _localctx).skill_melee.value);
                            }
                            break;
                            case SKILL_SHOOT: {
                                setState(203);
                                ((RaceContext) _localctx).skill_shoot = skill_shoot();
                                skillsInit.put(PlayerSkill.SKILL_TO_HIT_BOW, ((RaceContext) _localctx).skill_shoot.value);
                            }
                            break;
                            case SKILL_THROW: {
                                setState(206);
                                ((RaceContext) _localctx).skill_throw = skill_throw();
                                skillsInit.put(PlayerSkill.SKILL_TO_HIT_THROW, ((RaceContext) _localctx).skill_throw.value);
                            }
                            break;
                            case SKILL_DIG: {
                                setState(209);
                                ((RaceContext) _localctx).skill_dig = skill_dig();
                                skillsInit.put(PlayerSkill.SKILL_DIGGING, ((RaceContext) _localctx).skill_dig.value);
                            }
                            break;
                            case HITDIE: {
                                setState(212);
                                ((RaceContext) _localctx).hitdie = hitdie();
                                hitDieInit = ((RaceContext) _localctx).hitdie.die;
                            }
                            break;
                            case EXP: {
                                setState(215);
                                ((RaceContext) _localctx).exp = exp();
                                expInit = ((RaceContext) _localctx).exp.expInt;
                            }
                            break;
                            case INFRAVISION: {
                                setState(218);
                                ((RaceContext) _localctx).infravision = infravision();
                                infraInit = ((RaceContext) _localctx).infravision.infraInt;
                            }
                            break;
                            case HISTORY: {
                                setState(221);
                                ((RaceContext) _localctx).history = history();
                                historyInit = ((RaceContext) _localctx).history.histInt;
                            }
                            break;
                            case AGE: {
                                setState(224);
                                ((RaceContext) _localctx).age = age();
                                ageBaseInit = ((RaceContext) _localctx).age.ageBase;
                                ageModInit = ((RaceContext) _localctx).age.ageMod;
                            }
                            break;
                            case HEIGHT: {
                                setState(227);
                                ((RaceContext) _localctx).height = height();
                                heightBaseInit = ((RaceContext) _localctx).height.heightBase;
                                heightModInit = ((RaceContext) _localctx).height.heightMod;
                            }
                            break;
                            case WEIGHT: {
                                setState(230);
                                ((RaceContext) _localctx).weight = weight();
                                weightBaseInit = ((RaceContext) _localctx).weight.weightBase;
                                weightModInit = ((RaceContext) _localctx).weight.weightMod;
                            }
                            break;
                            case OBJ_FLAGS: {
                                setState(233);
                                ((RaceContext) _localctx).obj_flags = obj_flags();
                                oFlagsInit = ((RaceContext) _localctx).obj_flags.flags;
                            }
                            break;
                            case PLAYER_FLAGS: {
                                setState(236);
                                ((RaceContext) _localctx).player_flags = player_flags();
                                pFlagsInit = ((RaceContext) _localctx).player_flags.flags;
                            }
                            break;
                            case VALUES: {
                                setState(239);
                                ((RaceContext) _localctx).values = values();
                                valuesInit = ((RaceContext) _localctx).values.modifiers;
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                    }
                    setState(244);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 33554416L) != 0));
            }
            _ctx.stop = _input.LT(-1);

            PlayerHistoryChart chart = GameConstants.lookupPlayerHistoryChart(historyInit);
            PlayerBody bodyInit = GameConstants.lookupPlayerBody(bodyIntInit);

            ((RaceContext) _localctx).playerRace = new PlayerRace(nameInit, rIndexInit, hitDieInit,
                    expInit, ageBaseInit, ageModInit, heightBaseInit,
                    heightModInit, weightBaseInit, weightModInit,
                    infraInit, bodyInit, statsInit, skillsInit, oFlagsInit,
                    pFlagsInit, chart, valuesInit);

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
        public List<PlayerRace> races;
        public RaceContext race;

        public TerminalNode EOF() {
            return getToken(PlayerRaceParser.EOF, 0);
        }

        public List<RaceContext> race() {
            return getRuleContexts(RaceContext.class);
        }

        public RaceContext race(int i) {
            return getRuleContext(RaceContext.class, i);
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
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerRaceListener) ((PlayerRaceListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerRaceVisitor) return ((PlayerRaceVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 46, RULE_file);

        ((FileContext) _localctx).races = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(249);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(246);
                            ((FileContext) _localctx).race = race();

                            _localctx.races.add(((FileContext) _localctx).race.playerRace);

                        }
                    }
                    setState(251);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(253);
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
            "\u0004\u0001\u001e\u0100\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001" +
                    "\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004" +
                    "\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007" +
                    "\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b" +
                    "\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007" +
                    "\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007" +
                    "\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007" +
                    "\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t" +
                    "\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r" +
                    "\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e" +
                    "\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010" +
                    "\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011" +
                    "\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012" +
                    "\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0005\u0013\u0091\b\u0013" +
                    "\n\u0013\f\u0013\u0094\t\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001" +
                    "\u0014\u0001\u0014\u0001\u0014\u0005\u0014\u009c\b\u0014\n\u0014\f\u0014" +
                    "\u009f\t\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015" +
                    "\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015" +
                    "\u0001\u0015\u0005\u0015\u00ad\b\u0015\n\u0015\f\u0015\u00b0\t\u0015\u0001" +
                    "\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001" +
                    "\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001" +
                    "\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001" +
                    "\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001" +
                    "\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001" +
                    "\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001" +
                    "\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001" +
                    "\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001" +
                    "\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001" +
                    "\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001" +
                    "\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0004\u0016\u00f3" +
                    "\b\u0016\u000b\u0016\f\u0016\u00f4\u0001\u0017\u0001\u0017\u0001\u0017" +
                    "\u0004\u0017\u00fa\b\u0017\u000b\u0017\f\u0017\u00fb\u0001\u0017\u0001" +
                    "\u0017\u0001\u0017\u0000\u0000\u0018\u0000\u0002\u0004\u0006\b\n\f\u000e" +
                    "\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.\u0000\u0000" +
                    "\u0100\u00000\u0001\u0000\u0000\u0000\u00024\u0001\u0000\u0000\u0000\u0004" +
                    "@\u0001\u0000\u0000\u0000\u0006D\u0001\u0000\u0000\u0000\bH\u0001\u0000" +
                    "\u0000\u0000\nL\u0001\u0000\u0000\u0000\fP\u0001\u0000\u0000\u0000\u000e" +
                    "T\u0001\u0000\u0000\u0000\u0010X\u0001\u0000\u0000\u0000\u0012\\\u0001" +
                    "\u0000\u0000\u0000\u0014`\u0001\u0000\u0000\u0000\u0016d\u0001\u0000\u0000" +
                    "\u0000\u0018h\u0001\u0000\u0000\u0000\u001al\u0001\u0000\u0000\u0000\u001c" +
                    "p\u0001\u0000\u0000\u0000\u001et\u0001\u0000\u0000\u0000 x\u0001\u0000" +
                    "\u0000\u0000\"~\u0001\u0000\u0000\u0000$\u0084\u0001\u0000\u0000\u0000" +
                    "&\u008a\u0001\u0000\u0000\u0000(\u0095\u0001\u0000\u0000\u0000*\u00a0" +
                    "\u0001\u0000\u0000\u0000,\u00b1\u0001\u0000\u0000\u0000.\u00f9\u0001\u0000" +
                    "\u0000\u000001\u0005\u0003\u0000\u000012\u0005\u001a\u0000\u000023\u0006" +
                    "\u0000\uffff\uffff\u00003\u0001\u0001\u0000\u0000\u000045\u0005\u0004" +
                    "\u0000\u000056\u0005\u0019\u0000\u000067\u0005\u001d\u0000\u000078\u0005" +
                    "\u0019\u0000\u000089\u0005\u001d\u0000\u00009:\u0005\u0019\u0000\u0000" +
                    ":;\u0005\u001d\u0000\u0000;<\u0005\u0019\u0000\u0000<=\u0005\u001d\u0000" +
                    "\u0000=>\u0005\u0019\u0000\u0000>?\u0006\u0001\uffff\uffff\u0000?\u0003" +
                    "\u0001\u0000\u0000\u0000@A\u0005\u0005\u0000\u0000AB\u0005\u0019\u0000" +
                    "\u0000BC\u0006\u0002\uffff\uffff\u0000C\u0005\u0001\u0000\u0000\u0000" +
                    "DE\u0005\u0006\u0000\u0000EF\u0005\u0019\u0000\u0000FG\u0006\u0003\uffff" +
                    "\uffff\u0000G\u0007\u0001\u0000\u0000\u0000HI\u0005\u0007\u0000\u0000" +
                    "IJ\u0005\u0019\u0000\u0000JK\u0006\u0004\uffff\uffff\u0000K\t\u0001\u0000" +
                    "\u0000\u0000LM\u0005\b\u0000\u0000MN\u0005\u0019\u0000\u0000NO\u0006\u0005" +
                    "\uffff\uffff\u0000O\u000b\u0001\u0000\u0000\u0000PQ\u0005\t\u0000\u0000" +
                    "QR\u0005\u0019\u0000\u0000RS\u0006\u0006\uffff\uffff\u0000S\r\u0001\u0000" +
                    "\u0000\u0000TU\u0005\n\u0000\u0000UV\u0005\u0019\u0000\u0000VW\u0006\u0007" +
                    "\uffff\uffff\u0000W\u000f\u0001\u0000\u0000\u0000XY\u0005\u000b\u0000" +
                    "\u0000YZ\u0005\u0019\u0000\u0000Z[\u0006\b\uffff\uffff\u0000[\u0011\u0001" +
                    "\u0000\u0000\u0000\\]\u0005\f\u0000\u0000]^\u0005\u0019\u0000\u0000^_" +
                    "\u0006\t\uffff\uffff\u0000_\u0013\u0001\u0000\u0000\u0000`a\u0005\r\u0000" +
                    "\u0000ab\u0005\u0019\u0000\u0000bc\u0006\n\uffff\uffff\u0000c\u0015\u0001" +
                    "\u0000\u0000\u0000de\u0005\u000e\u0000\u0000ef\u0005\u0019\u0000\u0000" +
                    "fg\u0006\u000b\uffff\uffff\u0000g\u0017\u0001\u0000\u0000\u0000hi\u0005" +
                    "\u000f\u0000\u0000ij\u0005\u0019\u0000\u0000jk\u0006\f\uffff\uffff\u0000" +
                    "k\u0019\u0001\u0000\u0000\u0000lm\u0005\u0010\u0000\u0000mn\u0005\u0019" +
                    "\u0000\u0000no\u0006\r\uffff\uffff\u0000o\u001b\u0001\u0000\u0000\u0000" +
                    "pq\u0005\u0011\u0000\u0000qr\u0005\u0019\u0000\u0000rs\u0006\u000e\uffff" +
                    "\uffff\u0000s\u001d\u0001\u0000\u0000\u0000tu\u0005\u0012\u0000\u0000" +
                    "uv\u0005\u0019\u0000\u0000vw\u0006\u000f\uffff\uffff\u0000w\u001f\u0001" +
                    "\u0000\u0000\u0000xy\u0005\u0013\u0000\u0000yz\u0005\u0019\u0000\u0000" +
                    "z{\u0005\u001d\u0000\u0000{|\u0005\u0019\u0000\u0000|}\u0006\u0010\uffff" +
                    "\uffff\u0000}!\u0001\u0000\u0000\u0000~\u007f\u0005\u0014\u0000\u0000" +
                    "\u007f\u0080\u0005\u0019\u0000\u0000\u0080\u0081\u0005\u001d\u0000\u0000" +
                    "\u0081\u0082\u0005\u0019\u0000\u0000\u0082\u0083\u0006\u0011\uffff\uffff" +
                    "\u0000\u0083#\u0001\u0000\u0000\u0000\u0084\u0085\u0005\u0015\u0000\u0000" +
                    "\u0085\u0086\u0005\u0019\u0000\u0000\u0086\u0087\u0005\u001d\u0000\u0000" +
                    "\u0087\u0088\u0005\u0019\u0000\u0000\u0088\u0089\u0006\u0012\uffff\uffff" +
                    "\u0000\u0089%\u0001\u0000\u0000\u0000\u008a\u008b\u0005\u0016\u0000\u0000" +
                    "\u008b\u008c\u0005\u001a\u0000\u0000\u008c\u0092\u0006\u0013\uffff\uffff" +
                    "\u0000\u008d\u008e\u0005\u001e\u0000\u0000\u008e\u008f\u0005\u001a\u0000" +
                    "\u0000\u008f\u0091\u0006\u0013\uffff\uffff\u0000\u0090\u008d\u0001\u0000" +
                    "\u0000\u0000\u0091\u0094\u0001\u0000\u0000\u0000\u0092\u0090\u0001\u0000" +
                    "\u0000\u0000\u0092\u0093\u0001\u0000\u0000\u0000\u0093\'\u0001\u0000\u0000" +
                    "\u0000\u0094\u0092\u0001\u0000\u0000\u0000\u0095\u0096\u0005\u0017\u0000" +
                    "\u0000\u0096\u0097\u0005\u001a\u0000\u0000\u0097\u009d\u0006\u0014\uffff" +
                    "\uffff\u0000\u0098\u0099\u0005\u001e\u0000\u0000\u0099\u009a\u0005\u001a" +
                    "\u0000\u0000\u009a\u009c\u0006\u0014\uffff\uffff\u0000\u009b\u0098\u0001" +
                    "\u0000\u0000\u0000\u009c\u009f\u0001\u0000\u0000\u0000\u009d\u009b\u0001" +
                    "\u0000\u0000\u0000\u009d\u009e\u0001\u0000\u0000\u0000\u009e)\u0001\u0000" +
                    "\u0000\u0000\u009f\u009d\u0001\u0000\u0000\u0000\u00a0\u00a1\u0005\u0018" +
                    "\u0000\u0000\u00a1\u00a2\u0005\u001a\u0000\u0000\u00a2\u00a3\u0005\u001b" +
                    "\u0000\u0000\u00a3\u00a4\u0005\u0019\u0000\u0000\u00a4\u00a5\u0005\u001c" +
                    "\u0000\u0000\u00a5\u00ae\u0006\u0015\uffff\uffff\u0000\u00a6\u00a7\u0005" +
                    "\u001e\u0000\u0000\u00a7\u00a8\u0005\u001a\u0000\u0000\u00a8\u00a9\u0005" +
                    "\u001b\u0000\u0000\u00a9\u00aa\u0005\u0019\u0000\u0000\u00aa\u00ab\u0005" +
                    "\u001c\u0000\u0000\u00ab\u00ad\u0006\u0015\uffff\uffff\u0000\u00ac\u00a6" +
                    "\u0001\u0000\u0000\u0000\u00ad\u00b0\u0001\u0000\u0000\u0000\u00ae\u00ac" +
                    "\u0001\u0000\u0000\u0000\u00ae\u00af\u0001\u0000\u0000\u0000\u00af+\u0001" +
                    "\u0000\u0000\u0000\u00b0\u00ae\u0001\u0000\u0000\u0000\u00b1\u00b2\u0003" +
                    "\u0000\u0000\u0000\u00b2\u00f2\u0006\u0016\uffff\uffff\u0000\u00b3\u00b4" +
                    "\u0003\u0002\u0001\u0000\u00b4\u00b5\u0006\u0016\uffff\uffff\u0000\u00b5" +
                    "\u00f3\u0001\u0000\u0000\u0000\u00b6\u00b7\u0003\u0004\u0002\u0000\u00b7" +
                    "\u00b8\u0006\u0016\uffff\uffff\u0000\u00b8\u00f3\u0001\u0000\u0000\u0000" +
                    "\u00b9\u00ba\u0003\u0006\u0003\u0000\u00ba\u00bb\u0006\u0016\uffff\uffff" +
                    "\u0000\u00bb\u00f3\u0001\u0000\u0000\u0000\u00bc\u00bd\u0003\b\u0004\u0000" +
                    "\u00bd\u00be\u0006\u0016\uffff\uffff\u0000\u00be\u00f3\u0001\u0000\u0000" +
                    "\u0000\u00bf\u00c0\u0003\n\u0005\u0000\u00c0\u00c1\u0006\u0016\uffff\uffff" +
                    "\u0000\u00c1\u00f3\u0001\u0000\u0000\u0000\u00c2\u00c3\u0003\f\u0006\u0000" +
                    "\u00c3\u00c4\u0006\u0016\uffff\uffff\u0000\u00c4\u00f3\u0001\u0000\u0000" +
                    "\u0000\u00c5\u00c6\u0003\u000e\u0007\u0000\u00c6\u00c7\u0006\u0016\uffff" +
                    "\uffff\u0000\u00c7\u00f3\u0001\u0000\u0000\u0000\u00c8\u00c9\u0003\u0010" +
                    "\b\u0000\u00c9\u00ca\u0006\u0016\uffff\uffff\u0000\u00ca\u00f3\u0001\u0000" +
                    "\u0000\u0000\u00cb\u00cc\u0003\u0012\t\u0000\u00cc\u00cd\u0006\u0016\uffff" +
                    "\uffff\u0000\u00cd\u00f3\u0001\u0000\u0000\u0000\u00ce\u00cf\u0003\u0014" +
                    "\n\u0000\u00cf\u00d0\u0006\u0016\uffff\uffff\u0000\u00d0\u00f3\u0001\u0000" +
                    "\u0000\u0000\u00d1\u00d2\u0003\u0016\u000b\u0000\u00d2\u00d3\u0006\u0016" +
                    "\uffff\uffff\u0000\u00d3\u00f3\u0001\u0000\u0000\u0000\u00d4\u00d5\u0003" +
                    "\u0018\f\u0000\u00d5\u00d6\u0006\u0016\uffff\uffff\u0000\u00d6\u00f3\u0001" +
                    "\u0000\u0000\u0000\u00d7\u00d8\u0003\u001a\r\u0000\u00d8\u00d9\u0006\u0016" +
                    "\uffff\uffff\u0000\u00d9\u00f3\u0001\u0000\u0000\u0000\u00da\u00db\u0003" +
                    "\u001c\u000e\u0000\u00db\u00dc\u0006\u0016\uffff\uffff\u0000\u00dc\u00f3" +
                    "\u0001\u0000\u0000\u0000\u00dd\u00de\u0003\u001e\u000f\u0000\u00de\u00df" +
                    "\u0006\u0016\uffff\uffff\u0000\u00df\u00f3\u0001\u0000\u0000\u0000\u00e0" +
                    "\u00e1\u0003 \u0010\u0000\u00e1\u00e2\u0006\u0016\uffff\uffff\u0000\u00e2" +
                    "\u00f3\u0001\u0000\u0000\u0000\u00e3\u00e4\u0003\"\u0011\u0000\u00e4\u00e5" +
                    "\u0006\u0016\uffff\uffff\u0000\u00e5\u00f3\u0001\u0000\u0000\u0000\u00e6" +
                    "\u00e7\u0003$\u0012\u0000\u00e7\u00e8\u0006\u0016\uffff\uffff\u0000\u00e8" +
                    "\u00f3\u0001\u0000\u0000\u0000\u00e9\u00ea\u0003&\u0013\u0000\u00ea\u00eb" +
                    "\u0006\u0016\uffff\uffff\u0000\u00eb\u00f3\u0001\u0000\u0000\u0000\u00ec" +
                    "\u00ed\u0003(\u0014\u0000\u00ed\u00ee\u0006\u0016\uffff\uffff\u0000\u00ee" +
                    "\u00f3\u0001\u0000\u0000\u0000\u00ef\u00f0\u0003*\u0015\u0000\u00f0\u00f1" +
                    "\u0006\u0016\uffff\uffff\u0000\u00f1\u00f3\u0001\u0000\u0000\u0000\u00f2" +
                    "\u00b3\u0001\u0000\u0000\u0000\u00f2\u00b6\u0001\u0000\u0000\u0000\u00f2" +
                    "\u00b9\u0001\u0000\u0000\u0000\u00f2\u00bc\u0001\u0000\u0000\u0000\u00f2" +
                    "\u00bf\u0001\u0000\u0000\u0000\u00f2\u00c2\u0001\u0000\u0000\u0000\u00f2" +
                    "\u00c5\u0001\u0000\u0000\u0000\u00f2\u00c8\u0001\u0000\u0000\u0000\u00f2" +
                    "\u00cb\u0001\u0000\u0000\u0000\u00f2\u00ce\u0001\u0000\u0000\u0000\u00f2" +
                    "\u00d1\u0001\u0000\u0000\u0000\u00f2\u00d4\u0001\u0000\u0000\u0000\u00f2" +
                    "\u00d7\u0001\u0000\u0000\u0000\u00f2\u00da\u0001\u0000\u0000\u0000\u00f2" +
                    "\u00dd\u0001\u0000\u0000\u0000\u00f2\u00e0\u0001\u0000\u0000\u0000\u00f2" +
                    "\u00e3\u0001\u0000\u0000\u0000\u00f2\u00e6\u0001\u0000\u0000\u0000\u00f2" +
                    "\u00e9\u0001\u0000\u0000\u0000\u00f2\u00ec\u0001\u0000\u0000\u0000\u00f2" +
                    "\u00ef\u0001\u0000\u0000\u0000\u00f3\u00f4\u0001\u0000\u0000\u0000\u00f4" +
                    "\u00f2\u0001\u0000\u0000\u0000\u00f4\u00f5\u0001\u0000\u0000\u0000\u00f5" +
                    "-\u0001\u0000\u0000\u0000\u00f6\u00f7\u0003,\u0016\u0000\u00f7\u00f8\u0006" +
                    "\u0017\uffff\uffff\u0000\u00f8\u00fa\u0001\u0000\u0000\u0000\u00f9\u00f6" +
                    "\u0001\u0000\u0000\u0000\u00fa\u00fb\u0001\u0000\u0000\u0000\u00fb\u00f9" +
                    "\u0001\u0000\u0000\u0000\u00fb\u00fc\u0001\u0000\u0000\u0000\u00fc\u00fd" +
                    "\u0001\u0000\u0000\u0000\u00fd\u00fe\u0005\u0000\u0000\u0001\u00fe/\u0001" +
                    "\u0000\u0000\u0000\u0006\u0092\u009d\u00ae\u00f2\u00f4\u00fb";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}