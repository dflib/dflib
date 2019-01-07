[![Maven Central](https://img.shields.io/maven-central/v/com.nhl.dflib/dflib.svg)](https://maven-badges.herokuapp.com/maven-central/com.nhl.dflib/dflib/)
[![Build Status](https://travis-ci.org/nhl/dflib.svg?branch=master)](https://travis-ci.org/nhl/dflib)

# DFLib

DFLib ("DataFrame Library") is a simple Java implementation of a common
DataFrame data structure. With DFLib you get essentially the same tools
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
infrastructure. DFLib project's goal is to feel this gap.

_Its primary use case is manipulating data sets that can either fully
fit in the app memory, or can be split into batches, with
each batch individually processed in memory._

DFLib can do filtering, data alteration and restructuring in both table
dimensions, it supports grouping, joins and other forms of recombination
of multiple DataFrames, and can be extended with any number of custom
data operations.

DFLib is easy to convert to and from other tabular formats, such as
RDBMS tables, CSV. It can also be mapped to hierarchical formats like
JSON and XML.

## Usage Examples

Include DFLib in a project:

```xml
<dependency>
    <groupId>com.nhl.dflib</groupId>
    <artifactId>dflib</artifactId>
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
   .join(anotherDF, (c, lr, rr) -> Objects.equals(c.getLeft(lr, 0), c.getRight(rr, 0));
```

## Difference with Pandas

* DFLib is implemented in Java.

* DFLib has nowhere near the amount of features that pandas has. The goal
is to add any currently missing functionality as it is requested by the users.

* DFLib DataFrames are immutable. So each transformation creates a new
copy (of course cloning the internal data matrix is avoided whenever
possible).

* There is no "row index" (yet?) in DFLib. Only the "column index".

* While there's a big conceptual overlap, DFLib makes no attempt to follow
Pandas API naming. So e.g. DFLib `join` is closer to pandas `merge`,
not `join` (DFLib `join` is also closer to SQL `join`).