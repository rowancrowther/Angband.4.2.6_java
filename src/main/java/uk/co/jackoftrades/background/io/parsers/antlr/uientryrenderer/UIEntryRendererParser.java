// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/background/io/parsers/antlr/grammars/UIEntryRendererEnum.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.background.io.parsers.antlr.uientryrenderer;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.frontend.screen.UIEntryRenderer;
import uk.co.jackoftrades.frontend.screen.enums.UIEntryEnum;
import uk.co.jackoftrades.frontend.screen.enums.UIEntryRendererEnum;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class UIEntryRendererParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, NAME = 3, CODE = 4, COLOURS = 5, LABELCOLOURS = 6, SYMBOLS = 7,
            NDIGIT = 8, SIGN = 9, COLOURCHAR = 10, SYMBOLCHAR = 11, NUMBER = 12, LCASE = 13, UCASE = 14;
    public static final int
            RULE_name = 0, RULE_code = 1, RULE_colours = 2, RULE_labelColours = 3,
            RULE_symbols = 4, RULE_nDigit = 5, RULE_sign = 6, RULE_entry = 7, RULE_entries = 8;

    private static String[] makeRuleNames() {
        return new String[]{
                "name", "code", "colours", "labelColours", "symbols", "nDigit", "sign",
                "entry", "entries"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'name:'", "'code:'", "'colors:'", "'labelcolors:'",
                "'symbols:'", "'ndigit:'", "'sign:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "NAME", "CODE", "COLOURS", "LABELCOLOURS", "SYMBOLS",
                "NDIGIT", "SIGN", "COLOURCHAR", "SYMBOLCHAR", "NUMBER", "LCASE", "UCASE"
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
        return "UIEntryRendererEnum.g4";
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

    public UIEntryRendererParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class NameContext extends ParserRuleContext {
        public String uiName;
        public Token LCASE;

        public TerminalNode NAME() {
            return getToken(UIEntryRendererParser.NAME, 0);
        }

        public TerminalNode LCASE() {
            return getToken(UIEntryRendererParser.LCASE, 0);
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
            if (listener instanceof UIEntryRendererListener) ((UIEntryRendererListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererListener) ((UIEntryRendererListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryRendererVisitor)
                return ((UIEntryRendererVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(18);
                match(NAME);
                setState(19);
                ((NameContext) _localctx).LCASE = match(LCASE);

                ((NameContext) _localctx).uiName = ((NameContext) _localctx).LCASE.getText();

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
    public static class CodeContext extends ParserRuleContext {
        public UIEntryRendererEnum flag;
        public Token UCASE;

        public TerminalNode CODE() {
            return getToken(UIEntryRendererParser.CODE, 0);
        }

        public TerminalNode UCASE() {
            return getToken(UIEntryRendererParser.UCASE, 0);
        }

        public CodeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_code;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererListener) ((UIEntryRendererListener) listener).enterCode(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererListener) ((UIEntryRendererListener) listener).exitCode(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryRendererVisitor)
                return ((UIEntryRendererVisitor<? extends T>) visitor).visitCode(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CodeContext code() throws RecognitionException {
        CodeContext _localctx = new CodeContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_code);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(22);
                match(CODE);
                setState(23);
                ((CodeContext) _localctx).UCASE = match(UCASE);

                String flagString = "UI_ENTRY_RENDERER_" + ((CodeContext) _localctx).UCASE.getText();
                ((CodeContext) _localctx).flag = UIEntryRendererEnum.valueOf(flagString);

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
    public static class ColoursContext extends ParserRuleContext {
        public String colourString;
        public Token COLOURCHAR;

        public TerminalNode COLOURS() {
            return getToken(UIEntryRendererParser.COLOURS, 0);
        }

        public TerminalNode COLOURCHAR() {
            return getToken(UIEntryRendererParser.COLOURCHAR, 0);
        }

        public ColoursContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_colours;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererListener) ((UIEntryRendererListener) listener).enterColours(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererListener) ((UIEntryRendererListener) listener).exitColours(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryRendererVisitor)
                return ((UIEntryRendererVisitor<? extends T>) visitor).visitColours(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ColoursContext colours() throws RecognitionException {
        ColoursContext _localctx = new ColoursContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_colours);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(26);
                match(COLOURS);
                setState(27);
                ((ColoursContext) _localctx).COLOURCHAR = match(COLOURCHAR);

                ((ColoursContext) _localctx).colourString = ((ColoursContext) _localctx).COLOURCHAR.getText();

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
    public static class LabelColoursContext extends ParserRuleContext {
        public String labelColourString;
        public Token COLOURCHAR;

        public TerminalNode LABELCOLOURS() {
            return getToken(UIEntryRendererParser.LABELCOLOURS, 0);
        }

        public TerminalNode COLOURCHAR() {
            return getToken(UIEntryRendererParser.COLOURCHAR, 0);
        }

        public LabelColoursContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_labelColours;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererListener)
                ((UIEntryRendererListener) listener).enterLabelColours(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererListener)
                ((UIEntryRendererListener) listener).exitLabelColours(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryRendererVisitor)
                return ((UIEntryRendererVisitor<? extends T>) visitor).visitLabelColours(this);
            else return visitor.visitChildren(this);
        }
    }

    public final LabelColoursContext labelColours() throws RecognitionException {
        LabelColoursContext _localctx = new LabelColoursContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_labelColours);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(30);
                match(LABELCOLOURS);
                setState(31);
                ((LabelColoursContext) _localctx).COLOURCHAR = match(COLOURCHAR);

                ((LabelColoursContext) _localctx).labelColourString = ((LabelColoursContext) _localctx).COLOURCHAR.getText();

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
    public static class SymbolsContext extends ParserRuleContext {
        public String symbolString;
        public Token SYMBOLCHAR;

        public TerminalNode SYMBOLS() {
            return getToken(UIEntryRendererParser.SYMBOLS, 0);
        }

        public TerminalNode SYMBOLCHAR() {
            return getToken(UIEntryRendererParser.SYMBOLCHAR, 0);
        }

        public SymbolsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_symbols;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererListener) ((UIEntryRendererListener) listener).enterSymbols(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererListener) ((UIEntryRendererListener) listener).exitSymbols(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryRendererVisitor)
                return ((UIEntryRendererVisitor<? extends T>) visitor).visitSymbols(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SymbolsContext symbols() throws RecognitionException {
        SymbolsContext _localctx = new SymbolsContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_symbols);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(34);
                match(SYMBOLS);
                setState(35);
                ((SymbolsContext) _localctx).SYMBOLCHAR = match(SYMBOLCHAR);

                ((SymbolsContext) _localctx).symbolString = ((SymbolsContext) _localctx).SYMBOLCHAR.getText();

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
    public static class NDigitContext extends ParserRuleContext {
        public int numDigits;
        public Token NUMBER;

        public TerminalNode NDIGIT() {
            return getToken(UIEntryRendererParser.NDIGIT, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(UIEntryRendererParser.NUMBER, 0);
        }

        public NDigitContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_nDigit;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererListener) ((UIEntryRendererListener) listener).enterNDigit(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererListener) ((UIEntryRendererListener) listener).exitNDigit(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryRendererVisitor)
                return ((UIEntryRendererVisitor<? extends T>) visitor).visitNDigit(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NDigitContext nDigit() throws RecognitionException {
        NDigitContext _localctx = new NDigitContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_nDigit);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(38);
                match(NDIGIT);
                setState(39);
                ((NDigitContext) _localctx).NUMBER = match(NUMBER);

                ((NDigitContext) _localctx).numDigits = Integer.parseInt(((NDigitContext) _localctx).NUMBER.getText());

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
    public static class SignContext extends ParserRuleContext {
        public UIEntryEnum entryEnum;
        public Token UCASE;

        public TerminalNode SIGN() {
            return getToken(UIEntryRendererParser.SIGN, 0);
        }

        public TerminalNode UCASE() {
            return getToken(UIEntryRendererParser.UCASE, 0);
        }

        public SignContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_sign;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererListener) ((UIEntryRendererListener) listener).enterSign(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererListener) ((UIEntryRendererListener) listener).exitSign(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryRendererVisitor)
                return ((UIEntryRendererVisitor<? extends T>) visitor).visitSign(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SignContext sign() throws RecognitionException {
        SignContext _localctx = new SignContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_sign);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(42);
                match(SIGN);
                setState(43);
                ((SignContext) _localctx).UCASE = match(UCASE);

                ((SignContext) _localctx).entryEnum = UIEntryEnum.valueOf("UI_ENTRY_" + ((SignContext) _localctx).UCASE.getText());

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
        public UIEntryRenderer uiEntryRenderer;
        public NameContext name;
        public CodeContext code;
        public ColoursContext colours;
        public LabelColoursContext labelColours;
        public SymbolsContext symbols;
        public NDigitContext nDigit;
        public SignContext sign;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public CodeContext code() {
            return getRuleContext(CodeContext.class, 0);
        }

        public ColoursContext colours() {
            return getRuleContext(ColoursContext.class, 0);
        }

        public LabelColoursContext labelColours() {
            return getRuleContext(LabelColoursContext.class, 0);
        }

        public SymbolsContext symbols() {
            return getRuleContext(SymbolsContext.class, 0);
        }

        public NDigitContext nDigit() {
            return getRuleContext(NDigitContext.class, 0);
        }

        public SignContext sign() {
            return getRuleContext(SignContext.class, 0);
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
            if (listener instanceof UIEntryRendererListener) ((UIEntryRendererListener) listener).enterEntry(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererListener) ((UIEntryRendererListener) listener).exitEntry(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryRendererVisitor)
                return ((UIEntryRendererVisitor<? extends T>) visitor).visitEntry(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EntryContext entry() throws RecognitionException {
        EntryContext _localctx = new EntryContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_entry);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(46);
                ((EntryContext) _localctx).name = name();
                setState(47);
                ((EntryContext) _localctx).code = code();
                setState(48);
                ((EntryContext) _localctx).colours = colours();
                setState(49);
                ((EntryContext) _localctx).labelColours = labelColours();
                setState(50);
                ((EntryContext) _localctx).symbols = symbols();
                setState(52);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == NDIGIT) {
                    {
                        setState(51);
                        ((EntryContext) _localctx).nDigit = nDigit();
                    }
                }

                setState(55);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == SIGN) {
                    {
                        setState(54);
                        ((EntryContext) _localctx).sign = sign();
                    }
                }


                String initName = ((EntryContext) _localctx).name.uiName;
                UIEntryRendererEnum initCode = ((EntryContext) _localctx).code.flag;
                String initColour = ((EntryContext) _localctx).colours.colourString;
                String initLColour = ((EntryContext) _localctx).labelColours.labelColourString;
                String initSymbols = ((EntryContext) _localctx).symbols.symbolString;
                int initNumDigits;
                if (((EntryContext) _localctx).nDigit == null)
                    initNumDigits = 0;
                else
                    initNumDigits = ((EntryContext) _localctx).nDigit.numDigits;
                UIEntryEnum initSign;
                if (((EntryContext) _localctx).sign == null)
                    initSign = UIEntryEnum.UI_ENTRY_SIGN_DEFAULT;
                else
                    initSign = ((EntryContext) _localctx).sign.entryEnum;

                ((EntryContext) _localctx).uiEntryRenderer = new UIEntryRenderer(initName,
                        initCode,
                        initColour,
                        initLColour,
                        initSymbols,
                        initNumDigits,
                        initSign);

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
    public static class EntriesContext extends ParserRuleContext {
        public ArrayList<UIEntryRenderer> allEntries;
        public EntryContext entry;

        public TerminalNode EOF() {
            return getToken(UIEntryRendererParser.EOF, 0);
        }

        public List<EntryContext> entry() {
            return getRuleContexts(EntryContext.class);
        }

        public EntryContext entry(int i) {
            return getRuleContext(EntryContext.class, i);
        }

        public EntriesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_entries;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererListener) ((UIEntryRendererListener) listener).enterEntries(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryRendererListener) ((UIEntryRendererListener) listener).exitEntries(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryRendererVisitor)
                return ((UIEntryRendererVisitor<? extends T>) visitor).visitEntries(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EntriesContext entries() throws RecognitionException {
        EntriesContext _localctx = new EntriesContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_entries);

        ((EntriesContext) _localctx).allEntries = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(62);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(59);
                            ((EntriesContext) _localctx).entry = entry();

                            _localctx.allEntries.add(((EntriesContext) _localctx).entry.uiEntryRenderer);

                        }
                    }
                    setState(64);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(66);
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
            "\u0004\u0001\u000eE\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002" +
                    "\b\u0007\b\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007" +
                    "5\b\u0007\u0001\u0007\u0003\u00078\b\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\b\u0001\b\u0001\b\u0004\b?\b\b\u000b\b\f\b@\u0001\b\u0001\b\u0001\b\u0000" +
                    "\u0000\t\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0000\u0000>\u0000" +
                    "\u0012\u0001\u0000\u0000\u0000\u0002\u0016\u0001\u0000\u0000\u0000\u0004" +
                    "\u001a\u0001\u0000\u0000\u0000\u0006\u001e\u0001\u0000\u0000\u0000\b\"" +
                    "\u0001\u0000\u0000\u0000\n&\u0001\u0000\u0000\u0000\f*\u0001\u0000\u0000" +
                    "\u0000\u000e.\u0001\u0000\u0000\u0000\u0010>\u0001\u0000\u0000\u0000\u0012" +
                    "\u0013\u0005\u0003\u0000\u0000\u0013\u0014\u0005\r\u0000\u0000\u0014\u0015" +
                    "\u0006\u0000\uffff\uffff\u0000\u0015\u0001\u0001\u0000\u0000\u0000\u0016" +
                    "\u0017\u0005\u0004\u0000\u0000\u0017\u0018\u0005\u000e\u0000\u0000\u0018" +
                    "\u0019\u0006\u0001\uffff\uffff\u0000\u0019\u0003\u0001\u0000\u0000\u0000" +
                    "\u001a\u001b\u0005\u0005\u0000\u0000\u001b\u001c\u0005\n\u0000\u0000\u001c" +
                    "\u001d\u0006\u0002\uffff\uffff\u0000\u001d\u0005\u0001\u0000\u0000\u0000" +
                    "\u001e\u001f\u0005\u0006\u0000\u0000\u001f \u0005\n\u0000\u0000 !\u0006" +
                    "\u0003\uffff\uffff\u0000!\u0007\u0001\u0000\u0000\u0000\"#\u0005\u0007" +
                    "\u0000\u0000#$\u0005\u000b\u0000\u0000$%\u0006\u0004\uffff\uffff\u0000" +
                    "%\t\u0001\u0000\u0000\u0000&\'\u0005\b\u0000\u0000\'(\u0005\f\u0000\u0000" +
                    "()\u0006\u0005\uffff\uffff\u0000)\u000b\u0001\u0000\u0000\u0000*+\u0005" +
                    "\t\u0000\u0000+,\u0005\u000e\u0000\u0000,-\u0006\u0006\uffff\uffff\u0000" +
                    "-\r\u0001\u0000\u0000\u0000./\u0003\u0000\u0000\u0000/0\u0003\u0002\u0001" +
                    "\u000001\u0003\u0004\u0002\u000012\u0003\u0006\u0003\u000024\u0003\b\u0004" +
                    "\u000035\u0003\n\u0005\u000043\u0001\u0000\u0000\u000045\u0001\u0000\u0000" +
                    "\u000057\u0001\u0000\u0000\u000068\u0003\f\u0006\u000076\u0001\u0000\u0000" +
                    "\u000078\u0001\u0000\u0000\u000089\u0001\u0000\u0000\u00009:\u0006\u0007" +
                    "\uffff\uffff\u0000:\u000f\u0001\u0000\u0000\u0000;<\u0003\u000e\u0007" +
                    "\u0000<=\u0006\b\uffff\uffff\u0000=?\u0001\u0000\u0000\u0000>;\u0001\u0000" +
                    "\u0000\u0000?@\u0001\u0000\u0000\u0000@>\u0001\u0000\u0000\u0000@A\u0001" +
                    "\u0000\u0000\u0000AB\u0001\u0000\u0000\u0000BC\u0005\u0000\u0000\u0001" +
                    "C\u0011\u0001\u0000\u0000\u0000\u000347@";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}