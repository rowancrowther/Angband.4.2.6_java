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

// Generated from FlavourLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.flavour;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class FlavourLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		KIND=1, FIXED=2, FLAVOUR=3, INTEGER=4, COMMENT=5, INDENT=6, EOL=7, STRING=8, 
		COLON=9, DELIM_EOL=10;
	public static final int
		DELIM=1;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "DELIM"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"KIND", "FIXED", "FLAVOUR", "INTEGER", "COMMENT", "NL", "NEWLINE", "INDENT", 
			"EOL", "STRING", "COLON", "DELIM_EOL"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'kind:'", "'fixed:'", "'flavor:'", null, null, null, null, null, 
			"':'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "KIND", "FIXED", "FLAVOUR", "INTEGER", "COMMENT", "INDENT", "EOL", 
			"STRING", "COLON", "DELIM_EOL"
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


	public FlavourLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "FlavourLexer.g4"; }

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
		"\u0004\u0000\n~\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000\u0007"+
		"\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007"+
		"\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007"+
		"\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n"+
		"\u0007\n\u0002\u000b\u0007\u000b\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0003\u0003\u00037\b\u0003\u0001\u0003\u0004\u0003:\b\u0003\u000b\u0003"+
		"\f\u0003;\u0001\u0004\u0001\u0004\u0005\u0004@\b\u0004\n\u0004\f\u0004"+
		"C\t\u0004\u0001\u0004\u0001\u0004\u0003\u0004G\b\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0005\u0005\u0005L\b\u0005\n\u0005\f\u0005O\t\u0005\u0001"+
		"\u0005\u0004\u0005R\b\u0005\u000b\u0005\f\u0005S\u0001\u0005\u0003\u0005"+
		"W\b\u0005\u0001\u0005\u0003\u0005Z\b\u0005\u0001\u0006\u0004\u0006]\b"+
		"\u0006\u000b\u0006\f\u0006^\u0001\u0006\u0003\u0006b\b\u0006\u0001\u0006"+
		"\u0003\u0006e\b\u0006\u0001\u0007\u0001\u0007\u0004\u0007i\b\u0007\u000b"+
		"\u0007\f\u0007j\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\t\u0004\tt\b\t\u000b\t\f\tu\u0001\n\u0001\n\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0000\u0000\f\u0002\u0001\u0004"+
		"\u0002\u0006\u0003\b\u0004\n\u0005\f\u0000\u000e\u0000\u0010\u0006\u0012"+
		"\u0007\u0014\b\u0016\t\u0018\n\u0002\u0000\u0001\u0003\u0002\u0000\n\n"+
		"\r\r\u0002\u0000\t\t  \u0003\u0000\n\n\r\r::\u0087\u0000\u0002\u0001\u0000"+
		"\u0000\u0000\u0000\u0004\u0001\u0000\u0000\u0000\u0000\u0006\u0001\u0000"+
		"\u0000\u0000\u0000\b\u0001\u0000\u0000\u0000\u0000\n\u0001\u0000\u0000"+
		"\u0000\u0000\u0010\u0001\u0000\u0000\u0000\u0000\u0012\u0001\u0000\u0000"+
		"\u0000\u0001\u0014\u0001\u0000\u0000\u0000\u0001\u0016\u0001\u0000\u0000"+
		"\u0000\u0001\u0018\u0001\u0000\u0000\u0000\u0002\u001a\u0001\u0000\u0000"+
		"\u0000\u0004\"\u0001\u0000\u0000\u0000\u0006+\u0001\u0000\u0000\u0000"+
		"\b6\u0001\u0000\u0000\u0000\n=\u0001\u0000\u0000\u0000\fM\u0001\u0000"+
		"\u0000\u0000\u000ed\u0001\u0000\u0000\u0000\u0010f\u0001\u0000\u0000\u0000"+
		"\u0012n\u0001\u0000\u0000\u0000\u0014s\u0001\u0000\u0000\u0000\u0016w"+
		"\u0001\u0000\u0000\u0000\u0018y\u0001\u0000\u0000\u0000\u001a\u001b\u0005"+
		"k\u0000\u0000\u001b\u001c\u0005i\u0000\u0000\u001c\u001d\u0005n\u0000"+
		"\u0000\u001d\u001e\u0005d\u0000\u0000\u001e\u001f\u0005:\u0000\u0000\u001f"+
		" \u0001\u0000\u0000\u0000 !\u0006\u0000\u0000\u0000!\u0003\u0001\u0000"+
		"\u0000\u0000\"#\u0005f\u0000\u0000#$\u0005i\u0000\u0000$%\u0005x\u0000"+
		"\u0000%&\u0005e\u0000\u0000&\'\u0005d\u0000\u0000\'(\u0005:\u0000\u0000"+
		"()\u0001\u0000\u0000\u0000)*\u0006\u0001\u0000\u0000*\u0005\u0001\u0000"+
		"\u0000\u0000+,\u0005f\u0000\u0000,-\u0005l\u0000\u0000-.\u0005a\u0000"+
		"\u0000./\u0005v\u0000\u0000/0\u0005o\u0000\u000001\u0005r\u0000\u0000"+
		"12\u0005:\u0000\u000023\u0001\u0000\u0000\u000034\u0006\u0002\u0000\u0000"+
		"4\u0007\u0001\u0000\u0000\u000057\u0005-\u0000\u000065\u0001\u0000\u0000"+
		"\u000067\u0001\u0000\u0000\u000079\u0001\u0000\u0000\u00008:\u000209\u0000"+
		"98\u0001\u0000\u0000\u0000:;\u0001\u0000\u0000\u0000;9\u0001\u0000\u0000"+
		"\u0000;<\u0001\u0000\u0000\u0000<\t\u0001\u0000\u0000\u0000=A\u0005#\u0000"+
		"\u0000>@\b\u0000\u0000\u0000?>\u0001\u0000\u0000\u0000@C\u0001\u0000\u0000"+
		"\u0000A?\u0001\u0000\u0000\u0000AB\u0001\u0000\u0000\u0000BF\u0001\u0000"+
		"\u0000\u0000CA\u0001\u0000\u0000\u0000DG\u0003\f\u0005\u0000EG\u0005\u0000"+
		"\u0000\u0001FD\u0001\u0000\u0000\u0000FE\u0001\u0000\u0000\u0000GH\u0001"+
		"\u0000\u0000\u0000HI\u0006\u0004\u0001\u0000I\u000b\u0001\u0000\u0000"+
		"\u0000JL\u0007\u0001\u0000\u0000KJ\u0001\u0000\u0000\u0000LO\u0001\u0000"+
		"\u0000\u0000MK\u0001\u0000\u0000\u0000MN\u0001\u0000\u0000\u0000NY\u0001"+
		"\u0000\u0000\u0000OM\u0001\u0000\u0000\u0000PR\u0005\r\u0000\u0000QP\u0001"+
		"\u0000\u0000\u0000RS\u0001\u0000\u0000\u0000SQ\u0001\u0000\u0000\u0000"+
		"ST\u0001\u0000\u0000\u0000TV\u0001\u0000\u0000\u0000UW\u0005\n\u0000\u0000"+
		"VU\u0001\u0000\u0000\u0000VW\u0001\u0000\u0000\u0000WZ\u0001\u0000\u0000"+
		"\u0000XZ\u0005\n\u0000\u0000YQ\u0001\u0000\u0000\u0000YX\u0001\u0000\u0000"+
		"\u0000Z\r\u0001\u0000\u0000\u0000[]\u0005\r\u0000\u0000\\[\u0001\u0000"+
		"\u0000\u0000]^\u0001\u0000\u0000\u0000^\\\u0001\u0000\u0000\u0000^_\u0001"+
		"\u0000\u0000\u0000_a\u0001\u0000\u0000\u0000`b\u0005\n\u0000\u0000a`\u0001"+
		"\u0000\u0000\u0000ab\u0001\u0000\u0000\u0000be\u0001\u0000\u0000\u0000"+
		"ce\u0005\n\u0000\u0000d\\\u0001\u0000\u0000\u0000dc\u0001\u0000\u0000"+
		"\u0000e\u000f\u0001\u0000\u0000\u0000fh\u0004\u0007\u0000\u0000gi\u0007"+
		"\u0001\u0000\u0000hg\u0001\u0000\u0000\u0000ij\u0001\u0000\u0000\u0000"+
		"jh\u0001\u0000\u0000\u0000jk\u0001\u0000\u0000\u0000kl\u0001\u0000\u0000"+
		"\u0000lm\u0006\u0007\u0001\u0000m\u0011\u0001\u0000\u0000\u0000no\u0003"+
		"\f\u0005\u0000op\u0001\u0000\u0000\u0000pq\u0006\b\u0001\u0000q\u0013"+
		"\u0001\u0000\u0000\u0000rt\b\u0002\u0000\u0000sr\u0001\u0000\u0000\u0000"+
		"tu\u0001\u0000\u0000\u0000us\u0001\u0000\u0000\u0000uv\u0001\u0000\u0000"+
		"\u0000v\u0015\u0001\u0000\u0000\u0000wx\u0005:\u0000\u0000x\u0017\u0001"+
		"\u0000\u0000\u0000yz\u0003\f\u0005\u0000z{\u0001\u0000\u0000\u0000{|\u0006"+
		"\u000b\u0002\u0000|}\u0006\u000b\u0001\u0000}\u0019\u0001\u0000\u0000"+
		"\u0000\u000f\u0000\u00016;AFMSVY^adju\u0003\u0005\u0001\u0000\u0006\u0000"+
		"\u0000\u0004\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}