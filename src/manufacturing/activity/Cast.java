package manufacturing.activity;

import manufacturing.Constants;
import manufacturing.ToyAirplaneManufacturing;
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
        this.name = "Casting" + stationId;
        station = model.rcCastingStations[stationId];
        station.busy = true;

        if (station.castingTimeLeft > 0) {
            // resuming from an interrupted casting - nothing else to be done
        }
        else {
            // a new casting session
            station.bin = new Bin();
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
        CastingStation station = model.rcCastingStations[stationId];
        station.busy = false;

        double duration = duration();
        if (station.castingTimeLeft > 0) { // resume from an interrupted cast
            // done with the casting
            if (station.castingTimeLeft == duration) {
                station.bin.planeType = station.planeType;
                model.castingsCreated[station.planeType] += 4;
            }
            // if castingTimeLeft > duration, that means interrupted again - this machine sure breaks down a lot...
            // either way, reduce casting timeLeft by duration (will be reduced to 0 if the casting is completed)
            if (station.castingTimeLeft >= duration) {
                station.castingTimeLeft -= duration;
            }
            // can't be <, that wouldn't make sense
        }
        else { // new cast
            // new cast is not done, machine is broken midway
            if (duration < model.rvp.uStationWorkTime(Constants.CAST)) {
                station.castingTimeLeft = model.rvp.uStationWorkTime(Constants.CAST) - duration;
            }
            // new cast is done
            else if (duration == model.rvp.uStationWorkTime(Constants.CAST)) {
                station.bin.planeType = station.planeType;
                model.castingsCreated[station.planeType] += 4;
            }
            // can't be >, wouldn't make sense (duration must be <= casting time, always)
        }
        station.timeToFailure -= duration;

        if (station.timeToFailure == 0) {
            model.qRepairQueue.spInsertQue(stationId);
        }
    }
}
