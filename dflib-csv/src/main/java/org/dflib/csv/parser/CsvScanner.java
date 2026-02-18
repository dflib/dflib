package org.dflib.csv.parser;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.ParserContext;
import org.dflib.csv.parser.rules.ParserRule;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 * Reads character data from a {@link Reader} and drives parser rules over a reusable char buffer.
 */
final class CsvScanner {

    /**
     * Initial char buffer size
     */
    static final int INITIAL_BUFFER_SIZE = 8 * 1024; // 8kb

    /**
     * Max allowed buffer size, effectively max allowed single row size in row-first mode
     */
    static final int MAX_BUFFER_SIZE = 8 * 1024 * 1024; // 8mb

    final ParserContext context;
    final ParserRuleFlow ruleFlow;

    /**
     * Buffer to read char data from a CSV file to
     */
    CharBuffer readBuffer;

    /**
     * Tracks where the inner parse loop left off in the buffer. Used as the resume
     * position for the next iteration, preventing re-scanning of already-processed data.
     */
    int lastParsePos;

    CsvScanner(ParserContext context, ParserRuleFlow ruleFlow) {
        this.ruleFlow = ruleFlow;
        this.readBuffer = CharBuffer.allocate(INITIAL_BUFFER_SIZE);
        this.context = context;
        this.context.rebindRowSlices(readBuffer.array());
    }

    void scan(Reader reader) {
        ParserRule rule = ruleFlow.initialRule();
        DataSlice ruleSlice = new DataSlice();
        int newPosition;
        try {
            while (reader.read(readBuffer) != -1) {
                readBuffer.flip();
                char[] data = readBuffer.array();
                ruleSlice.setData(data);
                ruleSlice.setFrom(lastParsePos);
                ruleSlice.setTo(readBuffer.limit());
                while (ruleSlice.from() < ruleSlice.to()) {
                    newPosition = rule.consume(context, ruleSlice);
                    if (context.stopRequested()) {
                        return;
                    }
                    if (newPosition == ParserRule.CONTINUE) {
                        // rule can't find what it needs to end
                        break;
                    }
                    ruleSlice.setFrom(newPosition);
                    rule = ruleFlow.nextRule();
                }
                lastParsePos = ruleSlice.from();
                endOfBuffer();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!context.stopRequested()) {
            eof(rule);
        }
    }

    private void eof(ParserRule rule) {
        rule.validateAtEof(context);
        readBuffer.flip();
        context.markLastRow(readBuffer.limit());
    }

    /**
     * Prepare the read buffer for the next {@code Reader.read(...)} call.
     *
     * <p>Computes the earliest buffer offset that must be preserved (from row anchor,
     * open column, or parse resume position), then either clears, grows, or compacts
     * the buffer accordingly.
     */
    void endOfBuffer() {
        int anchor = context.bufferAnchor();
        int limit = readBuffer.limit();

        // Compute the earliest offset to preserve
        int earliest = anchor;
        if (lastParsePos > 0 && lastParsePos < limit) {
            earliest = earliest >= 0 ? Math.min(earliest, lastParsePos) : lastParsePos;
        }

        // 1. Nothing to preserve — clear the buffer
        if (earliest == -1) {
            readBuffer.clear();
            lastParsePos = 0;
            return;
        }

        // 2. Data starts at 0 and spans the entire buffer — grow
        if (earliest == 0) {
            int newSize = readBuffer.capacity() * 2;
            if (newSize > MAX_BUFFER_SIZE) {
                throw new RuntimeException("Char buffer is too small to read a single value");
            }

            CharBuffer newBuffer = CharBuffer.allocate(newSize);
            readBuffer.position(0);
            newBuffer.put(readBuffer);
            context.rebindRowSlices(newBuffer.array());
            readBuffer = newBuffer;
            // lastParsePos unchanged — no shift
            return;
        }

        // 3. Compact from the earliest offset to preserve partial data still needed by the current rule.
        context.shiftAll(earliest);
        readBuffer.position(earliest);
        readBuffer.compact();
        lastParsePos -= earliest;
    }
}
