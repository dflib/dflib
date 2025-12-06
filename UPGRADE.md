# UPGRADE INSTRUCTIONS

## 2.0.0

* [dflib #421](https://github.com/dflib/dflib/issues/421): `RowSet.expand(..)` pair of methods became "non-terminal",
so it no longer returns a `DataFrame`, but rather a `RowSet`. As a result you may get a compilation error.
You will need to rewrite this code as `df.rows(..).expand(..).merge()`. While doing that, note that you will now have extra
capabilities. E.g. you can pass column transformation expressions to the `merge(..)` method, potentially simplifying
your code.

* [dflib #422](https://github.com/dflib/dflib/issues/422): `ColumnSet.expand(..)` / `ColumnSet.expandArray(..)` pair of 
methods became "non-terminal", so it no longer returns a `DataFrame`, but rather a `ColumnSet`. As a result, you may get
a compilation error. Since now the expanded columns are internally combined with the original DataFrame instead of being
the sole columns of the column set, the upgrade path depends on how the column set was defined. If you are using 
`df.cols()` (i.e. not explicitly specifying column set columns), it is as simple as `df.cols().expand(..).merge()`. 
Otherwise, you will need to explicitly list all desired result columns in `df.cols(..)` and use `.select(..)` instead 
of `.merge(..)`. The advantage of the new expansion API though is that both `.select(..)` and `.merge(..)` can take 
column transformation expressions, usually simplifying the overall code.

* [dflib #433](https://github.com/dflib/dflib/issues/433): Primitive value mappers (e.g. `IntValueMapper`) 
now consistently handle nulls and default object-to-primitive conversions. The old conversion methods were deprecated 
with proper notes in Javadocs. However, internally DFLib switched to the new methods for a number of operations. 
Specifically, the behavior of `$bool(..)` column expression, `ColumnSet.compactBool(..)`, and `JsonLoader.boolCol(..)` 
is now different in regard to numbers. Where previously it would evaluate a number to `false`, now it will evaluate
to `true` all numbers except `0`.

* [dflib #447](https://github.com/dflib/dflib/issues/447): _Non-aggregating_ expressions when applied to a `Window`
previously returned the last value of a partition or range. This was incorrect, and it was changed to return the value 
corresponding to the result row. In an unlikely event that your code calling `Window.select(..)` or `Window.merge(..)`,
relied on that incorrect value, you will need to revisit and tweak the expression arguments to those methods to match
your expectations.

* [dflib #478](https://github.com/dflib/dflib/issues/478): `Exp.ifNull(Exp<T> exp, T ifNull)` was renamed without 
deprecation to `ifNullVal(Exp<T> exp, T ifNull)` to avoid compilation conflict with the other `ifNull(..)` variant.

* [dflib #486](https://github.com/dflib/dflib/issues/486): `ColumnSet.compactInt(..)`, `ColumnSet.compactLong(..)` and
other primitive compaction methods became "non-terminal", so they no longer return a `DataFrame`, but rather a 
`ColumnSet`. As a result, you may get a compilation error. You should add a "merge" step to them, like: 
`ColumnSet.compactInt(..).merge()`

* [dflib #503](https://github.com/dflib/dflib/issues/503): If you are using DFLib in Jupyter with [JJava kernel](https://github.com/dflib/jjava/), DFLib starting from version 2.0.0-M3 would require JJava kernel version [1.0-a5](https://github.com/dflib/jjava/releases/tag/1.0-a5) or newer.

* [dflib #525](https://github.com/dflib/dflib/issues/525): `DataFrame.stack()` method is now non-terminal, so you
will need to change it to `DataFrame.stack().select()`.

* [dflib #539](https://github.com/dflib/dflib/issues/539): `sum(int(col))` expression now produces a `long` instead of
an `int`. This may change the type of numeric return values in group by and window expressions. 

* [dflib #546](https://github.com/dflib/dflib/issues/546): If you are using DFLib in Jupyter with JJava kernel, the
minimal supported kernel version is `1.0-a6`. You will need to upgrade the kernel if you are on an earlier version.

* [dflib #548](https://github.com/dflib/dflib/issues/548): ECharts JavaScript library was upgraded to version 6.0.0. 
For the list of EChart changes, you can follow 
[this page](https://echarts.apache.org/handbook/en/basics/release-note/v6-upgrade-guide/). The new ECharts is much more 
powerful but also introduces a new default chart theme. We recommend that you just use it, but if you have to switch 
back to the old v5 for any reason, you can try the new custom theme feature as shown below:
```java
ECharts
    .chart()
    .theme("v5", "https://cdn.jsdelivr.net/npm/echarts@6.0.0/theme/v5.js")
    // ...
    .plot(df);
```

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