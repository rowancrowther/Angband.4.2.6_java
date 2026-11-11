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

// Generated from HistoryLexer.g4 by ANTLR 4.13.2
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
		CHART=1, PHRASE=2, INTEGER=3, COLON=4, COMMENT=5, INDENT=6, EOL=7, STRING=8, 
		STRING_EOL=9;
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
			"CHART", "PHRASE", "INTEGER", "COLON", "COMMENT", "NL", "NEWLINE", "INDENT", 
			"EOL", "STRING", "STRING_EOL"
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
			null, "CHART", "PHRASE", "INTEGER", "COLON", "COMMENT", "INDENT", "EOL", 
			"STRING", "STRING_EOL"
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

	@Override
	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 7:
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
		"\u0004\u0000\tr\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000\u0007"+
		"\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007"+
		"\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007"+
		"\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n"+
		"\u0007\n\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0002\u0003\u0002+\b\u0002\u0001\u0002\u0004\u0002.\b\u0002\u000b\u0002"+
		"\f\u0002/\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0005\u0004"+
		"6\b\u0004\n\u0004\f\u00049\t\u0004\u0001\u0004\u0001\u0004\u0003\u0004"+
		"=\b\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0005\u0005B\b\u0005\n\u0005"+
		"\f\u0005E\t\u0005\u0001\u0005\u0004\u0005H\b\u0005\u000b\u0005\f\u0005"+
		"I\u0001\u0005\u0003\u0005M\b\u0005\u0001\u0005\u0003\u0005P\b\u0005\u0001"+
		"\u0006\u0004\u0006S\b\u0006\u000b\u0006\f\u0006T\u0001\u0006\u0003\u0006"+
		"X\b\u0006\u0001\u0006\u0003\u0006[\b\u0006\u0001\u0007\u0001\u0007\u0004"+
		"\u0007_\b\u0007\u000b\u0007\f\u0007`\u0001\u0007\u0001\u0007\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\t\u0004\tj\b\t\u000b\t\f\tk\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0000\u0000\u000b\u0002\u0001\u0004\u0002\u0006\u0003"+
		"\b\u0004\n\u0005\f\u0000\u000e\u0000\u0010\u0006\u0012\u0007\u0014\b\u0016"+
		"\t\u0002\u0000\u0001\u0002\u0002\u0000\n\n\r\r\u0002\u0000\t\t  {\u0000"+
		"\u0002\u0001\u0000\u0000\u0000\u0000\u0004\u0001\u0000\u0000\u0000\u0000"+
		"\u0006\u0001\u0000\u0000\u0000\u0000\b\u0001\u0000\u0000\u0000\u0000\n"+
		"\u0001\u0000\u0000\u0000\u0000\u0010\u0001\u0000\u0000\u0000\u0000\u0012"+
		"\u0001\u0000\u0000\u0000\u0001\u0014\u0001\u0000\u0000\u0000\u0001\u0016"+
		"\u0001\u0000\u0000\u0000\u0002\u0018\u0001\u0000\u0000\u0000\u0004\u001f"+
		"\u0001\u0000\u0000\u0000\u0006*\u0001\u0000\u0000\u0000\b1\u0001\u0000"+
		"\u0000\u0000\n3\u0001\u0000\u0000\u0000\fC\u0001\u0000\u0000\u0000\u000e"+
		"Z\u0001\u0000\u0000\u0000\u0010\\\u0001\u0000\u0000\u0000\u0012d\u0001"+
		"\u0000\u0000\u0000\u0014i\u0001\u0000\u0000\u0000\u0016m\u0001\u0000\u0000"+
		"\u0000\u0018\u0019\u0005c\u0000\u0000\u0019\u001a\u0005h\u0000\u0000\u001a"+
		"\u001b\u0005a\u0000\u0000\u001b\u001c\u0005r\u0000\u0000\u001c\u001d\u0005"+
		"t\u0000\u0000\u001d\u001e\u0005:\u0000\u0000\u001e\u0003\u0001\u0000\u0000"+
		"\u0000\u001f \u0005p\u0000\u0000 !\u0005h\u0000\u0000!\"\u0005r\u0000"+
		"\u0000\"#\u0005a\u0000\u0000#$\u0005s\u0000\u0000$%\u0005e\u0000\u0000"+
		"%&\u0005:\u0000\u0000&\'\u0001\u0000\u0000\u0000\'(\u0006\u0001\u0000"+
		"\u0000(\u0005\u0001\u0000\u0000\u0000)+\u0005-\u0000\u0000*)\u0001\u0000"+
		"\u0000\u0000*+\u0001\u0000\u0000\u0000+-\u0001\u0000\u0000\u0000,.\u0002"+
		"09\u0000-,\u0001\u0000\u0000\u0000./\u0001\u0000\u0000\u0000/-\u0001\u0000"+
		"\u0000\u0000/0\u0001\u0000\u0000\u00000\u0007\u0001\u0000\u0000\u0000"+
		"12\u0005:\u0000\u00002\t\u0001\u0000\u0000\u000037\u0005#\u0000\u0000"+
		"46\b\u0000\u0000\u000054\u0001\u0000\u0000\u000069\u0001\u0000\u0000\u0000"+
		"75\u0001\u0000\u0000\u000078\u0001\u0000\u0000\u00008<\u0001\u0000\u0000"+
		"\u000097\u0001\u0000\u0000\u0000:=\u0003\f\u0005\u0000;=\u0005\u0000\u0000"+
		"\u0001<:\u0001\u0000\u0000\u0000<;\u0001\u0000\u0000\u0000=>\u0001\u0000"+
		"\u0000\u0000>?\u0006\u0004\u0001\u0000?\u000b\u0001\u0000\u0000\u0000"+
		"@B\u0007\u0001\u0000\u0000A@\u0001\u0000\u0000\u0000BE\u0001\u0000\u0000"+
		"\u0000CA\u0001\u0000\u0000\u0000CD\u0001\u0000\u0000\u0000DO\u0001\u0000"+
		"\u0000\u0000EC\u0001\u0000\u0000\u0000FH\u0005\r\u0000\u0000GF\u0001\u0000"+
		"\u0000\u0000HI\u0001\u0000\u0000\u0000IG\u0001\u0000\u0000\u0000IJ\u0001"+
		"\u0000\u0000\u0000JL\u0001\u0000\u0000\u0000KM\u0005\n\u0000\u0000LK\u0001"+
		"\u0000\u0000\u0000LM\u0001\u0000\u0000\u0000MP\u0001\u0000\u0000\u0000"+
		"NP\u0005\n\u0000\u0000OG\u0001\u0000\u0000\u0000ON\u0001\u0000\u0000\u0000"+
		"P\r\u0001\u0000\u0000\u0000QS\u0005\r\u0000\u0000RQ\u0001\u0000\u0000"+
		"\u0000ST\u0001\u0000\u0000\u0000TR\u0001\u0000\u0000\u0000TU\u0001\u0000"+
		"\u0000\u0000UW\u0001\u0000\u0000\u0000VX\u0005\n\u0000\u0000WV\u0001\u0000"+
		"\u0000\u0000WX\u0001\u0000\u0000\u0000X[\u0001\u0000\u0000\u0000Y[\u0005"+
		"\n\u0000\u0000ZR\u0001\u0000\u0000\u0000ZY\u0001\u0000\u0000\u0000[\u000f"+
		"\u0001\u0000\u0000\u0000\\^\u0004\u0007\u0000\u0000]_\u0007\u0001\u0000"+
		"\u0000^]\u0001\u0000\u0000\u0000_`\u0001\u0000\u0000\u0000`^\u0001\u0000"+
		"\u0000\u0000`a\u0001\u0000\u0000\u0000ab\u0001\u0000\u0000\u0000bc\u0006"+
		"\u0007\u0001\u0000c\u0011\u0001\u0000\u0000\u0000de\u0003\f\u0005\u0000"+
		"ef\u0001\u0000\u0000\u0000fg\u0006\b\u0001\u0000g\u0013\u0001\u0000\u0000"+
		"\u0000hj\b\u0000\u0000\u0000ih\u0001\u0000\u0000\u0000jk\u0001\u0000\u0000"+
		"\u0000ki\u0001\u0000\u0000\u0000kl\u0001\u0000\u0000\u0000l\u0015\u0001"+
		"\u0000\u0000\u0000mn\u0003\f\u0005\u0000no\u0001\u0000\u0000\u0000op\u0006"+
		"\n\u0002\u0000pq\u0006\n\u0001\u0000q\u0017\u0001\u0000\u0000\u0000\u000f"+
		"\u0000\u0001*/7<CILOTWZ`k\u0003\u0005\u0001\u0000\u0006\u0000\u0000\u0004"+
		"\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}