package manufacturing.activity;

import manufacturing.ToyAirplaneManufacturing;
import simulationModelling.Activity;

/**
 * Repair a casting station when it is broken down. The maintenance person must not be busy.
 *
 */
public class Repair extends Activity {
    private ToyAirplaneManufacturing model;
    private int stationId;

    public Repair(ToyAirplaneManufacturing model) {
        this.model = model;
    }

    public static boolean precondition(ToyAirplaneManufacturing model) {
        return model.qRepairQueue.getN() > 0 &&
                !model.rMaintenancePerson.busy;
    }

    @Override
    public void startingEvent() {
        stationId = model.qRepairQueue.spRemoveQue();
        this.name = "Casting" + stationId;
        model.rMaintenancePerson.busy = true;
    }

    @Override
    protected double duration() {
        return model.rvp.uRepairTime();
    }

    @Override
    protected void terminatingEvent() {
        model.rcCastingStations[stationId].timeToFailure = model.rvp.uTimeToFailure();
        model.rMaintenancePerson.busy = false;
    }
}
