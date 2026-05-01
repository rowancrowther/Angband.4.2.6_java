// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/background/io/parsers/antlr/grammars/ObjectBaseFormatter.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.objectbaseformatter;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.objects.ObjectBase;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ObjectBaseFormatterParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COLON = 1, OR = 2, COMMENT = 3, EOL = 4, NUMBER = 5, DEFAULTB = 6, DEFAULTM = 7, NAME = 8,
            GRAPHICS = 9, BREAK = 10, MAXSTACK = 11, HATES = 12, FLAGS = 13, SPACE = 14, LCASE = 15,
            UCASE = 16, MCASE = 17;
    public static final int
            RULE_default = 0, RULE_name = 1, RULE_graphics = 2, RULE_break = 3, RULE_maxstack = 4,
            RULE_flag = 5, RULE_flags = 6, RULE_base = 7, RULE_file = 8;

    private static String[] makeRuleNames() {
        return new String[]{
                "default", "name", "graphics", "break", "maxstack", "flag", "flags",
                "base", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "':'", "' | '", null, null, null, "'default:break-chance'", "'default:max-stack'",
                "'name'", "'graphics'", "'break'", "'max-stack'", "'HATES_'", "'flags'",
                "' '"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COLON", "OR", "COMMENT", "EOL", "NUMBER", "DEFAULTB", "DEFAULTM",
                "NAME", "GRAPHICS", "BREAK", "MAXSTACK", "HATES", "FLAGS", "SPACE", "LCASE",
                "UCASE", "MCASE"
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
        return "ObjectBaseFormatter.g4";
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


    ArrayList<ObjectBase> bases = new ArrayList<>();

    public ObjectBaseFormatterParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class DefaultContext extends ParserRuleContext {
        public String tag;
        public int value;
        public Token NUMBER;

        public TerminalNode DEFAULTB() {
            return getToken(ObjectBaseFormatterParser.DEFAULTB, 0);
        }

        public TerminalNode COLON() {
            return getToken(ObjectBaseFormatterParser.COLON, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(ObjectBaseFormatterParser.NUMBER, 0);
        }

        public TerminalNode DEFAULTM() {
            return getToken(ObjectBaseFormatterParser.DEFAULTM, 0);
        }

        public DefaultContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_default;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseFormatterListener)
                ((ObjectBaseFormatterListener) listener).enterDefault(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseFormatterListener)
                ((ObjectBaseFormatterListener) listener).exitDefault(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectBaseFormatterVisitor)
                return ((ObjectBaseFormatterVisitor<? extends T>) visitor).visitDefault(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DefaultContext default_() throws RecognitionException {
        DefaultContext _localctx = new DefaultContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_default);
        try {
            setState(26);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case DEFAULTB:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(18);
                    match(DEFAULTB);
                    setState(19);
                    match(COLON);
                    setState(20);
                    ((DefaultContext) _localctx).NUMBER = match(NUMBER);
                    ((DefaultContext) _localctx).tag = "break-chance";
                    ((DefaultContext) _localctx).value = Integer.parseInt(((DefaultContext) _localctx).NUMBER.getText());
                }
                break;
                case DEFAULTM:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(22);
                    match(DEFAULTM);
                    setState(23);
                    match(COLON);
                    setState(24);
                    ((DefaultContext) _localctx).NUMBER = match(NUMBER);
                    ((DefaultContext) _localctx).tag = "max-stack";
                    ((DefaultContext) _localctx).value = Integer.parseInt(((DefaultContext) _localctx).NUMBER.getText());
                }
                break;
                default:
                    throw new NoViableAltException(this);
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
        public String tValue;
        public String nameString;
        public Token LCASE;
        public Token MCASE;

        public TerminalNode NAME() {
            return getToken(ObjectBaseFormatterParser.NAME, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(ObjectBaseFormatterParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(ObjectBaseFormatterParser.COLON, i);
        }

        public TerminalNode LCASE() {
            return getToken(ObjectBaseFormatterParser.LCASE, 0);
        }

        public TerminalNode MCASE() {
            return getToken(ObjectBaseFormatterParser.MCASE, 0);
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
            if (listener instanceof ObjectBaseFormatterListener)
                ((ObjectBaseFormatterListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseFormatterListener)
                ((ObjectBaseFormatterListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectBaseFormatterVisitor)
                return ((ObjectBaseFormatterVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_name);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(28);
                match(NAME);
                setState(29);
                match(COLON);
                setState(30);
                ((NameContext) _localctx).LCASE = match(LCASE);
                ((NameContext) _localctx).tValue = ((NameContext) _localctx).LCASE.getText();
                setState(35);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COLON) {
                    {
                        setState(32);
                        match(COLON);
                        setState(33);
                        ((NameContext) _localctx).MCASE = match(MCASE);
                        ((NameContext) _localctx).nameString = ((NameContext) _localctx).MCASE.getText();
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
    public static class GraphicsContext extends ParserRuleContext {
        public ColourType attr;
        public Token LCASE;

        public TerminalNode GRAPHICS() {
            return getToken(ObjectBaseFormatterParser.GRAPHICS, 0);
        }

        public TerminalNode COLON() {
            return getToken(ObjectBaseFormatterParser.COLON, 0);
        }

        public TerminalNode LCASE() {
            return getToken(ObjectBaseFormatterParser.LCASE, 0);
        }

        public GraphicsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_graphics;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseFormatterListener)
                ((ObjectBaseFormatterListener) listener).enterGraphics(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseFormatterListener)
                ((ObjectBaseFormatterListener) listener).exitGraphics(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectBaseFormatterVisitor)
                return ((ObjectBaseFormatterVisitor<? extends T>) visitor).visitGraphics(this);
            else return visitor.visitChildren(this);
        }
    }

    public final GraphicsContext graphics() throws RecognitionException {
        GraphicsContext _localctx = new GraphicsContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_graphics);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(37);
                match(GRAPHICS);
                setState(38);
                match(COLON);
                setState(39);
                ((GraphicsContext) _localctx).LCASE = match(LCASE);
                ((GraphicsContext) _localctx).attr = ColourType.getColourType(((GraphicsContext) _localctx).LCASE.getText());
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
    public static class BreakContext extends ParserRuleContext {
        public int breakChance;
        public Token NUMBER;

        public TerminalNode BREAK() {
            return getToken(ObjectBaseFormatterParser.BREAK, 0);
        }

        public TerminalNode COLON() {
            return getToken(ObjectBaseFormatterParser.COLON, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(ObjectBaseFormatterParser.NUMBER, 0);
        }

        public BreakContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_break;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseFormatterListener)
                ((ObjectBaseFormatterListener) listener).enterBreak(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseFormatterListener)
                ((ObjectBaseFormatterListener) listener).exitBreak(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectBaseFormatterVisitor)
                return ((ObjectBaseFormatterVisitor<? extends T>) visitor).visitBreak(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BreakContext break_() throws RecognitionException {
        BreakContext _localctx = new BreakContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_break);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(42);
                match(BREAK);
                setState(43);
                match(COLON);
                setState(44);
                ((BreakContext) _localctx).NUMBER = match(NUMBER);
                ((BreakContext) _localctx).breakChance = Integer.parseInt(((BreakContext) _localctx).NUMBER.getText());
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
    public static class MaxstackContext extends ParserRuleContext {
        public int maxStack;
        public Token NUMBER;

        public TerminalNode MAXSTACK() {
            return getToken(ObjectBaseFormatterParser.MAXSTACK, 0);
        }

        public TerminalNode COLON() {
            return getToken(ObjectBaseFormatterParser.COLON, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(ObjectBaseFormatterParser.NUMBER, 0);
        }

        public MaxstackContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_maxstack;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseFormatterListener)
                ((ObjectBaseFormatterListener) listener).enterMaxstack(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseFormatterListener)
                ((ObjectBaseFormatterListener) listener).exitMaxstack(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectBaseFormatterVisitor)
                return ((ObjectBaseFormatterVisitor<? extends T>) visitor).visitMaxstack(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MaxstackContext maxstack() throws RecognitionException {
        MaxstackContext _localctx = new MaxstackContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_maxstack);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(47);
                match(MAXSTACK);
                setState(48);
                match(COLON);
                setState(49);
                ((MaxstackContext) _localctx).NUMBER = match(NUMBER);
                ((MaxstackContext) _localctx).maxStack = Integer.parseInt(((MaxstackContext) _localctx).NUMBER.getText());
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
    public static class FlagContext extends ParserRuleContext {
        public String flagString;
        public Token kname;

        public TerminalNode UCASE() {
            return getToken(ObjectBaseFormatterParser.UCASE, 0);
        }

        public FlagContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_flag;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseFormatterListener)
                ((ObjectBaseFormatterListener) listener).enterFlag(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseFormatterListener)
                ((ObjectBaseFormatterListener) listener).exitFlag(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectBaseFormatterVisitor)
                return ((ObjectBaseFormatterVisitor<? extends T>) visitor).visitFlag(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FlagContext flag() throws RecognitionException {
        FlagContext _localctx = new FlagContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_flag);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(52);
                ((FlagContext) _localctx).kname = match(UCASE);

                ((FlagContext) _localctx).flagString = ((FlagContext) _localctx).kname.getText();

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
        public ArrayList<String> flagStrings;
        public FlagContext f1;
        public FlagContext f2;

        public TerminalNode FLAGS() {
            return getToken(ObjectBaseFormatterParser.FLAGS, 0);
        }

        public TerminalNode COLON() {
            return getToken(ObjectBaseFormatterParser.COLON, 0);
        }

        public List<FlagContext> flag() {
            return getRuleContexts(FlagContext.class);
        }

        public FlagContext flag(int i) {
            return getRuleContext(FlagContext.class, i);
        }

        public List<TerminalNode> OR() {
            return getTokens(ObjectBaseFormatterParser.OR);
        }

        public TerminalNode OR(int i) {
            return getToken(ObjectBaseFormatterParser.OR, i);
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
            if (listener instanceof ObjectBaseFormatterListener)
                ((ObjectBaseFormatterListener) listener).enterFlags(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseFormatterListener)
                ((ObjectBaseFormatterListener) listener).exitFlags(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectBaseFormatterVisitor)
                return ((ObjectBaseFormatterVisitor<? extends T>) visitor).visitFlags(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FlagsContext flags() throws RecognitionException {
        FlagsContext _localctx = new FlagsContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_flags);

        ((FlagsContext) _localctx).flagStrings = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(55);
                match(FLAGS);
                setState(56);
                match(COLON);
                setState(57);
                ((FlagsContext) _localctx).f1 = flag();

                _localctx.flagStrings.add(((FlagsContext) _localctx).f1.flagString);

                setState(65);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == OR) {
                    {
                        {
                            setState(59);
                            match(OR);
                            setState(60);
                            ((FlagsContext) _localctx).f2 = flag();

                            _localctx.flagStrings.add(((FlagsContext) _localctx).f2.flagString);

                        }
                    }
                    setState(67);
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
        public ObjectBase objectBase;
        public NameContext name;
        public GraphicsContext graphics;
        public BreakContext bc;
        public MaxstackContext maxstack;
        public FlagsContext flags;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public GraphicsContext graphics() {
            return getRuleContext(GraphicsContext.class, 0);
        }

        public MaxstackContext maxstack() {
            return getRuleContext(MaxstackContext.class, 0);
        }

        public List<FlagsContext> flags() {
            return getRuleContexts(FlagsContext.class);
        }

        public FlagsContext flags(int i) {
            return getRuleContext(FlagsContext.class, i);
        }

        public BreakContext break_() {
            return getRuleContext(BreakContext.class, 0);
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
            if (listener instanceof ObjectBaseFormatterListener)
                ((ObjectBaseFormatterListener) listener).enterBase(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseFormatterListener)
                ((ObjectBaseFormatterListener) listener).exitBase(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectBaseFormatterVisitor)
                return ((ObjectBaseFormatterVisitor<? extends T>) visitor).visitBase(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BaseContext base() throws RecognitionException {
        BaseContext _localctx = new BaseContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_base);

        ((BaseContext) _localctx).objectBase = new ObjectBase();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(68);
                ((BaseContext) _localctx).name = name();

                TValue tVal = TValue.fromName(((BaseContext) _localctx).name.tValue);
                _localctx.objectBase.settVal(tVal);
                if (null != ((BaseContext) _localctx).name.nameString)
                    _localctx.objectBase.setName(((BaseContext) _localctx).name.nameString);

                setState(70);
                ((BaseContext) _localctx).graphics = graphics();

                _localctx.objectBase.setAttr(((BaseContext) _localctx).graphics.attr);

                setState(75);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == BREAK) {
                    {
                        setState(72);
                        ((BaseContext) _localctx).bc = break_();

                        _localctx.objectBase.setBreakPerc(((BaseContext) _localctx).bc.breakChance);

                    }
                }

                setState(80);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == MAXSTACK) {
                    {
                        setState(77);
                        ((BaseContext) _localctx).maxstack = maxstack();

                        _localctx.objectBase.setMaxStack(((BaseContext) _localctx).maxstack.maxStack);

                    }
                }

                setState(87);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == FLAGS) {
                    {
                        {
                            setState(82);
                            ((BaseContext) _localctx).flags = flags();

                            _localctx.objectBase.setFlags(((BaseContext) _localctx).flags.flagStrings);

                        }
                    }
                    setState(89);
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
    public static class FileContext extends ParserRuleContext {
        public ArrayList<ObjectBase> bases;
        public int defaultBreakChance;
        public int defaultMaxStack;
        public DefaultContext def;
        public BaseContext bs;

        public List<DefaultContext> default_() {
            return getRuleContexts(DefaultContext.class);
        }

        public DefaultContext default_(int i) {
            return getRuleContext(DefaultContext.class, i);
        }

        public List<BaseContext> base() {
            return getRuleContexts(BaseContext.class);
        }

        public BaseContext base(int i) {
            return getRuleContext(BaseContext.class, i);
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
            if (listener instanceof ObjectBaseFormatterListener)
                ((ObjectBaseFormatterListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseFormatterListener)
                ((ObjectBaseFormatterListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectBaseFormatterVisitor)
                return ((ObjectBaseFormatterVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_file);

        ((FileContext) _localctx).bases = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(93);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(90);
                            ((FileContext) _localctx).def = default_();

                            if (((FileContext) _localctx).def.tag.equals("break-chance"))
                                ((FileContext) _localctx).defaultBreakChance = ((FileContext) _localctx).def.value;
                            else
                                ((FileContext) _localctx).defaultMaxStack = ((FileContext) _localctx).def.value;

                        }
                    }
                    setState(95);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == DEFAULTB || _la == DEFAULTM);
                setState(100);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(97);
                            ((FileContext) _localctx).bs = base();

                            _localctx.bases.add(((FileContext) _localctx).bs.objectBase);

                        }
                    }
                    setState(102);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
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
            "\u0004\u0001\u0011i\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002" +
                    "\b\u0007\b\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0000\u0001\u0000\u0001\u0000\u0003\u0000\u001b\b\u0000\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0003\u0001$\b\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0005\u0006@\b\u0006" +
                    "\n\u0006\f\u0006C\t\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007L\b\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0003\u0007Q\b\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0005\u0007V\b\u0007\n\u0007\f\u0007Y\t\u0007\u0001\b\u0001" +
                    "\b\u0001\b\u0004\b^\b\b\u000b\b\f\b_\u0001\b\u0001\b\u0001\b\u0004\be" +
                    "\b\b\u000b\b\f\bf\u0001\b\u0000\u0000\t\u0000\u0002\u0004\u0006\b\n\f" +
                    "\u000e\u0010\u0000\u0000g\u0000\u001a\u0001\u0000\u0000\u0000\u0002\u001c" +
                    "\u0001\u0000\u0000\u0000\u0004%\u0001\u0000\u0000\u0000\u0006*\u0001\u0000" +
                    "\u0000\u0000\b/\u0001\u0000\u0000\u0000\n4\u0001\u0000\u0000\u0000\f7" +
                    "\u0001\u0000\u0000\u0000\u000eD\u0001\u0000\u0000\u0000\u0010]\u0001\u0000" +
                    "\u0000\u0000\u0012\u0013\u0005\u0006\u0000\u0000\u0013\u0014\u0005\u0001" +
                    "\u0000\u0000\u0014\u0015\u0005\u0005\u0000\u0000\u0015\u001b\u0006\u0000" +
                    "\uffff\uffff\u0000\u0016\u0017\u0005\u0007\u0000\u0000\u0017\u0018\u0005" +
                    "\u0001\u0000\u0000\u0018\u0019\u0005\u0005\u0000\u0000\u0019\u001b\u0006" +
                    "\u0000\uffff\uffff\u0000\u001a\u0012\u0001\u0000\u0000\u0000\u001a\u0016" +
                    "\u0001\u0000\u0000\u0000\u001b\u0001\u0001\u0000\u0000\u0000\u001c\u001d" +
                    "\u0005\b\u0000\u0000\u001d\u001e\u0005\u0001\u0000\u0000\u001e\u001f\u0005" +
                    "\u000f\u0000\u0000\u001f#\u0006\u0001\uffff\uffff\u0000 !\u0005\u0001" +
                    "\u0000\u0000!\"\u0005\u0011\u0000\u0000\"$\u0006\u0001\uffff\uffff\u0000" +
                    "# \u0001\u0000\u0000\u0000#$\u0001\u0000\u0000\u0000$\u0003\u0001\u0000" +
                    "\u0000\u0000%&\u0005\t\u0000\u0000&\'\u0005\u0001\u0000\u0000\'(\u0005" +
                    "\u000f\u0000\u0000()\u0006\u0002\uffff\uffff\u0000)\u0005\u0001\u0000" +
                    "\u0000\u0000*+\u0005\n\u0000\u0000+,\u0005\u0001\u0000\u0000,-\u0005\u0005" +
                    "\u0000\u0000-.\u0006\u0003\uffff\uffff\u0000.\u0007\u0001\u0000\u0000" +
                    "\u0000/0\u0005\u000b\u0000\u000001\u0005\u0001\u0000\u000012\u0005\u0005" +
                    "\u0000\u000023\u0006\u0004\uffff\uffff\u00003\t\u0001\u0000\u0000\u0000" +
                    "45\u0005\u0010\u0000\u000056\u0006\u0005\uffff\uffff\u00006\u000b\u0001" +
                    "\u0000\u0000\u000078\u0005\r\u0000\u000089\u0005\u0001\u0000\u00009:\u0003" +
                    "\n\u0005\u0000:A\u0006\u0006\uffff\uffff\u0000;<\u0005\u0002\u0000\u0000" +
                    "<=\u0003\n\u0005\u0000=>\u0006\u0006\uffff\uffff\u0000>@\u0001\u0000\u0000" +
                    "\u0000?;\u0001\u0000\u0000\u0000@C\u0001\u0000\u0000\u0000A?\u0001\u0000" +
                    "\u0000\u0000AB\u0001\u0000\u0000\u0000B\r\u0001\u0000\u0000\u0000CA\u0001" +
                    "\u0000\u0000\u0000DE\u0003\u0002\u0001\u0000EF\u0006\u0007\uffff\uffff" +
                    "\u0000FG\u0003\u0004\u0002\u0000GK\u0006\u0007\uffff\uffff\u0000HI\u0003" +
                    "\u0006\u0003\u0000IJ\u0006\u0007\uffff\uffff\u0000JL\u0001\u0000\u0000" +
                    "\u0000KH\u0001\u0000\u0000\u0000KL\u0001\u0000\u0000\u0000LP\u0001\u0000" +
                    "\u0000\u0000MN\u0003\b\u0004\u0000NO\u0006\u0007\uffff\uffff\u0000OQ\u0001" +
                    "\u0000\u0000\u0000PM\u0001\u0000\u0000\u0000PQ\u0001\u0000\u0000\u0000" +
                    "QW\u0001\u0000\u0000\u0000RS\u0003\f\u0006\u0000ST\u0006\u0007\uffff\uffff" +
                    "\u0000TV\u0001\u0000\u0000\u0000UR\u0001\u0000\u0000\u0000VY\u0001\u0000" +
                    "\u0000\u0000WU\u0001\u0000\u0000\u0000WX\u0001\u0000\u0000\u0000X\u000f" +
                    "\u0001\u0000\u0000\u0000YW\u0001\u0000\u0000\u0000Z[\u0003\u0000\u0000" +
                    "\u0000[\\\u0006\b\uffff\uffff\u0000\\^\u0001\u0000\u0000\u0000]Z\u0001" +
                    "\u0000\u0000\u0000^_\u0001\u0000\u0000\u0000_]\u0001\u0000\u0000\u0000" +
                    "_`\u0001\u0000\u0000\u0000`d\u0001\u0000\u0000\u0000ab\u0003\u000e\u0007" +
                    "\u0000bc\u0006\b\uffff\uffff\u0000ce\u0001\u0000\u0000\u0000da\u0001\u0000" +
                    "\u0000\u0000ef\u0001\u0000\u0000\u0000fd\u0001\u0000\u0000\u0000fg\u0001" +
                    "\u0000\u0000\u0000g\u0011\u0001\u0000\u0000\u0000\b\u001a#AKPW_f";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}