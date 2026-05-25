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
 *    Java code copyright (c) Rowan Crowther 2026
 */

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/UIEntryBaseLexer.g4 by ANTLR 4.13.2

package uk.co.jackoftrades.backend.parser.uientrybase;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class UIEntryBaseLexer extends Lexer {
	static {
		RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
	}

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
			new PredictionContextCache();
	public static final int
			COMMENT = 1, EOL = 2, NAME = 3, RENDERER = 4, COMBINE = 5, CATEGORY = 6, FLAGS = 7,
			DESC = 8, LCASEWORD = 9, UCASEWORD = 10, DESC_TEXT = 11, DESC_EOL = 12;
	public static final int
			DESC_MODE = 1;
	public static String[] channelNames = {
			"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
			"DEFAULT_MODE", "DESC_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[]{
				"COMMENT", "EOL", "NAME", "RENDERER", "COMBINE", "CATEGORY", "FLAGS",
				"DESC", "LCASEWORD", "UCASEWORD", "DESC_TEXT", "DESC_EOL"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[]{
				null, null, null, "'name:'", "'renderer:'", "'combine:'", "'category:'",
				"'flags:'", "'desc:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[]{
				null, "COMMENT", "EOL", "NAME", "RENDERER", "COMBINE", "CATEGORY", "FLAGS",
				"DESC", "LCASEWORD", "UCASEWORD", "DESC_TEXT", "DESC_EOL"
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


	public UIEntryBaseLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
	}

	@Override
	public String getGrammarFileName() {
		return "UIEntryBaseLexer.g4";
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
			"\u0004\u0000\f~\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000\u0007" +
					"\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007" +
					"\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007" +
					"\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n" +
					"\u0007\n\u0002\u000b\u0007\u000b\u0001\u0000\u0001\u0000\u0005\u0000\u001d" +
					"\b\u0000\n\u0000\f\u0000 \t\u0000\u0001\u0000\u0004\u0000#\b\u0000\u000b" +
					"\u0000\f\u0000$\u0001\u0000\u0001\u0000\u0001\u0001\u0005\u0001*\b\u0001" +
					"\n\u0001\f\u0001-\t\u0001\u0001\u0001\u0003\u00010\b\u0001\u0001\u0001" +
					"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002" +
					"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003" +
					"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003" +
					"\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004" +
					"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005" +
					"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005" +
					"\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006" +
					"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007" +
					"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0004" +
					"\bi\b\b\u000b\b\f\bj\u0001\t\u0004\tn\b\t\u000b\t\f\to\u0001\n\u0004\n" +
					"s\b\n\u000b\n\f\nt\u0001\u000b\u0003\u000bx\b\u000b\u0001\u000b\u0001" +
					"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0000\u0000\f\u0002\u0001\u0004" +
					"\u0002\u0006\u0003\b\u0004\n\u0005\f\u0006\u000e\u0007\u0010\b\u0012\t" +
					"\u0014\n\u0016\u000b\u0018\f\u0002\u0000\u0001\u0004\u0001\u0000\n\n\u0003" +
					"\u000009__az\u0003\u000009AZ__\u0002\u0000\n\n\r\r\u0084\u0000\u0002\u0001" +
					"\u0000\u0000\u0000\u0000\u0004\u0001\u0000\u0000\u0000\u0000\u0006\u0001" +
					"\u0000\u0000\u0000\u0000\b\u0001\u0000\u0000\u0000\u0000\n\u0001\u0000" +
					"\u0000\u0000\u0000\f\u0001\u0000\u0000\u0000\u0000\u000e\u0001\u0000\u0000" +
					"\u0000\u0000\u0010\u0001\u0000\u0000\u0000\u0000\u0012\u0001\u0000\u0000" +
					"\u0000\u0000\u0014\u0001\u0000\u0000\u0000\u0001\u0016\u0001\u0000\u0000" +
					"\u0000\u0001\u0018\u0001\u0000\u0000\u0000\u0002\u001a\u0001\u0000\u0000" +
					"\u0000\u0004+\u0001\u0000\u0000\u0000\u00065\u0001\u0000\u0000\u0000\b" +
					";\u0001\u0000\u0000\u0000\nE\u0001\u0000\u0000\u0000\fN\u0001\u0000\u0000" +
					"\u0000\u000eX\u0001\u0000\u0000\u0000\u0010_\u0001\u0000\u0000\u0000\u0012" +
					"h\u0001\u0000\u0000\u0000\u0014m\u0001\u0000\u0000\u0000\u0016r\u0001" +
					"\u0000\u0000\u0000\u0018w\u0001\u0000\u0000\u0000\u001a\u001e\u0005#\u0000" +
					"\u0000\u001b\u001d\b\u0000\u0000\u0000\u001c\u001b\u0001\u0000\u0000\u0000" +
					"\u001d \u0001\u0000\u0000\u0000\u001e\u001c\u0001\u0000\u0000\u0000\u001e" +
					"\u001f\u0001\u0000\u0000\u0000\u001f\"\u0001\u0000\u0000\u0000 \u001e" +
					"\u0001\u0000\u0000\u0000!#\u0005\n\u0000\u0000\"!\u0001\u0000\u0000\u0000" +
					"#$\u0001\u0000\u0000\u0000$\"\u0001\u0000\u0000\u0000$%\u0001\u0000\u0000" +
					"\u0000%&\u0001\u0000\u0000\u0000&\'\u0006\u0000\u0000\u0000\'\u0003\u0001" +
					"\u0000\u0000\u0000(*\u0005 \u0000\u0000)(\u0001\u0000\u0000\u0000*-\u0001" +
					"\u0000\u0000\u0000+)\u0001\u0000\u0000\u0000+,\u0001\u0000\u0000\u0000" +
					",/\u0001\u0000\u0000\u0000-+\u0001\u0000\u0000\u0000.0\u0005\r\u0000\u0000" +
					"/.\u0001\u0000\u0000\u0000/0\u0001\u0000\u0000\u000001\u0001\u0000\u0000" +
					"\u000012\u0005\n\u0000\u000023\u0001\u0000\u0000\u000034\u0006\u0001\u0000" +
					"\u00004\u0005\u0001\u0000\u0000\u000056\u0005n\u0000\u000067\u0005a\u0000" +
					"\u000078\u0005m\u0000\u000089\u0005e\u0000\u00009:\u0005:\u0000\u0000" +
					":\u0007\u0001\u0000\u0000\u0000;<\u0005r\u0000\u0000<=\u0005e\u0000\u0000" +
					"=>\u0005n\u0000\u0000>?\u0005d\u0000\u0000?@\u0005e\u0000\u0000@A\u0005" +
					"r\u0000\u0000AB\u0005e\u0000\u0000BC\u0005r\u0000\u0000CD\u0005:\u0000" +
					"\u0000D\t\u0001\u0000\u0000\u0000EF\u0005c\u0000\u0000FG\u0005o\u0000" +
					"\u0000GH\u0005m\u0000\u0000HI\u0005b\u0000\u0000IJ\u0005i\u0000\u0000" +
					"JK\u0005n\u0000\u0000KL\u0005e\u0000\u0000LM\u0005:\u0000\u0000M\u000b" +
					"\u0001\u0000\u0000\u0000NO\u0005c\u0000\u0000OP\u0005a\u0000\u0000PQ\u0005" +
					"t\u0000\u0000QR\u0005e\u0000\u0000RS\u0005g\u0000\u0000ST\u0005o\u0000" +
					"\u0000TU\u0005r\u0000\u0000UV\u0005y\u0000\u0000VW\u0005:\u0000\u0000" +
					"W\r\u0001\u0000\u0000\u0000XY\u0005f\u0000\u0000YZ\u0005l\u0000\u0000" +
					"Z[\u0005a\u0000\u0000[\\\u0005g\u0000\u0000\\]\u0005s\u0000\u0000]^\u0005" +
					":\u0000\u0000^\u000f\u0001\u0000\u0000\u0000_`\u0005d\u0000\u0000`a\u0005" +
					"e\u0000\u0000ab\u0005s\u0000\u0000bc\u0005c\u0000\u0000cd\u0005:\u0000" +
					"\u0000de\u0001\u0000\u0000\u0000ef\u0006\u0007\u0001\u0000f\u0011\u0001" +
					"\u0000\u0000\u0000gi\u0007\u0001\u0000\u0000hg\u0001\u0000\u0000\u0000" +
					"ij\u0001\u0000\u0000\u0000jh\u0001\u0000\u0000\u0000jk\u0001\u0000\u0000" +
					"\u0000k\u0013\u0001\u0000\u0000\u0000ln\u0007\u0002\u0000\u0000ml\u0001" +
					"\u0000\u0000\u0000no\u0001\u0000\u0000\u0000om\u0001\u0000\u0000\u0000" +
					"op\u0001\u0000\u0000\u0000p\u0015\u0001\u0000\u0000\u0000qs\b\u0003\u0000" +
					"\u0000rq\u0001\u0000\u0000\u0000st\u0001\u0000\u0000\u0000tr\u0001\u0000" +
					"\u0000\u0000tu\u0001\u0000\u0000\u0000u\u0017\u0001\u0000\u0000\u0000" +
					"vx\u0005\r\u0000\u0000wv\u0001\u0000\u0000\u0000wx\u0001\u0000\u0000\u0000" +
					"xy\u0001\u0000\u0000\u0000yz\u0005\n\u0000\u0000z{\u0001\u0000\u0000\u0000" +
					"{|\u0006\u000b\u0002\u0000|}\u0006\u000b\u0000\u0000}\u0019\u0001\u0000" +
					"\u0000\u0000\n\u0000\u0001\u001e$+/jotw\u0003\u0006\u0000\u0000\u0005" +
					"\u0001\u0000\u0004\u0000\u0000";
	public static final ATN _ATN =
			new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}