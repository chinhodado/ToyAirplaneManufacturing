import java.util.Arrays;

import cern.jet.random.engine.RandomSeedGenerator;
import manufacturing.Seeds;
import manufacturing.ToyAirplaneManufacturing;

/**
 * Used for generation of trace logs for the purpose of verification and validation
 */
public class ExperimentLogging {
    public static void main(String[] args) {
        int i, NUMRUNS = 1;
        double endTime = 8 * 60; // run for 8 hours
        Seeds[] sds = new Seeds[NUMRUNS];
        ToyAirplaneManufacturing model = null; // Simulation object

        // Let's get a set of uncorrelated seeds, different seeds for each run
        RandomSeedGenerator rsg = new RandomSeedGenerator();
        for (i = 0; i < NUMRUNS; i++)
            sds[i] = new Seeds(rsg);

        int numMover = 7,
            numF16CastingStation = 3,
            numConcordeCastingStation = 4,
            numSpitfireCastingStation = 5,
            numCuttingGrindingStation = 1,
            numCoatingStation = 6,
            numInspectionPackagingStation = 4;

        int[] params = new int[] { numMover, numF16CastingStation, numConcordeCastingStation,
                numSpitfireCastingStation, numCuttingGrindingStation, numCoatingStation,
                numInspectionPackagingStation};
        System.out.println("==================New run with parameters: " + Arrays.toString(params) + "===================");
        int[] numCastingStations = new int[] {numF16CastingStation, numConcordeCastingStation,
                numSpitfireCastingStation};
        int numCastingStation = numF16CastingStation + numConcordeCastingStation + numSpitfireCastingStation;
        int[] numStations = new int[] { numCastingStation, numCuttingGrindingStation, numCoatingStation,
                numInspectionPackagingStation};

        double meanNumConcordeProducedDaily = 0, meanNumF16ProducedDaily = 0, meanNumSpitfireProducedDaily = 0;
        for (i = 0; i < NUMRUNS; i++) {
            model = new ToyAirplaneManufacturing(endTime, numMover, numCastingStations, numStations, sds[i], true);
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
    }
}
