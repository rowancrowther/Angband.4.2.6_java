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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/.claude/worktrees/gameconstants-assembler/src/main/java/uk/co/jackoftrades/backend/parser/grammars/Slay.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
    import uk.co.jackoftrades.middle.monsters.MonsterBase;
    import uk.co.jackoftrades.middle.objects.Slay;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;

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
public class SlayLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		COMMENT=1, EOL=2, CODE=3, NAME=4, RACE_FLAG=5, BASE=6, MULTIPLIER=7, O_MULTIPLIER=8, 
		POWER=9, MELEE_VERB=10, RANGE_VERB=11, NUMBER=12, FLAG=13, TEXT=14, BASE_FLAG=15;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"COMMENT", "EOL", "CODE", "NAME", "RACE_FLAG", "BASE", "MULTIPLIER", 
			"O_MULTIPLIER", "POWER", "MELEE_VERB", "RANGE_VERB", "NUMBER", "FLAG", 
			"TEXT", "BASE_FLAG"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, "'code:'", "'name:'", "'race-flag:'", "'base:'", "'multiplier:'", 
			"'o-multiplier:'", "'power:'", "'melee-verb:'", "'range-verb:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "COMMENT", "EOL", "CODE", "NAME", "RACE_FLAG", "BASE", "MULTIPLIER", 
			"O_MULTIPLIER", "POWER", "MELEE_VERB", "RANGE_VERB", "NUMBER", "FLAG", 
			"TEXT", "BASE_FLAG"
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


	public SlayLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Slay.g4"; }

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
		"\u0004\u0000\u000f\u00a4\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002"+
		"\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002"+
		"\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002"+
		"\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002"+
		"\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e"+
		"\u0001\u0000\u0001\u0000\u0005\u0000\"\b\u0000\n\u0000\f\u0000%\t\u0000"+
		"\u0001\u0000\u0004\u0000(\b\u0000\u000b\u0000\f\u0000)\u0001\u0000\u0001"+
		"\u0000\u0001\u0001\u0005\u0001/\b\u0001\n\u0001\f\u00012\t\u0001\u0001"+
		"\u0001\u0003\u00015\b\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0004\u000b"+
		"\u0092\b\u000b\u000b\u000b\f\u000b\u0093\u0001\f\u0004\f\u0097\b\f\u000b"+
		"\f\f\f\u0098\u0001\r\u0004\r\u009c\b\r\u000b\r\f\r\u009d\u0001\u000e\u0004"+
		"\u000e\u00a1\b\u000e\u000b\u000e\f\u000e\u00a2\u0000\u0000\u000f\u0001"+
		"\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007"+
		"\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e\u001d"+
		"\u000f\u0001\u0000\u0004\u0001\u0000\n\n\u0003\u000009AZ__\u0002\u0000"+
		"  az\u0003\u0000  AZaz\u00ab\u0000\u0001\u0001\u0000\u0000\u0000\u0000"+
		"\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000"+
		"\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b"+
		"\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001"+
		"\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001"+
		"\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001"+
		"\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000\u0000\u001b\u0001"+
		"\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000\u0000\u0001\u001f\u0001"+
		"\u0000\u0000\u0000\u00030\u0001\u0000\u0000\u0000\u0005:\u0001\u0000\u0000"+
		"\u0000\u0007@\u0001\u0000\u0000\u0000\tF\u0001\u0000\u0000\u0000\u000b"+
		"Q\u0001\u0000\u0000\u0000\rW\u0001\u0000\u0000\u0000\u000fc\u0001\u0000"+
		"\u0000\u0000\u0011q\u0001\u0000\u0000\u0000\u0013x\u0001\u0000\u0000\u0000"+
		"\u0015\u0084\u0001\u0000\u0000\u0000\u0017\u0091\u0001\u0000\u0000\u0000"+
		"\u0019\u0096\u0001\u0000\u0000\u0000\u001b\u009b\u0001\u0000\u0000\u0000"+
		"\u001d\u00a0\u0001\u0000\u0000\u0000\u001f#\u0005#\u0000\u0000 \"\b\u0000"+
		"\u0000\u0000! \u0001\u0000\u0000\u0000\"%\u0001\u0000\u0000\u0000#!\u0001"+
		"\u0000\u0000\u0000#$\u0001\u0000\u0000\u0000$\'\u0001\u0000\u0000\u0000"+
		"%#\u0001\u0000\u0000\u0000&(\u0005\n\u0000\u0000\'&\u0001\u0000\u0000"+
		"\u0000()\u0001\u0000\u0000\u0000)\'\u0001\u0000\u0000\u0000)*\u0001\u0000"+
		"\u0000\u0000*+\u0001\u0000\u0000\u0000+,\u0006\u0000\u0000\u0000,\u0002"+
		"\u0001\u0000\u0000\u0000-/\u0005 \u0000\u0000.-\u0001\u0000\u0000\u0000"+
		"/2\u0001\u0000\u0000\u00000.\u0001\u0000\u0000\u000001\u0001\u0000\u0000"+
		"\u000014\u0001\u0000\u0000\u000020\u0001\u0000\u0000\u000035\u0005\r\u0000"+
		"\u000043\u0001\u0000\u0000\u000045\u0001\u0000\u0000\u000056\u0001\u0000"+
		"\u0000\u000067\u0005\n\u0000\u000078\u0001\u0000\u0000\u000089\u0006\u0001"+
		"\u0000\u00009\u0004\u0001\u0000\u0000\u0000:;\u0005c\u0000\u0000;<\u0005"+
		"o\u0000\u0000<=\u0005d\u0000\u0000=>\u0005e\u0000\u0000>?\u0005:\u0000"+
		"\u0000?\u0006\u0001\u0000\u0000\u0000@A\u0005n\u0000\u0000AB\u0005a\u0000"+
		"\u0000BC\u0005m\u0000\u0000CD\u0005e\u0000\u0000DE\u0005:\u0000\u0000"+
		"E\b\u0001\u0000\u0000\u0000FG\u0005r\u0000\u0000GH\u0005a\u0000\u0000"+
		"HI\u0005c\u0000\u0000IJ\u0005e\u0000\u0000JK\u0005-\u0000\u0000KL\u0005"+
		"f\u0000\u0000LM\u0005l\u0000\u0000MN\u0005a\u0000\u0000NO\u0005g\u0000"+
		"\u0000OP\u0005:\u0000\u0000P\n\u0001\u0000\u0000\u0000QR\u0005b\u0000"+
		"\u0000RS\u0005a\u0000\u0000ST\u0005s\u0000\u0000TU\u0005e\u0000\u0000"+
		"UV\u0005:\u0000\u0000V\f\u0001\u0000\u0000\u0000WX\u0005m\u0000\u0000"+
		"XY\u0005u\u0000\u0000YZ\u0005l\u0000\u0000Z[\u0005t\u0000\u0000[\\\u0005"+
		"i\u0000\u0000\\]\u0005p\u0000\u0000]^\u0005l\u0000\u0000^_\u0005i\u0000"+
		"\u0000_`\u0005e\u0000\u0000`a\u0005r\u0000\u0000ab\u0005:\u0000\u0000"+
		"b\u000e\u0001\u0000\u0000\u0000cd\u0005o\u0000\u0000de\u0005-\u0000\u0000"+
		"ef\u0005m\u0000\u0000fg\u0005u\u0000\u0000gh\u0005l\u0000\u0000hi\u0005"+
		"t\u0000\u0000ij\u0005i\u0000\u0000jk\u0005p\u0000\u0000kl\u0005l\u0000"+
		"\u0000lm\u0005i\u0000\u0000mn\u0005e\u0000\u0000no\u0005r\u0000\u0000"+
		"op\u0005:\u0000\u0000p\u0010\u0001\u0000\u0000\u0000qr\u0005p\u0000\u0000"+
		"rs\u0005o\u0000\u0000st\u0005w\u0000\u0000tu\u0005e\u0000\u0000uv\u0005"+
		"r\u0000\u0000vw\u0005:\u0000\u0000w\u0012\u0001\u0000\u0000\u0000xy\u0005"+
		"m\u0000\u0000yz\u0005e\u0000\u0000z{\u0005l\u0000\u0000{|\u0005e\u0000"+
		"\u0000|}\u0005e\u0000\u0000}~\u0005-\u0000\u0000~\u007f\u0005v\u0000\u0000"+
		"\u007f\u0080\u0005e\u0000\u0000\u0080\u0081\u0005r\u0000\u0000\u0081\u0082"+
		"\u0005b\u0000\u0000\u0082\u0083\u0005:\u0000\u0000\u0083\u0014\u0001\u0000"+
		"\u0000\u0000\u0084\u0085\u0005r\u0000\u0000\u0085\u0086\u0005a\u0000\u0000"+
		"\u0086\u0087\u0005n\u0000\u0000\u0087\u0088\u0005g\u0000\u0000\u0088\u0089"+
		"\u0005e\u0000\u0000\u0089\u008a\u0005-\u0000\u0000\u008a\u008b\u0005v"+
		"\u0000\u0000\u008b\u008c\u0005e\u0000\u0000\u008c\u008d\u0005r\u0000\u0000"+
		"\u008d\u008e\u0005b\u0000\u0000\u008e\u008f\u0005:\u0000\u0000\u008f\u0016"+
		"\u0001\u0000\u0000\u0000\u0090\u0092\u000209\u0000\u0091\u0090\u0001\u0000"+
		"\u0000\u0000\u0092\u0093\u0001\u0000\u0000\u0000\u0093\u0091\u0001\u0000"+
		"\u0000\u0000\u0093\u0094\u0001\u0000\u0000\u0000\u0094\u0018\u0001\u0000"+
		"\u0000\u0000\u0095\u0097\u0007\u0001\u0000\u0000\u0096\u0095\u0001\u0000"+
		"\u0000\u0000\u0097\u0098\u0001\u0000\u0000\u0000\u0098\u0096\u0001\u0000"+
		"\u0000\u0000\u0098\u0099\u0001\u0000\u0000\u0000\u0099\u001a\u0001\u0000"+
		"\u0000\u0000\u009a\u009c\u0007\u0002\u0000\u0000\u009b\u009a\u0001\u0000"+
		"\u0000\u0000\u009c\u009d\u0001\u0000\u0000\u0000\u009d\u009b\u0001\u0000"+
		"\u0000\u0000\u009d\u009e\u0001\u0000\u0000\u0000\u009e\u001c\u0001\u0000"+
		"\u0000\u0000\u009f\u00a1\u0007\u0003\u0000\u0000\u00a0\u009f\u0001\u0000"+
		"\u0000\u0000\u00a1\u00a2\u0001\u0000\u0000\u0000\u00a2\u00a0\u0001\u0000"+
		"\u0000\u0000\u00a2\u00a3\u0001\u0000\u0000\u0000\u00a3\u001e\u0001\u0000"+
		"\u0000\u0000\t\u0000#)04\u0093\u0098\u009d\u00a2\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}