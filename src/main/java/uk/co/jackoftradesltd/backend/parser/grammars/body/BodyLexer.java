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

// Generated from BodyLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.body;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class BodyLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		BODY=1, SLOT=2, INTEGER=3, SLOT_NAME=4, COLON=5, COMMENT=6, INDENT=7, 
		EOL=8, STRING=9, FREE_TEXT_EOL=10;
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
			"BODY", "SLOT", "INTEGER", "SLOT_NAME", "COLON", "COMMENT", "NL", "NEWLINE", 
			"INDENT", "EOL", "STRING", "FREE_TEXT_EOL"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'body:'", "'slot:'", null, null, "':'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "BODY", "SLOT", "INTEGER", "SLOT_NAME", "COLON", "COMMENT", "INDENT", 
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


	public BodyLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "BodyLexer.g4"; }

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
		"\u0004\u0000\nx\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000\u0007"+
		"\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007"+
		"\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007"+
		"\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n"+
		"\u0007\n\u0002\u000b\u0007\u000b\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0003"+
		"\u0002*\b\u0002\u0001\u0002\u0004\u0002-\b\u0002\u000b\u0002\f\u0002."+
		"\u0001\u0003\u0004\u00032\b\u0003\u000b\u0003\f\u00033\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0005\u0005<\b"+
		"\u0005\n\u0005\f\u0005?\t\u0005\u0001\u0005\u0001\u0005\u0003\u0005C\b"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0005\u0006H\b\u0006\n\u0006"+
		"\f\u0006K\t\u0006\u0001\u0006\u0004\u0006N\b\u0006\u000b\u0006\f\u0006"+
		"O\u0001\u0006\u0003\u0006S\b\u0006\u0001\u0006\u0003\u0006V\b\u0006\u0001"+
		"\u0007\u0004\u0007Y\b\u0007\u000b\u0007\f\u0007Z\u0001\u0007\u0003\u0007"+
		"^\b\u0007\u0001\u0007\u0003\u0007a\b\u0007\u0001\b\u0001\b\u0004\be\b"+
		"\b\u000b\b\f\bf\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\n\u0004\np\b\n\u000b\n\f\nq\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0000\u0000\f\u0002\u0001\u0004\u0002\u0006\u0003\b"+
		"\u0004\n\u0005\f\u0006\u000e\u0000\u0010\u0000\u0012\u0007\u0014\b\u0016"+
		"\t\u0018\n\u0002\u0000\u0001\u0003\u0002\u0000AZ__\u0002\u0000\n\n\r\r"+
		"\u0002\u0000\t\t  \u0082\u0000\u0002\u0001\u0000\u0000\u0000\u0000\u0004"+
		"\u0001\u0000\u0000\u0000\u0000\u0006\u0001\u0000\u0000\u0000\u0000\b\u0001"+
		"\u0000\u0000\u0000\u0000\n\u0001\u0000\u0000\u0000\u0000\f\u0001\u0000"+
		"\u0000\u0000\u0000\u0012\u0001\u0000\u0000\u0000\u0000\u0014\u0001\u0000"+
		"\u0000\u0000\u0001\u0016\u0001\u0000\u0000\u0000\u0001\u0018\u0001\u0000"+
		"\u0000\u0000\u0002\u001a\u0001\u0000\u0000\u0000\u0004\"\u0001\u0000\u0000"+
		"\u0000\u0006)\u0001\u0000\u0000\u0000\b1\u0001\u0000\u0000\u0000\n5\u0001"+
		"\u0000\u0000\u0000\f9\u0001\u0000\u0000\u0000\u000eI\u0001\u0000\u0000"+
		"\u0000\u0010`\u0001\u0000\u0000\u0000\u0012b\u0001\u0000\u0000\u0000\u0014"+
		"j\u0001\u0000\u0000\u0000\u0016o\u0001\u0000\u0000\u0000\u0018s\u0001"+
		"\u0000\u0000\u0000\u001a\u001b\u0005b\u0000\u0000\u001b\u001c\u0005o\u0000"+
		"\u0000\u001c\u001d\u0005d\u0000\u0000\u001d\u001e\u0005y\u0000\u0000\u001e"+
		"\u001f\u0005:\u0000\u0000\u001f \u0001\u0000\u0000\u0000 !\u0006\u0000"+
		"\u0000\u0000!\u0003\u0001\u0000\u0000\u0000\"#\u0005s\u0000\u0000#$\u0005"+
		"l\u0000\u0000$%\u0005o\u0000\u0000%&\u0005t\u0000\u0000&\'\u0005:\u0000"+
		"\u0000\'\u0005\u0001\u0000\u0000\u0000(*\u0005-\u0000\u0000)(\u0001\u0000"+
		"\u0000\u0000)*\u0001\u0000\u0000\u0000*,\u0001\u0000\u0000\u0000+-\u0002"+
		"09\u0000,+\u0001\u0000\u0000\u0000-.\u0001\u0000\u0000\u0000.,\u0001\u0000"+
		"\u0000\u0000./\u0001\u0000\u0000\u0000/\u0007\u0001\u0000\u0000\u0000"+
		"02\u0007\u0000\u0000\u000010\u0001\u0000\u0000\u000023\u0001\u0000\u0000"+
		"\u000031\u0001\u0000\u0000\u000034\u0001\u0000\u0000\u00004\t\u0001\u0000"+
		"\u0000\u000056\u0005:\u0000\u000067\u0001\u0000\u0000\u000078\u0006\u0004"+
		"\u0000\u00008\u000b\u0001\u0000\u0000\u00009=\u0005#\u0000\u0000:<\b\u0001"+
		"\u0000\u0000;:\u0001\u0000\u0000\u0000<?\u0001\u0000\u0000\u0000=;\u0001"+
		"\u0000\u0000\u0000=>\u0001\u0000\u0000\u0000>B\u0001\u0000\u0000\u0000"+
		"?=\u0001\u0000\u0000\u0000@C\u0003\u000e\u0006\u0000AC\u0005\u0000\u0000"+
		"\u0001B@\u0001\u0000\u0000\u0000BA\u0001\u0000\u0000\u0000CD\u0001\u0000"+
		"\u0000\u0000DE\u0006\u0005\u0001\u0000E\r\u0001\u0000\u0000\u0000FH\u0007"+
		"\u0002\u0000\u0000GF\u0001\u0000\u0000\u0000HK\u0001\u0000\u0000\u0000"+
		"IG\u0001\u0000\u0000\u0000IJ\u0001\u0000\u0000\u0000JU\u0001\u0000\u0000"+
		"\u0000KI\u0001\u0000\u0000\u0000LN\u0005\r\u0000\u0000ML\u0001\u0000\u0000"+
		"\u0000NO\u0001\u0000\u0000\u0000OM\u0001\u0000\u0000\u0000OP\u0001\u0000"+
		"\u0000\u0000PR\u0001\u0000\u0000\u0000QS\u0005\n\u0000\u0000RQ\u0001\u0000"+
		"\u0000\u0000RS\u0001\u0000\u0000\u0000SV\u0001\u0000\u0000\u0000TV\u0005"+
		"\n\u0000\u0000UM\u0001\u0000\u0000\u0000UT\u0001\u0000\u0000\u0000V\u000f"+
		"\u0001\u0000\u0000\u0000WY\u0005\r\u0000\u0000XW\u0001\u0000\u0000\u0000"+
		"YZ\u0001\u0000\u0000\u0000ZX\u0001\u0000\u0000\u0000Z[\u0001\u0000\u0000"+
		"\u0000[]\u0001\u0000\u0000\u0000\\^\u0005\n\u0000\u0000]\\\u0001\u0000"+
		"\u0000\u0000]^\u0001\u0000\u0000\u0000^a\u0001\u0000\u0000\u0000_a\u0005"+
		"\n\u0000\u0000`X\u0001\u0000\u0000\u0000`_\u0001\u0000\u0000\u0000a\u0011"+
		"\u0001\u0000\u0000\u0000bd\u0004\b\u0000\u0000ce\u0007\u0002\u0000\u0000"+
		"dc\u0001\u0000\u0000\u0000ef\u0001\u0000\u0000\u0000fd\u0001\u0000\u0000"+
		"\u0000fg\u0001\u0000\u0000\u0000gh\u0001\u0000\u0000\u0000hi\u0006\b\u0001"+
		"\u0000i\u0013\u0001\u0000\u0000\u0000jk\u0003\u000e\u0006\u0000kl\u0001"+
		"\u0000\u0000\u0000lm\u0006\t\u0001\u0000m\u0015\u0001\u0000\u0000\u0000"+
		"np\b\u0001\u0000\u0000on\u0001\u0000\u0000\u0000pq\u0001\u0000\u0000\u0000"+
		"qo\u0001\u0000\u0000\u0000qr\u0001\u0000\u0000\u0000r\u0017\u0001\u0000"+
		"\u0000\u0000st\u0003\u000e\u0006\u0000tu\u0001\u0000\u0000\u0000uv\u0006"+
		"\u000b\u0001\u0000vw\u0006\u000b\u0002\u0000w\u0019\u0001\u0000\u0000"+
		"\u0000\u0010\u0000\u0001).3=BIORUZ]`fq\u0003\u0005\u0001\u0000\u0006\u0000"+
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