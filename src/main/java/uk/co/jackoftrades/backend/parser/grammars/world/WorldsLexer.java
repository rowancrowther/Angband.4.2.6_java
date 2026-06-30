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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/WorldsLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.world;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class WorldsLexer extends Lexer {
	static {
		RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
	}

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
			new PredictionContextCache();
	public static final int
			RECORD_COUNT = 1, LEVEL = 2, COLON = 3, INTEGER = 4, NAME = 5, COMMENT = 6, EOL = 7;
	public static String[] channelNames = {
			"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
			"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[]{
				"RECORD_COUNT", "LEVEL", "COLON", "INTEGER", "NAME", "COMMENT", "EOL"
		};
	}

	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[]{
				null, "'record-count:'", "'level:'", "':'"
		};
	}

	private static final String[] _LITERAL_NAMES = makeLiteralNames();

	private static String[] makeSymbolicNames() {
		return new String[]{
				null, "RECORD_COUNT", "LEVEL", "COLON", "INTEGER", "NAME", "COMMENT",
				"EOL"
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


	public WorldsLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
	}

	@Override
	public String getGrammarFileName() {
		return "WorldsLexer.g4";
	}

	@Override
	public String[] getRuleNames() {
		return ruleNames;
	}

	@Override
	public String getSerializedATN() {
		return _serializedATN;
	}

	@Override
	public String[] getChannelNames() {
		return channelNames;
	}

	@Override
	public String[] getModeNames() {
		return modeNames;
	}

	@Override
	public ATN getATN() {
		return _ATN;
	}

	public static final String _serializedATN =
			"\u0004\u0000\u0007K\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001" +
					"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004" +
					"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0001\u0000" +
					"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
					"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000" +
					"\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
					"\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0003\u0003\u0003" +
					"(\b\u0003\u0001\u0003\u0004\u0003+\b\u0003\u000b\u0003\f\u0003,\u0001" +
					"\u0004\u0004\u00040\b\u0004\u000b\u0004\f\u00041\u0001\u0005\u0001\u0005" +
					"\u0005\u00056\b\u0005\n\u0005\f\u00059\t\u0005\u0001\u0005\u0001\u0005" +
					"\u0001\u0005\u0001\u0005\u0001\u0006\u0005\u0006@\b\u0006\n\u0006\f\u0006" +
					"C\t\u0006\u0001\u0006\u0003\u0006F\b\u0006\u0001\u0006\u0001\u0006\u0001" +
					"\u0006\u0001\u0006\u0000\u0000\u0007\u0001\u0001\u0003\u0002\u0005\u0003" +
					"\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u0001\u0000\u0002\u0004\u0000" +
					"  09AZaz\u0001\u0000\n\nP\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003" +
					"\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007" +
					"\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001" +
					"\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0001\u000f\u0001\u0000" +
					"\u0000\u0000\u0003\u001d\u0001\u0000\u0000\u0000\u0005$\u0001\u0000\u0000" +
					"\u0000\u0007\'\u0001\u0000\u0000\u0000\t/\u0001\u0000\u0000\u0000\u000b" +
					"3\u0001\u0000\u0000\u0000\rA\u0001\u0000\u0000\u0000\u000f\u0010\u0005" +
					"r\u0000\u0000\u0010\u0011\u0005e\u0000\u0000\u0011\u0012\u0005c\u0000" +
					"\u0000\u0012\u0013\u0005o\u0000\u0000\u0013\u0014\u0005r\u0000\u0000\u0014" +
					"\u0015\u0005d\u0000\u0000\u0015\u0016\u0005-\u0000\u0000\u0016\u0017\u0005" +
					"c\u0000\u0000\u0017\u0018\u0005o\u0000\u0000\u0018\u0019\u0005u\u0000" +
					"\u0000\u0019\u001a\u0005n\u0000\u0000\u001a\u001b\u0005t\u0000\u0000\u001b" +
					"\u001c\u0005:\u0000\u0000\u001c\u0002\u0001\u0000\u0000\u0000\u001d\u001e" +
					"\u0005l\u0000\u0000\u001e\u001f\u0005e\u0000\u0000\u001f \u0005v\u0000" +
					"\u0000 !\u0005e\u0000\u0000!\"\u0005l\u0000\u0000\"#\u0005:\u0000\u0000" +
					"#\u0004\u0001\u0000\u0000\u0000$%\u0005:\u0000\u0000%\u0006\u0001\u0000" +
					"\u0000\u0000&(\u0005-\u0000\u0000\'&\u0001\u0000\u0000\u0000\'(\u0001" +
					"\u0000\u0000\u0000(*\u0001\u0000\u0000\u0000)+\u000209\u0000*)\u0001\u0000" +
					"\u0000\u0000+,\u0001\u0000\u0000\u0000,*\u0001\u0000\u0000\u0000,-\u0001" +
					"\u0000\u0000\u0000-\b\u0001\u0000\u0000\u0000.0\u0007\u0000\u0000\u0000" +
					"/.\u0001\u0000\u0000\u000001\u0001\u0000\u0000\u00001/\u0001\u0000\u0000" +
					"\u000012\u0001\u0000\u0000\u00002\n\u0001\u0000\u0000\u000037\u0005#\u0000" +
					"\u000046\b\u0001\u0000\u000054\u0001\u0000\u0000\u000069\u0001\u0000\u0000" +
					"\u000075\u0001\u0000\u0000\u000078\u0001\u0000\u0000\u00008:\u0001\u0000" +
					"\u0000\u000097\u0001\u0000\u0000\u0000:;\u0005\n\u0000\u0000;<\u0001\u0000" +
					"\u0000\u0000<=\u0006\u0005\u0000\u0000=\f\u0001\u0000\u0000\u0000>@\u0005" +
					" \u0000\u0000?>\u0001\u0000\u0000\u0000@C\u0001\u0000\u0000\u0000A?\u0001" +
					"\u0000\u0000\u0000AB\u0001\u0000\u0000\u0000BE\u0001\u0000\u0000\u0000" +
					"CA\u0001\u0000\u0000\u0000DF\u0005\r\u0000\u0000ED\u0001\u0000\u0000\u0000" +
					"EF\u0001\u0000\u0000\u0000FG\u0001\u0000\u0000\u0000GH\u0005\n\u0000\u0000" +
					"HI\u0001\u0000\u0000\u0000IJ\u0006\u0006\u0000\u0000J\u000e\u0001\u0000" +
					"\u0000\u0000\u0007\u0000\',17AE\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
			new ATNDeserializer().deserialize(_serializedATN.toCharArray());

	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}