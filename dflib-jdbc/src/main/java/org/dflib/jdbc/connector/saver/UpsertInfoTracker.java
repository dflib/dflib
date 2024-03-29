package org.dflib.jdbc.connector.saver;

import org.dflib.Series;
import org.dflib.jdbc.SaveOp;
import org.dflib.join.JoinIndicator;
import org.dflib.builder.ObjectAccum;

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
        ObjectAccum<SaveOp> accum = new ObjectAccum<>(height);
        for (int i = 0; i < height; i++) {

            switch (newOldJoin.get(i)) {
                case both:
                    accum.push(SaveOp.skip);
                    break;
                case left_only:
                    accum.push(SaveOp.insert);
                    break;
                default:
                    throw new IllegalStateException("Unexpected join indicator: " + newOldJoin.get(i));
            }
        }
        return accum.toSeries();
    }

    private Series<SaveOp> insertsUpdatesAndSkips() {

        ObjectAccum<SaveOp> accum = new ObjectAccum<>(height);
        for (int i = 0, updatePos = 0; i < height; i++) {

            switch (newOldJoin.get(i)) {
                case both:
                    int c = updatePositions.get(updatePos).cardinality();
                    accum.push(c == width ? SaveOp.skip : SaveOp.update);
                    updatePos++;
                    break;
                case left_only:
                    accum.push(SaveOp.insert);
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
