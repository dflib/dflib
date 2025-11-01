package org.dflib.echarts;

class ColumnLinkedCalendarCoords {
    final String columnName;
    final CalendarCoords calendar;

    ColumnLinkedCalendarCoords(String columnName, CalendarCoords calendar) {
        this.columnName = columnName;
        this.calendar = calendar;
    }

    public CalendarCoords getCalendar() {
        return calendar;
    }
}
