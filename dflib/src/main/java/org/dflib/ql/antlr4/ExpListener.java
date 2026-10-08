// Generated from org/dflib/ql/antlr4/Exp.g4 by ANTLR 4.13.2
package org.dflib.ql.antlr4;

import java.util.stream.Collectors;

import org.dflib.*;

import static org.dflib.ql.antlr4.ExpParserUtils.*;
import static org.dflib.ql.antlr4.Literals.*;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ExpParser}.
 */
public interface ExpListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ExpParser#expRoot}.
	 * @param ctx the parse tree
	 */
	void enterExpRoot(ExpParser.ExpRootContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#expRoot}.
	 * @param ctx the parse tree
	 */
	void exitExpRoot(ExpParser.ExpRootContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#expSingle}.
	 * @param ctx the parse tree
	 */
	void enterExpSingle(ExpParser.ExpSingleContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#expSingle}.
	 * @param ctx the parse tree
	 */
	void exitExpSingle(ExpParser.ExpSingleContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#expArray}.
	 * @param ctx the parse tree
	 */
	void enterExpArray(ExpParser.ExpArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#expArray}.
	 * @param ctx the parse tree
	 */
	void exitExpArray(ExpParser.ExpArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#sorterRoot}.
	 * @param ctx the parse tree
	 */
	void enterSorterRoot(ExpParser.SorterRootContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#sorterRoot}.
	 * @param ctx the parse tree
	 */
	void exitSorterRoot(ExpParser.SorterRootContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#sorterSingle}.
	 * @param ctx the parse tree
	 */
	void enterSorterSingle(ExpParser.SorterSingleContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#sorterSingle}.
	 * @param ctx the parse tree
	 */
	void exitSorterSingle(ExpParser.SorterSingleContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#sorterArray}.
	 * @param ctx the parse tree
	 */
	void enterSorterArray(ExpParser.SorterArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#sorterArray}.
	 * @param ctx the parse tree
	 */
	void exitSorterArray(ExpParser.SorterArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(ExpParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(ExpParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#orExp}.
	 * @param ctx the parse tree
	 */
	void enterOrExp(ExpParser.OrExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#orExp}.
	 * @param ctx the parse tree
	 */
	void exitOrExp(ExpParser.OrExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#andExp}.
	 * @param ctx the parse tree
	 */
	void enterAndExp(ExpParser.AndExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#andExp}.
	 * @param ctx the parse tree
	 */
	void exitAndExp(ExpParser.AndExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#notExp}.
	 * @param ctx the parse tree
	 */
	void enterNotExp(ExpParser.NotExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#notExp}.
	 * @param ctx the parse tree
	 */
	void exitNotExp(ExpParser.NotExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#eqExp}.
	 * @param ctx the parse tree
	 */
	void enterEqExp(ExpParser.EqExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#eqExp}.
	 * @param ctx the parse tree
	 */
	void exitEqExp(ExpParser.EqExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#cmpExp}.
	 * @param ctx the parse tree
	 */
	void enterCmpExp(ExpParser.CmpExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#cmpExp}.
	 * @param ctx the parse tree
	 */
	void exitCmpExp(ExpParser.CmpExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#addExp}.
	 * @param ctx the parse tree
	 */
	void enterAddExp(ExpParser.AddExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#addExp}.
	 * @param ctx the parse tree
	 */
	void exitAddExp(ExpParser.AddExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#mulExp}.
	 * @param ctx the parse tree
	 */
	void enterMulExp(ExpParser.MulExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#mulExp}.
	 * @param ctx the parse tree
	 */
	void exitMulExp(ExpParser.MulExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#unaryExp}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExp(ExpParser.UnaryExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#unaryExp}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExp(ExpParser.UnaryExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary(ExpParser.PrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary(ExpParser.PrimaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#fnCall}.
	 * @param ctx the parse tree
	 */
	void enterFnCall(ExpParser.FnCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#fnCall}.
	 * @param ctx the parse tree
	 */
	void exitFnCall(ExpParser.FnCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#scalar}.
	 * @param ctx the parse tree
	 */
	void enterScalar(ExpParser.ScalarContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#scalar}.
	 * @param ctx the parse tree
	 */
	void exitScalar(ExpParser.ScalarContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#anyScalarList}.
	 * @param ctx the parse tree
	 */
	void enterAnyScalarList(ExpParser.AnyScalarListContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#anyScalarList}.
	 * @param ctx the parse tree
	 */
	void exitAnyScalarList(ExpParser.AnyScalarListContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#boolScalar}.
	 * @param ctx the parse tree
	 */
	void enterBoolScalar(ExpParser.BoolScalarContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#boolScalar}.
	 * @param ctx the parse tree
	 */
	void exitBoolScalar(ExpParser.BoolScalarContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#numScalar}.
	 * @param ctx the parse tree
	 */
	void enterNumScalar(ExpParser.NumScalarContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#numScalar}.
	 * @param ctx the parse tree
	 */
	void exitNumScalar(ExpParser.NumScalarContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#integerScalar}.
	 * @param ctx the parse tree
	 */
	void enterIntegerScalar(ExpParser.IntegerScalarContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#integerScalar}.
	 * @param ctx the parse tree
	 */
	void exitIntegerScalar(ExpParser.IntegerScalarContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#floatingPointScalar}.
	 * @param ctx the parse tree
	 */
	void enterFloatingPointScalar(ExpParser.FloatingPointScalarContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#floatingPointScalar}.
	 * @param ctx the parse tree
	 */
	void exitFloatingPointScalar(ExpParser.FloatingPointScalarContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#strScalar}.
	 * @param ctx the parse tree
	 */
	void enterStrScalar(ExpParser.StrScalarContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#strScalar}.
	 * @param ctx the parse tree
	 */
	void exitStrScalar(ExpParser.StrScalarContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#column}.
	 * @param ctx the parse tree
	 */
	void enterColumn(ExpParser.ColumnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#column}.
	 * @param ctx the parse tree
	 */
	void exitColumn(ExpParser.ColumnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#columnId}.
	 * @param ctx the parse tree
	 */
	void enterColumnId(ExpParser.ColumnIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#columnId}.
	 * @param ctx the parse tree
	 */
	void exitColumnId(ExpParser.ColumnIdContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#identifier}.
	 * @param ctx the parse tree
	 */
	void enterIdentifier(ExpParser.IdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#identifier}.
	 * @param ctx the parse tree
	 */
	void exitIdentifier(ExpParser.IdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#array}.
	 * @param ctx the parse tree
	 */
	void enterArray(ExpParser.ArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#array}.
	 * @param ctx the parse tree
	 */
	void exitArray(ExpParser.ArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#keywordAsIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterKeywordAsIdentifier(ExpParser.KeywordAsIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#keywordAsIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitKeywordAsIdentifier(ExpParser.KeywordAsIdentifierContext ctx);
}