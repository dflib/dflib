## Release 0.11

* #116 Map operation over the entire DataFrame
* #118 Read/write DataFrames from/to Avro files
* #119 Primitive (custom) accumulators for DataFrameBuilder.byRow(..)
* #120 DataFrame.pivot(..)
* #121 BigDecimal aggregators with controlled scale
* #122 Expression API
* #123 Improve performance of "DF.filterRows(BooleanSeries)" 
* #125 Column-based sort algorithm for better performance and expressions support
* #127 Make Aggregator compatible with SeriesExp
* #129 Rename "filter" methods to "select"

## Release 0.10

* "Clean" BOM that does not leak unrelated dependency versions

## Release 0.9

* #113 MySQL 8 requires special treatment of local time
* #114 "shift" operation

## Release 0.8

* #79 Support filter condition in CsvLoader
* #91  DataFrame.over() - window functions
* #92  Series.sortIndex()
* #93  Null-safe Sorters with implicit NULL ordering policy
* #94  dflib-jdbc: SqlSaver to allow wrapping DataFrame updates in custom functions
* #95  Reusable SqlLoader - allow different parameters for the same loader instance
* #98  DbFlavor as an object with state and behavior
* #99  DbFlavor for PostgreSQL
* #100 Adding missing load/save methods to CSV
* #101 Shortcut to add single-value columns
* #102 add testcontainers in integration tests
* #103 Added option to save a DataFrame without Index/header
* #104 "dflib-junit5" - a test module that supports JUnit5
* #105 "dflib-json" and JsonLoader - the initial version of DFLib JSON "connector"
* #106 TableLoader.neq condition
* #107 TableSaver.deleteUnmatchedRows - full sync mode 
* #108 TableDeleter
* #109 JdbcConnector: Support for custom data converters
* #110 Inferring Series type from data

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
* #82 Make Series iterable
* #83 Convert Series to List, Set, array
* #84 Shortcuts for Series aggregations 
* #85 Series.map flavor that returns a DataFrame
* #86 Expose JdbcConnector DataSource for ease of integration
* #87 DataFrame.addColumns with RowMapper
* #88 Index.toSeries() 
* #89 Filter condition in aggregators (partial) 
* #90 Comparable min/max aggregators 

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

