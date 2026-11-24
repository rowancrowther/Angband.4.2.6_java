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
// Generated from QuestLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.quest;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class QuestLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, LEVEL=2, RACE=3, NUMBER=4, INTEGER=5, COMMENT=6, INDENT=7, EOL=8, 
		STRING=9;
	public static final int
		REST_OF_LINE=1;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "REST_OF_LINE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"NAME", "LEVEL", "RACE", "NUMBER", "INTEGER", "COMMENT", "NL", "NEWLINE", 
			"INDENT", "EOL", "STRING"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'level:'", "'race:'", "'number:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "LEVEL", "RACE", "NUMBER", "INTEGER", "COMMENT", "INDENT", 
			"EOL", "STRING"
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


	public QuestLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "QuestLexer.g4"; }

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
		case 8:
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
		"\u0004\u0000\t\u0089\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000\u0007"+
		"\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007"+
		"\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007"+
		"\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n"+
		"\u0007\n\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0004\u0003\u00049\b\u0004\u0001\u0004\u0001"+
		"\u0004\u0003\u0004=\b\u0004\u0003\u0004?\b\u0004\u0001\u0004\u0004\u0004"+
		"B\b\u0004\u000b\u0004\f\u0004C\u0001\u0005\u0001\u0005\u0005\u0005H\b"+
		"\u0005\n\u0005\f\u0005K\t\u0005\u0001\u0005\u0001\u0005\u0003\u0005O\b"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0005\u0006T\b\u0006\n\u0006"+
		"\f\u0006W\t\u0006\u0001\u0006\u0004\u0006Z\b\u0006\u000b\u0006\f\u0006"+
		"[\u0001\u0006\u0003\u0006_\b\u0006\u0001\u0006\u0003\u0006b\b\u0006\u0001"+
		"\u0006\u0004\u0006e\b\u0006\u000b\u0006\f\u0006f\u0001\u0006\u0003\u0006"+
		"j\b\u0006\u0001\u0007\u0004\u0007m\b\u0007\u000b\u0007\f\u0007n\u0001"+
		"\u0007\u0003\u0007r\b\u0007\u0001\u0007\u0003\u0007u\b\u0007\u0001\b\u0001"+
		"\b\u0004\by\b\b\u000b\b\f\bz\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\n\u0004\n\u0084\b\n\u000b\n\f\n\u0085\u0001\n\u0001\n\u0000\u0000"+
		"\u000b\u0002\u0001\u0004\u0002\u0006\u0003\b\u0004\n\u0005\f\u0006\u000e"+
		"\u0000\u0010\u0000\u0012\u0007\u0014\b\u0016\t\u0002\u0000\u0001\u0005"+
		"\u0002\u0000++--\u0002\u0000XXxx\u0003\u000009AFaf\u0002\u0000\n\n\r\r"+
		"\u0002\u0000\t\t  \u0096\u0000\u0002\u0001\u0000\u0000\u0000\u0000\u0004"+
		"\u0001\u0000\u0000\u0000\u0000\u0006\u0001\u0000\u0000\u0000\u0000\b\u0001"+
		"\u0000\u0000\u0000\u0000\n\u0001\u0000\u0000\u0000\u0000\f\u0001\u0000"+
		"\u0000\u0000\u0000\u0012\u0001\u0000\u0000\u0000\u0000\u0014\u0001\u0000"+
		"\u0000\u0000\u0001\u0016\u0001\u0000\u0000\u0000\u0002\u0018\u0001\u0000"+
		"\u0000\u0000\u0004 \u0001\u0000\u0000\u0000\u0006\'\u0001\u0000\u0000"+
		"\u0000\b/\u0001\u0000\u0000\u0000\n8\u0001\u0000\u0000\u0000\fE\u0001"+
		"\u0000\u0000\u0000\u000ei\u0001\u0000\u0000\u0000\u0010t\u0001\u0000\u0000"+
		"\u0000\u0012v\u0001\u0000\u0000\u0000\u0014~\u0001\u0000\u0000\u0000\u0016"+
		"\u0083\u0001\u0000\u0000\u0000\u0018\u0019\u0005n\u0000\u0000\u0019\u001a"+
		"\u0005a\u0000\u0000\u001a\u001b\u0005m\u0000\u0000\u001b\u001c\u0005e"+
		"\u0000\u0000\u001c\u001d\u0005:\u0000\u0000\u001d\u001e\u0001\u0000\u0000"+
		"\u0000\u001e\u001f\u0006\u0000\u0000\u0000\u001f\u0003\u0001\u0000\u0000"+
		"\u0000 !\u0005l\u0000\u0000!\"\u0005e\u0000\u0000\"#\u0005v\u0000\u0000"+
		"#$\u0005e\u0000\u0000$%\u0005l\u0000\u0000%&\u0005:\u0000\u0000&\u0005"+
		"\u0001\u0000\u0000\u0000\'(\u0005r\u0000\u0000()\u0005a\u0000\u0000)*"+
		"\u0005c\u0000\u0000*+\u0005e\u0000\u0000+,\u0005:\u0000\u0000,-\u0001"+
		"\u0000\u0000\u0000-.\u0006\u0002\u0000\u0000.\u0007\u0001\u0000\u0000"+
		"\u0000/0\u0005n\u0000\u000001\u0005u\u0000\u000012\u0005m\u0000\u0000"+
		"23\u0005b\u0000\u000034\u0005e\u0000\u000045\u0005r\u0000\u000056\u0005"+
		":\u0000\u00006\t\u0001\u0000\u0000\u000079\u0007\u0000\u0000\u000087\u0001"+
		"\u0000\u0000\u000089\u0001\u0000\u0000\u00009>\u0001\u0000\u0000\u0000"+
		":<\u00050\u0000\u0000;=\u0007\u0001\u0000\u0000<;\u0001\u0000\u0000\u0000"+
		"<=\u0001\u0000\u0000\u0000=?\u0001\u0000\u0000\u0000>:\u0001\u0000\u0000"+
		"\u0000>?\u0001\u0000\u0000\u0000?A\u0001\u0000\u0000\u0000@B\u0007\u0002"+
		"\u0000\u0000A@\u0001\u0000\u0000\u0000BC\u0001\u0000\u0000\u0000CA\u0001"+
		"\u0000\u0000\u0000CD\u0001\u0000\u0000\u0000D\u000b\u0001\u0000\u0000"+
		"\u0000EI\u0005#\u0000\u0000FH\b\u0003\u0000\u0000GF\u0001\u0000\u0000"+
		"\u0000HK\u0001\u0000\u0000\u0000IG\u0001\u0000\u0000\u0000IJ\u0001\u0000"+
		"\u0000\u0000JN\u0001\u0000\u0000\u0000KI\u0001\u0000\u0000\u0000LO\u0003"+
		"\u000e\u0006\u0000MO\u0005\u0000\u0000\u0001NL\u0001\u0000\u0000\u0000"+
		"NM\u0001\u0000\u0000\u0000OP\u0001\u0000\u0000\u0000PQ\u0006\u0005\u0001"+
		"\u0000Q\r\u0001\u0000\u0000\u0000RT\u0007\u0004\u0000\u0000SR\u0001\u0000"+
		"\u0000\u0000TW\u0001\u0000\u0000\u0000US\u0001\u0000\u0000\u0000UV\u0001"+
		"\u0000\u0000\u0000Va\u0001\u0000\u0000\u0000WU\u0001\u0000\u0000\u0000"+
		"XZ\u0005\r\u0000\u0000YX\u0001\u0000\u0000\u0000Z[\u0001\u0000\u0000\u0000"+
		"[Y\u0001\u0000\u0000\u0000[\\\u0001\u0000\u0000\u0000\\^\u0001\u0000\u0000"+
		"\u0000]_\u0005\n\u0000\u0000^]\u0001\u0000\u0000\u0000^_\u0001\u0000\u0000"+
		"\u0000_b\u0001\u0000\u0000\u0000`b\u0005\n\u0000\u0000aY\u0001\u0000\u0000"+
		"\u0000a`\u0001\u0000\u0000\u0000bj\u0001\u0000\u0000\u0000ce\u0007\u0004"+
		"\u0000\u0000dc\u0001\u0000\u0000\u0000ef\u0001\u0000\u0000\u0000fd\u0001"+
		"\u0000\u0000\u0000fg\u0001\u0000\u0000\u0000gh\u0001\u0000\u0000\u0000"+
		"hj\u0005\u0000\u0000\u0001iU\u0001\u0000\u0000\u0000id\u0001\u0000\u0000"+
		"\u0000j\u000f\u0001\u0000\u0000\u0000km\u0005\r\u0000\u0000lk\u0001\u0000"+
		"\u0000\u0000mn\u0001\u0000\u0000\u0000nl\u0001\u0000\u0000\u0000no\u0001"+
		"\u0000\u0000\u0000oq\u0001\u0000\u0000\u0000pr\u0005\n\u0000\u0000qp\u0001"+
		"\u0000\u0000\u0000qr\u0001\u0000\u0000\u0000ru\u0001\u0000\u0000\u0000"+
		"su\u0005\n\u0000\u0000tl\u0001\u0000\u0000\u0000ts\u0001\u0000\u0000\u0000"+
		"u\u0011\u0001\u0000\u0000\u0000vx\u0004\b\u0000\u0000wy\u0007\u0004\u0000"+
		"\u0000xw\u0001\u0000\u0000\u0000yz\u0001\u0000\u0000\u0000zx\u0001\u0000"+
		"\u0000\u0000z{\u0001\u0000\u0000\u0000{|\u0001\u0000\u0000\u0000|}\u0006"+
		"\b\u0001\u0000}\u0013\u0001\u0000\u0000\u0000~\u007f\u0003\u000e\u0006"+
		"\u0000\u007f\u0080\u0001\u0000\u0000\u0000\u0080\u0081\u0006\t\u0001\u0000"+
		"\u0081\u0015\u0001\u0000\u0000\u0000\u0082\u0084\b\u0003\u0000\u0000\u0083"+
		"\u0082\u0001\u0000\u0000\u0000\u0084\u0085\u0001\u0000\u0000\u0000\u0085"+
		"\u0083\u0001\u0000\u0000\u0000\u0085\u0086\u0001\u0000\u0000\u0000\u0086"+
		"\u0087\u0001\u0000\u0000\u0000\u0087\u0088\u0006\n\u0002\u0000\u0088\u0017"+
		"\u0001\u0000\u0000\u0000\u0013\u0000\u00018<>CINU[^afinqtz\u0085\u0003"+
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