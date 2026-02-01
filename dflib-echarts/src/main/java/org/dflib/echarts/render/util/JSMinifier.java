package org.dflib.echarts.render.util;

/**
 * A simple and fast HTML/JavaScript minifier that strips leading spaces, removes comments, and concatenates multiple
 * lines.
 *
 * @since 2.0.0
 */
public class JSMinifier {

    /**
     * Minifies HTML/JavaScript string by removing leading spaces, comments, and concatenating lines. Performs the
     * following operations:
     * <ul>
     *   <li>Removes leading whitespace from each line</li>
     *   <li>Removes single-line comments (//)</li>
     *   <li>Removes multi-line comments (&#47;* *&#47;)</li>
     *   <li>Concatenates lines without spaces</li>
     * </ul>
     */
    public static String minify(String js) {
        if (js == null || js.isEmpty()) {
            return js;
        }

        int len = js.length();
        StringBuilder result = new StringBuilder(len);
        int i = 0;
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean inBacktick = false;
        boolean atLineStart = true;

        while (i < len) {
            char c = js.charAt(i);
            char next = (i + 1 < len) ? js.charAt(i + 1) : '\0';

            // Track string literals to avoid processing comments inside them
            if (!inDoubleQuote && !inBacktick && c == '\'' && (i == 0 || js.charAt(i - 1) != '\\')) {
                inSingleQuote = !inSingleQuote;
                result.append(c);
                atLineStart = false;
                i++;
                continue;
            }

            if (!inSingleQuote && !inBacktick && c == '"' && (i == 0 || js.charAt(i - 1) != '\\')) {
                inDoubleQuote = !inDoubleQuote;
                result.append(c);
                atLineStart = false;
                i++;
                continue;
            }

            if (!inSingleQuote && !inDoubleQuote && c == '`' && (i == 0 || js.charAt(i - 1) != '\\')) {
                inBacktick = !inBacktick;
                result.append(c);
                atLineStart = false;
                i++;
                continue;
            }

            // Skip leading whitespace
            if (atLineStart && (c == ' ' || c == '\t')) {
                i++;
                continue;
            }

            // Process comments only if not inside a string
            if (!inSingleQuote && !inDoubleQuote && !inBacktick) {
                // Single-line comment
                if (c == '/' && next == '/') {
                    // Remove trailing whitespace before the comment
                    while (!result.isEmpty() && (result.charAt(result.length() - 1) == ' ' || result.charAt(result.length() - 1) == '\t')) {
                        result.setLength(result.length() - 1);
                    }
                    // Skip until end of line
                    i += 2;
                    while (i < len && js.charAt(i) != '\n' && js.charAt(i) != '\r') {
                        i++;
                    }
                    continue;
                }

                // Multi-line comment
                if (c == '/' && next == '*') {
                    // Remove trailing whitespace before the comment
                    while (!result.isEmpty() && (result.charAt(result.length() - 1) == ' ' || result.charAt(result.length() - 1) == '\t')) {
                        result.setLength(result.length() - 1);
                    }
                    // Skip until */
                    i += 2;
                    while (i < len - 1) {
                        if (js.charAt(i) == '*' && js.charAt(i + 1) == '/') {
                            i += 2;
                            break;
                        }
                        i++;
                    }
                    continue;
                }
            }

            // Handle newlines: skip them unless inside a string literal
            if (c == '\n' || c == '\r') {
                // Inside template literals, preserve newlines
                if (inBacktick) {
                    if (c == '\r' && next == '\n') {
                        result.append("\r\n");
                        i += 2;
                    } else {
                        result.append(c);
                        i++;
                    }
                    atLineStart = true;
                    continue;
                }

                // Skip CR in CRLF
                if (c == '\r' && next == '\n') {
                    i++;
                }

                atLineStart = true;
                i++;
                continue;
            }

            // Regular character
            result.append(c);
            atLineStart = false;
            i++;
        }

        return result.toString();
    }
}
