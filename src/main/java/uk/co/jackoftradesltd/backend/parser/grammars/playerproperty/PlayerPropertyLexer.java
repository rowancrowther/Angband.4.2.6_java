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

// Generated from PlayerPropertyLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.playerproperty;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class PlayerPropertyLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		TYPE=1, CODE=2, BINDUI=3, NAME=4, DESC=5, VALUE=6, INTEGER=7, TYPE_VALUE=8, 
		LCASE=9, TAG=10, COLON=11, COMMENT=12, INDENT=13, EOL=14, STRING=15, FLAG=16;
	public static final int
		FREE_TEXT_MODE=1, FLAG_MODE=2;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "FREE_TEXT_MODE", "FLAG_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"TYPE", "CODE", "BINDUI", "NAME", "DESC", "VALUE", "INTEGER", "TYPE_VALUE", 
			"LCASE", "TAG", "COLON", "COMMENT", "NL", "NEWLINE", "INDENT", "EOL", 
			"FLAG_BODY", "STRING", "FLAG"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'type:'", "'code:'", "'bindui:'", "'name:'", "'desc:'", "'value:'", 
			null, null, null, null, "':'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "TYPE", "CODE", "BINDUI", "NAME", "DESC", "VALUE", "INTEGER", "TYPE_VALUE", 
			"LCASE", "TAG", "COLON", "COMMENT", "INDENT", "EOL", "STRING", "FLAG"
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
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "PlayerPropertyLexer.g4"; }

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
		case 14:
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
		"\u0004\u0000\u0010\u00c9\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff"+
		"\uffff\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007"+
		"\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007"+
		"\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b"+
		"\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007"+
		"\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f\u0002"+
		"\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0003\u0006X\b\u0006\u0001"+
		"\u0006\u0004\u0006[\b\u0006\u000b\u0006\f\u0006\\\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007"+
		"r\b\u0007\u0001\b\u0004\bu\b\b\u000b\b\f\bv\u0001\t\u0001\t\u0004\t{\b"+
		"\t\u000b\t\f\t|\u0001\t\u0001\t\u0001\n\u0001\n\u0001\u000b\u0001\u000b"+
		"\u0005\u000b\u0085\b\u000b\n\u000b\f\u000b\u0088\t\u000b\u0001\u000b\u0001"+
		"\u000b\u0003\u000b\u008c\b\u000b\u0001\u000b\u0001\u000b\u0001\f\u0005"+
		"\f\u0091\b\f\n\f\f\f\u0094\t\f\u0001\f\u0004\f\u0097\b\f\u000b\f\f\f\u0098"+
		"\u0001\f\u0003\f\u009c\b\f\u0001\f\u0003\f\u009f\b\f\u0001\r\u0004\r\u00a2"+
		"\b\r\u000b\r\f\r\u00a3\u0001\r\u0003\r\u00a7\b\r\u0001\r\u0003\r\u00aa"+
		"\b\r\u0001\u000e\u0001\u000e\u0004\u000e\u00ae\b\u000e\u000b\u000e\f\u000e"+
		"\u00af\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u0010\u0001\u0010\u0005\u0010\u00ba\b\u0010\n\u0010\f\u0010"+
		"\u00bd\t\u0010\u0001\u0011\u0004\u0011\u00c0\b\u0011\u000b\u0011\f\u0011"+
		"\u00c1\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0000\u0000\u0013\u0003\u0001\u0005\u0002\u0007\u0003\t\u0004\u000b"+
		"\u0005\r\u0006\u000f\u0007\u0011\b\u0013\t\u0015\n\u0017\u000b\u0019\f"+
		"\u001b\u0000\u001d\u0000\u001f\r!\u000e#\u0000%\u000f\'\u0010\u0003\u0000"+
		"\u0001\u0002\u0004\u0003\u000009__az\u0002\u0000\n\n\r\r\u0002\u0000\t"+
		"\t  \u0003\u000009AZ__\u00d5\u0000\u0003\u0001\u0000\u0000\u0000\u0000"+
		"\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000"+
		"\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r"+
		"\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011"+
		"\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015"+
		"\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019"+
		"\u0001\u0000\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0000!\u0001"+
		"\u0000\u0000\u0000\u0001%\u0001\u0000\u0000\u0000\u0002\'\u0001\u0000"+
		"\u0000\u0000\u0003)\u0001\u0000\u0000\u0000\u0005/\u0001\u0000\u0000\u0000"+
		"\u00077\u0001\u0000\u0000\u0000\t?\u0001\u0000\u0000\u0000\u000bG\u0001"+
		"\u0000\u0000\u0000\rO\u0001\u0000\u0000\u0000\u000fW\u0001\u0000\u0000"+
		"\u0000\u0011q\u0001\u0000\u0000\u0000\u0013t\u0001\u0000\u0000\u0000\u0015"+
		"x\u0001\u0000\u0000\u0000\u0017\u0080\u0001\u0000\u0000\u0000\u0019\u0082"+
		"\u0001\u0000\u0000\u0000\u001b\u0092\u0001\u0000\u0000\u0000\u001d\u00a9"+
		"\u0001\u0000\u0000\u0000\u001f\u00ab\u0001\u0000\u0000\u0000!\u00b3\u0001"+
		"\u0000\u0000\u0000#\u00b7\u0001\u0000\u0000\u0000%\u00bf\u0001\u0000\u0000"+
		"\u0000\'\u00c5\u0001\u0000\u0000\u0000)*\u0005t\u0000\u0000*+\u0005y\u0000"+
		"\u0000+,\u0005p\u0000\u0000,-\u0005e\u0000\u0000-.\u0005:\u0000\u0000"+
		".\u0004\u0001\u0000\u0000\u0000/0\u0005c\u0000\u000001\u0005o\u0000\u0000"+
		"12\u0005d\u0000\u000023\u0005e\u0000\u000034\u0005:\u0000\u000045\u0001"+
		"\u0000\u0000\u000056\u0006\u0001\u0000\u00006\u0006\u0001\u0000\u0000"+
		"\u000078\u0005b\u0000\u000089\u0005i\u0000\u00009:\u0005n\u0000\u0000"+
		":;\u0005d\u0000\u0000;<\u0005u\u0000\u0000<=\u0005i\u0000\u0000=>\u0005"+
		":\u0000\u0000>\b\u0001\u0000\u0000\u0000?@\u0005n\u0000\u0000@A\u0005"+
		"a\u0000\u0000AB\u0005m\u0000\u0000BC\u0005e\u0000\u0000CD\u0005:\u0000"+
		"\u0000DE\u0001\u0000\u0000\u0000EF\u0006\u0003\u0001\u0000F\n\u0001\u0000"+
		"\u0000\u0000GH\u0005d\u0000\u0000HI\u0005e\u0000\u0000IJ\u0005s\u0000"+
		"\u0000JK\u0005c\u0000\u0000KL\u0005:\u0000\u0000LM\u0001\u0000\u0000\u0000"+
		"MN\u0006\u0004\u0001\u0000N\f\u0001\u0000\u0000\u0000OP\u0005v\u0000\u0000"+
		"PQ\u0005a\u0000\u0000QR\u0005l\u0000\u0000RS\u0005u\u0000\u0000ST\u0005"+
		"e\u0000\u0000TU\u0005:\u0000\u0000U\u000e\u0001\u0000\u0000\u0000VX\u0005"+
		"-\u0000\u0000WV\u0001\u0000\u0000\u0000WX\u0001\u0000\u0000\u0000XZ\u0001"+
		"\u0000\u0000\u0000Y[\u000209\u0000ZY\u0001\u0000\u0000\u0000[\\\u0001"+
		"\u0000\u0000\u0000\\Z\u0001\u0000\u0000\u0000\\]\u0001\u0000\u0000\u0000"+
		"]\u0010\u0001\u0000\u0000\u0000^_\u0005p\u0000\u0000_`\u0005l\u0000\u0000"+
		"`a\u0005a\u0000\u0000ab\u0005y\u0000\u0000bc\u0005e\u0000\u0000cr\u0005"+
		"r\u0000\u0000de\u0005o\u0000\u0000ef\u0005b\u0000\u0000fg\u0005j\u0000"+
		"\u0000gh\u0005e\u0000\u0000hi\u0005c\u0000\u0000ir\u0005t\u0000\u0000"+
		"jk\u0005e\u0000\u0000kl\u0005l\u0000\u0000lm\u0005e\u0000\u0000mn\u0005"+
		"m\u0000\u0000no\u0005e\u0000\u0000op\u0005n\u0000\u0000pr\u0005t\u0000"+
		"\u0000q^\u0001\u0000\u0000\u0000qd\u0001\u0000\u0000\u0000qj\u0001\u0000"+
		"\u0000\u0000r\u0012\u0001\u0000\u0000\u0000su\u0007\u0000\u0000\u0000"+
		"ts\u0001\u0000\u0000\u0000uv\u0001\u0000\u0000\u0000vt\u0001\u0000\u0000"+
		"\u0000vw\u0001\u0000\u0000\u0000w\u0014\u0001\u0000\u0000\u0000xz\u0005"+
		"<\u0000\u0000y{\u0002AZ\u0000zy\u0001\u0000\u0000\u0000{|\u0001\u0000"+
		"\u0000\u0000|z\u0001\u0000\u0000\u0000|}\u0001\u0000\u0000\u0000}~\u0001"+
		"\u0000\u0000\u0000~\u007f\u0005>\u0000\u0000\u007f\u0016\u0001\u0000\u0000"+
		"\u0000\u0080\u0081\u0005:\u0000\u0000\u0081\u0018\u0001\u0000\u0000\u0000"+
		"\u0082\u0086\u0005#\u0000\u0000\u0083\u0085\b\u0001\u0000\u0000\u0084"+
		"\u0083\u0001\u0000\u0000\u0000\u0085\u0088\u0001\u0000\u0000\u0000\u0086"+
		"\u0084\u0001\u0000\u0000\u0000\u0086\u0087\u0001\u0000\u0000\u0000\u0087"+
		"\u008b\u0001\u0000\u0000\u0000\u0088\u0086\u0001\u0000\u0000\u0000\u0089"+
		"\u008c\u0003\u001b\f\u0000\u008a\u008c\u0005\u0000\u0000\u0001\u008b\u0089"+
		"\u0001\u0000\u0000\u0000\u008b\u008a\u0001\u0000\u0000\u0000\u008c\u008d"+
		"\u0001\u0000\u0000\u0000\u008d\u008e\u0006\u000b\u0002\u0000\u008e\u001a"+
		"\u0001\u0000\u0000\u0000\u008f\u0091\u0007\u0002\u0000\u0000\u0090\u008f"+
		"\u0001\u0000\u0000\u0000\u0091\u0094\u0001\u0000\u0000\u0000\u0092\u0090"+
		"\u0001\u0000\u0000\u0000\u0092\u0093\u0001\u0000\u0000\u0000\u0093\u009e"+
		"\u0001\u0000\u0000\u0000\u0094\u0092\u0001\u0000\u0000\u0000\u0095\u0097"+
		"\u0005\r\u0000\u0000\u0096\u0095\u0001\u0000\u0000\u0000\u0097\u0098\u0001"+
		"\u0000\u0000\u0000\u0098\u0096\u0001\u0000\u0000\u0000\u0098\u0099\u0001"+
		"\u0000\u0000\u0000\u0099\u009b\u0001\u0000\u0000\u0000\u009a\u009c\u0005"+
		"\n\u0000\u0000\u009b\u009a\u0001\u0000\u0000\u0000\u009b\u009c\u0001\u0000"+
		"\u0000\u0000\u009c\u009f\u0001\u0000\u0000\u0000\u009d\u009f\u0005\n\u0000"+
		"\u0000\u009e\u0096\u0001\u0000\u0000\u0000\u009e\u009d\u0001\u0000\u0000"+
		"\u0000\u009f\u001c\u0001\u0000\u0000\u0000\u00a0\u00a2\u0005\r\u0000\u0000"+
		"\u00a1\u00a0\u0001\u0000\u0000\u0000\u00a2\u00a3\u0001\u0000\u0000\u0000"+
		"\u00a3\u00a1\u0001\u0000\u0000\u0000\u00a3\u00a4\u0001\u0000\u0000\u0000"+
		"\u00a4\u00a6\u0001\u0000\u0000\u0000\u00a5\u00a7\u0005\n\u0000\u0000\u00a6"+
		"\u00a5\u0001\u0000\u0000\u0000\u00a6\u00a7\u0001\u0000\u0000\u0000\u00a7"+
		"\u00aa\u0001\u0000\u0000\u0000\u00a8\u00aa\u0005\n\u0000\u0000\u00a9\u00a1"+
		"\u0001\u0000\u0000\u0000\u00a9\u00a8\u0001\u0000\u0000\u0000\u00aa\u001e"+
		"\u0001\u0000\u0000\u0000\u00ab\u00ad\u0004\u000e\u0000\u0000\u00ac\u00ae"+
		"\u0007\u0002\u0000\u0000\u00ad\u00ac\u0001\u0000\u0000\u0000\u00ae\u00af"+
		"\u0001\u0000\u0000\u0000\u00af\u00ad\u0001\u0000\u0000\u0000\u00af\u00b0"+
		"\u0001\u0000\u0000\u0000\u00b0\u00b1\u0001\u0000\u0000\u0000\u00b1\u00b2"+
		"\u0006\u000e\u0002\u0000\u00b2 \u0001\u0000\u0000\u0000\u00b3\u00b4\u0003"+
		"\u001b\f\u0000\u00b4\u00b5\u0001\u0000\u0000\u0000\u00b5\u00b6\u0006\u000f"+
		"\u0002\u0000\u00b6\"\u0001\u0000\u0000\u0000\u00b7\u00bb\u0002AZ\u0000"+
		"\u00b8\u00ba\u0007\u0003\u0000\u0000\u00b9\u00b8\u0001\u0000\u0000\u0000"+
		"\u00ba\u00bd\u0001\u0000\u0000\u0000\u00bb\u00b9\u0001\u0000\u0000\u0000"+
		"\u00bb\u00bc\u0001\u0000\u0000\u0000\u00bc$\u0001\u0000\u0000\u0000\u00bd"+
		"\u00bb\u0001\u0000\u0000\u0000\u00be\u00c0\b\u0001\u0000\u0000\u00bf\u00be"+
		"\u0001\u0000\u0000\u0000\u00c0\u00c1\u0001\u0000\u0000\u0000\u00c1\u00bf"+
		"\u0001\u0000\u0000\u0000\u00c1\u00c2\u0001\u0000\u0000\u0000\u00c2\u00c3"+
		"\u0001\u0000\u0000\u0000\u00c3\u00c4\u0006\u0011\u0003\u0000\u00c4&\u0001"+
		"\u0000\u0000\u0000\u00c5\u00c6\u0003#\u0010\u0000\u00c6\u00c7\u0001\u0000"+
		"\u0000\u0000\u00c7\u00c8\u0006\u0012\u0003\u0000\u00c8(\u0001\u0000\u0000"+
		"\u0000\u0014\u0000\u0001\u0002W\\qv|\u0086\u008b\u0092\u0098\u009b\u009e"+
		"\u00a3\u00a6\u00a9\u00af\u00bb\u00c1\u0004\u0005\u0002\u0000\u0005\u0001"+
		"\u0000\u0006\u0000\u0000\u0004\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}