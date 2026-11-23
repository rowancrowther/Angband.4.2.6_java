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

// Generated from RealmLexer.g4 by ANTLR 4.13.2
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
		INDENT=8, EOL=9, COLON=10, STRING=11, FREE_TEXT_EOL=12;
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
			"NL", "NEWLINE", "INDENT", "EOL", "COLON", "STRING", "FREE_TEXT_EOL"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'stat:'", "'verb:'", "'spell-noun:'", "'book-noun:'", 
			null, null, null, null, "':'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "STAT", "VERB", "SPELL_NOUN", "BOOK_NOUN", "INTEGER", "COMMENT", 
			"INDENT", "EOL", "COLON", "STRING", "FREE_TEXT_EOL"
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

	@Override
	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 9:
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
		"\u0004\u0000\f\u00a2\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000\u0007"+
		"\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007"+
		"\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007"+
		"\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n"+
		"\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0003\u0005S\b\u0005\u0001"+
		"\u0005\u0004\u0005V\b\u0005\u000b\u0005\f\u0005W\u0001\u0006\u0001\u0006"+
		"\u0005\u0006\\\b\u0006\n\u0006\f\u0006_\t\u0006\u0001\u0006\u0001\u0006"+
		"\u0003\u0006c\b\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0005\u0007"+
		"h\b\u0007\n\u0007\f\u0007k\t\u0007\u0001\u0007\u0004\u0007n\b\u0007\u000b"+
		"\u0007\f\u0007o\u0001\u0007\u0003\u0007s\b\u0007\u0001\u0007\u0003\u0007"+
		"v\b\u0007\u0001\u0007\u0004\u0007y\b\u0007\u000b\u0007\f\u0007z\u0001"+
		"\u0007\u0003\u0007~\b\u0007\u0001\b\u0004\b\u0081\b\b\u000b\b\f\b\u0082"+
		"\u0001\b\u0003\b\u0086\b\b\u0001\b\u0003\b\u0089\b\b\u0001\t\u0001\t\u0004"+
		"\t\u008d\b\t\u000b\t\f\t\u008e\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n"+
		"\u0001\n\u0001\u000b\u0001\u000b\u0001\f\u0004\f\u009a\b\f\u000b\f\f\f"+
		"\u009b\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0000\u0000\u000e\u0002"+
		"\u0001\u0004\u0002\u0006\u0003\b\u0004\n\u0005\f\u0006\u000e\u0007\u0010"+
		"\u0000\u0012\u0000\u0014\b\u0016\t\u0018\n\u001a\u000b\u001c\f\u0002\u0000"+
		"\u0001\u0002\u0002\u0000\n\n\r\r\u0002\u0000\t\t  \u00ad\u0000\u0002\u0001"+
		"\u0000\u0000\u0000\u0000\u0004\u0001\u0000\u0000\u0000\u0000\u0006\u0001"+
		"\u0000\u0000\u0000\u0000\b\u0001\u0000\u0000\u0000\u0000\n\u0001\u0000"+
		"\u0000\u0000\u0000\f\u0001\u0000\u0000\u0000\u0000\u000e\u0001\u0000\u0000"+
		"\u0000\u0000\u0014\u0001\u0000\u0000\u0000\u0000\u0016\u0001\u0000\u0000"+
		"\u0000\u0001\u0018\u0001\u0000\u0000\u0000\u0001\u001a\u0001\u0000\u0000"+
		"\u0000\u0001\u001c\u0001\u0000\u0000\u0000\u0002\u001e\u0001\u0000\u0000"+
		"\u0000\u0004&\u0001\u0000\u0000\u0000\u0006.\u0001\u0000\u0000\u0000\b"+
		"6\u0001\u0000\u0000\u0000\nD\u0001\u0000\u0000\u0000\fR\u0001\u0000\u0000"+
		"\u0000\u000eY\u0001\u0000\u0000\u0000\u0010}\u0001\u0000\u0000\u0000\u0012"+
		"\u0088\u0001\u0000\u0000\u0000\u0014\u008a\u0001\u0000\u0000\u0000\u0016"+
		"\u0092\u0001\u0000\u0000\u0000\u0018\u0096\u0001\u0000\u0000\u0000\u001a"+
		"\u0099\u0001\u0000\u0000\u0000\u001c\u009d\u0001\u0000\u0000\u0000\u001e"+
		"\u001f\u0005n\u0000\u0000\u001f \u0005a\u0000\u0000 !\u0005m\u0000\u0000"+
		"!\"\u0005e\u0000\u0000\"#\u0005:\u0000\u0000#$\u0001\u0000\u0000\u0000"+
		"$%\u0006\u0000\u0000\u0000%\u0003\u0001\u0000\u0000\u0000&\'\u0005s\u0000"+
		"\u0000\'(\u0005t\u0000\u0000()\u0005a\u0000\u0000)*\u0005t\u0000\u0000"+
		"*+\u0005:\u0000\u0000+,\u0001\u0000\u0000\u0000,-\u0006\u0001\u0000\u0000"+
		"-\u0005\u0001\u0000\u0000\u0000./\u0005v\u0000\u0000/0\u0005e\u0000\u0000"+
		"01\u0005r\u0000\u000012\u0005b\u0000\u000023\u0005:\u0000\u000034\u0001"+
		"\u0000\u0000\u000045\u0006\u0002\u0000\u00005\u0007\u0001\u0000\u0000"+
		"\u000067\u0005s\u0000\u000078\u0005p\u0000\u000089\u0005e\u0000\u0000"+
		"9:\u0005l\u0000\u0000:;\u0005l\u0000\u0000;<\u0005-\u0000\u0000<=\u0005"+
		"n\u0000\u0000=>\u0005o\u0000\u0000>?\u0005u\u0000\u0000?@\u0005n\u0000"+
		"\u0000@A\u0005:\u0000\u0000AB\u0001\u0000\u0000\u0000BC\u0006\u0003\u0000"+
		"\u0000C\t\u0001\u0000\u0000\u0000DE\u0005b\u0000\u0000EF\u0005o\u0000"+
		"\u0000FG\u0005o\u0000\u0000GH\u0005k\u0000\u0000HI\u0005-\u0000\u0000"+
		"IJ\u0005n\u0000\u0000JK\u0005o\u0000\u0000KL\u0005u\u0000\u0000LM\u0005"+
		"n\u0000\u0000MN\u0005:\u0000\u0000NO\u0001\u0000\u0000\u0000OP\u0006\u0004"+
		"\u0000\u0000P\u000b\u0001\u0000\u0000\u0000QS\u0005-\u0000\u0000RQ\u0001"+
		"\u0000\u0000\u0000RS\u0001\u0000\u0000\u0000SU\u0001\u0000\u0000\u0000"+
		"TV\u000209\u0000UT\u0001\u0000\u0000\u0000VW\u0001\u0000\u0000\u0000W"+
		"U\u0001\u0000\u0000\u0000WX\u0001\u0000\u0000\u0000X\r\u0001\u0000\u0000"+
		"\u0000Y]\u0005#\u0000\u0000Z\\\b\u0000\u0000\u0000[Z\u0001\u0000\u0000"+
		"\u0000\\_\u0001\u0000\u0000\u0000][\u0001\u0000\u0000\u0000]^\u0001\u0000"+
		"\u0000\u0000^b\u0001\u0000\u0000\u0000_]\u0001\u0000\u0000\u0000`c\u0003"+
		"\u0010\u0007\u0000ac\u0005\u0000\u0000\u0001b`\u0001\u0000\u0000\u0000"+
		"ba\u0001\u0000\u0000\u0000cd\u0001\u0000\u0000\u0000de\u0006\u0006\u0001"+
		"\u0000e\u000f\u0001\u0000\u0000\u0000fh\u0007\u0001\u0000\u0000gf\u0001"+
		"\u0000\u0000\u0000hk\u0001\u0000\u0000\u0000ig\u0001\u0000\u0000\u0000"+
		"ij\u0001\u0000\u0000\u0000ju\u0001\u0000\u0000\u0000ki\u0001\u0000\u0000"+
		"\u0000ln\u0005\r\u0000\u0000ml\u0001\u0000\u0000\u0000no\u0001\u0000\u0000"+
		"\u0000om\u0001\u0000\u0000\u0000op\u0001\u0000\u0000\u0000pr\u0001\u0000"+
		"\u0000\u0000qs\u0005\n\u0000\u0000rq\u0001\u0000\u0000\u0000rs\u0001\u0000"+
		"\u0000\u0000sv\u0001\u0000\u0000\u0000tv\u0005\n\u0000\u0000um\u0001\u0000"+
		"\u0000\u0000ut\u0001\u0000\u0000\u0000v~\u0001\u0000\u0000\u0000wy\u0007"+
		"\u0001\u0000\u0000xw\u0001\u0000\u0000\u0000yz\u0001\u0000\u0000\u0000"+
		"zx\u0001\u0000\u0000\u0000z{\u0001\u0000\u0000\u0000{|\u0001\u0000\u0000"+
		"\u0000|~\u0005\u0000\u0000\u0001}i\u0001\u0000\u0000\u0000}x\u0001\u0000"+
		"\u0000\u0000~\u0011\u0001\u0000\u0000\u0000\u007f\u0081\u0005\r\u0000"+
		"\u0000\u0080\u007f\u0001\u0000\u0000\u0000\u0081\u0082\u0001\u0000\u0000"+
		"\u0000\u0082\u0080\u0001\u0000\u0000\u0000\u0082\u0083\u0001\u0000\u0000"+
		"\u0000\u0083\u0085\u0001\u0000\u0000\u0000\u0084\u0086\u0005\n\u0000\u0000"+
		"\u0085\u0084\u0001\u0000\u0000\u0000\u0085\u0086\u0001\u0000\u0000\u0000"+
		"\u0086\u0089\u0001\u0000\u0000\u0000\u0087\u0089\u0005\n\u0000\u0000\u0088"+
		"\u0080\u0001\u0000\u0000\u0000\u0088\u0087\u0001\u0000\u0000\u0000\u0089"+
		"\u0013\u0001\u0000\u0000\u0000\u008a\u008c\u0004\t\u0000\u0000\u008b\u008d"+
		"\u0007\u0001\u0000\u0000\u008c\u008b\u0001\u0000\u0000\u0000\u008d\u008e"+
		"\u0001\u0000\u0000\u0000\u008e\u008c\u0001\u0000\u0000\u0000\u008e\u008f"+
		"\u0001\u0000\u0000\u0000\u008f\u0090\u0001\u0000\u0000\u0000\u0090\u0091"+
		"\u0006\t\u0001\u0000\u0091\u0015\u0001\u0000\u0000\u0000\u0092\u0093\u0003"+
		"\u0010\u0007\u0000\u0093\u0094\u0001\u0000\u0000\u0000\u0094\u0095\u0006"+
		"\n\u0001\u0000\u0095\u0017\u0001\u0000\u0000\u0000\u0096\u0097\u0005:"+
		"\u0000\u0000\u0097\u0019\u0001\u0000\u0000\u0000\u0098\u009a\b\u0000\u0000"+
		"\u0000\u0099\u0098\u0001\u0000\u0000\u0000\u009a\u009b\u0001\u0000\u0000"+
		"\u0000\u009b\u0099\u0001\u0000\u0000\u0000\u009b\u009c\u0001\u0000\u0000"+
		"\u0000\u009c\u001b\u0001\u0000\u0000\u0000\u009d\u009e\u0003\u0010\u0007"+
		"\u0000\u009e\u009f\u0001\u0000\u0000\u0000\u009f\u00a0\u0006\r\u0001\u0000"+
		"\u00a0\u00a1\u0006\r\u0002\u0000\u00a1\u001d\u0001\u0000\u0000\u0000\u0011"+
		"\u0000\u0001RW]bioruz}\u0082\u0085\u0088\u008e\u009b\u0003\u0005\u0001"+
		"\u0000\u0006\u0000\u0000\u0004\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}