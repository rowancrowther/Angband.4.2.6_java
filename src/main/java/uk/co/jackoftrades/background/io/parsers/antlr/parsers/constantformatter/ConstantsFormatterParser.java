// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/background/io/parsers/antlr/grammars/ConstantsFormatter.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.background.io.parsers.antlr.parsers.constantformatter;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ConstantsFormatterParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            TOKEN = 1, FURTHER = 2, COMMENT = 3, COLON = 4, VALUE = 5, EOL = 6;
    public static final int
            RULE_section = 0, RULE_furtherValue = 1, RULE_multiValue = 2, RULE_line = 3,
            RULE_file = 4;

    private static String[] makeRuleNames() {
        return new String[]{
                "section", "furtherValue", "multiValue", "line", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, null, "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "TOKEN", "FURTHER", "COMMENT", "COLON", "VALUE", "EOL"
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
        return "ConstantsFormatter.g4";
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

    public ConstantsFormatterParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class SectionContext extends ParserRuleContext {
        public String sect;
        public String value;
        public Token token1;
        public Token token2;
        public Token VALUE;

        public List<TerminalNode> COLON() {
            return getTokens(ConstantsFormatterParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(ConstantsFormatterParser.COLON, i);
        }

        public TerminalNode VALUE() {
            return getToken(ConstantsFormatterParser.VALUE, 0);
        }

        public List<TerminalNode> TOKEN() {
            return getTokens(ConstantsFormatterParser.TOKEN);
        }

        public TerminalNode TOKEN(int i) {
            return getToken(ConstantsFormatterParser.TOKEN, i);
        }

        public SectionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_section;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ConstantsFormatterListener)
                ((ConstantsFormatterListener) listener).enterSection(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ConstantsFormatterListener)
                ((ConstantsFormatterListener) listener).exitSection(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ConstantsFormatterVisitor)
                return ((ConstantsFormatterVisitor<? extends T>) visitor).visitSection(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SectionContext section() throws RecognitionException {
        SectionContext _localctx = new SectionContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_section);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(10);
                ((SectionContext) _localctx).token1 = match(TOKEN);
                ((SectionContext) _localctx).sect = ((SectionContext) _localctx).token1.getText();
                setState(12);
                match(COLON);
                setState(13);
                ((SectionContext) _localctx).token2 = match(TOKEN);
                ((SectionContext) _localctx).value = ((SectionContext) _localctx).token2.getText();
                setState(15);
                match(COLON);
                setState(16);
                ((SectionContext) _localctx).VALUE = match(VALUE);
                ((SectionContext) _localctx).value = _localctx.value + ":" + ((SectionContext) _localctx).VALUE.getText();
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
    public static class FurtherValueContext extends ParserRuleContext {
        public String sect;
        public String further;
        public Token TOKEN;
        public Token val1;
        public Token val2;
        public Token FURTHER;

        public TerminalNode TOKEN() {
            return getToken(ConstantsFormatterParser.TOKEN, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(ConstantsFormatterParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(ConstantsFormatterParser.COLON, i);
        }

        public TerminalNode FURTHER() {
            return getToken(ConstantsFormatterParser.FURTHER, 0);
        }

        public List<TerminalNode> VALUE() {
            return getTokens(ConstantsFormatterParser.VALUE);
        }

        public TerminalNode VALUE(int i) {
            return getToken(ConstantsFormatterParser.VALUE, i);
        }

        public FurtherValueContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_furtherValue;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ConstantsFormatterListener)
                ((ConstantsFormatterListener) listener).enterFurtherValue(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ConstantsFormatterListener)
                ((ConstantsFormatterListener) listener).exitFurtherValue(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ConstantsFormatterVisitor)
                return ((ConstantsFormatterVisitor<? extends T>) visitor).visitFurtherValue(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FurtherValueContext furtherValue() throws RecognitionException {
        FurtherValueContext _localctx = new FurtherValueContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_furtherValue);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(19);
                ((FurtherValueContext) _localctx).TOKEN = match(TOKEN);
                ((FurtherValueContext) _localctx).sect = ((FurtherValueContext) _localctx).TOKEN.getText();
                setState(21);
                match(COLON);
                setState(22);
                ((FurtherValueContext) _localctx).val1 = match(VALUE);
                setState(23);
                match(COLON);
                setState(24);
                ((FurtherValueContext) _localctx).val2 = match(VALUE);
                setState(25);
                match(COLON);
                setState(26);
                ((FurtherValueContext) _localctx).FURTHER = match(FURTHER);

                ((FurtherValueContext) _localctx).further = ((FurtherValueContext) _localctx).val1.getText() + ":" + ((FurtherValueContext) _localctx).val2.getText() + ":" + ((FurtherValueContext) _localctx).FURTHER.getText();

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
    public static class MultiValueContext extends ParserRuleContext {
        public String sect;
        public String multi;
        public Token TOKEN;
        public Token val1;
        public Token val2;
        public Token val3;
        public Token FURTHER;

        public TerminalNode TOKEN() {
            return getToken(ConstantsFormatterParser.TOKEN, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(ConstantsFormatterParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(ConstantsFormatterParser.COLON, i);
        }

        public TerminalNode FURTHER() {
            return getToken(ConstantsFormatterParser.FURTHER, 0);
        }

        public List<TerminalNode> VALUE() {
            return getTokens(ConstantsFormatterParser.VALUE);
        }

        public TerminalNode VALUE(int i) {
            return getToken(ConstantsFormatterParser.VALUE, i);
        }

        public MultiValueContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_multiValue;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ConstantsFormatterListener)
                ((ConstantsFormatterListener) listener).enterMultiValue(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ConstantsFormatterListener)
                ((ConstantsFormatterListener) listener).exitMultiValue(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ConstantsFormatterVisitor)
                return ((ConstantsFormatterVisitor<? extends T>) visitor).visitMultiValue(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MultiValueContext multiValue() throws RecognitionException {
        MultiValueContext _localctx = new MultiValueContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_multiValue);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(29);
                ((MultiValueContext) _localctx).TOKEN = match(TOKEN);
                ((MultiValueContext) _localctx).sect = ((MultiValueContext) _localctx).TOKEN.getText();
                setState(31);
                match(COLON);
                setState(32);
                ((MultiValueContext) _localctx).val1 = match(VALUE);
                setState(33);
                match(COLON);
                setState(34);
                ((MultiValueContext) _localctx).val2 = match(VALUE);
                setState(35);
                match(COLON);
                setState(36);
                ((MultiValueContext) _localctx).val3 = match(VALUE);
                setState(37);
                match(COLON);
                setState(38);
                ((MultiValueContext) _localctx).FURTHER = match(FURTHER);

                ((MultiValueContext) _localctx).multi = ((MultiValueContext) _localctx).val1.getText() + ":" + ((MultiValueContext) _localctx).val2.getText() + ":" + ((MultiValueContext) _localctx).val3.getText() + ":" + ((MultiValueContext) _localctx).FURTHER.getText();

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
    public static class LineContext extends ParserRuleContext {
        public String sect;
        public String val;
        public SectionContext section;
        public FurtherValueContext furtherValue;
        public MultiValueContext multiValue;

        public SectionContext section() {
            return getRuleContext(SectionContext.class, 0);
        }

        public List<TerminalNode> EOL() {
            return getTokens(ConstantsFormatterParser.EOL);
        }

        public TerminalNode EOL(int i) {
            return getToken(ConstantsFormatterParser.EOL, i);
        }

        public FurtherValueContext furtherValue() {
            return getRuleContext(FurtherValueContext.class, 0);
        }

        public MultiValueContext multiValue() {
            return getRuleContext(MultiValueContext.class, 0);
        }

        public LineContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_line;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ConstantsFormatterListener) ((ConstantsFormatterListener) listener).enterLine(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ConstantsFormatterListener) ((ConstantsFormatterListener) listener).exitLine(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ConstantsFormatterVisitor)
                return ((ConstantsFormatterVisitor<? extends T>) visitor).visitLine(this);
            else return visitor.visitChildren(this);
        }
    }

    public final LineContext line() throws RecognitionException {
        LineContext _localctx = new LineContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_line);
        try {
            int _alt;
            setState(68);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 3, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(41);
                    ((LineContext) _localctx).section = section();
                    setState(45);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 0, _ctx);
                    while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                        if (_alt == 1) {
                            {
                                {
                                    setState(42);
                                    match(EOL);
                                }
                            }
                        }
                        setState(47);
                        _errHandler.sync(this);
                        _alt = getInterpreter().adaptivePredict(_input, 0, _ctx);
                    }

                    ((LineContext) _localctx).sect = ((LineContext) _localctx).section.sect;
                    ((LineContext) _localctx).val = ((LineContext) _localctx).section.value;

                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(50);
                    ((LineContext) _localctx).furtherValue = furtherValue();
                    setState(54);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 1, _ctx);
                    while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                        if (_alt == 1) {
                            {
                                {
                                    setState(51);
                                    match(EOL);
                                }
                            }
                        }
                        setState(56);
                        _errHandler.sync(this);
                        _alt = getInterpreter().adaptivePredict(_input, 1, _ctx);
                    }

                    ((LineContext) _localctx).sect = ((LineContext) _localctx).furtherValue.sect;
                    ((LineContext) _localctx).val = ((LineContext) _localctx).furtherValue.further;

                }
                break;
                case 3:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(59);
                    ((LineContext) _localctx).multiValue = multiValue();
                    setState(63);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 2, _ctx);
                    while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                        if (_alt == 1) {
                            {
                                {
                                    setState(60);
                                    match(EOL);
                                }
                            }
                        }
                        setState(65);
                        _errHandler.sync(this);
                        _alt = getInterpreter().adaptivePredict(_input, 2, _ctx);
                    }

                    ((LineContext) _localctx).sect = ((LineContext) _localctx).multiValue.sect;
                    ((LineContext) _localctx).val = ((LineContext) _localctx).multiValue.multi;

                }
                break;
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
        public HashMap<String, String> keyValues;
        public LineContext l1;
        public LineContext l2;

        public TerminalNode EOF() {
            return getToken(ConstantsFormatterParser.EOF, 0);
        }

        public List<LineContext> line() {
            return getRuleContexts(LineContext.class);
        }

        public LineContext line(int i) {
            return getRuleContext(LineContext.class, i);
        }

        public List<TerminalNode> EOL() {
            return getTokens(ConstantsFormatterParser.EOL);
        }

        public TerminalNode EOL(int i) {
            return getToken(ConstantsFormatterParser.EOL, i);
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
            if (listener instanceof ConstantsFormatterListener) ((ConstantsFormatterListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ConstantsFormatterListener) ((ConstantsFormatterListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ConstantsFormatterVisitor)
                return ((ConstantsFormatterVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_file);

        ((FileContext) _localctx).keyValues = new HashMap<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(70);
                ((FileContext) _localctx).l1 = line();

                _localctx.keyValues.put(((FileContext) _localctx).l1.sect, ((FileContext) _localctx).l1.val);

                setState(77);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == TOKEN) {
                    {
                        {
                            setState(72);
                            ((FileContext) _localctx).l2 = line();

                            _localctx.keyValues.put(((FileContext) _localctx).l2.sect, ((FileContext) _localctx).l2.val);

                        }
                    }
                    setState(79);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
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
            "\u0004\u0001\u0006Y\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0003\u0001\u0003\u0005\u0003,\b\u0003\n\u0003\f\u0003/\t" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0005\u00035\b" +
                    "\u0003\n\u0003\f\u00038\t\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0005\u0003>\b\u0003\n\u0003\f\u0003A\t\u0003\u0001\u0003\u0001" +
                    "\u0003\u0003\u0003E\b\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0005\u0004L\b\u0004\n\u0004\f\u0004O\t\u0004\u0001" +
                    "\u0004\u0005\u0004R\b\u0004\n\u0004\f\u0004U\t\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0000\u0000\u0005\u0000\u0002\u0004\u0006\b\u0000\u0000" +
                    "Z\u0000\n\u0001\u0000\u0000\u0000\u0002\u0013\u0001\u0000\u0000\u0000" +
                    "\u0004\u001d\u0001\u0000\u0000\u0000\u0006D\u0001\u0000\u0000\u0000\b" +
                    "F\u0001\u0000\u0000\u0000\n\u000b\u0005\u0001\u0000\u0000\u000b\f\u0006" +
                    "\u0000\uffff\uffff\u0000\f\r\u0005\u0004\u0000\u0000\r\u000e\u0005\u0001" +
                    "\u0000\u0000\u000e\u000f\u0006\u0000\uffff\uffff\u0000\u000f\u0010\u0005" +
                    "\u0004\u0000\u0000\u0010\u0011\u0005\u0005\u0000\u0000\u0011\u0012\u0006" +
                    "\u0000\uffff\uffff\u0000\u0012\u0001\u0001\u0000\u0000\u0000\u0013\u0014" +
                    "\u0005\u0001\u0000\u0000\u0014\u0015\u0006\u0001\uffff\uffff\u0000\u0015" +
                    "\u0016\u0005\u0004\u0000\u0000\u0016\u0017\u0005\u0005\u0000\u0000\u0017" +
                    "\u0018\u0005\u0004\u0000\u0000\u0018\u0019\u0005\u0005\u0000\u0000\u0019" +
                    "\u001a\u0005\u0004\u0000\u0000\u001a\u001b\u0005\u0002\u0000\u0000\u001b" +
                    "\u001c\u0006\u0001\uffff\uffff\u0000\u001c\u0003\u0001\u0000\u0000\u0000" +
                    "\u001d\u001e\u0005\u0001\u0000\u0000\u001e\u001f\u0006\u0002\uffff\uffff" +
                    "\u0000\u001f \u0005\u0004\u0000\u0000 !\u0005\u0005\u0000\u0000!\"\u0005" +
                    "\u0004\u0000\u0000\"#\u0005\u0005\u0000\u0000#$\u0005\u0004\u0000\u0000" +
                    "$%\u0005\u0005\u0000\u0000%&\u0005\u0004\u0000\u0000&\'\u0005\u0002\u0000" +
                    "\u0000\'(\u0006\u0002\uffff\uffff\u0000(\u0005\u0001\u0000\u0000\u0000" +
                    ")-\u0003\u0000\u0000\u0000*,\u0005\u0006\u0000\u0000+*\u0001\u0000\u0000" +
                    "\u0000,/\u0001\u0000\u0000\u0000-+\u0001\u0000\u0000\u0000-.\u0001\u0000" +
                    "\u0000\u0000.0\u0001\u0000\u0000\u0000/-\u0001\u0000\u0000\u000001\u0006" +
                    "\u0003\uffff\uffff\u00001E\u0001\u0000\u0000\u000026\u0003\u0002\u0001" +
                    "\u000035\u0005\u0006\u0000\u000043\u0001\u0000\u0000\u000058\u0001\u0000" +
                    "\u0000\u000064\u0001\u0000\u0000\u000067\u0001\u0000\u0000\u000079\u0001" +
                    "\u0000\u0000\u000086\u0001\u0000\u0000\u00009:\u0006\u0003\uffff\uffff" +
                    "\u0000:E\u0001\u0000\u0000\u0000;?\u0003\u0004\u0002\u0000<>\u0005\u0006" +
                    "\u0000\u0000=<\u0001\u0000\u0000\u0000>A\u0001\u0000\u0000\u0000?=\u0001" +
                    "\u0000\u0000\u0000?@\u0001\u0000\u0000\u0000@B\u0001\u0000\u0000\u0000" +
                    "A?\u0001\u0000\u0000\u0000BC\u0006\u0003\uffff\uffff\u0000CE\u0001\u0000" +
                    "\u0000\u0000D)\u0001\u0000\u0000\u0000D2\u0001\u0000\u0000\u0000D;\u0001" +
                    "\u0000\u0000\u0000E\u0007\u0001\u0000\u0000\u0000FG\u0003\u0006\u0003" +
                    "\u0000GM\u0006\u0004\uffff\uffff\u0000HI\u0003\u0006\u0003\u0000IJ\u0006" +
                    "\u0004\uffff\uffff\u0000JL\u0001\u0000\u0000\u0000KH\u0001\u0000\u0000" +
                    "\u0000LO\u0001\u0000\u0000\u0000MK\u0001\u0000\u0000\u0000MN\u0001\u0000" +
                    "\u0000\u0000NS\u0001\u0000\u0000\u0000OM\u0001\u0000\u0000\u0000PR\u0005" +
                    "\u0006\u0000\u0000QP\u0001\u0000\u0000\u0000RU\u0001\u0000\u0000\u0000" +
                    "SQ\u0001\u0000\u0000\u0000ST\u0001\u0000\u0000\u0000TV\u0001\u0000\u0000" +
                    "\u0000US\u0001\u0000\u0000\u0000VW\u0005\u0000\u0000\u0001W\t\u0001\u0000" +
                    "\u0000\u0000\u0006-6?DMS";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}