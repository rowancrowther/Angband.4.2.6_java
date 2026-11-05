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

// Generated from /home/rowan/IdeaProjects/Angband.4.2.6_java/src/main/java/uk/co/jackoftradesltd/backend/parser/grammars/SummonLexer.g4 by ANTLR 4.13.2
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
		COMMENT=9, EOL=10, FLAG=11, STRING=12;
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
			"NAME", "MSGT", "UNIQUES", "BASE", "RACE_FLAG", "FALLBACK", "DESC", "INTEGER", 
			"COMMENT", "EOL", "FLAG_BODY", "FLAG", "STRING"
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
			"INTEGER", "COMMENT", "EOL", "FLAG", "STRING"
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

	public static final String _serializedATN =
		"\u0004\u0000\f\u0091\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff\uffff"+
		"\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002"+
		"\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005"+
		"\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002"+
		"\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007\f\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0007\u0003\u0007a\b\u0007\u0001\u0007\u0004\u0007d\b\u0007\u000b\u0007"+
		"\f\u0007e\u0001\b\u0001\b\u0005\bj\b\b\n\b\f\bm\t\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\t\u0005\tt\b\t\n\t\f\tw\t\t\u0001\t\u0003\tz\b\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0005\n\u0082\b\n\n\n\f\n\u0085"+
		"\t\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0004\f\u008c"+
		"\b\f\u000b\f\f\f\u008d\u0001\f\u0001\f\u0000\u0000\r\u0003\u0001\u0005"+
		"\u0002\u0007\u0003\t\u0004\u000b\u0005\r\u0006\u000f\u0007\u0011\b\u0013"+
		"\t\u0015\n\u0017\u0000\u0019\u000b\u001b\f\u0003\u0000\u0001\u0002\u0003"+
		"\u0001\u0000\n\n\u0003\u000009AZ__\u0002\u0000\n\n\r\r\u0094\u0000\u0003"+
		"\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007"+
		"\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001"+
		"\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000"+
		"\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000"+
		"\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0001\u0019\u0001\u0000"+
		"\u0000\u0000\u0002\u001b\u0001\u0000\u0000\u0000\u0003\u001d\u0001\u0000"+
		"\u0000\u0000\u0005%\u0001\u0000\u0000\u0000\u0007-\u0001\u0000\u0000\u0000"+
		"\t6\u0001\u0000\u0000\u0000\u000b>\u0001\u0000\u0000\u0000\rK\u0001\u0000"+
		"\u0000\u0000\u000fW\u0001\u0000\u0000\u0000\u0011`\u0001\u0000\u0000\u0000"+
		"\u0013g\u0001\u0000\u0000\u0000\u0015u\u0001\u0000\u0000\u0000\u0017\u007f"+
		"\u0001\u0000\u0000\u0000\u0019\u0086\u0001\u0000\u0000\u0000\u001b\u008b"+
		"\u0001\u0000\u0000\u0000\u001d\u001e\u0005n\u0000\u0000\u001e\u001f\u0005"+
		"a\u0000\u0000\u001f \u0005m\u0000\u0000 !\u0005e\u0000\u0000!\"\u0005"+
		":\u0000\u0000\"#\u0001\u0000\u0000\u0000#$\u0006\u0000\u0000\u0000$\u0004"+
		"\u0001\u0000\u0000\u0000%&\u0005m\u0000\u0000&\'\u0005s\u0000\u0000\'"+
		"(\u0005g\u0000\u0000()\u0005t\u0000\u0000)*\u0005:\u0000\u0000*+\u0001"+
		"\u0000\u0000\u0000+,\u0006\u0001\u0001\u0000,\u0006\u0001\u0000\u0000"+
		"\u0000-.\u0005u\u0000\u0000./\u0005n\u0000\u0000/0\u0005i\u0000\u0000"+
		"01\u0005q\u0000\u000012\u0005u\u0000\u000023\u0005e\u0000\u000034\u0005"+
		"s\u0000\u000045\u0005:\u0000\u00005\b\u0001\u0000\u0000\u000067\u0005"+
		"b\u0000\u000078\u0005a\u0000\u000089\u0005s\u0000\u00009:\u0005e\u0000"+
		"\u0000:;\u0005:\u0000\u0000;<\u0001\u0000\u0000\u0000<=\u0006\u0003\u0000"+
		"\u0000=\n\u0001\u0000\u0000\u0000>?\u0005r\u0000\u0000?@\u0005a\u0000"+
		"\u0000@A\u0005c\u0000\u0000AB\u0005e\u0000\u0000BC\u0005-\u0000\u0000"+
		"CD\u0005f\u0000\u0000DE\u0005l\u0000\u0000EF\u0005a\u0000\u0000FG\u0005"+
		"g\u0000\u0000GH\u0005:\u0000\u0000HI\u0001\u0000\u0000\u0000IJ\u0006\u0004"+
		"\u0001\u0000J\f\u0001\u0000\u0000\u0000KL\u0005f\u0000\u0000LM\u0005a"+
		"\u0000\u0000MN\u0005l\u0000\u0000NO\u0005l\u0000\u0000OP\u0005b\u0000"+
		"\u0000PQ\u0005a\u0000\u0000QR\u0005c\u0000\u0000RS\u0005k\u0000\u0000"+
		"ST\u0005:\u0000\u0000TU\u0001\u0000\u0000\u0000UV\u0006\u0005\u0000\u0000"+
		"V\u000e\u0001\u0000\u0000\u0000WX\u0005d\u0000\u0000XY\u0005e\u0000\u0000"+
		"YZ\u0005s\u0000\u0000Z[\u0005c\u0000\u0000[\\\u0005:\u0000\u0000\\]\u0001"+
		"\u0000\u0000\u0000]^\u0006\u0006\u0000\u0000^\u0010\u0001\u0000\u0000"+
		"\u0000_a\u0005-\u0000\u0000`_\u0001\u0000\u0000\u0000`a\u0001\u0000\u0000"+
		"\u0000ac\u0001\u0000\u0000\u0000bd\u000209\u0000cb\u0001\u0000\u0000\u0000"+
		"de\u0001\u0000\u0000\u0000ec\u0001\u0000\u0000\u0000ef\u0001\u0000\u0000"+
		"\u0000f\u0012\u0001\u0000\u0000\u0000gk\u0005#\u0000\u0000hj\b\u0000\u0000"+
		"\u0000ih\u0001\u0000\u0000\u0000jm\u0001\u0000\u0000\u0000ki\u0001\u0000"+
		"\u0000\u0000kl\u0001\u0000\u0000\u0000ln\u0001\u0000\u0000\u0000mk\u0001"+
		"\u0000\u0000\u0000no\u0005\n\u0000\u0000op\u0001\u0000\u0000\u0000pq\u0006"+
		"\b\u0002\u0000q\u0014\u0001\u0000\u0000\u0000rt\u0005 \u0000\u0000sr\u0001"+
		"\u0000\u0000\u0000tw\u0001\u0000\u0000\u0000us\u0001\u0000\u0000\u0000"+
		"uv\u0001\u0000\u0000\u0000vy\u0001\u0000\u0000\u0000wu\u0001\u0000\u0000"+
		"\u0000xz\u0005\r\u0000\u0000yx\u0001\u0000\u0000\u0000yz\u0001\u0000\u0000"+
		"\u0000z{\u0001\u0000\u0000\u0000{|\u0005\n\u0000\u0000|}\u0001\u0000\u0000"+
		"\u0000}~\u0006\t\u0002\u0000~\u0016\u0001\u0000\u0000\u0000\u007f\u0083"+
		"\u0002AZ\u0000\u0080\u0082\u0007\u0001\u0000\u0000\u0081\u0080\u0001\u0000"+
		"\u0000\u0000\u0082\u0085\u0001\u0000\u0000\u0000\u0083\u0081\u0001\u0000"+
		"\u0000\u0000\u0083\u0084\u0001\u0000\u0000\u0000\u0084\u0018\u0001\u0000"+
		"\u0000\u0000\u0085\u0083\u0001\u0000\u0000\u0000\u0086\u0087\u0003\u0017"+
		"\n\u0000\u0087\u0088\u0001\u0000\u0000\u0000\u0088\u0089\u0006\u000b\u0003"+
		"\u0000\u0089\u001a\u0001\u0000\u0000\u0000\u008a\u008c\b\u0002\u0000\u0000"+
		"\u008b\u008a\u0001\u0000\u0000\u0000\u008c\u008d\u0001\u0000\u0000\u0000"+
		"\u008d\u008b\u0001\u0000\u0000\u0000\u008d\u008e\u0001\u0000\u0000\u0000"+
		"\u008e\u008f\u0001\u0000\u0000\u0000\u008f\u0090\u0006\f\u0003\u0000\u0090"+
		"\u001c\u0001\u0000\u0000\u0000\n\u0000\u0001\u0002`ekuy\u0083\u008d\u0004"+
		"\u0005\u0002\u0000\u0005\u0001\u0000\u0006\u0000\u0000\u0004\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}