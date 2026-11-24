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

// Generated from Random.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.random;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class RandomLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		SPACE=1, D=2, M=3, PLUS=4, MINUS=5, DOLLAR_LETTER=6, SPACE_NUMBER=7, NUMBER=8;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"SPACE", "D", "M", "PLUS", "MINUS", "DOLLAR_LETTER", "SPACE_NUMBER", 
			"NUMBER"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, "'d'", null, "'+'", "'-'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "SPACE", "D", "M", "PLUS", "MINUS", "DOLLAR_LETTER", "SPACE_NUMBER", 
			"NUMBER"
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


	public RandomLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Random.g4"; }

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
		case 6:
			return SPACE_NUMBER_sempred((RuleContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean SPACE_NUMBER_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return !Character.isDigit(_input.LA(1));
		case 1:
			return _tokenStartCharIndex > 0;
		case 2:
			return !Character.isDigit(_input.LA(1));
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0000\bR\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0001\u0000\u0004\u0000\u0013\b\u0000\u000b\u0000\f\u0000"+
		"\u0014\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0003\u0001"+
		"\u0003\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0006\u0004\u0006#\b\u0006\u000b\u0006\f\u0006$\u0001\u0006\u0001\u0006"+
		"\u0004\u0006)\b\u0006\u000b\u0006\f\u0006*\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0004\u00061\b\u0006\u000b\u0006\f\u00062\u0001\u0006"+
		"\u0001\u0006\u0005\u00067\b\u0006\n\u0006\f\u0006:\t\u0006\u0001\u0006"+
		"\u0004\u0006=\b\u0006\u000b\u0006\f\u0006>\u0001\u0006\u0004\u0006B\b"+
		"\u0006\u000b\u0006\f\u0006C\u0001\u0006\u0001\u0006\u0004\u0006H\b\u0006"+
		"\u000b\u0006\f\u0006I\u0003\u0006L\b\u0006\u0001\u0007\u0004\u0007O\b"+
		"\u0007\u000b\u0007\f\u0007P\u0000\u0000\b\u0001\u0001\u0003\u0002\u0005"+
		"\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0001\u0000\u0002"+
		"\u0002\u0000\t\t  \u0002\u0000MMmm]\u0000\u0001\u0001\u0000\u0000\u0000"+
		"\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000"+
		"\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000"+
		"\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f"+
		"\u0001\u0000\u0000\u0000\u0001\u0012\u0001\u0000\u0000\u0000\u0003\u0016"+
		"\u0001\u0000\u0000\u0000\u0005\u0018\u0001\u0000\u0000\u0000\u0007\u001a"+
		"\u0001\u0000\u0000\u0000\t\u001c\u0001\u0000\u0000\u0000\u000b\u001e\u0001"+
		"\u0000\u0000\u0000\rK\u0001\u0000\u0000\u0000\u000fN\u0001\u0000\u0000"+
		"\u0000\u0011\u0013\u0007\u0000\u0000\u0000\u0012\u0011\u0001\u0000\u0000"+
		"\u0000\u0013\u0014\u0001\u0000\u0000\u0000\u0014\u0012\u0001\u0000\u0000"+
		"\u0000\u0014\u0015\u0001\u0000\u0000\u0000\u0015\u0002\u0001\u0000\u0000"+
		"\u0000\u0016\u0017\u0005d\u0000\u0000\u0017\u0004\u0001\u0000\u0000\u0000"+
		"\u0018\u0019\u0007\u0001\u0000\u0000\u0019\u0006\u0001\u0000\u0000\u0000"+
		"\u001a\u001b\u0005+\u0000\u0000\u001b\b\u0001\u0000\u0000\u0000\u001c"+
		"\u001d\u0005-\u0000\u0000\u001d\n\u0001\u0000\u0000\u0000\u001e\u001f"+
		"\u0005$\u0000\u0000\u001f \u0002AZ\u0000 \f\u0001\u0000\u0000\u0000!#"+
		"\u0007\u0000\u0000\u0000\"!\u0001\u0000\u0000\u0000#$\u0001\u0000\u0000"+
		"\u0000$\"\u0001\u0000\u0000\u0000$%\u0001\u0000\u0000\u0000%&\u0001\u0000"+
		"\u0000\u0000&(\u0005-\u0000\u0000\')\u00050\u0000\u0000(\'\u0001\u0000"+
		"\u0000\u0000)*\u0001\u0000\u0000\u0000*(\u0001\u0000\u0000\u0000*+\u0001"+
		"\u0000\u0000\u0000+,\u0001\u0000\u0000\u0000,L\u0004\u0006\u0000\u0000"+
		"-.\u0004\u0006\u0001\u0000.0\u0005-\u0000\u0000/1\u00050\u0000\u00000"+
		"/\u0001\u0000\u0000\u000012\u0001\u0000\u0000\u000020\u0001\u0000\u0000"+
		"\u000023\u0001\u0000\u0000\u000034\u0001\u0000\u0000\u00004L\u0004\u0006"+
		"\u0002\u000057\u0007\u0000\u0000\u000065\u0001\u0000\u0000\u00007:\u0001"+
		"\u0000\u0000\u000086\u0001\u0000\u0000\u000089\u0001\u0000\u0000\u0000"+
		"9<\u0001\u0000\u0000\u0000:8\u0001\u0000\u0000\u0000;=\u000209\u0000<"+
		";\u0001\u0000\u0000\u0000=>\u0001\u0000\u0000\u0000><\u0001\u0000\u0000"+
		"\u0000>?\u0001\u0000\u0000\u0000?L\u0001\u0000\u0000\u0000@B\u0007\u0000"+
		"\u0000\u0000A@\u0001\u0000\u0000\u0000BC\u0001\u0000\u0000\u0000CA\u0001"+
		"\u0000\u0000\u0000CD\u0001\u0000\u0000\u0000DE\u0001\u0000\u0000\u0000"+
		"EG\u0003\u0007\u0003\u0000FH\u000209\u0000GF\u0001\u0000\u0000\u0000H"+
		"I\u0001\u0000\u0000\u0000IG\u0001\u0000\u0000\u0000IJ\u0001\u0000\u0000"+
		"\u0000JL\u0001\u0000\u0000\u0000K\"\u0001\u0000\u0000\u0000K-\u0001\u0000"+
		"\u0000\u0000K8\u0001\u0000\u0000\u0000KA\u0001\u0000\u0000\u0000L\u000e"+
		"\u0001\u0000\u0000\u0000MO\u000209\u0000NM\u0001\u0000\u0000\u0000OP\u0001"+
		"\u0000\u0000\u0000PN\u0001\u0000\u0000\u0000PQ\u0001\u0000\u0000\u0000"+
		"Q\u0010\u0001\u0000\u0000\u0000\u000b\u0000\u0014$*28>CIKP\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}