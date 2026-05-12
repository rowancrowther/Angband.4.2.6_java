// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/UIEntryGram.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.uientry;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.frontend.entries.UIEntry;
import uk.co.jackoftrades.frontend.entries.UIEntry.StatElemType;
import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
import uk.co.jackoftrades.frontend.entries.enums.EntryFlag;
import uk.co.jackoftrades.frontend.screen.enums.CombinerName;
import uk.co.jackoftrades.middle.enums.StatElementEnum;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.game.globals.GameConstants;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class UIEntryGramParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, NAME = 3, PARAMETER = 4, RENDERER = 5, COMBINE = 6, CATEGORY = 7,
            DESC = 8, FLAGS = 9, LABEL = 10, LABEL5 = 11, LABEL2 = 12, TEMPLATE = 13, PRIORITY = 14,
            PARAMETERTYPE = 15, COMBINETYPE = 16, NUMBER = 17, MINUS = 18, TAG = 19, TEXT = 20;
    public static final int
            RULE_name = 0, RULE_parameter = 1, RULE_renderer = 2, RULE_combine = 3,
            RULE_priority = 4, RULE_category = 5, RULE_flags = 6, RULE_desc = 7, RULE_label = 8,
            RULE_label2 = 9, RULE_label5 = 10, RULE_template = 11, RULE_entry = 12,
            RULE_entries = 13;

    private static String[] makeRuleNames() {
        return new String[]{
                "name", "parameter", "renderer", "combine", "priority", "category", "flags",
                "desc", "label", "label2", "label5", "template", "entry", "entries"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'name:'", "'parameter:'", "'renderer:'", "'combine:'",
                "'category:'", "'desc:'", "'flags:'", "'label:'", "'label5:'", "'label2:'",
                "'template:'", "'priority:'", null, null, null, "'-'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "NAME", "PARAMETER", "RENDERER", "COMBINE", "CATEGORY",
                "DESC", "FLAGS", "LABEL", "LABEL5", "LABEL2", "TEMPLATE", "PRIORITY",
                "PARAMETERTYPE", "COMBINETYPE", "NUMBER", "MINUS", "TAG", "TEXT"
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
        return "UIEntryGram.g4";
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

    public UIEntryGramParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class NameContext extends ParserRuleContext {
        public String nameText;
        public StatElementEnum tag;
        public Token TEXT;
        public Token TAG;

        public TerminalNode NAME() {
            return getToken(UIEntryGramParser.NAME, 0);
        }

        public TerminalNode TEXT() {
            return getToken(UIEntryGramParser.TEXT, 0);
        }

        public TerminalNode TAG() {
            return getToken(UIEntryGramParser.TAG, 0);
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
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryGramVisitor)
                return ((UIEntryGramVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_name);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(28);
                match(NAME);
                setState(29);
                ((NameContext) _localctx).TEXT = match(TEXT);
                setState(31);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == TAG) {
                    {
                        setState(30);
                        ((NameContext) _localctx).TAG = match(TAG);
                    }
                }


                ((NameContext) _localctx).nameText = ((NameContext) _localctx).TEXT.getText();
                if (((NameContext) _localctx).TAG != null) {
                    String t = ((NameContext) _localctx).TAG.getText();
                    t = t.substring(1, t.length() - 1);
                    // Assume it's a stat and try to get it from Stats
                    String test = "STAT_" + t;
                    try {
                        Stats.valueOf(test);
                        ((NameContext) _localctx).tag = StatElementEnum.valueOf("STAT_" + t);
                    } catch (IllegalArgumentException i) {
                        ((NameContext) _localctx).tag = StatElementEnum.valueOf("ELEM_" + t);
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
    public static class ParameterContext extends ParserRuleContext {
        public StatElemType parameterType;
        public Token PARAMETERTYPE;

        public TerminalNode PARAMETER() {
            return getToken(UIEntryGramParser.PARAMETER, 0);
        }

        public TerminalNode PARAMETERTYPE() {
            return getToken(UIEntryGramParser.PARAMETERTYPE, 0);
        }

        public ParameterContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_parameter;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).enterParameter(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).exitParameter(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryGramVisitor)
                return ((UIEntryGramVisitor<? extends T>) visitor).visitParameter(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ParameterContext parameter() throws RecognitionException {
        ParameterContext _localctx = new ParameterContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_parameter);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(35);
                match(PARAMETER);
                setState(36);
                ((ParameterContext) _localctx).PARAMETERTYPE = match(PARAMETERTYPE);

                String parm = ((ParameterContext) _localctx).PARAMETERTYPE.getText().toUpperCase();
                ((ParameterContext) _localctx).parameterType = StatElemType.valueOf(parm);

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
        public Token TEXT;

        public TerminalNode RENDERER() {
            return getToken(UIEntryGramParser.RENDERER, 0);
        }

        public TerminalNode TEXT() {
            return getToken(UIEntryGramParser.TEXT, 0);
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
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).enterRenderer(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).exitRenderer(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryGramVisitor)
                return ((UIEntryGramVisitor<? extends T>) visitor).visitRenderer(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RendererContext renderer() throws RecognitionException {
        RendererContext _localctx = new RendererContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_renderer);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(39);
                match(RENDERER);
                setState(40);
                ((RendererContext) _localctx).TEXT = match(TEXT);

                String rendName = ((RendererContext) _localctx).TEXT.getText();
                ((RendererContext) _localctx).rend = GameConstants.getUIEntryRenderer(rendName);

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
        public CombinerName combineType;
        public Token COMBINETYPE;

        public TerminalNode COMBINE() {
            return getToken(UIEntryGramParser.COMBINE, 0);
        }

        public TerminalNode COMBINETYPE() {
            return getToken(UIEntryGramParser.COMBINETYPE, 0);
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
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).enterCombine(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).exitCombine(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryGramVisitor)
                return ((UIEntryGramVisitor<? extends T>) visitor).visitCombine(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CombineContext combine() throws RecognitionException {
        CombineContext _localctx = new CombineContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_combine);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(43);
                match(COMBINE);
                setState(44);
                ((CombineContext) _localctx).COMBINETYPE = match(COMBINETYPE);

                String combiner = ((CombineContext) _localctx).COMBINETYPE.getText();
                ((CombineContext) _localctx).combineType = CombinerName.valueOf(combiner);

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
    public static class PriorityContext extends ParserRuleContext {
        public String priorityString;
        public int priorityNum;
        public Token TEXT;
        public Token MINUS;
        public Token NUMBER;

        public TerminalNode PRIORITY() {
            return getToken(UIEntryGramParser.PRIORITY, 0);
        }

        public TerminalNode TEXT() {
            return getToken(UIEntryGramParser.TEXT, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(UIEntryGramParser.NUMBER, 0);
        }

        public TerminalNode MINUS() {
            return getToken(UIEntryGramParser.MINUS, 0);
        }

        public PriorityContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_priority;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).enterPriority(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).exitPriority(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryGramVisitor)
                return ((UIEntryGramVisitor<? extends T>) visitor).visitPriority(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PriorityContext priority() throws RecognitionException {
        PriorityContext _localctx = new PriorityContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_priority);

        ((PriorityContext) _localctx).priorityString = "";
        ((PriorityContext) _localctx).priorityNum = 0;

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(47);
                match(PRIORITY);
                setState(55);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case TEXT: {
                        setState(48);
                        ((PriorityContext) _localctx).TEXT = match(TEXT);

                        ((PriorityContext) _localctx).priorityString = ((PriorityContext) _localctx).TEXT.getText();

                    }
                    break;
                    case NUMBER:
                    case MINUS: {
                        setState(51);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == MINUS) {
                            {
                                setState(50);
                                ((PriorityContext) _localctx).MINUS = match(MINUS);
                            }
                        }

                        setState(53);
                        ((PriorityContext) _localctx).NUMBER = match(NUMBER);

                        int negative = (((PriorityContext) _localctx).MINUS == null) ? 1 : -1;
                        ((PriorityContext) _localctx).priorityNum = negative * Integer.parseInt(((PriorityContext) _localctx).NUMBER.getText());

                    }
                    break;
                    default:
                        throw new NoViableAltException(this);
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
    public static class CategoryContext extends ParserRuleContext {
        public String cat;
        public Token TEXT;

        public TerminalNode CATEGORY() {
            return getToken(UIEntryGramParser.CATEGORY, 0);
        }

        public TerminalNode TEXT() {
            return getToken(UIEntryGramParser.TEXT, 0);
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
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).enterCategory(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).exitCategory(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryGramVisitor)
                return ((UIEntryGramVisitor<? extends T>) visitor).visitCategory(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CategoryContext category() throws RecognitionException {
        CategoryContext _localctx = new CategoryContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_category);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(57);
                match(CATEGORY);
                setState(58);
                ((CategoryContext) _localctx).TEXT = match(TEXT);

                ((CategoryContext) _localctx).cat = ((CategoryContext) _localctx).TEXT.getText();

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
        public String flagString;
        public Token TEXT;

        public TerminalNode FLAGS() {
            return getToken(UIEntryGramParser.FLAGS, 0);
        }

        public TerminalNode TEXT() {
            return getToken(UIEntryGramParser.TEXT, 0);
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
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).enterFlags(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).exitFlags(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryGramVisitor)
                return ((UIEntryGramVisitor<? extends T>) visitor).visitFlags(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FlagsContext flags() throws RecognitionException {
        FlagsContext _localctx = new FlagsContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_flags);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(61);
                match(FLAGS);
                setState(62);
                ((FlagsContext) _localctx).TEXT = match(TEXT);

                ((FlagsContext) _localctx).flagString = ((FlagsContext) _localctx).TEXT.getText();

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
            return getToken(UIEntryGramParser.DESC, 0);
        }

        public TerminalNode TEXT() {
            return getToken(UIEntryGramParser.TEXT, 0);
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
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).enterDesc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).exitDesc(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryGramVisitor)
                return ((UIEntryGramVisitor<? extends T>) visitor).visitDesc(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DescContext desc() throws RecognitionException {
        DescContext _localctx = new DescContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_desc);

        ((DescContext) _localctx).description = "";

        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(65);
                match(DESC);
                setState(66);
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
    public static class LabelContext extends ParserRuleContext {
        public String labelText;
        public Token TEXT;

        public TerminalNode LABEL() {
            return getToken(UIEntryGramParser.LABEL, 0);
        }

        public TerminalNode TEXT() {
            return getToken(UIEntryGramParser.TEXT, 0);
        }

        public LabelContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_label;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).enterLabel(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).exitLabel(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryGramVisitor)
                return ((UIEntryGramVisitor<? extends T>) visitor).visitLabel(this);
            else return visitor.visitChildren(this);
        }
    }

    public final LabelContext label() throws RecognitionException {
        LabelContext _localctx = new LabelContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_label);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(69);
                match(LABEL);
                setState(70);
                ((LabelContext) _localctx).TEXT = match(TEXT);

                ((LabelContext) _localctx).labelText = ((LabelContext) _localctx).TEXT.getText();

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
    public static class Label2Context extends ParserRuleContext {
        public String label2Text;
        public Token TEXT;

        public TerminalNode LABEL2() {
            return getToken(UIEntryGramParser.LABEL2, 0);
        }

        public TerminalNode TEXT() {
            return getToken(UIEntryGramParser.TEXT, 0);
        }

        public Label2Context(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_label2;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).enterLabel2(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).exitLabel2(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryGramVisitor)
                return ((UIEntryGramVisitor<? extends T>) visitor).visitLabel2(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Label2Context label2() throws RecognitionException {
        Label2Context _localctx = new Label2Context(_ctx, getState());
        enterRule(_localctx, 18, RULE_label2);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(73);
                match(LABEL2);
                setState(74);
                ((Label2Context) _localctx).TEXT = match(TEXT);

                ((Label2Context) _localctx).label2Text = ((Label2Context) _localctx).TEXT.getText();

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
    public static class Label5Context extends ParserRuleContext {
        public String label5Text;
        public Token TEXT;

        public TerminalNode LABEL5() {
            return getToken(UIEntryGramParser.LABEL5, 0);
        }

        public TerminalNode TEXT() {
            return getToken(UIEntryGramParser.TEXT, 0);
        }

        public Label5Context(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_label5;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).enterLabel5(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).exitLabel5(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryGramVisitor)
                return ((UIEntryGramVisitor<? extends T>) visitor).visitLabel5(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Label5Context label5() throws RecognitionException {
        Label5Context _localctx = new Label5Context(_ctx, getState());
        enterRule(_localctx, 20, RULE_label5);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(77);
                match(LABEL5);
                setState(78);
                ((Label5Context) _localctx).TEXT = match(TEXT);

                ((Label5Context) _localctx).label5Text = ((Label5Context) _localctx).TEXT.getText();

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
    public static class TemplateContext extends ParserRuleContext {
        public String templateText;
        public Token TEXT;

        public TerminalNode TEMPLATE() {
            return getToken(UIEntryGramParser.TEMPLATE, 0);
        }

        public TerminalNode TEXT() {
            return getToken(UIEntryGramParser.TEXT, 0);
        }

        public TemplateContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_template;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).enterTemplate(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).exitTemplate(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryGramVisitor)
                return ((UIEntryGramVisitor<? extends T>) visitor).visitTemplate(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TemplateContext template() throws RecognitionException {
        TemplateContext _localctx = new TemplateContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_template);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(81);
                match(TEMPLATE);
                setState(82);
                ((TemplateContext) _localctx).TEXT = match(TEXT);

                ((TemplateContext) _localctx).templateText = ((TemplateContext) _localctx).TEXT.getText();

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
        public UIEntry ent;
        public NameContext name;
        public ParameterContext parameter;
        public RendererContext renderer;
        public CombineContext combine;
        public PriorityContext priority;
        public CategoryContext category;
        public FlagsContext flags;
        public DescContext desc;
        public LabelContext label;
        public Label5Context label5;
        public Label2Context label2;
        public TemplateContext template;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public ParameterContext parameter() {
            return getRuleContext(ParameterContext.class, 0);
        }

        public RendererContext renderer() {
            return getRuleContext(RendererContext.class, 0);
        }

        public CombineContext combine() {
            return getRuleContext(CombineContext.class, 0);
        }

        public PriorityContext priority() {
            return getRuleContext(PriorityContext.class, 0);
        }

        public LabelContext label() {
            return getRuleContext(LabelContext.class, 0);
        }

        public TemplateContext template() {
            return getRuleContext(TemplateContext.class, 0);
        }

        public List<CategoryContext> category() {
            return getRuleContexts(CategoryContext.class);
        }

        public CategoryContext category(int i) {
            return getRuleContext(CategoryContext.class, i);
        }

        public FlagsContext flags() {
            return getRuleContext(FlagsContext.class, 0);
        }

        public List<DescContext> desc() {
            return getRuleContexts(DescContext.class);
        }

        public DescContext desc(int i) {
            return getRuleContext(DescContext.class, i);
        }

        public Label5Context label5() {
            return getRuleContext(Label5Context.class, 0);
        }

        public Label2Context label2() {
            return getRuleContext(Label2Context.class, 0);
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
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).enterEntry(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).exitEntry(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryGramVisitor)
                return ((UIEntryGramVisitor<? extends T>) visitor).visitEntry(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EntryContext entry() throws RecognitionException {
        EntryContext _localctx = new EntryContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_entry);

        String nameStr = "";
        StatElementEnum statOrElem = null;
        StatElemType parameterType = null;
        UIEntryRenderer render = null;
        CombinerName combinerName = null;
        int priorityNum = 0;
        String priorityName = "";
        ArrayList<String> categories = new ArrayList<>();
        EntryFlag entryFlag = null;
        String description = "";
        String labelStr = "";
        String label2Str = "";
        String label5Str = "";
        String templateStr = "";

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(85);
                ((EntryContext) _localctx).name = name();

                nameStr = ((EntryContext) _localctx).name.nameText;
                statOrElem = ((EntryContext) _localctx).name.tag;

                setState(150);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case PARAMETER: {
                        setState(87);
                        ((EntryContext) _localctx).parameter = parameter();

                        parameterType = ((EntryContext) _localctx).parameter.parameterType;

                        setState(89);
                        ((EntryContext) _localctx).renderer = renderer();

                        render = ((EntryContext) _localctx).renderer.rend;

                        setState(91);
                        ((EntryContext) _localctx).combine = combine();

                        combinerName = ((EntryContext) _localctx).combine.combineType;

                        setState(93);
                        ((EntryContext) _localctx).priority = priority();

                        priorityNum = ((EntryContext) _localctx).priority.priorityNum;
                        priorityName = ((EntryContext) _localctx).priority.priorityString;

                        setState(98);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        do {
                            {
                                {
                                    setState(95);
                                    ((EntryContext) _localctx).category = category();

                                    categories.add(((EntryContext) _localctx).category.cat);

                                }
                            }
                            setState(100);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        } while (_la == CATEGORY);
                        setState(105);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == FLAGS) {
                            {
                                setState(102);
                                ((EntryContext) _localctx).flags = flags();

                                entryFlag = EntryFlag.valueOf("ENTRY_FLAG_" + ((EntryContext) _localctx).flags.flagString);

                            }
                        }

                        setState(110);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        do {
                            {
                                {
                                    setState(107);
                                    ((EntryContext) _localctx).desc = desc();

                                    description = description + ((EntryContext) _localctx).desc.description;

                                }
                            }
                            setState(112);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        } while (_la == DESC);
                    }
                    break;
                    case LABEL: {
                        setState(114);
                        ((EntryContext) _localctx).label = label();

                        labelStr = ((EntryContext) _localctx).label.labelText;

                        setState(119);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == LABEL5) {
                            {
                                setState(116);
                                ((EntryContext) _localctx).label5 = label5();

                                label5Str = ((EntryContext) _localctx).label5.label5Text;

                            }
                        }

                        setState(124);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == LABEL2) {
                            {
                                setState(121);
                                ((EntryContext) _localctx).label2 = label2();

                                label2Str = ((EntryContext) _localctx).label2.label2Text;

                            }
                        }

                        setState(129);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        do {
                            {
                                {
                                    setState(126);
                                    ((EntryContext) _localctx).category = category();

                                    categories.add(((EntryContext) _localctx).category.cat);

                                }
                            }
                            setState(131);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        } while (_la == CATEGORY);
                    }
                    break;
                    case TEMPLATE: {
                        setState(133);
                        ((EntryContext) _localctx).template = template();

                        templateStr = ((EntryContext) _localctx).template.templateText;

                        setState(135);
                        ((EntryContext) _localctx).label = label();

                        labelStr = ((EntryContext) _localctx).label.labelText;

                        setState(140);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == LABEL5) {
                            {
                                setState(137);
                                ((EntryContext) _localctx).label5 = label5();

                                label5Str = ((EntryContext) _localctx).label5.label5Text;

                            }
                        }

                        setState(145);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        if (_la == LABEL2) {
                            {
                                setState(142);
                                ((EntryContext) _localctx).label2 = label2();

                                label2Str = ((EntryContext) _localctx).label2.label2Text;

                            }
                        }

                        setState(147);
                        ((EntryContext) _localctx).priority = priority();

                        priorityNum = ((EntryContext) _localctx).priority.priorityNum;
                        priorityName = ((EntryContext) _localctx).priority.priorityString;

                    }
                    break;
                    default:
                        throw new NoViableAltException(this);
                }
            }
            _ctx.stop = _input.LT(-1);

            ((EntryContext) _localctx).ent = new UIEntry(nameStr, statOrElem, parameterType,
                    render, combinerName, categories,
                    priorityNum, priorityName, entryFlag,
                    description, labelStr, label2Str,
                    label5Str, templateStr);

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
        public ArrayList<UIEntry> entryList;
        public EntryContext entry;

        public TerminalNode EOF() {
            return getToken(UIEntryGramParser.EOF, 0);
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
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).enterEntries(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof UIEntryGramListener) ((UIEntryGramListener) listener).exitEntries(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof UIEntryGramVisitor)
                return ((UIEntryGramVisitor<? extends T>) visitor).visitEntries(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EntriesContext entries() throws RecognitionException {
        EntriesContext _localctx = new EntriesContext(_ctx, getState());
        enterRule(_localctx, 26, RULE_entries);

        ((EntriesContext) _localctx).entryList = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(155);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(152);
                            ((EntriesContext) _localctx).entry = entry();

                            _localctx.entryList.add(((EntriesContext) _localctx).entry.ent);

                        }
                    }
                    setState(157);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(159);
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
            "\u0004\u0001\u0014\u00a2\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001" +
                    "\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004" +
                    "\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007" +
                    "\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b" +
                    "\u0002\f\u0007\f\u0002\r\u0007\r\u0001\u0000\u0001\u0000\u0001\u0000\u0003" +
                    "\u0000 \b\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0003\u00044\b\u0004\u0001\u0004\u0001\u0004\u0003" +
                    "\u00048\b\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001" +
                    "\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001" +
                    "\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0004\fc\b\f\u000b" +
                    "\f\f\fd\u0001\f\u0001\f\u0001\f\u0003\fj\b\f\u0001\f\u0001\f\u0001\f\u0004" +
                    "\fo\b\f\u000b\f\f\fp\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0003\fx" +
                    "\b\f\u0001\f\u0001\f\u0001\f\u0003\f}\b\f\u0001\f\u0001\f\u0001\f\u0004" +
                    "\f\u0082\b\f\u000b\f\f\f\u0083\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f" +
                    "\u0001\f\u0001\f\u0003\f\u008d\b\f\u0001\f\u0001\f\u0001\f\u0003\f\u0092" +
                    "\b\f\u0001\f\u0001\f\u0001\f\u0003\f\u0097\b\f\u0001\r\u0001\r\u0001\r" +
                    "\u0004\r\u009c\b\r\u000b\r\f\r\u009d\u0001\r\u0001\r\u0001\r\u0000\u0000" +
                    "\u000e\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018" +
                    "\u001a\u0000\u0000\u00a1\u0000\u001c\u0001\u0000\u0000\u0000\u0002#\u0001" +
                    "\u0000\u0000\u0000\u0004\'\u0001\u0000\u0000\u0000\u0006+\u0001\u0000" +
                    "\u0000\u0000\b/\u0001\u0000\u0000\u0000\n9\u0001\u0000\u0000\u0000\f=" +
                    "\u0001\u0000\u0000\u0000\u000eA\u0001\u0000\u0000\u0000\u0010E\u0001\u0000" +
                    "\u0000\u0000\u0012I\u0001\u0000\u0000\u0000\u0014M\u0001\u0000\u0000\u0000" +
                    "\u0016Q\u0001\u0000\u0000\u0000\u0018U\u0001\u0000\u0000\u0000\u001a\u009b" +
                    "\u0001\u0000\u0000\u0000\u001c\u001d\u0005\u0003\u0000\u0000\u001d\u001f" +
                    "\u0005\u0014\u0000\u0000\u001e \u0005\u0013\u0000\u0000\u001f\u001e\u0001" +
                    "\u0000\u0000\u0000\u001f \u0001\u0000\u0000\u0000 !\u0001\u0000\u0000" +
                    "\u0000!\"\u0006\u0000\uffff\uffff\u0000\"\u0001\u0001\u0000\u0000\u0000" +
                    "#$\u0005\u0004\u0000\u0000$%\u0005\u000f\u0000\u0000%&\u0006\u0001\uffff" +
                    "\uffff\u0000&\u0003\u0001\u0000\u0000\u0000\'(\u0005\u0005\u0000\u0000" +
                    "()\u0005\u0014\u0000\u0000)*\u0006\u0002\uffff\uffff\u0000*\u0005\u0001" +
                    "\u0000\u0000\u0000+,\u0005\u0006\u0000\u0000,-\u0005\u0010\u0000\u0000" +
                    "-.\u0006\u0003\uffff\uffff\u0000.\u0007\u0001\u0000\u0000\u0000/7\u0005" +
                    "\u000e\u0000\u000001\u0005\u0014\u0000\u000018\u0006\u0004\uffff\uffff" +
                    "\u000024\u0005\u0012\u0000\u000032\u0001\u0000\u0000\u000034\u0001\u0000" +
                    "\u0000\u000045\u0001\u0000\u0000\u000056\u0005\u0011\u0000\u000068\u0006" +
                    "\u0004\uffff\uffff\u000070\u0001\u0000\u0000\u000073\u0001\u0000\u0000" +
                    "\u00008\t\u0001\u0000\u0000\u00009:\u0005\u0007\u0000\u0000:;\u0005\u0014" +
                    "\u0000\u0000;<\u0006\u0005\uffff\uffff\u0000<\u000b\u0001\u0000\u0000" +
                    "\u0000=>\u0005\t\u0000\u0000>?\u0005\u0014\u0000\u0000?@\u0006\u0006\uffff" +
                    "\uffff\u0000@\r\u0001\u0000\u0000\u0000AB\u0005\b\u0000\u0000BC\u0005" +
                    "\u0014\u0000\u0000CD\u0006\u0007\uffff\uffff\u0000D\u000f\u0001\u0000" +
                    "\u0000\u0000EF\u0005\n\u0000\u0000FG\u0005\u0014\u0000\u0000GH\u0006\b" +
                    "\uffff\uffff\u0000H\u0011\u0001\u0000\u0000\u0000IJ\u0005\f\u0000\u0000" +
                    "JK\u0005\u0014\u0000\u0000KL\u0006\t\uffff\uffff\u0000L\u0013\u0001\u0000" +
                    "\u0000\u0000MN\u0005\u000b\u0000\u0000NO\u0005\u0014\u0000\u0000OP\u0006" +
                    "\n\uffff\uffff\u0000P\u0015\u0001\u0000\u0000\u0000QR\u0005\r\u0000\u0000" +
                    "RS\u0005\u0014\u0000\u0000ST\u0006\u000b\uffff\uffff\u0000T\u0017\u0001" +
                    "\u0000\u0000\u0000UV\u0003\u0000\u0000\u0000V\u0096\u0006\f\uffff\uffff" +
                    "\u0000WX\u0003\u0002\u0001\u0000XY\u0006\f\uffff\uffff\u0000YZ\u0003\u0004" +
                    "\u0002\u0000Z[\u0006\f\uffff\uffff\u0000[\\\u0003\u0006\u0003\u0000\\" +
                    "]\u0006\f\uffff\uffff\u0000]^\u0003\b\u0004\u0000^b\u0006\f\uffff\uffff" +
                    "\u0000_`\u0003\n\u0005\u0000`a\u0006\f\uffff\uffff\u0000ac\u0001\u0000" +
                    "\u0000\u0000b_\u0001\u0000\u0000\u0000cd\u0001\u0000\u0000\u0000db\u0001" +
                    "\u0000\u0000\u0000de\u0001\u0000\u0000\u0000ei\u0001\u0000\u0000\u0000" +
                    "fg\u0003\f\u0006\u0000gh\u0006\f\uffff\uffff\u0000hj\u0001\u0000\u0000" +
                    "\u0000if\u0001\u0000\u0000\u0000ij\u0001\u0000\u0000\u0000jn\u0001\u0000" +
                    "\u0000\u0000kl\u0003\u000e\u0007\u0000lm\u0006\f\uffff\uffff\u0000mo\u0001" +
                    "\u0000\u0000\u0000nk\u0001\u0000\u0000\u0000op\u0001\u0000\u0000\u0000" +
                    "pn\u0001\u0000\u0000\u0000pq\u0001\u0000\u0000\u0000q\u0097\u0001\u0000" +
                    "\u0000\u0000rs\u0003\u0010\b\u0000sw\u0006\f\uffff\uffff\u0000tu\u0003" +
                    "\u0014\n\u0000uv\u0006\f\uffff\uffff\u0000vx\u0001\u0000\u0000\u0000w" +
                    "t\u0001\u0000\u0000\u0000wx\u0001\u0000\u0000\u0000x|\u0001\u0000\u0000" +
                    "\u0000yz\u0003\u0012\t\u0000z{\u0006\f\uffff\uffff\u0000{}\u0001\u0000" +
                    "\u0000\u0000|y\u0001\u0000\u0000\u0000|}\u0001\u0000\u0000\u0000}\u0081" +
                    "\u0001\u0000\u0000\u0000~\u007f\u0003\n\u0005\u0000\u007f\u0080\u0006" +
                    "\f\uffff\uffff\u0000\u0080\u0082\u0001\u0000\u0000\u0000\u0081~\u0001" +
                    "\u0000\u0000\u0000\u0082\u0083\u0001\u0000\u0000\u0000\u0083\u0081\u0001" +
                    "\u0000\u0000\u0000\u0083\u0084\u0001\u0000\u0000\u0000\u0084\u0097\u0001" +
                    "\u0000\u0000\u0000\u0085\u0086\u0003\u0016\u000b\u0000\u0086\u0087\u0006" +
                    "\f\uffff\uffff\u0000\u0087\u0088\u0003\u0010\b\u0000\u0088\u008c\u0006" +
                    "\f\uffff\uffff\u0000\u0089\u008a\u0003\u0014\n\u0000\u008a\u008b\u0006" +
                    "\f\uffff\uffff\u0000\u008b\u008d\u0001\u0000\u0000\u0000\u008c\u0089\u0001" +
                    "\u0000\u0000\u0000\u008c\u008d\u0001\u0000\u0000\u0000\u008d\u0091\u0001" +
                    "\u0000\u0000\u0000\u008e\u008f\u0003\u0012\t\u0000\u008f\u0090\u0006\f" +
                    "\uffff\uffff\u0000\u0090\u0092\u0001\u0000\u0000\u0000\u0091\u008e\u0001" +
                    "\u0000\u0000\u0000\u0091\u0092\u0001\u0000\u0000\u0000\u0092\u0093\u0001" +
                    "\u0000\u0000\u0000\u0093\u0094\u0003\b\u0004\u0000\u0094\u0095\u0006\f" +
                    "\uffff\uffff\u0000\u0095\u0097\u0001\u0000\u0000\u0000\u0096W\u0001\u0000" +
                    "\u0000\u0000\u0096r\u0001\u0000\u0000\u0000\u0096\u0085\u0001\u0000\u0000" +
                    "\u0000\u0097\u0019\u0001\u0000\u0000\u0000\u0098\u0099\u0003\u0018\f\u0000" +
                    "\u0099\u009a\u0006\r\uffff\uffff\u0000\u009a\u009c\u0001\u0000\u0000\u0000" +
                    "\u009b\u0098\u0001\u0000\u0000\u0000\u009c\u009d\u0001\u0000\u0000\u0000" +
                    "\u009d\u009b\u0001\u0000\u0000\u0000\u009d\u009e\u0001\u0000\u0000\u0000" +
                    "\u009e\u009f\u0001\u0000\u0000\u0000\u009f\u00a0\u0005\u0000\u0000\u0001" +
                    "\u00a0\u001b\u0001\u0000\u0000\u0000\r\u001f37dipw|\u0083\u008c\u0091" +
                    "\u0096\u009d";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}