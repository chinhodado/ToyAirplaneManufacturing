package manufacturing;

import simulationModelling.AOSimulationModel;
import simulationModelling.Behaviour;

public class Manufacturing extends AOSimulationModel
{
	// Entities
	Machines [] rMachines = new Machines[3];
	Conveyors [] qConveyors = new Conveyors[3];
	
	// Parameters
	// Implemented as attributes of qConveyors[M2].length and qConveyors[M3].length
	
	// Random variate procedures
	RVPs rvp;
	
	// User Defined Procedures
	UDPs udp = new UDPs(this);
	
	// Outputs
    Output output = new Output(this);
    
    // DSOVs
    public double getPercentTimeDown() { return output.percentTimeDown(); }
    public double getTimeC2Full() { return output.timeC2Full(); }
    public double getTimeC3Full() { return output.timeC3Full(); }
   
    // Constructor
	public Manufacturing(double tftime, int lc2, int lc3, Seeds sd, boolean log)
	{
		// For turning on logging
		logFlag = log;
		
		// Set up RVPs
		rvp = new RVPs(this,sd);
		
		// Initialise parameters
		// Need to create the entities/objects here instead of the intialise action
		for(int id = Constants.M1; id <= Constants.M3; id++) qConveyors[id] = new Conveyors();
		qConveyors[Constants.M2].length = lc2;
		qConveyors[Constants.M3].length = lc3;
		
		this.initAOSimulModel(0, tftime);
		
		// Schedule Initialise action
		Initialise init = new Initialise(this);
		scheduleAction(init);  // Should always be first one scheduled.
		// Start arrivals
		CompArrivals aArr = new CompArrivals(this);
		scheduleAction(aArr);
		
		//printDebug("At Start");
	}
	
	@Override
	public void testPreconditions(Behaviour behObj)
	{
		reschedule(behObj);
		while (scanPreconditions() == true) /* repeat */;
	}

	// Single scan of all preconditions
	// Returns true if at least one precondition was true.
	private boolean scanPreconditions()
	{
		boolean statusChanged = false;
		// Conditional Actions
		if (MoveCOutOfM1.precondition(this) == true)
		{
			MoveCOutOfM1 act = new MoveCOutOfM1(this); // Generate instance																// instance
			act.actionEvent();
			statusChanged = true;
		}
		// Conditional Activities
		if (CompProcessing.precondition(this) == true)
		{
			CompProcessing act = new CompProcessing(this); // Generate instance
			act.startingEvent();
			scheduleActivity(act);
			statusChanged = true;
		}
		return (statusChanged);
	}
		
	public void eventOccured()
	{		
		output.updateSequences(); // for updating trajectory sets	
		if(logFlag) printDebug();
	}
	
	// for Debugging
	boolean logFlag = false;
	protected void printDebug()
	{
		// Debugging
		System.out.printf("Clock = %10.4f\n", getClock());
		// Machine M1
		System.out.print("   M1: qConveyor[].n= "+qConveyors[Constants.M1].getN()+
			             ", R.Machines[].busy="+rMachines[Constants.M1].busy +
			             ", R.Machines[].component=");
		if(rMachines[Constants.M1].component == Machines.NO_COMP) System.out.println("NO_COMP");
		else System.out.println(rMachines[Constants.M1].component.uType);
		// Machine M2
		System.out.println("   M2: qConveyor[].n= "+qConveyors[Constants.M2].getN()+
	             ", R.Machines[].busy="+rMachines[Constants.M2].busy);
		//Machine M3
		System.out.println("   M3: qConveyor[].n= "+qConveyors[Constants.M3].getN()+
	             ", R.Machines[].busy="+rMachines[Constants.M3].busy);		
		showSBL();
		System.out.println(">-----------------------------------------------<");
	}

}
