package org.dflib.csv.parser;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.ParserContext;
import org.dflib.csv.parser.format.LineBreak;
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

    /**
     * Max number of chars before and after the error position to include in the error message
     */
    static final int ERROR_EXCERPT_RADIUS = 50;

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

    /**
     * The current parser rule
     */
    private ParserRule rule;

    CsvScanner(ParserContext context, ParserRuleFlow ruleFlow) {
        this.ruleFlow = ruleFlow;
        this.readBuffer = CharBuffer.allocate(INITIAL_BUFFER_SIZE);
        this.context = context;
        this.context.rebindRowSlices(readBuffer.array());
    }

    void scan(Reader reader) {
        this.rule = ruleFlow.initialRule();
        DataSlice ruleSlice = new DataSlice();
        boolean autoLineBreak = ruleFlow.config.csvFormat().lineBreak() == LineBreak.AUTO;
        // the previous read ended with CR, the LF that may follow belongs to it
        boolean skipLf = false;
        try {
            while (reader.read(readBuffer) != -1) {
                readBuffer.flip();
                char[] data = readBuffer.array();
                int limit = readBuffer.limit();
                if (skipLf && lastParsePos < limit) {
                    if (data[lastParsePos] == '\n') {
                        lastParsePos++;
                    }
                    skipLf = false;
                }

                ruleSlice.setData(data);
                ruleSlice.setFrom(lastParsePos);
                ruleSlice.setTo(limit);
                int newPosition = scanBuffer(ruleSlice, limit);
                if (context.stopRequested()) {
                    return;
                }

                skipLf = autoLineBreak && newPosition == limit && data[limit - 1] == '\r';
                endOfBuffer();
                // keep the slice in sync with the (possibly compacted) buffer for the error reporting
                ruleSlice.setFrom(lastParsePos);
            }

            if (!context.stopRequested()) {
                eof(rule);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (IllegalStateException e) {
            throw withLocation(e, ruleSlice.from());
        }
    }

    /**
     * Runs the parser rules over the buffer, updating {@link #rule} and {@link #lastParsePos}. Kept separate, with a
     * single loop exit and no rarely taken branches, so that the JIT doesn't deoptimize the scanner during the first
     * loads.
     *
     * @return the position returned by the last rule
     */
    private int scanBuffer(DataSlice ruleSlice, int limit) {
        ParserRule rule = this.rule;
        int newPosition = ParserRule.CONTINUE;
        int resumePosition = ruleSlice.from();

        while (ruleSlice.from() < limit) {
            newPosition = rule.consume(context, ruleSlice);

            // rules only mark the row end, see ParserContext.markRowEnd()
            if (context.rowEnded()) {
                context.endRow();
                // only a row can request a stop
                if (context.stopRequested()) {
                    break;
                }
            }

            if (newPosition == ParserRule.CONTINUE) {
                // the rule needs more data
                resumePosition = ruleSlice.from();
                ruleSlice.setFrom(limit);
            } else {
                resumePosition = newPosition;
                ruleSlice.setFrom(newPosition);
                rule = ruleFlow.nextRule();
            }
        }

        this.rule = rule;
        this.lastParsePos = resumePosition;
        return newPosition;
    }

    private void eof(ParserRule rule) {
        readBuffer.flip();
        rule.validateAtEof(context);
        context.markLastRow(readBuffer.limit());
    }

    /**
     * Adds the row number and the line text to a parsing error.
     */
    private IllegalStateException withLocation(IllegalStateException e, int position) {
        String message = e.getMessage() != null ? e.getMessage() : "Parsing error";

        // offset rows are not counted by the context, and can't fail
        int row = Math.max(0, ruleFlow.config.offset()) + context.rowsRead() + 1;
        String excerpt = excerpt(readBuffer.array(), position, readBuffer.limit());

        return new IllegalStateException(message + " (row " + row + ")"
                + (excerpt.isEmpty() ? "" : ": " + excerpt), e);
    }

    /**
     * Returns a part of the line around the position, limited to {@link #ERROR_EXCERPT_RADIUS} chars in each direction.
     */
    private static String excerpt(char[] data, int position, int limit) {
        int pos = Math.max(0, Math.min(position, limit));

        int start = pos;
        while (start > 0 && pos - start < ERROR_EXCERPT_RADIUS && !isLineBreak(data[start - 1])) {
            start--;
        }

        int end = pos;
        while (end < limit && end - pos < ERROR_EXCERPT_RADIUS && !isLineBreak(data[end])) {
            end++;
        }

        if (start == end) {
            return "";
        }

        String prefix = start > 0 && !isLineBreak(data[start - 1]) ? "..." : "";
        String suffix = end < limit && !isLineBreak(data[end]) ? "..." : "";
        return prefix + new String(data, start, end - start) + suffix;
    }

    private static boolean isLineBreak(char c) {
        return c == '\n' || c == '\r';
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
        if (lastParsePos < limit) {
            earliest = earliest >= 0 ? Math.min(earliest, lastParsePos) : lastParsePos;
        }

        // 1. Nothing to preserve — clear the buffer
        if (earliest == -1) {
            readBuffer.clear();
            lastParsePos = 0;
            return;
        }

        // 2. Data starts at 0
        if (earliest == 0) {

            // a partial read, keep reading after the data
            if (limit < readBuffer.capacity()) {
                readBuffer.position(limit);
                readBuffer.limit(readBuffer.capacity());
                return;
            }

            // the buffer is full — grow
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
