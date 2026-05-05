// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/BrandReader.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.brandreader;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.objects.Brand;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class BrandReaderParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, CODE = 3, NAME = 4, RESIST = 5, VULNERABLE = 6, MULTIPLIER = 7,
            O_MULTIPLIER = 8, POWER = 9, VERB = 10, NUMBER = 11, TEXT = 12;
    public static final int
            RULE_code = 0, RULE_name = 1, RULE_resistFlag = 2, RULE_vulnFlag = 3,
            RULE_multiplier = 4, RULE_oMultiplier = 5, RULE_power = 6, RULE_verb = 7,
            RULE_brand = 8, RULE_brands = 9;

    private static String[] makeRuleNames() {
        return new String[]{
                "code", "name", "resistFlag", "vulnFlag", "multiplier", "oMultiplier",
                "power", "verb", "brand", "brands"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'code:'", "'name:'", "'resist-flag:'", "'vuln-flag:'",
                "'multiplier:'", "'o-multiplier:'", "'power:'", "'verb:'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "CODE", "NAME", "RESIST", "VULNERABLE", "MULTIPLIER",
                "O_MULTIPLIER", "POWER", "VERB", "NUMBER", "TEXT"
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
        return "BrandReader.g4";
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

    public BrandReaderParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class CodeContext extends ParserRuleContext {
        public String codeStr;
        public Token TEXT;

        public TerminalNode CODE() {
            return getToken(BrandReaderParser.CODE, 0);
        }

        public TerminalNode TEXT() {
            return getToken(BrandReaderParser.TEXT, 0);
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
            if (listener instanceof BrandReaderListener) ((BrandReaderListener) listener).enterCode(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandReaderListener) ((BrandReaderListener) listener).exitCode(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandReaderVisitor)
                return ((BrandReaderVisitor<? extends T>) visitor).visitCode(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CodeContext code() throws RecognitionException {
        CodeContext _localctx = new CodeContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_code);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(20);
                match(CODE);
                setState(21);
                ((CodeContext) _localctx).TEXT = match(TEXT);

                ((CodeContext) _localctx).codeStr = ((CodeContext) _localctx).TEXT.getText();

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
        public String nameStr;
        public Token TEXT;

        public TerminalNode NAME() {
            return getToken(BrandReaderParser.NAME, 0);
        }

        public TerminalNode TEXT() {
            return getToken(BrandReaderParser.TEXT, 0);
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
            if (listener instanceof BrandReaderListener) ((BrandReaderListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandReaderListener) ((BrandReaderListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandReaderVisitor)
                return ((BrandReaderVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(24);
                match(NAME);
                setState(25);
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
    public static class ResistFlagContext extends ParserRuleContext {
        public MonsterRaceFlag resistFlagEnum;
        public Token TEXT;

        public TerminalNode RESIST() {
            return getToken(BrandReaderParser.RESIST, 0);
        }

        public TerminalNode TEXT() {
            return getToken(BrandReaderParser.TEXT, 0);
        }

        public ResistFlagContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_resistFlag;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BrandReaderListener) ((BrandReaderListener) listener).enterResistFlag(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandReaderListener) ((BrandReaderListener) listener).exitResistFlag(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandReaderVisitor)
                return ((BrandReaderVisitor<? extends T>) visitor).visitResistFlag(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ResistFlagContext resistFlag() throws RecognitionException {
        ResistFlagContext _localctx = new ResistFlagContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_resistFlag);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(28);
                match(RESIST);
                setState(29);
                ((ResistFlagContext) _localctx).TEXT = match(TEXT);

                MonsterRaceFlag flag = MonsterRaceFlag.RF_NONE;
                String flagText = "RF_" + ((ResistFlagContext) _localctx).TEXT.getText();
                try {
                    flag = MonsterRaceFlag.valueOf(flagText);
                } catch (Exception e) {
                    // Do nothing
                }
                ((ResistFlagContext) _localctx).resistFlagEnum = flag;

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
    public static class VulnFlagContext extends ParserRuleContext {
        public MonsterRaceFlag vulnFlagEnum;
        public Token TEXT;

        public TerminalNode VULNERABLE() {
            return getToken(BrandReaderParser.VULNERABLE, 0);
        }

        public TerminalNode TEXT() {
            return getToken(BrandReaderParser.TEXT, 0);
        }

        public VulnFlagContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_vulnFlag;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BrandReaderListener) ((BrandReaderListener) listener).enterVulnFlag(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandReaderListener) ((BrandReaderListener) listener).exitVulnFlag(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandReaderVisitor)
                return ((BrandReaderVisitor<? extends T>) visitor).visitVulnFlag(this);
            else return visitor.visitChildren(this);
        }
    }

    public final VulnFlagContext vulnFlag() throws RecognitionException {
        VulnFlagContext _localctx = new VulnFlagContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_vulnFlag);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(32);
                match(VULNERABLE);
                setState(33);
                ((VulnFlagContext) _localctx).TEXT = match(TEXT);

                MonsterRaceFlag flag = MonsterRaceFlag.RF_NONE;
                String flagText = "RF_" + ((VulnFlagContext) _localctx).TEXT.getText();
                try {
                    flag = MonsterRaceFlag.valueOf(flagText);
                } catch (Exception e) {
                    // Do nothing
                }
                ((VulnFlagContext) _localctx).vulnFlagEnum = flag;

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
    public static class MultiplierContext extends ParserRuleContext {
        public int multiplierNum;
        public Token NUMBER;

        public TerminalNode MULTIPLIER() {
            return getToken(BrandReaderParser.MULTIPLIER, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(BrandReaderParser.NUMBER, 0);
        }

        public MultiplierContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_multiplier;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BrandReaderListener) ((BrandReaderListener) listener).enterMultiplier(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandReaderListener) ((BrandReaderListener) listener).exitMultiplier(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandReaderVisitor)
                return ((BrandReaderVisitor<? extends T>) visitor).visitMultiplier(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MultiplierContext multiplier() throws RecognitionException {
        MultiplierContext _localctx = new MultiplierContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_multiplier);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(36);
                match(MULTIPLIER);
                setState(37);
                ((MultiplierContext) _localctx).NUMBER = match(NUMBER);

                ((MultiplierContext) _localctx).multiplierNum = Integer.parseInt(((MultiplierContext) _localctx).NUMBER.getText());

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
    public static class OMultiplierContext extends ParserRuleContext {
        public int oMultiplierNum;
        public Token NUMBER;

        public TerminalNode O_MULTIPLIER() {
            return getToken(BrandReaderParser.O_MULTIPLIER, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(BrandReaderParser.NUMBER, 0);
        }

        public OMultiplierContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_oMultiplier;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BrandReaderListener) ((BrandReaderListener) listener).enterOMultiplier(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandReaderListener) ((BrandReaderListener) listener).exitOMultiplier(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandReaderVisitor)
                return ((BrandReaderVisitor<? extends T>) visitor).visitOMultiplier(this);
            else return visitor.visitChildren(this);
        }
    }

    public final OMultiplierContext oMultiplier() throws RecognitionException {
        OMultiplierContext _localctx = new OMultiplierContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_oMultiplier);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(40);
                match(O_MULTIPLIER);
                setState(41);
                ((OMultiplierContext) _localctx).NUMBER = match(NUMBER);

                ((OMultiplierContext) _localctx).oMultiplierNum = Integer.parseInt(((OMultiplierContext) _localctx).NUMBER.getText());

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
    public static class PowerContext extends ParserRuleContext {
        public int powerNum;
        public Token NUMBER;

        public TerminalNode POWER() {
            return getToken(BrandReaderParser.POWER, 0);
        }

        public TerminalNode NUMBER() {
            return getToken(BrandReaderParser.NUMBER, 0);
        }

        public PowerContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_power;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BrandReaderListener) ((BrandReaderListener) listener).enterPower(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandReaderListener) ((BrandReaderListener) listener).exitPower(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandReaderVisitor)
                return ((BrandReaderVisitor<? extends T>) visitor).visitPower(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PowerContext power() throws RecognitionException {
        PowerContext _localctx = new PowerContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_power);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(44);
                match(POWER);
                setState(45);
                ((PowerContext) _localctx).NUMBER = match(NUMBER);

                ((PowerContext) _localctx).powerNum = Integer.parseInt(((PowerContext) _localctx).NUMBER.getText());

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
        public Token TEXT;

        public TerminalNode VERB() {
            return getToken(BrandReaderParser.VERB, 0);
        }

        public TerminalNode TEXT() {
            return getToken(BrandReaderParser.TEXT, 0);
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
            if (listener instanceof BrandReaderListener) ((BrandReaderListener) listener).enterVerb(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandReaderListener) ((BrandReaderListener) listener).exitVerb(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandReaderVisitor)
                return ((BrandReaderVisitor<? extends T>) visitor).visitVerb(this);
            else return visitor.visitChildren(this);
        }
    }

    public final VerbContext verb() throws RecognitionException {
        VerbContext _localctx = new VerbContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_verb);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(48);
                match(VERB);
                setState(49);
                ((VerbContext) _localctx).TEXT = match(TEXT);

                ((VerbContext) _localctx).verbStr = ((VerbContext) _localctx).TEXT.getText();

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
    public static class BrandContext extends ParserRuleContext {
        public Brand brandObject;
        public CodeContext code;
        public NameContext name;
        public VerbContext verb;
        public MultiplierContext multiplier;
        public OMultiplierContext oMultiplier;
        public PowerContext power;
        public ResistFlagContext resistFlag;
        public VulnFlagContext vulnFlag;

        public CodeContext code() {
            return getRuleContext(CodeContext.class, 0);
        }

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public VerbContext verb() {
            return getRuleContext(VerbContext.class, 0);
        }

        public MultiplierContext multiplier() {
            return getRuleContext(MultiplierContext.class, 0);
        }

        public OMultiplierContext oMultiplier() {
            return getRuleContext(OMultiplierContext.class, 0);
        }

        public PowerContext power() {
            return getRuleContext(PowerContext.class, 0);
        }

        public ResistFlagContext resistFlag() {
            return getRuleContext(ResistFlagContext.class, 0);
        }

        public VulnFlagContext vulnFlag() {
            return getRuleContext(VulnFlagContext.class, 0);
        }

        public BrandContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_brand;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BrandReaderListener) ((BrandReaderListener) listener).enterBrand(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandReaderListener) ((BrandReaderListener) listener).exitBrand(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandReaderVisitor)
                return ((BrandReaderVisitor<? extends T>) visitor).visitBrand(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BrandContext brand() throws RecognitionException {
        BrandContext _localctx = new BrandContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_brand);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(52);
                ((BrandContext) _localctx).code = code();
                setState(53);
                ((BrandContext) _localctx).name = name();
                setState(54);
                ((BrandContext) _localctx).verb = verb();
                setState(55);
                ((BrandContext) _localctx).multiplier = multiplier();
                setState(56);
                ((BrandContext) _localctx).oMultiplier = oMultiplier();
                setState(57);
                ((BrandContext) _localctx).power = power();
                setState(58);
                ((BrandContext) _localctx).resistFlag = resistFlag();
                setState(60);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == VULNERABLE) {
                    {
                        setState(59);
                        ((BrandContext) _localctx).vulnFlag = vulnFlag();
                    }
                }


                if (((BrandContext) _localctx).vulnFlag != null)
                    ((BrandContext) _localctx).brandObject = new Brand(((BrandContext) _localctx).code.codeStr, ((BrandContext) _localctx).name.nameStr, ((BrandContext) _localctx).verb.verbStr,
                            ((BrandContext) _localctx).resistFlag.resistFlagEnum,
                            ((BrandContext) _localctx).vulnFlag.vulnFlagEnum,
                            ((BrandContext) _localctx).multiplier.multiplierNum,
                            ((BrandContext) _localctx).oMultiplier.oMultiplierNum, ((BrandContext) _localctx).power.powerNum);
                else
                    ((BrandContext) _localctx).brandObject = new Brand(((BrandContext) _localctx).code.codeStr, ((BrandContext) _localctx).name.nameStr, ((BrandContext) _localctx).verb.verbStr,
                            ((BrandContext) _localctx).resistFlag.resistFlagEnum,
                            MonsterRaceFlag.RF_NONE,
                            ((BrandContext) _localctx).multiplier.multiplierNum,
                            ((BrandContext) _localctx).oMultiplier.oMultiplierNum, ((BrandContext) _localctx).power.powerNum);

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
    public static class BrandsContext extends ParserRuleContext {
        public ArrayList<Brand> brandList;
        public BrandContext brand;

        public TerminalNode EOF() {
            return getToken(BrandReaderParser.EOF, 0);
        }

        public List<BrandContext> brand() {
            return getRuleContexts(BrandContext.class);
        }

        public BrandContext brand(int i) {
            return getRuleContext(BrandContext.class, i);
        }

        public BrandsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_brands;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof BrandReaderListener) ((BrandReaderListener) listener).enterBrands(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof BrandReaderListener) ((BrandReaderListener) listener).exitBrands(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof BrandReaderVisitor)
                return ((BrandReaderVisitor<? extends T>) visitor).visitBrands(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BrandsContext brands() throws RecognitionException {
        BrandsContext _localctx = new BrandsContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_brands);

        ((BrandsContext) _localctx).brandList = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(67);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(64);
                            ((BrandsContext) _localctx).brand = brand();

                            _localctx.brandList.add(((BrandsContext) _localctx).brand.brandObject);

                        }
                    }
                    setState(69);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == CODE);
                setState(71);
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
            "\u0004\u0001\fJ\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002" +
                    "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002" +
                    "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002" +
                    "\b\u0007\b\u0002\t\u0007\t\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0003\b=\b\b\u0001\b\u0001" +
                    "\b\u0001\t\u0001\t\u0001\t\u0004\tD\b\t\u000b\t\f\tE\u0001\t\u0001\t\u0001" +
                    "\t\u0000\u0000\n\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0000" +
                    "\u0000A\u0000\u0014\u0001\u0000\u0000\u0000\u0002\u0018\u0001\u0000\u0000" +
                    "\u0000\u0004\u001c\u0001\u0000\u0000\u0000\u0006 \u0001\u0000\u0000\u0000" +
                    "\b$\u0001\u0000\u0000\u0000\n(\u0001\u0000\u0000\u0000\f,\u0001\u0000" +
                    "\u0000\u0000\u000e0\u0001\u0000\u0000\u0000\u00104\u0001\u0000\u0000\u0000" +
                    "\u0012C\u0001\u0000\u0000\u0000\u0014\u0015\u0005\u0003\u0000\u0000\u0015" +
                    "\u0016\u0005\f\u0000\u0000\u0016\u0017\u0006\u0000\uffff\uffff\u0000\u0017" +
                    "\u0001\u0001\u0000\u0000\u0000\u0018\u0019\u0005\u0004\u0000\u0000\u0019" +
                    "\u001a\u0005\f\u0000\u0000\u001a\u001b\u0006\u0001\uffff\uffff\u0000\u001b" +
                    "\u0003\u0001\u0000\u0000\u0000\u001c\u001d\u0005\u0005\u0000\u0000\u001d" +
                    "\u001e\u0005\f\u0000\u0000\u001e\u001f\u0006\u0002\uffff\uffff\u0000\u001f" +
                    "\u0005\u0001\u0000\u0000\u0000 !\u0005\u0006\u0000\u0000!\"\u0005\f\u0000" +
                    "\u0000\"#\u0006\u0003\uffff\uffff\u0000#\u0007\u0001\u0000\u0000\u0000" +
                    "$%\u0005\u0007\u0000\u0000%&\u0005\u000b\u0000\u0000&\'\u0006\u0004\uffff" +
                    "\uffff\u0000\'\t\u0001\u0000\u0000\u0000()\u0005\b\u0000\u0000)*\u0005" +
                    "\u000b\u0000\u0000*+\u0006\u0005\uffff\uffff\u0000+\u000b\u0001\u0000" +
                    "\u0000\u0000,-\u0005\t\u0000\u0000-.\u0005\u000b\u0000\u0000./\u0006\u0006" +
                    "\uffff\uffff\u0000/\r\u0001\u0000\u0000\u000001\u0005\n\u0000\u000012" +
                    "\u0005\f\u0000\u000023\u0006\u0007\uffff\uffff\u00003\u000f\u0001\u0000" +
                    "\u0000\u000045\u0003\u0000\u0000\u000056\u0003\u0002\u0001\u000067\u0003" +
                    "\u000e\u0007\u000078\u0003\b\u0004\u000089\u0003\n\u0005\u00009:\u0003" +
                    "\f\u0006\u0000:<\u0003\u0004\u0002\u0000;=\u0003\u0006\u0003\u0000<;\u0001" +
                    "\u0000\u0000\u0000<=\u0001\u0000\u0000\u0000=>\u0001\u0000\u0000\u0000" +
                    ">?\u0006\b\uffff\uffff\u0000?\u0011\u0001\u0000\u0000\u0000@A\u0003\u0010" +
                    "\b\u0000AB\u0006\t\uffff\uffff\u0000BD\u0001\u0000\u0000\u0000C@\u0001" +
                    "\u0000\u0000\u0000DE\u0001\u0000\u0000\u0000EC\u0001\u0000\u0000\u0000" +
                    "EF\u0001\u0000\u0000\u0000FG\u0001\u0000\u0000\u0000GH\u0005\u0000\u0000" +
                    "\u0001H\u0013\u0001\u0000\u0000\u0000\u0002<E";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}