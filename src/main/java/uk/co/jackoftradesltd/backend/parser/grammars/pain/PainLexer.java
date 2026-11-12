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

// Generated from PainLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.pain;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class PainLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		TYPE=1, MESSAGE=2, UINT=3, COMMENT=4, INDENT=5, EOL=6, STRING=7;
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
			"TYPE", "MESSAGE", "UINT", "COMMENT", "NL", "NEWLINE", "INDENT", "EOL", 
			"STRING"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'type:'", "'message:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "TYPE", "MESSAGE", "UINT", "COMMENT", "INDENT", "EOL", "STRING"
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


	public PainLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "PainLexer.g4"; }

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
		"\u0004\u0000\u0007u\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000\u0007"+
		"\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007"+
		"\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007"+
		"\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0003\u0002\'\b\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002-\b\u0002"+
		"\u0001\u0002\u0004\u00020\b\u0002\u000b\u0002\f\u00021\u0001\u0002\u0005"+
		"\u00025\b\u0002\n\u0002\f\u00028\t\u0002\u0001\u0003\u0001\u0003\u0005"+
		"\u0003<\b\u0003\n\u0003\f\u0003?\t\u0003\u0001\u0003\u0001\u0003\u0003"+
		"\u0003C\b\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0005\u0004H\b\u0004"+
		"\n\u0004\f\u0004K\t\u0004\u0001\u0004\u0004\u0004N\b\u0004\u000b\u0004"+
		"\f\u0004O\u0001\u0004\u0003\u0004S\b\u0004\u0001\u0004\u0003\u0004V\b"+
		"\u0004\u0001\u0005\u0004\u0005Y\b\u0005\u000b\u0005\f\u0005Z\u0001\u0005"+
		"\u0003\u0005^\b\u0005\u0001\u0005\u0003\u0005a\b\u0005\u0001\u0006\u0001"+
		"\u0006\u0004\u0006e\b\u0006\u000b\u0006\f\u0006f\u0001\u0006\u0001\u0006"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0004\bp\b\b"+
		"\u000b\b\f\bq\u0001\b\u0001\b\u0000\u0000\t\u0002\u0001\u0004\u0002\u0006"+
		"\u0003\b\u0004\n\u0000\f\u0000\u000e\u0005\u0010\u0006\u0012\u0007\u0002"+
		"\u0000\u0001\u0005\u0002\u0000++--\u0003\u000009AFaf\u0003\u0000\n\n\r"+
		"\r::\u0002\u0000\n\n\r\r\u0002\u0000\t\t  \u0081\u0000\u0002\u0001\u0000"+
		"\u0000\u0000\u0000\u0004\u0001\u0000\u0000\u0000\u0000\u0006\u0001\u0000"+
		"\u0000\u0000\u0000\b\u0001\u0000\u0000\u0000\u0000\u000e\u0001\u0000\u0000"+
		"\u0000\u0000\u0010\u0001\u0000\u0000\u0000\u0001\u0012\u0001\u0000\u0000"+
		"\u0000\u0002\u0014\u0001\u0000\u0000\u0000\u0004\u001a\u0001\u0000\u0000"+
		"\u0000\u0006&\u0001\u0000\u0000\u0000\b9\u0001\u0000\u0000\u0000\nI\u0001"+
		"\u0000\u0000\u0000\f`\u0001\u0000\u0000\u0000\u000eb\u0001\u0000\u0000"+
		"\u0000\u0010j\u0001\u0000\u0000\u0000\u0012o\u0001\u0000\u0000\u0000\u0014"+
		"\u0015\u0005t\u0000\u0000\u0015\u0016\u0005y\u0000\u0000\u0016\u0017\u0005"+
		"p\u0000\u0000\u0017\u0018\u0005e\u0000\u0000\u0018\u0019\u0005:\u0000"+
		"\u0000\u0019\u0003\u0001\u0000\u0000\u0000\u001a\u001b\u0005m\u0000\u0000"+
		"\u001b\u001c\u0005e\u0000\u0000\u001c\u001d\u0005s\u0000\u0000\u001d\u001e"+
		"\u0005s\u0000\u0000\u001e\u001f\u0005a\u0000\u0000\u001f \u0005g\u0000"+
		"\u0000 !\u0005e\u0000\u0000!\"\u0005:\u0000\u0000\"#\u0001\u0000\u0000"+
		"\u0000#$\u0006\u0001\u0000\u0000$\u0005\u0001\u0000\u0000\u0000%\'\u0007"+
		"\u0000\u0000\u0000&%\u0001\u0000\u0000\u0000&\'\u0001\u0000\u0000\u0000"+
		"\',\u0001\u0000\u0000\u0000()\u00050\u0000\u0000)-\u0005x\u0000\u0000"+
		"*+\u00050\u0000\u0000+-\u0005X\u0000\u0000,(\u0001\u0000\u0000\u0000,"+
		"*\u0001\u0000\u0000\u0000,-\u0001\u0000\u0000\u0000-/\u0001\u0000\u0000"+
		"\u0000.0\u0007\u0001\u0000\u0000/.\u0001\u0000\u0000\u000001\u0001\u0000"+
		"\u0000\u00001/\u0001\u0000\u0000\u000012\u0001\u0000\u0000\u000026\u0001"+
		"\u0000\u0000\u000035\b\u0002\u0000\u000043\u0001\u0000\u0000\u000058\u0001"+
		"\u0000\u0000\u000064\u0001\u0000\u0000\u000067\u0001\u0000\u0000\u0000"+
		"7\u0007\u0001\u0000\u0000\u000086\u0001\u0000\u0000\u00009=\u0005#\u0000"+
		"\u0000:<\b\u0003\u0000\u0000;:\u0001\u0000\u0000\u0000<?\u0001\u0000\u0000"+
		"\u0000=;\u0001\u0000\u0000\u0000=>\u0001\u0000\u0000\u0000>B\u0001\u0000"+
		"\u0000\u0000?=\u0001\u0000\u0000\u0000@C\u0003\n\u0004\u0000AC\u0005\u0000"+
		"\u0000\u0001B@\u0001\u0000\u0000\u0000BA\u0001\u0000\u0000\u0000CD\u0001"+
		"\u0000\u0000\u0000DE\u0006\u0003\u0001\u0000E\t\u0001\u0000\u0000\u0000"+
		"FH\u0007\u0004\u0000\u0000GF\u0001\u0000\u0000\u0000HK\u0001\u0000\u0000"+
		"\u0000IG\u0001\u0000\u0000\u0000IJ\u0001\u0000\u0000\u0000JU\u0001\u0000"+
		"\u0000\u0000KI\u0001\u0000\u0000\u0000LN\u0005\r\u0000\u0000ML\u0001\u0000"+
		"\u0000\u0000NO\u0001\u0000\u0000\u0000OM\u0001\u0000\u0000\u0000OP\u0001"+
		"\u0000\u0000\u0000PR\u0001\u0000\u0000\u0000QS\u0005\n\u0000\u0000RQ\u0001"+
		"\u0000\u0000\u0000RS\u0001\u0000\u0000\u0000SV\u0001\u0000\u0000\u0000"+
		"TV\u0005\n\u0000\u0000UM\u0001\u0000\u0000\u0000UT\u0001\u0000\u0000\u0000"+
		"V\u000b\u0001\u0000\u0000\u0000WY\u0005\r\u0000\u0000XW\u0001\u0000\u0000"+
		"\u0000YZ\u0001\u0000\u0000\u0000ZX\u0001\u0000\u0000\u0000Z[\u0001\u0000"+
		"\u0000\u0000[]\u0001\u0000\u0000\u0000\\^\u0005\n\u0000\u0000]\\\u0001"+
		"\u0000\u0000\u0000]^\u0001\u0000\u0000\u0000^a\u0001\u0000\u0000\u0000"+
		"_a\u0005\n\u0000\u0000`X\u0001\u0000\u0000\u0000`_\u0001\u0000\u0000\u0000"+
		"a\r\u0001\u0000\u0000\u0000bd\u0004\u0006\u0000\u0000ce\u0007\u0004\u0000"+
		"\u0000dc\u0001\u0000\u0000\u0000ef\u0001\u0000\u0000\u0000fd\u0001\u0000"+
		"\u0000\u0000fg\u0001\u0000\u0000\u0000gh\u0001\u0000\u0000\u0000hi\u0006"+
		"\u0006\u0001\u0000i\u000f\u0001\u0000\u0000\u0000jk\u0003\n\u0004\u0000"+
		"kl\u0001\u0000\u0000\u0000lm\u0006\u0007\u0001\u0000m\u0011\u0001\u0000"+
		"\u0000\u0000np\b\u0003\u0000\u0000on\u0001\u0000\u0000\u0000pq\u0001\u0000"+
		"\u0000\u0000qo\u0001\u0000\u0000\u0000qr\u0001\u0000\u0000\u0000rs\u0001"+
		"\u0000\u0000\u0000st\u0006\b\u0002\u0000t\u0013\u0001\u0000\u0000\u0000"+
		"\u0011\u0000\u0001&,16=BIORUZ]`fq\u0003\u0005\u0001\u0000\u0006\u0000"+
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