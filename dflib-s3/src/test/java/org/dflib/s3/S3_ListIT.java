package org.dflib.s3;

import org.dflib.s3.unit.BaseS3Test;
import org.dflib.s3.unit.S3TestContainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class S3_ListIT extends BaseS3Test {

    @Test
    void list() {
        assertEquals(
                List.of("data/a.csv", "data/b.csv.gz", "data/notes.txt"),
                keys(s3("data").list()));
    }

    @Test
    void list_IncludeFolders() {
        List<S3Object> objects = s3("data").list(true);
        assertEquals(
                List.of("data/a.csv", "data/b.csv.gz", "data/empty/", "data/notes.txt", "data/sub/"),
                keys(objects));

        // a folder with no physical marker is represented by a synthetic object
        S3Object sub = objects.get(4);
        assertNull(sub.size());
        assertNull(sub.lastModified());
    }

    @Test
    void list_IncludeSubfolders() {
        assertEquals(
                List.of("data/a.csv", "data/b.csv.gz", "data/notes.txt", "data/sub/c.csv"),
                keys(s3("data").includeSubfolders().list()));
    }

    @Test
    void list_IncludeSubfolders_IncludeFolders() {
        List<S3Object> objects = s3("data").includeSubfolders().list(true);
        assertEquals(
                List.of("data/a.csv", "data/b.csv.gz", "data/empty/", "data/notes.txt", "data/sub/", "data/sub/c.csv"),
                keys(objects));

        // a physical folder marker has real metadata
        assertNotNull(objects.get(2).lastModified());
    }

    @Test
    void list_IncludeHidden() {
        assertEquals(
                List.of("data/.hidden.csv", "data/a.csv", "data/b.csv.gz", "data/notes.txt"),
                keys(s3("data").includeHidden().list()));

        assertEquals(
                List.of("data/.git/config", "data/.hidden.csv", "data/a.csv", "data/b.csv.gz", "data/notes.txt", "data/sub/c.csv"),
                keys(s3("data").includeHidden().includeSubfolders().list()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"csv", ".csv"})
    void list_IncludeExtension(String ext) {
        assertEquals(
                List.of("data/a.csv", "data/sub/c.csv"),
                keys(s3("data").includeSubfolders().includeExtension(ext).list(true)));
    }

    @Test
    void list_IncludePredicate() {
        assertEquals(
                List.of("data/notes.txt"),
                keys(s3("data").include(o -> o.key().contains("note")).list()));

        // the second "include" replaces the first one
        assertEquals(
                List.of("data/a.csv"),
                keys(s3("data").include(o -> o.key().contains("note")).include(o -> o.key().endsWith("a.csv")).list()));
    }

    @Test
    void list_BucketRoot() {
        assertEquals(
                List.of("data.csv", "data/", "data_old/", "paging/", "root.csv"),
                keys(S3.of(S3TestContainer.BUCKET).client(S3TestContainer.client()).list(true)));
    }

    @Test
    void list_NoSuchFolder() {
        assertTrue(s3("no-such-folder").list().isEmpty());
    }

    @Test
    void list_NoSuchBucket() {
        S3 s3 = S3.of("s3://no-such-bucket/data").client(S3TestContainer.client());
        RuntimeException e = assertThrows(RuntimeException.class, s3::list);
        assertTrue(e.getMessage().contains("s3://no-such-bucket/data/"), e.getMessage());
    }

    @Test
    void list_Paging() {
        S3 s3 = s3("paging");

        assertEquals(
                List.of("paging/d1/", "paging/d2/", "paging/d3/",
                        "paging/f1.txt", "paging/f2.txt", "paging/f3.txt", "paging/f4.txt", "paging/f5.txt"),
                keys(s3.list(true, 2)));

        assertEquals(
                List.of("paging/d1/", "paging/d1/x.txt", "paging/d2/", "paging/d2/x.txt", "paging/d3/", "paging/d3/x.txt",
                        "paging/f1.txt", "paging/f2.txt", "paging/f3.txt", "paging/f4.txt", "paging/f5.txt"),
                keys(s3.includeSubfolders().list(true, 2)));
    }

    private static List<String> keys(List<S3Object> objects) {
        return objects.stream().map(S3Object::key).toList();
    }
}
