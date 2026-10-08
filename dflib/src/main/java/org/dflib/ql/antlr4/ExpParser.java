// Generated from org/dflib/ql/antlr4/Exp.g4 by ANTLR 4.13.2
package org.dflib.ql.antlr4;

import java.util.stream.Collectors;

import org.dflib.*;

import static org.dflib.ql.antlr4.ExpParserUtils.*;
import static org.dflib.ql.antlr4.Literals.*;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ExpParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		LP=1, RP=2, COMMA=3, NOT=4, EQ=5, NE=6, LE=7, GE=8, LT=9, GT=10, BETWEEN=11, 
		IN=12, ADD=13, SUB=14, MUL=15, DIV=16, MOD=17, AND=18, OR=19, BOOL=20, 
		INT=21, LONG=22, BIGINT=23, FLOAT=24, DOUBLE=25, DECIMAL=26, STR=27, COL=28, 
		DATE=29, TIME=30, DATETIME=31, OFFSET_DATETIME=32, ARRAY=33, NULL=34, 
		TRUE=35, FALSE=36, ASC=37, DESC=38, AS=39, PARAMETER=40, INTEGER_LITERAL=41, 
		FLOAT_LITERAL=42, STRING_LITERAL=43, QUOTED_IDENTIFIER=44, IDENTIFIER=45, 
		WS=46;
	public static final int
		RULE_expRoot = 0, RULE_expSingle = 1, RULE_expArray = 2, RULE_sorterRoot = 3, 
		RULE_sorterSingle = 4, RULE_sorterArray = 5, RULE_expression = 6, RULE_orExp = 7, 
		RULE_andExp = 8, RULE_notExp = 9, RULE_eqExp = 10, RULE_cmpExp = 11, RULE_addExp = 12, 
		RULE_mulExp = 13, RULE_unaryExp = 14, RULE_primary = 15, RULE_fnCall = 16, 
		RULE_scalar = 17, RULE_anyScalarList = 18, RULE_boolScalar = 19, RULE_numScalar = 20, 
		RULE_integerScalar = 21, RULE_floatingPointScalar = 22, RULE_strScalar = 23, 
		RULE_column = 24, RULE_columnId = 25, RULE_identifier = 26, RULE_array = 27, 
		RULE_keywordAsIdentifier = 28;
	private static String[] makeRuleNames() {
		return new String[] {
			"expRoot", "expSingle", "expArray", "sorterRoot", "sorterSingle", "sorterArray", 
			"expression", "orExp", "andExp", "notExp", "eqExp", "cmpExp", "addExp", 
			"mulExp", "unaryExp", "primary", "fnCall", "scalar", "anyScalarList", 
			"boolScalar", "numScalar", "integerScalar", "floatingPointScalar", "strScalar", 
			"column", "columnId", "identifier", "array", "keywordAsIdentifier"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'('", "')'", "','", "'not'", "'='", "'!='", "'<='", "'>='", "'<'", 
			"'>'", "'between'", "'in'", "'+'", "'-'", "'*'", "'/'", "'%'", "'and'", 
			"'or'", "'bool'", "'int'", "'long'", "'bigint'", "'float'", "'double'", 
			"'decimal'", "'str'", "'col'", "'date'", "'time'", "'dateTime'", "'offsetDateTime'", 
			"'array'", "'null'", "'true'", "'false'", "'asc'", "'desc'", "'as'", 
			"'?'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "LP", "RP", "COMMA", "NOT", "EQ", "NE", "LE", "GE", "LT", "GT", 
			"BETWEEN", "IN", "ADD", "SUB", "MUL", "DIV", "MOD", "AND", "OR", "BOOL", 
			"INT", "LONG", "BIGINT", "FLOAT", "DOUBLE", "DECIMAL", "STR", "COL", 
			"DATE", "TIME", "DATETIME", "OFFSET_DATETIME", "ARRAY", "NULL", "TRUE", 
			"FALSE", "ASC", "DESC", "AS", "PARAMETER", "INTEGER_LITERAL", "FLOAT_LITERAL", 
			"STRING_LITERAL", "QUOTED_IDENTIFIER", "IDENTIFIER", "WS"
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
	public String getGrammarFileName() { return "Exp.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }


	// global state of the parser
	PositionalParamSource paramSource;
	Environment env;

	public void setParameters(Object... params) {
	    this.paramSource = new PositionalParamSource(params);
	}

	public void setEnv(Environment env) {
	    this.env = env;
	}

	public ExpParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpRootContext extends ParserRuleContext {
		public Exp<?> exp;
		public ExpSingleContext expSingle;
		public ExpSingleContext expSingle() {
			return getRuleContext(ExpSingleContext.class,0);
		}
		public TerminalNode EOF() { return getToken(ExpParser.EOF, 0); }
		public ExpRootContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expRoot; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterExpRoot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitExpRoot(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitExpRoot(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpRootContext expRoot() throws RecognitionException {
		ExpRootContext _localctx = new ExpRootContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_expRoot);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58);
			((ExpRootContext)_localctx).expSingle = expSingle();
			setState(59);
			match(EOF);
			 ((ExpRootContext)_localctx).exp =  ((ExpRootContext)_localctx).expSingle.exp; 
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
	public static class ExpSingleContext extends ParserRuleContext {
		public Exp<?> exp;
		public String alias;
		public ExpressionContext expression;
		public IdentifierContext identifier;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode AS() { return getToken(ExpParser.AS, 0); }
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public ExpSingleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expSingle; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterExpSingle(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitExpSingle(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitExpSingle(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpSingleContext expSingle() throws RecognitionException {
		ExpSingleContext _localctx = new ExpSingleContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_expSingle);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(62);
			((ExpSingleContext)_localctx).expression = expression();
			setState(67);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS) {
				{
				setState(63);
				match(AS);
				setState(64);
				((ExpSingleContext)_localctx).identifier = identifier();
				 ((ExpSingleContext)_localctx).alias =  ((ExpSingleContext)_localctx).identifier.id; 
				}
			}

			 ((ExpSingleContext)_localctx).exp =  _localctx.alias == null ? ((ExpSingleContext)_localctx).expression.exp : ((ExpSingleContext)_localctx).expression.exp.as(_localctx.alias); 
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
	public static class ExpArrayContext extends ParserRuleContext {
		public Exp[] expressions;
		public ExpSingleContext expSingle;
		public List<ExpSingleContext> args = new ArrayList<ExpSingleContext>();
		public TerminalNode EOF() { return getToken(ExpParser.EOF, 0); }
		public List<ExpSingleContext> expSingle() {
			return getRuleContexts(ExpSingleContext.class);
		}
		public ExpSingleContext expSingle(int i) {
			return getRuleContext(ExpSingleContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(ExpParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(ExpParser.COMMA, i);
		}
		public ExpArrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expArray; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterExpArray(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitExpArray(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitExpArray(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpArrayContext expArray() throws RecognitionException {
		ExpArrayContext _localctx = new ExpArrayContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_expArray);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(71);
			((ExpArrayContext)_localctx).expSingle = expSingle();
			((ExpArrayContext)_localctx).args.add(((ExpArrayContext)_localctx).expSingle);
			setState(76);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(72);
				match(COMMA);
				setState(73);
				((ExpArrayContext)_localctx).expSingle = expSingle();
				((ExpArrayContext)_localctx).args.add(((ExpArrayContext)_localctx).expSingle);
				}
				}
				setState(78);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(79);
			match(EOF);
			 ((ExpArrayContext)_localctx).expressions =  ((ExpArrayContext)_localctx).args.stream().map(e -> e.exp).toArray(Exp[]::new); 
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
	public static class SorterRootContext extends ParserRuleContext {
		public Sorter sorter;
		public SorterSingleContext sorterSingle;
		public SorterSingleContext sorterSingle() {
			return getRuleContext(SorterSingleContext.class,0);
		}
		public TerminalNode EOF() { return getToken(ExpParser.EOF, 0); }
		public SorterRootContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sorterRoot; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterSorterRoot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitSorterRoot(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitSorterRoot(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SorterRootContext sorterRoot() throws RecognitionException {
		SorterRootContext _localctx = new SorterRootContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_sorterRoot);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			((SorterRootContext)_localctx).sorterSingle = sorterSingle();
			setState(83);
			match(EOF);
			 ((SorterRootContext)_localctx).sorter =  ((SorterRootContext)_localctx).sorterSingle.sorter; 
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
	public static class SorterSingleContext extends ParserRuleContext {
		public Sorter sorter;
		public boolean desc;
		public ExpressionContext expression;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode ASC() { return getToken(ExpParser.ASC, 0); }
		public TerminalNode DESC() { return getToken(ExpParser.DESC, 0); }
		public SorterSingleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sorterSingle; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterSorterSingle(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitSorterSingle(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitSorterSingle(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SorterSingleContext sorterSingle() throws RecognitionException {
		SorterSingleContext _localctx = new SorterSingleContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_sorterSingle);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(86);
			((SorterSingleContext)_localctx).expression = expression();
			setState(90);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ASC:
				{
				setState(87);
				match(ASC);
				}
				break;
			case DESC:
				{
				setState(88);
				match(DESC);
				 ((SorterSingleContext)_localctx).desc =  true; 
				}
				break;
			case EOF:
			case COMMA:
				break;
			default:
				break;
			}
			 ((SorterSingleContext)_localctx).sorter =  _localctx.desc ? ((SorterSingleContext)_localctx).expression.exp.desc() : ((SorterSingleContext)_localctx).expression.exp.asc(); 
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
	public static class SorterArrayContext extends ParserRuleContext {
		public Sorter[] sorters;
		public SorterSingleContext sorterSingle;
		public List<SorterSingleContext> args = new ArrayList<SorterSingleContext>();
		public TerminalNode EOF() { return getToken(ExpParser.EOF, 0); }
		public List<SorterSingleContext> sorterSingle() {
			return getRuleContexts(SorterSingleContext.class);
		}
		public SorterSingleContext sorterSingle(int i) {
			return getRuleContext(SorterSingleContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(ExpParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(ExpParser.COMMA, i);
		}
		public SorterArrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sorterArray; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterSorterArray(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitSorterArray(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitSorterArray(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SorterArrayContext sorterArray() throws RecognitionException {
		SorterArrayContext _localctx = new SorterArrayContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_sorterArray);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(94);
			((SorterArrayContext)_localctx).sorterSingle = sorterSingle();
			((SorterArrayContext)_localctx).args.add(((SorterArrayContext)_localctx).sorterSingle);
			setState(99);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(95);
				match(COMMA);
				setState(96);
				((SorterArrayContext)_localctx).sorterSingle = sorterSingle();
				((SorterArrayContext)_localctx).args.add(((SorterArrayContext)_localctx).sorterSingle);
				}
				}
				setState(101);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(102);
			match(EOF);
			 ((SorterArrayContext)_localctx).sorters =  ((SorterArrayContext)_localctx).args.stream().map(s -> s.sorter).toArray(Sorter[]::new); 
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
	public static class ExpressionContext extends ParserRuleContext {
		public Exp<?> exp;
		public OrExpContext orExp;
		public OrExpContext orExp() {
			return getRuleContext(OrExpContext.class,0);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(105);
			((ExpressionContext)_localctx).orExp = orExp();
			 ((ExpressionContext)_localctx).exp =  ((ExpressionContext)_localctx).orExp.exp; 
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
	public static class OrExpContext extends ParserRuleContext {
		public Exp<?> exp;
		public AndExpContext a;
		public Token op;
		public AndExpContext b;
		public List<AndExpContext> andExp() {
			return getRuleContexts(AndExpContext.class);
		}
		public AndExpContext andExp(int i) {
			return getRuleContext(AndExpContext.class,i);
		}
		public List<TerminalNode> OR() { return getTokens(ExpParser.OR); }
		public TerminalNode OR(int i) {
			return getToken(ExpParser.OR, i);
		}
		public OrExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orExp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterOrExp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitOrExp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitOrExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OrExpContext orExp() throws RecognitionException {
		OrExpContext _localctx = new OrExpContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_orExp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(108);
			((OrExpContext)_localctx).a = andExp();
			 ((OrExpContext)_localctx).exp =  ((OrExpContext)_localctx).a.exp; 
			setState(116);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OR) {
				{
				{
				setState(110);
				((OrExpContext)_localctx).op = match(OR);
				setState(111);
				((OrExpContext)_localctx).b = andExp();
				 ((OrExpContext)_localctx).exp =  logical(_localctx.exp, ((OrExpContext)_localctx).op, ((OrExpContext)_localctx).b.exp); 
				}
				}
				setState(118);
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
	public static class AndExpContext extends ParserRuleContext {
		public Exp<?> exp;
		public NotExpContext a;
		public Token op;
		public NotExpContext b;
		public List<NotExpContext> notExp() {
			return getRuleContexts(NotExpContext.class);
		}
		public NotExpContext notExp(int i) {
			return getRuleContext(NotExpContext.class,i);
		}
		public List<TerminalNode> AND() { return getTokens(ExpParser.AND); }
		public TerminalNode AND(int i) {
			return getToken(ExpParser.AND, i);
		}
		public AndExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_andExp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterAndExp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitAndExp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitAndExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AndExpContext andExp() throws RecognitionException {
		AndExpContext _localctx = new AndExpContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_andExp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(119);
			((AndExpContext)_localctx).a = notExp();
			 ((AndExpContext)_localctx).exp =  ((AndExpContext)_localctx).a.exp; 
			setState(127);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AND) {
				{
				{
				setState(121);
				((AndExpContext)_localctx).op = match(AND);
				setState(122);
				((AndExpContext)_localctx).b = notExp();
				 ((AndExpContext)_localctx).exp =  logical(_localctx.exp, ((AndExpContext)_localctx).op, ((AndExpContext)_localctx).b.exp); 
				}
				}
				setState(129);
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
	public static class NotExpContext extends ParserRuleContext {
		public Exp<?> exp;
		public Token op;
		public NotExpContext notExp;
		public EqExpContext eqExp;
		public NotExpContext notExp() {
			return getRuleContext(NotExpContext.class,0);
		}
		public TerminalNode NOT() { return getToken(ExpParser.NOT, 0); }
		public EqExpContext eqExp() {
			return getRuleContext(EqExpContext.class,0);
		}
		public NotExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_notExp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterNotExp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitNotExp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitNotExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NotExpContext notExp() throws RecognitionException {
		NotExpContext _localctx = new NotExpContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_notExp);
		try {
			setState(137);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NOT:
				enterOuterAlt(_localctx, 1);
				{
				setState(130);
				((NotExpContext)_localctx).op = match(NOT);
				setState(131);
				((NotExpContext)_localctx).notExp = notExp();
				 ((NotExpContext)_localctx).exp =  not(((NotExpContext)_localctx).notExp.exp, ((NotExpContext)_localctx).op); 
				}
				break;
			case LP:
			case SUB:
			case BOOL:
			case INT:
			case LONG:
			case BIGINT:
			case FLOAT:
			case DOUBLE:
			case DECIMAL:
			case STR:
			case COL:
			case DATE:
			case TIME:
			case DATETIME:
			case OFFSET_DATETIME:
			case ARRAY:
			case NULL:
			case TRUE:
			case FALSE:
			case ASC:
			case DESC:
			case PARAMETER:
			case INTEGER_LITERAL:
			case FLOAT_LITERAL:
			case STRING_LITERAL:
			case QUOTED_IDENTIFIER:
			case IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
				setState(134);
				((NotExpContext)_localctx).eqExp = eqExp();
				 ((NotExpContext)_localctx).exp =  ((NotExpContext)_localctx).eqExp.exp; 
				}
				break;
			default:
				throw new NoViableAltException(this);
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
	public static class EqExpContext extends ParserRuleContext {
		public Exp<?> exp;
		public CmpExpContext a;
		public Token op;
		public CmpExpContext b;
		public List<CmpExpContext> cmpExp() {
			return getRuleContexts(CmpExpContext.class);
		}
		public CmpExpContext cmpExp(int i) {
			return getRuleContext(CmpExpContext.class,i);
		}
		public List<TerminalNode> EQ() { return getTokens(ExpParser.EQ); }
		public TerminalNode EQ(int i) {
			return getToken(ExpParser.EQ, i);
		}
		public List<TerminalNode> NE() { return getTokens(ExpParser.NE); }
		public TerminalNode NE(int i) {
			return getToken(ExpParser.NE, i);
		}
		public EqExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eqExp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterEqExp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitEqExp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitEqExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EqExpContext eqExp() throws RecognitionException {
		EqExpContext _localctx = new EqExpContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_eqExp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(139);
			((EqExpContext)_localctx).a = cmpExp();
			 ((EqExpContext)_localctx).exp =  ((EqExpContext)_localctx).a.exp; 
			setState(147);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EQ || _la==NE) {
				{
				{
				setState(141);
				((EqExpContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==EQ || _la==NE) ) {
					((EqExpContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(142);
				((EqExpContext)_localctx).b = cmpExp();
				 ((EqExpContext)_localctx).exp =  rel(_localctx.exp, ((EqExpContext)_localctx).op, ((EqExpContext)_localctx).b.exp); 
				}
				}
				setState(149);
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
	public static class CmpExpContext extends ParserRuleContext {
		public Exp<?> exp;
		public boolean negate;
		public AddExpContext a;
		public Token op;
		public AddExpContext b;
		public AddExpContext c;
		public AnyScalarListContext l;
		public List<AddExpContext> addExp() {
			return getRuleContexts(AddExpContext.class);
		}
		public AddExpContext addExp(int i) {
			return getRuleContext(AddExpContext.class,i);
		}
		public TerminalNode AND() { return getToken(ExpParser.AND, 0); }
		public TerminalNode BETWEEN() { return getToken(ExpParser.BETWEEN, 0); }
		public TerminalNode IN() { return getToken(ExpParser.IN, 0); }
		public AnyScalarListContext anyScalarList() {
			return getRuleContext(AnyScalarListContext.class,0);
		}
		public TerminalNode GT() { return getToken(ExpParser.GT, 0); }
		public TerminalNode GE() { return getToken(ExpParser.GE, 0); }
		public TerminalNode LT() { return getToken(ExpParser.LT, 0); }
		public TerminalNode LE() { return getToken(ExpParser.LE, 0); }
		public TerminalNode NOT() { return getToken(ExpParser.NOT, 0); }
		public CmpExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cmpExp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterCmpExp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitCmpExp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitCmpExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CmpExpContext cmpExp() throws RecognitionException {
		CmpExpContext _localctx = new CmpExpContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_cmpExp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(150);
			((CmpExpContext)_localctx).a = addExp();
			 ((CmpExpContext)_localctx).exp =  ((CmpExpContext)_localctx).a.exp; 
			setState(176);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				{
				setState(152);
				((CmpExpContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 1920L) != 0)) ) {
					((CmpExpContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(153);
				((CmpExpContext)_localctx).b = addExp();
				 ((CmpExpContext)_localctx).exp =  rel(_localctx.exp, ((CmpExpContext)_localctx).op, ((CmpExpContext)_localctx).b.exp); 
				}
				break;
			case 2:
				{
				 ((CmpExpContext)_localctx).negate =  false; 
				setState(159);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(157);
					match(NOT);
					 ((CmpExpContext)_localctx).negate =  true; 
					}
				}

				setState(161);
				((CmpExpContext)_localctx).op = match(BETWEEN);
				setState(162);
				((CmpExpContext)_localctx).b = addExp();
				setState(163);
				match(AND);
				setState(164);
				((CmpExpContext)_localctx).c = addExp();

				            ((CmpExpContext)_localctx).exp =  between(_localctx.exp, ((CmpExpContext)_localctx).op, ((CmpExpContext)_localctx).b.exp, ((CmpExpContext)_localctx).c.exp, _localctx.negate);
				        
				}
				break;
			case 3:
				{
				 ((CmpExpContext)_localctx).negate =  false; 
				setState(170);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(168);
					match(NOT);
					 ((CmpExpContext)_localctx).negate =  true; 
					}
				}

				setState(172);
				((CmpExpContext)_localctx).op = match(IN);
				setState(173);
				((CmpExpContext)_localctx).l = anyScalarList();

				            ((CmpExpContext)_localctx).exp =  in(_localctx.exp, ((CmpExpContext)_localctx).op, ((CmpExpContext)_localctx).l.value, _localctx.negate);
				        
				}
				break;
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
	public static class AddExpContext extends ParserRuleContext {
		public Exp<?> exp;
		public MulExpContext a;
		public Token op;
		public MulExpContext b;
		public List<MulExpContext> mulExp() {
			return getRuleContexts(MulExpContext.class);
		}
		public MulExpContext mulExp(int i) {
			return getRuleContext(MulExpContext.class,i);
		}
		public List<TerminalNode> ADD() { return getTokens(ExpParser.ADD); }
		public TerminalNode ADD(int i) {
			return getToken(ExpParser.ADD, i);
		}
		public List<TerminalNode> SUB() { return getTokens(ExpParser.SUB); }
		public TerminalNode SUB(int i) {
			return getToken(ExpParser.SUB, i);
		}
		public AddExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_addExp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterAddExp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitAddExp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitAddExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AddExpContext addExp() throws RecognitionException {
		AddExpContext _localctx = new AddExpContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_addExp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(178);
			((AddExpContext)_localctx).a = mulExp();
			 ((AddExpContext)_localctx).exp =  ((AddExpContext)_localctx).a.exp; 
			setState(186);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ADD || _la==SUB) {
				{
				{
				setState(180);
				((AddExpContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==ADD || _la==SUB) ) {
					((AddExpContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(181);
				((AddExpContext)_localctx).b = mulExp();
				 ((AddExpContext)_localctx).exp =  arithmetic(_localctx.exp, ((AddExpContext)_localctx).op, ((AddExpContext)_localctx).b.exp); 
				}
				}
				setState(188);
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
	public static class MulExpContext extends ParserRuleContext {
		public Exp<?> exp;
		public UnaryExpContext a;
		public Token op;
		public UnaryExpContext b;
		public List<UnaryExpContext> unaryExp() {
			return getRuleContexts(UnaryExpContext.class);
		}
		public UnaryExpContext unaryExp(int i) {
			return getRuleContext(UnaryExpContext.class,i);
		}
		public List<TerminalNode> MUL() { return getTokens(ExpParser.MUL); }
		public TerminalNode MUL(int i) {
			return getToken(ExpParser.MUL, i);
		}
		public List<TerminalNode> DIV() { return getTokens(ExpParser.DIV); }
		public TerminalNode DIV(int i) {
			return getToken(ExpParser.DIV, i);
		}
		public List<TerminalNode> MOD() { return getTokens(ExpParser.MOD); }
		public TerminalNode MOD(int i) {
			return getToken(ExpParser.MOD, i);
		}
		public MulExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mulExp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterMulExp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitMulExp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitMulExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MulExpContext mulExp() throws RecognitionException {
		MulExpContext _localctx = new MulExpContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_mulExp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(189);
			((MulExpContext)_localctx).a = unaryExp();
			 ((MulExpContext)_localctx).exp =  ((MulExpContext)_localctx).a.exp; 
			setState(197);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 229376L) != 0)) {
				{
				{
				setState(191);
				((MulExpContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 229376L) != 0)) ) {
					((MulExpContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(192);
				((MulExpContext)_localctx).b = unaryExp();
				 ((MulExpContext)_localctx).exp =  arithmetic(_localctx.exp, ((MulExpContext)_localctx).op, ((MulExpContext)_localctx).b.exp); 
				}
				}
				setState(199);
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
	public static class UnaryExpContext extends ParserRuleContext {
		public Exp<?> exp;
		public Token op;
		public UnaryExpContext unaryExp;
		public PrimaryContext primary;
		public UnaryExpContext unaryExp() {
			return getRuleContext(UnaryExpContext.class,0);
		}
		public TerminalNode SUB() { return getToken(ExpParser.SUB, 0); }
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public UnaryExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryExp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterUnaryExp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitUnaryExp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitUnaryExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnaryExpContext unaryExp() throws RecognitionException {
		UnaryExpContext _localctx = new UnaryExpContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_unaryExp);
		try {
			setState(207);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SUB:
				enterOuterAlt(_localctx, 1);
				{
				setState(200);
				((UnaryExpContext)_localctx).op = match(SUB);
				setState(201);
				((UnaryExpContext)_localctx).unaryExp = unaryExp();
				 ((UnaryExpContext)_localctx).exp =  negate(((UnaryExpContext)_localctx).unaryExp.exp, ((UnaryExpContext)_localctx).op); 
				}
				break;
			case LP:
			case BOOL:
			case INT:
			case LONG:
			case BIGINT:
			case FLOAT:
			case DOUBLE:
			case DECIMAL:
			case STR:
			case COL:
			case DATE:
			case TIME:
			case DATETIME:
			case OFFSET_DATETIME:
			case ARRAY:
			case NULL:
			case TRUE:
			case FALSE:
			case ASC:
			case DESC:
			case PARAMETER:
			case INTEGER_LITERAL:
			case FLOAT_LITERAL:
			case STRING_LITERAL:
			case QUOTED_IDENTIFIER:
			case IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
				setState(204);
				((UnaryExpContext)_localctx).primary = primary();
				 ((UnaryExpContext)_localctx).exp =  ((UnaryExpContext)_localctx).primary.exp; 
				}
				break;
			default:
				throw new NoViableAltException(this);
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
	public static class PrimaryContext extends ParserRuleContext {
		public Exp<?> exp;
		public ExpressionContext expression;
		public FnCallContext fnCall;
		public ColumnContext column;
		public ArrayContext array;
		public ScalarContext scalar;
		public TerminalNode LP() { return getToken(ExpParser.LP, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RP() { return getToken(ExpParser.RP, 0); }
		public FnCallContext fnCall() {
			return getRuleContext(FnCallContext.class,0);
		}
		public ColumnContext column() {
			return getRuleContext(ColumnContext.class,0);
		}
		public ArrayContext array() {
			return getRuleContext(ArrayContext.class,0);
		}
		public ScalarContext scalar() {
			return getRuleContext(ScalarContext.class,0);
		}
		public TerminalNode NULL() { return getToken(ExpParser.NULL, 0); }
		public PrimaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterPrimary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitPrimary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitPrimary(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimaryContext primary() throws RecognitionException {
		PrimaryContext _localctx = new PrimaryContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_primary);
		try {
			setState(228);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(209);
				match(LP);
				setState(210);
				((PrimaryContext)_localctx).expression = expression();
				setState(211);
				match(RP);
				 ((PrimaryContext)_localctx).exp =  ((PrimaryContext)_localctx).expression.exp; 
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(214);
				((PrimaryContext)_localctx).fnCall = fnCall();
				 ((PrimaryContext)_localctx).exp =  ((PrimaryContext)_localctx).fnCall.exp; 
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(217);
				((PrimaryContext)_localctx).column = column();
				 ((PrimaryContext)_localctx).exp =  ((PrimaryContext)_localctx).column.exp; 
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(220);
				((PrimaryContext)_localctx).array = array();
				 ((PrimaryContext)_localctx).exp =  ((PrimaryContext)_localctx).array.exp; 
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(223);
				((PrimaryContext)_localctx).scalar = scalar();
				 ((PrimaryContext)_localctx).exp =  val(((PrimaryContext)_localctx).scalar.value); 
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(226);
				match(NULL);
				 ((PrimaryContext)_localctx).exp =  val(null); 
				}
				break;
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
	public static class FnCallContext extends ParserRuleContext {
		public Exp<?> exp;
		public Token IDENTIFIER;
		public ExpressionContext expression;
		public List<ExpressionContext> args = new ArrayList<ExpressionContext>();
		public TerminalNode IDENTIFIER() { return getToken(ExpParser.IDENTIFIER, 0); }
		public TerminalNode LP() { return getToken(ExpParser.LP, 0); }
		public TerminalNode RP() { return getToken(ExpParser.RP, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(ExpParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(ExpParser.COMMA, i);
		}
		public FnCallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fnCall; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterFnCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitFnCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitFnCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FnCallContext fnCall() throws RecognitionException {
		FnCallContext _localctx = new FnCallContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_fnCall);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(230);
			((FnCallContext)_localctx).IDENTIFIER = match(IDENTIFIER);
			setState(231);
			match(LP);
			setState(240);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 69818987331602L) != 0)) {
				{
				setState(232);
				((FnCallContext)_localctx).expression = expression();
				((FnCallContext)_localctx).args.add(((FnCallContext)_localctx).expression);
				setState(237);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(233);
					match(COMMA);
					setState(234);
					((FnCallContext)_localctx).expression = expression();
					((FnCallContext)_localctx).args.add(((FnCallContext)_localctx).expression);
					}
					}
					setState(239);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(242);
			match(RP);

			        ((FnCallContext)_localctx).exp =  fn(env, ((FnCallContext)_localctx).IDENTIFIER, ((FnCallContext)_localctx).args.stream().map(ctx -> ctx.exp).collect(Collectors.toList()));
			    
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
	public static class ScalarContext extends ParserRuleContext {
		public Object value;
		public BoolScalarContext boolScalar;
		public NumScalarContext numScalar;
		public StrScalarContext strScalar;
		public Token PARAMETER;
		public BoolScalarContext boolScalar() {
			return getRuleContext(BoolScalarContext.class,0);
		}
		public NumScalarContext numScalar() {
			return getRuleContext(NumScalarContext.class,0);
		}
		public StrScalarContext strScalar() {
			return getRuleContext(StrScalarContext.class,0);
		}
		public TerminalNode PARAMETER() { return getToken(ExpParser.PARAMETER, 0); }
		public ScalarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_scalar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterScalar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitScalar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitScalar(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ScalarContext scalar() throws RecognitionException {
		ScalarContext _localctx = new ScalarContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_scalar);
		try {
			setState(256);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TRUE:
			case FALSE:
				enterOuterAlt(_localctx, 1);
				{
				setState(245);
				((ScalarContext)_localctx).boolScalar = boolScalar();
				 ((ScalarContext)_localctx).value =  ((ScalarContext)_localctx).boolScalar.value; 
				}
				break;
			case INTEGER_LITERAL:
			case FLOAT_LITERAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(248);
				((ScalarContext)_localctx).numScalar = numScalar();
				 ((ScalarContext)_localctx).value =  ((ScalarContext)_localctx).numScalar.value; 
				}
				break;
			case STRING_LITERAL:
				enterOuterAlt(_localctx, 3);
				{
				setState(251);
				((ScalarContext)_localctx).strScalar = strScalar();
				 ((ScalarContext)_localctx).value =  ((ScalarContext)_localctx).strScalar.value; 
				}
				break;
			case PARAMETER:
				enterOuterAlt(_localctx, 4);
				{
				setState(254);
				((ScalarContext)_localctx).PARAMETER = match(PARAMETER);
				 ((ScalarContext)_localctx).value =  paramSource.next(((ScalarContext)_localctx).PARAMETER); 
				}
				break;
			default:
				throw new NoViableAltException(this);
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
	public static class AnyScalarListContext extends ParserRuleContext {
		public Object[] value;
		public ScalarContext scalar;
		public List<ScalarContext> values = new ArrayList<ScalarContext>();
		public Token PARAMETER;
		public TerminalNode LP() { return getToken(ExpParser.LP, 0); }
		public TerminalNode RP() { return getToken(ExpParser.RP, 0); }
		public List<ScalarContext> scalar() {
			return getRuleContexts(ScalarContext.class);
		}
		public ScalarContext scalar(int i) {
			return getRuleContext(ScalarContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(ExpParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(ExpParser.COMMA, i);
		}
		public TerminalNode PARAMETER() { return getToken(ExpParser.PARAMETER, 0); }
		public AnyScalarListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_anyScalarList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterAnyScalarList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitAnyScalarList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitAnyScalarList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AnyScalarListContext anyScalarList() throws RecognitionException {
		AnyScalarListContext _localctx = new AnyScalarListContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_anyScalarList);
		int _la;
		try {
			setState(272);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LP:
				enterOuterAlt(_localctx, 1);
				{
				setState(258);
				match(LP);
				setState(259);
				((AnyScalarListContext)_localctx).scalar = scalar();
				((AnyScalarListContext)_localctx).values.add(((AnyScalarListContext)_localctx).scalar);
				setState(264);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(260);
					match(COMMA);
					setState(261);
					((AnyScalarListContext)_localctx).scalar = scalar();
					((AnyScalarListContext)_localctx).values.add(((AnyScalarListContext)_localctx).scalar);
					}
					}
					setState(266);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(267);
				match(RP);
				 ((AnyScalarListContext)_localctx).value =  ((AnyScalarListContext)_localctx).values.stream().map(a -> a.value).toArray(); 
				}
				break;
			case PARAMETER:
				enterOuterAlt(_localctx, 2);
				{
				setState(270);
				((AnyScalarListContext)_localctx).PARAMETER = match(PARAMETER);
				 ((AnyScalarListContext)_localctx).value =  paramSource.nextArray(((AnyScalarListContext)_localctx).PARAMETER); 
				}
				break;
			default:
				throw new NoViableAltException(this);
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
	public static class BoolScalarContext extends ParserRuleContext {
		public Boolean value;
		public TerminalNode TRUE() { return getToken(ExpParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(ExpParser.FALSE, 0); }
		public BoolScalarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolScalar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterBoolScalar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitBoolScalar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitBoolScalar(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BoolScalarContext boolScalar() throws RecognitionException {
		BoolScalarContext _localctx = new BoolScalarContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_boolScalar);
		try {
			setState(278);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TRUE:
				enterOuterAlt(_localctx, 1);
				{
				setState(274);
				match(TRUE);
				 ((BoolScalarContext)_localctx).value =  true; 
				}
				break;
			case FALSE:
				enterOuterAlt(_localctx, 2);
				{
				setState(276);
				match(FALSE);
				 ((BoolScalarContext)_localctx).value =  false; 
				}
				break;
			default:
				throw new NoViableAltException(this);
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
	public static class NumScalarContext extends ParserRuleContext {
		public Number value;
		public IntegerScalarContext integerScalar;
		public FloatingPointScalarContext floatingPointScalar;
		public IntegerScalarContext integerScalar() {
			return getRuleContext(IntegerScalarContext.class,0);
		}
		public FloatingPointScalarContext floatingPointScalar() {
			return getRuleContext(FloatingPointScalarContext.class,0);
		}
		public NumScalarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numScalar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterNumScalar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitNumScalar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitNumScalar(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumScalarContext numScalar() throws RecognitionException {
		NumScalarContext _localctx = new NumScalarContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_numScalar);
		try {
			setState(286);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INTEGER_LITERAL:
				enterOuterAlt(_localctx, 1);
				{
				setState(280);
				((NumScalarContext)_localctx).integerScalar = integerScalar();
				 ((NumScalarContext)_localctx).value =  ((NumScalarContext)_localctx).integerScalar.value; 
				}
				break;
			case FLOAT_LITERAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(283);
				((NumScalarContext)_localctx).floatingPointScalar = floatingPointScalar();
				 ((NumScalarContext)_localctx).value =  ((NumScalarContext)_localctx).floatingPointScalar.value; 
				}
				break;
			default:
				throw new NoViableAltException(this);
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
	public static class IntegerScalarContext extends ParserRuleContext {
		public Number value;
		public TerminalNode INTEGER_LITERAL() { return getToken(ExpParser.INTEGER_LITERAL, 0); }
		public IntegerScalarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_integerScalar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterIntegerScalar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitIntegerScalar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitIntegerScalar(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IntegerScalarContext integerScalar() throws RecognitionException {
		IntegerScalarContext _localctx = new IntegerScalarContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_integerScalar);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(288);
			match(INTEGER_LITERAL);
			 ((IntegerScalarContext)_localctx).value =  parseIntegerValue(_input.getText(_localctx.start, _input.LT(-1))); 
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
	public static class FloatingPointScalarContext extends ParserRuleContext {
		public Number value;
		public TerminalNode FLOAT_LITERAL() { return getToken(ExpParser.FLOAT_LITERAL, 0); }
		public FloatingPointScalarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_floatingPointScalar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterFloatingPointScalar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitFloatingPointScalar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitFloatingPointScalar(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FloatingPointScalarContext floatingPointScalar() throws RecognitionException {
		FloatingPointScalarContext _localctx = new FloatingPointScalarContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_floatingPointScalar);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(291);
			match(FLOAT_LITERAL);
			 ((FloatingPointScalarContext)_localctx).value =  parseFloatingPointValue(_input.getText(_localctx.start, _input.LT(-1))); 
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
	public static class StrScalarContext extends ParserRuleContext {
		public String value;
		public TerminalNode STRING_LITERAL() { return getToken(ExpParser.STRING_LITERAL, 0); }
		public StrScalarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_strScalar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterStrScalar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitStrScalar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitStrScalar(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StrScalarContext strScalar() throws RecognitionException {
		StrScalarContext _localctx = new StrScalarContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_strScalar);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(294);
			match(STRING_LITERAL);
			 ((StrScalarContext)_localctx).value =  unescapeString(_input.getText(_localctx.start, _input.LT(-1)).substring(1, _input.getText(_localctx.start, _input.LT(-1)).length() - 1)); 
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
	public static class ColumnContext extends ParserRuleContext {
		public Exp<?> exp;
		public Token type;
		public ColumnIdContext columnId;
		public IdentifierContext identifier;
		public TerminalNode LP() { return getToken(ExpParser.LP, 0); }
		public ColumnIdContext columnId() {
			return getRuleContext(ColumnIdContext.class,0);
		}
		public TerminalNode RP() { return getToken(ExpParser.RP, 0); }
		public TerminalNode BOOL() { return getToken(ExpParser.BOOL, 0); }
		public TerminalNode INT() { return getToken(ExpParser.INT, 0); }
		public TerminalNode LONG() { return getToken(ExpParser.LONG, 0); }
		public TerminalNode BIGINT() { return getToken(ExpParser.BIGINT, 0); }
		public TerminalNode FLOAT() { return getToken(ExpParser.FLOAT, 0); }
		public TerminalNode DOUBLE() { return getToken(ExpParser.DOUBLE, 0); }
		public TerminalNode DECIMAL() { return getToken(ExpParser.DECIMAL, 0); }
		public TerminalNode STR() { return getToken(ExpParser.STR, 0); }
		public TerminalNode DATE() { return getToken(ExpParser.DATE, 0); }
		public TerminalNode TIME() { return getToken(ExpParser.TIME, 0); }
		public TerminalNode DATETIME() { return getToken(ExpParser.DATETIME, 0); }
		public TerminalNode OFFSET_DATETIME() { return getToken(ExpParser.OFFSET_DATETIME, 0); }
		public TerminalNode COL() { return getToken(ExpParser.COL, 0); }
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public ColumnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_column; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterColumn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitColumn(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitColumn(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ColumnContext column() throws RecognitionException {
		ColumnContext _localctx = new ColumnContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_column);
		int _la;
		try {
			setState(306);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(297);
				((ColumnContext)_localctx).type = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 8588886016L) != 0)) ) {
					((ColumnContext)_localctx).type = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(298);
				match(LP);
				setState(299);
				((ColumnContext)_localctx).columnId = columnId();
				setState(300);
				match(RP);
				 ((ColumnContext)_localctx).exp =  col(((ColumnContext)_localctx).type, ((ColumnContext)_localctx).columnId.id); 
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(303);
				((ColumnContext)_localctx).identifier = identifier();
				 ((ColumnContext)_localctx).exp =  col(((ColumnContext)_localctx).identifier.id); 
				}
				break;
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
	public static class ColumnIdContext extends ParserRuleContext {
		public Object id;
		public IntegerScalarContext integerScalar;
		public IdentifierContext identifier;
		public Token PARAMETER;
		public IntegerScalarContext integerScalar() {
			return getRuleContext(IntegerScalarContext.class,0);
		}
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TerminalNode PARAMETER() { return getToken(ExpParser.PARAMETER, 0); }
		public ColumnIdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_columnId; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterColumnId(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitColumnId(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitColumnId(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ColumnIdContext columnId() throws RecognitionException {
		ColumnIdContext _localctx = new ColumnIdContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_columnId);
		try {
			setState(316);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INTEGER_LITERAL:
				enterOuterAlt(_localctx, 1);
				{
				setState(308);
				((ColumnIdContext)_localctx).integerScalar = integerScalar();
				 ((ColumnIdContext)_localctx).id =  ((ColumnIdContext)_localctx).integerScalar.value; 
				}
				break;
			case BOOL:
			case INT:
			case LONG:
			case BIGINT:
			case FLOAT:
			case DOUBLE:
			case DECIMAL:
			case STR:
			case COL:
			case DATE:
			case TIME:
			case DATETIME:
			case OFFSET_DATETIME:
			case ARRAY:
			case ASC:
			case DESC:
			case QUOTED_IDENTIFIER:
			case IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
				setState(311);
				((ColumnIdContext)_localctx).identifier = identifier();
				 ((ColumnIdContext)_localctx).id =  ((ColumnIdContext)_localctx).identifier.id; 
				}
				break;
			case PARAMETER:
				enterOuterAlt(_localctx, 3);
				{
				setState(314);
				((ColumnIdContext)_localctx).PARAMETER = match(PARAMETER);
				 ((ColumnIdContext)_localctx).id =  paramSource.next(((ColumnIdContext)_localctx).PARAMETER); 
				}
				break;
			default:
				throw new NoViableAltException(this);
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
	public static class IdentifierContext extends ParserRuleContext {
		public String id;
		public KeywordAsIdentifierContext keywordAsIdentifier;
		public TerminalNode IDENTIFIER() { return getToken(ExpParser.IDENTIFIER, 0); }
		public TerminalNode QUOTED_IDENTIFIER() { return getToken(ExpParser.QUOTED_IDENTIFIER, 0); }
		public KeywordAsIdentifierContext keywordAsIdentifier() {
			return getRuleContext(KeywordAsIdentifierContext.class,0);
		}
		public IdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdentifierContext identifier() throws RecognitionException {
		IdentifierContext _localctx = new IdentifierContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_identifier);
		try {
			setState(325);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				enterOuterAlt(_localctx, 1);
				{
				setState(318);
				match(IDENTIFIER);
				 ((IdentifierContext)_localctx).id =  _input.getText(_localctx.start, _input.LT(-1)); 
				}
				break;
			case QUOTED_IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
				setState(320);
				match(QUOTED_IDENTIFIER);
				 ((IdentifierContext)_localctx).id =  unescapeIdentifier(_input.getText(_localctx.start, _input.LT(-1)).substring(1, _input.getText(_localctx.start, _input.LT(-1)).length() - 1)); 
				}
				break;
			case BOOL:
			case INT:
			case LONG:
			case BIGINT:
			case FLOAT:
			case DOUBLE:
			case DECIMAL:
			case STR:
			case COL:
			case DATE:
			case TIME:
			case DATETIME:
			case OFFSET_DATETIME:
			case ARRAY:
			case ASC:
			case DESC:
				enterOuterAlt(_localctx, 3);
				{
				setState(322);
				((IdentifierContext)_localctx).keywordAsIdentifier = keywordAsIdentifier();
				 ((IdentifierContext)_localctx).id =  ((IdentifierContext)_localctx).keywordAsIdentifier.id; 
				}
				break;
			default:
				throw new NoViableAltException(this);
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
	public static class ArrayContext extends ParserRuleContext {
		public Exp<?> exp;
		public ExpressionContext e;
		public StrScalarContext t;
		public TerminalNode ARRAY() { return getToken(ExpParser.ARRAY, 0); }
		public TerminalNode LP() { return getToken(ExpParser.LP, 0); }
		public TerminalNode COMMA() { return getToken(ExpParser.COMMA, 0); }
		public TerminalNode RP() { return getToken(ExpParser.RP, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public StrScalarContext strScalar() {
			return getRuleContext(StrScalarContext.class,0);
		}
		public ArrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_array; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterArray(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitArray(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitArray(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayContext array() throws RecognitionException {
		ArrayContext _localctx = new ArrayContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_array);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(327);
			match(ARRAY);
			setState(328);
			match(LP);
			setState(329);
			((ArrayContext)_localctx).e = expression();
			setState(330);
			match(COMMA);
			setState(331);
			((ArrayContext)_localctx).t = strScalar();
			setState(332);
			match(RP);
			 ((ArrayContext)_localctx).exp =  ExpParserUtils.array(((ArrayContext)_localctx).e.exp, ((ArrayContext)_localctx).t.value); 
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
	public static class KeywordAsIdentifierContext extends ParserRuleContext {
		public String id;
		public TerminalNode BOOL() { return getToken(ExpParser.BOOL, 0); }
		public TerminalNode INT() { return getToken(ExpParser.INT, 0); }
		public TerminalNode LONG() { return getToken(ExpParser.LONG, 0); }
		public TerminalNode BIGINT() { return getToken(ExpParser.BIGINT, 0); }
		public TerminalNode FLOAT() { return getToken(ExpParser.FLOAT, 0); }
		public TerminalNode DOUBLE() { return getToken(ExpParser.DOUBLE, 0); }
		public TerminalNode DECIMAL() { return getToken(ExpParser.DECIMAL, 0); }
		public TerminalNode STR() { return getToken(ExpParser.STR, 0); }
		public TerminalNode COL() { return getToken(ExpParser.COL, 0); }
		public TerminalNode DATE() { return getToken(ExpParser.DATE, 0); }
		public TerminalNode TIME() { return getToken(ExpParser.TIME, 0); }
		public TerminalNode DATETIME() { return getToken(ExpParser.DATETIME, 0); }
		public TerminalNode OFFSET_DATETIME() { return getToken(ExpParser.OFFSET_DATETIME, 0); }
		public TerminalNode ARRAY() { return getToken(ExpParser.ARRAY, 0); }
		public TerminalNode ASC() { return getToken(ExpParser.ASC, 0); }
		public TerminalNode DESC() { return getToken(ExpParser.DESC, 0); }
		public KeywordAsIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_keywordAsIdentifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).enterKeywordAsIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpListener ) ((ExpListener)listener).exitKeywordAsIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpVisitor ) return ((ExpVisitor<? extends T>)visitor).visitKeywordAsIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final KeywordAsIdentifierContext keywordAsIdentifier() throws RecognitionException {
		KeywordAsIdentifierContext _localctx = new KeywordAsIdentifierContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_keywordAsIdentifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(335);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 429495681024L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			 ((KeywordAsIdentifierContext)_localctx).id =  _input.getText(_localctx.start, _input.LT(-1)); 
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
		"\u0004\u0001.\u0153\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0001"+
		"D\b\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0005\u0002K\b\u0002\n\u0002\f\u0002N\t\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004[\b\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0005\u0005b\b\u0005"+
		"\n\u0005\f\u0005e\t\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0005\u0007s\b\u0007\n\u0007\f\u0007v\t\u0007"+
		"\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0005\b~\b\b\n\b\f\b"+
		"\u0081\t\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0003"+
		"\t\u008a\b\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0005\n\u0092"+
		"\b\n\n\n\f\n\u0095\t\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b"+
		"\u00a0\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u00ab\b\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u00b1\b\u000b"+
		"\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0005\f\u00b9\b\f\n\f"+
		"\f\f\u00bc\t\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0005\r"+
		"\u00c4\b\r\n\r\f\r\u00c7\t\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0003\u000e\u00d0\b\u000e\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0003\u000f\u00e5\b\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0005\u0010\u00ec\b\u0010\n\u0010\f\u0010\u00ef\t\u0010"+
		"\u0003\u0010\u00f1\b\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0003\u0011\u0101\b\u0011"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0005\u0012\u0107\b\u0012"+
		"\n\u0012\f\u0012\u010a\t\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0012\u0001\u0012\u0003\u0012\u0111\b\u0012\u0001\u0013\u0001\u0013\u0001"+
		"\u0013\u0001\u0013\u0003\u0013\u0117\b\u0013\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0003\u0014\u011f\b\u0014\u0001"+
		"\u0015\u0001\u0015\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0001"+
		"\u0017\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001"+
		"\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0003"+
		"\u0018\u0133\b\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001"+
		"\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0003\u0019\u013d\b\u0019\u0001"+
		"\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001"+
		"\u001a\u0003\u001a\u0146\b\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0001"+
		"\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001c\u0001"+
		"\u001c\u0001\u001c\u0001\u001c\u0000\u0000\u001d\u0000\u0002\u0004\u0006"+
		"\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,."+
		"02468\u0000\u0006\u0001\u0000\u0005\u0006\u0001\u0000\u0007\n\u0001\u0000"+
		"\r\u000e\u0001\u0000\u000f\u0011\u0001\u0000\u0014 \u0002\u0000\u0014"+
		"!%&\u0159\u0000:\u0001\u0000\u0000\u0000\u0002>\u0001\u0000\u0000\u0000"+
		"\u0004G\u0001\u0000\u0000\u0000\u0006R\u0001\u0000\u0000\u0000\bV\u0001"+
		"\u0000\u0000\u0000\n^\u0001\u0000\u0000\u0000\fi\u0001\u0000\u0000\u0000"+
		"\u000el\u0001\u0000\u0000\u0000\u0010w\u0001\u0000\u0000\u0000\u0012\u0089"+
		"\u0001\u0000\u0000\u0000\u0014\u008b\u0001\u0000\u0000\u0000\u0016\u0096"+
		"\u0001\u0000\u0000\u0000\u0018\u00b2\u0001\u0000\u0000\u0000\u001a\u00bd"+
		"\u0001\u0000\u0000\u0000\u001c\u00cf\u0001\u0000\u0000\u0000\u001e\u00e4"+
		"\u0001\u0000\u0000\u0000 \u00e6\u0001\u0000\u0000\u0000\"\u0100\u0001"+
		"\u0000\u0000\u0000$\u0110\u0001\u0000\u0000\u0000&\u0116\u0001\u0000\u0000"+
		"\u0000(\u011e\u0001\u0000\u0000\u0000*\u0120\u0001\u0000\u0000\u0000,"+
		"\u0123\u0001\u0000\u0000\u0000.\u0126\u0001\u0000\u0000\u00000\u0132\u0001"+
		"\u0000\u0000\u00002\u013c\u0001\u0000\u0000\u00004\u0145\u0001\u0000\u0000"+
		"\u00006\u0147\u0001\u0000\u0000\u00008\u014f\u0001\u0000\u0000\u0000:"+
		";\u0003\u0002\u0001\u0000;<\u0005\u0000\u0000\u0001<=\u0006\u0000\uffff"+
		"\uffff\u0000=\u0001\u0001\u0000\u0000\u0000>C\u0003\f\u0006\u0000?@\u0005"+
		"\'\u0000\u0000@A\u00034\u001a\u0000AB\u0006\u0001\uffff\uffff\u0000BD"+
		"\u0001\u0000\u0000\u0000C?\u0001\u0000\u0000\u0000CD\u0001\u0000\u0000"+
		"\u0000DE\u0001\u0000\u0000\u0000EF\u0006\u0001\uffff\uffff\u0000F\u0003"+
		"\u0001\u0000\u0000\u0000GL\u0003\u0002\u0001\u0000HI\u0005\u0003\u0000"+
		"\u0000IK\u0003\u0002\u0001\u0000JH\u0001\u0000\u0000\u0000KN\u0001\u0000"+
		"\u0000\u0000LJ\u0001\u0000\u0000\u0000LM\u0001\u0000\u0000\u0000MO\u0001"+
		"\u0000\u0000\u0000NL\u0001\u0000\u0000\u0000OP\u0005\u0000\u0000\u0001"+
		"PQ\u0006\u0002\uffff\uffff\u0000Q\u0005\u0001\u0000\u0000\u0000RS\u0003"+
		"\b\u0004\u0000ST\u0005\u0000\u0000\u0001TU\u0006\u0003\uffff\uffff\u0000"+
		"U\u0007\u0001\u0000\u0000\u0000VZ\u0003\f\u0006\u0000W[\u0005%\u0000\u0000"+
		"XY\u0005&\u0000\u0000Y[\u0006\u0004\uffff\uffff\u0000ZW\u0001\u0000\u0000"+
		"\u0000ZX\u0001\u0000\u0000\u0000Z[\u0001\u0000\u0000\u0000[\\\u0001\u0000"+
		"\u0000\u0000\\]\u0006\u0004\uffff\uffff\u0000]\t\u0001\u0000\u0000\u0000"+
		"^c\u0003\b\u0004\u0000_`\u0005\u0003\u0000\u0000`b\u0003\b\u0004\u0000"+
		"a_\u0001\u0000\u0000\u0000be\u0001\u0000\u0000\u0000ca\u0001\u0000\u0000"+
		"\u0000cd\u0001\u0000\u0000\u0000df\u0001\u0000\u0000\u0000ec\u0001\u0000"+
		"\u0000\u0000fg\u0005\u0000\u0000\u0001gh\u0006\u0005\uffff\uffff\u0000"+
		"h\u000b\u0001\u0000\u0000\u0000ij\u0003\u000e\u0007\u0000jk\u0006\u0006"+
		"\uffff\uffff\u0000k\r\u0001\u0000\u0000\u0000lm\u0003\u0010\b\u0000mt"+
		"\u0006\u0007\uffff\uffff\u0000no\u0005\u0013\u0000\u0000op\u0003\u0010"+
		"\b\u0000pq\u0006\u0007\uffff\uffff\u0000qs\u0001\u0000\u0000\u0000rn\u0001"+
		"\u0000\u0000\u0000sv\u0001\u0000\u0000\u0000tr\u0001\u0000\u0000\u0000"+
		"tu\u0001\u0000\u0000\u0000u\u000f\u0001\u0000\u0000\u0000vt\u0001\u0000"+
		"\u0000\u0000wx\u0003\u0012\t\u0000x\u007f\u0006\b\uffff\uffff\u0000yz"+
		"\u0005\u0012\u0000\u0000z{\u0003\u0012\t\u0000{|\u0006\b\uffff\uffff\u0000"+
		"|~\u0001\u0000\u0000\u0000}y\u0001\u0000\u0000\u0000~\u0081\u0001\u0000"+
		"\u0000\u0000\u007f}\u0001\u0000\u0000\u0000\u007f\u0080\u0001\u0000\u0000"+
		"\u0000\u0080\u0011\u0001\u0000\u0000\u0000\u0081\u007f\u0001\u0000\u0000"+
		"\u0000\u0082\u0083\u0005\u0004\u0000\u0000\u0083\u0084\u0003\u0012\t\u0000"+
		"\u0084\u0085\u0006\t\uffff\uffff\u0000\u0085\u008a\u0001\u0000\u0000\u0000"+
		"\u0086\u0087\u0003\u0014\n\u0000\u0087\u0088\u0006\t\uffff\uffff\u0000"+
		"\u0088\u008a\u0001\u0000\u0000\u0000\u0089\u0082\u0001\u0000\u0000\u0000"+
		"\u0089\u0086\u0001\u0000\u0000\u0000\u008a\u0013\u0001\u0000\u0000\u0000"+
		"\u008b\u008c\u0003\u0016\u000b\u0000\u008c\u0093\u0006\n\uffff\uffff\u0000"+
		"\u008d\u008e\u0007\u0000\u0000\u0000\u008e\u008f\u0003\u0016\u000b\u0000"+
		"\u008f\u0090\u0006\n\uffff\uffff\u0000\u0090\u0092\u0001\u0000\u0000\u0000"+
		"\u0091\u008d\u0001\u0000\u0000\u0000\u0092\u0095\u0001\u0000\u0000\u0000"+
		"\u0093\u0091\u0001\u0000\u0000\u0000\u0093\u0094\u0001\u0000\u0000\u0000"+
		"\u0094\u0015\u0001\u0000\u0000\u0000\u0095\u0093\u0001\u0000\u0000\u0000"+
		"\u0096\u0097\u0003\u0018\f\u0000\u0097\u00b0\u0006\u000b\uffff\uffff\u0000"+
		"\u0098\u0099\u0007\u0001\u0000\u0000\u0099\u009a\u0003\u0018\f\u0000\u009a"+
		"\u009b\u0006\u000b\uffff\uffff\u0000\u009b\u00b1\u0001\u0000\u0000\u0000"+
		"\u009c\u009f\u0006\u000b\uffff\uffff\u0000\u009d\u009e\u0005\u0004\u0000"+
		"\u0000\u009e\u00a0\u0006\u000b\uffff\uffff\u0000\u009f\u009d\u0001\u0000"+
		"\u0000\u0000\u009f\u00a0\u0001\u0000\u0000\u0000\u00a0\u00a1\u0001\u0000"+
		"\u0000\u0000\u00a1\u00a2\u0005\u000b\u0000\u0000\u00a2\u00a3\u0003\u0018"+
		"\f\u0000\u00a3\u00a4\u0005\u0012\u0000\u0000\u00a4\u00a5\u0003\u0018\f"+
		"\u0000\u00a5\u00a6\u0006\u000b\uffff\uffff\u0000\u00a6\u00b1\u0001\u0000"+
		"\u0000\u0000\u00a7\u00aa\u0006\u000b\uffff\uffff\u0000\u00a8\u00a9\u0005"+
		"\u0004\u0000\u0000\u00a9\u00ab\u0006\u000b\uffff\uffff\u0000\u00aa\u00a8"+
		"\u0001\u0000\u0000\u0000\u00aa\u00ab\u0001\u0000\u0000\u0000\u00ab\u00ac"+
		"\u0001\u0000\u0000\u0000\u00ac\u00ad\u0005\f\u0000\u0000\u00ad\u00ae\u0003"+
		"$\u0012\u0000\u00ae\u00af\u0006\u000b\uffff\uffff\u0000\u00af\u00b1\u0001"+
		"\u0000\u0000\u0000\u00b0\u0098\u0001\u0000\u0000\u0000\u00b0\u009c\u0001"+
		"\u0000\u0000\u0000\u00b0\u00a7\u0001\u0000\u0000\u0000\u00b0\u00b1\u0001"+
		"\u0000\u0000\u0000\u00b1\u0017\u0001\u0000\u0000\u0000\u00b2\u00b3\u0003"+
		"\u001a\r\u0000\u00b3\u00ba\u0006\f\uffff\uffff\u0000\u00b4\u00b5\u0007"+
		"\u0002\u0000\u0000\u00b5\u00b6\u0003\u001a\r\u0000\u00b6\u00b7\u0006\f"+
		"\uffff\uffff\u0000\u00b7\u00b9\u0001\u0000\u0000\u0000\u00b8\u00b4\u0001"+
		"\u0000\u0000\u0000\u00b9\u00bc\u0001\u0000\u0000\u0000\u00ba\u00b8\u0001"+
		"\u0000\u0000\u0000\u00ba\u00bb\u0001\u0000\u0000\u0000\u00bb\u0019\u0001"+
		"\u0000\u0000\u0000\u00bc\u00ba\u0001\u0000\u0000\u0000\u00bd\u00be\u0003"+
		"\u001c\u000e\u0000\u00be\u00c5\u0006\r\uffff\uffff\u0000\u00bf\u00c0\u0007"+
		"\u0003\u0000\u0000\u00c0\u00c1\u0003\u001c\u000e\u0000\u00c1\u00c2\u0006"+
		"\r\uffff\uffff\u0000\u00c2\u00c4\u0001\u0000\u0000\u0000\u00c3\u00bf\u0001"+
		"\u0000\u0000\u0000\u00c4\u00c7\u0001\u0000\u0000\u0000\u00c5\u00c3\u0001"+
		"\u0000\u0000\u0000\u00c5\u00c6\u0001\u0000\u0000\u0000\u00c6\u001b\u0001"+
		"\u0000\u0000\u0000\u00c7\u00c5\u0001\u0000\u0000\u0000\u00c8\u00c9\u0005"+
		"\u000e\u0000\u0000\u00c9\u00ca\u0003\u001c\u000e\u0000\u00ca\u00cb\u0006"+
		"\u000e\uffff\uffff\u0000\u00cb\u00d0\u0001\u0000\u0000\u0000\u00cc\u00cd"+
		"\u0003\u001e\u000f\u0000\u00cd\u00ce\u0006\u000e\uffff\uffff\u0000\u00ce"+
		"\u00d0\u0001\u0000\u0000\u0000\u00cf\u00c8\u0001\u0000\u0000\u0000\u00cf"+
		"\u00cc\u0001\u0000\u0000\u0000\u00d0\u001d\u0001\u0000\u0000\u0000\u00d1"+
		"\u00d2\u0005\u0001\u0000\u0000\u00d2\u00d3\u0003\f\u0006\u0000\u00d3\u00d4"+
		"\u0005\u0002\u0000\u0000\u00d4\u00d5\u0006\u000f\uffff\uffff\u0000\u00d5"+
		"\u00e5\u0001\u0000\u0000\u0000\u00d6\u00d7\u0003 \u0010\u0000\u00d7\u00d8"+
		"\u0006\u000f\uffff\uffff\u0000\u00d8\u00e5\u0001\u0000\u0000\u0000\u00d9"+
		"\u00da\u00030\u0018\u0000\u00da\u00db\u0006\u000f\uffff\uffff\u0000\u00db"+
		"\u00e5\u0001\u0000\u0000\u0000\u00dc\u00dd\u00036\u001b\u0000\u00dd\u00de"+
		"\u0006\u000f\uffff\uffff\u0000\u00de\u00e5\u0001\u0000\u0000\u0000\u00df"+
		"\u00e0\u0003\"\u0011\u0000\u00e0\u00e1\u0006\u000f\uffff\uffff\u0000\u00e1"+
		"\u00e5\u0001\u0000\u0000\u0000\u00e2\u00e3\u0005\"\u0000\u0000\u00e3\u00e5"+
		"\u0006\u000f\uffff\uffff\u0000\u00e4\u00d1\u0001\u0000\u0000\u0000\u00e4"+
		"\u00d6\u0001\u0000\u0000\u0000\u00e4\u00d9\u0001\u0000\u0000\u0000\u00e4"+
		"\u00dc\u0001\u0000\u0000\u0000\u00e4\u00df\u0001\u0000\u0000\u0000\u00e4"+
		"\u00e2\u0001\u0000\u0000\u0000\u00e5\u001f\u0001\u0000\u0000\u0000\u00e6"+
		"\u00e7\u0005-\u0000\u0000\u00e7\u00f0\u0005\u0001\u0000\u0000\u00e8\u00ed"+
		"\u0003\f\u0006\u0000\u00e9\u00ea\u0005\u0003\u0000\u0000\u00ea\u00ec\u0003"+
		"\f\u0006\u0000\u00eb\u00e9\u0001\u0000\u0000\u0000\u00ec\u00ef\u0001\u0000"+
		"\u0000\u0000\u00ed\u00eb\u0001\u0000\u0000\u0000\u00ed\u00ee\u0001\u0000"+
		"\u0000\u0000\u00ee\u00f1\u0001\u0000\u0000\u0000\u00ef\u00ed\u0001\u0000"+
		"\u0000\u0000\u00f0\u00e8\u0001\u0000\u0000\u0000\u00f0\u00f1\u0001\u0000"+
		"\u0000\u0000\u00f1\u00f2\u0001\u0000\u0000\u0000\u00f2\u00f3\u0005\u0002"+
		"\u0000\u0000\u00f3\u00f4\u0006\u0010\uffff\uffff\u0000\u00f4!\u0001\u0000"+
		"\u0000\u0000\u00f5\u00f6\u0003&\u0013\u0000\u00f6\u00f7\u0006\u0011\uffff"+
		"\uffff\u0000\u00f7\u0101\u0001\u0000\u0000\u0000\u00f8\u00f9\u0003(\u0014"+
		"\u0000\u00f9\u00fa\u0006\u0011\uffff\uffff\u0000\u00fa\u0101\u0001\u0000"+
		"\u0000\u0000\u00fb\u00fc\u0003.\u0017\u0000\u00fc\u00fd\u0006\u0011\uffff"+
		"\uffff\u0000\u00fd\u0101\u0001\u0000\u0000\u0000\u00fe\u00ff\u0005(\u0000"+
		"\u0000\u00ff\u0101\u0006\u0011\uffff\uffff\u0000\u0100\u00f5\u0001\u0000"+
		"\u0000\u0000\u0100\u00f8\u0001\u0000\u0000\u0000\u0100\u00fb\u0001\u0000"+
		"\u0000\u0000\u0100\u00fe\u0001\u0000\u0000\u0000\u0101#\u0001\u0000\u0000"+
		"\u0000\u0102\u0103\u0005\u0001\u0000\u0000\u0103\u0108\u0003\"\u0011\u0000"+
		"\u0104\u0105\u0005\u0003\u0000\u0000\u0105\u0107\u0003\"\u0011\u0000\u0106"+
		"\u0104\u0001\u0000\u0000\u0000\u0107\u010a\u0001\u0000\u0000\u0000\u0108"+
		"\u0106\u0001\u0000\u0000\u0000\u0108\u0109\u0001\u0000\u0000\u0000\u0109"+
		"\u010b\u0001\u0000\u0000\u0000\u010a\u0108\u0001\u0000\u0000\u0000\u010b"+
		"\u010c\u0005\u0002\u0000\u0000\u010c\u010d\u0006\u0012\uffff\uffff\u0000"+
		"\u010d\u0111\u0001\u0000\u0000\u0000\u010e\u010f\u0005(\u0000\u0000\u010f"+
		"\u0111\u0006\u0012\uffff\uffff\u0000\u0110\u0102\u0001\u0000\u0000\u0000"+
		"\u0110\u010e\u0001\u0000\u0000\u0000\u0111%\u0001\u0000\u0000\u0000\u0112"+
		"\u0113\u0005#\u0000\u0000\u0113\u0117\u0006\u0013\uffff\uffff\u0000\u0114"+
		"\u0115\u0005$\u0000\u0000\u0115\u0117\u0006\u0013\uffff\uffff\u0000\u0116"+
		"\u0112\u0001\u0000\u0000\u0000\u0116\u0114\u0001\u0000\u0000\u0000\u0117"+
		"\'\u0001\u0000\u0000\u0000\u0118\u0119\u0003*\u0015\u0000\u0119\u011a"+
		"\u0006\u0014\uffff\uffff\u0000\u011a\u011f\u0001\u0000\u0000\u0000\u011b"+
		"\u011c\u0003,\u0016\u0000\u011c\u011d\u0006\u0014\uffff\uffff\u0000\u011d"+
		"\u011f\u0001\u0000\u0000\u0000\u011e\u0118\u0001\u0000\u0000\u0000\u011e"+
		"\u011b\u0001\u0000\u0000\u0000\u011f)\u0001\u0000\u0000\u0000\u0120\u0121"+
		"\u0005)\u0000\u0000\u0121\u0122\u0006\u0015\uffff\uffff\u0000\u0122+\u0001"+
		"\u0000\u0000\u0000\u0123\u0124\u0005*\u0000\u0000\u0124\u0125\u0006\u0016"+
		"\uffff\uffff\u0000\u0125-\u0001\u0000\u0000\u0000\u0126\u0127\u0005+\u0000"+
		"\u0000\u0127\u0128\u0006\u0017\uffff\uffff\u0000\u0128/\u0001\u0000\u0000"+
		"\u0000\u0129\u012a\u0007\u0004\u0000\u0000\u012a\u012b\u0005\u0001\u0000"+
		"\u0000\u012b\u012c\u00032\u0019\u0000\u012c\u012d\u0005\u0002\u0000\u0000"+
		"\u012d\u012e\u0006\u0018\uffff\uffff\u0000\u012e\u0133\u0001\u0000\u0000"+
		"\u0000\u012f\u0130\u00034\u001a\u0000\u0130\u0131\u0006\u0018\uffff\uffff"+
		"\u0000\u0131\u0133\u0001\u0000\u0000\u0000\u0132\u0129\u0001\u0000\u0000"+
		"\u0000\u0132\u012f\u0001\u0000\u0000\u0000\u01331\u0001\u0000\u0000\u0000"+
		"\u0134\u0135\u0003*\u0015\u0000\u0135\u0136\u0006\u0019\uffff\uffff\u0000"+
		"\u0136\u013d\u0001\u0000\u0000\u0000\u0137\u0138\u00034\u001a\u0000\u0138"+
		"\u0139\u0006\u0019\uffff\uffff\u0000\u0139\u013d\u0001\u0000\u0000\u0000"+
		"\u013a\u013b\u0005(\u0000\u0000\u013b\u013d\u0006\u0019\uffff\uffff\u0000"+
		"\u013c\u0134\u0001\u0000\u0000\u0000\u013c\u0137\u0001\u0000\u0000\u0000"+
		"\u013c\u013a\u0001\u0000\u0000\u0000\u013d3\u0001\u0000\u0000\u0000\u013e"+
		"\u013f\u0005-\u0000\u0000\u013f\u0146\u0006\u001a\uffff\uffff\u0000\u0140"+
		"\u0141\u0005,\u0000\u0000\u0141\u0146\u0006\u001a\uffff\uffff\u0000\u0142"+
		"\u0143\u00038\u001c\u0000\u0143\u0144\u0006\u001a\uffff\uffff\u0000\u0144"+
		"\u0146\u0001\u0000\u0000\u0000\u0145\u013e\u0001\u0000\u0000\u0000\u0145"+
		"\u0140\u0001\u0000\u0000\u0000\u0145\u0142\u0001\u0000\u0000\u0000\u0146"+
		"5\u0001\u0000\u0000\u0000\u0147\u0148\u0005!\u0000\u0000\u0148\u0149\u0005"+
		"\u0001\u0000\u0000\u0149\u014a\u0003\f\u0006\u0000\u014a\u014b\u0005\u0003"+
		"\u0000\u0000\u014b\u014c\u0003.\u0017\u0000\u014c\u014d\u0005\u0002\u0000"+
		"\u0000\u014d\u014e\u0006\u001b\uffff\uffff\u0000\u014e7\u0001\u0000\u0000"+
		"\u0000\u014f\u0150\u0007\u0005\u0000\u0000\u0150\u0151\u0006\u001c\uffff"+
		"\uffff\u0000\u01519\u0001\u0000\u0000\u0000\u0019CLZct\u007f\u0089\u0093"+
		"\u009f\u00aa\u00b0\u00ba\u00c5\u00cf\u00e4\u00ed\u00f0\u0100\u0108\u0110"+
		"\u0116\u011e\u0132\u013c\u0145";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}