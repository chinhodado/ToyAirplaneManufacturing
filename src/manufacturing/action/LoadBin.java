package manufacturing.action;

import manufacturing.Constants;
import manufacturing.ToyAirplaneManufacturing;
import simulationModelling.ConditionalAction;

/**
 * A mover load a bin into his trolley from an output area of a station.
 */
public class LoadBin extends ConditionalAction {
    private ToyAirplaneManufacturing model;

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
        this.name = Constants.stationLabel[stationType] + "_" + stationId;
        model.udp.LoadBin(stationType, stationId);
    }
}
