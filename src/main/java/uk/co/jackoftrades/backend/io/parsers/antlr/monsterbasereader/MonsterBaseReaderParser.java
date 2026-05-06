// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/MonsterBaseReader.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.monsterbasereader;

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
import uk.co.jackoftrades.middle.game.Game;
import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.MonsterPain;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class MonsterBaseReaderParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, NAME = 3, GLYPH = 4, PAIN = 5, FLAGS = 6, DESC = 7, OR = 8, NUMBER = 9,
            SINGLE_CHAR = 10, TEXT = 11;
    public static final int
            RULE_name = 0, RULE_glyph = 1, RULE_pain = 2, RULE_flags = 3, RULE_desc = 4,
            RULE_entry = 5, RULE_monsterBases = 6;

    private static String[] makeRuleNames() {
        return new String[]{
                "name", "glyph", "pain", "flags", "desc", "entry", "monsterBases"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'name:'", "'glyph:'", "'pain:'", "'flags:'", "'desc:'",
                "'| '"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "NAME", "GLYPH", "PAIN", "FLAGS", "DESC", "OR",
                "NUMBER", "SINGLE_CHAR", "TEXT"
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
        return "MonsterBaseReader.g4";
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

    public MonsterBaseReaderParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class NameContext extends ParserRuleContext {
        public String nameStr;
        public Token TEXT;

        public TerminalNode NAME() {
            return getToken(MonsterBaseReaderParser.NAME, 0);
        }

        public TerminalNode TEXT() {
            return getToken(MonsterBaseReaderParser.TEXT, 0);
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
            if (listener instanceof MonsterBaseReaderListener) ((MonsterBaseReaderListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterBaseReaderListener) ((MonsterBaseReaderListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterBaseReaderVisitor)
                return ((MonsterBaseReaderVisitor<? extends T>) visitor).visitName(this);
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
        public Token SINGLE_CHAR;

        public TerminalNode GLYPH() {
            return getToken(MonsterBaseReaderParser.GLYPH, 0);
        }

        public TerminalNode SINGLE_CHAR() {
            return getToken(MonsterBaseReaderParser.SINGLE_CHAR, 0);
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
            if (listener instanceof MonsterBaseReaderListener) ((MonsterBaseReaderListener) listener).enterGlyph(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterBaseReaderListener) ((MonsterBaseReaderListener) listener).exitGlyph(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterBaseReaderVisitor)
                return ((MonsterBaseReaderVisitor<? extends T>) visitor).visitGlyph(this);
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
                ((GlyphContext) _localctx).SINGLE_CHAR = match(SINGLE_CHAR);

                ((GlyphContext) _localctx).glyphChar = ((GlyphContext) _localctx).SINGLE_CHAR.getText().charAt(0);

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
        public MonsterPain painIndex;
        public Token NUMBER;

        public TerminalNode PAIN() {
            return getToken(MonsterBaseReaderParser.PAIN, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(MonsterBaseReaderParser.NUMBER, 0);
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
            if (listener instanceof MonsterBaseReaderListener) ((MonsterBaseReaderListener) listener).enterPain(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterBaseReaderListener) ((MonsterBaseReaderListener) listener).exitPain(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterBaseReaderVisitor)
                return ((MonsterBaseReaderVisitor<? extends T>) visitor).visitPain(this);
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
                ((PainContext) _localctx).NUMBER = match(NUMBER);

                int index = Integer.parseInt(((PainContext) _localctx).NUMBER.getText());
                ((PainContext) _localctx).painIndex = Game.getPainFromIndex(index);

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
        public ArrayList<MonsterRaceFlag> flagList;
        public Token flag1;
        public Token flag2;

        public List<TerminalNode> FLAGS() {
            return getTokens(MonsterBaseReaderParser.FLAGS);
        }

        public TerminalNode FLAGS(int i) {
            return getToken(MonsterBaseReaderParser.FLAGS, i);
        }

        public List<TerminalNode> TEXT() {
            return getTokens(MonsterBaseReaderParser.TEXT);
        }

        public TerminalNode TEXT(int i) {
            return getToken(MonsterBaseReaderParser.TEXT, i);
        }

        public List<TerminalNode> OR() {
            return getTokens(MonsterBaseReaderParser.OR);
        }

        public TerminalNode OR(int i) {
            return getToken(MonsterBaseReaderParser.OR, i);
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
            if (listener instanceof MonsterBaseReaderListener) ((MonsterBaseReaderListener) listener).enterFlags(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterBaseReaderListener) ((MonsterBaseReaderListener) listener).exitFlags(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterBaseReaderVisitor)
                return ((MonsterBaseReaderVisitor<? extends T>) visitor).visitFlags(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FlagsContext flags() throws RecognitionException {
        FlagsContext _localctx = new FlagsContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_flags);

        ((FlagsContext) _localctx).flagList = new ArrayList<MonsterRaceFlag>();

        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(37);
                _errHandler.sync(this);
                _alt = 1;
                do {
                    switch (_alt) {
                        case 1: {
                            {
                                setState(26);
                                match(FLAGS);
                                setState(27);
                                ((FlagsContext) _localctx).flag1 = match(TEXT);

                                _localctx.flagList.add(MonsterRaceFlag.valueOf("RF_" + ((FlagsContext) _localctx).flag1.getText().trim()));

                                setState(34);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                                while (_la == OR) {
                                    {
                                        {
                                            setState(29);
                                            match(OR);
                                            setState(30);
                                            ((FlagsContext) _localctx).flag2 = match(TEXT);

                                            _localctx.flagList.add(MonsterRaceFlag.valueOf("RF_" + ((FlagsContext) _localctx).flag2.getText().trim()));

                                        }
                                    }
                                    setState(36);
                                    _errHandler.sync(this);
                                    _la = _input.LA(1);
                                }
                            }
                        }
                        break;
                        default:
                            throw new NoViableAltException(this);
                    }
                    setState(39);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 1, _ctx);
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
    public static class DescContext extends ParserRuleContext {
        public String descStr;
        public Token TEXT;

        public TerminalNode DESC() {
            return getToken(MonsterBaseReaderParser.DESC, 0);
        }

        public TerminalNode TEXT() {
            return getToken(MonsterBaseReaderParser.TEXT, 0);
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
            if (listener instanceof MonsterBaseReaderListener) ((MonsterBaseReaderListener) listener).enterDesc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterBaseReaderListener) ((MonsterBaseReaderListener) listener).exitDesc(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterBaseReaderVisitor)
                return ((MonsterBaseReaderVisitor<? extends T>) visitor).visitDesc(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DescContext desc() throws RecognitionException {
        DescContext _localctx = new DescContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_desc);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(41);
                match(DESC);
                setState(42);
                ((DescContext) _localctx).TEXT = match(TEXT);

                ((DescContext) _localctx).descStr = ((DescContext) _localctx).TEXT.getText();

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
    public static class EntryContext extends ParserRuleContext {
        public MonsterBase monsterBase;
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

        public List<FlagsContext> flags() {
            return getRuleContexts(FlagsContext.class);
        }

        public FlagsContext flags(int i) {
            return getRuleContext(FlagsContext.class, i);
        }

        public DescContext desc() {
            return getRuleContext(DescContext.class, 0);
        }

        public EntryContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_entry;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterBaseReaderListener) ((MonsterBaseReaderListener) listener).enterEntry(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterBaseReaderListener) ((MonsterBaseReaderListener) listener).exitEntry(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterBaseReaderVisitor)
                return ((MonsterBaseReaderVisitor<? extends T>) visitor).visitEntry(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EntryContext entry() throws RecognitionException {
        EntryContext _localctx = new EntryContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_entry);

        String nameInit = "";
        char glyphInit = '?';
        MonsterPain painInit = null;
        Flag<MonsterRaceFlag> flagsInit = new Flag<MonsterRaceFlag>(MonsterRaceFlag.class);
        String descInit = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(45);
                ((EntryContext) _localctx).name = name();

                nameInit = ((EntryContext) _localctx).name.nameStr;

                setState(47);
                ((EntryContext) _localctx).glyph = glyph();

                glyphInit = ((EntryContext) _localctx).glyph.glyphChar;

                setState(52);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == PAIN) {
                    {
                        setState(49);
                        ((EntryContext) _localctx).pain = pain();

                        painInit = ((EntryContext) _localctx).pain.painIndex;

                    }
                }

                setState(59);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == FLAGS) {
                    {
                        {
                            setState(54);
                            ((EntryContext) _localctx).flags = flags();

                            for (MonsterRaceFlag flg : ((EntryContext) _localctx).flags.flagList) {
                                flagsInit.on(flg);
                            }

                        }
                    }
                    setState(61);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(65);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == DESC) {
                    {
                        setState(62);
                        ((EntryContext) _localctx).desc = desc();

                        descInit = ((EntryContext) _localctx).desc.descStr;

                    }
                }

            }
            _ctx.stop = _input.LT(-1);

            ((EntryContext) _localctx).monsterBase = new MonsterBase(nameInit,
                    nameInit, flagsInit, glyphInit, painInit,
                    descInit);

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
    public static class MonsterBasesContext extends ParserRuleContext {
        public ArrayList<MonsterBase> entries;
        public EntryContext entry;

        public TerminalNode EOF() {
            return getToken(MonsterBaseReaderParser.EOF, 0);
        }

        public List<EntryContext> entry() {
            return getRuleContexts(EntryContext.class);
        }

        public EntryContext entry(int i) {
            return getRuleContext(EntryContext.class, i);
        }

        public MonsterBasesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_monsterBases;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof MonsterBaseReaderListener)
                ((MonsterBaseReaderListener) listener).enterMonsterBases(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof MonsterBaseReaderListener)
                ((MonsterBaseReaderListener) listener).exitMonsterBases(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof MonsterBaseReaderVisitor)
                return ((MonsterBaseReaderVisitor<? extends T>) visitor).visitMonsterBases(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MonsterBasesContext monsterBases() throws RecognitionException {
        MonsterBasesContext _localctx = new MonsterBasesContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_monsterBases);

        ((MonsterBasesContext) _localctx).entries = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(70);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(67);
                            ((MonsterBasesContext) _localctx).entry = entry();

                            _localctx.entries.add(((MonsterBasesContext) _localctx).entry.monsterBase);

                        }
                    }
                    setState(72);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(74);
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
            "\u0004\u0001\u000bM\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0005\u0003!\b\u0003\n\u0003" +
                    "\f\u0003$\t\u0003\u0004\u0003&\b\u0003\u000b\u0003\f\u0003\'\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0003\u00055\b\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0005\u0005:\b\u0005\n\u0005\f\u0005" +
                    "=\t\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0003\u0005B\b\u0005\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0004\u0006G\b\u0006\u000b\u0006\f\u0006" +
                    "H\u0001\u0006\u0001\u0006\u0001\u0006\u0000\u0000\u0007\u0000\u0002\u0004" +
                    "\u0006\b\n\f\u0000\u0000K\u0000\u000e\u0001\u0000\u0000\u0000\u0002\u0012" +
                    "\u0001\u0000\u0000\u0000\u0004\u0016\u0001\u0000\u0000\u0000\u0006%\u0001" +
                    "\u0000\u0000\u0000\b)\u0001\u0000\u0000\u0000\n-\u0001\u0000\u0000\u0000" +
                    "\fF\u0001\u0000\u0000\u0000\u000e\u000f\u0005\u0003\u0000\u0000\u000f" +
                    "\u0010\u0005\u000b\u0000\u0000\u0010\u0011\u0006\u0000\uffff\uffff\u0000" +
                    "\u0011\u0001\u0001\u0000\u0000\u0000\u0012\u0013\u0005\u0004\u0000\u0000" +
                    "\u0013\u0014\u0005\n\u0000\u0000\u0014\u0015\u0006\u0001\uffff\uffff\u0000" +
                    "\u0015\u0003\u0001\u0000\u0000\u0000\u0016\u0017\u0005\u0005\u0000\u0000" +
                    "\u0017\u0018\u0005\t\u0000\u0000\u0018\u0019\u0006\u0002\uffff\uffff\u0000" +
                    "\u0019\u0005\u0001\u0000\u0000\u0000\u001a\u001b\u0005\u0006\u0000\u0000" +
                    "\u001b\u001c\u0005\u000b\u0000\u0000\u001c\"\u0006\u0003\uffff\uffff\u0000" +
                    "\u001d\u001e\u0005\b\u0000\u0000\u001e\u001f\u0005\u000b\u0000\u0000\u001f" +
                    "!\u0006\u0003\uffff\uffff\u0000 \u001d\u0001\u0000\u0000\u0000!$\u0001" +
                    "\u0000\u0000\u0000\" \u0001\u0000\u0000\u0000\"#\u0001\u0000\u0000\u0000" +
                    "#&\u0001\u0000\u0000\u0000$\"\u0001\u0000\u0000\u0000%\u001a\u0001\u0000" +
                    "\u0000\u0000&\'\u0001\u0000\u0000\u0000\'%\u0001\u0000\u0000\u0000\'(" +
                    "\u0001\u0000\u0000\u0000(\u0007\u0001\u0000\u0000\u0000)*\u0005\u0007" +
                    "\u0000\u0000*+\u0005\u000b\u0000\u0000+,\u0006\u0004\uffff\uffff\u0000" +
                    ",\t\u0001\u0000\u0000\u0000-.\u0003\u0000\u0000\u0000./\u0006\u0005\uffff" +
                    "\uffff\u0000/0\u0003\u0002\u0001\u000004\u0006\u0005\uffff\uffff\u0000" +
                    "12\u0003\u0004\u0002\u000023\u0006\u0005\uffff\uffff\u000035\u0001\u0000" +
                    "\u0000\u000041\u0001\u0000\u0000\u000045\u0001\u0000\u0000\u00005;\u0001" +
                    "\u0000\u0000\u000067\u0003\u0006\u0003\u000078\u0006\u0005\uffff\uffff" +
                    "\u00008:\u0001\u0000\u0000\u000096\u0001\u0000\u0000\u0000:=\u0001\u0000" +
                    "\u0000\u0000;9\u0001\u0000\u0000\u0000;<\u0001\u0000\u0000\u0000<A\u0001" +
                    "\u0000\u0000\u0000=;\u0001\u0000\u0000\u0000>?\u0003\b\u0004\u0000?@\u0006" +
                    "\u0005\uffff\uffff\u0000@B\u0001\u0000\u0000\u0000A>\u0001\u0000\u0000" +
                    "\u0000AB\u0001\u0000\u0000\u0000B\u000b\u0001\u0000\u0000\u0000CD\u0003" +
                    "\n\u0005\u0000DE\u0006\u0006\uffff\uffff\u0000EG\u0001\u0000\u0000\u0000" +
                    "FC\u0001\u0000\u0000\u0000GH\u0001\u0000\u0000\u0000HF\u0001\u0000\u0000" +
                    "\u0000HI\u0001\u0000\u0000\u0000IJ\u0001\u0000\u0000\u0000JK\u0005\u0000" +
                    "\u0000\u0001K\r\u0001\u0000\u0000\u0000\u0006\"\'4;AH";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}