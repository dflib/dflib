package org.dflib;

import org.dflib.print.InlinePrinter;
import org.dflib.print.Printer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

public class EnvironmentTest {

    @Test
    void resetEnv() {
        Printer p = new InlinePrinter();

        Environment defaultEnv = Environment.commonEnv();
        try {
            assertNotSame(p, Environment.commonEnv().printer());
            Environment.setPrinter(p);
            assertSame(p, Environment.commonEnv().printer());
        } finally {
            Environment.setCommonEnv(defaultEnv);
        }
    }
}
