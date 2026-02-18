package org.dflib.csv.parser.format;

/**
 * Supported CSV record separators.
 * @since 2.0.0
 */
public enum LineBreak {
    AUTO, // any line break
    CR,   // \r
    LF,   // \n
    CRLF, // \r\n
}
