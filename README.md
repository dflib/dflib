[![Maven Central](https://img.shields.io/maven-central/v/com.nhl.yadf/yadf.svg)](https://maven-badges.herokuapp.com/maven-central/com.nhl.yadf/yadf/)
[![Build Status](https://travis-ci.org/nhl/yadf.svg)](https://travis-ci.org/nhl/yadf)

# YADF

YADF ("Yet Another DataFrame") is a simple Java implementation of a common
DataFrame data structure. With YADF you get essentially the same tools
you may be used to having with SQL (such as queries and joins), only you
can use them in-memory and over dynamically defined data structures.

## What is DataFrame

DataFrame is a 2-dimensional table containing some data (numbers, Strings, Objects)
in columns and rows. You can think of it as a programming analog of a
table in a spreadsheet (or in a DB). DataFrame data structure is
ubiquitous in data transformation and analysis
in Python ([pandas](https://pandas.pydata.org/)), R,
[Apache Spark](https://spark.apache.org/docs/latest/sql-programming-guide.html#datasets-and-dataframes), etc.
But somehow there is no common lightweight DataFrame implementation
for Java that can be created and executed in memory without special
infrastructure.

YADF project's goal is to feel this gap. _Its use cases cover manipulating
data sets that can either fully fit in the program memory, or can be cleanly split into
batches before processing._

YADF can do filtering and data alteration in both table dimensions, it
supports grouping, joins and other forms of recombination of multiple
DataFrames, and can be extended with any number of custom data operations.

YADF is easy to convert to and from other tabular formats, such as
RDBMS tables, CSV. It can also be mapped to hierarchical formats like
JSON and XML.

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

* YADF is implemented in Java.

* YADF DataFrames are immutable. So each transformation creates a new
copy (of course cloning the internal data matrix is avoided whenever
possible).

* There is no "row index" (yet?) in YADF. Only the "column index".

* While there's a big conceptual overlap, YADF makes no attempt to follow
Pandas API naming. So e.g. YADF `join` is closer to pandas `merge`,
not `join` (YADF `join` is also closer to SQL `join`).