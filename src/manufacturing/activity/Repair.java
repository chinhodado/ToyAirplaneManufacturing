package manufacturing.activity;

import manufacturing.Constants;
import manufacturing.ToyAirplaneManufacturing;
import manufacturing.Util;
import manufacturing.entity.CastingStation;
import simulationModelling.Activity;

/**
 * Repair a casting station when it is broken down. The maintenance person must not be busy.
 *
 */
public class Repair extends Activity {
    ToyAirplaneManufacturing model;
    int stationId;

    public Repair(ToyAirplaneManufacturing model) {
        this.model = model;
    }

    public static boolean precondition(ToyAirplaneManufacturing model) {
        return model.udp.StationReadyForRepair() != Constants.NONE &&
                !model.rMaintenancePerson.busy;
    }

    @Override
    public void startingEvent() {
        stationId = model.udp.StationReadyForRepair();
        Util.logVerbose("Repair.startingEvent[" + stationId + "]");

        this.name = "C" + stationId;
        model.rMaintenancePerson.busy = true;
    }

    @Override
    protected double duration() {
        return model.rvp.uRepairTime();
    }

    @Override
    protected void terminatingEvent() {
        CastingStation station = model.rcCastingStations[stationId];
        station.timeToFailure = model.rvp.uTimeToFailure();
        model.rMaintenancePerson.busy = false;
    }
}
