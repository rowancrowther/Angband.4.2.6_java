// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/background/io/parsers/antlr/grammars/SummonFormatter.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.background.io.parsers.antlr.summon;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class SummonFormatterLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, WORD = 2, SPACE = 3, EOL = 4, NAMETAG = 5, MSGTTAG = 6, UNIQUESTAG = 7,
            BASETAG = 8, RACEFLAG = 9, FALLBACKTAG = 10, DESCTAG = 11, BOOLEAN = 12;
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "COMMENT", "WORD", "SPACE", "EOL", "NAMETAG", "MSGTTAG", "UNIQUESTAG",
                "BASETAG", "RACEFLAG", "FALLBACKTAG", "DESCTAG", "BOOLEAN"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "' '"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "WORD", "SPACE", "EOL", "NAMETAG", "MSGTTAG", "UNIQUESTAG",
                "BASETAG", "RACEFLAG", "FALLBACKTAG", "DESCTAG", "BOOLEAN"
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


    public SummonFormatterLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "SummonFormatter.g4";
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
            "\u0004\u0000\fk\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001" +
                    "\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004" +
                    "\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007" +
                    "\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b" +
                    "\u0007\u000b\u0001\u0000\u0001\u0000\u0005\u0000\u001c\b\u0000\n\u0000" +
                    "\f\u0000\u001f\t\u0000\u0001\u0000\u0004\u0000\"\b\u0000\u000b\u0000\f" +
                    "\u0000#\u0001\u0000\u0001\u0000\u0001\u0001\u0004\u0001)\b\u0001\u000b" +
                    "\u0001\f\u0001*\u0001\u0002\u0001\u0002\u0001\u0003\u0003\u00030\b\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001" +
                    "\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001" +
                    "\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b" +
                    "\u0001\u000b\u0000\u0000\f\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004" +
                    "\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017" +
                    "\f\u0001\u0000\u0002\u0001\u0000\n\n\u0003\u0000AZ__azn\u0000\u0001\u0001" +
                    "\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001" +
                    "\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000" +
                    "\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000" +
                    "\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000" +
                    "\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000" +
                    "\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0001\u0019\u0001\u0000\u0000" +
                    "\u0000\u0003(\u0001\u0000\u0000\u0000\u0005,\u0001\u0000\u0000\u0000\u0007" +
                    "/\u0001\u0000\u0000\u0000\t3\u0001\u0000\u0000\u0000\u000b9\u0001\u0000" +
                    "\u0000\u0000\r?\u0001\u0000\u0000\u0000\u000fH\u0001\u0000\u0000\u0000" +
                    "\u0011N\u0001\u0000\u0000\u0000\u0013Y\u0001\u0000\u0000\u0000\u0015c" +
                    "\u0001\u0000\u0000\u0000\u0017i\u0001\u0000\u0000\u0000\u0019\u001d\u0005" +
                    "#\u0000\u0000\u001a\u001c\b\u0000\u0000\u0000\u001b\u001a\u0001\u0000" +
                    "\u0000\u0000\u001c\u001f\u0001\u0000\u0000\u0000\u001d\u001b\u0001\u0000" +
                    "\u0000\u0000\u001d\u001e\u0001\u0000\u0000\u0000\u001e!\u0001\u0000\u0000" +
                    "\u0000\u001f\u001d\u0001\u0000\u0000\u0000 \"\u0005\n\u0000\u0000! \u0001" +
                    "\u0000\u0000\u0000\"#\u0001\u0000\u0000\u0000#!\u0001\u0000\u0000\u0000" +
                    "#$\u0001\u0000\u0000\u0000$%\u0001\u0000\u0000\u0000%&\u0006\u0000\u0000" +
                    "\u0000&\u0002\u0001\u0000\u0000\u0000\')\u0007\u0001\u0000\u0000(\'\u0001" +
                    "\u0000\u0000\u0000)*\u0001\u0000\u0000\u0000*(\u0001\u0000\u0000\u0000" +
                    "*+\u0001\u0000\u0000\u0000+\u0004\u0001\u0000\u0000\u0000,-\u0005 \u0000" +
                    "\u0000-\u0006\u0001\u0000\u0000\u0000.0\u0005\r\u0000\u0000/.\u0001\u0000" +
                    "\u0000\u0000/0\u0001\u0000\u0000\u000001\u0001\u0000\u0000\u000012\u0005" +
                    "\n\u0000\u00002\b\u0001\u0000\u0000\u000034\u0005n\u0000\u000045\u0005" +
                    "a\u0000\u000056\u0005m\u0000\u000067\u0005e\u0000\u000078\u0005:\u0000" +
                    "\u00008\n\u0001\u0000\u0000\u00009:\u0005m\u0000\u0000:;\u0005s\u0000" +
                    "\u0000;<\u0005g\u0000\u0000<=\u0005t\u0000\u0000=>\u0005:\u0000\u0000" +
                    ">\f\u0001\u0000\u0000\u0000?@\u0005u\u0000\u0000@A\u0005n\u0000\u0000" +
                    "AB\u0005i\u0000\u0000BC\u0005q\u0000\u0000CD\u0005u\u0000\u0000DE\u0005" +
                    "e\u0000\u0000EF\u0005s\u0000\u0000FG\u0005:\u0000\u0000G\u000e\u0001\u0000" +
                    "\u0000\u0000HI\u0005b\u0000\u0000IJ\u0005a\u0000\u0000JK\u0005s\u0000" +
                    "\u0000KL\u0005e\u0000\u0000LM\u0005:\u0000\u0000M\u0010\u0001\u0000\u0000" +
                    "\u0000NO\u0005r\u0000\u0000OP\u0005a\u0000\u0000PQ\u0005c\u0000\u0000" +
                    "QR\u0005e\u0000\u0000RS\u0005-\u0000\u0000ST\u0005f\u0000\u0000TU\u0005" +
                    "l\u0000\u0000UV\u0005a\u0000\u0000VW\u0005g\u0000\u0000WX\u0005:\u0000" +
                    "\u0000X\u0012\u0001\u0000\u0000\u0000YZ\u0005f\u0000\u0000Z[\u0005a\u0000" +
                    "\u0000[\\\u0005l\u0000\u0000\\]\u0005l\u0000\u0000]^\u0005b\u0000\u0000" +
                    "^_\u0005a\u0000\u0000_`\u0005c\u0000\u0000`a\u0005k\u0000\u0000ab\u0005" +
                    ":\u0000\u0000b\u0014\u0001\u0000\u0000\u0000cd\u0005d\u0000\u0000de\u0005" +
                    "e\u0000\u0000ef\u0005s\u0000\u0000fg\u0005c\u0000\u0000gh\u0005:\u0000" +
                    "\u0000h\u0016\u0001\u0000\u0000\u0000ij\u000201\u0000j\u0018\u0001\u0000" +
                    "\u0000\u0000\u0005\u0000\u001d#*/\u0001\u0006\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}