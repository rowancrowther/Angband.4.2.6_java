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

// Generated from HintLexer.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.hint;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class HintLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		HINT=1, INTEGER=2, COMMENT=3, INDENT=4, EOL=5, STRING=6;
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
			"HINT", "INTEGER", "COMMENT", "NL", "NEWLINE", "INDENT", "EOL", "STRING"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'H:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "HINT", "INTEGER", "COMMENT", "INDENT", "EOL", "STRING"
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


	public HintLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "HintLexer.g4"; }

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
		case 5:
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
		"\u0004\u0000\u0006[\u0006\uffff\uffff\u0006\uffff\uffff\u0002\u0000\u0007"+
		"\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007"+
		"\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007"+
		"\u0006\u0002\u0007\u0007\u0007\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0003\u0001\u0019\b\u0001\u0001\u0001\u0004"+
		"\u0001\u001c\b\u0001\u000b\u0001\f\u0001\u001d\u0001\u0002\u0001\u0002"+
		"\u0005\u0002\"\b\u0002\n\u0002\f\u0002%\t\u0002\u0001\u0002\u0001\u0002"+
		"\u0003\u0002)\b\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0005\u0003"+
		".\b\u0003\n\u0003\f\u00031\t\u0003\u0001\u0003\u0004\u00034\b\u0003\u000b"+
		"\u0003\f\u00035\u0001\u0003\u0003\u00039\b\u0003\u0001\u0003\u0003\u0003"+
		"<\b\u0003\u0001\u0004\u0004\u0004?\b\u0004\u000b\u0004\f\u0004@\u0001"+
		"\u0004\u0003\u0004D\b\u0004\u0001\u0004\u0003\u0004G\b\u0004\u0001\u0005"+
		"\u0001\u0005\u0004\u0005K\b\u0005\u000b\u0005\f\u0005L\u0001\u0005\u0001"+
		"\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0004"+
		"\u0007V\b\u0007\u000b\u0007\f\u0007W\u0001\u0007\u0001\u0007\u0000\u0000"+
		"\b\u0002\u0001\u0004\u0002\u0006\u0003\b\u0000\n\u0000\f\u0004\u000e\u0005"+
		"\u0010\u0006\u0002\u0000\u0001\u0002\u0002\u0000\n\n\r\r\u0002\u0000\t"+
		"\t  d\u0000\u0002\u0001\u0000\u0000\u0000\u0000\u0004\u0001\u0000\u0000"+
		"\u0000\u0000\u0006\u0001\u0000\u0000\u0000\u0000\f\u0001\u0000\u0000\u0000"+
		"\u0000\u000e\u0001\u0000\u0000\u0000\u0001\u0010\u0001\u0000\u0000\u0000"+
		"\u0002\u0012\u0001\u0000\u0000\u0000\u0004\u0018\u0001\u0000\u0000\u0000"+
		"\u0006\u001f\u0001\u0000\u0000\u0000\b/\u0001\u0000\u0000\u0000\nF\u0001"+
		"\u0000\u0000\u0000\fH\u0001\u0000\u0000\u0000\u000eP\u0001\u0000\u0000"+
		"\u0000\u0010U\u0001\u0000\u0000\u0000\u0012\u0013\u0005H\u0000\u0000\u0013"+
		"\u0014\u0005:\u0000\u0000\u0014\u0015\u0001\u0000\u0000\u0000\u0015\u0016"+
		"\u0006\u0000\u0000\u0000\u0016\u0003\u0001\u0000\u0000\u0000\u0017\u0019"+
		"\u0005-\u0000\u0000\u0018\u0017\u0001\u0000\u0000\u0000\u0018\u0019\u0001"+
		"\u0000\u0000\u0000\u0019\u001b\u0001\u0000\u0000\u0000\u001a\u001c\u0002"+
		"09\u0000\u001b\u001a\u0001\u0000\u0000\u0000\u001c\u001d\u0001\u0000\u0000"+
		"\u0000\u001d\u001b\u0001\u0000\u0000\u0000\u001d\u001e\u0001\u0000\u0000"+
		"\u0000\u001e\u0005\u0001\u0000\u0000\u0000\u001f#\u0005#\u0000\u0000 "+
		"\"\b\u0000\u0000\u0000! \u0001\u0000\u0000\u0000\"%\u0001\u0000\u0000"+
		"\u0000#!\u0001\u0000\u0000\u0000#$\u0001\u0000\u0000\u0000$(\u0001\u0000"+
		"\u0000\u0000%#\u0001\u0000\u0000\u0000&)\u0003\b\u0003\u0000\')\u0005"+
		"\u0000\u0000\u0001(&\u0001\u0000\u0000\u0000(\'\u0001\u0000\u0000\u0000"+
		")*\u0001\u0000\u0000\u0000*+\u0006\u0002\u0001\u0000+\u0007\u0001\u0000"+
		"\u0000\u0000,.\u0007\u0001\u0000\u0000-,\u0001\u0000\u0000\u0000.1\u0001"+
		"\u0000\u0000\u0000/-\u0001\u0000\u0000\u0000/0\u0001\u0000\u0000\u0000"+
		"0;\u0001\u0000\u0000\u00001/\u0001\u0000\u0000\u000024\u0005\r\u0000\u0000"+
		"32\u0001\u0000\u0000\u000045\u0001\u0000\u0000\u000053\u0001\u0000\u0000"+
		"\u000056\u0001\u0000\u0000\u000068\u0001\u0000\u0000\u000079\u0005\n\u0000"+
		"\u000087\u0001\u0000\u0000\u000089\u0001\u0000\u0000\u00009<\u0001\u0000"+
		"\u0000\u0000:<\u0005\n\u0000\u0000;3\u0001\u0000\u0000\u0000;:\u0001\u0000"+
		"\u0000\u0000<\t\u0001\u0000\u0000\u0000=?\u0005\r\u0000\u0000>=\u0001"+
		"\u0000\u0000\u0000?@\u0001\u0000\u0000\u0000@>\u0001\u0000\u0000\u0000"+
		"@A\u0001\u0000\u0000\u0000AC\u0001\u0000\u0000\u0000BD\u0005\n\u0000\u0000"+
		"CB\u0001\u0000\u0000\u0000CD\u0001\u0000\u0000\u0000DG\u0001\u0000\u0000"+
		"\u0000EG\u0005\n\u0000\u0000F>\u0001\u0000\u0000\u0000FE\u0001\u0000\u0000"+
		"\u0000G\u000b\u0001\u0000\u0000\u0000HJ\u0004\u0005\u0000\u0000IK\u0007"+
		"\u0001\u0000\u0000JI\u0001\u0000\u0000\u0000KL\u0001\u0000\u0000\u0000"+
		"LJ\u0001\u0000\u0000\u0000LM\u0001\u0000\u0000\u0000MN\u0001\u0000\u0000"+
		"\u0000NO\u0006\u0005\u0001\u0000O\r\u0001\u0000\u0000\u0000PQ\u0003\b"+
		"\u0003\u0000QR\u0001\u0000\u0000\u0000RS\u0006\u0006\u0001\u0000S\u000f"+
		"\u0001\u0000\u0000\u0000TV\b\u0000\u0000\u0000UT\u0001\u0000\u0000\u0000"+
		"VW\u0001\u0000\u0000\u0000WU\u0001\u0000\u0000\u0000WX\u0001\u0000\u0000"+
		"\u0000XY\u0001\u0000\u0000\u0000YZ\u0006\u0007\u0002\u0000Z\u0011\u0001"+
		"\u0000\u0000\u0000\u000f\u0000\u0001\u0018\u001d#(/58;@CFLW\u0003\u0005"+
		"\u0001\u0000\u0006\u0000\u0000\u0004\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}