package org.dflib.echarts.render.util;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import org.dflib.echarts.ECharts;
import org.dflib.echarts.render.ChartModel;
import org.dflib.echarts.render.ContainerModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;

public class Renderer {

    private static final Mustache CONTAINER_TEMPLATE = loadTemplate("container.html.mustache");
    private static final Mustache SCRIPT_TEMPLATE = loadTemplate("chart.js.mustache");

    /**
     * Loads a Mustache template, resolving the name against classpath
     */
    public static Mustache loadTemplate(String name) {
        return loadTemplate(ECharts.class.getResource(name));
    }

    /**
     * Loads a Mustache template from the provided URL
     */
    public static Mustache loadTemplate(URL url) {

        try (InputStream in = url.openStream()) {

            if (in == null) {
                throw new RuntimeException("ECharts 'cell.mustache' template is not found");
            }

            // not providing an explicit resolver of subtemplates. assuming a single flat template for now
            try (Reader reader = new InputStreamReader(in)) {
                return new DefaultMustacheFactory().compile(reader, url.getFile());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading mustache template: " + url, e);
        }
    }

    /**
     * @since 2.0.0
     */
    public static String renderContainer(ContainerModel model) {
        return CONTAINER_TEMPLATE.execute(new StringWriter(), model).toString();
    }

    /**
     * @since 2.0.0
     */
    public static String renderChart(ChartModel model) {
        return SCRIPT_TEMPLATE.execute(new StringWriter(), model).toString();
    }

    public static String quotedValue(Object value) {
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
        escaped.append('\'').append(s, 0, firstEscapePos).append('\\').append(s.charAt(firstEscapePos));

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
        return o != null && !(o instanceof Number) && !(o instanceof Boolean);
    }
}
