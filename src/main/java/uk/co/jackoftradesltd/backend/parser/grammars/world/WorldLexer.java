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

// Generated from WorldLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.world;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class WorldLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		LEVEL=1, COLON=2, INTEGER=3, NAME=4, COMMENT=5, INDENT=6, EOL=7;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"LEVEL", "COLON", "INTEGER", "NAME", "COMMENT", "NL", "NEWLINE", "INDENT", 
			"EOL"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'level:'", "':'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "LEVEL", "COLON", "INTEGER", "NAME", "COMMENT", "INDENT", "EOL"
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


	public WorldLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "WorldLexer.g4"; }

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
		"\u0004\u0000\u0007^\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001"+
		"\u0002\u0003\u0002\u001e\b\u0002\u0001\u0002\u0004\u0002!\b\u0002\u000b"+
		"\u0002\f\u0002\"\u0001\u0003\u0004\u0003&\b\u0003\u000b\u0003\f\u0003"+
		"\'\u0001\u0004\u0001\u0004\u0005\u0004,\b\u0004\n\u0004\f\u0004/\t\u0004"+
		"\u0001\u0004\u0001\u0004\u0003\u00043\b\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0005\u0005\u00058\b\u0005\n\u0005\f\u0005;\t\u0005\u0001\u0005"+
		"\u0004\u0005>\b\u0005\u000b\u0005\f\u0005?\u0001\u0005\u0003\u0005C\b"+
		"\u0005\u0001\u0005\u0003\u0005F\b\u0005\u0001\u0006\u0004\u0006I\b\u0006"+
		"\u000b\u0006\f\u0006J\u0001\u0006\u0003\u0006N\b\u0006\u0001\u0006\u0003"+
		"\u0006Q\b\u0006\u0001\u0007\u0001\u0007\u0004\u0007U\b\u0007\u000b\u0007"+
		"\f\u0007V\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0000"+
		"\u0000\t\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b"+
		"\u0000\r\u0000\u000f\u0006\u0011\u0007\u0001\u0000\u0003\u0003\u0000\n"+
		"\n\r\r::\u0002\u0000\n\n\r\r\u0002\u0000\t\t  h\u0000\u0001\u0001\u0000"+
		"\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000"+
		"\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000"+
		"\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000"+
		"\u0000\u0001\u0013\u0001\u0000\u0000\u0000\u0003\u001a\u0001\u0000\u0000"+
		"\u0000\u0005\u001d\u0001\u0000\u0000\u0000\u0007%\u0001\u0000\u0000\u0000"+
		"\t)\u0001\u0000\u0000\u0000\u000b9\u0001\u0000\u0000\u0000\rP\u0001\u0000"+
		"\u0000\u0000\u000fR\u0001\u0000\u0000\u0000\u0011Z\u0001\u0000\u0000\u0000"+
		"\u0013\u0014\u0005l\u0000\u0000\u0014\u0015\u0005e\u0000\u0000\u0015\u0016"+
		"\u0005v\u0000\u0000\u0016\u0017\u0005e\u0000\u0000\u0017\u0018\u0005l"+
		"\u0000\u0000\u0018\u0019\u0005:\u0000\u0000\u0019\u0002\u0001\u0000\u0000"+
		"\u0000\u001a\u001b\u0005:\u0000\u0000\u001b\u0004\u0001\u0000\u0000\u0000"+
		"\u001c\u001e\u0005-\u0000\u0000\u001d\u001c\u0001\u0000\u0000\u0000\u001d"+
		"\u001e\u0001\u0000\u0000\u0000\u001e \u0001\u0000\u0000\u0000\u001f!\u0002"+
		"09\u0000 \u001f\u0001\u0000\u0000\u0000!\"\u0001\u0000\u0000\u0000\" "+
		"\u0001\u0000\u0000\u0000\"#\u0001\u0000\u0000\u0000#\u0006\u0001\u0000"+
		"\u0000\u0000$&\b\u0000\u0000\u0000%$\u0001\u0000\u0000\u0000&\'\u0001"+
		"\u0000\u0000\u0000\'%\u0001\u0000\u0000\u0000\'(\u0001\u0000\u0000\u0000"+
		"(\b\u0001\u0000\u0000\u0000)-\u0005#\u0000\u0000*,\b\u0001\u0000\u0000"+
		"+*\u0001\u0000\u0000\u0000,/\u0001\u0000\u0000\u0000-+\u0001\u0000\u0000"+
		"\u0000-.\u0001\u0000\u0000\u0000.2\u0001\u0000\u0000\u0000/-\u0001\u0000"+
		"\u0000\u000003\u0003\u000b\u0005\u000013\u0005\u0000\u0000\u000120\u0001"+
		"\u0000\u0000\u000021\u0001\u0000\u0000\u000034\u0001\u0000\u0000\u0000"+
		"45\u0006\u0004\u0000\u00005\n\u0001\u0000\u0000\u000068\u0007\u0002\u0000"+
		"\u000076\u0001\u0000\u0000\u00008;\u0001\u0000\u0000\u000097\u0001\u0000"+
		"\u0000\u00009:\u0001\u0000\u0000\u0000:E\u0001\u0000\u0000\u0000;9\u0001"+
		"\u0000\u0000\u0000<>\u0005\r\u0000\u0000=<\u0001\u0000\u0000\u0000>?\u0001"+
		"\u0000\u0000\u0000?=\u0001\u0000\u0000\u0000?@\u0001\u0000\u0000\u0000"+
		"@B\u0001\u0000\u0000\u0000AC\u0005\n\u0000\u0000BA\u0001\u0000\u0000\u0000"+
		"BC\u0001\u0000\u0000\u0000CF\u0001\u0000\u0000\u0000DF\u0005\n\u0000\u0000"+
		"E=\u0001\u0000\u0000\u0000ED\u0001\u0000\u0000\u0000F\f\u0001\u0000\u0000"+
		"\u0000GI\u0005\r\u0000\u0000HG\u0001\u0000\u0000\u0000IJ\u0001\u0000\u0000"+
		"\u0000JH\u0001\u0000\u0000\u0000JK\u0001\u0000\u0000\u0000KM\u0001\u0000"+
		"\u0000\u0000LN\u0005\n\u0000\u0000ML\u0001\u0000\u0000\u0000MN\u0001\u0000"+
		"\u0000\u0000NQ\u0001\u0000\u0000\u0000OQ\u0005\n\u0000\u0000PH\u0001\u0000"+
		"\u0000\u0000PO\u0001\u0000\u0000\u0000Q\u000e\u0001\u0000\u0000\u0000"+
		"RT\u0004\u0007\u0000\u0000SU\u0007\u0002\u0000\u0000TS\u0001\u0000\u0000"+
		"\u0000UV\u0001\u0000\u0000\u0000VT\u0001\u0000\u0000\u0000VW\u0001\u0000"+
		"\u0000\u0000WX\u0001\u0000\u0000\u0000XY\u0006\u0007\u0000\u0000Y\u0010"+
		"\u0001\u0000\u0000\u0000Z[\u0003\u000b\u0005\u0000[\\\u0001\u0000\u0000"+
		"\u0000\\]\u0006\b\u0000\u0000]\u0012\u0001\u0000\u0000\u0000\u000e\u0000"+
		"\u001d\"\'-29?BEJMPV\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}