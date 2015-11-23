
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import cern.jet.random.engine.RandomSeedGenerator;
import manufacturing.Constants;
import manufacturing.Seeds;
import manufacturing.ToyAirplaneManufacturing;

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
        while (numMover <= 50 &&
                numF16CastingStation <= 20 &&
                numConcordeCastingStation <= 20 &&
                numSpitfireCastingStation <= 20 &&
                numF16CastingStation + numConcordeCastingStation + numSpitfireCastingStation <= 20 &&
                numCuttingGrindingStation <= 20 &&
                numCoatingStation <= 20 &&
                numInspectionPackagingStation <= 20) {

            System.out.println("==================New run with modified parameter===================");
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

            if (model.output.numConcordeProducedDaily >= 2340 &&
                model.output.numF16ProducedDaily >= 1950 &&
                model.output.numSpitfireProducedDaily >= 1300)
                break;

            double percentConcordeProduced = (double)model.output.numConcordeProducedDaily / 2340;
            double percentF16Produced = (double)model.output.numF16ProducedDaily / 1950;
            double percentSpitfireProduced = (double)model.output.numSpitfireProducedDaily / 1300;
            System.out.println("Num Concorde produced: " + model.output.numConcordeProducedDaily);
            System.out.println("Percent Concorde produced: " + percentConcordeProduced);
            System.out.println("Num F16 produced: " + model.output.numF16ProducedDaily);
            System.out.println("Percent F16 produced: " + percentF16Produced);
            System.out.println("Num Spitfire produced: " + model.output.numSpitfireProducedDaily);
            System.out.println("Percent Spitfire produced: " + percentSpitfireProduced);

            double minProducedPercent = Collections.min(new ArrayList<Double>(
                    Arrays.asList(percentConcordeProduced, percentF16Produced, percentSpitfireProduced)));

            double maxBlockTime = Collections.max(new ArrayList<Double>(
                    Arrays.asList(meanCastingBlockedTime, meanCutGringBlockedTime,
                                    meanCoatBlockedTime, meanInspectPackBlockedTime)));

            int numCastingStation = numF16CastingStation + numConcordeCastingStation + numSpitfireCastingStation;
            if (numCastingStation == 20 && numCuttingGrindingStation == 20 && numCoatingStation == 20 && numInspectionPackagingStation == 20) {
                System.out.println("Something went horribly wrong...");
                break;
            }

            if (maxBlockTime == meanCastingBlockedTime && numCastingStation < 20) {
                if (minProducedPercent == percentConcordeProduced) {
                    System.out.println("Adding 1 Concorde casting station");
                    numConcordeCastingStation++;
                }
                else if (minProducedPercent == percentF16Produced){
                    System.out.println("Adding 1 F16 casting station");
                    numF16CastingStation++;
                }
                else if (minProducedPercent == percentSpitfireProduced) {
                    System.out.println("Adding 1 Spitfire casting station");
                    numSpitfireCastingStation++;
                }
            }
            else if (maxBlockTime == meanCutGringBlockedTime && numCuttingGrindingStation < 20) {
                System.out.println("Adding 1 cutting/grinding station");
                numCuttingGrindingStation++;
            }
            else if (maxBlockTime == meanCoatBlockedTime && numCoatingStation < 20) {
                System.out.println("Adding 1 coating station");
                numCoatingStation++;
            }
            else if (maxBlockTime == meanInspectPackBlockedTime && numInspectionPackagingStation < 20) {
                System.out.println("Adding 1 inspection/packaging station");
                numInspectionPackagingStation++;
            }
        }
    }
}
