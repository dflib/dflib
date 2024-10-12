package org.dflib.window;

import org.dflib.DataFrame;

import java.util.Objects;

/**
 * A relative row offset used when applying window functions.
 */
public class WindowRange {

    private final int startOffsetInclusive;
    private final int endOffsetInclusive;

    public static final WindowRange allPreceding = new WindowRange(Integer.MAX_VALUE, 0) {
        @Override
        public boolean alwaysInRange(int height) {
            return height <= 1;
        }
    };

    public static final WindowRange allFollowing = new WindowRange(0, Integer.MAX_VALUE - 1) {
        @Override
        public boolean alwaysInRange(int height) {
            return height <= 1;
        }
    };

    public static final WindowRange all = new WindowRange(Integer.MAX_VALUE, Integer.MAX_VALUE - 1) {
        @Override
        public boolean alwaysInRange(int height) {
            return true;
        }
    };

    public static WindowRange of(int startOffsetInclusive, int endOffsetInclusive) {
        return new WindowRange(startOffsetInclusive, endOffsetInclusive);
    }

    protected WindowRange(int startOffsetInclusive, int endOffsetInclusive) {

        if (startOffsetInclusive < 0) {
            throw new IllegalArgumentException("'startOffsetInclusive' must be non-negative: " + startOffsetInclusive);
        }

        if (endOffsetInclusive < 0) {
            throw new IllegalArgumentException("'endOffsetInclusive' must be non-negative: " + endOffsetInclusive);
        }

        this.startOffsetInclusive = startOffsetInclusive;
        this.endOffsetInclusive = endOffsetInclusive;
    }

    /**
     * Returns true if for the given DataFrame height, any row index will always be in range
     */
    public boolean alwaysInRange(int height) {

        return height <= 1

                // 0 or 1 element structures are always fully in range, as there are no offsets to calculate
                ? true

                // "height" is exclusive, and the range boundaries are inclusive.
                // Convert height to an inclusive value for proper comparison
                : startOffsetInclusive >= height - 1 && endOffsetInclusive >= height - 1;
    }

    /**
     * Returns a slice of the DataFrame rows that are within the range defined relative to the DataFrame row index.
     */
    public DataFrame selectRows(DataFrame dataFrame, int rowIndex) {

        int h = dataFrame.height();

        if (rowIndex < 0) {
            throw new ArrayIndexOutOfBoundsException("Negative row index: " + rowIndex);
        }
        if (rowIndex >= h) {
            throw new ArrayIndexOutOfBoundsException("Row index higher than the DataFrame height: " + rowIndex);
        }

        int fromInclusive = Math.max(0, rowIndex - startOffsetInclusive);

        // avoid int overflow - only add the offset when it is smaller than height
        // TODO: still there is a range when overflow is possible when the height is close to Integer.MAX_VALUE
        int delta = endOffsetInclusive - h;
        int toExclusive = delta <= 0 ? Math.min(h, rowIndex + endOffsetInclusive + 1) : h;

        return dataFrame.rowsRange(fromInclusive, toExclusive).select();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WindowRange that = (WindowRange) o;
        return startOffsetInclusive == that.startOffsetInclusive && endOffsetInclusive == that.endOffsetInclusive;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startOffsetInclusive, endOffsetInclusive);
    }
}
