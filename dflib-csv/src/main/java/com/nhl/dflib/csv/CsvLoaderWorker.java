package com.nhl.dflib.csv;

import com.nhl.dflib.DataFrame;
import org.apache.commons.csv.CSVRecord;

import java.util.Iterator;

interface CsvLoaderWorker {

    DataFrame load(Iterator<CSVRecord> it);
}
