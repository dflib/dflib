package org.dflib.s3;

import org.dflib.ByteSource;
import org.dflib.ByteSources;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CommonPrefix;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a "folder" in S3-compatible storage (a bucket plus an optional key prefix). Allows to work with multiple
 * objects in the folder as {@link ByteSources}. Keys ending with "/" are treated as folders. Unless a client is set
 * via {@link #client(S3Client)}, a shared default client configured via the standard AWS SDK settings is used.
 *
 * @since 2.0.0
 */
public class S3 {

    static final String SCHEME = "s3://";

    private final String bucket;
    private final String prefix;
    private final S3Client client;
    private final boolean includeSubfolders;
    private final Predicate<S3Object> filter;
    private final Predicate<S3Object> extFilter;
    private final boolean includeHidden;

    private S3(
            String bucket,
            String prefix,
            S3Client client,
            boolean includeSubfolders,
            Predicate<S3Object> filter,
            Predicate<S3Object> extFilter,
            boolean includeHidden) {

        this.bucket = Objects.requireNonNull(bucket);
        this.prefix = Objects.requireNonNull(prefix);

        // null means the default client
        this.client = client;

        this.includeSubfolders = includeSubfolders;
        this.filter = filter;
        this.extFilter = extFilter;
        this.includeHidden = includeHidden;
    }

    /**
     * Creates an S3 folder from a location like "s3://bucket/prefix" or "bucket/prefix". Doesn't access S3.
     */
    public static S3 of(String location) {
        Objects.requireNonNull(location);

        String path = location.startsWith(SCHEME) ? location.substring(SCHEME.length()) : location;

        int slash = path.indexOf('/');
        String bucket = slash < 0
                ? path
                : path.substring(0, slash);

        // bucket names can't contain ':', so it indicates an unsupported scheme
        if (bucket.indexOf(':') >= 0) {
            throw new IllegalArgumentException("Unsupported location scheme or invalid bucket name. Only '"
                    + SCHEME + "' scheme is allowed: " + location);
        }

        if (bucket.isEmpty()) {
            throw new IllegalArgumentException("No bucket name in location: " + location);
        }

        String prefix = slash < 0
                ? ""
                : S3Keys.toFolderPrefix(path.substring(slash + 1));
        if (prefix.startsWith("/")) {
            throw new IllegalArgumentException("Key prefix can not start with '/': " + location);
        }

        return new S3(bucket, prefix, null, false, null, null, false);
    }

    public String getBucket() {
        return bucket;
    }

    /**
     * Returns the key prefix, either an empty String or a String ending with "/".
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Returns an instance of S3 that uses the provided client. The caller is responsible for closing it.
     */
    public S3 client(S3Client client) {
        Objects.requireNonNull(client);
        return new S3(bucket, prefix, client, includeSubfolders, filter, extFilter, includeHidden);
    }

    /**
     * Returns an instance of S3 that is a subfolder of this folder.
     */
    public S3 path(String subfolder) {
        String subPrefix = S3Keys.appendPath(prefix, subfolder);
        return subPrefix.equals(prefix)
                ? this
                : new S3(bucket, subPrefix, client, includeSubfolders, filter, extFilter, includeHidden);
    }

    /**
     * Returns an instance of S3 that will only include objects that match the provided predicate. Any other
     * preconfigured filters (specific extensions or inclusion of hidden objects) are also applied.
     */
    public S3 include(Predicate<S3Object> filter) {
        Objects.requireNonNull(filter);
        return new S3(bucket, prefix, client, includeSubfolders, filter, extFilter, includeHidden);
    }

    /**
     * Returns an instance of S3 that will include objects with any relative key part starting with a dot.
     */
    public S3 includeHidden() {
        return includeHidden
                ? this
                : new S3(bucket, prefix, client, includeSubfolders, filter, extFilter, true);
    }

    /**
     * Returns an instance of S3 that will filter folder objects by extension.
     */
    public S3 includeExtension(String ext) {
        return new S3(bucket, prefix, client, includeSubfolders, filter, extensionFilter(ext), includeHidden);
    }

    /**
     * Returns an instance of S3 that will include objects in subfolders.
     */
    public S3 includeSubfolders() {
        return includeSubfolders
                ? this
                : new S3(bucket, prefix, client, true, filter, extFilter, includeHidden);
    }

    /**
     * Returns a single object within the folder. Since the key is explicit, the object doesn't need to match folder
     * filters. S3 is not accessed until the source is read.
     */
    public ByteSource source(String key) {
        S3Keys.checkRelativeKey(key);
        return new S3ByteSource(clientSupplier(), bucket, prefix + key);
    }

    /**
     * Returns all objects contained in the folder represented as {@link ByteSources}, filtered according to S3
     * configuration. The map keys are relative to this folder.
     */
    public ByteSources sources() {
        Supplier<S3Client> clientSupplier = clientSupplier();
        Map<String, ByteSource> sources = new LinkedHashMap<>();

        list().forEach(o -> sources.put(
                S3Keys.relativize(prefix, o.key()),
                new S3ByteSource(clientSupplier, bucket, o.key())));

        return ByteSources.of(sources);
    }

    public List<S3Object> list() {
        return list(false);
    }

    /**
     * Returns all objects in the folder, filtered according to S3 configuration and sorted by key. If "includeFolders"
     * is true, subfolders are included as objects with keys ending with "/". These may be synthetic, with no size or
     * last modified date. A non-existent folder is reported as empty.
     */
    public List<S3Object> list(boolean includeFolders) {
        return list(includeFolders, null);
    }

    // "pageSize" is only exposed for pagination tests
    List<S3Object> list(boolean includeFolders, Integer pageSize) {

        ListObjectsV2Request.Builder request = ListObjectsV2Request.builder().bucket(bucket).prefix(prefix);
        if (!includeSubfolders) {
            request.delimiter("/");
        }

        if (pageSize != null) {
            request.maxKeys(pageSize);
        }

        // sorted by key, no duplicate folders
        Map<String, S3Object> objects = new TreeMap<>();

        try {
            for (ListObjectsV2Response page : clientSupplier().get().listObjectsV2Paginator(request.build())) {

                for (S3Object o : page.contents()) {
                    String key = o.key();
                    if (key.equals(prefix) || (!includeFolders && key.endsWith("/"))) {
                        continue;
                    }

                    // physical folder markers override synthetic ones
                    objects.put(key, o);

                    if (includeSubfolders && includeFolders) {
                        S3Keys.impliedFolders(prefix, key)
                                .forEach(f -> objects.putIfAbsent(f, folder(f)));
                    }
                }

                if (includeFolders) {
                    for (CommonPrefix cp : page.commonPrefixes()) {
                        objects.putIfAbsent(cp.prefix(), folder(cp.prefix()));
                    }
                }
            }
        } catch (SdkException e) {
            throw new RuntimeException("Error listing S3 folder: " + this, e);
        }

        return objects.values()
                .stream()
                .filter(combinedFilter(includeFolders))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return SCHEME + bucket + "/" + prefix;
    }

    private Supplier<S3Client> clientSupplier() {
        S3Client client = this.client;
        return client != null
                ? () -> client
                : S3DefaultClient.supplier();
    }

    private Predicate<S3Object> combinedFilter(boolean includeFolders) {
        Predicate<S3Object> typeFilter = includeFolders ? o -> true : o -> !o.key().endsWith("/");
        Predicate<S3Object> notHiddenFilter = includeHidden ? null : notHiddenFilter(prefix);

        return Stream.of(filter, extFilter, notHiddenFilter)
                .filter(Objects::nonNull)
                .reduce(typeFilter, Predicate::and);
    }

    private static S3Object folder(String key) {
        return S3Object.builder().key(key).build();
    }

    static Predicate<S3Object> extensionFilter(String ext) {
        Objects.requireNonNull(ext);
        if (ext.isEmpty()) {
            throw new IllegalArgumentException("Empty extension");
        }

        String normalizedExt = ext.startsWith(".") ? ext : "." + ext;
        return o -> o.key().endsWith(normalizedExt);
    }

    static Predicate<S3Object> notHiddenFilter(String prefix) {
        return o -> !S3Keys.isHidden(prefix, o.key());
    }
}
