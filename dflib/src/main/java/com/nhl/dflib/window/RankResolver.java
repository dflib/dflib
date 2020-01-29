package com.nhl.dflib.window;

@FunctionalInterface
interface RankResolver {

    int resolve(int i, int rowIndex, int precedingRowIndex);
}
