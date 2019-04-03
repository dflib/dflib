[![Maven Central](https://img.shields.io/maven-central/v/com.nhl.dflib/dflib.svg)](https://maven-badges.herokuapp.com/maven-central/com.nhl.dflib/dflib/)
[![Build Status](https://travis-ci.org/nhl/dflib.svg?branch=master)](https://travis-ci.org/nhl/dflib)

# DFLib

DFLib ("DataFrame Library") is a simple Java implementation of a common
DataFrame data structure. With DFLib you get essentially the same data 
manipulation tools you may be used to in SQL (such as queries and joins), 
only you can use them in-memory and over dynamically defined data structures.

## What is DataFrame

DataFrame is a 2-dimensional table containing some data (numbers, Strings, Objects)
in columns and rows. You can think of it as a programming analog of a
table in a spreadsheet (or in a DB). DataFrame is
ubiquitous in data transformation, analysis and ML fields. There are 
implementations in Python ([pandas](https://pandas.pydata.org/)), R,
[Apache Spark](https://spark.apache.org/docs/latest/sql-programming-guide.html#datasets-and-dataframes), etc.
But somehow there is no common lightweight DataFrame
in Java that can be created and executed in memory without special
infrastructure. DFLib project's goal is to fill this gap.

_Its primary use case is manipulating data sets that can either fully
fit in the app memory, or can be split into batches, with
each batch individually processed in memory._

DFLib can do data filtering, alteration and restructuring in both table
dimensions, it supports grouping, joins and other forms of data recombination, 
and can be extended with any number of custom data operations.

DFLib is easy to convert to and from other tabular formats, such as
RDBMS tables, CSV. It can also be mapped to hierarchical formats like
JSON and XML.

## Usage Examples

Include DFLib in a project. Start by declaring a "BOM" to have a common version
for multiple DFLib modules:
```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>com.nhl.dflib</groupId>
      <artifactId>dflib-bom</artifactId>
      <version>0.5</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```
Next include the dependency oin DFLib core:
```xml
...
<dependency>
    <groupId>com.nhl.dflib</groupId>
    <artifactId>dflib</artifactId>
</dependency>
```

Create a DataFrame and run varios data transformations:
```java
// creation
Index columns = Index.withLabels("a", "b", "c");
DataFrame df1 = DataFrame.forStreamFoldByRow(columns, IntStream.range(1, 10000).boxed())

// filtering, mapping
DataFrame df2 = df1
   .filterByColumn("a", (Integer v) -> v % 2 == 0)
   .mapColumnValue("b", (Integer v) -> v * 5)        // 1. transform a single column
   .map((from, to) -> from.copy(to)); // 2. transform the entire row. 
                                      // Showing how the row is copied.
   
// joins
DataFrame df3 = DataFrame.forSequenceFoldByRow(columns, 2, "a", "b", 4, "c", "d")
   .innerJoin(df2, Hasher.forColumn("a"), Hasher.forColumn("a"));
```

Print DFLib state to console
```java
// TODO
```

Store DFLib in a CSV file:
```java
// TODO
```
Store DFLib in a DB table:
```java
// TODO
```


## Difference with Pandas

* DFLib is implemented in Java (instead of Python).

* DFLib has nowhere near the amount of features that pandas has. The goal
is to gradually works towards feature parity.

* DFLib DataFrames are immutable (this is important for your sanity!)
So each transformation creates a new copy (of course cloning the internal
data matrix is avoided whenever possible).

* There is no explicit "row index" (yet?) in DFLib. Only the "column index".
Though a concept of an "indexed" DataFrame is used a lot internally.

* DFLib makes no attempt to follow Pandas API naming. So e.g. DFLib 
`join` is closer to pandas `merge`, not `join` (DFLib `join` is also 
closer to SQL `join`).
