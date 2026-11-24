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
		"\u0004\u0000\u0007l\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001"+
		"\u0002\u0003\u0002\u001e\b\u0002\u0001\u0002\u0001\u0002\u0003\u0002\""+
		"\b\u0002\u0003\u0002$\b\u0002\u0001\u0002\u0004\u0002\'\b\u0002\u000b"+
		"\u0002\f\u0002(\u0001\u0003\u0004\u0003,\b\u0003\u000b\u0003\f\u0003-"+
		"\u0001\u0004\u0001\u0004\u0005\u00042\b\u0004\n\u0004\f\u00045\t\u0004"+
		"\u0001\u0004\u0001\u0004\u0003\u00049\b\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0005\u0005\u0005>\b\u0005\n\u0005\f\u0005A\t\u0005\u0001\u0005"+
		"\u0004\u0005D\b\u0005\u000b\u0005\f\u0005E\u0001\u0005\u0003\u0005I\b"+
		"\u0005\u0001\u0005\u0003\u0005L\b\u0005\u0001\u0005\u0004\u0005O\b\u0005"+
		"\u000b\u0005\f\u0005P\u0001\u0005\u0003\u0005T\b\u0005\u0001\u0006\u0004"+
		"\u0006W\b\u0006\u000b\u0006\f\u0006X\u0001\u0006\u0003\u0006\\\b\u0006"+
		"\u0001\u0006\u0003\u0006_\b\u0006\u0001\u0007\u0001\u0007\u0004\u0007"+
		"c\b\u0007\u000b\u0007\f\u0007d\u0001\u0007\u0001\u0007\u0001\b\u0001\b"+
		"\u0001\b\u0001\b\u0000\u0000\t\u0001\u0001\u0003\u0002\u0005\u0003\u0007"+
		"\u0004\t\u0005\u000b\u0000\r\u0000\u000f\u0006\u0011\u0007\u0001\u0000"+
		"\u0006\u0002\u0000++--\u0002\u0000XXxx\u0003\u000009AFaf\u0003\u0000\n"+
		"\n\r\r::\u0002\u0000\n\n\r\r\u0002\u0000\t\t  z\u0000\u0001\u0001\u0000"+
		"\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000"+
		"\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000"+
		"\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000"+
		"\u0000\u0001\u0013\u0001\u0000\u0000\u0000\u0003\u001a\u0001\u0000\u0000"+
		"\u0000\u0005\u001d\u0001\u0000\u0000\u0000\u0007+\u0001\u0000\u0000\u0000"+
		"\t/\u0001\u0000\u0000\u0000\u000bS\u0001\u0000\u0000\u0000\r^\u0001\u0000"+
		"\u0000\u0000\u000f`\u0001\u0000\u0000\u0000\u0011h\u0001\u0000\u0000\u0000"+
		"\u0013\u0014\u0005l\u0000\u0000\u0014\u0015\u0005e\u0000\u0000\u0015\u0016"+
		"\u0005v\u0000\u0000\u0016\u0017\u0005e\u0000\u0000\u0017\u0018\u0005l"+
		"\u0000\u0000\u0018\u0019\u0005:\u0000\u0000\u0019\u0002\u0001\u0000\u0000"+
		"\u0000\u001a\u001b\u0005:\u0000\u0000\u001b\u0004\u0001\u0000\u0000\u0000"+
		"\u001c\u001e\u0007\u0000\u0000\u0000\u001d\u001c\u0001\u0000\u0000\u0000"+
		"\u001d\u001e\u0001\u0000\u0000\u0000\u001e#\u0001\u0000\u0000\u0000\u001f"+
		"!\u00050\u0000\u0000 \"\u0007\u0001\u0000\u0000! \u0001\u0000\u0000\u0000"+
		"!\"\u0001\u0000\u0000\u0000\"$\u0001\u0000\u0000\u0000#\u001f\u0001\u0000"+
		"\u0000\u0000#$\u0001\u0000\u0000\u0000$&\u0001\u0000\u0000\u0000%\'\u0007"+
		"\u0002\u0000\u0000&%\u0001\u0000\u0000\u0000\'(\u0001\u0000\u0000\u0000"+
		"(&\u0001\u0000\u0000\u0000()\u0001\u0000\u0000\u0000)\u0006\u0001\u0000"+
		"\u0000\u0000*,\b\u0003\u0000\u0000+*\u0001\u0000\u0000\u0000,-\u0001\u0000"+
		"\u0000\u0000-+\u0001\u0000\u0000\u0000-.\u0001\u0000\u0000\u0000.\b\u0001"+
		"\u0000\u0000\u0000/3\u0005#\u0000\u000002\b\u0004\u0000\u000010\u0001"+
		"\u0000\u0000\u000025\u0001\u0000\u0000\u000031\u0001\u0000\u0000\u0000"+
		"34\u0001\u0000\u0000\u000048\u0001\u0000\u0000\u000053\u0001\u0000\u0000"+
		"\u000069\u0003\u000b\u0005\u000079\u0005\u0000\u0000\u000186\u0001\u0000"+
		"\u0000\u000087\u0001\u0000\u0000\u00009:\u0001\u0000\u0000\u0000:;\u0006"+
		"\u0004\u0000\u0000;\n\u0001\u0000\u0000\u0000<>\u0007\u0005\u0000\u0000"+
		"=<\u0001\u0000\u0000\u0000>A\u0001\u0000\u0000\u0000?=\u0001\u0000\u0000"+
		"\u0000?@\u0001\u0000\u0000\u0000@K\u0001\u0000\u0000\u0000A?\u0001\u0000"+
		"\u0000\u0000BD\u0005\r\u0000\u0000CB\u0001\u0000\u0000\u0000DE\u0001\u0000"+
		"\u0000\u0000EC\u0001\u0000\u0000\u0000EF\u0001\u0000\u0000\u0000FH\u0001"+
		"\u0000\u0000\u0000GI\u0005\n\u0000\u0000HG\u0001\u0000\u0000\u0000HI\u0001"+
		"\u0000\u0000\u0000IL\u0001\u0000\u0000\u0000JL\u0005\n\u0000\u0000KC\u0001"+
		"\u0000\u0000\u0000KJ\u0001\u0000\u0000\u0000LT\u0001\u0000\u0000\u0000"+
		"MO\u0007\u0005\u0000\u0000NM\u0001\u0000\u0000\u0000OP\u0001\u0000\u0000"+
		"\u0000PN\u0001\u0000\u0000\u0000PQ\u0001\u0000\u0000\u0000QR\u0001\u0000"+
		"\u0000\u0000RT\u0005\u0000\u0000\u0001S?\u0001\u0000\u0000\u0000SN\u0001"+
		"\u0000\u0000\u0000T\f\u0001\u0000\u0000\u0000UW\u0005\r\u0000\u0000VU"+
		"\u0001\u0000\u0000\u0000WX\u0001\u0000\u0000\u0000XV\u0001\u0000\u0000"+
		"\u0000XY\u0001\u0000\u0000\u0000Y[\u0001\u0000\u0000\u0000Z\\\u0005\n"+
		"\u0000\u0000[Z\u0001\u0000\u0000\u0000[\\\u0001\u0000\u0000\u0000\\_\u0001"+
		"\u0000\u0000\u0000]_\u0005\n\u0000\u0000^V\u0001\u0000\u0000\u0000^]\u0001"+
		"\u0000\u0000\u0000_\u000e\u0001\u0000\u0000\u0000`b\u0004\u0007\u0000"+
		"\u0000ac\u0007\u0005\u0000\u0000ba\u0001\u0000\u0000\u0000cd\u0001\u0000"+
		"\u0000\u0000db\u0001\u0000\u0000\u0000de\u0001\u0000\u0000\u0000ef\u0001"+
		"\u0000\u0000\u0000fg\u0006\u0007\u0000\u0000g\u0010\u0001\u0000\u0000"+
		"\u0000hi\u0003\u000b\u0005\u0000ij\u0001\u0000\u0000\u0000jk\u0006\b\u0000"+
		"\u0000k\u0012\u0001\u0000\u0000\u0000\u0012\u0000\u001d!#(-38?EHKPSX["+
		"^d\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}