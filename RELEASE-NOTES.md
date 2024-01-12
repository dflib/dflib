## Release 1.0.0-M1

* #210 RowProxy: support for primitives and simpler casting
* #211 Optimization: replace Accum with direct array ops on primitive Series
* #212 DataFrame.cols(..).op(..): transformation over a subset of columns
* #213 StrExp.split()
* #214 Parallel Aggregation of columns
* #215 DataFrame: "name" property and column aliases in joins
* #219 IndexedSeries.size(): NPE due to a race condtion
* #221 Index.positions(), .positionsExcept()
* #222 Refactor "Index.forLabel(..)" to "Index.of(..)" 
* #223 Select / transform columns inside joins
* #224 Extractor.$val(..)
* #225 Exp.rowNum()
* #226 "between(..)" operator
* #227 Super-lazy IndexedSeries
* #228 DataFrame : negative values for "head" and "tail"
* #229 Add missing Avro, Excel, JSON modules to Jupyter dependencies
* #230 Optimize SingleValueSeries creation
* #231 Series.diff(), intersect()
* #232 "ne(..)" is broken for BooleanSeries, DoubleSeries, LongSeries
* #233 Series.replace(..) to replace positions from another Series
* #234 IntSequenceSeries.rangeOpenClosedInt() is broken
* #235 Move to dflib.org - change POM group and Java packages
* #236 Fluent RowBuilder.set(..)
* #237 Primitive Series "copyTo" ignores "fromOffset"

## Release 0.18

*  #41 DataFrame.unique - produce DataFrame with non-repeating rows
* #184 DataFrame: rename "convertColumn(pos, Exp)" to "replaceColumn(pos, Exp)"
* #186 Exp.in, Exp.notIn, Series.in, Series.notIn
* #187 Exp.last(), Series.last()
* #189 ExcelLoader: Duplicate empty column names
* #190 DateTimeExp - direct converter to DateExp and TimeExp
* #191 ExcelLoader.skipRows(int)
* #192 Avoid "arg + vararg" methods
* #193 DFLib lack of "addRow" method
* #195 Bump org.apache.avro:avro from 1.10.1 to 1.11.3
* #196 StrExp.contains()
* #198 Numeric "Exp.eq", "Exp.ne" has different op label vs other expressions
* #200 DataFrame: deprecate methods returning columns
* #201 Series.castAsXyz methods
* #202  DataFrame: deprecate "toXyzColumn()" API
* #203 DataFrame / Series: negative values for "head" and "tail"
* #204 "IntSequenceSeries.headInt(n)" is broken
* #205 Primitive Series: deprecate "headXyz" and "tailXyz" variants
* #206 Primitive Series: deprecate "uniqueXyz" variants
* #207 Primitive Series: deprecate "materializeXyz" variants
* #208 Polymorphic "select" return type for primitive Series

## Release 0.17

* #185 Undeprecate and fix "convertColumn(X, ValueMapper)" 

## Release 0.16

* #158 DataFrame.vExplode()
* #167 Exp.castAsDate()
* #168 Exp.castAsStr()
* #169 DateExp comparison expressions
* #170 DateExp - exp to extract day, month, year
* #171 TimeExp
* #173 Remove API deprecated since 0.12
* #175 Optimize Exp.concat(..)
* #178 JsonLoader add columnType function
* #179 DateTimeExp
* #180 Primitives-aware DataFrame builder
* #181 Reimplement and deprecate DataFrameBuilder
* #182 Refactor and unify Series builder methods
* #183 CsvLoader.selectRows(RowPredicate)

## Release 0.15

* #166 Exp.mapCondition(), Exp.mapConditionVal()

## Release 0.14
_(this release is broken - dflib-bom artifact is missing. Use 0.15 or newer)_

* #138 Storing DataFrame in Excel
* #152 Cumulative sum expression
* #153 Range definitions for WindowBuilder
* #154 Remove API deprecated in 0.11
* #155 WindowBuilder.mapColumn(..)
* #156 Incorrect default name for "$decimal("a").sum()" 
* #157 "Exp.castAsCondition()" 
* #160 Switch to Java 11
* #162 ExcelLoader.firstRowAsHeader()
* #163 Load/store DataFrames from Excel Workbook object
* #165 Upgrade to Jackson 2.13.4 / 2.13.4.2

## Release 0.13

* #136 Support for loading Excel files
* #137 "ne" incorrectly compares numbers with non-numbers
* #139 JDBC Tx "Error processing connection" on rollback of nested transactions
* #142 Use Exp.getColumn instead of  Exp.toQL for method DataFrame.selectColumns
* #150 Upgrade jackson to 2.13.3

## Release 0.12

* #134 Bootique and Testcontainers dependency leaks via BOM import

## Release 0.11

* #116 Map operation over the entire DataFrame
* #118 Read/write DataFrames from/to Avro files
* #119 Primitive (custom) accumulators for DataFrameBuilder.byRow(..)
* #120 DataFrame.pivot(..)
* #121 BigDecimal aggregators with controlled scale
* #122 Expression API
* #123 Improve performance of "DF.filterRows(BooleanSeries)" 
* #125 Column-based sort algorithm for better performance and expressions support
* #127 Make Aggregator compatible with Exp
* #128 Exp to eval against Series
* #129 Rename "filter" methods to "select"
* #130 Support Sorter in window functions
* #131 Support aggregation expressions as window functions
* #132 dflib-json is not included in the BOM
* #133 dflib-json - implement JsonSaver

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

