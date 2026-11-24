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

// Generated from /home/rowan/IdeaProjects/Angband.4.2.6_java/scratch/pt9/imports/CommentsAndEol.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class CommentsAndEol extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		COMMENT=1, INDENT=2, EOL=3;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"COMMENT", "NL", "NEWLINE", "INDENT", "EOL"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "COMMENT", "INDENT", "EOL"
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


	public CommentsAndEol(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "CommentsAndEol.g4"; }

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
		case 3:
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
		"\u0004\u0000\u0003H\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0001\u0000\u0001\u0000\u0005\u0000\u000e\b\u0000\n\u0000"+
		"\f\u0000\u0011\t\u0000\u0001\u0000\u0001\u0000\u0003\u0000\u0015\b\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0001\u0005\u0001\u001a\b\u0001\n\u0001"+
		"\f\u0001\u001d\t\u0001\u0001\u0001\u0004\u0001 \b\u0001\u000b\u0001\f"+
		"\u0001!\u0001\u0001\u0003\u0001%\b\u0001\u0001\u0001\u0003\u0001(\b\u0001"+
		"\u0001\u0001\u0004\u0001+\b\u0001\u000b\u0001\f\u0001,\u0001\u0001\u0003"+
		"\u00010\b\u0001\u0001\u0002\u0004\u00023\b\u0002\u000b\u0002\f\u00024"+
		"\u0001\u0002\u0003\u00028\b\u0002\u0001\u0002\u0003\u0002;\b\u0002\u0001"+
		"\u0003\u0001\u0003\u0004\u0003?\b\u0003\u000b\u0003\f\u0003@\u0001\u0003"+
		"\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0000\u0000"+
		"\u0005\u0001\u0001\u0003\u0000\u0005\u0000\u0007\u0002\t\u0003\u0001\u0000"+
		"\u0002\u0002\u0000\n\n\r\r\u0002\u0000\t\t  Q\u0000\u0001\u0001\u0000"+
		"\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000"+
		"\u0000\u0001\u000b\u0001\u0000\u0000\u0000\u0003/\u0001\u0000\u0000\u0000"+
		"\u0005:\u0001\u0000\u0000\u0000\u0007<\u0001\u0000\u0000\u0000\tD\u0001"+
		"\u0000\u0000\u0000\u000b\u000f\u0005#\u0000\u0000\f\u000e\b\u0000\u0000"+
		"\u0000\r\f\u0001\u0000\u0000\u0000\u000e\u0011\u0001\u0000\u0000\u0000"+
		"\u000f\r\u0001\u0000\u0000\u0000\u000f\u0010\u0001\u0000\u0000\u0000\u0010"+
		"\u0014\u0001\u0000\u0000\u0000\u0011\u000f\u0001\u0000\u0000\u0000\u0012"+
		"\u0015\u0003\u0003\u0001\u0000\u0013\u0015\u0005\u0000\u0000\u0001\u0014"+
		"\u0012\u0001\u0000\u0000\u0000\u0014\u0013\u0001\u0000\u0000\u0000\u0015"+
		"\u0016\u0001\u0000\u0000\u0000\u0016\u0017\u0006\u0000\u0000\u0000\u0017"+
		"\u0002\u0001\u0000\u0000\u0000\u0018\u001a\u0007\u0001\u0000\u0000\u0019"+
		"\u0018\u0001\u0000\u0000\u0000\u001a\u001d\u0001\u0000\u0000\u0000\u001b"+
		"\u0019\u0001\u0000\u0000\u0000\u001b\u001c\u0001\u0000\u0000\u0000\u001c"+
		"\'\u0001\u0000\u0000\u0000\u001d\u001b\u0001\u0000\u0000\u0000\u001e "+
		"\u0005\r\u0000\u0000\u001f\u001e\u0001\u0000\u0000\u0000 !\u0001\u0000"+
		"\u0000\u0000!\u001f\u0001\u0000\u0000\u0000!\"\u0001\u0000\u0000\u0000"+
		"\"$\u0001\u0000\u0000\u0000#%\u0005\n\u0000\u0000$#\u0001\u0000\u0000"+
		"\u0000$%\u0001\u0000\u0000\u0000%(\u0001\u0000\u0000\u0000&(\u0005\n\u0000"+
		"\u0000\'\u001f\u0001\u0000\u0000\u0000\'&\u0001\u0000\u0000\u0000(0\u0001"+
		"\u0000\u0000\u0000)+\u0007\u0001\u0000\u0000*)\u0001\u0000\u0000\u0000"+
		"+,\u0001\u0000\u0000\u0000,*\u0001\u0000\u0000\u0000,-\u0001\u0000\u0000"+
		"\u0000-.\u0001\u0000\u0000\u0000.0\u0005\u0000\u0000\u0001/\u001b\u0001"+
		"\u0000\u0000\u0000/*\u0001\u0000\u0000\u00000\u0004\u0001\u0000\u0000"+
		"\u000013\u0005\r\u0000\u000021\u0001\u0000\u0000\u000034\u0001\u0000\u0000"+
		"\u000042\u0001\u0000\u0000\u000045\u0001\u0000\u0000\u000057\u0001\u0000"+
		"\u0000\u000068\u0005\n\u0000\u000076\u0001\u0000\u0000\u000078\u0001\u0000"+
		"\u0000\u00008;\u0001\u0000\u0000\u00009;\u0005\n\u0000\u0000:2\u0001\u0000"+
		"\u0000\u0000:9\u0001\u0000\u0000\u0000;\u0006\u0001\u0000\u0000\u0000"+
		"<>\u0004\u0003\u0000\u0000=?\u0007\u0001\u0000\u0000>=\u0001\u0000\u0000"+
		"\u0000?@\u0001\u0000\u0000\u0000@>\u0001\u0000\u0000\u0000@A\u0001\u0000"+
		"\u0000\u0000AB\u0001\u0000\u0000\u0000BC\u0006\u0003\u0000\u0000C\b\u0001"+
		"\u0000\u0000\u0000DE\u0003\u0003\u0001\u0000EF\u0001\u0000\u0000\u0000"+
		"FG\u0006\u0004\u0000\u0000G\n\u0001\u0000\u0000\u0000\r\u0000\u000f\u0014"+
		"\u001b!$\',/47:@\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}