# UPGRADE INSTRUCTIONS

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