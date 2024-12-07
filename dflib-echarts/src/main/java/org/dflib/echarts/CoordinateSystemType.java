package org.dflib.echarts;

/**
 * @since 2.0.0
 */
public enum CoordinateSystemType {

    cartesian2d, calendar, none;

    public boolean isCartesian() {
        return this == cartesian2d;
    }

    public boolean isCalendar() {
        return this == calendar;
    }
}
