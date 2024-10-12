package org.dflib.echarts.render.option;

/**
 * A distance expressed as % or pixels.
 */
public abstract class Distance {

    public static Distance ofPx(int pixels) {
        return new PxDistance(pixels);
    }

    public static Distance ofPct(double percent) {
        return new PctDistance(percent);
    }

    public abstract String asString();

    static class PctDistance extends Distance {
        private final double value;

        PctDistance(double value) {
            this.value = value;
        }

        @Override
        public String asString() {
            return "'" + value + "%'";
        }
    }

    static class PxDistance extends Distance {
        private final int value;

        PxDistance(int value) {
            this.value = value;
        }

        @Override
        public String asString() {
            return String.valueOf(value);
        }
    }
}
