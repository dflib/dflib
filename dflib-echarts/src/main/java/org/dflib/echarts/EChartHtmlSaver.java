package org.dflib.echarts;

import com.github.mustachejava.Mustache;
import org.dflib.echarts.saver.HtmlPageModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * @since 2.0.0
 */
public class EChartHtmlSaver {

    private static final Mustache HTML_PAGE_TEMPLATE = EChart.loadTemplate("html_page.mustache");

    private boolean createMissingDirs;
    private String title;
    private String chartStyle;

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
     * Overrides the default style of the chart "div" containers with the provided inline CSS snippet. The style argument
     * will be inserted inside the <code>.dfl_ech { .. }</code> block and will be applied to each chart.
     */
    public EChartHtmlSaver chartStyle(String style) {
        this.chartStyle = style;
        return this;
    }

    public EChartHtmlSaver title(String title) {
        this.title = title;
        return this;
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

        String scriptImport = charts.length > 0 ? charts[0].getExternalScript() : "";

        HtmlPageModel model = new HtmlPageModel(
                this.title != null ? this.title : "DFLib Chart",
                scriptImport,
                chartStyle != null ? chartStyle : "margin: 50px auto; padding: 20px;",
                Arrays.asList(charts)
        );

        boolean direct = out instanceof Writer;
        Writer writer = direct ? (Writer) out : new StringWriter();
        HTML_PAGE_TEMPLATE.execute(writer, model);

        if (!direct) {
            out.append(writer.toString());
        }
    }
}
