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
     * that has the most empty space for a mover to load his bin into
     * @return [stationType, stationId], or [NONE, NONE] if no input area satisfies the condition
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
            if (model.qIOAreas[Constants.IN][stationType][stationIdWithMinInput].getN() < 5 &&
                model.qLoadUnload[Constants.IN][stationType].getN() > 0) {
                return new int[] {stationType, stationIdWithMinInput};
            }
        }
        return new int[] { Constants.NONE, Constants.NONE };
    }

    /**
     * Unload a mover's bins into the input area of a station, one at a time.
     * @param stationType The station type
     * @param stationId The station ID
     */
    public int UnloadBin(int stationType, int stationId) {
        int moverId = model.qLoadUnload[Constants.IN][stationType].moverList.get(0);
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
        return moverId;
    }

    /**
     * Get the the output area of a station with stationType and stationId
     * that has a bin for a mover to load into his trolley
     * @return [stationType, stationId], or [NONE, NONE] if no output area satisfies the condition
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
            if (model.qIOAreas[Constants.OUT][stationType][stationIdWithMaxOutput].getN() > 0 &&
                model.qLoadUnload[Constants.OUT][stationType].getN() > 0) {
                return new int[] {stationType, stationIdWithMaxOutput};
            }
        }
        return new int[] { Constants.NONE, Constants.NONE };
    }

    /**
     * Load a bin from an output area into a mover's trolley, one at a time
     * @param stationType The station type
     * @param stationId The station ID
     */
    public void LoadBin(int stationType, int stationId) {
        int moverId = model.qLoadUnload[Constants.OUT][stationType].moverList.get(0);
        // this is just to be safe
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
     * Get a mover ready for moving, and the stationType of where the mover currently is
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

    /**
     * Get a casting station ready for casting
     * @return The stationId of the casting station, or NONE.
     */
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
     * @return [stationType, stationId] or [NONE, NONE]
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
        boolean hasAllSpitfirePlanes = HasAllSpitfirePlanes(moverId);

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

    /**
     * Return true if the mover has all Spitfire bins, and false otherwise
     * @param moverId The moverId
     * @return true if the mover has all Spitfire bins, and false otherwise
     */
    public boolean HasAllSpitfirePlanes(int moverId) {
        for (Bin bin : model.rgMovers[moverId].binList) {
            if (bin.planeType != Constants.SPITFIRE) {
                return false;
            }
        }
        return true;
    }
}
