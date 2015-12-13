package manufacturing.action;

import manufacturing.Constants;
import manufacturing.ToyAirplaneManufacturing;
import manufacturing.entity.CastingStation;
import manufacturing.entity.IOArea;
import manufacturing.entity.LoadUnload;
import manufacturing.entity.MaintenancePerson;
import manufacturing.entity.Mover;
import manufacturing.entity.Station;
import simulationModelling.ScheduledAction;

public class Initialise extends ScheduledAction {
    ToyAirplaneManufacturing model;

    // Constructor
    public Initialise(ToyAirplaneManufacturing model) {
        this.model = model;
    }

    double[] ts = { 0.0, -1.0 }; // -1.0 ends scheduling
    int tsix = 0; // set index to first entry.

    @Override
    public double timeSequence() {
        return ts[tsix++]; // only invoked at t=0
    }

    @Override
    public void actionEvent() {
        // Stations
        for (int stationType = Constants.CUT_GRIND; stationType <= Constants.INSPECT_PACK; stationType++) {
            for (int stationId = 0; stationId < model.numStations[stationType]; stationId++) {
                // attributes are initialized when object is created
                model.rgStations[stationType][stationId] = new Station();
            }
        }

        // Casting stations
        for (int stationId = 0; stationId < model.numF16CastingStation; stationId++) {
            model.rcCastingStations[stationId] = new CastingStation(Constants.F16);
        }
        for (int stationId = 0; stationId < model.numSpitfireCastingStation; stationId++) {
            model.rcCastingStations[stationId + model.numF16CastingStation] = new CastingStation(Constants.SPITFIRE);
        }
        for (int stationId = 0; stationId < model.numConcordeCastingStation; stationId++) {
            model.rcCastingStations[stationId + model.numF16CastingStation + model.numSpitfireCastingStation] =
                    new CastingStation(Constants.CONCORDE);
        }
        for (int stationId = 0; stationId < model.numCastingStation; stationId++) {
            model.rcCastingStations[stationId].timeToFailure = model.rvp.uTimeToFailure();
        }

        // these are already 0 by default, but let's just be explicit
        model.castingsCreated[Constants.F16] = 0;
        model.castingsCreated[Constants.SPITFIRE] = 0;
        model.castingsCreated[Constants.CONCORDE] = 0;

        // Input areas and unloading queues
        for (int stationType = Constants.CUT_GRIND; stationType <= Constants.INSPECT_PACK; stationType++) {
            for (int stationId = 0; stationId < model.numStations[stationType]; stationId++) {
                model.qIOAreas[Constants.IN][stationType][stationId] = new IOArea();
            }
            model.qLoadUnload[Constants.IN][stationType] = new LoadUnload();
        }

        // Output areas and loading queues
        for (int stationType = Constants.CAST; stationType <= Constants.COAT; stationType++) {
            for (int stationId = 0; stationId < model.numStations[stationType]; stationId++) {
                model.qIOAreas[Constants.OUT][stationType][stationId] = new IOArea();
            }
            model.qLoadUnload[Constants.OUT][stationType] = new LoadUnload();
        }

        for (int moverId = 0; moverId < model.numMover; moverId++) {
            model.rgMovers[moverId] = new Mover();
            model.qLoadUnload[Constants.OUT][Constants.CAST].spInsertQue(moverId);
        }

        model.rMaintenancePerson = new MaintenancePerson();
    }
}
