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

// Generated from /home/rowan/IdeaProjects/Angband.4.2.6_java/src/main/java/uk/co/jackoftradesltd/backend/parser/grammars/HistoryLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.history;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class HistoryLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		CHART=1, PHRASE=2, INTEGER=3, COLON=4, COMMENT=5, EOL=6, STRING=7, STRING_EOL=8;
	public static final int
		FREE_TEXT_MODE=1;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "FREE_TEXT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"CHART", "PHRASE", "INTEGER", "COLON", "COMMENT", "EOL", "STRING", "STRING_EOL"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'chart:'", "'phrase:'", null, "':'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "CHART", "PHRASE", "INTEGER", "COLON", "COMMENT", "EOL", "STRING", 
			"STRING_EOL"
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


	public HistoryLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "HistoryLexer.g4"; }

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
		"\u0004\u0000\bU\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000\u0007"+
		"\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007"+
		"\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007"+
		"\u0006\u0002\u0007\u0007\u0007\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0003\u0002%\b\u0002\u0001\u0002\u0004"+
		"\u0002(\b\u0002\u000b\u0002\f\u0002)\u0001\u0003\u0001\u0003\u0001\u0004"+
		"\u0001\u0004\u0005\u00040\b\u0004\n\u0004\f\u00043\t\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0005\u0005:\b\u0005"+
		"\n\u0005\f\u0005=\t\u0005\u0001\u0005\u0003\u0005@\b\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0004\u0006G\b\u0006"+
		"\u000b\u0006\f\u0006H\u0001\u0007\u0005\u0007L\b\u0007\n\u0007\f\u0007"+
		"O\t\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0000\u0000\b\u0002\u0001\u0004\u0002\u0006\u0003\b\u0004\n\u0005\f\u0006"+
		"\u000e\u0007\u0010\b\u0002\u0000\u0001\u0002\u0001\u0000\n\n\u0002\u0000"+
		"\n\n\r\rZ\u0000\u0002\u0001\u0000\u0000\u0000\u0000\u0004\u0001\u0000"+
		"\u0000\u0000\u0000\u0006\u0001\u0000\u0000\u0000\u0000\b\u0001\u0000\u0000"+
		"\u0000\u0000\n\u0001\u0000\u0000\u0000\u0000\f\u0001\u0000\u0000\u0000"+
		"\u0001\u000e\u0001\u0000\u0000\u0000\u0001\u0010\u0001\u0000\u0000\u0000"+
		"\u0002\u0012\u0001\u0000\u0000\u0000\u0004\u0019\u0001\u0000\u0000\u0000"+
		"\u0006$\u0001\u0000\u0000\u0000\b+\u0001\u0000\u0000\u0000\n-\u0001\u0000"+
		"\u0000\u0000\f;\u0001\u0000\u0000\u0000\u000eF\u0001\u0000\u0000\u0000"+
		"\u0010M\u0001\u0000\u0000\u0000\u0012\u0013\u0005c\u0000\u0000\u0013\u0014"+
		"\u0005h\u0000\u0000\u0014\u0015\u0005a\u0000\u0000\u0015\u0016\u0005r"+
		"\u0000\u0000\u0016\u0017\u0005t\u0000\u0000\u0017\u0018\u0005:\u0000\u0000"+
		"\u0018\u0003\u0001\u0000\u0000\u0000\u0019\u001a\u0005p\u0000\u0000\u001a"+
		"\u001b\u0005h\u0000\u0000\u001b\u001c\u0005r\u0000\u0000\u001c\u001d\u0005"+
		"a\u0000\u0000\u001d\u001e\u0005s\u0000\u0000\u001e\u001f\u0005e\u0000"+
		"\u0000\u001f \u0005:\u0000\u0000 !\u0001\u0000\u0000\u0000!\"\u0006\u0001"+
		"\u0000\u0000\"\u0005\u0001\u0000\u0000\u0000#%\u0005-\u0000\u0000$#\u0001"+
		"\u0000\u0000\u0000$%\u0001\u0000\u0000\u0000%\'\u0001\u0000\u0000\u0000"+
		"&(\u000209\u0000\'&\u0001\u0000\u0000\u0000()\u0001\u0000\u0000\u0000"+
		")\'\u0001\u0000\u0000\u0000)*\u0001\u0000\u0000\u0000*\u0007\u0001\u0000"+
		"\u0000\u0000+,\u0005:\u0000\u0000,\t\u0001\u0000\u0000\u0000-1\u0005#"+
		"\u0000\u0000.0\b\u0000\u0000\u0000/.\u0001\u0000\u0000\u000003\u0001\u0000"+
		"\u0000\u00001/\u0001\u0000\u0000\u000012\u0001\u0000\u0000\u000024\u0001"+
		"\u0000\u0000\u000031\u0001\u0000\u0000\u000045\u0005\n\u0000\u000056\u0001"+
		"\u0000\u0000\u000067\u0006\u0004\u0001\u00007\u000b\u0001\u0000\u0000"+
		"\u00008:\u0005 \u0000\u000098\u0001\u0000\u0000\u0000:=\u0001\u0000\u0000"+
		"\u0000;9\u0001\u0000\u0000\u0000;<\u0001\u0000\u0000\u0000<?\u0001\u0000"+
		"\u0000\u0000=;\u0001\u0000\u0000\u0000>@\u0005\r\u0000\u0000?>\u0001\u0000"+
		"\u0000\u0000?@\u0001\u0000\u0000\u0000@A\u0001\u0000\u0000\u0000AB\u0005"+
		"\n\u0000\u0000BC\u0001\u0000\u0000\u0000CD\u0006\u0005\u0001\u0000D\r"+
		"\u0001\u0000\u0000\u0000EG\b\u0001\u0000\u0000FE\u0001\u0000\u0000\u0000"+
		"GH\u0001\u0000\u0000\u0000HF\u0001\u0000\u0000\u0000HI\u0001\u0000\u0000"+
		"\u0000I\u000f\u0001\u0000\u0000\u0000JL\u0005\r\u0000\u0000KJ\u0001\u0000"+
		"\u0000\u0000LO\u0001\u0000\u0000\u0000MK\u0001\u0000\u0000\u0000MN\u0001"+
		"\u0000\u0000\u0000NP\u0001\u0000\u0000\u0000OM\u0001\u0000\u0000\u0000"+
		"PQ\u0005\n\u0000\u0000QR\u0001\u0000\u0000\u0000RS\u0006\u0007\u0002\u0000"+
		"ST\u0006\u0007\u0001\u0000T\u0011\u0001\u0000\u0000\u0000\t\u0000\u0001"+
		"$)1;?HM\u0003\u0005\u0001\u0000\u0006\u0000\u0000\u0004\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}