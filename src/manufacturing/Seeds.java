package manufacturing;

import cern.jet.random.engine.RandomSeedGenerator;

public class Seeds {
    int cutTime;
    int grindTime;
    int inspectPackTIme;
    int failureTime;
    int repairTime;

    public Seeds(RandomSeedGenerator rsg) {
        cutTime = rsg.nextSeed();
        grindTime = rsg.nextSeed();
        inspectPackTIme = rsg.nextSeed();
        failureTime = rsg.nextSeed();
        repairTime = rsg.nextSeed();
    }
}
