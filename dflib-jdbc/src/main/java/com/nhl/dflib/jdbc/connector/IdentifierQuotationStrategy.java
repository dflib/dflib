package com.nhl.dflib.jdbc.connector;

import java.util.Objects;

public interface IdentifierQuotationStrategy {

    /**
     * @param quote a quote symbol for identifiers.
     * @return a strategy that will enclose identifiers in the provided quotation symbol.
     */
    static IdentifierQuotationStrategy forQuoteSymbol(String quote) {
        Objects.requireNonNull(quote);
        return id -> quote + id + quote;
    }

    /**
     * @return a strategy that will returns identifiers unchanged.
     */
    static IdentifierQuotationStrategy noQuote() {
        return id -> id;
    }

    String quoted(String bareIdentifier);
}

