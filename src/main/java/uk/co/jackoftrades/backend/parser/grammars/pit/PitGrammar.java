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

// Generated from PitGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.pit;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.parser.pit.PitParseRecord;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class PitGrammar extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            RECORD_COUNT = 1, NAME = 2, ROOM = 3, ALLOC = 4, OBJ_RARITY = 5, COLOUR = 6, MON_BASE = 7,
            FLAGS_REQ = 8, FLAGS_BAN = 9, INNATE_FREQ = 10, SPELL_REQ = 11, SPELL_BAN = 12,
            MON_BAN = 13, PIT_INTEGER = 14, COMMENT = 15, EOL = 16, FLAG = 17, FLAG_OR = 18, FLAG_EOL = 19,
            STRING = 20, REST_OF_LINE_EOL = 21, ALLOC_COLON = 22, ALLOC_INTEGER = 23, ALLOC_EOL = 24,
            COLOUR_CHAR = 25, COLOUR_EOL = 26;
    public static final int
            RULE_recordCount = 0, RULE_name = 1, RULE_room = 2, RULE_alloc = 3, RULE_objRarity = 4,
            RULE_colour = 5, RULE_monBase = 6, RULE_flagsReq = 7, RULE_flagsBan = 8,
            RULE_innateFreq = 9, RULE_spellReq = 10, RULE_spellBan = 11, RULE_monBan = 12,
            RULE_pitRecord = 13, RULE_file = 14;

    private static String[] makeRuleNames() {
        return new String[]{
                "recordCount", "name", "room", "alloc", "objRarity", "colour", "monBase",
                "flagsReq", "flagsBan", "innateFreq", "spellReq", "spellBan", "monBan",
                "pitRecord", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'record-count:'", "'name:'", "'room:'", "'alloc:'", "'obj-rarity:'",
                "'color:'", "'mon-base:'", "'flags-req:'", "'flags-ban:'", "'innate-freq:'",
                "'spell-req:'", "'spell-ban:'", "'mon-ban:'", null, null, null, null,
                null, null, null, null, "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "RECORD_COUNT", "NAME", "ROOM", "ALLOC", "OBJ_RARITY", "COLOUR",
                "MON_BASE", "FLAGS_REQ", "FLAGS_BAN", "INNATE_FREQ", "SPELL_REQ", "SPELL_BAN",
                "MON_BAN", "PIT_INTEGER", "COMMENT", "EOL", "FLAG", "FLAG_OR", "FLAG_EOL",
                "STRING", "REST_OF_LINE_EOL", "ALLOC_COLON", "ALLOC_INTEGER", "ALLOC_EOL",
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
        return "PitGrammar.g4";
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

    public PitGrammar(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class RecordCountContext extends ParserRuleContext {
        public String count;
        public Token PIT_INTEGER;

        public TerminalNode RECORD_COUNT() {
            return getToken(PitGrammar.RECORD_COUNT, 0);
        }

        public TerminalNode PIT_INTEGER() {
            return getToken(PitGrammar.PIT_INTEGER, 0);
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
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).enterRecordCount(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).exitRecordCount(this);
        }
    }

    public final RecordCountContext recordCount() throws RecognitionException {
        RecordCountContext _localctx = new RecordCountContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_recordCount);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(30);
                match(RECORD_COUNT);
                setState(31);
                ((RecordCountContext) _localctx).PIT_INTEGER = match(PIT_INTEGER);
                ((RecordCountContext) _localctx).count = ((RecordCountContext) _localctx).PIT_INTEGER.getText();
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
            return getToken(PitGrammar.NAME, 0);
        }

        public TerminalNode STRING() {
            return getToken(PitGrammar.STRING, 0);
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
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).exitName(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(34);
                match(NAME);
                setState(35);
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
    public static class RoomContext extends ParserRuleContext {
        public String roomType;
        public Token PIT_INTEGER;

        public TerminalNode ROOM() {
            return getToken(PitGrammar.ROOM, 0);
        }

        public TerminalNode PIT_INTEGER() {
            return getToken(PitGrammar.PIT_INTEGER, 0);
        }

        public RoomContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_room;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).enterRoom(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).exitRoom(this);
        }
    }

    public final RoomContext room() throws RecognitionException {
        RoomContext _localctx = new RoomContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_room);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(38);
                match(ROOM);
                setState(39);
                ((RoomContext) _localctx).PIT_INTEGER = match(PIT_INTEGER);
                ((RoomContext) _localctx).roomType = ((RoomContext) _localctx).PIT_INTEGER.getText();
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
    public static class AllocContext extends ParserRuleContext {
        public String rarity;
        public String averageLevel;
        public Token r;
        public Token l;

        public TerminalNode ALLOC() {
            return getToken(PitGrammar.ALLOC, 0);
        }

        public TerminalNode ALLOC_COLON() {
            return getToken(PitGrammar.ALLOC_COLON, 0);
        }

        public List<TerminalNode> ALLOC_INTEGER() {
            return getTokens(PitGrammar.ALLOC_INTEGER);
        }

        public TerminalNode ALLOC_INTEGER(int i) {
            return getToken(PitGrammar.ALLOC_INTEGER, i);
        }

        public AllocContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_alloc;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).enterAlloc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).exitAlloc(this);
        }
    }

    public final AllocContext alloc() throws RecognitionException {
        AllocContext _localctx = new AllocContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_alloc);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(42);
                match(ALLOC);
                setState(43);
                ((AllocContext) _localctx).r = match(ALLOC_INTEGER);
                setState(44);
                match(ALLOC_COLON);
                setState(45);
                ((AllocContext) _localctx).l = match(ALLOC_INTEGER);
                ((AllocContext) _localctx).rarity = ((AllocContext) _localctx).r.getText();
                ((AllocContext) _localctx).averageLevel = ((AllocContext) _localctx).l.getText();
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
    public static class ObjRarityContext extends ParserRuleContext {
        public String objectRarity;
        public Token PIT_INTEGER;

        public TerminalNode OBJ_RARITY() {
            return getToken(PitGrammar.OBJ_RARITY, 0);
        }

        public TerminalNode PIT_INTEGER() {
            return getToken(PitGrammar.PIT_INTEGER, 0);
        }

        public ObjRarityContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_objRarity;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).enterObjRarity(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).exitObjRarity(this);
        }
    }

    public final ObjRarityContext objRarity() throws RecognitionException {
        ObjRarityContext _localctx = new ObjRarityContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_objRarity);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(48);
                match(OBJ_RARITY);
                setState(49);
                ((ObjRarityContext) _localctx).PIT_INTEGER = match(PIT_INTEGER);
                ((ObjRarityContext) _localctx).objectRarity = ((ObjRarityContext) _localctx).PIT_INTEGER.getText();
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
        public String colourChar;
        public Token COLOUR_CHAR;

        public TerminalNode COLOUR() {
            return getToken(PitGrammar.COLOUR, 0);
        }

        public TerminalNode COLOUR_CHAR() {
            return getToken(PitGrammar.COLOUR_CHAR, 0);
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
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).enterColour(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).exitColour(this);
        }
    }

    public final ColourContext colour() throws RecognitionException {
        ColourContext _localctx = new ColourContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_colour);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(52);
                match(COLOUR);
                setState(53);
                ((ColourContext) _localctx).COLOUR_CHAR = match(COLOUR_CHAR);
                ((ColourContext) _localctx).colourChar = ((ColourContext) _localctx).COLOUR_CHAR.getText();
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
    public static class MonBaseContext extends ParserRuleContext {
        public String monsterBase;
        public Token STRING;

        public TerminalNode MON_BASE() {
            return getToken(PitGrammar.MON_BASE, 0);
        }

        public TerminalNode STRING() {
            return getToken(PitGrammar.STRING, 0);
        }

        public MonBaseContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_monBase;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).enterMonBase(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).exitMonBase(this);
        }
    }

    public final MonBaseContext monBase() throws RecognitionException {
        MonBaseContext _localctx = new MonBaseContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_monBase);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(56);
                match(MON_BASE);
                setState(57);
                ((MonBaseContext) _localctx).STRING = match(STRING);
                ((MonBaseContext) _localctx).monsterBase = ((MonBaseContext) _localctx).STRING.getText();
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
    public static class FlagsReqContext extends ParserRuleContext {
        public List<String> flags;
        public Token f1;
        public Token f2;

        public TerminalNode FLAGS_REQ() {
            return getToken(PitGrammar.FLAGS_REQ, 0);
        }

        public List<TerminalNode> FLAG() {
            return getTokens(PitGrammar.FLAG);
        }

        public TerminalNode FLAG(int i) {
            return getToken(PitGrammar.FLAG, i);
        }

        public List<TerminalNode> FLAG_OR() {
            return getTokens(PitGrammar.FLAG_OR);
        }

        public TerminalNode FLAG_OR(int i) {
            return getToken(PitGrammar.FLAG_OR, i);
        }

        public FlagsReqContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_flagsReq;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).enterFlagsReq(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).exitFlagsReq(this);
        }
    }

    public final FlagsReqContext flagsReq() throws RecognitionException {
        FlagsReqContext _localctx = new FlagsReqContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_flagsReq);

        ((FlagsReqContext) _localctx).flags = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(60);
                match(FLAGS_REQ);
                setState(61);
                ((FlagsReqContext) _localctx).f1 = match(FLAG);
                _localctx.flags.add(((FlagsReqContext) _localctx).f1.getText());
                setState(68);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == FLAG_OR) {
                    {
                        {
                            setState(63);
                            match(FLAG_OR);
                            setState(64);
                            ((FlagsReqContext) _localctx).f2 = match(FLAG);
                            _localctx.flags.add(((FlagsReqContext) _localctx).f2.getText());
                        }
                    }
                    setState(70);
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
    public static class FlagsBanContext extends ParserRuleContext {
        public List<String> flags;
        public Token f1;
        public Token f2;

        public TerminalNode FLAGS_BAN() {
            return getToken(PitGrammar.FLAGS_BAN, 0);
        }

        public List<TerminalNode> FLAG() {
            return getTokens(PitGrammar.FLAG);
        }

        public TerminalNode FLAG(int i) {
            return getToken(PitGrammar.FLAG, i);
        }

        public List<TerminalNode> FLAG_OR() {
            return getTokens(PitGrammar.FLAG_OR);
        }

        public TerminalNode FLAG_OR(int i) {
            return getToken(PitGrammar.FLAG_OR, i);
        }

        public FlagsBanContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_flagsBan;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).enterFlagsBan(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).exitFlagsBan(this);
        }
    }

    public final FlagsBanContext flagsBan() throws RecognitionException {
        FlagsBanContext _localctx = new FlagsBanContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_flagsBan);

        ((FlagsBanContext) _localctx).flags = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(71);
                match(FLAGS_BAN);
                setState(72);
                ((FlagsBanContext) _localctx).f1 = match(FLAG);
                _localctx.flags.add(((FlagsBanContext) _localctx).f1.getText());
                setState(79);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == FLAG_OR) {
                    {
                        {
                            setState(74);
                            match(FLAG_OR);
                            setState(75);
                            ((FlagsBanContext) _localctx).f2 = match(FLAG);
                            _localctx.flags.add(((FlagsBanContext) _localctx).f2.getText());
                        }
                    }
                    setState(81);
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
        public String innFreq;
        public Token f;

        public TerminalNode INNATE_FREQ() {
            return getToken(PitGrammar.INNATE_FREQ, 0);
        }

        public TerminalNode PIT_INTEGER() {
            return getToken(PitGrammar.PIT_INTEGER, 0);
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
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).enterInnateFreq(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).exitInnateFreq(this);
        }
    }

    public final InnateFreqContext innateFreq() throws RecognitionException {
        InnateFreqContext _localctx = new InnateFreqContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_innateFreq);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(82);
                match(INNATE_FREQ);
                setState(83);
                ((InnateFreqContext) _localctx).f = match(PIT_INTEGER);
                ((InnateFreqContext) _localctx).innFreq = ((InnateFreqContext) _localctx).f.getText();
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
    public static class SpellReqContext extends ParserRuleContext {
        public List<String> spells;
        public Token s1;
        public Token s2;

        public TerminalNode SPELL_REQ() {
            return getToken(PitGrammar.SPELL_REQ, 0);
        }

        public List<TerminalNode> FLAG() {
            return getTokens(PitGrammar.FLAG);
        }

        public TerminalNode FLAG(int i) {
            return getToken(PitGrammar.FLAG, i);
        }

        public List<TerminalNode> FLAG_OR() {
            return getTokens(PitGrammar.FLAG_OR);
        }

        public TerminalNode FLAG_OR(int i) {
            return getToken(PitGrammar.FLAG_OR, i);
        }

        public SpellReqContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_spellReq;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).enterSpellReq(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).exitSpellReq(this);
        }
    }

    public final SpellReqContext spellReq() throws RecognitionException {
        SpellReqContext _localctx = new SpellReqContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_spellReq);

        ((SpellReqContext) _localctx).spells = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(86);
                match(SPELL_REQ);
                setState(87);
                ((SpellReqContext) _localctx).s1 = match(FLAG);
                _localctx.spells.add(((SpellReqContext) _localctx).s1.getText());
                setState(94);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == FLAG_OR) {
                    {
                        {
                            setState(89);
                            match(FLAG_OR);
                            setState(90);
                            ((SpellReqContext) _localctx).s2 = match(FLAG);
                            _localctx.spells.add(((SpellReqContext) _localctx).s2.getText());
                        }
                    }
                    setState(96);
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
    public static class SpellBanContext extends ParserRuleContext {
        public List<String> spells;
        public Token s1;
        public Token s2;

        public TerminalNode SPELL_BAN() {
            return getToken(PitGrammar.SPELL_BAN, 0);
        }

        public List<TerminalNode> FLAG() {
            return getTokens(PitGrammar.FLAG);
        }

        public TerminalNode FLAG(int i) {
            return getToken(PitGrammar.FLAG, i);
        }

        public List<TerminalNode> FLAG_OR() {
            return getTokens(PitGrammar.FLAG_OR);
        }

        public TerminalNode FLAG_OR(int i) {
            return getToken(PitGrammar.FLAG_OR, i);
        }

        public SpellBanContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_spellBan;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).enterSpellBan(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).exitSpellBan(this);
        }
    }

    public final SpellBanContext spellBan() throws RecognitionException {
        SpellBanContext _localctx = new SpellBanContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_spellBan);

        ((SpellBanContext) _localctx).spells = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(97);
                match(SPELL_BAN);
                setState(98);
                ((SpellBanContext) _localctx).s1 = match(FLAG);
                _localctx.spells.add(((SpellBanContext) _localctx).s1.getText());
                setState(105);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == FLAG_OR) {
                    {
                        {
                            setState(100);
                            match(FLAG_OR);
                            setState(101);
                            ((SpellBanContext) _localctx).s2 = match(FLAG);
                            _localctx.spells.add(((SpellBanContext) _localctx).s2.getText());
                        }
                    }
                    setState(107);
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
    public static class MonBanContext extends ParserRuleContext {
        public String monName;
        public Token STRING;

        public TerminalNode MON_BAN() {
            return getToken(PitGrammar.MON_BAN, 0);
        }

        public TerminalNode STRING() {
            return getToken(PitGrammar.STRING, 0);
        }

        public MonBanContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_monBan;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).enterMonBan(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).exitMonBan(this);
        }
    }

    public final MonBanContext monBan() throws RecognitionException {
        MonBanContext _localctx = new MonBanContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_monBan);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(108);
                match(MON_BAN);
                setState(109);
                ((MonBanContext) _localctx).STRING = match(STRING);
                ((MonBanContext) _localctx).monName = ((MonBanContext) _localctx).STRING.getText();
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
    public static class PitRecordContext extends ParserRuleContext {
        public PitParseRecord pit;
        public NameContext name;
        public RoomContext room;
        public AllocContext alloc;
        public ObjRarityContext objRarity;
        public ColourContext colour;
        public MonBaseContext monBase;
        public MonBanContext monBan;
        public FlagsReqContext flagsReq;
        public FlagsBanContext flagsBan;
        public InnateFreqContext innateFreq;
        public SpellReqContext spellReq;
        public SpellBanContext spellBan;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public List<RoomContext> room() {
            return getRuleContexts(RoomContext.class);
        }

        public RoomContext room(int i) {
            return getRuleContext(RoomContext.class, i);
        }

        public List<AllocContext> alloc() {
            return getRuleContexts(AllocContext.class);
        }

        public AllocContext alloc(int i) {
            return getRuleContext(AllocContext.class, i);
        }

        public List<ObjRarityContext> objRarity() {
            return getRuleContexts(ObjRarityContext.class);
        }

        public ObjRarityContext objRarity(int i) {
            return getRuleContext(ObjRarityContext.class, i);
        }

        public List<ColourContext> colour() {
            return getRuleContexts(ColourContext.class);
        }

        public ColourContext colour(int i) {
            return getRuleContext(ColourContext.class, i);
        }

        public List<MonBaseContext> monBase() {
            return getRuleContexts(MonBaseContext.class);
        }

        public MonBaseContext monBase(int i) {
            return getRuleContext(MonBaseContext.class, i);
        }

        public List<MonBanContext> monBan() {
            return getRuleContexts(MonBanContext.class);
        }

        public MonBanContext monBan(int i) {
            return getRuleContext(MonBanContext.class, i);
        }

        public List<FlagsReqContext> flagsReq() {
            return getRuleContexts(FlagsReqContext.class);
        }

        public FlagsReqContext flagsReq(int i) {
            return getRuleContext(FlagsReqContext.class, i);
        }

        public List<FlagsBanContext> flagsBan() {
            return getRuleContexts(FlagsBanContext.class);
        }

        public FlagsBanContext flagsBan(int i) {
            return getRuleContext(FlagsBanContext.class, i);
        }

        public List<InnateFreqContext> innateFreq() {
            return getRuleContexts(InnateFreqContext.class);
        }

        public InnateFreqContext innateFreq(int i) {
            return getRuleContext(InnateFreqContext.class, i);
        }

        public List<SpellReqContext> spellReq() {
            return getRuleContexts(SpellReqContext.class);
        }

        public SpellReqContext spellReq(int i) {
            return getRuleContext(SpellReqContext.class, i);
        }

        public List<SpellBanContext> spellBan() {
            return getRuleContexts(SpellBanContext.class);
        }

        public SpellBanContext spellBan(int i) {
            return getRuleContext(SpellBanContext.class, i);
        }

        public PitRecordContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_pitRecord;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).enterPitRecord(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).exitPitRecord(this);
        }
    }

    public final PitRecordContext pitRecord() throws RecognitionException {
        PitRecordContext _localctx = new PitRecordContext(_ctx, getState());
        enterRule(_localctx, 26, RULE_pitRecord);

        String nameInit = "";
        String pitRoomTypeInit = "";
        String rarityInit = "";
        String depthInit = "";
        String objRarityInit = "";
        List<String> flags = new ArrayList<>();
        List<String> bannedFlags = new ArrayList<>();
        String innateFreqInit = "";
        List<String> spells = new ArrayList<>();
        List<String> bannedSpells = new ArrayList<>();
        List<String> colours = new ArrayList<>();
        List<String> monsterBases = new ArrayList<>();
        List<String> bannedMonsterBases = new ArrayList<>();
        int line = 0;

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(112);
                ((PitRecordContext) _localctx).name = name();
                nameInit = ((PitRecordContext) _localctx).name.nameStr;
                line = ((PitRecordContext) _localctx).name.line;
                setState(147);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        setState(147);
                        _errHandler.sync(this);
                        switch (_input.LA(1)) {
                            case ROOM: {
                                setState(114);
                                ((PitRecordContext) _localctx).room = room();
                                pitRoomTypeInit = ((PitRecordContext) _localctx).room.roomType;
                            }
                            break;
                            case ALLOC: {
                                setState(117);
                                ((PitRecordContext) _localctx).alloc = alloc();
                                rarityInit = ((PitRecordContext) _localctx).alloc.rarity;
                                depthInit = ((PitRecordContext) _localctx).alloc.averageLevel;
                            }
                            break;
                            case OBJ_RARITY: {
                                setState(120);
                                ((PitRecordContext) _localctx).objRarity = objRarity();
                                objRarityInit = ((PitRecordContext) _localctx).objRarity.objectRarity;
                            }
                            break;
                            case COLOUR: {
                                setState(123);
                                ((PitRecordContext) _localctx).colour = colour();
                                colours.add(((PitRecordContext) _localctx).colour.colourChar);
                            }
                            break;
                            case MON_BASE: {
                                setState(126);
                                ((PitRecordContext) _localctx).monBase = monBase();
                                monsterBases.add(((PitRecordContext) _localctx).monBase.monsterBase);
                            }
                            break;
                            case MON_BAN: {
                                setState(129);
                                ((PitRecordContext) _localctx).monBan = monBan();
                                bannedMonsterBases.add(((PitRecordContext) _localctx).monBan.monName);
                            }
                            break;
                            case FLAGS_REQ: {
                                setState(132);
                                ((PitRecordContext) _localctx).flagsReq = flagsReq();
                                flags.addAll(((PitRecordContext) _localctx).flagsReq.flags);
                            }
                            break;
                            case FLAGS_BAN: {
                                setState(135);
                                ((PitRecordContext) _localctx).flagsBan = flagsBan();
                                bannedFlags.addAll(((PitRecordContext) _localctx).flagsBan.flags);
                            }
                            break;
                            case INNATE_FREQ: {
                                setState(138);
                                ((PitRecordContext) _localctx).innateFreq = innateFreq();
                                innateFreqInit = ((PitRecordContext) _localctx).innateFreq.innFreq;
                            }
                            break;
                            case SPELL_REQ: {
                                setState(141);
                                ((PitRecordContext) _localctx).spellReq = spellReq();
                                spells.addAll(((PitRecordContext) _localctx).spellReq.spells);
                            }
                            break;
                            case SPELL_BAN: {
                                setState(144);
                                ((PitRecordContext) _localctx).spellBan = spellBan();
                                bannedSpells.addAll(((PitRecordContext) _localctx).spellBan.spells);
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                    }
                    setState(149);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 16376L) != 0));
            }
            _ctx.stop = _input.LT(-1);

            ((PitRecordContext) _localctx).pit = new PitParseRecord(nameInit, pitRoomTypeInit,
                    rarityInit, depthInit, objRarityInit, flags,
                    bannedFlags, innateFreqInit, spells, bannedSpells,
                    colours, monsterBases, bannedMonsterBases, line);

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
        public List<PitParseRecord> pits;
        public RecordCountContext recordCount;
        public PitRecordContext pitRecord;

        public RecordCountContext recordCount() {
            return getRuleContext(RecordCountContext.class, 0);
        }

        public TerminalNode EOF() {
            return getToken(PitGrammar.EOF, 0);
        }

        public List<PitRecordContext> pitRecord() {
            return getRuleContexts(PitRecordContext.class);
        }

        public PitRecordContext pitRecord(int i) {
            return getRuleContext(PitRecordContext.class, i);
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
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PitGrammarListener) ((PitGrammarListener) listener).exitFile(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 28, RULE_file);
        ((FileContext) _localctx).pits = new ArrayList<>();
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(151);
                ((FileContext) _localctx).recordCount = recordCount();
                ((FileContext) _localctx).declaredRecordCount = ((FileContext) _localctx).recordCount.count;
                setState(156);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(153);
                            ((FileContext) _localctx).pitRecord = pitRecord();
                            _localctx.pits.add(((FileContext) _localctx).pitRecord.pit);
                        }
                    }
                    setState(158);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(160);
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
            "\u0004\u0001\u001a\u00a3\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001" +
                    "\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004" +
                    "\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007" +
                    "\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b" +
                    "\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0005\u0007C\b" +
                    "\u0007\n\u0007\f\u0007F\t\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0005\bN\b\b\n\b\f\bQ\t\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0005\n]\b\n\n\n\f\n`\t\n\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0005" +
                    "\u000bh\b\u000b\n\u000b\f\u000bk\t\u000b\u0001\f\u0001\f\u0001\f\u0001" +
                    "\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001" +
                    "\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001" +
                    "\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001" +
                    "\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0004" +
                    "\r\u0094\b\r\u000b\r\f\r\u0095\u0001\u000e\u0001\u000e\u0001\u000e\u0001" +
                    "\u000e\u0001\u000e\u0004\u000e\u009d\b\u000e\u000b\u000e\f\u000e\u009e" +
                    "\u0001\u000e\u0001\u000e\u0001\u000e\u0000\u0000\u000f\u0000\u0002\u0004" +
                    "\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u0000\u0000" +
                    "\u00a3\u0000\u001e\u0001\u0000\u0000\u0000\u0002\"\u0001\u0000\u0000\u0000" +
                    "\u0004&\u0001\u0000\u0000\u0000\u0006*\u0001\u0000\u0000\u0000\b0\u0001" +
                    "\u0000\u0000\u0000\n4\u0001\u0000\u0000\u0000\f8\u0001\u0000\u0000\u0000" +
                    "\u000e<\u0001\u0000\u0000\u0000\u0010G\u0001\u0000\u0000\u0000\u0012R" +
                    "\u0001\u0000\u0000\u0000\u0014V\u0001\u0000\u0000\u0000\u0016a\u0001\u0000" +
                    "\u0000\u0000\u0018l\u0001\u0000\u0000\u0000\u001ap\u0001\u0000\u0000\u0000" +
                    "\u001c\u0097\u0001\u0000\u0000\u0000\u001e\u001f\u0005\u0001\u0000\u0000" +
                    "\u001f \u0005\u000e\u0000\u0000 !\u0006\u0000\uffff\uffff\u0000!\u0001" +
                    "\u0001\u0000\u0000\u0000\"#\u0005\u0002\u0000\u0000#$\u0005\u0014\u0000" +
                    "\u0000$%\u0006\u0001\uffff\uffff\u0000%\u0003\u0001\u0000\u0000\u0000" +
                    "&\'\u0005\u0003\u0000\u0000\'(\u0005\u000e\u0000\u0000()\u0006\u0002\uffff" +
                    "\uffff\u0000)\u0005\u0001\u0000\u0000\u0000*+\u0005\u0004\u0000\u0000" +
                    "+,\u0005\u0017\u0000\u0000,-\u0005\u0016\u0000\u0000-.\u0005\u0017\u0000" +
                    "\u0000./\u0006\u0003\uffff\uffff\u0000/\u0007\u0001\u0000\u0000\u0000" +
                    "01\u0005\u0005\u0000\u000012\u0005\u000e\u0000\u000023\u0006\u0004\uffff" +
                    "\uffff\u00003\t\u0001\u0000\u0000\u000045\u0005\u0006\u0000\u000056\u0005" +
                    "\u0019\u0000\u000067\u0006\u0005\uffff\uffff\u00007\u000b\u0001\u0000" +
                    "\u0000\u000089\u0005\u0007\u0000\u00009:\u0005\u0014\u0000\u0000:;\u0006" +
                    "\u0006\uffff\uffff\u0000;\r\u0001\u0000\u0000\u0000<=\u0005\b\u0000\u0000" +
                    "=>\u0005\u0011\u0000\u0000>D\u0006\u0007\uffff\uffff\u0000?@\u0005\u0012" +
                    "\u0000\u0000@A\u0005\u0011\u0000\u0000AC\u0006\u0007\uffff\uffff\u0000" +
                    "B?\u0001\u0000\u0000\u0000CF\u0001\u0000\u0000\u0000DB\u0001\u0000\u0000" +
                    "\u0000DE\u0001\u0000\u0000\u0000E\u000f\u0001\u0000\u0000\u0000FD\u0001" +
                    "\u0000\u0000\u0000GH\u0005\t\u0000\u0000HI\u0005\u0011\u0000\u0000IO\u0006" +
                    "\b\uffff\uffff\u0000JK\u0005\u0012\u0000\u0000KL\u0005\u0011\u0000\u0000" +
                    "LN\u0006\b\uffff\uffff\u0000MJ\u0001\u0000\u0000\u0000NQ\u0001\u0000\u0000" +
                    "\u0000OM\u0001\u0000\u0000\u0000OP\u0001\u0000\u0000\u0000P\u0011\u0001" +
                    "\u0000\u0000\u0000QO\u0001\u0000\u0000\u0000RS\u0005\n\u0000\u0000ST\u0005" +
                    "\u000e\u0000\u0000TU\u0006\t\uffff\uffff\u0000U\u0013\u0001\u0000\u0000" +
                    "\u0000VW\u0005\u000b\u0000\u0000WX\u0005\u0011\u0000\u0000X^\u0006\n\uffff" +
                    "\uffff\u0000YZ\u0005\u0012\u0000\u0000Z[\u0005\u0011\u0000\u0000[]\u0006" +
                    "\n\uffff\uffff\u0000\\Y\u0001\u0000\u0000\u0000]`\u0001\u0000\u0000\u0000" +
                    "^\\\u0001\u0000\u0000\u0000^_\u0001\u0000\u0000\u0000_\u0015\u0001\u0000" +
                    "\u0000\u0000`^\u0001\u0000\u0000\u0000ab\u0005\f\u0000\u0000bc\u0005\u0011" +
                    "\u0000\u0000ci\u0006\u000b\uffff\uffff\u0000de\u0005\u0012\u0000\u0000" +
                    "ef\u0005\u0011\u0000\u0000fh\u0006\u000b\uffff\uffff\u0000gd\u0001\u0000" +
                    "\u0000\u0000hk\u0001\u0000\u0000\u0000ig\u0001\u0000\u0000\u0000ij\u0001" +
                    "\u0000\u0000\u0000j\u0017\u0001\u0000\u0000\u0000ki\u0001\u0000\u0000" +
                    "\u0000lm\u0005\r\u0000\u0000mn\u0005\u0014\u0000\u0000no\u0006\f\uffff" +
                    "\uffff\u0000o\u0019\u0001\u0000\u0000\u0000pq\u0003\u0002\u0001\u0000" +
                    "q\u0093\u0006\r\uffff\uffff\u0000rs\u0003\u0004\u0002\u0000st\u0006\r" +
                    "\uffff\uffff\u0000t\u0094\u0001\u0000\u0000\u0000uv\u0003\u0006\u0003" +
                    "\u0000vw\u0006\r\uffff\uffff\u0000w\u0094\u0001\u0000\u0000\u0000xy\u0003" +
                    "\b\u0004\u0000yz\u0006\r\uffff\uffff\u0000z\u0094\u0001\u0000\u0000\u0000" +
                    "{|\u0003\n\u0005\u0000|}\u0006\r\uffff\uffff\u0000}\u0094\u0001\u0000" +
                    "\u0000\u0000~\u007f\u0003\f\u0006\u0000\u007f\u0080\u0006\r\uffff\uffff" +
                    "\u0000\u0080\u0094\u0001\u0000\u0000\u0000\u0081\u0082\u0003\u0018\f\u0000" +
                    "\u0082\u0083\u0006\r\uffff\uffff\u0000\u0083\u0094\u0001\u0000\u0000\u0000" +
                    "\u0084\u0085\u0003\u000e\u0007\u0000\u0085\u0086\u0006\r\uffff\uffff\u0000" +
                    "\u0086\u0094\u0001\u0000\u0000\u0000\u0087\u0088\u0003\u0010\b\u0000\u0088" +
                    "\u0089\u0006\r\uffff\uffff\u0000\u0089\u0094\u0001\u0000\u0000\u0000\u008a" +
                    "\u008b\u0003\u0012\t\u0000\u008b\u008c\u0006\r\uffff\uffff\u0000\u008c" +
                    "\u0094\u0001\u0000\u0000\u0000\u008d\u008e\u0003\u0014\n\u0000\u008e\u008f" +
                    "\u0006\r\uffff\uffff\u0000\u008f\u0094\u0001\u0000\u0000\u0000\u0090\u0091" +
                    "\u0003\u0016\u000b\u0000\u0091\u0092\u0006\r\uffff\uffff\u0000\u0092\u0094" +
                    "\u0001\u0000\u0000\u0000\u0093r\u0001\u0000\u0000\u0000\u0093u\u0001\u0000" +
                    "\u0000\u0000\u0093x\u0001\u0000\u0000\u0000\u0093{\u0001\u0000\u0000\u0000" +
                    "\u0093~\u0001\u0000\u0000\u0000\u0093\u0081\u0001\u0000\u0000\u0000\u0093" +
                    "\u0084\u0001\u0000\u0000\u0000\u0093\u0087\u0001\u0000\u0000\u0000\u0093" +
                    "\u008a\u0001\u0000\u0000\u0000\u0093\u008d\u0001\u0000\u0000\u0000\u0093" +
                    "\u0090\u0001\u0000\u0000\u0000\u0094\u0095\u0001\u0000\u0000\u0000\u0095" +
                    "\u0093\u0001\u0000\u0000\u0000\u0095\u0096\u0001\u0000\u0000\u0000\u0096" +
                    "\u001b\u0001\u0000\u0000\u0000\u0097\u0098\u0003\u0000\u0000\u0000\u0098" +
                    "\u009c\u0006\u000e\uffff\uffff\u0000\u0099\u009a\u0003\u001a\r\u0000\u009a" +
                    "\u009b\u0006\u000e\uffff\uffff\u0000\u009b\u009d\u0001\u0000\u0000\u0000" +
                    "\u009c\u0099\u0001\u0000\u0000\u0000\u009d\u009e\u0001\u0000\u0000\u0000" +
                    "\u009e\u009c\u0001\u0000\u0000\u0000\u009e\u009f\u0001\u0000\u0000\u0000" +
                    "\u009f\u00a0\u0001\u0000\u0000\u0000\u00a0\u00a1\u0005\u0000\u0000\u0001" +
                    "\u00a1\u001d\u0001\u0000\u0000\u0000\u0007DO^i\u0093\u0095\u009e";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}