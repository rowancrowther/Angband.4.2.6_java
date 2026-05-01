// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/UIEntryGram.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.uientry;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class UIEntryGramLexer extends Lexer {
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
    public static String[] channelNames = {
            "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    };

    public static String[] modeNames = {
            "DEFAULT_MODE"
    };

    private static String[] makeRuleNames() {
        return new String[]{
                "COMMENT", "EOL", "NAME", "PARAMETER", "RENDERER", "COMBINE", "CATEGORY",
                "DESC", "FLAGS", "LABEL", "LABEL5", "LABEL2", "TEMPLATE", "PRIORITY",
                "PARAMETERTYPE", "COMBINETYPE", "NUMBER", "MINUS", "TAG", "TEXT"
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


    public UIEntryGramLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
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
            "\u0004\u0000\u0014\u00d8\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002" +
                    "\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002" +
                    "\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002" +
                    "\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002" +
                    "\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e" +
                    "\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011" +
                    "\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0001\u0000\u0001\u0000" +
                    "\u0005\u0000,\b\u0000\n\u0000\f\u0000/\t\u0000\u0001\u0000\u0004\u0000" +
                    "2\b\u0000\u000b\u0000\f\u00003\u0001\u0000\u0001\u0000\u0001\u0001\u0005" +
                    "\u00019\b\u0001\n\u0001\f\u0001<\t\u0001\u0001\u0001\u0003\u0001?\b\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002" +
                    "\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
                    "\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
                    "\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
                    "\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
                    "\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
                    "\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001" +
                    "\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001" +
                    "\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b" +
                    "\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b" +
                    "\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001" +
                    "\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001" +
                    "\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e" +
                    "\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e" +
                    "\u0001\u000e\u0003\u000e\u00b6\b\u000e\u0001\u000f\u0001\u000f\u0001\u000f" +
                    "\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f" +
                    "\u0001\u000f\u0001\u000f\u0003\u000f\u00c3\b\u000f\u0001\u0010\u0004\u0010" +
                    "\u00c6\b\u0010\u000b\u0010\f\u0010\u00c7\u0001\u0011\u0001\u0011\u0001" +
                    "\u0012\u0001\u0012\u0004\u0012\u00ce\b\u0012\u000b\u0012\f\u0012\u00cf" +
                    "\u0001\u0012\u0001\u0012\u0001\u0013\u0004\u0013\u00d5\b\u0013\u000b\u0013" +
                    "\f\u0013\u00d6\u0000\u0000\u0014\u0001\u0001\u0003\u0002\u0005\u0003\u0007" +
                    "\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b" +
                    "\u0017\f\u0019\r\u001b\u000e\u001d\u000f\u001f\u0010!\u0011#\u0012%\u0013" +
                    "\'\u0014\u0001\u0000\u0004\u0001\u0000\n\n\u0001\u000009\u0001\u0000A" +
                    "Z\b\u0000  \'\',,..09AZ__az\u00e0\u0000\u0001\u0001\u0000\u0000\u0000" +
                    "\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000" +
                    "\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000" +
                    "\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f" +
                    "\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013" +
                    "\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017" +
                    "\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000\u0000\u001b" +
                    "\u0001\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000\u0000\u0000\u001f" +
                    "\u0001\u0000\u0000\u0000\u0000!\u0001\u0000\u0000\u0000\u0000#\u0001\u0000" +
                    "\u0000\u0000\u0000%\u0001\u0000\u0000\u0000\u0000\'\u0001\u0000\u0000" +
                    "\u0000\u0001)\u0001\u0000\u0000\u0000\u0003:\u0001\u0000\u0000\u0000\u0005" +
                    "D\u0001\u0000\u0000\u0000\u0007J\u0001\u0000\u0000\u0000\tU\u0001\u0000" +
                    "\u0000\u0000\u000b_\u0001\u0000\u0000\u0000\rh\u0001\u0000\u0000\u0000" +
                    "\u000fr\u0001\u0000\u0000\u0000\u0011x\u0001\u0000\u0000\u0000\u0013\u007f" +
                    "\u0001\u0000\u0000\u0000\u0015\u0086\u0001\u0000\u0000\u0000\u0017\u008e" +
                    "\u0001\u0000\u0000\u0000\u0019\u0096\u0001\u0000\u0000\u0000\u001b\u00a0" +
                    "\u0001\u0000\u0000\u0000\u001d\u00b5\u0001\u0000\u0000\u0000\u001f\u00c2" +
                    "\u0001\u0000\u0000\u0000!\u00c5\u0001\u0000\u0000\u0000#\u00c9\u0001\u0000" +
                    "\u0000\u0000%\u00cb\u0001\u0000\u0000\u0000\'\u00d4\u0001\u0000\u0000" +
                    "\u0000)-\u0005#\u0000\u0000*,\b\u0000\u0000\u0000+*\u0001\u0000\u0000" +
                    "\u0000,/\u0001\u0000\u0000\u0000-+\u0001\u0000\u0000\u0000-.\u0001\u0000" +
                    "\u0000\u0000.1\u0001\u0000\u0000\u0000/-\u0001\u0000\u0000\u000002\u0005" +
                    "\n\u0000\u000010\u0001\u0000\u0000\u000023\u0001\u0000\u0000\u000031\u0001" +
                    "\u0000\u0000\u000034\u0001\u0000\u0000\u000045\u0001\u0000\u0000\u0000" +
                    "56\u0006\u0000\u0000\u00006\u0002\u0001\u0000\u0000\u000079\u0005 \u0000" +
                    "\u000087\u0001\u0000\u0000\u00009<\u0001\u0000\u0000\u0000:8\u0001\u0000" +
                    "\u0000\u0000:;\u0001\u0000\u0000\u0000;>\u0001\u0000\u0000\u0000<:\u0001" +
                    "\u0000\u0000\u0000=?\u0005\r\u0000\u0000>=\u0001\u0000\u0000\u0000>?\u0001" +
                    "\u0000\u0000\u0000?@\u0001\u0000\u0000\u0000@A\u0005\n\u0000\u0000AB\u0001" +
                    "\u0000\u0000\u0000BC\u0006\u0001\u0000\u0000C\u0004\u0001\u0000\u0000" +
                    "\u0000DE\u0005n\u0000\u0000EF\u0005a\u0000\u0000FG\u0005m\u0000\u0000" +
                    "GH\u0005e\u0000\u0000HI\u0005:\u0000\u0000I\u0006\u0001\u0000\u0000\u0000" +
                    "JK\u0005p\u0000\u0000KL\u0005a\u0000\u0000LM\u0005r\u0000\u0000MN\u0005" +
                    "a\u0000\u0000NO\u0005m\u0000\u0000OP\u0005e\u0000\u0000PQ\u0005t\u0000" +
                    "\u0000QR\u0005e\u0000\u0000RS\u0005r\u0000\u0000ST\u0005:\u0000\u0000" +
                    "T\b\u0001\u0000\u0000\u0000UV\u0005r\u0000\u0000VW\u0005e\u0000\u0000" +
                    "WX\u0005n\u0000\u0000XY\u0005d\u0000\u0000YZ\u0005e\u0000\u0000Z[\u0005" +
                    "r\u0000\u0000[\\\u0005e\u0000\u0000\\]\u0005r\u0000\u0000]^\u0005:\u0000" +
                    "\u0000^\n\u0001\u0000\u0000\u0000_`\u0005c\u0000\u0000`a\u0005o\u0000" +
                    "\u0000ab\u0005m\u0000\u0000bc\u0005b\u0000\u0000cd\u0005i\u0000\u0000" +
                    "de\u0005n\u0000\u0000ef\u0005e\u0000\u0000fg\u0005:\u0000\u0000g\f\u0001" +
                    "\u0000\u0000\u0000hi\u0005c\u0000\u0000ij\u0005a\u0000\u0000jk\u0005t" +
                    "\u0000\u0000kl\u0005e\u0000\u0000lm\u0005g\u0000\u0000mn\u0005o\u0000" +
                    "\u0000no\u0005r\u0000\u0000op\u0005y\u0000\u0000pq\u0005:\u0000\u0000" +
                    "q\u000e\u0001\u0000\u0000\u0000rs\u0005d\u0000\u0000st\u0005e\u0000\u0000" +
                    "tu\u0005s\u0000\u0000uv\u0005c\u0000\u0000vw\u0005:\u0000\u0000w\u0010" +
                    "\u0001\u0000\u0000\u0000xy\u0005f\u0000\u0000yz\u0005l\u0000\u0000z{\u0005" +
                    "a\u0000\u0000{|\u0005g\u0000\u0000|}\u0005s\u0000\u0000}~\u0005:\u0000" +
                    "\u0000~\u0012\u0001\u0000\u0000\u0000\u007f\u0080\u0005l\u0000\u0000\u0080" +
                    "\u0081\u0005a\u0000\u0000\u0081\u0082\u0005b\u0000\u0000\u0082\u0083\u0005" +
                    "e\u0000\u0000\u0083\u0084\u0005l\u0000\u0000\u0084\u0085\u0005:\u0000" +
                    "\u0000\u0085\u0014\u0001\u0000\u0000\u0000\u0086\u0087\u0005l\u0000\u0000" +
                    "\u0087\u0088\u0005a\u0000\u0000\u0088\u0089\u0005b\u0000\u0000\u0089\u008a" +
                    "\u0005e\u0000\u0000\u008a\u008b\u0005l\u0000\u0000\u008b\u008c\u00055" +
                    "\u0000\u0000\u008c\u008d\u0005:\u0000\u0000\u008d\u0016\u0001\u0000\u0000" +
                    "\u0000\u008e\u008f\u0005l\u0000\u0000\u008f\u0090\u0005a\u0000\u0000\u0090" +
                    "\u0091\u0005b\u0000\u0000\u0091\u0092\u0005e\u0000\u0000\u0092\u0093\u0005" +
                    "l\u0000\u0000\u0093\u0094\u00052\u0000\u0000\u0094\u0095\u0005:\u0000" +
                    "\u0000\u0095\u0018\u0001\u0000\u0000\u0000\u0096\u0097\u0005t\u0000\u0000" +
                    "\u0097\u0098\u0005e\u0000\u0000\u0098\u0099\u0005m\u0000\u0000\u0099\u009a" +
                    "\u0005p\u0000\u0000\u009a\u009b\u0005l\u0000\u0000\u009b\u009c\u0005a" +
                    "\u0000\u0000\u009c\u009d\u0005t\u0000\u0000\u009d\u009e\u0005e\u0000\u0000" +
                    "\u009e\u009f\u0005:\u0000\u0000\u009f\u001a\u0001\u0000\u0000\u0000\u00a0" +
                    "\u00a1\u0005p\u0000\u0000\u00a1\u00a2\u0005r\u0000\u0000\u00a2\u00a3\u0005" +
                    "i\u0000\u0000\u00a3\u00a4\u0005o\u0000\u0000\u00a4\u00a5\u0005r\u0000" +
                    "\u0000\u00a5\u00a6\u0005i\u0000\u0000\u00a6\u00a7\u0005t\u0000\u0000\u00a7" +
                    "\u00a8\u0005y\u0000\u0000\u00a8\u00a9\u0005:\u0000\u0000\u00a9\u001c\u0001" +
                    "\u0000\u0000\u0000\u00aa\u00ab\u0005s\u0000\u0000\u00ab\u00ac\u0005t\u0000" +
                    "\u0000\u00ac\u00ad\u0005a\u0000\u0000\u00ad\u00b6\u0005t\u0000\u0000\u00ae" +
                    "\u00af\u0005e\u0000\u0000\u00af\u00b0\u0005l\u0000\u0000\u00b0\u00b1\u0005" +
                    "e\u0000\u0000\u00b1\u00b2\u0005m\u0000\u0000\u00b2\u00b3\u0005e\u0000" +
                    "\u0000\u00b3\u00b4\u0005n\u0000\u0000\u00b4\u00b6\u0005t\u0000\u0000\u00b5" +
                    "\u00aa\u0001\u0000\u0000\u0000\u00b5\u00ae\u0001\u0000\u0000\u0000\u00b6" +
                    "\u001e\u0001\u0000\u0000\u0000\u00b7\u00b8\u0005A\u0000\u0000\u00b8\u00b9" +
                    "\u0005D\u0000\u0000\u00b9\u00c3\u0005D\u0000\u0000\u00ba\u00bb\u0005R" +
                    "\u0000\u0000\u00bb\u00bc\u0005E\u0000\u0000\u00bc\u00bd\u0005S\u0000\u0000" +
                    "\u00bd\u00be\u0005I\u0000\u0000\u00be\u00bf\u0005S\u0000\u0000\u00bf\u00c0" +
                    "\u0005T\u0000\u0000\u00c0\u00c1\u0005_\u0000\u0000\u00c1\u00c3\u00050" +
                    "\u0000\u0000\u00c2\u00b7\u0001\u0000\u0000\u0000\u00c2\u00ba\u0001\u0000" +
                    "\u0000\u0000\u00c3 \u0001\u0000\u0000\u0000\u00c4\u00c6\u0007\u0001\u0000" +
                    "\u0000\u00c5\u00c4\u0001\u0000\u0000\u0000\u00c6\u00c7\u0001\u0000\u0000" +
                    "\u0000\u00c7\u00c5\u0001\u0000\u0000\u0000\u00c7\u00c8\u0001\u0000\u0000" +
                    "\u0000\u00c8\"\u0001\u0000\u0000\u0000\u00c9\u00ca\u0005-\u0000\u0000" +
                    "\u00ca$\u0001\u0000\u0000\u0000\u00cb\u00cd\u0005<\u0000\u0000\u00cc\u00ce" +
                    "\u0007\u0002\u0000\u0000\u00cd\u00cc\u0001\u0000\u0000\u0000\u00ce\u00cf" +
                    "\u0001\u0000\u0000\u0000\u00cf\u00cd\u0001\u0000\u0000\u0000\u00cf\u00d0" +
                    "\u0001\u0000\u0000\u0000\u00d0\u00d1\u0001\u0000\u0000\u0000\u00d1\u00d2" +
                    "\u0005>\u0000\u0000\u00d2&\u0001\u0000\u0000\u0000\u00d3\u00d5\u0007\u0003" +
                    "\u0000\u0000\u00d4\u00d3\u0001\u0000\u0000\u0000\u00d5\u00d6\u0001\u0000" +
                    "\u0000\u0000\u00d6\u00d4\u0001\u0000\u0000\u0000\u00d6\u00d7\u0001\u0000" +
                    "\u0000\u0000\u00d7(\u0001\u0000\u0000\u0000\n\u0000-3:>\u00b5\u00c2\u00c7" +
                    "\u00cf\u00d6\u0001\u0006\u0000\u0000";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}