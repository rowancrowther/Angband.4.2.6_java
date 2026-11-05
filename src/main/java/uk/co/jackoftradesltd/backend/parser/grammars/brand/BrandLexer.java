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

// Generated from /home/rowan/IdeaProjects/Angband.4.2.6_java/src/main/java/uk/co/jackoftradesltd/backend/parser/grammars/BrandLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.brand;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class BrandLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		CODE=1, NAME=2, MULTIPLIER=3, O_MULTIPLIER=4, POWER=5, VERB=6, RESIST_FLAG=7, 
		VULN_FLAG=8, INTEGER=9, COMMENT=10, EOL=11, FLAG=12, STRING=13;
	public static final int
		FLAG_MODE=1, FREE_TEXT_MODE=2;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "FLAG_MODE", "FREE_TEXT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"CODE", "NAME", "MULTIPLIER", "O_MULTIPLIER", "POWER", "VERB", "RESIST_FLAG", 
			"VULN_FLAG", "INTEGER", "COMMENT", "EOL", "FLAG_BODY", "FLAG", "STRING"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'code:'", "'name:'", "'multiplier:'", "'o-multiplier:'", "'power:'", 
			"'verb:'", "'resist-flag:'", "'vuln-flag:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "CODE", "NAME", "MULTIPLIER", "O_MULTIPLIER", "POWER", "VERB", 
			"RESIST_FLAG", "VULN_FLAG", "INTEGER", "COMMENT", "EOL", "FLAG", "STRING"
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


	public BrandLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "BrandLexer.g4"; }

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

	public static final String _serializedATN =
		"\u0004\u0000\r\u00a6\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff\uffff"+
		"\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002"+
		"\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005"+
		"\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002"+
		"\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007\f\u0002"+
		"\r\u0007\r\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\b\u0003\bv\b\b\u0001\b\u0004\by\b\b\u000b"+
		"\b\f\bz\u0001\t\u0001\t\u0005\t\u007f\b\t\n\t\f\t\u0082\t\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\n\u0005\n\u0089\b\n\n\n\f\n\u008c\t\n\u0001\n"+
		"\u0003\n\u008f\b\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b"+
		"\u0005\u000b\u0097\b\u000b\n\u000b\f\u000b\u009a\t\u000b\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\r\u0004\r\u00a1\b\r\u000b\r\f\r\u00a2\u0001\r"+
		"\u0001\r\u0000\u0000\u000e\u0003\u0001\u0005\u0002\u0007\u0003\t\u0004"+
		"\u000b\u0005\r\u0006\u000f\u0007\u0011\b\u0013\t\u0015\n\u0017\u000b\u0019"+
		"\u0000\u001b\f\u001d\r\u0003\u0000\u0001\u0002\u0003\u0001\u0000\n\n\u0003"+
		"\u000009AZ__\u0002\u0000\n\n\r\r\u00a9\u0000\u0003\u0001\u0000\u0000\u0000"+
		"\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000"+
		"\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000"+
		"\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011"+
		"\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015"+
		"\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0001\u001b"+
		"\u0001\u0000\u0000\u0000\u0002\u001d\u0001\u0000\u0000\u0000\u0003\u001f"+
		"\u0001\u0000\u0000\u0000\u0005\'\u0001\u0000\u0000\u0000\u0007/\u0001"+
		"\u0000\u0000\u0000\t;\u0001\u0000\u0000\u0000\u000bI\u0001\u0000\u0000"+
		"\u0000\rP\u0001\u0000\u0000\u0000\u000fX\u0001\u0000\u0000\u0000\u0011"+
		"g\u0001\u0000\u0000\u0000\u0013u\u0001\u0000\u0000\u0000\u0015|\u0001"+
		"\u0000\u0000\u0000\u0017\u008a\u0001\u0000\u0000\u0000\u0019\u0094\u0001"+
		"\u0000\u0000\u0000\u001b\u009b\u0001\u0000\u0000\u0000\u001d\u00a0\u0001"+
		"\u0000\u0000\u0000\u001f \u0005c\u0000\u0000 !\u0005o\u0000\u0000!\"\u0005"+
		"d\u0000\u0000\"#\u0005e\u0000\u0000#$\u0005:\u0000\u0000$%\u0001\u0000"+
		"\u0000\u0000%&\u0006\u0000\u0000\u0000&\u0004\u0001\u0000\u0000\u0000"+
		"\'(\u0005n\u0000\u0000()\u0005a\u0000\u0000)*\u0005m\u0000\u0000*+\u0005"+
		"e\u0000\u0000+,\u0005:\u0000\u0000,-\u0001\u0000\u0000\u0000-.\u0006\u0001"+
		"\u0000\u0000.\u0006\u0001\u0000\u0000\u0000/0\u0005m\u0000\u000001\u0005"+
		"u\u0000\u000012\u0005l\u0000\u000023\u0005t\u0000\u000034\u0005i\u0000"+
		"\u000045\u0005p\u0000\u000056\u0005l\u0000\u000067\u0005i\u0000\u0000"+
		"78\u0005e\u0000\u000089\u0005r\u0000\u00009:\u0005:\u0000\u0000:\b\u0001"+
		"\u0000\u0000\u0000;<\u0005o\u0000\u0000<=\u0005-\u0000\u0000=>\u0005m"+
		"\u0000\u0000>?\u0005u\u0000\u0000?@\u0005l\u0000\u0000@A\u0005t\u0000"+
		"\u0000AB\u0005i\u0000\u0000BC\u0005p\u0000\u0000CD\u0005l\u0000\u0000"+
		"DE\u0005i\u0000\u0000EF\u0005e\u0000\u0000FG\u0005r\u0000\u0000GH\u0005"+
		":\u0000\u0000H\n\u0001\u0000\u0000\u0000IJ\u0005p\u0000\u0000JK\u0005"+
		"o\u0000\u0000KL\u0005w\u0000\u0000LM\u0005e\u0000\u0000MN\u0005r\u0000"+
		"\u0000NO\u0005:\u0000\u0000O\f\u0001\u0000\u0000\u0000PQ\u0005v\u0000"+
		"\u0000QR\u0005e\u0000\u0000RS\u0005r\u0000\u0000ST\u0005b\u0000\u0000"+
		"TU\u0005:\u0000\u0000UV\u0001\u0000\u0000\u0000VW\u0006\u0005\u0000\u0000"+
		"W\u000e\u0001\u0000\u0000\u0000XY\u0005r\u0000\u0000YZ\u0005e\u0000\u0000"+
		"Z[\u0005s\u0000\u0000[\\\u0005i\u0000\u0000\\]\u0005s\u0000\u0000]^\u0005"+
		"t\u0000\u0000^_\u0005-\u0000\u0000_`\u0005f\u0000\u0000`a\u0005l\u0000"+
		"\u0000ab\u0005a\u0000\u0000bc\u0005g\u0000\u0000cd\u0005:\u0000\u0000"+
		"de\u0001\u0000\u0000\u0000ef\u0006\u0006\u0001\u0000f\u0010\u0001\u0000"+
		"\u0000\u0000gh\u0005v\u0000\u0000hi\u0005u\u0000\u0000ij\u0005l\u0000"+
		"\u0000jk\u0005n\u0000\u0000kl\u0005-\u0000\u0000lm\u0005f\u0000\u0000"+
		"mn\u0005l\u0000\u0000no\u0005a\u0000\u0000op\u0005g\u0000\u0000pq\u0005"+
		":\u0000\u0000qr\u0001\u0000\u0000\u0000rs\u0006\u0007\u0001\u0000s\u0012"+
		"\u0001\u0000\u0000\u0000tv\u0005-\u0000\u0000ut\u0001\u0000\u0000\u0000"+
		"uv\u0001\u0000\u0000\u0000vx\u0001\u0000\u0000\u0000wy\u000209\u0000x"+
		"w\u0001\u0000\u0000\u0000yz\u0001\u0000\u0000\u0000zx\u0001\u0000\u0000"+
		"\u0000z{\u0001\u0000\u0000\u0000{\u0014\u0001\u0000\u0000\u0000|\u0080"+
		"\u0005#\u0000\u0000}\u007f\b\u0000\u0000\u0000~}\u0001\u0000\u0000\u0000"+
		"\u007f\u0082\u0001\u0000\u0000\u0000\u0080~\u0001\u0000\u0000\u0000\u0080"+
		"\u0081\u0001\u0000\u0000\u0000\u0081\u0083\u0001\u0000\u0000\u0000\u0082"+
		"\u0080\u0001\u0000\u0000\u0000\u0083\u0084\u0005\n\u0000\u0000\u0084\u0085"+
		"\u0001\u0000\u0000\u0000\u0085\u0086\u0006\t\u0002\u0000\u0086\u0016\u0001"+
		"\u0000\u0000\u0000\u0087\u0089\u0005 \u0000\u0000\u0088\u0087\u0001\u0000"+
		"\u0000\u0000\u0089\u008c\u0001\u0000\u0000\u0000\u008a\u0088\u0001\u0000"+
		"\u0000\u0000\u008a\u008b\u0001\u0000\u0000\u0000\u008b\u008e\u0001\u0000"+
		"\u0000\u0000\u008c\u008a\u0001\u0000\u0000\u0000\u008d\u008f\u0005\r\u0000"+
		"\u0000\u008e\u008d\u0001\u0000\u0000\u0000\u008e\u008f\u0001\u0000\u0000"+
		"\u0000\u008f\u0090\u0001\u0000\u0000\u0000\u0090\u0091\u0005\n\u0000\u0000"+
		"\u0091\u0092\u0001\u0000\u0000\u0000\u0092\u0093\u0006\n\u0002\u0000\u0093"+
		"\u0018\u0001\u0000\u0000\u0000\u0094\u0098\u0002AZ\u0000\u0095\u0097\u0007"+
		"\u0001\u0000\u0000\u0096\u0095\u0001\u0000\u0000\u0000\u0097\u009a\u0001"+
		"\u0000\u0000\u0000\u0098\u0096\u0001\u0000\u0000\u0000\u0098\u0099\u0001"+
		"\u0000\u0000\u0000\u0099\u001a\u0001\u0000\u0000\u0000\u009a\u0098\u0001"+
		"\u0000\u0000\u0000\u009b\u009c\u0003\u0019\u000b\u0000\u009c\u009d\u0001"+
		"\u0000\u0000\u0000\u009d\u009e\u0006\f\u0003\u0000\u009e\u001c\u0001\u0000"+
		"\u0000\u0000\u009f\u00a1\b\u0002\u0000\u0000\u00a0\u009f\u0001\u0000\u0000"+
		"\u0000\u00a1\u00a2\u0001\u0000\u0000\u0000\u00a2\u00a0\u0001\u0000\u0000"+
		"\u0000\u00a2\u00a3\u0001\u0000\u0000\u0000\u00a3\u00a4\u0001\u0000\u0000"+
		"\u0000\u00a4\u00a5\u0006\r\u0003\u0000\u00a5\u001e\u0001\u0000\u0000\u0000"+
		"\n\u0000\u0001\u0002uz\u0080\u008a\u008e\u0098\u00a2\u0004\u0005\u0002"+
		"\u0000\u0005\u0001\u0000\u0006\u0000\u0000\u0004\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}