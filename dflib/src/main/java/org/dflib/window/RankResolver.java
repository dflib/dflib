package org.dflib.window;

@FunctionalInterface
interface RankResolver {

    int resolve(int i, int rowIndex, int precedingRowIndex);
}
