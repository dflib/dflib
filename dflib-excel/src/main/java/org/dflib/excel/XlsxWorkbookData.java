package org.dflib.excel;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A streaming XLSX reader. Unlike POI XSSFWorkbook, only parses the requested sheets and only extracts the cached cell
 * values, without building an in-memory model of the sheet XML.
 */
class XlsxWorkbookData implements WorkbookData {

    private final OPCPackage pkg;
    private final XSSFReader reader;
    private final XSSFReader.SheetIterator sheetIterator;
    private final List<XSSFReader.XSSFSheetRef> sheets;

    // lazily initialized when the first sheet is read
    private boolean initialized;
    private StylesTable styles;
    private int numCellStyles;
    private Boolean[] dateStyles;
    private ReadOnlySharedStringsTable sharedStrings;
    private String[] sharedStringsCache;
    private boolean date1904;

    static XlsxWorkbookData open(File file) throws IOException {

        OPCPackage pkg;
        try {
            // opening in the READ mode, so that the source file is never modified
            pkg = OPCPackage.open(file, PackageAccess.READ);
        } catch (OpenXML4JException e) {
            throw new IOException(e);
        }

        return open(pkg);
    }

    static XlsxWorkbookData open(InputStream in) throws IOException {

        OPCPackage pkg;
        try {
            pkg = OPCPackage.open(in);
        } catch (OpenXML4JException e) {
            throw new IOException(e);
        }

        return open(pkg);
    }

    private static XlsxWorkbookData open(OPCPackage pkg) throws IOException {
        try {
            XSSFReader reader = new XSSFReader(pkg);
            return new XlsxWorkbookData(pkg, reader, reader.getSheetIterator());
        } catch (OpenXML4JException | IOException | RuntimeException e) {
            pkg.revert();
            throw e instanceof IOException ioe ? ioe : new IOException(e);
        }
    }

    private XlsxWorkbookData(OPCPackage pkg, XSSFReader reader, XSSFReader.SheetIterator sheetIterator) {
        this.pkg = pkg;
        this.reader = reader;
        this.sheetIterator = sheetIterator;

        // match XSSFWorkbook behavior, skipping sheets declared in the workbook, but missing in the package
        List<XSSFReader.XSSFSheetRef> sheets = new ArrayList<>();
        for (XSSFReader.XSSFSheetRef ref : sheetIterator.getSheetRefs()) {
            if (sheetIterator.getSheetPart(ref) != null) {
                sheets.add(ref);
            }
        }

        this.sheets = sheets;
    }

    @Override
    public int sheetCount() {
        return sheets.size();
    }

    @Override
    public String sheetName(int index) {
        return sheets.get(index).getName();
    }

    @Override
    public int sheetIndex(String name) {
        int len = sheets.size();
        for (int i = 0; i < len; i++) {
            if (sheets.get(i).getName().equalsIgnoreCase(name)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public SheetData sheet(int index, int rowOffset, int rowLimit) {

        initialize();

        XSSFReader.XSSFSheetRef ref = sheets.get(index);
        PackagePart part = sheetIterator.getSheetPart(ref);
        XlsxSheetHandler handler = new XlsxSheetHandler(this, rowOffset, rowLimit);

        try (InputStream in = part.getInputStream()) {
            parse(in, handler);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new RuntimeException("Error reading Excel sheet '" + ref.getName() + "'", e);
        }

        return handler.sheetData();
    }

    @Override
    public void close() {
        // READ mode or stream-backed, so nothing to save
        pkg.revert();
    }

    boolean date1904() {
        return date1904;
    }

    int numCellStyles() {
        return numCellStyles;
    }

    /**
     * Returns whether a style at the index (within [0, numCellStyles)) has a date number format.
     */
    boolean isDateStyle(int styleIndex) {

        Boolean cached = dateStyles[styleIndex];
        if (cached != null) {
            return cached;
        }

        XSSFCellStyle style = styles.getStyleAt(styleIndex);
        boolean isDate = style != null && DateUtil.isADateFormat(style.getDataFormat(), style.getDataFormatString());
        dateStyles[styleIndex] = isDate;
        return isDate;
    }

    /**
     * Resolves a shared string by its index. Each string is resolved once, so the cells referencing it share the same
     * String instance.
     */
    String sharedString(String indexString) {

        int index;
        String value;

        // like XSSFCell, treating invalid references as empty strings
        try {
            index = Integer.parseInt(indexString);

            if (index < sharedStringsCache.length) {
                value = sharedStringsCache[index];
                if (value != null) {
                    return value;
                }
            }

            value = sharedStrings.getItemAt(index).getString();
        } catch (RuntimeException e) {
            return "";
        }

        // "index" is now known to be valid, so it is safe to grow the cache to fit it
        if (index >= sharedStringsCache.length) {
            sharedStringsCache = Arrays.copyOf(sharedStringsCache, Math.max(index + 1, sharedStringsCache.length * 2));
        }

        sharedStringsCache[index] = value;
        return value;
    }

    private void initialize() {

        if (initialized) {
            return;
        }

        try {
            this.styles = reader.getStylesTable();
            this.sharedStrings = new ReadOnlySharedStringsTable(pkg, false);
            this.date1904 = readDate1904();
        } catch (IOException | SAXException | ParserConfigurationException | OpenXML4JException e) {
            throw new RuntimeException("Error reading Excel workbook", e);
        }

        this.numCellStyles = styles != null ? styles.getNumCellStyles() : 0;
        this.dateStyles = new Boolean[numCellStyles];

        // "uniqueCount" comes from the file and may be absent or wrong, so only using it as a capacity hint
        this.sharedStringsCache = new String[Math.max(16, Math.min(sharedStrings.getUniqueCount(), 1 << 16))];

        this.initialized = true;
    }

    private boolean readDate1904() throws IOException, SAXException, ParserConfigurationException, OpenXML4JException {

        boolean[] date1904 = new boolean[1];
        DefaultHandler handler = new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) {
                if ("workbookPr".equals(localName)) {
                    String value = attributes.getValue("date1904");
                    date1904[0] = "1".equals(value) || "true".equals(value);
                }
            }
        };

        try (InputStream in = reader.getWorkbookData()) {
            parse(in, handler);
        }

        return date1904[0];
    }

    private static void parse(InputStream in, ContentHandler handler)
            throws IOException, SAXException, ParserConfigurationException {
        XMLReader xmlReader = XMLHelper.newXMLReader();
        xmlReader.setContentHandler(handler);
        xmlReader.parse(new InputSource(in));
    }
}
