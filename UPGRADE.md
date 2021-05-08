# UPGRADE INSTRUCTIONS

## 0.11

* [dflib #127](https://github.com/bootique/bootique-agrest/issues/127):

`DataFrame.agg(..)` method's return type is changed from `Series` to a single-row `DataFrame`. The previous return 
type was confusing: it represented a row, not a column; it contained non-uniform data (potentially a mix of 
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