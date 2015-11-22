package manufacturing.entity;

import java.util.ArrayList;

import manufacturing.Constants;

public class IOArea {
    // Implement the queue using an ArrayList object
    // Size is initialized to 0
    public ArrayList<Bin> binList = new ArrayList<Bin>();

    public int length = 5;

    /**
     * Get the number of bins this input/output area is holding
     * @return
     */
    public int getN() {
        return binList.size();
    }

    /**
     * Add a bin to this input/output area
     * @param comp
     */
    public void spInsertQue(Bin comp) {
        binList.add(comp);
    }

    /**
     * Get a bin from this input/output area
     * @return
     */
    public Bin spRemoveQue() {
        Bin comp = Constants.NO_BIN;
        if (binList.size() != 0)
            comp = binList.remove(0);
        return comp;
    }
}
