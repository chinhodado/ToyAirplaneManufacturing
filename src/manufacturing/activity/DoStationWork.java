package manufacturing.activity;

import manufacturing.Constants;
import manufacturing.ToyAirplaneManufacturing;
import simulationModelling.Activity;

/**
 * Depending on the type of station, do the cutting/grinding, coating or inspection/packaging.
 */
public class DoStationWork extends Activity {
    private ToyAirplaneManufacturing model;
    private int stationType;
    private int stationId;

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

        // Place identifier in name of behaviour object for logging, used by showSbl()
        this.name = Constants.stationLabel[stationType] + "_" + stationId;

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
        model.rgStations[stationType][stationId].busy = false;

        // We opted to leave the model as-is and don't add in the 88% pass rate, since adding it would destroy
        // some existing assumptions in the current model, such as the casting stations can't stop making castings
        // when the casting quota is reached anymore.
        if (stationType == Constants.INSPECT_PACK) {
            switch (model.rgStations[stationType][stationId].bin.planeType) {
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
                System.out.printf("DoStationWork.terminatingEvent: Invalid plane type: %d\n", model.rgStations[stationType][stationId].bin.planeType);
                break;
            }

            model.rgStations[stationType][stationId].bin = Constants.NO_BIN;
        }
    }
}
