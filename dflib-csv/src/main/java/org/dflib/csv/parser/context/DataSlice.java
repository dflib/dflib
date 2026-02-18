package org.dflib.csv.parser.context;

/**
 * Mutable view of a value currently being parsed from the shared scanner buffer.
 *
 * <p>{@code from} and {@code to} use scanner-buffer indexes:
 * <ul>
 *   <li>{@code from == -1}: no column is currently open</li>
 *   <li>{@code from >= 0}: an open column starts at this offset</li>
 *   <li>{@code to >= from}: column end offset when column is closed</li>
 * </ul>
 *
 * Internal API. Part of the {@link org.dflib.csv.CsvLoader} API
 *
 * @since 2.0.0
 */
public class DataSlice {

    private char[] data;

    /**
     * Start offset in the current scanner buffer. {@code -1} means no open column.
     */
    private int from = -1;

    /**
     * End offset in the current scanner buffer. {@code -1} means not set.
     */
    private int to = -1;
    private boolean quoted = false;
    private boolean escaped = false;

    public static DataSlice of(char[] data) {
        DataSlice slice = new DataSlice();
        slice.data = data;
        slice.from = 0;
        slice.to = data.length;
        return slice;
    }

    public static DataSlice of(char[] data, boolean escaped) {
        DataSlice slice = of(data);
        slice.escaped = escaped;
        return slice;
    }

    public DataSlice() {
    }

    public char[] data() {
        return data;
    }

    public char data(int idx) {
        return data[idx];
    }

    public void setData(char[] data) {
        this.data = data;
    }

    public int from() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int to() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public boolean quoted() {
        return quoted;
    }

    public void setQuoted(boolean quoted) {
        this.quoted = quoted;
    }

    public boolean escaped() {
        return escaped;
    }

    public void setEscaped(boolean escaped) {
        this.escaped = escaped;
    }

    public void reset() {
        from = -1;
        to = -1;
        quoted = false;
        escaped = false;
    }

    public void shift(int pos) {
        if (from > -1) {
            from -= pos;
        }
        if (to > -1) {
            to -= pos;
        }
    }

    public boolean open() {
        return from > -1;
    }

    public boolean empty() {
        return from == -1;
    }

    @Override
    public String toString() {
        if(from < 0 || to == from) {
            return "";
        }
        return new String(data, from, to - from);
    }

    public DataSlice copy() {
        DataSlice slice = new DataSlice();
        char[] data;
        if(from == to) {
            data = new char[0];
        } else {
            data = new char[to - from];
            System.arraycopy(this.data(), from, data, 0, to - from);
        }
        slice.data = data;
        slice.from = 0;
        slice.to = to - from;
        slice.quoted = quoted;
        slice.escaped = escaped;
        return slice;
    }
}
