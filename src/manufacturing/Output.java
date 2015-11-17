package manufacturing;

import simulationModelling.OutputSequence;

class Output 
{
	Manufacturing model;

	// DSOVs
	protected double percentTimeDown() 
	{
		trjM1Down.computeTrjDSOVs(model.getTime0(), model.getTimef());
		return (trjM1Down.getMean());
	}

	protected double timeC2Full() 
	{
		trjTimeConv2Full.computeTrjDSOVs(model.getTime0(), model.getTimef());
		return (trjTimeConv2Full.getMean());
	}

	protected double timeC3Full() 
	{
		trjTimeConv3Full.computeTrjDSOVs(model.getTime0(), model.getTimef());
		return (trjTimeConv3Full.getMean());
	}
	// Trajectory Sequences
	OutputSequence trjM1Down = new OutputSequence("M1Down");
	OutputSequence trjTimeConv2Full = new OutputSequence("trjTimeConv2Full");
	OutputSequence trjTimeConv3Full = new OutputSequence("trjTimeConv3Full");
	
	// Last value saved in the trajectory set
	double lastRM1down;
	double lastQConveyorsM2N;
	double lastQConveyorsM3N;
	// Constructor
	protected Output(Manufacturing model)
	{
		this.model = model;
		// First points in trajectory sequences - range = 0.0
		double lastRM1down = 0.0;
		double lastQConveyorsM2N = 0.0;
		double lastQConveyorsM3N = 0.0;
		trjM1Down.put(0.0,lastRM1down);
		trjTimeConv2Full.put(0.0,lastQConveyorsM2N);
		trjTimeConv3Full.put(0.0,lastQConveyorsM3N);
	}
	
	// Update the trajectory sequences
	// curTime - the current time
	protected void updateSequences()
	{
		// update TRJ[M1Down]
		double currentM1Down;
		double currentTimeConv2Full;
		double currentTimeConv3Full;
		if(model.rMachines[Constants.M1].busy == false && model.rMachines[Constants.M1].component != null)
			currentM1Down = 1;
		else currentM1Down = 0;
		if(currentM1Down != lastRM1down)
		{
			trjM1Down.put(model.getClock(), currentM1Down);
			lastRM1down = currentM1Down;
		}
		// Update TRJ[Q.Conveyors[M2].N
		if(model.qConveyors[Constants.M2].getN() == model.qConveyors[Constants.M2].length) currentTimeConv2Full = 1;
		else currentTimeConv2Full = 0;
		if(currentTimeConv2Full != lastQConveyorsM2N)
		{
			lastQConveyorsM2N = currentTimeConv2Full;
			trjTimeConv2Full.put(model.getClock(), lastQConveyorsM2N);			
		}

		// Update TRJ[Q.Conveyors[M3].N
		if(model.qConveyors[Constants.M3].getN() == model.qConveyors[Constants.M3].length) currentTimeConv3Full = 1;
		else currentTimeConv3Full = 0;
		if(currentTimeConv3Full != lastQConveyorsM3N)
		{
			lastQConveyorsM3N = currentTimeConv3Full;
			trjTimeConv3Full.put(model.getClock(), lastQConveyorsM3N);			
		}
	}   
}
