package manufacturing.action;

import manufacturing.Constants;
import manufacturing.ToyAirplaneManufacturing;
import manufacturing.Util;
import simulationModelling.ConditionalAction;

/**
 * A mover unload his bins into the input areas of a station when there are free spaces available.
 */
public class UnloadBin extends ConditionalAction {
    ToyAirplaneManufacturing model;

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
        Util.logVerbose("UnloadBin[" + stationType + ", " + stationId + "]");

        model.qLoadUnload[Constants.IN][stationType].spRemoveQue(moverId);
        model.udp.UnloadBin(moverId, stationType, stationId);
        if (model.rgMovers[moverId].getN() == 0) {
            if (stationType != Constants.INSPECT_PACK) {
                // move to the loading queue
                model.qLoadUnload[Constants.OUT][stationType].spInsertQue(moverId);
            }
        }
        else {
            // reinsert mover to current queue
            model.qLoadUnload[Constants.IN][stationType].spInsertQue(moverId);
        }
    }
}
