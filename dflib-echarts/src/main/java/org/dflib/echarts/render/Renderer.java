package org.dflib.echarts.render;

class Renderer {

    static String quotedValue(Object value) {
        return shouldQuote(value) ? quoteAndEscape(value) : String.valueOf(value);
    }

    private static String quoteAndEscape(Object value) {
        if (value == null) {
            return "null";
        }

        String s = value.toString();
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c == '\'') {
                return quoteAndEscape(s, i);
            }
        }

        return "'" + s + "'";
    }

    private static String quoteAndEscape(String s, int firstEscapePos) {

        int len = s.length();
        StringBuilder escaped = new StringBuilder(len + 5);
        escaped.append('\'').append(s.substring(0, firstEscapePos)).append('\\').append(s.charAt(firstEscapePos));

        for (int i = firstEscapePos + 1; i < len; i++) {
            char c = s.charAt(i);
            if (c == '\'') {
                escaped.append('\\');
            }

            escaped.append(c);
        }

        return escaped.append('\'').toString();
    }

    private static boolean shouldQuote(Object o) {
        return !(o instanceof Number) && !(o instanceof Boolean);
    }
}
