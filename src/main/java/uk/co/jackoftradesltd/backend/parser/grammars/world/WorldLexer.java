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
		LEVEL=1, COLON=2, INTEGER=3, NAME=4, COLON_TO_END=5, COMMENT=6, INDENT=7, 
		EOL=8;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"LEVEL", "COLON", "INTEGER", "NAME", "COLON_TO_END", "COMMENT", "NL", 
			"NEWLINE", "INDENT", "EOL"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'level:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "LEVEL", "COLON", "INTEGER", "NAME", "COLON_TO_END", "COMMENT", 
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
		case 0:
			LEVEL_action((RuleContext)_localctx, actionIndex);
			break;
		case 1:
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
		case 1:
			return COLON_sempred((RuleContext)_localctx, predIndex);
		case 4:
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
		"\u0004\u0000\bz\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0003"+
		"\u0002$\b\u0002\u0001\u0002\u0001\u0002\u0003\u0002(\b\u0002\u0003\u0002"+
		"*\b\u0002\u0001\u0002\u0004\u0002-\b\u0002\u000b\u0002\f\u0002.\u0001"+
		"\u0003\u0004\u00032\b\u0003\u000b\u0003\f\u00033\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0005\u00049\b\u0004\n\u0004\f\u0004<\t\u0004\u0001\u0005"+
		"\u0001\u0005\u0005\u0005@\b\u0005\n\u0005\f\u0005C\t\u0005\u0001\u0005"+
		"\u0001\u0005\u0003\u0005G\b\u0005\u0001\u0005\u0001\u0005\u0001\u0006"+
		"\u0005\u0006L\b\u0006\n\u0006\f\u0006O\t\u0006\u0001\u0006\u0004\u0006"+
		"R\b\u0006\u000b\u0006\f\u0006S\u0001\u0006\u0003\u0006W\b\u0006\u0001"+
		"\u0006\u0003\u0006Z\b\u0006\u0001\u0006\u0004\u0006]\b\u0006\u000b\u0006"+
		"\f\u0006^\u0001\u0006\u0003\u0006b\b\u0006\u0001\u0007\u0004\u0007e\b"+
		"\u0007\u000b\u0007\f\u0007f\u0001\u0007\u0003\u0007j\b\u0007\u0001\u0007"+
		"\u0003\u0007m\b\u0007\u0001\b\u0001\b\u0004\bq\b\b\u000b\b\f\br\u0001"+
		"\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0000\u0000\n\u0001\u0001\u0003"+
		"\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0000\u000f\u0000"+
		"\u0011\u0007\u0013\b\u0001\u0000\u0006\u0002\u0000++--\u0002\u0000XXx"+
		"x\u0003\u000009AFaf\u0003\u0000\n\n\r\r::\u0002\u0000\n\n\r\r\u0002\u0000"+
		"\t\t  \u0089\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000"+
		"\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000"+
		"\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000"+
		"\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000"+
		"\u0000\u0001\u0015\u0001\u0000\u0000\u0000\u0003\u001e\u0001\u0000\u0000"+
		"\u0000\u0005#\u0001\u0000\u0000\u0000\u00071\u0001\u0000\u0000\u0000\t"+
		"5\u0001\u0000\u0000\u0000\u000b=\u0001\u0000\u0000\u0000\ra\u0001\u0000"+
		"\u0000\u0000\u000fl\u0001\u0000\u0000\u0000\u0011n\u0001\u0000\u0000\u0000"+
		"\u0013v\u0001\u0000\u0000\u0000\u0015\u0016\u0005l\u0000\u0000\u0016\u0017"+
		"\u0005e\u0000\u0000\u0017\u0018\u0005v\u0000\u0000\u0018\u0019\u0005e"+
		"\u0000\u0000\u0019\u001a\u0005l\u0000\u0000\u001a\u001b\u0005:\u0000\u0000"+
		"\u001b\u001c\u0001\u0000\u0000\u0000\u001c\u001d\u0006\u0000\u0000\u0000"+
		"\u001d\u0002\u0001\u0000\u0000\u0000\u001e\u001f\u0004\u0001\u0000\u0000"+
		"\u001f \u0005:\u0000\u0000 !\u0006\u0001\u0001\u0000!\u0004\u0001\u0000"+
		"\u0000\u0000\"$\u0007\u0000\u0000\u0000#\"\u0001\u0000\u0000\u0000#$\u0001"+
		"\u0000\u0000\u0000$)\u0001\u0000\u0000\u0000%\'\u00050\u0000\u0000&(\u0007"+
		"\u0001\u0000\u0000\'&\u0001\u0000\u0000\u0000\'(\u0001\u0000\u0000\u0000"+
		"(*\u0001\u0000\u0000\u0000)%\u0001\u0000\u0000\u0000)*\u0001\u0000\u0000"+
		"\u0000*,\u0001\u0000\u0000\u0000+-\u0007\u0002\u0000\u0000,+\u0001\u0000"+
		"\u0000\u0000-.\u0001\u0000\u0000\u0000.,\u0001\u0000\u0000\u0000./\u0001"+
		"\u0000\u0000\u0000/\u0006\u0001\u0000\u0000\u000002\b\u0003\u0000\u0000"+
		"10\u0001\u0000\u0000\u000023\u0001\u0000\u0000\u000031\u0001\u0000\u0000"+
		"\u000034\u0001\u0000\u0000\u00004\b\u0001\u0000\u0000\u000056\u0004\u0004"+
		"\u0001\u00006:\u0005:\u0000\u000079\b\u0004\u0000\u000087\u0001\u0000"+
		"\u0000\u00009<\u0001\u0000\u0000\u0000:8\u0001\u0000\u0000\u0000:;\u0001"+
		"\u0000\u0000\u0000;\n\u0001\u0000\u0000\u0000<:\u0001\u0000\u0000\u0000"+
		"=A\u0005#\u0000\u0000>@\b\u0004\u0000\u0000?>\u0001\u0000\u0000\u0000"+
		"@C\u0001\u0000\u0000\u0000A?\u0001\u0000\u0000\u0000AB\u0001\u0000\u0000"+
		"\u0000BF\u0001\u0000\u0000\u0000CA\u0001\u0000\u0000\u0000DG\u0003\r\u0006"+
		"\u0000EG\u0005\u0000\u0000\u0001FD\u0001\u0000\u0000\u0000FE\u0001\u0000"+
		"\u0000\u0000GH\u0001\u0000\u0000\u0000HI\u0006\u0005\u0002\u0000I\f\u0001"+
		"\u0000\u0000\u0000JL\u0007\u0005\u0000\u0000KJ\u0001\u0000\u0000\u0000"+
		"LO\u0001\u0000\u0000\u0000MK\u0001\u0000\u0000\u0000MN\u0001\u0000\u0000"+
		"\u0000NY\u0001\u0000\u0000\u0000OM\u0001\u0000\u0000\u0000PR\u0005\r\u0000"+
		"\u0000QP\u0001\u0000\u0000\u0000RS\u0001\u0000\u0000\u0000SQ\u0001\u0000"+
		"\u0000\u0000ST\u0001\u0000\u0000\u0000TV\u0001\u0000\u0000\u0000UW\u0005"+
		"\n\u0000\u0000VU\u0001\u0000\u0000\u0000VW\u0001\u0000\u0000\u0000WZ\u0001"+
		"\u0000\u0000\u0000XZ\u0005\n\u0000\u0000YQ\u0001\u0000\u0000\u0000YX\u0001"+
		"\u0000\u0000\u0000Zb\u0001\u0000\u0000\u0000[]\u0007\u0005\u0000\u0000"+
		"\\[\u0001\u0000\u0000\u0000]^\u0001\u0000\u0000\u0000^\\\u0001\u0000\u0000"+
		"\u0000^_\u0001\u0000\u0000\u0000_`\u0001\u0000\u0000\u0000`b\u0005\u0000"+
		"\u0000\u0001aM\u0001\u0000\u0000\u0000a\\\u0001\u0000\u0000\u0000b\u000e"+
		"\u0001\u0000\u0000\u0000ce\u0005\r\u0000\u0000dc\u0001\u0000\u0000\u0000"+
		"ef\u0001\u0000\u0000\u0000fd\u0001\u0000\u0000\u0000fg\u0001\u0000\u0000"+
		"\u0000gi\u0001\u0000\u0000\u0000hj\u0005\n\u0000\u0000ih\u0001\u0000\u0000"+
		"\u0000ij\u0001\u0000\u0000\u0000jm\u0001\u0000\u0000\u0000km\u0005\n\u0000"+
		"\u0000ld\u0001\u0000\u0000\u0000lk\u0001\u0000\u0000\u0000m\u0010\u0001"+
		"\u0000\u0000\u0000np\u0004\b\u0002\u0000oq\u0007\u0005\u0000\u0000po\u0001"+
		"\u0000\u0000\u0000qr\u0001\u0000\u0000\u0000rp\u0001\u0000\u0000\u0000"+
		"rs\u0001\u0000\u0000\u0000st\u0001\u0000\u0000\u0000tu\u0006\b\u0002\u0000"+
		"u\u0012\u0001\u0000\u0000\u0000vw\u0003\r\u0006\u0000wx\u0001\u0000\u0000"+
		"\u0000xy\u0006\t\u0002\u0000y\u0014\u0001\u0000\u0000\u0000\u0013\u0000"+
		"#\').3:AFMSVY^afilr\u0003\u0001\u0000\u0000\u0001\u0001\u0001\u0006\u0000"+
		"\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}