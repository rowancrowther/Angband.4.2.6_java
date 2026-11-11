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

// Generated from DungeonProfileLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.dungeonprofile;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class DungeonProfileLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, PARAMS=2, TUNNEL=3, STREAMER=4, ROOM=5, MIN_LEVEL=6, ALLOC=7, 
		INTEGER=8, COLON=9, COMMENT=10, INDENT=11, EOL=12, TEXT_BETWEEN_COLON=13, 
		DELIMITER=14, STRING=15, END_OF_MODE=16;
	public static final int
		DELIMITED_TEXT=1, REST_OF_LINE=2;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "DELIMITED_TEXT", "REST_OF_LINE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"NAME", "PARAMS", "TUNNEL", "STREAMER", "ROOM", "MIN_LEVEL", "ALLOC", 
			"INTEGER", "COLON", "COMMENT", "NL", "NEWLINE", "INDENT", "EOL", "TEXT_BETWEEN_COLON", 
			"DELIMITER", "STRING", "END_OF_MODE"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'params:'", "'tunnel:'", "'streamer:'", "'room:'", 
			"'min-level:'", "'alloc:'", null, "':'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "PARAMS", "TUNNEL", "STREAMER", "ROOM", "MIN_LEVEL", "ALLOC", 
			"INTEGER", "COLON", "COMMENT", "INDENT", "EOL", "TEXT_BETWEEN_COLON", 
			"DELIMITER", "STRING", "END_OF_MODE"
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


	public DungeonProfileLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "DungeonProfileLexer.g4"; }

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
		"\u0004\u0000\u0010\u00b8\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff"+
		"\uffff\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007"+
		"\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007"+
		"\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b"+
		"\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007"+
		"\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f\u0002"+
		"\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0003\u0007e\b"+
		"\u0007\u0001\u0007\u0004\u0007h\b\u0007\u000b\u0007\f\u0007i\u0001\b\u0001"+
		"\b\u0001\t\u0001\t\u0005\tp\b\t\n\t\f\ts\t\t\u0001\t\u0001\t\u0003\tw"+
		"\b\t\u0001\t\u0001\t\u0001\n\u0005\n|\b\n\n\n\f\n\u007f\t\n\u0001\n\u0004"+
		"\n\u0082\b\n\u000b\n\f\n\u0083\u0001\n\u0003\n\u0087\b\n\u0001\n\u0003"+
		"\n\u008a\b\n\u0001\u000b\u0004\u000b\u008d\b\u000b\u000b\u000b\f\u000b"+
		"\u008e\u0001\u000b\u0003\u000b\u0092\b\u000b\u0001\u000b\u0003\u000b\u0095"+
		"\b\u000b\u0001\f\u0001\f\u0004\f\u0099\b\f\u000b\f\f\f\u009a\u0001\f\u0001"+
		"\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0004\u000e\u00a4\b\u000e"+
		"\u000b\u000e\f\u000e\u00a5\u0001\u000f\u0001\u000f\u0003\u000f\u00aa\b"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0004\u0010\u00b0"+
		"\b\u0010\u000b\u0010\f\u0010\u00b1\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0000\u0000\u0012\u0003\u0001\u0005\u0002\u0007"+
		"\u0003\t\u0004\u000b\u0005\r\u0006\u000f\u0007\u0011\b\u0013\t\u0015\n"+
		"\u0017\u0000\u0019\u0000\u001b\u000b\u001d\f\u001f\r!\u000e#\u000f%\u0010"+
		"\u0003\u0000\u0001\u0002\u0003\u0002\u0000\n\n\r\r\u0002\u0000\t\t  \u0003"+
		"\u0000\n\n\r\r::\u00c2\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005"+
		"\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001"+
		"\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000"+
		"\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000"+
		"\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000"+
		"\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d\u0001\u0000"+
		"\u0000\u0000\u0001\u001f\u0001\u0000\u0000\u0000\u0001!\u0001\u0000\u0000"+
		"\u0000\u0002#\u0001\u0000\u0000\u0000\u0002%\u0001\u0000\u0000\u0000\u0003"+
		"\'\u0001\u0000\u0000\u0000\u0005/\u0001\u0000\u0000\u0000\u00077\u0001"+
		"\u0000\u0000\u0000\t?\u0001\u0000\u0000\u0000\u000bI\u0001\u0000\u0000"+
		"\u0000\rQ\u0001\u0000\u0000\u0000\u000f\\\u0001\u0000\u0000\u0000\u0011"+
		"d\u0001\u0000\u0000\u0000\u0013k\u0001\u0000\u0000\u0000\u0015m\u0001"+
		"\u0000\u0000\u0000\u0017}\u0001\u0000\u0000\u0000\u0019\u0094\u0001\u0000"+
		"\u0000\u0000\u001b\u0096\u0001\u0000\u0000\u0000\u001d\u009e\u0001\u0000"+
		"\u0000\u0000\u001f\u00a3\u0001\u0000\u0000\u0000!\u00a9\u0001\u0000\u0000"+
		"\u0000#\u00af\u0001\u0000\u0000\u0000%\u00b3\u0001\u0000\u0000\u0000\'"+
		"(\u0005n\u0000\u0000()\u0005a\u0000\u0000)*\u0005m\u0000\u0000*+\u0005"+
		"e\u0000\u0000+,\u0005:\u0000\u0000,-\u0001\u0000\u0000\u0000-.\u0006\u0000"+
		"\u0000\u0000.\u0004\u0001\u0000\u0000\u0000/0\u0005p\u0000\u000001\u0005"+
		"a\u0000\u000012\u0005r\u0000\u000023\u0005a\u0000\u000034\u0005m\u0000"+
		"\u000045\u0005s\u0000\u000056\u0005:\u0000\u00006\u0006\u0001\u0000\u0000"+
		"\u000078\u0005t\u0000\u000089\u0005u\u0000\u00009:\u0005n\u0000\u0000"+
		":;\u0005n\u0000\u0000;<\u0005e\u0000\u0000<=\u0005l\u0000\u0000=>\u0005"+
		":\u0000\u0000>\b\u0001\u0000\u0000\u0000?@\u0005s\u0000\u0000@A\u0005"+
		"t\u0000\u0000AB\u0005r\u0000\u0000BC\u0005e\u0000\u0000CD\u0005a\u0000"+
		"\u0000DE\u0005m\u0000\u0000EF\u0005e\u0000\u0000FG\u0005r\u0000\u0000"+
		"GH\u0005:\u0000\u0000H\n\u0001\u0000\u0000\u0000IJ\u0005r\u0000\u0000"+
		"JK\u0005o\u0000\u0000KL\u0005o\u0000\u0000LM\u0005m\u0000\u0000MN\u0005"+
		":\u0000\u0000NO\u0001\u0000\u0000\u0000OP\u0006\u0004\u0001\u0000P\f\u0001"+
		"\u0000\u0000\u0000QR\u0005m\u0000\u0000RS\u0005i\u0000\u0000ST\u0005n"+
		"\u0000\u0000TU\u0005-\u0000\u0000UV\u0005l\u0000\u0000VW\u0005e\u0000"+
		"\u0000WX\u0005v\u0000\u0000XY\u0005e\u0000\u0000YZ\u0005l\u0000\u0000"+
		"Z[\u0005:\u0000\u0000[\u000e\u0001\u0000\u0000\u0000\\]\u0005a\u0000\u0000"+
		"]^\u0005l\u0000\u0000^_\u0005l\u0000\u0000_`\u0005o\u0000\u0000`a\u0005"+
		"c\u0000\u0000ab\u0005:\u0000\u0000b\u0010\u0001\u0000\u0000\u0000ce\u0005"+
		"-\u0000\u0000dc\u0001\u0000\u0000\u0000de\u0001\u0000\u0000\u0000eg\u0001"+
		"\u0000\u0000\u0000fh\u000209\u0000gf\u0001\u0000\u0000\u0000hi\u0001\u0000"+
		"\u0000\u0000ig\u0001\u0000\u0000\u0000ij\u0001\u0000\u0000\u0000j\u0012"+
		"\u0001\u0000\u0000\u0000kl\u0005:\u0000\u0000l\u0014\u0001\u0000\u0000"+
		"\u0000mq\u0005#\u0000\u0000np\b\u0000\u0000\u0000on\u0001\u0000\u0000"+
		"\u0000ps\u0001\u0000\u0000\u0000qo\u0001\u0000\u0000\u0000qr\u0001\u0000"+
		"\u0000\u0000rv\u0001\u0000\u0000\u0000sq\u0001\u0000\u0000\u0000tw\u0003"+
		"\u0017\n\u0000uw\u0005\u0000\u0000\u0001vt\u0001\u0000\u0000\u0000vu\u0001"+
		"\u0000\u0000\u0000wx\u0001\u0000\u0000\u0000xy\u0006\t\u0002\u0000y\u0016"+
		"\u0001\u0000\u0000\u0000z|\u0007\u0001\u0000\u0000{z\u0001\u0000\u0000"+
		"\u0000|\u007f\u0001\u0000\u0000\u0000}{\u0001\u0000\u0000\u0000}~\u0001"+
		"\u0000\u0000\u0000~\u0089\u0001\u0000\u0000\u0000\u007f}\u0001\u0000\u0000"+
		"\u0000\u0080\u0082\u0005\r\u0000\u0000\u0081\u0080\u0001\u0000\u0000\u0000"+
		"\u0082\u0083\u0001\u0000\u0000\u0000\u0083\u0081\u0001\u0000\u0000\u0000"+
		"\u0083\u0084\u0001\u0000\u0000\u0000\u0084\u0086\u0001\u0000\u0000\u0000"+
		"\u0085\u0087\u0005\n\u0000\u0000\u0086\u0085\u0001\u0000\u0000\u0000\u0086"+
		"\u0087\u0001\u0000\u0000\u0000\u0087\u008a\u0001\u0000\u0000\u0000\u0088"+
		"\u008a\u0005\n\u0000\u0000\u0089\u0081\u0001\u0000\u0000\u0000\u0089\u0088"+
		"\u0001\u0000\u0000\u0000\u008a\u0018\u0001\u0000\u0000\u0000\u008b\u008d"+
		"\u0005\r\u0000\u0000\u008c\u008b\u0001\u0000\u0000\u0000\u008d\u008e\u0001"+
		"\u0000\u0000\u0000\u008e\u008c\u0001\u0000\u0000\u0000\u008e\u008f\u0001"+
		"\u0000\u0000\u0000\u008f\u0091\u0001\u0000\u0000\u0000\u0090\u0092\u0005"+
		"\n\u0000\u0000\u0091\u0090\u0001\u0000\u0000\u0000\u0091\u0092\u0001\u0000"+
		"\u0000\u0000\u0092\u0095\u0001\u0000\u0000\u0000\u0093\u0095\u0005\n\u0000"+
		"\u0000\u0094\u008c\u0001\u0000\u0000\u0000\u0094\u0093\u0001\u0000\u0000"+
		"\u0000\u0095\u001a\u0001\u0000\u0000\u0000\u0096\u0098\u0004\f\u0000\u0000"+
		"\u0097\u0099\u0007\u0001\u0000\u0000\u0098\u0097\u0001\u0000\u0000\u0000"+
		"\u0099\u009a\u0001\u0000\u0000\u0000\u009a\u0098\u0001\u0000\u0000\u0000"+
		"\u009a\u009b\u0001\u0000\u0000\u0000\u009b\u009c\u0001\u0000\u0000\u0000"+
		"\u009c\u009d\u0006\f\u0002\u0000\u009d\u001c\u0001\u0000\u0000\u0000\u009e"+
		"\u009f\u0003\u0017\n\u0000\u009f\u00a0\u0001\u0000\u0000\u0000\u00a0\u00a1"+
		"\u0006\r\u0002\u0000\u00a1\u001e\u0001\u0000\u0000\u0000\u00a2\u00a4\b"+
		"\u0002\u0000\u0000\u00a3\u00a2\u0001\u0000\u0000\u0000\u00a4\u00a5\u0001"+
		"\u0000\u0000\u0000\u00a5\u00a3\u0001\u0000\u0000\u0000\u00a5\u00a6\u0001"+
		"\u0000\u0000\u0000\u00a6 \u0001\u0000\u0000\u0000\u00a7\u00aa\u0005:\u0000"+
		"\u0000\u00a8\u00aa\u0003\u0017\n\u0000\u00a9\u00a7\u0001\u0000\u0000\u0000"+
		"\u00a9\u00a8\u0001\u0000\u0000\u0000\u00aa\u00ab\u0001\u0000\u0000\u0000"+
		"\u00ab\u00ac\u0006\u000f\u0002\u0000\u00ac\u00ad\u0006\u000f\u0003\u0000"+
		"\u00ad\"\u0001\u0000\u0000\u0000\u00ae\u00b0\b\u0000\u0000\u0000\u00af"+
		"\u00ae\u0001\u0000\u0000\u0000\u00b0\u00b1\u0001\u0000\u0000\u0000\u00b1"+
		"\u00af\u0001\u0000\u0000\u0000\u00b1\u00b2\u0001\u0000\u0000\u0000\u00b2"+
		"$\u0001\u0000\u0000\u0000\u00b3\u00b4\u0003\u0017\n\u0000\u00b4\u00b5"+
		"\u0001\u0000\u0000\u0000\u00b5\u00b6\u0006\u0011\u0002\u0000\u00b6\u00b7"+
		"\u0006\u0011\u0003\u0000\u00b7&\u0001\u0000\u0000\u0000\u0012\u0000\u0001"+
		"\u0002diqv}\u0083\u0086\u0089\u008e\u0091\u0094\u009a\u00a5\u00a9\u00b1"+
		"\u0004\u0005\u0002\u0000\u0005\u0001\u0000\u0006\u0000\u0000\u0004\u0000"+
		"\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}