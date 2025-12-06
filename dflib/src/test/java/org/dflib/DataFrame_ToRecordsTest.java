package org.dflib;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataFrame_ToRecordsTest {

    public record Person(String name, int age) {
    }

    public record Product(String name, double price, int quantity) {
    }

    public record PersonWithExtra(String name, int age, String city, int salary) {
    }

    public record Empty() {
    }

    @Test
    public void basic() {
        DataFrame df = DataFrame.foldByRow("name", "age").of(
                "Alice", 30,
                "Bob", 25
        );

        List<Person> records = df.toRecords(Person.class);

        assertEquals(2, records.size());
        assertEquals(new Person("Alice", 30), records.get(0));
        assertEquals(new Person("Bob", 25), records.get(1));
    }

    @Test
    public void recordWithExtraComponents() {
        // DataFrame has only "name" and "age", but record has "extra" component
        DataFrame df = DataFrame.foldByRow("name", "age").of(
                "Alice", 30,
                "Bob", 25
        );

        List<PersonWithExtra> records = df.toRecords(PersonWithExtra.class);

        assertEquals(2, records.size());

        // check both object and primitive extra components
        assertEquals(new PersonWithExtra("Alice", 30, null, 0), records.get(0));
        assertEquals(new PersonWithExtra("Bob", 25, null, 0), records.get(1));
    }

    @Test
    public void dataFrameColumnNotInRecord() {
        // DataFrame has column "salary" that doesn't exist in Person record
        DataFrame df = DataFrame.foldByRow("name", "age", "salary").of(
                "Alice", 30, 50000
        );

        assertThrows(IllegalArgumentException.class, () -> df.toRecords(Person.class));
    }

    @Test
    public void emptyDataFrame() {
        DataFrame df = DataFrame.empty("name", "age");
        List<Person> records = df.toRecords(Person.class);
        assertEquals(0, records.size());
    }

    @Test
    public void singleRow() {
        DataFrame df = DataFrame.foldByRow("name", "age").of(
                "Alice", 30
        );

        List<Person> records = df.toRecords(Person.class);

        assertEquals(1, records.size());
        assertEquals(new Person("Alice", 30), records.get(0));
    }

    @Test
    public void withNulls() {
        DataFrame df = DataFrame.foldByRow("name", "age").of(
                "Alice", 30,
                null, 25,
                "Charlie", null
        );

        List<Person> records = df.toRecords(Person.class);

        assertEquals(3, records.size());
        assertEquals(new Person("Alice", 30), records.get(0));
        assertEquals(new Person(null, 25), records.get(1));
        assertEquals(new Person("Charlie", 0), records.get(2));
    }

    @Test
    public void columnOrderDoesNotMatter() {
        // DataFrame columns in different order than record components
        DataFrame df = DataFrame.foldByRow("age", "name").of(
                30, "Alice",
                25, "Bob"
        );

        List<Person> records = df.toRecords(Person.class);

        assertEquals(2, records.size());
        assertEquals(new Person("Alice", 30), records.get(0));
        assertEquals(new Person("Bob", 25), records.get(1));
    }

    @Test
    public void threeFields() {
        DataFrame df = DataFrame.foldByRow("name", "price", "quantity").of(
                "Widget", 9.99, 100,
                "Gadget", 19.99, 50
        );

        List<Product> records = df.toRecords(Product.class);

        assertEquals(2, records.size());
        assertEquals(new Product("Widget", 9.99, 100), records.get(0));
        assertEquals(new Product("Gadget", 19.99, 50), records.get(1));
    }

    @Test
    public void empty() {
        DataFrame df = DataFrame.empty();
        List<Empty> records = df.toRecords(Empty.class);
        assertEquals(0, records.size());
    }
}
