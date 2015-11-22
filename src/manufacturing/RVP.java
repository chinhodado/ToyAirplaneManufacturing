package manufacturing;

import cern.jet.random.Exponential;
import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister;
import dataModelling.TriangularVariate;

public class RVP {
    ToyAirplaneManufacturing model;

    final double CASTING_TIME = 3;
    final double COATING_TIME = 12;

    // cutting time
    final double CU_MIN_TIME = 0.25;
    final double CU_MOD_TIME = 0.28;
    final double CU_MAX_TIME = 0.35;
    TriangularVariate cutTime;

    // grinding time
    final double G_MEAN_TIME    = 0.241;
    final double G_STV_DEV_TIME = 0.045;
    Normal grindTime;

    // inspection/packaging time
    final double IP_MIN_TIME = 0.27;
    final double IP_MOD_TIME = 0.30;
    final double IP_MAX_TIME = 0.40;
    TriangularVariate inspectPackTime;

    // time to failure
    final double MEAN_TIME_BETWEEN_FAILURE = 30;
    Exponential failureTime;

    // repair time
    final double RP_MEAN_TIME = 8;
    final double RP_STD_DEV_TIME = 2;
    Normal repairTime;

    // Constructor
    RVP(ToyAirplaneManufacturing model, Seeds sd) {
        this.model = model;

        // Initialize internal modules, user modules and input variables
        cutTime = new TriangularVariate(CU_MIN_TIME, CU_MOD_TIME, CU_MAX_TIME, new MersenneTwister(sd.cutTime));
        grindTime = new Normal(G_MEAN_TIME, G_STV_DEV_TIME, new MersenneTwister(sd.grindTime));
        inspectPackTime = new TriangularVariate(IP_MIN_TIME, IP_MOD_TIME, IP_MAX_TIME, new MersenneTwister(sd.inspectPackTIme));
        failureTime = new Exponential(1 / MEAN_TIME_BETWEEN_FAILURE, new MersenneTwister(sd.failureTime));
        repairTime = new Normal(RP_MEAN_TIME, RP_STD_DEV_TIME, new MersenneTwister(sd.repairTime));
    }

    public double uStationWorkTime(int stationType) {
        double workTime = 10;

        switch (stationType) {
        case Constants.CAST:
            workTime = CASTING_TIME;
            break;
        case Constants.CUT_GRIND:
            workTime = cutTime.next() + grindTime.nextDouble();
            break;
        case Constants.COAT:
            workTime = COATING_TIME;
            break;
        case Constants.INSPECT_PACK:
            workTime = inspectPackTime.next();
            break;
        default:
            System.out.printf("udp.uStationWorkTime: Invalid station type: %d\n", stationType);
        }

        return workTime;
    }

    public double uTimeToFailure() {
        return failureTime.nextDouble();
    }

    public double uRepairTime() {
        return repairTime.nextDouble();
    }
}
