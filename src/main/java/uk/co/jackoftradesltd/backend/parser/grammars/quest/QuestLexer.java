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
// Generated from /home/rowan/IdeaProjects/Angband.4.2.6_java/src/main/java/uk/co/jackoftradesltd/backend/parser/grammars/QuestLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.quest;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class QuestLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, LEVEL=2, RACE=3, NUMBER=4, INTEGER=5, COMMENT=6, EOL=7, STRING=8;
	public static final int
		REST_OF_LINE=1;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "REST_OF_LINE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"NAME", "LEVEL", "RACE", "NUMBER", "INTEGER", "COMMENT", "EOL", "STRING"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'level:'", "'race:'", "'number:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "LEVEL", "RACE", "NUMBER", "INTEGER", "COMMENT", "EOL", 
			"STRING"
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


	public QuestLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "QuestLexer.g4"; }

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
		"\u0004\u0000\bX\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000\u0007"+
		"\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007"+
		"\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007"+
		"\u0006\u0002\u0007\u0007\u0007\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0003\u00043\b"+
		"\u0004\u0001\u0004\u0004\u00046\b\u0004\u000b\u0004\f\u00047\u0001\u0005"+
		"\u0001\u0005\u0005\u0005<\b\u0005\n\u0005\f\u0005?\t\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0005\u0006F\b\u0006"+
		"\n\u0006\f\u0006I\t\u0006\u0001\u0006\u0003\u0006L\b\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0004\u0007S\b\u0007"+
		"\u000b\u0007\f\u0007T\u0001\u0007\u0001\u0007\u0000\u0000\b\u0002\u0001"+
		"\u0004\u0002\u0006\u0003\b\u0004\n\u0005\f\u0006\u000e\u0007\u0010\b\u0002"+
		"\u0000\u0001\u0002\u0001\u0000\n\n\u0002\u0000\n\n\r\r\\\u0000\u0002\u0001"+
		"\u0000\u0000\u0000\u0000\u0004\u0001\u0000\u0000\u0000\u0000\u0006\u0001"+
		"\u0000\u0000\u0000\u0000\b\u0001\u0000\u0000\u0000\u0000\n\u0001\u0000"+
		"\u0000\u0000\u0000\f\u0001\u0000\u0000\u0000\u0000\u000e\u0001\u0000\u0000"+
		"\u0000\u0001\u0010\u0001\u0000\u0000\u0000\u0002\u0012\u0001\u0000\u0000"+
		"\u0000\u0004\u001a\u0001\u0000\u0000\u0000\u0006!\u0001\u0000\u0000\u0000"+
		"\b)\u0001\u0000\u0000\u0000\n2\u0001\u0000\u0000\u0000\f9\u0001\u0000"+
		"\u0000\u0000\u000eG\u0001\u0000\u0000\u0000\u0010R\u0001\u0000\u0000\u0000"+
		"\u0012\u0013\u0005n\u0000\u0000\u0013\u0014\u0005a\u0000\u0000\u0014\u0015"+
		"\u0005m\u0000\u0000\u0015\u0016\u0005e\u0000\u0000\u0016\u0017\u0005:"+
		"\u0000\u0000\u0017\u0018\u0001\u0000\u0000\u0000\u0018\u0019\u0006\u0000"+
		"\u0000\u0000\u0019\u0003\u0001\u0000\u0000\u0000\u001a\u001b\u0005l\u0000"+
		"\u0000\u001b\u001c\u0005e\u0000\u0000\u001c\u001d\u0005v\u0000\u0000\u001d"+
		"\u001e\u0005e\u0000\u0000\u001e\u001f\u0005l\u0000\u0000\u001f \u0005"+
		":\u0000\u0000 \u0005\u0001\u0000\u0000\u0000!\"\u0005r\u0000\u0000\"#"+
		"\u0005a\u0000\u0000#$\u0005c\u0000\u0000$%\u0005e\u0000\u0000%&\u0005"+
		":\u0000\u0000&\'\u0001\u0000\u0000\u0000\'(\u0006\u0002\u0000\u0000(\u0007"+
		"\u0001\u0000\u0000\u0000)*\u0005n\u0000\u0000*+\u0005u\u0000\u0000+,\u0005"+
		"m\u0000\u0000,-\u0005b\u0000\u0000-.\u0005e\u0000\u0000./\u0005r\u0000"+
		"\u0000/0\u0005:\u0000\u00000\t\u0001\u0000\u0000\u000013\u0005-\u0000"+
		"\u000021\u0001\u0000\u0000\u000023\u0001\u0000\u0000\u000035\u0001\u0000"+
		"\u0000\u000046\u000209\u000054\u0001\u0000\u0000\u000067\u0001\u0000\u0000"+
		"\u000075\u0001\u0000\u0000\u000078\u0001\u0000\u0000\u00008\u000b\u0001"+
		"\u0000\u0000\u00009=\u0005#\u0000\u0000:<\b\u0000\u0000\u0000;:\u0001"+
		"\u0000\u0000\u0000<?\u0001\u0000\u0000\u0000=;\u0001\u0000\u0000\u0000"+
		"=>\u0001\u0000\u0000\u0000>@\u0001\u0000\u0000\u0000?=\u0001\u0000\u0000"+
		"\u0000@A\u0005\n\u0000\u0000AB\u0001\u0000\u0000\u0000BC\u0006\u0005\u0001"+
		"\u0000C\r\u0001\u0000\u0000\u0000DF\u0005 \u0000\u0000ED\u0001\u0000\u0000"+
		"\u0000FI\u0001\u0000\u0000\u0000GE\u0001\u0000\u0000\u0000GH\u0001\u0000"+
		"\u0000\u0000HK\u0001\u0000\u0000\u0000IG\u0001\u0000\u0000\u0000JL\u0005"+
		"\r\u0000\u0000KJ\u0001\u0000\u0000\u0000KL\u0001\u0000\u0000\u0000LM\u0001"+
		"\u0000\u0000\u0000MN\u0005\n\u0000\u0000NO\u0001\u0000\u0000\u0000OP\u0006"+
		"\u0006\u0001\u0000P\u000f\u0001\u0000\u0000\u0000QS\b\u0001\u0000\u0000"+
		"RQ\u0001\u0000\u0000\u0000ST\u0001\u0000\u0000\u0000TR\u0001\u0000\u0000"+
		"\u0000TU\u0001\u0000\u0000\u0000UV\u0001\u0000\u0000\u0000VW\u0006\u0007"+
		"\u0002\u0000W\u0011\u0001\u0000\u0000\u0000\b\u0000\u000127=GKT\u0003"+
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