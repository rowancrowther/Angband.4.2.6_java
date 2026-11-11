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

// Generated from VaultLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.vault;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class VaultLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, TYPE=2, RATING=3, ROWS=4, COLUMNS=5, MIN_DEPTH=6, MAX_DEPTH=7, 
		FLAGS=8, D=9, INTEGER=10, COMMENT=11, INDENT=12, EOL=13, FLAG=14, OR=15, 
		FLAG_EOL=16, STRING=17, END_OF_LINE=18;
	public static final int
		FLAG_MODE=1, REST_OF_LINE=2;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "FLAG_MODE", "REST_OF_LINE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"NAME", "TYPE", "RATING", "ROWS", "COLUMNS", "MIN_DEPTH", "MAX_DEPTH", 
			"FLAGS", "D", "INTEGER", "COMMENT", "NL", "NEWLINE", "INDENT", "EOL", 
			"FLAG_BODY", "FLAG", "OR", "FLAG_EOL", "STRING", "END_OF_LINE"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'type:'", "'rating:'", "'rows:'", "'columns:'", "'min-depth:'", 
			"'max-depth:'", "'flags:'", "'D:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "TYPE", "RATING", "ROWS", "COLUMNS", "MIN_DEPTH", "MAX_DEPTH", 
			"FLAGS", "D", "INTEGER", "COMMENT", "INDENT", "EOL", "FLAG", "OR", "FLAG_EOL", 
			"STRING", "END_OF_LINE"
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


	public VaultLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "VaultLexer.g4"; }

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
		case 13:
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
		"\u0004\u0000\u0012\u00d4\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff"+
		"\uffff\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007"+
		"\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007"+
		"\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b"+
		"\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007"+
		"\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f\u0002"+
		"\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012\u0002"+
		"\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0003"+
		"\tz\b\t\u0001\t\u0004\t}\b\t\u000b\t\f\t~\u0001\n\u0001\n\u0005\n\u0083"+
		"\b\n\n\n\f\n\u0086\t\n\u0001\n\u0001\n\u0003\n\u008a\b\n\u0001\n\u0001"+
		"\n\u0001\u000b\u0005\u000b\u008f\b\u000b\n\u000b\f\u000b\u0092\t\u000b"+
		"\u0001\u000b\u0004\u000b\u0095\b\u000b\u000b\u000b\f\u000b\u0096\u0001"+
		"\u000b\u0003\u000b\u009a\b\u000b\u0001\u000b\u0003\u000b\u009d\b\u000b"+
		"\u0001\f\u0004\f\u00a0\b\f\u000b\f\f\f\u00a1\u0001\f\u0003\f\u00a5\b\f"+
		"\u0001\f\u0003\f\u00a8\b\f\u0001\r\u0001\r\u0004\r\u00ac\b\r\u000b\r\f"+
		"\r\u00ad\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000f\u0001\u000f\u0005\u000f\u00b8\b\u000f\n\u000f\f\u000f\u00bb"+
		"\t\u000f\u0001\u0010\u0001\u0010\u0001\u0011\u0003\u0011\u00c0\b\u0011"+
		"\u0001\u0011\u0001\u0011\u0003\u0011\u00c4\b\u0011\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0013\u0004\u0013\u00cc\b\u0013"+
		"\u000b\u0013\f\u0013\u00cd\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0000\u0000\u0015\u0003\u0001\u0005\u0002\u0007\u0003\t\u0004"+
		"\u000b\u0005\r\u0006\u000f\u0007\u0011\b\u0013\t\u0015\n\u0017\u000b\u0019"+
		"\u0000\u001b\u0000\u001d\f\u001f\r!\u0000#\u000e%\u000f\'\u0010)\u0011"+
		"+\u0012\u0003\u0000\u0001\u0002\u0003\u0002\u0000\n\n\r\r\u0002\u0000"+
		"\t\t  \u0003\u000009AZ__\u00de\u0000\u0003\u0001\u0000\u0000\u0000\u0000"+
		"\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000"+
		"\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r"+
		"\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011"+
		"\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015"+
		"\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u001d"+
		"\u0001\u0000\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0001#\u0001"+
		"\u0000\u0000\u0000\u0001%\u0001\u0000\u0000\u0000\u0001\'\u0001\u0000"+
		"\u0000\u0000\u0002)\u0001\u0000\u0000\u0000\u0002+\u0001\u0000\u0000\u0000"+
		"\u0003-\u0001\u0000\u0000\u0000\u00055\u0001\u0000\u0000\u0000\u0007="+
		"\u0001\u0000\u0000\u0000\tE\u0001\u0000\u0000\u0000\u000bK\u0001\u0000"+
		"\u0000\u0000\rT\u0001\u0000\u0000\u0000\u000f_\u0001\u0000\u0000\u0000"+
		"\u0011j\u0001\u0000\u0000\u0000\u0013s\u0001\u0000\u0000\u0000\u0015y"+
		"\u0001\u0000\u0000\u0000\u0017\u0080\u0001\u0000\u0000\u0000\u0019\u0090"+
		"\u0001\u0000\u0000\u0000\u001b\u00a7\u0001\u0000\u0000\u0000\u001d\u00a9"+
		"\u0001\u0000\u0000\u0000\u001f\u00b1\u0001\u0000\u0000\u0000!\u00b5\u0001"+
		"\u0000\u0000\u0000#\u00bc\u0001\u0000\u0000\u0000%\u00bf\u0001\u0000\u0000"+
		"\u0000\'\u00c5\u0001\u0000\u0000\u0000)\u00cb\u0001\u0000\u0000\u0000"+
		"+\u00cf\u0001\u0000\u0000\u0000-.\u0005n\u0000\u0000./\u0005a\u0000\u0000"+
		"/0\u0005m\u0000\u000001\u0005e\u0000\u000012\u0005:\u0000\u000023\u0001"+
		"\u0000\u0000\u000034\u0006\u0000\u0000\u00004\u0004\u0001\u0000\u0000"+
		"\u000056\u0005t\u0000\u000067\u0005y\u0000\u000078\u0005p\u0000\u0000"+
		"89\u0005e\u0000\u00009:\u0005:\u0000\u0000:;\u0001\u0000\u0000\u0000;"+
		"<\u0006\u0001\u0000\u0000<\u0006\u0001\u0000\u0000\u0000=>\u0005r\u0000"+
		"\u0000>?\u0005a\u0000\u0000?@\u0005t\u0000\u0000@A\u0005i\u0000\u0000"+
		"AB\u0005n\u0000\u0000BC\u0005g\u0000\u0000CD\u0005:\u0000\u0000D\b\u0001"+
		"\u0000\u0000\u0000EF\u0005r\u0000\u0000FG\u0005o\u0000\u0000GH\u0005w"+
		"\u0000\u0000HI\u0005s\u0000\u0000IJ\u0005:\u0000\u0000J\n\u0001\u0000"+
		"\u0000\u0000KL\u0005c\u0000\u0000LM\u0005o\u0000\u0000MN\u0005l\u0000"+
		"\u0000NO\u0005u\u0000\u0000OP\u0005m\u0000\u0000PQ\u0005n\u0000\u0000"+
		"QR\u0005s\u0000\u0000RS\u0005:\u0000\u0000S\f\u0001\u0000\u0000\u0000"+
		"TU\u0005m\u0000\u0000UV\u0005i\u0000\u0000VW\u0005n\u0000\u0000WX\u0005"+
		"-\u0000\u0000XY\u0005d\u0000\u0000YZ\u0005e\u0000\u0000Z[\u0005p\u0000"+
		"\u0000[\\\u0005t\u0000\u0000\\]\u0005h\u0000\u0000]^\u0005:\u0000\u0000"+
		"^\u000e\u0001\u0000\u0000\u0000_`\u0005m\u0000\u0000`a\u0005a\u0000\u0000"+
		"ab\u0005x\u0000\u0000bc\u0005-\u0000\u0000cd\u0005d\u0000\u0000de\u0005"+
		"e\u0000\u0000ef\u0005p\u0000\u0000fg\u0005t\u0000\u0000gh\u0005h\u0000"+
		"\u0000hi\u0005:\u0000\u0000i\u0010\u0001\u0000\u0000\u0000jk\u0005f\u0000"+
		"\u0000kl\u0005l\u0000\u0000lm\u0005a\u0000\u0000mn\u0005g\u0000\u0000"+
		"no\u0005s\u0000\u0000op\u0005:\u0000\u0000pq\u0001\u0000\u0000\u0000q"+
		"r\u0006\u0007\u0001\u0000r\u0012\u0001\u0000\u0000\u0000st\u0005D\u0000"+
		"\u0000tu\u0005:\u0000\u0000uv\u0001\u0000\u0000\u0000vw\u0006\b\u0000"+
		"\u0000w\u0014\u0001\u0000\u0000\u0000xz\u0005-\u0000\u0000yx\u0001\u0000"+
		"\u0000\u0000yz\u0001\u0000\u0000\u0000z|\u0001\u0000\u0000\u0000{}\u0002"+
		"09\u0000|{\u0001\u0000\u0000\u0000}~\u0001\u0000\u0000\u0000~|\u0001\u0000"+
		"\u0000\u0000~\u007f\u0001\u0000\u0000\u0000\u007f\u0016\u0001\u0000\u0000"+
		"\u0000\u0080\u0084\u0005#\u0000\u0000\u0081\u0083\b\u0000\u0000\u0000"+
		"\u0082\u0081\u0001\u0000\u0000\u0000\u0083\u0086\u0001\u0000\u0000\u0000"+
		"\u0084\u0082\u0001\u0000\u0000\u0000\u0084\u0085\u0001\u0000\u0000\u0000"+
		"\u0085\u0089\u0001\u0000\u0000\u0000\u0086\u0084\u0001\u0000\u0000\u0000"+
		"\u0087\u008a\u0003\u0019\u000b\u0000\u0088\u008a\u0005\u0000\u0000\u0001"+
		"\u0089\u0087\u0001\u0000\u0000\u0000\u0089\u0088\u0001\u0000\u0000\u0000"+
		"\u008a\u008b\u0001\u0000\u0000\u0000\u008b\u008c\u0006\n\u0002\u0000\u008c"+
		"\u0018\u0001\u0000\u0000\u0000\u008d\u008f\u0007\u0001\u0000\u0000\u008e"+
		"\u008d\u0001\u0000\u0000\u0000\u008f\u0092\u0001\u0000\u0000\u0000\u0090"+
		"\u008e\u0001\u0000\u0000\u0000\u0090\u0091\u0001\u0000\u0000\u0000\u0091"+
		"\u009c\u0001\u0000\u0000\u0000\u0092\u0090\u0001\u0000\u0000\u0000\u0093"+
		"\u0095\u0005\r\u0000\u0000\u0094\u0093\u0001\u0000\u0000\u0000\u0095\u0096"+
		"\u0001\u0000\u0000\u0000\u0096\u0094\u0001\u0000\u0000\u0000\u0096\u0097"+
		"\u0001\u0000\u0000\u0000\u0097\u0099\u0001\u0000\u0000\u0000\u0098\u009a"+
		"\u0005\n\u0000\u0000\u0099\u0098\u0001\u0000\u0000\u0000\u0099\u009a\u0001"+
		"\u0000\u0000\u0000\u009a\u009d\u0001\u0000\u0000\u0000\u009b\u009d\u0005"+
		"\n\u0000\u0000\u009c\u0094\u0001\u0000\u0000\u0000\u009c\u009b\u0001\u0000"+
		"\u0000\u0000\u009d\u001a\u0001\u0000\u0000\u0000\u009e\u00a0\u0005\r\u0000"+
		"\u0000\u009f\u009e\u0001\u0000\u0000\u0000\u00a0\u00a1\u0001\u0000\u0000"+
		"\u0000\u00a1\u009f\u0001\u0000\u0000\u0000\u00a1\u00a2\u0001\u0000\u0000"+
		"\u0000\u00a2\u00a4\u0001\u0000\u0000\u0000\u00a3\u00a5\u0005\n\u0000\u0000"+
		"\u00a4\u00a3\u0001\u0000\u0000\u0000\u00a4\u00a5\u0001\u0000\u0000\u0000"+
		"\u00a5\u00a8\u0001\u0000\u0000\u0000\u00a6\u00a8\u0005\n\u0000\u0000\u00a7"+
		"\u009f\u0001\u0000\u0000\u0000\u00a7\u00a6\u0001\u0000\u0000\u0000\u00a8"+
		"\u001c\u0001\u0000\u0000\u0000\u00a9\u00ab\u0004\r\u0000\u0000\u00aa\u00ac"+
		"\u0007\u0001\u0000\u0000\u00ab\u00aa\u0001\u0000\u0000\u0000\u00ac\u00ad"+
		"\u0001\u0000\u0000\u0000\u00ad\u00ab\u0001\u0000\u0000\u0000\u00ad\u00ae"+
		"\u0001\u0000\u0000\u0000\u00ae\u00af\u0001\u0000\u0000\u0000\u00af\u00b0"+
		"\u0006\r\u0002\u0000\u00b0\u001e\u0001\u0000\u0000\u0000\u00b1\u00b2\u0003"+
		"\u0019\u000b\u0000\u00b2\u00b3\u0001\u0000\u0000\u0000\u00b3\u00b4\u0006"+
		"\u000e\u0002\u0000\u00b4 \u0001\u0000\u0000\u0000\u00b5\u00b9\u0002AZ"+
		"\u0000\u00b6\u00b8\u0007\u0002\u0000\u0000\u00b7\u00b6\u0001\u0000\u0000"+
		"\u0000\u00b8\u00bb\u0001\u0000\u0000\u0000\u00b9\u00b7\u0001\u0000\u0000"+
		"\u0000\u00b9\u00ba\u0001\u0000\u0000\u0000\u00ba\"\u0001\u0000\u0000\u0000"+
		"\u00bb\u00b9\u0001\u0000\u0000\u0000\u00bc\u00bd\u0003!\u000f\u0000\u00bd"+
		"$\u0001\u0000\u0000\u0000\u00be\u00c0\u0005 \u0000\u0000\u00bf\u00be\u0001"+
		"\u0000\u0000\u0000\u00bf\u00c0\u0001\u0000\u0000\u0000\u00c0\u00c1\u0001"+
		"\u0000\u0000\u0000\u00c1\u00c3\u0005|\u0000\u0000\u00c2\u00c4\u0005 \u0000"+
		"\u0000\u00c3\u00c2\u0001\u0000\u0000\u0000\u00c3\u00c4\u0001\u0000\u0000"+
		"\u0000\u00c4&\u0001\u0000\u0000\u0000\u00c5\u00c6\u0003\u0019\u000b\u0000"+
		"\u00c6\u00c7\u0001\u0000\u0000\u0000\u00c7\u00c8\u0006\u0012\u0002\u0000"+
		"\u00c8\u00c9\u0006\u0012\u0003\u0000\u00c9(\u0001\u0000\u0000\u0000\u00ca"+
		"\u00cc\b\u0000\u0000\u0000\u00cb\u00ca\u0001\u0000\u0000\u0000\u00cc\u00cd"+
		"\u0001\u0000\u0000\u0000\u00cd\u00cb\u0001\u0000\u0000\u0000\u00cd\u00ce"+
		"\u0001\u0000\u0000\u0000\u00ce*\u0001\u0000\u0000\u0000\u00cf\u00d0\u0003"+
		"\u0019\u000b\u0000\u00d0\u00d1\u0001\u0000\u0000\u0000\u00d1\u00d2\u0006"+
		"\u0014\u0002\u0000\u00d2\u00d3\u0006\u0014\u0003\u0000\u00d3,\u0001\u0000"+
		"\u0000\u0000\u0013\u0000\u0001\u0002y~\u0084\u0089\u0090\u0096\u0099\u009c"+
		"\u00a1\u00a4\u00a7\u00ad\u00b9\u00bf\u00c3\u00cd\u0004\u0005\u0002\u0000"+
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