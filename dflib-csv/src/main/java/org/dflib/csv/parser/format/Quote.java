package org.dflib.csv.parser.format;

/**
 * CSV quote configuration.
 * @since 2.0.0
 */
public class Quote {

    final char quote;
    final boolean optional;

    /**
     * Returns quote settings with quoting disabled.
     */
    public static Quote none() {
        return new Quote((char)0, false);
    }

    /**
     * Returns optional quote settings with the given quote character.
     */
    public static Quote optionalOf(char quote) {
        return new Quote(quote, true);
    }

    /**
     * Returns mandatory quote settings with the given quote character.
     */
    public static Quote of(char quote) {
        return new Quote(quote, false);
    }

    private Quote(char quote, boolean optional) {
        this.quote = quote;
        this.optional = optional;
    }

    /**
     * Returns whether quoting is optional.
     */
    public boolean optional() {
        return optional;
    }

    /**
     * Returns whether quoting is disabled.
     */
    public boolean noQuote() {
        return quote == 0;
    }

    /**
     * Returns configured quote character.
     */
    public char quoteChar() {
        return quote;
    }
}
