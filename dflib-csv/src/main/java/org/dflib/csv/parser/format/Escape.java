package org.dflib.csv.parser.format;

/**
 * Special chars escape mode.
 * <p>
 * For quoted fields, quote chars are escaped according to the selected mode.
 * For unquoted fields, {@link #BACKSLASH} and {@link #CUSTOM} allow escaping delimiters.
 * <p>
 * Examples:<br/>
 * <pre>
 * # quoted field, DOUBLE mode:
 * "1","Project ""DFLib"""
 * # quoted field, BACKSLASH mode:
 * "1","Project \"DFLib\""
 * # unquoted field, BACKSLASH mode (tab-delimited):
 * 1	Project\	DFLib
 * # quoted field, CUSTOM mode ('$')
 * "1","Project $"DFLib$""
 * </pre>
 *
 * @see CsvFormat.Builder#escape(Escape)
 * @see CsvFormat.Builder#escape(char)
 *
 * @since 2.0.0
 */
public enum Escape {
    /**
     * No escaping
     */
    NONE,

    /**
     * Double quotes are considered escape, default mode
     */
    DOUBLE,

    /**
     * Backslash `\` char escapes special chars
     */
    BACKSLASH,

    /**
     * Custom char used for escaping
     */
    CUSTOM
}
