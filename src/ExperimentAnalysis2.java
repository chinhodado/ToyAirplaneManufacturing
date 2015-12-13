import cern.jet.random.engine.RandomSeedGenerator;
import manufacturing.Seeds;
import manufacturing.ToyAirplaneManufacturing;
import outputAnalysis.ConfidenceInterval;

public class ExperimentAnalysis2 {
    static final int [] NUM_RUNS_ARRAY = {20, 30, 40, 60, 80, 100, 1000, 10000};
    static final double CONF_LEVEL = 0.99;
    public static void main(String[] args) {
        int i, NUMRUNS = 10000;
        double endTime = 8 * 60; // run for 8 hours
        Seeds[] sds = new Seeds[NUMRUNS];
        ToyAirplaneManufacturing model = null; // Simulation object

        // Let's get a set of uncorrelated seeds, different seeds for each run
        RandomSeedGenerator rsg = new RandomSeedGenerator();
        for (i = 0; i < NUMRUNS; i++)
            sds[i] = new Seeds(rsg);

        double[] allValuesF16 = new double[NUMRUNS];
        double[] allValuesConcorde = new double[NUMRUNS];
        double[] allValuesSpitfire = new double[NUMRUNS];

        int numMover = 9,
            numF16CastingStation = 4,
            numConcordeCastingStation = 5,
            numSpitfireCastingStation = 3,
            numCuttingGrindingStation = 8,
            numCoatingStation = 6,
            numInspectionPackagingStation = 5;

        int[] numCastingStations = new int[] {numF16CastingStation, numConcordeCastingStation,
                numSpitfireCastingStation};
        int numCastingStation = numF16CastingStation + numConcordeCastingStation + numSpitfireCastingStation;
        int[] numStations = new int[] { numCastingStation, numCuttingGrindingStation, numCoatingStation,
                numInspectionPackagingStation};

        for (i = 0; i < NUMRUNS; i++) {
            model = new ToyAirplaneManufacturing(endTime, numMover, numCastingStations, numStations, sds[i], false);
            model.runSimulation();

            allValuesConcorde[i] = model.getNumConcordeProduced();
            allValuesF16[i]      = model.getNumF16Produced();
            allValuesSpitfire[i] = model.getNumSpitfireProduced();
        }

        displayTable(allValuesConcorde, allValuesF16, allValuesSpitfire);
    }

    private static void displayTable(double[] allValuesConcorde, double[] allValuesF16, double[] allValuesSpitfire) {
           System.out.printf("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
           System.out.printf("                             Result (Concorde, F16, Spitfire):\n");
           System.out.printf("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
           System.out.printf("    n             y(n)                           s(n)                           zeta(n)                       CI Min                          CI Max                    zeta(n)/y(n)\n");
           System.out.printf("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
           for(int ix1 = 0; ix1 < NUM_RUNS_ARRAY.length; ix1++) {
               int numruns = NUM_RUNS_ARRAY[ix1];
               double[] valuesF16 = new double[numruns];
               double[] valuesConcorde = new double[numruns];
               double[] valuesSpitfire = new double[numruns];
               for(int ix2 = 0 ; ix2 < numruns; ix2++) {
                   valuesF16[ix2]      = allValuesF16[ix2];
                   valuesConcorde[ix2] = allValuesConcorde[ix2];
                   valuesSpitfire[ix2] = allValuesSpitfire[ix2];
               }
               ConfidenceInterval cfConcorde = new ConfidenceInterval(valuesConcorde, CONF_LEVEL);
               ConfidenceInterval cfF16 = new ConfidenceInterval(valuesF16, CONF_LEVEL);
               ConfidenceInterval cfSpitfire = new ConfidenceInterval(valuesSpitfire, CONF_LEVEL);
               System.out.printf("%5d (%8.3f, %8.3f, %8.3f) (%8.3f, %8.3f, %8.3f) (%8.3f, %8.3f, %8.3f) (%8.3f, %8.3f, %8.3f) (%8.3f, %8.3f, %8.3f) (%5.3f, %5.3f, %5.3f)\n",
                               numruns,
                               cfConcorde.getPointEstimate(), cfF16.getPointEstimate(), cfSpitfire.getPointEstimate(),
                               cfConcorde.getStdDev(), cfF16.getStdDev(), cfSpitfire.getStdDev(),
                               cfConcorde.getZeta(), cfF16.getZeta(), cfSpitfire.getZeta(),
                               cfConcorde.getCfMin(), cfF16.getCfMin(), cfSpitfire.getCfMin(),
                               cfConcorde.getCfMax(), cfF16.getCfMax(), cfSpitfire.getCfMax(),
                               cfConcorde.getZeta()/cfConcorde.getPointEstimate(), cfF16.getZeta()/cfF16.getPointEstimate(), cfSpitfire.getZeta()/cfSpitfire.getPointEstimate());
           }
           System.out.printf("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
    }
}
