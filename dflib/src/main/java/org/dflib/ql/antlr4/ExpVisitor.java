// Generated from org/dflib/ql/antlr4/Exp.g4 by ANTLR 4.13.2
package org.dflib.ql.antlr4;

import java.util.stream.Collectors;

import org.dflib.*;

import static org.dflib.ql.antlr4.ExpParserUtils.*;
import static org.dflib.ql.antlr4.Literals.*;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ExpParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ExpVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ExpParser#expRoot}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpRoot(ExpParser.ExpRootContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#expSingle}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpSingle(ExpParser.ExpSingleContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#expArray}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpArray(ExpParser.ExpArrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#sorterRoot}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSorterRoot(ExpParser.SorterRootContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#sorterSingle}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSorterSingle(ExpParser.SorterSingleContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#sorterArray}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSorterArray(ExpParser.SorterArrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(ExpParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#orExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrExp(ExpParser.OrExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#andExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAndExp(ExpParser.AndExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#notExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotExp(ExpParser.NotExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#eqExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqExp(ExpParser.EqExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#cmpExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCmpExp(ExpParser.CmpExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#addExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddExp(ExpParser.AddExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#mulExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulExp(ExpParser.MulExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#unaryExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExp(ExpParser.UnaryExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimary(ExpParser.PrimaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#fnCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFnCall(ExpParser.FnCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#scalar}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScalar(ExpParser.ScalarContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#anyScalarList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnyScalarList(ExpParser.AnyScalarListContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#boolScalar}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolScalar(ExpParser.BoolScalarContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#numScalar}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumScalar(ExpParser.NumScalarContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#integerScalar}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntegerScalar(ExpParser.IntegerScalarContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#floatingPointScalar}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloatingPointScalar(ExpParser.FloatingPointScalarContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#strScalar}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrScalar(ExpParser.StrScalarContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#column}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColumn(ExpParser.ColumnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#columnId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColumnId(ExpParser.ColumnIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(ExpParser.IdentifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#array}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray(ExpParser.ArrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#keywordAsIdentifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeywordAsIdentifier(ExpParser.KeywordAsIdentifierContext ctx);
}