
import manufacturing.Manufacturing;
import manufacturing.Seeds;
import cern.jet.random.engine.RandomSeedGenerator;

public class Experiment
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		int i, NUMRUNS = 40;
		double endTime = 30 * 24 * 60; // run for 30 days
		Seeds[] sds = new Seeds[NUMRUNS];
		Manufacturing mnf; // Simulation object

		// Lets get a set of uncorrelated seeds, different seeds for each run
		RandomSeedGenerator rsg = new RandomSeedGenerator();
		for (i = 0; i < NUMRUNS; i++)
			sds[i] = new Seeds(rsg);

		// Loop for NUMRUN simulation runs for each case
		int lc2 = 3;
		int lc3 = 3;
		double meanDnTime=0, meanC2Full=0, meanC3Full=0;
		while(lc2 <= 10 && lc3 <= 10)
		{
			//System.out.println("Conveyor Limits: "+lc2+", "+lc3);
			for (i = 0; i < NUMRUNS; i++)
			{
				mnf = new Manufacturing(endTime, lc2, lc3, sds[i], false);
				mnf.runSimulation();
				meanDnTime += mnf.getPercentTimeDown() ;
				meanC2Full += mnf.getTimeC2Full();
				meanC3Full += mnf.getTimeC3Full();
			}
			meanDnTime /= NUMRUNS;
			meanC2Full /= NUMRUNS;
			meanC3Full /= NUMRUNS;
			System.out.println("Q.Conveyors[M2].length = "+lc2+", Q.Conveyors[M2].length = "+lc3+": DnTimePE= "+meanDnTime+
				               ", C2FullTimePE= "+meanC2Full+", C3FullTimePE= "+meanC3Full);
			if(meanDnTime < 0.10) break;
			if(meanC2Full > meanC3Full) lc2++;
			else lc3++;
		}
	}
}
