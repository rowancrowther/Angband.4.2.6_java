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

// Generated from /home/rowan/IdeaProjects/Angband.4.2.6_java/src/main/java/uk/co/jackoftradesltd/backend/parser/grammars/RealmLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.realm;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class RealmLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, STAT=2, VERB=3, SPELL_NOUN=4, BOOK_NOUN=5, INTEGER=6, COMMENT=7, 
		EOL=8, STRING=9, FREE_TEXT_EOL=10;
	public static final int
		FREE_TEXT=1;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "FREE_TEXT"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"NAME", "STAT", "VERB", "SPELL_NOUN", "BOOK_NOUN", "INTEGER", "COMMENT", 
			"EOL", "STRING", "FREE_TEXT_EOL"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'stat:'", "'verb:'", "'spell-noun:'", "'book-noun:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "STAT", "VERB", "SPELL_NOUN", "BOOK_NOUN", "INTEGER", "COMMENT", 
			"EOL", "STRING", "FREE_TEXT_EOL"
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


	public RealmLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "RealmLexer.g4"; }

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

	public static final String _serializedATN =
		"\u0004\u0000\ny\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000\u0007"+
		"\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007"+
		"\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007"+
		"\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0005\u0003\u0005K\b\u0005\u0001\u0005"+
		"\u0004\u0005N\b\u0005\u000b\u0005\f\u0005O\u0001\u0006\u0001\u0006\u0005"+
		"\u0006T\b\u0006\n\u0006\f\u0006W\t\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0007\u0005\u0007^\b\u0007\n\u0007\f\u0007a\t"+
		"\u0007\u0001\u0007\u0003\u0007d\b\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\b\u0004\bk\b\b\u000b\b\f\bl\u0001\t\u0005\tp"+
		"\b\t\n\t\f\ts\t\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0000\u0000"+
		"\n\u0002\u0001\u0004\u0002\u0006\u0003\b\u0004\n\u0005\f\u0006\u000e\u0007"+
		"\u0010\b\u0012\t\u0014\n\u0002\u0000\u0001\u0002\u0001\u0000\n\n\u0002"+
		"\u0000\n\n\r\r~\u0000\u0002\u0001\u0000\u0000\u0000\u0000\u0004\u0001"+
		"\u0000\u0000\u0000\u0000\u0006\u0001\u0000\u0000\u0000\u0000\b\u0001\u0000"+
		"\u0000\u0000\u0000\n\u0001\u0000\u0000\u0000\u0000\f\u0001\u0000\u0000"+
		"\u0000\u0000\u000e\u0001\u0000\u0000\u0000\u0000\u0010\u0001\u0000\u0000"+
		"\u0000\u0001\u0012\u0001\u0000\u0000\u0000\u0001\u0014\u0001\u0000\u0000"+
		"\u0000\u0002\u0016\u0001\u0000\u0000\u0000\u0004\u001e\u0001\u0000\u0000"+
		"\u0000\u0006&\u0001\u0000\u0000\u0000\b.\u0001\u0000\u0000\u0000\n<\u0001"+
		"\u0000\u0000\u0000\fJ\u0001\u0000\u0000\u0000\u000eQ\u0001\u0000\u0000"+
		"\u0000\u0010_\u0001\u0000\u0000\u0000\u0012j\u0001\u0000\u0000\u0000\u0014"+
		"q\u0001\u0000\u0000\u0000\u0016\u0017\u0005n\u0000\u0000\u0017\u0018\u0005"+
		"a\u0000\u0000\u0018\u0019\u0005m\u0000\u0000\u0019\u001a\u0005e\u0000"+
		"\u0000\u001a\u001b\u0005:\u0000\u0000\u001b\u001c\u0001\u0000\u0000\u0000"+
		"\u001c\u001d\u0006\u0000\u0000\u0000\u001d\u0003\u0001\u0000\u0000\u0000"+
		"\u001e\u001f\u0005s\u0000\u0000\u001f \u0005t\u0000\u0000 !\u0005a\u0000"+
		"\u0000!\"\u0005t\u0000\u0000\"#\u0005:\u0000\u0000#$\u0001\u0000\u0000"+
		"\u0000$%\u0006\u0001\u0000\u0000%\u0005\u0001\u0000\u0000\u0000&\'\u0005"+
		"v\u0000\u0000\'(\u0005e\u0000\u0000()\u0005r\u0000\u0000)*\u0005b\u0000"+
		"\u0000*+\u0005:\u0000\u0000+,\u0001\u0000\u0000\u0000,-\u0006\u0002\u0000"+
		"\u0000-\u0007\u0001\u0000\u0000\u0000./\u0005s\u0000\u0000/0\u0005p\u0000"+
		"\u000001\u0005e\u0000\u000012\u0005l\u0000\u000023\u0005l\u0000\u0000"+
		"34\u0005-\u0000\u000045\u0005n\u0000\u000056\u0005o\u0000\u000067\u0005"+
		"u\u0000\u000078\u0005n\u0000\u000089\u0005:\u0000\u00009:\u0001\u0000"+
		"\u0000\u0000:;\u0006\u0003\u0000\u0000;\t\u0001\u0000\u0000\u0000<=\u0005"+
		"b\u0000\u0000=>\u0005o\u0000\u0000>?\u0005o\u0000\u0000?@\u0005k\u0000"+
		"\u0000@A\u0005-\u0000\u0000AB\u0005n\u0000\u0000BC\u0005o\u0000\u0000"+
		"CD\u0005u\u0000\u0000DE\u0005n\u0000\u0000EF\u0005:\u0000\u0000FG\u0001"+
		"\u0000\u0000\u0000GH\u0006\u0004\u0000\u0000H\u000b\u0001\u0000\u0000"+
		"\u0000IK\u0005-\u0000\u0000JI\u0001\u0000\u0000\u0000JK\u0001\u0000\u0000"+
		"\u0000KM\u0001\u0000\u0000\u0000LN\u000209\u0000ML\u0001\u0000\u0000\u0000"+
		"NO\u0001\u0000\u0000\u0000OM\u0001\u0000\u0000\u0000OP\u0001\u0000\u0000"+
		"\u0000P\r\u0001\u0000\u0000\u0000QU\u0005#\u0000\u0000RT\b\u0000\u0000"+
		"\u0000SR\u0001\u0000\u0000\u0000TW\u0001\u0000\u0000\u0000US\u0001\u0000"+
		"\u0000\u0000UV\u0001\u0000\u0000\u0000VX\u0001\u0000\u0000\u0000WU\u0001"+
		"\u0000\u0000\u0000XY\u0005\n\u0000\u0000YZ\u0001\u0000\u0000\u0000Z[\u0006"+
		"\u0006\u0001\u0000[\u000f\u0001\u0000\u0000\u0000\\^\u0005 \u0000\u0000"+
		"]\\\u0001\u0000\u0000\u0000^a\u0001\u0000\u0000\u0000_]\u0001\u0000\u0000"+
		"\u0000_`\u0001\u0000\u0000\u0000`c\u0001\u0000\u0000\u0000a_\u0001\u0000"+
		"\u0000\u0000bd\u0005\r\u0000\u0000cb\u0001\u0000\u0000\u0000cd\u0001\u0000"+
		"\u0000\u0000de\u0001\u0000\u0000\u0000ef\u0005\n\u0000\u0000fg\u0001\u0000"+
		"\u0000\u0000gh\u0006\u0007\u0001\u0000h\u0011\u0001\u0000\u0000\u0000"+
		"ik\b\u0001\u0000\u0000ji\u0001\u0000\u0000\u0000kl\u0001\u0000\u0000\u0000"+
		"lj\u0001\u0000\u0000\u0000lm\u0001\u0000\u0000\u0000m\u0013\u0001\u0000"+
		"\u0000\u0000np\u0005\r\u0000\u0000on\u0001\u0000\u0000\u0000ps\u0001\u0000"+
		"\u0000\u0000qo\u0001\u0000\u0000\u0000qr\u0001\u0000\u0000\u0000rt\u0001"+
		"\u0000\u0000\u0000sq\u0001\u0000\u0000\u0000tu\u0005\n\u0000\u0000uv\u0001"+
		"\u0000\u0000\u0000vw\u0006\t\u0001\u0000wx\u0006\t\u0002\u0000x\u0015"+
		"\u0001\u0000\u0000\u0000\t\u0000\u0001JOU_clq\u0003\u0005\u0001\u0000"+
		"\u0006\u0000\u0000\u0004\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}