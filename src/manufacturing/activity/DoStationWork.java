package manufacturing.activity;

import manufacturing.Constants;
import manufacturing.ToyAirplaneManufacturing;
import manufacturing.Util;
import manufacturing.entity.Station;
import simulationModelling.Activity;

/**
 * Depending on the type of station, do the cutting/grinding, coating or inspection/packaging.
 */
public class DoStationWork extends Activity {
    ToyAirplaneManufacturing model;
    int stationType;
    int stationId;

    public DoStationWork(ToyAirplaneManufacturing model) {
        this.model = model;
    }

    public static boolean precondition(ToyAirplaneManufacturing model) {
        // just checking the stationType is enough. If stationType is NONE, that
        // means no station is ready anyway.
        return model.udp.StationReadyForWork()[0] != Constants.NONE;
    }

    @Override
    public void startingEvent() {
        // identify the station
        int[] ids = model.udp.StationReadyForWork();
        stationType = ids[0];
        stationId = ids[1];
        Util.logVerbose("DoStationWork.startingEvent[" + stationType + ", " + stationId + "]");

        // Place identifier in name of behaviour object for logging, used by showSbl()
        this.name = "S" + stationType + "_" + stationId;

        // Set station to busy and move bin from input area to station
        model.rgStations[stationType][stationId].busy = true;
        model.rgStations[stationType][stationId].bin = model.qIOAreas[Constants.IN][stationType][stationId].spRemoveQue();
    }

    @Override
    public double duration() {
        return model.rvp.uStationWorkTime(stationType);
    }

    @Override
    public void terminatingEvent() {
        Station station = model.rgStations[stationType][stationId];
        station.busy = false;

        if (stationType == Constants.INSPECT_PACK) {
            switch (station.bin.planeType) {
            case Constants.SPITFIRE:
                model.output.numSpitfireProducedDaily += 24;
                break;
            case Constants.F16:
                model.output.numF16ProducedDaily += 24;
                break;
            case Constants.CONCORDE:
                model.output.numConcordeProducedDaily += 24;
                break;
            default:
                System.out.printf("DoStationWork.terminatingEvent: Invalid plane type: %d\n", station.bin.planeType);
                break;
            }

            station.bin = Constants.NO_BIN;
        }
    }
}
