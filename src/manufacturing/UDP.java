package manufacturing;

import java.util.ArrayList;

import manufacturing.entity.Bin;
import manufacturing.entity.CastingStation;
import manufacturing.entity.IOArea;
import manufacturing.entity.Mover;
import manufacturing.entity.Station;

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
                IOArea currentInputArea = model.qIOAreas[Constants.IN][stationType][stationId];
                IOArea currentMinInputArea = model.qIOAreas[Constants.IN][stationType][stationIdWithMinInput];
                if (currentInputArea.getN() < currentMinInputArea.getN()) {
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
     * Unload a mover's bins into the input area of a station, one at a time
     * @param moverId The id of the mover
     * @param stationType The station type
     * @param stationId The station ID
     */
    public void UnloadBin(int moverId, int stationType, int stationId) {
        Mover mover = model.rgMovers[moverId];
        IOArea inputArea = model.qIOAreas[Constants.IN][stationType][stationId];
        boolean doneUnload = false;
        for (Bin bin : new ArrayList<Bin>(mover.binList)) { // clone-and-remove trick
            if (doneUnload) {
                break;
            }
            else if (stationType != Constants.COAT ||
                    (stationType == Constants.COAT && bin.planeType != Constants.SPITFIRE)) {
                inputArea.spInsertQue(bin);
                mover.binList.remove(bin);
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
                IOArea currentOutputArea = model.qIOAreas[Constants.OUT][stationType][stationId];
                IOArea currentMaxOutputArea = model.qIOAreas[Constants.OUT][stationType][stationIdWithMaxOutput];
                if (currentOutputArea.getN() > currentMaxOutputArea.getN()) {
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
        Mover mover = model.rgMovers[moverId];
        IOArea outputArea = model.qIOAreas[Constants.OUT][stationType][stationId];
        // this is just to be safe - if we are in here it means that we got a valid moverId
        // from GetMoverReadyForLoad already
        int numEmptySlots = Mover.MAX_NUM_BIN - mover.getN();
        boolean doneLoading = false;
        for (Bin bin : new ArrayList<Bin>(outputArea.binList)) { // clone-and-remove trick
            if (numEmptySlots <= 0 || doneLoading) {
                break;
            }
            else {
                mover.binList.add(bin);
                outputArea.binList.remove(bin);
                numEmptySlots--;
                doneLoading = true;
            }
        }
    }

    public int GetMoverReadyForUnload(int stationType) {
        for (Integer moverId : model.qLoadUnload[Constants.IN][stationType].moverList) {
            Mover mover = model.rgMovers[moverId];
            if (mover.getN() > 0) {
                if (stationType != Constants.COAT ||
                    (stationType == Constants.COAT && !mover.hasAllSpitfirePlanes())) {
                    return moverId;
                }
            }
        }
        return Constants.NONE;
    }

    public int GetMoverReadyForLoad(int stationType) {
        for (Integer moverId : model.qLoadUnload[Constants.OUT][stationType].moverList) {
            Mover mover = model.rgMovers[moverId];
            if (mover.getN() < Mover.MAX_NUM_BIN) {
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
            Mover mover = model.rgMovers[moverId];
            if (mover.getN() == Mover.MAX_NUM_BIN) {
                for (int stationType = Constants.CAST; stationType <= Constants.COAT; stationType++) {
                    if (model.qLoadUnload[Constants.OUT][stationType].contains(moverId)) {
                        return new int[] { moverId, stationType};
                    }
                }
            }
            else if (mover.getN() == 0) {
                if (model.qLoadUnload[Constants.IN][Constants.INSPECT_PACK].contains(moverId)) {
                    return new int[] { moverId, Constants.INSPECT_PACK};
                }
            }
        }
        return new int[] { Constants.NONE, Constants.NONE };
    }

    public int StationReadyForCasting() {
        for (int stationId = 0; stationId < model.rcCastingStations.length; stationId++) {
            CastingStation station = model.rcCastingStations[stationId];
            if (!station.busy &&
                station.timeToFailure != 0 &&
                ((station.bin == Constants.NO_BIN && model.castingsCreated[station.planeType] < Constants.NUM_CASTING_NEEDED[station.planeType]) ||
                 (station.bin != Constants.NO_BIN && station.castingTimeLeft > 0))) {
                return stationId;
            }
        }

        return Constants.NONE;
    }

    public int StationReadyForRepair() {
        for (int stationId = 0; stationId < model.rcCastingStations.length; stationId++) {
            CastingStation station = model.rcCastingStations[stationId];
            if (station.timeToFailure == 0) {
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
                Station station = model.rgStations[stationType][stationId];
                if (!station.busy &&
                    station.bin == Constants.NO_BIN &&
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
                    CastingStation station = model.rcCastingStations[stationId];
                    condition = condition && station.bin != Constants.NO_BIN &&
                            station.bin.planeType != Constants.NONE && !station.busy;
                }
                else {
                    Station station = model.rgStations[stationType][stationId];
                    condition = condition && station.bin != Constants.NO_BIN && !station.busy;
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
        Mover mover = model.rgMovers[moverId];
        boolean hasAllSpitfirePlanes = mover.hasAllSpitfirePlanes();

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
