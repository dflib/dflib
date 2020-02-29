package com.nhl.dflib.jdbc.connector.saver;

import com.nhl.dflib.Series;
import com.nhl.dflib.jdbc.SaveOp;
import com.nhl.dflib.join.JoinIndicator;
import com.nhl.dflib.accumulator.ObjectAccumulator;

import java.util.BitSet;

class UpsertInfoTracker {

    private int width;
    private int height;

    private Series<JoinIndicator> newOldJoin;
    private Series<BitSet> updatePositions;

    public UpsertInfoTracker(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Series<SaveOp> getInfo() {
        // chances are this method won't get called at all, so defer any calculation till this method is actually called
        return updatePositions != null ? insertsUpdatesAndSkips() : insertsAndSkips();
    }

    private Series<SaveOp> insertsAndSkips() {
        ObjectAccumulator<SaveOp> accum = new ObjectAccumulator<>(height);
        for (int i = 0; i < height; i++) {

            switch (newOldJoin.get(i)) {
                case both:
                    accum.add(SaveOp.skip);
                    break;
                case left_only:
                    accum.add(SaveOp.insert);
                    break;
                default:
                    throw new IllegalStateException("Unexpected join indicator: " + newOldJoin.get(i));
            }
        }
        return accum.toSeries();
    }

    private Series<SaveOp> insertsUpdatesAndSkips() {

        ObjectAccumulator<SaveOp> accum = new ObjectAccumulator<>(height);
        for (int i = 0, updatePos = 0; i < height; i++) {

            switch (newOldJoin.get(i)) {
                case both:
                    int c = updatePositions.get(updatePos).cardinality();
                    accum.add(c == width ? SaveOp.skip : SaveOp.update);
                    updatePos++;
                    break;
                case left_only:
                    accum.add(SaveOp.insert);
                    break;
                default:
                    throw new IllegalStateException("Unexpected join indicator: " + newOldJoin.get(i));
            }
        }
        return accum.toSeries();
    }

    public void insertAndUpdate(Series<JoinIndicator> newOldJoin) {
        this.newOldJoin = newOldJoin;
    }

    public void updatesCardinality(Series<BitSet> updatePositions) {
        this.updatePositions = updatePositions;
    }
}
