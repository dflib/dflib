[![Maven Central](https://img.shields.io/maven-central/v/com.nhl.yadf/yadf.svg)](https://maven-badges.herokuapp.com/maven-central/com.nhl.yadf/yadf/)
[![Build Status](https://travis-ci.org/nhl/yadf.svg)](https://travis-ci.org/nhl/yadf)

# YADF

YADF ("Yet Another DataFrame") is a simple Java implementation of a common
DataFrame data structure.

## What is DataFrame

DataFrame is a 2-dimensional table containing some data (numbers, Strings, Objects).
You can think of it as a programming analog of a table in a spreadsheet.
DataFrame is a data structure ubiquitous in data analysis and transformation
in Python ([pandas](https://pandas.pydata.org/)), R,
[Apache Spark](https://spark.apache.org/docs/latest/sql-programming-guide.html#datasets-and-dataframes), etc.
But somehow there is no common lightweight DataFrame implementation
for Java that can be executed in memory without special infrastructure.

YADF project goal is to feel this gap. Its use cases cover data sets that
can either fully fit in memory, or can be cleanly split into batches before
processing.

YADF can do data filtering and alteration on both table dimensions, it
supports grouping, joins and other forms of recombination of multiple
DataFrames, and can be extended with any number of custom data operations.

## Usage Examples

Include YADF in a project:

```xml
<dependency>
    <groupId>com.nhl.yadf</groupId>
    <artifactId>yadf</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

Create a DataFrame and do some transformations:

```java
List<Object[]> data = ...
Index columns = Index.withNames("a", "b", "c");

DataFrame df = DataFrame.create(columns, data)
   .filter((c, r) -> c.get(r, 0).startsWith("a"))
   .mapColumn("b", (c, r) -> c.get(r, "b")).toString().toLowerCase())
   .join(anotherDF, (lr, rr) -> Objects.equals(lr[0], rr[0]));
```

## Difference with Pandas

* YADF DataFrames are immutable. So each transformation creates a new
copy (of course copying the internal data matrix is avoided whenever
possible).

* There is no "row index" (yet?) in YADF. Only the "column index".