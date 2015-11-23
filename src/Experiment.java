
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import cern.jet.random.engine.RandomSeedGenerator;
import manufacturing.Constants;
import manufacturing.Seeds;
import manufacturing.ToyAirplaneManufacturing;
import manufacturing.Util;

public class Experiment {
    /**
     * @param args
     */
    public static void main(String[] args) {
        int i, NUMRUNS = 40;
        double endTime = 8 * 60; // run for 8 hours
        Seeds[] sds = new Seeds[NUMRUNS];
        ToyAirplaneManufacturing model = null; // Simulation object

        // Let's get a set of uncorrelated seeds, different seeds for each run
        RandomSeedGenerator rsg = new RandomSeedGenerator();
        for (i = 0; i < NUMRUNS; i++)
            sds[i] = new Seeds(rsg);

        // Loop for NUMRUN simulation runs for each case
        int numMover = 50,
            numF16CastingStation = 3,
            numConcordeCastingStation = 3,
            numSpitfireCastingStation = 2,
            numCuttingGrindingStation = 1,
            numCoatingStation = 1,
            numInspectionPackagingStation = 1;

        double meanCastingBlockedTime = 0, meanCutGringBlockedTime = 0, meanCoatBlockedTime = 0, meanInspectPackBlockedTime = 0;

        while (true) {
            Util.logDetail("==================New run with modified parameter===================");
            int[] params = new int[] { numMover, numF16CastingStation, numConcordeCastingStation,
                    numSpitfireCastingStation, numCuttingGrindingStation, numCoatingStation,
                    numInspectionPackagingStation};
            System.out.println(Arrays.toString(params));
            for (i = 0; i < NUMRUNS; i++) {
                model = new ToyAirplaneManufacturing(endTime, params, sds[i], true);
                model.runSimulation();

                meanCastingBlockedTime += model.getPercentTimeCastingStationBlocked();
                meanCutGringBlockedTime += model.getPercentTimeStationBlocked(Constants.CUT_GRIND);
                meanCoatBlockedTime += model.getPercentTimeStationBlocked(Constants.COAT);
                meanInspectPackBlockedTime += model.getPercentTimeStationBlocked(Constants.INSPECT_PACK);
            }

            meanCastingBlockedTime /= NUMRUNS;
            meanCutGringBlockedTime /= NUMRUNS;
            meanCoatBlockedTime /= NUMRUNS;
            meanInspectPackBlockedTime /= NUMRUNS;

            double percentConcordeProduced = (double)model.output.numConcordeProducedDaily / 2340;
            double percentF16Produced = (double)model.output.numF16ProducedDaily / 1950;
            double percentSpitfireProduced = (double)model.output.numSpitfireProducedDaily / 1300;
            Util.logDetail("Num F16 produced: " + model.output.numF16ProducedDaily +
                    "(" + percentF16Produced * 100 + "%)");
            Util.logDetail("Num Concorde produced: " + model.output.numConcordeProducedDaily +
                    "(" + percentConcordeProduced * 100 + "%)");
            Util.logDetail("Num Spitfire produced: " + model.output.numSpitfireProducedDaily +
                    "(" + percentSpitfireProduced * 100 + "%)");

            if (model.output.numConcordeProducedDaily >= 2340 &&
                model.output.numF16ProducedDaily >= 1950 &&
                model.output.numSpitfireProducedDaily >= 1300) {
                System.out.println("Output reached! Simulation completed.");
                break;
            }

            int numCastingStation = numF16CastingStation + numConcordeCastingStation + numSpitfireCastingStation;
            if (numCastingStation == 20 && numCuttingGrindingStation == 20 && numCoatingStation == 20 && numInspectionPackagingStation == 20) {
                System.out.println("Every station is at maximum and output is not reached. Something went horribly wrong...");
                break;
            }

            double minProducedPercent = Collections.min(new ArrayList<Double>(
                    Arrays.asList(percentConcordeProduced, percentF16Produced, percentSpitfireProduced)));

            ArrayList<Double[]> maxBlockTimeList = new ArrayList<Double[]>();
            if (numCastingStation < 20) maxBlockTimeList.add(new Double[] { meanCastingBlockedTime, (double) Constants.CAST });
            if (numCuttingGrindingStation < 20) maxBlockTimeList.add(new Double[] { meanCutGringBlockedTime, (double) Constants.CUT_GRIND });
            if (numCoatingStation < 20) maxBlockTimeList.add(new Double[] { meanCoatBlockedTime, (double) Constants.COAT });
            if (numInspectionPackagingStation < 20) maxBlockTimeList.add(new Double[] { meanInspectPackBlockedTime, (double) Constants.INSPECT_PACK });

            Collections.sort(maxBlockTimeList, new Comparator<Double[]>() {
                @Override
                public int compare(Double[] o1, Double[] o2) {
                    return Double.compare(o2[0], o1[0]);
                }

            });

            int mostBlockedStation = maxBlockTimeList.get(0)[1].intValue();

            if (mostBlockedStation == Constants.CAST) {
                if (minProducedPercent == percentConcordeProduced) {
                    Util.logDetail("Adding 1 Concorde casting station");
                    numConcordeCastingStation++;
                }
                else if (minProducedPercent == percentF16Produced){
                    Util.logDetail("Adding 1 F16 casting station");
                    numF16CastingStation++;
                }
                else if (minProducedPercent == percentSpitfireProduced) {
                    Util.logDetail("Adding 1 Spitfire casting station");
                    numSpitfireCastingStation++;
                }
            }
            else if (mostBlockedStation == Constants.CUT_GRIND) {
                Util.logDetail("Adding 1 cutting/grinding station");
                numCuttingGrindingStation++;
            }
            else if (mostBlockedStation == Constants.COAT) {
                Util.logDetail("Adding 1 coating station");
                numCoatingStation++;
            }
            else if (mostBlockedStation == Constants.INSPECT_PACK) {
                Util.logDetail("Adding 1 inspection/packaging station");
                numInspectionPackagingStation++;
            }
        }
    }
}
