[![Maven Central](https://img.shields.io/maven-central/v/org.dflib/dflib.svg)](https://maven-badges.herokuapp.com/maven-central/org.dflib/dflib/)
[![Build Status](https://github.com/dflib/dflib/workflows/build%20test%20deploy/badge.svg?branch=master)](https://github.com/dflib/dflib/actions)

# DFLib

DFLib ("DataFrame Library") is a lightweight pure Java implementation of a common `DataFrame` data structure. 
DataFrames exist in Python (pandas), R, Spark and other languages and frameworks. DFLib's DataFrame is specifically 
intended for Java and JVM languages.

With DataFrame API, you get essentially the same data manipulation capabilities you may be used to in SQL (such as 
joins, etc.), only you apply them in-memory and over dynamically defined "table" objects. While SQL is "declarative", 
`DataFrame` allows step-by-step transformations that are somewhat easier to understand and much easier to compose. 

`DataFrame` is extremely versatile and can be used to model a variety of data tasks. ETL, log analysis, spreadsheets 
processing are just some of the examples. DFLib comes with connectors for many data formats: 
CSV, Excel, RDBMS, Avro, JSON and can be easily adapted to other formats (e.g. web-service-based ones like 
Google Sheets, etc.)

DFLib provides integration with Apache Echarts to visualize DataFrame data. Charts are generated in a form of HTML/JavaScript 
code and work in Jupyter as well as regular web applications.

![dflib-bar-chart](https://dflib.org/images/charts/dflib-bar-chart_v2.svg)
![dflib-timeseries-chart](https://dflib.org/images/charts/dflib-pie-chart-area_v2.svg) 
![dflib-timeseries-chart](https://dflib.org/images/charts/dflib-timeseries-chart_v2.svg) 
![dflib-timeseries-chart](https://dflib.org/images/charts/dflib-pie-chart-angle_v2.svg) 

While DFLib works in any Java application, it has a [special intergation](https://dflib.org/docs/1.x/#jupyter) with 
[Jupyter Notebook](https://jupyter.org/), a browser-based interactive environment for data exploration and analysis popular 
among data scientists and data engineers. In fact, our community maintains a [Java "kernel" for Jupyter](https://github.com/dflib/jjava) 
as a sister project to DFLib.

## Project Links

* [Website](https://dflib.org/)
* [Getting Started](https://dflib.org/docs/1.x/#_get_started_with_dflib)
* [Documentation](https://dflib.org/docs/1.x/)
* [Discussions and Support Forum](https://github.com/dflib/dflib/discussions)

## Presentation Videos
* [DataFrame, a Swiss Army Knife of Java Data Processing](https://www.youtube.com/watch?v=OrGqCflOMIc), Nashville JUG, 03/2024
* [Early version of DFLib](http://www.youtube.com/watch?v=WSqvEdRZsuE), Frankfurt WO Day, 04/2019



