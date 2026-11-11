package org.dflib.excel;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.dflib.DataFrame;
import org.dflib.junit.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.dflib.excel.TestXlsx.STYLE_DATE;
import static org.dflib.excel.TestXlsx.STYLE_DATETIME;
import static org.dflib.excel.TestXlsx.STYLE_NUMBER;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Checks that the streaming XLSX reader produces the same DataFrames as the POI usermodel.
 */
public class ExcelLoader_StreamingParityTest {

    private static final List<String> XLSX_RESOURCES = List.of(
            "cardinality-midrange-header.xlsx",
            "cardinality-midrange.xlsx",
            "cardinality.xlsx",
            "empty-column-names.xlsx",
            "multi-sheet.xlsx",
            "one-sheet-data-types.xlsx",
            "one-sheet-offset.xlsx",
            "one-sheet.xlsx",
            "phantom-leading-rows.xlsx",
            "phantom-rows-only.xlsx",
            "phantom-trailing-rows.xlsx",
            "sparse.xlsx");

    private static final Map<String, Supplier<ExcelLoader>> LOADERS = new LinkedHashMap<>();

    static {
        LOADERS.put("default", ExcelLoader::new);
        LOADERS.put("firstRowAsHeader", () -> new ExcelLoader().firstRowAsHeader());
        LOADERS.put("offset(1).limit(2)", () -> new ExcelLoader().offset(1).limit(2));
        LOADERS.put("offset(1).limit(2).firstRowAsHeader",
                () -> new ExcelLoader().offset(1).limit(2).firstRowAsHeader());
        LOADERS.put("offset(2)", () -> new ExcelLoader().offset(2));
        LOADERS.put("limit(1)", () -> new ExcelLoader().limit(1));
        LOADERS.put("offset(100)", () -> new ExcelLoader().offset(100));
        LOADERS.put("compactCol",
                () -> new ExcelLoader().sheet("Sheet1", SheetLoader.of().compactCol(0).firstRowAsHeader()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource
    public void resources(String name, String resource, Supplier<ExcelLoader> loaderFactory)
            throws URISyntaxException, IOException {

        File file = new File(getClass().getResource(resource).toURI());

        Map<String, DataFrame> streamed = loaderFactory.get().load(file);

        Map<String, DataFrame> poi;
        try (Workbook wb = WorkbookFactory.create(file, null, true)) {
            poi = loaderFactory.get().load(wb);
        }

        TestXlsx.assertSameData(poi, streamed);
    }

    private static Stream<Arguments> resources() {
        return XLSX_RESOURCES.stream().flatMap(r -> LOADERS.entrySet().stream()
                .map(l -> Arguments.of(r + ", " + l.getKey(), r, l.getValue())));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource
    public void fixtures(String name, byte[] xlsx, Supplier<ExcelLoader> loaderFactory) {
        TestXlsx.loadAndCompare(xlsx, loaderFactory);
    }

    private static Stream<Arguments> fixtures() {

        Map<String, byte[]> fixtures = new LinkedHashMap<>();
        fixtures.put("missingRefs", missingRefsXlsx());
        fixtures.put("strings", stringsXlsx());
        fixtures.put("formulas", formulasXlsx());
        fixtures.put("dates", datesXlsx());
        fixtures.put("dates1904", dates1904Xlsx());
        fixtures.put("range", rangeXlsx());

        return fixtures.entrySet().stream().flatMap(f -> LOADERS.entrySet().stream()
                .map(l -> Arguments.of(f.getKey() + ", " + l.getKey(), f.getValue(), l.getValue())));
    }

    @Test
    public void missingRefs() {
        DataFrame df = TestXlsx.loadAndCompare(missingRefsXlsx(), ExcelLoader::new).get("Sheet1");
        new DataFrameAsserts(df, "A", "B", "C")
                .expectHeight(4)
                .expectRow(0, "a", 1., null)
                .expectRow(1, null, null, null)
                .expectRow(2, null, 2., 3.)
                .expectRow(3, 4., null, null);
    }

    @Test
    public void strings() {
        DataFrame df = TestXlsx.loadAndCompare(stringsXlsx(), ExcelLoader::new).get("Sheet1");
        new DataFrameAsserts(df, "A", "B", "C", "D", "E", "F", "G", "H", "I")
                .expectHeight(2)
                .expectRow(0, "plain", "rich", "a\rb", "kanji", "inline", "fx", "", "", "")
                .expectRow(1, "plain", null, null, null, null, null, null, null, null);

        // cells referencing the same shared string should share the String instance
        assertSame(df.getColumn(0).get(0), df.getColumn(0).get(1));
    }

    @Test
    public void formulas() {
        DataFrame df = TestXlsx.loadAndCompare(formulasXlsx(), ExcelLoader::new).get("Sheet1");
        new DataFrameAsserts(df, "A", "B", "C", "D", "E", "F", "G")
                .expectHeight(2)
                .expectRow(0, 2., 0., true, null, false, 4., LocalDateTime.of(2020, 1, 18, 0, 0))
                .expectRow(1, null, null, null, null, null, 6., null);
    }

    @Test
    public void dates() {
        DataFrame df = TestXlsx.loadAndCompare(datesXlsx(), ExcelLoader::new).get("Sheet1");
        new DataFrameAsserts(df, "A", "B", "C")
                .expectHeight(4)
                .expectRow(0, LocalDateTime.of(2020, 1, 18, 12, 0), LocalDateTime.of(2020, 1, 18, 0, 0), 43848.)
                .expectRow(1, LocalDateTime.of(2020, 1, 18, 0, 0), LocalDateTime.of(2020, 1, 19, 0, 0), -1.)
                .expectRow(2, 43848., 43848., null)
                .expectRow(3, 43848., null, null);
    }

    @Test
    public void dates1904() {
        DataFrame df = TestXlsx.loadAndCompare(dates1904Xlsx(), ExcelLoader::new).get("Sheet1");
        new DataFrameAsserts(df, "A")
                .expectHeight(1)
                .expectRow(0, LocalDateTime.of(1904, 1, 2, 0, 0));
    }

    @Test
    public void range() {
        DataFrame df = TestXlsx.loadAndCompare(rangeXlsx(), ExcelLoader::new).get("Sheet1");
        new DataFrameAsserts(df, "A", "B", "C", "D")
                .expectHeight(5)
                .expectRow(0, "a", null, null, null)
                .expectRow(1, null, null, null, null)
                .expectRow(2, null, 1., null, null)
                .expectRow(3, null, null, null, null)
                .expectRow(4, null, null, null, null);
    }

    @Test
    public void range_OffsetLimit() {
        DataFrame df = new ExcelLoader().offset(2).limit(1).load(new ByteArrayInputStream(rangeXlsx())).get("Sheet1");
        new DataFrameAsserts(df, "A", "B", "C", "D")
                .expectHeight(1)
                .expectRow(0, null, 1., null, null);
    }

    // rows and cells without the "r" references
    private static byte[] missingRefsXlsx() {
        return TestXlsx.of().sheet("Sheet1", """
                <row><c t="inlineStr"><is><t>a</t></is></c><c><v>1</v></c></row>
                <row r="3"><c r="B3"><v>2</v></c><c><v>3</v></c></row>
                <row><c><v>4</v></c></row>
                """).toBytes();
    }

    private static byte[] stringsXlsx() {
        return TestXlsx.of()
                .sharedString("<t>plain</t>")
                .sharedString("<r><t>ri</t></r><r><rPr><b/></rPr><t>ch</t></r>")
                .sharedString("<t>a_x000D_b</t>")
                .sharedString("<t>kanji</t><rPh sb=\"0\" eb=\"1\"><t>KANA</t></rPh>")
                .sheet("Sheet1", """
                        <row r="1">
                          <c r="A1" t="s"><v>0</v></c>
                          <c r="B1" t="s"><v>1</v></c>
                          <c r="C1" t="s"><v>2</v></c>
                          <c r="D1" t="s"><v>3</v></c>
                          <c r="E1" t="inlineStr"><is><r><t>in</t></r><r><t>line</t></r><rPh sb="0" eb="1"><t>X</t></rPh></is></c>
                          <c r="F1" t="str"><f>"f"&amp;"x"</f><v>fx</v></c>
                          <c r="G1" t="str"><f>""</f></c>
                          <c r="H1" t="s"/>
                          <c r="I1" t="s"><v>99</v></c>
                        </row>
                        <row r="2"><c r="A2" t="s"><v>0</v></c></row>
                        """)
                .toBytes();
    }

    private static byte[] formulasXlsx() {
        return TestXlsx.of().sheet("Sheet1", """
                <row r="1">
                  <c r="A1"><f>1+1</f><v>2</v></c>
                  <c r="B1"><f>1+1</f></c>
                  <c r="C1" t="b"><f>TRUE()</f><v>1</v></c>
                  <c r="D1" t="e"><f>1/0</f><v>#DIV/0!</v></c>
                  <c r="E1" t="b"><v>0</v></c>
                  <c r="F1"><f t="shared" ref="F1:F2" si="0">A1*2</f><v>4</v></c>
                  <c r="G1" s="%d"><f>DATE(2020,1,18)</f><v>43848</v></c>
                </row>
                <row r="2"><c r="F2"><f t="shared" si="0"/><v>6</v></c></row>
                """.formatted(STYLE_DATE)).toBytes();
    }

    // date detection via explicit cell styles, column default styles, row styles and the default style
    private static byte[] datesXlsx() {
        return TestXlsx.of().sheet("Sheet1",
                "<cols><col min=\"2\" max=\"2\" width=\"10\" customWidth=\"1\" style=\"" + STYLE_DATE + "\"/></cols>",
                """
                        <row r="1">
                          <c r="A1" s="%1$d"><v>43848.5</v></c>
                          <c r="B1"><v>43848</v></c>
                          <c r="C1" s="%3$d"><v>43848</v></c>
                        </row>
                        <row r="2" s="%2$d" customFormat="1">
                          <c r="A2"><v>43848</v></c>
                          <c r="B2"><v>43849</v></c>
                          <c r="C2" s="%2$d"><v>-1</v></c>
                        </row>
                        <row r="3" s="%3$d" customFormat="1">
                          <c r="A3"><v>43848</v></c>
                          <c r="B3" s="0"><v>43848</v></c>
                        </row>
                        <row r="4"><c r="A4" s="99"><v>43848</v></c></row>
                        """.formatted(STYLE_DATETIME, STYLE_DATE, STYLE_NUMBER)).toBytes();
    }

    private static byte[] dates1904Xlsx() {
        return TestXlsx.of().date1904().sheet("Sheet1", """
                <row r="1"><c r="A1" s="%d"><v>1</v></c></row>
                """.formatted(STYLE_DATE)).toBytes();
    }

    // blank styled cells and phantom rows
    private static byte[] rangeXlsx() {
        return TestXlsx.of().sheet("Sheet1", """
                <row r="1"><c r="A1" t="inlineStr"><is><t>a</t></is></c><c r="C1" s="%1$d"/></row>
                <row r="2" spans="1:3"/>
                <row r="3"><c r="B3"><v>1</v></c></row>
                <row r="5"><c r="D5" s="%1$d"/></row>
                <row r="6" spans="1:3"/>
                """.formatted(STYLE_NUMBER)).toBytes();
    }
}
