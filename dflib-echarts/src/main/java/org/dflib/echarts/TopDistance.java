package org.dflib.echarts;

import org.dflib.echarts.render.option.Distance;

import java.util.Objects;

class TopDistance {
    private final Distance distance;
    private final AutoTopDistance autoDistance;

    TopDistance(AutoTopDistance autoDistance) {
        this.autoDistance = Objects.requireNonNull(autoDistance);
        this.distance = null;
    }

    TopDistance(Distance distance) {
        this.distance = Objects.requireNonNull(distance);
        this.autoDistance = null;
    }

    String asString() {
        return distance != null ? distance.asString() : "'" + autoDistance.name() + "'";
    }

    enum AutoTopDistance {
        top, middle, bottom
    }
}
