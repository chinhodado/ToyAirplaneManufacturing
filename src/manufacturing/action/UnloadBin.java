package manufacturing.action;

import manufacturing.Constants;
import manufacturing.ToyAirplaneManufacturing;
import simulationModelling.ConditionalAction;

/**
 * A mover unload his bins into the input areas of a station when there are free spaces available.
 */
public class UnloadBin extends ConditionalAction {
    private ToyAirplaneManufacturing model;

    public UnloadBin(ToyAirplaneManufacturing model) {
        this.model = model;
    }

    public static boolean precondition(ToyAirplaneManufacturing model) {
        return model.udp.GetInputAreaWithEmptySpace()[0] != Constants.NONE;
    }

    @Override
    public void actionEvent() {
        int[] ids = model.udp.GetInputAreaWithEmptySpace();
        int stationType = ids[0];
        int stationId = ids[1];
        int moverId = ids[2];
        this.name = Constants.stationLabel[stationType] + "_" + stationId;
        model.udp.UnloadBin(moverId, stationType, stationId);

        // if mover has no more bins left
        if (model.rgMovers[moverId].getN() == 0) {
            if (stationType != Constants.INSPECT_PACK) {
                // move to the loading queue
                model.qLoadUnload[Constants.IN][stationType].spRemoveQue(moverId);
                model.qLoadUnload[Constants.OUT][stationType].spInsertQue(moverId);
            }
        }
        else {
            if (model.udp.HasAllSpitfirePlanes(moverId) && stationType == Constants.COAT) {
                model.qLoadUnload[Constants.IN][stationType].spRemoveQue(moverId);
                model.qLoadUnload[Constants.OUT][stationType].spInsertQue(moverId);
            }
        }
    }
}
