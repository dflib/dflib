package org.dflib.echarts.render;

import java.util.Objects;

/**
 * A model for rendering EChart script
 *
 * @since 1.0.0-M21
 */
public class ScriptModel {

    private final String id;
    private final String theme;
    private final OptionModel option;

    public ScriptModel(
            String id,
            String theme,
            OptionModel option) {

        this.id = Objects.requireNonNull(id);
        this.theme = theme;
        this.option = option;
    }

    public String getId() {
        return id;
    }

    public OptionModel getOption() {
        return option;
    }

    public String getTheme() {
        return theme;
    }
}
