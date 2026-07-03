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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/UIEntry.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars;

    import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.frontend.screen.enums.CombinerName;
    import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
    import uk.co.jackoftrades.frontend.entries.UIEntry;
    import uk.co.jackoftrades.frontend.entries.UIEntry.StatElemType;
    import uk.co.jackoftrades.frontend.entries.UIEntryBase;
    import uk.co.jackoftrades.frontend.entries.enums.EntryFlag;

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
public class UIEntryLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		COMMENT=1, EOL=2, NAME=3, PARAMETER=4, PARAMETERTYPE=5, RENDERER=6, COMBINE=7, 
		PRIORITY=8, CATEGORY=9, FLAGS=10, DESC=11, LABEL=12, LABEL5=13, LABEL2=14, 
		TEMPLATE=15, PRIORITYWORD=16, PRIORITYNUM=17, TEXT=18, TAG=19;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"COMMENT", "EOL", "NAME", "PARAMETER", "PARAMETERTYPE", "RENDERER", "COMBINE", 
			"PRIORITY", "CATEGORY", "FLAGS", "DESC", "LABEL", "LABEL5", "LABEL2", 
			"TEMPLATE", "PRIORITYWORD", "PRIORITYNUM", "TEXT", "TAG"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, "'name:'", "'parameter:'", null, "'renderer:'", "'combine:'", 
			"'priority:'", "'category:'", "'flags:'", "'desc:'", "'label:'", "'label5:'", 
			"'label2:'", "'template:'", "'negative_index'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "COMMENT", "EOL", "NAME", "PARAMETER", "PARAMETERTYPE", "RENDERER", 
			"COMBINE", "PRIORITY", "CATEGORY", "FLAGS", "DESC", "LABEL", "LABEL5", 
			"LABEL2", "TEMPLATE", "PRIORITYWORD", "PRIORITYNUM", "TEXT", "TAG"
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


	public UIEntryLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "UIEntry.g4"; }

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
		"\u0004\u0000\u0013\u00d9\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002"+
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
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0003\u0004_\b\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0003"+
		"\u0010\u00c6\b\u0010\u0001\u0010\u0004\u0010\u00c9\b\u0010\u000b\u0010"+
		"\f\u0010\u00ca\u0001\u0011\u0004\u0011\u00ce\b\u0011\u000b\u0011\f\u0011"+
		"\u00cf\u0001\u0012\u0001\u0012\u0004\u0012\u00d4\b\u0012\u000b\u0012\f"+
		"\u0012\u00d5\u0001\u0012\u0001\u0012\u0000\u0000\u0013\u0001\u0001\u0003"+
		"\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011"+
		"\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e\u001d\u000f\u001f\u0010"+
		"!\u0011#\u0012%\u0013\u0001\u0000\u0002\u0001\u0000\n\n\b\u0000  \'\'"+
		",,..01AZ__az\u00e1\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001"+
		"\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001"+
		"\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000"+
		"\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000"+
		"\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000"+
		"\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000"+
		"\u0000\u0000\u0019\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000"+
		"\u0000\u0000\u001d\u0001\u0000\u0000\u0000\u0000\u001f\u0001\u0000\u0000"+
		"\u0000\u0000!\u0001\u0000\u0000\u0000\u0000#\u0001\u0000\u0000\u0000\u0000"+
		"%\u0001\u0000\u0000\u0000\u0001\'\u0001\u0000\u0000\u0000\u00038\u0001"+
		"\u0000\u0000\u0000\u0005B\u0001\u0000\u0000\u0000\u0007H\u0001\u0000\u0000"+
		"\u0000\t^\u0001\u0000\u0000\u0000\u000b`\u0001\u0000\u0000\u0000\rj\u0001"+
		"\u0000\u0000\u0000\u000fs\u0001\u0000\u0000\u0000\u0011}\u0001\u0000\u0000"+
		"\u0000\u0013\u0087\u0001\u0000\u0000\u0000\u0015\u008e\u0001\u0000\u0000"+
		"\u0000\u0017\u0094\u0001\u0000\u0000\u0000\u0019\u009b\u0001\u0000\u0000"+
		"\u0000\u001b\u00a3\u0001\u0000\u0000\u0000\u001d\u00ab\u0001\u0000\u0000"+
		"\u0000\u001f\u00b5\u0001\u0000\u0000\u0000!\u00c5\u0001\u0000\u0000\u0000"+
		"#\u00cd\u0001\u0000\u0000\u0000%\u00d1\u0001\u0000\u0000\u0000\'+\u0005"+
		"#\u0000\u0000(*\b\u0000\u0000\u0000)(\u0001\u0000\u0000\u0000*-\u0001"+
		"\u0000\u0000\u0000+)\u0001\u0000\u0000\u0000+,\u0001\u0000\u0000\u0000"+
		",/\u0001\u0000\u0000\u0000-+\u0001\u0000\u0000\u0000.0\u0005\n\u0000\u0000"+
		"/.\u0001\u0000\u0000\u000001\u0001\u0000\u0000\u00001/\u0001\u0000\u0000"+
		"\u000012\u0001\u0000\u0000\u000023\u0001\u0000\u0000\u000034\u0006\u0000"+
		"\u0000\u00004\u0002\u0001\u0000\u0000\u000057\u0005 \u0000\u000065\u0001"+
		"\u0000\u0000\u00007:\u0001\u0000\u0000\u000086\u0001\u0000\u0000\u0000"+
		"89\u0001\u0000\u0000\u00009<\u0001\u0000\u0000\u0000:8\u0001\u0000\u0000"+
		"\u0000;=\u0005\r\u0000\u0000<;\u0001\u0000\u0000\u0000<=\u0001\u0000\u0000"+
		"\u0000=>\u0001\u0000\u0000\u0000>?\u0005\n\u0000\u0000?@\u0001\u0000\u0000"+
		"\u0000@A\u0006\u0001\u0000\u0000A\u0004\u0001\u0000\u0000\u0000BC\u0005"+
		"n\u0000\u0000CD\u0005a\u0000\u0000DE\u0005m\u0000\u0000EF\u0005e\u0000"+
		"\u0000FG\u0005:\u0000\u0000G\u0006\u0001\u0000\u0000\u0000HI\u0005p\u0000"+
		"\u0000IJ\u0005a\u0000\u0000JK\u0005r\u0000\u0000KL\u0005a\u0000\u0000"+
		"LM\u0005m\u0000\u0000MN\u0005e\u0000\u0000NO\u0005t\u0000\u0000OP\u0005"+
		"e\u0000\u0000PQ\u0005r\u0000\u0000QR\u0005:\u0000\u0000R\b\u0001\u0000"+
		"\u0000\u0000ST\u0005s\u0000\u0000TU\u0005t\u0000\u0000UV\u0005a\u0000"+
		"\u0000V_\u0005t\u0000\u0000WX\u0005e\u0000\u0000XY\u0005l\u0000\u0000"+
		"YZ\u0005e\u0000\u0000Z[\u0005m\u0000\u0000[\\\u0005e\u0000\u0000\\]\u0005"+
		"n\u0000\u0000]_\u0005t\u0000\u0000^S\u0001\u0000\u0000\u0000^W\u0001\u0000"+
		"\u0000\u0000_\n\u0001\u0000\u0000\u0000`a\u0005r\u0000\u0000ab\u0005e"+
		"\u0000\u0000bc\u0005n\u0000\u0000cd\u0005d\u0000\u0000de\u0005e\u0000"+
		"\u0000ef\u0005r\u0000\u0000fg\u0005e\u0000\u0000gh\u0005r\u0000\u0000"+
		"hi\u0005:\u0000\u0000i\f\u0001\u0000\u0000\u0000jk\u0005c\u0000\u0000"+
		"kl\u0005o\u0000\u0000lm\u0005m\u0000\u0000mn\u0005b\u0000\u0000no\u0005"+
		"i\u0000\u0000op\u0005n\u0000\u0000pq\u0005e\u0000\u0000qr\u0005:\u0000"+
		"\u0000r\u000e\u0001\u0000\u0000\u0000st\u0005p\u0000\u0000tu\u0005r\u0000"+
		"\u0000uv\u0005i\u0000\u0000vw\u0005o\u0000\u0000wx\u0005r\u0000\u0000"+
		"xy\u0005i\u0000\u0000yz\u0005t\u0000\u0000z{\u0005y\u0000\u0000{|\u0005"+
		":\u0000\u0000|\u0010\u0001\u0000\u0000\u0000}~\u0005c\u0000\u0000~\u007f"+
		"\u0005a\u0000\u0000\u007f\u0080\u0005t\u0000\u0000\u0080\u0081\u0005e"+
		"\u0000\u0000\u0081\u0082\u0005g\u0000\u0000\u0082\u0083\u0005o\u0000\u0000"+
		"\u0083\u0084\u0005r\u0000\u0000\u0084\u0085\u0005y\u0000\u0000\u0085\u0086"+
		"\u0005:\u0000\u0000\u0086\u0012\u0001\u0000\u0000\u0000\u0087\u0088\u0005"+
		"f\u0000\u0000\u0088\u0089\u0005l\u0000\u0000\u0089\u008a\u0005a\u0000"+
		"\u0000\u008a\u008b\u0005g\u0000\u0000\u008b\u008c\u0005s\u0000\u0000\u008c"+
		"\u008d\u0005:\u0000\u0000\u008d\u0014\u0001\u0000\u0000\u0000\u008e\u008f"+
		"\u0005d\u0000\u0000\u008f\u0090\u0005e\u0000\u0000\u0090\u0091\u0005s"+
		"\u0000\u0000\u0091\u0092\u0005c\u0000\u0000\u0092\u0093\u0005:\u0000\u0000"+
		"\u0093\u0016\u0001\u0000\u0000\u0000\u0094\u0095\u0005l\u0000\u0000\u0095"+
		"\u0096\u0005a\u0000\u0000\u0096\u0097\u0005b\u0000\u0000\u0097\u0098\u0005"+
		"e\u0000\u0000\u0098\u0099\u0005l\u0000\u0000\u0099\u009a\u0005:\u0000"+
		"\u0000\u009a\u0018\u0001\u0000\u0000\u0000\u009b\u009c\u0005l\u0000\u0000"+
		"\u009c\u009d\u0005a\u0000\u0000\u009d\u009e\u0005b\u0000\u0000\u009e\u009f"+
		"\u0005e\u0000\u0000\u009f\u00a0\u0005l\u0000\u0000\u00a0\u00a1\u00055"+
		"\u0000\u0000\u00a1\u00a2\u0005:\u0000\u0000\u00a2\u001a\u0001\u0000\u0000"+
		"\u0000\u00a3\u00a4\u0005l\u0000\u0000\u00a4\u00a5\u0005a\u0000\u0000\u00a5"+
		"\u00a6\u0005b\u0000\u0000\u00a6\u00a7\u0005e\u0000\u0000\u00a7\u00a8\u0005"+
		"l\u0000\u0000\u00a8\u00a9\u00052\u0000\u0000\u00a9\u00aa\u0005:\u0000"+
		"\u0000\u00aa\u001c\u0001\u0000\u0000\u0000\u00ab\u00ac\u0005t\u0000\u0000"+
		"\u00ac\u00ad\u0005e\u0000\u0000\u00ad\u00ae\u0005m\u0000\u0000\u00ae\u00af"+
		"\u0005p\u0000\u0000\u00af\u00b0\u0005l\u0000\u0000\u00b0\u00b1\u0005a"+
		"\u0000\u0000\u00b1\u00b2\u0005t\u0000\u0000\u00b2\u00b3\u0005e\u0000\u0000"+
		"\u00b3\u00b4\u0005:\u0000\u0000\u00b4\u001e\u0001\u0000\u0000\u0000\u00b5"+
		"\u00b6\u0005n\u0000\u0000\u00b6\u00b7\u0005e\u0000\u0000\u00b7\u00b8\u0005"+
		"g\u0000\u0000\u00b8\u00b9\u0005a\u0000\u0000\u00b9\u00ba\u0005t\u0000"+
		"\u0000\u00ba\u00bb\u0005i\u0000\u0000\u00bb\u00bc\u0005v\u0000\u0000\u00bc"+
		"\u00bd\u0005e\u0000\u0000\u00bd\u00be\u0005_\u0000\u0000\u00be\u00bf\u0005"+
		"i\u0000\u0000\u00bf\u00c0\u0005n\u0000\u0000\u00c0\u00c1\u0005d\u0000"+
		"\u0000\u00c1\u00c2\u0005e\u0000\u0000\u00c2\u00c3\u0005x\u0000\u0000\u00c3"+
		" \u0001\u0000\u0000\u0000\u00c4\u00c6\u0005-\u0000\u0000\u00c5\u00c4\u0001"+
		"\u0000\u0000\u0000\u00c5\u00c6\u0001\u0000\u0000\u0000\u00c6\u00c8\u0001"+
		"\u0000\u0000\u0000\u00c7\u00c9\u000209\u0000\u00c8\u00c7\u0001\u0000\u0000"+
		"\u0000\u00c9\u00ca\u0001\u0000\u0000\u0000\u00ca\u00c8\u0001\u0000\u0000"+
		"\u0000\u00ca\u00cb\u0001\u0000\u0000\u0000\u00cb\"\u0001\u0000\u0000\u0000"+
		"\u00cc\u00ce\u0007\u0001\u0000\u0000\u00cd\u00cc\u0001\u0000\u0000\u0000"+
		"\u00ce\u00cf\u0001\u0000\u0000\u0000\u00cf\u00cd\u0001\u0000\u0000\u0000"+
		"\u00cf\u00d0\u0001\u0000\u0000\u0000\u00d0$\u0001\u0000\u0000\u0000\u00d1"+
		"\u00d3\u0005<\u0000\u0000\u00d2\u00d4\u0002AZ\u0000\u00d3\u00d2\u0001"+
		"\u0000\u0000\u0000\u00d4\u00d5\u0001\u0000\u0000\u0000\u00d5\u00d3\u0001"+
		"\u0000\u0000\u0000\u00d5\u00d6\u0001\u0000\u0000\u0000\u00d6\u00d7\u0001"+
		"\u0000\u0000\u0000\u00d7\u00d8\u0005>\u0000\u0000\u00d8&\u0001\u0000\u0000"+
		"\u0000\n\u0000+18<^\u00c5\u00ca\u00cf\u00d5\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}