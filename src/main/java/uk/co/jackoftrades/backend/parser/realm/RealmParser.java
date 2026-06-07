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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Realm.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.realm;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.magic.MagicRealm;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class RealmParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, NAME = 3, STAT = 4, VERB = 5, SPELL_NOUN = 6, BOOK_NOUN = 7, UCASE = 8,
            LCASE = 9;
    public static final int
            RULE_name = 0, RULE_stat = 1, RULE_verb = 2, RULE_spell_noun = 3, RULE_book_noun = 4,
            RULE_realm = 5, RULE_file = 6;

    private static String[] makeRuleNames() {
        return new String[]{
                "name", "stat", "verb", "spell_noun", "book_noun", "realm", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'name:'", "'stat:'", "'verb:'", "'spell-noun:'", "'book-noun:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "NAME", "STAT", "VERB", "SPELL_NOUN", "BOOK_NOUN",
                "UCASE", "LCASE"
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
        return "Realm.g4";
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

    public RealmParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class NameContext extends ParserRuleContext {
        public String nameStr;
        public Token LCASE;

        public TerminalNode NAME() {
            return getToken(RealmParser.NAME, 0);
        }

        public TerminalNode LCASE() {
            return getToken(RealmParser.LCASE, 0);
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
            if (listener instanceof RealmListener) ((RealmListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof RealmListener) ((RealmListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RealmVisitor) return ((RealmVisitor<? extends T>) visitor).visitName(this);
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
                ((NameContext) _localctx).LCASE = match(LCASE);
                ((NameContext) _localctx).nameStr = ((NameContext) _localctx).LCASE.getText();
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
    public static class StatContext extends ParserRuleContext {
        public Stats statEnum;
        public Token UCASE;

        public TerminalNode STAT() {
            return getToken(RealmParser.STAT, 0);
        }

        public TerminalNode UCASE() {
            return getToken(RealmParser.UCASE, 0);
        }

        public StatContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_stat;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof RealmListener) ((RealmListener) listener).enterStat(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof RealmListener) ((RealmListener) listener).exitStat(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RealmVisitor) return ((RealmVisitor<? extends T>) visitor).visitStat(this);
            else return visitor.visitChildren(this);
        }
    }

    public final StatContext stat() throws RecognitionException {
        StatContext _localctx = new StatContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_stat);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(18);
                match(STAT);
                setState(19);
                ((StatContext) _localctx).UCASE = match(UCASE);

                String raw = "STAT_" + ((StatContext) _localctx).UCASE.getText().toUpperCase().trim();
                ((StatContext) _localctx).statEnum = Stats.valueOf(raw);

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
    public static class VerbContext extends ParserRuleContext {
        public String verbStr;
        public Token LCASE;

        public TerminalNode VERB() {
            return getToken(RealmParser.VERB, 0);
        }

        public TerminalNode LCASE() {
            return getToken(RealmParser.LCASE, 0);
        }

        public VerbContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_verb;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof RealmListener) ((RealmListener) listener).enterVerb(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof RealmListener) ((RealmListener) listener).exitVerb(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RealmVisitor) return ((RealmVisitor<? extends T>) visitor).visitVerb(this);
            else return visitor.visitChildren(this);
        }
    }

    public final VerbContext verb() throws RecognitionException {
        VerbContext _localctx = new VerbContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_verb);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(22);
                match(VERB);
                setState(23);
                ((VerbContext) _localctx).LCASE = match(LCASE);
                ((VerbContext) _localctx).verbStr = ((VerbContext) _localctx).LCASE.getText();
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
    public static class Spell_nounContext extends ParserRuleContext {
        public String nounStr;
        public Token LCASE;

        public TerminalNode SPELL_NOUN() {
            return getToken(RealmParser.SPELL_NOUN, 0);
        }

        public TerminalNode LCASE() {
            return getToken(RealmParser.LCASE, 0);
        }

        public Spell_nounContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_spell_noun;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof RealmListener) ((RealmListener) listener).enterSpell_noun(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof RealmListener) ((RealmListener) listener).exitSpell_noun(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RealmVisitor) return ((RealmVisitor<? extends T>) visitor).visitSpell_noun(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Spell_nounContext spell_noun() throws RecognitionException {
        Spell_nounContext _localctx = new Spell_nounContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_spell_noun);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(26);
                match(SPELL_NOUN);
                setState(27);
                ((Spell_nounContext) _localctx).LCASE = match(LCASE);
                ((Spell_nounContext) _localctx).nounStr = ((Spell_nounContext) _localctx).LCASE.getText();
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
    public static class Book_nounContext extends ParserRuleContext {
        public TValue bookTVal;
        public Token LCASE;

        public TerminalNode BOOK_NOUN() {
            return getToken(RealmParser.BOOK_NOUN, 0);
        }

        public TerminalNode LCASE() {
            return getToken(RealmParser.LCASE, 0);
        }

        public Book_nounContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_book_noun;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof RealmListener) ((RealmListener) listener).enterBook_noun(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof RealmListener) ((RealmListener) listener).exitBook_noun(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RealmVisitor) return ((RealmVisitor<? extends T>) visitor).visitBook_noun(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Book_nounContext book_noun() throws RecognitionException {
        Book_nounContext _localctx = new Book_nounContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_book_noun);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(30);
                match(BOOK_NOUN);
                setState(31);
                ((Book_nounContext) _localctx).LCASE = match(LCASE);
                ((Book_nounContext) _localctx).bookTVal = TValue.fromName(((Book_nounContext) _localctx).LCASE.getText());
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
    public static class RealmContext extends ParserRuleContext {
        public MagicRealm magicRealm;
        public NameContext name;
        public StatContext stat;
        public VerbContext verb;
        public Spell_nounContext spell_noun;
        public Book_nounContext book_noun;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public List<StatContext> stat() {
            return getRuleContexts(StatContext.class);
        }

        public StatContext stat(int i) {
            return getRuleContext(StatContext.class, i);
        }

        public List<VerbContext> verb() {
            return getRuleContexts(VerbContext.class);
        }

        public VerbContext verb(int i) {
            return getRuleContext(VerbContext.class, i);
        }

        public List<Spell_nounContext> spell_noun() {
            return getRuleContexts(Spell_nounContext.class);
        }

        public Spell_nounContext spell_noun(int i) {
            return getRuleContext(Spell_nounContext.class, i);
        }

        public List<Book_nounContext> book_noun() {
            return getRuleContexts(Book_nounContext.class);
        }

        public Book_nounContext book_noun(int i) {
            return getRuleContext(Book_nounContext.class, i);
        }

        public RealmContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_realm;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof RealmListener) ((RealmListener) listener).enterRealm(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof RealmListener) ((RealmListener) listener).exitRealm(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RealmVisitor) return ((RealmVisitor<? extends T>) visitor).visitRealm(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RealmContext realm() throws RecognitionException {
        RealmContext _localctx = new RealmContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_realm);

        String nameInit = "";
        Stats statInit = Stats.STAT_NONE;
        String verbInit = "";
        String nounInit = "";
        TValue bookInit = TValue.TV_NONE;

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(34);
                ((RealmContext) _localctx).name = name();
                nameInit = ((RealmContext) _localctx).name.nameStr;
                setState(48);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        setState(48);
                        _errHandler.sync(this);
                        switch (_input.LA(1)) {
                            case STAT: {
                                setState(36);
                                ((RealmContext) _localctx).stat = stat();
                                statInit = ((RealmContext) _localctx).stat.statEnum;
                            }
                            break;
                            case VERB: {
                                setState(39);
                                ((RealmContext) _localctx).verb = verb();
                                verbInit = ((RealmContext) _localctx).verb.verbStr;
                            }
                            break;
                            case SPELL_NOUN: {
                                setState(42);
                                ((RealmContext) _localctx).spell_noun = spell_noun();
                                nounInit = ((RealmContext) _localctx).spell_noun.nounStr;
                            }
                            break;
                            case BOOK_NOUN: {
                                setState(45);
                                ((RealmContext) _localctx).book_noun = book_noun();
                                bookInit = ((RealmContext) _localctx).book_noun.bookTVal;
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                    }
                    setState(50);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 240L) != 0));
            }
            _ctx.stop = _input.LT(-1);

            ((RealmContext) _localctx).magicRealm = new MagicRealm(nameInit, statInit, verbInit, nounInit, bookInit);

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
        public List<MagicRealm> realms;
        public RealmContext realm;

        public TerminalNode EOF() {
            return getToken(RealmParser.EOF, 0);
        }

        public List<RealmContext> realm() {
            return getRuleContexts(RealmContext.class);
        }

        public RealmContext realm(int i) {
            return getRuleContext(RealmContext.class, i);
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
            if (listener instanceof RealmListener) ((RealmListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof RealmListener) ((RealmListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RealmVisitor) return ((RealmVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_file);

        ((FileContext) _localctx).realms = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(55);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(52);
                            ((FileContext) _localctx).realm = realm();
                            _localctx.realms.add(((FileContext) _localctx).realm.magicRealm);
                        }
                    }
                    setState(57);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(59);
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
            "\u0004\u0001\t>\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0004\u00051\b\u0005\u000b\u0005\f\u00052\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0004\u00068\b\u0006\u000b\u0006\f\u00069\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0000\u0000\u0007\u0000\u0002\u0004\u0006" +
                    "\b\n\f\u0000\u0000;\u0000\u000e\u0001\u0000\u0000\u0000\u0002\u0012\u0001" +
                    "\u0000\u0000\u0000\u0004\u0016\u0001\u0000\u0000\u0000\u0006\u001a\u0001" +
                    "\u0000\u0000\u0000\b\u001e\u0001\u0000\u0000\u0000\n\"\u0001\u0000\u0000" +
                    "\u0000\f7\u0001\u0000\u0000\u0000\u000e\u000f\u0005\u0003\u0000\u0000" +
                    "\u000f\u0010\u0005\t\u0000\u0000\u0010\u0011\u0006\u0000\uffff\uffff\u0000" +
                    "\u0011\u0001\u0001\u0000\u0000\u0000\u0012\u0013\u0005\u0004\u0000\u0000" +
                    "\u0013\u0014\u0005\b\u0000\u0000\u0014\u0015\u0006\u0001\uffff\uffff\u0000" +
                    "\u0015\u0003\u0001\u0000\u0000\u0000\u0016\u0017\u0005\u0005\u0000\u0000" +
                    "\u0017\u0018\u0005\t\u0000\u0000\u0018\u0019\u0006\u0002\uffff\uffff\u0000" +
                    "\u0019\u0005\u0001\u0000\u0000\u0000\u001a\u001b\u0005\u0006\u0000\u0000" +
                    "\u001b\u001c\u0005\t\u0000\u0000\u001c\u001d\u0006\u0003\uffff\uffff\u0000" +
                    "\u001d\u0007\u0001\u0000\u0000\u0000\u001e\u001f\u0005\u0007\u0000\u0000" +
                    "\u001f \u0005\t\u0000\u0000 !\u0006\u0004\uffff\uffff\u0000!\t\u0001\u0000" +
                    "\u0000\u0000\"#\u0003\u0000\u0000\u0000#0\u0006\u0005\uffff\uffff\u0000" +
                    "$%\u0003\u0002\u0001\u0000%&\u0006\u0005\uffff\uffff\u0000&1\u0001\u0000" +
                    "\u0000\u0000\'(\u0003\u0004\u0002\u0000()\u0006\u0005\uffff\uffff\u0000" +
                    ")1\u0001\u0000\u0000\u0000*+\u0003\u0006\u0003\u0000+,\u0006\u0005\uffff" +
                    "\uffff\u0000,1\u0001\u0000\u0000\u0000-.\u0003\b\u0004\u0000./\u0006\u0005" +
                    "\uffff\uffff\u0000/1\u0001\u0000\u0000\u00000$\u0001\u0000\u0000\u0000" +
                    "0\'\u0001\u0000\u0000\u00000*\u0001\u0000\u0000\u00000-\u0001\u0000\u0000" +
                    "\u000012\u0001\u0000\u0000\u000020\u0001\u0000\u0000\u000023\u0001\u0000" +
                    "\u0000\u00003\u000b\u0001\u0000\u0000\u000045\u0003\n\u0005\u000056\u0006" +
                    "\u0006\uffff\uffff\u000068\u0001\u0000\u0000\u000074\u0001\u0000\u0000" +
                    "\u000089\u0001\u0000\u0000\u000097\u0001\u0000\u0000\u00009:\u0001\u0000" +
                    "\u0000\u0000:;\u0001\u0000\u0000\u0000;<\u0005\u0000\u0000\u0001<\r\u0001" +
                    "\u0000\u0000\u0000\u0003029";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}