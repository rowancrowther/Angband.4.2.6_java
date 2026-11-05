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
// Generated from /home/rowan/IdeaProjects/Angband.4.2.6_java/src/main/java/uk/co/jackoftradesltd/backend/parser/grammars/NamesLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.names;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class NamesLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		SECTION=1, WORD=2, INTEGER=3, COMMENT=4, EOL=5, STRING=6;
	public static final int
		STRING_MODE=1;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "STRING_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"SECTION", "WORD", "INTEGER", "COMMENT", "EOL", "STRING"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'section:'", "'word:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "SECTION", "WORD", "INTEGER", "COMMENT", "EOL", "STRING"
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


	public NamesLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "NamesLexer.g4"; }

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
		"\u0004\u0000\u0006F\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000\u0007"+
		"\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007"+
		"\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0003\u0002!\b"+
		"\u0002\u0001\u0002\u0004\u0002$\b\u0002\u000b\u0002\f\u0002%\u0001\u0003"+
		"\u0001\u0003\u0005\u0003*\b\u0003\n\u0003\f\u0003-\t\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0005\u00044\b\u0004"+
		"\n\u0004\f\u00047\t\u0004\u0001\u0004\u0003\u0004:\b\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0004\u0005A\b\u0005"+
		"\u000b\u0005\f\u0005B\u0001\u0005\u0001\u0005\u0000\u0000\u0006\u0002"+
		"\u0001\u0004\u0002\u0006\u0003\b\u0004\n\u0005\f\u0006\u0002\u0000\u0001"+
		"\u0002\u0001\u0000\n\n\u0002\u0000\n\n\r\rJ\u0000\u0002\u0001\u0000\u0000"+
		"\u0000\u0000\u0004\u0001\u0000\u0000\u0000\u0000\u0006\u0001\u0000\u0000"+
		"\u0000\u0000\b\u0001\u0000\u0000\u0000\u0000\n\u0001\u0000\u0000\u0000"+
		"\u0001\f\u0001\u0000\u0000\u0000\u0002\u000e\u0001\u0000\u0000\u0000\u0004"+
		"\u0017\u0001\u0000\u0000\u0000\u0006 \u0001\u0000\u0000\u0000\b\'\u0001"+
		"\u0000\u0000\u0000\n5\u0001\u0000\u0000\u0000\f@\u0001\u0000\u0000\u0000"+
		"\u000e\u000f\u0005s\u0000\u0000\u000f\u0010\u0005e\u0000\u0000\u0010\u0011"+
		"\u0005c\u0000\u0000\u0011\u0012\u0005t\u0000\u0000\u0012\u0013\u0005i"+
		"\u0000\u0000\u0013\u0014\u0005o\u0000\u0000\u0014\u0015\u0005n\u0000\u0000"+
		"\u0015\u0016\u0005:\u0000\u0000\u0016\u0003\u0001\u0000\u0000\u0000\u0017"+
		"\u0018\u0005w\u0000\u0000\u0018\u0019\u0005o\u0000\u0000\u0019\u001a\u0005"+
		"r\u0000\u0000\u001a\u001b\u0005d\u0000\u0000\u001b\u001c\u0005:\u0000"+
		"\u0000\u001c\u001d\u0001\u0000\u0000\u0000\u001d\u001e\u0006\u0001\u0000"+
		"\u0000\u001e\u0005\u0001\u0000\u0000\u0000\u001f!\u0005-\u0000\u0000 "+
		"\u001f\u0001\u0000\u0000\u0000 !\u0001\u0000\u0000\u0000!#\u0001\u0000"+
		"\u0000\u0000\"$\u000209\u0000#\"\u0001\u0000\u0000\u0000$%\u0001\u0000"+
		"\u0000\u0000%#\u0001\u0000\u0000\u0000%&\u0001\u0000\u0000\u0000&\u0007"+
		"\u0001\u0000\u0000\u0000\'+\u0005#\u0000\u0000(*\b\u0000\u0000\u0000)"+
		"(\u0001\u0000\u0000\u0000*-\u0001\u0000\u0000\u0000+)\u0001\u0000\u0000"+
		"\u0000+,\u0001\u0000\u0000\u0000,.\u0001\u0000\u0000\u0000-+\u0001\u0000"+
		"\u0000\u0000./\u0005\n\u0000\u0000/0\u0001\u0000\u0000\u000001\u0006\u0003"+
		"\u0001\u00001\t\u0001\u0000\u0000\u000024\u0005 \u0000\u000032\u0001\u0000"+
		"\u0000\u000047\u0001\u0000\u0000\u000053\u0001\u0000\u0000\u000056\u0001"+
		"\u0000\u0000\u000069\u0001\u0000\u0000\u000075\u0001\u0000\u0000\u0000"+
		"8:\u0005\r\u0000\u000098\u0001\u0000\u0000\u00009:\u0001\u0000\u0000\u0000"+
		":;\u0001\u0000\u0000\u0000;<\u0005\n\u0000\u0000<=\u0001\u0000\u0000\u0000"+
		"=>\u0006\u0004\u0001\u0000>\u000b\u0001\u0000\u0000\u0000?A\b\u0001\u0000"+
		"\u0000@?\u0001\u0000\u0000\u0000AB\u0001\u0000\u0000\u0000B@\u0001\u0000"+
		"\u0000\u0000BC\u0001\u0000\u0000\u0000CD\u0001\u0000\u0000\u0000DE\u0006"+
		"\u0005\u0002\u0000E\r\u0001\u0000\u0000\u0000\b\u0000\u0001 %+59B\u0003"+
		"\u0005\u0001\u0000\u0006\u0000\u0000\u0004\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}