package manufacturing;

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

    public LoadUnload[][] qLoadUnload;
    public Mover[] rgMovers;
    public MaintenancePerson rMaintenancePerson;

    // Parameters
    int numMover, numF16CastingStation, numConcordeCastingStation, numSpitfireCastingStation,
        numCuttingGrindingStation, numCoatingStation, numInspectionPackagingStation;
    int numCastingStation; // total number of casting stations
    int[] numStations;

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

    // DSOVs
    public double getPercentTimeCastingStationBlocked() {
        return output.percentTimeCastingStationBlocked();
    }

    public double getPercentTimeStationBlocked(int stationType) {
        return output.percentTimeStationBlocked(stationType);
    }

    /**
     * Constructor
     * @param tftime The end time (???)
     * @param params An array of parameters containing (0)numMover, (1)numF16CastingStation,
     *               (2)numConcordeCastingStation, (3)numSpitfireCastingStation,
     *               (4)numCuttingGrindingStation, (5)numCoatingStation and (6)numInspectionPackagingStation
     *               in that order
     * @param sd     Seeds used
     * @param log    log
     */
    public ToyAirplaneManufacturing(double tftime, int[] params, Seeds sd, boolean log) {
        // For turning on logging
        logFlag = log;

        // Set up RVPs
        rvp = new RVP(this, sd);

        // Initialize parameters
        numMover = params[0];

        numF16CastingStation = params[1];
        numConcordeCastingStation = params[2];
        numSpitfireCastingStation = params[3];

        numCuttingGrindingStation = params[4];
        numCoatingStation = params[5];
        numInspectionPackagingStation = params[6];

        numCastingStation = numF16CastingStation + numConcordeCastingStation + numSpitfireCastingStation;
        numStations = new int[] { numCastingStation, numCuttingGrindingStation,
                numCoatingStation, numInspectionPackagingStation };

        rgStations = new Station[4][]; // the first array is not used
        rgStations[Constants.CUT_GRIND] = new Station[numCuttingGrindingStation];
        rgStations[Constants.COAT] = new Station[numCoatingStation];
        rgStations[Constants.INSPECT_PACK] = new Station[numInspectionPackagingStation];

        rcCastingStations = new CastingStation[numCastingStation];

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

        this.initAOSimulModel(0, tftime);

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
    public void eventOccured() {
        output.updateSequences(); // for updating trajectory sets
        if (logFlag)
            printDebug();
    }

    protected void printDebug() {
        // Debugging
        System.out.printf("Clock = %10.4f\n", getClock());

        // Casting stations
        System.out.println("   CAST:");
        System.out.println("      RC.CastingStations:");
        String[] planeLabel = new String[] {"F16", "Concorde", "Spitfire"};
        for (int i = 0; i < numCastingStation; i++) {
            CastingStation station = rcCastingStations[i];
            System.out.println("         " + i + ".busy: " + station.busy + ", timeToFailure: " + station.timeToFailure +
                    ", castingTimeLeft: " + station.castingTimeLeft + ", planeType: " + planeLabel[station.planeType]);
        }

        // casting output areas
        System.out.println("      Q.IOAreas[OUT][CAST]");
        System.out.print("         ");
        for (int stationId = 0; stationId < numCastingStation; stationId++) {
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
    }
}
