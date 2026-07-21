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
// Generated from MonsterGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.monster;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.parser.monster.MonsterParseRecord;
import uk.co.jackoftrades.backend.parser.monster.MonsterParseRecord.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class MonsterGrammar extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, NAME = 2, PLURAL = 3, BASE = 4, GLYPH = 5, COLOUR = 6, SPEED = 7,
            HIT_POINTS = 8, LIGHT = 9, HEARING = 10, ARMOUR_CLASS = 11, SLEEPINESS = 12, DEPTH = 13,
            RARITY = 14, EXPERIENCE = 15, BLOW = 16, FLAGS = 17, FLAGS_OFF = 18, INNATE_FREQ = 19,
            SPELL_FREQ = 20, SPELL_POWER = 21, SPELLS = 22, MESSAGE_VIS = 23, MESSAGE_INVIS = 24,
            MESSAGE_MISS = 25, DESC = 26, DROP = 27, DROP_BASE = 28, MIMIC = 29, FRIENDS = 30,
            FRIENDS_BASE = 31, SMELL = 32, SHAPE = 33, COLOUR_CYCLE = 34, MON_INTEGER = 35,
            COMMENT = 36, EOL = 37, SIMPLE_DICE_STRING = 38, COMPLEX_DICE_STRING = 39, STRING = 40,
            ROL_EOL = 41, BLOW_COLON = 42, BLOW_DICESTRING = 43, BLOW_WORD = 44, BLOW_EOL = 45,
            FLAG = 46, FLAG_OR = 47, FLAG_EOL = 48, COLON = 49, DELIMITED_STRING = 50, DELIMITED_EOL = 51,
            FRIEND_INTEGER = 52, FRIEND_DICE = 53, FRIEND_COLON = 54, FRIEND_NAME = 55, FRIEND_EOL = 56,
            DROP_COLON = 57, DROP_INTEGER = 58, DROP_STRING = 59, DROP_EOL = 60, COLOUR_CHAR = 61,
            COLOUR_EOL = 62;
    public static final int
            RULE_recordCount = 0, RULE_name = 1, RULE_plural = 2, RULE_base = 3, RULE_glyph = 4,
            RULE_colour = 5, RULE_speed = 6, RULE_hitPoints = 7, RULE_light = 8, RULE_hearing = 9,
            RULE_smell = 10, RULE_shape = 11, RULE_colourCycle = 12, RULE_armourClass = 13,
            RULE_sleepiness = 14, RULE_depthLevel = 15, RULE_rarity = 16, RULE_experience = 17,
            RULE_blow = 18, RULE_flags = 19, RULE_flagsOff = 20, RULE_innateFreq = 21,
            RULE_spellFreq = 22, RULE_spellPower = 23, RULE_spells = 24, RULE_messageVis = 25,
            RULE_messageInvis = 26, RULE_messageMiss = 27, RULE_desc = 28, RULE_drop = 29,
            RULE_dropBase = 30, RULE_mimic = 31, RULE_friends = 32, RULE_friendsBase = 33,
            RULE_monster = 34, RULE_file = 35;

    private static String[] makeRuleNames() {
        return new String[]{
                "recordCount", "name", "plural", "base", "glyph", "colour", "speed",
                "hitPoints", "light", "hearing", "smell", "shape", "colourCycle", "armourClass",
                "sleepiness", "depthLevel", "rarity", "experience", "blow", "flags",
                "flagsOff", "innateFreq", "spellFreq", "spellPower", "spells", "messageVis",
                "messageInvis", "messageMiss", "desc", "drop", "dropBase", "mimic", "friends",
                "friendsBase", "monster", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'name:'", "'plural:'", "'base:'", "'glyph:'",
                "'color:'", "'speed:'", "'hit-points:'", "'light:'", "'hearing:'", "'armor-class:'",
                "'sleepiness:'", "'depth:'", "'rarity:'", "'experience:'", "'blow:'",
                "'flags:'", null, "'innate-freq:'", "'spell-freq:'", "'spell-power:'",
                "'spells:'", "'message-vis:'", "'message-invis:'", "'message-miss:'",
                "'desc:'", "'drop:'", "'drop-base:'", "'mimic:'", "'friends:'", "'friends-base:'",
                "'smell:'", "'shape:'", "'color-cycle:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "NAME", "PLURAL", "BASE", "GLYPH", "COLOUR", "SPEED",
                "HIT_POINTS", "LIGHT", "HEARING", "ARMOUR_CLASS", "SLEEPINESS", "DEPTH",
                "RARITY", "EXPERIENCE", "BLOW", "FLAGS", "FLAGS_OFF", "INNATE_FREQ",
                "SPELL_FREQ", "SPELL_POWER", "SPELLS", "MESSAGE_VIS", "MESSAGE_INVIS",
                "MESSAGE_MISS", "DESC", "DROP", "DROP_BASE", "MIMIC", "FRIENDS", "FRIENDS_BASE",
                "SMELL", "SHAPE", "COLOUR_CYCLE", "MON_INTEGER", "COMMENT", "EOL", "SIMPLE_DICE_STRING",
                "COMPLEX_DICE_STRING", "STRING", "ROL_EOL", "BLOW_COLON", "BLOW_DICESTRING",
                "BLOW_WORD", "BLOW_EOL", "FLAG", "FLAG_OR", "FLAG_EOL", "COLON", "DELIMITED_STRING",
                "DELIMITED_EOL", "FRIEND_INTEGER", "FRIEND_DICE", "FRIEND_COLON", "FRIEND_NAME",
                "FRIEND_EOL", "DROP_COLON", "DROP_INTEGER", "DROP_STRING", "DROP_EOL",
                "COLOUR_CHAR", "COLOUR_EOL"
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
        return "MonsterGrammar.g4";
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

    public MonsterGrammar(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RecordCountContext extends ParserRuleContext {
        public String count;
        public Token MON_INTEGER;

        public TerminalNode RECORD_COUNT() {
            return getToken(MonsterGrammar.RECORD_COUNT, 0);
        }

        public TerminalNode MON_INTEGER() {
            return getToken(MonsterGrammar.MON_INTEGER, 0);
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
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterRecordCount(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitRecordCount(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitRecordCount(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RecordCountContext recordCount() throws RecognitionException {
        RecordCountContext _localctx = new RecordCountContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_recordCount);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(72);
                match(RECORD_COUNT);
                setState(73);
                ((RecordCountContext) _localctx).MON_INTEGER = match(MON_INTEGER);
                ((RecordCountContext) _localctx).count = ((RecordCountContext) _localctx).MON_INTEGER.getText();
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
            return getToken(MonsterGrammar.NAME, 0);
        }

        public TerminalNode STRING() {
            return getToken(MonsterGrammar.STRING, 0);
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
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(76);
                match(NAME);
                setState(77);
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
    public static class PluralContext extends ParserRuleContext {
        public String pluralStr;
        public Token STRING;

        public TerminalNode PLURAL() {
            return getToken(MonsterGrammar.PLURAL, 0);
        }

        public TerminalNode STRING() {
            return getToken(MonsterGrammar.STRING, 0);
        }

        public PluralContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_plural;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterPlural(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitPlural(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitPlural(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PluralContext plural() throws RecognitionException {
        PluralContext _localctx = new PluralContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_plural);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(80);
                match(PLURAL);
                setState(81);
                ((PluralContext) _localctx).STRING = match(STRING);
                ((PluralContext) _localctx).pluralStr = ((PluralContext) _localctx).STRING.getText();
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
    public static class BaseContext extends ParserRuleContext {
        public String baseStr;
        public Token STRING;

        public TerminalNode BASE() {
            return getToken(MonsterGrammar.BASE, 0);
        }

        public TerminalNode STRING() {
            return getToken(MonsterGrammar.STRING, 0);
        }

        public BaseContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_base;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterBase(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitBase(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitBase(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BaseContext base() throws RecognitionException {
        BaseContext _localctx = new BaseContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_base);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(84);
                match(BASE);
                setState(85);
                ((BaseContext) _localctx).STRING = match(STRING);
                ((BaseContext) _localctx).baseStr = ((BaseContext) _localctx).STRING.getText();
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
    public static class GlyphContext extends ParserRuleContext {
        public String glyphStr;
        public Token STRING;

        public TerminalNode GLYPH() {
            return getToken(MonsterGrammar.GLYPH, 0);
        }

        public TerminalNode STRING() {
            return getToken(MonsterGrammar.STRING, 0);
        }

        public GlyphContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_glyph;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterGlyph(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitGlyph(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitGlyph(this);
            else return visitor.visitChildren(this);
        }
    }

    public final GlyphContext glyph() throws RecognitionException {
        GlyphContext _localctx = new GlyphContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_glyph);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(88);
                match(GLYPH);
                setState(89);
                ((GlyphContext) _localctx).STRING = match(STRING);
                ((GlyphContext) _localctx).glyphStr = ((GlyphContext) _localctx).STRING.getText();
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
    public static class ColourContext extends ParserRuleContext {
        public String colourStr;
        public Token COLOUR_CHAR;

        public TerminalNode COLOUR() {
            return getToken(MonsterGrammar.COLOUR, 0);
        }

        public TerminalNode COLOUR_CHAR() {
            return getToken(MonsterGrammar.COLOUR_CHAR, 0);
        }

        public ColourContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_colour;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterColour(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitColour(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitColour(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ColourContext colour() throws RecognitionException {
        ColourContext _localctx = new ColourContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_colour);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(92);
                match(COLOUR);
                setState(93);
                ((ColourContext) _localctx).COLOUR_CHAR = match(COLOUR_CHAR);
                ((ColourContext) _localctx).colourStr = ((ColourContext) _localctx).COLOUR_CHAR.getText();
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
    public static class SpeedContext extends ParserRuleContext {
        public String speedVal;
        public Token MON_INTEGER;

        public TerminalNode SPEED() {
            return getToken(MonsterGrammar.SPEED, 0);
        }

        public TerminalNode MON_INTEGER() {
            return getToken(MonsterGrammar.MON_INTEGER, 0);
        }

        public SpeedContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_speed;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterSpeed(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitSpeed(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitSpeed(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SpeedContext speed() throws RecognitionException {
        SpeedContext _localctx = new SpeedContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_speed);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(96);
                match(SPEED);
                setState(97);
                ((SpeedContext) _localctx).MON_INTEGER = match(MON_INTEGER);
                ((SpeedContext) _localctx).speedVal = ((SpeedContext) _localctx).MON_INTEGER.getText();
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
    public static class HitPointsContext extends ParserRuleContext {
        public String hitPointsVal;
        public Token MON_INTEGER;

        public TerminalNode HIT_POINTS() {
            return getToken(MonsterGrammar.HIT_POINTS, 0);
        }

        public TerminalNode MON_INTEGER() {
            return getToken(MonsterGrammar.MON_INTEGER, 0);
        }

        public HitPointsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_hitPoints;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterHitPoints(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitHitPoints(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitHitPoints(this);
            else return visitor.visitChildren(this);
        }
    }

    public final HitPointsContext hitPoints() throws RecognitionException {
        HitPointsContext _localctx = new HitPointsContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_hitPoints);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(100);
                match(HIT_POINTS);
                setState(101);
                ((HitPointsContext) _localctx).MON_INTEGER = match(MON_INTEGER);
                ((HitPointsContext) _localctx).hitPointsVal = ((HitPointsContext) _localctx).MON_INTEGER.getText();
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
    public static class LightContext extends ParserRuleContext {
        public String lightVal;
        public Token MON_INTEGER;

        public TerminalNode LIGHT() {
            return getToken(MonsterGrammar.LIGHT, 0);
        }

        public TerminalNode MON_INTEGER() {
            return getToken(MonsterGrammar.MON_INTEGER, 0);
        }

        public LightContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_light;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterLight(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitLight(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitLight(this);
            else return visitor.visitChildren(this);
        }
    }

    public final LightContext light() throws RecognitionException {
        LightContext _localctx = new LightContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_light);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(104);
                match(LIGHT);
                setState(105);
                ((LightContext) _localctx).MON_INTEGER = match(MON_INTEGER);
                ((LightContext) _localctx).lightVal = ((LightContext) _localctx).MON_INTEGER.getText();
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
    public static class HearingContext extends ParserRuleContext {
        public String hearingVal;
        public Token MON_INTEGER;

        public TerminalNode HEARING() {
            return getToken(MonsterGrammar.HEARING, 0);
        }

        public TerminalNode MON_INTEGER() {
            return getToken(MonsterGrammar.MON_INTEGER, 0);
        }

        public HearingContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_hearing;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterHearing(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitHearing(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitHearing(this);
            else return visitor.visitChildren(this);
        }
    }

    public final HearingContext hearing() throws RecognitionException {
        HearingContext _localctx = new HearingContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_hearing);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(108);
                match(HEARING);
                setState(109);
                ((HearingContext) _localctx).MON_INTEGER = match(MON_INTEGER);
                ((HearingContext) _localctx).hearingVal = ((HearingContext) _localctx).MON_INTEGER.getText();
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
    public static class SmellContext extends ParserRuleContext {
        public String smellVal;
        public Token MON_INTEGER;

        public TerminalNode SMELL() {
            return getToken(MonsterGrammar.SMELL, 0);
        }

        public TerminalNode MON_INTEGER() {
            return getToken(MonsterGrammar.MON_INTEGER, 0);
        }

        public SmellContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_smell;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterSmell(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitSmell(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitSmell(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SmellContext smell() throws RecognitionException {
        SmellContext _localctx = new SmellContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_smell);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(112);
                match(SMELL);
                setState(113);
                ((SmellContext) _localctx).MON_INTEGER = match(MON_INTEGER);
                ((SmellContext) _localctx).smellVal = ((SmellContext) _localctx).MON_INTEGER.getText();
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
    public static class ShapeContext extends ParserRuleContext {
        public String shapeStr;
        public Token STRING;

        public TerminalNode SHAPE() {
            return getToken(MonsterGrammar.SHAPE, 0);
        }

        public TerminalNode STRING() {
            return getToken(MonsterGrammar.STRING, 0);
        }

        public ShapeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_shape;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterShape(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitShape(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitShape(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ShapeContext shape() throws RecognitionException {
        ShapeContext _localctx = new ShapeContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_shape);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(116);
                match(SHAPE);
                setState(117);
                ((ShapeContext) _localctx).STRING = match(STRING);
                ((ShapeContext) _localctx).shapeStr = ((ShapeContext) _localctx).STRING.getText();
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
    public static class ColourCycleContext extends ParserRuleContext {
        public String groupStr;
        public String nameStr;
        public Token g;
        public Token n;

        public TerminalNode COLOUR_CYCLE() {
            return getToken(MonsterGrammar.COLOUR_CYCLE, 0);
        }

        public TerminalNode COLON() {
            return getToken(MonsterGrammar.COLON, 0);
        }

        public List<TerminalNode> DELIMITED_STRING() {
            return getTokens(MonsterGrammar.DELIMITED_STRING);
        }

        public TerminalNode DELIMITED_STRING(int i) {
            return getToken(MonsterGrammar.DELIMITED_STRING, i);
        }

        public ColourCycleContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_colourCycle;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterColourCycle(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitColourCycle(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitColourCycle(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ColourCycleContext colourCycle() throws RecognitionException {
        ColourCycleContext _localctx = new ColourCycleContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_colourCycle);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(120);
                match(COLOUR_CYCLE);
                setState(121);
                ((ColourCycleContext) _localctx).g = match(DELIMITED_STRING);
                setState(122);
                match(COLON);
                setState(123);
                ((ColourCycleContext) _localctx).n = match(DELIMITED_STRING);

                ((ColourCycleContext) _localctx).groupStr = ((ColourCycleContext) _localctx).g.getText();
                ((ColourCycleContext) _localctx).nameStr = ((ColourCycleContext) _localctx).n.getText();

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
    public static class ArmourClassContext extends ParserRuleContext {
        public String armourClassVal;
        public Token MON_INTEGER;

        public TerminalNode ARMOUR_CLASS() {
            return getToken(MonsterGrammar.ARMOUR_CLASS, 0);
        }

        public TerminalNode MON_INTEGER() {
            return getToken(MonsterGrammar.MON_INTEGER, 0);
        }

        public ArmourClassContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_armourClass;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterArmourClass(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitArmourClass(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitArmourClass(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ArmourClassContext armourClass() throws RecognitionException {
        ArmourClassContext _localctx = new ArmourClassContext(_ctx, getState());
        enterRule(_localctx, 26, RULE_armourClass);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(126);
                match(ARMOUR_CLASS);
                setState(127);
                ((ArmourClassContext) _localctx).MON_INTEGER = match(MON_INTEGER);
                ((ArmourClassContext) _localctx).armourClassVal = ((ArmourClassContext) _localctx).MON_INTEGER.getText();
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
    public static class SleepinessContext extends ParserRuleContext {
        public String sleepinessVal;
        public Token MON_INTEGER;

        public TerminalNode SLEEPINESS() {
            return getToken(MonsterGrammar.SLEEPINESS, 0);
        }

        public TerminalNode MON_INTEGER() {
            return getToken(MonsterGrammar.MON_INTEGER, 0);
        }

        public SleepinessContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_sleepiness;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterSleepiness(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitSleepiness(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitSleepiness(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SleepinessContext sleepiness() throws RecognitionException {
        SleepinessContext _localctx = new SleepinessContext(_ctx, getState());
        enterRule(_localctx, 28, RULE_sleepiness);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(130);
                match(SLEEPINESS);
                setState(131);
                ((SleepinessContext) _localctx).MON_INTEGER = match(MON_INTEGER);
                ((SleepinessContext) _localctx).sleepinessVal = ((SleepinessContext) _localctx).MON_INTEGER.getText();
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
    public static class DepthLevelContext extends ParserRuleContext {
        public String depthVal;
        public Token MON_INTEGER;

        public TerminalNode DEPTH() {
            return getToken(MonsterGrammar.DEPTH, 0);
        }

        public TerminalNode MON_INTEGER() {
            return getToken(MonsterGrammar.MON_INTEGER, 0);
        }

        public DepthLevelContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_depthLevel;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterDepthLevel(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitDepthLevel(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitDepthLevel(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DepthLevelContext depthLevel() throws RecognitionException {
        DepthLevelContext _localctx = new DepthLevelContext(_ctx, getState());
        enterRule(_localctx, 30, RULE_depthLevel);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(134);
                match(DEPTH);
                setState(135);
                ((DepthLevelContext) _localctx).MON_INTEGER = match(MON_INTEGER);
                ((DepthLevelContext) _localctx).depthVal = ((DepthLevelContext) _localctx).MON_INTEGER.getText();
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
    public static class RarityContext extends ParserRuleContext {
        public String rarityVal;
        public Token MON_INTEGER;

        public TerminalNode RARITY() {
            return getToken(MonsterGrammar.RARITY, 0);
        }

        public TerminalNode MON_INTEGER() {
            return getToken(MonsterGrammar.MON_INTEGER, 0);
        }

        public RarityContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_rarity;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterRarity(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitRarity(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitRarity(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RarityContext rarity() throws RecognitionException {
        RarityContext _localctx = new RarityContext(_ctx, getState());
        enterRule(_localctx, 32, RULE_rarity);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(138);
                match(RARITY);
                setState(139);
                ((RarityContext) _localctx).MON_INTEGER = match(MON_INTEGER);
                ((RarityContext) _localctx).rarityVal = ((RarityContext) _localctx).MON_INTEGER.getText();
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
    public static class ExperienceContext extends ParserRuleContext {
        public String experienceVal;
        public Token MON_INTEGER;

        public TerminalNode EXPERIENCE() {
            return getToken(MonsterGrammar.EXPERIENCE, 0);
        }

        public TerminalNode MON_INTEGER() {
            return getToken(MonsterGrammar.MON_INTEGER, 0);
        }

        public ExperienceContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_experience;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterExperience(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitExperience(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitExperience(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExperienceContext experience() throws RecognitionException {
        ExperienceContext _localctx = new ExperienceContext(_ctx, getState());
        enterRule(_localctx, 34, RULE_experience);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(142);
                match(EXPERIENCE);
                setState(143);
                ((ExperienceContext) _localctx).MON_INTEGER = match(MON_INTEGER);
                ((ExperienceContext) _localctx).experienceVal = ((ExperienceContext) _localctx).MON_INTEGER.getText();
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
    public static class BlowContext extends ParserRuleContext {
        public String blowType;
        public String blowSubType;
        public String blowDice;
        public Token t;
        public Token s;
        public Token d;

        public TerminalNode BLOW() {
            return getToken(MonsterGrammar.BLOW, 0);
        }

        public List<TerminalNode> BLOW_WORD() {
            return getTokens(MonsterGrammar.BLOW_WORD);
        }

        public TerminalNode BLOW_WORD(int i) {
            return getToken(MonsterGrammar.BLOW_WORD, i);
        }

        public List<TerminalNode> BLOW_COLON() {
            return getTokens(MonsterGrammar.BLOW_COLON);
        }

        public TerminalNode BLOW_COLON(int i) {
            return getToken(MonsterGrammar.BLOW_COLON, i);
        }

        public TerminalNode BLOW_DICESTRING() {
            return getToken(MonsterGrammar.BLOW_DICESTRING, 0);
        }

        public BlowContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_blow;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterBlow(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitBlow(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitBlow(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BlowContext blow() throws RecognitionException {
        BlowContext _localctx = new BlowContext(_ctx, getState());
        enterRule(_localctx, 36, RULE_blow);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(146);
                match(BLOW);
                setState(147);
                ((BlowContext) _localctx).t = match(BLOW_WORD);
                ((BlowContext) _localctx).blowType = ((BlowContext) _localctx).t.getText();
                ((BlowContext) _localctx).blowSubType = "";
                ((BlowContext) _localctx).blowDice = "";
                setState(157);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == BLOW_COLON) {
                    {
                        setState(149);
                        match(BLOW_COLON);
                        setState(150);
                        ((BlowContext) _localctx).s = match(BLOW_WORD);
                        ((BlowContext) _localctx).blowSubType = ((BlowContext) _localctx).s.getText();
                        setState(155);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == BLOW_COLON) {
                            {
                                setState(152);
                                match(BLOW_COLON);
                                setState(153);
                                ((BlowContext) _localctx).d = match(BLOW_DICESTRING);
                                ((BlowContext) _localctx).blowDice = ((BlowContext) _localctx).d.getText();
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
    public static class FlagsContext extends ParserRuleContext {
        public List<String> flagsList;
        public Token f1;
        public Token f2;

        public TerminalNode FLAGS() {
            return getToken(MonsterGrammar.FLAGS, 0);
        }

        public List<TerminalNode> FLAG() {
            return getTokens(MonsterGrammar.FLAG);
        }

        public TerminalNode FLAG(int i) {
            return getToken(MonsterGrammar.FLAG, i);
        }

        public List<TerminalNode> FLAG_OR() {
            return getTokens(MonsterGrammar.FLAG_OR);
        }

        public TerminalNode FLAG_OR(int i) {
            return getToken(MonsterGrammar.FLAG_OR, i);
        }

        public FlagsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_flags;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterFlags(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitFlags(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitFlags(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FlagsContext flags() throws RecognitionException {
        FlagsContext _localctx = new FlagsContext(_ctx, getState());
        enterRule(_localctx, 38, RULE_flags);

        ((FlagsContext) _localctx).flagsList = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(159);
                match(FLAGS);
                setState(160);
                ((FlagsContext) _localctx).f1 = match(FLAG);
                _localctx.flagsList.add(((FlagsContext) _localctx).f1.getText());
                setState(167);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == FLAG_OR) {
                    {
                        {
                            setState(162);
                            match(FLAG_OR);
                            setState(163);
                            ((FlagsContext) _localctx).f2 = match(FLAG);
                            _localctx.flagsList.add(((FlagsContext) _localctx).f2.getText());
                        }
                    }
                    setState(169);
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
    public static class FlagsOffContext extends ParserRuleContext {
        public List<String> flagsList;
        public Token f1;
        public Token f2;

        public TerminalNode FLAGS_OFF() {
            return getToken(MonsterGrammar.FLAGS_OFF, 0);
        }

        public List<TerminalNode> FLAG() {
            return getTokens(MonsterGrammar.FLAG);
        }

        public TerminalNode FLAG(int i) {
            return getToken(MonsterGrammar.FLAG, i);
        }

        public List<TerminalNode> FLAG_OR() {
            return getTokens(MonsterGrammar.FLAG_OR);
        }

        public TerminalNode FLAG_OR(int i) {
            return getToken(MonsterGrammar.FLAG_OR, i);
        }

        public FlagsOffContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_flagsOff;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterFlagsOff(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitFlagsOff(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitFlagsOff(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FlagsOffContext flagsOff() throws RecognitionException {
        FlagsOffContext _localctx = new FlagsOffContext(_ctx, getState());
        enterRule(_localctx, 40, RULE_flagsOff);

        ((FlagsOffContext) _localctx).flagsList = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(170);
                match(FLAGS_OFF);
                setState(171);
                ((FlagsOffContext) _localctx).f1 = match(FLAG);
                _localctx.flagsList.add(((FlagsOffContext) _localctx).f1.getText());
                setState(178);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == FLAG_OR) {
                    {
                        {
                            setState(173);
                            match(FLAG_OR);
                            setState(174);
                            ((FlagsOffContext) _localctx).f2 = match(FLAG);
                            _localctx.flagsList.add(((FlagsOffContext) _localctx).f2.getText());
                        }
                    }
                    setState(180);
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
    public static class InnateFreqContext extends ParserRuleContext {
        public String innateFreqVal;
        public Token MON_INTEGER;

        public TerminalNode INNATE_FREQ() {
            return getToken(MonsterGrammar.INNATE_FREQ, 0);
        }

        public TerminalNode MON_INTEGER() {
            return getToken(MonsterGrammar.MON_INTEGER, 0);
        }

        public InnateFreqContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_innateFreq;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterInnateFreq(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitInnateFreq(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitInnateFreq(this);
            else return visitor.visitChildren(this);
        }
    }

    public final InnateFreqContext innateFreq() throws RecognitionException {
        InnateFreqContext _localctx = new InnateFreqContext(_ctx, getState());
        enterRule(_localctx, 42, RULE_innateFreq);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(181);
                match(INNATE_FREQ);
                setState(182);
                ((InnateFreqContext) _localctx).MON_INTEGER = match(MON_INTEGER);
                ((InnateFreqContext) _localctx).innateFreqVal = ((InnateFreqContext) _localctx).MON_INTEGER.getText();
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
    public static class SpellFreqContext extends ParserRuleContext {
        public String spellFreqVal;
        public Token MON_INTEGER;

        public TerminalNode SPELL_FREQ() {
            return getToken(MonsterGrammar.SPELL_FREQ, 0);
        }

        public TerminalNode MON_INTEGER() {
            return getToken(MonsterGrammar.MON_INTEGER, 0);
        }

        public SpellFreqContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_spellFreq;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterSpellFreq(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitSpellFreq(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitSpellFreq(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SpellFreqContext spellFreq() throws RecognitionException {
        SpellFreqContext _localctx = new SpellFreqContext(_ctx, getState());
        enterRule(_localctx, 44, RULE_spellFreq);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(185);
                match(SPELL_FREQ);
                setState(186);
                ((SpellFreqContext) _localctx).MON_INTEGER = match(MON_INTEGER);
                ((SpellFreqContext) _localctx).spellFreqVal = ((SpellFreqContext) _localctx).MON_INTEGER.getText();
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
    public static class SpellPowerContext extends ParserRuleContext {
        public String spellPowerVal;
        public Token MON_INTEGER;

        public TerminalNode SPELL_POWER() {
            return getToken(MonsterGrammar.SPELL_POWER, 0);
        }

        public TerminalNode MON_INTEGER() {
            return getToken(MonsterGrammar.MON_INTEGER, 0);
        }

        public SpellPowerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_spellPower;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterSpellPower(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitSpellPower(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitSpellPower(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SpellPowerContext spellPower() throws RecognitionException {
        SpellPowerContext _localctx = new SpellPowerContext(_ctx, getState());
        enterRule(_localctx, 46, RULE_spellPower);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(189);
                match(SPELL_POWER);
                setState(190);
                ((SpellPowerContext) _localctx).MON_INTEGER = match(MON_INTEGER);
                ((SpellPowerContext) _localctx).spellPowerVal = ((SpellPowerContext) _localctx).MON_INTEGER.getText();
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
    public static class SpellsContext extends ParserRuleContext {
        public List<String> spellList;
        public Token s1;
        public Token s2;

        public TerminalNode SPELLS() {
            return getToken(MonsterGrammar.SPELLS, 0);
        }

        public List<TerminalNode> FLAG() {
            return getTokens(MonsterGrammar.FLAG);
        }

        public TerminalNode FLAG(int i) {
            return getToken(MonsterGrammar.FLAG, i);
        }

        public List<TerminalNode> FLAG_OR() {
            return getTokens(MonsterGrammar.FLAG_OR);
        }

        public TerminalNode FLAG_OR(int i) {
            return getToken(MonsterGrammar.FLAG_OR, i);
        }

        public SpellsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_spells;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterSpells(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitSpells(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitSpells(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SpellsContext spells() throws RecognitionException {
        SpellsContext _localctx = new SpellsContext(_ctx, getState());
        enterRule(_localctx, 48, RULE_spells);

        ((SpellsContext) _localctx).spellList = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(193);
                match(SPELLS);
                setState(194);
                ((SpellsContext) _localctx).s1 = match(FLAG);
                _localctx.spellList.add(((SpellsContext) _localctx).s1.getText());
                setState(201);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == FLAG_OR) {
                    {
                        {
                            setState(196);
                            match(FLAG_OR);
                            setState(197);
                            ((SpellsContext) _localctx).s2 = match(FLAG);
                            _localctx.spellList.add(((SpellsContext) _localctx).s2.getText());
                        }
                    }
                    setState(203);
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
    public static class MessageVisContext extends ParserRuleContext {
        public String spell;
        public String line;
        public Token s;
        public Token l;

        public TerminalNode MESSAGE_VIS() {
            return getToken(MonsterGrammar.MESSAGE_VIS, 0);
        }

        public TerminalNode COLON() {
            return getToken(MonsterGrammar.COLON, 0);
        }

        public List<TerminalNode> DELIMITED_STRING() {
            return getTokens(MonsterGrammar.DELIMITED_STRING);
        }

        public TerminalNode DELIMITED_STRING(int i) {
            return getToken(MonsterGrammar.DELIMITED_STRING, i);
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
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterMessageVis(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitMessageVis(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitMessageVis(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MessageVisContext messageVis() throws RecognitionException {
        MessageVisContext _localctx = new MessageVisContext(_ctx, getState());
        enterRule(_localctx, 50, RULE_messageVis);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(204);
                match(MESSAGE_VIS);
                setState(205);
                ((MessageVisContext) _localctx).s = match(DELIMITED_STRING);
                ((MessageVisContext) _localctx).spell = ((MessageVisContext) _localctx).s.getText();
                setState(207);
                match(COLON);
                setState(208);
                ((MessageVisContext) _localctx).l = match(DELIMITED_STRING);
                ((MessageVisContext) _localctx).line = ((MessageVisContext) _localctx).l.getText();
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
        public String spell;
        public String line;
        public Token s;
        public Token l;

        public TerminalNode MESSAGE_INVIS() {
            return getToken(MonsterGrammar.MESSAGE_INVIS, 0);
        }

        public TerminalNode COLON() {
            return getToken(MonsterGrammar.COLON, 0);
        }

        public List<TerminalNode> DELIMITED_STRING() {
            return getTokens(MonsterGrammar.DELIMITED_STRING);
        }

        public TerminalNode DELIMITED_STRING(int i) {
            return getToken(MonsterGrammar.DELIMITED_STRING, i);
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
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterMessageInvis(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitMessageInvis(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitMessageInvis(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MessageInvisContext messageInvis() throws RecognitionException {
        MessageInvisContext _localctx = new MessageInvisContext(_ctx, getState());
        enterRule(_localctx, 52, RULE_messageInvis);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(211);
                match(MESSAGE_INVIS);
                setState(212);
                ((MessageInvisContext) _localctx).s = match(DELIMITED_STRING);
                ((MessageInvisContext) _localctx).spell = ((MessageInvisContext) _localctx).s.getText();
                setState(214);
                match(COLON);
                setState(215);
                ((MessageInvisContext) _localctx).l = match(DELIMITED_STRING);
                ((MessageInvisContext) _localctx).line = ((MessageInvisContext) _localctx).l.getText();
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
        public String spell;
        public String line;
        public Token s;
        public Token l;

        public TerminalNode MESSAGE_MISS() {
            return getToken(MonsterGrammar.MESSAGE_MISS, 0);
        }

        public TerminalNode COLON() {
            return getToken(MonsterGrammar.COLON, 0);
        }

        public List<TerminalNode> DELIMITED_STRING() {
            return getTokens(MonsterGrammar.DELIMITED_STRING);
        }

        public TerminalNode DELIMITED_STRING(int i) {
            return getToken(MonsterGrammar.DELIMITED_STRING, i);
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
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterMessageMiss(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitMessageMiss(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitMessageMiss(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MessageMissContext messageMiss() throws RecognitionException {
        MessageMissContext _localctx = new MessageMissContext(_ctx, getState());
        enterRule(_localctx, 54, RULE_messageMiss);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(218);
                match(MESSAGE_MISS);
                setState(219);
                ((MessageMissContext) _localctx).s = match(DELIMITED_STRING);
                ((MessageMissContext) _localctx).spell = ((MessageMissContext) _localctx).s.getText();
                setState(221);
                match(COLON);
                setState(222);
                ((MessageMissContext) _localctx).l = match(DELIMITED_STRING);
                ((MessageMissContext) _localctx).line = ((MessageMissContext) _localctx).l.getText();
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
        public String line;
        public Token STRING;

        public TerminalNode DESC() {
            return getToken(MonsterGrammar.DESC, 0);
        }

        public TerminalNode STRING() {
            return getToken(MonsterGrammar.STRING, 0);
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
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterDesc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitDesc(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitDesc(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DescContext desc() throws RecognitionException {
        DescContext _localctx = new DescContext(_ctx, getState());
        enterRule(_localctx, 56, RULE_desc);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(225);
                match(DESC);
                setState(226);
                ((DescContext) _localctx).STRING = match(STRING);
                ((DescContext) _localctx).line = ((DescContext) _localctx).STRING.getText();
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
    public static class DropContext extends ParserRuleContext {
        public String typeStr;
        public String nameStr;
        public String chanceVal;
        public String min;
        public String max;
        public Token t;
        public Token s;
        public Token c;
        public Token n;
        public Token x;

        public TerminalNode DROP() {
            return getToken(MonsterGrammar.DROP, 0);
        }

        public List<TerminalNode> DROP_COLON() {
            return getTokens(MonsterGrammar.DROP_COLON);
        }

        public TerminalNode DROP_COLON(int i) {
            return getToken(MonsterGrammar.DROP_COLON, i);
        }

        public List<TerminalNode> DROP_STRING() {
            return getTokens(MonsterGrammar.DROP_STRING);
        }

        public TerminalNode DROP_STRING(int i) {
            return getToken(MonsterGrammar.DROP_STRING, i);
        }

        public List<TerminalNode> DROP_INTEGER() {
            return getTokens(MonsterGrammar.DROP_INTEGER);
        }

        public TerminalNode DROP_INTEGER(int i) {
            return getToken(MonsterGrammar.DROP_INTEGER, i);
        }

        public DropContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_drop;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterDrop(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitDrop(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitDrop(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DropContext drop() throws RecognitionException {
        DropContext _localctx = new DropContext(_ctx, getState());
        enterRule(_localctx, 58, RULE_drop);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(229);
                match(DROP);
                setState(230);
                ((DropContext) _localctx).t = match(DROP_STRING);
                setState(231);
                match(DROP_COLON);
                setState(232);
                ((DropContext) _localctx).s = match(DROP_STRING);
                setState(233);
                match(DROP_COLON);
                setState(234);
                ((DropContext) _localctx).c = match(DROP_INTEGER);
                setState(235);
                match(DROP_COLON);
                setState(236);
                ((DropContext) _localctx).n = match(DROP_INTEGER);
                setState(237);
                match(DROP_COLON);
                setState(238);
                ((DropContext) _localctx).x = match(DROP_INTEGER);

                ((DropContext) _localctx).typeStr = ((DropContext) _localctx).t.getText();
                ((DropContext) _localctx).nameStr = ((DropContext) _localctx).s.getText();
                ((DropContext) _localctx).chanceVal = ((DropContext) _localctx).c.getText();
                ((DropContext) _localctx).min = ((DropContext) _localctx).n.getText();
                ((DropContext) _localctx).max = ((DropContext) _localctx).x.getText();

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
    public static class DropBaseContext extends ParserRuleContext {
        public String typeStr;
        public String chanceVal;
        public String min;
        public String max;
        public Token t;
        public Token c;
        public Token n;
        public Token x;

        public TerminalNode DROP_BASE() {
            return getToken(MonsterGrammar.DROP_BASE, 0);
        }

        public List<TerminalNode> DROP_COLON() {
            return getTokens(MonsterGrammar.DROP_COLON);
        }

        public TerminalNode DROP_COLON(int i) {
            return getToken(MonsterGrammar.DROP_COLON, i);
        }

        public TerminalNode DROP_STRING() {
            return getToken(MonsterGrammar.DROP_STRING, 0);
        }

        public List<TerminalNode> DROP_INTEGER() {
            return getTokens(MonsterGrammar.DROP_INTEGER);
        }

        public TerminalNode DROP_INTEGER(int i) {
            return getToken(MonsterGrammar.DROP_INTEGER, i);
        }

        public DropBaseContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_dropBase;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterDropBase(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitDropBase(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitDropBase(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DropBaseContext dropBase() throws RecognitionException {
        DropBaseContext _localctx = new DropBaseContext(_ctx, getState());
        enterRule(_localctx, 60, RULE_dropBase);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(241);
                match(DROP_BASE);
                setState(242);
                ((DropBaseContext) _localctx).t = match(DROP_STRING);
                setState(243);
                match(DROP_COLON);
                setState(244);
                ((DropBaseContext) _localctx).c = match(DROP_INTEGER);
                setState(245);
                match(DROP_COLON);
                setState(246);
                ((DropBaseContext) _localctx).n = match(DROP_INTEGER);
                setState(247);
                match(DROP_COLON);
                setState(248);
                ((DropBaseContext) _localctx).x = match(DROP_INTEGER);

                ((DropBaseContext) _localctx).typeStr = ((DropBaseContext) _localctx).t.getText();
                ((DropBaseContext) _localctx).chanceVal = ((DropBaseContext) _localctx).c.getText();
                ((DropBaseContext) _localctx).min = ((DropBaseContext) _localctx).n.getText();
                ((DropBaseContext) _localctx).max = ((DropBaseContext) _localctx).x.getText();

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
    public static class MimicContext extends ParserRuleContext {
        public String tVal;
        public String sVal;
        public Token t;
        public Token s;

        public TerminalNode MIMIC() {
            return getToken(MonsterGrammar.MIMIC, 0);
        }

        public TerminalNode COLON() {
            return getToken(MonsterGrammar.COLON, 0);
        }

        public List<TerminalNode> DELIMITED_STRING() {
            return getTokens(MonsterGrammar.DELIMITED_STRING);
        }

        public TerminalNode DELIMITED_STRING(int i) {
            return getToken(MonsterGrammar.DELIMITED_STRING, i);
        }

        public MimicContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_mimic;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterMimic(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitMimic(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitMimic(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MimicContext mimic() throws RecognitionException {
        MimicContext _localctx = new MimicContext(_ctx, getState());
        enterRule(_localctx, 62, RULE_mimic);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(251);
                match(MIMIC);
                setState(252);
                ((MimicContext) _localctx).t = match(DELIMITED_STRING);
                setState(253);
                match(COLON);
                setState(254);
                ((MimicContext) _localctx).s = match(DELIMITED_STRING);

                ((MimicContext) _localctx).tVal = ((MimicContext) _localctx).t.getText();
                ((MimicContext) _localctx).sVal = ((MimicContext) _localctx).s.getText();
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
    public static class FriendsContext extends ParserRuleContext {
        public String chance;
        public String number;
        public String nameStr;
        public String role;
        public Token c;
        public Token v;
        public Token n;
        public Token r;

        public TerminalNode FRIENDS() {
            return getToken(MonsterGrammar.FRIENDS, 0);
        }

        public List<TerminalNode> FRIEND_COLON() {
            return getTokens(MonsterGrammar.FRIEND_COLON);
        }

        public TerminalNode FRIEND_COLON(int i) {
            return getToken(MonsterGrammar.FRIEND_COLON, i);
        }

        public TerminalNode FRIEND_INTEGER() {
            return getToken(MonsterGrammar.FRIEND_INTEGER, 0);
        }

        public TerminalNode FRIEND_DICE() {
            return getToken(MonsterGrammar.FRIEND_DICE, 0);
        }

        public List<TerminalNode> FRIEND_NAME() {
            return getTokens(MonsterGrammar.FRIEND_NAME);
        }

        public TerminalNode FRIEND_NAME(int i) {
            return getToken(MonsterGrammar.FRIEND_NAME, i);
        }

        public FriendsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_friends;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterFriends(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitFriends(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitFriends(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FriendsContext friends() throws RecognitionException {
        FriendsContext _localctx = new FriendsContext(_ctx, getState());
        enterRule(_localctx, 64, RULE_friends);

        ((FriendsContext) _localctx).chance = "";
        ((FriendsContext) _localctx).number = "";
        ((FriendsContext) _localctx).nameStr = "";
        ((FriendsContext) _localctx).role = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(257);
                match(FRIENDS);
                setState(258);
                ((FriendsContext) _localctx).c = match(FRIEND_INTEGER);
                setState(259);
                match(FRIEND_COLON);
                setState(260);
                ((FriendsContext) _localctx).v = match(FRIEND_DICE);
                setState(261);
                match(FRIEND_COLON);
                setState(262);
                ((FriendsContext) _localctx).n = match(FRIEND_NAME);
                ((FriendsContext) _localctx).chance = ((FriendsContext) _localctx).c.getText();
                ((FriendsContext) _localctx).number = ((FriendsContext) _localctx).v.getText();
                ((FriendsContext) _localctx).nameStr = ((FriendsContext) _localctx).n.getText();
                setState(267);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == FRIEND_COLON) {
                    {
                        setState(264);
                        match(FRIEND_COLON);
                        setState(265);
                        ((FriendsContext) _localctx).r = match(FRIEND_NAME);
                        ((FriendsContext) _localctx).role = ((FriendsContext) _localctx).r.getText();
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
    public static class FriendsBaseContext extends ParserRuleContext {
        public String chance;
        public String number;
        public String nameStr;
        public String role;
        public Token c;
        public Token v;
        public Token n;
        public Token r;

        public TerminalNode FRIENDS_BASE() {
            return getToken(MonsterGrammar.FRIENDS_BASE, 0);
        }

        public List<TerminalNode> FRIEND_COLON() {
            return getTokens(MonsterGrammar.FRIEND_COLON);
        }

        public TerminalNode FRIEND_COLON(int i) {
            return getToken(MonsterGrammar.FRIEND_COLON, i);
        }

        public TerminalNode FRIEND_INTEGER() {
            return getToken(MonsterGrammar.FRIEND_INTEGER, 0);
        }

        public TerminalNode FRIEND_DICE() {
            return getToken(MonsterGrammar.FRIEND_DICE, 0);
        }

        public List<TerminalNode> FRIEND_NAME() {
            return getTokens(MonsterGrammar.FRIEND_NAME);
        }

        public TerminalNode FRIEND_NAME(int i) {
            return getToken(MonsterGrammar.FRIEND_NAME, i);
        }

        public FriendsBaseContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_friendsBase;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterFriendsBase(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitFriendsBase(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitFriendsBase(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FriendsBaseContext friendsBase() throws RecognitionException {
        FriendsBaseContext _localctx = new FriendsBaseContext(_ctx, getState());
        enterRule(_localctx, 66, RULE_friendsBase);

        ((FriendsBaseContext) _localctx).chance = "";
        ((FriendsBaseContext) _localctx).number = "";
        ((FriendsBaseContext) _localctx).nameStr = "";
        ((FriendsBaseContext) _localctx).role = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(269);
                match(FRIENDS_BASE);
                setState(270);
                ((FriendsBaseContext) _localctx).c = match(FRIEND_INTEGER);
                setState(271);
                match(FRIEND_COLON);
                setState(272);
                ((FriendsBaseContext) _localctx).v = match(FRIEND_DICE);
                setState(273);
                match(FRIEND_COLON);
                setState(274);
                ((FriendsBaseContext) _localctx).n = match(FRIEND_NAME);
                ((FriendsBaseContext) _localctx).chance = ((FriendsBaseContext) _localctx).c.getText();
                ((FriendsBaseContext) _localctx).number = ((FriendsBaseContext) _localctx).v.getText();
                ((FriendsBaseContext) _localctx).nameStr = ((FriendsBaseContext) _localctx).n.getText();
                setState(279);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == FRIEND_COLON) {
                    {
                        setState(276);
                        match(FRIEND_COLON);
                        setState(277);
                        ((FriendsBaseContext) _localctx).r = match(FRIEND_NAME);
                        ((FriendsBaseContext) _localctx).role = ((FriendsBaseContext) _localctx).r.getText();
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
    public static class MonsterContext extends ParserRuleContext {
        public MonsterParseRecord monsterRecord;
        public NameContext name;
        public PluralContext plural;
        public BaseContext base;
        public GlyphContext glyph;
        public ColourContext colour;
        public SpeedContext speed;
        public HitPointsContext hitPoints;
        public LightContext light;
        public HearingContext hearing;
        public SmellContext smell;
        public ShapeContext shape;
        public ColourCycleContext colourCycle;
        public ArmourClassContext armourClass;
        public SleepinessContext sleepiness;
        public DepthLevelContext depthLevel;
        public RarityContext rarity;
        public ExperienceContext experience;
        public BlowContext blow;
        public FlagsContext flags;
        public FlagsOffContext flagsOff;
        public InnateFreqContext innateFreq;
        public SpellFreqContext spellFreq;
        public SpellPowerContext spellPower;
        public SpellsContext spells;
        public MessageVisContext messageVis;
        public MessageInvisContext messageInvis;
        public MessageMissContext messageMiss;
        public DescContext desc;
        public DropContext drop;
        public DropBaseContext dropBase;
        public MimicContext mimic;
        public FriendsContext friends;
        public FriendsBaseContext friendsBase;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public List<PluralContext> plural() {
            return getRuleContexts(PluralContext.class);
        }

        public PluralContext plural(int i) {
            return getRuleContext(PluralContext.class, i);
        }

        public List<BaseContext> base() {
            return getRuleContexts(BaseContext.class);
        }

        public BaseContext base(int i) {
            return getRuleContext(BaseContext.class, i);
        }

        public List<GlyphContext> glyph() {
            return getRuleContexts(GlyphContext.class);
        }

        public GlyphContext glyph(int i) {
            return getRuleContext(GlyphContext.class, i);
        }

        public List<ColourContext> colour() {
            return getRuleContexts(ColourContext.class);
        }

        public ColourContext colour(int i) {
            return getRuleContext(ColourContext.class, i);
        }

        public List<SpeedContext> speed() {
            return getRuleContexts(SpeedContext.class);
        }

        public SpeedContext speed(int i) {
            return getRuleContext(SpeedContext.class, i);
        }

        public List<HitPointsContext> hitPoints() {
            return getRuleContexts(HitPointsContext.class);
        }

        public HitPointsContext hitPoints(int i) {
            return getRuleContext(HitPointsContext.class, i);
        }

        public List<LightContext> light() {
            return getRuleContexts(LightContext.class);
        }

        public LightContext light(int i) {
            return getRuleContext(LightContext.class, i);
        }

        public List<HearingContext> hearing() {
            return getRuleContexts(HearingContext.class);
        }

        public HearingContext hearing(int i) {
            return getRuleContext(HearingContext.class, i);
        }

        public List<SmellContext> smell() {
            return getRuleContexts(SmellContext.class);
        }

        public SmellContext smell(int i) {
            return getRuleContext(SmellContext.class, i);
        }

        public List<ShapeContext> shape() {
            return getRuleContexts(ShapeContext.class);
        }

        public ShapeContext shape(int i) {
            return getRuleContext(ShapeContext.class, i);
        }

        public List<ColourCycleContext> colourCycle() {
            return getRuleContexts(ColourCycleContext.class);
        }

        public ColourCycleContext colourCycle(int i) {
            return getRuleContext(ColourCycleContext.class, i);
        }

        public List<ArmourClassContext> armourClass() {
            return getRuleContexts(ArmourClassContext.class);
        }

        public ArmourClassContext armourClass(int i) {
            return getRuleContext(ArmourClassContext.class, i);
        }

        public List<SleepinessContext> sleepiness() {
            return getRuleContexts(SleepinessContext.class);
        }

        public SleepinessContext sleepiness(int i) {
            return getRuleContext(SleepinessContext.class, i);
        }

        public List<DepthLevelContext> depthLevel() {
            return getRuleContexts(DepthLevelContext.class);
        }

        public DepthLevelContext depthLevel(int i) {
            return getRuleContext(DepthLevelContext.class, i);
        }

        public List<RarityContext> rarity() {
            return getRuleContexts(RarityContext.class);
        }

        public RarityContext rarity(int i) {
            return getRuleContext(RarityContext.class, i);
        }

        public List<ExperienceContext> experience() {
            return getRuleContexts(ExperienceContext.class);
        }

        public ExperienceContext experience(int i) {
            return getRuleContext(ExperienceContext.class, i);
        }

        public List<BlowContext> blow() {
            return getRuleContexts(BlowContext.class);
        }

        public BlowContext blow(int i) {
            return getRuleContext(BlowContext.class, i);
        }

        public List<FlagsContext> flags() {
            return getRuleContexts(FlagsContext.class);
        }

        public FlagsContext flags(int i) {
            return getRuleContext(FlagsContext.class, i);
        }

        public List<FlagsOffContext> flagsOff() {
            return getRuleContexts(FlagsOffContext.class);
        }

        public FlagsOffContext flagsOff(int i) {
            return getRuleContext(FlagsOffContext.class, i);
        }

        public List<InnateFreqContext> innateFreq() {
            return getRuleContexts(InnateFreqContext.class);
        }

        public InnateFreqContext innateFreq(int i) {
            return getRuleContext(InnateFreqContext.class, i);
        }

        public List<SpellFreqContext> spellFreq() {
            return getRuleContexts(SpellFreqContext.class);
        }

        public SpellFreqContext spellFreq(int i) {
            return getRuleContext(SpellFreqContext.class, i);
        }

        public List<SpellPowerContext> spellPower() {
            return getRuleContexts(SpellPowerContext.class);
        }

        public SpellPowerContext spellPower(int i) {
            return getRuleContext(SpellPowerContext.class, i);
        }

        public List<SpellsContext> spells() {
            return getRuleContexts(SpellsContext.class);
        }

        public SpellsContext spells(int i) {
            return getRuleContext(SpellsContext.class, i);
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

        public List<DescContext> desc() {
            return getRuleContexts(DescContext.class);
        }

        public DescContext desc(int i) {
            return getRuleContext(DescContext.class, i);
        }

        public List<DropContext> drop() {
            return getRuleContexts(DropContext.class);
        }

        public DropContext drop(int i) {
            return getRuleContext(DropContext.class, i);
        }

        public List<DropBaseContext> dropBase() {
            return getRuleContexts(DropBaseContext.class);
        }

        public DropBaseContext dropBase(int i) {
            return getRuleContext(DropBaseContext.class, i);
        }

        public List<MimicContext> mimic() {
            return getRuleContexts(MimicContext.class);
        }

        public MimicContext mimic(int i) {
            return getRuleContext(MimicContext.class, i);
        }

        public List<FriendsContext> friends() {
            return getRuleContexts(FriendsContext.class);
        }

        public FriendsContext friends(int i) {
            return getRuleContext(FriendsContext.class, i);
        }

        public List<FriendsBaseContext> friendsBase() {
            return getRuleContexts(FriendsBaseContext.class);
        }

        public FriendsBaseContext friendsBase(int i) {
            return getRuleContext(FriendsBaseContext.class, i);
        }

        public MonsterContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_monster;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterMonster(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitMonster(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitMonster(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MonsterContext monster() throws RecognitionException {
        MonsterContext _localctx = new MonsterContext(_ctx, getState());
        enterRule(_localctx, 68, RULE_monster);

        String nameInit = "";
        String pluralInit = "";
        String baseInit = "";
        String glyphInit = "";
        String colourInit = "";
        String speedInit = "";
        String hitPointsInit = "";
        String lightInit = "";
        String hearingInit = "";
        String smellInit = "";
        List<String> shapeInit = new ArrayList<>();
        String colourCycleGroupInit = "";
        String colourCycleNameInit = "";
        String armourClassInit = "";
        String sleepinessInit = "";
        String depthInit = "";
        String rarityInit = "";
        String experienceInit = "";
        List<MonsterBlowParseRecord> blowsList = new ArrayList<>();
        List<String> flagsList = new ArrayList<>();
        List<String> flagsOffList = new ArrayList<>();
        String innateFreqInit = "";
        String spellFreqInit = "";
        String spellPowerInit = "";
        List<String> spellsList = new ArrayList<>();
        Map<String, String> visSB = new HashMap<>();
        Map<String, String> invisSB = new HashMap<>();
        Map<String, String> missSB = new HashMap<>();
        StringBuilder descSB = new StringBuilder();
        List<MonsterDropParseRecord> dropsList = new ArrayList<>();
        List<MonsterDropBaseParseRecord> dropsBaseList = new ArrayList<>();
        List<MonsterMimicParseRecord> mimics = new ArrayList();
        List<MonsterFriendsParseRecord> friendsList = new ArrayList<>();
        List<MonsterFriendsParseRecord> friendsBaseList = new ArrayList<>();
        int line = 0;

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(281);
                ((MonsterContext) _localctx).name = name();
                nameInit = ((MonsterContext) _localctx).name.nameStr;
                line = _localctx.start.getLine();
                setState(379);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        setState(379);
                        _errHandler.sync(this);
                        switch (_input.LA(1)) {
                            case PLURAL: {
                                setState(283);
                                ((MonsterContext) _localctx).plural = plural();
                                pluralInit = ((MonsterContext) _localctx).plural.pluralStr;
                            }
                            break;
                            case BASE: {
                                setState(286);
                                ((MonsterContext) _localctx).base = base();
                                baseInit = ((MonsterContext) _localctx).base.baseStr;
                            }
                            break;
                            case GLYPH: {
                                setState(289);
                                ((MonsterContext) _localctx).glyph = glyph();
                                glyphInit = ((MonsterContext) _localctx).glyph.glyphStr;
                            }
                            break;
                            case COLOUR: {
                                setState(292);
                                ((MonsterContext) _localctx).colour = colour();
                                colourInit = ((MonsterContext) _localctx).colour.colourStr;
                            }
                            break;
                            case SPEED: {
                                setState(295);
                                ((MonsterContext) _localctx).speed = speed();
                                speedInit = ((MonsterContext) _localctx).speed.speedVal;
                            }
                            break;
                            case HIT_POINTS: {
                                setState(298);
                                ((MonsterContext) _localctx).hitPoints = hitPoints();
                                hitPointsInit = ((MonsterContext) _localctx).hitPoints.hitPointsVal;
                            }
                            break;
                            case LIGHT: {
                                setState(301);
                                ((MonsterContext) _localctx).light = light();
                                lightInit = ((MonsterContext) _localctx).light.lightVal;
                            }
                            break;
                            case HEARING: {
                                setState(304);
                                ((MonsterContext) _localctx).hearing = hearing();
                                hearingInit = ((MonsterContext) _localctx).hearing.hearingVal;
                            }
                            break;
                            case SMELL: {
                                setState(307);
                                ((MonsterContext) _localctx).smell = smell();
                                smellInit = ((MonsterContext) _localctx).smell.smellVal;
                            }
                            break;
                            case SHAPE: {
                                setState(310);
                                ((MonsterContext) _localctx).shape = shape();
                                shapeInit.add(((MonsterContext) _localctx).shape.shapeStr);
                            }
                            break;
                            case COLOUR_CYCLE: {
                                setState(313);
                                ((MonsterContext) _localctx).colourCycle = colourCycle();
                                colourCycleGroupInit = ((MonsterContext) _localctx).colourCycle.groupStr;
                                colourCycleNameInit = ((MonsterContext) _localctx).colourCycle.nameStr;
                            }
                            break;
                            case ARMOUR_CLASS: {
                                setState(316);
                                ((MonsterContext) _localctx).armourClass = armourClass();
                                armourClassInit = ((MonsterContext) _localctx).armourClass.armourClassVal;
                            }
                            break;
                            case SLEEPINESS: {
                                setState(319);
                                ((MonsterContext) _localctx).sleepiness = sleepiness();
                                sleepinessInit = ((MonsterContext) _localctx).sleepiness.sleepinessVal;
                            }
                            break;
                            case DEPTH: {
                                setState(322);
                                ((MonsterContext) _localctx).depthLevel = depthLevel();
                                depthInit = ((MonsterContext) _localctx).depthLevel.depthVal;
                            }
                            break;
                            case RARITY: {
                                setState(325);
                                ((MonsterContext) _localctx).rarity = rarity();
                                rarityInit = ((MonsterContext) _localctx).rarity.rarityVal;
                            }
                            break;
                            case EXPERIENCE: {
                                setState(328);
                                ((MonsterContext) _localctx).experience = experience();
                                experienceInit = ((MonsterContext) _localctx).experience.experienceVal;
                            }
                            break;
                            case BLOW: {
                                setState(331);
                                ((MonsterContext) _localctx).blow = blow();
                                blowsList.add(new MonsterParseRecord.MonsterBlowParseRecord(((MonsterContext) _localctx).blow.blowType,
                                        ((MonsterContext) _localctx).blow.blowSubType, ((MonsterContext) _localctx).blow.blowDice));
                            }
                            break;
                            case FLAGS: {
                                setState(334);
                                ((MonsterContext) _localctx).flags = flags();
                                flagsList.addAll(((MonsterContext) _localctx).flags.flagsList);
                            }
                            break;
                            case FLAGS_OFF: {
                                setState(337);
                                ((MonsterContext) _localctx).flagsOff = flagsOff();
                                flagsOffList.addAll(((MonsterContext) _localctx).flagsOff.flagsList);
                            }
                            break;
                            case INNATE_FREQ: {
                                setState(340);
                                ((MonsterContext) _localctx).innateFreq = innateFreq();
                                innateFreqInit = ((MonsterContext) _localctx).innateFreq.innateFreqVal;
                            }
                            break;
                            case SPELL_FREQ: {
                                setState(343);
                                ((MonsterContext) _localctx).spellFreq = spellFreq();
                                spellFreqInit = ((MonsterContext) _localctx).spellFreq.spellFreqVal;
                            }
                            break;
                            case SPELL_POWER: {
                                setState(346);
                                ((MonsterContext) _localctx).spellPower = spellPower();
                                spellPowerInit = ((MonsterContext) _localctx).spellPower.spellPowerVal;
                            }
                            break;
                            case SPELLS: {
                                setState(349);
                                ((MonsterContext) _localctx).spells = spells();
                                spellsList.addAll(((MonsterContext) _localctx).spells.spellList);
                            }
                            break;
                            case MESSAGE_VIS: {
                                setState(352);
                                ((MonsterContext) _localctx).messageVis = messageVis();
                                visSB.put(((MonsterContext) _localctx).messageVis.spell, ((MonsterContext) _localctx).messageVis.line);
                            }
                            break;
                            case MESSAGE_INVIS: {
                                setState(355);
                                ((MonsterContext) _localctx).messageInvis = messageInvis();
                                invisSB.put(((MonsterContext) _localctx).messageInvis.spell, ((MonsterContext) _localctx).messageInvis.line);
                            }
                            break;
                            case MESSAGE_MISS: {
                                setState(358);
                                ((MonsterContext) _localctx).messageMiss = messageMiss();
                                missSB.put(((MonsterContext) _localctx).messageMiss.spell, ((MonsterContext) _localctx).messageMiss.line);
                            }
                            break;
                            case DESC: {
                                setState(361);
                                ((MonsterContext) _localctx).desc = desc();
                                descSB.append(((MonsterContext) _localctx).desc.line);
                            }
                            break;
                            case DROP: {
                                setState(364);
                                ((MonsterContext) _localctx).drop = drop();
                                dropsList.add(new MonsterDropParseRecord(((MonsterContext) _localctx).drop.typeStr,
                                        ((MonsterContext) _localctx).drop.nameStr, ((MonsterContext) _localctx).drop.chanceVal, ((MonsterContext) _localctx).drop.min, ((MonsterContext) _localctx).drop.max));
                            }
                            break;
                            case DROP_BASE: {
                                setState(367);
                                ((MonsterContext) _localctx).dropBase = dropBase();
                                dropsBaseList.add(new MonsterDropBaseParseRecord(((MonsterContext) _localctx).dropBase.typeStr,
                                        ((MonsterContext) _localctx).dropBase.chanceVal, ((MonsterContext) _localctx).dropBase.min, ((MonsterContext) _localctx).dropBase.max));
                            }
                            break;
                            case MIMIC: {
                                setState(370);
                                ((MonsterContext) _localctx).mimic = mimic();
                                mimics.add(new MonsterMimicParseRecord(((MonsterContext) _localctx).mimic.tVal, ((MonsterContext) _localctx).mimic.sVal));
                            }
                            break;
                            case FRIENDS: {
                                setState(373);
                                ((MonsterContext) _localctx).friends = friends();
                                friendsList.add(new MonsterFriendsParseRecord(((MonsterContext) _localctx).friends.chance,
                                        ((MonsterContext) _localctx).friends.number, ((MonsterContext) _localctx).friends.nameStr, ((MonsterContext) _localctx).friends.role));
                            }
                            break;
                            case FRIENDS_BASE: {
                                setState(376);
                                ((MonsterContext) _localctx).friendsBase = friendsBase();
                                friendsBaseList.add(new MonsterFriendsParseRecord(((MonsterContext) _localctx).friendsBase.chance,
                                        ((MonsterContext) _localctx).friendsBase.number, ((MonsterContext) _localctx).friendsBase.nameStr, ((MonsterContext) _localctx).friendsBase.role));
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                    }
                    setState(381);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 34359738360L) != 0));
            }
            _ctx.stop = _input.LT(-1);

            ((MonsterContext) _localctx).monsterRecord = new MonsterParseRecord(nameInit, pluralInit, baseInit,
                    glyphInit, colourInit, speedInit, hitPointsInit, lightInit, hearingInit,
                    smellInit, shapeInit, colourCycleGroupInit, colourCycleNameInit,
                    armourClassInit, sleepinessInit, depthInit, rarityInit, experienceInit,
                    blowsList, flagsList, flagsOffList, innateFreqInit, spellFreqInit,
                    spellPowerInit, spellsList, visSB, invisSB, missSB,
                    descSB.toString(), dropsList, dropsBaseList, mimics, friendsList,
                    friendsBaseList, line);

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
        public List<MonsterParseRecord> monsters;
        public RecordCountContext recordCount;
        public MonsterContext monster;

        public RecordCountContext recordCount() {
            return getRuleContext(RecordCountContext.class, 0);
        }

        public TerminalNode EOF() {
            return getToken(MonsterGrammar.EOF, 0);
        }

        public List<MonsterContext> monster() {
            return getRuleContexts(MonsterContext.class);
        }

        public MonsterContext monster(int i) {
            return getRuleContext(MonsterContext.class, i);
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
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterGrammarListener) ((MonsterGrammarListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterGrammarVisitor)
                return ((MonsterGrammarVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 70, RULE_file);

        ((FileContext) _localctx).monsters = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(383);
                ((FileContext) _localctx).recordCount = recordCount();
                ((FileContext) _localctx).declaredRecordCount = ((FileContext) _localctx).recordCount.count;
                setState(388);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(385);
                            ((FileContext) _localctx).monster = monster();
                            _localctx.monsters.add(((FileContext) _localctx).monster.monsterRecord);
                        }
                    }
                    setState(390);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(392);
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
            "\u0004\u0001>\u018b\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
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
                    "#\u0007#\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t" +
                    "\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f" +
                    "\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001" +
                    "\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001" +
                    "\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001" +
                    "\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001" +
                    "\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0003\u0012\u009c" +
                    "\b\u0012\u0003\u0012\u009e\b\u0012\u0001\u0013\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0001\u0013\u0001\u0013\u0005\u0013\u00a6\b\u0013\n\u0013" +
                    "\f\u0013\u00a9\t\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014" +
                    "\u0001\u0014\u0001\u0014\u0005\u0014\u00b1\b\u0014\n\u0014\f\u0014\u00b4" +
                    "\t\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0016\u0001" +
                    "\u0016\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0017\u0001" +
                    "\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001" +
                    "\u0018\u0005\u0018\u00c8\b\u0018\n\u0018\f\u0018\u00cb\t\u0018\u0001\u0019" +
                    "\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019" +
                    "\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a" +
                    "\u0001\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b" +
                    "\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c" +
                    "\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d" +
                    "\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d" +
                    "\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e" +
                    "\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0001\u001f\u0001\u001f" +
                    "\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001 \u0001 \u0001 " +
                    "\u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0003 \u010c\b \u0001" +
                    "!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0001!\u0003" +
                    "!\u0118\b!\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001" +
                    "\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001" +
                    "\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001" +
                    "\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001" +
                    "\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001" +
                    "\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001" +
                    "\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001" +
                    "\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001" +
                    "\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001" +
                    "\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001" +
                    "\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001" +
                    "\"\u0004\"\u017c\b\"\u000b\"\f\"\u017d\u0001#\u0001#\u0001#\u0001#\u0001" +
                    "#\u0004#\u0185\b#\u000b#\f#\u0186\u0001#\u0001#\u0001#\u0000\u0000$\u0000" +
                    "\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c" +
                    "\u001e \"$&(*,.02468:<>@BDF\u0000\u0000\u018e\u0000H\u0001\u0000\u0000" +
                    "\u0000\u0002L\u0001\u0000\u0000\u0000\u0004P\u0001\u0000\u0000\u0000\u0006" +
                    "T\u0001\u0000\u0000\u0000\bX\u0001\u0000\u0000\u0000\n\\\u0001\u0000\u0000" +
                    "\u0000\f`\u0001\u0000\u0000\u0000\u000ed\u0001\u0000\u0000\u0000\u0010" +
                    "h\u0001\u0000\u0000\u0000\u0012l\u0001\u0000\u0000\u0000\u0014p\u0001" +
                    "\u0000\u0000\u0000\u0016t\u0001\u0000\u0000\u0000\u0018x\u0001\u0000\u0000" +
                    "\u0000\u001a~\u0001\u0000\u0000\u0000\u001c\u0082\u0001\u0000\u0000\u0000" +
                    "\u001e\u0086\u0001\u0000\u0000\u0000 \u008a\u0001\u0000\u0000\u0000\"" +
                    "\u008e\u0001\u0000\u0000\u0000$\u0092\u0001\u0000\u0000\u0000&\u009f\u0001" +
                    "\u0000\u0000\u0000(\u00aa\u0001\u0000\u0000\u0000*\u00b5\u0001\u0000\u0000" +
                    "\u0000,\u00b9\u0001\u0000\u0000\u0000.\u00bd\u0001\u0000\u0000\u00000" +
                    "\u00c1\u0001\u0000\u0000\u00002\u00cc\u0001\u0000\u0000\u00004\u00d3\u0001" +
                    "\u0000\u0000\u00006\u00da\u0001\u0000\u0000\u00008\u00e1\u0001\u0000\u0000" +
                    "\u0000:\u00e5\u0001\u0000\u0000\u0000<\u00f1\u0001\u0000\u0000\u0000>" +
                    "\u00fb\u0001\u0000\u0000\u0000@\u0101\u0001\u0000\u0000\u0000B\u010d\u0001" +
                    "\u0000\u0000\u0000D\u0119\u0001\u0000\u0000\u0000F\u017f\u0001\u0000\u0000" +
                    "\u0000HI\u0005\u0001\u0000\u0000IJ\u0005#\u0000\u0000JK\u0006\u0000\uffff" +
                    "\uffff\u0000K\u0001\u0001\u0000\u0000\u0000LM\u0005\u0002\u0000\u0000" +
                    "MN\u0005(\u0000\u0000NO\u0006\u0001\uffff\uffff\u0000O\u0003\u0001\u0000" +
                    "\u0000\u0000PQ\u0005\u0003\u0000\u0000QR\u0005(\u0000\u0000RS\u0006\u0002" +
                    "\uffff\uffff\u0000S\u0005\u0001\u0000\u0000\u0000TU\u0005\u0004\u0000" +
                    "\u0000UV\u0005(\u0000\u0000VW\u0006\u0003\uffff\uffff\u0000W\u0007\u0001" +
                    "\u0000\u0000\u0000XY\u0005\u0005\u0000\u0000YZ\u0005(\u0000\u0000Z[\u0006" +
                    "\u0004\uffff\uffff\u0000[\t\u0001\u0000\u0000\u0000\\]\u0005\u0006\u0000" +
                    "\u0000]^\u0005=\u0000\u0000^_\u0006\u0005\uffff\uffff\u0000_\u000b\u0001" +
                    "\u0000\u0000\u0000`a\u0005\u0007\u0000\u0000ab\u0005#\u0000\u0000bc\u0006" +
                    "\u0006\uffff\uffff\u0000c\r\u0001\u0000\u0000\u0000de\u0005\b\u0000\u0000" +
                    "ef\u0005#\u0000\u0000fg\u0006\u0007\uffff\uffff\u0000g\u000f\u0001\u0000" +
                    "\u0000\u0000hi\u0005\t\u0000\u0000ij\u0005#\u0000\u0000jk\u0006\b\uffff" +
                    "\uffff\u0000k\u0011\u0001\u0000\u0000\u0000lm\u0005\n\u0000\u0000mn\u0005" +
                    "#\u0000\u0000no\u0006\t\uffff\uffff\u0000o\u0013\u0001\u0000\u0000\u0000" +
                    "pq\u0005 \u0000\u0000qr\u0005#\u0000\u0000rs\u0006\n\uffff\uffff\u0000" +
                    "s\u0015\u0001\u0000\u0000\u0000tu\u0005!\u0000\u0000uv\u0005(\u0000\u0000" +
                    "vw\u0006\u000b\uffff\uffff\u0000w\u0017\u0001\u0000\u0000\u0000xy\u0005" +
                    "\"\u0000\u0000yz\u00052\u0000\u0000z{\u00051\u0000\u0000{|\u00052\u0000" +
                    "\u0000|}\u0006\f\uffff\uffff\u0000}\u0019\u0001\u0000\u0000\u0000~\u007f" +
                    "\u0005\u000b\u0000\u0000\u007f\u0080\u0005#\u0000\u0000\u0080\u0081\u0006" +
                    "\r\uffff\uffff\u0000\u0081\u001b\u0001\u0000\u0000\u0000\u0082\u0083\u0005" +
                    "\f\u0000\u0000\u0083\u0084\u0005#\u0000\u0000\u0084\u0085\u0006\u000e" +
                    "\uffff\uffff\u0000\u0085\u001d\u0001\u0000\u0000\u0000\u0086\u0087\u0005" +
                    "\r\u0000\u0000\u0087\u0088\u0005#\u0000\u0000\u0088\u0089\u0006\u000f" +
                    "\uffff\uffff\u0000\u0089\u001f\u0001\u0000\u0000\u0000\u008a\u008b\u0005" +
                    "\u000e\u0000\u0000\u008b\u008c\u0005#\u0000\u0000\u008c\u008d\u0006\u0010" +
                    "\uffff\uffff\u0000\u008d!\u0001\u0000\u0000\u0000\u008e\u008f\u0005\u000f" +
                    "\u0000\u0000\u008f\u0090\u0005#\u0000\u0000\u0090\u0091\u0006\u0011\uffff" +
                    "\uffff\u0000\u0091#\u0001\u0000\u0000\u0000\u0092\u0093\u0005\u0010\u0000" +
                    "\u0000\u0093\u0094\u0005,\u0000\u0000\u0094\u009d\u0006\u0012\uffff\uffff" +
                    "\u0000\u0095\u0096\u0005*\u0000\u0000\u0096\u0097\u0005,\u0000\u0000\u0097" +
                    "\u009b\u0006\u0012\uffff\uffff\u0000\u0098\u0099\u0005*\u0000\u0000\u0099" +
                    "\u009a\u0005+\u0000\u0000\u009a\u009c\u0006\u0012\uffff\uffff\u0000\u009b" +
                    "\u0098\u0001\u0000\u0000\u0000\u009b\u009c\u0001\u0000\u0000\u0000\u009c" +
                    "\u009e\u0001\u0000\u0000\u0000\u009d\u0095\u0001\u0000\u0000\u0000\u009d" +
                    "\u009e\u0001\u0000\u0000\u0000\u009e%\u0001\u0000\u0000\u0000\u009f\u00a0" +
                    "\u0005\u0011\u0000\u0000\u00a0\u00a1\u0005.\u0000\u0000\u00a1\u00a7\u0006" +
                    "\u0013\uffff\uffff\u0000\u00a2\u00a3\u0005/\u0000\u0000\u00a3\u00a4\u0005" +
                    ".\u0000\u0000\u00a4\u00a6\u0006\u0013\uffff\uffff\u0000\u00a5\u00a2\u0001" +
                    "\u0000\u0000\u0000\u00a6\u00a9\u0001\u0000\u0000\u0000\u00a7\u00a5\u0001" +
                    "\u0000\u0000\u0000\u00a7\u00a8\u0001\u0000\u0000\u0000\u00a8\'\u0001\u0000" +
                    "\u0000\u0000\u00a9\u00a7\u0001\u0000\u0000\u0000\u00aa\u00ab\u0005\u0012" +
                    "\u0000\u0000\u00ab\u00ac\u0005.\u0000\u0000\u00ac\u00b2\u0006\u0014\uffff" +
                    "\uffff\u0000\u00ad\u00ae\u0005/\u0000\u0000\u00ae\u00af\u0005.\u0000\u0000" +
                    "\u00af\u00b1\u0006\u0014\uffff\uffff\u0000\u00b0\u00ad\u0001\u0000\u0000" +
                    "\u0000\u00b1\u00b4\u0001\u0000\u0000\u0000\u00b2\u00b0\u0001\u0000\u0000" +
                    "\u0000\u00b2\u00b3\u0001\u0000\u0000\u0000\u00b3)\u0001\u0000\u0000\u0000" +
                    "\u00b4\u00b2\u0001\u0000\u0000\u0000\u00b5\u00b6\u0005\u0013\u0000\u0000" +
                    "\u00b6\u00b7\u0005#\u0000\u0000\u00b7\u00b8\u0006\u0015\uffff\uffff\u0000" +
                    "\u00b8+\u0001\u0000\u0000\u0000\u00b9\u00ba\u0005\u0014\u0000\u0000\u00ba" +
                    "\u00bb\u0005#\u0000\u0000\u00bb\u00bc\u0006\u0016\uffff\uffff\u0000\u00bc" +
                    "-\u0001\u0000\u0000\u0000\u00bd\u00be\u0005\u0015\u0000\u0000\u00be\u00bf" +
                    "\u0005#\u0000\u0000\u00bf\u00c0\u0006\u0017\uffff\uffff\u0000\u00c0/\u0001" +
                    "\u0000\u0000\u0000\u00c1\u00c2\u0005\u0016\u0000\u0000\u00c2\u00c3\u0005" +
                    ".\u0000\u0000\u00c3\u00c9\u0006\u0018\uffff\uffff\u0000\u00c4\u00c5\u0005" +
                    "/\u0000\u0000\u00c5\u00c6\u0005.\u0000\u0000\u00c6\u00c8\u0006\u0018\uffff" +
                    "\uffff\u0000\u00c7\u00c4\u0001\u0000\u0000\u0000\u00c8\u00cb\u0001\u0000" +
                    "\u0000\u0000\u00c9\u00c7\u0001\u0000\u0000\u0000\u00c9\u00ca\u0001\u0000" +
                    "\u0000\u0000\u00ca1\u0001\u0000\u0000\u0000\u00cb\u00c9\u0001\u0000\u0000" +
                    "\u0000\u00cc\u00cd\u0005\u0017\u0000\u0000\u00cd\u00ce\u00052\u0000\u0000" +
                    "\u00ce\u00cf\u0006\u0019\uffff\uffff\u0000\u00cf\u00d0\u00051\u0000\u0000" +
                    "\u00d0\u00d1\u00052\u0000\u0000\u00d1\u00d2\u0006\u0019\uffff\uffff\u0000" +
                    "\u00d23\u0001\u0000\u0000\u0000\u00d3\u00d4\u0005\u0018\u0000\u0000\u00d4" +
                    "\u00d5\u00052\u0000\u0000\u00d5\u00d6\u0006\u001a\uffff\uffff\u0000\u00d6" +
                    "\u00d7\u00051\u0000\u0000\u00d7\u00d8\u00052\u0000\u0000\u00d8\u00d9\u0006" +
                    "\u001a\uffff\uffff\u0000\u00d95\u0001\u0000\u0000\u0000\u00da\u00db\u0005" +
                    "\u0019\u0000\u0000\u00db\u00dc\u00052\u0000\u0000\u00dc\u00dd\u0006\u001b" +
                    "\uffff\uffff\u0000\u00dd\u00de\u00051\u0000\u0000\u00de\u00df\u00052\u0000" +
                    "\u0000\u00df\u00e0\u0006\u001b\uffff\uffff\u0000\u00e07\u0001\u0000\u0000" +
                    "\u0000\u00e1\u00e2\u0005\u001a\u0000\u0000\u00e2\u00e3\u0005(\u0000\u0000" +
                    "\u00e3\u00e4\u0006\u001c\uffff\uffff\u0000\u00e49\u0001\u0000\u0000\u0000" +
                    "\u00e5\u00e6\u0005\u001b\u0000\u0000\u00e6\u00e7\u0005;\u0000\u0000\u00e7" +
                    "\u00e8\u00059\u0000\u0000\u00e8\u00e9\u0005;\u0000\u0000\u00e9\u00ea\u0005" +
                    "9\u0000\u0000\u00ea\u00eb\u0005:\u0000\u0000\u00eb\u00ec\u00059\u0000" +
                    "\u0000\u00ec\u00ed\u0005:\u0000\u0000\u00ed\u00ee\u00059\u0000\u0000\u00ee" +
                    "\u00ef\u0005:\u0000\u0000\u00ef\u00f0\u0006\u001d\uffff\uffff\u0000\u00f0" +
                    ";\u0001\u0000\u0000\u0000\u00f1\u00f2\u0005\u001c\u0000\u0000\u00f2\u00f3" +
                    "\u0005;\u0000\u0000\u00f3\u00f4\u00059\u0000\u0000\u00f4\u00f5\u0005:" +
                    "\u0000\u0000\u00f5\u00f6\u00059\u0000\u0000\u00f6\u00f7\u0005:\u0000\u0000" +
                    "\u00f7\u00f8\u00059\u0000\u0000\u00f8\u00f9\u0005:\u0000\u0000\u00f9\u00fa" +
                    "\u0006\u001e\uffff\uffff\u0000\u00fa=\u0001\u0000\u0000\u0000\u00fb\u00fc" +
                    "\u0005\u001d\u0000\u0000\u00fc\u00fd\u00052\u0000\u0000\u00fd\u00fe\u0005" +
                    "1\u0000\u0000\u00fe\u00ff\u00052\u0000\u0000\u00ff\u0100\u0006\u001f\uffff" +
                    "\uffff\u0000\u0100?\u0001\u0000\u0000\u0000\u0101\u0102\u0005\u001e\u0000" +
                    "\u0000\u0102\u0103\u00054\u0000\u0000\u0103\u0104\u00056\u0000\u0000\u0104" +
                    "\u0105\u00055\u0000\u0000\u0105\u0106\u00056\u0000\u0000\u0106\u0107\u0005" +
                    "7\u0000\u0000\u0107\u010b\u0006 \uffff\uffff\u0000\u0108\u0109\u00056" +
                    "\u0000\u0000\u0109\u010a\u00057\u0000\u0000\u010a\u010c\u0006 \uffff\uffff" +
                    "\u0000\u010b\u0108\u0001\u0000\u0000\u0000\u010b\u010c\u0001\u0000\u0000" +
                    "\u0000\u010cA\u0001\u0000\u0000\u0000\u010d\u010e\u0005\u001f\u0000\u0000" +
                    "\u010e\u010f\u00054\u0000\u0000\u010f\u0110\u00056\u0000\u0000\u0110\u0111" +
                    "\u00055\u0000\u0000\u0111\u0112\u00056\u0000\u0000\u0112\u0113\u00057" +
                    "\u0000\u0000\u0113\u0117\u0006!\uffff\uffff\u0000\u0114\u0115\u00056\u0000" +
                    "\u0000\u0115\u0116\u00057\u0000\u0000\u0116\u0118\u0006!\uffff\uffff\u0000" +
                    "\u0117\u0114\u0001\u0000\u0000\u0000\u0117\u0118\u0001\u0000\u0000\u0000" +
                    "\u0118C\u0001\u0000\u0000\u0000\u0119\u011a\u0003\u0002\u0001\u0000\u011a" +
                    "\u017b\u0006\"\uffff\uffff\u0000\u011b\u011c\u0003\u0004\u0002\u0000\u011c" +
                    "\u011d\u0006\"\uffff\uffff\u0000\u011d\u017c\u0001\u0000\u0000\u0000\u011e" +
                    "\u011f\u0003\u0006\u0003\u0000\u011f\u0120\u0006\"\uffff\uffff\u0000\u0120" +
                    "\u017c\u0001\u0000\u0000\u0000\u0121\u0122\u0003\b\u0004\u0000\u0122\u0123" +
                    "\u0006\"\uffff\uffff\u0000\u0123\u017c\u0001\u0000\u0000\u0000\u0124\u0125" +
                    "\u0003\n\u0005\u0000\u0125\u0126\u0006\"\uffff\uffff\u0000\u0126\u017c" +
                    "\u0001\u0000\u0000\u0000\u0127\u0128\u0003\f\u0006\u0000\u0128\u0129\u0006" +
                    "\"\uffff\uffff\u0000\u0129\u017c\u0001\u0000\u0000\u0000\u012a\u012b\u0003" +
                    "\u000e\u0007\u0000\u012b\u012c\u0006\"\uffff\uffff\u0000\u012c\u017c\u0001" +
                    "\u0000\u0000\u0000\u012d\u012e\u0003\u0010\b\u0000\u012e\u012f\u0006\"" +
                    "\uffff\uffff\u0000\u012f\u017c\u0001\u0000\u0000\u0000\u0130\u0131\u0003" +
                    "\u0012\t\u0000\u0131\u0132\u0006\"\uffff\uffff\u0000\u0132\u017c\u0001" +
                    "\u0000\u0000\u0000\u0133\u0134\u0003\u0014\n\u0000\u0134\u0135\u0006\"" +
                    "\uffff\uffff\u0000\u0135\u017c\u0001\u0000\u0000\u0000\u0136\u0137\u0003" +
                    "\u0016\u000b\u0000\u0137\u0138\u0006\"\uffff\uffff\u0000\u0138\u017c\u0001" +
                    "\u0000\u0000\u0000\u0139\u013a\u0003\u0018\f\u0000\u013a\u013b\u0006\"" +
                    "\uffff\uffff\u0000\u013b\u017c\u0001\u0000\u0000\u0000\u013c\u013d\u0003" +
                    "\u001a\r\u0000\u013d\u013e\u0006\"\uffff\uffff\u0000\u013e\u017c\u0001" +
                    "\u0000\u0000\u0000\u013f\u0140\u0003\u001c\u000e\u0000\u0140\u0141\u0006" +
                    "\"\uffff\uffff\u0000\u0141\u017c\u0001\u0000\u0000\u0000\u0142\u0143\u0003" +
                    "\u001e\u000f\u0000\u0143\u0144\u0006\"\uffff\uffff\u0000\u0144\u017c\u0001" +
                    "\u0000\u0000\u0000\u0145\u0146\u0003 \u0010\u0000\u0146\u0147\u0006\"" +
                    "\uffff\uffff\u0000\u0147\u017c\u0001\u0000\u0000\u0000\u0148\u0149\u0003" +
                    "\"\u0011\u0000\u0149\u014a\u0006\"\uffff\uffff\u0000\u014a\u017c\u0001" +
                    "\u0000\u0000\u0000\u014b\u014c\u0003$\u0012\u0000\u014c\u014d\u0006\"" +
                    "\uffff\uffff\u0000\u014d\u017c\u0001\u0000\u0000\u0000\u014e\u014f\u0003" +
                    "&\u0013\u0000\u014f\u0150\u0006\"\uffff\uffff\u0000\u0150\u017c\u0001" +
                    "\u0000\u0000\u0000\u0151\u0152\u0003(\u0014\u0000\u0152\u0153\u0006\"" +
                    "\uffff\uffff\u0000\u0153\u017c\u0001\u0000\u0000\u0000\u0154\u0155\u0003" +
                    "*\u0015\u0000\u0155\u0156\u0006\"\uffff\uffff\u0000\u0156\u017c\u0001" +
                    "\u0000\u0000\u0000\u0157\u0158\u0003,\u0016\u0000\u0158\u0159\u0006\"" +
                    "\uffff\uffff\u0000\u0159\u017c\u0001\u0000\u0000\u0000\u015a\u015b\u0003" +
                    ".\u0017\u0000\u015b\u015c\u0006\"\uffff\uffff\u0000\u015c\u017c\u0001" +
                    "\u0000\u0000\u0000\u015d\u015e\u00030\u0018\u0000\u015e\u015f\u0006\"" +
                    "\uffff\uffff\u0000\u015f\u017c\u0001\u0000\u0000\u0000\u0160\u0161\u0003" +
                    "2\u0019\u0000\u0161\u0162\u0006\"\uffff\uffff\u0000\u0162\u017c\u0001" +
                    "\u0000\u0000\u0000\u0163\u0164\u00034\u001a\u0000\u0164\u0165\u0006\"" +
                    "\uffff\uffff\u0000\u0165\u017c\u0001\u0000\u0000\u0000\u0166\u0167\u0003" +
                    "6\u001b\u0000\u0167\u0168\u0006\"\uffff\uffff\u0000\u0168\u017c\u0001" +
                    "\u0000\u0000\u0000\u0169\u016a\u00038\u001c\u0000\u016a\u016b\u0006\"" +
                    "\uffff\uffff\u0000\u016b\u017c\u0001\u0000\u0000\u0000\u016c\u016d\u0003" +
                    ":\u001d\u0000\u016d\u016e\u0006\"\uffff\uffff\u0000\u016e\u017c\u0001" +
                    "\u0000\u0000\u0000\u016f\u0170\u0003<\u001e\u0000\u0170\u0171\u0006\"" +
                    "\uffff\uffff\u0000\u0171\u017c\u0001\u0000\u0000\u0000\u0172\u0173\u0003" +
                    ">\u001f\u0000\u0173\u0174\u0006\"\uffff\uffff\u0000\u0174\u017c\u0001" +
                    "\u0000\u0000\u0000\u0175\u0176\u0003@ \u0000\u0176\u0177\u0006\"\uffff" +
                    "\uffff\u0000\u0177\u017c\u0001\u0000\u0000\u0000\u0178\u0179\u0003B!\u0000" +
                    "\u0179\u017a\u0006\"\uffff\uffff\u0000\u017a\u017c\u0001\u0000\u0000\u0000" +
                    "\u017b\u011b\u0001\u0000\u0000\u0000\u017b\u011e\u0001\u0000\u0000\u0000" +
                    "\u017b\u0121\u0001\u0000\u0000\u0000\u017b\u0124\u0001\u0000\u0000\u0000" +
                    "\u017b\u0127\u0001\u0000\u0000\u0000\u017b\u012a\u0001\u0000\u0000\u0000" +
                    "\u017b\u012d\u0001\u0000\u0000\u0000\u017b\u0130\u0001\u0000\u0000\u0000" +
                    "\u017b\u0133\u0001\u0000\u0000\u0000\u017b\u0136\u0001\u0000\u0000\u0000" +
                    "\u017b\u0139\u0001\u0000\u0000\u0000\u017b\u013c\u0001\u0000\u0000\u0000" +
                    "\u017b\u013f\u0001\u0000\u0000\u0000\u017b\u0142\u0001\u0000\u0000\u0000" +
                    "\u017b\u0145\u0001\u0000\u0000\u0000\u017b\u0148\u0001\u0000\u0000\u0000" +
                    "\u017b\u014b\u0001\u0000\u0000\u0000\u017b\u014e\u0001\u0000\u0000\u0000" +
                    "\u017b\u0151\u0001\u0000\u0000\u0000\u017b\u0154\u0001\u0000\u0000\u0000" +
                    "\u017b\u0157\u0001\u0000\u0000\u0000\u017b\u015a\u0001\u0000\u0000\u0000" +
                    "\u017b\u015d\u0001\u0000\u0000\u0000\u017b\u0160\u0001\u0000\u0000\u0000" +
                    "\u017b\u0163\u0001\u0000\u0000\u0000\u017b\u0166\u0001\u0000\u0000\u0000" +
                    "\u017b\u0169\u0001\u0000\u0000\u0000\u017b\u016c\u0001\u0000\u0000\u0000" +
                    "\u017b\u016f\u0001\u0000\u0000\u0000\u017b\u0172\u0001\u0000\u0000\u0000" +
                    "\u017b\u0175\u0001\u0000\u0000\u0000\u017b\u0178\u0001\u0000\u0000\u0000" +
                    "\u017c\u017d\u0001\u0000\u0000\u0000\u017d\u017b\u0001\u0000\u0000\u0000" +
                    "\u017d\u017e\u0001\u0000\u0000\u0000\u017eE\u0001\u0000\u0000\u0000\u017f" +
                    "\u0180\u0003\u0000\u0000\u0000\u0180\u0184\u0006#\uffff\uffff\u0000\u0181" +
                    "\u0182\u0003D\"\u0000\u0182\u0183\u0006#\uffff\uffff\u0000\u0183\u0185" +
                    "\u0001\u0000\u0000\u0000\u0184\u0181\u0001\u0000\u0000\u0000\u0185\u0186" +
                    "\u0001\u0000\u0000\u0000\u0186\u0184\u0001\u0000\u0000\u0000\u0186\u0187" +
                    "\u0001\u0000\u0000\u0000\u0187\u0188\u0001\u0000\u0000\u0000\u0188\u0189" +
                    "\u0005\u0000\u0000\u0001\u0189G\u0001\u0000\u0000\u0000\n\u009b\u009d" +
                    "\u00a7\u00b2\u00c9\u010b\u0117\u017b\u017d\u0186";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}