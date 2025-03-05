package org.dflib.s3;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class S3ByteSourceTest {

    @Test
    void uri() {
        S3ByteSource source = new S3ByteSource(mock(S3Client.class), "test-bucket", "test/key.txt");
        assertEquals("s3://test-bucket/test/key.txt", source.uri().orElse(null));
    }

    @Test
    void stream_nonExistentObject() {
        S3Client mockS3 = mock(S3Client.class);
        when(mockS3.getObject(any(GetObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().build());

        S3ByteSource source = new S3ByteSource(mockS3, "test-bucket", "non-existent.txt");
        assertThrows(RuntimeException.class, source::stream);
    }

    @Test
    void stream_s3Error() {
        S3Client mockS3 = mock(S3Client.class);
        when(mockS3.getObject(any(GetObjectRequest.class)))
                .thenThrow(SdkException.builder().build());

        S3ByteSource source = new S3ByteSource(mockS3, "test-bucket", "error.txt");
        assertThrows(RuntimeException.class, source::stream);
    }
}
