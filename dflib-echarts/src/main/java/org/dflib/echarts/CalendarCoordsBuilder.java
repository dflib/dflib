package org.dflib.echarts;

class CalendarCoordsBuilder {
    final String columnName;
    final CalendarCoords calendar;

    CalendarCoordsBuilder(String columnName, CalendarCoords calendar) {
        this.columnName = columnName;
        this.calendar = calendar;
    }

    public CalendarCoords getCalendar() {
        return calendar;
    }
}
