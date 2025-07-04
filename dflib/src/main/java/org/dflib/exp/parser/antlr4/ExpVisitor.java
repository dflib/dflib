// Generated from org/dflib/exp/parser/antlr4/Exp.g4 by ANTLR 4.13.2
package org.dflib.exp.parser.antlr4;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.BiFunction;

import org.dflib.*;

import static org.dflib.exp.parser.antlr4.ExpParserUtils.*;

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
	 * Visit a parse tree produced by {@link ExpParser#root}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRoot(ExpParser.RootContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#sorterRoot}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSorterRoot(ExpParser.SorterRootContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(ExpParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#numExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumExp(ExpParser.NumExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#boolExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolExp(ExpParser.BoolExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#strExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrExp(ExpParser.StrExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#temporalExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTemporalExp(ExpParser.TemporalExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#timeExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimeExp(ExpParser.TimeExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#dateExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateExp(ExpParser.DateExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#dateTimeExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateTimeExp(ExpParser.DateTimeExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#offsetDateTimeExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOffsetDateTimeExp(ExpParser.OffsetDateTimeExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#genericExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenericExp(ExpParser.GenericExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#anyScalar}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnyScalar(ExpParser.AnyScalarContext ctx);
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
	 * Visit a parse tree produced by {@link ExpParser#numScalarList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumScalarList(ExpParser.NumScalarListContext ctx);
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
	 * Visit a parse tree produced by {@link ExpParser#timeStrScalar}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimeStrScalar(ExpParser.TimeStrScalarContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#dateStrScalar}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateStrScalar(ExpParser.DateStrScalarContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#dateTimeStrScalar}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateTimeStrScalar(ExpParser.DateTimeStrScalarContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#offsetDateTimeStrScalar}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOffsetDateTimeStrScalar(ExpParser.OffsetDateTimeStrScalarContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#strScalar}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrScalar(ExpParser.StrScalarContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#strScalarList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrScalarList(ExpParser.StrScalarListContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#numColumn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumColumn(ExpParser.NumColumnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#intColumn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntColumn(ExpParser.IntColumnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#longColumn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLongColumn(ExpParser.LongColumnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#bigintColumn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBigintColumn(ExpParser.BigintColumnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#floatColumn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloatColumn(ExpParser.FloatColumnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#doubleColumn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDoubleColumn(ExpParser.DoubleColumnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#decimalColumn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecimalColumn(ExpParser.DecimalColumnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#boolColumn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolColumn(ExpParser.BoolColumnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#strColumn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrColumn(ExpParser.StrColumnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#dateColumn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateColumn(ExpParser.DateColumnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#timeColumn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimeColumn(ExpParser.TimeColumnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#dateTimeColumn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateTimeColumn(ExpParser.DateTimeColumnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#offsetDateTimeColumn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOffsetDateTimeColumn(ExpParser.OffsetDateTimeColumnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#genericColumn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenericColumn(ExpParser.GenericColumnContext ctx);
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
	 * Visit a parse tree produced by {@link ExpParser#relation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelation(ExpParser.RelationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#numRelation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumRelation(ExpParser.NumRelationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#strRelation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrRelation(ExpParser.StrRelationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#timeRelation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimeRelation(ExpParser.TimeRelationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#dateRelation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateRelation(ExpParser.DateRelationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#dateTimeRelation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateTimeRelation(ExpParser.DateTimeRelationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#offsetDateTimeRelation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOffsetDateTimeRelation(ExpParser.OffsetDateTimeRelationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#genericRelation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenericRelation(ExpParser.GenericRelationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#numFn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumFn(ExpParser.NumFnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#timeFieldFn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimeFieldFn(ExpParser.TimeFieldFnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#dateFieldFn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateFieldFn(ExpParser.DateFieldFnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#dateTimeFieldFn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateTimeFieldFn(ExpParser.DateTimeFieldFnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#offsetDateTimeFieldFn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOffsetDateTimeFieldFn(ExpParser.OffsetDateTimeFieldFnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#boolFn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolFn(ExpParser.BoolFnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#timeFn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimeFn(ExpParser.TimeFnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#dateFn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateFn(ExpParser.DateFnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#dateTimeFn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateTimeFn(ExpParser.DateTimeFnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#offsetDateTimeFn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOffsetDateTimeFn(ExpParser.OffsetDateTimeFnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#strFn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrFn(ExpParser.StrFnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#castAsBool}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCastAsBool(ExpParser.CastAsBoolContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#castAsInt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCastAsInt(ExpParser.CastAsIntContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#castAsLong}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCastAsLong(ExpParser.CastAsLongContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#castAsBigint}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCastAsBigint(ExpParser.CastAsBigintContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#castAsFloat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCastAsFloat(ExpParser.CastAsFloatContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#castAsDouble}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCastAsDouble(ExpParser.CastAsDoubleContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#castAsDecimal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCastAsDecimal(ExpParser.CastAsDecimalContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#castAsStr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCastAsStr(ExpParser.CastAsStrContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#castAsTime}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCastAsTime(ExpParser.CastAsTimeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#castAsDate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCastAsDate(ExpParser.CastAsDateContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#castAsDateTime}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCastAsDateTime(ExpParser.CastAsDateTimeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#castAsOffsetDateTime}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCastAsOffsetDateTime(ExpParser.CastAsOffsetDateTimeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#genericFn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenericFn(ExpParser.GenericFnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#ifExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfExp(ExpParser.IfExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#ifNull}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfNull(ExpParser.IfNullContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#nullableExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNullableExp(ExpParser.NullableExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#split}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSplit(ExpParser.SplitContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#shift}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitShift(ExpParser.ShiftContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#genericShiftExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenericShiftExp(ExpParser.GenericShiftExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#aggregateFn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggregateFn(ExpParser.AggregateFnContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#genericAgg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenericAgg(ExpParser.GenericAggContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#positionalAgg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPositionalAgg(ExpParser.PositionalAggContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#vConcat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVConcat(ExpParser.VConcatContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList(ExpParser.ListContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#set}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSet(ExpParser.SetContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#array}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray(ExpParser.ArrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#numAgg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumAgg(ExpParser.NumAggContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#timeAgg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimeAgg(ExpParser.TimeAggContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#dateAgg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateAgg(ExpParser.DateAggContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#dateTimeAgg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDateTimeAgg(ExpParser.DateTimeAggContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#strAgg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrAgg(ExpParser.StrAggContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpParser#fnName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFnName(ExpParser.FnNameContext ctx);
}