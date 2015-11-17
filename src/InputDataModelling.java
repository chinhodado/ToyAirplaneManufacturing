import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import cern.jet.random.Exponential;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomSeedGenerator;


public class InputDataModelling 
{
	public final static int OBSERVATION_INTERVAL = 10*7*24*60;  // observation interval: weeks x days/week x hours/day x minutes/hour
	// Exponential means for interarrival times for components A and B
	public final static double MEAN_INTER_ARRVL_A = 12.7;  // minutes
	public final static double MEAN_INTER_ARRVL_B = 15.4;  // minutes

	public static void main(String[] args) 
	{
		RandomSeedGenerator rsg = new RandomSeedGenerator();		
		for(int i=0; i<5; i++)
		{
			arrivalRun(rsg);
		}
	}
	
	public static void arrivalRun(RandomSeedGenerator rsg)
	{
		Exponential interArrival_a;
		Exponential interArrival_b;
		// Generate data for part A components over 5 weeks (5*7*26*60) = 50,400 minutes
		interArrival_a = new Exponential(1/MEAN_INTER_ARRVL_A, new MersenneTwister(rsg.nextSeed()));
		interArrival_b = new Exponential(1/MEAN_INTER_ARRVL_B, new MersenneTwister(rsg.nextSeed()));
		// Use ArrayLists to record the data:
		
		ArrayList<Double> arrivals = new ArrayList<Double>();
		int aCount=0, bCount=0; // count number of A and B components
		double aArrival, bArrival;  // For arrivals of components
		// First arrival is from t = 0
		aArrival = interArrival_a.nextDouble();
		bArrival = interArrival_b.nextDouble();
		while(aArrival < OBSERVATION_INTERVAL && bArrival < OBSERVATION_INTERVAL)
		{
			if(aArrival <= bArrival)  // should be rare that arrivals are equal
			{
				if(aArrival == bArrival) System.out.printf(" aArrival = bArrival at %f\n",aArrival);  // Flag equality
				// save aArrival as next arrival and get next value for aArrival
				arrivals.add(aArrival);  // Example of autoboxing - double value in aArrival is automatically converted to a Double object
				aArrival = aArrival + interArrival_a.nextDouble();
				aCount++;
			}
			else  // bArrival < aArrival
			{
				// save bArrival as next arrival and get next value for bArrival
				arrivals.add(bArrival);  // Example of autoboxing - double value in aArrival is automatically converted to a Double object
				bArrival = bArrival + interArrival_b.nextDouble();	
				bCount++;
			}
		}
		System.out.printf("The percentage of A components = %f %% (aCount = %d)\n", (double)aCount/(aCount+bCount), aCount);
		System.out.printf("The percentage of B components = %f %% (bCount = %d)\n", (double)bCount/(aCount+bCount), bCount);
		System.out.printf("Total number of components arrived = %d\n", (aCount+bCount));
		
		// Lets compute the average interarrval rate and print the values for evaluation in Excel
		double arrival = 0.0;  // initialise current arrival to 0
		double nxtArrival;
		double sumInterArrivals = 0.0;  // for computing the average
		// For outputing to a file
		PrintStream outFileStream;
		try 
		{
			outFileStream = new PrintStream("data.csv");
			outFileStream.printf("Arrival Time, Inter Arrival Time\n");
			int cnt;
			for(cnt = 0; cnt < arrivals.size(); cnt++)
			{
				nxtArrival = arrivals.get(cnt);
				outFileStream.printf("%f,%f\n", nxtArrival, nxtArrival - arrival);
				sumInterArrivals += nxtArrival - arrival;
				arrival = nxtArrival;	   	
			}
			System.out.printf("Interarrival mean = %f, number of interarrival times = %d\n\n", sumInterArrivals/cnt, cnt);
			outFileStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
