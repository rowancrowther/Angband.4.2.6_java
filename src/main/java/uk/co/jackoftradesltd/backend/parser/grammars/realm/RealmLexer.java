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
		INDENT=8, EOL=9, STRING=10, FREE_TEXT_EOL=11;
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
			"NL", "NEWLINE", "INDENT", "EOL", "STRING", "FREE_TEXT_EOL"
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
			"INDENT", "EOL", "STRING", "FREE_TEXT_EOL"
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
		"\u0004\u0000\u000b\u0096\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000"+
		"\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003"+
		"\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006"+
		"\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002"+
		"\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007\f\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0005\u0003\u0005Q\b\u0005\u0001\u0005\u0004\u0005"+
		"T\b\u0005\u000b\u0005\f\u0005U\u0001\u0006\u0001\u0006\u0005\u0006Z\b"+
		"\u0006\n\u0006\f\u0006]\t\u0006\u0001\u0006\u0001\u0006\u0003\u0006a\b"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0005\u0007f\b\u0007\n\u0007"+
		"\f\u0007i\t\u0007\u0001\u0007\u0004\u0007l\b\u0007\u000b\u0007\f\u0007"+
		"m\u0001\u0007\u0003\u0007q\b\u0007\u0001\u0007\u0003\u0007t\b\u0007\u0001"+
		"\b\u0004\bw\b\b\u000b\b\f\bx\u0001\b\u0003\b|\b\b\u0001\b\u0003\b\u007f"+
		"\b\b\u0001\t\u0001\t\u0004\t\u0083\b\t\u000b\t\f\t\u0084\u0001\t\u0001"+
		"\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0004\u000b\u008e\b\u000b"+
		"\u000b\u000b\f\u000b\u008f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0000"+
		"\u0000\r\u0002\u0001\u0004\u0002\u0006\u0003\b\u0004\n\u0005\f\u0006\u000e"+
		"\u0007\u0010\u0000\u0012\u0000\u0014\b\u0016\t\u0018\n\u001a\u000b\u0002"+
		"\u0000\u0001\u0002\u0002\u0000\n\n\r\r\u0002\u0000\t\t  \u009f\u0000\u0002"+
		"\u0001\u0000\u0000\u0000\u0000\u0004\u0001\u0000\u0000\u0000\u0000\u0006"+
		"\u0001\u0000\u0000\u0000\u0000\b\u0001\u0000\u0000\u0000\u0000\n\u0001"+
		"\u0000\u0000\u0000\u0000\f\u0001\u0000\u0000\u0000\u0000\u000e\u0001\u0000"+
		"\u0000\u0000\u0000\u0014\u0001\u0000\u0000\u0000\u0000\u0016\u0001\u0000"+
		"\u0000\u0000\u0001\u0018\u0001\u0000\u0000\u0000\u0001\u001a\u0001\u0000"+
		"\u0000\u0000\u0002\u001c\u0001\u0000\u0000\u0000\u0004$\u0001\u0000\u0000"+
		"\u0000\u0006,\u0001\u0000\u0000\u0000\b4\u0001\u0000\u0000\u0000\nB\u0001"+
		"\u0000\u0000\u0000\fP\u0001\u0000\u0000\u0000\u000eW\u0001\u0000\u0000"+
		"\u0000\u0010g\u0001\u0000\u0000\u0000\u0012~\u0001\u0000\u0000\u0000\u0014"+
		"\u0080\u0001\u0000\u0000\u0000\u0016\u0088\u0001\u0000\u0000\u0000\u0018"+
		"\u008d\u0001\u0000\u0000\u0000\u001a\u0091\u0001\u0000\u0000\u0000\u001c"+
		"\u001d\u0005n\u0000\u0000\u001d\u001e\u0005a\u0000\u0000\u001e\u001f\u0005"+
		"m\u0000\u0000\u001f \u0005e\u0000\u0000 !\u0005:\u0000\u0000!\"\u0001"+
		"\u0000\u0000\u0000\"#\u0006\u0000\u0000\u0000#\u0003\u0001\u0000\u0000"+
		"\u0000$%\u0005s\u0000\u0000%&\u0005t\u0000\u0000&\'\u0005a\u0000\u0000"+
		"\'(\u0005t\u0000\u0000()\u0005:\u0000\u0000)*\u0001\u0000\u0000\u0000"+
		"*+\u0006\u0001\u0000\u0000+\u0005\u0001\u0000\u0000\u0000,-\u0005v\u0000"+
		"\u0000-.\u0005e\u0000\u0000./\u0005r\u0000\u0000/0\u0005b\u0000\u0000"+
		"01\u0005:\u0000\u000012\u0001\u0000\u0000\u000023\u0006\u0002\u0000\u0000"+
		"3\u0007\u0001\u0000\u0000\u000045\u0005s\u0000\u000056\u0005p\u0000\u0000"+
		"67\u0005e\u0000\u000078\u0005l\u0000\u000089\u0005l\u0000\u00009:\u0005"+
		"-\u0000\u0000:;\u0005n\u0000\u0000;<\u0005o\u0000\u0000<=\u0005u\u0000"+
		"\u0000=>\u0005n\u0000\u0000>?\u0005:\u0000\u0000?@\u0001\u0000\u0000\u0000"+
		"@A\u0006\u0003\u0000\u0000A\t\u0001\u0000\u0000\u0000BC\u0005b\u0000\u0000"+
		"CD\u0005o\u0000\u0000DE\u0005o\u0000\u0000EF\u0005k\u0000\u0000FG\u0005"+
		"-\u0000\u0000GH\u0005n\u0000\u0000HI\u0005o\u0000\u0000IJ\u0005u\u0000"+
		"\u0000JK\u0005n\u0000\u0000KL\u0005:\u0000\u0000LM\u0001\u0000\u0000\u0000"+
		"MN\u0006\u0004\u0000\u0000N\u000b\u0001\u0000\u0000\u0000OQ\u0005-\u0000"+
		"\u0000PO\u0001\u0000\u0000\u0000PQ\u0001\u0000\u0000\u0000QS\u0001\u0000"+
		"\u0000\u0000RT\u000209\u0000SR\u0001\u0000\u0000\u0000TU\u0001\u0000\u0000"+
		"\u0000US\u0001\u0000\u0000\u0000UV\u0001\u0000\u0000\u0000V\r\u0001\u0000"+
		"\u0000\u0000W[\u0005#\u0000\u0000XZ\b\u0000\u0000\u0000YX\u0001\u0000"+
		"\u0000\u0000Z]\u0001\u0000\u0000\u0000[Y\u0001\u0000\u0000\u0000[\\\u0001"+
		"\u0000\u0000\u0000\\`\u0001\u0000\u0000\u0000][\u0001\u0000\u0000\u0000"+
		"^a\u0003\u0010\u0007\u0000_a\u0005\u0000\u0000\u0001`^\u0001\u0000\u0000"+
		"\u0000`_\u0001\u0000\u0000\u0000ab\u0001\u0000\u0000\u0000bc\u0006\u0006"+
		"\u0001\u0000c\u000f\u0001\u0000\u0000\u0000df\u0007\u0001\u0000\u0000"+
		"ed\u0001\u0000\u0000\u0000fi\u0001\u0000\u0000\u0000ge\u0001\u0000\u0000"+
		"\u0000gh\u0001\u0000\u0000\u0000hs\u0001\u0000\u0000\u0000ig\u0001\u0000"+
		"\u0000\u0000jl\u0005\r\u0000\u0000kj\u0001\u0000\u0000\u0000lm\u0001\u0000"+
		"\u0000\u0000mk\u0001\u0000\u0000\u0000mn\u0001\u0000\u0000\u0000np\u0001"+
		"\u0000\u0000\u0000oq\u0005\n\u0000\u0000po\u0001\u0000\u0000\u0000pq\u0001"+
		"\u0000\u0000\u0000qt\u0001\u0000\u0000\u0000rt\u0005\n\u0000\u0000sk\u0001"+
		"\u0000\u0000\u0000sr\u0001\u0000\u0000\u0000t\u0011\u0001\u0000\u0000"+
		"\u0000uw\u0005\r\u0000\u0000vu\u0001\u0000\u0000\u0000wx\u0001\u0000\u0000"+
		"\u0000xv\u0001\u0000\u0000\u0000xy\u0001\u0000\u0000\u0000y{\u0001\u0000"+
		"\u0000\u0000z|\u0005\n\u0000\u0000{z\u0001\u0000\u0000\u0000{|\u0001\u0000"+
		"\u0000\u0000|\u007f\u0001\u0000\u0000\u0000}\u007f\u0005\n\u0000\u0000"+
		"~v\u0001\u0000\u0000\u0000~}\u0001\u0000\u0000\u0000\u007f\u0013\u0001"+
		"\u0000\u0000\u0000\u0080\u0082\u0004\t\u0000\u0000\u0081\u0083\u0007\u0001"+
		"\u0000\u0000\u0082\u0081\u0001\u0000\u0000\u0000\u0083\u0084\u0001\u0000"+
		"\u0000\u0000\u0084\u0082\u0001\u0000\u0000\u0000\u0084\u0085\u0001\u0000"+
		"\u0000\u0000\u0085\u0086\u0001\u0000\u0000\u0000\u0086\u0087\u0006\t\u0001"+
		"\u0000\u0087\u0015\u0001\u0000\u0000\u0000\u0088\u0089\u0003\u0010\u0007"+
		"\u0000\u0089\u008a\u0001\u0000\u0000\u0000\u008a\u008b\u0006\n\u0001\u0000"+
		"\u008b\u0017\u0001\u0000\u0000\u0000\u008c\u008e\b\u0000\u0000\u0000\u008d"+
		"\u008c\u0001\u0000\u0000\u0000\u008e\u008f\u0001\u0000\u0000\u0000\u008f"+
		"\u008d\u0001\u0000\u0000\u0000\u008f\u0090\u0001\u0000\u0000\u0000\u0090"+
		"\u0019\u0001\u0000\u0000\u0000\u0091\u0092\u0003\u0010\u0007\u0000\u0092"+
		"\u0093\u0001\u0000\u0000\u0000\u0093\u0094\u0006\f\u0001\u0000\u0094\u0095"+
		"\u0006\f\u0002\u0000\u0095\u001b\u0001\u0000\u0000\u0000\u000f\u0000\u0001"+
		"PU[`gmpsx{~\u0084\u008f\u0003\u0005\u0001\u0000\u0006\u0000\u0000\u0004"+
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