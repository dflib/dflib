# Benchmark suit

This module is a set of JMH benchmarks for the **DFLib**

### Build

```bash
mvn package
```

### Usage

Run all benchmarks:

```bash
java -jar target/dflib-benchmark.jar
```

List available benchmarks:

```bash
java -jar target/dflib-benchmark.jar -l
```

Filter benchmarks to run:

```bash
java -jar target/dflib-benchmark.jar [regexp]
```

### Comparision with Pandas

Data set: 5 000 000 rows with 4 columns

 &nbsp;          | DFLib     | Pandas  
-----------------|-----------|---------
 filter          | 78ms      | 181ms
 median          | 191ms     | 11ms 
 median + filter | 197ms     | 188ms
 head            | 1µs       | 1ms
 map column      | 3649ms    | 3468ms


