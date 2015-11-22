package manufacturing.entity;

import java.util.ArrayList;

import manufacturing.Constants;

public class Mover {
    public ArrayList<Bin> binList = new ArrayList<Bin>();

    public static final int MAX_NUM_BIN = 3;

    /**
     * Get the number of bins this mover is holding
     * @return
     */
    public int getN() {
        return binList.size();
    }

    /**
     * Add a bin to this mover's trolley
     * @param comp
     */
    public void spInsertQue(Bin comp) {
        binList.add(comp);
    }

    /**
     * Get a bin from this mover's trolley
     * @return
     */
    public Bin spRemoveQue() {
        Bin comp = Constants.NO_BIN;
        if (binList.size() != 0)
            comp = binList.remove(0);
        return comp;
    }

    public boolean hasAllSpitfirePlanes() {
        // I miss C# and LINQ...
        for (Bin bin : binList) {
            if (bin.planeType != Constants.SPITFIRE) {
                return false;
            }
        }
        return true;
    }
}
