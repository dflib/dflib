package org.dflib.echarts;

import org.dflib.ByteSource;
import org.junit.jupiter.api.Test;

import static org.dflib.echarts.EChartTestDatasets.df1;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EChartMapTest {

    private static final String SIMPLE_GEOJSON = """
            {
              "type": "FeatureCollection",
              "features": [
                {
                  "type": "Feature",
                  "geometry": {
                    "type": "Point",
                    "coordinates": [102.0, 0.5]
                  },
                  "properties": {
                    "name": "Test Point"
                  }
                }
              ]
            }""";

    private static final String POLYGON_GEOJSON = """
            {
              "type": "FeatureCollection",
              "features": [
                {
                  "type": "Feature",
                  "geometry": {
                    "type": "Polygon",
                    "coordinates": [[
                      [100.0, 0.0],
                      [101.0, 0.0],
                      [101.0, 1.0],
                      [100.0, 1.0],
                      [100.0, 0.0]
                    ]]
                  },
                  "properties": {
                    "name": "Test Region"
                  }
                }
              ]
            }""";

    @Test
    public void noMaps() {
        String s1 = ECharts.chart().plot(df1, "_tid").renderChartScript();
        assertFalse(s1.contains("registerMap"), s1);
        assertFalse(s1.contains("geoJSON"), s1);
    }

    @Test
    public void noMapsWithLoadECharts() {
        String s1 = ECharts.chart().plot(df1, "_tid").renderChartScript(true);
        assertFalse(s1.contains("registerMap"), s1);
        assertFalse(s1.contains("geoJSON"), s1);
    }

    @Test
    public void singleMap() {
        ByteSource geoJson = ByteSource.of(SIMPLE_GEOJSON.getBytes());

        String s1 = ECharts.chart()
                .map("testMap", geoJson)
                .plot(df1, "_tid")
                .renderChartScript(true);

        assertTrue(s1.contains("window.echarts.registerMap"), s1);
        assertTrue(s1.contains("'testMap'"), s1);
        assertTrue(s1.contains("geoJSON:"), s1);
        assertTrue(s1.contains("\"type\": \"FeatureCollection\""), s1);
        assertTrue(s1.contains("\"coordinates\": [102.0, 0.5]"), s1);
    }

    @Test
    public void singleMapWithoutLoadECharts() {
        ByteSource geoJson = ByteSource.of(SIMPLE_GEOJSON.getBytes());

        // renderChartScript(false) should not include registerMap calls since echarts may not be loaded yet
        String s1 = ECharts.chart()
                .map("testMap", geoJson)
                .plot(df1, "_tid")
                .renderChartScript(false);

        assertFalse(s1.contains("registerMap"), s1);
    }

    @Test
    public void multipleMaps() {
        ByteSource geoJson1 = ByteSource.of(SIMPLE_GEOJSON.getBytes());
        ByteSource geoJson2 = ByteSource.of(POLYGON_GEOJSON.getBytes());

        String s1 = ECharts.chart()
                .map("pointMap", geoJson1)
                .map("polygonMap", geoJson2)
                .plot(df1, "_tid")
                .renderChartScript(true);

        assertTrue(s1.contains("window.echarts.registerMap"), s1);
        assertTrue(s1.contains("'pointMap'"), s1);
        assertTrue(s1.contains("'polygonMap'"), s1);
        assertTrue(s1.contains("geoJSON:"), s1);
        assertTrue(s1.contains("\"name\": \"Test Point\""), s1);
        assertTrue(s1.contains("\"name\": \"Test Region\""), s1);
    }

    @Test
    public void mapReplacesExistingWithSameName() {
        ByteSource geoJson1 = ByteSource.of(SIMPLE_GEOJSON.getBytes());
        ByteSource geoJson2 = ByteSource.of(POLYGON_GEOJSON.getBytes());

        String s1 = ECharts.chart()
                .map("testMap", geoJson1)
                .map("testMap", geoJson2)  // Should replace the first one
                .plot(df1, "_tid")
                .renderChartScript(true);

        assertTrue(s1.contains("window.echarts.registerMap('testMap'"), s1);
        // Should only contain the second GeoJSON
        assertTrue(s1.contains("\"name\": \"Test Region\""), s1);
        assertFalse(s1.contains("\"name\": \"Test Point\""), s1);
    }
}
