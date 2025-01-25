package org.dflib.echarts;

import com.github.mustachejava.Mustache;
import org.dflib.echarts.render.util.Renderer;
import org.dflib.echarts.saver.HtmlChartModel;
import org.dflib.echarts.saver.HtmlPageModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @since 2.0.0
 */
public class EChartHtmlSaver {

    private static final Mustache DEFAULT_PAGE_TEMPLATE = Renderer.loadTemplate("html_page.mustache");

    private boolean createMissingDirs;
    private String title;
    private URL htmlTemplate;

    /**
     * Instructs the saver to create any missing directories in the file path.
     *
     * @return this saver instance
     */
    public EChartHtmlSaver createMissingDirs() {
        this.createMissingDirs = true;
        return this;
    }

    /**
     * Sets HTML page title.
     */
    public EChartHtmlSaver title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Sets a custom HTML template. Can be filesystem based, a classpath resource or a web URL. If not set, a standard
     * template is used. Custom template should contain the desired look-and-feel, with "dynamic" elements specified
     * using the Mustache format. The saver will fill them with chart data:
     * <ul>
     *     <li>{{pageTitle}}</li>
     *     <li>{{echartsUrl}}</li>
     *     <li>{{chartDivN}}, where N is a number between 0 and 29</li>
     *     <li>{{chartScriptN}}, where N is a number between 0 and 29</li>
     *     <li>{{#charts}} / {{/charts}} - a list of charts</li>
     *     <li>{{chartDiv}} - a chart div tag inside the {{#charts}} ... {{/charts}} block</li>
     *     <li>{{chartScript}} - a chart script inside the {{#charts}} ... {{/charts}} block</li>
     * </ul>
     */
    public EChartHtmlSaver htmlTemplate(URL htmlTemplate) {
        this.htmlTemplate = htmlTemplate;
        return this;
    }

    /**
     * @see #htmlTemplate(URL)
     */
    public EChartHtmlSaver htmlTemplate(File htmlTemplate) {
        try {
            this.htmlTemplate = htmlTemplate.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * @see #htmlTemplate(URL)
     */
    public EChartHtmlSaver htmlTemplate(Path htmlTemplate) {
        try {
            this.htmlTemplate = htmlTemplate.toUri().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * @see #htmlTemplate(URL)
     */
    public EChartHtmlSaver htmlTemplate(String htmlTemplateFile) {
        return htmlTemplate(new File(htmlTemplateFile));
    }

    public void save(Path path, EChartHtml... charts) {
        save(path.toFile(), charts);
    }

    public void save(String file, EChartHtml... charts) {
        save(new File(file), charts);
    }

    public void save(File file, EChartHtml... charts) {
        if (createMissingDirs) {
            File dir = file.getParentFile();
            if (dir != null) {
                dir.mkdirs();
            }
        }

        try (FileWriter out = new FileWriter(file)) {
            doSave(out, charts);
        } catch (IOException e) {
            throw new RuntimeException("Error writing chart to " + file + ": " + e.getMessage(), e);
        }
    }

    public void save(Appendable out, EChartHtml... charts) {
        try {
            doSave(out, charts);
        } catch (IOException e) {
            throw new RuntimeException("Error writing chart: " + e.getMessage(), e);
        }
    }

    private void doSave(Appendable out, EChartHtml... charts) throws IOException {

        HtmlPageModel model = new HtmlPageModel(
                this.title != null ? this.title : "DFLib Chart",
                charts.length > 0 ? charts[0].getEchartsUrl() : "",
                Arrays.stream(charts).map(c -> new HtmlChartModel(c.getChartDiv(), c.getChartScript())).collect(Collectors.toList())
        );

        boolean direct = out instanceof Writer;
        Writer writer = direct ? (Writer) out : new StringWriter();
        htmlTemplate().execute(writer, model);

        if (!direct) {
            out.append(writer.toString());
        }
    }

    private Mustache htmlTemplate() {
        // TODO: cache precompiled templates for cases of mass chart generation for a single template?
        return this.htmlTemplate != null ? Renderer.loadTemplate(htmlTemplate) : DEFAULT_PAGE_TEMPLATE;
    }
}
