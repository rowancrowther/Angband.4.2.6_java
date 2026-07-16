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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/MonsterNest.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.cave.PitProfile;
    import uk.co.jackoftrades.middle.cave.enums.PitRoomType;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.monsters.MonsterBase;
    import uk.co.jackoftrades.middle.monsters.MonsterRace;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;
    import uk.co.jackoftrades.frontend.colour.enums.ColourType;

    import java.util.ArrayList;
    import java.util.List;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class MonsterNestLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		COMMENT=1, EOL=2, NAME=3, ROOM=4, ALLOC=5, OBJ_RARITY=6, COLOUR=7, MON_BASE=8, 
		FLAGS_REQ=9, FLAGS_BAN=10, INNATE_FREQ=11, SPELL_REQ=12, SPELL_BAN=13, 
		MON_BAN=14, INTEGER=15, COLOUR_CHAR=16, STRING=17, COLON=18, OR=19;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"COMMENT", "EOL", "NAME", "ROOM", "ALLOC", "OBJ_RARITY", "COLOUR", "MON_BASE", 
			"FLAGS_REQ", "FLAGS_BAN", "INNATE_FREQ", "SPELL_REQ", "SPELL_BAN", "MON_BAN", 
			"INTEGER", "COLOUR_CHAR", "STRING", "COLON", "OR"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, "'name:'", "'room:'", "'alloc:'", "'obj-rarity:'", 
			"'color:'", "'mon-base:'", "'flags-req:'", "'flags-ban:'", "'innate-freq:'", 
			"'spell-req:'", "'spell-ban:'", "'mon-ban:'", null, null, null, "':'", 
			"'|'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "COMMENT", "EOL", "NAME", "ROOM", "ALLOC", "OBJ_RARITY", "COLOUR", 
			"MON_BASE", "FLAGS_REQ", "FLAGS_BAN", "INNATE_FREQ", "SPELL_REQ", "SPELL_BAN", 
			"MON_BAN", "INTEGER", "COLOUR_CHAR", "STRING", "COLON", "OR"
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


	public MonsterNestLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "MonsterNest.g4"; }

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
		"\u0004\u0000\u0013\u00c4\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002"+
		"\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002"+
		"\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002"+
		"\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002"+
		"\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e"+
		"\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011"+
		"\u0002\u0012\u0007\u0012\u0001\u0000\u0001\u0000\u0005\u0000*\b\u0000"+
		"\n\u0000\f\u0000-\t\u0000\u0001\u0000\u0004\u00000\b\u0000\u000b\u0000"+
		"\f\u00001\u0001\u0000\u0001\u0000\u0001\u0001\u0005\u00017\b\u0001\n\u0001"+
		"\f\u0001:\t\u0001\u0001\u0001\u0003\u0001=\b\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f"+
		"\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0004\u000e\u00b6\b\u000e\u000b"+
		"\u000e\f\u000e\u00b7\u0001\u000f\u0001\u000f\u0001\u0010\u0004\u0010\u00bd"+
		"\b\u0010\u000b\u0010\f\u0010\u00be\u0001\u0011\u0001\u0011\u0001\u0012"+
		"\u0001\u0012\u0000\u0000\u0013\u0001\u0001\u0003\u0002\u0005\u0003\u0007"+
		"\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b"+
		"\u0017\f\u0019\r\u001b\u000e\u001d\u000f\u001f\u0010!\u0011#\u0012%\u0013"+
		"\u0001\u0000\u0003\u0001\u0000\n\n\u0011\u0000BBDDGGIIMMPPRRTWYZbbddg"+
		"giimmoprwyz\u0005\u0000  --AZ__az\u00c9\u0000\u0001\u0001\u0000\u0000"+
		"\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000"+
		"\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000"+
		"\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000"+
		"\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000"+
		"\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000"+
		"\u0017\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000\u0000"+
		"\u001b\u0001\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000\u0000\u0000"+
		"\u001f\u0001\u0000\u0000\u0000\u0000!\u0001\u0000\u0000\u0000\u0000#\u0001"+
		"\u0000\u0000\u0000\u0000%\u0001\u0000\u0000\u0000\u0001\'\u0001\u0000"+
		"\u0000\u0000\u00038\u0001\u0000\u0000\u0000\u0005B\u0001\u0000\u0000\u0000"+
		"\u0007H\u0001\u0000\u0000\u0000\tN\u0001\u0000\u0000\u0000\u000bU\u0001"+
		"\u0000\u0000\u0000\ra\u0001\u0000\u0000\u0000\u000fh\u0001\u0000\u0000"+
		"\u0000\u0011r\u0001\u0000\u0000\u0000\u0013}\u0001\u0000\u0000\u0000\u0015"+
		"\u0088\u0001\u0000\u0000\u0000\u0017\u0095\u0001\u0000\u0000\u0000\u0019"+
		"\u00a0\u0001\u0000\u0000\u0000\u001b\u00ab\u0001\u0000\u0000\u0000\u001d"+
		"\u00b5\u0001\u0000\u0000\u0000\u001f\u00b9\u0001\u0000\u0000\u0000!\u00bc"+
		"\u0001\u0000\u0000\u0000#\u00c0\u0001\u0000\u0000\u0000%\u00c2\u0001\u0000"+
		"\u0000\u0000\'+\u0005#\u0000\u0000(*\b\u0000\u0000\u0000)(\u0001\u0000"+
		"\u0000\u0000*-\u0001\u0000\u0000\u0000+)\u0001\u0000\u0000\u0000+,\u0001"+
		"\u0000\u0000\u0000,/\u0001\u0000\u0000\u0000-+\u0001\u0000\u0000\u0000"+
		".0\u0005\n\u0000\u0000/.\u0001\u0000\u0000\u000001\u0001\u0000\u0000\u0000"+
		"1/\u0001\u0000\u0000\u000012\u0001\u0000\u0000\u000023\u0001\u0000\u0000"+
		"\u000034\u0006\u0000\u0000\u00004\u0002\u0001\u0000\u0000\u000057\u0005"+
		" \u0000\u000065\u0001\u0000\u0000\u00007:\u0001\u0000\u0000\u000086\u0001"+
		"\u0000\u0000\u000089\u0001\u0000\u0000\u00009<\u0001\u0000\u0000\u0000"+
		":8\u0001\u0000\u0000\u0000;=\u0005\r\u0000\u0000<;\u0001\u0000\u0000\u0000"+
		"<=\u0001\u0000\u0000\u0000=>\u0001\u0000\u0000\u0000>?\u0005\n\u0000\u0000"+
		"?@\u0001\u0000\u0000\u0000@A\u0006\u0001\u0000\u0000A\u0004\u0001\u0000"+
		"\u0000\u0000BC\u0005n\u0000\u0000CD\u0005a\u0000\u0000DE\u0005m\u0000"+
		"\u0000EF\u0005e\u0000\u0000FG\u0005:\u0000\u0000G\u0006\u0001\u0000\u0000"+
		"\u0000HI\u0005r\u0000\u0000IJ\u0005o\u0000\u0000JK\u0005o\u0000\u0000"+
		"KL\u0005m\u0000\u0000LM\u0005:\u0000\u0000M\b\u0001\u0000\u0000\u0000"+
		"NO\u0005a\u0000\u0000OP\u0005l\u0000\u0000PQ\u0005l\u0000\u0000QR\u0005"+
		"o\u0000\u0000RS\u0005c\u0000\u0000ST\u0005:\u0000\u0000T\n\u0001\u0000"+
		"\u0000\u0000UV\u0005o\u0000\u0000VW\u0005b\u0000\u0000WX\u0005j\u0000"+
		"\u0000XY\u0005-\u0000\u0000YZ\u0005r\u0000\u0000Z[\u0005a\u0000\u0000"+
		"[\\\u0005r\u0000\u0000\\]\u0005i\u0000\u0000]^\u0005t\u0000\u0000^_\u0005"+
		"y\u0000\u0000_`\u0005:\u0000\u0000`\f\u0001\u0000\u0000\u0000ab\u0005"+
		"c\u0000\u0000bc\u0005o\u0000\u0000cd\u0005l\u0000\u0000de\u0005o\u0000"+
		"\u0000ef\u0005r\u0000\u0000fg\u0005:\u0000\u0000g\u000e\u0001\u0000\u0000"+
		"\u0000hi\u0005m\u0000\u0000ij\u0005o\u0000\u0000jk\u0005n\u0000\u0000"+
		"kl\u0005-\u0000\u0000lm\u0005b\u0000\u0000mn\u0005a\u0000\u0000no\u0005"+
		"s\u0000\u0000op\u0005e\u0000\u0000pq\u0005:\u0000\u0000q\u0010\u0001\u0000"+
		"\u0000\u0000rs\u0005f\u0000\u0000st\u0005l\u0000\u0000tu\u0005a\u0000"+
		"\u0000uv\u0005g\u0000\u0000vw\u0005s\u0000\u0000wx\u0005-\u0000\u0000"+
		"xy\u0005r\u0000\u0000yz\u0005e\u0000\u0000z{\u0005q\u0000\u0000{|\u0005"+
		":\u0000\u0000|\u0012\u0001\u0000\u0000\u0000}~\u0005f\u0000\u0000~\u007f"+
		"\u0005l\u0000\u0000\u007f\u0080\u0005a\u0000\u0000\u0080\u0081\u0005g"+
		"\u0000\u0000\u0081\u0082\u0005s\u0000\u0000\u0082\u0083\u0005-\u0000\u0000"+
		"\u0083\u0084\u0005b\u0000\u0000\u0084\u0085\u0005a\u0000\u0000\u0085\u0086"+
		"\u0005n\u0000\u0000\u0086\u0087\u0005:\u0000\u0000\u0087\u0014\u0001\u0000"+
		"\u0000\u0000\u0088\u0089\u0005i\u0000\u0000\u0089\u008a\u0005n\u0000\u0000"+
		"\u008a\u008b\u0005n\u0000\u0000\u008b\u008c\u0005a\u0000\u0000\u008c\u008d"+
		"\u0005t\u0000\u0000\u008d\u008e\u0005e\u0000\u0000\u008e\u008f\u0005-"+
		"\u0000\u0000\u008f\u0090\u0005f\u0000\u0000\u0090\u0091\u0005r\u0000\u0000"+
		"\u0091\u0092\u0005e\u0000\u0000\u0092\u0093\u0005q\u0000\u0000\u0093\u0094"+
		"\u0005:\u0000\u0000\u0094\u0016\u0001\u0000\u0000\u0000\u0095\u0096\u0005"+
		"s\u0000\u0000\u0096\u0097\u0005p\u0000\u0000\u0097\u0098\u0005e\u0000"+
		"\u0000\u0098\u0099\u0005l\u0000\u0000\u0099\u009a\u0005l\u0000\u0000\u009a"+
		"\u009b\u0005-\u0000\u0000\u009b\u009c\u0005r\u0000\u0000\u009c\u009d\u0005"+
		"e\u0000\u0000\u009d\u009e\u0005q\u0000\u0000\u009e\u009f\u0005:\u0000"+
		"\u0000\u009f\u0018\u0001\u0000\u0000\u0000\u00a0\u00a1\u0005s\u0000\u0000"+
		"\u00a1\u00a2\u0005p\u0000\u0000\u00a2\u00a3\u0005e\u0000\u0000\u00a3\u00a4"+
		"\u0005l\u0000\u0000\u00a4\u00a5\u0005l\u0000\u0000\u00a5\u00a6\u0005-"+
		"\u0000\u0000\u00a6\u00a7\u0005b\u0000\u0000\u00a7\u00a8\u0005a\u0000\u0000"+
		"\u00a8\u00a9\u0005n\u0000\u0000\u00a9\u00aa\u0005:\u0000\u0000\u00aa\u001a"+
		"\u0001\u0000\u0000\u0000\u00ab\u00ac\u0005m\u0000\u0000\u00ac\u00ad\u0005"+
		"o\u0000\u0000\u00ad\u00ae\u0005n\u0000\u0000\u00ae\u00af\u0005-\u0000"+
		"\u0000\u00af\u00b0\u0005b\u0000\u0000\u00b0\u00b1\u0005a\u0000\u0000\u00b1"+
		"\u00b2\u0005n\u0000\u0000\u00b2\u00b3\u0005:\u0000\u0000\u00b3\u001c\u0001"+
		"\u0000\u0000\u0000\u00b4\u00b6\u000209\u0000\u00b5\u00b4\u0001\u0000\u0000"+
		"\u0000\u00b6\u00b7\u0001\u0000\u0000\u0000\u00b7\u00b5\u0001\u0000\u0000"+
		"\u0000\u00b7\u00b8\u0001\u0000\u0000\u0000\u00b8\u001e\u0001\u0000\u0000"+
		"\u0000\u00b9\u00ba\u0007\u0001\u0000\u0000\u00ba \u0001\u0000\u0000\u0000"+
		"\u00bb\u00bd\u0007\u0002\u0000\u0000\u00bc\u00bb\u0001\u0000\u0000\u0000"+
		"\u00bd\u00be\u0001\u0000\u0000\u0000\u00be\u00bc\u0001\u0000\u0000\u0000"+
		"\u00be\u00bf\u0001\u0000\u0000\u0000\u00bf\"\u0001\u0000\u0000\u0000\u00c0"+
		"\u00c1\u0005:\u0000\u0000\u00c1$\u0001\u0000\u0000\u0000\u00c2\u00c3\u0005"+
		"|\u0000\u0000\u00c3&\u0001\u0000\u0000\u0000\u0007\u0000+18<\u00b7\u00be"+
		"\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}