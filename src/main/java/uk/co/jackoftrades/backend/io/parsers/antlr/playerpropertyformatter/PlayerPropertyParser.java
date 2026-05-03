// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/PlayerProperty.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.playerpropertyformatter;

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
import uk.co.jackoftrades.middle.enums.StatElementEnum;
import uk.co.jackoftrades.middle.game.Game;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.PlayerProperty;
import uk.co.jackoftrades.middle.player.PlayerProperty.PlayerPropertyType;
import uk.co.jackoftrades.middle.player.PlayerProperty.PlayerPropertyValue;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class PlayerPropertyParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, TYPE = 3, CODE = 4, BINDUI = 5, NAME = 6, DESC = 7, VALUE = 8, NUMBER = 9,
            SPECIAL = 10, COLON = 11, STRING = 12, BINDUITAG = 13, MINUS = 14;
    public static final int
            RULE_type = 0, RULE_code = 1, RULE_bindui = 2, RULE_name = 3, RULE_desc = 4,
            RULE_value = 5, RULE_playerProperty = 6, RULE_playerProperties = 7;

    private static String[] makeRuleNames() {
        return new String[]{
                "type", "code", "bindui", "name", "desc", "value", "playerProperty",
                "playerProperties"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'type:'", "'code:'", "'bindui:'", "'name:'", "'desc:'",
                "'value:'", null, "'special'", "':'", null, null, "'-'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "TYPE", "CODE", "BINDUI", "NAME", "DESC", "VALUE",
                "NUMBER", "SPECIAL", "COLON", "STRING", "BINDUITAG", "MINUS"
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
        return "PlayerProperty.g4";
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

    public PlayerPropertyParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class TypeContext extends ParserRuleContext {
        public PlayerPropertyType ppType;
        public Token STRING;

        public TerminalNode TYPE() {
            return getToken(PlayerPropertyParser.TYPE, 0);
        }

        public TerminalNode STRING() {
            return getToken(PlayerPropertyParser.STRING, 0);
        }

        public TypeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_type;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).enterType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).exitType(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyVisitor)
                return ((PlayerPropertyVisitor<? extends T>) visitor).visitType(this);
            else return visitor.visitChildren(this);
        }
    }

    public final TypeContext type() throws RecognitionException {
        TypeContext _localctx = new TypeContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_type);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(16);
                match(TYPE);
                setState(17);
                ((TypeContext) _localctx).STRING = match(STRING);

                String ppt = "PROP_TYPE_" + ((TypeContext) _localctx).STRING.getText().toUpperCase();
                ((TypeContext) _localctx).ppType = PlayerPropertyType.valueOf(ppt);

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
        public ObjectFlag objectFlag;
        public PlayerFlag playerFlag;
        public Token STRING;

        public TerminalNode CODE() {
            return getToken(PlayerPropertyParser.CODE, 0);
        }

        public TerminalNode STRING() {
            return getToken(PlayerPropertyParser.STRING, 0);
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
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).enterCode(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).exitCode(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyVisitor)
                return ((PlayerPropertyVisitor<? extends T>) visitor).visitCode(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CodeContext code() throws RecognitionException {
        CodeContext _localctx = new CodeContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_code);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(20);
                match(CODE);
                setState(21);
                ((CodeContext) _localctx).STRING = match(STRING);

                String flag = ((CodeContext) _localctx).STRING.getText();
                ObjectFlag oFlag = ObjectFlag.OF_NONE;
                PlayerFlag pFlag = PlayerFlag.PF_NONE;
                boolean isObjectFlag;
                try {
                    ((CodeContext) _localctx).objectFlag = ObjectFlag.valueOf("OF_" + flag);
                    isObjectFlag = true;
                } catch (Exception e) {
                    isObjectFlag = false;
                }

                if (isObjectFlag) {
                    ((CodeContext) _localctx).objectFlag = ObjectFlag.valueOf("OF_" + flag);
                    ((CodeContext) _localctx).playerFlag = pFlag;
                } else {
                    ((CodeContext) _localctx).objectFlag = oFlag;
                    ((CodeContext) _localctx).playerFlag = PlayerFlag.valueOf("PF_" + flag);
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
    public static class BinduiContext extends ParserRuleContext {
        public UIEntry uiEntry;
        public StatElementEnum tab;
        public boolean passType;
        public PlayerPropertyValue passValue;
        public boolean special;
        public Token STRING;
        public Token uiEntryTAG;
        public Token passTypeNum;
        public Token SPECIAL;
        public Token neg;
        public Token valNum;

        public TerminalNode BINDUI() {
            return getToken(PlayerPropertyParser.BINDUI, 0);
        }

        public TerminalNode STRING() {
            return getToken(PlayerPropertyParser.STRING, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(PlayerPropertyParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(PlayerPropertyParser.COLON, i);
        }

        public List<TerminalNode> NUMBER() {
            return getTokens(PlayerPropertyParser.NUMBER);
        }

        public TerminalNode NUMBER(int i) {
            return getToken(PlayerPropertyParser.NUMBER, i);
        }

        public TerminalNode SPECIAL() {
            return getToken(PlayerPropertyParser.SPECIAL, 0);
        }

        public TerminalNode BINDUITAG() {
            return getToken(PlayerPropertyParser.BINDUITAG, 0);
        }

        public TerminalNode MINUS() {
            return getToken(PlayerPropertyParser.MINUS, 0);
        }

        public BinduiContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_bindui;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).enterBindui(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).exitBindui(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyVisitor)
                return ((PlayerPropertyVisitor<? extends T>) visitor).visitBindui(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BinduiContext bindui() throws RecognitionException {
        BinduiContext _localctx = new BinduiContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_bindui);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(24);
                match(BINDUI);
                setState(25);
                ((BinduiContext) _localctx).STRING = match(STRING);
                setState(27);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == BINDUITAG) {
                    {
                        setState(26);
                        ((BinduiContext) _localctx).uiEntryTAG = match(BINDUITAG);
                    }
                }

                setState(29);
                match(COLON);
                setState(30);
                ((BinduiContext) _localctx).passTypeNum = match(NUMBER);
                setState(31);
                match(COLON);
                setState(37);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case SPECIAL: {
                        setState(32);
                        ((BinduiContext) _localctx).SPECIAL = match(SPECIAL);
                    }
                    break;
                    case NUMBER:
                    case MINUS: {
                        {
                            setState(34);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                            if (_la == MINUS) {
                                {
                                    setState(33);
                                    ((BinduiContext) _localctx).neg = match(MINUS);
                                }
                            }

                            setState(36);
                            ((BinduiContext) _localctx).valNum = match(NUMBER);
                        }
                    }
                    break;
                    default:
                        throw new NoViableAltException(this);
                }

                String uiEntryName = ((BinduiContext) _localctx).STRING.getText();
                ((BinduiContext) _localctx).uiEntry = Game.getUIEntry(uiEntryName);

                if (((BinduiContext) _localctx).uiEntryTAG != null) {
                    String tag = ((BinduiContext) _localctx).uiEntryTAG.getText().toUpperCase();
                    String innerTag = tag.substring(1, tag.length() - 1);
                    try {
                        ((BinduiContext) _localctx).tab = StatElementEnum.valueOf("STAT_" + innerTag);
                    } catch (Exception e) {
                        ((BinduiContext) _localctx).tab = StatElementEnum.valueOf("ELEM_" + innerTag);
                    }
                }

                int negate = 1;

                if (((BinduiContext) _localctx).neg != null) {
                    negate = -1;
                }

                ((BinduiContext) _localctx).passType = (Integer.parseInt(((BinduiContext) _localctx).passTypeNum.getText()) != 0);

                if (((BinduiContext) _localctx).SPECIAL != null) {
                    ((BinduiContext) _localctx).special = true;
                    ((BinduiContext) _localctx).passValue = PlayerPropertyValue.NONE;
                } else {
                    ((BinduiContext) _localctx).special = false;
                    int passVal = Integer.parseInt(((BinduiContext) _localctx).valNum.getText());
                    if (passVal == 3)
                        ((BinduiContext) _localctx).passValue = PlayerPropertyValue.IMMUNITY;
                    else {
                        if (negate == -1)
                            ((BinduiContext) _localctx).passValue = PlayerPropertyValue.VULNERABILITY;
                        else
                            ((BinduiContext) _localctx).passValue = PlayerPropertyValue.RESISTANCE;
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
    public static class NameContext extends ParserRuleContext {
        public String propertyName;
        public Token STRING;

        public TerminalNode NAME() {
            return getToken(PlayerPropertyParser.NAME, 0);
        }

        public TerminalNode STRING() {
            return getToken(PlayerPropertyParser.STRING, 0);
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
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyVisitor)
                return ((PlayerPropertyVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(41);
                match(NAME);
                setState(42);
                ((NameContext) _localctx).STRING = match(STRING);

                ((NameContext) _localctx).propertyName = ((NameContext) _localctx).STRING.getText();

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
        public Token STRING;

        public List<TerminalNode> DESC() {
            return getTokens(PlayerPropertyParser.DESC);
        }

        public TerminalNode DESC(int i) {
            return getToken(PlayerPropertyParser.DESC, i);
        }

        public List<TerminalNode> STRING() {
            return getTokens(PlayerPropertyParser.STRING);
        }

        public TerminalNode STRING(int i) {
            return getToken(PlayerPropertyParser.STRING, i);
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
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).enterDesc(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).exitDesc(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyVisitor)
                return ((PlayerPropertyVisitor<? extends T>) visitor).visitDesc(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DescContext desc() throws RecognitionException {
        DescContext _localctx = new DescContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_desc);

        ((DescContext) _localctx).description = "";

        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(48);
                _errHandler.sync(this);
                _alt = 1;
                do {
                    switch (_alt) {
                        case 1: {
                            {
                                setState(45);
                                match(DESC);
                                setState(46);
                                ((DescContext) _localctx).STRING = match(STRING);

                                ((DescContext) _localctx).description = _localctx.description + ((DescContext) _localctx).STRING.getText();

                            }
                        }
                        break;
                        default:
                            throw new NoViableAltException(this);
                    }
                    setState(50);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 3, _ctx);
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
    public static class ValueContext extends ParserRuleContext {
        public PlayerPropertyValue val;
        public Token MINUS;
        public Token NUMBER;

        public TerminalNode VALUE() {
            return getToken(PlayerPropertyParser.VALUE, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(PlayerPropertyParser.NUMBER, 0);
        }

        public TerminalNode MINUS() {
            return getToken(PlayerPropertyParser.MINUS, 0);
        }

        public ValueContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_value;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).enterValue(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyListener) ((PlayerPropertyListener) listener).exitValue(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyVisitor)
                return ((PlayerPropertyVisitor<? extends T>) visitor).visitValue(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ValueContext value() throws RecognitionException {
        ValueContext _localctx = new ValueContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_value);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(52);
                match(VALUE);
                setState(54);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == MINUS) {
                    {
                        setState(53);
                        ((ValueContext) _localctx).MINUS = match(MINUS);
                    }
                }

                setState(56);
                ((ValueContext) _localctx).NUMBER = match(NUMBER);

                if (((ValueContext) _localctx).MINUS != null) {
                    ((ValueContext) _localctx).val = PlayerPropertyValue.VULNERABILITY;
                } else {
                    if (Integer.parseInt(((ValueContext) _localctx).NUMBER.getText()) == 1)
                        ((ValueContext) _localctx).val = PlayerPropertyValue.RESISTANCE;
                    else
                        ((ValueContext) _localctx).val = PlayerPropertyValue.IMMUNITY;
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
    public static class PlayerPropertyContext extends ParserRuleContext {
        public PlayerProperty property;
        public TypeContext type;
        public CodeContext code;
        public BinduiContext bindui;
        public NameContext name;
        public DescContext desc;
        public ValueContext value;

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public CodeContext code() {
            return getRuleContext(CodeContext.class, 0);
        }

        public BinduiContext bindui() {
            return getRuleContext(BinduiContext.class, 0);
        }

        public List<DescContext> desc() {
            return getRuleContexts(DescContext.class);
        }

        public DescContext desc(int i) {
            return getRuleContext(DescContext.class, i);
        }

        public ValueContext value() {
            return getRuleContext(ValueContext.class, 0);
        }

        public PlayerPropertyContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_playerProperty;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyListener)
                ((PlayerPropertyListener) listener).enterPlayerProperty(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyListener)
                ((PlayerPropertyListener) listener).exitPlayerProperty(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyVisitor)
                return ((PlayerPropertyVisitor<? extends T>) visitor).visitPlayerProperty(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PlayerPropertyContext playerProperty() throws RecognitionException {
        PlayerPropertyContext _localctx = new PlayerPropertyContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_playerProperty);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(59);
                ((PlayerPropertyContext) _localctx).type = type();
                setState(61);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == CODE) {
                    {
                        setState(60);
                        ((PlayerPropertyContext) _localctx).code = code();
                    }
                }

                setState(64);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == BINDUI) {
                    {
                        setState(63);
                        ((PlayerPropertyContext) _localctx).bindui = bindui();
                    }
                }

                setState(66);
                ((PlayerPropertyContext) _localctx).name = name();
                setState(70);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == DESC) {
                    {
                        {
                            setState(67);
                            ((PlayerPropertyContext) _localctx).desc = desc();
                        }
                    }
                    setState(72);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(74);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == VALUE) {
                    {
                        setState(73);
                        ((PlayerPropertyContext) _localctx).value = value();
                    }
                }


                PlayerPropertyType ppType = ((PlayerPropertyContext) _localctx).type.ppType;

                ObjectFlag oCode;
                PlayerFlag pCode;
                if (((PlayerPropertyContext) _localctx).code != null) {
                    oCode = ((PlayerPropertyContext) _localctx).code.objectFlag;
                    pCode = ((PlayerPropertyContext) _localctx).code.playerFlag;
                } else {
                    oCode = ObjectFlag.OF_NONE;
                    pCode = PlayerFlag.PF_NONE;
                }

                UIEntry ent = null;
                StatElementEnum statElement = null;
                boolean passType = false;
                PlayerPropertyValue passValue = null;
                boolean special = false;
                if (((PlayerPropertyContext) _localctx).bindui != null) {
                    ent = ((PlayerPropertyContext) _localctx).bindui.uiEntry;
                    statElement = ((PlayerPropertyContext) _localctx).bindui.tab;
                    passType = ((PlayerPropertyContext) _localctx).bindui.passType;
                    passValue = ((PlayerPropertyContext) _localctx).bindui.passValue;
                    special = ((PlayerPropertyContext) _localctx).bindui.special;
                }

                String playerName = ((PlayerPropertyContext) _localctx).name.propertyName;
                String description = ((PlayerPropertyContext) _localctx).desc.description;

                PlayerPropertyValue val = null;
                if (((PlayerPropertyContext) _localctx).value != null) {
                    val = ((PlayerPropertyContext) _localctx).value.val;
                }

                ((PlayerPropertyContext) _localctx).property = new PlayerProperty(ppType, pCode, oCode, ent,
                        statElement, passType, passValue,
                        special, playerName, description,
                        val);

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
    public static class PlayerPropertiesContext extends ParserRuleContext {
        public ArrayList<PlayerProperty> properties;
        public PlayerPropertyContext playerProperty;

        public TerminalNode EOF() {
            return getToken(PlayerPropertyParser.EOF, 0);
        }

        public List<PlayerPropertyContext> playerProperty() {
            return getRuleContexts(PlayerPropertyContext.class);
        }

        public PlayerPropertyContext playerProperty(int i) {
            return getRuleContext(PlayerPropertyContext.class, i);
        }

        public PlayerPropertiesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_playerProperties;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyListener)
                ((PlayerPropertyListener) listener).enterPlayerProperties(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerPropertyListener)
                ((PlayerPropertyListener) listener).exitPlayerProperties(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerPropertyVisitor)
                return ((PlayerPropertyVisitor<? extends T>) visitor).visitPlayerProperties(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PlayerPropertiesContext playerProperties() throws RecognitionException {
        PlayerPropertiesContext _localctx = new PlayerPropertiesContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_playerProperties);

        ((PlayerPropertiesContext) _localctx).properties = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(81);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(78);
                            ((PlayerPropertiesContext) _localctx).playerProperty = playerProperty();

                            _localctx.properties.add(((PlayerPropertiesContext) _localctx).playerProperty.property);

                        }
                    }
                    setState(83);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == TYPE);
                setState(85);
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
            "\u0004\u0001\u000eX\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002\u001c" +
                    "\b\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003" +
                    "\u0002#\b\u0002\u0001\u0002\u0003\u0002&\b\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0004\u00041\b\u0004\u000b\u0004\f\u00042\u0001\u0005\u0001" +
                    "\u0005\u0003\u00057\b\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0006\u0001\u0006\u0003\u0006>\b\u0006\u0001\u0006\u0003\u0006A\b\u0006" +
                    "\u0001\u0006\u0001\u0006\u0005\u0006E\b\u0006\n\u0006\f\u0006H\t\u0006" +
                    "\u0001\u0006\u0003\u0006K\b\u0006\u0001\u0006\u0001\u0006\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0004\u0007R\b\u0007\u000b\u0007\f\u0007S\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0000\u0000\b\u0000\u0002\u0004\u0006\b" +
                    "\n\f\u000e\u0000\u0000Y\u0000\u0010\u0001\u0000\u0000\u0000\u0002\u0014" +
                    "\u0001\u0000\u0000\u0000\u0004\u0018\u0001\u0000\u0000\u0000\u0006)\u0001" +
                    "\u0000\u0000\u0000\b0\u0001\u0000\u0000\u0000\n4\u0001\u0000\u0000\u0000" +
                    "\f;\u0001\u0000\u0000\u0000\u000eQ\u0001\u0000\u0000\u0000\u0010\u0011" +
                    "\u0005\u0003\u0000\u0000\u0011\u0012\u0005\f\u0000\u0000\u0012\u0013\u0006" +
                    "\u0000\uffff\uffff\u0000\u0013\u0001\u0001\u0000\u0000\u0000\u0014\u0015" +
                    "\u0005\u0004\u0000\u0000\u0015\u0016\u0005\f\u0000\u0000\u0016\u0017\u0006" +
                    "\u0001\uffff\uffff\u0000\u0017\u0003\u0001\u0000\u0000\u0000\u0018\u0019" +
                    "\u0005\u0005\u0000\u0000\u0019\u001b\u0005\f\u0000\u0000\u001a\u001c\u0005" +
                    "\r\u0000\u0000\u001b\u001a\u0001\u0000\u0000\u0000\u001b\u001c\u0001\u0000" +
                    "\u0000\u0000\u001c\u001d\u0001\u0000\u0000\u0000\u001d\u001e\u0005\u000b" +
                    "\u0000\u0000\u001e\u001f\u0005\t\u0000\u0000\u001f%\u0005\u000b\u0000" +
                    "\u0000 &\u0005\n\u0000\u0000!#\u0005\u000e\u0000\u0000\"!\u0001\u0000" +
                    "\u0000\u0000\"#\u0001\u0000\u0000\u0000#$\u0001\u0000\u0000\u0000$&\u0005" +
                    "\t\u0000\u0000% \u0001\u0000\u0000\u0000%\"\u0001\u0000\u0000\u0000&\'" +
                    "\u0001\u0000\u0000\u0000\'(\u0006\u0002\uffff\uffff\u0000(\u0005\u0001" +
                    "\u0000\u0000\u0000)*\u0005\u0006\u0000\u0000*+\u0005\f\u0000\u0000+,\u0006" +
                    "\u0003\uffff\uffff\u0000,\u0007\u0001\u0000\u0000\u0000-.\u0005\u0007" +
                    "\u0000\u0000./\u0005\f\u0000\u0000/1\u0006\u0004\uffff\uffff\u00000-\u0001" +
                    "\u0000\u0000\u000012\u0001\u0000\u0000\u000020\u0001\u0000\u0000\u0000" +
                    "23\u0001\u0000\u0000\u00003\t\u0001\u0000\u0000\u000046\u0005\b\u0000" +
                    "\u000057\u0005\u000e\u0000\u000065\u0001\u0000\u0000\u000067\u0001\u0000" +
                    "\u0000\u000078\u0001\u0000\u0000\u000089\u0005\t\u0000\u00009:\u0006\u0005" +
                    "\uffff\uffff\u0000:\u000b\u0001\u0000\u0000\u0000;=\u0003\u0000\u0000" +
                    "\u0000<>\u0003\u0002\u0001\u0000=<\u0001\u0000\u0000\u0000=>\u0001\u0000" +
                    "\u0000\u0000>@\u0001\u0000\u0000\u0000?A\u0003\u0004\u0002\u0000@?\u0001" +
                    "\u0000\u0000\u0000@A\u0001\u0000\u0000\u0000AB\u0001\u0000\u0000\u0000" +
                    "BF\u0003\u0006\u0003\u0000CE\u0003\b\u0004\u0000DC\u0001\u0000\u0000\u0000" +
                    "EH\u0001\u0000\u0000\u0000FD\u0001\u0000\u0000\u0000FG\u0001\u0000\u0000" +
                    "\u0000GJ\u0001\u0000\u0000\u0000HF\u0001\u0000\u0000\u0000IK\u0003\n\u0005" +
                    "\u0000JI\u0001\u0000\u0000\u0000JK\u0001\u0000\u0000\u0000KL\u0001\u0000" +
                    "\u0000\u0000LM\u0006\u0006\uffff\uffff\u0000M\r\u0001\u0000\u0000\u0000" +
                    "NO\u0003\f\u0006\u0000OP\u0006\u0007\uffff\uffff\u0000PR\u0001\u0000\u0000" +
                    "\u0000QN\u0001\u0000\u0000\u0000RS\u0001\u0000\u0000\u0000SQ\u0001\u0000" +
                    "\u0000\u0000ST\u0001\u0000\u0000\u0000TU\u0001\u0000\u0000\u0000UV\u0005" +
                    "\u0000\u0000\u0001V\u000f\u0001\u0000\u0000\u0000\n\u001b\"%26=@FJS";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}