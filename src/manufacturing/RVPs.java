package manufacturing;

import cern.jet.random.Exponential;
import cern.jet.random.engine.MersenneTwister;

class RVPs 
{
	   Manufacturing model;  // reference to the complete model
	   	   
		// Constructor
		RVPs(Manufacturing model, Seeds sd) 
		{ 
			this.model = model; 
			// Initialise Internal modules, user modules and input variables
			interArr = new Exponential(1/MEAN_INTER_ARR, new MersenneTwister(sd.cArr));
            typeDM = new MersenneTwister(sd.type);
			procM1A = new Exponential(1/MEAN_PROC_TIME_M1_A, new MersenneTwister(sd.ptM1A));
			procM1B = new Exponential(1/MEAN_PROC_TIME_M1_B, new MersenneTwister(sd.ptM1B));
			procM2 = new Exponential(1/MEAN_PROC_TIME_M2, new MersenneTwister(sd.ptM2));
			procM3 = new Exponential(1/MEAN_PROC_TIME_M3, new MersenneTwister(sd.ptM3));			
		}

		// RVP for interarrival times.
		private final double MEAN_INTER_ARR= 7.0;
		private Exponential interArr;
		protected double duCArr( )
		{
		   double nxtTime=0.0;	   
		   nxtTime = model.getClock()+interArr.nextDouble();
		   return(nxtTime);
		}
		
		// For determining type of arriving component.
		private final double PERCENT_A = 0.55;  // PERCENT_B not required
		private MersenneTwister typeDM;
		protected Component.CompType uCompType()
		{
			Component.CompType type = Component.CompType.B;
			if(typeDM.nextDouble() < PERCENT_A) type = Component.CompType.A;
			return(type);			
		}

		// Processing Times procedure for M1, different for different component types
		final double MEAN_PROC_TIME_M1_A=2.1;
		final double MEAN_PROC_TIME_M1_B=4.2;
		Exponential procM1A;
		Exponential procM1B;
		// Processing Time procedure for M2 and M3
		final double MEAN_PROC_TIME_M2=9.4;
		final double MEAN_PROC_TIME_M3=10.5;
		Exponential procM2;
		Exponential procM3;

		public double uProcTime(int machineId, Component.CompType type)
		{
			double processingTime = 1.0;  // an arbitrary default value.
			switch(machineId)
			{
			   case Constants.M1:
				   if(type == Component.CompType.A) processingTime = procM1A.nextDouble();
				   else processingTime = procM1B.nextDouble();
				   break;
			   case Constants.M2:
				   processingTime = procM2.nextDouble();
				   break;
			   case Constants.M3:
				   processingTime = procM3.nextDouble();
				   break;
			   default:
				   System.out.printf("udp.uProcTime: Invalid machine id: %d\n", machineId);					   
			}
			return(processingTime);		
		}
}
