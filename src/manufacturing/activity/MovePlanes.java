package manufacturing.activity;

import manufacturing.Constants;
import manufacturing.ToyAirplaneManufacturing;
import manufacturing.Util;
import simulationModelling.Activity;

/**
 * The movers move bins containing planes/castings from one station to another stations
 * when he is currently at a loading queue and has already loaded 3 bins into his trolley,
 * or when he is at the unloading queue of the INSPECT_PACK station and he has no bin left.
 */
public class MovePlanes extends Activity {
    ToyAirplaneManufacturing model;
    int moverId;
    int stationType;
    int nextStationType;

    public MovePlanes(ToyAirplaneManufacturing model) {
        this.model = model;
    }

    public static boolean precondition(ToyAirplaneManufacturing model) {
        return model.udp.MoverReadyForMoving()[0] != Constants.NONE;
    }

    @Override
    public void startingEvent() {
        // identify the station
        int[] ids = model.udp.MoverReadyForMoving();
        moverId = ids[0];
        stationType = ids[1];
        Util.logVerbose("MovePlanes.statingEvent[" + moverId + ", " + stationType + "]");

        // Place identifier in name of behaviour object for logging, used by showSbl()
        this.name = "M" + moverId + "_" + stationType;

        if (stationType == Constants.INSPECT_PACK) {
            model.qLoadUnload[Constants.IN][stationType].spRemoveQue(moverId);
        }
        else {
            model.qLoadUnload[Constants.OUT][stationType].spRemoveQue(moverId);
        }

        nextStationType = model.udp.MovePlanes(moverId, stationType);
    }

    @Override
    public double duration() {
        return model.dvp.GetMoveToStationTime(stationType, nextStationType); // TODO: will this work?
    }

    @Override
    public void terminatingEvent() {
        if (stationType != Constants.INSPECT_PACK) {
            model.qLoadUnload[Constants.IN][nextStationType].spInsertQue(moverId);
        }
        else {
            model.qLoadUnload[Constants.OUT][nextStationType].spInsertQue(moverId);
        }
    }
}
