package manufacturing.action;

import manufacturing.Constants;
import manufacturing.ToyAirplaneManufacturing;
import simulationModelling.ConditionalAction;

/**
 * Put a bin into the output area of a station when there is space available at that output area.
 */
public class PutInOutputArea extends ConditionalAction {
    private ToyAirplaneManufacturing model;

    public PutInOutputArea(ToyAirplaneManufacturing model) {
        this.model = model;
    }

    public static boolean precondition(ToyAirplaneManufacturing model) {
        return model.udp.GetReadyOutputArea()[0] != Constants.NONE;
    }

    @Override
    public void actionEvent() {
        int[] ids = model.udp.GetReadyOutputArea();
        int stationType = ids[0];
        int stationId = ids[1];
        this.name = Constants.stationLabel[stationType] + "_" + stationId;
        if (stationType != Constants.CAST) {
            model.qIOAreas[Constants.OUT][stationType][stationId].spInsertQue(model.rgStations[stationType][stationId].bin);
            model.rgStations[stationType][stationId].bin = Constants.NO_BIN;
        }
        else {
            model.qIOAreas[Constants.OUT][stationType][stationId].spInsertQue(model.rcCastingStations[stationId].bin);
            model.rcCastingStations[stationId].bin = Constants.NO_BIN;
        }
    }
}
