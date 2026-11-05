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

// Generated from /home/rowan/IdeaProjects/Angband.4.2.6_java/src/main/java/uk/co/jackoftradesltd/backend/parser/grammars/FlavourLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.flavour;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class FlavourLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		KIND=1, FIXED=2, FLAVOUR=3, INTEGER=4, COMMENT=5, EOL=6, STRING=7, COLON=8, 
		DELIM_EOL=9;
	public static final int
		DELIM=1;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "DELIM"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"KIND", "FIXED", "FLAVOUR", "INTEGER", "COMMENT", "EOL", "STRING", "COLON", 
			"DELIM_EOL"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'kind:'", "'fixed:'", "'flavor:'", null, null, null, null, "':'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "KIND", "FIXED", "FLAVOUR", "INTEGER", "COMMENT", "EOL", "STRING", 
			"COLON", "DELIM_EOL"
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


	public FlavourLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "FlavourLexer.g4"; }

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
		"\u0004\u0000\ta\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000\u0007"+
		"\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007"+
		"\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007"+
		"\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0003\u0003\u00031\b\u0003\u0001\u0003\u0004\u0003"+
		"4\b\u0003\u000b\u0003\f\u00035\u0001\u0004\u0001\u0004\u0005\u0004:\b"+
		"\u0004\n\u0004\f\u0004=\t\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0005\u0005\u0005D\b\u0005\n\u0005\f\u0005G\t\u0005\u0001"+
		"\u0005\u0003\u0005J\b\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0006\u0004\u0006Q\b\u0006\u000b\u0006\f\u0006R\u0001\u0007"+
		"\u0001\u0007\u0001\b\u0005\bX\b\b\n\b\f\b[\t\b\u0001\b\u0001\b\u0001\b"+
		"\u0001\b\u0001\b\u0000\u0000\t\u0002\u0001\u0004\u0002\u0006\u0003\b\u0004"+
		"\n\u0005\f\u0006\u000e\u0007\u0010\b\u0012\t\u0002\u0000\u0001\u0002\u0001"+
		"\u0000\n\n\u0003\u0000\n\n\r\r::f\u0000\u0002\u0001\u0000\u0000\u0000"+
		"\u0000\u0004\u0001\u0000\u0000\u0000\u0000\u0006\u0001\u0000\u0000\u0000"+
		"\u0000\b\u0001\u0000\u0000\u0000\u0000\n\u0001\u0000\u0000\u0000\u0000"+
		"\f\u0001\u0000\u0000\u0000\u0001\u000e\u0001\u0000\u0000\u0000\u0001\u0010"+
		"\u0001\u0000\u0000\u0000\u0001\u0012\u0001\u0000\u0000\u0000\u0002\u0014"+
		"\u0001\u0000\u0000\u0000\u0004\u001c\u0001\u0000\u0000\u0000\u0006%\u0001"+
		"\u0000\u0000\u0000\b0\u0001\u0000\u0000\u0000\n7\u0001\u0000\u0000\u0000"+
		"\fE\u0001\u0000\u0000\u0000\u000eP\u0001\u0000\u0000\u0000\u0010T\u0001"+
		"\u0000\u0000\u0000\u0012Y\u0001\u0000\u0000\u0000\u0014\u0015\u0005k\u0000"+
		"\u0000\u0015\u0016\u0005i\u0000\u0000\u0016\u0017\u0005n\u0000\u0000\u0017"+
		"\u0018\u0005d\u0000\u0000\u0018\u0019\u0005:\u0000\u0000\u0019\u001a\u0001"+
		"\u0000\u0000\u0000\u001a\u001b\u0006\u0000\u0000\u0000\u001b\u0003\u0001"+
		"\u0000\u0000\u0000\u001c\u001d\u0005f\u0000\u0000\u001d\u001e\u0005i\u0000"+
		"\u0000\u001e\u001f\u0005x\u0000\u0000\u001f \u0005e\u0000\u0000 !\u0005"+
		"d\u0000\u0000!\"\u0005:\u0000\u0000\"#\u0001\u0000\u0000\u0000#$\u0006"+
		"\u0001\u0000\u0000$\u0005\u0001\u0000\u0000\u0000%&\u0005f\u0000\u0000"+
		"&\'\u0005l\u0000\u0000\'(\u0005a\u0000\u0000()\u0005v\u0000\u0000)*\u0005"+
		"o\u0000\u0000*+\u0005r\u0000\u0000+,\u0005:\u0000\u0000,-\u0001\u0000"+
		"\u0000\u0000-.\u0006\u0002\u0000\u0000.\u0007\u0001\u0000\u0000\u0000"+
		"/1\u0005-\u0000\u00000/\u0001\u0000\u0000\u000001\u0001\u0000\u0000\u0000"+
		"13\u0001\u0000\u0000\u000024\u000209\u000032\u0001\u0000\u0000\u00004"+
		"5\u0001\u0000\u0000\u000053\u0001\u0000\u0000\u000056\u0001\u0000\u0000"+
		"\u00006\t\u0001\u0000\u0000\u00007;\u0005#\u0000\u00008:\b\u0000\u0000"+
		"\u000098\u0001\u0000\u0000\u0000:=\u0001\u0000\u0000\u0000;9\u0001\u0000"+
		"\u0000\u0000;<\u0001\u0000\u0000\u0000<>\u0001\u0000\u0000\u0000=;\u0001"+
		"\u0000\u0000\u0000>?\u0005\n\u0000\u0000?@\u0001\u0000\u0000\u0000@A\u0006"+
		"\u0004\u0001\u0000A\u000b\u0001\u0000\u0000\u0000BD\u0005 \u0000\u0000"+
		"CB\u0001\u0000\u0000\u0000DG\u0001\u0000\u0000\u0000EC\u0001\u0000\u0000"+
		"\u0000EF\u0001\u0000\u0000\u0000FI\u0001\u0000\u0000\u0000GE\u0001\u0000"+
		"\u0000\u0000HJ\u0005\r\u0000\u0000IH\u0001\u0000\u0000\u0000IJ\u0001\u0000"+
		"\u0000\u0000JK\u0001\u0000\u0000\u0000KL\u0005\n\u0000\u0000LM\u0001\u0000"+
		"\u0000\u0000MN\u0006\u0005\u0001\u0000N\r\u0001\u0000\u0000\u0000OQ\b"+
		"\u0001\u0000\u0000PO\u0001\u0000\u0000\u0000QR\u0001\u0000\u0000\u0000"+
		"RP\u0001\u0000\u0000\u0000RS\u0001\u0000\u0000\u0000S\u000f\u0001\u0000"+
		"\u0000\u0000TU\u0005:\u0000\u0000U\u0011\u0001\u0000\u0000\u0000VX\u0005"+
		"\r\u0000\u0000WV\u0001\u0000\u0000\u0000X[\u0001\u0000\u0000\u0000YW\u0001"+
		"\u0000\u0000\u0000YZ\u0001\u0000\u0000\u0000Z\\\u0001\u0000\u0000\u0000"+
		"[Y\u0001\u0000\u0000\u0000\\]\u0005\n\u0000\u0000]^\u0001\u0000\u0000"+
		"\u0000^_\u0006\b\u0002\u0000_`\u0006\b\u0001\u0000`\u0013\u0001\u0000"+
		"\u0000\u0000\t\u0000\u000105;EIRY\u0003\u0005\u0001\u0000\u0006\u0000"+
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