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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/ObjectBase.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.objectbase;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.objects.ObjectBase;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ObjectBaseParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            T__0 = 1, COMMENT = 2, EOL = 3, DEFAULT_MAX_STACK = 4, DEFAULT_BREAK_CHANCE = 5,
            NAME = 6, GRAPHICS = 7, BREAK = 8, MAX_STACK = 9, FLAGS = 10, NUMBER = 11, COLON = 12,
            TEXT = 13;
    public static final int
            RULE_default_value = 0, RULE_name = 1, RULE_graphics = 2, RULE_break_chance = 3,
            RULE_max_stack = 4, RULE_flags = 5, RULE_object_base = 6, RULE_file = 7;

    private static String[] makeRuleNames() {
        return new String[]{
                "default_value", "name", "graphics", "break_chance", "max_stack", "flags",
                "object_base", "file"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, "'|'", null, null, "'default:max-stack:'", "'default:break-chance:'",
                "'name:'", "'graphics:'", "'break:'", "'max-stack:'", "'flags:'", null,
                "':'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, null, "COMMENT", "EOL", "DEFAULT_MAX_STACK", "DEFAULT_BREAK_CHANCE",
                "NAME", "GRAPHICS", "BREAK", "MAX_STACK", "FLAGS", "NUMBER", "COLON",
                "TEXT"
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
        return "ObjectBase.g4";
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

    public ObjectBaseParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class Default_valueContext extends ParserRuleContext {
        public int maxStackNum;
        public int breakChanceNum;
        public Token val1;
        public Token val2;

        public TerminalNode DEFAULT_BREAK_CHANCE() {
            return getToken(ObjectBaseParser.DEFAULT_BREAK_CHANCE, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(ObjectBaseParser.NUMBER, 0);
        }

        public TerminalNode DEFAULT_MAX_STACK() {
            return getToken(ObjectBaseParser.DEFAULT_MAX_STACK, 0);
        }

        public Default_valueContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_default_value;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseListener) ((ObjectBaseListener) listener).enterDefault_value(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseListener) ((ObjectBaseListener) listener).exitDefault_value(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectBaseVisitor)
                return ((ObjectBaseVisitor<? extends T>) visitor).visitDefault_value(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Default_valueContext default_value() throws RecognitionException {
        Default_valueContext _localctx = new Default_valueContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_default_value);

        ((Default_valueContext) _localctx).maxStackNum = -1;
        ((Default_valueContext) _localctx).breakChanceNum = -1;

        try {
            setState(22);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case DEFAULT_BREAK_CHANCE:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(16);
                    match(DEFAULT_BREAK_CHANCE);
                    setState(17);
                    ((Default_valueContext) _localctx).val1 = match(NUMBER);

                    ((Default_valueContext) _localctx).breakChanceNum = Integer.parseInt(((Default_valueContext) _localctx).val1.getText());

                }
                break;
                case DEFAULT_MAX_STACK:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(19);
                    match(DEFAULT_MAX_STACK);
                    setState(20);
                    ((Default_valueContext) _localctx).val2 = match(NUMBER);

                    ((Default_valueContext) _localctx).maxStackNum = Integer.parseInt(((Default_valueContext) _localctx).val2.getText());

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
        public TValue tValue;
        public String nameStr;
        public Token tval1;
        public Token nameIn;
        public Token tval2;

        public TerminalNode NAME() {
            return getToken(ObjectBaseParser.NAME, 0);
        }

        public TerminalNode COLON() {
            return getToken(ObjectBaseParser.COLON, 0);
        }

        public List<TerminalNode> TEXT() {
            return getTokens(ObjectBaseParser.TEXT);
        }

        public TerminalNode TEXT(int i) {
            return getToken(ObjectBaseParser.TEXT, i);
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
            if (listener instanceof ObjectBaseListener) ((ObjectBaseListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseListener) ((ObjectBaseListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectBaseVisitor) return ((ObjectBaseVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_name);
        try {
            setState(34);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 1, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    {
                        setState(24);
                        match(NAME);
                        setState(25);
                        ((NameContext) _localctx).tval1 = match(TEXT);
                        setState(26);
                        match(COLON);
                        setState(27);
                        ((NameContext) _localctx).nameIn = match(TEXT);
                    }

                    String raw1 = ((NameContext) _localctx).tval1.getText().toUpperCase().trim();
                    // Deal with the two special flags
                    if (raw1.equals("DIGGER"))
                        raw1 = "DIGGING";
                    if (raw1.equals("DRAGON ARMOR"))
                        raw1 = "DRAG ARMOR";
                    raw1 = raw1.replace(' ', '_');
                    ((NameContext) _localctx).tValue = TValue.valueOf("TV_" + raw1);
                    ((NameContext) _localctx).nameStr = ((NameContext) _localctx).nameIn.getText();

                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    {
                        setState(30);
                        match(NAME);
                        setState(31);
                        ((NameContext) _localctx).tval2 = match(TEXT);
                    }

                    String raw2 = ((NameContext) _localctx).tval2.getText().toUpperCase().trim();
                    // Deal with the two special flags
                    if (raw2.equals("DIGGER"))
                        raw2 = "DIGGING";
                    if (raw2.equals("DRAGON ARMOR"))
                        raw2 = "DRAG ARMOR";
                    raw2 = raw2.replace(' ', '_');
                    ((NameContext) _localctx).tValue = TValue.valueOf("TV_" + raw2);

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
    public static class GraphicsContext extends ParserRuleContext {
        public ColourType graphicsColour;
        public Token TEXT;

        public TerminalNode GRAPHICS() {
            return getToken(ObjectBaseParser.GRAPHICS, 0);
        }

        public TerminalNode TEXT() {
            return getToken(ObjectBaseParser.TEXT, 0);
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
            if (listener instanceof ObjectBaseListener) ((ObjectBaseListener) listener).enterGraphics(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseListener) ((ObjectBaseListener) listener).exitGraphics(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectBaseVisitor)
                return ((ObjectBaseVisitor<? extends T>) visitor).visitGraphics(this);
            else return visitor.visitChildren(this);
        }
    }

    public final GraphicsContext graphics() throws RecognitionException {
        GraphicsContext _localctx = new GraphicsContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_graphics);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(36);
                match(GRAPHICS);
                setState(37);
                ((GraphicsContext) _localctx).TEXT = match(TEXT);

                ((GraphicsContext) _localctx).graphicsColour = ColourType.getColourType(((GraphicsContext) _localctx).TEXT.getText());

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
    public static class Break_chanceContext extends ParserRuleContext {
        public int breakChance;
        public Token NUMBER;

        public TerminalNode BREAK() {
            return getToken(ObjectBaseParser.BREAK, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(ObjectBaseParser.NUMBER, 0);
        }

        public Break_chanceContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_break_chance;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseListener) ((ObjectBaseListener) listener).enterBreak_chance(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseListener) ((ObjectBaseListener) listener).exitBreak_chance(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectBaseVisitor)
                return ((ObjectBaseVisitor<? extends T>) visitor).visitBreak_chance(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Break_chanceContext break_chance() throws RecognitionException {
        Break_chanceContext _localctx = new Break_chanceContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_break_chance);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(40);
                match(BREAK);
                setState(41);
                ((Break_chanceContext) _localctx).NUMBER = match(NUMBER);

                ((Break_chanceContext) _localctx).breakChance = Integer.parseInt(((Break_chanceContext) _localctx).NUMBER.getText());

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
    public static class Max_stackContext extends ParserRuleContext {
        public int maxStack;
        public Token NUMBER;

        public TerminalNode MAX_STACK() {
            return getToken(ObjectBaseParser.MAX_STACK, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(ObjectBaseParser.NUMBER, 0);
        }

        public Max_stackContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_max_stack;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseListener) ((ObjectBaseListener) listener).enterMax_stack(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseListener) ((ObjectBaseListener) listener).exitMax_stack(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectBaseVisitor)
                return ((ObjectBaseVisitor<? extends T>) visitor).visitMax_stack(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Max_stackContext max_stack() throws RecognitionException {
        Max_stackContext _localctx = new Max_stackContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_max_stack);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(44);
                match(MAX_STACK);
                setState(45);
                ((Max_stackContext) _localctx).NUMBER = match(NUMBER);

                ((Max_stackContext) _localctx).maxStack = Integer.parseInt(((Max_stackContext) _localctx).NUMBER.getText());

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
        public List<String> flagsList;
        public Token flag1;
        public Token flag2;

        public TerminalNode FLAGS() {
            return getToken(ObjectBaseParser.FLAGS, 0);
        }

        public List<TerminalNode> TEXT() {
            return getTokens(ObjectBaseParser.TEXT);
        }

        public TerminalNode TEXT(int i) {
            return getToken(ObjectBaseParser.TEXT, i);
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
            if (listener instanceof ObjectBaseListener) ((ObjectBaseListener) listener).enterFlags(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseListener) ((ObjectBaseListener) listener).exitFlags(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectBaseVisitor)
                return ((ObjectBaseVisitor<? extends T>) visitor).visitFlags(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FlagsContext flags() throws RecognitionException {
        FlagsContext _localctx = new FlagsContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_flags);

        ((FlagsContext) _localctx).flagsList = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(48);
                match(FLAGS);
                setState(49);
                ((FlagsContext) _localctx).flag1 = match(TEXT);

                _localctx.flagsList.add(((FlagsContext) _localctx).flag1.getText().trim());

                setState(56);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == T__0) {
                    {
                        {
                            setState(51);
                            match(T__0);
                            setState(52);
                            ((FlagsContext) _localctx).flag2 = match(TEXT);

                            _localctx.flagsList.add(((FlagsContext) _localctx).flag2.getText().trim());

                        }
                    }
                    setState(58);
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
    public static class Object_baseContext extends ParserRuleContext {
        public ObjectBase objectBase;
        public NameContext name;
        public GraphicsContext graphics;
        public Break_chanceContext break_chance;
        public Max_stackContext max_stack;
        public FlagsContext flags;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public GraphicsContext graphics() {
            return getRuleContext(GraphicsContext.class, 0);
        }

        public Break_chanceContext break_chance() {
            return getRuleContext(Break_chanceContext.class, 0);
        }

        public Max_stackContext max_stack() {
            return getRuleContext(Max_stackContext.class, 0);
        }

        public List<FlagsContext> flags() {
            return getRuleContexts(FlagsContext.class);
        }

        public FlagsContext flags(int i) {
            return getRuleContext(FlagsContext.class, i);
        }

        public Object_baseContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_object_base;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseListener) ((ObjectBaseListener) listener).enterObject_base(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseListener) ((ObjectBaseListener) listener).exitObject_base(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectBaseVisitor)
                return ((ObjectBaseVisitor<? extends T>) visitor).visitObject_base(this);
            else return visitor.visitChildren(this);
        }
    }

    public final Object_baseContext object_base() throws RecognitionException {
        Object_baseContext _localctx = new Object_baseContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_object_base);

        ((Object_baseContext) _localctx).objectBase = new ObjectBase();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(59);
                ((Object_baseContext) _localctx).name = name();

                _localctx.objectBase.setName(((Object_baseContext) _localctx).name.nameStr);
                _localctx.objectBase.settVal(((Object_baseContext) _localctx).name.tValue);

                setState(61);
                ((Object_baseContext) _localctx).graphics = graphics();

                _localctx.objectBase.setAttr(((Object_baseContext) _localctx).graphics.graphicsColour);

                setState(66);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == BREAK) {
                    {
                        setState(63);
                        ((Object_baseContext) _localctx).break_chance = break_chance();

                        _localctx.objectBase.setBreakPerc(((Object_baseContext) _localctx).break_chance.breakChance);

                    }
                }

                setState(71);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == MAX_STACK) {
                    {
                        setState(68);
                        ((Object_baseContext) _localctx).max_stack = max_stack();

                        _localctx.objectBase.setMaxStack(((Object_baseContext) _localctx).max_stack.maxStack);

                    }
                }

                setState(78);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == FLAGS) {
                    {
                        {
                            setState(73);
                            ((Object_baseContext) _localctx).flags = flags();

                            _localctx.objectBase.setFlags(((Object_baseContext) _localctx).flags.flagsList);

                        }
                    }
                    setState(80);
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
        public List<ObjectBase> objectBaseList;
        public Default_valueContext default_value;
        public Object_baseContext object_base;

        public TerminalNode EOF() {
            return getToken(ObjectBaseParser.EOF, 0);
        }

        public List<Default_valueContext> default_value() {
            return getRuleContexts(Default_valueContext.class);
        }

        public Default_valueContext default_value(int i) {
            return getRuleContext(Default_valueContext.class, i);
        }

        public List<Object_baseContext> object_base() {
            return getRuleContexts(Object_baseContext.class);
        }

        public Object_baseContext object_base(int i) {
            return getRuleContext(Object_baseContext.class, i);
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
            if (listener instanceof ObjectBaseListener) ((ObjectBaseListener) listener).enterFile(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof ObjectBaseListener) ((ObjectBaseListener) listener).exitFile(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof ObjectBaseVisitor) return ((ObjectBaseVisitor<? extends T>) visitor).visitFile(this);
            else return visitor.visitChildren(this);
        }
    }

    public final FileContext file() throws RecognitionException {
        FileContext _localctx = new FileContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_file);

        ((FileContext) _localctx).objectBaseList = new ArrayList<>();
        int defaultMaxStack = 0;
        int defaultBreakChance = 0;

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(84);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(81);
                            ((FileContext) _localctx).default_value = default_value();

                            if (((FileContext) _localctx).default_value.maxStackNum == -1)
                                defaultBreakChance = ((FileContext) _localctx).default_value.breakChanceNum;
                            else
                                defaultMaxStack = ((FileContext) _localctx).default_value.maxStackNum;

                        }
                    }
                    setState(86);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == DEFAULT_MAX_STACK || _la == DEFAULT_BREAK_CHANCE);
                setState(91);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(88);
                            ((FileContext) _localctx).object_base = object_base();

                            ObjectBase base = ((FileContext) _localctx).object_base.objectBase;
                            // put in the default values if required
                            if (base.getBreakPerc() == -1)
                                base.setBreakPerc(defaultBreakChance);

                            if (base.getMaxStack() == -1)
                                base.setMaxStack(defaultMaxStack);

                            // add it to the list
                            _localctx.objectBaseList.add(base);

                        }
                    }
                    setState(93);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(95);
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
            "\u0004\u0001\rb\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0001" +
                    "\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0003" +
                    "\u0000\u0017\b\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0003" +
                    "\u0001#\b\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0005\u0005\u00057\b\u0005\n\u0005\f\u0005:\t\u0005\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0006\u0003\u0006C\b\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003" +
                    "\u0006H\b\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0005\u0006M\b\u0006" +
                    "\n\u0006\f\u0006P\t\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0004\u0007" +
                    "U\b\u0007\u000b\u0007\f\u0007V\u0001\u0007\u0001\u0007\u0001\u0007\u0004" +
                    "\u0007\\\b\u0007\u000b\u0007\f\u0007]\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0000\u0000\b\u0000\u0002\u0004\u0006\b\n\f\u000e\u0000\u0000a\u0000" +
                    "\u0016\u0001\u0000\u0000\u0000\u0002\"\u0001\u0000\u0000\u0000\u0004$" +
                    "\u0001\u0000\u0000\u0000\u0006(\u0001\u0000\u0000\u0000\b,\u0001\u0000" +
                    "\u0000\u0000\n0\u0001\u0000\u0000\u0000\f;\u0001\u0000\u0000\u0000\u000e" +
                    "T\u0001\u0000\u0000\u0000\u0010\u0011\u0005\u0005\u0000\u0000\u0011\u0012" +
                    "\u0005\u000b\u0000\u0000\u0012\u0017\u0006\u0000\uffff\uffff\u0000\u0013" +
                    "\u0014\u0005\u0004\u0000\u0000\u0014\u0015\u0005\u000b\u0000\u0000\u0015" +
                    "\u0017\u0006\u0000\uffff\uffff\u0000\u0016\u0010\u0001\u0000\u0000\u0000" +
                    "\u0016\u0013\u0001\u0000\u0000\u0000\u0017\u0001\u0001\u0000\u0000\u0000" +
                    "\u0018\u0019\u0005\u0006\u0000\u0000\u0019\u001a\u0005\r\u0000\u0000\u001a" +
                    "\u001b\u0005\f\u0000\u0000\u001b\u001c\u0005\r\u0000\u0000\u001c\u001d" +
                    "\u0001\u0000\u0000\u0000\u001d#\u0006\u0001\uffff\uffff\u0000\u001e\u001f" +
                    "\u0005\u0006\u0000\u0000\u001f \u0005\r\u0000\u0000 !\u0001\u0000\u0000" +
                    "\u0000!#\u0006\u0001\uffff\uffff\u0000\"\u0018\u0001\u0000\u0000\u0000" +
                    "\"\u001e\u0001\u0000\u0000\u0000#\u0003\u0001\u0000\u0000\u0000$%\u0005" +
                    "\u0007\u0000\u0000%&\u0005\r\u0000\u0000&\'\u0006\u0002\uffff\uffff\u0000" +
                    "\'\u0005\u0001\u0000\u0000\u0000()\u0005\b\u0000\u0000)*\u0005\u000b\u0000" +
                    "\u0000*+\u0006\u0003\uffff\uffff\u0000+\u0007\u0001\u0000\u0000\u0000" +
                    ",-\u0005\t\u0000\u0000-.\u0005\u000b\u0000\u0000./\u0006\u0004\uffff\uffff" +
                    "\u0000/\t\u0001\u0000\u0000\u000001\u0005\n\u0000\u000012\u0005\r\u0000" +
                    "\u000028\u0006\u0005\uffff\uffff\u000034\u0005\u0001\u0000\u000045\u0005" +
                    "\r\u0000\u000057\u0006\u0005\uffff\uffff\u000063\u0001\u0000\u0000\u0000" +
                    "7:\u0001\u0000\u0000\u000086\u0001\u0000\u0000\u000089\u0001\u0000\u0000" +
                    "\u00009\u000b\u0001\u0000\u0000\u0000:8\u0001\u0000\u0000\u0000;<\u0003" +
                    "\u0002\u0001\u0000<=\u0006\u0006\uffff\uffff\u0000=>\u0003\u0004\u0002" +
                    "\u0000>B\u0006\u0006\uffff\uffff\u0000?@\u0003\u0006\u0003\u0000@A\u0006" +
                    "\u0006\uffff\uffff\u0000AC\u0001\u0000\u0000\u0000B?\u0001\u0000\u0000" +
                    "\u0000BC\u0001\u0000\u0000\u0000CG\u0001\u0000\u0000\u0000DE\u0003\b\u0004" +
                    "\u0000EF\u0006\u0006\uffff\uffff\u0000FH\u0001\u0000\u0000\u0000GD\u0001" +
                    "\u0000\u0000\u0000GH\u0001\u0000\u0000\u0000HN\u0001\u0000\u0000\u0000" +
                    "IJ\u0003\n\u0005\u0000JK\u0006\u0006\uffff\uffff\u0000KM\u0001\u0000\u0000" +
                    "\u0000LI\u0001\u0000\u0000\u0000MP\u0001\u0000\u0000\u0000NL\u0001\u0000" +
                    "\u0000\u0000NO\u0001\u0000\u0000\u0000O\r\u0001\u0000\u0000\u0000PN\u0001" +
                    "\u0000\u0000\u0000QR\u0003\u0000\u0000\u0000RS\u0006\u0007\uffff\uffff" +
                    "\u0000SU\u0001\u0000\u0000\u0000TQ\u0001\u0000\u0000\u0000UV\u0001\u0000" +
                    "\u0000\u0000VT\u0001\u0000\u0000\u0000VW\u0001\u0000\u0000\u0000W[\u0001" +
                    "\u0000\u0000\u0000XY\u0003\f\u0006\u0000YZ\u0006\u0007\uffff\uffff\u0000" +
                    "Z\\\u0001\u0000\u0000\u0000[X\u0001\u0000\u0000\u0000\\]\u0001\u0000\u0000" +
                    "\u0000][\u0001\u0000\u0000\u0000]^\u0001\u0000\u0000\u0000^_\u0001\u0000" +
                    "\u0000\u0000_`\u0005\u0000\u0000\u0001`\u000f\u0001\u0000\u0000\u0000" +
                    "\b\u0016\"8BGNV]";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}