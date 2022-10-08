# UPGRADE INSTRUCTIONS

## 0.14

* [dflib #160](https://github.com/bootique/bootique-agrest/issues/160): From this release DFLob requires Java 11
  as the minimal version.

* [dflib #161](https://github.com/bootique/bootique-agrest/issues/161): `dflib-test` module that provides JUnit 4 
  integration will no longer be shipped. `dflib-junit5` is the only option offered from now on. If you still need
  JUnit 4 and can not upgrade, you can take `dflib-test` classes from v.0.13 and maintain them in your own code. 
  There are only a few of them, and they are fairly simple.

## 0.11

* [dflib #127](https://github.com/bootique/bootique-agrest/issues/127): With DataFrame aggregation API migration to
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

* [dflib #128](https://github.com/bootique/bootique-agrest/issues/128): Similar to #127 above, but for Series. There 
  are a few breaking changes:

  * `Series.agg(..)` method's return type is changed from a single value `R` to a single-value `Series<R>`.

  * `Series.aggMultiple(..)` method's return type is changed from `Series` to a single-row `DataFrame`. The previous 
    return type was confusing: it represented a row, not a column; it contained non-uniform data (potentially a mix of
    different data types); it was losing labels information. Returning a small single-row DataFrame looks like a more
    logical abstraction.


## 0.8

* [dflib #95](https://github.com/bootique/bootique-agrest/issues/95):

`SqlLoader` was made immutable and reusable. This resulted in API changes. If you were using `SqlLoader.params(..)`, 
remove this method call, and pass the same parameters Series (or array) to the `load` method instead.

## 0.6

* [dflib #54](https://github.com/bootique/bootique-agrest/issues/37): 

  * `Aggregator` is no longer an object that aggregates the entire row 
  (and can be composed from multiple ColumnAggregators). Instead it is 
  an aggregation spec for a single column in the aggregated result. 
  So the methods that took a single Aggregator as a parameter were 
  removed from `DataFrame` and `GroupBy`. Also `Aggregator` static factory
  methods now return `Aggregator` instances, not `ColumnAggregator`

  * `Aggregator.first()` used to return the first **non-null** element from 
  a column, which is counter-intuitive. It is changed to return the first 
  element whether it is null or not.