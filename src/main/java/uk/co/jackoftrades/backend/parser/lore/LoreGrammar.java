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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/LoreGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.lore;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.combat.BlowMethod;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.*;
import uk.co.jackoftrades.middle.monsters.enums.MonsterGroupRole;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;
import uk.co.jackoftrades.middle.objects.ObjectKind;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class LoreGrammar extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            EOL = 1, NAME = 2, COUNTS = 3, BLOW = 4, FLAGS = 5, SPELLS = 6, BASE = 7, DROP = 8, DROP_BASE = 9,
            FRIENDS = 10, FRIENDS_BASE = 11, MIMIC = 12, INTEGER = 13, COLON = 14, STRING = 15,
            TVAL = 16, FRIENDS_NAME = 17, FRIENDS_BASE_NAME = 18, DICE_STRING = 19, MONSTER_NAME = 20,
            FLAG = 21, OR = 22, FLAG_EOL = 23, SPELL = 24, SPELL_EOL = 25, SPELL_OR = 26, BLOW_MODE_VALUES = 27,
            BLOW_EOL = 28;
    public static final int
            RULE_name = 0, RULE_counts = 1, RULE_blow = 2, RULE_flags = 3, RULE_spells = 4,
            RULE_base = 5, RULE_drop = 6, RULE_dropBase = 7, RULE_friends = 8, RULE_friendsBase = 9,
            RULE_mimic = 10, RULE_monsterLore = 11, RULE_file = 12;

    private static String[] makeRuleNames() {
        return new String[]{
                "name", "counts", "blow", "flags", "spells", "base", "drop", "dropBase",
                "friends", "friendsBase", "mimic", "monsterLore", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, "'name:'", "'counts:'", "'blow:'", "'flags:'", "'spells:'",
                "'base:'", "'drop:'", "'drop-base:'", "'friends:'", "'friends-base:'",
                "'mimic:'", null, "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "EOL", "NAME", "COUNTS", "BLOW", "FLAGS", "SPELLS", "BASE", "DROP",
                "DROP_BASE", "FRIENDS", "FRIENDS_BASE", "MIMIC", "INTEGER", "COLON",
                "STRING", "TVAL", "FRIENDS_NAME", "FRIENDS_BASE_NAME", "DICE_STRING",
                "MONSTER_NAME", "FLAG", "OR", "FLAG_EOL", "SPELL", "SPELL_EOL", "SPELL_OR",
                "BLOW_MODE_VALUES", "BLOW_EOL"
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
        return "LoreGrammar.g4";
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

    public LoreGrammar(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class NameContext extends ParserRuleContext {
        public String nameString;
        public Token MONSTER_NAME;

        public TerminalNode NAME() {
            return getToken(LoreGrammar.NAME, 0);
        }

        public TerminalNode MONSTER_NAME() {
            return getToken(LoreGrammar.MONSTER_NAME, 0);
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
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof LoreGrammarVisitor)
                return ((LoreGrammarVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(26);
                match(NAME);
                setState(27);
                ((NameContext) _localctx).MONSTER_NAME = match(MONSTER_NAME);
                ((NameContext) _localctx).nameString = ((NameContext) _localctx).MONSTER_NAME.getText();
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
    public static class CountsContext extends ParserRuleContext {
        public int sightings;
        public int deaths;
        public int kills;
        public int wake;
        public int ignore;
        public int innate;
        public int spell;
        public Token sig;
        public Token dea;
        public Token kil;
        public Token wak;
        public Token ign;
        public Token inn;
        public Token spe;

        public TerminalNode COUNTS() {
            return getToken(LoreGrammar.COUNTS, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(LoreGrammar.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(LoreGrammar.COLON, i);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(LoreGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(LoreGrammar.INTEGER, i);
        }

        public CountsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_counts;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).enterCounts(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).exitCounts(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof LoreGrammarVisitor)
                return ((LoreGrammarVisitor<? extends T>) visitor).visitCounts(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CountsContext counts() throws RecognitionException {
        CountsContext _localctx = new CountsContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_counts);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(30);
                match(COUNTS);
                setState(31);
                ((CountsContext) _localctx).sig = match(INTEGER);
                setState(32);
                match(COLON);
                setState(33);
                ((CountsContext) _localctx).dea = match(INTEGER);
                setState(34);
                match(COLON);
                setState(35);
                ((CountsContext) _localctx).kil = match(INTEGER);
                setState(36);
                match(COLON);
                setState(37);
                ((CountsContext) _localctx).wak = match(INTEGER);
                setState(38);
                match(COLON);
                setState(39);
                ((CountsContext) _localctx).ign = match(INTEGER);
                setState(40);
                match(COLON);
                setState(41);
                ((CountsContext) _localctx).inn = match(INTEGER);
                setState(42);
                match(COLON);
                setState(43);
                ((CountsContext) _localctx).spe = match(INTEGER);

                ((CountsContext) _localctx).sightings = Integer.parseInt(((CountsContext) _localctx).sig.getText());
                ((CountsContext) _localctx).deaths = Integer.parseInt(((CountsContext) _localctx).dea.getText());
                ((CountsContext) _localctx).kills = Integer.parseInt(((CountsContext) _localctx).kil.getText());
                ((CountsContext) _localctx).wake = Integer.parseInt(((CountsContext) _localctx).wak.getText());
                ((CountsContext) _localctx).ignore = Integer.parseInt(((CountsContext) _localctx).ign.getText());
                ((CountsContext) _localctx).innate = Integer.parseInt(((CountsContext) _localctx).inn.getText());
                ((CountsContext) _localctx).spell = Integer.parseInt(((CountsContext) _localctx).spe.getText());

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
        public MonsterBlow monBlow;
        public Integer timesSeen;
        public Token BLOW_MODE_VALUES;

        public TerminalNode BLOW() {
            return getToken(LoreGrammar.BLOW, 0);
        }

        public TerminalNode BLOW_MODE_VALUES() {
            return getToken(LoreGrammar.BLOW_MODE_VALUES, 0);
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
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).enterBlow(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).exitBlow(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof LoreGrammarVisitor)
                return ((LoreGrammarVisitor<? extends T>) visitor).visitBlow(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BlowContext blow() throws RecognitionException {
        BlowContext _localctx = new BlowContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_blow);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(46);
                match(BLOW);
                setState(47);
                ((BlowContext) _localctx).BLOW_MODE_VALUES = match(BLOW_MODE_VALUES);

                String raw = ((BlowContext) _localctx).BLOW_MODE_VALUES.getText();
                String[] parts = raw.split(":");
                BlowMethod blowMethod = GameConstants.lookupBlowMethod(parts[0]);
                BlowEffect blowEffect = GameConstants.lookupBlowEffect(parts[1]);
                Random dice = Random.parseStr(parts[2]);
                ((BlowContext) _localctx).timesSeen = Integer.parseInt(parts[3]);

                ((BlowContext) _localctx).monBlow = new MonsterBlow(blowMethod, blowEffect, dice);

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
        public Flag<MonsterRaceFlag> loreFlags;
        public Token f1;
        public Token f2;

        public TerminalNode FLAGS() {
            return getToken(LoreGrammar.FLAGS, 0);
        }

        public List<TerminalNode> FLAG() {
            return getTokens(LoreGrammar.FLAG);
        }

        public TerminalNode FLAG(int i) {
            return getToken(LoreGrammar.FLAG, i);
        }

        public List<TerminalNode> OR() {
            return getTokens(LoreGrammar.OR);
        }

        public TerminalNode OR(int i) {
            return getToken(LoreGrammar.OR, i);
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
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).enterFlags(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).exitFlags(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof LoreGrammarVisitor)
                return ((LoreGrammarVisitor<? extends T>) visitor).visitFlags(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FlagsContext flags() throws RecognitionException {
        FlagsContext _localctx = new FlagsContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_flags);

        ((FlagsContext) _localctx).loreFlags = new Flag<>(MonsterRaceFlag.class);

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(50);
                match(FLAGS);
                setState(51);
                ((FlagsContext) _localctx).f1 = match(FLAG);

                String raw1 = "RF_" + ((FlagsContext) _localctx).f1.getText().trim();
                _localctx.loreFlags.on(MonsterRaceFlag.valueOf(raw1));

                setState(58);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == OR) {
                    {
                        {
                            setState(53);
                            match(OR);
                            setState(54);
                            ((FlagsContext) _localctx).f2 = match(FLAG);

                            String raw2 = "RF_" + ((FlagsContext) _localctx).f2.getText().trim();
                            _localctx.loreFlags.on(MonsterRaceFlag.valueOf(raw2));

                        }
                    }
                    setState(60);
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
    public static class SpellsContext extends ParserRuleContext {
        public Flag<MonsterSpell> spellFlags;
        public Token s1;
        public Token s2;

        public TerminalNode SPELLS() {
            return getToken(LoreGrammar.SPELLS, 0);
        }

        public List<TerminalNode> FLAG() {
            return getTokens(LoreGrammar.FLAG);
        }

        public TerminalNode FLAG(int i) {
            return getToken(LoreGrammar.FLAG, i);
        }

        public List<TerminalNode> SPELL_OR() {
            return getTokens(LoreGrammar.SPELL_OR);
        }

        public TerminalNode SPELL_OR(int i) {
            return getToken(LoreGrammar.SPELL_OR, i);
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
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).enterSpells(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).exitSpells(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof LoreGrammarVisitor)
                return ((LoreGrammarVisitor<? extends T>) visitor).visitSpells(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SpellsContext spells() throws RecognitionException {
        SpellsContext _localctx = new SpellsContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_spells);

        ((SpellsContext) _localctx).spellFlags = new Flag<>(MonsterSpell.class);

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(61);
                match(SPELLS);
                setState(62);
                ((SpellsContext) _localctx).s1 = match(FLAG);

                String raw1 = "RSF_" + ((SpellsContext) _localctx).s1.getText().trim();
                _localctx.spellFlags.on(MonsterSpell.valueOf(raw1));

                setState(69);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == SPELL_OR) {
                    {
                        {
                            setState(64);
                            match(SPELL_OR);
                            setState(65);
                            ((SpellsContext) _localctx).s2 = match(FLAG);

                            String raw2 = "RSF_" + ((SpellsContext) _localctx).s2.getText().trim();
                            _localctx.spellFlags.on(MonsterSpell.valueOf(raw2));

                        }
                    }
                    setState(71);
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
    public static class BaseContext extends ParserRuleContext {
        public MonsterBase baseObj;
        public Token MONSTER_NAME;

        public TerminalNode BASE() {
            return getToken(LoreGrammar.BASE, 0);
        }

        public TerminalNode MONSTER_NAME() {
            return getToken(LoreGrammar.MONSTER_NAME, 0);
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
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).enterBase(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).exitBase(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof LoreGrammarVisitor)
                return ((LoreGrammarVisitor<? extends T>) visitor).visitBase(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BaseContext base() throws RecognitionException {
        BaseContext _localctx = new BaseContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_base);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(72);
                match(BASE);
                setState(73);
                ((BaseContext) _localctx).MONSTER_NAME = match(MONSTER_NAME);

                String raw = ((BaseContext) _localctx).MONSTER_NAME.getText();
                ((BaseContext) _localctx).baseObj = GameConstants.getBaseFromName(raw);

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
        public MonsterDrop monsterDrop;
        public Token TVAL;
        public Token STRING;
        public Token ch;
        public Token mn;
        public Token mx;

        public TerminalNode DROP() {
            return getToken(LoreGrammar.DROP, 0);
        }

        public TerminalNode TVAL() {
            return getToken(LoreGrammar.TVAL, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(LoreGrammar.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(LoreGrammar.COLON, i);
        }

        public TerminalNode STRING() {
            return getToken(LoreGrammar.STRING, 0);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(LoreGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(LoreGrammar.INTEGER, i);
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
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).enterDrop(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).exitDrop(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof LoreGrammarVisitor)
                return ((LoreGrammarVisitor<? extends T>) visitor).visitDrop(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DropContext drop() throws RecognitionException {
        DropContext _localctx = new DropContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_drop);

        TValue tval = TValue.TV_NONE;
        ObjectKind sval = null;
        int chance = 0;
        int min = 0;
        int max = 0;

        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(76);
                match(DROP);
                setState(77);
                ((DropContext) _localctx).TVAL = match(TVAL);
                setState(78);
                match(COLON);
                setState(79);
                ((DropContext) _localctx).STRING = match(STRING);
                setState(80);
                match(COLON);
                setState(81);
                ((DropContext) _localctx).ch = match(INTEGER);
                setState(82);
                match(COLON);
                setState(83);
                ((DropContext) _localctx).mn = match(INTEGER);
                setState(84);
                match(COLON);
                setState(85);
                ((DropContext) _localctx).mx = match(INTEGER);

                tval = TValue.valueOf("TV_" + ((DropContext) _localctx).TVAL.getText().toUpperCase().replace(" ", "_").replace("ARMOUR", "ARMOR"));
                sval = GameConstants.lookupObjectKind(tval, ((DropContext) _localctx).STRING.getText());
                chance = Integer.parseInt(((DropContext) _localctx).ch.getText());
                min = Integer.parseInt(((DropContext) _localctx).mn.getText());
                max = Integer.parseInt(((DropContext) _localctx).mx.getText());

            }
            _ctx.stop = _input.LT(-1);

            ((DropContext) _localctx).monsterDrop = new MonsterDrop(tval, sval, chance, min, max);

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
        public MonsterDrop monsterDropBase;
        public Token TVAL;
        public Token ch;
        public Token mn;
        public Token mx;

        public TerminalNode DROP_BASE() {
            return getToken(LoreGrammar.DROP_BASE, 0);
        }

        public TerminalNode TVAL() {
            return getToken(LoreGrammar.TVAL, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(LoreGrammar.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(LoreGrammar.COLON, i);
        }

        public List<TerminalNode> INTEGER() {
            return getTokens(LoreGrammar.INTEGER);
        }

        public TerminalNode INTEGER(int i) {
            return getToken(LoreGrammar.INTEGER, i);
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
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).enterDropBase(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).exitDropBase(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof LoreGrammarVisitor)
                return ((LoreGrammarVisitor<? extends T>) visitor).visitDropBase(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DropBaseContext dropBase() throws RecognitionException {
        DropBaseContext _localctx = new DropBaseContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_dropBase);

        TValue tval = TValue.TV_NONE;
        String sval = "";
        int chance = 0;
        int min = 0;
        int max = 0;

        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(88);
                match(DROP_BASE);
                setState(89);
                ((DropBaseContext) _localctx).TVAL = match(TVAL);
                setState(90);
                match(COLON);
                setState(91);
                ((DropBaseContext) _localctx).ch = match(INTEGER);
                setState(92);
                match(COLON);
                setState(93);
                ((DropBaseContext) _localctx).mn = match(INTEGER);
                setState(94);
                match(COLON);
                setState(95);
                ((DropBaseContext) _localctx).mx = match(INTEGER);

                tval = TValue.valueOf("TV_" + ((DropBaseContext) _localctx).TVAL.getText().toUpperCase().replace(" ", "_").replace("ARMOUR", "ARMOR"));
                chance = Integer.parseInt(((DropBaseContext) _localctx).ch.getText());
                min = Integer.parseInt(((DropBaseContext) _localctx).mn.getText());
                max = Integer.parseInt(((DropBaseContext) _localctx).mx.getText());

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
        public MonsterFriends monsterFriends;
        public Token chance;
        public Token number;
        public Token fName;
        public Token role;

        public TerminalNode FRIENDS() {
            return getToken(LoreGrammar.FRIENDS, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(LoreGrammar.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(LoreGrammar.COLON, i);
        }

        public TerminalNode INTEGER() {
            return getToken(LoreGrammar.INTEGER, 0);
        }

        public TerminalNode DICE_STRING() {
            return getToken(LoreGrammar.DICE_STRING, 0);
        }

        public List<TerminalNode> FRIENDS_NAME() {
            return getTokens(LoreGrammar.FRIENDS_NAME);
        }

        public TerminalNode FRIENDS_NAME(int i) {
            return getToken(LoreGrammar.FRIENDS_NAME, i);
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
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).enterFriends(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).exitFriends(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof LoreGrammarVisitor)
                return ((LoreGrammarVisitor<? extends T>) visitor).visitFriends(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FriendsContext friends() throws RecognitionException {
        FriendsContext _localctx = new FriendsContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_friends);

        String friendsName = "";
        MonsterGroupRole friendsRole = MonsterGroupRole.MON_GROUP_NONE;
        int percentChance = 0;
        String diceString = "";
        int numberOfDice = 0;
        int sidesOfDice = 0;

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(98);
                match(FRIENDS);
                setState(99);
                ((FriendsContext) _localctx).chance = match(INTEGER);
                setState(100);
                match(COLON);
                setState(101);
                ((FriendsContext) _localctx).number = match(DICE_STRING);
                setState(102);
                match(COLON);
                setState(103);
                ((FriendsContext) _localctx).fName = match(FRIENDS_NAME);

                percentChance = Integer.parseInt(((FriendsContext) _localctx).chance.getText());
                diceString = ((FriendsContext) _localctx).number.getText();
                Random temp = Random.parseStr(diceString);
                numberOfDice = temp.getDice();
                sidesOfDice = temp.getSides();
                friendsName = ((FriendsContext) _localctx).fName.getText();

                setState(108);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COLON) {
                    {
                        setState(105);
                        match(COLON);
                        setState(106);
                        ((FriendsContext) _localctx).role = match(FRIENDS_NAME);

                        String raw = ((FriendsContext) _localctx).role.getText();
                        friendsRole = MonsterGroupRole.valueOf("MON_GROUP_" + raw);

                    }
                }

            }
            _ctx.stop = _input.LT(-1);

            ((FriendsContext) _localctx).monsterFriends = new MonsterFriends(friendsName, friendsRole, percentChance,
                    numberOfDice, sidesOfDice);

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
        public MonsterFriendsBase baseFriend;
        public Token chance;
        public Token dice;
        public Token fName;
        public Token fRole;

        public TerminalNode FRIENDS_BASE() {
            return getToken(LoreGrammar.FRIENDS_BASE, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(LoreGrammar.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(LoreGrammar.COLON, i);
        }

        public TerminalNode INTEGER() {
            return getToken(LoreGrammar.INTEGER, 0);
        }

        public TerminalNode DICE_STRING() {
            return getToken(LoreGrammar.DICE_STRING, 0);
        }

        public TerminalNode FRIENDS_BASE_NAME() {
            return getToken(LoreGrammar.FRIENDS_BASE_NAME, 0);
        }

        public TerminalNode FRIENDS_NAME() {
            return getToken(LoreGrammar.FRIENDS_NAME, 0);
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
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).enterFriendsBase(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).exitFriendsBase(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof LoreGrammarVisitor)
                return ((LoreGrammarVisitor<? extends T>) visitor).visitFriendsBase(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FriendsBaseContext friendsBase() throws RecognitionException {
        FriendsBaseContext _localctx = new FriendsBaseContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_friendsBase);

        MonsterBase base = null;
        MonsterGroupRole role = MonsterGroupRole.MON_GROUP_NONE;
        int percentageChance = 0;
        int numberOfDice = 0;
        int numberOfSides = 0;

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(110);
                match(FRIENDS_BASE);
                setState(111);
                ((FriendsBaseContext) _localctx).chance = match(INTEGER);
                setState(112);
                match(COLON);
                setState(113);
                ((FriendsBaseContext) _localctx).dice = match(DICE_STRING);
                setState(114);
                match(COLON);
                setState(115);
                ((FriendsBaseContext) _localctx).fName = match(FRIENDS_BASE_NAME);

                percentageChance = Integer.parseInt(((FriendsBaseContext) _localctx).chance.getText());
                Random temp = Random.parseStr(((FriendsBaseContext) _localctx).dice.getText());
                numberOfDice = temp.getDice();
                numberOfSides = temp.getSides();
                base = GameConstants.getMonsterBase(((FriendsBaseContext) _localctx).fName.getText());

                setState(120);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COLON) {
                    {
                        setState(117);
                        match(COLON);
                        setState(118);
                        ((FriendsBaseContext) _localctx).fRole = match(FRIENDS_NAME);

                        role = MonsterGroupRole.valueOf("MON_GROUP_" + ((FriendsBaseContext) _localctx).fRole.getText().toUpperCase());

                    }
                }

            }
            _ctx.stop = _input.LT(-1);

            ((FriendsBaseContext) _localctx).baseFriend = new MonsterFriendsBase(base, role, percentageChance, numberOfDice,
                    numberOfSides);

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
        public ObjectKind kind;
        public Token TVAL;
        public Token STRING;

        public TerminalNode MIMIC() {
            return getToken(LoreGrammar.MIMIC, 0);
        }

        public TerminalNode TVAL() {
            return getToken(LoreGrammar.TVAL, 0);
        }

        public TerminalNode COLON() {
            return getToken(LoreGrammar.COLON, 0);
        }

        public TerminalNode STRING() {
            return getToken(LoreGrammar.STRING, 0);
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
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).enterMimic(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).exitMimic(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof LoreGrammarVisitor)
                return ((LoreGrammarVisitor<? extends T>) visitor).visitMimic(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MimicContext mimic() throws RecognitionException {
        MimicContext _localctx = new MimicContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_mimic);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(122);
                match(MIMIC);
                setState(123);
                ((MimicContext) _localctx).TVAL = match(TVAL);
                setState(124);
                match(COLON);
                setState(125);
                ((MimicContext) _localctx).STRING = match(STRING);

                TValue tval = TValue.valueOf("TV_" + ((MimicContext) _localctx).TVAL.getText().toUpperCase().replace(" ", "_")
                        .replace("ARMOUR", "ARMOR"));
                String sval = ((MimicContext) _localctx).STRING.getText();
                ((MimicContext) _localctx).kind = GameConstants.lookupObjectKind(tval, sval);

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
    public static class MonsterLoreContext extends ParserRuleContext {
        public MonsterRace race;
        public MonsterLore lore;
        public NameContext name;
        public CountsContext counts;
        public BaseContext base;
        public BlowContext blow;
        public FlagsContext flags;
        public SpellsContext spells;
        public DropContext drop;
        public FriendsContext friends;
        public FriendsBaseContext friendsBase;
        public MimicContext mimic;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public List<CountsContext> counts() {
            return getRuleContexts(CountsContext.class);
        }

        public CountsContext counts(int i) {
            return getRuleContext(CountsContext.class, i);
        }

        public List<BaseContext> base() {
            return getRuleContexts(BaseContext.class);
        }

        public BaseContext base(int i) {
            return getRuleContext(BaseContext.class, i);
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

        public List<SpellsContext> spells() {
            return getRuleContexts(SpellsContext.class);
        }

        public SpellsContext spells(int i) {
            return getRuleContext(SpellsContext.class, i);
        }

        public List<DropContext> drop() {
            return getRuleContexts(DropContext.class);
        }

        public DropContext drop(int i) {
            return getRuleContext(DropContext.class, i);
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

        public List<MimicContext> mimic() {
            return getRuleContexts(MimicContext.class);
        }

        public MimicContext mimic(int i) {
            return getRuleContext(MimicContext.class, i);
        }

        public MonsterLoreContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_monsterLore;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).enterMonsterLore(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).exitMonsterLore(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof LoreGrammarVisitor)
                return ((LoreGrammarVisitor<? extends T>) visitor).visitMonsterLore(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MonsterLoreContext monsterLore() throws RecognitionException {
        MonsterLoreContext _localctx = new MonsterLoreContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_monsterLore);

        int sights = 0;
        int deaths = 0;
        int pkills = 0;
        int thefts = 0;
        int tkills = 0;
        int wake = 0;
        int ignore = 0;
        int innate = 0;
        int spell = 0;
        int maxGold = 0;
        int maxItem = 0;
        int castInnate = 0;
        int castSpell = 0;
        Map<MonsterBlow, Integer> blows = new HashMap<>();
        Flag<MonsterRaceFlag> flags = new Flag<>(MonsterRaceFlag.class);
        Flag<MonsterSpell> spells = new Flag<>(MonsterSpell.class);
        List<MonsterDrop> drops = new ArrayList<>();
        List<MonsterFriends> friends = new ArrayList<>();
        List<MonsterFriendsBase> baseFriends = new ArrayList<>();
        List<ObjectKind> mimics = new ArrayList<>();
        MonsterBase monsterBase = null;

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(128);
                ((MonsterLoreContext) _localctx).name = name();
                ((MonsterLoreContext) _localctx).race = GameConstants.lookupMonsterRace(((MonsterLoreContext) _localctx).name.nameString);
                setState(157);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        setState(157);
                        _errHandler.sync(this);
                        switch (_input.LA(1)) {
                            case COUNTS: {
                                setState(130);
                                ((MonsterLoreContext) _localctx).counts = counts();

                                sights = ((MonsterLoreContext) _localctx).counts.sightings;
                                deaths = ((MonsterLoreContext) _localctx).counts.deaths;
                                tkills = ((MonsterLoreContext) _localctx).counts.kills;
                                wake = ((MonsterLoreContext) _localctx).counts.wake;
                                ignore = ((MonsterLoreContext) _localctx).counts.ignore;
                                innate = ((MonsterLoreContext) _localctx).counts.innate;
                                spell = ((MonsterLoreContext) _localctx).counts.spell;

                            }
                            break;
                            case BASE: {
                                setState(133);
                                ((MonsterLoreContext) _localctx).base = base();

                                monsterBase = ((MonsterLoreContext) _localctx).base.baseObj;

                            }
                            break;
                            case BLOW: {
                                setState(136);
                                ((MonsterLoreContext) _localctx).blow = blow();

                                blows.put(((MonsterLoreContext) _localctx).blow.monBlow, ((MonsterLoreContext) _localctx).blow.timesSeen);

                            }
                            break;
                            case FLAGS: {
                                setState(139);
                                ((MonsterLoreContext) _localctx).flags = flags();

                                flags.union(((MonsterLoreContext) _localctx).flags.loreFlags);

                            }
                            break;
                            case SPELLS: {
                                setState(142);
                                ((MonsterLoreContext) _localctx).spells = spells();

                                spells.union(((MonsterLoreContext) _localctx).spells.spellFlags);

                            }
                            break;
                            case DROP: {
                                setState(145);
                                ((MonsterLoreContext) _localctx).drop = drop();

                                drops.add(((MonsterLoreContext) _localctx).drop.monsterDrop);

                            }
                            break;
                            case FRIENDS: {
                                setState(148);
                                ((MonsterLoreContext) _localctx).friends = friends();

                                friends.add(((MonsterLoreContext) _localctx).friends.monsterFriends);

                            }
                            break;
                            case FRIENDS_BASE: {
                                setState(151);
                                ((MonsterLoreContext) _localctx).friendsBase = friendsBase();

                                baseFriends.add(((MonsterLoreContext) _localctx).friendsBase.baseFriend);

                            }
                            break;
                            case MIMIC: {
                                setState(154);
                                ((MonsterLoreContext) _localctx).mimic = mimic();

                                mimics.add(((MonsterLoreContext) _localctx).mimic.kind);

                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                    }
                    setState(159);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 7672L) != 0));
            }
            _ctx.stop = _input.LT(-1);

            MonsterMimic mimicInit = new MonsterMimic(mimics);
            List<MonsterMimic> mm = new ArrayList<>();
            mm.add(mimicInit);
            ((MonsterLoreContext) _localctx).lore = new MonsterLore(sights, deaths, pkills, thefts,
                    tkills, wake, ignore, maxGold,
                    maxItem, castInnate, castSpell,
                    flags, spells, drops,
                    friends, baseFriends, mm,
                    blows, monsterBase);

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
        public List<MonsterLore> loreEntries;

        public TerminalNode EOF() {
            return getToken(LoreGrammar.EOF, 0);
        }

        public List<MonsterLoreContext> monsterLore() {
            return getRuleContexts(MonsterLoreContext.class);
        }

        public MonsterLoreContext monsterLore(int i) {
            return getRuleContext(MonsterLoreContext.class, i);
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
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof LoreGrammarListener) ((LoreGrammarListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof LoreGrammarVisitor)
                return ((LoreGrammarVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_file);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(162);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(161);
                            monsterLore();
                        }
                    }
                    setState(164);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(166);
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
            "\u0004\u0001\u001c\u00a9\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001" +
                    "\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004" +
                    "\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007" +
                    "\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b" +
                    "\u0002\f\u0007\f\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0005\u00039\b\u0003\n\u0003\f\u0003<\t\u0003\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0005" +
                    "\u0004D\b\u0004\n\u0004\f\u0004G\t\u0004\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0003\bm\b\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0003\ty\b\t\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0004\u000b\u009e\b\u000b\u000b\u000b\f\u000b\u009f\u0001\f\u0004" +
                    "\f\u00a3\b\f\u000b\f\f\f\u00a4\u0001\f\u0001\f\u0001\f\u0000\u0000\r\u0000" +
                    "\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u0000\u0000" +
                    "\u00a9\u0000\u001a\u0001\u0000\u0000\u0000\u0002\u001e\u0001\u0000\u0000" +
                    "\u0000\u0004.\u0001\u0000\u0000\u0000\u00062\u0001\u0000\u0000\u0000\b" +
                    "=\u0001\u0000\u0000\u0000\nH\u0001\u0000\u0000\u0000\fL\u0001\u0000\u0000" +
                    "\u0000\u000eX\u0001\u0000\u0000\u0000\u0010b\u0001\u0000\u0000\u0000\u0012" +
                    "n\u0001\u0000\u0000\u0000\u0014z\u0001\u0000\u0000\u0000\u0016\u0080\u0001" +
                    "\u0000\u0000\u0000\u0018\u00a2\u0001\u0000\u0000\u0000\u001a\u001b\u0005" +
                    "\u0002\u0000\u0000\u001b\u001c\u0005\u0014\u0000\u0000\u001c\u001d\u0006" +
                    "\u0000\uffff\uffff\u0000\u001d\u0001\u0001\u0000\u0000\u0000\u001e\u001f" +
                    "\u0005\u0003\u0000\u0000\u001f \u0005\r\u0000\u0000 !\u0005\u000e\u0000" +
                    "\u0000!\"\u0005\r\u0000\u0000\"#\u0005\u000e\u0000\u0000#$\u0005\r\u0000" +
                    "\u0000$%\u0005\u000e\u0000\u0000%&\u0005\r\u0000\u0000&\'\u0005\u000e" +
                    "\u0000\u0000\'(\u0005\r\u0000\u0000()\u0005\u000e\u0000\u0000)*\u0005" +
                    "\r\u0000\u0000*+\u0005\u000e\u0000\u0000+,\u0005\r\u0000\u0000,-\u0006" +
                    "\u0001\uffff\uffff\u0000-\u0003\u0001\u0000\u0000\u0000./\u0005\u0004" +
                    "\u0000\u0000/0\u0005\u001b\u0000\u000001\u0006\u0002\uffff\uffff\u0000" +
                    "1\u0005\u0001\u0000\u0000\u000023\u0005\u0005\u0000\u000034\u0005\u0015" +
                    "\u0000\u00004:\u0006\u0003\uffff\uffff\u000056\u0005\u0016\u0000\u0000" +
                    "67\u0005\u0015\u0000\u000079\u0006\u0003\uffff\uffff\u000085\u0001\u0000" +
                    "\u0000\u00009<\u0001\u0000\u0000\u0000:8\u0001\u0000\u0000\u0000:;\u0001" +
                    "\u0000\u0000\u0000;\u0007\u0001\u0000\u0000\u0000<:\u0001\u0000\u0000" +
                    "\u0000=>\u0005\u0006\u0000\u0000>?\u0005\u0015\u0000\u0000?E\u0006\u0004" +
                    "\uffff\uffff\u0000@A\u0005\u001a\u0000\u0000AB\u0005\u0015\u0000\u0000" +
                    "BD\u0006\u0004\uffff\uffff\u0000C@\u0001\u0000\u0000\u0000DG\u0001\u0000" +
                    "\u0000\u0000EC\u0001\u0000\u0000\u0000EF\u0001\u0000\u0000\u0000F\t\u0001" +
                    "\u0000\u0000\u0000GE\u0001\u0000\u0000\u0000HI\u0005\u0007\u0000\u0000" +
                    "IJ\u0005\u0014\u0000\u0000JK\u0006\u0005\uffff\uffff\u0000K\u000b\u0001" +
                    "\u0000\u0000\u0000LM\u0005\b\u0000\u0000MN\u0005\u0010\u0000\u0000NO\u0005" +
                    "\u000e\u0000\u0000OP\u0005\u000f\u0000\u0000PQ\u0005\u000e\u0000\u0000" +
                    "QR\u0005\r\u0000\u0000RS\u0005\u000e\u0000\u0000ST\u0005\r\u0000\u0000" +
                    "TU\u0005\u000e\u0000\u0000UV\u0005\r\u0000\u0000VW\u0006\u0006\uffff\uffff" +
                    "\u0000W\r\u0001\u0000\u0000\u0000XY\u0005\t\u0000\u0000YZ\u0005\u0010" +
                    "\u0000\u0000Z[\u0005\u000e\u0000\u0000[\\\u0005\r\u0000\u0000\\]\u0005" +
                    "\u000e\u0000\u0000]^\u0005\r\u0000\u0000^_\u0005\u000e\u0000\u0000_`\u0005" +
                    "\r\u0000\u0000`a\u0006\u0007\uffff\uffff\u0000a\u000f\u0001\u0000\u0000" +
                    "\u0000bc\u0005\n\u0000\u0000cd\u0005\r\u0000\u0000de\u0005\u000e\u0000" +
                    "\u0000ef\u0005\u0013\u0000\u0000fg\u0005\u000e\u0000\u0000gh\u0005\u0011" +
                    "\u0000\u0000hl\u0006\b\uffff\uffff\u0000ij\u0005\u000e\u0000\u0000jk\u0005" +
                    "\u0011\u0000\u0000km\u0006\b\uffff\uffff\u0000li\u0001\u0000\u0000\u0000" +
                    "lm\u0001\u0000\u0000\u0000m\u0011\u0001\u0000\u0000\u0000no\u0005\u000b" +
                    "\u0000\u0000op\u0005\r\u0000\u0000pq\u0005\u000e\u0000\u0000qr\u0005\u0013" +
                    "\u0000\u0000rs\u0005\u000e\u0000\u0000st\u0005\u0012\u0000\u0000tx\u0006" +
                    "\t\uffff\uffff\u0000uv\u0005\u000e\u0000\u0000vw\u0005\u0011\u0000\u0000" +
                    "wy\u0006\t\uffff\uffff\u0000xu\u0001\u0000\u0000\u0000xy\u0001\u0000\u0000" +
                    "\u0000y\u0013\u0001\u0000\u0000\u0000z{\u0005\f\u0000\u0000{|\u0005\u0010" +
                    "\u0000\u0000|}\u0005\u000e\u0000\u0000}~\u0005\u000f\u0000\u0000~\u007f" +
                    "\u0006\n\uffff\uffff\u0000\u007f\u0015\u0001\u0000\u0000\u0000\u0080\u0081" +
                    "\u0003\u0000\u0000\u0000\u0081\u009d\u0006\u000b\uffff\uffff\u0000\u0082" +
                    "\u0083\u0003\u0002\u0001\u0000\u0083\u0084\u0006\u000b\uffff\uffff\u0000" +
                    "\u0084\u009e\u0001\u0000\u0000\u0000\u0085\u0086\u0003\n\u0005\u0000\u0086" +
                    "\u0087\u0006\u000b\uffff\uffff\u0000\u0087\u009e\u0001\u0000\u0000\u0000" +
                    "\u0088\u0089\u0003\u0004\u0002\u0000\u0089\u008a\u0006\u000b\uffff\uffff" +
                    "\u0000\u008a\u009e\u0001\u0000\u0000\u0000\u008b\u008c\u0003\u0006\u0003" +
                    "\u0000\u008c\u008d\u0006\u000b\uffff\uffff\u0000\u008d\u009e\u0001\u0000" +
                    "\u0000\u0000\u008e\u008f\u0003\b\u0004\u0000\u008f\u0090\u0006\u000b\uffff" +
                    "\uffff\u0000\u0090\u009e\u0001\u0000\u0000\u0000\u0091\u0092\u0003\f\u0006" +
                    "\u0000\u0092\u0093\u0006\u000b\uffff\uffff\u0000\u0093\u009e\u0001\u0000" +
                    "\u0000\u0000\u0094\u0095\u0003\u0010\b\u0000\u0095\u0096\u0006\u000b\uffff" +
                    "\uffff\u0000\u0096\u009e\u0001\u0000\u0000\u0000\u0097\u0098\u0003\u0012" +
                    "\t\u0000\u0098\u0099\u0006\u000b\uffff\uffff\u0000\u0099\u009e\u0001\u0000" +
                    "\u0000\u0000\u009a\u009b\u0003\u0014\n\u0000\u009b\u009c\u0006\u000b\uffff" +
                    "\uffff\u0000\u009c\u009e\u0001\u0000\u0000\u0000\u009d\u0082\u0001\u0000" +
                    "\u0000\u0000\u009d\u0085\u0001\u0000\u0000\u0000\u009d\u0088\u0001\u0000" +
                    "\u0000\u0000\u009d\u008b\u0001\u0000\u0000\u0000\u009d\u008e\u0001\u0000" +
                    "\u0000\u0000\u009d\u0091\u0001\u0000\u0000\u0000\u009d\u0094\u0001\u0000" +
                    "\u0000\u0000\u009d\u0097\u0001\u0000\u0000\u0000\u009d\u009a\u0001\u0000" +
                    "\u0000\u0000\u009e\u009f\u0001\u0000\u0000\u0000\u009f\u009d\u0001\u0000" +
                    "\u0000\u0000\u009f\u00a0\u0001\u0000\u0000\u0000\u00a0\u0017\u0001\u0000" +
                    "\u0000\u0000\u00a1\u00a3\u0003\u0016\u000b\u0000\u00a2\u00a1\u0001\u0000" +
                    "\u0000\u0000\u00a3\u00a4\u0001\u0000\u0000\u0000\u00a4\u00a2\u0001\u0000" +
                    "\u0000\u0000\u00a4\u00a5\u0001\u0000\u0000\u0000\u00a5\u00a6\u0001\u0000" +
                    "\u0000\u0000\u00a6\u00a7\u0005\u0000\u0000\u0001\u00a7\u0019\u0001\u0000" +
                    "\u0000\u0000\u0007:Elx\u009d\u009f\u00a4";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}