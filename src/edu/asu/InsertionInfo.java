package edu.asu;
//Author : Mahendra Rao (mrrao1)
public class InsertionInfo {
    InsertionInfo(int insertIndex, boolean matched) {
        this.insertIndex = insertIndex;
        this.matched = matched;
    }

    public int getIndex() {
        return insertIndex;
    }

    public boolean getMatched() {
        return matched;
    }

    private final int insertIndex;
    private final boolean matched;
}
