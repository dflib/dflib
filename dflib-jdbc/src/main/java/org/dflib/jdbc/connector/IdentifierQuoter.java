package org.dflib.jdbc.connector;

import java.util.Objects;

public interface IdentifierQuoter {

    /**
     * @param quote a quote symbol for identifiers.
     * @return a strategy that will enclose identifiers in the provided quotation symbol.
     */
    static IdentifierQuoter forQuoteSymbol(String quote) {
        Objects.requireNonNull(quote);
        return id -> quote + id + quote;
    }

    /**
     * @return a strategy that will returns identifiers unchanged.
     */
    static IdentifierQuoter noQuote() {
        return id -> id;
    }

    String quoted(String bareIdentifier);
}

