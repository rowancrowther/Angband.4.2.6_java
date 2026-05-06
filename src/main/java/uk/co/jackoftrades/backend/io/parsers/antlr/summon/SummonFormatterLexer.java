// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/SummonFormatter.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.summon;

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
			COMMENT = 1, EOL = 2, NAME = 3, MSGTAG = 4, UNIQUES = 5, BASE = 6, RACE_FLAG = 7, FALLBACK = 8,
			DESC = 9, WORD = 10, BOOLEAN = 11;
	public static String[] channelNames = {
			"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
			"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[]{
				"COMMENT", "EOL", "NAME", "MSGTAG", "UNIQUES", "BASE", "RACE_FLAG", "FALLBACK",
				"DESC", "WORD", "BOOLEAN"
		};
	}

	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[]{
				null, null, null, "'name:'", "'msgt:'", "'uniques:'", "'base:'", "'race-flag:'",
				"'fallback:'", "'desc:'"
		};
	}

	private static final String[] _LITERAL_NAMES = makeLiteralNames();

	private static String[] makeSymbolicNames() {
		return new String[]{
				null, "COMMENT", "EOL", "NAME", "MSGTAG", "UNIQUES", "BASE", "RACE_FLAG",
				"FALLBACK", "DESC", "WORD", "BOOLEAN"
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
			"\u0004\u0000\u000bq\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001" +
					"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004" +
					"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007" +
					"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0001\u0000" +
					"\u0001\u0000\u0005\u0000\u001a\b\u0000\n\u0000\f\u0000\u001d\t\u0000\u0001" +
					"\u0000\u0004\u0000 \b\u0000\u000b\u0000\f\u0000!\u0001\u0000\u0001\u0000" +
					"\u0001\u0001\u0005\u0001\'\b\u0001\n\u0001\f\u0001*\t\u0001\u0001\u0001" +
					"\u0001\u0001\u0005\u0001.\b\u0001\n\u0001\f\u00011\t\u0001\u0001\u0001" +
					"\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002" +
					"\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
					"\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
					"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005" +
					"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006" +
					"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
					"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007" +
					"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007" +
					"\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0004" +
					"\tl\b\t\u000b\t\f\tm\u0001\n\u0001\n\u0000\u0000\u000b\u0001\u0001\u0003" +
					"\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011" +
					"\t\u0013\n\u0015\u000b\u0001\u0000\u0002\u0001\u0000\n\n\u0004\u0000 " +
					" AZ__azu\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000" +
					"\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000" +
					"\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000" +
					"\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000" +
					"\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000" +
					"\u0015\u0001\u0000\u0000\u0000\u0001\u0017\u0001\u0000\u0000\u0000\u0003" +
					"(\u0001\u0000\u0000\u0000\u00054\u0001\u0000\u0000\u0000\u0007:\u0001" +
					"\u0000\u0000\u0000\t@\u0001\u0000\u0000\u0000\u000bI\u0001\u0000\u0000" +
					"\u0000\rO\u0001\u0000\u0000\u0000\u000fZ\u0001\u0000\u0000\u0000\u0011" +
					"d\u0001\u0000\u0000\u0000\u0013k\u0001\u0000\u0000\u0000\u0015o\u0001" +
					"\u0000\u0000\u0000\u0017\u001b\u0005#\u0000\u0000\u0018\u001a\b\u0000" +
					"\u0000\u0000\u0019\u0018\u0001\u0000\u0000\u0000\u001a\u001d\u0001\u0000" +
					"\u0000\u0000\u001b\u0019\u0001\u0000\u0000\u0000\u001b\u001c\u0001\u0000" +
					"\u0000\u0000\u001c\u001f\u0001\u0000\u0000\u0000\u001d\u001b\u0001\u0000" +
					"\u0000\u0000\u001e \u0005\n\u0000\u0000\u001f\u001e\u0001\u0000\u0000" +
					"\u0000 !\u0001\u0000\u0000\u0000!\u001f\u0001\u0000\u0000\u0000!\"\u0001" +
					"\u0000\u0000\u0000\"#\u0001\u0000\u0000\u0000#$\u0006\u0000\u0000\u0000" +
					"$\u0002\u0001\u0000\u0000\u0000%\'\u0005\r\u0000\u0000&%\u0001\u0000\u0000" +
					"\u0000\'*\u0001\u0000\u0000\u0000(&\u0001\u0000\u0000\u0000()\u0001\u0000" +
					"\u0000\u0000)+\u0001\u0000\u0000\u0000*(\u0001\u0000\u0000\u0000+/\u0005" +
					"\n\u0000\u0000,.\u0005 \u0000\u0000-,\u0001\u0000\u0000\u0000.1\u0001" +
					"\u0000\u0000\u0000/-\u0001\u0000\u0000\u0000/0\u0001\u0000\u0000\u0000" +
					"02\u0001\u0000\u0000\u00001/\u0001\u0000\u0000\u000023\u0006\u0001\u0000" +
					"\u00003\u0004\u0001\u0000\u0000\u000045\u0005n\u0000\u000056\u0005a\u0000" +
					"\u000067\u0005m\u0000\u000078\u0005e\u0000\u000089\u0005:\u0000\u0000" +
					"9\u0006\u0001\u0000\u0000\u0000:;\u0005m\u0000\u0000;<\u0005s\u0000\u0000" +
					"<=\u0005g\u0000\u0000=>\u0005t\u0000\u0000>?\u0005:\u0000\u0000?\b\u0001" +
					"\u0000\u0000\u0000@A\u0005u\u0000\u0000AB\u0005n\u0000\u0000BC\u0005i" +
					"\u0000\u0000CD\u0005q\u0000\u0000DE\u0005u\u0000\u0000EF\u0005e\u0000" +
					"\u0000FG\u0005s\u0000\u0000GH\u0005:\u0000\u0000H\n\u0001\u0000\u0000" +
					"\u0000IJ\u0005b\u0000\u0000JK\u0005a\u0000\u0000KL\u0005s\u0000\u0000" +
					"LM\u0005e\u0000\u0000MN\u0005:\u0000\u0000N\f\u0001\u0000\u0000\u0000" +
					"OP\u0005r\u0000\u0000PQ\u0005a\u0000\u0000QR\u0005c\u0000\u0000RS\u0005" +
					"e\u0000\u0000ST\u0005-\u0000\u0000TU\u0005f\u0000\u0000UV\u0005l\u0000" +
					"\u0000VW\u0005a\u0000\u0000WX\u0005g\u0000\u0000XY\u0005:\u0000\u0000" +
					"Y\u000e\u0001\u0000\u0000\u0000Z[\u0005f\u0000\u0000[\\\u0005a\u0000\u0000" +
					"\\]\u0005l\u0000\u0000]^\u0005l\u0000\u0000^_\u0005b\u0000\u0000_`\u0005" +
					"a\u0000\u0000`a\u0005c\u0000\u0000ab\u0005k\u0000\u0000bc\u0005:\u0000" +
					"\u0000c\u0010\u0001\u0000\u0000\u0000de\u0005d\u0000\u0000ef\u0005e\u0000" +
					"\u0000fg\u0005s\u0000\u0000gh\u0005c\u0000\u0000hi\u0005:\u0000\u0000" +
					"i\u0012\u0001\u0000\u0000\u0000jl\u0007\u0001\u0000\u0000kj\u0001\u0000" +
					"\u0000\u0000lm\u0001\u0000\u0000\u0000mk\u0001\u0000\u0000\u0000mn\u0001" +
					"\u0000\u0000\u0000n\u0014\u0001\u0000\u0000\u0000op\u000201\u0000p\u0016" +
					"\u0001\u0000\u0000\u0000\u0006\u0000\u001b!(/m\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
			new ATNDeserializer().deserialize(_serializedATN.toCharArray());

	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}