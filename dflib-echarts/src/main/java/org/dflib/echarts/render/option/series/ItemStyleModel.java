package org.dflib.echarts.render.option.series;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @since 1.1.0
 */
public class ItemStyleModel {

    private static final int BORDER_RADIUS_LENGTH = 4;

    private final String color;
    private final String color0;
    private final String borderColor;
    private final String borderColor0;
    private final String borderColorDoji;
    private final Integer borderWidth;
    private final int[] borderRadius;
    private final Double opacity;

    public ItemStyleModel(
            String color,
            String color0,
            String borderColor,
            String borderColor0,
            String borderColorDoji,
            Integer borderWidth,
            int[] borderRadius,
            Double opacity) {

        this.borderColor0 = borderColor0;
        this.color = color;
        this.color0 = color0;
        this.borderColor = borderColor;
        this.borderColorDoji = borderColorDoji;
        this.borderWidth = borderWidth;

        if (borderRadius != null && borderRadius.length != BORDER_RADIUS_LENGTH) {
            throw new IllegalArgumentException("'borderRadius' must contain exactly " + BORDER_RADIUS_LENGTH + " items. Instead got " + borderRadius.length);
        }
        this.borderRadius = borderRadius;

        this.opacity = opacity;
    }

    public String getColor() {
        return color;
    }

    public String getBorderColor0() {
        return borderColor0;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public String getBorderColorDoji() {
        return borderColorDoji;
    }

    public Integer getBorderWidth() {
        return borderWidth;
    }

    public String getColor0() {
        return color0;
    }

    public boolean isBorderRadiusPresent() {
        return borderRadius != null;
    }

    public String borderRadiusString() {
        for (int i = 1; i < BORDER_RADIUS_LENGTH; i++) {
            if (borderRadius[0] != borderRadius[i]) {
                // render as array
                return IntStream.of(borderRadius).mapToObj(String::valueOf).collect(Collectors.joining(",", "[", "]"));
            }
        }

        // render as one value
        return String.valueOf(borderRadius[0]);
    }

    public Double getOpacity() {
        return opacity;
    }
}
