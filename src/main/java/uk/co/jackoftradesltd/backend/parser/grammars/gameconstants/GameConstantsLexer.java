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

// Generated from GameConstantsLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.gameconstants;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class GameConstantsLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		GC_NAME=1, GC_MSG=2, COLON=3, INTEGER=4, COMMENT=5, INDENT=6, EOL=7;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"GC_NAME", "GC_MSG", "COLON", "INTEGER", "COMMENT", "NL", "NEWLINE", 
			"INDENT", "EOL"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, "':'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "GC_NAME", "GC_MSG", "COLON", "INTEGER", "COMMENT", "INDENT", "EOL"
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


	public GameConstantsLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "GameConstantsLexer.g4"; }

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
		case 7:
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
		"\u0004\u0000\u0007\\\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0001\u0000\u0004\u0000\u0015\b\u0000\u000b"+
		"\u0000\f\u0000\u0016\u0001\u0001\u0004\u0001\u001a\b\u0001\u000b\u0001"+
		"\f\u0001\u001b\u0001\u0002\u0001\u0002\u0001\u0003\u0003\u0003!\b\u0003"+
		"\u0001\u0003\u0004\u0003$\b\u0003\u000b\u0003\f\u0003%\u0001\u0004\u0001"+
		"\u0004\u0005\u0004*\b\u0004\n\u0004\f\u0004-\t\u0004\u0001\u0004\u0001"+
		"\u0004\u0003\u00041\b\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0005"+
		"\u00056\b\u0005\n\u0005\f\u00059\t\u0005\u0001\u0005\u0004\u0005<\b\u0005"+
		"\u000b\u0005\f\u0005=\u0001\u0005\u0003\u0005A\b\u0005\u0001\u0005\u0003"+
		"\u0005D\b\u0005\u0001\u0006\u0004\u0006G\b\u0006\u000b\u0006\f\u0006H"+
		"\u0001\u0006\u0003\u0006L\b\u0006\u0001\u0006\u0003\u0006O\b\u0006\u0001"+
		"\u0007\u0001\u0007\u0004\u0007S\b\u0007\u000b\u0007\f\u0007T\u0001\u0007"+
		"\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0000\u0000\t\u0001\u0001"+
		"\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0000\r\u0000\u000f"+
		"\u0006\u0011\u0007\u0001\u0000\u0004\u0002\u0000--az\u0002\u0000AZ__\u0002"+
		"\u0000\n\n\r\r\u0002\u0000\t\t  g\u0000\u0001\u0001\u0000\u0000\u0000"+
		"\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000"+
		"\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000"+
		"\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0001"+
		"\u0014\u0001\u0000\u0000\u0000\u0003\u0019\u0001\u0000\u0000\u0000\u0005"+
		"\u001d\u0001\u0000\u0000\u0000\u0007 \u0001\u0000\u0000\u0000\t\'\u0001"+
		"\u0000\u0000\u0000\u000b7\u0001\u0000\u0000\u0000\rN\u0001\u0000\u0000"+
		"\u0000\u000fP\u0001\u0000\u0000\u0000\u0011X\u0001\u0000\u0000\u0000\u0013"+
		"\u0015\u0007\u0000\u0000\u0000\u0014\u0013\u0001\u0000\u0000\u0000\u0015"+
		"\u0016\u0001\u0000\u0000\u0000\u0016\u0014\u0001\u0000\u0000\u0000\u0016"+
		"\u0017\u0001\u0000\u0000\u0000\u0017\u0002\u0001\u0000\u0000\u0000\u0018"+
		"\u001a\u0007\u0001\u0000\u0000\u0019\u0018\u0001\u0000\u0000\u0000\u001a"+
		"\u001b\u0001\u0000\u0000\u0000\u001b\u0019\u0001\u0000\u0000\u0000\u001b"+
		"\u001c\u0001\u0000\u0000\u0000\u001c\u0004\u0001\u0000\u0000\u0000\u001d"+
		"\u001e\u0005:\u0000\u0000\u001e\u0006\u0001\u0000\u0000\u0000\u001f!\u0005"+
		"-\u0000\u0000 \u001f\u0001\u0000\u0000\u0000 !\u0001\u0000\u0000\u0000"+
		"!#\u0001\u0000\u0000\u0000\"$\u000209\u0000#\"\u0001\u0000\u0000\u0000"+
		"$%\u0001\u0000\u0000\u0000%#\u0001\u0000\u0000\u0000%&\u0001\u0000\u0000"+
		"\u0000&\b\u0001\u0000\u0000\u0000\'+\u0005#\u0000\u0000(*\b\u0002\u0000"+
		"\u0000)(\u0001\u0000\u0000\u0000*-\u0001\u0000\u0000\u0000+)\u0001\u0000"+
		"\u0000\u0000+,\u0001\u0000\u0000\u0000,0\u0001\u0000\u0000\u0000-+\u0001"+
		"\u0000\u0000\u0000.1\u0003\u000b\u0005\u0000/1\u0005\u0000\u0000\u0001"+
		"0.\u0001\u0000\u0000\u00000/\u0001\u0000\u0000\u000012\u0001\u0000\u0000"+
		"\u000023\u0006\u0004\u0000\u00003\n\u0001\u0000\u0000\u000046\u0007\u0003"+
		"\u0000\u000054\u0001\u0000\u0000\u000069\u0001\u0000\u0000\u000075\u0001"+
		"\u0000\u0000\u000078\u0001\u0000\u0000\u00008C\u0001\u0000\u0000\u0000"+
		"97\u0001\u0000\u0000\u0000:<\u0005\r\u0000\u0000;:\u0001\u0000\u0000\u0000"+
		"<=\u0001\u0000\u0000\u0000=;\u0001\u0000\u0000\u0000=>\u0001\u0000\u0000"+
		"\u0000>@\u0001\u0000\u0000\u0000?A\u0005\n\u0000\u0000@?\u0001\u0000\u0000"+
		"\u0000@A\u0001\u0000\u0000\u0000AD\u0001\u0000\u0000\u0000BD\u0005\n\u0000"+
		"\u0000C;\u0001\u0000\u0000\u0000CB\u0001\u0000\u0000\u0000D\f\u0001\u0000"+
		"\u0000\u0000EG\u0005\r\u0000\u0000FE\u0001\u0000\u0000\u0000GH\u0001\u0000"+
		"\u0000\u0000HF\u0001\u0000\u0000\u0000HI\u0001\u0000\u0000\u0000IK\u0001"+
		"\u0000\u0000\u0000JL\u0005\n\u0000\u0000KJ\u0001\u0000\u0000\u0000KL\u0001"+
		"\u0000\u0000\u0000LO\u0001\u0000\u0000\u0000MO\u0005\n\u0000\u0000NF\u0001"+
		"\u0000\u0000\u0000NM\u0001\u0000\u0000\u0000O\u000e\u0001\u0000\u0000"+
		"\u0000PR\u0004\u0007\u0000\u0000QS\u0007\u0003\u0000\u0000RQ\u0001\u0000"+
		"\u0000\u0000ST\u0001\u0000\u0000\u0000TR\u0001\u0000\u0000\u0000TU\u0001"+
		"\u0000\u0000\u0000UV\u0001\u0000\u0000\u0000VW\u0006\u0007\u0000\u0000"+
		"W\u0010\u0001\u0000\u0000\u0000XY\u0003\u000b\u0005\u0000YZ\u0001\u0000"+
		"\u0000\u0000Z[\u0006\b\u0000\u0000[\u0012\u0001\u0000\u0000\u0000\u000f"+
		"\u0000\u0016\u001b %+07=@CHKNT\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}