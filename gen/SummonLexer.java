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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/Summon.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.middle.monsters.Summon;
    import uk.co.jackoftrades.middle.monsters.MonsterBase;
    import uk.co.jackoftrades.middle.enums.MessageType;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

    import java.util.List;
    import java.util.ArrayList;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class SummonLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		COMMENT=1, EOL=2, NAME=3, MSGT=4, UNIQUES=5, BASE=6, RACE_FLAG=7, FALLBACK=8, 
		DESC=9, BOOLEAN=10, FLAG=11, TEXT=12;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"COMMENT", "EOL", "NAME", "MSGT", "UNIQUES", "BASE", "RACE_FLAG", "FALLBACK", 
			"DESC", "BOOLEAN", "FLAG", "TEXT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, "'name:'", "'msgt:'", "'uniques:'", "'base:'", "'race-flag:'", 
			"'fallback:'", "'desc:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "COMMENT", "EOL", "NAME", "MSGT", "UNIQUES", "BASE", "RACE_FLAG", 
			"FALLBACK", "DESC", "BOOLEAN", "FLAG", "TEXT"
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


	public SummonLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Summon.g4"; }

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
		"\u0004\u0000\fv\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b"+
		"\u0007\u000b\u0001\u0000\u0001\u0000\u0005\u0000\u001c\b\u0000\n\u0000"+
		"\f\u0000\u001f\t\u0000\u0001\u0000\u0004\u0000\"\b\u0000\u000b\u0000\f"+
		"\u0000#\u0001\u0000\u0001\u0000\u0001\u0001\u0005\u0001)\b\u0001\n\u0001"+
		"\f\u0001,\t\u0001\u0001\u0001\u0003\u0001/\b\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\t\u0001\t\u0001\n\u0004\nn\b\n\u000b\n\f\no\u0001\u000b\u0004"+
		"\u000bs\b\u000b\u000b\u000b\f\u000bt\u0000\u0000\f\u0001\u0001\u0003\u0002"+
		"\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013"+
		"\n\u0015\u000b\u0017\f\u0001\u0000\u0003\u0001\u0000\n\n\u0002\u0000A"+
		"Z__\u0002\u0000  az{\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001"+
		"\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001"+
		"\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000"+
		"\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000"+
		"\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000"+
		"\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000"+
		"\u0000\u0001\u0019\u0001\u0000\u0000\u0000\u0003*\u0001\u0000\u0000\u0000"+
		"\u00054\u0001\u0000\u0000\u0000\u0007:\u0001\u0000\u0000\u0000\t@\u0001"+
		"\u0000\u0000\u0000\u000bI\u0001\u0000\u0000\u0000\rO\u0001\u0000\u0000"+
		"\u0000\u000fZ\u0001\u0000\u0000\u0000\u0011d\u0001\u0000\u0000\u0000\u0013"+
		"j\u0001\u0000\u0000\u0000\u0015m\u0001\u0000\u0000\u0000\u0017r\u0001"+
		"\u0000\u0000\u0000\u0019\u001d\u0005#\u0000\u0000\u001a\u001c\b\u0000"+
		"\u0000\u0000\u001b\u001a\u0001\u0000\u0000\u0000\u001c\u001f\u0001\u0000"+
		"\u0000\u0000\u001d\u001b\u0001\u0000\u0000\u0000\u001d\u001e\u0001\u0000"+
		"\u0000\u0000\u001e!\u0001\u0000\u0000\u0000\u001f\u001d\u0001\u0000\u0000"+
		"\u0000 \"\u0005\n\u0000\u0000! \u0001\u0000\u0000\u0000\"#\u0001\u0000"+
		"\u0000\u0000#!\u0001\u0000\u0000\u0000#$\u0001\u0000\u0000\u0000$%\u0001"+
		"\u0000\u0000\u0000%&\u0006\u0000\u0000\u0000&\u0002\u0001\u0000\u0000"+
		"\u0000\')\u0005 \u0000\u0000(\'\u0001\u0000\u0000\u0000),\u0001\u0000"+
		"\u0000\u0000*(\u0001\u0000\u0000\u0000*+\u0001\u0000\u0000\u0000+.\u0001"+
		"\u0000\u0000\u0000,*\u0001\u0000\u0000\u0000-/\u0005\r\u0000\u0000.-\u0001"+
		"\u0000\u0000\u0000./\u0001\u0000\u0000\u0000/0\u0001\u0000\u0000\u0000"+
		"01\u0005\n\u0000\u000012\u0001\u0000\u0000\u000023\u0006\u0001\u0000\u0000"+
		"3\u0004\u0001\u0000\u0000\u000045\u0005n\u0000\u000056\u0005a\u0000\u0000"+
		"67\u0005m\u0000\u000078\u0005e\u0000\u000089\u0005:\u0000\u00009\u0006"+
		"\u0001\u0000\u0000\u0000:;\u0005m\u0000\u0000;<\u0005s\u0000\u0000<=\u0005"+
		"g\u0000\u0000=>\u0005t\u0000\u0000>?\u0005:\u0000\u0000?\b\u0001\u0000"+
		"\u0000\u0000@A\u0005u\u0000\u0000AB\u0005n\u0000\u0000BC\u0005i\u0000"+
		"\u0000CD\u0005q\u0000\u0000DE\u0005u\u0000\u0000EF\u0005e\u0000\u0000"+
		"FG\u0005s\u0000\u0000GH\u0005:\u0000\u0000H\n\u0001\u0000\u0000\u0000"+
		"IJ\u0005b\u0000\u0000JK\u0005a\u0000\u0000KL\u0005s\u0000\u0000LM\u0005"+
		"e\u0000\u0000MN\u0005:\u0000\u0000N\f\u0001\u0000\u0000\u0000OP\u0005"+
		"r\u0000\u0000PQ\u0005a\u0000\u0000QR\u0005c\u0000\u0000RS\u0005e\u0000"+
		"\u0000ST\u0005-\u0000\u0000TU\u0005f\u0000\u0000UV\u0005l\u0000\u0000"+
		"VW\u0005a\u0000\u0000WX\u0005g\u0000\u0000XY\u0005:\u0000\u0000Y\u000e"+
		"\u0001\u0000\u0000\u0000Z[\u0005f\u0000\u0000[\\\u0005a\u0000\u0000\\"+
		"]\u0005l\u0000\u0000]^\u0005l\u0000\u0000^_\u0005b\u0000\u0000_`\u0005"+
		"a\u0000\u0000`a\u0005c\u0000\u0000ab\u0005k\u0000\u0000bc\u0005:\u0000"+
		"\u0000c\u0010\u0001\u0000\u0000\u0000de\u0005d\u0000\u0000ef\u0005e\u0000"+
		"\u0000fg\u0005s\u0000\u0000gh\u0005c\u0000\u0000hi\u0005:\u0000\u0000"+
		"i\u0012\u0001\u0000\u0000\u0000jk\u000201\u0000k\u0014\u0001\u0000\u0000"+
		"\u0000ln\u0007\u0001\u0000\u0000ml\u0001\u0000\u0000\u0000no\u0001\u0000"+
		"\u0000\u0000om\u0001\u0000\u0000\u0000op\u0001\u0000\u0000\u0000p\u0016"+
		"\u0001\u0000\u0000\u0000qs\u0007\u0002\u0000\u0000rq\u0001\u0000\u0000"+
		"\u0000st\u0001\u0000\u0000\u0000tr\u0001\u0000\u0000\u0000tu\u0001\u0000"+
		"\u0000\u0000u\u0018\u0001\u0000\u0000\u0000\u0007\u0000\u001d#*.ot\u0001"+
		"\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}