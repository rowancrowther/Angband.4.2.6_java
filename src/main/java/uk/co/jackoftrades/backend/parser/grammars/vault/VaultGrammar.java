// Generated from src/main/java/uk/co/jackoftrades/backend/parser/grammars/VaultGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.vault;

    import uk.co.jackoftrades.backend.parser.vault.VaultParseRecord;

    import java.util.ArrayList;
    import java.util.List;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class VaultGrammar extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		RECORD_COUNT=1, NAME=2, TYPE=3, RATING=4, ROWS=5, COLUMNS=6, MIN_DEPTH=7, 
		MAX_DEPTH=8, FLAGS=9, D=10, INTEGER=11, COMMENT=12, EOL=13, FLAG=14, OR=15, 
		FLAG_EOL=16, STRING=17, END_OF_LINE=18;
	public static final int
		RULE_recordCount = 0, RULE_name = 1, RULE_type = 2, RULE_rating = 3, RULE_rows = 4, 
		RULE_columns = 5, RULE_minDepth = 6, RULE_maxDepth = 7, RULE_flag = 8, 
		RULE_d = 9, RULE_profileRecord = 10, RULE_file = 11;
	private static String[] makeRuleNames() {
		return new String[] {
			"recordCount", "name", "type", "rating", "rows", "columns", "minDepth", 
			"maxDepth", "flag", "d", "profileRecord", "file"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'record-count:'", "'name:'", "'type:'", "'rating:'", "'rows:'", 
			"'columns:'", "'min-depth:'", "'max-depth:'", "'flags:'", "'D:'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "RECORD_COUNT", "NAME", "TYPE", "RATING", "ROWS", "COLUMNS", "MIN_DEPTH", 
			"MAX_DEPTH", "FLAGS", "D", "INTEGER", "COMMENT", "EOL", "FLAG", "OR", 
			"FLAG_EOL", "STRING", "END_OF_LINE"
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

	@Override
	public String getGrammarFileName() { return "VaultGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public VaultGrammar(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RecordCountContext extends ParserRuleContext {
		public String count;
		public Token c;
		public TerminalNode RECORD_COUNT() { return getToken(VaultGrammar.RECORD_COUNT, 0); }
		public TerminalNode INTEGER() { return getToken(VaultGrammar.INTEGER, 0); }
		public RecordCountContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_recordCount; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).enterRecordCount(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).exitRecordCount(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VaultGrammarVisitor ) return ((VaultGrammarVisitor<? extends T>)visitor).visitRecordCount(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RecordCountContext recordCount() throws RecognitionException {
		RecordCountContext _localctx = new RecordCountContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_recordCount);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(24);
			match(RECORD_COUNT);
			setState(25);
			((RecordCountContext)_localctx).c = match(INTEGER);
			 ((RecordCountContext)_localctx).count =  ((RecordCountContext)_localctx).c.getText(); 
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NameContext extends ParserRuleContext {
		public String nameStr;
		public int line;
		public Token n;
		public TerminalNode NAME() { return getToken(VaultGrammar.NAME, 0); }
		public TerminalNode STRING() { return getToken(VaultGrammar.STRING, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VaultGrammarVisitor ) return ((VaultGrammarVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(28);
			match(NAME);
			setState(29);
			((NameContext)_localctx).n = match(STRING);
			 ((NameContext)_localctx).nameStr =  ((NameContext)_localctx).n.getText(); ((NameContext)_localctx).line =  _localctx.start.getLine(); 
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeContext extends ParserRuleContext {
		public String typeStr;
		public Token t;
		public TerminalNode TYPE() { return getToken(VaultGrammar.TYPE, 0); }
		public TerminalNode STRING() { return getToken(VaultGrammar.STRING, 0); }
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).exitType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VaultGrammarVisitor ) return ((VaultGrammarVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(32);
			match(TYPE);
			setState(33);
			((TypeContext)_localctx).t = match(STRING);
			 ((TypeContext)_localctx).typeStr =  ((TypeContext)_localctx).t.getText(); 
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RatingContext extends ParserRuleContext {
		public String ratingStr;
		public Token r;
		public TerminalNode RATING() { return getToken(VaultGrammar.RATING, 0); }
		public TerminalNode INTEGER() { return getToken(VaultGrammar.INTEGER, 0); }
		public RatingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rating; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).enterRating(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).exitRating(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VaultGrammarVisitor ) return ((VaultGrammarVisitor<? extends T>)visitor).visitRating(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RatingContext rating() throws RecognitionException {
		RatingContext _localctx = new RatingContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_rating);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(36);
			match(RATING);
			setState(37);
			((RatingContext)_localctx).r = match(INTEGER);
			 ((RatingContext)_localctx).ratingStr =  ((RatingContext)_localctx).r.getText(); 
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RowsContext extends ParserRuleContext {
		public String rowsStr;
		public Token r;
		public TerminalNode ROWS() { return getToken(VaultGrammar.ROWS, 0); }
		public TerminalNode INTEGER() { return getToken(VaultGrammar.INTEGER, 0); }
		public RowsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rows; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).enterRows(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).exitRows(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VaultGrammarVisitor ) return ((VaultGrammarVisitor<? extends T>)visitor).visitRows(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RowsContext rows() throws RecognitionException {
		RowsContext _localctx = new RowsContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_rows);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(40);
			match(ROWS);
			setState(41);
			((RowsContext)_localctx).r = match(INTEGER);
			 ((RowsContext)_localctx).rowsStr =  ((RowsContext)_localctx).r.getText(); 
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ColumnsContext extends ParserRuleContext {
		public String columnsStr;
		public Token c;
		public TerminalNode COLUMNS() { return getToken(VaultGrammar.COLUMNS, 0); }
		public TerminalNode INTEGER() { return getToken(VaultGrammar.INTEGER, 0); }
		public ColumnsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_columns; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).enterColumns(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).exitColumns(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VaultGrammarVisitor ) return ((VaultGrammarVisitor<? extends T>)visitor).visitColumns(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ColumnsContext columns() throws RecognitionException {
		ColumnsContext _localctx = new ColumnsContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_columns);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			match(COLUMNS);
			setState(45);
			((ColumnsContext)_localctx).c = match(INTEGER);
			 ((ColumnsContext)_localctx).columnsStr =  ((ColumnsContext)_localctx).c.getText(); 
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MinDepthContext extends ParserRuleContext {
		public String minDepthStr;
		public Token m;
		public TerminalNode MIN_DEPTH() { return getToken(VaultGrammar.MIN_DEPTH, 0); }
		public TerminalNode INTEGER() { return getToken(VaultGrammar.INTEGER, 0); }
		public MinDepthContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_minDepth; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).enterMinDepth(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).exitMinDepth(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VaultGrammarVisitor ) return ((VaultGrammarVisitor<? extends T>)visitor).visitMinDepth(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MinDepthContext minDepth() throws RecognitionException {
		MinDepthContext _localctx = new MinDepthContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_minDepth);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			match(MIN_DEPTH);
			setState(49);
			((MinDepthContext)_localctx).m = match(INTEGER);
			 ((MinDepthContext)_localctx).minDepthStr =  ((MinDepthContext)_localctx).m.getText(); 
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MaxDepthContext extends ParserRuleContext {
		public String maxDepthStr;
		public Token m;
		public TerminalNode MAX_DEPTH() { return getToken(VaultGrammar.MAX_DEPTH, 0); }
		public TerminalNode INTEGER() { return getToken(VaultGrammar.INTEGER, 0); }
		public MaxDepthContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_maxDepth; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).enterMaxDepth(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).exitMaxDepth(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VaultGrammarVisitor ) return ((VaultGrammarVisitor<? extends T>)visitor).visitMaxDepth(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MaxDepthContext maxDepth() throws RecognitionException {
		MaxDepthContext _localctx = new MaxDepthContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_maxDepth);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			match(MAX_DEPTH);
			setState(53);
			((MaxDepthContext)_localctx).m = match(INTEGER);
			 ((MaxDepthContext)_localctx).maxDepthStr =  ((MaxDepthContext)_localctx).m.getText(); 
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FlagContext extends ParserRuleContext {
		public List<String> flags;
		public Token f1;
		public Token f2;
		public TerminalNode FLAGS() { return getToken(VaultGrammar.FLAGS, 0); }
		public List<TerminalNode> FLAG() { return getTokens(VaultGrammar.FLAG); }
		public TerminalNode FLAG(int i) {
			return getToken(VaultGrammar.FLAG, i);
		}
		public List<TerminalNode> OR() { return getTokens(VaultGrammar.OR); }
		public TerminalNode OR(int i) {
			return getToken(VaultGrammar.OR, i);
		}
		public FlagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).enterFlag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).exitFlag(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VaultGrammarVisitor ) return ((VaultGrammarVisitor<? extends T>)visitor).visitFlag(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlagContext flag() throws RecognitionException {
		FlagContext _localctx = new FlagContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_flag);
		 ((FlagContext)_localctx).flags =  new ArrayList<>(); 
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			match(FLAGS);
			setState(57);
			((FlagContext)_localctx).f1 = match(FLAG);
			 _localctx.flags.add(((FlagContext)_localctx).f1.getText()); 
			setState(64);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OR) {
				{
				{
				setState(59);
				match(OR);
				setState(60);
				((FlagContext)_localctx).f2 = match(FLAG);
				 _localctx.flags.add(((FlagContext)_localctx).f2.getText()); 
				}
				}
				setState(66);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DContext extends ParserRuleContext {
		public List<String> map;
		public Token m;
		public List<TerminalNode> D() { return getTokens(VaultGrammar.D); }
		public TerminalNode D(int i) {
			return getToken(VaultGrammar.D, i);
		}
		public List<TerminalNode> STRING() { return getTokens(VaultGrammar.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(VaultGrammar.STRING, i);
		}
		public DContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_d; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).enterD(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).exitD(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VaultGrammarVisitor ) return ((VaultGrammarVisitor<? extends T>)visitor).visitD(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DContext d() throws RecognitionException {
		DContext _localctx = new DContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_d);
		 ((DContext)_localctx).map =  new ArrayList<>(); 
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(67);
				match(D);
				setState(68);
				((DContext)_localctx).m = match(STRING);
				 _localctx.map.add(((DContext)_localctx).m.getText()); 
				}
				}
				setState(72); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==D );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProfileRecordContext extends ParserRuleContext {
		public VaultParseRecord record;
		public NameContext name;
		public TypeContext type;
		public RatingContext rating;
		public RowsContext rows;
		public ColumnsContext columns;
		public MinDepthContext minDepth;
		public MaxDepthContext maxDepth;
		public FlagContext flag;
		public DContext d;
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public RatingContext rating() {
			return getRuleContext(RatingContext.class,0);
		}
		public RowsContext rows() {
			return getRuleContext(RowsContext.class,0);
		}
		public ColumnsContext columns() {
			return getRuleContext(ColumnsContext.class,0);
		}
		public MinDepthContext minDepth() {
			return getRuleContext(MinDepthContext.class,0);
		}
		public MaxDepthContext maxDepth() {
			return getRuleContext(MaxDepthContext.class,0);
		}
		public DContext d() {
			return getRuleContext(DContext.class,0);
		}
		public List<FlagContext> flag() {
			return getRuleContexts(FlagContext.class);
		}
		public FlagContext flag(int i) {
			return getRuleContext(FlagContext.class,i);
		}
		public ProfileRecordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_profileRecord; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).enterProfileRecord(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).exitProfileRecord(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VaultGrammarVisitor ) return ((VaultGrammarVisitor<? extends T>)visitor).visitProfileRecord(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProfileRecordContext profileRecord() throws RecognitionException {
		ProfileRecordContext _localctx = new ProfileRecordContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_profileRecord);

		            String nameInit = "";
		            String typeInit = "";
		            String ratingInit = "";
		            String rowsInit = "";
		            String columnsInit = "";
		            String minDepthInit = "";
		            String maxDepthInit = "";
		            List<String> flags = new ArrayList<>();
		            String mapText = "";
		            List<String> map = new ArrayList<>();
		            int line = 0;
		        
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			((ProfileRecordContext)_localctx).name = name();

			                line = ((ProfileRecordContext)_localctx).name.line;
			                nameInit = ((ProfileRecordContext)_localctx).name.nameStr;
			            
			setState(76);
			((ProfileRecordContext)_localctx).type = type();
			 typeInit = ((ProfileRecordContext)_localctx).type.typeStr; 
			setState(78);
			((ProfileRecordContext)_localctx).rating = rating();
			 ratingInit = ((ProfileRecordContext)_localctx).rating.ratingStr; 
			setState(80);
			((ProfileRecordContext)_localctx).rows = rows();
			 rowsInit = ((ProfileRecordContext)_localctx).rows.rowsStr; 
			setState(82);
			((ProfileRecordContext)_localctx).columns = columns();
			 columnsInit = ((ProfileRecordContext)_localctx).columns.columnsStr; 
			setState(84);
			((ProfileRecordContext)_localctx).minDepth = minDepth();
			 minDepthInit = ((ProfileRecordContext)_localctx).minDepth.minDepthStr; 
			setState(86);
			((ProfileRecordContext)_localctx).maxDepth = maxDepth();
			 maxDepthInit = ((ProfileRecordContext)_localctx).maxDepth.maxDepthStr; 
			setState(93);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FLAGS) {
				{
				{
				setState(88);
				((ProfileRecordContext)_localctx).flag = flag();
				 flags.addAll(((ProfileRecordContext)_localctx).flag.flags); 
				}
				}
				setState(95);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(96);
			((ProfileRecordContext)_localctx).d = d();

			                map.addAll(((ProfileRecordContext)_localctx).d.map);
			                StringBuilder sb = new StringBuilder();
			                for (String mapLine : map) {
			                    sb.append(mapLine);
			                }
			                mapText = sb.toString();
			            
			}
			_ctx.stop = _input.LT(-1);

			            ((ProfileRecordContext)_localctx).record =  new VaultParseRecord(nameInit, typeInit, mapText, map, ratingInit,
			                rowsInit, columnsInit, minDepthInit, maxDepthInit, flags, line);
			        
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FileContext extends ParserRuleContext {
		public List<VaultParseRecord> profiles;
		public String declaredRecordCount;
		public RecordCountContext recordCount;
		public ProfileRecordContext profileRecord;
		public RecordCountContext recordCount() {
			return getRuleContext(RecordCountContext.class,0);
		}
		public TerminalNode EOF() { return getToken(VaultGrammar.EOF, 0); }
		public List<ProfileRecordContext> profileRecord() {
			return getRuleContexts(ProfileRecordContext.class);
		}
		public ProfileRecordContext profileRecord(int i) {
			return getRuleContext(ProfileRecordContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof VaultGrammarListener ) ((VaultGrammarListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof VaultGrammarVisitor ) return ((VaultGrammarVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_file);
		 ((FileContext)_localctx).profiles =  new ArrayList<>(); 
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			((FileContext)_localctx).recordCount = recordCount();
			 ((FileContext)_localctx).declaredRecordCount =  ((FileContext)_localctx).recordCount.count; 
			setState(104); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(101);
				((FileContext)_localctx).profileRecord = profileRecord();
				 _localctx.profiles.add(((FileContext)_localctx).profileRecord.record); 
				}
				}
				setState(106); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==NAME );
			setState(108);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u0012o\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0005"+
		"\b?\b\b\n\b\f\bB\t\b\u0001\t\u0001\t\u0001\t\u0004\tG\b\t\u000b\t\f\t"+
		"H\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0005"+
		"\n\\\b\n\n\n\f\n_\t\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0004\u000bi\b\u000b\u000b\u000b"+
		"\f\u000bj\u0001\u000b\u0001\u000b\u0001\u000b\u0000\u0000\f\u0000\u0002"+
		"\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0000\u0000f\u0000\u0018"+
		"\u0001\u0000\u0000\u0000\u0002\u001c\u0001\u0000\u0000\u0000\u0004 \u0001"+
		"\u0000\u0000\u0000\u0006$\u0001\u0000\u0000\u0000\b(\u0001\u0000\u0000"+
		"\u0000\n,\u0001\u0000\u0000\u0000\f0\u0001\u0000\u0000\u0000\u000e4\u0001"+
		"\u0000\u0000\u0000\u00108\u0001\u0000\u0000\u0000\u0012F\u0001\u0000\u0000"+
		"\u0000\u0014J\u0001\u0000\u0000\u0000\u0016c\u0001\u0000\u0000\u0000\u0018"+
		"\u0019\u0005\u0001\u0000\u0000\u0019\u001a\u0005\u000b\u0000\u0000\u001a"+
		"\u001b\u0006\u0000\uffff\uffff\u0000\u001b\u0001\u0001\u0000\u0000\u0000"+
		"\u001c\u001d\u0005\u0002\u0000\u0000\u001d\u001e\u0005\u0011\u0000\u0000"+
		"\u001e\u001f\u0006\u0001\uffff\uffff\u0000\u001f\u0003\u0001\u0000\u0000"+
		"\u0000 !\u0005\u0003\u0000\u0000!\"\u0005\u0011\u0000\u0000\"#\u0006\u0002"+
		"\uffff\uffff\u0000#\u0005\u0001\u0000\u0000\u0000$%\u0005\u0004\u0000"+
		"\u0000%&\u0005\u000b\u0000\u0000&\'\u0006\u0003\uffff\uffff\u0000\'\u0007"+
		"\u0001\u0000\u0000\u0000()\u0005\u0005\u0000\u0000)*\u0005\u000b\u0000"+
		"\u0000*+\u0006\u0004\uffff\uffff\u0000+\t\u0001\u0000\u0000\u0000,-\u0005"+
		"\u0006\u0000\u0000-.\u0005\u000b\u0000\u0000./\u0006\u0005\uffff\uffff"+
		"\u0000/\u000b\u0001\u0000\u0000\u000001\u0005\u0007\u0000\u000012\u0005"+
		"\u000b\u0000\u000023\u0006\u0006\uffff\uffff\u00003\r\u0001\u0000\u0000"+
		"\u000045\u0005\b\u0000\u000056\u0005\u000b\u0000\u000067\u0006\u0007\uffff"+
		"\uffff\u00007\u000f\u0001\u0000\u0000\u000089\u0005\t\u0000\u00009:\u0005"+
		"\u000e\u0000\u0000:@\u0006\b\uffff\uffff\u0000;<\u0005\u000f\u0000\u0000"+
		"<=\u0005\u000e\u0000\u0000=?\u0006\b\uffff\uffff\u0000>;\u0001\u0000\u0000"+
		"\u0000?B\u0001\u0000\u0000\u0000@>\u0001\u0000\u0000\u0000@A\u0001\u0000"+
		"\u0000\u0000A\u0011\u0001\u0000\u0000\u0000B@\u0001\u0000\u0000\u0000"+
		"CD\u0005\n\u0000\u0000DE\u0005\u0011\u0000\u0000EG\u0006\t\uffff\uffff"+
		"\u0000FC\u0001\u0000\u0000\u0000GH\u0001\u0000\u0000\u0000HF\u0001\u0000"+
		"\u0000\u0000HI\u0001\u0000\u0000\u0000I\u0013\u0001\u0000\u0000\u0000"+
		"JK\u0003\u0002\u0001\u0000KL\u0006\n\uffff\uffff\u0000LM\u0003\u0004\u0002"+
		"\u0000MN\u0006\n\uffff\uffff\u0000NO\u0003\u0006\u0003\u0000OP\u0006\n"+
		"\uffff\uffff\u0000PQ\u0003\b\u0004\u0000QR\u0006\n\uffff\uffff\u0000R"+
		"S\u0003\n\u0005\u0000ST\u0006\n\uffff\uffff\u0000TU\u0003\f\u0006\u0000"+
		"UV\u0006\n\uffff\uffff\u0000VW\u0003\u000e\u0007\u0000W]\u0006\n\uffff"+
		"\uffff\u0000XY\u0003\u0010\b\u0000YZ\u0006\n\uffff\uffff\u0000Z\\\u0001"+
		"\u0000\u0000\u0000[X\u0001\u0000\u0000\u0000\\_\u0001\u0000\u0000\u0000"+
		"][\u0001\u0000\u0000\u0000]^\u0001\u0000\u0000\u0000^`\u0001\u0000\u0000"+
		"\u0000_]\u0001\u0000\u0000\u0000`a\u0003\u0012\t\u0000ab\u0006\n\uffff"+
		"\uffff\u0000b\u0015\u0001\u0000\u0000\u0000cd\u0003\u0000\u0000\u0000"+
		"dh\u0006\u000b\uffff\uffff\u0000ef\u0003\u0014\n\u0000fg\u0006\u000b\uffff"+
		"\uffff\u0000gi\u0001\u0000\u0000\u0000he\u0001\u0000\u0000\u0000ij\u0001"+
		"\u0000\u0000\u0000jh\u0001\u0000\u0000\u0000jk\u0001\u0000\u0000\u0000"+
		"kl\u0001\u0000\u0000\u0000lm\u0005\u0000\u0000\u0001m\u0017\u0001\u0000"+
		"\u0000\u0000\u0004@H]j";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}