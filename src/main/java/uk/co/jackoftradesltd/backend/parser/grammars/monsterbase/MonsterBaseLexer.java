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

// Generated from MonsterBaseLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.monsterbase;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class MonsterBaseLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, GLYPH=2, PAIN=3, FLAGS=4, DESC=5, UINT=6, COMMENT=7, INDENT=8, 
		EOL=9, SINGLE_CHAR=10, STRING=11, FLAG=12, FLAG_OR=13, FLAG_EOL=14;
	public static final int
		GLYPH_MODE=1, FREE_TEXT=2, FLAG_MODE=3;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "GLYPH_MODE", "FREE_TEXT", "FLAG_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"NAME", "GLYPH", "PAIN", "FLAGS", "DESC", "UINT", "COMMENT", "NL", "NEWLINE", 
			"INDENT", "EOL", "FLAG_BODY", "SINGLE_CHAR", "STRING", "FLAG", "FLAG_OR", 
			"FLAG_EOL"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'glyph:'", "'pain:'", "'flags:'", "'desc:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "GLYPH", "PAIN", "FLAGS", "DESC", "UINT", "COMMENT", "INDENT", 
			"EOL", "SINGLE_CHAR", "STRING", "FLAG", "FLAG_OR", "FLAG_EOL"
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


	public MonsterBaseLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "MonsterBaseLexer.g4"; }

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
		"\u0004\u0000\u000e\u00b9\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff"+
		"\uffff\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0005\u0003\u0005P\b\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0003\u0005V\b\u0005\u0001\u0005\u0004\u0005Y\b\u0005"+
		"\u000b\u0005\f\u0005Z\u0001\u0005\u0005\u0005^\b\u0005\n\u0005\f\u0005"+
		"a\t\u0005\u0001\u0006\u0001\u0006\u0005\u0006e\b\u0006\n\u0006\f\u0006"+
		"h\t\u0006\u0001\u0006\u0001\u0006\u0003\u0006l\b\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0007\u0005\u0007q\b\u0007\n\u0007\f\u0007t\t\u0007\u0001"+
		"\u0007\u0004\u0007w\b\u0007\u000b\u0007\f\u0007x\u0001\u0007\u0003\u0007"+
		"|\b\u0007\u0001\u0007\u0003\u0007\u007f\b\u0007\u0001\b\u0004\b\u0082"+
		"\b\b\u000b\b\f\b\u0083\u0001\b\u0003\b\u0087\b\b\u0001\b\u0003\b\u008a"+
		"\b\b\u0001\t\u0001\t\u0004\t\u008e\b\t\u000b\t\f\t\u008f\u0001\t\u0001"+
		"\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0005\u000b"+
		"\u009a\b\u000b\n\u000b\f\u000b\u009d\t\u000b\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\r\u0004\r\u00a4\b\r\u000b\r\f\r\u00a5\u0001\r\u0001\r\u0001\u000e"+
		"\u0001\u000e\u0001\u000f\u0003\u000f\u00ad\b\u000f\u0001\u000f\u0001\u000f"+
		"\u0003\u000f\u00b1\b\u000f\u0001\u0010\u0004\u0010\u00b4\b\u0010\u000b"+
		"\u0010\f\u0010\u00b5\u0001\u0010\u0001\u0010\u0000\u0000\u0011\u0004\u0001"+
		"\u0006\u0002\b\u0003\n\u0004\f\u0005\u000e\u0006\u0010\u0007\u0012\u0000"+
		"\u0014\u0000\u0016\b\u0018\t\u001a\u0000\u001c\n\u001e\u000b \f\"\r$\u000e"+
		"\u0004\u0000\u0001\u0002\u0003\u0006\u0002\u0000++--\u0003\u000009AFa"+
		"f\u0003\u0000\n\n\r\r::\u0002\u0000\n\n\r\r\u0002\u0000\t\t  \u0003\u0000"+
		"09AZ__\u00c6\u0000\u0004\u0001\u0000\u0000\u0000\u0000\u0006\u0001\u0000"+
		"\u0000\u0000\u0000\b\u0001\u0000\u0000\u0000\u0000\n\u0001\u0000\u0000"+
		"\u0000\u0000\f\u0001\u0000\u0000\u0000\u0000\u000e\u0001\u0000\u0000\u0000"+
		"\u0000\u0010\u0001\u0000\u0000\u0000\u0000\u0016\u0001\u0000\u0000\u0000"+
		"\u0000\u0018\u0001\u0000\u0000\u0000\u0001\u001c\u0001\u0000\u0000\u0000"+
		"\u0002\u001e\u0001\u0000\u0000\u0000\u0003 \u0001\u0000\u0000\u0000\u0003"+
		"\"\u0001\u0000\u0000\u0000\u0003$\u0001\u0000\u0000\u0000\u0004&\u0001"+
		"\u0000\u0000\u0000\u0006.\u0001\u0000\u0000\u0000\b7\u0001\u0000\u0000"+
		"\u0000\n=\u0001\u0000\u0000\u0000\fF\u0001\u0000\u0000\u0000\u000eO\u0001"+
		"\u0000\u0000\u0000\u0010b\u0001\u0000\u0000\u0000\u0012r\u0001\u0000\u0000"+
		"\u0000\u0014\u0089\u0001\u0000\u0000\u0000\u0016\u008b\u0001\u0000\u0000"+
		"\u0000\u0018\u0093\u0001\u0000\u0000\u0000\u001a\u0097\u0001\u0000\u0000"+
		"\u0000\u001c\u009e\u0001\u0000\u0000\u0000\u001e\u00a3\u0001\u0000\u0000"+
		"\u0000 \u00a9\u0001\u0000\u0000\u0000\"\u00ac\u0001\u0000\u0000\u0000"+
		"$\u00b3\u0001\u0000\u0000\u0000&\'\u0005n\u0000\u0000\'(\u0005a\u0000"+
		"\u0000()\u0005m\u0000\u0000)*\u0005e\u0000\u0000*+\u0005:\u0000\u0000"+
		"+,\u0001\u0000\u0000\u0000,-\u0006\u0000\u0000\u0000-\u0005\u0001\u0000"+
		"\u0000\u0000./\u0005g\u0000\u0000/0\u0005l\u0000\u000001\u0005y\u0000"+
		"\u000012\u0005p\u0000\u000023\u0005h\u0000\u000034\u0005:\u0000\u0000"+
		"45\u0001\u0000\u0000\u000056\u0006\u0001\u0001\u00006\u0007\u0001\u0000"+
		"\u0000\u000078\u0005p\u0000\u000089\u0005a\u0000\u00009:\u0005i\u0000"+
		"\u0000:;\u0005n\u0000\u0000;<\u0005:\u0000\u0000<\t\u0001\u0000\u0000"+
		"\u0000=>\u0005f\u0000\u0000>?\u0005l\u0000\u0000?@\u0005a\u0000\u0000"+
		"@A\u0005g\u0000\u0000AB\u0005s\u0000\u0000BC\u0005:\u0000\u0000CD\u0001"+
		"\u0000\u0000\u0000DE\u0006\u0003\u0002\u0000E\u000b\u0001\u0000\u0000"+
		"\u0000FG\u0005d\u0000\u0000GH\u0005e\u0000\u0000HI\u0005s\u0000\u0000"+
		"IJ\u0005c\u0000\u0000JK\u0005:\u0000\u0000KL\u0001\u0000\u0000\u0000L"+
		"M\u0006\u0004\u0000\u0000M\r\u0001\u0000\u0000\u0000NP\u0007\u0000\u0000"+
		"\u0000ON\u0001\u0000\u0000\u0000OP\u0001\u0000\u0000\u0000PU\u0001\u0000"+
		"\u0000\u0000QR\u00050\u0000\u0000RV\u0005x\u0000\u0000ST\u00050\u0000"+
		"\u0000TV\u0005X\u0000\u0000UQ\u0001\u0000\u0000\u0000US\u0001\u0000\u0000"+
		"\u0000UV\u0001\u0000\u0000\u0000VX\u0001\u0000\u0000\u0000WY\u0007\u0001"+
		"\u0000\u0000XW\u0001\u0000\u0000\u0000YZ\u0001\u0000\u0000\u0000ZX\u0001"+
		"\u0000\u0000\u0000Z[\u0001\u0000\u0000\u0000[_\u0001\u0000\u0000\u0000"+
		"\\^\b\u0002\u0000\u0000]\\\u0001\u0000\u0000\u0000^a\u0001\u0000\u0000"+
		"\u0000_]\u0001\u0000\u0000\u0000_`\u0001\u0000\u0000\u0000`\u000f\u0001"+
		"\u0000\u0000\u0000a_\u0001\u0000\u0000\u0000bf\u0005#\u0000\u0000ce\b"+
		"\u0003\u0000\u0000dc\u0001\u0000\u0000\u0000eh\u0001\u0000\u0000\u0000"+
		"fd\u0001\u0000\u0000\u0000fg\u0001\u0000\u0000\u0000gk\u0001\u0000\u0000"+
		"\u0000hf\u0001\u0000\u0000\u0000il\u0003\u0012\u0007\u0000jl\u0005\u0000"+
		"\u0000\u0001ki\u0001\u0000\u0000\u0000kj\u0001\u0000\u0000\u0000lm\u0001"+
		"\u0000\u0000\u0000mn\u0006\u0006\u0003\u0000n\u0011\u0001\u0000\u0000"+
		"\u0000oq\u0007\u0004\u0000\u0000po\u0001\u0000\u0000\u0000qt\u0001\u0000"+
		"\u0000\u0000rp\u0001\u0000\u0000\u0000rs\u0001\u0000\u0000\u0000s~\u0001"+
		"\u0000\u0000\u0000tr\u0001\u0000\u0000\u0000uw\u0005\r\u0000\u0000vu\u0001"+
		"\u0000\u0000\u0000wx\u0001\u0000\u0000\u0000xv\u0001\u0000\u0000\u0000"+
		"xy\u0001\u0000\u0000\u0000y{\u0001\u0000\u0000\u0000z|\u0005\n\u0000\u0000"+
		"{z\u0001\u0000\u0000\u0000{|\u0001\u0000\u0000\u0000|\u007f\u0001\u0000"+
		"\u0000\u0000}\u007f\u0005\n\u0000\u0000~v\u0001\u0000\u0000\u0000~}\u0001"+
		"\u0000\u0000\u0000\u007f\u0013\u0001\u0000\u0000\u0000\u0080\u0082\u0005"+
		"\r\u0000\u0000\u0081\u0080\u0001\u0000\u0000\u0000\u0082\u0083\u0001\u0000"+
		"\u0000\u0000\u0083\u0081\u0001\u0000\u0000\u0000\u0083\u0084\u0001\u0000"+
		"\u0000\u0000\u0084\u0086\u0001\u0000\u0000\u0000\u0085\u0087\u0005\n\u0000"+
		"\u0000\u0086\u0085\u0001\u0000\u0000\u0000\u0086\u0087\u0001\u0000\u0000"+
		"\u0000\u0087\u008a\u0001\u0000\u0000\u0000\u0088\u008a\u0005\n\u0000\u0000"+
		"\u0089\u0081\u0001\u0000\u0000\u0000\u0089\u0088\u0001\u0000\u0000\u0000"+
		"\u008a\u0015\u0001\u0000\u0000\u0000\u008b\u008d\u0004\t\u0000\u0000\u008c"+
		"\u008e\u0007\u0004\u0000\u0000\u008d\u008c\u0001\u0000\u0000\u0000\u008e"+
		"\u008f\u0001\u0000\u0000\u0000\u008f\u008d\u0001\u0000\u0000\u0000\u008f"+
		"\u0090\u0001\u0000\u0000\u0000\u0090\u0091\u0001\u0000\u0000\u0000\u0091"+
		"\u0092\u0006\t\u0003\u0000\u0092\u0017\u0001\u0000\u0000\u0000\u0093\u0094"+
		"\u0003\u0012\u0007\u0000\u0094\u0095\u0001\u0000\u0000\u0000\u0095\u0096"+
		"\u0006\n\u0003\u0000\u0096\u0019\u0001\u0000\u0000\u0000\u0097\u009b\u0002"+
		"AZ\u0000\u0098\u009a\u0007\u0005\u0000\u0000\u0099\u0098\u0001\u0000\u0000"+
		"\u0000\u009a\u009d\u0001\u0000\u0000\u0000\u009b\u0099\u0001\u0000\u0000"+
		"\u0000\u009b\u009c\u0001\u0000\u0000\u0000\u009c\u001b\u0001\u0000\u0000"+
		"\u0000\u009d\u009b\u0001\u0000\u0000\u0000\u009e\u009f\b\u0003\u0000\u0000"+
		"\u009f\u00a0\u0001\u0000\u0000\u0000\u00a0\u00a1\u0006\f\u0004\u0000\u00a1"+
		"\u001d\u0001\u0000\u0000\u0000\u00a2\u00a4\b\u0003\u0000\u0000\u00a3\u00a2"+
		"\u0001\u0000\u0000\u0000\u00a4\u00a5\u0001\u0000\u0000\u0000\u00a5\u00a3"+
		"\u0001\u0000\u0000\u0000\u00a5\u00a6\u0001\u0000\u0000\u0000\u00a6\u00a7"+
		"\u0001\u0000\u0000\u0000\u00a7\u00a8\u0006\r\u0004\u0000\u00a8\u001f\u0001"+
		"\u0000\u0000\u0000\u00a9\u00aa\u0003\u001a\u000b\u0000\u00aa!\u0001\u0000"+
		"\u0000\u0000\u00ab\u00ad\u0005 \u0000\u0000\u00ac\u00ab\u0001\u0000\u0000"+
		"\u0000\u00ac\u00ad\u0001\u0000\u0000\u0000\u00ad\u00ae\u0001\u0000\u0000"+
		"\u0000\u00ae\u00b0\u0005|\u0000\u0000\u00af\u00b1\u0005 \u0000\u0000\u00b0"+
		"\u00af\u0001\u0000\u0000\u0000\u00b0\u00b1\u0001\u0000\u0000\u0000\u00b1"+
		"#\u0001\u0000\u0000\u0000\u00b2\u00b4\u0003\u0012\u0007\u0000\u00b3\u00b2"+
		"\u0001\u0000\u0000\u0000\u00b4\u00b5\u0001\u0000\u0000\u0000\u00b5\u00b3"+
		"\u0001\u0000\u0000\u0000\u00b5\u00b6\u0001\u0000\u0000\u0000\u00b6\u00b7"+
		"\u0001\u0000\u0000\u0000\u00b7\u00b8\u0006\u0010\u0004\u0000\u00b8%\u0001"+
		"\u0000\u0000\u0000\u0017\u0000\u0001\u0002\u0003OUZ_fkrx{~\u0083\u0086"+
		"\u0089\u008f\u009b\u00a5\u00ac\u00b0\u00b5\u0005\u0005\u0002\u0000\u0005"+
		"\u0001\u0000\u0005\u0003\u0000\u0006\u0000\u0000\u0004\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}