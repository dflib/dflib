package org.dflib.excel;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.dflib.DataFrame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Builds minimal XLSX files from raw sheet XML, and compares the streaming XLSX reader with POI usermodel.
 */
class TestXlsx {

    private static final String NS = "http://schemas.openxmlformats.org/spreadsheetml/2006/main";
    private static final String REL_NS = "http://schemas.openxmlformats.org/officeDocument/2006/relationships";

    // cell styles: 0 - General, 1 - built-in date format 14, 2 - custom date-time format, 3 - number format
    static final int STYLE_DATE = 1;
    static final int STYLE_DATETIME = 2;
    static final int STYLE_NUMBER = 3;

    private static final String STYLES = """
            <styleSheet xmlns="%s">
              <numFmts count="2">
                <numFmt numFmtId="164" formatCode="yyyy-mm-dd hh:mm"/>
                <numFmt numFmtId="165" formatCode="0.000"/>
              </numFmts>
              <fonts count="1"><font><sz val="11"/><name val="Calibri"/></font></fonts>
              <fills count="2"><fill><patternFill patternType="none"/></fill><fill><patternFill patternType="gray125"/></fill></fills>
              <borders count="1"><border><left/><right/><top/><bottom/><diagonal/></border></borders>
              <cellStyleXfs count="1"><xf numFmtId="0" fontId="0" fillId="0" borderId="0"/></cellStyleXfs>
              <cellXfs count="4">
                <xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/>
                <xf numFmtId="14" fontId="0" fillId="0" borderId="0" xfId="0" applyNumberFormat="1"/>
                <xf numFmtId="164" fontId="0" fillId="0" borderId="0" xfId="0" applyNumberFormat="1"/>
                <xf numFmtId="165" fontId="0" fillId="0" borderId="0" xfId="0" applyNumberFormat="1"/>
              </cellXfs>
            </styleSheet>
            """.formatted(NS);

    private final List<String> sheetNames;
    private final List<String> sheetXmls;
    private final List<String> sharedStrings;
    private boolean date1904;

    static TestXlsx of() {
        return new TestXlsx();
    }

    private TestXlsx() {
        this.sheetNames = new ArrayList<>();
        this.sheetXmls = new ArrayList<>();
        this.sharedStrings = new ArrayList<>();
    }

    TestXlsx date1904() {
        this.date1904 = true;
        return this;
    }

    /**
     * Adds raw "si" element contents to the shared strings table.
     */
    TestXlsx sharedString(String siContents) {
        sharedStrings.add(siContents);
        return this;
    }

    TestXlsx sheet(String name, String sheetData) {
        return sheet(name, "", sheetData);
    }

    /**
     * @param beforeSheetData raw XML to place in the "worksheet" element before "sheetData" (e.g. "cols")
     * @param sheetData       raw XML of the "sheetData" element contents
     */
    TestXlsx sheet(String name, String beforeSheetData, String sheetData) {
        sheetNames.add(name);
        sheetXmls.add("<worksheet xmlns=\"" + NS + "\">" + beforeSheetData + "<sheetData>" + sheetData + "</sheetData></worksheet>");
        return this;
    }

    byte[] toBytes() {

        StringBuilder contentTypes = new StringBuilder("""
                <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
                <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
                <Default Extension="xml" ContentType="application/xml"/>
                <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
                <Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
                <Override PartName="/xl/sharedStrings.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sharedStrings+xml"/>
                """);

        StringBuilder workbook = new StringBuilder("<workbook xmlns=\"" + NS + "\" xmlns:r=\"" + REL_NS + "\">");
        if (date1904) {
            workbook.append("<workbookPr date1904=\"1\"/>");
        }
        workbook.append("<sheets>");

        StringBuilder workbookRels = new StringBuilder("""
                <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                <Relationship Id="rIdStyles" Type="%s/styles" Target="styles.xml"/>
                <Relationship Id="rIdStrings" Type="%s/sharedStrings" Target="sharedStrings.xml"/>
                """.formatted(REL_NS, REL_NS));

        for (int i = 0; i < sheetNames.size(); i++) {
            int n = i + 1;
            contentTypes.append("<Override PartName=\"/xl/worksheets/sheet").append(n)
                    .append(".xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>");
            workbook.append("<sheet name=\"").append(sheetNames.get(i)).append("\" sheetId=\"").append(n)
                    .append("\" r:id=\"rId").append(n).append("\"/>");
            workbookRels.append("<Relationship Id=\"rId").append(n).append("\" Type=\"").append(REL_NS)
                    .append("/worksheet\" Target=\"worksheets/sheet").append(n).append(".xml\"/>");
        }

        contentTypes.append("</Types>");
        workbook.append("</sheets></workbook>");
        workbookRels.append("</Relationships>");

        StringBuilder sst = new StringBuilder("<sst xmlns=\"" + NS + "\" count=\"" + sharedStrings.size()
                + "\" uniqueCount=\"" + sharedStrings.size() + "\">");
        for (String si : sharedStrings) {
            sst.append("<si>").append(si).append("</si>");
        }
        sst.append("</sst>");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(out)) {
            write(zip, "[Content_Types].xml", contentTypes.toString());
            write(zip, "_rels/.rels", """
                    <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                    <Relationship Id="rId1" Type="%s/officeDocument" Target="xl/workbook.xml"/>
                    </Relationships>
                    """.formatted(REL_NS));
            write(zip, "xl/workbook.xml", workbook.toString());
            write(zip, "xl/_rels/workbook.xml.rels", workbookRels.toString());
            write(zip, "xl/styles.xml", STYLES);
            write(zip, "xl/sharedStrings.xml", sst.toString());

            for (int i = 0; i < sheetXmls.size(); i++) {
                write(zip, "xl/worksheets/sheet" + (i + 1) + ".xml", sheetXmls.get(i));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return out.toByteArray();
    }

    private static void write(ZipOutputStream zip, String name, String content) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    /**
     * Loads XLSX bytes with the streaming reader and with POI usermodel, asserting that the results are identical.
     * Returns the streaming reader result.
     */
    static Map<String, DataFrame> loadAndCompare(byte[] xlsx, Supplier<ExcelLoader> loaderFactory) {

        Map<String, DataFrame> streamed = loaderFactory.get().load(new ByteArrayInputStream(xlsx));

        Map<String, DataFrame> poi;
        try (Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(xlsx))) {
            poi = loaderFactory.get().load(wb);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        assertSameData(poi, streamed);
        return streamed;
    }

    static void assertSameData(Map<String, DataFrame> expected, Map<String, DataFrame> actual) {
        assertEquals(new ArrayList<>(expected.keySet()), new ArrayList<>(actual.keySet()), "Sheet names differ");
        for (Map.Entry<String, DataFrame> e : expected.entrySet()) {
            assertSameData(e.getKey(), e.getValue(), actual.get(e.getKey()));
        }
    }

    static void assertSameData(String label, DataFrame expected, DataFrame actual) {

        assertArrayEquals(expected.getColumnsIndex().toArray(), actual.getColumnsIndex().toArray(), label + ": columns differ");
        assertEquals(expected.height(), actual.height(), label + ": height differs");

        int w = expected.width();
        int h = expected.height();
        for (int c = 0; c < w; c++) {
            for (int r = 0; r < h; r++) {
                assertEquals(
                        expected.getColumn(c).get(r),
                        actual.getColumn(c).get(r),
                        label + ": value differs at row " + r + ", column " + expected.getColumnsIndex().get(c));
            }
        }
    }
}
