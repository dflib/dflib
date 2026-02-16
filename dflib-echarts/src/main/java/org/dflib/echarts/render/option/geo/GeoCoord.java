package org.dflib.echarts.render.option.geo;

/**
 * A single geo coordinate expressed as a number of a %.
 *
 * @since 2.0.0
 */
public abstract class GeoCoord {

    public static GeoCoord of(double value) {
        return new DoubleCoord(value);
    }

    public static GeoCoord ofPct(double percent) {
        return new PctCoord(percent);
    }

    public abstract String asString();

    static class PctCoord extends GeoCoord {
        private final double value;

        PctCoord(double value) {
            this.value = value;
        }

        @Override
        public String asString() {
            return "'" + value + "%'";
        }
    }

    static class DoubleCoord extends GeoCoord {
        private final double value;

        DoubleCoord(double value) {
            this.value = value;
        }

        @Override
        public String asString() {
            return String.valueOf(value);
        }
    }
}
