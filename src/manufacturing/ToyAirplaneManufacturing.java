package manufacturing;

import manufacturing.action.Initialise;
import manufacturing.action.LoadBin;
import manufacturing.action.PutInOutputArea;
import manufacturing.action.UnloadBin;
import manufacturing.activity.Cast;
import manufacturing.activity.DoStationWork;
import manufacturing.activity.MovePlanes;
import manufacturing.activity.Repair;
import manufacturing.entity.CastingStation;
import manufacturing.entity.IOArea;
import manufacturing.entity.LoadUnload;
import manufacturing.entity.MaintenancePerson;
import manufacturing.entity.Mover;
import manufacturing.entity.RepairQueue;
import manufacturing.entity.Station;
import simulationModelling.AOSimulationModel;
import simulationModelling.Behaviour;

public class ToyAirplaneManufacturing extends AOSimulationModel {
    // Entities
    public Station[][] rgStations;
    public CastingStation[] rcCastingStations;

    /**
     * IOArea[IN/OUT][stationType][stationId]
     */
    public IOArea[][][] qIOAreas;

    public RepairQueue qRepairQueue;
    public LoadUnload[][] qLoadUnload;
    public Mover[] rgMovers;
    public MaintenancePerson rMaintenancePerson;

    // Parameters
    public int numMover;
    public int[] numCastingStations;
    public int[] numStations;

    // Random Variate Procedures
    public RVP rvp;

    // User Defined Procedures
    public UDP udp;

    // Deterministic Variate Procedures
    public DVP dvp = new DVP();

    // Outputs
    public Output output;

    // for Debugging
    boolean logFlag = false;

    // SSOVs
    public double getNumF16Produced() {
        return output.numF16ProducedDaily;
    }

    public double getNumConcordeProduced() {
        return output.numConcordeProducedDaily;
    }

    public double getNumSpitfireProduced() {
        return output.numSpitfireProducedDaily;
    }

    double endTime;

    /**
     * Constructor
     * @param tftime The end time
     * @param numMover number of movers
     * @param numCastingStations number of F16, Concorde and Spitfire casting stations
     * @param numStations number of casting stations, numCuttingGrindingStation, numCoatingStation and numInspectionPackagingStation
     * @param sd     Seeds used
     * @param log    log
     */
    public ToyAirplaneManufacturing(double tftime, int numMover, int[] numCastingStations, int[] numStations, Seeds sd, boolean log) {
        // For turning on logging
        logFlag = log;

        // Set up RVPs
        rvp = new RVP(this, sd);

        // Initialize parameters
        this.numMover = numMover;
        this.numCastingStations = numCastingStations;
        this.numStations = numStations;

        rgStations = new Station[4][]; // the first array is not used
        rgStations[Constants.CUT_GRIND] = new Station[numStations[Constants.CUT_GRIND]];
        rgStations[Constants.COAT] = new Station[numStations[Constants.COAT]];
        rgStations[Constants.INSPECT_PACK] = new Station[numStations[Constants.INSPECT_PACK]];

        rcCastingStations = new CastingStation[numStations[Constants.CAST]];

        qRepairQueue = new RepairQueue();
        qIOAreas = new IOArea[2][4][];
        qLoadUnload = new LoadUnload[2][4];
        for (int stationType = Constants.CUT_GRIND; stationType <= Constants.INSPECT_PACK; stationType++) {
            qIOAreas[Constants.IN][stationType] = new IOArea[numStations[stationType]];
        }

        for (int stationType = Constants.CAST; stationType <= Constants.COAT; stationType++) {
            qIOAreas[Constants.OUT][stationType] = new IOArea[numStations[stationType]];
        }

        rgMovers = new Mover[numMover];
        output = new Output(this);
        udp = new UDP(this);

        this.initAOSimulModel(0);
        endTime = tftime;

        // Schedule Initialise action
        Initialise init = new Initialise(this);
        scheduleAction(init); // Should always be first one scheduled.

        // printDebug("At Start");
    }

    @Override
    public void testPreconditions(Behaviour behObj) {
        reschedule(behObj);
        while (scanPreconditions() == true)
            /* repeat */;
    }

    // Single scan of all preconditions
    // Returns true if at least one precondition was true.
    private boolean scanPreconditions() {
        boolean statusChanged = false;
        // Conditional Actions
        if (LoadBin.precondition(this) == true) {
            LoadBin act = new LoadBin(this);
            act.actionEvent();
            statusChanged = true;
        }
        if (PutInOutputArea.precondition(this) == true) {
            PutInOutputArea act = new PutInOutputArea(this);
            act.actionEvent();
            statusChanged = true;
        }
        if (UnloadBin.precondition(this) == true) {
            UnloadBin act = new UnloadBin(this);
            act.actionEvent();
            statusChanged = true;
        }

        // Conditional Activities
        if (Cast.precondition(this) == true) {
            Cast act = new Cast(this);
            act.startingEvent();
            scheduleActivity(act);
            statusChanged = true;
        }
        if (DoStationWork.precondition(this) == true) {
            DoStationWork act = new DoStationWork(this);
            act.startingEvent();
            scheduleActivity(act);
            statusChanged = true;
        }
        if (MovePlanes.precondition(this) == true) {
            MovePlanes act = new MovePlanes(this);
            act.startingEvent();
            scheduleActivity(act);
            statusChanged = true;
        }
        if (Repair.precondition(this) == true) {
            Repair act = new Repair(this);
            act.startingEvent();
            scheduleActivity(act);
            statusChanged = true;
        }
        return statusChanged;
    }

    @Override
    public boolean implicitStopCondition() {
        // termination explicit

        // if sbl is empty, it means that no more activity can be done,
        // so all stations must be empty. We also don't allow the time
        // to go past 8 hours so the workers can go home.
        if (sbl.isEmpty() || getClock() > 480) {
            if (logFlag) System.out.println("Current time at ending: " + getClock());
            return true;
        }

        return false;
    }

    @Override
    public void eventOccured() {
        if (logFlag)
            printDebug();
    }

    private void printDebug() {
        // Debugging
        System.out.printf("Clock = %10.4f\n", getClock());

        // Casting stations
        System.out.println("   CAST:");
        System.out.println("      RC.CastingStations:");
        String[] planeLabel = new String[] {"F16", "Concorde", "Spitfire"};
        for (int i = 0; i < numStations[Constants.CAST]; i++) {
            CastingStation station = rcCastingStations[i];
            System.out.println("         " + i + ".busy: " + station.busy + ", timeToFailure: " + String.format( "%.2f", station.timeToFailure) +
                    ", castingTimeLeft: " + String.format( "%.2f", station.castingTimeLeft) + ", planeType: " + planeLabel[station.planeType]);
        }

        // casting output areas
        System.out.println("      Q.IOAreas[OUT][CAST]");
        System.out.print("         ");
        for (int stationId = 0; stationId < numStations[Constants.CAST]; stationId++) {
            System.out.print(stationId + ".n: " + qIOAreas[Constants.OUT][Constants.CAST][stationId].getN() + "  ");
        }
        System.out.println();

        // casting loading queue
        System.out.println("      Q.LoadUnload[OUT][CAST]");
        System.out.print("         ");
        for (Integer moverId : qLoadUnload[Constants.OUT][Constants.CAST].moverList) {
            System.out.print(moverId + ".n: " + rgMovers[moverId].getN() + "  ");
        }
        System.out.println();

        String[] stationLabel = new String[] {"CAST", "CUT_GRIND", "COAT", "INSPECT_PACK"};

        for (int stationType = Constants.CUT_GRIND; stationType <= Constants.INSPECT_PACK; stationType++) {
            System.out.println("   " + stationLabel[stationType]+ ":");

            // unloading queue
            System.out.println("      Q.LoadUnload[IN][" + stationLabel[stationType]+ "]:");
            System.out.print("         ");
            for (Integer moverId : qLoadUnload[Constants.IN][stationType].moverList) {
                System.out.print(moverId + ".n: " + rgMovers[moverId].getN() + "  ");
            }
            System.out.println();

            // input areas
            System.out.println("      Q.IOAreas[IN][" + stationLabel[stationType]+ "]:");
            System.out.print("         ");
            for (int stationId = 0; stationId < numStations[stationType]; stationId++) {
                System.out.print(stationId + ".n: " + qIOAreas[Constants.IN][stationType][stationId].getN() + "  ");
            }
            System.out.println();

            // stations
            System.out.println("      RG.Stations[" + stationLabel[stationType]+ "]:");
            System.out.print("         ");
            for (int stationId = 0; stationId < numStations[stationType]; stationId++) {
                Station station = rgStations[stationType][stationId];
                System.out.print(stationId + ".busy: " + station.busy + "  ");
            }
            System.out.println();

            if (stationType != Constants.INSPECT_PACK) {
                // output areas
                System.out.println("      Q.IOAreas[OUT][" + stationLabel[stationType]+ "]");
                System.out.print("         ");
                for (int stationId = 0; stationId < numStations[stationType]; stationId++) {
                    System.out.print(stationId + ".n: " + qIOAreas[Constants.OUT][stationType][stationId].getN() + "  ");
                }
                System.out.println();

                // loading queue
                System.out.println("      Q.LoadUnload[OUT][" + stationLabel[stationType]+ "]");
                System.out.print("         ");
                for (Integer moverId : qLoadUnload[Constants.OUT][stationType].moverList) {
                    System.out.print(moverId + ".n: " + rgMovers[moverId].getN() + "  ");
                }
                System.out.println();
            }
        }
        showSBL();
    }
}
