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
        System.out.println("Cast.startingEvent[" + stationId + "]");
        this.name = "C" + stationId;
        CastingStation station = model.rcCastingStations[stationId];
        station.busy = true;
        station.bin = new Bin();
        station.bin.planeType = station.planeType;
    }

    @Override
    protected double duration() {
        return model.rvp.uStationWorkTime(Constants.CAST);
    }

    @Override
    protected void terminatingEvent() {
        CastingStation station = model.rcCastingStations[stationId];
        station.busy = false;
        station.timeToFailure -= duration();
    }
}
