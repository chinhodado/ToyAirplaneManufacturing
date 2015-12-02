
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import cern.jet.random.engine.RandomSeedGenerator;
import manufacturing.Seeds;
import manufacturing.ToyAirplaneManufacturing;

public class Experiment {
    /**
     * @param args
     */
    public static void main(String[] args) {
        int i, NUMRUNS = 5;
        double endTime = 8 * 60; // run for 8 hours
        Seeds[] sds = new Seeds[NUMRUNS];
        ToyAirplaneManufacturing model = null; // Simulation object

        // Let's get a set of uncorrelated seeds, different seeds for each run
        RandomSeedGenerator rsg = new RandomSeedGenerator();
        for (i = 0; i < NUMRUNS; i++)
            sds[i] = new Seeds(rsg);

        boolean foundFinalNumCastingStations = false,
                foundFinalNumCuttingGrindingStations = false,
                foundFinalNumCoatingStations = false,
                foundFinalNumInspectionPackagingStations = false,
                foundFinalNumMover = false;

        // Loop for NUMRUN simulation runs for each case
        int numMover = 20,
            numF16CastingStation = 3,
            numConcordeCastingStation = 3,
            numSpitfireCastingStation = 2,
            numCuttingGrindingStation = 20,
            numCoatingStation = 20,
            numInspectionPackagingStation = 20;

        while (true) {
            int[] params = new int[] { numMover, numF16CastingStation, numConcordeCastingStation,
                    numSpitfireCastingStation, numCuttingGrindingStation, numCoatingStation,
                    numInspectionPackagingStation};
            System.out.println("==================New run with modified parameter: " + Arrays.toString(params) + "===================");

            double meanNumConcordeProducedDaily = 0, meanNumF16ProducedDaily = 0, meanNumSpitfireProducedDaily = 0;
            for (i = 0; i < NUMRUNS; i++) {
                model = new ToyAirplaneManufacturing(endTime, params, sds[i], false);
                model.runSimulation();

                meanNumConcordeProducedDaily += model.getNumConcordeProduced();
                meanNumF16ProducedDaily      += model.getNumF16Produced();
                meanNumSpitfireProducedDaily += model.getNumSpitfireProduced();
            }

            meanNumConcordeProducedDaily /= NUMRUNS;
            meanNumF16ProducedDaily /= NUMRUNS;
            meanNumSpitfireProducedDaily /= NUMRUNS;

            double percentConcordeProduced = (double)meanNumConcordeProducedDaily / 2340;
            double percentF16Produced = (double)meanNumF16ProducedDaily / 1950;
            double percentSpitfireProduced = (double)meanNumSpitfireProducedDaily / 1300;
            System.out.println("Num F16 produced: " + meanNumF16ProducedDaily +
                    "(" + String.format( "%.2f", percentF16Produced * 100) + "%)");
            System.out.println("Num Concorde produced: " + meanNumConcordeProducedDaily +
                    "(" + String.format( "%.2f", percentConcordeProduced * 100) + "%)");
            System.out.println("Num Spitfire produced: " + meanNumSpitfireProducedDaily +
                    "(" + String.format( "%.2f", percentSpitfireProduced * 100) + "%)");

            if (meanNumConcordeProducedDaily >= 2340 &&
                meanNumF16ProducedDaily >= 1950 &&
                meanNumSpitfireProducedDaily >= 1300) {

                if (!foundFinalNumCastingStations) {
                    foundFinalNumCastingStations = true;
                    System.out.println("Final number of casting stations found. Searching for optimal number of cutting/grinding stations.");
                    numCuttingGrindingStation = 6;
                    continue;
                }
                else if (!foundFinalNumCuttingGrindingStations) {
                    foundFinalNumCuttingGrindingStations = true;
                    System.out.println("Final number of cutting/grinding stations found. Searching for optimal number of coating stations.");
                    numCoatingStation = 6;
                    continue;
                }
                else if (!foundFinalNumCoatingStations) {
                    foundFinalNumCoatingStations = true;
                    System.out.println("Final number of coating stations found. Searching for optimal number of inspection/packaging stations.");
                    numInspectionPackagingStation = 4;
                    continue;
                }
                else if (!foundFinalNumInspectionPackagingStations) {
                    foundFinalNumInspectionPackagingStations = true;
                    System.out.println("Final number of inspection/packaging stations found. Searching for optimal number of movers.");
                    numMover = 1;
                    continue;
                }
                else if (!foundFinalNumMover) {
                    foundFinalNumMover = true;
                    System.out.println("Output reached! Simulation completed.");
                    break;
                }
            }

            int numCastingStation = numF16CastingStation + numConcordeCastingStation + numSpitfireCastingStation;
            if (numCastingStation == 20 && numCuttingGrindingStation == 20 && numCoatingStation == 20 && numInspectionPackagingStation == 20) {
                System.out.println("Every station is at maximum and output is not reached. Something went horribly wrong...");
                break;
            }

            double minProducedPercent = Collections.min(new ArrayList<Double>(
                    Arrays.asList(percentConcordeProduced, percentF16Produced, percentSpitfireProduced)));

            if (!foundFinalNumCastingStations) {
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
            else if (!foundFinalNumCuttingGrindingStations) {
                numCuttingGrindingStation++;
            }
            else if (!foundFinalNumCoatingStations) {
                numCoatingStation++;
            }
            else if (!foundFinalNumInspectionPackagingStations) {
                numInspectionPackagingStation++;
            }
            else if (!foundFinalNumMover) {
                numMover++;
            }
        }
    }
}
