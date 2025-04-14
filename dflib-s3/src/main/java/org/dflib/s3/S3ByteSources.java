package org.dflib.s3;

import org.dflib.ByteSource;
import org.dflib.ByteSources;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * A {@link ByteSource} implementation that reads data from Amazon S3.
 *
 * @since 2.0.0
 */
class S3ByteSources implements ByteSources {

    private final S3 s3;

    public S3ByteSources(S3 s3) {
        this.s3 = s3;
    }

    @Override
    public <T> Map<String, T> process(BiFunction<String, ByteSource, T> processor) {
        return s3.list().stream()
                .collect(Collectors.toMap(S3Object::key, o -> processor.apply(o.key(), s3.source(o.key()))));
    }
}
