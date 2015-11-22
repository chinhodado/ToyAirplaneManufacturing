package manufacturing.activity;

import manufacturing.Constants;
import manufacturing.ToyAirplaneManufacturing;
import manufacturing.Util;
import manufacturing.entity.CastingStation;
import simulationModelling.Activity;

/**
 * Resume casting at a casting station after it was repaired *
 */
public class ResumeCast extends Activity{
    ToyAirplaneManufacturing model;
    int stationId;

    public ResumeCast(ToyAirplaneManufacturing model) {
        this.model = model;
    }

    public static boolean precondition(ToyAirplaneManufacturing model) {
        return model.udp.StationReadyForResumeCast() != Constants.NONE;
    }

    @Override
    public void startingEvent() {
        stationId = model.udp.StationReadyForResumeCast();
        Util.logVerbose("ResumeCast.startingEvent[" + stationId + "]");

        this.name = "C" + stationId;
        CastingStation station = model.rcCastingStations[stationId];
        station.isSuspended = false;
    }

    @Override
    protected double duration() {
        return model.rcCastingStations[stationId].castingTimeLeft;
    }

    @Override
    protected void terminatingEvent() {
        CastingStation station = model.rcCastingStations[stationId];
        station.busy = false;
        station.timeToFailure -= duration();
        station.castingTimeLeft = 0;
    }
}
