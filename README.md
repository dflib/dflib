

[![Maven Central](https://img.shields.io/maven-central/v/org.dflib/dflib.svg)](https://maven-badges.herokuapp.com/maven-central/org.dflib/dflib/)
[![Build Status](https://github.com/dflib/dflib/workflows/build%20test%20deploy/badge.svg?branch=master)](https://github.com/dflib/dflib/actions)

# DFLib

DFLib ("DataFrame Library") is a lightweight pure Java implementation of a common `DataFrame` data structure. 
DataFrames exist in Python (pandas), in Spark and other languages and frameworks. DFLib's DataFrame is specifically 
intended for Java and JVM languages.

With DataFrame API, you get essentially the same data manipulation capabilities you may be used to in SQL (such as 
joins, etc.), only you apply them in-memory and over dynamically defined "tables". While SQL is "declarative", 
`DataFrame` allows step-by-step transformations that are somewhat easier to understand and much easier to compose. 

`DataFrame` is extremely versatile and can be used to model a variety of data tasks. Ad-hoc log analysis, simple ETL, 
spreadsheets processing are just some of the common examples. DFLib comes with connectors for a variety of data formats: 
CSV, Excel, RDBMS (JDBC), Avro, JSON and can be easily adapted to any other formats (e.g. web-service-based ones like 
Google Sheets, etc.)

## Project Links

* [Getting Started](https://dflib.org/docs/1.x/#_getting_started_with_dflib)
* [Documentation](https://dflib.org/docs/1.x/)
* [Discussions and Support Forum](https://github.com/dflib/dflib/discussions)

### Older Stuff

* A 20-min presentation of an early version of DFLib. While the DFLib API has evolved substantially since then, the 
video still demonstrates the idea of the project...

[![DFLib at WODay Frankfurt](http://img.youtube.com/vi/WSqvEdRZsuE/0.jpg)](http://www.youtube.com/watch?v=WSqvEdRZsuE)


