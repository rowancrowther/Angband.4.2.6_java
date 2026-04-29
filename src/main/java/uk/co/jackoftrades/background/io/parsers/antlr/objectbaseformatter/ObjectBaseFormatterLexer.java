// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/background/io/parsers/antlr/grammars/ObjectBaseFormatter.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.background.io.parsers.antlr.objectbaseformatter;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import uk.co.jackoftrades.middle.objects.ObjectBase;

import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ObjectBaseFormatterLexer extends Lexer {
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
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "COLON", "OR", "COMMENT", "EOL", "NUMBER", "DEFAULTB", "DEFAULTM", "NAME",
                "GRAPHICS", "BREAK", "MAXSTACK", "HATES", "FLAGS", "SPACE", "LCASE",
                "UCASE", "MCASE"
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


    ArrayList<ObjectBase> bases = new ArrayList<>();


    public ObjectBaseFormatterLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
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
    public String[] getChannelNames() {
        return channelNames;
    }

    @Override
    public String[] getModeNames() {
        return modeNames;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public static final String _serializedATN =
            "\u0004\u0000\u0011\u00ac\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002" +
                    "\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002" +
                    "\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002" +
                    "\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002" +
                    "\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e" +
                    "\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0001\u0000\u0001\u0000" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002" +
                    "\u0005\u0002,\b\u0002\n\u0002\f\u0002/\t\u0002\u0001\u0002\u0004\u0002" +
                    "2\b\u0002\u000b\u0002\f\u00023\u0001\u0002\u0001\u0002\u0001\u0003\u0003" +
                    "\u00039\b\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001" +
                    "\u0004\u0004\u0004@\b\u0004\u000b\u0004\f\u0004A\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b" +
                    "\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001" +
                    "\u000e\u0001\u000e\u0005\u000e\u009a\b\u000e\n\u000e\f\u000e\u009d\t\u000e" +
                    "\u0001\u000f\u0001\u000f\u0005\u000f\u00a1\b\u000f\n\u000f\f\u000f\u00a4" +
                    "\t\u000f\u0001\u0010\u0001\u0010\u0005\u0010\u00a8\b\u0010\n\u0010\f\u0010" +
                    "\u00ab\t\u0010\u0000\u0000\u0011\u0001\u0001\u0003\u0002\u0005\u0003\u0007" +
                    "\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b" +
                    "\u0017\f\u0019\r\u001b\u000e\u001d\u000f\u001f\u0010!\u0011\u0001\u0000" +
                    "\u0005\u0001\u0000\n\n\u0003\u0000  --az\u0002\u0000AZ__\u0002\u0000A" +
                    "Zaz\u0005\u0000  --AZaz~~\u00b2\u0000\u0001\u0001\u0000\u0000\u0000\u0000" +
                    "\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000" +
                    "\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b" +
                    "\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001" +
                    "\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001" +
                    "\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001" +
                    "\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000\u0000\u001b\u0001" +
                    "\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000\u0000\u0000\u001f\u0001" +
                    "\u0000\u0000\u0000\u0000!\u0001\u0000\u0000\u0000\u0001#\u0001\u0000\u0000" +
                    "\u0000\u0003%\u0001\u0000\u0000\u0000\u0005)\u0001\u0000\u0000\u0000\u0007" +
                    "8\u0001\u0000\u0000\u0000\t?\u0001\u0000\u0000\u0000\u000bC\u0001\u0000" +
                    "\u0000\u0000\rX\u0001\u0000\u0000\u0000\u000fj\u0001\u0000\u0000\u0000" +
                    "\u0011o\u0001\u0000\u0000\u0000\u0013x\u0001\u0000\u0000\u0000\u0015~" +
                    "\u0001\u0000\u0000\u0000\u0017\u0088\u0001\u0000\u0000\u0000\u0019\u008f" +
                    "\u0001\u0000\u0000\u0000\u001b\u0095\u0001\u0000\u0000\u0000\u001d\u0097" +
                    "\u0001\u0000\u0000\u0000\u001f\u009e\u0001\u0000\u0000\u0000!\u00a5\u0001" +
                    "\u0000\u0000\u0000#$\u0005:\u0000\u0000$\u0002\u0001\u0000\u0000\u0000" +
                    "%&\u0005 \u0000\u0000&\'\u0005|\u0000\u0000\'(\u0005 \u0000\u0000(\u0004" +
                    "\u0001\u0000\u0000\u0000)-\u0005#\u0000\u0000*,\b\u0000\u0000\u0000+*" +
                    "\u0001\u0000\u0000\u0000,/\u0001\u0000\u0000\u0000-+\u0001\u0000\u0000" +
                    "\u0000-.\u0001\u0000\u0000\u0000.1\u0001\u0000\u0000\u0000/-\u0001\u0000" +
                    "\u0000\u000002\u0005\n\u0000\u000010\u0001\u0000\u0000\u000023\u0001\u0000" +
                    "\u0000\u000031\u0001\u0000\u0000\u000034\u0001\u0000\u0000\u000045\u0001" +
                    "\u0000\u0000\u000056\u0006\u0002\u0000\u00006\u0006\u0001\u0000\u0000" +
                    "\u000079\u0005\r\u0000\u000087\u0001\u0000\u0000\u000089\u0001\u0000\u0000" +
                    "\u00009:\u0001\u0000\u0000\u0000:;\u0005\n\u0000\u0000;<\u0001\u0000\u0000" +
                    "\u0000<=\u0006\u0003\u0000\u0000=\b\u0001\u0000\u0000\u0000>@\u000209" +
                    "\u0000?>\u0001\u0000\u0000\u0000@A\u0001\u0000\u0000\u0000A?\u0001\u0000" +
                    "\u0000\u0000AB\u0001\u0000\u0000\u0000B\n\u0001\u0000\u0000\u0000CD\u0005" +
                    "d\u0000\u0000DE\u0005e\u0000\u0000EF\u0005f\u0000\u0000FG\u0005a\u0000" +
                    "\u0000GH\u0005u\u0000\u0000HI\u0005l\u0000\u0000IJ\u0005t\u0000\u0000" +
                    "JK\u0005:\u0000\u0000KL\u0005b\u0000\u0000LM\u0005r\u0000\u0000MN\u0005" +
                    "e\u0000\u0000NO\u0005a\u0000\u0000OP\u0005k\u0000\u0000PQ\u0005-\u0000" +
                    "\u0000QR\u0005c\u0000\u0000RS\u0005h\u0000\u0000ST\u0005a\u0000\u0000" +
                    "TU\u0005n\u0000\u0000UV\u0005c\u0000\u0000VW\u0005e\u0000\u0000W\f\u0001" +
                    "\u0000\u0000\u0000XY\u0005d\u0000\u0000YZ\u0005e\u0000\u0000Z[\u0005f" +
                    "\u0000\u0000[\\\u0005a\u0000\u0000\\]\u0005u\u0000\u0000]^\u0005l\u0000" +
                    "\u0000^_\u0005t\u0000\u0000_`\u0005:\u0000\u0000`a\u0005m\u0000\u0000" +
                    "ab\u0005a\u0000\u0000bc\u0005x\u0000\u0000cd\u0005-\u0000\u0000de\u0005" +
                    "s\u0000\u0000ef\u0005t\u0000\u0000fg\u0005a\u0000\u0000gh\u0005c\u0000" +
                    "\u0000hi\u0005k\u0000\u0000i\u000e\u0001\u0000\u0000\u0000jk\u0005n\u0000" +
                    "\u0000kl\u0005a\u0000\u0000lm\u0005m\u0000\u0000mn\u0005e\u0000\u0000" +
                    "n\u0010\u0001\u0000\u0000\u0000op\u0005g\u0000\u0000pq\u0005r\u0000\u0000" +
                    "qr\u0005a\u0000\u0000rs\u0005p\u0000\u0000st\u0005h\u0000\u0000tu\u0005" +
                    "i\u0000\u0000uv\u0005c\u0000\u0000vw\u0005s\u0000\u0000w\u0012\u0001\u0000" +
                    "\u0000\u0000xy\u0005b\u0000\u0000yz\u0005r\u0000\u0000z{\u0005e\u0000" +
                    "\u0000{|\u0005a\u0000\u0000|}\u0005k\u0000\u0000}\u0014\u0001\u0000\u0000" +
                    "\u0000~\u007f\u0005m\u0000\u0000\u007f\u0080\u0005a\u0000\u0000\u0080" +
                    "\u0081\u0005x\u0000\u0000\u0081\u0082\u0005-\u0000\u0000\u0082\u0083\u0005" +
                    "s\u0000\u0000\u0083\u0084\u0005t\u0000\u0000\u0084\u0085\u0005a\u0000" +
                    "\u0000\u0085\u0086\u0005c\u0000\u0000\u0086\u0087\u0005k\u0000\u0000\u0087" +
                    "\u0016\u0001\u0000\u0000\u0000\u0088\u0089\u0005H\u0000\u0000\u0089\u008a" +
                    "\u0005A\u0000\u0000\u008a\u008b\u0005T\u0000\u0000\u008b\u008c\u0005E" +
                    "\u0000\u0000\u008c\u008d\u0005S\u0000\u0000\u008d\u008e\u0005_\u0000\u0000" +
                    "\u008e\u0018\u0001\u0000\u0000\u0000\u008f\u0090\u0005f\u0000\u0000\u0090" +
                    "\u0091\u0005l\u0000\u0000\u0091\u0092\u0005a\u0000\u0000\u0092\u0093\u0005" +
                    "g\u0000\u0000\u0093\u0094\u0005s\u0000\u0000\u0094\u001a\u0001\u0000\u0000" +
                    "\u0000\u0095\u0096\u0005 \u0000\u0000\u0096\u001c\u0001\u0000\u0000\u0000" +
                    "\u0097\u009b\u0002az\u0000\u0098\u009a\u0007\u0001\u0000\u0000\u0099\u0098" +
                    "\u0001\u0000\u0000\u0000\u009a\u009d\u0001\u0000\u0000\u0000\u009b\u0099" +
                    "\u0001\u0000\u0000\u0000\u009b\u009c\u0001\u0000\u0000\u0000\u009c\u001e" +
                    "\u0001\u0000\u0000\u0000\u009d\u009b\u0001\u0000\u0000\u0000\u009e\u00a2" +
                    "\u0002AZ\u0000\u009f\u00a1\u0007\u0002\u0000\u0000\u00a0\u009f\u0001\u0000" +
                    "\u0000\u0000\u00a1\u00a4\u0001\u0000\u0000\u0000\u00a2\u00a0\u0001\u0000" +
                    "\u0000\u0000\u00a2\u00a3\u0001\u0000\u0000\u0000\u00a3 \u0001\u0000\u0000" +
                    "\u0000\u00a4\u00a2\u0001\u0000\u0000\u0000\u00a5\u00a9\u0007\u0003\u0000" +
                    "\u0000\u00a6\u00a8\u0007\u0004\u0000\u0000\u00a7\u00a6\u0001\u0000\u0000" +
                    "\u0000\u00a8\u00ab\u0001\u0000\u0000\u0000\u00a9\u00a7\u0001\u0000\u0000" +
                    "\u0000\u00a9\u00aa\u0001\u0000\u0000\u0000\u00aa\"\u0001\u0000\u0000\u0000" +
                    "\u00ab\u00a9\u0001\u0000\u0000\u0000\b\u0000-38A\u009b\u00a2\u00a9\u0001" +
                    "\u0006\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}