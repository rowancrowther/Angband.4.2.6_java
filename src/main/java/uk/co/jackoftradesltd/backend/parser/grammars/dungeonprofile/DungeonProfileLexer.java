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

// Generated from /home/rowan/IdeaProjects/Angband.4.2.6_java/src/main/java/uk/co/jackoftradesltd/backend/parser/grammars/DungeonProfileLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.dungeonprofile;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class DungeonProfileLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NAME=1, PARAMS=2, TUNNEL=3, STREAMER=4, ROOM=5, MIN_LEVEL=6, ALLOC=7, 
		INTEGER=8, COLON=9, COMMENT=10, EOL=11, TEXT_BETWEEN_COLON=12, DELIMITER=13, 
		STRING=14, END_OF_MODE=15;
	public static final int
		DELIMITED_TEXT=1, REST_OF_LINE=2;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "DELIMITED_TEXT", "REST_OF_LINE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"NAME", "PARAMS", "TUNNEL", "STREAMER", "ROOM", "MIN_LEVEL", "ALLOC", 
			"INTEGER", "COLON", "COMMENT", "EOL", "TEXT_BETWEEN_COLON", "DELIMITER", 
			"STRING", "END_OF_MODE"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'name:'", "'params:'", "'tunnel:'", "'streamer:'", "'room:'", 
			"'min-level:'", "'alloc:'", null, "':'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "NAME", "PARAMS", "TUNNEL", "STREAMER", "ROOM", "MIN_LEVEL", "ALLOC", 
			"INTEGER", "COLON", "COMMENT", "EOL", "TEXT_BETWEEN_COLON", "DELIMITER", 
			"STRING", "END_OF_MODE"
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


	public DungeonProfileLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "DungeonProfileLexer.g4"; }

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
		"\u0004\u0000\u000f\u0099\u0006\uffff\uffff\u0006\uffff\uffff\u0006\uffff"+
		"\uffff\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007"+
		"\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007"+
		"\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b"+
		"\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007"+
		"\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0003\u0007_\b"+
		"\u0007\u0001\u0007\u0004\u0007b\b\u0007\u000b\u0007\f\u0007c\u0001\b\u0001"+
		"\b\u0001\t\u0001\t\u0005\tj\b\t\n\t\f\tm\t\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\n\u0005\nt\b\n\n\n\f\nw\t\n\u0001\n\u0003\nz\b\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0001\u000b\u0004\u000b\u0081\b\u000b\u000b\u000b\f"+
		"\u000b\u0082\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0004\r\u008b"+
		"\b\r\u000b\r\f\r\u008c\u0001\u000e\u0005\u000e\u0090\b\u000e\n\u000e\f"+
		"\u000e\u0093\t\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0000\u0000\u000f\u0003\u0001\u0005\u0002\u0007\u0003\t\u0004\u000b"+
		"\u0005\r\u0006\u000f\u0007\u0011\b\u0013\t\u0015\n\u0017\u000b\u0019\f"+
		"\u001b\r\u001d\u000e\u001f\u000f\u0003\u0000\u0001\u0002\u0003\u0001\u0000"+
		"\n\n\u0003\u0000\n\n\r\r::\u0002\u0000\n\n\r\r\u009e\u0000\u0003\u0001"+
		"\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001"+
		"\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000"+
		"\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000"+
		"\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000"+
		"\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000"+
		"\u0000\u0001\u0019\u0001\u0000\u0000\u0000\u0001\u001b\u0001\u0000\u0000"+
		"\u0000\u0002\u001d\u0001\u0000\u0000\u0000\u0002\u001f\u0001\u0000\u0000"+
		"\u0000\u0003!\u0001\u0000\u0000\u0000\u0005)\u0001\u0000\u0000\u0000\u0007"+
		"1\u0001\u0000\u0000\u0000\t9\u0001\u0000\u0000\u0000\u000bC\u0001\u0000"+
		"\u0000\u0000\rK\u0001\u0000\u0000\u0000\u000fV\u0001\u0000\u0000\u0000"+
		"\u0011^\u0001\u0000\u0000\u0000\u0013e\u0001\u0000\u0000\u0000\u0015g"+
		"\u0001\u0000\u0000\u0000\u0017u\u0001\u0000\u0000\u0000\u0019\u0080\u0001"+
		"\u0000\u0000\u0000\u001b\u0084\u0001\u0000\u0000\u0000\u001d\u008a\u0001"+
		"\u0000\u0000\u0000\u001f\u0091\u0001\u0000\u0000\u0000!\"\u0005n\u0000"+
		"\u0000\"#\u0005a\u0000\u0000#$\u0005m\u0000\u0000$%\u0005e\u0000\u0000"+
		"%&\u0005:\u0000\u0000&\'\u0001\u0000\u0000\u0000\'(\u0006\u0000\u0000"+
		"\u0000(\u0004\u0001\u0000\u0000\u0000)*\u0005p\u0000\u0000*+\u0005a\u0000"+
		"\u0000+,\u0005r\u0000\u0000,-\u0005a\u0000\u0000-.\u0005m\u0000\u0000"+
		"./\u0005s\u0000\u0000/0\u0005:\u0000\u00000\u0006\u0001\u0000\u0000\u0000"+
		"12\u0005t\u0000\u000023\u0005u\u0000\u000034\u0005n\u0000\u000045\u0005"+
		"n\u0000\u000056\u0005e\u0000\u000067\u0005l\u0000\u000078\u0005:\u0000"+
		"\u00008\b\u0001\u0000\u0000\u00009:\u0005s\u0000\u0000:;\u0005t\u0000"+
		"\u0000;<\u0005r\u0000\u0000<=\u0005e\u0000\u0000=>\u0005a\u0000\u0000"+
		">?\u0005m\u0000\u0000?@\u0005e\u0000\u0000@A\u0005r\u0000\u0000AB\u0005"+
		":\u0000\u0000B\n\u0001\u0000\u0000\u0000CD\u0005r\u0000\u0000DE\u0005"+
		"o\u0000\u0000EF\u0005o\u0000\u0000FG\u0005m\u0000\u0000GH\u0005:\u0000"+
		"\u0000HI\u0001\u0000\u0000\u0000IJ\u0006\u0004\u0001\u0000J\f\u0001\u0000"+
		"\u0000\u0000KL\u0005m\u0000\u0000LM\u0005i\u0000\u0000MN\u0005n\u0000"+
		"\u0000NO\u0005-\u0000\u0000OP\u0005l\u0000\u0000PQ\u0005e\u0000\u0000"+
		"QR\u0005v\u0000\u0000RS\u0005e\u0000\u0000ST\u0005l\u0000\u0000TU\u0005"+
		":\u0000\u0000U\u000e\u0001\u0000\u0000\u0000VW\u0005a\u0000\u0000WX\u0005"+
		"l\u0000\u0000XY\u0005l\u0000\u0000YZ\u0005o\u0000\u0000Z[\u0005c\u0000"+
		"\u0000[\\\u0005:\u0000\u0000\\\u0010\u0001\u0000\u0000\u0000]_\u0005-"+
		"\u0000\u0000^]\u0001\u0000\u0000\u0000^_\u0001\u0000\u0000\u0000_a\u0001"+
		"\u0000\u0000\u0000`b\u000209\u0000a`\u0001\u0000\u0000\u0000bc\u0001\u0000"+
		"\u0000\u0000ca\u0001\u0000\u0000\u0000cd\u0001\u0000\u0000\u0000d\u0012"+
		"\u0001\u0000\u0000\u0000ef\u0005:\u0000\u0000f\u0014\u0001\u0000\u0000"+
		"\u0000gk\u0005#\u0000\u0000hj\b\u0000\u0000\u0000ih\u0001\u0000\u0000"+
		"\u0000jm\u0001\u0000\u0000\u0000ki\u0001\u0000\u0000\u0000kl\u0001\u0000"+
		"\u0000\u0000ln\u0001\u0000\u0000\u0000mk\u0001\u0000\u0000\u0000no\u0005"+
		"\n\u0000\u0000op\u0001\u0000\u0000\u0000pq\u0006\t\u0002\u0000q\u0016"+
		"\u0001\u0000\u0000\u0000rt\u0005 \u0000\u0000sr\u0001\u0000\u0000\u0000"+
		"tw\u0001\u0000\u0000\u0000us\u0001\u0000\u0000\u0000uv\u0001\u0000\u0000"+
		"\u0000vy\u0001\u0000\u0000\u0000wu\u0001\u0000\u0000\u0000xz\u0005\r\u0000"+
		"\u0000yx\u0001\u0000\u0000\u0000yz\u0001\u0000\u0000\u0000z{\u0001\u0000"+
		"\u0000\u0000{|\u0005\n\u0000\u0000|}\u0001\u0000\u0000\u0000}~\u0006\n"+
		"\u0002\u0000~\u0018\u0001\u0000\u0000\u0000\u007f\u0081\b\u0001\u0000"+
		"\u0000\u0080\u007f\u0001\u0000\u0000\u0000\u0081\u0082\u0001\u0000\u0000"+
		"\u0000\u0082\u0080\u0001\u0000\u0000\u0000\u0082\u0083\u0001\u0000\u0000"+
		"\u0000\u0083\u001a\u0001\u0000\u0000\u0000\u0084\u0085\u0007\u0001\u0000"+
		"\u0000\u0085\u0086\u0001\u0000\u0000\u0000\u0086\u0087\u0006\f\u0002\u0000"+
		"\u0087\u0088\u0006\f\u0003\u0000\u0088\u001c\u0001\u0000\u0000\u0000\u0089"+
		"\u008b\b\u0002\u0000\u0000\u008a\u0089\u0001\u0000\u0000\u0000\u008b\u008c"+
		"\u0001\u0000\u0000\u0000\u008c\u008a\u0001\u0000\u0000\u0000\u008c\u008d"+
		"\u0001\u0000\u0000\u0000\u008d\u001e\u0001\u0000\u0000\u0000\u008e\u0090"+
		"\u0005\r\u0000\u0000\u008f\u008e\u0001\u0000\u0000\u0000\u0090\u0093\u0001"+
		"\u0000\u0000\u0000\u0091\u008f\u0001\u0000\u0000\u0000\u0091\u0092\u0001"+
		"\u0000\u0000\u0000\u0092\u0094\u0001\u0000\u0000\u0000\u0093\u0091\u0001"+
		"\u0000\u0000\u0000\u0094\u0095\u0005\n\u0000\u0000\u0095\u0096\u0001\u0000"+
		"\u0000\u0000\u0096\u0097\u0006\u000e\u0002\u0000\u0097\u0098\u0006\u000e"+
		"\u0003\u0000\u0098 \u0001\u0000\u0000\u0000\u000b\u0000\u0001\u0002^c"+
		"kuy\u0082\u008c\u0091\u0004\u0005\u0002\u0000\u0005\u0001\u0000\u0006"+
		"\u0000\u0000\u0004\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}