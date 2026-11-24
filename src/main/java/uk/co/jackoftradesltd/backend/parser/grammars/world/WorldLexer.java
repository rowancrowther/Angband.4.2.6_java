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
		COMMENT=1, LEVEL=2, COLON=3, INTEGER=4, NAME=5, COLON_TO_END=6, INDENT=7, 
		EOL=8;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"COMMENT", "LEVEL", "COLON", "INTEGER", "NAME", "COLON_TO_END", "NL", 
			"NEWLINE", "INDENT", "EOL"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "COMMENT", "LEVEL", "COLON", "INTEGER", "NAME", "COLON_TO_END", 
			"INDENT", "EOL"
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


	    private int colons = 0;


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
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 1:
			LEVEL_action((RuleContext)_localctx, actionIndex);
			break;
		case 2:
			COLON_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void LEVEL_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:
			 colons = 0; 
			break;
		}
	}
	private void COLON_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 1:
			 colons++; 
			break;
		}
	}
	@Override
	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 2:
			return COLON_sempred((RuleContext)_localctx, predIndex);
		case 5:
			return COLON_TO_END_sempred((RuleContext)_localctx, predIndex);
		case 8:
			return INDENT_sempred((RuleContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean COLON_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return colons < 3;
		}
		return true;
	}
	private boolean COLON_TO_END_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return colons == 3;
		}
		return true;
	}
	private boolean INDENT_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return  getCharPositionInLine() == 0 ;
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0000\b\u0080\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0001\u0000\u0003\u0000\u0017"+
		"\b\u0000\u0001\u0000\u0001\u0000\u0005\u0000\u001b\b\u0000\n\u0000\f\u0000"+
		"\u001e\t\u0000\u0001\u0000\u0001\u0000\u0003\u0000\"\b\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0001\u0003\u0001\'\b\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003"+
		"\u0003\u00037\b\u0003\u0001\u0003\u0001\u0003\u0003\u0003;\b\u0003\u0003"+
		"\u0003=\b\u0003\u0001\u0003\u0004\u0003@\b\u0003\u000b\u0003\f\u0003A"+
		"\u0001\u0004\u0004\u0004E\b\u0004\u000b\u0004\f\u0004F\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0005\u0005L\b\u0005\n\u0005\f\u0005O\t\u0005\u0001"+
		"\u0006\u0005\u0006R\b\u0006\n\u0006\f\u0006U\t\u0006\u0001\u0006\u0004"+
		"\u0006X\b\u0006\u000b\u0006\f\u0006Y\u0001\u0006\u0003\u0006]\b\u0006"+
		"\u0001\u0006\u0003\u0006`\b\u0006\u0001\u0006\u0004\u0006c\b\u0006\u000b"+
		"\u0006\f\u0006d\u0001\u0006\u0003\u0006h\b\u0006\u0001\u0007\u0004\u0007"+
		"k\b\u0007\u000b\u0007\f\u0007l\u0001\u0007\u0003\u0007p\b\u0007\u0001"+
		"\u0007\u0003\u0007s\b\u0007\u0001\b\u0001\b\u0004\bw\b\b\u000b\b\f\bx"+
		"\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0000\u0000\n\u0001\u0001"+
		"\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0000\u000f"+
		"\u0000\u0011\u0007\u0013\b\u0001\u0000\u0006\u0002\u0000\n\n\r\r\u0002"+
		"\u0000++--\u0002\u0000XXxx\u0003\u000009AFaf\u0003\u0000\n\n\r\r::\u0002"+
		"\u0000\t\t  \u0091\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001"+
		"\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001"+
		"\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000"+
		"\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000"+
		"\u0000\u0000\u0001\u0016\u0001\u0000\u0000\u0000\u0003&\u0001\u0000\u0000"+
		"\u0000\u00051\u0001\u0000\u0000\u0000\u00076\u0001\u0000\u0000\u0000\t"+
		"D\u0001\u0000\u0000\u0000\u000bH\u0001\u0000\u0000\u0000\rg\u0001\u0000"+
		"\u0000\u0000\u000fr\u0001\u0000\u0000\u0000\u0011t\u0001\u0000\u0000\u0000"+
		"\u0013|\u0001\u0000\u0000\u0000\u0015\u0017\u0003\u0011\b\u0000\u0016"+
		"\u0015\u0001\u0000\u0000\u0000\u0016\u0017\u0001\u0000\u0000\u0000\u0017"+
		"\u0018\u0001\u0000\u0000\u0000\u0018\u001c\u0005#\u0000\u0000\u0019\u001b"+
		"\b\u0000\u0000\u0000\u001a\u0019\u0001\u0000\u0000\u0000\u001b\u001e\u0001"+
		"\u0000\u0000\u0000\u001c\u001a\u0001\u0000\u0000\u0000\u001c\u001d\u0001"+
		"\u0000\u0000\u0000\u001d!\u0001\u0000\u0000\u0000\u001e\u001c\u0001\u0000"+
		"\u0000\u0000\u001f\"\u0003\r\u0006\u0000 \"\u0005\u0000\u0000\u0001!\u001f"+
		"\u0001\u0000\u0000\u0000! \u0001\u0000\u0000\u0000\"#\u0001\u0000\u0000"+
		"\u0000#$\u0006\u0000\u0000\u0000$\u0002\u0001\u0000\u0000\u0000%\'\u0003"+
		"\u0011\b\u0000&%\u0001\u0000\u0000\u0000&\'\u0001\u0000\u0000\u0000\'"+
		"(\u0001\u0000\u0000\u0000()\u0005l\u0000\u0000)*\u0005e\u0000\u0000*+"+
		"\u0005v\u0000\u0000+,\u0005e\u0000\u0000,-\u0005l\u0000\u0000-.\u0005"+
		":\u0000\u0000./\u0001\u0000\u0000\u0000/0\u0006\u0001\u0001\u00000\u0004"+
		"\u0001\u0000\u0000\u000012\u0004\u0002\u0000\u000023\u0005:\u0000\u0000"+
		"34\u0006\u0002\u0002\u00004\u0006\u0001\u0000\u0000\u000057\u0007\u0001"+
		"\u0000\u000065\u0001\u0000\u0000\u000067\u0001\u0000\u0000\u00007<\u0001"+
		"\u0000\u0000\u00008:\u00050\u0000\u00009;\u0007\u0002\u0000\u0000:9\u0001"+
		"\u0000\u0000\u0000:;\u0001\u0000\u0000\u0000;=\u0001\u0000\u0000\u0000"+
		"<8\u0001\u0000\u0000\u0000<=\u0001\u0000\u0000\u0000=?\u0001\u0000\u0000"+
		"\u0000>@\u0007\u0003\u0000\u0000?>\u0001\u0000\u0000\u0000@A\u0001\u0000"+
		"\u0000\u0000A?\u0001\u0000\u0000\u0000AB\u0001\u0000\u0000\u0000B\b\u0001"+
		"\u0000\u0000\u0000CE\b\u0004\u0000\u0000DC\u0001\u0000\u0000\u0000EF\u0001"+
		"\u0000\u0000\u0000FD\u0001\u0000\u0000\u0000FG\u0001\u0000\u0000\u0000"+
		"G\n\u0001\u0000\u0000\u0000HI\u0004\u0005\u0001\u0000IM\u0005:\u0000\u0000"+
		"JL\b\u0000\u0000\u0000KJ\u0001\u0000\u0000\u0000LO\u0001\u0000\u0000\u0000"+
		"MK\u0001\u0000\u0000\u0000MN\u0001\u0000\u0000\u0000N\f\u0001\u0000\u0000"+
		"\u0000OM\u0001\u0000\u0000\u0000PR\u0007\u0005\u0000\u0000QP\u0001\u0000"+
		"\u0000\u0000RU\u0001\u0000\u0000\u0000SQ\u0001\u0000\u0000\u0000ST\u0001"+
		"\u0000\u0000\u0000T_\u0001\u0000\u0000\u0000US\u0001\u0000\u0000\u0000"+
		"VX\u0005\r\u0000\u0000WV\u0001\u0000\u0000\u0000XY\u0001\u0000\u0000\u0000"+
		"YW\u0001\u0000\u0000\u0000YZ\u0001\u0000\u0000\u0000Z\\\u0001\u0000\u0000"+
		"\u0000[]\u0005\n\u0000\u0000\\[\u0001\u0000\u0000\u0000\\]\u0001\u0000"+
		"\u0000\u0000]`\u0001\u0000\u0000\u0000^`\u0005\n\u0000\u0000_W\u0001\u0000"+
		"\u0000\u0000_^\u0001\u0000\u0000\u0000`h\u0001\u0000\u0000\u0000ac\u0007"+
		"\u0005\u0000\u0000ba\u0001\u0000\u0000\u0000cd\u0001\u0000\u0000\u0000"+
		"db\u0001\u0000\u0000\u0000de\u0001\u0000\u0000\u0000ef\u0001\u0000\u0000"+
		"\u0000fh\u0005\u0000\u0000\u0001gS\u0001\u0000\u0000\u0000gb\u0001\u0000"+
		"\u0000\u0000h\u000e\u0001\u0000\u0000\u0000ik\u0005\r\u0000\u0000ji\u0001"+
		"\u0000\u0000\u0000kl\u0001\u0000\u0000\u0000lj\u0001\u0000\u0000\u0000"+
		"lm\u0001\u0000\u0000\u0000mo\u0001\u0000\u0000\u0000np\u0005\n\u0000\u0000"+
		"on\u0001\u0000\u0000\u0000op\u0001\u0000\u0000\u0000ps\u0001\u0000\u0000"+
		"\u0000qs\u0005\n\u0000\u0000rj\u0001\u0000\u0000\u0000rq\u0001\u0000\u0000"+
		"\u0000s\u0010\u0001\u0000\u0000\u0000tv\u0004\b\u0002\u0000uw\u0007\u0005"+
		"\u0000\u0000vu\u0001\u0000\u0000\u0000wx\u0001\u0000\u0000\u0000xv\u0001"+
		"\u0000\u0000\u0000xy\u0001\u0000\u0000\u0000yz\u0001\u0000\u0000\u0000"+
		"z{\u0006\b\u0000\u0000{\u0012\u0001\u0000\u0000\u0000|}\u0003\r\u0006"+
		"\u0000}~\u0001\u0000\u0000\u0000~\u007f\u0006\t\u0000\u0000\u007f\u0014"+
		"\u0001\u0000\u0000\u0000\u0015\u0000\u0016\u001c!&6:<AFMSY\\_dglorx\u0003"+
		"\u0006\u0000\u0000\u0001\u0001\u0000\u0001\u0002\u0001";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}