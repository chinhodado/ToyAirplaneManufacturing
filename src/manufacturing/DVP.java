package manufacturing;

public class DVP {
    public double GetMoveToStationTime(int currentStationType, int nextStationType) {
        double time = 0; // just some random number
        switch (nextStationType) {
        case Constants.CUT_GRIND:
            time = 0.85;
            break;
        case Constants.COAT:
            time = 0.43;
            break;
        case Constants.INSPECT_PACK:
            if (currentStationType == Constants.COAT) time = 0.41;
            else if (currentStationType == Constants.CUT_GRIND) time = 0.84;
            else System.out.printf("DVP.GetMoveToStationTime: Invalid current station type: %d\n", currentStationType);
            break;
        case Constants.CAST:
            time = 1.35;
            break;
        default:
            System.out.printf("DVP.GetMoveToStationTime: Invalid next station type: %d\n", nextStationType);
            break;
        }
        return time;
    }
}
