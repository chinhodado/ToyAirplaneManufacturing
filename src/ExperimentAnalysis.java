import cern.jet.random.engine.RandomSeedGenerator;
import manufacturing.Seeds;
import manufacturing.ToyAirplaneManufacturing;
import outputAnalysis.ConfidenceInterval;

public class ExperimentAnalysis {
    public static void main(String[] args) {
        int i, NUMRUNS = 100;
        final double CONF_LEVEL = 0.99;
        double endTime = 8 * 60; // run for 8 hours
        Seeds[] sds = new Seeds[NUMRUNS];
        ToyAirplaneManufacturing model = null; // Simulation object

        // Let's get a set of uncorrelated seeds, different seeds for each run
        RandomSeedGenerator rsg = new RandomSeedGenerator();
        for (i = 0; i < NUMRUNS; i++)
            sds[i] = new Seeds(rsg);

        double [] valuesF16Case1 = new double[NUMRUNS];
        double [] valuesConcordeCase1 = new double[NUMRUNS];
        double [] valuesSpitfireCase1 = new double[NUMRUNS];

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

            valuesConcordeCase1[i] = model.getNumConcordeProduced();
            valuesF16Case1[i]      = model.getNumF16Produced();
            valuesSpitfireCase1[i] = model.getNumSpitfireProduced();
        }

        ConfidenceInterval cfConcorde = new ConfidenceInterval(valuesConcordeCase1, CONF_LEVEL);
        ConfidenceInterval cfF16 = new ConfidenceInterval(valuesF16Case1, CONF_LEVEL);
        ConfidenceInterval cfSpitfire = new ConfidenceInterval(valuesSpitfireCase1, CONF_LEVEL);
        // Create the table
        System.out.printf("-------------------------------------------------------------------------------------\n");
        System.out.printf("Planes    Point estimate(ybar(n))  s(n)     zeta   CI Min   CI Max     |zeta/ybar(n)|\n");
        System.out.printf("-------------------------------------------------------------------------------------\n");
        System.out.printf("Concorde %13.3f %18.3f %8.3f %8.3f %8.3f %14.3f\n",
                  cfConcorde.getPointEstimate(), cfConcorde.getVariance(), cfConcorde.getZeta(),
                  cfConcorde.getCfMin(), cfConcorde.getCfMax(),
                  Math.abs(cfConcorde.getZeta()/cfConcorde.getPointEstimate()));
        System.out.printf("F16      %13.3f %18.3f %8.3f %8.3f %8.3f %14.3f\n",
                  cfF16.getPointEstimate(), cfF16.getVariance(), cfF16.getZeta(),
                  cfF16.getCfMin(), cfF16.getCfMax(),
                  Math.abs(cfF16.getZeta()/cfF16.getPointEstimate()));
        System.out.printf("Spitfire %13.3f %18.3f %8.3f %8.3f %8.3f %14.3f\n",
                  cfSpitfire.getPointEstimate(), cfSpitfire.getVariance(), cfSpitfire.getZeta(),
                  cfSpitfire.getCfMin(), cfSpitfire.getCfMax(),
                  Math.abs(cfSpitfire.getZeta()/cfSpitfire.getPointEstimate()));
        System.out.printf("-------------------------------------------------------------------------------------\n");
    }
}
