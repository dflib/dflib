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
 * Represents a "folder" in S3-compatible storage, i.e., a bucket plus an optional key prefix. Allows to
 * work with multiple objects in the folder as {@link ByteSources}.
 * And S3 object is immutable. All the methods that change its configuration return a new instance.
 * <p>
 * This connector treats any key prefix ending with "/" or objects with keys ending with "/" are treated
 * as folders as well, and are never returned as data sources.
 * <p>
 * Unless an explicit client is provided via {@link #client(S3Client)}, a shared default client is created lazily on
 * first access to S3. It takes its region and credentials from the standard AWS SDK configuration chains (environment
 * variables, system properties, "~/.aws/config", etc.).
 *
 * @since 2.0.0
 */
public class S3 {

    private static final String SCHEME = "s3://";

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

        // can be null, in which case the default client is used
        this.client = client;

        this.includeSubfolders = includeSubfolders;
        this.filter = filter;
        this.extFilter = extFilter;
        this.includeHidden = includeHidden;
    }

    /**
     * Creates an S3 folder from a location String that contains a bucket name and an optional key prefix. The location
     * may start with the "s3://" scheme or omit it. The prefix is treated as a folder, and a trailing slash is added to
     * it if missing. E.g., "s3://my-bucket", "s3://my-bucket/data/2024", "my-bucket/data/2024/" are all valid
     * locations. This method doesn't access S3 and doesn't require AWS configuration.
     */
    public static S3 of(String location) {
        Objects.requireNonNull(location);

        String path = location.startsWith(SCHEME) ? location.substring(SCHEME.length()) : location;
        if (path.contains("://")) {
            throw new IllegalArgumentException("Unsupported location scheme. Only '" + SCHEME + "' is allowed: " + location);
        }

        int slash = path.indexOf('/');
        String bucket = slash < 0
                ? path
                : path.substring(0, slash);
        if (bucket.isEmpty()) {
            throw new IllegalArgumentException("No bucket name in location: " + location);
        }

        String prefix = slash < 0
                ? ""
                : S3Keys.toFolderPrefix(path.substring(slash + 1));
        return new S3(bucket, prefix, null, false, null, null, false);
    }

    public String getBucket() {
        return bucket;
    }

    /**
     * Returns the key prefix of this folder. It is either an empty String (for the bucket root), or a String ending
     * with "/".
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Returns a new S3 folder with an explicitly provided S3Client. The caller is responsible for the client
     * lifecycle (e.g., closing it when no longer needed). If this method is not called, a shared default client is
     * used.
     */
    public S3 client(S3Client client) {
        Objects.requireNonNull(client);
        return new S3(bucket, prefix, client, includeSubfolders, filter, extFilter, includeHidden);
    }

    /**
     * Returns an instance of S3 that is a subfolder of this folder. S3 keys are not normalized, so "." and ".."
     * are treated as literal names.
     */
    public S3 path(String subfolder) {
        String subPrefix = S3Keys.appendPath(prefix, subfolder);
        return subPrefix.equals(prefix)
                ? this
                : new S3(bucket, subPrefix, client, includeSubfolders, filter, extFilter, includeHidden);
    }

    /**
     * Returns an instance of S3 that will only include objects that match the provided predicate. Any other
     * preconfigured filters (specific extensions or inclusion of hidden objects) are also applied. The predicate
     * receives objects with full keys.
     */
    public S3 include(Predicate<S3Object> filter) {
        Objects.requireNonNull(filter);
        return new S3(bucket, prefix, client, includeSubfolders, filter, extFilter, includeHidden);
    }

    /**
     * Returns an instance of S3 that will include "hidden" objects. An object is considered hidden if any part of its
     * key relative to this folder starts with a dot.
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
     * Returns a single object within the folder. The key is relative to the folder. Since the key is explicit, the
     * object doesn't need to match folder filters. E.g., a hidden object can be resolved even if the folder listing is
     * not showing them. Same goes for extension filters, etc. This method does not access S3, so the object existence
     * is only checked when the source is read.
     */
    public ByteSource source(String key) {
        S3Keys.checkRelativeKey(key);
        return new S3ByteSource(clientSupplier(), bucket, prefix + key);
    }

    /**
     * Returns all objects contained in the folder represented as {@link ByteSources}. The contents are filtered
     * according to S3 configuration. The map keys are relative to this folder. S3 is accessed immediately to obtain
     * the object list, while reading the objects is deferred until the sources are processed.
     */
    public ByteSources sources() {
        Supplier<S3Client> clientSupplier = clientSupplier();
        Map<String, ByteSource> sources = new LinkedHashMap<>();

        // The map key must be relative to the folder, whereas the ByteSource must use the full key
        list().forEach(o -> sources.put(
                S3Keys.relativize(prefix, o.key()),
                new S3ByteSource(clientSupplier, bucket, o.key())));

        return ByteSources.of(sources);
    }

    public List<S3Object> list() {
        return list(false);
    }

    /**
     * Returns the list of all objects contained in the folder, filtered according to S3 configuration and sorted by
     * key. The objects have full keys. If "includeFolders" is true, subfolders are included as objects with keys
     * ending with "/". If there's no physical "folder marker" object for a given subfolder, it is represented by a
     * synthetic object that has no size, last modified date, etc. The folder itself is never included. A
     * non-existent folder is reported as empty.
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

        // sorts by key and ensures that each folder is only included once
        Map<String, S3Object> objects = new TreeMap<>();

        try {
            for (ListObjectsV2Response page : clientSupplier().get().listObjectsV2Paginator(request.build())) {

                for (S3Object o : page.contents()) {
                    String key = o.key();

                    if (key.equals(prefix)) {
                        continue;
                    }

                    // "put" ensures that physical folder markers replace any synthetic folders added earlier
                    objects.put(key, o);

                    if (includeSubfolders) {
                        S3Keys.impliedFolders(prefix, key)
                                .forEach(f -> objects.putIfAbsent(f, folder(f)));
                    }
                }

                for (CommonPrefix cp : page.commonPrefixes()) {
                    objects.putIfAbsent(cp.prefix(), folder(cp.prefix()));
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
        return Stream.of(filter, extFilter, includeHidden
                        ? null
                        : notHiddenFilter(prefix)).filter(Objects::nonNull).reduce(typeFilter, Predicate::and);
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
