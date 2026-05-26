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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/MonsterBase.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.monsterbase;

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
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.MonsterPain;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class MonsterBaseParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            T__0 = 1, COMMENT = 2, EOL = 3, NAME = 4, GLYPH = 5, PAIN = 6, FLAGS = 7, DESC = 8, PAIN_NUMBER = 9,
            TEXT = 10;
    public static final int
            RULE_name = 0, RULE_glyph = 1, RULE_pain = 2, RULE_flags = 3, RULE_desc = 4,
            RULE_monsterBase = 5, RULE_file = 6;

    private static String[] makeRuleNames() {
        return new String[]{
                "name", "glyph", "pain", "flags", "desc", "monsterBase", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'|'", null, null, "'name:'", "'glyph:'", "'pain:'", "'flags:'",
                "'desc:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, null, "COMMENT", "EOL", "NAME", "GLYPH", "PAIN", "FLAGS", "DESC",
                "PAIN_NUMBER", "TEXT"
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
        return "MonsterBase.g4";
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

    public MonsterBaseParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class NameContext extends ParserRuleContext {
        public String nameStr;
        public Token TEXT;

        public TerminalNode NAME() {
            return getToken(MonsterBaseParser.NAME, 0);
        }

        public TerminalNode TEXT() {
            return getToken(MonsterBaseParser.TEXT, 0);
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
            if (listener instanceof MonsterBaseListener) ((MonsterBaseListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterBaseListener) ((MonsterBaseListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterBaseVisitor)
                return ((MonsterBaseVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(14);
                match(NAME);
                setState(15);
                ((NameContext) _localctx).TEXT = match(TEXT);
                ((NameContext) _localctx).nameStr = ((NameContext) _localctx).TEXT.getText();
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
        public char glyphChar;
        public Token TEXT;

        public TerminalNode GLYPH() {
            return getToken(MonsterBaseParser.GLYPH, 0);
        }

        public TerminalNode TEXT() {
            return getToken(MonsterBaseParser.TEXT, 0);
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
            if (listener instanceof MonsterBaseListener) ((MonsterBaseListener) listener).enterGlyph(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterBaseListener) ((MonsterBaseListener) listener).exitGlyph(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterBaseVisitor)
                return ((MonsterBaseVisitor<? extends T>) visitor).visitGlyph(this);
            else return visitor.visitChildren(this);
        }
    }

    public final GlyphContext glyph() throws RecognitionException {
        GlyphContext _localctx = new GlyphContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_glyph);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(18);
                match(GLYPH);
                setState(19);
                ((GlyphContext) _localctx).TEXT = match(TEXT);

                String raw = ((GlyphContext) _localctx).TEXT.getText();

                if (raw.length() != 1)
                    throw new IllegalArgumentException(
                            "Glyph must be exactly one character while parsing MonsterBases"
                    );

                ((GlyphContext) _localctx).glyphChar = raw.charAt(0);

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
    public static class PainContext extends ParserRuleContext {
        public MonsterPain monPain;
        public Token PAIN_NUMBER;

        public TerminalNode PAIN() {
            return getToken(MonsterBaseParser.PAIN, 0);
        }

        public TerminalNode PAIN_NUMBER() {
            return getToken(MonsterBaseParser.PAIN_NUMBER, 0);
        }

        public PainContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_pain;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterBaseListener) ((MonsterBaseListener) listener).enterPain(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterBaseListener) ((MonsterBaseListener) listener).exitPain(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterBaseVisitor)
                return ((MonsterBaseVisitor<? extends T>) visitor).visitPain(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PainContext pain() throws RecognitionException {
        PainContext _localctx = new PainContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_pain);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(22);
                match(PAIN);
                setState(23);
                ((PainContext) _localctx).PAIN_NUMBER = match(PAIN_NUMBER);

                int index = Integer.parseInt(((PainContext) _localctx).PAIN_NUMBER.getText());
                ((PainContext) _localctx).monPain = GameConstants.lookupMonsterPain(index);

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
        public Flag<MonsterRaceFlag> raceFlags;
        public Token flag1;
        public Token flag2;

        public TerminalNode FLAGS() {
            return getToken(MonsterBaseParser.FLAGS, 0);
        }

        public List<TerminalNode> TEXT() {
            return getTokens(MonsterBaseParser.TEXT);
        }

        public TerminalNode TEXT(int i) {
            return getToken(MonsterBaseParser.TEXT, i);
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
            if (listener instanceof MonsterBaseListener) ((MonsterBaseListener) listener).enterFlags(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterBaseListener) ((MonsterBaseListener) listener).exitFlags(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterBaseVisitor)
                return ((MonsterBaseVisitor<? extends T>) visitor).visitFlags(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FlagsContext flags() throws RecognitionException {
        FlagsContext _localctx = new FlagsContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_flags);

        ((FlagsContext) _localctx).raceFlags = new Flag<>(MonsterRaceFlag.class);

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(26);
                match(FLAGS);
                setState(27);
                ((FlagsContext) _localctx).flag1 = match(TEXT);

                _localctx.raceFlags.on(MonsterRaceFlag.valueOf("RF_" + ((FlagsContext) _localctx).flag1.getText().trim()));

                setState(34);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == T__0) {
                    {
                        {
                            setState(29);
                            match(T__0);
                            setState(30);
                            ((FlagsContext) _localctx).flag2 = match(TEXT);

                            _localctx.raceFlags.on(MonsterRaceFlag.valueOf("RF_" + ((FlagsContext) _localctx).flag2.getText().trim()));

                        }
                    }
                    setState(36);
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
    public static class DescContext extends ParserRuleContext {
        public String description;
        public Token TEXT;

        public TerminalNode DESC() {
            return getToken(MonsterBaseParser.DESC, 0);
        }

        public TerminalNode TEXT() {
            return getToken(MonsterBaseParser.TEXT, 0);
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
            if (listener instanceof MonsterBaseListener) ((MonsterBaseListener) listener).enterDesc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterBaseListener) ((MonsterBaseListener) listener).exitDesc(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterBaseVisitor)
                return ((MonsterBaseVisitor<? extends T>) visitor).visitDesc(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DescContext desc() throws RecognitionException {
        DescContext _localctx = new DescContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_desc);

        ((DescContext) _localctx).description = "";

        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(37);
                match(DESC);
                setState(38);
                ((DescContext) _localctx).TEXT = match(TEXT);
                ((DescContext) _localctx).description = _localctx.description + ((DescContext) _localctx).TEXT.getText();
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
    public static class MonsterBaseContext extends ParserRuleContext {
        public MonsterBase base;
        public NameContext name;
        public GlyphContext glyph;
        public PainContext pain;
        public FlagsContext flags;
        public DescContext desc;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public GlyphContext glyph() {
            return getRuleContext(GlyphContext.class, 0);
        }

        public PainContext pain() {
            return getRuleContext(PainContext.class, 0);
        }

        public DescContext desc() {
            return getRuleContext(DescContext.class, 0);
        }

        public List<FlagsContext> flags() {
            return getRuleContexts(FlagsContext.class);
        }

        public FlagsContext flags(int i) {
            return getRuleContext(FlagsContext.class, i);
        }

        public MonsterBaseContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_monsterBase;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterBaseListener) ((MonsterBaseListener) listener).enterMonsterBase(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterBaseListener) ((MonsterBaseListener) listener).exitMonsterBase(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterBaseVisitor)
                return ((MonsterBaseVisitor<? extends T>) visitor).visitMonsterBase(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MonsterBaseContext monsterBase() throws RecognitionException {
        MonsterBaseContext _localctx = new MonsterBaseContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_monsterBase);

        String nameInit = "";
        char glyphInit = '\0';
        MonsterPain monPainInit = null;
        Flag<MonsterRaceFlag> flagsInit = new Flag<>(MonsterRaceFlag.class);
        String descInit = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(41);
                ((MonsterBaseContext) _localctx).name = name();
                nameInit = ((MonsterBaseContext) _localctx).name.nameStr;
                setState(43);
                ((MonsterBaseContext) _localctx).glyph = glyph();
                glyphInit = ((MonsterBaseContext) _localctx).glyph.glyphChar;
                setState(45);
                ((MonsterBaseContext) _localctx).pain = pain();
                monPainInit = ((MonsterBaseContext) _localctx).pain.monPain;
                setState(52);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == FLAGS) {
                    {
                        {
                            setState(47);
                            ((MonsterBaseContext) _localctx).flags = flags();

                            flagsInit.union(((MonsterBaseContext) _localctx).flags.raceFlags);

                        }
                    }
                    setState(54);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(55);
                ((MonsterBaseContext) _localctx).desc = desc();
                descInit = descInit + ((MonsterBaseContext) _localctx).desc.description;
            }
            _ctx.stop = _input.LT(-1);

            ((MonsterBaseContext) _localctx).base = new MonsterBase(nameInit, nameInit, flagsInit,
                    glyphInit, monPainInit, descInit);

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
        public List<MonsterBase> bases;
        public MonsterBaseContext monsterBase;

        public TerminalNode EOF() {
            return getToken(MonsterBaseParser.EOF, 0);
        }

        public List<MonsterBaseContext> monsterBase() {
            return getRuleContexts(MonsterBaseContext.class);
        }

        public MonsterBaseContext monsterBase(int i) {
            return getRuleContext(MonsterBaseContext.class, i);
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
            if (listener instanceof MonsterBaseListener) ((MonsterBaseListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterBaseListener) ((MonsterBaseListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterBaseVisitor)
                return ((MonsterBaseVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_file);

        ((FileContext) _localctx).bases = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(61);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(58);
                            ((FileContext) _localctx).monsterBase = monsterBase();
                            _localctx.bases.add(((FileContext) _localctx).monsterBase.base);
                        }
                    }
                    setState(63);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(65);
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
            "\u0004\u0001\nD\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0005\u0003!\b\u0003\n\u0003" +
                    "\f\u0003$\t\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0005\u00053\b\u0005\n\u0005\f\u00056\t" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0004\u0006>\b\u0006\u000b\u0006\f\u0006?\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0000\u0000\u0007\u0000\u0002\u0004\u0006\b\n\f\u0000\u0000" +
                    "?\u0000\u000e\u0001\u0000\u0000\u0000\u0002\u0012\u0001\u0000\u0000\u0000" +
                    "\u0004\u0016\u0001\u0000\u0000\u0000\u0006\u001a\u0001\u0000\u0000\u0000" +
                    "\b%\u0001\u0000\u0000\u0000\n)\u0001\u0000\u0000\u0000\f=\u0001\u0000" +
                    "\u0000\u0000\u000e\u000f\u0005\u0004\u0000\u0000\u000f\u0010\u0005\n\u0000" +
                    "\u0000\u0010\u0011\u0006\u0000\uffff\uffff\u0000\u0011\u0001\u0001\u0000" +
                    "\u0000\u0000\u0012\u0013\u0005\u0005\u0000\u0000\u0013\u0014\u0005\n\u0000" +
                    "\u0000\u0014\u0015\u0006\u0001\uffff\uffff\u0000\u0015\u0003\u0001\u0000" +
                    "\u0000\u0000\u0016\u0017\u0005\u0006\u0000\u0000\u0017\u0018\u0005\t\u0000" +
                    "\u0000\u0018\u0019\u0006\u0002\uffff\uffff\u0000\u0019\u0005\u0001\u0000" +
                    "\u0000\u0000\u001a\u001b\u0005\u0007\u0000\u0000\u001b\u001c\u0005\n\u0000" +
                    "\u0000\u001c\"\u0006\u0003\uffff\uffff\u0000\u001d\u001e\u0005\u0001\u0000" +
                    "\u0000\u001e\u001f\u0005\n\u0000\u0000\u001f!\u0006\u0003\uffff\uffff" +
                    "\u0000 \u001d\u0001\u0000\u0000\u0000!$\u0001\u0000\u0000\u0000\" \u0001" +
                    "\u0000\u0000\u0000\"#\u0001\u0000\u0000\u0000#\u0007\u0001\u0000\u0000" +
                    "\u0000$\"\u0001\u0000\u0000\u0000%&\u0005\b\u0000\u0000&\'\u0005\n\u0000" +
                    "\u0000\'(\u0006\u0004\uffff\uffff\u0000(\t\u0001\u0000\u0000\u0000)*\u0003" +
                    "\u0000\u0000\u0000*+\u0006\u0005\uffff\uffff\u0000+,\u0003\u0002\u0001" +
                    "\u0000,-\u0006\u0005\uffff\uffff\u0000-.\u0003\u0004\u0002\u0000.4\u0006" +
                    "\u0005\uffff\uffff\u0000/0\u0003\u0006\u0003\u000001\u0006\u0005\uffff" +
                    "\uffff\u000013\u0001\u0000\u0000\u00002/\u0001\u0000\u0000\u000036\u0001" +
                    "\u0000\u0000\u000042\u0001\u0000\u0000\u000045\u0001\u0000\u0000\u0000" +
                    "57\u0001\u0000\u0000\u000064\u0001\u0000\u0000\u000078\u0003\b\u0004\u0000" +
                    "89\u0006\u0005\uffff\uffff\u00009\u000b\u0001\u0000\u0000\u0000:;\u0003" +
                    "\n\u0005\u0000;<\u0006\u0006\uffff\uffff\u0000<>\u0001\u0000\u0000\u0000" +
                    "=:\u0001\u0000\u0000\u0000>?\u0001\u0000\u0000\u0000?=\u0001\u0000\u0000" +
                    "\u0000?@\u0001\u0000\u0000\u0000@A\u0001\u0000\u0000\u0000AB\u0005\u0000" +
                    "\u0000\u0001B\r\u0001\u0000\u0000\u0000\u0003\"4?";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}