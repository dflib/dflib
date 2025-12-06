package org.dflib.echarts.saver;

import java.util.List;
import java.util.Optional;

/**
 * @since 2.0.0
 */
public record HtmlPageModel(
        String pageTitle,
        String echartsUrl,
        List<String> themeUrls,
        List<HtmlChartModel> charts) {

    // expose the first 30 charts with explicit methods that require no iterator. This would allow explicit positioning
    // of specific charts on the page

    public String getChartScript0() {
        return get(0).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv0() {
        return get(0).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript1() {
        return get(1).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv1() {
        return get(1).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript2() {
        return get(2).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv2() {
        return get(2).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript3() {
        return get(3).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv3() {
        return get(3).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript4() {
        return get(4).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv4() {
        return get(4).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript5() {
        return get(5).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv5() {
        return get(5).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript6() {
        return get(6).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv6() {
        return get(6).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript7() {
        return get(7).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv7() {
        return get(7).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript8() {
        return get(8).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv8() {
        return get(8).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript9() {
        return get(9).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv9() {
        return get(9).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript10() {
        return get(10).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv10() {
        return get(10).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript11() {
        return get(11).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv11() {
        return get(11).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript12() {
        return get(12).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv12() {
        return get(12).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript13() {
        return get(13).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv13() {
        return get(13).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript14() {
        return get(14).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv14() {
        return get(14).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript15() {
        return get(15).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv15() {
        return get(15).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript16() {
        return get(16).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv16() {
        return get(16).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript17() {
        return get(17).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv17() {
        return get(3).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript18() {
        return get(18).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv18() {
        return get(18).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript19() {
        return get(19).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv19() {
        return get(19).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript20() {
        return get(20).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv20() {
        return get(20).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript21() {
        return get(21).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv21() {
        return get(21).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript22() {
        return get(22).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv22() {
        return get(22).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript23() {
        return get(23).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv23() {
        return get(23).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript24() {
        return get(24).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv24() {
        return get(24).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript25() {
        return get(25).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv25() {
        return get(3).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript26() {
        return get(26).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv26() {
        return get(26).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript27() {
        return get(27).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv27() {
        return get(27).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript28() {
        return get(28).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv28() {
        return get(28).map(HtmlChartModel::chartDiv).orElse("");
    }

    public String getChartScript29() {
        return get(29).map(HtmlChartModel::chartScript).orElse("");
    }

    public String getChartDiv29() {
        return get(29).map(HtmlChartModel::chartDiv).orElse("");
    }

    private Optional<HtmlChartModel> get(int pos) {
        return pos >= 0 && pos < charts.size() ? Optional.of(charts.get(pos)) : Optional.empty();
    }
}
