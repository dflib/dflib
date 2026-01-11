# UPGRADE INSTRUCTIONS

_Upgrade instructions to earlier 1.x versions are available [here](UPGRADE-1x.md)_

## 2.0.0

### [dflib #421](https://github.com/dflib/dflib/issues/421)
`RowSet.expand(..)` pair of methods became "non-terminal",
so it no longer returns a `DataFrame`, but rather a `RowSet`. As a result you may get a compilation error.
You will need to rewrite this code as `df.rows(..).expand(..).merge()`. While doing that, note that you will now have extra
capabilities. E.g. you can pass column transformation expressions to the `merge(..)` method, potentially simplifying
your code.

### [dflib #422](https://github.com/dflib/dflib/issues/422): 
`ColumnSet.expand(..)` / `ColumnSet.expandArray(..)` pair of 
methods became "non-terminal", so it no longer returns a `DataFrame`, but rather a `ColumnSet`. As a result, you may get
a compilation error. Since now the expanded columns are internally combined with the original DataFrame instead of being
the sole columns of the column set, the upgrade path depends on how the column set was defined. If you are using 
`df.cols()` (i.e. not explicitly specifying column set columns), it is as simple as `df.cols().expand(..).merge()`. 
Otherwise, you will need to explicitly list all desired result columns in `df.cols(..)` and use `.select(..)` instead 
of `.merge(..)`. The advantage of the new expansion API though is that both `.select(..)` and `.merge(..)` can take 
column transformation expressions, usually simplifying the overall code.

### [dflib #433](https://github.com/dflib/dflib/issues/433)
Primitive value mappers (e.g. `IntValueMapper`) 
now consistently handle nulls and default object-to-primitive conversions. The old conversion methods were deprecated 
with proper notes in Javadocs. However, internally DFLib switched to the new methods for a number of operations. 
Specifically, the behavior of `$bool(..)` column expression, `ColumnSet.compactBool(..)`, and `JsonLoader.boolCol(..)` 
is now different in regard to numbers. Where previously it would evaluate a number to `false`, now it will evaluate
to `true` all numbers except `0`.

### [dflib #447](https://github.com/dflib/dflib/issues/447)
_Non-aggregating_ expressions when applied to a `Window`
previously returned the last value of a partition or range. This was incorrect, and it was changed to return the value 
corresponding to the result row. In an unlikely event that your code calling `Window.select(..)` or `Window.merge(..)`,
relied on that incorrect value, you will need to revisit and tweak the expression arguments to those methods to match
your expectations.

### [dflib #478](https://github.com/dflib/dflib/issues/478)
`Exp.ifNull(Exp<T> exp, T ifNull)` was renamed without deprecation to `ifNullVal(Exp<T> exp, T ifNull)` to avoid 
compilation conflict with the other `ifNull(..)` variant.

### [dflib #486](https://github.com/dflib/dflib/issues/486)
`ColumnSet.compactInt(..)`, `ColumnSet.compactLong(..)` and
other primitive compaction methods became "non-terminal", so they no longer return a `DataFrame`, but rather a 
`ColumnSet`. As a result, you may get a compilation error. You should add a "merge" step to them, like: 
`ColumnSet.compactInt(..).merge()`

### [dflib #503](https://github.com/dflib/dflib/issues/503)
If you are using DFLib in Jupyter with [JJava kernel](https://github.com/dflib/jjava/), DFLib starting from version 2.0.0-M3 would require JJava 
kernel version [1.0-a5](https://github.com/dflib/jjava/releases/tag/1.0-a5) or newer.

### [dflib #525](https://github.com/dflib/dflib/issues/525)
`DataFrame.stack()` method is now non-terminal, so you will need to change it to `DataFrame.stack().select()`.

### [dflib #539](https://github.com/dflib/dflib/issues/539)
`sum(int(col))` expression now produces a `long` instead of an `int`. This may change the type of numeric return values 
in group by and window expressions. 

### [dflib #546](https://github.com/dflib/dflib/issues/546)
If you are using DFLib in Jupyter with JJava kernel, the minimal supported kernel version is `1.0-a6`. You will need to 
upgrade the kernel if you are on an earlier version.

### [dflib #548](https://github.com/dflib/dflib/issues/548)
ECharts JavaScript library was upgraded to version 6.0.0. For the list of EChart changes, you can follow 
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

### [dflib #597](https://github.com/dflib/dflib/issues/597)

_This advice only applies to 2.0.0-M6. In M7 we simply made compaction the default that can not be changed. This just
gives the best performance out of the box without any known downsides. So ignore it for any other versions._

While Parquet file loading became much faster due to other improvement, implicit column compaction was turned off for 
consistency, that can result in higher memory usage (and sometimes slower loads). You need to explicitly tag all your 
lower-cardinality columns as "compact" to get the best performance:

```java
Parquet.loader()
  .compactCols("a", "c")
  .load("my.parquet");
```

### [dflib #601](https://github.com/dflib/dflib/issues/601)
When saving to `.avro`, we stopped using the following 
DFLib-specific logical types: `dflib-bytes`, `dflib-localdate`, `dflib-localtime`, `dflib-localdatetime`, `dflib-bigdecimal`,
replacing them with proper types from the Avro specification. DFLib 2 will be able to read `.avro` files created in
DFLib 1 (so it is backwards compatible), but DFLib 1 may not be always able to properly convert the types produced by v2.