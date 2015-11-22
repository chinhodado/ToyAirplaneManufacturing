
import cern.jet.random.engine.RandomSeedGenerator;
import manufacturing.Seeds;
import manufacturing.ToyAirplaneManufacturing;

public class LoggingExperiment {
    /**
     * @param args
     */
    public static void main(String[] args) {
        int i, NUMRUNS = 1;
        double endTime = 1 * 24 * 60; // run for 1 days
        Seeds[] sds = new Seeds[NUMRUNS];
        ToyAirplaneManufacturing mnf; // Simulation object

        // Lets get a set of uncorrelated seeds, different seeds for each run
        RandomSeedGenerator rsg = new RandomSeedGenerator();
        for (i = 0; i < NUMRUNS; i++)
            sds[i] = new Seeds(rsg);

        // Loop for NUMRUN simulation runs for each case
        int lc2 = 3;
        int lc3 = 3;
        System.out.println("Conveyor Limits: " + lc2 + ", " + lc3);
        printDSOVsHeader();
        for (i = 0; i < NUMRUNS; i++) {
//            mnf = new ToyAirplaneManufacturing(endTime, lc2, lc3, sds[i], true);
//            mnf.runSimulation();
//            printDSOVs(i + 1, mnf);
        }
    }

    private static void printDSOVsHeader() {
        System.out.println("Run, AverageM1Down, AverageConvM2Full, AveragemConvM3Full");
    }

    private static void printDSOVs(int num, ToyAirplaneManufacturing mnf) {
//        System.out.println(
//                num + ", " + mnf.getPercentTimeCastingStationBlocked() + ", " + mnf.getTimeC2Full() + ", " + mnf.getTimeC3Full());
    }
}
