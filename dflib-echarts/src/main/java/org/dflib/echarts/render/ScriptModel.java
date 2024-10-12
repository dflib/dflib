package org.dflib.echarts.render;

import java.util.Objects;

/**
 * A model for rendering EChart script
 */
public class ScriptModel {

    private final String id;
    private final String theme;
    private final InitOptsModel initOpts;
    private final OptionModel option;

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

    public String getId() {
        return id;
    }

    public String getThemeOrNull() {
        // need to have a null arg placeholder to be able to optionally pass init options
        return theme != null ? "'" + theme + "'" : "null";
    }

    public InitOptsModel getInitOpts() {
        return initOpts;
    }

    public OptionModel getOption() {
        return option;
    }
}
