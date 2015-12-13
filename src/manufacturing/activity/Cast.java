package manufacturing.activity;

import manufacturing.Constants;
import manufacturing.ToyAirplaneManufacturing;
import manufacturing.entity.Bin;
import simulationModelling.Activity;

/**
 * Creates casting at a casting station when the casting machine is not busy,
 * does not contain a casting and the casting time is less than the time to failure.
 *
 */
public class Cast extends Activity {
    private ToyAirplaneManufacturing model;
    private int stationId;
    private double duration;

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
        model.rcCastingStations[stationId].busy = true;

        if (!(model.rcCastingStations[stationId].castingTimeLeft > 0)) {
            // a new casting session. Each casting session produces 4 castings
            model.rcCastingStations[stationId].bin = new Bin();
        }
        // otherwise we're resuming from an interrupted casting session - we already have a bin,
        // don't have to create a new one

        double wouldBeTime;
        if (model.rcCastingStations[stationId].castingTimeLeft > 0) {
            wouldBeTime = model.rcCastingStations[stationId].castingTimeLeft;
        }
        else {
            wouldBeTime = model.rvp.uStationWorkTime(Constants.CAST);
        }

        if (wouldBeTime > model.rcCastingStations[stationId].timeToFailure) {
            duration = model.rcCastingStations[stationId].timeToFailure;
        }
        else {
            duration = wouldBeTime;
        }
    }

    @Override
    protected double duration() {
        return duration;
    }

    @Override
    protected void terminatingEvent() {
        model.rcCastingStations[stationId].busy = false;

        // resume from an interrupted casting session (i.e. not all 4 castings have been made yet)
        if (model.rcCastingStations[stationId].castingTimeLeft > 0) {
            // done with the casting session
            if (model.rcCastingStations[stationId].castingTimeLeft == duration) {
                model.rcCastingStations[stationId].bin.planeType = model.rcCastingStations[stationId].planeType;
                model.output.castingsCreated[model.rcCastingStations[stationId].planeType] += 4;
            }
            // if castingTimeLeft > duration, that means interrupted again - this machine sure breaks down a lot...
            // either way, reduce castingTimeLeft by duration and add the wasted time (will be reduced to 0 if the casting session is completed)
            if (model.rcCastingStations[stationId].castingTimeLeft >= duration) {
                // the time wasted on doing a casting that will have to be re-done
                double wastedTime = duration % model.rvp.CASTING_TIME;
                model.rcCastingStations[stationId].castingTimeLeft = model.rcCastingStations[stationId].castingTimeLeft - duration + wastedTime;
            }
            // can't be <, that wouldn't make sense
        }
        else { // new casting session
            // new casting session is not done, machine is broken midway
            if (duration < model.rvp.uStationWorkTime(Constants.CAST)) {
                // the time wasted on doing a casting that will have to be re-done
                double wastedTime = duration % model.rvp.CASTING_TIME;
                model.rcCastingStations[stationId].castingTimeLeft = model.rvp.uStationWorkTime(Constants.CAST)
                        - duration + wastedTime;
            }
            // new casting session is done
            else if (duration == model.rvp.uStationWorkTime(Constants.CAST)) {
                model.rcCastingStations[stationId].bin.planeType = model.rcCastingStations[stationId].planeType;
                model.output.castingsCreated[model.rcCastingStations[stationId].planeType] += 4;
            }
            // can't be >, wouldn't make sense (duration must be <= casting time, always)
        }
        model.rcCastingStations[stationId].timeToFailure -= duration;

        if (model.rcCastingStations[stationId].timeToFailure == 0) {
            model.qRepairQueue.spInsertQue(stationId);
        }
    }
}
