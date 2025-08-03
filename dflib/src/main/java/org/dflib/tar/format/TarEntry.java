package org.dflib.tar.format;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @since 2.0.0
 */
public class TarEntry {

    // Indicates unknown mode, user/groupids, device numbers and modTime when parsing a file in lenient
    //  mode, and the archive contains illegal fields.
    private static final long UNKNOWN = -1L;

    // Strips Windows' drive letter as well as any leading slashes, turns path separators into forward slashes.
    private static String normalizeFileName(String fileName) {

        String property = System.getProperty("os.name");
        if (property != null) {
            String osName = property.toLowerCase(Locale.ROOT);

            // Strip off drive letters! Would a better check be "(File.separator == '\')"?
            if (osName.startsWith("windows")) {
                if (fileName.length() > 2) {
                    char ch1 = fileName.charAt(0);
                    char ch2 = fileName.charAt(1);

                    if (ch2 == ':' && (ch1 >= 'a' && ch1 <= 'z' || ch1 >= 'A' && ch1 <= 'Z')) {
                        fileName = fileName.substring(2);
                    }
                }
            } else if (osName.contains("netware")) {
                final int colon = fileName.indexOf(':');
                if (colon != -1) {
                    fileName = fileName.substring(colon + 1);
                }
            }
        }

        fileName = fileName.replace(File.separatorChar, '/');

        // No absolute pathnames. Windows (and Posix?) paths can start with "\\NetworkDrive\", so we loop on starting /'s.
        while (fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }
        return fileName;
    }

    private String name;
    private int mode;
    private long size;
    private byte linkFlag;
    private String linkName;
    private List<TarStructSparse> sparseHeaders;
    // If an extension sparse header follows.
    private boolean extended;
    private long realSize;
    // Is this entry a GNU sparse entry using one of the PAX formats?
    private boolean paxGNUSparse;
    // Is this entry a GNU sparse entry using 1.X PAX formats? the sparse headers of 1.x PAX Format is stored in file data block
    private boolean paxGNU1XSparse;
    // Is this entry a star sparse entry using the PAX header?
    private boolean starSparse;
    private long dataOffset;

    TarEntry(
            String name,
            long size,
            long realSize,
            long dataOffset,
            int mode,
            boolean extended,
            byte linkFlag,
            String linkName,
            boolean paxGNU1XSparse,
            boolean paxGNUSparse,
            boolean starSparse,
            List<TarStructSparse> sparseHeaders) {

        this.extended = extended;
        this.linkFlag = linkFlag;
        this.linkName = linkName;
        this.mode = mode;
        this.name = name;
        this.paxGNU1XSparse = paxGNU1XSparse;
        this.paxGNUSparse = paxGNUSparse;
        this.realSize = realSize;
        this.size = size;
        this.sparseHeaders = sparseHeaders;
        this.starSparse = starSparse;
        this.dataOffset = dataOffset;
    }

    TarEntry(
            Map<String, String> globalPaxHeaders,
            byte[] headerBuf,
            ZipEncoding encoding,
            boolean lenient) throws IOException {

        this.dataOffset = -1;
        this.name = "";
        this.linkName = "";
        parseTarHeader(globalPaxHeaders, headerBuf, encoding, lenient);
    }

    TarEntry(
            Map<String, String> globalPaxHeaders,
            byte[] headerBuf,
            ZipEncoding encoding,
            boolean lenient,
            long dataOffset) throws IOException {

        this(globalPaxHeaders, headerBuf, encoding, lenient);
        setDataOffset(dataOffset);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return getName().equals(((TarEntry) o).getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    /**
     * Evaluates an entry's header format from a header buffer.
     */
    private int evaluateType(Map<String, String> globalPaxHeaders, byte[] header) {

        if (TarUtils.matchAsciiBuffer(TarConstants.MAGIC_GNU, header, TarConstants.MAGIC_OFFSET, TarConstants.MAGICLEN)) {
            return TarConstants.FORMAT_OLDGNU;
        }

        if (TarUtils.matchAsciiBuffer(TarConstants.MAGIC_POSIX, header, TarConstants.MAGIC_OFFSET, TarConstants.MAGICLEN)) {
            if (isXstar(globalPaxHeaders, header)) {
                return TarConstants.FORMAT_XSTAR;
            }
            return TarConstants.FORMAT_POSIX;
        }
        return 0;
    }

    void fillGNUSparse0xData(Map<String, String> headers) {
        paxGNUSparse = true;
        realSize = TarUtils.parseIntValue(headers.get(TarGnuSparseKeys.SIZE));
        if (headers.containsKey(TarGnuSparseKeys.NAME)) {
            // version 0.1
            name = headers.get(TarGnuSparseKeys.NAME);
        }
    }

    private void fillGNUSparse1xData(Map<String, String> headers) {
        paxGNUSparse = true;
        paxGNU1XSparse = true;
        if (headers.containsKey(TarGnuSparseKeys.NAME)) {
            name = headers.get(TarGnuSparseKeys.NAME);
        }
        if (headers.containsKey(TarGnuSparseKeys.REALSIZE)) {
            realSize = TarUtils.parseIntValue(headers.get(TarGnuSparseKeys.REALSIZE));
        }
    }

    private void fillStarSparseData(Map<String, String> headers) {
        starSparse = true;
        if (headers.containsKey("SCHILY.realsize")) {
            realSize = TarUtils.parseLongValue(headers.get("SCHILY.realsize"));
        }
    }

    long getDataOffset() {
        return dataOffset;
    }

    public byte getLinkFlag() {
        return linkFlag;
    }

    public String getLinkName() {
        return linkName;
    }

    public int getMode() {
        return mode;
    }

    /**
     * Gets this entry's name. Returns the raw name as it is stored inside the archive.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets this entry's sparse headers ordered by offset with all empty sparse sections at the start filtered out.
     */
    List<TarStructSparse> getOrderedSparseHeaders() throws IOException {
        if (sparseHeaders == null || sparseHeaders.isEmpty()) {
            return Collections.emptyList();
        }
        List<TarStructSparse> orderedAndFiltered = sparseHeaders.stream().filter(s -> s.getOffset() > 0 || s.getNumbytes() > 0)
                .sorted(Comparator.comparingLong(TarStructSparse::getOffset)).collect(Collectors.toList());
        int numberOfHeaders = orderedAndFiltered.size();
        for (int i = 0; i < numberOfHeaders; i++) {
            TarStructSparse str = orderedAndFiltered.get(i);
            if (i + 1 < numberOfHeaders && str.getOffset() + str.getNumbytes() > orderedAndFiltered.get(i + 1).getOffset()) {
                throw new IOException("Corrupted TAR archive. Sparse blocks for " + getName() + " overlap each other.");
            }
            if (str.getOffset() + str.getNumbytes() < 0) {
                // integer overflow?
                throw new IOException("Unreadable TAR archive. Offset and numbytes for sparse block in " + getName() + " too large.");
            }
        }
        if (!orderedAndFiltered.isEmpty()) {
            TarStructSparse last = orderedAndFiltered.get(numberOfHeaders - 1);
            if (last.getOffset() + last.getNumbytes() > getRealSize()) {
                throw new IOException("Corrupted TAR archive. Sparse block extends beyond real size of the entry");
            }
        }
        return orderedAndFiltered;
    }

    /**
     * Gets this entry's real file size in case of a sparse file. This is the size a file would take on disk if the
     * entry was expanded. If the file is not a sparse file, return size instead of realSize.
     */
    public long getRealSize() {
        return isSparse() ? realSize : size;
    }

    /**
     * Gets this entry's file size.
     * <p>
     * This is the size the entry's data uses inside the archive. Usually this is the same as {@link #getRealSize}, but it doesn't take the "holes" into account
     * when the entry represents a sparse file.
     */
    public long getSize() {
        return size;
    }

    public List<TarStructSparse> getSparseHeaders() {
        return sparseHeaders;
    }

    /**
     * Tests whether or not this entry represents a directory.
     *
     * @return True if this entry is a directory.
     */
    public boolean isDirectory() {

        if (linkFlag == TarConstants.LF_DIR) {
            return true;
        }
        return !isPaxHeader() && !isGlobalPaxHeader() && getName().endsWith("/");
    }

    /**
     * Tests whether in case of an oldgnu sparse file if an extension sparse header follows.
     *
     * @return true if an extension oldgnu sparse header follows.
     */
    public boolean isExtended() {
        return extended;
    }

    /**
     * Tests whether this is a FIFO (pipe) entry.
     */
    public boolean isFifo() {
        return linkFlag == TarConstants.LF_FIFO;
    }

    /**
     * Tests whether this is a "normal file"
     */
    public boolean isFile() {
        if (linkFlag == TarConstants.LF_OLDNORM || linkFlag == TarConstants.LF_NORMAL) {
            return true;
        }
        return linkFlag != TarConstants.LF_DIR && !getName().endsWith("/");
    }

    /**
     * Tests whether this is a Pax header.
     */
    public boolean isGlobalPaxHeader() {
        return linkFlag == TarConstants.LF_PAX_GLOBAL_EXTENDED_HEADER;
    }

    /**
     * Tests whether this entry is a GNU long linkname block
     *
     * @return true if this is a long name extension provided by GNU tar
     */
    public boolean isGnuLongLink() {
        return linkFlag == TarConstants.LF_GNUTYPE_LONGLINK;
    }

    /**
     * Tests whether this entry is a GNU long name block
     *
     * @return true if this is a long name extension provided by GNU tar
     */
    public boolean isGnuLongName() {
        return linkFlag == TarConstants.LF_GNUTYPE_LONGNAME;
    }

    /**
     * Tests whether this entry is a GNU sparse block.
     */
    public boolean isGnuSparse() {
        return isOldGnuSparse() || isPaxGnuSparse();
    }

    private boolean isValidPrefix(byte[] header) {

        // prefix[130] is guaranteed to be '\0' with XSTAR/XUSTAR
        if (header[TarConstants.XSTAR_PREFIX_OFFSET + 130] != 0) {

            // except when typeflag is 'M'
            if (header[TarConstants.LF_OFFSET] != TarConstants.LF_MULTIVOLUME) {
                return false;
            }

            // We come only here if we try to read in a GNU/xstar/xustar multivolume archive starting past volume #0
            // As of 1.22, commons-compress does not support multivolume tar archives.
            // If/when it does, this should work as intended.
            return (header[TarConstants.XSTAR_MULTIVOLUME_OFFSET] & 0x80) != 0
                    || header[TarConstants.XSTAR_MULTIVOLUME_OFFSET + 11] == ' ';
        }
        return true;
    }

    private boolean isValidXtarTime(byte[] buffer, int offset, int length) {
        // If atime[0]...atime[10] or ctime[0]...ctime[10] is not a POSIX octal number it cannot be 'xstar'.
        if ((buffer[offset] & 0x80) == 0) {
            final int lastIndex = length - 1;
            for (int i = 0; i < lastIndex; i++) {
                final byte b = buffer[offset + i];
                if (b < '0' || b > '7') {
                    return false;
                }
            }
            // Check for both POSIX compliant end of number characters if not using base 256
            byte b = buffer[offset + lastIndex];
            return b == ' ' || b == 0;
        }
        return true;
    }

    /**
     * Tests whether this is a link entry.
     */
    public boolean isLink() {
        return linkFlag == TarConstants.LF_LINK;
    }

    /**
     * Tests whether this entry is a GNU or star sparse block using the oldgnu format.
     */
    public boolean isOldGnuSparse() {
        return linkFlag == TarConstants.LF_GNUTYPE_SPARSE;
    }

    /**
     * Tests whether this entry is a sparse file with 1.X PAX Format or not
     */
    public boolean isPaxGnu1XSparse() {
        return paxGNU1XSparse;
    }

    /**
     * Tests whether this entry is a GNU sparse block using one of the PAX formats.
     */
    public boolean isPaxGnuSparse() {
        return paxGNUSparse;
    }

    /**
     * Tests whether this is a Pax header.
     */
    public boolean isPaxHeader() {
        return linkFlag == TarConstants.LF_PAX_EXTENDED_HEADER_LC || linkFlag == TarConstants.LF_PAX_EXTENDED_HEADER_UC;
    }

    /**
     * Tests whether this is a sparse entry.
     */
    public boolean isSparse() {
        return isGnuSparse() || isStarSparse();
    }

    /**
     * Tests whether this entry is a star sparse block using PAX headers.
     */
    public boolean isStarSparse() {
        return starSparse;
    }

    /**
     * Tests whether this is a symbolic link entry.
     */
    public boolean isSymbolicLink() {
        return linkFlag == TarConstants.LF_SYMLINK;
    }

    /**
     * Tests whether the <a href="https://www.mkssoftware.com/docs/man4/tar.4.asp">type flag</a> contains a valid USTAR value.
     *
     * <pre>
     * Type Flag    File Type
     * 0 or null    Regular file
     * 1            Link to another file already archived
     * 2            Symbolic link
     * 3            Character special device
     * 4            Block special device
     * 5            Directory
     * 6            FIFO special file
     * 7            Reserved
     * A-Z          Available for custom usage
     * </pre>
     */
    boolean isTypeFlagUstar() {
        return linkFlag == 0 || linkFlag >= '0' && linkFlag <= '7' || linkFlag >= 'A' && linkFlag <= 'Z';
    }

    /**
     * Tests whether the given header is in XSTAR / XUSTAR format.
     * <p>
     * Use the same logic found in star version 1.6 in {@code header.c}, function {@code isxmagic(TCB *ptb)}.
     */
    private boolean isXstar(Map<String, String> globalPaxHeaders, byte[] header) {
        // Check if this is XSTAR
        if (TarUtils.matchAsciiBuffer(TarConstants.MAGIC_XSTAR, header, TarConstants.XSTAR_MAGIC_OFFSET, TarConstants.XSTAR_MAGIC_LEN)) {
            return true;
        }
        //
        // If SCHILY.archtype is present in the global PAX header, we can use it to identify the type of archive.
        //
        // Possible values for XSTAR: - xustar: 'xstar' format without "tar" signature at header offset 508. - exustar: 'xustar' format variant that always
        // includes x-headers and g-headers.
        //
        final String archType = globalPaxHeaders.get("SCHILY.archtype");
        if (archType != null) {
            return "xustar".equals(archType) || "exustar".equals(archType);
        }

        // Check if this is XUSTAR
        return isValidPrefix(header)
                && isValidXtarTime(header, TarConstants.XSTAR_ATIME_OFFSET, TarConstants.ATIMELEN_XSTAR)
                && isValidXtarTime(header, TarConstants.XSTAR_CTIME_OFFSET, TarConstants.CTIMELEN_XSTAR);
    }

    private long parseOctalOrBinary(byte[] header, int offset, int length, boolean lenient) {
        if (lenient) {
            try {
                return TarUtils.parseOctalOrBinary(header, offset, length);
            } catch (IllegalArgumentException ex) { // NOSONAR
                return UNKNOWN;
            }
        }
        return TarUtils.parseOctalOrBinary(header, offset, length);
    }

    private void parseTarHeader(Map<String, String> globalPaxHeaders, byte[] header, ZipEncoding encoding, boolean lenient) throws IOException {
        try {
            parseUstarHeaderBlock(globalPaxHeaders, header, encoding, lenient);
        } catch (IllegalArgumentException ex) {
            throw new IOException("Corrupted TAR archive.", ex);
        }
    }

    private int parseTarHeaderBlock(byte[] header, ZipEncoding encoding, boolean lenient, int offset)
            throws IOException {
        name = TarUtils.parseName(header, offset, TarConstants.NAMELEN, encoding);
        offset += TarConstants.NAMELEN;
        mode = (int) parseOctalOrBinary(header, offset, TarConstants.MODELEN, lenient);
        offset += TarConstants.MODELEN;
        offset += TarConstants.UIDLEN;
        offset += TarConstants.GIDLEN;
        size = TarUtils.parseOctalOrBinary(header, offset, TarConstants.SIZELEN);
        if (size < 0) {
            throw new IOException("broken archive, entry with negative size");
        }
        offset += TarConstants.SIZELEN;
        offset += TarConstants.MODTIMELEN;
        offset += TarConstants.CHKSUMLEN;
        linkFlag = header[offset++];
        linkName = TarUtils.parseName(header, offset, TarConstants.NAMELEN, encoding);
        return offset;
    }

    private void parseUstarHeaderBlock(Map<String, String> globalPaxHeaders, byte[] header, ZipEncoding encoding, boolean lenient) throws IOException {
        int offset = 0;
        offset = parseTarHeaderBlock(header, encoding, lenient, offset);
        offset += TarConstants.NAMELEN;
        offset += TarConstants.MAGICLEN;
        offset += TarConstants.VERSIONLEN;
        offset += TarConstants.UNAMELEN;
        offset += TarConstants.GNAMELEN;
        if (linkFlag == TarConstants.LF_CHR || linkFlag == TarConstants.LF_BLK) {
            offset += TarConstants.DEVLEN;
            offset += TarConstants.DEVLEN;
        } else {
            offset += 2 * TarConstants.DEVLEN;
        }
        final int type = evaluateType(globalPaxHeaders, header);
        switch (type) {
            case TarConstants.FORMAT_OLDGNU: {
                offset += TarConstants.ATIMELEN_GNU;
                offset += TarConstants.CTIMELEN_GNU;
                offset += TarConstants.OFFSETLEN_GNU;
                offset += TarConstants.LONGNAMESLEN_GNU;
                offset += TarConstants.PAD2LEN_GNU;
                sparseHeaders = new ArrayList<>(TarUtils.readSparseStructs(header, offset, TarConstants.SPARSE_HEADERS_IN_OLDGNU_HEADER));
                offset += TarConstants.SPARSELEN_GNU;
                extended = TarUtils.parseBoolean(header, offset);
                offset += TarConstants.ISEXTENDEDLEN_GNU;
                realSize = TarUtils.parseOctal(header, offset, TarConstants.REALSIZELEN_GNU);
                offset += TarConstants.REALSIZELEN_GNU; // NOSONAR - assignment as documentation
                break;
            }
            case TarConstants.FORMAT_XSTAR: {
                String xstarPrefix = TarUtils.parseName(header, offset, TarConstants.PREFIXLEN_XSTAR, encoding);
                offset += TarConstants.PREFIXLEN_XSTAR;
                if (!xstarPrefix.isEmpty()) {
                    name = xstarPrefix + "/" + name;
                }
                offset += TarConstants.ATIMELEN_XSTAR;
                offset += TarConstants.CTIMELEN_XSTAR; // NOSONAR - assignment as documentation
                break;
            }
            case TarConstants.FORMAT_POSIX:
            default: {
                String prefix = TarUtils.parseName(header, offset, TarConstants.PREFIXLEN, encoding);
                offset += TarConstants.PREFIXLEN; // NOSONAR - assignment as documentation
                // SunOS tar -E does not add / to directory names, so fix up to be consistent
                if (isDirectory() && !name.endsWith("/")) {
                    name += "/";
                }
                if (!prefix.isEmpty()) {
                    name = prefix + "/" + name;
                }
            }
        }
    }

    /**
     * Processes one pax header, using the supplied map as source for extra headers to be used when handling entries for sparse files
     */
    private void processPaxHeader(String key, String val, Map<String, String> headers) throws IOException {
        /*
         * The following headers are defined for Pax. charset: cannot use these without changing TarEntry fields mtime atime ctime
         * LIBARCHIVE.creationtime comment gid, gname linkpath size uid,uname SCHILY.devminor, SCHILY.devmajor: don't have setters/getters for those
         *
         * GNU sparse files use additional members, we use GNU.sparse.size to detect the 0.0 and 0.1 versions and GNU.sparse.realsize for 1.0.
         *
         * star files use additional members of which we use SCHILY.filetype in order to detect star sparse files.
         *
         * If called from addExtraPaxHeader, these additional headers must be already present.
         */
        switch (key) {
            case "path":
                setName(val);
                break;
            case "linkpath":
                setLinkName(val);
                break;
            case "size":
                long size = TarUtils.parseLongValue(val);
                if (size < 0) {
                    throw new IOException("Corrupted TAR archive. Entry size is negative");
                }
                this.size = size;
                break;
            case TarGnuSparseKeys.SIZE:
                fillGNUSparse0xData(headers);
                break;
            case TarGnuSparseKeys.REALSIZE:
                fillGNUSparse1xData(headers);
                break;
            case "SCHILY.filetype":
                if ("sparse".equals(val)) {
                    fillStarSparseData(headers);
                }
                break;

            // ignore a bunch of properties. Don't read them, don't validate
            case "mtime":
            case "atime":
            case "ctime":
            case "uid":
            case "uname":
            case "gid":
            case "gname":
            case "SCHILY.devminor":
            case "SCHILY.devmajor":
                break;
        }
    }

    /**
     * Sets the offset of the data for the tar entry.
     */
    void setDataOffset(long dataOffset) {
        if (dataOffset < 0) {
            throw new IllegalArgumentException("The offset cannot be smaller than 0");
        }
        this.dataOffset = dataOffset;
    }

    void setLinkName(String link) {
        this.linkName = link;
    }

    void setName(String name) {
        this.name = TarEntry.normalizeFileName(name);
    }

    void setSparseHeaders(List<TarStructSparse> sparseHeaders) {
        this.sparseHeaders = sparseHeaders;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Update the entry using a map of pax headers.
     */
    void updateEntryFromPaxHeaders(Map<String, String> headers) throws IOException {
        for (Map.Entry<String, String> ent : headers.entrySet()) {
            processPaxHeader(ent.getKey(), ent.getValue(), headers);
        }
    }
}
