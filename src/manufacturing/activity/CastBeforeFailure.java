package manufacturing.activity;

import manufacturing.Constants;
import manufacturing.ToyAirplaneManufacturing;
import manufacturing.Util;
import manufacturing.entity.Bin;
import manufacturing.entity.CastingStation;
import simulationModelling.Activity;

/**
 * Creates casting at a casting station when the casting machine is not busy,
 * does not contain a casting and the casting time is greater than the time to failure.
 *
 */
public class CastBeforeFailure extends Activity {
    ToyAirplaneManufacturing model;
    int stationId;

    public CastBeforeFailure(ToyAirplaneManufacturing model) {
        this.model = model;
    }

    public static boolean precondition(ToyAirplaneManufacturing model) {
        return model.udp.StationReadyForCastingBeforeFailure() != Constants.NONE;
    }

    @Override
    public void startingEvent() {
        stationId = model.udp.StationReadyForCastingBeforeFailure();
        Util.logVerbose("CastBeforeFailure.startingEvent[" + stationId + "]");
        this.name = "C" + stationId;
        CastingStation station = model.rcCastingStations[stationId];
        station.busy = true;
        station.bin = new Bin();
        station.bin.planeType = station.planeType;
    }

    @Override
    protected double duration() {
        return model.rcCastingStations[stationId].timeToFailure;
    }

    @Override
    protected void terminatingEvent() {
        CastingStation station = model.rcCastingStations[stationId];
        station.castingTimeLeft = model.rvp.uStationWorkTime(Constants.CAST) - model.rcCastingStations[stationId].timeToFailure;
        station.timeToFailure = 0;
    }
}
