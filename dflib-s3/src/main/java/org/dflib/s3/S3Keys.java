package org.dflib.s3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Helpers for manipulating S3 keys and folder prefixes. A folder prefix is either an empty String or a String ending
 * with "/".
 *
 * @since 2.0.0
 */
class S3Keys {

    static String toFolderPrefix(String path) {
        return path.isEmpty() || path.endsWith("/") ? path : path + "/";
    }

    static String appendPath(String prefix, String subfolder) {
        Objects.requireNonNull(subfolder);
        if (subfolder.startsWith("/")) {
            throw new IllegalArgumentException("Subfolder path can not start with '/': " + subfolder);
        }

        return prefix + toFolderPrefix(subfolder);
    }

    static void checkRelativeKey(String key) {
        Objects.requireNonNull(key);
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Empty key");
        }

        if (key.startsWith("/")) {
            throw new IllegalArgumentException("Key can not start with '/': " + key);
        }

        if (key.endsWith("/")) {
            throw new IllegalArgumentException("Key can not end with '/', as it denotes a folder: " + key);
        }
    }

    static String relativize(String prefix, String key) {
        if (!key.startsWith(prefix)) {
            throw new IllegalArgumentException("Key '" + key + "' is not in the folder '" + prefix + "'");
        }

        return key.substring(prefix.length());
    }

    /**
     * Returns folder prefixes located between the folder prefix and the key. E.g., for "p/" and "p/a/b/c.csv", the
     * result is ["p/a/", "p/a/b/"].
     */
    static List<String> impliedFolders(String prefix, String key) {
        List<String> folders = new ArrayList<>(2);

        int from = prefix.length();
        int slash;
        while ((slash = key.indexOf('/', from)) >= 0 && slash < key.length() - 1) {
            folders.add(key.substring(0, slash + 1));
            from = slash + 1;
        }

        return folders;
    }

    /**
     * Checks whether any part of the key relative to the folder prefix starts with a dot.
     */
    static boolean isHidden(String prefix, String key) {
        String relative = relativize(prefix, key);

        int from = 0;
        int len = relative.length();
        while (from < len) {
            if (relative.charAt(from) == '.') {
                return true;
            }

            int slash = relative.indexOf('/', from);
            if (slash < 0) {
                break;
            }

            from = slash + 1;
        }

        return false;
    }
}
