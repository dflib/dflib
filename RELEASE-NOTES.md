## Release 2.0.0-M7

* #604 ParquetLoader to use compaction by default
* #607 CsvLoader.nullPadRows()

## Release 2.0.0-M6

* #439 DataFrame.colsRange(..), colsHead(..), colsTail(..)
* #440 DataFrame.rowsHead(..), rowsTail(..)
* #550 ECharts: custom themes don't work in Jupyter
* #559 NumExp / QL "negate" op
* #562 Column getColumnName() and toQL() consistency
* #563 QL: "startsWith(..)" function without the "str(..)" wrapper for the first argument
* #564 QL: lower(..), upper(..) without the "str(..)" wrapper for the argument
* #565 QL: trim(..) without the "str(..)" wrapper for the argument
* #566 QL: QL: contains(..), matches(..) without "str(..)" wrapper for the argument
* #571 DataFrame <-> Iterable<Map> conversions
* #572 Require Java 17+
* #573 DataFrame <-> Iterable<? extends Record> conversions
* #574 Robust ECharts embedding script
* #576 QL: Can't specify parameters for "col(?)" index
* #577 Udf0 functional interface
* #580 Issues with parquet types inference
* #581 Avro loader: support standard logical types #581
* #582 Parquet loader: support for all spec primitive types, including most "logical"
* #585 "mergeAll(Udf1)"
* #586 Parquet loader: support for MAP and LIST
* #587 Avro loader: support for complex types
* #589 ECharts: YAxis.scale(..) property
* #590 ECharts: VisualMap.outOfRange
* #591 ECharts: CalendarModel.yearLabel
* #592 ECharts: VisualMap.show
* #593 ECharts: scatter charts to support "symbol"
* #594 "split(..)" expression should produce compact columns
* #595 dflib-jupyter - JJava-aware logging settings
* #596 Parquet loader optimizations
* #597 ParquetLoader: align value cardinality with explicit compaction settings
* #598 AvroSchemaLoader: support loading schema from Avro files
* #599 AvroLoader: column filtering
* #600 AvroLoader, ParquetLoader: compactCols(..)
* #601 AvroSaver: use spec types where possible
* #602 AvroSaver: support for UUID, Instant values

## Release 2.0.0-M5

* #532 Can not expand collections with QL
* #538 Allow nulls in numeric aggregation functions
* #539 Consistently return "long" when summing ints
* #540 Edge conditions for variance and stdDev
* #541 Support for variance and stdDev functions for $bigint
* #543 ECharts: "itemStyle.color" linked to a DataFrame column
* #544 QL: can not use parameter where a single column or val 
* #546 Upgrade to JJava 1.0-a6
* #547 ECharts: line chart "symbol" property
* #548 ECharts: upgrade to 6.0.0
* #549 ECharts: Allow custom themes (experimental)
* #551 ECharts: NPE with two series reusing column names
* #552 ECharts: scatter plots on a single axis
* #553 ECharts: bar chart to allow an arbitrary column to be used in bar labeling
* #554 ECharts: line chart to allow an arbitrary column to be used in symbol labeling
* #555 Performance: parallel column evaluation
* #556 IndexedSeries.compact() race condition

## Release 2.0.0-M4

* #406 ECharts: "symbolSize" linked to a DataFrame column
* #506 Exp parser: support for the scale() function 
* #507 Exp parser: concat() function should allow any argument type 
* #508 Exp parser: support vConcat() aggregation function 
* #510 String split expression for "ColumnSet.expand(..)"
* #512 Exp parser: support set(), list() and array() aggregation functions 
* #513 Exp parser: allow using function names as a column in the corresponding context 
* #514 Exp parser: support in and not in operators
* #515 String sorter parser 
* #516 Parse lists of Exp and Sorters 
* #517 Parameters in the QL parser
* #518 QL Parser: column alias support 
* #519 "FSFolder.include(..)" filter
* #520 Zip source to exclude hidden files by default
* #521 ZipEntry name extension filter for Zip
* #522 "Zip.include(..)" filter
* #523 Tar ByteSource(s)
* #525 Configurable "stack()" operation
* #527 StrExp.lower(), upper()
* #528 Add notBetween() operator
* #530 StrExp.len()

## Release 2.0.0-M3

* #422 "ColumnSet.expand()" as a non-terminal operation
* #446 "RowSet.unique()" as a non-terminal operation
* #462 "Exp.shift(..)" in specialized expressions must return the type of that expression
* #463 "Exp.as(..)" in specialized expressions must return the type of that expression
* #464 Printer.toString(..) -> Printer.print(..)
* #465 Align display defaults between core Printers and Jupyter
* #466 Add max columns limit to Printers
* #467 Index.head(..), tail(..)
* #468 Faster tabular printers
* #469 Exp.mapBool(..) and Exp.mapBoolVal(..) with binary predicate
* #470 Tabular Printers should output "..." for a row limit of 1
* #471 Align Window API with GroupBy
* #472 "df.cols().merge(Series)" overrides columns with numeric names
* #474 "$decimalVal(..)" improvements
* #475 "$bigintVal(..)" String argument flavor
* #478 ifNull($col(...), $col(...)) does not compile
* #479 "RowSet.sort()" as a non-terminal operation
* #483 Upgrade ECharts to 5.6.0
* #484 Upgrade Parquet and Hadoop
* #486 ColumnSet.compact()
* #487 Series.compact()
* #488 Upgrade Apache POI to 5.4.1
* #489 Fix resource management for HttpByteSource
* #490 ECharts: BarSeriesOpts.barWidth
* #493 IntReverseSequenceSeries - decreasing sequential IntSeries
* #494 Upgrade to Parquet 1.15.2
* #495 Force column name deduplication in SqlLoader
* #496 Force column name deduplication in CsvLoader
* #497 String expressions parser
* #498 Exp.toQL() should not quote boolean scalars
* #500 Use Udf1 for pivot aggregator for consistency
* #503 Upgrade JJava to 1.0-a5 
* #504 Methods taking Exp<?> argument(s) should have String argument(s) versions

## Release 2.0.0-M2

* #459 `BoolBuilder.buildSeries()` produces incorrect first element for size greater than 64 

## Release 2.0.0-M1

* #326 Memory-efficient bitset-based BooleanSeries
* #359 Exp: IllegalArgumentException on arithmetics ops following aggregation
* #361 Introduce aggregating semantics in Exp
* #372 NumExp.as() should return a NumExp
* #374 "min", "max", "avg", "median" aggregating functions for date and time expressions
* #377 Upgrade SLF4J to 2.0.16
* #378 Upgrade commons-compress to 1.27.1
* #379 Upgrade Apache POI to 5.3.0
* #381 DFLibJupyterExtension to auto-load root imports of each "dflib-xyz" module
* #384 EChartHtmlSaver to save charts to HTML
* #385 ECharts: "legend" display parameters
* #386 ECharts: "title" subtext and positioning parameters
* #387 EChartsHtmlSaver and custom HTML templates
* #388 Column expressions ".reduce(..)" to behave as ".first()"
* #392 Quantile series method and expression
* #396 NumExp.round()
* #398 Deprecate "DataFrame.convertColumn(..)"
* #399 CSV GZIP (de)compression
* #401 ECharts: heatmap chart
* #403 BoolAccum - java.lang.ArrayIndexOutOfBoundsException 
* #407 "min", "max" aggregating functions for StrExp
* #411 ECharts Jupyter: Can't render the same chart multiple times
* #416 Zip ByteSource(s)
* #417 Filesystem ByteSource(s)
* #421 "RowSet.expand()" as a non-terminal operation
* #425 Series.replace(int,T)
* #426 All "Json.load(..)" and "Json.save(..)" methods must be static
* #427 Type-specific versions of Exp.$val(..)
* #429 Invalid column names for CSVs with leading BOM char
* #431 Datetime expressions' comparison operations to take String arguments 
* #432 Unify map-to-primitive-Series API with ColumnSets
* #433 Primitive ValueMappers consistency
* #436 ByteSource.uri()
* #438 ByteSource.decompress()
* #442 Add DecimalScalarExp
* #443 Implement equals() and hashCode() for expressions
* #444 BigInteger expressions
* #445 RowSet.merge(), ColumnSet.merge(), RowColumnSet.merge()
* #447 Window.select(..), Window.merge(..) do not work with non-aggregating functions
* #449 "$decimal(..).avg()", "$bigint(..).avg()"
* #450 Unexpected decimal operations precision

## Release 1.4.0

* #492 "IntSequenceSeries.copyToInt(..)" is broken
* #501 ExcelLoader modifies Excel files it opens

## Release 1.3.0

* #457 MissingFormatWidthException when printing Series with all empty strings
* #477 vConcat loses the type of the column
* #480 Add "NumExp.variance(..)" and "NumExp.sdtDev(..)"
* #481 NumExp.max() for double can't handle all negative numbers

## Release 1.2.0

* #430 CsvLoader.nullString(String)
* #435 ByteSources.process() exception for single-entry source
* #437 ByteSource doesn't close streams in internal methods
* #451 Decimal equality should work if two decimals only differ in trailing fractional zeros
* #455 Insert row into dataframe

## Release 1.1.0

* #245 Exp.shift(..)
* #356 ECharts: "candlestick" should allow color selection
* #357 Primitive FloatSeries
* #362 Aggregating expressions do not generate proper QL
* #363 BooleanSeries.firstFalse()
* #367 ECharts: support "itemStyle" for "bar" series
* #368 ECharts: support "itemStyle", "symbolSize" for "scatter" series #368 
* #369 ECharts: support "itemStyle" for "pie" series
* #370 ECharts: support "itemStyle", "symbolSize", "lineStyle" for "line" series
* #371 ECharts: support "itemStyle" for "boxplot" series
* #382 $bool(..).cumSum()
* #391 Support for OffsetDateTime
* #413 ByteSource: generic data source for loaders
* #414 HTTP connector with ByteSource
* #415 CsvLoader to support an explicit encoding
* #418 Exception printing null columns

## Release 1.0.0

* #402 ECharts: multiple X axes tied to a single series
* #405 ECharts: NPE when passing XAxis without a column name

## Release 1.0.0-RC2

* #360 RowColumnSet: rename "map" to "merge"
* #364 Empty series isTrue(..) should return "false"
* #365 BooleanSeries.eq(..) : inefficiency and potential ClassCastException
* #375 Exception printing empty columns
* #376 dflib-excel: upgrade commons-compress to 1.26.0
* #383 ArrayRangeSeries.selectRange(..) is broken
* #390 "Series.shift()" with out of bounds negative shift results in ArrayIndexOutOfBoundsException
* #393 IntSequenceSeries.median(..) returns invalid value for empty series
* #394 "$decimal(0).median()" must evaluate to null for empty input

## Release 1.0.0-RC1

* #314 ECharts: "candlestick" series
* #323 CI/CD actions to create a GitHub release when doing a Maven release
* #324 Remove API deprecated since M22
* #325 ECharts: "boxplot" series
* #327 Bump org.apache.hadoop:hadoop-common from 3.3.6 to 3.4.0
* #328 Upgrade to parquet 1.14.2
* #329 Compacting columns on load
* #330 Remove API deprecated since M23
* #331 CsvLoader.colType() -> col()
* #332 Compacting CSV columns on load
* #333 Compacting Avro columns on load
* #334 AvroLoader: "string" type without "avro.java.string":"String" is processed incorrectly
* #335 Stop adding "avro.java.string" property to String field schemas
* #337 Compacting Parquet columns on load
* #338 Compacting JDBC columns on load
* #341 JsonLoader: align API naming with other loaders
* #343 Compacting Excel columns on load
* #344 Compacting JSON columns on load
* #345 JsonLoader: File, Path, InputStream load options
* #346 JsonLoader: loading from Reader results in an empty DataFrame
* #347 JsonSaver.saveToString(..)
* #349 Upgrade Avro client to 1.12.0
* #351 Methods for std deviation and variance
* #352 Methods to add and multiply Series with a constant value
* #353 Series.reduce(..)

## Release 1.0.0-M23

* #117 Read/write DataFrames from/to Parquet files
* #306 Remove APIs deprecated since M21
* #307 CsvLoader - error when specifying columns types and col names
* #308 CsvLoader - unify method naming
* #309 CsvLoader.decimalCol(..)
* #311 ColSet.select(RowToValueMapper...) does "merge" instead of "select"
* #312 DataFrame.get(col, row)
* #313 Upgrade ECharts to 5.5.1
* #316 ECharts AxisLabel options: rotate, font...
* #317 ECharts: bar chart ignores "stack" setting
* #318 ECharts: disallow overriding the hardcoded default series opts
* #320 ECharts: plot series - explicit "name" property
* #321 Switch to the "org.dflib.jjava:jupyter-jvm-basekernel", turn into jjava extension

## Release 1.0.0-M22

* #291 Remove APIs deprecated since M20
* #292 Invalid ECharts JS for strings containing single quotes
* #293 ECharts: SeriesOps.showSymbol
* #294 ECharts: SeriesOpts.label
* #295 ECharts: tooltip
* #296 ECharts: more Axis properties, multiple Y axes
* #297 PivotBuilder - align method naming with DataFrame
* #298 WindowBuilder - support "range" in "agg(..)"
* #299 Window - align API with ColumnSet and GroupBy
* #300 GroupBy.map(..) / select(..) - create columns for positions that do not match
* #301 ECharts: pie chart
* #302 Rename "map" to "merge" in RowSet, ColumnSet, GroupBy
* #303 ECharts: multiple X axes
* #304 ECharts: Grid support

## Release 1.0.0-M21

* #265 RowSet: add missing "sort" methods
* #266 GroupBy.cols(..)
* #267 GroupBy "head()", "tail()" to support negative values
* #268 Remove APIs deprecated since 1.0.0-M19
* #269 StrExp.substr(..)
* #270 Replace custom predicates with the ones from JDK
* #271 Make Index more Series-like
* #272 GroupBy.select(Exp...) / map(Exp...)
* #273 Series.replace(Map), position(..), contains(..), expand(..)
* #278 Upgrade Avro -> commons-compress to 1.26.1
* #279 Upgrade POI dependency to 5.2.5
* #280 Upgrade Jackson dependency to 2.15.4
* #285 Integrate ECharts JS plot library in DFLib
* #286 commons-csv 1.11.0

## Release 1.0.0-M20

* #240 Reusable user-defined functions (UDF)
* #249 Remove APIs deprecated since 0.16
* #250 Remove APIs deprecated since 0.17 
* #251 Remove APIs deprecated since 0.18
* #252 ColumnSet / RowSet like API for CsvLoader
* #253 DataFrame.byRow() - automatically guess buffer capacity
* #254 CsvLoader.offset(..), CsvLoader.limit(..) #254 
* #256 Environment-based Printer for better JShell integration
* #258 TableSaver.batchSize(int)
* #259 CsvLoader.generateHeader()
* #260 ExcelLoader.offset(..), ExcelLoader.limit(..)
* #261 TableLoader and SqlLoader - align API with CsvLoader
* #262 ColumnSet.agg(Exp...)

## Release 1.0.0-M19

* #78 DataFrame.rows(..) : transformation over a subset of rows
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
* #238  Exp.mapVal(..) / mapConditionVal(..) to allow mapping nulls
* #239 Upgrade json-path lib to 2.9.0
* #242 DataFrame.getColumn(int) must throw declared IllegalArgumentException
* #247 RowSet.locate(..) / index(..)

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

