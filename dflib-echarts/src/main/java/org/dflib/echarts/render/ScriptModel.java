package org.dflib.echarts.render;

import java.util.Objects;

/**
 * A model for rendering EChart script
 */
public record ScriptModel(
        String id,
        String theme,
        InitOptsModel initOpts,
        OptionModel option) {

    public ScriptModel(
            String id,
            String theme,
            InitOptsModel initOpts,
            OptionModel option) {

        this.id = Objects.requireNonNull(id);
        this.theme = theme;
        this.initOpts = initOpts;
        this.option = option;
    }

    public ScriptModel id(String id) {
        return new ScriptModel(id, this.theme, this.initOpts, this.option);
    }

    public String getThemeOrNull() {
        // need to have a null arg placeholder to be able to optionally pass init options
        return theme != null ? "'" + theme + "'" : "null";
    }
}
