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

// Generated from UIEntryBaseLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.frontend.ui.entrybase.antlr4.uientrybase;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class UIEntryBaseLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, RENDERER=2, COMBINE=3, CATEGORY=4, FLAGS=5, DESC=6, INTEGER=7, 
		LCASE=8, COMMENT=9, INDENT=10, EOL=11, FLAG=12, OR=13, FLAG_EOL=14, STRING=15;
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
			"NAME", "RENDERER", "COMBINE", "CATEGORY", "FLAGS", "DESC", "INTEGER", 
			"LCASE", "COMMENT", "NL", "INDENT", "EOL", "FLAG_BODY", "FLAG", "OR", 
			"FLAG_EOL", "STRING"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'renderer:'", "'combine:'", "'category:'", "'flags:'", 
			"'desc:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "RENDERER", "COMBINE", "CATEGORY", "FLAGS", "DESC", "INTEGER", 
			"LCASE", "COMMENT", "INDENT", "EOL", "FLAG", "OR", "FLAG_EOL", "STRING"
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


	public UIEntryBaseLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "UIEntryBaseLexer.g4"; }

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
		case 10:
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
		"\u0004\u0000\u000f\u00b0\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff"+
		"\uffff\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007"+
		"\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007"+
		"\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b"+
		"\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007"+
		"\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f\u0002"+
		"\u0010\u0007\u0010\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0003\u0006_\b"+
		"\u0006\u0001\u0006\u0004\u0006b\b\u0006\u000b\u0006\f\u0006c\u0001\u0007"+
		"\u0004\u0007g\b\u0007\u000b\u0007\f\u0007h\u0001\b\u0001\b\u0005\bm\b"+
		"\b\n\b\f\bp\t\b\u0001\b\u0001\b\u0003\bt\b\b\u0001\b\u0001\b\u0001\t\u0005"+
		"\ty\b\t\n\t\f\t|\t\t\u0001\t\u0004\t\u007f\b\t\u000b\t\f\t\u0080\u0001"+
		"\t\u0003\t\u0084\b\t\u0001\t\u0003\t\u0087\b\t\u0001\n\u0001\n\u0004\n"+
		"\u008b\b\n\u000b\n\f\n\u008c\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\f\u0001\f\u0005\f\u0097\b\f\n\f\f\f\u009a\t\f"+
		"\u0001\r\u0001\r\u0001\u000e\u0003\u000e\u009f\b\u000e\u0001\u000e\u0001"+
		"\u000e\u0003\u000e\u00a3\b\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u0010\u0004\u0010\u00ab\b\u0010\u000b\u0010\f"+
		"\u0010\u00ac\u0001\u0010\u0001\u0010\u0000\u0000\u0011\u0003\u0001\u0005"+
		"\u0002\u0007\u0003\t\u0004\u000b\u0005\r\u0006\u000f\u0007\u0011\b\u0013"+
		"\t\u0015\u0000\u0017\n\u0019\u000b\u001b\u0000\u001d\f\u001f\r!\u000e"+
		"#\u000f\u0003\u0000\u0001\u0002\u0004\u0003\u000009__az\u0002\u0000\n"+
		"\n\r\r\u0002\u0000\t\t  \u0003\u000009AZ__\u00b9\u0000\u0003\u0001\u0000"+
		"\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000"+
		"\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000"+
		"\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000"+
		"\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000"+
		"\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000"+
		"\u0001\u001d\u0001\u0000\u0000\u0000\u0001\u001f\u0001\u0000\u0000\u0000"+
		"\u0001!\u0001\u0000\u0000\u0000\u0002#\u0001\u0000\u0000\u0000\u0003%"+
		"\u0001\u0000\u0000\u0000\u0005+\u0001\u0000\u0000\u0000\u00075\u0001\u0000"+
		"\u0000\u0000\t@\u0001\u0000\u0000\u0000\u000bL\u0001\u0000\u0000\u0000"+
		"\rU\u0001\u0000\u0000\u0000\u000f^\u0001\u0000\u0000\u0000\u0011f\u0001"+
		"\u0000\u0000\u0000\u0013j\u0001\u0000\u0000\u0000\u0015z\u0001\u0000\u0000"+
		"\u0000\u0017\u0088\u0001\u0000\u0000\u0000\u0019\u0090\u0001\u0000\u0000"+
		"\u0000\u001b\u0094\u0001\u0000\u0000\u0000\u001d\u009b\u0001\u0000\u0000"+
		"\u0000\u001f\u009e\u0001\u0000\u0000\u0000!\u00a4\u0001\u0000\u0000\u0000"+
		"#\u00aa\u0001\u0000\u0000\u0000%&\u0005n\u0000\u0000&\'\u0005a\u0000\u0000"+
		"\'(\u0005m\u0000\u0000()\u0005e\u0000\u0000)*\u0005:\u0000\u0000*\u0004"+
		"\u0001\u0000\u0000\u0000+,\u0005r\u0000\u0000,-\u0005e\u0000\u0000-.\u0005"+
		"n\u0000\u0000./\u0005d\u0000\u0000/0\u0005e\u0000\u000001\u0005r\u0000"+
		"\u000012\u0005e\u0000\u000023\u0005r\u0000\u000034\u0005:\u0000\u0000"+
		"4\u0006\u0001\u0000\u0000\u000056\u0005c\u0000\u000067\u0005o\u0000\u0000"+
		"78\u0005m\u0000\u000089\u0005b\u0000\u00009:\u0005i\u0000\u0000:;\u0005"+
		"n\u0000\u0000;<\u0005e\u0000\u0000<=\u0005:\u0000\u0000=>\u0001\u0000"+
		"\u0000\u0000>?\u0006\u0002\u0000\u0000?\b\u0001\u0000\u0000\u0000@A\u0005"+
		"c\u0000\u0000AB\u0005a\u0000\u0000BC\u0005t\u0000\u0000CD\u0005e\u0000"+
		"\u0000DE\u0005g\u0000\u0000EF\u0005o\u0000\u0000FG\u0005r\u0000\u0000"+
		"GH\u0005y\u0000\u0000HI\u0005:\u0000\u0000IJ\u0001\u0000\u0000\u0000J"+
		"K\u0006\u0003\u0001\u0000K\n\u0001\u0000\u0000\u0000LM\u0005f\u0000\u0000"+
		"MN\u0005l\u0000\u0000NO\u0005a\u0000\u0000OP\u0005g\u0000\u0000PQ\u0005"+
		"s\u0000\u0000QR\u0005:\u0000\u0000RS\u0001\u0000\u0000\u0000ST\u0006\u0004"+
		"\u0000\u0000T\f\u0001\u0000\u0000\u0000UV\u0005d\u0000\u0000VW\u0005e"+
		"\u0000\u0000WX\u0005s\u0000\u0000XY\u0005c\u0000\u0000YZ\u0005:\u0000"+
		"\u0000Z[\u0001\u0000\u0000\u0000[\\\u0006\u0005\u0001\u0000\\\u000e\u0001"+
		"\u0000\u0000\u0000]_\u0005-\u0000\u0000^]\u0001\u0000\u0000\u0000^_\u0001"+
		"\u0000\u0000\u0000_a\u0001\u0000\u0000\u0000`b\u000209\u0000a`\u0001\u0000"+
		"\u0000\u0000bc\u0001\u0000\u0000\u0000ca\u0001\u0000\u0000\u0000cd\u0001"+
		"\u0000\u0000\u0000d\u0010\u0001\u0000\u0000\u0000eg\u0007\u0000\u0000"+
		"\u0000fe\u0001\u0000\u0000\u0000gh\u0001\u0000\u0000\u0000hf\u0001\u0000"+
		"\u0000\u0000hi\u0001\u0000\u0000\u0000i\u0012\u0001\u0000\u0000\u0000"+
		"jn\u0005#\u0000\u0000km\b\u0001\u0000\u0000lk\u0001\u0000\u0000\u0000"+
		"mp\u0001\u0000\u0000\u0000nl\u0001\u0000\u0000\u0000no\u0001\u0000\u0000"+
		"\u0000os\u0001\u0000\u0000\u0000pn\u0001\u0000\u0000\u0000qt\u0003\u0015"+
		"\t\u0000rt\u0005\u0000\u0000\u0001sq\u0001\u0000\u0000\u0000sr\u0001\u0000"+
		"\u0000\u0000tu\u0001\u0000\u0000\u0000uv\u0006\b\u0002\u0000v\u0014\u0001"+
		"\u0000\u0000\u0000wy\u0007\u0002\u0000\u0000xw\u0001\u0000\u0000\u0000"+
		"y|\u0001\u0000\u0000\u0000zx\u0001\u0000\u0000\u0000z{\u0001\u0000\u0000"+
		"\u0000{\u0086\u0001\u0000\u0000\u0000|z\u0001\u0000\u0000\u0000}\u007f"+
		"\u0005\r\u0000\u0000~}\u0001\u0000\u0000\u0000\u007f\u0080\u0001\u0000"+
		"\u0000\u0000\u0080~\u0001\u0000\u0000\u0000\u0080\u0081\u0001\u0000\u0000"+
		"\u0000\u0081\u0083\u0001\u0000\u0000\u0000\u0082\u0084\u0005\n\u0000\u0000"+
		"\u0083\u0082\u0001\u0000\u0000\u0000\u0083\u0084\u0001\u0000\u0000\u0000"+
		"\u0084\u0087\u0001\u0000\u0000\u0000\u0085\u0087\u0005\n\u0000\u0000\u0086"+
		"~\u0001\u0000\u0000\u0000\u0086\u0085\u0001\u0000\u0000\u0000\u0087\u0016"+
		"\u0001\u0000\u0000\u0000\u0088\u008a\u0004\n\u0000\u0000\u0089\u008b\u0007"+
		"\u0002\u0000\u0000\u008a\u0089\u0001\u0000\u0000\u0000\u008b\u008c\u0001"+
		"\u0000\u0000\u0000\u008c\u008a\u0001\u0000\u0000\u0000\u008c\u008d\u0001"+
		"\u0000\u0000\u0000\u008d\u008e\u0001\u0000\u0000\u0000\u008e\u008f\u0006"+
		"\n\u0002\u0000\u008f\u0018\u0001\u0000\u0000\u0000\u0090\u0091\u0003\u0015"+
		"\t\u0000\u0091\u0092\u0001\u0000\u0000\u0000\u0092\u0093\u0006\u000b\u0002"+
		"\u0000\u0093\u001a\u0001\u0000\u0000\u0000\u0094\u0098\u0002AZ\u0000\u0095"+
		"\u0097\u0007\u0003\u0000\u0000\u0096\u0095\u0001\u0000\u0000\u0000\u0097"+
		"\u009a\u0001\u0000\u0000\u0000\u0098\u0096\u0001\u0000\u0000\u0000\u0098"+
		"\u0099\u0001\u0000\u0000\u0000\u0099\u001c\u0001\u0000\u0000\u0000\u009a"+
		"\u0098\u0001\u0000\u0000\u0000\u009b\u009c\u0003\u001b\f\u0000\u009c\u001e"+
		"\u0001\u0000\u0000\u0000\u009d\u009f\u0005 \u0000\u0000\u009e\u009d\u0001"+
		"\u0000\u0000\u0000\u009e\u009f\u0001\u0000\u0000\u0000\u009f\u00a0\u0001"+
		"\u0000\u0000\u0000\u00a0\u00a2\u0005|\u0000\u0000\u00a1\u00a3\u0005 \u0000"+
		"\u0000\u00a2\u00a1\u0001\u0000\u0000\u0000\u00a2\u00a3\u0001\u0000\u0000"+
		"\u0000\u00a3 \u0001\u0000\u0000\u0000\u00a4\u00a5\u0003\u0015\t\u0000"+
		"\u00a5\u00a6\u0001\u0000\u0000\u0000\u00a6\u00a7\u0006\u000f\u0002\u0000"+
		"\u00a7\u00a8\u0006\u000f\u0003\u0000\u00a8\"\u0001\u0000\u0000\u0000\u00a9"+
		"\u00ab\b\u0001\u0000\u0000\u00aa\u00a9\u0001\u0000\u0000\u0000\u00ab\u00ac"+
		"\u0001\u0000\u0000\u0000\u00ac\u00aa\u0001\u0000\u0000\u0000\u00ac\u00ad"+
		"\u0001\u0000\u0000\u0000\u00ad\u00ae\u0001\u0000\u0000\u0000\u00ae\u00af"+
		"\u0006\u0010\u0003\u0000\u00af$\u0001\u0000\u0000\u0000\u0011\u0000\u0001"+
		"\u0002^chnsz\u0080\u0083\u0086\u008c\u0098\u009e\u00a2\u00ac\u0004\u0005"+
		"\u0001\u0000\u0005\u0002\u0000\u0006\u0000\u0000\u0004\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}