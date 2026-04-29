// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/background/io/parsers/antlr/grammars/SummonFormatter.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.background.io.parsers.antlr.summon;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.MonsterBases;
import uk.co.jackoftrades.middle.monsters.Summon;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class SummonFormatterParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, WORD = 2, SPACE = 3, EOL = 4, NAMETAG = 5, MSGTTAG = 6, UNIQUESTAG = 7,
            BASETAG = 8, RACEFLAG = 9, FALLBACKTAG = 10, DESCTAG = 11, BOOLEAN = 12;
    public static final int
            RULE_word = 0, RULE_tagText = 1, RULE_one_or_zero = 2, RULE_summon = 3,
            RULE_file = 4;

    private static String[] makeRuleNames() {
        return new String[]{
                "word", "tagText", "one_or_zero", "summon", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "' '"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "WORD", "SPACE", "EOL", "NAMETAG", "MSGTTAG", "UNIQUESTAG",
                "BASETAG", "RACEFLAG", "FALLBACKTAG", "DESCTAG", "BOOLEAN"
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
        return "SummonFormatter.g4";
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

    public SummonFormatterParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class WordContext extends ParserRuleContext {
        public String wrd;
        public Token WORD;

        public TerminalNode WORD() {
            return getToken(SummonFormatterParser.WORD, 0);
        }

        public WordContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_word;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).enterWord(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).exitWord(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SummonFormatterVisitor)
                return ((SummonFormatterVisitor<? extends T>) visitor).visitWord(this);
            else return visitor.visitChildren(this);
        }
    }

    public final WordContext word() throws RecognitionException {
        WordContext _localctx = new WordContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_word);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(10);
                ((WordContext) _localctx).WORD = match(WORD);
                ((WordContext) _localctx).wrd = ((WordContext) _localctx).WORD.toString();

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
    public static class TagTextContext extends ParserRuleContext {
        public String txt;
        public Token word1;
        public Token word2;

        public List<TerminalNode> WORD() {
            return getTokens(SummonFormatterParser.WORD);
        }

        public TerminalNode WORD(int i) {
            return getToken(SummonFormatterParser.WORD, i);
        }

        public TerminalNode SPACE() {
            return getToken(SummonFormatterParser.SPACE, 0);
        }

        public TagTextContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_tagText;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).enterTagText(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).exitTagText(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SummonFormatterVisitor)
                return ((SummonFormatterVisitor<? extends T>) visitor).visitTagText(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TagTextContext tagText() throws RecognitionException {
        TagTextContext _localctx = new TagTextContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_tagText);

        StringBuilder sb = new StringBuilder();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(13);
                ((TagTextContext) _localctx).word1 = match(WORD);
                sb.append(((TagTextContext) _localctx).word1.getText());

                setState(18);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == SPACE) {
                    {
                        setState(15);
                        match(SPACE);
                        setState(16);
                        ((TagTextContext) _localctx).word2 = match(WORD);
                        sb.append(" ").append(((TagTextContext) _localctx).word2.getText());

                    }
                }

                ((TagTextContext) _localctx).txt = sb.toString();
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
    public static class One_or_zeroContext extends ParserRuleContext {
        public boolean unique;
        public Token BOOLEAN;

        public TerminalNode BOOLEAN() {
            return getToken(SummonFormatterParser.BOOLEAN, 0);
        }

        public One_or_zeroContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_one_or_zero;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SummonFormatterListener)
                ((SummonFormatterListener) listener).enterOne_or_zero(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).exitOne_or_zero(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SummonFormatterVisitor)
                return ((SummonFormatterVisitor<? extends T>) visitor).visitOne_or_zero(this);
            else return visitor.visitChildren(this);
        }
    }

    public final One_or_zeroContext one_or_zero() throws RecognitionException {
        One_or_zeroContext _localctx = new One_or_zeroContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_one_or_zero);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(22);
                ((One_or_zeroContext) _localctx).BOOLEAN = match(BOOLEAN);
                ((One_or_zeroContext) _localctx).unique = (Integer.parseInt(((One_or_zeroContext) _localctx).BOOLEAN.getText()) == 1);

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
    public static class SummonContext extends ParserRuleContext {
        public Summon smn;
        public WordContext word;
        public One_or_zeroContext one_or_zero;
        public TagTextContext tagText;

        public TerminalNode NAMETAG() {
            return getToken(SummonFormatterParser.NAMETAG, 0);
        }

        public List<WordContext> word() {
            return getRuleContexts(WordContext.class);
        }

        public WordContext word(int i) {
            return getRuleContext(WordContext.class, i);
        }

        public List<TerminalNode> EOL() {
            return getTokens(SummonFormatterParser.EOL);
        }

        public TerminalNode EOL(int i) {
            return getToken(SummonFormatterParser.EOL, i);
        }

        public TerminalNode MSGTTAG() {
            return getToken(SummonFormatterParser.MSGTTAG, 0);
        }

        public TerminalNode UNIQUESTAG() {
            return getToken(SummonFormatterParser.UNIQUESTAG, 0);
        }

        public One_or_zeroContext one_or_zero() {
            return getRuleContext(One_or_zeroContext.class, 0);
        }

        public TerminalNode DESCTAG() {
            return getToken(SummonFormatterParser.DESCTAG, 0);
        }

        public List<TagTextContext> tagText() {
            return getRuleContexts(TagTextContext.class);
        }

        public TagTextContext tagText(int i) {
            return getRuleContext(TagTextContext.class, i);
        }

        public List<TerminalNode> BASETAG() {
            return getTokens(SummonFormatterParser.BASETAG);
        }

        public TerminalNode BASETAG(int i) {
            return getToken(SummonFormatterParser.BASETAG, i);
        }

        public TerminalNode RACEFLAG() {
            return getToken(SummonFormatterParser.RACEFLAG, 0);
        }

        public TerminalNode FALLBACKTAG() {
            return getToken(SummonFormatterParser.FALLBACKTAG, 0);
        }

        public SummonContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_summon;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).enterSummon(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).exitSummon(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SummonFormatterVisitor)
                return ((SummonFormatterVisitor<? extends T>) visitor).visitSummon(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SummonContext summon() throws RecognitionException {
        SummonContext _localctx = new SummonContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_summon);

        String nameTag = "";
        String msgTag = "";
        boolean unique = false;
        MonsterBases mb = new MonsterBases();
        String raceFlag = "";
        String fallBack = "";
        String desc = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(25);
                match(NAMETAG);
                setState(26);
                ((SummonContext) _localctx).word = word();
                nameTag = ((SummonContext) _localctx).word.wrd;
                setState(28);
                match(EOL);
                setState(29);
                match(MSGTTAG);
                setState(30);
                ((SummonContext) _localctx).word = word();
                msgTag = ((SummonContext) _localctx).word.wrd;
                setState(32);
                match(EOL);
                setState(33);
                match(UNIQUESTAG);
                setState(34);
                ((SummonContext) _localctx).one_or_zero = one_or_zero();
                unique = ((SummonContext) _localctx).one_or_zero.unique;
                setState(36);
                match(EOL);
                setState(44);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == BASETAG) {
                    {
                        {
                            setState(37);
                            match(BASETAG);
                            setState(38);
                            ((SummonContext) _localctx).tagText = tagText();

                            String baseTag = ((SummonContext) _localctx).tagText.txt;
                            MonsterBase base = new MonsterBase(baseTag);
                            mb.put(base);

                            setState(40);
                            match(EOL);
                        }
                    }
                    setState(46);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(52);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == RACEFLAG) {
                    {
                        setState(47);
                        match(RACEFLAG);
                        setState(48);
                        ((SummonContext) _localctx).word = word();
                        raceFlag = ((SummonContext) _localctx).word.wrd;
                        setState(50);
                        match(EOL);
                    }
                }

                setState(59);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == FALLBACKTAG) {
                    {
                        setState(54);
                        match(FALLBACKTAG);
                        setState(55);
                        ((SummonContext) _localctx).word = word();
                        fallBack = ((SummonContext) _localctx).word.wrd;
                        setState(57);
                        match(EOL);
                    }
                }

                setState(61);
                match(DESCTAG);
                setState(62);
                ((SummonContext) _localctx).tagText = tagText();
                desc = ((SummonContext) _localctx).tagText.txt;
            }
            _ctx.stop = _input.LT(-1);

            ((SummonContext) _localctx).smn = new Summon(nameTag, msgTag, unique, mb, raceFlag, fallBack, desc);

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
        public ArrayList<Summon> summons;
        public SummonContext summon;

        public List<SummonContext> summon() {
            return getRuleContexts(SummonContext.class);
        }

        public SummonContext summon(int i) {
            return getRuleContext(SummonContext.class, i);
        }

        public TerminalNode EOF() {
            return getToken(SummonFormatterParser.EOF, 0);
        }

        public List<TerminalNode> EOL() {
            return getTokens(SummonFormatterParser.EOL);
        }

        public TerminalNode EOL(int i) {
            return getToken(SummonFormatterParser.EOL, i);
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
            if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SummonFormatterListener) ((SummonFormatterListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SummonFormatterVisitor)
                return ((SummonFormatterVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_file);

        ((FileContext) _localctx).summons = new ArrayList<>();

        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(65);
                ((FileContext) _localctx).summon = summon();
                _localctx.summons.add(((FileContext) _localctx).summon.smn);
                setState(77);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 5, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(68);
                                _errHandler.sync(this);
                                _la = _input.LA(1);
                                do {
                                    {
                                        {
                                            setState(67);
                                            match(EOL);
                                        }
                                    }
                                    setState(70);
                                    _errHandler.sync(this);
                                    _la = _input.LA(1);
                                } while (_la == EOL);
                                setState(72);
                                ((FileContext) _localctx).summon = summon();
                                _localctx.summons.add(((FileContext) _localctx).summon.smn);
                            }
                        }
                    }
                    setState(79);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 5, _ctx);
                }
                setState(83);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == EOL) {
                    {
                        {
                            setState(80);
                            match(EOL);
                        }
                    }
                    setState(85);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(86);
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
            "\u0004\u0001\fY\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0003\u0001\u0013\b\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0005\u0003+\b\u0003\n\u0003\f\u0003.\t\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u00035\b" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003" +
                    "\u0003<\b\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0004\u0004E\b\u0004\u000b\u0004\f\u0004" +
                    "F\u0001\u0004\u0001\u0004\u0001\u0004\u0005\u0004L\b\u0004\n\u0004\f\u0004" +
                    "O\t\u0004\u0001\u0004\u0005\u0004R\b\u0004\n\u0004\f\u0004U\t\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0000\u0000\u0005\u0000\u0002\u0004\u0006" +
                    "\b\u0000\u0000Z\u0000\n\u0001\u0000\u0000\u0000\u0002\r\u0001\u0000\u0000" +
                    "\u0000\u0004\u0016\u0001\u0000\u0000\u0000\u0006\u0019\u0001\u0000\u0000" +
                    "\u0000\bA\u0001\u0000\u0000\u0000\n\u000b\u0005\u0002\u0000\u0000\u000b" +
                    "\f\u0006\u0000\uffff\uffff\u0000\f\u0001\u0001\u0000\u0000\u0000\r\u000e" +
                    "\u0005\u0002\u0000\u0000\u000e\u0012\u0006\u0001\uffff\uffff\u0000\u000f" +
                    "\u0010\u0005\u0003\u0000\u0000\u0010\u0011\u0005\u0002\u0000\u0000\u0011" +
                    "\u0013\u0006\u0001\uffff\uffff\u0000\u0012\u000f\u0001\u0000\u0000\u0000" +
                    "\u0012\u0013\u0001\u0000\u0000\u0000\u0013\u0014\u0001\u0000\u0000\u0000" +
                    "\u0014\u0015\u0006\u0001\uffff\uffff\u0000\u0015\u0003\u0001\u0000\u0000" +
                    "\u0000\u0016\u0017\u0005\f\u0000\u0000\u0017\u0018\u0006\u0002\uffff\uffff" +
                    "\u0000\u0018\u0005\u0001\u0000\u0000\u0000\u0019\u001a\u0005\u0005\u0000" +
                    "\u0000\u001a\u001b\u0003\u0000\u0000\u0000\u001b\u001c\u0006\u0003\uffff" +
                    "\uffff\u0000\u001c\u001d\u0005\u0004\u0000\u0000\u001d\u001e\u0005\u0006" +
                    "\u0000\u0000\u001e\u001f\u0003\u0000\u0000\u0000\u001f \u0006\u0003\uffff" +
                    "\uffff\u0000 !\u0005\u0004\u0000\u0000!\"\u0005\u0007\u0000\u0000\"#\u0003" +
                    "\u0004\u0002\u0000#$\u0006\u0003\uffff\uffff\u0000$,\u0005\u0004\u0000" +
                    "\u0000%&\u0005\b\u0000\u0000&\'\u0003\u0002\u0001\u0000\'(\u0006\u0003" +
                    "\uffff\uffff\u0000()\u0005\u0004\u0000\u0000)+\u0001\u0000\u0000\u0000" +
                    "*%\u0001\u0000\u0000\u0000+.\u0001\u0000\u0000\u0000,*\u0001\u0000\u0000" +
                    "\u0000,-\u0001\u0000\u0000\u0000-4\u0001\u0000\u0000\u0000.,\u0001\u0000" +
                    "\u0000\u0000/0\u0005\t\u0000\u000001\u0003\u0000\u0000\u000012\u0006\u0003" +
                    "\uffff\uffff\u000023\u0005\u0004\u0000\u000035\u0001\u0000\u0000\u0000" +
                    "4/\u0001\u0000\u0000\u000045\u0001\u0000\u0000\u00005;\u0001\u0000\u0000" +
                    "\u000067\u0005\n\u0000\u000078\u0003\u0000\u0000\u000089\u0006\u0003\uffff" +
                    "\uffff\u00009:\u0005\u0004\u0000\u0000:<\u0001\u0000\u0000\u0000;6\u0001" +
                    "\u0000\u0000\u0000;<\u0001\u0000\u0000\u0000<=\u0001\u0000\u0000\u0000" +
                    "=>\u0005\u000b\u0000\u0000>?\u0003\u0002\u0001\u0000?@\u0006\u0003\uffff" +
                    "\uffff\u0000@\u0007\u0001\u0000\u0000\u0000AB\u0003\u0006\u0003\u0000" +
                    "BM\u0006\u0004\uffff\uffff\u0000CE\u0005\u0004\u0000\u0000DC\u0001\u0000" +
                    "\u0000\u0000EF\u0001\u0000\u0000\u0000FD\u0001\u0000\u0000\u0000FG\u0001" +
                    "\u0000\u0000\u0000GH\u0001\u0000\u0000\u0000HI\u0003\u0006\u0003\u0000" +
                    "IJ\u0006\u0004\uffff\uffff\u0000JL\u0001\u0000\u0000\u0000KD\u0001\u0000" +
                    "\u0000\u0000LO\u0001\u0000\u0000\u0000MK\u0001\u0000\u0000\u0000MN\u0001" +
                    "\u0000\u0000\u0000NS\u0001\u0000\u0000\u0000OM\u0001\u0000\u0000\u0000" +
                    "PR\u0005\u0004\u0000\u0000QP\u0001\u0000\u0000\u0000RU\u0001\u0000\u0000" +
                    "\u0000SQ\u0001\u0000\u0000\u0000ST\u0001\u0000\u0000\u0000TV\u0001\u0000" +
                    "\u0000\u0000US\u0001\u0000\u0000\u0000VW\u0005\u0000\u0000\u0001W\t\u0001" +
                    "\u0000\u0000\u0000\u0007\u0012,4;FMS";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}