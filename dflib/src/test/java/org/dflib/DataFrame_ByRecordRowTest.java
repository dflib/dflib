package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataFrame_ByRecordRowTest {

    public record Person(String name, int age) {
    }

    public record Product(String name, double price, int quantity) {
    }

    public record Empty() {
    }

    public record SingleField(String value) {
    }

    public record ManyFields(String z, int a, double m, boolean h) {
    }

    @Test
    public void allColumns() {
        List<Person> data = List.of(
                new Person("Alice", 30),
                new Person("Bob", 25)
        );

        DataFrame df = DataFrame
                .byRecordRow(Person.class)
                .of(data);

        // Columns should be sorted alphabetically: age, name
        new DataFrameAsserts(df, "age", "name")
                .expectHeight(2)
                .expectRow(0, 30, "Alice")
                .expectRow(1, 25, "Bob");
    }

    @Test
    public void allColumns_SingleRecord() {
        List<Person> data = List.of(
                new Person("Alice", 30)
        );

        DataFrame df = DataFrame
                .byRecordRow(Person.class)
                .of(data);

        new DataFrameAsserts(df, "age", "name")
                .expectHeight(1)
                .expectRow(0, 30, "Alice");
    }

    @Test
    public void allColumns_EmptyIterable() {
        List<Person> data = List.of();

        DataFrame df = DataFrame
                .byRecordRow(Person.class)
                .of(data);

        new DataFrameAsserts(df, "age", "name").expectHeight(0);
    }

    @Test
    public void selectiveColumns_ByName() {
        List<Product> data = List.of(
                new Product("Widget", 9.99, 100),
                new Product("Gadget", 19.99, 50)
        );

        DataFrame df = DataFrame
                .byRecordRow(Product.class)
                .columnNames("name", "price")
                .of(data);

        new DataFrameAsserts(df, "name", "price")
                .expectHeight(2)
                .expectRow(0, "Widget", 9.99)
                .expectRow(1, "Gadget", 19.99);
    }

    @Test
    public void selectiveColumns_CustomOrder() {
        List<Product> data = List.of(
                new Product("Widget", 9.99, 100),
                new Product("Gadget", 19.99, 50)
        );

        DataFrame df = DataFrame
                .byRecordRow(Product.class)
                .columnNames("quantity", "name", "price")
                .of(data);

        // Column order should match the specified order
        new DataFrameAsserts(df, "quantity", "name", "price")
                .expectHeight(2)
                .expectRow(0, 100, "Widget", 9.99)
                .expectRow(1, 50, "Gadget", 19.99);
    }

    @Test
    public void selectiveColumns_SingleColumn() {
        List<Person> data = List.of(
                new Person("Alice", 30),
                new Person("Bob", 25)
        );

        DataFrame df = DataFrame
                .byRecordRow(Person.class)
                .columnNames("name")
                .of(data);

        new DataFrameAsserts(df, "name")
                .expectHeight(2)
                .expectRow(0, "Alice")
                .expectRow(1, "Bob");
    }

    @Test
    public void selectiveColumns_ByIndex() {
        List<Product> data = List.of(
                new Product("Widget", 9.99, 100)
        );

        DataFrame df = DataFrame
                .byRecordRow(Product.class)
                .columnIndex(Index.of("price", "quantity"))
                .of(data);

        new DataFrameAsserts(df, "price", "quantity")
                .expectHeight(1)
                .expectRow(0, 9.99, 100);
    }

    @Test
    public void selectiveColumns_NonExistentColumn() {
        List<Person> data = List.of(
                new Person("Alice", 30)
        );

        assertThrows(IllegalArgumentException.class, () -> {
            DataFrame.byRecordRow(Person.class)
                    .columnNames("name", "nonexistent")
                    .of(data);
        });
    }

    @Test
    public void emptyRecord() {
        List<Empty> data = List.of(
                new Empty(),
                new Empty()
        );

        DataFrame df = DataFrame
                .byRecordRow(Empty.class)
                .of(data);

        assertEquals(0, df.height());
        assertEquals(0, df.width());
    }

    @Test
    public void singleFieldRecord() {
        List<SingleField> data = List.of(
                new SingleField("one"),
                new SingleField("two")
        );

        DataFrame df = DataFrame
                .byRecordRow(SingleField.class)
                .of(data);

        new DataFrameAsserts(df, "value")
                .expectHeight(2)
                .expectRow(0, "one")
                .expectRow(1, "two");
    }

    @Test
    public void withNulls() {
        List<Person> data = List.of(
                new Person("Alice", 30),
                new Person(null, 25),
                new Person("Charlie", 0)
        );

        DataFrame df = DataFrame
                .byRecordRow(Person.class)
                .of(data);

        new DataFrameAsserts(df, "age", "name")
                .expectHeight(3)
                .expectRow(0, 30, "Alice")
                .expectRow(1, 25, null)
                .expectRow(2, 0, "Charlie");
    }

    @Test
    public void withCapacity() {
        List<Person> data = List.of(
                new Person("Alice", 30),
                new Person("Bob", 25)
        );

        DataFrame df = DataFrame
                .byRecordRow(Person.class)
                .capacity(10)
                .of(data);

        new DataFrameAsserts(df, "age", "name")
                .expectHeight(2)
                .expectRow(0, 30, "Alice")
                .expectRow(1, 25, "Bob");
    }

    @Test
    public void alphabeticalOrdering() {

        List<ManyFields> data = List.of(
                new ManyFields("zed", 1, 3.14, true)
        );

        DataFrame df = DataFrame
                .byRecordRow(ManyFields.class)
                .of(data);

        // Should be sorted alphabetically: a, h, m, z
        new DataFrameAsserts(df, "a", "h", "m", "z")
                .expectHeight(1)
                .expectRow(0, 1, true, 3.14, "zed");
    }
}
