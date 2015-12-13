package manufacturing.entity;

import java.util.ArrayList;

import manufacturing.Constants;

/**
 * Given that this is a simple queue, we could have simply use the ArrayList class directly.
 * However we want to make it consistent (one class per entity)
 */
public class RepairQueue {
    // Implement the queue using an ArrayList object
    // Size is initialized to 0
    private ArrayList<Integer> castingStationList = new ArrayList<Integer>();

    /**
     * Get the number of casting stations this queue is holding
     * @return The size of the queue
     */
    public int getN() {
        return castingStationList.size();
    }

    /**
     * Add a casting station to this queue
     * @param stationId The station id to insert
     */
    public void spInsertQue(Integer stationId) {
        castingStationList.add(stationId);
    }

    /**
     * Get a station from this queue
     * @return The stationId at the head of the queue, or NONE if the queue is empty
     */
    public int spRemoveQue() {
        Integer stationId = Constants.NONE;
        if (castingStationList.size() != 0)
            stationId = castingStationList.remove(0);
        return stationId;
    }
}
