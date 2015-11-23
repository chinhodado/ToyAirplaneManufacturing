package manufacturing.activity;

import manufacturing.Constants;
import manufacturing.ToyAirplaneManufacturing;
import manufacturing.Util;
import manufacturing.entity.Bin;
import manufacturing.entity.CastingStation;
import simulationModelling.Activity;

/**
 * Creates casting at a casting station when the casting machine is not busy,
 * does not contain a casting and the casting time is less than the time to failure.
 *
 */
public class Cast extends Activity {
    ToyAirplaneManufacturing model;
    CastingStation station;
    int stationId;

    public Cast(ToyAirplaneManufacturing model) {
        this.model = model;
    }

    public static boolean precondition(ToyAirplaneManufacturing model) {
        return model.udp.StationReadyForCasting() != Constants.NONE;
    }

    @Override
    public void startingEvent() {
        stationId = model.udp.StationReadyForCasting();
        Util.logVerbose("Cast.startingEvent[" + stationId + "]");
        this.name = "C" + stationId;
        station = model.rcCastingStations[stationId];
        station.busy = true;

        if (station.castingTimeLeft > 0) {

        }
        else {
            station.bin = new Bin();
            station.bin.planeType = station.planeType;
        }
    }

    @Override
    protected double duration() {
        double wouldBeTime;
        if (station.castingTimeLeft > 0) {
            wouldBeTime = station.castingTimeLeft;
        }
        else {
            wouldBeTime = model.rvp.uStationWorkTime(Constants.CAST);
        }

        if (wouldBeTime > station.timeToFailure) {
            return station.timeToFailure;
        }
        else {
            return wouldBeTime;
        }
    }

    @Override
    protected void terminatingEvent() {
        Util.logVerbose("Cast.terminatingEvent[" + stationId + "]");
        CastingStation station = model.rcCastingStations[stationId];
        station.busy = false;

        double duration = duration();
        if (station.castingTimeLeft > 0) {
            if (station.castingTimeLeft >= duration) {
                station.castingTimeLeft -= duration;
            }
            // can't be <, that wouldn't make sense
        }
        else {
            if (duration < model.rvp.uStationWorkTime(Constants.CAST)) {
                station.castingTimeLeft = model.rvp.uStationWorkTime(Constants.CAST) - duration;
            }
            // can't be >, wouldn't make sense (duration must be <= casting time, always)
        }
        station.timeToFailure -= duration;
    }
}
