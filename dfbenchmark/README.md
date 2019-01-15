# Benchmark suit

This module is a set of JMH benchmarks for the **DFLib**

### Build

```bash
mvn package
```

### Usage

Run all benchmarks:

```bash
java -jar target/dfbenchmark.java
```

List available benchmarks:

```bash
java -jar target/dfbenchmark.java -l
```

Filter benchmarks to run:

```bash
java -jar target/dfbenchmark.java [regexp]
```
