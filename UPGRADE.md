# UPGRADE INSTRUCTIONS

## 2.0.0

* [dflib #433](https://github.com/dflib/dflib/issues/433): Primitive value mappers (e.g. `IntValueMapper`) 
now consistently handle nulls and default object-to-primitive conversions. The old conversion methods were deprecated 
with proper notes in Javadocs. However, internally DFLib switched to the new methods for a number of operations. 
Specifically, the behavior of `$bool(..)` column expression, `ColumnSet.compactBool(..)`, and `JsonLoader.boolCol(..)` 
is now different in regard to numbers. Where previously it would evaluate a number to `false`, now it will evaluate
to `true` all numbers except `0`.

## 1.1.0

* [dflib #362](https://github.com/dflib/dflib/issues/362): Due to the changes in the aggregated column name generation algorithm, default aggregated column names are no longer equal to aggregation source column names. E.g. `$int("a").first()` would previously be called `a`, and now is called `first(a)`. This may cause an exception like the following: `java.lang.IllegalArgumentException: Value 'my_column' is not present in the Index`. To address this, you will need to either explicitly name your columns when specifying a column set (e.g., `df.group("a").cols("a", ...)`) or use `as` on a column-generating  expression  (e.g., `$int("a").first().as("a")`)

## 1.0.0-RC1

* [dflib #331](https://github.com/dflib/dflib/issues/331): Recently added `CsvLoader.colType(...)` was
  replaced with `CsvLoader.col(...)` without deprecation. If you get a compilation error on this method, change it
  accordingly.
* 
* [dflib #341](https://github.com/dflib/dflib/issues/341): `JsonLoader.columnType(...)` was
replaced with `JsonLoader.col(...)`, `JsonLoader.intColumn(...)` - with `JsonLoader.intCol(...)` and so on, all 
without deprecation. If you get compilation errors on these methods, change it accordingly.

## 1.0.0-M23
* [dflib #318](https://github.com/dflib/dflib/issues/318): The default series type can no longer
be overridden and is always a "line chart" with default set of options. `Echarts.chart().defaultSeriesOpts(..)` 
method was removed as a result. If you are calling this method, you will get a compilation error. You will need to
remove it, and pass its `opts` argument to each affected `Echarts.chart().series(opts, "a", "b")` call instead.

* [dflib #321](https://github.com/dflib/dflib/issues/321): "jjava" Jupyter kernel had a significant backend,
change in version `1.0-M3`. `dflib-jupyter` had to be updated accordingly, making it incompatible with older version os 
"jjava". If you are working with DFLib in Jupyter, once you switch to DFLib `M23` or newer, you will need to upgrade
your kernel to `1.0-M3` (and vice versa). As a bonus, there are fewer notebook setup steps with the newer kernel and 
DFLib. Basic imports and kernel integrations are loaded automatically on startup using the new "jjava" extensions mechanism.

## 1.0.0-M22

* [dflib #296](https://github.com/dflib/dflib/issues/296): ECharts `Axis` class was split into two
subclasses - `XAxis` and `YAxis`, so some of the static factory methods on `Axis` are no longer possible (and the
rest are deprecated).  If yuo get a compilation error in your charts code, please convert those methods to the 
corresponding static factory methods coming from `XAxis` and `YAxis`.

## 1.0.0-M19

* [dflib #235](https://github.com/dflib/dflib/issues/235): Since the project got moved to dflib.org,
you will need to change your Maven/Gradle artifact group from `com.nhl.dflib` to `org.dflib` (e.g. 
`org.dflib:dflib-bom:1.0.0-M19`). And in the Java code, change the import packages from `com.nhl.dflib` to `org.dflib`.

## 0.16
* [dflib #181](https://github.com/dflib/dflib/issues/181): This task changes how to manually build 
  DataFrames. `DataFrame.newFrame(..)` is deprecated. You should use the new methods depending on the required
  assembly strategy: `DataFrame.empty(..)`, `DataFrame.byColumn(..)`, `DataFrame.byRow(..)`, 
  `DataFrame.byArrayRow(..)`, `DataFrame.foldByRow(..)`, `DataFrame.foldByColumn(..)`.
 
  `Accumulator` API to assemble Series was renamed. `Accumulator` became `ValueAccum`, `ObjectAccumulator` - 
  `ObjectAccum` and so on. Since this API is not normally used outside DFLib itself, we decided not to keep the old 
  classes around. So there will be compilation errors if your code relied on them. It should be easy to upgrade.

* [dflib #182](https://github.com/dflib/dflib/issues/182): This simplifies manual Series assembly methods,
  deprecating the old `Series.forData(..)`, `IntSeries.forInts(..)`, etc. Instead, there are a number of `Series.ofXyz`
  methods on the base Series interface. Make sure to upgrade your code before the old deprecated API goes away completely.

* [dflib #183](https://github.com/dflib/dflib/issues/183): There are some changes to `CsvLoader` filtering 
  API that break backwards compatibility:

  * The existing `selectRows(pos, ValuePredicate)` and `filterRows(pos, ValuePredicate)` were replaced with 
    `selectRows(RowPredicate)`.
  * Only the columns included in the resulting DataFrame can be referenced in the select condition. You can no longer 
  filter on CSV columns that are otherwise not present in the result.
  * Indices / names of the resulting DataFrame columns should be used in the condition and not the indices of the CSV.
  * Multiple conditions are not combined. The last specified condition wins.

## 0.14

* [dflib #160](https://github.com/dflib/dflib/issues/160): From this release DFLob requires Java 11
  as the minimal version.

* [dflib #161](https://github.com/dflib/dflib/issues/161): `dflib-test` module that provides JUnit 4 
  integration will no longer be shipped. `dflib-junit5` is the only option offered from now on. If you still need
  JUnit 4 and can not upgrade, you can take `dflib-test` classes from v.0.13 and maintain them in your own code. 
  There are only a few of them, and they are fairly simple.

## 0.11

* [dflib #127](https://github.com/dflib/dflib/issues/127): With DataFrame aggregation API migration to
a similar, but distinct `Exp` based API, there are a few breaking changes:

  * `DataFrame.agg(..)` method's return type is changed from `Series` to a single-row `DataFrame`. The previous return 
  type was confusing: it represented a row, not a column; it contained non-uniform data (potentially a mix of 
  different data types); it was losing labels information. Returning a small single-row DataFrame looks like a more 
  logical abstraction.

  * `DataFrame.agg(..)` and `GroupBy.agg(..)` methods now take `Exp...` instead of `Aggregator...`. `Agregator` 
  static methods will still work, as they now return `Exp...`, and are internally implemented using the new API. 
  But the class itself is deprecated, so you should look into replacing it with the new expressions.
    
  * Resulting column name defaults now include the name of the aggreation function. E.g. previously `Aggregator.sumInt("a")`
    would produce a column named "a". Now `$int("a").sum()` would produce a column named "sum(a)". To get back the old
    names, you would need to specify them explicitly. E.g. $int("a").sum().as("a")`

* [dflib #128](https://github.com/dflib/dflib/issues/128): Similar to #127 above, but for Series. There 
  are a few breaking changes:

  * `Series.agg(..)` method's return type is changed from a single value `R` to a single-value `Series<R>`.

  * `Series.aggMultiple(..)` method's return type is changed from `Series` to a single-row `DataFrame`. The previous 
    return type was confusing: it represented a row, not a column; it contained non-uniform data (potentially a mix of
    different data types); it was losing labels information. Returning a small single-row DataFrame looks like a more
    logical abstraction.


## 0.8

* [dflib #95](https://github.com/dflib/dflib/issues/95):

`SqlLoader` was made immutable and reusable. This resulted in API changes. If you were using `SqlLoader.params(..)`, 
remove this method call, and pass the same parameters Series (or array) to the `load` method instead.

## 0.6

* [dflib #54](https://github.com/dflib/dflib/issues/37): 

  * `Aggregator` is no longer an object that aggregates the entire row 
  (and can be composed from multiple ColumnAggregators). Instead it is 
  an aggregation spec for a single column in the aggregated result. 
  So the methods that took a single Aggregator as a parameter were 
  removed from `DataFrame` and `GroupBy`. Also `Aggregator` static factory
  methods now return `Aggregator` instances, not `ColumnAggregator`

  * `Aggregator.first()` used to return the first **non-null** element from 
  a column, which is counter-intuitive. It is changed to return the first 
  element whether it is null or not.