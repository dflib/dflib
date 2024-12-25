# Benchmarks

A set of DFLib JMH benchmarks. They are not particularly systematic, and often are ad-hoc things to check the 
performance of individual features. Yet, keeping them together and running all at once provides a way to 
detect cross-release and cross-JDK performance trends.

All commands below assume you are in the parent directory of benchmarks (i.e. in the `dflib` source root directory).

## Build

```bash
mvn clean package
```
Or, to compile with a particular JDK bytecode target...

```bash
mvn clean package -Dmaven.compiler.release=23
```

## Run

Run all benchmarks...
```bash
java -jar dflib-benchmark/target/dflib-benchmark.jar
```

List available benchmarks...
```bash
java -jar dflib-benchmark/target/dflib-benchmark.jar -l
```

Filter benchmarks to run...
```bash
java -jar dflib-benchmark/target/dflib-benchmark.jar [regexp]
```

### Comparison with Pandas

Data set: 5 000 000 rows with 4 columns

_Disclaimer: this was run at one time in the past on a specific hardware and specific versions of DFLib and pandas_

 &nbsp;          | DFLib     | Pandas  
-----------------|-----------|---------
 filter          | 78ms      | 181ms
 median          | 191ms     | 11ms 
 median + filter | 197ms     | 188ms
 head            | 1µs       | 1ms
 map column      | 3649ms    | 3468ms


