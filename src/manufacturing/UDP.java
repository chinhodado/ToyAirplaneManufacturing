package manufacturing;

import java.util.ArrayList;

import manufacturing.entity.Bin;
import manufacturing.entity.Mover;

/**
 * User Defined Procedures
 */
public class UDP {
    ToyAirplaneManufacturing model;

    // Constructor
    protected UDP(ToyAirplaneManufacturing model) {
        this.model = model;
    }

    /**
     * Get the the input area of a station with stationType and stationId
     * that has the most empty space for a mover to load his bin into, and the moverId of that mover
     * @return [stationType, stationId, moverId], or [NONE, NONE, NONE] if no input area satisfies the condition
     */
    public int[] GetInputAreaWithEmptySpace() {
        for (int stationType = Constants.CUT_GRIND; stationType <= Constants.INSPECT_PACK; stationType++) {
            // the stationId of the station with an input area that can hold the most bins,
            // i.e. holding the least number of bins
            // alternatively, we can use a priority queue, but nah...
            int stationIdWithMinInput = 0;
            for (int stationId = 0; stationId < model.numStations[stationType]; stationId++) {
                if (model.qIOAreas[Constants.IN][stationType][stationId].getN() < model.qIOAreas[Constants.IN][stationType][stationIdWithMinInput].getN()) {
                    stationIdWithMinInput = stationId;
                }
            }
            int moverId = GetMoverReadyForUnload(stationType);
            if (model.qIOAreas[Constants.IN][stationType][stationIdWithMinInput].getN() < 5 &&
                moverId != Constants.NONE) {
                return new int[] {stationType, stationIdWithMinInput, moverId};
            }
        }
        return new int[] { Constants.NONE, Constants.NONE, Constants.NONE };
    }

    /**
     * Unload a mover's bins into the input area of a station, one at a time.
     * We need to be given the moverId since we can't just take the mover from the top of the queue
     * - for example, a mover at the unload queue of the inspect/pack area may have already unloaded
     * all of his bins and have no bin left, but he is still in the queue since the activity MovePlanes
     * have not started yet.
     * @param moverId The id of the mover
     * @param stationType The station type
     * @param stationId The station ID
     */
    public void UnloadBin(int moverId, int stationType, int stationId) {
        boolean doneUnload = false;
        // make a copy of existing list and iterate over the new copy, while removing from the
        // original list if needed
        for (Bin bin : new ArrayList<Bin>(model.rgMovers[moverId].binList)) {
            if (doneUnload) {
                break;
            }
            else if (stationType != Constants.COAT ||
                    (stationType == Constants.COAT && bin.planeType != Constants.SPITFIRE)) {
                model.qIOAreas[Constants.IN][stationType][stationId].spInsertQue(bin);
                model.rgMovers[moverId].binList.remove(bin);
                doneUnload = true;
            }
        }
    }

    /**
     * Get the the output area of a station with stationType and stationId
     * that has a bin for a mover to load into his trolley, and the moverId of that mover
     * @return [stationType, stationId, moverId], or [NONE, NONE, NONE] if no output area satisfies the condition
     */
    public int[] GetOutputAreaWithBin() {
        for (int stationType = Constants.CAST; stationType <= Constants.COAT; stationType++) {
            // the stationId of the station with an output area that has the most bins,
            int stationIdWithMaxOutput = 0;
            for (int stationId = 0; stationId < model.numStations[stationType]; stationId++) {
                if (model.qIOAreas[Constants.OUT][stationType][stationId].getN() > model.qIOAreas[Constants.OUT][stationType][stationIdWithMaxOutput].getN()) {
                    stationIdWithMaxOutput = stationId;
                }
            }
            int moverId = GetMoverReadyForLoad(stationType);
            if (model.qIOAreas[Constants.OUT][stationType][stationIdWithMaxOutput].getN() > 0 &&
                moverId != Constants.NONE) {
                return new int[] {stationType, stationIdWithMaxOutput, moverId};
            }
        }
        return new int[] { Constants.NONE, Constants.NONE, Constants.NONE };
    }

    /**
     * Load a bin from an output area into a mover's trolley, one at a time
     * @param moverId The id of the mover
     * @param stationType The station type
     * @param stationId The station ID
     */
    public void LoadBin(int moverId, int stationType, int stationId) {
        // this is just to be safe - if we are in here it means that we got a valid moverId
        // from GetMoverReadyForLoad already
        int numEmptySlots = Mover.MAX_NUM_BIN - model.rgMovers[moverId].getN();
        boolean doneLoading = false;
        // make a copy of existing list and iterate over the new copy, while removing from the
        // original list if needed
        for (Bin bin : new ArrayList<Bin>(model.qIOAreas[Constants.OUT][stationType][stationId].binList)) {
            if (numEmptySlots <= 0 || doneLoading) {
                break;
            }
            else {
                model.rgMovers[moverId].binList.add(bin);
                model.qIOAreas[Constants.OUT][stationType][stationId].binList.remove(bin);
                numEmptySlots--;
                doneLoading = true;
            }
        }
    }

    /**
     * Get a mover ready for unloading bin at the unloading queue of a station type
     * @param stationType The station type
     * @return The moverId of the ready mover
     */
    private int GetMoverReadyForUnload(int stationType) {
        for (Integer moverId : model.qLoadUnload[Constants.IN][stationType].moverList) {
            if (model.rgMovers[moverId].getN() > 0) {
                if (stationType != Constants.COAT ||
                    (stationType == Constants.COAT && !model.rgMovers[moverId].hasAllSpitfirePlanes())) {
                    return moverId;
                }
            }
        }
        return Constants.NONE;
    }

    /**
     * Get a mover ready for loading bin at the loading queue of a station type
     * @param stationType The station type
     * @return The moverId of the ready mover
     */
    private int GetMoverReadyForLoad(int stationType) {
        for (Integer moverId : model.qLoadUnload[Constants.OUT][stationType].moverList) {
            if (model.rgMovers[moverId].getN() < Mover.MAX_NUM_BIN) {
                return moverId;
            }
        }
        return Constants.NONE;
    }

    /**
     *
     * @return [moverId, stationType], or [NONE, NONE]
     */
    public int[] MoverReadyForMoving() {
        for (int moverId = 0; moverId < model.rgMovers.length; moverId++) {
            if (model.rgMovers[moverId].getN() == Mover.MAX_NUM_BIN) {
                for (int stationType = Constants.CAST; stationType <= Constants.COAT; stationType++) {
                    if (model.qLoadUnload[Constants.OUT][stationType].contains(moverId)) {
                        return new int[] { moverId, stationType};
                    }
                }
            }
            else if (model.rgMovers[moverId].getN() == 0) {
                if (model.qLoadUnload[Constants.IN][Constants.INSPECT_PACK].contains(moverId)) {
                    return new int[] { moverId, Constants.INSPECT_PACK};
                }
            }
        }
        return new int[] { Constants.NONE, Constants.NONE };
    }

    public int StationReadyForCasting() {
        for (int stationId = 0; stationId < model.rcCastingStations.length; stationId++) {
            if (!model.rcCastingStations[stationId].busy &&
                model.rcCastingStations[stationId].timeToFailure != 0 &&
                ((model.rcCastingStations[stationId].bin == Constants.NO_BIN &&
                    model.getClock() < model.endTime &&
                    model.output.castingsCreated[model.rcCastingStations[stationId].planeType] < Constants.NUM_CASTING_NEEDED[model.rcCastingStations[stationId].planeType]) ||
                 (model.rcCastingStations[stationId].bin != Constants.NO_BIN && model.rcCastingStations[stationId].castingTimeLeft > 0))) {
                return stationId;
            }
        }

        return Constants.NONE;
    }

    /**
     * Get the id and station type of a station that is ready for work
     * @return [stationType, stationId] of a station that is ready for work,
     *         or [NONE, NONE] if no station is ready
     */
    public int[] StationReadyForWork() {
        for (int stationType = Constants.CUT_GRIND; stationType <= Constants.INSPECT_PACK; stationType++) {
            for (int stationId = 0; stationId < model.numStations[stationType]; stationId++) {
                if (!model.rgStations[stationType][stationId].busy &&
                    model.rgStations[stationType][stationId].bin == Constants.NO_BIN &&
                    model.qIOAreas[Constants.IN][stationType][stationId].getN() > 0) {
                    return new int[] { stationType, stationId };
                }
            }
        }

        return new int[] { Constants.NONE, Constants.NONE };
    }

    /**
     * Get an output area ready for putting bin in
     * @return
     */
    public int[] GetReadyOutputArea() {
        for (int stationType = Constants.CAST; stationType <= Constants.COAT; stationType++) {
            for (int stationId = 0; stationId < model.numStations[stationType]; stationId++) {
                boolean condition = model.qIOAreas[Constants.OUT][stationType][stationId].getN() < 5;
                if (stationType == Constants.CAST) {
                    condition = condition && model.rcCastingStations[stationId].bin != Constants.NO_BIN &&
                            model.rcCastingStations[stationId].bin.planeType != Constants.NONE &&
                            !model.rcCastingStations[stationId].busy;
                }
                else {
                    condition = condition && model.rgStations[stationType][stationId].bin != Constants.NO_BIN &&
                            !model.rgStations[stationType][stationId].busy;
                }

                if (condition) {
                    return new int[] { stationType, stationId };
                }
            }
        }

        return new int[] { Constants.NONE, Constants.NONE };
    }

    /**
     * Move the mover from one station type to another
     * @param moverId The mover id
     * @param currentStationType The current station type
     * @return The next station type
     */
    public int MovePlanes(int moverId, int currentStationType) {
        boolean hasAllSpitfirePlanes = model.rgMovers[moverId].hasAllSpitfirePlanes();

        int nextStationType = Constants.NONE;

        switch (currentStationType) {
        case Constants.CAST:
            nextStationType = Constants.CUT_GRIND;
            break;
        case Constants.CUT_GRIND:
            if (hasAllSpitfirePlanes) {
                nextStationType = Constants.INSPECT_PACK;
            }
            else {
                nextStationType = Constants.COAT;
            }
            break;
        case Constants.COAT:
            nextStationType = Constants.INSPECT_PACK;
            break;
        case Constants.INSPECT_PACK:
            nextStationType = Constants.CAST;
            break;
        default:
            System.out.printf("UDP.MovePlanes: Invalid station type: %d\n", currentStationType);
            break;
        }

        return nextStationType;
    }
}
