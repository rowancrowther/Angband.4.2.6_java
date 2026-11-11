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

// Generated from BlowMethodLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.blowmethod;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class BlowMethodLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, CUT=2, STUN=3, MISS=4, PHYS=5, MSG=6, ACT=7, DESC=8, INTEGER=9, 
		COMMENT=10, INDENT=11, EOL=12, STRING=13, FREE_TEXT_EOL=14;
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
			"NAME", "CUT", "STUN", "MISS", "PHYS", "MSG", "ACT", "DESC", "INTEGER", 
			"COMMENT", "NL", "NEWLINE", "INDENT", "EOL", "STRING", "FREE_TEXT_EOL"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'cut:'", "'stun:'", "'miss:'", "'phys:'", "'msg:'", 
			"'act:'", "'desc:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "CUT", "STUN", "MISS", "PHYS", "MSG", "ACT", "DESC", "INTEGER", 
			"COMMENT", "INDENT", "EOL", "STRING", "FREE_TEXT_EOL"
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


	public BlowMethodLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "BlowMethodLexer.g4"; }

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
		case 12:
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
		"\u0004\u0000\u000e\u009e\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000"+
		"\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003"+
		"\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006"+
		"\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002"+
		"\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002"+
		"\u000e\u0007\u000e\u0002\u000f\u0007\u000f\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\b\u0003\bY\b\b\u0001\b\u0004\b\\"+
		"\b\b\u000b\b\f\b]\u0001\t\u0001\t\u0005\tb\b\t\n\t\f\te\t\t\u0001\t\u0001"+
		"\t\u0003\ti\b\t\u0001\t\u0001\t\u0001\n\u0005\nn\b\n\n\n\f\nq\t\n\u0001"+
		"\n\u0004\nt\b\n\u000b\n\f\nu\u0001\n\u0003\ny\b\n\u0001\n\u0003\n|\b\n"+
		"\u0001\u000b\u0004\u000b\u007f\b\u000b\u000b\u000b\f\u000b\u0080\u0001"+
		"\u000b\u0003\u000b\u0084\b\u000b\u0001\u000b\u0003\u000b\u0087\b\u000b"+
		"\u0001\f\u0001\f\u0004\f\u008b\b\f\u000b\f\f\f\u008c\u0001\f\u0001\f\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0004\u000e\u0096\b\u000e\u000b"+
		"\u000e\f\u000e\u0097\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0000\u0000\u0010\u0002\u0001\u0004\u0002\u0006\u0003\b\u0004\n"+
		"\u0005\f\u0006\u000e\u0007\u0010\b\u0012\t\u0014\n\u0016\u0000\u0018\u0000"+
		"\u001a\u000b\u001c\f\u001e\r \u000e\u0002\u0000\u0001\u0002\u0002\u0000"+
		"\n\n\r\r\u0002\u0000\t\t  \u00a7\u0000\u0002\u0001\u0000\u0000\u0000\u0000"+
		"\u0004\u0001\u0000\u0000\u0000\u0000\u0006\u0001\u0000\u0000\u0000\u0000"+
		"\b\u0001\u0000\u0000\u0000\u0000\n\u0001\u0000\u0000\u0000\u0000\f\u0001"+
		"\u0000\u0000\u0000\u0000\u000e\u0001\u0000\u0000\u0000\u0000\u0010\u0001"+
		"\u0000\u0000\u0000\u0000\u0012\u0001\u0000\u0000\u0000\u0000\u0014\u0001"+
		"\u0000\u0000\u0000\u0000\u001a\u0001\u0000\u0000\u0000\u0000\u001c\u0001"+
		"\u0000\u0000\u0000\u0001\u001e\u0001\u0000\u0000\u0000\u0001 \u0001\u0000"+
		"\u0000\u0000\u0002\"\u0001\u0000\u0000\u0000\u0004*\u0001\u0000\u0000"+
		"\u0000\u0006/\u0001\u0000\u0000\u0000\b5\u0001\u0000\u0000\u0000\n;\u0001"+
		"\u0000\u0000\u0000\fA\u0001\u0000\u0000\u0000\u000eH\u0001\u0000\u0000"+
		"\u0000\u0010O\u0001\u0000\u0000\u0000\u0012X\u0001\u0000\u0000\u0000\u0014"+
		"_\u0001\u0000\u0000\u0000\u0016o\u0001\u0000\u0000\u0000\u0018\u0086\u0001"+
		"\u0000\u0000\u0000\u001a\u0088\u0001\u0000\u0000\u0000\u001c\u0090\u0001"+
		"\u0000\u0000\u0000\u001e\u0095\u0001\u0000\u0000\u0000 \u0099\u0001\u0000"+
		"\u0000\u0000\"#\u0005n\u0000\u0000#$\u0005a\u0000\u0000$%\u0005m\u0000"+
		"\u0000%&\u0005e\u0000\u0000&\'\u0005:\u0000\u0000\'(\u0001\u0000\u0000"+
		"\u0000()\u0006\u0000\u0000\u0000)\u0003\u0001\u0000\u0000\u0000*+\u0005"+
		"c\u0000\u0000+,\u0005u\u0000\u0000,-\u0005t\u0000\u0000-.\u0005:\u0000"+
		"\u0000.\u0005\u0001\u0000\u0000\u0000/0\u0005s\u0000\u000001\u0005t\u0000"+
		"\u000012\u0005u\u0000\u000023\u0005n\u0000\u000034\u0005:\u0000\u0000"+
		"4\u0007\u0001\u0000\u0000\u000056\u0005m\u0000\u000067\u0005i\u0000\u0000"+
		"78\u0005s\u0000\u000089\u0005s\u0000\u00009:\u0005:\u0000\u0000:\t\u0001"+
		"\u0000\u0000\u0000;<\u0005p\u0000\u0000<=\u0005h\u0000\u0000=>\u0005y"+
		"\u0000\u0000>?\u0005s\u0000\u0000?@\u0005:\u0000\u0000@\u000b\u0001\u0000"+
		"\u0000\u0000AB\u0005m\u0000\u0000BC\u0005s\u0000\u0000CD\u0005g\u0000"+
		"\u0000DE\u0005:\u0000\u0000EF\u0001\u0000\u0000\u0000FG\u0006\u0005\u0000"+
		"\u0000G\r\u0001\u0000\u0000\u0000HI\u0005a\u0000\u0000IJ\u0005c\u0000"+
		"\u0000JK\u0005t\u0000\u0000KL\u0005:\u0000\u0000LM\u0001\u0000\u0000\u0000"+
		"MN\u0006\u0006\u0000\u0000N\u000f\u0001\u0000\u0000\u0000OP\u0005d\u0000"+
		"\u0000PQ\u0005e\u0000\u0000QR\u0005s\u0000\u0000RS\u0005c\u0000\u0000"+
		"ST\u0005:\u0000\u0000TU\u0001\u0000\u0000\u0000UV\u0006\u0007\u0000\u0000"+
		"V\u0011\u0001\u0000\u0000\u0000WY\u0005-\u0000\u0000XW\u0001\u0000\u0000"+
		"\u0000XY\u0001\u0000\u0000\u0000Y[\u0001\u0000\u0000\u0000Z\\\u000209"+
		"\u0000[Z\u0001\u0000\u0000\u0000\\]\u0001\u0000\u0000\u0000][\u0001\u0000"+
		"\u0000\u0000]^\u0001\u0000\u0000\u0000^\u0013\u0001\u0000\u0000\u0000"+
		"_c\u0005#\u0000\u0000`b\b\u0000\u0000\u0000a`\u0001\u0000\u0000\u0000"+
		"be\u0001\u0000\u0000\u0000ca\u0001\u0000\u0000\u0000cd\u0001\u0000\u0000"+
		"\u0000dh\u0001\u0000\u0000\u0000ec\u0001\u0000\u0000\u0000fi\u0003\u0016"+
		"\n\u0000gi\u0005\u0000\u0000\u0001hf\u0001\u0000\u0000\u0000hg\u0001\u0000"+
		"\u0000\u0000ij\u0001\u0000\u0000\u0000jk\u0006\t\u0001\u0000k\u0015\u0001"+
		"\u0000\u0000\u0000ln\u0007\u0001\u0000\u0000ml\u0001\u0000\u0000\u0000"+
		"nq\u0001\u0000\u0000\u0000om\u0001\u0000\u0000\u0000op\u0001\u0000\u0000"+
		"\u0000p{\u0001\u0000\u0000\u0000qo\u0001\u0000\u0000\u0000rt\u0005\r\u0000"+
		"\u0000sr\u0001\u0000\u0000\u0000tu\u0001\u0000\u0000\u0000us\u0001\u0000"+
		"\u0000\u0000uv\u0001\u0000\u0000\u0000vx\u0001\u0000\u0000\u0000wy\u0005"+
		"\n\u0000\u0000xw\u0001\u0000\u0000\u0000xy\u0001\u0000\u0000\u0000y|\u0001"+
		"\u0000\u0000\u0000z|\u0005\n\u0000\u0000{s\u0001\u0000\u0000\u0000{z\u0001"+
		"\u0000\u0000\u0000|\u0017\u0001\u0000\u0000\u0000}\u007f\u0005\r\u0000"+
		"\u0000~}\u0001\u0000\u0000\u0000\u007f\u0080\u0001\u0000\u0000\u0000\u0080"+
		"~\u0001\u0000\u0000\u0000\u0080\u0081\u0001\u0000\u0000\u0000\u0081\u0083"+
		"\u0001\u0000\u0000\u0000\u0082\u0084\u0005\n\u0000\u0000\u0083\u0082\u0001"+
		"\u0000\u0000\u0000\u0083\u0084\u0001\u0000\u0000\u0000\u0084\u0087\u0001"+
		"\u0000\u0000\u0000\u0085\u0087\u0005\n\u0000\u0000\u0086~\u0001\u0000"+
		"\u0000\u0000\u0086\u0085\u0001\u0000\u0000\u0000\u0087\u0019\u0001\u0000"+
		"\u0000\u0000\u0088\u008a\u0004\f\u0000\u0000\u0089\u008b\u0007\u0001\u0000"+
		"\u0000\u008a\u0089\u0001\u0000\u0000\u0000\u008b\u008c\u0001\u0000\u0000"+
		"\u0000\u008c\u008a\u0001\u0000\u0000\u0000\u008c\u008d\u0001\u0000\u0000"+
		"\u0000\u008d\u008e\u0001\u0000\u0000\u0000\u008e\u008f\u0006\f\u0001\u0000"+
		"\u008f\u001b\u0001\u0000\u0000\u0000\u0090\u0091\u0003\u0016\n\u0000\u0091"+
		"\u0092\u0001\u0000\u0000\u0000\u0092\u0093\u0006\r\u0001\u0000\u0093\u001d"+
		"\u0001\u0000\u0000\u0000\u0094\u0096\b\u0000\u0000\u0000\u0095\u0094\u0001"+
		"\u0000\u0000\u0000\u0096\u0097\u0001\u0000\u0000\u0000\u0097\u0095\u0001"+
		"\u0000\u0000\u0000\u0097\u0098\u0001\u0000\u0000\u0000\u0098\u001f\u0001"+
		"\u0000\u0000\u0000\u0099\u009a\u0003\u0016\n\u0000\u009a\u009b\u0001\u0000"+
		"\u0000\u0000\u009b\u009c\u0006\u000f\u0001\u0000\u009c\u009d\u0006\u000f"+
		"\u0002\u0000\u009d!\u0001\u0000\u0000\u0000\u000f\u0000\u0001X]choux{"+
		"\u0080\u0083\u0086\u008c\u0097\u0003\u0005\u0001\u0000\u0006\u0000\u0000"+
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