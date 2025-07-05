// Generated from org/dflib/ql/antlr4/Exp.g4 by ANTLR 4.13.2
package org.dflib.ql.antlr4;

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

import static org.dflib.ql.antlr4.ExpParserUtils.*;

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
	 * Enter a parse tree produced by {@link ExpParser#numExp}.
	 * @param ctx the parse tree
	 */
	void enterNumExp(ExpParser.NumExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#numExp}.
	 * @param ctx the parse tree
	 */
	void exitNumExp(ExpParser.NumExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#boolExp}.
	 * @param ctx the parse tree
	 */
	void enterBoolExp(ExpParser.BoolExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#boolExp}.
	 * @param ctx the parse tree
	 */
	void exitBoolExp(ExpParser.BoolExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#strExp}.
	 * @param ctx the parse tree
	 */
	void enterStrExp(ExpParser.StrExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#strExp}.
	 * @param ctx the parse tree
	 */
	void exitStrExp(ExpParser.StrExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#temporalExp}.
	 * @param ctx the parse tree
	 */
	void enterTemporalExp(ExpParser.TemporalExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#temporalExp}.
	 * @param ctx the parse tree
	 */
	void exitTemporalExp(ExpParser.TemporalExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#timeExp}.
	 * @param ctx the parse tree
	 */
	void enterTimeExp(ExpParser.TimeExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#timeExp}.
	 * @param ctx the parse tree
	 */
	void exitTimeExp(ExpParser.TimeExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#dateExp}.
	 * @param ctx the parse tree
	 */
	void enterDateExp(ExpParser.DateExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#dateExp}.
	 * @param ctx the parse tree
	 */
	void exitDateExp(ExpParser.DateExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#dateTimeExp}.
	 * @param ctx the parse tree
	 */
	void enterDateTimeExp(ExpParser.DateTimeExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#dateTimeExp}.
	 * @param ctx the parse tree
	 */
	void exitDateTimeExp(ExpParser.DateTimeExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#offsetDateTimeExp}.
	 * @param ctx the parse tree
	 */
	void enterOffsetDateTimeExp(ExpParser.OffsetDateTimeExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#offsetDateTimeExp}.
	 * @param ctx the parse tree
	 */
	void exitOffsetDateTimeExp(ExpParser.OffsetDateTimeExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#genericExp}.
	 * @param ctx the parse tree
	 */
	void enterGenericExp(ExpParser.GenericExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#genericExp}.
	 * @param ctx the parse tree
	 */
	void exitGenericExp(ExpParser.GenericExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#anyScalar}.
	 * @param ctx the parse tree
	 */
	void enterAnyScalar(ExpParser.AnyScalarContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#anyScalar}.
	 * @param ctx the parse tree
	 */
	void exitAnyScalar(ExpParser.AnyScalarContext ctx);
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
	 * Enter a parse tree produced by {@link ExpParser#numScalarList}.
	 * @param ctx the parse tree
	 */
	void enterNumScalarList(ExpParser.NumScalarListContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#numScalarList}.
	 * @param ctx the parse tree
	 */
	void exitNumScalarList(ExpParser.NumScalarListContext ctx);
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
	 * Enter a parse tree produced by {@link ExpParser#timeStrScalar}.
	 * @param ctx the parse tree
	 */
	void enterTimeStrScalar(ExpParser.TimeStrScalarContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#timeStrScalar}.
	 * @param ctx the parse tree
	 */
	void exitTimeStrScalar(ExpParser.TimeStrScalarContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#dateStrScalar}.
	 * @param ctx the parse tree
	 */
	void enterDateStrScalar(ExpParser.DateStrScalarContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#dateStrScalar}.
	 * @param ctx the parse tree
	 */
	void exitDateStrScalar(ExpParser.DateStrScalarContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#dateTimeStrScalar}.
	 * @param ctx the parse tree
	 */
	void enterDateTimeStrScalar(ExpParser.DateTimeStrScalarContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#dateTimeStrScalar}.
	 * @param ctx the parse tree
	 */
	void exitDateTimeStrScalar(ExpParser.DateTimeStrScalarContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#offsetDateTimeStrScalar}.
	 * @param ctx the parse tree
	 */
	void enterOffsetDateTimeStrScalar(ExpParser.OffsetDateTimeStrScalarContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#offsetDateTimeStrScalar}.
	 * @param ctx the parse tree
	 */
	void exitOffsetDateTimeStrScalar(ExpParser.OffsetDateTimeStrScalarContext ctx);
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
	 * Enter a parse tree produced by {@link ExpParser#strScalarList}.
	 * @param ctx the parse tree
	 */
	void enterStrScalarList(ExpParser.StrScalarListContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#strScalarList}.
	 * @param ctx the parse tree
	 */
	void exitStrScalarList(ExpParser.StrScalarListContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#numColumn}.
	 * @param ctx the parse tree
	 */
	void enterNumColumn(ExpParser.NumColumnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#numColumn}.
	 * @param ctx the parse tree
	 */
	void exitNumColumn(ExpParser.NumColumnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#intColumn}.
	 * @param ctx the parse tree
	 */
	void enterIntColumn(ExpParser.IntColumnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#intColumn}.
	 * @param ctx the parse tree
	 */
	void exitIntColumn(ExpParser.IntColumnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#longColumn}.
	 * @param ctx the parse tree
	 */
	void enterLongColumn(ExpParser.LongColumnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#longColumn}.
	 * @param ctx the parse tree
	 */
	void exitLongColumn(ExpParser.LongColumnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#bigintColumn}.
	 * @param ctx the parse tree
	 */
	void enterBigintColumn(ExpParser.BigintColumnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#bigintColumn}.
	 * @param ctx the parse tree
	 */
	void exitBigintColumn(ExpParser.BigintColumnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#floatColumn}.
	 * @param ctx the parse tree
	 */
	void enterFloatColumn(ExpParser.FloatColumnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#floatColumn}.
	 * @param ctx the parse tree
	 */
	void exitFloatColumn(ExpParser.FloatColumnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#doubleColumn}.
	 * @param ctx the parse tree
	 */
	void enterDoubleColumn(ExpParser.DoubleColumnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#doubleColumn}.
	 * @param ctx the parse tree
	 */
	void exitDoubleColumn(ExpParser.DoubleColumnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#decimalColumn}.
	 * @param ctx the parse tree
	 */
	void enterDecimalColumn(ExpParser.DecimalColumnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#decimalColumn}.
	 * @param ctx the parse tree
	 */
	void exitDecimalColumn(ExpParser.DecimalColumnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#boolColumn}.
	 * @param ctx the parse tree
	 */
	void enterBoolColumn(ExpParser.BoolColumnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#boolColumn}.
	 * @param ctx the parse tree
	 */
	void exitBoolColumn(ExpParser.BoolColumnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#strColumn}.
	 * @param ctx the parse tree
	 */
	void enterStrColumn(ExpParser.StrColumnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#strColumn}.
	 * @param ctx the parse tree
	 */
	void exitStrColumn(ExpParser.StrColumnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#dateColumn}.
	 * @param ctx the parse tree
	 */
	void enterDateColumn(ExpParser.DateColumnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#dateColumn}.
	 * @param ctx the parse tree
	 */
	void exitDateColumn(ExpParser.DateColumnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#timeColumn}.
	 * @param ctx the parse tree
	 */
	void enterTimeColumn(ExpParser.TimeColumnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#timeColumn}.
	 * @param ctx the parse tree
	 */
	void exitTimeColumn(ExpParser.TimeColumnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#dateTimeColumn}.
	 * @param ctx the parse tree
	 */
	void enterDateTimeColumn(ExpParser.DateTimeColumnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#dateTimeColumn}.
	 * @param ctx the parse tree
	 */
	void exitDateTimeColumn(ExpParser.DateTimeColumnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#offsetDateTimeColumn}.
	 * @param ctx the parse tree
	 */
	void enterOffsetDateTimeColumn(ExpParser.OffsetDateTimeColumnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#offsetDateTimeColumn}.
	 * @param ctx the parse tree
	 */
	void exitOffsetDateTimeColumn(ExpParser.OffsetDateTimeColumnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#genericColumn}.
	 * @param ctx the parse tree
	 */
	void enterGenericColumn(ExpParser.GenericColumnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#genericColumn}.
	 * @param ctx the parse tree
	 */
	void exitGenericColumn(ExpParser.GenericColumnContext ctx);
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
	 * Enter a parse tree produced by {@link ExpParser#relation}.
	 * @param ctx the parse tree
	 */
	void enterRelation(ExpParser.RelationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#relation}.
	 * @param ctx the parse tree
	 */
	void exitRelation(ExpParser.RelationContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#numRelation}.
	 * @param ctx the parse tree
	 */
	void enterNumRelation(ExpParser.NumRelationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#numRelation}.
	 * @param ctx the parse tree
	 */
	void exitNumRelation(ExpParser.NumRelationContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#strRelation}.
	 * @param ctx the parse tree
	 */
	void enterStrRelation(ExpParser.StrRelationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#strRelation}.
	 * @param ctx the parse tree
	 */
	void exitStrRelation(ExpParser.StrRelationContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#timeRelation}.
	 * @param ctx the parse tree
	 */
	void enterTimeRelation(ExpParser.TimeRelationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#timeRelation}.
	 * @param ctx the parse tree
	 */
	void exitTimeRelation(ExpParser.TimeRelationContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#dateRelation}.
	 * @param ctx the parse tree
	 */
	void enterDateRelation(ExpParser.DateRelationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#dateRelation}.
	 * @param ctx the parse tree
	 */
	void exitDateRelation(ExpParser.DateRelationContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#dateTimeRelation}.
	 * @param ctx the parse tree
	 */
	void enterDateTimeRelation(ExpParser.DateTimeRelationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#dateTimeRelation}.
	 * @param ctx the parse tree
	 */
	void exitDateTimeRelation(ExpParser.DateTimeRelationContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#offsetDateTimeRelation}.
	 * @param ctx the parse tree
	 */
	void enterOffsetDateTimeRelation(ExpParser.OffsetDateTimeRelationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#offsetDateTimeRelation}.
	 * @param ctx the parse tree
	 */
	void exitOffsetDateTimeRelation(ExpParser.OffsetDateTimeRelationContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#genericRelation}.
	 * @param ctx the parse tree
	 */
	void enterGenericRelation(ExpParser.GenericRelationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#genericRelation}.
	 * @param ctx the parse tree
	 */
	void exitGenericRelation(ExpParser.GenericRelationContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#numFn}.
	 * @param ctx the parse tree
	 */
	void enterNumFn(ExpParser.NumFnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#numFn}.
	 * @param ctx the parse tree
	 */
	void exitNumFn(ExpParser.NumFnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#timeFieldFn}.
	 * @param ctx the parse tree
	 */
	void enterTimeFieldFn(ExpParser.TimeFieldFnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#timeFieldFn}.
	 * @param ctx the parse tree
	 */
	void exitTimeFieldFn(ExpParser.TimeFieldFnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#dateFieldFn}.
	 * @param ctx the parse tree
	 */
	void enterDateFieldFn(ExpParser.DateFieldFnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#dateFieldFn}.
	 * @param ctx the parse tree
	 */
	void exitDateFieldFn(ExpParser.DateFieldFnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#dateTimeFieldFn}.
	 * @param ctx the parse tree
	 */
	void enterDateTimeFieldFn(ExpParser.DateTimeFieldFnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#dateTimeFieldFn}.
	 * @param ctx the parse tree
	 */
	void exitDateTimeFieldFn(ExpParser.DateTimeFieldFnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#offsetDateTimeFieldFn}.
	 * @param ctx the parse tree
	 */
	void enterOffsetDateTimeFieldFn(ExpParser.OffsetDateTimeFieldFnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#offsetDateTimeFieldFn}.
	 * @param ctx the parse tree
	 */
	void exitOffsetDateTimeFieldFn(ExpParser.OffsetDateTimeFieldFnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#boolFn}.
	 * @param ctx the parse tree
	 */
	void enterBoolFn(ExpParser.BoolFnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#boolFn}.
	 * @param ctx the parse tree
	 */
	void exitBoolFn(ExpParser.BoolFnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#timeFn}.
	 * @param ctx the parse tree
	 */
	void enterTimeFn(ExpParser.TimeFnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#timeFn}.
	 * @param ctx the parse tree
	 */
	void exitTimeFn(ExpParser.TimeFnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#dateFn}.
	 * @param ctx the parse tree
	 */
	void enterDateFn(ExpParser.DateFnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#dateFn}.
	 * @param ctx the parse tree
	 */
	void exitDateFn(ExpParser.DateFnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#dateTimeFn}.
	 * @param ctx the parse tree
	 */
	void enterDateTimeFn(ExpParser.DateTimeFnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#dateTimeFn}.
	 * @param ctx the parse tree
	 */
	void exitDateTimeFn(ExpParser.DateTimeFnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#offsetDateTimeFn}.
	 * @param ctx the parse tree
	 */
	void enterOffsetDateTimeFn(ExpParser.OffsetDateTimeFnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#offsetDateTimeFn}.
	 * @param ctx the parse tree
	 */
	void exitOffsetDateTimeFn(ExpParser.OffsetDateTimeFnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#strFn}.
	 * @param ctx the parse tree
	 */
	void enterStrFn(ExpParser.StrFnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#strFn}.
	 * @param ctx the parse tree
	 */
	void exitStrFn(ExpParser.StrFnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#castAsBool}.
	 * @param ctx the parse tree
	 */
	void enterCastAsBool(ExpParser.CastAsBoolContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#castAsBool}.
	 * @param ctx the parse tree
	 */
	void exitCastAsBool(ExpParser.CastAsBoolContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#castAsInt}.
	 * @param ctx the parse tree
	 */
	void enterCastAsInt(ExpParser.CastAsIntContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#castAsInt}.
	 * @param ctx the parse tree
	 */
	void exitCastAsInt(ExpParser.CastAsIntContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#castAsLong}.
	 * @param ctx the parse tree
	 */
	void enterCastAsLong(ExpParser.CastAsLongContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#castAsLong}.
	 * @param ctx the parse tree
	 */
	void exitCastAsLong(ExpParser.CastAsLongContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#castAsBigint}.
	 * @param ctx the parse tree
	 */
	void enterCastAsBigint(ExpParser.CastAsBigintContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#castAsBigint}.
	 * @param ctx the parse tree
	 */
	void exitCastAsBigint(ExpParser.CastAsBigintContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#castAsFloat}.
	 * @param ctx the parse tree
	 */
	void enterCastAsFloat(ExpParser.CastAsFloatContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#castAsFloat}.
	 * @param ctx the parse tree
	 */
	void exitCastAsFloat(ExpParser.CastAsFloatContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#castAsDouble}.
	 * @param ctx the parse tree
	 */
	void enterCastAsDouble(ExpParser.CastAsDoubleContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#castAsDouble}.
	 * @param ctx the parse tree
	 */
	void exitCastAsDouble(ExpParser.CastAsDoubleContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#castAsDecimal}.
	 * @param ctx the parse tree
	 */
	void enterCastAsDecimal(ExpParser.CastAsDecimalContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#castAsDecimal}.
	 * @param ctx the parse tree
	 */
	void exitCastAsDecimal(ExpParser.CastAsDecimalContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#castAsStr}.
	 * @param ctx the parse tree
	 */
	void enterCastAsStr(ExpParser.CastAsStrContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#castAsStr}.
	 * @param ctx the parse tree
	 */
	void exitCastAsStr(ExpParser.CastAsStrContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#castAsTime}.
	 * @param ctx the parse tree
	 */
	void enterCastAsTime(ExpParser.CastAsTimeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#castAsTime}.
	 * @param ctx the parse tree
	 */
	void exitCastAsTime(ExpParser.CastAsTimeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#castAsDate}.
	 * @param ctx the parse tree
	 */
	void enterCastAsDate(ExpParser.CastAsDateContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#castAsDate}.
	 * @param ctx the parse tree
	 */
	void exitCastAsDate(ExpParser.CastAsDateContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#castAsDateTime}.
	 * @param ctx the parse tree
	 */
	void enterCastAsDateTime(ExpParser.CastAsDateTimeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#castAsDateTime}.
	 * @param ctx the parse tree
	 */
	void exitCastAsDateTime(ExpParser.CastAsDateTimeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#castAsOffsetDateTime}.
	 * @param ctx the parse tree
	 */
	void enterCastAsOffsetDateTime(ExpParser.CastAsOffsetDateTimeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#castAsOffsetDateTime}.
	 * @param ctx the parse tree
	 */
	void exitCastAsOffsetDateTime(ExpParser.CastAsOffsetDateTimeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#genericFn}.
	 * @param ctx the parse tree
	 */
	void enterGenericFn(ExpParser.GenericFnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#genericFn}.
	 * @param ctx the parse tree
	 */
	void exitGenericFn(ExpParser.GenericFnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#ifExp}.
	 * @param ctx the parse tree
	 */
	void enterIfExp(ExpParser.IfExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#ifExp}.
	 * @param ctx the parse tree
	 */
	void exitIfExp(ExpParser.IfExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#ifNull}.
	 * @param ctx the parse tree
	 */
	void enterIfNull(ExpParser.IfNullContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#ifNull}.
	 * @param ctx the parse tree
	 */
	void exitIfNull(ExpParser.IfNullContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#nullableExp}.
	 * @param ctx the parse tree
	 */
	void enterNullableExp(ExpParser.NullableExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#nullableExp}.
	 * @param ctx the parse tree
	 */
	void exitNullableExp(ExpParser.NullableExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#split}.
	 * @param ctx the parse tree
	 */
	void enterSplit(ExpParser.SplitContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#split}.
	 * @param ctx the parse tree
	 */
	void exitSplit(ExpParser.SplitContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#shift}.
	 * @param ctx the parse tree
	 */
	void enterShift(ExpParser.ShiftContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#shift}.
	 * @param ctx the parse tree
	 */
	void exitShift(ExpParser.ShiftContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#genericShiftExp}.
	 * @param ctx the parse tree
	 */
	void enterGenericShiftExp(ExpParser.GenericShiftExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#genericShiftExp}.
	 * @param ctx the parse tree
	 */
	void exitGenericShiftExp(ExpParser.GenericShiftExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#aggregateFn}.
	 * @param ctx the parse tree
	 */
	void enterAggregateFn(ExpParser.AggregateFnContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#aggregateFn}.
	 * @param ctx the parse tree
	 */
	void exitAggregateFn(ExpParser.AggregateFnContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#genericAgg}.
	 * @param ctx the parse tree
	 */
	void enterGenericAgg(ExpParser.GenericAggContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#genericAgg}.
	 * @param ctx the parse tree
	 */
	void exitGenericAgg(ExpParser.GenericAggContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#positionalAgg}.
	 * @param ctx the parse tree
	 */
	void enterPositionalAgg(ExpParser.PositionalAggContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#positionalAgg}.
	 * @param ctx the parse tree
	 */
	void exitPositionalAgg(ExpParser.PositionalAggContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#vConcat}.
	 * @param ctx the parse tree
	 */
	void enterVConcat(ExpParser.VConcatContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#vConcat}.
	 * @param ctx the parse tree
	 */
	void exitVConcat(ExpParser.VConcatContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#list}.
	 * @param ctx the parse tree
	 */
	void enterList(ExpParser.ListContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#list}.
	 * @param ctx the parse tree
	 */
	void exitList(ExpParser.ListContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#set}.
	 * @param ctx the parse tree
	 */
	void enterSet(ExpParser.SetContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#set}.
	 * @param ctx the parse tree
	 */
	void exitSet(ExpParser.SetContext ctx);
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
	 * Enter a parse tree produced by {@link ExpParser#numAgg}.
	 * @param ctx the parse tree
	 */
	void enterNumAgg(ExpParser.NumAggContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#numAgg}.
	 * @param ctx the parse tree
	 */
	void exitNumAgg(ExpParser.NumAggContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#timeAgg}.
	 * @param ctx the parse tree
	 */
	void enterTimeAgg(ExpParser.TimeAggContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#timeAgg}.
	 * @param ctx the parse tree
	 */
	void exitTimeAgg(ExpParser.TimeAggContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#dateAgg}.
	 * @param ctx the parse tree
	 */
	void enterDateAgg(ExpParser.DateAggContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#dateAgg}.
	 * @param ctx the parse tree
	 */
	void exitDateAgg(ExpParser.DateAggContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#dateTimeAgg}.
	 * @param ctx the parse tree
	 */
	void enterDateTimeAgg(ExpParser.DateTimeAggContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#dateTimeAgg}.
	 * @param ctx the parse tree
	 */
	void exitDateTimeAgg(ExpParser.DateTimeAggContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#strAgg}.
	 * @param ctx the parse tree
	 */
	void enterStrAgg(ExpParser.StrAggContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#strAgg}.
	 * @param ctx the parse tree
	 */
	void exitStrAgg(ExpParser.StrAggContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpParser#fnName}.
	 * @param ctx the parse tree
	 */
	void enterFnName(ExpParser.FnNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpParser#fnName}.
	 * @param ctx the parse tree
	 */
	void exitFnName(ExpParser.FnNameContext ctx);
}