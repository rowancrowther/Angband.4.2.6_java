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
		TYPE=1, MESSAGE=2, INTEGER=3, COMMENT=4, INDENT=5, EOL=6, STRING=7;
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
			"TYPE", "MESSAGE", "INTEGER", "COMMENT", "NL", "NEWLINE", "INDENT", "EOL", 
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
			null, "TYPE", "MESSAGE", "INTEGER", "COMMENT", "INDENT", "EOL", "STRING"
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
		"\u0004\u0000\u0007i\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000\u0007"+
		"\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007"+
		"\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007"+
		"\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0003\u0002\'\b\u0002"+
		"\u0001\u0002\u0004\u0002*\b\u0002\u000b\u0002\f\u0002+\u0001\u0003\u0001"+
		"\u0003\u0005\u00030\b\u0003\n\u0003\f\u00033\t\u0003\u0001\u0003\u0001"+
		"\u0003\u0003\u00037\b\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0005"+
		"\u0004<\b\u0004\n\u0004\f\u0004?\t\u0004\u0001\u0004\u0004\u0004B\b\u0004"+
		"\u000b\u0004\f\u0004C\u0001\u0004\u0003\u0004G\b\u0004\u0001\u0004\u0003"+
		"\u0004J\b\u0004\u0001\u0005\u0004\u0005M\b\u0005\u000b\u0005\f\u0005N"+
		"\u0001\u0005\u0003\u0005R\b\u0005\u0001\u0005\u0003\u0005U\b\u0005\u0001"+
		"\u0006\u0001\u0006\u0004\u0006Y\b\u0006\u000b\u0006\f\u0006Z\u0001\u0006"+
		"\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0004"+
		"\bd\b\b\u000b\b\f\be\u0001\b\u0001\b\u0000\u0000\t\u0002\u0001\u0004\u0002"+
		"\u0006\u0003\b\u0004\n\u0000\f\u0000\u000e\u0005\u0010\u0006\u0012\u0007"+
		"\u0002\u0000\u0001\u0002\u0002\u0000\n\n\r\r\u0002\u0000\t\t  r\u0000"+
		"\u0002\u0001\u0000\u0000\u0000\u0000\u0004\u0001\u0000\u0000\u0000\u0000"+
		"\u0006\u0001\u0000\u0000\u0000\u0000\b\u0001\u0000\u0000\u0000\u0000\u000e"+
		"\u0001\u0000\u0000\u0000\u0000\u0010\u0001\u0000\u0000\u0000\u0001\u0012"+
		"\u0001\u0000\u0000\u0000\u0002\u0014\u0001\u0000\u0000\u0000\u0004\u001a"+
		"\u0001\u0000\u0000\u0000\u0006&\u0001\u0000\u0000\u0000\b-\u0001\u0000"+
		"\u0000\u0000\n=\u0001\u0000\u0000\u0000\fT\u0001\u0000\u0000\u0000\u000e"+
		"V\u0001\u0000\u0000\u0000\u0010^\u0001\u0000\u0000\u0000\u0012c\u0001"+
		"\u0000\u0000\u0000\u0014\u0015\u0005t\u0000\u0000\u0015\u0016\u0005y\u0000"+
		"\u0000\u0016\u0017\u0005p\u0000\u0000\u0017\u0018\u0005e\u0000\u0000\u0018"+
		"\u0019\u0005:\u0000\u0000\u0019\u0003\u0001\u0000\u0000\u0000\u001a\u001b"+
		"\u0005m\u0000\u0000\u001b\u001c\u0005e\u0000\u0000\u001c\u001d\u0005s"+
		"\u0000\u0000\u001d\u001e\u0005s\u0000\u0000\u001e\u001f\u0005a\u0000\u0000"+
		"\u001f \u0005g\u0000\u0000 !\u0005e\u0000\u0000!\"\u0005:\u0000\u0000"+
		"\"#\u0001\u0000\u0000\u0000#$\u0006\u0001\u0000\u0000$\u0005\u0001\u0000"+
		"\u0000\u0000%\'\u0005-\u0000\u0000&%\u0001\u0000\u0000\u0000&\'\u0001"+
		"\u0000\u0000\u0000\')\u0001\u0000\u0000\u0000(*\u000209\u0000)(\u0001"+
		"\u0000\u0000\u0000*+\u0001\u0000\u0000\u0000+)\u0001\u0000\u0000\u0000"+
		"+,\u0001\u0000\u0000\u0000,\u0007\u0001\u0000\u0000\u0000-1\u0005#\u0000"+
		"\u0000.0\b\u0000\u0000\u0000/.\u0001\u0000\u0000\u000003\u0001\u0000\u0000"+
		"\u00001/\u0001\u0000\u0000\u000012\u0001\u0000\u0000\u000026\u0001\u0000"+
		"\u0000\u000031\u0001\u0000\u0000\u000047\u0003\n\u0004\u000057\u0005\u0000"+
		"\u0000\u000164\u0001\u0000\u0000\u000065\u0001\u0000\u0000\u000078\u0001"+
		"\u0000\u0000\u000089\u0006\u0003\u0001\u00009\t\u0001\u0000\u0000\u0000"+
		":<\u0007\u0001\u0000\u0000;:\u0001\u0000\u0000\u0000<?\u0001\u0000\u0000"+
		"\u0000=;\u0001\u0000\u0000\u0000=>\u0001\u0000\u0000\u0000>I\u0001\u0000"+
		"\u0000\u0000?=\u0001\u0000\u0000\u0000@B\u0005\r\u0000\u0000A@\u0001\u0000"+
		"\u0000\u0000BC\u0001\u0000\u0000\u0000CA\u0001\u0000\u0000\u0000CD\u0001"+
		"\u0000\u0000\u0000DF\u0001\u0000\u0000\u0000EG\u0005\n\u0000\u0000FE\u0001"+
		"\u0000\u0000\u0000FG\u0001\u0000\u0000\u0000GJ\u0001\u0000\u0000\u0000"+
		"HJ\u0005\n\u0000\u0000IA\u0001\u0000\u0000\u0000IH\u0001\u0000\u0000\u0000"+
		"J\u000b\u0001\u0000\u0000\u0000KM\u0005\r\u0000\u0000LK\u0001\u0000\u0000"+
		"\u0000MN\u0001\u0000\u0000\u0000NL\u0001\u0000\u0000\u0000NO\u0001\u0000"+
		"\u0000\u0000OQ\u0001\u0000\u0000\u0000PR\u0005\n\u0000\u0000QP\u0001\u0000"+
		"\u0000\u0000QR\u0001\u0000\u0000\u0000RU\u0001\u0000\u0000\u0000SU\u0005"+
		"\n\u0000\u0000TL\u0001\u0000\u0000\u0000TS\u0001\u0000\u0000\u0000U\r"+
		"\u0001\u0000\u0000\u0000VX\u0004\u0006\u0000\u0000WY\u0007\u0001\u0000"+
		"\u0000XW\u0001\u0000\u0000\u0000YZ\u0001\u0000\u0000\u0000ZX\u0001\u0000"+
		"\u0000\u0000Z[\u0001\u0000\u0000\u0000[\\\u0001\u0000\u0000\u0000\\]\u0006"+
		"\u0006\u0001\u0000]\u000f\u0001\u0000\u0000\u0000^_\u0003\n\u0004\u0000"+
		"_`\u0001\u0000\u0000\u0000`a\u0006\u0007\u0001\u0000a\u0011\u0001\u0000"+
		"\u0000\u0000bd\b\u0000\u0000\u0000cb\u0001\u0000\u0000\u0000de\u0001\u0000"+
		"\u0000\u0000ec\u0001\u0000\u0000\u0000ef\u0001\u0000\u0000\u0000fg\u0001"+
		"\u0000\u0000\u0000gh\u0006\b\u0002\u0000h\u0013\u0001\u0000\u0000\u0000"+
		"\u000f\u0000\u0001&+16=CFINQTZe\u0003\u0005\u0001\u0000\u0006\u0000\u0000"+
		"\u0004\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}