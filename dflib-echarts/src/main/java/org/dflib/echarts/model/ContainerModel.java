package org.dflib.echarts.model;

import java.util.Objects;

/**
 * A model for rendering EChart HTML container
 *
 * @since 1.0.0-M21
 */
public class ContainerModel {

    private final String id;
    private final int width;
    private final int height;

    public ContainerModel(
            String id,
            int width,
            int height) {

        this.id = Objects.requireNonNull(id);
        this.width = width;
        this.height = height;
    }

    public String getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
