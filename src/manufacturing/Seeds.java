package manufacturing;

import cern.jet.random.engine.RandomSeedGenerator;

public class Seeds
{
	int cArr;   // component arrivals
	int type;   // for types
	int ptM1A;   // process times M1, A
	int ptM1B;   // process times M1, B
	int ptM2;   // process times M2
	int ptM3;   // process times M3

	public Seeds(RandomSeedGenerator rsg)
	{
		cArr=rsg.nextSeed();
		type=rsg.nextSeed();
		ptM1A=rsg.nextSeed();
		ptM1B=rsg.nextSeed();
		ptM2=rsg.nextSeed();
		ptM3=rsg.nextSeed();
	}
}
