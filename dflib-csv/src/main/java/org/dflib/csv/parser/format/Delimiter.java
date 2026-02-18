package org.dflib.csv.parser.format;

/**
 * CSV field delimiter wrapper.
 *
 * @since 2.0.0
 */
public class Delimiter {

    final String delimiter;

    Delimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Returns whether delimiter is a single character.
     */
    public boolean isSingleChar() {
        return delimiter.length() == 1;
    }

    /**
     * Returns delimiter as a single character.
     */
    public char singleChar() {
        return delimiter.charAt(0);
    }

    /**
     * Returns delimiter as a character array.
     */
    public char[] asArray() {
        return delimiter.toCharArray();
    }
}
