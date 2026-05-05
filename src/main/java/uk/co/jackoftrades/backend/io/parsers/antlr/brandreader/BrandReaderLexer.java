// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/BrandReader.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.brandreader;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class BrandReaderLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, CODE = 3, NAME = 4, RESIST = 5, VULNERABLE = 6, MULTIPLIER = 7,
            O_MULTIPLIER = 8, POWER = 9, VERB = 10, NUMBER = 11, TEXT = 12;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "COMMENT", "EOL", "CODE", "NAME", "RESIST", "VULNERABLE", "MULTIPLIER",
                "O_MULTIPLIER", "POWER", "VERB", "NUMBER", "TEXT"
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


    public BrandReaderLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
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
            "\u0004\u0000\f\u008c\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001" +
                    "\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004" +
                    "\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007" +
                    "\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b" +
                    "\u0007\u000b\u0001\u0000\u0001\u0000\u0001\u0000\u0005\u0000\u001d\b\u0000" +
                    "\n\u0000\f\u0000 \t\u0000\u0001\u0000\u0004\u0000#\b\u0000\u000b\u0000" +
                    "\f\u0000$\u0001\u0000\u0001\u0000\u0001\u0001\u0005\u0001*\b\u0001\n\u0001" +
                    "\f\u0001-\t\u0001\u0001\u0001\u0003\u00010\b\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001" +
                    "\t\u0001\t\u0001\n\u0004\n\u0082\b\n\u000b\n\f\n\u0083\u0001\u000b\u0001" +
                    "\u000b\u0005\u000b\u0088\b\u000b\n\u000b\f\u000b\u008b\t\u000b\u0000\u0000" +
                    "\f\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006" +
                    "\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0001\u0000\u0005" +
                    "\u0001\u0000::\u0001\u0000\n\n\u0001\u000009\u0002\u0000AZaz\u0005\u0000" +
                    "  09AZ__az\u0091\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001" +
                    "\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001" +
                    "\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000" +
                    "\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000" +
                    "\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000" +
                    "\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000" +
                    "\u0000\u0001\u0019\u0001\u0000\u0000\u0000\u0003+\u0001\u0000\u0000\u0000" +
                    "\u00055\u0001\u0000\u0000\u0000\u0007;\u0001\u0000\u0000\u0000\tA\u0001" +
                    "\u0000\u0000\u0000\u000bN\u0001\u0000\u0000\u0000\rY\u0001\u0000\u0000" +
                    "\u0000\u000fe\u0001\u0000\u0000\u0000\u0011s\u0001\u0000\u0000\u0000\u0013" +
                    "z\u0001\u0000\u0000\u0000\u0015\u0081\u0001\u0000\u0000\u0000\u0017\u0085" +
                    "\u0001\u0000\u0000\u0000\u0019\u001a\u0005#\u0000\u0000\u001a\u001e\b" +
                    "\u0000\u0000\u0000\u001b\u001d\b\u0001\u0000\u0000\u001c\u001b\u0001\u0000" +
                    "\u0000\u0000\u001d \u0001\u0000\u0000\u0000\u001e\u001c\u0001\u0000\u0000" +
                    "\u0000\u001e\u001f\u0001\u0000\u0000\u0000\u001f\"\u0001\u0000\u0000\u0000" +
                    " \u001e\u0001\u0000\u0000\u0000!#\u0005\n\u0000\u0000\"!\u0001\u0000\u0000" +
                    "\u0000#$\u0001\u0000\u0000\u0000$\"\u0001\u0000\u0000\u0000$%\u0001\u0000" +
                    "\u0000\u0000%&\u0001\u0000\u0000\u0000&\'\u0006\u0000\u0000\u0000\'\u0002" +
                    "\u0001\u0000\u0000\u0000(*\u0005 \u0000\u0000)(\u0001\u0000\u0000\u0000" +
                    "*-\u0001\u0000\u0000\u0000+)\u0001\u0000\u0000\u0000+,\u0001\u0000\u0000" +
                    "\u0000,/\u0001\u0000\u0000\u0000-+\u0001\u0000\u0000\u0000.0\u0005\r\u0000" +
                    "\u0000/.\u0001\u0000\u0000\u0000/0\u0001\u0000\u0000\u000001\u0001\u0000" +
                    "\u0000\u000012\u0005\n\u0000\u000023\u0001\u0000\u0000\u000034\u0006\u0001" +
                    "\u0000\u00004\u0004\u0001\u0000\u0000\u000056\u0005c\u0000\u000067\u0005" +
                    "o\u0000\u000078\u0005d\u0000\u000089\u0005e\u0000\u00009:\u0005:\u0000" +
                    "\u0000:\u0006\u0001\u0000\u0000\u0000;<\u0005n\u0000\u0000<=\u0005a\u0000" +
                    "\u0000=>\u0005m\u0000\u0000>?\u0005e\u0000\u0000?@\u0005:\u0000\u0000" +
                    "@\b\u0001\u0000\u0000\u0000AB\u0005r\u0000\u0000BC\u0005e\u0000\u0000" +
                    "CD\u0005s\u0000\u0000DE\u0005i\u0000\u0000EF\u0005s\u0000\u0000FG\u0005" +
                    "t\u0000\u0000GH\u0005-\u0000\u0000HI\u0005f\u0000\u0000IJ\u0005l\u0000" +
                    "\u0000JK\u0005a\u0000\u0000KL\u0005g\u0000\u0000LM\u0005:\u0000\u0000" +
                    "M\n\u0001\u0000\u0000\u0000NO\u0005v\u0000\u0000OP\u0005u\u0000\u0000" +
                    "PQ\u0005l\u0000\u0000QR\u0005n\u0000\u0000RS\u0005-\u0000\u0000ST\u0005" +
                    "f\u0000\u0000TU\u0005l\u0000\u0000UV\u0005a\u0000\u0000VW\u0005g\u0000" +
                    "\u0000WX\u0005:\u0000\u0000X\f\u0001\u0000\u0000\u0000YZ\u0005m\u0000" +
                    "\u0000Z[\u0005u\u0000\u0000[\\\u0005l\u0000\u0000\\]\u0005t\u0000\u0000" +
                    "]^\u0005i\u0000\u0000^_\u0005p\u0000\u0000_`\u0005l\u0000\u0000`a\u0005" +
                    "i\u0000\u0000ab\u0005e\u0000\u0000bc\u0005r\u0000\u0000cd\u0005:\u0000" +
                    "\u0000d\u000e\u0001\u0000\u0000\u0000ef\u0005o\u0000\u0000fg\u0005-\u0000" +
                    "\u0000gh\u0005m\u0000\u0000hi\u0005u\u0000\u0000ij\u0005l\u0000\u0000" +
                    "jk\u0005t\u0000\u0000kl\u0005i\u0000\u0000lm\u0005p\u0000\u0000mn\u0005" +
                    "l\u0000\u0000no\u0005i\u0000\u0000op\u0005e\u0000\u0000pq\u0005r\u0000" +
                    "\u0000qr\u0005:\u0000\u0000r\u0010\u0001\u0000\u0000\u0000st\u0005p\u0000" +
                    "\u0000tu\u0005o\u0000\u0000uv\u0005w\u0000\u0000vw\u0005e\u0000\u0000" +
                    "wx\u0005r\u0000\u0000xy\u0005:\u0000\u0000y\u0012\u0001\u0000\u0000\u0000" +
                    "z{\u0005v\u0000\u0000{|\u0005e\u0000\u0000|}\u0005r\u0000\u0000}~\u0005" +
                    "b\u0000\u0000~\u007f\u0005:\u0000\u0000\u007f\u0014\u0001\u0000\u0000" +
                    "\u0000\u0080\u0082\u0007\u0002\u0000\u0000\u0081\u0080\u0001\u0000\u0000" +
                    "\u0000\u0082\u0083\u0001\u0000\u0000\u0000\u0083\u0081\u0001\u0000\u0000" +
                    "\u0000\u0083\u0084\u0001\u0000\u0000\u0000\u0084\u0016\u0001\u0000\u0000" +
                    "\u0000\u0085\u0089\u0007\u0003\u0000\u0000\u0086\u0088\u0007\u0004\u0000" +
                    "\u0000\u0087\u0086\u0001\u0000\u0000\u0000\u0088\u008b\u0001\u0000\u0000" +
                    "\u0000\u0089\u0087\u0001\u0000\u0000\u0000\u0089\u008a\u0001\u0000\u0000" +
                    "\u0000\u008a\u0018\u0001\u0000\u0000\u0000\u008b\u0089\u0001\u0000\u0000" +
                    "\u0000\u0007\u0000\u001e$+/\u0083\u0089\u0001\u0006\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}