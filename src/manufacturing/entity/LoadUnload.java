package manufacturing.entity;

import java.util.ArrayList;

import manufacturing.Constants;

/**
 * Given that this is a simple queue, we could have simply use the ArrayList class directly.
 * However we want to make it consistent (one class per entity)
 */
public class LoadUnload {
    // Implement the queue using an ArrayList object
    // Size is initialized to 0
    public ArrayList<Integer> moverList = new ArrayList<Integer>();

    /**
     * Get the number of movers this queue is holding
     * @return
     */
    public int getN() {
        return moverList.size();
    }

    /**
     * Add a mover to this queue
     * @param moverId
     */
    public void spInsertQue(Integer moverId) {
        moverList.add(moverId);
    }

    /**
     * Get a mover from this queue
     * @return
     */
    public int spRemoveQue() {
        Integer moverId = Constants.NONE;
        if (moverList.size() != 0)
            moverId = moverList.remove(0);
        return moverId;
    }

    public boolean spRemoveQue(int moverId) {
        return moverList.remove(new Integer(moverId));
    }

    public boolean contains(int moverId) {
        return moverList.contains(moverId);
    }
}
