package org.dflib.s3.unit;

import org.dflib.s3.S3;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * A superclass of the integration tests that require S3. The tests are skipped if Docker is not available.
 */
public abstract class BaseS3Test {

    @BeforeAll
    static void checkDocker() {
        assumeTrue(S3TestContainer.dockerAvailable(), "Docker is not available. Skipping S3 tests");
    }

    /**
     * Returns an S3 folder within the test bucket configured with the test client.
     */
    protected static S3 s3(String path) {
        return S3.of(S3TestContainer.BUCKET).path(path).client(S3TestContainer.client());
    }
}
