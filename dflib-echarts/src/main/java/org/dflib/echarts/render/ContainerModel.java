package org.dflib.echarts.render;

import java.util.Objects;

/**
 * A model for rendering EChart HTML container
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

    public ContainerModel id(String id) {
        return new ContainerModel(id, this.width, this.height);
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
