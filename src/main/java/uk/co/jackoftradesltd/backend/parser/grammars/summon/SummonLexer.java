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

// Generated from SummonLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.summon;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class SummonLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, MSGT=2, UNIQUES=3, BASE=4, RACE_FLAG=5, FALLBACK=6, DESC=7, INTEGER=8, 
		COMMENT=9, INDENT=10, EOL=11, FLAG=12, STRING=13, STRING_NO_COLON=14;
	public static final int
		FLAG_MODE=1, FREE_TEXT_MODE=2, MSGT_FREE_TEXT=3;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "FLAG_MODE", "FREE_TEXT_MODE", "MSGT_FREE_TEXT"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"NAME", "MSGT", "UNIQUES", "BASE", "RACE_FLAG", "FALLBACK", "DESC", "INTEGER", 
			"COMMENT", "NL", "NEWLINE", "INDENT", "EOL", "FLAG_BODY", "FLAG", "STRING", 
			"STRING_NO_COLON"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'msgt:'", "'uniques:'", "'base:'", "'race-flag:'", 
			"'fallback:'", "'desc:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "MSGT", "UNIQUES", "BASE", "RACE_FLAG", "FALLBACK", "DESC", 
			"INTEGER", "COMMENT", "INDENT", "EOL", "FLAG", "STRING", "STRING_NO_COLON"
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


	public SummonLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SummonLexer.g4"; }

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
		case 11:
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
		"\u0004\u0000\u000e\u00c6\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff"+
		"\uffff\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0003\u0007j\b\u0007\u0001"+
		"\u0007\u0004\u0007m\b\u0007\u000b\u0007\f\u0007n\u0001\b\u0001\b\u0005"+
		"\bs\b\b\n\b\f\bv\t\b\u0001\b\u0001\b\u0003\bz\b\b\u0001\b\u0001\b\u0001"+
		"\t\u0005\t\u007f\b\t\n\t\f\t\u0082\t\t\u0001\t\u0004\t\u0085\b\t\u000b"+
		"\t\f\t\u0086\u0001\t\u0003\t\u008a\b\t\u0001\t\u0003\t\u008d\b\t\u0001"+
		"\t\u0004\t\u0090\b\t\u000b\t\f\t\u0091\u0001\t\u0003\t\u0095\b\t\u0001"+
		"\n\u0004\n\u0098\b\n\u000b\n\f\n\u0099\u0001\n\u0003\n\u009d\b\n\u0001"+
		"\n\u0003\n\u00a0\b\n\u0001\u000b\u0001\u000b\u0004\u000b\u00a4\b\u000b"+
		"\u000b\u000b\f\u000b\u00a5\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\r\u0001\r\u0005\r\u00b0\b\r\n\r\f\r\u00b3\t\r\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0004\u000f\u00ba\b\u000f"+
		"\u000b\u000f\f\u000f\u00bb\u0001\u000f\u0001\u000f\u0001\u0010\u0004\u0010"+
		"\u00c1\b\u0010\u000b\u0010\f\u0010\u00c2\u0001\u0010\u0001\u0010\u0000"+
		"\u0000\u0011\u0004\u0001\u0006\u0002\b\u0003\n\u0004\f\u0005\u000e\u0006"+
		"\u0010\u0007\u0012\b\u0014\t\u0016\u0000\u0018\u0000\u001a\n\u001c\u000b"+
		"\u001e\u0000 \f\"\r$\u000e\u0004\u0000\u0001\u0002\u0003\u0003\u0002\u0000"+
		"\n\n\r\r\u0002\u0000\t\t  \u0003\u000009AZ__\u00d0\u0000\u0004\u0001\u0000"+
		"\u0000\u0000\u0000\u0006\u0001\u0000\u0000\u0000\u0000\b\u0001\u0000\u0000"+
		"\u0000\u0000\n\u0001\u0000\u0000\u0000\u0000\f\u0001\u0000\u0000\u0000"+
		"\u0000\u000e\u0001\u0000\u0000\u0000\u0000\u0010\u0001\u0000\u0000\u0000"+
		"\u0000\u0012\u0001\u0000\u0000\u0000\u0000\u0014\u0001\u0000\u0000\u0000"+
		"\u0000\u001a\u0001\u0000\u0000\u0000\u0000\u001c\u0001\u0000\u0000\u0000"+
		"\u0001 \u0001\u0000\u0000\u0000\u0002\"\u0001\u0000\u0000\u0000\u0003"+
		"$\u0001\u0000\u0000\u0000\u0004&\u0001\u0000\u0000\u0000\u0006.\u0001"+
		"\u0000\u0000\u0000\b6\u0001\u0000\u0000\u0000\n?\u0001\u0000\u0000\u0000"+
		"\fG\u0001\u0000\u0000\u0000\u000eT\u0001\u0000\u0000\u0000\u0010`\u0001"+
		"\u0000\u0000\u0000\u0012i\u0001\u0000\u0000\u0000\u0014p\u0001\u0000\u0000"+
		"\u0000\u0016\u0094\u0001\u0000\u0000\u0000\u0018\u009f\u0001\u0000\u0000"+
		"\u0000\u001a\u00a1\u0001\u0000\u0000\u0000\u001c\u00a9\u0001\u0000\u0000"+
		"\u0000\u001e\u00ad\u0001\u0000\u0000\u0000 \u00b4\u0001\u0000\u0000\u0000"+
		"\"\u00b9\u0001\u0000\u0000\u0000$\u00c0\u0001\u0000\u0000\u0000&\'\u0005"+
		"n\u0000\u0000\'(\u0005a\u0000\u0000()\u0005m\u0000\u0000)*\u0005e\u0000"+
		"\u0000*+\u0005:\u0000\u0000+,\u0001\u0000\u0000\u0000,-\u0006\u0000\u0000"+
		"\u0000-\u0005\u0001\u0000\u0000\u0000./\u0005m\u0000\u0000/0\u0005s\u0000"+
		"\u000001\u0005g\u0000\u000012\u0005t\u0000\u000023\u0005:\u0000\u0000"+
		"34\u0001\u0000\u0000\u000045\u0006\u0001\u0001\u00005\u0007\u0001\u0000"+
		"\u0000\u000067\u0005u\u0000\u000078\u0005n\u0000\u000089\u0005i\u0000"+
		"\u00009:\u0005q\u0000\u0000:;\u0005u\u0000\u0000;<\u0005e\u0000\u0000"+
		"<=\u0005s\u0000\u0000=>\u0005:\u0000\u0000>\t\u0001\u0000\u0000\u0000"+
		"?@\u0005b\u0000\u0000@A\u0005a\u0000\u0000AB\u0005s\u0000\u0000BC\u0005"+
		"e\u0000\u0000CD\u0005:\u0000\u0000DE\u0001\u0000\u0000\u0000EF\u0006\u0003"+
		"\u0000\u0000F\u000b\u0001\u0000\u0000\u0000GH\u0005r\u0000\u0000HI\u0005"+
		"a\u0000\u0000IJ\u0005c\u0000\u0000JK\u0005e\u0000\u0000KL\u0005-\u0000"+
		"\u0000LM\u0005f\u0000\u0000MN\u0005l\u0000\u0000NO\u0005a\u0000\u0000"+
		"OP\u0005g\u0000\u0000PQ\u0005:\u0000\u0000QR\u0001\u0000\u0000\u0000R"+
		"S\u0006\u0004\u0002\u0000S\r\u0001\u0000\u0000\u0000TU\u0005f\u0000\u0000"+
		"UV\u0005a\u0000\u0000VW\u0005l\u0000\u0000WX\u0005l\u0000\u0000XY\u0005"+
		"b\u0000\u0000YZ\u0005a\u0000\u0000Z[\u0005c\u0000\u0000[\\\u0005k\u0000"+
		"\u0000\\]\u0005:\u0000\u0000]^\u0001\u0000\u0000\u0000^_\u0006\u0005\u0000"+
		"\u0000_\u000f\u0001\u0000\u0000\u0000`a\u0005d\u0000\u0000ab\u0005e\u0000"+
		"\u0000bc\u0005s\u0000\u0000cd\u0005c\u0000\u0000de\u0005:\u0000\u0000"+
		"ef\u0001\u0000\u0000\u0000fg\u0006\u0006\u0000\u0000g\u0011\u0001\u0000"+
		"\u0000\u0000hj\u0005-\u0000\u0000ih\u0001\u0000\u0000\u0000ij\u0001\u0000"+
		"\u0000\u0000jl\u0001\u0000\u0000\u0000km\u000209\u0000lk\u0001\u0000\u0000"+
		"\u0000mn\u0001\u0000\u0000\u0000nl\u0001\u0000\u0000\u0000no\u0001\u0000"+
		"\u0000\u0000o\u0013\u0001\u0000\u0000\u0000pt\u0005#\u0000\u0000qs\b\u0000"+
		"\u0000\u0000rq\u0001\u0000\u0000\u0000sv\u0001\u0000\u0000\u0000tr\u0001"+
		"\u0000\u0000\u0000tu\u0001\u0000\u0000\u0000uy\u0001\u0000\u0000\u0000"+
		"vt\u0001\u0000\u0000\u0000wz\u0003\u0016\t\u0000xz\u0005\u0000\u0000\u0001"+
		"yw\u0001\u0000\u0000\u0000yx\u0001\u0000\u0000\u0000z{\u0001\u0000\u0000"+
		"\u0000{|\u0006\b\u0003\u0000|\u0015\u0001\u0000\u0000\u0000}\u007f\u0007"+
		"\u0001\u0000\u0000~}\u0001\u0000\u0000\u0000\u007f\u0082\u0001\u0000\u0000"+
		"\u0000\u0080~\u0001\u0000\u0000\u0000\u0080\u0081\u0001\u0000\u0000\u0000"+
		"\u0081\u008c\u0001\u0000\u0000\u0000\u0082\u0080\u0001\u0000\u0000\u0000"+
		"\u0083\u0085\u0005\r\u0000\u0000\u0084\u0083\u0001\u0000\u0000\u0000\u0085"+
		"\u0086\u0001\u0000\u0000\u0000\u0086\u0084\u0001\u0000\u0000\u0000\u0086"+
		"\u0087\u0001\u0000\u0000\u0000\u0087\u0089\u0001\u0000\u0000\u0000\u0088"+
		"\u008a\u0005\n\u0000\u0000\u0089\u0088\u0001\u0000\u0000\u0000\u0089\u008a"+
		"\u0001\u0000\u0000\u0000\u008a\u008d\u0001\u0000\u0000\u0000\u008b\u008d"+
		"\u0005\n\u0000\u0000\u008c\u0084\u0001\u0000\u0000\u0000\u008c\u008b\u0001"+
		"\u0000\u0000\u0000\u008d\u0095\u0001\u0000\u0000\u0000\u008e\u0090\u0007"+
		"\u0001\u0000\u0000\u008f\u008e\u0001\u0000\u0000\u0000\u0090\u0091\u0001"+
		"\u0000\u0000\u0000\u0091\u008f\u0001\u0000\u0000\u0000\u0091\u0092\u0001"+
		"\u0000\u0000\u0000\u0092\u0093\u0001\u0000\u0000\u0000\u0093\u0095\u0005"+
		"\u0000\u0000\u0001\u0094\u0080\u0001\u0000\u0000\u0000\u0094\u008f\u0001"+
		"\u0000\u0000\u0000\u0095\u0017\u0001\u0000\u0000\u0000\u0096\u0098\u0005"+
		"\r\u0000\u0000\u0097\u0096\u0001\u0000\u0000\u0000\u0098\u0099\u0001\u0000"+
		"\u0000\u0000\u0099\u0097\u0001\u0000\u0000\u0000\u0099\u009a\u0001\u0000"+
		"\u0000\u0000\u009a\u009c\u0001\u0000\u0000\u0000\u009b\u009d\u0005\n\u0000"+
		"\u0000\u009c\u009b\u0001\u0000\u0000\u0000\u009c\u009d\u0001\u0000\u0000"+
		"\u0000\u009d\u00a0\u0001\u0000\u0000\u0000\u009e\u00a0\u0005\n\u0000\u0000"+
		"\u009f\u0097\u0001\u0000\u0000\u0000\u009f\u009e\u0001\u0000\u0000\u0000"+
		"\u00a0\u0019\u0001\u0000\u0000\u0000\u00a1\u00a3\u0004\u000b\u0000\u0000"+
		"\u00a2\u00a4\u0007\u0001\u0000\u0000\u00a3\u00a2\u0001\u0000\u0000\u0000"+
		"\u00a4\u00a5\u0001\u0000\u0000\u0000\u00a5\u00a3\u0001\u0000\u0000\u0000"+
		"\u00a5\u00a6\u0001\u0000\u0000\u0000\u00a6\u00a7\u0001\u0000\u0000\u0000"+
		"\u00a7\u00a8\u0006\u000b\u0003\u0000\u00a8\u001b\u0001\u0000\u0000\u0000"+
		"\u00a9\u00aa\u0003\u0016\t\u0000\u00aa\u00ab\u0001\u0000\u0000\u0000\u00ab"+
		"\u00ac\u0006\f\u0003\u0000\u00ac\u001d\u0001\u0000\u0000\u0000\u00ad\u00b1"+
		"\u0002AZ\u0000\u00ae\u00b0\u0007\u0002\u0000\u0000\u00af\u00ae\u0001\u0000"+
		"\u0000\u0000\u00b0\u00b3\u0001\u0000\u0000\u0000\u00b1\u00af\u0001\u0000"+
		"\u0000\u0000\u00b1\u00b2\u0001\u0000\u0000\u0000\u00b2\u001f\u0001\u0000"+
		"\u0000\u0000\u00b3\u00b1\u0001\u0000\u0000\u0000\u00b4\u00b5\u0003\u001e"+
		"\r\u0000\u00b5\u00b6\u0001\u0000\u0000\u0000\u00b6\u00b7\u0006\u000e\u0004"+
		"\u0000\u00b7!\u0001\u0000\u0000\u0000\u00b8\u00ba\b\u0000\u0000\u0000"+
		"\u00b9\u00b8\u0001\u0000\u0000\u0000\u00ba\u00bb\u0001\u0000\u0000\u0000"+
		"\u00bb\u00b9\u0001\u0000\u0000\u0000\u00bb\u00bc\u0001\u0000\u0000\u0000"+
		"\u00bc\u00bd\u0001\u0000\u0000\u0000\u00bd\u00be\u0006\u000f\u0004\u0000"+
		"\u00be#\u0001\u0000\u0000\u0000\u00bf\u00c1\b\u0000\u0000\u0000\u00c0"+
		"\u00bf\u0001\u0000\u0000\u0000\u00c1\u00c2\u0001\u0000\u0000\u0000\u00c2"+
		"\u00c0\u0001\u0000\u0000\u0000\u00c2\u00c3\u0001\u0000\u0000\u0000\u00c3"+
		"\u00c4\u0001\u0000\u0000\u0000\u00c4\u00c5\u0006\u0010\u0005\u0000\u00c5"+
		"%\u0001\u0000\u0000\u0000\u0015\u0000\u0001\u0002\u0003inty\u0080\u0086"+
		"\u0089\u008c\u0091\u0094\u0099\u009c\u009f\u00a5\u00b1\u00bb\u00c2\u0006"+
		"\u0005\u0002\u0000\u0002\u0003\u0000\u0005\u0001\u0000\u0006\u0000\u0000"+
		"\u0004\u0000\u0000\u0002\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}