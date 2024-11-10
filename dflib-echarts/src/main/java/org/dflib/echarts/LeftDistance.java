package org.dflib.echarts;

import org.dflib.echarts.render.option.Distance;

import java.util.Objects;

class LeftDistance {
    private final Distance distance;
    private final AutoLeftDistance autoDistance;

    LeftDistance(AutoLeftDistance autoDistance) {
        this.autoDistance = Objects.requireNonNull(autoDistance);
        this.distance = null;
    }

    LeftDistance(Distance distance) {
        this.distance = Objects.requireNonNull(distance);
        this.autoDistance = null;
    }

    String asString() {
        return distance != null ? distance.asString() : "'" + autoDistance.name() + "'";
    }

    enum AutoLeftDistance {
        left, center, right
    }
}
