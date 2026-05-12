// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/UIEntryBase.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.uientrybase;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.frontend.entries.UIEntryBase;
import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
import uk.co.jackoftrades.frontend.screen.enums.CombinerName;
import uk.co.jackoftrades.middle.game.globals.GameConstants;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class UIEntryBaseParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, NAME = 3, RENDERER = 4, COMBINE = 5, CATEGORY = 6, FLAGS = 7,
            DESC = 8, UCASE = 9, TEXT = 10, LCASE = 11;
    public static final int
            RULE_name = 0, RULE_renderer = 1, RULE_combine = 2, RULE_category = 3,
            RULE_flags = 4, RULE_desc = 5, RULE_base = 6, RULE_bases = 7;

    private static String[] makeRuleNames() {
        return new String[]{
                "name", "renderer", "combine", "category", "flags", "desc", "base", "bases"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'name:'", "'renderer:'", "'combine:'", null, "'flags:'",
                "'desc:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "NAME", "RENDERER", "COMBINE", "CATEGORY", "FLAGS",
                "DESC", "UCASE", "TEXT", "LCASE"
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
        return "UIEntryBase.g4";
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

    public UIEntryBaseParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class NameContext extends ParserRuleContext {
        public String nameString;
        public Token LCASE;

        public TerminalNode NAME() {
            return getToken(UIEntryBaseParser.NAME, 0);
        }

        public TerminalNode LCASE() {
            return getToken(UIEntryBaseParser.LCASE, 0);
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
            if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryBaseVisitor)
                return ((UIEntryBaseVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(16);
                match(NAME);
                setState(17);
                ((NameContext) _localctx).LCASE = match(LCASE);

                ((NameContext) _localctx).nameString = ((NameContext) _localctx).LCASE.getText();

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
    public static class RendererContext extends ParserRuleContext {
        public UIEntryRenderer rend;
        public Token LCASE;

        public TerminalNode RENDERER() {
            return getToken(UIEntryBaseParser.RENDERER, 0);
        }

        public TerminalNode LCASE() {
            return getToken(UIEntryBaseParser.LCASE, 0);
        }

        public RendererContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_renderer;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).enterRenderer(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).exitRenderer(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryBaseVisitor)
                return ((UIEntryBaseVisitor<? extends T>) visitor).visitRenderer(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RendererContext renderer() throws RecognitionException {
        RendererContext _localctx = new RendererContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_renderer);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(20);
                match(RENDERER);
                setState(21);
                ((RendererContext) _localctx).LCASE = match(LCASE);

                ((RendererContext) _localctx).rend = GameConstants.getUIEntryRenderer(((RendererContext) _localctx).LCASE.getText());

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
    public static class CombineContext extends ParserRuleContext {
        public CombinerName combName;
        public Token UCASE;

        public TerminalNode COMBINE() {
            return getToken(UIEntryBaseParser.COMBINE, 0);
        }

        public TerminalNode UCASE() {
            return getToken(UIEntryBaseParser.UCASE, 0);
        }

        public CombineContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_combine;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).enterCombine(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).exitCombine(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryBaseVisitor)
                return ((UIEntryBaseVisitor<? extends T>) visitor).visitCombine(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CombineContext combine() throws RecognitionException {
        CombineContext _localctx = new CombineContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_combine);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(24);
                match(COMBINE);
                setState(25);
                ((CombineContext) _localctx).UCASE = match(UCASE);

                ((CombineContext) _localctx).combName = CombinerName.valueOf(((CombineContext) _localctx).UCASE.getText());

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
    public static class CategoryContext extends ParserRuleContext {
        public ArrayList<String> categories;
        public Token CATEGORY;

        public List<TerminalNode> CATEGORY() {
            return getTokens(UIEntryBaseParser.CATEGORY);
        }

        public TerminalNode CATEGORY(int i) {
            return getToken(UIEntryBaseParser.CATEGORY, i);
        }

        public CategoryContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_category;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).enterCategory(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).exitCategory(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryBaseVisitor)
                return ((UIEntryBaseVisitor<? extends T>) visitor).visitCategory(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CategoryContext category() throws RecognitionException {
        CategoryContext _localctx = new CategoryContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_category);

        ((CategoryContext) _localctx).categories = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(30);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(28);
                            ((CategoryContext) _localctx).CATEGORY = match(CATEGORY);

                            String fromFile = ((CategoryContext) _localctx).CATEGORY.getText();
                            String toSave = fromFile.substring(9);
                            _localctx.categories.add(toSave);

                        }
                    }
                    setState(32);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == CATEGORY);
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
        public String flag;
        public Token UCASE;

        public TerminalNode FLAGS() {
            return getToken(UIEntryBaseParser.FLAGS, 0);
        }

        public TerminalNode UCASE() {
            return getToken(UIEntryBaseParser.UCASE, 0);
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
            if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).enterFlags(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).exitFlags(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryBaseVisitor)
                return ((UIEntryBaseVisitor<? extends T>) visitor).visitFlags(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FlagsContext flags() throws RecognitionException {
        FlagsContext _localctx = new FlagsContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_flags);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(34);
                match(FLAGS);
                setState(35);
                ((FlagsContext) _localctx).UCASE = match(UCASE);

                ((FlagsContext) _localctx).flag = ((FlagsContext) _localctx).UCASE.getText();

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

        public List<TerminalNode> DESC() {
            return getTokens(UIEntryBaseParser.DESC);
        }

        public TerminalNode DESC(int i) {
            return getToken(UIEntryBaseParser.DESC, i);
        }

        public List<TerminalNode> TEXT() {
            return getTokens(UIEntryBaseParser.TEXT);
        }

        public TerminalNode TEXT(int i) {
            return getToken(UIEntryBaseParser.TEXT, i);
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
            if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).enterDesc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).exitDesc(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryBaseVisitor)
                return ((UIEntryBaseVisitor<? extends T>) visitor).visitDesc(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DescContext desc() throws RecognitionException {
        DescContext _localctx = new DescContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_desc);

        ((DescContext) _localctx).description = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(41);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(38);
                            match(DESC);
                            setState(39);
                            ((DescContext) _localctx).TEXT = match(TEXT);

                            ((DescContext) _localctx).description = _localctx.description + ((DescContext) _localctx).TEXT.getText();

                        }
                    }
                    setState(43);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == DESC);
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
        public UIEntryBase uiBase;
        public NameContext name;
        public RendererContext renderer;
        public CombineContext combine;
        public CategoryContext category;
        public FlagsContext flags;
        public DescContext desc;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public RendererContext renderer() {
            return getRuleContext(RendererContext.class, 0);
        }

        public CombineContext combine() {
            return getRuleContext(CombineContext.class, 0);
        }

        public CategoryContext category() {
            return getRuleContext(CategoryContext.class, 0);
        }

        public FlagsContext flags() {
            return getRuleContext(FlagsContext.class, 0);
        }

        public DescContext desc() {
            return getRuleContext(DescContext.class, 0);
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
            if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).enterBase(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).exitBase(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryBaseVisitor)
                return ((UIEntryBaseVisitor<? extends T>) visitor).visitBase(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BaseContext base() throws RecognitionException {
        BaseContext _localctx = new BaseContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_base);

        String n;
        UIEntryRenderer r;
        CombinerName co;
        ArrayList<String> ca;
        String f;
        String d;

        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(45);
                ((BaseContext) _localctx).name = name();

                n = ((BaseContext) _localctx).name.nameString;

                setState(47);
                ((BaseContext) _localctx).renderer = renderer();

                r = ((BaseContext) _localctx).renderer.rend;

                setState(49);
                ((BaseContext) _localctx).combine = combine();

                co = ((BaseContext) _localctx).combine.combName;

                setState(51);
                ((BaseContext) _localctx).category = category();

                ca = ((BaseContext) _localctx).category.categories;

                setState(53);
                ((BaseContext) _localctx).flags = flags();

                f = ((BaseContext) _localctx).flags.flag;

                setState(55);
                ((BaseContext) _localctx).desc = desc();

                d = ((BaseContext) _localctx).desc.description;

            }
            _ctx.stop = _input.LT(-1);

            ((BaseContext) _localctx).uiBase = new UIEntryBase(n, r, co, ca, f, d);

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
    public static class BasesContext extends ParserRuleContext {
        public ArrayList<UIEntryBase> allBases;
        public BaseContext base;

        public TerminalNode EOF() {
            return getToken(UIEntryBaseParser.EOF, 0);
        }

        public List<BaseContext> base() {
            return getRuleContexts(BaseContext.class);
        }

        public BaseContext base(int i) {
            return getRuleContext(BaseContext.class, i);
        }

        public BasesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_bases;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).enterBases(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryBaseListener) ((UIEntryBaseListener) listener).exitBases(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryBaseVisitor)
                return ((UIEntryBaseVisitor<? extends T>) visitor).visitBases(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BasesContext bases() throws RecognitionException {
        BasesContext _localctx = new BasesContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_bases);

        ((BasesContext) _localctx).allBases = new ArrayList<>();

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
                            ((BasesContext) _localctx).base = base();

                            _localctx.allBases.add(((BasesContext) _localctx).base.uiBase);

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
            "\u0004\u0001\u000bD\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0003\u0001\u0003\u0004\u0003\u001f\b\u0003\u000b\u0003\f\u0003 \u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0004\u0005*\b\u0005\u000b\u0005\f\u0005+\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0004\u0007>\b\u0007\u000b\u0007\f\u0007?\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0000\u0000\b\u0000\u0002\u0004\u0006\b" +
                    "\n\f\u000e\u0000\u0000>\u0000\u0010\u0001\u0000\u0000\u0000\u0002\u0014" +
                    "\u0001\u0000\u0000\u0000\u0004\u0018\u0001\u0000\u0000\u0000\u0006\u001e" +
                    "\u0001\u0000\u0000\u0000\b\"\u0001\u0000\u0000\u0000\n)\u0001\u0000\u0000" +
                    "\u0000\f-\u0001\u0000\u0000\u0000\u000e=\u0001\u0000\u0000\u0000\u0010" +
                    "\u0011\u0005\u0003\u0000\u0000\u0011\u0012\u0005\u000b\u0000\u0000\u0012" +
                    "\u0013\u0006\u0000\uffff\uffff\u0000\u0013\u0001\u0001\u0000\u0000\u0000" +
                    "\u0014\u0015\u0005\u0004\u0000\u0000\u0015\u0016\u0005\u000b\u0000\u0000" +
                    "\u0016\u0017\u0006\u0001\uffff\uffff\u0000\u0017\u0003\u0001\u0000\u0000" +
                    "\u0000\u0018\u0019\u0005\u0005\u0000\u0000\u0019\u001a\u0005\t\u0000\u0000" +
                    "\u001a\u001b\u0006\u0002\uffff\uffff\u0000\u001b\u0005\u0001\u0000\u0000" +
                    "\u0000\u001c\u001d\u0005\u0006\u0000\u0000\u001d\u001f\u0006\u0003\uffff" +
                    "\uffff\u0000\u001e\u001c\u0001\u0000\u0000\u0000\u001f \u0001\u0000\u0000" +
                    "\u0000 \u001e\u0001\u0000\u0000\u0000 !\u0001\u0000\u0000\u0000!\u0007" +
                    "\u0001\u0000\u0000\u0000\"#\u0005\u0007\u0000\u0000#$\u0005\t\u0000\u0000" +
                    "$%\u0006\u0004\uffff\uffff\u0000%\t\u0001\u0000\u0000\u0000&\'\u0005\b" +
                    "\u0000\u0000\'(\u0005\n\u0000\u0000(*\u0006\u0005\uffff\uffff\u0000)&" +
                    "\u0001\u0000\u0000\u0000*+\u0001\u0000\u0000\u0000+)\u0001\u0000\u0000" +
                    "\u0000+,\u0001\u0000\u0000\u0000,\u000b\u0001\u0000\u0000\u0000-.\u0003" +
                    "\u0000\u0000\u0000./\u0006\u0006\uffff\uffff\u0000/0\u0003\u0002\u0001" +
                    "\u000001\u0006\u0006\uffff\uffff\u000012\u0003\u0004\u0002\u000023\u0006" +
                    "\u0006\uffff\uffff\u000034\u0003\u0006\u0003\u000045\u0006\u0006\uffff" +
                    "\uffff\u000056\u0003\b\u0004\u000067\u0006\u0006\uffff\uffff\u000078\u0003" +
                    "\n\u0005\u000089\u0006\u0006\uffff\uffff\u00009\r\u0001\u0000\u0000\u0000" +
                    ":;\u0003\f\u0006\u0000;<\u0006\u0007\uffff\uffff\u0000<>\u0001\u0000\u0000" +
                    "\u0000=:\u0001\u0000\u0000\u0000>?\u0001\u0000\u0000\u0000?=\u0001\u0000" +
                    "\u0000\u0000?@\u0001\u0000\u0000\u0000@A\u0001\u0000\u0000\u0000AB\u0005" +
                    "\u0000\u0000\u0001B\u000f\u0001\u0000\u0000\u0000\u0003 +?";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}