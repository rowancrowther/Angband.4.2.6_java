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
 *    Java code and ANTLR4 grammars copyright (c) Rowan Crowther 2026
 */

// Generated from BrandLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.brand;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class BrandLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		CODE=1, NAME=2, MULTIPLIER=3, O_MULTIPLIER=4, POWER=5, VERB=6, RESIST_FLAG=7, 
		VULN_FLAG=8, INTEGER=9, COMMENT=10, INDENT=11, EOL=12, FLAG=13, STRING=14;
	public static final int
		FLAG_MODE=1, FREE_TEXT_MODE=2;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "FLAG_MODE", "FREE_TEXT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"CODE", "NAME", "MULTIPLIER", "O_MULTIPLIER", "POWER", "VERB", "RESIST_FLAG", 
			"VULN_FLAG", "INTEGER", "COMMENT", "NL", "NEWLINE", "INDENT", "EOL", 
			"FLAG_BODY", "FLAG", "STRING"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'code:'", "'name:'", "'multiplier:'", "'o-multiplier:'", "'power:'", 
			"'verb:'", "'resist-flag:'", "'vuln-flag:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "CODE", "NAME", "MULTIPLIER", "O_MULTIPLIER", "POWER", "VERB", 
			"RESIST_FLAG", "VULN_FLAG", "INTEGER", "COMMENT", "INDENT", "EOL", "FLAG", 
			"STRING"
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


	public BrandLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "BrandLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 12:
			return INDENT_sempred((RuleContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean INDENT_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return  getCharPositionInLine() == 0 ;
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0000\u000e\u00c9\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff"+
		"\uffff\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007"+
		"\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007"+
		"\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b"+
		"\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007"+
		"\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f\u0002"+
		"\u0010\u0007\u0010\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\b\u0003\b|\b\b\u0001\b\u0004\b\u007f"+
		"\b\b\u000b\b\f\b\u0080\u0001\t\u0001\t\u0005\t\u0085\b\t\n\t\f\t\u0088"+
		"\t\t\u0001\t\u0001\t\u0003\t\u008c\b\t\u0001\t\u0001\t\u0001\n\u0005\n"+
		"\u0091\b\n\n\n\f\n\u0094\t\n\u0001\n\u0004\n\u0097\b\n\u000b\n\f\n\u0098"+
		"\u0001\n\u0003\n\u009c\b\n\u0001\n\u0003\n\u009f\b\n\u0001\u000b\u0004"+
		"\u000b\u00a2\b\u000b\u000b\u000b\f\u000b\u00a3\u0001\u000b\u0003\u000b"+
		"\u00a7\b\u000b\u0001\u000b\u0003\u000b\u00aa\b\u000b\u0001\f\u0001\f\u0004"+
		"\f\u00ae\b\f\u000b\f\f\f\u00af\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r"+
		"\u0001\r\u0001\u000e\u0001\u000e\u0005\u000e\u00ba\b\u000e\n\u000e\f\u000e"+
		"\u00bd\t\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010"+
		"\u0004\u0010\u00c4\b\u0010\u000b\u0010\f\u0010\u00c5\u0001\u0010\u0001"+
		"\u0010\u0000\u0000\u0011\u0003\u0001\u0005\u0002\u0007\u0003\t\u0004\u000b"+
		"\u0005\r\u0006\u000f\u0007\u0011\b\u0013\t\u0015\n\u0017\u0000\u0019\u0000"+
		"\u001b\u000b\u001d\f\u001f\u0000!\r#\u000e\u0003\u0000\u0001\u0002\u0003"+
		"\u0002\u0000\n\n\r\r\u0002\u0000\t\t  \u0003\u000009AZ__\u00d1\u0000\u0003"+
		"\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007"+
		"\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001"+
		"\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000"+
		"\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000"+
		"\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000"+
		"\u0000\u0000\u0000\u001d\u0001\u0000\u0000\u0000\u0001!\u0001\u0000\u0000"+
		"\u0000\u0002#\u0001\u0000\u0000\u0000\u0003%\u0001\u0000\u0000\u0000\u0005"+
		"-\u0001\u0000\u0000\u0000\u00075\u0001\u0000\u0000\u0000\tA\u0001\u0000"+
		"\u0000\u0000\u000bO\u0001\u0000\u0000\u0000\rV\u0001\u0000\u0000\u0000"+
		"\u000f^\u0001\u0000\u0000\u0000\u0011m\u0001\u0000\u0000\u0000\u0013{"+
		"\u0001\u0000\u0000\u0000\u0015\u0082\u0001\u0000\u0000\u0000\u0017\u0092"+
		"\u0001\u0000\u0000\u0000\u0019\u00a9\u0001\u0000\u0000\u0000\u001b\u00ab"+
		"\u0001\u0000\u0000\u0000\u001d\u00b3\u0001\u0000\u0000\u0000\u001f\u00b7"+
		"\u0001\u0000\u0000\u0000!\u00be\u0001\u0000\u0000\u0000#\u00c3\u0001\u0000"+
		"\u0000\u0000%&\u0005c\u0000\u0000&\'\u0005o\u0000\u0000\'(\u0005d\u0000"+
		"\u0000()\u0005e\u0000\u0000)*\u0005:\u0000\u0000*+\u0001\u0000\u0000\u0000"+
		"+,\u0006\u0000\u0000\u0000,\u0004\u0001\u0000\u0000\u0000-.\u0005n\u0000"+
		"\u0000./\u0005a\u0000\u0000/0\u0005m\u0000\u000001\u0005e\u0000\u0000"+
		"12\u0005:\u0000\u000023\u0001\u0000\u0000\u000034\u0006\u0001\u0000\u0000"+
		"4\u0006\u0001\u0000\u0000\u000056\u0005m\u0000\u000067\u0005u\u0000\u0000"+
		"78\u0005l\u0000\u000089\u0005t\u0000\u00009:\u0005i\u0000\u0000:;\u0005"+
		"p\u0000\u0000;<\u0005l\u0000\u0000<=\u0005i\u0000\u0000=>\u0005e\u0000"+
		"\u0000>?\u0005r\u0000\u0000?@\u0005:\u0000\u0000@\b\u0001\u0000\u0000"+
		"\u0000AB\u0005o\u0000\u0000BC\u0005-\u0000\u0000CD\u0005m\u0000\u0000"+
		"DE\u0005u\u0000\u0000EF\u0005l\u0000\u0000FG\u0005t\u0000\u0000GH\u0005"+
		"i\u0000\u0000HI\u0005p\u0000\u0000IJ\u0005l\u0000\u0000JK\u0005i\u0000"+
		"\u0000KL\u0005e\u0000\u0000LM\u0005r\u0000\u0000MN\u0005:\u0000\u0000"+
		"N\n\u0001\u0000\u0000\u0000OP\u0005p\u0000\u0000PQ\u0005o\u0000\u0000"+
		"QR\u0005w\u0000\u0000RS\u0005e\u0000\u0000ST\u0005r\u0000\u0000TU\u0005"+
		":\u0000\u0000U\f\u0001\u0000\u0000\u0000VW\u0005v\u0000\u0000WX\u0005"+
		"e\u0000\u0000XY\u0005r\u0000\u0000YZ\u0005b\u0000\u0000Z[\u0005:\u0000"+
		"\u0000[\\\u0001\u0000\u0000\u0000\\]\u0006\u0005\u0000\u0000]\u000e\u0001"+
		"\u0000\u0000\u0000^_\u0005r\u0000\u0000_`\u0005e\u0000\u0000`a\u0005s"+
		"\u0000\u0000ab\u0005i\u0000\u0000bc\u0005s\u0000\u0000cd\u0005t\u0000"+
		"\u0000de\u0005-\u0000\u0000ef\u0005f\u0000\u0000fg\u0005l\u0000\u0000"+
		"gh\u0005a\u0000\u0000hi\u0005g\u0000\u0000ij\u0005:\u0000\u0000jk\u0001"+
		"\u0000\u0000\u0000kl\u0006\u0006\u0001\u0000l\u0010\u0001\u0000\u0000"+
		"\u0000mn\u0005v\u0000\u0000no\u0005u\u0000\u0000op\u0005l\u0000\u0000"+
		"pq\u0005n\u0000\u0000qr\u0005-\u0000\u0000rs\u0005f\u0000\u0000st\u0005"+
		"l\u0000\u0000tu\u0005a\u0000\u0000uv\u0005g\u0000\u0000vw\u0005:\u0000"+
		"\u0000wx\u0001\u0000\u0000\u0000xy\u0006\u0007\u0001\u0000y\u0012\u0001"+
		"\u0000\u0000\u0000z|\u0005-\u0000\u0000{z\u0001\u0000\u0000\u0000{|\u0001"+
		"\u0000\u0000\u0000|~\u0001\u0000\u0000\u0000}\u007f\u000209\u0000~}\u0001"+
		"\u0000\u0000\u0000\u007f\u0080\u0001\u0000\u0000\u0000\u0080~\u0001\u0000"+
		"\u0000\u0000\u0080\u0081\u0001\u0000\u0000\u0000\u0081\u0014\u0001\u0000"+
		"\u0000\u0000\u0082\u0086\u0005#\u0000\u0000\u0083\u0085\b\u0000\u0000"+
		"\u0000\u0084\u0083\u0001\u0000\u0000\u0000\u0085\u0088\u0001\u0000\u0000"+
		"\u0000\u0086\u0084\u0001\u0000\u0000\u0000\u0086\u0087\u0001\u0000\u0000"+
		"\u0000\u0087\u008b\u0001\u0000\u0000\u0000\u0088\u0086\u0001\u0000\u0000"+
		"\u0000\u0089\u008c\u0003\u0017\n\u0000\u008a\u008c\u0005\u0000\u0000\u0001"+
		"\u008b\u0089\u0001\u0000\u0000\u0000\u008b\u008a\u0001\u0000\u0000\u0000"+
		"\u008c\u008d\u0001\u0000\u0000\u0000\u008d\u008e\u0006\t\u0002\u0000\u008e"+
		"\u0016\u0001\u0000\u0000\u0000\u008f\u0091\u0007\u0001\u0000\u0000\u0090"+
		"\u008f\u0001\u0000\u0000\u0000\u0091\u0094\u0001\u0000\u0000\u0000\u0092"+
		"\u0090\u0001\u0000\u0000\u0000\u0092\u0093\u0001\u0000\u0000\u0000\u0093"+
		"\u009e\u0001\u0000\u0000\u0000\u0094\u0092\u0001\u0000\u0000\u0000\u0095"+
		"\u0097\u0005\r\u0000\u0000\u0096\u0095\u0001\u0000\u0000\u0000\u0097\u0098"+
		"\u0001\u0000\u0000\u0000\u0098\u0096\u0001\u0000\u0000\u0000\u0098\u0099"+
		"\u0001\u0000\u0000\u0000\u0099\u009b\u0001\u0000\u0000\u0000\u009a\u009c"+
		"\u0005\n\u0000\u0000\u009b\u009a\u0001\u0000\u0000\u0000\u009b\u009c\u0001"+
		"\u0000\u0000\u0000\u009c\u009f\u0001\u0000\u0000\u0000\u009d\u009f\u0005"+
		"\n\u0000\u0000\u009e\u0096\u0001\u0000\u0000\u0000\u009e\u009d\u0001\u0000"+
		"\u0000\u0000\u009f\u0018\u0001\u0000\u0000\u0000\u00a0\u00a2\u0005\r\u0000"+
		"\u0000\u00a1\u00a0\u0001\u0000\u0000\u0000\u00a2\u00a3\u0001\u0000\u0000"+
		"\u0000\u00a3\u00a1\u0001\u0000\u0000\u0000\u00a3\u00a4\u0001\u0000\u0000"+
		"\u0000\u00a4\u00a6\u0001\u0000\u0000\u0000\u00a5\u00a7\u0005\n\u0000\u0000"+
		"\u00a6\u00a5\u0001\u0000\u0000\u0000\u00a6\u00a7\u0001\u0000\u0000\u0000"+
		"\u00a7\u00aa\u0001\u0000\u0000\u0000\u00a8\u00aa\u0005\n\u0000\u0000\u00a9"+
		"\u00a1\u0001\u0000\u0000\u0000\u00a9\u00a8\u0001\u0000\u0000\u0000\u00aa"+
		"\u001a\u0001\u0000\u0000\u0000\u00ab\u00ad\u0004\f\u0000\u0000\u00ac\u00ae"+
		"\u0007\u0001\u0000\u0000\u00ad\u00ac\u0001\u0000\u0000\u0000\u00ae\u00af"+
		"\u0001\u0000\u0000\u0000\u00af\u00ad\u0001\u0000\u0000\u0000\u00af\u00b0"+
		"\u0001\u0000\u0000\u0000\u00b0\u00b1\u0001\u0000\u0000\u0000\u00b1\u00b2"+
		"\u0006\f\u0002\u0000\u00b2\u001c\u0001\u0000\u0000\u0000\u00b3\u00b4\u0003"+
		"\u0017\n\u0000\u00b4\u00b5\u0001\u0000\u0000\u0000\u00b5\u00b6\u0006\r"+
		"\u0002\u0000\u00b6\u001e\u0001\u0000\u0000\u0000\u00b7\u00bb\u0002AZ\u0000"+
		"\u00b8\u00ba\u0007\u0002\u0000\u0000\u00b9\u00b8\u0001\u0000\u0000\u0000"+
		"\u00ba\u00bd\u0001\u0000\u0000\u0000\u00bb\u00b9\u0001\u0000\u0000\u0000"+
		"\u00bb\u00bc\u0001\u0000\u0000\u0000\u00bc \u0001\u0000\u0000\u0000\u00bd"+
		"\u00bb\u0001\u0000\u0000\u0000\u00be\u00bf\u0003\u001f\u000e\u0000\u00bf"+
		"\u00c0\u0001\u0000\u0000\u0000\u00c0\u00c1\u0006\u000f\u0003\u0000\u00c1"+
		"\"\u0001\u0000\u0000\u0000\u00c2\u00c4\b\u0000\u0000\u0000\u00c3\u00c2"+
		"\u0001\u0000\u0000\u0000\u00c4\u00c5\u0001\u0000\u0000\u0000\u00c5\u00c3"+
		"\u0001\u0000\u0000\u0000\u00c5\u00c6\u0001\u0000\u0000\u0000\u00c6\u00c7"+
		"\u0001\u0000\u0000\u0000\u00c7\u00c8\u0006\u0010\u0003\u0000\u00c8$\u0001"+
		"\u0000\u0000\u0000\u0011\u0000\u0001\u0002{\u0080\u0086\u008b\u0092\u0098"+
		"\u009b\u009e\u00a3\u00a6\u00a9\u00af\u00bb\u00c5\u0004\u0005\u0002\u0000"+
		"\u0005\u0001\u0000\u0006\u0000\u0000\u0004\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}