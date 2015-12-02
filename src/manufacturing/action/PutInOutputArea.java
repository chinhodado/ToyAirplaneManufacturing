package manufacturing.action;

import manufacturing.Constants;
import manufacturing.ToyAirplaneManufacturing;
import manufacturing.entity.CastingStation;
import manufacturing.entity.Station;
import simulationModelling.ConditionalAction;

/**
 * Put planes and castings in bins at the output area of a station when there is space available at that output area.
 */
public class PutInOutputArea extends ConditionalAction {
    ToyAirplaneManufacturing model;

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
            Station station = model.rgStations[stationType][stationId];
            model.qIOAreas[Constants.OUT][stationType][stationId].spInsertQue(station.bin);
            station.bin = Constants.NO_BIN;
        }
        else {
            CastingStation station = model.rcCastingStations[stationId];
            model.qIOAreas[Constants.OUT][stationType][stationId].spInsertQue(station.bin);
            station.bin = Constants.NO_BIN;
        }
    }
}
