## Release 0.7

* #69 TableSaver: apply quotes to schema/catalog/table name separately
* #70 Remove API deprecated in 0.6 
* #71 Series, DataFrame "sample"
* #72 DataFrame.selectColumns(Predicate<String>)
* #73 JDBC sampling 
* #74 CSV sampling
* #75 DataFrame.dropColumns(Predicate<String>)
* #76 Series.forData(Iterable<T>) 
* #80 CsvLoader - better way to include/exclude columns
* #81 Implement min/max aggregation operations 

## Release 0.6

* #12 dflib-jdbc: TableSaver to merge data by key 
* #21 Add LocalDate and LocalDateTime converters 
* #23 Simplifying join API
* #24 Tabular printer should right-align numbers and booleans
* #25 CsvLoader to provide shortcuts for datetime and number conversions
* #26 IntSeries / IntMutableList for joins and filters
* #27 IntSeries / IntMutableList for sort and group
* #28 Make IntSeries compatible with Series<Integer>
* #29 DoubleSeries
* #30 BooleanSeries 
* #31 Series.replace, Series.replaceNoMatch, DataFrame.nullify, DataFrame.nullifyNoMatch #31
* #32 Series/DataFrame element-wise equality operations 
* #33 DataFrame.stack operation
* #34 Tabular and Inline printers should show head and tail
* #35 LongSeries
* #36 Series.locate(ValuePredicate) 
* #37 DataFrame.fillNullsFromSeries(..) / Series.fillNullsFromSeries(..)
* #38 Primitive Series "select" methods must account for nulls 
* #39 DataFrame.mapColumn
* #40 "Series.unique" - a method to produce Series with non-repeating values
* #43 Column converter to enums for categorical data 
* #44 "dflib-jupyter" - integration into Jupyter notebook
* #45 DataFrame.renameColumns(UnaryOperator<String>) to upper/lower case column names
* #46 Move interfaces with public static methods for creating lambdas to the root package
* #47 Series.map 
* #48 dflib-jdbc: saver must support Month, Year, enums, etc
* #49 dflib-jdbc: alternative way for TableSaver to find the types of PreparedStatement parameters
* #50 Hash Join builder 
* #51 "indicator" column for joins
* #52 Allow selection by BooleanSeries
* #54 Column-oriented aggregators, control aggregated column names
* #55 Aggregator "count" and "sum" functions improvements
* #56 "TableSaver.save()" should optionally return per-row breakdown of database operations 
* #57 CsvSaver.createMissingDirs() to save CSVs in arbitrray locations
* #58 Builder for DataFrame creation
* #59 Series.valueCount() - utility method for data exploration
* #60 dflib-jdbc: JdbcConnector to support external transaction management
* #62 Series.filter(ValuePredicate), Series.filter(BooleanSeries) 
* #63 Basic `Series.groupBy(..)` 
* #64 Series / SeriesGroupBy aggregation functionality 
* #65 API for DataFrame-based value aggregator function 
* #66 "dflib-test" a module for unit testing of Series and DataFrames 
* #67 Series.sort
* #68 BooleanSeries: and, or, not operations

## Release 0.5

* A bunch of basic DataFrame ops
*  #1 Support for 'groupBy'
*  #2 Support for DataFrame aggregation API
*  #4 'vconcat' and 'hconcat' : support for concatenting DataFrames
*  #5 'sort' and 'sortByColumns' to reorder DataFrames
*  #6 Load DataFrames from CSV
*  #7 RowProxy - "flyweight" API to hide access to Object[]
*  #8 Store DataFrames in CSV
*  #9 JDBC loader for DataFrames
* #10 JDBC saver for DataFrames
* #13 Column-oriented DataFrame
* #14 Scalar: helper accessor to DataFrame values
* #15 DataFrame.addColumn(Series)
* #16 GroupBy.rowNumbers()
* #17 Series.fillNulls / DataFrame.fillNulls
* #18 "DataFrame.select" operation for selecting rows
* #19 GroupBy.head(int) and GroupBy.toDataFrame() functions
* #20 DataFrame.tail(i), GroupBy.tail(i)

