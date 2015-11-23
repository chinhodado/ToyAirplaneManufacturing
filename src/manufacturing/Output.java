package manufacturing;

import manufacturing.entity.CastingStation;
import manufacturing.entity.Station;
import simulationModelling.OutputSequence;

public class Output {
    ToyAirplaneManufacturing model;
    public int numSpitfireProducedDaily;
    public int numF16ProducedDaily;
    public int numConcordeProducedDaily;

    // Trajectory Sequences
    OutputSequence[] trjCastingStationBlocked;
    OutputSequence[][] trjStationBlocked;

    // Last value saved in the trajectory set
    double[] lastCastingStationBlocked;
    double[][] lastStationBlocked;

    // Constructor
    protected Output(ToyAirplaneManufacturing model) {
        this.model = model;

        // First points in trajectory sequences - range = 0.0
        // Java will initialize array values to 0 automatically - no need to do that here
        lastCastingStationBlocked = new double[model.numCastingStation];
        lastStationBlocked = new double[4][];
        for (int stationType = Constants.CUT_GRIND; stationType <= Constants.INSPECT_PACK; stationType++) {
            lastStationBlocked[stationType] = new double[model.numStations[stationType]];
        }

        trjCastingStationBlocked = new OutputSequence[model.numCastingStation];
        for (int stationId = 0; stationId < model.numCastingStation; stationId++) {
            trjCastingStationBlocked[stationId] = new OutputSequence("CastingStation" + stationId + "blocked.txt");
            trjCastingStationBlocked[stationId].put(0.0, lastCastingStationBlocked[stationId]);
        }

        trjStationBlocked = new OutputSequence[4][];
        for (int stationType = Constants.CUT_GRIND; stationType <= Constants.INSPECT_PACK; stationType++) {
            trjStationBlocked[stationType] = new OutputSequence[model.numStations[stationType]];
            for (int stationId = 0; stationId < model.numStations[stationType]; stationId++) {
                trjStationBlocked[stationType][stationId] = new OutputSequence("Station" + stationType + "_" + stationId + "blocked.txt");
                trjStationBlocked[stationType][stationId].put(0.0, lastStationBlocked[stationType][stationId]);
            }
        }
    }

    // DSOVs
    protected double percentTimeCastingStationBlocked() {
        double meanSum = 0;
        for (int stationId = 0; stationId < model.numCastingStation; stationId++) {
            trjCastingStationBlocked[stationId].computeTrjDSOVs(model.getTime0(), model.getTimef());
            meanSum += trjCastingStationBlocked[stationId].getMean();
        }
        return meanSum / model.numCastingStation;
    }

    protected double percentTimeStationBlocked(int stationType) {
        double meanSum = 0;
        for (int stationId = 0; stationId < model.numStations[stationType]; stationId++) {
            trjStationBlocked[stationType][stationId].computeTrjDSOVs(model.getTime0(), model.getTimef());
            meanSum += trjStationBlocked[stationType][stationId].getMean();
        }
        return meanSum / model.numStations[stationType];
    }

    // Update the trajectory sequences
    // curTime - the current time
    protected void updateSequences() {
        // update TRJ[RC.CastingStations[stationId].blocked]
        for (int stationId = 0; stationId < model.numCastingStation; stationId++) {
            double currentCastingStationBlocked;
            CastingStation station = model.rcCastingStations[stationId];

            // The station.bin.planeType != Constants.NONE part is mainly for casting stations,
            // since a "bin" can be sitting in a station while it was broken in the middle of a casting,
            // and it is not busy but waiting to be repaired and then resume casting with the current "bin",
            // so it's not considered blocked
            if (!station.busy && station.bin != Constants.NO_BIN && station.bin.planeType != Constants.NONE)
                currentCastingStationBlocked = 1;
            else
                currentCastingStationBlocked = 0;

            if (currentCastingStationBlocked != lastCastingStationBlocked[stationId]) {
                trjCastingStationBlocked[stationId].put(model.getClock(), currentCastingStationBlocked);
                lastCastingStationBlocked[stationId] = currentCastingStationBlocked;
            }
        }

        // update TRJ[RG.Stations[stationType][stationId].blocked]
        for (int stationType = Constants.CUT_GRIND; stationType <= Constants.INSPECT_PACK; stationType++) {
            for (int stationId = 0; stationId < model.numStations[stationType]; stationId++) {
                double currentStationBlocked;
                Station station = model.rgStations[stationType][stationId];

                if (!station.busy && station.bin != Constants.NO_BIN)
                    currentStationBlocked = 1;
                else
                    currentStationBlocked = 0;

                if (currentStationBlocked != lastStationBlocked[stationType][stationId]) {
                    trjStationBlocked[stationType][stationId].put(model.getClock(), currentStationBlocked);
                    lastStationBlocked[stationType][stationId] = currentStationBlocked;
                }
            }
        }
    }
}
