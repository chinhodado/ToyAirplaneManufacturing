package manufacturing.action;

import manufacturing.Constants;
import manufacturing.ToyAirplaneManufacturing;
import manufacturing.Util;
import simulationModelling.ConditionalAction;

/**
 * A mover load bins into his trolley from the output areas of a station.
 */
public class LoadBin extends ConditionalAction {
    ToyAirplaneManufacturing model;

    public LoadBin(ToyAirplaneManufacturing model) {
        this.model = model;
    }

    public static boolean precondition(ToyAirplaneManufacturing model) {
        return model.udp.GetOutputAreaWithBin()[0] != Constants.NONE;
    }

    @Override
    public void actionEvent() {
        int[] ids = model.udp.GetOutputAreaWithBin();
        int stationType = ids[0];
        int stationId = ids[1];
        int moverId = ids[2];
        Util.logVerbose("LoadBin[" + stationType + ", " + stationId + "]");

        model.udp.LoadBin(moverId, stationType, stationId);
    }
}
