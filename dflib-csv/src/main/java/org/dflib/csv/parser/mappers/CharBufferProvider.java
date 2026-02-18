package org.dflib.csv.parser.mappers;

import java.util.function.IntFunction;

@FunctionalInterface
interface CharBufferProvider extends IntFunction<char[]> {

    static CharBufferProvider singleton() {
        return new SingletonProvider();
    }

    /**
     * Single instance of the char buffer, with on-demand growing
     */
    final class SingletonProvider implements CharBufferProvider {

        private char[] instance = new char[256];

        @Override
        public char[] apply(int length) {
            if (length > instance.length) {
                instance = new char[length];
            }
            return instance;
        }
    }
}
