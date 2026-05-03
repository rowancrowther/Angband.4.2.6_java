// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/PlayerProperty.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.playerpropertyformatter;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class PlayerPropertyLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, TYPE = 3, CODE = 4, BINDUI = 5, NAME = 6, DESC = 7, VALUE = 8, NUMBER = 9,
            SPECIAL = 10, COLON = 11, STRING = 12, BINDUITAG = 13, MINUS = 14;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "COMMENT", "EOL", "TYPE", "CODE", "BINDUI", "NAME", "DESC", "VALUE",
                "NUMBER", "SPECIAL", "COLON", "STRING", "BINDUITAG", "MINUS"
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


    public PlayerPropertyLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
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
            "\u0004\u0000\u000e}\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001" +
                    "\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004" +
                    "\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007" +
                    "\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b" +
                    "\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0001\u0000\u0001\u0000\u0005" +
                    "\u0000 \b\u0000\n\u0000\f\u0000#\t\u0000\u0001\u0000\u0004\u0000&\b\u0000" +
                    "\u000b\u0000\f\u0000\'\u0001\u0000\u0001\u0000\u0001\u0001\u0005\u0001" +
                    "-\b\u0001\n\u0001\f\u00010\t\u0001\u0001\u0001\u0003\u00013\b\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001" +
                    "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001" +
                    "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001" +
                    "\u0007\u0001\b\u0004\ba\b\b\u000b\b\f\bb\u0001\t\u0001\t\u0001\t\u0001" +
                    "\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\u000b\u0004\u000b" +
                    "p\b\u000b\u000b\u000b\f\u000bq\u0001\f\u0001\f\u0004\fv\b\f\u000b\f\f" +
                    "\fw\u0001\f\u0001\f\u0001\r\u0001\r\u0000\u0000\u000e\u0001\u0001\u0003" +
                    "\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011" +
                    "\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e\u0001\u0000\u0004\u0001" +
                    "\u0000\n\n\u0001\u000009\n\u0000  (),,..09;;A[]]__az\u0001\u0000AZ\u0083" +
                    "\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000" +
                    "\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000" +
                    "\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000" +
                    "\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011" +
                    "\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015" +
                    "\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019" +
                    "\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0001\u001d" +
                    "\u0001\u0000\u0000\u0000\u0003.\u0001\u0000\u0000\u0000\u00058\u0001\u0000" +
                    "\u0000\u0000\u0007>\u0001\u0000\u0000\u0000\tD\u0001\u0000\u0000\u0000" +
                    "\u000bL\u0001\u0000\u0000\u0000\rR\u0001\u0000\u0000\u0000\u000fX\u0001" +
                    "\u0000\u0000\u0000\u0011`\u0001\u0000\u0000\u0000\u0013d\u0001\u0000\u0000" +
                    "\u0000\u0015l\u0001\u0000\u0000\u0000\u0017o\u0001\u0000\u0000\u0000\u0019" +
                    "s\u0001\u0000\u0000\u0000\u001b{\u0001\u0000\u0000\u0000\u001d!\u0005" +
                    "#\u0000\u0000\u001e \b\u0000\u0000\u0000\u001f\u001e\u0001\u0000\u0000" +
                    "\u0000 #\u0001\u0000\u0000\u0000!\u001f\u0001\u0000\u0000\u0000!\"\u0001" +
                    "\u0000\u0000\u0000\"%\u0001\u0000\u0000\u0000#!\u0001\u0000\u0000\u0000" +
                    "$&\u0005\n\u0000\u0000%$\u0001\u0000\u0000\u0000&\'\u0001\u0000\u0000" +
                    "\u0000\'%\u0001\u0000\u0000\u0000\'(\u0001\u0000\u0000\u0000()\u0001\u0000" +
                    "\u0000\u0000)*\u0006\u0000\u0000\u0000*\u0002\u0001\u0000\u0000\u0000" +
                    "+-\u0005 \u0000\u0000,+\u0001\u0000\u0000\u0000-0\u0001\u0000\u0000\u0000" +
                    ".,\u0001\u0000\u0000\u0000./\u0001\u0000\u0000\u0000/2\u0001\u0000\u0000" +
                    "\u00000.\u0001\u0000\u0000\u000013\u0005\r\u0000\u000021\u0001\u0000\u0000" +
                    "\u000023\u0001\u0000\u0000\u000034\u0001\u0000\u0000\u000045\u0005\n\u0000" +
                    "\u000056\u0001\u0000\u0000\u000067\u0006\u0001\u0000\u00007\u0004\u0001" +
                    "\u0000\u0000\u000089\u0005t\u0000\u00009:\u0005y\u0000\u0000:;\u0005p" +
                    "\u0000\u0000;<\u0005e\u0000\u0000<=\u0005:\u0000\u0000=\u0006\u0001\u0000" +
                    "\u0000\u0000>?\u0005c\u0000\u0000?@\u0005o\u0000\u0000@A\u0005d\u0000" +
                    "\u0000AB\u0005e\u0000\u0000BC\u0005:\u0000\u0000C\b\u0001\u0000\u0000" +
                    "\u0000DE\u0005b\u0000\u0000EF\u0005i\u0000\u0000FG\u0005n\u0000\u0000" +
                    "GH\u0005d\u0000\u0000HI\u0005u\u0000\u0000IJ\u0005i\u0000\u0000JK\u0005" +
                    ":\u0000\u0000K\n\u0001\u0000\u0000\u0000LM\u0005n\u0000\u0000MN\u0005" +
                    "a\u0000\u0000NO\u0005m\u0000\u0000OP\u0005e\u0000\u0000PQ\u0005:\u0000" +
                    "\u0000Q\f\u0001\u0000\u0000\u0000RS\u0005d\u0000\u0000ST\u0005e\u0000" +
                    "\u0000TU\u0005s\u0000\u0000UV\u0005c\u0000\u0000VW\u0005:\u0000\u0000" +
                    "W\u000e\u0001\u0000\u0000\u0000XY\u0005v\u0000\u0000YZ\u0005a\u0000\u0000" +
                    "Z[\u0005l\u0000\u0000[\\\u0005u\u0000\u0000\\]\u0005e\u0000\u0000]^\u0005" +
                    ":\u0000\u0000^\u0010\u0001\u0000\u0000\u0000_a\u0007\u0001\u0000\u0000" +
                    "`_\u0001\u0000\u0000\u0000ab\u0001\u0000\u0000\u0000b`\u0001\u0000\u0000" +
                    "\u0000bc\u0001\u0000\u0000\u0000c\u0012\u0001\u0000\u0000\u0000de\u0005" +
                    "s\u0000\u0000ef\u0005p\u0000\u0000fg\u0005e\u0000\u0000gh\u0005c\u0000" +
                    "\u0000hi\u0005i\u0000\u0000ij\u0005a\u0000\u0000jk\u0005l\u0000\u0000" +
                    "k\u0014\u0001\u0000\u0000\u0000lm\u0005:\u0000\u0000m\u0016\u0001\u0000" +
                    "\u0000\u0000np\u0007\u0002\u0000\u0000on\u0001\u0000\u0000\u0000pq\u0001" +
                    "\u0000\u0000\u0000qo\u0001\u0000\u0000\u0000qr\u0001\u0000\u0000\u0000" +
                    "r\u0018\u0001\u0000\u0000\u0000su\u0005<\u0000\u0000tv\u0007\u0003\u0000" +
                    "\u0000ut\u0001\u0000\u0000\u0000vw\u0001\u0000\u0000\u0000wu\u0001\u0000" +
                    "\u0000\u0000wx\u0001\u0000\u0000\u0000xy\u0001\u0000\u0000\u0000yz\u0005" +
                    ">\u0000\u0000z\u001a\u0001\u0000\u0000\u0000{|\u0005-\u0000\u0000|\u001c" +
                    "\u0001\u0000\u0000\u0000\b\u0000!\'.2bqw\u0001\u0006\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}