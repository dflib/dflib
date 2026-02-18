package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.format.CsvFormat;

class NoopFactory implements RuleFactory {

    @Override
    public ParserRule create(CsvFormat format) {
        return (context, slice) -> slice.from();
    }
}
